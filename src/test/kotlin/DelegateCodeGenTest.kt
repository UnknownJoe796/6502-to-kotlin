package com.ivieleague.decompiler6502tokotlin

import com.ivieleague.decompiler6502tokotlin.hand.*
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Test that code generation uses delegates for memory access.
 */
class DelegateCodeGenTest {

    @Test
    fun testSimpleFunctionUsesDirectDelegate() {
        // Assembly that accesses SCREENTIMER directly
        val asm = """
            ResetScreenTimer:
                LDA #${'$'}07
                STA ScreenTimer
                INC ScreenRoutineTask
                RTS
        """.trimIndent()

        val parsed = asm.parseToAssemblyCodeFile()
        val blocks = parsed.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        val function = functions.first()

        val kotlinCode = function.toKotlinFunction().toKotlin()
        println("Generated code:")
        println(kotlinCode)

        // Should use delegates
        assertTrue(kotlinCode.contains("by MemoryByte"), "Should use MemoryByte delegate")
        assertTrue(kotlinCode.contains("screenTimer"), "Should have camelCase property name")

        // Should NOT use memory[] for named constants
        assertFalse(kotlinCode.contains("memory[SCREENTIMER]"), "Should not use memory[] for named constant")
    }

    @Test
    fun testIndexedAccessUsesIndexedDelegate() {
        // Assembly that accesses array with index
        val asm = """
            TestFunc:
                LDX #${'$'}00
                LDA Enemy_Flag,X
                STA Enemy_Flag,X
                RTS
        """.trimIndent()

        val parsed = asm.parseToAssemblyCodeFile()
        val blocks = parsed.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        val function = functions.first()

        val kotlinCode = function.toKotlinFunction().toKotlin()
        println("Generated code with indexed access:")
        println(kotlinCode)

        // Should use indexed delegate
        assertTrue(kotlinCode.contains("by MemoryByteIndexed"), "Should use MemoryByteIndexed delegate")
        assertTrue(kotlinCode.contains("enemyFlag["), "Should use array syntax")

        // Should NOT use memory[CONSTANT + x]
        assertFalse(kotlinCode.contains("memory[ENEMY_FLAG"), "Should not use memory[] for indexed access")
    }

    private fun assertFalse(condition: Boolean, message: String) {
        assertTrue(!condition, message)
    }
}
