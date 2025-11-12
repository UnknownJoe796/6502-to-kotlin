package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class NaturalLoopTest {

    @Test
    fun `detect simple while loop as natural loop`() {
        val code = """
            loop_header:
              LDA counter
              BEQ exit
              DEC counter
              JMP loop_header
            exit:
              RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()

        val loops = blocks.detectNaturalLoops()

        assertEquals(1, loops.size, "Should detect one natural loop")
        val loop = loops[0]
        assertEquals("loop_header", loop.header.label)
        assertTrue(loop.body.size >= 2, "Loop body should have at least header and body blocks")
    }

    @Test
    fun `detect ProcessAreaData-style loop with distant back-edge`() {
        // Simplified ProcessAreaData pattern
        val code = """
            ProcessAreaData:
              LDX #${'$'}02
            ProcADLoop:
              STX offset
              LDA data,X
              CMP #${'$'}FD
              BEQ RdyDecode
            RdyDecode:
              STA temp
            ChkLength:
              LDX offset
              LDA length,X
              BMI ProcLoopb
              DEC length,X
            ProcLoopb:
              DEX
              BPL ProcADLoop
            EndParse:
              RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()

        val loops = blocks.detectNaturalLoops()

        assertTrue(loops.isNotEmpty(), "Should detect at least one natural loop")

        // Should detect ProcADLoop as a loop header
        val procLoop = loops.find { it.header.label == "ProcADLoop" }
        assertNotNull(procLoop, "Should detect ProcADLoop as a natural loop")

        println("Detected ${loops.size} natural loops:")
        loops.forEach { loop ->
            println("  Loop at ${loop.header.label}:")
            println("    Body blocks: ${loop.body.size}")
            println("    Back-edges: ${loop.backEdges.size}")
            println("    Exits: ${loop.exits.map { it.label }}")
        }
    }

    @Test
    fun `detect do-while as natural loop`() {
        val code = """
            loop_start:
              DEC counter
              LDA counter
              BNE loop_start
            done:
              RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()

        val loops = blocks.detectNaturalLoops()

        assertEquals(1, loops.size, "Should detect one natural loop")
        val loop = loops[0]
        assertEquals("loop_start", loop.header.label)
        assertEquals(1, loop.backEdges.size, "Should have one back-edge")
    }

    @Test
    fun `detect nested loops`() {
        val code = """
            outer_loop:
              LDA outer_counter
              BEQ outer_exit
            inner_loop:
              DEC inner_counter
              LDA inner_counter
              BNE inner_loop
              DEC outer_counter
              JMP outer_loop
            outer_exit:
              RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()

        val loops = blocks.detectNaturalLoops()

        assertTrue(loops.size >= 2, "Should detect at least 2 loops (outer and inner)")

        val outerLoop = loops.find { it.header.label == "outer_loop" }
        val innerLoop = loops.find { it.header.label == "inner_loop" }

        assertNotNull(outerLoop, "Should detect outer loop")
        assertNotNull(innerLoop, "Should detect inner loop")

        // Inner loop should be part of outer loop's body
        assertTrue(outerLoop!!.body.contains(innerLoop!!.header),
            "Inner loop header should be in outer loop body")
    }

    @Test
    fun `no loops in straight-line code`() {
        val code = """
            start:
              LDA #${'$'}05
              STA temp
              RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()

        val loops = blocks.detectNaturalLoops()

        assertEquals(0, loops.size, "Should detect no loops in straight-line code")
    }

    @Test
    fun `no loop for forward branches`() {
        val code = """
            start:
              LDA value
              BEQ skip
              STA result
            skip:
              RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()

        val loops = blocks.detectNaturalLoops()

        assertEquals(0, loops.size, "Should detect no loops for forward-only branches")
    }
}
