@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.testgen

import com.ivieleague.decompiler6502tokotlin.interpreter.BinaryInterpreter6502
import com.ivieleague.decompiler6502tokotlin.interpreter.FM2Parser
import com.ivieleague.decompiler6502tokotlin.interpreter.NESLoader
import java.io.File
import kotlin.test.Test

/**
 * Captures function states using PC-based entry detection.
 *
 * This approach is more accurate than JSR-based capture because:
 * 1. We only capture functions we actually care about (decompiled ones)
 * 2. We capture by PC match, not by how the function was called
 * 3. Direct 1:1 correspondence between captured data and decompiled functions
 */
class PCBasedCaptureTest {

    // PPU stub with VBlank simulation
    class PPUStub {
        var ppuCtrl: UByte = 0u
            private set
        private var ppuStatus: UByte = 0x00u

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
        }

        fun endVBlank() {
            ppuStatus = (ppuStatus.toInt() and 0x7F).toUByte()
        }

        fun isNmiEnabled(): Boolean = (ppuCtrl.toInt() and 0x80) != 0
    }

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
    fun `capture decompiled functions using PC-based detection`() {
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

        val asmFile = File("smbdism.asm")
        if (!asmFile.exists()) {
            println("⚠️ Skipping test - smbdism.asm not found")
            return
        }

        val decompiledFile = File("smb/src/main/kotlin/com/ivieleague/decompiler6502tokotlin/smb/generated/SMBDecompiled.kt")
        if (!decompiledFile.exists()) {
            println("⚠️ Skipping test - SMBDecompiled.kt not found")
            return
        }

        println("=== PC-Based Function Capture ===")
        println("ROM: ${romFile.absolutePath}")
        println("TAS: ${tasFile.absolutePath}")
        println("ASM: ${asmFile.absolutePath}")
        println("Decompiled: ${decompiledFile.absolutePath}")

        // Load ROM data for verification
        val romData = romFile.readBytes()

        // Extract function metadata from decompiled code with ROM verification
        println("\n1. Extracting function metadata from decompiled code...")
        println("   Using ROM-verified address mapping for accuracy")
        val extractor = DecompiledFunctionExtractor.withRomVerifiedMapper(asmFile, romData)
        val allFunctions = extractor.extractFromFile(decompiledFile)
        val functionsWithAddress = allFunctions.filter { it.address != null }

        println("   Total decompiled functions: ${allFunctions.size}")
        println("   Functions with known addresses: ${functionsWithAddress.size}")
        println("   Functions without addresses: ${allFunctions.size - functionsWithAddress.size}")

        // Show sample of target functions
        println("\n   Sample target functions:")
        for (func in functionsWithAddress.take(10)) {
            val addrHex = func.address!!.toString(16).uppercase().padStart(4, '0')
            val params = if (func.parameters.isEmpty()) "()" else "(${func.parameters.joinToString(", ")})"
            println("      0x$addrHex: ${func.functionName}$params")
        }

        // Debug: Check DividePDiff address
        val dividePDiff = functionsWithAddress.find { it.sourceLabel == "DividePDiff" || it.functionName == "dividePDiff" }
        if (dividePDiff != null) {
            println("\n   DEBUG: DividePDiff found:")
            println("      functionName: ${dividePDiff.functionName}")
            println("      sourceLabel: ${dividePDiff.sourceLabel}")
            println("      address: 0x${dividePDiff.address?.toString(16)?.uppercase()}")
        } else {
            println("\n   DEBUG: DividePDiff NOT FOUND in extracted functions!")
        }

        // Load ROM
        println("\n2. Loading ROM and interpreter...")
        val rom = NESLoader.load(romFile)
        val interp = BinaryInterpreter6502()
        NESLoader.loadIntoMemory(rom, interp.memory)

        // Parse TAS
        val tasInputs = FM2Parser.parse(tasFile)
        println("   Loaded ${tasInputs.size} TAS frames")

        // Create PC-based tracer
        println("\n3. Creating PC-based tracer for ${functionsWithAddress.size} functions...")
        val tracer = PCBasedFunctionTracer.fromMetadata(interp, functionsWithAddress)

        // Set up PPU/controller stubs
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

        // Attach tracer
        tracer.attach()

        // Reset and run
        interp.reset()
        println("   Reset vector: \$${interp.cpu.PC.toString(16).uppercase()}")

        val cyclesPerFrame = 29780
        val vblankStartCycle = 27400
        val maxFrames = tasInputs.size

        println("\n4. Running $maxFrames frames with PC-based capture...")

        for (frame in 0 until maxFrames) {
            tracer.onFrameStart(frame)
            ppu.startFrame()

            val buttons = if (frame < tasInputs.size) tasInputs[frame].buttons else 0
            controller.setButtons(buttons)

            var frameCycles = 0
            var vblankSet = false
            var nmiTriggeredThisFrame = false

            while (frameCycles < cyclesPerFrame && !interp.halted) {
                val nmiEnabledBefore = ppu.isNmiEnabled()

                // PC-based detection: check BEFORE step
                tracer.beforeStep()

                val cycles = interp.step()
                frameCycles += cycles

                // Track instruction count
                tracer.afterStep()

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
            if (frame % 500 == 0 || frame == maxFrames - 1) {
                val uniqueFuncs = tracer.getCapturedFunctions().size
                println("   Frame $frame: ${tracer.getCaptureCount()} captures, $uniqueFuncs unique functions")
            }

            if (interp.halted) {
                println("   ⚠️ Interpreter halted at frame $frame")
                break
            }
        }

        tracer.detach()

        // Get captures
        val allCaptures = tracer.getCaptures()
        println("\n=== Capture Complete ===")
        println("Total captures: ${allCaptures.size}")
        println("Unique functions captured: ${tracer.getCapturedFunctions().size}")
        println("NMI stats: ${tracer.getNmiStats()}")

        // Select test cases
        val selector = TestCaseSelector(maxTestsPerFunction = 10)
        val selectedByFunction = selector.selectTestCases(allCaptures)

        println("\nFunctions with selected tests: ${selectedByFunction.size}")

        // Show statistics
        val stats = selector.getStatistics(allCaptures)
        println("\nTop 20 most-called functions:")
        for (funcStat in stats.functions.take(20)) {
            println("  $funcStat")
        }

        // Save to JSON
        val outputDir = File("local/testgen")
        outputDir.mkdirs()

        val testData = TestDataSerialization.createTestData(
            tasName = "pc-based-${tasFile.nameWithoutExtension}",
            totalFrames = maxFrames,
            allCaptures = allCaptures,
            selectedByFunction = selectedByFunction
        )

        val jsonFile = File(outputDir, "captured-tests-pc-based.json")
        TestDataSerialization.save(testData, jsonFile)
        println("\n✅ Saved test data to: ${jsonFile.absolutePath}")

        // Build function signatures from metadata
        val functionSignatures = functionsWithAddress.associate {
            it.functionName to it.parameters
        }

        // Generate Kotlin tests
        val smbTestDir = File("smb/src/test/kotlin/com/ivieleague/decompiler6502tokotlin/smb/generated")
        // by Claude - Added romFilePath to load ROM data for functions that read from ROM tables
        val generator = KotlinTestGenerator(
            packageName = "com.ivieleague.decompiler6502tokotlin.smb.generated",
            functionNames = functionsWithAddress.associate { it.address!! to it.functionName },
            validFunctions = functionSignatures.keys,
            functionSignatures = functionSignatures,
            fuzzyMatchThreshold = 0,
            decompiledFunctionAddresses = functionsWithAddress.associate { it.address!! to it.functionName },
            romFilePath = romFile.absolutePath
        )

        val testFile = File(smbTestDir, "GeneratedFunctionTests.kt")
        generator.generateTestFile(testData, testFile)
        println("✅ Generated test file: ${testFile.absolutePath}")

        // Print summary
        println("\n" + generator.generateSummary(testData))

        // Summary comparison
        println("\n=== Coverage Summary ===")
        println("Target functions (from decompiled code): ${functionsWithAddress.size}")
        println("Functions actually captured: ${tracer.getCapturedFunctions().size}")
        println("Coverage: ${tracer.getCapturedFunctions().size * 100 / functionsWithAddress.size}%")
    }
}
