package com.ivieleague.decompiler6502tokotlin.interpreter

import com.ivieleague.decompiler6502tokotlin.hand.*
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Real-world 6502 program tests simulating actual use cases.
 * Based on common patterns from NES games and 6502 software.
 */
class Interpreter6502RealWorldTest {

    @Test
    fun testMemoryCopy() {
        // Simulate copying a block of memory
        val interp = Interpreter6502()
        interp.labelResolver = { label ->
            when (label) {
                "source" -> 0x2000
                "dest" -> 0x3000
                else -> 0
            }
        }

        // Source data
        val sourceData = listOf<UByte>(0x11u, 0x22u, 0x33u, 0x44u, 0x55u)
        interp.memory.loadProgram(0x2000, sourceData)

        // Copy loop simulation
        for (i in 0 until 5) {
            interp.cpu.X = i.toUByte()

            // LDA source,X
            interp.executeInstruction(
                AssemblyInstruction(
                    AssemblyOp.LDA,
                    AssemblyAddressing.DirectX("source")
                )
            )

            // STA dest,X
            interp.executeInstruction(
                AssemblyInstruction(
                    AssemblyOp.STA,
                    AssemblyAddressing.DirectX("dest")
                )
            )
        }

        // Verify copy
        for (i in 0 until 5) {
            assertEquals(
                sourceData[i],
                interp.memory.readByte(0x3000 + i),
                "Byte $i should be copied correctly"
            )
        }
    }

    @Test
    fun testMemoryFill() {
        // Fill memory with a value (common pattern)
        val interp = Interpreter6502()
        interp.labelResolver = { label -> if (label == "buffer") 0x2000 else 0 }

        val fillValue = 0xAAu
        val size = 256

        // LDA #fillValue
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(fillValue, AssemblyAddressing.Radix.Hex)
            )
        )

        // Fill loop
        for (i in 0 until size) {
            interp.cpu.X = i.toUByte()
            interp.executeInstruction(
                AssemblyInstruction(
                    AssemblyOp.STA,
                    AssemblyAddressing.DirectX("buffer")
                )
            )
        }

        // Verify fill
        for (i in 0 until size) {
            assertEquals(fillValue, interp.memory.readByte(0x2000 + i))
        }
    }

    @Test
    fun testCountdown() {
        // Common countdown loop pattern
        val interp = Interpreter6502()

        // LDX #10
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDX,
                AssemblyAddressing.ByteValue(10u, AssemblyAddressing.Radix.Decimal)
            )
        )

        var iterations = 0
        while (iterations < 20) {  // Safety limit
            // DEX
            interp.executeInstruction(AssemblyInstruction(AssemblyOp.DEX))

            // Check if zero
            if (interp.cpu.Z) break
            iterations++
        }

        assertEquals(10, iterations, "Should count down from 10 to 0")
        assertEquals(0x00u, interp.cpu.X)
    }

    @Test
    fun testBitMasking() {
        // Common bit manipulation pattern
        val interp = Interpreter6502()

        // Set bit 7
        interp.cpu.A = 0x00u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ORA,
                AssemblyAddressing.ByteValue(0x80u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x80u, interp.cpu.A)

        // Clear bit 7
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.AND,
                AssemblyAddressing.ByteValue(0x7Fu, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x00u, interp.cpu.A)

        // Toggle bits
        interp.cpu.A = 0b10101010u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.EOR,
                AssemblyAddressing.ByteValue(0b11111111u, AssemblyAddressing.Radix.Binary)
            )
        )
        assertEquals(0b01010101u, interp.cpu.A)
    }

    @Test
    fun testNESStyleSpriteUpdate() {
        // Simulate updating sprite attributes (common NES pattern)
        val interp = Interpreter6502()
        interp.labelResolver = { label ->
            when (label) {
                "sprite_x" -> 0x0200
                "sprite_y" -> 0x0201
                "sprite_tile" -> 0x0202
                "sprite_attr" -> 0x0203
                else -> 0
            }
        }

        // Set sprite X position
        interp.cpu.A = 100u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.STA,
                AssemblyAddressing.Direct("sprite_x")
            )
        )

        // Set sprite Y position
        interp.cpu.A = 50u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.STA,
                AssemblyAddressing.Direct("sprite_y")
            )
        )

        // Set sprite tile
        interp.cpu.A = 0x42u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.STA,
                AssemblyAddressing.Direct("sprite_tile")
            )
        )

        // Set sprite attributes
        interp.cpu.A = 0x03u  // Palette 3
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.STA,
                AssemblyAddressing.Direct("sprite_attr")
            )
        )

        // Verify
        assertEquals(100u, interp.memory.readByte(0x0200))
        assertEquals(50u, interp.memory.readByte(0x0201))
        assertEquals(0x42u, interp.memory.readByte(0x0202))
        assertEquals(0x03u, interp.memory.readByte(0x0203))
    }

    @Test
    fun test16BitIncrement() {
        // Increment a 16-bit value in memory
        val interp = Interpreter6502()
        interp.labelResolver = { label ->
            when (label) {
                "counter_lo" -> 0x10
                "counter_hi" -> 0x11
                else -> 0
            }
        }

        // Initialize counter to 0x00FF
        interp.memory.writeByte(0x10, 0xFFu)
        interp.memory.writeByte(0x11, 0x00u)

        // Increment low byte
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.INC,
                AssemblyAddressing.Direct("counter_lo")
            )
        )

        // Check if zero (overflow)
        val shouldIncrementHigh = interp.cpu.Z

        // Conditionally increment high byte
        if (shouldIncrementHigh) {
            interp.executeInstruction(
                AssemblyInstruction(
                    AssemblyOp.INC,
                    AssemblyAddressing.Direct("counter_hi")
                )
            )
        }

        // Result should be 0x0100
        assertEquals(0x00u, interp.memory.readByte(0x10))
        assertEquals(0x01u, interp.memory.readByte(0x11))
    }

    @Test
    fun test16BitAddition() {
        // Add two 16-bit numbers using 8-bit operations
        val interp = Interpreter6502()
        interp.labelResolver = { label ->
            when (label) {
                "num1_lo" -> 0x10
                "num1_hi" -> 0x11
                "num2_lo" -> 0x12
                "num2_hi" -> 0x13
                "result_lo" -> 0x14
                "result_hi" -> 0x15
                else -> 0
            }
        }

        // num1 = 0x1234
        interp.memory.writeByte(0x10, 0x34u)
        interp.memory.writeByte(0x11, 0x12u)

        // num2 = 0x5678
        interp.memory.writeByte(0x12, 0x78u)
        interp.memory.writeByte(0x13, 0x56u)

        // Add low bytes
        interp.cpu.C = false
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.Direct("num1_lo")
            )
        )
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.Direct("num2_lo")
            )
        )
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.STA,
                AssemblyAddressing.Direct("result_lo")
            )
        )

        // Add high bytes with carry
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.Direct("num1_hi")
            )
        )
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.Direct("num2_hi")
            )
        )
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.STA,
                AssemblyAddressing.Direct("result_hi")
            )
        )

        // Result should be 0x1234 + 0x5678 = 0x68AC
        assertEquals(0xACu, interp.memory.readByte(0x14))
        assertEquals(0x68u, interp.memory.readByte(0x15))
    }

    @Test
    fun testTableLookup() {
        // Lookup value from a table using index
        val interp = Interpreter6502()
        interp.labelResolver = { label -> if (label == "table") 0x3000 else 0 }

        // Create lookup table
        val table = listOf<UByte>(0x10u, 0x20u, 0x30u, 0x40u, 0x50u, 0x60u, 0x70u, 0x80u)
        interp.memory.loadProgram(0x3000, table)

        // Lookup index 5
        interp.cpu.X = 5u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.DirectX("table")
            )
        )

        assertEquals(0x60u, interp.cpu.A, "Should load value at index 5")
    }

    @Test
    fun testBCDScoreIncrement() {
        // Common pattern: increment BCD score
        val interp = Interpreter6502()
        interp.cpu.D = true  // Decimal mode
        interp.labelResolver = { label ->
            when (label) {
                "score_lo" -> 0x20
                "score_hi" -> 0x21
                else -> 0
            }
        }

        // Initial score: 0099
        interp.memory.writeByte(0x20, 0x99u)
        interp.memory.writeByte(0x21, 0x00u)

        // Add 2 points
        interp.cpu.C = false
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.Direct("score_lo")
            )
        )
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x02u, AssemblyAddressing.Radix.Hex)
            )
        )
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.STA,
                AssemblyAddressing.Direct("score_lo")
            )
        )

        // Handle carry to high byte
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.Direct("score_hi")
            )
        )
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x00u, AssemblyAddressing.Radix.Hex)
            )
        )
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.STA,
                AssemblyAddressing.Direct("score_hi")
            )
        )

        // Score should now be 0101 (in BCD = 101 decimal)
        assertEquals(0x01u, interp.memory.readByte(0x20))
        assertEquals(0x01u, interp.memory.readByte(0x21))
    }

    @Test
    fun testRandomNumberGenerator() {
        // Simple LFSR (Linear Feedback Shift Register) RNG
        val interp = Interpreter6502()
        interp.labelResolver = { label -> if (label == "rng_seed") 0x10 else 0 }

        // Initialize seed
        interp.memory.writeByte(0x10, 0xACu)

        val randoms = mutableListOf<UByte>()

        // Generate 10 random numbers
        repeat(10) {
            // Load seed
            interp.executeInstruction(
                AssemblyInstruction(
                    AssemblyOp.LDA,
                    AssemblyAddressing.Direct("rng_seed")
                )
            )

            // Shift left
            interp.executeInstruction(AssemblyInstruction(AssemblyOp.ASL, null))

            // If carry, XOR with polynomial
            if (interp.cpu.C) {
                interp.executeInstruction(
                    AssemblyInstruction(
                        AssemblyOp.EOR,
                        AssemblyAddressing.ByteValue(0x1Du, AssemblyAddressing.Radix.Hex)
                    )
                )
            }

            // Store new seed
            interp.executeInstruction(
                AssemblyInstruction(
                    AssemblyOp.STA,
                    AssemblyAddressing.Direct("rng_seed")
                )
            )

            randoms.add(interp.cpu.A)
        }

        // Verify we got different values (not all the same)
        val uniqueValues = randoms.toSet()
        assertTrue(uniqueValues.size > 1, "RNG should generate different values")
    }

    @Test
    fun testIndirectJumpTable() {
        // Simulate a jump table using indirect addressing
        val interp = Interpreter6502()
        interp.labelResolver = { label ->
            when (label) {
                "jump_table" -> 0x2000
                "handler1" -> 0x3000
                "handler2" -> 0x3100
                "handler3" -> 0x3200
                else -> 0
            }
        }

        // Set up jump table
        interp.memory.writeWord(0x2000, 0x3000u)  // Handler 1
        interp.memory.writeWord(0x2002, 0x3100u)  // Handler 2
        interp.memory.writeWord(0x2004, 0x3200u)  // Handler 3

        // Select handler 2 (index 1, so offset 2)
        interp.cpu.A = 1u

        // ASL to multiply by 2 (each entry is 2 bytes)
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ASL, null))

        // Transfer to X for indexing
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.TAX))

        // Load handler address (simulated)
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.DirectX("jump_table")
            )
        )

        val handlerLo = interp.cpu.A

        // Load high byte
        interp.cpu.X = (interp.cpu.X.toInt() + 1).toUByte()
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.DirectX("jump_table")
            )
        )

        val handlerHi = interp.cpu.A

        // Verify handler address
        assertEquals(0x00u, handlerLo)
        assertEquals(0x31u, handlerHi)  // 0x3100
    }

    @Test
    fun testStackFrameSimulation() {
        // Simulate function call with stack frame
        val interp = Interpreter6502()

        // Save registers (function prologue)
        interp.cpu.A = 0x42u
        interp.cpu.X = 0x11u
        interp.cpu.Y = 0x22u

        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PHA))
        val savedSP1 = interp.cpu.SP

        interp.cpu.A = interp.cpu.X
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PHA))

        interp.cpu.A = interp.cpu.Y
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PHA))

        // Do some work (modify registers)
        interp.cpu.A = 0xFFu
        interp.cpu.X = 0xAAu
        interp.cpu.Y = 0xBBu

        // Restore registers (function epilogue)
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PLA))
        interp.cpu.Y = interp.cpu.A

        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PLA))
        interp.cpu.X = interp.cpu.A

        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PLA))

        // Verify restored values
        assertEquals(0x42u, interp.cpu.A)
        assertEquals(0x11u, interp.cpu.X)
        assertEquals(0x22u, interp.cpu.Y)
    }

    @Test
    fun testMultiplyBy10() {
        // Multiply a number by 10 using shifts and adds
        val interp = Interpreter6502()

        // Multiply 7 by 10 = 70
        val number = 7u
        interp.cpu.A = number

        // Save original value
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PHA))

        // Multiply by 2 (ASL)
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ASL, null))

        // Save (now A = 14)
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PHA))

        // Multiply by 2 again (now A = 28)
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ASL, null))

        // A = 28, add 14 from stack
        interp.cpu.C = false
        val temp = interp.cpu.A
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PLA))
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(temp, AssemblyAddressing.Radix.Hex)
            )
        )
        // A = 42 (28 + 14)

        // Restore original and add
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PLA))
        // Clean up (original value)

        // Manual calculation: 7 * 8 + 7 * 2 = 56 + 14 = 70
        // But let's do it properly
        interp.cpu.A = number

        // A * 2
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ASL, null))
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ASL, null))
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ASL, null))
        // A = 56 (7 * 8)

        interp.cpu.C = false
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(number, AssemblyAddressing.Radix.Hex)
            )
        )
        // A = 63 (56 + 7)

        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(number, AssemblyAddressing.Radix.Hex)
            )
        )
        // A = 70 (63 + 7)

        assertEquals(70u, interp.cpu.A)
    }
}
