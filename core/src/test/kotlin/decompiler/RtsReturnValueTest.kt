// by Claude - Test for RTS return value tracking bug
package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertContains

/**
 * Tests for decompiler handling of return values from functions.
 *
 * This test catches the bug where RTS doesn't capture ctx.registerA,
 * causing the generated code to return undefined "A" instead of the
 * actual computed value.
 *
 * Bug example from GetLrgObjAttrib:
 * ```asm
 * LDA (AreaData),Y    ; load value
 * AND #$0F            ; mask low nibble
 * TAY                 ; transfer A to Y
 * RTS                 ; should return A (the masked value)
 * ```
 *
 * Was generating:
 * ```kotlin
 * temp2 = memory[...].toInt() and 0x0F
 * return A  // ERROR: A is never defined!
 * ```
 *
 * Should generate:
 * ```kotlin
 * temp2 = memory[...].toInt() and 0x0F
 * return temp2  // Return the actual computed value
 * ```
 */
class RtsReturnValueTest {

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `RTS returns value in A register after LDA and AND`() {
        // Simple pattern: load, mask, return
        val code = """
            TestFunc:
                LDA #${'$'}FF
                AND #${'$'}0F
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        // Mark output as A (since function returns in A)
        val func = functions.first()
        func.outputs = setOf(TrackedAsIo.A)

        val kotlinCode = func.toKotlinFunction().toKotlin()

        println("Generated Kotlin code for LDA+AND+RTS:")
        println(kotlinCode)

        // The return should NOT be "return A" with undefined A
        // It should return a temp variable or expression
        assertFalse(
            kotlinCode.contains("return A") && !kotlinCode.contains("var A"),
            "Should not return undefined 'A' variable - should return the computed value (temp var or expression)"
        )

        // Should have the AND operation
        assertTrue(
            kotlinCode.contains("and 0x0F") || kotlinCode.contains("and 0x0f"),
            "Should contain the AND operation"
        )

        // The return value should be from the AND result
        // Could be "return temp0" or "return (0xFF and 0x0F)" or similar
        assertTrue(
            kotlinCode.contains("return") && (
                kotlinCode.contains("temp") ||  // temp variable
                kotlinCode.contains("0x0F") ||  // inline expression
                kotlinCode.contains("0x0f")     // lowercase variant
            ),
            "Should return the AND result value"
        )
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `RTS returns value in A after complex operations`() {
        // Pattern from GetLrgObjAttrib: indirect load, AND, transfer, return
        val code = """
            SomeVar = ${'$'}0086
            GetValue:
                LDY SomeVar
                LDA SomeVar
                AND #${'$'}0F
                STA ${'$'}07
                INY
                LDA SomeVar
                AND #${'$'}0F
                TAY
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        // Mark output as A
        val func = functions.first()
        func.outputs = setOf(TrackedAsIo.A)

        val kotlinCode = func.toKotlinFunction().toKotlin()

        println("Generated Kotlin code for complex pattern:")
        println(kotlinCode)

        // Should not have undefined A in return
        // Check that if "return A" exists, A must be defined
        if (kotlinCode.contains("return A")) {
            assertTrue(
                kotlinCode.contains("var A") || kotlinCode.contains("val A") ||
                kotlinCode.contains("A: Int") ||  // function parameter
                kotlinCode.contains("param.*A".toRegex()),
                "If returning A, A must be defined somewhere in the function"
            )
        }
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `void function RTS does not add return value`() {
        // Void function should just return, not return a value
        val code = """
            VoidFunc:
                LDA #${'$'}00
                STA ${'$'}07
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        // Mark as void function (no outputs, or outputs not including A)
        val func = functions.first()
        func.outputs = emptySet()

        val kotlinCode = func.toKotlinFunction().toKotlin()

        println("Generated Kotlin code for void function:")
        println(kotlinCode)

        // Void function should have empty return or no return value
        // Should NOT have "return temp0" or similar
        val hasReturnWithValue = """return\s+\w+""".toRegex().containsMatchIn(kotlinCode)
        assertFalse(
            hasReturnWithValue,
            "Void function should not return a value"
        )
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `RTS returns correct value when A is modified multiple times`() {
        // Multiple modifications to A - should return final value
        val code = """
            MultiMod:
                LDA #${'$'}FF
                AND #${'$'}F0
                ORA #${'$'}05
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertTrue(functions.isNotEmpty(), "Should create at least one function")

        val func = functions.first()
        func.outputs = setOf(TrackedAsIo.A)

        val kotlinCode = func.toKotlinFunction().toKotlin()

        println("Generated Kotlin code for multiple A modifications:")
        println(kotlinCode)

        // Should have the final ORA result
        assertTrue(
            kotlinCode.contains("or 0x05") || kotlinCode.contains("or 0x5"),
            "Should contain the final ORA operation"
        )

        // The return should reference the result of ORA, not earlier operations
        assertFalse(
            kotlinCode.contains("return A") && !kotlinCode.contains("var A"),
            "Should not return undefined 'A' - should return the ORA result"
        )
    }
}
