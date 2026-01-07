package com.ivieleague.decompiler6502tokotlin.hand

/*
 * ═══════════════════════════════════════════════════════════════════════════
 * STRUCTURED CONTROL FLOW ANALYSIS (controls.kt)
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * This file defines the intermediate representation (IR) for structured control
 * flow in the 6502 decompiler. It converts basic blocks (from blocks.kt) into
 * a hierarchical tree of high-level control structures.
 *
 * PIPELINE POSITION:
 * blocks.kt → controls.kt → (expression analysis) → Kotlin code generation
 *
 * INPUT: List<AssemblyBlock> (flat basic blocks with branch/fallthrough edges)
 * OUTPUT: List<ControlNode> (hierarchical control flow tree)
 *
 * ───────────────────────────────────────────────────────────────────────────
 * KEY CONCEPT: Branch Instructions Remain in Blocks
 * ───────────────────────────────────────────────────────────────────────────
 *
 * IMPORTANT: At this stage, branch instructions (BEQ, BNE, JMP, etc.) are NOT
 * removed from the AssemblyBlocks. They remain as assembly instructions within
 * the block's lines. What we're building here is a STRUCTURAL UNDERSTANDING of
 * how those branches form high-level patterns.
 *
 * The Condition object records:
 * - WHICH block contains the branch (branchBlock)
 * - WHICH instruction is the branch (branchLine)
 * - WHAT the branch means structurally (sense: is branch-taken the "yes" path?)
 * - WHAT the condition tests semantically (expr: currently UnknownCond)
 *
 * Later passes will:
 * 1. Analyze the instructions BEFORE the branch to understand the condition
 *    (e.g., "LDA $00; BEQ target" → "if (memory[$00] == 0)")
 * 2. Build expression trees for the condition
 * 3. Eventually remove/transform the branch instructions during code generation
 *
 * ───────────────────────────────────────────────────────────────────────────
 * CONTROL FLOW HIERARCHY
 * ───────────────────────────────────────────────────────────────────────────
 *
 * ControlNode (sealed interface)
 *  ├─ BlockNode: Leaf node wrapping a single basic block
 *  ├─ Structured Constructs (composite nodes):
 *  │   ├─ IfNode: if/then or if/then/else conditionals
 *  │   ├─ LoopNode: while, do-while, or infinite loops
 *  │   └─ SwitchNode: switch/case from jump tables
 *  └─ Escape Hatches (unstructured jumps):
 *      ├─ GotoNode: Arbitrary jump that doesn't fit a pattern
 *      ├─ BreakNode: Early exit from a loop
 *      └─ ContinueNode: Skip to next iteration of a loop
 *
 * ───────────────────────────────────────────────────────────────────────────
 * ANALYSIS ALGORITHM (analyzeControls)
 * ───────────────────────────────────────────────────────────────────────────
 *
 * The analysis works by pattern matching on block layout and branch directions:
 *
 * 1. Natural Loop Detection:
 *    - Use dominator tree analysis to find back-edges (jumps to earlier blocks)
 *    - Classify as PreTest (while), PostTest (do-while), or Infinite
 *    - Recursively analyze loop bodies
 *
 * 2. Conditional Pattern Matching:
 *    - Forward branch + fall-through = potential IF-THEN
 *    - IF-THEN with final JMP = potential IF-THEN-ELSE
 *    - Backward conditional branch = potential loop
 *
 * 3. Layout Order Heuristics:
 *    - Blocks are ordered by original source line (layout order)
 *    - Forward jumps suggest conditionals (skip over code)
 *    - Backward jumps suggest loops (repeat code)
 *
 * The algorithm proceeds in a single pass, greedily matching patterns from
 * largest to smallest to build the control tree bottom-up.
 *
 * ───────────────────────────────────────────────────────────────────────────
 * FUTURE WORK
 * ───────────────────────────────────────────────────────────────────────────
 *
 * - Goto elimination: Transform remaining GotoNodes into structured constructs
 * - Region formation: Identify single-entry/single-exit regions
 * - Expression analysis: Build semantic ConditionExpr from flag-setting instructions
 * - Code generation: Emit Kotlin if/while/do-while from this IR
 */

/**
 * Structured control flow representation for decompiled 6502 assembly.
 *
 * This hierarchy models high-level control structures (if/else, loops, etc.) reconstructed
 * from basic blocks and branch instructions. Each ControlNode represents either:
 * 1. A leaf node (BlockNode) - a single basic block
 * 2. A structured construct (IfNode, LoopNode, SwitchNode) - composed of child nodes
 * 3. An escape hatch (GotoNode, BreakNode, ContinueNode) - unstructured jumps
 */
sealed interface ControlNode {
    /** Unique identifier for this node */
    val id: Int

    /** The entry block where control enters this construct */
    val entry: AssemblyBlock

    /** All basic blocks contained within this control structure (including nested) */
    val coveredBlocks: Set<AssemblyBlock>

    /** Blocks where control exits this construct (potential successors after execution) */
    val exits: Set<AssemblyBlock>

    /** Parent node in the control tree (null for top-level nodes) */
    var parent: ControlNode?
}

/**
 * BlockNode: Leaf node representing a single basic block.
 *
 * This is the fundamental building block of the control flow tree. Each BlockNode wraps
 * one AssemblyBlock which contains a linear sequence of instructions with:
 * - Zero or one conditional branch instruction (BEQ, BNE, BCC, BCS, BMI, BPL, BVC, BVS)
 * - Zero or one unconditional jump (JMP)
 * - Zero or one return (RTS, RTI)
 *
 * BRANCH OPERATIONS IN BlockNode:
 * The actual branch instructions remain INSIDE the AssemblyBlock. They are NOT lifted out yet.
 * The block's branch/fallthrough exits are derived from analyzing the last instruction:
 *
 * Examples:
 * 1. Conditional branch (BEQ $8050):
 *    - branchExit = block at $8050 (taken if Z=1)
 *    - fallThroughExit = next sequential block (not taken)
 *
 * 2. Unconditional jump (JMP $9000):
 *    - branchExit = block at $9000
 *    - fallThroughExit = null
 *
 * 3. Return (RTS):
 *    - branchExit = null
 *    - fallThroughExit = null
 *
 * 4. Linear block (LDA #$05, STA $00):
 *    - branchExit = null
 *    - fallThroughExit = next sequential block
 */
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

/**
 * IfNode: Represents an if/then or if/then/else conditional construct.
 *
 * Reconstructed from a conditional branch instruction by pattern matching basic block layout.
 *
 * BRANCH OPERATIONS IN IfNode:
 * The branch instruction that forms the condition is STILL in the condition.branchBlock.
 * The Condition object records:
 * - Which block contains the branch (branchBlock)
 * - The actual branch instruction line (branchLine)
 * - The "sense" of the condition (which path is "then" vs "else")
 * - An expression representing the condition (currently UnknownCond, later will be semantic)
 *
 * How branch targets map to then/else:
 * - If sense=true: branch-taken → then, fall-through → else
 * - If sense=false: fall-through → then, branch-taken → else
 *
 * Pattern examples:
 * 1. IF-THEN (no else):
 *    Block A: BEQ skip    ; conditional branch forward
 *    Block B: ...then...  ; then body (fall-through path)
 *    Block C: skip:       ; join point (branch target)
 *    → condition.sense = false (fall-through is then)
 *
 * 2. IF-THEN-ELSE:
 *    Block A: BEQ else_label  ; conditional branch to else
 *    Block B: ...then...      ; then body
 *    Block C: JMP join        ; unconditional jump to join
 *    Block D: else_label:     ; else body (branch target)
 *    Block E: join:           ; reconvergence point
 *    → condition.sense = false (fall-through is then)
 *
 * Structure:
 * - entry: The block containing the conditional branch
 * - thenBranch: List of nodes executed when condition is true
 * - elseBranch: List of nodes executed when condition is false (empty if no else)
 * - join: Optional reconvergence block where both paths meet again
 */
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

enum class LoopKind {
    /** while (cond) { body } - test before execution */
    PreTest,
    /** do { body } while (cond) - test after execution */
    PostTest,
    /** loop { body } - no exit condition */
    Infinite
}

/**
 * LoopNode: Represents a loop construct (while, do-while, or infinite loop).
 *
 * Reconstructed by detecting cyclic control flow (back-edges in the CFG).
 *
 * BRANCH OPERATIONS IN LoopNode:
 * Loops involve TWO types of branches:
 * 1. The loop condition (exit test) - stored in the Condition object
 * 2. Back-edges (jumps back to loop header) - implicit in block connectivity
 *
 * Loop kinds and their branch patterns:
 *
 * 1. PreTest (while loop):
 *    header: BEQ exit    ; condition at start - branch exits loop
 *    body:   ...         ; loop body
 *            JMP header  ; unconditional back-jump
 *    exit:   ...         ; after loop
 *    → condition.branchBlock = header
 *    → condition.sense = false (fall-through continues, branch exits)
 *
 * 2. PostTest (do-while loop):
 *    header: ...         ; loop body starts immediately
 *    body:   ...
 *    test:   BNE header  ; condition at end - branch continues
 *    exit:   ...         ; fall-through exits
 *    → condition.branchBlock = test block (last in body)
 *    → condition.sense = true (branch continues, fall-through exits)
 *
 * 3. Infinite loop:
 *    header: ...         ; loop body
 *    body:   ...
 *            JMP header  ; unconditional back-jump
 *    → condition = null (no exit test)
 *    → No normal exits (breakTargets empty)
 *
 * Special cases:
 * - Self-loops: Single-block loops where header branches to itself
 * - Multiple exits: break statements create additional exit points (goto to breakTargets)
 * - Continue statements: jumps to continueTargets (usually the header)
 *
 * Detection uses natural loop analysis (dominator tree back-edges) combined with
 * pattern matching on block layout and branch direction.
 */
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

/**
 * SwitchNode: Represents a switch/case construct (typically from jump tables).
 *
 * Jump tables are a 6502 optimization technique where indirect jumps select from
 * a table of addresses based on a runtime value (often an index in A or X).
 *
 * BRANCH OPERATIONS IN SwitchNode:
 * Switch statements don't use explicit conditional branches. Instead, they use:
 * 1. Computed indirect jumps: JMP (addr,X) or JMP (addr)
 * 2. Jump tables: Arrays of addresses in memory
 *
 * Pattern example (6502 jump table):
 *    LDA state        ; load selector value
 *    ASL A            ; multiply by 2 (addresses are 2 bytes)
 *    TAX              ; transfer to X register
 *    LDA table,X      ; load low byte of target address
 *    STA temp         ; store to zero-page temp
 *    LDA table+1,X    ; load high byte
 *    STA temp+1
 *    JMP (temp)       ; indirect jump through temp
 *
 *    table:
 *    .dw case_0       ; address of case 0 handler
 *    .dw case_1       ; address of case 1 handler
 *    .dw case_2       ; address of case 2 handler
 *    .dw default      ; default case
 *
 * The selector ValueExpr represents the computed value (e.g., "state" variable).
 * Each Case contains the matching values and the nodes to execute for that case.
 *
 * NOTE: Switch detection is complex and may not be implemented yet. This structure
 * is prepared for future jump table analysis.
 */
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

// ===========================
// Escape hatches (unstructured control flow)
// ===========================

/**
 * GotoNode: Represents an unstructured jump that couldn't be matched to a higher-level construct.
 *
 * These are fallback nodes for control flow that doesn't fit standard patterns (if/loop/switch).
 *
 * BRANCH OPERATIONS IN GotoNode:
 * This represents a raw jump that remains after structural analysis fails to recognize a pattern.
 * The actual branch instruction (JMP, or conditional branch) is in the 'from' block.
 *
 * Common cases that create GotoNodes:
 * 1. Jumps into the middle of a loop or conditional
 * 2. Complex multi-way branches not matching if/switch patterns
 * 3. Computed jumps that aren't recognized as jump tables
 * 4. Error handling or state machine transitions
 *
 * Example (jump into middle of construct):
 *    Block A: LDA value
 *             CMP #5
 *             BEQ middle    ; jumps into middle of Block C
 *    Block B: ...
 *    Block C: first_part:
 *             ...
 *             middle:       ; entry point from Block A
 *             ...
 *
 * These gotos will be progressively eliminated in later passes (goto elimination).
 */
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

/**
 * BreakNode: Represents a break statement (early exit from a loop).
 *
 * Created when a jump from inside a loop targets one of the loop's breakTargets.
 *
 * BRANCH OPERATIONS IN BreakNode:
 * The actual branch that breaks out of the loop is in one of the loop body's blocks.
 * This node is a placeholder that will generate a "break" statement in the output.
 *
 * Example:
 *    header: BEQ exit      ; normal loop exit
 *    body:   LDA condition
 *            BNE continue  ; conditional skip
 *            JMP exit      ; early exit (break)
 *    continue:
 *            ...
 *            JMP header
 *    exit:
 *
 * The JMP exit from the middle of the loop body becomes a BreakNode.
 */
data class BreakNode(
    override val id: Int,
    val loop: LoopNode
) : ControlNode {
    override val entry: AssemblyBlock = loop.entry
    override val coveredBlocks: Set<AssemblyBlock> = emptySet()
    override val exits: Set<AssemblyBlock> = loop.breakTargets
    override var parent: ControlNode? = null
}

/**
 * ContinueNode: Represents a continue statement (skip to next loop iteration).
 *
 * Created when a jump from inside a loop targets the loop header (or continue point).
 *
 * BRANCH OPERATIONS IN ContinueNode:
 * The actual branch that continues the loop is in one of the loop body's blocks.
 * This node is a placeholder that will generate a "continue" statement in the output.
 *
 * Example:
 *    header: LDX #0
 *    test:   CPX #10
 *            BEQ exit
 *    body:   LDA array,X
 *            CMP #0
 *            BEQ skip      ; skip processing if zero
 *            JSR process   ; process non-zero values
 *    skip:   INX
 *            JMP test      ; this becomes a continue
 *    exit:
 *
 * The JMP test at the end becomes a ContinueNode pointing to the loop header.
 */
data class ContinueNode(
    override val id: Int,
    val loop: LoopNode
) : ControlNode {
    override val entry: AssemblyBlock = loop.entry
    override val coveredBlocks: Set<AssemblyBlock> = emptySet()
    override val exits: Set<AssemblyBlock> = setOf(loop.header)
    override var parent: ControlNode? = null
}

// ===========================
// Condition and Value Expression stubs
// ===========================

/**
 * ValueExpr: Represents a computed value (for switch selectors, conditions, etc.).
 *
 * Implemented below with concrete types for registers, memory, and literals.
 */
// sealed interface ValueExpr - defined below after CompareOp

/**
 * ConditionExpr: Represents a boolean condition (for if/loop tests).
 *
 * This will eventually contain semantic representations of 6502 flag tests, such as:
 * - Zero flag: Z == 1 (from CMP, LDA, etc.)
 * - Carry flag: C == 1 (from CMP, ADC, SBC)
 * - Negative flag: N == 1 (sign bit test)
 * - Overflow flag: V == 1 (signed overflow)
 * - Combined conditions: (Z == 0 && C == 1) for unsigned greater-than
 *
 * Currently all conditions are UnknownCond placeholders.
 */
sealed interface ConditionExpr {
    /** Convert to Kotlin boolean expression */
    fun toKotlinExpr(): KotlinExpr
}

object UnknownCond : ConditionExpr {
    override fun toKotlinExpr() = KVar("unknownCondition")
}

/** Direct flag test: if (zeroFlag), if (!carryFlag) */
data class FlagTest(
    val flag: AssemblyAffectable, // One of: Negative, Zero, Carry, Overflow
    val positive: Boolean // true = test if set, false = test if clear
) : ConditionExpr {
    override fun toKotlinExpr(): KotlinExpr {
        val flagVar = when (flag) {
            AssemblyAffectable.Negative -> KVar("negativeFlag")
            AssemblyAffectable.Zero -> KVar("zeroFlag")
            AssemblyAffectable.Carry -> KVar("carryFlag")
            AssemblyAffectable.Overflow -> KVar("overflowFlag")
            else -> KVar("unknownFlag")
        }
        return if (positive) flagVar else KUnaryOp("!", flagVar)
    }
}

/** Comparison expression: A == value, X < #$10, memory[addr] != 0 */
data class ComparisonExpr(
    val left: ValueExpr,
    val op: CompareOp,
    val right: ValueExpr
) : ConditionExpr {
    override fun toKotlinExpr(): KotlinExpr {
        val opStr = when (op) {
            CompareOp.EQ -> "=="
            CompareOp.NE -> "!="
            CompareOp.LT_UNSIGNED -> "<"
            CompareOp.LE_UNSIGNED -> "<="
            CompareOp.GT_UNSIGNED -> ">"
            CompareOp.GE_UNSIGNED -> ">="
            CompareOp.LT_SIGNED -> "<"
            CompareOp.LE_SIGNED -> "<="
            CompareOp.GT_SIGNED -> ">"
            CompareOp.GE_SIGNED -> ">="
        }
        return KBinaryOp(left.toKotlinExpr(), opStr, right.toKotlinExpr())
    }
}

enum class CompareOp {
    EQ, NE,
    LT_UNSIGNED, LE_UNSIGNED, GT_UNSIGNED, GE_UNSIGNED,
    LT_SIGNED, LE_SIGNED, GT_SIGNED, GE_SIGNED
}

/** Value expressions: registers, memory, literals */
sealed interface ValueExpr {
    fun toKotlinExpr(): KotlinExpr
}

data class RegisterValue(val register: AssemblyAffectable) : ValueExpr {
    override fun toKotlinExpr() = when (register) {
        AssemblyAffectable.A -> KVar("A")
        AssemblyAffectable.X -> KVar("X")
        AssemblyAffectable.Y -> KVar("Y")
        else -> KVar("unknown")
    }
}

data class MemoryValue(val address: String) : ValueExpr {
    override fun toKotlinExpr() = KMemberAccess(KVar("memory"), KLiteral(address), isIndexed = true)
}

data class LiteralValue(val value: Int) : ValueExpr {
    override fun toKotlinExpr() = KLiteral("0x${value.toString(16).uppercase().padStart(2, '0')}")
}

/** Bitwise test: (value & mask) != 0 */
data class BitwiseTest(
    val value: ValueExpr,
    val mask: Int,
    val nonZero: Boolean // true = test if any bits set, false = test if all bits clear
) : ConditionExpr {
    override fun toKotlinExpr(): KotlinExpr {
        val masked = KBinaryOp(
            value.toKotlinExpr(),
            "and",
            KLiteral("0x${mask.toString(16).uppercase()}")
        )
        val comparison = KBinaryOp(masked, if (nonZero) "!=" else "==", KLiteral("0"))
        return comparison
    }
}

/**
 * Condition: Encapsulates a conditional branch and its semantic meaning.
 *
 * This object bridges the gap between low-level branch instructions and high-level conditionals.
 *
 * CRITICAL: Understanding 'sense' (the polarity of the condition):
 * - sense = true: "branch-taken path is the THEN/continue path"
 * - sense = false: "fall-through path is the THEN/continue path"
 *
 * Why 'sense' is needed:
 * 6502 has 8 conditional branches that test different flags in different ways:
 * - BEQ (branch if Z=1), BNE (branch if Z=0)
 * - BCC (branch if C=0), BCS (branch if C=1)
 * - BMI (branch if N=1), BPL (branch if N=0)
 * - BVC (branch if V=0), BVS (branch if V=1)
 *
 * When reconstructing high-level code, we need to normalize these:
 *
 * Example 1 (sense = false):
 *    BEQ skip    ; "if zero, skip then-body"
 *    ...then...  ; fall-through = then
 *    skip:
 * → if (!zero) { then }  or  if (value != 0) { then }
 *
 * Example 2 (sense = true):
 *    BNE do_it   ; "if not zero, do it"
 *    JMP skip
 *    do_it:
 *    ...then...  ; branch-taken = then
 *    skip:
 * → if (value != 0) { then }
 *
 * The expr field contains the actual condition expression that can be reconstructed
 * by analyzing the instructions before the branch.
 */
data class Condition(
    /** The block containing the conditional branch instruction */
    val branchBlock: AssemblyBlock,
    /** The actual assembly line with the branch instruction */
    val branchLine: AssemblyLine,
    /**
     * Polarity of the condition:
     * - true: branch-taken is the "yes" path (then/continue)
     * - false: fall-through is the "yes" path (then/continue)
     */
    val sense: Boolean,
    /**
     * The reconstructed condition expression.
     * Can be set by expression analysis passes.
     */
    var expr: ConditionExpr = UnknownCond
) {
    override fun toString(): String = "Condition(${branchLine.instruction})"

    /**
     * Get the condition as a Kotlin expression, respecting the sense.
     * If sense is false, negates the expression.
     */
    fun toKotlinCondition(): KotlinExpr {
        val base = expr.toKotlinExpr()
        return if (sense) base else KUnaryOp("!", base)
    }
}

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

    // Detect natural loops using dominator analysis
    val naturalLoops = reachable.toList().detectNaturalLoops()
    val loopByHeader = naturalLoops.associateBy { it.header }

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

    // Track loop headers currently being processed to prevent infinite recursion
    val processingLoopHeaders = mutableSetOf<AssemblyBlock>()

    fun buildRange(start: Int, endExclusive: Int): MutableList<ControlNode> {
        val out = mutableListOf<ControlNode>()
        var i = start
        while (i < endExclusive) {
            val b = layout[i]
            val last = b.lastInstructionLine()

            // Natural loop detection: If this block is a loop header, create a LoopNode
            // Skip if this header is already being processed (prevents infinite recursion)
            val naturalLoop = loopByHeader[b]?.takeIf { b !in processingLoopHeaders }
            if (naturalLoop != null) {
                // Find the extent of the loop body in the layout
                val loopBodyBlocks = naturalLoop.body.sortedBy { indexOf[it] ?: Int.MAX_VALUE }
                val loopStart = indexOf[loopBodyBlocks.first()] ?: i
                val loopEnd = indexOf[loopBodyBlocks.last()]?.plus(1) ?: (i + 1)

                // Skip this loop if it contains other loop headers (process inner loops first)
                val containsOtherLoopHeaders = naturalLoop.body.any { block ->
                    block != b && loopByHeader.containsKey(block)
                }

                // Only process as a loop if we haven't already consumed these blocks
                if (loopStart == i && loopEnd <= endExclusive && !containsOtherLoopHeaders) {
                    // Determine loop kind based on structure
                    // Key distinction:
                    // - PreTest (while): Header has conditional that EXITS the loop
                    // - PostTest (do-while): Header has no exit condition OR internal conditional only; back-edge source is conditional
                    val headerIsConditional = b.isConditional()
                    val backEdgeSource = naturalLoop.backEdges.firstOrNull()?.first
                    val backEdgeIsConditional = backEdgeSource?.let { it.isConditional() } ?: false

                    // CRITICAL FIX: Check if header's branch target is INSIDE the loop body
                    // If so, it's an internal conditional (like "skip some code"), not a loop exit test
                    val headerBranchTargetInLoop = b.branchExit?.let { it in naturalLoop.body } ?: false
                    val headerHasInternalBranch = headerIsConditional && headerBranchTargetInLoop

                    val loopKind = when {
                        // Infinite loop: no conditional, no exits
                        !headerIsConditional && !backEdgeIsConditional && naturalLoop.exits.isEmpty() -> LoopKind.Infinite
                        // Single-block self-loop with conditional: PostTest (do-while)
                        // Check this FIRST before other heuristics
                        naturalLoop.body.size == 1 && backEdgeSource == b && headerIsConditional -> LoopKind.PostTest
                        // PostTest: header has internal branch only (not exit test), and back-edge is conditional
                        // Example: ShuffleLoop has BCC NextSprOffset (internal), and NextSprOffset has BPL ShuffleLoop (back-edge)
                        headerHasInternalBranch && backEdgeIsConditional -> LoopKind.PostTest
                        // PostTest: header is not conditional, but back-edge source is
                        !headerIsConditional && backEdgeIsConditional -> LoopKind.PostTest
                        // PreTest: header is conditional with a forward EXIT (not internal branch)
                        headerIsConditional && !headerBranchTargetInLoop && naturalLoop.exits.isNotEmpty() -> LoopKind.PreTest
                        // Otherwise, assume PreTest (while) as default
                        else -> LoopKind.PreTest
                    }

                    // Recursively analyze the loop body's internal control flow
                    // For PostTest loops with internal header branches, INCLUDE the header so its conditional becomes an if-then
                    // For PreTest loops, skip the header (its condition is the loop condition, already handled)
                    val bodyStart = if (loopKind == LoopKind.PostTest && headerHasInternalBranch) {
                        loopStart  // Include header in body analysis
                    } else {
                        loopStart + 1  // Skip the loop header itself
                    }

                    // Mark this header as being processed to prevent infinite recursion
                    processingLoopHeaders.add(b)
                    val bodyNodes: MutableList<ControlNode> = try {
                        if (bodyStart < loopEnd) {
                            buildRange(bodyStart, loopEnd)
                        } else {
                            // Single-block loop - just add the header as a block node
                            mutableListOf(BlockNode(id = nextId++, block = b))
                        }
                    } finally {
                        processingLoopHeaders.remove(b)
                    }

                    // Find the condition block (last block with back-edge)
                    // For Infinite loops, condition is null
                    val cond = if (loopKind == LoopKind.Infinite) {
                        null
                    } else {
                        val conditionBlock = naturalLoop.backEdges.firstOrNull()?.first ?: b
                        // Determine sense: if branch-taken goes to header, sense=true; if fall-through goes to header, sense=false
                        val branchGoesToHeader = conditionBlock.branchExit == b
                        Condition(
                            branchBlock = conditionBlock,
                            branchLine = conditionBlock.lastInstructionLine() ?: b.lastInstructionLine()!!,
                            sense = branchGoesToHeader,
                        )
                    }

                    out.add(
                        LoopNode(
                            id = nextId++,
                            kind = loopKind,
                            header = b,
                            condition = cond,
                            body = bodyNodes,
                            continueTargets = setOf(b),
                            breakTargets = naturalLoop.exits
                        )
                    )
                    i = loopEnd
                    continue
                }
            }

            // Pre-test loop (variant): header has no condition, body ends with conditional back-branch
            // Pattern: loop_start: body; test; branch_back_if_true loop_start
            // This is common in 6502 where initialization happens before loop, and test is at bottom
            // IMPORTANT: Only detect this if we're NOT at the function start (to avoid tail recursion)
            if (!b.isConditional() && !b.isUnconditionalJmp() && !b.isReturnLike() && i > start) {
                // Look ahead for a backward conditional branch to this block
                var backBranchIdx: Int? = null
                var k = i + 1
                while (k < endExclusive && k - i < 15) { // Look ahead max 15 blocks for loop
                    val bk = layout[k]
                    if (bk.isConditional()) {
                        val bkBranchTarget = bk.branchExit
                        val bkFtTarget = bk.fallThroughExit
                        // Check if this conditional branches back to our header
                        if (bkBranchTarget == b || bkFtTarget == b) {
                            backBranchIdx = k
                            break
                        }
                    }
                    k++
                }

                if (backBranchIdx != null) {
                    val backBlock = layout[backBranchIdx]
                    val branchesToHeader = backBlock.branchExit == b
                    val fallsThroughToHeader = backBlock.fallThroughExit == b
                    val exitBlock = if (branchesToHeader) backBlock.fallThroughExit else backBlock.branchExit

                    // Only create loop if exit is forward (not another backward jump)
                    val exitIdx = exitBlock?.let { indexOf[it] } ?: -1
                    if (exitIdx > backBranchIdx) {
                        // Don't recursively process - just wrap blocks as BlockNodes to avoid infinite recursion
                        val bodyNodes: MutableList<ControlNode> = (i..backBranchIdx).map { idx ->
                            BlockNode(id = nextId++, block = layout[idx])
                        }.toMutableList()

                        val cond = Condition(
                            branchBlock = backBlock,
                            branchLine = backBlock.lastInstructionLine()!!,
                            sense = branchesToHeader, // true if branch goes to header
                        )
                        out.add(
                            LoopNode(
                                id = nextId++,
                                kind = LoopKind.PreTest,
                                header = b,
                                condition = cond,
                                body = bodyNodes,
                                continueTargets = setOf(b),
                                breakTargets = exitBlock?.let { setOf(it) } ?: emptySet()
                            )
                        )
                        i = exitIdx
                        continue
                    }
                }
            }

            // Pre-test loop: header has conditional branch that exits forward, body ends with JMP back to header
            // Check this BEFORE IF-THEN patterns to correctly identify loops
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
                        // Check for unconditional JMP or conditional branch back to header
                        val hasBackJump = (bk.isUnconditionalJmp() && bk.branchExit == b) ||
                                         (bk.isConditional() && (bk.branchExit == b || bk.fallThroughExit == b))
                        if (hasBackJump) {
                            backJmpIdx = k
                            break
                        }
                        k++
                    }
                    if (backJmpIdx != null) {
                        val bodyNodes = buildRange(i + 1, backJmpIdx + 1) // include the back-jmp block
                        val cond = Condition(
                            branchBlock = b,
                            branchLine = b.lastInstructionLine()!!,
                            sense = false,
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

            // If / Then [/ Else] patterns
            if (b.isConditional()) {
                val ft = b.fallThroughExit
                val br = b.branchExit
                val ftIdx = ft?.let { indexOf[it] } ?: -1
                val brIdx = br?.let { indexOf[it] } ?: -1

                // CRITICAL FIX: Constrain branch index to current range's endExclusive
                // This prevents inner if-then patterns from including blocks that belong
                // to outer structures (like branch targets of earlier branches)
                val constrainedBrIdx = if (brIdx >= endExclusive) -1 else brIdx

                // Handle forward branches where target is outside current range
                // These should still create if-then structure, but then-branch is constrained to endExclusive
                val hasOutOfRangeBranch = brIdx >= endExclusive && brIdx >= 0 && brIdx < layout.size

                if (ft != null && br != null && ftIdx == i + 1 && (constrainedBrIdx in (i + 1) until endExclusive || hasOutOfRangeBranch)) {
                    // try IF-ELSE: then ends with JMP join
                    if (constrainedBrIdx > i + 1) {
                        val lastThenIdx = constrainedBrIdx - 1
                        val lastThen = layout[lastThenIdx]
                        if (lastThen.isUnconditionalJmp()) {
                            val join = lastThen.branchExit
                            val joinIdx = join?.let { indexOf[it] }
                            if (joinIdx != null && joinIdx > constrainedBrIdx && joinIdx <= endExclusive) {
                                // then = (i+1 .. lastThenIdx) but omit a pure-JMP tail block if it contains only the JMP
                                val pureJmp = lastThen.lines.count { it.instruction != null } == 1
                                val thenEnd = if (pureJmp) lastThenIdx else lastThenIdx + 1
                                val thenNodes = buildRange(i + 1, thenEnd)
                                val elseNodes = buildRange(constrainedBrIdx, joinIdx)
                                val cond = Condition(
                                    branchBlock = b,
                                    branchLine = last!!,
                                    sense = false, // branch-taken goes to else; fall-through is THEN
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
                    // When branch target is outside current range, constrain then-branch to endExclusive
                    val effectiveThenEnd = if (hasOutOfRangeBranch) endExclusive else constrainedBrIdx
                    val thenNodes = buildRange(i + 1, effectiveThenEnd)
                    val cond = Condition(
                        branchBlock = b,
                        branchLine = last!!,
                        sense = false, // branch-taken skips THEN to join
                    )

                    // Determine join block - use out-of-range target if no in-range target
                    val branchTargetBlock = if (hasOutOfRangeBranch) layout[brIdx] else layout[constrainedBrIdx]
                    val branchTargetIdx = indexOf[branchTargetBlock] ?: brIdx

                    // Check if branch target returns (ends execution) AND is an alternative path
                    // Only treat as else if the branch target is NOT where the then-branch naturally ends
                    // (i.e., it's a separate code path, not a join point)
                    val branchTargetReturns = branchTargetBlock.isReturnLike()
                    val isAlternativePath = branchTargetIdx != effectiveThenEnd

                    if (branchTargetReturns && isAlternativePath) {
                        // The branch target is an alternative path that returns, not a reconvergence point
                        // Include it as the else branch along with any subsequent return blocks
                        // Find how many blocks after the branch target also return (part of else path)
                        var elseEnd = branchTargetIdx + 1
                        // Don't extend else beyond endExclusive for the outer scope
                        while (elseEnd < layout.size && layout[elseEnd].isReturnLike()) {
                            elseEnd++
                        }
                        val elseNodes = buildRange(branchTargetIdx, minOf(elseEnd, layout.size))

                        out.add(
                            IfNode(
                                id = nextId++,
                                condition = cond,
                                thenBranch = thenNodes,
                                elseBranch = elseNodes,
                                join = null
                            )
                        )
                        // Skip past both then and else ranges
                        i = maxOf(effectiveThenEnd, elseEnd)
                    } else {
                        out.add(
                            IfNode(
                                id = nextId++,
                                condition = cond,
                                thenBranch = thenNodes,
                                elseBranch = mutableListOf(),
                                join = branchTargetBlock
                            )
                        )
                        i = effectiveThenEnd
                    }
                    continue
                }
            }

            // Post-test loop: body executes first, ends with conditional branch back to start
            // Pattern: simple do-while where body is a linear sequence
            // IMPORTANT: Only detect PostTest if the loop body is simple/linear.
            // Complex backward branches (like tail recursion or PreTest loops with bottom condition)
            // should be handled by other patterns or left as goto.
            if (b.isConditional()) {
                val ft = b.fallThroughExit
                val br = b.branchExit
                val ftIdx = ft?.let { indexOf[it] } ?: -1
                val brIdx = br?.let { indexOf[it] } ?: -1

                // Check if branch goes backward (to earlier block in layout) - includes self-loops
                if (brIdx >= 0 && brIdx <= i) {
                    val loopHeader = layout[brIdx]
                    val exitBlock = ft // fall-through is the exit

                    // CRITICAL FIX: Skip if the loop header is already being processed by natural loop detection
                    // This prevents creating duplicate nested loops when we're inside a natural loop's body
                    if (loopHeader in processingLoopHeaders) {
                        // Don't create a nested loop - this block is part of an outer loop's body
                        // Fall through to just add as BlockNode
                    } else {
                    // Check if the "loop header" immediately exits via JMP - if so, this isn't a loop
                    // It's a conditional branch that exits the function (e.g., BEQ NoJump / NoJump: JMP X_Physics)
                    val headerExitsViaJmp = loopHeader.lines.any { line ->
                        line.instruction?.op == AssemblyOp.JMP
                    }
                    if (headerExitsViaJmp) {
                        // This isn't a loop - the target immediately exits
                        // Let it fall through to be handled as an if statement or goto
                    } else {
                    // Heuristic: Only treat as PostTest loop if:
                    // 1. The span is small enough to be a simple do-while (< 10 blocks)
                    // 2. If jumping back to start, only accept if span is very small (simple do-while)
                    val loopSpan = i - brIdx + 1
                    val isJumpToFunctionStart = brIdx == start
                    val isLargeSpan = loopSpan > 10
                    val isSimpleDoWhile = loopSpan <= 3  // Very simple loop

                    val shouldDetectAsPostTest = if (isJumpToFunctionStart) {
                        // If jumping to function start, only treat as loop if it's a simple do-while
                        isSimpleDoWhile
                    } else {
                        // Otherwise, accept if not too large
                        !isLargeSpan
                    }

                    if (shouldDetectAsPostTest) {
                        // Simple PostTest loop
                        val bodyNodes: MutableList<ControlNode> = (brIdx..i).map { idx ->
                            BlockNode(id = nextId++, block = layout[idx])
                        }.toMutableList()

                        val cond = Condition(
                            branchBlock = b,
                            branchLine = b.lastInstructionLine()!!,
                            sense = true, // branch-taken continues loop
                        )
                        out.add(
                            LoopNode(
                                id = nextId++,
                                kind = LoopKind.PostTest,
                                header = loopHeader,
                                condition = cond,
                                body = bodyNodes,
                                continueTargets = setOf(loopHeader),
                                breakTargets = exitBlock?.let { setOf(it) } ?: emptySet()
                            )
                        )
                        i++
                        continue
                    }
                    // Otherwise, let it fall through to be handled as blocks/gotos
                    } // end else (not an immediate JMP exit)
                    } // end else (loopHeader not in processingLoopHeaders)
                }

                // Check if fall-through goes backward (branch is exit) - includes self-loops
                if (ftIdx >= 0 && ftIdx <= i) {
                    val loopHeader = layout[ftIdx]
                    val exitBlock = br // branch is the exit

                    // CRITICAL FIX: Skip if the loop header is already being processed by natural loop detection
                    if (loopHeader in processingLoopHeaders) {
                        // Don't create a nested loop - this block is part of an outer loop's body
                        // Fall through to just add as BlockNode
                    } else {
                    // Same heuristic as above
                    val loopSpan = i - ftIdx + 1
                    val isJumpToFunctionStart = ftIdx == start
                    val isLargeSpan = loopSpan > 10
                    val isSimpleDoWhile = loopSpan <= 3

                    val shouldDetectAsPostTest = if (isJumpToFunctionStart) {
                        isSimpleDoWhile
                    } else {
                        !isLargeSpan
                    }

                    if (shouldDetectAsPostTest) {
                        val bodyNodes: MutableList<ControlNode> = (ftIdx..i).map { idx ->
                            BlockNode(id = nextId++, block = layout[idx])
                        }.toMutableList()

                        val cond = Condition(
                            branchBlock = b,
                            branchLine = b.lastInstructionLine()!!,
                            sense = false, // fall-through continues loop
                        )
                        out.add(
                            LoopNode(
                                id = nextId++,
                                kind = LoopKind.PostTest,
                                header = loopHeader,
                                condition = cond,
                                body = bodyNodes,
                                continueTargets = setOf(loopHeader),
                                breakTargets = exitBlock?.let { setOf(it) } ?: emptySet()
                            )
                        )
                        i++
                        continue
                    }
                    // Otherwise, let it fall through
                    } // end else (loopHeader not in processingLoopHeaders)
                }
            }

            // Infinite loop: unconditional JMP backward
            if (b.isUnconditionalJmp()) {
                val target = b.branchExit
                val targetIdx = target?.let { indexOf[it] } ?: -1

                // Jump backward = infinite loop (includes self-loops)
                if (targetIdx >= 0 && targetIdx <= i) {
                    val loopHeader = layout[targetIdx]
                    // Body is from targetIdx to i (inclusive) - just wrap as BlockNodes
                    val bodyNodes: MutableList<ControlNode> = (targetIdx..i).map { idx ->
                        BlockNode(id = nextId++, block = layout[idx])
                    }.toMutableList()

                    out.add(
                        LoopNode(
                            id = nextId++,
                            kind = LoopKind.Infinite,
                            header = loopHeader,
                            condition = null, // no exit condition
                            body = bodyNodes,
                            continueTargets = setOf(loopHeader),
                            breakTargets = emptySet() // no normal exit
                        )
                    )
                    i++
                    continue
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
