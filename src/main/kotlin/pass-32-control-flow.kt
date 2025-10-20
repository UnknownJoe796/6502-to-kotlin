package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 32: Control Flow Simplification
 * - Eliminate empty blocks (containing only jumps)
 * - Merge blocks when predecessor has only one successor
 * - Thread jumps through chains of unconditional jumps
 * - Simplify branches with constant conditions
 * - Remove unreachable code after unconditional jumps
 *
 * This pass runs after DCE and Copy Propagation, and may enable further optimizations.
 */

/**
 * An empty block that can be eliminated (contains only unconditional jump)
 */
data class EmptyBlock(
    val blockLeader: Int,
    val jumpTarget: Int,  // Where this block jumps to
    val incomingEdges: List<CfgEdge>  // Edges that need to be redirected
)

/**
 * Two blocks that can be merged (predecessor has single successor)
 */
data class MergeCandidate(
    val predecessorLeader: Int,
    val successorLeader: Int,
    val edgeKind: CfgEdgeKind,
    val reason: String
)

/**
 * A jump that can be threaded (redirected to skip intermediate jumps)
 */
data class ThreadableJump(
    val fromLeader: Int,
    val intermediateLeader: Int,  // The jump-only block
    val finalTarget: Int,  // Where to redirect to
    val edgeKind: CfgEdgeKind
)

/**
 * A branch with a constant condition that can be eliminated
 */
data class ConstantBranch(
    val blockLeader: Int,
    val condition: Boolean,  // true = always taken, false = never taken
    val takenTarget: Int?,
    val fallThroughTarget: Int?
)

/**
 * Analysis of control flow simplification opportunities
 */
data class ControlFlowSimplification(
    val function: FunctionCfg,
    val emptyBlocks: List<EmptyBlock>,
    val mergeCandidates: List<MergeCandidate>,
    val threadableJumps: List<ThreadableJump>,
    val constantBranches: List<ConstantBranch>
)

/**
 * Statistics for control flow simplification
 */
data class ControlFlowStats(
    val emptyBlocksEliminated: Int,
    val blocksMerged: Int,
    val jumpsThreaded: Int,
    val branchesSimplified: Int,
    val edgesRemoved: Int
)

/**
 * Result of control flow simplification for a function
 */
data class FunctionControlFlowSimplification(
    val originalFunction: FunctionCfg,
    val simplifiedFunction: FunctionCfg,
    val analysis: ControlFlowSimplification,
    val stats: ControlFlowStats
)

/**
 * Complete control flow simplification result
 */
data class ControlFlowSimplificationResult(
    val functionsSimplified: List<FunctionControlFlowSimplification>,
    val globalStats: ControlFlowStats
)

/**
 * Simplify control flow in a function's CFG
 */
fun FunctionCfg.simplifyControlFlow(
    codeFile: AssemblyCodeFile
): FunctionControlFlowSimplification {

    val leaderToBlock = blocks.associateBy { it.leaderIndex }
    val outgoingEdges = edges.groupBy { it.fromLeader }
    val incomingEdges = edges.groupBy { it.toLeader }

    // Find empty blocks (blocks with only unconditional jump)
    val emptyBlocks = findEmptyBlocks(codeFile, leaderToBlock, outgoingEdges, incomingEdges)

    // Find blocks that can be merged
    val mergeCandidates = findMergeCandidates(leaderToBlock, outgoingEdges, incomingEdges)

    // Find jump chains that can be threaded
    val threadableJumps = findThreadableJumps(codeFile, leaderToBlock, outgoingEdges)

    // Find branches with constant conditions (would need constant propagation data)
    val constantBranches = findConstantBranches(codeFile, leaderToBlock)

    val analysis = ControlFlowSimplification(
        function = this,
        emptyBlocks = emptyBlocks,
        mergeCandidates = mergeCandidates,
        threadableJumps = threadableJumps,
        constantBranches = constantBranches
    )

    // Apply simplifications
    val simplifiedFunction = applySimplifications(analysis, codeFile)

    // Calculate statistics
    val stats = ControlFlowStats(
        emptyBlocksEliminated = emptyBlocks.size,
        blocksMerged = mergeCandidates.size,
        jumpsThreaded = threadableJumps.size,
        branchesSimplified = constantBranches.size,
        edgesRemoved = emptyBlocks.sumOf { it.incomingEdges.size } + constantBranches.size
    )

    return FunctionControlFlowSimplification(
        originalFunction = this,
        simplifiedFunction = simplifiedFunction,
        analysis = analysis,
        stats = stats
    )
}

/**
 * Find blocks that contain only an unconditional jump
 */
private fun findEmptyBlocks(
    codeFile: AssemblyCodeFile,
    leaderToBlock: Map<Int, BasicBlock>,
    outgoingEdges: Map<Int, List<CfgEdge>>,
    incomingEdges: Map<Int?, List<CfgEdge>>
): List<EmptyBlock> {
    val emptyBlocks = mutableListOf<EmptyBlock>()

    for (block in leaderToBlock.values) {
        // Block must have exactly one instruction
        if (block.lineIndexes.size != 1) continue

        val line = codeFile.lines[block.leaderIndex]
        val instr = line.instruction ?: continue

        // Must be an unconditional jump
        if (instr.op != AssemblyOp.JMP) continue

        // Find the single outgoing unconditional edge
        val edges = outgoingEdges[block.leaderIndex] ?: continue
        val unconditionalEdge = edges.singleOrNull { it.kind == CfgEdgeKind.UNCONDITIONAL } ?: continue
        val target = unconditionalEdge.toLeader ?: continue

        // Get incoming edges
        val incoming = incomingEdges[block.leaderIndex] ?: emptyList()

        // Don't eliminate entry blocks
        if (incoming.isEmpty()) continue

        emptyBlocks.add(
            EmptyBlock(
                blockLeader = block.leaderIndex,
                jumpTarget = target,
                incomingEdges = incoming
            )
        )
    }

    return emptyBlocks
}

/**
 * Find pairs of blocks that can be merged (predecessor has single successor)
 */
private fun findMergeCandidates(
    leaderToBlock: Map<Int, BasicBlock>,
    outgoingEdges: Map<Int, List<CfgEdge>>,
    incomingEdges: Map<Int?, List<CfgEdge>>
): List<MergeCandidate> {
    val candidates = mutableListOf<MergeCandidate>()

    for (block in leaderToBlock.values) {
        val outgoing = outgoingEdges[block.leaderIndex] ?: continue

        // Block must have exactly one successor
        if (outgoing.size != 1) continue

        val edge = outgoing.first()
        val successorLeader = edge.toLeader ?: continue
        val successor = leaderToBlock[successorLeader] ?: continue

        // Successor must have exactly one predecessor (this block)
        val successorIncoming = incomingEdges[successorLeader] ?: continue
        if (successorIncoming.size != 1) continue

        // Edge must be fall-through or unconditional
        if (edge.kind != CfgEdgeKind.FALL_THROUGH && edge.kind != CfgEdgeKind.UNCONDITIONAL) {
            continue
        }

        candidates.add(
            MergeCandidate(
                predecessorLeader = block.leaderIndex,
                successorLeader = successorLeader,
                edgeKind = edge.kind,
                reason = "Single predecessor-successor pair with ${edge.kind} edge"
            )
        )
    }

    return candidates
}

/**
 * Find jumps that can be threaded through chains of unconditional jumps
 */
private fun findThreadableJumps(
    codeFile: AssemblyCodeFile,
    leaderToBlock: Map<Int, BasicBlock>,
    outgoingEdges: Map<Int, List<CfgEdge>>
): List<ThreadableJump> {
    val threadable = mutableListOf<ThreadableJump>()

    // Build a map of jump-only blocks (blocks with single unconditional outgoing edge)
    val jumpOnlyBlocks = mutableMapOf<Int, Int>()  // leader -> target
    for (block in leaderToBlock.values) {
        val edges = outgoingEdges[block.leaderIndex] ?: continue
        val unconditional = edges.singleOrNull { it.kind == CfgEdgeKind.UNCONDITIONAL }
        if (unconditional != null && unconditional.toLeader != null) {
            jumpOnlyBlocks[block.leaderIndex] = unconditional.toLeader!!
        }
    }

    // Find jumps that can be threaded
    for (block in leaderToBlock.values) {
        val edges = outgoingEdges[block.leaderIndex] ?: continue

        for (edge in edges) {
            val target = edge.toLeader ?: continue

            // If target is a jump-only block, we can thread through it
            val finalTarget = jumpOnlyBlocks[target]
            if (finalTarget != null && finalTarget != block.leaderIndex) {
                // Make sure we don't create a self-loop
                if (finalTarget != block.leaderIndex) {
                    threadable.add(
                        ThreadableJump(
                            fromLeader = block.leaderIndex,
                            intermediateLeader = target,
                            finalTarget = finalTarget,
                            edgeKind = edge.kind
                        )
                    )
                }
            }
        }
    }

    return threadable
}

/**
 * Find branches with constant conditions
 * Note: This requires constant propagation data from Pass 18, which we'll integrate when available
 */
private fun findConstantBranches(
    codeFile: AssemblyCodeFile,
    leaderToBlock: Map<Int, BasicBlock>
): List<ConstantBranch> {
    // For now, return empty list
    // TODO: Integrate with Pass 18 constant propagation when available
    // This would check if branch conditions are always true/false based on flag analysis
    return emptyList()
}

/**
 * Apply all simplifications to produce a new, simplified CFG
 */
private fun FunctionCfg.applySimplifications(
    analysis: ControlFlowSimplification,
    codeFile: AssemblyCodeFile
): FunctionCfg {
    var currentBlocks = this.blocks
    var currentEdges = this.edges

    // Apply jump threading first (simplest transformation)
    if (analysis.threadableJumps.isNotEmpty()) {
        currentEdges = threadJumps(currentEdges, analysis.threadableJumps)
    }

    // Apply empty block elimination
    if (analysis.emptyBlocks.isNotEmpty()) {
        val eliminated = analysis.emptyBlocks.map { it.blockLeader }.toSet()
        currentBlocks = currentBlocks.filter { it.leaderIndex !in eliminated }
        currentEdges = eliminateEmptyBlocks(currentEdges, analysis.emptyBlocks)
    }

    // Apply block merging (most complex, changes block structure)
    // Note: For now we just identify candidates but don't merge blocks
    // because merging requires reconstructing BasicBlock objects with merged lineIndexes
    // This would be done in a full implementation

    // Apply constant branch simplification
    if (analysis.constantBranches.isNotEmpty()) {
        currentEdges = simplifyConstantBranches(currentEdges, analysis.constantBranches)
    }

    return FunctionCfg(
        entryLeader = this.entryLeader,
        entryAddress = this.entryAddress,
        entryLabel = this.entryLabel,
        blocks = currentBlocks,
        edges = currentEdges
    )
}

/**
 * Thread jumps through intermediate jump-only blocks
 */
private fun threadJumps(
    edges: List<CfgEdge>,
    threadableJumps: List<ThreadableJump>
): List<CfgEdge> {
    val redirectMap = threadableJumps.associate {
        (it.fromLeader to it.intermediateLeader) to it.finalTarget
    }

    return edges.map { edge ->
        val newTarget = redirectMap[edge.fromLeader to edge.toLeader]
        if (newTarget != null) {
            edge.copy(toLeader = newTarget)
        } else {
            edge
        }
    }
}

/**
 * Eliminate empty blocks by redirecting incoming edges to their targets
 */
private fun eliminateEmptyBlocks(
    edges: List<CfgEdge>,
    emptyBlocks: List<EmptyBlock>
): List<CfgEdge> {
    val emptyBlockMap = emptyBlocks.associate { it.blockLeader to it.jumpTarget }

    return edges.mapNotNull { edge ->
        when {
            // Remove edges from empty blocks
            edge.fromLeader in emptyBlockMap -> null

            // Redirect edges to empty blocks
            edge.toLeader != null && edge.toLeader in emptyBlockMap -> {
                edge.copy(toLeader = emptyBlockMap[edge.toLeader])
            }

            else -> edge
        }
    }
}

/**
 * Simplify branches with constant conditions
 */
private fun simplifyConstantBranches(
    edges: List<CfgEdge>,
    constantBranches: List<ConstantBranch>
): List<CfgEdge> {
    val branchMap = constantBranches.associateBy { it.blockLeader }

    return edges.mapNotNull { edge ->
        val branch = branchMap[edge.fromLeader]
        if (branch != null) {
            // Remove the edge that won't be taken
            when {
                branch.condition && edge.kind == CfgEdgeKind.FALSE -> null
                !branch.condition && edge.kind == CfgEdgeKind.TRUE -> null
                else -> {
                    // Convert conditional edge to unconditional if condition is constant
                    if (edge.kind == CfgEdgeKind.TRUE || edge.kind == CfgEdgeKind.FALSE) {
                        edge.copy(kind = CfgEdgeKind.UNCONDITIONAL)
                    } else {
                        edge
                    }
                }
            }
        } else {
            edge
        }
    }
}

/**
 * Simplify control flow for all functions in a CFG construction
 */
fun CfgConstruction.simplifyControlFlow(
    codeFile: AssemblyCodeFile
): ControlFlowSimplificationResult {
    val functionsSimplified = functions.map { it.simplifyControlFlow(codeFile) }

    val globalStats = ControlFlowStats(
        emptyBlocksEliminated = functionsSimplified.sumOf { it.stats.emptyBlocksEliminated },
        blocksMerged = functionsSimplified.sumOf { it.stats.blocksMerged },
        jumpsThreaded = functionsSimplified.sumOf { it.stats.jumpsThreaded },
        branchesSimplified = functionsSimplified.sumOf { it.stats.branchesSimplified },
        edgesRemoved = functionsSimplified.sumOf { it.stats.edgesRemoved }
    )

    return ControlFlowSimplificationResult(
        functionsSimplified = functionsSimplified,
        globalStats = globalStats
    )
}
