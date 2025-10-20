package com.ivieleague.decompiler6502tokotlin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Basic tests for Kotlin emission - start simple!
 */
class BasicEmissionTest {

    @Test
    fun `simple load and store`() {
        val asm = """
            Start:
                LDA #${'$'}42
                STA ${'$'}0200
                RTS
        """.trimIndent()

        val lines = asm.parseToAssemblyCodeFile()
        val codeFile = CodeFile(lines.lines, null)
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Start"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        // Generate Kotlin
        val result = cfg.emitCompleteKotlin(codeFile, null, null, resolution, null, null)
        val kotlinCode = result.kotlinFile.emitToString()

        println("=== Generated Kotlin ===")
        println(kotlinCode)

        // Verify basics
        assertTrue(kotlinCode.contains("fun Start()"), "Should have Start function")
        assertTrue(kotlinCode.contains("A = 0x42") || kotlinCode.contains("A = 66"), "Should load 0x42")
        assertTrue(kotlinCode.contains("memory[0x200]") || kotlinCode.contains("memory[512]"), "Should store to 0x200")
        assertFalse(kotlinCode.contains("// //"), "Should not have double comments")
    }

    @Test
    fun `simple branch`() {
        val asm = """
            CheckValue:
                LDA ${'$'}00
                BEQ Zero
                LDA #${'$'}01
                RTS
            Zero:
                LDA #${'$'}00
                RTS
        """.trimIndent()

        val lines = asm.parseToAssemblyCodeFile()
        val codeFile = CodeFile(lines.lines, null)
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("CheckValue"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitCompleteKotlin(codeFile, null, null, resolution, null, null)
        val kotlinCode = result.kotlinFile.emitToString()

        println("=== Generated Kotlin ===")
        println(kotlinCode)

        // Verify branching
        assertTrue(kotlinCode.contains("fun CheckValue()"), "Should have CheckValue function")
        val hasControlFlow = kotlinCode.contains("if") || kotlinCode.contains("while")
        if (!hasControlFlow) {
            println("ERROR: No control flow found. Looking for if/while in:")
            println(kotlinCode)
        }
        assertTrue(hasControlFlow, "Should have control flow (if or while)")
        assertFalse(kotlinCode.contains("// //"), "Should not have double comments")
    }

    @Test
    fun `loop with counter`() {
        val asm = """
            Loop:
                LDX #${'$'}10
            LoopBody:
                DEX
                BNE LoopBody
                RTS
        """.trimIndent()

        val lines = asm.parseToAssemblyCodeFile()
        val codeFile = CodeFile(lines.lines, null)
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Loop"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitCompleteKotlin(codeFile, null, null, resolution, null, null)
        val kotlinCode = result.kotlinFile.emitToString()

        println("=== Generated Kotlin ===")
        println(kotlinCode)

        // Verify loop
        assertTrue(kotlinCode.contains("fun Loop()"), "Should have Loop function")
        assertTrue(kotlinCode.contains("X = 0x10") || kotlinCode.contains("X = 16"), "Should init counter")
        assertTrue(kotlinCode.contains("while"), "Should have while loop")
        assertFalse(kotlinCode.contains("// //"), "Should not have double comments")
    }
}
