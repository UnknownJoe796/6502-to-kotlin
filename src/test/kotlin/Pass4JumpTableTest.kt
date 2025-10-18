package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import java.io.File

/**
 * Test Pass 4 jump table detection for JSR JumpEngine pattern
 */
class Pass4JumpTableTest {

    @Test
    fun testJumpEnginePatternDetection() {
        val assembly = """
            .export Start
            Start:
                LDA #$00
                JSR JumpEngine

                .dw Handler1
                .dw Handler2
                .dw Handler3

            Handler1:
                LDA #$01
                RTS

            Handler2:
                LDA #$02
                RTS

            Handler3:
                LDA #$03
                RTS

            JumpEngine:
                RTS
        """.trimIndent()

        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("Start"))

        // Should find jump table entries
        val jumpTableEntries = entries.entryPoints.filter { it.kind == EntryPointKind.JUMP_TABLE }

        println("Jump table entries found: ${jumpTableEntries.size}")
        jumpTableEntries.forEach { entry ->
            println("  ${entry.label}: ${entry.address?.let { "$" + it.toString(16).uppercase() }}")
        }

        assertEquals(3, jumpTableEntries.size, "Should find 3 jump table entries")
        assertTrue(jumpTableEntries.any { it.label == "Handler1" }, "Should find Handler1")
        assertTrue(jumpTableEntries.any { it.label == "Handler2" }, "Should find Handler2")
        assertTrue(jumpTableEntries.any { it.label == "Handler3" }, "Should find Handler3")
    }

    @Test
    fun testSMBJumpTableDetection() {
        val smbFile = File("smbdism.asm")
        if (!smbFile.exists()) {
            println("Warning: smbdism.asm not found, skipping test")
            return
        }

        val assembly = smbFile.readText()
        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution)

        // Count jump table entries
        val jumpTableEntries = entries.entryPoints.filter { it.kind == EntryPointKind.JUMP_TABLE }

        println("Total entry points: ${entries.entryPoints.size}")
        println("Jump table entries: ${jumpTableEntries.size}")

        // Verify GameMenuRoutine is now found
        val gameMenuEntry = jumpTableEntries.find { it.label == "GameMenuRoutine" }
        assertTrue(gameMenuEntry != null, "GameMenuRoutine should be detected as jump table entry")

        println("\nSample jump table entries:")
        jumpTableEntries.take(20).forEach { entry ->
            println("  ${entry.label}: ${entry.address?.let { "$" + it.toString(16).uppercase() }}")
        }

        // Should have found a significant number of jump table entries
        assertTrue(jumpTableEntries.size >= 10, "Should find at least 10 jump table entries in SMB")
    }

    @Test
    fun testGameMenuRoutineReachability() {
        val smbFile = File("smbdism.asm")
        if (!smbFile.exists()) {
            println("Warning: smbdism.asm not found, skipping test")
            return
        }

        val assembly = smbFile.readText()
        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution)
        val reachability = lines.analyzeReachability(resolution, entries)

        // GameMenuRoutine should now be reachable (as entry point)
        val gameMenuAddr = resolution.labelToAddress["GameMenuRoutine"]
        val chkSelectAddr = resolution.labelToAddress["ChkSelect"]

        println("GameMenuRoutine address: ${gameMenuAddr?.let { "$" + it.toString(16).uppercase() }}")
        println("GameMenuRoutine reachable: ${gameMenuAddr in reachability.reachableAddresses}")

        println("\nChkSelect address: ${chkSelectAddr?.let { "$" + it.toString(16).uppercase() }}")
        println("ChkSelect reachable: ${chkSelectAddr in reachability.reachableAddresses}")

        // GameMenuRoutine should be reachable as it's now an entry point
        assertTrue(gameMenuAddr in reachability.reachableAddresses,
            "GameMenuRoutine should be reachable via jump table entry point")

        // ChkSelect should be reachable via branch from GameMenuRoutine
        assertTrue(chkSelectAddr in reachability.reachableAddresses,
            "ChkSelect should be reachable via branch from GameMenuRoutine")
    }
}
