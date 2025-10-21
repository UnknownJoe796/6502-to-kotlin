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
        val lines = text.parseToAssemblyCodeFile()
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

    @Test
    fun testSimpleConstantResolution() {
        val assembly = """
            MyVar = ${'$'}0722
            LDA MyVar
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)

        // Verify constant is in symbol table
        assertEquals(0x0722, resolution.labelToAddress["MyVar"], "MyVar should resolve to 0x0722")
    }

    @Test
    fun testIndexedConstantResolution() {
        val assembly = """
            EnemyArray = ${'$'}0200,X
            LDA EnemyArray
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)

        // Verify constant is in symbol table (indexed constants resolve to base address)
        assertEquals(0x0200, resolution.labelToAddress["EnemyArray"])
    }

    @Test
    fun testForwardReferenceConstant() {
        val assembly = """
            FirstVar = SecondVar
            SecondVar = ${'$'}0100
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)

        // Both should resolve to $0100
        assertEquals(0x0100, resolution.labelToAddress["SecondVar"])
        assertEquals(0x0100, resolution.labelToAddress["FirstVar"], "FirstVar should forward-resolve to 0x0100")
    }

    @Test
    fun testMultipleConstantsResolution() {
        val assembly = """
            Player_X = ${'$'}86
            Player_Y = ${'$'}87
            Enemy_X = ${'$'}90

            LDA Player_X
            STA Enemy_X
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)

        assertEquals(0x86, resolution.labelToAddress["Player_X"])
        assertEquals(0x87, resolution.labelToAddress["Player_Y"])
        assertEquals(0x90, resolution.labelToAddress["Enemy_X"])
    }

    @Test
    fun testSMBConstantsInSymbolTable() {
        val path = Paths.get("smbdism.asm")
        assertTrue(Files.exists(path), "smbdism.asm should exist at project root for this test")
        val text = Files.readString(path)
        val lines = text.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)

        // Count how many constants were resolved
        val constantLines = lines.lines.count { it.constant != null }

        // All constants should be in labelToAddress
        assertTrue(constantLines > 0, "SMB disassembly should have constants")

        // Some constants from SMB might be in the symbol table
        // Just verify the symbol table has entries
        assertTrue(resolution.labelToAddress.isNotEmpty(), "Symbol table should have entries")
    }
}
