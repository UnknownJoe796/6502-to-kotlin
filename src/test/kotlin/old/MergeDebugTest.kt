package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Test
import java.io.File

class MergeDebugTest {

    @Test
    fun debugMergeLogic() {
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

        // Get control flow BEFORE merging
        val beforeMerge = musicFunc.analyzeControls()

        // Apply just the merge pass
        println("=== BEFORE MERGE ===")
        printStructure(beforeMerge.take(8), 0)

        val afterMerge = beforeMerge.mergeConsecutiveSameComparison()

        println("\n=== AFTER MERGE ===")
        printStructure(afterMerge.take(8), 0)

        // Check if anything changed
        val beforeSize = countNodes(beforeMerge)
        val afterSize = countNodes(afterMerge)
        println("\nBefore merge: $beforeSize nodes")
        println("After merge: $afterSize nodes")
        println("Change: ${beforeSize - afterSize} nodes removed")
    }

    fun countNodes(nodes: List<ControlNode>): Int {
        var count = nodes.size
        for (node in nodes) {
            when (node) {
                is IfNode -> {
                    count += countNodes(node.thenBranch)
                    count += countNodes(node.elseBranch)
                }
                is LoopNode -> {
                    count += countNodes(node.body)
                }
                else -> {}
            }
        }
        return count
    }

    fun printStructure(nodes: List<ControlNode>, indent: Int) {
        val prefix = "  ".repeat(indent)
        for ((i, node) in nodes.withIndex()) {
            when (node) {
                is BlockNode -> {
                    println("$prefix[$i] Block: ${node.block.label ?: "@${node.block.originalLineIndex}"}")
                }
                is IfNode -> {
                    val condBlock = node.condition.branchBlock
                    val comparison = condBlock.findComparisonForBranch()
                    val compStr = comparison?.originalLine?.trim() ?: "?"

                    // Debug: why is comparison null?
                    if (comparison == null && indent < 3) {
                        println("$prefix[$i] If (${condBlock.label ?: "@${condBlock.originalLineIndex}"}) [NO COMP FOUND]")
                        println("$prefix    Block has ${condBlock.lines.size} lines, ${condBlock.enteredFrom.size} predecessors")
                        condBlock.lines.take(5).forEach { println("$prefix      ${it.originalLine?.trim()}") }
                    } else {
                        println("$prefix[$i] If (${condBlock.label ?: "@${condBlock.originalLineIndex}"}) [$compStr]")
                    }

                    if (node.thenBranch.isNotEmpty()) {
                        println("$prefix    Then:")
                        printStructure(node.thenBranch, indent + 2)
                    }
                    if (node.elseBranch.isNotEmpty()) {
                        println("$prefix    Else:")
                        printStructure(node.elseBranch, indent + 2)
                    }
                }
                is LoopNode -> {
                    println("$prefix[$i] Loop: ${node.kind}")
                    printStructure(node.body, indent + 1)
                }
                else -> {
                    println("$prefix[$i] ${node::class.simpleName}")
                }
            }
        }
    }
}
