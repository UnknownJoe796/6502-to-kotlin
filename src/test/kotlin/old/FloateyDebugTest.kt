package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Test
import java.io.File

class FloateyDebugTest {

    @Test
    fun debugFloateyNumbersRoutine() {
        // Parse the SMB disassembly
        val asmFile = File("smbdism.asm")
        val code = asmFile.readText().parseToAssemblyCodeFile()

        // Basic block and function analysis
        val blocks = code.lines.blockify()
        blocks.dominators()

        // Find the FloateyNumbersRoutine block
        val floateyBlock = blocks.find { it.label == "FloateyNumbersRoutine" }
        println("FloateyNumbersRoutine block found: $floateyBlock")
        println("  Index: ${floateyBlock?.originalLineIndex}")
        println("  Lines: ${floateyBlock?.lines?.size}")
        println("  First line: ${floateyBlock?.lines?.firstOrNull()?.label}")
        println("  FallThrough: ${floateyBlock?.fallThroughExit?.label}")
        println("  Branch: ${floateyBlock?.branchExit?.label}")

        // Find EndExitOne block
        val endExitBlock = blocks.find { it.label == "EndExitOne" }
        println("\nEndExitOne block found: $endExitBlock")
        println("  Index: ${endExitBlock?.originalLineIndex}")
        println("  Lines: ${endExitBlock?.lines?.size}")
        println("  FallThrough: ${endExitBlock?.fallThroughExit?.label}")
        println("  Branch: ${endExitBlock?.branchExit?.label}")

        // Functionify
        val functions = blocks.functionify()

        // Find FloateyNumbersRoutine function
        val floateyFunc = functions.find { it.startingBlock.label == "FloateyNumbersRoutine" }
        println("\nFloateyNumbersRoutine function:")
        println("  Starting block: ${floateyFunc?.startingBlock?.label}")
        println("  Callers: ${floateyFunc?.callers?.size}")

        // Find all blocks in function
        val funcBlocks = blocks.filter { it.function == floateyFunc }
        println("  Blocks in function: ${funcBlocks.size}")
        funcBlocks.forEach {
            println("    - ${it.label ?: "@${it.originalLineIndex}"}")
        }

        // Show control flow
        floateyFunc?.analyzeControls()
        println("\n  Control flow (original):")
        floateyFunc?.asControls?.forEach { node ->
            println("    ${node::class.simpleName}: ${(node as? BlockNode)?.block?.label}")
        }

        // Show improved control flow
        floateyFunc?.improveControlFlow()
        println("\n  Control flow (improved):")
        floateyFunc?.asControls?.forEach { node ->
            println("    ${node::class.simpleName}: ${(node as? BlockNode)?.block?.label}")
        }
    }
}
