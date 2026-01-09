@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.smb

import com.ivieleague.decompiler6502tokotlin.hand.*
import com.ivieleague.decompiler6502tokotlin.interpreter.*
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Frame-by-frame comparison between the interpreter and decompiled versions.
 *
 * This test runs both versions with the same TAS input and compares RAM snapshots
 * at each frame to identify the first point of divergence.
 */
class InterpreterDecompiledComparisonTest {

    // SMB memory addresses of interest
    object Addresses {
        const val OperMode = 0x0770
        const val OperMode_Task = 0x0772
        const val FrameCounter = 0x09
        const val IntervalTimerControl = 0x077F
        const val WorldNumber = 0x075F
        const val LevelNumber = 0x0760
        const val Player_X_Position = 0x0086
        const val Player_Y_Position = 0x00CE
        const val Player_X_Speed = 0x0057
        const val Player_PageLoc = 0x006D
        const val Player_State = 0x001D
        const val GameEngineSubroutine = 0x000E
        const val SavedJoypadBits = 0x06FC
        const val JoypadOverride = 0x0717
        const val NumberofLives = 0x075A
    }

    /**
     * Compare critical game state variables between interpreter and decompiled versions.
     */
    data class CriticalState(
        val operMode: Int,
        val operModeTask: Int,
        val frameCounter: Int,
        val worldNumber: Int,
        val levelNumber: Int,
        val playerX: Int,
        val playerY: Int,
        val playerXSpeed: Int,
        val playerPage: Int,
        val playerState: Int,
        val gameEngSub: Int,
        val savedJoypad: Int,
        val joypadOverride: Int,
        val intervalTimerControl: Int
    ) {
        companion object {
            fun fromInterpreter(interp: BinaryInterpreter6502): CriticalState {
                return CriticalState(
                    operMode = interp.memory.readByte(Addresses.OperMode).toInt(),
                    operModeTask = interp.memory.readByte(Addresses.OperMode_Task).toInt(),
                    frameCounter = interp.memory.readByte(Addresses.FrameCounter).toInt(),
                    worldNumber = interp.memory.readByte(Addresses.WorldNumber).toInt() + 1,
                    levelNumber = interp.memory.readByte(Addresses.LevelNumber).toInt() + 1,
                    playerX = interp.memory.readByte(Addresses.Player_X_Position).toInt(),
                    playerY = interp.memory.readByte(Addresses.Player_Y_Position).toInt(),
                    playerXSpeed = interp.memory.readByte(Addresses.Player_X_Speed).toInt(),
                    playerPage = interp.memory.readByte(Addresses.Player_PageLoc).toInt(),
                    playerState = interp.memory.readByte(Addresses.Player_State).toInt(),
                    gameEngSub = interp.memory.readByte(Addresses.GameEngineSubroutine).toInt(),
                    savedJoypad = interp.memory.readByte(Addresses.SavedJoypadBits).toInt(),
                    joypadOverride = interp.memory.readByte(Addresses.JoypadOverride).toInt(),
                    intervalTimerControl = interp.memory.readByte(Addresses.IntervalTimerControl).toInt()
                )
            }

            fun fromDecompiled(): CriticalState {
                return CriticalState(
                    operMode = memory[Addresses.OperMode].toInt(),
                    operModeTask = memory[Addresses.OperMode_Task].toInt(),
                    frameCounter = memory[Addresses.FrameCounter].toInt(),
                    worldNumber = memory[Addresses.WorldNumber].toInt() + 1,
                    levelNumber = memory[Addresses.LevelNumber].toInt() + 1,
                    playerX = memory[Addresses.Player_X_Position].toInt(),
                    playerY = memory[Addresses.Player_Y_Position].toInt(),
                    playerXSpeed = memory[Addresses.Player_X_Speed].toInt(),
                    playerPage = memory[Addresses.Player_PageLoc].toInt(),
                    playerState = memory[Addresses.Player_State].toInt(),
                    gameEngSub = memory[Addresses.GameEngineSubroutine].toInt(),
                    savedJoypad = memory[Addresses.SavedJoypadBits].toInt(),
                    joypadOverride = memory[Addresses.JoypadOverride].toInt(),
                    intervalTimerControl = memory[Addresses.IntervalTimerControl].toInt()
                )
            }
        }

        fun diff(other: CriticalState): List<String> {
            val diffs = mutableListOf<String>()
            if (operMode != other.operMode) diffs.add("OperMode: $operMode vs ${other.operMode}")
            if (operModeTask != other.operModeTask) diffs.add("OperModeTask: $operModeTask vs ${other.operModeTask}")
            if (frameCounter != other.frameCounter) diffs.add("FrameCounter: $frameCounter vs ${other.frameCounter}")
            if (worldNumber != other.worldNumber) diffs.add("World: $worldNumber vs ${other.worldNumber}")
            if (levelNumber != other.levelNumber) diffs.add("Level: $levelNumber vs ${other.levelNumber}")
            if (playerX != other.playerX) diffs.add("PlayerX: $playerX vs ${other.playerX}")
            if (playerY != other.playerY) diffs.add("PlayerY: $playerY vs ${other.playerY}")
            if (playerXSpeed != other.playerXSpeed) diffs.add("XSpeed: $playerXSpeed vs ${other.playerXSpeed}")
            if (playerPage != other.playerPage) diffs.add("PlayerPage: $playerPage vs ${other.playerPage}")
            if (playerState != other.playerState) diffs.add("PlayerState: $playerState vs ${other.playerState}")
            if (gameEngSub != other.gameEngSub) diffs.add("GameEngSub: $gameEngSub vs ${other.gameEngSub}")
            if (savedJoypad != other.savedJoypad) diffs.add("SavedJoypad: ${savedJoypad.toString(16)} vs ${other.savedJoypad.toString(16)}")
            if (joypadOverride != other.joypadOverride) diffs.add("JoypadOverride: $joypadOverride vs ${other.joypadOverride}")
            if (intervalTimerControl != other.intervalTimerControl) diffs.add("IntCtrl: $intervalTimerControl vs ${other.intervalTimerControl}")
            return diffs
        }

        override fun toString(): String {
            return "Mode=$operMode Task=$operModeTask FC=$frameCounter W$worldNumber-$levelNumber " +
                   "X=$playerX Y=$playerY XSpd=$playerXSpeed Page=$playerPage State=$playerState " +
                   "GameEng=$gameEngSub Joy=${savedJoypad.toString(16)} IntCtrl=$intervalTimerControl"
        }
    }

    /**
     * Full RAM snapshot comparison result.
     */
    data class RamDifference(
        val address: Int,
        val interpreterValue: Int,
        val decompiledValue: Int
    ) {
        override fun toString(): String =
            "[$${address.toString(16).uppercase().padStart(4, '0')}]: interp=${interpreterValue.toString(16).padStart(2, '0')} decomp=${decompiledValue.toString(16).padStart(2, '0')}"
    }

    /**
     * Compare all RAM bytes between interpreter and decompiled.
     * Returns list of differences (empty if identical).
     */
    fun compareRam(interp: BinaryInterpreter6502, range: IntRange = 0x0000..0x07FF): List<RamDifference> {
        val diffs = mutableListOf<RamDifference>()
        for (addr in range) {
            val interpVal = interp.memory.readByte(addr).toInt()
            val decompVal = memory[addr].toInt()
            if (interpVal != decompVal) {
                diffs.add(RamDifference(addr, interpVal, decompVal))
            }
        }
        return diffs
    }

    /**
     * Set up the interpreter with PPU and controller stubs.
     * Returns the configured interpreter ready for frame execution.
     */
    fun setupInterpreter(): BinaryInterpreter6502? {
        val romPaths = listOf(
            "local/roms/smb.nes",
            "../local/roms/smb.nes",
            "../../local/roms/smb.nes"
        )
        val romFile = romPaths.map { File(it) }.firstOrNull { it.exists() } ?: return null

        val rom = NESLoader.load(romFile)
        val interp = BinaryInterpreter6502()
        NESLoader.loadIntoMemory(rom, interp.memory)

        // Set up PPU and controller stubs (like FrameCounterBasedTASTest)
        val ppu = SimplePPU()
        val controller = SimpleController()

        interp.memoryReadHook = { addr ->
            when {
                addr in 0x2000..0x2007 -> ppu.read(addr)
                addr == 0x4016 -> controller.read()
                else -> null
            }
        }

        interp.memoryWriteHook = { addr, value ->
            when {
                addr in 0x2000..0x2007 -> {
                    ppu.write(addr, value)
                    true
                }
                addr == 0x4014 -> true  // OAM DMA - ignore
                addr == 0x4016 -> {
                    controller.writeStrobe(value)
                    true
                }
                else -> false
            }
        }

        return interp
    }

    /**
     * Run initialization sequence for interpreter (equivalent to coldBoot).
     * This runs the Start routine until it enters EndlessLoop, then fires first NMI.
     */
    fun runInterpreterInit(interp: BinaryInterpreter6502, ppu: SimplePPU, controller: SimpleController) {
        // Clear RAM
        for (addr in 0x0000..0x07FF) {
            interp.memory.writeByte(addr, 0u)
        }

        // Set reset vector entry point
        interp.cpu.PC = 0x8000u

        // Run until we hit an infinite loop (PC doesn't change after instruction)
        // This detects EndlessLoop: jmp EndlessLoop
        var cycles = 0
        val maxCycles = 50000
        var prevPC: Int
        while (cycles < maxCycles && !interp.halted) {
            prevPC = interp.cpu.PC.toInt()
            cycles += interp.step()

            // Detect infinite loop: PC jumps back to where it was
            val opcode = interp.memory.readByte(prevPC).toInt()
            if (opcode == 0x4C) {  // JMP absolute
                val jumpTarget = interp.memory.readByte(prevPC + 1).toInt() or
                                (interp.memory.readByte(prevPC + 2).toInt() shl 8)
                if (jumpTarget == prevPC) {
                    // Found EndlessLoop: jmp EndlessLoop
                    println("Detected EndlessLoop at PC=0x${prevPC.toString(16)}, cycles=$cycles")
                    break
                }
            }
        }

        // Now run the first NMI to complete initialization
        // This sets up IntervalTimerControl and runs OperModeExecutionTree
        controller.setButtons(0)
        ppu.startFrame()
        interp.nmiPending = true
        interp.inNmiHandler = false

        var hitNmi = false
        cycles = 0
        while (cycles < 50000 && !interp.halted) {
            val prevInNmi = interp.inNmiHandler
            cycles += interp.step()
            ppu.advanceStep()

            if (!prevInNmi && interp.inNmiHandler) {
                hitNmi = true
                println("NMI entered at PC=0x${interp.cpu.PC.toInt().toString(16)}")
            }

            if (hitNmi && !interp.inNmiHandler) {
                val fc = interp.memory.readByte(Addresses.FrameCounter).toInt()
                val intCtrl = interp.memory.readByte(Addresses.IntervalTimerControl).toInt()
                println("First NMI completed after $cycles cycles, FC=$fc, IntCtrl=$intCtrl")
                break
            }
        }

        // Debug: show state after init
        val fc = interp.memory.readByte(Addresses.FrameCounter).toInt()
        val task = interp.memory.readByte(Addresses.OperMode_Task).toInt()
        println("After init NMI: FC=$fc, Task=$task")
    }

    /**
     * Run a single frame in the interpreter.
     */
    fun runInterpreterFrame(interp: BinaryInterpreter6502, buttons: Int, ppu: SimplePPU, controller: SimpleController, debug: Boolean = false) {
        controller.setButtons(buttons)
        ppu.startFrame()

        // Capture before state
        val beforeFC = interp.memory.readByte(Addresses.FrameCounter).toInt()
        val beforeIntCtrl = interp.memory.readByte(Addresses.IntervalTimerControl).toInt()

        // Trigger NMI
        interp.nmiPending = true
        interp.inNmiHandler = false

        // Run until NMI handler returns
        var cycles = 0
        val maxCycles = 50000
        var hitNmi = false
        while (cycles < maxCycles && !interp.halted) {
            val prevInNmi = interp.inNmiHandler
            cycles += interp.step()
            ppu.advanceStep()

            if (!prevInNmi && interp.inNmiHandler) {
                hitNmi = true
            }

            // Check if RTI cleared the in-handler flag (indicates NMI handler done)
            if (hitNmi && !interp.inNmiHandler) {
                break
            }
        }

        if (debug) {
            val afterFC = interp.memory.readByte(Addresses.FrameCounter).toInt()
            val afterIntCtrl = interp.memory.readByte(Addresses.IntervalTimerControl).toInt()
            println("  Interp frame: FC=$beforeFC->$afterFC, IntCtrl=$beforeIntCtrl->$afterIntCtrl, cycles=$cycles, hitNmi=$hitNmi")
        }
    }

    @Test
    fun `compare single frame from known state`() {
        // Set up interpreter
        val interp = setupInterpreter()
        if (interp == null) {
            println("⚠️ ROM not found, skipping test")
            return
        }

        // Set up decompiled runner
        val runner = SMBGameRunner()
        runner.initialize()

        // Capture initial states
        val interpState = CriticalState.fromInterpreter(interp)
        val decompState = CriticalState.fromDecompiled()

        println("=== Initial State Comparison ===")
        println("Interpreter: $interpState")
        println("Decompiled:  $decompState")

        val initialDiffs = interpState.diff(decompState)
        if (initialDiffs.isEmpty()) {
            println("✅ Initial states match!")
        } else {
            println("❌ Initial state differences:")
            initialDiffs.forEach { println("  $it") }
        }

        // Run a single frame with no input
        // TODO: Need to properly sync the PPU/controller stubs
    }

    @Test
    fun `find first divergence point in TAS`() {
        // Load TAS
        val tasPaths = listOf(
            "happylee-warps.fm2",
            "../happylee-warps.fm2",
            "../../happylee-warps.fm2"
        )
        val tasFile = tasPaths.map { File(it) }.firstOrNull { it.exists() }
        if (tasFile == null) {
            println("⚠️ TAS file not found, skipping test")
            return
        }
        val movie = TASMovie.parseFM2(tasFile.readText())
        println("Loaded TAS with ${movie.frameCount} frames")

        // Set up interpreter
        val interp = setupInterpreter()
        if (interp == null) {
            println("⚠️ ROM not found, skipping test")
            return
        }

        // Set up decompiled runner
        val runner = SMBGameRunner()
        runner.initialize()

        // Copy RAM from decompiled to interpreter to start from same state
        // (The decompiled runner has already done coldBoot)
        for (addr in 0x0000..0x07FF) {
            interp.memory.writeByte(addr, memory[addr])
        }

        // Also set up interpreter's CPU state to match post-init
        interp.cpu.PC = 0x8000u  // Will be overwritten by NMI
        interp.halted = false

        // Set up PPU/controller for interpreter
        val ppu = SimplePPU()
        val controller = SimpleController()

        interp.memoryReadHook = { addr ->
            when {
                addr in 0x2000..0x2007 -> ppu.read(addr)
                addr == 0x4016 -> controller.read()
                else -> null
            }
        }

        interp.memoryWriteHook = { addr, value ->
            when {
                addr in 0x2000..0x2007 -> {
                    ppu.write(addr, value)
                    true
                }
                addr == 0x4014 -> true
                addr == 0x4016 -> {
                    controller.writeStrobe(value)
                    true
                }
                else -> false
            }
        }

        println("\n=== Running Frame-by-Frame Comparison ===")

        val maxFrames = minOf(500, movie.frameCount)
        var firstDivergenceFrame = -1
        var lastMatchFrame = -1

        for (frame in 0 until maxFrames) {
            val input = movie.getFrame(frame)

            // Run decompiled frame
            runner.runFrame(input.player1, input.player2)

            // Run interpreter frame
            runInterpreterFrame(interp, input.player1, ppu, controller)

            // Compare states
            val interpState = CriticalState.fromInterpreter(interp)
            val decompState = CriticalState.fromDecompiled()
            val diffs = interpState.diff(decompState)

            if (diffs.isEmpty()) {
                lastMatchFrame = frame
                if (frame % 50 == 0) {
                    println("Frame $frame: ✅ Match - $interpState")
                }
            } else {
                if (firstDivergenceFrame < 0) {
                    firstDivergenceFrame = frame
                    println("\n🔴 FIRST DIVERGENCE at frame $frame!")
                    println("Interpreter: $interpState")
                    println("Decompiled:  $decompState")
                    println("Differences:")
                    diffs.forEach { println("  - $it") }

                    // Also show full RAM differences for this frame
                    val ramDiffs = compareRam(interp, 0x0000..0x07FF)
                    println("\nRAM differences (first 20):")
                    ramDiffs.take(20).forEach { println("  $it") }
                    if (ramDiffs.size > 20) {
                        println("  ... and ${ramDiffs.size - 20} more")
                    }
                }
                // Continue a few more frames to see the pattern
                if (frame < firstDivergenceFrame + 5) {
                    println("Frame $frame: Diverged - ${diffs.take(3).joinToString(", ")}")
                }
            }
        }

        println("\n=== Summary ===")
        println("Last matching frame: $lastMatchFrame")
        println("First divergence frame: $firstDivergenceFrame")

        if (firstDivergenceFrame >= 0) {
            println("\n⚠️ Divergence detected - decompiled code differs from interpreter")
        } else {
            println("\n✅ All $maxFrames frames matched!")
        }
    }

    @Test
    fun `single frame step comparison`() {
        // This test runs from a specific interpreter state and compares one frame
        // Useful for debugging specific issues

        println("=== Single Frame Step Comparison ===")
        println("This test verifies decompiled code matches interpreter for a single frame")
        println("given identical initial memory state.\n")

        // Load ROM for interpreter
        val interp = setupInterpreter()
        if (interp == null) {
            println("⚠️ ROM not found, skipping test")
            return
        }

        // Initialize decompiled
        val runner = SMBGameRunner()
        runner.initialize()

        // After decompiled init, copy its memory to interpreter
        // This gives us identical starting state
        for (addr in 0x0000..0x07FF) {
            interp.memory.writeByte(addr, memory[addr])
        }

        println("Initial state copied from decompiled to interpreter")

        val beforeInterp = CriticalState.fromInterpreter(interp)
        val beforeDecomp = CriticalState.fromDecompiled()
        println("Before frame 0:")
        println("  Interpreter: $beforeInterp")
        println("  Decompiled:  $beforeDecomp")

        // Set up PPU/controller
        val ppu = SimplePPU()
        val controller = SimpleController()

        interp.memoryReadHook = { addr ->
            when {
                addr in 0x2000..0x2007 -> ppu.read(addr)
                addr == 0x4016 -> controller.read()
                else -> null
            }
        }

        interp.memoryWriteHook = { addr, value ->
            when {
                addr in 0x2000..0x2007 -> { ppu.write(addr, value); true }
                addr == 0x4014 -> true
                addr == 0x4016 -> { controller.writeStrobe(value); true }
                else -> false
            }
        }

        // Run one frame with no input
        runner.runFrame(0, 0)
        runInterpreterFrame(interp, 0, ppu, controller)

        val afterInterp = CriticalState.fromInterpreter(interp)
        val afterDecomp = CriticalState.fromDecompiled()

        println("\nAfter frame 0:")
        println("  Interpreter: $afterInterp")
        println("  Decompiled:  $afterDecomp")

        val diffs = afterInterp.diff(afterDecomp)
        if (diffs.isEmpty()) {
            println("\n✅ Single frame matches!")
        } else {
            println("\n❌ Differences after single frame:")
            diffs.forEach { println("  - $it") }

            // Show RAM differences
            val ramDiffs = compareRam(interp, 0x0000..0x07FF)
            println("\nRAM differences (first 30):")
            ramDiffs.take(30).forEach { println("  $it") }
        }
    }

    @Test
    fun `interpreter initialized comparison`() {
        // This test runs the interpreter through full initialization,
        // copies its state to the decompiled runtime, then compares frames.
        // This bypasses the decompiled init and uses interpreter's state directly.

        println("=== Interpreter-Initialized Comparison ===")
        println("Running interpreter init, then copying to decompiled runtime.\n")

        // Load ROM for interpreter
        val interp = setupInterpreter()
        if (interp == null) {
            println("⚠️ ROM not found, skipping test")
            return
        }

        // Set up PPU/controller for interpreter
        val ppu = SimplePPU()
        val controller = SimpleController()

        interp.memoryReadHook = { addr ->
            when {
                addr in 0x2000..0x2007 -> ppu.read(addr)
                addr == 0x4016 -> controller.read()
                else -> null
            }
        }

        interp.memoryWriteHook = { addr, value ->
            when {
                addr in 0x2000..0x2007 -> { ppu.write(addr, value); true }
                addr == 0x4014 -> true
                addr == 0x4016 -> { controller.writeStrobe(value); true }
                else -> false
            }
        }

        // Run interpreter initialization (includes first NMI)
        runInterpreterInit(interp, ppu, controller)
        println("Interpreter initialization complete")
        println("Interpreter state: ${CriticalState.fromInterpreter(interp)}")

        // Now copy interpreter RAM to decompiled memory
        clearMemory()  // Clear decompiled memory first
        for (addr in 0x0000..0x07FF) {
            memory[addr] = interp.memory.readByte(addr)
        }
        // Also copy ROM area (for level data lookups etc)
        for (addr in 0x8000..0xFFFF) {
            memory[addr] = interp.memory.readByte(addr)
        }

        println("Copied interpreter RAM to decompiled memory")
        println("Decompiled state: ${CriticalState.fromDecompiled()}")

        // Create a runner and partially initialize it
        val runner = SMBGameRunner()
        // Reset just the runtime components (PPU, controller) but NOT memory
        runner.runtime.ppu.reset()
        runner.runtime.controller.reset()
        // Disable NMI skipping since we're starting from interpreter state
        runner.runtime.disableNmiSkip()
        // Enable NMI in the PPU - the mirror register might have bit 7 cleared during NMI handler
        // but we need NMI enabled for the next frame
        val ppuCtrl = (memory[Mirror_PPU_CTRL_REG1].toInt() or 0x80).toUByte()
        runner.runtime.ppu.writeRegister(0x2000, ppuCtrl)
        println("Enabled NMI in PPU (PPUCTRL = 0x${ppuCtrl.toInt().toString(16)})")

        // Run 5 frames and compare
        println("\n=== Running 5 frames in parallel ===")
        for (frame in 0 until 5) {
            val input = 0  // No input for now

            // Capture before state
            val beforeInterp = CriticalState.fromInterpreter(interp)
            val beforeDecomp = CriticalState.fromDecompiled()

            // Run one frame each
            runInterpreterFrame(interp, input, ppu, controller, debug = true)
            runner.runFrame(input, 0)

            // Compare after state
            val afterInterp = CriticalState.fromInterpreter(interp)
            val afterDecomp = CriticalState.fromDecompiled()

            val diffs = afterInterp.diff(afterDecomp)
            if (diffs.isEmpty()) {
                println("Frame $frame: ✅ Match - $afterInterp")
            } else {
                println("Frame $frame: ❌ DIVERGENCE")
                println("  Interpreter: $afterInterp")
                println("  Decompiled:  $afterDecomp")
                println("  Differences:")
                diffs.forEach { println("    - $it") }

                val ramDiffs = compareRam(interp, 0x0000..0x07FF)
                println("  RAM diffs (first 10): ${ramDiffs.take(10).joinToString(", ")}")

                // Stop on first divergence
                break
            }
        }
    }
}

/**
 * Simple PPU stub for interpreter testing.
 * Models sprite 0 hit timing for SMB's split-screen status bar.
 */
class SimplePPU {
    var ppuCtrl: UByte = 0u
        private set
    private var ppuStatus: UByte = 0x00u
    var frameSteps = 0

    val stepsPerFrame = 8509
    val vblankEndSteps = 800
    val sprite0HitSteps = 1200

    fun advanceStep() {
        frameSteps++
    }

    fun read(addr: Int): UByte {
        return when (addr and 0x07) {
            0x02 -> {
                val phaseStep = frameSteps % stepsPerFrame
                val sprite0Hit = phaseStep >= sprite0HitSteps
                var status = ppuStatus.toInt()
                if (sprite0Hit) status = status or 0x40 else status = status and 0xBF
                val inVblank = phaseStep < vblankEndSteps
                if (inVblank) status = status or 0x80 else status = status and 0x7F
                ppuStatus = (status and 0x7F).toUByte()
                status.toUByte()
            }
            else -> 0u
        }
    }

    fun write(addr: Int, value: UByte) {
        when (addr and 0x07) {
            0x00 -> ppuCtrl = value
        }
    }

    fun startFrame() {
        ppuStatus = 0x80u
        frameSteps = 0
    }
}

/**
 * Simple controller for interpreter testing.
 */
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
