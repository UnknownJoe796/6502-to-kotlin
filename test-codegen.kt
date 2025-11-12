#!/usr/bin/env kotlin

// Quick script to test code generation without full build
// Run with: kotlinc -script test-codegen.kt

import com.ivieleague.decompiler6502tokotlin.hand.*

fun main() {
    println("=".repeat(60))
    println("CODE GENERATION TEST")
    println("=".repeat(60))

    val testCases = listOf(
        "LDA #\$42" to listOf(
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex))
        ),
        "LDA #\$42 / TAX" to listOf(
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)),
            AssemblyInstruction(AssemblyOp.TAX)
        ),
        "INX" to listOf(
            AssemblyInstruction(AssemblyOp.INX)
        ),
        "LDA #\$10 / ADC #\$20" to listOf(
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.ByteValue(0x10u, AssemblyAddressing.Radix.Hex)),
            AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.ByteValue(0x20u, AssemblyAddressing.Radix.Hex))
        ),
        "LDA #\$FF / AND #\$0F" to listOf(
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.ByteValue(0xFFu, AssemblyAddressing.Radix.Hex)),
            AssemblyInstruction(AssemblyOp.AND, AssemblyAddressing.ByteValue(0x0Fu, AssemblyAddressing.Radix.Hex))
        )
    )

    for ((name, instructions) in testCases) {
        println("\n--- $name ---")
        val ctx = CodeGenContext()
        val allStmts = instructions.flatMap { it.toKotlin(ctx) }

        if (allStmts.isEmpty()) {
            println("  (no code generated)")
        } else {
            allStmts.forEach { stmt ->
                println("  ${stmt.toKotlin()}")
            }
        }
    }

    println("\n" + "=".repeat(60))
}

main()
