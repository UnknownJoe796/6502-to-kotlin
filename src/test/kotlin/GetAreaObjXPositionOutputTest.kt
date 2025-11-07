package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertTrue

class GetAreaObjXPositionOutputTest {
    @Test
    fun `GetAreaObjXPosition should have A as output`() {
        // Parse and build the assembly
        val code = File("smbdism.asm").readText().parseToAssemblyCodeFile()
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        // Find the GetAreaObjXPosition function
        val function = functions.find { it.startingBlock.label == "GetAreaObjXPosition" }
        require(function != null) { "GetAreaObjXPosition function not found" }

        println("Function: ${function.startingBlock.label}")
        println("Inputs: ${function.inputs}")
        println("Outputs: ${function.outputs}")
        println("Clobbers: ${function.clobbers}")
        println()

        // Print callers and what they do with A
        println("Callers (${function.callers.size}):")
        for (caller in function.callers) {
            println("  Caller at line ${caller.originalLineIndex}: ${caller.originalLine?.trim()}")
            val block = caller.block
            if (block != null) {
                val idx = block.lines.indexOf(caller)
                if (idx != -1 && idx + 1 < block.lines.size) {
                    // Print the next few instructions
                    for (i in (idx + 1)..minOf(idx + 3, block.lines.lastIndex)) {
                        val nextLine = block.lines[i]
                        println("    -> ${nextLine.originalLine?.trim()}")
                        val instr = nextLine.instruction
                        if (instr != null) {
                            val reads = instr.op.reads(instr.address?.let { it::class })
                            val tracked = reads.mapNotNull { it.toTrackedAsIo(instr.address) }
                            println("       reads: $reads -> tracked: $tracked")
                        }
                    }
                }
            }
            println()
        }

        // The function should have A as an output since callers use it
        assertTrue(
            function.outputs?.contains(TrackedAsIo.A) == true,
            "GetAreaObjXPosition should have A as an output. Current outputs: ${function.outputs}"
        )
    }
}
