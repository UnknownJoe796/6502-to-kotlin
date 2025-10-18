package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 21: Stack Frame Analysis
 * - Match PHA/PLA pairs for saved registers
 * - Track stack depth through execution
 * - Identify parameter passing patterns
 * - Detect local variables on stack
 * - Verify stack balance
 */

/**
 * A stack operation (push or pull)
 */
sealed class StackOp {
    data class Push(
        val lineRef: AssemblyLineReference,
        val value: StackValue,
        val blockLeader: Int
    ) : StackOp()

    data class Pull(
        val lineRef: AssemblyLineReference,
        val target: StackValue,
        val blockLeader: Int
    ) : StackOp()
}

/**
 * Value being pushed/pulled from stack
 */
sealed class StackValue {
    object RegisterA : StackValue() {
        override fun toString() = "A"
    }
    object Flags : StackValue() {
        override fun toString() = "P"
    }
    object Unknown : StackValue() {
        override fun toString() = "?"
    }
}

/**
 * A matched push/pull pair
 */
data class StackFrame(
    val pushOp: StackOp.Push,
    val pullOp: StackOp.Pull,
    val frameType: FrameType
)

enum class FrameType {
    REGISTER_SAVE,    // Saved at function entry, restored at exit
    PARAMETER,        // Pushed before JSR
    LOCAL_VARIABLE,   // Temporary storage within function
    RETURN_VALUE      // Pulled after RTS
}

/**
 * Stack slot at a specific depth from function entry
 */
data class StackSlot(
    val depth: Int,              // 0 = top of stack at entry
    val pushedValue: StackValue?,
    val pushedAt: AssemblyLineReference?,
    val pulledAt: AssemblyLineReference?
)

/**
 * Stack state at a program point
 */
data class StackState(
    val depth: Int,              // Current depth (0 = function entry level)
    val slots: List<StackSlot>   // Stack contents from entry
) {
    /**
     * Push a value onto the stack
     */
    fun push(value: StackValue, lineRef: AssemblyLineReference): StackState {
        val newSlot = StackSlot(
            depth = depth,
            pushedValue = value,
            pushedAt = lineRef,
            pulledAt = null
        )
        return StackState(
            depth = depth + 1,
            slots = slots + newSlot
        )
    }

    /**
     * Pull a value from the stack
     */
    fun pull(lineRef: AssemblyLineReference): Pair<StackState, StackSlot?> {
        if (depth <= 0) {
            // Stack underflow - pulling from below entry level
            return this to null
        }

        val poppedSlot = slots.lastOrNull()
        val newSlots = if (slots.isNotEmpty()) slots.dropLast(1) else emptyList()

        return StackState(
            depth = depth - 1,
            slots = newSlots
        ) to poppedSlot?.copy(pulledAt = lineRef)
    }

    /**
     * Adjust stack depth (for JSR/RTS which affect SP but don't access data)
     */
    fun adjustDepth(delta: Int): StackState {
        return copy(depth = depth + delta)
    }

    /**
     * Join two stack states at a merge point
     */
    fun join(other: StackState): StackState {
        // If depths differ, we can't precisely track stack
        if (depth != other.depth) {
            return StackState(depth = maxOf(depth, other.depth), slots = emptyList())
        }

        // Join slots pairwise
        val minSize = minOf(slots.size, other.slots.size)
        val joinedSlots = (0 until minSize).map { i ->
            val slot1 = slots[i]
            val slot2 = other.slots[i]

            // If slots differ, mark as unknown
            if (slot1.pushedValue != slot2.pushedValue) {
                slot1.copy(pushedValue = StackValue.Unknown)
            } else {
                slot1
            }
        }

        return StackState(depth = depth, slots = joinedSlots)
    }
}

/**
 * Stack analysis for a single function
 */
data class FunctionStackAnalysis(
    val function: FunctionCfg,
    val entryStackDepth: Int,       // Should be 0 (relative to function entry)
    val exitStackDepth: Int,        // Should be 0 for balanced functions
    val maxStackDepth: Int,         // Maximum stack usage within function
    val savedRegisters: List<StackFrame>,    // Registers saved and restored
    val pushPullPairs: List<StackFrame>,     // All matched push/pull pairs
    val unmatchedPushes: List<StackOp.Push>, // Pushes without matching pulls
    val unmatchedPulls: List<StackOp.Pull>,  // Pulls without matching pushes
    val isStackBalanced: Boolean    // Entry depth == exit depth
) {
    fun getStatistics(): Map<String, Int> {
        return mapOf(
            "maxDepth" to maxStackDepth,
            "savedRegisters" to savedRegisters.size,
            "matchedPairs" to pushPullPairs.size,
            "unmatchedPushes" to unmatchedPushes.size,
            "unmatchedPulls" to unmatchedPulls.size,
            "balanced" to if (isStackBalanced) 1 else 0
        )
    }
}

/**
 * Complete stack frame analysis
 */
data class StackFrameAnalysis(
    val functions: List<FunctionStackAnalysis>
)

/**
 * Perform stack frame analysis on all functions
 */
fun AssemblyCodeFile.analyzeStackFrames(
    cfg: CfgConstruction
): StackFrameAnalysis {
    val functionAnalyses = cfg.functions.map { function ->
        analyzeStackFrameForFunction(this, function)
    }

    return StackFrameAnalysis(functions = functionAnalyses)
}

/**
 * Analyze stack frame for a single function
 */
private fun analyzeStackFrameForFunction(
    codeFile: AssemblyCodeFile,
    function: FunctionCfg
): FunctionStackAnalysis {
    // Collect all stack operations
    val pushes = mutableListOf<StackOp.Push>()
    val pulls = mutableListOf<StackOp.Pull>()

    function.blocks.forEach { block ->
        block.lineIndexes.forEach { lineIndex ->
            val lineRef = codeFile.get(lineIndex)
            val line = lineRef.content
            val instruction = line.instruction

            when (instruction?.op) {
                AssemblyOp.PHA -> {
                    pushes.add(StackOp.Push(lineRef, StackValue.RegisterA, block.leaderIndex))
                }
                AssemblyOp.PHP -> {
                    pushes.add(StackOp.Push(lineRef, StackValue.Flags, block.leaderIndex))
                }
                AssemblyOp.PLA -> {
                    pulls.add(StackOp.Pull(lineRef, StackValue.RegisterA, block.leaderIndex))
                }
                AssemblyOp.PLP -> {
                    pulls.add(StackOp.Pull(lineRef, StackValue.Flags, block.leaderIndex))
                }
                else -> {}
            }
        }
    }

    // Track stack depth through CFG
    val blockStates = trackStackDepth(codeFile, function)

    // Find max stack depth
    val maxDepth = blockStates.values.maxOfOrNull { it.exitState.depth } ?: 0

    // Match push/pull pairs
    val pairs = matchPushPullPairs(pushes, pulls, blockStates, function)

    // Identify saved registers (pushed at entry, pulled before exit)
    val savedRegisters = identifySavedRegisters(pairs, function)

    // Find unmatched operations
    val matchedPushes = pairs.map { it.pushOp }.toSet()
    val matchedPulls = pairs.map { it.pullOp }.toSet()
    val unmatchedPushes = pushes.filter { it !in matchedPushes }
    val unmatchedPulls = pulls.filter { it !in matchedPulls }

    // Check stack balance (exit depth should equal entry depth)
    val exitBlocks = function.blocks.filter { block ->
        codeFile.lines.getOrNull(block.endIndex)?.instruction?.op in setOf(
            AssemblyOp.RTS,
            AssemblyOp.RTI
        )
    }

    val isBalanced = exitBlocks.all { block ->
        blockStates[block.leaderIndex]?.exitState?.depth == 0
    }

    return FunctionStackAnalysis(
        function = function,
        entryStackDepth = 0,
        exitStackDepth = exitBlocks.firstOrNull()?.let { blockStates[it.leaderIndex]?.exitState?.depth } ?: 0,
        maxStackDepth = maxDepth,
        savedRegisters = savedRegisters,
        pushPullPairs = pairs,
        unmatchedPushes = unmatchedPushes,
        unmatchedPulls = unmatchedPulls,
        isStackBalanced = isBalanced
    )
}

/**
 * State tracking for stack depth analysis
 */
private data class BlockStackState(
    val entryState: StackState,
    val exitState: StackState
)

/**
 * Track stack depth through the CFG using dataflow analysis
 */
private fun trackStackDepth(
    codeFile: AssemblyCodeFile,
    function: FunctionCfg
): Map<Int, BlockStackState> {
    val blockStates = mutableMapOf<Int, BlockStackState>()

    // Initialize all blocks
    function.blocks.forEach { block ->
        blockStates[block.leaderIndex] = BlockStackState(
            entryState = StackState(0, emptyList()),
            exitState = StackState(0, emptyList())
        )
    }

    // Iterative dataflow analysis
    var changed = true
    var iterations = 0

    while (changed && iterations < 100) {
        changed = false
        iterations++

        function.blocks.forEach { block ->
            val oldState = blockStates[block.leaderIndex]!!

            // Compute entry state from predecessors
            val predecessors = function.edges
                .filter { it.toLeader == block.leaderIndex }
                .mapNotNull { edge -> blockStates[edge.fromLeader]?.exitState }

            val entryState = if (predecessors.isEmpty()) {
                StackState(0, emptyList())  // Entry block
            } else {
                predecessors.reduce { acc, state -> acc.join(state) }
            }

            // Transfer through block
            val exitState = transferStackThroughBlock(codeFile, block, entryState)

            // Update if changed
            if (entryState != oldState.entryState || exitState != oldState.exitState) {
                blockStates[block.leaderIndex] = BlockStackState(entryState, exitState)
                changed = true
            }
        }
    }

    return blockStates
}

/**
 * Transfer stack state through a basic block
 */
private fun transferStackThroughBlock(
    codeFile: AssemblyCodeFile,
    block: BasicBlock,
    entryState: StackState
): StackState {
    var state = entryState

    block.lineIndexes.forEach { lineIndex ->
        val lineRef = codeFile.get(lineIndex)
        val line = lineRef.content
        val instruction = line.instruction

        state = when (instruction?.op) {
            AssemblyOp.PHA, AssemblyOp.PHP -> {
                val value = if (instruction.op == AssemblyOp.PHA) StackValue.RegisterA else StackValue.Flags
                state.push(value, lineRef)
            }
            AssemblyOp.PLA, AssemblyOp.PLP -> {
                state.pull(lineRef).first
            }
            AssemblyOp.JSR -> {
                // JSR pushes return address (2 bytes)
                state.adjustDepth(2)
            }
            AssemblyOp.RTS, AssemblyOp.RTI -> {
                // RTS pulls return address (2 bytes)
                state.adjustDepth(-2)
            }
            else -> state
        }
    }

    return state
}

/**
 * Match push operations with their corresponding pull operations
 */
private fun matchPushPullPairs(
    pushes: List<StackOp.Push>,
    pulls: List<StackOp.Pull>,
    blockStates: Map<Int, BlockStackState>,
    function: FunctionCfg
): List<StackFrame> {
    val pairs = mutableListOf<StackFrame>()

    // Simple matching: for each pull, find the most recent unmatched push of same type
    val unmatchedPushes = pushes.toMutableList()

    pulls.forEach { pull ->
        // Find candidate pushes (same value type, before the pull)
        val candidates = unmatchedPushes.filter { push ->
            push.value::class == pull.target::class &&
            push.lineRef.line < pull.lineRef.line
        }

        // Take the most recent push
        val matchedPush = candidates.maxByOrNull { it.lineRef.line }

        if (matchedPush != null) {
            val frameType = determineFrameType(matchedPush, pull, function)
            pairs.add(StackFrame(matchedPush, pull, frameType))
            unmatchedPushes.remove(matchedPush)
        }
    }

    return pairs
}

/**
 * Determine the type of stack frame
 */
private fun determineFrameType(
    push: StackOp.Push,
    pull: StackOp.Pull,
    function: FunctionCfg
): FrameType {
    val entryBlock = function.blocks.first()
    val exitBlocks = function.blocks.filter { block ->
        function.edges.any { edge -> edge.fromLeader == block.leaderIndex && edge.kind == CfgEdgeKind.RETURN }
    }

    // Check if push is in entry block and pull is in exit block
    val pushInEntry = push.blockLeader == entryBlock.leaderIndex
    val pullInExit = exitBlocks.any { it.leaderIndex == pull.blockLeader }

    return when {
        pushInEntry && pullInExit -> FrameType.REGISTER_SAVE
        else -> FrameType.LOCAL_VARIABLE
    }
}

/**
 * Identify saved registers (push at entry, pull at exit)
 */
private fun identifySavedRegisters(
    pairs: List<StackFrame>,
    function: FunctionCfg
): List<StackFrame> {
    return pairs.filter { it.frameType == FrameType.REGISTER_SAVE }
}
