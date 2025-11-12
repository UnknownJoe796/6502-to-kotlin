package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetAreaObjXPositionReturnTest {
    @Test
    fun `GetAreaObjXPosition should have Int return type`() {
        // Parse and build the assembly
        val code = File("smbdism.asm").readText().parseToAssemblyCodeFile()
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        // Find the GetAreaObjXPosition function
        val function = functions.find { it.startingBlock.label == "GetAreaObjXPosition" }
        require(function != null) { "GetAreaObjXPosition function not found" }

        // Convert to Kotlin function
        val functionRegistry = functions.associate {
            val name = it.startingBlock.label?.let { label -> assemblyLabelToKotlinName(label) }
                ?: "func_${it.startingBlock.originalLineIndex}"
            name to it
        }
        val kFunction = function.toKotlinFunction(functionRegistry)

        println("Generated function:")
        println(kFunction.toKotlin())
        println()

        // Verify return type is detected
        assertEquals("Int", kFunction.returnType, "GetAreaObjXPosition should return Int")

        // Verify the function body contains a return statement with A
        val hasReturnA = kFunction.body.any { stmt ->
            stmt is KReturn && stmt.value?.toKotlin()?.contains("A") == true
        }
        assertTrue(hasReturnA, "Function should have 'return A' statement")
    }

    @Test
    fun `GetAreaObjYPosition should have Int return type`() {
        // Parse and build the assembly
        val code = File("smbdism.asm").readText().parseToAssemblyCodeFile()
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        // Find the GetAreaObjYPosition function
        val function = functions.find { it.startingBlock.label == "GetAreaObjYPosition" }
        require(function != null) { "GetAreaObjYPosition function not found" }

        // Convert to Kotlin function
        val functionRegistry = functions.associate {
            val name = it.startingBlock.label?.let { label -> assemblyLabelToKotlinName(label) }
                ?: "func_${it.startingBlock.originalLineIndex}"
            name to it
        }
        val kFunction = function.toKotlinFunction(functionRegistry)

        println("Generated function:")
        println(kFunction.toKotlin())
        println()

        // Verify return type is detected
        assertEquals("Int", kFunction.returnType, "GetAreaObjYPosition should return Int")
    }
}
