package com.ivieleague.decompiler6502tokotlin.hand

/**
 * Kotlin AST (Abstract Syntax Tree) for code generation.
 *
 * This represents the final Kotlin code structure that will be emitted.
 * It's a simplified AST focused on the subset of Kotlin needed for 6502 decompilation.
 */

// ===========================
// Expressions
// ===========================

sealed interface KotlinExpr {
    fun toKotlin(): String
}

/** Literal values: 0x42, true, "hello" */
data class KLiteral(val value: String) : KotlinExpr {
    override fun toKotlin() = value
}

/** Variable reference: myVar, memory, A */
data class KVar(val name: String) : KotlinExpr {
    override fun toKotlin() = name
}

/** Binary operation: a + b, x && y, value shl 2 */
data class KBinaryOp(val left: KotlinExpr, val op: String, val right: KotlinExpr) : KotlinExpr {
    override fun toKotlin() = "${left.toKotlin()} $op ${right.toKotlin()}"
}

/** Unary operation: !flag, -value, value.inv() */
data class KUnaryOp(val op: String, val expr: KotlinExpr, val postfix: Boolean = false) : KotlinExpr {
    override fun toKotlin(): String {
        // For prefix unary operations on complex expressions (like binary ops), add parentheses
        val needsParens = !postfix && op == "!" && expr is KBinaryOp
        val exprStr = if (needsParens) "(${expr.toKotlin()})" else expr.toKotlin()
        return if (postfix) "$exprStr$op" else "$op$exprStr"
    }
}

/** Function call: doSomething(), process(x, y) */
data class KCall(val name: String, val args: List<KotlinExpr> = emptyList()) : KotlinExpr {
    override fun toKotlin() = "$name(${args.joinToString(", ") { it.toKotlin() }})"
}

/** Member access: obj.field, memory[0x42] */
data class KMemberAccess(val receiver: KotlinExpr, val member: KotlinExpr, val isIndexed: Boolean = false) : KotlinExpr {
    override fun toKotlin() = if (isIndexed) {
        "${receiver.toKotlin()}[${member.toKotlin()}]"
    } else {
        "${receiver.toKotlin()}.${member.toKotlin()}"
    }
}

/** Parenthesized expression: (a + b) */
data class KParen(val expr: KotlinExpr) : KotlinExpr {
    override fun toKotlin() = "(${expr.toKotlin()})"
}

/** Cast: value as UByte */
data class KCast(val expr: KotlinExpr, val type: String) : KotlinExpr {
    override fun toKotlin() = "${expr.toKotlin()} as $type"
}

// ===========================
// Statements
// ===========================

sealed interface KotlinStmt {
    fun toKotlin(indent: String = ""): String
}

/** Expression statement: doSomething() */
data class KExprStmt(val expr: KotlinExpr) : KotlinStmt {
    override fun toKotlin(indent: String) = indent + expr.toKotlin()
}

/** Variable declaration: val x = 5, var flag = true */
data class KVarDecl(val name: String, val type: String? = null, val value: KotlinExpr? = null, val mutable: Boolean = false) : KotlinStmt {
    override fun toKotlin(indent: String): String {
        val keyword = if (mutable) "var" else "val"
        val typeAnnotation = type?.let { ": $it" } ?: ""
        val initializer = value?.let { " = ${it.toKotlin()}" } ?: ""
        return "$indent$keyword $name$typeAnnotation$initializer"
    }
}

/** Assignment: x = 5, memory[addr] = value */
data class KAssignment(val target: KotlinExpr, val value: KotlinExpr) : KotlinStmt {
    override fun toKotlin(indent: String) = "$indent${target.toKotlin()} = ${value.toKotlin()}"
}

/** If statement: if (cond) { ... } else { ... } */
data class KIf(
    val condition: KotlinExpr,
    val thenBranch: List<KotlinStmt>,
    val elseBranch: List<KotlinStmt> = emptyList()
) : KotlinStmt {
    override fun toKotlin(indent: String): String {
        val sb = StringBuilder()
        sb.append("${indent}if (${condition.toKotlin()}) {\n")
        thenBranch.forEach { sb.append(it.toKotlin("$indent    ")).append("\n") }

        if (elseBranch.isNotEmpty()) {
            sb.append("$indent} else {\n")
            elseBranch.forEach { sb.append(it.toKotlin("$indent    ")).append("\n") }
        }

        sb.append("$indent}")
        return sb.toString()
    }
}

/** While loop: while (cond) { ... } */
data class KWhile(val condition: KotlinExpr, val body: List<KotlinStmt>) : KotlinStmt {
    override fun toKotlin(indent: String): String {
        val sb = StringBuilder()
        sb.append("${indent}while (${condition.toKotlin()}) {\n")
        body.forEach { sb.append(it.toKotlin("$indent    ")).append("\n") }
        sb.append("$indent}")
        return sb.toString()
    }
}

/** Do-while loop: do { ... } while (cond) */
data class KDoWhile(val body: List<KotlinStmt>, val condition: KotlinExpr) : KotlinStmt {
    override fun toKotlin(indent: String): String {
        val sb = StringBuilder()
        sb.append("${indent}do {\n")
        body.forEach { sb.append(it.toKotlin("$indent    ")).append("\n") }
        sb.append("$indent} while (${condition.toKotlin()})")
        return sb.toString()
    }
}

/** Infinite loop: while (true) { ... } */
data class KLoop(val body: List<KotlinStmt>) : KotlinStmt {
    override fun toKotlin(indent: String): String {
        val sb = StringBuilder()
        sb.append("${indent}while (true) {\n")
        body.forEach { sb.append(it.toKotlin("$indent    ")).append("\n") }
        sb.append("$indent}")
        return sb.toString()
    }
}

/** Return statement: return, return value */
data class KReturn(val value: KotlinExpr? = null) : KotlinStmt {
    override fun toKotlin(indent: String) = if (value != null) {
        "${indent}return ${value.toKotlin()}"
    } else {
        "${indent}return"
    }
}

/** Break statement: break */
object KBreak : KotlinStmt {
    override fun toKotlin(indent: String) = "${indent}break"
}

/** Continue statement: continue */
object KContinue : KotlinStmt {
    override fun toKotlin(indent: String) = "${indent}continue"
}

/** Comment: // This is a comment */
data class KComment(val text: String, val commentTypeIndicator: String = "") : KotlinStmt {
    override fun toKotlin(indent: String) = "$indent//$commentTypeIndicator $text"
}

/** Block comment: /* ... */ */
data class KBlockComment(val lines: List<String>) : KotlinStmt {
    override fun toKotlin(indent: String): String {
        if (lines.size == 1) return "$indent// ${lines.first()}"
        val sb = StringBuilder()
        sb.append("$indent/*\n")
        lines.forEach { sb.append("$indent * $it\n") }
        sb.append("$indent */")
        return sb.toString()
    }
}

// ===========================
// Top-level declarations
// ===========================

/** Function declaration */
data class KFunction(
    val name: String,
    val params: List<KParam> = emptyList(),
    val returnType: String? = null,
    val body: List<KotlinStmt>,
    val comment: String? = null
) {
    fun toKotlin(indent: String = ""): String {
        val sb = StringBuilder()

        // Add comment if present
        if (comment != null) {
            sb.append("$indent// $comment\n")
        }

        // Function signature
        sb.append("${indent}fun $name(")
        sb.append(params.joinToString(", ") { "${it.name}: ${it.type}" })
        sb.append(")")

        if (returnType != null) {
            sb.append(": $returnType")
        }

        sb.append(" {\n")

        // Body
        body.forEach { sb.append(it.toKotlin("$indent    ")).append("\n") }

        sb.append("$indent}")
        return sb.toString()
    }
}

data class KParam(val name: String, val type: String, val default: String? = null)

/** Class declaration (for memory/state) */
data class KClass(
    val name: String,
    val properties: List<KProperty>,
    val functions: List<KFunction> = emptyList(),
    val comment: String? = null
) {
    fun toKotlin(indent: String = ""): String {
        val sb = StringBuilder()

        if (comment != null) {
            sb.append("$indent// $comment\n")
        }

        sb.append("${indent}class $name {\n")

        // Properties
        properties.forEach { prop ->
            val keyword = if (prop.mutable) "var" else "val"
            val initializer = prop.initialValue?.let { " = ${it.toKotlin()}" } ?: ""
            sb.append("$indent    $keyword ${prop.name}: ${prop.type}$initializer\n")
        }

        if (properties.isNotEmpty() && functions.isNotEmpty()) {
            sb.append("\n")
        }

        // Functions
        functions.forEach { func ->
            sb.append(func.toKotlin("$indent    ")).append("\n\n")
        }

        sb.append("$indent}")
        return sb.toString()
    }
}

data class KProperty(
    val name: String,
    val type: String,
    val mutable: Boolean = false,
    val initialValue: KotlinExpr? = null
)

/** Top-level file */
data class KFile(
    val packageName: String,
    val imports: List<String> = emptyList(),
    val declarations: List<Any>, // KFunction, KClass, etc.
    val comment: String? = null
) {
    fun toKotlin(): String {
        val sb = StringBuilder()

        // Package
        sb.append("package $packageName\n\n")

        // Imports
        if (imports.isNotEmpty()) {
            imports.forEach { sb.append("import $it\n") }
            sb.append("\n")
        }

        // File comment
        if (comment != null) {
            sb.append("/*\n")
            sb.append(" * $comment\n")
            sb.append(" */\n\n")
        }

        // Declarations
        declarations.forEachIndexed { index, decl ->
            when (decl) {
                is KFunction -> sb.append(decl.toKotlin())
                is KClass -> sb.append(decl.toKotlin())
            }

            if (index < declarations.lastIndex) {
                sb.append("\n\n")
            }
        }

        return sb.toString()
    }
}
