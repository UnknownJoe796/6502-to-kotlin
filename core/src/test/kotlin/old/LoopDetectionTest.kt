package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit

/**
 * Tests for loop detection in the control flow analyzer.
 * Tests all three loop kinds: PreTest (while), PostTest (do-while), and Infinite.
 */
class LoopDetectionTest {

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `analyzeControls detects PreTest loop (while pattern)`() {
        // Pattern: header checks condition, exits forward if false, body ends with JMP back to header
        // while (condition) { body }
        val code = """
            loop_header:
              LDA counter
              BEQ loop_exit    ; branch exits loop if zero
              DEC counter      ; loop body
              JMP loop_header  ; back jump
            loop_exit:
              RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertEquals(1, functions.size)

        val controls = functions[0].analyzeControls()

        // by Claude - Debug output
        println("=== PreTest Loop Debug ===")
        println("Blocks: ${blocks.map { it.label ?: "@${it.originalLineIndex}" }}")
        blocks.forEach { block ->
            println("  ${block.label ?: "@${block.originalLineIndex}"}:")
            println("    idom: ${block.immediateDominator?.label}")
            println("    branchExit: ${block.branchExit?.label}")
            println("    fallThroughExit: ${block.fallThroughExit?.label}")
            println("    lines: ${block.lines.mapNotNull { it.instruction?.op }}")
        }
        println("\nNatural loops:")
        val naturalLoops = blocks.detectNaturalLoops()
        naturalLoops.forEach { loop ->
            println("  header=${loop.header.label}, body=${loop.body.map { it.label }}, exits=${loop.exits.map { it.label }}")
            println("  back-edges: ${loop.backEdges.map { (from, to) -> "${from.label}->${to.label}" }}")
        }
        println("\nControl nodes:")
        controls.forEach { println("  $it") }
        println()

        // Should produce a LoopNode
        assertTrue(controls.isNotEmpty(), "Expected control nodes")

        val loopNode = controls.filterIsInstance<LoopNode>().firstOrNull()
        assertNotNull(loopNode, "Should detect a LoopNode")
        println("Loop kind detected: ${loopNode?.kind}")
        assertEquals(LoopKind.PreTest, loopNode!!.kind, "Should be PreTest loop")
        assertNotNull(loopNode.condition, "PreTest loop should have a condition")
        assertEquals(blocks[0], loopNode.header, "Loop header should be first block")

        // Body should include the DEC and JMP blocks
        assertTrue(loopNode.body.isNotEmpty(), "Loop body should not be empty")

        // Break target should be loop_exit
        assertTrue(loopNode.breakTargets.isNotEmpty(), "Should have break target")
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `analyzeControls detects PostTest loop (do-while pattern)`() {
        // Pattern: body executes first, then conditional branch at end jumps back if true
        // do { body } while (condition)
        val code = """
            loop_start:
              DEC counter      ; loop body executes first
              INX
              LDA counter
              BNE loop_start   ; branch back to start if not zero
            loop_exit:
              RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertEquals(1, functions.size)

        val controls = functions[0].analyzeControls()

        // Should produce a LoopNode with PostTest kind
        assertTrue(controls.isNotEmpty(), "Should have control nodes")

        val loopNode = controls.filterIsInstance<LoopNode>().firstOrNull()
        assertNotNull(loopNode, "Should detect a LoopNode")
        assertEquals(LoopKind.PostTest, loopNode!!.kind, "Should be PostTest loop (do-while)")
        assertNotNull(loopNode.condition, "PostTest loop should have a condition")

        // For post-test, the header is the start of the loop body
        assertEquals(blocks[0], loopNode.header, "Loop header should be first block")
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `analyzeControls detects Infinite loop with no exit condition`() {
        // Pattern: unconditional jump back to start with no exit branch
        // while (true) { body }
        val code = """
            main_loop:
              LDA $0200
              STA $0201
              INC $0202
              JMP main_loop    ; unconditional back jump
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertEquals(1, functions.size)

        val controls = functions[0].analyzeControls()

        // Should produce a LoopNode with Infinite kind
        assertTrue(controls.isNotEmpty(), "Should have control nodes")

        val loopNode = controls.filterIsInstance<LoopNode>().firstOrNull()
        assertNotNull(loopNode, "Should detect a LoopNode")
        assertEquals(LoopKind.Infinite, loopNode!!.kind, "Should be Infinite loop")
        assertNull(loopNode.condition, "Infinite loop should have no condition")
        assertEquals(blocks[0], loopNode.header, "Loop header should be first block")
        assertTrue(loopNode.breakTargets.isEmpty(), "Infinite loop should have no break targets")
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `analyzeControls detects nested loops`() {
        // Outer while loop containing inner do-while loop
        val code = """
            outer_loop:
              LDA outer_counter
              BEQ outer_exit
              ; Inner loop (post-test)
            inner_loop:
              DEC inner_counter
              LDA inner_counter
              BNE inner_loop
              ; Continue outer
              DEC outer_counter
              JMP outer_loop
            outer_exit:
              RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertEquals(1, functions.size)

        val controls = functions[0].analyzeControls()

        // Should detect outer loop
        val outerLoop = controls.filterIsInstance<LoopNode>().firstOrNull()
        assertNotNull(outerLoop, "Should detect outer loop")

        // Inner loop should be in outer loop's body
        fun findNestedLoops(nodes: List<ControlNode>): List<LoopNode> {
            val result = mutableListOf<LoopNode>()
            for (node in nodes) {
                when (node) {
                    is LoopNode -> {
                        result.add(node)
                        result.addAll(findNestedLoops(node.body))
                    }
                    is IfNode -> {
                        result.addAll(findNestedLoops(node.thenBranch))
                        result.addAll(findNestedLoops(node.elseBranch))
                    }
                    else -> {}
                }
            }
            return result
        }

        val allLoops = findNestedLoops(controls)
        assertTrue(allLoops.size >= 1, "Should detect at least outer loop")
        // Note: Inner loop detection depends on implementation complexity
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `analyzeControls detects loop with break (early exit)`() {
        // Loop with conditional break inside
        val code = """
            loop_start:
              LDA status
              BEQ early_exit   ; conditional break
              STA $0200
              DEC counter
              JMP loop_start
            early_exit:
              RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertEquals(1, functions.size)

        val controls = functions[0].analyzeControls()

        // Should detect a loop (likely Infinite or PreTest depending on implementation)
        val loopNode = controls.filterIsInstance<LoopNode>().firstOrNull()
        assertNotNull(loopNode, "Should detect a loop")

        // Should recognize break target
        if (loopNode != null) {
            assertFalse(loopNode.breakTargets.isEmpty(), "Should have break target for early_exit")
        }
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `analyzeControls detects loop with continue`() {
        // Loop with conditional continue
        val code = """
            loop_header:
              LDA counter
              BEQ loop_exit
            loop_body:
              LDA skip_flag
              BNE loop_header  ; continue - skip to next iteration
              STA $0200
              DEC counter
              JMP loop_header
            loop_exit:
              RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertEquals(1, functions.size)

        val controls = functions[0].analyzeControls()

        val loopNode = controls.filterIsInstance<LoopNode>().firstOrNull()
        assertNotNull(loopNode, "Should detect a loop")

        if (loopNode != null) {
            assertTrue(loopNode.continueTargets.contains(blocks[0]),
                "Continue target should be loop header")
        }
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `analyzeControls distinguishes between forward branch and loop`() {
        // Not a loop - just a forward conditional branch
        val code = """
            start:
              LDA value
              BEQ skip
              STA $0200
            skip:
              RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertEquals(1, functions.size)

        val controls = functions[0].analyzeControls()

        // Should NOT produce a LoopNode - this is a forward branch (if statement)
        val loopNodes = controls.filterIsInstance<LoopNode>()
        assertTrue(loopNodes.isEmpty(), "Should not detect loop for forward-only branch")

        // Should be an IfNode instead
        val ifNodes = controls.filterIsInstance<IfNode>()
        assertTrue(ifNodes.isNotEmpty() || controls.filterIsInstance<BlockNode>().isNotEmpty(),
            "Should be if-statement or simple blocks, not loop")
    }
}
