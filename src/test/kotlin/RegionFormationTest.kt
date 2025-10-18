package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

class RegionFormationTest {

    @Test
    fun simpleBlockRegion() {
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

        assertEquals(1, regions.functions.size, "Should have one function")

        val mainRegions = regions.functions[0]
        assertNotNull(mainRegions.rootRegion, "Should have root region")
        assertTrue(mainRegions.rootRegion is Region.Function, "Root should be function region")
        assertTrue(mainRegions.allRegions.isNotEmpty(), "Should have regions")
    }

    @Test
    fun loopRegion() {
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

        val mainRegions = regions.functions[0]

        // Should have loop region
        val loopRegion = mainRegions.allRegions.find { it is Region.Loop }
        assertNotNull(loopRegion, "Should have loop region")
        assertTrue(loopRegion is Region.Loop, "Should be loop region type")
        assertTrue((loopRegion as Region.Loop).loop.loopType == LoopType.DO_WHILE, "Should be do-while loop")
    }

    @Test
    fun conditionalRegion() {
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

        val mainRegions = regions.functions[0]

        // Should have if-then-else region
        val conditionalRegion = mainRegions.allRegions.find { it is Region.IfThenElse }
        assertNotNull(conditionalRegion, "Should have conditional region")
        assertTrue(conditionalRegion is Region.IfThenElse, "Should be if-then-else region type")

        val ifRegion = conditionalRegion as Region.IfThenElse
        assertNotNull(ifRegion.thenRegion, "Should have then region")
        assertNotNull(ifRegion.elseRegion, "Should have else region")
    }

    @Test
    fun nestedRegions() {
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

        val mainRegions = regions.functions[0]

        // Should have loop regions (may be nested or merged)
        val loopRegions = mainRegions.allRegions.filterIsInstance<Region.Loop>()
        assertTrue(loopRegions.isNotEmpty(), "Should have at least one loop region")

        // If we have nested loops, check they're properly structured
        if (loopRegions.size >= 2) {
            val innerLoop = loopRegions.find { it.loop.nestingDepth == 1 }
            val outerLoop = loopRegions.find { it.loop.nestingDepth == 0 }
            assertNotNull(innerLoop, "Should have inner loop")
            assertNotNull(outerLoop, "Should have outer loop")
        }
    }

    @Test
    fun regionDepth() {
        val assembly = """
            main:
                LDA #${'$'}10
                CMP #${'$'}05
                BEQ skip
                LDA #${'$'}20
            skip:
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

        val mainRegions = regions.functions[0]

        // Check depth calculation
        val rootDepth = mainRegions.rootRegion.getDepth()
        assertTrue(rootDepth >= 0, "Should have valid depth")
    }

    @Test
    fun blockToRegionMapping() {
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

        val mainRegions = regions.functions[0]

        // Each block should be mapped to a region
        assertTrue(mainRegions.blockToRegion.isNotEmpty(), "Should have block to region mapping")

        // All blocks should be mapped
        val allBlocks = mainRegions.function.blocks.map { it.leaderIndex }
        allBlocks.forEach { blockIndex ->
            assertNotNull(mainRegions.blockToRegion[blockIndex], "Block $blockIndex should be mapped to a region")
        }
    }
}
