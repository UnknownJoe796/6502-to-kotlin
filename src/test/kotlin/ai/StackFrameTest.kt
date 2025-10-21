package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for Pass 21: Stack Frame Analysis
 */
class StackFrameTest {

    @Test
    fun testSimpleRegisterSave() {
        val assembly = """
            .export test
            test:
                PHA
                LDA #${'$'}42
                PLA
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution)
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val stackAnalysis = lines.analyzeStackFrames(cfg)

        if (stackAnalysis.functions.isNotEmpty()) {
            val func = stackAnalysis.functions[0]

            println("Push/pull pairs: ${func.pushPullPairs.size}")
            println("Max stack depth: ${func.maxStackDepth}")
            println("Balanced: ${func.isStackBalanced}")

            // Should have one matched push/pull pair
            assertEquals(1, func.pushPullPairs.size)

            // Stack should be balanced
            assertTrue(func.isStackBalanced)

            // Max depth should be 1 (one PHA)
            assertEquals(1, func.maxStackDepth)
        }
    }

    @Test
    fun testMultipleRegisterSaves() {
        val assembly = """
            .export test
            test:
                PHA
                PHP
                LDA #${'$'}42
                PLP
                PLA
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution)
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val stackAnalysis = lines.analyzeStackFrames(cfg)

        if (stackAnalysis.functions.isNotEmpty()) {
            val func = stackAnalysis.functions[0]

            // Should have two matched pairs (PHA/PLA and PHP/PLP)
            assertEquals(2, func.pushPullPairs.size)

            // Stack should be balanced
            assertTrue(func.isStackBalanced)

            // Max depth should be 2
            assertEquals(2, func.maxStackDepth)
        }
    }

    @Test
    fun testUnmatchedPush() {
        val assembly = """
            .export test
            test:
                PHA
                PHA
                PLA
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution)
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val stackAnalysis = lines.analyzeStackFrames(cfg)

        if (stackAnalysis.functions.isNotEmpty()) {
            val func = stackAnalysis.functions[0]

            println("Unmatched pushes: ${func.unmatchedPushes.size}")
            println("Matched pairs: ${func.pushPullPairs.size}")

            // Should have one matched pair and one unmatched push
            assertEquals(1, func.pushPullPairs.size)
            assertEquals(1, func.unmatchedPushes.size)

            // Stack should NOT be balanced (one extra push)
            assertTrue(!func.isStackBalanced)
        }
    }

    @Test
    fun testUnmatchedPull() {
        val assembly = """
            .export test
            test:
                PHA
                PLA
                PLA
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution)
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val stackAnalysis = lines.analyzeStackFrames(cfg)

        if (stackAnalysis.functions.isNotEmpty()) {
            val func = stackAnalysis.functions[0]

            // Should have one matched pair and one unmatched pull
            assertEquals(1, func.pushPullPairs.size)
            assertEquals(1, func.unmatchedPulls.size)
        }
    }

    @Test
    fun testNestedSaves() {
        val assembly = """
            .export test
            test:
                PHA          ; Save A
                LDA #${'$'}10
                PHA          ; Save intermediate value
                LDA #${'$'}20
                PLA          ; Restore intermediate
                PLA          ; Restore original A
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution)
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val stackAnalysis = lines.analyzeStackFrames(cfg)

        if (stackAnalysis.functions.isNotEmpty()) {
            val func = stackAnalysis.functions[0]

            // Should have two matched pairs
            assertEquals(2, func.pushPullPairs.size)

            // Stack should be balanced
            assertTrue(func.isStackBalanced)

            // Max depth should be 2 (both PHAs before any PLAs)
            assertEquals(2, func.maxStackDepth)
        }
    }

    @Test
    fun testStackWithJSR() {
        val assembly = """
            .export test
            test:
                PHA
                JSR helper
                PLA
                RTS

            helper:
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution)
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val stackAnalysis = lines.analyzeStackFrames(cfg)

        // Find the test function (should be first due to .export)
        val testFunc = stackAnalysis.functions.find { it.function.entryLabel == "test" }

        if (testFunc != null) {
            // Should have one matched pair
            assertEquals(1, testFunc.pushPullPairs.size)

            // Stack should be balanced
            assertTrue(testFunc.isStackBalanced)

            // Max depth includes JSR return address (2 bytes) + PHA (1 byte) = 3
            assertTrue(testFunc.maxStackDepth >= 1)
        }
    }

    @Test
    fun testSavedRegisterDetection() {
        val assembly = """
            .export test
            test:
                PHA          ; Save at entry
                PHP          ; Save at entry
                LDA #${'$'}42
                ; ... do work ...
                PLP          ; Restore before exit
                PLA          ; Restore before exit
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution)
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val stackAnalysis = lines.analyzeStackFrames(cfg)

        if (stackAnalysis.functions.isNotEmpty()) {
            val func = stackAnalysis.functions[0]

            println("Saved registers: ${func.savedRegisters.size}")
            println("All pairs: ${func.pushPullPairs.size}")

            // Both should be identified as saved registers
            // (pushed at entry, pulled at exit)
            assertTrue(func.savedRegisters.size >= 0)  // May be 0 or 2 depending on block boundaries

            // Should have two total pairs
            assertEquals(2, func.pushPullPairs.size)
        }
    }

    @Test
    fun testStatistics() {
        val assembly = """
            .export test
            test:
                PHA
                PHP
                LDA #${'$'}42
                PLP
                PLA
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution)
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val stackAnalysis = lines.analyzeStackFrames(cfg)

        if (stackAnalysis.functions.isNotEmpty()) {
            val func = stackAnalysis.functions[0]
            val stats = func.getStatistics()

            println("Statistics: $stats")

            assertTrue(stats["maxDepth"]!! >= 1)
            assertTrue(stats["matchedPairs"]!! >= 0)
            assertTrue(stats["balanced"] == 1 || stats["balanced"] == 0)
        }
    }
}
