package com.ivieleague.decompiler6502tokotlin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for Pass 37: Control Flow Emission
 *
 * This pass converts control flow structures to Kotlin AST statements,
 * emitting if/else, while loops, and for loops from 6502 branch patterns.
 */
class Pass37ControlFlowEmissionTest {

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

        val result = cfg.emitControlFlow(codeFile, null, null)

        assertEquals(1, result.functions.size)
        val funcFlow = result.functions.first()
        assertNotNull(funcFlow.body)
        assertTrue(funcFlow.body.statements.isNotEmpty())

        // Should have at least one return statement
        assertEquals(1, result.statistics.returnStatements)
    }

    @Test
    fun `test load instruction emission`() {
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

        val result = cfg.emitControlFlow(codeFile, null, null)

        val funcFlow = result.functions.first()
        val statements = funcFlow.body.statements

        // Should have assignment for LDA
        val assignments = statements.filterIsInstance<KotlinAst.Statement.Assignment>()
        assertTrue(assignments.isNotEmpty())

        val ldaAssignment = assignments.find {
            (it.target as? KotlinAst.Expression.Variable)?.name == "A"
        }
        assertNotNull(ldaAssignment)
    }

    @Test
    fun `test store instruction emission`() {
        val asm = """
            Test:
                STA ${'$'}0200
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitControlFlow(codeFile, null, null)

        val funcFlow = result.functions.first()
        val statements = funcFlow.body.statements

        // Should have assignment for STA
        val assignments = statements.filterIsInstance<KotlinAst.Statement.Assignment>()
        assertTrue(assignments.isNotEmpty())

        // Target should be memory array access
        val staAssignment = assignments.find {
            it.target is KotlinAst.Expression.ArrayAccess
        }
        assertNotNull(staAssignment)
    }

    @Test
    fun `test transfer instruction emission`() {
        val asm = """
            Test:
                TAX
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitControlFlow(codeFile, null, null)

        val funcFlow = result.functions.first()
        val statements = funcFlow.body.statements

        // Should have assignment for TAX (X = A)
        val assignments = statements.filterIsInstance<KotlinAst.Statement.Assignment>()
        val taxAssignment = assignments.find {
            (it.target as? KotlinAst.Expression.Variable)?.name == "X" &&
            (it.value as? KotlinAst.Expression.Variable)?.name == "A"
        }
        assertNotNull(taxAssignment)
    }

    @Test
    fun `test increment instruction emission`() {
        val asm = """
            Test:
                INX
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitControlFlow(codeFile, null, null)

        val funcFlow = result.functions.first()
        val statements = funcFlow.body.statements

        // Should have assignment for INX (X = (X + 1) and 0xFF)
        val assignments = statements.filterIsInstance<KotlinAst.Statement.Assignment>()
        val inxAssignment = assignments.find {
            (it.target as? KotlinAst.Expression.Variable)?.name == "X"
        }
        assertNotNull(inxAssignment)
    }

    @Test
    fun `test arithmetic instruction emission`() {
        val asm = """
            Test:
                ADC #${'$'}01
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitControlFlow(codeFile, null, null)

        val funcFlow = result.functions.first()
        val statements = funcFlow.body.statements

        // Should have assignment for ADC
        val assignments = statements.filterIsInstance<KotlinAst.Statement.Assignment>()
        val adcAssignment = assignments.find {
            (it.target as? KotlinAst.Expression.Variable)?.name == "A"
        }
        assertNotNull(adcAssignment)
    }

    @Test
    fun `test multiple instructions emission`() {
        val asm = """
            Test:
                LDA #${'$'}00
                STA ${'$'}0200
                LDX #${'$'}10
                INX
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitControlFlow(codeFile, null, null)

        val funcFlow = result.functions.first()
        val statements = funcFlow.body.statements

        // Should have multiple assignments
        val assignments = statements.filterIsInstance<KotlinAst.Statement.Assignment>()
        assertTrue(assignments.size >= 4)  // LDA, STA, LDX, INX
    }

    @Test
    fun `test assembly comments are included`() {
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

        val result = cfg.emitControlFlow(codeFile, null, null)

        val funcFlow = result.functions.first()
        val statements = funcFlow.body.statements

        // Should have comment statements
        val comments = statements.filterIsInstance<KotlinAst.Statement.Comment>()
        assertTrue(comments.isNotEmpty())
    }

    @Test
    fun `test statistics tracking`() {
        val asm = """
            Test:
                LDA #${'$'}00
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitControlFlow(codeFile, null, null)

        // Check statistics are tracked
        assertNotNull(result.statistics)
        assertTrue(result.statistics.returnStatements >= 1)
    }

    @Test
    fun `test indexed addressing emission`() {
        val asm = """
            Test:
                LDA ${'$'}0200,X
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.emitControlFlow(codeFile, null, null)

        val funcFlow = result.functions.first()
        val statements = funcFlow.body.statements

        // Should have assignment with indexed memory access
        val assignments = statements.filterIsInstance<KotlinAst.Statement.Assignment>()
        val indexedAssignment = assignments.find {
            val value = it.value
            value is KotlinAst.Expression.ArrayAccess &&
            value.index is KotlinAst.Expression.Binary
        }
        assertNotNull(indexedAssignment)
    }
}
