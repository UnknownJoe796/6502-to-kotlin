package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Test
import java.io.File

class AllIfNodesTest {

    @Test
    fun printAllIfNodes() {
        // Parse the SMB disassembly
        val asmFile = File("smbdism.asm")
        val code = asmFile.readText().parseToAssemblyCodeFile()

        // Basic block and function analysis
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        // Find the MusicHandler function
        val musicFunc = functions.find { it.startingBlock.label == "MusicHandler" }

        if (musicFunc == null) {
            println("MusicHandler not found")
            return
        }

        // Get improved control flow
        val controls = musicFunc.improveControlFlow()

        val outFile = File("outputs/all-if-nodes.txt")
        outFile.parentFile?.mkdirs()
        val out = outFile.bufferedWriter()

        out.appendLine("Printing ALL if nodes in MusicHandler control flow:")

        var count = 0
        fun printIf(nodes: List<ControlNode>, indent: Int = 0) {
            val prefix = "  ".repeat(indent)
            for (node in nodes) {
                when (node) {
                    is IfNode -> {
                        count++
                        val block = node.condition.branchBlock
                        val flagSetter = block.findFlagSetterForBranch()

                        out.appendLine("${prefix}[$count] IfNode:")
                        out.appendLine("$prefix  Branch block: ${block.label ?: "@${block.originalLineIndex}"}")
                        out.appendLine("$prefix  Sense: ${node.condition.sense}")
                        out.appendLine("$prefix  Flag setter: ${flagSetter?.originalLine?.trim() ?: "null"}")

                        // Also print the generated Kotlin condition
                        val ctx = CodeGenContext()
                        val kotlinCond = node.condition.toKotlinExpr(ctx).toKotlin()
                        out.appendLine("$prefix  Kotlin condition: $kotlinCond")

                        out.appendLine("$prefix  Then branch has ${node.thenBranch.size} nodes, else has ${node.elseBranch.size} nodes")

                        if (node.thenBranch.isNotEmpty()) {
                            out.appendLine("$prefix  Then:")
                            printIf(node.thenBranch, indent + 2)
                        }
                        if (node.elseBranch.isNotEmpty()) {
                            out.appendLine("$prefix  Else:")
                            printIf(node.elseBranch, indent + 2)
                        }
                    }
                    is LoopNode -> {
                        out.appendLine("${prefix}LoopNode (${node.kind}):")
                        printIf(node.body, indent + 1)
                    }
                    else -> {}
                }
            }
        }

        printIf(controls)
        out.appendLine("\nTotal if nodes: $count")
        out.close()

        println("Output written to: ${outFile.absolutePath}")
    }
}
