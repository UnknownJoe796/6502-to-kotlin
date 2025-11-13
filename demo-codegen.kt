#!/usr/bin/env kotlin

/**
 * Standalone demo of SMB function code generation.
 *
 * This script demonstrates the complete decompilation pipeline:
 * 1. Load SMB function from smbdism.asm
 * 2. Generate Kotlin code
 * 3. Display the results
 *
 * Run with: kotlinc -script demo-codegen.kt
 */

@file:DependsOn(".")

import com.ivieleague.decompiler6502tokotlin.hand.*
import com.ivieleague.decompiler6502tokotlin.hand.stages.SMBTestFixtures

fun main() {
    println("=" .repeat(80))
    println("6502-to-Kotlin Code Generation Demo")
    println("=" .repeat(80))

    val functionName = "DecTimers"

    try {
        println("\n📦 Loading function: $functionName")
        val function = SMBTestFixtures.loadFunction(functionName)
        val blocks = SMBTestFixtures.getFunctionBlocks(functionName)

        println("✓ Function loaded successfully")
        println("  Entry point: ${function.startingBlock.label}")
        println("  Total blocks: ${blocks.size}")
        println("  Total lines: ${blocks.sumOf { it.lines.size }}")

        println("\n📝 Original Assembly:")
        println("-".repeat(80))

        var lineCount = 0
        blocks.forEach { block ->
            if (block.label != null) {
                println("${block.label}:")
            }
            block.lines.forEach { line ->
                lineCount++
                val label = line.label?.padEnd(20) ?: "".padEnd(20)
                val inst = line.instruction?.toString() ?: line.data?.toString() ?: ""
                val comment = line.comment?.let { "  ; $it" } ?: ""
                println("  $label$inst$comment")
            }
        }

        println("\n🔄 Generating Kotlin code...")
        val ctx = CodeGenContext()
        var stmtCount = 0

        println("\n✨ Generated Kotlin Code:")
        println("-".repeat(80))
        println("fun ${assemblyLabelToKotlinName(functionName)}() {")

        blocks.forEach { block ->
            if (block.label != null) {
                println("  // Block: ${block.label}")
            }

            block.lines.forEach { line ->
                line.instruction?.let { instruction ->
                    val stmts = instruction.toKotlin(ctx)
                    stmts.forEach { stmt ->
                        stmtCount++
                        println(stmt.toKotlin("  "))
                    }
                }
            }
            println()
        }

        println("}")

        println("\n📊 Statistics:")
        println("-".repeat(80))
        println("  Assembly lines: $lineCount")
        println("  Kotlin statements: $stmtCount")
        println("  Expansion ratio: ${String.format("%.2f", stmtCount.toDouble() / lineCount)}x")

        println("\n✅ Code generation completed successfully!")

    } catch (e: Exception) {
        println("\n❌ Error during code generation:")
        println("  ${e.message}")
        e.printStackTrace()
    }
}

main()
