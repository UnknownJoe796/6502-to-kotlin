package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

class GotoEliminationTest {

    @Test
    fun simpleSequence() {
        val assembly = """
            main:
                LDA #${'$'}42
                STA ${'$'}2000
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val loops = lines.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val conditionals = lines.detectConditionals(resolution, entries, reachability, blocks, cfg, dominators, loops)
        val regions = lines.formRegions(resolution, entries, reachability, blocks, cfg, dominators, loops, conditionals)
        val gotoElim = lines.eliminateGotos(resolution, entries, reachability, blocks, cfg, dominators, loops, conditionals, regions)

        assertEquals(1, gotoElim.functions.size, "Should have one function")

        val mainFunc = gotoElim.functions[0]
        assertTrue(mainFunc.isFullyStructured, "Simple sequence should be fully structured")
        assertEquals(0, mainFunc.remainingGotos.size, "Should have no gotos")
        assertTrue(mainFunc.body.isNotEmpty(), "Should have body statements")
    }

    @Test
    fun whileLoop() {
        val assembly = """
            main:
                LDX #${'$'}10
            loop:
                DEX
                BNE loop
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val loops = lines.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val conditionals = lines.detectConditionals(resolution, entries, reachability, blocks, cfg, dominators, loops)
        val regions = lines.formRegions(resolution, entries, reachability, blocks, cfg, dominators, loops, conditionals)
        val gotoElim = lines.eliminateGotos(resolution, entries, reachability, blocks, cfg, dominators, loops, conditionals, regions)

        val mainFunc = gotoElim.functions[0]

        // Check statistics - this checks recursively
        val stats = mainFunc.getStatistics()
        assertTrue(stats["loops"]!! > 0, "Should have at least one loop in statistics")
    }

    @Test
    fun ifThenElse() {
        val assembly = """
            main:
                LDA #${'$'}10
                CMP #${'$'}05
                BEQ then_branch
                LDA #${'$'}20
                JMP end
            then_branch:
                LDA #${'$'}30
            end:
                STA ${'$'}2000
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val loops = lines.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val conditionals = lines.detectConditionals(resolution, entries, reachability, blocks, cfg, dominators, loops)
        val regions = lines.formRegions(resolution, entries, reachability, blocks, cfg, dominators, loops, conditionals)
        val gotoElim = lines.eliminateGotos(resolution, entries, reachability, blocks, cfg, dominators, loops, conditionals, regions)

        val mainFunc = gotoElim.functions[0]

        // Check statistics - this checks recursively
        val stats = mainFunc.getStatistics()
        assertTrue(stats["ifs"]!! > 0, "Should have at least one if in statistics")
    }

    @Test
    fun nestedStructures() {
        val assembly = """
            main:
                LDX #${'$'}10
            outer:
                LDY #${'$'}05
            inner:
                DEY
                BNE inner
                DEX
                BNE outer
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val loops = lines.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val conditionals = lines.detectConditionals(resolution, entries, reachability, blocks, cfg, dominators, loops)
        val regions = lines.formRegions(resolution, entries, reachability, blocks, cfg, dominators, loops, conditionals)
        val gotoElim = lines.eliminateGotos(resolution, entries, reachability, blocks, cfg, dominators, loops, conditionals, regions)

        val mainFunc = gotoElim.functions[0]

        // Should have nested loops
        val stats = mainFunc.getStatistics()
        assertTrue(stats["loops"]!! >= 2, "Should have at least two loops")
    }

    @Test
    fun statisticsCalculation() {
        val assembly = """
            main:
                LDA #${'$'}10
                CMP #${'$'}05
                BEQ skip
                LDA #${'$'}20
            skip:
                LDX #${'$'}10
            loop:
                DEX
                BNE loop
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val loops = lines.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val conditionals = lines.detectConditionals(resolution, entries, reachability, blocks, cfg, dominators, loops)
        val regions = lines.formRegions(resolution, entries, reachability, blocks, cfg, dominators, loops, conditionals)
        val gotoElim = lines.eliminateGotos(resolution, entries, reachability, blocks, cfg, dominators, loops, conditionals, regions)

        val mainFunc = gotoElim.functions[0]

        // Get statistics
        val stats = mainFunc.getStatistics()

        // Should have expected keys
        assertTrue(stats.containsKey("ifs"), "Should have ifs count")
        assertTrue(stats.containsKey("loops"), "Should have loops count")
        assertTrue(stats.containsKey("breaks"), "Should have breaks count")
        assertTrue(stats.containsKey("continues"), "Should have continues count")
        assertTrue(stats.containsKey("returns"), "Should have returns count")
        assertTrue(stats.containsKey("gotos"), "Should have gotos count")
        assertTrue(stats.containsKey("blocks"), "Should have blocks count")

        // Should have at least one if and one loop
        assertTrue(stats["ifs"]!! > 0, "Should have conditionals")
        assertTrue(stats["loops"]!! > 0, "Should have loops")
    }

    @Test
    fun fullyStructuredCode() {
        val assembly = """
            main:
                LDA #${'$'}10
                CMP #${'$'}05
                BEQ then_branch
                LDA #${'$'}20
                JMP end
            then_branch:
                LDA #${'$'}30
            end:
                STA ${'$'}2000
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val loops = lines.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val conditionals = lines.detectConditionals(resolution, entries, reachability, blocks, cfg, dominators, loops)
        val regions = lines.formRegions(resolution, entries, reachability, blocks, cfg, dominators, loops, conditionals)
        val gotoElim = lines.eliminateGotos(resolution, entries, reachability, blocks, cfg, dominators, loops, conditionals, regions)

        // Check that functions are fully structured
        val fullyStructured = gotoElim.functions.filter { it.isFullyStructured }
        assertTrue(fullyStructured.isNotEmpty(), "Should have fully structured functions")

        // Check that functionsWithGotos is empty for this simple case
        val withGotos = gotoElim.functionsWithGotos
        assertEquals(0, withGotos.size, "Simple structured code should have no remaining gotos")
    }
}
