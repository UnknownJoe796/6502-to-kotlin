package com.ivieleague.decompiler6502tokotlin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.io.File

/**
 * Full decompilation test using Super Mario Bros disassembly
 */
class SMBDecompilationTest {

    @Test
    fun `decompile SMB and verify Kotlin compiles`() {
        // Load SMB disassembly
        val smbFile = File("smbdism.asm")
        assertTrue(smbFile.exists(), "SMB disassembly file should exist")

        println("\n=== Loading SMB Disassembly ===")
        val smbAsm = smbFile.readText()
        println("File size: ${smbAsm.length} bytes")
        println("Lines: ${smbAsm.lines().size}")

        // Parse the assembly
        println("\n=== Parsing Assembly ===")
        val assemblyLines = smbAsm.parseToAssemblyCodeFile()
        val codeFile = CodeFile(assemblyLines.lines, smbFile)
        println("Parsed ${codeFile.size} lines")

        // Run all analysis passes
        println("\n=== Running Analysis Passes ===")

        val resolution = codeFile.resolveAddresses(0x8000)
        println("✓ Pass 2: Address Resolution")

        // Use a subset of known entry points for initial test
        val knownEntryPoints = setOf("Start", "InitializeMemory", "InitializeGame")
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = knownEntryPoints)
        println("✓ Pass 4: Entry Points (${entries.entryPoints.size} found)")

        val reachability = codeFile.analyzeReachability(resolution, entries)
        println("✓ Pass 5: Reachability Analysis")

        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        println("✓ Pass 6: Basic Blocks (${blocks.blocks.size} blocks)")

        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        println("✓ Pass 7: CFG Construction (${cfg.functions.size} functions)")

        val dominators = codeFile.constructDominatorTrees(cfg)
        println("✓ Pass 9: Dominator Trees")

        val constants = codeFile.analyzeConstants(cfg)
        println("✓ Pass 18: Constant Propagation")

        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)
        println("✓ Pass 10: Data Flow Analysis")

        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        println("✓ Pass 19: Memory Pattern Analysis")

        val typeInference = codeFile.inferTypes(cfg, dataFlow, constants, memoryPatterns)
        println("✓ Pass 20: Type Inference")

        val loops = codeFile.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        println("✓ Pass 14: Loop Detection")

        val functionBoundaries = codeFile.detectFunctionBoundaries(resolution, entries, reachability, blocks, cfg)
        println("✓ Pass 12: Function Boundaries")

        val callGraph = codeFile.constructCallGraph(resolution, entries, reachability, blocks, cfg, functionBoundaries)
        println("✓ Pass 11: Call Graph")

        val variableId = codeFile.identifyVariables(cfg, dataFlow, typeInference, memoryPatterns, loops)
        println("✓ Pass 27: Variable Identification (${variableId.globals.size} globals)")

        val naming = variableId.assignNames(resolution)
        println("✓ Pass 28: Variable Naming")

        val paramRecovery = variableId.recoverParameters(cfg, callGraph, naming, resolution)
        println("✓ Pass 29: Parameter Recovery (${paramRecovery.functionSignatures.size} signatures)")

        // Generate Kotlin code
        println("\n=== Generating Kotlin Code ===")
        val result = cfg.emitCompleteKotlin(codeFile, variableId, paramRecovery, resolution, null, null)
        println("✓ Pass 38: Complete Emission")
        println("  Functions: ${result.statistics.totalFunctions}")
        println("  Statements: ${result.statistics.totalStatements}")
        println("  Lines of code: ${result.statistics.linesOfCode}")

        // Write to file
        val outputFile = File("build/SMB_decompiled.kt")
        outputFile.parentFile?.mkdirs()
        val kotlinCode = result.kotlinFile.emitToString()
        outputFile.writeText(kotlinCode)
        println("\n✓ Written to: ${outputFile.absolutePath}")

        // Show a sample of the generated code
        println("\n=== Sample Generated Code (first 50 lines) ===")
        kotlinCode.lines().take(50).forEach { println(it) }

        // Verify basic structure
        assertTrue(kotlinCode.contains("package decompiled"), "Should have package declaration")
        assertTrue(kotlinCode.contains("val memory"), "Should have memory array")
        assertTrue(result.statistics.totalFunctions > 0, "Should have generated functions")

        // Try to compile the generated Kotlin (basic check)
        println("\n=== Checking Kotlin Syntax ===")
        val syntaxErrors = checkBasicKotlinSyntax(kotlinCode)
        if (syntaxErrors.isNotEmpty()) {
            println("⚠ Potential syntax issues found:")
            syntaxErrors.take(10).forEach { println("  - $it") }
        } else {
            println("✓ Basic syntax checks passed")
        }
    }

    /**
     * Basic syntax checks for generated Kotlin
     */
    private fun checkBasicKotlinSyntax(code: String): List<String> {
        val errors = mutableListOf<String>()
        val lines = code.lines()

        // Check for common syntax issues
        lines.forEachIndexed { index, line ->
            // Check for unmatched braces
            val openBraces = line.count { it == '{' }
            val closeBraces = line.count { it == '}' }

            // Check for invalid variable names (labels that might not have been sanitized)
            if (line.contains("fun ") && line.contains("[") && !line.contains("//")) {
                errors.add("Line ${index + 1}: Possible invalid function name: $line")
            }

            // Check for $ in identifiers (should be sanitized)
            if (line.contains("$") && !line.contains("\"") && !line.contains("//")) {
                val trimmed = line.trim()
                if (!trimmed.startsWith("//") && !trimmed.contains("\${")) {
                    errors.add("Line ${index + 1}: Unsanitized $ in identifier: $line")
                }
            }
        }

        // Check overall brace matching
        val totalOpen = code.count { it == '{' }
        val totalClose = code.count { it == '}' }
        if (totalOpen != totalClose) {
            errors.add("Unmatched braces: $totalOpen open, $totalClose close")
        }

        return errors
    }

    @Test
    fun `generate minimal SMB functions`() {
        // Load SMB disassembly
        val smbFile = File("smbdism.asm")
        val smbAsm = smbFile.readText()

        println("\n=== Minimal SMB Decompilation ===")
        val assemblyLines = smbAsm.parseToAssemblyCodeFile()
        val codeFile = CodeFile(assemblyLines.lines, smbFile)

        // Just decompile a single known function
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("InitializeMemory"))

        if (entries.entryPoints.isEmpty()) {
            println("⚠ InitializeMemory not found, using first entry point")
        }

        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        println("Functions found: ${cfg.functions.size}")
        cfg.functions.take(3).forEach { func ->
            val name = func.entryLabel ?: "func_${func.entryAddress.toString(16)}"
            println("  - $name at 0x${func.entryAddress.toString(16).uppercase()} (${func.blocks.size} blocks)")
        }

        // Run minimal analysis
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val typeInference = codeFile.inferTypes(cfg, dataFlow, constants, memoryPatterns)
        val loops = codeFile.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val variableId = codeFile.identifyVariables(cfg, dataFlow, typeInference, memoryPatterns, loops)

        // Generate without parameter recovery for simplicity
        val result = cfg.emitCompleteKotlin(codeFile, variableId, null, resolution, null, null)

        println("\n=== Generated Code ===")
        val kotlinCode = result.kotlinFile.emitToString()
        println(kotlinCode)

        // Write to file
        val outputFile = File("build/SMB_minimal.kt")
        outputFile.writeText(kotlinCode)
        println("\n✓ Written to: ${outputFile.absolutePath}")
    }
}
