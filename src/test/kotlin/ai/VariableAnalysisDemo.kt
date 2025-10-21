package com.ivieleague.decompiler6502tokotlin

import org.junit.jupiter.api.Test

/**
 * Demonstration test showing the difference between global and function-local registers
 */
class VariableAnalysisDemo {

    @Test
    fun `demonstrate global vs local register variables`() {
        val asm = """
            .export Init, Process
            Init:
                LDA #${'$'}00
                STA ${'$'}0200
                RTS

            Process:
                LDX #${'$'}01
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Init", "Process"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        println("\n=== WITHOUT Variable Analysis (Legacy - Global Registers) ===")
        val legacyResult = cfg.emitCompleteKotlin(codeFile, null, null, null, null, null)
        println(legacyResult.kotlinFile.emitToString())

        println("\n=== WITH Variable Analysis (Function-Local Registers) ===")
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val typeInference = codeFile.inferTypes(cfg, dataFlow, constants, memoryPatterns)
        val loops = codeFile.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val variableId = codeFile.identifyVariables(cfg, dataFlow, typeInference, memoryPatterns, loops)

        val modernResult = cfg.emitCompleteKotlin(codeFile, variableId, null, null, null, null)
        println(modernResult.kotlinFile.emitToString())

        println("\n=== Variable Analysis Summary ===")
        println("Global variables: ${variableId.globals.size}")
        variableId.globals.forEach { variable ->
            println("  - ${variable.id} (${variable.scope})")
        }

        variableId.functions.forEach { funcVars ->
            val funcName = funcVars.function.entryLabel ?: "func_${funcVars.function.entryAddress.toString(16)}"
            println("\nFunction: $funcName")
            println("  Local variables: ${funcVars.localVariables.size}")
            funcVars.localVariables.forEach { variable ->
                println("    - ${variable.id}")
            }
            println("  Parameters: ${funcVars.parameters.size}")
            println("  Return values: ${funcVars.returnValues.size}")
        }
    }
}
