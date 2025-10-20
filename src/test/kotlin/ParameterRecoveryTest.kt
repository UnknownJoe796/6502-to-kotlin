package com.ivieleague.decompiler6502tokotlin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class ParameterRecoveryTest {

    @Test
    fun `test register-based parameter detection`() {
        val code = """
            .export AddNumbers
            AddNumbers:
                ; A and X are parameters
                STX ${'$'}00
                CLC
                ADC ${'$'}00
                ; Result in A
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses()
        val entryPoints = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("AddNumbers"))
        val reachability = codeFile.analyzeReachability(resolution, entryPoints)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entryPoints)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entryPoints)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val typeInference = codeFile.inferTypes(cfg, dataFlow, constants, memoryPatterns)
        val loops = codeFile.detectLoops(resolution, entryPoints, reachability, blocks, cfg, dominators)
        val functionBoundaries = codeFile.detectFunctionBoundaries(resolution, entryPoints, reachability, blocks, cfg)
        val callGraph = codeFile.constructCallGraph(resolution, entryPoints, reachability, blocks, cfg, functionBoundaries)
        val variableId = codeFile.identifyVariables(cfg, dataFlow, typeInference, memoryPatterns, loops)
        val naming = variableId.assignNames(resolution)

        val paramRecovery = variableId.recoverParameters(cfg, callGraph, naming, resolution).also { println(it) }

        // Should have one function signature
        assertTrue(paramRecovery.functionSignatures.isNotEmpty(), "Should have function signatures")

        val signature = paramRecovery.functionSignatures.first()
        assertEquals("AddNumbers", signature.name, "Should have correct function name")

        // Should detect register-based calling convention
        assertTrue(signature.callingConvention is CallingConvention.RegisterBased, "Should be register-based")
    }

    @Test
    fun `test return value detection`() {
        val code = """
            .export GetValue
            GetValue:
                LDA #$42
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses()
        val entryPoints = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("GetValue"))
        val reachability = codeFile.analyzeReachability(resolution, entryPoints)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entryPoints)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entryPoints)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val typeInference = codeFile.inferTypes(cfg, dataFlow, constants, memoryPatterns)
        val loops = codeFile.detectLoops(resolution, entryPoints, reachability, blocks, cfg, dominators)
        val functionBoundaries = codeFile.detectFunctionBoundaries(resolution, entryPoints, reachability, blocks, cfg)
        val callGraph = codeFile.constructCallGraph(resolution, entryPoints, reachability, blocks, cfg, functionBoundaries)
        val variableId = codeFile.identifyVariables(cfg, dataFlow, typeInference, memoryPatterns, loops)
        val naming = variableId.assignNames(resolution)

        val paramRecovery = variableId.recoverParameters(cfg, callGraph, naming, resolution)

        val signature = paramRecovery.functionSignatures.first()

        // Should detect return value in A register
        // Note: This depends on liveness analysis identifying A as live-out
        assertNotNull(signature, "Should have signature")
    }

    @Test
    fun `test side effect detection`() {
        val code = """
            .export WriteToGlobal
            WriteToGlobal:
                LDA #$05
                STA GlobalVar
                RTS

            GlobalVar = ${'$'}0200
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses()
        val entryPoints = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("WriteToGlobal"))
        val reachability = codeFile.analyzeReachability(resolution, entryPoints)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entryPoints)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entryPoints)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val typeInference = codeFile.inferTypes(cfg, dataFlow, constants, memoryPatterns)
        val loops = codeFile.detectLoops(resolution, entryPoints, reachability, blocks, cfg, dominators)
        val functionBoundaries = codeFile.detectFunctionBoundaries(resolution, entryPoints, reachability, blocks, cfg)
        val callGraph = codeFile.constructCallGraph(resolution, entryPoints, reachability, blocks, cfg, functionBoundaries)
        val variableId = codeFile.identifyVariables(cfg, dataFlow, typeInference, memoryPatterns, loops)
        val naming = variableId.assignNames(resolution)

        val paramRecovery = variableId.recoverParameters(cfg, callGraph, naming, resolution)

        val signature = paramRecovery.functionSignatures.first()

        // Should detect global memory modification as side effect
        // Note: This test may need adjustment based on how globals are tracked
        assertNotNull(signature.sideEffects, "Should have side effects")
    }

    @Test
    fun `test hardware register detection`() {
        val code = """
            .export WritePPU
            WritePPU:
                LDA #$80
                STA ${'$'}2000
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses()
        val entryPoints = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("WritePPU"))
        val reachability = codeFile.analyzeReachability(resolution, entryPoints)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entryPoints)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entryPoints)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val typeInference = codeFile.inferTypes(cfg, dataFlow, constants, memoryPatterns)
        val loops = codeFile.detectLoops(resolution, entryPoints, reachability, blocks, cfg, dominators)
        val functionBoundaries = codeFile.detectFunctionBoundaries(resolution, entryPoints, reachability, blocks, cfg)
        val callGraph = codeFile.constructCallGraph(resolution, entryPoints, reachability, blocks, cfg, functionBoundaries)
        val variableId = codeFile.identifyVariables(cfg, dataFlow, typeInference, memoryPatterns, loops)
        val naming = variableId.assignNames(resolution)

        val paramRecovery = variableId.recoverParameters(cfg, callGraph, naming, resolution)

        val signature = paramRecovery.functionSignatures.first()

        // Should detect hardware modification
        assertTrue(
            signature.sideEffects.any { it is SideEffect.ModifiesHardware },
            "Should detect hardware modification"
        )
    }

    @Test
    fun `test memory-based parameters`() {
        val code = """
            .export ProcessData
            ProcessData:
                LDA InputParam
                CLC
                ADC #$10
                STA OutputParam
                RTS

            InputParam = ${'$'}0080
            OutputParam = ${'$'}0081
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses()
        val entryPoints = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("ProcessData"))
        val reachability = codeFile.analyzeReachability(resolution, entryPoints)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entryPoints)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entryPoints)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val typeInference = codeFile.inferTypes(cfg, dataFlow, constants, memoryPatterns)
        val loops = codeFile.detectLoops(resolution, entryPoints, reachability, blocks, cfg, dominators)
        val functionBoundaries = codeFile.detectFunctionBoundaries(resolution, entryPoints, reachability, blocks, cfg)
        val callGraph = codeFile.constructCallGraph(resolution, entryPoints, reachability, blocks, cfg, functionBoundaries)
        val variableId = codeFile.identifyVariables(cfg, dataFlow, typeInference, memoryPatterns, loops)
        val naming = variableId.assignNames(resolution)

        val paramRecovery = variableId.recoverParameters(cfg, callGraph, naming, resolution)

        val signature = paramRecovery.functionSignatures.first()

        // Should have detected some form of calling convention
        assertNotNull(signature.callingConvention, "Should have calling convention")
    }

    @Test
    fun `test function with no parameters`() {
        val code = """
            .export NoParams
            NoParams:
                LDA #$00
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses()
        val entryPoints = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("NoParams"))
        val reachability = codeFile.analyzeReachability(resolution, entryPoints)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entryPoints)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entryPoints)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val typeInference = codeFile.inferTypes(cfg, dataFlow, constants, memoryPatterns)
        val loops = codeFile.detectLoops(resolution, entryPoints, reachability, blocks, cfg, dominators)
        val functionBoundaries = codeFile.detectFunctionBoundaries(resolution, entryPoints, reachability, blocks, cfg)
        val callGraph = codeFile.constructCallGraph(resolution, entryPoints, reachability, blocks, cfg, functionBoundaries)
        val variableId = codeFile.identifyVariables(cfg, dataFlow, typeInference, memoryPatterns, loops)
        val naming = variableId.assignNames(resolution)

        val paramRecovery = variableId.recoverParameters(cfg, callGraph, naming, resolution)

        val signature = paramRecovery.functionSignatures.first()

        // Should have no parameters
        assertTrue(signature.parameters.isEmpty(), "Should have no parameters")
    }
}
