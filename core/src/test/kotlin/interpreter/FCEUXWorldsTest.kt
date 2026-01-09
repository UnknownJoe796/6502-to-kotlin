package com.ivieleague.decompiler6502tokotlin.interpreter

import java.io.File
import kotlin.test.Test

class FCEUXWorldsTest {

    @Test
    fun `check FCEUX world progression`() {
        val fceuxRamFile = File("local/tas/fceux-full-ram.bin")
        if (!fceuxRamFile.exists()) {
            println("No FCEUX RAM dump found")
            return
        }

        val ram = fceuxRamFile.readBytes()
        val totalFrames = ram.size / 2048
        println("Total frames in FCEUX dump: $totalFrames")

        val WorldNumber = 0x075F
        val LevelNumber = 0x0760
        val OperMode = 0x0770

        var lastWorld = -1
        var lastLevel = -1

        println("\nWorld/Level transitions in FCEUX dump:")
        for (frame in 0 until totalFrames) {
            val offset = frame * 2048
            val world = (ram[offset + WorldNumber].toInt() and 0xFF) + 1
            val level = (ram[offset + LevelNumber].toInt() and 0xFF) + 1
            val mode = ram[offset + OperMode].toInt() and 0xFF

            if (world != lastWorld || level != lastLevel) {
                println("Frame $frame: W$world-$level (Mode=$mode)")
                lastWorld = world
                lastLevel = level
            }

            // Check for game over (Mode=3)
            if (mode == 3 && lastWorld > 0 && frame > 1000) {
                println("Frame $frame: GAME OVER")
                break
            }

            // Check for victory (Mode=2)
            if (mode == 2) {
                println("Frame $frame: VICTORY!")
                break
            }
        }
    }
}
