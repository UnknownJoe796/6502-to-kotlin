package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Test
import java.io.File

class NestedIfStructureTest {

    @Test
    fun analyzeNestedIfCauses() {
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

        // Find the deeply nested IfNode sequence
        fun findDeepNesting(nodes: List<ControlNode>, depth: Int = 0): Int {
            var maxDepth = depth
            for (node in nodes) {
                when (node) {
                    is IfNode -> {
                        val thenDepth = findDeepNesting(node.thenBranch, depth + 1)
                        val elseDepth = findDeepNesting(node.elseBranch, depth + 1)
                        maxDepth = maxOf(maxDepth, thenDepth, elseDepth)
                    }
                    is LoopNode -> {
                        val bodyDepth = findDeepNesting(node.body, depth)
                        maxDepth = maxOf(maxDepth, bodyDepth)
                    }
                    else -> {}  // BlockNode, GotoNode, etc.
                }
            }
            return maxDepth
        }

        val maxNesting = findDeepNesting(controls)
        println("Maximum if nesting depth: $maxNesting")

        // Find the specific pattern - consecutive IfNodes in the same branch
        fun findConsecutiveIfs(nodes: List<ControlNode>, path: String = "root") {
            for ((i, node) in nodes.withIndex()) {
                when (node) {
                    is IfNode -> {
                        val condBlock = node.condition.branchBlock
                        val comparison = condBlock.findComparisonForBranch()

                        println("$path[$i]: IfNode testing block ${condBlock.label ?: "@${condBlock.originalLineIndex}"}")
                        if (comparison != null) {
                            println("  Comparison: ${comparison.originalLine}")
                        } else {
                            println("  Comparison: (not in same block)")
                        }
                        println("  Then branch has ${node.thenBranch.size} nodes")
                        println("  Else branch has ${node.elseBranch.size} nodes")

                        // Check if first node in then branch is also an IfNode
                        if (node.thenBranch.firstOrNull() is IfNode) {
                            val innerIf = node.thenBranch.first() as IfNode
                            val innerCondBlock = innerIf.condition.branchBlock
                            val innerComparison = innerCondBlock.findComparisonForBranch()
                            println("  >> First node in then is also IfNode testing block ${innerCondBlock.label ?: "@${innerCondBlock.originalLineIndex}"}")
                            if (innerComparison != null) {
                                println("     Inner comparison: ${innerComparison.originalLine}")
                                if (comparison != null && comparison.isSameComparisonAs(innerComparison)) {
                                    println("     ** SAME COMPARISON! Should be merged!")
                                }
                            }
                        }

                        findConsecutiveIfs(node.thenBranch, "$path[$i].then")
                        findConsecutiveIfs(node.elseBranch, "$path[$i].else")
                    }
                    is LoopNode -> {
                        findConsecutiveIfs(node.body, "$path[$i].loop")
                    }
                    else -> {}  // BlockNode, GotoNode, etc.
                }
            }
        }

        findConsecutiveIfs(controls.take(12))  // Only first 12 to avoid too much output
    }
}
