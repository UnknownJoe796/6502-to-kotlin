package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 16: Region Formation
 * - Build hierarchical regions from loops and conditionals
 * - Create abstract syntax tree (AST)-like structure
 * - Support for proper nesting and scope
 * - Foundation for goto elimination
 */

/**
 * Types of regions in the structural analysis
 */
sealed class Region {
    abstract val blocks: Set<Int>
    abstract val entry: Int
    abstract val exits: Set<Int>
    abstract val children: List<Region>

    /** Sequential block region (straight-line code) */
    data class Block(
        override val entry: Int,
        override val blocks: Set<Int> = setOf(entry),
        override val exits: Set<Int> = setOf(entry),
        override val children: List<Region> = emptyList()
    ) : Region()

    /** Loop region */
    data class Loop(
        val loop: NaturalLoop,
        override val entry: Int,
        override val blocks: Set<Int>,
        override val exits: Set<Int>,
        val body: Region,
        override val children: List<Region>
    ) : Region()

    /** Conditional region (if/if-else) */
    data class IfThenElse(
        val conditional: Conditional,
        override val entry: Int,
        override val blocks: Set<Int>,
        override val exits: Set<Int>,
        val thenRegion: Region,
        val elseRegion: Region?,
        override val children: List<Region>
    ) : Region()

    /** Sequence of regions */
    data class Sequence(
        override val entry: Int,
        override val blocks: Set<Int>,
        override val exits: Set<Int>,
        override val children: List<Region>
    ) : Region()

    /** Function body region */
    data class Function(
        val function: FunctionCfg,
        override val entry: Int,
        override val blocks: Set<Int>,
        override val exits: Set<Int>,
        val body: Region,
        override val children: List<Region>
    ) : Region()
}

/**
 * Complete region formation result for a function
 */
data class FunctionRegionInfo(
    /** The function being analyzed */
    val function: FunctionCfg,
    /** Root region (function body) */
    val rootRegion: Region.Function,
    /** All regions in the function (flattened) */
    val allRegions: List<Region>,
    /** Map from block to smallest region containing it */
    val blockToRegion: Map<Int, Region>
)

/**
 * Complete region formation result
 */
data class RegionFormation(
    /** Region information for each function */
    val functions: List<FunctionRegionInfo>
)

/**
 * Form regions from loops and conditionals
 */
fun AssemblyCodeFile.formRegions(
    resolution: AddressResolution = this.resolveAddresses(),
    entries: EntryPointDiscovery = this.discoverEntryPoints(resolution),
    reachability: ReachabilityReport = this.analyzeReachability(resolution, entries),
    blocks: BasicBlockConstruction = this.constructBasicBlocks(resolution, reachability, entries),
    cfg: CfgConstruction = this.constructCfg(resolution, reachability, blocks, entries),
    dominators: DominatorConstruction = this.constructDominatorTrees(cfg),
    loops: LoopDetection = this.detectLoops(resolution, entries, reachability, blocks, cfg, dominators),
    conditionals: ConditionalDetection = this.detectConditionals(resolution, entries, reachability, blocks, cfg, dominators, loops)
): RegionFormation {
    val functionRegionInfo = cfg.functions.mapIndexed { index, function ->
        val loopInfo = loops.functions[index]
        val conditionalInfo = conditionals.functions[index]
        formFunctionRegions(function, loopInfo, conditionalInfo)
    }

    return RegionFormation(functions = functionRegionInfo)
}

/**
 * Form regions for a single function
 */
private fun formFunctionRegions(
    function: FunctionCfg,
    loopInfo: FunctionLoopInfo,
    conditionalInfo: FunctionConditionalInfo
): FunctionRegionInfo {
    // Build regions bottom-up: blocks -> conditionals/loops -> sequences -> function

    val blockRegions = function.blocks.associate { block ->
        block.leaderIndex to Region.Block(entry = block.leaderIndex)
    }

    // Build compound regions by processing loops and conditionals
    val allRegions = mutableListOf<Region>()
    allRegions.addAll(blockRegions.values)

    // Process top-level loops (outermost first)
    val loopRegions = loopInfo.topLevelLoops.map { loop ->
        buildLoopRegion(loop, loopInfo, conditionalInfo, blockRegions)
    }
    allRegions.addAll(loopRegions)

    // Process top-level conditionals
    val conditionalRegions = conditionalInfo.topLevelConditionals
        .filter { cond ->
            // Exclude conditionals that are part of loops
            !loopInfo.loops.any { loop -> cond.header in loop.body }
        }
        .map { cond ->
            buildConditionalRegion(cond, conditionalInfo, blockRegions)
        }
    allRegions.addAll(conditionalRegions)

    // Build sequence for function body
    val bodyRegion = buildSequenceRegion(
        function.entryLeader,
        function.blocks.map { it.leaderIndex }.toSet(),
        loopRegions + conditionalRegions + blockRegions.values.toList()
    )

    // Create function region
    val functionRegion = Region.Function(
        function = function,
        entry = function.entryLeader,
        blocks = function.blocks.map { it.leaderIndex }.toSet(),
        exits = setOf(), // Function exits handled separately
        body = bodyRegion,
        children = listOf(bodyRegion)
    )

    allRegions.add(functionRegion)

    // Build block to region map
    val blockToRegion = mutableMapOf<Int, Region>()
    fun mapBlocks(region: Region) {
        region.blocks.forEach { block ->
            // Keep smallest (most nested) region
            if (block !in blockToRegion) {
                blockToRegion[block] = region
            }
        }
        region.children.forEach { mapBlocks(it) }
    }
    mapBlocks(functionRegion)

    return FunctionRegionInfo(
        function = function,
        rootRegion = functionRegion,
        allRegions = allRegions,
        blockToRegion = blockToRegion
    )
}

/**
 * Build a loop region
 */
private fun buildLoopRegion(
    loop: NaturalLoop,
    loopInfo: FunctionLoopInfo,
    conditionalInfo: FunctionConditionalInfo,
    blockRegions: Map<Int, Region.Block>
): Region.Loop {
    // Build body region from child loops, conditionals, and blocks
    val childRegions = mutableListOf<Region>()

    // Add child loops
    loop.childLoops.forEach { childLoop ->
        childRegions.add(buildLoopRegion(childLoop, loopInfo, conditionalInfo, blockRegions))
    }

    // Add conditionals in this loop
    conditionalInfo.conditionals
        .filter { cond -> cond.header in loop.body }
        .filter { cond -> cond.parentConditional == null || cond.parentConditional.header !in loop.body }
        .forEach { cond ->
            childRegions.add(buildConditionalRegion(cond, conditionalInfo, blockRegions))
        }

    // Add remaining blocks
    loop.body.forEach { block ->
        if (childRegions.none { block in it.blocks }) {
            blockRegions[block]?.let { childRegions.add(it) }
        }
    }

    val bodyRegion = buildSequenceRegion(loop.header, loop.body, childRegions)

    return Region.Loop(
        loop = loop,
        entry = loop.header,
        blocks = loop.body,
        exits = loop.exits,
        body = bodyRegion,
        children = listOf(bodyRegion)
    )
}

/**
 * Build a conditional region
 */
private fun buildConditionalRegion(
    conditional: Conditional,
    conditionalInfo: FunctionConditionalInfo,
    blockRegions: Map<Int, Region.Block>
): Region.IfThenElse {
    // Build then region
    val thenChildRegions = mutableListOf<Region>()
    conditional.thenBranch.forEach { block ->
        blockRegions[block]?.let { thenChildRegions.add(it) }
    }
    val thenRegion = if (thenChildRegions.size == 1) {
        thenChildRegions.first()
    } else {
        buildSequenceRegion(
            conditional.thenBranch.minOrNull() ?: conditional.header,
            conditional.thenBranch,
            thenChildRegions
        )
    }

    // Build else region (if exists)
    val elseRegion = if (conditional.elseBranch.isNotEmpty()) {
        val elseChildRegions = mutableListOf<Region>()
        conditional.elseBranch.forEach { block ->
            blockRegions[block]?.let { elseChildRegions.add(it) }
        }
        if (elseChildRegions.size == 1) {
            elseChildRegions.first()
        } else {
            buildSequenceRegion(
                conditional.elseBranch.minOrNull() ?: conditional.header,
                conditional.elseBranch,
                elseChildRegions
            )
        }
    } else {
        null
    }

    val allBlocks = setOf(conditional.header) + conditional.thenBranch + conditional.elseBranch
    val allExits = if (conditional.mergePoint != null) setOf(conditional.mergePoint) else conditional.thenBranch + conditional.elseBranch

    return Region.IfThenElse(
        conditional = conditional,
        entry = conditional.header,
        blocks = allBlocks,
        exits = allExits,
        thenRegion = thenRegion,
        elseRegion = elseRegion,
        children = listOfNotNull(thenRegion, elseRegion)
    )
}

/**
 * Build a sequence region
 */
private fun buildSequenceRegion(
    entry: Int,
    blocks: Set<Int>,
    childRegions: List<Region>
): Region {
    // If only one child, return it directly
    if (childRegions.size == 1) {
        return childRegions.first()
    }

    // Sort children by entry point
    val sorted = childRegions.sortedBy { it.entry }

    val allBlocks = sorted.flatMap { it.blocks }.toSet()
    val allExits = sorted.flatMap { it.exits }.toSet()

    return Region.Sequence(
        entry = entry,
        blocks = allBlocks,
        exits = allExits,
        children = sorted
    )
}

/**
 * Helper to get region depth
 */
fun Region.getDepth(): Int {
    return when (this) {
        is Region.Block -> 0
        is Region.Loop -> 1 + body.getDepth()
        is Region.IfThenElse -> 1 + maxOf(thenRegion.getDepth(), elseRegion?.getDepth() ?: 0)
        is Region.Sequence -> 1 + (children.maxOfOrNull { it.getDepth() } ?: 0)
        is Region.Function -> 1 + body.getDepth()
    }
}

/**
 * Helper to print region tree
 */
fun Region.toPrettyString(indent: String = ""): String {
    return when (this) {
        is Region.Block -> "${indent}Block($entry)"
        is Region.Loop -> "${indent}Loop(header=$entry, type=${loop.loopType})\n${body.toPrettyString(indent + "  ")}"
        is Region.IfThenElse -> {
            val elseStr = elseRegion?.let { "\n${indent}Else:\n${it.toPrettyString(indent + "  ")}" } ?: ""
            "${indent}If(header=$entry, type=${conditional.type})\n${indent}Then:\n${thenRegion.toPrettyString(indent + "  ")}$elseStr"
        }
        is Region.Sequence -> "${indent}Sequence(entry=$entry)\n${children.joinToString("\n") { it.toPrettyString(indent + "  ") }}"
        is Region.Function -> "${indent}Function(entry=$entry)\n${body.toPrettyString(indent + "  ")}"
    }
}
