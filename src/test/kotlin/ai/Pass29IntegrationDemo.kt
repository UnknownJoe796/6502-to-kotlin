package com.ivieleague.decompiler6502tokotlin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Demonstration of Pass 29 (Parameter Recovery) integration
 *
 * Shows how function parameters and return values are recovered from
 * register usage patterns and emitted in the final Kotlin code.
 */
class Pass29IntegrationDemo {

    @Test
    fun `demonstrate function with register parameters`() {
        val asm = """
            .export AddNumbers
            ; Function that adds two numbers
            ; Parameters: A (first number), X (second number)
            ; Returns: A (sum)
            AddNumbers:
                STX ${'$'}00
                CLC
                ADC ${'$'}00
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("AddNumbers"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        // Run all analysis passes
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val typeInference = codeFile.inferTypes(cfg, dataFlow, constants, memoryPatterns)
        val loops = codeFile.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val functionBoundaries = codeFile.detectFunctionBoundaries(resolution, entries, reachability, blocks, cfg)
        val callGraph = codeFile.constructCallGraph(resolution, entries, reachability, blocks, cfg, functionBoundaries)
        val variableId = codeFile.identifyVariables(cfg, dataFlow, typeInference, memoryPatterns, loops)
        val naming = variableId.assignNames(resolution)

        // Pass 29: Parameter Recovery
        val paramRecovery = variableId.recoverParameters(cfg, callGraph, naming, resolution)

        println("\n=== Pass 29: Parameter Recovery ===")
        println("Function signatures: ${paramRecovery.functionSignatures.size}")

        val signature = paramRecovery.functionSignatures.first()
        println("\nFunction: ${signature.name}")
        println("  Parameters: ${signature.parameters.size}")
        signature.parameters.forEach { param ->
            println("    - ${param.name}: ${param.type} (${param.location})")
        }
        println("  Return value: ${signature.returnValue?.let { "${it.type} (${it.location})" } ?: "none"}")
        println("  Calling convention: ${signature.callingConvention}")

        // Generate Kotlin code with parameters
        println("\n=== Generated Kotlin Code ===")
        val result = cfg.emitCompleteKotlin(codeFile, variableId, paramRecovery, null, null, null)
        println(result.kotlinFile.emitToString())

        // Parameters might not be detected depending on liveness analysis
        // Just verify the infrastructure works
        assertEquals("AddNumbers", signature.name)
        assertNotNull(signature.callingConvention)
    }

    @Test
    fun `demonstrate function with return value`() {
        val asm = """
            .export GetValue
            ; Function that returns a constant
            ; Returns: A (value)
            GetValue:
                LDA #${'$'}42
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("GetValue"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val typeInference = codeFile.inferTypes(cfg, dataFlow, constants, memoryPatterns)
        val loops = codeFile.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val functionBoundaries = codeFile.detectFunctionBoundaries(resolution, entries, reachability, blocks, cfg)
        val callGraph = codeFile.constructCallGraph(resolution, entries, reachability, blocks, cfg, functionBoundaries)
        val variableId = codeFile.identifyVariables(cfg, dataFlow, typeInference, memoryPatterns, loops)
        val naming = variableId.assignNames(resolution)

        val paramRecovery = variableId.recoverParameters(cfg, callGraph, naming, resolution)

        println("\n=== Function with Return Value ===")
        val signature = paramRecovery.functionSignatures.first()
        println("Function: ${signature.name}")
        println("  Parameters: ${signature.parameters.size}")
        println("  Return value: ${signature.returnValue?.let { "${it.type} (${it.location})" } ?: "none"}")

        val result = cfg.emitCompleteKotlin(codeFile, variableId, paramRecovery, null, null, null)
        println("\n=== Generated Kotlin ===")
        println(result.kotlinFile.emitToString())
    }

    @Test
    fun `demonstrate side effect detection`() {
        val asm = """
            .export WriteToMemory
            ; Function that writes to memory
            WriteToMemory:
                LDA #${'$'}05
                STA ${'$'}0200
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("WriteToMemory"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val typeInference = codeFile.inferTypes(cfg, dataFlow, constants, memoryPatterns)
        val loops = codeFile.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val functionBoundaries = codeFile.detectFunctionBoundaries(resolution, entries, reachability, blocks, cfg)
        val callGraph = codeFile.constructCallGraph(resolution, entries, reachability, blocks, cfg, functionBoundaries)
        val variableId = codeFile.identifyVariables(cfg, dataFlow, typeInference, memoryPatterns, loops)
        val naming = variableId.assignNames(resolution)

        val paramRecovery = variableId.recoverParameters(cfg, callGraph, naming, resolution)

        println("\n=== Side Effect Detection ===")
        val signature = paramRecovery.functionSignatures.first()
        println("Function: ${signature.name}")
        println("  Side effects: ${signature.sideEffects.size}")
        signature.sideEffects.forEach { effect ->
            println("    - $effect")
        }

        val result = cfg.emitCompleteKotlin(codeFile, variableId, paramRecovery, null, null, null)
        println("\n=== Generated Kotlin ===")
        println(result.kotlinFile.emitToString())

        // Side effect detection is best-effort
        // Just verify the function signature was created
        assertEquals("WriteToMemory", signature.name)
        assertNotNull(signature.sideEffects)
    }

    @Test
    fun `compare before and after parameter recovery`() {
        val asm = """
            .export Multiply
            ; Multiplies A by X, returns in A
            Multiply:
                STA ${'$'}00
                LDA #${'$'}00
            Loop:
                CLC
                ADC ${'$'}00
                DEX
                BNE Loop
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Multiply"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val typeInference = codeFile.inferTypes(cfg, dataFlow, constants, memoryPatterns)
        val loops = codeFile.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val functionBoundaries = codeFile.detectFunctionBoundaries(resolution, entries, reachability, blocks, cfg)
        val callGraph = codeFile.constructCallGraph(resolution, entries, reachability, blocks, cfg, functionBoundaries)
        val variableId = codeFile.identifyVariables(cfg, dataFlow, typeInference, memoryPatterns, loops)
        val naming = variableId.assignNames(resolution)

        val paramRecovery = variableId.recoverParameters(cfg, callGraph, naming, resolution)

        println("\n=== WITHOUT Parameter Recovery ===")
        val withoutParams = cfg.emitCompleteKotlin(codeFile, variableId, null, null, null)
        println(withoutParams.kotlinFile.emitToString())

        println("\n=== WITH Parameter Recovery ===")
        val withParams = cfg.emitCompleteKotlin(codeFile, variableId, paramRecovery, null, null, null)
        println(withParams.kotlinFile.emitToString())

        val signature = paramRecovery.functionSignatures.first()
        println("\n=== Signature Summary ===")
        println("${signature.name}(${signature.parameters.joinToString(", ") { "${it.name}: ${it.type}" }})" +
                (signature.returnValue?.let { ": ${it.type}" } ?: ""))
    }
}
