package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Test
import java.io.File

class GroundMusicDuplicateTest {

    @Test
    fun analyzeGroundMusicDuplicates() {
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

        // Analyze control flow
        val controls = musicFunc.improveControlFlow()

        // Find ALL IfNodes that test "cpy #$32" (GroundMusicHeaderOfs)
        val groundMusicTests = mutableListOf<Pair<String, IfNode>>()

        fun findTests(nodes: List<ControlNode>, path: String) {
            for ((i, node) in nodes.withIndex()) {
                when (node) {
                    is IfNode -> {
                        val flagSetter = node.condition.branchBlock.findFlagSetterForBranch()
                        if (flagSetter?.originalLine?.contains("cpy #\$32") == true) {
                            groundMusicTests.add(Pair("$path[$i]", node))
                        }
                        findTests(node.thenBranch, "$path[$i].then")
                        findTests(node.elseBranch, "$path[$i].else")
                    }
                    is LoopNode -> {
                        findTests(node.body, "$path[$i].loop")
                    }
                    else -> {}
                }
            }
        }

        findTests(controls, "root")

        println("Found ${groundMusicTests.size} tests of 'cpy #\$32':")
        groundMusicTests.forEach { (path, node) ->
            val block = node.condition.branchBlock
            println("  At $path:")
            println("    Block: ${block.label ?: "@${block.originalLineIndex}"}")
            println("    Sense: ${node.condition.sense}")
            println("    Then has ${node.thenBranch.size} nodes, else has ${node.elseBranch.size} nodes")

            // Check what's in the then branch
            if (node.thenBranch.isNotEmpty()) {
                val firstInThen = node.thenBranch.first()
                println("    First in then: ${firstInThen::class.simpleName}")
                if (firstInThen is IfNode) {
                    val innerFlagSetter = firstInThen.condition.branchBlock.findFlagSetterForBranch()
                    println("      Tests: ${innerFlagSetter?.originalLine?.trim()}")
                }
            }
        }

        // Check if any are duplicates that should have been merged
        println("\nLooking for mergeable patterns...")
        for ((path, node) in groundMusicTests) {
            if (node.thenBranch.size >= 1 && node.thenBranch.first() is IfNode && node.elseBranch.isEmpty()) {
                val innerIf = node.thenBranch.first() as IfNode
                val outerSetter = node.condition.branchBlock.findFlagSetterForBranch()
                val innerSetter = innerIf.condition.branchBlock.findFlagSetterForBranch()

                if (outerSetter != null && innerSetter != null) {
                    val same = outerSetter.isSameComparisonAs(innerSetter)
                    println("At $path:")
                    println("  Outer: ${outerSetter.originalLine?.trim()}")
                    println("  Inner: ${innerSetter.originalLine?.trim()}")
                    println("  Same? $same")
                    if (same) {
                        println("  ** SHOULD HAVE BEEN MERGED **")
                    }
                }
            }
        }
    }
}
