package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 15: Conditional Detection
 * - Identify if-then-else patterns in the CFG
 * - Detect short-circuit boolean expressions (&&, ||)
 * - Find switch/case-like patterns
 * - Classify conditional structures
 */

/**
 * Types of conditional structures
 */
enum class ConditionalType {
    /** Simple if (no else) */
    IF,
    /** if-else with both branches */
    IF_ELSE,
    /** if-else-if chain */
    IF_ELSE_IF,
    /** Short-circuit AND (&&) */
    SHORT_CIRCUIT_AND,
    /** Short-circuit OR (||) */
    SHORT_CIRCUIT_OR,
    /** Switch/case-like pattern */
    SWITCH
}

/**
 * Information about a detected conditional structure
 */
data class Conditional(
    /** Header block containing the condition */
    val header: Int,
    /** Type of conditional */
    val type: ConditionalType,
    /** "Then" branch blocks */
    val thenBranch: Set<Int>,
    /** "Else" branch blocks (empty for simple if) */
    val elseBranch: Set<Int>,
    /** Merge point where branches rejoin (null if no merge) */
    val mergePoint: Int?,
    /** Edge from header to then branch */
    val thenEdge: CfgEdge,
    /** Edge from header to else branch (or fallthrough) */
    val elseEdge: CfgEdge?,
    /** Nesting depth */
    val nestingDepth: Int,
    /** Parent conditional (for nested if statements) */
    val parentConditional: Conditional? = null
)

/**
 * Complete conditional detection result for a function
 */
data class FunctionConditionalInfo(
    /** The function being analyzed */
    val function: FunctionCfg,
    /** All conditionals in this function */
    val conditionals: List<Conditional>,
    /** Map from header block to conditional */
    val headerToConditional: Map<Int, Conditional>,
    /** Top-level conditionals (not nested in others) */
    val topLevelConditionals: List<Conditional>
)

/**
 * Complete conditional detection result
 */
data class ConditionalDetection(
    /** Conditional information for each function */
    val functions: List<FunctionConditionalInfo>
)

/**
 * Detect conditionals in all functions
 */
fun AssemblyCodeFile.detectConditionals(
    resolution: AddressResolution = this.resolveAddresses(),
    entries: EntryPointDiscovery = this.discoverEntryPoints(resolution),
    reachability: ReachabilityReport = this.analyzeReachability(resolution, entries),
    blocks: BasicBlockConstruction = this.constructBasicBlocks(resolution, reachability, entries),
    cfg: CfgConstruction = this.constructCfg(resolution, reachability, blocks, entries),
    dominators: DominatorConstruction = this.constructDominatorTrees(cfg),
    loops: LoopDetection = this.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
): ConditionalDetection {
    val functionConditionalInfo = cfg.functions.mapIndexed { index, function ->
        val domAnalysis = dominators.functions[index]
        val loopInfo = loops.functions[index]
        detectFunctionConditionals(function, domAnalysis, loopInfo)
    }

    return ConditionalDetection(functions = functionConditionalInfo)
}

/**
 * Detect conditionals in a single function
 */
private fun detectFunctionConditionals(
    function: FunctionCfg,
    dominatorAnalysis: DominatorAnalysis,
    loopInfo: FunctionLoopInfo
): FunctionConditionalInfo {
    val conditionals = mutableListOf<Conditional>()
    val headerToConditional = mutableMapOf<Int, Conditional>()

    // Find blocks with conditional branches (not loop back edges)
    for (block in function.blocks) {
        val leader = block.leaderIndex

        // Get outgoing edges
        val outgoing = function.edges.filter { it.fromLeader == leader }

        // Look for conditional branches (TRUE/FALSE edges)
        val trueEdge = outgoing.find { it.kind == CfgEdgeKind.TRUE }
        val falseEdge = outgoing.find { it.kind == CfgEdgeKind.FALSE }

        if (trueEdge != null && falseEdge != null) {
            // This is a conditional branch
            val trueDest = trueEdge.toLeader
            val falseDest = falseEdge.toLeader

            if (trueDest == null || falseDest == null) continue

            // Skip if this is a loop back edge
            val isLoopBackEdge = loopInfo.loops.any { loop ->
                (trueEdge.fromLeader to trueEdge.toLeader) in loop.backEdges.map { it.fromLeader to it.toLeader } ||
                (falseEdge.fromLeader to falseEdge.toLeader) in loop.backEdges.map { it.fromLeader to it.toLeader }
            }

            if (isLoopBackEdge) continue

            // Analyze the conditional structure
            val conditional = analyzeConditional(
                function,
                dominatorAnalysis,
                loopInfo,
                leader,
                trueEdge,
                falseEdge
            )

            if (conditional != null) {
                conditionals.add(conditional)
                headerToConditional[leader] = conditional
            }
        }
    }

    // Build nesting hierarchy
    conditionals.forEach { cond ->
        // Find parent (smallest conditional that contains this one's header)
        val parent = conditionals
            .filter { it != cond }
            .filter { cond.header in it.thenBranch || cond.header in it.elseBranch }
            .minByOrNull { it.thenBranch.size + it.elseBranch.size }

        if (parent != null) {
            val updatedCond = cond.copy(
                parentConditional = parent,
                nestingDepth = parent.nestingDepth + 1
            )
            val index = conditionals.indexOf(cond)
            conditionals[index] = updatedCond
            headerToConditional[cond.header] = updatedCond
        }
    }

    val topLevelConditionals = conditionals.filter { it.parentConditional == null }

    return FunctionConditionalInfo(
        function = function,
        conditionals = conditionals,
        headerToConditional = headerToConditional,
        topLevelConditionals = topLevelConditionals
    )
}

/**
 * Analyze a specific conditional structure
 */
private fun analyzeConditional(
    function: FunctionCfg,
    dominatorAnalysis: DominatorAnalysis,
    loopInfo: FunctionLoopInfo,
    header: Int,
    trueEdge: CfgEdge,
    falseEdge: CfgEdge
): Conditional? {
    val trueDest = trueEdge.toLeader!!
    val falseDest = falseEdge.toLeader!!

    // Find the immediate post-dominator of header (merge point)
    val mergePoint = findImmediatePostDominator(function, dominatorAnalysis, header)

    // Compute then and else branches
    val thenBranch = computeBranchRegion(function, loopInfo, header, trueDest, mergePoint)
    val elseBranch = computeBranchRegion(function, loopInfo, header, falseDest, mergePoint)

    // Classify the conditional type
    val type = classifyConditional(function, header, thenBranch, elseBranch, mergePoint)

    return Conditional(
        header = header,
        type = type,
        thenBranch = thenBranch,
        elseBranch = elseBranch,
        mergePoint = mergePoint,
        thenEdge = trueEdge,
        elseEdge = falseEdge,
        nestingDepth = 0  // Will be updated when building hierarchy
    )
}

/**
 * Compute all blocks in a branch region
 */
private fun computeBranchRegion(
    function: FunctionCfg,
    loopInfo: FunctionLoopInfo,
    header: Int,
    branchStart: Int,
    mergePoint: Int?
): Set<Int> {
    val region = mutableSetOf<Int>()
    val queue = ArrayDeque<Int>()
    queue.add(branchStart)
    region.add(branchStart)

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()

        // Don't traverse past the merge point
        if (current != mergePoint) {
            // Get successors
            function.edges.filter { it.fromLeader == current }.forEach { edge ->
                val succ = edge.toLeader
                if (succ != null && succ != header && succ !in region) {
                    // Don't include blocks that are in a different branch or after merge
                    if (mergePoint == null || isOnPathTo(function, succ, mergePoint, visited = region)) {
                        region.add(succ)
                        queue.add(succ)
                    }
                }
            }
        }
    }

    return region
}

/**
 * Check if there's a path from block to target
 */
private fun isOnPathTo(
    function: FunctionCfg,
    from: Int,
    to: Int,
    visited: Set<Int> = emptySet()
): Boolean {
    if (from == to) return true

    val localVisited = visited.toMutableSet()
    val queue = ArrayDeque<Int>()
    queue.add(from)
    localVisited.add(from)

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        if (current == to) return true

        function.edges.filter { it.fromLeader == current }.forEach { edge ->
            val succ = edge.toLeader
            if (succ != null && succ !in localVisited) {
                localVisited.add(succ)
                queue.add(succ)
            }
        }
    }

    return false
}

/**
 * Find immediate post-dominator (simplified - just find common successor)
 */
private fun findImmediatePostDominator(
    function: FunctionCfg,
    dominatorAnalysis: DominatorAnalysis,
    block: Int
): Int? {
    // Find blocks reachable from both branches
    val outgoing = function.edges.filter { it.fromLeader == block }
    if (outgoing.size != 2) return null

    val branch1 = outgoing[0].toLeader ?: return null
    val branch2 = outgoing[1].toLeader ?: return null

    // Find first common successor
    val reachableFrom1 = findReachable(function, branch1)
    val reachableFrom2 = findReachable(function, branch2)

    val common = reachableFrom1.intersect(reachableFrom2)

    // Return the closest common successor
    return common.minByOrNull { block ->
        // Simplified distance metric
        reachableFrom1.indexOf(block).let { if (it >= 0) it else Int.MAX_VALUE }
    }
}

/**
 * Find all blocks reachable from start (BFS order)
 */
private fun findReachable(function: FunctionCfg, start: Int): List<Int> {
    val result = mutableListOf<Int>()
    val visited = mutableSetOf<Int>()
    val queue = ArrayDeque<Int>()

    queue.add(start)
    visited.add(start)

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        result.add(current)

        function.edges.filter { it.fromLeader == current }.forEach { edge ->
            val succ = edge.toLeader
            if (succ != null && succ !in visited) {
                visited.add(succ)
                queue.add(succ)
            }
        }
    }

    return result
}

/**
 * Classify conditional type
 */
private fun classifyConditional(
    function: FunctionCfg,
    header: Int,
    thenBranch: Set<Int>,
    elseBranch: Set<Int>,
    mergePoint: Int?
): ConditionalType {
    // Check for simple if (else branch is just merge point)
    if (elseBranch.isEmpty() || (elseBranch.size == 1 && mergePoint in elseBranch)) {
        return ConditionalType.IF
    }

    // Check for if-else
    if (thenBranch.isNotEmpty() && elseBranch.isNotEmpty()) {
        // Check if else branch contains another conditional (if-else-if)
        val elseBranchHasConditional = elseBranch.any { block ->
            function.edges.filter { it.fromLeader == block }
                .count { it.kind == CfgEdgeKind.TRUE || it.kind == CfgEdgeKind.FALSE } >= 2
        }

        if (elseBranchHasConditional) {
            return ConditionalType.IF_ELSE_IF
        }

        return ConditionalType.IF_ELSE
    }

    return ConditionalType.IF
}
