@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.testgen

import java.io.File
import kotlin.test.Test

class AddressLabelMapperDebugTest {

    @Test
    fun verifyFloateyNumbersRoutineAddress() {
        val asmFile = File("smbdism.asm")
        val mapper = AddressLabelMapper.fromAssemblyFile(asmFile)

        val addr = mapper.getAddress("FloateyNumbersRoutine")
        println("FloateyNumbersRoutine: 0x${addr?.toString(16)?.uppercase()}")

        // Check what's at 0x84C3
        val label = mapper.getLabel(0x84C3)
        println("Label at 0x84C3: $label")

        // Check nearby labels
        println("\nNearby labels:")
        for (delta in -20..20) {
            val nearAddr = 0x84C3 + delta
            val nearLabel = mapper.getLabel(nearAddr)
            if (nearLabel != null) {
                println("0x${nearAddr.toString(16).uppercase()}: $nearLabel")
            }
        }
    }

    @Test
    fun verifyDividePDiffAddress() {
        val asmFile = File("smbdism.asm")
        val mapper = AddressLabelMapper.fromAssemblyFile(asmFile)

        println("=== DividePDiff Address Verification ===")

        // Check what AddressLabelMapper says
        val mappedAddr = mapper.getAddress("DividePDiff")
        println("AddressLabelMapper says DividePDiff is at: 0x${mappedAddr?.toString(16)?.uppercase() ?: "NOT FOUND"}")

        // Check what's at 0xF12A (what generated tests say)
        val labelAtF12A = mapper.getLabel(0xF12A)
        println("Label at 0xF12A (from generated tests): $labelAtF12A")

        // Check what's at 0xFDBA (what SMBConstants says)
        val labelAtFDBA = mapper.getLabel(0xFDBA)
        println("Label at 0xFDBA (from SMBConstants): $labelAtFDBA")

        println("\n--- Labels near 0xF12A ---")
        for (delta in -20..20) {
            val nearAddr = 0xF12A + delta
            val nearLabel = mapper.getLabel(nearAddr)
            if (nearLabel != null) {
                println("0x${nearAddr.toString(16).uppercase()}: $nearLabel")
            }
        }

        println("\n--- Labels near 0xFDBA ---")
        for (delta in -20..20) {
            val nearAddr = 0xFDBA + delta
            val nearLabel = mapper.getLabel(nearAddr)
            if (nearLabel != null) {
                println("0x${nearAddr.toString(16).uppercase()}: $nearLabel")
            }
        }

        // Also verify against ROM if available
        val romFile = File("local/roms/smb.nes")
        if (romFile.exists()) {
            val romData = romFile.readBytes()
            val prgOffset = 16 // iNES header

            println("\n--- ROM Verification ---")
            println("Expected DividePDiff starts with: sta \$05 = 0x85 0x05")

            // Check AddressLabelMapper address (0xF12A)
            val mapper0xF12A = 0xF12A - 0x8000 + prgOffset
            println("\nAt 0xF12A (AddressLabelMapper): ${
                (0..5).map { romData[mapper0xF12A + it].toInt() and 0xFF }
                    .joinToString(" ") { "0x${it.toString(16).uppercase().padStart(2, '0')}" }
            }")

            // Check SMBConstants address (0xFDBA)
            val smbConst0xFDBA = 0xFDBA - 0x8000 + prgOffset
            println("At 0xFDBA (SMBConstants):        ${
                (0..5).map { romData[smbConst0xFDBA + it].toInt() and 0xFF }
                    .joinToString(" ") { "0x${it.toString(16).uppercase().padStart(2, '0')}" }
            }")

            // Search for the expected bytes "85 05 A5 07 C5 06" (sta $05, lda $07, cmp $06)
            val searchBytes = byteArrayOf(0x85.toByte(), 0x05, 0xA5.toByte(), 0x07, 0xC5.toByte(), 0x06)
            println("\nSearching for DividePDiff signature (85 05 A5 07 C5 06)...")
            for (offset in 0 until romData.size - 6) {
                if ((0..5).all { romData[offset + it] == searchBytes[it] }) {
                    val addr = offset - prgOffset + 0x8000
                    println("Found at ROM offset $offset = address 0x${addr.toString(16).uppercase()}")
                }
            }

            // Check what label is at the real address (0xF26D)
            val realAddr = 0xF26D
            val labelAtReal = mapper.getLabel(realAddr)
            println("\nLabel at real DividePDiff address 0x${realAddr.toString(16).uppercase()}: $labelAtReal")

            // Check labels near 0xF26D
            println("\n--- Labels near 0xF26D (real address) ---")
            for (delta in -20..20) {
                val nearAddr = realAddr + delta
                val nearLabel = mapper.getLabel(nearAddr)
                if (nearLabel != null) {
                    println("0x${nearAddr.toString(16).uppercase()}: $nearLabel")
                }
            }

            // Calculate discrepancy
            println("\n--- Address Discrepancy Analysis ---")
            val currentMapped = mapper.getAddress("DividePDiff") ?: 0xF12A
            println("AddressLabelMapper offset: 0xF26D - 0x${currentMapped.toString(16).uppercase()} = ${0xF26D - currentMapped} bytes")

            // Test ROM-based verification
            println("\n--- ROM Verification Function Test ---")
            val correctedAddr = AddressLabelMapper.verifyAddressWithRom("DividePDiff", currentMapped, romData)
            println("Original mapper address: 0x${currentMapped.toString(16).uppercase()}")
            println("ROM-verified address:    0x${correctedAddr.toString(16).uppercase()}")
            println("Expected address:        0xF26D")
            println("Correction worked: ${correctedAddr == 0xF26D}")

            // Check multiple functions to see where error accumulates
            // Verified opcodes from assembly:
            println("\n--- Multi-point Verification ---")
            val checkPoints = listOf(
                "Start" to Pair(0x78, "SEI"),                     // sei
                "NonMaskableInterrupt" to Pair(0xAD, "LDA abs"),  // lda $4015
                "PauseRoutine" to Pair(0xAD, "LDA abs"),          // lda TimerControl
                "SpriteShuffler" to Pair(0xAC, "LDY abs"),        // ldy AreaType ($074e)
                "MoveAllSpritesOffscreen" to Pair(0xA0, "LDY imm"), // ldy #$00
                "InitializeGame" to Pair(0xA0, "LDY imm"),        // ldy #$6f
                "GameCoreRoutine" to Pair(0xAE, "LDX abs"),       // ldx CurrentPlayer ($0753)
                "GetOffScreenBitsSet" to Pair(0x98, "TYA"),       // tya
                "DividePDiff" to Pair(0x85, "STA zp"),            // sta $05
            )

            for ((label, expected) in checkPoints) {
                val addr = mapper.getAddress(label)
                if (addr == null) {
                    println("  $label: NOT FOUND")
                    continue
                }
                val romOffset = addr - 0x8000 + prgOffset
                if (romOffset < 0 || romOffset >= romData.size) {
                    println("  $label @ 0x${addr.toString(16).uppercase()}: OUT OF BOUNDS")
                    continue
                }
                val actualByte = romData[romOffset].toInt() and 0xFF
                val status = if (actualByte == expected.first) "✓" else {
                    // Find where expected byte actually is
                    val searchStart = maxOf(0, romOffset - 100)
                    val searchEnd = minOf(romData.size, romOffset + 100)
                    val foundOffset = (searchStart until searchEnd).find {
                        (romData[it].toInt() and 0xFF) == expected.first
                    }
                    val realAddr = if (foundOffset != null) foundOffset - prgOffset + 0x8000 else null
                    val delta = if (realAddr != null) realAddr - addr else null
                    "✗ found 0x${actualByte.toString(16).uppercase()}, expected ${expected.second} (0x${expected.first.toString(16).uppercase()})" +
                        (if (delta != null) " at ${if (delta > 0) "+" else ""}$delta bytes" else "")
                }
                println("  $label @ 0x${addr.toString(16).uppercase()}: $status")
            }
        }
    }
}
