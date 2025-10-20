package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 38: Variable & Function Emission
 * - Combine all previous passes to emit complete Kotlin functions
 * - Integrate AST construction (Pass 35), expression emission (Pass 36),
 *   and control flow emission (Pass 37)
 * - Add function bodies from control flow emission to AST functions
 * - Generate complete, compilable Kotlin code
 * - Handle variable declarations and initializations
 *
 * This is the final assembly pass that produces actual Kotlin code.
 */

/**
 * Result of complete code emission
 */
data class CompleteEmissionResult(
    val kotlinFile: KotlinAst.File,
    val statistics: CompleteEmissionStats
)

/**
 * Statistics about complete emission
 */
data class CompleteEmissionStats(
    val totalFunctions: Int,
    val totalStatements: Int,
    val totalExpressions: Int,
    val linesOfCode: Int
)

/**
 * Emit complete Kotlin code from all analysis passes
 */
fun CfgConstruction.emitCompleteKotlin(
    codeFile: AssemblyCodeFile,
    variableAnalysis: VariableIdentification? = null,
    parameterRecovery: ParameterRecovery? = null,
    addressResolution: AddressResolution? = null,
    expressionEmission: ExpressionEmissionResult? = null,
    controlFlowEmission: ControlFlowEmissionResult? = null
): CompleteEmissionResult {

    // Step 1: Build base AST with CPU state
    val astFile = this.constructKotlinAst(codeFile, variableAnalysis, null, null, parameterRecovery)

    // Step 2: Get control flow emission (emit if not provided)
    val cfEmission = controlFlowEmission ?: this.emitControlFlow(codeFile, addressResolution, null, expressionEmission)

    // Step 3: Merge function bodies from Pass 35 (local vars) and Pass 37 (control flow)
    val updatedDeclarations = astFile.declarations.map { decl ->
        when (decl) {
            is KotlinAst.Declaration.Function -> {
                // Find matching function in control flow emission
                val matchingCf = cfEmission.functions.find { cf ->
                    cf.function.entryLabel == decl.name ||
                    "function_${cf.function.entryAddress.toString(16).uppercase()}" == decl.name
                }

                if (matchingCf != null) {
                    // Extract local variable declarations from Pass 35
                    val localVarDecls = decl.body.statements.filterIsInstance<KotlinAst.Statement.LocalVariable>()

                    // Merge: local vars from Pass 35 + control flow from Pass 37
                    val mergedStatements = localVarDecls + matchingCf.body.statements
                    decl.copy(body = KotlinAst.Block(mergedStatements))
                } else {
                    decl
                }
            }
            else -> decl
        }
    }

    // Step 4: Create final file with updated declarations
    val finalFile = astFile.copy(declarations = updatedDeclarations)

    // Step 5: Collect statistics
    val totalFunctions = updatedDeclarations.count { it is KotlinAst.Declaration.Function }
    val totalStatements = updatedDeclarations
        .filterIsInstance<KotlinAst.Declaration.Function>()
        .sumOf { it.body.statements.size }

    val stats = CompleteEmissionStats(
        totalFunctions = totalFunctions,
        totalStatements = totalStatements,
        totalExpressions = expressionEmission?.statistics?.totalExpressions ?: 0,
        linesOfCode = estimateLinesOfCode(finalFile)
    )

    return CompleteEmissionResult(finalFile, stats)
}

/**
 * Estimate lines of code in the generated file
 */
private fun estimateLinesOfCode(file: KotlinAst.File): Int {
    var lines = 0

    // Header
    lines += 5  // package, file annotation, etc.

    // Imports
    lines += file.imports.size

    // Declarations
    for (decl in file.declarations) {
        when (decl) {
            is KotlinAst.Declaration.Function -> {
                lines += 1  // function signature
                lines += decl.body.statements.size
                lines += 1  // closing brace
            }
            is KotlinAst.Declaration.Property -> {
                lines += 1
            }
            else -> {
                lines += 1
            }
        }
    }

    return lines
}

/**
 * Emit the Kotlin file as a string
 */
fun KotlinAst.File.emitToString(): String {
    val builder = StringBuilder()

    // File annotations (must come before package declaration)
    if (header.suppressions.isNotEmpty()) {
        builder.append("@file:Suppress(")
        builder.append(header.suppressions.joinToString(", ") { "\"$it\"" })
        builder.appendLine(")")
        builder.appendLine()
    }

    // Package declaration
    builder.appendLine("package $packageName")
    builder.appendLine()

    // Imports
    for (import in imports) {
        if (import.name != null) {
            builder.appendLine("import ${import.packagePath}.${import.name}")
        } else {
            builder.appendLine("import ${import.packagePath}.*")
        }
    }
    if (imports.isNotEmpty()) {
        builder.appendLine()
    }

    // Header comment
    builder.appendLine("// ${header.comment}")
    builder.appendLine("// Original source: ${header.originalSource}")
    builder.appendLine()

    // Declarations
    for (decl in declarations) {
        builder.append(decl.emitToString())
        builder.appendLine()
    }

    return builder.toString()
}

/**
 * Emit a declaration to string
 */
private fun KotlinAst.Declaration.emitToString(): String {
    return when (this) {
        is KotlinAst.Declaration.Function -> this.emitFunctionToString()
        is KotlinAst.Declaration.Property -> this.emitPropertyToString()
        is KotlinAst.Declaration.DataClass -> this.emitDataClassToString()
        is KotlinAst.Declaration.EnumClass -> this.emitEnumClassToString()
    }
}

/**
 * Emit a function to string
 */
private fun KotlinAst.Declaration.Function.emitFunctionToString(): String {
    val builder = StringBuilder()

    // KDoc if present
    if (kdoc != null) {
        builder.appendLine("/**")
        builder.appendLine(" * $kdoc")
        builder.appendLine(" */")
    }

    // Function signature
    builder.append("fun $name(")
    builder.append(parameters.joinToString(", ") { param ->
        "${param.name}: ${param.type}"
    })
    builder.append(")")

    if (returnType != null) {
        builder.append(": $returnType")
    }

    builder.appendLine(" {")

    // Body
    for (statement in body.statements) {
        builder.append("    ")
        builder.appendLine(statement.emitToString())
    }

    builder.appendLine("}")

    return builder.toString()
}

/**
 * Emit a property to string
 */
internal fun KotlinAst.Declaration.Property.emitPropertyToString(): String {
    val builder = StringBuilder()

    // KDoc if present
    if (kdoc != null) {
        builder.append("/** $kdoc */")
        builder.append(" ")
    }

    // Property declaration
    val varKeyword = if (isVar) "var" else "val"
    builder.append("$varKeyword $name: $type")

    if (initializer != null) {
        builder.append(" = ")
        builder.append(initializer.formatAsKotlin())
    }

    return builder.toString()
}

/**
 * Emit a data class to string
 */
private fun KotlinAst.Declaration.DataClass.emitDataClassToString(): String {
    val builder = StringBuilder()

    if (kdoc != null) {
        builder.appendLine("/** $kdoc */")
    }

    builder.append("data class $name(")
    builder.append(properties.joinToString(", ") { prop ->
        val varKeyword = if (prop.isVar) "var" else "val"
        "$varKeyword ${prop.name}: ${prop.type}"
    })
    builder.append(")")

    return builder.toString()
}

/**
 * Emit an enum class to string
 */
private fun KotlinAst.Declaration.EnumClass.emitEnumClassToString(): String {
    val builder = StringBuilder()

    if (kdoc != null) {
        builder.appendLine("/** $kdoc */")
    }

    builder.appendLine("enum class $name {")
    for (entry in entries) {
        builder.append("    ${entry.name}")
        if (entry != entries.last()) {
            builder.append(",")
        }
        builder.appendLine()
    }
    builder.append("}")

    return builder.toString()
}

/**
 * Check if an expression is a memory access (memory[...])
 */
private fun isMemoryAccess(expr: KotlinAst.Expression): Boolean {
    return when (expr) {
        is KotlinAst.Expression.ArrayAccess -> {
            expr.array is KotlinAst.Expression.Variable &&
            (expr.array as KotlinAst.Expression.Variable).name == "memory"
        }
        else -> false
    }
}

/**
 * Emit a statement to string
 */
private fun KotlinAst.Statement.emitToString(): String {
    return when (this) {
        is KotlinAst.Statement.Assignment -> {
            // Check if target is memory access - need to convert Int to Byte
            // Check if value is memory access - need to convert Byte to Int
            val valueStr = when {
                isMemoryAccess(target) -> {
                    // Assigning TO memory: wrap value in .toByte() cast
                    "(${value.formatAsKotlin()}).toByte()"
                }
                isMemoryAccess(value) -> {
                    // Reading FROM memory: wrap in .toInt() to convert Byte to Int
                    "(${value.formatAsKotlin()}).toInt() and 0xFF"
                }
                else -> {
                    value.formatAsKotlin()
                }
            }
            "${target.formatAsKotlin()} = $valueStr"
        }
        is KotlinAst.Statement.If -> {
            val thenStr = if (thenBranch.statements.size == 1) {
                thenBranch.statements.first().emitToString()
            } else {
                "{ ... }"
            }
            val elseStr = elseBranch?.let {
                if (it.statements.size == 1) {
                    " else ${it.statements.first().emitToString()}"
                } else {
                    " else { ... }"
                }
            } ?: ""
            "if (${condition.formatAsKotlin()}) $thenStr$elseStr"
        }
        is KotlinAst.Statement.While -> {
            val labelStr = label?.let { "$it@ " } ?: ""
            "${labelStr}while (${condition.formatAsKotlin()}) { ... }"
        }
        is KotlinAst.Statement.For -> {
            "for ($variable in ${range.formatAsKotlin()}) { ... }"
        }
        is KotlinAst.Statement.Return -> {
            if (value != null) {
                "return ${value.formatAsKotlin()}"
            } else {
                "return"
            }
        }
        is KotlinAst.Statement.Comment -> {
            "// $text"
        }
        is KotlinAst.Statement.LocalVariable -> {
            val varKeyword = if (isVar) "var" else "val"
            val initStr = initializer?.formatAsKotlin() ?: ""
            "$varKeyword $name: $type = $initStr"
        }
        is KotlinAst.Statement.When -> {
            "when (${subject?.formatAsKotlin() ?: ""}) { ... }"
        }
        is KotlinAst.Statement.ExpressionStatement -> {
            expression.formatAsKotlin()
        }
    }
}
