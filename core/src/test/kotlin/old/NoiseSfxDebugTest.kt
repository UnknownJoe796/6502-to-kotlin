package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Test
import java.io.File

class NoiseSfxDebugTest {

    @Test
    fun debugNoiseSfxHandler() {
        // Parse the SMB disassembly
        val asmFile = File("smbdism.asm")
        val code = asmFile.readText().parseToAssemblyCodeFile()

        // Basic block and function analysis
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        // Find the NoiseSfxHandler function
        val noiseFunc = functions.find { it.startingBlock.label == "NoiseSfxHandler" }

        if (noiseFunc == null) {
            println("NoiseSfxHandler not found")
            return
        }

        val outFile = File("outputs/noisesfx-debug.txt")
        outFile.parentFile?.mkdirs()
        val out = outFile.bufferedWriter()

        out.appendLine("NoiseSfxHandler function:")
        out.appendLine("  Starting block: ${noiseFunc.startingBlock.label}")

        // Find all blocks in this function
        val funcBlocks = blocks.filter { it.function == noiseFunc }
        out.appendLine("  Blocks in function (${funcBlocks.size}):")
        funcBlocks.forEach { block ->
            val hasRts = block.lines.any { it.instruction?.op == AssemblyOp.RTS }
            val rtsMarker = if (hasRts) " [HAS RTS]" else ""
            out.appendLine("    - ${block.label ?: "@${block.originalLineIndex}"}$rtsMarker")

            // Show last few lines
            block.lines.takeLast(3).forEach { line ->
                out.appendLine("        ${line.originalLine?.trim()}")
            }
        }

        // Check reachability from starting block
        out.appendLine("\n  Starting block for control flow: ${noiseFunc.startingBlock.label}")
        out.appendLine("  Starting block line index: ${noiseFunc.startingBlock.originalLineIndex}")

        // Check which blocks are actually reachable (within this function only)
        val reachableInFunc = mutableSetOf<AssemblyBlock>()
        fun walkInFunc(block: AssemblyBlock) {
            if (block.function != noiseFunc) return  // Don't cross function boundaries
            if (!reachableInFunc.add(block)) return
            block.fallThroughExit?.let { walkInFunc(it) }
            block.branchExit?.let { walkInFunc(it) }
        }
        walkInFunc(noiseFunc.startingBlock)
        out.appendLine("  Reachable blocks from starting block (within function): ${reachableInFunc.size}")
        out.appendLine("  Assigned blocks: ${funcBlocks.size}")
        out.appendLine("  Unreachable assigned blocks: ${funcBlocks.size - reachableInFunc.size}")

        if (reachableInFunc.size < funcBlocks.size) {
            out.appendLine("\n  Unreachable blocks assigned to this function:")
            funcBlocks.filter { it !in reachableInFunc }.forEach { block ->
                val hasRts = block.lines.any { it.instruction?.op == AssemblyOp.RTS }
                val rtsMarker = if (hasRts) " [HAS RTS]" else ""
                out.appendLine("    - ${block.label ?: "@${block.originalLineIndex}"}$rtsMarker")
            }
        }

        // Check both control flow methods
        out.appendLine("\n  Control flow (analyzeControls):")
        val controlsBasic = noiseFunc.analyzeControls()
        out.appendLine("  Nodes: ${controlsBasic.size}")

        fun printControls(nodes: List<ControlNode>, indent: Int = 2) {
            val prefix = "  ".repeat(indent)
            for (node in nodes) {
                when (node) {
                    is BlockNode -> {
                        val hasReturn = node.block.lines.any { it.instruction?.op == AssemblyOp.RTS }
                        val marker = if (hasReturn) " [RETURN]" else ""
                        out.appendLine("$prefix- Block: ${node.block.label ?: "@${node.block.originalLineIndex}"}$marker")
                    }
                    is IfNode -> {
                        out.appendLine("$prefix- If:")
                        if (node.thenBranch.isNotEmpty()) {
                            out.appendLine("$prefix    Then:")
                            printControls(node.thenBranch, indent + 2)
                        }
                        if (node.elseBranch.isNotEmpty()) {
                            out.appendLine("$prefix    Else:")
                            printControls(node.elseBranch, indent + 2)
                        }
                    }
                    is LoopNode -> {
                        out.appendLine("$prefix- Loop (${node.kind}):")
                        printControls(node.body, indent + 1)
                    }
                    else -> {
                        out.appendLine("$prefix- ${node::class.simpleName}")
                    }
                }
            }
        }

        printControls(controlsBasic)

        out.appendLine("\n  Control flow (improveControlFlow):")
        val controls = noiseFunc.improveControlFlow()
        out.appendLine("  Nodes: ${controls.size}")

        printControls(controls)
        out.close()
        println("Output written to: ${outFile.absolutePath}")
    }
}
