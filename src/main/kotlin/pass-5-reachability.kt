package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 5: Reachability Analysis
 * - Trace all reachable code starting from discovered entry points
 * - Mark dead/unreachable code (instruction lines not reached)
 * - Identify code vs data regions via reachability on instruction lines
 */

data class ReachabilityReport(
    val reachableAddresses: Set<Int>,
    val reachableLineIndexes: Set<Int>,
    val deadCodeLineIndexes: Set<Int>,
    val codeLineIndexes: Set<Int>,
)


private data class ResCtx(
    val addrToIndex: Map<Int, Int>,
    val labelToAddress: Map<String, Int>,
    val resolved: List<ResolvedLine>,
)

private fun buildContext(resolution: AddressResolution): ResCtx {
    val addrToIndex = mutableMapOf<Int, Int>()
    resolution.resolved.forEachIndexed { idx, r ->
        val a = r.address
        if (a != null) addrToIndex.putIfAbsent(a, idx)
    }
    return ResCtx(addrToIndex = addrToIndex, labelToAddress = resolution.labelToAddress, resolved = resolution.resolved)
}

private fun AssemblyInstruction.successorAddresses(
    currentAddress: Int,
    sizeBytes: Int,
    ctx: ResCtx,
): Set<Int> {
    val op = this.op
    val addr = this.address
    val out = mutableSetOf<Int>()

    fun addTargetAddress(target: Int?) {
        if (target != null) out += target
    }

    fun decodeLabelAddress(label: String): Int? = ctx.labelToAddress[label] ?: parseHexAddr(label)

    // Helper to add fall-through to next instruction
    fun addFallThrough() {
        addTargetAddress(currentAddress + sizeBytes)
    }

    when {
        op.isBranch -> {
            // Conditional branch: add both target and fall-through
            val target = (addr as? AssemblyAddressing.Label)?.let { decodeLabelAddress(it.label) }
            addTargetAddress(target)
            addFallThrough()
        }
        op == AssemblyOp.JSR -> {
            // Call: target and fall-through
            val target = (addr as? AssemblyAddressing.Label)?.let { decodeLabelAddress(it.label) }
            addTargetAddress(target)
            addFallThrough()
        }
        op == AssemblyOp.JMP -> {
            // Unconditional jump: to target only (no fall-through)
            when (addr) {
                is AssemblyAddressing.Label -> addTargetAddress(decodeLabelAddress(addr.label))
                is AssemblyAddressing.IndirectAbsolute -> {
                    // For indirect jumps, try to resolve the target address if it's a known label
                    // This is better than stopping analysis completely
                    val indirectAddr = decodeLabelAddress(addr.label)
                    if (indirectAddr != null) {
                        // Look for potential jump table entries by checking if the indirect address
                        // points to a data section that might contain addresses
                        // For now, we'll be conservative but not completely stop analysis
                        // TODO: Implement proper jump table analysis in future passes
                    }
                    // Note: We intentionally don't add fall-through for indirect jumps
                    // since they're true jumps, but we could add heuristics here
                }
                else -> {
                    // If parser represents absolute as Label with "$...." we already handled. Nothing else for now.
                }
            }
        }
        op == AssemblyOp.RTS || op == AssemblyOp.RTI || op == AssemblyOp.BRK -> {
            // Terminators: no successors
        }
        else -> {
            // Normal instruction: fall-through to next sequential instruction
            addFallThrough()
        }
    }
    return out
}

fun List<AssemblyLine>.analyzeReachability(
    resolution: AddressResolution = this.resolveAddresses(),
    entries: EntryPointDiscovery = this.discoverEntryPoints(resolution),
): ReachabilityReport {
    val ctx = buildContext(resolution)
    val n = resolution.resolved.size

    // Identify which line indexes are instruction lines
    val codeLineIndexes = buildSet {
        resolution.resolved.forEachIndexed { idx, r ->
            if (!r.isData && r.line.instruction != null && r.address != null && r.sizeBytes > 0) add(idx)
        }
    }

    // Seed worklist with entry point addresses that map to instruction lines
    val seedAddresses = entries.entryPoints.mapNotNull { it.address }.toMutableSet()

    // Some entry points may have label but null address (unlikely with current pipeline); try to resolve
    entries.entryPoints.filter { it.address == null && it.label != null }.forEach { ep ->
        resolution.labelToAddress[ep.label!!]?.let { seedAddresses += it }
    }

    val visitedAddresses = mutableSetOf<Int>()
    val reachableLineIndexes = mutableSetOf<Int>()

    // Worklist traversal (BFS)
    val queue: ArrayDeque<Int> = ArrayDeque(seedAddresses)
    while (queue.isNotEmpty()) {
        val addr = queue.removeFirst()
        if (!visitedAddresses.add(addr)) continue

        val idx = ctx.addrToIndex[addr] ?: continue
        val r = ctx.resolved[idx]
        val line = r.line
        // Only traverse into instruction lines
        if (r.isData || line.instruction == null) continue

        reachableLineIndexes += idx

        // Compute successors
        val succAddrs = line.instruction.successorAddresses(
            currentAddress = r.address!!,
            sizeBytes = r.sizeBytes,
            ctx = ctx,
        )

        // Only queue successors that point to instruction lines
        succAddrs.forEach { sa ->
            val sIdx = ctx.addrToIndex[sa]
            if (sIdx != null) {
                val sr = ctx.resolved[sIdx]
                if (!sr.isData && sr.line.instruction != null) {
                    if (sa !in visitedAddresses) queue.addLast(sa)
                }
            }
        }
    }

    val deadCodeLineIndexes = codeLineIndexes - reachableLineIndexes

    // Collect reachable addresses from indexes
    val reachableAddresses = buildSet {
        reachableLineIndexes.forEach { idx ->
            ctx.resolved[idx].address?.let { add(it) }
        }
    }

    return ReachabilityReport(
        reachableAddresses = reachableAddresses,
        reachableLineIndexes = reachableLineIndexes,
        deadCodeLineIndexes = deadCodeLineIndexes,
        codeLineIndexes = codeLineIndexes,
    )
}
