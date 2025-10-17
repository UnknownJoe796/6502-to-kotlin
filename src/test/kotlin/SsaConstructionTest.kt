package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

class SsaConstructionTest {

    @Test
    fun simpleLinearCode() {
        // Simple linear code - no phi functions needed
        val assembly = """
            main:
                LDA #${'$'}10
                STA ${'$'}2000
                LDX #${'$'}20
                STX ${'$'}3000
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
        val ssa = lines.constructSsaForm(cfg, dominators, dataflow)

        assertEquals(1, ssa.functions.size, "Should have one function")

        val mainFunction = ssa.functions[0]
        assertTrue(mainFunction.blocks.isNotEmpty(), "Should have SSA blocks")

        // Linear code should have no phi functions
        val totalPhiFunctions = mainFunction.blocks.sumOf { it.phiFunctions.size }
        assertEquals(0, totalPhiFunctions, "Linear code should not need phi functions")

        // Should have SSA instructions
        val totalInstructions = mainFunction.blocks.sumOf { it.instructions.size }
        assertTrue(totalInstructions > 0, "Should have SSA instructions")

        // Each definition should increment the version
        val allDefs = mainFunction.blocks.flatMap { it.instructions }.flatMap { it.defines }
        assertTrue(allDefs.isNotEmpty(), "Should have definitions")

        // Check that A register gets versioned: A_0 -> A_1
        val aDefs = allDefs.filter { it.baseVariable == Variable.RegisterA }
        assertTrue(aDefs.isNotEmpty(), "Should have A register definitions")
        assertTrue(aDefs.all { it.version > 0 }, "Defined variables should have version > 0")
    }

    @Test
    fun conditionalBranchWithPhiFunctions() {
        // Conditional branch creates a join point that needs phi functions
        val assembly = """
            main:
                LDA #${'$'}10
                CMP #${'$'}05
                BEQ equal
                LDA #${'$'}20    ; A redefined in one path
            equal:
                STA ${'$'}4000   ; Join point - needs phi for A
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
        val ssa = lines.constructSsaForm(cfg, dominators, dataflow)

        val mainFunction = ssa.functions[0]

        // Should have multiple blocks
        assertTrue(mainFunction.blocks.size > 1, "Should have multiple blocks")

        // Should have phi functions at the join point (equal label)
        val totalPhiFunctions = mainFunction.blocks.sumOf { it.phiFunctions.size }
        assertTrue(totalPhiFunctions > 0, "Should have phi functions at join point")

        // Find the phi function for A register
        val aPhiFunctions = mainFunction.blocks
            .flatMap { it.phiFunctions }
            .filter { it.result.baseVariable == Variable.RegisterA }

        assertTrue(aPhiFunctions.isNotEmpty(), "Should have phi function for A register")

        // Phi function should have 2 operands (one from each path)
        val aPhi = aPhiFunctions.first()
        assertEquals(2, aPhi.operands.size, "Phi function should have 2 operands")

        // Both operands should be versions of A register
        assertTrue(aPhi.operands.all { it.second.baseVariable == Variable.RegisterA },
                   "All phi operands should be A register versions")
    }

    @Test
    fun loopWithPhiFunctions() {
        // Loop creates back edges that need phi functions
        val assembly = """
            main:
                LDX #${'$'}10
            loop:
                DEX            ; X redefined in loop
                BNE loop       ; Back edge to loop
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
        val ssa = lines.constructSsaForm(cfg, dominators, dataflow)

        val mainFunction = ssa.functions[0]

        // Should have phi functions for the loop variable
        val totalPhiFunctions = mainFunction.blocks.sumOf { it.phiFunctions.size }
        assertTrue(totalPhiFunctions > 0, "Loop should need phi functions")

        // Should have phi function for X register at loop header
        val xPhiFunctions = mainFunction.blocks
            .flatMap { it.phiFunctions }
            .filter { it.result.baseVariable == Variable.RegisterX }

        assertTrue(xPhiFunctions.isNotEmpty(), "Should have phi function for X register")

        // Phi should merge initial value and loop update
        val xPhi = xPhiFunctions.first()
        assertTrue(xPhi.operands.size >= 2, "Loop phi should have at least 2 operands")
    }

    @Test
    fun nestedConditionals() {
        // Nested conditionals test complex phi placement
        val assembly = """
            main:
                LDA #${'$'}10
                CMP #${'$'}05
                BEQ outer_equal
                LDA #${'$'}20
                CMP #${'$'}15
                BEQ inner_equal
                LDA #${'$'}30
            inner_equal:
                STA ${'$'}5000  ; Join point for inner conditional
            outer_equal:
                STA ${'$'}6000  ; Join point for outer conditional
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
        val ssa = lines.constructSsaForm(cfg, dominators, dataflow)

        val mainFunction = ssa.functions[0]

        // Should have phi functions at both join points
        val totalPhiFunctions = mainFunction.blocks.sumOf { it.phiFunctions.size }
        assertTrue(totalPhiFunctions > 0, "Nested conditionals should need phi functions")

        // Should have multiple A register versions
        val allAVariables = mainFunction.ssaVariables.filter {
            it.baseVariable == Variable.RegisterA
        }
        assertTrue(allAVariables.size > 1, "Should have multiple versions of A register")
    }

    @Test
    fun registerTransferChain() {
        // Test SSA renaming with register transfers
        val assembly = """
            main:
                LDX #${'$'}05
                TXA
                TAY
                STY ${'$'}3000
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
        val ssa = lines.constructSsaForm(cfg, dominators, dataflow)

        val mainFunction = ssa.functions[0]

        // Should have SSA variables for X, A, and Y
        val xVars = mainFunction.ssaVariables.filter { it.baseVariable == Variable.RegisterX }
        val aVars = mainFunction.ssaVariables.filter { it.baseVariable == Variable.RegisterA }
        val yVars = mainFunction.ssaVariables.filter { it.baseVariable == Variable.RegisterY }

        assertTrue(xVars.isNotEmpty(), "Should have X register SSA variables")
        assertTrue(aVars.isNotEmpty(), "Should have A register SSA variables")
        assertTrue(yVars.isNotEmpty(), "Should have Y register SSA variables")

        // Check use-def relationships are preserved in SSA
        val instructions = mainFunction.blocks.flatMap { it.instructions }

        // TXA should use X and define A
        val txaInstr = instructions.find { it.originalInstruction.op == AssemblyOp.TXA }
        assertNotNull(txaInstr, "Should find TXA instruction")
        assertTrue(txaInstr.uses.any { it.baseVariable == Variable.RegisterX },
                   "TXA should use X")
        assertTrue(txaInstr.defines.any { it.baseVariable == Variable.RegisterA },
                   "TXA should define A")

        // TAY should use A and define Y
        val tayInstr = instructions.find { it.originalInstruction.op == AssemblyOp.TAY }
        assertNotNull(tayInstr, "Should find TAY instruction")
        assertTrue(tayInstr.uses.any { it.baseVariable == Variable.RegisterA },
                   "TAY should use A")
        assertTrue(tayInstr.defines.any { it.baseVariable == Variable.RegisterY },
                   "TAY should define Y")
    }

    @Test
    fun memoryOperationsInSsa() {
        // Test SSA with memory operations
        val assembly = """
            main:
                LDA #${'$'}42
                STA ${'$'}2000
                INC ${'$'}2000
                LDA ${'$'}2000
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
        val ssa = lines.constructSsaForm(cfg, dominators, dataflow)

        val mainFunction = ssa.functions[0]
        val instructions = mainFunction.blocks.flatMap { it.instructions }

        // Should have memory SSA variables
        val memVars = mainFunction.ssaVariables.filter { it.baseVariable is Variable.Memory }
        assertTrue(memVars.isNotEmpty(), "Should have memory SSA variables")

        // Memory location should have multiple versions (STA, INC)
        val mem2000Vars = memVars.filter {
            (it.baseVariable as? Variable.Memory)?.address == 0x2000
        }
        assertTrue(mem2000Vars.size > 1, "Memory location should have multiple versions")

        // INC should both use and define memory
        val incInstr = instructions.find { it.originalInstruction.op == AssemblyOp.INC }
        assertNotNull(incInstr, "Should find INC instruction")
        assertTrue(incInstr.uses.any { it.baseVariable is Variable.Memory },
                   "INC should use memory")
        assertTrue(incInstr.defines.any { it.baseVariable is Variable.Memory },
                   "INC should define memory")
    }

    @Test
    fun statusFlagsInSsa() {
        // Test SSA with status flags
        val assembly = """
            main:
                LDA #${'$'}10
                CMP #${'$'}05   ; Defines flags
                BEQ equal       ; Uses flags
                LDA #${'$'}20
            equal:
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
        val ssa = lines.constructSsaForm(cfg, dominators, dataflow)

        val mainFunction = ssa.functions[0]
        val instructions = mainFunction.blocks.flatMap { it.instructions }

        // Should have status flag SSA variables
        val flagVars = mainFunction.ssaVariables.filter {
            it.baseVariable == Variable.StatusFlags
        }
        assertTrue(flagVars.isNotEmpty(), "Should have status flag SSA variables")

        // CMP should define flags
        val cmpInstr = instructions.find { it.originalInstruction.op == AssemblyOp.CMP }
        assertNotNull(cmpInstr, "Should find CMP instruction")
        assertTrue(cmpInstr.defines.any { it.baseVariable == Variable.StatusFlags },
                   "CMP should define status flags")

        // BEQ should use flags
        val beqInstr = instructions.find { it.originalInstruction.op == AssemblyOp.BEQ }
        assertNotNull(beqInstr, "Should find BEQ instruction")
        assertTrue(beqInstr.uses.any { it.baseVariable == Variable.StatusFlags },
                   "BEQ should use status flags")
    }

    @Test
    fun multipleDefinitionsReachUse() {
        // Test that SSA correctly handles multiple definitions reaching a use
        val assembly = """
            main:
                LDX #${'$'}00
                CMP #${'$'}42
                BEQ path1
                LDX #${'$'}10   ; X redefined
                JMP join
            path1:
                LDX #${'$'}20   ; X redefined differently
            join:
                STX ${'$'}4000  ; Use of X - needs phi
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
        val ssa = lines.constructSsaForm(cfg, dominators, dataflow)

        val mainFunction = ssa.functions[0]

        // Should have phi function for X at join point
        val xPhiFunctions = mainFunction.blocks
            .flatMap { it.phiFunctions }
            .filter { it.result.baseVariable == Variable.RegisterX }

        assertTrue(xPhiFunctions.isNotEmpty(), "Should have phi function for X at join")

        // Phi should have operands from both paths
        val joinPhi = xPhiFunctions.find { phi ->
            phi.operands.size >= 2
        }
        assertNotNull(joinPhi, "Join point should have phi with multiple operands")

        // All X SSA variables should have proper versions
        val xVars = mainFunction.ssaVariables.filter {
            it.baseVariable == Variable.RegisterX
        }
        // Should have multiple versions: initial + path definitions + phi result
        assertTrue(xVars.size >= 2, "Should have at least 2 versions of X (definitions in both paths)")
    }

    @Test
    fun stackOperationsInSsa() {
        // Test SSA with stack operations
        val assembly = """
            main:
                LDA #${'$'}42
                PHA
                LDA #${'$'}99
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
        val ssa = lines.constructSsaForm(cfg, dominators, dataflow)

        val mainFunction = ssa.functions[0]
        val instructions = mainFunction.blocks.flatMap { it.instructions }

        // Should have stack pointer SSA variables
        val spVars = mainFunction.ssaVariables.filter {
            it.baseVariable == Variable.StackPointer
        }
        assertTrue(spVars.isNotEmpty(), "Should have stack pointer SSA variables")

        // PHA should use A and modify SP
        val phaInstr = instructions.find { it.originalInstruction.op == AssemblyOp.PHA }
        assertNotNull(phaInstr, "Should find PHA instruction")
        assertTrue(phaInstr.uses.any { it.baseVariable == Variable.RegisterA },
                   "PHA should use A")
        assertTrue(phaInstr.defines.any { it.baseVariable == Variable.StackPointer },
                   "PHA should modify stack pointer")

        // PLA should define A and modify SP
        val plaInstr = instructions.find { it.originalInstruction.op == AssemblyOp.PLA }
        assertNotNull(plaInstr, "Should find PLA instruction")
        assertTrue(plaInstr.defines.any { it.baseVariable == Variable.RegisterA },
                   "PLA should define A")
        assertTrue(plaInstr.defines.any { it.baseVariable == Variable.StackPointer },
                   "PLA should modify stack pointer")
    }

    @Test
    fun complexControlFlowGraph() {
        // Test SSA with a more complex CFG
        val assembly = """
            main:
                LDX #${'$'}00
            loop:
                INX
                CPX #${'$'}10
                BCC loop
                CMP #${'$'}42
                BEQ special
                LDX #${'$'}50
                JMP done
            special:
                LDX #${'$'}99
            done:
                STX ${'$'}5000
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
        val ssa = lines.constructSsaForm(cfg, dominators, dataflow)

        val mainFunction = ssa.functions[0]

        // Should have multiple blocks
        assertTrue(mainFunction.blocks.size > 3, "Should have multiple blocks")

        // Should have phi functions (loop and join points)
        val totalPhiFunctions = mainFunction.blocks.sumOf { it.phiFunctions.size }
        assertTrue(totalPhiFunctions > 0, "Should have phi functions")

        // Should have X phi at loop header and at done label
        val xPhiFunctions = mainFunction.blocks
            .flatMap { it.phiFunctions }
            .filter { it.result.baseVariable == Variable.RegisterX }

        assertTrue(xPhiFunctions.isNotEmpty(), "Should have phi functions for X")

        // All SSA variables should have unique versions
        val allVersions = mainFunction.ssaVariables.groupBy { it.baseVariable }
        allVersions.forEach { (variable, versions) ->
            val versionNumbers = versions.map { it.version }
            assertEquals(versionNumbers.size, versionNumbers.distinct().size,
                        "All versions of $variable should be unique")
        }
    }
}
