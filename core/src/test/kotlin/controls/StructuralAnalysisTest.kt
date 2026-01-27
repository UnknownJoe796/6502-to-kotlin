package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

// by Claude - Tests for the new principled structural analysis system

class StructuralAnalysisTest {

    // Helper to parse and analyze assembly
    private fun analyzeStructure(asm: String): StructureResult {
        val blocks = asm.parseToAssemblyCodeFile().lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        val function = functions.first()
        return function.analyzeControlsStructured(useStrict = false)
    }

    // Helper to parse assembly to blocks
    private fun parseBlocks(asm: String): List<AssemblyBlock> {
        return asm.parseToAssemblyCodeFile().lines.blockify()
    }

    // =====================================================================
    // EDGE TRACKING TESTS
    // =====================================================================

    @Test
    fun `edge tracker collects all edges`() {
        val result = analyzeStructure("""
            Start: LDA #${'$'}00
            BEQ Skip
            STA ${'$'}00
            Skip: RTS
        """.trimIndent())

        val validation = result.validation
        assertNotNull(validation, "Should have validation result")
        // Should have edges: Start->STA (fall-through), Start->Skip (branch), STA->Skip (fall-through)
        assertTrue(validation!!.allEdges.isNotEmpty(), "Should detect edges")
    }

    @Test
    fun `edge tracker marks consumed edges`() {
        val result = analyzeStructure("""
            Start: LDA #${'$'}00
            BEQ Skip
            STA ${'$'}00
            Skip: RTS
        """.trimIndent())

        val validation = result.validation!!
        // An if-then pattern should consume the conditional branch edge
        assertTrue(validation.consumedEdges.isNotEmpty(), "Should have consumed edges for if-then")
    }

    @Test
    fun `edge tracker reports missing edges`() {
        // A simple linear sequence should have all edges consumed
        val result = analyzeStructure("""
            Start: LDA #${'$'}00
            STA ${'$'}00
            RTS
        """.trimIndent())

        val validation = result.validation!!
        assertTrue(validation.missingEdges.isEmpty(), "Simple linear code should have no missing edges")
    }

    // =====================================================================
    // REGION BUILDING TESTS
    // =====================================================================

    @Test
    fun `linear code produces sequence region`() {
        val result = analyzeStructure("""
            Start: LDA #${'$'}00
            STA ${'$'}00
            LDX #${'$'}01
            STX ${'$'}01
            RTS
        """.trimIndent())

        val region = result.region
        // Should be a sequence of block regions
        assertTrue(region.kind == RegionKind.SEQUENCE || region.kind == RegionKind.BLOCK,
            "Linear code should produce sequence or block region, got ${region.kind}")
    }

    @Test
    fun `if-then produces IfThenRegion`() {
        val result = analyzeStructure("""
            Start: LDA #${'$'}00
            BEQ Skip
            STA ${'$'}00
            Skip: RTS
        """.trimIndent())

        fun hasIfThen(region: Region): Boolean = when (region) {
            is IfThenRegion -> true
            is SequenceRegion -> region.children.any { hasIfThen(it) }
            else -> false
        }

        assertTrue(hasIfThen(result.region), "Should detect if-then pattern")
    }

    @Test
    fun `natural loop produces loop region`() {
        val result = analyzeStructure("""
            Loop: LDA ${'$'}00
            BEQ Done
            DEX
            JMP Loop
            Done: RTS
        """.trimIndent())

        fun hasLoop(region: Region): Boolean = when (region) {
            is WhileLoopRegion, is DoWhileLoopRegion, is InfiniteLoopRegion -> true
            is SequenceRegion -> region.children.any { hasLoop(it) }
            else -> false
        }

        assertTrue(hasLoop(result.region) || result.validation?.consumedEdges?.any { it.kind == EdgeKind.BACK_EDGE } == true,
            "Should detect loop pattern or consume back-edge")
    }

    // =====================================================================
    // CONVERSION TO CONTROL NODES
    // =====================================================================

    @Test
    fun `region converts to control nodes`() {
        val result = analyzeStructure("""
            Start: LDA #${'$'}00
            BEQ Skip
            STA ${'$'}00
            Skip: RTS
        """.trimIndent())

        val controlNodes = result.region.toControlNodes()
        assertTrue(controlNodes.isNotEmpty(), "Should produce control nodes")
    }

    @Test
    fun `analyzeControlsStructured populates asControls`() {
        val blocks = parseBlocks("""
            Start: LDA #${'$'}00
            BEQ Skip
            STA ${'$'}00
            Skip: RTS
        """.trimIndent())
        blocks.dominators()
        val functions = blocks.functionify()
        val function = functions.first()

        function.analyzeControlsStructured()

        assertNotNull(function.asControls, "asControls should be populated")
        assertTrue(function.asControls!!.isNotEmpty(), "asControls should not be empty")
    }

    // =====================================================================
    // DIAGNOSTIC OUTPUT TESTS
    // =====================================================================

    @Test
    fun `region summarize produces output`() {
        val result = analyzeStructure("""
            Start: LDA #${'$'}00
            BEQ Skip
            STA ${'$'}00
            Skip: RTS
        """.trimIndent())

        val summary = result.region.summarize()
        assertTrue(summary.isNotEmpty(), "Summary should not be empty")
        assertTrue(summary.contains("BLOCK") || summary.contains("IF_THEN") || summary.contains("SEQUENCE"),
            "Summary should contain region kind")
    }

    @Test
    fun `validation report produces output`() {
        val result = analyzeStructure("""
            Start: LDA #${'$'}00
            RTS
        """.trimIndent())

        val report = result.validation?.report()
        assertNotNull(report, "Should have validation report")
        assertTrue(report!!.contains("Edge Validation Report"), "Report should have header")
    }

    // =====================================================================
    // 6502 PATTERN TESTS
    // =====================================================================

    @Test
    fun `detect consecutive branch pairs`() {
        val blocks = parseBlocks("""
            Start: LDA ${'$'}00
            BPL Positive
            BMI Negative
            Positive: STA ${'$'}01
            RTS
            Negative: STA ${'$'}02
            RTS
        """.trimIndent())
        val pairs = blocks.detectConsecutiveBranchPairs()

        assertTrue(pairs.isNotEmpty(), "Should detect BPL/BMI pair")
        val pair = pairs.first()
        assertEquals(AssemblyOp.BPL, pair.firstOp)
        assertEquals(AssemblyOp.BMI, pair.secondOp)
        assertTrue(pair.secondBranchIsUnconditional, "BMI should be unconditional after BPL")
    }

    @Test
    fun `consecutive branch pair condition building`() {
        val blocks = parseBlocks("""
            Start: LDA ${'$'}00
            BPL Positive
            BMI Negative
            Positive: RTS
            Negative: RTS
        """.trimIndent())
        val pairs = blocks.detectConsecutiveBranchPairs()

        val pair = pairs.first()
        val condition = pair.buildCombinedCondition()

        assertTrue(condition is FlagTest)
        assertEquals(AssemblyAffectable.Negative, (condition as FlagTest).flag)
    }

    // =====================================================================
    // DEBUG TESTS
    // =====================================================================

    @Test
    fun `debug while loop BEQ exits loop`() {
        // Pattern from ControlsUnitTest that's failing
        val result = analyzeStructure("""
            Header: LDX #${'$'}05
            Check: DEX
            BEQ Exit
            JMP Check
            Exit: RTS
        """.trimIndent())

        println("Region tree:")
        println(result.region.summarize())

        println("\nControl nodes:")
        val nodes = result.region.toControlNodes()
        for (node in nodes) {
            println("  ${node::class.simpleName}: ${node}")
        }

        println("\nLooking for LoopNode:")
        val loopNodes = nodes.filterIsInstance<LoopNode>()
        println("  Found ${loopNodes.size} loop nodes")

        // What natural loops were detected?
        val blocks = parseBlocks("""
            Header: LDX #${'$'}05
            Check: DEX
            BEQ Exit
            JMP Check
            Exit: RTS
        """.trimIndent())

        println("\nBlocks created: ${blocks.size}")
        for (b in blocks) {
            println("  ${b.label ?: "@${b.originalLineIndex}"}: ${b.lines.size} lines, function=${b.function?.startingBlock?.label}")
            println("    fall-through -> ${b.fallThroughExit?.label ?: b.fallThroughExit?.originalLineIndex?.let { "@$it" }}")
            println("    branch -> ${b.branchExit?.label ?: b.branchExit?.originalLineIndex?.let { "@$it" }}")
        }

        val functions = blocks.functionify()
        println("\nFunctions: ${functions.size}")
        for (f in functions) {
            println("  ${f.startingBlock.label}: ${f.blocks?.size ?: 0} blocks")
            f.blocks?.forEach { b ->
                println("    - ${b.label ?: "@${b.originalLineIndex}"}")
            }
        }

        blocks.dominators()
        val naturalLoops = blocks.detectNaturalLoops()
        println("\nNatural loops detected: ${naturalLoops.size}")
        for (loop in naturalLoops) {
            println("  Header: ${loop.header.label}, body: ${loop.body.size} blocks")
        }
    }

    // =====================================================================
    // COMPARISON WITH OLD ANALYSIS
    // =====================================================================

    @Test
    fun `compareControlAnalysis produces output`() {
        val blocks = parseBlocks("""
            Start: LDA #${'$'}00
            BEQ Skip
            STA ${'$'}00
            Skip: RTS
        """.trimIndent())
        blocks.dominators()
        val functions = blocks.functionify()
        val function = functions.first()

        val comparison = function.compareControlAnalysis()

        assertTrue(comparison.isNotEmpty(), "Comparison should produce output")
        assertTrue(comparison.contains("OLD ANALYSIS"), "Comparison should have old analysis section")
        assertTrue(comparison.contains("NEW ANALYSIS"), "Comparison should have new analysis section")
        assertTrue(comparison.contains("EDGE TRACKING"), "Comparison should have edge tracking section")
    }
}
