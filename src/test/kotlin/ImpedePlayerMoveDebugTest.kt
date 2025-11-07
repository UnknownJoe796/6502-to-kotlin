package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Test
import java.io.File

class ImpedePlayerMoveDebugTest {

    @Test
    fun debugImpedePlayerMove() {
        // Parse the SMB disassembly
        val asmFile = File("smbdism.asm")
        val code = asmFile.readText().parseToAssemblyCodeFile()

        // Basic block and function analysis
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        // Find the ImpedePlayerMove function
        val impedeFunc = functions.find { it.startingBlock.label == "ImpedePlayerMove" }

        if (impedeFunc == null) {
            println("ImpedePlayerMove not found")
            return
        }

        val outFile = File("outputs/impede-debug.txt")
        outFile.parentFile?.mkdirs()
        val out = outFile.bufferedWriter()

        out.appendLine("ImpedePlayerMove function:")
        out.appendLine("  Starting block: ${impedeFunc.startingBlock.label}")
        out.appendLine("  Starting block line: ${impedeFunc.startingBlock.originalLineIndex}")

        // Show all blocks in function
        val funcBlocks = blocks.filter { it.function == impedeFunc }
        out.appendLine("\nBlocks in function (${funcBlocks.size}):")
        funcBlocks.forEach { block ->
            out.appendLine("  Block: ${block.label ?: "@${block.originalLineIndex}"}")
            out.appendLine("    Lines (${block.lines.size}):")
            block.lines.forEach { line ->
                out.appendLine("      ${line.originalLine?.trim()}")
            }
        }

        // Get control flow
        out.appendLine("\nControl flow (improveControlFlow):")
        val controls = impedeFunc.improveControlFlow()

        fun printNode(node: ControlNode, indent: Int = 2) {
            val prefix = "  ".repeat(indent)
            when (node) {
                is BlockNode -> {
                    out.appendLine("$prefix- Block: ${node.block.label ?: "@${node.block.originalLineIndex}"}")
                    out.appendLine("$prefix  Lines: ${node.block.lines.size}")
                    node.block.lines.take(3).forEach {
                        out.appendLine("$prefix    ${it.originalLine?.trim()}")
                    }
                }
                is IfNode -> {
                    val condBlock = node.condition.branchBlock
                    out.appendLine("$prefix- If (block ${condBlock.label ?: "@${condBlock.originalLineIndex}"})")
                    out.appendLine("$prefix    Then (${node.thenBranch.size} nodes):")
                    node.thenBranch.forEach { printNode(it, indent + 2) }
                    if (node.elseBranch.isNotEmpty()) {
                        out.appendLine("$prefix    Else (${node.elseBranch.size} nodes):")
                        node.elseBranch.forEach { printNode(it, indent + 2) }
                    }
                }
                is LoopNode -> {
                    out.appendLine("$prefix- Loop (${node.kind}):")
                    node.body.forEach { printNode(it, indent + 1) }
                }
                else -> out.appendLine("$prefix- ${node::class.simpleName}")
            }
        }

        controls.forEach { printNode(it) }

        // Generate Kotlin
        out.appendLine("\nGenerated Kotlin:")
        val kFunc = impedeFunc.toKotlinFunction()
        out.appendLine(kFunc.toKotlin())

        out.close()
        println("Output written to: ${outFile.absolutePath}")
    }
}
