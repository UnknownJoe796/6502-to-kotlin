package com.ivieleague.decompiler6502tokotlin.interpreter

import com.ivieleague.decompiler6502tokotlin.hand.*
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Interpreter6502IntegrationTest {

    @Test
    fun testSimpleProgram() {
        // Program: Load value, add to it, store result
        // LDA #$10
        // ADC #$20
        // STA $1000
        val interp = Interpreter6502()
        interp.labelResolver = { label -> if (label == "1000") 0x1000 else 0 }

        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x10u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x10u, interp.cpu.A)

        interp.cpu.C = false
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x20u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x30u, interp.cpu.A)

        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.STA,
                AssemblyAddressing.Direct("1000")
            )
        )
        assertEquals(0x30u, interp.memory.readByte(0x1000))
    }

    @Test
    fun testLoopCounter() {
        // Simulate a simple counter loop
        // LDX #$00
        // INX
        // CPX #$05
        // BNE (loop)
        val interp = Interpreter6502()

        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDX,
                AssemblyAddressing.ByteValue(0x00u, AssemblyAddressing.Radix.Hex)
            )
        )

        var iterations = 0
        while (iterations < 10) {
            interp.executeInstruction(AssemblyInstruction(AssemblyOp.INX))

            interp.executeInstruction(
                AssemblyInstruction(
                    AssemblyOp.CPX,
                    AssemblyAddressing.ByteValue(0x05u, AssemblyAddressing.Radix.Hex)
                )
            )

            val bne = AssemblyInstruction(AssemblyOp.BNE)
            if (!interp.shouldBranch(bne)) {
                break
            }
            iterations++
        }

        assertEquals(0x05u, interp.cpu.X)
        assertEquals(5, iterations)
    }

    @Test
    fun testStackOperationsSequence() {
        val interp = Interpreter6502()

        // Push multiple values
        interp.cpu.A = 0x11u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PHA))

        interp.cpu.A = 0x22u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PHA))

        interp.cpu.A = 0x33u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PHA))

        // Pull them back in reverse order
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PLA))
        assertEquals(0x33u, interp.cpu.A)

        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PLA))
        assertEquals(0x22u, interp.cpu.A)

        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PLA))
        assertEquals(0x11u, interp.cpu.A)
    }

    @Test
    fun testMemoryArrayAccess() {
        // Test array access using indexed addressing
        val interp = Interpreter6502()
        interp.labelResolver = { label -> if (label == "2000") 0x2000 else 0 }

        // Initialize array in memory
        val array = listOf<UByte>(0x10u, 0x20u, 0x30u, 0x40u, 0x50u)
        interp.memory.loadProgram(0x2000, array)

        // Access each element using X register
        for (i in 0..4) {
            interp.cpu.X = i.toUByte()
            interp.executeInstruction(
                AssemblyInstruction(
                    AssemblyOp.LDA,
                    AssemblyAddressing.DirectX("2000")
                )
            )
            assertEquals(array[i], interp.cpu.A)
        }
    }

    @Test
    fun testBitManipulation() {
        val interp = Interpreter6502()

        // Set specific bits using ORA
        interp.cpu.A = 0b00000000u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ORA,
                AssemblyAddressing.ByteValue(0b00000001u, AssemblyAddressing.Radix.Binary)
            )
        )
        assertEquals(0b00000001u, interp.cpu.A)

        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ORA,
                AssemblyAddressing.ByteValue(0b00000100u, AssemblyAddressing.Radix.Binary)
            )
        )
        assertEquals(0b00000101u, interp.cpu.A)

        // Clear specific bits using AND
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.AND,
                AssemblyAddressing.ByteValue(0b11111110u, AssemblyAddressing.Radix.Binary)
            )
        )
        assertEquals(0b00000100u, interp.cpu.A)

        // Toggle bits using EOR
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.EOR,
                AssemblyAddressing.ByteValue(0b11111111u, AssemblyAddressing.Radix.Binary)
            )
        )
        assertEquals(0b11111011u, interp.cpu.A)
    }

    @Test
    fun testByteRotation() {
        val interp = Interpreter6502()

        // Rotate a byte left by 4 positions
        interp.cpu.A = 0b00001111u
        interp.cpu.C = false

        repeat(4) {
            interp.executeInstruction(AssemblyInstruction(AssemblyOp.ROL, null))
        }

        assertEquals(0b11110000u, interp.cpu.A)
    }

    @Test
    fun testMultiplyByTwo() {
        val interp = Interpreter6502()

        // Multiply by 2 using ASL
        interp.cpu.A = 0x10u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ASL, null))
        assertEquals(0x20u, interp.cpu.A)
        assertFalse(interp.cpu.C)

        interp.cpu.A = 0x80u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ASL, null))
        assertEquals(0x00u, interp.cpu.A)
        assertTrue(interp.cpu.C) // Overflow
    }

    @Test
    fun testDivideByTwo() {
        val interp = Interpreter6502()

        // Divide by 2 using LSR
        interp.cpu.A = 0x20u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.LSR, null))
        assertEquals(0x10u, interp.cpu.A)
        assertFalse(interp.cpu.C)

        interp.cpu.A = 0x01u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.LSR, null))
        assertEquals(0x00u, interp.cpu.A)
        assertTrue(interp.cpu.C) // Remainder
    }

    @Test
    fun testZeroPageWraparound() {
        val interp = Interpreter6502()
        interp.labelResolver = { label -> if (label == "FF") 0xFF else 0 }

        // Test that zero page addressing wraps around
        interp.memory.writeByte(0x00FF, 0xAAu)
        interp.cpu.X = 0x01u

        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.DirectX("FF")
            )
        )

        // Should wrap to 0x0000 + 0xFF + 0x01 = 0x0100
        assertEquals(0x00u, interp.memory.readByte(0x0100))
    }

    @Test
    fun testOverflowFlag() {
        val interp = Interpreter6502()

        // Test overflow on addition: positive + positive = negative
        interp.cpu.A = 0x50u  // +80 in signed
        interp.cpu.C = false
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x50u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0xA0u, interp.cpu.A)  // -96 in signed (overflow!)
        assertTrue(interp.cpu.V)

        // Test no overflow: positive + positive = positive
        interp.cpu.A = 0x20u
        interp.cpu.C = false
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x20u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x40u, interp.cpu.A)
        assertFalse(interp.cpu.V)
    }

    @Test
    fun testCarryFlagInArithmetic() {
        val interp = Interpreter6502()

        // Test carry set on overflow
        interp.cpu.A = 0xFFu
        interp.cpu.C = false
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x02u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x01u, interp.cpu.A)
        assertTrue(interp.cpu.C)

        // Test carry in subtraction (borrow)
        interp.cpu.A = 0x10u
        interp.cpu.C = true  // No borrow
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.SBC,
                AssemblyAddressing.ByteValue(0x20u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertFalse(interp.cpu.C)  // Borrow occurred
    }

    @Test
    fun testMemoryShiftOperations() {
        val interp = Interpreter6502()
        interp.labelResolver = { label -> if (label == "1000") 0x1000 else 0 }

        // Test ASL on memory
        interp.memory.writeByte(0x1000, 0b00000001u)
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ASL,
                AssemblyAddressing.Direct("1000")
            )
        )
        assertEquals(0b00000010u, interp.memory.readByte(0x1000))

        // Test LSR on memory
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LSR,
                AssemblyAddressing.Direct("1000")
            )
        )
        assertEquals(0b00000001u, interp.memory.readByte(0x1000))
    }

    @Test
    fun testRegisterWraparound() {
        val interp = Interpreter6502()

        // Test X register wraps around on increment
        interp.cpu.X = 0xFFu
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.INX))
        assertEquals(0x00u, interp.cpu.X)
        assertTrue(interp.cpu.Z)

        // Test Y register wraps around on decrement
        interp.cpu.Y = 0x00u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.DEY))
        assertEquals(0xFFu, interp.cpu.Y)
        assertTrue(interp.cpu.N)
    }

    @Test
    fun testComparisonEdgeCases() {
        val interp = Interpreter6502()

        // Compare equal values
        interp.cpu.A = 0x00u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.CMP,
                AssemblyAddressing.ByteValue(0x00u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertTrue(interp.cpu.Z)
        assertTrue(interp.cpu.C)
        assertFalse(interp.cpu.N)

        // Compare with 0xFF
        interp.cpu.A = 0xFFu
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.CMP,
                AssemblyAddressing.ByteValue(0xFFu, AssemblyAddressing.Radix.Hex)
            )
        )
        assertTrue(interp.cpu.Z)
        assertTrue(interp.cpu.C)
    }

    @Test
    fun testValueUpperAndLowerSelection() {
        val interp = Interpreter6502()

        // Test lower byte selection
        val lowerInstr = AssemblyInstruction(
            AssemblyOp.LDA,
            AssemblyAddressing.ValueLowerSelection(
                AssemblyAddressing.ShortValue(0x1234u, AssemblyAddressing.Radix.Hex)
            )
        )
        interp.executeInstruction(lowerInstr)
        assertEquals(0x34u, interp.cpu.A)

        // Test upper byte selection
        val upperInstr = AssemblyInstruction(
            AssemblyOp.LDA,
            AssemblyAddressing.ValueUpperSelection(
                AssemblyAddressing.ShortValue(0x1234u, AssemblyAddressing.Radix.Hex)
            )
        )
        interp.executeInstruction(upperInstr)
        assertEquals(0x12u, interp.cpu.A)
    }

    @Test
    fun testComplexAddressingChain() {
        // Test a complex chain of operations simulating real 6502 code
        val interp = Interpreter6502()
        interp.labelResolver = { label ->
            when (label) {
                "data" -> 0x2000
                "temp" -> 0x10
                else -> 0
            }
        }

        // Set up test data
        interp.memory.loadProgram(
            0x2000,
            listOf<UByte>(0x01u, 0x02u, 0x03u, 0x04u, 0x05u)
        )

        // LDX #$00
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDX,
                AssemblyAddressing.ByteValue(0x00u, AssemblyAddressing.Radix.Hex)
            )
        )

        // LDA data,X
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.DirectX("data")
            )
        )
        assertEquals(0x01u, interp.cpu.A)

        // INX
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.INX))

        // LDA data,X
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.DirectX("data")
            )
        )
        assertEquals(0x02u, interp.cpu.A)
    }
}
