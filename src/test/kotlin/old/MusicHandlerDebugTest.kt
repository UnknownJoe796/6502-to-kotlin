package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Test
import java.io.File

class MusicHandlerDebugTest {

    @Test
    fun debugMusicHandlerControls() {
        // Parse the SMB disassembly
        val asmFile = File("smbdism.asm")
        val code = asmFile.readText().parseToAssemblyCodeFile()

        // Basic block and function analysis
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        // Find the HandleAreaMusicLoopB or LoadAreaMusic function
        val musicFunc = functions.find {
            it.startingBlock.label == "HandleAreaMusicLoopB" ||
            it.startingBlock.label == "LoadAreaMusic" ||
            it.startingBlock.label == "MusicHandler"
        }

        if (musicFunc == null) {
            println("Music handler function not found")
            println("Available functions around that area:")
            functions.filter { it.startingBlock.originalLineIndex in 15600..15700 }
                .forEach { println("  ${it.startingBlock.label} @ ${it.startingBlock.originalLineIndex}") }
            return
        }

        println("Found music function: ${musicFunc.startingBlock.label}")
        println("  Starting block: ${musicFunc.startingBlock.label} @ ${musicFunc.startingBlock.originalLineIndex}")

        // Find all blocks in function
        val funcBlocks = blocks.filter { it.function == musicFunc }
        println("  Blocks in function: ${funcBlocks.size}")
        funcBlocks.forEach {
            println("    - ${it.label ?: "@${it.originalLineIndex}"} (line ${it.originalLineIndex})")
        }

        // Find blocks that mention GroundMusicHeaderOfs
        val relevantBlocks = funcBlocks.filter { block ->
            block.lines.any { line ->
                line.originalLine?.contains("GroundMusicHeaderOfs") == true
            }
        }
        println("\n  Blocks mentioning GroundMusicHeaderOfs: ${relevantBlocks.size}")
        relevantBlocks.forEach { block ->
            println("    Block ${block.label ?: "@${block.originalLineIndex}"}:")
            block.lines.forEach { line ->
                if (line.originalLine?.contains("GroundMusicHeaderOfs") == true) {
                    println("      ${line.originalLine}")
                }
            }
            println("      FallThrough: ${block.fallThroughExit?.label}")
            println("      Branch: ${block.branchExit?.label}")
        }

        // Show control flow
        musicFunc.analyzeControls()
        println("\n  Control flow (original):")
        musicFunc.asControls?.forEachIndexed { i, node ->
            println("    [$i] ${node::class.simpleName}: ${(node as? BlockNode)?.block?.label ?: (node as? IfNode)?.condition?.branchBlock?.label}")
        }

        // Show improved control flow
        musicFunc.improveControlFlow()
        println("\n  Control flow (improved):")
        musicFunc.asControls?.forEachIndexed { i, node ->
            println("    [$i] ${node::class.simpleName}: ${(node as? BlockNode)?.block?.label ?: (node as? IfNode)?.condition?.branchBlock?.label}")
        }
    }
}
