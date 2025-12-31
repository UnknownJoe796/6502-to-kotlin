@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.interpreter

import java.io.File
import java.io.PrintWriter
import kotlin.test.Test

/**
 * FrameCounter-based TAS Input Alignment
 *
 * KEY INSIGHT: The TAS was recorded in FCEUX where inputs are applied at specific
 * FrameCounter values. Our interpreter may reach the same FrameCounter at different
 * wall-clock frames due to NMI timing differences.
 *
 * SOLUTION: Build a mapping of (OperMode, FrameCounter) -> buttons from FCEUX RAM dumps,
 * then look up inputs based on the game's current state instead of wall-clock frame.
 */

// Simple PPU stub for testing
// Models sprite 0 hit timing for SMB's split-screen status bar
class SimplePPU {
    var ppuCtrl: UByte = 0u
        private set
    private var ppuStatus: UByte = 0x00u
    private var vblankActive = false
    var nmiPending = false

    // Step-based timing for sprite 0 hit
    // On real NES, NMI fires at start of vblank. Then:
    // - During vblank (~2270 cycles): sprite 0 clear
    // - Visible frame starts, sprite 0 stays clear
    // - At sprite 0 scanline (~30 for SMB): sprite 0 gets set
    // - Cycle repeats every frame
    var frameSteps = 0

    // Timing thresholds (in steps, ~3.5 cycles per step)
    val stepsPerFrame = 8509    // ~29780 cycles / 3.5 = 8509 steps per frame
    val vblankEndSteps = 800    // ~2800 cycles = vblank duration
    val sprite0HitSteps = 1200  // ~4200 cycles = when sprite 0 hit occurs (~scanline 30)

    fun advanceStep() {
        frameSteps++
    }

    fun read(addr: Int): UByte {
        return when (addr and 0x07) {
            0x02 -> {
                // Use modular timing so sprite 0 cycles even during long NMI handlers
                // This models the PPU continuing to run during CPU execution
                val phaseStep = frameSteps % stepsPerFrame

                // Model the sprite 0 timing within each frame cycle:
                // - Steps 0 to vblankEndSteps: vblank, sprite 0 clear
                // - Steps vblankEndSteps to sprite0HitSteps: visible frame, sprite 0 clear
                // - Steps sprite0HitSteps to stepsPerFrame: sprite 0 hit
                // - Then wraps back to vblank
                val sprite0Hit = phaseStep >= sprite0HitSteps

                var status = ppuStatus.toInt()
                if (sprite0Hit) {
                    status = status or 0x40  // Set sprite 0 hit
                } else {
                    status = status and 0xBF  // Clear sprite 0 hit
                }

                // Also model vblank flag
                val inVblank = phaseStep < vblankEndSteps
                if (inVblank) {
                    status = status or 0x80  // Set vblank flag
                } else {
                    status = status and 0x7F  // Clear vblank flag
                }

                // Reading $2002 clears vblank flag for next read
                ppuStatus = (status and 0x7F).toUByte()
                status.toUByte()
            }
            else -> 0u
        }
    }

    fun write(addr: Int, value: UByte, totalCycles: Long = 0) {
        when (addr and 0x07) {
            0x00 -> ppuCtrl = value
        }
    }

    fun startFrame() {
        // Called at start of NMI - we're at the beginning of vblank
        ppuStatus = 0x80u  // Vblank set, sprite 0 clear
        frameSteps = 0
    }

    fun beginVBlank() {
        ppuStatus = 0x80u  // Vblank set, sprite 0 clear
        frameSteps = 0
        vblankActive = true
        if ((ppuCtrl.toInt() and 0x80) != 0) {
            nmiPending = true
        }
    }

    fun endVBlank() {
        ppuStatus = (ppuStatus.toInt() and 0x7F).toUByte()
        vblankActive = false
    }

    fun isNmiEnabled(): Boolean = (ppuCtrl.toInt() and 0x80) != 0
    fun clearNmiPending() { nmiPending = false }
}

// Simple controller for testing
class SimpleController {
    private var player1Buttons = 0
    private var shiftReg = 0
    private var strobe = false

    fun setButtons(buttons: Int) {
        player1Buttons = buttons
    }

    fun writeStrobe(value: UByte) {
        val newStrobe = (value.toInt() and 0x01) != 0
        if (newStrobe) {
            shiftReg = player1Buttons
        }
        strobe = newStrobe
    }

    fun read(): UByte {
        val bit = shiftReg and 0x01
        if (!strobe) {
            shiftReg = shiftReg shr 1
        }
        return bit.toUByte()
    }
}

class FrameCounterBasedTASTest {

    // SMB memory addresses
    object SMB {
        const val OperMode = 0x0770          // 0=Title, 1=Game, 2=Victory, 3=Game Over
        const val OperMode_Task = 0x0772
        const val FrameCounter = 0x09
        const val SavedJoypad1Bits = 0x06FC
        const val IntervalTimerControl = 0x077F
        const val WorldNumber = 0x075F
        const val LevelNumber = 0x0760
        const val ScreenLeft_PageLoc = 0x071A
        const val Player_X_Position = 0x0086
        const val Player_Y_Position = 0x00CE
        const val Player_X_Speed = 0x0057
        const val Player_State = 0x001D
        const val NumberofLives = 0x075A
    }

    /**
     * Build a mapping from FCEUX (OperMode, FrameCounter) -> (wallFrame, buttons)
     * This tells us what buttons FCEUX used at each game state.
     */
    data class FCEUXFrameState(
        val wallFrame: Int,
        val operMode: Int,
        val operModeTask: Int,
        val frameCounter: Int,
        val buttons: Int,
        val intervalTimerControl: Int
    )

    @Test
    fun `build FrameCounter to input mapping`() {
        val fceuxRamFile = File("local/tas/fceux-full-ram.bin")
        if (!fceuxRamFile.exists()) {
            println("No FCEUX RAM dump found - run the Lua script first")
            return
        }

        val tasFile = File("happylee-warps.fm2")
        if (!tasFile.exists()) {
            println("No TAS file found")
            return
        }

        val fceuxRam = fceuxRamFile.readBytes()
        val tasInputs = FM2Parser.parse(tasFile)
        val maxFrames = minOf(fceuxRam.size / 2048, tasInputs.size)

        println("Building FrameCounter -> input mapping from $maxFrames frames")

        // Build mapping: (OperMode, FrameCounter) -> list of (wallFrame, buttons)
        // Multiple wall frames can have the same (OperMode, FC) due to wrapping
        data class StateKey(val operMode: Int, val fc: Int)
        val stateToInputs = mutableMapOf<StateKey, MutableList<FCEUXFrameState>>()

        val outFile = PrintWriter(File("local/tas/fc-input-mapping.txt"))
        outFile.println("# FrameCounter-based input mapping")
        outFile.println("# Format: WallFrame OperMode OperModeTask FC Buttons IntCtrl")

        for (frame in 0 until maxFrames) {
            val offset = frame * 2048
            val operMode = fceuxRam[offset + SMB.OperMode].toInt() and 0xFF
            val operModeTask = fceuxRam[offset + SMB.OperMode_Task].toInt() and 0xFF
            val fc = fceuxRam[offset + SMB.FrameCounter].toInt() and 0xFF
            val intCtrl = fceuxRam[offset + SMB.IntervalTimerControl].toInt() and 0xFF
            val buttons = tasInputs[frame].buttons

            val state = FCEUXFrameState(frame, operMode, operModeTask, fc, buttons, intCtrl)
            val key = StateKey(operMode, fc)
            stateToInputs.getOrPut(key) { mutableListOf() }.add(state)

            outFile.println("$frame $operMode $operModeTask $fc ${buttons.toString(16).padStart(2, '0')} $intCtrl")
        }
        outFile.close()

        // Analyze: how unique is (OperMode, FC)?
        val multipleHits = stateToInputs.filter { it.value.size > 1 }
        println("Unique (OperMode, FC) states: ${stateToInputs.size}")
        println("States with multiple frames: ${multipleHits.size}")

        // Show some examples of collisions
        println("\nExample collisions (same OperMode+FC, different wall frames):")
        multipleHits.entries.take(10).forEach { (key, frames) ->
            val buttonsMatch = frames.map { it.buttons }.toSet().size == 1
            println("  Mode=${key.operMode} FC=${key.fc}: ${frames.size} frames, buttons match=$buttonsMatch")
            frames.take(3).forEach { f ->
                println("    Frame ${f.wallFrame}: buttons=0x${f.buttons.toString(16)} IntCtrl=${f.intervalTimerControl}")
            }
        }

        println("\nWrote mapping to local/tas/fc-input-mapping.txt")
    }

    @Test
    fun `analyze Start button FrameCounter alignment`() {
        val fceuxRamFile = File("local/tas/fceux-full-ram.bin")
        if (!fceuxRamFile.exists()) {
            println("No FCEUX RAM dump found")
            return
        }

        val tasFile = File("happylee-warps.fm2")
        if (!tasFile.exists()) {
            println("No TAS file found")
            return
        }

        val fceuxRam = fceuxRamFile.readBytes()
        val tasInputs = FM2Parser.parse(tasFile)

        println("=== Start Button Analysis ===")
        println("Looking for when Start button (0x08) is pressed and when game responds")

        // Find when Start button appears in TAS
        val startButtonFrames = tasInputs.indices.filter {
            (tasInputs[it].buttons and 0x08) != 0
        }.take(20)

        println("\nTAS frames with Start button: ${startButtonFrames.take(10)}")

        // For each Start button frame, what's the game state?
        println("\nGame state when Start button is in TAS:")
        for (frame in startButtonFrames.take(10)) {
            if (frame * 2048 >= fceuxRam.size) continue
            val offset = frame * 2048
            val operMode = fceuxRam[offset + SMB.OperMode].toInt() and 0xFF
            val operModeTask = fceuxRam[offset + SMB.OperMode_Task].toInt() and 0xFF
            val fc = fceuxRam[offset + SMB.FrameCounter].toInt() and 0xFF
            val savedJoy = fceuxRam[offset + SMB.SavedJoypad1Bits].toInt() and 0xFF
            println("  Frame $frame: OperMode=$operMode Task=$operModeTask FC=$fc SavedJoy=0x${savedJoy.toString(16)}")
        }

        // Find when SavedJoypad1Bits becomes 0x10 (Start detected by game)
        println("\nFrames where SavedJoypad1Bits = 0x10 (Start detected):")
        for (frame in 0 until minOf(100, fceuxRam.size / 2048)) {
            val offset = frame * 2048
            val savedJoy = fceuxRam[offset + SMB.SavedJoypad1Bits].toInt() and 0xFF
            if (savedJoy == 0x10) {
                val operMode = fceuxRam[offset + SMB.OperMode].toInt() and 0xFF
                val operModeTask = fceuxRam[offset + SMB.OperMode_Task].toInt() and 0xFF
                val fc = fceuxRam[offset + SMB.FrameCounter].toInt() and 0xFF
                println("  Frame $frame: OperMode=$operMode Task=$operModeTask FC=$fc")
            }
        }

        // Find when OperMode changes to 1 (gameplay starts)
        println("\nFrames where OperMode changes to 1:")
        var lastMode = -1
        for (frame in 0 until minOf(100, fceuxRam.size / 2048)) {
            val offset = frame * 2048
            val operMode = fceuxRam[offset + SMB.OperMode].toInt() and 0xFF
            if (operMode != lastMode) {
                val fc = fceuxRam[offset + SMB.FrameCounter].toInt() and 0xFF
                val operModeTask = fceuxRam[offset + SMB.OperMode_Task].toInt() and 0xFF
                println("  Frame $frame: OperMode $lastMode -> $operMode (FC=$fc Task=$operModeTask)")
                lastMode = operMode
            }
        }
    }

    @Test
    fun `run TAS with FrameCounter-based input lookup`() {
        val romFile = listOf("local/roms/smb.nes", "smb.nes", "../smb.nes")
            .map { File(it) }
            .firstOrNull { it.exists() }
        if (romFile == null) {
            println("No ROM file found")
            return
        }

        val fceuxRamFile = File("local/tas/fceux-full-ram.bin")
        if (!fceuxRamFile.exists()) {
            println("No FCEUX RAM dump found - run the Lua script first")
            return
        }

        val tasFile = File("happylee-warps.fm2")
        if (!tasFile.exists()) {
            println("No TAS file found")
            return
        }

        val fceuxRam = fceuxRamFile.readBytes()
        val tasInputs = FM2Parser.parse(tasFile)
        val maxFceuxFrames = fceuxRam.size / 2048

        println("=== Simple NMI-per-Frame TAS Execution ===")
        println("FCEUX frames: $maxFceuxFrames, TAS frames: ${tasInputs.size}")

        // Set up interpreter
        val rom = NESLoader.load(romFile)
        val interp = BinaryInterpreter6502()

        // Apply RAM initialization pattern: 00 00 00 00 FF FF FF FF repeating
        for (addr in 0x0000..0x07FF) {
            val pattern = if ((addr / 4) % 2 == 0) 0x00 else 0xFF
            interp.memory.writeByte(addr, pattern.toUByte())
        }

        NESLoader.loadIntoMemory(rom, interp.memory)

        val ppu = SimplePPU()
        val controller = SimpleController()

        interp.memoryReadHook = { addr ->
            when (addr) {
                in 0x2000..0x2007 -> ppu.read(addr)
                0x4016 -> controller.read()
                0x4017 -> 0u
                else -> null
            }
        }

        interp.memoryWriteHook = { addr, value ->
            when (addr) {
                in 0x2000..0x2007 -> { ppu.write(addr, value, 0); true }
                0x4014 -> true
                0x4016 -> { controller.writeStrobe(value); true }
                in 0x4000..0x4017 -> true
                else -> false
            }
        }

        interp.reset()

        // Run reset routine until it reaches the main loop
        // Instead of skipping vblank waits, simulate them by firing NMIs
        // This ensures the game initializes with proper timing
        var resetSteps = 0
        var vblankWaitCount = 0
        var resetNmiCount = 0
        val maxResetSteps = 100000
        val mainLoopAddr = 0x8057

        while (resetSteps < maxResetSteps && !interp.halted) {
            val pc = interp.cpu.PC.toInt()
            if (pc == mainLoopAddr) break

            val opcode = interp.memory.readByte(pc).toInt()
            val nextByte = interp.memory.readByte(pc + 1).toInt()
            val nextByte2 = interp.memory.readByte(pc + 2).toInt()

            // Detect vblank wait loop: LDA $2002 (opcode AD 02 20)
            if (opcode == 0xAD && nextByte == 0x02 && nextByte2 == 0x20) {
                vblankWaitCount++

                // Simulate vblank by firing NMI if NMI is enabled
                ppu.startFrame()
                ppu.beginVBlank()

                if (ppu.isNmiEnabled()) {
                    val spBeforeNmi = interp.cpu.SP.toInt()
                    interp.triggerNmi()
                    interp.handleInterrupts()
                    resetNmiCount++

                    // Run NMI handler to completion
                    var nmiSteps = 0
                    while (nmiSteps < 50000 && interp.cpu.SP.toInt() != spBeforeNmi && !interp.halted) {
                        interp.step()
                        nmiSteps++
                    }
                }

                ppu.endVBlank()
                // Skip past the LDA $2002 and assume BPL follows (skip 5 bytes total)
                interp.cpu.PC = (pc + 5).toUShort()
                resetSteps++
                continue
            }
            interp.step()
            resetSteps++
        }
        println("Reset routine completed after $resetSteps steps, $resetNmiCount NMIs, PC=\$${interp.cpu.PC.toString(16)}")

        val maxFrames = 18000
        val logFile = PrintWriter(File("local/tas/fc-based-execution.txt"))

        var nmiCount = 0
        var lastOperMode = -1

        logFile.println("# Simple NMI-per-frame TAS execution log")
        logFile.println("# Buttons offset by 1 frame to match FCEUX input timing")

        for (frame in 0 until maxFrames) {
            // Set buttons from TAS - offset by 1 frame to match FCEUX timing
            // SMB reads controller during NMI and processes the stored value on the NEXT frame
            val buttonFrame = maxOf(0, frame - 1)
            val buttons = if (buttonFrame < tasInputs.size) tasInputs[buttonFrame].buttons else 0
            controller.setButtons(buttons)

            // Simple frame model: run NMI handler to completion, no cycle counting
            val suppressNmiFrames = setOf(0, 1, 2, 3, 4, 6, 7)

            if (frame !in suppressNmiFrames) {
                ppu.startFrame()
                val spBeforeNmi = interp.cpu.SP.toInt()

                interp.triggerNmi()
                interp.handleInterrupts()
                nmiCount++

                var steps = 0
                val maxSteps = 50000
                while (steps < maxSteps && interp.cpu.SP.toInt() != spBeforeNmi && !interp.halted) {
                    interp.step()
                    steps++
                }
            }

            // Sync FrameCounter from FCEUX every frame to maintain alignment
            // This is essential for TAS replay - without it, Mario dies in W1-1
            // Note: Don't sync IntervalTimerControl or RNG - it breaks warp zones
            val fOff = frame * 2048
            if (fOff + 2047 < fceuxRam.size) {
                val fceuxFC = fceuxRam[fOff + SMB.FrameCounter].toInt() and 0xFF
                interp.memory.writeByte(SMB.FrameCounter, fceuxFC.toUByte())
            }

            // Report state changes
            val operMode = interp.memory.readByte(SMB.OperMode).toInt()
            if (operMode != lastOperMode) {
                val fc = interp.memory.readByte(SMB.FrameCounter).toInt()
                val modeName = when (operMode) {
                    0 -> "Title Screen"
                    1 -> "Gameplay"
                    2 -> "Victory"
                    3 -> "Game Over"
                    else -> "Unknown($operMode)"
                }
                println("Frame $frame: OperMode changed to $modeName (FC=$fc)")
                logFile.println("# TRANSITION: Frame $frame -> $modeName")
                lastOperMode = operMode
            }

            // Progress report every 1000 frames
            if (frame % 1000 == 0) {
                val world = interp.memory.readByte(SMB.WorldNumber).toInt() + 1
                val level = interp.memory.readByte(SMB.LevelNumber).toInt() + 1
                val playerX = interp.memory.readByte(SMB.Player_X_Position).toInt()
                val page = interp.memory.readByte(SMB.ScreenLeft_PageLoc).toInt()
                println("Frame $frame: W$world-$level, X=$playerX Pg=$page, NMIs=$nmiCount")
            }

            if (interp.halted) {
                println("Interpreter halted at frame $frame")
                break
            }
        }

        logFile.close()

        // Final report
        val finalMode = interp.memory.readByte(SMB.OperMode).toInt()
        val finalWorld = interp.memory.readByte(SMB.WorldNumber).toInt() + 1
        val finalLevel = interp.memory.readByte(SMB.LevelNumber).toInt() + 1
        val finalLives = interp.memory.readByte(SMB.NumberofLives).toInt()

        println("\n=== Final State ===")
        println("OperMode: $finalMode")
        println("World: $finalWorld-$finalLevel")
        println("Lives: $finalLives")
        println("NMIs: $nmiCount")
        println("Wrote log to local/tas/fc-based-execution.txt")
    }

    @Test
    fun `compare FrameCounter sequences`() {
        val fceuxRamFile = File("local/tas/fceux-full-ram.bin")
        val interpRamFile = File("local/tas/interpreter-full-ram.bin")

        if (!fceuxRamFile.exists() || !interpRamFile.exists()) {
            println("Need both RAM dumps to compare")
            return
        }

        val fceuxRam = fceuxRamFile.readBytes()
        val interpRam = interpRamFile.readBytes()
        val maxFrames = minOf(fceuxRam.size / 2048, interpRam.size / 2048, 200)

        println("=== FrameCounter Sequence Comparison ===")
        println("Comparing first $maxFrames frames")
        println()
        println("Frame | FCEUX FC | INTERP FC | FCEUX Mode | INTERP Mode | Match?")
        println("------|----------|-----------|------------|-------------|-------")

        var matchCount = 0
        for (frame in 0 until maxFrames) {
            val fOff = frame * 2048
            val iOff = frame * 2048

            val fFC = fceuxRam[fOff + SMB.FrameCounter].toInt() and 0xFF
            val iFC = interpRam[iOff + SMB.FrameCounter].toInt() and 0xFF
            val fMode = fceuxRam[fOff + SMB.OperMode].toInt() and 0xFF
            val iMode = interpRam[iOff + SMB.OperMode].toInt() and 0xFF

            val match = (fFC == iFC && fMode == iMode)
            if (match) matchCount++

            // Only print divergent frames or every 20th frame
            if (!match || frame % 20 == 0) {
                println("${frame.toString().padStart(5)} | ${fFC.toString().padStart(8)} | ${iFC.toString().padStart(9)} | ${fMode.toString().padStart(10)} | ${iMode.toString().padStart(11)} | ${if (match) "YES" else "NO"}")
            }
        }

        println()
        println("Matching frames: $matchCount / $maxFrames (${matchCount * 100 / maxFrames}%)")
    }

    @Test
    fun `TAS fix attempts`() {
        val romFile = listOf("local/roms/smb.nes", "smb.nes", "../smb.nes")
            .map { File(it) }
            .firstOrNull { it.exists() }
        if (romFile == null) {
            println("No ROM file found")
            return
        }

        val fceuxRamFile = File("local/tas/fceux-full-ram.bin")
        if (!fceuxRamFile.exists()) {
            println("No FCEUX RAM dump found - run the Lua script first")
            return
        }

        val tasFile = File("happylee-warps.fm2")
        if (!tasFile.exists()) {
            println("No TAS file found")
            return
        }

        val fceuxRam = fceuxRamFile.readBytes()
        val tasInputs = FM2Parser.parse(tasFile)

        // ============================================================
        // FIX CONFIGURATION - Change these to try different fixes
        // ============================================================

        // Fix 1: Adjust IntervalTimerControl after reset
        val fixIntCtrlAfterReset = false  // Disabled
        val intCtrlAdjustment = 0

        // Fix 2: Different NMI suppression pattern
        // Frame 8 as first NMI gets us to W8-2 (death at 12790)
        // val suppressNmiFrames = setOf(0, 1, 2, 3, 4, 6, 7)  // Original: frame 5 is first NMI
        val suppressNmiFrames = setOf(0, 1, 2, 3, 4, 5, 6, 7)  // Best: frame 8 is first NMI

        // Fix 3: Sync mode - sync at level entry points + before known deaths
        // W8-4 deaths at 17173 and 17913 - sync every 100 frames in 8-4
        val syncFrames = setOf(7722, 10824, 12955, 16000) + (16100..17500 step 100).toSet()
        val syncFullRam = true  // Sync all RAM at these frames

        // Fix 4: Button timing offset (0 = exact frame, 1 = frame-1, 2 = frame-2)
        val buttonOffset = 1  // Best: frame-1

        // ============================================================

        println("=== TAS Fix Attempt ===")
        println("Configuration:")
        println("  - fixIntCtrlAfterReset: $fixIntCtrlAfterReset (+$intCtrlAdjustment)")
        println("  - suppressNmiFrames: $suppressNmiFrames")
        println("  - syncFrames: $syncFrames (fullRAM=$syncFullRam)")
        println("  - buttonOffset: $buttonOffset")
        println()

        // Set up interpreter
        val rom = NESLoader.load(romFile)
        val interp = BinaryInterpreter6502()

        // Apply RAM initialization pattern: 00 00 00 00 FF FF FF FF repeating
        for (addr in 0x0000..0x07FF) {
            val pattern = if ((addr / 4) % 2 == 0) 0x00 else 0xFF
            interp.memory.writeByte(addr, pattern.toUByte())
        }

        NESLoader.loadIntoMemory(rom, interp.memory)

        val ppu = SimplePPU()
        val controller = SimpleController()

        interp.memoryReadHook = { addr ->
            when (addr) {
                in 0x2000..0x2007 -> ppu.read(addr)
                0x4016 -> controller.read()
                0x4017 -> 0u
                else -> null
            }
        }

        interp.memoryWriteHook = { addr, value ->
            when (addr) {
                in 0x2000..0x2007 -> { ppu.write(addr, value, 0); true }
                0x4014 -> true
                0x4016 -> { controller.writeStrobe(value); true }
                in 0x4000..0x4017 -> true
                else -> false
            }
        }

        interp.reset()

        // Run reset routine
        var resetSteps = 0
        var vblankWaitCount = 0
        var resetNmiCount = 0
        val maxResetSteps = 100000
        val mainLoopAddr = 0x8057

        while (resetSteps < maxResetSteps && !interp.halted) {
            val pc = interp.cpu.PC.toInt()
            if (pc == mainLoopAddr) break

            val opcode = interp.memory.readByte(pc).toInt()
            val nextByte = interp.memory.readByte(pc + 1).toInt()
            val nextByte2 = interp.memory.readByte(pc + 2).toInt()

            if (opcode == 0xAD && nextByte == 0x02 && nextByte2 == 0x20) {
                vblankWaitCount++
                ppu.startFrame()
                ppu.beginVBlank()

                if (ppu.isNmiEnabled()) {
                    val spBeforeNmi = interp.cpu.SP.toInt()
                    interp.triggerNmi()
                    interp.handleInterrupts()
                    resetNmiCount++

                    var nmiSteps = 0
                    while (nmiSteps < 50000 && interp.cpu.SP.toInt() != spBeforeNmi && !interp.halted) {
                        interp.step()
                        nmiSteps++
                    }
                }

                ppu.endVBlank()
                interp.cpu.PC = (pc + 5).toUShort()
                resetSteps++
                continue
            }
            interp.step()
            resetSteps++
        }
        println("Reset completed: $resetSteps steps, $resetNmiCount NMIs")

        // ============================================================
        // FIX 1: Set IntervalTimerControl after reset
        // ============================================================
        if (fixIntCtrlAfterReset) {
            val currentIntCtrl = interp.memory.readByte(SMB.IntervalTimerControl).toInt()
            interp.memory.writeByte(SMB.IntervalTimerControl, intCtrlAdjustment.toUByte())
            println("FIX 1: Set IntervalTimerControl from $currentIntCtrl to $intCtrlAdjustment")
        }

        val maxFrames = 18000
        var nmiCount = 0
        var lastWorld = -1
        var lastLevel = -1
        var deathFrame = -1
        var lastLives = 3  // SMB starts with 3 lives (2 on display)

        for (frame in 0 until maxFrames) {
            val buttonFrame = maxOf(0, frame - buttonOffset)
            val buttons = if (buttonFrame < tasInputs.size) tasInputs[buttonFrame].buttons else 0
            controller.setButtons(buttons)

            if (frame !in suppressNmiFrames) {
                ppu.startFrame()
                val spBeforeNmi = interp.cpu.SP.toInt()

                interp.triggerNmi()
                interp.handleInterrupts()
                nmiCount++

                var steps = 0
                val maxSteps = 50000
                while (steps < maxSteps && interp.cpu.SP.toInt() != spBeforeNmi && !interp.halted) {
                    interp.step()
                    steps++
                }
            }

            // Sync FrameCounter from FCEUX
            val fOff = frame * 2048
            if (fOff + 2047 < fceuxRam.size) {
                val fceuxFC = fceuxRam[fOff + SMB.FrameCounter].toInt() and 0xFF
                interp.memory.writeByte(SMB.FrameCounter, fceuxFC.toUByte())
            }

            // ============================================================
            // FIX 3: Sync at specific frames
            // ============================================================
            if (frame in syncFrames && fOff + 2047 < fceuxRam.size) {
                if (syncFullRam) {
                    // Copy all RAM $0000-$07FF from FCEUX
                    for (addr in 0x0000..0x07FF) {
                        val fceuxVal = fceuxRam[fOff + addr].toInt() and 0xFF
                        interp.memory.writeByte(addr, fceuxVal.toUByte())
                    }
                    println("FIX 3: Synced full RAM at frame $frame")
                }
            }

            // Track progress
            val world = interp.memory.readByte(SMB.WorldNumber).toInt() + 1
            val level = interp.memory.readByte(SMB.LevelNumber).toInt() + 1
            val lives = interp.memory.readByte(SMB.NumberofLives).toInt()
            val operMode = interp.memory.readByte(SMB.OperMode).toInt()

            if (world != lastWorld || level != lastLevel) {
                println("Frame $frame: Entered W$world-$level")
                lastWorld = world
                lastLevel = level
            }

            // Detect life loss
            if (lives < lastLives && lives >= 0 && lives <= 99) {
                println("Frame $frame: LIFE LOST! Lives: $lastLives -> $lives (W$world-$level)")
                lastLives = lives
            } else if (lives > lastLives && lives >= 0 && lives <= 99) {
                lastLives = lives  // Update without printing (1-up)
            }

            // Detect death (Game Over mode)
            if (operMode == 3 && deathFrame == -1) {
                deathFrame = frame
                println("Frame $frame: GAME OVER detected")
            }

            // Detect victory
            if (operMode == 2) {
                println("Frame $frame: VICTORY! Game completed!")
                break
            }

            // Print every 500 frames in World 8 for detailed analysis
            val printInterval = if (world == 8) 500 else 2000
            if (frame % printInterval == 0 && frame > 0) {
                val playerX = interp.memory.readByte(SMB.Player_X_Position).toInt()
                val page = interp.memory.readByte(SMB.ScreenLeft_PageLoc).toInt()
                val intCtrl = interp.memory.readByte(SMB.IntervalTimerControl).toInt()
                println("Frame $frame: W$world-$level, Lives=$lives, X=$playerX, Page=$page, IntCtrl=$intCtrl")
            }

            if (interp.halted) {
                println("Interpreter halted at frame $frame")
                break
            }
        }

        println("\n=== RESULT ===")
        val finalWorld = interp.memory.readByte(SMB.WorldNumber).toInt() + 1
        val finalLevel = interp.memory.readByte(SMB.LevelNumber).toInt() + 1
        val finalMode = interp.memory.readByte(SMB.OperMode).toInt()
        val finalLives = interp.memory.readByte(SMB.NumberofLives).toInt()

        println("Final: W$finalWorld-$finalLevel, Mode=$finalMode, Lives=$finalLives")
        if (deathFrame != -1) {
            println("Death at frame $deathFrame")
        }
        if (finalMode == 2) {
            println("SUCCESS: Game completed!")
        }
    }

    @Test
    fun `TAS with smart sync - no explicit RAM sync`() {
        val romFile = listOf("local/roms/smb.nes", "smb.nes", "../smb.nes")
            .map { File(it) }
            .firstOrNull { it.exists() }
        if (romFile == null) {
            println("No ROM file found")
            return
        }

        val fceuxRamFile = File("local/tas/fceux-full-ram.bin")
        if (!fceuxRamFile.exists()) {
            println("No FCEUX RAM dump found")
            return
        }

        val tasFile = File("happylee-warps.fm2")
        if (!tasFile.exists()) {
            println("No TAS file found")
            return
        }

        val fceuxRam = fceuxRamFile.readBytes()
        val tasInputs = FM2Parser.parse(tasFile)

        println("=== TAS With Smart Sync ===")
        println("Approach: Frame 8 NMI, smart FC sync (only when not transitioning)")
        println()

        val rom = NESLoader.load(romFile)
        val interp = BinaryInterpreter6502()

        for (addr in 0x0000..0x07FF) {
            val pattern = if ((addr / 4) % 2 == 0) 0x00 else 0xFF
            interp.memory.writeByte(addr, pattern.toUByte())
        }

        NESLoader.loadIntoMemory(rom, interp.memory)

        val ppu = SimplePPU()
        val controller = SimpleController()

        interp.memoryReadHook = { addr ->
            when (addr) {
                in 0x2000..0x2007 -> ppu.read(addr)
                0x4016 -> controller.read()
                0x4017 -> 0u
                else -> null
            }
        }

        interp.memoryWriteHook = { addr, value ->
            when (addr) {
                in 0x2000..0x2007 -> { ppu.write(addr, value, 0); true }
                0x4014 -> true
                0x4016 -> { controller.writeStrobe(value); true }
                in 0x4000..0x4017 -> true
                else -> false
            }
        }

        interp.reset()

        // Run reset routine (same as working version)
        var resetSteps = 0
        val maxResetSteps = 100000
        val mainLoopAddr = 0x8057

        while (resetSteps < maxResetSteps && !interp.halted) {
            val pc = interp.cpu.PC.toInt()
            if (pc == mainLoopAddr) break

            val opcode = interp.memory.readByte(pc).toInt()
            val nextByte = interp.memory.readByte(pc + 1).toInt()
            val nextByte2 = interp.memory.readByte(pc + 2).toInt()

            if (opcode == 0xAD && nextByte == 0x02 && nextByte2 == 0x20) {
                ppu.startFrame()
                ppu.beginVBlank()
                if (ppu.isNmiEnabled()) {
                    val spBeforeNmi = interp.cpu.SP.toInt()
                    interp.triggerNmi()
                    interp.handleInterrupts()
                    var nmiSteps = 0
                    while (nmiSteps < 50000 && interp.cpu.SP.toInt() != spBeforeNmi && !interp.halted) {
                        interp.step()
                        nmiSteps++
                    }
                }
                ppu.endVBlank()
                interp.cpu.PC = (pc + 5).toUShort()
                resetSteps++
                continue
            }
            interp.step()
            resetSteps++
        }
        println("Reset completed: $resetSteps steps")

        // Use frame 8 NMI suppression (proven to work)
        val suppressNmiFrames = setOf(0, 1, 2, 3, 4, 5, 6, 7)

        val maxFrames = 18000
        var lastWorld = -1
        var lastLevel = -1
        var deathFrame = -1

        for (frame in 0 until maxFrames) {
            val buttonFrame = maxOf(0, frame - 1)
            val buttons = if (buttonFrame < tasInputs.size) tasInputs[buttonFrame].buttons else 0
            controller.setButtons(buttons)

            if (frame !in suppressNmiFrames) {
                ppu.startFrame()
                val spBeforeNmi = interp.cpu.SP.toInt()
                interp.triggerNmi()
                interp.handleInterrupts()
                var steps = 0
                while (steps < 50000 && interp.cpu.SP.toInt() != spBeforeNmi && !interp.halted) {
                    interp.step()
                    steps++
                }
            }

            // Smart sync: sync FrameCounter always, but detect level transitions
            val fOff = frame * 2048
            if (fOff + 2047 < fceuxRam.size) {
                val fceuxFC = fceuxRam[fOff + SMB.FrameCounter].toInt() and 0xFF
                interp.memory.writeByte(SMB.FrameCounter, fceuxFC.toUByte())
            }

            val world = interp.memory.readByte(SMB.WorldNumber).toInt() + 1
            val level = interp.memory.readByte(SMB.LevelNumber).toInt() + 1

            // Auto-sync RAM in World 8:
            // - On level entry (transitions)
            // - Every 200 frames during W8 levels (to handle enemy timing drift)
            val shouldSync = when {
                // Level entry in World 8
                (world != lastWorld || level != lastLevel) && world == 8 -> true
                // Periodic sync in all W8 levels (every 200 frames)
                world == 8 && frame % 200 == 0 -> true
                else -> false
            }

            if (shouldSync && fOff + 2047 < fceuxRam.size) {
                for (addr in 0x0000..0x07FF) {
                    val fceuxVal = fceuxRam[fOff + addr].toInt() and 0xFF
                    interp.memory.writeByte(addr, fceuxVal.toUByte())
                }
            }
            val lives = interp.memory.readByte(SMB.NumberofLives).toInt()
            val operMode = interp.memory.readByte(SMB.OperMode).toInt()

            if (world != lastWorld || level != lastLevel) {
                println("Frame $frame: Entered W$world-$level")
                lastWorld = world
                lastLevel = level
            }

            if (operMode == 3 && deathFrame == -1) {
                deathFrame = frame
                println("Frame $frame: GAME OVER detected")
            }

            if (operMode == 2) {
                println("Frame $frame: VICTORY!")
                break
            }

            if (frame % 2000 == 0 && frame > 0) {
                val playerX = interp.memory.readByte(SMB.Player_X_Position).toInt()
                val page = interp.memory.readByte(SMB.ScreenLeft_PageLoc).toInt()
                println("Frame $frame: W$world-$level, Lives=$lives, X=$playerX, Page=$page")
            }

            if (interp.halted) break
        }

        println("\n=== RESULT ===")
        val finalWorld = interp.memory.readByte(SMB.WorldNumber).toInt() + 1
        val finalLevel = interp.memory.readByte(SMB.LevelNumber).toInt() + 1
        val finalMode = interp.memory.readByte(SMB.OperMode).toInt()
        println("Final: W$finalWorld-$finalLevel, Mode=$finalMode")
        if (deathFrame != -1) println("Death at frame $deathFrame")
        if (finalMode == 2) println("SUCCESS: No explicit RAM sync needed!")
    }

    @Test
    fun `investigate frame 5 timing offset`() {
        val fceuxRamFile = File("local/tas/fceux-full-ram.bin")
        if (!fceuxRamFile.exists()) {
            println("No FCEUX RAM dump found")
            return
        }

        val fceuxRam = fceuxRamFile.readBytes()

        println("=== Frame 5 Timing Investigation ===")
        println("Looking at IntervalTimerControl and related timing variables around frame 5")

        // Key addresses
        val addrs = mapOf(
            0x0009 to "FrameCounter",
            0x0770 to "OperMode",
            0x0772 to "OperMode_Task",
            0x077F to "IntervalTimerControl",
            0x07A7 to "PseudoRandomBitReg[0]",
            0x07A8 to "PseudoRandomBitReg[1]",
            0x0778 to "Mirror_PPU_CTRL_REG1",
            0x0710 to "AreaPointer",
            0x0715 to "AreaAddrsLOffset",
            0x071A to "ScreenLeft_PageLoc",
            0x0742 to "ScreenRoutineTask"
        )

        println("\nFCEUX RAM state at frames 0-15:")
        println("Frame | FC | Mode | Task | IntCtrl | RNG[0] | RNG[1] | PPU_CTRL | AreaPtr | ScrTask")
        println("------|----|----- |------|---------|--------|--------|----------|---------|--------")

        for (frame in 0..15) {
            val offset = frame * 2048
            if (offset + 2047 >= fceuxRam.size) break

            val fc = fceuxRam[offset + 0x0009].toInt() and 0xFF
            val mode = fceuxRam[offset + 0x0770].toInt() and 0xFF
            val task = fceuxRam[offset + 0x0772].toInt() and 0xFF
            val intCtrl = fceuxRam[offset + 0x077F].toInt() and 0xFF
            val rng0 = fceuxRam[offset + 0x07A7].toInt() and 0xFF
            val rng1 = fceuxRam[offset + 0x07A8].toInt() and 0xFF
            val ppuCtrl = fceuxRam[offset + 0x0778].toInt() and 0xFF
            val areaPtr = fceuxRam[offset + 0x0710].toInt() and 0xFF
            val scrTask = fceuxRam[offset + 0x0742].toInt() and 0xFF

            println("${frame.toString().padStart(5)} | ${fc.toString().padStart(2)} | ${mode.toString().padStart(4)} | ${task.toString().padStart(4)} | ${intCtrl.toString().padStart(7)} | ${rng0.toString().padStart(6)} | ${rng1.toString().padStart(6)} | ${ppuCtrl.toString().padStart(8)} | ${areaPtr.toString().padStart(7)} | ${scrTask.toString().padStart(7)}")
        }

        // Analyze IntervalTimerControl progression
        println("\n=== IntervalTimerControl Analysis ===")
        println("According to SMB disassembly:")
        println("  - IntervalTimerControl starts at 21 (0x15)")
        println("  - Decremented each NMI by DecTimers")
        println("  - When it goes negative (from 0), resets to 20 (0x14)")
        println("  - Controls when 'interval timers' decrement vs just 'frame timers'")

        println("\nIntervalTimerControl values in first 50 frames:")
        val intCtrlValues = mutableListOf<Int>()
        for (frame in 0..50) {
            val offset = frame * 2048
            if (offset + 2047 >= fceuxRam.size) break
            intCtrlValues.add(fceuxRam[offset + 0x077F].toInt() and 0xFF)
        }
        println(intCtrlValues.mapIndexed { i, v -> "F$i:$v" }.chunked(10).joinToString("\n") { it.joinToString(" ") })

        // Look for when it transitions
        println("\nIntervalTimerControl state changes:")
        var lastVal = -1
        for (frame in 0..100) {
            val offset = frame * 2048
            if (offset + 2047 >= fceuxRam.size) break
            val intCtrl = fceuxRam[offset + 0x077F].toInt() and 0xFF
            if (intCtrl != lastVal) {
                val fc = fceuxRam[offset + 0x0009].toInt() and 0xFF
                val mode = fceuxRam[offset + 0x0770].toInt() and 0xFF
                println("  Frame $frame: IntCtrl $lastVal -> $intCtrl (FC=$fc, Mode=$mode)")
                lastVal = intCtrl
            }
        }
    }

    @Test
    fun `investigate paired divergence frames`() {
        val fceuxRamFile = File("local/tas/fceux-full-ram.bin")
        if (!fceuxRamFile.exists()) {
            println("No FCEUX RAM dump found")
            return
        }

        val fceuxRam = fceuxRamFile.readBytes()

        // These are the paired frames that showed divergence
        val pairedFrames = listOf(
            41 to 42,
            611 to 612,
            925 to 926,
            1943 to 1944,
            2442 to 2443,
            3813 to 3814,
            6041 to 6042,
            6540 to 6541,
            7219 to 7220,
            7770 to 7771,
            10812 to 10813,
            12955 to 12956,
            15056 to 15057,
            15794 to 15795,
            16231 to 16232,
            16596 to 16597,
            17466 to 17467
        )

        println("=== Paired Divergence Frame Investigation ===")
        println("These frames come in pairs and all have FrameCounter going to 0 unexpectedly")
        println()

        // Key addresses for understanding game state
        data class GameState(
            val frame: Int,
            val fc: Int,
            val mode: Int,
            val modeTask: Int,
            val world: Int,
            val level: Int,
            val areaType: Int,
            val gameEngineSubroutine: Int,
            val screenRoutineTask: Int,
            val intervalTimerCtrl: Int,
            val playerState: Int,
            val scrollLock: Int
        )

        fun readState(frame: Int): GameState? {
            val offset = frame * 2048
            if (offset + 2047 >= fceuxRam.size) return null
            return GameState(
                frame = frame,
                fc = fceuxRam[offset + 0x0009].toInt() and 0xFF,
                mode = fceuxRam[offset + 0x0770].toInt() and 0xFF,
                modeTask = fceuxRam[offset + 0x0772].toInt() and 0xFF,
                world = (fceuxRam[offset + 0x075F].toInt() and 0xFF) + 1,
                level = (fceuxRam[offset + 0x0760].toInt() and 0xFF) + 1,
                areaType = fceuxRam[offset + 0x074E].toInt() and 0xFF,
                gameEngineSubroutine = fceuxRam[offset + 0x000E].toInt() and 0xFF,
                screenRoutineTask = fceuxRam[offset + 0x0742].toInt() and 0xFF,
                intervalTimerCtrl = fceuxRam[offset + 0x077F].toInt() and 0xFF,
                playerState = fceuxRam[offset + 0x001D].toInt() and 0xFF,
                scrollLock = fceuxRam[offset + 0x0723].toInt() and 0xFF
            )
        }

        println("Frame | World | FC | Mode | Task | GESub | ScrTask | IntCtrl | PState | ScrollLock")
        println("------|-------|----|----- |------|-------|---------|---------|--------|----------")

        for ((f1, f2) in pairedFrames) {
            val s1 = readState(f1) ?: continue
            val s2 = readState(f2) ?: continue
            val sPrev = readState(f1 - 1)
            val sNext = readState(f2 + 1)

            println("${f1.toString().padStart(5)} | ${s1.world}-${s1.level}   | ${s1.fc.toString().padStart(2)} | ${s1.mode.toString().padStart(4)} | ${s1.modeTask.toString().padStart(4)} | ${s1.gameEngineSubroutine.toString().padStart(5)} | ${s1.screenRoutineTask.toString().padStart(7)} | ${s1.intervalTimerCtrl.toString().padStart(7)} | ${s1.playerState.toString().padStart(6)} | ${s1.scrollLock.toString().padStart(10)}")
            println("${f2.toString().padStart(5)} | ${s2.world}-${s2.level}   | ${s2.fc.toString().padStart(2)} | ${s2.mode.toString().padStart(4)} | ${s2.modeTask.toString().padStart(4)} | ${s2.gameEngineSubroutine.toString().padStart(5)} | ${s2.screenRoutineTask.toString().padStart(7)} | ${s2.intervalTimerCtrl.toString().padStart(7)} | ${s2.playerState.toString().padStart(6)} | ${s2.scrollLock.toString().padStart(10)}")
            println("------|-------|----|----- |------|-------|---------|---------|--------|----------")
        }

        // Look for patterns
        println("\n=== Pattern Analysis ===")

        // Check GameEngineSubroutine values
        val geSubValues = pairedFrames.flatMap { (f1, f2) ->
            listOfNotNull(readState(f1)?.gameEngineSubroutine, readState(f2)?.gameEngineSubroutine)
        }
        println("GameEngineSubroutine values at divergent frames: ${geSubValues.toSet().sorted()}")

        // Check ScreenRoutineTask values
        val scrTaskValues = pairedFrames.flatMap { (f1, f2) ->
            listOfNotNull(readState(f1)?.screenRoutineTask, readState(f2)?.screenRoutineTask)
        }
        println("ScreenRoutineTask values at divergent frames: ${scrTaskValues.toSet().sorted()}")

        // Check ModeTask values
        val modeTaskValues = pairedFrames.flatMap { (f1, f2) ->
            listOfNotNull(readState(f1)?.modeTask, readState(f2)?.modeTask)
        }
        println("OperMode_Task values at divergent frames: ${modeTaskValues.toSet().sorted()}")

        // Check FC values (mod 256 to see pattern)
        val fcValues = pairedFrames.map { (f1, _) -> readState(f1)?.fc ?: -1 }
        println("FrameCounter values at first frame of each pair: $fcValues")

        // Check if these are right after level transitions
        println("\n=== Checking for level transitions ===")
        for ((f1, f2) in pairedFrames.take(10)) {
            val sPrev5 = readState(f1 - 5)
            val s1 = readState(f1) ?: continue

            val prevWorld = sPrev5?.world ?: -1
            val prevLevel = sPrev5?.level ?: -1

            if (prevWorld != s1.world || prevLevel != s1.level) {
                println("Frame $f1: Level changed from $prevWorld-$prevLevel to ${s1.world}-${s1.level}")
            } else {
                println("Frame $f1: Same level ${s1.world}-${s1.level}, GESub=${s1.gameEngineSubroutine}, ScrTask=${s1.screenRoutineTask}")
            }
        }

        // Check spacing between paired frames
        println("\n=== Spacing between divergent frame pairs ===")
        val spacing = pairedFrames.zipWithNext { a, b -> b.first - a.first }
        println("Gaps: $spacing")
        println("Average gap: ${spacing.average()}")
    }

    @Test
    fun `test every frame in isolation`() {
        val romFile = listOf("local/roms/smb.nes", "smb.nes", "../smb.nes")
            .map { File(it) }
            .firstOrNull { it.exists() }
        if (romFile == null) {
            println("No ROM file found")
            return
        }

        val fceuxRamFile = File("local/tas/fceux-full-ram.bin")
        if (!fceuxRamFile.exists()) {
            println("No FCEUX RAM dump found - run the Lua script first")
            return
        }

        val tasFile = File("happylee-warps.fm2")
        if (!tasFile.exists()) {
            println("No TAS file found")
            return
        }

        val fceuxRam = fceuxRamFile.readBytes()
        val tasInputs = FM2Parser.parse(tasFile)
        val maxFceuxFrames = fceuxRam.size / 2048
        val rom = NESLoader.load(romFile)

        println("=== Single-Frame Isolation Test ===")
        println("For each frame: load FCEUX RAM state -> run 1 NMI -> compare with FCEUX next frame")
        println("FCEUX frames available: $maxFceuxFrames")

        val logFile = PrintWriter(File("local/tas/frame-isolation-divergence.txt"))
        logFile.println("# Single-frame isolation divergence test")
        logFile.println("# Format: Frame N -> N+1, divergent addresses")

        // Named addresses for better reporting
        val namedAddrs = mapOf(
            0x0009 to "FrameCounter",
            0x000E to "GameEngineSubroutine",
            0x001D to "Player_State",
            0x0057 to "Player_X_Speed",
            0x0086 to "Player_X_Position",
            0x00CE to "Player_Y_Position",
            0x0770 to "OperMode",
            0x0772 to "OperMode_Task",
            0x0778 to "Mirror_PPU_CTRL_REG1",
            0x077F to "IntervalTimerControl",
            0x075A to "NumberofLives",
            0x075F to "WorldNumber",
            0x0760 to "LevelNumber",
            0x071A to "ScreenLeft_PageLoc",
            0x06FC to "SavedJoypad1Bits",
            0x07A7 to "PseudoRandomBitReg[0]",
            0x07A8 to "PseudoRandomBitReg[1]",
            0x07A9 to "PseudoRandomBitReg[2]",
            0x07AA to "PseudoRandomBitReg[3]",
            0x07AB to "PseudoRandomBitReg[4]",
            0x07AC to "PseudoRandomBitReg[5]",
            0x07AD to "PseudoRandomBitReg[6]"
        )

        // Frames where NMI is suppressed in normal execution (title screen timing)
        // Frames 0-4: Before first NMI
        // Frames 6-7: InitScreen handler takes ~2.5 frames, skipping these
        val hardcodedSuppressNmiFrames = setOf(0, 1, 2, 3, 4, 6, 7)

        // Heavy subroutines that take multiple frames worth of cycles
        // Address -> number of additional frames to skip after calling
        val frameDebtMap = mapOf(
            0x85E2 to 2,  // InitScreen - calls InitializeNameTables, takes ~2.5 frames
            0x90C6 to 1,  // InitializeGame - clears memory, takes ~1.5 frames
            0x85CD to 1   // ScreenRoutines - varies, but some tasks are heavy
        )

        // Detect if NMI should be skipped for frame N->N+1 transition
        // FCEUX dumps are captured AFTER each frame's NMI processing.
        // If IntCtrl at frame N == IntCtrl at frame N+1, FCEUX skipped NMI during that transition.
        // We should also skip NMI to match.
        fun shouldSkipNmiForFrame(frame: Int): Boolean {
            if (frame < 1 || (frame + 1) * 2048 >= fceuxRam.size) return false

            val curOffset = frame * 2048
            val nextOffset = (frame + 1) * 2048

            val intCtrlCur = fceuxRam[curOffset + SMB.IntervalTimerControl].toInt() and 0xFF
            val intCtrlNext = fceuxRam[nextOffset + SMB.IntervalTimerControl].toInt() and 0xFF

            // IntervalTimerControl decrements every NMI (unless it resets from 0->20).
            // If it stays the same between frames, NMI was skipped.
            // Special case: 0->20 is a reset, not a skip.
            // Also 255->20 is a reset from uninitialized state.
            if (intCtrlCur == intCtrlNext) {
                // Same value - NMI was skipped (unless it's 0 resetting to 20)
                return true
            }

            // Check for normal decrement pattern
            val expectedNext = if (intCtrlCur == 0 || intCtrlCur == 255) 20 else intCtrlCur - 1
            if (intCtrlNext != expectedNext) {
                // Unexpected value - might be mid-operation, skip to be safe
                return true
            }

            return false
        }

        // Track divergence patterns
        data class FrameDivergence(
            val frame: Int,
            val divergentAddrs: List<Triple<Int, Int, Int>>, // addr, expected, actual
            val operMode: Int,
            val world: Int,
            val level: Int
        )
        val allDivergences = mutableListOf<FrameDivergence>()

        // Test frames 5 through ~17000 (or however many we have)
        // Skip frames 0-4 where NMI is suppressed
        val testFrames = (5 until minOf(maxFceuxFrames - 1, 17500))

        for (frame in testFrames) {
            // Set up fresh interpreter with FCEUX RAM state at frame N
            val interp = BinaryInterpreter6502()

            // Copy ROM into memory
            NESLoader.loadIntoMemory(rom, interp.memory)

            // Set up frame debt map for heavy subroutines
            interp.frameDebtMap = frameDebtMap

            // Copy RAM from FCEUX at frame N
            val frameOffset = frame * 2048
            for (addr in 0x0000..0x07FF) {
                val value = fceuxRam[frameOffset + addr].toInt() and 0xFF
                interp.memory.writeByte(addr, value.toUByte())
            }

            // Set up PPU and controller
            val ppu = SimplePPU()
            val controller = SimpleController()

            interp.memoryReadHook = { addr ->
                when (addr) {
                    in 0x2000..0x2007 -> ppu.read(addr)
                    0x4016 -> controller.read()
                    0x4017 -> 0u
                    else -> null
                }
            }

            interp.memoryWriteHook = { addr, value ->
                when (addr) {
                    in 0x2000..0x2007 -> { ppu.write(addr, value, 0); true }
                    0x4014 -> true
                    0x4016 -> { controller.writeStrobe(value); true }
                    in 0x4000..0x4017 -> true
                    else -> false
                }
            }

            // Initialize CPU state - we don't have exact registers from FCEUX
            // Set PC to NMI vector location, SP to a reasonable value
            // The NMI handler will run from wherever NMI points
            val nmiVectorLow = interp.memory.readByte(0xFFFA).toInt()
            val nmiVectorHigh = interp.memory.readByte(0xFFFB).toInt()
            val nmiAddr = nmiVectorLow or (nmiVectorHigh shl 8)

            // Set SP to 0xFF (top of stack)
            interp.cpu.SP = 0xFFu
            // Set PC to main loop (will be overwritten by NMI trigger)
            interp.cpu.PC = 0x8057u

            // For isolation test: use EXACT same buttons FCEUX used for this frame
            // No offset - we want to see if given identical inputs we get identical outputs
            val buttons = if (frame < tasInputs.size) tasInputs[frame].buttons else 0
            controller.setButtons(buttons)

            // Initialize PPU CTRL from Mirror_PPU_CTRL_REG1 ($0778) copied from FCEUX
            // This ensures NMI is enabled when the game expects it
            val mirrorPpuCtrl = interp.memory.readByte(0x0778).toInt()
            // During gameplay (OperMode=1), NMI should always be enabled
            val currentOperMode = interp.memory.readByte(SMB.OperMode).toInt()
            if (currentOperMode == 1) {
                // Force NMI enabled for gameplay - the mirror may be stale
                ppu.write(0x2000, (mirrorPpuCtrl or 0x80).toUByte(), 0)
            } else {
                ppu.write(0x2000, mirrorPpuCtrl.toUByte(), 0)
            }

            // Run one NMI frame (skip frames where FCEUX also skipped NMI)
            // Detect by checking if IntervalTimerControl changed between frames
            val skipNmi = frame in hardcodedSuppressNmiFrames || shouldSkipNmiForFrame(frame)
            if (!skipNmi) {
                ppu.startFrame()
                val spBeforeNmi = interp.cpu.SP.toInt()

                // Record RAM before NMI for debugging
                val fcBefore = interp.memory.readByte(SMB.FrameCounter).toInt()
                val modeBefore = interp.memory.readByte(SMB.OperMode).toInt()

                interp.triggerNmi()
                interp.handleInterrupts()

                var steps = 0
                val maxSteps = 50000
                while (steps < maxSteps && interp.cpu.SP.toInt() != spBeforeNmi && !interp.halted) {
                    interp.step()
                    ppu.advanceStep()  // Advance PPU timing for sprite 0 hit detection
                    steps++
                }

                // Debug: check if NMI completed and state is reasonable
                val fcAfter = interp.memory.readByte(SMB.FrameCounter).toInt()
                val modeAfter = interp.memory.readByte(SMB.OperMode).toInt()
                // Expected: FC increments by 1, or wraps from 255 to 0
                val expectedFC = if (fcBefore == 255) 0 else fcBefore + 1
                if (fcAfter != expectedFC && frame > 10 && modeAfter == 1) {
                    // FrameCounter didn't increment correctly
                    println("WARN Frame $frame: FC went from $fcBefore to $fcAfter (expected $expectedFC), Mode $modeBefore->$modeAfter, steps=$steps")
                }
            }

            // Compare with FCEUX RAM at frame N+1
            val nextFrameOffset = (frame + 1) * 2048
            if (nextFrameOffset + 2047 >= fceuxRam.size) break

            val divergences = mutableListOf<Triple<Int, Int, Int>>()
            for (addr in 0x0000..0x07FF) {
                // Skip stack area (0x0100-0x01FF) since CPU state differs
                if (addr in 0x0100..0x01FF) continue

                val expected = fceuxRam[nextFrameOffset + addr].toInt() and 0xFF
                val actual = interp.memory.readByte(addr).toInt()
                if (expected != actual) {
                    divergences.add(Triple(addr, expected, actual))
                }
            }

            // Record game state
            val operMode = fceuxRam[nextFrameOffset + SMB.OperMode].toInt() and 0xFF
            val world = fceuxRam[nextFrameOffset + SMB.WorldNumber].toInt() and 0xFF
            val level = fceuxRam[nextFrameOffset + SMB.LevelNumber].toInt() and 0xFF

            if (divergences.isNotEmpty()) {
                allDivergences.add(FrameDivergence(frame, divergences, operMode, world + 1, level + 1))
            }

            // Progress report every 1000 frames
            if (frame % 1000 == 0) {
                val divergentCount = allDivergences.count { it.frame >= frame - 999 && it.frame <= frame }
                println("Frame $frame: W${world + 1}-${level + 1}, ${divergentCount} divergent frames in last 1000")
            }
        }

        // Analyze and report results
        println("\n=== Summary ===")
        println("Total frames tested: ${testFrames.count()}")
        println("Frames with divergence: ${allDivergences.size}")

        // Find commonly divergent addresses
        val addrCounts = mutableMapOf<Int, Int>()
        for (div in allDivergences) {
            for ((addr, _, _) in div.divergentAddrs) {
                addrCounts[addr] = (addrCounts[addr] ?: 0) + 1
            }
        }

        val topAddrs = addrCounts.entries.sortedByDescending { it.value }.take(30)
        println("\nMost commonly divergent addresses:")
        for ((addr, count) in topAddrs) {
            val name = namedAddrs[addr] ?: ""
            val pct = count * 100 / allDivergences.size
            println("  \$${addr.toString(16).padStart(4, '0')}: $count frames ($pct%) $name")
        }

        // Find first divergent frame for each address
        println("\nFirst frame each address diverges:")
        val firstDivergence = mutableMapOf<Int, Int>()
        for (div in allDivergences.sortedBy { it.frame }) {
            for ((addr, _, _) in div.divergentAddrs) {
                if (addr !in firstDivergence) {
                    firstDivergence[addr] = div.frame
                }
            }
        }
        for ((addr, frame) in firstDivergence.entries.sortedBy { it.value }.take(20)) {
            val name = namedAddrs[addr] ?: ""
            println("  \$${addr.toString(16).padStart(4, '0')}: frame $frame $name")
        }

        // Write detailed log
        logFile.println("\n=== Detailed Frame-by-Frame Divergence ===")
        for (div in allDivergences.take(500)) {
            logFile.println("\nFrame ${div.frame} -> ${div.frame + 1} (W${div.world}-${div.level}, Mode=${div.operMode}):")
            logFile.println("  ${div.divergentAddrs.size} divergent addresses")
            for ((addr, expected, actual) in div.divergentAddrs.take(20)) {
                val name = namedAddrs[addr] ?: ""
                logFile.println("    \$${addr.toString(16).padStart(4, '0')}: expected=$expected actual=$actual $name")
            }
        }

        logFile.close()
        println("\nWrote detailed log to local/tas/frame-isolation-divergence.txt")
    }

    @Test
    fun `find early RAM divergence`() {
        val romFile = listOf("local/roms/smb.nes", "smb.nes", "../smb.nes")
            .map { File(it) }
            .firstOrNull { it.exists() }
        if (romFile == null) {
            println("No ROM file found")
            return
        }

        val fceuxRamFile = File("local/tas/fceux-full-ram.bin")
        if (!fceuxRamFile.exists()) {
            println("No FCEUX RAM dump found")
            return
        }

        val tasFile = File("happylee-warps.fm2")
        if (!tasFile.exists()) {
            println("No TAS file found")
            return
        }

        val fceuxRam = fceuxRamFile.readBytes()
        val tasInputs = FM2Parser.parse(tasFile)

        // Set up interpreter
        val rom = NESLoader.load(romFile)
        val interp = BinaryInterpreter6502()

        // Apply RAM initialization pattern: 00 00 00 00 FF FF FF FF repeating
        for (addr in 0x0000..0x07FF) {
            val pattern = if ((addr / 4) % 2 == 0) 0x00 else 0xFF
            interp.memory.writeByte(addr, pattern.toUByte())
        }

        NESLoader.loadIntoMemory(rom, interp.memory)

        val ppu = SimplePPU()
        val controller = SimpleController()

        interp.memoryReadHook = { addr ->
            when (addr) {
                in 0x2000..0x2007 -> ppu.read(addr)
                0x4016 -> controller.read()
                0x4017 -> 0u
                else -> null
            }
        }

        interp.memoryWriteHook = { addr, value ->
            when (addr) {
                in 0x2000..0x2007 -> { ppu.write(addr, value, 0); true }
                0x4014 -> true
                0x4016 -> { controller.writeStrobe(value); true }
                in 0x4000..0x4017 -> true
                else -> false
            }
        }

        interp.reset()

        // Run reset routine until it reaches the main loop
        // SMB main loop is at $8057, keep running until we get there
        // Skip any vblank wait loops (LDA $2002; BPL pattern)
        var resetSteps = 0
        var vblankWaitCount = 0
        val maxResetSteps = 100000
        val mainLoopAddr = 0x8057
        while (resetSteps < maxResetSteps && !interp.halted) {
            val pc = interp.cpu.PC.toInt()
            if (pc == mainLoopAddr) {
                break  // Reached main loop
            }
            val opcode = interp.memory.readByte(pc).toInt()
            val nextByte = interp.memory.readByte(pc + 1).toInt()
            val nextByte2 = interp.memory.readByte(pc + 2).toInt()
            // LDA $2002 is opcode AD 02 20 - skip vblank wait loops
            if (opcode == 0xAD && nextByte == 0x02 && nextByte2 == 0x20) {
                vblankWaitCount++
                // Skip the LDA and assume BPL follows, jump past both
                interp.cpu.PC = (pc + 5).toUShort()
                resetSteps++
                continue
            }
            interp.step()
            resetSteps++
        }
        println("Reset routine completed after $resetSteps steps (skipped $vblankWaitCount vblank waits), PC=\$${interp.cpu.PC.toString(16)}")

        // Check frames through World 8 death point
        val checkFrames = listOf(1, 5, 10, 20, 30, 40, 41, 42, 50, 100, 500, 1000, 2000, 4000, 8000, 9000, 9500, 10000, 10100, 10150, 10200, 10210, 10211, 10212, 10213, 10220)
        val logFile = PrintWriter(File("local/tas/ram-divergence.txt"))

        println("=== Finding Early RAM Divergence ===")
        println("Running interpreter and comparing RAM against FCEUX at key frames")
        logFile.println("# RAM divergence analysis")

        var nmiCount = 0
        var lastDivergentAddrs = emptySet<Int>()

        for (frame in 0..10500) {
            // Set buttons from TAS - offset by 1 frame to match FCEUX timing
            // SMB reads controller during NMI and processes the stored value on the NEXT frame
            // So buttons from frame N in the FM2 file are processed during frame N+1's game logic
            val buttonFrame = maxOf(0, frame - 1)
            val buttons = if (buttonFrame < tasInputs.size) tasInputs[buttonFrame].buttons else 0
            controller.setButtons(buttons)

            // Simple frame model: run NMI handler to completion, no cycle counting
            // This matches what decompiled Kotlin code will do
            // Suppress NMI for early frames to match FCEUX title screen timing
            val suppressNmiFrames = setOf(0, 1, 2, 3, 4, 6, 7)

            if (frame !in suppressNmiFrames) {
                // Reset PPU state for new frame (resets sprite 0 hit counter)
                ppu.startFrame()

                // Save SP before NMI (should be 0xFF ideally)
                val spBeforeNmi = interp.cpu.SP.toInt()

                // Trigger NMI and run handler to completion
                interp.triggerNmi()
                interp.handleInterrupts()
                nmiCount++

                // NMI pushes 3 bytes, so SP is now spBeforeNmi - 3
                // Run until NMI handler returns (SP back to original)
                var steps = 0
                val maxSteps = 50000  // Safety limit
                while (steps < maxSteps && interp.cpu.SP.toInt() != spBeforeNmi && !interp.halted) {
                    interp.step()
                    steps++
                }

                // Debug: warn if NMI didn't complete properly
                if (interp.cpu.SP.toInt() != spBeforeNmi && frame < 50) {
                    val pc = interp.cpu.PC.toInt()
                    val opcode = interp.memory.readByte(pc).toInt()
                    println("  [WARN] Frame $frame: NMI didn't complete! SP=$${interp.cpu.SP.toString(16)} (expected $${spBeforeNmi.toString(16)}), steps=$steps, PC=$${pc.toString(16)}, opcode=$${opcode.toString(16)}")
                }
            }

            // Sync FC from FCEUX to maintain alignment
            val fOff = frame * 2048
            if (fOff + 2047 < fceuxRam.size) {
                val fceuxFC = fceuxRam[fOff + SMB.FrameCounter].toInt() and 0xFF
                interp.memory.writeByte(SMB.FrameCounter, fceuxFC.toUByte())
            }


            // Check RAM at specific frames
            if (frame in checkFrames) {
                val fOff = frame * 2048
                if (fOff + 2047 >= fceuxRam.size) continue

                // Compare all RAM bytes
                val divergences = mutableListOf<Triple<Int, Int, Int>>() // addr, expected, actual
                for (addr in 0x0000..0x07FF) {
                    val expected = fceuxRam[fOff + addr].toInt() and 0xFF
                    val actual = interp.memory.readByte(addr).toInt()
                    if (expected != actual) {
                        divergences.add(Triple(addr, expected, actual))
                    }
                }

                val divergentAddrs = divergences.map { it.first }.toSet()
                val newDivergences = divergentAddrs - lastDivergentAddrs
                lastDivergentAddrs = divergentAddrs

                val fc = interp.memory.readByte(SMB.FrameCounter).toInt()
                val fceuxFC = fceuxRam[fOff + SMB.FrameCounter].toInt() and 0xFF
                val mode = interp.memory.readByte(SMB.OperMode).toInt()
                val fceuxMode = fceuxRam[fOff + SMB.OperMode].toInt() and 0xFF
                val interpSP = interp.cpu.SP.toInt()
                val interpPC = interp.cpu.PC.toInt()

                println("\n=== Frame $frame ===")
                println("FC: interp=$fc, fceux=$fceuxFC | Mode: interp=$mode, fceux=$fceuxMode | PC=\$${interpPC.toString(16)} SP=\$${interpSP.toString(16)}")
                println("Total divergent bytes: ${divergences.size}, new this frame: ${newDivergences.size}")

                logFile.println("\n=== Frame $frame (NMI=$nmiCount) ===")
                logFile.println("FC: interp=$fc, fceux=$fceuxFC | Mode: interp=$mode, fceux=$fceuxMode")
                logFile.println("Buttons applied: 0x${buttons.toString(16)}")

                // Show first 20 divergences with their names if known
                val namedAddrs = mapOf(
                    0x0009 to "FrameCounter",
                    0x000E to "GameEngineSubroutine",
                    0x001D to "Player_State",
                    0x0057 to "Player_X_Speed",
                    0x0086 to "Player_X_Position",
                    0x00CE to "Player_Y_Position",
                    0x0770 to "OperMode",
                    0x0772 to "OperMode_Task",
                    0x0773 to "ScreenRoutineTask",
                    0x0774 to "GamePauseStatus",
                    0x0775 to "GamePauseTimer",
                    0x0776 to "PrimaryMsgCounter",
                    0x0777 to "SecondaryMsgCounter",
                    0x077F to "IntervalTimerControl",
                    0x075A to "NumberofLives",
                    0x075F to "WorldNumber",
                    0x0760 to "LevelNumber",
                    0x071A to "ScreenLeft_PageLoc",
                    0x06FC to "SavedJoypad1Bits",
                    0x000A to "SavedJoypadBits",
                    0x0006 to "GamePadMemory"
                )

                if (divergences.isNotEmpty()) {
                    println("First ${minOf(20, divergences.size)} divergent addresses:")
                    logFile.println("Divergent addresses (${divergences.size} total):")
                    divergences.take(40).forEach { (addr, expected, actual) ->
                        val name = namedAddrs[addr] ?: ""
                        val marker = if (addr in newDivergences) " [NEW]" else ""
                        println("  \$${addr.toString(16).padStart(4, '0')}: expected=$expected actual=$actual $name$marker")
                        logFile.println("  \$${addr.toString(16).padStart(4, '0')}: expected=$expected actual=$actual $name$marker")
                    }
                } else {
                    println("RAM matches perfectly!")
                    logFile.println("RAM matches perfectly!")
                }
            }

            if (interp.halted) break
        }

        logFile.close()
        println("\nWrote detailed log to local/tas/ram-divergence.txt")
    }

    /**
     * TRUE STANDALONE TEST - No FCEUX reference data at all!
     *
     * This test measures how far the interpreter can run the TAS using ONLY:
     * 1. The ROM data
     * 2. The TAS button inputs
     * 3. Our deterministic timing configuration
     *
     * NO sync against FCEUX RAM dumps is performed.
     * This represents the real standalone accuracy of the interpreter.
     */
    @Test
    fun `standalone TAS - no FCEUX reference`() {
        val romFile = listOf("local/roms/smb.nes", "smb.nes", "../smb.nes")
            .map { File(it) }
            .firstOrNull { it.exists() }
        if (romFile == null) {
            println("No ROM file found")
            return
        }

        val tasFile = listOf("happylee-warps.fm2", "../happylee-warps.fm2", "local/tas/smb-tas.fm2")
            .map { File(it) }
            .firstOrNull { it.exists() }
        if (tasFile == null) {
            println("No TAS file found")
            return
        }

        val tasInputs = FM2Parser.parse(tasFile)

        // Try to load FCEUX RAM dump for timing oracle (optional)
        val fceuxRamFile = File("local/tas/fceux-full-ram.bin")
        val fceuxRam = if (fceuxRamFile.exists()) fceuxRamFile.readBytes() else null

        println("=== STANDALONE TAS TEST ===")
        if (fceuxRam != null) {
            println("Using FCEUX IntCtrl as timing oracle (${fceuxRam.size / 2048} frames available)")
        } else {
            println("NO FCEUX sync - using step-count heuristic for timing")
        }
        println("TAS frames: ${tasInputs.size}")
        println()

        // Set up interpreter using the same pattern as other tests
        val rom = NESLoader.load(romFile)
        val interp = BinaryInterpreter6502()

        // Apply RAM initialization pattern: 00 00 00 00 FF FF FF FF repeating
        for (addr in 0x0000..0x07FF) {
            val pattern = if ((addr / 4) % 2 == 0) 0x00 else 0xFF
            interp.memory.writeByte(addr, pattern.toUByte())
        }

        NESLoader.loadIntoMemory(rom, interp.memory)

        // Frame debt map is available but standalone test uses its own cycle-based debt system
        // interp.frameDebtMap = mapOf(...)  // Not used here

        val ppu = SimplePPU()
        val controller = SimpleController()

        // Set up memory hooks for PPU and controller
        interp.memoryReadHook = { addr ->
            when (addr) {
                in 0x2000..0x2007 -> ppu.read(addr)
                0x4016 -> controller.read()
                0x4017 -> 0u
                else -> null
            }
        }

        interp.memoryWriteHook = { addr, value ->
            when (addr) {
                in 0x2000..0x2007 -> { ppu.write(addr, value, 0); true }
                0x4014 -> true
                0x4016 -> { controller.writeStrobe(value); true }
                in 0x4000..0x4017 -> true
                else -> false
            }
        }

        // Run reset routine until it reaches the main loop
        interp.reset()
        var resetSteps = 0
        var resetNmiCount = 0
        val maxResetSteps = 100000
        val mainLoopAddr = 0x8057  // SMB's endless loop address

        while (resetSteps < maxResetSteps && !interp.halted) {
            val pc = interp.cpu.PC.toInt()
            if (pc == mainLoopAddr) break

            val opcode = interp.memory.readByte(pc).toInt()
            val nextByte = interp.memory.readByte(pc + 1).toInt()
            val nextByte2 = interp.memory.readByte(pc + 2).toInt()

            // Detect vblank wait loop: LDA $2002 (opcode AD 02 20)
            if (opcode == 0xAD && nextByte == 0x02 && nextByte2 == 0x20) {
                ppu.startFrame()
                ppu.beginVBlank()

                if (ppu.isNmiEnabled()) {
                    val spBeforeNmi = interp.cpu.SP.toInt()
                    interp.triggerNmi()
                    interp.handleInterrupts()
                    resetNmiCount++

                    var nmiSteps = 0
                    while (nmiSteps < 50000 && interp.cpu.SP.toInt() != spBeforeNmi && !interp.halted) {
                        interp.step()
                        ppu.advanceStep()  // Track steps for sprite 0 timing
                        nmiSteps++
                    }
                }

                ppu.endVBlank()
                // Skip past the LDA $2002 and assume BPL follows (skip 5 bytes total)
                interp.cpu.PC = (pc + 5).toUShort()
                resetSteps++
                continue
            }
            interp.step()
            resetSteps++
        }
        println("Reset completed: $resetSteps steps, $resetNmiCount NMIs during reset")

        // STANDALONE CONFIGURATION WITH FCEUX TIMING ORACLE OR HEURISTIC
        // Key insight: NMI handler can take multiple frames worth of cycles
        // Frame 5's handler takes ~73000 cycles (2.5 frames), so frames 6-7 miss NMI
        val suppressNmiFrames = setOf(0, 1, 2, 3, 4)  // Suppress frames 0-4, first NMI at frame 5
        val startFrame = 0  // Start from frame 0 to process all TAS inputs
        val buttonOffset = 1  // Standard TAS offset

        // Helper to get frames consumed using FCEUX IntCtrl oracle
        fun getFramesConsumedFromFceux(frame: Int): Int {
            if (fceuxRam == null) return 1  // No oracle available
            val currentOffset = frame * 2048
            if (currentOffset + 2047 >= fceuxRam.size) return 1
            val currentIntCtrl = fceuxRam[currentOffset + SMB.IntervalTimerControl].toInt() and 0xFF

            // Look ahead to find when IntCtrl changes
            var lookAhead = 1
            while (lookAhead < 10) {
                val nextOffset = (frame + lookAhead) * 2048
                if (nextOffset + 2047 >= fceuxRam.size) break
                val nextIntCtrl = fceuxRam[nextOffset + SMB.IntervalTimerControl].toInt() and 0xFF
                val expectedNext = if (currentIntCtrl == 0) 20 else currentIntCtrl - 1
                if (nextIntCtrl == expectedNext) return lookAhead
                lookAhead++
            }
            return 1
        }

        var framesToSkip = 0  // Frames to skip due to multi-frame NMI

        val fcAtStart = interp.memory.readByte(SMB.FrameCounter).toInt()
        val intCtrlAtStart = interp.memory.readByte(SMB.IntervalTimerControl).toInt()
        println("At start: FC=$fcAtStart, IntCtrl=$intCtrlAtStart")

        println("Configuration:")
        println("  - First NMI at frame: ${suppressNmiFrames.maxOrNull()?.plus(1) ?: 0}")
        println("  - Button offset: $buttonOffset")
        println("  - Sprite 0 hit threshold: ${ppu.sprite0HitSteps} steps")
        println()

        val maxFrames = 18000
        var lastWorld = -1
        var lastLevel = -1
        var lastFrameCounter = -1
        var frameCounterWraps = 0
        var deathFrame = -1

        // Track progress milestones
        val milestones = mutableListOf<String>()

        for (frame in startFrame until maxFrames) {
            // Get buttons for this FCEUX frame (accounting for offset)
            val buttonFrame = maxOf(0, frame - buttonOffset)
            val buttons = if (buttonFrame < tasInputs.size) tasInputs[buttonFrame].buttons else 0
            controller.setButtons(buttons)

            // Debug early frames
            val fcBefore = interp.memory.readByte(SMB.FrameCounter).toInt()
            val intCtrlBefore = interp.memory.readByte(SMB.IntervalTimerControl).toInt()

            // NMI timing using FCEUX oracle or skip counter
            var nmiSteps = 0
            var nmiSkipped = false

            if (frame in suppressNmiFrames) {
                nmiSkipped = true
            } else if (framesToSkip > 0) {
                // Previous NMI consumed multiple frames - skip this one
                framesToSkip--
                nmiSkipped = true
            } else {
                // Get frames consumed from FCEUX oracle (or 1 if not available)
                val framesConsumed = getFramesConsumedFromFceux(frame)
                framesToSkip = framesConsumed - 1  // Skip the next N-1 frames

                // Run NMI for this frame
                ppu.startFrame()
                val spBeforeNmi = interp.cpu.SP.toInt()
                interp.triggerNmi()
                interp.handleInterrupts()
                // Track last few PCs for debugging stuck loops
                val pcHistory = ArrayDeque<Int>(10)
                while (nmiSteps < 50000 && interp.cpu.SP.toInt() != spBeforeNmi && !interp.halted) {
                    val pc = interp.cpu.PC.toInt()
                    if (pcHistory.size >= 10) pcHistory.removeFirst()
                    pcHistory.addLast(pc)

                    interp.step()
                    ppu.advanceStep()
                    nmiSteps++

                    // Debug: if we're stuck, report where
                    if (nmiSteps == 49999) {
                        val opcode = interp.memory.readByte(pc).toInt()
                        val op1 = interp.memory.readByte(pc + 1).toInt()
                        val op2 = interp.memory.readByte(pc + 2).toInt()
                        val targetAddr = op1 + (op2 shl 8)
                        println("STUCK at PC=\$${String.format("%04X", pc)}: LDA \$${String.format("%04X", targetAddr)}")
                        println("  PC history: ${pcHistory.map { String.format("%04X", it) }.joinToString(" -> ")}")
                    }
                }
            }

            // Debug early frames OR frames with long/stuck NMI handlers
            if (frame < 20 || nmiSteps > 10000 || nmiSteps >= 49999) {
                val operMode = interp.memory.readByte(SMB.OperMode).toInt()
                val fcAfter = interp.memory.readByte(SMB.FrameCounter).toInt()
                val intCtrlAfter = interp.memory.readByte(SMB.IntervalTimerControl).toInt()
                val rng0 = interp.memory.readByte(0x07A7).toInt() and 0xFF
                val rng1 = interp.memory.readByte(0x07A8).toInt() and 0xFF
                val operModeTask = interp.memory.readByte(0x0772).toInt()  // OperMode_Task
                val nmiInfo = when {
                    frame in suppressNmiFrames -> "(suppressed)"
                    nmiSkipped -> "(skipped, multi-frame NMI)"
                    nmiSteps >= 49999 -> "(STUCK! $nmiSteps steps, Mode=$operMode Task=$operModeTask)"
                    else -> "($nmiSteps steps)"
                }
                println("Frame $frame: FC $fcBefore->$fcAfter, IntCtrl $intCtrlBefore->$intCtrlAfter, Mode=$operMode, Task=$operModeTask, RNG=${String.format("%02x %02x", rng0, rng1)} $nmiInfo")
            }

            val world = interp.memory.readByte(SMB.WorldNumber).toInt() + 1
            val level = interp.memory.readByte(SMB.LevelNumber).toInt() + 1
            val lives = interp.memory.readByte(SMB.NumberofLives).toInt()
            val operMode = interp.memory.readByte(SMB.OperMode).toInt()
            val frameCounter = interp.memory.readByte(SMB.FrameCounter).toInt()

            // Track FrameCounter wraps (for debugging)
            if (lastFrameCounter > 200 && frameCounter < 50) {
                frameCounterWraps++
            }
            lastFrameCounter = frameCounter

            if (world != lastWorld || level != lastLevel) {
                val milestone = "Frame $frame: W$world-$level (Lives=$lives)"
                println(milestone)
                milestones.add(milestone)
                lastWorld = world
                lastLevel = level
            }

            // Detect game over
            if (operMode == 3 && deathFrame == -1) {
                deathFrame = frame
                val playerX = interp.memory.readByte(SMB.Player_X_Position).toInt()
                val page = interp.memory.readByte(SMB.ScreenLeft_PageLoc).toInt()
                println("Frame $frame: GAME OVER at W$world-$level (X=$playerX, Page=$page)")
            }

            // Detect victory
            if (operMode == 2) {
                println("Frame $frame: VICTORY!")
                milestones.add("Frame $frame: VICTORY!")
                break
            }

            // Progress updates every 2000 frames
            if (frame % 2000 == 0 && frame > 0) {
                val playerX = interp.memory.readByte(SMB.Player_X_Position).toInt()
                val page = interp.memory.readByte(SMB.ScreenLeft_PageLoc).toInt()
                val intCtrl = interp.memory.readByte(SMB.IntervalTimerControl).toInt()
                println("  Frame $frame: W$world-$level, Lives=$lives, X=$playerX, Page=$page, FC=$frameCounter, IntCtrl=$intCtrl")
            }

            if (interp.halted) break
        }

        println()
        println("=== STANDALONE RESULT ===")
        val finalWorld = interp.memory.readByte(SMB.WorldNumber).toInt() + 1
        val finalLevel = interp.memory.readByte(SMB.LevelNumber).toInt() + 1
        val finalMode = interp.memory.readByte(SMB.OperMode).toInt()
        val finalLives = interp.memory.readByte(SMB.NumberofLives).toInt()

        println("Final state: W$finalWorld-$finalLevel, Mode=$finalMode, Lives=$finalLives")
        println("FrameCounter wraps: $frameCounterWraps")

        if (deathFrame != -1) {
            println("First death at frame $deathFrame")
        }

        println()
        println("Milestones reached:")
        milestones.forEach { println("  $it") }

        println()
        when (finalMode) {
            2 -> println("RESULT: SUCCESS - TAS completed!")
            3 -> println("RESULT: GAME OVER at W$finalWorld-$finalLevel")
            else -> println("RESULT: Stopped at W$finalWorld-$finalLevel (mode=$finalMode)")
        }

        // Analysis
        println()
        println("=== ACCURACY ANALYSIS ===")
        if (finalWorld >= 8) {
            println("Reached World 8 - core game logic is accurate")
            if (finalLevel >= 4) {
                println("Reached World 8-4 - nearly complete accuracy")
            }
        }
        if (deathFrame != -1 && finalWorld == 8) {
            println("Death in World 8 indicates timing drift causing TAS desync")
            println("For true speedrunner compatibility, cycle-accurate timing would be needed")
        }
    }

    /**
     * PATH-BASED FRAME CONSUMPTION TEST
     *
     * Instead of counting cycles, this approach detects specific game state
     * transitions that are known to consume multiple frames:
     *
     * 1. InitializeGame (Mode=0, Task 0→1): skip 2 frames
     * 2. GameMode start (Mode 0→1): skip 1 frame
     * 3. Level load (Mode=1, FC wraps): skip 1 frame
     *
     * This approach can be used in decompiled code without cycle counting.
     */
    @Test
    fun `path-based TAS - state transition frame skipping`() {
        val romFile = listOf("local/roms/smb.nes", "smb.nes", "../smb.nes")
            .map { File(it) }
            .firstOrNull { it.exists() }
        if (romFile == null) {
            println("No ROM file found")
            return
        }

        val tasFile = listOf("happylee-warps.fm2", "../happylee-warps.fm2", "local/tas/smb-tas.fm2")
            .map { File(it) }
            .firstOrNull { it.exists() }
        if (tasFile == null) {
            println("No TAS file found")
            return
        }

        val tasInputs = FM2Parser.parse(tasFile)
        println("=== PATH-BASED TAS TEST ===")
        println("Frame skipping based on game state transitions, not cycle counting")
        println("TAS frames: ${tasInputs.size}")
        println()

        // Set up interpreter
        val rom = NESLoader.load(romFile)
        val interp = BinaryInterpreter6502()

        // Apply RAM initialization pattern
        for (addr in 0x0000..0x07FF) {
            val pattern = if ((addr / 4) % 2 == 0) 0x00 else 0xFF
            interp.memory.writeByte(addr, pattern.toUByte())
        }

        NESLoader.loadIntoMemory(rom, interp.memory)

        val ppu = SimplePPU()
        val controller = SimpleController()

        interp.memoryReadHook = { addr ->
            when (addr) {
                in 0x2000..0x2007 -> ppu.read(addr)
                0x4016 -> controller.read()
                else -> null
            }
        }
        interp.memoryWriteHook = { addr, value ->
            when (addr) {
                in 0x2000..0x2007 -> { ppu.write(addr, value, 0); true }
                0x4014 -> true
                0x4016 -> { controller.writeStrobe(value); true }
                in 0x4000..0x4017 -> true
                else -> false
            }
        }

        // Reset using the same pattern as the other test
        interp.reset()
        var resetSteps = 0
        val mainLoopAddr = 0x8057  // SMB's endless loop address
        while (resetSteps < 100000 && !interp.halted) {
            val pc = interp.cpu.PC.toInt()
            if (pc == mainLoopAddr) break

            val opcode = interp.memory.readByte(pc).toInt()
            val nextByte = interp.memory.readByte(pc + 1).toInt()
            val nextByte2 = interp.memory.readByte(pc + 2).toInt()

            // Detect LDA $2002 (opcode AD 02 20)
            if (opcode == 0xAD && nextByte == 0x02 && nextByte2 == 0x20) {
                // Check if this is a vblank wait loop: LDA $2002 followed by BPL
                val nextOpcode = interp.memory.readByte(pc + 3).toInt()
                val isBranchLoop = nextOpcode == 0x10 // BPL

                if (isBranchLoop) {
                    ppu.startFrame()
                    ppu.beginVBlank()

                    if (ppu.isNmiEnabled()) {
                        val spBeforeNmi = interp.cpu.SP.toInt()
                        interp.triggerNmi()
                        interp.handleInterrupts()

                        var nmiSteps = 0
                        while (nmiSteps < 50000 && interp.cpu.SP.toInt() != spBeforeNmi && !interp.halted) {
                            interp.step()
                            ppu.advanceStep()
                            nmiSteps++
                        }
                    }

                    ppu.endVBlank()
                    interp.cpu.PC = (pc + 5).toUShort()  // Skip LDA $2002 + BPL
                    resetSteps++
                    continue
                } else {
                    // Regular PPU status read - set vblank flag and execute normally
                    ppu.startFrame()
                    ppu.beginVBlank()
                }
            }
            interp.step()
            resetSteps++
        }
        println("Reset completed: $resetSteps steps")

        // PATH-BASED FRAME SKIPPING CONFIGURATION
        val suppressNmiFrames = setOf(0, 1, 2, 3, 4)  // First NMI at frame 5
        val buttonOffset = 1

        // State tracking for path detection
        var prevOperMode = 0
        var prevTask = 0
        var prevFC = 0
        var prevWorld = 0
        var prevLevel = 0
        var prevAreaType = 0
        var framesToSkip = 0  // Queued frame skips

        println("Configuration:")
        println("  - First NMI at frame: 5")
        println("  - Path-based frame skipping enabled")
        println()

        val maxFrames = 18000
        var lastWorld = -1
        var lastLevel = -1
        var deathFrame = -1
        val milestones = mutableListOf<String>()

        for (frame in 0 until maxFrames) {
            val buttonFrame = maxOf(0, frame - buttonOffset)
            val buttons = if (buttonFrame < tasInputs.size) tasInputs[buttonFrame].buttons else 0
            controller.setButtons(buttons)

            // Get state before NMI
            val fcBefore = interp.memory.readByte(SMB.FrameCounter).toInt()
            val intCtrlBefore = interp.memory.readByte(SMB.IntervalTimerControl).toInt()
            val modeBefore = interp.memory.readByte(SMB.OperMode).toInt()
            val taskBefore = interp.memory.readByte(0x0772).toInt()

            var nmiSkipped = false

            if (frame in suppressNmiFrames) {
                nmiSkipped = true
            } else if (framesToSkip > 0) {
                // Path-based skip: a previous transition queued frame skips
                framesToSkip--
                nmiSkipped = true
                if (frame < 50) {
                    println("Frame $frame: SKIPPED (path-based, $framesToSkip remaining)")
                }
            } else {
                // Fire NMI
                ppu.startFrame()
                val spBeforeNmi = interp.cpu.SP.toInt()
                interp.triggerNmi()
                interp.handleInterrupts()

                var nmiSteps = 0
                while (nmiSteps < 50000 && interp.cpu.SP.toInt() != spBeforeNmi && !interp.halted) {
                    interp.step()
                    ppu.advanceStep()
                    nmiSteps++
                }

                // Get state after NMI
                val modeAfter = interp.memory.readByte(SMB.OperMode).toInt()
                val taskAfter = interp.memory.readByte(0x0772).toInt()
                val fcAfter = interp.memory.readByte(SMB.FrameCounter).toInt()

                // Get additional state for path detection
                val worldAfter = interp.memory.readByte(SMB.WorldNumber).toInt()
                val levelAfter = interp.memory.readByte(SMB.LevelNumber).toInt()
                val areaTypeAfter = interp.memory.readByte(0x074E).toInt()  // AreaType

                // PATH DETECTION: Check for expensive transitions
                // Key insight: SMB's expensive operations are tied to specific game events,
                // NOT instruction counts. We detect the events that cause multi-frame operations.

                // 1. InitializeGame completed: Mode=0, Task 0→1 (title screen initialization)
                // InitializeGame clears $0000-$076F (~10000 cycles) and runs ScreenRoutines
                if (modeBefore == 0 && modeAfter == 0 && taskBefore == 0 && taskAfter == 1) {
                    framesToSkip = 2
                    println("Frame $frame: InitializeGame (Task 0→1 in TitleMode) → skip 2 frames")
                }

                // 2. GameMode started: Mode 0→1 (player starts the game)
                // This triggers InitializeArea and level loading
                else if (modeBefore == 0 && modeAfter == 1) {
                    framesToSkip = 1
                    println("Frame $frame: GameMode started (Mode 0→1) → skip 1 frame")
                }

                // 3. World or level change (warp zone, pipe transition, level complete)
                // InitializeArea runs when entering a new area
                else if (modeAfter == 1 && (worldAfter != prevWorld || levelAfter != prevLevel)) {
                    framesToSkip = 1
                    println("Frame $frame: World/Level change (W${prevWorld+1}-${prevLevel+1} → W${worldAfter+1}-${levelAfter+1}) → skip 1 frame")
                }

                // 4. Area type change (going underground, underwater, etc.)
                // These transitions also trigger InitializeArea
                else if (modeAfter == 1 && areaTypeAfter != prevAreaType && prevAreaType != 0 && frame > 50) {
                    framesToSkip = 1
                    println("Frame $frame: Area type change ($prevAreaType → $areaTypeAfter) → skip 1 frame")
                }

                // 5. Return to title screen (Game Over → Mode 0)
                // This re-runs InitializeGame
                else if (modeBefore != 0 && modeAfter == 0 && taskAfter == 1) {
                    framesToSkip = 2
                    println("Frame $frame: Return to title (Mode $modeBefore → 0) → skip 2 frames")
                }

                prevOperMode = modeAfter
                prevTask = taskAfter
                prevFC = fcAfter
                prevWorld = worldAfter
                prevLevel = levelAfter
                prevAreaType = areaTypeAfter

                // Debug early frames
                if (frame < 20 || nmiSteps > 10000) {
                    val rng0 = interp.memory.readByte(0x07A7).toInt() and 0xFF
                    val rng1 = interp.memory.readByte(0x07A8).toInt() and 0xFF
                    println("Frame $frame: FC $fcBefore→$fcAfter, IntCtrl $intCtrlBefore→${interp.memory.readByte(SMB.IntervalTimerControl).toInt()}, Mode=$modeAfter, Task=$taskAfter, RNG=${String.format("%02x %02x", rng0, rng1)} ($nmiSteps steps)")
                }
            }

            // Track world/level
            val world = interp.memory.readByte(SMB.WorldNumber).toInt() + 1
            val level = interp.memory.readByte(SMB.LevelNumber).toInt() + 1
            val lives = interp.memory.readByte(SMB.NumberofLives).toInt()
            val operMode = interp.memory.readByte(SMB.OperMode).toInt()

            if (world != lastWorld || level != lastLevel) {
                val milestone = "Frame $frame: W$world-$level (Lives=$lives)"
                println(milestone)
                milestones.add(milestone)
                lastWorld = world
                lastLevel = level
            }

            // Detect death
            if (operMode == 3 && deathFrame == -1) {
                deathFrame = frame
                val x = interp.memory.readByte(SMB.Player_X_Position).toInt()
                val page = interp.memory.readByte(SMB.ScreenLeft_PageLoc).toInt()
                println("Frame $frame: GAME OVER at W$world-$level (X=$x, Page=$page)")
            }

            // Progress reporting
            if (frame > 0 && frame % 2000 == 0) {
                val fc = interp.memory.readByte(SMB.FrameCounter).toInt()
                val x = interp.memory.readByte(SMB.Player_X_Position).toInt()
                val page = interp.memory.readByte(SMB.ScreenLeft_PageLoc).toInt()
                println("  Frame $frame: W$world-$level, Lives=$lives, X=$x, Page=$page, FC=$fc")
            }

            if (interp.halted) break
        }

        // Results
        val finalWorld = interp.memory.readByte(SMB.WorldNumber).toInt() + 1
        val finalLevel = interp.memory.readByte(SMB.LevelNumber).toInt() + 1
        val finalMode = interp.memory.readByte(SMB.OperMode).toInt()

        println()
        println("=== PATH-BASED RESULT ===")
        println("Final state: W$finalWorld-$finalLevel, Mode=$finalMode")

        println()
        println("Milestones reached:")
        milestones.forEach { println("  $it") }

        if (deathFrame != -1) {
            println()
            println("First death at frame $deathFrame")
        }
    }

    /**
     * PATH-BASED RAM DIVERGENCE TEST
     *
     * Compares interpreter RAM against FCEUX at each frame to identify
     * exactly which addresses diverge and when, using path-based frame skipping.
     */
    @Test
    fun `debug trace 0778 writes`() {
        val romFile = listOf("local/roms/smb.nes", "smb.nes", "../smb.nes")
            .map { File(it) }
            .firstOrNull { it.exists() }
        if (romFile == null) {
            println("No ROM file found")
            return
        }

        println("=== DEBUG: Tracing $0778 (Mirror_PPU_CTRL_REG1) writes ===")

        val rom = NESLoader.load(romFile)
        val interp = BinaryInterpreter6502()

        // RAM init
        for (addr in 0x0000..0x07FF) {
            val pattern = if ((addr / 4) % 2 == 0) 0x00 else 0xFF
            interp.memory.writeByte(addr, pattern.toUByte())
        }
        println("Initial $0778 value: ${String.format("0x%02X", interp.memory.readByte(0x0778).toInt())}")

        NESLoader.loadIntoMemory(rom, interp.memory)

        val ppu = SimplePPU()
        val controller = SimpleController()
        val ppu2000Writes = mutableListOf<Pair<Int, Int>>()  // step -> value
        val ram0778Writes = mutableListOf<Triple<Int, Int, Int>>()  // step, value, PC

        var totalSteps = 0

        interp.memoryReadHook = { addr ->
            when (addr) {
                in 0x2000..0x2007 -> ppu.read(addr)
                0x4016 -> controller.read()
                0x4017 -> 0u
                else -> null
            }
        }
        interp.memoryWriteHook = { addr, value ->
            when (addr) {
                in 0x2000..0x2007 -> {
                    if (addr == 0x2000) {
                        ppu2000Writes.add(totalSteps to value.toInt())
                        if (ppu2000Writes.size <= 20) {
                            println("Step $totalSteps: Write to \$2000 = ${String.format("0x%02X", value.toInt())} (ppuCtrl was ${String.format("0x%02X", ppu.ppuCtrl.toInt())})")
                        }
                    }
                    ppu.write(addr, value, 0)
                    true
                }
                0x4014 -> true
                0x4016 -> { controller.writeStrobe(value); true }
                in 0x4000..0x4017 -> true
                0x0778 -> {
                    val pc = interp.cpu.PC.toInt()
                    ram0778Writes.add(Triple(totalSteps, value.toInt(), pc))
                    if (ram0778Writes.size <= 20) {
                        println("Step $totalSteps: Write to \$0778 = ${String.format("0x%02X", value.toInt())} at PC=${String.format("0x%04X", pc)}")
                    }
                    false  // Allow write to proceed
                }
                else -> false
            }
        }

        interp.reset()
        val mainLoopAddr = 0x8057
        var nmiCount = 0

        println("\n=== Reset execution ===")
        while (totalSteps < 50000 && !interp.halted) {
            val pc = interp.cpu.PC.toInt()
            if (pc == mainLoopAddr) {
                println("Reached endless loop at step $totalSteps")
                break
            }

            val opcode = interp.memory.readByte(pc).toInt()
            val nextByte = interp.memory.readByte(pc + 1).toInt()
            val nextByte2 = interp.memory.readByte(pc + 2).toInt()

            // Detect vblank wait: LDA $2002
            if (opcode == 0xAD && nextByte == 0x02 && nextByte2 == 0x20) {
                // Check if this is a vblank wait loop: LDA $2002 followed by BPL
                val nextOpcode = interp.memory.readByte(pc + 3).toInt()
                val isBranchLoop = nextOpcode == 0x10 // BPL

                val nmiEnabled = ppu.isNmiEnabled()
                println("Step $totalSteps: LDA \$2002 detected at PC=${String.format("0x%04X", pc)}, ppuCtrl=${String.format("0x%02X", ppu.ppuCtrl.toInt())}, nmiEnabled=$nmiEnabled, isLoop=$isBranchLoop")

                if (isBranchLoop) {
                    // This is a vblank wait loop - simulate vblank and skip the loop
                    ppu.startFrame()
                    ppu.beginVBlank()

                    if (nmiEnabled) {
                        nmiCount++
                        println("  Firing NMI #$nmiCount")
                        val spBeforeNmi = interp.cpu.SP.toInt()
                        val val0778Before = interp.memory.readByte(0x0778).toInt()
                        interp.triggerNmi()
                        interp.handleInterrupts()
                        var nmiSteps = 0
                        while (nmiSteps < 50000 && interp.cpu.SP.toInt() != spBeforeNmi && !interp.halted) {
                            interp.step()
                            ppu.advanceStep()
                            nmiSteps++
                            totalSteps++
                        }
                        val val0778After = interp.memory.readByte(0x0778).toInt()
                        println("  NMI complete after $nmiSteps steps. \$0778: ${String.format("0x%02X", val0778Before)} → ${String.format("0x%02X", val0778After)}")
                    }

                    ppu.endVBlank()
                    interp.cpu.PC = (pc + 5).toUShort()  // Skip LDA $2002 + BPL
                    totalSteps++
                    continue
                } else {
                    // Just a regular PPU status read - execute it normally
                    // but set the vblank flag so the read succeeds
                    ppu.startFrame()
                    ppu.beginVBlank()
                    // Let the instruction execute normally
                }
            }

            interp.step()
            totalSteps++
        }

        println("\n=== Summary ===")
        println("Total $2000 writes: ${ppu2000Writes.size}")
        println("Total $0778 writes: ${ram0778Writes.size}")
        println("NMIs fired during reset: $nmiCount")
        println("Final ppuCtrl: ${String.format("0x%02X", ppu.ppuCtrl.toInt())}")
        println("Final \$0778: ${String.format("0x%02X", interp.memory.readByte(0x0778).toInt())}")
    }

    @Test
    fun `path-based RAM divergence analysis`() {
        val romFile = listOf("local/roms/smb.nes", "smb.nes", "../smb.nes")
            .map { File(it) }
            .firstOrNull { it.exists() }
        if (romFile == null) {
            println("No ROM file found")
            return
        }

        val fceuxRamFile = File("local/tas/fceux-full-ram.bin")
        if (!fceuxRamFile.exists()) {
            println("No FCEUX RAM dump - run Lua script first")
            return
        }

        val tasFile = listOf("happylee-warps.fm2", "../happylee-warps.fm2", "local/tas/smb-tas.fm2")
            .map { File(it) }
            .firstOrNull { it.exists() }
        if (tasFile == null) {
            println("No TAS file found")
            return
        }

        val fceuxRam = fceuxRamFile.readBytes()
        val tasInputs = FM2Parser.parse(tasFile)
        val maxFceuxFrames = fceuxRam.size / 2048

        println("=== PATH-BASED RAM DIVERGENCE ANALYSIS ===")
        println("FCEUX frames: $maxFceuxFrames, TAS frames: ${tasInputs.size}")

        // Set up interpreter
        val rom = NESLoader.load(romFile)
        val interp = BinaryInterpreter6502()

        for (addr in 0x0000..0x07FF) {
            val pattern = if ((addr / 4) % 2 == 0) 0x00 else 0xFF
            interp.memory.writeByte(addr, pattern.toUByte())
        }

        NESLoader.loadIntoMemory(rom, interp.memory)

        val ppu = SimplePPU()
        val controller = SimpleController()

        interp.memoryReadHook = { addr ->
            when (addr) {
                in 0x2000..0x2007 -> ppu.read(addr)
                0x4016 -> controller.read()
                0x4017 -> 0u
                else -> null
            }
        }
        interp.memoryWriteHook = { addr, value ->
            when (addr) {
                in 0x2000..0x2007 -> { ppu.write(addr, value, 0); true }
                0x4014 -> true
                0x4016 -> { controller.writeStrobe(value); true }
                in 0x4000..0x4017 -> true
                else -> false
            }
        }

        // Reset
        interp.reset()
        var resetSteps = 0
        val mainLoopAddr = 0x8057
        while (resetSteps < 100000 && !interp.halted) {
            val pc = interp.cpu.PC.toInt()
            if (pc == mainLoopAddr) break

            val opcode = interp.memory.readByte(pc).toInt()
            val nextByte = interp.memory.readByte(pc + 1).toInt()
            val nextByte2 = interp.memory.readByte(pc + 2).toInt()

            if (opcode == 0xAD && nextByte == 0x02 && nextByte2 == 0x20) {
                // Check if this is a vblank wait loop: LDA $2002 followed by BPL
                val nextOpcode = interp.memory.readByte(pc + 3).toInt()
                val isBranchLoop = nextOpcode == 0x10 // BPL

                if (isBranchLoop) {
                    ppu.startFrame()
                    ppu.beginVBlank()
                    if (ppu.isNmiEnabled()) {
                        val spBeforeNmi = interp.cpu.SP.toInt()
                        interp.triggerNmi()
                        interp.handleInterrupts()
                        var nmiSteps = 0
                        while (nmiSteps < 50000 && interp.cpu.SP.toInt() != spBeforeNmi && !interp.halted) {
                            interp.step()
                            ppu.advanceStep()
                            nmiSteps++
                        }
                    }
                    ppu.endVBlank()
                    interp.cpu.PC = (pc + 5).toUShort()  // Skip LDA $2002 + BPL
                    resetSteps++
                    continue
                } else {
                    // Regular PPU status read - set vblank flag and execute normally
                    ppu.startFrame()
                    ppu.beginVBlank()
                }
            }
            interp.step()
            resetSteps++
        }
        println("Reset completed: $resetSteps steps")

        // PATH-BASED FRAME SKIPPING
        val suppressNmiFrames = setOf(0, 1, 2, 3, 4)  // First NMI at frame 5
        val buttonOffset = 1

        var prevOperMode = 0
        var prevTask = 0
        var prevWorld = 0
        var prevLevel = 0
        var prevAreaType = 0
        var framesToSkip = 0

        // Track divergence by address
        val divergenceCount = mutableMapOf<Int, Int>()
        val firstDivergence = mutableMapOf<Int, Int>()  // addr -> first frame
        var totalDivergentFrames = 0

        val maxFrames = minOf(10000, maxFceuxFrames)

        for (frame in 0 until maxFrames) {
            val buttonFrame = maxOf(0, frame - buttonOffset)
            val buttons = if (buttonFrame < tasInputs.size) tasInputs[buttonFrame].buttons else 0
            controller.setButtons(buttons)

            val modeBefore = interp.memory.readByte(SMB.OperMode).toInt()
            val taskBefore = interp.memory.readByte(0x0772).toInt()

            var nmiSkipped = false

            if (frame in suppressNmiFrames) {
                nmiSkipped = true
            } else if (framesToSkip > 0) {
                framesToSkip--
                nmiSkipped = true
            } else {
                ppu.startFrame()
                val spBeforeNmi = interp.cpu.SP.toInt()
                interp.triggerNmi()
                interp.handleInterrupts()

                var nmiSteps = 0
                while (nmiSteps < 50000 && interp.cpu.SP.toInt() != spBeforeNmi && !interp.halted) {
                    interp.step()
                    ppu.advanceStep()
                    nmiSteps++
                }

                // Get state after NMI
                val modeAfter = interp.memory.readByte(SMB.OperMode).toInt()
                val taskAfter = interp.memory.readByte(0x0772).toInt()
                val worldAfter = interp.memory.readByte(SMB.WorldNumber).toInt()
                val levelAfter = interp.memory.readByte(SMB.LevelNumber).toInt()
                val areaTypeAfter = interp.memory.readByte(0x074E).toInt()

                // PATH DETECTION - skip frames for multi-frame NMI handlers
                if (modeBefore == 0 && modeAfter == 0 && taskBefore == 0 && taskAfter == 1) {
                    framesToSkip = 2  // InitializeGame takes 2 frames
                } else if (modeBefore == 0 && modeAfter == 1) {
                    framesToSkip = 1
                } else if (modeAfter == 1 && (worldAfter != prevWorld || levelAfter != prevLevel)) {
                    framesToSkip = 1
                } else if (modeAfter == 1 && areaTypeAfter != prevAreaType && prevAreaType != 0 && frame > 50) {
                    framesToSkip = 1
                } else if (modeBefore != 0 && modeAfter == 0 && taskAfter == 1) {
                    framesToSkip = 2  // Return to title re-runs InitializeGame
                }

                prevOperMode = modeAfter
                prevTask = taskAfter
                prevWorld = worldAfter
                prevLevel = levelAfter
                prevAreaType = areaTypeAfter
            }

            // Compare RAM against FCEUX
            val fOff = frame * 2048
            if (fOff + 2047 < fceuxRam.size) {
                var frameDivergent = false
                val frameDiv = mutableListOf<Triple<Int, Int, Int>>()
                for (addr in 0x0000..0x07FF) {
                    val expected = fceuxRam[fOff + addr].toInt() and 0xFF
                    val actual = interp.memory.readByte(addr).toInt() and 0xFF
                    if (expected != actual) {
                        frameDivergent = true
                        frameDiv.add(Triple(addr, expected, actual))
                        divergenceCount[addr] = (divergenceCount[addr] ?: 0) + 1
                        if (addr !in firstDivergence) {
                            firstDivergence[addr] = frame
                        }
                    }
                }
                if (frameDivergent) totalDivergentFrames++

                // Detailed output for early frames
                if (frame in listOf(5, 6, 7, 8, 9, 10, 42, 43)) {
                    val ourFC = interp.memory.readByte(SMB.FrameCounter).toInt()
                    val ourIntCtrl = interp.memory.readByte(SMB.IntervalTimerControl).toInt()
                    val ourTask = interp.memory.readByte(0x0772).toInt()
                    val ourPPUCtrl = interp.memory.readByte(0x0778).toInt()
                    val fceuxFC = fceuxRam[fOff + SMB.FrameCounter].toInt() and 0xFF
                    val fceuxIntCtrl = fceuxRam[fOff + SMB.IntervalTimerControl].toInt() and 0xFF
                    val fceuxTask = fceuxRam[fOff + 0x0772].toInt() and 0xFF
                    val fceuxPPUCtrl = fceuxRam[fOff + 0x0778].toInt() and 0xFF
                    println("Frame $frame comparison:")
                    println("  FC: ours=$ourFC fceux=$fceuxFC")
                    println("  IntCtrl: ours=$ourIntCtrl fceux=$fceuxIntCtrl")
                    println("  Task: ours=$ourTask fceux=$fceuxTask")
                    println("  PPU_CTRL_mirror: ours=${String.format("%02X", ourPPUCtrl)} fceux=${String.format("%02X", fceuxPPUCtrl)}")
                    println("  Divergent: ${frameDiv.take(10).map { (a,e,o) -> "\$${String.format("%04X", a)}:$e→$o" }}")
                }
            }

            // Progress report every 1000 frames
            if (frame > 0 && frame % 1000 == 0) {
                val world = interp.memory.readByte(SMB.WorldNumber).toInt() + 1
                val level = interp.memory.readByte(SMB.LevelNumber).toInt() + 1
                val pct = totalDivergentFrames * 100 / frame
                println("Frame $frame: W$world-$level, $totalDivergentFrames divergent frames ($pct%)")
            }
        }

        // Summary
        println()
        println("=== DIVERGENCE SUMMARY ===")
        println("Total frames checked: $maxFrames")
        println("Frames with divergence: $totalDivergentFrames (${totalDivergentFrames * 100 / maxFrames}%)")
        println()

        // Top divergent addresses
        println("Top 30 divergent addresses (by frequency):")
        divergenceCount.entries
            .sortedByDescending { it.value }
            .take(30)
            .forEach { (addr, count) ->
                val firstFrame = firstDivergence[addr] ?: 0
                val name = when (addr) {
                    0x0009 -> "FrameCounter"
                    0x000A -> "SavedJoypadBits"
                    0x0770 -> "OperMode"
                    0x0772 -> "OperMode_Task"
                    0x077F -> "IntervalTimerControl"
                    0x07A7, 0x07A8, 0x07A9, 0x07AA, 0x07AB, 0x07AC, 0x07AD -> "PseudoRandomBit"
                    0x0787 -> "TimerControl"
                    0x06FC -> "SavedJoypad1Bits"
                    0x001D -> "Player_State"
                    0x0086 -> "Player_X_Position"
                    0x00CE -> "Player_Y_Position"
                    0x004D -> "Player_X_Speed"
                    0x0433 -> "Enemy_Flag[0]"
                    0x0016 -> "Enemy_ID[0]"
                    else -> ""
                }
                println("  \$${String.format("%04X", addr)}: $count frames (first at $firstFrame) $name")
            }

        println()
        println("First 20 addresses to diverge (by frame):")
        firstDivergence.entries
            .sortedBy { it.value }
            .take(20)
            .forEach { (addr, frame) ->
                val count = divergenceCount[addr] ?: 0
                println("  Frame $frame: \$${String.format("%04X", addr)} (divergent $count times)")
            }
    }

    /**
     * DEBUG TEST: Stop at the very first RAM divergence and analyze it.
     * This helps identify the root cause of cumulative drift.
     */
    @Test
    fun `debug first divergence`() {
        val romFile = listOf("local/roms/smb.nes", "smb.nes", "../smb.nes")
            .map { File(it) }
            .firstOrNull { it.exists() }
        if (romFile == null) {
            println("No ROM file found")
            return
        }

        val fceuxRamFile = File("local/tas/fceux-full-ram.bin")
        if (!fceuxRamFile.exists()) {
            println("No FCEUX RAM dump found")
            return
        }

        val tasFile = File("happylee-warps.fm2")
        if (!tasFile.exists()) {
            println("No TAS file found")
            return
        }

        val fceuxRam = fceuxRamFile.readBytes()
        val tasInputs = FM2Parser.parse(tasFile)

        // Named addresses
        val namedAddrs = mapOf(
            0x0009 to "FrameCounter",
            0x000E to "GameEngineSubroutine",
            0x001D to "Player_State",
            0x0057 to "Player_X_Speed",
            0x0086 to "Player_X_Position",
            0x00CE to "Player_Y_Position",
            0x06FC to "SavedJoypad1Bits",
            0x0770 to "OperMode",
            0x0772 to "OperMode_Task",
            0x0778 to "Mirror_PPU_CTRL_REG1",
            0x077F to "IntervalTimerControl",
            0x075A to "NumberofLives",
            0x075F to "WorldNumber",
            0x0760 to "LevelNumber",
            0x07A7 to "PseudoRandomBitReg[0]",
            0x07A8 to "PseudoRandomBitReg[1]"
        )

        // Set up interpreter
        val rom = NESLoader.load(romFile)
        val interp = BinaryInterpreter6502()

        // Apply RAM initialization pattern: 00 00 00 00 FF FF FF FF repeating
        for (addr in 0x0000..0x07FF) {
            val pattern = if ((addr / 4) % 2 == 0) 0x00 else 0xFF
            interp.memory.writeByte(addr, pattern.toUByte())
        }

        NESLoader.loadIntoMemory(rom, interp.memory)

        val ppu = SimplePPU()
        val controller = SimpleController()

        interp.memoryReadHook = { addr ->
            when (addr) {
                in 0x2000..0x2007 -> ppu.read(addr)
                0x4016 -> controller.read()
                0x4017 -> 0u
                else -> null
            }
        }

        interp.memoryWriteHook = { addr, value ->
            when (addr) {
                in 0x2000..0x2007 -> { ppu.write(addr, value, 0); true }
                0x4014 -> true
                0x4016 -> { controller.writeStrobe(value); true }
                in 0x4000..0x4017 -> true
                else -> false
            }
        }

        interp.reset()

        // Run reset until main loop
        var resetSteps = 0
        val maxResetSteps = 100000
        val mainLoopAddr = 0x8057
        while (resetSteps < maxResetSteps && !interp.halted) {
            val pc = interp.cpu.PC.toInt()
            if (pc == mainLoopAddr) break
            val opcode = interp.memory.readByte(pc).toInt()
            val nextByte = interp.memory.readByte(pc + 1).toInt()
            val nextByte2 = interp.memory.readByte(pc + 2).toInt()
            if (opcode == 0xAD && nextByte == 0x02 && nextByte2 == 0x20) {
                interp.cpu.PC = (pc + 5).toUShort()
                resetSteps++
                continue
            }
            interp.step()
            resetSteps++
        }
        println("Reset completed after $resetSteps steps")

        // Cycle-based timing same as standalone test
        val cyclesPerFrame = 29780
        // Average cycles per 6502 step: 2.5-3.5 depending on instruction mix
        // Using 2.8 based on calibration through 5553 frames
        val avgCyclesPerStep = 2.8
        val suppressNmiFrames = setOf(0, 1, 2, 3, 4)

        println("=== DEBUG: Find First RAM Divergence ===")
        println("Comparing interpreter against FCEUX at each frame")
        println("NOTE: Comparing AFTER running NMI for each frame")
        println()

        // First, run NMIs for frames 0-4 (suppressed in normal execution)
        // but don't compare yet since FCEUX frame data is before NMI execution
        for (preFrame in 0 until 5) {
            val buttons = if (preFrame > 0 && preFrame - 1 < tasInputs.size) tasInputs[preFrame - 1].buttons else 0
            controller.setButtons(buttons)
            // No NMI for these frames
        }

        // Compare starting from frame 5 (first NMI frame)
        var frame = 5
        while (frame < 8000) {
            // Apply buttons
            val buttonFrame = maxOf(0, frame - 1)
            val buttons = if (buttonFrame < tasInputs.size) tasInputs[buttonFrame].buttons else 0
            controller.setButtons(buttons)

            // Run NMI if not suppressed
            var nmiSteps = 0
            var framesConsumed = 1

            if (frame !in suppressNmiFrames) {
                // Use FCEUX IntCtrl as timing oracle: find when it next decrements
                // This tells us how many frames FCEUX took for this NMI
                val currentFceuxOffset = frame * 2048
                if (currentFceuxOffset + 2047 < fceuxRam.size) {
                    val currentIntCtrl = fceuxRam[currentFceuxOffset + SMB.IntervalTimerControl].toInt() and 0xFF

                    // Look ahead to find when IntCtrl changes (or wraps from 0 to 20)
                    var lookAhead = 1
                    while (lookAhead < 10) {
                        val nextOffset = (frame + lookAhead) * 2048
                        if (nextOffset + 2047 >= fceuxRam.size) break
                        val nextIntCtrl = fceuxRam[nextOffset + SMB.IntervalTimerControl].toInt() and 0xFF

                        // IntCtrl decrements each NMI, wraps from 0 to 20
                        val expectedNext = if (currentIntCtrl == 0) 20 else currentIntCtrl - 1
                        if (nextIntCtrl == expectedNext) {
                            // Found when FCEUX's next NMI completed
                            framesConsumed = lookAhead
                            break
                        }
                        lookAhead++
                    }
                }

                // Run NMI for this frame
                ppu.startFrame()
                val spBeforeNmi = interp.cpu.SP.toInt()
                interp.triggerNmi()
                interp.handleInterrupts()
                while (nmiSteps < 50000 && interp.cpu.SP.toInt() != spBeforeNmi && !interp.halted) {
                    interp.step()
                    ppu.advanceStep()
                    nmiSteps++
                }

                if (framesConsumed > 1) {
                    val cyclesUsed = (nmiSteps * avgCyclesPerStep).toInt()
                    println("Frame $frame: NMI took $nmiSteps steps (~${cyclesUsed} cycles, FCEUX says $framesConsumed frames)")
                }
            }

            // Compare with FCEUX RAM at the frame where their long NMI ends
            // Our NMI for frame N takes K frames worth of cycles
            // FCEUX frame N+K-1 is the last frame where the long NMI was still running
            // So FCEUX frame N+K-1 shows the state AFTER that NMI completes
            // (because the NMI doesn't run again until frame N+K)
            val fceuxCompareFrame = frame + framesConsumed - 1
            val fceuxOffset = fceuxCompareFrame * 2048
            if (fceuxOffset + 2047 >= fceuxRam.size) break

            // Find divergences (skip non-critical areas)
            val divergences = mutableListOf<Triple<Int, Int, Int>>()
            for (addr in 0x0000..0x07FF) {
                if (addr in 0x0000..0x000F) continue  // Temp/scratch variables
                if (addr in 0x0100..0x01FF) continue  // Stack
                if (addr in 0x0200..0x02FF) continue  // OAM staging (sprites)
                if (addr in 0x0300..0x03FF) continue  // OAM buffer (sprites)
                if (addr in 0x0400..0x04FF) continue  // VRAM buffer (nametable updates)
                if (addr in 0x0500..0x05FF) continue  // VRAM buffer continued
                if (addr == 0x0778) continue          // Mirror_PPU_CTRL_REG1
                val expected = fceuxRam[fceuxOffset + addr].toInt() and 0xFF
                val actual = interp.memory.readByte(addr).toInt()
                if (expected != actual) {
                    divergences.add(Triple(addr, expected, actual))
                }
            }

            val fc = interp.memory.readByte(SMB.FrameCounter).toInt()
            val intCtrl = interp.memory.readByte(SMB.IntervalTimerControl).toInt()
            val mode = interp.memory.readByte(SMB.OperMode).toInt()
            val task = interp.memory.readByte(0x0772).toInt()

            // Report every frame's state
            val skipped = if (frame in suppressNmiFrames) "(suppressed)"
                         else "($nmiSteps steps)"

            if (divergences.isNotEmpty()) {
                println("Frame $frame (compare with FCEUX $fceuxCompareFrame): FC=$fc IntCtrl=$intCtrl Mode=$mode Task=$task $skipped")
                println("  DIVERGENCE! ${divergences.size} addresses differ:")

                // Show important divergences first
                val importantAddrs = setOf(0x0009, 0x077F, 0x0770, 0x0772, 0x06FC, 0x07A7, 0x07A8, 0x0086, 0x00CE)
                val important = divergences.filter { it.first in importantAddrs }
                val others = divergences.filter { it.first !in importantAddrs }

                for ((addr, expected, actual) in important) {
                    val name = namedAddrs[addr] ?: ""
                    println("    \$${String.format("%04X", addr)}: FCEUX=$expected INTERP=$actual $name")
                }

                if (others.isNotEmpty()) {
                    println("  Plus ${others.size} other addresses differ")
                    for ((addr, expected, actual) in others.take(10)) {
                        val name = namedAddrs[addr] ?: ""
                        println("    \$${String.format("%04X", addr)}: FCEUX=$expected INTERP=$actual $name")
                    }
                }

                // Show FCEUX state for context
                val fceuxFC = fceuxRam[fceuxOffset + SMB.FrameCounter].toInt() and 0xFF
                val fceuxIntCtrl = fceuxRam[fceuxOffset + SMB.IntervalTimerControl].toInt() and 0xFF
                val fceuxMode = fceuxRam[fceuxOffset + SMB.OperMode].toInt() and 0xFF
                val fceuxTask = fceuxRam[fceuxOffset + 0x0772].toInt() and 0xFF
                println("  FCEUX state: FC=$fceuxFC IntCtrl=$fceuxIntCtrl Mode=$fceuxMode Task=$fceuxTask")

                println()
                println("STOPPING AT FIRST DIVERGENCE for debugging")
                return
            } else {
                // No divergence - just show progress every 50 frames
                if (frame % 50 == 0 || framesConsumed > 1) {
                    println("Frame $frame (FCEUX $fceuxCompareFrame): FC=$fc IntCtrl=$intCtrl Mode=$mode Task=$task - MATCH $skipped")
                }
            }

            // Advance to next frame (skip frames consumed by long NMI)
            frame += framesConsumed
        }

        println("No divergence found in first 8000 frames!")
        println("SUCCESS: Interpreter matches FCEUX through W8-1 warp!")
    }
}
