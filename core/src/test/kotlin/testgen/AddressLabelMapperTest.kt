package com.ivieleague.decompiler6502tokotlin.testgen

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AddressLabelMapperTest {

    @Test
    fun `test basic address mapping`() {
        val asm = """
            .org ${'$'}8000
            Start:      sei
                        cld
                        lda #${'$'}10
            Loop:       sta ${'$'}2000
                        jmp Loop
        """.trimIndent()

        val mapper = AddressLabelMapper.fromAssemblyText(asm)

        // Start should be at $8000
        assertEquals(0x8000, mapper.getAddress("Start"))

        // Loop should be at $8000 + 1(sei) + 1(cld) + 2(lda imm) = $8004
        assertEquals(0x8004, mapper.getAddress("Loop"))

        // Reverse lookup
        assertEquals("Start", mapper.getLabel(0x8000))
        assertEquals("Loop", mapper.getLabel(0x8004))
    }

    @Test
    fun `test function name conversion`() {
        assertEquals("pauseRoutine", AddressLabelMapper.labelToKotlinFunctionName("PauseRoutine"))
        assertEquals("initializeMemory", AddressLabelMapper.labelToKotlinFunctionName("InitializeMemory"))
        assertEquals("nMI", AddressLabelMapper.labelToKotlinFunctionName("NMI"))
    }

    @Test
    fun `test with smbdism if available`() {
        val asmFile = File("smbdism.asm")
        if (!asmFile.exists()) {
            println("Skipping SMB test - smbdism.asm not found")
            return
        }

        val mapper = AddressLabelMapper.fromAssemblyFile(asmFile)

        // SMB starts code at $8000
        val coldBootAddr = mapper.getAddress("ColdBoot")
        assertNotNull(coldBootAddr, "ColdBoot label should exist")
        assertTrue(coldBootAddr >= 0x8000, "ColdBoot should be in code section")

        println("Found ${mapper.getAllAddresses().size} labeled addresses")

        // Print some known functions for verification
        listOf("NMI", "ColdBoot", "PauseRoutine", "ReadJoypads", "JumpEngine").forEach { label ->
            val addr = mapper.getAddress(label)
            if (addr != null) {
                println("$label -> 0x${addr.toString(16).uppercase()}")
            }
        }
    }

    @Test
    fun `test top function addresses match captured`() {
        val asmFile = File("smbdism.asm")
        if (!asmFile.exists()) {
            println("Skipping SMB test - smbdism.asm not found")
            return
        }

        val mapper = AddressLabelMapper.fromAssemblyFile(asmFile)

        // Top captured function addresses from the capture test
        val topAddresses = listOf(
            0x9BE1, 0xC047, 0x84C3, 0xF26D, 0x8E04,
            0xEBB2, 0xE3EC, 0xF1F6, 0xBE70, 0xF1D7,
            0x8182  // PauseRoutine area
        )

        println("Checking top captured addresses:")
        for (addr in topAddresses) {
            val label = mapper.getLabel(addr)
            if (label != null) {
                println("  0x${addr.toString(16).uppercase()} -> $label")
            } else {
                // Find nearest label
                val nearby = mapper.getAllAddresses()
                    .filter { kotlin.math.abs(it - addr) <= 20 }
                    .sortedBy { kotlin.math.abs(it - addr) }
                    .take(3)
                    .map { "0x${it.toString(16).uppercase()}:${mapper.getLabel(it)}" }
                println("  0x${addr.toString(16).uppercase()} -> (no label, nearby: $nearby)")
            }
        }

        // Check 0x8182 vs 0x8183 (PauseRoutine)
        println("\nSpecial check for PauseRoutine area:")
        for (addr in 0x8180..0x8190) {
            val label = mapper.getLabel(addr)
            if (label != null) {
                println("  0x${addr.toString(16).uppercase()} = $label")
            }
        }
    }
}
