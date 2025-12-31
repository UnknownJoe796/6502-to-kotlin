package com.ivieleague.decompiler6502tokotlin.validation

import com.ivieleague.decompiler6502tokotlin.hand.*
import java.io.File
import kotlin.test.Test

/**
 * Debug specific validation failures
 */
class DebugFailures {

    @Test
    fun debugWritePPUReg1() {
        val assemblyFile = File("outputs/smbdism-annotated.asm")
        val constantsFile = File("outputs/smb-constants.kt")

        println("\n=== Debugging WritePPUReg1 ===\n")

        // Extract assembly
        val assembly = AutomatedFunctionExtractor.extractAssemblyFunction(assemblyFile, "WritePPUReg1")
        println("Original Assembly:")
        println(assembly)

        // Load constants
        val constants = ConstantsLoader.loadConstants(constantsFile)
        println("\n\nRelevant Constants:")
        constants.filter { it.key.contains("PPU") }.forEach { (name, addr) ->
            println("  $name = 0x${addr.toString(16).uppercase().padStart(4, '0')}")
        }

        // Preprocess
        val preprocessed = ConstantsLoader.preprocessAssembly(assembly!!, constantsFile)
        println("\n\nPreprocessed Assembly:")
        println(preprocessed)

        // Try to parse
        try {
            val parsed = preprocessed.parseToAssemblyCodeFile()
            println("\n\nParsed Successfully!")
            println("Lines: ${parsed.lines.size}")
            parsed.lines.forEach { line ->
                line.instruction?.let { instr ->
                    println("  ${instr.op} ${instr.address}")
                }
            }
        } catch (e: Exception) {
            println("\n\nParsing FAILED: ${e.message}")
            e.printStackTrace()
        }
    }

    @Test
    fun debugMissingFunctions() {
        val kotlinFile = File("outputs/smb-decompiled.kt")

        val missingLabels = listOf(
            "SetVRAMOffset",
            "IncSubtask",
            "IncModeTask_B",
            "NoInter",
            "OutputCol"
        )

        println("\n=== Debugging Missing Function Extraction ===\n")

        missingLabels.forEach { label ->
            println("--- Searching for $label ---")

            // Search for the label in comments
            val found = kotlinFile.readLines().any { line ->
                line.contains("//> $label:")
            }

            if (found) {
                println("✓ Found in comments")

                // Find the function
                val result = AutomatedFunctionExtractor.findKotlinFunction(kotlinFile, label)
                if (result != null) {
                    println("✓ Found function: ${result.first}")
                } else {
                    println("✗ Could not extract function (comment found but function not located)")

                    // Show context
                    val lines = kotlinFile.readLines()
                    val commentIdx = lines.indexOfFirst { it.contains("//> $label:") }
                    if (commentIdx >= 0) {
                        println("\nContext (lines ${commentIdx-5} to ${commentIdx+5}):")
                        for (i in maxOf(0, commentIdx - 5)..minOf(lines.size - 1, commentIdx + 5)) {
                            val marker = if (i == commentIdx) ">>>" else "   "
                            println("$marker ${i.toString().padStart(5)}: ${lines[i]}")
                        }
                    }
                }
            } else {
                println("✗ Not found in decompiled output")
            }
            println()
        }
    }
}
