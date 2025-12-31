@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.interpreter

import java.io.File
import java.io.PrintWriter
import kotlin.test.Test

/**
 * Analyzes RAM divergence with frame offset compensation.
 *
 * Key insight: FCEUX frame numbering starts at 1, interpreter starts at 0.
 * So FCEUX frame N should be compared to interpreter frame N-1.
 */
class RAMDivergenceAnalyzerV2 {

    val criticalAddresses = mapOf(
        0x0009 to "FrameCounter",
        0x000A to "A_B_Buttons",
        0x000C to "Left_Right_Buttons",
        0x001D to "Player_State",
        0x0057 to "Player_X_Speed",
        0x006D to "Player_PageLoc",
        0x0086 to "Player_X_Position",
        0x009F to "Player_Y_Speed",
        0x00CE to "Player_Y_Position",
        0x0704 to "SwimmingFlag",
        0x071A to "ScreenLeft_PageLoc",
        0x0754 to "PlayerSize",
        0x075A to "NumberofLives",
        0x075F to "WorldNumber",
        0x0760 to "LevelNumber",
        0x0770 to "OperMode",
        0x0772 to "OperMode_Task",
        0x077F to "IntervalTimerControl",
        0x06FC to "SavedJoypad1Bits"
    )

    @Test
    fun `analyze with 1-frame offset compensation`() {
        val fceuxFile = File("local/tas/fceux-full-ram.bin")
        val interpFile = File("local/tas/interpreter-full-ram.bin")

        if (!fceuxFile.exists() || !interpFile.exists()) {
            println("ERROR: RAM dump files not found.")
            return
        }

        val fceuxRam = fceuxFile.readBytes()
        val interpRam = interpFile.readBytes()

        // FCEUX starts at frame 1, we start at frame 0
        // So compare FCEUX frame F to interpreter frame F-1
        val frameOffset = 1  // FCEUX frame X compares to interp frame X-1

        println("=== Frame-Offset Compensated Analysis ===")
        println("Comparing FCEUX frame N to Interpreter frame N-$frameOffset")
        println()

        val reportFile = PrintWriter(File("local/tas/divergence-offset-report.txt"))
        reportFile.println("# Frame-Offset Compensated Divergence Analysis")
        reportFile.println("# FCEUX frame N compared to Interpreter frame N-$frameOffset")
        reportFile.println()

        val maxFceuxFrame = fceuxRam.size / 2048
        val maxInterpFrame = interpRam.size / 2048

        var totalPerfectMatches = 0
        var firstDivergentFrame = -1

        for (fceuxFrame in 1 until minOf(maxFceuxFrame, 200)) {
            val interpFrame = fceuxFrame - frameOffset
            if (interpFrame < 0 || interpFrame >= maxInterpFrame) continue

            val fceuxOffset = fceuxFrame * 2048
            val interpOffset = interpFrame * 2048

            var diffCount = 0
            val criticalDiffs = mutableListOf<Triple<Int, Int, Int>>() // addr, fceux, interp

            for (addr in 0 until 2048) {
                val fceuxByte = fceuxRam[fceuxOffset + addr].toInt() and 0xFF
                val interpByte = interpRam[interpOffset + addr].toInt() and 0xFF
                if (fceuxByte != interpByte) {
                    diffCount++
                    if (addr in criticalAddresses) {
                        criticalDiffs.add(Triple(addr, fceuxByte, interpByte))
                    }
                }
            }

            if (diffCount == 0) {
                totalPerfectMatches++
                if (fceuxFrame <= 60) {
                    reportFile.println("FCEUX Frame $fceuxFrame = Interp Frame $interpFrame: PERFECT MATCH!")
                }
            } else {
                if (firstDivergentFrame < 0) firstDivergentFrame = fceuxFrame
                reportFile.println("FCEUX Frame $fceuxFrame = Interp Frame $interpFrame: $diffCount bytes differ")
                for ((addr, fceux, interp) in criticalDiffs) {
                    val name = criticalAddresses[addr]
                    reportFile.println("  CRITICAL: \$${addr.toString(16).padStart(4, '0')} ($name) FCEUX=$fceux INTERP=$interp")
                }
            }
        }

        reportFile.println()
        reportFile.println("=== SUMMARY ===")
        reportFile.println("Perfect matches: $totalPerfectMatches out of first 199 frames")
        reportFile.println("First divergent frame: $firstDivergentFrame")
        reportFile.close()

        println("Perfect matches (first 200 frames): $totalPerfectMatches")
        println("First divergent frame: $firstDivergentFrame")
        println("Report written to: local/tas/divergence-offset-report.txt")
    }

    @Test
    fun `try multiple offsets to find best match`() {
        val fceuxFile = File("local/tas/fceux-full-ram.bin")
        val interpFile = File("local/tas/interpreter-full-ram.bin")

        if (!fceuxFile.exists() || !interpFile.exists()) {
            System.err.println("ERROR: RAM dump files not found.")
            return
        }

        val fceuxRam = fceuxFile.readBytes()
        val interpRam = interpFile.readBytes()

        val out = PrintWriter(File("local/tas/offset-analysis.txt"))
        out.println("=== Testing Different Frame Offsets ===")
        out.println()

        // Try offsets from -3 to +3
        for (frameOffset in -3..3) {
            var perfectMatches = 0
            var totalCompared = 0

            for (fceuxFrame in 10 until minOf(fceuxRam.size / 2048, 100)) {
                val interpFrame = fceuxFrame - frameOffset
                if (interpFrame < 0 || interpFrame >= interpRam.size / 2048) continue

                val fceuxOffset = fceuxFrame * 2048
                val interpOffset = interpFrame * 2048

                var diffCount = 0
                for (addr in 0 until 2048) {
                    val fceuxByte = fceuxRam[fceuxOffset + addr].toInt() and 0xFF
                    val interpByte = interpRam[interpOffset + addr].toInt() and 0xFF
                    if (fceuxByte != interpByte) diffCount++
                }

                totalCompared++
                if (diffCount == 0) perfectMatches++
            }

            out.println("Offset $frameOffset: $perfectMatches perfect matches out of $totalCompared frames (${100.0 * perfectMatches / totalCompared}%)")
        }
        out.close()
    }

    @Test
    fun `detailed comparison at key frames`() {
        val fceuxFile = File("local/tas/fceux-full-ram.bin")
        val interpFile = File("local/tas/interpreter-full-ram.bin")

        if (!fceuxFile.exists() || !interpFile.exists()) {
            System.err.println("ERROR: RAM dump files not found.")
            return
        }

        val fceuxRam = fceuxFile.readBytes()
        val interpRam = interpFile.readBytes()

        val out = PrintWriter(File("local/tas/key-frame-analysis.txt"))
        out.println("=== Detailed Comparison at Key Frames ===")
        out.println()

        // Compare at frame 41 (where OperMode changes in FCEUX)
        // and try different interpreter frames to see which matches best
        val targetFceuxFrame = 41

        out.println("FCEUX Frame $targetFceuxFrame state:")
        val fOffset = targetFceuxFrame * 2048
        for ((addr, name) in criticalAddresses) {
            val value = fceuxRam[fOffset + addr].toInt() and 0xFF
            out.println("  \$${addr.toString(16).padStart(4, '0')} ($name) = $value")
        }

        out.println()
        out.println("Finding best matching interpreter frame...")

        var bestMatch = -1
        var bestMatchScore = Int.MAX_VALUE

        for (interpFrame in 38..48) {
            val iOffset = interpFrame * 2048
            var diffScore = 0
            for ((addr, _) in criticalAddresses) {
                val fceuxVal = fceuxRam[fOffset + addr].toInt() and 0xFF
                val interpVal = interpRam[iOffset + addr].toInt() and 0xFF
                diffScore += kotlin.math.abs(fceuxVal - interpVal)
            }
            out.println("  Interp Frame $interpFrame: total diff score = $diffScore")
            if (diffScore < bestMatchScore) {
                bestMatchScore = diffScore
                bestMatch = interpFrame
            }
        }

        out.println()
        out.println("Best match: Interpreter frame $bestMatch (diff score: $bestMatchScore)")
        out.println()

        // Show the best match details
        val iOffset = bestMatch * 2048
        out.println("Comparison (FCEUX frame $targetFceuxFrame vs Interpreter frame $bestMatch):")
        for ((addr, name) in criticalAddresses) {
            val fceuxVal = fceuxRam[fOffset + addr].toInt() and 0xFF
            val interpVal = interpRam[iOffset + addr].toInt() and 0xFF
            val match = if (fceuxVal == interpVal) "=" else "!="
            out.println("  \$${addr.toString(16).padStart(4, '0')} ($name): FCEUX=$fceuxVal $match INTERP=$interpVal")
        }
        out.close()
    }
}
