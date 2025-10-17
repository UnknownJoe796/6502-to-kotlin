package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import java.nio.file.Files
import java.nio.file.Paths

class AddressResolutionTest {
    @Test
    fun resolveAddressesOnSmbDisasm() {
        val path = Paths.get("smbdism.asm")
        assertTrue(Files.exists(path), "smbdism.asm should exist at project root for this test")
        val text = Files.readString(path)
        val lines = text.parseAssemblyLines()
        val res = lines.resolveAddresses(baseAddress = 0x8000)

        // basic shape
        assertEquals(lines.size, res.resolved.size, "Each line should produce a resolved entry")

        // there should be some addresses assigned
        val addressed = res.resolved.filter { it.address != null && it.sizeBytes > 0 }
        assertTrue(addressed.isNotEmpty(), "There should be addressed entries (code or data)")

        // addresses should be monotonically non-decreasing by line order when defined
        var lastAddr = -1
        addressed.forEach { entry ->
            val addr = entry.address!!
            assertTrue(addr >= lastAddr, "Addresses should be non-decreasing along the file")
            lastAddr = addr
        }

        // symbol mapping should not be empty
        assertTrue(res.labelToAddress.isNotEmpty(), "There should be labels mapped to addresses")

        // there should be at least some data lines detected from .db in the SMB disassembly
        val dataCount = res.resolved.count { it.isData && it.sizeBytes > 0 }
        assertTrue(dataCount > 0, ".db data lines should be detected")
    }
}
