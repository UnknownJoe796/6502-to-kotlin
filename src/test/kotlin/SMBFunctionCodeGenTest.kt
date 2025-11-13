package com.ivieleague.decompiler6502tokotlin

import com.ivieleague.decompiler6502tokotlin.hand.*
import com.ivieleague.decompiler6502tokotlin.hand.stages.SMBTestFixtures
import org.junit.Test
import java.io.File

/**
 * Tests that generate Kotlin code for actual SMB functions.
 *
 * These tests demonstrate the complete decompilation pipeline:
 * 1. Parse assembly from smbdism.asm
 * 2. Build blocks and control flow
 * 3. Generate Kotlin code
 * 4. Show the final output
 */
class SMBFunctionCodeGenTest {

    /**
     * Generate Kotlin code for a simple function and print it.
     */
    private fun generateAndPrintFunction(functionName: String) {
        println("\n" + "=".repeat(80))
        println("FUNCTION: $functionName")
        println("=".repeat(80))

        try {
            // Load the function
            val function = SMBTestFixtures.loadFunction(functionName)
            val blocks = SMBTestFixtures.getFunctionBlocks(functionName)

            println("\nFunction Info:")
            println("  Entry: ${function.startingBlock.label}")
            println("  Blocks: ${blocks.size}")
            println("  Lines: ${blocks.sumOf { it.lines.size }}")

            println("\nOriginal Assembly:")
            println("-".repeat(80))
            blocks.forEach { block ->
                if (block.label != null) {
                    println("${block.label}:")
                }
                block.lines.forEach { line ->
                    val label = line.label?.padEnd(20) ?: "".padEnd(20)
                    val inst = line.instruction?.toString() ?: line.data?.toString() ?: ""
                    val comment = line.comment?.let { "  ; $it" } ?: ""
                    println("  $label$inst$comment")
                }
            }

            println("\nGenerated Kotlin Code:")
            println("-".repeat(80))

            // Generate Kotlin code for each block
            val ctx = CodeGenContext()
            blocks.forEach { block ->
                if (block.label != null) {
                    println("// Block: ${block.label}")
                }

                block.lines.forEach { line ->
                    line.instruction?.let { instruction ->
                        val stmts = instruction.toKotlin(ctx)
                        stmts.forEach { stmt ->
                            println(stmt.toKotlin("  "))
                        }
                    }
                }
                println()
            }

        } catch (e: Exception) {
            println("\n❌ Error generating code for $functionName:")
            println("  ${e.message}")
            e.printStackTrace()
        }
    }

    @Test
    fun testDecTimersCodeGen() {
        generateAndPrintFunction("DecTimers")
    }

    @Test
    fun testRotPRandomBitCodeGen() {
        generateAndPrintFunction("RotPRandomBit")
    }

    @Test
    fun testInitBufferCodeGen() {
        generateAndPrintFunction("InitBuffer")
    }

    @Test
    fun testMultipleFunctions() {
        val functions = listOf(
            "DecTimers",
            "RotPRandomBit",
            "InitBuffer",
            "NoDecTimers"
        )

        println("\n" + "=".repeat(80))
        println("MULTIPLE FUNCTION CODE GENERATION TEST")
        println("=".repeat(80))

        for (funcName in functions) {
            try {
                generateAndPrintFunction(funcName)
            } catch (e: Exception) {
                println("\n⚠️  Skipping $funcName: ${e.message}")
            }
        }
    }

    @Test
    fun testCodeGenStats() {
        println("\n" + "=".repeat(80))
        println("CODE GENERATION STATISTICS")
        println("=".repeat(80))

        val testFunctions = listOf(
            "DecTimers",
            "RotPRandomBit",
            "InitBuffer",
            "NoDecTimers",
            "PauseSkip",
            "Sprite0Clr"
        )

        val stats = mutableListOf<Triple<String, Int, Int>>()

        for (funcName in testFunctions) {
            try {
                val function = SMBTestFixtures.loadFunction(funcName)
                val blocks = SMBTestFixtures.getFunctionBlocks(funcName)
                val asmLines = blocks.sumOf { it.lines.size }

                // Count generated Kotlin statements
                val ctx = CodeGenContext()
                var kotlinStmts = 0
                blocks.forEach { block ->
                    block.lines.forEach { line ->
                        line.instruction?.let { instruction ->
                            kotlinStmts += instruction.toKotlin(ctx).size
                        }
                    }
                }

                stats.add(Triple(funcName, asmLines, kotlinStmts))
            } catch (e: Exception) {
                // Skip functions that can't be loaded
            }
        }

        println("\n| Function | ASM Lines | Kotlin Stmts | Ratio |")
        println("|----------|-----------|--------------|-------|")
        for ((name, asm, kotlin) in stats.sortedByDescending { it.third }) {
            val ratio = if (asm > 0) kotlin.toDouble() / asm else 0.0
            println("| ${name.padEnd(20)} | ${asm.toString().padStart(9)} | ${kotlin.toString().padStart(12)} | ${String.format("%.2f", ratio).padStart(5)} |")
        }

        val totalAsm = stats.sumOf { it.second }
        val totalKotlin = stats.sumOf { it.third }
        val avgRatio = if (totalAsm > 0) totalKotlin.toDouble() / totalAsm else 0.0

        println("|----------|-----------|--------------|-------|")
        println("| ${"TOTAL".padEnd(20)} | ${totalAsm.toString().padStart(9)} | ${totalKotlin.toString().padStart(12)} | ${String.format("%.2f", avgRatio).padStart(5)} |")

        println("\nSummary:")
        println("  Functions tested: ${stats.size}")
        println("  Total ASM lines: $totalAsm")
        println("  Total Kotlin statements: $totalKotlin")
        println("  Average expansion: ${String.format("%.2f", avgRatio)}x")
    }

    @Test
    fun testSaveGeneratedCodeToFile() {
        val functionName = "DecTimers"
        val outputFile = File("outputs/DecTimers-generated.kt")

        outputFile.parentFile?.mkdirs()

        try {
            val function = SMBTestFixtures.loadFunction(functionName)
            val blocks = SMBTestFixtures.getFunctionBlocks(functionName)

            outputFile.bufferedWriter().use { writer ->
                writer.write("// Generated from SMB function: $functionName\n")
                writer.write("// Total blocks: ${blocks.size}\n\n")

                writer.write("fun ${assemblyLabelToKotlinName(functionName)}() {\n")

                val ctx = CodeGenContext()
                blocks.forEach { block ->
                    if (block.label != null) {
                        writer.write("    // Block: ${block.label}\n")
                    }

                    block.lines.forEach { line ->
                        line.instruction?.let { instruction ->
                            val stmts = instruction.toKotlin(ctx)
                            stmts.forEach { stmt ->
                                writer.write(stmt.toKotlin("    ") + "\n")
                            }
                        }
                    }
                    writer.write("\n")
                }

                writer.write("}\n")
            }

            println("\n✅ Generated code saved to: ${outputFile.absolutePath}")
            println("   File size: ${outputFile.length()} bytes")

        } catch (e: Exception) {
            println("\n❌ Error saving generated code:")
            println("  ${e.message}")
        }
    }
}
