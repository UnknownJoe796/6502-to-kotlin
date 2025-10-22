package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class BlockifyTest {

    @Test
    fun `blockify creates single block from straight-line code`() {
        val code = """
            LDA #5
            STA $0200
            RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()

        Assertions.assertEquals(1, blocks.size)
        Assertions.assertEquals(3, blocks[0].lines.size)
        Assertions.assertNull(blocks[0].label)
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

        Assertions.assertEquals(2, blocks.size)
        Assertions.assertEquals("start", blocks[0].label)
        Assertions.assertEquals("next", blocks[1].label)
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
        Assertions.assertEquals(3, blocks.size)
        Assertions.assertEquals("start", blocks[0].label)
        Assertions.assertNull(blocks[1].label)
        Assertions.assertEquals("zero", blocks[2].label)
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

        Assertions.assertEquals(3, blocks.size)

        // First block jumps to second
        Assertions.assertEquals(blocks[2], blocks[0].branchExit)
        Assertions.assertNull(blocks[0].fallThroughExit)

        // Second block is entered from first
        Assertions.assertTrue(blocks[2].enteredFrom.contains(blocks[0]))
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
        Assertions.assertEquals(3, blocks.size)

        // First block: falls through to block 1, branches to block 2
        Assertions.assertEquals(blocks[1], blocks[0].fallThroughExit)
        Assertions.assertEquals(blocks[2], blocks[0].branchExit)
        Assertions.assertEquals(blocks[2], blocks[1].fallThroughExit)
        Assertions.assertEquals(null, blocks[1].branchExit)
        Assertions.assertEquals(null, blocks[2].fallThroughExit)
        Assertions.assertEquals(null, blocks[2].branchExit)

        // Block 1 is entered from block 0 (fall through)
        Assertions.assertTrue(blocks[1].enteredFrom.contains(blocks[0]))

        // Block 2 is entered from block 0 (branch)
        Assertions.assertTrue(blocks[2].enteredFrom.contains(blocks[0]))
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

        Assertions.assertEquals(2, blocks.size)
        Assertions.assertNull(blocks[0].fallThroughExit)
        Assertions.assertNull(blocks[0].branchExit)
        Assertions.assertNull(blocks[1].fallThroughExit)
        Assertions.assertNull(blocks[1].branchExit)
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
        Assertions.assertEquals(2, blocks.size)
        Assertions.assertEquals(3, blocks[0].lines.size) // LDA, JSR, STA all in same block
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
        Assertions.assertEquals(1, blocks.size)
        Assertions.assertEquals(3, blocks[0].lines.size)
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

        Assertions.assertEquals(2, blocks.size)

        // Block 0 branches back to itself
        Assertions.assertEquals(blocks[0], blocks[0].branchExit)
        Assertions.assertTrue(blocks[0].enteredFrom.contains(blocks[0])) // Self-loop
    }
}