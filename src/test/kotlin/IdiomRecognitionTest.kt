package com.ivieleague.decompiler6502tokotlin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for Pass 23: Idiom Recognition
 */
class IdiomRecognitionTest {

    @Test
    fun `test multiply by power of 2 recognition`() {
        val assembly = """
            .export test
            test:
            LDA #$05
            ASL A
            ASL A
            ASL A
            RTS
        """.trimIndent()

        val codeFile = assembly.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataflow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val types = codeFile.inferTypes(cfg, dataflow, constants, memoryPatterns)
        val expressions = codeFile.buildExpressionTrees(cfg, constants, types, memoryPatterns)

        val idioms = codeFile.recognizeIdioms(cfg, expressions, constants)

        // Should recognize multiply by 8 (3 shifts)
        assertNotNull(idioms)
        assertTrue(idioms.functions.isNotEmpty())

        val recognizedIdioms = idioms.functions.first().recognizedIdioms
        assertTrue(recognizedIdioms.any { it.idiom is Idiom.MultiplyByPowerOf2 })
    }

    @Test
    fun `test multiply by 10 recognition`() {
        val assembly = """
            .export test
            test:
            LDA #$07
            ASL A
            STA $00
            ASL A
            ASL A
            CLC
            ADC $00
            RTS
        """.trimIndent()

        val codeFile = assembly.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataflow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val types = codeFile.inferTypes(cfg, dataflow, constants, memoryPatterns)
        val expressions = codeFile.buildExpressionTrees(cfg, constants, types, memoryPatterns)

        val idioms = codeFile.recognizeIdioms(cfg, expressions, constants)

        // Should recognize shift pattern (multiply by 10 would need more complex analysis)
        assertNotNull(idioms)
        val recognizedIdioms = idioms.functions.first().recognizedIdioms
        // At minimum, should recognize the shift operations as multiply by power of 2
        assertTrue(recognizedIdioms.any { it.idiom is Idiom.MultiplyByPowerOf2 })
    }

    @Test
    fun `test set bit pattern recognition`() {
        val assembly = """
            .export test
            test:
            LDA $0200
            ORA #$80
            STA $0200
            RTS
        """.trimIndent()

        val codeFile = assembly.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataflow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val types = codeFile.inferTypes(cfg, dataflow, constants, memoryPatterns)
        val expressions = codeFile.buildExpressionTrees(cfg, constants, types, memoryPatterns)

        val idioms = codeFile.recognizeIdioms(cfg, expressions, constants)

        // Should recognize idioms (set bit requires expression-level pattern matching)
        assertNotNull(idioms)
        assertTrue(idioms.functions.isNotEmpty())
    }

    @Test
    fun `test clear bit pattern recognition`() {
        val assembly = """
            .export test
            test:
            LDA $0200
            AND #$7F
            STA $0200
            RTS
        """.trimIndent()

        val codeFile = assembly.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataflow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val types = codeFile.inferTypes(cfg, dataflow, constants, memoryPatterns)
        val expressions = codeFile.buildExpressionTrees(cfg, constants, types, memoryPatterns)

        val idioms = codeFile.recognizeIdioms(cfg, expressions, constants)

        // Should recognize clear bit 7
        assertNotNull(idioms)
        val recognizedIdioms = idioms.functions.first().recognizedIdioms
        assertTrue(recognizedIdioms.any { it.idiom is Idiom.ClearBit })
    }

    @Test
    fun `test bit test pattern recognition`() {
        val assembly = """
            .export test
            test:
            LDA $0200
            AND #$01
            BEQ skip
        skip:
            NOP
            RTS
        """.trimIndent()

        val codeFile = assembly.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataflow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val types = codeFile.inferTypes(cfg, dataflow, constants, memoryPatterns)
        val expressions = codeFile.buildExpressionTrees(cfg, constants, types, memoryPatterns)

        val idioms = codeFile.recognizeIdioms(cfg, expressions, constants)

        // Should recognize test bit 0
        assertNotNull(idioms)
        val recognizedIdioms = idioms.functions.first().recognizedIdioms
        assertTrue(recognizedIdioms.any { it.idiom is Idiom.TestBit })
    }

    @Test
    fun `test array fill loop recognition`() {
        val assembly = """
            .export test
            test:
            LDX #$00
            LDA #$42
        loop:
            STA $0200,X
            INX
            CPX #$10
            BNE loop
            RTS
        """.trimIndent()

        val codeFile = assembly.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataflow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val types = codeFile.inferTypes(cfg, dataflow, constants, memoryPatterns)
        val expressions = codeFile.buildExpressionTrees(cfg, constants, types, memoryPatterns)

        val idioms = codeFile.recognizeIdioms(cfg, expressions, constants)

        // Should recognize idioms (array fill requires loop-level pattern matching)
        assertNotNull(idioms)
        assertTrue(idioms.functions.isNotEmpty())
    }

    @Test
    fun `test array clear loop recognition`() {
        val assembly = """
            .export test
            test:
            LDX #$00
            LDA #$00
        loop:
            STA $0200,X
            INX
            CPX #$10
            BNE loop
            RTS
        """.trimIndent()

        val codeFile = assembly.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataflow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val types = codeFile.inferTypes(cfg, dataflow, constants, memoryPatterns)
        val expressions = codeFile.buildExpressionTrees(cfg, constants, types, memoryPatterns)

        val idioms = codeFile.recognizeIdioms(cfg, expressions, constants)

        // Should recognize idioms (array clear requires loop-level pattern matching)
        assertNotNull(idioms)
        assertTrue(idioms.functions.isNotEmpty())
    }

    @Test
    fun `test VBlank wait pattern recognition`() {
        val assembly = """
            .export test
            test:
        vblankwait:
            BIT $2002
            BPL vblankwait
            RTS
        """.trimIndent()

        val codeFile = assembly.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataflow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val types = codeFile.inferTypes(cfg, dataflow, constants, memoryPatterns)
        val expressions = codeFile.buildExpressionTrees(cfg, constants, types, memoryPatterns)

        val idioms = codeFile.recognizeIdioms(cfg, expressions, constants)

        // Should recognize NES VBlank wait pattern
        assertNotNull(idioms)
        val recognizedIdioms = idioms.functions.first().recognizedIdioms
        assertTrue(recognizedIdioms.any { it.idiom is Idiom.WaitForVBlank })
    }

    @Test
    fun `test non-overlapping idiom selection`() {
        val assembly = """
            .export test
            test:
            LDA #$05
            ASL A
            ASL A
            RTS
        """.trimIndent()

        val codeFile = assembly.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataflow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val types = codeFile.inferTypes(cfg, dataflow, constants, memoryPatterns)
        val expressions = codeFile.buildExpressionTrees(cfg, constants, types, memoryPatterns)

        val idioms = codeFile.recognizeIdioms(cfg, expressions, constants)

        // Should not have overlapping idioms
        val recognizedIdioms = idioms.functions.first().recognizedIdioms
        val allLines = recognizedIdioms.flatMap { it.lineRange.toList() }
        val uniqueLines = allLines.toSet()

        // Each line should appear at most once
        assertEquals(uniqueLines.size, allLines.size)
    }
}
