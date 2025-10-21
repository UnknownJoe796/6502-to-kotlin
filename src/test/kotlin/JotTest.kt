package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class BlockifyTest {
    
    @Test
    fun `blockify creates single block from straight-line code`() {
        val code = """
            LDA #5
            STA $0200
            RTS
        """.trimIndent().parseToAssemblyCodeFile()
        
        val blocks = code.lines.blockify()
        
        assertEquals(1, blocks.size)
        assertEquals(3, blocks[0].lines.size)
        assertNull(blocks[0].label)
    }
    
    @Test
    fun `blockify splits at labels`() {
        val code = """
            start:
              LDA #5
            next:
              STA $0200
              RTS
        """.trimIndent().parseToAssemblyCodeFile()
        
        val blocks = code.lines.blockify()
        
        assertEquals(2, blocks.size)
        assertEquals("start", blocks[0].label)
        assertEquals("next", blocks[1].label)
    }
    
    @Test
    fun `blockify splits at branch targets`() {
        val code = """
            start:
            LDA #5
            BEQ zero
            LDA #10
            zero:
            STA $0200
            RTS
        """.trimIndent().parseToAssemblyCodeFile()
        
        val blocks = code.lines.blockify()
        
        // Block 0: LDA #5, BEQ zero
        // Block 1: LDA #10
        // Block 2: zero: STA $0200, RTS
        assertEquals(3, blocks.size)
        assertEquals("start", blocks[0].label)
        assertNull(blocks[1].label)
        assertEquals("zero", blocks[2].label)
    }
    
    @Test
    fun `blockify creates control flow edges for unconditional jump`() {
        val code = """
            start:
              LDA #5
              JMP target
              LDA #6
            target:
              STA $0200
              RTS
        """.trimIndent().parseToAssemblyCodeFile()
        
        val blocks = code.lines.blockify()
        
        assertEquals(3, blocks.size)
        
        // First block jumps to second
        assertEquals(blocks[2], blocks[0].branchExit)
        assertNull(blocks[0].fallThroughExit)
        
        // Second block is entered from first
        assertTrue(blocks[2].enteredFrom.contains(blocks[0]))
    }
    
    @Test
    fun `blockify creates control flow edges for conditional branch`() {
        val code = """
            LDA $80
            BEQ zero
            LDA #10
            zero:
            STA $0200
            RTS
        """.trimIndent().parseToAssemblyCodeFile()
        
        val blocks = code.lines.blockify()
        
        // Block 0: LDA $80, BEQ zero
        // Block 1: LDA #10 (fall through)
        // Block 2: zero: STA $0200
        assertEquals(3, blocks.size)
        
        // First block: falls through to block 1, branches to block 2
        assertEquals(blocks[1], blocks[0].fallThroughExit)
        assertEquals(blocks[2], blocks[0].branchExit)
        assertEquals(blocks[2], blocks[1].fallThroughExit)
        assertEquals(null, blocks[1].branchExit)
        assertEquals(null, blocks[2].fallThroughExit)
        assertEquals(null, blocks[2].branchExit)

        // Block 1 is entered from block 0 (fall through)
        assertTrue(blocks[1].enteredFrom.contains(blocks[0]))
        
        // Block 2 is entered from block 0 (branch)
        assertTrue(blocks[2].enteredFrom.contains(blocks[0]))
    }
    
    @Test
    fun `blockify handles RTS as block terminator`() {
        val code = """
            LDA #5
            RTS
            LDA #10
            RTS
        """.trimIndent().parseToAssemblyCodeFile()
        
        val blocks = code.lines.blockify()
        
        assertEquals(2, blocks.size)
        assertNull(blocks[0].fallThroughExit)
        assertNull(blocks[0].branchExit)
        assertNull(blocks[1].fallThroughExit)
        assertNull(blocks[1].branchExit)
    }
    
    @Test
    fun `blockify handles JSR as non-terminating instruction`() {
        val code = """
            LDA #5
            JSR subroutine
            STA $0200
            subroutine:
            RTS
        """.trimIndent().parseToAssemblyCodeFile()
        
        val blocks = code.lines.blockify()
        
        // JSR doesn't terminate a block; it continues to next instruction
        assertEquals(2, blocks.size)
        assertEquals(3, blocks[0].lines.size) // LDA, JSR, STA all in same block
    }
    
    @Test
    fun `blockify ignores data directives`() {
        val code = """
            LDA #5
            .db ${'$'}00, ${'$'}01, ${'$'}02
            STA $0200
        """.trimIndent().parseToAssemblyCodeFile()
        
        val blocks = code.lines.blockify()
        
        // Data doesn't split blocks
        assertEquals(1, blocks.size)
        assertEquals(3, blocks[0].lines.size)
    }
    
    @Test
    fun `blockify handles backward branch creating loop`() {
        val code = """
            loop:
              LDA $80
              BNE loop
              RTS
        """.trimIndent().parseToAssemblyCodeFile()
        
        val blocks = code.lines.blockify()
        
        assertEquals(2, blocks.size)
        
        // Block 0 branches back to itself
        assertEquals(blocks[0], blocks[0].branchExit)
        assertTrue(blocks[0].enteredFrom.contains(blocks[0])) // Self-loop
    }
}

class DominatorsTest {
    
    @Test
    fun `dominators sets entry block as root`() {
        val code = """
            start:
              LDA #5
              STA $0200
              RTS
        """.trimIndent().parseToAssemblyCodeFile()
        
        val blocks = code.lines.blockify()
        blocks.dominators()
        
        // Entry block has no immediate dominator
        assertNull(blocks[0].immediateDominator)
    }
    
    @Test
    fun `dominators computes simple chain`() {
        val code = """
            start:
              LDA #5
              JMP next
            next:
              STA $0200
              RTS
        """.trimIndent().parseToAssemblyCodeFile()
        
        val blocks = code.lines.blockify()
        blocks.dominators()
        
        // start dominates next
        assertEquals(blocks[0], blocks[1].immediateDominator)
        assertTrue(blocks[0].dominates.contains(blocks[1]))
    }
    
    @Test
    fun `dominators handles if-then-else merge point`() {
        val code = """
            LDA $80
            BEQ else_branch
            LDA #5
            JMP merge
            else_branch:
            LDA #10
            merge:
            STA $0200
            RTS
        """.trimIndent().parseToAssemblyCodeFile()
        
        val blocks = code.lines.blockify()
        blocks.dominators()
        
        // Block layout:
        // 0: LDA $80, BEQ else_branch
        // 1: LDA #5, JMP merge
        // 2: else_branch: LDA #10
        // 3: merge: STA $0200, RTS
        
        // Entry block (0) dominates everything
        assertNull(blocks[0].immediateDominator)
        assertTrue(blocks[0].dominates.contains(blocks[1])) // then branch
        assertTrue(blocks[0].dominates.contains(blocks[2])) // else branch
        assertTrue(blocks[0].dominates.contains(blocks[3])) // merge point
        
        // Both branches dominated by entry
        assertEquals(blocks[0], blocks[1].immediateDominator)
        assertEquals(blocks[0], blocks[2].immediateDominator)
        
        // Merge point dominated by entry (not by either branch)
        assertEquals(blocks[0], blocks[3].immediateDominator)
    }
    
    @Test
    fun `dominators handles loop structure`() {
        val code = """
            loop_header:
              LDA $80
              BEQ exit
              DEC $80
              JMP loop_header
            exit:
              RTS
        """.trimIndent().parseToAssemblyCodeFile()
        
        val blocks = code.lines.blockify()
        blocks.dominators()
        
        // Block 0: loop_header (LDA, BEQ)
        // Block 1: DEC, JMP (loop body)
        // Block 2: exit: RTS
        
        // Loop header dominates loop body
        assertEquals(blocks[0], blocks[1].immediateDominator)
        assertTrue(blocks[0].dominates.contains(blocks[1]))
        
        // Loop header dominates exit
        assertEquals(blocks[0], blocks[2].immediateDominator)
        assertTrue(blocks[0].dominates.contains(blocks[2]))
    }
    
    @Test
    fun `dominators handles nested blocks`() {
        val code = """
            outer:
              LDA #5
            inner:
              STA $0200
              RTS
        """.trimIndent().parseToAssemblyCodeFile()
        
        val blocks = code.lines.blockify()
        blocks.dominators()
        
        assertEquals(blocks[0], blocks[1].immediateDominator)
    }
}

class FunctionifyTest {
    
    @Test
    fun `functionify identifies single function`() {
        val code = """
            main:
              LDA #5
              STA $0200
              RTS
        """.trimIndent().parseToAssemblyCodeFile()
        
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        
        assertEquals(1, functions.size)
        assertEquals("main", functions[0].startingBlock.label)
    }
    
    @Test
    fun `functionify identifies JSR target as function`() {
        val code = """
            main:
              JSR subroutine
              RTS
            subroutine:
              LDA #5
              STA $0200
              RTS
        """.trimIndent().parseToAssemblyCodeFile()
        
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        
        // Should identify both main and subroutine as functions
        assertEquals(2, functions.size)
        
        val labels = functions.map { it.startingBlock.label }.toSet()
        assertTrue(labels.contains("main"))
        assertTrue(labels.contains("subroutine"))
    }
    
    @Test
    fun `functionify computes inputs for register consumption`() {
        val code = """
            func:
              STA $0200
              RTS
        """.trimIndent().parseToAssemblyCodeFile()
        
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        
        // STA consumes A register without setting it first
        assertEquals(1, functions.size)
        assertTrue(functions[0].inputs?.contains(ExpressionifiedState.A) == true)
    }

    @Test
    fun `functionify tracks flags as inputs`() {
        val code = """
            func:
              BEQ skip
              LDA #5
              skip:
              RTS
        """.trimIndent().parseToAssemblyCodeFile()
        
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        
        // BEQ consumes zero flag without setting it
        assertEquals(1, functions.size)
        assertTrue(functions[0].inputs?.contains(ExpressionifiedState.ZeroFlag) == true)
    }
    
    @Test
    fun `functionify tracks zero-page virtual registers`() {
        val code = """
            func:
              LDA $02
              STA $8F
              RTS
        """.trimIndent().parseToAssemblyCodeFile()
        
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        
        // Loading from 'temp' makes it an input
        assertEquals(1, functions.size)
        val tempInput = functions[0].inputs?.filterIsInstance<ExpressionifiedState.VirtualRegister>()
            ?.find { it.label == "$02" }
        assertNotNull(tempInput)
    }
    
    @Test
    fun `functionify handles function with no inputs or outputs`() {
        val code = """
            func:
              NOP
              RTS
        """.trimIndent().parseToAssemblyCodeFile()
        
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertEquals(1, functions.size)
        assertTrue(functions[0].inputs?.isEmpty() == true)
    }
}
