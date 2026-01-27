package com.ivieleague.decompiler6502tokotlin.hand

// by Claude - Edge tracking infrastructure for principled control flow analysis
// This ensures every CFG edge is accounted for - either consumed by a structured
// construct (if-then, loop, sequence) or marked as unstructured (will emit goto)

/**
 * Represents a control flow edge in the CFG.
 * An edge connects a source block to a target block, with a type indicating
 * whether it's a fall-through, branch, or back-edge.
 */
data class CfgEdge(
    val source: AssemblyBlock,
    val target: AssemblyBlock,
    val kind: EdgeKind
) {
    override fun toString(): String {
        val srcLabel = source.label ?: "@${source.originalLineIndex}"
        val tgtLabel = target.label ?: "@${target.originalLineIndex}"
        return "$srcLabel --[$kind]--> $tgtLabel"
    }
}

/**
 * The type of a CFG edge.
 */
enum class EdgeKind {
    /** Fall-through to next sequential block */
    FALL_THROUGH,
    /** Conditional or unconditional branch */
    BRANCH,
    /** Back-edge (forms a loop) - branch/fall-through that goes to an earlier block */
    BACK_EDGE
}

/**
 * Result of edge tracking validation.
 */
data class EdgeValidationResult(
    val success: Boolean,
    val consumedEdges: Set<CfgEdge>,
    val unstructuredEdges: Map<CfgEdge, String>, // edge -> reason
    val missingEdges: Set<CfgEdge>, // edges not accounted for
    val allEdges: Set<CfgEdge>
) {
    fun report(): String = buildString {
        appendLine("Edge Validation Report")
        appendLine("======================")
        appendLine("Total edges: ${allEdges.size}")
        appendLine("Consumed by structured constructs: ${consumedEdges.size}")
        appendLine("Marked as unstructured: ${unstructuredEdges.size}")
        appendLine("Missing (ERROR): ${missingEdges.size}")
        appendLine()

        if (missingEdges.isNotEmpty()) {
            appendLine("MISSING EDGES (these need to be handled):")
            for (edge in missingEdges) {
                appendLine("  - $edge")
            }
            appendLine()
        }

        if (unstructuredEdges.isNotEmpty()) {
            appendLine("UNSTRUCTURED EDGES (will emit goto):")
            for ((edge, reason) in unstructuredEdges) {
                appendLine("  - $edge: $reason")
            }
        }
    }
}

/**
 * Tracks all edges in a function's CFG and validates that every edge is accounted for.
 *
 * The core principle: for each CFG edge (source → target):
 * - EITHER it's consumed by a structured construct (if-then, loop, sequence)
 * - OR it's marked as UNSTRUCTURED and emits explicit goto
 * - NEVER silently dropped
 */
class EdgeTracker(
    private val function: AssemblyFunction,
    private val functionBlocks: Set<AssemblyBlock>
) {
    /** All edges in this function's CFG */
    val allEdges: Set<CfgEdge>

    /** Edges that have been consumed by structured constructs */
    private val consumedEdges = mutableSetOf<CfgEdge>()

    /** Edges that are marked as unstructured (will become gotos) */
    private val unstructuredEdges = mutableMapOf<CfgEdge, String>()

    /** Map from (source, target) to edge for quick lookup */
    private val edgeMap: Map<Pair<AssemblyBlock, AssemblyBlock>, CfgEdge>

    init {
        // Collect all edges from blocks in this function
        val edges = mutableSetOf<CfgEdge>()
        val layout = functionBlocks.sortedBy { it.originalLineIndex }
        val indexOf = layout.withIndex().associate { it.value to it.index }

        for (block in functionBlocks) {
            val blockIdx = indexOf[block] ?: continue

            // Fall-through edge
            block.fallThroughExit?.let { target ->
                if (target in functionBlocks) {
                    val targetIdx = indexOf[target] ?: Int.MAX_VALUE
                    val kind = if (targetIdx <= blockIdx) EdgeKind.BACK_EDGE else EdgeKind.FALL_THROUGH
                    edges.add(CfgEdge(block, target, kind))
                }
            }

            // Branch edge
            block.branchExit?.let { target ->
                if (target in functionBlocks) {
                    val targetIdx = indexOf[target] ?: Int.MAX_VALUE
                    val kind = if (targetIdx <= blockIdx) EdgeKind.BACK_EDGE else EdgeKind.BRANCH
                    edges.add(CfgEdge(block, target, kind))
                }
            }
        }

        allEdges = edges
        edgeMap = edges.associateBy { it.source to it.target }
    }

    /**
     * Get the edge between two blocks, or null if no such edge exists.
     */
    fun getEdge(source: AssemblyBlock, target: AssemblyBlock): CfgEdge? {
        return edgeMap[source to target]
    }

    /**
     * Mark an edge as consumed by a structured construct.
     * This means the edge is represented by an if-then, loop, sequence, etc.
     */
    fun consumeEdge(edge: CfgEdge) {
        if (edge in allEdges) {
            consumedEdges.add(edge)
            unstructuredEdges.remove(edge) // Remove from unstructured if it was there
        }
    }

    /**
     * Mark an edge as consumed by looking up source and target.
     */
    fun consumeEdge(source: AssemblyBlock, target: AssemblyBlock) {
        getEdge(source, target)?.let { consumeEdge(it) }
    }

    /**
     * Mark an edge as unstructured (will become goto).
     * @param edge The edge to mark
     * @param reason Diagnostic explaining why this edge couldn't be structured
     */
    fun markUnstructured(edge: CfgEdge, reason: String) {
        if (edge in allEdges && edge !in consumedEdges) {
            unstructuredEdges[edge] = reason
        }
    }

    /**
     * Mark an edge as unstructured by looking up source and target.
     */
    fun markUnstructured(source: AssemblyBlock, target: AssemblyBlock, reason: String) {
        getEdge(source, target)?.let { markUnstructured(it, reason) }
    }

    /**
     * Check if an edge has been consumed.
     */
    fun isConsumed(edge: CfgEdge): Boolean = edge in consumedEdges

    /**
     * Check if an edge has been consumed by looking up source and target.
     */
    fun isConsumed(source: AssemblyBlock, target: AssemblyBlock): Boolean {
        return getEdge(source, target)?.let { isConsumed(it) } ?: false
    }

    /**
     * Check if an edge is marked as unstructured.
     */
    fun isUnstructured(edge: CfgEdge): Boolean = edge in unstructuredEdges

    /**
     * Get all edges that are neither consumed nor marked as unstructured.
     */
    fun getMissingEdges(): Set<CfgEdge> {
        return allEdges - consumedEdges - unstructuredEdges.keys
    }

    /**
     * Validate that all edges are accounted for.
     * Returns a validation result with diagnostic information.
     *
     * THROWS EdgeValidationException if any edges are missing (not consumed or unstructured).
     */
    fun validate(): EdgeValidationResult {
        val missing = getMissingEdges()
        val result = EdgeValidationResult(
            success = missing.isEmpty(),
            consumedEdges = consumedEdges.toSet(),
            unstructuredEdges = unstructuredEdges.toMap(),
            missingEdges = missing,
            allEdges = allEdges
        )

        if (missing.isNotEmpty()) {
            val funcLabel = function.startingBlock.label ?: "@${function.startingBlock.originalLineIndex}"
            throw EdgeValidationException(
                "Function $funcLabel has ${missing.size} unaccounted edges:\n" +
                missing.joinToString("\n") { "  - $it" }
            )
        }

        return result
    }

    /**
     * Validate without throwing - returns the result for inspection.
     */
    fun validateSoft(): EdgeValidationResult {
        val missing = getMissingEdges()
        return EdgeValidationResult(
            success = missing.isEmpty(),
            consumedEdges = consumedEdges.toSet(),
            unstructuredEdges = unstructuredEdges.toMap(),
            missingEdges = missing,
            allEdges = allEdges
        )
    }

    /**
     * Get all back-edges in the CFG.
     */
    fun getBackEdges(): Set<CfgEdge> = allEdges.filter { it.kind == EdgeKind.BACK_EDGE }.toSet()

    /**
     * Get all forward edges (non-back-edges) in the CFG.
     */
    fun getForwardEdges(): Set<CfgEdge> = allEdges.filter { it.kind != EdgeKind.BACK_EDGE }.toSet()

    /**
     * Get edges emanating from a specific block.
     */
    fun getEdgesFrom(block: AssemblyBlock): List<CfgEdge> {
        return allEdges.filter { it.source == block }
    }

    /**
     * Get edges entering a specific block.
     */
    fun getEdgesTo(block: AssemblyBlock): List<CfgEdge> {
        return allEdges.filter { it.target == block }
    }
}

/**
 * Exception thrown when edge validation fails.
 */
class EdgeValidationException(message: String) : RuntimeException(message)

/**
 * Create an EdgeTracker for a function.
 */
fun AssemblyFunction.createEdgeTracker(): EdgeTracker {
    val blocks = this.blocks ?: setOf(this.startingBlock)
    return EdgeTracker(this, blocks)
}
