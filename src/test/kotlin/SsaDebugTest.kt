package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test

class SsaDebugTest {

    @Test
    fun debugConditionalBranch() {
        val assembly = """
            main:
                LDA #${'$'}10
                CMP #${'$'}05
                BEQ equal
                LDA #${'$'}20
            equal:
                STA ${'$'}4000
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

        println("\n=== CFG ===")
        cfg.functions.forEach { function ->
            println("Function at ${function.entryLeader}:")
            function.blocks.forEach { block ->
                println("  Block ${block.leaderIndex}: lines ${block.lineIndexes}")
            }
            println("  Edges:")
            function.edges.forEach { edge ->
                println("    ${edge.fromLeader} -> ${edge.toLeader} (${edge.kind})")
            }
        }

        println("\n=== Dominators ===")
        dominators.functions.forEach { domAnalysis ->
            println("Function:")
            domAnalysis.leaderToDomNode.values.forEach { node ->
                println("  Block ${node.leaderIndex}:")
                println("    Immediate Dominator: ${node.immediateDominator?.leaderIndex}")
                println("    Children: ${node.children.map { it.leaderIndex }}")
                println("    Dominance Frontier: ${node.dominanceFrontier}")
            }
        }

        println("\n=== Data Flow ===")
        dataflow.functions.forEach { funcDataflow ->
            println("Function:")
            println("  Definitions:")
            funcDataflow.definitions.forEach { def ->
                println("    Line ${def.lineRef.line}: defines ${def.variable} in block ${def.blockLeader}")
            }
            println("  Uses:")
            funcDataflow.uses.forEach { use ->
                println("    Line ${use.lineRef.line}: uses ${use.variable} in block ${use.blockLeader}")
            }
        }

        val ssa = lines.constructSsaForm(cfg, dominators, dataflow)

        println("\n=== SSA ===")
        ssa.functions.forEach { ssaFunc ->
            println("Function:")
            ssaFunc.blocks.forEach { block ->
                println("  Block ${block.originalBlock.leaderIndex}:")
                println("    Phi functions: ${block.phiFunctions.size}")
                block.phiFunctions.forEach { phi ->
                    println("      ${phi.result} = φ(${phi.operands.joinToString { "${it.first}:${it.second}" }})")
                }
                println("    Instructions: ${block.instructions.size}")
                block.instructions.forEach { instr ->
                    println("      ${instr.originalInstruction.op}: uses=${instr.uses}, defines=${instr.defines}")
                }
            }
        }
    }
}
