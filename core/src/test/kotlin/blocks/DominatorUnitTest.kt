// by Claude - Phase 3: Dominator Unit Tests (Dominator Tree Construction)
// Tests for blocks.kt:dominators() - building dominator trees for control flow analysis
package com.ivieleague.decompiler6502tokotlin.blocks

import com.ivieleague.decompiler6502tokotlin.hand.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertSame
import kotlin.test.assertContains

/**
 * Dominator Unit Test Suite
 *
 * Tests for the dominators() function that computes the dominator tree
 * for a control flow graph.
 *
 * A dominator tree captures which blocks must be executed before reaching
 * other blocks. Block A dominates block B if every path from the entry to B
 * must go through A.
 */
class DominatorUnitTest {

    // Helper to parse, blockify, and compute dominators
    private fun buildCfgWithDominators(asm: String): List<AssemblyBlock> {
        val blocks = asm.parseToAssemblyCodeFile().lines.blockify()
        blocks.dominators()
        return blocks
    }

    // =====================================================================
    // BASIC DOMINATOR TREES - LINEAR CHAIN
    // =====================================================================

    @Test
    fun `linear chain - each block dominates next`() {
        val blocks = buildCfgWithDominators("""
            First: LDA #${'$'}00
            Second: STA ${'$'}0200
            Third: LDX #${'$'}01
            Fourth: RTS
        """.trimIndent())

        assertEquals(4, blocks.size)
        // First block has no dominator (it's the root)
        assertNull(blocks[0].immediateDominator)
        // Second is dominated by First
        assertEquals(blocks[0], blocks[1].immediateDominator)
        // Third is dominated by Second
        assertEquals(blocks[1], blocks[2].immediateDominator)
        // Fourth is dominated by Third
        assertEquals(blocks[2], blocks[3].immediateDominator)
    }

    @Test
    fun `linear chain - dominates list is populated`() {
        val blocks = buildCfgWithDominators("""
            First: LDA #${'$'}00
            Second: STA ${'$'}0200
            Third: RTS
        """.trimIndent())

        // First dominates Second
        assertTrue(blocks[1] in blocks[0].dominates)
        // Second dominates Third
        assertTrue(blocks[2] in blocks[1].dominates)
    }

    // =====================================================================
    // DIAMOND PATTERN (IF-THEN-ELSE JOIN)
    // =====================================================================

    @Test
    fun `diamond pattern - only entry dominates join`() {
        // Pattern:
        //    Entry
        //    / \
        //  Then Else
        //    \ /
        //   Join
        val blocks = buildCfgWithDominators("""
            Entry: BEQ Else
            Then: LDA #${'$'}01
            JMP Join
            Else: LDA #${'$'}02
            Join: RTS
        """.trimIndent())

        // Find the blocks
        val entry = blocks.find { it.label == "Entry" }
        val thenBlock = blocks.find { it.label == "Then" }
        val elseBlock = blocks.find { it.label == "Else" }
        val join = blocks.find { it.label == "Join" }

        assertNotNull(entry)
        assertNotNull(thenBlock)
        assertNotNull(elseBlock)
        assertNotNull(join)

        // Entry has no dominator
        assertNull(entry.immediateDominator)

        // Then is dominated by Entry (falls through from Entry)
        assertEquals(entry, thenBlock.immediateDominator)

        // Else is dominated by Entry (branch target from Entry)
        assertEquals(entry, elseBlock.immediateDominator)

        // Join is dominated by Entry (only Entry is on BOTH paths to Join)
        assertEquals(entry, join.immediateDominator)
    }

    @Test
    fun `if-then pattern - then dominated by entry`() {
        // Pattern:
        //   Entry (BEQ Skip)
        //     |
        //   Then
        //     |
        //   Skip
        val blocks = buildCfgWithDominators("""
            Entry: BEQ Skip
            Then: LDA #${'$'}01
            Skip: RTS
        """.trimIndent())

        val entry = blocks.find { it.label == "Entry" }
        val thenBlock = blocks.find { it.label == "Then" }
        val skip = blocks.find { it.label == "Skip" }

        assertNotNull(entry)
        assertNotNull(thenBlock)
        assertNotNull(skip)

        // Then falls through from Entry
        assertEquals(entry, thenBlock.immediateDominator)

        // Skip is reached from Entry (branch) and Then (fallthrough)
        // Only Entry dominates Skip
        assertEquals(entry, skip.immediateDominator)
    }

    // =====================================================================
    // LOOP HANDLING
    // =====================================================================

    @Test
    fun `single back-edge loop - header dominates body`() {
        // Pattern:
        //   Header <---+
        //     |        |
        //   Body  -----+
        val blocks = buildCfgWithDominators("""
            Header: LDX #${'$'}05
            Body: DEX
            BNE Header
            Exit: RTS
        """.trimIndent())

        val header = blocks.find { it.label == "Header" }
        val body = blocks.find { it.label == "Body" }
        val exit = blocks.find { it.label == "Exit" }

        assertNotNull(header)
        assertNotNull(body)
        assertNotNull(exit)

        // Body is dominated by Header
        assertEquals(header, body.immediateDominator)
    }

    @Test
    fun `nested loops - outer header dominates inner`() {
        // Pattern:
        //   Outer <--------+
        //     |            |
        //   Inner <----+   |
        //     |        |   |
        //   InnerBody -+   |
        //     |            |
        //   OuterBody -----+
        //     |
        //   Exit
        val blocks = buildCfgWithDominators("""
            Outer: LDX #${'$'}05
            Inner: LDY #${'$'}03
            InnerBody: DEY
            BNE Inner
            OuterBody: DEX
            BNE Outer
            Exit: RTS
        """.trimIndent())

        val outer = blocks.find { it.label == "Outer" }
        val inner = blocks.find { it.label == "Inner" }

        assertNotNull(outer)
        assertNotNull(inner)

        // Inner is dominated by Outer
        assertEquals(outer, inner.immediateDominator)
    }

    @Test
    fun `loop with multiple exits - header dominates all`() {
        val blocks = buildCfgWithDominators("""
            Header: LDX #${'$'}05
            Body: DEX
            BEQ EarlyExit
            BNE Header
            EarlyExit: RTS
        """.trimIndent())

        val header = blocks.find { it.label == "Header" }
        val body = blocks.find { it.label == "Body" }

        assertNotNull(header)
        assertNotNull(body)

        // Body is dominated by Header even with early exit
        assertEquals(header, body.immediateDominator)
    }

    // =====================================================================
    // MULTI-ROOT CFG
    // =====================================================================

    @Test
    fun `two entry points - super root handles multiple roots`() {
        // Two disconnected functions
        val blocks = buildCfgWithDominators("""
            Func1: LDA #${'$'}00
            RTS
            Func2: LDX #${'$'}01
            RTS
        """.trimIndent())

        val func1 = blocks.find { it.label == "Func1" }
        val func2 = blocks.find { it.label == "Func2" }

        assertNotNull(func1)
        assertNotNull(func2)

        // Both roots should have no dominator
        assertNull(func1.immediateDominator)
        assertNull(func2.immediateDominator)
    }

    @Test
    fun `unreachable blocks - handled gracefully`() {
        // Block that can't be reached from entry
        val blocks = buildCfgWithDominators("""
            Entry: JMP Skip
            Unreachable: LDA #${'$'}00
            Skip: RTS
        """.trimIndent())

        // Should complete without crashing
        assertTrue(blocks.isNotEmpty())
    }

    // =====================================================================
    // BIDIRECTIONAL CONSISTENCY
    // =====================================================================

    @Test
    fun `immediateDominator-dominates consistency`() {
        val blocks = buildCfgWithDominators("""
            First: LDA #${'$'}00
            BEQ Third
            Second: STA ${'$'}0200
            Third: RTS
        """.trimIndent())

        // For every block with an immediate dominator,
        // that block should be in the dominator's dominates list
        for (block in blocks) {
            block.immediateDominator?.let { idom ->
                assertTrue(block in idom.dominates,
                    "Block ${block.label ?: block.originalLineIndex} has idom ${idom.label ?: idom.originalLineIndex} " +
                    "but is not in its dominates list")
            }
        }
    }

    @Test
    fun `dominates list matches immediateDominator references`() {
        val blocks = buildCfgWithDominators("""
            Entry: BEQ Target
            Middle: LDA #${'$'}00
            Target: RTS
        """.trimIndent())

        // For every dominated block, its immediateDominator should point back
        for (block in blocks) {
            for (dominated in block.dominates) {
                assertEquals(block, dominated.immediateDominator,
                    "Block ${dominated.label ?: dominated.originalLineIndex} is in dominates of " +
                    "${block.label ?: block.originalLineIndex} but has different idom")
            }
        }
    }

    @Test
    fun `reset and recompute dominators`() {
        val blocks = buildCfgWithDominators("""
            First: LDA #${'$'}00
            Second: STA ${'$'}0200
            Third: RTS
        """.trimIndent())

        // Save original dominators
        val originalIdoms = blocks.map { it.immediateDominator }

        // Recompute
        blocks.dominators()

        // Should produce same results
        for ((i, block) in blocks.withIndex()) {
            assertEquals(originalIdoms[i], block.immediateDominator,
                "Dominator changed after recomputation for block ${block.label}")
        }
    }

    // =====================================================================
    // DOMINATION DEPTH
    // =====================================================================

    @Test
    fun `domination depth - linear chain`() {
        val blocks = buildCfgWithDominators("""
            First: LDA #${'$'}00
            Second: STA ${'$'}0200
            Third: LDX #${'$'}01
            Fourth: RTS
        """.trimIndent())

        // Depth increases down the chain
        assertEquals(0, blocks[0].dominationDepth)
        assertEquals(1, blocks[1].dominationDepth)
        assertEquals(2, blocks[2].dominationDepth)
        assertEquals(3, blocks[3].dominationDepth)
    }

    @Test
    fun `domination depth - diamond`() {
        val blocks = buildCfgWithDominators("""
            Entry: BEQ Else
            Then: LDA #${'$'}01
            JMP Join
            Else: LDA #${'$'}02
            Join: RTS
        """.trimIndent())

        val entry = blocks.find { it.label == "Entry" }!!
        val thenBlock = blocks.find { it.label == "Then" }!!
        val elseBlock = blocks.find { it.label == "Else" }!!
        val join = blocks.find { it.label == "Join" }!!

        // Entry is at depth 0
        assertEquals(0, entry.dominationDepth)
        // Then and Else are at depth 1 (both dominated by Entry)
        assertEquals(1, thenBlock.dominationDepth)
        assertEquals(1, elseBlock.dominationDepth)
        // Join is at depth 1 (dominated by Entry, not Then or Else)
        assertEquals(1, join.dominationDepth)
    }

    // =====================================================================
    // COMPLEX PATTERNS
    // =====================================================================

    @Test
    fun `switch-like dispatch - multiple successors`() {
        val blocks = buildCfgWithDominators("""
            Dispatch: LDA #${'$'}00
            BEQ Case0
            BNE Case1
            Case0: LDX #${'$'}00
            JMP Done
            Case1: LDX #${'$'}01
            Done: RTS
        """.trimIndent())

        // All cases should be dominated by dispatch
        for (block in blocks) {
            if (block.label != "Dispatch" && block.immediateDominator != null) {
                // Trace back to root - should go through Dispatch
                var current: AssemblyBlock? = block
                var foundDispatch = false
                while (current != null) {
                    if (current.label == "Dispatch") {
                        foundDispatch = true
                        break
                    }
                    current = current.immediateDominator
                }
                assertTrue(foundDispatch, "Block ${block.label} not dominated by Dispatch")
            }
        }
    }

    @Test
    fun `validate consistency after dominator computation`() {
        val blocks = buildCfgWithDominators("""
            Entry: BEQ Skip
            Then: LDA #${'$'}01
            Skip: BNE Entry
            Done: RTS
        """.trimIndent())

        // Should not throw
        blocks.validateAllConsistency()
    }

    // =====================================================================
    // EDGE CASES
    // =====================================================================

    @Test
    fun `empty block list - handles gracefully`() {
        val blocks = emptyList<AssemblyBlock>()
        // Should not crash
        blocks.dominators()
    }

    @Test
    fun `single block - no dominator`() {
        val blocks = buildCfgWithDominators("Entry: RTS")
        assertEquals(1, blocks.size)
        assertNull(blocks[0].immediateDominator)
    }

    @Test
    fun `self loop - block dominates itself via loop`() {
        val blocks = buildCfgWithDominators("""
            Loop: DEX
            BNE Loop
            Exit: RTS
        """.trimIndent())

        val loop = blocks.find { it.label == "Loop" }
        assertNotNull(loop)

        // Loop block still has no immediate dominator (it's the entry)
        assertNull(loop.immediateDominator)
    }
}
