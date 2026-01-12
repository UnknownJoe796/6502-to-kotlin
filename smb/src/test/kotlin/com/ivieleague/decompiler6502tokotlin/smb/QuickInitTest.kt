@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.smb

import com.ivieleague.decompiler6502tokotlin.interpreter.BinaryInterpreter6502
import com.ivieleague.decompiler6502tokotlin.interpreter.FM2Parser
import com.ivieleague.decompiler6502tokotlin.hand.*
import com.ivieleague.decompiler6502tokotlin.smb.generated.*
import java.io.File
import kotlin.test.Test

/**
 * by Claude - Minimal test to debug initialization hang
 */
class QuickInitTest {

    @Test
    fun `quick init test`() {
        val romFile = listOf("../local/roms/smb.nes", "local/roms/smb.nes", "../smb.nes", "smb.nes")
            .map { File(it) }
            .firstOrNull { it.exists() }

        if (romFile == null) {
            println("Skipping: No ROM file found")
            return
        }

        println("STEP 1: Loading ROM")
        val romData = romFile.readBytes().toUByteArray()
        val prgRom = romData.sliceArray(16 until 16 + 0x8000)
        println("STEP 2: ROM loaded, size ${prgRom.size}")

        println("STEP 3: Creating interpreter")
        val interp = BinaryInterpreter6502()
        println("STEP 4: Interpreter created")

        println("STEP 5: Loading PRG into memory")
        for (i in prgRom.indices) {
            interp.memory.writeByte(0x8000 + i, prgRom[i])
        }
        println("STEP 6: PRG loaded")

        // Simple PPU stub
        var ppuStatus: UByte = 0x80u
        var ppuStatusReads = 0

        interp.memoryReadHook = { addr: Int ->
            when (addr) {
                0x2002 -> {
                    ppuStatusReads++
                    val status = ppuStatus
                    ppuStatus = if (ppuStatusReads > 3) 0x40u else 0x00u
                    status
                }
                in 0x2000..0x2007 -> 0u
                else -> null
            }
        }

        interp.memoryWriteHook = { addr: Int, _: UByte ->
            when (addr) {
                in 0x2000..0x2007 -> true
                0x4014 -> true
                in 0x4000..0x4017 -> true
                else -> false
            }
        }
        println("STEP 7: Hooks set up")

        println("STEP 8: Running RESET")
        interp.cpu.PC = 0x8000u
        var cycles = 0
        while (cycles < 50000) {
            val pc = interp.cpu.PC.toInt()
            if (pc == 0x8075) break
            interp.step()
            cycles++
        }
        println("STEP 9: RESET done in $cycles cycles")

        println("STEP 10: Running NMI")
        ppuStatus = 0x80u
        ppuStatusReads = 0
        interp.cpu.PC = 0x8082u
        cycles = 0
        while (cycles < 50000) {
            if (interp.memory.readByte(interp.cpu.PC.toInt()) == 0x40.toUByte()) break
            interp.step()
            cycles++
        }
        println("STEP 11: NMI done in $cycles cycles")

        println("STEP 12: Clearing memory")
        clearMemory()
        println("STEP 13: Memory cleared")

        println("STEP 14: Copying RAM")
        for (addr in 0x0000..0x07FF) {
            memory[addr] = interp.memory.readByte(addr)
        }
        println("STEP 15: RAM copied")

        println("STEP 16: Init ROM data")
        initializeRomData()
        println("STEP 17: ROM data initialized")

        println("SUCCESS: All init steps completed!")

        // Test one frame
        println("STEP 18: Running one decompiled frame")
        memory[0x2000] = 0x90u  // Enable NMI
        memory[0x06FC] = 0x00u  // No controller input

        // Run NMI equivalent
        soundEngine()
        println("STEP 19: soundEngine done")
        readJoypads()
        println("STEP 20: readJoypads done")
        pauseRoutine()
        println("STEP 21: pauseRoutine done")
        updateTopScore()
        println("STEP 22: updateTopScore done")
        moveSpritesOffscreen()
        println("STEP 23: moveSpritesOffscreen done")
        spriteShuffler()
        println("STEP 24: spriteShuffler done")

        // Timer decrements
        val timerControl = memory[0x0747].toInt()
        if (timerControl == 0) {
            var intervalCtrl = memory[0x077F].toInt()
            intervalCtrl = (intervalCtrl - 1) and 0xFF
            memory[0x077F] = intervalCtrl.toUByte()
        }
        println("STEP 25: Timers done")

        // Frame counter
        val fc = memory[0x09].toInt()
        memory[0x09] = ((fc + 1) and 0xFF).toUByte()
        println("STEP 26: Frame counter done")

        // Main game logic
        println("STEP 27: Calling operModeExecutionTree")
        operModeExecutionTree()
        println("STEP 28: operModeExecutionTree done")

        println("FRAME SUCCESS: One frame completed!")
    }

    // by Claude - Test multiple frames to find exactly where the hang is
    @Test
    fun `test multi-frame execution`() {
        val romFile = listOf("../local/roms/smb.nes", "local/roms/smb.nes", "../smb.nes", "smb.nes")
            .map { File(it) }
            .firstOrNull { it.exists() }

        if (romFile == null) {
            println("Skipping: No ROM file found")
            return
        }

        // Initialize interpreter
        val romData = romFile.readBytes().toUByteArray()
        val prgRom = romData.sliceArray(16 until 16 + 0x8000)
        val interp = BinaryInterpreter6502()
        for (i in prgRom.indices) {
            interp.memory.writeByte(0x8000 + i, prgRom[i])
        }

        var ppuStatus: UByte = 0x80u
        var ppuStatusReads = 0

        interp.memoryReadHook = { addr: Int ->
            when (addr) {
                0x2002 -> {
                    ppuStatusReads++
                    val status = ppuStatus
                    ppuStatus = if (ppuStatusReads > 3) 0x40u else 0x00u
                    status
                }
                in 0x2000..0x2007 -> 0u
                else -> null
            }
        }

        interp.memoryWriteHook = { addr: Int, _: UByte ->
            when (addr) {
                in 0x2000..0x2007 -> true
                0x4014 -> true
                in 0x4000..0x4017 -> true
                else -> false
            }
        }

        interp.cpu.PC = 0x8000u
        var cycles = 0
        while (cycles < 50000) {
            if (interp.cpu.PC.toInt() == 0x8075) break
            interp.step()
            cycles++
        }

        ppuStatus = 0x80u
        ppuStatusReads = 0
        interp.cpu.PC = 0x8082u
        cycles = 0
        while (cycles < 50000) {
            if (interp.memory.readByte(interp.cpu.PC.toInt()) == 0x40.toUByte()) break
            interp.step()
            cycles++
        }

        clearMemory()
        for (addr in 0x0000..0x07FF) {
            memory[addr] = interp.memory.readByte(addr)
        }
        initializeRomData()
        memory[0x2000] = 0x90u

        System.err.println("=== Multi-frame test (with GameEng trace) ===")
        System.err.flush()

        // by Claude - Increased to 200 frames after fixing Bug #20 (SideCheckLoop hang)
        val maxFrames = 200
        for (frame in 0 until maxFrames) {
            val mode = memory[0x0770].toInt()
            val task = memory[0x0772].toInt()
            val demoTimer = memory[0x07A2].toInt()  // DemoTimer
            val gameEng = memory[0x0E].toInt()  // GameEngineSubroutine
            val loopCmd = memory[0x0745].toInt()  // LoopCommand
            val areaTask = memory[0x0774].toInt()  // AreaParserTaskNum

            val altEntrance = memory[0x0752].toInt()  // AltEntranceControl
            val playerY = memory[0x00CE].toInt()  // Player_Y_Position
            val playerYHi = memory[0x00B5].toInt()  // Player_Y_HighPos
            val playerState = memory[0x001D].toInt()  // Player_State
            System.err.println("Frame $frame: Mode=$mode Task=$task DemoTimer=$demoTimer GameEng=$gameEng LoopCmd=$loopCmd AreaTask=$areaTask AltEnt=$altEntrance PY=$playerY PYH=$playerYHi State=$playerState")
            System.err.flush()

            // Simple NMI logic
            soundEngine()
            readJoypads()
            pauseRoutine()
            updateTopScore()
            moveSpritesOffscreen()
            spriteShuffler()

            // Timer handling
            val timerControl = memory[0x0747].toInt()
            if (timerControl == 0) {
                var intervalCtrl = memory[0x077F].toInt()
                intervalCtrl = (intervalCtrl - 1) and 0xFF
                memory[0x077F] = intervalCtrl.toUByte()
            }

            // Frame counter
            val fc = memory[0x09].toInt()
            memory[0x09] = ((fc + 1) and 0xFF).toUByte()

            // Debug operModeExecutionTree by checking sub-calls
            System.err.println("  -> About to call operModeExecutionTree (frame $frame)")
            System.err.flush()

            val startMs = System.currentTimeMillis()
            try {
                operModeExecutionTree()
            } catch (e: Exception) {
                System.err.println("  !! Exception in frame $frame: ${e.message}")
                e.printStackTrace(System.err)
                throw e
            }
            val elapsed = System.currentTimeMillis() - startMs

            System.err.println("  -> operModeExecutionTree done in ${elapsed}ms")
            System.err.flush()

            if (elapsed > 1000) {
                System.err.println("  !! Frame $frame took ${elapsed}ms - possible issue")
                System.err.flush()
                break
            }
        }

        System.err.println("SUCCESS: Ran through frames")
        System.err.flush()
    }

    @Test
    fun `test FM2 parsing`() {
        println("STEP 1: Finding TAS file")
        val tasFile = listOf(
            "happylee-warps.fm2",
            "../smb/happylee-warps.fm2",
            "smb/happylee-warps.fm2"
        ).map { File(it) }.firstOrNull { it.exists() }

        if (tasFile == null) {
            println("Skipping: No TAS file found")
            return
        }
        println("STEP 2: Found TAS at ${tasFile.absolutePath}")

        println("STEP 3: Parsing TAS")
        val inputs = FM2Parser.parse(tasFile)
        println("STEP 4: Parsed ${inputs.size} frames")

        println("FM2 PARSE SUCCESS!")
    }
}
