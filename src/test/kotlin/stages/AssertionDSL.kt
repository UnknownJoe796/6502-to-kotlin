package com.ivieleague.decompiler6502tokotlin.hand.stages

import com.ivieleague.decompiler6502tokotlin.hand.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * DSL for asserting block structure expectations.
 */
class BlockStructureAssertion(private val blocks: List<AssemblyBlock>) {

    fun assertBlock(label: String, assertions: BlockAssertion.() -> Unit) {
        val block = blocks.firstOrNull { it.label == label }
        assertNotNull(block, "Block with label '$label' not found")
        BlockAssertion(block, blocks).assertions()
    }

    fun assertBlockCount(expected: Int) {
        assertEquals(expected, blocks.size, "Expected $expected blocks, found ${blocks.size}")
    }
}

class BlockAssertion(private val block: AssemblyBlock, private val allBlocks: List<AssemblyBlock>) {

    fun hasInstructionCount(count: Int) {
        val actualCount = block.lines.count { it.instruction != null }
        assertEquals(count, actualCount,
            "Block ${block.label} expected $count instructions, found $actualCount")
    }

    fun hasFallThrough(targetLabel: String?) {
        val actual = block.fallThroughExit?.label
        assertEquals(targetLabel, actual,
            "Block ${block.label} expected fallThrough to $targetLabel, found $actual")
    }

    fun hasBranch(targetLabel: String?) {
        val actual = block.branchExit?.label
        assertEquals(targetLabel, actual,
            "Block ${block.label} expected branch to $targetLabel, found $actual")
    }

    fun hasEnteredFrom(vararg labels: String) {
        val actualLabels = block.enteredFrom.mapNotNull { it.label }.toSet()
        val expectedLabels = labels.toSet()
        assertEquals(expectedLabels, actualLabels,
            "Block ${block.label} expected enteredFrom $expectedLabels, found $actualLabels")
    }

    fun isReturn() {
        val lastInstr = block.lines.lastOrNull { it.instruction != null }?.instruction
        assertTrue(
            lastInstr?.op == AssemblyOp.RTS || lastInstr?.op == AssemblyOp.RTI,
            "Block ${block.label} expected to end with RTS/RTI"
        )
    }
}

/**
 * DSL for asserting dominator relationships.
 */
class DominatorAssertion(private val blocks: List<AssemblyBlock>) {

    fun assertDominator(blockLabel: String, assertions: DominatorBlockAssertion.() -> Unit) {
        val block = blocks.firstOrNull { it.label == blockLabel }
        assertNotNull(block, "Block with label '$blockLabel' not found")
        DominatorBlockAssertion(block, blocks).assertions()
    }

    fun assertEntryIsRoot(entryLabel: String) {
        val entry = blocks.firstOrNull { it.label == entryLabel }
        assertNotNull(entry, "Entry block '$entryLabel' not found")

        // Entry block may have a dominator from outside the function (global analysis)
        // But it should not be dominated by any block within this function
        val dominator = entry.immediateDominator
        if (dominator != null && dominator in blocks) {
            throw AssertionError(
                "Entry block '$entryLabel' is dominated by '${dominator.label}' which is within the same function"
            )
        }
    }
}

class DominatorBlockAssertion(private val block: AssemblyBlock, private val allBlocks: List<AssemblyBlock>) {

    fun dominates(vararg labels: String) {
        val actualLabels = block.dominates.mapNotNull { it.label }.toSet()
        val expectedLabels = labels.toSet()
        assertEquals(expectedLabels, actualLabels,
            "Block ${block.label} expected to dominate $expectedLabels, found $actualLabels")
    }

    fun dominatedBy(label: String?) {
        val actual = block.immediateDominator?.label
        assertEquals(label, actual,
            "Block ${block.label} expected to be dominated by $label, found $actual")
    }

    fun isDominatedByEntry() {
        assertTrue(isDominatedBy(block, allBlocks.first()),
            "Block ${block.label} should be dominated by entry")
    }
}

/**
 * DSL for asserting function I/O.
 */
class FunctionIOAssertion(private val function: AssemblyFunction) {

    fun hasInput(io: TrackedAsIo) {
        assertTrue(function.inputs?.contains(io) == true,
            "Function ${function.startingBlock.label} expected to have input $io, found inputs: ${function.inputs}")
    }

    fun hasInputs(vararg expected: TrackedAsIo) {
        val expectedSet = expected.toSet()
        assertEquals(expectedSet, function.inputs,
            "Function ${function.startingBlock.label} expected inputs $expectedSet, found ${function.inputs}")
    }

    fun hasNoInputs() {
        assertTrue(function.inputs?.isEmpty() == true,
            "Function ${function.startingBlock.label} expected no inputs, found ${function.inputs}")
    }

    fun hasOutput(io: TrackedAsIo) {
        assertTrue(function.outputs?.contains(io) == true,
            "Function ${function.startingBlock.label} expected to have output $io, found outputs: ${function.outputs}")
    }

    fun hasOutputs(vararg expected: TrackedAsIo) {
        val expectedSet = expected.toSet()
        assertEquals(expectedSet, function.outputs,
            "Function ${function.startingBlock.label} expected outputs $expectedSet, found ${function.outputs}")
    }

    fun hasNoOutputs() {
        assertTrue(function.outputs?.isEmpty() == true,
            "Function ${function.startingBlock.label} expected no outputs, found ${function.outputs}")
    }

    fun clobbers(vararg expected: TrackedAsIo) {
        for (io in expected) {
            assertTrue(function.clobbers?.contains(io) == true,
                "Function ${function.startingBlock.label} expected to clobber $io, found clobbers: ${function.clobbers}")
        }
    }

    fun hasClobbers(vararg expected: TrackedAsIo) {
        val expectedSet = expected.toSet()
        assertTrue(function.clobbers?.containsAll(expectedSet) == true,
            "Function ${function.startingBlock.label} expected to clobber at least $expectedSet, found ${function.clobbers}")
    }
}

/**
 * Extension functions for easy DSL usage.
 */
fun List<AssemblyBlock>.assertStructure(assertions: BlockStructureAssertion.() -> Unit) {
    BlockStructureAssertion(this).assertions()
}

fun List<AssemblyBlock>.assertDominators(assertions: DominatorAssertion.() -> Unit) {
    DominatorAssertion(this).assertions()
}

fun AssemblyFunction.assertIO(assertions: FunctionIOAssertion.() -> Unit) {
    FunctionIOAssertion(this).assertions()
}
