package com.ivieleague.decompiler6502tokotlin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class VariableNamingTest {

    @Test
    fun `test naming from original labels`() {
        val code = """
            .export TestFunction
            TestFunction:
                LDA #$05
                STA Player_X_Position
                RTS

            Player_X_Position = ${'$'}0200
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
        val variableId = codeFile.identifyVariables(cfg, dataFlow, typeInference, memoryPatterns, loops)

        val naming = variableId.assignNames(resolution)

        // Should use original label name, converted to camelCase
        val namedVar = naming.namedVariables.values.find {
            it.variable.id is VariableId.Memory && (it.variable.id as VariableId.Memory).address == 0x0200
        }

        assertNotNull(namedVar, "Should find named variable")
        namedVar?.let {
            assertEquals("playerXPosition", it.name, "Should convert to camelCase")
            assertEquals(NameSource.OriginalLabel, it.nameSource, "Should use original label")
            assertEquals(1.0, it.confidence, "Should have high confidence")
        }
    }

    @Test
    fun `test naming from type inference`() {
        val code = """
            .export TestFunction
            TestFunction:
                LDA #$00
                STA ${'$'}0080
            Loop:
                INC ${'$'}0080
                LDA ${'$'}0080
                CMP #$10
                BNE Loop
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
        val variableId = codeFile.identifyVariables(cfg, dataFlow, typeInference, memoryPatterns, loops)

        val naming = variableId.assignNames(resolution)

        // Should infer name from type/usage (counter or index)
        val namedVar = naming.namedVariables.values.find {
            it.variable.id is VariableId.Memory && (it.variable.id as VariableId.Memory).address == 0x0080
        }

        assertNotNull(namedVar, "Should find named variable")
        namedVar?.let {
            assertTrue(
                it.name.contains("counter") || it.name.contains("index"),
                "Should contain 'counter' or 'index'"
            )
        }
    }

    @Test
    fun `test generic naming fallback`() {
        val code = """
            .export TestFunction
            TestFunction:
                LDA #$05
                STA ${'$'}0200
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
        val variableId = codeFile.identifyVariables(cfg, dataFlow, typeInference, memoryPatterns, loops)

        val naming = variableId.assignNames(resolution)

        // Should use generic name based on address
        val namedVar = naming.namedVariables.values.find {
            it.variable.id is VariableId.Memory && (it.variable.id as VariableId.Memory).address == 0x0200
        }

        assertNotNull(namedVar, "Should find named variable")
        namedVar?.let {
            assertTrue(it.name.contains("0200"), "Should contain address in hex")
        }
    }

    @Test
    fun `test naming conflict resolution`() {
        val code = """
            .export Function1, Function2
            Function1:
                LDA #$05
                STA ${'$'}0080
                RTS

            Function2:
                LDA #$10
                STA ${'$'}0081
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
        val variableId = codeFile.identifyVariables(cfg, dataFlow, typeInference, memoryPatterns, loops)

        val naming = variableId.assignNames(resolution)

        // Should have unique names for both variables
        val names = naming.namedVariables.values.map { it.name }
        assertEquals(names.size, names.distinct().size, "All names should be unique")
    }

    @Test
    fun `test keyword avoidance`() {
        val code = """
            .export TestFunction
            TestFunction:
                LDA #$05
                STA class
                RTS

            class = ${'$'}0080
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
        val variableId = codeFile.identifyVariables(cfg, dataFlow, typeInference, memoryPatterns, loops)

        val naming = variableId.assignNames(resolution)

        // Should avoid Kotlin keyword "class"
        val namedVar = naming.namedVariables.values.find {
            it.variable.id is VariableId.Memory && (it.variable.id as VariableId.Memory).address == 0x0080
        }

        assertNotNull(namedVar, "Should find named variable")
        namedVar?.let {
            assertNotEquals("class", it.name, "Should avoid keyword 'class'")
            assertEquals("_class", it.name, "Should prefix with underscore")
        }
    }

    @Test
    fun `test multi-byte variable naming`() {
        val code = """
            .export TestFunction
            TestFunction:
                LDA ${'$'}0200
                LDX ${'$'}0201
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
        val variableId = codeFile.identifyVariables(cfg, dataFlow, typeInference, memoryPatterns, loops)

        val naming = variableId.assignNames(resolution)

        // Should name multi-byte variable
        val namedVar = naming.namedVariables.values.find {
            it.variable.id is VariableId.MultiByteMemory
        }

        assertNotNull(namedVar, "Should find multi-byte variable")
        namedVar?.let {
            assertTrue(it.name.contains("16") || it.name.contains("0200"), "Should indicate 16-bit or address")
        }
    }
}
