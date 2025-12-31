@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.interpreter

import java.io.File
import java.io.PrintWriter
import kotlin.test.Test

/**
 * Supervised Execution Mode Test
 *
 * This test isolates single-frame computation bugs by:
 * 1. Copying FCEUX RAM state to the interpreter BEFORE each frame
 * 2. Running exactly one frame with the interpreter
 * 3. Comparing resulting RAM with FCEUX's next frame
 *
 * If a frame diverges, we know the bug is in THAT frame's computation,
 * not inherited from earlier errors. This eliminates cumulative drift
 * as a confounding factor.
 */
class SupervisedExecutionTest {

    // SMB memory addresses for categorizing divergence
    object Addr {
        // Critical - affects gameplay
        val CRITICAL = mapOf(
            0x0086 to "Player_X_Position",
            0x00CE to "Player_Y_Position",
            0x0057 to "Player_X_Speed",
            0x009F to "Player_Y_Speed",
            0x001D to "Player_State",
            0x0754 to "PlayerSize",
            0x075A to "NumberofLives",
            0x0770 to "OperMode",
            0x0772 to "OperMode_Task",
            0x075F to "WorldNumber",
            0x0760 to "LevelNumber",
            0x06FC to "SavedJoypad1Bits",
            0x000A to "A_B_Buttons",
            0x000C to "Left_Right_Buttons",
        )

        // Timing - may self-correct or be cosmetic
        val TIMING = mapOf(
            0x0009 to "FrameCounter",
            0x077F to "IntervalTimerControl",
            0x0787 to "GameTimerCtrlTimer",
        )

        // Cosmetic - doesn't affect gameplay logic
        val COSMETIC_RANGES = listOf(
            0x0200..0x02FF,  // OAM buffer
            0x0300..0x03FF,  // PPU update buffer
        )
    }

    data class Divergence(
        val address: Int,
        val fceux: Int,
        val interp: Int,
        val name: String?,
        val category: String
    )

    data class FrameResult(
        val frame: Int,
        val divergences: List<Divergence>,
        val criticalCount: Int,
        val timingCount: Int,
        val otherCount: Int
    )

    // PPU stub - simplified for supervised mode
    class PPUStub {
        var ppuCtrl: UByte = 0u
        private var ppuStatus: UByte = 0x00u
        private var scrollLatch = false
        private var addrLatch = false
        private var sprite0HitCycle = 0
        private var vblankActive = false

        fun read(addr: Int): UByte {
            return when (addr and 0x07) {
                0x02 -> {
                    sprite0HitCycle++
                    // Sprite 0 hit timing
                    when {
                        sprite0HitCycle < 250 -> ppuStatus = (ppuStatus.toInt() or 0x40).toUByte()
                        sprite0HitCycle < 670 -> ppuStatus = (ppuStatus.toInt() and 0xBF).toUByte()
                        else -> ppuStatus = (ppuStatus.toInt() or 0x40).toUByte()
                    }
                    val status = ppuStatus
                    ppuStatus = (ppuStatus.toInt() and 0x7F).toUByte()
                    scrollLatch = false
                    addrLatch = false
                    status
                }
                else -> 0u
            }
        }

        fun write(addr: Int, value: UByte) {
            when (addr and 0x07) {
                0x00 -> ppuCtrl = value
                0x05 -> scrollLatch = !scrollLatch
                0x06 -> addrLatch = !addrLatch
            }
        }

        fun startFrame() {
            ppuStatus = (ppuStatus.toInt() and 0x3F).toUByte()
            sprite0HitCycle = 0
        }

        fun beginVBlank() {
            ppuStatus = (ppuStatus.toInt() or 0xC0).toUByte()
            sprite0HitCycle = 0
            vblankActive = true
        }

        fun endVBlank() {
            ppuStatus = (ppuStatus.toInt() and 0x7F).toUByte()
            vblankActive = false
        }

        fun isNmiEnabled(): Boolean = (ppuCtrl.toInt() and 0x80) != 0
    }

    // Controller stub
    class Controller {
        private var buttons = 0
        private var shiftReg = 0
        private var strobe = false
        var debugEnabled = false
        var readCount = 0

        fun setButtons(b: Int) {
            if (debugEnabled && b != 0) println("  CTRL: setButtons(0x${b.toString(16)})")
            buttons = b
        }

        fun writeStrobe(value: UByte) {
            val newStrobe = (value.toInt() and 0x01) != 0
            if (newStrobe) {
                shiftReg = buttons
                if (debugEnabled) println("  CTRL: strobe ON, shiftReg=0x${shiftReg.toString(16)}")
                readCount = 0
            }
            strobe = newStrobe
        }

        fun read(): UByte {
            val bit = shiftReg and 0x01
            if (!strobe) shiftReg = shiftReg shr 1
            readCount++
            if (debugEnabled) println("  CTRL: read #$readCount -> bit=$bit (shiftReg now 0x${shiftReg.toString(16)})")
            return bit.toUByte()
        }
    }

    private fun findRom(): File? {
        return listOf("local/roms/smb.nes", "smb.nes", "../smb.nes")
            .map { File(it) }
            .firstOrNull { it.exists() }
    }

    private fun loadFceuxRam(): ByteArray? {
        val file = File("local/tas/fceux-full-ram.bin")
        return if (file.exists()) file.readBytes() else null
    }

    private fun loadFceuxInputs(): List<Int>? {
        val file = File("local/tas/nmi-filtered-inputs.txt")
        if (!file.exists()) return null
        return file.readLines()
            .filter { !it.startsWith("#") && it.isNotBlank() }
            .map { line ->
                val parts = line.trim().split(" ")
                parts[2].removePrefix("0x").toInt(16)
            }
    }

    private fun categorize(addr: Int): Pair<String?, String> {
        Addr.CRITICAL[addr]?.let { return it to "CRITICAL" }
        Addr.TIMING[addr]?.let { return it to "TIMING" }
        for (range in Addr.COSMETIC_RANGES) {
            if (addr in range) return null to "COSMETIC"
        }
        return null to "OTHER"
    }

    private fun compareRam(fceux: ByteArray, fceuxOffset: Int, interp: Memory6502): List<Divergence> {
        val divergences = mutableListOf<Divergence>()
        for (addr in 0 until 0x800) {
            val fceuxVal = fceux[fceuxOffset + addr].toInt() and 0xFF
            val interpVal = interp.readByte(addr).toInt()
            if (fceuxVal != interpVal) {
                val (name, category) = categorize(addr)
                divergences.add(Divergence(addr, fceuxVal, interpVal, name, category))
            }
        }
        return divergences
    }

    private fun copyFceuxRamToInterpreter(fceux: ByteArray, fceuxOffset: Int, memory: Memory6502) {
        for (addr in 0 until 0x800) {
            memory.writeByte(addr, (fceux[fceuxOffset + addr].toInt() and 0xFF).toUByte())
        }
    }

    @Test
    fun `supervised execution - single frame isolation`() {
        val romFile = findRom()
        if (romFile == null) {
            println("⚠️ Skipping test - no ROM file found")
            return
        }

        val fceuxRam = loadFceuxRam()
        if (fceuxRam == null) {
            println("⚠️ Skipping test - no FCEUX RAM dump found")
            return
        }

        val fceuxInputs = loadFceuxInputs()
        if (fceuxInputs == null) {
            println("⚠️ Skipping test - no FCEUX inputs found")
            return
        }

        val rom = NESLoader.load(romFile)
        val interp = BinaryInterpreter6502()
        NESLoader.loadIntoMemory(rom, interp.memory)

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
                0x4014 -> true
                0x4016 -> { controller.writeStrobe(value); true }
                in 0x4000..0x4017 -> true
                else -> false
            }
        }

        // Output files
        val reportFile = PrintWriter(File("local/tas/supervised-execution-report.txt"))
        val summaryFile = PrintWriter(File("local/tas/supervised-execution-summary.txt"))

        reportFile.println("# Supervised Execution Report")
        reportFile.println("# Each frame: copy FCEUX RAM -> run interpreter -> compare with FCEUX next frame")
        reportFile.println("# Divergences indicate single-frame computation bugs")
        reportFile.println()

        val maxFrames = minOf(fceuxRam.size / 2048 - 1, fceuxInputs.size, 1000) // Start with 1000 frames
        val results = mutableListOf<FrameResult>()

        val cyclesPerFrame = 29780
        val vblankStartCycle = 27400

        // NMI suppression frames (from previous analysis)
        val suppressNmiFrames = setOf(3, 5, 6, 42, 612, 926, 1944, 2443, 3814)

        println("Running supervised execution for $maxFrames frames...")
        println()

        for (frame in 0 until maxFrames) {
            // Step 1: Copy FCEUX RAM state to interpreter
            val fceuxOffset = frame * 2048
            copyFceuxRamToInterpreter(fceuxRam, fceuxOffset, interp.memory)

            // Step 2: Set up controller with FCEUX input for this frame
            val buttons = if (frame < fceuxInputs.size) fceuxInputs[frame] else 0
            controller.setButtons(buttons)

            // Step 3: Reset interpreter PC to where FCEUX would be
            // For supervised mode, we need to start from a consistent point
            // The NMI vector is the safest - it's where each frame's logic begins
            // But we also need to handle the main loop continuation

            // Actually, we should just run the frame loop normally
            // The RAM state includes the stack and PC implicitly through game state

            // Reset CPU state but keep RAM
            // This is tricky - we need to simulate starting from where FCEUX was
            // For now, let's just run from the reset vector and see what happens
            // Actually no - we should preserve the flow

            // Simpler approach: just run cycles and let the game code execute
            ppu.startFrame()

            var frameCycles = 0
            var nmiTriggered = false

            // Run frame cycles
            while (frameCycles < cyclesPerFrame && !interp.halted) {
                val nmiEnabledBefore = ppu.isNmiEnabled()
                val cycles = interp.step()
                frameCycles += cycles

                // VBlank and NMI
                if (!nmiTriggered && frameCycles >= vblankStartCycle) {
                    ppu.beginVBlank()
                    val suppressNmi = frame in suppressNmiFrames
                    if (nmiEnabledBefore && !suppressNmi) {
                        interp.triggerNmi()
                        interp.handleInterrupts()
                        nmiTriggered = true
                    }
                }
            }

            ppu.endVBlank()

            // Step 4: Compare with FCEUX's NEXT frame
            val nextFceuxOffset = (frame + 1) * 2048
            val divergences = compareRam(fceuxRam, nextFceuxOffset, interp.memory)

            val criticalCount = divergences.count { it.category == "CRITICAL" }
            val timingCount = divergences.count { it.category == "TIMING" }
            val otherCount = divergences.count { it.category == "OTHER" }

            val result = FrameResult(frame, divergences, criticalCount, timingCount, otherCount)
            results.add(result)

            // Report divergences
            if (divergences.isNotEmpty()) {
                val criticalDivs = divergences.filter { it.category == "CRITICAL" }
                val timingDivs = divergences.filter { it.category == "TIMING" }

                reportFile.println("=== Frame $frame ===")
                reportFile.println("Total divergences: ${divergences.size} (CRITICAL: $criticalCount, TIMING: $timingCount, OTHER: $otherCount)")

                if (criticalDivs.isNotEmpty()) {
                    reportFile.println("CRITICAL:")
                    for (div in criticalDivs) {
                        reportFile.println("  \$${div.address.toString(16).padStart(4, '0')} ${div.name}: FCEUX=${div.fceux} INTERP=${div.interp}")
                    }
                }

                if (timingDivs.isNotEmpty()) {
                    reportFile.println("TIMING:")
                    for (div in timingDivs) {
                        reportFile.println("  \$${div.address.toString(16).padStart(4, '0')} ${div.name}: FCEUX=${div.fceux} INTERP=${div.interp}")
                    }
                }

                // Show first 10 OTHER divergences
                val otherDivs = divergences.filter { it.category == "OTHER" }.take(10)
                if (otherDivs.isNotEmpty()) {
                    reportFile.println("OTHER (first 10):")
                    for (div in otherDivs) {
                        reportFile.println("  \$${div.address.toString(16).padStart(4, '0')}: FCEUX=${div.fceux} INTERP=${div.interp}")
                    }
                }
                reportFile.println()
            }

            // Progress indicator
            if (frame % 100 == 0) {
                println("Frame $frame: ${divergences.size} divergences (C:$criticalCount T:$timingCount O:$otherCount)")
            }
        }

        // Summary statistics
        val framesWithCritical = results.count { it.criticalCount > 0 }
        val framesWithAnyDivergence = results.count { it.divergences.isNotEmpty() }
        val cleanFrames = results.count { it.divergences.isEmpty() }

        summaryFile.println("# Supervised Execution Summary")
        summaryFile.println()
        summaryFile.println("Total frames analyzed: $maxFrames")
        summaryFile.println("Clean frames (no divergence): $cleanFrames")
        summaryFile.println("Frames with ANY divergence: $framesWithAnyDivergence")
        summaryFile.println("Frames with CRITICAL divergence: $framesWithCritical")
        summaryFile.println()

        // Find patterns
        summaryFile.println("## Divergence Patterns")
        summaryFile.println()

        // Which addresses diverge most often?
        val addressFrequency = mutableMapOf<Int, Int>()
        for (result in results) {
            for (div in result.divergences) {
                addressFrequency[div.address] = addressFrequency.getOrDefault(div.address, 0) + 1
            }
        }

        val topAddresses = addressFrequency.entries.sortedByDescending { it.value }.take(20)
        summaryFile.println("### Most Frequently Divergent Addresses")
        for ((addr, count) in topAddresses) {
            val (name, category) = categorize(addr)
            val displayName = name ?: "?"
            summaryFile.println("  \$${addr.toString(16).padStart(4, '0')} $displayName [$category]: $count frames")
        }
        summaryFile.println()

        // First frame with critical divergence
        val firstCritical = results.firstOrNull { it.criticalCount > 0 }
        if (firstCritical != null) {
            summaryFile.println("### First CRITICAL Divergence")
            summaryFile.println("Frame: ${firstCritical.frame}")
            for (div in firstCritical.divergences.filter { it.category == "CRITICAL" }) {
                summaryFile.println("  ${div.name}: FCEUX=${div.fceux} INTERP=${div.interp}")
            }
        }

        reportFile.close()
        summaryFile.close()

        println()
        println("=== SUPERVISED EXECUTION COMPLETE ===")
        println("Total frames: $maxFrames")
        println("Clean frames: $cleanFrames")
        println("Frames with CRITICAL divergence: $framesWithCritical")
        println()
        println("Reports written to:")
        println("  local/tas/supervised-execution-report.txt")
        println("  local/tas/supervised-execution-summary.txt")
    }

    @Test
    fun `supervised execution - trace specific frame`() {
        // This test runs supervised execution on a SINGLE frame with detailed tracing
        // Useful for debugging a specific divergent frame

        val targetFrame = 193  // Change this to investigate specific frames

        val romFile = findRom() ?: run { println("⚠️ No ROM"); return }
        val fceuxRam = loadFceuxRam() ?: run { println("⚠️ No FCEUX RAM"); return }
        val fceuxInputs = loadFceuxInputs() ?: run { println("⚠️ No inputs"); return }

        if (targetFrame >= fceuxRam.size / 2048 - 1) {
            println("⚠️ Target frame $targetFrame exceeds FCEUX dump")
            return
        }

        val rom = NESLoader.load(romFile)
        val interp = BinaryInterpreter6502()
        NESLoader.loadIntoMemory(rom, interp.memory)

        val ppu = PPUStub()
        val controller = Controller()

        // Detailed instruction trace
        val trace = StringBuilder()
        var instrCount = 0

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
                0x4014 -> true
                0x4016 -> { controller.writeStrobe(value); true }
                in 0x4000..0x4017 -> true
                else -> false
            }
        }

        // Copy FCEUX RAM for target frame
        val fceuxOffset = targetFrame * 2048
        copyFceuxRamToInterpreter(fceuxRam, fceuxOffset, interp.memory)

        // Log initial state
        println("=== Frame $targetFrame Detailed Trace ===")
        println()
        println("Initial state (copied from FCEUX):")
        println("  OperMode: ${interp.memory.readByte(0x0770)}")
        println("  Player_X: ${interp.memory.readByte(0x0086)}")
        println("  Player_Y: ${interp.memory.readByte(0x00CE)}")
        println("  FrameCounter: ${interp.memory.readByte(0x0009)}")
        println("  SavedJoypad: 0x${interp.memory.readByte(0x06FC).toString(16)}")
        println()

        // Set controller
        val buttons = if (targetFrame < fceuxInputs.size) fceuxInputs[targetFrame] else 0
        controller.setButtons(buttons)
        controller.debugEnabled = (buttons != 0)  // Debug when there's actual input
        println("Input buttons: 0x${buttons.toString(16)}")
        println()

        // Initialize PPU CTRL based on game mode - NMI is always enabled during gameplay
        val operMode = interp.memory.readByte(0x0770).toInt()
        if (operMode == 1) {
            // Gameplay mode - NMI must be enabled
            ppu.write(0x2000, 0x90u)  // Enable NMI (bit 7) + typical settings
        }

        // Run frame with tracing
        ppu.startFrame()
        val cyclesPerFrame = 29780
        val vblankStartCycle = 27400
        var frameCycles = 0
        var nmiTriggered = false
        val suppressNmi = targetFrame in setOf(3, 5, 6, 42, 612, 926, 1944, 2443, 3814)

        while (frameCycles < cyclesPerFrame && !interp.halted && instrCount < 50000) {
            val pc = interp.cpu.PC
            val nmiEnabledBefore = ppu.isNmiEnabled()

            // Log key addresses before step
            val cycles = interp.step()
            frameCycles += cycles
            instrCount++

            // VBlank
            if (!nmiTriggered && frameCycles >= vblankStartCycle) {
                ppu.beginVBlank()
                if (nmiEnabledBefore && !suppressNmi) {
                    trace.appendLine(">>> NMI triggered at cycle $frameCycles")
                    interp.triggerNmi()
                    interp.handleInterrupts()
                    nmiTriggered = true
                }
            }
        }

        ppu.endVBlank()

        // Compare with FCEUX next frame
        val nextFceuxOffset = (targetFrame + 1) * 2048
        val divergences = compareRam(fceuxRam, nextFceuxOffset, interp.memory)

        println("After frame execution:")
        println("  Instructions executed: $instrCount")
        println("  Total cycles: $frameCycles")
        println("  NMI triggered: $nmiTriggered")
        println()

        println("Final state:")
        println("  OperMode: ${interp.memory.readByte(0x0770)} (FCEUX: ${fceuxRam[nextFceuxOffset + 0x0770].toInt() and 0xFF})")
        println("  Player_X: ${interp.memory.readByte(0x0086)} (FCEUX: ${fceuxRam[nextFceuxOffset + 0x0086].toInt() and 0xFF})")
        println("  Player_Y: ${interp.memory.readByte(0x00CE)} (FCEUX: ${fceuxRam[nextFceuxOffset + 0x00CE].toInt() and 0xFF})")
        println("  FrameCounter: ${interp.memory.readByte(0x0009)} (FCEUX: ${fceuxRam[nextFceuxOffset + 0x0009].toInt() and 0xFF})")
        println("  SavedJoypad: 0x${interp.memory.readByte(0x06FC).toString(16)} (FCEUX: 0x${(fceuxRam[nextFceuxOffset + 0x06FC].toInt() and 0xFF).toString(16)})")
        println()

        println("Divergences: ${divergences.size}")
        val critical = divergences.filter { it.category == "CRITICAL" }
        if (critical.isNotEmpty()) {
            println("CRITICAL divergences:")
            for (div in critical) {
                println("  ${div.name ?: "$${div.address.toString(16)}"}: FCEUX=${div.fceux} INTERP=${div.interp} (diff=${div.interp - div.fceux})")
            }
        }
    }
}
