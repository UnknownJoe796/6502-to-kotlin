@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.smb.generated

import com.ivieleague.decompiler6502tokotlin.testgen.AddressLabelMapper
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

// by Claude - Debug test to verify address calculation
class FloateyDebugTest {
    @Test
    fun `verify MaxLeftXSpdData address`() {
        val asmFile = File("/Users/jivie/Projects/decompiler-6502-kotlin/smbdism.asm")
        val romFile = File("/Users/jivie/Projects/decompiler-6502-kotlin/local/roms/smb.nes")

        if (!asmFile.exists() || !romFile.exists()) {
            println("Required files not found")
            return
        }

        // Get address from mapper
        val mapper = AddressLabelMapper.fromAssemblyFile(asmFile)
        val calculatedAddr = mapper.getAddress("MaxLeftXSpdData")

        // Expected data from assembly: $d8, $e8, $f0
        val romData = romFile.readBytes()
        val prgOffset = 16

        // Check what's actually in ROM at the calculated address
        val romOffset = (calculatedAddr ?: 0) - 0x8000 + prgOffset

        // Verify the calculated address has the expected data
        assertEquals(0xD8, romData[romOffset].toInt() and 0xFF, "MaxLeftXSpdData[0] should be 0xD8")
        assertEquals(0xE8, romData[romOffset + 1].toInt() and 0xFF, "MaxLeftXSpdData[1] should be 0xE8")
        assertEquals(0xF0, romData[romOffset + 2].toInt() and 0xFF, "MaxLeftXSpdData[2] should be 0xF0")

        println("MaxLeftXSpdData address verified: 0x${calculatedAddr?.toString(16)?.uppercase()}")
    }
}
