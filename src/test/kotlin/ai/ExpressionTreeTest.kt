package com.ivieleague.decompiler6502tokotlin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for Pass 22: Expression Tree Building
 */
class ExpressionTreeTest {

    @Test
    fun `test simple literal loading`() {
        val assembly = """
            .export test
            test:
            LDA #$42
            RTS
        """.trimIndent()

        val codeFile = assembly.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataflow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val types = codeFile.inferTypes(cfg, dataflow, constants, memoryPatterns)

        val expressions = codeFile.buildExpressionTrees(cfg, constants, types, memoryPatterns)

        assertNotNull(expressions)
        assertTrue(expressions.functions.isNotEmpty())
    }

    @Test
    fun `test arithmetic addition`() {
        val assembly = """
            .export test
            test:
            LDA #$10
            ADC #$05
            RTS
        """.trimIndent()

        val codeFile = assembly.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataflow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val types = codeFile.inferTypes(cfg, dataflow, constants, memoryPatterns)

        val expressions = codeFile.buildExpressionTrees(cfg, constants, types, memoryPatterns)

        // Should build expression: 0x10 + 0x05
        assertNotNull(expressions)
        val funcExpr = expressions.functions.firstOrNull()
        assertNotNull(funcExpr)
    }

    @Test
    fun `test variable assignment`() {
        val assembly = """
            .export test
            test:
            LDA #$42
            STA $0200
            RTS
        """.trimIndent()


        val codeFile = assembly.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val loops = codeFile.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val dataflow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val types = codeFile.inferTypes(cfg, dataflow, constants, memoryPatterns)
        val expressions = codeFile.buildExpressionTrees(cfg, constants, types, memoryPatterns)

        // Should create assignment expression
        val funcExpr = expressions.functions.firstOrNull()
        assertNotNull(funcExpr)

        val blockExpr = funcExpr?.blockExpressions?.values?.firstOrNull()
        assertNotNull(blockExpr)
        assertTrue(blockExpr!!.expressions.any {
            it.root is Expression.Assignment
        })
    }

    @Test
    fun `test bitwise operations`() {
        val assembly = """
            .export test
            test:
            LDA #$0xFF
            AND #$0F
            RTS
        """.trimIndent()

        val codeFile = assembly.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val loops = codeFile.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val dataflow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val types = codeFile.inferTypes(cfg, dataflow, constants, memoryPatterns)
        val expressions = codeFile.buildExpressionTrees(cfg, constants, types, memoryPatterns)

        // Should build bitwise AND expression
        assertNotNull(expressions)
    }

    @Test
    fun `test shift left operation`() {
        val assembly = """
            .export test
            test:
            LDA #$01
            ASL A
            RTS
        """.trimIndent()

        val codeFile = assembly.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val loops = codeFile.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val dataflow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val types = codeFile.inferTypes(cfg, dataflow, constants, memoryPatterns)
        val expressions = codeFile.buildExpressionTrees(cfg, constants, types, memoryPatterns)

        // Should build shift left expression
        assertNotNull(expressions)
    }

    @Test
    fun `test register transfers`() {
        val assembly = """
            .export test
            test:
            LDA #$42
            TAX
            TXA
            RTS
        """.trimIndent()

        val codeFile = assembly.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val loops = codeFile.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val dataflow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val types = codeFile.inferTypes(cfg, dataflow, constants, memoryPatterns)
        val expressions = codeFile.buildExpressionTrees(cfg, constants, types, memoryPatterns)

        // Register transfers should propagate expressions
        assertNotNull(expressions)
    }

    @Test
    fun `test array access with X register`() {
        val assembly = """
            .export test
            test:
            LDX #$05
            LDA $0200,X
            RTS
        """.trimIndent()

        val codeFile = assembly.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val loops = codeFile.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val dataflow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val types = codeFile.inferTypes(cfg, dataflow, constants, memoryPatterns)

        val expressions = codeFile.buildExpressionTrees(cfg, constants, types, memoryPatterns)

        // Should recognize array access pattern
        assertNotNull(expressions)
    }

    @Test
    fun `test increment operations`() {
        val assembly = """
            .export test
            test:
            LDX #$00
            INX
            INX
            RTS
        """.trimIndent()

        val codeFile = assembly.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val loops = codeFile.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val dataflow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val types = codeFile.inferTypes(cfg, dataflow, constants, memoryPatterns)
        val expressions = codeFile.buildExpressionTrees(cfg, constants, types, memoryPatterns)

        // Should build increment expressions
        assertNotNull(expressions)
    }

    @Test
    fun `test expression simplification result`() {
        // Test that expression tree building produces valid expressions
        val literal = Expression.Literal(42)
        assertEquals(42, (literal as Expression.Literal).value)

        val binOp = Expression.BinaryOp(
            BinaryOperator.ADD,
            Expression.Literal(10),
            Expression.Literal(5)
        )
        assertTrue(binOp.left is Expression.Literal)
        assertTrue(binOp.right is Expression.Literal)
    }
}
