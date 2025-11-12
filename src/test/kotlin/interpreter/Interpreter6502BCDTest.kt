package com.ivieleague.decompiler6502tokotlin.interpreter

import com.ivieleague.decompiler6502tokotlin.hand.*
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Comprehensive BCD (Binary Coded Decimal) mode tests.
 * Tests decimal mode arithmetic for ADC and SBC instructions.
 */
class Interpreter6502BCDTest {

    @Test
    fun testBCDAdditionSimple() {
        val interp = Interpreter6502()
        interp.cpu.D = true  // Enable decimal mode

        // 09 + 01 = 10 in BCD
        interp.cpu.A = 0x09u
        interp.cpu.C = false
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x01u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x10u, interp.cpu.A, "9 + 1 should equal 10 in BCD")
        assertFalse(interp.cpu.C, "No carry expected")
    }

    @Test
    fun testBCDAdditionWithCarry() {
        val interp = Interpreter6502()
        interp.cpu.D = true

        // 99 + 01 = 00 with carry
        interp.cpu.A = 0x99u
        interp.cpu.C = false
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x01u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x00u, interp.cpu.A, "99 + 1 should equal 00 in BCD")
        assertTrue(interp.cpu.C, "Carry should be set")
    }

    @Test
    fun testBCDAdditionMultipleDigits() {
        val interp = Interpreter6502()
        interp.cpu.D = true

        // 58 + 46 = 04 with carry (104 in decimal)
        interp.cpu.A = 0x58u
        interp.cpu.C = false
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x46u, AssemblyAddressing.Radix.Hex)
            )
        )
        // 58 + 46 = 104, result is 04 with carry
        assertEquals(0x04u, interp.cpu.A, "58 + 46 should equal 04 in BCD (with carry)")
        assertTrue(interp.cpu.C, "Carry should be set")
    }

    @Test
    fun testBCDAdditionWithInputCarry() {
        val interp = Interpreter6502()
        interp.cpu.D = true

        // 25 + 34 + carry(1) = 60
        interp.cpu.A = 0x25u
        interp.cpu.C = true  // Carry in
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x34u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x60u, interp.cpu.A, "25 + 34 + 1 should equal 60 in BCD")
        assertFalse(interp.cpu.C, "No carry out expected")
    }

    @Test
    fun testBCDAdditionEdgeCases() {
        val interp = Interpreter6502()
        interp.cpu.D = true

        // 00 + 00 = 00
        interp.cpu.A = 0x00u
        interp.cpu.C = false
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x00u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x00u, interp.cpu.A)
        assertFalse(interp.cpu.C)

        // 50 + 50 = 00 with carry
        interp.cpu.A = 0x50u
        interp.cpu.C = false
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x50u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x00u, interp.cpu.A, "50 + 50 should equal 00 in BCD (with carry)")
        assertTrue(interp.cpu.C)
    }

    @Test
    fun testBCDSubtractionSimple() {
        val interp = Interpreter6502()
        interp.cpu.D = true

        // 09 - 01 = 08
        interp.cpu.A = 0x09u
        interp.cpu.C = true  // No borrow
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.SBC,
                AssemblyAddressing.ByteValue(0x01u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x08u, interp.cpu.A, "9 - 1 should equal 8 in BCD")
        assertTrue(interp.cpu.C, "Carry should remain set (no borrow)")
    }

    @Test
    fun testBCDSubtractionWithBorrow() {
        val interp = Interpreter6502()
        interp.cpu.D = true

        // 00 - 01 = 99 with borrow
        interp.cpu.A = 0x00u
        interp.cpu.C = true  // No borrow initially
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.SBC,
                AssemblyAddressing.ByteValue(0x01u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x99u, interp.cpu.A, "0 - 1 should equal 99 in BCD (with borrow)")
        assertFalse(interp.cpu.C, "Carry should be clear (borrow occurred)")
    }

    @Test
    fun testBCDSubtractionMultipleDigits() {
        val interp = Interpreter6502()
        interp.cpu.D = true

        // 46 - 12 = 34
        interp.cpu.A = 0x46u
        interp.cpu.C = true
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.SBC,
                AssemblyAddressing.ByteValue(0x12u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x34u, interp.cpu.A, "46 - 12 should equal 34 in BCD")
        assertTrue(interp.cpu.C)
    }

    @Test
    fun testBCDSubtractionWithInputBorrow() {
        val interp = Interpreter6502()
        interp.cpu.D = true

        // 50 - 25 - borrow(1) = 24
        interp.cpu.A = 0x50u
        interp.cpu.C = false  // Borrow in
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.SBC,
                AssemblyAddressing.ByteValue(0x25u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x24u, interp.cpu.A, "50 - 25 - 1 should equal 24 in BCD")
        assertTrue(interp.cpu.C, "No borrow out")
    }

    @Test
    fun testBCDSubtractionEdgeCases() {
        val interp = Interpreter6502()
        interp.cpu.D = true

        // 00 - 00 = 00
        interp.cpu.A = 0x00u
        interp.cpu.C = true
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.SBC,
                AssemblyAddressing.ByteValue(0x00u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x00u, interp.cpu.A)
        assertTrue(interp.cpu.C)

        // 99 - 99 = 00
        interp.cpu.A = 0x99u
        interp.cpu.C = true
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.SBC,
                AssemblyAddressing.ByteValue(0x99u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x00u, interp.cpu.A)
        assertTrue(interp.cpu.C)
    }

    @Test
    fun testBCDVsBinaryMode() {
        val interp = Interpreter6502()

        // Binary mode: 09 + 01 = 0A
        interp.cpu.D = false
        interp.cpu.A = 0x09u
        interp.cpu.C = false
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x01u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x0Au, interp.cpu.A, "Binary mode: 0x09 + 0x01 = 0x0A")

        // Decimal mode: 09 + 01 = 10
        interp.cpu.D = true
        interp.cpu.A = 0x09u
        interp.cpu.C = false
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x01u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x10u, interp.cpu.A, "Decimal mode: 09 + 01 = 10")
    }

    @Test
    fun testBCDMultiByteAddition() {
        // Simulate adding two 2-byte BCD numbers: 9876 + 1234 = 11110
        val interp = Interpreter6502()
        interp.cpu.D = true

        // Low bytes: 76 + 34 = 10 with carry
        interp.cpu.A = 0x76u
        interp.cpu.C = false
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x34u, AssemblyAddressing.Radix.Hex)
            )
        )
        val lowResult = interp.cpu.A
        val carry1 = interp.cpu.C

        // High bytes: 98 + 12 + carry
        interp.cpu.A = 0x98u
        interp.cpu.C = carry1
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x12u, AssemblyAddressing.Radix.Hex)
            )
        )
        val highResult = interp.cpu.A
        val carry2 = interp.cpu.C

        // Result should be 0x10 (low), 0x11 (high), carry = 1
        // Which represents 1,11,10 = 11110
        assertEquals(0x10u, lowResult)
        assertEquals(0x11u, highResult)
        assertTrue(carry2, "Should have final carry")
    }

    @Test
    fun testBCDMultiByteSubtraction() {
        // Simulate subtracting two 2-byte BCD numbers: 5000 - 1234 = 3766
        val interp = Interpreter6502()
        interp.cpu.D = true

        // Low bytes: 00 - 34 = 66 with borrow
        interp.cpu.A = 0x00u
        interp.cpu.C = true  // No initial borrow
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.SBC,
                AssemblyAddressing.ByteValue(0x34u, AssemblyAddressing.Radix.Hex)
            )
        )
        val lowResult = interp.cpu.A
        val borrow1 = !interp.cpu.C

        // High bytes: 50 - 12 - borrow
        interp.cpu.A = 0x50u
        interp.cpu.C = !borrow1
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.SBC,
                AssemblyAddressing.ByteValue(0x12u, AssemblyAddressing.Radix.Hex)
            )
        )
        val highResult = interp.cpu.A

        // Result should be 0x66 (low), 0x37 (high)
        // Which represents 3766
        assertEquals(0x66u, lowResult)
        assertEquals(0x37u, highResult)
    }

    @Test
    fun testSEDAndCLD() {
        val interp = Interpreter6502()

        // Test SED (set decimal mode)
        assertFalse(interp.cpu.D, "Decimal mode should be off initially")
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.SED))
        assertTrue(interp.cpu.D, "SED should enable decimal mode")

        // Test CLD (clear decimal mode)
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.CLD))
        assertFalse(interp.cpu.D, "CLD should disable decimal mode")
    }

    @Test
    fun testBCDAdditionAllPairs() {
        // Test a comprehensive set of BCD additions
        val interp = Interpreter6502()
        interp.cpu.D = true

        val testCases = listOf(
            Triple(0x00u, 0x00u, 0x00u),
            Triple(0x01u, 0x01u, 0x02u),
            Triple(0x05u, 0x05u, 0x10u),
            Triple(0x09u, 0x09u, 0x18u),
            Triple(0x19u, 0x01u, 0x20u),
            Triple(0x49u, 0x01u, 0x50u),
            Triple(0x89u, 0x01u, 0x90u),
            Triple(0x99u, 0x01u, 0x00u),  // With carry
            Triple(0x15u, 0x15u, 0x30u),
            Triple(0x25u, 0x36u, 0x61u),
            Triple(0x81u, 0x92u, 0x73u),  // With carry
        )

        for ((a, b, expected) in testCases) {
            interp.cpu.A = a
            interp.cpu.C = false
            interp.executeInstruction(
                AssemblyInstruction(
                    AssemblyOp.ADC,
                    AssemblyAddressing.ByteValue(b, AssemblyAddressing.Radix.Hex)
                )
            )
            val result = interp.cpu.A
            val expectedWithCarry = if (expected.toInt() > 0x99) {
                (expected.toInt() - 0x100).toUByte()
            } else {
                expected
            }
            assertEquals(
                expectedWithCarry,
                result,
                "BCD: $a + $b should equal ${expected.toString(16)} (got ${result.toString(16)})"
            )
        }
    }

    @Test
    fun testBCDSubtractionAllPairs() {
        // Test a comprehensive set of BCD subtractions
        val interp = Interpreter6502()
        interp.cpu.D = true

        val testCases = listOf(
            Triple(0x00u, 0x00u, 0x00u),
            Triple(0x09u, 0x01u, 0x08u),
            Triple(0x10u, 0x01u, 0x09u),
            Triple(0x50u, 0x25u, 0x25u),
            Triple(0x99u, 0x01u, 0x98u),
            Triple(0x46u, 0x12u, 0x34u),
            Triple(0x25u, 0x25u, 0x00u),
        )

        for ((a, b, expected) in testCases) {
            interp.cpu.A = a
            interp.cpu.C = true  // No borrow
            interp.executeInstruction(
                AssemblyInstruction(
                    AssemblyOp.SBC,
                    AssemblyAddressing.ByteValue(b, AssemblyAddressing.Radix.Hex)
                )
            )
            assertEquals(
                expected,
                interp.cpu.A,
                "BCD: $a - $b should equal $expected (got ${interp.cpu.A})"
            )
        }
    }

    @Test
    fun testBCDCarryPropagation() {
        // Test that carry properly propagates through multiple BCD operations
        val interp = Interpreter6502()
        interp.cpu.D = true

        // Chain of additions that propagate carry
        interp.cpu.A = 0x95u
        interp.cpu.C = false

        // 95 + 05 = 00 (carry)
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x05u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x00u, interp.cpu.A)
        assertTrue(interp.cpu.C)

        // 00 + 00 + carry = 01
        interp.cpu.A = 0x00u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x00u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x01u, interp.cpu.A)
        assertFalse(interp.cpu.C)
    }

    @Test
    fun testBCDZeroResult() {
        val interp = Interpreter6502()
        interp.cpu.D = true

        // Addition resulting in zero
        interp.cpu.A = 0x00u
        interp.cpu.C = false
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x00u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x00u, interp.cpu.A)
        assertTrue(interp.cpu.Z, "Zero flag should be set for BCD zero result")

        // Subtraction resulting in zero
        interp.cpu.A = 0x50u
        interp.cpu.C = true
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.SBC,
                AssemblyAddressing.ByteValue(0x50u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x00u, interp.cpu.A)
        assertTrue(interp.cpu.Z, "Zero flag should be set for BCD zero result")
    }
}
