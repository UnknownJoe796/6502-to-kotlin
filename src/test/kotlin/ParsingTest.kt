package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import java.nio.file.Files
import java.nio.file.Paths

class ParsingTest {
    @Test
    fun parseSmbDisasmAndBuildSymbols() {
        val path = Paths.get("smbdism.asm")
        assertTrue(Files.exists(path), "smbdism.asm should exist at project root for this test")
        val text = Files.readString(path)
        val lines = text.parseAssemblyLines()
        // basic sanity checks
        assertTrue(lines.lines.isNotEmpty(), "Parsed lines should not be empty")
        assertEquals(text.lines().size, lines.size, "Line count should match input")

        // build symbol table
        val symbols = lines.buildSymbolTable()
        // we expect at least some labels in the SMB disassembly
        assertTrue(symbols.labelToLineIndex.isNotEmpty(), "Symbol table should not be empty")

        // Ensure no crash on duplicates: duplicates are allowed and recorded
        // If there are duplicates, they should appear in the duplicates map
        // Not asserting presence since it depends on the source, but ensure consistency
        symbols.duplicates.forEach { (label, idxs) ->
            assertTrue(label in symbols.labelToLineIndex.keys || idxs.isNotEmpty())
        }
    }

    @Test
    fun parseDbDirectiveProducesData() {
        val path = Paths.get("smbdism.asm")
        assertTrue(Files.exists(path), "smbdism.asm should exist at project root for this test")
        val text = Files.readString(path)
        val lines = text.parseAssemblyLines()

        // Find a known .db line with numeric bytes
        val target = lines.lines.firstOrNull { it.originalLine?.contains(".db \$10, \$51, \$88, \$c0", ignoreCase = true) == true }
        assertTrue(target != null, "Expected to find known .db line in disassembly")

        val data = target!!.data
        assertTrue(data is AssemblyData.Db, ".db line should be parsed into AssemblyData.Db")
        val items = (data as AssemblyData.Db).items
        assertEquals(4, items.size, "Expected 4 items in parsed .db list")

        // Verify the four byte values
        val bytes = items.mapNotNull { (it as? AssemblyData.DbItem.ByteValue)?.value }
        assertEquals(listOf(0x10, 0x51, 0x88, 0xC0), bytes, "Parsed byte values should match source")
    }
}
