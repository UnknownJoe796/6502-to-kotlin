package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Test
import java.io.File

class NestedIfDebugTest {

    @Test
    fun debugNestedIfStructure() {
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

        println("MusicHandler function:")

        // Get improved control flow
        val controls = musicFunc.improveControlFlow()

        // Print the structure with nesting
        fun printNode(node: ControlNode, indent: Int = 0) {
            val prefix = "  ".repeat(indent)
            when (node) {
                is BlockNode -> {
                    println("$prefix BlockNode: ${node.block.label ?: "@${node.block.originalLineIndex}"}")
                }
                is IfNode -> {
                    val condBlock = node.condition.branchBlock
                    val sense = node.condition.sense
                    println("$prefix IfNode: block ${condBlock.label ?: "@${condBlock.originalLineIndex}"} sense=$sense")
                    println("$prefix   Then:")
                    node.thenBranch.forEach { printNode(it, indent + 2) }
                    if (node.elseBranch.isNotEmpty()) {
                        println("$prefix   Else:")
                        node.elseBranch.forEach { printNode(it, indent + 2) }
                    }
                }
                is LoopNode -> {
                    println("$prefix LoopNode: ${node.kind}")
                    node.body.forEach { printNode(it, indent + 1) }
                }
                else -> {
                    println("$prefix ${node::class.simpleName}")
                }
            }
        }

        controls.take(10).forEach { printNode(it) }
    }
}
