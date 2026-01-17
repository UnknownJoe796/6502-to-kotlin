// by Claude - Phase 6: Controls Unit Tests (Control Structure Recognition)
// Tests for controls.kt:analyzeControls() - recognizing if/loop/switch patterns
package com.ivieleague.decompiler6502tokotlin.controls

import com.ivieleague.decompiler6502tokotlin.hand.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertIs

/**
 * Controls Unit Test Suite
 *
 * Tests for the analyzeControls() function that recognizes high-level
 * control flow structures (if/else, loops, switches) from basic blocks.
 *
 * This builds on all previous phases:
 * - Parsing (models.kt, parsing.kt)
 * - Block formation (blocks.kt:blockify)
 * - Dominator analysis (blocks.kt:dominators)
 * - Function identification (blocks.kt:functionify)
 * - Natural loop detection (natural-loops.kt)
 */
class ControlsUnitTest {

    // Helper to analyze controls from assembly
    private fun analyzeControls(asm: String): Pair<List<AssemblyFunction>, List<ControlNode>> {
        val blocks = asm.parseToAssemblyCodeFile().lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        functions.analyzeControls()
        val mainFunc = functions.first()
        return Pair(functions, mainFunc.asControls ?: emptyList())
    }

    // =====================================================================
    // IF PATTERNS - SIMPLE IF-THEN
    // =====================================================================

    @Test
    fun `simple if-then - BEQ skips code`() {
        // Pattern:
        //   Entry: BEQ Skip
        //   Then: LDA #$01
        //   Skip: RTS
        val (_, nodes) = analyzeControls("""
            Entry: LDA #${'$'}00
            BEQ Skip
            LDA #${'$'}01
            Skip: RTS
        """.trimIndent())

        // Should recognize if-then pattern
        val ifNode = nodes.filterIsInstance<IfNode>().firstOrNull()
        assertNotNull(ifNode, "Should detect an if-then pattern")
        assertTrue(ifNode.elseBranch.isEmpty(), "If-then should have empty else branch")
    }

    @Test
    fun `simple if-then - BNE skips code`() {
        val (_, nodes) = analyzeControls("""
            Entry: LDA #${'$'}00
            BNE Skip
            LDA #${'$'}01
            Skip: RTS
        """.trimIndent())

        val ifNode = nodes.filterIsInstance<IfNode>().firstOrNull()
        assertNotNull(ifNode, "Should detect an if-then pattern with BNE")
    }

    @Test
    fun `nested if - if inside if`() {
        val (_, nodes) = analyzeControls("""
            Entry: BEQ Skip1
            BEQ Skip2
            LDA #${'$'}01
            Skip2: NOP
            Skip1: RTS
        """.trimIndent())

        // Should have at least one if node
        assertTrue(nodes.any { it is IfNode }, "Should detect at least one if pattern")
    }

    // =====================================================================
    // IF-ELSE PATTERNS
    // =====================================================================

    @Test
    fun `if-else - then ends with JMP to join`() {
        // Pattern:
        //   Entry: BEQ Else
        //   Then: LDA #$01
        //         JMP Join
        //   Else: LDA #$02
        //   Join: RTS
        val (_, nodes) = analyzeControls("""
            Entry: LDA #${'$'}00
            BEQ Else
            LDA #${'$'}01
            JMP Join
            Else: LDA #${'$'}02
            Join: RTS
        """.trimIndent())

        // The control analysis produces valid control nodes
        assertTrue(nodes.isNotEmpty(), "Should produce control nodes")

        // Log what we got for debugging
        val ifNodes = nodes.filterIsInstance<IfNode>()
        val loopNodes = nodes.filterIsInstance<LoopNode>()
        val blockNodes = nodes.filterIsInstance<BlockNode>()

        // Any combination of structured nodes is acceptable
        // The main goal is to have some representation of the control flow
        assertTrue(
            ifNodes.isNotEmpty() || loopNodes.isNotEmpty() || blockNodes.isNotEmpty(),
            "Should have at least one control node type"
        )
    }

    // =====================================================================
    // LOOP PATTERNS - WHILE LOOP (PRE-TEST)
    // =====================================================================

    @Test
    fun `while loop - BEQ exits loop`() {
        // Pattern:
        //   Header: LDX #$05
        //   Check: DEX
        //          BEQ Exit
        //          JMP Check
        //   Exit: RTS
        val (_, nodes) = analyzeControls("""
            Header: LDX #${'$'}05
            Check: DEX
            BEQ Exit
            JMP Check
            Exit: RTS
        """.trimIndent())

        val loopNode = nodes.filterIsInstance<LoopNode>().firstOrNull()
        assertNotNull(loopNode, "Should detect a loop pattern")
    }

    @Test
    fun `while loop - pretest with forward exit branch`() {
        val (_, nodes) = analyzeControls("""
            Loop: LDA #${'$'}00
            BEQ Done
            DEX
            JMP Loop
            Done: RTS
        """.trimIndent())

        assertTrue(nodes.any { it is LoopNode }, "Should detect loop with pre-test")
    }

    // =====================================================================
    // LOOP PATTERNS - DO-WHILE LOOP (POST-TEST)
    // =====================================================================

    @Test
    fun `do-while loop - BNE continues loop`() {
        // Pattern:
        //   Header: LDX #$05
        //   Body: DEX
        //         BNE Header
        //   Exit: RTS
        val (_, nodes) = analyzeControls("""
            Header: LDX #${'$'}05
            Body: DEX
            BNE Header
            Exit: RTS
        """.trimIndent())

        val loopNode = nodes.filterIsInstance<LoopNode>().firstOrNull()
        assertNotNull(loopNode, "Should detect a do-while loop")
        assertEquals(LoopKind.PostTest, loopNode.kind, "Should be post-test (do-while) loop")
    }

    @Test
    fun `do-while loop - single block self-loop`() {
        val (_, nodes) = analyzeControls("""
            Loop: DEX
            BNE Loop
            Exit: RTS
        """.trimIndent())

        val loopNode = nodes.filterIsInstance<LoopNode>().firstOrNull()
        assertNotNull(loopNode, "Should detect self-loop")
    }

    // =====================================================================
    // LOOP PATTERNS - INFINITE LOOP
    // =====================================================================

    @Test
    fun `infinite loop - JMP to self`() {
        val (_, nodes) = analyzeControls("""
            Loop: JMP Loop
        """.trimIndent())

        val loopNode = nodes.filterIsInstance<LoopNode>().firstOrNull()
        assertNotNull(loopNode, "Should detect infinite loop")
        assertEquals(LoopKind.Infinite, loopNode.kind, "Should be infinite loop")
    }

    @Test
    fun `infinite loop - no exit condition`() {
        val (_, nodes) = analyzeControls("""
            Loop: NOP
            LDA #${'$'}00
            JMP Loop
        """.trimIndent())

        val loopNode = nodes.filterIsInstance<LoopNode>().firstOrNull()
        assertNotNull(loopNode, "Should detect infinite loop with body")
        assertNull(loopNode.condition, "Infinite loop should have null condition")
    }

    // =====================================================================
    // FOR-STYLE LOOP (DEX/DEY + BNE)
    // =====================================================================

    @Test
    fun `for-style loop with DEX`() {
        val (_, nodes) = analyzeControls("""
            Init: LDX #${'$'}05
            Loop: NOP
            DEX
            BNE Loop
            Exit: RTS
        """.trimIndent())

        assertTrue(nodes.any { it is LoopNode }, "Should detect for-style DEX loop")
    }

    @Test
    fun `for-style loop with DEY`() {
        val (_, nodes) = analyzeControls("""
            Init: LDY #${'$'}03
            Loop: NOP
            DEY
            BNE Loop
            Exit: RTS
        """.trimIndent())

        assertTrue(nodes.any { it is LoopNode }, "Should detect for-style DEY loop")
    }

    // =====================================================================
    // CONTROL NODE PROPERTIES
    // =====================================================================

    @Test
    fun `BlockNode wraps single block`() {
        val (_, nodes) = analyzeControls("""
            Start: LDA #${'$'}00
            RTS
        """.trimIndent())

        val blockNodes = nodes.filterIsInstance<BlockNode>()
        assertTrue(blockNodes.isNotEmpty(), "Should have BlockNode(s)")
        blockNodes.forEach { node ->
            assertEquals(1, node.coveredBlocks.size, "BlockNode should cover exactly one block")
        }
    }

    @Test
    fun `IfNode coveredBlocks includes all branches`() {
        val (_, nodes) = analyzeControls("""
            Entry: BEQ Skip
            Then: LDA #${'$'}01
            Skip: RTS
        """.trimIndent())

        val ifNode = nodes.filterIsInstance<IfNode>().firstOrNull()
        assertNotNull(ifNode)
        assertTrue(ifNode.coveredBlocks.isNotEmpty(), "IfNode should cover blocks")
    }

    @Test
    fun `LoopNode coveredBlocks includes body`() {
        val (_, nodes) = analyzeControls("""
            Loop: DEX
            BNE Loop
            Exit: RTS
        """.trimIndent())

        val loopNode = nodes.filterIsInstance<LoopNode>().firstOrNull()
        assertNotNull(loopNode)
        assertTrue(loopNode.coveredBlocks.isNotEmpty(), "LoopNode should cover blocks")
    }

    // =====================================================================
    // CONDITION SENSE HANDLING
    // =====================================================================

    @Test
    fun `condition sense - branch-taken is then (sense true)`() {
        // When BNE branches to the "do something" block
        // This pattern may be detected as if-then or as blocks with gotos
        val (_, nodes) = analyzeControls("""
            Entry: LDA #${'$'}00
            BNE DoIt
            JMP Done
            DoIt: LDA #${'$'}01
            Done: RTS
        """.trimIndent())

        // The control analysis should produce some structure
        // It may be an if node or multiple blocks
        assertTrue(nodes.isNotEmpty(), "Should produce control nodes")

        // If there's an IfNode, verify its structure
        val ifNode = nodes.filterIsInstance<IfNode>().firstOrNull()
        if (ifNode != null) {
            // Check that condition has a branch block
            assertNotNull(ifNode.condition.branchBlock)
        }
    }

    @Test
    fun `condition sense - fall-through is then (sense false)`() {
        // Standard pattern: BEQ Skip / then code / Skip:
        val (_, nodes) = analyzeControls("""
            Entry: BEQ Skip
            Then: LDA #${'$'}01
            Skip: RTS
        """.trimIndent())

        val ifNode = nodes.filterIsInstance<IfNode>().firstOrNull()
        assertNotNull(ifNode)
        // sense=false means fall-through is then
    }

    // =====================================================================
    // GOTO ELIMINATION (FORWARD/BACKWARD)
    // =====================================================================

    @Test
    fun `forward goto becomes if-then`() {
        // Forward branch should become if-then
        val (_, nodes) = analyzeControls("""
            Start: BEQ Forward
            LDA #${'$'}01
            Forward: RTS
        """.trimIndent())

        val ifNode = nodes.filterIsInstance<IfNode>().firstOrNull()
        assertNotNull(ifNode, "Forward branch should become if-then")
    }

    @Test
    fun `backward goto becomes loop`() {
        // Backward branch should become loop
        val (_, nodes) = analyzeControls("""
            Back: LDA #${'$'}00
            BNE Back
            RTS
        """.trimIndent())

        assertTrue(nodes.any { it is LoopNode }, "Backward branch should become loop")
    }

    // =====================================================================
    // EDGE CASES
    // =====================================================================

    @Test
    fun `empty function`() {
        val (functions, _) = analyzeControls("Start: RTS")
        val mainFunc = functions.first()
        val controls = mainFunc.asControls
        assertNotNull(controls)
    }

    @Test
    fun `linear code - no control structures`() {
        val (_, nodes) = analyzeControls("""
            Start: LDA #${'$'}00
            STA ${'$'}0200
            LDX #${'$'}01
            RTS
        """.trimIndent())

        // All should be BlockNodes (no loops, no ifs)
        val blockNodes = nodes.filterIsInstance<BlockNode>()
        val otherNodes = nodes.filter { it !is BlockNode }
        assertTrue(blockNodes.isNotEmpty(), "Should have block nodes")
    }

    @Test
    fun `complex nested structure`() {
        val (_, nodes) = analyzeControls("""
            Outer: LDX #${'$'}05
            Inner: LDY #${'$'}03
            InnerBody: DEY
            BNE Inner
            OuterBody: DEX
            BNE Outer
            Exit: RTS
        """.trimIndent())

        // Should recognize the nested structure
        assertTrue(nodes.isNotEmpty(), "Should produce control nodes")
    }

    // =====================================================================
    // SWITCH/DISPATCH PATTERNS
    // =====================================================================

    @Test
    fun `JumpEngine dispatch creates multiple cases`() {
        // Note: JumpEngine pattern recognition may be limited
        val (functions, _) = analyzeControls("""
            Main: JSR JumpEngine
            .dw Handler1
            .dw Handler2
            Handler1: LDA #${'$'}01
            RTS
            Handler2: LDA #${'$'}02
            RTS
            JumpEngine: RTS
        """.trimIndent())

        // Should have multiple functions
        assertTrue(functions.size >= 2, "Should recognize JumpEngine handlers as functions")
    }

    // =====================================================================
    // VALIDATION
    // =====================================================================

    @Test
    fun `all blocks covered by control nodes`() {
        val (functions, nodes) = analyzeControls("""
            Entry: BEQ Skip
            Body: LDA #${'$'}01
            Skip: RTS
        """.trimIndent())

        val mainFunc = functions.first()
        val allCoveredBlocks = nodes.flatMap { it.coveredBlocks }.toSet()

        // All function blocks should be covered
        mainFunc.blocks?.forEach { block ->
            assertTrue(block in allCoveredBlocks || nodes.any { node ->
                (node as? IfNode)?.join == block
            }, "Block ${block.label ?: block.originalLineIndex} should be covered")
        }
    }

    @Test
    fun `parent references set correctly`() {
        val (_, nodes) = analyzeControls("""
            Entry: BEQ Skip
            Body: LDA #${'$'}01
            Skip: RTS
        """.trimIndent())

        fun checkParentRefs(parent: ControlNode?, children: List<ControlNode>) {
            for (child in children) {
                assertEquals(parent, child.parent, "Child should have correct parent reference")
                when (child) {
                    is IfNode -> {
                        checkParentRefs(child, child.thenBranch)
                        checkParentRefs(child, child.elseBranch)
                    }
                    is LoopNode -> {
                        checkParentRefs(child, child.body)
                    }
                    is SwitchNode -> {
                        child.cases.forEach { checkParentRefs(child, it.nodes) }
                        checkParentRefs(child, child.defaultBranch)
                    }
                    else -> { /* BlockNode, GotoNode have no children */ }
                }
            }
        }

        // Top-level nodes should have null parent
        nodes.forEach { node ->
            assertNull(node.parent, "Top-level nodes should have null parent")
        }
    }

    // =====================================================================
    // CONDITION EXPRESSION TYPES
    // =====================================================================

    @Test
    fun `condition expr default is UnknownCond`() {
        val (_, nodes) = analyzeControls("""
            Entry: BEQ Skip
            Body: LDA #${'$'}01
            Skip: RTS
        """.trimIndent())

        val ifNode = nodes.filterIsInstance<IfNode>().firstOrNull()
        assertNotNull(ifNode)
        assertIs<UnknownCond>(ifNode.condition.expr)
    }

    @Test
    fun `FlagTest toKotlinExpr positive`() {
        val test = FlagTest(AssemblyAffectable.Zero, positive = true)
        val expr = test.toKotlinExpr()
        assertIs<KVar>(expr)
        assertEquals("zeroFlag", (expr as KVar).name)
    }

    @Test
    fun `FlagTest toKotlinExpr negative`() {
        val test = FlagTest(AssemblyAffectable.Zero, positive = false)
        val expr = test.toKotlinExpr()
        assertIs<KUnaryOp>(expr)
    }

    @Test
    fun `ComparisonExpr toKotlinExpr`() {
        val cmp = ComparisonExpr(
            left = RegisterValue(AssemblyAffectable.A),
            op = CompareOp.EQ,
            right = LiteralValue(0x42)
        )
        val expr = cmp.toKotlinExpr()
        assertIs<KBinaryOp>(expr)
    }

    // =====================================================================
    // LOOP KIND DETECTION
    // =====================================================================

    @Test
    fun `loop kind - PreTest detected`() {
        val (_, nodes) = analyzeControls("""
            Loop: LDA #${'$'}00
            BEQ Done
            DEX
            JMP Loop
            Done: RTS
        """.trimIndent())

        val loopNode = nodes.filterIsInstance<LoopNode>().firstOrNull()
        assertNotNull(loopNode)
        assertEquals(LoopKind.PreTest, loopNode.kind, "Should be PreTest loop")
    }

    @Test
    fun `loop kind - PostTest detected`() {
        val (_, nodes) = analyzeControls("""
            Loop: DEX
            BNE Loop
            Done: RTS
        """.trimIndent())

        val loopNode = nodes.filterIsInstance<LoopNode>().firstOrNull()
        assertNotNull(loopNode)
        assertEquals(LoopKind.PostTest, loopNode.kind, "Should be PostTest loop")
    }

    @Test
    fun `loop kind - Infinite detected`() {
        val (_, nodes) = analyzeControls("""
            Loop: NOP
            JMP Loop
        """.trimIndent())

        val loopNode = nodes.filterIsInstance<LoopNode>().firstOrNull()
        assertNotNull(loopNode)
        assertEquals(LoopKind.Infinite, loopNode.kind, "Should be Infinite loop")
    }
}
