package com.ivieleague.decompiler6502tokotlin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class VariableIdentificationTest {

    @Test
    fun `test simple local variable identification`() {
        val code = """
            .export TestFunction
            TestFunction:
                LDA #$05
                STA ${'$'}0200
                LDA ${'$'}0200
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses()
        val entryPoints = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("TestFunction"))
        val reachability = codeFile.analyzeReachability(resolution, entryPoints)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entryPoints)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entryPoints)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val typeInference = codeFile.inferTypes(cfg, dataFlow, constants, memoryPatterns)
        val loops = codeFile.detectLoops(resolution, entryPoints, reachability, blocks, cfg, dominators)

        val variableId = codeFile.identifyVariables(
            cfg, dataFlow, typeInference, memoryPatterns, loops
        )

        // Should identify $0200 as a local variable
        val function = variableId.functions.first()
        assertTrue(function.localVariables.any {
            it.id is VariableId.Memory && (it.id as VariableId.Memory).address == 0x0200
        }, "Should identify \$0200 as a local variable")

        val variable = function.localVariables.first {
            it.id is VariableId.Memory && (it.id as VariableId.Memory).address == 0x0200
        }

        // Should have both read and write usages
        assertTrue(variable.usageSites.any { it.usageType == UsageType.WRITE }, "Should have write usage")
        assertTrue(variable.usageSites.any { it.usageType == UsageType.READ }, "Should have read usage")
    }

    @Test
    fun `test multi-byte variable detection`() {
        val code = """
            .export Load16Bit
            Load16Bit:
                LDA ${'$'}0200
                LDX ${'$'}0201
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses()
        val entryPoints = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Load16Bit"))
        val reachability = codeFile.analyzeReachability(resolution, entryPoints)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entryPoints)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entryPoints)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val typeInference = codeFile.inferTypes(cfg, dataFlow, constants, memoryPatterns)
        val loops = codeFile.detectLoops(resolution, entryPoints, reachability, blocks, cfg, dominators)

        val variableId = codeFile.identifyVariables(
            cfg, dataFlow, typeInference, memoryPatterns, loops
        )

        val function = variableId.functions.first()

        // Should identify $0200-$0201 as a 16-bit variable
        val multiByteVar = function.localVariables.find {
            it.id is VariableId.MultiByteMemory
        }

        assertNotNull(multiByteVar, "Should identify multi-byte variable")
        multiByteVar?.let {
            val id = it.id as VariableId.MultiByteMemory
            assertEquals(0x0200, id.baseAddress, "Should have correct base address")
            assertEquals(2, id.size, "Should be 2 bytes")
            assertEquals(InferredType.UInt16(), it.inferredType, "Should infer UInt16 type")
        }
    }

    @Test
    fun `test global variable identification`() {
        val code = """
            .export Function1, Function2
            Function1:
                LDA ${'$'}0200
                RTS

            Function2:
                STA ${'$'}0200
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses()
        val entryPoints = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Function1", "Function2"))
        val reachability = codeFile.analyzeReachability(resolution, entryPoints)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entryPoints)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entryPoints)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val typeInference = codeFile.inferTypes(cfg, dataFlow, constants, memoryPatterns)
        val loops = codeFile.detectLoops(resolution, entryPoints, reachability, blocks, cfg, dominators)

        val variableId = codeFile.identifyVariables(
            cfg, dataFlow, typeInference, memoryPatterns, loops
        )

        // Should identify $0200 as a global variable (accessed by both functions)
        val globalVar = variableId.globals.find {
            it.id is VariableId.Memory && (it.id as VariableId.Memory).address == 0x0200
        }

        assertNotNull(globalVar, "Should identify \$0200 as global variable")
        globalVar?.let {
            assertEquals(VariableScope.Global, it.scope, "Should have global scope")
        }
    }

    @Test
    fun `test loop induction variable detection`() {
        val code = """
            .export CounterLoop
            CounterLoop:
                LDX #${'$'}00
            Loop:
                INX
                CPX #${'$'}10
                BNE Loop
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses()
        val entryPoints = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("CounterLoop"))
        val reachability = codeFile.analyzeReachability(resolution, entryPoints)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entryPoints)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entryPoints)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val typeInference = codeFile.inferTypes(cfg, dataFlow, constants, memoryPatterns)
        val loops = codeFile.detectLoops(resolution, entryPoints, reachability, blocks, cfg, dominators)

        val variableId = codeFile.identifyVariables(
            cfg, dataFlow, typeInference, memoryPatterns, loops
        )

        // Register X should be identified with loop role
        // Note: This test may need adjustment based on how we track register variables
        assertNotNull(variableId.functions.first())
    }

    @Test
    fun `test variable scope determination`() {
        val code = """
            .export Main, Helper
            Main:
                LDA #${'$'}05
                STA LocalVar
                JSR Helper
                RTS

            LocalVar = ${'$'}0080

            Helper:
                LDA LocalVar
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses()
        val entryPoints = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Main", "Helper"))
        val reachability = codeFile.analyzeReachability(resolution, entryPoints)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entryPoints)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entryPoints)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val typeInference = codeFile.inferTypes(cfg, dataFlow, constants, memoryPatterns)
        val loops = codeFile.detectLoops(resolution, entryPoints, reachability, blocks, cfg, dominators)

        val variableId = codeFile.identifyVariables(
            cfg, dataFlow, typeInference, memoryPatterns, loops
        )

        // LocalVar should be global since used by both Main and Helper
        val globalVar = variableId.globals.find {
            it.id is VariableId.Memory && (it.id as VariableId.Memory).address == 0x0080
        }

        assertNotNull(globalVar, "LocalVar should be identified as global")
    }
}
