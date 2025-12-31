package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit

class DominatorsTest {
    
    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
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
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
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
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
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
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
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
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
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

