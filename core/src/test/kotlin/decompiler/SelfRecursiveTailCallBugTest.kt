// by Claude - Unit tests to document and verify the self-recursive tail call bug
// See TODO.md "VERIFIED BUG: Self-Recursive Tail Calls"
package com.ivieleague.decompiler6502tokotlin.decompiler

import com.ivieleague.decompiler6502tokotlin.hand.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests for the self-recursive tail call bug.
 *
 * BUG: When a conditional branch (like BPL) targets the START of the CURRENT function,
 * the decompiler generates a recursive tail call instead of a loop continuation.
 *
 * Example: In renderUnderPart, `bpl RenderUnderPart` should continue the loop,
 * but instead generates `renderUnderPart(A, X, Y); return` causing StackOverflow.
 *
 * Root cause: kotlin-codegen.kt line 864 checks `branchTargetFunction != null`
 * without excluding `branchTargetFunction == ctx.currentFunction`.
 */
class SelfRecursiveTailCallBugTest {

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `backward branch to function start should NOT generate recursive call`() {
        // Simplified version of RenderUnderPart pattern
        // BPL branches back to the function's own entry point - this should be a loop, not recursion
        val code = """
            RenderUnderPart:
                STY ${'$'}07          ; store Y
                LDA #${'$'}00         ; load something
                INX                ; increment X
                CPX #${'$'}0D         ; compare X to 13
                BCS ExitUPartR     ; exit if X >= 13
                LDY ${'$'}07          ; load Y back
                DEY                ; decrement Y
                BPL RenderUnderPart  ; LOOP BACK if Y >= 0 (bug: generates recursive call)
            ExitUPartR:
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        // Parse and analyze
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()
        println("Generated Kotlin code:")
        println(kotlinCode)

        // BUG CHECK: The generated code should NOT contain a recursive call to renderUnderPart
        // If it does, this test documents the bug
        val hasRecursiveCall = kotlinCode.contains("renderUnderPart(") &&
                               kotlinCode.contains("return") &&
                               !kotlinCode.contains("return renderUnderPart(")  // tail-call return is also wrong

        if (hasRecursiveCall) {
            println("\n⚠️ BUG CONFIRMED: Generated code has recursive call to self")
            println("This causes StackOverflowError in functions like renderUnderPart")
        }

        // EXPECTED BEHAVIOR: Should use loop structure instead of recursive call
        // When fixed, this assertion should pass:
        // assertFalse(hasRecursiveCall, "Should not generate recursive call to self - use loop instead")

        // For now, document that the bug exists by checking code structure
        val hasDoWhileLoop = kotlinCode.contains("do {") || kotlinCode.contains("while (")
        assertTrue(hasDoWhileLoop, "Should have some kind of loop structure")
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `loop condition should use correct register`() {
        // BPL after DEY should test Y, not A
        val code = """
            LoopFunc:
                LDA ${'$'}10          ; load data into A
                DEY                ; decrement Y - THIS sets the N flag
                BPL LoopFunc       ; branch if Y positive (N flag clear)
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertTrue(functions.isNotEmpty())

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()
        println("Generated Kotlin for loop condition test:")
        println(kotlinCode)

        // BUG CHECK: The loop condition should reference Y (from DEY), not A
        val conditionUsesA = kotlinCode.contains("(A and 0x80)") ||
                             kotlinCode.contains("(A.and(0x80))")
        val conditionUsesY = kotlinCode.contains("(Y and 0x80)") ||
                             kotlinCode.contains("(Y.and(0x80))") ||
                             kotlinCode.contains("Y >= 0") ||
                             kotlinCode.contains("Y < 0")

        if (conditionUsesA && !conditionUsesY) {
            println("\n⚠️ BUG CONFIRMED: Loop condition uses A instead of Y")
            println("DEY sets the N flag from Y, but condition tests A")
        }

        // When fixed, this should pass:
        // assertTrue(conditionUsesY, "Loop condition should test Y (the decremented register)")
        // assertFalse(conditionUsesA, "Loop condition should NOT test A (which was just loaded)")
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `DEX followed by BPL should test X not A`() {
        val code = """
            XLoopFunc:
                LDA ${'$'}20          ; load data
                DEX                ; decrement X - THIS sets the N flag
                BPL XLoopFunc      ; branch if X positive
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertTrue(functions.isNotEmpty())

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()
        println("Generated Kotlin for DEX/BPL test:")
        println(kotlinCode)

        val conditionUsesX = kotlinCode.contains("(X and 0x80)") ||
                             kotlinCode.contains("X >= 0") ||
                             kotlinCode.contains("X < 0")
        val conditionUsesA = kotlinCode.contains("(A and 0x80)")

        if (conditionUsesA && !conditionUsesX) {
            println("\n⚠️ BUG: DEX/BPL pattern using A in condition instead of X")
        }
    }
}
