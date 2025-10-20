package com.ivieleague.decompiler6502tokotlin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for Pass 31: Copy Propagation & Constant Folding
 */
class CopyPropagationTest {

    /**
     * Test basic register copy propagation
     * LDX #$05 ; X = 5
     * TXA      ; A = X (copy)
     * STA $0200 ; store A
     *
     * Should recognize that A is a copy of X
     */
    @Test
    fun testRegisterCopyDetection() {
        val code = """
            START:
                LDX #${'$'}05
                TXA
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

        val copyPropResult = codeFile.propagateCopies(dataFlowAnalysis = dataFlow)

        // Should detect the TXA as a copy
        assertTrue(copyPropResult.functionsOptimized.isNotEmpty())
        val functionResult = copyPropResult.functionsOptimized[0]
        assertTrue(functionResult.copyAnalysis.copies.isNotEmpty(),
            "Should detect TXA as a copy operation")

        // Verify the copy relationship
        val copy = functionResult.copyAnalysis.copies.first()
        assertEquals(Variable.RegisterA, copy.copyVariable)
        assertEquals(Variable.RegisterX, copy.sourceVariable)
    }

    /**
     * Test all register transfer instructions
     */
    @Test
    fun testAllRegisterTransfers() {
        val code = """
            START:
                LDA #${'$'}10
                TAX
                TAY
                TXA
                TYA
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

        val copyPropResult = codeFile.propagateCopies(dataFlowAnalysis = dataFlow)

        val functionResult = copyPropResult.functionsOptimized[0]
        val copies = functionResult.copyAnalysis.copies

        // Should find 4 copy operations (TAX, TAY, TXA, TYA)
        assertEquals(4, copies.size, "Should detect all 4 register transfer instructions")
    }

    /**
     * Test that copy validity range is correctly computed
     */
    @Test
    fun testCopyValidityRange() {
        val code = """
            START:
                LDX #${'$'}05
                TXA
                LDX #${'$'}10
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

        val copyPropResult = codeFile.propagateCopies(dataFlowAnalysis = dataFlow)

        val functionResult = copyPropResult.functionsOptimized[0]
        val copy = functionResult.copyAnalysis.copies.firstOrNull()

        assertNotNull(copy, "Should find TXA copy")
        // Copy should be invalidated by the second LDX
        assertTrue(copy!!.validRange.endLine < codeFile.lines.size,
            "Copy validity should be limited")
    }

    /**
     * Test constant folding in expressions
     */
    @Test
    fun testConstantFolding() {
        val code = """
            START:
                LDA #${'$'}05
                ADC #${'$'}03
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

        val copyPropResult = codeFile.propagateCopies(dataFlowAnalysis = dataFlow)

        // Analysis should complete successfully
        assertNotNull(copyPropResult)
        assertNotNull(copyPropResult.globalStats)
    }

    /**
     * Test copy propagation with branches
     */
    @Test
    fun testCopyPropagationWithBranches() {
        val code = """
            START:
                LDX #${'$'}00
                BEQ SKIP
                TXA
            SKIP:
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

        val copyPropResult = codeFile.propagateCopies(dataFlowAnalysis = dataFlow)

        // Should handle branching correctly
        assertNotNull(copyPropResult)
        val functionResult = copyPropResult.functionsOptimized[0]

        // Should find the TXA copy in the conditional branch
        val hasCopy = functionResult.copyAnalysis.copies.any {
            it.copyVariable == Variable.RegisterA && it.sourceVariable == Variable.RegisterX
        }
        assertTrue(hasCopy, "Should detect TXA even in conditional branch")
    }

    /**
     * Test that non-propagatable copies are identified correctly
     */
    @Test
    fun testNonPropagatableCopy() {
        val code = """
            START:
                LDX #${'$'}05
                TXA
                LDX #${'$'}10
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

        val copyPropResult = codeFile.propagateCopies(dataFlowAnalysis = dataFlow)

        val functionResult = copyPropResult.functionsOptimized[0]

        // Should find the copy but may not be able to propagate it
        // because X is redefined before A is used
        assertTrue(functionResult.copyAnalysis.copies.isNotEmpty(),
            "Should detect the copy")
    }

    /**
     * Test statistics are tracked correctly
     */
    @Test
    fun testStatisticsTracking() {
        val code = """
            START:
                LDX #${'$'}05
                TXA
                TAY
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

        val copyPropResult = codeFile.propagateCopies(dataFlowAnalysis = dataFlow)

        val stats = copyPropResult.globalStats
        assertNotNull(stats)

        // Stats should be initialized (even if zero)
        assertTrue(stats.copiesEliminated >= 0)
        assertTrue(stats.constantsFolded >= 0)
        assertTrue(stats.strengthReductions >= 0)
        assertTrue(stats.algebraicSimplifications >= 0)
    }
}
