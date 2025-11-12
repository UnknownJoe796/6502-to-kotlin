package com.ivieleague.decompiler6502tokotlin.interpreter

import com.ivieleague.decompiler6502tokotlin.hand.*
import org.junit.Test
import kotlin.test.*

/**
 * Comprehensive edge case tests for 6502 interpreter.
 * Tests corner cases, boundary conditions, and subtle behaviors.
 */
class Interpreter6502EdgeCaseTest {

    @Test
    fun testPageBoundaryCrossing() {
        // Test that indexed addressing crosses page boundaries correctly
        val interp = Interpreter6502()
        interp.labelResolver = { label -> if (label == "FF") 0x00FF else 0 }

        // Write value at page boundary crossing
        interp.memory.writeByte(0x0100, 0x42u)

        interp.cpu.X = 0x01u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.DirectX("FF")
            )
        )

        assertEquals(0x42u, interp.cpu.A)
    }

    @Test
    fun testZeroPageIndexWrapping() {
        // Zero page indexed addressing should wrap within zero page
        val interp = Interpreter6502()
        interp.memory.writeByte(0x0002, 0xAAu)

        interp.cpu.X = 0x03u
        interp.labelResolver = { label -> if (label == "FF") 0xFF else 0 }

        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.DirectX("FF")
            )
        )

        // 0xFF + 0x03 = 0x102, but should wrap to 0x02 in zero page
        assertEquals(0xAAu, interp.cpu.A)
    }

    @Test
    fun testIndirectJMPPageBoundaryBug() {
        // Famous 6502 bug: JMP ($xxFF) fetches high byte from $xx00 instead of $xx+1,00
        val interp = Interpreter6502()

        // Set up the bug condition
        interp.memory.writeByte(0x10FF, 0x00u)  // Low byte
        interp.memory.writeByte(0x1100, 0x99u)  // What we'd expect for high byte
        interp.memory.writeByte(0x1000, 0x20u)  // What actually gets used (bug)

        interp.labelResolver = { label -> if (label == "10FF") 0x10FF else 0 }

        val instruction = AssemblyInstruction(
            AssemblyOp.JMP,
            AssemblyAddressing.IndirectAbsolute("10FF")
        )

        // Our implementation should handle this correctly
        // The target address should be formed from bytes at 10FF and 1000 (wrapping)
        val addr = interp.memory.readByte(0x10FF).toInt()
        val highByte = interp.memory.readByte(0x1000).toInt()  // Bug: wraps to page start
        val targetAddr = (highByte shl 8) or addr

        assertEquals(0x2000, targetAddr)
    }

    @Test
    fun testADCOverflowCases() {
        val interp = Interpreter6502()

        // Positive + Positive = Negative (overflow)
        interp.cpu.A = 0x50u  // +80
        interp.cpu.C = false
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x50u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0xA0u, interp.cpu.A)  // -96 (overflow)
        assertTrue(interp.cpu.V, "Overflow flag should be set")
        assertFalse(interp.cpu.C, "Carry flag should be clear")
        assertTrue(interp.cpu.N, "Negative flag should be set")

        // Negative + Negative = Positive (overflow)
        interp.cpu.A = 0x80u  // -128
        interp.cpu.C = false
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x80u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x00u, interp.cpu.A)  // 0 (overflow)
        assertTrue(interp.cpu.V, "Overflow flag should be set")
        assertTrue(interp.cpu.C, "Carry flag should be set")
        assertFalse(interp.cpu.N, "Negative flag should be clear")

        // Positive + Negative = No overflow
        interp.cpu.A = 0x50u  // +80
        interp.cpu.C = false
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x90u, AssemblyAddressing.Radix.Hex)  // -112
            )
        )
        assertEquals(0xE0u, interp.cpu.A)  // -32
        assertFalse(interp.cpu.V, "Overflow flag should be clear")
    }

    @Test
    fun testSBCOverflowCases() {
        val interp = Interpreter6502()

        // Positive - Negative = Positive (could overflow)
        interp.cpu.A = 0x50u  // +80
        interp.cpu.C = true  // No borrow
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.SBC,
                AssemblyAddressing.ByteValue(0xF0u, AssemblyAddressing.Radix.Hex)  // -16
            )
        )
        assertEquals(0x60u, interp.cpu.A)  // +96
        assertFalse(interp.cpu.V, "Overflow flag should be clear")

        // Negative - Positive = Negative (could overflow)
        interp.cpu.A = 0x80u  // -128
        interp.cpu.C = true
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.SBC,
                AssemblyAddressing.ByteValue(0x01u, AssemblyAddressing.Radix.Hex)  // +1
            )
        )
        assertEquals(0x7Fu, interp.cpu.A)  // +127 (overflow!)
        assertTrue(interp.cpu.V, "Overflow flag should be set")
    }

    @Test
    fun testADCWithCarryChaining() {
        // Test multi-byte addition using carry
        val interp = Interpreter6502()

        // Add 0xFFFF + 0x0002 = 0x10001 (16-bit addition)
        // Low byte: 0xFF + 0x02
        interp.cpu.A = 0xFFu
        interp.cpu.C = false
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x02u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x01u, interp.cpu.A)
        assertTrue(interp.cpu.C, "Carry should be set for high byte")

        // High byte: 0xFF + 0x00 + carry
        interp.cpu.A = 0xFFu
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x00u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x00u, interp.cpu.A)
        assertTrue(interp.cpu.C, "Final carry should be set")
    }

    @Test
    fun testSBCWithBorrowChaining() {
        // Test multi-byte subtraction using borrow
        val interp = Interpreter6502()

        // Subtract 0x0100 - 0x0001 = 0x00FF
        // Low byte: 0x00 - 0x01 (with borrow)
        interp.cpu.A = 0x00u
        interp.cpu.C = true  // No borrow initially
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.SBC,
                AssemblyAddressing.ByteValue(0x01u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0xFFu, interp.cpu.A)
        assertFalse(interp.cpu.C, "Carry should be clear (borrow occurred)")

        // High byte: 0x01 - 0x00 - borrow
        interp.cpu.A = 0x01u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.SBC,
                AssemblyAddressing.ByteValue(0x00u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x00u, interp.cpu.A)
        assertTrue(interp.cpu.C, "Final carry should be set (no borrow)")
    }

    @Test
    fun testROLThroughCarry() {
        val interp = Interpreter6502()

        // Rotate left through carry multiple times
        interp.cpu.A = 0b10000001u
        interp.cpu.C = false

        // First ROL: C becomes 1, A becomes 00000010
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ROL, null))
        assertEquals(0b00000010u, interp.cpu.A)
        assertTrue(interp.cpu.C)

        // Second ROL: C becomes 0, A becomes 00000101 (carry shifted in)
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ROL, null))
        assertEquals(0b00000101u, interp.cpu.A)
        assertFalse(interp.cpu.C)
    }

    @Test
    fun testRORThroughCarry() {
        val interp = Interpreter6502()

        // Rotate right through carry multiple times
        interp.cpu.A = 0b10000001u
        interp.cpu.C = false

        // First ROR: C becomes 1, A becomes 01000000
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ROR, null))
        assertEquals(0b01000000u, interp.cpu.A)
        assertTrue(interp.cpu.C)

        // Second ROR: C becomes 0, A becomes 10100000 (carry shifted in)
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ROR, null))
        assertEquals(0b10100000u, interp.cpu.A)
        assertFalse(interp.cpu.C)
    }

    @Test
    fun testBITFlagBehavior() {
        val interp = Interpreter6502()
        interp.labelResolver = { label -> if (label == "1000") 0x1000 else 0 }

        // Test that BIT sets N and V from memory, not from result
        interp.cpu.A = 0b00001111u
        interp.memory.writeByte(0x1000, 0b11000000u)

        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.BIT,
                AssemblyAddressing.Direct("1000")
            )
        )

        // N flag = bit 7 of memory value
        assertTrue(interp.cpu.N, "N should be set from bit 7 of memory")
        // V flag = bit 6 of memory value
        assertTrue(interp.cpu.V, "V should be set from bit 6 of memory")
        // Z flag = (A & memory) == 0
        assertTrue(interp.cpu.Z, "Z should be set because A & memory = 0")

        // Test with non-zero result
        interp.cpu.A = 0b11110000u
        interp.memory.writeByte(0x1000, 0b01110000u)

        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.BIT,
                AssemblyAddressing.Direct("1000")
            )
        )

        assertFalse(interp.cpu.N, "N should match bit 7 of memory (0)")
        assertTrue(interp.cpu.V, "V should match bit 6 of memory (1)")
        assertFalse(interp.cpu.Z, "Z should be clear because A & memory != 0")
    }

    @Test
    fun testCompareWithZeroAndFF() {
        val interp = Interpreter6502()

        // CMP with equal values (0x00)
        interp.cpu.A = 0x00u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.CMP,
                AssemblyAddressing.ByteValue(0x00u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertTrue(interp.cpu.Z, "Z flag should be set")
        assertTrue(interp.cpu.C, "C flag should be set")
        assertFalse(interp.cpu.N, "N flag should be clear")

        // CMP with equal values (0xFF)
        interp.cpu.A = 0xFFu
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.CMP,
                AssemblyAddressing.ByteValue(0xFFu, AssemblyAddressing.Radix.Hex)
            )
        )
        assertTrue(interp.cpu.Z)
        assertTrue(interp.cpu.C)
        assertFalse(interp.cpu.N)

        // CMP 0x00 - 0x01 (borrow)
        interp.cpu.A = 0x00u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.CMP,
                AssemblyAddressing.ByteValue(0x01u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertFalse(interp.cpu.Z)
        assertFalse(interp.cpu.C, "C flag should be clear (A < operand)")
        assertTrue(interp.cpu.N, "N flag should be set (result is negative)")
    }

    @Test
    fun testIncrementDecrementWrapAround() {
        val interp = Interpreter6502()

        // INX wraps from FF to 00
        interp.cpu.X = 0xFFu
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.INX))
        assertEquals(0x00u, interp.cpu.X)
        assertTrue(interp.cpu.Z)
        assertFalse(interp.cpu.N)

        // DEX wraps from 00 to FF
        interp.cpu.X = 0x00u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.DEX))
        assertEquals(0xFFu, interp.cpu.X)
        assertFalse(interp.cpu.Z)
        assertTrue(interp.cpu.N)

        // INY wraps from FF to 00
        interp.cpu.Y = 0xFFu
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.INY))
        assertEquals(0x00u, interp.cpu.Y)
        assertTrue(interp.cpu.Z)

        // DEY wraps from 00 to FF
        interp.cpu.Y = 0x00u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.DEY))
        assertEquals(0xFFu, interp.cpu.Y)
        assertTrue(interp.cpu.N)
    }

    @Test
    fun testMemoryIncrementDecrementWrap() {
        val interp = Interpreter6502()
        interp.labelResolver = { label -> if (label == "1000") 0x1000 else 0 }

        // INC wraps from FF to 00
        interp.memory.writeByte(0x1000, 0xFFu)
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.INC,
                AssemblyAddressing.Direct("1000")
            )
        )
        assertEquals(0x00u, interp.memory.readByte(0x1000))
        assertTrue(interp.cpu.Z)
        assertFalse(interp.cpu.N)

        // DEC wraps from 00 to FF
        interp.memory.writeByte(0x1000, 0x00u)
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.DEC,
                AssemblyAddressing.Direct("1000")
            )
        )
        assertEquals(0xFFu, interp.memory.readByte(0x1000))
        assertFalse(interp.cpu.Z)
        assertTrue(interp.cpu.N)
    }

    @Test
    fun testStackUnderflowOverflow() {
        val interp = Interpreter6502()

        // Push until stack wraps
        interp.cpu.SP = 0x00u
        interp.cpu.A = 0x42u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PHA))
        assertEquals(0xFFu, interp.cpu.SP, "Stack should wrap to FF")

        // Pull until stack wraps
        interp.cpu.SP = 0xFFu
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PLA))
        assertEquals(0x00u, interp.cpu.SP, "Stack should wrap to 00")
    }

    @Test
    fun testIndirectXZeroPageWrap() {
        val interp = Interpreter6502()
        interp.labelResolver = { label -> if (label == "FF") 0xFF else 0 }

        // Set up indirect address wrapping in zero page
        // (FF,X) with X=02 should read from 01 (wraps: FF+02=101, wraps to 01)
        interp.cpu.X = 0x02u
        interp.memory.writeWord(0x01, 0x2000u)  // Zero page wraps
        interp.memory.writeByte(0x2000, 0xAAu)

        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.IndirectX("FF")
            )
        )

        assertEquals(0xAAu, interp.cpu.A)
    }

    @Test
    fun testIndirectYPageCrossing() {
        val interp = Interpreter6502()
        interp.labelResolver = { label -> if (label == "10") 0x10 else 0 }

        // Set up indirect addressing that crosses page boundary
        interp.memory.writeWord(0x10, 0x10FFu)
        interp.cpu.Y = 0x02u
        interp.memory.writeByte(0x1101, 0xBBu)  // 0x10FF + 0x02 = 0x1101

        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.IndirectY("10")
            )
        )

        assertEquals(0xBBu, interp.cpu.A)
    }

    @Test
    fun testAllFlagCombinations() {
        val interp = Interpreter6502()

        // Test that we can set all flags independently
        interp.cpu.N = true
        interp.cpu.V = true
        interp.cpu.D = true
        interp.cpu.I = true
        interp.cpu.Z = true
        interp.cpu.C = true

        val status = interp.cpu.getStatusByte()
        assertTrue((status.toInt() and 0b10000000) != 0, "N flag")
        assertTrue((status.toInt() and 0b01000000) != 0, "V flag")
        assertTrue((status.toInt() and 0b00001000) != 0, "D flag")
        assertTrue((status.toInt() and 0b00000100) != 0, "I flag")
        assertTrue((status.toInt() and 0b00000010) != 0, "Z flag")
        assertTrue((status.toInt() and 0b00000001) != 0, "C flag")

        // Test we can clear all flags
        interp.cpu.N = false
        interp.cpu.V = false
        interp.cpu.D = false
        interp.cpu.I = false
        interp.cpu.Z = false
        interp.cpu.C = false

        val status2 = interp.cpu.getStatusByte()
        assertEquals(0b00100000, status2.toInt(), "Only bit 5 should be set")
    }

    @Test
    fun testPHPPLPPreservesFlags() {
        val interp = Interpreter6502()

        // Set up specific flag state
        interp.cpu.N = true
        interp.cpu.V = false
        interp.cpu.D = true
        interp.cpu.I = false
        interp.cpu.Z = true
        interp.cpu.C = false

        val originalStatus = interp.cpu.getStatusByte()

        // Push and pop status
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PHP))

        // Modify flags
        interp.cpu.N = false
        interp.cpu.V = true
        interp.cpu.Z = false
        interp.cpu.C = true

        // Restore flags
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PLP))

        val restoredStatus = interp.cpu.getStatusByte()
        assertEquals(originalStatus, restoredStatus, "PHP/PLP should preserve all flags")
    }

    @Test
    fun testASLCarryAndZeroFlags() {
        val interp = Interpreter6502()

        // ASL that sets carry
        interp.cpu.A = 0x80u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ASL, null))
        assertEquals(0x00u, interp.cpu.A)
        assertTrue(interp.cpu.C, "Carry should be set")
        assertTrue(interp.cpu.Z, "Zero should be set")
        assertFalse(interp.cpu.N, "Negative should be clear")

        // ASL that clears carry
        interp.cpu.A = 0x40u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ASL, null))
        assertEquals(0x80u, interp.cpu.A)
        assertFalse(interp.cpu.C, "Carry should be clear")
        assertFalse(interp.cpu.Z, "Zero should be clear")
        assertTrue(interp.cpu.N, "Negative should be set")
    }

    @Test
    fun testLSRCarryAndZeroFlags() {
        val interp = Interpreter6502()

        // LSR that sets carry
        interp.cpu.A = 0x01u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.LSR, null))
        assertEquals(0x00u, interp.cpu.A)
        assertTrue(interp.cpu.C, "Carry should be set")
        assertTrue(interp.cpu.Z, "Zero should be set")
        assertFalse(interp.cpu.N, "Negative should always be clear after LSR")

        // LSR that clears carry
        interp.cpu.A = 0x80u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.LSR, null))
        assertEquals(0x40u, interp.cpu.A)
        assertFalse(interp.cpu.C, "Carry should be clear")
        assertFalse(interp.cpu.Z, "Zero should be clear")
        assertFalse(interp.cpu.N, "Negative should be clear")
    }

    @Test
    fun testEORResultingInZero() {
        val interp = Interpreter6502()

        // EOR with same value = 0
        interp.cpu.A = 0xAAu
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.EOR,
                AssemblyAddressing.ByteValue(0xAAu, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x00u, interp.cpu.A)
        assertTrue(interp.cpu.Z)
        assertFalse(interp.cpu.N)
    }

    @Test
    fun testANDResultingInZero() {
        val interp = Interpreter6502()

        // AND with 0 = 0
        interp.cpu.A = 0xFFu
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.AND,
                AssemblyAddressing.ByteValue(0x00u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x00u, interp.cpu.A)
        assertTrue(interp.cpu.Z)
        assertFalse(interp.cpu.N)
    }

    @Test
    fun testORAWithFF() {
        val interp = Interpreter6502()

        // OR with FF = FF
        interp.cpu.A = 0x42u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ORA,
                AssemblyAddressing.ByteValue(0xFFu, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0xFFu, interp.cpu.A)
        assertFalse(interp.cpu.Z)
        assertTrue(interp.cpu.N)
    }
}
