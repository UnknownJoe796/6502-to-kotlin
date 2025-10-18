package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 4: Entry Point Discovery
 * - Identify function entry points from code references and conventions
 * - Sources:
 *   - JSR targets (direct subroutine calls)
 *   - Interrupt vectors via well-known label names (NMI, RESET, IRQ) if present
 *   - Exported/public labels supplied by caller
 *   - Jump table targets (JSR JumpEngine pattern with .dw directives)
 */

enum class EntryPointKind {
    JSR_TARGET,
    INTERRUPT_NMI,
    INTERRUPT_RESET,
    INTERRUPT_IRQ,
    EXPORTED,
    JUMP_TABLE,
}

data class EntryPoint(
    val label: String?,
    val address: Int?,
    val kind: EntryPointKind,
)

data class EntryPointDiscovery(
    val entryPoints: List<EntryPoint>
)


private fun primaryLabelForAddress(address: Int, labelToAddress: Map<String, Int>): String? =
    labelToAddress.entries.firstOrNull { it.value == address }?.key

/**
 * Discover entry points.
 * @param resolution Address resolution for the same lines. If not provided by caller, it will be computed with baseAddress=0.
 * @param exportedLabels Optional set of label names considered exported/public.
 * @param interruptLabelNames Optional mapping of interrupt kinds to a set of candidate label names to probe in the symbol table.
 */
fun AssemblyCodeFile.discoverEntryPoints(
    resolution: AddressResolution = this.resolveAddresses(),
    exportedLabels: Set<String> = emptySet(),
    interruptLabelNames: Map<EntryPointKind, Set<String>> = mapOf(
        EntryPointKind.INTERRUPT_NMI to setOf("NMI", "NMIHandler", "VEC_NMI", "NonMaskableInterrupt"),
        EntryPointKind.INTERRUPT_RESET to setOf("RESET", "Reset", "RESET_HANDLER", "Init", "Start"),
        EntryPointKind.INTERRUPT_IRQ to setOf("IRQ", "IrqHandler", "VEC_IRQ")
    ),
): EntryPointDiscovery {
    val labelToAddress = resolution.labelToAddress

    // Use a map to deduplicate by (kind, address, label)
    val uniq = linkedSetOf<Triple<EntryPointKind, Int?, String?>>()

    // 1) JSR targets from code
    this.lines.forEach { line ->
        val instr = line.instruction ?: return@forEach
        if (instr.op == AssemblyOp.JSR) {
            when (val a = instr.address) {
                is AssemblyAddressing.Label -> {
                    val lbl = a.label
                    val addr = labelToAddress[lbl] ?: parseHexAddr(lbl)
                    uniq += Triple(EntryPointKind.JSR_TARGET, addr, lbl.takeIf { it in labelToAddress.keys })
                }
                // Future: support other forms if parser changes
                else -> {
                    // If it's some other addressing that encodes a constant, we don't currently support.
                }
            }
        }
    }

    // 2) Interrupt vectors via well-known labels present in symbol table
    interruptLabelNames.forEach { (kind, candidates) ->
        candidates.forEach { name ->
            val addr = labelToAddress[name]
            if (addr != null) {
                uniq += Triple(kind, addr, name)
            }
        }
    }

    // 3) Exported/public labels provided by caller
    exportedLabels.forEach { name ->
        val addr = labelToAddress[name]
        if (addr != null) uniq += Triple(EntryPointKind.EXPORTED, addr, name)
    }

    // 4) Jump tables - detect JSR JumpEngine pattern
    // Pattern: JSR JumpEngine followed immediately by .dw directives containing function addresses
    this.lines.forEachIndexed { idx, line ->
        val instr = line.instruction ?: return@forEachIndexed

        // Check for JSR JumpEngine
        if (instr.op == AssemblyOp.JSR) {
            val target = (instr.address as? AssemblyAddressing.Label)?.label
            if (target == "JumpEngine") {
                // Scan forward to find consecutive .dw directives
                var scanIdx = idx + 1
                while (scanIdx < this.lines.size) {
                    val scanLine = this.lines[scanIdx]

                    // Check if this is a .dw directive
                    val data = scanLine.data
                    if (data is AssemblyData.Db) {
                        // Extract label references from this .dw
                        data.items.forEach { item ->
                            when (item) {
                                is AssemblyData.DbItem.Expr -> {
                                    val targetLabel = item.expr
                                    val targetAddr = labelToAddress[targetLabel]
                                    if (targetAddr != null) {
                                        uniq += Triple(EntryPointKind.JUMP_TABLE, targetAddr, targetLabel)
                                    }
                                }
                                else -> { /* Skip byte values */ }
                            }
                        }
                        scanIdx++
                    } else if (scanLine.instruction != null || scanLine.label != null) {
                        // Stop when we hit an instruction or a new label (end of jump table)
                        break
                    } else {
                        // Empty line or comment, keep scanning
                        scanIdx++
                    }
                }
            }
        }
    }

    val entries = uniq
        .asSequence()
        .map { (kind, addr, lbl) ->
            val label = lbl ?: (addr?.let { primaryLabelForAddress(it, labelToAddress) })
            EntryPoint(label = label, address = addr, kind = kind)
        }
        .sortedWith(compareBy<EntryPoint>({ it.address ?: Int.MAX_VALUE }, { it.label ?: "" }, { it.kind.name }))
        .toList()

    return EntryPointDiscovery(entries)
}
