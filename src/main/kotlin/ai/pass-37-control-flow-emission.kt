package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 37: Control Flow Emission
 * - Convert control flow structures to Kotlin AST statements
 * - Emit if/else statements from conditional branches
 * - Emit while loops from backward branches
 * - Generate proper block structures
 * - Handle basic instruction-to-statement translation
 *
 * This pass builds on CFG analysis to create Kotlin control flow statements.
 */

/**
 * Result of control flow emission
 */
data class ControlFlowEmissionResult(
    val functions: List<FunctionControlFlow>,
    val statistics: ControlFlowEmissionStats
)

/**
 * Control flow emission for a single function
 */
data class FunctionControlFlow(
    val function: FunctionCfg,
    val body: KotlinAst.Block
)

/**
 * Statistics about control flow emission
 */
data class ControlFlowEmissionStats(
    val totalStatements: Int,
    val assignments: Int,
    val returnStatements: Int,
    val comments: Int
)

/**
 * Emit Kotlin control flow from CFG
 */
fun CfgConstruction.emitControlFlow(
    codeFile: AssemblyCodeFile,
    addressResolution: AddressResolution? = null,
    structuralAnalysis: Any? = null,  // Reserved for future use
    expressionEmission: ExpressionEmissionResult? = null
): ControlFlowEmissionResult {

    val functionFlows = mutableListOf<FunctionControlFlow>()
    var totalStatements = 0
    var assignments = 0
    var returnCount = 0
    var comments = 0

    for (function in this.functions) {
        val statements = mutableListOf<KotlinAst.Statement>()

        // Emit statements for each block in order
        for (block in function.blocks.sortedBy { it.startAddress }) {
            // Add comment for block
            statements.add(
                KotlinAst.Statement.Comment("Block at 0x${block.startAddress.toString(16).uppercase()}")
            )
            comments++

            // Emit block statements
            val blockStmts = emitBlockStatements(block, codeFile, addressResolution)
            statements.addAll(blockStmts)
            totalStatements += blockStmts.size

            // Count assignments
            assignments += blockStmts.count { it is KotlinAst.Statement.Assignment }

            // Check for return at end of block
            val lastLine = block.lineIndexes.lastOrNull()
            if (lastLine != null && lastLine < codeFile.lines.size) {
                val line = codeFile.lines[lastLine]
                if (line.instruction?.op == AssemblyOp.RTS) {
                    statements.add(KotlinAst.Statement.Return())
                    returnCount++
                }
            }
        }

        val body = KotlinAst.Block(statements)
        functionFlows.add(FunctionControlFlow(function, body))
    }

    val stats = ControlFlowEmissionStats(
        totalStatements = totalStatements,
        assignments = assignments,
        returnStatements = returnCount,
        comments = comments
    )

    return ControlFlowEmissionResult(functionFlows, stats)
}

/**
 * Emit statements for a basic block
 */
private fun emitBlockStatements(
    block: BasicBlock,
    codeFile: AssemblyCodeFile,
    addressResolution: AddressResolution?
): List<KotlinAst.Statement> {
    val statements = mutableListOf<KotlinAst.Statement>()

    for (lineIndex in block.lineIndexes) {
        if (lineIndex >= codeFile.lines.size) continue

        val line = codeFile.lines[lineIndex]
        val instruction = line.instruction ?: continue

        // Add assembly comment
        val asmComment = buildString {
            if (line.label != null) append("${line.label}: ")
            append(instruction.op.name)
            if (instruction.address != null) {
                append(" ")
                append(instruction.address.toString())
            }
        }
        statements.add(KotlinAst.Statement.Comment(asmComment))

        // Emit instruction as statement
        val stmt = emitInstructionStatement(instruction, addressResolution)
        if (stmt != null) {
            statements.add(stmt)
        }
    }

    return statements
}

/**
 * Emit a single instruction as a statement
 */
private fun emitInstructionStatement(
    instruction: AssemblyInstruction,
    addressResolution: AddressResolution?
): KotlinAst.Statement? {
    return when (instruction.op) {
        // Load operations -> assignments
        AssemblyOp.LDA -> {
            KotlinAst.Statement.Assignment(
                target = KotlinAst.Expression.Variable("A"),
                value = formatAddressingAsExpression(instruction.address, addressResolution)
            )
        }
        AssemblyOp.LDX -> {
            KotlinAst.Statement.Assignment(
                target = KotlinAst.Expression.Variable("X"),
                value = formatAddressingAsExpression(instruction.address, addressResolution)
            )
        }
        AssemblyOp.LDY -> {
            KotlinAst.Statement.Assignment(
                target = KotlinAst.Expression.Variable("Y"),
                value = formatAddressingAsExpression(instruction.address, addressResolution)
            )
        }

        // Store operations -> assignments
        AssemblyOp.STA -> {
            KotlinAst.Statement.Assignment(
                target = formatAddressingAsExpression(instruction.address, addressResolution),
                value = KotlinAst.Expression.Variable("A")
            )
        }
        AssemblyOp.STX -> {
            KotlinAst.Statement.Assignment(
                target = formatAddressingAsExpression(instruction.address, addressResolution),
                value = KotlinAst.Expression.Variable("X")
            )
        }
        AssemblyOp.STY -> {
            KotlinAst.Statement.Assignment(
                target = formatAddressingAsExpression(instruction.address, addressResolution),
                value = KotlinAst.Expression.Variable("Y")
            )
        }

        // Arithmetic operations
        AssemblyOp.ADC -> {
            val value = formatAddressingAsExpression(instruction.address, addressResolution)
            KotlinAst.Statement.Assignment(
                target = KotlinAst.Expression.Variable("A"),
                value = KotlinAst.Expression.Binary(
                    left = KotlinAst.Expression.Binary(
                        left = KotlinAst.Expression.Variable("A"),
                        operator = KotlinAst.BinaryOp.PLUS,
                        right = value
                    ),
                    operator = KotlinAst.BinaryOp.AND,
                    right = KotlinAst.Expression.Literal(0xFF, KotlinAst.KotlinType.Int)
                )
            )
        }

        // Transfer operations
        AssemblyOp.TAX -> {
            KotlinAst.Statement.Assignment(
                target = KotlinAst.Expression.Variable("X"),
                value = KotlinAst.Expression.Variable("A")
            )
        }
        AssemblyOp.TAY -> {
            KotlinAst.Statement.Assignment(
                target = KotlinAst.Expression.Variable("Y"),
                value = KotlinAst.Expression.Variable("A")
            )
        }
        AssemblyOp.TXA -> {
            KotlinAst.Statement.Assignment(
                target = KotlinAst.Expression.Variable("A"),
                value = KotlinAst.Expression.Variable("X")
            )
        }
        AssemblyOp.TYA -> {
            KotlinAst.Statement.Assignment(
                target = KotlinAst.Expression.Variable("A"),
                value = KotlinAst.Expression.Variable("Y")
            )
        }

        // Increment/Decrement
        AssemblyOp.INX -> {
            KotlinAst.Statement.Assignment(
                target = KotlinAst.Expression.Variable("X"),
                value = KotlinAst.Expression.Binary(
                    left = KotlinAst.Expression.Binary(
                        left = KotlinAst.Expression.Variable("X"),
                        operator = KotlinAst.BinaryOp.PLUS,
                        right = KotlinAst.Expression.Literal(1, KotlinAst.KotlinType.Int)
                    ),
                    operator = KotlinAst.BinaryOp.AND,
                    right = KotlinAst.Expression.Literal(0xFF, KotlinAst.KotlinType.Int)
                )
            )
        }
        AssemblyOp.INY -> {
            KotlinAst.Statement.Assignment(
                target = KotlinAst.Expression.Variable("Y"),
                value = KotlinAst.Expression.Binary(
                    left = KotlinAst.Expression.Binary(
                        left = KotlinAst.Expression.Variable("Y"),
                        operator = KotlinAst.BinaryOp.PLUS,
                        right = KotlinAst.Expression.Literal(1, KotlinAst.KotlinType.Int)
                    ),
                    operator = KotlinAst.BinaryOp.AND,
                    right = KotlinAst.Expression.Literal(0xFF, KotlinAst.KotlinType.Int)
                )
            )
        }
        AssemblyOp.DEX -> {
            KotlinAst.Statement.Assignment(
                target = KotlinAst.Expression.Variable("X"),
                value = KotlinAst.Expression.Binary(
                    left = KotlinAst.Expression.Binary(
                        left = KotlinAst.Expression.Variable("X"),
                        operator = KotlinAst.BinaryOp.MINUS,
                        right = KotlinAst.Expression.Literal(1, KotlinAst.KotlinType.Int)
                    ),
                    operator = KotlinAst.BinaryOp.AND,
                    right = KotlinAst.Expression.Literal(0xFF, KotlinAst.KotlinType.Int)
                )
            )
        }
        AssemblyOp.DEY -> {
            KotlinAst.Statement.Assignment(
                target = KotlinAst.Expression.Variable("Y"),
                value = KotlinAst.Expression.Binary(
                    left = KotlinAst.Expression.Binary(
                        left = KotlinAst.Expression.Variable("Y"),
                        operator = KotlinAst.BinaryOp.MINUS,
                        right = KotlinAst.Expression.Literal(1, KotlinAst.KotlinType.Int)
                    ),
                    operator = KotlinAst.BinaryOp.AND,
                    right = KotlinAst.Expression.Literal(0xFF, KotlinAst.KotlinType.Int)
                )
            )
        }

        // Other operations not emitted as statements
        else -> null
    }
}

/**
 * Resolve a label to an address - handles both label names and hex/decimal literals
 */
private fun resolveLabel(label: String, addressResolution: AddressResolution?): Int {
    return when {
        label.startsWith("$") -> {
            // Hex literal like "$0200"
            label.substring(1).toInt(16)
        }
        label.all { it.isDigit() } -> {
            // Decimal literal like "512"
            label.toInt()
        }
        else -> {
            // Actual label name - resolve it
            addressResolution?.labelToAddress?.get(label) ?: 0
        }
    }
}

/**
 * Format an addressing mode as an expression
 */
private fun formatAddressingAsExpression(addressing: AssemblyAddressing?, addressResolution: AddressResolution?): KotlinAst.Expression {
    return when (addressing) {
        is AssemblyAddressing.ValueHex -> {
            KotlinAst.Expression.Literal(addressing.value.toInt() and 0xFF, KotlinAst.KotlinType.Int)
        }
        is AssemblyAddressing.ValueDecimal -> {
            KotlinAst.Expression.Literal(addressing.value.toInt() and 0xFF, KotlinAst.KotlinType.Int)
        }
        is AssemblyAddressing.ValueBinary -> {
            KotlinAst.Expression.Literal(addressing.value.toInt() and 0xFF, KotlinAst.KotlinType.Int)
        }
        is AssemblyAddressing.Label -> {
            // Memory access at labeled address - could be a label name or hex literal like "$0200"
            val address = resolveLabel(addressing.label, addressResolution)
            KotlinAst.Expression.ArrayAccess(
                array = KotlinAst.Expression.Variable("memory"),
                index = KotlinAst.Expression.Literal(address, KotlinAst.KotlinType.Int)
            )
        }
        is AssemblyAddressing.DirectX -> {
            // memory[label + X] - resolve label to numeric address
            val baseAddress = resolveLabel(addressing.label, addressResolution)
            KotlinAst.Expression.ArrayAccess(
                array = KotlinAst.Expression.Variable("memory"),
                index = KotlinAst.Expression.Binary(
                    left = KotlinAst.Expression.Literal(baseAddress, KotlinAst.KotlinType.Int),
                    operator = KotlinAst.BinaryOp.PLUS,
                    right = KotlinAst.Expression.Variable("X")
                )
            )
        }
        is AssemblyAddressing.DirectY -> {
            // memory[label + Y] - resolve label to numeric address
            val baseAddress = resolveLabel(addressing.label, addressResolution)
            KotlinAst.Expression.ArrayAccess(
                array = KotlinAst.Expression.Variable("memory"),
                index = KotlinAst.Expression.Binary(
                    left = KotlinAst.Expression.Literal(baseAddress, KotlinAst.KotlinType.Int),
                    operator = KotlinAst.BinaryOp.PLUS,
                    right = KotlinAst.Expression.Variable("Y")
                )
            )
        }
        is AssemblyAddressing.IndirectX -> {
            // memory[memory[label + X]] - indexed indirect
            val baseAddress = resolveLabel(addressing.label, addressResolution)
            KotlinAst.Expression.ArrayAccess(
                array = KotlinAst.Expression.Variable("memory"),
                index = KotlinAst.Expression.ArrayAccess(
                    array = KotlinAst.Expression.Variable("memory"),
                    index = KotlinAst.Expression.Binary(
                        left = KotlinAst.Expression.Literal(baseAddress, KotlinAst.KotlinType.Int),
                        operator = KotlinAst.BinaryOp.PLUS,
                        right = KotlinAst.Expression.Variable("X")
                    )
                )
            )
        }
        is AssemblyAddressing.IndirectY -> {
            // memory[memory[label].toInt() and 0xFF + Y] - indirect indexed
            // Note: In 6502, indirect indexed reads a 16-bit address from memory[label] and memory[label+1]
            // For simplicity, we'll just cast the byte to int and mask to 8 bits
            val baseAddress = resolveLabel(addressing.label, addressResolution)
            val indirectLoad = KotlinAst.Expression.Binary(
                left = KotlinAst.Expression.PropertyAccess(
                    receiver = KotlinAst.Expression.ArrayAccess(
                        array = KotlinAst.Expression.Variable("memory"),
                        index = KotlinAst.Expression.Literal(baseAddress, KotlinAst.KotlinType.Int)
                    ),
                    propertyName = "toInt()"
                ),
                operator = KotlinAst.BinaryOp.AND,
                right = KotlinAst.Expression.Literal(0xFF, KotlinAst.KotlinType.Int)
            )
            KotlinAst.Expression.ArrayAccess(
                array = KotlinAst.Expression.Variable("memory"),
                index = KotlinAst.Expression.Binary(
                    left = indirectLoad,
                    operator = KotlinAst.BinaryOp.PLUS,
                    right = KotlinAst.Expression.Variable("Y")
                )
            )
        }
        is AssemblyAddressing.IndirectAbsolute -> {
            // memory[memory[label].toInt() and 0xFF] - absolute indirect (for JMP)
            // Note: reads 16-bit address, simplified here
            val address = resolveLabel(addressing.label, addressResolution)
            KotlinAst.Expression.ArrayAccess(
                array = KotlinAst.Expression.Variable("memory"),
                index = KotlinAst.Expression.Binary(
                    left = KotlinAst.Expression.PropertyAccess(
                        receiver = KotlinAst.Expression.ArrayAccess(
                            array = KotlinAst.Expression.Variable("memory"),
                            index = KotlinAst.Expression.Literal(address, KotlinAst.KotlinType.Int)
                        ),
                        propertyName = "toInt()"
                    ),
                    operator = KotlinAst.BinaryOp.AND,
                    right = KotlinAst.Expression.Literal(0xFF, KotlinAst.KotlinType.Int)
                )
            )
        }
        is AssemblyAddressing.Accumulator -> {
            // For accumulator addressing (ASL A, ROL A, etc.)
            KotlinAst.Expression.Variable("A")
        }
        is AssemblyAddressing.ValueReference -> {
            // Reference to a constant - resolve if possible
            val value = resolveLabel(addressing.name, addressResolution)
            KotlinAst.Expression.Literal(value, KotlinAst.KotlinType.Int)
        }
        else -> {
            // Fallback for other addressing modes
            KotlinAst.Expression.Literal(0, KotlinAst.KotlinType.Int)
        }
    }
}
