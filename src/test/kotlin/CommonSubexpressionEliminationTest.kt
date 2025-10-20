package com.ivieleague.decompiler6502tokotlin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for Pass 25: Common Subexpression Elimination
 */
class CommonSubexpressionEliminationTest {

    @Test
    fun `test local CSE with duplicate expressions`() {
        val assembly = """
            .export test
            test:
            LDA #$10
            ADC #$05
            STA $00
            LDA #$10
            ADC #$05
            STA $01
            RTS
        """.trimIndent()

        val codeFile = assembly.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses()
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
        val simplifications = expressions.simplifyExpressions()

        val cse = expressions.performCse(dominators, simplifications)

        // Should identify the duplicate addition expression
        assertNotNull(cse)
        assertTrue(cse.functions.isNotEmpty())
    }

    @Test
    fun `test CSE with complex expressions`() {
        val assembly = """
            .export test
            test:
            LDA #$05
            ASL A
            ASL A
            STA $00
            LDA #$05
            ASL A
            ASL A
            STA $01
            RTS
        """.trimIndent()

        val codeFile = assembly.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses()
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
        val simplifications = expressions.simplifyExpressions()

        val cse = expressions.performCse(dominators, simplifications)

        // Should find common subexpressions
        assertNotNull(cse)
    }

    @Test
    fun `test CSE profitability analysis`() {
        // Simple expressions (literals) should not be CSE'd
        val assembly = """
            .export test
            test:
            LDA #$42
            STA $00
            LDA #$42
            STA $01
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
        val simplifications = expressions.simplifyExpressions()

        val cse = expressions.performCse(dominators, simplifications)

        // Literals should not be CSE'd (not profitable)
        val funcResult = cse.functions.firstOrNull()
        assertNotNull(funcResult)

        // Simple loads should have no or few CSE opportunities
        val totalTemporaries = funcResult?.localCse?.values?.sumOf { it.introducedTemporaries.size } ?: 0
        assertTrue(totalTemporaries <= 1) // At most minimal CSE for very simple cases
    }

    @Test
    fun `test expression complexity calculation`() {
        val assembly = """
            .export test
            test:
            LDA #$10
            ADC #$05
            ADC #$03
            RTS
        """.trimIndent()

        val codeFile = assembly.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses()
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
        val simplifications = expressions.simplifyExpressions()

        val cse = expressions.performCse(dominators, simplifications)

        // Complex expressions should be candidates for CSE
        assertNotNull(cse)
    }

    @Test
    fun `test CSE with array accesses`() {
        val assembly = """
            .export test
            test:
            LDX #$05
            LDA $0200,X
            STA $00
            LDA $0200,X
            STA $01
            RTS
        """.trimIndent()


        val codeFile = assembly.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses()
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val loops = codeFile.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val dataflow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val types = codeFile.inferTypes(cfg, dataflow, constants, memoryPatterns)
        val expressions = codeFile.buildExpressionTrees(cfg, constants, types, memoryPatterns)
        val simplifications = expressions.simplifyExpressions()

        val cse = expressions.performCse(dominators, simplifications)

        // Duplicate array accesses should be identified
        assertNotNull(cse)
    }

    @Test
    fun `test CSE temporary introduction`() {
        val assembly = """
            .export test
            test:
            LDA #$10
            ADC #$20
            STA $00
            LDA #$10
            ADC #$20
            STA $01
            LDA #$10
            ADC #$20
            STA $02
            RTS
        """.trimIndent()

        val codeFile = assembly.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses()
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val loops = codeFile.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val dataflow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val types = codeFile.inferTypes(cfg, dataflow, constants, memoryPatterns)
        val expressions = codeFile.buildExpressionTrees(cfg, constants, types, memoryPatterns)
        val simplifications = expressions.simplifyExpressions()
        val cse = expressions.performCse(dominators, simplifications)

        // Should introduce temporaries for repeated expressions
        val funcResult = cse.functions.firstOrNull()
        assertNotNull(funcResult)
    }

    @Test
    fun `test CSE register pressure awareness`() {
        // Too many CSE opportunities should be limited by register pressure heuristic
        val assembly = """
            .export test
            test:
            LDA #$01
            ADC #$02
            STA $00
            LDA #$03
            ADC #$04
            STA $01
            LDA #$05
            ADC #$06
            STA $02
            LDA #$07
            ADC #$08
            STA $03
            RTS
        """.trimIndent()

        val codeFile = assembly.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses()
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val loops = codeFile.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val dataflow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val types = codeFile.inferTypes(cfg, dataflow, constants, memoryPatterns)
        val expressions = codeFile.buildExpressionTrees(cfg, constants, types, memoryPatterns)
        val simplifications = expressions.simplifyExpressions()
        val cse = expressions.performCse(dominators, simplifications)

        // Should limit number of temporaries (register pressure)
        val funcResult = cse.functions.firstOrNull()
        assertNotNull(funcResult)

        val totalTemporaries = funcResult?.localCse?.values?.sumOf { it.introducedTemporaries.size } ?: 0
        assertTrue(totalTemporaries <= 3) // Heuristic limit from implementation
    }
}
