package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 13: Call Graph Construction
 * - Build a graph of function calls (JSR instructions)
 * - Track which functions call which other functions
 * - Identify call sites and their target functions
 * - Support inter-procedural analysis
 * - Detect recursive calls and call cycles
 */

/**
 * Represents a single call site (JSR instruction)
 */
data class CallSite(
    /** The function containing this call */
    val callerEntryLeader: Int,
    /** The basic block containing the JSR */
    val callBlockLeader: Int,
    /** Line reference to the JSR instruction */
    val callLineRef: AssemblyLineReference,
    /** Target function entry leader (null if indirect or unresolved) */
    val calleeEntryLeader: Int?,
    /** Target address of the call */
    val targetAddress: Int?
)

/**
 * Call graph edge from one function to another
 */
data class CallEdge(
    /** Calling function's entry leader */
    val caller: Int,
    /** Called function's entry leader (null if indirect/unresolved) */
    val callee: Int?,
    /** All call sites for this edge */
    val callSites: List<CallSite>
)

/**
 * Call graph information for a single function
 */
data class FunctionCallInfo(
    /** The function */
    val function: DetectedFunction,
    /** Functions this function calls (outgoing edges) */
    val callees: Set<Int>,
    /** Functions that call this function (incoming edges) */
    val callers: Set<Int>,
    /** All call sites within this function */
    val callSites: List<CallSite>,
    /** Whether this function is recursive (calls itself directly) */
    val isDirectlyRecursive: Boolean
)

/**
 * Complete call graph analysis result
 */
data class CallGraphConstruction(
    /** Call information for each function */
    val functionCallInfo: Map<Int, FunctionCallInfo>,
    /** All call edges in the program */
    val callEdges: List<CallEdge>,
    /** Strongly connected components (cycles in the call graph) */
    val callCycles: List<Set<Int>>,
    /** Functions that are never called (entry points and unused) */
    val neverCalled: Set<Int>,
    /** Functions with indirect calls (JSR through register/memory) */
    val hasIndirectCalls: Boolean
)

/**
 * Construct the call graph from detected functions
 */
fun AssemblyCodeFile.constructCallGraph(
    resolution: AddressResolution = this.resolveAddresses(),
    entries: EntryPointDiscovery = this.discoverEntryPoints(resolution),
    reachability: ReachabilityReport = this.analyzeReachability(resolution, entries),
    blocks: BasicBlockConstruction = this.constructBasicBlocks(resolution, reachability, entries),
    cfg: CfgConstruction = this.constructCfg(resolution, reachability, blocks, entries),
    functionBoundaries: FunctionBoundaryDetection = this.detectFunctionBoundaries(resolution, entries, reachability, blocks, cfg)
): CallGraphConstruction {
    val codeFile = this

    // Build map from entry leader to function
    val leaderToFunction = functionBoundaries.functions.associateBy { it.entryLeader }

    // Build map from address to entry leader
    val addressToEntryLeader = functionBoundaries.functions.associate {
        it.entryAddress to it.entryLeader
    }

    // Find all JSR call sites
    val allCallSites = mutableListOf<CallSite>()
    val callSitesByFunction = mutableMapOf<Int, MutableList<CallSite>>()

    functionBoundaries.functions.forEach { function ->
        function.blocks.forEach { block ->
            block.lineIndexes.forEach { lineIndex ->
                val lineRef = codeFile[lineIndex]
                val instruction = lineRef.content.instruction

                if (instruction?.op == AssemblyOp.JSR) {
                    // Determine target address by looking up the label
                    val targetAddress = when (val addr = instruction.address) {
                        is AssemblyAddressing.Label -> {
                            // Resolve label to address
                            resolution.labelToAddress[addr.label]
                        }
                        else -> null // Indirect calls (JSR ($address)) are possible
                    }

                    // Find target function
                    val calleeEntryLeader = targetAddress?.let { addressToEntryLeader[it] }

                    val callSite = CallSite(
                        callerEntryLeader = function.entryLeader,
                        callBlockLeader = block.leaderIndex,
                        callLineRef = lineRef,
                        calleeEntryLeader = calleeEntryLeader,
                        targetAddress = targetAddress
                    )

                    allCallSites.add(callSite)
                    callSitesByFunction.getOrPut(function.entryLeader) { mutableListOf() }.add(callSite)
                }
            }
        }
    }

    // Build call edges
    val callEdgesMap = mutableMapOf<Pair<Int, Int?>, MutableList<CallSite>>()
    allCallSites.forEach { callSite ->
        val key = callSite.callerEntryLeader to callSite.calleeEntryLeader
        callEdgesMap.getOrPut(key) { mutableListOf() }.add(callSite)
    }

    val callEdges = callEdgesMap.map { (key, sites) ->
        CallEdge(caller = key.first, callee = key.second, callSites = sites)
    }

    // Build function call info
    val functionCallInfo = mutableMapOf<Int, FunctionCallInfo>()

    functionBoundaries.functions.forEach { function ->
        val entryLeader = function.entryLeader
        val sites = callSitesByFunction[entryLeader] ?: emptyList()

        // Outgoing calls (callees)
        val callees = sites.mapNotNull { it.calleeEntryLeader }.toSet()

        // Incoming calls (callers)
        val callers = allCallSites
            .filter { it.calleeEntryLeader == entryLeader }
            .map { it.callerEntryLeader }
            .toSet()

        // Check for direct recursion
        val isDirectlyRecursive = entryLeader in callees

        functionCallInfo[entryLeader] = FunctionCallInfo(
            function = function,
            callees = callees,
            callers = callers,
            callSites = sites,
            isDirectlyRecursive = isDirectlyRecursive
        )
    }

    // Find strongly connected components (call cycles) using Tarjan's algorithm
    val callCycles = findStronglyConnectedComponents(functionCallInfo)

    // Find functions that are never called
    val allCalledFunctions = allCallSites.mapNotNull { it.calleeEntryLeader }.toSet()
    val neverCalled = functionBoundaries.functions.map { it.entryLeader }.toSet() - allCalledFunctions

    // Check for indirect calls
    val hasIndirectCalls = allCallSites.any { it.calleeEntryLeader == null }

    return CallGraphConstruction(
        functionCallInfo = functionCallInfo,
        callEdges = callEdges,
        callCycles = callCycles,
        neverCalled = neverCalled,
        hasIndirectCalls = hasIndirectCalls
    )
}

/**
 * Find strongly connected components using Tarjan's algorithm
 * Returns sets of mutually recursive functions
 */
private fun findStronglyConnectedComponents(
    functionCallInfo: Map<Int, FunctionCallInfo>
): List<Set<Int>> {
    val sccs = mutableListOf<Set<Int>>()
    val index = mutableMapOf<Int, Int>()
    val lowLink = mutableMapOf<Int, Int>()
    val onStack = mutableSetOf<Int>()
    val stack = ArrayDeque<Int>()
    var currentIndex = 0

    fun strongConnect(v: Int) {
        index[v] = currentIndex
        lowLink[v] = currentIndex
        currentIndex++
        stack.addLast(v)
        onStack.add(v)

        // Consider successors (callees)
        val callInfo = functionCallInfo[v]
        callInfo?.callees?.forEach { w ->
            when {
                w !in index -> {
                    strongConnect(w)
                    lowLink[v] = minOf(lowLink[v]!!, lowLink[w]!!)
                }
                w in onStack -> {
                    lowLink[v] = minOf(lowLink[v]!!, index[w]!!)
                }
            }
        }

        // If v is a root node, pop the stack to create an SCC
        if (lowLink[v] == index[v]) {
            val scc = mutableSetOf<Int>()
            while (true) {
                val w = stack.removeLast()
                onStack.remove(w)
                scc.add(w)
                if (w == v) break
            }
            // Only add SCCs with more than one function or self-loops
            if (scc.size > 1 || (scc.size == 1 && v in functionCallInfo[v]?.callees.orEmpty())) {
                sccs.add(scc)
            }
        }
    }

    functionCallInfo.keys.forEach { v ->
        if (v !in index) {
            strongConnect(v)
        }
    }

    return sccs
}

/**
 * Helper to get call depth (max distance from entry points)
 */
fun CallGraphConstruction.getCallDepth(entryLeader: Int): Int {
    val info = functionCallInfo[entryLeader] ?: return 0

    // If never called, it's an entry point (depth 0)
    if (entryLeader in neverCalled) return 0

    // Use BFS to find shortest path from any entry point
    val visited = mutableSetOf<Int>()
    val queue = ArrayDeque<Pair<Int, Int>>()

    neverCalled.forEach { entry ->
        queue.add(entry to 0)
        visited.add(entry)
    }

    while (queue.isNotEmpty()) {
        val (current, depth) = queue.removeFirst()

        if (current == entryLeader) {
            return depth
        }

        functionCallInfo[current]?.callees?.forEach { callee ->
            if (callee !in visited) {
                visited.add(callee)
                queue.add(callee to depth + 1)
            }
        }
    }

    // If not reachable from any entry point, return -1
    return -1
}

/**
 * Helper to check if a function is reachable from entry points
 */
fun CallGraphConstruction.isReachableFromEntry(entryLeader: Int): Boolean {
    return getCallDepth(entryLeader) >= 0
}

/**
 * Helper to get all functions called transitively by this function
 */
fun CallGraphConstruction.getTransitiveCallees(entryLeader: Int): Set<Int> {
    val result = mutableSetOf<Int>()
    val queue = ArrayDeque<Int>()
    queue.add(entryLeader)

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        functionCallInfo[current]?.callees?.forEach { callee ->
            if (callee !in result) {
                result.add(callee)
                queue.add(callee)
            }
        }
    }

    return result
}
