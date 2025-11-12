package com.ivieleague.decompiler6502tokotlin

import com.ivieleague.decompiler6502tokotlin.hand.*
import org.junit.Test

/**
 * Quick test to see what code is being generated after our fixes.
 */
class CodeGenQuickTest {

    @Test
    fun testSimpleLoadCodeGen() {
        val instruction = AssemblyInstruction(
            AssemblyOp.LDA,
            AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)
        )

        val ctx = CodeGenContext()
        val stmts = instruction.toKotlin(ctx)

        println("\n=== LDA #\$42 Generated Code ===")
        stmts.forEach { println(it.toKotlin()) }
    }

    @Test
    fun testTransferCodeGen() {
        val instructions = listOf(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(AssemblyOp.TAX)
        )

        val ctx = CodeGenContext()
        val allStmts = instructions.flatMap { it.toKotlin(ctx) }

        println("\n=== LDA #\$42 / TAX Generated Code ===")
        allStmts.forEach { println(it.toKotlin()) }
    }

    @Test
    fun testArithmeticCodeGen() {
        val instructions = listOf(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x10u, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x20u, AssemblyAddressing.Radix.Hex)
            )
        )

        val ctx = CodeGenContext()
        val allStmts = instructions.flatMap { it.toKotlin(ctx) }

        println("\n=== LDA #\$10 / ADC #\$20 Generated Code ===")
        allStmts.forEach { println(it.toKotlin()) }
    }

    @Test
    fun testIncrementCodeGen() {
        val instruction = AssemblyInstruction(AssemblyOp.INX)

        val ctx = CodeGenContext()
        val stmts = instruction.toKotlin(ctx)

        println("\n=== INX Generated Code ===")
        stmts.forEach { println(it.toKotlin()) }
    }

    @Test
    fun testLogicalCodeGen() {
        val instructions = listOf(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0xFFu, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(
                AssemblyOp.AND,
                AssemblyAddressing.ByteValue(0x0Fu, AssemblyAddressing.Radix.Hex)
            )
        )

        val ctx = CodeGenContext()
        val allStmts = instructions.flatMap { it.toKotlin(ctx) }

        println("\n=== LDA #\$FF / AND #\$0F Generated Code ===")
        allStmts.forEach { println(it.toKotlin()) }
    }

    @Test
    fun testAllBasicInstructions() {
        val testCases = listOf(
            "LDA #\$42" to AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.ByteValue(0x42u)),
            "LDX #\$42" to AssemblyInstruction(AssemblyOp.LDX, AssemblyAddressing.ByteValue(0x42u)),
            "LDY #\$42" to AssemblyInstruction(AssemblyOp.LDY, AssemblyAddressing.ByteValue(0x42u)),
            "TAX" to AssemblyInstruction(AssemblyOp.TAX),
            "TAY" to AssemblyInstruction(AssemblyOp.TAY),
            "TXA" to AssemblyInstruction(AssemblyOp.TXA),
            "TYA" to AssemblyInstruction(AssemblyOp.TYA),
            "INX" to AssemblyInstruction(AssemblyOp.INX),
            "INY" to AssemblyInstruction(AssemblyOp.INY),
            "DEX" to AssemblyInstruction(AssemblyOp.DEX),
            "DEY" to AssemblyInstruction(AssemblyOp.DEY)
        )

        println("\n" + "=".repeat(60))
        println("ALL BASIC INSTRUCTIONS CODE GENERATION")
        println("=".repeat(60))

        for ((name, instruction) in testCases) {
            println("\n--- $name ---")
            val ctx = CodeGenContext()
            val stmts = instruction.toKotlin(ctx)
            if (stmts.isEmpty()) {
                println("  (no code generated)")
            } else {
                stmts.forEach { println("  ${it.toKotlin()}") }
            }
        }
    }
}
