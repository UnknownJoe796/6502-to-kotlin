@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.interpreter

import java.io.File
import java.io.PrintWriter
import kotlin.test.Test

/**
 * Analyzes RAM divergence between interpreter and FCEUX frame-by-frame.
 *
 * Goal: Find which memory addresses diverge first and build a pattern of divergence.
 * This is a "binary search" approach to quickly eliminate areas that match.
 */
class RAMDivergenceAnalyzer {

    // SMB memory region definitions
    enum class MemoryRegion(val range: IntRange, val description: String, val critical: Boolean) {
        ZERO_PAGE(0x0000..0x00FF, "Zero Page (player state, temps)", true),
        STACK(0x0100..0x01FF, "CPU Stack", false),
        OAM_BUFFER(0x0200..0x02FF, "OAM Sprite Buffer", false),
        MISC_300(0x0300..0x03FF, "Misc Data", true),
        BLOCK_BUFFER_1A(0x0400..0x04FF, "Block Buffer 1 (part A)", true),
        BLOCK_BUFFER_1B(0x0500..0x05CF, "Block Buffer 1 (part B)", true),
        BLOCK_BUFFER_2(0x05D0..0x06CF, "Block Buffer 2", true),
        MISC_6D0(0x06D0..0x06FF, "Misc 6D0-6FF", true),
        GAME_STATE(0x0700..0x07FF, "Game State (mode, world, timers)", true);

        companion object {
            fun forAddress(addr: Int): MemoryRegion? = entries.find { addr in it.range }
        }
    }

    // Critical addresses that affect gameplay
    val criticalAddresses = mapOf(
        0x0009 to "FrameCounter",
        0x000A to "A_B_Buttons",
        0x000C to "Left_Right_Buttons",
        0x001D to "Player_State",
        0x0024 to "CrouchingFlag",
        0x0057 to "Player_X_Speed",
        0x006D to "Player_PageLoc",
        0x0086 to "Player_X_Position",
        0x009F to "Player_Y_Speed",
        0x00CE to "Player_Y_Position",
        0x0704 to "SwimmingFlag",
        0x071A to "ScreenLeft_PageLoc",
        0x0723 to "ScrollLock",
        0x0754 to "PlayerSize",
        0x075A to "NumberofLives",
        0x075F to "WorldNumber",
        0x0760 to "LevelNumber",
        0x0770 to "OperMode",
        0x0772 to "OperMode_Task",
        0x077F to "IntervalTimerControl",
        0x06FC to "SavedJoypad1Bits"
    )

    data class ByteDivergence(
        val address: Int,
        val firstDivergentFrame: Int,
        val fceuxValue: Int,
        val interpValue: Int,
        val region: MemoryRegion?,
        val name: String?
    )

    data class FrameSnapshot(
        val frame: Int,
        val totalDifferentBytes: Int,
        val criticalDifferences: List<Pair<Int, Pair<Int, Int>>>, // addr -> (fceux, interp)
        val regionDifferences: Map<MemoryRegion, Int> // region -> count of different bytes
    )

    @Test
    fun `analyze RAM divergence patterns`() {
        val fceuxFile = File("local/tas/fceux-full-ram.bin")
        val interpFile = File("local/tas/interpreter-full-ram.bin")

        if (!fceuxFile.exists() || !interpFile.exists()) {
            println("ERROR: RAM dump files not found. Run FullTASValidationTest first.")
            println("  Expected: local/tas/fceux-full-ram.bin")
            println("  Expected: local/tas/interpreter-full-ram.bin")
            return
        }

        val fceuxRam = fceuxFile.readBytes()
        val interpRam = interpFile.readBytes()

        val fceuxFrames = fceuxRam.size / 2048
        val interpFrames = interpRam.size / 2048
        val maxFrames = minOf(fceuxFrames, interpFrames, 6000)

        println("=== RAM Divergence Analysis ===")
        println("FCEUX frames: $fceuxFrames")
        println("Interpreter frames: $interpFrames")
        println("Analyzing: $maxFrames frames")
        println()

        // Track first divergence per address
        val firstDivergence = mutableMapOf<Int, ByteDivergence>()

        // Track frame-by-frame snapshots
        val snapshots = mutableListOf<FrameSnapshot>()

        // Output file for detailed analysis
        val reportFile = PrintWriter(File("local/tas/divergence-report.txt"))
        reportFile.println("# RAM Divergence Analysis Report")
        reportFile.println("# Generated: ${java.time.LocalDateTime.now()}")
        reportFile.println()

        // Analyze each frame
        for (frame in 0 until maxFrames) {
            val fceuxOffset = frame * 2048
            val interpOffset = frame * 2048

            var totalDiff = 0
            val criticalDiffs = mutableListOf<Pair<Int, Pair<Int, Int>>>()
            val regionDiffs = mutableMapOf<MemoryRegion, Int>()

            for (addr in 0 until 2048) {
                val fceuxByte = fceuxRam[fceuxOffset + addr].toInt() and 0xFF
                val interpByte = interpRam[interpOffset + addr].toInt() and 0xFF

                if (fceuxByte != interpByte) {
                    totalDiff++

                    // Track first divergence for this address
                    if (addr !in firstDivergence) {
                        val region = MemoryRegion.forAddress(addr)
                        firstDivergence[addr] = ByteDivergence(
                            address = addr,
                            firstDivergentFrame = frame,
                            fceuxValue = fceuxByte,
                            interpValue = interpByte,
                            region = region,
                            name = criticalAddresses[addr]
                        )
                    }

                    // Track critical differences
                    if (addr in criticalAddresses) {
                        criticalDiffs.add(addr to (fceuxByte to interpByte))
                    }

                    // Track region differences
                    val region = MemoryRegion.forAddress(addr)
                    if (region != null) {
                        regionDiffs[region] = regionDiffs.getOrDefault(region, 0) + 1
                    }
                }
            }

            snapshots.add(FrameSnapshot(frame, totalDiff, criticalDiffs, regionDiffs))

            // Log interesting frames
            if (frame < 10 || frame % 100 == 0 || criticalDiffs.isNotEmpty()) {
                if (totalDiff > 0) {
                    reportFile.println("Frame $frame: $totalDiff bytes differ")
                    for ((addr, values) in criticalDiffs) {
                        val name = criticalAddresses[addr] ?: "???"
                        reportFile.println("  CRITICAL: \$${addr.toString(16).padStart(4, '0')} ($name) FCEUX=${values.first} INTERP=${values.second}")
                    }
                }
            }
        }

        reportFile.println()
        reportFile.println("=" .repeat(60))
        reportFile.println("SUMMARY: First Divergence Per Address")
        reportFile.println("=" .repeat(60))

        // Sort by first divergent frame
        val sortedDivergences = firstDivergence.values.sortedBy { it.firstDivergentFrame }

        // Group by frame for easier reading
        val byFrame = sortedDivergences.groupBy { it.firstDivergentFrame }

        for ((frame, divergences) in byFrame.entries.take(50)) { // First 50 frames with new divergences
            reportFile.println()
            reportFile.println("Frame $frame: ${divergences.size} new divergent addresses")
            for (div in divergences.sortedBy { it.address }) {
                val addrStr = "\$${div.address.toString(16).padStart(4, '0')}"
                val nameStr = div.name?.let { " ($it)" } ?: ""
                val regionStr = div.region?.description ?: "Unknown"
                val criticalMarker = if (div.name != null) " [CRITICAL]" else ""
                reportFile.println("  $addrStr$nameStr: FCEUX=${div.fceuxValue} INTERP=${div.interpValue} ($regionStr)$criticalMarker")
            }
        }

        // Region summary
        reportFile.println()
        reportFile.println("=" .repeat(60))
        reportFile.println("REGION SUMMARY: First Frame Each Region Diverges")
        reportFile.println("=" .repeat(60))

        val regionFirstDivergence = mutableMapOf<MemoryRegion, Int>()
        for (div in sortedDivergences) {
            if (div.region != null && div.region !in regionFirstDivergence) {
                regionFirstDivergence[div.region] = div.firstDivergentFrame
            }
        }

        for ((region, frame) in regionFirstDivergence.entries.sortedBy { it.value }) {
            val marker = if (region.critical) "[CRITICAL]" else ""
            reportFile.println("  Frame $frame: ${region.name} - ${region.description} $marker")
        }

        // Critical address timeline
        reportFile.println()
        reportFile.println("=" .repeat(60))
        reportFile.println("CRITICAL ADDRESS DIVERGENCE TIMELINE")
        reportFile.println("=" .repeat(60))

        val criticalDivergences = sortedDivergences.filter { it.name != null }
        for (div in criticalDivergences) {
            val addrStr = "\$${div.address.toString(16).padStart(4, '0')}"
            reportFile.println("  Frame ${div.firstDivergentFrame}: $addrStr (${div.name}) FCEUX=${div.fceuxValue} INTERP=${div.interpValue}")
        }

        // Find the first "cascade" - where many addresses diverge at once
        reportFile.println()
        reportFile.println("=" .repeat(60))
        reportFile.println("DIVERGENCE CASCADES (frames with 10+ new divergent addresses)")
        reportFile.println("=" .repeat(60))

        for ((frame, divergences) in byFrame.entries) {
            if (divergences.size >= 10) {
                val critCount = divergences.count { it.name != null }
                reportFile.println("  Frame $frame: ${divergences.size} new addresses ($critCount critical)")

                // List regions affected
                val regions = divergences.mapNotNull { it.region }.distinct()
                reportFile.println("    Regions: ${regions.joinToString { it.name }}")
            }
        }

        // Frame-by-frame difference count for first 100 frames
        reportFile.println()
        reportFile.println("=" .repeat(60))
        reportFile.println("DIFFERENCE COUNT BY FRAME (first 200 frames)")
        reportFile.println("=" .repeat(60))

        for (snapshot in snapshots.take(200)) {
            val critMarker = if (snapshot.criticalDifferences.isNotEmpty())
                " [${snapshot.criticalDifferences.size} critical]" else ""
            reportFile.println("  Frame ${snapshot.frame}: ${snapshot.totalDifferentBytes} bytes differ$critMarker")
        }

        reportFile.close()

        // Console summary
        println("=== Analysis Complete ===")
        println()
        println("Total addresses that diverge: ${firstDivergence.size}")
        println("First divergent frame: ${sortedDivergences.firstOrNull()?.firstDivergentFrame ?: "none"}")
        println()

        println("Critical addresses divergence timeline:")
        for (div in criticalDivergences.take(20)) {
            println("  Frame ${div.firstDivergentFrame}: ${div.name} (FCEUX=${div.fceuxValue}, INTERP=${div.interpValue})")
        }

        println()
        println("Cascade events (10+ new divergences):")
        for ((frame, divergences) in byFrame.entries.filter { it.value.size >= 10 }.take(10)) {
            println("  Frame $frame: ${divergences.size} new addresses")
        }

        println()
        println("Detailed report written to: local/tas/divergence-report.txt")

        // Also create a machine-readable CSV for further analysis
        val csvFile = PrintWriter(File("local/tas/divergence-by-address.csv"))
        csvFile.println("address,address_hex,first_frame,fceux_value,interp_value,region,name,critical")
        for (div in sortedDivergences) {
            csvFile.println("${div.address},${div.address.toString(16).padStart(4,'0')},${div.firstDivergentFrame},${div.fceuxValue},${div.interpValue},${div.region?.name ?: ""},${div.name ?: ""},${div.name != null}")
        }
        csvFile.close()
        println("CSV data written to: local/tas/divergence-by-address.csv")
    }

    @Test
    fun `find first critical divergence with context`() {
        val fceuxFile = File("local/tas/fceux-full-ram.bin")
        val interpFile = File("local/tas/interpreter-full-ram.bin")

        if (!fceuxFile.exists() || !interpFile.exists()) {
            println("ERROR: RAM dump files not found.")
            return
        }

        val fceuxRam = fceuxFile.readBytes()
        val interpRam = interpFile.readBytes()
        val maxFrames = minOf(fceuxRam.size / 2048, interpRam.size / 2048)

        println("=== Finding First Critical Divergence ===")
        println()

        // Find first frame with ANY critical address difference
        for (frame in 0 until minOf(maxFrames, 500)) {
            val fceuxOffset = frame * 2048
            val interpOffset = frame * 2048

            val criticalDiffs = mutableListOf<Triple<Int, Int, Int>>() // addr, fceux, interp

            for ((addr, name) in criticalAddresses) {
                if (addr >= 2048) continue
                val fceuxByte = fceuxRam[fceuxOffset + addr].toInt() and 0xFF
                val interpByte = interpRam[interpOffset + addr].toInt() and 0xFF
                if (fceuxByte != interpByte) {
                    criticalDiffs.add(Triple(addr, fceuxByte, interpByte))
                }
            }

            if (criticalDiffs.isNotEmpty()) {
                println("FIRST CRITICAL DIVERGENCE at Frame $frame")
                println()

                for ((addr, fceux, interp) in criticalDiffs) {
                    val name = criticalAddresses[addr]
                    println("  \$${addr.toString(16).padStart(4, '0')} ($name): FCEUX=$fceux INTERP=$interp (diff=${interp - fceux})")
                }
                println()

                // Show context: 5 frames before and after
                println("Context (critical addresses, frames ${maxOf(0, frame-5)} to ${minOf(maxFrames-1, frame+5)}):")
                println()

                val addresses = listOf(0x0009, 0x0770, 0x0772, 0x0086, 0x00CE, 0x001D, 0x075A)
                print("Frame  | ")
                for (addr in addresses) {
                    val name = criticalAddresses[addr]?.take(8)?.padEnd(8) ?: addr.toString(16).padStart(4, '0').padEnd(8)
                    print("$name | ")
                }
                println()
                println("-".repeat(80))

                for (f in maxOf(0, frame - 5)..minOf(maxFrames - 1, frame + 5)) {
                    val marker = if (f == frame) ">>>" else "   "
                    print("$marker${f.toString().padStart(4)} | ")

                    for (addr in addresses) {
                        val fOffset = f * 2048
                        val fceuxVal = fceuxRam[fOffset + addr].toInt() and 0xFF
                        val interpVal = interpRam[fOffset + addr].toInt() and 0xFF
                        val match = if (fceuxVal == interpVal) " " else "*"
                        print("${fceuxVal.toString().padStart(3)}/${interpVal.toString().padStart(3)}$match| ")
                    }
                    println()
                }

                return
            }
        }

        println("No critical divergence found in first 500 frames")
    }
}
