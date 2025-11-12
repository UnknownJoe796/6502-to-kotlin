package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Test
import java.io.File

class ComparisonTrackingTest {

    @Test
    fun analyzeGroundMusicHeaderOfsComparisons() {
        // Parse the SMB disassembly
        val asmFile = File("smbdism.asm")
        val code = asmFile.readText().parseToAssemblyCodeFile()

        // Basic block and function analysis
        val blocks = code.lines.blockify()
        blocks.dominators()

        // Find blocks around line 15649 that test GroundMusicHeaderOfs
        val relevantBlocks = blocks.filter { it.originalLineIndex in 15640..15660 }

        println("Blocks around GroundMusicHeaderOfs comparison:")
        relevantBlocks.forEach { block ->
            println("\n=== Block ${block.label ?: "@${block.originalLineIndex}"} ===")
            block.lines.forEach { line ->
                println("  ${line.originalLine}")
            }
            println("  -> FallThrough: ${block.fallThroughExit?.label ?: "@${block.fallThroughExit?.originalLineIndex}"}")
            println("  -> Branch: ${block.branchExit?.label ?: "@${block.branchExit?.originalLineIndex}"}")

            // Check for comparison instructions
            val hasComparison = block.lines.any { line ->
                val op = line.instruction?.op
                op == AssemblyOp.CMP || op == AssemblyOp.CPX || op == AssemblyOp.CPY
            }
            if (hasComparison) {
                println("  ** Contains comparison instruction")
            }

            // Check for branch instructions
            val hasBranch = block.lines.any { line ->
                line.instruction?.op?.isBranch == true
            }
            if (hasBranch) {
                println("  ** Contains branch instruction")
            }
        }
    }

    @Test
    fun traceFlagSetters() {
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

        println("Analyzing flag setters in MusicHandler:")

        // Find all blocks in function
        val funcBlocks = blocks.filter { it.function == musicFunc }

        // For each block with a branch, find what set the flags
        funcBlocks.forEach { block ->
            val branchLine = block.lines.lastOrNull { it.instruction?.op?.isBranch == true }
            if (branchLine != null) {
                println("\nBlock ${block.label ?: "@${block.originalLineIndex}"}")
                println("  Branch: ${branchLine.instruction?.op} to ${branchLine.instruction?.addressAsLabel?.label}")

                // Look backward for the instruction that set the flags
                var foundSetter = false
                for (i in block.lines.indexOf(branchLine) - 1 downTo 0) {
                    val line = block.lines[i]
                    val op = line.instruction?.op
                    val addr = line.instruction?.address
                    // Check if this instruction modifies flags
                    val modifiesFlags = op?.modifies(addr?.let { it::class })?.any {
                        it == AssemblyAffectable.Zero || it == AssemblyAffectable.Carry ||
                        it == AssemblyAffectable.Negative || it == AssemblyAffectable.Overflow
                    } == true
                    if (modifiesFlags) {
                        println("  Flag setter: ${line.originalLine}")
                        foundSetter = true
                        break
                    }
                }

                if (!foundSetter) {
                    println("  Flag setter: (from previous block)")
                }
            }
        }
    }
}
