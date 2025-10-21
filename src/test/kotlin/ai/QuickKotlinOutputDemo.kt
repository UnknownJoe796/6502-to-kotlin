package com.ivieleague.decompiler6502tokotlin

import org.junit.jupiter.api.Test
import java.io.File

/**
 * Demo: Generate actual Kotlin output from 6502 assembly
 */
class QuickKotlinOutputDemo {

    @Test
    fun `generate simple program output`() {
        val code = """
            ; Simple program that copies memory
            CopyMemory:
                LDX #${'$'}00          ; Initialize index to 0
            Loop:
                LDA ${'$'}0200,X       ; Load from source array
                STA ${'$'}0300,X       ; Store to destination array
                INX                ; Increment index
                CPX #${'$'}10          ; Compare with array length
                BNE Loop           ; Loop if not done
                RTS                ; Return
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("CopyMemory"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitQuickKotlin(codeFile)

        println("=".repeat(80))
        println("GENERATED KOTLIN CODE:")
        println("=".repeat(80))
        println(result.sourceCode)
        println("=".repeat(80))
        println("Statistics:")
        println("  Functions: ${result.functionCount}")
        println("  Lines: ${result.linesEmitted}")
        println("  Used Expression Trees: ${result.usedExpressionTrees}")
        println("=".repeat(80))

        // Optionally write to file
        val outputFile = File("outputs/demo-output.kt")
        outputFile.parentFile?.mkdirs()
        outputFile.writeText(result.sourceCode)
        println("Written to: ${outputFile.absolutePath}")
    }

    @Test
    fun `generate multi-function program`() {
        val code = """
            Init:
                LDA #${'$'}00
                STA ${'$'}0200
                JSR ProcessData
                RTS

            ProcessData:
                LDA ${'$'}0200
                ADC #${'$'}01
                STA ${'$'}0200
                RTS

            GetValue:
                LDA ${'$'}0200
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(
            resolution,
            exportedLabels = setOf("Init", "ProcessData", "GetValue")
        )
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitQuickKotlin(codeFile)

        println("=".repeat(80))
        println("MULTI-FUNCTION PROGRAM:")
        println("=".repeat(80))
        println(result.sourceCode)
        println("=".repeat(80))

        // Write to file
        val outputFile = File("outputs/multi-function-output.kt")
        outputFile.parentFile?.mkdirs()
        outputFile.writeText(result.sourceCode)
        println("Written to: ${outputFile.absolutePath}")
    }

    @Test
    fun `generate with arithmetic operations`() {
        val code = """
            Math:
                LDA #${'$'}05
                ADC #${'$'}10
                STA ${'$'}0200

                LDA #${'$'}20
                SBC #${'$'}05
                STA ${'$'}0201

                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Math"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitQuickKotlin(codeFile)

        println("=".repeat(80))
        println("ARITHMETIC PROGRAM:")
        println("=".repeat(80))
        println(result.sourceCode)
        println("=".repeat(80))

        // Write to file
        val outputFile = File("outputs/arithmetic-output.kt")
        outputFile.parentFile?.mkdirs()
        outputFile.writeText(result.sourceCode)
        println("Written to: ${outputFile.absolutePath}")
    }
}
