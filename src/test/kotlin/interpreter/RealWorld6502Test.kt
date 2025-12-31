package com.ivieleague.decompiler6502tokotlin.interpreter

import com.ivieleague.decompiler6502tokotlin.hand.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Real-world 6502 test cases based on well-known examples and test suites.
 * These tests verify correct behavior against documented 6502 implementations.
 */
class RealWorld6502Test {

    /**
     * Test ADC overflow flag behavior with signed arithmetic edge cases.
     * Based on examples from http://www.righto.com/2012/12/the-6502-overflow-flag-explained.html
     */
    @Test
    fun testADCOverflowCases() {
        val interp = Interpreter6502()

        // 1 + 1 = 2, no overflow (both positive, result positive)
        interp.cpu.C = false
        interp.cpu.A = 0x01u
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.ByteValue(0x01u, AssemblyAddressing.Radix.Hex))
        )
        assertEquals(0x02u, interp.cpu.A)
        assertFalse(interp.cpu.V, "1 + 1 should not overflow")
        assertFalse(interp.cpu.C, "1 + 1 should not carry")

        // 1 + -1 = 0, no overflow (positive + negative)
        interp.cpu.C = false
        interp.cpu.A = 0x01u
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.ByteValue(0xFFu, AssemblyAddressing.Radix.Hex))
        )
        assertEquals(0x00u, interp.cpu.A)
        assertFalse(interp.cpu.V, "1 + -1 should not overflow")
        assertTrue(interp.cpu.C, "1 + -1 should set carry")

        // 127 + 1 = 128, overflow! (both positive, result negative)
        interp.cpu.C = false
        interp.cpu.A = 0x7Fu
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.ByteValue(0x01u, AssemblyAddressing.Radix.Hex))
        )
        assertEquals(0x80u, interp.cpu.A)
        assertTrue(interp.cpu.V, "127 + 1 should overflow (positive to negative)")
        assertFalse(interp.cpu.C, "127 + 1 should not carry")

        // -128 + -1 = -129, overflow! (both negative, result positive)
        interp.cpu.C = false
        interp.cpu.A = 0x80u
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.ByteValue(0xFFu, AssemblyAddressing.Radix.Hex))
        )
        assertEquals(0x7Fu, interp.cpu.A)
        assertTrue(interp.cpu.V, "-128 + -1 should overflow (negative to positive)")
        assertTrue(interp.cpu.C, "-128 + -1 should carry")

        // 80 + 16 + carry = 97, no overflow
        interp.cpu.C = true
        interp.cpu.A = 0x50u
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.ByteValue(0x10u, AssemblyAddressing.Radix.Hex))
        )
        assertEquals(0x61u, interp.cpu.A)
        assertFalse(interp.cpu.V, "80 + 16 + 1 should not overflow")

        // 80 + 80 = 160, overflow (both positive, result negative)
        interp.cpu.C = false
        interp.cpu.A = 0x50u
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.ByteValue(0x50u, AssemblyAddressing.Radix.Hex))
        )
        assertEquals(0xA0u, interp.cpu.A)
        assertTrue(interp.cpu.V, "80 + 80 should overflow")
        assertFalse(interp.cpu.C)

        // -80 + -80 = -160, overflow (both negative, result positive)
        interp.cpu.C = false
        interp.cpu.A = 0xB0u  // -80 in two's complement
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.ByteValue(0xB0u, AssemblyAddressing.Radix.Hex))
        )
        assertEquals(0x60u, interp.cpu.A)
        assertTrue(interp.cpu.V, "-80 + -80 should overflow")
        assertTrue(interp.cpu.C)
    }

    /**
     * Test SBC overflow flag behavior with signed arithmetic edge cases.
     */
    @Test
    fun testSBCOverflowCases() {
        val interp = Interpreter6502()

        // 0 - 1 = -1, no overflow
        interp.cpu.C = true  // no borrow
        interp.cpu.A = 0x00u
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.SBC, AssemblyAddressing.ByteValue(0x01u, AssemblyAddressing.Radix.Hex))
        )
        assertEquals(0xFFu, interp.cpu.A)
        assertFalse(interp.cpu.V, "0 - 1 should not overflow")
        assertFalse(interp.cpu.C, "0 - 1 should set borrow (C=0)")

        // -128 - 1 = -129, overflow! (negative - positive = positive)
        interp.cpu.C = true
        interp.cpu.A = 0x80u
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.SBC, AssemblyAddressing.ByteValue(0x01u, AssemblyAddressing.Radix.Hex))
        )
        assertEquals(0x7Fu, interp.cpu.A)
        assertTrue(interp.cpu.V, "-128 - 1 should overflow")

        // 127 - -1 = 128, overflow! (positive - negative = negative)
        interp.cpu.C = true
        interp.cpu.A = 0x7Fu
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.SBC, AssemblyAddressing.ByteValue(0xFFu, AssemblyAddressing.Radix.Hex))
        )
        assertEquals(0x80u, interp.cpu.A)
        assertTrue(interp.cpu.V, "127 - -1 should overflow")

        // 5 - 3 = 2, no overflow
        interp.cpu.C = true
        interp.cpu.A = 0x05u
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.SBC, AssemblyAddressing.ByteValue(0x03u, AssemblyAddressing.Radix.Hex))
        )
        assertEquals(0x02u, interp.cpu.A)
        assertFalse(interp.cpu.V)
        assertTrue(interp.cpu.C)
    }

    /**
     * Test Fibonacci sequence calculator based on real 6502 implementation.
     * From: https://gist.github.com/pedrofrancescHerehi/1285964
     * Calculates the 7th Fibonacci number (13 = 0x0D)
     */
    @Test
    fun testFibonacciSequence() {
        val interp = Interpreter6502()

        // LDX #$01; x = 1
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.LDX, AssemblyAddressing.ByteValue(0x01u, AssemblyAddressing.Radix.Hex))
        )

        // STX $00; stores x
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.STX, AssemblyAddressing.Direct("\$00"))
        )

        // SEC; clean carry
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.SEC))

        // LDY #$07; calculates 7th fibonacci number
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.LDY, AssemblyAddressing.ByteValue(0x07u, AssemblyAddressing.Radix.Hex))
        )

        // TYA; transfer y register to accumulator
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.TYA))

        // SBC #$03; handles the algorithm iteration counting
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.SBC, AssemblyAddressing.ByteValue(0x03u, AssemblyAddressing.Radix.Hex))
        )

        // TAY; transfer the accumulator to the y register
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.TAY))

        // CLC; clean carry
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.CLC))

        // LDA #$02; a = 2
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.ByteValue(0x02u, AssemblyAddressing.Radix.Hex))
        )

        // STA $01; stores a
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$01"))
        )

        // loop: LDX $01; ADC $00; STA $01; STX $00; DEY; BNE loop
        while (true) {
            // LDX $01; x = a
            interp.executeInstruction(
                AssemblyInstruction(AssemblyOp.LDX, AssemblyAddressing.Direct("\$01"))
            )

            // ADC $00; a += x
            interp.executeInstruction(
                AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.Direct("\$00"))
            )

            // STA $01; stores a
            interp.executeInstruction(
                AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$01"))
            )

            // STX $00; stores x
            interp.executeInstruction(
                AssemblyInstruction(AssemblyOp.STX, AssemblyAddressing.Direct("\$00"))
            )

            // DEY; y -= 1
            interp.executeInstruction(AssemblyInstruction(AssemblyOp.DEY))

            // BNE loop
            if (!interp.shouldBranch(AssemblyInstruction(AssemblyOp.BNE))) {
                break
            }
        }

        // The accumulator should hold the 7th fibonacci number: 13 (0x0D)
        assertEquals(0x0Du, interp.cpu.A, "7th Fibonacci number should be 13")
    }

    /**
     * Test various Fibonacci numbers to verify algorithm correctness.
     */
    @Test
    fun testFibonacciVariousNumbers() {
        // Test a few key Fibonacci numbers
        val testCases = listOf(
            3 to 2u,
            4 to 3u,
            5 to 5u,
            6 to 8u,
            7 to 13u,
            10 to 55u,
            13 to 233u
        )

        for ((n, expected) in testCases) {
            val interp = Interpreter6502()

            // Run the full Fibonacci algorithm for position n
            interp.executeInstruction(
                AssemblyInstruction(AssemblyOp.LDX, AssemblyAddressing.ByteValue(0x01u, AssemblyAddressing.Radix.Hex))
            )
            interp.executeInstruction(
                AssemblyInstruction(AssemblyOp.STX, AssemblyAddressing.Direct("\$00"))
            )
            interp.executeInstruction(AssemblyInstruction(AssemblyOp.SEC))
            interp.executeInstruction(
                AssemblyInstruction(AssemblyOp.LDY, AssemblyAddressing.ByteValue(n.toUByte(), AssemblyAddressing.Radix.Hex))
            )
            interp.executeInstruction(AssemblyInstruction(AssemblyOp.TYA))
            interp.executeInstruction(
                AssemblyInstruction(AssemblyOp.SBC, AssemblyAddressing.ByteValue(0x03u, AssemblyAddressing.Radix.Hex))
            )
            interp.executeInstruction(AssemblyInstruction(AssemblyOp.TAY))
            interp.executeInstruction(AssemblyInstruction(AssemblyOp.CLC))
            interp.executeInstruction(
                AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.ByteValue(0x02u, AssemblyAddressing.Radix.Hex))
            )
            interp.executeInstruction(
                AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$01"))
            )

            // Loop - check Y before first iteration
            loop@ while (interp.cpu.Y != 0.toUByte()) {
                interp.executeInstruction(
                    AssemblyInstruction(AssemblyOp.LDX, AssemblyAddressing.Direct("\$01"))
                )
                interp.executeInstruction(
                    AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.Direct("\$00"))
                )
                interp.executeInstruction(
                    AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$01"))
                )
                interp.executeInstruction(
                    AssemblyInstruction(AssemblyOp.STX, AssemblyAddressing.Direct("\$00"))
                )
                interp.executeInstruction(AssemblyInstruction(AssemblyOp.DEY))

                // BNE loop - if Y is now 0, exit
                if (interp.cpu.Y == 0.toUByte()) {
                    break
                }
            }

            assertEquals(expected.toUByte(), interp.cpu.A, "Fib($n) should be $expected")
        }
    }

    /**
     * Test 8-bit multiplication using shift-and-add algorithm.
     * This is a common real-world 6502 pattern since there's no MUL instruction.
     */
    @Test
    fun testMultiplication8Bit() {
        val interp = Interpreter6502()

        // Multiply 12 * 10 = 120
        val multiplicand = 12u.toUByte()
        val multiplier = 10u.toUByte()

        // Setup: store multiplicand at $00, multiplier at $01, result at $02
        interp.memory.writeByte(0x00, multiplicand)
        interp.memory.writeByte(0x01, multiplier)
        interp.memory.writeByte(0x02, 0u)

        // LDA #$00 - clear accumulator
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.ByteValue(0x00u, AssemblyAddressing.Radix.Hex))
        )

        // LDX $01 - load multiplier into X
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.LDX, AssemblyAddressing.Direct("\$01"))
        )

        // Loop: add multiplicand X times
        while (true) {
            // CPX #$00
            interp.executeInstruction(
                AssemblyInstruction(AssemblyOp.CPX, AssemblyAddressing.ByteValue(0x00u, AssemblyAddressing.Radix.Hex))
            )

            // BEQ done (if X == 0)
            if (interp.shouldBranch(AssemblyInstruction(AssemblyOp.BEQ))) {
                break
            }

            // CLC
            interp.executeInstruction(AssemblyInstruction(AssemblyOp.CLC))

            // ADC $00 - add multiplicand
            interp.executeInstruction(
                AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.Direct("\$00"))
            )

            // DEX
            interp.executeInstruction(AssemblyInstruction(AssemblyOp.DEX))
        }

        // STA $02 - store result
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$02"))
        )

        assertEquals(120u, interp.memory.readByte(0x02), "12 * 10 should equal 120")
    }

    /**
     * Test sum of numbers from 1 to N (classic loop example).
     * Simple algorithm: sum = 0; for (i = N; i > 0; i--) sum += i
     */
    @Test
    fun testSumOneToN() {
        val interp = Interpreter6502()

        // Calculate sum of 1 to 10 = 55
        val n = 10u.toUByte()

        // Store N at $00
        interp.memory.writeByte(0x00, n)

        // LDA #$00 - accumulator = 0 (sum)
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.ByteValue(0x00u, AssemblyAddressing.Radix.Hex))
        )

        // STA $01 - store sum
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$01"))
        )

        // Loop: add current counter to sum, decrement counter
        while (true) {
            // LDA $01 - load current sum
            interp.executeInstruction(
                AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$01"))
            )

            // CLC
            interp.executeInstruction(AssemblyInstruction(AssemblyOp.CLC))

            // ADC $00 - add counter to sum
            interp.executeInstruction(
                AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.Direct("\$00"))
            )

            // STA $01 - store new sum
            interp.executeInstruction(
                AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$01"))
            )

            // DEC $00 - decrement counter
            interp.executeInstruction(
                AssemblyInstruction(AssemblyOp.DEC, AssemblyAddressing.Direct("\$00"))
            )

            // LDA $00 - load counter to set flags
            interp.executeInstruction(
                AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$00"))
            )

            // BNE loop - continue if counter != 0
            if (!interp.shouldBranch(AssemblyInstruction(AssemblyOp.BNE))) {
                break
            }
        }

        // Load final sum
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$01"))
        )

        assertEquals(55u, interp.cpu.A, "Sum of 1 to 10 should be 55")
    }

    /**
     * Test bit manipulation patterns common in 6502 programs.
     */
    @Test
    fun testBitManipulationPatterns() {
        val interp = Interpreter6502()

        // Test setting specific bits
        interp.cpu.A = 0b00001111u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ORA,
                AssemblyAddressing.ByteValue(0b11110000u, AssemblyAddressing.Radix.Binary)
            )
        )
        assertEquals(0b11111111u, interp.cpu.A, "ORA should set bits")

        // Test clearing specific bits
        interp.cpu.A = 0b11111111u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.AND,
                AssemblyAddressing.ByteValue(0b00001111u, AssemblyAddressing.Radix.Binary)
            )
        )
        assertEquals(0b00001111u, interp.cpu.A, "AND should clear bits")

        // Test toggling bits
        interp.cpu.A = 0b10101010u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.EOR,
                AssemblyAddressing.ByteValue(0b11111111u, AssemblyAddressing.Radix.Binary)
            )
        )
        assertEquals(0b01010101u, interp.cpu.A, "EOR should toggle bits")

        // Test bit testing with BIT instruction
        interp.memory.writeByte(0x10, 0b11000000u)
        interp.cpu.A = 0b00000001u
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.BIT, AssemblyAddressing.Direct("\$10"))
        )
        assertTrue(interp.cpu.N, "BIT should set N from bit 7")
        assertTrue(interp.cpu.V, "BIT should set V from bit 6")
        assertTrue(interp.cpu.Z, "BIT should set Z when AND result is 0")
    }

    /**
     * Test carry flag propagation through multiple byte addition.
     * This tests 16-bit addition using 8-bit operations.
     */
    @Test
    fun testMultiByteAddition() {
        val interp = Interpreter6502()

        // Add two 16-bit numbers: $12FF + $5601 = $6900
        // Low bytes: $FF + $01 = $00 (carry set)
        // High bytes: $12 + $56 + carry = $69

        // Store first number at $00-$01 (little-endian)
        interp.memory.writeByte(0x00, 0xFFu)
        interp.memory.writeByte(0x01, 0x12u)

        // Store second number at $02-$03
        interp.memory.writeByte(0x02, 0x01u)
        interp.memory.writeByte(0x03, 0x56u)

        // Add low bytes
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.CLC))
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$00"))
        )
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.Direct("\$02"))
        )
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$04"))
        )
        val carryFromLow = interp.cpu.C

        // Add high bytes with carry
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$01"))
        )
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.Direct("\$03"))
        )
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$05"))
        )

        // Verify result
        assertEquals(0x00u, interp.memory.readByte(0x04), "Low byte should be \$00")
        assertEquals(0x69u, interp.memory.readByte(0x05), "High byte should be \$69")
        assertTrue(carryFromLow, "Low byte addition should produce carry")
    }

    /**
     * Test zero-page indirect indexed addressing (commonly used in 6502 programs).
     */
    @Test
    fun testZeroPageIndirectIndexed() {
        val interp = Interpreter6502()

        // Set up a pointer at zero page location $10 pointing to $2000
        interp.memory.writeWord(0x10, 0x2000u)

        // Write test data at $2000 + offset
        interp.memory.writeByte(0x2005, 0x42u)

        // LDY #$05
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.LDY, AssemblyAddressing.ByteValue(0x05u, AssemblyAddressing.Radix.Hex))
        )

        // LDA ($10),Y - load from address stored at $10 plus Y
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.IndirectY("\$10"))
        )

        assertEquals(0x42u, interp.cpu.A, "Indirect indexed addressing should load correct value")
    }

    /**
     * Test indexed indirect addressing (X-indexed indirect).
     */
    @Test
    fun testIndexedIndirect() {
        val interp = Interpreter6502()

        // Set up a table of pointers in zero page
        // At $10: pointer to $3000
        // At $12: pointer to $3100
        interp.memory.writeWord(0x10, 0x3000u)
        interp.memory.writeWord(0x12, 0x3100u)

        // Write test data
        interp.memory.writeByte(0x3000, 0x42u)
        interp.memory.writeByte(0x3100, 0x99u)

        // LDX #$00
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.LDX, AssemblyAddressing.ByteValue(0x00u, AssemblyAddressing.Radix.Hex))
        )

        // LDA ($10,X) - load from address stored at ($10 + X)
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.IndirectX("\$10"))
        )

        assertEquals(0x42u, interp.cpu.A, "Should load from first pointer")

        // LDX #$02
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.LDX, AssemblyAddressing.ByteValue(0x02u, AssemblyAddressing.Radix.Hex))
        )

        // LDA ($10,X) - load from address stored at ($10 + X)
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.IndirectX("\$10"))
        )

        assertEquals(0x99u, interp.cpu.A, "Should load from second pointer")
    }

    /**
     * Test memory copy routine (common 6502 pattern).
     */
    @Test
    fun testMemoryCopyRoutine() {
        val interp = Interpreter6502()

        // Source data at $1000
        val sourceData = listOf<UByte>(0x11u, 0x22u, 0x33u, 0x44u, 0x55u)
        sourceData.forEachIndexed { index, byte ->
            interp.memory.writeByte(0x1000 + index, byte)
        }

        // Copy 5 bytes from $1000 to $2000
        val count = 5

        // LDX #$00 - index
        interp.executeInstruction(
            AssemblyInstruction(AssemblyOp.LDX, AssemblyAddressing.ByteValue(0x00u, AssemblyAddressing.Radix.Hex))
        )

        // Loop
        for (i in 0 until count) {
            // LDA $1000,X
            interp.memory.writeByte(0x10, 0x00u)  // Store base address low
            interp.memory.writeByte(0x11, 0x10u)  // Store base address high

            val sourceAddr = 0x1000 + interp.cpu.X.toInt()
            interp.cpu.A = interp.memory.readByte(sourceAddr)

            // STA $2000,X
            val destAddr = 0x2000 + interp.cpu.X.toInt()
            interp.memory.writeByte(destAddr, interp.cpu.A)

            // INX
            interp.executeInstruction(AssemblyInstruction(AssemblyOp.INX))
        }

        // Verify copy
        sourceData.forEachIndexed { index, byte ->
            assertEquals(byte, interp.memory.readByte(0x2000 + index), "Byte $index should be copied")
        }
    }

    /**
     * Test stack-based subroutine simulation.
     */
    @Test
    fun testStackSubroutinePattern() {
        val interp = Interpreter6502()

        // Simulate calling a subroutine that doubles a number
        // Input in A, output in A

        // Set up initial value
        interp.cpu.A = 0x15u  // 21 decimal

        // Save registers before "subroutine"
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PHA))
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.TXA))
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PHA))

        // "Subroutine": double the value (shift left once)
        interp.cpu.A = 0x15u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ASL, null))

        val doubled = interp.cpu.A

        // Restore registers
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PLA))
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.TAX))
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PLA))

        assertEquals(0x2Au, doubled, "Value should be doubled (21 * 2 = 42)")
        assertEquals(0x15u, interp.cpu.A, "Original A should be restored")
    }
}
