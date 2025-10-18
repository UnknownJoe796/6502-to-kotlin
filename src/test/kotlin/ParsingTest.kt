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
        val lines = text.parseToAssemblyCodeFile()
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
        val lines = text.parseToAssemblyCodeFile()

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

    @Test
    fun testSimpleHexConstant() {
        val assembly = """
            MyVar = ${'$'}0722
            LDA MyVar
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()

        // First line should be a constant
        val constantLine = lines.lines[0]
        assertTrue(constantLine.constant != null, "First line should be parsed as constant")
        assertEquals("MyVar", constantLine.constant?.name)
        assertTrue(constantLine.constant?.value is AssemblyAddressing.Label)
        assertEquals("$0722", (constantLine.constant?.value as AssemblyAddressing.Label).label)

        // Second line should be an instruction
        val instrLine = lines.lines[1]
        assertTrue(instrLine.instruction != null, "Second line should be an instruction")
        assertEquals(AssemblyOp.LDA, instrLine.instruction?.op)
    }

    @Test
    fun testIndexedConstant() {
        val assembly = """
            EnemyArray = ${'$'}0200,X
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()

        val constantLine = lines.lines[0]
        assertTrue(constantLine.constant != null, "Line should be parsed as constant")
        assertEquals("EnemyArray", constantLine.constant?.name)
        assertTrue(constantLine.constant?.value is AssemblyAddressing.DirectX)
        assertEquals("$0200", (constantLine.constant?.value as AssemblyAddressing.DirectX).label)
    }

    @Test
    fun testConstantWithComment() {
        val assembly = """
            Player_X_Position = ${'$'}86  ; X position of the player
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()

        val constantLine = lines.lines[0]
        assertTrue(constantLine.constant != null, "Line should be parsed as constant")
        assertEquals("Player_X_Position", constantLine.constant?.name)
        assertEquals("X position of the player", constantLine.comment)
    }

    @Test
    fun testSMBHasConstants() {
        val path = Paths.get("smbdism.asm")
        assertTrue(Files.exists(path), "smbdism.asm should exist at project root for this test")
        val text = Files.readString(path)
        val lines = text.parseToAssemblyCodeFile()

        // Count constants
        val constants = lines.lines.filter { it.constant != null }

        // Super Mario Bros should have many constants
        assertTrue(constants.isNotEmpty(), "SMB disassembly should have constants defined with =")

        // Check for specific known constants (if they exist)
        val constantNames = constants.mapNotNull { it.constant?.name }
        // Just verify we're parsing some constants - exact names may vary
        assertTrue(constantNames.size > 10, "Expected at least 10 constants in SMB disassembly")
    }
}
