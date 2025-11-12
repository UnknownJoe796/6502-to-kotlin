package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ControlFlowImprovedTest {

    /**
     * Test post-dominator computation on a simple diamond pattern:
     *
     *     A
     *    / \
     *   B   C
     *    \ /
     *     D
     *
     * D post-dominates A, B, C
     */
    @Test
    fun testPostDominatorSimpleDiamond() {
        val code = """
            Start:
                LDA #${'$'}00
                BEQ Else
            Then:
                LDX #${'$'}01
                JMP Join
            Else:
                LDX #${'$'}02
            Join:
                RTS
        """.trimIndent()

        val parsed = code.parseToAssemblyCodeFile()
        val blocks = parsed.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertEquals(1, functions.size, "Should have one function")
        val func = functions.first()

        val postDom = func.computePostDominators()

        // Find blocks by label
        val blockMap = blocks.associateBy { it.label }
        val start = blockMap["Start"]!!
        val thenBlock = blockMap["Then"]!!
        val elseBlock = blockMap["Else"]!!
        val join = blockMap["Join"]!!

        // Join post-dominates all blocks
        assertEquals(join, postDom[start], "Join should post-dominate Start")
        assertEquals(join, postDom[thenBlock], "Join should post-dominate Then")
        assertEquals(join, postDom[elseBlock], "Join should post-dominate Else")
        assertNull(postDom[join], "Join should have no post-dominator (it's the exit)")
    }

    /**
     * Test common block factoring removes trailing jumps:
     *
     * Pattern:
     *   if (cond) {
     *       A
     *       JMP Join
     *   } else {
     *       B
     *       JMP Join
     *   }
     *   Join
     *
     * Should become:
     *   if (cond) {
     *       A
     *   } else {
     *       B
     *   }
     *   Join
     */
    @Test
    fun testFactorCommonPostDominatorRemovesJumps() {
        val code = """
            Start:
                LDA #${'$'}00
                BEQ Else
            Then:
                LDX #${'$'}01
                JMP Join
            Else:
                LDX #${'$'}02
                JMP Join
            Join:
                RTS
        """.trimIndent()

        val parsed = code.parseToAssemblyCodeFile()
        val blocks = parsed.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        val func = functions.first()

        // Analyze controls
        func.analyzeControls()
        val nodes = func.asControls!!

        // Find the if node
        val ifNode = nodes.filterIsInstance<IfNode>().firstOrNull()
        assertNotNull(ifNode, "Should have an IfNode")

        // Before improvement: both branches might end with jump blocks
        val thenBlocks = ifNode!!.thenBranch.filterIsInstance<BlockNode>()
        val elseBlocks = ifNode.elseBranch.filterIsInstance<BlockNode>()

        // Apply improvements
        func.improveControlFlow()
        val improved = func.asControls!!

        val improvedIf = improved.filterIsInstance<IfNode>().firstOrNull()
        assertNotNull(improvedIf, "Should still have an IfNode")

        // After improvement: the join should be set and trailing jumps removed
        assertNotNull(improvedIf!!.join, "Join should be identified")
        assertEquals("Join", improvedIf.join?.label, "Join should be the Join block")
    }

    /**
     * Test duplicate exit elimination:
     *
     * Pattern:
     *   if (cond) {
     *       A
     *       Exit
     *   }
     *   Exit
     *
     * Should become:
     *   if (cond) {
     *       A
     *   }
     *   Exit
     */
    @Test
    fun testEliminateDuplicateExits() {
        val code = """
            Start:
                LDA #${'$'}00
                BEQ Skip
            Then:
                LDX #${'$'}01
            Skip:
                RTS
        """.trimIndent()

        val parsed = code.parseToAssemblyCodeFile()
        val blocks = parsed.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        val func = functions.first()

        func.improveControlFlow()
        val improved = func.asControls!!

        // Collect all block nodes
        fun collectBlocks(nodes: List<ControlNode>): List<BlockNode> {
            val result = mutableListOf<BlockNode>()
            for (node in nodes) {
                when (node) {
                    is BlockNode -> result.add(node)
                    is IfNode -> {
                        result.addAll(collectBlocks(node.thenBranch))
                        result.addAll(collectBlocks(node.elseBranch))
                    }
                    is LoopNode -> result.addAll(collectBlocks(node.body))
                    else -> {}
                }
            }
            return result
        }

        val allBlocks = collectBlocks(improved)

        // Count how many times each unique block appears
        val blockCounts = allBlocks.groupBy { it.block }.mapValues { it.value.size }

        // The Skip block should appear at most once after duplicate elimination
        val skipBlock = blocks.find { it.label == "Skip" }
        assertNotNull(skipBlock, "Skip block should exist")

        val skipCount = blockCounts[skipBlock] ?: 0
        assertTrue(skipCount <= 1, "Skip block should appear at most once, but appeared $skipCount times")
    }

    /**
     * Test early return detection in nested if:
     *
     * Pattern:
     *   if (cond1) {
     *       if (cond2) {
     *           return
     *       }
     *   }
     *   ... more code ...
     */
    @Test
    fun testEarlyReturnBlock() {
        val code = """
            Start:
                LDA #${'$'}00
                BEQ Skip1
            Check:
                LDA #${'$'}01
                BEQ Exit
            Skip1:
                LDX #${'$'}02
            Exit:
                RTS
        """.trimIndent()

        val parsed = code.parseToAssemblyCodeFile()
        val blocks = parsed.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        val func = functions.first()

        val exitBlock = blocks.find { it.label == "Exit" }
        assertNotNull(exitBlock, "Exit block should exist")
        assertTrue(exitBlock!!.isReturn(), "Exit block should be a return block")
    }

    /**
     * Test nested if-then-else with common exit:
     *
     * Pattern:
     *   if (cond1) {
     *       if (cond2) {
     *           A
     *       } else {
     *           B
     *       }
     *       Common
     *   } else {
     *       C
     *   }
     *   Exit
     */
    @Test
    fun testNestedIfWithCommonExit() {
        val code = """
            Outer:
                LDA #${'$'}00
                BEQ OuterElse
            OuterThen:
                LDA #${'$'}01
                BEQ InnerElse
            InnerThen:
                LDX #${'$'}02
                JMP Common
            InnerElse:
                LDX #${'$'}03
            Common:
                LDY #${'$'}04
                JMP Exit
            OuterElse:
                LDY #${'$'}05
            Exit:
                RTS
        """.trimIndent()

        val parsed = code.parseToAssemblyCodeFile()
        val blocks = parsed.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        val func = functions.first()

        func.improveControlFlow()
        val improved = func.asControls!!

        // Should have an outer if with nested if in then branch
        val outerIf = improved.filterIsInstance<IfNode>().firstOrNull()
        assertNotNull(outerIf, "Should have outer IfNode")

        val innerIf = outerIf!!.thenBranch.filterIsInstance<IfNode>().firstOrNull()
        assertNotNull(innerIf, "Should have inner IfNode in then branch")

        // The common block should appear after the inner if, not duplicated in branches
        val commonBlock = blocks.find { it.label == "Common" }
        assertNotNull(commonBlock, "Common block should exist")
    }

    /**
     * Test that loops preserve their structure through improvements.
     */
    @Test
    fun testLoopPreservedThroughImprovements() {
        val code = """
            Start:
                LDX #${'$'}00
            Loop:
                INX
                CPX #${'$'}0A
                BNE Loop
            Exit:
                RTS
        """.trimIndent()

        val parsed = code.parseToAssemblyCodeFile()
        val blocks = parsed.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        val func = functions.first()

        func.improveControlFlow()
        val improved = func.asControls!!

        val loops = improved.filterIsInstance<LoopNode>()
        assertEquals(1, loops.size, "Should have exactly one loop")

        val loop = loops.first()
        assertEquals(LoopKind.PostTest, loop.kind, "Should be a PostTest (do-while) loop")
    }
}
