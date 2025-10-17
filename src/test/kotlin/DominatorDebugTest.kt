package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test

class DominatorDebugTest {
    
    @Test
    fun debugSimpleLoop() {
        val assembly = """
            main:
                LDX #${'$'}10
            loop:
                DEX
                BNE loop
                RTS
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        
        println("=== CFG Debug ===")
        println("Functions: ${cfg.functions.size}")
        cfg.functions.forEach { fn ->
            println("Function: ${fn.entryLabel} (entry=${fn.entryLeader})")
            println("  Blocks: ${fn.blocks.size}")
            fn.blocks.forEach { block ->
                println("    Block ${block.leaderIndex}: ${lines[block.leaderIndex].content.originalLine?.trim()}")
            }
            println("  Edges: ${fn.edges.size}")
            fn.edges.forEach { edge ->
                println("    ${edge.fromLeader} -> ${edge.toLeader} (${edge.kind})")
            }
        }
        
        val dominators = lines.constructDominatorTrees(cfg)
        
        println("=== Dominator Debug ===")
        dominators.functions.forEach { fnAnalysis ->
            println("Function: ${fnAnalysis.function.entryLabel}")
            println("  Back edges: ${fnAnalysis.backEdges}")
            println("  Natural loops: ${fnAnalysis.naturalLoops.size}")
            fnAnalysis.naturalLoops.forEach { loop ->
                println("    Loop: header=${loop.header}, source=${loop.backEdgeSource}, blocks=${loop.blocks}")
            }
        }
        
        // Debug complete - check if we detected loops  
        val mainFunction = dominators.functions[0]
        println("Back edges found: ${mainFunction.backEdges.size}")
        println("Natural loops found: ${mainFunction.naturalLoops.size}")
    }
}