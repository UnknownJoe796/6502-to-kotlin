package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 31: Copy Propagation & Constant Folding
 * - Replace variable copies with originals
 * - Evaluate constant expressions at compile time
 * - Perform strength reduction (multiply/divide by power-of-2 → shift)
 * - Apply algebraic simplifications recursively
 *
 * This pass extends Pass 18 (Constant Propagation) and Pass 24 (Expression Simplification)
 */

/**
 * Information about a copy relationship between variables
 */
data class CopyInfo(
    /** The variable that is a copy of another */
    val copyVariable: Variable,
    /** The source variable being copied */
    val sourceVariable: Variable,
    /** Line where the copy occurs */
    val copyLineIndex: Int,
    /** The block where this copy is defined */
    val blockLeader: Int,
    /** Range where this copy is valid (before source is redefined) */
    val validRange: CodeRange
)

/**
 * Range of code where a copy/constant is valid
 */
data class CodeRange(
    val startLine: Int,
    val endLine: Int,
    val blockLeaders: Set<Int>  // Blocks where this is valid
)

/**
 * A compile-time constant value for optimization
 */
data class OptimizationConstant(
    val value: Int,
    val type: InferredType,
    val source: ConstantSource
)

/**
 * Source of a constant value
 */
enum class ConstantSource {
    IMMEDIATE_LOAD,      // LDA #$05
    PROPAGATED,          // From previous constant
    FOLDED,              // Result of constant folding
    ALGEBRAIC            // Result of algebraic simplification
}

/**
 * Analysis of copies in a function
 */
data class CopyAnalysis(
    val function: FunctionCfg,
    /** All copy relationships found */
    val copies: List<CopyInfo>,
    /** Constants available at each program point */
    val constants: Map<Variable, OptimizationConstant>,
    /** Variables that can be replaced by their source */
    val propagatableCopies: List<CopyInfo>
)

/**
 * Result of a strength reduction optimization
 */
data class StrengthReduction(
    val originalOp: BinaryOperator,
    val originalOperand: Int,
    val newOp: BinaryOperator,
    val newOperand: Int,
    val benefit: String  // Description of the optimization
)

/**
 * Statistics for copy propagation
 */
data class CopyPropagationStats(
    val copiesEliminated: Int,
    val constantsFolded: Int,
    val strengthReductions: Int,
    val algebraicSimplifications: Int
)

/**
 * Complete copy propagation result for a function
 */
data class FunctionCopyPropagation(
    val function: FunctionCfg,
    val copyAnalysis: CopyAnalysis,
    val optimizedExpressions: Map<Int, List<Expression>>,  // blockLeader -> expressions
    val stats: CopyPropagationStats
)

/**
 * Complete copy propagation result
 */
data class CopyPropagationResult(
    val functionsOptimized: List<FunctionCopyPropagation>,
    val globalStats: CopyPropagationStats
)

/**
 * Perform copy propagation and constant folding on a function
 */
fun FunctionCfg.propagateCopies(
    codeFile: AssemblyCodeFile,
    dataFlow: FunctionDataFlow,
    constantProp: FunctionConstantAnalysis? = null,
    expressions: FunctionExpressions? = null
): FunctionCopyPropagation {

    // 1. Find copy relationships (register transfers and simple assignments)
    val copies = findCopies(codeFile, dataFlow)

    // 2. Determine which copies can be propagated
    val propagatableCopies = copies.filter { copy ->
        canPropagateCopy(copy, dataFlow)
    }

    // 3. Build constant map (from existing constant propagation if available)
    val constants = buildConstantMap(dataFlow, constantProp)

    val copyAnalysis = CopyAnalysis(
        function = this,
        copies = copies,
        constants = constants,
        propagatableCopies = propagatableCopies
    )

    // 4. Apply optimizations to expressions if available
    val optimizedExpressions = mutableMapOf<Int, List<Expression>>()
    var copiesEliminated = 0
    var constantsFolded = 0
    var strengthReductions = 0
    var algebraicSimplifications = 0

    if (expressions != null) {
        for ((blockLeader, blockExprs) in expressions.blockExpressions) {
            val optimized = blockExprs.expressions.map { exprTree ->
                val (newExpr, stats) = optimizeExpression(
                    exprTree.root,
                    constants,
                    propagatableCopies
                )
                copiesEliminated += stats.copiesEliminated
                constantsFolded += stats.constantsFolded
                strengthReductions += stats.strengthReductions
                algebraicSimplifications += stats.algebraicSimplifications
                newExpr
            }
            optimizedExpressions[blockLeader] = optimized
        }
    }

    val stats = CopyPropagationStats(
        copiesEliminated = copiesEliminated,
        constantsFolded = constantsFolded,
        strengthReductions = strengthReductions,
        algebraicSimplifications = algebraicSimplifications
    )

    return FunctionCopyPropagation(
        function = this,
        copyAnalysis = copyAnalysis,
        optimizedExpressions = optimizedExpressions,
        stats = stats
    )
}

/**
 * Find copy relationships in the function
 */
private fun FunctionCfg.findCopies(
    codeFile: AssemblyCodeFile,
    dataFlow: FunctionDataFlow
): List<CopyInfo> {
    val copies = mutableListOf<CopyInfo>()

    for (block in this.blocks) {
        for (lineIndex in block.lineIndexes) {
            val line = codeFile.lines[lineIndex]
            val instr = line.instruction ?: continue

            // Detect copy patterns:
            // TAX, TAY, TXA, TYA - register transfers
            // LDA $addr followed by STA $addr2 - memory copy
            val copy = when (instr.op) {
                AssemblyOp.TAX -> CopyInfo(
                    copyVariable = Variable.RegisterX,
                    sourceVariable = Variable.RegisterA,
                    copyLineIndex = lineIndex,
                    blockLeader = block.leaderIndex,
                    validRange = computeValidRange(
                        Variable.RegisterX,
                        Variable.RegisterA,
                        lineIndex,
                        block,
                        dataFlow
                    )
                )
                AssemblyOp.TAY -> CopyInfo(
                    copyVariable = Variable.RegisterY,
                    sourceVariable = Variable.RegisterA,
                    copyLineIndex = lineIndex,
                    blockLeader = block.leaderIndex,
                    validRange = computeValidRange(
                        Variable.RegisterY,
                        Variable.RegisterA,
                        lineIndex,
                        block,
                        dataFlow
                    )
                )
                AssemblyOp.TXA -> CopyInfo(
                    copyVariable = Variable.RegisterA,
                    sourceVariable = Variable.RegisterX,
                    copyLineIndex = lineIndex,
                    blockLeader = block.leaderIndex,
                    validRange = computeValidRange(
                        Variable.RegisterA,
                        Variable.RegisterX,
                        lineIndex,
                        block,
                        dataFlow
                    )
                )
                AssemblyOp.TYA -> CopyInfo(
                    copyVariable = Variable.RegisterA,
                    sourceVariable = Variable.RegisterY,
                    copyLineIndex = lineIndex,
                    blockLeader = block.leaderIndex,
                    validRange = computeValidRange(
                        Variable.RegisterA,
                        Variable.RegisterY,
                        lineIndex,
                        block,
                        dataFlow
                    )
                )
                else -> null
            }

            if (copy != null) {
                copies.add(copy)
            }
        }
    }

    return copies
}

/**
 * Compute the range where a copy is valid
 */
private fun computeValidRange(
    copyVar: Variable,
    sourceVar: Variable,
    copyLine: Int,
    block: BasicBlock,
    dataFlow: FunctionDataFlow
): CodeRange {
    // Find when source variable is next redefined
    val laterLines = block.lineIndexes.filter { it > copyLine }
    val sourceRedefined = laterLines.firstOrNull { line ->
        dataFlow.definitions.any { def ->
            def.lineRef.line == line && def.variable == sourceVar
        }
    }

    val endLine = sourceRedefined ?: block.endIndex

    return CodeRange(
        startLine = copyLine,
        endLine = endLine,
        blockLeaders = setOf(block.leaderIndex)
    )
}

/**
 * Check if a copy can be safely propagated
 */
private fun canPropagateCopy(copy: CopyInfo, dataFlow: FunctionDataFlow): Boolean {
    // Copy can be propagated if:
    // 1. Source is not redefined in valid range
    // 2. Copy variable uses are within valid range
    // 3. Source is available at all use sites

    val usesOfCopy = dataFlow.uses.filter {
        it.variable == copy.copyVariable &&
        it.lineRef.line > copy.copyLineIndex &&
        it.lineRef.line <= copy.validRange.endLine
    }

    // Check that source is not modified between copy and uses
    for (use in usesOfCopy) {
        val defsBetween = dataFlow.definitions.filter { def ->
            def.variable == copy.sourceVariable &&
            def.lineRef.line > copy.copyLineIndex &&
            def.lineRef.line < use.lineRef.line
        }
        if (defsBetween.isNotEmpty()) {
            return false
        }
    }

    return true
}

/**
 * Build map of constants from constant propagation analysis
 */
private fun buildConstantMap(
    dataFlow: FunctionDataFlow,
    constantProp: FunctionConstantAnalysis?
): Map<Variable, OptimizationConstant> {
    val constants = mutableMapOf<Variable, OptimizationConstant>()

    // Extract constants from constant propagation if available
    if (constantProp != null) {
        for ((blockLeader, facts) in constantProp.blockFacts) {
            val state = facts.exitState

            // Check register A
            when (val regA = state.registerA) {
                is ConstantValue.Byte -> {
                    constants[Variable.RegisterA] = OptimizationConstant(
                        value = regA.value,
                        type = InferredType.UInt8(IntRange(regA.value, regA.value)),
                        source = ConstantSource.PROPAGATED
                    )
                }
                else -> { /* Not a constant */ }
            }

            // Check register X
            when (val regX = state.registerX) {
                is ConstantValue.Byte -> {
                    constants[Variable.RegisterX] = OptimizationConstant(
                        value = regX.value,
                        type = InferredType.UInt8(IntRange(regX.value, regX.value)),
                        source = ConstantSource.PROPAGATED
                    )
                }
                else -> { /* Not a constant */ }
            }

            // Check register Y
            when (val regY = state.registerY) {
                is ConstantValue.Byte -> {
                    constants[Variable.RegisterY] = OptimizationConstant(
                        value = regY.value,
                        type = InferredType.UInt8(IntRange(regY.value, regY.value)),
                        source = ConstantSource.PROPAGATED
                    )
                }
                else -> { /* Not a constant */ }
            }
        }
    }

    return constants
}

/**
 * Optimize an expression with copy propagation and constant folding
 */
private fun optimizeExpression(
    expr: Expression,
    constants: Map<Variable, OptimizationConstant>,
    copies: List<CopyInfo>
): Pair<Expression, CopyPropagationStats> {
    var copiesEliminated = 0
    var constantsFolded = 0
    var strengthReductions = 0
    var algebraicSimplifications = 0

    val optimized = when (expr) {
        // Replace variable references with copies or constants
        is Expression.VariableRef -> {
            // Check if this is a constant
            val constant = constants[expr.variable]
            if (constant != null) {
                constantsFolded++
                Expression.Literal(constant.value, constant.type)
            } else {
                // Check if this is a copy
                val copy = copies.firstOrNull { it.copyVariable == expr.variable }
                if (copy != null) {
                    copiesEliminated++
                    Expression.VariableRef(copy.sourceVariable, expr.type)
                } else {
                    expr
                }
            }
        }

        // Constant folding for binary operations
        is Expression.BinaryOp -> {
            val (leftOpt, leftStats) = optimizeExpression(expr.left, constants, copies)
            val (rightOpt, rightStats) = optimizeExpression(expr.right, constants, copies)

            copiesEliminated += leftStats.copiesEliminated + rightStats.copiesEliminated
            constantsFolded += leftStats.constantsFolded + rightStats.constantsFolded
            strengthReductions += leftStats.strengthReductions + rightStats.strengthReductions
            algebraicSimplifications += leftStats.algebraicSimplifications + rightStats.algebraicSimplifications

            // Try constant folding
            if (leftOpt is Expression.Literal && rightOpt is Expression.Literal) {
                constantsFolded++
                val result = evaluateBinaryOp(expr.op, leftOpt.value, rightOpt.value)
                Expression.Literal(result)
            } else {
                // Try strength reduction
                val reduced = tryStrengthReduction(expr.op, leftOpt, rightOpt)
                if (reduced != null) {
                    strengthReductions++
                    reduced
                } else {
                    // Try algebraic simplification
                    val simplified = applyAlgebraicSimplifications(expr.op, leftOpt, rightOpt)
                    if (simplified != null) {
                        algebraicSimplifications++
                        simplified
                    } else {
                        Expression.BinaryOp(expr.op, leftOpt, rightOpt)
                    }
                }
            }
        }

        // Recursively optimize other expression types
        is Expression.UnaryOp -> {
            val (operandOpt, stats) = optimizeExpression(expr.operand, constants, copies)
            copiesEliminated += stats.copiesEliminated
            constantsFolded += stats.constantsFolded
            strengthReductions += stats.strengthReductions
            algebraicSimplifications += stats.algebraicSimplifications

            if (operandOpt is Expression.Literal) {
                constantsFolded++
                val result = evaluateUnaryOp(expr.op, operandOpt.value)
                Expression.Literal(result)
            } else {
                Expression.UnaryOp(expr.op, operandOpt)
            }
        }

        is Expression.ArrayAccess -> {
            val (baseOpt, baseStats) = optimizeExpression(expr.base, constants, copies)
            val (indexOpt, indexStats) = optimizeExpression(expr.index, constants, copies)
            copiesEliminated += baseStats.copiesEliminated + indexStats.copiesEliminated
            constantsFolded += baseStats.constantsFolded + indexStats.constantsFolded
            Expression.ArrayAccess(baseOpt, indexOpt, expr.type)
        }

        is Expression.Assignment -> {
            val (targetOpt, targetStats) = optimizeExpression(expr.target, constants, copies)
            val (valueOpt, valueStats) = optimizeExpression(expr.value, constants, copies)
            copiesEliminated += targetStats.copiesEliminated + valueStats.copiesEliminated
            constantsFolded += targetStats.constantsFolded + valueStats.constantsFolded
            Expression.Assignment(targetOpt, valueOpt)
        }

        else -> expr
    }

    val stats = CopyPropagationStats(
        copiesEliminated = copiesEliminated,
        constantsFolded = constantsFolded,
        strengthReductions = strengthReductions,
        algebraicSimplifications = algebraicSimplifications
    )

    return optimized to stats
}

/**
 * Evaluate a binary operation on constants
 */
private fun evaluateBinaryOp(op: BinaryOperator, left: Int, right: Int): Int {
    return when (op) {
        BinaryOperator.ADD -> (left + right) and 0xFF
        BinaryOperator.SUB -> (left - right) and 0xFF
        BinaryOperator.MUL -> (left * right) and 0xFF
        BinaryOperator.DIV -> if (right != 0) left / right else 0
        BinaryOperator.MOD -> if (right != 0) left % right else 0
        BinaryOperator.AND -> left and right
        BinaryOperator.OR -> left or right
        BinaryOperator.XOR -> left xor right
        BinaryOperator.SHL -> (left shl right) and 0xFF
        BinaryOperator.SHR -> left shr right
        else -> left  // Unknown operation
    }
}

/**
 * Evaluate a unary operation on a constant
 */
private fun evaluateUnaryOp(op: UnaryOperator, operand: Int): Int {
    return when (op) {
        UnaryOperator.NOT -> operand xor 0xFF
        UnaryOperator.NEG -> (-operand) and 0xFF
        else -> operand
    }
}

/**
 * Try strength reduction (expensive ops → cheap ops)
 */
private fun tryStrengthReduction(
    op: BinaryOperator,
    left: Expression,
    right: Expression
): Expression? {
    // Multiply/divide by power of 2 → shift
    if (right is Expression.Literal) {
        val value = right.value
        if (isPowerOfTwo(value)) {
            val shiftAmount = log2(value)
            return when (op) {
                BinaryOperator.MUL -> Expression.BinaryOp(
                    BinaryOperator.SHL,
                    left,
                    Expression.Literal(shiftAmount)
                )
                BinaryOperator.DIV -> Expression.BinaryOp(
                    BinaryOperator.SHR,
                    left,
                    Expression.Literal(shiftAmount)
                )
                else -> null
            }
        }
    }
    return null
}

/**
 * Apply algebraic simplifications from Pass 24
 */
private fun applyAlgebraicSimplifications(
    op: BinaryOperator,
    left: Expression,
    right: Expression
): Expression? {
    // x + 0 = x
    if (op == BinaryOperator.ADD && right is Expression.Literal && right.value == 0) {
        return left
    }
    if (op == BinaryOperator.ADD && left is Expression.Literal && left.value == 0) {
        return right
    }

    // x - 0 = x
    if (op == BinaryOperator.SUB && right is Expression.Literal && right.value == 0) {
        return left
    }

    // x * 1 = x
    if (op == BinaryOperator.MUL && right is Expression.Literal && right.value == 1) {
        return left
    }
    if (op == BinaryOperator.MUL && left is Expression.Literal && left.value == 1) {
        return right
    }

    // x * 0 = 0
    if (op == BinaryOperator.MUL && (
        (right is Expression.Literal && right.value == 0) ||
        (left is Expression.Literal && left.value == 0)
    )) {
        return Expression.Literal(0)
    }

    // x / 1 = x
    if (op == BinaryOperator.DIV && right is Expression.Literal && right.value == 1) {
        return left
    }

    // x & 0xFF = x (for 8-bit values)
    if (op == BinaryOperator.AND && right is Expression.Literal && right.value == 0xFF) {
        return left
    }

    // x | 0 = x
    if (op == BinaryOperator.OR && right is Expression.Literal && right.value == 0) {
        return left
    }

    // x ^ 0 = x
    if (op == BinaryOperator.XOR && right is Expression.Literal && right.value == 0) {
        return left
    }

    return null
}

/**
 * Check if a number is a power of 2
 */
private fun isPowerOfTwo(n: Int): Boolean {
    return n > 0 && (n and (n - 1)) == 0
}

/**
 * Calculate log2 of a power of 2
 */
private fun log2(n: Int): Int {
    var result = 0
    var value = n
    while (value > 1) {
        value = value shr 1
        result++
    }
    return result
}

/**
 * Perform copy propagation on all functions
 */
fun AssemblyCodeFile.propagateCopies(
    dataFlowAnalysis: DataFlowAnalysis,
    constantPropagation: ConstantPropagationAnalysis? = null,
    expressionAnalysis: ExpressionTreeAnalysis? = null
): CopyPropagationResult {

    val functionResults = dataFlowAnalysis.functions.mapIndexed { index, functionDataFlow ->
        val function = functionDataFlow.function
        val constantProp = constantPropagation?.functions?.getOrNull(index)
        val expressions = expressionAnalysis?.functions?.getOrNull(index)

        function.propagateCopies(this, functionDataFlow, constantProp, expressions)
    }

    val globalStats = CopyPropagationStats(
        copiesEliminated = functionResults.sumOf { it.stats.copiesEliminated },
        constantsFolded = functionResults.sumOf { it.stats.constantsFolded },
        strengthReductions = functionResults.sumOf { it.stats.strengthReductions },
        algebraicSimplifications = functionResults.sumOf { it.stats.algebraicSimplifications }
    )

    return CopyPropagationResult(
        functionsOptimized = functionResults,
        globalStats = globalStats
    )
}
