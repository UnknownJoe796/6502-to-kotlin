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
        // Read the actual Super Mario Bros disassembly
        val smbFile = File("smbdism.asm")
        if (!smbFile.exists()) {
            println("Warning: smbdism.asm not found, skipping demo")
            return
        }

        val assembly = smbFile.readText()

        println("=" * 80)
        println("SUPER MARIO BROS - MEMORY LAYOUT WITH TYPE INFERENCE")
        println("=" * 80)
        println()

        // Parse and run all passes through Phase 5
        println("Running analysis passes...")
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

        println("✓ Analysis complete!")
        println()

        // Print overall statistics
        println("📊 ANALYSIS STATISTICS")
        println("-" * 80)
        println("Total Functions Analyzed: ${types.functions.size}")
        println("Total Variables Typed: ${types.globalTypes.size}")
        println("Total Memory Locations Tracked: ${memoryPatterns.globalMemoryMap.size}")

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
        println("Memory distribution:")
        memByRegion.forEach { (region, addrs) ->
            println("  $region: ${addrs.size} locations")
        }

        println()

        // Group variables by type
        val variablesByType = types.globalTypes.values.groupBy { it.inferredType::class.simpleName }

        println("📈 TYPE DISTRIBUTION")
        println("-" * 80)
        variablesByType.forEach { (typeName, vars) ->
            println("${typeName?.padEnd(20)}: ${vars.size} variables")
        }
        println()

        // Show register usage patterns
        println("🎮 REGISTER TYPE ANALYSIS")
        println("-" * 80)
        listOf(Variable.RegisterA, Variable.RegisterX, Variable.RegisterY).forEach { register ->
            val typeInfo = types.globalTypes[register]
            if (typeInfo != null) {
                println("${register.toString().padEnd(15)}: ${typeInfo.inferredType}")
                println("${"".padEnd(15)}  Confidence: ${"%.1f".format(typeInfo.confidence * 100)}%")
                println("${"".padEnd(15)}  Usage count: ${typeInfo.usageCount}")
                println("${"".padEnd(15)}  Constraints: ${typeInfo.constraints.size}")
                println()
            }
        }

        // Collect all memory locations from memory patterns analysis
        println("🗺️  MEMORY LAYOUT BY REGION")
        println("-" * 80)

        // Zero Page variables (most important for SMB)
        val zeroPageVars = memoryPatterns.globalMemoryMap.filter { (addr, _) -> addr < 0x100 }
            .toList()
            .sortedByDescending { (_, pattern) -> pattern.accessCount }
            .take(40)

        println("ZERO PAGE (${'$'}00-${'$'}FF): ${memoryPatterns.globalMemoryMap.count { it.key < 0x100 }} total locations")
        println("Showing top 40 by usage:")
        println()

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

            println("  $${addr.toString(16).uppercase().padStart(2, '0')}: $patternStr, $readWrite, " +
                "accesses: ${pattern.accessCount}, reads: ${pattern.reads.size}, writes: ${pattern.writes.size}")
        }
        println()

        // RAM variables ($0200-$07FF - NES RAM)
        val ramVars = memoryPatterns.globalMemoryMap.filter { (addr, _) ->
            addr in 0x0200..0x07FF
        }.toList()
            .sortedByDescending { (_, pattern) -> pattern.accessCount }
            .take(40)

        println("NES RAM (${'$'}0200-${'$'}07FF): ${memoryPatterns.globalMemoryMap.count { it.key in 0x0200..0x07FF }} total locations")
        println("Showing top 40 by usage:")
        println()

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

            println("  $${addr.toString(16).uppercase().padStart(4, '0')}: $patternStr, $readWrite, " +
                "accesses: ${pattern.accessCount}, reads: ${pattern.reads.size}, writes: ${pattern.writes.size}")
        }
        println()

        // Additional RAM ($0800+)
        val highRamVars = memoryPatterns.globalMemoryMap.filter { (addr, _) ->
            addr >= 0x0800 && addr < 0x2000
        }.toList()
            .sortedByDescending { (_, pattern) -> pattern.accessCount }
            .take(40)

        if (highRamVars.isNotEmpty()) {
            println("HIGH RAM (${'$'}0800-${'$'}1FFF): ${memoryPatterns.globalMemoryMap.count { it.key in 0x0800..0x1FFF }} total locations")
            println("Showing top 40 by usage:")
            println()

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

                println("  $${addr.toString(16).uppercase().padStart(4, '0')}: $patternStr, $readWrite, " +
                    "accesses: ${pattern.accessCount}, reads: ${pattern.reads.size}, writes: ${pattern.writes.size}")
            }
            println()
        }

        // Analyze specific well-known SMB functions
        println("🔍 FUNCTION-SPECIFIC TYPE ANALYSIS")
        println("-" * 80)

        val interestingFunctions = listOf("GameEngine", "PlayerMovementSubs", "RunGameTimer")

        types.functions.filter { it.function.entryLabel in interestingFunctions }.take(5).forEach { funcInfo ->
            println("Function: ${funcInfo.function.entryLabel ?: "UNNAMED"}")
            println("  Address: $${funcInfo.function.entryAddress.toString(16).uppercase()}")
            println("  Variables typed: ${funcInfo.variableTypes.size}")

            // Show parameters
            if (funcInfo.parameters.isNotEmpty()) {
                println("  Parameters (${funcInfo.parameters.size}):")
                funcInfo.parameters.forEach { param ->
                    println("    ${param.variable}: ${param.inferredType} (confidence: ${"%.1f".format(param.confidence * 100)}%)")
                }
            }

            // Show returns
            if (funcInfo.returns.isNotEmpty()) {
                println("  Returns (${funcInfo.returns.size}):")
                funcInfo.returns.forEach { ret ->
                    println("    ${ret.variable}: ${ret.inferredType} (confidence: ${"%.1f".format(ret.confidence * 100)}%)")
                }
            }

            // Show local variables with interesting types
            val interestingLocals = funcInfo.localVariables.filter {
                it.inferredType !is InferredType.UInt8 && it.inferredType !is InferredType.Unknown
            }
            if (interestingLocals.isNotEmpty()) {
                println("  Interesting locals (${interestingLocals.size}):")
                interestingLocals.take(5).forEach { local ->
                    println("    ${local.variable}: ${local.inferredType} (confidence: ${"%.1f".format(local.confidence * 100)}%)")
                }
            }

            println()
        }

        // Show type inference quality metrics
        println("📈 TYPE INFERENCE QUALITY")
        println("-" * 80)

        val typedVars = types.globalTypes.values
        val highConfidence = typedVars.count { it.confidence >= 0.7 }
        val mediumConfidence = typedVars.count { it.confidence in 0.4..0.69 }
        val lowConfidence = typedVars.count { it.confidence < 0.4 }

        println("High confidence (≥70%): $highConfidence variables (${"%.1f".format(highConfidence * 100.0 / typedVars.size)}%)")
        println("Medium confidence (40-69%): $mediumConfidence variables (${"%.1f".format(mediumConfidence * 100.0 / typedVars.size)}%)")
        println("Low confidence (<40%): $lowConfidence variables (${"%.1f".format(lowConfidence * 100.0 / typedVars.size)}%)")
        println()

        val nonGeneric = typedVars.count {
            it.inferredType !is InferredType.UInt8 && it.inferredType !is InferredType.Unknown
        }
        println("Specific types inferred: $nonGeneric / ${typedVars.size} (${"%.1f".format(nonGeneric * 100.0 / typedVars.size)}%)")
        println()

        // Type breakdown
        println("TYPE BREAKDOWN:")
        val typeBreakdown = typedVars.groupBy { it.inferredType::class.simpleName }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }

        typeBreakdown.forEach { (typeName, count) ->
            val percentage = count * 100.0 / typedVars.size
            println("  ${typeName?.padEnd(20)}: $count (${"%.1f".format(percentage)}%)")
        }
        println()

        // Show most-used variables (by usage count)
        println("🔥 MOST FREQUENTLY USED VARIABLES")
        println("-" * 80)
        val topUsed = typedVars.sortedByDescending { it.usageCount }.take(15)
        topUsed.forEach { tv ->
            println("${tv.variable.toString().padEnd(25)}: ${tv.usageCount.toString().padStart(4)} uses, " +
                "type: ${tv.inferredType}, confidence: ${"%.1f".format(tv.confidence * 100)}%")
        }
        println()

        println("=" * 80)
        println("✅ Memory layout analysis complete!")
        println("=" * 80)
    }

    private operator fun String.times(count: Int): String = this.repeat(count)
}
