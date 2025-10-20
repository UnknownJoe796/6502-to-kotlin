package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import java.io.File

/**
 * Demonstration of Phase 7 Variable & Naming analysis on real Super Mario Bros assembly code
 * This test shows what we can analyze through Pass 29 (Parameter Recovery)
 */
class Phase7AnalysisDemo {

    // Create output file
    private val outputFile = File("outputs/phase7-analysis-output.txt")
    private val output = StringBuilder()

    private fun log(msg: String = "") {
        output.appendLine(msg)
        println(msg)
    }

    @Test
    fun analyzeSuperMarioBrosCode() {
        // Read the actual Super Mario Bros disassembly
        val smbFile = File("smbdism.asm")
        if (!smbFile.exists()) {
            println("Warning: smbdism.asm not found, skipping demo")
            return
        }

        val assembly = smbFile.readText()

        log("=" * 80)
        log("PHASE 7 VARIABLE & NAMING ANALYSIS DEMO")
        log("Analyzing Super Mario Bros Disassembly")
        log("=" * 80)
        log("")

        // Parse and run all passes through Phase 7
        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution)
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val constants = lines.analyzeConstants(cfg)
        val dataFlow = lines.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = lines.analyzeMemoryPatterns(cfg, constants, resolution)
        val typeInference = lines.inferTypes(cfg, dataFlow, constants, memoryPatterns)
        val loops = lines.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val variableId = lines.identifyVariables(cfg, dataFlow, typeInference, memoryPatterns, loops)
        val naming = variableId.assignNames(resolution)
        val functionBoundaries = lines.detectFunctionBoundaries(resolution, entries, reachability, blocks, cfg)
        val callGraph = lines.constructCallGraph(resolution, entries, reachability, blocks, cfg, functionBoundaries)
        val paramRecovery = variableId.recoverParameters(cfg, callGraph, naming, resolution)

        log("📊 OVERALL STATISTICS")
        log("-" * 80)
        log("Total Assembly Lines: ${lines.lines.size}")
        log("Total Functions: ${cfg.functions.size}")
        log("Total Global Variables: ${variableId.globals.size}")
        log("Total Named Variables: ${naming.namedVariables.size}")
        log("Total Function Signatures: ${paramRecovery.functionSignatures.size}")
        log()

        // Global Variables Summary
        log("🌍 GLOBAL VARIABLES")
        log("=" * 80)
        log("Total: ${variableId.globals.size}")
        log()

        // Group globals by type
        val globalsByType = variableId.globals.groupBy { it.inferredType::class.simpleName }
        globalsByType.toList().sortedBy { it.first }.forEach { (typeName, vars) ->
            log("  $typeName: ${vars.size} variables")
            vars.take(5).forEach { variable ->
                val namedVar = naming.namedVariables[variable.id]
                val name = namedVar?.name ?: "unnamed"
                val address = when (val id = variable.id) {
                    is VariableId.Memory -> "0x${id.address.toString(16).uppercase().padStart(4, '0')}"
                    is VariableId.MultiByteMemory -> "0x${id.baseAddress.toString(16).uppercase().padStart(4, '0')}"
                    else -> "?"
                }
                log("    - $name @ $address (${variable.inferredType})")
            }
            if (vars.size > 5) {
                log("    ... and ${vars.size - 5} more")
            }
            log()
        }

        // Analyze interesting functions
        val interestingFunctionNames = setOf(
            "GameEngine",
            "PlayerLoseLife",
            "IncreaseScore",
            "ProcessAreaData"
        )

        cfg.functions.forEachIndexed { index, function ->
            if (function.entryLabel in interestingFunctionNames) {
                val funcVars = variableId.functions.getOrNull(index)
                val signature = paramRecovery.functionSignatures.getOrNull(index)

                if (funcVars != null && signature != null) {
                    analyzeFunctionInDetail(
                        lines,
                        resolution,
                        function,
                        funcVars,
                        signature,
                        naming,
                        typeInference.functions.getOrNull(index),
                        memoryPatterns.functions.getOrNull(index)
                    )
                }
            }
        }

        // Summary statistics
        log()
        log("🎯 PHASE 7 SUMMARY STATISTICS")
        log("=" * 80)

        // Variable identification stats
        val totalLocalVars = variableId.functions.sumOf { it.localVariables.size }
        val totalParams = variableId.functions.sumOf { it.parameters.size }
        val totalReturns = variableId.functions.sumOf { it.returnValues.size }

        log("Variable Identification:")
        log("  - Global variables: ${variableId.globals.size}")
        log("  - Local variables: $totalLocalVars")
        log("  - Parameters: $totalParams")
        log("  - Return values: $totalReturns")
        log("  - Total identified: ${variableId.globals.size + totalLocalVars + totalParams + totalReturns}")
        log()

        // Variable naming stats
        val namesBySource = naming.namedVariables.values.groupBy { it.nameSource }
        log("Variable Naming:")
        namesBySource.forEach { (source, vars) ->
            log("  - From $source: ${vars.size} variables")
            val avgConfidence = vars.map { it.confidence }.average()
            log("    Average confidence: ${"%.2f".format(avgConfidence)}")
        }
        log()

        // Type inference stats
        val globalTypeDistribution = variableId.globals.groupBy { it.inferredType::class.simpleName }
        log("Global Variable Types:")
        globalTypeDistribution.toList().sortedBy { it.first }.forEach { (type, vars) ->
            log("  - $type: ${vars.size}")
        }
        log()

        // Calling convention stats
        val conventionsByType = paramRecovery.functionSignatures.groupBy { it.callingConvention::class.simpleName }
        log("Calling Conventions:")
        conventionsByType.toList().sortedBy { it.first }.forEach { (convention, funcs) ->
            log("  - $convention: ${funcs.size} functions")
        }
        log()

        // Side effect analysis
        val totalSideEffects = paramRecovery.functionSignatures.sumOf { it.sideEffects.size }
        val funcsWithSideEffects = paramRecovery.functionSignatures.count { it.sideEffects.isNotEmpty() }
        log("Side Effect Analysis:")
        log("  - Functions with side effects: $funcsWithSideEffects / ${paramRecovery.functionSignatures.size}")
        log("  - Total side effects: $totalSideEffects")

        val sideEffectsByType = paramRecovery.functionSignatures
            .flatMap { it.sideEffects }
            .groupBy { it::class.simpleName }
        sideEffectsByType.toList().sortedBy { it.first }.forEach { (type, effects) ->
            log("    - $type: ${effects.size}")
        }
        log()

        log("=" * 80)

        // Write output to file
        outputFile.writeText(output.toString())
        println("\n\n✅ Analysis complete! Output written to: ${outputFile.absolutePath}")
        println("Total size: ${output.length} characters\n")
    }

    private fun analyzeFunctionInDetail(
        lines: AssemblyCodeFile,
        resolution: AddressResolution,
        function: FunctionCfg,
        funcVars: FunctionVariables,
        signature: FunctionSignature,
        naming: VariableNaming,
        typeInfo: FunctionTypeInfo?,
        memoryInfo: FunctionMemoryAnalysis?
    ) {
        log()
        log("🔍 FUNCTION ANALYSIS: ${signature.name}")
        log("=" * 80)
        log("Entry Address: 0x${function.entryAddress.toString(16).uppercase().padStart(4, '0')}")
        log("Basic Blocks: ${function.blocks.size}")
        log()

        // Function Signature
        log("📝 FUNCTION SIGNATURE:")
        log("-" * 80)

        val paramList = if (signature.parameters.isEmpty()) {
            "()"
        } else {
            "(\n" + signature.parameters.joinToString(",\n") { param ->
                "    ${param.name}: ${param.type} @ ${param.location}"
            } + "\n)"
        }

        val returnStr = signature.returnValue?.let {
            ": ${it.type} @ ${it.location}"
        } ?: ""

        log("fun ${signature.name}$paramList$returnStr")
        log()

        if (signature.parameters.isNotEmpty()) {
            log("Parameters: ${signature.parameters.size}")
            signature.parameters.forEach { param ->
                log("  - ${param.name}")
                log("    Type: ${param.type}")
                log("    Location: ${param.location}")
                log("    Position: ${param.index}")
            }
            log()
        }

        if (signature.returnValue != null) {
            log("Return Value:")
            log("  - Name: ${signature.returnValue.name}")
            log("  - Type: ${signature.returnValue.type}")
            log("  - Location: ${signature.returnValue.location}")
            log()
        }

        log("Calling Convention: ${signature.callingConvention::class.simpleName}")
        when (val conv = signature.callingConvention) {
            is CallingConvention.RegisterBased -> {
                if (conv.parameterOrder.isNotEmpty()) {
                    log("  Parameter registers: ${conv.parameterOrder.joinToString(", ")}")
                }
                conv.returnRegister?.let {
                    log("  Return register: $it")
                }
            }
            is CallingConvention.MemoryBased -> {
                log("  Parameter addresses: ${conv.parameterAddresses.joinToString(", ") { "0x${it.toString(16).uppercase()}" }}")
            }
            is CallingConvention.Mixed -> {
                log("  Registers: ${conv.registers.joinToString(", ")}")
                log("  Memory: ${conv.memoryLocations.joinToString(", ") { "0x${it.toString(16).uppercase()}" }}")
            }
            else -> {}
        }
        log()

        // Side Effects
        if (signature.sideEffects.isNotEmpty()) {
            log("⚠️  SIDE EFFECTS: ${signature.sideEffects.size}")
            log("-" * 80)
            signature.sideEffects.forEach { effect ->
                when (effect) {
                    is SideEffect.ModifiesMemory -> {
                        log("  - Modifies memory at 0x${effect.address.toString(16).uppercase()}")
                        effect.name?.let { log("    (${it})") }
                    }
                    is SideEffect.ModifiesGlobal -> {
                        val name = naming.namedVariables[effect.variable.id]?.name ?: "unknown"
                        log("  - Modifies global variable: $name")
                        log("    Type: ${effect.variable.inferredType}")
                    }
                    is SideEffect.ModifiesHardware -> {
                        log("  - Modifies hardware registers (PPU/APU)")
                    }
                    is SideEffect.CallsFunction -> {
                        log("  - Calls function at 0x${effect.targetFunction.entryAddress.toString(16).uppercase()}")
                    }
                }
            }
            log()
        }

        // Local Variables
        if (funcVars.localVariables.isNotEmpty()) {
            log("🔢 LOCAL VARIABLES: ${funcVars.localVariables.size}")
            log("-" * 80)
            funcVars.localVariables.take(10).forEach { variable ->
                val namedVar = naming.namedVariables[variable.id]
                val name = namedVar?.name ?: "unnamed"
                val nameSource = namedVar?.nameSource?.toString() ?: "?"
                val confidence = namedVar?.confidence?.let { "%.2f".format(it) } ?: "?"

                log("  - $name")
                log("    Type: ${variable.inferredType}")
                log("    Scope: ${variable.scope}")
                log("    Name source: $nameSource (confidence: $confidence)")
                log("    Access pattern: ${variable.accessPattern::class.simpleName}")
                log("    Usages: ${variable.usageSites.size}")

                val reads = variable.usageSites.count { it.usageType == UsageType.READ }
                val writes = variable.usageSites.count { it.usageType == UsageType.WRITE }
                val rmw = variable.usageSites.count { it.usageType == UsageType.READ_MODIFY_WRITE }
                log("      Reads: $reads, Writes: $writes, RMW: $rmw")

                val loopRoles = variable.usageSites.mapNotNull { it.context.loopRole }.distinct()
                if (loopRoles.isNotEmpty()) {
                    log("      Loop roles: ${loopRoles.joinToString(", ")}")
                }
                log()
            }

            if (funcVars.localVariables.size > 10) {
                log("  ... and ${funcVars.localVariables.size - 10} more local variables")
                log()
            }
        }

        // Memory Access Patterns
        memoryInfo?.let { info ->
            if (info.identifiedArrays.isNotEmpty() || info.identifiedPointers.isNotEmpty()) {
                log("💾 MEMORY ACCESS PATTERNS:")
                log("-" * 80)

                if (info.identifiedArrays.isNotEmpty()) {
                    log("Arrays: ${info.identifiedArrays.size}")
                    info.identifiedArrays.take(3).forEach { array ->
                        log("  - Base: 0x${array.baseAddress.toString(16).uppercase()}")
                        log("    Index register: ${array.indexRegister}")
                        log("    Estimated length: ${array.estimatedLength ?: "unknown"}")
                        log("    Observed indices: ${array.observedIndices.take(5).joinToString(", ")}")
                    }
                    if (info.identifiedArrays.size > 3) {
                        log("  ... and ${info.identifiedArrays.size - 3} more arrays")
                    }
                    log()
                }

                if (info.identifiedPointers.isNotEmpty()) {
                    log("Pointers: ${info.identifiedPointers.size}")
                    info.identifiedPointers.take(3).forEach { pointer ->
                        log("  - Pointer at 0x${pointer.pointerAddress.toString(16).uppercase()}")
                    }
                    if (info.identifiedPointers.size > 3) {
                        log("  ... and ${info.identifiedPointers.size - 3} more pointers")
                    }
                    log()
                }
            }
        }

        // Type inference confidence
        typeInfo?.let { info ->
            val typedVarsWithConfidence = info.variableTypes.values.filter { it.confidence > 0.5 }
            if (typedVarsWithConfidence.isNotEmpty()) {
                log("🎯 TYPE INFERENCE (high confidence):")
                log("-" * 80)
                val avgConfidence = typedVarsWithConfidence.map { it.confidence }.average()
                log("Average confidence: ${"%.2f".format(avgConfidence)}")
                log("High-confidence variables: ${typedVarsWithConfidence.size} / ${info.variableTypes.size}")
                log()
            }
        }

        log("-" * 80)
    }

    private operator fun String.times(count: Int): String = this.repeat(count)
}
