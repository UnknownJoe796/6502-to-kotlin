package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import kotlin.test.assertTrue
import java.nio.file.Files
import java.nio.file.Paths

class EntryPointDiscoveryTest {
    @Test
    fun discoverEntryPointsFromSnippet() {
        val snippet = """
            Start:
                JSR Foo
                JSR ${'$'}C000
                RTS
            Foo:
                RTS
        """.trimIndent()
        val lines = snippet.parseToAssemblyCodeFile()
        val res = lines.resolveAddresses(baseAddress = 0x8000)
        val discovered = lines.discoverEntryPoints(resolution = res, exportedLabels = setOf("Start"))

        // Expect at least one JSR target for Foo
        val jsrTargets = discovered.entryPoints.filter { it.kind == EntryPointKind.JSR_TARGET }
        assertTrue(jsrTargets.any { it.label == "Foo" }, "Expected Foo to be discovered as JSR target")

        // Numeric JSR target should be captured by address even without a label
        assertTrue(jsrTargets.any { it.address == 0xC000 }, "Expected ${'$'}C000 to be recorded as a JSR target address")

        // Exported label should be included
        assertTrue(discovered.entryPoints.any { it.kind == EntryPointKind.EXPORTED && it.label == "Start" },
            "Expected exported label 'Start' to be included")
    }

    @Test
    fun discoverEntryPointsOnSmbDisasm() {
        val path = Paths.get("smbdism.asm")
        assertTrue(Files.exists(path), "smbdism.asm should exist at project root for this test")
        val text = Files.readString(path)
        val lines = text.parseToAssemblyCodeFile()
        val res = lines.resolveAddresses(baseAddress = 0x8000)
        val discovered = lines.discoverEntryPoints(resolution = res)

        // There should be at least some JSR targets in SMB disassembly
        assertTrue(discovered.entryPoints.any { it.kind == EntryPointKind.JSR_TARGET },
            "Expected at least one JSR target to be discovered in SMB disassembly")

        // If known interrupt labels exist in the symbol table, they must be present as entry points
        val presentInterrupts = listOf("NMI", "RESET", "IRQ").filter { it in res.labelToAddress }
        presentInterrupts.forEach { name ->
            assertTrue(discovered.entryPoints.any { it.label == name },
                "Expected interrupt label '$name' to be included when present")
        }
    }
}
