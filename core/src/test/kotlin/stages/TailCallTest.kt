package com.ivieleague.decompiler6502tokotlin.hand.stages

import com.ivieleague.decompiler6502tokotlin.hand.*
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Tests that the decompiler generates tail calls when a function
 * falls through to another function's entry point.
 */
class TailCallTest {

    @Test
    fun `ReadJoypads should have tail call to ReadPortBits`() {
        // Use the test fixtures to get the function
        val readJoypads = try {
            SMBTestFixtures.loadFunction("ReadJoypads")
        } catch (e: Exception) {
            println("Skipping: ${e.message}")
            return
        }

        println("ReadJoypads function found")
        println("  Starting block: ${readJoypads.startingBlock.label}")

        // Find all blocks in the function
        val functionBlocks = SMBTestFixtures.getFunctionBlocks("ReadJoypads")
        println("  Function has ${functionBlocks.size} blocks")

        // Check for fall-through to another function
        var foundFallThrough = false
        var targetFunctionName: String? = null
        for (block in functionBlocks) {
            val fallThrough = block.fallThroughExit
            if (fallThrough != null && fallThrough.function != readJoypads) {
                println("  Block ${block.label ?: "@${block.originalLineIndex}"} falls through to ${fallThrough.label ?: "@${fallThrough.originalLineIndex}"}")
                println("  Target function: ${fallThrough.function?.startingBlock?.label}")
                targetFunctionName = fallThrough.function?.startingBlock?.label
                foundFallThrough = true
            }
        }

        assertTrue(foundFallThrough, "ReadJoypads should fall through to ReadPortBits")

        // Generate Kotlin code
        val functionRegistry = SMBTestFixtures.allFunctions.associateBy {
            it.startingBlock.label ?: "func_${it.startingBlock.originalLineIndex}"
        }
        val kotlinFunc = readJoypads.toKotlinFunction(functionRegistry)

        println("\nGenerated Kotlin:")
        val code = kotlinFunc.toKotlin()
        println(code)

        // Check that the code contains a call to the target function (readPortBits)
        if (targetFunctionName != null) {
            val expectedCall = assemblyLabelToKotlinName(targetFunctionName)
            assertTrue(code.contains("$expectedCall("),
                "Generated code should call $expectedCall as a tail call. Got:\n$code")
        }
    }
}
