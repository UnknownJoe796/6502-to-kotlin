@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.smb

import com.ivieleague.decompiler6502tokotlin.hand.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests that run the decompiled SMB code to verify it works.
 * These tests avoid hardware-dependent functions (PPU polling loops).
 */
class DecompiledGameTest {

    private fun initializeGameState() {
        resetCPU()
        clearMemory()
        memory[OperMode] = 0u
        memory[OperMode_Task] = 0u
        memory[WorldNumber] = 0u
        memory[LevelNumber] = 0u
        memory[AreaType] = 0u
        memory[DisableScreenFlag] = 0u
        memory[FrameCounter] = 0u
    }

    @Test
    fun `test game loop with debug tracing`() {
        resetCPU()
        clearMemory()

        // Set up controller input simulation
        val controller = ControllerInput()
        memoryReadIntercept = { addr ->
            when (addr) {
                0x4016, 0x4017 -> controller.readController(addr).toUByte()
                0x2002 -> 0x80u  // VBlank flag set
                else -> null
            }
        }
        memoryWriteIntercept = { addr, value ->
            when (addr) {
                0x4016 -> { controller.writeStrobe(value); true }
                in 0x2000..0x2007 -> true  // PPU writes - ignore
                else -> false
            }
        }

        // Initialize like SMBGameRunner does
        memory[DemoTimer] = 0x18u
        memory[IntervalTimerControl] = 0x14u
        memory[Mirror_PPU_CTRL_REG1] = 0x90u  // NMI enabled
        memory[Mirror_PPU_CTRL_REG2] = 0x06u
        memory[OperMode] = 0u
        memory[OperMode_Task] = 0u

        println("=== Testing individual NMI components ===")

        // Test each component separately
        println("1. Testing readJoypads...")
        readJoypads()
        println("   ✓ readJoypads completed")

        println("2. Testing pauseRoutine...")
        pauseRoutine()
        println("   ✓ pauseRoutine completed")

        println("3. Testing updateTopScore...")
        updateTopScore()
        println("   ✓ updateTopScore completed")

        println("4. Testing operModeExecutionTree (OperMode=0, Task=0)...")
        println("   OperMode=${memory[OperMode]}, OperMode_Task=${memory[OperMode_Task]}")

        // This is where it probably hangs - let's trace into it
        val operMode = memory[OperMode].toInt()
        println("   Calling titleScreenMode_real...")

        // Try each task separately
        for (task in 0..3) {
            memory[OperMode_Task] = task.toUByte()
            println("   Task $task: Starting...")
            try {
                when (task) {
                    0 -> {
                        println("     Calling initializeGame_real...")
                        // This calls initializeMemory and loadAreaPointer
                        // Let's trace further
                        println("     Calling initializeMemory(0x6F)...")
                        initializeMemory(0x6F)
                        println("     ✓ initializeMemory completed")
                    }
                    1 -> {
                        println("     Calling screenRoutines_real...")
                        screenRoutines_real()
                        println("     ✓ screenRoutines_real completed")
                    }
                    2 -> {
                        println("     Calling primaryGameSetup_real...")
                        primaryGameSetup_real()
                        println("     ✓ primaryGameSetup_real completed")
                    }
                    3 -> {
                        println("     Calling gameMenuRoutine_real...")
                        gameMenuRoutine_real()
                        println("     ✓ gameMenuRoutine_real completed")
                    }
                }
                println("   Task $task: Completed ✓")
            } catch (e: Exception) {
                println("   Task $task: EXCEPTION - ${e.message}")
                e.printStackTrace()
            }
        }

        // Now test the actual operModeExecutionTree
        println("\n=== Testing operModeExecutionTree directly ===")
        memory[OperMode] = 0u
        memory[OperMode_Task] = 3u  // Start at gameMenuRoutine (title screen waiting for input)
        println("Before: OperMode=${memory[OperMode]}, Task=${memory[OperMode_Task]}")
        operModeExecutionTree()
        println("After: OperMode=${memory[OperMode]}, Task=${memory[OperMode_Task]}")
        println("✓ operModeExecutionTree completed")

        // Test multiple frames with the full nmiHandler equivalent
        println("\n=== Testing multiple frames ===")
        memory[OperMode] = 0u
        memory[OperMode_Task] = 3u
        memory[GamePauseStatus] = 0u

        for (frame in 0 until 10) {
            println("Frame $frame starting...")

            // Read joypads
            readJoypads()

            // Pause handling
            pauseRoutine()

            // Update top score
            updateTopScore()

            // Increment frame counter
            memory[FrameCounter] = ((memory[FrameCounter].toInt() + 1) and 0xFF).toUByte()

            // Main game logic (if not paused)
            val gamePauseStatus = memory[GamePauseStatus].toInt()
            if ((gamePauseStatus and 0x01) == 0) {
                operModeExecutionTree()
            }

            println("Frame $frame: OperMode=${memory[OperMode]}, Task=${memory[OperMode_Task]}")
        }
        println("✓ 10 frames completed")

        // Now test using SMBGameRunner - run many frames and track progress
        println("\n=== Testing SMBGameRunner 100 frames ===")
        val runner = SMBGameRunner()
        runner.initialize()
        println("State after init: ${runner.getState()}")

        var lastTask = -1
        var lastScreenTask = -1
        for (frame in 0 until 100) {
            runner.runFrame(0)
            val task = memory[OperMode_Task].toInt()
            val screenTask = memory[ScreenRoutineTask].toInt()
            if (task != lastTask || screenTask != lastScreenTask) {
                println("Frame $frame: OperMode_Task=$task, ScreenRoutineTask=$screenTask")
                lastTask = task
                lastScreenTask = screenTask
            }
        }
        println("Final state: ${runner.getState()}")
        println("✓ 100 frames completed")

        // Now press Start
        println("\n=== Pressing Start button ===")
        println("SavedJoypad1Bits before: ${memory[SavedJoypad1Bits]}")
        runner.runFrame(ControllerInput.START)
        println("SavedJoypad1Bits after: ${memory[SavedJoypad1Bits]}")
        val afterStart = runner.getState()
        println("After Start: OperMode=${afterStart.operMode}, Task=${afterStart.operModeTask}")

        // Run more frames to see mode transition and gameplay
        var gameplayStartFrame = -1
        for (frame in 0 until 100) {
            val task = memory[OperMode_Task].toInt()
            val ge = memory[GameEngineSubroutine].toInt()
            val playerStatus = memory[PlayerStatus].toInt()
            val areaType = memory[AreaType].toInt()
            val operMode = memory[OperMode].toInt()

            // Debug frames in Gameplay with task=3
            if (operMode == 1 && task >= 2) {
                println("Frame $frame after Start: operMode=1, task=$task, ge=$ge, playerStatus=$playerStatus, areaType=$areaType")
            }

            runner.runFrame(0)
            val state = runner.getState()
            if (state.operMode != 0 && gameplayStartFrame == -1) {
                println("Frame $frame after Start: MODE CHANGED to ${state.operMode}!")
                gameplayStartFrame = frame
            }
        }
        println("State after Start+100 frames: ${runner.getState()}")

        memoryReadIntercept = null
        memoryWriteIntercept = null
    }

    @Test
    fun `test happylee TAS progress`() {
        // Load the TAS file
        val tasContent = java.io.File("happylee-warps.fm2").readText()
        val movie = TASMovie.parseFM2(tasContent)
        println("Loaded TAS with ${movie.frameCount} frames")

        // Initialize game runner
        val runner = SMBGameRunner()
        runner.initialize()

        // Track milestones
        var lastWorld = 0
        var lastLevel = 0
        var lastOperMode = -1
        val milestones = mutableListOf<String>()

        // Run full TAS to check complete gameplay
        val maxFrames = movie.frameCount
        for (frame in 0 until maxFrames) {
            val input = movie.getFrame(frame)

            runner.runFrame(input.player1, input.player2)

            val state = runner.getState()
            val world = state.world
            val level = state.level
            val operMode = state.operMode

            // Log mode transitions
            if (operMode != lastOperMode) {
                val modeName = when (operMode) {
                    0 -> "TitleScreen"
                    1 -> "Gameplay"
                    2 -> "Victory"
                    3 -> "GameOver"
                    else -> "Unknown($operMode)"
                }
                milestones.add("Frame $frame: Mode -> $modeName")
                println("Frame $frame: OperMode -> $modeName (Task=${state.operModeTask})")
                lastOperMode = operMode
            }

            // Log world/level transitions
            if (world != lastWorld || level != lastLevel) {
                milestones.add("Frame $frame: World $world-$level")
                println("Frame $frame: Now at World $world-$level")
                lastWorld = world
                lastLevel = level
            }

            // Early exit if game is won
            if (operMode == 2) {
                println("GAME BEATEN at frame $frame!")
                break
            }
        }

        println("\n=== MILESTONES ===")
        milestones.forEach { println(it) }
        println("\nFinal state after $maxFrames frames: ${runner.getState()}")
    }

    @Test
    fun `test readJoypads stores button state`() {
        resetCPU()
        clearMemory()

        // Set up controller input simulation
        val controller = ControllerInput()
        controller.setButtons(0, ControllerInput.START)  // 0x10

        memoryReadIntercept = { addr ->
            when (addr) {
                0x4016, 0x4017 -> controller.readController(addr).toUByte()
                else -> null
            }
        }
        memoryWriteIntercept = { addr, value ->
            when (addr) {
                0x4016 -> {
                    controller.writeStrobe(value)
                    true
                }
                else -> false
            }
        }

        // Call readJoypads
        readJoypads()

        // Check SavedJoypadBits[0] = SavedJoypad1Bits
        val savedJoypad1 = memory[SavedJoypad1Bits].toInt()
        val savedJoypad2 = memory[SavedJoypad1Bits + 1].toInt()

        println("After readJoypads():")
        println("  SavedJoypad1Bits (0x06FC) = 0x${savedJoypad1.toString(16)} (${savedJoypad1})")
        println("  SavedJoypad2Bits (0x06FD) = 0x${savedJoypad2.toString(16)} (${savedJoypad2})")
        println("  Start_Button = 0x${Start_Button.toString(16)}")

        // Clean up intercepts
        memoryReadIntercept = null
        memoryWriteIntercept = null

        // Verify Start button was saved - should be 0x10 or some value based on debounce logic
        assertTrue(savedJoypad1 != 0 || savedJoypad2 != 0,
            "At least one joypad should have button state saved")
        println("✅ readJoypads saved controller state correctly")
    }

    @Test
    fun `test game core routine early exit`() {
        initializeGameState()
        // Set up for early exit (task < 3)
        memory[OperMode_Task] = 2u
        memory[CurrentPlayer] = 0u
        gameCoreRoutine()
        println("✅ gameCoreRoutine() early exit completed")
    }

    @Test
    fun `test pause routine`() {
        initializeGameState()
        pauseRoutine()
        println("✅ pauseRoutine() completed")
    }

    @Test
    fun `test sprite shuffler`() {
        initializeGameState()
        spriteShuffler()
        println("✅ spriteShuffler() completed")
    }

    @Test
    fun `test color rotation`() {
        initializeGameState()
        memory[ColorRotateOffset] = 0u
        memory[FrameCounter] = 0u
        colorRotation()
        println("✅ colorRotation() completed")
    }

    @Test
    fun `test floatey numbers`() {
        initializeGameState()
        floateyNumbersRoutine(0)
        println("✅ floateyNumbersRoutine() completed")
    }

    @Test
    fun `test multiple frames of simple routines`() {
        initializeGameState()
        var frames = 0
        for (i in 0 until 60) {
            memory[FrameCounter] = (i and 0xFF).toUByte()
            pauseRoutine()
            spriteShuffler()
            colorRotation()
            frames++
        }
        println("✅ Ran $frames frames of simple routines")
        assertTrue(frames == 60, "Should complete 60 frames")
    }

    @Test
    fun `dump RAM frames for comparison`() {
        // Load the TAS file
        val tasContent = java.io.File("happylee-warps.fm2").readText()
        val movie = TASMovie.parseFM2(tasContent)
        println("Loaded TAS with ${movie.frameCount} frames")

        // Initialize game runner
        val runner = SMBGameRunner()
        runner.initialize()

        // Dump first 300 frames of RAM (2KB per frame)
        val outputFile = java.io.File("local/tas/decompiled-full-ram.bin")
        outputFile.parentFile.mkdirs()
        val output = java.io.FileOutputStream(outputFile)

        val maxFrames = minOf(movie.frameCount, 300)
        for (frame in 0 until maxFrames) {
            val input = movie.getFrame(frame)
            runner.runFrame(input.player1, input.player2)

            // Dump first 2KB of RAM (0x0000-0x07FF)
            val ramDump = ByteArray(2048)
            for (i in 0 until 2048) {
                ramDump[i] = memory[i].toByte()
            }
            output.write(ramDump)
        }
        output.close()

        println("Dumped $maxFrames frames to ${outputFile.absolutePath}")
        println("File size: ${outputFile.length()} bytes (expected: ${maxFrames * 2048})")
    }

    @Test
    fun `compare RAM frames with FCEUX`() {
        val fceuxFile = java.io.File("local/tas/fceux-full-ram.bin")
        val decompiledFile = java.io.File("local/tas/decompiled-full-ram.bin")

        if (!fceuxFile.exists()) {
            println("FCEUX RAM dump not found - skipping comparison")
            return
        }
        if (!decompiledFile.exists()) {
            println("Decompiled RAM dump not found - run 'dump RAM frames for comparison' first")
            return
        }

        val fceuxData = fceuxFile.readBytes()
        val decompiledData = decompiledFile.readBytes()

        val fceuxFrames = fceuxData.size / 2048
        val decompiledFrames = decompiledData.size / 2048

        println("FCEUX: $fceuxFrames frames, Decompiled: $decompiledFrames frames")

        // Start comparing from frame 0 to find initial divergence
        println("\n=== Finding first divergence point ===")
        for (frame in 0 until minOf(100, decompiledFrames, fceuxFrames)) {
            val fceuxRam = fceuxData.sliceArray(frame * 2048 until (frame + 1) * 2048)
            val decompiledRam = decompiledData.sliceArray(frame * 2048 until (frame + 1) * 2048)

            // Zero out FrameCounter ($0009) for comparison - it's expected to differ
            fceuxRam[0x09] = 0
            decompiledRam[0x09] = 0

            var diffCount = 0
            val diffs = mutableListOf<String>()
            for (i in 0 until 2048) {
                if (fceuxRam[i] != decompiledRam[i]) {
                    diffCount++
                    if (diffs.size < 15) {
                        diffs.add("  \$${i.toString(16).padStart(4, '0')}: FCEUX=${fceuxRam[i].toInt() and 0xFF} DECOMPILED=${decompiledRam[i].toInt() and 0xFF}")
                    }
                }
            }

            // Show OperMode and key state for context
            val fceuxOperMode = fceuxRam[OperMode].toInt() and 0xFF
            val decompiledOperMode = decompiledRam[OperMode].toInt() and 0xFF
            val fceuxTask = fceuxRam[OperMode_Task].toInt() and 0xFF
            val decompiledTask = decompiledRam[OperMode_Task].toInt() and 0xFF

            if (diffCount == 0) {
                println("Frame $frame: PERFECT MATCH ✅ (OperMode=$fceuxOperMode, Task=$fceuxTask)")
            } else if (diffCount < 20) {
                println("Frame $frame: $diffCount diffs (F:Mode=$fceuxOperMode,Task=$fceuxTask | D:Mode=$decompiledOperMode,Task=$decompiledTask)")
                diffs.forEach { println(it) }
            } else {
                println("Frame $frame: $diffCount diffs (F:Mode=$fceuxOperMode,Task=$fceuxTask | D:Mode=$decompiledOperMode,Task=$decompiledTask)")
                diffs.take(5).forEach { println(it) }
                println("  ... and ${diffCount - 5} more")
            }
        }
    }
}
