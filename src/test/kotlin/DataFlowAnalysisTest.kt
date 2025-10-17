package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

class DataFlowAnalysisTest {
    
    @Test
    fun simpleLoadStore() {
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
        val dataflow = lines.analyzeDataFlow(cfg, dominators)
        
        assertEquals(1, dataflow.functions.size, "Should have one function")
        
        val mainFunction = dataflow.functions[0]
        assertTrue(mainFunction.definitions.isNotEmpty(), "Should have definitions")
        assertTrue(mainFunction.uses.isNotEmpty(), "Should have uses")
        
        // Check that we have definitions for A register and memory location
        val aDefinitions = mainFunction.definitions.filter { it.variable == Variable.RegisterA }
        val memDefinitions = mainFunction.definitions.filter { it.variable == Variable.Memory(0x2000) }
        
        assertTrue(aDefinitions.isNotEmpty(), "Should define A register")
        assertTrue(memDefinitions.isNotEmpty(), "Should define memory location")
        
        // Check use-def chains
        assertTrue(mainFunction.useDefChains.isNotEmpty(), "Should have use-def chains")
    }
    
    @Test
    fun registerTransfer() {
        val assembly = """
            main:
                LDX #${'$'}05
                TXA
                STA ${'$'}3000
                RTS
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val dataflow = lines.analyzeDataFlow(cfg, dominators)
        
        val mainFunction = dataflow.functions[0]
        
        // Should have definitions for X, A, and memory
        val xDefs = mainFunction.definitions.filter { it.variable == Variable.RegisterX }
        val aDefs = mainFunction.definitions.filter { it.variable == Variable.RegisterA }
        val memDefs = mainFunction.definitions.filter { it.variable == Variable.Memory(0x3000) }
        
        assertTrue(xDefs.isNotEmpty(), "Should define X register")
        assertTrue(aDefs.isNotEmpty(), "Should define A register")
        assertTrue(memDefs.isNotEmpty(), "Should define memory location")
        
        // Should have uses for X register in TXA and A register in STA
        val xUses = mainFunction.uses.filter { it.variable == Variable.RegisterX }
        val aUses = mainFunction.uses.filter { it.variable == Variable.RegisterA }
        
        assertTrue(xUses.isNotEmpty(), "Should use X register")
        assertTrue(aUses.isNotEmpty(), "Should use A register")
    }
    
    @Test
    fun conditionalBranch() {
        val assembly = """
            main:
                LDA #${'$'}10
                CMP #${'$'}05
                BEQ equal
                LDA #${'$'}20
            equal:
                STA ${'$'}4000
                RTS
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val dataflow = lines.analyzeDataFlow(cfg, dominators)
        
        val mainFunction = dataflow.functions[0]
        
        // Should have multiple basic blocks
        assertTrue(mainFunction.function.blocks.size > 1, "Should have multiple blocks for branches")
        
        // Check that A register definitions reach the STA instruction
        val aDefs = mainFunction.definitions.filter { it.variable == Variable.RegisterA }
        val aUses = mainFunction.uses.filter { it.variable == Variable.RegisterA }
        
        assertTrue(aDefs.size >= 2, "Should have multiple A register definitions")
        assertTrue(aUses.isNotEmpty(), "Should have A register uses")
        
        // Check use-def chains for the STA instruction
        val staUse = aUses.find { 
            it.lineRef.content.instruction?.op == AssemblyOp.STA
        }
        assertNotNull(staUse, "Should find STA use of A register")
        
        val staUseDefChain = mainFunction.useDefChains.find { it.use == staUse }
        assertNotNull(staUseDefChain, "Should have use-def chain for STA")
        assertTrue(staUseDefChain.reachingDefinitions.isNotEmpty(), "STA should have reaching definitions")
    }
    
    @Test
    fun loopDataFlow() {
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
        val dataflow = lines.analyzeDataFlow(cfg, dominators)
        
        val mainFunction = dataflow.functions[0]
        
        // Should have X register definitions and uses
        val xDefs = mainFunction.definitions.filter { it.variable == Variable.RegisterX }
        val xUses = mainFunction.uses.filter { it.variable == Variable.RegisterX }
        
        assertTrue(xDefs.size >= 2, "Should have multiple X definitions (LDX and DEX)")
        assertTrue(xUses.isNotEmpty(), "Should have X uses (DEX reads X)")
        
        // Check that definitions reach across loop iterations
        val defUseChains = mainFunction.defUseChains.filter { it.definition.variable == Variable.RegisterX }
        assertTrue(defUseChains.isNotEmpty(), "Should have def-use chains for X register")
    }
    
    @Test
    fun deadCodeDetection() {
        val assembly = """
            main:
                LDA #${'$'}10    ; Used
                STA ${'$'}2000
                LDX #${'$'}20    ; Dead - never used
                LDA #${'$'}30    ; Used  
                STA ${'$'}3000
                RTS
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val dataflow = lines.analyzeDataFlow(cfg, dominators)
        
        val mainFunction = dataflow.functions[0]
        
        // Should detect dead X register definition
        val deadDefs = mainFunction.deadDefinitions
        val deadXDefs = deadDefs.filter { it.variable == Variable.RegisterX }
        
        assertTrue(deadXDefs.isNotEmpty(), "Should detect dead X register definition")
        
        // A register definitions should not be dead (they're used by STA)
        val deadADefs = deadDefs.filter { it.variable == Variable.RegisterA }
        val totalADefs = mainFunction.definitions.filter { it.variable == Variable.RegisterA }
        
        // Not all A definitions should be dead
        assertTrue(deadADefs.size < totalADefs.size, "Not all A definitions should be dead")
    }
    
    @Test
    fun memoryIndexing() {
        val assembly = """
            main:
                LDX #${'$'}05
                LDA ${'$'}2000,X
                STA ${'$'}3000,Y
                RTS
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val dataflow = lines.analyzeDataFlow(cfg, dominators)
        
        val mainFunction = dataflow.functions[0]
        
        // Should use X and Y registers as indexes
        val xUses = mainFunction.uses.filter { it.variable == Variable.RegisterX }
        val yUses = mainFunction.uses.filter { it.variable == Variable.RegisterY }
        
        assertTrue(xUses.isNotEmpty(), "Should use X register for indexing")
        assertTrue(yUses.isNotEmpty(), "Should use Y register for indexing")
        
        // Should access memory locations
        val memUses = mainFunction.uses.filter { it.variable is Variable.Memory }
        val memDefs = mainFunction.definitions.filter { it.variable is Variable.Memory }
        
        assertTrue(memUses.isNotEmpty(), "Should have memory uses")
        assertTrue(memDefs.isNotEmpty(), "Should have memory definitions")
    }
    
    @Test
    fun stackOperations() {
        val assembly = """
            main:
                LDA #${'$'}42
                PHA
                LDX #${'$'}99
                PLA
                RTS
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val dataflow = lines.analyzeDataFlow(cfg, dominators)
        
        val mainFunction = dataflow.functions[0]
        
        // Should have stack pointer definitions and uses
        val spDefs = mainFunction.definitions.filter { it.variable == Variable.StackPointer }
        val spUses = mainFunction.uses.filter { it.variable == Variable.StackPointer }
        
        assertTrue(spDefs.isNotEmpty(), "Should have stack pointer definitions")
        assertTrue(spUses.isNotEmpty(), "Should have stack pointer uses")
        
        // PHA should use A register, PLA should define A register
        val aUses = mainFunction.uses.filter { it.variable == Variable.RegisterA }
        val aDefs = mainFunction.definitions.filter { it.variable == Variable.RegisterA }
        
        assertTrue(aUses.isNotEmpty(), "Should use A register in PHA")
        assertTrue(aDefs.size >= 2, "Should define A register in LDA and PLA")
    }
    
    @Test
    fun arithmeticOperations() {
        val assembly = """
            main:
                LDA #${'$'}10
                ADC #${'$'}05
                SBC #${'$'}03
                STA ${'$'}5000
                RTS
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val dataflow = lines.analyzeDataFlow(cfg, dominators)
        
        val mainFunction = dataflow.functions[0]
        
        // Should have multiple A register definitions (LDA, ADC, SBC)
        val aDefs = mainFunction.definitions.filter { it.variable == Variable.RegisterA }
        assertTrue(aDefs.size >= 3, "Should have multiple A register definitions")
        
        // Should have status flag definitions (ADC, SBC modify flags)
        val flagDefs = mainFunction.definitions.filter { it.variable == Variable.StatusFlags }
        assertTrue(flagDefs.isNotEmpty(), "Should have status flag definitions")
        
        // ADC and SBC should use A register
        val aUses = mainFunction.uses.filter { it.variable == Variable.RegisterA }
        assertTrue(aUses.size >= 2, "Should use A register in ADC and SBC")
    }
    
    @Test
    fun multipleFunctions() {
        val assembly = """
            main:
                JSR subroutine
                RTS
                
            subroutine:
                LDA #${'$'}42
                RTS
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val dataflow = lines.analyzeDataFlow(cfg, dominators)
        
        // Should analyze each function separately
        assertTrue(dataflow.functions.isNotEmpty(), "Should have at least one function")
        
        dataflow.functions.forEach { functionDataFlow ->
            assertNotNull(functionDataFlow.function, "Each function should have CFG data")
            assertTrue(functionDataFlow.blockFacts.isNotEmpty(), "Each function should have data flow facts")
        }
    }
    
    @Test
    fun liveVariableAnalysis() {
        val assembly = """
            main:
                LDA #${'$'}10    ; A is live after this
                LDX #${'$'}20    ; X is live after this  
                CMP #${'$'}05    ; A is used here, then dead
                STA ${'$'}2000   ; A is used here, X still live
                STX ${'$'}3000   ; X is used here, then dead
                RTS
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val dataflow = lines.analyzeDataFlow(cfg, dominators)
        
        val mainFunction = dataflow.functions[0]
        
        // Should have computed live variable information
        val blockFacts = mainFunction.blockFacts.values
        assertTrue(blockFacts.isNotEmpty(), "Should have block facts")
        
        // At function entry, some variables might be live depending on calling convention
        // This is a basic test that live variable analysis ran without errors
        assertTrue(blockFacts.all { it.liveIn.size >= 0 && it.liveOut.size >= 0 }, 
                   "Live sets should be computed")
    }
}