package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 7: Control Flow Graph (CFG) Construction
 * - Link basic blocks with edges:
 *   - Conditional branches (TRUE/FALSE)
 *   - Unconditional jumps
 *   - Fall-through edges
 *   - Return edges (to function exit sentinel)
 * - Build per-function CFGs rooted at discovered entry points
 */

enum class CfgEdgeKind {
    TRUE,
    FALSE,
    UNCONDITIONAL,
    FALL_THROUGH,
    RETURN,
}

data class CfgEdge(
    /** Leader index of source block */
    val fromLeader: Int,
    /** Leader index of destination block; null means function exit sentinel */
    val toLeader: Int?,
    val kind: CfgEdgeKind,
)

/** Program-level CFG over all reachable basic blocks */
data class ControlFlowGraph(
    val blocks: List<BasicBlock>,
    val edges: List<CfgEdge>,
) {
    val leaderToBlock: Map<Int, BasicBlock> = blocks.associateBy { it.leaderIndex }
    val outgoing: Map<Int, List<CfgEdge>> = edges.groupBy { it.fromLeader }
}

/** Per-function CFG view, induced from the program CFG starting at an entry block. */
data class FunctionCfg(
    val entryLeader: Int,
    val entryAddress: Int,
    val entryLabel: String?,
    val blocks: List<BasicBlock>,
    val edges: List<CfgEdge>,
)

/** Container for the result of CFG construction. */
data class CfgConstruction(
    val program: ControlFlowGraph,
    val functions: List<FunctionCfg>,
)

private fun basicBlockTerminatorOp(codeFile: AssemblyCodeFile, bb: BasicBlock): AssemblyOp? {
    val endInstr = codeFile.lines[bb.endIndex].instruction ?: return null
    return endInstr.op
}

/**
 * Build a program-level CFG and per-entry (function) CFGs.
 * By design, this pass does NOT create inter-procedural edges for JSR; JSR keeps only a FALL_THROUGH edge.
 */
fun AssemblyCodeFile.constructCfg(
    resolution: AddressResolution = this.resolveAddresses(),
    reachability: ReachabilityReport = this.analyzeReachability(resolution, this.discoverEntryPoints(resolution)),
    blocks: BasicBlockConstruction = this.constructBasicBlocks(resolution, reachability),
    entries: EntryPointDiscovery = this.discoverEntryPoints(resolution),
): CfgConstruction {
    // Program-level edges from BasicBlock relationships
    val edges = mutableListOf<CfgEdge>()

    val leaderSet = blocks.leaderIndexes
    val leaderToBlock = blocks.blocks.associateBy { it.leaderIndex }

    fun addEdge(from: Int, to: Int?, kind: CfgEdgeKind) {
        edges += CfgEdge(fromLeader = from, toLeader = to, kind = kind)
    }

    blocks.blocks.forEach { bb ->
        val op = basicBlockTerminatorOp(this, bb)
        when {
            // Conditional branch: TRUE to branch target, FALSE to fall-through
            op?.isBranch == true -> {
                // There should be at most one target for 6502 branches; add all just in case
                bb.targetLeaders.forEach { tgt -> addEdge(bb.leaderIndex, tgt, CfgEdgeKind.TRUE) }
                bb.fallThroughLeader?.let { addEdge(bb.leaderIndex, it, CfgEdgeKind.FALSE) }
            }
            // Unconditional jump: UNCONDITIONAL to target, no fall-through
            op == AssemblyOp.JMP -> {
                bb.targetLeaders.forEach { tgt -> addEdge(bb.leaderIndex, tgt, CfgEdgeKind.UNCONDITIONAL) }
            }
            // Returns/interrupt return/break: RETURN edge to exit sentinel (null)
            op == AssemblyOp.RTS || op == AssemblyOp.RTI || op == AssemblyOp.BRK -> {
                addEdge(bb.leaderIndex, null, CfgEdgeKind.RETURN)
            }
            else -> {
                // JSR and all other non-terminators: FALL_THROUGH if present
                bb.fallThroughLeader?.let { addEdge(bb.leaderIndex, it, CfgEdgeKind.FALL_THROUGH) }
            }
        }
    }

    val program = ControlFlowGraph(blocks = blocks.blocks, edges = edges)

    // Per-function CFGs: seed from entry points, map to leader, then BFS to induce subgraph.
    val addrToLeader = blocks.blocks.associate { it.startAddress to it.leaderIndex }

    // Filter entry points to those we can map to blocks
    val entryDescriptors = entries.entryPoints
        .filter { it.kind == EntryPointKind.JSR_TARGET || it.kind == EntryPointKind.EXPORTED || it.kind.name.startsWith("INTERRUPT") }
        .mapNotNull { ep ->
            val addr = ep.address ?: (ep.label?.let { resolution.labelToAddress[it] })
            val leader = addr?.let { addrToLeader[it] }
            if (leader != null && leader in leaderSet) Triple(ep, addr!!, leader) else null
        }
        .distinctBy { it.third } // dedupe by leader
        .sortedBy { it.second }

    val functions = mutableListOf<FunctionCfg>()
    val edgeByFrom = program.outgoing

    entryDescriptors.forEach { (ep, addr, leader) ->
        // BFS over edges, ignoring RETURN edges (toLeader == null)
        // Also detect recursive calls: edges that branch back to the function entry
        val seen = linkedSetOf<Int>()
        val queue = ArrayDeque<Int>()
        seen += leader
        queue += leader
        val collectedEdges = mutableListOf<CfgEdge>()
        while (queue.isNotEmpty()) {
            val cur = queue.removeFirst()
            edgeByFrom[cur].orEmpty().forEach { e ->
                if (e.toLeader != null) {
                    // Check if this edge branches back to the function entry (recursive call)
                    // If so, treat it like a RETURN edge - include it but don't traverse
                    if (e.toLeader == leader && cur != leader) {
                        // This is a recursive call - convert to a RETURN-like edge
                        collectedEdges += CfgEdge(e.fromLeader, null, CfgEdgeKind.RETURN)
                    } else {
                        collectedEdges += e
                        if (seen.add(e.toLeader)) queue += e.toLeader
                    }
                } else {
                    // RETURN edge, include it but do not traverse
                    collectedEdges += e
                }
            }
        }
        val fnBlocks = seen.mapNotNull { leaderToBlock[it] }
        functions += FunctionCfg(
            entryLeader = leader,
            entryAddress = addr,
            entryLabel = ep.label,
            blocks = fnBlocks,
            edges = collectedEdges,
        )
    }

    return CfgConstruction(program = program, functions = functions)
}
