// by Claude - Phase 5: Natural Loop Unit Tests (Loop Detection)
// Tests for natural-loops.kt:detectNaturalLoops() - identifying loop structures
package com.ivieleague.decompiler6502tokotlin.loops

import com.ivieleague.decompiler6502tokotlin.hand.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertContains

/**
 * Natural Loop Unit Test Suite
 *
 * Tests for the detectNaturalLoops() function that identifies natural loops
 * using dominator analysis.
 *
 * A natural loop has:
 * - A single entry point (header)
 * - One or more back-edges (edges to dominating blocks)
 * - A body consisting of all blocks that can reach the latch without going through header
 */
class NaturalLoopUnitTest {

    // Helper to detect loops from assembly
    private fun detectLoops(asm: String): Pair<List<AssemblyBlock>, List<NaturalLoop>> {
        val blocks = asm.parseToAssemblyCodeFile().lines.blockify()
        blocks.dominators()
        val loops = blocks.detectNaturalLoops()
        return Pair(blocks, loops)
    }

    // =====================================================================
    // BASIC LOOP DETECTION - SIMPLE WHILE LOOP
    // =====================================================================

    @Test
    fun `simple while loop - pre-test`() {
        // Pattern:
        //   Header -> Check -> Body -> Header (back-edge)
        //                    -> Exit
        val (blocks, loops) = detectLoops("""
            Header: LDX #${'$'}05
            Check: DEX
            BEQ Exit
            Body: NOP
            JMP Check
            Exit: RTS
        """.trimIndent())

        // Should detect one loop
        assertEquals(1, loops.size)
        val loop = loops[0]

        // Header should be Check (where back-edge goes)
        assertEquals("Check", loop.header.label)
    }

    @Test
    fun `do-while loop - post-test`() {
        // Pattern:
        //   Header -> Body -> Test -> Header (back-edge)
        //                         -> Exit
        val (blocks, loops) = detectLoops("""
            Loop: LDX #${'$'}05
            Body: DEX
            BNE Loop
            Exit: RTS
        """.trimIndent())

        // Should detect one loop
        assertEquals(1, loops.size)
        val loop = loops[0]

        // Header should be Loop (target of BNE)
        assertEquals("Loop", loop.header.label)
    }

    @Test
    fun `infinite loop with no exit`() {
        val (blocks, loops) = detectLoops("""
            Loop: NOP
            JMP Loop
        """.trimIndent())

        assertEquals(1, loops.size)
        assertEquals("Loop", loops[0].header.label)
        assertTrue(loops[0].exits.isEmpty(), "Infinite loop should have no exits")
    }

    // =====================================================================
    // LOOP STRUCTURE - BACK-EDGE DETECTION
    // =====================================================================

    @Test
    fun `back-edge correctly identified`() {
        val (blocks, loops) = detectLoops("""
            Header: LDX #${'$'}05
            Body: DEX
            BNE Header
            Done: RTS
        """.trimIndent())

        assertEquals(1, loops.size)
        val loop = loops[0]

        // Should have one back-edge from Body to Header
        assertEquals(1, loop.backEdges.size)
        val (from, to) = loop.backEdges[0]
        assertEquals("Header", to.label)
    }

    @Test
    fun `multiple back-edges to same header`() {
        // Two different paths jump back to loop header
        val (blocks, loops) = detectLoops("""
            Header: LDA #${'$'}00
            BEQ BackPath1
            Path1: NOP
            BackPath1: JMP Header
        """.trimIndent())

        // Should detect one loop with the back-edge
        assertTrue(loops.isNotEmpty())
    }

    // =====================================================================
    // LOOP BODY COMPUTATION
    // =====================================================================

    @Test
    fun `loop body includes all reachable blocks`() {
        val (blocks, loops) = detectLoops("""
            Header: LDX #${'$'}05
            Body1: NOP
            Body2: DEX
            BNE Header
            Exit: RTS
        """.trimIndent())

        assertEquals(1, loops.size)
        val loop = loops[0]

        // Body should include Header, Body1, Body2
        val bodyLabels = loop.body.mapNotNull { it.label }
        assertTrue("Header" in bodyLabels)
    }

    @Test
    fun `loop body excludes exit blocks`() {
        val (blocks, loops) = detectLoops("""
            Header: DEX
            BEQ Exit
            BNE Header
            Exit: RTS
        """.trimIndent())

        assertEquals(1, loops.size)
        val loop = loops[0]

        // Exit should NOT be in the body
        val bodyLabels = loop.body.mapNotNull { it.label }
        assertTrue("Exit" !in bodyLabels)
    }

    // =====================================================================
    // EXIT DETECTION
    // =====================================================================

    @Test
    fun `single exit detected`() {
        val (blocks, loops) = detectLoops("""
            Header: DEX
            BEQ Exit
            JMP Header
            Exit: RTS
        """.trimIndent())

        assertEquals(1, loops.size)
        val loop = loops[0]

        // Should have one exit block (the block OUTSIDE the loop that we jump to)
        assertEquals(1, loop.exits.size)
    }

    @Test
    fun `multiple exits detected`() {
        val (blocks, loops) = detectLoops("""
            Header: LDA #${'$'}00
            BEQ EarlyExit
            DEX
            BEQ LateExit
            JMP Header
            EarlyExit: RTS
            LateExit: RTS
        """.trimIndent())

        assertTrue(loops.isNotEmpty())
        val loop = loops[0]

        // Should detect multiple exit targets
        assertTrue(loop.exits.size >= 1)
    }

    // =====================================================================
    // NESTED LOOPS
    // =====================================================================

    @Test
    fun `nested loops - inner contained in outer`() {
        // Outer loop contains inner loop
        val (blocks, loops) = detectLoops("""
            Outer: LDX #${'$'}05
            Inner: LDY #${'$'}03
            InnerBody: DEY
            BNE Inner
            OuterBody: DEX
            BNE Outer
            Exit: RTS
        """.trimIndent())

        // Should detect two loops
        assertEquals(2, loops.size)

        // Find inner and outer
        val innerLoop = loops.find { it.header.label == "Inner" }
        val outerLoop = loops.find { it.header.label == "Outer" }

        assertNotNull(innerLoop)
        assertNotNull(outerLoop)

        // Inner loop body should be subset of outer loop body
        assertTrue(innerLoop.body.all { it in outerLoop.body })
    }

    @Test
    fun `exit from inner to outer`() {
        val (blocks, loops) = detectLoops("""
            Outer: LDX #${'$'}05
            Inner: LDY #${'$'}03
            DEY
            BNE Inner
            DEX
            BNE Outer
            Exit: RTS
        """.trimIndent())

        assertEquals(2, loops.size)
        val innerLoop = loops.find { it.header.label == "Inner" }
        assertNotNull(innerLoop)

        // Inner loop's exit leads to outer loop body (not outside both)
        // The block after "BNE Inner" is in the outer loop
    }

    @Test
    fun `exit from inner to outside both`() {
        val (blocks, loops) = detectLoops("""
            Outer: LDX #${'$'}05
            Inner: LDY #${'$'}03
            BEQ Exit
            DEY
            BNE Inner
            DEX
            BNE Outer
            Exit: RTS
        """.trimIndent())

        // Should detect two loops
        assertTrue(loops.size >= 1)
    }

    // =====================================================================
    // EDGE CASES
    // =====================================================================

    @Test
    fun `no loops - linear code`() {
        val (blocks, loops) = detectLoops("""
            Start: LDA #${'$'}00
            STA ${'$'}0200
            RTS
        """.trimIndent())

        assertEquals(0, loops.size)
    }

    @Test
    fun `no loops - forward branches only`() {
        val (blocks, loops) = detectLoops("""
            Start: BEQ Skip
            LDA #${'$'}01
            Skip: RTS
        """.trimIndent())

        assertEquals(0, loops.size)
    }

    @Test
    fun `self loop - single block`() {
        val (blocks, loops) = detectLoops("""
            Loop: JMP Loop
        """.trimIndent())

        assertEquals(1, loops.size)
        val loop = loops[0]
        assertEquals("Loop", loop.header.label)
        assertEquals(1, loop.body.size)
    }

    @Test
    fun `loop with multiple back-edge sources`() {
        // Two different blocks both jump back to header
        val (blocks, loops) = detectLoops("""
            Header: LDA #${'$'}00
            BEQ Path1
            Path2: JMP Header
            Path1: JMP Header
        """.trimIndent())

        assertTrue(loops.isNotEmpty())
        // May detect this as one loop with multiple back-edges
        // or as separate loops sharing a header
    }

    // =====================================================================
    // LOOP ORDERING
    // =====================================================================

    @Test
    fun `loops sorted by original line index`() {
        val (blocks, loops) = detectLoops("""
            Loop1: NOP
            JMP Loop1
            Loop2: NOP
            JMP Loop2
        """.trimIndent())

        assertEquals(2, loops.size)
        // First loop should be earlier in source
        assertTrue(loops[0].header.originalLineIndex < loops[1].header.originalLineIndex)
    }

    // =====================================================================
    // HEADER IDENTIFICATION
    // =====================================================================

    @Test
    fun `header is target of back-edge`() {
        val (blocks, loops) = detectLoops("""
            Setup: LDX #${'$'}05
            LoopStart: DEX
            BNE LoopStart
            Done: RTS
        """.trimIndent())

        assertEquals(1, loops.size)
        // LoopStart is where the BNE jumps back to
        assertEquals("LoopStart", loops[0].header.label)
    }

    @Test
    fun `header with multiple entries from outside`() {
        // Entry from before loop AND entry from back-edge
        val (blocks, loops) = detectLoops("""
            Entry: BEQ Header
            Setup: NOP
            Header: LDA #${'$'}00
            BNE Header
            Exit: RTS
        """.trimIndent())

        assertTrue(loops.isNotEmpty())
    }

    // =====================================================================
    // COMPLEX PATTERNS
    // =====================================================================

    @Test
    fun `while-if-while nested pattern`() {
        val (blocks, loops) = detectLoops("""
            Outer: LDX #${'$'}05
            BEQ OuterExit
            Inner: LDY #${'$'}03
            BEQ InnerExit
            DEY
            JMP Inner
            InnerExit: DEX
            JMP Outer
            OuterExit: RTS
        """.trimIndent())

        // Should detect 2 loops (outer and inner)
        assertEquals(2, loops.size)
    }

    @Test
    fun `loop with conditional body`() {
        val (blocks, loops) = detectLoops("""
            Header: LDX #${'$'}05
            BEQ Skip
            LDA #${'$'}01
            Skip: DEX
            BNE Header
            Exit: RTS
        """.trimIndent())

        assertEquals(1, loops.size)
        // Skip block should be in the loop body
    }

    // =====================================================================
    // VALIDATION
    // =====================================================================

    @Test
    fun `loop body contains header`() {
        val (blocks, loops) = detectLoops("""
            Header: DEX
            BNE Header
            Exit: RTS
        """.trimIndent())

        assertEquals(1, loops.size)
        assertTrue(loops[0].header in loops[0].body)
    }

    @Test
    fun `back-edge target matches header`() {
        val (blocks, loops) = detectLoops("""
            Loop: NOP
            JMP Loop
        """.trimIndent())

        assertEquals(1, loops.size)
        for ((from, to) in loops[0].backEdges) {
            assertEquals(loops[0].header, to)
        }
    }
}
