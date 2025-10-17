package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import java.nio.file.Files
import java.nio.file.Paths

class ValidationTest {
    @Test
    fun validateAddressingOnSmbDisasmHasNoIssues() {
        val path = Paths.get("smbdism.asm")
        assertTrue(Files.exists(path), "smbdism.asm should exist at project root for this test")
        val text = Files.readString(path)
        val lines = text.parseAssemblyLines()
        val report = lines.validateDisassembly()
        // We expect the real SMB disassembly to use only legal 6502 addressing modes
        assertTrue(report.issues.isEmpty(), "Expected no addressing mode issues, found: ${report.issues.take(5)}")
    }

    @Test
    fun illegalAddressingIsFlagged() {
        val snippet = """
            STA #$10
            ASL A
            JMP ($1234)
            LDX $10,X
        """.trimIndent()
        val lines = snippet.parseAssemblyLines()
        val report = lines.validateDisassembly()
        // Expect 2 issues: STA immediate, LDX with ,X
        assertEquals(2, report.issues.size, "Expected two invalid addressing issues")
        assertTrue(report.issues.any { it.message.contains("STA") && it.message.contains("Illegal") })
        assertTrue(report.issues.any { it.message.contains("LDX") && it.message.contains("Illegal") })
    }
}
