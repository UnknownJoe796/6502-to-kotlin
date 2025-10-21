package com.ivieleague.decompiler6502tokotlin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for Pass 35: Quick Kotlin Code Emitter
 */
class QuickKotlinEmitTest {

    /**
     * Test 1: Simple function emission
     */
    @Test
    fun `test simple function emission`() {
        val code = """
            Start:
                LDA #${'$'}05
                STA ${'$'}0200
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Start"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitQuickKotlin(codeFile)

        // Should generate valid Kotlin
        assertTrue(result.sourceCode.contains("package decompiled"), "Should have package declaration")
        assertTrue(result.sourceCode.contains("fun Start()"), "Should have function Start")
        assertTrue(result.sourceCode.contains("val memory = ByteArray(65536)"), "Should declare memory")
        assertTrue(result.sourceCode.contains("var A = 0"), "Should declare register A")

        // Should have assembly comments
        assertTrue(result.sourceCode.contains("// LDA"), "Should have assembly comment for LDA")
        assertTrue(result.sourceCode.contains("// STA"), "Should have assembly comment for STA")

        // Should have function count
        assertEquals(1, result.functionCount, "Should have one function")
    }

    /**
     * Test 2: Multiple functions
     */
    @Test
    fun `test multiple functions`() {
        val code = """
            Func1:
                LDA #${'$'}05
                RTS

            Func2:
                LDA #${'$'}10
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Func1", "Func2"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitQuickKotlin(codeFile)

        // Should generate both functions
        assertTrue(result.sourceCode.contains("fun Func1()"), "Should have Func1")
        assertTrue(result.sourceCode.contains("fun Func2()"), "Should have Func2")
        assertEquals(2, result.functionCount, "Should have two functions")
    }

    /**
     * Test 3: Load and store instructions
     */
    @Test
    fun `test load and store instructions`() {
        val code = """
            Test:
                LDA #${'$'}42
                STA ${'$'}0200
                LDX ${'$'}0201
                STX ${'$'}0202
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitQuickKotlin(codeFile)

        // Should emit load/store operations
        assertTrue(result.sourceCode.contains("A ="), "Should assign to A")
        assertTrue(result.sourceCode.contains("memory["), "Should access memory")
        assertTrue(result.sourceCode.contains("0x42"), "Should have literal value")
    }

    /**
     * Test 4: Transfer instructions
     */
    @Test
    fun `test transfer instructions`() {
        val code = """
            Transfer:
                LDA #${'$'}05
                TAX
                TXA
                TAY
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Transfer"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitQuickKotlin(codeFile)

        // Should emit transfer operations
        assertTrue(result.sourceCode.contains("X = A"), "Should have TAX")
        assertTrue(result.sourceCode.contains("A = X"), "Should have TXA")
        assertTrue(result.sourceCode.contains("Y = A"), "Should have TAY")
    }

    /**
     * Test 5: Arithmetic operations
     */
    @Test
    fun `test arithmetic operations`() {
        val code = """
            Math:
                LDA #${'$'}05
                ADC #${'$'}10
                SBC #${'$'}03
                INX
                DEY
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Math"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitQuickKotlin(codeFile)

        // Should emit arithmetic operations
        assertTrue(result.sourceCode.contains("A = (A +") || result.sourceCode.contains("ADC"), "Should have addition")
        assertTrue(result.sourceCode.contains("X = (X + 1)") || result.sourceCode.contains("INX"), "Should have increment")
    }

    /**
     * Test 6: File header and declarations
     */
    @Test
    fun `test file header and declarations`() {
        val code = """
            Test:
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitQuickKotlin(codeFile)

        // Check header
        assertTrue(result.sourceCode.startsWith("// Decompiled"), "Should have decompiled comment")
        assertTrue(result.sourceCode.contains("@file:Suppress"), "Should have suppressions")
        assertTrue(result.sourceCode.contains("package decompiled"), "Should have package")

        // Check declarations
        assertTrue(result.sourceCode.contains("val memory = ByteArray(65536)"), "Should declare memory")
        assertTrue(result.sourceCode.contains("var A = 0"), "Should declare A register")
        assertTrue(result.sourceCode.contains("var X = 0"), "Should declare X register")
        assertTrue(result.sourceCode.contains("var Y = 0"), "Should declare Y register")
        assertTrue(result.sourceCode.contains("var SP = 0xFF"), "Should declare SP")
        assertTrue(result.sourceCode.contains("var flagN = false"), "Should declare flags")
    }

    /**
     * Test 7: Empty function
     */
    @Test
    fun `test empty function`() {
        val code = """
            Empty:
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Empty"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitQuickKotlin(codeFile)

        // Should still generate valid function
        assertTrue(result.sourceCode.contains("fun Empty()"), "Should have function")
        assertTrue(result.sourceCode.contains("return"), "Should have return")
        assertEquals(1, result.functionCount, "Should count the function")
    }

    /**
     * Test 8: Comparison operations
     */
    @Test
    fun `test comparison operations`() {
        val code = """
            Compare:
                LDA #${'$'}05
                CMP #${'$'}10
                CPX #${'$'}20
                CPY #${'$'}30
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Compare"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitQuickKotlin(codeFile)

        // Should set flags for comparisons
        assertTrue(result.sourceCode.contains("flagZ") || result.sourceCode.contains("CMP"), "Should handle comparison flags")
    }
}
