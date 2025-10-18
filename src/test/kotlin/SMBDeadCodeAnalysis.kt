package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import java.io.File

/**
 * Analysis test to identify unused constants, unreachable code, and dead code in SMB
 * Outputs results to smb-dead-code-analysis.txt for review
 */
class SMBDeadCodeAnalysis {

    @Test
    fun analyzeUnusedConstantsAndDeadCode() {
        val smbFile = File("smbdism.asm")
        if (!smbFile.exists()) {
            println("Warning: smbdism.asm not found, skipping analysis")
            return
        }

        val assembly = smbFile.readText()
        val outputFile = File("outputs/smb-dead-code-analysis.txt")

        println("Analyzing Super Mario Bros for unused constants and dead code...")

        // Parse and analyze
        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution)
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val dataFlow = lines.analyzeDataFlow(cfg, dominators)
        val constants = lines.analyzeConstants(cfg)
        val memoryPatterns = lines.analyzeMemoryPatterns(cfg, constants, resolution)

        val output = StringBuilder()
        output.appendLine("=" * 80)
        output.appendLine("SUPER MARIO BROS - DEAD CODE ANALYSIS")
        output.appendLine("=" * 80)
        output.appendLine()

        // 1. Find all defined constants
        val allConstants = lines.lines.mapNotNull { line ->
            line.constant?.let { const ->
                val address = resolution.labelToAddress[const.name]
                Triple(const.name, address, line.originalLine)
            }
        }

        output.appendLine("SUMMARY")
        output.appendLine("-" * 80)
        output.appendLine("Total constants defined: ${allConstants.size}")
        output.appendLine("Total memory locations accessed: ${memoryPatterns.globalMemoryMap.size}")
        output.appendLine("Entry points discovered: ${entries.entryPoints.size}")
        output.appendLine("Functions analyzed: ${cfg.functions.size}")
        output.appendLine()

        // 2. Find unused constants (defined but never accessed)
        val accessedAddresses = memoryPatterns.globalMemoryMap.keys
        val unusedConstants = allConstants.filter { (_, address, _) ->
            address != null && address !in accessedAddresses
        }

        output.appendLine("UNUSED CONSTANTS (${unusedConstants.size} total)")
        output.appendLine("-" * 80)
        output.appendLine("These constants are defined but never accessed in reachable code:")
        output.appendLine()

        // Group by memory region
        val unusedByRegion = unusedConstants.groupBy { (_, address, _) ->
            when {
                address == null -> "Unresolved"
                address < 0x0100 -> "Zero Page (\$00-\$FF)"
                address in 0x0100..0x01FF -> "Stack (\$0100-\$01FF)"
                address in 0x0200..0x07FF -> "NES RAM (\$0200-\$07FF)"
                address in 0x0800..0x1FFF -> "High RAM (\$0800-\$1FFF)"
                address in 0x2000..0x2007 -> "PPU Registers (\$2000-\$2007)"
                address in 0x4000..0x4017 -> "APU/IO Registers (\$4000-\$4017)"
                address >= 0x8000 -> "ROM (\$8000+)"
                else -> "Other"
            }
        }

        unusedByRegion.forEach { (region, constants) ->
            output.appendLine("$region: ${constants.size} unused constants")
            constants.sortedBy { it.second }.forEach { (name, address, originalLine) ->
                val addrStr = address?.let { "$" + it.toString(16).uppercase().padStart(4, '0') } ?: "????"
                output.appendLine("  $addrStr  $name")
                output.appendLine("           ${originalLine?.trim()}")
            }
            output.appendLine()
        }

        // 3. Find unreachable code (labels defined but not in reachability analysis)
        val reachableAddresses = reachability.reachableAddresses

        val allLabels = lines.lines.mapIndexedNotNull { idx, line ->
            line.label?.let { label ->
                val address = resolution.resolved.getOrNull(idx)?.address
                Triple(label, address, idx)
            }
        }

        // Helper function to check if a label marks a data-only section
        fun isDataOnlyLabel(labelIdx: Int): Boolean {
            // Scan forward from this label until we hit the next label or end
            var hasAnyData = false
            for (i in labelIdx until lines.lines.size) {
                val line = lines.lines[i]

                // For the label line itself, check if it has data on the same line
                if (i == labelIdx) {
                    if (line.instruction != null) {
                        return false // Label with instruction on same line
                    }
                    if (line.data != null) {
                        hasAnyData = true
                        continue // Label with data on same line - that's fine
                    }
                    continue // Label line with nothing, keep scanning
                }

                // If we hit another label, we're done with this section
                if (line.label != null) {
                    break
                }

                // Skip empty/comment lines
                if (line.instruction == null && line.data == null) {
                    continue
                }

                // If we find any instruction, this is not data-only
                if (line.instruction != null) {
                    return false
                }

                // If we find data, that's fine - keep scanning
                if (line.data != null) {
                    hasAnyData = true
                    continue
                }
            }

            // If we got here, we only saw data (or nothing), so it's data-only
            // But we need at least some data to consider it a data section
            return hasAnyData
        }

        val unreachableLabels = allLabels.filter { (_, address, labelIdx) ->
            address != null &&
            address !in reachableAddresses &&
            address >= 0x8000 && // Only check ROM
            !isDataOnlyLabel(labelIdx) // Exclude data-only labels
        }

        output.appendLine("INDIRECTLY REACHABLE CODE (${unreachableLabels.size} labels)")
        output.appendLine("-" * 80)
        output.appendLine("These labels are not reached via static analysis (JSR/branches).")
        output.appendLine("They may be reached via jump tables, function pointers, or other indirect calls:")
        output.appendLine()

        if (unreachableLabels.isNotEmpty()) {
            unreachableLabels.sortedBy { it.second }.forEach { (label, address, lineIdx) ->
                val addrStr = address?.let { "$" + it.toString(16).uppercase() } ?: "????"
                val line = lines.lines.getOrNull(lineIdx)
                output.appendLine("  $addrStr  $label")
                if (line?.instruction != null) {
                    output.appendLine("           ${line.instruction}")
                }
            }
        } else {
            output.appendLine("  (All labeled code is reachable)")
        }
        output.appendLine()

        // 4. Find defined-but-never-written memory locations (potentially read-only or const)
        val neverWritten = memoryPatterns.globalMemoryMap.filter { (_, info) ->
            info.writes.isEmpty() && info.reads.isNotEmpty()
        }

        output.appendLine("READ-ONLY MEMORY LOCATIONS (${neverWritten.size} total)")
        output.appendLine("-" * 80)
        output.appendLine("These locations are read but never written (may be ROM data or constants):")
        output.appendLine()

        val neverWrittenByRegion = neverWritten.entries.groupBy { (address, _) ->
            when {
                address < 0x0100 -> "Zero Page"
                address in 0x0200..0x07FF -> "NES RAM"
                address >= 0x8000 -> "ROM"
                else -> "Other"
            }
        }

        neverWrittenByRegion.forEach { (region, entries) ->
            output.appendLine("$region: ${entries.size} read-only locations")
            entries.sortedBy { it.key }.take(20).forEach { (address, info) ->
                val name = resolution.labelToAddress.entries.find { it.value == address }?.key ?: "unnamed"
                val addrStr = "$" + address.toString(16).uppercase().padStart(4, '0')
                output.appendLine("  $addrStr  ($name)  reads: ${info.reads.size}")
            }
            if (entries.size > 20) {
                output.appendLine("  ... and ${entries.size - 20} more")
            }
            output.appendLine()
        }

        // 5. Find defined-but-never-read memory locations (write-only, possibly dead stores)
        val neverRead = memoryPatterns.globalMemoryMap.filter { (_, info) ->
            info.reads.isEmpty() && info.writes.isNotEmpty()
        }

        output.appendLine("WRITE-ONLY MEMORY LOCATIONS (${neverRead.size} total)")
        output.appendLine("-" * 80)
        output.appendLine("These locations are written but never read (potentially dead stores):")
        output.appendLine()

        if (neverRead.isNotEmpty()) {
            neverRead.entries.sortedBy { it.key }.forEach { (address, info) ->
                val name = resolution.labelToAddress.entries.find { it.value == address }?.key ?: "unnamed"
                val addrStr = "$" + address.toString(16).uppercase().padStart(4, '0')
                output.appendLine("  $addrStr  ($name)  writes: ${info.writes.size}")
            }
        } else {
            output.appendLine("  (All written locations are also read)")
        }
        output.appendLine()

        // 6. Entry point analysis
        output.appendLine("ENTRY POINT ANALYSIS")
        output.appendLine("-" * 80)
        output.appendLine("Discovered entry points by type:")
        output.appendLine()

        val entriesByKind = entries.entryPoints.groupBy { it.kind }
        entriesByKind.forEach { (kind, points) ->
            output.appendLine("$kind: ${points.size} entry points")
            points.sortedBy { it.address }.take(10).forEach { entry ->
                val addrStr = entry.address?.let { "$" + it.toString(16).uppercase() } ?: "????"
                val label = entry.label ?: "unlabeled"
                output.appendLine("  $addrStr  $label")
            }
            if (points.size > 10) {
                output.appendLine("  ... and ${points.size - 10} more")
            }
            output.appendLine()
        }

        // Write to file
        outputFile.writeText(output.toString())

        println("✓ Analysis complete!")
        println("Results written to: ${outputFile.absolutePath}")
        println()
        println("Summary:")
        println("  Unused constants: ${unusedConstants.size}")
        println("  Unreachable labels: ${unreachableLabels.size}")
        println("  Read-only locations: ${neverWritten.size}")
        println("  Write-only locations: ${neverRead.size}")
    }

    private operator fun String.times(count: Int): String = this.repeat(count)
}
