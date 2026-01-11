// by Claude - Test for .db $2c BIT skip-byte pattern
package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * Tests for decompiler handling of the .db $2c BIT skip-byte pattern.
 *
 * This is a common 6502 optimization technique where a BIT instruction opcode ($2C)
 * is used to "hide" the next 2 bytes from execution. This allows two entry points
 * to share code with different initial register values.
 *
 * Example from MoveAllSpritesOffscreen:
 * ```asm
 * MoveAllSpritesOffscreen:
 *     ldy #$00           ; Y = 0 for this entry point
 *     .db $2c            ; BIT absolute opcode - next 2 bytes become operand
 * MoveSpritesOffscreen:
 *     ldy #$04           ; Y = 4 for this entry point (hidden for first entry)
 *     lda #$f8
 *     sta Sprite_Y_Position,y
 *     ; ... loop
 * ```
 *
 * When called via MoveAllSpritesOffscreen:
 * - CPU sees: LDY #$00, BIT $04A0, LDA #$F8, ...
 * - Y is set to 0, BIT instruction reads from $04A0 (harmless), continues
 *
 * When called via MoveSpritesOffscreen:
 * - CPU sees: LDY #$04, LDA #$F8, ...
 * - Y is set to 4
 *
 * The decompiler should:
 * 1. Recognize that MoveAllSpritesOffscreen sets Y=0 before the skip byte
 * 2. Generate code that sets Y=0 for MoveAllSpritesOffscreen
 * 3. Not confuse the two entry points
 */
class BitSkipByteTest {

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `MoveAllSpritesOffscreen pattern should handle skip byte`() {
        // This tests the exact pattern from SMB (simplified - constant defined inline)
        val code = """
            MoveAllSpritesOffscreen:
                ldy #${'$'}00
                .db ${'$'}2c
            MoveSpritesOffscreen:
                ldy #${'$'}04
                lda #${'$'}f8
            SprInitLoop:
                sta ${'$'}0200,y
                iny
                iny
                iny
                iny
                bne SprInitLoop
                rts
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        // Should find MoveAllSpritesOffscreen function by its starting block label
        val moveAllFunc = functions.find { it.startingBlock.label == "MoveAllSpritesOffscreen" }

        assertTrue(moveAllFunc != null, "Should find MoveAllSpritesOffscreen function")

        val kotlinCode = moveAllFunc!!.toKotlinFunction().toKotlin()

        println("Generated Kotlin code for MoveAllSpritesOffscreen:")
        println(kotlinCode)

        // Check that the skip comment is present
        val hasSkipComment = kotlinCode.contains("skipped by BIT")
        assertTrue(
            hasSkipComment,
            "MoveAllSpritesOffscreen should have a comment indicating the skipped instruction."
        )

        // The key test: Y should start at 0, not 4
        // The ldy #$04 should be skipped, so Y should be initialized to 0
        // by Claude - Updated to check for Y instead of temp0 (code gen now uses function-level vars)
        val hasYZeroInit = kotlinCode.contains("Y = 0x00") ||
                           kotlinCode.contains("Y = 0") ||
                           kotlinCode.contains("var Y: Int = 0") ||
                           kotlinCode.contains("temp0 = 0x00") ||
                           kotlinCode.contains("temp0 = 0")

        // Also check that it doesn't initialize to 4 after the 0x00 init
        // (the skipped instruction is ldy #$04)
        val hasYFourInit = kotlinCode.contains("Y = 0x04") ||
                           kotlinCode.contains("Y = 4") ||
                           kotlinCode.contains("temp0 = 0x04") ||
                           kotlinCode.contains("temp0 = 4")

        assertTrue(
            hasYZeroInit && !hasYFourInit,
            "MoveAllSpritesOffscreen should initialize Y to 0, not 4. " +
            "The .db \$2c skip-byte pattern should skip the ldy #\$04 instruction."
        )
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `Simple skip byte pattern should be recognized`() {
        // Simpler test case with the skip byte pattern
        val code = """
            EntryPointA:
                lda #${'$'}00
                .db ${'$'}2c
            EntryPointB:
                lda #${'$'}ff
                sta ${'$'}00
                rts
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        // Should find EntryPointA function by its starting block label
        val funcA = functions.find { it.startingBlock.label == "EntryPointA" }

        assertTrue(funcA != null, "Should find EntryPointA function")

        val kotlinCode = funcA!!.toKotlinFunction().toKotlin()

        println("Generated Kotlin code for EntryPointA:")
        println(kotlinCode)

        // For EntryPointA, A should be 0 (not 0xFF)
        // The .db $2c makes the next 2 bytes look like BIT operand
        // So when entering via EntryPointA: LDA #$00, BIT $FFA9 (A9 is lda opcode, FF is operand), STA $00, RTS
        // A ends up being 0, not 0xFF

        // Check that the stored value is 0, not 0xFF
        // The decompiler should store 0x00 because LDA #$FF is skipped
        val storesZero = kotlinCode.contains("= 0x00") || kotlinCode.contains("= 0.toUByte")
        val storesFF = kotlinCode.contains("= 0xFF") || kotlinCode.contains("= 255")

        // Also check for the skip comment
        val hasSkipComment = kotlinCode.contains("skipped by BIT")

        assertTrue(
            hasSkipComment,
            "EntryPointA should have a comment indicating the skipped instruction."
        )

        assertTrue(
            storesZero && !storesFF,
            "EntryPointA should store 0x00 to memory, not 0xFF. " +
            "The .db \$2c skip-byte hides the lda #\$ff instruction."
        )
    }

    // Note: Testing MoveSpritesOffscreen as a separate entry point would require
    // simulating a JSR to it, which is more complex. For now, we test that
    // the BIT skip pattern works correctly when entering via MoveAllSpritesOffscreen.
}
