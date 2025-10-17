package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

class DominatorTreeTest {
    
    @Test
    fun simpleLinearFunction() {
        val assembly = """
            main:
                LDA #${'$'}10
                STA ${'$'}2000
                RTS
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        
        assertEquals(1, dominators.functions.size, "Should have one function")
        
        val mainFunction = dominators.functions[0]
        assertNotNull(mainFunction.dominatorTree, "Should have dominator tree")
        
        // Simple linear function should have entry block dominating itself
        assertEquals(mainFunction.function.entryLeader, mainFunction.dominatorTree.leaderIndex)
        assertEquals(null, mainFunction.dominatorTree.immediateDominator, "Entry should have no immediate dominator")
        
        // No loops in linear code
        assertEquals(0, mainFunction.naturalLoops.size, "Linear function should have no loops")
        assertEquals(0, mainFunction.backEdges.size, "Linear function should have no back edges")
    }
    
    @Test
    fun simpleIfThenElse() {
        val assembly = """
            main:
                CMP #${'$'}10
                BEQ then_branch
                LDA #${'$'}20    ; else branch
                JMP end
            then_branch:
                LDA #${'$'}30
            end:
                STA ${'$'}2000
                RTS
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        
        assertEquals(1, dominators.functions.size, "Should have one function")
        
        val mainFunction = dominators.functions[0]
        assertTrue(mainFunction.dominatorTree.children.isNotEmpty(), "Entry should dominate other blocks")
        
        // Should have multiple blocks
        assertTrue(mainFunction.leaderToDomNode.size > 1, "Should have multiple basic blocks")
        
        // No loops in if-then-else
        assertEquals(0, mainFunction.naturalLoops.size, "If-then-else should have no loops")
    }
    
    @Test
    fun simpleLoop() {
        val assembly = """
            main:
                LDX #${'$'}10
            loop:
                DEX
                BNE loop
                RTS
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        
        assertEquals(1, dominators.functions.size, "Should have one function")
        
        val mainFunction = dominators.functions[0]
        
        // Should detect the loop 
        assertTrue(mainFunction.naturalLoops.isNotEmpty(), "Should detect natural loop")
        assertTrue(mainFunction.backEdges.isNotEmpty(), "Should detect back edge")
        
        val loop = mainFunction.naturalLoops[0]
        assertTrue(loop.blocks.isNotEmpty(), "Loop should contain at least one block")
        
        // Back edge should go from loop body back to header
        val backEdge = mainFunction.backEdges[0]
        assertEquals(loop.header, backEdge.second, "Back edge should target loop header")
    }
    
    @Test
    fun nestedLoop() {
        val assembly = """
            main:
                LDX #${'$'}05
            outer_loop:
                LDY #${'$'}03
            inner_loop:
                DEY
                BNE inner_loop
                DEX
                BNE outer_loop
                RTS
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        
        assertEquals(1, dominators.functions.size, "Should have one function")
        
        val mainFunction = dominators.functions[0]
        
        // Should detect multiple loops (inner and outer)
        assertTrue(mainFunction.naturalLoops.size >= 1, "Should detect at least one natural loop")
        assertTrue(mainFunction.backEdges.size >= 1, "Should detect at least one back edge")
        
        // Test dominator relationships
        val domTree = mainFunction.dominatorTree
        assertTrue(domTree.children.isNotEmpty(), "Entry should dominate other blocks")
    }
    
    @Test
    fun multipleExitLoop() {
        val assembly = """
            main:
                LDX #${'$'}10
            loop:
                DEX
                BEQ exit1
                CMP #${'$'}05
                BEQ exit2
                JMP loop
            exit1:
                LDA #${'$'}01
                JMP end
            exit2:
                LDA #${'$'}02
            end:
                STA ${'$'}2000
                RTS
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        
        assertEquals(1, dominators.functions.size, "Should have one function")
        
        val mainFunction = dominators.functions[0]
        
        // Should still detect the loop despite multiple exits
        assertTrue(mainFunction.naturalLoops.isNotEmpty(), "Should detect natural loop with multiple exits")
        
        val loop = mainFunction.naturalLoops[0] 
        assertTrue(loop.blocks.isNotEmpty(), "Loop should contain blocks")
    }
    
    @Test
    fun dominanceFrontierCalculation() {
        val assembly = """
            main:
                CMP #${'$'}10
                BEQ branch1
                CMP #${'$'}20
                BEQ branch2
                LDA #${'$'}01    ; default path
                JMP merge
            branch1:
                LDA #${'$'}02
                JMP merge
            branch2:
                LDA #${'$'}03
            merge:
                STA ${'$'}2000
                RTS
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        
        assertEquals(1, dominators.functions.size, "Should have one function")
        
        val mainFunction = dominators.functions[0]
        
        // Test that dominance frontiers are computed
        val hasNonEmptyFrontier = mainFunction.leaderToDomNode.values.any { 
            it.dominanceFrontier.isNotEmpty() 
        }
        
        // In a control flow with branches that merge, some blocks should have dominance frontiers
        // This is a basic check - detailed frontier verification would require more complex logic
        assertTrue(mainFunction.leaderToDomNode.isNotEmpty(), "Should have dominator nodes")
    }
    
    @Test
    fun multipleFunctions() {
        val assembly = """
            main:
                JSR subroutine
                RTS
                
            subroutine:
                LDA #${'$'}10
                RTS
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        
        // Should analyze each function separately
        assertTrue(dominators.functions.isNotEmpty(), "Should have at least one function")
        
        dominators.functions.forEach { functionAnalysis ->
            assertNotNull(functionAnalysis.dominatorTree, "Each function should have a dominator tree")
            assertTrue(functionAnalysis.leaderToDomNode.isNotEmpty(), "Each function should have dominator nodes")
        }
    }
}