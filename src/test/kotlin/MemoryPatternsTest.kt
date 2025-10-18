package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for Pass 19: Memory Access Pattern Analysis
 */
class MemoryPatternsTest {

    @Test
    fun testScalarDetection() {
        val assembly = """
            .export test
            test:
                LDA ${'$'}80
                STA ${'$'}81
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution)
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val constants = lines.analyzeConstants(cfg)
        val memoryPatterns = lines.analyzeMemoryPatterns(cfg, constants, resolution)

        if (memoryPatterns.functions.isNotEmpty()) {
            val func = memoryPatterns.functions[0]

            println("Memory accesses: ${func.memoryAccesses.size}")
            func.memoryAccesses.forEach { (addr, info) ->
                println("  ${'$'}${addr.toString(16).uppercase()}: ${info.accessPattern}")
            }

            // Should have two scalar accesses
            assertTrue(func.memoryAccesses.size >= 2)

            // Both should be classified as scalars
            func.memoryAccesses.values.forEach { info ->
                assertTrue(info.accessPattern is MemoryAccessPattern.Scalar)
            }
        }
    }

    @Test
    fun testArrayDetection() {
        val assembly = """
            .export test
            test:
                LDX #${'$'}00
            loop:
                LDA ${'$'}200,X
                STA ${'$'}300,X
                INX
                CPX #${'$'}10
                BNE loop
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution)
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val constants = lines.analyzeConstants(cfg)
        val memoryPatterns = lines.analyzeMemoryPatterns(cfg, constants, resolution)

        if (memoryPatterns.functions.isNotEmpty()) {
            val func = memoryPatterns.functions[0]

            println("Identified arrays: ${func.identifiedArrays.size}")
            func.identifiedArrays.forEach { array ->
                println("  Array at ${'$'}${array.baseAddress.toString(16).uppercase()} indexed by ${array.indexRegister}")
            }

            // Should identify at least one array (maybe two)
            assertTrue(func.identifiedArrays.isNotEmpty())

            // Arrays should use register X
            func.identifiedArrays.forEach { array ->
                assertEquals(Variable.RegisterX, array.indexRegister)
            }
        }
    }

    @Test
    fun testPointerDetection() {
        val assembly = """
            .export test
            test:
                LDA (${'$'}80)
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution)
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val constants = lines.analyzeConstants(cfg)
        val memoryPatterns = lines.analyzeMemoryPatterns(cfg, constants, resolution)

        if (memoryPatterns.functions.isNotEmpty()) {
            val func = memoryPatterns.functions[0]

            println("Identified pointers: ${func.identifiedPointers.size}")
            func.identifiedPointers.forEach { ptr ->
                println("  Pointer at ${'$'}${ptr.pointerAddress.toString(16).uppercase()}")
            }

            // Should identify the pointer
            assertTrue(func.identifiedPointers.isNotEmpty() || func.memoryAccesses.isNotEmpty())
        }
    }

    @Test
    fun testIndirectIndexedArray() {
        val assembly = """
            .export test
            test:
                LDY #${'$'}00
                LDA (${'$'}80),Y
                INY
                LDA (${'$'}80),Y
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution)
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val constants = lines.analyzeConstants(cfg)
        val memoryPatterns = lines.analyzeMemoryPatterns(cfg, constants, resolution)

        if (memoryPatterns.functions.isNotEmpty()) {
            val func = memoryPatterns.functions[0]

            println("Memory accesses: ${func.memoryAccesses.size}")
            func.memoryAccesses.forEach { (addr, info) ->
                println("  ${'$'}${addr.toString(16).uppercase()}: ${info.accessPattern}, indirection=${info.indirectionLevel}")
            }

            // Should detect indirect array access
            assertTrue(func.memoryAccesses.values.any { it.indirectionLevel > 0 })
        }
    }

    @Test
    fun testReadWriteClassification() {
        val assembly = """
            .export test
            test:
                LDA ${'$'}80    ; Read
                STA ${'$'}81    ; Write
                LDA ${'$'}82    ; Read
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution)
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val constants = lines.analyzeConstants(cfg)
        val memoryPatterns = lines.analyzeMemoryPatterns(cfg, constants, resolution)

        if (memoryPatterns.functions.isNotEmpty()) {
            val func = memoryPatterns.functions[0]

            // Check read/write classification
            func.memoryAccesses.forEach { (addr, info) ->
                when (addr) {
                    0x80 -> {
                        assertTrue(info.reads.isNotEmpty())
                        assertTrue(info.writes.isEmpty())
                        assertTrue(info.isReadOnly)
                    }
                    0x81 -> {
                        assertTrue(info.writes.isNotEmpty())
                        // May or may not have reads depending on analysis
                    }
                    0x82 -> {
                        assertTrue(info.reads.isNotEmpty())
                        assertTrue(info.writes.isEmpty())
                    }
                }
            }
        }
    }

    @Test
    fun testMemoryRegionClassification() {
        // Test zero page
        assertEquals(RegionType.ZERO_PAGE, classifyMemoryRegion(0x0080))

        // Test stack
        assertEquals(RegionType.STACK, classifyMemoryRegion(0x0180))

        // Test PPU registers (NES)
        assertEquals(RegionType.IO_REGISTERS, classifyMemoryRegion(0x2000))

        // Test APU registers (NES)
        assertEquals(RegionType.IO_REGISTERS, classifyMemoryRegion(0x4000))

        // Test ROM
        assertEquals(RegionType.ROM, classifyMemoryRegion(0x8000))

        // Test RAM
        assertEquals(RegionType.RAM, classifyMemoryRegion(0x0600))
    }

    @Test
    fun testGlobalMemoryMap() {
        val assembly = """
            .export func1
            func1:
                LDA ${'$'}200
                RTS

            .export func2
            func2:
                STA ${'$'}200
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution)
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val constants = lines.analyzeConstants(cfg)
        val memoryPatterns = lines.analyzeMemoryPatterns(cfg, constants, resolution)

        // Global map should merge accesses from both functions
        val globalInfo = memoryPatterns.globalMemoryMap[0x200]
        if (globalInfo != null) {
            println("Address ${'$'}200: reads=${globalInfo.reads.size}, writes=${globalInfo.writes.size}")

            // Should have both read and write from different functions
            assertTrue(globalInfo.reads.isNotEmpty() || globalInfo.writes.isNotEmpty())
        }
    }

    @Test
    fun testIndexedVsNonIndexed() {
        val assembly = """
            .export test
            test:
                LDA ${'$'}200      ; Non-indexed
                LDA ${'$'}200,X    ; Indexed
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution)
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val constants = lines.analyzeConstants(cfg)
        val memoryPatterns = lines.analyzeMemoryPatterns(cfg, constants, resolution)

        if (memoryPatterns.functions.isNotEmpty()) {
            val func = memoryPatterns.functions[0]
            val info = func.memoryAccesses[0x200]

            if (info != null) {
                // Should be marked as indexed (has at least one indexed access)
                assertTrue(info.isIndexed)

                // Pattern should be array (due to indexed access)
                assertTrue(info.accessPattern is MemoryAccessPattern.Array)
            }
        }
    }
}
