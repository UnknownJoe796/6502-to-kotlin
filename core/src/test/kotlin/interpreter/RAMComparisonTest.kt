package com.ivieleague.decompiler6502tokotlin.interpreter

import java.io.File
import kotlin.test.Test

/**
 * Compare RAM dumps between FCEUX and our interpreter to find divergence points.
 */
class RAMComparisonTest {

    private val frameSize = 2048  // $0000-$07FF

    // Key game state addresses and their names
    private val keyAddresses = mapOf(
        0x09 to "FrameCounter",
        0x0770 to "OperMode",
        0x075F to "GameEngineSubroutine",
        0x0772 to "OperMode_Task",
        0x0086 to "Player_X_Position",
        0x00CE to "Player_Y_Position",
        0x006D to "Player_PageLoc",
        0x0057 to "Player_X_Speed",
        0x009F to "Player_Y_Speed",
        0x001D to "Player_State",
        0x0754 to "PlayerSize",
        0x0756 to "PlayerStatus",
        0x0761 to "NumberofLives",
        0x075C to "WorldNumber",
        0x075D to "LevelNumber",
        0x0700 to "GamePauseStatus",
        0x0704 to "SwimmingFlag",
        0x071A to "ScreenLeft_PageLoc",
        0x071C to "ScreenLeft_X_Pos",
        0x06A0 to "BlockBufferColumnPos",
        0x00FB to "JoypadBitMask",
        0x0016 to "SavedJoypad1Bits"
    )

    @Test
    fun `find first byte-level RAM divergence`() {
        val fceuxRam = File("local/tas/fceux-full-ram.bin")
        val interpRam = File("local/tas/interpreter-full-ram.bin")

        if (!fceuxRam.exists() || !interpRam.exists()) {
            println("RAM dump files not found. Run FCEUX and interpreter tests first.")
            return
        }

        val fceuxData = fceuxRam.readBytes()
        val interpData = interpRam.readBytes()

        val fceuxFrames = fceuxData.size / frameSize
        val interpFrames = interpData.size / frameSize

        println("FCEUX frames: $fceuxFrames, Interpreter frames: $interpFrames")
        println("Checking ALL RAM bytes (not just key addresses)")

        // Find sync points
        var fceuxSyncFrame = -1
        var interpSyncFrame = -1

        for (frame in 0 until fceuxFrames) {
            val offset = frame * frameSize
            val mode = fceuxData[offset + 0x770].toInt() and 0xFF
            val fc = fceuxData[offset + 0x09].toInt() and 0xFF
            if (mode == 1 && fc == 0 && frame > 0) {
                val prevFC = fceuxData[(frame - 1) * frameSize + 0x09].toInt() and 0xFF
                if (prevFC > 0) {
                    fceuxSyncFrame = frame
                    break
                }
            }
        }

        for (frame in 0 until interpFrames) {
            val offset = frame * frameSize
            val mode = interpData[offset + 0x770].toInt() and 0xFF
            val fc = interpData[offset + 0x09].toInt() and 0xFF
            if (mode == 1 && fc == 0 && frame > 0) {
                val prevFC = interpData[(frame - 1) * frameSize + 0x09].toInt() and 0xFF
                if (prevFC > 0) {
                    interpSyncFrame = frame
                    break
                }
            }
        }

        println("FCEUX FC resets at frame $fceuxSyncFrame")
        println("Interpreter FC resets at frame $interpSyncFrame")

        if (fceuxSyncFrame < 0 || interpSyncFrame < 0) {
            println("Could not find sync point!")
            return
        }

        val frameDiff = interpSyncFrame - fceuxSyncFrame
        println("Frame offset: Interpreter is $frameDiff frames behind FCEUX")
        println("\n=== BYTE-BY-BYTE COMPARISON (checking all 2048 bytes) ===")
        println("Skipping offset 0 (initial RAM state differences)")

        // Compare every byte, starting from offset 1 to skip initial RAM differences
        val comparableFrames = minOf(fceuxFrames - fceuxSyncFrame, interpFrames - interpSyncFrame)
        for (offset in 1 until comparableFrames) {
            val fFrame = fceuxSyncFrame + offset
            val iFrame = interpSyncFrame + offset
            if (fFrame >= fceuxFrames || iFrame >= interpFrames) break

            val fOffset = fFrame * frameSize
            val iOffset = iFrame * frameSize

            val fFC = fceuxData[fOffset + 0x09].toInt() and 0xFF
            val iFC = interpData[iOffset + 0x09].toInt() and 0xFF

            // Compare all RAM bytes
            val diffs = mutableListOf<String>()
            for (addr in 0 until frameSize) {
                val fVal = fceuxData[fOffset + addr].toInt() and 0xFF
                val iVal = interpData[iOffset + addr].toInt() and 0xFF
                if (fVal != iVal) {
                    val addrName = keyAddresses[addr] ?: "Unknown"
                    diffs.add("\$${addr.toString(16).padStart(4,'0')} ($addrName): F=\$${fVal.toString(16).padStart(2,'0')} I=\$${iVal.toString(16).padStart(2,'0')}")
                }
            }

            if (diffs.isNotEmpty()) {
                println("\n=== FIRST DIVERGENCE at offset $offset (FCEUX frame $fFrame, Interp frame $iFrame) ===")
                println("FrameCounter: FCEUX=$fFC, Interp=$iFC")
                println("Found ${diffs.size} byte differences:")
                diffs.take(20).forEach { println("  $it") }
                if (diffs.size > 20) {
                    println("  ... and ${diffs.size - 20} more")
                }
                return
            }
        }

        println("\n✅ NO DIVERGENCE FOUND in $comparableFrames comparable frames!")
    }

    @Test
    fun `find first full RAM divergence`() {
        val fceuxRam = File("local/tas/fceux-full-ram.bin")
        val interpRam = File("local/tas/interpreter-full-ram.bin")

        if (!fceuxRam.exists() || !interpRam.exists()) {
            println("RAM dump files not found. Run FCEUX and interpreter tests first.")
            return
        }

        val fceuxData = fceuxRam.readBytes()
        val interpData = interpRam.readBytes()

        val fceuxFrames = fceuxData.size / frameSize
        val interpFrames = interpData.size / frameSize
        val maxFrames = minOf(fceuxFrames, interpFrames)

        println("FCEUX frames: $fceuxFrames, Interpreter frames: $interpFrames")

        // Find when FC first resets to 0 after game starts (this is when games sync)
        // First find when OperMode=1, then find when FC=0 after that
        var fceuxSyncFrame = -1
        var interpSyncFrame = -1

        for (frame in 0 until fceuxFrames) {
            val offset = frame * frameSize
            val mode = fceuxData[offset + 0x770].toInt() and 0xFF
            val fc = fceuxData[offset + 0x09].toInt() and 0xFF
            if (mode == 1 && fc == 0 && frame > 0) {
                // Verify previous frame had FC > 0 (actual reset, not just early frame)
                val prevFC = fceuxData[(frame - 1) * frameSize + 0x09].toInt() and 0xFF
                if (prevFC > 0) {
                    fceuxSyncFrame = frame
                    break
                }
            }
        }

        for (frame in 0 until interpFrames) {
            val offset = frame * frameSize
            val mode = interpData[offset + 0x770].toInt() and 0xFF
            val fc = interpData[offset + 0x09].toInt() and 0xFF
            if (mode == 1 && fc == 0 && frame > 0) {
                val prevFC = interpData[(frame - 1) * frameSize + 0x09].toInt() and 0xFF
                if (prevFC > 0) {
                    interpSyncFrame = frame
                    break
                }
            }
        }

        println("FCEUX FC resets at frame $fceuxSyncFrame")
        println("Interpreter FC resets at frame $interpSyncFrame")

        // Games sync when their FrameCounter resets. Compare using FC-aligned frames:
        // FCEUX frame (fceuxSyncFrame + N) should match Interp frame (interpSyncFrame + N)
        if (fceuxSyncFrame < 0 || interpSyncFrame < 0) {
            println("Could not find sync point!")
            return
        }

        val frameDiff = interpSyncFrame - fceuxSyncFrame
        println("Frame offset: Interpreter is $frameDiff frames behind FCEUX")
        println("\n=== FC-ALIGNED COMPARISON (from sync point) ===")

        // Compare FC-aligned: FCEUX frame F with Interp frame F+frameDiff
        val comparableFrames = minOf(fceuxFrames - fceuxSyncFrame, interpFrames - interpSyncFrame)
        for (offset in 0 until comparableFrames) {
            val fFrame = fceuxSyncFrame + offset
            val iFrame = interpSyncFrame + offset
            if (fFrame >= fceuxFrames || iFrame >= interpFrames) break

            val fOffset = fFrame * frameSize
            val iOffset = iFrame * frameSize

            // Check key state variables
            val keyDiffs = mutableListOf<String>()
            for ((addr, name) in keyAddresses) {
                if (addr >= frameSize) continue
                val fVal = fceuxData[fOffset + addr].toInt() and 0xFF
                val iVal = interpData[iOffset + addr].toInt() and 0xFF
                if (fVal != iVal) {
                    keyDiffs.add("$name(\$${addr.toString(16)}): F=\$${fVal.toString(16).padStart(2,'0')} I=\$${iVal.toString(16).padStart(2,'0')}")
                }
            }

            if (keyDiffs.isNotEmpty()) {
                val fFC = fceuxData[fOffset + 0x09].toInt() and 0xFF
                val iFC = interpData[iOffset + 0x09].toInt() and 0xFF
                println("\n=== FIRST DIVERGENCE at offset $offset (FCEUX frame $fFrame, Interp frame $iFrame) ===")
                println("FrameCounter: FCEUX=$fFC, Interp=$iFC")
                println("Key differences:")
                keyDiffs.forEach { println("  $it") }

                // Show context: previous 5 offsets
                println("\nContext (offsets ${offset-5} to $offset):")
                for (ctxOfs in (offset-5)..offset) {
                    if (ctxOfs < 0) continue
                    val ctxFFrame = fceuxSyncFrame + ctxOfs
                    val ctxIFrame = interpSyncFrame + ctxOfs
                    if (ctxFFrame >= fceuxFrames || ctxIFrame >= interpFrames) continue
                    val fo = ctxFFrame * frameSize
                    val io = ctxIFrame * frameSize
                    val ffFC = fceuxData[fo + 0x09].toInt() and 0xFF
                    val iiFC = interpData[io + 0x09].toInt() and 0xFF
                    val fX = fceuxData[fo + 0x86].toInt() and 0xFF
                    val iX = interpData[io + 0x86].toInt() and 0xFF
                    val fY = fceuxData[fo + 0xCE].toInt() and 0xFF
                    val iY = interpData[io + 0xCE].toInt() and 0xFF
                    val fPage = fceuxData[fo + 0x6D].toInt() and 0xFF
                    val iPage = interpData[io + 0x6D].toInt() and 0xFF
                    val fJoy = fceuxData[fo + 0x16].toInt() and 0xFF
                    val iJoy = interpData[io + 0x16].toInt() and 0xFF
                    val fTask = fceuxData[fo + 0x772].toInt() and 0xFF
                    val iTask = interpData[io + 0x772].toInt() and 0xFF
                    val fState = fceuxData[fo + 0x1D].toInt() and 0xFF
                    val iState = interpData[io + 0x1D].toInt() and 0xFF
                    val marker = if (ctxOfs == offset) " <-- DIVERGE" else ""
                    println("  Ofs$ctxOfs (F$ctxFFrame/I$ctxIFrame): FC[$ffFC/$iiFC] Task[$fTask/$iTask] State[$fState/$iState] X[$fX/$iX] Y[$fY/$iY] Page[$fPage/$iPage] Joy[\$${fJoy.toString(16)}/$${iJoy.toString(16)}]$marker")
                }
                break
            }
        }

        println("\nIf no divergence reported, games match on all key addresses!")
    }

    @Test
    fun `trace screen routine task divergence`() {
        val fceuxRam = File("local/tas/fceux-full-ram.bin")
        val interpRam = File("local/tas/interpreter-full-ram.bin")

        if (!fceuxRam.exists() || !interpRam.exists()) {
            println("RAM dump files not found.")
            return
        }

        val fceuxData = fceuxRam.readBytes()
        val interpData = interpRam.readBytes()

        println("=== SCREEN ROUTINE TASK PROGRESSION ===\n")
        println("Tracking ScreenRoutineTask ($073C), ScreenTimer ($07A0), IntervalTimerControl ($077F)...\n")

        println("Frame | ScrTask  | ScrTimer | IntCtrl  | Mode | Task | Notes")
        println("-".repeat(100))

        var lastFScrTask = -1
        var lastIScrTask = -1

        for (frame in 0 until 60) {  // Start from frame 0 to see initial state
            val fOffset = frame * frameSize
            val iOffset = frame * frameSize

            val fScrTask = fceuxData[fOffset + 0x073C].toInt() and 0xFF
            val iScrTask = interpData[iOffset + 0x073C].toInt() and 0xFF
            val fScrTimer = fceuxData[fOffset + 0x07A0].toInt() and 0xFF
            val iScrTimer = interpData[iOffset + 0x07A0].toInt() and 0xFF
            val fIntCtrl = fceuxData[fOffset + 0x077F].toInt() and 0xFF
            val iIntCtrl = interpData[iOffset + 0x077F].toInt() and 0xFF
            val fTimerCtrl = fceuxData[fOffset + 0x0747].toInt() and 0xFF  // TimerControl - gates timer decrements
            val iTimerCtrl = interpData[iOffset + 0x0747].toInt() and 0xFF
            val fMode = fceuxData[fOffset + 0x0770].toInt() and 0xFF
            val iMode = interpData[iOffset + 0x0770].toInt() and 0xFF
            val fFC = fceuxData[fOffset + 0x09].toInt() and 0xFF  // FrameCounter
            val iFC = interpData[iOffset + 0x09].toInt() and 0xFF

            val notes = mutableListOf<String>()
            if (fScrTask != iScrTask) notes.add("SCRTASK")
            if (fScrTimer != iScrTimer) notes.add("SCRTIMER")
            if (fIntCtrl != iIntCtrl) notes.add("INTCTRL")
            if (fTimerCtrl != iTimerCtrl) notes.add("TIMERCTRL")
            if (fMode != iMode) notes.add("MODE")
            if (fFC != iFC) notes.add("FC")

            // Show all frames initially to understand timing
            println("$frame".padStart(5) + " | " +
                    "FC[$fFC/$iFC]".padStart(10) + " | " +
                    "IntCtrl[$fIntCtrl/$iIntCtrl]".padStart(14) + " | " +
                    "TmrCtrl[$fTimerCtrl/$iTimerCtrl]".padStart(14) + " | " +
                    "Mode[$fMode/$iMode]".padStart(10) + " | " +
                    notes.joinToString(", "))

            lastFScrTask = fScrTask
            lastIScrTask = iScrTask
        }

        // Find first frame where IntervalTimerControl differs
        println("\n=== FIRST INTERVAL TIMER CONTROL DIVERGENCE ===")
        for (frame in 0 until minOf(fceuxData.size / frameSize, interpData.size / frameSize)) {
            val fOffset = frame * frameSize
            val iOffset = frame * frameSize
            val fIntCtrl = fceuxData[fOffset + 0x077F].toInt() and 0xFF
            val iIntCtrl = interpData[iOffset + 0x077F].toInt() and 0xFF

            if (fIntCtrl != iIntCtrl) {
                println("Frame $frame: FCEUX IntervalTimerControl=$fIntCtrl, Interp=$iIntCtrl")

                // Show previous 10 frames
                println("\nContext (frames ${maxOf(0, frame-10)} to $frame):")
                for (ctx in maxOf(0, frame-10)..frame) {
                    val fo = ctx * frameSize
                    val io = ctx * frameSize
                    val fI = fceuxData[fo + 0x077F].toInt() and 0xFF
                    val iI = interpData[io + 0x077F].toInt() and 0xFF
                    val fTimerCtrl = fceuxData[fo + 0x0747].toInt() and 0xFF
                    val iTimerCtrl = interpData[io + 0x0747].toInt() and 0xFF
                    val fFC = fceuxData[fo + 0x09].toInt() and 0xFF
                    val iFC = interpData[io + 0x09].toInt() and 0xFF
                    val fMode = fceuxData[fo + 0x0770].toInt() and 0xFF
                    val iMode = interpData[io + 0x0770].toInt() and 0xFF
                    val marker = if (ctx == frame) " <-- FIRST DIFF" else ""
                    println("  F$ctx: IntCtrl=[$fI/$iI] TimerCtrl=[$fTimerCtrl/$iTimerCtrl] FC=[$fFC/$iFC] Mode=[$fMode/$iMode]$marker")
                }
                break
            }
        }
    }

    @Test
    fun `analyze block buffer divergence`() {
        val fceuxRam = File("local/tas/fceux-full-ram.bin")
        val interpRam = File("local/tas/interpreter-full-ram.bin")

        if (!fceuxRam.exists() || !interpRam.exists()) {
            println("RAM dump files not found.")
            return
        }

        val fceuxData = fceuxRam.readBytes()
        val interpData = interpRam.readBytes()

        println("=== BLOCK BUFFER ANALYSIS AROUND FRAME 177 ===\n")

        // Addresses related to area parsing
        val areaAddrs = mapOf(
            0x06A0 to "BlockBufferColumnPos",
            0x071F to "AreaParserTaskNum",
            0x0726 to "AreaDataOffset",
            0x0727 to "CurrentNTAddr_Low",
            0x0728 to "CurrentNTAddr_High",
            0x0729 to "AreaPointer",
            0x072A to "AreaDataOfsLoopCount",
            0x073C to "ScreenRoutineTask",  // KEY: Controls which screen routine runs (8 = AreaParserTaskControl)
            0x0742 to "ColumnSets",
            0x0743 to "CurrentPageLoc",
            0x0744 to "CurrentColumnPos",
            0x0745 to "BackloadingFlag",
            0x0748 to "BehindAreaParserFlag",
            0x0752 to "AreaNumber",
            0x0770 to "OperMode",
            0x0772 to "OperMode_Task",
            0x07A0 to "ScreenTimer"  // Timer that gates screen routines
        )

        println("Frame | BBColPos | ScrTask  | AreaTask | AreaOfs | Mode | Task | Timer | Notes")
        println("-".repeat(100))

        for (frame in 170..200) {
            val fOffset = frame * frameSize
            val iOffset = frame * frameSize

            val fBBCP = fceuxData[fOffset + 0x06A0].toInt() and 0xFF
            val iBBCP = interpData[iOffset + 0x06A0].toInt() and 0xFF
            val fAreaTask = fceuxData[fOffset + 0x071F].toInt() and 0xFF
            val iAreaTask = interpData[iOffset + 0x071F].toInt() and 0xFF
            val fAreaOfs = fceuxData[fOffset + 0x0726].toInt() and 0xFF
            val iAreaOfs = interpData[iOffset + 0x0726].toInt() and 0xFF
            val fMode = fceuxData[fOffset + 0x0770].toInt() and 0xFF
            val iMode = interpData[iOffset + 0x0770].toInt() and 0xFF
            val fTask = fceuxData[fOffset + 0x0772].toInt() and 0xFF
            val iTask = interpData[iOffset + 0x0772].toInt() and 0xFF
            val fScrTask = fceuxData[fOffset + 0x073C].toInt() and 0xFF
            val iScrTask = interpData[iOffset + 0x073C].toInt() and 0xFF
            val fTimer = fceuxData[fOffset + 0x07A0].toInt() and 0xFF
            val iTimer = interpData[iOffset + 0x07A0].toInt() and 0xFF

            val diffs = mutableListOf<String>()
            if (fBBCP != iBBCP) diffs.add("BBCP")
            if (fAreaTask != iAreaTask) diffs.add("AreaTask")
            if (fAreaOfs != iAreaOfs) diffs.add("AreaOfs")
            if (fTask != iTask) diffs.add("Task")
            if (fScrTask != iScrTask) diffs.add("ScrTask")
            if (fTimer != iTimer) diffs.add("Timer")

            val notes = if (diffs.isEmpty()) "" else "DIFF: ${diffs.joinToString(",")}"

            println("$frame".padStart(5) + " | " +
                    "[$fBBCP/$iBBCP]".padStart(8) + " | " +
                    "[$fScrTask/$iScrTask]".padStart(8) + " | " +
                    "[$fAreaTask/$iAreaTask]".padStart(8) + " | " +
                    "[$fAreaOfs/$iAreaOfs]".padStart(7) + " | " +
                    "[$fMode/$iMode]".padStart(4) + " | " +
                    "[$fTask/$iTask]".padStart(4) + " | " +
                    "[$fTimer/$iTimer]".padStart(5) + " | " +
                    notes)
        }

        // Dump full area parser state at frame 176 (just before divergence)
        println("\n=== FULL AREA PARSER STATE AT FRAME 176 ===")
        for ((addr, name) in areaAddrs.entries.sortedBy { it.key }) {
            val fVal = fceuxData[176 * frameSize + addr].toInt() and 0xFF
            val iVal = interpData[176 * frameSize + addr].toInt() and 0xFF
            val diff = if (fVal != iVal) " <-- DIFF" else ""
            println("  $name (\$${addr.toString(16).uppercase()}): FCEUX=$fVal Interp=$iVal$diff")
        }

        println("\n=== FULL AREA PARSER STATE AT FRAME 177 ===")
        for ((addr, name) in areaAddrs.entries.sortedBy { it.key }) {
            val fVal = fceuxData[177 * frameSize + addr].toInt() and 0xFF
            val iVal = interpData[177 * frameSize + addr].toInt() and 0xFF
            val diff = if (fVal != iVal) " <-- DIFF" else ""
            println("  $name (\$${addr.toString(16).uppercase()}): FCEUX=$fVal Interp=$iVal$diff")
        }

        // Check ALL RAM at frame 176 for any differences
        println("\n=== ALL RAM DIFFERENCES AT FRAME 176 ===")
        var diffCount = 0
        for (addr in 0 until frameSize) {
            val fVal = fceuxData[176 * frameSize + addr].toInt() and 0xFF
            val iVal = interpData[176 * frameSize + addr].toInt() and 0xFF
            if (fVal != iVal) {
                diffCount++
                if (diffCount <= 50) {
                    println("  \$${addr.toString(16).uppercase().padStart(4, '0')}: FCEUX=\$${fVal.toString(16).uppercase().padStart(2, '0')} Interp=\$${iVal.toString(16).uppercase().padStart(2, '0')}")
                }
            }
        }
        if (diffCount > 50) {
            println("  ... and ${diffCount - 50} more differences")
        }
        if (diffCount == 0) {
            println("  NO DIFFERENCES - RAM is identical at frame 176!")
        }
        println("Total differences at frame 176: $diffCount")
    }

    @Test
    fun `trace divergence progression`() {
        val fceuxRam = File("local/tas/fceux-full-ram.bin")
        val interpRam = File("local/tas/interpreter-full-ram.bin")

        if (!fceuxRam.exists() || !interpRam.exists()) {
            println("RAM dump files not found.")
            return
        }

        val fceuxData = fceuxRam.readBytes()
        val interpData = interpRam.readBytes()
        val maxFrames = minOf(fceuxData.size / frameSize, interpData.size / frameSize)

        // Extended key addresses for detailed comparison
        val extendedAddresses = keyAddresses + mapOf(
            0x06A0 to "BlockBufferColumnPos",
            0x071F to "AreaParserTaskNum",
            0x0726 to "AreaDataOffset",
            0x0729 to "AreaPointer",
            0x0727 to "CurrentNTAddr_Low",
            0x0728 to "CurrentNTAddr_High"
        )

        println("=== DIVERGENCE PROGRESSION ===")
        println("Tracking how differences evolve over time...\n")

        // Start from frame 43 (sync point), show state every 10 frames
        // and highlight when new differences appear
        var lastDiffCount = 0
        var lastDiffAddrs = setOf<Int>()

        for (frame in 43 until minOf(maxFrames, 500)) {
            val fOffset = frame * frameSize
            val iOffset = frame * frameSize

            val diffs = mutableMapOf<Int, Pair<Int, Int>>()  // addr -> (fceux, interp)
            for ((addr, _) in extendedAddresses) {
                if (addr >= frameSize) continue
                val fVal = fceuxData[fOffset + addr].toInt() and 0xFF
                val iVal = interpData[iOffset + addr].toInt() and 0xFF
                if (fVal != iVal) {
                    diffs[addr] = fVal to iVal
                }
            }

            val diffAddrs = diffs.keys
            val newDiffs = diffAddrs - lastDiffAddrs
            val resolvedDiffs = lastDiffAddrs - diffAddrs

            // Report if differences changed
            if (newDiffs.isNotEmpty() || resolvedDiffs.isNotEmpty() || (frame % 50 == 0 && diffs.isNotEmpty())) {
                val fFC = fceuxData[fOffset + 0x09].toInt() and 0xFF
                val iFC = interpData[iOffset + 0x09].toInt() and 0xFF
                val fTask = fceuxData[fOffset + 0x772].toInt() and 0xFF
                val iTask = interpData[iOffset + 0x772].toInt() and 0xFF
                val fX = fceuxData[fOffset + 0x86].toInt() and 0xFF
                val iX = interpData[iOffset + 0x86].toInt() and 0xFF
                val fPage = fceuxData[fOffset + 0x6D].toInt() and 0xFF
                val iPage = interpData[iOffset + 0x6D].toInt() and 0xFF

                println("Frame $frame: FC=$fFC Task=[$fTask/$iTask] X=[$fX/$iX] Page=[$fPage/$iPage]")

                if (newDiffs.isNotEmpty()) {
                    println("  NEW DIFFERENCES:")
                    for (addr in newDiffs) {
                        val (f, i) = diffs[addr]!!
                        val name = extendedAddresses[addr] ?: "Unknown"
                        println("    $name(\$${addr.toString(16)}): FCEUX=\$${f.toString(16).padStart(2,'0')} Interp=\$${i.toString(16).padStart(2,'0')}")
                    }
                }

                if (resolvedDiffs.isNotEmpty()) {
                    println("  RESOLVED: ${resolvedDiffs.map { extendedAddresses[it] }}")
                }

                if (diffs.size > 5 && frame % 50 == 0) {
                    println("  Total differences: ${diffs.size}")
                }
            }

            lastDiffCount = diffs.size
            lastDiffAddrs = diffAddrs
        }
    }

    @Test
    fun `find first key state divergence`() {
        val fceuxRam = File("local/tas/fceux-full-ram.bin")
        val interpRam = File("local/tas/interpreter-full-ram.bin")

        if (!fceuxRam.exists() || !interpRam.exists()) {
            println("RAM dump files not found. Run FCEUX and interpreter tests first.")
            return
        }

        val fceuxData = fceuxRam.readBytes()
        val interpData = interpRam.readBytes()

        val fceuxFrames = fceuxData.size / frameSize
        val interpFrames = interpData.size / frameSize
        val maxFrames = minOf(fceuxFrames, interpFrames)

        println("FCEUX frames: $fceuxFrames, Interpreter frames: $interpFrames")
        println("Comparing key game state addresses...\n")

        // Track when each key address first diverges
        val firstDivergence = mutableMapOf<Int, Int>()  // addr -> frame

        for (frame in 0 until maxFrames) {
            val fceuxOffset = frame * frameSize
            val interpOffset = frame * frameSize

            for ((addr, name) in keyAddresses) {
                if (addr >= frameSize) continue
                if (addr in firstDivergence) continue  // Already found divergence

                val fceuxByte = fceuxData[fceuxOffset + addr].toInt() and 0xFF
                val interpByte = interpData[interpOffset + addr].toInt() and 0xFF

                if (fceuxByte != interpByte) {
                    firstDivergence[addr] = frame
                }
            }
        }

        // Report results sorted by frame
        println("=== KEY STATE DIVERGENCE SUMMARY ===\n")
        val byFrame = firstDivergence.entries.sortedBy { it.value }
        for ((addr, frame) in byFrame) {
            val name = keyAddresses[addr] ?: "Unknown"
            val fceuxByte = fceuxData[frame * frameSize + addr].toInt() and 0xFF
            val interpByte = interpData[frame * frameSize + addr].toInt() and 0xFF
            println("Frame $frame: $name (\$${addr.toString(16).uppercase()}) diverges - FCEUX=\$${fceuxByte.toString(16).uppercase().padStart(2,'0')} vs Interp=\$${interpByte.toString(16).uppercase().padStart(2,'0')}")
        }

        // Find first frame where OperMode matches between both (game started)
        var syncedFrame = -1
        for (frame in 0 until maxFrames) {
            val fceuxOffset = frame * frameSize
            val interpOffset = frame * frameSize
            val fceuxMode = fceuxData[fceuxOffset + 0x770].toInt() and 0xFF
            val interpMode = interpData[interpOffset + 0x770].toInt() and 0xFF
            val fceuxFC = fceuxData[fceuxOffset + 0x09].toInt() and 0xFF
            val interpFC = interpData[interpOffset + 0x09].toInt() and 0xFF

            if (fceuxMode == interpMode && fceuxFC == interpFC && fceuxFC > 0) {
                syncedFrame = frame
                break
            }
        }

        println("\n=== SYNC ANALYSIS ===")
        if (syncedFrame >= 0) {
            println("First synced frame (matching OperMode + FrameCounter): $syncedFrame")

            // From synced frame, find when they diverge
            println("\nLooking for divergence after sync...")
            for (frame in syncedFrame until maxFrames) {
                val fceuxOffset = frame * frameSize
                val interpOffset = frame * frameSize

                // Check all key addresses
                val diffs = mutableListOf<String>()
                for ((addr, name) in keyAddresses) {
                    if (addr >= frameSize) continue
                    val fceuxByte = fceuxData[fceuxOffset + addr].toInt() and 0xFF
                    val interpByte = interpData[interpOffset + addr].toInt() and 0xFF
                    if (fceuxByte != interpByte) {
                        diffs.add("$name: F=\$${fceuxByte.toString(16).uppercase().padStart(2,'0')} I=\$${interpByte.toString(16).uppercase().padStart(2,'0')}")
                    }
                }

                if (diffs.isNotEmpty()) {
                    println("\nFirst divergence after sync at frame $frame:")
                    diffs.forEach { println("  $it") }

                    // Print a few frames of context
                    println("\nContext (frames ${frame-2} to ${frame+2}):")
                    for (ctx in (frame-2)..(frame+2)) {
                        if (ctx < 0 || ctx >= maxFrames) continue
                        val fo = ctx * frameSize
                        val io = ctx * frameSize
                        val fFC = fceuxData[fo + 0x09].toInt() and 0xFF
                        val iFC = interpData[io + 0x09].toInt() and 0xFF
                        val fX = fceuxData[fo + 0x86].toInt() and 0xFF
                        val iX = interpData[io + 0x86].toInt() and 0xFF
                        val fY = fceuxData[fo + 0xCE].toInt() and 0xFF
                        val iY = interpData[io + 0xCE].toInt() and 0xFF
                        val fMode = fceuxData[fo + 0x770].toInt() and 0xFF
                        val iMode = interpData[io + 0x770].toInt() and 0xFF
                        val fJoy = fceuxData[fo + 0x16].toInt() and 0xFF
                        val iJoy = interpData[io + 0x16].toInt() and 0xFF
                        val marker = if (ctx == frame) " <-- DIVERGE" else ""
                        println("  F$ctx: FC=[$fFC/$iFC] Mode=[$fMode/$iMode] X=[$fX/$iX] Y=[$fY/$iY] Joy=[\$${fJoy.toString(16)}/$${iJoy.toString(16)}]$marker")
                    }
                    break
                }
            }
        } else {
            println("No synced frame found - games never reached same state")
        }
    }

    @Test
    fun `trace frame counter timing`() {
        val fceuxRam = File("local/tas/fceux-full-ram.bin")
        val interpRam = File("local/tas/interpreter-full-ram.bin")

        if (!fceuxRam.exists() || !interpRam.exists()) {
            println("RAM dump files not found.")
            return
        }

        val fceuxData = fceuxRam.readBytes()
        val interpData = interpRam.readBytes()

        println("=== FRAME COUNTER TIMING ANALYSIS ===\n")
        println("Tracking FrameCounter ($09) changes...\n")

        println("Frame | FCEUX FC | Interp FC | Diff | Notes")
        println("-".repeat(60))

        var lastFceuxFC = -1
        var lastInterpFC = -1

        for (frame in 0 until 100) {
            val fOffset = frame * frameSize
            val iOffset = frame * frameSize

            val fFC = fceuxData[fOffset + 0x09].toInt() and 0xFF
            val iFC = interpData[iOffset + 0x09].toInt() and 0xFF
            val fMode = fceuxData[fOffset + 0x770].toInt() and 0xFF
            val iMode = interpData[iOffset + 0x770].toInt() and 0xFF

            val fChanged = fFC != lastFceuxFC
            val iChanged = iFC != lastInterpFC

            if (fChanged || iChanged || frame < 10 || fMode == 1) {
                val notes = mutableListOf<String>()
                if (fChanged) notes.add("FCEUX changed")
                if (iChanged) notes.add("Interp changed")
                if (fMode == 1 && (frame == 0 || fceuxData[(frame-1) * frameSize + 0x770].toInt() and 0xFF != 1)) {
                    notes.add("FCEUX game start")
                }
                if (iMode == 1 && (frame == 0 || interpData[(frame-1) * frameSize + 0x770].toInt() and 0xFF != 1)) {
                    notes.add("Interp game start")
                }

                println("$frame".padStart(5) + " | " +
                        "$fFC".padStart(8) + " | " +
                        "$iFC".padStart(9) + " | " +
                        "${iFC - fFC}".padStart(4) + " | " +
                        notes.joinToString(", "))

                lastFceuxFC = fFC
                lastInterpFC = iFC
            }
        }

        // Show a summary of the first 50 frames where FC changes
        println("\n=== FRAME COUNTER INCREMENT TIMING ===")
        println("When does each FrameCounter value first appear?\n")

        println("FC Value | FCEUX Frame | Interp Frame | Delta")
        println("-".repeat(50))

        for (targetFC in 0..20) {
            var fceuxFrame = -1
            var interpFrame = -1

            for (frame in 0 until 200) {
                val fOffset = frame * frameSize
                val iOffset = frame * frameSize
                val fFC = fceuxData[fOffset + 0x09].toInt() and 0xFF
                val iFC = interpData[iOffset + 0x09].toInt() and 0xFF

                if (fceuxFrame == -1 && fFC == targetFC) fceuxFrame = frame
                if (interpFrame == -1 && iFC == targetFC) interpFrame = frame

                if (fceuxFrame != -1 && interpFrame != -1) break
            }

            if (fceuxFrame != -1 || interpFrame != -1) {
                val delta = if (fceuxFrame != -1 && interpFrame != -1) interpFrame - fceuxFrame else 0
                println("$targetFC".padStart(8) + " | " +
                        (if (fceuxFrame != -1) "$fceuxFrame" else "N/A").padStart(11) + " | " +
                        (if (interpFrame != -1) "$interpFrame" else "N/A").padStart(12) + " | " +
                        "$delta".padStart(5))
            }
        }
    }

    @Test
    fun `diagnose RAM dump contents`() {
        val fceuxRam = File("local/tas/fceux-full-ram.bin")
        val interpRam = File("local/tas/interpreter-full-ram.bin")

        println("=== RAM DUMP DIAGNOSTIC ===\n")

        if (fceuxRam.exists()) {
            val data = fceuxRam.readBytes()
            val frames = data.size / frameSize
            println("FCEUX RAM dump: $frames frames (${data.size} bytes)")

            // Scan key addresses every 100 frames
            println("\nFCEUX state evolution:")
            println("Frame | FrameCtr | OperMode | Task | Player_X | Player_Y | PageLoc | Lives")
            println("-".repeat(80))
            for (frame in 0 until frames step 100) {
                val offset = frame * frameSize
                val fc = data[offset + 0x09].toInt() and 0xFF
                val mode = data[offset + 0x770].toInt() and 0xFF
                val task = data[offset + 0x772].toInt() and 0xFF
                val px = data[offset + 0x86].toInt() and 0xFF
                val py = data[offset + 0xCE].toInt() and 0xFF
                val page = data[offset + 0x6D].toInt() and 0xFF
                val lives = data[offset + 0x761].toInt() and 0xFF
                println("$frame".padStart(5) + " | " +
                        "$fc".padStart(8) + " | " +
                        "$mode".padStart(8) + " | " +
                        "$task".padStart(4) + " | " +
                        "$px".padStart(8) + " | " +
                        "$py".padStart(8) + " | " +
                        "$page".padStart(7) + " | " +
                        "$lives".padStart(5))
            }

            // Find first frame where FrameCounter changes from initial
            var firstChange = -1
            var prevFC = data[0x09].toInt() and 0xFF
            for (frame in 1 until frames) {
                val fc = data[frame * frameSize + 0x09].toInt() and 0xFF
                if (fc != prevFC) {
                    firstChange = frame
                    println("\nFrameCounter first changes at frame $frame: $prevFC -> $fc")
                    break
                }
            }
            if (firstChange == -1) {
                println("\nFrameCounter NEVER changed! (stayed at $prevFC)")
            }
        } else {
            println("FCEUX RAM dump NOT FOUND")
        }

        println("\n" + "=".repeat(80) + "\n")

        if (interpRam.exists()) {
            val data = interpRam.readBytes()
            val frames = data.size / frameSize
            println("Interpreter RAM dump: $frames frames (${data.size} bytes)")

            println("\nInterpreter state evolution:")
            println("Frame | FrameCtr | OperMode | Task | Player_X | Player_Y | PageLoc | Lives")
            println("-".repeat(80))
            for (frame in 0 until frames step 100) {
                val offset = frame * frameSize
                val fc = data[offset + 0x09].toInt() and 0xFF
                val mode = data[offset + 0x770].toInt() and 0xFF
                val task = data[offset + 0x772].toInt() and 0xFF
                val px = data[offset + 0x86].toInt() and 0xFF
                val py = data[offset + 0xCE].toInt() and 0xFF
                val page = data[offset + 0x6D].toInt() and 0xFF
                val lives = data[offset + 0x761].toInt() and 0xFF
                println("$frame".padStart(5) + " | " +
                        "$fc".padStart(8) + " | " +
                        "$mode".padStart(8) + " | " +
                        "$task".padStart(4) + " | " +
                        "$px".padStart(8) + " | " +
                        "$py".padStart(8) + " | " +
                        "$page".padStart(7) + " | " +
                        "$lives".padStart(5))
            }

            var firstChange = -1
            var prevFC = data[0x09].toInt() and 0xFF
            for (frame in 1 until frames) {
                val fc = data[frame * frameSize + 0x09].toInt() and 0xFF
                if (fc != prevFC) {
                    firstChange = frame
                    println("\nFrameCounter first changes at frame $frame: $prevFC -> $fc")
                    break
                }
            }
            if (firstChange == -1) {
                println("\nFrameCounter NEVER changed! (stayed at $prevFC)")
            }
        } else {
            println("Interpreter RAM dump NOT FOUND")
        }
    }

    @Test
    fun `trace screen timer progression`() {
        val fceuxRam = File("local/tas/fceux-full-ram.bin")
        val interpRam = File("local/tas/interpreter-full-ram.bin")

        if (!fceuxRam.exists() || !interpRam.exists()) {
            println("RAM dump files not found.")
            return
        }

        val fceuxData = fceuxRam.readBytes()
        val interpData = interpRam.readBytes()

        println("=== SCREEN TIMER PROGRESSION ===\n")
        println("Looking for when ScreenTimer first becomes 7 (set by ResetScreenTimer)...\n")

        var fceuxFirst7Frame = -1
        var interpFirst7Frame = -1

        for (frame in 0 until 200) {
            val fOffset = frame * frameSize
            val iOffset = frame * frameSize

            val fTimer = fceuxData[fOffset + 0x07A0].toInt() and 0xFF
            val iTimer = interpData[iOffset + 0x07A0].toInt() and 0xFF
            val fTask = fceuxData[fOffset + 0x073C].toInt() and 0xFF
            val iTask = interpData[iOffset + 0x073C].toInt() and 0xFF
            val fMode = fceuxData[fOffset + 0x0770].toInt() and 0xFF
            val iMode = interpData[iOffset + 0x0770].toInt() and 0xFF
            val fFC = fceuxData[fOffset + 0x09].toInt() and 0xFF
            val iFC = interpData[iOffset + 0x09].toInt() and 0xFF

            if (fceuxFirst7Frame == -1 && fTimer == 7) {
                fceuxFirst7Frame = frame
            }
            if (interpFirst7Frame == -1 && iTimer == 7) {
                interpFirst7Frame = frame
            }

            // Show all frames from 35 to 50 (game start area)
            if (frame in 35..50 || fTimer == 7 || iTimer == 7) {
                val notes = mutableListOf<String>()
                if (fTimer != iTimer) notes.add("TIMER_DIFF")
                if (fTask != iTask) notes.add("TASK_DIFF")

                println("F$frame: Mode[$fMode/$iMode] FC[$fFC/$iFC] ScrTask[$fTask/$iTask] Timer[$fTimer/$iTimer] ${notes.joinToString(", ")}")
            }
        }

        println("\n=== FIRST TIMER=7 ===")
        println("FCEUX first ScreenTimer=7 at frame: $fceuxFirst7Frame")
        println("Interp first ScreenTimer=7 at frame: $interpFirst7Frame")

        // Trace Mode and FC around game start (frames 35-50)
        println("\n=== MODE AND FC AROUND GAME START ===")
        for (frame in 35..55) {
            val fOffset = frame * frameSize
            val iOffset = frame * frameSize
            val fMode = fceuxData[fOffset + 0x770].toInt() and 0xFF
            val iMode = interpData[iOffset + 0x770].toInt() and 0xFF
            val fFC = fceuxData[fOffset + 0x09].toInt() and 0xFF
            val iFC = interpData[iOffset + 0x09].toInt() and 0xFF
            val fTask = fceuxData[fOffset + 0x772].toInt() and 0xFF
            val iTask = interpData[iOffset + 0x772].toInt() and 0xFF

            val notes = mutableListOf<String>()
            if (frame > 0 && fMode != (fceuxData[(frame-1) * frameSize + 0x770].toInt() and 0xFF)) notes.add("FCEUX Mode change!")
            if (frame > 0 && iMode != (interpData[(frame-1) * frameSize + 0x770].toInt() and 0xFF)) notes.add("Interp Mode change!")
            if (frame > 0 && fFC != (fceuxData[(frame-1) * frameSize + 0x09].toInt() and 0xFF)) notes.add("FCEUX FC change")
            if (frame > 0 && iFC != (interpData[(frame-1) * frameSize + 0x09].toInt() and 0xFF)) notes.add("Interp FC change")

            println("F$frame: Mode[$fMode/$iMode] FC[$fFC/$iFC] Task[$fTask/$iTask] ${notes.joinToString(", ")}")
        }

        // Find first frame where IntervalTimerControl diverges
        println("\n=== FIRST INTERVAL TIMER CONTROL DIVERGENCE ===")
        for (frame in 0 until 60) {
            val fOffset = frame * frameSize
            val iOffset = frame * frameSize
            val fIntCtrl = fceuxData[fOffset + 0x077F].toInt() and 0xFF
            val iIntCtrl = interpData[iOffset + 0x077F].toInt() and 0xFF
            if (fIntCtrl != iIntCtrl) {
                println("First divergence at frame $frame: FCEUX=$fIntCtrl, Interp=$iIntCtrl")

                // Show context
                println("\nContext (frames ${maxOf(0, frame-5)} to ${frame+5}):")
                for (ctx in maxOf(0, frame-5)..minOf(frame+5, 59)) {
                    val fo = ctx * frameSize
                    val io = ctx * frameSize
                    val fI = fceuxData[fo + 0x077F].toInt() and 0xFF
                    val iI = interpData[io + 0x077F].toInt() and 0xFF
                    val fTC = fceuxData[fo + 0x0747].toInt() and 0xFF
                    val iTC = interpData[io + 0x0747].toInt() and 0xFF
                    val fFC = fceuxData[fo + 0x09].toInt() and 0xFF
                    val iFC = interpData[io + 0x09].toInt() and 0xFF
                    val fMode = fceuxData[fo + 0x0770].toInt() and 0xFF
                    val iMode = interpData[io + 0x0770].toInt() and 0xFF
                    val marker = if (ctx == frame) " <-- FIRST DIFF" else ""
                    println("  F$ctx: Mode[$fMode/$iMode] FC[$fFC/$iFC] TimerCtrl[$fTC/$iTC] IntCtrl[$fI/$iI]$marker")
                }
                break
            }
        }

        // Find sync frames for FC-aligned comparison
        val fceuxFrames = fceuxData.size / frameSize
        val interpFrames = interpData.size / frameSize

        var fSyncFrame = -1
        var iSyncFrame = -1
        for (frame in 0 until fceuxFrames) {
            val mode = fceuxData[frame * frameSize + 0x770].toInt() and 0xFF
            val fc = fceuxData[frame * frameSize + 0x09].toInt() and 0xFF
            if (mode == 1 && fc == 0 && frame > 0) {
                val prevFC = fceuxData[(frame - 1) * frameSize + 0x09].toInt() and 0xFF
                if (prevFC > 0) { fSyncFrame = frame; break }
            }
        }
        for (frame in 0 until interpFrames) {
            val mode = interpData[frame * frameSize + 0x770].toInt() and 0xFF
            val fc = interpData[frame * frameSize + 0x09].toInt() and 0xFF
            if (mode == 1 && fc == 0 && frame > 0) {
                val prevFC = interpData[(frame - 1) * frameSize + 0x09].toInt() and 0xFF
                if (prevFC > 0) { iSyncFrame = frame; break }
            }
        }

        // Trace timer state using FC-aligned frames around divergence (game frame 134)
        println("\n=== FC-ALIGNED TIMER STATE (around game frame 130-140) ===")
        println("Showing IntervalTimerControl and ScreenTimer with FC alignment")
        println()
        for (gameFrame in 125..145) {
            val fFrame = fSyncFrame + gameFrame
            val iFrame = iSyncFrame + gameFrame
            if (fFrame >= fceuxFrames || iFrame >= interpFrames) continue

            val fOffset = fFrame * frameSize
            val iOffset = iFrame * frameSize

            val fIntCtrl = fceuxData[fOffset + 0x077F].toInt() and 0xFF
            val iIntCtrl = interpData[iOffset + 0x077F].toInt() and 0xFF
            val fScrTimer = fceuxData[fOffset + 0x07A0].toInt() and 0xFF
            val iScrTimer = interpData[iOffset + 0x07A0].toInt() and 0xFF
            val fScrTask = fceuxData[fOffset + 0x073C].toInt() and 0xFF
            val iScrTask = interpData[iOffset + 0x073C].toInt() and 0xFF
            val fBBCP = fceuxData[fOffset + 0x06A0].toInt() and 0xFF
            val iBBCP = interpData[iOffset + 0x06A0].toInt() and 0xFF
            val fFC = fceuxData[fOffset + 0x09].toInt() and 0xFF
            val iFC = interpData[iOffset + 0x09].toInt() and 0xFF

            val diffs = mutableListOf<String>()
            if (fIntCtrl != iIntCtrl) diffs.add("IntCtrl")
            if (fScrTimer != iScrTimer) diffs.add("ScrTmr")
            if (fScrTask != iScrTask) diffs.add("ScrTask")
            if (fBBCP != iBBCP) diffs.add("BBCP")

            println("GF$gameFrame (F$fFrame/I$iFrame): FC[$fFC/$iFC] IntCtrl[$fIntCtrl/$iIntCtrl] ScrTmr[$fScrTimer/$iScrTimer] ScrTask[$fScrTask/$iScrTask] BBCP[$fBBCP/$iBBCP] ${diffs.joinToString(",")}")
        }

        // Show context around when each first gets timer=7
        if (fceuxFirst7Frame > 0) {
            println("\n=== FCEUX CONTEXT AROUND FIRST TIMER=7 ===")
            for (frame in maxOf(0, fceuxFirst7Frame - 5)..minOf(fceuxFirst7Frame + 5, 200)) {
                val offset = frame * frameSize
                val timer = fceuxData[offset + 0x07A0].toInt() and 0xFF
                val task = fceuxData[offset + 0x073C].toInt() and 0xFF
                val mode = fceuxData[offset + 0x0770].toInt() and 0xFF
                val fc = fceuxData[offset + 0x09].toInt() and 0xFF
                val marker = if (frame == fceuxFirst7Frame) " <-- FIRST" else ""
                println("  F$frame: Mode=$mode FC=$fc Task=$task Timer=$timer$marker")
            }
        }

        if (interpFirst7Frame > 0) {
            println("\n=== INTERP CONTEXT AROUND FIRST TIMER=7 ===")
            for (frame in maxOf(0, interpFirst7Frame - 5)..minOf(interpFirst7Frame + 5, 200)) {
                val offset = frame * frameSize
                val timer = interpData[offset + 0x07A0].toInt() and 0xFF
                val task = interpData[offset + 0x073C].toInt() and 0xFF
                val mode = interpData[offset + 0x0770].toInt() and 0xFF
                val fc = interpData[offset + 0x09].toInt() and 0xFF
                val marker = if (frame == interpFirst7Frame) " <-- FIRST" else ""
                println("  F$frame: Mode=$mode FC=$fc Task=$task Timer=$timer$marker")
            }
        }
    }

    @Test
    fun `trace divergence in detail`() {
        val fceuxRam = File("local/tas/fceux-full-ram.bin")
        val interpRam = File("local/tas/interpreter-full-ram.bin")
        val fceuxIndexFile = File("local/tas/fceux-frame-index.txt")
        val interpIndexFile = File("local/tas/interpreter-frame-index.txt")

        if (!fceuxRam.exists() || !interpRam.exists()) {
            println("RAM dump files not found.")
            return
        }

        val fceuxData = fceuxRam.readBytes()
        val interpData = interpRam.readBytes()

        // Parse frame index files to get NMI-enabled status
        data class FrameInfo(val frame: Int, val nmiEnabled: Boolean)

        fun parseIndex(file: File): List<FrameInfo> {
            return file.readLines()
                .filter { !it.startsWith("#") && it.isNotBlank() }
                .map { line ->
                    val parts = line.trim().split(" ")
                    FrameInfo(parts[0].toInt(), parts[2] == "1")
                }
        }

        val fceuxFrames = if (fceuxIndexFile.exists()) parseIndex(fceuxIndexFile) else emptyList()
        val interpFrames = if (interpIndexFile.exists()) parseIndex(interpIndexFile) else emptyList()

        // Build NMI -> frame mapping
        val fceuxNmiFrames = fceuxFrames.filter { it.nmiEnabled }.map { it.frame }
        val interpNmiFrames = interpFrames.filter { it.nmiEnabled }.map { it.frame }

        println("=== NMI Frame Mapping ===")
        println("FCEUX NMI-enabled frames (first 20): ${fceuxNmiFrames.take(20)}")
        println("Interp NMI-enabled frames (first 20): ${interpNmiFrames.take(20)}")

        val maxNmi = minOf(fceuxNmiFrames.size, interpNmiFrames.size, 100)

        println("\n=== NMI-Aligned Comparison ===")
        println("Comparing game state at matching NMI counts...\n")

        for (nmi in 0 until maxNmi) {
            val fFrame = fceuxNmiFrames[nmi]
            val iFrame = interpNmiFrames[nmi]

            val fceuxOffset = fFrame * frameSize
            val interpOffset = iFrame * frameSize

            val fFC = fceuxData[fceuxOffset + 0x09].toInt() and 0xFF
            val iFC = interpData[interpOffset + 0x09].toInt() and 0xFF
            val fX = fceuxData[fceuxOffset + 0x86].toInt() and 0xFF
            val iX = interpData[interpOffset + 0x86].toInt() and 0xFF
            val fY = fceuxData[fceuxOffset + 0xCE].toInt() and 0xFF
            val iY = interpData[interpOffset + 0xCE].toInt() and 0xFF
            val fMode = fceuxData[fceuxOffset + 0x770].toInt() and 0xFF
            val iMode = interpData[interpOffset + 0x770].toInt() and 0xFF
            val fJoy = fceuxData[fceuxOffset + 0x16].toInt() and 0xFF
            val iJoy = interpData[interpOffset + 0x16].toInt() and 0xFF

            val match = fFC == iFC && fX == iX && fY == iY && fMode == iMode
            val marker = if (!match) " <-- DIFF" else ""

            if (nmi < 50 || !match) {
                println("NMI $nmi: F${fFrame}/I${iFrame} FC=[$fFC/$iFC] Mode=[$fMode/$iMode] X=[$fX/$iX] Y=[$fY/$iY] Joy=[\$${fJoy.toString(16)}/$${iJoy.toString(16)}]$marker")
            }

            if (!match && nmi >= 35) {  // After game starts
                println("\nFirst post-start divergence at NMI $nmi")
                println("Detailed state comparison:")
                for ((addr, name) in keyAddresses) {
                    if (addr >= frameSize) continue
                    val fVal = fceuxData[fceuxOffset + addr].toInt() and 0xFF
                    val iVal = interpData[interpOffset + addr].toInt() and 0xFF
                    val diff = if (fVal != iVal) " <-- DIFF" else ""
                    println("  $name: FCEUX=\$${fVal.toString(16).uppercase().padStart(2,'0')} Interp=\$${iVal.toString(16).uppercase().padStart(2,'0')}$diff")
                }
                break
            }
        }
    }
}
