package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test

class DataFlowDebugTest {
    
    @Test
    fun debugDataFlow() {
        val assembly = """
            main:
                LDA #${'$'}10    ; Used
                STA ${'$'}2000
                LDX #${'$'}20    ; Dead - never used
                LDA #${'$'}30    ; Used  
                STA ${'$'}3000
                RTS
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("main"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val dataflow = lines.analyzeDataFlow(cfg, dominators)
        
        val mainFunction = dataflow.functions[0]
        
        println("=== Definitions ===")
        mainFunction.definitions.forEach { def ->
            println("${def.lineIndex}: ${def.variable} (${lines[def.lineIndex].originalLine?.trim()})")
        }
        
        println("=== Uses ===")
        mainFunction.uses.forEach { use ->
            println("${use.lineIndex}: ${use.variable} (${lines[use.lineIndex].originalLine?.trim()})")
        }
        
        println("=== Dead Definitions ===")
        mainFunction.deadDefinitions.forEach { def ->
            println("${def.lineIndex}: ${def.variable} (${lines[def.lineIndex].originalLine?.trim()})")
        }
        
        println("=== Def-Use Chains ===")
        mainFunction.defUseChains.forEach { chain ->
            println("Def ${chain.definition.lineIndex}: ${chain.definition.variable} -> ${chain.reachedUses.size} uses")
            chain.reachedUses.forEach { use ->
                println("  Use ${use.lineIndex}: ${use.variable}")
            }
        }
    }
}