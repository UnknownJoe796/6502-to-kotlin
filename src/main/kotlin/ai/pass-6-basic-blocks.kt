package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 6: Basic Block Construction
 * - Identify block leaders:
 *   - Entry points (from Pass 4)
 *   - First reachable instruction (fallback)
 *   - Branch/jump/call targets
 *   - Instructions immediately after branches/calls
 * - Split reachable code into straight-line basic blocks
 * - Record fall-through between blocks (and, for convenience, direct target successors)
 */

data class BasicBlock(
    /** Leader line index (into List<AssemblyLine>) */
    val leaderIndex: Int,
    /** Inclusive last line index in this basic block (must be an instruction line) */
    val endIndex: Int,
    /** All instruction line indexes contained in this block in execution order */
    val lineIndexes: List<Int>,
    /** Address of first instruction */
    val startAddress: Int,
    /** Address of last instruction (end) */
    val endAddress: Int,
    /** Leader index of fall-through successor block, if any */
    val fallThroughLeader: Int?,
    /** Leader indexes of explicit target successors (branch/jump/jsr) */
    val targetLeaders: Set<Int>,
)

data class BasicBlockConstruction(
    val blocks: List<BasicBlock>,
    /** Set of all leader line indexes */
    val leaderIndexes: Set<Int>,
    /** Mapping from instruction line index to its block leader index */
    val lineIndexToLeader: Map<Int, Int>,
)

private data class BBResCtx(
    val addrToIndex: Map<Int, Int>,
    val indexToAddr: Map<Int, Int>,
    val resolved: List<ResolvedLine>,
)

private fun buildCtx(resolution: AddressResolution): BBResCtx {
    val addrToIndex = mutableMapOf<Int, Int>()
    val indexToAddr = mutableMapOf<Int, Int>()
    resolution.resolved.forEachIndexed { idx, r ->
        val a = r.address
        if (a != null && r.line.instruction != null && !r.isData && r.sizeBytes > 0) {
            addrToIndex.putIfAbsent(a, idx)
            indexToAddr[idx] = a
        }
    }
    return BBResCtx(addrToIndex = addrToIndex, indexToAddr = indexToAddr, resolved = resolution.resolved)
}

private fun nextInstrIndexFrom(idx: Int, ctx: BBResCtx): Int? {
    val r = ctx.resolved[idx]
    val a = r.address ?: return null
    val nextAddr = a + r.sizeBytes
    return ctx.addrToIndex[nextAddr]
}

private fun labelOrHexToAddress(label: String, resolution: AddressResolution): Int? {
    // Try label map first, otherwise parse hex like $C000
    resolution.labelToAddress[label]?.let { return it }
    return parseHexAddr(label)
}

/**
 * Construct basic blocks for reachable code lines only.
 */
fun AssemblyCodeFile.constructBasicBlocks(
    resolution: AddressResolution = this.resolveAddresses(),
    reachability: ReachabilityReport = this.analyzeReachability(resolution, this.discoverEntryPoints(resolution)),
    entries: EntryPointDiscovery = this.discoverEntryPoints(resolution),
): BasicBlockConstruction {
    val ctx = buildCtx(resolution)

    // Consider only reachable instruction line indexes
    val reachable = reachability.reachableLineIndexes.filter { idx ->
        val r = ctx.resolved[idx]
        r.line.instruction != null && !r.isData && r.address != null && r.sizeBytes > 0
    }.toSet()
    if (reachable.isEmpty()) return BasicBlockConstruction(blocks = emptyList(), leaderIndexes = emptySet(), lineIndexToLeader = emptyMap())

    // Identify leaders
    val leaders = mutableSetOf<Int>()

    // Ensure at least the first reachable instruction is a leader (fallback)
    val firstReachable = reachable.minBy { ctx.indexToAddr[it] ?: Int.MAX_VALUE }
    leaders += firstReachable

    // Add entry points (from Pass 4) as leaders when they map to reachable instruction lines
    run {
        val entryAddrs = mutableSetOf<Int>()
        entries.entryPoints.forEach { ep ->
            ep.address?.let { entryAddrs += it } ?: ep.label?.let { lbl ->
                resolution.labelToAddress[lbl]?.let { entryAddrs += it }
            }
        }
        entryAddrs.forEach { addr ->
            val idx = ctx.addrToIndex[addr]
            if (idx != null && idx in reachable) leaders += idx
        }
    }

    // Add all labeled instructions as leaders (important for basic block boundaries)
    reachable.forEach { idx ->
        val line = this.lines[idx]
        if (line.label != null) {
            leaders += idx
        }
    }

    // For each reachable instruction, add targets of branches/jumps/calls and instruction after branch/call as leaders
    reachable.forEach { idx ->
        val r = ctx.resolved[idx]
        val instr = r.line.instruction ?: return@forEach
        val op = instr.op
        val addr = instr.address

        // Helper: add label-or-hex target
        fun addTargetFromLabelRef(label: String) {
            val ta = labelOrHexToAddress(label, resolution)
            val tIdx = ta?.let { ctx.addrToIndex[it] }
            if (tIdx != null && tIdx in reachable) leaders += tIdx
        }

        // Instruction following branches/calls are leaders
        if (op.isBranch || op == AssemblyOp.JSR) {
            nextInstrIndexFrom(idx, ctx)?.let { if (it in reachable) leaders += it }
        }

        when {
            op.isBranch -> {
                val t = (addr as? AssemblyAddressing.Label)?.label
                if (t != null) addTargetFromLabelRef(t)
            }
            op == AssemblyOp.JMP -> {
                when (addr) {
                    is AssemblyAddressing.Label -> addTargetFromLabelRef(addr.label)
                    is AssemblyAddressing.IndirectAbsolute -> { /* no static target at this pass */ }
                    else -> { /* ignore */ }
                }
            }
            op == AssemblyOp.JSR -> {
                val t = (addr as? AssemblyAddressing.Label)?.label
                if (t != null) addTargetFromLabelRef(t)
            }
        }
    }

    // Sort leaders by address for deterministic block order
    val leadersSorted = leaders.toList().sortedBy { ctx.indexToAddr[it] ?: Int.MAX_VALUE }

    // Build a set for quick membership checks
    val leaderSet = leadersSorted.toSet()

    val blocks = mutableListOf<BasicBlock>()
    val lineToLeader = mutableMapOf<Int, Int>()

    // Map from leader index to its position in leadersSorted for successor resolution
    val leaderIndexPosition = leadersSorted.withIndex().associate { it.value to it.index }

    // Helper to collect straight-line sequence for a leader
    fun buildBlockFromLeader(leaderIdx: Int): BasicBlock {
        val linesInBlock = mutableListOf<Int>()
        var cur = leaderIdx
        var endIdx = leaderIdx
        var terminate = false
        while (true) {
            linesInBlock += cur
            lineToLeader[cur] = leaderIdx
            endIdx = cur
            val r = ctx.resolved[cur]
            val instr = r.line.instruction!!
            val op = instr.op
            // Block ends on control transfers: branch, jmp, jsr, rts/rti/brk, or if next is another leader
            val isTerminator = (op == AssemblyOp.RTS || op == AssemblyOp.RTI || op == AssemblyOp.BRK)
            val isUncondJump = (op == AssemblyOp.JMP)
            val endsHere = isTerminator || isUncondJump || op.isBranch || op == AssemblyOp.JSR
            if (endsHere) break
            val next = nextInstrIndexFrom(cur, ctx) ?: break
            if (next !in reachable) break
            if (next in leaderSet) break
            cur = next
        }

        // Compute successors: fall-through and direct targets
        var fallThroughLeader: Int? = null
        val targetLeaders = mutableSetOf<Int>()
        run {
            val endR = ctx.resolved[endIdx]
            val instr = endR.line.instruction!!
            val op = instr.op
            val a = instr.address

            fun leaderOfAddress(address: Int): Int? {
                val tIdx = ctx.addrToIndex[address] ?: return null
                return if (tIdx in leaderSet) tIdx else null
            }

            // Fall-through: for branches and JSR and normal instructions (but not JMP/RTS/RTI/BRK)
            val canFallThrough = when {
                op == AssemblyOp.JMP -> false
                op == AssemblyOp.RTS || op == AssemblyOp.RTI || op == AssemblyOp.BRK -> false
                else -> true
            }
            if (canFallThrough) {
                // Calculate fall-through address properly based on instruction size
                val fallThroughAddr = endR.address!! + endR.sizeBytes
                fallThroughLeader = leaderOfAddress(fallThroughAddr)
            }

            // Direct target successors
            when {
                op.isBranch -> {
                    val lbl = (a as? AssemblyAddressing.Label)?.label
                    val ta = lbl?.let { labelOrHexToAddress(it, resolution) }
                    ta?.let { leaderOfAddress(it) }?.let { targetLeaders += it }
                }
                op == AssemblyOp.JMP -> {
                    when (a) {
                        is AssemblyAddressing.Label -> {
                            val ta = labelOrHexToAddress(a.label, resolution)
                            ta?.let { leaderOfAddress(it) }?.let { targetLeaders += it }
                        }
                        is AssemblyAddressing.IndirectAbsolute -> { /* unknown at this pass */ }
                        else -> {}
                    }
                }
                op == AssemblyOp.JSR -> {
                    val lbl = (a as? AssemblyAddressing.Label)?.label
                    val ta = lbl?.let { labelOrHexToAddress(it, resolution) }
                    ta?.let { leaderOfAddress(it) }?.let { targetLeaders += it }
                }
            }
        }

        val startAddr = ctx.indexToAddr[leaderIdx] ?: 0
        val endAddr = ctx.indexToAddr[endIdx] ?: startAddr
        return BasicBlock(
            leaderIndex = leaderIdx,
            endIndex = endIdx,
            lineIndexes = linesInBlock.toList(),
            startAddress = startAddr,
            endAddress = endAddr,
            fallThroughLeader = fallThroughLeader,
            targetLeaders = targetLeaders,
        )
    }

    leadersSorted.forEach { lead ->
        blocks += buildBlockFromLeader(lead)
    }

    return BasicBlockConstruction(
        blocks = blocks,
        leaderIndexes = leaderSet,
        lineIndexToLeader = lineToLeader,
    )
}
