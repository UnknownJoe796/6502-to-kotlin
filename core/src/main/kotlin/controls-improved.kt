package com.ivieleague.decompiler6502tokotlin.hand

/**
 * Improved control flow analysis with:
 * 1. Post-dominator detection to find common exit points
 * 2. Early return detection
 * 3. Region formation for single-entry/single-exit constructs
 * 4. Proper block ordering based on control flow rather than source order
 */

/**
 * Fix control flow ordering to respect function entry points.
 *
 * The problem: analyzeControls() sorts blocks by originalLineIndex, which can place
 * branch targets that appear earlier in the source file before the function entry point.
 * For example, FloateyNumbersRoutine (line 1282) branches to EndExitOne (line 1245 on early return),
 * causing the control flow to incorrectly start with EndExitOne.
 *
 * The fix: Filter out blocks that appear before the entry point in source order if they're
 * only reachable by branching backward.
 */
fun List<ControlNode>.fixBlockOrdering(entryBlock: AssemblyBlock): List<ControlNode> {
    // Find blocks that are backward branches (appear before entry in source)
    val backwardBlocks = mutableSetOf<AssemblyBlock>()

    fun collectBlocks(node: ControlNode) {
        when (node) {
            is BlockNode -> {
                if (node.block.originalLineIndex < entryBlock.originalLineIndex) {
                    backwardBlocks.add(node.block)
                }
            }
            is IfNode -> {
                node.thenBranch.forEach { collectBlocks(it) }
                node.elseBranch.forEach { collectBlocks(it) }
            }
            is LoopNode -> {
                node.body.forEach { collectBlocks(it) }
            }
            else -> {}
        }
    }

    this.forEach { collectBlocks(it) }

    // Check if first node involves backward blocks
    val first = this.firstOrNull()
    val firstIsBackward = when (first) {
        is BlockNode -> first.block in backwardBlocks && first.block != entryBlock
        is LoopNode -> first.entry.originalLineIndex < entryBlock.originalLineIndex
        else -> false
    }

    if (firstIsBackward) {
        // The first node is a backward block or contains one - need to reorder
        // Find the actual entry node and put it first
        val result = mutableListOf<ControlNode>()

        // Look for the entry block in the remaining nodes
        var foundEntry = false
        for (i in this.indices) {
            val node = this[i]
            if (node is BlockNode && node.block == entryBlock) {
                // Found the entry - move everything before it to after it
                result.add(node)
                result.addAll(this.drop(i + 1))
                // Add the backward nodes at the end (they should be inside ifs/loops already)
                for (j in 0 until i) {
                    val backwardNode = this[j]
                    // Only add if not redundant (backward blocks should be referenced within control flow)
                    // For now, skip them at top level to avoid duplication
                }
                foundEntry = true
                break
            }
        }

        if (!foundEntry) {
            // Entry not found in nodes - this shouldn't happen but handle it
            // Skip the first backward node(s) and keep the rest
            val filtered = this.dropWhile { node ->
                when (node) {
                    is BlockNode -> node.block in backwardBlocks && node.block != entryBlock
                    is LoopNode -> node.entry.originalLineIndex < entryBlock.originalLineIndex
                    else -> false
                }
            }
            return filtered
        }

        return result
    } else {
        return this
    }
}

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

        // Check if then branch ends with same block that follows
        if (node is IfNode && node.elseBranch.isEmpty()) {
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
 * Remove unreachable code after returns.
 *
 * Pattern:
 *   return
 *   ... unreachable code ...
 *
 * The control flow analyzer might create nodes after a return statement.
 * This pass removes them.
 */
fun List<ControlNode>.eliminateUnreachableAfterReturn(): List<ControlNode> {
    val result = mutableListOf<ControlNode>()

    for (node in this) {
        when (node) {
            is BlockNode -> {
                // Check if this block ends with RTS/RTI
                val hasReturn = node.block.lines.any { line ->
                    val op = line.instruction?.op
                    op == AssemblyOp.RTS || op == AssemblyOp.RTI
                }

                result.add(node)

                // If this block returns, stop processing - everything after is unreachable
                if (hasReturn) {
                    break
                }
            }
            is IfNode -> {
                // Check if both branches return
                val thenReturns = node.thenBranch.any { it.hasReturn() }
                val elseReturns = node.elseBranch.any { it.hasReturn() }

                // Recursively clean branches
                val cleanedNode = node.copy(
                    thenBranch = node.thenBranch.eliminateUnreachableAfterReturn().toMutableList(),
                    elseBranch = node.elseBranch.eliminateUnreachableAfterReturn().toMutableList()
                )

                result.add(cleanedNode)

                // If both branches return, everything after is unreachable
                if (thenReturns && elseReturns) {
                    break
                }
            }
            is LoopNode -> {
                result.add(node.copy(
                    body = node.body.eliminateUnreachableAfterReturn().toMutableList()
                ))
            }
            else -> {
                result.add(node)
            }
        }
    }

    return result
}

/**
 * Check if a ControlNode contains a return.
 */
fun ControlNode.hasReturn(): Boolean {
    return when (this) {
        is BlockNode -> {
            this.block.lines.any { line ->
                val op = line.instruction?.op
                op == AssemblyOp.RTS || op == AssemblyOp.RTI
            }
        }
        is IfNode -> {
            this.thenBranch.any { it.hasReturn() } || this.elseBranch.any { it.hasReturn() }
        }
        is LoopNode -> {
            this.body.any { it.hasReturn() }
        }
        else -> false
    }
}

/**
 * Find the instruction that set the flags for a branch.
 * Looks in the current block, then walks backward through predecessors.
 * Returns the flag-setting instruction line, or null if not found.
 */
fun AssemblyBlock.findFlagSetterForBranch(): AssemblyLine? {
    val visited = mutableSetOf<AssemblyBlock>()
    val toVisit = mutableListOf(Pair(this, true))  // (block, searchFromBranch)

    while (toVisit.isNotEmpty()) {
        val (current, searchFromBranch) = toVisit.removeAt(0)
        if (!visited.add(current)) continue

        // Determine where to start searching
        val searchStart = if (searchFromBranch && current == this) {
            val branchLine = this.lines.lastOrNull { it.instruction?.op?.isBranch == true } ?: return null
            this.lines.indexOf(branchLine) - 1
        } else {
            current.lines.size - 1
        }

        // Look backward for ANY instruction that modifies flags
        for (i in searchStart downTo 0) {
            val line = current.lines[i]
            val op = line.instruction?.op
            val addr = line.instruction?.address

            // Check if this instruction modifies any flags
            val modifiesFlags = op?.modifies(addr?.let { it::class })?.any {
                it == AssemblyAffectable.Zero || it == AssemblyAffectable.Carry ||
                it == AssemblyAffectable.Negative || it == AssemblyAffectable.Overflow
            } == true

            if (modifiesFlags) {
                // Found the instruction that set the flags
                return line
            }
        }

        // If not found in this block, check predecessors
        current.enteredFrom.forEach { pred ->
            if (pred.function == current.function && pred !in visited) {
                toVisit.add(Pair(pred, false))
            }
        }
    }
    return null
}

/**
 * Find the comparison instruction that set the flags for a branch (legacy).
 * Now just calls findFlagSetterForBranch for compatibility.
 */
fun AssemblyBlock.findComparisonForBranch(): AssemblyLine? = findFlagSetterForBranch()

/**
 * Check if two flag-setting instructions are equivalent (test the same thing).
 * Two instructions are considered the same if they're the same opcode operating on the same address.
 */
fun AssemblyLine.isSameComparisonAs(other: AssemblyLine?): Boolean {
    if (other == null) return false
    val thisOp = this.instruction?.op
    val otherOp = other.instruction?.op

    // Must be the same opcode
    if (thisOp != otherOp) return false

    // Compare the addressing modes/operands
    val thisAddr = this.instruction?.address
    val otherAddr = other.instruction?.address

    // For memory operations, they must access the same address
    // For immediate/accumulator operations, compare the values
    return thisAddr == otherAddr
}

/**
 * Merge consecutive IfNodes that test the same comparison result.
 *
 * Pattern:
 *   cpy #$32
 *   bne label1    -> if (!zero) goto label1
 *   (code)
 *   bne label2    -> if (!zero) goto label2  [testing SAME comparison]
 *
 * This should become:
 *   if (zero) {
 *     (code)
 *     goto label2
 *   } else {
 *     goto label1
 *   }
 */
fun List<ControlNode>.mergeConsecutiveSameComparison(): List<ControlNode> {
    val result = mutableListOf<ControlNode>()
    var i = 0

    while (i < this.size) {
        val node = this[i]

        if (node is IfNode && node.elseBranch.isEmpty()) {
            // Check if this IfNode's condition block has a comparison
            val comparisonBlock = node.condition.branchBlock
            val comparison = comparisonBlock.findComparisonForBranch()

            if (comparison != null) {
                // Look ahead to see if the next nodes also test the same comparison
                val thenContent = node.thenBranch.toMutableList()
                var j = 0
                var foundMerge = false

                // Check the content of the then branch for another IfNode testing the same thing
                while (j < thenContent.size) {
                    val innerNode = thenContent[j]

                    if (innerNode is IfNode && innerNode.elseBranch.isEmpty()) {
                        val innerBlock = innerNode.condition.branchBlock
                        val innerComparison = innerBlock.findComparisonForBranch()

                        if (comparison.isSameComparisonAs(innerComparison)) {
                            // Found a match! Merge these two IfNodes
                            // The outer if tests one branch, the inner if tests the other
                            foundMerge = true

                            // Create a proper if-else structure
                            // The "else" is the outer's then content up to this point + inner's then
                            val elseBranch = thenContent.take(j).toMutableList()
                            elseBranch.addAll(innerNode.thenBranch)

                            // Recursively process both branches
                            val mergedNode = node.copy(
                                thenBranch = thenContent.drop(j + 1).mergeConsecutiveSameComparison().toMutableList(),
                                elseBranch = elseBranch.mergeConsecutiveSameComparison().toMutableList()
                            )

                            result.add(mergedNode)
                            i++
                            break
                        }
                    }
                    j++
                }

                if (foundMerge) {
                    continue
                }
            }

            // No merge found, recursively process branches
            result.add(node.copy(
                thenBranch = node.thenBranch.mergeConsecutiveSameComparison().toMutableList(),
                elseBranch = node.elseBranch.mergeConsecutiveSameComparison().toMutableList()
            ))
        } else if (node is IfNode) {
            result.add(node.copy(
                thenBranch = node.thenBranch.mergeConsecutiveSameComparison().toMutableList(),
                elseBranch = node.elseBranch.mergeConsecutiveSameComparison().toMutableList()
            ))
        } else if (node is LoopNode) {
            result.add(node.copy(
                body = node.body.mergeConsecutiveSameComparison().toMutableList()
            ))
        } else {
            result.add(node)
        }

        i++
    }

    return result
}

/**
 * Remove IfNodes with empty then branches (they do nothing).
 */
fun List<ControlNode>.removeEmptyIfNodes(): List<ControlNode> {
    return this.mapNotNull { node ->
        when (node) {
            is IfNode -> {
                val cleanedThen = node.thenBranch.removeEmptyIfNodes()
                val cleanedElse = node.elseBranch.removeEmptyIfNodes()

                // If then branch is empty and no else, skip this if entirely
                if (cleanedThen.isEmpty() && cleanedElse.isEmpty()) {
                    null
                } else if (cleanedThen.isEmpty() && cleanedElse.isNotEmpty()) {
                    // Then is empty but else has content - invert the condition
                    // For now, just keep it as-is
                    node.copy(thenBranch = cleanedThen.toMutableList(), elseBranch = cleanedElse.toMutableList())
                } else {
                    node.copy(thenBranch = cleanedThen.toMutableList(), elseBranch = cleanedElse.toMutableList())
                }
            }
            is LoopNode -> {
                node.copy(body = node.body.removeEmptyIfNodes().toMutableList())
            }
            else -> node
        }
    }
}

/**
 * Flatten duplicate/contradictory nested if statements.
 *
 * Recursively removes patterns like:
 *   if (X) { if (X) { body } }  -> if (X) { body }
 *   if (X) { if (!X) { body } } -> if (X) { } (unreachable body removed)
 *
 * This must be applied recursively and repeatedly until no more changes occur.
 */
fun List<ControlNode>.flattenDuplicateConditions(): List<ControlNode> {
    return this.map { node ->
        when (node) {
            is IfNode -> {
                // First, recursively flatten the branches
                var flattenedThen = node.thenBranch.flattenDuplicateConditions()
                var flattenedElse = node.elseBranch.flattenDuplicateConditions()

                // Check if then branch is a single IfNode with same/opposite condition
                if (flattenedThen.size == 1 && flattenedThen[0] is IfNode) {
                    val innerIf = flattenedThen[0] as IfNode

                    // Check if same condition block
                    if (node.condition.branchBlock == innerIf.condition.branchBlock) {
                        val outerSense = node.condition.sense
                        val innerSense = innerIf.condition.sense

                        if (outerSense == innerSense) {
                            // Duplicate: if (X) { if (X) { body } } -> use inner directly
                            return@map innerIf.copy(
                                thenBranch = innerIf.thenBranch.flattenDuplicateConditions().toMutableList(),
                                elseBranch = innerIf.elseBranch.flattenDuplicateConditions().toMutableList()
                            )
                        } else {
                            // Contradictory: if (X) { if (!X) { body } } -> if (X) { }
                            return@map node.copy(
                                thenBranch = mutableListOf(),
                                elseBranch = flattenedElse.toMutableList()
                            )
                        }
                    }
                }

                // No flattening at this level, return with recursively flattened branches
                node.copy(
                    thenBranch = flattenedThen.toMutableList(),
                    elseBranch = flattenedElse.toMutableList()
                )
            }
            is LoopNode -> {
                node.copy(
                    body = node.body.flattenDuplicateConditions().toMutableList()
                )
            }
            else -> node
        }
    }
}

/**
 * Apply all improvements to control flow.
 */
fun AssemblyFunction.improveControlFlow(): List<ControlNode> {
    // First, run basic control analysis
    val nodes = this.analyzeControls().toMutableList()

    // Fix block ordering - ensure function starts at entry point, not at backward branches
    var improved = nodes.toList().fixBlockOrdering(this.startingBlock)

    // Merge consecutive branches testing the same comparison (e.g., cpy #$32; bne L1; ...; bne L2)
    improved = improved.mergeConsecutiveSameComparison()

    // Flatten duplicate/contradictory nested if statements (apply multiple times if needed)
    var prevSize = -1
    while (prevSize != improved.size) {
        prevSize = improved.size
        improved = improved.flattenDuplicateConditions()
    }

    // Compute post-dominators
    val postDom = this.computePostDominators()

    // Apply improvements in order
    improved = improved.factorCommonPostDominators(postDom)
    improved = improved.recognizeGuardClauses()
    improved = improved.hoistCommonSuffixes()
    improved = improved.eliminateDuplicateExits()

    // Final aggressive cleanup - remove ALL duplicate blocks globally
    improved = improved.globalDuplicateElimination()

    // Remove unreachable code after returns
    improved = improved.eliminateUnreachableAfterReturn()

    // Remove empty if nodes (they do nothing)
    improved = improved.removeEmptyIfNodes()

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
