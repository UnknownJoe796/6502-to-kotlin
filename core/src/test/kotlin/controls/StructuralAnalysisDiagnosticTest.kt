package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Test
import java.io.File

// by Claude - Diagnostic test to analyze all SMB functions with the new structural analysis
// This helps identify which patterns need to be fixed

class StructuralAnalysisDiagnosticTest {

    @Test
    fun `diagnose all SMB functions with new structural analysis`() {
        // Load SMB assembly - try multiple paths
        val possiblePaths = listOf(
            "../smbdism.asm",
            "../smb/smbdism.asm",
            "smbdism.asm",
            "smb/smbdism.asm"
        )
        val asmFile = possiblePaths.map { File(it) }.firstOrNull { it.exists() }
        if (asmFile == null) {
            println("SMB assembly file not found at any of: $possiblePaths")
            println("Working directory: ${File(".").absolutePath}")
            return
        }
        println("Using SMB file: ${asmFile.absolutePath}")

        val codeFile = asmFile.readText().parseToAssemblyCodeFile()
        val blocks = codeFile.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        println("=" .repeat(80))
        println("STRUCTURAL ANALYSIS DIAGNOSTIC REPORT")
        println("=" .repeat(80))
        println("Total functions: ${functions.size}")
        println()

        // Categorize results
        var fullyStructured = 0
        var hasUnstructured = 0
        var hasMissing = 0
        var failed = 0

        val unstructuredByReason = mutableMapOf<String, MutableList<String>>()
        val functionsWithIssues = mutableListOf<Pair<String, StructureResult>>()
        val failedFunctions = mutableListOf<Pair<String, Exception>>()

        for (func in functions) {
            val funcLabel = func.startingBlock.label ?: "@${func.startingBlock.originalLineIndex}"

            try {
                val result = func.analyzeControlsStructured(useStrict = false)
                val validation = result.validation

                if (validation == null) {
                    failed++
                    failedFunctions.add(funcLabel to RuntimeException("validation is null"))
                    continue
                }

                when {
                    validation.missingEdges.isNotEmpty() -> {
                        hasMissing++
                        functionsWithIssues.add(funcLabel to result)
                    }
                    validation.unstructuredEdges.isNotEmpty() -> {
                        hasUnstructured++
                        functionsWithIssues.add(funcLabel to result)
                        // Categorize by reason
                        for ((_, reason) in validation.unstructuredEdges) {
                            unstructuredByReason.getOrPut(reason) { mutableListOf() }.add(funcLabel)
                        }
                    }
                    else -> {
                        fullyStructured++
                    }
                }
            } catch (e: Exception) {
                failed++
                failedFunctions.add(funcLabel to e)
            }
        }

        println("SUMMARY")
        println("-" .repeat(40))
        println("Fully structured:     $fullyStructured")
        println("Has unstructured:     $hasUnstructured")
        println("Has missing edges:    $hasMissing")
        println("Failed with exception: $failed")
        println()

        if (failedFunctions.isNotEmpty()) {
            println("FAILED FUNCTIONS")
            println("-" .repeat(40))
            for ((funcLabel, e) in failedFunctions) {
                println("$funcLabel: ${e::class.simpleName} - ${e.message?.take(100)}")
            }
            println()
        }

        if (unstructuredByReason.isNotEmpty()) {
            println("UNSTRUCTURED EDGES BY REASON")
            println("-" .repeat(40))
            for ((reason, funcs) in unstructuredByReason.entries.sortedByDescending { it.value.size }) {
                println("$reason: ${funcs.size} occurrences")
                if (funcs.size <= 5) {
                    funcs.forEach { println("  - $it") }
                } else {
                    funcs.take(3).forEach { println("  - $it") }
                    println("  ... and ${funcs.size - 3} more")
                }
            }
            println()
        }

        // Show details for first few problematic functions
        println("DETAILED ISSUES (first 10)")
        println("-" .repeat(40))
        for ((funcLabel, result) in functionsWithIssues.take(10)) {
            val validation = result.validation!!
            println()
            println("Function: $funcLabel")
            println("  Total edges: ${validation.allEdges.size}")
            println("  Consumed: ${validation.consumedEdges.size}")
            println("  Unstructured: ${validation.unstructuredEdges.size}")
            println("  Missing: ${validation.missingEdges.size}")

            if (validation.missingEdges.isNotEmpty()) {
                println("  MISSING EDGES:")
                for (edge in validation.missingEdges.take(5)) {
                    println("    - $edge")
                }
            }

            if (validation.unstructuredEdges.isNotEmpty()) {
                println("  UNSTRUCTURED EDGES:")
                for ((edge, reason) in validation.unstructuredEdges.entries.take(5)) {
                    println("    - $edge: $reason")
                }
            }
        }

        println()
        println("=" .repeat(80))
        println("To fix: Focus on the most common unstructured reasons first")
        println("=" .repeat(80))
    }

    @Test
    fun `compare old vs new analysis on problematic functions`() {
        val asmFile = listOf("../smbdism.asm", "../smb/smbdism.asm", "smbdism.asm").map { File(it) }.firstOrNull { it.exists() }
        if (asmFile == null) {
            println("SMB assembly file not found, skipping diagnostic")
            return
        }

        val codeFile = asmFile.readText().parseToAssemblyCodeFile()
        val blocks = codeFile.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        // Known problematic functions from previous debugging
        val targetFunctions = listOf(
            "imposeFriction",
            "enemyTurnAround",
            "ProcessCannons",
            "FloateyNumbersRoutine"
        )

        for (targetName in targetFunctions) {
            val func = functions.find { it.startingBlock.label == targetName }
            if (func == null) {
                println("Function $targetName not found")
                continue
            }

            println()
            println("=" .repeat(60))
            println(func.compareControlAnalysis())
        }
    }

    @Test
    fun `detect 6502 patterns in SMB`() {
        val asmFile = listOf("../smbdism.asm", "../smb/smbdism.asm", "smbdism.asm").map { File(it) }.firstOrNull { it.exists() }
        if (asmFile == null) {
            println("SMB assembly file not found, skipping diagnostic")
            return
        }

        val codeFile = asmFile.readText().parseToAssemblyCodeFile()
        val blocks = codeFile.lines.blockify()

        val branchPairs = blocks.detectConsecutiveBranchPairs()
        val bitSkipPatterns = blocks.detectBitSkipPatterns()
        val jumpEnginePatterns = blocks.detectJumpEnginePatterns()

        println(generate6502PatternSummary(branchPairs, bitSkipPatterns, jumpEnginePatterns))
    }
}
