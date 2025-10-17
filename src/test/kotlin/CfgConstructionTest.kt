package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import java.nio.file.Files
import java.nio.file.Paths

class CfgConstructionTest {
    @Test
    fun cfgOnSnippetHasExpectedEdges() {
        val snippet = """
            Start:
                JSR Sub
                BNE Next
            AfterBranch:
                JMP End
            Next:
                NOP
            End:
                RTS
            Sub:
                RTS
        """.trimIndent()
        val lines = snippet.parseAssemblyLines()
        val res = lines.resolveAddresses(baseAddress = 0x8000)
        val eps = lines.discoverEntryPoints(resolution = res, exportedLabels = setOf("Start"))
        val reach = lines.analyzeReachability(resolution = res, entries = eps)
        val bbs = lines.constructBasicBlocks(resolution = res, reachability = reach)
        val cfg = lines.constructCfg(resolution = res, reachability = reach, blocks = bbs, entries = eps)

        // Helper to find leader by label
        fun leaderOf(label: String): Int {
            val addr = res.labelToAddress[label] ?: error("No address for label $label")
            val bb = cfg.program.blocks.firstOrNull { it.startAddress == addr } ?: error("No block for $label")
            return bb.leaderIndex
        }

        val nextLeader = leaderOf("Next")
        val endLeader = leaderOf("End")
        val afterBranchLeader = leaderOf("AfterBranch")

        // Expect a TRUE edge to Next from the block ending with BNE
        assertTrue(cfg.program.edges.any { it.kind == CfgEdgeKind.TRUE && it.toLeader == nextLeader }, "Expected TRUE edge to Next")
        // Expect a FALSE edge to AfterBranch (fall-through from BNE)
        assertTrue(cfg.program.edges.any { it.kind == CfgEdgeKind.FALSE && it.toLeader == afterBranchLeader }, "Expected FALSE edge to AfterBranch")
        // Expect an UNCONDITIONAL edge from AfterBranch to End
        assertTrue(cfg.program.edges.any { it.kind == CfgEdgeKind.UNCONDITIONAL && it.fromLeader == afterBranchLeader && it.toLeader == endLeader }, "Expected UNCONDITIONAL edge AfterBranch->End")
        // Expect at least one RETURN edge (from End and Sub blocks)
        assertTrue(cfg.program.edges.any { it.kind == CfgEdgeKind.RETURN && it.toLeader == null }, "Expected RETURN edge(s) to exit sentinel")

        // Verify per-function CFG for Start contains Start, Next, End but not Sub (callee)
        val startLeader = leaderOf("Start")
        val fn = cfg.functions.firstOrNull { it.entryLeader == startLeader } ?: error("No function CFG for Start")
        val fnLeaders = fn.blocks.map { it.leaderIndex }.toSet()
        assertTrue(startLeader in fnLeaders && nextLeader in fnLeaders && endLeader in fnLeaders, "Function CFG should include Start, Next, End")
        val subLeader = leaderOf("Sub")
        assertTrue(subLeader !in fnLeaders, "Function CFG should not include callee 'Sub' block")
    }

    @Test
    fun cfgBuildsOnSmbDisasm() {
        val path = Paths.get("smbdism.asm")
        assertTrue(Files.exists(path), "smbdism.asm should exist at project root for this test")
        val text = Files.readString(path)
        val lines = text.parseAssemblyLines()
        val res = lines.resolveAddresses(baseAddress = 0x8000)
        val eps = lines.discoverEntryPoints(resolution = res)
        val reach = lines.analyzeReachability(resolution = res, entries = eps)
        val bbs = lines.constructBasicBlocks(resolution = res, reachability = reach)
        val cfg = lines.constructCfg(resolution = res, reachability = reach, blocks = bbs, entries = eps)

        // Basic properties
        assertTrue(cfg.program.blocks.isNotEmpty(), "CFG should have some blocks")
        assertTrue(cfg.program.edges.isNotEmpty(), "CFG should have some edges")

        // Sanity: no data lines are represented as blocks (ensured by pass 6)
        // Also ensure that blocks ending with JMP have no FALL_THROUGH edges
        val leaderToOp = cfg.program.blocks.associate { it.leaderIndex to (lines[it.endIndex].content.instruction?.op) }
        cfg.program.edges.groupBy { it.fromLeader }.forEach { (from, edges) ->
            val op = leaderToOp[from]
            if (op == AssemblyOp.JMP) {
                assertTrue(edges.none { it.kind == CfgEdgeKind.FALL_THROUGH || it.kind == CfgEdgeKind.FALSE }, "JMP blocks should not have fall-through/false edges")
            }
        }
    }
}
