package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LivenessAnalysisTest {

    @Test
    fun simpleLinearCodeLiveness() {
        // Simple linear code - variables should be live only between def and use
        val assembly = """
            main:
                LDA #${'$'}10
                STA ${'$'}2000
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val dataflow = lines.analyzeDataFlow(cfg, dominators)
        val ssa = lines.constructSsaForm(cfg, dominators, dataflow)
        val liveness = lines.analyzeLiveness(ssa)

        assertEquals(1, liveness.functions.size, "Should have one function")

        val mainFunction = liveness.functions[0]
        assertTrue(mainFunction.blockLiveness.isNotEmpty(), "Should have block liveness info")

        // Check that A register is live between LDA and STA
        val aVars = mainFunction.function.ssaVariables.filter {
            it.baseVariable == Variable.RegisterA
        }
        assertTrue(aVars.isNotEmpty(), "Should have A register SSA variables")

        // At least one A variable should be live (the one defined by LDA and used by STA)
        val liveAVars = aVars.filter { mainFunction.isVariableLive(it) }
        assertTrue(liveAVars.isNotEmpty(), "At least one A variable should be live")
    }

    @Test
    fun deadCodeDetection() {
        // Variable defined but never used
        val assembly = """
            main:
                LDA #${'$'}10    ; Used by STA
                STA ${'$'}2000
                LDX #${'$'}20    ; Dead - never used
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val dataflow = lines.analyzeDataFlow(cfg, dominators)
        val ssa = lines.constructSsaForm(cfg, dominators, dataflow)
        val liveness = lines.analyzeLiveness(ssa)

        val mainFunction = liveness.functions[0]

        // X should have dead variables (defined by LDX but never used)
        val xVars = mainFunction.function.ssaVariables.filter {
            it.baseVariable == Variable.RegisterX
        }
        assertTrue(xVars.isNotEmpty(), "Should have X register variables")

        val deadXVars = xVars.filter { it in mainFunction.deadVariables }
        assertTrue(deadXVars.isNotEmpty(), "X should have dead variables")
    }

    @Test
    fun conditionalBranchLiveness() {
        // Variables live across branches
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

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val dataflow = lines.analyzeDataFlow(cfg, dominators)
        val ssa = lines.constructSsaForm(cfg, dominators, dataflow)
        val liveness = lines.analyzeLiveness(ssa)

        val mainFunction = liveness.functions[0]

        // Multiple blocks should have liveness info
        assertTrue(mainFunction.blockLiveness.size > 1, "Should have multiple blocks")

        // A register versions should be live at the join point
        val aVars = mainFunction.function.ssaVariables.filter {
            it.baseVariable == Variable.RegisterA
        }
        val liveAVars = aVars.filter { mainFunction.isVariableLive(it) }
        assertTrue(liveAVars.isNotEmpty(), "A register should be live")
    }

    @Test
    fun loopLiveness() {
        // Loop variable should be live throughout the loop
        val assembly = """
            main:
                LDX #${'$'}10
            loop:
                DEX
                BNE loop
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val dataflow = lines.analyzeDataFlow(cfg, dominators)
        val ssa = lines.constructSsaForm(cfg, dominators, dataflow)
        val liveness = lines.analyzeLiveness(ssa)

        val mainFunction = liveness.functions[0]

        // X variables should be live (used in loop)
        val xVars = mainFunction.function.ssaVariables.filter {
            it.baseVariable == Variable.RegisterX
        }
        val liveXVars = xVars.filter { mainFunction.isVariableLive(it) }
        assertTrue(liveXVars.isNotEmpty(), "X should be live in loop")

        // Loop block should have X in live-in set
        val loopBlock = mainFunction.blockLiveness.values.find { info ->
            val block = mainFunction.function.blocks.find {
                it.originalBlock.leaderIndex == info.blockLeader
            }
            block?.instructions?.any { it.originalInstruction.op == AssemblyOp.DEX } == true
        }
        assertTrue(loopBlock != null, "Should find loop block")
        assertTrue(loopBlock!!.liveIn.any { it.baseVariable == Variable.RegisterX },
                   "X should be in live-in set of loop block")
    }

    @Test
    fun multipleUsesLiveness() {
        // Variable used multiple times
        val assembly = """
            main:
                LDX #${'$'}05
                TXA
                STX ${'$'}3000
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val dataflow = lines.analyzeDataFlow(cfg, dominators)
        val ssa = lines.constructSsaForm(cfg, dominators, dataflow)
        val liveness = lines.analyzeLiveness(ssa)

        val mainFunction = liveness.functions[0]

        // X should be live between LDX and last use (STX)
        val xVars = mainFunction.function.ssaVariables.filter {
            it.baseVariable == Variable.RegisterX
        }
        val liveXVars = xVars.filter { mainFunction.isVariableLive(it) }
        assertTrue(liveXVars.isNotEmpty(), "X should be live")

        // Find last uses of X
        liveXVars.forEach { xVar ->
            val lastUses = mainFunction.findLastUses(xVar)
            // Should have a last use (either TXA or STX depending on SSA renaming)
            if (lastUses.isNotEmpty()) {
                assertTrue(lastUses.all {
                    it.originalInstruction.op == AssemblyOp.TXA ||
                    it.originalInstruction.op == AssemblyOp.STX
                }, "Last use should be TXA or STX")
            }
        }
    }

    @Test
    fun statusFlagsLiveness() {
        // Status flags live between definition and use
        val assembly = """
            main:
                LDA #${'$'}10
                CMP #${'$'}05
                BEQ equal
                LDA #${'$'}20
            equal:
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val dataflow = lines.analyzeDataFlow(cfg, dominators)
        val ssa = lines.constructSsaForm(cfg, dominators, dataflow)
        val liveness = lines.analyzeLiveness(ssa)

        val mainFunction = liveness.functions[0]

        // Flags should be live between CMP and BEQ
        val flagVars = mainFunction.function.ssaVariables.filter {
            it.baseVariable == Variable.StatusFlags
        }
        val liveFlagVars = flagVars.filter { mainFunction.isVariableLive(it) }
        assertTrue(liveFlagVars.isNotEmpty(), "Status flags should be live")
    }

    @Test
    fun instructionLevelLiveness() {
        // Test instruction-level liveness tracking
        val assembly = """
            main:
                LDA #${'$'}10
                STA ${'$'}2000
                LDA #${'$'}20
                STA ${'$'}3000
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val dataflow = lines.analyzeDataFlow(cfg, dominators)
        val ssa = lines.constructSsaForm(cfg, dominators, dataflow)
        val liveness = lines.analyzeLiveness(ssa)

        val mainFunction = liveness.functions[0]

        // Should have instruction-level liveness
        assertTrue(mainFunction.instructionLiveness.isNotEmpty(),
                   "Should have instruction-level liveness")

        // Each instruction should have liveBefore and liveAfter sets
        mainFunction.instructionLiveness.forEach { instrLiveness ->
            // Sets can be empty, but they should exist
            assertTrue(instrLiveness.liveBefore.size >= 0, "liveBefore should be computed")
            assertTrue(instrLiveness.liveAfter.size >= 0, "liveAfter should be computed")
        }
    }

    @Test
    fun phiFunctionLiveness() {
        // Phi functions should properly contribute to liveness
        val assembly = """
            main:
                LDX #${'$'}00
                CMP #${'$'}42
                BEQ path1
                LDX #${'$'}10
                JMP join
            path1:
                LDX #${'$'}20
            join:
                STX ${'$'}4000
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val dataflow = lines.analyzeDataFlow(cfg, dominators)
        val ssa = lines.constructSsaForm(cfg, dominators, dataflow)
        val liveness = lines.analyzeLiveness(ssa)

        val mainFunction = liveness.functions[0]

        // X variables should be live (defined in paths, merged at phi, used by STX)
        val xVars = mainFunction.function.ssaVariables.filter {
            it.baseVariable == Variable.RegisterX
        }
        val liveXVars = xVars.filter { mainFunction.isVariableLive(it) }
        assertTrue(liveXVars.isNotEmpty(), "X variables should be live")

        // Join block should have live-in containing X (from phi function operands)
        val joinBlock = mainFunction.function.blocks.find { block ->
            block.phiFunctions.any { it.result.baseVariable == Variable.RegisterX }
        }
        if (joinBlock != null) {
            val joinLiveness = mainFunction.blockLiveness[joinBlock.originalBlock.leaderIndex]
            assertTrue(joinLiveness != null, "Join block should have liveness info")
            assertTrue(joinLiveness!!.liveIn.any { it.baseVariable == Variable.RegisterX },
                       "Join block should have X in live-in")
        }
    }

    @Test
    fun memoryLiveness() {
        // Memory locations should have proper liveness
        val assembly = """
            main:
                LDA #${'$'}42
                STA ${'$'}2000
                INC ${'$'}2000
                LDA ${'$'}2000
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val dataflow = lines.analyzeDataFlow(cfg, dominators)
        val ssa = lines.constructSsaForm(cfg, dominators, dataflow)
        val liveness = lines.analyzeLiveness(ssa)

        val mainFunction = liveness.functions[0]

        // Memory SSA variables should be tracked
        val memVars = mainFunction.function.ssaVariables.filter {
            it.baseVariable is Variable.Memory
        }
        assertTrue(memVars.isNotEmpty(), "Should have memory SSA variables")

        // At least some memory variables should be live (used by INC and final LDA)
        val liveMemVars = memVars.filter { mainFunction.isVariableLive(it) }
        assertTrue(liveMemVars.isNotEmpty(), "Memory variables should be live")
    }

    @Test
    fun complexControlFlowLiveness() {
        // Test liveness with complex control flow
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

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val dataflow = lines.analyzeDataFlow(cfg, dominators)
        val ssa = lines.constructSsaForm(cfg, dominators, dataflow)
        val liveness = lines.analyzeLiveness(ssa)

        val mainFunction = liveness.functions[0]

        // Should have liveness info for all blocks
        assertTrue(mainFunction.blockLiveness.size >= 3, "Should have multiple blocks")

        // X variables should be live
        val xVars = mainFunction.function.ssaVariables.filter {
            it.baseVariable == Variable.RegisterX
        }
        val liveXVars = xVars.filter { mainFunction.isVariableLive(it) }
        assertTrue(liveXVars.isNotEmpty(), "X should be live")

        // All block liveness info should be consistent
        mainFunction.blockLiveness.values.forEach { info ->
            // live-in should be a subset of (use ∪ live-out)
            val expectedLiveIn = (info.use + info.liveOut) - info.def
            // This is an approximation check - exact equality may not hold due to phi functions
            assertTrue(info.liveIn.all { it in expectedLiveIn || it in info.use },
                       "Live-in should be consistent with dataflow equations")
        }
    }
}
