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

/** Check if an expression represents zero (including simplified expressions). */
fun KotlinExpr.isZeroLiteral(): Boolean = when (this) {
    is KLiteral -> value == "0" || value == "0x00" || value == "0x0" || value.lowercase() == "0x00"
    is KIfExpr -> {
        // If condition is a literal boolean, check the corresponding branch
        if (condition is KLiteral && condition.value == "true") thenExpr.isZeroLiteral()
        else if (condition is KLiteral && condition.value == "false") elseExpr.isZeroLiteral()
        else false
    }
    else -> false
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
    override fun toKotlin(): String {
        // Simplify identity operations
        if (op == "+" && right.isZeroLiteral()) return left.toKotlin()
        if (op == "+" && left.isZeroLiteral()) return right.toKotlin()
        if (op == "-" && right.isZeroLiteral()) return left.toKotlin()
        if (op == "or" && right.isZeroLiteral()) return left.toKotlin()
        if (op == "or" && left.isZeroLiteral()) return right.toKotlin()

        // Handle operator precedence - wrap lower precedence operators in parens
        val leftStr = if (left.needsParensIn(op)) "(${left.toKotlin()})" else left.toKotlin()
        val rightStr = if (right.needsParensIn(op)) "(${right.toKotlin()})" else right.toKotlin()
        return "$leftStr $op $rightStr"
    }
}

/** Check if this expression needs parentheses when used as operand of given operator */
private fun KotlinExpr.needsParensIn(parentOp: String): Boolean {
    if (this !is KBinaryOp) return false
    // Operator precedence (higher number = higher precedence)
    val precedence = mapOf(
        "||" to 1, "or" to 1,
        "&&" to 2, "and" to 2, "xor" to 2,
        "==" to 3, "!=" to 3, "<" to 3, ">" to 3, "<=" to 3, ">=" to 3,
        "+" to 4, "-" to 4,
        "*" to 5, "/" to 5, "%" to 5,
        "shl" to 6, "shr" to 6, "ushr" to 6
    )
    val myPrecedence = precedence[this.op] ?: 10
    val parentPrecedence = precedence[parentOp] ?: 10
    return myPrecedence < parentPrecedence
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

/** If expression: if (cond) thenExpr else elseExpr */
data class KIfExpr(val condition: KotlinExpr, val thenExpr: KotlinExpr, val elseExpr: KotlinExpr) : KotlinExpr {
    override fun toKotlin(): String {
        // Simplify if condition is a literal boolean
        if (condition is KLiteral) {
            if (condition.value == "true") return thenExpr.toKotlin()
            if (condition.value == "false") return elseExpr.toKotlin()
        }
        return "if (${condition.toKotlin()}) ${thenExpr.toKotlin()} else ${elseExpr.toKotlin()}"
    }
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
data class KVarDecl(
    val name: String,
    val type: String? = null,
    val value: KotlinExpr? = null,
    val mutable: Boolean = false,
    val delegated: Boolean = false
) : KotlinStmt {
    override fun toKotlin(indent: String): String {
        val keyword = if (mutable) "var" else "val"
        val typeAnnotation = type?.let { ": $it" } ?: ""
        val operator = if (delegated) " by " else " = "
        val initializer = value?.let { "$operator${it.toKotlin()}" } ?: ""
        return "$indent$keyword $name$typeAnnotation$initializer"
    }
}

/** Destructuring declaration: val (a, b) = pair() - by Claude */
data class KDestructuringDecl(
    val names: List<String>,
    val value: KotlinExpr
) : KotlinStmt {
    override fun toKotlin(indent: String): String {
        val namesStr = names.joinToString(", ")
        return "${indent}val ($namesStr) = ${value.toKotlin()}"
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

/** When expression/statement: when (x) { 0 -> ..., 1, 2 -> ..., else -> ... } */
data class KWhen(
    val subject: KotlinExpr,
    val branches: List<KWhenBranch>,
    val elseBranch: List<KotlinStmt> = emptyList()
) : KotlinStmt {
    override fun toKotlin(indent: String): String {
        val sb = StringBuilder()
        sb.append("${indent}when (${subject.toKotlin()}) {\n")

        for (branch in branches) {
            // Format: value1, value2 -> { statements }
            val values = branch.values.joinToString(", ") { it.toKotlin() }
            sb.append("$indent    $values -> {\n")
            branch.body.forEach { sb.append(it.toKotlin("$indent        ")).append("\n") }
            sb.append("$indent    }\n")
        }

        if (elseBranch.isNotEmpty()) {
            sb.append("$indent    else -> {\n")
            elseBranch.forEach { sb.append(it.toKotlin("$indent        ")).append("\n") }
            sb.append("$indent    }\n")
        }

        sb.append("$indent}")
        return sb.toString()
    }
}

data class KWhenBranch(val values: List<KotlinExpr>, val body: List<KotlinStmt>)

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
