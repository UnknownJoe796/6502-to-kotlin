@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.interpreter

import java.io.File
import java.io.PrintWriter
import kotlin.test.Test

/**
 * Detailed analysis of early frames (0-5) to understand divergence.
 */
class EarlyFrameAnalysisTest {

    private fun loadFceuxRam(): ByteArray? {
        val file = File("local/tas/fceux-full-ram.bin")
        return if (file.exists()) file.readBytes() else null
    }

    private fun loadInterpRam(): ByteArray? {
        val file = File("local/tas/interpreter-full-ram.bin")
        return if (file.exists()) file.readBytes() else null
    }

    @Test
    fun `analyze frames 0-5 in detail`() {
        val fceuxRam = loadFceuxRam() ?: run { println("No FCEUX RAM"); return }
        val interpRam = loadInterpRam() ?: run { println("No interp RAM"); return }

        val outFile = PrintWriter(File("local/tas/early-frame-analysis.txt"))
        fun log(s: String) { println(s); outFile.println(s) }

        log("=== EARLY FRAME ANALYSIS ===")
        log("")

        // Key addresses to track
        val keyAddrs = mapOf(
            0x0009 to "FrameCounter",
            0x077F to "IntervalTimerControl",
            0x0770 to "OperMode",
            0x0772 to "OperMode_Task",
            0x0086 to "Player_X_Position",
            0x00CE to "Player_Y_Position",
            0x06FC to "SavedJoypad1Bits",
            0x0754 to "PlayerSize",
            0x075F to "WorldNumber",
        )

        for (frame in 0..5) {
            log("=== FRAME $frame ===")

            val fOffset = frame * 2048
            val iOffset = frame * 2048

            // Compare key addresses
            for ((addr, name) in keyAddrs) {
                val fVal = fceuxRam[fOffset + addr].toInt() and 0xFF
                val iVal = interpRam[iOffset + addr].toInt() and 0xFF
                val match = if (fVal == iVal) "✓" else "✗"
                log("  $match $name (\$${addr.toString(16).padStart(4,'0')}): FCEUX=$fVal INTERP=$iVal")
            }

            // Count total differences
            var diffCount = 0
            for (addr in 0 until 0x800) {
                val fVal = fceuxRam[fOffset + addr].toInt() and 0xFF
                val iVal = interpRam[iOffset + addr].toInt() and 0xFF
                if (fVal != iVal) diffCount++
            }
            log("  Total RAM differences: $diffCount / 2048 bytes")
            log("")
        }

        // Specific analysis: what changed from frame 0 to frame 1?
        log("=== CHANGES FROM FRAME 0 TO FRAME 1 ===")
        log("")
        log("FCEUX changes:")
        for (addr in 0 until 0x800) {
            val f0 = fceuxRam[addr].toInt() and 0xFF
            val f1 = fceuxRam[2048 + addr].toInt() and 0xFF
            if (f0 != f1) {
                val name = keyAddrs[addr] ?: ""
                log("  \$${addr.toString(16).padStart(4,'0')} $name: $f0 -> $f1")
            }
        }
        log("")
        log("Interpreter changes:")
        for (addr in 0 until 0x800) {
            val i0 = interpRam[addr].toInt() and 0xFF
            val i1 = interpRam[2048 + addr].toInt() and 0xFF
            if (i0 != i1) {
                val name = keyAddrs[addr] ?: ""
                log("  \$${addr.toString(16).padStart(4,'0')} $name: $i0 -> $i1")
            }
        }

        // Find addresses where FCEUX stayed same but interpreter changed
        log("")
        log("=== ADDRESSES WHERE ONLY INTERPRETER CHANGED (FRAME 0->1) ===")
        for (addr in 0 until 0x800) {
            val f0 = fceuxRam[addr].toInt() and 0xFF
            val f1 = fceuxRam[2048 + addr].toInt() and 0xFF
            val i0 = interpRam[addr].toInt() and 0xFF
            val i1 = interpRam[2048 + addr].toInt() and 0xFF

            val fceuxChanged = (f0 != f1)
            val interpChanged = (i0 != i1)

            if (!fceuxChanged && interpChanged) {
                val name = keyAddrs[addr] ?: ""
                log("  \$${addr.toString(16).padStart(4,'0')} $name: FCEUX stayed $f0, INTERP $i0 -> $i1")
            }
        }

        log("")
        log("=== ADDRESSES WHERE ONLY FCEUX CHANGED (FRAME 0->1) ===")
        for (addr in 0 until 0x800) {
            val f0 = fceuxRam[addr].toInt() and 0xFF
            val f1 = fceuxRam[2048 + addr].toInt() and 0xFF
            val i0 = interpRam[addr].toInt() and 0xFF
            val i1 = interpRam[2048 + addr].toInt() and 0xFF

            val fceuxChanged = (f0 != f1)
            val interpChanged = (i0 != i1)

            if (fceuxChanged && !interpChanged) {
                val name = keyAddrs[addr] ?: ""
                log("  \$${addr.toString(16).padStart(4,'0')} $name: FCEUX $f0 -> $f1, INTERP stayed $i0")
            }
        }

        outFile.close()
        println("\nOutput written to local/tas/early-frame-analysis.txt")
    }

    @Test
    fun `analyze computation bug frames 5-8 and 39-43`() {
        val fceuxRam = loadFceuxRam() ?: run { println("No FCEUX RAM"); return }
        val interpRam = loadInterpRam() ?: run { println("No interp RAM"); return }

        val outFile = PrintWriter(File("local/tas/computation-bug-analysis.txt"))
        fun log(s: String) { println(s); outFile.println(s) }

        // Key addresses including NMI-related
        val keyAddrs = mapOf(
            0x0009 to "FrameCounter",
            0x077F to "IntervalTimerControl",
            0x0770 to "OperMode",
            0x0772 to "OperMode_Task",
            0x06FC to "SavedJoypad1Bits",
            0x000A to "A_B_Buttons",
            0x000C to "Left_Right_Buttons",
            0x0776 to "PauseModeFlag",
            0x0778 to "PauseCounter",
            0x00FE to "NMI_scratch_FE",
            0x00FF to "NMI_scratch_FF",
        )

        log("=== COMPUTATION BUG ANALYSIS ===")
        log("Comparing FCEUX frame N with Interpreter frame N-1 (timing offset)")
        log("")

        // Analyze frames 5-8 (FrameCounter/OperMode_Task bugs)
        log("=== FRAMES 5-8: FrameCounter & OperMode_Task bugs ===")
        for (fceuxFrame in 5..8) {
            val interpFrame = fceuxFrame - 1
            log("FCEUX Frame $fceuxFrame vs Interp Frame $interpFrame:")

            val fOffset = fceuxFrame * 2048
            val iOffset = interpFrame * 2048

            for ((addr, name) in keyAddrs) {
                val fVal = fceuxRam[fOffset + addr].toInt() and 0xFF
                val iVal = interpRam[iOffset + addr].toInt() and 0xFF
                val match = if (fVal == iVal) "✓" else "✗"
                log("  $match $name: FCEUX=$fVal INTERP=$iVal")
            }
            log("")
        }

        // Analyze frames 39-43 (Start button / OperMode bug)
        log("=== FRAMES 39-43: Start Button & OperMode bug ===")
        for (fceuxFrame in 39..43) {
            val interpFrame = fceuxFrame - 1
            log("FCEUX Frame $fceuxFrame vs Interp Frame $interpFrame:")

            val fOffset = fceuxFrame * 2048
            val iOffset = interpFrame * 2048

            for ((addr, name) in keyAddrs) {
                val fVal = fceuxRam[fOffset + addr].toInt() and 0xFF
                val iVal = interpRam[iOffset + addr].toInt() and 0xFF
                val match = if (fVal == iVal) "✓" else "✗"
                log("  $match $name: FCEUX=$fVal INTERP=$iVal")
            }
            log("")
        }

        // Look at deltas around the Start button frame
        log("=== DELTA ANALYSIS: What changed at FCEUX frame 41? ===")
        log("FCEUX changes (frame 40 -> 41):")
        val f40 = 40 * 2048
        val f41 = 41 * 2048
        for (addr in 0 until 0x800) {
            val v40 = fceuxRam[f40 + addr].toInt() and 0xFF
            val v41 = fceuxRam[f41 + addr].toInt() and 0xFF
            if (v40 != v41) {
                val name = keyAddrs[addr] ?: ""
                log("  \$${addr.toString(16).padStart(4,'0')} $name: $v40 -> $v41")
            }
        }

        log("")
        log("Interpreter changes (frame 39 -> 40):")
        val i39 = 39 * 2048
        val i40 = 40 * 2048
        for (addr in 0 until 0x800) {
            val v39 = interpRam[i39 + addr].toInt() and 0xFF
            val v40 = interpRam[i40 + addr].toInt() and 0xFF
            if (v39 != v40) {
                val name = keyAddrs[addr] ?: ""
                log("  \$${addr.toString(16).padStart(4,'0')} $name: $v39 -> $v40")
            }
        }

        outFile.close()
        println("\nOutput written to local/tas/computation-bug-analysis.txt")
    }
}
