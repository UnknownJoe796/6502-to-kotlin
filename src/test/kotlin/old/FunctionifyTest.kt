package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit

class FunctionifyTest {

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
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

        Assertions.assertEquals(1, functions.size)
        Assertions.assertEquals("main", functions[0].startingBlock.label)
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
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
        Assertions.assertEquals(2, functions.size)

        val labels = functions.map { it.startingBlock.label }.toSet()
        Assertions.assertTrue(labels.contains("main"))
        Assertions.assertTrue(labels.contains("subroutine"))
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
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
        Assertions.assertEquals(1, functions.size)
        Assertions.assertTrue(functions[0].inputs?.contains(TrackedAsIo.A) == true)
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
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
        Assertions.assertEquals(1, functions.size)
        Assertions.assertTrue(functions[0].inputs?.contains(TrackedAsIo.ZeroFlag) == true)
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
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
        Assertions.assertEquals(1, functions.size)
        val tempInput = functions[0].inputs?.filterIsInstance<TrackedAsIo.VirtualRegister>()
            ?.find { it.label == "$02" }
        Assertions.assertNotNull(tempInput)
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `functionify handles function with no inputs or outputs`() {
        val code = """
            func:
              NOP
              RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        Assertions.assertEquals(1, functions.size)
        Assertions.assertTrue(functions[0].inputs?.isEmpty() == true)
    }
}