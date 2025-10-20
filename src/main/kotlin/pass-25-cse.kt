package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 25: Common Subexpression Elimination (CSE)
 * - Identify duplicate computations within and across basic blocks
 * - Introduce temporary variables for common subexpressions
 * - Eliminate redundant computation
 * - Consider register pressure and profitability
 */

/**
 * A subexpression that appears multiple times
 */
data class Subexpression(
    val expression: Expression,
    val firstOccurrence: Int,           // Line index of first occurrence
    val occurrences: List<Int>,         // All line indices where this expression appears
    val savingsEstimate: Int,           // Estimated instruction savings
    val complexity: Int                 // Expression complexity (depth)
) {
    val occurrenceCount: Int get() = occurrences.size
    val isProfitable: Boolean get() = occurrenceCount > 1 && complexity > 1 && savingsEstimate > 0
}

/**
 * A temporary variable introduced for CSE
 */
data class CseTemporary(
    val name: String,
    val expression: Expression,
    val type: InferredType,
    val definitionLine: Int,
    val uses: List<Int>
)

/**
 * CSE result for a basic block (local CSE)
 */
data class BlockCseResult(
    val block: BasicBlock,
    val subexpressions: List<Subexpression>,
    val introducedTemporaries: List<CseTemporary>,
    val eliminatedExpressions: Map<Int, Expression>  // line -> original expression
)

/**
 * CSE result for a function
 */
data class FunctionCseResult(
    val function: FunctionCfg,
    val localCse: Map<Int, BlockCseResult>,        // Block leader -> local CSE result
    val globalSubexpressions: List<Subexpression>,  // Expressions common across blocks
    val globalTemporaries: List<CseTemporary>
)

/**
 * Complete CSE analysis
 */
data class CseAnalysis(
    val functions: List<FunctionCseResult>
)

/**
 * Perform CSE on all functions
 */
fun ExpressionTreeAnalysis.performCse(
    dominators: DominatorConstruction,
    simplifications: ExpressionSimplificationAnalysis
): CseAnalysis {
    val functionResults = functions.mapIndexed { index, funcExpr ->
        val domAnalysis = dominators.functions.getOrNull(index)
        val funcSimplifications = simplifications.functions.getOrNull(index)

        performFunctionCse(funcExpr, domAnalysis, funcSimplifications)
    }

    return CseAnalysis(functions = functionResults)
}

/**
 * Perform CSE on a single function
 */
private fun performFunctionCse(
    functionExpressions: FunctionExpressions,
    dominators: DominatorAnalysis?,
    simplifications: FunctionSimplifications?
): FunctionCseResult {
    // Step 1: Local CSE within each basic block
    val localCseResults = mutableMapOf<Int, BlockCseResult>()

    functionExpressions.blockExpressions.forEach { (leader, blockExpr) ->
        val result = performLocalCse(blockExpr, simplifications)
        localCseResults[leader] = result
    }

    // Step 2: Global CSE across basic blocks (using dominators)
    val (globalSubexpressions, globalTemporaries) = if (dominators != null) {
        performGlobalCse(functionExpressions, localCseResults, dominators)
    } else {
        emptyList<Subexpression>() to emptyList<CseTemporary>()
    }

    return FunctionCseResult(
        function = functionExpressions.function,
        localCse = localCseResults,
        globalSubexpressions = globalSubexpressions,
        globalTemporaries = globalTemporaries
    )
}

/**
 * Perform local CSE within a basic block
 */
private fun performLocalCse(
    blockExpressions: BlockExpressions,
    simplifications: FunctionSimplifications?
): BlockCseResult {
    // Collect all expressions in the block
    val expressionOccurrences = mutableMapOf<Expression, MutableList<Int>>()

    blockExpressions.expressions.forEachIndexed { index, exprTree ->
        // Use simplified expression if available
        val expr = simplifications?.blockSimplifications?.get(blockExpressions.block.leaderIndex)
            ?.simplifications?.get(exprTree)?.simplified ?: exprTree.root

        // Recursively collect subexpressions
        collectSubexpressions(expr, exprTree.lineRange.first, expressionOccurrences)
    }

    // Find subexpressions that occur multiple times
    val subexpressions = expressionOccurrences
        .filter { (expr, occurrences) -> occurrences.size > 1 && !isLeafExpression(expr) }
        .map { (expr, occurrences) ->
            Subexpression(
                expression = expr,
                firstOccurrence = occurrences.first(),
                occurrences = occurrences,
                savingsEstimate = estimateSavings(expr, occurrences.size),
                complexity = expressionComplexity(expr)
            )
        }
        .filter { it.isProfitable }
        .sortedByDescending { it.savingsEstimate }

    // Introduce temporaries for profitable subexpressions
    val temporaries = mutableListOf<CseTemporary>()
    val eliminatedExpressions = mutableMapOf<Int, Expression>()
    var tempCounter = 0

    subexpressions.forEach { subexpr ->
        // Check if introducing temporary doesn't create register pressure
        if (temporaries.size < 3) {  // Heuristic: limit number of temporaries
            val temp = CseTemporary(
                name = "temp_${blockExpressions.block.leaderIndex}_${tempCounter++}",
                expression = subexpr.expression,
                type = InferredType.UInt8(),
                definitionLine = subexpr.firstOccurrence,
                uses = subexpr.occurrences.drop(1)  // All uses except first
            )
            temporaries.add(temp)

            // Mark subsequent occurrences as eliminated
            subexpr.occurrences.drop(1).forEach { line ->
                eliminatedExpressions[line] = subexpr.expression
            }
        }
    }

    return BlockCseResult(
        block = blockExpressions.block,
        subexpressions = subexpressions,
        introducedTemporaries = temporaries,
        eliminatedExpressions = eliminatedExpressions
    )
}

/**
 * Perform global CSE across basic blocks using dominance information
 */
private fun performGlobalCse(
    functionExpressions: FunctionExpressions,
    localResults: Map<Int, BlockCseResult>,
    dominators: DominatorAnalysis
): Pair<List<Subexpression>, List<CseTemporary>> {
    // Find expressions that are available across multiple blocks
    val globalExpressionOccurrences = mutableMapOf<Expression, MutableList<Pair<Int, Int>>>()  // expr -> (block, line)

    localResults.forEach { (leader, result) ->
        result.subexpressions.forEach { subexpr ->
            subexpr.occurrences.forEach { line ->
                globalExpressionOccurrences
                    .getOrPut(subexpr.expression) { mutableListOf() }
                    .add(leader to line)
            }
        }
    }

    // Find expressions that appear in dominated blocks
    val globalSubexpressions = mutableListOf<Subexpression>()
    val globalTemporaries = mutableListOf<CseTemporary>()
    var tempCounter = 0

    globalExpressionOccurrences.forEach { (expr, occurrences) ->
        if (occurrences.size < 2) return@forEach

        // Group occurrences by block
        val blockGroups = occurrences.groupBy { it.first }
        if (blockGroups.size < 2) return@forEach

        // Find a common dominator for all occurrences
        val blocks = blockGroups.keys.toList()
        val commonDom = findCommonDominator(blocks, dominators)

        if (commonDom != null) {
            // Check if hoisting to common dominator is profitable
            val totalOccurrences = occurrences.size
            val complexity = expressionComplexity(expr)

            if (totalOccurrences > 2 && complexity > 1) {
                val subexpr = Subexpression(
                    expression = expr,
                    firstOccurrence = occurrences.minOf { it.second },
                    occurrences = occurrences.map { it.second },
                    savingsEstimate = estimateSavings(expr, totalOccurrences - 1),
                    complexity = complexity
                )
                globalSubexpressions.add(subexpr)

                // Introduce global temporary
                val temp = CseTemporary(
                    name = "global_temp_${tempCounter++}",
                    expression = expr,
                    type = InferredType.UInt8(),
                    definitionLine = commonDom,
                    uses = occurrences.map { it.second }
                )
                globalTemporaries.add(temp)
            }
        }
    }

    return globalSubexpressions to globalTemporaries
}

/**
 * Find common dominator for a set of blocks
 */
private fun findCommonDominator(
    blocks: List<Int>,
    dominators: DominatorAnalysis
): Int? {
    if (blocks.isEmpty()) return null
    if (blocks.size == 1) return blocks.first()

    // Collect all dominators for each block
    val allDominators: List<Set<Int>> = blocks.mapNotNull { leader ->
        val domNode = dominators.leaderToDomNode[leader] ?: return@mapNotNull null
        // Build set of all dominators by walking up the tree
        val doms = mutableSetOf<Int>()
        var current: DominatorNode? = domNode
        while (current != null) {
            doms.add(current.leaderIndex)
            current = current.immediateDominator
        }
        doms as Set<Int>
    }

    if (allDominators.isEmpty()) return null
    if (allDominators.size == 1) return allDominators.first().firstOrNull()

    // Find common dominators (intersection of all sets)
    val commonDoms = allDominators.reduce { acc, set -> acc.intersect(set) }

    // Return the most immediate common dominator (furthest from entry)
    // We find the dominator with the longest path to entry
    return commonDoms.maxByOrNull { leader ->
        // Count depth by walking up to root
        var depth = 0
        var current = dominators.leaderToDomNode[leader]
        while (current?.immediateDominator != null) {
            depth++
            current = current.immediateDominator
        }
        depth
    }
}

/**
 * Recursively collect all subexpressions
 */
private fun collectSubexpressions(
    expr: Expression,
    lineIndex: Int,
    occurrences: MutableMap<Expression, MutableList<Int>>
) {
    // Add this expression
    occurrences.getOrPut(expr) { mutableListOf() }.add(lineIndex)

    // Recursively collect children
    when (expr) {
        is Expression.BinaryOp -> {
            collectSubexpressions(expr.left, lineIndex, occurrences)
            collectSubexpressions(expr.right, lineIndex, occurrences)
        }

        is Expression.UnaryOp -> {
            collectSubexpressions(expr.operand, lineIndex, occurrences)
        }

        is Expression.Comparison -> {
            collectSubexpressions(expr.left, lineIndex, occurrences)
            collectSubexpressions(expr.right, lineIndex, occurrences)
        }

        is Expression.ArrayAccess -> {
            collectSubexpressions(expr.base, lineIndex, occurrences)
            collectSubexpressions(expr.index, lineIndex, occurrences)
        }

        is Expression.MemoryAccess -> {
            collectSubexpressions(expr.address, lineIndex, occurrences)
        }

        is Expression.Assignment -> {
            collectSubexpressions(expr.target, lineIndex, occurrences)
            collectSubexpressions(expr.value, lineIndex, occurrences)
        }

        is Expression.FunctionCall -> {
            expr.arguments.forEach { collectSubexpressions(it, lineIndex, occurrences) }
        }

        // Leaf nodes - no children
        is Expression.Literal,
        is Expression.VariableRef,
        is Expression.FieldAccess,
        is Expression.Phi -> {
            // No children to collect
        }
    }
}

/**
 * Check if expression is a leaf (literal or variable)
 */
private fun isLeafExpression(expr: Expression): Boolean {
    return expr is Expression.Literal || expr is Expression.VariableRef
}

/**
 * Calculate expression complexity (depth of expression tree)
 */
private fun expressionComplexity(expr: Expression): Int {
    return when (expr) {
        is Expression.BinaryOp -> 1 + maxOf(expressionComplexity(expr.left), expressionComplexity(expr.right))
        is Expression.UnaryOp -> 1 + expressionComplexity(expr.operand)
        is Expression.Comparison -> 1 + maxOf(expressionComplexity(expr.left), expressionComplexity(expr.right))
        is Expression.ArrayAccess -> 1 + maxOf(expressionComplexity(expr.base), expressionComplexity(expr.index))
        is Expression.MemoryAccess -> 1 + expressionComplexity(expr.address)
        is Expression.Assignment -> 1 + maxOf(expressionComplexity(expr.target), expressionComplexity(expr.value))
        is Expression.FunctionCall -> 1 + (expr.arguments.maxOfOrNull { expressionComplexity(it) } ?: 0)
        is Expression.FieldAccess -> 1 + expressionComplexity(expr.base)
        is Expression.Literal, is Expression.VariableRef, is Expression.Phi -> 0
    }
}

/**
 * Estimate instruction savings from eliminating N-1 occurrences of an expression
 */
private fun estimateSavings(expr: Expression, occurrenceCount: Int): Int {
    val instructionCost = estimateInstructionCost(expr)
    val eliminatedOccurrences = occurrenceCount - 1  // Keep first occurrence as definition

    // Savings = cost of eliminated occurrences - cost of temporary variable
    val savingsPerElimination = instructionCost - 1  // -1 for loading temporary
    return savingsPerElimination * eliminatedOccurrences
}

/**
 * Estimate number of 6502 instructions needed for an expression
 */
private fun estimateInstructionCost(expr: Expression): Int {
    return when (expr) {
        is Expression.BinaryOp -> {
            val leftCost = estimateInstructionCost(expr.left)
            val rightCost = estimateInstructionCost(expr.right)
            leftCost + rightCost + 1  // +1 for the operation itself
        }

        is Expression.UnaryOp -> estimateInstructionCost(expr.operand) + 1

        is Expression.Comparison -> {
            val leftCost = estimateInstructionCost(expr.left)
            val rightCost = estimateInstructionCost(expr.right)
            leftCost + rightCost + 1
        }

        is Expression.ArrayAccess -> {
            // Array access: load index + load base + indexed addressing
            estimateInstructionCost(expr.index) + 2
        }

        is Expression.MemoryAccess -> {
            estimateInstructionCost(expr.address) + 1
        }

        is Expression.FunctionCall -> {
            // Function call: setup args + JSR
            expr.arguments.sumOf { estimateInstructionCost(it) } + 2
        }

        is Expression.Literal -> 1  // LDA #immediate
        is Expression.VariableRef -> 1  // LDA variable
        is Expression.FieldAccess -> 2  // Base + offset
        is Expression.Assignment -> estimateInstructionCost(expr.value) + 1  // Value + STA
        is Expression.Phi -> 0  // No instruction cost (conceptual)
    }
}
