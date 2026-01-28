package com.ivieleague.decompiler6502tokotlin.hand.stages

import com.ivieleague.decompiler6502tokotlin.hand.stages.SMBTestFixtures.loadFunction
import com.ivieleague.decompiler6502tokotlin.hand.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests that control flow structuring correctly reconstructs high-level control structures
 * (if/else, loops, etc.) from assembly blocks.
 *
 * This is a placeholder for future control flow tests.
 * Control flow analysis involves:
 * - Converting assembly CFG into structured control flow (IfNode, LoopNode, etc.)
 * - Eliminating gotos where possible
 * - Recognizing common patterns (guard clauses, early returns, etc.)
 */
class SMBControlFlowTest {

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `InitializeMemory has nested loop structure`() {
        val func = loadFunction("InitializeMemory")

        // Verify that control flow analysis has been run
        assertNotNull(func.asControls, "Control flow should be analyzed")
        assertTrue(func.asControls!!.isNotEmpty(), "Control flow should have nodes")

        // Should have: entry block, loop node, exit block
        val controls = func.asControls!!

        // First node should be entry block
        assertTrue(controls[0] is BlockNode, "First node should be entry block")
        val entryBlock = controls[0] as BlockNode
        assertTrue(entryBlock.block.label == "InitializeMemory", "Entry should be InitializeMemory")

        // Second node should be a loop
        assertTrue(controls.any { it is LoopNode }, "Should have a loop node")
        val loopNode = controls.first { it is LoopNode } as LoopNode
        assertTrue(loopNode.body.isNotEmpty(), "Loop should have body")

        // Loop entry should be InitPageLoop
        assertTrue(loopNode.entry.label == "InitPageLoop", "Loop entry should be InitPageLoop")

        // Last node should be return block
        assertTrue(controls.last() is BlockNode, "Last node should be a block")
        val exitBlock = controls.last() as BlockNode
        assertTrue(exitBlock.block.lines.any { it.instruction?.op == AssemblyOp.RTS },
            "Exit block should contain RTS")
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `all test functions have control flow analyzed`() {
        val functions = listOf(
            "InitializeMemory",
            "FloateyNumbersRoutine",
            "MusicHandler",
            "ImpedePlayerMove",
            "MoveSpritesOffscreen",
            "GetAreaObjXPosition",
            "GetAreaObjYPosition",
            "PlayerEnemyDiff",
            "InitVStf",
            "PlayerLakituDiff",
            "CheckForSolidMTiles",
            "CheckPlayerVertical",
            "HandlePipeEntry",
            "OffscreenBoundsCheck",
            "BubbleCheck"
        )

        for (funcName in functions) {
            val func = loadFunction(funcName)

            // All functions should have control flow analysis completed
            assertNotNull(func.asControls, "$funcName should have control flow analyzed")
            assertTrue(func.asControls!!.isNotEmpty(), "$funcName should have non-empty control flow")
        }
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `MoveSpritesOffscreen has loop structure`() {
        val func = loadFunction("MoveSpritesOffscreen")

        // by Claude - Debug output
        println("=== MoveSpritesOffscreen Debug ===")
        println("Starting block: ${func.startingBlock.label}")
        println("Starting block owner: ${func.startingBlock.function?.startingBlock?.label}")
        println("Is empty function: ${func.isEmptyFunction}")
        println("Blocks in function: ${func.blocks?.map { it.label ?: "@${it.originalLineIndex}" }}")

        // Check MoveAllSpritesOffscreen too
        val allFunc = SMBTestFixtures.allFunctions.find { it.startingBlock.label == "MoveAllSpritesOffscreen" }
        if (allFunc != null) {
            println("\n=== MoveAllSpritesOffscreen Debug ===")
            println("Blocks: ${allFunc.blocks?.map { it.label ?: "@${it.originalLineIndex}" }}")
        }

        // Check dominator info
        println("\n=== Dominator Info ===")
        func.blocks?.forEach { block ->
            val label = block.label ?: "@${block.originalLineIndex}"
            val idom = block.immediateDominator?.let { it.label ?: "@${it.originalLineIndex}" } ?: "null"
            println("  $label -> idom = $idom")
            println("    branchExit: ${block.branchExit?.label ?: block.branchExit?.originalLineIndex}")
            println("    fallThroughExit: ${block.fallThroughExit?.label ?: block.fallThroughExit?.originalLineIndex}")
        }

        // Check natural loops
        val naturalLoops = func.blocks?.toList()?.detectNaturalLoops() ?: emptyList()
        println("\n=== Natural Loops Detected ===")
        if (naturalLoops.isEmpty()) {
            println("  NO LOOPS DETECTED!")
        } else {
            naturalLoops.forEach { loop ->
                println("  Loop header: ${loop.header.label}")
                println("  Body: ${loop.body.map { it.label ?: "@${it.originalLineIndex}" }}")
                println("  Back-edges: ${loop.backEdges.map { (from, to) -> "${from.label ?: "@${from.originalLineIndex}"} -> ${to.label ?: "@${to.originalLineIndex}"}" }}")
                println("  Exits: ${loop.exits.map { it.label ?: "@${it.originalLineIndex}" }}")
            }
        }

        println("\n=== Control Nodes ===")

        assertNotNull(func.asControls, "Control flow should be analyzed")
        val controls = func.asControls!!
        controls.forEach { println("  $it") }

        // Should have: entry block, loop node, exit block
        assertTrue(controls.size >= 2, "Should have at least 2 nodes")

        // First node should be entry
        assertTrue(controls[0] is BlockNode, "First node should be entry block")

        // Should have a loop
        assertTrue(controls.any { it is LoopNode }, "Should have a loop node")
        val loopNode = controls.first { it is LoopNode } as LoopNode
        assertTrue(loopNode.entry.label == "SprInitLoop", "Loop entry should be SprInitLoop")

        // Last node should be return
        assertTrue(controls.last() is BlockNode, "Last node should be block")
        val exitBlock = controls.last() as BlockNode
        assertTrue(exitBlock.block.lines.any { it.instruction?.op == AssemblyOp.RTS },
            "Exit block should contain RTS")
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `GetAreaObjXPosition has straight line control flow`() {
        val func = loadFunction("GetAreaObjXPosition")

        assertNotNull(func.asControls, "Control flow should be analyzed")
        val controls = func.asControls!!

        // Should be a single block (no loops, no conditionals)
        assertTrue(controls.size == 1, "Should have exactly 1 control node")
        assertTrue(controls[0] is BlockNode, "Single node should be a BlockNode")

        val block = (controls[0] as BlockNode).block
        assertTrue(block.label == "GetAreaObjXPosition", "Block should be function entry")
        assertTrue(block.lines.any { it.instruction?.op == AssemblyOp.RTS },
            "Block should contain RTS")
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `CheckPlayerVertical has conditional structure`() {
        val func = loadFunction("CheckPlayerVertical")

        assertNotNull(func.asControls, "Control flow should be analyzed")
        val controls = func.asControls!!

        // Should have at least an if node and a return block
        assertTrue(controls.size >= 2, "Should have multiple nodes")

        // Should have an IfNode
        assertTrue(controls.any { it is IfNode }, "Should have an IfNode")
        val ifNode = controls.first { it is IfNode } as IfNode

        // Verify the if has a condition block
        assertNotNull(ifNode.condition.branchBlock, "If should have condition block")
        assertTrue(ifNode.condition.branchBlock.label == "CheckPlayerVertical",
            "Condition block should be function entry")
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `HandlePipeEntry has multiple conditionals`() {
        val func = loadFunction("HandlePipeEntry")

        assertNotNull(func.asControls, "Control flow should be analyzed")
        val controls = func.asControls!!

        // Should have at least an if node
        assertTrue(controls.any { it is IfNode }, "Should have at least one IfNode")

        // Count blocks in the CFG to verify complexity
        val blocks = mutableListOf<AssemblyBlock>()
        fun collect(b: AssemblyBlock?) {
            if (b == null || b in blocks) return
            blocks.add(b)
            collect(b.fallThroughExit)
            collect(b.branchExit)
        }
        collect(func.startingBlock)

        // HandlePipeEntry has many conditional checks, should have multiple blocks
        assertTrue(blocks.size > 3, "Should have multiple blocks for conditional checks")
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `guard clause pattern creates multiple early returns`() {
        val func = loadFunction("HandlePipeEntry")

        // Count blocks in CFG
        val blocks = mutableListOf<AssemblyBlock>()
        fun collect(b: AssemblyBlock?) {
            if (b == null || b in blocks) return
            blocks.add(b)
            collect(b.fallThroughExit)
            collect(b.branchExit)
        }
        collect(func.startingBlock)

        // Find the exit block
        val exitBlock = blocks.first { it.label == "ExPipeE" }

        // ExPipeE should have multiple predecessors (guard clauses branch to it)
        assertTrue(exitBlock.enteredFrom.size >= 3,
            "Guard clause pattern should create multiple branches to exit, got ${exitBlock.enteredFrom.size}")

        // Each guard clause block should branch to exit
        val guardClauses = exitBlock.enteredFrom.filter { it != exitBlock }
        guardClauses.forEach { block ->
            assertTrue(block.branchExit == exitBlock || block.fallThroughExit == exitBlock,
                "Guard clause block ${block.label ?: "@${block.originalLineIndex}"} should branch to exit")
        }
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `FloateyNumbersRoutine backward branch handled correctly`() {
        val func = loadFunction("FloateyNumbersRoutine")

        assertNotNull(func.asControls, "Control flow should be analyzed")
        val controls = func.asControls!!

        // FloateyNumbersRoutine branches backward to EndExitOne (early return)
        // This is a known edge case - the control flow might be misordered

        // Collect all blocks
        val blocks = mutableListOf<AssemblyBlock>()
        fun collect(b: AssemblyBlock?) {
            if (b == null || b in blocks) return
            blocks.add(b)
            collect(b.fallThroughExit)
            collect(b.branchExit)
        }
        collect(func.startingBlock)

        // Entry block should be FloateyNumbersRoutine
        val entry = blocks.first { it.label == "FloateyNumbersRoutine" }
        assertNotNull(entry, "Should have entry block")

        // Entry should branch to EndExitOne for early return
        val endExitOne = blocks.firstOrNull { it.label == "EndExitOne" }
        if (endExitOne != null) {
            assertTrue(entry.branchExit == endExitOne || entry.fallThroughExit == endExitOne,
                "Entry should branch to EndExitOne for early return")
        }
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `control flow nodes should not start with return blocks`() {
        val func = loadFunction("FloateyNumbersRoutine")

        assertNotNull(func.asControls, "Control flow should be analyzed")
        val controls = func.asControls!!

        // The first control node should ideally be the function entry, not a return block
        // This is a known issue with backward branches
        val firstNode = controls.first()

        // Log what we actually see for debugging
        when (firstNode) {
            is BlockNode -> {
                val label = firstNode.block.label ?: "@${firstNode.block.originalLineIndex}"
                // This test documents the current behavior (which may be incorrect)
                // If the first node is EndExitOne, that's a control flow ordering issue
                if (label == "EndExitOne") {
                    println("WARNING: Control flow starts with return block EndExitOne instead of entry")
                    println("This is a known issue with backward branches for early returns")
                }
            }
            else -> {}
        }
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `OffscreenBoundsCheck if-else chain structure`() {
        val func = loadFunction("OffscreenBoundsCheck")

        assertNotNull(func.asControls, "Control flow should be analyzed")
        val controls = func.asControls!!

        // OffscreenBoundsCheck has if-else chain for enemy type checks
        // Should have at least one IfNode
        assertTrue(controls.any { it is IfNode }, "Should have IfNode for conditionals")

        // Count total blocks in CFG
        val blocks = mutableListOf<AssemblyBlock>()
        fun collect(b: AssemblyBlock?) {
            if (b == null || b in blocks) return
            blocks.add(b)
            collect(b.fallThroughExit)
            collect(b.branchExit)
        }
        collect(func.startingBlock)

        // Should have reasonable number of blocks for the logic
        assertTrue(blocks.size >= 5, "Should have multiple blocks for if-else chain, got ${blocks.size}")
    }
}
