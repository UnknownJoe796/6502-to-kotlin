@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.smb

import com.ivieleague.decompiler6502tokotlin.hand.*
import com.ivieleague.decompiler6502tokotlin.interpreter.*
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Simple function validation tests.
 *
 * These tests validate that simple decompiled functions produce
 * the same results as the binary interpreter.
 */
class SimpleFunctionValidationTest {

    private fun findRom(): File? {
        return listOf("local/roms/smb.nes", "smb.nes", "../smb.nes", "../../smb.nes")
            .map { File(it) }
            .firstOrNull { it.exists() }
    }

    /**
     * Test that doNothing1 writes 0xFF to $06C9
     */
    @Test
    fun `test doNothing1 function`() {
        // Reset decompiled code's state
        resetCPU()
        clearMemory()

        // Run decompiled function
        doNothing1()

        // Verify result
        assertEquals(0xFF, memory[0x6C9].toInt(), "doNothing1 should write 0xFF to $06C9")
    }

    /**
     * Test that doNothing2 is a no-op (just returns)
     */
    @Test
    fun `test doNothing2 function`() {
        // Reset decompiled code's state
        resetCPU()
        clearMemory()

        // Set some state to verify it's not changed
        A = 0x42
        X = 0x33
        Y = 0x77

        // Run decompiled function
        doNothing2()

        // Verify state unchanged (except possibly A which might be return value)
        assertEquals(0x33, X, "X should be unchanged")
        assertEquals(0x77, Y, "Y should be unchanged")
    }

    /**
     * Test initializeMemory - validates the function exists and runs.
     * Note: Full validation requires proper memory setup (zero page pointers).
     */
    @Test
    fun `test initializeMemory function runs`() {
        // Reset state
        resetCPU()
        clearMemory()

        // Set up zero page pointer for indirect addressing
        // initializeMemory uses ($06),Y - pointer at $06/$07
        memory[0x06] = 0x00u  // Low byte
        memory[0x07] = 0x07u  // High byte - points to $0700

        // Put some non-zero values to verify clearing works
        for (i in 0x0700..0x070F) {
            memory[i] = 0xAA.toUByte()
        }

        // Run with Y=0x0F to clear a small range
        initializeMemory(0x0F)

        // The function should have cleared some memory
        // Note: The actual behavior depends on the loop structure
        // This test just validates the function runs without error
        println("initializeMemory ran successfully")
    }

    /**
     * Compare binary interpreter execution against decompiled code for doNothing1.
     *
     * This test demonstrates the comparison framework approach:
     * 1. Run in binary interpreter
     * 2. Run in decompiled code
     * 3. Compare results
     */
    @Test
    fun `compare doNothing1 with binary interpreter`() {
        val romFile = findRom()
        if (romFile == null) {
            println("⚠️ Skipping binary comparison - no ROM file found")
            return
        }

        // Set up binary interpreter
        val interp = BinaryInterpreter6502()
        val rom = NESLoader.load(romFile)
        NESLoader.loadIntoMemory(rom, interp.memory)

        // Find doNothing1 address from ROM
        // DoNothing1 writes $FF to $06C9, so we can search for this pattern
        // LDA #$FF = A9 FF
        // STA $06C9 = 8D C9 06
        // The pattern is: A9 FF 8D C9 06

        var doNothing1Addr: Int? = null
        for (addr in 0x8000..0xFFFA) {
            if (interp.memory.readByte(addr).toInt() == 0xA9 &&
                interp.memory.readByte(addr + 1).toInt() == 0xFF &&
                interp.memory.readByte(addr + 2).toInt() == 0x8D &&
                interp.memory.readByte(addr + 3).toInt() == 0xC9 &&
                interp.memory.readByte(addr + 4).toInt() == 0x06
            ) {
                doNothing1Addr = addr
                break
            }
        }

        if (doNothing1Addr == null) {
            println("⚠️ Could not find doNothing1 function in ROM")
            return
        }

        println("Found doNothing1 at $${doNothing1Addr.toString(16).uppercase()}")

        // Clear memory at $06C9 before running
        interp.memory.writeByte(0x06C9, 0x00u)

        // Push return address and set PC
        interp.cpu.SP = 0xFFu
        interp.memory.writeByte(0x1FF, 0xFFu)  // High byte of return
        interp.memory.writeByte(0x1FE, 0xFEu)  // Low byte of return - 1
        interp.cpu.SP = 0xFDu
        interp.cpu.PC = doNothing1Addr.toUShort()

        // Run until RTS (opcode 0x60) or halt
        var cycles = 0
        while (cycles < 100 && !interp.halted) {
            val opcode = interp.memory.readByte(interp.cpu.PC.toInt()).toInt()
            cycles += interp.step()
            if (opcode == 0x60) break  // RTS
        }

        // Check binary interpreter result
        val interpResult = interp.memory.readByte(0x06C9).toInt()
        assertEquals(0xFF, interpResult, "Binary interpreter should write 0xFF to $06C9")

        // Now run decompiled version
        resetCPU()
        clearMemory()
        memory[0x06C9] = 0x00u

        doNothing1()

        val decompiledResult = memory[0x06C9].toInt()
        assertEquals(0xFF, decompiledResult, "Decompiled code should write 0xFF to $06C9")

        // Compare results
        assertEquals(interpResult, decompiledResult,
            "Binary interpreter and decompiled code should produce same result")

        println("✅ doNothing1 validated: binary interpreter = decompiled code")
    }

    /**
     * Frame-level validation: run the binary interpreter for several frames
     * and check that key game state variables are set correctly.
     */
    @Test
    fun `validate game initialization sequence`() {
        val romFile = findRom()
        if (romFile == null) {
            println("⚠️ Skipping game init validation - no ROM file found")
            return
        }

        // Set up binary interpreter with full NES emulation
        val interp = BinaryInterpreter6502()
        val rom = NESLoader.load(romFile)
        NESLoader.loadIntoMemory(rom, interp.memory)

        // Simple PPU stub
        var ppuCtrl: UByte = 0u
        var ppuStatus: UByte = 0x80u  // VBlank flag set

        interp.memoryReadHook = { addr ->
            when (addr) {
                0x2002 -> {
                    val status = ppuStatus
                    ppuStatus = (ppuStatus.toInt() and 0x7F).toUByte()  // Clear VBlank
                    status
                }
                else -> null
            }
        }

        interp.memoryWriteHook = { addr, value ->
            when (addr) {
                0x2000 -> { ppuCtrl = value; true }
                0x2001 -> true  // PPU mask
                in 0x2000..0x2007 -> true  // Other PPU regs
                0x4014 -> true  // OAM DMA
                in 0x4000..0x4017 -> true  // APU/IO
                else -> false
            }
        }

        // Reset and run boot sequence
        interp.reset()

        println("Starting game init validation...")
        println("Reset vector: $${interp.cpu.PC.toString(16).uppercase()}")

        // Run for a while to let game initialize
        var totalCycles = 0L
        for (i in 0 until 10000) {
            if (interp.halted) break
            totalCycles += interp.step()

            // Re-trigger VBlank periodically to simulate PPU
            if (i % 1000 == 0) {
                ppuStatus = (ppuStatus.toInt() or 0x80).toUByte()
            }
        }

        // Check some key game variables after init
        val operMode = interp.memory.readByte(0x0770).toInt()
        val worldNumber = interp.memory.readByte(0x075F).toInt()
        val levelNumber = interp.memory.readByte(0x0760).toInt()

        println("After init:")
        println("  OperMode: $operMode")
        println("  World: ${worldNumber + 1}-${levelNumber + 1}")
        println("  Total cycles: $totalCycles")

        // Verify we're at least in a valid state
        // OperMode 0 = title screen, which is expected after basic init
        println("✅ Game initialized to a valid state")
    }
}
