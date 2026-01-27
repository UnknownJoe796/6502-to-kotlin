package com.ivieleague.decompiler6502tokotlin.hand

import javax.sound.midi.Track

class AssemblyBlock(
    val lines: List<AssemblyLine>
) {
    init { lines.forEach { it.block = this } }
    override fun toString(): String = "Line $originalLineIndex: ${lines.first()}"
    val originalLineIndex = lines.first().originalLineIndex
    val label: String? = lines.firstOrNull()?.label

    /**
     * The blocks that could enter this block.
     * Automatically maintained by fallThroughExit and branchExit bidirectional delegates.
     */
    private val enteredFromDelegate = BackwardCollectionDelegate<AssemblyBlock>()
    val enteredFrom: List<AssemblyBlock> by enteredFromDelegate

    /**
     * The exit from either falling through to the next block or a jump.
     * If it is null, that indicates this block performs an RTS or RTI.
     * Note that the code we're analyzing does not use any indirect jumps.
     * Automatically updates the target's enteredFrom list.
     */
    var fallThroughExit: AssemblyBlock? by BidirectionalDelegate({ it.enteredFromDelegate.getMutable() })

    /**
     * The alternate exit from a conditional branch, or the target of an unconditional JMP.
     * Note that the code we're analyzing does not use any indirect jumps.
     * Always comes from last assembly line.
     * Automatically updates the target's enteredFrom list.
     */
    var branchExit: AssemblyBlock? by BidirectionalDelegate({ it.enteredFromDelegate.getMutable() })

    /**
     * The blocks dominated by this block (inverse of immediateDominator).
     * Automatically maintained by immediateDominator bidirectional delegate.
     */
    private val dominatesDelegate = BackwardCollectionDelegate<AssemblyBlock>()
    val dominates: List<AssemblyBlock> by dominatesDelegate

    /**
     * The immediate dominator of this block - in other words, the only block this block can come from.
     * Automatically updates the dominator's dominates list.
     * Can be reset because dominator analysis may run multiple times.
     */
    var immediateDominator: AssemblyBlock? by BidirectionalDelegate(
        backwardGetter = { it.dominatesDelegate.getMutable() },
        allowReset = true
    )

    /**
     * The function this block belongs to.
     * Note: A block may be reachable from multiple functions, in which case this will be
     * set to whichever function's analysis runs last. This is acceptable for shared epilogue code.
     */
    var function: AssemblyFunction? = null

    /**
     * by Claude - Set of functions for which the first instruction of this block should be skipped.
     * This is set when the block is reached via a .db $2c BIT skip pattern from another block.
     * The BIT instruction "eats" the next 2 bytes (the first instruction of this block).
     * Using a set allows the same block to have different skip behavior in different functions
     * (e.g., MoveSpritesOffscreen block skips when part of MoveAllSpritesOffscreen, but not when
     * decompiled as its own function).
     */
    val skipFirstInstructionForFunctions: MutableSet<AssemblyFunction> = mutableSetOf()

    val dominationDepth: Int get() = generateSequence(this.immediateDominator) { it.immediateDominator }.count()
}

sealed interface TrackedAsIo {
    object A : TrackedAsIo {
        override fun toString() = "A"
    }
    object X : TrackedAsIo {
        override fun toString() = "X"
    }
    object Y : TrackedAsIo {
        override fun toString() = "Y"
    }
    object ZeroFlag : TrackedAsIo {
        override fun toString() = "ZeroFlag"
    }
    object NegativeFlag : TrackedAsIo {
        override fun toString() = "NegativeFlag"
    }
    object OverflowFlag : TrackedAsIo {
        override fun toString() = "OverflowFlag"
    }
    object CarryFlag : TrackedAsIo {
        override fun toString() = "CarryFlag"
    }

    /**
     * An address in zero-page that is treated like a temporary register to move data between subroutines.
     */
    data class VirtualRegister(val label: String): TrackedAsIo {
        override fun toString(): String = label
    }
}
fun AssemblyAffectable.toTrackedAsIo(
    addressing: AssemblyAddressing?,
    labelIsVirtualRegister: (String) -> Boolean = {
        it.startsWith('$') && it.substring(1).toUShortOrNull(16)?.let { it <= 0x7u } == true
    }
): TrackedAsIo? = when (this) {
    AssemblyAffectable.A -> TrackedAsIo.A
    AssemblyAffectable.X -> TrackedAsIo.X
    AssemblyAffectable.Y -> TrackedAsIo.Y
    AssemblyAffectable.Stack -> null
    AssemblyAffectable.DisableInterrupt -> null
    AssemblyAffectable.StackPointer -> null
    AssemblyAffectable.Negative -> TrackedAsIo.NegativeFlag
    AssemblyAffectable.Overflow -> TrackedAsIo.OverflowFlag
    AssemblyAffectable.Zero -> TrackedAsIo.ZeroFlag
    AssemblyAffectable.Carry -> TrackedAsIo.CarryFlag
    AssemblyAffectable.Memory -> (addressing as? AssemblyAddressing.Direct)?.takeIf {
        labelIsVirtualRegister(it.label)
    }?.let { TrackedAsIo.VirtualRegister(it.label) }
}

class AssemblyFunction(
    val startingBlock: AssemblyBlock,
    val callers: List<AssemblyLine>
) {
    override fun toString(): String = "fun ${startingBlock.label}(${inputs?.joinToString("")}): ${outputs?.joinToString("")}"
    /**
     * States that are consumed before being set by the function, indicating that they are inputs.
     */
    var inputs: Set<TrackedAsIo>? = null

    /**
     * States that may be modified (written/clobbered) within the function body.
     * This is a superset of outputs, used to gate which states can be considered outputs.
     */
    var clobbers: Set<TrackedAsIo>? = null

    /**
     * States that calling functions read from that are affected by this function.
     */
    var outputs: Set<TrackedAsIo>? = null

    /**
     * The number of stack elements pushed by the function.
     * Negative indicates stack elements popped off the stack.
     */
    var stackSizeChange: Int? = null

    var asControls: List<ControlNode>? = null

    // by Claude - All blocks belonging to this function (populated during functionify)
    var blocks: Set<AssemblyBlock>? = null

    // by Claude - For "empty functions" whose starting block is owned by another function
    // (e.g., BlockBufferColli_Side whose code is absorbed into BlockBufferColli_Head via BIT skip pattern)
    // This points to the function that the code falls through to
    var emptyFunctionFallsTo: AssemblyFunction? = null

    // by Claude - Flag indicating this is an empty function that needs fall-through resolution
    var isEmptyFunction: Boolean = false
}

/**
 * Terminal subroutines are subroutines that don't return to the caller.
 * They use the return address on the stack for other purposes (like jump tables).
 */
private val TERMINAL_SUBROUTINES = setOf(
    "JumpEngine",  // Uses return address to index into jump table
)

/**
 * Check if a JSR instruction calls a terminal subroutine.
 */
fun isTerminalSubroutine(instruction: AssemblyInstruction): Boolean {
    val targetLabel = (instruction.address as? AssemblyAddressing.Direct)?.label
        ?: (instruction.address as? AssemblyAddressing.DirectX)?.label
        ?: (instruction.address as? AssemblyAddressing.DirectY)?.label
        ?: return false
    return targetLabel in TERMINAL_SUBROUTINES
}

/**
 * Organizes lines into linear blocks, where instructions are guaranteed to execute together.
 * All labels with an instruction immediately following become blocks, regardless of whether they are referenced or not.
 * In other words, reachability is not a concern.
 * Populates `enteredFrom`, `defaultExit`, and `alternateExit` on the blocks.
 */
fun List<AssemblyLine>.blockify(): List<AssemblyBlock> {
    val blocks = ArrayList<AssemblyBlock>()
    var currentBlock = ArrayList<AssemblyLine>()
    var previousBlock: AssemblyBlock? = null
    for (line in this) {
        if (line.label != null) {
            if (currentBlock.isNotEmpty()) {
                blocks.add(AssemblyBlock(currentBlock).also {
                    previousBlock?.let { pb ->
                        // Bidirectional delegate automatically updates it.enteredFrom
                        pb.fallThroughExit = it
                    }
                    previousBlock = it
                })
                currentBlock = ArrayList()
            }
        }
        currentBlock.add(line)
        when {
            line.instruction?.op?.isBranch == true -> {
                blocks.add(AssemblyBlock(currentBlock).also {
                    previousBlock?.let { pb ->
                        // Bidirectional delegate automatically updates it.enteredFrom
                        pb.fallThroughExit = it
                    }
                    previousBlock = it
                })
                currentBlock = ArrayList()
            }

            line.instruction?.op == AssemblyOp.JMP -> {
                blocks.add(AssemblyBlock(currentBlock).also {
                    previousBlock?.let { pb ->
                        // Bidirectional delegate automatically updates it.enteredFrom
                        pb.fallThroughExit = it
                    }
                    // The next found block isn't connected to this one.
                    previousBlock = null
                })
                currentBlock = ArrayList()
            }

            line.instruction?.op == AssemblyOp.RTS || line.instruction?.op == AssemblyOp.RTI -> {
                blocks.add(AssemblyBlock(currentBlock).also {
                    previousBlock?.let { pb ->
                        // Bidirectional delegate automatically updates it.enteredFrom
                        pb.fallThroughExit = it
                    }
                    // The next found block isn't connected to this one.
                    previousBlock = null
                })
                currentBlock = ArrayList()
            }

            // JSR to terminal subroutines (like JumpEngine) that don't return to caller
            line.instruction?.op == AssemblyOp.JSR && isTerminalSubroutine(line.instruction) -> {
                blocks.add(AssemblyBlock(currentBlock).also {
                    previousBlock?.let { pb ->
                        // Bidirectional delegate automatically updates it.enteredFrom
                        pb.fallThroughExit = it
                    }
                    // For JumpEngine, we need to preserve fallthrough to the dispatch table
                    // so that functionify() can scan the .dw entries to find function targets.
                    // For other terminal subroutines, no fallthrough.
                    val targetLabel = (line.instruction.address as? AssemblyAddressing.Direct)?.label
                    previousBlock = if (targetLabel == "JumpEngine") it else null
                })
                currentBlock = ArrayList()
            }

            else -> {
                // We don't assemble lines that aren't instructions or labels into blocks.
            }
        }
    }
    if (currentBlock.isNotEmpty()) {
        blocks.add(AssemblyBlock(currentBlock).also {
            previousBlock?.let { pb ->
                // Bidirectional delegate automatically updates it.enteredFrom
                pb.fallThroughExit = it
            }
        })
    }
    val blocksByLabel = blocks.associateBy { it.label }
    for (block in blocks) {
        val last = block.lines.lastOrNull { it.instruction != null } ?: continue
        val lastInstruction = last.instruction!!
        val op = lastInstruction.op
        when {
            op.isBranch -> {
                val label = lastInstruction.addressAsLabel.label
                val otherBlock = blocksByLabel[label]
                if (otherBlock != null) {
                    // Bidirectional delegate automatically updates otherBlock.enteredFrom
                    block.branchExit = otherBlock
                }
                // If label not found, branch points to external code - leave branchExit null
            }

            op == AssemblyOp.JMP -> {
                val addr = lastInstruction.address
                if (addr is AssemblyAddressing.Direct) {
                    val otherBlock = blocksByLabel[addr.label]
                    if (otherBlock != null) {
                        // Bidirectional delegate automatically updates otherBlock.enteredFrom
                        block.branchExit = otherBlock
                    }
                }
            }
        }
    }
    return blocks
}

/**
 * Assembles a dominator tree into the blocks, populating `immediateDominator` and `dominates`.
 */
fun List<AssemblyBlock>.dominators() {
    if (this.isEmpty()) return

    val indexOf = HashMap<AssemblyBlock, Int>(this.size)
    for ((i, b) in this.withIndex()) indexOf[b] = i

    val n = this.size
    val preds = Array(n) { mutableListOf<Int>() }
    val succs = Array(n) { mutableListOf<Int>() }

    // Build preds from enteredFrom (or keep your existing successor-based fill)
    for (i in 0 until n) {
        val b = this[i]
        for (p in b.enteredFrom) {
            val pi = indexOf[p] ?: continue
            preds[i].add(pi)
        }
        b.fallThroughExit?.let { t -> indexOf[t]?.let { succs[i].add(it) } }
        b.branchExit?.let { t -> indexOf[t]?.let { succs[i].add(it) } }
    }

    // Identify all real roots (no predecessors)
    val roots = (0 until n).filter { preds[it].isEmpty() }

    // If there’s only one root, we can use it directly; otherwise add a synthetic super-root
    val hasSuperRoot = roots.size > 1
    val total = if (hasSuperRoot) n + 1 else n
    val entry = if (hasSuperRoot) n else roots.firstOrNull() ?: 0

    val P = Array(total) { mutableListOf<Int>() }
    val S = Array(total) { mutableListOf<Int>() }

    // Copy original graph
    for (i in 0 until n) {
        P[i].addAll(preds[i])
        S[i].addAll(succs[i])
    }

    // Super-root edges
    if (hasSuperRoot) {
        for (r in roots) {
            P[r].add(entry)
            S[entry].add(r)
        }
    }

    // RPO from entry over extended graph
    val visited = BooleanArray(total)
    val rpoList = ArrayList<Int>(total)
    fun dfs(u: Int) {
        if (visited[u]) return
        visited[u] = true
        for (v in S[u]) dfs(v)
        rpoList.add(u)
    }
    dfs(entry)
    rpoList.reverse()

    val rpoIndex = IntArray(total) { Int.MAX_VALUE }
    for (i in rpoList.indices) rpoIndex[rpoList[i]] = i

    val idom = IntArray(total) { -1 }
    idom[entry] = entry

    fun intersect(a0: Int, b0: Int): Int {
        var a = a0
        var b = b0
        while (a != b) {
            while (rpoIndex[a] < rpoIndex[b]) b = idom[b]
            while (rpoIndex[b] < rpoIndex[a]) a = idom[a]
        }
        return a
    }

    var changed = true
    while (changed) {
        changed = false
        for (b in rpoList) {
            if (b == entry) continue
            var newIdom = -1
            for (p in P[b]) {
                if (!visited[p]) continue
                if (idom[p] != -1) { newIdom = p; break }
            }
            if (newIdom == -1) continue
            for (p in P[b]) {
                if (!visited[p]) continue
                if (p == newIdom) continue
                if (idom[p] != -1) newIdom = intersect(newIdom, p)
            }
            if (idom[b] != newIdom) { idom[b] = newIdom; changed = true }
        }
    }

    // No need to clear dominates - the bidirectional delegate handles cleanup when we reassign immediateDominator

    // Write results back to blocks, skipping the synthetic node
    for (i in 0 until n) {
        val block = this[i]
        if (!visited[i]) {
            block.immediateDominator = null
            continue
        }

        val isRealRoot = preds[i].isEmpty()
        val parent = idom[i]

        // Bidirectional delegate automatically updates dominates when we set immediateDominator
        block.immediateDominator = when {
            isRealRoot -> null
            parent == -1 -> null
            hasSuperRoot && parent == entry -> null // dominated only by super-root => real root
            parent == i -> null
            else -> this[parent]
        }
    }
}

/**
 * Identifies blocks that are functions and figures out their inputs and outputs.
 * Populates `inputs` and `outputs` on the functions.
 */
fun List<AssemblyBlock>.functionify(
    labelIsVirtualRegister: (String) -> Boolean = {
        it.startsWith('$') && it.substring(1).toUShortOrNull(16)?.let { it <= 0x7u } == true
    }
): List<AssemblyFunction> {
    if (this.isEmpty()) return emptyList()

    val functions = mutableListOf<AssemblyFunction>()

    // Step 1: Identify function entry points
    // - First block is always a function
    // - Any block that is the target of a JSR is a function
    // - Any block that is a target in a JumpEngine dispatch table is a function
    val functionEntryBlocks = HashMap<AssemblyBlock, ArrayList<AssemblyLine>>()
    functionEntryBlocks.put(this[0], ArrayList()) // Entry point

    // Build a map from label to block for fast lookup
    val labelToBlock = this.filter { it.label != null }.associateBy { it.label }

    // by Claude - Collect JMP targets as candidates, but don't immediately mark them as functions
    // We'll filter them after determining which are already reachable from JSR-based functions
    // Also track which block each JMP comes from so we can check if both are in the same loop
    val jmpTargetCandidates = mutableSetOf<AssemblyBlock>()
    val jmpSources = mutableMapOf<AssemblyBlock, MutableSet<AssemblyBlock>>() // target -> set of source blocks

    // by Claude - Track loop regions defined by backward BRANCHES (conditional)
    // A loop region spans from the backward branch target (loop header) to the source block (loop tail)
    // We use (headerLineIndex, tailLineIndex) pairs to define loop regions
    data class LoopRegion(val headerIdx: Int, val tailIdx: Int)
    val loopRegions = mutableListOf<LoopRegion>()

    for (block in this) {
        // by Claude - Check for backward branches (conditional branches that jump backward)
        val lastInstr = block.lines.lastOrNull { it.instruction != null }?.instruction
        if (lastInstr != null && lastInstr.op.isBranch) {
            val targetLabel = (lastInstr.address as? AssemblyAddressing.Direct)?.label
            val targetBlock = targetLabel?.let { labelToBlock[it] }
            if (targetBlock != null && block.originalLineIndex > targetBlock.originalLineIndex) {
                // This is a backward branch: block branches back to targetBlock
                // The loop region is from targetBlock (header) to block (tail)
                loopRegions.add(LoopRegion(targetBlock.originalLineIndex, block.originalLineIndex))
            }
        }

        for ((index, line) in block.lines.withIndex()) {
            // by Claude - Collect JMP targets as candidates for later filtering
            // Track which block the JMP comes from so we can check loop membership
            // IMPORTANT: Skip backward JMPs - those are loop jumps, not function calls
            if (line.instruction?.op == AssemblyOp.JMP) {
                val addr = line.instruction.address
                if (addr is AssemblyAddressing.Direct) {
                    val targetLabel = addr.label
                    val targetBlock = labelToBlock[targetLabel]
                    // Only consider as candidate if:
                    // - not already in the current block's fall-through path
                    // - not a backward jump (backward JMPs form loops, not function calls)
                    val isBackwardJmp = targetBlock != null &&
                        targetBlock.originalLineIndex <= block.originalLineIndex
                    if (targetBlock != null && targetBlock != block.fallThroughExit && !isBackwardJmp) {
                        jmpTargetCandidates.add(targetBlock)
                        jmpSources.getOrPut(targetBlock) { mutableSetOf() }.add(block)
                    }
                }
            }

            if (line.instruction?.op == AssemblyOp.JSR) {
                val targetLabel = (line.instruction.address as AssemblyAddressing.Direct).label
                val targetBlock = labelToBlock[targetLabel] ?: throw IllegalArgumentException("Could not find block with label $targetLabel")
                functionEntryBlocks.getOrPut(targetBlock) { ArrayList() }.add(line)

                // Check if this is a JumpEngine call - if so, the following DATA lines
                // contain function references that should also be function entry points
                if (targetLabel == "JumpEngine") {
                    // Scan following lines in this block and possibly subsequent blocks
                    // to collect the DATA entries with function names
                    var lineIdx = index + 1
                    var scanBlock = block

                    while (true) {
                        // If we've exhausted lines in current block, move to fall-through
                        if (lineIdx >= scanBlock.lines.size) {
                            scanBlock = scanBlock.fallThroughExit ?: break
                            lineIdx = 0
                        }

                        val dataLine = scanBlock.lines[lineIdx]
                        val data = dataLine.data

                        if (data is AssemblyData.Db) {
                            // Extract function names from Expr items
                            for (item in data.items) {
                                if (item is AssemblyData.DbItem.Expr) {
                                    // expr is like "TitleScreenMode" or "<TitleScreenMode" or ">TitleScreenMode"
                                    val funcName = item.expr
                                        .removePrefix("<")
                                        .removePrefix(">")
                                        .trim()
                                    if (funcName.isNotEmpty() && funcName.first().isLetter()) {
                                        val funcBlock = labelToBlock[funcName]
                                        if (funcBlock != null) {
                                            // Mark this as a function entry point (called via JumpEngine)
                                            functionEntryBlocks.getOrPut(funcBlock) { ArrayList() }.add(line)
                                        }
                                    }
                                }
                            }
                            lineIdx++
                        } else if (dataLine.instruction != null || dataLine.label != null) {
                            // Hit code or a new label - stop collecting
                            break
                        } else {
                            lineIdx++
                        }
                    }
                }
            }
        }
    }

    // by Claude - Selectively promote JMP targets to functions
    // This handles shared utility routines like IncSubtask that are reached via JMP from multiple functions.
    // However, we DON'T promote JMP targets where ALL JMP sources are in the same loop region as the target.
    // This preserves loop structures like ProcessCannons where internal labels (Chk_BB, Next3Slt)
    // are jumped to from within the same loop.

    // by Claude - Helper function to find which loop region a block is in (if any)
    fun getLoopRegion(block: AssemblyBlock): LoopRegion? {
        val blockIdx = block.originalLineIndex
        return loopRegions.find { region ->
            blockIdx in region.headerIdx..region.tailIdx
        }
    }

    for (candidate in jmpTargetCandidates) {
        // Skip if already a function entry (via JSR)
        if (candidate in functionEntryBlocks) continue

        // by Claude - Check if ALL JMP sources to this target are in the same loop as the target
        // If so, this is internal loop control flow and should NOT become a function
        val targetLoop = getLoopRegion(candidate)
        val sources = jmpSources[candidate] ?: emptySet()

        val allSourcesInSameLoop = if (targetLoop != null && sources.isNotEmpty()) {
            sources.all { source ->
                val sourceLoop = getLoopRegion(source)
                // Source is in the same loop if it's in the exact same loop region
                sourceLoop != null && sourceLoop.headerIdx == targetLoop.headerIdx && sourceLoop.tailIdx == targetLoop.tailIdx
            }
        } else {
            false
        }

        if (allSourcesInSameLoop) {
            continue // Don't promote internal loop blocks to functions
        }

        // by Claude - Check if this block has non-JMP entries (fall-through from other blocks).
        // If a block is reachable via fall-through, it's likely a join point within a function
        // (like the end of an if-then-else), not a separate function.
        val hasNonJmpEntry = candidate.enteredFrom.any { entry ->
            // Check if entry falls through to candidate
            entry.fallThroughExit == candidate
        }

        if (hasNonJmpEntry) {
            continue // Don't promote join points to functions
        }

        functionEntryBlocks.getOrPut(candidate) { ArrayList() }
    }

    // by Claude - Step 2a: First pass - identify which blocks are reachable from multiple functions.
    // These "contested" blocks should be promoted to their own functions.
    val blockReachableFrom = mutableMapOf<AssemblyBlock, MutableSet<AssemblyBlock>>() // block -> set of function entry blocks that can reach it

    // Sort by line number for deterministic processing
    val sortedFunctionEntries = functionEntryBlocks.entries.sortedBy { it.key.originalLineIndex }

    for ((entryBlock, _) in sortedFunctionEntries) {
        // Find all blocks reachable from this function entry
        val toVisit = mutableListOf(entryBlock)
        val visited = mutableSetOf<AssemblyBlock>()

        while (toVisit.isNotEmpty()) {
            val current = toVisit.removeAt(0)
            if (current in visited) continue
            visited.add(current)

            // Stop at other function entries (they're handled separately)
            if (current in functionEntryBlocks && current != entryBlock) {
                continue
            }

            // Record that this block is reachable from entryBlock
            blockReachableFrom.getOrPut(current) { mutableSetOf() }.add(entryBlock)

            // Continue traversal
            current.fallThroughExit?.let { if (it !in visited) toVisit.add(it) }
            current.branchExit?.let { if (it !in visited) toVisit.add(it) }
        }
    }

    // Promote contested blocks to their own functions
    val promotedBlocks = mutableSetOf<AssemblyBlock>()
    for ((block, reachableFromSet) in blockReachableFrom) {
        if (reachableFromSet.size > 1 && block !in functionEntryBlocks) {
            // This block is reachable from multiple functions - promote it
            functionEntryBlocks.getOrPut(block) { ArrayList() }
            promotedBlocks.add(block)
        }
    }

    // Re-sort after promotions
    val finalSortedFunctionEntries = functionEntryBlocks.entries.sortedBy { it.key.originalLineIndex }

    // Step 2b: For each function, compute its blocks using dominator tree
    for ((entryBlock, callers) in finalSortedFunctionEntries) {
        val function = AssemblyFunction(entryBlock, callers)

        // Find all blocks that belong to this function
        // A block belongs to a function if:
        // - It's reachable from the entry block
        // - It's not another function's entry block (unless it's dominated by this entry)
        val functionBlocks = mutableSetOf<AssemblyBlock>()
        val toVisit = mutableListOf(entryBlock)
        val visited = mutableSetOf<AssemblyBlock>()

        // by Claude - Track blocks that end with .db $2c (BIT skip pattern)
        // When a block ends with .db $2c, the fall-through target's first instruction is "skipped"
        // by the BIT instruction. We should continue traversal in this case to include the target's
        // code in this function, since the behavior is different when called directly vs via BIT skip.
        fun blockEndsWith2c(block: AssemblyBlock): Boolean {
            return block.lines.any { line ->
                val data = line.data
                data is AssemblyData.Db && data.items.size == 1 &&
                data.items[0] is AssemblyData.DbItem.ByteValue &&
                (data.items[0] as AssemblyData.DbItem.ByteValue).value == 0x2C
            }
        }

        while (toVisit.isNotEmpty()) {
            val current = toVisit.removeAt(0)
            if (current in visited) continue
            visited.add(current)

            // Stop if we hit another function entry (unless it's the current function)
            // Exception: If we arrived via a .db $2c BIT skip pattern, continue traversal
            // because the BIT instruction "eats" the first instruction of the target function.
            if (current in functionEntryBlocks && current != entryBlock) {
                // Check if we got here via a BIT skip from a block already in this function
                val arrivedViaBitSkip = functionBlocks.any { block ->
                    blockEndsWith2c(block) && block.fallThroughExit == current
                }
                if (!arrivedViaBitSkip) {
                    continue
                }
            }

            // by Claude - Check if we arrived via a BIT skip pattern from any block in this function
            // This applies to ALL blocks, not just function entries
            // The skip is recorded per-function so that a block can have different behavior
            // when decompiled as part of different functions
            val arrivedViaBitSkip = functionBlocks.any { block ->
                blockEndsWith2c(block) && block.fallThroughExit == current
            }
            if (arrivedViaBitSkip) {
                // Mark this block to skip its first instruction for THIS function only
                // because the BIT instruction "eats" the next 2 bytes (first instruction)
                current.skipFirstInstructionForFunctions.add(function)
            }

            // by Claude - If this block is already owned by a different function,
            // don't include it in this function's blocks. This happens when multiple
            // functions share common code via branches. The shared code stays with
            // whoever claimed it first, and other functions will generate calls to it.
            if (current.function != null && current.function != function) {
                continue
            }

            functionBlocks.add(current)
            current.function = function

            // Add successors
            current.fallThroughExit?.let { if (it !in visited) toVisit.add(it) }
            current.branchExit?.let { if (it !in visited) toVisit.add(it) }
        }

        // Step 3: Analyze data flow to determine inputs and outputs
        val inputs = mutableSetOf<TrackedAsIo>()
        val outputs = mutableSetOf<TrackedAsIo>()
        val defined = mutableSetOf<TrackedAsIo>() // States that have been written

        // Track which states are defined before being used
        for (block in functionBlocks) {
            for (line in block.lines) {
                val instruction = line.instruction ?: continue
                val op = instruction.op

                // Check if instruction consumes data
                op.reads(instruction.address?.let {it::class })
                    .mapNotNull { it.toTrackedAsIo(instruction.address, labelIsVirtualRegister) }
                    .forEach { state ->
                        if(state !in defined) inputs.add(state)
                    }

                // Also check if addressing mode uses X or Y registers
                when (instruction.address) {
                    is AssemblyAddressing.DirectX,
                    is AssemblyAddressing.IndirectX -> {
                        if (TrackedAsIo.X !in defined) inputs.add(TrackedAsIo.X)
                    }
                    is AssemblyAddressing.DirectY,
                    is AssemblyAddressing.IndirectY -> {
                        if (TrackedAsIo.Y !in defined) inputs.add(TrackedAsIo.Y)
                    }
                    else -> {
                        // Other addressing modes don't use X or Y
                    }
                }

                op.modifies(instruction.address?.let {it::class })
                    .mapNotNull { it.toTrackedAsIo(instruction.address, labelIsVirtualRegister) }
                    .forEach { state ->
                        defined.add(state)
                    }
            }
        }
        function.inputs = inputs
        function.clobbers = defined.toSet()
        function.blocks = functionBlocks  // by Claude - Store blocks for transitive input analysis

        // by Claude - Mark "empty functions" whose starting block is owned by another function
        // This happens with the BIT skip pattern (e.g., BlockBufferColli_Side).
        // We'll resolve the actual fall-through target after all functions are created.
        if (functionBlocks.isEmpty() && entryBlock.function != null && entryBlock.function != function) {
            // Mark this as an empty function - will be resolved later
            function.isEmptyFunction = true
        }

        // Analyze function outputs by looping through callers. A state is an output if:
        // - This function can define it, AND
        // - At least one caller reads that state immediately after the call before redefining it.
        run {
            val candidateDefs = function.clobbers ?: emptySet()
            val detectedOutputs = mutableSetOf<TrackedAsIo>()

            fun trackUse(state: TrackedAsIo, killed: Set<TrackedAsIo>) {
                if (state in candidateDefs && state !in killed) detectedOutputs.add(state)
            }

            for (caller in callers) {
                val block = caller.block ?: continue
                val idx = block.lines.indexOf(caller)
                if (idx == -1) continue

                val killed = mutableSetOf<TrackedAsIo>()
                // Scan forward within the same block after the JSR to see what the caller uses
                for (i in (idx + 1) until block.lines.size) {
                    val line = block.lines[i]
                    val instruction = line.instruction ?: continue
                    val op = instruction.op

                    // Uses: consumed flag
                    op.reads(instruction.address?.let {it::class })
                        .mapNotNull { it.toTrackedAsIo(instruction.address, labelIsVirtualRegister) }
                        .forEach { state ->
                            trackUse(state, killed)
                        }

                    // by Claude - CRITICAL FIX: Also track indexed addressing mode register uses
                    // When LDA addr,X is used, X is read but not included in op.reads()
                    when (instruction.address) {
                        is AssemblyAddressing.DirectX,
                        is AssemblyAddressing.IndirectX -> {
                            trackUse(TrackedAsIo.X, killed)
                        }
                        is AssemblyAddressing.DirectY,
                        is AssemblyAddressing.IndirectY -> {
                            trackUse(TrackedAsIo.Y, killed)
                        }
                        else -> {}
                    }

                    op.modifies(instruction.address?.let {it::class })
                        .mapNotNull { it.toTrackedAsIo(instruction.address, labelIsVirtualRegister) }
                        .forEach { state ->
                            killed.add(state)
                        }
                }

                // by Claude - CRITICAL FIX: Also scan fall-through blocks after the JSR
                // When a block ends with a JSR and the next block starts with a label,
                // the fall-through block may use registers that were set by the called function.
                // Example: jsr DividePDiff / YLdBData: lda YOffscreenBitsData,x
                // The X register is used in YLdBData but is in a different block.
                var currentBlock: AssemblyBlock? = block.fallThroughExit
                val maxFallThroughBlocks = 5  // Limit to avoid infinite loops
                var fallThroughCount = 0

                while (currentBlock != null && fallThroughCount < maxFallThroughBlocks) {
                    fallThroughCount++
                    var foundTerminator = false

                    for (line in currentBlock.lines) {
                        val instruction = line.instruction ?: continue
                        val op = instruction.op

                        // Track uses before checking for terminator
                        op.reads(instruction.address?.let { it::class })
                            .mapNotNull { it.toTrackedAsIo(instruction.address, labelIsVirtualRegister) }
                            .forEach { state ->
                                trackUse(state, killed)
                            }

                        // by Claude - Also track indexed addressing mode register uses in fall-through
                        when (instruction.address) {
                            is AssemblyAddressing.DirectX,
                            is AssemblyAddressing.IndirectX -> {
                                trackUse(TrackedAsIo.X, killed)
                            }
                            is AssemblyAddressing.DirectY,
                            is AssemblyAddressing.IndirectY -> {
                                trackUse(TrackedAsIo.Y, killed)
                            }
                            else -> {}
                        }

                        op.modifies(instruction.address?.let { it::class })
                            .mapNotNull { it.toTrackedAsIo(instruction.address, labelIsVirtualRegister) }
                            .forEach { state ->
                                killed.add(state)
                            }

                        // Stop at branch, jump, or return instructions
                        if (op.isBranch || op == AssemblyOp.JMP || op == AssemblyOp.RTS || op == AssemblyOp.RTI) {
                            foundTerminator = true
                            break
                        }
                    }

                    // Stop if we found a terminator or no fall-through
                    if (foundTerminator) break
                    currentBlock = currentBlock.fallThroughExit
                }
            }

            function.outputs = detectedOutputs
        }

        // Build a simple control list for this function
        function.analyzeControls()

        functions.add(function)
    }

    // by Claude - Step 4: Propagate transitive inputs from JSR calls
    // When function A calls function B, A inherits B's inputs (unless A already defines them before the call)
    // Use fixpoint iteration since call chains can be arbitrarily deep
    val labelToFunction = functions
        .filter { func -> func.startingBlock.label != null }
        .associateBy { func -> func.startingBlock.label!! }

    // by Claude - Resolve emptyFunctionFallsTo for empty functions
    // Now that all functions are created and labelToFunction is built, we can find the target function
    for (func in functions) {
        if (func.isEmptyFunction) {
            // Find the fall-through target by looking at the starting block's fall-through
            val fallThroughBlock = func.startingBlock.fallThroughExit
            val targetLabel = fallThroughBlock?.label
            val targetFunction = if (targetLabel != null) labelToFunction[targetLabel] else null
            if (targetFunction != null && targetFunction != func) {
                func.emptyFunctionFallsTo = targetFunction
            }
        }
    }

    var changed = true
    var iterations = 0
    val maxIterations = 100 // Prevent infinite loops in case of cycles

    while (changed && iterations < maxIterations) {
        changed = false
        iterations++

        for (func in functions) {
            // by Claude - Handle "empty functions" first
            // These are functions whose starting block is owned by another function (via BIT skip pattern)
            // They should inherit inputs from the target function EXCEPT for registers set by their code
            val fallsToFunc = func.emptyFunctionFallsTo
            if (fallsToFunc != null) {
                // Find which registers are set by the starting block's code
                val definedByBlock = mutableSetOf<TrackedAsIo>()
                for (line in func.startingBlock.lines) {
                    val instr = line.instruction ?: continue
                    when (instr.op) {
                        AssemblyOp.LDA -> {
                            if (instr.address is AssemblyAddressing.Value) {
                                definedByBlock.add(TrackedAsIo.A)
                            }
                        }
                        AssemblyOp.LDX -> {
                            if (instr.address is AssemblyAddressing.Value) {
                                definedByBlock.add(TrackedAsIo.X)
                            }
                        }
                        AssemblyOp.LDY -> {
                            if (instr.address is AssemblyAddressing.Value) {
                                definedByBlock.add(TrackedAsIo.Y)
                            }
                        }
                        else -> {}
                    }
                }

                val currentInputs = func.inputs?.toMutableSet() ?: mutableSetOf()
                val targetInputs = fallsToFunc.inputs ?: emptySet()
                for (input in targetInputs) {
                    // Only inherit input if NOT defined by the block's code
                    if (input !in currentInputs && input !in definedByBlock) {
                        currentInputs.add(input)
                        changed = true
                    }
                }
                if (currentInputs != func.inputs) {
                    func.inputs = currentInputs
                }
                continue  // Skip normal processing for empty functions
            }

            val funcBlocks = func.blocks
            if (funcBlocks == null || funcBlocks.isEmpty()) continue
            val currentInputs = func.inputs?.toMutableSet() ?: mutableSetOf()
            val definedBeforeCall = mutableSetOf<TrackedAsIo>()

            for (block in funcBlocks) {
                for (line in block.lines) {
                    val instr = line.instruction ?: continue

                    // Track what's been defined before each JSR or JMP to a function
                    // by Claude - CRITICAL FIX: Also handle JMP (tail calls) for input propagation
                    // When function A tail-calls function B via JMP, A needs B's inputs because
                    // B will use the register values that A had when it jumped.
                    // Example: imposeGravitySprObj JMPs to imposeGravity which uses X for indexing
                    if (instr.op == AssemblyOp.JSR || instr.op == AssemblyOp.JMP) {
                        // Get the called function
                        val targetLabel = (instr.address as? AssemblyAddressing.Direct)?.label
                        val calledFunction = if (targetLabel != null) labelToFunction[targetLabel] else null

                        if (calledFunction != null) {
                            val calleeInputs = calledFunction.inputs ?: emptySet()
                            // Add callee's inputs to caller's inputs if not already defined
                            for (input in calleeInputs) {
                                if (input !in definedBeforeCall && input !in currentInputs) {
                                    currentInputs.add(input)
                                    changed = true
                                }
                            }
                        }
                    }

                    // Update what's defined after this instruction
                    val addrClass = instr.address?.let { addr -> addr::class }
                    instr.op.modifies(addrClass)
                        .mapNotNull { aff -> aff.toTrackedAsIo(instr.address, labelIsVirtualRegister) }
                        .forEach { state -> definedBeforeCall.add(state) }
                }

                // by Claude - Also check for fall-through to another function at the end of this block
                // If this block falls through to another function's entry, we need that function's inputs
                val fallThrough = block.fallThroughExit
                if (fallThrough != null) {
                    val fallThroughFunction = labelToFunction[fallThrough.label]
                    if (fallThroughFunction != null && fallThroughFunction != func) {
                        // This block falls through to another function - inherit its inputs
                        val calleeInputs = fallThroughFunction.inputs ?: emptySet()
                        for (input in calleeInputs) {
                            if (input !in definedBeforeCall && input !in currentInputs) {
                                currentInputs.add(input)
                                changed = true
                            }
                        }
                    }
                }
            }

            if (currentInputs != func.inputs) {
                func.inputs = currentInputs
            }
        }
    }

    // by Claude - Bug #11 fix: Step 5: Propagate transitive clobbers through function calls
    // When function A calls function B (JSR) or tail-calls B (JMP), A inherits B's clobbers
    // This is needed so output detection works for functions that only contain calls
    changed = true
    iterations = 0

    while (changed && iterations < maxIterations) {
        changed = false
        iterations++

        for (func in functions) {
            // by Claude - Handle "empty functions" first for clobbers propagation
            val fallsToFunc = func.emptyFunctionFallsTo
            if (fallsToFunc != null) {
                val targetClobbers = fallsToFunc.clobbers ?: emptySet()
                val currentClobbers = func.clobbers?.toMutableSet() ?: mutableSetOf()
                val originalSize = currentClobbers.size
                currentClobbers.addAll(targetClobbers)
                if (currentClobbers.size != originalSize) {
                    func.clobbers = currentClobbers
                    changed = true
                }
                continue  // Skip normal processing for empty functions
            }

            val funcBlocks = func.blocks
            if (funcBlocks == null || funcBlocks.isEmpty()) continue
            val currentClobbers = func.clobbers?.toMutableSet() ?: mutableSetOf()
            val originalSize = currentClobbers.size

            for (block in funcBlocks) {
                for (line in block.lines) {
                    val instr = line.instruction ?: continue

                    // For JSR and JMP, inherit callee's clobbers
                    if (instr.op == AssemblyOp.JSR || instr.op == AssemblyOp.JMP) {
                        val targetLabel = (instr.address as? AssemblyAddressing.Direct)?.label
                        val calledFunction = if (targetLabel != null) labelToFunction[targetLabel] else null

                        if (calledFunction != null) {
                            val calleeClobbers = calledFunction.clobbers ?: emptySet()
                            currentClobbers.addAll(calleeClobbers)
                        }
                    }
                }
            }

            if (currentClobbers.size != originalSize) {
                func.clobbers = currentClobbers
                changed = true
            }
        }
    }

    // by Claude - Bug #11 fix: Step 6: Re-analyze outputs with updated clobbers
    // Now that clobbers include transitive effects, re-detect outputs
    // by Claude - CRITICAL FIX: This second pass must include fall-through scanning and indexed addressing
    // tracking, otherwise it will overwrite the first output detection without those improvements
    for (func in functions) {
        val candidateDefs = func.clobbers ?: emptySet()
        val detectedOutputs = mutableSetOf<TrackedAsIo>()

        fun trackUse(state: TrackedAsIo, killed: Set<TrackedAsIo>) {
            if (state in candidateDefs && state !in killed) detectedOutputs.add(state)
        }

        for (caller in func.callers) {
            val block = caller.block ?: continue
            val idx = block.lines.indexOf(caller)
            if (idx == -1) continue

            val killed = mutableSetOf<TrackedAsIo>()
            // Scan forward within the same block after the JSR to see what the caller uses
            for (i in (idx + 1) until block.lines.size) {
                val line = block.lines[i]
                val instruction = line.instruction ?: continue
                val op = instruction.op

                op.reads(instruction.address?.let { it::class })
                    .mapNotNull { it.toTrackedAsIo(instruction.address, labelIsVirtualRegister) }
                    .forEach { state ->
                        trackUse(state, killed)
                    }

                // by Claude - Track indexed addressing mode register uses
                when (instruction.address) {
                    is AssemblyAddressing.DirectX,
                    is AssemblyAddressing.IndirectX -> {
                        trackUse(TrackedAsIo.X, killed)
                    }
                    is AssemblyAddressing.DirectY,
                    is AssemblyAddressing.IndirectY -> {
                        trackUse(TrackedAsIo.Y, killed)
                    }
                    else -> {}
                }

                // by Claude - JMP to a function = that function's inputs are implicitly read
                // When we see `jsr Helper; jmp Target`, the JMP "uses" Target's inputs
                // because control transfers to Target with whatever register values exist
                // Example: jsr GetProperObjOffset; jmp GetOffScreenBitsSet
                // GetOffScreenBitsSet uses X, so GetProperObjOffset's X modification is an output
                if (op == AssemblyOp.JMP) {
                    val targetLabel = (instruction.address as? AssemblyAddressing.Direct)?.label
                    val targetFunction = if (targetLabel != null) labelToFunction[targetLabel] else null
                    if (targetFunction != null) {
                        val targetInputs = targetFunction.inputs ?: emptySet()
                        for (input in targetInputs) {
                            trackUse(input, killed)  // Treat JMP target's inputs as uses
                        }
                    }
                }

                op.modifies(instruction.address?.let { it::class })
                    .mapNotNull { it.toTrackedAsIo(instruction.address, labelIsVirtualRegister) }
                    .forEach { state ->
                        killed.add(state)
                    }
            }

            // by Claude - Scan fall-through blocks for output detection
            // When a block ends with a JSR and the next block starts with a label,
            // the fall-through block may use registers that were set by the called function.
            var currentBlock: AssemblyBlock? = block.fallThroughExit
            val maxFallThroughBlocks = 5
            var fallThroughCount = 0

            while (currentBlock != null && fallThroughCount < maxFallThroughBlocks) {
                fallThroughCount++
                var foundTerminator = false

                for (line in currentBlock.lines) {
                    val instruction = line.instruction ?: continue
                    val op = instruction.op

                    op.reads(instruction.address?.let { it::class })
                        .mapNotNull { it.toTrackedAsIo(instruction.address, labelIsVirtualRegister) }
                        .forEach { state ->
                            trackUse(state, killed)
                        }

                    when (instruction.address) {
                        is AssemblyAddressing.DirectX,
                        is AssemblyAddressing.IndirectX -> {
                            trackUse(TrackedAsIo.X, killed)
                        }
                        is AssemblyAddressing.DirectY,
                        is AssemblyAddressing.IndirectY -> {
                            trackUse(TrackedAsIo.Y, killed)
                        }
                        else -> {}
                    }

                    // by Claude - JMP to a function = that function's inputs are implicitly read
                    // (Same fix as above, for fall-through blocks)
                    if (op == AssemblyOp.JMP) {
                        val targetLabel = (instruction.address as? AssemblyAddressing.Direct)?.label
                        val targetFunction = if (targetLabel != null) labelToFunction[targetLabel] else null
                        if (targetFunction != null) {
                            val targetInputs = targetFunction.inputs ?: emptySet()
                            for (input in targetInputs) {
                                trackUse(input, killed)  // Treat JMP target's inputs as uses
                            }
                        }
                    }

                    op.modifies(instruction.address?.let { it::class })
                        .mapNotNull { it.toTrackedAsIo(instruction.address, labelIsVirtualRegister) }
                        .forEach { state ->
                            killed.add(state)
                        }

                    if (op.isBranch || op == AssemblyOp.JMP || op == AssemblyOp.RTS || op == AssemblyOp.RTI) {
                        foundTerminator = true
                        break
                    }
                }

                if (foundTerminator) break
                currentBlock = currentBlock.fallThroughExit
            }
        }

        func.outputs = detectedOutputs
    }

    // by Claude - Step 6.5: Detect pass-through inputs
    // When a register is an output but is NOT written on all paths to RTS,
    // that register must ALSO be an input (to preserve the value on early exit).
    // Example: DividePDiff outputs X (via TAX on main path) but early exit (BCS ExDivPD)
    // doesn't write X, so X must also be an input to preserve its value.
    for (func in functions) {
        val funcBlocks = func.blocks ?: continue
        val funcOutputs = func.outputs ?: continue
        if (funcOutputs.isEmpty()) continue

        val currentInputs = func.inputs?.toMutableSet() ?: mutableSetOf()
        val outputRegisters = funcOutputs.filterIsInstance<TrackedAsIo>()
            .filter { it == TrackedAsIo.X || it == TrackedAsIo.Y || it == TrackedAsIo.A }

        for (outputReg in outputRegisters) {
            // Check if this output is written on ALL paths to RTS
            // Use a worklist algorithm to track which paths write to the register

            // State: for each block, track whether the register has been written when entering that block
            // We need to check if there's any path from entry to RTS where register is NOT written

            // Track: (block, writtenSoFar) pairs - writtenSoFar is true if register was written on this path
            val visited = mutableMapOf<AssemblyBlock, MutableSet<Boolean>>() // block -> set of (written) states seen
            val worklist = ArrayDeque<Pair<AssemblyBlock, Boolean>>()
            worklist.add(func.startingBlock to false)

            var foundUnwrittenPath = false

            while (worklist.isNotEmpty() && !foundUnwrittenPath) {
                val (block, writtenBefore) = worklist.removeFirst()

                // Check if we've seen this state before
                val seenStates = visited.getOrPut(block) { mutableSetOf() }
                if (writtenBefore in seenStates) continue
                seenStates.add(writtenBefore)

                // Process this block - check if it writes to the register
                var writtenInBlock = writtenBefore
                var foundRts = false

                for (line in block.lines) {
                    val instr = line.instruction ?: continue

                    // Check if this instruction writes to our output register
                    val modifies = instr.op.modifies(instr.address?.let { it::class })
                        .mapNotNull { it.toTrackedAsIo(instr.address, labelIsVirtualRegister) }

                    if (outputReg in modifies) {
                        writtenInBlock = true
                    }

                    // Check for RTS/RTI
                    if (instr.op == AssemblyOp.RTS || instr.op == AssemblyOp.RTI) {
                        foundRts = true
                        if (!writtenInBlock) {
                            // Found a path to RTS that doesn't write the register!
                            foundUnwrittenPath = true
                        }
                        break
                    }

                    // Check for JMP to another function (tail call) - treat as RTS
                    if (instr.op == AssemblyOp.JMP) {
                        val targetLabel = (instr.address as? AssemblyAddressing.Direct)?.label
                        val targetFunction = if (targetLabel != null) labelToFunction[targetLabel] else null
                        if (targetFunction != null && targetFunction != func) {
                            // Tail call to another function - treat as exit point
                            if (!writtenInBlock) {
                                foundUnwrittenPath = true
                            }
                            break
                        }
                    }
                }

                if (foundRts || foundUnwrittenPath) continue

                // Add successors to worklist (only blocks within this function)
                block.fallThroughExit?.let { target ->
                    if (target in funcBlocks || target == func.startingBlock) {
                        worklist.add(target to writtenInBlock)
                    } else {
                        // Fall-through to another function's block - treat as exit
                        if (!writtenInBlock) {
                            foundUnwrittenPath = true
                        }
                    }
                }
                block.branchExit?.let { target ->
                    if (target in funcBlocks || target == func.startingBlock) {
                        worklist.add(target to writtenInBlock)
                    }
                }
            }

            // If any path to RTS doesn't write the register, it must be an input
            if (foundUnwrittenPath && outputReg !in currentInputs) {
                currentInputs.add(outputReg)
            }
        }

        func.inputs = currentInputs
    }

    // by Claude - Step 7: Propagate outputs through JMP chains
    // If function A outputs X and JMPs to function B, then B should also output X.
    // This is because when B returns via RTS, it returns to A's caller, which expects X.
    // Example: RunOffscrBitsSubs outputs A and JMPs to GetYOffscreenBits,
    // so GetYOffscreenBits should also output A.
    changed = true
    iterations = 0

    while (changed && iterations < maxIterations) {
        changed = false
        iterations++

        for (func in functions) {
            val funcBlocks = func.blocks ?: continue

            // Find all JMP targets within this function
            for (block in funcBlocks) {
                for (line in block.lines) {
                    val instr = line.instruction ?: continue
                    if (instr.op == AssemblyOp.JMP) {
                        val targetLabel = (instr.address as? AssemblyAddressing.Direct)?.label
                        val targetFunction = if (targetLabel != null) labelToFunction[targetLabel] else null

                        if (targetFunction != null && targetFunction != func) {
                            // This function JMPs to targetFunction
                            // targetFunction should inherit this function's outputs
                            val currentOutputs = func.outputs ?: emptySet()
                            val targetOutputs = targetFunction.outputs?.toMutableSet() ?: mutableSetOf()
                            val originalSize = targetOutputs.size

                            for (output in currentOutputs) {
                                // Only propagate if the target function clobbers this register
                                // (i.e., it could set this value)
                                if (output in (targetFunction.clobbers ?: emptySet())) {
                                    targetOutputs.add(output)
                                }
                            }

                            if (targetOutputs.size != originalSize) {
                                targetFunction.outputs = targetOutputs
                                changed = true
                            }
                        }
                    }
                }
            }
        }
    }

    // by Claude - Step 7b: Propagate outputs BACK from JMP targets and fall-throughs to source functions
    // If function A tail-calls (JMPs to or falls through to) function B, and B outputs X, then A should also output X.
    // This is because when someone calls A, they will eventually get B's return value.
    // Example: blockbuffercolliFeet falls through to blockbuffercolliHead which falls through to blockBufferCollision,
    // and blockBufferCollision outputs A, so both should also output A.
    changed = true
    iterations = 0

    while (changed && iterations < maxIterations) {
        changed = false
        iterations++

        for (func in functions) {
            // by Claude - Handle "empty functions" first for output propagation too
            val fallsToFunc = func.emptyFunctionFallsTo
            if (fallsToFunc != null) {
                val targetOutputs = fallsToFunc.outputs ?: emptySet()
                val currentOutputs = func.outputs?.toMutableSet() ?: mutableSetOf()
                val originalSize = currentOutputs.size
                for (output in targetOutputs) {
                    currentOutputs.add(output)
                }
                if (currentOutputs.size != originalSize) {
                    func.outputs = currentOutputs
                    changed = true
                }
                continue  // Skip normal processing for empty functions
            }

            val funcBlocks = func.blocks
            if (funcBlocks == null || funcBlocks.isEmpty()) continue

            // Find all JMP targets within this function (tail calls)
            for (block in funcBlocks) {
                for (line in block.lines) {
                    val instr = line.instruction ?: continue
                    if (instr.op == AssemblyOp.JMP) {
                        val targetLabel = (instr.address as? AssemblyAddressing.Direct)?.label
                        val targetFunction = if (targetLabel != null) labelToFunction[targetLabel] else null

                        if (targetFunction != null && targetFunction != func) {
                            // This function JMPs to targetFunction (tail call)
                            // If targetFunction outputs something, this function should too
                            val targetOutputs = targetFunction.outputs ?: emptySet()
                            val currentOutputs = func.outputs?.toMutableSet() ?: mutableSetOf()
                            val originalSize = currentOutputs.size

                            for (output in targetOutputs) {
                                currentOutputs.add(output)
                            }

                            if (currentOutputs.size != originalSize) {
                                func.outputs = currentOutputs
                                changed = true
                            }
                        }
                    }
                }

                // by Claude - Also check fall-through exits to other functions
                // If the last block falls through to another function, propagate that function's outputs back
                val fallThroughTarget = block.fallThroughExit
                if (fallThroughTarget != null) {
                    val targetFunction = labelToFunction[fallThroughTarget.label]
                    if (targetFunction != null && targetFunction != func) {
                        // This function falls through to targetFunction (tail call via fall-through)
                        val targetOutputs = targetFunction.outputs ?: emptySet()
                        val currentOutputs = func.outputs?.toMutableSet() ?: mutableSetOf()
                        val originalSize = currentOutputs.size

                        for (output in targetOutputs) {
                            currentOutputs.add(output)
                        }

                        if (currentOutputs.size != originalSize) {
                            func.outputs = currentOutputs
                            changed = true
                        }
                    }
                }
            }
        }
    }

    return functions
}

/**
 * Validates that this block's control flow graph references are bidirectionally consistent.
 * Throws IllegalStateException if inconsistencies are found.
 */
fun AssemblyBlock.validateConsistency() {
    // Check forward -> backward consistency for fallThroughExit
    fallThroughExit?.let { target ->
        check(this in target.enteredFrom) {
            "Block ${label ?: "@$originalLineIndex"} has fallThroughExit to ${target.label ?: "@${target.originalLineIndex}"}, " +
            "but this block is not in target's enteredFrom list"
        }
    }

    // Check forward -> backward consistency for branchExit
    branchExit?.let { target ->
        check(this in target.enteredFrom) {
            "Block ${label ?: "@$originalLineIndex"} has branchExit to ${target.label ?: "@${target.originalLineIndex}"}, " +
            "but this block is not in target's enteredFrom list"
        }
    }

    // Check backward -> forward consistency for enteredFrom
    for (pred in enteredFrom) {
        check(pred.fallThroughExit == this || pred.branchExit == this) {
            "Block ${pred.label ?: "@${pred.originalLineIndex}"} is in enteredFrom of ${label ?: "@$originalLineIndex"}, " +
            "but doesn't have this block as fallThroughExit or branchExit"
        }
    }

    // Check dominator consistency
    immediateDominator?.let { idom ->
        check(this in idom.dominates) {
            "Block ${label ?: "@$originalLineIndex"} has immediateDominator ${idom.label ?: "@${idom.originalLineIndex}"}, " +
            "but this block is not in dominator's dominates list"
        }
    }

    // Check dominates consistency
    for (dominated in dominates) {
        check(dominated.immediateDominator == this) {
            "Block ${dominated.label ?: "@${dominated.originalLineIndex}"} is in dominates list of ${label ?: "@$originalLineIndex"}, " +
            "but has different immediateDominator: ${dominated.immediateDominator?.label ?: "null"}"
        }
    }
}

/**
 * Validates consistency of all blocks in the list.
 * Useful for debugging and catching graph corruption early.
 */
fun List<AssemblyBlock>.validateAllConsistency() {
    forEach { it.validateConsistency() }
}

