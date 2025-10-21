package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import java.io.File

/**
 * Demonstration of Phase 5 Type Analysis on Super Mario Bros
 * Shows memory layout with inferred types from actual game code
 */
class SMBMemoryLayoutDemo {

    @Test
    fun analyzeSuperMarioBrosMemoryLayout() {
        val out = StringBuilder()
        fun logLine(msg: String = "") {
            println(msg)
            out.appendLine(msg)
        }

        // Read the actual Super Mario Bros disassembly
        val smbFile = File("smbdism.asm")
        if (!smbFile.exists()) {
            logLine("Warning: smbdism.asm not found, skipping demo")
            return
        }

        val assembly = smbFile.readText()

        logLine("=" * 80)
        logLine("SUPER MARIO BROS - MEMORY LAYOUT WITH TYPE INFERENCE")
        logLine("=" * 80)
        logLine()

        // Parse and run all passes through Phase 5
        logLine("Running analysis passes...")
        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution)
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val dataFlow = lines.analyzeDataFlow(cfg, dominators)
        val constants = lines.analyzeConstants(cfg)
        val memoryPatterns = lines.analyzeMemoryPatterns(cfg, constants, resolution)
        val types = lines.inferTypes(cfg, dataFlow, constants, memoryPatterns)

        logLine("✓ Analysis complete!")
        logLine()

        // Build reverse lookup: address -> constant name
        val addressToName = resolution.labelToAddress.entries
            .associate { (name, addr) -> addr to name }

        // Count assembly constants (those that don't start with L_ which are auto-generated labels)
        val constantCount = lines.lines.count { it.constant != null }
        val namedAddresses = addressToName.filter { !it.value.startsWith("L_") }

        // Print overall statistics
        logLine("📊 ANALYSIS STATISTICS")
        logLine("-" * 80)
        logLine("Total Functions Analyzed: ${types.functions.size}")
        logLine("Total Variables Typed: ${types.globalTypes.size}")
        logLine("Total Memory Locations Tracked: ${memoryPatterns.globalMemoryMap.size}")
        logLine("Assembly Constants Parsed: $constantCount")
        logLine("Named Memory Locations: ${namedAddresses.size}")

        // Debug: Show memory address distribution
        val memByRegion = memoryPatterns.globalMemoryMap.keys.groupBy { addr ->
            when {
                addr < 0x0100 -> "Zero Page"
                addr < 0x0200 -> "Stack"
                addr < 0x0800 -> "NES RAM"
                addr < 0x2000 -> "High RAM"
                addr < 0x4000 -> "PPU"
                addr < 0x6000 -> "APU/IO"
                addr < 0x8000 -> "Cartridge RAM"
                else -> "ROM"
            }
        }
        logLine("Memory distribution:")
        memByRegion.forEach { (region, addrs) ->
            logLine("  $region: ${addrs.size} locations")
        }

        logLine()

        // Group variables by type
        val variablesByType = types.globalTypes.values.groupBy { it.inferredType::class.simpleName }

        logLine("📈 TYPE DISTRIBUTION")
        logLine("-" * 80)
        variablesByType.forEach { (typeName, vars) ->
            logLine("${typeName?.padEnd(20)}: ${vars.size} variables")
        }
        logLine()

        // Show register usage patterns
        logLine("🎮 REGISTER TYPE ANALYSIS")
        logLine("-" * 80)
        listOf(Variable.RegisterA, Variable.RegisterX, Variable.RegisterY).forEach { register ->
            val typeInfo = types.globalTypes[register]
            if (typeInfo != null) {
                logLine("${register.toString().padEnd(15)}: ${typeInfo.inferredType}")
                logLine("${"".padEnd(15)}  Confidence: ${"%.1f".format(typeInfo.confidence * 100)}%")
                logLine("${"".padEnd(15)}  Usage count: ${typeInfo.usageCount}")
                logLine("${"".padEnd(15)}  Constraints: ${typeInfo.constraints.size}")
                logLine()
            }
        }

        // Collect all memory locations from memory patterns analysis
        logLine("🗺️  MEMORY LAYOUT BY REGION")
        logLine("-" * 80)

        // Zero Page variables (most important for SMB)
        val zeroPageVars = memoryPatterns.globalMemoryMap.filter { (addr, _) -> addr < 0x100 }
            .toList()
            .sortedByDescending { (_, pattern) -> pattern.accessCount }
            .take(40)

        logLine("ZERO PAGE (${'$'}00-${'$'}FF): ${memoryPatterns.globalMemoryMap.count { it.key < 0x100 }} total locations")
        logLine("Showing top 40 by usage:")
        logLine()

        zeroPageVars.forEach { (addr, pattern) ->
            val patternStr = when (pattern.accessPattern) {
                is MemoryAccessPattern.Array -> {
                    val arr = pattern.accessPattern as MemoryAccessPattern.Array
                    "Array[${arr.indexRegister}]"
                }
                is MemoryAccessPattern.Pointer -> "Pointer"
                is MemoryAccessPattern.Scalar -> "Scalar"
                else -> "Unknown"
            }

            val readWrite = when {
                pattern.isReadOnly -> "R/O"
                pattern.isWriteOnly -> "W/O"
                else -> "R/W"
            }

            val name = addressToName[addr]
            val nameStr = if (name != null && !name.startsWith("L_")) " ($name)" else ""

            logLine("  $${addr.toString(16).uppercase().padStart(2, '0')}$nameStr: $patternStr, $readWrite, " +
                "accesses: ${pattern.accessCount}, reads: ${pattern.reads.size}, writes: ${pattern.writes.size}")
        }
        logLine()

        // RAM variables ($0200-$07FF - NES RAM)
        val ramVars = memoryPatterns.globalMemoryMap.filter { (addr, _) ->
            addr in 0x0200..0x07FF
        }.toList()
            .sortedByDescending { (_, pattern) -> pattern.accessCount }

        logLine("NES RAM (${'$'}0200-${'$'}07FF): ${memoryPatterns.globalMemoryMap.count { it.key in 0x0200..0x07FF }} total locations")
        logLine("Showing all in usage order:")
        logLine()

        ramVars.forEach { (addr, pattern) ->
            val patternStr = when (pattern.accessPattern) {
                is MemoryAccessPattern.Array -> {
                    val arr = pattern.accessPattern as MemoryAccessPattern.Array
                    "Array[${arr.indexRegister}]"
                }
                is MemoryAccessPattern.Pointer -> "Pointer"
                is MemoryAccessPattern.Scalar -> "Scalar"
                else -> "Unknown"
            }

            val readWrite = when {
                pattern.isReadOnly -> "R/O"
                pattern.isWriteOnly -> "W/O"
                else -> "R/W"
            }

            val name = addressToName[addr]
            val nameStr = if (name != null && !name.startsWith("L_")) " ($name)" else ""

            logLine("  $${addr.toString(16).uppercase().padStart(4, '0')}$nameStr: $patternStr, $readWrite, " +
                "accesses: ${pattern.accessCount}, reads: ${pattern.reads.size}, writes: ${pattern.writes.size}")
        }
        logLine()

        // Additional RAM ($0800+)
        val highRamVars = memoryPatterns.globalMemoryMap.filter { (addr, _) ->
            addr >= 0x0800 && addr < 0x2000
        }.toList()
            .sortedByDescending { (_, pattern) -> pattern.accessCount }
            .take(40)

        if (highRamVars.isNotEmpty()) {
            logLine("HIGH RAM (${'$'}0800-${'$'}1FFF): ${memoryPatterns.globalMemoryMap.count { it.key in 0x0800..0x1FFF }} total locations")
            logLine("Showing top 40 by usage:")
            logLine()

            highRamVars.forEach { (addr, pattern) ->
                val patternStr = when (pattern.accessPattern) {
                    is MemoryAccessPattern.Array -> {
                        val arr = pattern.accessPattern as MemoryAccessPattern.Array
                        "Array[${arr.indexRegister}]"
                    }
                    is MemoryAccessPattern.Pointer -> "Pointer"
                    is MemoryAccessPattern.Scalar -> "Scalar"
                    else -> "Unknown"
                }

                val readWrite = when {
                    pattern.isReadOnly -> "R/O"
                    pattern.isWriteOnly -> "W/O"
                    else -> "R/W"
                }

                val name = addressToName[addr]
                val nameStr = if (name != null && !name.startsWith("L_")) " ($name)" else ""

                logLine("  $${addr.toString(16).uppercase().padStart(4, '0')}$nameStr: $patternStr, $readWrite, " +
                    "accesses: ${pattern.accessCount}, reads: ${pattern.reads.size}, writes: ${pattern.writes.size}")
            }
            logLine()
        }

        // Analyze specific well-known SMB functions
        logLine("🔍 FUNCTION-SPECIFIC TYPE ANALYSIS")
        logLine("-" * 80)

        val interestingFunctions = listOf("GameEngine", "PlayerMovementSubs", "RunGameTimer")

        types.functions.filter { it.function.entryLabel in interestingFunctions }.take(5).forEach { funcInfo ->
            logLine("Function: ${funcInfo.function.entryLabel ?: "UNNAMED"}")
            logLine("  Address: $${funcInfo.function.entryAddress.toString(16).uppercase()}")
            logLine("  Variables typed: ${funcInfo.variableTypes.size}")

            // Show parameters
            if (funcInfo.parameters.isNotEmpty()) {
                logLine("  Parameters (${funcInfo.parameters.size}):")
                funcInfo.parameters.forEach { param ->
                    logLine("    ${param.variable}: ${param.inferredType} (confidence: ${"%.1f".format(param.confidence * 100)}%)")
                }
            }

            // Show returns
            if (funcInfo.returns.isNotEmpty()) {
                logLine("  Returns (${funcInfo.returns.size}):")
                funcInfo.returns.forEach { ret ->
                    logLine("    ${ret.variable}: ${ret.inferredType} (confidence: ${"%.1f".format(ret.confidence * 100)}%)")
                }
            }

            // Show local variables with interesting types
            val interestingLocals = funcInfo.localVariables.filter {
                it.inferredType !is InferredType.UInt8 && it.inferredType !is InferredType.Unknown
            }
            if (interestingLocals.isNotEmpty()) {
                logLine("  Interesting locals (${interestingLocals.size}):")
                interestingLocals.take(5).forEach { local ->
                    logLine("    ${local.variable}: ${local.inferredType} (confidence: ${"%.1f".format(local.confidence * 100)}%)")
                }
            }

            logLine()
        }

        // Show type inference quality metrics
        logLine("📈 TYPE INFERENCE QUALITY")
        logLine("-" * 80)

        val typedVars = types.globalTypes.values
        val highConfidence = typedVars.count { it.confidence >= 0.7 }
        val mediumConfidence = typedVars.count { it.confidence in 0.4..0.69 }
        val lowConfidence = typedVars.count { it.confidence < 0.4 }

        logLine("High confidence (≥70%): $highConfidence variables (${"%.1f".format(highConfidence * 100.0 / typedVars.size)}%)")
        logLine("Medium confidence (40-69%): $mediumConfidence variables (${"%.1f".format(mediumConfidence * 100.0 / typedVars.size)}%)")
        logLine("Low confidence (<40%): $lowConfidence variables (${"%.1f".format(lowConfidence * 100.0 / typedVars.size)}%)")
        logLine()

        val nonGeneric = typedVars.count {
            it.inferredType !is InferredType.UInt8 && it.inferredType !is InferredType.Unknown
        }
        logLine("Specific types inferred: $nonGeneric / ${typedVars.size} (${"%.1f".format(nonGeneric * 100.0 / typedVars.size)}%)")
        logLine()

        // Type breakdown
        logLine("TYPE BREAKDOWN:")
        val typeBreakdown = typedVars.groupBy { it.inferredType::class.simpleName }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }

        typeBreakdown.forEach { (typeName, count) ->
            val percentage = count * 100.0 / typedVars.size
            logLine("  ${typeName?.padEnd(20)}: $count (${"%.1f".format(percentage)}%)")
        }
        logLine()

        // Show most-used variables (by usage count)
        logLine("🔥 MOST FREQUENTLY USED VARIABLES")
        logLine("-" * 80)
        val topUsed = typedVars.sortedByDescending { it.usageCount }.take(15)
        topUsed.forEach { tv ->
            logLine("${tv.variable.toString().padEnd(25)}: ${tv.usageCount.toString().padStart(4)} uses, " +
                "type: ${tv.inferredType}, confidence: ${"%.1f".format(tv.confidence * 100)}%")
        }
        logLine()

        logLine("=" * 80)
        logLine("✅ Memory layout analysis complete!")
        logLine("=" * 80)

        File("outputs/smb-memory-layout.txt").writeText(out.toString())
    }

    private operator fun String.times(count: Int): String = this.repeat(count)
}
