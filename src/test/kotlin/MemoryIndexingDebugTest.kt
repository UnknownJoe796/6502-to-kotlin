package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test

class MemoryIndexingDebugTest {
    
    @Test
    fun debugMemoryIndexing() {
        val assembly = """
            main:
                LDX #${'$'}05
                LDA ${'$'}2000,X
                STA ${'$'}3000,Y
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
        
        val xUses = mainFunction.uses.filter { it.variable == Variable.RegisterX }
        val yUses = mainFunction.uses.filter { it.variable == Variable.RegisterY }
        
        println("X uses: ${xUses.size}")
        println("Y uses: ${yUses.size}")
        
        // Debug addressing modes
        lines.forEachIndexed { index, line ->
            line.instruction?.let { instr ->
                if (instr.address != null) {
                    println("Line $index: ${instr.op} ${instr.address} (${instr.address!!::class.simpleName})")
                }
            }
        }
    }
}