package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import java.nio.file.Files
import java.nio.file.Paths

class BasicBlockConstructionTest {
    @Test
    fun basicBlocksOnSnippet() {
        val snippet = """
            Start:
                LDA #${'$'}01
                JSR Sub
            Next:
                BNE Next
                LDA #${'$'}00
                JMP End
            Unreached:
                LDA #${'$'}FF
            End:
                RTS
            Sub:
                RTS
        """.trimIndent()
        val lines = snippet.parseAssemblyLines()
        val res = lines.resolveAddresses(baseAddress = 0x8000)
        val entries = lines.discoverEntryPoints(resolution = res, exportedLabels = setOf("Start"))
        val reach = lines.analyzeReachability(resolution = res, entries = entries)
        val bb = lines.constructBasicBlocks(resolution = res, reachability = reach)

        // Expect blocks starting at addresses of Start, Next, End, Sub; and none for Unreached
        fun addrOf(label: String): Int = res.labelToAddress[label] ?: error("No address for label $label")
        val startAddr = addrOf("Start")
        val nextAddr = addrOf("Next")
        val endAddr = addrOf("End")
        val subAddr = addrOf("Sub")
        val unreachedAddr = addrOf("Unreached")

        val leaderAddrs = bb.blocks.map { it.startAddress }.toSet()
        assertTrue(startAddr in leaderAddrs, "Expect block leader at Start address")
        assertTrue(nextAddr in leaderAddrs, "Expect block leader at Next address")
        assertTrue(endAddr in leaderAddrs, "Expect block leader at End address")
        assertTrue(subAddr in leaderAddrs, "Expect block leader at Sub address")
        assertTrue(unreachedAddr !in leaderAddrs, "Unreached should not be a leader (unreachable)")

        val startBlock = bb.blocks.first { it.startAddress == startAddr }
        val nextBlock = bb.blocks.first { it.startAddress == nextAddr }

        // Start ends at JSR and should fall through to Next
        assertEquals(nextBlock.leaderIndex, startBlock.fallThroughLeader, "Start block should fall through to Next")

        // Next is a conditional branch to itself, so it should list itself as a target and fall through to the following block
        assertTrue(nextBlock.targetLeaders.contains(nextBlock.leaderIndex), "Next block should target itself via branch")
        val ftIdx = nextBlock.fallThroughLeader
        assertTrue(ftIdx != null && ftIdx != nextBlock.leaderIndex, "Next block should have a distinct fall-through successor")

        // There should be a block that ends with JMP and has no fall-through
        val jmpBlock = bb.blocks.first { block ->
            val lastIdx = block.endIndex
            val op = lines[lastIdx].content.instruction!!.op
            op == AssemblyOp.JMP
        }
        assertTrue(jmpBlock.fallThroughLeader == null, "Block ending with JMP should not have fall-through")
    }

    @Test
    fun basicBlocksOnSmbDisasm() {
        val path = Paths.get("smbdism.asm")
        assertTrue(Files.exists(path), "smbdism.asm should exist at project root for this test")
        val text = Files.readString(path)
        val lines = text.parseAssemblyLines()
        val res = lines.resolveAddresses(baseAddress = 0x8000)
        val reach = lines.analyzeReachability(resolution = res, entries = lines.discoverEntryPoints(resolution = res))
        val bb = lines.constructBasicBlocks(resolution = res, reachability = reach)

        assertTrue(bb.blocks.isNotEmpty(), "Expected some basic blocks in SMB disassembly")

        // Ensure blocks contain only instruction lines and never .db data lines
        bb.blocks.forEach { block ->
            block.lineIndexes.forEach { idx ->
                val r = res.resolved[idx]
                assertTrue(!r.isData && lines[idx].content.instruction != null, "Basic block should not include data lines")
            }
        }
    }
}
