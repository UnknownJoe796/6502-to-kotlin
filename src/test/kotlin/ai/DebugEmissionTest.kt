package com.ivieleague.decompiler6502tokotlin

import org.junit.jupiter.api.Test

/**
 * Debug test to understand what's happening
 */
class DebugEmissionTest {

    @Test
    fun `debug simple store`() {
        val asm = """
            Start:
                LDA #${'$'}42
                STA ${'$'}0200
                RTS
        """.trimIndent()

        val lines = asm.parseToAssemblyCodeFile()
        val codeFile = CodeFile(lines.lines, null)

        println("=== Parsed Lines ===")
        codeFile.lines.forEach { line ->
            println("Label: ${line.label}, Instruction: ${line.instruction?.op?.name}, Address: ${line.instruction?.address}")
        }

        val resolution = codeFile.resolveAddresses(0x8000)

        println("\n=== Address Resolution ===")
        println("labelToAddress: ${resolution.labelToAddress}")
        println("resolved lines: ${resolution.resolved.take(5)}")

        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Start"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        println("\n=== CFG ===")
        cfg.functions.forEach { func ->
            println("Function: ${func.entryLabel} at 0x${func.entryAddress.toString(16)}")
            func.blocks.forEach { block ->
                println("  Block: ${block.lineIndexes}")
            }
        }

        // Generate Kotlin
        val result = cfg.emitCompleteKotlin(codeFile, null, null, resolution, null, null)
        val kotlinCode = result.kotlinFile.emitToString()

        println("\n=== Generated Kotlin ===")
        println(kotlinCode)
    }
}
