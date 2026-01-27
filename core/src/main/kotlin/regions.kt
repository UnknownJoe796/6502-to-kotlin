package com.ivieleague.decompiler6502tokotlin.hand

// by Claude - Region data structures for principled control flow analysis
// Regions are single-entry subgraphs that represent structured control flow patterns

/**
 * A Region is a single-entry subgraph of the CFG that represents a structured
 * control flow pattern. Regions can be nested to form a hierarchy.
 *
 * Key property: Every region has exactly ONE entry point (the entry block).
 * This is essential for structured programming - you can only enter a construct
 * from one place.
 */
sealed class Region {
    /** The entry block where control enters this region */
    abstract val entry: AssemblyBlock

    /** All basic blocks contained within this region (including nested regions) */
    abstract val blocks: Set<AssemblyBlock>

    /** The kind of structured pattern this region represents */
    abstract val kind: RegionKind

    /** Blocks where control can exit this region (successors after execution) */
    abstract val exits: Set<AssemblyBlock>

    /** Unique identifier for debugging/tracing */
    abstract val id: Int

    /** Diagnostic information about unstructured edges within this region */
    open val unstructuredEdges: List<UnstructuredEdgeInfo> = emptyList()
}

/**
 * Classification of region patterns.
 */
enum class RegionKind {
    /** Single basic block - the atomic unit */
    BLOCK,
    /** Linear sequence of regions */
    SEQUENCE,
    /** if (cond) { then } */
    IF_THEN,
    /** if (cond) { then } else { else } */
    IF_THEN_ELSE,
    /** while (cond) { body } - condition at start */
    WHILE_LOOP,
    /** do { body } while (cond) - condition at end */
    DO_WHILE_LOOP,
    /** loop { body } - no exit condition (infinite loop) */
    INFINITE_LOOP,
    /** JumpEngine dispatch (switch/case) */
    SWITCH,
    /** Contains irreducible flow - will emit goto with diagnostics */
    UNSTRUCTURED
}

/**
 * Information about an unstructured edge that couldn't be represented
 * by a structured construct.
 */
data class UnstructuredEdgeInfo(
    val source: AssemblyBlock,
    val target: AssemblyBlock,
    val reason: String,
    val lineIndex: Int = source.originalLineIndex
) {
    override fun toString(): String {
        val srcLabel = source.label ?: "line ${source.originalLineIndex}"
        val tgtLabel = target.label ?: "line ${target.originalLineIndex}"
        return "Line $lineIndex: $srcLabel -> $tgtLabel ($reason)"
    }
}

// ============================================================================
// Concrete Region Types
// ============================================================================

/**
 * BlockRegion: Wraps a single basic block. The atomic unit of regions.
 */
data class BlockRegion(
    override val id: Int,
    val block: AssemblyBlock
) : Region() {
    override val entry: AssemblyBlock = block
    override val blocks: Set<AssemblyBlock> = setOf(block)
    override val kind: RegionKind = RegionKind.BLOCK
    override val exits: Set<AssemblyBlock> = buildSet {
        block.fallThroughExit?.let { add(it) }
        block.branchExit?.let { add(it) }
    }.filter { it !in blocks }.toSet()
}

/**
 * SequenceRegion: Linear sequence of regions executed in order.
 */
data class SequenceRegion(
    override val id: Int,
    val children: List<Region>
) : Region() {
    init {
        require(children.isNotEmpty()) { "SequenceRegion must have at least one child" }
    }

    override val entry: AssemblyBlock = children.first().entry
    override val blocks: Set<AssemblyBlock> = children.flatMapTo(mutableSetOf()) { it.blocks }
    override val kind: RegionKind = RegionKind.SEQUENCE
    override val exits: Set<AssemblyBlock> = children.last().exits.filter { it !in blocks }.toSet()
    override val unstructuredEdges: List<UnstructuredEdgeInfo> =
        children.flatMap { it.unstructuredEdges }
}

/**
 * IfThenRegion: Conditional with only a then-branch.
 *
 * Pattern:
 *   condition_block: branch to join if !cond
 *   then_region
 *   join_block (optional, may exit function)
 */
data class IfThenRegion(
    override val id: Int,
    val conditionBlock: AssemblyBlock,
    val conditionLine: AssemblyLine,
    /** true = branch-taken is the then-path, false = fall-through is the then-path */
    val sense: Boolean,
    val thenRegion: Region,
    val joinBlock: AssemblyBlock?
) : Region() {
    override val entry: AssemblyBlock = conditionBlock
    override val blocks: Set<AssemblyBlock> = buildSet {
        add(conditionBlock)
        addAll(thenRegion.blocks)
    }
    override val kind: RegionKind = RegionKind.IF_THEN
    override val exits: Set<AssemblyBlock> = when {
        joinBlock != null -> setOf(joinBlock)
        else -> thenRegion.exits.filter { it !in blocks }.toSet()
    }
    override val unstructuredEdges: List<UnstructuredEdgeInfo> = thenRegion.unstructuredEdges
}

/**
 * IfThenElseRegion: Conditional with both then and else branches.
 *
 * Pattern:
 *   condition_block: branch to else if !cond
 *   then_region
 *   [jmp to join]
 *   else_region
 *   join_block
 */
data class IfThenElseRegion(
    override val id: Int,
    val conditionBlock: AssemblyBlock,
    val conditionLine: AssemblyLine,
    /** true = branch-taken is the then-path, false = fall-through is the then-path */
    val sense: Boolean,
    val thenRegion: Region,
    val elseRegion: Region,
    val joinBlock: AssemblyBlock?
) : Region() {
    override val entry: AssemblyBlock = conditionBlock
    override val blocks: Set<AssemblyBlock> = buildSet {
        add(conditionBlock)
        addAll(thenRegion.blocks)
        addAll(elseRegion.blocks)
    }
    override val kind: RegionKind = RegionKind.IF_THEN_ELSE
    override val exits: Set<AssemblyBlock> = when {
        joinBlock != null -> setOf(joinBlock)
        else -> (thenRegion.exits + elseRegion.exits).filter { it !in blocks }.toSet()
    }
    override val unstructuredEdges: List<UnstructuredEdgeInfo> =
        thenRegion.unstructuredEdges + elseRegion.unstructuredEdges
}

/**
 * WhileLoopRegion: Pre-test loop (while).
 *
 * Pattern:
 *   header: test condition, branch to exit if false
 *   body_region
 *   [jmp to header]
 *   exit_block
 */
data class WhileLoopRegion(
    override val id: Int,
    val header: AssemblyBlock,
    val conditionLine: AssemblyLine,
    /** true = branch-taken continues loop, false = fall-through continues loop */
    val continueOnBranch: Boolean,
    val bodyRegion: Region,
    val exitBlock: AssemblyBlock?
) : Region() {
    override val entry: AssemblyBlock = header
    override val blocks: Set<AssemblyBlock> = buildSet {
        add(header)
        addAll(bodyRegion.blocks)
    }
    override val kind: RegionKind = RegionKind.WHILE_LOOP
    override val exits: Set<AssemblyBlock> = exitBlock?.let { setOf(it) } ?: emptySet()
    override val unstructuredEdges: List<UnstructuredEdgeInfo> = bodyRegion.unstructuredEdges
}

/**
 * DoWhileLoopRegion: Post-test loop (do-while).
 *
 * Pattern:
 *   header: start of body
 *   body_region
 *   condition_block: test condition, branch to header if true
 *   exit (fall-through)
 */
data class DoWhileLoopRegion(
    override val id: Int,
    val header: AssemblyBlock,
    val conditionBlock: AssemblyBlock,
    val conditionLine: AssemblyLine,
    /** true = branch-taken continues loop, false = fall-through continues loop */
    val continueOnBranch: Boolean,
    val bodyRegion: Region,
    val exitBlock: AssemblyBlock?
) : Region() {
    override val entry: AssemblyBlock = header
    override val blocks: Set<AssemblyBlock> = buildSet {
        add(header)
        addAll(bodyRegion.blocks)
        add(conditionBlock)
    }
    override val kind: RegionKind = RegionKind.DO_WHILE_LOOP
    override val exits: Set<AssemblyBlock> = exitBlock?.let { setOf(it) } ?: emptySet()
    override val unstructuredEdges: List<UnstructuredEdgeInfo> = bodyRegion.unstructuredEdges
}

/**
 * InfiniteLoopRegion: Loop with no exit condition.
 *
 * Pattern:
 *   header:
 *   body_region
 *   jmp header
 */
data class InfiniteLoopRegion(
    override val id: Int,
    val header: AssemblyBlock,
    val bodyRegion: Region
) : Region() {
    override val entry: AssemblyBlock = header
    override val blocks: Set<AssemblyBlock> = buildSet {
        add(header)
        addAll(bodyRegion.blocks)
    }
    override val kind: RegionKind = RegionKind.INFINITE_LOOP
    override val exits: Set<AssemblyBlock> = emptySet() // No normal exit
    override val unstructuredEdges: List<UnstructuredEdgeInfo> = bodyRegion.unstructuredEdges
}

/**
 * SwitchRegion: JumpEngine dispatch table.
 */
data class SwitchRegion(
    override val id: Int,
    val dispatchBlock: AssemblyBlock,
    val selectorExpr: ValueExpr?,
    val cases: List<SwitchCase>,
    val defaultRegion: Region?,
    val joinBlock: AssemblyBlock?
) : Region() {
    data class SwitchCase(
        val indices: List<Int>, // which case values map to this
        val targetLabel: String,
        val region: Region?
    )

    override val entry: AssemblyBlock = dispatchBlock
    override val blocks: Set<AssemblyBlock> = buildSet {
        add(dispatchBlock)
        cases.forEach { it.region?.let { r -> addAll(r.blocks) } }
        defaultRegion?.let { addAll(it.blocks) }
    }
    override val kind: RegionKind = RegionKind.SWITCH
    override val exits: Set<AssemblyBlock> = when {
        joinBlock != null -> setOf(joinBlock)
        else -> {
            val caseExits = cases.mapNotNull { it.region?.exits }.flatten()
            val defaultExits = defaultRegion?.exits ?: emptySet()
            (caseExits + defaultExits).filter { it !in blocks }.toSet()
        }
    }
    override val unstructuredEdges: List<UnstructuredEdgeInfo> = buildList {
        cases.forEach { it.region?.unstructuredEdges?.let { e -> addAll(e) } }
        defaultRegion?.unstructuredEdges?.let { addAll(it) }
    }
}

/**
 * UnstructuredRegion: Contains irreducible control flow that couldn't be
 * structured. Will emit goto statements with diagnostics.
 *
 * This is the "escape hatch" - when the algorithm can't find a structured
 * pattern, it creates an UnstructuredRegion and records WHY.
 */
data class UnstructuredRegion(
    override val id: Int,
    override val entry: AssemblyBlock,
    override val blocks: Set<AssemblyBlock>,
    override val unstructuredEdges: List<UnstructuredEdgeInfo>,
    val childRegions: List<Region> = emptyList()
) : Region() {
    override val kind: RegionKind = RegionKind.UNSTRUCTURED
    override val exits: Set<AssemblyBlock> = buildSet {
        for (block in blocks) {
            block.fallThroughExit?.let { if (it !in blocks) add(it) }
            block.branchExit?.let { if (it !in blocks) add(it) }
        }
    }

    fun getDiagnosticMessage(): String = buildString {
        val funcLabel = entry.label ?: "line ${entry.originalLineIndex}"
        appendLine("UNSTRUCTURED CONTROL FLOW in $funcLabel")
        appendLine("Blocks: ${blocks.size}")
        appendLine("Unstructured edges (${unstructuredEdges.size}):")
        for (edge in unstructuredEdges) {
            appendLine("  - $edge")
        }
    }
}

// ============================================================================
// Region Builder Helpers
// ============================================================================

private var globalRegionId = 0

/**
 * Get the next unique region ID.
 */
fun nextRegionId(): Int = globalRegionId++

/**
 * Reset the region ID counter (for testing).
 */
fun resetRegionIds() {
    globalRegionId = 0
}

/**
 * Collect all unstructured edges from a region tree.
 */
fun Region.getAllUnstructuredEdges(): List<UnstructuredEdgeInfo> {
    val result = mutableListOf<UnstructuredEdgeInfo>()
    result.addAll(unstructuredEdges)
    when (this) {
        is SequenceRegion -> children.forEach { result.addAll(it.getAllUnstructuredEdges()) }
        is IfThenRegion -> result.addAll(thenRegion.getAllUnstructuredEdges())
        is IfThenElseRegion -> {
            result.addAll(thenRegion.getAllUnstructuredEdges())
            result.addAll(elseRegion.getAllUnstructuredEdges())
        }
        is WhileLoopRegion -> result.addAll(bodyRegion.getAllUnstructuredEdges())
        is DoWhileLoopRegion -> result.addAll(bodyRegion.getAllUnstructuredEdges())
        is InfiniteLoopRegion -> result.addAll(bodyRegion.getAllUnstructuredEdges())
        is SwitchRegion -> {
            cases.forEach { it.region?.let { r -> result.addAll(r.getAllUnstructuredEdges()) } }
            defaultRegion?.let { result.addAll(it.getAllUnstructuredEdges()) }
        }
        is UnstructuredRegion -> childRegions.forEach { result.addAll(it.getAllUnstructuredEdges()) }
        is BlockRegion -> { /* No nested regions */ }
    }
    return result
}

/**
 * Check if a region is fully structured (no unstructured edges).
 */
fun Region.isFullyStructured(): Boolean = getAllUnstructuredEdges().isEmpty()

/**
 * Get a summary of the region tree structure.
 */
fun Region.summarize(indent: Int = 0): String = buildString {
    val prefix = "  ".repeat(indent)
    val entryLabel = entry.label ?: "@${entry.originalLineIndex}"
    append("$prefix${kind}: $entryLabel (${blocks.size} blocks)")

    if (unstructuredEdges.isNotEmpty()) {
        append(" [${unstructuredEdges.size} unstructured]")
    }
    appendLine()

    when (this@summarize) {
        is SequenceRegion -> children.forEach { append(it.summarize(indent + 1)) }
        is IfThenRegion -> append(thenRegion.summarize(indent + 1))
        is IfThenElseRegion -> {
            appendLine("${prefix}  THEN:")
            append(thenRegion.summarize(indent + 2))
            appendLine("${prefix}  ELSE:")
            append(elseRegion.summarize(indent + 2))
        }
        is WhileLoopRegion -> append(bodyRegion.summarize(indent + 1))
        is DoWhileLoopRegion -> append(bodyRegion.summarize(indent + 1))
        is InfiniteLoopRegion -> append(bodyRegion.summarize(indent + 1))
        is SwitchRegion -> {
            cases.forEach { case ->
                appendLine("${prefix}  CASE ${case.indices}: ${case.targetLabel}")
                case.region?.let { append(it.summarize(indent + 2)) }
            }
            defaultRegion?.let {
                appendLine("${prefix}  DEFAULT:")
                append(it.summarize(indent + 2))
            }
        }
        is UnstructuredRegion -> childRegions.forEach { append(it.summarize(indent + 1)) }
        is BlockRegion -> { /* No children */ }
    }
}
