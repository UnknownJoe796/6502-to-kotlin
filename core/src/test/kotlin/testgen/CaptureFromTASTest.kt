@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.testgen

import com.ivieleague.decompiler6502tokotlin.interpreter.BinaryInterpreter6502
import com.ivieleague.decompiler6502tokotlin.interpreter.FM2Parser
import com.ivieleague.decompiler6502tokotlin.interpreter.NESLoader
import java.io.File
import kotlin.test.Test

/**
 * Captures function input/output states from TAS replay to generate unit tests.
 */
class CaptureFromTASTest {

    // PPU stub with VBlank simulation (simplified from FullTASValidationTest)
    class PPUStub {
        var ppuCtrl: UByte = 0u
            private set
        private var ppuStatus: UByte = 0x00u
        private var vblankActive = false

        fun read(addr: Int): UByte {
            return when (addr and 0x07) {
                0x02 -> {
                    val status = ppuStatus
                    ppuStatus = (ppuStatus.toInt() and 0x7F).toUByte()
                    status
                }
                else -> 0u
            }
        }

        fun write(addr: Int, value: UByte) {
            when (addr and 0x07) {
                0x00 -> ppuCtrl = value
            }
        }

        fun startFrame() {
            ppuStatus = (ppuStatus.toInt() and 0x3F).toUByte()
        }

        fun beginVBlank() {
            ppuStatus = (ppuStatus.toInt() or 0xC0).toUByte()
            vblankActive = true
        }

        fun endVBlank() {
            ppuStatus = (ppuStatus.toInt() and 0x7F).toUByte()
            vblankActive = false
        }

        fun isNmiEnabled(): Boolean = (ppuCtrl.toInt() and 0x80) != 0
    }

    // Simple controller
    class Controller {
        private var player1Buttons = 0
        private var shiftReg = 0
        private var strobe = false

        fun setButtons(buttons: Int) {
            player1Buttons = buttons
        }

        fun writeStrobe(value: UByte) {
            val newStrobe = (value.toInt() and 0x01) != 0
            if (newStrobe) {
                shiftReg = player1Buttons
            }
            strobe = newStrobe
        }

        fun read(): UByte {
            val bit = shiftReg and 0x01
            if (!strobe) {
                shiftReg = shiftReg shr 1
            }
            return bit.toUByte()
        }
    }

    private fun findRom(): File? {
        return listOf("local/roms/smb.nes", "smb.nes", "../smb.nes")
            .map { File(it) }
            .firstOrNull { it.exists() }
    }

    private fun findTasFile(): File? {
        return listOf(
            "local/tas/happylee-warps.fm2",
            "local/tas/smb-tas.fm2",
            "smb/happylee-warps.fm2",
            "happylee-warps.fm2"
        ).map { File(it) }.firstOrNull { it.exists() }
    }

    @Test
    fun `capture function states from TAS`() {
        val romFile = findRom()
        if (romFile == null) {
            println("⚠️ Skipping capture test - no ROM file found")
            return
        }

        val tasFile = findTasFile()
        if (tasFile == null) {
            println("⚠️ Skipping capture test - no TAS file found")
            return
        }

        println("Loading ROM from: ${romFile.absolutePath}")
        println("Loading TAS from: ${tasFile.absolutePath}")

        // Load ROM
        val rom = NESLoader.load(romFile)
        val interp = BinaryInterpreter6502()
        NESLoader.loadIntoMemory(rom, interp.memory)

        // Parse TAS
        val tasInputs = FM2Parser.parse(tasFile)
        println("Loaded ${tasInputs.size} TAS frames")

        // Create tracer (capture all functions)
        val tracer = FunctionCallTracer(interp, targetFunctions = null)

        // Set up PPU/controller stubs
        val ppu = PPUStub()
        val controller = Controller()

        // Store original hooks before tracer attaches
        interp.memoryReadHook = { addr ->
            when (addr) {
                in 0x2000..0x2007 -> ppu.read(addr)
                0x4016 -> controller.read()
                0x4017 -> 0u
                else -> null
            }
        }

        interp.memoryWriteHook = { addr, value ->
            when (addr) {
                in 0x2000..0x2007 -> { ppu.write(addr, value); true }
                0x4016 -> { controller.writeStrobe(value); true }
                in 0x4000..0x4017 -> true  // APU/IO
                else -> false
            }
        }

        // Attach tracer (chains existing hooks)
        tracer.attach()

        // Reset and run
        interp.reset()
        println("Reset vector: \$${interp.cpu.PC.toString(16).uppercase()}")

        val cyclesPerFrame = 29780
        val vblankStartCycle = 27400
        val maxFrames = tasInputs.size  // Capture all TAS frames

        println("\nRunning $maxFrames frames with function capture...")

        for (frame in 0 until maxFrames) {
            tracer.onFrameStart(frame)
            ppu.startFrame()

            // Set controller input
            val buttons = if (frame < tasInputs.size) tasInputs[frame].buttons else 0
            controller.setButtons(buttons)

            // Run frame
            var frameCycles = 0
            var vblankSet = false
            var nmiTriggeredThisFrame = false

            while (frameCycles < cyclesPerFrame && !interp.halted) {
                val nmiEnabledBefore = ppu.isNmiEnabled()
                val cycles = interp.step()
                frameCycles += cycles
                tracer.onInstruction()

                // Handle VBlank/NMI
                if (!vblankSet && frameCycles >= vblankStartCycle) {
                    ppu.beginVBlank()
                    vblankSet = true

                    if (nmiEnabledBefore && !nmiTriggeredThisFrame) {
                        interp.triggerNmi()
                        interp.handleInterrupts()
                        nmiTriggeredThisFrame = true
                    }
                }
            }

            if (!vblankSet) {
                ppu.beginVBlank()
            }
            ppu.endVBlank()

            // Progress report
            if (frame % 200 == 0 || frame == maxFrames - 1) {
                val uniqueFuncs = tracer.getCapturedFunctions().size
                println("Frame $frame: ${tracer.getCaptureCount()} captures, $uniqueFuncs unique functions")
            }

            if (interp.halted) {
                println("⚠️ Interpreter halted at frame $frame")
                break
            }
        }

        // Detach tracer
        tracer.detach()

        // Get captures and select test cases
        val allCaptures = tracer.getCaptures()
        println("\n=== Capture Complete ===")
        println("Total captures: ${allCaptures.size}")
        println("Unique functions: ${tracer.getCapturedFunctions().size}")
        println("NMI stats: ${tracer.getNmiStats()}")

        // Select test cases
        val selector = TestCaseSelector(maxTestsPerFunction = 10)
        val selectedByFunction = selector.selectTestCases(allCaptures)

        println("\nFunctions with selected tests: ${selectedByFunction.size}")

        // Get statistics
        val stats = selector.getStatistics(allCaptures)
        println("\nTop 20 most-called functions:")
        for (funcStat in stats.functions.take(20)) {
            println("  $funcStat")
        }

        // Save to JSON
        val outputDir = File("local/testgen")
        outputDir.mkdirs()

        val testData = TestDataSerialization.createTestData(
            tasName = tasFile.nameWithoutExtension,
            totalFrames = maxFrames,
            allCaptures = allCaptures,
            selectedByFunction = selectedByFunction
        )

        val jsonFile = File(outputDir, "captured-tests-${tasFile.nameWithoutExtension}.json")
        TestDataSerialization.save(testData, jsonFile)
        println("\n✅ Saved test data to: ${jsonFile.absolutePath}")

        // Load address mapper for function names
        val asmFile = File("smbdism.asm")
        val mapper = if (asmFile.exists()) {
            println("\nLoading address-to-label mapping from smbdism.asm...")
            AddressLabelMapper.fromAssemblyFile(asmFile)
        } else {
            println("\n⚠️ smbdism.asm not found - using address-based function names")
            null
        }

        // Log JSR target statistics
        if (mapper != null) {
            val jsrTargets = mapper.getJsrTargetAddresses()
            println("JSR target labels: ${mapper.getJsrTargetLabels().size}")
            println("JSR target addresses: ${jsrTargets.size}")

            // Check how many captured addresses are JSR targets
            val capturedAddresses = allCaptures.map { it.functionAddress }.toSet()
            val capturedJsrTargets = capturedAddresses.filter { mapper.isJsrTarget(it) }
            println("Captured addresses that are JSR targets: ${capturedJsrTargets.size} / ${capturedAddresses.size}")
        }

        val functionNames = mapper?.getAllAddresses()?.associate { addr ->
            addr to mapper.getFunctionName(addr)!!
        } ?: emptyMap()

        // Parse function signatures from decompiled file (includes both parameterless and parameterized)
        val decompiledFile = File("smb/src/main/kotlin/com/ivieleague/decompiler6502tokotlin/smb/generated/SMBDecompiled.kt")
        val functionSignatures = if (decompiledFile.exists()) {
            KotlinTestGenerator.parseSignaturesFromFile(decompiledFile).also {
                val parameterless = it.count { (_, params) -> params.isEmpty() }
                val withParams = it.count { (_, params) -> params.isNotEmpty() }
                println("\nParsed ${it.size} functions from decompiled code:")
                println("  - $parameterless parameterless functions")
                println("  - $withParams functions with parameters")
            }
        } else {
            println("\n⚠️ SMBDecompiled.kt not found - will generate all tests (may have compile errors)")
            emptyMap()
        }

        // All function names are valid (we have their signatures)
        val validFunctions = functionSignatures.keys

        // Build map of decompiled function addresses for matching
        // Only include JSR targets to filter out internal labels
        val decompiledFunctionAddresses = if (mapper != null) {
            val result = mutableMapOf<Int, String>()
            val jsrTargetLabels = mapper.getJsrTargetLabels()
            var totalLabels = 0
            var jsrTargetCount = 0
            var validFunctionCount = 0

            for (label in mapper.getFunctionLabels()) {
                totalLabels++
                val funcName = AddressLabelMapper.labelToKotlinFunctionName(label)

                // Skip internal labels (not JSR targets)
                if (label !in jsrTargetLabels) continue
                jsrTargetCount++

                if (funcName in validFunctions) {
                    validFunctionCount++
                    mapper.getAddress(label)?.let { addr ->
                        result[addr] = funcName
                    }
                }
            }
            println("\nFiltering by JSR targets:")
            println("  Total labels: $totalLabels")
            println("  JSR targets: $jsrTargetCount")
            println("  Valid decompiled functions that are JSR targets: $validFunctionCount")
            println("Found ${result.size} decompiled function addresses for matching")
            result
        } else {
            emptyMap()
        }

        // Generate Kotlin tests with exact matches only (no fuzzy matching)
        // Output to smb module test directory so tests can be compiled and run
        val smbTestDir = File("smb/src/test/kotlin/com/ivieleague/decompiler6502tokotlin/smb/generated")
        // by Claude - Added romFilePath to load ROM data for functions that read from ROM tables
        val generator = KotlinTestGenerator(
            packageName = "com.ivieleague.decompiler6502tokotlin.smb.generated",
            functionNames = functionNames,
            validFunctions = validFunctions,
            functionSignatures = functionSignatures,
            fuzzyMatchThreshold = 0,  // Exact matches only - no fuzzy matching
            decompiledFunctionAddresses = decompiledFunctionAddresses,
            romFilePath = romFile.absolutePath
        )
        println("Using exact matches only (no fuzzy matching)")
        val testFile = File(smbTestDir, "GeneratedFunctionTests.kt")
        generator.generateTestFile(testData, testFile)
        println("✅ Generated test file: ${testFile.absolutePath}")

        // Also keep a copy in local/testgen for reference
        val localTestFile = File(outputDir, "GeneratedFunctionTests.kt")
        generator.generateTestFile(testData, localTestFile)

        // Print summary
        println("\n" + generator.generateSummary(testData))
    }

    @Test
    fun `capture specific functions only`() {
        val romFile = findRom()
        if (romFile == null) {
            println("⚠️ Skipping test - no ROM file found")
            return
        }

        val tasFile = findTasFile()
        if (tasFile == null) {
            println("⚠️ Skipping test - no TAS file found")
            return
        }

        // Example: Capture only specific functions of interest
        // These addresses are examples - replace with actual SMB function addresses
        val targetFunctions = setOf(
            0x8000,  // Reset vector area
            0x8057,  // Common subroutine
            // Add more function addresses here
        )

        val rom = NESLoader.load(romFile)
        val interp = BinaryInterpreter6502()
        NESLoader.loadIntoMemory(rom, interp.memory)

        val tasInputs = FM2Parser.parse(tasFile)
        val tracer = FunctionCallTracer(interp, targetFunctions = targetFunctions)

        val ppu = PPUStub()
        val controller = Controller()

        interp.memoryReadHook = { addr ->
            when (addr) {
                in 0x2000..0x2007 -> ppu.read(addr)
                0x4016 -> controller.read()
                0x4017 -> 0u
                else -> null
            }
        }

        interp.memoryWriteHook = { addr, value ->
            when (addr) {
                in 0x2000..0x2007 -> { ppu.write(addr, value); true }
                0x4016 -> { controller.writeStrobe(value); true }
                in 0x4000..0x4017 -> true
                else -> false
            }
        }

        tracer.attach()
        interp.reset()

        val cyclesPerFrame = 29780
        val vblankStartCycle = 27400
        val maxFrames = 500

        for (frame in 0 until maxFrames) {
            tracer.onFrameStart(frame)
            ppu.startFrame()

            val buttons = if (frame < tasInputs.size) tasInputs[frame].buttons else 0
            controller.setButtons(buttons)

            var frameCycles = 0
            var vblankSet = false

            while (frameCycles < cyclesPerFrame && !interp.halted) {
                val nmiEnabledBefore = ppu.isNmiEnabled()
                frameCycles += interp.step()
                tracer.onInstruction()

                if (!vblankSet && frameCycles >= vblankStartCycle) {
                    ppu.beginVBlank()
                    vblankSet = true
                    if (nmiEnabledBefore) {
                        interp.triggerNmi()
                        interp.handleInterrupts()
                    }
                }
            }

            ppu.endVBlank()
        }

        tracer.detach()

        println("Captured ${tracer.getCaptureCount()} calls to ${targetFunctions.size} target functions")
        for (addr in targetFunctions) {
            val calls = tracer.getCapturesForFunction(addr)
            println("  0x${addr.toString(16).uppercase()}: ${calls.size} captures")
        }
    }
}
