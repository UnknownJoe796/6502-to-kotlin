package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import java.nio.file.Files
import java.nio.file.Paths

class FunctionBoundaryDetectionTest {
    @Test
    fun tailCallIsFunctionExit() {
        val snippet = """
            Start:
                JMP Callee
            Callee:
                RTS
        """.trimIndent()
        val lines = snippet.parseAssemblyLines()
        val res = lines.resolveAddresses(baseAddress = 0x8000)
        val entries = lines.discoverEntryPoints(resolution = res, exportedLabels = setOf("Start", "Callee"))
        val reach = lines.analyzeReachability(resolution = res, entries = entries)
        val blocks = lines.constructBasicBlocks(resolution = res, reachability = reach, entries = entries)
        val cfg = lines.constructCfg(resolution = res, reachability = reach, blocks = blocks, entries = entries)
        val det = lines.detectFunctionBoundaries(resolution = res, entries = entries, reachability = reach, blocks = blocks, cfg = cfg)

        // Locate functions by label
        fun fnByLabel(name: String): DetectedFunction {
            val addr = res.labelToAddress[name] ?: error("no address for $name")
            val leader = blocks.blocks.first { it.startAddress == addr }.leaderIndex
            return det.functions.first { it.entryLeader == leader }
        }

        val startFn = fnByLabel("Start")
        val calleeFn = fnByLabel("Callee")

        // Start function should have exactly one block (the JMP) and a tail-call exit to Callee
        assertEquals(1, startFn.blocks.size, "Start should contain only its entry block due to tail call")
        val exit = startFn.exits.single { it.kind == FunctionExitKind.TAIL_CALL }
        assertTrue(exit.toFunctionLeader == calleeFn.entryLeader, "Tail-call should target Callee entry leader")

        // Callee function should contain its own block and a RETURN exit
        assertEquals(1, calleeFn.blocks.size, "Callee should contain one block")
        assertTrue(calleeFn.exits.any { it.kind == FunctionExitKind.RETURN }, "Callee should have a RETURN exit")

        // No overlaps expected
        assertTrue(det.overlappedLeaders.isEmpty(), "No block overlap expected in this simple case")
    }

    @Test
    fun overlapDetectionForSharedBlock() {
        val snippet = """
            Foo:
                JMP Shared
            Shared:
                RTS
            Bar:
                JMP Shared
        """.trimIndent()
        val lines = snippet.parseAssemblyLines()
        val res = lines.resolveAddresses(baseAddress = 0x8000)
        val entries = lines.discoverEntryPoints(resolution = res, exportedLabels = setOf("Foo", "Bar"))
        val reach = lines.analyzeReachability(resolution = res, entries = entries)
        val blocks = lines.constructBasicBlocks(resolution = res, reachability = reach, entries = entries)
        val cfg = lines.constructCfg(resolution = res, reachability = reach, blocks = blocks, entries = entries)
        val det = lines.detectFunctionBoundaries(resolution = res, entries = entries, reachability = reach, blocks = blocks, cfg = cfg)

        // Identify shared block leader (label Shared)
        val sharedAddr = res.labelToAddress["Shared"] ?: error("no address for Shared")
        val sharedLeader = blocks.blocks.first { it.startAddress == sharedAddr }.leaderIndex

        // Expect that Shared belongs to both functions (Foo via branch, Bar via JMP) because Shared is not an entry
        val containers = det.functions.map { fn -> fn.entryAddress to fn.blocks.any { it.leaderIndex == sharedLeader } }
        println("[DEBUG_LOG] Shared leader=$sharedLeader present in functions: " + containers.joinToString { (addr, has) -> "$addr:$has" })
        val count = containers.count { it.second }
        assertEquals(2, count, "Shared block should belong to both functions (overlap)")
        // And the global overlap set should reflect it
        assertTrue(sharedLeader in det.overlappedLeaders, "Shared block should be marked as overlapped between functions")
    }

    @Test
    fun detectionRunsOnSmbDisasm() {
        val path = Paths.get("smbdism.asm")
        assertTrue(Files.exists(path), "smbdism.asm should exist at project root for this test")
        val text = Files.readString(path)
        val lines = text.parseAssemblyLines()
        val res = lines.resolveAddresses(baseAddress = 0x8000)
        val entries = lines.discoverEntryPoints(resolution = res)
        val reach = lines.analyzeReachability(resolution = res, entries = entries)
        val blocks = lines.constructBasicBlocks(resolution = res, reachability = reach, entries = entries)
        val cfg = lines.constructCfg(resolution = res, reachability = reach, blocks = blocks, entries = entries)
        val det = lines.detectFunctionBoundaries(resolution = res, entries = entries, reachability = reach, blocks = blocks, cfg = cfg)

        assertTrue(det.functions.isNotEmpty(), "Expected some detected functions")
        // Some functions should have at least one RETURN exit
        val anyReturn = det.functions.any { fn -> fn.exits.any { it.kind == FunctionExitKind.RETURN } }
        assertTrue(anyReturn, "Expected at least one function with a RETURN exit")

        println(det.functions.joinToString(separator = "\n") { it.entryLabel ?: "?" })
    }
}
