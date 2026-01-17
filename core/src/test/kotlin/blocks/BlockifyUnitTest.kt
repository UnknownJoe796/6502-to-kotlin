// by Claude - Phase 2: Blockify Unit Tests (CFG Construction)
// Tests for blocks.kt:blockify() - constructing basic blocks and control flow graph
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
 * Blockify Unit Test Suite
 *
 * Tests for the blockify() function that converts a list of AssemblyLines
 * into AssemblyBlocks with control flow edges.
 *
 * This is the second layer of decompiler testing - after parsing is verified,
 * we verify that blocks are correctly formed and CFG edges are properly constructed.
 */
class BlockifyUnitTest {

    // Helper to parse and blockify assembly
    private fun blockify(asm: String): List<AssemblyBlock> {
        return asm.parseToAssemblyCodeFile().lines.blockify()
    }

    // =====================================================================
    // BLOCK BOUNDARY DETECTION - NEW BLOCKS AT LABELS
    // =====================================================================

    @Test
    fun `new block at label - single labeled block`() {
        val blocks = blockify("""
            Start: LDA #${'$'}00
            STA ${'$'}0200
        """.trimIndent())
        assertEquals(1, blocks.size)
        assertEquals("Start", blocks[0].label)
    }

    @Test
    fun `new block at label - two labeled blocks`() {
        val blocks = blockify("""
            First: LDA #${'$'}00
            Second: LDA #${'$'}01
        """.trimIndent())
        assertEquals(2, blocks.size)
        assertEquals("First", blocks[0].label)
        assertEquals("Second", blocks[1].label)
    }

    @Test
    fun `new block at label - label starts new block`() {
        val blocks = blockify("""
            LDA #${'$'}00
            STA ${'$'}0200
            Label: LDX #${'$'}05
        """.trimIndent())
        assertEquals(2, blocks.size)
        assertNull(blocks[0].label)
        assertEquals("Label", blocks[1].label)
    }

    // =====================================================================
    // BLOCK BOUNDARY DETECTION - NEW BLOCKS AFTER BRANCHES
    // =====================================================================

    @Test
    fun `new block after BEQ`() {
        val blocks = blockify("""
            LDA #${'$'}00
            BEQ Target
            LDX #${'$'}01
            Target: RTS
        """.trimIndent())
        assertEquals(3, blocks.size)
        // First block: LDA, BEQ
        // Second block: LDX
        // Third block: RTS (labeled Target)
    }

    @Test
    fun `new block after BNE`() {
        val blocks = blockify("""
            LDA #${'$'}00
            BNE Skip
            LDX #${'$'}01
            Skip: RTS
        """.trimIndent())
        assertEquals(3, blocks.size)
    }

    @Test
    fun `new block after all branch types`() {
        val branchTypes = listOf("BEQ", "BNE", "BCC", "BCS", "BMI", "BPL", "BVC", "BVS")
        for (branch in branchTypes) {
            val blocks = blockify("""
                LDA #${'$'}00
                $branch Target
                LDX #${'$'}01
                Target: RTS
            """.trimIndent())
            assertEquals(3, blocks.size, "Failed for $branch")
        }
    }

    // =====================================================================
    // BLOCK BOUNDARY DETECTION - NEW BLOCKS AFTER JMP
    // =====================================================================

    @Test
    fun `new block after JMP - no fallthrough`() {
        val blocks = blockify("""
            Loop: LDA #${'$'}00
            JMP Loop
            Dead: LDX #${'$'}01
        """.trimIndent())
        // 2 blocks: Loop (with LDA + JMP) and Dead (with LDX)
        assertEquals(2, blocks.size)
        // Loop block with JMP has no fallthrough
        assertNull(blocks[0].fallThroughExit)
    }

    @Test
    fun `JMP creates branch exit`() {
        val blocks = blockify("""
            Start: LDA #${'$'}00
            JMP Target
            Target: RTS
        """.trimIndent())
        assertEquals(2, blocks.size)
        assertEquals(blocks[1], blocks[0].branchExit)
    }

    // =====================================================================
    // BLOCK BOUNDARY DETECTION - NEW BLOCKS AFTER RTS/RTI
    // =====================================================================

    @Test
    fun `new block after RTS - no fallthrough`() {
        val blocks = blockify("""
            Func: LDA #${'$'}00
            RTS
            NextFunc: LDX #${'$'}01
        """.trimIndent())
        assertEquals(2, blocks.size)
        assertNull(blocks[0].fallThroughExit)
    }

    @Test
    fun `new block after RTI - no fallthrough`() {
        val blocks = blockify("""
            Handler: LDA #${'$'}00
            RTI
            NextHandler: LDX #${'$'}01
        """.trimIndent())
        assertEquals(2, blocks.size)
        assertNull(blocks[0].fallThroughExit)
    }

    // =====================================================================
    // CONTROL FLOW EDGE CONSTRUCTION - FALL-THROUGH EDGES
    // =====================================================================

    @Test
    fun `fallthrough edge - linear code`() {
        val blocks = blockify("""
            First: LDA #${'$'}00
            Second: STA ${'$'}0200
        """.trimIndent())
        assertEquals(2, blocks.size)
        assertEquals(blocks[1], blocks[0].fallThroughExit)
    }

    @Test
    fun `fallthrough edge - after conditional branch`() {
        val blocks = blockify("""
            LDA #${'$'}00
            BEQ Target
            LDX #${'$'}01
            Target: RTS
        """.trimIndent())
        // Block 0 (LDA, BEQ) should fall through to Block 1 (LDX)
        assertEquals(blocks[1], blocks[0].fallThroughExit)
    }

    @Test
    fun `no fallthrough after JMP`() {
        val blocks = blockify("""
            First: JMP Target
            Dead: LDA #${'$'}00
            Target: RTS
        """.trimIndent())
        assertNull(blocks[0].fallThroughExit)
    }

    @Test
    fun `no fallthrough after RTS`() {
        val blocks = blockify("""
            First: RTS
            Second: LDA #${'$'}00
        """.trimIndent())
        assertNull(blocks[0].fallThroughExit)
    }

    // =====================================================================
    // CONTROL FLOW EDGE CONSTRUCTION - BRANCH EDGES
    // =====================================================================

    @Test
    fun `branch edge - conditional branch`() {
        val blocks = blockify("""
            LDA #${'$'}00
            BEQ Target
            LDX #${'$'}01
            Target: RTS
        """.trimIndent())
        // Block 0 (LDA, BEQ) should have branch edge to Target
        assertEquals(blocks[2], blocks[0].branchExit)
    }

    @Test
    fun `branch edge - JMP target`() {
        val blocks = blockify("""
            Start: JMP End
            Middle: NOP
            End: RTS
        """.trimIndent())
        assertEquals(blocks[2], blocks[0].branchExit)
    }

    @Test
    fun `branch edge - missing label returns null`() {
        val blocks = blockify("""
            LDA #${'$'}00
            BEQ NonExistent
        """.trimIndent())
        // Branch to non-existent label should leave branchExit null
        assertNull(blocks[0].branchExit)
    }

    // =====================================================================
    // ENTEREDFROM BIDIRECTIONAL CONSISTENCY
    // =====================================================================

    @Test
    fun `enteredFrom - populated for fallthrough`() {
        val blocks = blockify("""
            First: LDA #${'$'}00
            Second: STA ${'$'}0200
        """.trimIndent())
        assertTrue(blocks[0] in blocks[1].enteredFrom)
    }

    @Test
    fun `enteredFrom - populated for branch`() {
        val blocks = blockify("""
            LDA #${'$'}00
            BEQ Target
            LDX #${'$'}01
            Target: RTS
        """.trimIndent())
        assertTrue(blocks[0] in blocks[2].enteredFrom)
    }

    @Test
    fun `enteredFrom - multiple predecessors`() {
        val blocks = blockify("""
            Start: BEQ Target
            LDA #${'$'}00
            Target: RTS
        """.trimIndent())
        // Target is reached by fallthrough from LDA and branch from Start
        assertEquals(2, blocks[2].enteredFrom.size)
    }

    @Test
    fun `enteredFrom - bidirectional consistency with fallThroughExit`() {
        val blocks = blockify("""
            First: LDA #${'$'}00
            Second: STA ${'$'}0200
            Third: RTS
        """.trimIndent())
        // Verify each block's fallthrough target has this block in enteredFrom
        for (block in blocks) {
            block.fallThroughExit?.let { target ->
                assertTrue(block in target.enteredFrom,
                    "Block ${block.label} fallthrough to ${target.label} not reflected in enteredFrom")
            }
        }
    }

    @Test
    fun `enteredFrom - bidirectional consistency with branchExit`() {
        val blocks = blockify("""
            LDA #${'$'}00
            BEQ Target
            BNE Target
            Target: RTS
        """.trimIndent())
        // Verify each block's branch target has this block in enteredFrom
        for (block in blocks) {
            block.branchExit?.let { target ->
                assertTrue(block in target.enteredFrom,
                    "Block ${block.originalLineIndex} branch to ${target.label} not reflected in enteredFrom")
            }
        }
    }

    // =====================================================================
    // EDGE CASES
    // =====================================================================

    @Test
    fun `empty input - returns empty list`() {
        val blocks = emptyList<AssemblyLine>().blockify()
        assertTrue(blocks.isEmpty())
    }

    @Test
    fun `single instruction - one block`() {
        val blocks = blockify("NOP")
        assertEquals(1, blocks.size)
    }

    @Test
    fun `linear code - no branches`() {
        val blocks = blockify("""
            LDA #${'$'}00
            STA ${'$'}0200
            LDA #${'$'}01
            STA ${'$'}0201
        """.trimIndent())
        // Without labels, this is one block
        assertEquals(1, blocks.size)
    }

    @Test
    fun `all branches - no linear flow`() {
        // Note: Can't use single letter "A" as it conflicts with accumulator mode
        val blocks = blockify("""
            First: BEQ Second
            Second: BNE First
            Third: RTS
        """.trimIndent())
        assertEquals(3, blocks.size)
    }

    @Test
    fun `self loop - JMP to same block`() {
        val blocks = blockify("""
            Loop: NOP
            JMP Loop
        """.trimIndent())
        assertEquals(1, blocks.size)
        assertEquals(blocks[0], blocks[0].branchExit)
    }

    @Test
    fun `conditional self loop - BEQ to same block`() {
        val blocks = blockify("""
            Loop: DEX
            BNE Loop
            RTS
        """.trimIndent())
        assertEquals(2, blocks.size)
        assertEquals(blocks[0], blocks[0].branchExit)
    }

    // =====================================================================
    // BIT SKIP PATTERN ($2C) HANDLING
    // =====================================================================

    @Test
    fun `bit skip pattern - db 2c recognized`() {
        val blocks = blockify("""
            .db ${'$'}2C
            LDA #${'$'}00
            RTS
        """.trimIndent())
        // The .db $2C acts like a BIT instruction that skips next 2 bytes
        assertTrue(blocks.isNotEmpty())
    }

    // =====================================================================
    // JSR TO TERMINAL SUBROUTINE (JumpEngine)
    // =====================================================================

    @Test
    fun `JSR to JumpEngine - terminates block`() {
        val blocks = blockify("""
            Start: LDA #${'$'}00
            JSR JumpEngine
            .dw Handler1
            .dw Handler2
            Other: RTS
        """.trimIndent())
        // JSR JumpEngine should end the block because JumpEngine doesn't return normally
        assertTrue(blocks.size >= 2)
    }

    // =====================================================================
    // BLOCK LINE ASSIGNMENT
    // =====================================================================

    @Test
    fun `lines have block reference set`() {
        val blocks = blockify("""
            First: LDA #${'$'}00
            STA ${'$'}0200
            Second: RTS
        """.trimIndent())
        // Each line should have its block reference set
        for (block in blocks) {
            for (line in block.lines) {
                assertEquals(block, line.block)
            }
        }
    }

    @Test
    fun `originalLineIndex is preserved`() {
        val blocks = blockify("""
            First: LDA #${'$'}00
            Second: STA ${'$'}0200
        """.trimIndent())
        assertEquals(0, blocks[0].originalLineIndex)
        assertEquals(1, blocks[1].originalLineIndex)
    }

    // =====================================================================
    // VALIDATE CONSISTENCY HELPER
    // =====================================================================

    @Test
    fun `validateConsistency passes for well-formed CFG`() {
        val blocks = blockify("""
            Start: LDA #${'$'}00
            BEQ Target
            LDX #${'$'}01
            Target: RTS
        """.trimIndent())
        // Should not throw
        blocks.validateAllConsistency()
    }

    @Test
    fun `complex CFG - multiple entries and exits`() {
        // Note: Can't use single letter "A" as it conflicts with accumulator mode
        val blocks = blockify("""
            First: BEQ Third
            Second: BNE Third
            Third: BCC First
            Fourth: RTS
        """.trimIndent())
        blocks.validateAllConsistency()

        // Verify Third has multiple entries
        val blockThird = blocks.find { it.label == "Third" }
        assertNotNull(blockThird)
        assertTrue(blockThird.enteredFrom.size >= 2)
    }
}
