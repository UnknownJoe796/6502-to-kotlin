package com.ivieleague.decompiler6502tokotlin.validation

import com.ivieleague.decompiler6502tokotlin.hand.*
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Tests for the code generation component of the validation framework.
 * These tests demonstrate that we can generate Kotlin code from 6502 instructions.
 */
class CodeGenerationTest {

    private val decompiler = SimpleDecompiler()

    @Test
    fun testGenerateSimpleLoadStore() {
        val instructions = listOf(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(
                AssemblyOp.STA,
                AssemblyAddressing.Direct("\$1000")
            )
        )

        val kotlinCode = decompiler.generateKotlinCode(instructions)

        println("Generated Kotlin for simple load/store:")
        println(kotlinCode)
        println()

        // Verify key elements are present
        assertTrue(kotlinCode.contains("class Generated6502"))
        assertTrue(kotlinCode.contains("var A:"))
        assertTrue(kotlinCode.contains("A = 66u")) // 0x42 = 66
        assertTrue(kotlinCode.contains("memory[4096]")) // 0x1000 = 4096
    }

    @Test
    fun testGenerateArithmetic() {
        val instructions = listOf(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x10u, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(AssemblyOp.CLC),
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x20u, AssemblyAddressing.Radix.Hex)
            )
        )

        val kotlinCode = decompiler.generateKotlinCode(instructions)

        println("Generated Kotlin for arithmetic:")
        println(kotlinCode)
        println()

        // Verify arithmetic code generation
        assertTrue(kotlinCode.contains("LDA"))
        assertTrue(kotlinCode.contains("CLC"))
        assertTrue(kotlinCode.contains("ADC"))
        assertTrue(kotlinCode.contains("C = false")) // CLC
        assertTrue(kotlinCode.contains("result")) // ADC generates result variable
    }

    @Test
    fun testGenerateComparison() {
        val instructions = listOf(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(
                AssemblyOp.CMP,
                AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)
            )
        )

        val kotlinCode = decompiler.generateKotlinCode(instructions)

        println("Generated Kotlin for comparison:")
        println(kotlinCode)
        println()

        // Verify comparison code
        assertTrue(kotlinCode.contains("CMP"))
        assertTrue(kotlinCode.contains("cmpResult"))
        assertTrue(kotlinCode.contains("Z ="))
        assertTrue(kotlinCode.contains("C ="))
    }

    @Test
    fun testGenerateIndexedAddressing() {
        val instructions = listOf(
            AssemblyInstruction(
                AssemblyOp.LDX,
                AssemblyAddressing.ByteValue(0x05u, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.DirectX("\$1000")
            )
        )

        val kotlinCode = decompiler.generateKotlinCode(instructions)

        println("Generated Kotlin for indexed addressing:")
        println(kotlinCode)
        println()

        // Verify indexed addressing
        assertTrue(kotlinCode.contains("X = 5u"))
        assertTrue(kotlinCode.contains("4096 + X.toInt()")) // $1000,X
    }

    @Test
    fun testGenerateLoop() {
        val instructions = listOf(
            // LDA #$00
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x00u, AssemblyAddressing.Radix.Hex)
            ),
            // STA $01
            AssemblyInstruction(
                AssemblyOp.STA,
                AssemblyAddressing.Direct("\$01")
            ),
            // LDA $01
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.Direct("\$01")
            ),
            // CLC
            AssemblyInstruction(AssemblyOp.CLC),
            // ADC $00
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.Direct("\$00")
            ),
            // STA $01
            AssemblyInstruction(
                AssemblyOp.STA,
                AssemblyAddressing.Direct("\$01")
            ),
            // DEC $00
            AssemblyInstruction(
                AssemblyOp.DEC,
                AssemblyAddressing.Direct("\$00")
            )
        )

        val kotlinCode = decompiler.generateKotlinCode(instructions)

        println("Generated Kotlin for loop body:")
        println(kotlinCode)
        println()

        // Verify loop-related instructions
        assertTrue(kotlinCode.contains("DEC"))
        assertTrue(kotlinCode.contains("decValue"))
        assertTrue(kotlinCode.contains("memory[0]")) // $00
        assertTrue(kotlinCode.contains("memory[1]")) // $01
    }

    @Test
    fun testGenerateShifts() {
        val instructions = listOf(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0xAAu, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(AssemblyOp.ASL, null), // Accumulator mode
            AssemblyInstruction(
                AssemblyOp.STA,
                AssemblyAddressing.Direct("\$2000")
            ),
            AssemblyInstruction(
                AssemblyOp.LSR,
                AssemblyAddressing.Direct("\$2000")
            )
        )

        val kotlinCode = decompiler.generateKotlinCode(instructions)

        println("Generated Kotlin for shift operations:")
        println(kotlinCode)
        println()

        // Verify shift code
        assertTrue(kotlinCode.contains("ASL"))
        assertTrue(kotlinCode.contains("LSR"))
        assertTrue(kotlinCode.contains("shl"))
        assertTrue(kotlinCode.contains("ushr"))
    }

    @Test
    fun testGenerateBitManipulation() {
        val instructions = listOf(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x0Fu, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(
                AssemblyOp.ORA,
                AssemblyAddressing.ByteValue(0xF0u, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(
                AssemblyOp.AND,
                AssemblyAddressing.ByteValue(0xAAu, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(
                AssemblyOp.EOR,
                AssemblyAddressing.ByteValue(0x55u, AssemblyAddressing.Radix.Hex)
            )
        )

        val kotlinCode = decompiler.generateKotlinCode(instructions)

        println("Generated Kotlin for bit manipulation:")
        println(kotlinCode)
        println()

        // Verify bitwise operations
        assertTrue(kotlinCode.contains("ORA"))
        assertTrue(kotlinCode.contains("AND"))
        assertTrue(kotlinCode.contains("EOR"))
        assertTrue(kotlinCode.contains(" or "))
        assertTrue(kotlinCode.contains(" and "))
        assertTrue(kotlinCode.contains(" xor "))
    }

    @Test
    fun testGenerateTransfers() {
        val instructions = listOf(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(AssemblyOp.TAX),
            AssemblyInstruction(AssemblyOp.TAY),
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x99u, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(AssemblyOp.TXA)
        )

        val kotlinCode = decompiler.generateKotlinCode(instructions)

        println("Generated Kotlin for register transfers:")
        println(kotlinCode)
        println()

        // Verify transfer code
        assertTrue(kotlinCode.contains("TAX"))
        assertTrue(kotlinCode.contains("TAY"))
        assertTrue(kotlinCode.contains("TXA"))
        assertTrue(kotlinCode.contains("X = A"))
        assertTrue(kotlinCode.contains("Y = A"))
        assertTrue(kotlinCode.contains("A = X"))
    }

    @Test
    fun testGenerateStackOperations() {
        val instructions = listOf(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x55u, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(AssemblyOp.PHA),
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0xAAu, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(AssemblyOp.PLA)
        )

        val kotlinCode = decompiler.generateKotlinCode(instructions)

        println("Generated Kotlin for stack operations:")
        println(kotlinCode)
        println()

        // Verify stack code
        assertTrue(kotlinCode.contains("PHA"))
        assertTrue(kotlinCode.contains("PLA"))
        assertTrue(kotlinCode.contains("0x0100")) // Stack base
        assertTrue(kotlinCode.contains("SP"))
    }
}
