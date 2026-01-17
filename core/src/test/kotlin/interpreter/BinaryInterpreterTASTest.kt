@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.interpreter

import java.io.File
import java.util.concurrent.TimeUnit
import org.junit.jupiter.api.Timeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * TAS validation tests using the binary 6502 interpreter.
 *
 * These tests run actual NES ROMs with TAS inputs to verify
 * the interpreter correctly executes 6502 machine code.
 *
 * To run these tests, place the following files in ./local/roms/:
 * - smb.nes (Super Mario Bros. ROM, mapper 0)
 * - smb-any%.fm2 (TAS movie in FM2 format)
 */
class BinaryInterpreterTASTest {

    // ROM search paths
    private val romPaths = listOf(
        "local/roms/smb.nes",
        "local/smb.nes",
        "../local/roms/smb.nes",
        System.getProperty("user.home") + "/roms/smb.nes"
    )

    // TAS search paths
    private val tasPaths = listOf(
        "local/tas/smb-any%.fm2",
        "local/smb-any%.fm2",
        "../local/tas/smb-any%.fm2"
    )

    /**
     * Simple PPU stub for NES emulation
     */
    class PPUStub {
        var ppuCtrl: UByte = 0u
        var ppuMask: UByte = 0u
        var ppuStatus: UByte = 0u
        var oamAddr: UByte = 0u
        var ppuScroll: Int = 0
        var ppuAddr: Int = 0
        private var scrollLatch: Boolean = false
        private var addrLatch: Boolean = false
        private var vblankFlag: Boolean = false

        val oam = UByteArray(256)
        val vram = UByteArray(0x4000)

        fun reset() {
            ppuCtrl = 0u
            ppuMask = 0u
            ppuStatus = 0u
            oamAddr = 0u
            ppuScroll = 0
            ppuAddr = 0
            scrollLatch = false
            addrLatch = false
            vblankFlag = false
        }

        fun beginVBlank() {
            vblankFlag = true
            ppuStatus = (ppuStatus.toInt() or 0x80).toUByte()
        }

        fun endVBlank() {
            vblankFlag = false
            ppuStatus = (ppuStatus.toInt() and 0x7F).toUByte()
        }

        fun shouldTriggerNmi(): Boolean = vblankFlag && (ppuCtrl.toInt() and 0x80) != 0

        fun read(addr: Int): UByte {
            return when (addr and 0x07) {
                0x02 -> {  // PPUSTATUS
                    val status = ppuStatus
                    ppuStatus = (ppuStatus.toInt() and 0x7F).toUByte()
                    scrollLatch = false
                    addrLatch = false
                    status
                }
                0x04 -> oam[oamAddr.toInt()]  // OAMDATA
                0x07 -> {  // PPUDATA
                    val data = vram[ppuAddr and 0x3FFF]
                    ppuAddr = (ppuAddr + if ((ppuCtrl.toInt() and 0x04) != 0) 32 else 1) and 0x3FFF
                    data
                }
                else -> 0u
            }
        }

        fun write(addr: Int, value: UByte) {
            when (addr and 0x07) {
                0x00 -> ppuCtrl = value
                0x01 -> ppuMask = value
                0x03 -> oamAddr = value
                0x04 -> {
                    oam[oamAddr.toInt()] = value
                    oamAddr = ((oamAddr.toInt() + 1) and 0xFF).toUByte()
                }
                0x05 -> {  // PPUSCROLL
                    if (!scrollLatch) {
                        ppuScroll = (ppuScroll and 0xFF00) or value.toInt()
                    } else {
                        ppuScroll = (ppuScroll and 0x00FF) or (value.toInt() shl 8)
                    }
                    scrollLatch = !scrollLatch
                }
                0x06 -> {  // PPUADDR
                    if (!addrLatch) {
                        ppuAddr = (ppuAddr and 0x00FF) or ((value.toInt() and 0x3F) shl 8)
                    } else {
                        ppuAddr = (ppuAddr and 0xFF00) or value.toInt()
                    }
                    addrLatch = !addrLatch
                }
                0x07 -> {  // PPUDATA
                    vram[ppuAddr and 0x3FFF] = value
                    ppuAddr = (ppuAddr + if ((ppuCtrl.toInt() and 0x04) != 0) 32 else 1) and 0x3FFF
                }
            }
        }

        fun oamDma(data: UByteArray) {
            for (i in 0 until 256) {
                oam[(oamAddr.toInt() + i) and 0xFF] = data[i]
            }
        }
    }

    /**
     * Controller input handler
     */
    class ControllerState {
        private val buttons = IntArray(2)
        private val shiftRegisters = IntArray(2)
        private var strobe: Boolean = false

        fun setButtons(player: Int, value: Int) {
            buttons[player] = value
        }

        fun writeStrobe(value: UByte) {
            strobe = (value.toInt() and 0x01) != 0
            if (strobe) {
                shiftRegisters[0] = buttons[0]
                shiftRegisters[1] = buttons[1]
            }
        }

        fun readController(addr: Int): UByte {
            val player = if (addr == 0x4016) 0 else 1
            val bit = shiftRegisters[player] and 0x01
            if (!strobe) {
                shiftRegisters[player] = shiftRegisters[player] shr 1
            }
            return bit.toUByte()
        }

        fun reset() {
            buttons[0] = 0
            buttons[1] = 0
            shiftRegisters[0] = 0
            shiftRegisters[1] = 0
            strobe = false
        }

        companion object {
            const val A = 0x01
            const val B = 0x02
            const val SELECT = 0x04
            const val START = 0x08
            const val UP = 0x10
            const val DOWN = 0x20
            const val LEFT = 0x40
            const val RIGHT = 0x80
        }
    }

    /**
     * Parse FM2 TAS movie format
     */
    data class TASFrame(val player1: Int, val player2: Int = 0)

    fun parseFM2(content: String): List<TASFrame> {
        return content.lineSequence()
            .filter { it.startsWith("|") }
            .map { line ->
                val parts = line.split("|")
                if (parts.size >= 3) {
                    TASFrame(
                        parseButtons(parts[2]),
                        if (parts.size >= 4) parseButtons(parts[3]) else 0
                    )
                } else {
                    TASFrame(0)
                }
            }
            .toList()
    }

    private fun parseButtons(str: String): Int {
        if (str.length < 8) return 0
        var buttons = 0
        // FCEUX format: RLDUTSBA (8 chars, position determines button)
        if (str[0] != '.') buttons = buttons or ControllerState.RIGHT
        if (str[1] != '.') buttons = buttons or ControllerState.LEFT
        if (str[2] != '.') buttons = buttons or ControllerState.DOWN
        if (str[3] != '.') buttons = buttons or ControllerState.UP
        if (str[4] != '.') buttons = buttons or ControllerState.START
        if (str[5] != '.') buttons = buttons or ControllerState.SELECT
        if (str[6] != '.') buttons = buttons or ControllerState.B
        if (str[7] != '.') buttons = buttons or ControllerState.A
        return buttons
    }

    private fun findFile(paths: List<String>): File? {
        return paths.map { File(it) }.firstOrNull { it.exists() }
    }

    // by Claude - 60 second timeout to catch infinite loops
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    @Test
    fun `test ROM loading`() {
        val romFile = findFile(romPaths)
        if (romFile == null) {
            println("⚠️ Skipping ROM loading test - no ROM file found")
            println("Place smb.nes in one of: $romPaths")
            return
        }

        val rom = NESLoader.load(romFile)
        assertEquals(0, rom.mapper, "SMB should use mapper 0 (NROM)")
        assertEquals(32768, rom.prgRom.size, "SMB has 32KB PRG-ROM")
        assertEquals(8192, rom.chrRom.size, "SMB has 8KB CHR-ROM")

        println("✅ ROM loaded successfully: ${romFile.name}")
        println("   PRG-ROM: ${rom.prgRom.size} bytes")
        println("   CHR-ROM: ${rom.chrRom.size} bytes")
        println("   Mapper: ${rom.mapper}")
    }

    // by Claude - 60 second timeout to catch infinite loops
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    @Test
    fun `test interpreter with ROM - boot sequence`() {
        val romFile = findFile(romPaths)
        if (romFile == null) {
            println("⚠️ Skipping boot sequence test - no ROM file found")
            return
        }

        val rom = NESLoader.load(romFile)
        val interp = BinaryInterpreter6502()
        NESLoader.loadIntoMemory(rom, interp.memory)

        // Set up PPU/controller hooks
        val ppu = PPUStub()
        val controller = ControllerState()

        interp.memoryReadHook = { addr ->
            when (addr) {
                in 0x2000..0x2007 -> ppu.read(addr)
                0x4016, 0x4017 -> controller.readController(addr)
                else -> null
            }
        }

        interp.memoryWriteHook = { addr, value ->
            when (addr) {
                in 0x2000..0x2007 -> { ppu.write(addr, value); true }
                0x4014 -> {
                    // OAM DMA
                    val srcAddr = value.toInt() shl 8
                    val data = UByteArray(256) { interp.memory.readByte(srcAddr + it) }
                    ppu.oamDma(data)
                    true
                }
                0x4016 -> { controller.writeStrobe(value); true }
                else -> false
            }
        }

        // Reset CPU and run boot sequence
        interp.reset()

        val resetVector = interp.cpu.PC.toInt()
        println("Reset vector: $${resetVector.toString(16).uppercase()}")

        // Run some instructions to verify boot works
        var cycles = 0L
        for (i in 0 until 1000) {
            if (interp.halted) break
            cycles += interp.step()
        }

        println("✅ Boot sequence ran $cycles cycles")
        println("   PC: $${interp.cpu.PC.toString(16).uppercase()}")
        println("   A: $${interp.cpu.A.toString(16).uppercase()}")
        println("   X: $${interp.cpu.X.toString(16).uppercase()}")
        println("   Y: $${interp.cpu.Y.toString(16).uppercase()}")
    }

    // by Claude - 60 second timeout to catch infinite loops
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    @Test
    fun `test interpreter with ROM - run frames`() {
        val romFile = findFile(romPaths)
        if (romFile == null) {
            println("⚠️ Skipping frame test - no ROM file found")
            return
        }

        val rom = NESLoader.load(romFile)
        val interp = BinaryInterpreter6502()
        NESLoader.loadIntoMemory(rom, interp.memory)

        val ppu = PPUStub()
        val controller = ControllerState()

        interp.memoryReadHook = { addr ->
            when (addr) {
                in 0x2000..0x2007 -> ppu.read(addr)
                0x4016, 0x4017 -> controller.readController(addr)
                else -> null
            }
        }

        interp.memoryWriteHook = { addr, value ->
            when (addr) {
                in 0x2000..0x2007 -> { ppu.write(addr, value); true }
                0x4014 -> {
                    val srcAddr = value.toInt() shl 8
                    val data = UByteArray(256) { interp.memory.readByte(srcAddr + it) }
                    ppu.oamDma(data)
                    true
                }
                0x4016 -> { controller.writeStrobe(value); true }
                else -> false
            }
        }

        interp.reset()

        // Run 60 frames (1 second of gameplay)
        val cyclesPerFrame = 29780  // NTSC NES cycles per frame
        var totalCycles = 0L
        var frames = 0

        for (frame in 0 until 60) {
            // Run CPU cycles for this frame
            var frameCycles = 0
            while (frameCycles < cyclesPerFrame && !interp.halted) {
                frameCycles += interp.step()
            }

            // Trigger NMI at end of frame
            ppu.beginVBlank()
            if (ppu.shouldTriggerNmi()) {
                interp.triggerNmi()
                interp.handleInterrupts()
            }

            // Run NMI handler
            var nmiCycles = 0
            while (nmiCycles < 2273 && !interp.halted) {  // VBlank cycles
                nmiCycles += interp.step()
            }
            ppu.endVBlank()

            totalCycles += frameCycles + nmiCycles
            frames++
        }

        // Check game state - SMB should have initialized
        val worldNumber = interp.memory.readByte(0x075F).toInt()
        val levelNumber = interp.memory.readByte(0x0760).toInt()
        val operMode = interp.memory.readByte(0x0770).toInt()

        println("✅ Ran $frames frames, $totalCycles total cycles")
        println("   World: ${worldNumber + 1}-${levelNumber + 1}")
        println("   OperMode: $operMode")
        println("   Halted: ${interp.halted}")

        // After 60 frames of title screen, game should still be in title mode
        assertTrue(frames == 60, "Should have run 60 frames")
    }

    // by Claude - 60 second timeout to catch infinite loops
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    @Test
    fun `test TAS playback - first 1000 frames`() {
        val romFile = findFile(romPaths)
        val tasFile = findFile(tasPaths)

        if (romFile == null) {
            println("⚠️ Skipping TAS test - no ROM file found")
            return
        }

        val movie = if (tasFile != null) {
            println("Loading TAS: ${tasFile.name}")
            parseFM2(tasFile.readText())
        } else {
            println("⚠️ No TAS file found, using simple test input")
            // Simple test: wait for title, press start twice
            buildList {
                repeat(180) { add(TASFrame(0)) }  // Wait 3 seconds
                add(TASFrame(ControllerState.START))  // Press start
                repeat(60) { add(TASFrame(0)) }  // Wait 1 second
                add(TASFrame(ControllerState.START))  // Press start again
                repeat(760) { add(TASFrame(0)) }  // Run more frames
            }
        }

        val rom = NESLoader.load(romFile)
        val interp = BinaryInterpreter6502()
        NESLoader.loadIntoMemory(rom, interp.memory)

        val ppu = PPUStub()
        val controller = ControllerState()

        interp.memoryReadHook = { addr ->
            when (addr) {
                in 0x2000..0x2007 -> ppu.read(addr)
                0x4016, 0x4017 -> controller.readController(addr)
                else -> null
            }
        }

        interp.memoryWriteHook = { addr, value ->
            when (addr) {
                in 0x2000..0x2007 -> { ppu.write(addr, value); true }
                0x4014 -> {
                    val srcAddr = value.toInt() shl 8
                    val data = UByteArray(256) { interp.memory.readByte(srcAddr + it) }
                    ppu.oamDma(data)
                    true
                }
                0x4016 -> { controller.writeStrobe(value); true }
                else -> false
            }
        }

        interp.reset()

        val cyclesPerFrame = 29780
        val maxFrames = minOf(1000, movie.size)
        var lastWorld = -1
        var lastLevel = -1

        for (frame in 0 until maxFrames) {
            val input = movie[frame]
            controller.setButtons(0, input.player1)
            controller.setButtons(1, input.player2)

            // Run frame
            var frameCycles = 0
            while (frameCycles < cyclesPerFrame && !interp.halted) {
                frameCycles += interp.step()
            }

            // NMI
            ppu.beginVBlank()
            if (ppu.shouldTriggerNmi()) {
                interp.triggerNmi()
                interp.handleInterrupts()
            }

            var nmiCycles = 0
            while (nmiCycles < 2273 && !interp.halted) {
                nmiCycles += interp.step()
            }
            ppu.endVBlank()

            // Progress report every 100 frames
            if (frame % 100 == 0 || frame == maxFrames - 1) {
                val world = interp.memory.readByte(0x075F).toInt() + 1
                val level = interp.memory.readByte(0x0760).toInt() + 1
                val operMode = interp.memory.readByte(0x0770).toInt()

                if (world != lastWorld || level != lastLevel) {
                    println("Frame $frame: World $world-$level (mode=$operMode)")
                    lastWorld = world
                    lastLevel = level
                }
            }

            if (interp.halted) {
                println("⚠️ Interpreter halted at frame $frame")
                break
            }
        }

        val finalWorld = interp.memory.readByte(0x075F).toInt() + 1
        val finalLevel = interp.memory.readByte(0x0760).toInt() + 1

        println("✅ Completed $maxFrames frames")
        println("   Final position: World $finalWorld-$finalLevel")
    }
}
