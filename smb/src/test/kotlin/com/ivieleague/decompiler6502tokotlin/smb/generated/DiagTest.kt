@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.smb.generated

import com.ivieleague.decompiler6502tokotlin.hand.*
import com.ivieleague.decompiler6502tokotlin.smb.*
import org.junit.jupiter.api.Test
import java.io.File

class DiagTest {
    @Test
    fun diagnose() {
        // Load ROM into memory for functions that read from ROM tables
        val romFile = File("/Users/jivie/Projects/decompiler-6502-kotlin/local/roms/smb.nes")
        if (romFile.exists()) {
            val romData = romFile.readBytes().toUByteArray()
            // SMB ROM: 16-byte header + 32KB PRG ROM
            val prgStart = 16
            val prgSize = 0x8000
            for (i in 0 until prgSize) {
                memory[0x8000 + i] = romData[prgStart + i]
            }
            println("ROM loaded, BlockBufferAdderData[2] = ${memory[0xE3AD + 2]}")
        } else {
            println("WARNING: ROM file not found!")
        }

        resetCPU()
        clearMemory()
        // Setup from frame36_test0
        memory[0x0000] = 0x02u
        memory[0x0003] = 0x00u
        memory[0x0004] = 0x0Eu
        memory[0x0005] = 0x30u
        memory[0x0006] = 0x03u
        memory[0x0007] = 0x05u
        memory[0x000B] = 0x00u
        memory[0x000E] = 0x08u
        memory[0x001D] = 0x00u
        memory[0x006D] = 0x00u
        memory[0x0086] = 0x28u
        memory[0x009F] = 0x00u
        memory[0x00B5] = 0x01u
        memory[0x00CE] = 0xB0u
        memory[0x00EB] = 0x0Eu
        memory[0x01A9] = 0x00u
        memory[0x01ED] = 0x03u
        memory[0x01EE] = 0x03u
        memory[0x01EF] = 0x0Au
        memory[0x01F0] = 0xE4u
        memory[0x01F1] = 0x00u
        memory[0x01F2] = 0xC3u
        memory[0x01F3] = 0xDCu
        memory[0x05A2] = 0x00u
        memory[0x05A3] = 0x00u
        memory[0x05B2] = 0x54u
        memory[0x05B3] = 0x54u
        memory[0x0704] = 0x00u
        memory[0x070E] = 0x00u
        memory[0x0714] = 0x00u
        memory[0x0716] = 0x00u
        memory[0x0754] = 0x01u

        println("Before: memory[0x00EB] = ${memory[0x00EB]}")
        println("Player_Y_Position (0xCE) = ${memory[0x00CE]}")

        playerBGCollision()

        println("After: memory[0x00EB] = ${memory[0x00EB]} (expected 0x13 = 19)")
        println("After: memory[0x0000] = ${memory[0x0000]} (counter, should be 0)")
        println("After: memory[0x0001] = ${memory[0x0001]} (saved metatile)")
        // Check addresses that blockbuffercolliSide might use
        println("Debug: BlockBuffer address range values...")
    }
}
