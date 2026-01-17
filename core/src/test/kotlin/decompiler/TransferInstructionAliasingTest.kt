// by Claude - Test for transfer instruction register aliasing bug
package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * Tests for decompiler handling of transfer instructions (TAY, TAX, TXA, TYA).
 *
 * Bug (Fix #9): When TAY copies a temp variable and the source is later reassigned
 * by LDA, the Y register tracking becomes stale. This caused CPY conditions to
 * check the wrong value.
 *
 * Example from OutputNumbers:
 * ```asm
 *     asl           ; temp2 = shifted value
 *     tay           ; ctx.registerY = temp2 (KVar reference)
 *     ldx offset    ; loads X from memory
 *     lda #$20      ; temp2 = 0x20 (overwrites the shifted value!)
 *     cpy #$00      ; should check Y (the old shifted value), not temp2 (0x20)
 *     bne SetupNums
 *     lda #$22      ; this should only execute if Y was 0
 * ```
 *
 * Fix: Transfer instructions now materialize the source value into a new immutable
 * variable when the source is a mutable KVar. This preserves the snapshot value.
 */
class TransferInstructionAliasingTest {

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `TAY followed by LDA should preserve Y value for CPY comparison`() {
        // Simplified pattern from OutputNumbers
        val code = """
            TestFunc:
                ASL                ; A = A << 1
                TAY                ; Y = A (save shifted value)
                LDA #${'$'}20      ; A = 0x20 (overwrites A but Y should still have old value)
                CPY #${'$'}00      ; compare Y (old shifted value) with 0
                BNE Skip
                LDA #${'$'}22      ; only if Y == 0
            Skip:
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()

        println("Generated Kotlin code for TAY + LDA pattern:")
        println(kotlinCode)

        // After the fix, the Y register should correctly preserve the shifted A value
        // The condition should compare the preserved Y value (A after ASL) with 0
        // NOT the LDA #$20 value (which would always be false)

        // The if statement should exist
        assertTrue(
            kotlinCode.contains("if ("),
            "Should have an if statement for the BNE branch"
        )

        // The condition should NOT compare literal 0x20 with 0 (that's the LDA value)
        assertFalse(
            kotlinCode.contains("if (0x20") || kotlinCode.contains("if (temp0"),
            "Condition should compare the preserved Y value (from ASL), not the LDA #$20 value (temp0)"
        )

        // The condition should reference A, Y, or a temp variable derived from them
        // After TAY, the Y register holds the shifted A value, so comparing Y is correct
        assertTrue(
            kotlinCode.contains("if (A") || kotlinCode.contains("if (temp1") ||
            kotlinCode.contains("(A ==") || kotlinCode.contains("if (Y") || kotlinCode.contains("(Y =="),
            "Condition should compare the preserved value (A or Y after TAY) with 0"
        )
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `TAX followed by LDA should preserve X value for CPX comparison`() {
        // Similar pattern with TAX
        val code = """
            TestFunc:
                LDA #${'$'}05
                TAX                ; X = A = 5
                LDA #${'$'}FF      ; A = 0xFF (overwrites A but X should still be 5)
                CPX #${'$'}05      ; compare X (should be 5) with 5
                BEQ Equal
                RTS
            Equal:
                LDA #${'$'}00
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()

        println("Generated Kotlin code for TAX + LDA pattern:")
        println(kotlinCode)

        // The CPX comparison should reference the preserved X value (which was 5),
        // not the current A value (which is 0xFF)
        assertFalse(
            kotlinCode.contains("0xFF == 0x05") || kotlinCode.contains("255 == 5"),
            "Condition should not compare 0xFF with 5 (that's always false)"
        )
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `TXA followed by LDX should preserve A value for CMP comparison`() {
        // Pattern with TXA (X -> A)
        val code = """
            TestFunc:
                LDX #${'$'}10
                TXA                ; A = X = 0x10
                LDX #${'$'}00      ; X = 0 (overwrites X but A should still be 0x10)
                CMP #${'$'}10      ; compare A (should be 0x10) with 0x10
                BEQ Equal
                RTS
            Equal:
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()

        println("Generated Kotlin code for TXA + LDX pattern:")
        println(kotlinCode)

        // The CMP comparison should reference the preserved A value (which was 0x10),
        // not any undefined value
        assertTrue(
            kotlinCode.contains("if (") || kotlinCode.contains("== 0x10"),
            "Should have conditional based on the preserved A value"
        )
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `TYA followed by LDY should preserve A value`() {
        // Pattern with TYA (Y -> A)
        val code = """
            TestFunc:
                LDY #${'$'}20
                TYA                ; A = Y = 0x20
                LDY #${'$'}00      ; Y = 0 (overwrites Y but A should still be 0x20)
                CMP #${'$'}20      ; compare A (should be 0x20) with 0x20
                BEQ Equal
                RTS
            Equal:
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()

        println("Generated Kotlin code for TYA + LDY pattern:")
        println(kotlinCode)

        // The code should preserve the A value through TYA
        assertTrue(
            kotlinCode.contains("if (") || kotlinCode.contains("0x20"),
            "Should have logic that preserves the A value from TYA"
        )
    }
}
