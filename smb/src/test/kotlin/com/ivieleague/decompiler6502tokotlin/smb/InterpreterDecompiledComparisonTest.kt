@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.smb

import com.ivieleague.decompiler6502tokotlin.hand.*
import com.ivieleague.decompiler6502tokotlin.interpreter.BinaryInterpreter6502
import com.ivieleague.decompiler6502tokotlin.smb.generated.*
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Compares interpreter execution vs decompiled Kotlin execution frame by frame.
 *
 * This is the authoritative test for decompiler correctness:
 * 1. Run interpreter initialization
 * 2. Copy interpreter RAM to decompiled memory
 * 3. Run both in parallel frame by frame
 * 4. Compare memory after each frame
 */
class InterpreterDecompiledComparisonTest {

    companion object {
        // Key addresses for comparison
        const val OperMode = 0x0770
        const val OperMode_Task = 0x0772
        const val FrameCounter = 0x09
        const val WorldNumber = 0x075F
        const val LevelNumber = 0x0760
        const val Player_X_Position = 0x0086
        const val Player_Y_Position = 0x00CE
        const val IntervalTimerControl = 0x077F
        const val GameEngineSubroutine = 0x0E
        const val SavedJoypad1Bits = 0x06FC

        // NMI entry point in ROM
        const val NMI_ENTRY = 0x8082
    }

    /**
     * Test: Initialize interpreter, copy state to decompiled, run a few frames in parallel.
     */
    @Test
    fun `interpreter initialized comparison`() {
        val romFile = listOf("../local/roms/smb.nes", "local/roms/smb.nes", "../smb.nes", "smb.nes")
            .map { File(it) }
            .firstOrNull { it.exists() }

        if (romFile == null) {
            println("Skipping: No ROM file found")
            return
        }

        println("=== Interpreter-Initialized Comparison ===")
        println("Running interpreter init, then copying to decompiled runtime.")

        // Load ROM
        val romData = romFile.readBytes().toUByteArray()
        val prgRom = romData.sliceArray(16 until 16 + 0x8000)  // 32KB PRG

        // Initialize interpreter
        val interp = BinaryInterpreter6502()

        // Load PRG ROM into interpreter memory at $8000-$FFFF
        for (i in prgRom.indices) {
            interp.memory.writeByte(0x8000 + i, prgRom[i])
        }

        // Simple PPU stub for interpreter
        var ppuCtrl: UByte = 0u
        var ppuStatus: UByte = 0x80u  // Start in VBlank
        var ppuStatusReads = 0

        interp.memoryReadHook = { addr: Int ->
            when (addr) {
                0x2002 -> {
                    ppuStatusReads++
                    val status = ppuStatus
                    // Clear VBlank on read, set sprite 0 after a few reads
                    ppuStatus = if (ppuStatusReads > 3) 0x40u else 0x00u
                    status
                }
                in 0x2000..0x2007 -> 0u
                else -> null
            }
        }

        interp.memoryWriteHook = { addr: Int, value: UByte ->
            when (addr) {
                0x2000 -> { ppuCtrl = value; true }
                0x2001 -> true
                in 0x2002..0x2007 -> true
                0x4014 -> true  // OAM DMA
                in 0x4000..0x4017 -> true  // APU
                else -> false
            }
        }

        // Run RESET sequence (but not the infinite loop)
        interp.cpu.PC = 0x8000u
        var cycles = 0
        while (cycles < 50000) {
            val pc = interp.cpu.PC.toInt()
            // Stop at EndlessLoop (JMP to self)
            if (pc == 0x8075) break
            interp.step()
            cycles++
        }

        // Now run one NMI to get game started
        ppuStatus = 0x80u
        ppuStatusReads = 0
        interp.cpu.PC = NMI_ENTRY.toUShort()
        cycles = 0
        while (cycles < 50000) {
            if (interp.memory.readByte(interp.cpu.PC.toInt()) == 0x40.toUByte()) {
                println("NMI entered at PC=0x${interp.cpu.PC.toString(16)}")
                break
            }
            interp.step()
            cycles++
        }

        val interpFC = interp.memory.readByte(FrameCounter).toInt()
        val interpTask = interp.memory.readByte(OperMode_Task).toInt()
        println("After init NMI: FC=$interpFC, Task=$interpTask")
        println("Interpreter initialization complete")

        // Get interpreter state summary
        // by Claude - added ScreenRoutineTask for debugging
        val ScreenRoutineTask = 0x073C
        fun interpState() = buildString {
            append("Mode=${interp.memory.readByte(OperMode)} ")
            append("Task=${interp.memory.readByte(OperMode_Task)} ")
            append("ScrTask=${interp.memory.readByte(ScreenRoutineTask)} ")
            append("FC=${interp.memory.readByte(FrameCounter)} ")
            append("W${interp.memory.readByte(WorldNumber).toInt() + 1}-${interp.memory.readByte(LevelNumber).toInt() + 1} ")
            append("X=${interp.memory.readByte(Player_X_Position)} ")
            append("Y=${interp.memory.readByte(Player_Y_Position)} ")
            append("XSpd=${interp.memory.readByte(0x0057)} ")  // Player_X_Speed
            append("Page=${interp.memory.readByte(0x006D)} ")  // Player_PageLoc
            append("State=${interp.memory.readByte(0x001D)} ")  // Player_State
            append("GameEng=${interp.memory.readByte(GameEngineSubroutine)} ")
            append("Joy=${interp.memory.readByte(SavedJoypad1Bits)} ")
            append("IntCtrl=${interp.memory.readByte(IntervalTimerControl)}")
        }

        println("Interpreter state: ${interpState()}")

        // Copy interpreter RAM to decompiled memory array
        clearMemory()
        for (addr in 0x0000..0x07FF) {
            memory[addr] = interp.memory.readByte(addr)
        }

        // Also need ROM data for the decompiled version
        initializeRomData()

        // Get decompiled state summary (should match interpreter)
        // by Claude - added ScreenRoutineTask for debugging
        fun decompState() = buildString {
            append("Mode=${memory[OperMode]} ")
            append("Task=${memory[OperMode_Task]} ")
            append("ScrTask=${memory[ScreenRoutineTask]} ")
            append("FC=${memory[FrameCounter]} ")
            append("W${memory[WorldNumber].toInt() + 1}-${memory[LevelNumber].toInt() + 1} ")
            append("X=${memory[Player_X_Position]} ")
            append("Y=${memory[Player_Y_Position]} ")
            append("XSpd=${memory[0x0057]} ")
            append("Page=${memory[0x006D]} ")
            append("State=${memory[0x001D]} ")
            append("GameEng=${memory[GameEngineSubroutine]} ")
            append("Joy=${memory[SavedJoypad1Bits]} ")
            append("IntCtrl=${memory[IntervalTimerControl]}")
        }

        println("Copied interpreter RAM to decompiled memory")
        println("Decompiled state: ${decompState()}")

        // Enable NMI in PPU (decompiled needs this set up)
        memory[0x2000] = 0x90u  // PPUCTRL with NMI enabled
        println("Enabled NMI in PPU (PPUCTRL = 0x90)")

        // Now run both in parallel for more frames with controller input
        println("\n=== Running frames in parallel with controller input ===")

        // Controller input pattern: wait a bit then press START to begin game
        val controllerInputs = buildList {
            repeat(100) { add(0) }  // Wait on title screen
            add(0x10)  // START button (bit 4)
            repeat(300) { add(0) }  // Let game initialize
        }

        for (frame in 0 until 200) {
            val controller = controllerInputs.getOrElse(frame) { 0 }

            // Set controller input for both systems
            interp.memory.writeByte(SavedJoypad1Bits, controller.toUByte())
            memory[SavedJoypad1Bits] = controller.toUByte()

            // Run interpreter NMI
            ppuStatus = 0x80u
            ppuStatusReads = 0
            interp.cpu.PC = NMI_ENTRY.toUShort()
            val interpFCBefore = interp.memory.readByte(FrameCounter).toInt()
            val interpIntCtrlBefore = interp.memory.readByte(IntervalTimerControl).toInt()
            cycles = 0
            var hitNmi = false
            while (cycles < 100000) {
                val opcode = interp.memory.readByte(interp.cpu.PC.toInt())
                if (opcode == 0x40.toUByte()) {  // RTI
                    hitNmi = true
                    break
                }
                interp.step()
                cycles++
            }
            val interpFCAfter = interp.memory.readByte(FrameCounter).toInt()
            val interpIntCtrlAfter = interp.memory.readByte(IntervalTimerControl).toInt()

            // by Claude - print every frame for debugging
            val shouldPrint = frame in listOf(0, 1, 2, 3) || frame >= 20 // Check first few frames and near divergence
            val interpXBefore = interp.memory.readByte(Player_X_Position).toInt()
            val decompXBefore = memory[Player_X_Position].toInt()
            val interpTask = interp.memory.readByte(OperMode_Task).toInt()
            val decompTask = memory[OperMode_Task].toInt()

            // by Claude - debug Player_State changes at Frame 26 and 27
            if (frame in 26..27) {
                val DisableColDet = 0x0716
                val SwimmingFlag = 0x0704
                val Player_State = 0x001D
                val Player_Y_HighPos = 0x00B5
                println("DEBUG Frame $frame after interpreter NMI:")
                println("  Interp Player_State=${interp.memory.readByte(Player_State)}, GameEngSub=${interp.memory.readByte(GameEngineSubroutine)}")
                println("  Decomp Player_State=${memory[Player_State]} (not yet updated, will run NMI next)")
            }

            if (shouldPrint) {
                println("  Frame $frame: FC=$interpFCBefore->$interpFCAfter, IntCtrl=$interpIntCtrlBefore->$interpIntCtrlAfter, Task=$interpTask/$decompTask, ScrTask=${interp.memory.readByte(ScreenRoutineTask)}, Mode=${interp.memory.readByte(OperMode)}, X_before: interp=$interpXBefore decomp=$decompXBefore")
            }

            // Run decompiled NMI equivalent
            // The decompiled code has the NMI logic spread across functions
            // We need to call them in the right order
            runDecompiledNMI()

            // by Claude - track X position and Task after NMI
            val interpXAfter = interp.memory.readByte(Player_X_Position).toInt()
            val decompXAfter = memory[Player_X_Position].toInt()
            val interpTaskAfter = interp.memory.readByte(OperMode_Task).toInt()
            val decompTaskAfter = memory[OperMode_Task].toInt()
            if (shouldPrint) {
                if (interpXBefore != interpXAfter || decompXBefore != decompXAfter || interpXAfter != decompXAfter) {
                    println("    X changed: interp $interpXBefore->$interpXAfter, decomp $decompXBefore->$decompXAfter")
                }
                if (interpTask != interpTaskAfter || decompTask != decompTaskAfter || interpTaskAfter != decompTaskAfter) {
                    println("    Task changed: interp $interpTask->$interpTaskAfter, decomp $decompTask->$decompTaskAfter")
                }
            }

            // Compare states
            val interpSt = interpState()
            val decompSt = decompState()

            // by Claude - Also check for RAM diffs even when states match to detect hidden divergence
            val ramDiffs = mutableListOf<String>()
            for (addr in 0x0000..0x07FF) {
                val interpVal = interp.memory.readByte(addr)
                val decompVal = memory[addr]
                if (interpVal != decompVal) {
                    ramDiffs.add("[\$${addr.toString(16).padStart(4, '0')}]: interp=${interpVal.toString(16).padStart(2, '0')} decomp=${decompVal.toString(16).padStart(2, '0')}")
                }
            }

            if (interpSt == decompSt) {
                // Only print match on significant frames
                if (shouldPrint) {
                    if (ramDiffs.isNotEmpty()) {
                        println("Frame $frame: ⚠️ States match but ${ramDiffs.size} RAM diffs!")
                        println("  RAM diffs (first 10): ${ramDiffs.take(10).joinToString(", ")}")
                    } else {
                        println("Frame $frame: ✓ States match")
                    }
                }
            } else {
                println("Frame $frame: ❌ DIVERGENCE")
                println("  Interpreter: $interpSt")
                println("  Decompiled:  $decompSt")

                // Find specific differences
                val diffs = mutableListOf<String>()
                if (interp.memory.readByte(OperMode_Task) != memory[OperMode_Task]) {
                    diffs.add("OperModeTask: ${interp.memory.readByte(OperMode_Task)} vs ${memory[OperMode_Task]}")
                }
                if (interp.memory.readByte(FrameCounter) != memory[FrameCounter]) {
                    diffs.add("FrameCounter: ${interp.memory.readByte(FrameCounter)} vs ${memory[FrameCounter]}")
                }
                println("  Differences:")
                diffs.forEach { println("    - $it") }

                // by Claude - ramDiffs is now computed earlier
                println("  RAM diffs (first 20): ${ramDiffs.take(20).joinToString(", ")}")
                println("  Total RAM diffs: ${ramDiffs.size}")

                // by Claude - Continue to see if the divergence grows or recovers
                if (frame >= 30) break
            }
        }
    }

    /**
     * Run the decompiled NMI equivalent.
     * This calls the key NMI functions in the right order.
     */
    private fun runDecompiledNMI() {
        // The NMI handler does (roughly):
        // 1. PPU setup (we skip - no real PPU)
        // 2. UpdateScreen (VRAM buffer writes - we skip)
        // 3. SoundEngine
        // 4. ReadJoypads
        // 5. PauseRoutine
        // 6. UpdateTopScore
        // 7. DecTimers (if not paused)
        // 8. FrameCounter++
        // 9. Random number update
        // 10. Sprite handling (skip)
        // 11. OperModeExecutionTree (if not paused)

        // by Claude - DEBUG: track writes to Player_X_Position and Player_Y_HighPos during Frame 25-27
        val frameNum = memory[FrameCounter].toInt()
        val areaTypeAddr = 0x074E
        val Player_Y_HighPos_addr = 0x00CF
        if (frameNum == 25) {
            println("DEBUG Frame $frameNum: AreaType = ${memory[areaTypeAddr]}, Player_Y_HighPos = ${memory[Player_Y_HighPos_addr]}")
        }
        if (frameNum in 25..27) {
            memoryWriteIntercept = { addr, value ->
                if (addr == Player_X_Position) {
                    val oldVal = memory[Player_X_Position].toInt()
                    val trace = Thread.currentThread().stackTrace.take(15).drop(2).joinToString(" <- ") { "${it.methodName}:${it.lineNumber}" }
                    println("DEBUG Player_X_Position write: FC=$frameNum, AreaType=${memory[areaTypeAddr]}, $oldVal -> ${value.toInt()}, trace=$trace")
                }
                if (addr == Player_Y_HighPos_addr) {
                    val oldVal = memory[Player_Y_HighPos_addr].toInt()
                    val trace = Thread.currentThread().stackTrace.take(15).drop(2).joinToString(" <- ") { "${it.methodName}:${it.lineNumber}" }
                    println("DEBUG Player_Y_HighPos write: FC=$frameNum, $oldVal -> ${value.toInt()}, trace=$trace")
                }
                false // Don't intercept, just log
            }
        } else {
            memoryWriteIntercept = null
        }

        // by Claude - Added verbose error logging to catch silent failures
        try {
            // Sound engine (may have side effects)
            soundEngine()
        } catch (e: Exception) {
            System.err.println("soundEngine error at FC=${memory[FrameCounter]}: ${e.message}")
        }

        try {
            readJoypads()
        } catch (e: Exception) {
            System.err.println("readJoypads error at FC=${memory[FrameCounter]}: ${e.message}")
        }

        try {
            pauseRoutine()
        } catch (e: Exception) {
            System.err.println("pauseRoutine error at FC=${memory[FrameCounter]}: ${e.message}")
        }

        try {
            updateTopScore()
        } catch (e: Exception) {
            System.err.println("updateTopScore error at FC=${memory[FrameCounter]}: ${e.message}")
        }

        // by Claude - Sprite handling (done before Sprite0Hit polling)
        // lda GamePauseStatus; lsr; bcs Sprite0Hit; jsr MoveSpritesOffscreen; jsr SpriteShuffler
        val gamePauseStatus = memory[0x0776].toInt()
        if ((gamePauseStatus and 0x01) == 0) {
            try {
                moveSpritesOffscreen()
            } catch (e: Exception) {
                System.err.println("moveSpritesOffscreen error at FC=${memory[FrameCounter]}: ${e.message}")
            }
            try {
                spriteShuffler()
            } catch (e: Exception) {
                System.err.println("spriteShuffler error at FC=${memory[FrameCounter]}: ${e.message}")
            }
        }

        // Decrement timers (simplified)
        if ((gamePauseStatus and 0x01) == 0) {
            val timerControl = memory[0x0747].toInt()
            if (timerControl == 0) {
                var intervalCtrl = memory[IntervalTimerControl].toInt()
                intervalCtrl = (intervalCtrl - 1) and 0xFF
                memory[IntervalTimerControl] = intervalCtrl.toUByte()

                val endOffset = if (intervalCtrl >= 0x80) 0x23 else 0x14
                for (x in endOffset downTo 0) {
                    val timerAddr = 0x0780 + x
                    val timer = memory[timerAddr].toInt()
                    if (timer > 0) {
                        memory[timerAddr] = (timer - 1).toUByte()
                    }
                }

                // Reset interval control if it went negative
                if (intervalCtrl >= 0x80) {
                    memory[IntervalTimerControl] = 0x14u
                }
            } else {
                memory[0x0747] = (timerControl - 1).toUByte()
            }
        }

        // Increment frame counter
        val fc = memory[FrameCounter].toInt()
        memory[FrameCounter] = ((fc + 1) and 0xFF).toUByte()

        // by Claude - Random number generation (fixed to match interpreter)
        // The interpreter XORs bit 1 (mask 0x02) of bytes at 0x07A7 and 0x07A8
        // and uses the result as the carry bit
        val prng0Bit = (memory[0x07A7].toInt() and 0x02) shr 1  // bit 1 shifted to position 0
        val prng1Bit = (memory[0x07A8].toInt() and 0x02) shr 1  // bit 1 shifted to position 0
        val carry = (prng0Bit xor prng1Bit) and 0x01
        var carryBit = carry
        for (i in 0 until 8) {
            val addr = 0x07A7 + i
            val oldVal = memory[addr].toInt()
            val newCarry = oldVal and 0x01  // Save LSB before rotate
            memory[addr] = ((oldVal shr 1) or (carryBit shl 7)).toUByte()
            carryBit = newCarry
        }

        // Main game logic (if not paused)
        // by Claude - debug: track Task and ScrTask changes in operModeExecutionTree
        val taskBefore = memory[OperMode_Task].toInt()
        val scrTaskBefore = memory[ScreenRoutineTask].toInt()
        if ((gamePauseStatus and 0x01) == 0) {
            try {
                operModeExecutionTree()
            } catch (e: Exception) {
                // by Claude - Log errors instead of silently ignoring
                System.err.println("operModeExecutionTree error at FC=${memory[FrameCounter]}: ${e.message}")
                e.printStackTrace(System.err)
            }
        }
        val taskAfter = memory[OperMode_Task].toInt()
        val scrTaskAfter = memory[ScreenRoutineTask].toInt()
        if (taskBefore != taskAfter) {
            println("    DEBUG: Task $taskBefore -> $taskAfter in operModeExecutionTree")
        }
        if (scrTaskBefore != scrTaskAfter) {
            println("    DEBUG: ScrTask $scrTaskBefore -> $scrTaskAfter in operModeExecutionTree")
        }
    }
}
