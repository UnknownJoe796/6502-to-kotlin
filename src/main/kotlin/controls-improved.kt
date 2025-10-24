package com.ivieleague.decompiler6502tokotlin.hand

/**
 * Improved control flow analysis with:
 * 1. Post-dominator detection to find common exit points
 * 2. Early return detection
 * 3. Region formation for single-entry/single-exit constructs
 */

/**
 * Compute post-dominators for all blocks in a function.
 * A block X post-dominates block Y if all paths from Y to the exit must go through X.
 *
 * Returns a map: block -> its immediate post-dominator (null if block is exit)
 */
fun AssemblyFunction.computePostDominators(): Map<AssemblyBlock, AssemblyBlock?> {
    // Collect all reachable blocks
    val blocks = mutableSetOf<AssemblyBlock>()
    fun collectBlocks(b: AssemblyBlock?) {
        if (b == null) return
        if (b.function != this) return
        if (!blocks.add(b)) return
        collectBlocks(b.fallThroughExit)
        collectBlocks(b.branchExit)
    }
    collectBlocks(this.startingBlock)

    if (blocks.isEmpty()) return emptyMap()

    // Find exit blocks (no successors within this function)
    val exitBlocks = blocks.filter { b ->
        val ftExit = b.fallThroughExit
        val brExit = b.branchExit
        (ftExit == null || ftExit.function != this) && (brExit == null || brExit.function != this)
    }.toSet()

    // Initialize: all blocks post-dominate themselves, exits post-dominate nothing else
    val postDom = mutableMapOf<AssemblyBlock, MutableSet<AssemblyBlock>>()
    for (b in blocks) {
        postDom[b] = if (b in exitBlocks) {
            mutableSetOf(b)
        } else {
            blocks.toMutableSet() // Initially assume all blocks post-dominate
        }
    }

    // Iteratively compute post-dominators
    var changed = true
    while (changed) {
        changed = false
        for (b in blocks) {
            if (b in exitBlocks) continue

            // A block is post-dominated by the intersection of its successors' post-dominators
            val successors = listOfNotNull(b.fallThroughExit, b.branchExit)
                .filter { it.function == this }

            if (successors.isEmpty()) continue

            val newPostDom = successors
                .map { postDom[it] ?: emptySet() }
                .reduce { acc, set -> (acc intersect set).toMutableSet() }
                .toMutableSet()

            newPostDom.add(b) // Block always post-dominates itself

            if (newPostDom != postDom[b]) {
                postDom[b] = newPostDom
                changed = true
            }
        }
    }

    // Find immediate post-dominator (closest post-dominator in post-dominator tree)
    val immediatePostDom = mutableMapOf<AssemblyBlock, AssemblyBlock?>()
    for (b in blocks) {
        val postDominators = postDom[b]?.minus(b) ?: emptySet()

        if (postDominators.isEmpty()) {
            immediatePostDom[b] = null // Exit block or no post-dom
        } else {
            // Find the closest post-dominator (one that is not post-dominated by any other)
            val immediate = postDominators.firstOrNull { candidate ->
                postDominators.all { other ->
                    other == candidate || candidate !in (postDom[other] ?: emptySet())
                }
            }
            immediatePostDom[b] = immediate
        }
    }

    return immediatePostDom
}

/**
 * Check if a block ends with a return instruction (RTS or RTI).
 */
fun AssemblyBlock.isReturn(): Boolean {
    val lastInstruction = this.lines.lastOrNull { it.instruction != null }?.instruction
    return lastInstruction?.op == AssemblyOp.RTS || lastInstruction?.op == AssemblyOp.RTI
}

/**
 * Detect and eliminate duplicate blocks that appear after if/else branches.
 *
 * Pattern to detect:
 *   if (cond) {
 *       A
 *       BlockX  // duplicate
 *   } else {
 *       B
 *   }
 *   BlockX  // duplicate at top level
 *
 * This happens when the control flow analyzer creates an if-then, but the "then" branch
 * also includes the code that follows the if statement.
 *
 * Strategy: Check if the last block(s) of a branch match the next top-level blocks.
 * If so, remove them from the branch.
 */
fun List<ControlNode>.eliminateDuplicateExits(): List<ControlNode> {
    fun getTrailingBlocks(nodes: List<ControlNode>): List<AssemblyBlock> {
        return nodes.reversed().takeWhile { it is BlockNode }.map { (it as BlockNode).block }
    }

    fun processLevel(nodes: List<ControlNode>): List<ControlNode> {
        val result = mutableListOf<ControlNode>()

        var i = 0
        while (i < nodes.size) {
            val node = nodes[i]

            when (node) {
                is IfNode -> {
                    // Look ahead to see what blocks follow this if statement
                    val followingBlocks = nodes.drop(i + 1)
                        .takeWhile { it is BlockNode }
                        .map { (it as BlockNode).block }

                    // Check if then/else branches end with these same blocks
                    var newThenBranch = processLevel(node.thenBranch)
                    var newElseBranch = processLevel(node.elseBranch)

                    // Remove trailing blocks from then branch if they match following blocks
                    val thenTrailing = getTrailingBlocks(newThenBranch)
                    val thenOverlap = thenTrailing.takeWhile { it in followingBlocks }.size
                    if (thenOverlap > 0) {
                        newThenBranch = newThenBranch.dropLast(thenOverlap)
                    }

                    // Remove trailing blocks from else branch if they match following blocks
                    val elseTrailing = getTrailingBlocks(newElseBranch)
                    val elseOverlap = elseTrailing.takeWhile { it in followingBlocks }.size
                    if (elseOverlap > 0) {
                        newElseBranch = newElseBranch.dropLast(elseOverlap)
                    }

                    result.add(node.copy(
                        thenBranch = newThenBranch.toMutableList(),
                        elseBranch = newElseBranch.toMutableList()
                    ))
                }
                is LoopNode -> {
                    val newBody = processLevel(node.body)
                    result.add(node.copy(body = newBody.toMutableList()))
                }
                else -> {
                    result.add(node)
                }
            }

            i++
        }

        return result
    }

    return processLevel(this)
}

/**
 * Factor out common post-dominators from if/else branches.
 *
 * Pattern:
 *   if (cond) {
 *       A
 *       Common
 *   } else {
 *       B
 *       Common
 *   }
 *
 * Transforms to:
 *   if (cond) {
 *       A
 *   } else {
 *       B
 *   }
 *   Common
 */
fun List<ControlNode>.factorCommonPostDominators(postDom: Map<AssemblyBlock, AssemblyBlock?>): List<ControlNode> {
    val result = mutableListOf<ControlNode>()

    for (node in this) {
        when (node) {
            is IfNode -> {
                // Find common exit block
                val thenExits = node.thenBranch.map { it.exits }.flatten().toSet()
                val elseExits = node.elseBranch.map { it.exits }.flatten().toSet()
                val commonExits = thenExits intersect elseExits

                if (commonExits.size == 1 && node.join == null) {
                    // We found a common exit - this should be the join point
                    val join = commonExits.first()

                    // Check if both branches end with blocks that jump to this join
                    val thenEndsWith = node.thenBranch.lastOrNull()
                    val elseEndsWith = node.elseBranch.lastOrNull()

                    // Remove trailing blocks that just jump to join
                    val newThenBranch = node.thenBranch.toMutableList()
                    val newElseBranch = node.elseBranch.toMutableList()

                    if (thenEndsWith is BlockNode && thenEndsWith.exits == setOf(join)) {
                        // Check if this block only contains a jump
                        val isJustJump = thenEndsWith.block.lines.filter { it.instruction != null }.size == 1
                        if (isJustJump) {
                            newThenBranch.removeAt(newThenBranch.lastIndex)
                        }
                    }

                    if (elseEndsWith is BlockNode && elseEndsWith.exits == setOf(join)) {
                        val isJustJump = elseEndsWith.block.lines.filter { it.instruction != null }.size == 1
                        if (isJustJump) {
                            newElseBranch.removeAt(newElseBranch.lastIndex)
                        }
                    }

                    result.add(node.copy(
                        thenBranch = newThenBranch,
                        elseBranch = newElseBranch,
                        join = join
                    ))
                } else {
                    result.add(node)
                }
            }
            else -> result.add(node)
        }
    }

    return result
}

/**
 * Convert nested ifs with shared exits to use early returns/gotos where appropriate.
 *
 * Pattern to detect:
 *   if (!cond1) {
 *       SharedExit
 *   }
 *   if (!cond2) {
 *       SharedExit
 *   }
 *   ...
 *   SharedExit
 *
 * This is the "guard clause" pattern where early exits share the same destination.
 */
fun List<ControlNode>.recognizeGuardClauses(): List<ControlNode> {
    val result = mutableListOf<ControlNode>()

    // Find sequences of ifs with the same exit block
    var i = 0
    while (i < this.size) {
        val node = this[i]

        if (node is IfNode && node.elseBranch.isEmpty()) {
            // Check if this is a guard clause (then branch ends with a common block)
            val thenExit = node.thenBranch.lastOrNull() as? BlockNode
            val followingBlock = this.getOrNull(i + 1) as? BlockNode

            if (thenExit != null && followingBlock != null && thenExit.block == followingBlock.block) {
                // This is a guard clause - the then branch exits to the same block that follows
                // Remove the duplicate exit from the then branch
                val newThenBranch = node.thenBranch.dropLast(1).toMutableList()

                result.add(node.copy(thenBranch = newThenBranch))
                i++
                continue
            }
        }

        // Recursively process nested structures
        when (node) {
            is IfNode -> {
                result.add(node.copy(
                    thenBranch = node.thenBranch.recognizeGuardClauses().toMutableList(),
                    elseBranch = node.elseBranch.recognizeGuardClauses().toMutableList()
                ))
            }
            is LoopNode -> {
                result.add(node.copy(
                    body = node.body.recognizeGuardClauses().toMutableList()
                ))
            }
            else -> result.add(node)
        }

        i++
    }

    return result
}

/**
 * Hoist common code out of if branches to reduce duplication.
 *
 * Pattern:
 *   if (cond) {
 *       UniqueA
 *       Common
 *   } else {
 *       UniqueB
 *       Common
 *   }
 *
 * Transforms to:
 *   if (cond) {
 *       UniqueA
 *   } else {
 *       UniqueB
 *   }
 *   Common
 */
fun List<ControlNode>.hoistCommonSuffixes(): List<ControlNode> {
    val result = mutableListOf<ControlNode>()

    for (node in this) {
        when (node) {
            is IfNode -> {
                // First, recursively process children
                var newThenBranch = node.thenBranch.hoistCommonSuffixes()
                var newElseBranch = node.elseBranch.hoistCommonSuffixes()

                // If both branches have content, check for common suffixes
                if (newThenBranch.isNotEmpty() && newElseBranch.isNotEmpty()) {
                    // Find common trailing blocks
                    val commonSuffix = mutableListOf<ControlNode>()

                    var thenIdx = newThenBranch.lastIndex
                    var elseIdx = newElseBranch.lastIndex

                    while (thenIdx >= 0 && elseIdx >= 0) {
                        val thenNode = newThenBranch[thenIdx]
                        val elseNode = newElseBranch[elseIdx]

                        // Check if both are block nodes with the same block
                        if (thenNode is BlockNode && elseNode is BlockNode && thenNode.block == elseNode.block) {
                            commonSuffix.add(0, thenNode)
                            thenIdx--
                            elseIdx--
                        } else {
                            break
                        }
                    }

                    if (commonSuffix.isNotEmpty()) {
                        // Remove common suffix from branches
                        newThenBranch = newThenBranch.dropLast(commonSuffix.size)
                        newElseBranch = newElseBranch.dropLast(commonSuffix.size)

                        // Add the modified if node
                        result.add(node.copy(
                            thenBranch = newThenBranch.toMutableList(),
                            elseBranch = newElseBranch.toMutableList()
                        ))

                        // Add the common suffix after the if
                        result.addAll(commonSuffix)
                    } else {
                        // No common suffix, just add the processed node
                        result.add(node.copy(
                            thenBranch = newThenBranch.toMutableList(),
                            elseBranch = newElseBranch.toMutableList()
                        ))
                    }
                } else {
                    result.add(node.copy(
                        thenBranch = newThenBranch.toMutableList(),
                        elseBranch = newElseBranch.toMutableList()
                    ))
                }
            }
            is LoopNode -> {
                result.add(node.copy(
                    body = node.body.hoistCommonSuffixes().toMutableList()
                ))
            }
            else -> result.add(node)
        }
    }

    return result
}

/**
 * Global duplicate elimination - removes ALL duplicate block occurrences across the entire tree.
 *
 * This is more aggressive than eliminateDuplicateExits - it tracks every block globally
 * and only allows each block to appear once in the output tree.
 *
 * Use this as a final cleanup pass.
 */
fun List<ControlNode>.globalDuplicateElimination(): List<ControlNode> {
    val seenBlocks = mutableSetOf<AssemblyBlock>()

    fun processNode(node: ControlNode): ControlNode? {
        return when (node) {
            is BlockNode -> {
                if (seenBlocks.add(node.block)) {
                    node  // First time seeing this block, keep it
                } else {
                    null  // Already seen, remove it
                }
            }
            is IfNode -> {
                val newThen = node.thenBranch.mapNotNull { processNode(it) }.toMutableList()
                val newElse = node.elseBranch.mapNotNull { processNode(it) }.toMutableList()
                node.copy(thenBranch = newThen, elseBranch = newElse)
            }
            is LoopNode -> {
                val newBody = node.body.mapNotNull { processNode(it) }.toMutableList()
                node.copy(body = newBody)
            }
            else -> node
        }
    }

    return this.mapNotNull { processNode(it) }
}

/**
 * Apply all improvements to control flow.
 */
fun AssemblyFunction.improveControlFlow(): List<ControlNode> {
    // First, run basic control analysis
    val nodes = this.analyzeControls().toMutableList()

    // Compute post-dominators
    val postDom = this.computePostDominators()

    // Apply improvements in order
    var improved = nodes.toList()
    improved = improved.factorCommonPostDominators(postDom)
    improved = improved.recognizeGuardClauses()
    improved = improved.hoistCommonSuffixes()
    improved = improved.eliminateDuplicateExits()

    // Final aggressive cleanup - remove ALL duplicate blocks globally
    improved = improved.globalDuplicateElimination()

    // Store back
    this.asControls = improved
    return improved
}

/**
 * Apply improvements to all functions.
 */
fun List<AssemblyFunction>.improveControlFlow() {
    for (f in this) {
        f.improveControlFlow()
    }
}
