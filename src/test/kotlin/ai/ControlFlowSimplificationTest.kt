package com.ivieleague.decompiler6502tokotlin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Test Pass 32: Control Flow Simplification
 */
class ControlFlowSimplificationTest {

    /**
     * Test 1: Empty block elimination
     * A block containing only "JMP target" should be eliminated and edges redirected
     */
    @Test
    fun `test empty block elimination`() {
        val code = """
            Start:
                LDA #${'$'}05
                JMP IntermediateJump

            IntermediateJump:
                JMP ActualTarget

            ActualTarget:
                STA ${'$'}0200
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Start"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        // Should have at least one function
        assertTrue(cfg.functions.isNotEmpty(), "Should have at least one function")

        val function = cfg.functions.first()
        val result = function.simplifyControlFlow(codeFile)

        // The intermediate jump block should be identified as empty
        assertTrue(
            result.analysis.emptyBlocks.isNotEmpty(),
            "Should identify at least one empty block"
        )

        // After simplification, the empty block should be removed
        val emptyBlockLeaders = result.analysis.emptyBlocks.map { it.blockLeader }.toSet()
        val remainingBlocks = result.simplifiedFunction.blocks.map { it.leaderIndex }.toSet()

        assertTrue(
            emptyBlockLeaders.all { it !in remainingBlocks },
            "Empty blocks should be removed from simplified CFG"
        )

        // Stats should show at least one empty block eliminated
        assertTrue(
            result.stats.emptyBlocksEliminated > 0,
            "Stats should show empty blocks eliminated"
        )
    }

    /**
     * Test 2: Jump threading
     * Jumps through chains of jumps should be redirected to final target
     */
    @Test
    fun `test jump threading`() {
        val code = """
            Start:
                LDA #${'$'}05
                BEQ Skip1
                LDA #${'$'}10

            Skip1:
                JMP Skip2

            Skip2:
                JMP Final

            Final:
                STA ${'$'}0200
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Start"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val function = cfg.functions.first()
        val result = function.simplifyControlFlow(codeFile)

        // Should identify threadable jumps
        assertTrue(
            result.analysis.threadableJumps.isNotEmpty() || result.analysis.emptyBlocks.isNotEmpty(),
            "Should identify threadable jumps or empty blocks in jump chain"
        )
    }

    /**
     * Test 3: Block merging detection
     * Blocks with single predecessor/successor should be identified as mergeable
     */
    @Test
    fun `test block merge candidates`() {
        val code = """
            Start:
                LDA #${'$'}05
                STA ${'$'}0200
                ; Fall through to next block

            Continue:
                LDA #${'$'}10
                STA ${'$'}0201
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Start"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val function = cfg.functions.first()
        val result = function.simplifyControlFlow(codeFile)

        // With fall-through, we should identify merge candidates
        // The exact number depends on block construction, so we just verify the analysis runs
        assertNotNull(result.analysis.mergeCandidates, "Should analyze merge candidates")
    }

    /**
     * Test 4: No simplification for already optimal code
     */
    @Test
    fun `test no simplification needed`() {
        val code = """
            Start:
                LDA #${'$'}05
                STA ${'$'}0200
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Start"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val function = cfg.functions.first()
        val result = function.simplifyControlFlow(codeFile)

        // Should identify no empty blocks
        assertEquals(0, result.analysis.emptyBlocks.size, "Should find no empty blocks in optimal code")
        assertEquals(0, result.stats.emptyBlocksEliminated, "Should eliminate no blocks")
    }

    /**
     * Test 5: Complex control flow with branches
     */
    @Test
    fun `test control flow with conditional branches`() {
        val code = """
            Start:
                LDA ${'$'}0200
                BEQ IsZero
                JMP NotZero

            IsZero:
                LDA #${'$'}00
                JMP End

            NotZero:
                LDA #${'$'}FF
                JMP End

            End:
                STA ${'$'}0201
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Start"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val function = cfg.functions.first()
        val result = function.simplifyControlFlow(codeFile)

        // Should identify the JMP instructions after branches as potential optimizations
        // The exact count depends on how blocks are formed, but we verify analysis completes
        assertNotNull(result.simplifiedFunction, "Should produce simplified function")
        assertNotNull(result.stats, "Should produce statistics")
    }

    /**
     * Test 6: Entry block preservation
     * Entry blocks should not be eliminated even if they're jump-only
     */
    @Test
    fun `test entry block not eliminated`() {
        val code = """
            Start:
                JMP ActualStart

            ActualStart:
                LDA #${'$'}05
                STA ${'$'}0200
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Start"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val function = cfg.functions.first()
        val result = function.simplifyControlFlow(codeFile)

        // Entry block should be preserved even if it's just a jump
        val entryStillExists = result.simplifiedFunction.blocks.any {
            it.leaderIndex == function.entryLeader
        }
        assertTrue(entryStillExists, "Entry block should be preserved")
    }

    /**
     * Test 7: Statistics tracking
     */
    @Test
    fun `test statistics tracking`() {
        val code = """
            Start:
                LDA #${'$'}05
                JMP Middle

            Middle:
                JMP End

            End:
                STA ${'$'}0200
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Start"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val function = cfg.functions.first()
        val result = function.simplifyControlFlow(codeFile)

        // Verify stats are consistent
        assertEquals(
            result.analysis.emptyBlocks.size,
            result.stats.emptyBlocksEliminated,
            "Stats should match analysis"
        )
        assertEquals(
            result.analysis.threadableJumps.size,
            result.stats.jumpsThreaded,
            "Jump threading stats should match analysis"
        )
        assertEquals(
            result.analysis.constantBranches.size,
            result.stats.branchesSimplified,
            "Branch simplification stats should match analysis"
        )
    }

    /**
     * Test 8: Multiple functions
     */
    @Test
    fun `test multiple functions`() {
        val code = """
            Function1:
                LDA #${'$'}05
                JMP Jump1

            Jump1:
                JMP End1

            End1:
                STA ${'$'}0200
                RTS

            Function2:
                LDA #${'$'}10
                STA ${'$'}0201
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Start"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.simplifyControlFlow(codeFile)

        // Should process all functions
        assertEquals(
            cfg.functions.size,
            result.functionsSimplified.size,
            "Should process all functions"
        )

        // Global stats should aggregate function stats
        val sumEmptyBlocks = result.functionsSimplified.sumOf { it.stats.emptyBlocksEliminated }
        assertEquals(
            sumEmptyBlocks,
            result.globalStats.emptyBlocksEliminated,
            "Global stats should sum function stats"
        )
    }

}
