package com.ivieleague.decompiler6502tokotlin.hand

// Structured control flow
sealed interface ControlNode {
    val id: Int
    val entry: AssemblyBlock
    val coveredBlocks: Set<AssemblyBlock>
    val exits: Set<AssemblyBlock>
    var parent: ControlNode?
}

// Leaf: single basic block
data class BlockNode(
    override val id: Int,
    val block: AssemblyBlock
) : ControlNode {
    override val entry: AssemblyBlock = block
    override val coveredBlocks: Set<AssemblyBlock> = setOf(block)
    override val exits: Set<AssemblyBlock> = buildSet {
        block.branchExit?.let { add(it) }
        block.fallThroughExit?.let { add(it) }
    }
    override var parent: ControlNode? = null
}

// If/Then[/Else] with lists instead of SequenceNode
data class IfNode(
    override val id: Int,
    val condition: Condition,
    val thenBranch: MutableList<ControlNode>,
    val elseBranch: MutableList<ControlNode> = mutableListOf(),
    // Optional reconvergence hint (unique post-dominator) when known
    val join: AssemblyBlock? = null
) : ControlNode {
    init { wireParent(this, thenBranch); wireParent(this, elseBranch) }

    override val entry: AssemblyBlock = condition.branchBlock
    override val coveredBlocks: Set<AssemblyBlock> = buildSet {
        add(condition.branchBlock)
        addAll(thenBranch.coveredBlocks())
        addAll(elseBranch.coveredBlocks())
    }
    override val exits: Set<AssemblyBlock> = computeIfExits(condition, thenBranch, elseBranch, join)
    override var parent: ControlNode? = null
}

enum class LoopKind { PreTest, PostTest, Infinite }

// Loop with list body
data class LoopNode(
    override val id: Int,
    val kind: LoopKind,
    val header: AssemblyBlock,
    val condition: Condition?, // null for Infinite
    val body: MutableList<ControlNode>,
    val continueTargets: Set<AssemblyBlock> = emptySet(),
    val breakTargets: Set<AssemblyBlock> = emptySet()
) : ControlNode {
    init { wireParent(this, body) }

    override val entry: AssemblyBlock = header
    override val coveredBlocks: Set<AssemblyBlock> = buildSet {
        add(header)
        addAll(body.coveredBlocks())
    }
    override val exits: Set<AssemblyBlock> = breakTargets
    override var parent: ControlNode? = null
}

// Switch / jump table with list cases
data class SwitchNode(
    override val id: Int,
    val selector: ValueExpr,
    val cases: List<Case>,
    val defaultBranch: MutableList<ControlNode> = mutableListOf(),
    // Optional reconvergence when known
    val join: AssemblyBlock? = null
) : ControlNode {
    data class Case(val matchValues: List<Int>, val nodes: MutableList<ControlNode>)

    init { cases.forEach { wireParent(this, it.nodes) }; wireParent(this, defaultBranch) }

    override val entry: AssemblyBlock = cases.firstOrNull()?.nodes?.firstOrNull()?.entry
        ?: defaultBranch.firstOrNull()?.entry
        ?: error("SwitchNode with no children")

    override val coveredBlocks: Set<AssemblyBlock> = buildSet {
        for (c in cases) addAll(c.nodes.coveredBlocks())
        addAll(defaultBranch.coveredBlocks())
    }

    override val exits: Set<AssemblyBlock> = when (join) {
        null -> buildSet {
            for (c in cases) addAll(c.nodes.lastExits())
            addAll(defaultBranch.lastExits())
        }
        else -> setOf(join)
    }

    override var parent: ControlNode? = null
}

// Escape hatches

data class GotoNode(
    override val id: Int,
    val from: AssemblyBlock,
    val to: AssemblyBlock
) : ControlNode {
    override val entry: AssemblyBlock = from
    override val coveredBlocks: Set<AssemblyBlock> = setOf(from)
    override val exits: Set<AssemblyBlock> = setOf(to)
    override var parent: ControlNode? = null
}

data class BreakNode(
    override val id: Int,
    val loop: LoopNode
) : ControlNode {
    override val entry: AssemblyBlock = loop.entry
    override val coveredBlocks: Set<AssemblyBlock> = emptySet()
    override val exits: Set<AssemblyBlock> = loop.breakTargets
    override var parent: ControlNode? = null
}

data class ContinueNode(
    override val id: Int,
    val loop: LoopNode
) : ControlNode {
    override val entry: AssemblyBlock = loop.entry
    override val coveredBlocks: Set<AssemblyBlock> = emptySet()
    override val exits: Set<AssemblyBlock> = setOf(loop.header)
    override var parent: ControlNode? = null
}

// 6502-oriented condition/value stubs remain as before.
sealed interface ValueExpr
sealed interface ConditionExpr

object UnknownCond : ConditionExpr

data class Condition(
    val branchBlock: AssemblyBlock,
    val branchLine: AssemblyLine,
    val sense: Boolean, // true => branch-taken is THEN
    val expr: ConditionExpr
)

fun List<ControlNode>.coveredBlocks(): Set<AssemblyBlock> =
    flatMapTo(mutableSetOf()) { it.coveredBlocks }

fun List<ControlNode>.lastExits(): Set<AssemblyBlock> =
    lastOrNull()?.exits ?: emptySet()

fun wireParent(owner: ControlNode, children: List<ControlNode>) {
    children.forEach { it.parent = owner }
}

private fun computeIfExits(
    condition: Condition,
    thenB: List<ControlNode>,
    elseB: List<ControlNode>,
    join: AssemblyBlock?
): Set<AssemblyBlock> {
    if (join != null) return setOf(join)

    val thenEx = thenB.lastExits()
    val elseEx = elseB.lastExits()

    return when {
        thenEx.isNotEmpty() && elseEx.isNotEmpty() -> {
            val inter = thenEx intersect elseEx
            if (inter.isNotEmpty()) inter else thenEx union elseEx
        }
        thenEx.isNotEmpty() -> thenEx
        elseEx.isNotEmpty() -> elseEx
        else -> {
            // Both branches empty: both legs must jump directly to the same place (or fall through).
            // Return the successors of the branch block. If they differ, you’ll preserve the ambiguity.
            buildSet {
                condition.branchBlock.branchExit?.let { add(it) }
                condition.branchBlock.fallThroughExit?.let { add(it) }
            }
        }
    }
}

// -------------------------
// Minimal control analyzer
// -------------------------

/**
 * Build a simple structured representation for this function by linearizing
 * its reachable basic blocks into BlockNodes in reverse postorder. The result
 * is stored in [AssemblyFunction.asControls] and also returned.
 */
fun AssemblyFunction.analyzeControls(): List<ControlNode> {
    val fn = this

    // Collect reachable blocks within this function
    val reachable = LinkedHashSet<AssemblyBlock>()
    fun walk(b: AssemblyBlock?) {
        if (b == null) return
        if (b.function != fn) return
        if (!reachable.add(b)) return
        walk(b.fallThroughExit)
        walk(b.branchExit)
    }
    walk(startingBlock)

    if (reachable.isEmpty()) {
        this.asControls = emptyList()
        return emptyList()
    }

    // Use source/layout order to detect structured patterns (typical 6502 style)
    val layout = reachable.sortedBy { it.originalLineIndex }
    val indexOf = HashMap<AssemblyBlock, Int>(layout.size).also { map ->
        for ((i, b) in layout.withIndex()) map[b] = i
    }

    var nextId = 0

    fun AssemblyBlock.lastInstructionLine(): AssemblyLine? =
        this.lines.lastOrNull { it.instruction != null }

    fun AssemblyBlock.isConditional(): Boolean =
        this.lastInstructionLine()?.instruction?.op?.isBranch == true

    fun AssemblyBlock.isUnconditionalJmp(): Boolean =
        this.lastInstructionLine()?.instruction?.op == AssemblyOp.JMP

    fun AssemblyBlock.isReturnLike(): Boolean =
        this.lastInstructionLine()?.instruction?.op.let { it == AssemblyOp.RTS || it == AssemblyOp.RTI } == true

    fun buildRange(start: Int, endExclusive: Int): MutableList<ControlNode> {
        val out = mutableListOf<ControlNode>()
        var i = start
        while (i < endExclusive) {
            val b = layout[i]
            val last = b.lastInstructionLine()

            // If / Then [/ Else] patterns
            if (b.isConditional()) {
                val ft = b.fallThroughExit
                val br = b.branchExit
                val ftIdx = ft?.let { indexOf[it] } ?: -1
                val brIdx = br?.let { indexOf[it] } ?: -1

                if (ft != null && br != null && ftIdx == i + 1 && brIdx in (i + 1)..layout.lastIndex) {
                    // try IF-ELSE: then ends with JMP join
                    if (brIdx > i + 1) {
                        val lastThenIdx = brIdx - 1
                        val lastThen = layout[lastThenIdx]
                        if (lastThen.isUnconditionalJmp()) {
                            val join = lastThen.branchExit
                            val joinIdx = join?.let { indexOf[it] }
                            if (joinIdx != null && joinIdx > brIdx && joinIdx <= endExclusive) {
                                // then = (i+1 .. lastThenIdx) but omit a pure-JMP tail block if it contains only the JMP
                                val pureJmp = lastThen.lines.count { it.instruction != null } == 1
                                val thenEnd = if (pureJmp) lastThenIdx else lastThenIdx + 1
                                val thenNodes = buildRange(i + 1, thenEnd)
                                val elseNodes = buildRange(brIdx, joinIdx)
                                val cond = Condition(
                                    branchBlock = b,
                                    branchLine = last!!,
                                    sense = false, // branch-taken goes to else; fall-through is THEN
                                    expr = UnknownCond
                                )
                                out.add(
                                    IfNode(
                                        id = nextId++,
                                        condition = cond,
                                        thenBranch = thenNodes,
                                        elseBranch = elseNodes,
                                        join = layout[joinIdx]
                                    )
                                )
                                i = joinIdx
                                continue
                            }
                        }
                    }
                    // IF-THEN: branch target is the join right after then
                    val thenNodes = buildRange(i + 1, brIdx)
                    val cond = Condition(
                        branchBlock = b,
                        branchLine = last!!,
                        sense = false, // branch-taken skips THEN to join
                        expr = UnknownCond
                    )
                    out.add(
                        IfNode(
                            id = nextId++,
                            condition = cond,
                            thenBranch = thenNodes,
                            elseBranch = mutableListOf(),
                            join = layout[brIdx]
                        )
                    )
                    i = brIdx
                    continue
                }
            }

            // Pre-test loop: header has conditional branch that exits forward, body ends with JMP back to header
            if (b.isConditional()) {
                val ft = b.fallThroughExit
                val br = b.branchExit
                val ftIdx = ft?.let { indexOf[it] } ?: -1
                val brIdx = br?.let { indexOf[it] } ?: -1
                val exitIdx = if (brIdx > i) brIdx else null

                if (ft != null && ftIdx == i + 1) {
                    var backJmpIdx: Int? = null
                    var k = i + 1
                    while (k < endExclusive && (exitIdx == null || k < exitIdx)) {
                        val bk = layout[k]
                        if (bk.isUnconditionalJmp() && bk.branchExit == b) { backJmpIdx = k; break }
                        k++
                    }
                    if (backJmpIdx != null) {
                        val bodyNodes = buildRange(i + 1, backJmpIdx + 1) // include the back-jmp block
                        val cond = Condition(
                            branchBlock = b,
                            branchLine = b.lastInstructionLine()!!,
                            sense = false,
                            expr = UnknownCond
                        )
                        out.add(
                            LoopNode(
                                id = nextId++,
                                kind = LoopKind.PreTest,
                                header = b,
                                condition = cond,
                                body = bodyNodes,
                                continueTargets = setOf(b),
                                breakTargets = br?.let { setOf(it) } ?: emptySet()
                            )
                        )
                        i = exitIdx ?: (backJmpIdx + 1)
                        continue
                    }
                }
            }

            // Default: just a basic block
            out.add(BlockNode(id = nextId++, block = b))
            i++
        }
        return out
    }

    val nodes = buildRange(0, layout.size)
    this.asControls = nodes
    return nodes
}

/** Analyze controls for a list of functions. */
fun List<AssemblyFunction>.analyzeControls() {
    for (f in this) f.analyzeControls()
}
