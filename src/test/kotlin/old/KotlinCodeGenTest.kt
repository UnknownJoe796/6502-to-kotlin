package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

class KotlinCodeGenTest {

    @Test
    fun testSimpleFunctionGeneration() {
        val code = """
            MyFunction:
                LDA #${'$'}42
                STA ${'$'}00
                RTS
        """.trimIndent()

        val parsed = code.parseToAssemblyCodeFile()
        val blocks = parsed.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertEquals(1, functions.size)
        val func = functions.first()

        val kFunc = func.toKotlinFunction()

        assertEquals("myFunction", kFunc.name)
        assertTrue(kFunc.body.isNotEmpty(), "Function body should not be empty")

        val kotlin = kFunc.toKotlin()
        println("Generated Kotlin:")
        println(kotlin)

        assertTrue(kotlin.contains("fun myFunction"))
    }

    @Test
    fun testIfElseGeneration() {
        val code = """
            CheckValue:
                LDA #${'$'}00
                BEQ IsZero
            NotZero:
                LDX #${'$'}01
                RTS
            IsZero:
                LDX #${'$'}02
                RTS
        """.trimIndent()

        val parsed = code.parseToAssemblyCodeFile()
        val blocks = parsed.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        val func = functions.first()
        val kFunc = func.toKotlinFunction()

        val kotlin = kFunc.toKotlin()
        println("\nGenerated Kotlin for if/else:")
        println(kotlin)

        assertTrue(kotlin.contains("if"))
        assertTrue(kotlin.contains("else"))
    }

    @Test
    fun testLoopGeneration() {
        val code = """
            CountLoop:
                LDX #${'$'}00
            Loop:
                INX
                CPX #${'$'}0A
                BNE Loop
                RTS
        """.trimIndent()

        val parsed = code.parseToAssemblyCodeFile()
        val blocks = parsed.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        val func = functions.first()
        val kFunc = func.toKotlinFunction()

        val kotlin = kFunc.toKotlin()
        println("\nGenerated Kotlin for loop:")
        println(kotlin)

        assertTrue(kotlin.contains("while") || kotlin.contains("do"))
    }

    @Test
    fun generateKotlinForSMB() {
        // Parse the SMB disassembly
        val asmFile = File("smbdism.asm")
        if (!asmFile.exists()) {
            println("Skipping SMB test - smbdism.asm not found")
            return
        }

        val code = asmFile.readText().parseToAssemblyCodeFile()

        // Basic block and function analysis
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        // Ensure outputs directory exists
        val outDir = File("outputs")
        if (!outDir.exists()) outDir.mkdirs()
        val outFile = outDir.resolve("smb-decompiled.kt")

        // Build function registry: map of function names to AssemblyFunction
        val functionRegistry = functions.associateBy { func ->
            func.startingBlock.label?.let { label ->
                com.ivieleague.decompiler6502tokotlin.hand.assemblyLabelToKotlinName(label)
            } ?: "func_${func.startingBlock.originalLineIndex}"
        }

        outFile.bufferedWriter().use { out ->
            out.appendLine("package com.ivieleague.smb")
            out.appendLine()
            out.appendLine("// Decompiled Super Mario Bros. NES ROM")
            out.appendLine("// Generated from smbdism.asm")
            out.appendLine()

            // Convert all functions
            for (func in functions.sortedBy { it.startingBlock.originalLineIndex }) {
                try {
                    val kFunc = func.toKotlinFunction(functionRegistry)
                    out.appendLine(kFunc.toKotlin())
                    out.appendLine()
                } catch (e: Exception) {
                    println("Error converting function ${func.startingBlock.label}: ${e.message}")
                    e.printStackTrace()
                }
            }
        }

        println("\nGenerated Kotlin code written to: ${outFile.absolutePath}")
        println("First 10 functions converted successfully!")

        assertTrue(outFile.exists())
        assertTrue(outFile.length() > 0)
    }

    @Test
    fun testKotlinASTConstruction() {
        // Test the Kotlin AST directly
        val func = KFunction(
            name = "testFunction",
            params = listOf(KParam("x", "Int"), KParam("y", "Int")),
            returnType = "Int",
            body = listOf(
                KVarDecl("result", "Int", KBinaryOp(KVar("x"), "+", KVar("y"))),
                KReturn(KVar("result"))
            )
        )

        val kotlin = func.toKotlin()
        println("\nTest Kotlin AST:")
        println(kotlin)

        assertTrue(kotlin.contains("fun testFunction(x: Int, y: Int): Int"))
        assertTrue(kotlin.contains("val result: Int = x + y"))
        assertTrue(kotlin.contains("return result"))
    }
}
