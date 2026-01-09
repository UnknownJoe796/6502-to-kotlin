package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * Tests for decompiler handling of nested loops.
 *
 * This test catches the bug found in writeNTAddr where the decompiler
 * generated an infinite loop due to incorrect nested loop detection.
 */
class NestedLoopTest {

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `decompiler handles nested loop with shared label without infinite loop`() {
        // This is the problematic pattern from InitNTLoop in smbdism.asm
        // Both BNE instructions branch to the same label!
        val code = """
            LDX #${'$'}04
            LDY #${'$'}C0
            LDA #${'$'}24
        InitNTLoop:
            STA ${'$'}2007
            DEY
            BNE InitNTLoop
            DEX
            BNE InitNTLoop
            RTS
        """.trimIndent().parseToAssemblyCodeFile()

        // Parse and analyze
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        // Generate Kotlin code
        val kotlinCode = functions.first().toKotlinFunction().toKotlin()

        println("Generated Kotlin code:")
        println(kotlinCode)

        // Check for infinite loop patterns
        // The bug was: while (temp0 == 0) with temp0 starting at 0x04
        assertFalse(
            kotlinCode.contains("while (temp0 == 0)") ||
            kotlinCode.contains("while (temp1 == 0)"),
            "Should not generate while (temp == 0) when temp starts non-zero"
        )

        // Should have proper loop structure
        assertTrue(
            kotlinCode.contains("while") || kotlinCode.contains("do {"),
            "Should generate loop structure"
        )

        // The generated code should decrement counters
        assertTrue(
            kotlinCode.contains("- 1") || kotlinCode.contains("--"),
            "Should decrement loop counters"
        )
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `decompiler handles simple nested loops correctly`() {
        // Simpler nested loop: outer loop with inner loop, different labels
        val code = """
            LDX #${'$'}03
        OuterLoop:
            LDY #${'$'}02
        InnerLoop:
            DEY
            BNE InnerLoop
            DEX
            BNE OuterLoop
            RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()

        println("Simple nested loop generated code:")
        println(kotlinCode)

        // Should have loop structures
        assertTrue(
            kotlinCode.contains("while") || kotlinCode.contains("do {"),
            "Should generate loop structures"
        )
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `decompiler handles countdown loop correctly`() {
        // Single countdown loop that should be recognized
        val code = """
            LDX #${'$'}10
        Loop:
            DEX
            BNE Loop
            RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()

        println("Countdown loop generated code:")
        println(kotlinCode)

        // Should generate a loop that counts down
        assertTrue(
            (kotlinCode.contains("while") || kotlinCode.contains("do {")) &&
            (kotlinCode.contains("!= 0") || kotlinCode.contains("== 0")),
            "Should generate loop with zero check"
        )
    }
}
