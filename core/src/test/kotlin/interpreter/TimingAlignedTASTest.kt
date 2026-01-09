@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.interpreter

import java.io.File
import kotlin.test.Test

/**
 * TAS test with proper timing alignment.
 *
 * FCEUX captures RAM at the START of each frame (before NMI runs).
 * So we should compare interpreter state BEFORE running each frame's NMI.
 */
class TimingAlignedTASTest {

    companion object {
        const val InitializeGame = 0x8FCF
        const val InitializeArea = 0x8FE4
        const val OperMode = 0x0770
        const val OperMode_Task = 0x0772
        const val FrameCounter = 0x09
        const val IntervalTimerControl = 0x077F
        const val WorldNumber = 0x075F
        const val LevelNumber = 0x0760
        const val NumberofLives = 0x075A
    }

    class SimplePPU {
        var ppuCtrl: UByte = 0u
        private var ppuStatus: UByte = 0x00u
        var frameSteps = 0
        val stepsPerFrame = 8509
        val sprite0HitSteps = 1200

        fun advanceStep() { frameSteps++ }

        fun read(addr: Int): UByte {
            return when (addr and 0x07) {
                0x02 -> {
                    val phaseStep = frameSteps % stepsPerFrame
                    val sprite0Hit = phaseStep >= sprite0HitSteps
                    var status = ppuStatus.toInt()
                    if (sprite0Hit) status = status or 0x40 else status = status and 0xBF
                    val inVblank = phaseStep < 800
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

        fun isNmiEnabled(): Boolean = (ppuCtrl.toInt() and 0x80) != 0
    }

    class SimpleController {
        private var player1Buttons = 0
        private var shiftReg = 0
        private var strobe = false

        fun setButtons(buttons: Int) { player1Buttons = buttons }

        fun writeStrobe(value: UByte) {
            val newStrobe = (value.toInt() and 0x01) != 0
            if (newStrobe) shiftReg = player1Buttons
            strobe = newStrobe
        }

        fun read(): UByte {
            val bit = shiftReg and 0x01
            if (!strobe) shiftReg = shiftReg shr 1
            return bit.toUByte()
        }
    }

    @Test
    fun `timing aligned comparison`() {
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

        val fceuxRamFile = File("local/tas/fceux-full-ram.bin")
        if (!fceuxRamFile.exists()) {
            println("No FCEUX RAM dump found")
            return
        }

        val tasInputs = FM2Parser.parse(tasFile)
        val fceuxRam = fceuxRamFile.readBytes()

        println("=== TIMING ALIGNED TAS TEST ===")
        println("Comparing interpreter state BEFORE NMI with FCEUX snapshot")
        println()

        // Set up interpreter
        val rom = NESLoader.load(romFile)
        val interp = BinaryInterpreter6502()

        for (addr in 0x0000..0x07FF) {
            val pattern = if ((addr / 4) % 2 == 0) 0x00 else 0xFF
            interp.memory.writeByte(addr, pattern.toUByte())
        }

        NESLoader.loadIntoMemory(rom, interp.memory)

        interp.frameDebtMap = mapOf(
            InitializeGame to 3,
            InitializeArea to 2
        )

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
                in 0x2000..0x2007 -> { ppu.write(addr, value); true }
                0x4014 -> true
                0x4016 -> { controller.writeStrobe(value); true }
                in 0x4000..0x4017 -> true
                else -> false
            }
        }

        // Reset
        interp.reset()
        val mainLoopAddr = 0x8057
        var resetSteps = 0

        while (resetSteps < 100000 && !interp.halted) {
            val pc = interp.cpu.PC.toInt()
            if (pc == mainLoopAddr) break

            val opcode = interp.memory.readByte(pc).toInt()
            val nextByte = interp.memory.readByte(pc + 1).toInt()
            val nextByte2 = interp.memory.readByte(pc + 2).toInt()

            if (opcode == 0xAD && nextByte == 0x02 && nextByte2 == 0x20) {
                ppu.startFrame()
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
                interp.cpu.PC = (pc + 5).toUShort()
                resetSteps++
                continue
            }
            interp.step()
            resetSteps++
        }
        println("Reset completed: $resetSteps steps")

        interp.frameDebt = 0
        interp.clearDebtTriggered()

        val bootSuppressFrames = setOf(0, 1, 2, 3, 4)

        // Try different button offsets to find best match
        for (buttonOffset in 3..8) {
            println()
            println("=== Button offset: $buttonOffset ===")
            testWithOffset(interp.copy(), ppu, controller, tasInputs, fceuxRam, bootSuppressFrames, buttonOffset)
        }
    }

    private fun testWithOffset(
        interp: BinaryInterpreter6502,
        ppu: SimplePPU,
        controller: SimpleController,
        tasInputs: List<FM2Parser.Frame>,
        fceuxRam: ByteArray,
        bootSuppressFrames: Set<Int>,
        buttonOffset: Int
    ) {
        var matchingFrames = 0
        var divergentFrames = 0
        var lastWorld = -1
        var lastLevel = -1
        val milestones = mutableListOf<String>()

        for (frame in 0 until 500) {
            val buttonFrame = maxOf(0, frame - buttonOffset)
            val tasFrame = if (buttonFrame < tasInputs.size) tasInputs[buttonFrame] else null
            controller.setButtons(tasFrame?.buttons ?: 0)

            // Compare BEFORE running NMI (this matches FCEUX snapshot timing)
            val fcBefore = interp.memory.readByte(FrameCounter).toInt()
            val intCtrlBefore = interp.memory.readByte(IntervalTimerControl).toInt()
            val modeBefore = interp.memory.readByte(OperMode).toInt()
            val taskBefore = interp.memory.readByte(OperMode_Task).toInt()

            // Compare with FCEUX
            val offset = frame * 2048
            val fceuxFC = fceuxRam[offset + FrameCounter].toInt() and 0xFF
            val fceuxIntCtrl = fceuxRam[offset + IntervalTimerControl].toInt() and 0xFF
            val fceuxMode = fceuxRam[offset + OperMode].toInt() and 0xFF
            val fceuxTask = fceuxRam[offset + OperMode_Task].toInt() and 0xFF

            val fcMatch = fcBefore == fceuxFC
            val intCtrlMatch = intCtrlBefore == fceuxIntCtrl
            val modeMatch = modeBefore == fceuxMode
            val taskMatch = taskBefore == fceuxTask

            if (fcMatch && intCtrlMatch && modeMatch && taskMatch) {
                matchingFrames++
            } else {
                divergentFrames++
                if (divergentFrames <= 10 || frame < 50) {
                    println("  Frame $frame: FC=$fcBefore(fceux=$fceuxFC) IntCtrl=$intCtrlBefore(fceux=$fceuxIntCtrl) Mode=$modeBefore(fceux=$fceuxMode) Task=$taskBefore(fceux=$fceuxTask)")
                }
            }

            // Run NMI
            val skipDueToDebt = interp.shouldSkipNmiDueToDebt()
            val skipDueToBoot = frame in bootSuppressFrames

            if (!skipDueToDebt && !skipDueToBoot) {
                ppu.startFrame()
                interp.clearDebtTriggered()
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

            // Track progress
            val world = interp.memory.readByte(WorldNumber).toInt() + 1
            val level = interp.memory.readByte(LevelNumber).toInt() + 1
            if (world != lastWorld || level != lastLevel) {
                milestones.add("Frame $frame: W$world-$level")
                lastWorld = world
                lastLevel = level
            }

            val operMode = interp.memory.readByte(OperMode).toInt()
            if (operMode == 3) break  // Game over
        }

        println("  Matching frames: $matchingFrames, Divergent: $divergentFrames")
        println("  Milestones: ${milestones.joinToString(" → ")}")
    }

    // Extension to copy interpreter state
    private fun BinaryInterpreter6502.copy(): BinaryInterpreter6502 {
        val copy = BinaryInterpreter6502()
        // Copy memory
        for (addr in 0x0000..0xFFFF) {
            try {
                copy.memory.writeByte(addr, this.memory.readByte(addr))
            } catch (e: Exception) {}
        }
        // Copy CPU state
        copy.cpu.A = this.cpu.A
        copy.cpu.X = this.cpu.X
        copy.cpu.Y = this.cpu.Y
        copy.cpu.SP = this.cpu.SP
        copy.cpu.PC = this.cpu.PC
        copy.cpu.N = this.cpu.N
        copy.cpu.V = this.cpu.V
        copy.cpu.B = this.cpu.B
        copy.cpu.D = this.cpu.D
        copy.cpu.I = this.cpu.I
        copy.cpu.Z = this.cpu.Z
        copy.cpu.C = this.cpu.C
        copy.frameDebtMap = this.frameDebtMap
        copy.frameDebt = this.frameDebt
        copy.memoryReadHook = this.memoryReadHook
        copy.memoryWriteHook = this.memoryWriteHook
        return copy
    }
}
