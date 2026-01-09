@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.interpreter

import java.io.File
import kotlin.test.Test

/**
 * TAS test using the frame debt system to handle multi-frame subroutines.
 *
 * This test uses @FRAMES_CONSUMED markers in the assembly to determine when
 * subroutines take multiple frames to complete, and skips NMI accordingly.
 */
class FrameDebtTASTest {

    // SMB addresses (CORRECTED - SMBConstants.kt has wrong values!)
    companion object {
        const val InitializeGame = 0x8FCF  // Was incorrectly 0x90C6 in SMBConstants
        const val InitializeArea = 0x8FE4  // Was incorrectly 0x90DC in SMBConstants

        // Game state addresses
        const val OperMode = 0x0770
        const val OperMode_Task = 0x0772
        const val FrameCounter = 0x09
        const val IntervalTimerControl = 0x077F
        const val WorldNumber = 0x075F
        const val LevelNumber = 0x0760
        const val NumberofLives = 0x075A
        const val Player_X_Position = 0x0086
        const val Player_Y_Position = 0x00CE
        const val ScreenLeft_PageLoc = 0x071A
    }

    // Simple PPU stub
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

    // Simple controller
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
    fun `run TAS with frame debt system`() {
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

        // Load FCEUX RAM for comparison (optional)
        val fceuxRamFile = File("local/tas/fceux-full-ram.bin")
        val fceuxRam = if (fceuxRamFile.exists()) fceuxRamFile.readBytes() else null

        println("=== FRAME DEBT TAS TEST ===")
        println("TAS frames: ${tasInputs.size}")
        if (fceuxRam != null) {
            println("FCEUX comparison: ${fceuxRam.size / 2048} frames available")
        }

        // Set up interpreter
        val rom = NESLoader.load(romFile)
        val interp = BinaryInterpreter6502()

        // RAM initialization pattern
        for (addr in 0x0000..0x07FF) {
            val pattern = if ((addr / 4) % 2 == 0) 0x00 else 0xFF
            interp.memory.writeByte(addr, pattern.toUByte())
        }

        NESLoader.loadIntoMemory(rom, interp.memory)

        // Set up frame debt map based on @FRAMES_CONSUMED markers
        interp.frameDebtMap = mapOf(
            InitializeGame to 3,  // @FRAMES_CONSUMED: 3
            InitializeArea to 2   // @FRAMES_CONSUMED: 2
        )
        println("Frame debt map: ${interp.frameDebtMap.map { (k, v) -> "0x${k.toString(16)} → $v frames" }}")

        val ppu = SimplePPU()
        val controller = SimpleController()

        // Memory hooks
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
        var resetSteps = 0
        val mainLoopAddr = 0x8057

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
        val taskAfterReset = interp.memory.readByte(OperMode_Task).toInt()
        val modeAfterReset = interp.memory.readByte(OperMode).toInt()
        println("After reset: OperMode=$modeAfterReset, Task=$taskAfterReset")

        // Clear any debt accumulated during reset
        interp.frameDebt = 0
        interp.clearDebtTriggered()
        println("Debt cleared after reset")


        // Suppress NMI for early boot frames (before game enables NMI)
        val bootSuppressFrames = setOf(0, 1, 2, 3, 4)
        // Button offset 0 achieves TAS completion - warps work correctly
        val buttonOffset = 0

        var lastWorld = -1
        var lastLevel = -1
        var deathFrame = -1
        var divergentFrames = 0
        val milestones = mutableListOf<String>()

        val maxFrames = 18000
        for (frame in 0 until maxFrames) {
            val buttonFrame = maxOf(0, frame - buttonOffset)
            val buttons = if (buttonFrame < tasInputs.size) tasInputs[buttonFrame].buttons else 0
            controller.setButtons(buttons)

            // Check frame debt BEFORE deciding to fire NMI
            val skipDueToDebt = interp.shouldSkipNmiDueToDebt()
            val skipDueToBoot = frame in bootSuppressFrames

            var nmiSteps = 0
            val debtBefore = interp.frameDebt
            if (!skipDueToDebt && !skipDueToBoot) {
                ppu.startFrame()
                interp.clearDebtTriggered()  // Allow debt detection for this NMI
                val spBeforeNmi = interp.cpu.SP.toInt()
                interp.triggerNmi()
                interp.handleInterrupts()

                while (nmiSteps < 50000 && interp.cpu.SP.toInt() != spBeforeNmi && !interp.halted) {
                    interp.step()
                    ppu.advanceStep()
                    nmiSteps++
                }
            }
            val debtAfter = interp.frameDebt

            // Log debt changes
            if (debtAfter > debtBefore && frame < 100) {
                println("Frame $frame: Debt increased $debtBefore → $debtAfter (will skip $debtAfter frames)")
            }

            // Compare with FCEUX
            if (fceuxRam != null && frame < fceuxRam.size / 2048) {
                val offset = frame * 2048
                val fceuxFC = fceuxRam[offset + FrameCounter].toInt() and 0xFF
                val fceuxIntCtrl = fceuxRam[offset + IntervalTimerControl].toInt() and 0xFF
                val interpFC = interp.memory.readByte(FrameCounter).toInt()
                val interpIntCtrl = interp.memory.readByte(IntervalTimerControl).toInt()

                if (fceuxFC != interpFC || fceuxIntCtrl != interpIntCtrl) {
                    divergentFrames++
                    if (divergentFrames <= 20 || frame < 50) {
                        val skipReason = when {
                            skipDueToBoot -> "(boot skip)"
                            skipDueToDebt -> "(debt skip, debt=${interp.frameDebt})"
                            else -> "(NMI ran, $nmiSteps steps)"
                        }
                        println("Frame $frame DIVERGE: FC fceux=$fceuxFC interp=$interpFC, IntCtrl fceux=$fceuxIntCtrl interp=$interpIntCtrl $skipReason")
                    }
                }
            }

            // Track world/level changes
            val world = interp.memory.readByte(WorldNumber).toInt() + 1
            val level = interp.memory.readByte(LevelNumber).toInt() + 1
            if (world != lastWorld || level != lastLevel) {
                val milestone = "Frame $frame: W$world-$level"
                println(milestone)
                milestones.add(milestone)
                lastWorld = world
                lastLevel = level
            }

            // Progress every 2000 frames
            if (frame % 2000 == 0 && frame > 0) {
                val operMode = interp.memory.readByte(OperMode).toInt()
                val lives = interp.memory.readByte(NumberofLives).toInt()
                val x = interp.memory.readByte(Player_X_Position).toInt()
                val page = interp.memory.readByte(ScreenLeft_PageLoc).toInt()
                println("  Frame $frame: W$world-$level Lives=$lives X=$x Page=$page Mode=$operMode")
            }

            // Detect game over
            val operMode = interp.memory.readByte(OperMode).toInt()
            if (operMode == 3 && deathFrame == -1) {
                deathFrame = frame
                println("Frame $frame: GAME OVER")
            }

            // Detect victory
            if (operMode == 2) {
                println("Frame $frame: VICTORY!")
                milestones.add("Frame $frame: VICTORY!")
                break
            }

            if (interp.halted) break
        }

        println()
        println("=== RESULTS ===")
        println("Milestones: ${milestones.joinToString(" → ")}")
        println("Divergent frames (first 18000): $divergentFrames")
        if (deathFrame != -1) println("First death: frame $deathFrame")

        val finalWorld = interp.memory.readByte(WorldNumber).toInt() + 1
        val finalLevel = interp.memory.readByte(LevelNumber).toInt() + 1
        val finalMode = interp.memory.readByte(OperMode).toInt()
        println("Final: W$finalWorld-$finalLevel, Mode=$finalMode")

        when (finalMode) {
            2 -> println("✅ SUCCESS - TAS completed!")
            3 -> println("❌ GAME OVER")
            else -> println("⚠️ Stopped at W$finalWorld-$finalLevel")
        }
    }
}
