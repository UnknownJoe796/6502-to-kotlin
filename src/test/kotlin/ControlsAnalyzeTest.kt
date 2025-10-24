package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.io.File
import java.util.concurrent.TimeUnit

class ControlsAnalyzeTest {

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `analyzeControls on linear function produces single BlockNode`() {
        val code = """
            func:
              LDA #1
              STA $0200
              RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertEquals(1, functions.size)

        val nodes = functions[0].analyzeControls()
        assertEquals(1, nodes.size)
        val n0 = nodes[0]
        assertTrue(n0 is BlockNode)
        assertTrue(n0.exits.isEmpty(), "Linear function should have no exits from its single block")
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `analyzeControls detects IF-THEN with fall-through as THEN and sense false`() {
        val code = """
            func:
              BEQ join
              LDA #1
              STA $0200
            join:
              RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertEquals(1, functions.size)

        val byLabel = blocks.associateBy { it.label }
        val joinBlock = byLabel["join"] ?: error("join block not found")

        val nodes = functions[0].analyzeControls()
        assertTrue(nodes[0] is IfNode, "First node should be an IfNode")
        val ifn = nodes[0] as IfNode

        // Sense must be false: branch-taken goes to ELSE, fall-through is THEN
        assertFalse(ifn.condition.sense)

        // THEN branch should have some content, ELSE is empty, join points to join block
        assertTrue(ifn.thenBranch.isNotEmpty())
        assertTrue(ifn.elseBranch.isEmpty())
        assertEquals(joinBlock, ifn.join)

        // The following node should be the join block
        assertTrue(nodes[1] is BlockNode)
        val joinNode = nodes[1] as BlockNode
        assertEquals(joinBlock, joinNode.block)

        // Exits of the IfNode should be the join
        assertEquals(setOf(joinBlock), ifn.exits)
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `analyzeControls detects IF-ELSE with JMP-join and omits pure-JMP then tail`() {
        val code = """
            func:
              BEQ else
            then1:
              LDA #1
            thenTail:
              JMP join
            else:
              LDA #2
            join:
              RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertEquals(1, functions.size)

        val byLabel = blocks.associateBy { it.label }
        val joinBlock = byLabel["join"] ?: error("join block not found")

        val nodes = functions[0].analyzeControls()
        assertTrue(nodes[0] is IfNode, "First node should be an IfNode")
        val ifn = nodes[0] as IfNode

        // Sense must be false: branch-taken goes to ELSE, fall-through is THEN
        assertFalse(ifn.condition.sense)
        assertTrue(ifn.thenBranch.isNotEmpty(), "Then branch should not be empty")
        assertTrue(ifn.elseBranch.isNotEmpty(), "Else branch should not be empty")
        assertEquals(joinBlock, ifn.join)

        // Ensure the THEN branch does not include the pure-JMP tail block
        val lastThen = ifn.thenBranch.last() as BlockNode
        val lastInstr = lastThen.block.lines.lastOrNull { it.instruction != null }?.instruction
        assertNotNull(lastInstr)
        assertTrue(lastInstr!!.op != AssemblyOp.JMP, "Pure JMP tail block should be omitted from THEN branch")

        // Exits of the IfNode should be the join
        assertEquals(setOf(joinBlock), ifn.exits)
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `analyzeControls handles pre-test loop shape (now properly detected as LoopNode)`() {
        val code = """
            func:
              BEQ exit
              LDA #1
              JMP func
            exit:
              RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertEquals(1, functions.size)

        val byLabel = blocks.associateBy { it.label }
        val header = byLabel["func"] ?: error("header block not found")
        val exitBlock = byLabel["exit"] ?: error("exit block not found")

        val nodes = functions[0].analyzeControls()
        // Now properly recognized as a PreTest LoopNode
        assertTrue(nodes[0] is LoopNode, "First node should be a LoopNode")
        val loopNode = nodes[0] as LoopNode

        assertEquals(LoopKind.PreTest, loopNode.kind, "Should be PreTest loop")
        assertNotNull(loopNode.condition, "PreTest loop should have condition")
        assertEquals(header, loopNode.header, "Loop header should be func block")
        assertTrue(loopNode.body.isNotEmpty(), "Loop body should not be empty")
        assertTrue(loopNode.breakTargets.contains(exitBlock), "Exit block should be break target")

        // After the loop node, we should see the exit block as a block node
        val after = nodes[1]
        assertTrue(after is BlockNode)
        assertEquals(exitBlock, (after as BlockNode).block)
    }

    @Test
    @Timeout(60, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `analyzeControls runs over full SMB disassembly without crashing`() {
        val asmFile = File("smbdism.asm")
        assertTrue(asmFile.exists(), "smbdism.asm must exist at project root")
        val code = asmFile.readText().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertTrue(functions.isNotEmpty(), "Expected at least one function in SMB disassembly")

        // Run analyzeControls on all functions; ensure non-null and basic sanity
        functions.analyzeControls()
        for (f in functions) {
            val nodes = f.asControls
            assertNotNull(nodes, "Controls should be computed for every function")
            // Entry node of a function-level analysis should start at the function's starting block or soon after
            // We cannot guarantee shapes, but we can assert coverage includes the starting block
            val covered = nodes!!.flatMap { it.coveredBlocks }.toSet()
            assertTrue(covered.contains(f.startingBlock), "Control coverage should include the function's starting block for ${f.startingBlock.label}")
        }
    }
}
