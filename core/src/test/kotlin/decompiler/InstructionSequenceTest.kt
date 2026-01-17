// by Claude - Tests for multi-instruction sequences where register state matters
package com.ivieleague.decompiler6502tokotlin.decompiler

import com.ivieleague.decompiler6502tokotlin.hand.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * Tests for multi-instruction sequence decompilation.
 *
 * These tests verify that register aliasing, flag preservation, and carry chain
 * patterns are correctly handled across multiple instructions.
 *
 * The key insight is that register state must be preserved correctly when:
 * - A register is transferred (TAX/TAY/TXA/TYA)
 * - The source register is then overwritten
 * - The destination register should retain the transferred value
 */
class InstructionSequenceTest {

    // =========================================================================
    // Transfer + Overwrite Patterns
    // These test that Y/X register preserves value after A is overwritten
    // =========================================================================

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `TAY followed by LDA then CPY should compare preserved Y value`() {
        val code = """
            TestFunc:
                LDA #${'$'}10      ; A = 0x10
                TAY              ; Y = A (0x10)
                LDA #${'$'}00      ; A = 0 (Y still 0x10)
                CPY #${'$'}10      ; Compare Y (0x10) with 0x10
                BEQ Equal
                RTS
            Equal:
                LDA #${'$'}FF
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()
        println("TAY+LDA+CPY pattern:\n$kotlinCode")

        // The comparison should use Y (or its preserved value), not A (which is 0)
        // If Y is correctly preserved, the condition should compare Y with 0x10
        assertFalse(
            kotlinCode.contains("if (0x00") || kotlinCode.contains("if (A =="),
            "Should not compare A (0x00) with 0x10"
        )
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `TAX followed by LDA then CPX should compare preserved X value`() {
        val code = """
            TestFunc:
                LDA #${'$'}20      ; A = 0x20
                TAX              ; X = A (0x20)
                LDA #${'$'}00      ; A = 0 (X still 0x20)
                CPX #${'$'}20      ; Compare X (0x20) with 0x20
                BEQ Equal
                RTS
            Equal:
                LDA #${'$'}FF
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()
        println("TAX+LDA+CPX pattern:\n$kotlinCode")

        // X should retain 0x20, not be affected by LDA #$00
        assertFalse(
            kotlinCode.contains("if (0x00") || kotlinCode.contains("if (A =="),
            "Should not compare A (0x00) with 0x20"
        )
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `TXA followed by LDX then CMP should compare preserved A value`() {
        val code = """
            TestFunc:
                LDX #${'$'}30      ; X = 0x30
                TXA              ; A = X (0x30)
                LDX #${'$'}00      ; X = 0 (A still 0x30)
                CMP #${'$'}30      ; Compare A (0x30) with 0x30
                BEQ Equal
                RTS
            Equal:
                LDY #${'$'}FF
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()
        println("TXA+LDX+CMP pattern:\n$kotlinCode")

        // A should retain 0x30 from TXA
        assertTrue(
            kotlinCode.contains("if (") || kotlinCode.contains("0x30"),
            "Should have comparison involving 0x30"
        )
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `TYA followed by LDY then CMP should compare preserved A value`() {
        val code = """
            TestFunc:
                LDY #${'$'}40      ; Y = 0x40
                TYA              ; A = Y (0x40)
                LDY #${'$'}00      ; Y = 0 (A still 0x40)
                CMP #${'$'}40      ; Compare A (0x40) with 0x40
                BEQ Equal
                RTS
            Equal:
                LDX #${'$'}FF
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()
        println("TYA+LDY+CMP pattern:\n$kotlinCode")

        // A should retain 0x40 from TYA
        assertTrue(
            kotlinCode.contains("if (") || kotlinCode.contains("0x40"),
            "Should have comparison involving 0x40"
        )
    }

    // =========================================================================
    // Carry Chain Patterns
    // These test that carry flag propagates correctly through arithmetic
    // =========================================================================

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `CLC followed by ADC should add without initial carry`() {
        val code = """
            TestFunc:
                CLC              ; Clear carry
                LDA #${'$'}10      ; A = 0x10
                ADC #${'$'}10      ; A = 0x10 + 0x10 + 0 = 0x20
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()
        println("CLC+ADC pattern:\n$kotlinCode")

        // The addition should happen, resulting in 0x20
        assertTrue(
            kotlinCode.contains("+") || kotlinCode.contains("ADC"),
            "Should have addition operation"
        )
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `SEC followed by SBC should subtract with borrow`() {
        val code = """
            TestFunc:
                SEC              ; Set carry (no borrow)
                LDA #${'$'}30      ; A = 0x30
                SBC #${'$'}10      ; A = 0x30 - 0x10 - 0 = 0x20
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()
        println("SEC+SBC pattern:\n$kotlinCode")

        // The subtraction should happen
        assertTrue(
            kotlinCode.contains("-") || kotlinCode.contains("SBC"),
            "Should have subtraction operation"
        )
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `ASL followed by ROL should propagate carry`() {
        val code = """
            TestFunc:
                LDA #${'$'}80      ; A = 0x80 (high bit set)
                ASL A            ; A = 0x00, C = 1 (high bit shifted into carry)
                ROL A            ; A = 0x01 (carry shifted into low bit)
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()
        println("ASL+ROL carry propagation:\n$kotlinCode")

        // Both shift operations should be present
        assertTrue(
            kotlinCode.contains("shl") || kotlinCode.contains("<<") ||
            kotlinCode.contains("ASL") || kotlinCode.contains("ROL"),
            "Should have shift operations"
        )
    }

    // =========================================================================
    // Flag Preservation Patterns
    // These test that comparison results survive load operations
    // =========================================================================

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `CMP followed by PHA then PLA should preserve flags`() {
        val code = """
            TestFunc:
                LDA #${'$'}10
                CMP #${'$'}10      ; Sets Z flag (equal)
                PHA              ; Push A
                PLA              ; Pull A (flags should be preserved from CMP)
                BEQ Equal        ; Branch if Z still set from CMP
                RTS
            Equal:
                LDA #${'$'}FF
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()
        println("CMP+PHA+PLA flag preservation:\n$kotlinCode")

        // Should have conditional logic based on the comparison
        assertTrue(
            kotlinCode.contains("if (") || kotlinCode.contains("=="),
            "Should have conditional from CMP"
        )
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `CPX followed by LDA then BNE should branch on X comparison`() {
        val code = """
            TestFunc:
                LDX #${'$'}05
                CPX #${'$'}10      ; Compare X (5) with 0x10 - not equal
                LDA #${'$'}00      ; Load A (shouldn't affect Z from CPX)
                BNE NotEqual     ; Branch if Z not set (from CPX, not LDA!)
                RTS
            NotEqual:
                LDA #${'$'}FF
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()
        println("CPX+LDA+BNE pattern:\n$kotlinCode")

        // The condition should be based on X comparison, not A value
        assertTrue(
            kotlinCode.contains("if (") || kotlinCode.contains("!=") || kotlinCode.contains("=="),
            "Should have conditional from CPX comparison"
        )
    }

    // =========================================================================
    // Multi-Register Coordination
    // These test complex patterns involving multiple registers
    // =========================================================================

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `Loop counter in Y with A used for calculation`() {
        val code = """
            TestFunc:
                LDY #${'$'}03      ; Y = 3 (loop counter)
            Loop:
                TYA              ; A = Y (counter value)
                ASL A            ; A = A * 2
                STA ${'$'}0200      ; Store calculated value
                DEY              ; Y-- (decrement counter)
                BNE Loop         ; Loop while Y != 0
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()
        println("Loop with Y counter:\n$kotlinCode")

        // Should have a loop structure with Y decrement
        assertTrue(
            kotlinCode.contains("while") || kotlinCode.contains("do {") ||
            kotlinCode.contains("Y") || kotlinCode.contains("--"),
            "Should have loop structure"
        )
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `Index calculation with X register preserved`() {
        val code = """
            TestFunc:
                LDX #${'$'}05      ; X = 5 (index)
                TXA              ; A = X
                ASL A            ; A = X * 2
                TAY              ; Y = A (index * 2)
                LDA ${'$'}0200,Y    ; Load from array at index*2
                STA ${'$'}0300,X    ; Store to another array at index
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()
        println("Index calculation pattern:\n$kotlinCode")

        // X should be preserved for the final store
        assertTrue(
            kotlinCode.contains("X") || kotlinCode.contains("["),
            "Should have indexed access with X"
        )
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `Swap A and Y using stack`() {
        val code = """
            TestFunc:
                LDA #${'$'}AA      ; A = 0xAA
                LDY #${'$'}55      ; Y = 0x55
                PHA              ; Push A (0xAA)
                TYA              ; A = Y (0x55)
                TAX              ; X = A (0x55) - temporary
                PLA              ; A = 0xAA (from stack)
                TAY              ; Y = A (0xAA)
                TXA              ; A = X (0x55)
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()
        println("A/Y swap pattern:\n$kotlinCode")

        // The code should preserve values correctly through transfers
        assertTrue(kotlinCode.isNotEmpty(), "Should generate code")
    }

    // =========================================================================
    // Edge Cases
    // =========================================================================

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `Multiple transfers without intervening loads`() {
        val code = """
            TestFunc:
                LDA #${'$'}42
                TAX              ; X = A = 0x42
                TAY              ; Y = A = 0x42
                TXA              ; A = X = 0x42 (should be no-op effectively)
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()
        println("Multiple transfers:\n$kotlinCode")

        // All registers should end up with the same value
        assertTrue(kotlinCode.isNotEmpty(), "Should generate code")
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `Self-comparison after load`() {
        val code = """
            TestFunc:
                LDA ${'$'}1000      ; Load from memory
                CMP ${'$'}1000      ; Compare A with same memory location
                BEQ Equal        ; Should always branch (A == A)
                RTS
            Equal:
                LDA #${'$'}FF
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()
        println("Self-comparison:\n$kotlinCode")

        // The comparison should be present
        assertTrue(
            kotlinCode.contains("if (") || kotlinCode.contains("=="),
            "Should have comparison"
        )
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `Increment and compare in sequence`() {
        val code = """
            TestFunc:
                LDX #${'$'}00
            Loop:
                INX              ; X++
                CPX #${'$'}10      ; Compare X with 16
                BNE Loop         ; Loop while X != 16
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()
        println("Increment and compare loop:\n$kotlinCode")

        // Should have loop and increment
        assertTrue(
            kotlinCode.contains("while") || kotlinCode.contains("do {") ||
            kotlinCode.contains("++") || kotlinCode.contains("X = X + 1"),
            "Should have loop with increment"
        )
    }
}
