package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 8: Function Boundary Detection
 * - Group basic blocks into functions
 * - Identify exits: returns (RTS/RTI/BRK) and tail calls (JMP to another function entry)
 * - Handle overlapping functions (blocks shared by multiple functions)
 * - Detect unreachable code within each function's address span (best-effort)
 */

enum class FunctionExitKind { RETURN, TAIL_CALL }

/**
 * A function exit either returns or performs a tail call to another function entry.
 * toFunctionLeader is the leader index of the callee's entry block for tail calls; null for returns.
 */
data class FunctionExit(
    val fromLeader: Int,
    val kind: FunctionExitKind,
    val toFunctionLeader: Int? = null,
)

/**
 * The result for one detected function, built from CFG while honoring tail calls as boundaries.
 */
data class DetectedFunction(
    val entryLeader: Int,
    val entryAddress: Int,
    val entryLabel: String?,
    val blocks: List<BasicBlock>,
    val exits: List<FunctionExit>,
    /**
     * Blocks that lie within this function's min..max address span but are not reachable
     * from the entry (likely dead/unreferenced within the function region).
     */
    val unreachableInside: List<BasicBlock>,
)

/**
 * Pass 8 overall result.
 * overlappedLeaders contains leader indexes that appear in more than one detected function.
 */
data class FunctionBoundaryDetection(
    val functions: List<DetectedFunction>,
    val overlappedLeaders: Set<Int>,
)

/**
 * Detect function boundaries using the CFG from Pass 7. Tail calls (JMP to another entry leader)
 * are treated as exits and do not traverse into the callee blocks.
 */
fun List<AssemblyLine>.detectFunctionBoundaries(
    resolution: AddressResolution = this.resolveAddresses(),
    entries: EntryPointDiscovery = this.discoverEntryPoints(resolution),
    reachability: ReachabilityReport = this.analyzeReachability(resolution, entries),
    blocks: BasicBlockConstruction = this.constructBasicBlocks(resolution, reachability),
    cfg: CfgConstruction = this.constructCfg(resolution, reachability, blocks, entries),
): FunctionBoundaryDetection {
    val program = cfg.program
    val leaderToBlock = program.leaderToBlock

    // Determine the set of entry leaders (candidate function starts)
    val functionEntries = cfg.functions.map { it.entryLeader }.toSet()

    // Helper maps
    val outgoing = program.outgoing

    val detected = mutableListOf<DetectedFunction>()
    val membership = mutableMapOf<Int, MutableSet<Int>>() // block leader -> set of function entry leaders that include it

    // Build each function by BFS, cutting at returns and tail calls
    cfg.functions.forEach { fn ->
        val entryLeader = fn.entryLeader
        val entryAddress = fn.entryAddress
        val entryLabel = fn.entryLabel

        val seen = linkedSetOf<Int>()
        val queue = ArrayDeque<Int>()
        val exits = mutableListOf<FunctionExit>()

        seen += entryLeader
        queue += entryLeader

        while (queue.isNotEmpty()) {
            val cur = queue.removeFirst()
            // Record membership
            membership.getOrPut(cur) { mutableSetOf() } += entryLeader

            // Traverse edges
            outgoing[cur].orEmpty().forEach { e ->
                when (e.kind) {
                    CfgEdgeKind.RETURN -> {
                        exits += FunctionExit(fromLeader = cur, kind = FunctionExitKind.RETURN)
                        // do not traverse
                    }
                    CfgEdgeKind.UNCONDITIONAL -> {
                        val to = e.toLeader
                        if (to != null && to != entryLeader && to in functionEntries) {
                            // Tail call to another function entry => boundary
                            exits += FunctionExit(fromLeader = cur, kind = FunctionExitKind.TAIL_CALL, toFunctionLeader = to)
                            // do not traverse into callee
                        } else if (to != null) {
                            if (seen.add(to)) queue += to
                        }
                    }
                    else -> {
                        val to = e.toLeader
                        if (to != null) {
                            if (seen.add(to)) queue += to
                        }
                    }
                }
            }
        }

        val blockList = seen.mapNotNull { leaderToBlock[it] }

        // Detect unreachable blocks inside this function's address range (best-effort, address-based)
        val minAddr = blockList.minOfOrNull { it.startAddress }
        val maxAddr = blockList.maxOfOrNull { it.endAddress }
        val unreachableInside = if (minAddr != null && maxAddr != null) {
            program.blocks
                .asSequence()
                .filter { it.startAddress in minAddr..maxAddr }
                .filter { it.leaderIndex !in seen }
                .toList()
        } else emptyList()

        detected += DetectedFunction(
            entryLeader = entryLeader,
            entryAddress = entryAddress,
            entryLabel = entryLabel,
            blocks = blockList,
            exits = exits,
            unreachableInside = unreachableInside,
        )
    }

    // Compute overlaps: any leader appearing in more than one function
    val overlapped = membership.filterValues { it.size > 1 }.keys

    return FunctionBoundaryDetection(
        functions = detected,
        overlappedLeaders = overlapped,
    )
}
