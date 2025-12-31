package com.ivieleague.decompiler6502tokotlin.validation

import com.ivieleague.decompiler6502tokotlin.hand.*
import kotlin.test.Test

/**
 * Debug test to see what's being generated
 */
class DebugValidationTest {

    @Test
    fun debugMultipleBranches() {
        val asm = """
            TestFunc:
                LDA ${'$'}0200
                CMP #${'$'}00
                BEQ IsZero
                CMP #${'$'}01
                BEQ IsOne
                LDA #${'$'}FF
                STA ${'$'}0201
                RTS
            IsZero:
                LDA #${'$'}00
                STA ${'$'}0201
                RTS
            IsOne:
                LDA #${'$'}11
                STA ${'$'}0201
                RTS
        """.trimIndent()

        println("=== Input Assembly ===")
        println(asm)

        val parsed = asm.parseToAssemblyCodeFile()
        println("\n=== Parsed Lines ===")
        parsed.lines.forEachIndexed { i, line ->
            println("  [$i] $line")
        }

        val blocks = parsed.lines.blockify()
        println("\n=== Blocks ===")
        blocks.forEach { block ->
            println("  Block ${block.label}")
        }

        blocks.dominators()
        val functions = blocks.functionify()
        val function = functions.first()

        println("\n=== Generated Kotlin Code ===")
        val kotlinFunction = function.toKotlinFunction()
        println(kotlinFunction.toKotlin())
    }
}
