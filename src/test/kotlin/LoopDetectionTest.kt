package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class LoopDetectionTest {

    @Test
    fun simpleWhileLoop() {
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
        val loops = lines.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)

        assertEquals(1, loops.functions.size, "Should have one function")

        val mainLoops = loops.functions[0]
        assertEquals(1, mainLoops.loops.size, "Should detect one loop")

        val loop = mainLoops.loops[0]
        assertEquals(LoopType.DO_WHILE, loop.loopType, "Should be do-while loop")
        assertTrue(loop.backEdges.isNotEmpty(), "Should have back edges")
        assertEquals(0, loop.nestingDepth, "Should be top-level loop")
    }

    @Test
    fun nestedLoops() {
        val assembly = """
            main:
                LDX #${'$'}10
            outer:
                LDY #${'$'}05
            inner:
                DEY
                BNE inner
                DEX
                BNE outer
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val loops = lines.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)

        val mainLoops = loops.functions[0]
        assertEquals(2, mainLoops.loops.size, "Should detect two loops")

        // Find inner and outer loops
        val innerLoop = mainLoops.loops.find { it.nestingDepth == 1 }
        val outerLoop = mainLoops.loops.find { it.nestingDepth == 0 }

        assertTrue(innerLoop != null, "Should have inner loop")
        assertTrue(outerLoop != null, "Should have outer loop")

        assertEquals(outerLoop, innerLoop?.parentLoop, "Inner loop should have outer as parent")
        assertTrue(innerLoop in outerLoop!!.childLoops, "Outer loop should contain inner")
    }

    @Test
    fun infiniteLoop() {
        val assembly = """
            main:
            forever:
                LDA #${'$'}42
                JMP forever
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val loops = lines.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)

        val mainLoops = loops.functions[0]
        assertEquals(1, mainLoops.loops.size, "Should detect one loop")

        val loop = mainLoops.loops[0]
        assertEquals(LoopType.INFINITE, loop.loopType, "Should be infinite loop")
        assertTrue(loop.exits.isEmpty(), "Should have no exits")
    }

    @Test
    fun loopWithExit() {
        val assembly = """
            main:
                LDX #${'$'}10
            loop:
                DEX
                BEQ done
                JMP loop
            done:
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val loops = lines.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)

        val mainLoops = loops.functions[0]
        assertEquals(1, mainLoops.loops.size, "Should detect one loop")

        val loop = mainLoops.loops[0]
        assertTrue(loop.exits.isNotEmpty(), "Should have exit blocks")
        assertTrue(loop.exitTargets.isNotEmpty(), "Should have exit targets")
    }

    @Test
    fun noLoops() {
        val assembly = """
            main:
                LDA #${'$'}42
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
        val loops = lines.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)

        val mainLoops = loops.functions[0]
        assertEquals(0, mainLoops.loops.size, "Should detect no loops")
        assertEquals(0, mainLoops.topLevelLoops.size, "Should have no top-level loops")
    }
}
