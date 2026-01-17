// by Claude - Phase 2: Basic Block & CFG Verification
// Tests that verify the control flow graph is correctly constructed from assembly
package com.ivieleague.decompiler6502tokotlin.decompiler

import com.ivieleague.decompiler6502tokotlin.hand.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * CFG Correctness Test Suite
 *
 * Tests that the blockify() function correctly constructs basic blocks and
 * control flow edges for canonical assembly patterns. Each test verifies:
 * - Correct block boundaries (blocks split at labels, branches, jumps, returns)
 * - Correct fallThroughExit edges (next sequential block)
 * - Correct branchExit edges (branch/jump targets)
 * - Correct enteredFrom lists (predecessors are correctly tracked)
 *
 * These tests use standalone assembly snippets and don't depend on SMB.
 */
class CFGCorrectnessTest {

    // =====================================================================
    // HELPER: Parse assembly and blockify
    // =====================================================================

    private fun parseAndBlockify(asm: String): List<AssemblyBlock> {
        return asm.trimIndent().parseToAssemblyCodeFile().lines.blockify()
    }

    private fun List<AssemblyBlock>.getBlock(label: String): AssemblyBlock {
        return firstOrNull { it.label == label }
            ?: error("Block with label '$label' not found. Available: ${map { it.label }}")
    }

    private fun List<AssemblyBlock>.getBlockAt(index: Int): AssemblyBlock {
        return this[index]
    }

    // =====================================================================
    // PATTERN 1: LINEAR CODE (Single Block)
    // No branches, just sequential instructions ending with RTS
    // =====================================================================

    @Test
    fun `linear code - single block`() {
        val blocks = parseAndBlockify("""
            Start:
                LDA #${'$'}42
                STA ${'$'}00
                RTS
        """)

        assertEquals(1, blocks.size, "Linear code should have 1 block")

        val block = blocks.getBlock("Start")
        assertNull(block.fallThroughExit, "Single block should have no fall-through")
        assertNull(block.branchExit, "Single block should have no branch")
        assertTrue(block.enteredFrom.isEmpty(), "Entry block should have no predecessors")
    }

    @Test
    fun `linear code - multiple instructions`() {
        val blocks = parseAndBlockify("""
            Multiply:
                LDA ${'$'}10
                ASL
                ASL
                ASL
                ASL
                STA ${'$'}20
                RTS
        """)

        assertEquals(1, blocks.size, "All instructions in one block")

        val block = blocks.getBlock("Multiply")
        // Count actual instructions (not comments or blank lines)
        val instrCount = block.lines.count { it.instruction != null }
        assertEquals(7, instrCount, "Should have 7 instructions")
    }

    // =====================================================================
    // PATTERN 2: FORWARD BRANCH (If-Then)
    // BEQ/BNE forward to skip some code
    // =====================================================================

    @Test
    fun `forward branch - simple if-then (BEQ skip)`() {
        val blocks = parseAndBlockify("""
            Start:
                LDA ${'$'}00
                BEQ Skip
                INC ${'$'}01
            Skip:
                RTS
        """)

        assertEquals(3, blocks.size, "if-then should have 3 blocks")

        val startBlock = blocks.getBlock("Start")
        val thenBlock = blocks[1]  // The INC block (no label)
        val skipBlock = blocks.getBlock("Skip")

        // Start block: falls through to then, branches to skip
        assertEquals(thenBlock, startBlock.fallThroughExit, "Start falls to INC block")
        assertEquals(skipBlock, startBlock.branchExit, "Start branches to Skip")

        // Then block: falls through to skip
        assertEquals(skipBlock, thenBlock.fallThroughExit, "INC block falls to Skip")
        assertNull(thenBlock.branchExit, "INC block has no branch")

        // Skip block: return, no exits
        assertNull(skipBlock.fallThroughExit, "Return block has no fall-through")
        assertNull(skipBlock.branchExit, "Return block has no branch")

        // enteredFrom checks
        assertTrue(startBlock.enteredFrom.isEmpty(), "Entry has no predecessors")
        assertEquals(listOf(startBlock), thenBlock.enteredFrom, "INC entered from Start")
        assertEquals(setOf(startBlock, thenBlock), skipBlock.enteredFrom.toSet(), "Skip entered from Start and INC")
    }

    @Test
    fun `forward branch - if-then with BNE`() {
        val blocks = parseAndBlockify("""
            Check:
                LDX ${'$'}10
                BNE Found
                LDA #${'$'}00
                RTS
            Found:
                LDA #${'$'}FF
                RTS
        """)

        assertEquals(3, blocks.size, "Should have 3 blocks")

        val checkBlock = blocks.getBlock("Check")
        val notFoundBlock = blocks[1]  // LDA #$00, RTS
        val foundBlock = blocks.getBlock("Found")

        // Check branches to Found on BNE
        assertEquals(notFoundBlock, checkBlock.fallThroughExit)
        assertEquals(foundBlock, checkBlock.branchExit)

        // notFoundBlock ends with RTS, no fall-through
        assertNull(notFoundBlock.fallThroughExit, "RTS block has no fall-through")

        // Found also ends with RTS
        assertNull(foundBlock.fallThroughExit, "RTS block has no fall-through")
    }

    // =====================================================================
    // PATTERN 3: IF-THEN-ELSE (Branch + JMP)
    // =====================================================================

    @Test
    fun `if-then-else - BEQ with JMP merge`() {
        val blocks = parseAndBlockify("""
            Start:
                LDA ${'$'}00
                BEQ DoElse
                LDA #${'$'}01
                JMP Merge
            DoElse:
                LDA #${'$'}02
            Merge:
                STA ${'$'}10
                RTS
        """)

        assertEquals(4, blocks.size, "if-then-else should have 4 blocks")

        val startBlock = blocks.getBlock("Start")
        val thenBlock = blocks[1]  // LDA #$01, JMP
        val elseBlock = blocks.getBlock("DoElse")
        val mergeBlock = blocks.getBlock("Merge")

        // Start: fall to then, branch to else
        assertEquals(thenBlock, startBlock.fallThroughExit)
        assertEquals(elseBlock, startBlock.branchExit)

        // Then: JMP to Merge (branchExit), no fall-through
        assertNull(thenBlock.fallThroughExit, "JMP block has no fall-through")
        assertEquals(mergeBlock, thenBlock.branchExit, "JMP targets Merge")

        // Else: falls through to Merge
        assertEquals(mergeBlock, elseBlock.fallThroughExit)
        assertNull(elseBlock.branchExit)

        // Merge: entered from both then (via JMP) and else (via fall-through)
        assertEquals(setOf(thenBlock, elseBlock), mergeBlock.enteredFrom.toSet())
    }

    // =====================================================================
    // PATTERN 4: BACKWARD BRANCH (Simple Loop)
    // =====================================================================

    @Test
    fun `backward branch - simple loop with BNE`() {
        val blocks = parseAndBlockify("""
            Start:
                LDX #${'$'}10
            Loop:
                DEX
                BNE Loop
                RTS
        """)

        assertEquals(3, blocks.size, "Loop should have 3 blocks")

        val startBlock = blocks.getBlock("Start")
        val loopBlock = blocks.getBlock("Loop")
        val returnBlock = blocks[2]  // RTS block

        // Start falls to Loop
        assertEquals(loopBlock, startBlock.fallThroughExit)
        assertNull(startBlock.branchExit)

        // Loop: falls to return, branches back to Loop (back edge!)
        assertEquals(returnBlock, loopBlock.fallThroughExit)
        assertEquals(loopBlock, loopBlock.branchExit, "Back edge to Loop")

        // Loop is entered from Start and from itself (back edge)
        assertEquals(setOf(startBlock, loopBlock), loopBlock.enteredFrom.toSet())
    }

    @Test
    fun `backward branch - while loop with condition at top`() {
        val blocks = parseAndBlockify("""
            Start:
                LDX #${'$'}00
            WhileLoop:
                CPX #${'$'}10
                BEQ Done
                INX
                JMP WhileLoop
            Done:
                RTS
        """)

        assertEquals(4, blocks.size, "While loop should have 4 blocks")

        val startBlock = blocks.getBlock("Start")
        val condBlock = blocks.getBlock("WhileLoop")
        val bodyBlock = blocks[2]  // INX, JMP
        val doneBlock = blocks.getBlock("Done")

        // WhileLoop: condition check
        assertEquals(bodyBlock, condBlock.fallThroughExit)  // falls to body
        assertEquals(doneBlock, condBlock.branchExit)  // BEQ Done

        // Body: JMP back to WhileLoop
        assertNull(bodyBlock.fallThroughExit, "JMP has no fall-through")
        assertEquals(condBlock, bodyBlock.branchExit, "JMP WhileLoop")

        // WhileLoop entered from Start and from body JMP
        assertEquals(setOf(startBlock, bodyBlock), condBlock.enteredFrom.toSet())
    }

    @Test
    fun `backward branch - do-while loop with condition at bottom`() {
        val blocks = parseAndBlockify("""
            Start:
                LDA #${'$'}00
            DoLoop:
                CLC
                ADC #${'$'}01
                CMP #${'$'}10
                BNE DoLoop
                RTS
        """)

        assertEquals(3, blocks.size, "Do-while should have 3 blocks")

        val startBlock = blocks.getBlock("Start")
        val loopBlock = blocks.getBlock("DoLoop")
        val returnBlock = blocks[2]

        // DoLoop branches back on BNE, falls to return
        assertEquals(returnBlock, loopBlock.fallThroughExit)
        assertEquals(loopBlock, loopBlock.branchExit, "Back edge to DoLoop")

        // DoLoop entered from Start and from itself
        assertEquals(setOf(startBlock, loopBlock), loopBlock.enteredFrom.toSet())
    }

    // =====================================================================
    // PATTERN 5: NESTED BRANCHES
    // =====================================================================

    @Test
    fun `nested branches - if inside if`() {
        val blocks = parseAndBlockify("""
            Start:
                LDA ${'$'}00
                BEQ Skip1
                LDA ${'$'}01
                BEQ Skip2
                INC ${'$'}10
            Skip2:
                INC ${'$'}11
            Skip1:
                RTS
        """)

        assertEquals(5, blocks.size, "Nested if should have 5 blocks")

        val startBlock = blocks.getBlock("Start")
        val innerIfBlock = blocks[1]  // LDA $01, BEQ Skip2
        val thenBlock = blocks[2]  // INC $10
        val skip2Block = blocks.getBlock("Skip2")
        val skip1Block = blocks.getBlock("Skip1")

        // Start branches to Skip1
        assertEquals(skip1Block, startBlock.branchExit)
        assertEquals(innerIfBlock, startBlock.fallThroughExit)

        // InnerIf branches to Skip2
        assertEquals(skip2Block, innerIfBlock.branchExit)
        assertEquals(thenBlock, innerIfBlock.fallThroughExit)

        // Skip1 is the merge point - should be entered from multiple paths
        assertTrue(skip1Block.enteredFrom.size >= 2, "Skip1 should have multiple predecessors")
    }

    // =====================================================================
    // PATTERN 6: MULTIPLE EXITS (Multiple RTS)
    // =====================================================================

    @Test
    fun `multiple exits - early return`() {
        val blocks = parseAndBlockify("""
            Start:
                LDA ${'$'}00
                BEQ EarlyReturn
                LDA ${'$'}01
                STA ${'$'}02
                RTS
            EarlyReturn:
                LDA #${'$'}FF
                RTS
        """)

        assertEquals(3, blocks.size, "Early return should have 3 blocks")

        val startBlock = blocks.getBlock("Start")
        val normalBlock = blocks[1]  // normal path
        val earlyBlock = blocks.getBlock("EarlyReturn")

        // Both normal and early blocks end with RTS, no exits
        assertNull(normalBlock.fallThroughExit)
        assertNull(earlyBlock.fallThroughExit)

        // Start can go to either path
        assertEquals(normalBlock, startBlock.fallThroughExit)
        assertEquals(earlyBlock, startBlock.branchExit)
    }

    // =====================================================================
    // PATTERN 7: FALL-THROUGH TO LABEL (Bug #3 Pattern)
    // Critical case where code falls through from one labeled block to another
    // =====================================================================

    @Test
    fun `fall-through to label - sequential labels`() {
        val blocks = parseAndBlockify("""
            First:
                LDA #${'$'}01
            Second:
                STA ${'$'}00
                RTS
        """)

        assertEquals(2, blocks.size, "Two labels should create two blocks")

        val firstBlock = blocks.getBlock("First")
        val secondBlock = blocks.getBlock("Second")

        // First falls through to Second
        assertEquals(secondBlock, firstBlock.fallThroughExit)

        // Second entered from First
        assertEquals(listOf(firstBlock), secondBlock.enteredFrom)
    }

    @Test
    fun `fall-through to label - branch target and fall-through merge`() {
        val blocks = parseAndBlockify("""
            Start:
                LDA ${'$'}00
                BEQ Target
                LDA ${'$'}01
            Target:
                STA ${'$'}02
                RTS
        """)

        assertEquals(3, blocks.size, "Should have 3 blocks")

        val startBlock = blocks.getBlock("Start")
        val middleBlock = blocks[1]  // LDA $01
        val targetBlock = blocks.getBlock("Target")

        // Start branches to Target
        assertEquals(targetBlock, startBlock.branchExit)

        // Middle falls through to Target
        assertEquals(targetBlock, middleBlock.fallThroughExit)

        // Target is entered from both Start (branch) and middle (fall-through)
        assertEquals(setOf(startBlock, middleBlock), targetBlock.enteredFrom.toSet())
    }

    // =====================================================================
    // PATTERN 8: JMP AS UNCONDITIONAL BRANCH
    // =====================================================================

    @Test
    fun `JMP creates branch edge not fall-through`() {
        val blocks = parseAndBlockify("""
            Start:
                JMP Target
            Unreachable:
                LDA #${'$'}00
            Target:
                RTS
        """)

        assertEquals(3, blocks.size)

        val startBlock = blocks.getBlock("Start")
        val unreachableBlock = blocks.getBlock("Unreachable")
        val targetBlock = blocks.getBlock("Target")

        // Start has JMP - no fall-through, branch to Target
        assertNull(startBlock.fallThroughExit, "JMP has no fall-through")
        assertEquals(targetBlock, startBlock.branchExit)

        // Unreachable is not entered from Start
        assertTrue(startBlock !in unreachableBlock.enteredFrom)
    }

    // =====================================================================
    // PATTERN 9: COMPLEX LOOP WITH BREAK
    // =====================================================================

    @Test
    fun `loop with break - BEQ exits loop`() {
        val blocks = parseAndBlockify("""
            Start:
                LDX #${'$'}10
            Loop:
                LDA ${'$'}00,X
                BEQ LoopEnd
                STA ${'$'}10,X
                DEX
                BNE Loop
            LoopEnd:
                RTS
        """)

        assertEquals(4, blocks.size)

        val loopBlock = blocks.getBlock("Loop")
        val bodyBlock = blocks[2]  // STA, DEX, BNE
        val endBlock = blocks.getBlock("LoopEnd")

        // Loop block: BEQ to LoopEnd (break), fall to body
        assertEquals(bodyBlock, loopBlock.fallThroughExit)
        assertEquals(endBlock, loopBlock.branchExit)

        // Body: BNE back to Loop (continue), fall to LoopEnd (after loop)
        assertEquals(endBlock, bodyBlock.fallThroughExit)
        assertEquals(loopBlock, bodyBlock.branchExit)

        // LoopEnd entered from Loop (break) and body (loop exit)
        assertEquals(setOf(loopBlock, bodyBlock), endBlock.enteredFrom.toSet())
    }

    // =====================================================================
    // BIDIRECTIONAL CONSISTENCY CHECKS
    // =====================================================================

    @Test
    fun `bidirectional consistency - all edges have inverse`() {
        // Note: Avoid using 'A', 'X', 'Y' as label names since they conflict with register names
        val blocks = parseAndBlockify("""
            BlockA:
                LDA ${'$'}00
                BEQ BlockC
            BlockB:
                INC ${'$'}01
            BlockC:
                LDA ${'$'}02
                BNE BlockA
                RTS
        """)

        // Validate that all edges are bidirectionally consistent
        blocks.validateAllConsistency()

        // Additional explicit checks
        for (block in blocks) {
            val blockName = block.label ?: "@${block.originalLineIndex}"

            // Check fallThroughExit -> enteredFrom
            block.fallThroughExit?.let { target ->
                val targetName = target.label ?: "@${target.originalLineIndex}"
                assertTrue(block in target.enteredFrom,
                    "$blockName fallsThrough to $targetName but not in enteredFrom")
            }

            // Check branchExit -> enteredFrom
            block.branchExit?.let { target ->
                val targetName = target.label ?: "@${target.originalLineIndex}"
                assertTrue(block in target.enteredFrom,
                    "$blockName branches to $targetName but not in enteredFrom")
            }

            // Check enteredFrom -> has edge pointing here
            for (pred in block.enteredFrom) {
                val predName = pred.label ?: "@${pred.originalLineIndex}"
                assertTrue(pred.fallThroughExit == block || pred.branchExit == block,
                    "$predName in enteredFrom of $blockName but no edge")
            }
        }
    }
}
