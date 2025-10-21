package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 9: Dominator Tree Construction
 * - Build dominator tree for each function
 * - Identify natural loops (back edges in dominator tree)
 * - Calculate dominance frontiers for SSA construction
 */

/**
 * A dominator tree node representing a basic block and its dominance relationships
 */
class DominatorNode(
    /** The basic block leader index this node represents */
    val leaderIndex: Int,
    /** The immediate dominator of this node (null for entry node) */
    var immediateDominator: DominatorNode?,
    /** Children dominated by this node */
    val children: MutableSet<DominatorNode> = mutableSetOf(),
    /** Dominance frontier - set of blocks where this node's dominance ends */
    val dominanceFrontier: MutableSet<Int> = mutableSetOf()
) {
    override fun toString(): String = "DominatorNode($leaderIndex)"
    override fun equals(other: Any?): Boolean = other is DominatorNode && other.leaderIndex == leaderIndex
    override fun hashCode(): Int = leaderIndex
}

/**
 * Result of dominator analysis for a single function
 */
data class DominatorAnalysis(
    /** The function this analysis applies to */
    val function: FunctionCfg,
    /** Root of the dominator tree (entry block) */
    val dominatorTree: DominatorNode,
    /** Map from block leader to its dominator node */
    val leaderToDomNode: Map<Int, DominatorNode>,
    /** Back edges that create loops (source -> target) */
    val backEdges: List<Pair<Int, Int>>
)

/**
 * Result of dominator analysis for the entire program
 */
data class DominatorConstruction(
    /** Dominator analysis for each function */
    val functions: List<DominatorAnalysis>
)

/**
 * Build dominator trees for all functions using Lengauer-Tarjan algorithm
 */
fun AssemblyCodeFile.constructDominatorTrees(
    cfg: CfgConstruction
): DominatorConstruction {
    val functionAnalyses = cfg.functions.map { function ->
        buildDominatorAnalysisForFunction(function)
    }
    
    return DominatorConstruction(functions = functionAnalyses)
}

/**
 * Legacy extension for backward compatibility
 */
@Deprecated("Use AssemblyCodeFile.constructDominatorTrees instead")
fun List<AssemblyLine>.constructDominatorTrees(
    cfg: CfgConstruction
): DominatorConstruction = this.toCodeFile().constructDominatorTrees(cfg)

/**
 * Build dominator analysis for a single function
 */
private fun buildDominatorAnalysisForFunction(function: FunctionCfg): DominatorAnalysis {
    val blocks = function.blocks
    val edges = function.edges
    
    if (blocks.isEmpty()) {
        // Empty function - create minimal analysis
        val dummyRoot = DominatorNode(function.entryLeader, null)
        return DominatorAnalysis(
            function = function,
            dominatorTree = dummyRoot,
            leaderToDomNode = mapOf(function.entryLeader to dummyRoot),
            backEdges = emptyList()
        )
    }
    
    // Build adjacency lists for the CFG
    val successors = mutableMapOf<Int, MutableList<Int>>()
    val predecessors = mutableMapOf<Int, MutableList<Int>>()
    
    blocks.forEach { block ->
        successors[block.leaderIndex] = mutableListOf()
        predecessors[block.leaderIndex] = mutableListOf()
    }
    
    edges.forEach { edge ->
        val from = edge.fromLeader
        val to = edge.toLeader
        if (to != null) { // Ignore return edges (toLeader == null)
            successors[from]?.add(to)
            predecessors[to]?.add(from)
        }
    }
    
    // Compute dominators using iterative algorithm
    val dominators = computeDominators(function.entryLeader, blocks.map { it.leaderIndex }, predecessors)
    
    // Build dominator tree
    val domNodes = mutableMapOf<Int, DominatorNode>()
    val entryLeader = function.entryLeader
    
    // Create root node
    val rootNode = DominatorNode(entryLeader, null)
    domNodes[entryLeader] = rootNode
    
    // Create other nodes and find immediate dominators
    blocks.forEach { block ->
        val leader = block.leaderIndex
        if (leader != entryLeader) {
            // Find immediate dominator properly
            val doms = dominators[leader] ?: emptySet()
            val strictDoms = doms.filter { it != leader }
            
            // Immediate dominator is the one closest to this node in the dominator chain
            // It's the dominator that is dominated by all other dominators
            val immDom = strictDoms.find { candidate ->
                strictDoms.all { other -> 
                    other == candidate || dominators[candidate]?.contains(other) == true
                }
            } ?: entryLeader
            
            val immDomNode = domNodes[immDom] ?: rootNode
            val node = DominatorNode(leader, immDomNode)
            domNodes[leader] = node
            
            // Add to parent's children
            immDomNode.children.add(node)
        }
    }
    
    // Compute dominance frontiers for SSA construction
    computeDominanceFrontiers(domNodes, successors, dominators)

    // Find back edges (natural loops are now detected in Pass 14)
    val backEdges = findBackEdges(edges, dominators)

    return DominatorAnalysis(
        function = function,
        dominatorTree = rootNode,
        leaderToDomNode = domNodes,
        backEdges = backEdges
    )
}

/**
 * Compute dominators using iterative dataflow algorithm
 */
private fun computeDominators(
    entryBlock: Int,
    allBlocks: List<Int>,
    predecessors: Map<Int, List<Int>>
): Map<Int, Set<Int>> {
    val dominators = mutableMapOf<Int, Set<Int>>()
    
    // Initialize: entry dominates only itself, others dominate all blocks
    allBlocks.forEach { block ->
        dominators[block] = if (block == entryBlock) {
            setOf(block)
        } else {
            allBlocks.toSet()
        }
    }
    
    // Iterate until fixed point
    var changed = true
    var iterations = 0
    while (changed && iterations < 100) { // Add safety limit
        changed = false
        iterations++
        
        allBlocks.forEach { block ->
            if (block == entryBlock) return@forEach
            
            val preds = predecessors[block] ?: emptyList()
            if (preds.isNotEmpty()) {
                // New dominators = {block} ∪ (intersection of all predecessor dominators)
                val predDomSets = preds.mapNotNull { dominators[it] }
                val newDoms = if (predDomSets.isNotEmpty()) {
                    predDomSets.reduce { acc, predDoms -> acc.intersect(predDoms) } + block
                } else {
                    setOf(block)
                }
                
                if (newDoms != dominators[block]) {
                    dominators[block] = newDoms
                    changed = true
                }
            }
        }
    }
    
    return dominators
}

/**
 * Compute dominance frontiers for each node
 */
private fun computeDominanceFrontiers(
    domNodes: Map<Int, DominatorNode>,
    successors: Map<Int, List<Int>>,
    dominators: Map<Int, Set<Int>>
) {
    domNodes.forEach { (block, domNode) ->
        successors[block]?.forEach { successor ->
            // successor is in dominance frontier of block if:
            // 1. block dominates a predecessor of successor
            // 2. block does not strictly dominate successor
            val blockDominatesSuccessor = dominators[successor]?.contains(block) == true
            if (!blockDominatesSuccessor || block == successor) {
                // Find all blocks that dominate a predecessor of successor
                val successorPreds = successors.entries.filter { (_, succs) -> successor in succs }.map { it.key }
                successorPreds.forEach { pred ->
                    if (dominators[pred]?.contains(block) == true) {
                        domNode.dominanceFrontier.add(successor)
                    }
                }
            }
        }
    }
}

/**
 * Find back edges in the CFG (edges that go to dominating blocks)
 */
private fun findBackEdges(
    edges: List<CfgEdge>,
    dominators: Map<Int, Set<Int>>
): List<Pair<Int, Int>> {
    val backEdges = mutableListOf<Pair<Int, Int>>()
    
    edges.forEach { edge ->
        val from = edge.fromLeader
        val to = edge.toLeader
        
        if (to != null) {
            // Back edge if target dominates source
            if (dominators[from]?.contains(to) == true) {
                backEdges.add(from to to)
            }
        }
    }
    
    return backEdges
}