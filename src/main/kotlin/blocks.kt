package com.ivieleague.decompiler6502tokotlin.hand

class AssemblyBlock(
    val lines: List<AssemblyLine>
) {
    init { lines.forEach { it.block = this } }
    override fun toString(): String = "$originalLineIndex) ${lines.first()}"
    val originalLineIndex = lines.first().originalLineIndex
    val label: String? = lines.firstOrNull()?.label

    /**
     * The blocks that could enter this block.
     */
    val enteredFrom = ArrayList<AssemblyBlock>()

    /**
     * The exit from either falling through to the next block or a jump.
     * If it is null, that indicates this block performs an RTS or RTI.
     * Note that the code we're analyzing does not use any indirect jumps.
     */
    var fallThroughExit: AssemblyBlock? = null

    /**
     * The alternate exit from a conditional branch.
     * Note that the code we're analyzing does not use any indirect jumps.
     */
    var branchExit: AssemblyBlock? = null  // Always comes from last assembly line

    /**
     * The immediate dominator of this block - in other words, the only block this block can come from.
     */
    var immediateDominator: AssemblyBlock? = null

    /**
     * The immediate dominator of this block - in other words, the only block this block can come from.
     */
    val dominates = ArrayList<AssemblyBlock>()

    var function: AssemblyFunction? = null

    val dominationDepth: Int get() = generateSequence(this.immediateDominator) { it.immediateDominator }.count()
}

sealed interface ExpressionifiedState {
    object A : ExpressionifiedState {
        override fun toString() = "A"
    }
    object X : ExpressionifiedState {
        override fun toString() = "X"
    }
    object Y : ExpressionifiedState {
        override fun toString() = "Y"
    }
    object ZeroFlag : ExpressionifiedState {
        override fun toString() = "ZeroFlag"
    }
    object NegativeFlag : ExpressionifiedState {
        override fun toString() = "NegativeFlag"
    }
    object OverflowFlag : ExpressionifiedState {
        override fun toString() = "OverflowFlag"
    }
    object CarryFlag : ExpressionifiedState {
        override fun toString() = "CarryFlag"
    }

    /**
     * An address in zero-page that is treated like a temporary register to move data between subroutines.
     */
    data class VirtualRegister(val label: String): ExpressionifiedState {
        override fun toString(): String = label
    }
}

class AssemblyFunction(
    val startingBlock: AssemblyBlock,
    val callers: List<AssemblyLine>
) {
    override fun toString(): String = "fun ${startingBlock.label}(${inputs?.joinToString("")}): ${outputs?.joinToString("")}"
    /**
     * States that are consumed before being set by the function, indicating that they are inputs.
     */
    var inputs: Set<ExpressionifiedState>? = null

    /**
     * States that calling functions read from that are affected by this function.
     */
    var outputs: Set<ExpressionifiedState>? = null

    /**
     * The number of stack elements pushed by the function.
     * Negative indicates stack elements popped off the stack.
     */
    var stackSizeChange: Int? = null
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
        when {
            line.label != null -> {
                if (currentBlock.isNotEmpty()) {
                    blocks.add(AssemblyBlock(currentBlock).also {
                        previousBlock?.let { pb ->
                            it.enteredFrom.add(pb)
                            pb.fallThroughExit = it
                        }
                        previousBlock = it
                    })
                    currentBlock = ArrayList()
                }
                currentBlock.add(line)
            }

            line.instruction?.op?.isBranch == true -> {
                currentBlock.add(line)
                blocks.add(AssemblyBlock(currentBlock).also {
                    previousBlock?.let { pb ->
                        it.enteredFrom.add(pb)
                        pb.fallThroughExit = it
                    }
                    previousBlock = it
                })
                currentBlock = ArrayList()
            }

            line.instruction?.op == AssemblyOp.JMP -> {
                currentBlock.add(line)
                blocks.add(AssemblyBlock(currentBlock).also {
                    previousBlock?.let { pb ->
                        it.enteredFrom.add(pb)
                        pb.fallThroughExit = it
                    }
                    // The next found block isn't connected to this one.
                    previousBlock = null
                })
                currentBlock = ArrayList()
            }

            line.instruction?.op == AssemblyOp.RTS || line.instruction?.op == AssemblyOp.RTI -> {
                currentBlock.add(line)
                blocks.add(AssemblyBlock(currentBlock).also {
                    previousBlock?.let { pb ->
                        it.enteredFrom.add(pb)
                        pb.fallThroughExit = it
                    }
                    // The next found block isn't connected to this one.
                    previousBlock = null
                })
                currentBlock = ArrayList()
            }

            line.instruction != null || line.data != null -> {
                currentBlock.add(line)
            }
            else -> {
                // We don't assemble lines that aren't instructions or labels into blocks.
            }
        }
    }
    if (currentBlock.isNotEmpty()) {
        blocks.add(AssemblyBlock(currentBlock).also {
            previousBlock?.let { pb ->
                it.enteredFrom.add(pb)
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
                val otherBlock = blocksByLabel[lastInstruction.addressAsLabel.label]!!
                block.branchExit = otherBlock
                otherBlock.enteredFrom.add(block)
            }

            op == AssemblyOp.JMP -> {
                val addr = lastInstruction.address
                if (addr is AssemblyAddressing.Direct) {
                    val otherBlock = blocksByLabel[addr.label]
                    if (otherBlock != null) {
                        block.branchExit = otherBlock
                        otherBlock.enteredFrom.add(block)
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

    // Clear existing dominance info
    for (block in this) block.dominates.clear()

    // Write results back to blocks, skipping the synthetic node
    for (i in 0 until n) {
        val block = this[i]
        if (!visited[i]) {
            block.immediateDominator = null
            continue
        }

        val isRealRoot = preds[i].isEmpty()
        val parent = idom[i]

        block.immediateDominator = when {
            isRealRoot -> null
            parent == -1 -> null
            hasSuperRoot && parent == entry -> null // dominated only by super-root => real root
            parent == i -> null
            else -> this[parent]
        }

        block.immediateDominator?.dominates?.add(block)
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
    val functionEntryBlocks = HashMap<AssemblyBlock, ArrayList<AssemblyLine>>()
    functionEntryBlocks.put(this[0], ArrayList()) // Entry point

    for (block in this) {
        for (line in block.lines) {
            if (line.instruction?.op == AssemblyOp.JSR) {
                val targetLabel = (line.instruction.address as AssemblyAddressing.Direct).label
                val targetBlock = this.find { it.label == targetLabel } ?: throw IllegalArgumentException("Could not find block with label $targetLabel")
                functionEntryBlocks.getOrPut(targetBlock) { ArrayList() }.add(line)
            }
        }
    }

    // Step 2: For each function, compute its blocks using dominator tree
    for ((entryBlock, callers) in functionEntryBlocks.entries) {
        val function = AssemblyFunction(entryBlock, callers)

        // Find all blocks that belong to this function
        // A block belongs to a function if:
        // - It's reachable from the entry block
        // - It's not another function's entry block (unless it's dominated by this entry)
        val functionBlocks = mutableSetOf<AssemblyBlock>()
        val toVisit = mutableListOf(entryBlock)
        val visited = mutableSetOf<AssemblyBlock>()

        while (toVisit.isNotEmpty()) {
            val current = toVisit.removeAt(0)
            if (current in visited) continue
            visited.add(current)

            // Stop if we hit another function entry (unless it's the current function)
            if (current in functionEntryBlocks && current != entryBlock) {
                continue
            }

            functionBlocks.add(current)
            current.function = function

            // Add successors
            current.fallThroughExit?.let { if (it !in visited) toVisit.add(it) }
            current.branchExit?.let { if (it !in visited) toVisit.add(it) }
        }

        // Step 3: Analyze data flow to determine inputs and outputs
        val inputs = mutableSetOf<ExpressionifiedState>()
        val outputs = mutableSetOf<ExpressionifiedState>()
        val defined = mutableSetOf<ExpressionifiedState>() // States that have been written

        // Track which states are defined before being used
        for (block in functionBlocks) {
            for (line in block.lines) {
                val instruction = line.instruction ?: continue
                val op = instruction.op

                // Check if instruction consumes a flag
                op.consumedFlag?.let { flag ->
                    val state = when (flag) {
                        AssemblyFlag.N -> ExpressionifiedState.NegativeFlag
                        AssemblyFlag.V -> ExpressionifiedState.OverflowFlag
                        AssemblyFlag.Z -> ExpressionifiedState.ZeroFlag
                        AssemblyFlag.C -> ExpressionifiedState.CarryFlag
                    }
                    if (state !in defined) {
                        inputs.add(state)
                    }
                }

                // Check if instruction reads from registers
                when (op) {
                    AssemblyOp.STA, AssemblyOp.CMP, AssemblyOp.ADC, AssemblyOp.SBC,
                    AssemblyOp.AND, AssemblyOp.EOR, AssemblyOp.ORA, AssemblyOp.PHA,
                    AssemblyOp.TAX, AssemblyOp.TAY -> {
                        if (ExpressionifiedState.A !in defined) {
                            inputs.add(ExpressionifiedState.A)
                        }
                    }
                    AssemblyOp.STX, AssemblyOp.CPX, AssemblyOp.TXA, AssemblyOp.TXS,
                    AssemblyOp.INX, AssemblyOp.DEX -> {
                        if (ExpressionifiedState.X !in defined) {
                            inputs.add(ExpressionifiedState.X)
                        }
                    }
                    AssemblyOp.STY, AssemblyOp.CPY, AssemblyOp.TYA,
                    AssemblyOp.INY, AssemblyOp.DEY -> {
                        if (ExpressionifiedState.Y !in defined) {
                            inputs.add(ExpressionifiedState.Y)
                        }
                    }
                    else -> {}
                }

                // Check if instruction reads from memory (virtual registers)
                when (val addr = instruction.address) {
                    is AssemblyAddressing.Direct -> {
                        if(labelIsVirtualRegister(addr.label)) {
                            // Reading from memory location
                            if (op == AssemblyOp.LDA || op == AssemblyOp.LDX || op == AssemblyOp.LDY ||
                                op == AssemblyOp.CMP || op == AssemblyOp.CPX || op == AssemblyOp.CPY ||
                                op == AssemblyOp.ADC || op == AssemblyOp.SBC ||
                                op == AssemblyOp.AND || op == AssemblyOp.EOR || op == AssemblyOp.ORA
                            ) {
                                val virtualReg =
                                    ExpressionifiedState.VirtualRegister(addr.label) as ExpressionifiedState
                                if (!defined.contains(virtualReg)) {
                                    inputs.add(virtualReg)
                                }
                            }
                        }
                    }
                    else -> {}
                }

                // Track what this instruction defines
                when (op) {
                    AssemblyOp.LDA, AssemblyOp.TXA, AssemblyOp.TYA, AssemblyOp.PLA,
                    AssemblyOp.ADC, AssemblyOp.SBC, AssemblyOp.AND, AssemblyOp.EOR, AssemblyOp.ORA,
                    AssemblyOp.ASL, AssemblyOp.LSR, AssemblyOp.ROL, AssemblyOp.ROR -> {
                        defined.add(ExpressionifiedState.A)
                    }
                    AssemblyOp.LDX, AssemblyOp.TAX, AssemblyOp.TSX,
                    AssemblyOp.INX, AssemblyOp.DEX -> {
                        defined.add(ExpressionifiedState.X)
                    }
                    AssemblyOp.LDY, AssemblyOp.TAY,
                    AssemblyOp.INY, AssemblyOp.DEY -> {
                        defined.add(ExpressionifiedState.Y)
                    }
                    else -> {}
                }

                // Track flag definitions
                op.affectedFlags.forEach { flag ->
                    val state = when (flag) {
                        AssemblyFlag.N -> ExpressionifiedState.NegativeFlag
                        AssemblyFlag.V -> ExpressionifiedState.OverflowFlag
                        AssemblyFlag.Z -> ExpressionifiedState.ZeroFlag
                        AssemblyFlag.C -> ExpressionifiedState.CarryFlag
                    }
                    defined.add(state)
                }

                // Track memory writes (virtual registers)
                when (val addr = instruction.address) {
                    is AssemblyAddressing.Direct -> {
                        if (op == AssemblyOp.STA || op == AssemblyOp.STX || op == AssemblyOp.STY) {
                            defined.add(ExpressionifiedState.VirtualRegister(addr.label))
                        }
                    }
                    else -> {}
                }
            }
        }

        // TODO: Analyze function outputs by looping through callers

        function.inputs = inputs
        functions.add(function)
    }


    return functions
}
