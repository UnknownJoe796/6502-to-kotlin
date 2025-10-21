package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 30: Dead Code Elimination (DCE)
 * - Remove unreachable basic blocks
 * - Eliminate dead stores (writes never read)
 * - Remove unused variables
 * - Eliminate dead expressions (computed but unused)
 * - Preserve side effects (hardware I/O, function calls)
 */

/**
 * Hardware memory-mapped I/O regions that must be preserved
 */
object HardwareRegions {
    /** PPU (Picture Processing Unit) registers */
    val PPU_REGISTERS = 0x2000..0x2007

    /** APU (Audio Processing Unit) and I/O registers */
    val APU_IO_REGISTERS = 0x4000..0x4017

    /** Check if an address is hardware-mapped */
    fun isHardwareAddress(address: Int): Boolean {
        return address in PPU_REGISTERS || address in APU_IO_REGISTERS
    }
}

/**
 * Analysis of dead code in the program
 */
data class DeadCodeAnalysis(
    /** Basic block leaders that are unreachable */
    val deadBlocks: Set<Int>,

    /** Store operations that write to variables never read (line indexes) */
    val deadStores: Set<Int>,

    /** Variables that are never used */
    val deadVariables: Set<VariableId>,

    /** Expression computations whose results are unused */
    val deadExpressions: Set<ExpressionTreeId>,

    /** Operations preserved despite appearing dead (have side effects, line indexes) */
    val preservedForSideEffects: Set<Int>
)

/**
 * Identifier for an expression tree
 */
data class ExpressionTreeId(
    val blockLeader: Int,
    val exprIndex: Int  // Index within block's expression list
)

/**
 * Result of dead code elimination for a single function
 */
data class FunctionDeadCodeElimination(
    val function: FunctionCfg,
    val analysis: DeadCodeAnalysis,
    val removedInstructions: Int,
    val removedBlocks: Int,
    val removedVariables: Int
)

/**
 * Statistics about dead code elimination
 */
data class DeadCodeStats(
    val totalDeadStores: Int,
    val totalDeadVariables: Int,
    val totalDeadBlocks: Int,
    val totalInstructionsRemoved: Int,
    val preservedForSideEffects: Int
)

/**
 * Complete dead code elimination result
 */
data class DeadCodeEliminationResult(
    val functionsOptimized: List<FunctionDeadCodeElimination>,
    val globalStats: DeadCodeStats
)

/**
 * Perform dead code elimination on a function's CFG
 */
fun FunctionCfg.eliminateDeadCode(
    codeFile: AssemblyCodeFile,
    dataFlow: FunctionDataFlow,
    functionVariables: FunctionVariables? = null,
    functionExpressions: FunctionExpressions? = null
): FunctionDeadCodeElimination {

    val deadBlocks = mutableSetOf<Int>()
    val deadStores = mutableSetOf<Int>()  // Line indexes
    val deadVariables = mutableSetOf<VariableId>()
    val deadExpressions = mutableSetOf<ExpressionTreeId>()
    val preservedForSideEffects = mutableSetOf<Int>()  // Line indexes

    // 1. Find unreachable blocks
    val reachableBlocks = this.blocks.map { it.leaderIndex }.toSet()
    // Any block not in CFG is already eliminated, so we track those removed from CFG
    // In practice, unreachable blocks should already be filtered by reachability analysis
    // So this pass focuses on blocks that became unreachable after optimizations

    // 2. Find dead stores using liveness analysis
    for (block in this.blocks) {
        val blockLeader = block.leaderIndex
        val blockLiveness = dataFlow.blockFacts[blockLeader] ?: continue

        // For each instruction in the block
        for (lineIndex in block.lineIndexes) {
            // Find definitions at this line
            val definitions = dataFlow.definitions.filter { it.lineRef.line == lineIndex }

            for (def in definitions) {
                val variable = def.variable

                // Check if this variable is live after this definition
                // A definition is dead if the variable is not in liveOut of this point
                // and no use exists between this def and the next def of same variable

                val isLiveAfter = isVariableLiveAfter(
                    variable = variable,
                    lineIndex = lineIndex,
                    blockLiveness = blockLiveness,
                    dataFlow = dataFlow,
                    block = block
                )

                if (!isLiveAfter) {
                    // Check for side effects before marking as dead
                    if (hasSideEffects(variable, lineIndex)) {
                        preservedForSideEffects.add(lineIndex)
                    } else {
                        deadStores.add(lineIndex)
                    }
                }
            }
        }
    }

    // 3. Find completely unused variables (if variable analysis available)
    if (functionVariables != null) {
        val allVars = functionVariables.localVariables + functionVariables.parameters + functionVariables.returnValues
        for (variable in allVars) {
            val usages = variable.usageSites
            val hasReads = usages.any { it.usageType == UsageType.READ || it.usageType == UsageType.READ_MODIFY_WRITE }

            if (!hasReads) {
                // Check if any writes have side effects
                val hasEffectfulWrites = usages.any { usage ->
                    hasSideEffects(variable.id, usage.lineRef.line)
                }

                if (!hasEffectfulWrites) {
                    deadVariables.add(variable.id)
                }
            }
        }
    }

    // 4. Find dead expressions (if expression analysis available)
    if (functionExpressions != null) {
        for ((blockLeader, blockExprs) in functionExpressions.blockExpressions) {
            blockExprs.expressions.forEachIndexed { index, exprTree ->
                if (isExpressionResultUnused(exprTree)) {
                    // Only mark as dead if no side effects
                    if (!expressionHasSideEffects(exprTree.root)) {
                        deadExpressions.add(ExpressionTreeId(blockLeader, index))
                    }
                }
            }
        }
    }

    val analysis = DeadCodeAnalysis(
        deadBlocks = deadBlocks,
        deadStores = deadStores,
        deadVariables = deadVariables,
        deadExpressions = deadExpressions,
        preservedForSideEffects = preservedForSideEffects
    )

    return FunctionDeadCodeElimination(
        function = this,
        analysis = analysis,
        removedInstructions = deadStores.size,
        removedBlocks = deadBlocks.size,
        removedVariables = deadVariables.size
    )
}

/**
 * Check if a variable is live after a specific line
 */
private fun isVariableLiveAfter(
    variable: Variable,
    lineIndex: Int,
    blockLiveness: DataFlowFacts,
    dataFlow: FunctionDataFlow,
    block: BasicBlock
): Boolean {
    // Check if variable is in liveOut of this block
    if (variable in blockLiveness.liveOut) {
        return true
    }

    // Check if there's a use of this variable later in the same block
    val laterLinesInBlock = block.lineIndexes.filter { it > lineIndex }
    for (laterLine in laterLinesInBlock) {
        val uses = dataFlow.uses.filter { it.lineRef.line == laterLine && it.variable == variable }
        if (uses.isNotEmpty()) {
            return true
        }
    }

    return false
}

/**
 * Check if a write to a variable has observable side effects
 */
private fun hasSideEffects(variable: Variable, lineIndex: Int): Boolean {
    return when (variable) {
        is Variable.Memory -> HardwareRegions.isHardwareAddress(variable.address)
        is Variable.ZeroPage -> HardwareRegions.isHardwareAddress(variable.address)
        else -> false  // Register writes have no external side effects
    }
}

/**
 * Check if a write to a variable ID has observable side effects
 */
private fun hasSideEffects(variableId: VariableId, lineIndex: Int): Boolean {
    return when (variableId) {
        is VariableId.Memory -> HardwareRegions.isHardwareAddress(variableId.address)
        is VariableId.MultiByteMemory -> HardwareRegions.isHardwareAddress(variableId.baseAddress)
        else -> false
    }
}

/**
 * Check if an expression result is unused
 */
private fun isExpressionResultUnused(exprTree: ExpressionTree): Boolean {
    // An expression is unused if it's not assigned to anything
    // or if it's assigned to a dead variable
    // For now, simple heuristic: check if it's a statement vs expression
    return false  // TODO: Implement based on expression tree structure
}

/**
 * Check if an expression has side effects
 */
private fun expressionHasSideEffects(expr: Expression): Boolean {
    return when (expr) {
        is Expression.FunctionCall -> true  // Function calls may have side effects
        is Expression.MemoryAccess -> {
            // Reading from hardware registers may have side effects (e.g., PPU status)
            when (val addr = expr.address) {
                is Expression.Literal -> HardwareRegions.isHardwareAddress(addr.value)
                else -> true  // Conservative: assume unknown addresses have effects
            }
        }
        is Expression.Assignment -> {
            // Assignment target might be hardware
            val target = expr.target
            when (target) {
                is Expression.MemoryAccess -> expressionHasSideEffects(target)
                else -> expressionHasSideEffects(expr.value)
            }
        }
        is Expression.BinaryOp -> {
            expressionHasSideEffects(expr.left) || expressionHasSideEffects(expr.right)
        }
        is Expression.UnaryOp -> expressionHasSideEffects(expr.operand)
        is Expression.ArrayAccess -> {
            // Array access might be to hardware region
            true  // Conservative
        }
        else -> false
    }
}

/**
 * Analyze all functions for dead code
 */
fun AssemblyCodeFile.analyzeDeadCode(
    dataFlowAnalysis: DataFlowAnalysis,
    variableIdentification: VariableIdentification? = null,
    expressionAnalysis: ExpressionTreeAnalysis? = null
): DeadCodeEliminationResult {

    val functionResults = dataFlowAnalysis.functions.mapIndexed { index, functionDataFlow ->
        val function = functionDataFlow.function
        val variables = variableIdentification?.functions?.getOrNull(index)
        val expressions = expressionAnalysis?.functions?.getOrNull(index)

        function.eliminateDeadCode(this, functionDataFlow, variables, expressions)
    }

    val globalStats = DeadCodeStats(
        totalDeadStores = functionResults.sumOf { it.analysis.deadStores.size },
        totalDeadVariables = functionResults.sumOf { it.analysis.deadVariables.size },
        totalDeadBlocks = functionResults.sumOf { it.analysis.deadBlocks.size },
        totalInstructionsRemoved = functionResults.sumOf { it.removedInstructions },
        preservedForSideEffects = functionResults.sumOf { it.analysis.preservedForSideEffects.size }
    )

    return DeadCodeEliminationResult(
        functionsOptimized = functionResults,
        globalStats = globalStats
    )
}
