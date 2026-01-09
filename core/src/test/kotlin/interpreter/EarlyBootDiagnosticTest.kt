@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.interpreter

import java.io.File
import kotlin.test.Test

/**
 * Detailed diagnostic for early boot frames to understand divergence.
 */
class EarlyBootDiagnosticTest {

    companion object {
        const val InitializeGame = 0x8FCF
        const val InitializeArea = 0x8FE4
        const val OperMode = 0x0770
        const val OperMode_Task = 0x0772
        const val FrameCounter = 0x09
        const val IntervalTimerControl = 0x077F
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
    fun `detailed early boot frame analysis`() {
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
        val fceuxRam = fceuxRamFile.readBytes()

        println("=== EARLY BOOT DIAGNOSTIC ===")
        println("Analyzing first 50 frames in detail")
        println()

        // First, just look at FCEUX data for early frames
        println("=== FCEUX REFERENCE DATA ===")
        println("Frame | FC | IntCtrl | OperMode | Task")
        for (frame in 0 until 60) {
            val offset = frame * 2048
            val fc = fceuxRam[offset + FrameCounter].toInt() and 0xFF
            val intCtrl = fceuxRam[offset + IntervalTimerControl].toInt() and 0xFF
            val opMode = fceuxRam[offset + OperMode].toInt() and 0xFF
            val task = fceuxRam[offset + OperMode_Task].toInt() and 0xFF
            println("  %3d |  %d |   %3d   |    %d     |  %d".format(frame, fc, intCtrl, opMode, task))
        }

        println()
        println("=== INTERPRETER RUN ===")

        // Set up interpreter
        val rom = NESLoader.load(romFile)
        val interp = BinaryInterpreter6502()

        // RAM initialization pattern
        for (addr in 0x0000..0x07FF) {
            val pattern = if ((addr / 4) % 2 == 0) 0x00 else 0xFF
            interp.memory.writeByte(addr, pattern.toUByte())
        }

        NESLoader.loadIntoMemory(rom, interp.memory)

        // Frame debt map
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

        // Clear debt from reset
        interp.frameDebt = 0
        interp.clearDebtTriggered()

        println()
        println("Frame | FC | IntCtrl | Mode | Task | Debt | Action")
        println("------|----|---------|----- |------|------|-------")

        val bootSuppressFrames = setOf(0, 1, 2, 3, 4)

        for (frame in 0 until 60) {
            controller.setButtons(0)

            val skipDueToDebt = interp.shouldSkipNmiDueToDebt()
            val skipDueToBoot = frame in bootSuppressFrames

            val debtBefore = interp.frameDebt
            var nmiSteps = 0
            var action = ""

            if (skipDueToBoot) {
                action = "boot-skip"
            } else if (skipDueToDebt) {
                action = "debt-skip (debt=$debtBefore)"
            } else {
                ppu.startFrame()
                interp.clearDebtTriggered()
                val spBeforeNmi = interp.cpu.SP.toInt()
                interp.triggerNmi()
                interp.handleInterrupts()

                while (nmiSteps < 50000 && interp.cpu.SP.toInt() != spBeforeNmi && !interp.halted) {
                    interp.step()
                    ppu.advanceStep()
                    nmiSteps++
                }
                action = "NMI ($nmiSteps steps)"
            }

            val debtAfter = interp.frameDebt
            if (debtAfter > debtBefore) {
                action += " → DEBT+${debtAfter - debtBefore}"
            }

            val fc = interp.memory.readByte(FrameCounter).toInt()
            val intCtrl = interp.memory.readByte(IntervalTimerControl).toInt()
            val opMode = interp.memory.readByte(OperMode).toInt()
            val task = interp.memory.readByte(OperMode_Task).toInt()

            // Compare with FCEUX
            val offset = frame * 2048
            val fceuxFC = fceuxRam[offset + FrameCounter].toInt() and 0xFF
            val fceuxIntCtrl = fceuxRam[offset + IntervalTimerControl].toInt() and 0xFF

            val match = if (fc == fceuxFC && intCtrl == fceuxIntCtrl) "✓" else "✗"

            println("  %3d |  %d |   %3d   |  %d   |  %d   |  %d   | %s %s (fceux: FC=%d IntCtrl=%d)".format(
                frame, fc, intCtrl, opMode, task, debtAfter, action, match, fceuxFC, fceuxIntCtrl
            ))
        }
    }
}
