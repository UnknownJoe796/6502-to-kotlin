package com.ivieleague.decompiler6502tokotlin.interpreter

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests for the binary 6502 interpreter
 */
class BinaryInterpreter6502Test {

    @Test
    fun `test simple program - load and store`() {
        val interp = BinaryInterpreter6502()

        // Program: LDA #$42, STA $00, BRK
        // A9 42   LDA #$42
        // 85 00   STA $00
        // 00      BRK
        val program = ubyteArrayOf(0xA9u, 0x42u, 0x85u, 0x00u, 0x00u)
        program.forEachIndexed { i, b -> interp.memory.writeByte(0x8000 + i, b) }

        interp.cpu.PC = 0x8000u

        // Execute LDA #$42
        interp.step()
        assertEquals(0x42u.toUByte(), interp.cpu.A)
        assertEquals(0x8002u.toUShort(), interp.cpu.PC)

        // Execute STA $00
        interp.step()
        assertEquals(0x42u.toUByte(), interp.memory.readByte(0x00))
        assertEquals(0x8004u.toUShort(), interp.cpu.PC)

        // Execute BRK
        interp.step()
        assertTrue(interp.halted || interp.cpu.I)  // BRK either halts or sets I
    }

    @Test
    fun `test fibonacci calculation`() {
        val interp = BinaryInterpreter6502()

        // Calculate 7th Fibonacci number (13)
        // Same algorithm as RealWorld6502Test but in binary
        val program = ubyteArrayOf(
            0xA2u, 0x01u,       // LDX #$01
            0x86u, 0x00u,       // STX $00
            0x38u,              // SEC
            0xA0u, 0x07u,       // LDY #$07
            0x98u,              // TYA
            0xE9u, 0x03u,       // SBC #$03
            0xA8u,              // TAY
            0x18u,              // CLC
            0xA9u, 0x02u,       // LDA #$02
            0x85u, 0x01u,       // STA $01
            // Loop:
            0xA6u, 0x01u,       // LDX $01
            0x65u, 0x00u,       // ADC $00
            0x85u, 0x01u,       // STA $01
            0x86u, 0x00u,       // STX $00
            0x88u,              // DEY
            0xD0u, 0xF5u,       // BNE Loop (-11 from PC after fetch)
            0x00u               // BRK
        )
        program.forEachIndexed { i, b -> interp.memory.writeByte(0x8000 + i, b) }

        interp.cpu.PC = 0x8000u
        interp.run(1000)  // Run with cycle limit

        assertEquals(0x0Du.toUByte(), interp.cpu.A, "7th Fibonacci number should be 13")
    }

    @Test
    fun `test reset vector`() {
        val interp = BinaryInterpreter6502()

        // Set reset vector to $8000
        interp.memory.writeByte(0xFFFC, 0x00u)
        interp.memory.writeByte(0xFFFD, 0x80u)

        // Put a simple program at $8000
        interp.memory.writeByte(0x8000, 0xA9u)  // LDA #$42
        interp.memory.writeByte(0x8001, 0x42u)
        interp.memory.writeByte(0x8002, 0x00u)  // BRK

        interp.reset()

        assertEquals(0x8000u.toUShort(), interp.cpu.PC)

        interp.step()
        assertEquals(0x42u.toUByte(), interp.cpu.A)
    }

    @Test
    fun `test JSR and RTS`() {
        val interp = BinaryInterpreter6502()

        // Main: JSR Sub, LDA #$99, BRK
        // Sub: LDA #$42, RTS
        val program = ubyteArrayOf(
            // $8000: Main
            0x20u, 0x07u, 0x80u,  // JSR $8007
            0xA9u, 0x99u,         // LDA #$99
            0x00u,                // BRK
            0x00u,                // (padding)
            // $8007: Sub
            0xA9u, 0x42u,         // LDA #$42
            0x60u                 // RTS
        )
        program.forEachIndexed { i, b -> interp.memory.writeByte(0x8000 + i, b) }

        interp.cpu.PC = 0x8000u

        // Execute JSR
        interp.step()
        assertEquals(0x8007u.toUShort(), interp.cpu.PC)

        // Execute LDA #$42 in subroutine
        interp.step()
        assertEquals(0x42u.toUByte(), interp.cpu.A)

        // Execute RTS
        interp.step()
        assertEquals(0x8003u.toUShort(), interp.cpu.PC)

        // Execute LDA #$99 after return
        interp.step()
        assertEquals(0x99u.toUByte(), interp.cpu.A)
    }

    @Test
    fun `test NMI interrupt`() {
        val interp = BinaryInterpreter6502()

        // Set NMI vector to $9000
        interp.memory.writeByte(0xFFFA, 0x00u)
        interp.memory.writeByte(0xFFFB, 0x90u)

        // NMI handler: LDA #$FF, RTI
        interp.memory.writeByte(0x9000, 0xA9u)  // LDA #$FF
        interp.memory.writeByte(0x9001, 0xFFu)
        interp.memory.writeByte(0x9002, 0x40u)  // RTI

        // Main program at $8000
        interp.memory.writeByte(0x8000, 0xA9u)  // LDA #$00
        interp.memory.writeByte(0x8001, 0x00u)
        interp.memory.writeByte(0x8002, 0xEAu)  // NOP
        interp.memory.writeByte(0x8003, 0x00u)  // BRK

        interp.cpu.PC = 0x8000u

        // Run first instruction
        interp.step()
        assertEquals(0x00u.toUByte(), interp.cpu.A)

        // Trigger NMI
        interp.triggerNmi()
        interp.handleInterrupts()

        assertEquals(0x9000u.toUShort(), interp.cpu.PC)

        // Execute NMI handler
        interp.step()  // LDA #$FF
        assertEquals(0xFFu.toUByte(), interp.cpu.A)

        interp.step()  // RTI
        assertEquals(0x8002u.toUShort(), interp.cpu.PC)  // Return to where we were
    }

    @Test
    fun `test memory hooks`() {
        val interp = BinaryInterpreter6502()

        var ppuWrites = mutableListOf<Pair<Int, UByte>>()

        // Hook writes to PPU registers ($2000-$2007)
        interp.memoryWriteHook = { addr, value ->
            if (addr in 0x2000..0x2007) {
                ppuWrites.add(addr to value)
                true  // Handled
            } else {
                false  // Use normal memory
            }
        }

        // Program: LDA #$80, STA $2000
        interp.memory.writeByte(0x8000, 0xA9u)  // LDA #$80
        interp.memory.writeByte(0x8001, 0x80u)
        interp.memory.writeByte(0x8002, 0x8Du)  // STA $2000
        interp.memory.writeByte(0x8003, 0x00u)
        interp.memory.writeByte(0x8004, 0x20u)
        interp.memory.writeByte(0x8005, 0x00u)  // BRK

        interp.cpu.PC = 0x8000u
        interp.run(100)

        assertEquals(1, ppuWrites.size)
        assertEquals(0x2000 to 0x80u.toUByte(), ppuWrites[0])
    }
}
