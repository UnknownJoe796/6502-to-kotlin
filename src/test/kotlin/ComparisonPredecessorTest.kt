package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Test
import java.io.File

class ComparisonPredecessorTest {

    @Test
    fun debugComparisonDetection() {
        // Parse the SMB disassembly
        val asmFile = File("smbdism.asm")
        val code = asmFile.readText().parseToAssemblyCodeFile()

        // Basic block and function analysis
        val blocks = code.lines.blockify()
        blocks.dominators()

        // Find block @15649 that has the cpy #$32 pattern
        val block15649 = blocks.find { it.originalLineIndex == 15649 }

        if (block15649 == null) {
            println("Block @15649 not found")
            return
        }

        println("=== Block @15649 ===")
        block15649.lines.forEach { line ->
            println("  ${line.originalLine}")
        }
        println()

        println("Predecessors:")
        block15649.enteredFrom.forEach { pred ->
            println("  ${pred.label ?: "@${pred.originalLineIndex}"}")
            pred.lines.takeLast(3).forEach { line ->
                println("    ${line.originalLine}")
            }
        }
        println()

        println("Attempting to find comparison for this block:")
        val comparison = block15649.findComparisonForBranch()
        if (comparison != null) {
            println("  Found: ${comparison.originalLine}")
        } else {
            println("  NOT FOUND")
        }
    }
}
