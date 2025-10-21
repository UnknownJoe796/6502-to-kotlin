package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 14: Loop Detection
 * - Identify natural loops using dominance analysis
 * - Classify loop types (while, do-while, for-like, infinite)
 * - Find loop headers, bodies, exits, and back edges
 * - Support nested loops and loop hierarchies
 */

/**
 * Types of loops based on structure
 */
enum class LoopType {
    /** while (condition) { body } - condition tested before body */
    WHILE,
    /** do { body } while (condition) - condition tested after body */
    DO_WHILE,
    /** for-like loop with iterator pattern */
    FOR_LIKE,
    /** Loop with no obvious exit (infinite or break-based) */
    INFINITE,
    /** Loop with multiple exits */
    MULTI_EXIT
}

/**
 * A back edge that forms a loop
 */
data class BackEdge(
    /** Block that jumps back to header */
    val fromLeader: Int,
    /** Loop header block */
    val toLeader: Int,
    /** The CFG edge representing this back edge */
    val edge: CfgEdge
)

/**
 * Information about a detected natural loop
 */
data class NaturalLoop(
    /** Header block (entry point of loop) */
    val header: Int,
    /** All blocks in the loop body (including header) */
    val body: Set<Int>,
    /** Back edges that form this loop */
    val backEdges: List<BackEdge>,
    /** Exit blocks (in loop) that have edges leaving the loop */
    val exits: Set<Int>,
    /** Blocks outside loop that are targets of exit edges */
    val exitTargets: Set<Int>,
    /** Immediate parent loop (null if top-level) */
    val parentLoop: NaturalLoop?,
    /** Immediate child loops nested within this loop */
    val childLoops: MutableList<NaturalLoop> = mutableListOf(),
    /** Loop type classification */
    val loopType: LoopType,
    /** Nesting depth (0 for outermost loops) */
    val nestingDepth: Int
)

/**
 * Complete loop detection result for a function
 */
data class FunctionLoopInfo(
    /** The function being analyzed */
    val function: FunctionCfg,
    /** All natural loops in this function */
    val loops: List<NaturalLoop>,
    /** Map from header block to loop */
    val headerToLoop: Map<Int, NaturalLoop>,
    /** Map from any block to innermost loop containing it */
    val blockToInnermostLoop: Map<Int, NaturalLoop>,
    /** Top-level loops (no parent) */
    val topLevelLoops: List<NaturalLoop>
)

/**
 * Complete loop detection result
 */
data class LoopDetection(
    /** Loop information for each function */
    val functions: List<FunctionLoopInfo>
)

/**
 * Detect loops in all functions
 */
fun AssemblyCodeFile.detectLoops(
    resolution: AddressResolution = this.resolveAddresses(),
    entries: EntryPointDiscovery = this.discoverEntryPoints(resolution),
    reachability: ReachabilityReport = this.analyzeReachability(resolution, entries),
    blocks: BasicBlockConstruction = this.constructBasicBlocks(resolution, reachability, entries),
    cfg: CfgConstruction = this.constructCfg(resolution, reachability, blocks, entries),
    dominators: DominatorConstruction = this.constructDominatorTrees(cfg)
): LoopDetection {
    val functionLoopInfo = cfg.functions.mapIndexed { index, function ->
        val domAnalysis = dominators.functions[index]
        detectFunctionLoops(function, domAnalysis)
    }

    return LoopDetection(functions = functionLoopInfo)
}

/**
 * Detect loops in a single function
 */
private fun detectFunctionLoops(
    function: FunctionCfg,
    dominatorAnalysis: DominatorAnalysis
): FunctionLoopInfo {
    // Step 1: Find all back edges (edges where target dominates source)
    val backEdges = mutableListOf<BackEdge>()

    function.edges.forEach { edge ->
        val from = edge.fromLeader
        val to = edge.toLeader

        if (to != null && dominates(dominatorAnalysis, to, from)) {
            backEdges.add(BackEdge(from, to, edge))
        }
    }

    // Step 2: For each back edge, find the natural loop
    val loopsMap = mutableMapOf<Int, MutableSet<Int>>() // header -> body blocks
    val backEdgesByHeader = backEdges.groupBy { it.toLeader }

    backEdges.forEach { backEdge ->
        val header = backEdge.toLeader
        val tail = backEdge.fromLeader

        // Natural loop = header + all blocks that can reach tail without going through header
        val loopBody = findNaturalLoopBody(function, dominatorAnalysis, header, tail)

        loopsMap.getOrPut(header) { mutableSetOf() }.addAll(loopBody)
    }

    // Step 3: Create loop objects with hierarchy
    val loops = mutableListOf<NaturalLoop>()
    val headerToLoop = mutableMapOf<Int, NaturalLoop>()

    // Sort headers by body size (largest first) to build nesting properly
    val sortedHeaders = loopsMap.keys.sortedByDescending { loopsMap[it]?.size ?: 0 }

    sortedHeaders.forEach { header ->
        val body = loopsMap[header]!!
        val loopBackEdges = backEdgesByHeader[header] ?: emptyList()

        // Find exits and exit targets
        val exits = mutableSetOf<Int>()
        val exitTargets = mutableSetOf<Int>()

        body.forEach { blockInLoop ->
            function.edges.filter { it.fromLeader == blockInLoop }.forEach { edge ->
                val target = edge.toLeader
                if (target != null && target !in body) {
                    exits.add(blockInLoop)
                    exitTargets.add(target)
                }
            }
        }

        // Determine parent loop (smallest loop that contains this loop)
        var parentLoop: NaturalLoop? = null
        for (otherHeader in sortedHeaders) {
            if (otherHeader == header) continue
            val otherBody = loopsMap[otherHeader]!!
            if (header in otherBody && body.all { it in otherBody }) {
                // This loop is nested in otherLoop
                val otherLoop = headerToLoop[otherHeader]
                if (parentLoop == null || otherBody.size < (loopsMap[parentLoop.header]?.size ?: Int.MAX_VALUE)) {
                    parentLoop = otherLoop
                }
            }
        }

        val nestingDepth = (parentLoop?.nestingDepth ?: -1) + 1

        // Classify loop type
        val loopType = classifyLoop(function, header, body, exits, loopBackEdges)

        val loop = NaturalLoop(
            header = header,
            body = body,
            backEdges = loopBackEdges,
            exits = exits,
            exitTargets = exitTargets,
            parentLoop = parentLoop,
            loopType = loopType,
            nestingDepth = nestingDepth
        )

        loops.add(loop)
        headerToLoop[header] = loop

        // Add to parent's children
        parentLoop?.childLoops?.add(loop)
    }

    // Step 4: Build block to innermost loop map
    val blockToInnermostLoop = mutableMapOf<Int, NaturalLoop>()
    loops.forEach { loop ->
        loop.body.forEach { block ->
            // Only set if not already set (ensures innermost)
            if (block !in blockToInnermostLoop) {
                blockToInnermostLoop[block] = loop
            } else {
                // Replace if this loop is more nested
                val existing = blockToInnermostLoop[block]!!
                if (loop.nestingDepth > existing.nestingDepth) {
                    blockToInnermostLoop[block] = loop
                }
            }
        }
    }

    val topLevelLoops = loops.filter { it.parentLoop == null }

    return FunctionLoopInfo(
        function = function,
        loops = loops,
        headerToLoop = headerToLoop,
        blockToInnermostLoop = blockToInnermostLoop,
        topLevelLoops = topLevelLoops
    )
}

/**
 * Find the natural loop body for a back edge from tail to header
 */
private fun findNaturalLoopBody(
    function: FunctionCfg,
    dominatorAnalysis: DominatorAnalysis,
    header: Int,
    tail: Int
): Set<Int> {
    val body = mutableSetOf<Int>()
    body.add(header)

    if (header == tail) {
        return body // Self-loop
    }

    body.add(tail)

    // BFS backward from tail, adding predecessors until we hit header
    val queue = ArrayDeque<Int>()
    queue.add(tail)

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()

        // Find predecessors
        function.edges.filter { it.toLeader == current }.forEach { edge ->
            val pred = edge.fromLeader
            if (pred !in body) {
                body.add(pred)
                if (pred != header) {
                    queue.add(pred)
                }
            }
        }
    }

    return body
}

/**
 * Check if dominator dominates dominated
 */
private fun dominates(dominatorAnalysis: DominatorAnalysis, dominator: Int, dominated: Int): Boolean {
    val domNode = dominatorAnalysis.leaderToDomNode[dominated] ?: return false

    var current: DominatorNode? = domNode
    while (current != null) {
        if (current.leaderIndex == dominator) return true
        current = current.immediateDominator
    }

    return false
}

/**
 * Classify the loop type based on structure
 */
private fun classifyLoop(
    function: FunctionCfg,
    header: Int,
    body: Set<Int>,
    exits: Set<Int>,
    backEdges: List<BackEdge>
): LoopType {
    // Check for infinite loop (no exits)
    if (exits.isEmpty()) {
        return LoopType.INFINITE
    }

    // Check for multiple exits
    if (exits.size > 1) {
        return LoopType.MULTI_EXIT
    }

    // Single exit - determine while vs do-while
    val exitBlock = exits.first()

    // Check if this is a self-loop (header == exit block with back edge to itself)
    if (body.size == 1 && header == exitBlock && backEdges.any { it.fromLeader == header && it.toLeader == header }) {
        // Self-loop is do-while: body executes, then condition checked
        return LoopType.DO_WHILE
    }

    // If exit block is same as header and has conditional exit
    val headerEdges = function.edges.filter { it.fromLeader == header }
    val headerExitEdge = headerEdges.find { it.toLeader != null && it.toLeader !in body }

    if (exitBlock == header && headerExitEdge != null && (headerExitEdge.kind == CfgEdgeKind.TRUE || headerExitEdge.kind == CfgEdgeKind.FALSE)) {
        // Header has conditional exit - could be while or do-while depending on back edge source
        // If back edge comes from same block as exit, it's do-while
        if (backEdges.any { it.fromLeader == exitBlock }) {
            return LoopType.DO_WHILE
        }
        // Otherwise it's while
        return LoopType.WHILE
    }

    // If exit is not from header and back edge comes from exit block, it's do-while
    if (exitBlock != header && backEdges.any { it.fromLeader == exitBlock }) {
        return LoopType.DO_WHILE
    }

    // Default to while loop
    return LoopType.WHILE
}

/**
 * Helper to check if a block is in a loop
 */
fun FunctionLoopInfo.isInLoop(blockLeader: Int): Boolean {
    return blockLeader in blockToInnermostLoop
}

/**
 * Helper to get all loops containing a block (from innermost to outermost)
 */
fun FunctionLoopInfo.getLoopsContaining(blockLeader: Int): List<NaturalLoop> {
    val result = mutableListOf<NaturalLoop>()
    var current = blockToInnermostLoop[blockLeader]

    while (current != null) {
        result.add(current)
        current = current.parentLoop
    }

    return result
}
