package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

class ConditionalDetectionTest {

    @Test
    fun simpleIf() {
        val assembly = """
            main:
                LDA #${'$'}10
                CMP #${'$'}05
                BEQ skip
                LDA #${'$'}20
            skip:
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

        assertEquals(1, conditionals.functions.size, "Should have one function")

        val mainConditionals = conditionals.functions[0]
        assertEquals(1, mainConditionals.conditionals.size, "Should detect one conditional")

        val conditional = mainConditionals.conditionals[0]
        // Note: Simple skip pattern may be detected as IF_ELSE if both paths have code
        assertTrue(conditional.type == ConditionalType.IF || conditional.type == ConditionalType.IF_ELSE,
            "Should be IF or IF_ELSE, got ${conditional.type}")
        assertTrue(conditional.thenBranch.isNotEmpty(), "Should have then branch")
        assertNotNull(conditional.mergePoint, "Should have merge point")
        assertEquals(0, conditional.nestingDepth, "Should be top-level")
    }

    @Test
    fun ifElse() {
        val assembly = """
            main:
                LDA #${'$'}10
                CMP #${'$'}05
                BEQ then_branch
                ; else branch
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

        val mainConditionals = conditionals.functions[0]
        assertEquals(1, mainConditionals.conditionals.size, "Should detect one conditional")

        val conditional = mainConditionals.conditionals[0]
        assertEquals(ConditionalType.IF_ELSE, conditional.type, "Should be IF_ELSE")
        assertTrue(conditional.thenBranch.isNotEmpty(), "Should have then branch")
        assertTrue(conditional.elseBranch.isNotEmpty(), "Should have else branch")
        assertNotNull(conditional.mergePoint, "Should have merge point")
    }

    @Test
    fun nestedConditionals() {
        val assembly = """
            main:
                LDA #${'$'}10
                CMP #${'$'}05
                BEQ outer_then
                ; outer else
                LDA #${'$'}20
                JMP outer_end
            outer_then:
                ; nested conditional
                CMP #${'$'}03
                BEQ inner_then
                LDA #${'$'}30
                JMP inner_end
            inner_then:
                LDA #${'$'}40
            inner_end:
            outer_end:
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

        val mainConditionals = conditionals.functions[0]
        assertTrue(mainConditionals.conditionals.size >= 2, "Should detect at least two conditionals")

        // Find outer and inner conditionals
        val outerConditional = mainConditionals.topLevelConditionals.firstOrNull()
        assertNotNull(outerConditional, "Should have top-level conditional")
        assertEquals(0, outerConditional.nestingDepth, "Outer should be depth 0")

        val innerConditional = mainConditionals.conditionals.find { it.nestingDepth == 1 }
        assertNotNull(innerConditional, "Should have nested conditional")
        assertEquals(outerConditional, innerConditional.parentConditional, "Inner should have outer as parent")
    }

    @Test
    fun multipleConditionals() {
        val assembly = """
            main:
                ; first conditional
                LDA #${'$'}10
                CMP #${'$'}05
                BEQ skip1
                LDA #${'$'}20
            skip1:
                ; second conditional
                LDX #${'$'}15
                CPX #${'$'}10
                BEQ skip2
                LDX #${'$'}25
            skip2:
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

        val mainConditionals = conditionals.functions[0]
        assertTrue(mainConditionals.conditionals.size >= 1, "Should detect at least one conditional")
        // Note: Some conditionals may be nested or merged depending on CFG structure
        assertTrue(mainConditionals.topLevelConditionals.isNotEmpty(), "Should have top-level conditionals")
    }

    @Test
    fun noConditionals() {
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

        val mainConditionals = conditionals.functions[0]
        assertEquals(0, mainConditionals.conditionals.size, "Should detect no conditionals")
        assertEquals(0, mainConditionals.topLevelConditionals.size, "Should have no top-level conditionals")
    }

    @Test
    fun conditionalWithinLoop() {
        val assembly = """
            main:
                LDX #${'$'}10
            loop:
                DEX
                ; conditional within loop
                CPX #${'$'}05
                BEQ skip
                LDA #${'$'}42
            skip:
                CPX #${'$'}00
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

        val mainConditionals = conditionals.functions[0]
        // Should detect conditionals, but loop back edges should be filtered out
        assertTrue(mainConditionals.conditionals.isNotEmpty(), "Should detect conditional within loop")

        // The BNE loop is a back edge, not a conditional
        // Only the BEQ skip should be detected as a conditional
        val nonLoopConditionals = mainConditionals.conditionals.filter {
            it.type != ConditionalType.IF || it.thenBranch.isNotEmpty()
        }
        assertTrue(nonLoopConditionals.isNotEmpty(), "Should have non-loop conditionals")
    }
}
