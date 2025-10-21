package com.ivieleague.decompiler6502tokotlin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for Pass 38: Variable & Function Emission
 *
 * This pass combines all previous passes to emit complete Kotlin code.
 */
class Pass38VariableFunctionEmissionTest {

    @Test
    fun `test simple function emission`() {
        val asm = """
            Init:
                LDA #${'$'}00
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Init"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitCompleteKotlin(codeFile, null, null, null, null, null)

        assertNotNull(result.kotlinFile)
        assertEquals("decompiled", result.kotlinFile.packageName)
        assertTrue(result.kotlinFile.declarations.isNotEmpty())

        // Should have function
        val functions = result.kotlinFile.declarations.filterIsInstance<KotlinAst.Declaration.Function>()
        assertTrue(functions.any { it.name == "Init" })
    }

    @Test
    fun `test complete emission statistics`() {
        val asm = """
            Test:
                LDA #${'$'}42
                STA ${'$'}0200
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitCompleteKotlin(codeFile, null, null, null, null, null)

        // Check statistics
        assertTrue(result.statistics.totalFunctions >= 1)
        assertTrue(result.statistics.totalStatements > 0)
        assertTrue(result.statistics.linesOfCode > 0)
    }

    @Test
    fun `test function body replacement`() {
        val asm = """
            Init:
                LDA #${'$'}00
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Init"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitCompleteKotlin(codeFile, null, null, null, null, null)

        // Function should have non-empty body
        val initFunc = result.kotlinFile.declarations
            .filterIsInstance<KotlinAst.Declaration.Function>()
            .find { it.name == "Init" }

        assertNotNull(initFunc)
        assertTrue(initFunc!!.body.statements.isNotEmpty())
    }

    @Test
    fun `test emit to string produces valid Kotlin`() {
        val asm = """
            Test:
                LDA #${'$'}42
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitCompleteKotlin(codeFile, null, null, null, null, null)
        val kotlinCode = result.kotlinFile.emitToString()

        // Should have package declaration
        assertTrue(kotlinCode.contains("package decompiled"))

        // Should have file annotation
        assertTrue(kotlinCode.contains("@file:Suppress"))

        // Should have function
        assertTrue(kotlinCode.contains("fun Test"))
    }

    @Test
    fun `test CPU state variables are included`() {
        val asm = """
            Test:
                LDA #${'$'}42
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitCompleteKotlin(codeFile, null, null, null, null, null)
        val kotlinCode = result.kotlinFile.emitToString()

        // Should have CPU registers
        assertTrue(kotlinCode.contains("var A"))
        assertTrue(kotlinCode.contains("var X"))
        assertTrue(kotlinCode.contains("var Y"))

        // Should have memory
        assertTrue(kotlinCode.contains("val memory"))
    }

    @Test
    fun `test multiple functions emission`() {
        val asm = """
            Init:
                LDA #${'$'}00
                RTS

            Process:
                LDA #${'$'}01
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Init", "Process"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitCompleteKotlin(codeFile, null, null, null, null, null)

        // Should have both functions
        val functions = result.kotlinFile.declarations.filterIsInstance<KotlinAst.Declaration.Function>()
        assertTrue(functions.any { it.name == "Init" })
        assertTrue(functions.any { it.name == "Process" })

        assertEquals(2, result.statistics.totalFunctions)
    }

    @Test
    fun `test property emission to string`() {
        val prop = KotlinAst.Declaration.Property(
            name = "counter",
            type = KotlinAst.KotlinType.Int,
            initializer = KotlinAst.Expression.Literal(0, KotlinAst.KotlinType.Int),
            isVar = true,
            kdoc = "Test counter",
            visibility = KotlinAst.Visibility.PUBLIC
        )

        val emitted = prop.emitPropertyToString()

        assertTrue(emitted.contains("var counter"))
        assertTrue(emitted.contains("Int"))
        assertTrue(emitted.contains("0x0"))
    }

    @Test
    fun `test function with statements emission`() {
        val asm = """
            Test:
                LDA #${'$'}05
                TAX
                INX
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitCompleteKotlin(codeFile, null, null, null, null, null)
        val kotlinCode = result.kotlinFile.emitToString()

        // Should have multiple statements
        assertTrue(kotlinCode.contains("A = "))
        assertTrue(kotlinCode.contains("X = "))
    }

    @Test
    fun `test lines of code estimation`() {
        val asm = """
            Init:
                LDA #${'$'}00
                STA ${'$'}0200
                RTS

            Process:
                LDA #${'$'}01
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Init", "Process"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        // Run variable identification
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val typeInference = codeFile.inferTypes(cfg, dataFlow, constants, memoryPatterns)
        val loops = codeFile.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val variableId = codeFile.identifyVariables(cfg, dataFlow, typeInference, memoryPatterns, loops)

        val result = cfg.emitCompleteKotlin(codeFile, variableId, null, null, null, null).also {
            println("=== Generated Kotlin with Variable Analysis ===")
            println(it.kotlinFile.emitToString())
        }

        // Should have reasonable LOC estimate
        assertTrue(result.statistics.linesOfCode > 10)  // At least header + variables + functions
    }

    @Test
    fun `test integration with control flow emission`() {
        val asm = """
            Test:
                LDA #${'$'}42
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        // Emit control flow separately
        val cfEmission = cfg.emitControlFlow(codeFile, null, null, null)

        // Pass to complete emission
        val result = cfg.emitCompleteKotlin(codeFile, null, null, null, null, cfEmission)

        assertNotNull(result.kotlinFile)
        assertTrue(result.statistics.totalStatements > 0)
    }
}
