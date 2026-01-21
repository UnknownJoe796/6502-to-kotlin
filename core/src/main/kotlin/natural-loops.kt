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
    // by Claude - CRITICAL FIX: Only consider CONDITIONAL back-edges for loop detection.
    // JMP back-edges (unconditional) are usually GOTO patterns within structured code,
    // not proper loops. Including them causes spurious "while(true)" loops to be detected.
    // Proper loops in 6502 code almost always use conditional branches (BNE, BPL, etc.)
    // for the back-edge, with the loop condition being the branch condition.
    for (block in this) {
        // Check the last instruction to see if this block ends with a conditional branch
        val lastInstr = block.lines.lastOrNull { it.instruction != null }?.instruction
        val isConditionalBlock = lastInstr?.op?.isBranch == true

        // For conditional blocks, check if the branch target dominates us (back-edge)
        if (isConditionalBlock) {
            val branchTarget = block.branchExit
            if (branchTarget != null && isDominatedBy(block, branchTarget)) {
                allLoops.getOrPut(branchTarget) { mutableListOf() }.add(block to branchTarget)
            }
        }

        // For fall-through, also check if it creates a back-edge (rare but possible)
        val fallthrough = block.fallThroughExit
        if (fallthrough != null && isDominatedBy(block, fallthrough)) {
            allLoops.getOrPut(fallthrough) { mutableListOf() }.add(block to fallthrough)
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
