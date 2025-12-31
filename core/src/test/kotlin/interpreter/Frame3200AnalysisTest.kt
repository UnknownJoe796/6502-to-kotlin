package com.ivieleague.decompiler6502tokotlin.interpreter

import java.io.File
import kotlin.test.Test

class Frame3200AnalysisTest {

    // SMB memory addresses
    val OperMode = 0x0770
    val OperMode_Task = 0x0772
    val WorldNumber = 0x075F
    val LevelNumber = 0x0760
    val AreaNumber = 0x075C
    val Player_X = 0x0086
    val Player_Y = 0x00CE
    val ScreenLeft_PageLoc = 0x071A
    val Player_State = 0x001D
    val GameTimerDisplay1 = 0x07F8
    val GameTimerDisplay2 = 0x07F9
    val GameTimerDisplay3 = 0x07FA
    val NumberofLives = 0x075A
    val FrameCounter = 0x09
    val SavedJoypad1Bits = 0x06FC  // Last read joypad state

    @Test
    fun `check inputs summary`() {
        val nmiFile = File("local/tas/nmi-filtered-inputs.txt")
        if (!nmiFile.exists()) {
            println("NMI input file not found")
            return
        }

        println("=== TAS Input Summary ===\n")

        // Parse NMI-filtered inputs
        data class InputEntry(val nmiIndex: Int, val originalFrame: Int, val joy: Int)
        val allInputs = mutableListOf<InputEntry>()
        nmiFile.readLines().forEach { line ->
            if (line.startsWith("#") || line.isBlank()) return@forEach
            val parts = line.split(" ")
            if (parts.size >= 3) {
                val nmiIndex = parts[0].toInt()
                val originalFrame = parts[1].toInt()
                val joy = parts[2].removePrefix("0x").toInt(16)
                allInputs.add(InputEntry(nmiIndex, originalFrame, joy))
            }
        }

        println("Total NMI-filtered inputs: ${allInputs.size}")
        println("NMI range: ${allInputs.firstOrNull()?.nmiIndex} to ${allInputs.lastOrNull()?.nmiIndex}")

        // Count button presses
        val buttonCounts = mutableMapOf<String, Int>()
        fun decodeButtons(joy: Int): List<String> {
            val buttons = mutableListOf<String>()
            if (joy and 0x80 != 0) buttons.add("A")
            if (joy and 0x40 != 0) buttons.add("B")
            if (joy and 0x20 != 0) buttons.add("Select")
            if (joy and 0x10 != 0) buttons.add("Start")
            if (joy and 0x08 != 0) buttons.add("Up")
            if (joy and 0x04 != 0) buttons.add("Down")
            if (joy and 0x02 != 0) buttons.add("Left")
            if (joy and 0x01 != 0) buttons.add("Right")
            return buttons
        }

        allInputs.forEach { entry ->
            decodeButtons(entry.joy).forEach { btn ->
                buttonCounts[btn] = (buttonCounts[btn] ?: 0) + 1
            }
        }

        println("\nButton press counts:")
        buttonCounts.entries.sortedByDescending { it.value }.forEach { (btn, count) ->
            println("  $btn: $count")
        }

        // Find all Down presses (pipe entries)
        println("\n=== All Down button presses (first 20) ===")
        var downCount = 0
        allInputs.filter { it.joy and 0x04 != 0 }.take(20).forEach { entry ->
            println("NMI ${entry.nmiIndex} (frame ${entry.originalFrame}): Down pressed")
            downCount++
        }
        val totalDowns = allInputs.count { it.joy and 0x04 != 0 }
        println("Total Down presses: $totalDowns")

        // Show segments of non-zero input
        println("\n=== Input activity regions (non-zero input) ===")
        var inActivity = false
        var activityStart = 0
        allInputs.forEachIndexed { idx, entry ->
            if (entry.joy != 0 && !inActivity) {
                inActivity = true
                activityStart = entry.nmiIndex
            } else if (entry.joy == 0 && inActivity) {
                inActivity = false
                if (entry.nmiIndex - activityStart > 5) {
                    println("Active: NMI $activityStart to ${entry.nmiIndex - 1}")
                }
            }
        }
    }

    @Test
    fun `analyze level transition at frame 1950`() {
        val interpRam = File("local/tas/interpreter-full-ram.bin")
        val fceuxRam = File("local/tas/fceux-full-ram.bin")

        if (!interpRam.exists() || !fceuxRam.exists()) {
            println("RAM dumps not found")
            return
        }

        val iBytes = interpRam.readBytes()
        val fBytes = fceuxRam.readBytes()

        println("=== Level Transition Analysis (Frames 1850-2050) ===\n")

        // Check frame-by-frame around the 1-1 to 1-2 transition
        for (frame in 1850..2050) {
            val offset = frame * 2048
            if (offset + 0x800 > iBytes.size || offset + 0x800 > fBytes.size) continue

            fun getByte(bytes: ByteArray, addr: Int) = bytes[offset + addr].toInt() and 0xFF

            val iFC = getByte(iBytes, FrameCounter)
            val fFC = getByte(fBytes, FrameCounter)
            val iMode = getByte(iBytes, OperMode)
            val fMode = getByte(fBytes, OperMode)
            val iModeTask = getByte(iBytes, OperMode_Task)
            val fModeTask = getByte(fBytes, OperMode_Task)
            val iWorld = getByte(iBytes, WorldNumber) + 1
            val fWorld = getByte(fBytes, WorldNumber) + 1
            val iLevel = getByte(iBytes, LevelNumber) + 1
            val fLevel = getByte(fBytes, LevelNumber) + 1
            val iPage = getByte(iBytes, ScreenLeft_PageLoc)
            val fPage = getByte(fBytes, ScreenLeft_PageLoc)

            // Calculate FC difference (handle wraparound)
            val fcDiff = (iFC - fFC + 256) % 256
            val normalizedDiff = if (fcDiff > 128) fcDiff - 256 else fcDiff

            // Only print when something changes or at key points
            val important = iMode != fMode || iModeTask != fModeTask ||
                           iLevel != fLevel || frame == 1850 || frame == 2050 ||
                           (frame % 25 == 0)

            if (important) {
                val markers = mutableListOf<String>()
                if (iMode != fMode) markers.add("MODE")
                if (iModeTask != fModeTask) markers.add("TASK")
                if (iLevel != fLevel) markers.add("LEVEL")

                val markerStr = if (markers.isNotEmpty()) " [${markers.joinToString(", ")}]" else ""

                println("Frame $frame:$markerStr")
                println("  FC: I=$iFC F=$fFC (diff=$normalizedDiff)")
                println("  Mode/Task: I=$iMode/$iModeTask F=$fMode/$fModeTask")
                println("  World-Level: I=${iWorld}-${iLevel} F=${fWorld}-${fLevel}")
                println("  Page: I=$iPage F=$fPage")
                println()
            }
        }
    }

    @Test
    fun `find available frame range`() {
        val interpRam = File("local/tas/interpreter-full-ram.bin")
        val fceuxRam = File("local/tas/fceux-full-ram.bin")

        if (!interpRam.exists() || !fceuxRam.exists()) {
            println("RAM dumps not found")
            return
        }

        val iBytes = interpRam.readBytes()
        val fBytes = fceuxRam.readBytes()

        val iFrames = iBytes.size / 2048
        val fFrames = fBytes.size / 2048

        println("Interpreter RAM dump: ${iBytes.size} bytes = $iFrames frames")
        println("FCEUX RAM dump: ${fBytes.size} bytes = $fFrames frames")
        println("Usable frames: 0 to ${minOf(iFrames, fFrames) - 1}")
    }

    @Test
    fun `track FrameCounter drift from start`() {
        val interpRam = File("local/tas/interpreter-full-ram.bin")
        val fceuxRam = File("local/tas/fceux-full-ram.bin")

        if (!interpRam.exists() || !fceuxRam.exists()) {
            println("RAM dumps not found")
            return
        }

        val iBytes = interpRam.readBytes()
        val fBytes = fceuxRam.readBytes()
        val maxFrame = minOf(iBytes.size, fBytes.size) / 2048 - 1

        println("=== FrameCounter Drift Analysis (first available frames) ===\n")

        var lastFCDiff = 0
        var lastWorld = -1
        var lastLevel = -1
        var lastMode = -1

        // Sample at key points to find when drift starts
        val framesToCheck = (0..maxFrame step 50).toList() +
            listOf(maxFrame)  // Always include last frame

        for (frame in framesToCheck.filter { it <= maxFrame }) {
            val offset = frame * 2048

            fun getByte(bytes: ByteArray, addr: Int) = bytes[offset + addr].toInt() and 0xFF

            val iFC = getByte(iBytes, FrameCounter)
            val fFC = getByte(fBytes, FrameCounter)
            val iMode = getByte(iBytes, OperMode)
            val fMode = getByte(fBytes, OperMode)
            val iWorld = getByte(iBytes, WorldNumber) + 1
            val fWorld = getByte(fBytes, WorldNumber) + 1
            val iLevel = getByte(iBytes, LevelNumber) + 1
            val fLevel = getByte(fBytes, LevelNumber) + 1
            val iPage = getByte(iBytes, ScreenLeft_PageLoc)
            val fPage = getByte(fBytes, ScreenLeft_PageLoc)
            val iX = getByte(iBytes, Player_X)
            val fX = getByte(fBytes, Player_X)
            val iJoy = getByte(iBytes, SavedJoypad1Bits)
            val fJoy = getByte(fBytes, SavedJoypad1Bits)

            // Calculate FC difference (handle wraparound)
            val fcDiff = (iFC - fFC + 256) % 256
            val normalizedDiff = if (fcDiff > 128) fcDiff - 256 else fcDiff

            // Detect transition points
            val modeChanged = iMode != lastMode || fMode != lastMode
            val levelChanged = iWorld != lastWorld || iLevel != lastLevel ||
                              fWorld != lastWorld || fLevel != lastLevel
            val driftChanged = normalizedDiff != lastFCDiff

            if (frame < 200 || driftChanged || modeChanged || levelChanged || frame == maxFrame) {
                val marker = when {
                    modeChanged -> " [MODE CHANGE]"
                    levelChanged -> " [LEVEL CHANGE]"
                    driftChanged -> " [DRIFT CHANGED: $lastFCDiff → $normalizedDiff]"
                    else -> ""
                }

                println("Frame $frame:$marker")
                println("  FC: I=$iFC F=$fFC (diff=$normalizedDiff)")
                println("  Mode: I=$iMode F=$fMode")
                println("  World-Level: I=${iWorld}-${iLevel} F=${fWorld}-${fLevel}")
                println("  Page: I=$iPage F=$fPage, X: I=$iX F=$fX")
                println("  Joy: I=0x${iJoy.toString(16).padStart(2,'0')} F=0x${fJoy.toString(16).padStart(2,'0')}")
                println()
            }

            lastFCDiff = normalizedDiff
            lastWorld = iWorld
            lastLevel = iLevel
            lastMode = iMode
        }
    }

    @Test
    fun `analyze divergence at frame 3200`() {
        val interpRam = File("local/tas/interpreter-full-ram.bin")
        val fceuxRam = File("local/tas/fceux-full-ram.bin")

        if (!interpRam.exists() || !fceuxRam.exists()) {
            println("RAM dumps not found")
            return
        }

        val iBytes = interpRam.readBytes()
        val fBytes = fceuxRam.readBytes()

        println("=== Detailed Analysis Around Frame 3200 ===\n")

        // Check earlier to find when divergence started
        for (frame in listOf(1000, 1500, 2000, 2200, 2400, 2600, 2800, 2900, 2950, 3000, 3050, 3100, 3150, 3180, 3190, 3195, 3200, 3205, 3210, 3220, 3250, 3300)) {
            val offset = frame * 2048
            if (offset + 0x800 > iBytes.size || offset + 0x800 > fBytes.size) continue

            fun getByte(bytes: ByteArray, addr: Int) = bytes[offset + addr].toInt() and 0xFF

            val iWorld = getByte(iBytes, WorldNumber) + 1
            val fWorld = getByte(fBytes, WorldNumber) + 1
            val iLevel = getByte(iBytes, LevelNumber) + 1
            val fLevel = getByte(fBytes, LevelNumber) + 1
            val iPage = getByte(iBytes, ScreenLeft_PageLoc)
            val fPage = getByte(fBytes, ScreenLeft_PageLoc)
            val iX = getByte(iBytes, Player_X)
            val fX = getByte(fBytes, Player_X)
            val iY = getByte(iBytes, Player_Y)
            val fY = getByte(fBytes, Player_Y)
            val iMode = getByte(iBytes, OperMode)
            val fMode = getByte(fBytes, OperMode)
            val iState = getByte(iBytes, Player_State)
            val fState = getByte(fBytes, Player_State)
            val iLives = getByte(iBytes, NumberofLives)
            val fLives = getByte(fBytes, NumberofLives)
            val iFC = getByte(iBytes, FrameCounter)
            val fFC = getByte(fBytes, FrameCounter)

            val diff = if (iPage != fPage || iWorld != fWorld || iLevel != fLevel || iMode != fMode) " ← DIFF" else ""

            println("Frame $frame:$diff")
            println("  World-Level: I=${iWorld}-${iLevel}  F=${fWorld}-${fLevel}")
            println("  Page: I=$iPage  F=$fPage")
            println("  Player X,Y: I=($iX,$iY)  F=($fX,$fY)")
            println("  OperMode: I=$iMode  F=$fMode")
            println("  State: I=$iState  F=$fState")
            println("  Lives: I=$iLives  F=$fLives")
            println("  FC: I=$iFC  F=$fFC")
            println()
        }
    }
}
