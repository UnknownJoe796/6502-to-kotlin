package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import java.io.File

/**
 * Demonstration of Phase 4 structural analysis on real Super Mario Bros assembly code
 * This test shows what we can analyze through Pass 17 (Goto Elimination)
 */
class Phase4AnalysisDemo {

    // Create output file
    private val outputFile = File("outputs/phase4-analysis-output.txt")
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
        log("PHASE 4 STRUCTURAL ANALYSIS DEMO")
        log("Analyzing Super Mario Bros Disassembly")
        log("=" * 80)
        log("")

        // Parse and run all passes through Phase 4
        val lines = assembly.parseToAssemblyCodeFile()
        val resolution = lines.resolveAddresses(0x8000)
        val entries = lines.discoverEntryPoints(resolution)
        val reachability = lines.analyzeReachability(resolution, entries)
        val blocks = lines.constructBasicBlocks(resolution, reachability, entries)
        val cfg = lines.constructCfg(resolution, reachability, blocks, entries)
        val dominators = lines.constructDominatorTrees(cfg)
        val loops = lines.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val conditionals = lines.detectConditionals(resolution, entries, reachability, blocks, cfg, dominators, loops)
        val regions = lines.formRegions(resolution, entries, reachability, blocks, cfg, dominators, loops, conditionals)
        val gotoElim = lines.eliminateGotos(resolution, entries, reachability, blocks, cfg, dominators, loops, conditionals, regions)

        log("📊 OVERALL STATISTICS")
        log("-" * 80)
        log("Total Assembly Lines: ${lines.lines.size}")
        log("Total Functions: ${cfg.functions.size}")
        log("Total Basic Blocks: ${blocks.blocks.size}")
        log("Total CFG Edges: ${cfg.functions.sumOf { it.edges.size }}")
        log()

        // Analyze first few interesting functions
        val interestingFunctionNames = setOf(
            "ProcessAreaData",
            "DecodeAreaData"
        )

        cfg.functions.forEachIndexed { index, function ->
            if (function.entryLabel in interestingFunctionNames) {
                analyzeFunctionInDetail(
                    lines,
                    resolution,
                    blocks,
                    function,
                    dominators.functions[index],
                    loops.functions[index],
                    conditionals.functions[index],
                    regions.functions[index],
                    gotoElim.functions[index]
                )
            }
        }

        // Summary statistics
        log()
        log("🎯 PHASE 4 SUMMARY STATISTICS")
        log("=" * 80)

        val totalLoops = loops.functions.sumOf { it.loops.size }
        val totalConditionals = conditionals.functions.sumOf { it.conditionals.size }
        val totalRegions = regions.functions.sumOf { it.allRegions.size }
        val fullyStructured = gotoElim.functions.count { it.isFullyStructured }

        log("Total Loops Detected: $totalLoops")
        log("  - While loops: ${loops.functions.sumOf { f -> f.loops.count { it.loopType == LoopType.WHILE } }}")
        log("  - Do-while loops: ${loops.functions.sumOf { f -> f.loops.count { it.loopType == LoopType.DO_WHILE } }}")
        log("  - Infinite loops: ${loops.functions.sumOf { f -> f.loops.count { it.loopType == LoopType.INFINITE } }}")
        log("  - Multi-exit loops: ${loops.functions.sumOf { f -> f.loops.count { it.loopType == LoopType.MULTI_EXIT } }}")
        log()

        log("Total Conditionals Detected: $totalConditionals")
        log("  - Simple IF: ${conditionals.functions.sumOf { f -> f.conditionals.count { it.type == ConditionalType.IF } }}")
        log("  - IF-ELSE: ${conditionals.functions.sumOf { f -> f.conditionals.count { it.type == ConditionalType.IF_ELSE } }}")
        log("  - IF-ELSE-IF: ${conditionals.functions.sumOf { f -> f.conditionals.count { it.type == ConditionalType.IF_ELSE_IF } }}")
        log()

        log("Total Regions Formed: $totalRegions")
        log("  - Block regions: ${regions.functions.sumOf { it.allRegions.count { r -> r is Region.Block } }}")
        log("  - Loop regions: ${regions.functions.sumOf { it.allRegions.count { r -> r is Region.Loop } }}")
        log("  - Conditional regions: ${regions.functions.sumOf { it.allRegions.count { r -> r is Region.IfThenElse } }}")
        log("  - Sequence regions: ${regions.functions.sumOf { it.allRegions.count { r -> r is Region.Sequence } }}")
        log()

        log("Goto Elimination:")
        log("  - Fully structured functions: $fullyStructured / ${gotoElim.functions.size}")
        log("  - Functions with remaining gotos: ${gotoElim.functionsWithGotos.size}")

        val totalStats = gotoElim.functions.map { it.getStatistics() }
            .fold(mutableMapOf<String, Int>()) { acc, map ->
                map.forEach { (k, v) -> acc[k] = (acc[k] ?: 0) + v }
                acc
            }

        log()
        log("Structured Control Flow Statistics:")
        log("  - If statements: ${totalStats["ifs"]}")
        log("  - Loop statements: ${totalStats["loops"]}")
        log("  - Break statements: ${totalStats["breaks"]}")
        log("  - Continue statements: ${totalStats["continues"]}")
        log("  - Return statements: ${totalStats["returns"]}")
        log("  - Block statements: ${totalStats["blocks"]}")
        log("  - Remaining gotos: ${totalStats["gotos"]}")
        log("")
        log("=" * 80)

        // Write output to file
        outputFile.writeText(output.toString())
        println("\n\n✅ Analysis complete! Output written to: ${outputFile.absolutePath}")
        println("Total size: ${output.length} characters\n")
    }

    private fun analyzeFunctionInDetail(
        lines: AssemblyCodeFile,
        resolution: AddressResolution,
        blocks: BasicBlockConstruction,
        function: FunctionCfg,
        dominator: DominatorAnalysis,
        loopInfo: FunctionLoopInfo,
        conditionalInfo: FunctionConditionalInfo,
        regionInfo: FunctionRegionInfo,
        gotoElimInfo: FunctionStructuredCode
    ) {
        log()
        log("🔍 FUNCTION ANALYSIS: ${function.entryLabel ?: "UNNAMED"}")
        log("=" * 80)
        log("Entry Address: 0x${function.entryLeader.toString(16).uppercase().padStart(4, '0')}")
        log("Basic Blocks: ${function.blocks.size}")
        log("CFG Edges: ${function.edges.size}")
        log()

        // Print annotated assembly code for this function
        printAnnotatedAssembly(lines, resolution, blocks, function, dominator, loopInfo, conditionalInfo)
        log()

        // Dominator Analysis
        log("📐 Dominator Tree:")
        log("  Back Edges: ${dominator.backEdges.size}")
        if (dominator.backEdges.isNotEmpty()) {
            dominator.backEdges.take(3).forEach { (from, to) ->
                log("    0x${from.toString(16).uppercase()} -> 0x${to.toString(16).uppercase()}")
            }
            if (dominator.backEdges.size > 3) {
                log("    ... and ${dominator.backEdges.size - 3} more")
            }
        }


        // Loop Detection
        if (loopInfo.loops.isNotEmpty()) {
            log("🔄 Loops Detected: ${loopInfo.loops.size}")
            loopInfo.loops.take(3).forEach { loop ->
                log("  Loop at 0x${loop.header.toString(16).uppercase()}:")
                log("    Type: ${loop.loopType}")
                log("    Body size: ${loop.body.size} blocks")
                log("    Exits: ${loop.exits.size}")
                log("    Nesting depth: ${loop.nestingDepth}")
                if (loop.parentLoop != null) {
                    log("    Parent loop: 0x${loop.parentLoop.header.toString(16).uppercase()}")
                }
            }
            if (loopInfo.loops.size > 3) {
                log("  ... and ${loopInfo.loops.size - 3} more loops")
            }
            log()
        }

        // Conditional Detection
        if (conditionalInfo.conditionals.isNotEmpty()) {
            log("🔀 Conditionals Detected: ${conditionalInfo.conditionals.size}")
            conditionalInfo.conditionals.take(3).forEach { cond ->
                log("  Conditional at 0x${cond.header.toString(16).uppercase()}:")
                log("    Type: ${cond.type}")
                log("    Then branch: ${cond.thenBranch.size} blocks")
                log("    Else branch: ${cond.elseBranch.size} blocks")
                log("    Merge point: ${cond.mergePoint?.let { "0x${it.toString(16).uppercase()}" } ?: "none"}")
                log("    Nesting depth: ${cond.nestingDepth}")
            }
            if (conditionalInfo.conditionals.size > 3) {
                log("  ... and ${conditionalInfo.conditionals.size - 3} more conditionals")
            }
            log()
        }

        // Region Formation
        log("🏗️  Region Structure:")
        log("  Total regions: ${regionInfo.allRegions.size}")
        log("  Root region depth: ${regionInfo.rootRegion.getDepth()}")
        log()
        log("  Region tree (first 20 lines):")
        val regionTree = regionInfo.rootRegion.toPrettyString("    ")
        regionTree.lines().take(20).forEach { log(it) }
        if (regionTree.lines().size > 20) {
            log("    ... (${regionTree.lines().size - 20} more lines)")
        }
        log()

        // Goto Elimination
        log("✨ Structured Code:")
        val stats = gotoElimInfo.getStatistics()
        log("  Fully structured: ${if (gotoElimInfo.isFullyStructured) "YES ✓" else "NO (${gotoElimInfo.remainingGotos.size} gotos)"}")
        log("  Control flow breakdown:")
        log("    If statements: ${stats["ifs"]}")
        log("    Loops: ${stats["loops"]}")
        log("    Breaks: ${stats["breaks"]}")
        log("    Continues: ${stats["continues"]}")
        log("    Returns: ${stats["returns"]}")
        log("    Block statements: ${stats["blocks"]}")
        if (stats["gotos"]!! > 0) {
            log("    Remaining gotos: ${stats["gotos"]}")
        }
        log()
        log("-" * 80)
    }

    private fun printAnnotatedAssembly(
        lines: AssemblyCodeFile,
        resolution: AddressResolution,
        blocks: BasicBlockConstruction,
        function: FunctionCfg,
        dominator: DominatorAnalysis,
        loopInfo: FunctionLoopInfo,
        conditionalInfo: FunctionConditionalInfo
    ) {
        log("📝 ANNOTATED ASSEMBLY CODE:")
        log("-" * 80)

        // Build address-to-line-index map
        val addressToLineIndex = resolution.resolved
            .mapIndexedNotNull { index, resolvedLine ->
                resolvedLine.address?.let { it to index }
            }
            .toMap()

        // Find all line indices for this function
        val functionLineIndices = function.blocks
            .flatMap { block -> block.lineIndexes }
            .toSet()

        // Get all addresses in this function, sorted
        val functionAddresses = function.blocks.flatMap { block ->
            block.lineIndexes.mapNotNull { lineIdx ->
                resolution.resolved.getOrNull(lineIdx)?.address
            }
        }.distinct().sorted()

        // Build annotation maps
        val blockStarts = mutableMapOf<Int, Int>() // address -> block start address
        val loopHeaderAddrs = mutableSetOf<Int>()
        val conditionalHeaderAddrs = mutableSetOf<Int>()
        val backEdgeTargetAddrs = mutableSetOf<Int>()

        // Build leader index to address map
        val leaderToAddress = function.blocks.associate { block ->
            block.leaderIndex to block.startAddress
        }

        function.blocks.forEach { block ->
            block.lineIndexes.forEach { lineIdx ->
                resolution.resolved.getOrNull(lineIdx)?.address?.let { addr ->
                    blockStarts[addr] = block.startAddress
                }
            }
        }

        loopInfo.loops.forEach { loop ->
            leaderToAddress[loop.header]?.let { loopHeaderAddrs.add(it) }
        }

        conditionalInfo.conditionals.forEach { cond ->
            leaderToAddress[cond.header]?.let { conditionalHeaderAddrs.add(it) }
        }

        dominator.backEdges.forEach { (_, target) ->
            leaderToAddress[target]?.let { backEdgeTargetAddrs.add(it) }
        }

        // Print each line with annotations
        functionAddresses.forEach { addr ->
            val lineIdx = addressToLineIndex[addr] ?: return@forEach
            val line = lines.lines.getOrNull(lineIdx) ?: return@forEach
            val annotations = mutableListOf<String>()

            // Check if this is a block start
            if (blockStarts[addr] == addr) {
                annotations.add("BB_START")
            }

            // Check if this is a loop header
            if (loopHeaderAddrs.contains(addr)) {
                val loop = loopInfo.loops.find { leaderToAddress[it.header] == addr }
                if (loop != null) {
                    annotations.add("LOOP_${loop.loopType}")
                }
            }

            // Check if this is a conditional header
            if (conditionalHeaderAddrs.contains(addr)) {
                val cond = conditionalInfo.conditionals.find { leaderToAddress[it.header] == addr }
                if (cond != null) {
                    annotations.add("COND_${cond.type}")
                }
            }

            // Check if this is a back edge target
            if (backEdgeTargetAddrs.contains(addr)) {
                annotations.add("BACK_EDGE_TARGET")
            }

            // Format the line
            val addrStr = "0x${addr.toString(16).uppercase().padStart(4, '0')}"
            val labelStr = (line.label?.let { "$it:" } ?: "").padEnd(24)
            val instrStr = buildString {
                append("  ")
                if (line.instruction != null) {
                    append(line.instruction.toString())
                } else if (line.data != null) {
                    append(line.data.toString())
                }
            }.padEnd(32)

            val annotationStr = if (annotations.isNotEmpty()) {
                "  ; [${annotations.joinToString(", ")}]"
            } else {
                line.comment?.let { "  ; $it" } ?: ""
            }

            log("  $addrStr  $labelStr$instrStr$annotationStr")
        }

        log("-" * 80)
    }

    private operator fun String.times(count: Int): String = this.repeat(count)
}
