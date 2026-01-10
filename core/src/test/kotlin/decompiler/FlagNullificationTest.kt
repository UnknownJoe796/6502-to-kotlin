// by Claude - Test for flag nullification bug in if-else branches
package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * Tests for decompiler handling of flags that are set inside conditional branches.
 *
 * Bug: When an if-else has different flag states in each branch, flags are set to null
 * after merge. This causes loop conditions to fall back to undefined variables like
 * `flagN` or `flagZ`, leading to infinite loops or undefined behavior.
 *
 * Example from GetXOffscreenBits:
 * ```asm
 * XOfsLoop:
 *     ...
 *     bne ExitLoop     ; if non-zero, exit
 *     dey              ; decrement Y (sets N flag)
 *     bpl XOfsLoop     ; loop while positive
 * ExitLoop: rts
 * ```
 *
 * The DEY is only executed when the branch is NOT taken. After merging the if-else,
 * the N flag is null, causing the do-while condition to use undefined `flagN`.
 */
class FlagNullificationTest {

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `do-while loop with DEY in conditional should not use undefined flagN`() {
        // Simpler pattern: loop with conditional exit and DEY
        val code = """
            SomeVar = ${'$'}0000
            TestLoop:
                LDA SomeVar
                BNE ExitLoop
                DEY
                BPL TestLoop
            ExitLoop:
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()

        println("Generated Kotlin code for conditional DEY loop:")
        println(kotlinCode)

        // Should NOT contain undefined flagN variable
        assertFalse(
            kotlinCode.contains("flagN"),
            "Should not use undefined 'flagN' variable - should use actual computed expression"
        )

        // Should have a loop structure
        assertTrue(
            kotlinCode.contains("while") || kotlinCode.contains("do {"),
            "Should generate loop structure"
        )

        // The loop condition should reference the actual Y/temp variable, not a flag
        // After DEY, the condition should be based on the Y value (like Y >= 0 or similar)
        // Or it should properly track the N flag expression
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `simple DEY loop should have valid condition`() {
        // Simple case: just DEY + BPL loop (no conditional inside)
        val code = """
            SimpleLoop:
                DEY
                BPL SimpleLoop
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()

        println("Generated Kotlin code for simple DEY loop:")
        println(kotlinCode)

        // Should NOT contain undefined flagN
        assertFalse(
            kotlinCode.contains("flagN"),
            "Should not use undefined 'flagN' variable"
        )

        // Should have proper loop with Y-based condition
        assertTrue(
            kotlinCode.contains("while") || kotlinCode.contains("do {"),
            "Should generate loop structure"
        )
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `loop with DEX in conditional should not use undefined flagN`() {
        // Same pattern but with DEX
        val code = """
            SomeVar = ${'$'}0000
            XLoop:
                LDA SomeVar
                BNE ExitXLoop
                DEX
                BPL XLoop
            ExitXLoop:
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()

        println("Generated Kotlin code for conditional DEX loop:")
        println(kotlinCode)

        // Should NOT contain undefined flagN variable
        assertFalse(
            kotlinCode.contains("flagN"),
            "Should not use undefined 'flagN' variable - should use actual computed expression"
        )
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `loop with BNE condition should not use undefined flagZ`() {
        // Pattern using zero flag
        val code = """
            SomeVar = ${'$'}0000
            ZLoop:
                LDY SomeVar
                BEQ ExitZLoop
                DEX
                BNE ZLoop
            ExitZLoop:
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()

        println("Generated Kotlin code for conditional Z flag loop:")
        println(kotlinCode)

        // Should NOT contain undefined flagZ variable
        assertFalse(
            kotlinCode.contains("flagZ"),
            "Should not use undefined 'flagZ' variable - should use actual computed expression"
        )
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `loop with return in else branch should use then-branch flags for condition`() {
        // Pattern from GetXOffscreenBits:
        // Loop body does computation, then:
        // - BNE ExitLoop (if A != 0, exit)
        // - DEY (decrement Y, sets N flag)
        // - BPL Loop (if positive, loop)
        // ExitLoop: RTS
        //
        // The DEY is in the then-branch (A == 0), return is in the else-branch (A != 0)
        // After merge, should use then-branch's N flag, not null
        val code = """
            SomeData = ${'$'}0000
            TestFunc:
                LDY #${'$'}02
            XOfsLoop:
                LDA SomeData,y
                CMP #${'$'}00
                BNE ExitLoop
                DEY
                BPL XOfsLoop
            ExitLoop:
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()

        println("Generated Kotlin code for loop with return in else-branch:")
        println(kotlinCode)

        // Should NOT contain undefined flagN variable
        assertFalse(
            kotlinCode.contains("flagN"),
            "Should not use undefined 'flagN' variable when else-branch returns. " +
            "Should use the N flag expression from the continuing (then) branch."
        )

        // Should have a loop structure
        assertTrue(
            kotlinCode.contains("while") || kotlinCode.contains("do {"),
            "Should generate loop structure"
        )
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `exact GetXOffscreenBits pattern should not use undefined flagN`() {
        // Exact pattern from the problematic function
        val code = """
            XOffscreenBitsData = ${'$'}E900
            GetXOffscreenBits:
                STX ${'$'}04
                LDY #${'$'}01
            XOfsLoop:
                LDA XOffscreenBitsData,y
                LDX ${'$'}04
                CMP #${'$'}00
                BNE ExXOfsBS
                DEY
                BPL XOfsLoop
            ExXOfsBS:
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()

        println("Generated Kotlin code for GetXOffscreenBits pattern:")
        println(kotlinCode)

        // Should NOT contain undefined flagN variable
        assertFalse(
            kotlinCode.contains("flagN"),
            "Should not use undefined 'flagN' in loop condition. " +
            "The DEY in the continuing branch should provide the N flag."
        )
    }
}
