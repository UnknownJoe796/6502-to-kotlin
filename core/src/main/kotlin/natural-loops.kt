package com.ivieleague.decompiler6502tokotlin.hand

/**
 * Represents a natural loop detected using dominator analysis.
 * A natural loop has a single entry point (header) and one or more back-edges.
 */
data class NaturalLoop(
    val header: AssemblyBlock,
    val backEdges: List<Pair<AssemblyBlock, AssemblyBlock>>, // (from, to) where to == header
    val body: Set<AssemblyBlock> // includes header
) {
    val exits: Set<AssemblyBlock> by lazy {
        // Find all blocks that have successors outside the loop
        body.flatMap { block ->
            listOfNotNull(block.fallThroughExit, block.branchExit)
                .filter { it !in body }
        }.toSet()
    }
}

/**
 * Detect all natural loops in a control flow graph using dominator information.
 * 
 * A natural loop is identified by:
 * 1. Finding back-edges: edges (N → H) where H dominates N
 * 2. The loop header H is the target of the back-edge
 * 3. The loop body is all nodes that can reach N without going through H
 */
fun List<AssemblyBlock>.detectNaturalLoops(): List<NaturalLoop> {
    val allLoops = mutableMapOf<AssemblyBlock, MutableList<Pair<AssemblyBlock, AssemblyBlock>>>()

    // Step 1: Find all back-edges
    for (block in this) {
        val successors = listOfNotNull(block.fallThroughExit, block.branchExit)
        for (successor in successors) {
            // A back-edge is an edge to a block that dominates the current block
            if (isDominatedBy(block, successor)) {
                // successor dominates block, so (block → successor) is a back-edge
                allLoops.getOrPut(successor) { mutableListOf() }.add(block to successor)
            }
        }
    }

    // Step 2: For each loop header, compute the loop body
    return allLoops.map { (header, backEdges) ->
        val body = mutableSetOf<AssemblyBlock>()
        body.add(header)

        // For each back-edge, find all nodes that can reach the tail without going through the header
        for ((tail, _) in backEdges) {
            if (tail != header) {
                body.add(tail)
                findLoopBody(tail, header, body)
            }
        }

        NaturalLoop(header, backEdges, body)
    }.sortedBy { it.header.originalLineIndex }
}

/**
 * Check if block is dominated by dominator.
 */
private fun isDominatedBy(block: AssemblyBlock, dominator: AssemblyBlock): Boolean {
    var current: AssemblyBlock? = block
    while (current != null) {
        if (current == dominator) return true
        current = current.immediateDominator
    }
    return false
}

/**
 * Find all blocks in the loop body by walking backwards from tail to header.
 * Uses a worklist algorithm to find all nodes that can reach the tail without going through the header.
 */
private fun findLoopBody(tail: AssemblyBlock, header: AssemblyBlock, body: MutableSet<AssemblyBlock>) {
    val worklist = mutableListOf(tail)
    val visited = mutableSetOf<AssemblyBlock>()

    while (worklist.isNotEmpty()) {
        val current = worklist.removeAt(worklist.lastIndex)
        if (current in visited) continue
        visited.add(current)

        for (predecessor in current.enteredFrom) {
            if (predecessor == header) continue // Don't go through header
            if (body.add(predecessor)) {
                worklist.add(predecessor)
            }
        }
    }
}
