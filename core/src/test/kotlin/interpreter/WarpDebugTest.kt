@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.interpreter

import java.io.File
import kotlin.test.Test

/**
 * Debug test focused on the warp zone in W1-2.
 * Tracks Mario's position and warp state during critical frames.
 */
class WarpDebugTest {

    companion object {
        const val InitializeGame = 0x8FCF
        const val InitializeArea = 0x8FE4

        // Game state
        const val OperMode = 0x0770
        const val OperMode_Task = 0x0772
        const val FrameCounter = 0x09
        const val IntervalTimerControl = 0x077F
        const val WorldNumber = 0x075F
        const val LevelNumber = 0x0760

        // Player state
        const val Player_X_Position = 0x0086
        const val Player_Y_Position = 0x00CE
        const val Player_PageLoc = 0x006D
        const val Player_State = 0x001D

        // Warp state
        const val WarpZoneControl = 0x06D6
        const val AreaType = 0x074E
        const val ScreenLeft_PageLoc = 0x071A
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
    fun `debug warp zone`() {
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
        val fceuxRam = if (fceuxRamFile.exists()) fceuxRamFile.readBytes() else null

        val tasInputs = FM2Parser.parse(tasFile)
        println("=== WARP DEBUG TEST ===")
        println("TAS frames: ${tasInputs.size}")

        // Set up interpreter
        val rom = NESLoader.load(romFile)
        val interp = BinaryInterpreter6502()

        for (addr in 0x0000..0x07FF) {
            val pattern = if ((addr / 4) % 2 == 0) 0x00 else 0xFF
            interp.memory.writeByte(addr, pattern.toUByte())
        }

        NESLoader.loadIntoMemory(rom, interp.memory)
        interp.frameDebtMap = mapOf(InitializeGame to 3, InitializeArea to 2)

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
        println("Reset completed")

        interp.frameDebt = 0
        interp.clearDebtTriggered()

        val bootSuppressFrames = setOf(0, 1, 2, 3, 4)

        // Button offset 0 works best for warps
        println()
        println("=== Testing full TAS with button offset: 0 ===")
        runFullTAS(interp.copy(), ppu, tasInputs, fceuxRam, bootSuppressFrames, 0)
    }

    private fun runWithOffset(
        interp: BinaryInterpreter6502,
        ppu: SimplePPU,
        tasInputs: List<FM2Parser.Frame>,
        fceuxRam: ByteArray?,
        bootSuppressFrames: Set<Int>,
        buttonOffset: Int
    ) {
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

        // Reset debt state for this run
        interp.frameDebt = 0
        interp.clearDebtTriggered()

        var lastWorld = -1
        var lastLevel = -1
        var inW12 = false
        var warpFound = false

        for (frame in 0 until 5000) {
            val buttonFrame = maxOf(0, frame - buttonOffset)
            val buttons = if (buttonFrame < tasInputs.size) tasInputs[buttonFrame].buttons else 0
            controller.setButtons(buttons)

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

            val world = interp.memory.readByte(WorldNumber).toInt() + 1
            val level = interp.memory.readByte(LevelNumber).toInt() + 1
            val x = interp.memory.readByte(Player_X_Position).toInt()
            val y = interp.memory.readByte(Player_Y_Position).toInt()
            val page = interp.memory.readByte(Player_PageLoc).toInt()
            val warpCtrl = interp.memory.readByte(WarpZoneControl).toInt()
            val screenPage = interp.memory.readByte(ScreenLeft_PageLoc).toInt()
            val operMode = interp.memory.readByte(OperMode).toInt()

            // Check FCEUX for comparison
            var fceuxWorld = -1
            var fceuxLevel = -1
            var fceuxWarp = -1
            if (fceuxRam != null && frame < fceuxRam.size / 2048) {
                val offset = frame * 2048
                fceuxWorld = (fceuxRam[offset + WorldNumber].toInt() and 0xFF) + 1
                fceuxLevel = (fceuxRam[offset + LevelNumber].toInt() and 0xFF) + 1
                fceuxWarp = fceuxRam[offset + WarpZoneControl].toInt() and 0xFF
            }

            if (world != lastWorld || level != lastLevel) {
                val fceuxStr = if (fceuxWorld > 0) " (FCEUX: W$fceuxWorld-$fceuxLevel)" else ""
                println("Frame $frame: Entered W$world-$level$fceuxStr")

                // Track warps (based on FCEUX dump: 1-3 → 4-1, 4-2 → 8-1)
                if (lastWorld == 1 && lastLevel == 3 && world == 4 && level == 1) {
                    println("  ✓ WARP SUCCESSFUL! W1-3 → W4-1")
                    warpFound = true
                } else if (lastWorld == 4 && lastLevel == 2 && world == 8 && level == 1) {
                    println("  ✓ WARP SUCCESSFUL! W4-2 → W8-1")
                }

                lastWorld = world
                lastLevel = level
            }

            // Log detailed state when in W1-2 near the warp zone
            if (world == 1 && level == 2 && page >= 9 && operMode == 1) {
                val fceuxWarpStr = if (fceuxWarp >= 0) " (fceux=$fceuxWarp)" else ""
                if (warpCtrl != 0 || frame % 20 == 0) {
                    println("  Frame $frame: Page=$page X=$x Y=$y WarpCtrl=$warpCtrl$fceuxWarpStr Btns=${buttonString(buttons)}")
                }
            }

            if (operMode == 3) {
                println("Frame $frame: GAME OVER")
                break
            }

            if (warpFound) break
        }

        if (!warpFound) {
            println("  Warp not found in first 3000 frames")
        }
    }

    private fun runFullTAS(
        interp: BinaryInterpreter6502,
        ppu: SimplePPU,
        tasInputs: List<FM2Parser.Frame>,
        fceuxRam: ByteArray?,
        bootSuppressFrames: Set<Int>,
        buttonOffset: Int
    ) {
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

        interp.frameDebt = 0
        interp.clearDebtTriggered()

        var lastWorld = -1
        var lastLevel = -1
        val milestones = mutableListOf<String>()
        var deathFrame = -1

        val maxFrames = 18500
        for (frame in 0 until maxFrames) {
            val buttonFrame = maxOf(0, frame - buttonOffset)
            val buttons = if (buttonFrame < tasInputs.size) tasInputs[buttonFrame].buttons else 0
            controller.setButtons(buttons)

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

            val world = interp.memory.readByte(WorldNumber).toInt() + 1
            val level = interp.memory.readByte(LevelNumber).toInt() + 1
            val operMode = interp.memory.readByte(OperMode).toInt()

            // Check FCEUX for comparison
            var fceuxWorld = -1
            var fceuxLevel = -1
            if (fceuxRam != null && frame < fceuxRam.size / 2048) {
                val offset = frame * 2048
                fceuxWorld = (fceuxRam[offset + WorldNumber].toInt() and 0xFF) + 1
                fceuxLevel = (fceuxRam[offset + LevelNumber].toInt() and 0xFF) + 1
            }

            if (world != lastWorld || level != lastLevel) {
                val fceuxStr = if (fceuxWorld > 0 && fceuxWorld < 256) " (FCEUX: W$fceuxWorld-$fceuxLevel)" else ""
                val milestone = "Frame $frame: W$world-$level$fceuxStr"
                println(milestone)
                milestones.add("W$world-$level@$frame")
                lastWorld = world
                lastLevel = level
            }

            // Progress every 2000 frames
            if (frame % 2000 == 0 && frame > 0) {
                val lives = interp.memory.readByte(0x075A).toInt()
                val page = interp.memory.readByte(0x006D).toInt()
                println("  Frame $frame: W$world-$level Lives=$lives Page=$page Mode=$operMode")
            }

            // Game over detection
            if (operMode == 3 && deathFrame == -1) {
                deathFrame = frame
                println("Frame $frame: GAME OVER")
            }

            // Victory detection
            if (operMode == 2) {
                println("Frame $frame: VICTORY!")
                break
            }

            if (interp.halted) break
        }

        println()
        println("=== FINAL RESULTS ===")
        println("Milestones: ${milestones.joinToString(" → ")}")
        if (deathFrame != -1) println("First death: frame $deathFrame")
        val finalMode = interp.memory.readByte(OperMode).toInt()
        when (finalMode) {
            2 -> println("✅ SUCCESS - TAS completed!")
            3 -> println("❌ GAME OVER")
            else -> println("⚠️ Stopped at W$lastWorld-$lastLevel")
        }
    }

    private fun buttonString(buttons: Int): String {
        val sb = StringBuilder()
        if (buttons and 0x01 != 0) sb.append("A")
        if (buttons and 0x02 != 0) sb.append("B")
        if (buttons and 0x04 != 0) sb.append("Sel")
        if (buttons and 0x08 != 0) sb.append("Sta")
        if (buttons and 0x10 != 0) sb.append("U")
        if (buttons and 0x20 != 0) sb.append("D")
        if (buttons and 0x40 != 0) sb.append("L")
        if (buttons and 0x80 != 0) sb.append("R")
        return if (sb.isEmpty()) "-" else sb.toString()
    }

    // Extension to copy interpreter state
    private fun BinaryInterpreter6502.copy(): BinaryInterpreter6502 {
        val copy = BinaryInterpreter6502()
        for (addr in 0x0000..0xFFFF) {
            try { copy.memory.writeByte(addr, this.memory.readByte(addr)) } catch (e: Exception) {}
        }
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
        return copy
    }
}
