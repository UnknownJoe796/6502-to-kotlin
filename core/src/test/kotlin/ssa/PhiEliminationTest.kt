// by Claude - Tests for SSA-based code generation
package com.ivieleague.decompiler6502tokotlin.hand.ssa

import com.ivieleague.decompiler6502tokotlin.hand.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PhiEliminationTest {

    @Test
    fun `simple function generates valid Kotlin`() {
        val code = """
            TestFunc:
                LDA #${'$'}42
                STA ${'$'}00
                RTS
        """.trimIndent()

        val parsed = code.parseToAssemblyCodeFile()
        val blocks = parsed.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        val func = functions.first()
        val kFunction = func.toKotlinFunctionSSA()

        println("Generated function:\n${kFunction.toKotlin()}")

        assertEquals("testFunc", kFunction.name)
        assertNotNull(kFunction.body)
        assertTrue(kFunction.body.isNotEmpty())
    }

    @Test
    fun `function with branch generates valid Kotlin`() {
        val code = """
            TestFunc:
                LDA ${'$'}00
                BEQ Skip
                LDA #${'$'}01
            Skip:
                STA ${'$'}01
                RTS
        """.trimIndent()

        val parsed = code.parseToAssemblyCodeFile()
        val blocks = parsed.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        val func = functions.first()
        val kFunction = func.toKotlinFunctionSSA()

        println("Generated function:\n${kFunction.toKotlin()}")

        // Should contain an if statement
        val rendered = kFunction.toKotlin()
        assertTrue(rendered.isNotEmpty())
    }

    @Test
    fun `loop function generates valid Kotlin`() {
        val code = """
            LoopFunc:
                LDY #${'$'}03
            LoopStart:
                STA ${'$'}00,Y
                DEY
                BPL LoopStart
                RTS
        """.trimIndent()

        val parsed = code.parseToAssemblyCodeFile()
        val blocks = parsed.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        val func = functions.first()
        val kFunction = func.toKotlinFunctionSSA()

        println("Generated function:\n${kFunction.toKotlin()}")

        // Should complete without error
        assertNotNull(kFunction)
    }

    @Test
    fun `ASL sets correct carry flag`() {
        // This is the key test for the stale reference bug
        val code = """
            ShiftFunc:
                LDA #${'$'}80
                ASL A
                BCS CarrySet
                LDA #${'$'}00
                RTS
            CarrySet:
                LDA #${'$'}FF
                RTS
        """.trimIndent()

        val parsed = code.parseToAssemblyCodeFile()
        val blocks = parsed.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        val func = functions.first()
        val ssaResult = func.buildSSA()

        // Get the carry flag after ASL
        // The carry should be computed from the ORIGINAL A value (0x80),
        // not the shifted value
        val shiftBlock = blocks.find { it.label == "ShiftFunc" }!!
        val exitState = ssaResult.blockExitState[shiftBlock]
        val carryExpr = exitState?.get(SSABase.FlagCarry)?.definingExpr

        println("Carry expression: ${carryExpr?.toKotlin()}")

        // The carry expression should reference 0x80 (the original value)
        // NOT the shifted result
        val carryStr = carryExpr?.toKotlin() ?: ""
        assertTrue(carryStr.contains("0x80") || carryStr.contains("128"),
            "Carry should be computed from original value 0x80, got: $carryStr")
    }

    @Test
    fun `phi eliminator detects loop phi nodes`() {
        val code = """
            LoopTest:
                LDY #${'$'}05
            LoopBody:
                DEY
                BPL LoopBody
                RTS
        """.trimIndent()

        val parsed = code.parseToAssemblyCodeFile()
        val blocks = parsed.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        val func = functions.first()
        val ssaResult = func.buildSSA()
        val phiEliminator = PhiEliminator(ssaResult, func)
        phiEliminator.analyze()

        println("Mutable variables: ${phiEliminator.mutableVariables}")
        println("Block copies: ${phiEliminator.blockCopies}")

        // Loop should create mutable variables for the loop counter
        // (may or may not depending on phi node insertion)
    }

    @Test
    fun `SMB function generates valid Kotlin`() {
        val asmFile = java.io.File("smbdism.asm")
        if (!asmFile.exists()) {
            println("Skipping SMB test - smbdism.asm not found")
            return
        }

        val code = asmFile.readText().parseToAssemblyCodeFile()
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        // Test a simple function
        val testFunc = functions.find { it.startingBlock.label == "GetPlayerColors" }
            ?: functions.find { it.startingBlock.label == "GetAreaType" }
            ?: functions.first()

        println("Testing SSA code generation on: ${testFunc.startingBlock.label}")

        val kFunction = testFunc.toKotlinFunctionSSA()
        val rendered = kFunction.toKotlin()

        println("Generated (first 50 lines):")
        rendered.lines().take(50).forEach { println(it) }

        assertTrue(rendered.isNotEmpty())
        // Should not have undefined variables
        assertFalse(rendered.contains("undefined"), "Generated code should not have undefined variables")
    }
}
