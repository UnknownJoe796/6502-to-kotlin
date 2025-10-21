package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 26: Expression Hoisting (Loop-Invariant Code Motion)
 * - Identify loop-invariant expressions
 * - Hoist invariant computations out of loops
 * - Create loop preheaders if needed
 * - Introduce temporaries for hoisted expressions
 * - Reduce redundant computation in loops
 */

/**
 * An expression that can be hoisted out of a loop
 */
data class HoistableExpression(
    val expression: Expression,
    val originalLocation: Int,          // Line index inside loop
    val originalBlock: Int,             // Block leader inside loop
    val hoistTarget: Int,               // Block leader to hoist to (preheader)
    val loop: NaturalLoop,
    val isLoopInvariant: Boolean,
    val savingsPerIteration: Int,
    val estimatedIterations: Int = 10   // Conservative estimate
) {
    val totalSavings: Int get() = savingsPerIteration * estimatedIterations
    val isProfitable: Boolean get() = totalSavings > 5  // Heuristic threshold
}

/**
 * A temporary variable for a hoisted expression
 */
data class HoistedTemporary(
    val name: String,
    val expression: Expression,
    val type: InferredType,
    val loop: NaturalLoop,
    val preheaderLine: Int,
    val usesInLoop: List<Int>
)

/**
 * Expression hoisting result for a function
 */
data class ExpressionHoistingResult(
    val function: FunctionCfg,
    val hoistedExpressions: List<HoistableExpression>,
    val loopInvariantTemporaries: Map<NaturalLoop, List<HoistedTemporary>>,
    val createdPreheaders: Map<NaturalLoop, Int>  // loop -> preheader block leader
)

/**
 * Complete expression hoisting analysis
 */
data class ExpressionHoistingAnalysis(
    val functions: List<ExpressionHoistingResult>
)

/**
 * Perform expression hoisting on all functions
 */
fun ExpressionTreeAnalysis.hoistLoopInvariantExpressions(
    cfg: CfgConstruction,
    loops: LoopDetection,
    dominators: DominatorConstruction,
    simplifications: ExpressionSimplificationAnalysis,
    cse: CseAnalysis
): ExpressionHoistingAnalysis {
    val functionResults = cfg.functions.mapIndexed { index, function ->
        val funcExpr = functions.getOrNull(index)
        val funcLoops = loops.functions.getOrNull(index)
        val funcDom = dominators.functions.getOrNull(index)
        val funcSimp = simplifications.functions.getOrNull(index)
        val funcCse = cse.functions.getOrNull(index)

        if (funcExpr != null && funcLoops != null && funcDom != null) {
            performFunctionHoisting(function, funcExpr, funcLoops, funcDom, funcSimp, funcCse)
        } else {
            ExpressionHoistingResult(
                function = function,
                hoistedExpressions = emptyList(),
                loopInvariantTemporaries = emptyMap(),
                createdPreheaders = emptyMap()
            )
        }
    }

    return ExpressionHoistingAnalysis(functions = functionResults)
}

/**
 * Perform expression hoisting for a single function
 */
private fun performFunctionHoisting(
    function: FunctionCfg,
    expressions: FunctionExpressions,
    loopAnalysis: FunctionLoopInfo,
    dominators: DominatorAnalysis,
    simplifications: FunctionSimplifications?,
    cse: FunctionCseResult?
): ExpressionHoistingResult {
    val hoistableExpressions = mutableListOf<HoistableExpression>()
    val loopTemporaries = mutableMapOf<NaturalLoop, MutableList<HoistedTemporary>>()
    val createdPreheaders = mutableMapOf<NaturalLoop, Int>()

    // Process each natural loop
    loopAnalysis.loops.forEach { loop ->
        // Ensure loop has a preheader (single entry block before loop header)
        val preheader = ensureLoopPreheader(loop, function, dominators)
        if (preheader != null) {
            createdPreheaders[loop] = preheader
        }

        // Find loop-invariant expressions
        val invariantExprs = findLoopInvariantExpressions(
            loop,
            expressions,
            simplifications,
            dominators
        )

        // Create hoistable expressions
        invariantExprs.forEach { (expr, location) ->
            val hoistTarget = preheader ?: loop.header

            val hoistable = HoistableExpression(
                expression = expr,
                originalLocation = location.second,
                originalBlock = location.first,
                hoistTarget = hoistTarget,
                loop = loop,
                isLoopInvariant = true,
                savingsPerIteration = estimateInstructionCost(expr),
                estimatedIterations = estimateLoopIterations(loop)
            )

            if (hoistable.isProfitable) {
                hoistableExpressions.add(hoistable)
            }
        }

        // Create temporaries for hoisted expressions
        val temporaries = createHoistedTemporaries(
            hoistableExpressions.filter { it.loop == loop },
            loop,
            preheader ?: loop.header
        )
        if (temporaries.isNotEmpty()) {
            loopTemporaries[loop] = temporaries.toMutableList()
        }
    }

    return ExpressionHoistingResult(
        function = function,
        hoistedExpressions = hoistableExpressions,
        loopInvariantTemporaries = loopTemporaries,
        createdPreheaders = createdPreheaders
    )
}

/**
 * Ensure loop has a preheader block (single predecessor outside loop)
 * Returns existing or created preheader block leader
 */
private fun ensureLoopPreheader(
    loop: NaturalLoop,
    function: FunctionCfg,
    dominators: DominatorAnalysis
): Int? {
    // Find all edges entering the loop header from outside the loop
    val enteringEdges = function.edges.filter { edge ->
        edge.toLeader == loop.header &&
        edge.fromLeader !in loop.body
    }

    // If there's exactly one entering edge, that block can serve as preheader
    if (enteringEdges.size == 1) {
        return enteringEdges.first().fromLeader
    }

    // Multiple entering edges - would need to create a new preheader block
    // For now, return null (would require CFG modification)
    return null
}

/**
 * Find loop-invariant expressions in a loop
 */
private fun findLoopInvariantExpressions(
    loop: NaturalLoop,
    expressions: FunctionExpressions,
    simplifications: FunctionSimplifications?,
    dominators: DominatorAnalysis
): List<Pair<Expression, Pair<Int, Int>>> {  // (expr, (block, line))
    val invariantExpressions = mutableListOf<Pair<Expression, Pair<Int, Int>>>()
    val loopBlockLeaders = loop.body

    // Collect all variables defined inside the loop
    val loopDefinedVars = mutableSetOf<Variable>()
    loop.body.forEach { blockLeader ->
        val blockExpr = expressions.blockExpressions[blockLeader]
        blockExpr?.assignments?.keys?.forEach { variable ->
            loopDefinedVars.add(variable)
        }
    }

    // Check each expression in loop blocks
    loop.body.forEach { blockLeader ->
        val blockExpr = expressions.blockExpressions[blockLeader] ?: return@forEach

        blockExpr.expressions.forEach { exprTree ->
            // Use simplified expression if available
            val expr = simplifications?.blockSimplifications?.get(blockLeader)
                ?.simplifications?.get(exprTree)?.simplified ?: exprTree.root

            // Check if expression is loop-invariant
            if (isLoopInvariant(expr, loopDefinedVars, loopBlockLeaders)) {
                invariantExpressions.add(expr to (blockLeader to exprTree.lineRange.first))
            }
        }
    }

    return invariantExpressions
}

/**
 * Check if an expression is loop-invariant
 * An expression is loop-invariant if:
 * 1. All operands are constants, OR
 * 2. All operands are variables defined outside the loop, OR
 * 3. All operands are themselves loop-invariant expressions
 */
private fun isLoopInvariant(
    expr: Expression,
    loopDefinedVars: Set<Variable>,
    loopBlocks: Set<Int>
): Boolean {
    return when (expr) {
        is Expression.Literal -> true  // Constants are always loop-invariant

        is Expression.VariableRef -> {
            // Variable is loop-invariant if not defined in loop
            expr.variable !in loopDefinedVars
        }

        is Expression.BinaryOp -> {
            // Binary op is loop-invariant if both operands are
            isLoopInvariant(expr.left, loopDefinedVars, loopBlocks) &&
            isLoopInvariant(expr.right, loopDefinedVars, loopBlocks)
        }

        is Expression.UnaryOp -> {
            // Unary op is loop-invariant if operand is
            isLoopInvariant(expr.operand, loopDefinedVars, loopBlocks)
        }

        is Expression.Comparison -> {
            // Comparison is loop-invariant if both sides are
            isLoopInvariant(expr.left, loopDefinedVars, loopBlocks) &&
            isLoopInvariant(expr.right, loopDefinedVars, loopBlocks)
        }

        is Expression.ArrayAccess -> {
            // Array access is loop-invariant if base and index are
            isLoopInvariant(expr.base, loopDefinedVars, loopBlocks) &&
            isLoopInvariant(expr.index, loopDefinedVars, loopBlocks)
        }

        is Expression.MemoryAccess -> {
            // Memory access is loop-invariant if address is
            // NOTE: This is conservative - memory could be modified in loop
            // For safety, we'll consider memory accesses as NOT loop-invariant
            false
        }

        is Expression.FieldAccess -> {
            isLoopInvariant(expr.base, loopDefinedVars, loopBlocks)
        }

        is Expression.FunctionCall -> {
            // Function calls are generally NOT loop-invariant (side effects)
            false
        }

        is Expression.Assignment -> {
            // Assignments are NOT loop-invariant (they modify state)
            false
        }

        is Expression.Phi -> {
            // Phi functions are NOT loop-invariant
            false
        }
    }
}

/**
 * Create temporaries for hoisted expressions
 */
private fun createHoistedTemporaries(
    hoistableExprs: List<HoistableExpression>,
    loop: NaturalLoop,
    preheaderBlock: Int
): List<HoistedTemporary> {
    val temporaries = mutableListOf<HoistedTemporary>()
    var tempCounter = 0

    // Group expressions by value (eliminate duplicates)
    val uniqueExprs = hoistableExprs.groupBy { it.expression }

    uniqueExprs.forEach { (expr, instances) ->
        val temp = HoistedTemporary(
            name = "loop_${loop.header}_inv_${tempCounter++}",
            expression = expr,
            type = InferredType.UInt8(),
            loop = loop,
            preheaderLine = preheaderBlock,
            usesInLoop = instances.map { it.originalLocation }
        )
        temporaries.add(temp)
    }

    return temporaries
}

/**
 * Estimate number of loop iterations (heuristic)
 */
private fun estimateLoopIterations(loop: NaturalLoop): Int {
    // Heuristics based on loop type
    return when (loop.loopType) {
        LoopType.DO_WHILE, LoopType.WHILE -> {
            // Check if there's a counted loop pattern (e.g., for i = 0 to N)
            // For now, use conservative estimate
            10
        }
        LoopType.FOR_LIKE -> {
            // Try to extract iteration count from loop condition
            // For now, use conservative estimate
            20
        }
        LoopType.INFINITE -> {
            // Infinite loops - very high iteration count
            1000
        }
        LoopType.MULTI_EXIT -> 10
    }
}

/**
 * Estimate instruction cost for an expression (reuse from CSE)
 */
private fun estimateInstructionCost(expr: Expression): Int {
    return when (expr) {
        is Expression.BinaryOp -> {
            val leftCost = estimateInstructionCost(expr.left)
            val rightCost = estimateInstructionCost(expr.right)
            leftCost + rightCost + 1
        }

        is Expression.UnaryOp -> estimateInstructionCost(expr.operand) + 1

        is Expression.Comparison -> {
            val leftCost = estimateInstructionCost(expr.left)
            val rightCost = estimateInstructionCost(expr.right)
            leftCost + rightCost + 1
        }

        is Expression.ArrayAccess -> {
            estimateInstructionCost(expr.index) + 2
        }

        is Expression.MemoryAccess -> {
            estimateInstructionCost(expr.address) + 1
        }

        is Expression.FunctionCall -> {
            expr.arguments.sumOf { estimateInstructionCost(it) } + 2
        }

        is Expression.Literal -> 1
        is Expression.VariableRef -> 1
        is Expression.FieldAccess -> 2
        is Expression.Assignment -> estimateInstructionCost(expr.value) + 1
        is Expression.Phi -> 0
    }
}
