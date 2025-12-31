@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.interpreter

import java.io.File
import java.io.PrintWriter
import kotlin.test.Test

/**
 * Delta Comparison Test
 *
 * Instead of comparing absolute RAM values, we compare DELTAS:
 * - FCEUX delta = RAM[frame N+1] - RAM[frame N]
 * - Interpreter delta = RAM[frame N+1] - RAM[frame N]
 *
 * If both emulators compute the same changes, their deltas should match
 * even if their absolute values diverged earlier.
 *
 * This isolates "computation bugs" from "timing/sync bugs":
 * - Same delta = correct computation, timing issue
 * - Different delta = actual computation bug
 */
class DeltaComparisonTest {

    data class RamDelta(
        val address: Int,
        val oldValue: Int,
        val newValue: Int
    ) {
        val change: Int get() = newValue - oldValue
    }

    data class FrameDelta(
        val frame: Int,
        val changes: Map<Int, RamDelta>  // address -> delta
    )

    data class DeltaMismatch(
        val address: Int,
        val fceuxOld: Int,
        val fceuxNew: Int,
        val interpOld: Int,
        val interpNew: Int,
        val name: String?
    ) {
        val fceuxDelta: Int get() = fceuxNew - fceuxOld
        val interpDelta: Int get() = interpNew - interpOld
    }

    // Key addresses to focus on
    val KEY_ADDRESSES = mapOf(
        0x0009 to "FrameCounter",
        0x0086 to "Player_X_Position",
        0x00CE to "Player_Y_Position",
        0x0057 to "Player_X_Speed",
        0x009F to "Player_Y_Speed",
        0x001D to "Player_State",
        0x0770 to "OperMode",
        0x0772 to "OperMode_Task",
        0x075A to "NumberofLives",
        0x075F to "WorldNumber",
        0x0760 to "LevelNumber",
        0x06FC to "SavedJoypad1Bits",
        0x000A to "A_B_Buttons",
        0x000C to "Left_Right_Buttons",
        0x077F to "IntervalTimerControl",
        0x0754 to "PlayerSize",
        0x006D to "Player_PageLoc",
        0x071A to "ScreenLeft_PageLoc",
    )

    // Addresses to ignore in delta comparison (they're expected to differ)
    val IGNORE_ADDRESSES = (0x0100..0x01FF).toSet() + // Stack
            (0x0200..0x02FF).toSet() // OAM buffer

    private fun loadFceuxRam(): ByteArray? {
        val file = File("local/tas/fceux-full-ram.bin")
        return if (file.exists()) file.readBytes() else null
    }

    private fun loadInterpRam(): ByteArray? {
        val file = File("local/tas/interpreter-full-ram.bin")
        return if (file.exists()) file.readBytes() else null
    }

    private fun computeDelta(ram: ByteArray, frame: Int): Map<Int, RamDelta> {
        val offset1 = frame * 2048
        val offset2 = (frame + 1) * 2048
        if (offset2 + 2048 > ram.size) return emptyMap()

        val deltas = mutableMapOf<Int, RamDelta>()
        for (addr in 0 until 0x800) {
            if (addr in IGNORE_ADDRESSES) continue
            val old = ram[offset1 + addr].toInt() and 0xFF
            val new = ram[offset2 + addr].toInt() and 0xFF
            if (old != new) {
                deltas[addr] = RamDelta(addr, old, new)
            }
        }
        return deltas
    }

    private fun compareDeltasAtMatchingStates(
        fceuxRam: ByteArray,
        interpRam: ByteArray,
        reportFile: PrintWriter
    ): List<Pair<Int, List<DeltaMismatch>>> {
        // Strategy: Find frames where key game state matches, then compare deltas

        val fceuxFrames = fceuxRam.size / 2048
        val interpFrames = interpRam.size / 2048
        val maxFrames = minOf(fceuxFrames, interpFrames) - 1

        val results = mutableListOf<Pair<Int, List<DeltaMismatch>>>()

        // For each FCEUX frame, find the best matching interpreter frame
        // based on key game state variables

        data class GameState(
            val operMode: Int,
            val operModeTask: Int,
            val playerX: Int,
            val playerY: Int,
            val world: Int,
            val level: Int,
            val fc: Int
        )

        fun extractState(ram: ByteArray, offset: Int): GameState {
            return GameState(
                operMode = ram[offset + 0x0770].toInt() and 0xFF,
                operModeTask = ram[offset + 0x0772].toInt() and 0xFF,
                playerX = ram[offset + 0x0086].toInt() and 0xFF,
                playerY = ram[offset + 0x00CE].toInt() and 0xFF,
                world = ram[offset + 0x075F].toInt() and 0xFF,
                level = ram[offset + 0x0760].toInt() and 0xFF,
                fc = ram[offset + 0x0009].toInt() and 0xFF
            )
        }

        // Build index of interpreter states
        val interpStateIndex = mutableMapOf<GameState, MutableList<Int>>()
        for (frame in 0 until interpFrames) {
            val state = extractState(interpRam, frame * 2048)
            interpStateIndex.getOrPut(state) { mutableListOf() }.add(frame)
        }

        reportFile.println("# Delta Comparison Report")
        reportFile.println("# Comparing RAM deltas between FCEUX and interpreter at matching game states")
        reportFile.println()

        var matchedFrames = 0
        var framesWithDeltaMismatch = 0

        for (fceuxFrame in 0 until minOf(maxFrames, 1000)) {
            val fceuxState = extractState(fceuxRam, fceuxFrame * 2048)

            // Find interpreter frames with matching state
            val matchingInterpFrames = interpStateIndex[fceuxState] ?: continue
            if (matchingInterpFrames.isEmpty()) continue

            // Use the first matching interpreter frame
            val interpFrame = matchingInterpFrames.first()
            if (interpFrame >= interpFrames - 1) continue

            matchedFrames++

            // Compute deltas for both
            val fceuxDelta = computeDelta(fceuxRam, fceuxFrame)
            val interpDelta = computeDelta(interpRam, interpFrame)

            // Find addresses that changed differently
            val allAddrs = (fceuxDelta.keys + interpDelta.keys).filter { it !in IGNORE_ADDRESSES }
            val mismatches = mutableListOf<DeltaMismatch>()

            for (addr in allAddrs) {
                val fd = fceuxDelta[addr]
                val id = interpDelta[addr]

                // Get values for comparison
                val fceuxOld = if (fd != null) fd.oldValue else (fceuxRam[fceuxFrame * 2048 + addr].toInt() and 0xFF)
                val fceuxNew = if (fd != null) fd.newValue else fceuxOld
                val interpOld = if (id != null) id.oldValue else (interpRam[interpFrame * 2048 + addr].toInt() and 0xFF)
                val interpNew = if (id != null) id.newValue else interpOld

                val fceuxChange = fceuxNew - fceuxOld
                val interpChange = interpNew - interpOld

                if (fceuxChange != interpChange) {
                    mismatches.add(DeltaMismatch(
                        addr, fceuxOld, fceuxNew, interpOld, interpNew,
                        KEY_ADDRESSES[addr]
                    ))
                }
            }

            if (mismatches.isNotEmpty()) {
                framesWithDeltaMismatch++
                results.add(fceuxFrame to mismatches)

                reportFile.println("=== FCEUX Frame $fceuxFrame <-> Interp Frame $interpFrame ===")
                reportFile.println("State: Mode=${fceuxState.operMode} Task=${fceuxState.operModeTask} " +
                        "X=${fceuxState.playerX} Y=${fceuxState.playerY} W${fceuxState.world+1}-${fceuxState.level+1} FC=${fceuxState.fc}")
                reportFile.println("Delta mismatches: ${mismatches.size}")

                // Show key mismatches first
                val keyMismatches = mismatches.filter { it.name != null }
                if (keyMismatches.isNotEmpty()) {
                    reportFile.println("KEY ADDRESS MISMATCHES:")
                    for (m in keyMismatches) {
                        reportFile.println("  ${m.name} (\$${m.address.toString(16)}): " +
                                "FCEUX ${m.fceuxOld}->${m.fceuxNew} (Δ${m.fceuxDelta}) vs " +
                                "INTERP ${m.interpOld}->${m.interpNew} (Δ${m.interpDelta})")
                    }
                }

                // Show first 5 other mismatches
                val otherMismatches = mismatches.filter { it.name == null }.take(5)
                if (otherMismatches.isNotEmpty()) {
                    reportFile.println("OTHER MISMATCHES (first 5):")
                    for (m in otherMismatches) {
                        reportFile.println("  \$${m.address.toString(16).padStart(4, '0')}: " +
                                "FCEUX ${m.fceuxOld}->${m.fceuxNew} (Δ${m.fceuxDelta}) vs " +
                                "INTERP ${m.interpOld}->${m.interpNew} (Δ${m.interpDelta})")
                    }
                }
                reportFile.println()
            }
        }

        reportFile.println("=== SUMMARY ===")
        reportFile.println("Matched game states: $matchedFrames")
        reportFile.println("States with delta mismatch: $framesWithDeltaMismatch")
        reportFile.println()

        return results
    }

    @Test
    fun `delta comparison - find computation differences`() {
        val fceuxRam = loadFceuxRam()
        if (fceuxRam == null) {
            println("⚠️ No FCEUX RAM dump found")
            return
        }

        val interpRam = loadInterpRam()
        if (interpRam == null) {
            println("⚠️ No interpreter RAM dump found")
            return
        }

        val reportFile = PrintWriter(File("local/tas/delta-comparison-report.txt"))

        println("Comparing RAM deltas...")
        println("FCEUX frames: ${fceuxRam.size / 2048}")
        println("Interpreter frames: ${interpRam.size / 2048}")
        println()

        val mismatches = compareDeltasAtMatchingStates(fceuxRam, interpRam, reportFile)

        reportFile.close()

        println("=== DELTA COMPARISON COMPLETE ===")
        println("Found ${mismatches.size} frame pairs with delta mismatches")
        println("Report: local/tas/delta-comparison-report.txt")

        if (mismatches.isNotEmpty()) {
            println()
            println("First mismatch at FCEUX frame ${mismatches[0].first}:")
            val firstMismatches = mismatches[0].second.filter { it.name != null }.take(5)
            for (m in firstMismatches) {
                println("  ${m.name}: FCEUX Δ${m.fceuxDelta} vs INTERP Δ${m.interpDelta}")
            }
        }
    }

    @Test
    fun `direct frame-by-frame delta comparison`() {
        // Simpler approach: compare same frame numbers directly
        // This finds where the interpreter diverges even without state matching

        val fceuxRam = loadFceuxRam()
        val interpRam = loadInterpRam()

        if (fceuxRam == null || interpRam == null) {
            println("⚠️ Missing RAM dumps")
            return
        }

        val reportFile = PrintWriter(File("local/tas/direct-delta-report.txt"))
        reportFile.println("# Direct Frame-by-Frame Delta Comparison")
        reportFile.println("# Comparing frame N delta in FCEUX vs frame N delta in interpreter")
        reportFile.println()

        val maxFrames = minOf(fceuxRam.size / 2048, interpRam.size / 2048, 500) - 1
        var mismatchCount = 0

        for (frame in 0 until maxFrames) {
            val fceuxDelta = computeDelta(fceuxRam, frame)
            val interpDelta = computeDelta(interpRam, frame)

            // Compare only key addresses
            val keyMismatches = mutableListOf<DeltaMismatch>()

            for ((addr, name) in KEY_ADDRESSES) {
                val fd = fceuxDelta[addr]
                val id = interpDelta[addr]

                val fceuxOld = fceuxRam[frame * 2048 + addr].toInt() and 0xFF
                val fceuxNew = fceuxRam[(frame + 1) * 2048 + addr].toInt() and 0xFF
                val interpOld = interpRam[frame * 2048 + addr].toInt() and 0xFF
                val interpNew = interpRam[(frame + 1) * 2048 + addr].toInt() and 0xFF

                val fceuxChange = fceuxNew - fceuxOld
                val interpChange = interpNew - interpOld

                // Only report if BOTH have the same starting value but different deltas
                // This indicates a computation bug, not a cumulative difference
                if (fceuxOld == interpOld && fceuxChange != interpChange) {
                    keyMismatches.add(DeltaMismatch(addr, fceuxOld, fceuxNew, interpOld, interpNew, name))
                }
            }

            if (keyMismatches.isNotEmpty()) {
                mismatchCount++
                reportFile.println("=== Frame $frame ===")
                for (m in keyMismatches) {
                    reportFile.println("  ${m.name}: start=${m.fceuxOld} -> FCEUX=${m.fceuxNew} (Δ${m.fceuxDelta}) INTERP=${m.interpNew} (Δ${m.interpDelta})")
                }
                reportFile.println()

                // Stop at first few mismatches for focused debugging
                if (mismatchCount >= 20) {
                    reportFile.println("... (stopping after 20 mismatches)")
                    break
                }
            }
        }

        reportFile.println()
        reportFile.println("Total frames with key address delta mismatch: $mismatchCount")
        reportFile.close()

        println("=== DIRECT DELTA COMPARISON ===")
        println("Frames analyzed: $maxFrames")
        println("Frames with key delta mismatch (same start, different change): $mismatchCount")
        println("Report: local/tas/direct-delta-report.txt")
    }

    @Test
    fun `find first critical divergence point`() {
        // Find the exact frame and address where FCEUX and interpreter first differ
        // on a CRITICAL address, starting from matching initial states

        val fceuxRam = loadFceuxRam()
        val interpRam = loadInterpRam()

        if (fceuxRam == null || interpRam == null) {
            println("⚠️ Missing RAM dumps")
            return
        }

        val maxFrames = minOf(fceuxRam.size / 2048, interpRam.size / 2048, 1000)

        val outFile = PrintWriter(File("local/tas/first-divergence.txt"))
        fun log(s: String) { println(s); outFile.println(s) }

        log("Searching for first critical divergence...")
        log("")

        // Track which addresses have diverged
        val divergedAt = mutableMapOf<Int, Int>()  // address -> first divergent frame

        // CRITICAL TIMING INSIGHT:
        // - FCEUX dumps RAM at START of frame (before code execution)
        // - Interpreter dumps RAM at END of frame (after code execution)
        // - So FCEUX frame N = interpreter frame N-1 (interpreter is 1 frame ahead)
        //
        // To compare properly: FCEUX frame F should match interpreter frame F-1

        for (frame in 1 until maxFrames) {  // Start at 1 since we need F-1 for interpreter
            val fOffset = frame * 2048           // FCEUX frame N
            val iOffset = (frame - 1) * 2048     // Interpreter frame N-1

            for ((addr, name) in KEY_ADDRESSES) {
                if (addr in divergedAt) continue  // Already diverged

                val fVal = fceuxRam[fOffset + addr].toInt() and 0xFF
                val iVal = interpRam[iOffset + addr].toInt() and 0xFF

                if (fVal != iVal) {
                    divergedAt[addr] = frame
                    log("Frame $frame: $name (\$${addr.toString(16)}) diverges")
                    log("  FCEUX=$fVal, INTERP=$iVal, diff=${iVal - fVal}")

                    // Show context - what were the values in previous frame?
                    if (frame > 0) {
                        val prevF = fceuxRam[(frame - 1) * 2048 + addr].toInt() and 0xFF
                        val prevI = interpRam[(frame - 1) * 2048 + addr].toInt() and 0xFF
                        log("  Previous frame: FCEUX=$prevF, INTERP=$prevI")

                        if (prevF == prevI) {
                            log("  ** COMPUTATION BUG: Same value at frame ${frame-1}, different at frame $frame **")
                        } else {
                            log("  (Already diverged in previous frame)")
                        }
                    }
                    log("")
                }
            }

            // Stop after we've found divergence in key addresses
            if (divergedAt.size >= KEY_ADDRESSES.size / 2) {
                log("Found divergence in ${divergedAt.size} key addresses, stopping search")
                break
            }
        }

        log("")
        log("=== DIVERGENCE SUMMARY ===")
        for ((addr, frame) in divergedAt.entries.sortedBy { it.value }) {
            val name = KEY_ADDRESSES[addr] ?: "?"
            log("Frame $frame: $name (\$${addr.toString(16).padStart(4, '0')})")
        }
        outFile.close()
    }
}
