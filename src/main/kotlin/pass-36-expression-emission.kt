package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 36: Expression Emission
 * - Convert Pass 22 expression trees into Kotlin AST expressions
 * - Emit idiomatic Kotlin expressions from 6502 operations
 * - Handle arithmetic, logical, bitwise operations
 * - Generate memory accesses and register operations
 * - Optimize away unnecessary masking (and 0xFF)
 *
 * This pass bridges the gap between 6502 expression trees and Kotlin AST.
 */

/**
 * Result of expression emission analysis
 */
data class ExpressionEmissionResult(
    val functions: List<FunctionEmission>,
    val statistics: ExpressionEmissionStats
)

/**
 * Expression emission for a single function
 */
data class FunctionEmission(
    val function: FunctionCfg,
    val blockEmissions: Map<Int, BlockEmission>  // leader -> emitted expressions
)

/**
 * Emitted expressions for a basic block
 */
data class BlockEmission(
    val block: BasicBlock,
    val expressions: List<KotlinAst.Expression>
)

/**
 * Statistics about expression emission
 */
data class ExpressionEmissionStats(
    val totalExpressions: Int,
    val memoryAccesses: Int,
    val arithmeticOps: Int,
    val logicalOps: Int,
    val bitwiseOps: Int,
    val comparisons: Int
)

/**
 * Convert expression trees to Kotlin AST expressions
 */
fun ExpressionTreeAnalysis.emitKotlinExpressions(): ExpressionEmissionResult {

    val functionEmissions = mutableListOf<FunctionEmission>()
    var totalExpressions = 0
    var memoryAccesses = 0
    var arithmeticOps = 0
    var logicalOps = 0
    var bitwiseOps = 0
    var comparisons = 0

    // Convert each function's expressions
    for (funcExpr in this.functions) {
        val blockEmissions = mutableMapOf<Int, BlockEmission>()

        for ((leader, blockExpr) in funcExpr.blockExpressions) {
            val expressions = blockExpr.expressions.map { tree ->
                val expr = tree.root.toKotlinExpression()
                totalExpressions++

                // Count operation types
                when (tree.root) {
                    is Expression.MemoryAccess -> memoryAccesses++
                    is Expression.BinaryOp -> {
                        when ((tree.root as Expression.BinaryOp).op) {
                            BinaryOperator.ADD, BinaryOperator.SUB, BinaryOperator.MUL,
                            BinaryOperator.DIV, BinaryOperator.MOD -> arithmeticOps++
                            BinaryOperator.AND, BinaryOperator.OR, BinaryOperator.XOR -> bitwiseOps++
                            else -> {}
                        }
                    }
                    is Expression.Comparison -> comparisons++
                    else -> {}
                }

                expr
            }

            blockEmissions[leader] = BlockEmission(blockExpr.block, expressions)
        }

        functionEmissions.add(FunctionEmission(funcExpr.function, blockEmissions))
    }

    val stats = ExpressionEmissionStats(
        totalExpressions = totalExpressions,
        memoryAccesses = memoryAccesses,
        arithmeticOps = arithmeticOps,
        logicalOps = logicalOps,
        bitwiseOps = bitwiseOps,
        comparisons = comparisons
    )

    return ExpressionEmissionResult(functionEmissions, stats)
}

/**
 * Convert an Expression to KotlinAst.Expression
 */
internal fun Expression.toKotlinExpression(): KotlinAst.Expression {
    return when (this) {
        is Expression.Literal -> emitLiteral(this)
        is Expression.VariableRef -> emitVariableRef(this)
        is Expression.MemoryAccess -> emitMemoryAccess(this)
        is Expression.BinaryOp -> emitBinaryOp(this)
        is Expression.Comparison -> emitComparison(this)
        is Expression.UnaryOp -> emitUnaryOp(this)
        is Expression.ArrayAccess -> emitArrayAccess(this)
        is Expression.FieldAccess -> emitFieldAccess(this)
        is Expression.FunctionCall -> emitFunctionCall(this)
        is Expression.Assignment -> emitAssignment(this)
        is Expression.Phi -> emitPhi(this)
    }
}

/**
 * Emit a literal value
 */
private fun emitLiteral(expr: Expression.Literal): KotlinAst.Expression {
    return KotlinAst.Expression.Literal(
        value = expr.value,
        type = KotlinAst.KotlinType.Int
    )
}

/**
 * Emit a variable reference
 */
private fun emitVariableRef(expr: Expression.VariableRef): KotlinAst.Expression {
    val varName = when (expr.variable) {
        Variable.RegisterA -> "A"
        Variable.RegisterX -> "X"
        Variable.RegisterY -> "Y"
        Variable.StackPointer -> "SP"
        Variable.StatusFlags -> "flags"
        is Variable.Memory -> "mem_${expr.variable.address.toString(16)}"
        is Variable.ZeroPage -> "zp_${expr.variable.address.toString(16)}"
        is Variable.Indirect -> "ind_${expr.variable.baseAddress.toString(16)}"
    }

    return KotlinAst.Expression.Variable(varName)
}

/**
 * Emit a memory access: memory[address]
 * Memory is ByteArray, so reads return Byte which needs conversion to Int
 */
private fun emitMemoryAccess(expr: Expression.MemoryAccess): KotlinAst.Expression {
    val addressExpr = expr.address.toKotlinExpression()

    val arrayAccess = KotlinAst.Expression.ArrayAccess(
        array = KotlinAst.Expression.Variable("memory"),
        index = addressExpr
    )

    // Wrap in binary AND with 0xFF to convert signed byte to unsigned int
    // This generates: (memory[addr].toInt() and 0xFF)
    return KotlinAst.Expression.Binary(
        left = KotlinAst.Expression.Cast(
            expression = arrayAccess,
            targetType = KotlinAst.KotlinType.Int,
            isSafe = false
        ),
        operator = KotlinAst.BinaryOp.AND,
        right = KotlinAst.Expression.Literal(0xFF, KotlinAst.KotlinType.Int)
    )
}

/**
 * Emit a binary operation
 */
private fun emitBinaryOp(expr: Expression.BinaryOp): KotlinAst.Expression {
    val left = expr.left.toKotlinExpression()
    val right = expr.right.toKotlinExpression()

    val kotlinOp = when (expr.op) {
        BinaryOperator.ADD -> KotlinAst.BinaryOp.PLUS
        BinaryOperator.SUB -> KotlinAst.BinaryOp.MINUS
        BinaryOperator.MUL -> KotlinAst.BinaryOp.TIMES
        BinaryOperator.DIV -> KotlinAst.BinaryOp.DIV
        BinaryOperator.MOD -> KotlinAst.BinaryOp.MOD
        BinaryOperator.AND -> KotlinAst.BinaryOp.AND
        BinaryOperator.OR -> KotlinAst.BinaryOp.OR
        BinaryOperator.XOR -> KotlinAst.BinaryOp.XOR
        BinaryOperator.SHL -> KotlinAst.BinaryOp.SHL
        BinaryOperator.SHR -> KotlinAst.BinaryOp.SHR
        BinaryOperator.ROL, BinaryOperator.ROR -> KotlinAst.BinaryOp.PLUS  // Fallback for now
    }

    val binaryExpr = KotlinAst.Expression.Binary(left, kotlinOp, right)

    // For arithmetic operations, add masking to keep 8-bit values
    return when (expr.op) {
        BinaryOperator.ADD, BinaryOperator.SUB -> {
            // Wrap in (expr) and 0xFF to keep 8-bit
            KotlinAst.Expression.Binary(
                left = binaryExpr,
                operator = KotlinAst.BinaryOp.AND,
                right = KotlinAst.Expression.Literal(0xFF, KotlinAst.KotlinType.Int)
            )
        }
        else -> binaryExpr
    }
}

/**
 * Emit a comparison operation
 */
private fun emitComparison(expr: Expression.Comparison): KotlinAst.Expression {
    val left = expr.left.toKotlinExpression()
    val right = expr.right.toKotlinExpression()

    val kotlinOp = when (expr.op) {
        ComparisonOp.EQ -> KotlinAst.BinaryOp.EQ
        ComparisonOp.NE -> KotlinAst.BinaryOp.NE
        ComparisonOp.LT -> KotlinAst.BinaryOp.LT
        ComparisonOp.LE -> KotlinAst.BinaryOp.LE
        ComparisonOp.GT -> KotlinAst.BinaryOp.GT
        ComparisonOp.GE -> KotlinAst.BinaryOp.GE
    }

    return KotlinAst.Expression.Binary(left, kotlinOp, right)
}

/**
 * Emit a unary operation
 */
private fun emitUnaryOp(expr: Expression.UnaryOp): KotlinAst.Expression {
    val operand = expr.operand.toKotlinExpression()

    val kotlinOp = when (expr.op) {
        UnaryOperator.NEG -> KotlinAst.UnaryOp.MINUS
        UnaryOperator.NOT -> KotlinAst.UnaryOp.NOT
        UnaryOperator.COMPLEMENT -> KotlinAst.UnaryOp.INV
    }

    return KotlinAst.Expression.Unary(kotlinOp, operand)
}

/**
 * Emit an array access
 */
private fun emitArrayAccess(expr: Expression.ArrayAccess): KotlinAst.Expression {
    val base = expr.base.toKotlinExpression()
    val index = expr.index.toKotlinExpression()

    return KotlinAst.Expression.ArrayAccess(base, index)
}

/**
 * Emit a field access
 */
private fun emitFieldAccess(expr: Expression.FieldAccess): KotlinAst.Expression {
    val receiver = expr.base.toKotlinExpression()

    return KotlinAst.Expression.PropertyAccess(receiver, expr.fieldName)
}

/**
 * Emit a function call
 */
private fun emitFunctionCall(expr: Expression.FunctionCall): KotlinAst.Expression {
    val arguments = expr.arguments.map { it.toKotlinExpression() }

    return KotlinAst.Expression.Call(
        name = expr.target,
        arguments = arguments
    )
}

/**
 * Emit an assignment (target = value)
 */
private fun emitAssignment(expr: Expression.Assignment): KotlinAst.Expression {
    val target = expr.target.toKotlinExpression()
    val value = expr.value.toKotlinExpression()

    return KotlinAst.Expression.Binary(
        left = target,
        operator = KotlinAst.BinaryOp.ASSIGN,
        right = value
    )
}

/**
 * Emit a Phi function (SSA)
 * For now, just pick the first operand
 */
private fun emitPhi(expr: Expression.Phi): KotlinAst.Expression {
    // Phi functions are handled by SSA -> Kotlin translation
    // For now, just emit a variable reference to the phi variable
    return KotlinAst.Expression.Variable("phi_var")
}

/**
 * Format expression as readable Kotlin code
 */
fun KotlinAst.Expression.formatAsKotlin(): String {
    return when (this) {
        is KotlinAst.Expression.Literal -> {
            when (value) {
                is Int -> "0x${value.toString(16).uppercase()}"
                is UInt -> "${value}u"
                is UByte -> "${value}u"
                is UShort -> "${value}u"
                is Boolean -> value.toString()
                is String -> "\"$value\""
                else -> value.toString()
            }
        }
        is KotlinAst.Expression.Variable -> name
        is KotlinAst.Expression.Binary -> {
            val leftStr = left.formatAsKotlin()
            val rightStr = right.formatAsKotlin()
            "($leftStr ${operator.symbol} $rightStr)"
        }
        is KotlinAst.Expression.Unary -> {
            val operandStr = operand.formatAsKotlin()
            "${operator.symbol}$operandStr"
        }
        is KotlinAst.Expression.Call -> {
            val argsStr = arguments.joinToString(", ") { it.formatAsKotlin() }
            "$name($argsStr)"
        }
        is KotlinAst.Expression.ArrayAccess -> {
            val arrayStr = array.formatAsKotlin()
            val indexStr = index.formatAsKotlin()
            "$arrayStr[$indexStr]"
        }
        is KotlinAst.Expression.PropertyAccess -> {
            val receiverStr = receiver.formatAsKotlin()
            "$receiverStr.$propertyName"
        }
        is KotlinAst.Expression.Cast -> {
            val exprStr = expression.formatAsKotlin()
            "$exprStr as ${targetType}"
        }
        is KotlinAst.Expression.IfExpression -> {
            val condStr = condition.formatAsKotlin()
            val thenStr = thenValue.formatAsKotlin()
            val elseStr = elseValue.formatAsKotlin()
            "if ($condStr) $thenStr else $elseStr"
        }
        is KotlinAst.Expression.WhenExpression -> {
            val subjectStr = subject?.formatAsKotlin() ?: ""
            "when ($subjectStr) { ... }"
        }
    }
}
