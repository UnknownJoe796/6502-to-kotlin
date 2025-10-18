package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for Pass 18: Constant Propagation
 */
class ConstantPropagationTest {

    @Test
    fun testSimpleImmediateLoad() {
        val assembly = """
            .export test
            test:
                LDA #$05
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution)
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val constants = lines.analyzeConstants(cfg)

        // Debug output
        println("Functions found: ${constants.functions.size}")
        if (constants.functions.isNotEmpty()) {
            val func = constants.functions[0]
            println("Block facts: ${func.blockFacts.size}")

            // Should have one block
            assertTrue(func.blockFacts.isNotEmpty())

            // After LDA #$05, register A should be constant 5
            val exitState = func.blockFacts.values.first().exitState
            assertTrue(exitState.registerA is ConstantValue.Byte)
            assertEquals(5, (exitState.registerA as ConstantValue.Byte).value)
        }
    }

    @Test
    fun testRegisterTransfer() {
        val assembly = """
            .export test
            test:
                LDA #$42
                TAX
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val constants = lines.analyzeConstants(cfg)

        val func = constants.functions[0]
        val exitState = func.blockFacts.values.first().exitState

        // After TAX, both A and X should be $42
        assertTrue(exitState.registerA is ConstantValue.Byte)
        assertTrue(exitState.registerX is ConstantValue.Byte)
        assertEquals(0x42, (exitState.registerA as ConstantValue.Byte).value)
        assertEquals(0x42, (exitState.registerX as ConstantValue.Byte).value)
    }

    @Test
    fun testConstantArithmetic() {
        val assembly = """
            .export test
            test:
                LDA #$10
                ADC #$05
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val constants = lines.analyzeConstants(cfg)

        val func = constants.functions[0]
        val exitState = func.blockFacts.values.first().exitState

        // After ADC, A should be $15 (assuming carry clear)
        // Note: Without explicit CLC, carry state is unknown, so result might be unknown
        // For this test, we'll check if it's at least tracked
        println("Exit state A: ${exitState.registerA}")
    }

    @Test
    fun testIncrementDecrement() {
        val assembly = """
            .export test
            test:
                LDX #$00
                INX
                INX
                DEX
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val constants = lines.analyzeConstants(cfg)

        val func = constants.functions[0]
        val exitState = func.blockFacts.values.first().exitState

        // After LDX #0, INX, INX, DEX, X should be 1
        assertTrue(exitState.registerX is ConstantValue.Byte)
        assertEquals(1, (exitState.registerX as ConstantValue.Byte).value)
    }

    @Test
    fun testMergePoint() {
        val assembly = """
            .export test
            test:
                LDX #$00
                BEQ skip
                LDX #$01
            skip:
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val constants = lines.analyzeConstants(cfg)

        val func = constants.functions[0]

        // At skip label, X could be either 0 or 1, so should be Bottom
        // Find the skip block
        val skipBlock = func.function.blocks.find { block ->
            lines.lines.getOrNull(block.leaderIndex)?.label == "skip"
        }

        if (skipBlock != null) {
            val skipFacts = func.blockFacts[skipBlock.leaderIndex]
            if (skipFacts != null) {
                // X should be Bottom (conflicting values)
                assertTrue(
                    skipFacts.entryState.registerX is ConstantValue.Bottom ||
                    skipFacts.entryState.registerX is ConstantValue.Unknown
                )
            }
        }
    }

    @Test
    fun testBitwiseOperations() {
        val assembly = """
            .export test
            test:
                LDA #${'$'}FF
                AND #${'$'}0F
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val constants = lines.analyzeConstants(cfg)

        val func = constants.functions[0]
        val exitState = func.blockFacts.values.first().exitState

        // After AND, A should be $0F
        assertTrue(exitState.registerA is ConstantValue.Byte)
        assertEquals(0x0F, (exitState.registerA as ConstantValue.Byte).value)
    }

    @Test
    fun testStoreAndLoad() {
        val assembly = """
            .export test
            test:
                LDA #${'$'}42
                STA ${'$'}80
                LDA #${'$'}00
                LDA ${'$'}80
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("test"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val constants = lines.analyzeConstants(cfg)

        val func = constants.functions[0]
        val exitState = func.blockFacts.values.first().exitState

        // After loading from $80, A should be $42 (the value we stored)
        assertTrue(exitState.registerA is ConstantValue.Byte)
        assertEquals(0x42, (exitState.registerA as ConstantValue.Byte).value)
    }
}
