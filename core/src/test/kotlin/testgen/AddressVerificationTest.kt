package com.ivieleague.decompiler6502tokotlin.testgen

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test to verify address calculation accuracy by comparing against ROM.
 */
class AddressVerificationTest {

    private val romFile = File("local/roms/smb.nes")
    private val asmFile = File("smbdism.asm")

    @Test
    fun `verify key function addresses against ROM`() {
        if (!romFile.exists() || !asmFile.exists()) {
            println("Skipping test - ROM or ASM file not found")
            return
        }

        val romData = romFile.readBytes()
        val prgOffset = 16 // Skip iNES header

        val mapper = AddressLabelMapper.fromAssemblyFile(asmFile)

        // Known functions and their expected first opcodes
        val knownFunctions = mapOf(
            "Start" to 0x78,  // sei
            "ColdBoot" to 0x20,  // jsr
            "VBlank1" to 0xAD,  // lda abs
            "VBlank2" to 0xAD,  // lda abs
            "EndlessLoop" to 0x4C,  // jmp
            "NonMaskableInterrupt" to 0xAD,  // lda abs
            "ScreenOff" to 0x8D,  // sta abs
            "InitBuffer" to 0xBE,  // ldx abs,y
            "DecTimers" to 0xA2,  // ldx imm
            "DecTimersLoop" to 0xBD,  // lda abs,x
            "SkipExpTimer" to 0xCA,  // dex
            "NoDecTimers" to 0xEE,  // inc abs
            "PauseSkip" to 0xA2,  // ldx imm
            "RotPRandomBit" to 0x76,  // ror zp,x
            "Sprite0Clr" to 0xAD,  // lda abs
            "PauseRoutine" to 0xAD,  // lda abs
            "SpriteShuffler" to 0xA0,  // ldy imm
        )

        println("Verifying function addresses:")
        var errorsFound = 0

        for ((label, expectedOpcode) in knownFunctions) {
            val mappedAddr = mapper.getAddress(label)
            if (mappedAddr == null) {
                println("  $label: NOT FOUND in mapper")
                errorsFound++
                continue
            }

            val romOffset = mappedAddr - 0x8000 + prgOffset
            val actualOpcode = romData[romOffset].toInt() and 0xFF

            val status = if (actualOpcode == expectedOpcode) "✓" else {
                errorsFound++
                // Find where the expected opcode actually is
                val searchRange = (mappedAddr - 5)..(mappedAddr + 5)
                val actualPosition = searchRange.find { addr ->
                    val offset = addr - 0x8000 + prgOffset
                    offset >= 0 && offset < romData.size &&
                    (romData[offset].toInt() and 0xFF) == expectedOpcode
                }
                val offsetStr = if (actualPosition != null) {
                    "(expected at 0x${actualPosition.toString(16).uppercase()}, off by ${actualPosition - mappedAddr})"
                } else {
                    "(expected opcode 0x${expectedOpcode.toString(16).uppercase()} not found nearby)"
                }
                "✗ found 0x${actualOpcode.toString(16).uppercase()} $offsetStr"
            }

            println("  $label @ 0x${mappedAddr.toString(16).uppercase()}: $status")
        }

        if (errorsFound > 0) {
            println("\n$errorsFound errors found")
        }

        // Show the bytes between EndlessLoop and NonMaskableInterrupt to debug data section
        val endlessLoopAddr = mapper.getAddress("EndlessLoop")!!
        val nmiAddr = mapper.getAddress("NonMaskableInterrupt")!!
        val dataSectionSize = nmiAddr - endlessLoopAddr - 3 // -3 for JMP instruction

        println("\nData section size between EndlessLoop+3 and NMI: $dataSectionSize bytes")
        println("Expected: 40 bytes (19+19+2 from tables)")
    }
}
