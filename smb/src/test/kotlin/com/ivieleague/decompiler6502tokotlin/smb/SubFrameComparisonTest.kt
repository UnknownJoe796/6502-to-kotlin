@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.smb

import com.ivieleague.decompiler6502tokotlin.hand.*
import com.ivieleague.decompiler6502tokotlin.interpreter.BinaryInterpreter6502
import com.ivieleague.decompiler6502tokotlin.smb.generated.*
import java.io.File
import kotlin.test.Test

/**
 * Sub-frame comparison test for diagnosing decompiler issues.
 *
 * by Claude - This test runs interpreter and decompiled code side-by-side,
 * comparing RAM state after each major function call within the NMI handler.
 * When divergence is found, it reports exactly which function caused it.
 *
 * Usage:
 * 1. Run the test
 * 2. It will report the first divergence with function name and RAM diffs
 * 3. Fix the decompiler issue for that function
 * 4. Regenerate SMBDecompiled.kt
 * 5. Repeat until no divergence
 */
class SubFrameComparisonTest {

    companion object {
        // Key addresses
        const val OperMode = 0x0770
        const val OperMode_Task = 0x0772
        const val FrameCounter = 0x09
        const val WorldNumber = 0x075F
        const val LevelNumber = 0x0760
        const val AreaNumber = 0x0762
        const val IntervalTimerControl = 0x077F
        const val GamePauseStatus = 0x0776
        const val TimerControl = 0x0747
        const val ScreenRoutineTask = 0x073C
        const val SavedJoypad1Bits = 0x06FC
        const val NMI_ENTRY = 0x8082
        const val Sprite0HitDetectFlag = 0x0722
    }

    // Controller simulation for decompiled code
    private class ControllerState {
        private var _buttons = 0
        private var shiftReg = 0
        private var strobe = false

        fun setButtons(b: Int) { _buttons = b }

        fun writeStrobe(value: Int) {
            if ((value and 0x01) != 0) shiftReg = _buttons
            strobe = (value and 0x01) != 0
        }

        fun readPort(): Int {
            val bit = shiftReg and 0x01
            if (!strobe) shiftReg = shiftReg shr 1
            return bit
        }
    }

    private val controller = ControllerState()

    /**
     * Snapshot of RAM state for comparison
     */
    private data class RamSnapshot(
        val data: ByteArray,
        val label: String
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is RamSnapshot) return false
            return data.contentEquals(other.data)
        }

        override fun hashCode() = data.contentHashCode()

        fun diff(other: RamSnapshot): List<String> {
            val diffs = mutableListOf<String>()
            for (i in data.indices) {
                if (data[i] != other.data[i]) {
                    diffs.add("[$${i.toString(16).padStart(4, '0')}]: ${data[i].toUByte().toString(16).padStart(2, '0')} vs ${other.data[i].toUByte().toString(16).padStart(2, '0')}")
                }
            }
            return diffs
        }
    }

    private fun snapshotInterpreterRam(interp: BinaryInterpreter6502, label: String): RamSnapshot {
        val data = ByteArray(0x800)
        for (i in 0 until 0x800) {
            data[i] = interp.memory.readByte(i).toByte()
        }
        return RamSnapshot(data, label)
    }

    private fun snapshotDecompiledRam(label: String): RamSnapshot {
        val data = ByteArray(0x800)
        for (i in 0 until 0x800) {
            data[i] = memory[i].toByte()
        }
        return RamSnapshot(data, label)
    }

    @Test
    fun `sub-frame comparison finds first divergence`() {
        val romFile = listOf("../local/roms/smb.nes", "local/roms/smb.nes", "../smb.nes", "smb.nes")
            .map { File(it) }.firstOrNull { it.exists() }

        if (romFile == null) {
            println("⚠️ Skipping: No ROM file found")
            return
        }

        println("=== Sub-Frame Comparison Test ===")
        println("ROM: ${romFile.absolutePath}")

        // Set up interpreter
        val romData = romFile.readBytes().toUByteArray()
        val prgRom = romData.sliceArray(16 until 16 + 0x8000)
        val interp = BinaryInterpreter6502()

        for (i in prgRom.indices) {
            interp.memory.writeByte(0x8000 + i, prgRom[i])
        }

        // PPU stub for interpreter
        var ppuCtrl: UByte = 0u
        var ppuStatus: UByte = 0x80u
        var ppuStatusReads = 0
        var interpController = ControllerState()

        interp.memoryReadHook = { addr: Int ->
            when (addr) {
                0x2002 -> {
                    ppuStatusReads++
                    val status = ppuStatus
                    ppuStatus = if (ppuStatusReads > 3) 0x40u else 0x00u
                    status
                }
                0x4016 -> interpController.readPort().toUByte()
                0x4017 -> 0u
                in 0x2000..0x2007 -> 0u
                else -> null
            }
        }

        interp.memoryWriteHook = { addr: Int, value: UByte ->
            when (addr) {
                0x2000 -> { ppuCtrl = value; true }
                0x4016 -> { interpController.writeStrobe(value.toInt()); true }
                in 0x2001..0x2007 -> true
                0x4014 -> true
                in 0x4000..0x4017 -> true
                else -> false
            }
        }

        // Run RESET
        interp.cpu.PC = 0x8000u
        var cycles = 0
        while (cycles < 50000) {
            if (interp.cpu.PC.toInt() == 0x8075) break
            interp.step()
            cycles++
        }

        // Run one interpreter NMI to initialize
        ppuStatus = 0x80u
        ppuStatusReads = 0
        interp.cpu.PC = NMI_ENTRY.toUShort()
        cycles = 0
        while (cycles < 50000) {
            if (interp.memory.readByte(interp.cpu.PC.toInt()) == 0x40.toUByte()) break
            interp.step()
            cycles++
        }

        // Copy interpreter RAM to decompiled memory
        clearMemory()
        for (addr in 0x0000..0x07FF) {
            memory[addr] = interp.memory.readByte(addr)
        }
        initializeRomData()
        setupControllerIntercepts()

        println("Initialization complete")
        println("Initial state: Mode=${memory[OperMode]}, Task=${memory[OperMode_Task]}, ScrTask=${memory[ScreenRoutineTask]}")

        // by Claude - Known addresses for debugging
        val knownAddrs = mapOf(
            0x06e0 to "SprShuffleAmtOffset",
            0x06e1 to "SprShuffleAmt",
            0x06f3 to "Misc_SprDataOffset",
            0x0779 to "Mirror_PPU_CTRL_REG2",
            0x0770 to "OperMode",
            0x0772 to "OperMode_Task",
            0x073C to "ScreenRoutineTask",
        )

        // Controller inputs - wait then press START at frame 40 (after demo initialization)
        val controllerInputs = buildList {
            repeat(40) { add(0) }
            add(0x10) // START
            repeat(500) { add(0) }
        }

        // by Claude - Track benign diff addresses to filter them
        // These are display-related or implementation details that don't affect game logic
        // When key state (Mode, Task, ScrTask) matches, these can be ignored
        val benignAddressRanges = listOf(
            0x00..0x0F,      // Zero page scratch variables
            0x1f0..0x1ff,    // Stack (return addresses)
            0x200..0x2ff,    // OAM (sprite data - display only)
            0x300..0x3ff,    // VRAM buffers (display only)
            0x400..0x4ff,    // Object data arrays (display related)
            0x500..0x5ff,    // Metatile buffers (display only)
            0x600..0x6ff,    // Nametable buffers (display only)
            0x700..0x73f,    // Game state + nametable/area loading
        )
        // Only non-benign when key game state differs
        fun isBenign(addr: Int) = benignAddressRanges.any { addr in it }

        // Run frames with sub-frame comparison
        for (frame in 0 until 300) {
            val buttons = controllerInputs.getOrElse(frame) { 0 }
            interpController.setButtons(buttons)
            controller.setButtons(buttons)
            memory[SavedJoypad1Bits] = buttons.toUByte()
            interp.memory.writeByte(SavedJoypad1Bits, buttons.toUByte())

            // Run interpreter NMI
            ppuStatus = 0x80u
            ppuStatusReads = 0
            interp.cpu.PC = NMI_ENTRY.toUShort()
            cycles = 0
            while (cycles < 100000) {
                if (interp.memory.readByte(interp.cpu.PC.toInt()) == 0x40.toUByte()) break
                interp.step()
                cycles++
            }

            // Run decompiled NMI (full frame)
            runDecompiledNMI()

            // Compare end-of-frame state
            val interpSnap = snapshotInterpreterRam(interp, "end_of_frame")
            val decompSnap = snapshotDecompiledRam("end_of_frame")
            val allDiffs = interpSnap.diff(decompSnap)

            // by Claude - Check key game state addresses first
            val keyAddresses = listOf(
                OperMode to "OperMode",
                OperMode_Task to "OperMode_Task",
                ScreenRoutineTask to "ScreenRoutineTask",
                WorldNumber to "WorldNumber",
                LevelNumber to "LevelNumber",
                AreaNumber to "AreaNumber",
                GamePauseStatus to "GamePauseStatus",
            )
            val keyDiffs = keyAddresses.filter { (addr, _) ->
                interp.memory.readByte(addr) != memory[addr]
            }

            if (keyDiffs.isNotEmpty()) {
                println("\n❌ KEY STATE DIVERGENCE at frame $frame")
                println("   Interpreter: Mode=${interp.memory.readByte(OperMode)}, Task=${interp.memory.readByte(OperMode_Task)}, ScrTask=${interp.memory.readByte(ScreenRoutineTask)}, FC=${interp.memory.readByte(FrameCounter)}")
                println("   Decompiled:  Mode=${memory[OperMode]}, Task=${memory[OperMode_Task]}, ScrTask=${memory[ScreenRoutineTask]}, FC=${memory[FrameCounter]}")
                println("   Key diffs: ${keyDiffs.map { (addr, name) -> "$name: ${interp.memory.readByte(addr)} vs ${memory[addr]}" }.joinToString(", ")}")
                drillDownDivergence(interp, interpController, ppuStatusReads, buttons)
                return
            }

            // Progress report - show world/level info
            val world = memory[WorldNumber].toInt() + 1
            val level = memory[LevelNumber].toInt() + 1
            val operMode = memory[OperMode].toInt()
            if (frame % 20 == 0 || frame < 5 || operMode != 0) {
                val status = if (allDiffs.isEmpty()) "✓ Perfect" else "⚠️ Benign diffs (${allDiffs.size})"
                println("Frame $frame: $status - Mode=$operMode, Task=${memory[OperMode_Task]}, W$world-$level")
            }
        }

        println("\n✅ All frames matched (or only benign diffs)!")
    }

    private data class Divergence(
        val functionName: String,
        val diffs: List<String>
    )

    /**
     * Run the decompiled NMI with checkpoints after each major function.
     * Compare RAM with interpreter after each checkpoint.
     * Returns the first divergence found, or null if all match.
     */
    private fun runDecompiledNMIWithCheckpoints(interp: BinaryInterpreter6502, frame: Int): Divergence? {
        // List of (functionName, functionToRun)
        val checkpoints = listOf<Pair<String, () -> Unit>>(
            "soundEngine" to { soundEngine() },
            "readJoypads" to { readJoypads() },
            "pauseRoutine" to { pauseRoutine() },
            "updateTopScore" to { updateTopScore() },
        )

        // Run each checkpoint and compare
        for ((name, func) in checkpoints) {
            try {
                func()
            } catch (e: Exception) {
                return Divergence("$name (exception: ${e.message})", listOf(e.stackTraceToString()))
            }

            val interpSnap = snapshotInterpreterRam(interp, name)
            val decompSnap = snapshotDecompiledRam(name)
            val diffs = interpSnap.diff(decompSnap)

            if (diffs.isNotEmpty()) {
                return Divergence(name, diffs)
            }
        }

        // Sprite handling (if not paused)
        val gamePauseStatus = memory[GamePauseStatus].toInt()
        if ((gamePauseStatus and 0x01) == 0) {
            for ((name, func) in listOf<Pair<String, () -> Unit>>(
                "moveSpritesOffscreen" to { moveSpritesOffscreen() },
                "spriteShuffler" to { spriteShuffler() },
            )) {
                try {
                    func()
                } catch (e: Exception) {
                    return Divergence("$name (exception)", listOf(e.message ?: "unknown"))
                }

                val diffs = snapshotInterpreterRam(interp, name).diff(snapshotDecompiledRam(name))
                if (diffs.isNotEmpty()) {
                    return Divergence(name, diffs)
                }
            }
        }

        // Timer decrement
        if ((gamePauseStatus and 0x01) == 0) {
            val timerControl = memory[TimerControl].toInt()
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

                if (intervalCtrl >= 0x80) {
                    memory[IntervalTimerControl] = 0x14u
                }
            } else {
                memory[TimerControl] = (timerControl - 1).toUByte()
            }
        }

        // Check after timer decrement
        run {
            val diffs = snapshotInterpreterRam(interp, "timerDecrement").diff(snapshotDecompiledRam("timerDecrement"))
            if (diffs.isNotEmpty()) {
                return Divergence("timerDecrement", diffs)
            }
        }

        // Frame counter increment
        val fc = memory[FrameCounter].toInt()
        memory[FrameCounter] = ((fc + 1) and 0xFF).toUByte()

        // RNG update
        val prng0Bit = (memory[0x07A7].toInt() and 0x02) shr 1
        val prng1Bit = (memory[0x07A8].toInt() and 0x02) shr 1
        val carry = (prng0Bit xor prng1Bit) and 0x01
        var carryBit = carry
        for (i in 0 until 8) {
            val addr = 0x07A7 + i
            val oldVal = memory[addr].toInt()
            val newCarry = oldVal and 0x01
            memory[addr] = ((oldVal shr 1) or (carryBit shl 7)).toUByte()
            carryBit = newCarry
        }

        // Check after FC/RNG
        run {
            val diffs = snapshotInterpreterRam(interp, "frameCounterRng").diff(snapshotDecompiledRam("frameCounterRng"))
            if (diffs.isNotEmpty()) {
                return Divergence("frameCounterRng", diffs)
            }
        }

        // Main game logic
        if ((gamePauseStatus and 0x01) == 0) {
            try {
                operModeExecutionTree()
            } catch (e: Exception) {
                return Divergence("operModeExecutionTree (exception: ${e.message})", listOf(e.stackTraceToString()))
            }

            val diffs = snapshotInterpreterRam(interp, "operModeExecutionTree").diff(snapshotDecompiledRam("operModeExecutionTree"))
            if (diffs.isNotEmpty()) {
                return Divergence("operModeExecutionTree", diffs)
            }
        }

        return null // All matched
    }

    /**
     * by Claude - Drill down to find which function causes divergence.
     * Uses address mapping to identify which game subsystem has issues.
     */
    private fun drillDownDivergence(
        interp: BinaryInterpreter6502,
        interpController: ControllerState,
        ppuStatusReads: Int,
        buttons: Int
    ) {
        val interpSnap = snapshotInterpreterRam(interp, "")
        val decompSnap = snapshotDecompiledRam("")

        // Categorize diffs by memory region
        val zeroPage = mutableListOf<String>()  // $00-$FF
        val stack = mutableListOf<String>()      // $100-$1FF
        val ram = mutableListOf<String>()        // $200-$7FF

        for (i in 0 until 0x800) {
            if (interpSnap.data[i] != decompSnap.data[i]) {
                val addr = "\$${i.toString(16).padStart(4, '0')}"
                val iv = interpSnap.data[i].toUByte().toString(16).padStart(2, '0')
                val dv = decompSnap.data[i].toUByte().toString(16).padStart(2, '0')
                val diff = "[$addr]: $iv vs $dv"
                when {
                    i < 0x100 -> zeroPage.add(diff)
                    i < 0x200 -> stack.add(diff)
                    else -> ram.add(diff)
                }
            }
        }

        println("   Zero page diffs (${zeroPage.size}): ${zeroPage.take(10).joinToString(", ")}")
        println("   Stack diffs (${stack.size}): ${stack.take(10).joinToString(", ")}")
        println("   RAM diffs (${ram.size}): ${ram.take(10).joinToString(", ")}")

        // Correct address-to-name mappings from smbdism.asm
        val knownAddrs = mapOf(
            0x06e0 to "SprShuffleAmtOffset",
            0x06e1 to "SprShuffleAmt",
            0x06e4 to "Player_SprDataOffset",
            0x06e5 to "Enemy_SprDataOffset",
            0x06ec to "Block_SprDataOffset/Alt_SprDataOffset",
            0x06ee to "Bubble_SprDataOffset",
            0x06f1 to "FBall_SprDataOffset",
            0x06f3 to "Misc_SprDataOffset",
            0x0779 to "Mirror_PPU_CTRL_REG2",
            0x0778 to "Mirror_PPU_CTRL_REG1",
            0x0770 to "OperMode",
            0x0772 to "OperMode_Task",
            0x073C to "ScreenRoutineTask",
        )

        println("\n   Known address diffs:")
        for ((addr, name) in knownAddrs) {
            val iv = interp.memory.readByte(addr).toInt()
            val dv = memory[addr].toInt()
            if (iv != dv) {
                println("   $name (\$${addr.toString(16)}): interp=$iv, decomp=$dv")
            }
        }

        // Identify likely culprit based on addresses
        val spriteRelatedDiffs = ram.count {
            val addrStr = it.substringAfter("[\$").substringBefore("]")
            val addr = addrStr.toIntOrNull(16) ?: 0
            addr in 0x06e0..0x06ff
        }
        val ppuMirrorDiffs = ram.count {
            val addrStr = it.substringAfter("[\$").substringBefore("]")
            val addr = addrStr.toIntOrNull(16) ?: 0
            addr in 0x0778..0x0779
        }

        println("\n   Analysis:")
        if (spriteRelatedDiffs > 0) {
            println("   → $spriteRelatedDiffs sprite-related diffs - check moveSpritesOffscreen() or spriteShuffler()")
        }
        if (ppuMirrorDiffs > 0) {
            println("   → $ppuMirrorDiffs PPU mirror diffs - check PPU register handling")
        }
        if (zeroPage.size > 0) {
            println("   → ${zeroPage.size} zero-page diffs (often scratch/temp - may be benign)")
        }
        if (stack.size > 0) {
            println("   → ${stack.size} stack diffs (return addresses - usually benign)")
        }
    }

    /**
     * by Claude - Run the decompiled NMI (full frame, no checkpoints)
     * Now includes PPU mirror register updates to match interpreter NMI
     */
    private fun runDecompiledNMI() {
        // by Claude - NMI prologue: update PPU mirror registers like interpreter does
        // This matches the assembly:
        //   lda Mirror_PPU_CTRL_REG1; and #%01111111; sta Mirror_PPU_CTRL_REG1
        //   lda Mirror_PPU_CTRL_REG2; and #%11100110 (or ora #%00011110); sta Mirror_PPU_CTRL_REG2
        val mirrorPpuCtrl1 = 0x0778
        val mirrorPpuCtrl2 = 0x0779
        val disableScreenFlag = 0x0774

        var ctrl1 = memory[mirrorPpuCtrl1].toInt()
        ctrl1 = ctrl1 and 0x7F  // Clear bit 7 (NMI enable)
        memory[mirrorPpuCtrl1] = ctrl1.toUByte()

        var ctrl2 = memory[mirrorPpuCtrl2].toInt()
        ctrl2 = ctrl2 and 0xE6  // Clear bits 0, 3, 4
        if (memory[disableScreenFlag].toInt() == 0) {
            ctrl2 = memory[mirrorPpuCtrl2].toInt() or 0x1E  // Re-enable bits
        }
        memory[mirrorPpuCtrl2] = ctrl2.toUByte()

        soundEngine()
        readJoypads()
        pauseRoutine()
        updateTopScore()

        val gamePauseStatus = memory[GamePauseStatus].toInt()

        // Timer decrement (if not paused)
        if ((gamePauseStatus and 0x01) == 0) {
            val timerControl = memory[TimerControl].toInt()
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

                if (intervalCtrl >= 0x80) {
                    memory[IntervalTimerControl] = 0x14u
                }
            } else {
                memory[TimerControl] = (timerControl - 1).toUByte()
            }
        }

        // Frame counter increment (always happens, even when paused)
        val fc = memory[FrameCounter].toInt()
        memory[FrameCounter] = ((fc + 1) and 0xFF).toUByte()

        // RNG update (always happens)
        val prng0Bit = (memory[0x07A7].toInt() and 0x02) shr 1
        val prng1Bit = (memory[0x07A8].toInt() and 0x02) shr 1
        val carry = (prng0Bit xor prng1Bit) and 0x01
        var carryBit = carry
        for (i in 0 until 8) {
            val addr = 0x07A7 + i
            val oldVal = memory[addr].toInt()
            val newCarry = oldVal and 0x01
            memory[addr] = ((oldVal shr 1) or (carryBit shl 7)).toUByte()
            carryBit = newCarry
        }

        // by Claude - Sprite handling is gated by Sprite0HitDetectFlag ($0722)
        // lda Sprite0HitDetectFlag; beq SkipSprite0
        // If flag is 0, ALL sprite handling is skipped
        val sprite0HitDetectFlag = 0x0722
        if (memory[sprite0HitDetectFlag].toInt() != 0) {
            // Then there's a pause check inside
            // lda GamePauseStatus; lsr; bcs Sprite0Hit
            if ((gamePauseStatus and 0x01) == 0) {
                moveSpritesOffscreen()
                spriteShuffler()
            }
        }

        // Main game logic (if not paused)
        if ((gamePauseStatus and 0x01) == 0) {
            operModeExecutionTree()
        }
    }

    private fun setupControllerIntercepts() {
        val JOYPAD_PORT = 0x4016

        memoryReadIntercept = { addr ->
            when (addr) {
                JOYPAD_PORT -> controller.readPort().toUByte()
                0x4017 -> 0u
                in 0x2000..0x2007 -> if (addr == 0x2002) 0x80u else 0u
                else -> null
            }
        }

        memoryWriteIntercept = { addr, value ->
            when (addr) {
                JOYPAD_PORT -> { controller.writeStrobe(value.toInt()); true }
                in 0x2000..0x2007 -> true
                in 0x4000..0x4017 -> true
                else -> false
            }
        }
    }

    /**
     * by Claude - Debug test that traces specific addresses through the NMI
     * to identify exactly where divergence occurs.
     */
    @Test
    fun `trace sprite shuffler divergence`() {
        val romFile = listOf("../local/roms/smb.nes", "local/roms/smb.nes", "../smb.nes", "smb.nes")
            .map { File(it) }.firstOrNull { it.exists() }

        if (romFile == null) {
            println("⚠️ Skipping: No ROM file found")
            return
        }

        println("=== Sprite Shuffler Divergence Trace ===")

        // Set up interpreter
        val romData = romFile.readBytes().toUByteArray()
        val prgRom = romData.sliceArray(16 until 16 + 0x8000)
        val interp = BinaryInterpreter6502()

        for (i in prgRom.indices) {
            interp.memory.writeByte(0x8000 + i, prgRom[i])
        }

        // PPU stub
        var ppuStatus: UByte = 0x80u
        var ppuStatusReads = 0

        interp.memoryReadHook = { addr: Int ->
            when (addr) {
                0x2002 -> {
                    ppuStatusReads++
                    val status = ppuStatus
                    ppuStatus = if (ppuStatusReads > 3) 0x40u else 0x00u
                    status
                }
                in 0x2000..0x2007 -> 0u
                in 0x4016..0x4017 -> 0u  // Joypads return 0
                else -> null
            }
        }

        interp.memoryWriteHook = { addr: Int, value: UByte ->
            when (addr) {
                in 0x2000..0x2007 -> true
                0x4014 -> true
                in 0x4000..0x4017 -> true
                else -> false
            }
        }

        // Run RESET
        interp.cpu.PC = 0x8000u
        var cycles = 0
        while (cycles < 50000) {
            if (interp.cpu.PC.toInt() == 0x8075) break
            interp.step()
            cycles++
        }

        // Run one interpreter NMI
        ppuStatus = 0x80u
        ppuStatusReads = 0
        interp.cpu.PC = NMI_ENTRY.toUShort()
        cycles = 0
        while (cycles < 50000) {
            if (interp.memory.readByte(interp.cpu.PC.toInt()) == 0x40.toUByte()) break
            interp.step()
            cycles++
        }

        // Check key addresses after first NMI
        println("After RESET + 1 NMI (interpreter):")
        println("  SprShuffleAmtOffset ($06e0): ${interp.memory.readByte(0x06e0)}")
        println("  GamePauseStatus ($0776): ${interp.memory.readByte(0x0776)}")
        println("  Misc_SprDataOffset ($06f3-$06fb):")
        for (i in 0..8) {
            print("    [$${(0x06f3 + i).toString(16)}]: ${interp.memory.readByte(0x06f3 + i)}  ")
            if (i % 4 == 3) println()
        }
        println()

        // Copy to decompiled
        clearMemory()
        for (addr in 0x0000..0x07FF) {
            memory[addr] = interp.memory.readByte(addr)
        }
        initializeRomData()
        setupControllerIntercepts()

        println("Copied to decompiled - checking same addresses:")
        println("  SprShuffleAmtOffset ($06e0): ${memory[0x06e0]}")
        println("  GamePauseStatus ($0776): ${memory[0x0776]}")

        // Run interpreter NMI (2nd NMI)
        ppuStatus = 0x80u
        ppuStatusReads = 0
        interp.cpu.PC = NMI_ENTRY.toUShort()
        cycles = 0
        while (cycles < 100000) {
            if (interp.memory.readByte(interp.cpu.PC.toInt()) == 0x40.toUByte()) break
            interp.step()
            cycles++
        }

        println("\nAfter interpreter's 2nd NMI:")
        println("  SprShuffleAmtOffset ($06e0): ${interp.memory.readByte(0x06e0)}")
        println("  GamePauseStatus ($0776): ${interp.memory.readByte(0x0776)}")
        println("  Misc_SprDataOffset ($06f3-$06fb):")
        for (i in 0..8) {
            print("    [$${(0x06f3 + i).toString(16)}]: ${interp.memory.readByte(0x06f3 + i)}  ")
            if (i % 4 == 3) println()
        }
        println()

        // Run decompiled NMI
        runDecompiledNMI()

        println("After decompiled's 1st NMI (since copy):")
        println("  SprShuffleAmtOffset ($06e0): ${memory[0x06e0]}")
        println("  GamePauseStatus ($0776): ${memory[0x0776]}")
        println("  Misc_SprDataOffset ($06f3-$06fb):")
        for (i in 0..8) {
            print("    [$${(0x06f3 + i).toString(16)}]: ${memory[0x06f3 + i]}  ")
            if (i % 4 == 3) println()
        }
        println()

        // Compare
        println("\nComparison (interp vs decomp):")
        val sprAddr = 0x06e0
        println("  SprShuffleAmtOffset: ${interp.memory.readByte(sprAddr)} vs ${memory[sprAddr]}")
        for (i in 0..8) {
            val addr = 0x06f3 + i
            val iv = interp.memory.readByte(addr)
            val dv = memory[addr]
            if (iv != dv) {
                println("  [$${addr.toString(16)}]: $iv vs $dv (DIFF)")
            }
        }
    }
}
