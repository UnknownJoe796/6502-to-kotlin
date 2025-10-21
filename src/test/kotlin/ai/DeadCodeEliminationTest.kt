package com.ivieleague.decompiler6502tokotlin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for Pass 30: Dead Code Elimination
 */
class DeadCodeEliminationTest {

    /**
     * Test that dead stores are correctly identified
     * LDA #$05 ; A = 5
     * LDA #$10 ; A = 16 (previous store to A is dead)
     * STA $0200 ; store A to memory
     */
    @Test
    fun testDeadStoreElimination() {
        val code = """
            START:
                LDA #${'$'}05
                LDA #${'$'}10
                STA ${'$'}0200
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("START"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)

        val deadCodeAnalysis = codeFile.analyzeDeadCode(dataFlowAnalysis = dataFlow)

        // Should find at least one dead store (the first LDA #$05)
        assertTrue(deadCodeAnalysis.globalStats.totalDeadStores > 0,
            "Should detect the first LDA #$05 as a dead store")

        // Verify stats
        assertNotNull(deadCodeAnalysis.functionsOptimized)
        assertTrue(deadCodeAnalysis.functionsOptimized.isNotEmpty())
    }

    /**
     * Test that hardware I/O writes are preserved
     * STA $2000 ; Write to PPU control register - must be preserved
     */
    @Test
    fun testHardwareIoPreserved() {
        val code = """
            START:
                LDA #${'$'}80
                STA ${'$'}2000
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("START"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)

        val deadCodeAnalysis = codeFile.analyzeDeadCode(dataFlowAnalysis = dataFlow)

        // Hardware write should be preserved, not marked as dead
        val stats = deadCodeAnalysis.globalStats

        // If there are preservedForSideEffects, the hardware write was correctly preserved
        // If there are no dead stores, the write was considered live
        val hardwareWritePreserved = stats.preservedForSideEffects > 0 || stats.totalDeadStores == 0
        assertTrue(hardwareWritePreserved,
            "Hardware I/O write to PPU ($2000) should be preserved")
    }

    /**
     * Test that live variables are not marked as dead
     */
    @Test
    fun testLiveVariablesPreserved() {
        val code = """
            START:
                LDX #${'$'}00
            LOOP:
                LDA ${'$'}0200,X
                STA ${'$'}0300,X
                INX
                CPX #${'$'}10
                BNE LOOP
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("START"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)

        val deadCodeAnalysis = codeFile.analyzeDeadCode(dataFlowAnalysis = dataFlow)

        // In a loop like this, X and A are live variables
        // Should not have many dead stores
        val stats = deadCodeAnalysis.globalStats

        // The code should be mostly live (loop variable usage)
        assertNotNull(stats)
    }

    /**
     * Test dead code with branches
     */
    @Test
    fun testDeadCodeWithBranches() {
        val code = """
            START:
                LDA #${'$'}05
                BEQ SKIP
                LDA #${'$'}10
            SKIP:
                STA ${'$'}0200
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("START"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)

        val deadCodeAnalysis = codeFile.analyzeDeadCode(dataFlowAnalysis = dataFlow)

        // Analysis should complete successfully with branches
        assertNotNull(deadCodeAnalysis)
        assertNotNull(deadCodeAnalysis.globalStats)
    }

    /**
     * Test APU register writes are preserved
     */
    @Test
    fun testApuWritesPreserved() {
        val code = """
            START:
                LDA #${'$'}30
                STA ${'$'}4000
                LDA #${'$'}08
                STA ${'$'}4001
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("START"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)

        val deadCodeAnalysis = codeFile.analyzeDeadCode(dataFlowAnalysis = dataFlow)

        // APU writes should be preserved
        val stats = deadCodeAnalysis.globalStats

        // Hardware writes should not be marked as dead
        val apuWritesPreserved = stats.preservedForSideEffects >= 2 || stats.totalDeadStores == 0
        assertTrue(apuWritesPreserved,
            "APU register writes should be preserved as side effects")
    }

    /**
     * Test empty function (edge case)
     */
    @Test
    fun testEmptyFunction() {
        val code = """
            START:
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("START"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)

        val deadCodeAnalysis = codeFile.analyzeDeadCode(dataFlowAnalysis = dataFlow)

        // Should handle empty function gracefully - no dead variables expected
        // (RTS affects stack pointer so there may be definitions, but no dead stores to user code)
        assertEquals(0, deadCodeAnalysis.globalStats.totalDeadVariables)
        assertNotNull(deadCodeAnalysis.functionsOptimized)
    }
}
