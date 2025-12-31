@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.interpreter

import java.io.File
import kotlin.test.Test

/**
 * TAS Calibration Test - Phase 1 of the per-level offset map approach.
 *
 * This test runs the interpreter with NO dynamic adjustment and compares
 * FrameCounter values with FCEUX at key points to identify:
 * 1. When level transitions occur in each emulator
 * 2. The exact timing drift at each transition
 * 3. A static offset map that can be applied during playback
 */
class TASCalibrationTest {

    // SMB memory addresses
    object SMB {
        const val OperMode = 0x0770
        const val OperMode_Task = 0x0772
        const val WorldNumber = 0x075F
        const val LevelNumber = 0x0760
        const val AreaNumber = 0x075C
        const val Player_X_Position = 0x0086
        const val Player_Y_Position = 0x00CE
        const val ScreenLeft_PageLoc = 0x071A
        const val FrameCounter = 0x09
        const val NumberofLives = 0x075A
        const val Player_State = 0x001D
        const val GameEngineSubroutine = 0x000E
    }

    data class FrameState(
        val frame: Int,
        val fc: Int,
        val world: Int,
        val level: Int,
        val operMode: Int,
        val page: Int,
        val playerX: Int,
        val lives: Int,
        val state: Int
    )

    @Test
    fun `calibrate level transitions`() {
        val fceuxRamFile = File("local/tas/fceux-full-ram.bin")
        if (!fceuxRamFile.exists()) {
            println("FCEUX RAM dump not found - cannot calibrate")
            return
        }

        val fceuxRam = fceuxRamFile.readBytes()
        val maxFrame = fceuxRam.size / 2048

        println("=== TAS Calibration: Level Transition Analysis ===")
        println("FCEUX RAM dump: $maxFrame frames")

        // Extract FCEUX state at each frame
        fun getFceuxState(frame: Int): FrameState? {
            val offset = frame * 2048
            if (offset + 0x800 > fceuxRam.size) return null

            fun getByte(addr: Int) = fceuxRam[offset + addr].toInt() and 0xFF

            return FrameState(
                frame = frame,
                fc = getByte(SMB.FrameCounter),
                world = getByte(SMB.WorldNumber) + 1,
                level = getByte(SMB.LevelNumber) + 1,
                operMode = getByte(SMB.OperMode),
                page = getByte(SMB.ScreenLeft_PageLoc),
                playerX = getByte(SMB.Player_X_Position),
                lives = getByte(SMB.NumberofLives),
                state = getByte(SMB.Player_State)
            )
        }

        // Find all level transitions in FCEUX
        println("\n=== FCEUX Level Transitions ===")
        var lastWorld = -1
        var lastLevel = -1
        var lastMode = -1

        data class Transition(val frame: Int, val fromLevel: String, val toLevel: String, val fc: Int)
        val fceuxTransitions = mutableListOf<Transition>()

        for (frame in 0 until maxFrame) {
            val state = getFceuxState(frame) ?: continue

            // Detect OperMode transitions (0=Title, 1=Game)
            if (state.operMode != lastMode) {
                println("Frame $frame: OperMode ${lastMode} -> ${state.operMode}")
                lastMode = state.operMode

                if (state.operMode == 1) {
                    fceuxTransitions.add(Transition(frame, "start", "${state.world}-${state.level}", state.fc))
                }
            }

            // Detect level transitions during gameplay
            if (state.operMode == 1 && (state.world != lastWorld || state.level != lastLevel)) {
                if (lastWorld > 0) {
                    println("Frame $frame: Level ${lastWorld}-${lastLevel} -> ${state.world}-${state.level} (FC=${state.fc})")
                    fceuxTransitions.add(Transition(frame, "${lastWorld}-${lastLevel}", "${state.world}-${state.level}", state.fc))
                }
                lastWorld = state.world
                lastLevel = state.level
            }
        }

        println("\n=== FCEUX Transition Summary ===")
        fceuxTransitions.forEach { t ->
            println("Frame ${t.frame}: ${t.fromLevel} -> ${t.toLevel} (FC=${t.fc})")
        }

        // Now analyze the relationship between FCEUX frames and game progress
        // Look for key gameplay milestones and their exact frames
        println("\n=== Key Milestone Frames ===")

        // Find game start (when OperMode becomes 1)
        val gameStartFrame = (0 until maxFrame)
            .firstOrNull { getFceuxState(it)?.operMode == 1 }
        println("Game start (OperMode=1): Frame $gameStartFrame")

        // Find when player first moves right (page increases from 0)
        val firstMoveFrame = (0 until maxFrame)
            .firstOrNull {
                val s = getFceuxState(it) ?: return@firstOrNull false
                s.operMode == 1 && s.page > 0
            }
        println("First scroll (Page > 0): Frame $firstMoveFrame")

        // Track page progression to find approximate level end
        println("\n=== Page Progression ===")
        var lastPage = -1
        for (frame in 0 until minOf(maxFrame, 6000) step 100) {
            val state = getFceuxState(frame) ?: continue
            if (state.operMode == 1 && state.page != lastPage) {
                println("Frame $frame: Page ${state.page}, World ${state.world}-${state.level}, FC=${state.fc}")
                lastPage = state.page
            }
        }

        // Find death events (lives decreasing)
        println("\n=== Death Events ===")
        var lastLives = -1
        for (frame in 0 until maxFrame) {
            val state = getFceuxState(frame) ?: continue
            if (state.lives != lastLives && lastLives >= 0) {
                if (state.lives < lastLives) {
                    println("Frame $frame: Death! Lives ${lastLives} -> ${state.lives} (World ${state.world}-${state.level})")
                }
                lastLives = state.lives
            }
            if (lastLives < 0) lastLives = state.lives
        }

        // Generate the offset map based on transitions
        println("\n=== PROPOSED OFFSET MAP ===")
        println("Based on FCEUX transitions, here's the per-level offset adjustment:")
        println("(These offsets are relative to the base bootNmiOffset of 11)")
        println()

        // Load interpreter transitions if available
        val interpTransitions = mutableMapOf<String, Int>()
        val interpRamFile = File("local/tas/interpreter-full-ram.bin")
        if (interpRamFile.exists()) {
            val interpRam = interpRamFile.readBytes()
            var lastLevel = ""
            for (frame in 0 until interpRam.size / 2048) {
                val offset = frame * 2048
                val mode = interpRam[offset + SMB.OperMode].toInt() and 0xFF
                val world = (interpRam[offset + SMB.WorldNumber].toInt() and 0xFF) + 1
                val level = (interpRam[offset + SMB.LevelNumber].toInt() and 0xFF) + 1
                val key = "$world-$level"
                if (mode == 1 && key != lastLevel && !interpTransitions.containsKey(key)) {
                    interpTransitions[key] = frame
                    println("Interpreter transition to $key at frame $frame")
                    lastLevel = key
                }
            }
        }

        // Calculate correct offsets
        // Formula: levelOffset = interp_transition_frame - bootNmiOffset - fceux_transition_frame
        // This ensures inputIndex = nmiInputIndex - totalOffset = fceux_frame
        val bootNmiOffset = 11
        val offsetMap = mutableMapOf<String, Int>()

        fceuxTransitions.forEach { transition ->
            val fceuxFrame = transition.frame
            val interpFrame = interpTransitions[transition.toLevel] ?: fceuxFrame

            // levelOffset such that: inputIndex = nmiInputIndex - (bootNmiOffset + levelOffset) = fceuxFrame
            // When at interp frame: interpFrame - bootNmiOffset - levelOffset = fceuxFrame
            // levelOffset = interpFrame - bootNmiOffset - fceuxFrame
            val levelOffset = interpFrame - bootNmiOffset - fceuxFrame

            offsetMap[transition.toLevel] = levelOffset
            println("Level ${transition.toLevel}: FCEUX frame $fceuxFrame, Interp frame $interpFrame, levelOffset = $levelOffset")
        }

        // Write the offset map to a file for use in the TAS test
        val offsetMapFile = File("local/tas/level-offset-map.txt")
        offsetMapFile.printWriter().use { out ->
            out.println("# Per-level input offset map")
            out.println("# Generated from FCEUX/interpreter transition comparison")
            out.println("# Format: LEVEL FCEUX_FRAME LEVEL_OFFSET")
            out.println("# Calculation: inputIndex = nmiInputIndex - (bootNmiOffset + levelOffset)")
            out.println("# where bootNmiOffset = 11")
            offsetMap.entries.forEach { (level, offset) ->
                val fceuxFrame = fceuxTransitions.find { it.toLevel == level }?.frame ?: 0
                out.println("$level $fceuxFrame $offset")
            }
        }
        println("\nWrote offset map to local/tas/level-offset-map.txt")
    }

    @Test
    fun `compare interpreter and FCEUX FrameCounter at transitions`() {
        val interpRamFile = File("local/tas/interpreter-full-ram.bin")
        val fceuxRamFile = File("local/tas/fceux-full-ram.bin")

        if (!interpRamFile.exists() || !fceuxRamFile.exists()) {
            println("RAM dumps not found - run FullTASValidationTest first")
            return
        }

        val interpRam = interpRamFile.readBytes()
        val fceuxRam = fceuxRamFile.readBytes()
        val maxFrame = minOf(interpRam.size, fceuxRam.size) / 2048

        println("=== FrameCounter Drift Analysis ===")
        println("Comparing interpreter and FCEUX FrameCounters")
        println("Max frames: $maxFrame")

        fun getByte(ram: ByteArray, frame: Int, addr: Int): Int {
            val offset = frame * 2048
            if (offset + addr >= ram.size) return 0
            return ram[offset + addr].toInt() and 0xFF
        }

        // Track FC drift over time
        var lastDrift = 0
        var lastLevel = ""

        data class DriftPoint(val frame: Int, val iFC: Int, val fFC: Int, val drift: Int, val level: String)
        val driftPoints = mutableListOf<DriftPoint>()

        for (frame in 0 until maxFrame step 10) {
            val iFC = getByte(interpRam, frame, SMB.FrameCounter)
            val fFC = getByte(fceuxRam, frame, SMB.FrameCounter)
            val iWorld = getByte(interpRam, frame, SMB.WorldNumber) + 1
            val iLevel = getByte(interpRam, frame, SMB.LevelNumber) + 1
            val fWorld = getByte(fceuxRam, frame, SMB.WorldNumber) + 1
            val fLevel = getByte(fceuxRam, frame, SMB.LevelNumber) + 1
            val iMode = getByte(interpRam, frame, SMB.OperMode)
            val fMode = getByte(fceuxRam, frame, SMB.OperMode)

            // Calculate drift (accounting for wraparound)
            val rawDrift = iFC - fFC
            val drift = when {
                rawDrift > 20 -> rawDrift - 40  // Wrapped backwards
                rawDrift < -20 -> rawDrift + 40  // Wrapped forwards
                else -> rawDrift
            }

            val levelKey = "$iWorld-$iLevel (F:$fWorld-$fLevel)"

            // Report when drift changes or level changes
            if (drift != lastDrift || levelKey != lastLevel) {
                println("Frame $frame: drift=$drift (I:FC=$iFC F:FC=$fFC) Level=$levelKey Mode I:$iMode F:$fMode")
                driftPoints.add(DriftPoint(frame, iFC, fFC, drift, levelKey))
                lastDrift = drift
                lastLevel = levelKey
            }
        }

        // Summary
        println("\n=== Drift Summary ===")
        println("Drift changes at these points:")
        driftPoints.forEach { p ->
            println("  Frame ${p.frame}: drift=${p.drift} (${p.level})")
        }

        // Calculate average drift per level segment
        println("\n=== Average Drift by Level ===")
        var segmentStart = 0
        var segmentDrift = 0
        var segmentLevel = driftPoints.firstOrNull()?.level ?: ""

        for (i in 1 until driftPoints.size) {
            val p = driftPoints[i]
            if (p.level != segmentLevel) {
                val avgDrift = segmentDrift / maxOf(1, i - segmentStart)
                println("  $segmentLevel: avg drift ≈ $avgDrift (frames ${driftPoints[segmentStart].frame}-${driftPoints[i-1].frame})")
                segmentStart = i
                segmentLevel = p.level
            }
            segmentDrift += p.drift
        }
    }

    @Test
    fun `find exact sync points between interpreter and FCEUX`() {
        val interpRamFile = File("local/tas/interpreter-full-ram.bin")
        val fceuxRamFile = File("local/tas/fceux-full-ram.bin")

        if (!interpRamFile.exists() || !fceuxRamFile.exists()) {
            println("RAM dumps not found")
            return
        }

        val interpRam = interpRamFile.readBytes()
        val fceuxRam = fceuxRamFile.readBytes()

        val outputFile = File("local/tas/sync-point-analysis.txt").printWriter()
        fun log(msg: String) {
            println(msg)
            outputFile.println(msg)
            outputFile.flush()
        }

        log("=== Finding Sync Points ===")
        log("Looking for frames where interpreter state matches FCEUX state")

        fun getByte(ram: ByteArray, frame: Int, addr: Int): Int {
            val offset = frame * 2048
            if (offset + addr >= ram.size) return -1
            return ram[offset + addr].toInt() and 0xFF
        }

        // For each interpreter frame, find the closest FCEUX frame with matching state
        data class SyncPoint(val iFrame: Int, val fFrame: Int, val offset: Int, val matchQuality: Int)
        val syncPoints = mutableListOf<SyncPoint>()

        val maxFrame = minOf(interpRam.size, fceuxRam.size) / 2048

        // Sample at key points
        for (iFrame in listOf(100, 200, 300, 500, 750, 1000, 1250, 1500, 1750, 2000, 2500, 3000)) {
            if (iFrame >= maxFrame) continue

            val iMode = getByte(interpRam, iFrame, SMB.OperMode)
            val iWorld = getByte(interpRam, iFrame, SMB.WorldNumber)
            val iLevel = getByte(interpRam, iFrame, SMB.LevelNumber)
            val iPage = getByte(interpRam, iFrame, SMB.ScreenLeft_PageLoc)
            val iX = getByte(interpRam, iFrame, SMB.Player_X_Position)

            if (iMode != 1) continue  // Only during gameplay

            // Search FCEUX frames around iFrame for best match
            var bestMatch = -1
            var bestQuality = 0
            var bestOffset = 0

            for (offset in -100..100) {
                val fFrame = iFrame + offset
                if (fFrame < 0 || fFrame >= maxFrame) continue

                val fMode = getByte(fceuxRam, fFrame, SMB.OperMode)
                val fWorld = getByte(fceuxRam, fFrame, SMB.WorldNumber)
                val fLevel = getByte(fceuxRam, fFrame, SMB.LevelNumber)
                val fPage = getByte(fceuxRam, fFrame, SMB.ScreenLeft_PageLoc)
                val fX = getByte(fceuxRam, fFrame, SMB.Player_X_Position)

                if (fMode != 1) continue

                // Calculate match quality
                var quality = 0
                if (fWorld == iWorld) quality += 100
                if (fLevel == iLevel) quality += 50
                if (fPage == iPage) quality += 20
                if (kotlin.math.abs(fX - iX) < 16) quality += 10
                if (kotlin.math.abs(fX - iX) < 4) quality += 5

                if (quality > bestQuality) {
                    bestQuality = quality
                    bestMatch = fFrame
                    bestOffset = offset
                }
            }

            if (bestMatch >= 0 && bestQuality >= 150) {
                syncPoints.add(SyncPoint(iFrame, bestMatch, bestOffset, bestQuality))
                println("Interpreter frame $iFrame matches FCEUX frame $bestMatch (offset=$bestOffset, quality=$bestQuality)")
            }
        }

        // Analyze the pattern
        println("\n=== Offset Pattern ===")
        if (syncPoints.size >= 2) {
            val avgOffset = syncPoints.map { it.offset }.average()
            println("Average offset: $avgOffset")
            println("Offset progression:")
            syncPoints.forEach { sp ->
                println("  iFrame ${sp.iFrame}: offset=${sp.offset}")
            }

            // Try to fit a linear model: offset = baseOffset + driftRate * frame
            if (syncPoints.size >= 3) {
                val first = syncPoints.first()
                val last = syncPoints.last()
                val driftRate = (last.offset - first.offset).toDouble() / (last.iFrame - first.iFrame)
                val baseOffset = first.offset - driftRate * first.iFrame

                println("\nLinear model: offset ≈ ${baseOffset.toInt()} + ${(driftRate * 1000).toInt()}/1000 * frame")
                println("At frame 1000: offset ≈ ${(baseOffset + driftRate * 1000).toInt()}")
                println("At frame 2000: offset ≈ ${(baseOffset + driftRate * 2000).toInt()}")
                println("At frame 3000: offset ≈ ${(baseOffset + driftRate * 3000).toInt()}")
            }
        }
    }
}
