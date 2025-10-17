package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import java.nio.file.Files
import java.nio.file.Paths

class ReachabilityTest {
    @Test
    fun reachabilityOnSnippetFindsDeadCode() {
        val snippet = """
            Start:
                JSR Sub
                BNE Next
                JMP End
            Next:
                NOP
            End:
                RTS
            Sub:
                RTS
            Unreached:
                NOP
        """.trimIndent()
        val lines = snippet.parseAssemblyLines()
        val res = lines.resolveAddresses(baseAddress = 0x8000)
        val eps = lines.discoverEntryPoints(resolution = res, exportedLabels = setOf("Start"))
        val reach = lines.analyzeReachability(resolution = res, entries = eps)

        // Check that the addresses of key labels are reachable
        fun addrOf(name: String) = res.labelToAddress[name] ?: error("No address for label $name")
        assertTrue(addrOf("Start") in reach.reachableAddresses, "Start should be reachable")
        assertTrue(addrOf("Sub") in reach.reachableAddresses, "Sub should be reachable via JSR")
        assertTrue(addrOf("Next") in reach.reachableAddresses, "Next should be reachable via branch/fall-through")
        assertTrue(addrOf("End") in reach.reachableAddresses, "End should be reachable via JMP target")

        // Unreached block should be marked dead (the instruction at its label address)
        val unreachedAddr = addrOf("Unreached")
        val unreachedIdx = res.resolved.indexOfFirst { it.address == unreachedAddr }
        assertTrue(unreachedIdx >= 0 && unreachedIdx in reach.deadCodeLineIndexes, "Unreached should be identified as dead code")

        // Sanity: no data lines are marked as reachable code
        val anyDataReachable = reach.reachableLineIndexes.any { res.resolved[it].isData }
        assertEquals(false, anyDataReachable, "No data lines should be marked as reachable code")
    }

    @Test
    fun reachabilityOnSmbDisasmRuns() {
        val path = Paths.get("smbdism.asm")
        assertTrue(Files.exists(path), "smbdism.asm should exist at project root for this test")
        val text = Files.readString(path)
        val lines = text.parseAssemblyLines()
        val res = lines.resolveAddresses(baseAddress = 0x8000)
        val eps = lines.discoverEntryPoints(resolution = res)
        val reach = lines.analyzeReachability(resolution = res, entries = eps)

        // Basic properties
        assertTrue(reach.reachableLineIndexes.isNotEmpty(), "There should be some reachable code lines")
        // Reachable must be subset of instruction lines
        assertTrue(reach.reachableLineIndexes.all { lines[it].instruction != null }, "Reachable set should contain only instruction lines")
        // Ensure no data lines included
        val anyDataReachable = reach.reachableLineIndexes.any { res.resolved[it].isData }
        assertEquals(false, anyDataReachable, "No data lines should be marked as reachable code")
    }
}
