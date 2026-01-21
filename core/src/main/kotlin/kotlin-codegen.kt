package com.ivieleague.decompiler6502tokotlin.hand

/**
 * Code generation: Convert control flow structures and 6502 instructions to Kotlin AST.
 *
 * This is the main code generation pipeline that produces actual Kotlin code from
 * the analyzed assembly structure.
 */

/**
 * Represents a JumpEngine dispatch table at a specific location.
 * The JumpEngine pattern in 6502 uses the return address to index into a jump table
 * immediately following the JSR instruction.
 */
data class JumpEngineTable(
    val lineIndex: Int,
    val targets: List<String>  // Function names in order (index 0, 1, 2, ...)
)

/**
 * Detect JumpEngine patterns in parsed assembly and build a registry.
 * Scans for JSR JumpEngine followed by DATA lines with Expr entries.
 */
fun detectJumpEngineTables(lines: List<AssemblyLine>): Map<Int, JumpEngineTable> {
    val tables = mutableMapOf<Int, JumpEngineTable>()

    var i = 0
    while (i < lines.size) {
        val line = lines[i]
        val instruction = line.instruction

        // Check for JSR JumpEngine
        if (instruction?.op == AssemblyOp.JSR) {
            val targetLabel = (instruction.address as? AssemblyAddressing.Direct)?.label
            if (targetLabel == "JumpEngine") {
                // Collect DATA lines that follow
                val targets = mutableListOf<String>()
                var j = i + 1
                while (j < lines.size) {
                    val dataLine = lines[j]
                    val data = dataLine.data
                    if (data is AssemblyData.Db) {
                        // Extract function names from Expr items
                        for (item in data.items) {
                            if (item is AssemblyData.DbItem.Expr) {
                                // expr is like "TitleScreenMode" or "<TitleScreenMode" or ">TitleScreenMode"
                                val funcName = item.expr
                                    .removePrefix("<")
                                    .removePrefix(">")
                                    .trim()
                                if (funcName.isNotEmpty() && funcName.first().isLetter()) {
                                    targets.add(funcName)
                                }
                            }
                        }
                        j++
                    } else if (dataLine.instruction != null || dataLine.label != null) {
                        // Hit code or a new label - stop collecting
                        break
                    } else {
                        j++
                    }
                }

                if (targets.isNotEmpty()) {
                    // Each DATA line with an Expr is already a full function reference
                    // (.dw directive in the original assembly creates word entries)
                    tables[line.originalLineIndex] = JumpEngineTable(line.originalLineIndex, targets)
                }
            }
        }
        i++
    }

    return tables
}

/**
 * Context for code generation - tracks state during conversion.
 */
class CodeGenContext(
    /** Function registry to look up function signatures for call sites */
    val functionRegistry: Map<String, AssemblyFunction> = emptyMap(),
    /** JumpEngine dispatch tables, keyed by line index */
    val jumpEngineTables: Map<Int, JumpEngineTable> = emptyMap(),
    /** by Claude - The current function being decompiled, used for function-specific behavior */
    val currentFunction: AssemblyFunction? = null,
    /** by Claude - Fall-through graph for cycle detection (prevents mutual recursion in tail calls) */
    val fallThroughGraph: Map<AssemblyFunction, Set<AssemblyFunction>> = emptyMap()
) {
    /** Memory model: tracks which addresses are variables vs raw memory */
    val memory = mutableMapOf<Int, String>() // address -> variable name

    /** Register state tracker */
    var registerA: KotlinExpr? = null
    var registerX: KotlinExpr? = null
    var registerY: KotlinExpr? = null

    /** Flag state tracker */
    var zeroFlag: KotlinExpr? = null
    var carryFlag: KotlinExpr? = null
    var negativeFlag: KotlinExpr? = null
    var overflowFlag: KotlinExpr? = null

    /** Generated variable counter */
    private var tempVarCounter = 0

    fun nextTempVar(): String = "temp${tempVarCounter++}"

    // by Claude - Counter for Pair result variables (uses "pair" prefix to avoid Int hoisting)
    private var pairVarCounter = 0

    /** Get a unique variable name for storing a Pair result. Uses "pair" prefix to avoid hoisting as Int. */
    fun nextPairVar(): String = "pair${pairVarCounter++}"

    // by Claude - Counter for register-tracking variables (uses "reg" prefix to avoid hoisting as Int = 0)
    // These are used when a register needs to be captured into a variable that preserves its current value
    private var regVarCounter = 0

    /** Get a unique variable name for register tracking. Uses "reg" prefix to avoid hoisting. */
    fun nextRegVar(): String = "reg${regVarCounter++}"

    // by Claude - Counter for temporary flag variables (used to capture carry before shift operations)
    private var flagVarCounter = 0

    /** Get a unique variable name for flag tracking. */
    fun nextFlagVar(): String = "${flagVarCounter++}"

    /** Track which blocks have been converted to avoid duplicates */
    val convertedBlocks = mutableSetOf<AssemblyBlock>()

    // by Claude - Track whether we're converting a block that's part of a structured control flow
    // (e.g., the branch block of an IfNode). When true, skip the orphaned branch handler because
    // the branch is already handled by the control structure (IfNode/LoopNode).
    var isInsideStructuredBranch: Boolean = false

    // by Claude - Stack of enclosing loops for labeled break support
    // Each entry is (loopLabel, breakTargets) where breakTargets are blocks that exit the loop
    private var loopLabelCounter = 0
    val loopStack = mutableListOf<Pair<String, Set<AssemblyBlock>>>()

    /** Push a new loop onto the stack and return its label */
    fun pushLoop(breakTargets: Set<AssemblyBlock>): String {
        val label = "loop${loopLabelCounter++}"
        loopStack.add(label to breakTargets)
        return label
    }

    /** Pop the current loop from the stack */
    fun popLoop() {
        if (loopStack.isNotEmpty()) loopStack.removeLast()
    }

    /** Find the label for a break target block. Returns null if not a break target. */
    // by Claude - IMPORTANT: We need to find the OUTERMOST loop that has this target as a break target.
    // This handles nested loops where an inner branch exits BOTH loops (like beq RendBBuf).
    // If we returned the innermost loop, break@innerLoop wouldn't exit the outer loop.
    fun findBreakLabel(target: AssemblyBlock): String? {
        // Search from OUTERMOST to innermost loop - return the first (outermost) match
        for (i in loopStack.indices) {
            val (label, breakTargets) = loopStack[i]
            if (target in breakTargets) return label
        }
        return null
    }

    /** Check if a block is a break target for ANY enclosing loop */
    fun isBreakTarget(target: AssemblyBlock): Boolean = findBreakLabel(target) != null

    // ===== DELEGATE MEMORY ACCESS TRACKING =====

    /** Track direct memory accesses: constant -> property name */
    val directMemoryAccesses = mutableMapOf<String, String>() // CONSTANT -> propertyName

    /** Track indexed memory accesses: constant -> property name */
    val indexedMemoryAccesses = mutableMapOf<String, String>() // CONSTANT -> propertyName

    /**
     * Convert label names to camelCase property names.
     * Examples:
     *   Screen_Timer -> screenTimer
     *   Enemy_Flag -> enemyFlag
     *   ScreenTimer -> screenTimer
     *   PPU -> ppu
     */
    private fun toCamelCase(name: String): String {
        // If it has underscores, use snake_case conversion
        // Screen_Timer -> screenTimer
        if ('_' in name) {
            val parts = name.lowercase().split('_')
            return parts.mapIndexed { index, part ->
                if (index == 0) part else part.replaceFirstChar { it.uppercase() }
            }.joinToString("")
        }

        // No underscores - just lowercase the first character
        // ScreenTimer -> screenTimer
        // PPU -> ppu
        return name.replaceFirstChar { it.lowercase() }
    }

    /**
     * Register a direct memory access and return the property name.
     * Example: registerDirectAccess("ScreenTimer") -> "screenTimer"
     */
    fun registerDirectAccess(constant: String): String {
        return directMemoryAccesses.getOrPut(constant) {
            toCamelCase(constant)
        }
    }

    /**
     * Register an indexed memory access and return the property name.
     * Example: registerIndexedAccess("Enemy_Flag") -> "enemyFlag"
     *
     * If the label was previously registered as a direct access, it gets upgraded
     * to an indexed access (the direct access is removed).
     */
    fun registerIndexedAccess(constant: String): String {
        // If we previously registered this as a direct access, remove it - indexed takes precedence
        directMemoryAccesses.remove(constant)
        return indexedMemoryAccesses.getOrPut(constant) {
            toCamelCase(constant)
        }
    }

    /**
     * Function-level register variables - once created, these are reused across all branches.
     * This ensures that after merges, we can restore a valid variable reference instead of null.
     */
    var functionLevelA: String? = null
    var functionLevelX: String? = null
    var functionLevelY: String? = null

    /**
     * Get or create a function-level variable for a register.
     * Returns the variable name and whether it was newly created.
     */
    // by Claude - Return the register name directly since A, X, Y are already declared at function start
    // (either as parameters or as local vars initialized to 0)
    // This avoids scope issues where regN vars created inside if-bodies aren't visible outside
    fun getOrCreateFunctionLevelVar(register: String): Pair<String, Boolean> {
        return when (register) {
            "A" -> {
                if (functionLevelA == null) {
                    functionLevelA = register  // Use "A" directly
                    Pair(register, false)  // false = don't declare inline, it's already declared
                } else {
                    Pair(functionLevelA!!, false)
                }
            }
            "X" -> {
                if (functionLevelX == null) {
                    functionLevelX = register  // Use "X" directly
                    Pair(register, false)
                } else {
                    Pair(functionLevelX!!, false)
                }
            }
            "Y" -> {
                if (functionLevelY == null) {
                    functionLevelY = register  // Use "Y" directly
                    Pair(register, false)
                } else {
                    Pair(functionLevelY!!, false)
                }
            }
            else -> Pair("unknown", false)
        }
    }

    /**
     * Get the function-level variable for a register, if one exists.
     */
    fun getFunctionLevelVar(register: String): KVar? {
        return when (register) {
            "A" -> functionLevelA?.let { KVar(it) }
            "X" -> functionLevelX?.let { KVar(it) }
            "Y" -> functionLevelY?.let { KVar(it) }
            else -> null
        }
    }

    /** Save register/flag state for branch handling */
    data class SavedState(
        val registerA: KotlinExpr?,
        val registerX: KotlinExpr?,
        val registerY: KotlinExpr?,
        val zeroFlag: KotlinExpr?,
        val carryFlag: KotlinExpr?,
        val negativeFlag: KotlinExpr?,
        val overflowFlag: KotlinExpr?
    )

    fun saveState(): SavedState = SavedState(
        registerA, registerX, registerY,
        zeroFlag, carryFlag, negativeFlag, overflowFlag
    )

    fun restoreState(saved: SavedState) {
        registerA = saved.registerA
        registerX = saved.registerX
        registerY = saved.registerY
        zeroFlag = saved.zeroFlag
        carryFlag = saved.carryFlag
        negativeFlag = saved.negativeFlag
        overflowFlag = saved.overflowFlag
    }

    /**
     * Merge two states after a branch.
     * If values differ, use the function-level variable if available, otherwise set to null.
     */
    fun mergeStates(thenState: SavedState, elseState: SavedState) {
        registerA = when {
            thenState.registerA == elseState.registerA -> thenState.registerA
            // If both are KVar referencing the same function-level variable, keep it
            thenState.registerA is KVar && elseState.registerA is KVar &&
                (thenState.registerA as KVar).name == functionLevelA -> thenState.registerA
            // Otherwise, restore function-level var if it exists
            else -> getFunctionLevelVar("A")
        }
        registerX = when {
            thenState.registerX == elseState.registerX -> thenState.registerX
            thenState.registerX is KVar && elseState.registerX is KVar &&
                (thenState.registerX as KVar).name == functionLevelX -> thenState.registerX
            else -> getFunctionLevelVar("X")
        }
        registerY = when {
            thenState.registerY == elseState.registerY -> thenState.registerY
            thenState.registerY is KVar && elseState.registerY is KVar &&
                (thenState.registerY as KVar).name == functionLevelY -> thenState.registerY
            else -> getFunctionLevelVar("Y")
        }
        // Flags are less critical - just keep same if equal, otherwise null
        zeroFlag = if (thenState.zeroFlag == elseState.zeroFlag) thenState.zeroFlag else null
        carryFlag = if (thenState.carryFlag == elseState.carryFlag) thenState.carryFlag else null
        negativeFlag = if (thenState.negativeFlag == elseState.negativeFlag) thenState.negativeFlag else null
        overflowFlag = if (thenState.overflowFlag == elseState.overflowFlag) thenState.overflowFlag else null
    }

    /**
     * Materialize a register if it contains any value that needs to be captured before branches.
     * Returns statements to emit (variable declaration or assignment) and updates the register to reference the variable.
     *
     * Uses function-level variables to ensure all branches update the same variable.
     * If a function-level var already exists, emits an assignment instead of a declaration.
     */
    fun materializeRegister(register: String, mutable: Boolean = false): List<KotlinStmt> {
        val expr = when (register) {
            "A" -> registerA
            "X" -> registerX
            "Y" -> registerY
            else -> return emptyList()
        }

        // Don't materialize if null
        if (expr == null) {
            return emptyList()
        }

        // Check if we already have a function-level var for this register
        val existingVar = getFunctionLevelVar(register)

        // If the register already points to the function-level var, nothing to do
        if (expr is KVar && existingVar != null && expr.name == existingVar.name) {
            return emptyList()
        }

        // Get or create function-level variable
        val (varName, isNew) = getOrCreateFunctionLevelVar(register)
        val varRef = KVar(varName)

        // Update the register to point to this variable
        when (register) {
            "A" -> registerA = varRef
            "X" -> registerX = varRef
            "Y" -> registerY = varRef
        }

        // If new, emit declaration; otherwise emit assignment
        return if (isNew) {
            listOf(KVarDecl(varName, "Int", expr, mutable = true))  // Always mutable for function-level vars
        } else {
            listOf(KAssignment(varRef, expr))
        }
    }

    /**
     * Materialize all registers that have complex expressions.
     * Call this before branches to ensure register values are captured in mutable variables
     * that can be updated in the branches.
     */
    fun materializeAllRegisters(): List<KotlinStmt> {
        val stmts = mutableListOf<KotlinStmt>()
        // Use mutable=true since branches may modify these registers
        stmts.addAll(materializeRegister("A", mutable = true))
        stmts.addAll(materializeRegister("X", mutable = true))
        stmts.addAll(materializeRegister("Y", mutable = true))
        return stmts
    }

    /**
     * Check if a register is null (unknown) and needs initialization.
     * Used to detect when we're reading a register that was never set.
     */
    fun isRegisterUnknown(register: String): Boolean {
        return when (register) {
            "A" -> registerA == null
            "X" -> registerX == null
            "Y" -> registerY == null
            else -> true
        }
    }
}

// AssemblyInstruction.toKotlin() moved to instruction-handlers.kt

// by Claude - Cycle detection for fall-through tail calls
// This prevents mutual recursion when function A falls through to B and B falls through to A.
// In assembly, these share common code that eventually reaches RTS, not actual recursive calls.

/**
 * Build a graph of fall-through relationships between functions.
 * Returns a map from function to the set of functions it falls through to.
 */
fun buildFallThroughGraph(functions: Collection<AssemblyFunction>): Map<AssemblyFunction, Set<AssemblyFunction>> {
    val graph = mutableMapOf<AssemblyFunction, MutableSet<AssemblyFunction>>()

    for (func in functions) {
        // Find all blocks belonging to this function
        val functionBlocks = mutableSetOf<AssemblyBlock>()
        val toVisit = mutableListOf(func.startingBlock)
        while (toVisit.isNotEmpty()) {
            val block = toVisit.removeAt(0)
            if (!functionBlocks.add(block)) continue
            if (block.function == func) {
                block.fallThroughExit?.let { if (it.function == func) toVisit.add(it) }
                block.branchExit?.let { if (it.function == func) toVisit.add(it) }
            }
        }

        // Find exit blocks that fall through to another function
        for (block in functionBlocks) {
            val fallThrough = block.fallThroughExit
            if (fallThrough != null && fallThrough.function != func && fallThrough.function != null) {
                graph.getOrPut(func) { mutableSetOf() }.add(fallThrough.function!!)
            }
        }
    }

    return graph
}

/**
 * Check if adding a fall-through tail call from sourceFunc to targetFunc would create a cycle.
 * A cycle exists if we can follow fall-through relationships from targetFunc back to sourceFunc.
 */
fun wouldCreateFallThroughCycle(
    sourceFunc: AssemblyFunction,
    targetFunc: AssemblyFunction,
    fallThroughGraph: Map<AssemblyFunction, Set<AssemblyFunction>>
): Boolean {
    // BFS from targetFunc to see if we can reach sourceFunc
    val visited = mutableSetOf<AssemblyFunction>()
    val queue = ArrayDeque<AssemblyFunction>()
    queue.add(targetFunc)

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        if (current == sourceFunc) {
            return true  // Found a path back to source - this would create a cycle
        }
        if (!visited.add(current)) continue

        // Add all functions that current falls through to
        fallThroughGraph[current]?.forEach { next ->
            if (next !in visited) {
                queue.add(next)
            }
        }
    }

    return false  // No cycle detected
}

/**
 * Convert a ControlNode to Kotlin statements.
 */
fun ControlNode.toKotlin(ctx: CodeGenContext): List<KotlinStmt> {
    return when (this) {
        is BlockNode -> {
            this.block.toKotlin(ctx)
        }

        is IfNode -> {
            val result = mutableListOf<KotlinStmt>()

            // Include the statements from the block containing the branch
            // (the instructions before the branch instruction)
            // by Claude - Set flag to indicate branch is handled by this IfNode structure
            // This prevents the orphaned branch handler from generating duplicate if-return logic
            val branchBlock = this.condition.branchBlock
            val wasInsideStructuredBranch = ctx.isInsideStructuredBranch
            ctx.isInsideStructuredBranch = true
            result.addAll(branchBlock.toKotlin(ctx))
            ctx.isInsideStructuredBranch = wasInsideStructuredBranch

            // Materialize any complex register expressions before branching
            // This ensures we capture values into named variables before they can be lost
            result.addAll(ctx.materializeAllRegisters())

            // Save state before branches - each branch starts from the same state
            val stateBeforeBranch = ctx.saveState()

            // by Claude - Bug fix: Save convertedBlocks state before branches.
            // Each branch should convert its blocks independently, because a block may be
            // reachable from MULTIPLE control flow paths. If block B is in both thenBranch
            // and elseBranch (as a join point or shared continuation), each branch needs
            // to convert it independently. Otherwise, the second branch gets an empty body.
            // Example: ProcessAreaData's RdyDecode block is reached from both "bne RdyDecode"
            // and "bcc SetBehind" fall-through - both paths need the RdyDecode code.
            val convertedBlocksBeforeBranch = ctx.convertedBlocks.toSet()

            // Then add the if statement - process branches independently
            val condition = this.condition.toKotlinExpr(ctx)

            // Process then branch
            ctx.restoreState(stateBeforeBranch)
            ctx.convertedBlocks.clear()
            ctx.convertedBlocks.addAll(convertedBlocksBeforeBranch)
            val thenStmts = this.thenBranch.flatMap { it.toKotlin(ctx) }
            val thenState = ctx.saveState()
            val thenConvertedBlocks = ctx.convertedBlocks.toSet()

            // Process else branch (starting fresh from pre-branch state)
            ctx.restoreState(stateBeforeBranch)
            ctx.convertedBlocks.clear()
            ctx.convertedBlocks.addAll(convertedBlocksBeforeBranch)
            var elseStmts = this.elseBranch.flatMap { it.toKotlin(ctx) }.toMutableList()
            val elseState = ctx.saveState()
            val elseConvertedBlocks = ctx.convertedBlocks.toSet()

            // by Claude - Check if branch target is a break target for an enclosing loop
            // This handles nested loop breaks like: beq RendBBuf (where RendBBuf is outside both loops)
            // When elseBranch is empty and the branch would go to a loop's break target,
            // we need to generate a labeled break instead of just skipping the then-branch.
            if (elseStmts.isEmpty() && !this.condition.sense) {
                // sense=false means branch-taken skips the then-branch
                // Check if branch target is a break target
                val branchTarget = branchBlock.branchExit
                if (branchTarget != null) {
                    val breakLabel = ctx.findBreakLabel(branchTarget)
                    if (breakLabel != null) {
                        // Add labeled break to else branch
                        elseStmts.add(KLabeledBreak(breakLabel))
                    }
                }
            }

            // by Claude - Merge convertedBlocks: blocks converted in EITHER branch should
            // be marked as converted for the code AFTER the if-else (the join point code).
            // This prevents duplicate conversion of join point blocks.
            ctx.convertedBlocks.clear()
            ctx.convertedBlocks.addAll(convertedBlocksBeforeBranch)
            ctx.convertedBlocks.addAll(thenConvertedBlocks)
            ctx.convertedBlocks.addAll(elseConvertedBlocks)

            // by Claude - Smart merge: if one branch terminates (return), use the other branch's state
            // This preserves flags from the continuing branch for loop conditions
            val thenTerminates = thenStmts.lastOrNull()?.isTerminating() == true
            val elseTerminates = elseStmts.lastOrNull()?.isTerminating() == true

            when {
                thenTerminates && !elseTerminates -> ctx.restoreState(elseState)
                elseTerminates && !thenTerminates -> ctx.restoreState(thenState)
                else -> ctx.mergeStates(thenState, elseState) // Both continue or both terminate
            }

            result.add(KIf(condition, thenStmts, elseStmts))

            result
        }

        is LoopNode -> {
            val result = mutableListOf<KotlinStmt>()

            // Materialize any literal register values before the loop body
            // This ensures loop counters are proper variables that can be updated
            result.addAll(ctx.materializeRegister("X", mutable = true))
            result.addAll(ctx.materializeRegister("Y", mutable = true))

            // by Claude - Push loop onto stack for labeled break support
            // If this loop has break targets, we may need labeled breaks from inner code
            val loopLabel = ctx.pushLoop(this.breakTargets)
            val needsLabel = this.breakTargets.isNotEmpty()

            // by Claude - CRITICAL FIX: For PreTest loops (while loops), save the context state
            // BEFORE processing the body. This is because the loop condition should be evaluated
            // using the pre-loop state, not the state after the body has been processed.
            // This fixes issues where temp variables (like carryFromLsr2) are referenced in the
            // loop condition but declared inside the loop body.
            val preBodyState = if (this.kind == LoopKind.PreTest) ctx.saveState() else null

            val bodyStmts = this.body.flatMap { it.toKotlin(ctx) }

            // by Claude - Pop loop from stack after body is processed
            ctx.popLoop()

            val loop = when (this.kind) {
                LoopKind.PreTest -> {
                    // by Claude - Restore pre-body state for condition evaluation
                    // This ensures the condition uses variables that exist BEFORE the loop body
                    preBodyState?.let { ctx.restoreState(it) }
                    val condition = this.condition?.toKotlinExpr(ctx) ?: KLiteral("true")
                    if (needsLabel) KLabeledWhile(loopLabel, condition, bodyStmts)
                    else KWhile(condition, bodyStmts)
                }
                LoopKind.PostTest -> {
                    val condition = this.condition?.toKotlinExpr(ctx) ?: KLiteral("true")
                    if (needsLabel) KLabeledDoWhile(loopLabel, bodyStmts, condition)
                    else KDoWhile(bodyStmts, condition)
                }
                LoopKind.Infinite -> {
                    if (needsLabel) KLabeledLoop(loopLabel, bodyStmts)
                    else KLoop(bodyStmts)
                }
            }

            result.add(loop)
            result
        }

        is GotoNode -> {
            listOf(KComment("goto ${this.to.label ?: "@${this.to.originalLineIndex}"}"))
        }

        is BreakNode -> {
            listOf(KBreak)
        }

        is ContinueNode -> {
            listOf(KContinue)
        }

        is SwitchNode -> {
            // Convert switch/jump table to Kotlin when expression
            val selectorExpr = this.selector.toKotlinExpr()

            // Convert each case
            val branches = this.cases.map { case ->
                val values = case.matchValues.map { KLiteral("0x${it.toString(16).uppercase()}") }
                val body = case.nodes.flatMap { it.toKotlin(ctx) }
                KWhenBranch(values, body)
            }

            // Convert default branch
            val defaultBody = this.defaultBranch.flatMap { it.toKotlin(ctx) }

            listOf(KWhen(selectorExpr, branches, defaultBody))
        }
    }
}

/**
 * Simplify a Kotlin expression for readability.
 *
 * Patterns simplified:
 * - `!(a - b == 0)` → `a != b`
 * - `!(a == 0)` → `a != 0`
 * - `a - b == 0` → `a == b`
 * - `!(a != b)` → `a == b`
 * - `!(a == b)` → `a != b`
 * - `a - 0x00` → `a`
 */
fun KotlinExpr.simplify(): KotlinExpr {
    return when (this) {
        is KUnaryOp -> {
            val simplifiedInner = expr.simplify()
            when {
                // !(a == b) → a != b
                op == "!" && simplifiedInner is KBinaryOp && simplifiedInner.op == "==" -> {
                    KBinaryOp(simplifiedInner.left.simplify(), "!=", simplifiedInner.right.simplify())
                }
                // !(a != b) → a == b
                op == "!" && simplifiedInner is KBinaryOp && simplifiedInner.op == "!=" -> {
                    KBinaryOp(simplifiedInner.left.simplify(), "==", simplifiedInner.right.simplify())
                }
                // Propagate simplification
                else -> KUnaryOp(op, simplifiedInner, postfix)
            }
        }
        is KBinaryOp -> {
            val simplifiedLeft = left.simplify()
            val simplifiedRight = right.simplify()
            when {
                // (a - b) == 0 → a == b
                op == "==" && simplifiedRight.isZero() && simplifiedLeft is KBinaryOp && simplifiedLeft.op == "-" -> {
                    KBinaryOp(simplifiedLeft.left.simplify(), "==", simplifiedLeft.right.simplify())
                }
                // 0 == (a - b) → a == b
                op == "==" && simplifiedLeft.isZero() && simplifiedRight is KBinaryOp && simplifiedRight.op == "-" -> {
                    KBinaryOp(simplifiedRight.left.simplify(), "==", simplifiedRight.right.simplify())
                }
                // (a - b) != 0 → a != b
                op == "!=" && simplifiedRight.isZero() && simplifiedLeft is KBinaryOp && simplifiedLeft.op == "-" -> {
                    KBinaryOp(simplifiedLeft.left.simplify(), "!=", simplifiedLeft.right.simplify())
                }
                // a - 0 → a (remove useless subtraction)
                op == "-" && simplifiedRight.isZero() -> {
                    simplifiedLeft
                }
                // a + 0 → a (remove useless addition)
                op == "+" && simplifiedRight.isZero() -> {
                    simplifiedLeft
                }
                // 0 + a → a
                op == "+" && simplifiedLeft.isZero() -> {
                    simplifiedRight
                }
                else -> KBinaryOp(simplifiedLeft, op, simplifiedRight)
            }
        }
        is KParen -> {
            val simplified = expr.simplify()
            // Only keep parens if still needed
            if (simplified is KBinaryOp || simplified is KUnaryOp) KParen(simplified) else simplified
        }
        is KMemberAccess -> {
            KMemberAccess(receiver.simplify(), member.simplify(), isIndexed)
        }
        is KCall -> {
            KCall(name, args.map { it.simplify() })
        }
        is KCast -> {
            KCast(expr.simplify(), type)
        }
        is KIfExpr -> {
            KIfExpr(condition.simplify(), thenExpr.simplify(), elseExpr.simplify())
        }
        // Literals and variables don't need simplification
        else -> this
    }
}

/**
 * Check if an expression represents zero.
 */
fun KotlinExpr.isZero(): Boolean {
    return when (this) {
        is KLiteral -> value == "0" || value == "0x00" || value == "0x0" || value.lowercase() == "0x00"
        else -> false
    }
}

/**
 * Convert a Condition to a Kotlin expression.
 * Uses the current context's flag state which should have been set by processing the block.
 */
// by Claude - Improved fallback logic when flags are null - look at instruction before branch
fun Condition.toKotlinExpr(ctx: CodeGenContext): KotlinExpr {
    // Get the branch instruction
    val branchInstr = this.branchLine.instruction ?: return KLiteral("/* unknown condition */")

    // by Claude - Helper to find the flag-setting instruction before the branch
    // This is used as fallback when ctx flags are null (e.g., after if-else merge)
    fun findFlagSettingInstruction(): AssemblyInstruction? {
        val lines = this.branchBlock.lines
        val branchIdx = lines.indexOfLast { it.instruction?.op?.isBranch == true }
        if (branchIdx <= 0) return null
        // Look backwards for a flag-setting instruction
        for (i in (branchIdx - 1) downTo 0) {
            val instr = lines[i].instruction ?: continue
            // CMP, CPX, CPY, and most ALU ops set flags
            if (instr.op in listOf(AssemblyOp.CMP, AssemblyOp.CPX, AssemblyOp.CPY,
                    AssemblyOp.LDA, AssemblyOp.LDX, AssemblyOp.LDY,
                    AssemblyOp.AND, AssemblyOp.ORA, AssemblyOp.EOR,
                    AssemblyOp.INX, AssemblyOp.INY, AssemblyOp.DEX, AssemblyOp.DEY,
                    AssemblyOp.TAX, AssemblyOp.TAY, AssemblyOp.TXA, AssemblyOp.TYA,
                    AssemblyOp.ADC, AssemblyOp.SBC, AssemblyOp.BIT)) {
                return instr
            }
        }
        return null
    }

    // by Claude - Build zero flag expression from a flag-setting instruction
    fun buildZeroFlagFromInstruction(instr: AssemblyInstruction): KotlinExpr? {
        // Parse immediate operand - supports ByteValue (most common for comparison)
        fun parseOperand(): KotlinExpr? {
            return when (val addr = instr.address) {
                is AssemblyAddressing.ByteValue -> {
                    KLiteral("0x${addr.value.toString(16).uppercase().padStart(2, '0')}")
                }
                is AssemblyAddressing.ShortValue -> {
                    KLiteral("0x${addr.value.toString(16).uppercase().padStart(4, '0')}")
                }
                is AssemblyAddressing.ConstantReference -> KVar(addr.name)
                else -> null
            }
        }
        val operand = parseOperand()

        return when (instr.op) {
            AssemblyOp.CMP -> {
                // CMP sets Z if A == operand
                val reg = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
                if (operand != null) KBinaryOp(reg, "==", operand) else null
            }
            AssemblyOp.CPX -> {
                // CPX sets Z if X == operand
                val reg = ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X")
                if (operand != null) KBinaryOp(reg, "==", operand) else null
            }
            AssemblyOp.CPY -> {
                // CPY sets Z if Y == operand
                val reg = ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y")
                if (operand != null) KBinaryOp(reg, "==", operand) else null
            }
            AssemblyOp.LDA, AssemblyOp.TXA, AssemblyOp.TYA -> {
                val reg = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
                KBinaryOp(reg, "==", KLiteral("0"))
            }
            AssemblyOp.LDX, AssemblyOp.TAX, AssemblyOp.INX, AssemblyOp.DEX -> {
                val reg = ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X")
                KBinaryOp(reg, "==", KLiteral("0"))
            }
            AssemblyOp.LDY, AssemblyOp.TAY, AssemblyOp.INY, AssemblyOp.DEY -> {
                val reg = ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y")
                KBinaryOp(reg, "==", KLiteral("0"))
            }
            else -> null
        }
    }

    // Use the current context's flags - they should have been set when the block was processed
    // This avoids re-processing instructions which would create duplicate temp variables
    // When flags are null (e.g., after if-else merge), try to reconstruct from the branch block
    val flagExpr = when (branchInstr.op) {
        AssemblyOp.BEQ, AssemblyOp.BNE -> ctx.zeroFlag ?: run {
            // by Claude - Fallback: look at instruction before branch to determine what comparison was made
            val flagSetterInstr = findFlagSettingInstruction()
            val reconstructed = flagSetterInstr?.let { buildZeroFlagFromInstruction(it) }
            reconstructed ?: run {
                // Last resort: use A register
                val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
                KBinaryOp(a, "==", KLiteral("0"))
            }
        }
        AssemblyOp.BCC, AssemblyOp.BCS -> ctx.carryFlag ?: run {
            // by Claude - Try to reconstruct carry from comparison instruction
            val flagSetterInstr = findFlagSettingInstruction()
            // Helper to parse immediate operand - supports ByteValue, ShortValue, and ConstantReference
            fun parseImmediateOperand(instr: AssemblyInstruction): KotlinExpr {
                return when (val addr = instr.address) {
                    is AssemblyAddressing.ByteValue -> {
                        KLiteral("0x${addr.value.toString(16).uppercase().padStart(2, '0')}")
                    }
                    is AssemblyAddressing.ShortValue -> {
                        KLiteral("0x${addr.value.toString(16).uppercase().padStart(4, '0')}")
                    }
                    is AssemblyAddressing.ConstantReference -> KVar(addr.name)
                    else -> KLiteral("0")
                }
            }
            when (flagSetterInstr?.op) {
                AssemblyOp.CMP -> {
                    val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
                    val operand = parseImmediateOperand(flagSetterInstr)
                    KBinaryOp(a, ">=", operand)  // Carry set if A >= operand
                }
                AssemblyOp.CPX -> {
                    val x = ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X")
                    val operand = parseImmediateOperand(flagSetterInstr)
                    KBinaryOp(x, ">=", operand)  // Carry set if X >= operand
                }
                AssemblyOp.CPY -> {
                    val y = ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y")
                    val operand = parseImmediateOperand(flagSetterInstr)
                    KBinaryOp(y, ">=", operand)  // Carry set if Y >= operand
                }
                else -> KVar("flagC") as KotlinExpr
            }
        }
        AssemblyOp.BMI, AssemblyOp.BPL -> ctx.negativeFlag ?: run {
            // Fallback: build negative test from likely register
            // Most often N flag comes from A, Y, or X after LDA/DEY/DEX/etc.
            val reg = ctx.registerA ?: ctx.registerY ?: ctx.registerX
                ?: ctx.getFunctionLevelVar("A") ?: ctx.getFunctionLevelVar("Y") ?: ctx.getFunctionLevelVar("X")
                ?: KVar("A")
            KBinaryOp(KParen(KBinaryOp(reg, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }
        AssemblyOp.BVC, AssemblyOp.BVS -> ctx.overflowFlag ?: KVar("flagV") // Overflow is harder to reconstruct
        // Unknown branch type - default to true to create valid syntax
        // TODO: Investigate control flow to determine proper condition
        else -> KLiteral("true /* unknown branch ${branchInstr.op} */")
    }

    // Determine the polarity (positive or negative test)
    val positiveTest = when (branchInstr.op) {
        AssemblyOp.BEQ, AssemblyOp.BCS, AssemblyOp.BMI, AssemblyOp.BVS -> true  // Branch if flag is set/true
        AssemblyOp.BNE, AssemblyOp.BCC, AssemblyOp.BPL, AssemblyOp.BVC -> false // Branch if flag is clear/false
        else -> true
    }

    // Apply sense (whether branch-taken is the "yes" path)
    val finalExpr = if (this.sense == positiveTest) {
        flagExpr
    } else {
        KUnaryOp("!", flagExpr)
    }

    // Simplify the expression for readability
    return finalExpr.simplify()
}

/**
 * Convert an AssemblyBlock to Kotlin statements.
 */
fun AssemblyBlock.toKotlin(ctx: CodeGenContext): List<KotlinStmt> {
    // Skip if already converted (due to global duplicate elimination)
    if (this in ctx.convertedBlocks) {
        return emptyList()
    }
    ctx.convertedBlocks.add(this)

    val stmts = mutableListOf<KotlinStmt>()

    // by Claude - Track if we should skip the first instruction due to BIT skip-byte pattern
    // When a block is reached via .db $2c (BIT opcode), the BIT instruction "eats" the next 2 bytes,
    // which is typically the first instruction of this block (like LDY #$04 = 2 bytes).
    // We still show it as a comment but don't generate code for it.
    // Note: The skip is function-specific - a block may skip in one function but not another.
    val shouldSkipForThisFunction = ctx.currentFunction != null &&
        ctx.currentFunction in this.skipFirstInstructionForFunctions
    var skipNextInstruction = shouldSkipForThisFunction

    // Convert each line - show ALL assembly lines as comments
    for (line in this.lines) {
        // Add original assembly line as comment
        if (line.originalLine != null) {
            val trimmedLine = line.originalLine.trim()
            if (trimmedLine.isNotEmpty()) {
                stmts.add(KComment(trimmedLine, commentTypeIndicator = ">"))
            }
        }

        // Generate Kotlin code for the instruction (if it's not a branch)
        // by Claude - Bug #1 fix: Removed JMP exclusion - JMP now handled by instruction handler to generate tail calls
        val instr = line.instruction
        if (instr != null && !instr.op.isBranch) {
            // by Claude - Check if we should skip this instruction due to BIT skip-byte pattern
            if (skipNextInstruction) {
                // The BIT instruction consumes 2 bytes, which is typically the first instruction
                // Add comment indicating this instruction is skipped by BIT
                stmts.add(KComment("(skipped by BIT \$2C)", commentTypeIndicator = " "))
                skipNextInstruction = false  // Only skip the first instruction
            } else {
                stmts.addAll(instr.toKotlin(ctx, line.originalLineIndex))
            }
        }
    }

    // Handle orphaned conditional branches - branches that weren't picked up by control flow analysis
    // This happens when the branch target is outside the function or leads to an immediate exit (like JMP to another function)
    // by Claude - Skip if this block's branch is already handled by a structured control flow (IfNode/LoopNode)
    // The isInsideStructuredBranch flag is set when converting the branchBlock of an IfNode
    val lastInstr = this.lines.lastOrNull { it.instruction != null }?.instruction
    if (lastInstr?.op?.isBranch == true && !ctx.isInsideStructuredBranch) {
        val branchTarget = this.branchExit
        val fallthrough = this.fallThroughExit

        // by Claude - Check if branch target is outside this function (true orphaned branch)
        val targetIsExit = branchTarget?.function != this.function

        // by Claude - Also check if this is an internal forward branch that wasn't handled by control flow
        // For internal branches, we should still generate conditional logic to ensure the branch
        // condition is properly evaluated, even if we can't inline the target code
        val isInternalForwardBranch = !targetIsExit && branchTarget != null &&
            branchTarget.originalLineIndex > this.originalLineIndex

        // by Claude - Check if branch target is a break target for an enclosing loop
        // If so, we need to generate a labeled break. If NOT a break target, the branch
        // just skips some code within the loop and we shouldn't generate return.
        val targetIsBreakTarget = branchTarget != null && ctx.isBreakTarget(branchTarget)

        // Only generate orphaned branch handling if:
        // 1. Target is outside the function (exit), or
        // 2. Target is a break target for an enclosing loop
        // Skip handling for internal forward branches that just skip code within a loop
        if ((targetIsExit || targetIsBreakTarget) && branchTarget != null) {
            // Generate an if-statement for this orphaned branch
            // The condition is based on the branch type
            val condition = buildOrphanedBranchCondition(lastInstr, ctx)
            if (condition != null) {
                val targetLabel = branchTarget.label ?: "exit"

                // by Claude - Bug #1 fix: Check if branchTarget contains JMP to another function
                // If so, generate a tail call to that function instead of just returning
                val jmpInstr = branchTarget.lines.find { it.instruction?.op == AssemblyOp.JMP }?.instruction
                val jmpTarget = (jmpInstr?.address as? AssemblyAddressing.Direct)?.label
                val jmpTargetFunction = jmpTarget?.let { label ->
                    ctx.functionRegistry[assemblyLabelToKotlinName(label)]
                }

                val thenBody = if (targetIsBreakTarget) {
                    // by Claude - Branch to a break target - generate labeled break
                    val breakLabel = ctx.findBreakLabel(branchTarget)!!
                    listOf(
                        KComment("goto $targetLabel", commentTypeIndicator = " "),
                        KLabeledBreak(breakLabel)
                    )
                } else if (jmpTargetFunction != null && jmpTarget != null) {
                    // Generate tail call to the JMP target function
                    // by Claude - Use getRegisterValueOrDefault to handle registers properly
                    val targetName = assemblyLabelToKotlinName(jmpTarget)
                    val args = mutableListOf<KotlinExpr>()
                    if (jmpTargetFunction.inputs?.contains(TrackedAsIo.A) == true) {
                        args.add(getRegisterValueOrDefault("A", ctx))
                    }
                    if (jmpTargetFunction.inputs?.contains(TrackedAsIo.X) == true) {
                        args.add(getRegisterValueOrDefault("X", ctx))
                    }
                    if (jmpTargetFunction.inputs?.contains(TrackedAsIo.Y) == true) {
                        args.add(getRegisterValueOrDefault("Y", ctx))
                    }
                    listOf(
                        KComment("goto $targetLabel -> $targetName", commentTypeIndicator = " "),
                        KExprStmt(KCall(targetName, args)),
                        KReturn()
                    )
                } else {
                    // No JMP target found, just return
                    listOf(
                        KComment("goto $targetLabel", commentTypeIndicator = " "),
                        KReturn()
                    )
                }

                stmts.add(KIf(
                    condition = condition,
                    thenBranch = thenBody,
                    elseBranch = emptyList()
                ))
            }
        }
    }

    // by Claude - Handle fall-through to external function (different from current function)
    // This happens when a block like SetBehind just increments a flag and falls through
    // to NextAObj which is a separate function. We need to generate a tail call.
    val fallThroughBlock = this.fallThroughExit
    if (fallThroughBlock != null && fallThroughBlock.function != ctx.currentFunction && fallThroughBlock.function != null) {
        // This block falls through to another function - generate tail call
        val targetFunction = fallThroughBlock.function!!
        val targetName = targetFunction.startingBlock.label?.let { assemblyLabelToKotlinName(it) }
            ?: "func_${targetFunction.startingBlock.originalLineIndex}"

        // by Claude - Check for mutual recursion cycles before generating tail call
        // If the target function would eventually fall through back to us, skip the tail call
        // to avoid infinite mutual recursion. In assembly, these functions share common code
        // that reaches RTS, but in Kotlin this would create A calls B calls A...
        val currentFunc = ctx.currentFunction
        val wouldCycle = currentFunc != null && wouldCreateFallThroughCycle(currentFunc, targetFunction, ctx.fallThroughGraph)

        if (wouldCycle) {
            stmts.add(KComment("SKIPPED: Fall-through to $targetName would create mutual recursion cycle", commentTypeIndicator = " "))
            // Don't generate the tail call - just return to avoid the cycle
            stmts.add(KReturn())
        } else {
            // Get the target function's inputs to determine arguments
            val targetInputs = targetFunction.inputs ?: emptySet()
            val args = mutableListOf<KotlinExpr>()

            // by Claude - Pass registers in the standard order: A, X, Y
            // If target takes A as input, pass current A value
            if (TrackedAsIo.A in targetInputs) {
                args.add(getRegisterValueOrDefault("A", ctx))
            }
            // If target takes X as input, pass current X value
            if (TrackedAsIo.X in targetInputs) {
                args.add(getRegisterValueOrDefault("X", ctx))
            }
            // If target takes Y as input, pass current Y value
            if (TrackedAsIo.Y in targetInputs) {
                args.add(getRegisterValueOrDefault("Y", ctx))
            }

            stmts.add(KComment("Fall-through tail call to $targetName", commentTypeIndicator = " "))
            stmts.add(KExprStmt(KCall(targetName, args)))
            stmts.add(KReturn())
        }
    }

    return stmts
}

/**
 * Build condition expression for an orphaned branch instruction.
 */
private fun buildOrphanedBranchCondition(instr: AssemblyInstruction, ctx: CodeGenContext): KotlinExpr? {
    return when (instr.op) {
        AssemblyOp.BEQ -> ctx.zeroFlag ?: KBinaryOp(ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A"), "==", KLiteral("0"))
        AssemblyOp.BNE -> {
            val zf = ctx.zeroFlag ?: KBinaryOp(ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A"), "==", KLiteral("0"))
            KUnaryOp("!", KParen(zf))
        }
        AssemblyOp.BCS -> ctx.carryFlag ?: KVar("flagC")
        AssemblyOp.BCC -> {
            val cf = ctx.carryFlag ?: KVar("flagC")
            KUnaryOp("!", KParen(cf))
        }
        AssemblyOp.BMI -> ctx.negativeFlag ?: KBinaryOp(KParen(KBinaryOp(ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A"), "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        AssemblyOp.BPL -> {
            val nf = ctx.negativeFlag ?: KBinaryOp(KParen(KBinaryOp(ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A"), "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            KUnaryOp("!", KParen(nf))
        }
        AssemblyOp.BVS -> ctx.overflowFlag ?: KVar("flagV")
        AssemblyOp.BVC -> {
            val vf = ctx.overflowFlag ?: KVar("flagV")
            KUnaryOp("!", KParen(vf))
        }
        else -> null
    }
}

/**
 * Check if a register is assigned to (as opposed to just read from).
 */
fun List<KotlinStmt>.isRegisterAssigned(register: String): Boolean {
    fun checkStmt(stmt: KotlinStmt): Boolean {
        return when (stmt) {
            is KAssignment -> stmt.target is KVar && (stmt.target as KVar).name == register
            is KVarDecl -> stmt.name == register
            is KDestructuringDecl -> register in stmt.names  // by Claude
            is KIf -> stmt.thenBranch.any { checkStmt(it) } || stmt.elseBranch.any { checkStmt(it) }
            is KWhile -> stmt.body.any { checkStmt(it) }
            is KDoWhile -> stmt.body.any { checkStmt(it) }
            is KLoop -> stmt.body.any { checkStmt(it) }
            // by Claude - Handle labeled loop variants
            is KLabeledWhile -> stmt.body.any { checkStmt(it) }
            is KLabeledDoWhile -> stmt.body.any { checkStmt(it) }
            is KLabeledLoop -> stmt.body.any { checkStmt(it) }
            is KWhen -> stmt.branches.any { it.body.any { s -> checkStmt(s) } } || stmt.elseBranch.any { checkStmt(it) }
            else -> false
        }
    }
    return this.any { checkStmt(it) }
}

/**
 * Collect all temp variable names (tempN) used in a list of statements.
 */
fun collectTempVars(stmts: List<KotlinStmt>): Set<String> {
    val tempVars = mutableSetOf<String>()

    fun collectFromExpr(expr: KotlinExpr) {
        when (expr) {
            is KVar -> if (expr.name.startsWith("temp") && expr.name.substring(4).all { it.isDigit() }) {
                tempVars.add(expr.name)
            }
            is KBinaryOp -> {
                collectFromExpr(expr.left)
                collectFromExpr(expr.right)
            }
            is KUnaryOp -> collectFromExpr(expr.expr)
            is KParen -> collectFromExpr(expr.expr)
            is KMemberAccess -> {
                collectFromExpr(expr.receiver)
                collectFromExpr(expr.member)
            }
            is KCall -> expr.args.forEach { collectFromExpr(it) }
            is KCast -> collectFromExpr(expr.expr)
            is KIfExpr -> {
                collectFromExpr(expr.condition)
                collectFromExpr(expr.thenExpr)
                collectFromExpr(expr.elseExpr)
            }
            else -> {}
        }
    }

    fun collectFromStmt(stmt: KotlinStmt) {
        when (stmt) {
            is KVarDecl -> {
                if (stmt.name.startsWith("temp") && stmt.name.substring(4).all { it.isDigit() }) {
                    tempVars.add(stmt.name)
                }
                stmt.value?.let { collectFromExpr(it) }
            }
            is KAssignment -> {
                collectFromExpr(stmt.target)
                collectFromExpr(stmt.value)
            }
            is KExprStmt -> collectFromExpr(stmt.expr)
            is KIf -> {
                collectFromExpr(stmt.condition)
                stmt.thenBranch.forEach { collectFromStmt(it) }
                stmt.elseBranch.forEach { collectFromStmt(it) }
            }
            is KWhile -> {
                collectFromExpr(stmt.condition)
                stmt.body.forEach { collectFromStmt(it) }
            }
            is KDoWhile -> {
                stmt.body.forEach { collectFromStmt(it) }
                collectFromExpr(stmt.condition)
            }
            is KLoop -> stmt.body.forEach { collectFromStmt(it) }
            // by Claude - Handle labeled loop variants
            is KLabeledWhile -> {
                collectFromExpr(stmt.condition)
                stmt.body.forEach { collectFromStmt(it) }
            }
            is KLabeledDoWhile -> {
                stmt.body.forEach { collectFromStmt(it) }
                collectFromExpr(stmt.condition)
            }
            is KLabeledLoop -> stmt.body.forEach { collectFromStmt(it) }
            is KReturn -> stmt.value?.let { collectFromExpr(it) }
            is KWhen -> {
                collectFromExpr(stmt.subject)
                stmt.branches.forEach { branch ->
                    branch.body.forEach { collectFromStmt(it) }
                }
                stmt.elseBranch.forEach { collectFromStmt(it) }
            }
            // by Claude - Destructuring decls use pairA/pairY names, not temp, so just collect from value
            is KDestructuringDecl -> collectFromExpr(stmt.value)
            else -> {}
        }
    }

    stmts.forEach { collectFromStmt(it) }
    return tempVars
}

/**
 * Convert temp variable declarations to assignments (since they're declared at function start).
 */
fun convertTempDeclsToAssignments(stmts: List<KotlinStmt>, tempVars: Set<String>): List<KotlinStmt> {
    fun convertStmt(stmt: KotlinStmt): KotlinStmt {
        return when (stmt) {
            is KVarDecl -> {
                // If this is a temp variable declaration, convert to assignment
                if (stmt.name in tempVars) {
                    KAssignment(KVar(stmt.name), stmt.value ?: KLiteral("0"))
                } else {
                    stmt
                }
            }
            is KIf -> KIf(
                condition = stmt.condition,
                thenBranch = stmt.thenBranch.map { convertStmt(it) },
                elseBranch = stmt.elseBranch.map { convertStmt(it) }
            )
            is KWhile -> KWhile(stmt.condition, stmt.body.map { convertStmt(it) })
            is KDoWhile -> KDoWhile(stmt.body.map { convertStmt(it) }, stmt.condition)
            is KLoop -> KLoop(stmt.body.map { convertStmt(it) })
            // by Claude - Handle labeled loop variants
            is KLabeledWhile -> KLabeledWhile(stmt.label, stmt.condition, stmt.body.map { convertStmt(it) })
            is KLabeledDoWhile -> KLabeledDoWhile(stmt.label, stmt.body.map { convertStmt(it) }, stmt.condition)
            is KLabeledLoop -> KLabeledLoop(stmt.label, stmt.body.map { convertStmt(it) })
            is KWhen -> KWhen(
                subject = stmt.subject,
                branches = stmt.branches.map { branch ->
                    KWhenBranch(branch.values, branch.body.map { convertStmt(it) })
                },
                elseBranch = stmt.elseBranch.map { convertStmt(it) }
            )
            else -> stmt
        }
    }

    return stmts.map { convertStmt(it) }
}

// HARDWARE_REGISTER_PREFIXES moved to codegen-helpers.kt

/**
 * Pre-scan all instructions in a function to identify indexed memory accesses.
 * This ensures that memory locations like AreaObjectLength that are accessed
 * both directly and with an offset will use the indexed delegate consistently.
 */
fun AssemblyFunction.preScanMemoryAccesses(ctx: CodeGenContext) {
    val blocksToVisit = mutableListOf(startingBlock)
    val visitedBlocks = mutableSetOf<AssemblyBlock>()

    while (blocksToVisit.isNotEmpty()) {
        val block = blocksToVisit.removeAt(0)
        if (!visitedBlocks.add(block)) continue
        if (block.function != this) continue

        for (line in block.lines) {
            val address = line.instruction?.address
            // Check for offset accesses (e.g., AreaObjectLength+1)
            when (address) {
                is AssemblyAddressing.Direct -> {
                    if (!address.label.startsWith("$")) {
                        if (address.offset != 0) {
                            // This is an offset access - register the base label as indexed
                            ctx.registerIndexedAccess(address.label)
                        } else if (address.label in HARDWARE_REGISTER_PREFIXES) {
                            // Hardware registers that commonly have multiple registers
                            ctx.registerIndexedAccess(address.label)
                        }
                    }
                }
                is AssemblyAddressing.DirectX -> {
                    if (!address.label.startsWith("$")) {
                        // X-indexed access - register as indexed
                        ctx.registerIndexedAccess(address.label)
                    }
                }
                is AssemblyAddressing.DirectY -> {
                    if (!address.label.startsWith("$")) {
                        // Y-indexed access - register as indexed
                        ctx.registerIndexedAccess(address.label)
                    }
                }
                else -> {}
            }
        }

        block.fallThroughExit?.let { if (it.function == this) blocksToVisit.add(it) }
        block.branchExit?.let { if (it.function == this) blocksToVisit.add(it) }
    }
}

/**
 * Convert an AssemblyFunction to a KFunction.
 * @param functionRegistry Map of function names to AssemblyFunction for looking up call signatures
 * @param jumpEngineTables Map of line index to JumpEngine dispatch tables
 * @param fallThroughGraph by Claude - Graph of fall-through relationships for cycle detection
 */
fun AssemblyFunction.toKotlinFunction(
    functionRegistry: Map<String, AssemblyFunction> = emptyMap(),
    jumpEngineTables: Map<Int, JumpEngineTable> = emptyMap(),
    fallThroughGraph: Map<AssemblyFunction, Set<AssemblyFunction>> = emptyMap()
): KFunction {
    val ctx = CodeGenContext(functionRegistry, jumpEngineTables, this, fallThroughGraph)

    // by Claude - Pre-initialize register references for registers that are likely parameters
    // This enables materializeAllRegisters() to work correctly before branches that modify them
    // If a register is an input to this function, we set it to the parameter reference so that
    // materializeRegister() will create a temp var initialized with the parameter value
    if (this.inputs?.contains(TrackedAsIo.A) == true) {
        ctx.registerA = KVar("A")
    }
    if (this.inputs?.contains(TrackedAsIo.X) == true) {
        ctx.registerX = KVar("X")
    }
    if (this.inputs?.contains(TrackedAsIo.Y) == true) {
        ctx.registerY = KVar("Y")
    }

    // Pre-scan all instructions to identify indexed memory accesses.
    // This ensures that labels like AreaObjectLength that are accessed both
    // directly and with an offset use the indexed delegate consistently.
    preScanMemoryAccesses(ctx)

    // Get control flow structure
    val controlNodes = this.asControls ?: this.analyzeControls()

    // Convert all control nodes to Kotlin statements, but stop after a return
    val body = mutableListOf<KotlinStmt>()

    // Helper to check if statements end with a return
    fun endsWithReturn(stmts: List<KotlinStmt>): Boolean {
        return stmts.lastOrNull() is KReturn
    }

    var i = 0
    while (i < controlNodes.size) {
        val node = controlNodes[i]
        val stmts = node.toKotlin(ctx)

        // Check if this is an if-return followed by more code
        // Convert: if (...) { return } <code>
        // Into: if (...) { return } else { <code> }
        if (stmts.isNotEmpty() && stmts.last() is KIf) {
            val ifStmt = stmts.last() as KIf
            if (ifStmt.elseBranch.isEmpty() && endsWithReturn(ifStmt.thenBranch)) {
                // Check if there's a next node
                if (i + 1 < controlNodes.size) {
                    val nextNode = controlNodes[i + 1]
                    val nextStmts = nextNode.toKotlin(ctx)

                    // Add all statements except the last (the if)
                    body.addAll(stmts.dropLast(1))

                    // Combine the if with the next node as else
                    val combinedIf = KIf(
                        condition = ifStmt.condition,
                        thenBranch = ifStmt.thenBranch,
                        elseBranch = nextStmts
                    )
                    body.add(combinedIf)

                    // If else branch also ends with return, stop here
                    if (endsWithReturn(nextStmts)) {
                        break
                    }

                    i += 2  // Skip both nodes
                    continue
                }
            }
        }

        body.addAll(stmts)

        // If this node contained a return at the top level, stop processing
        if (stmts.any { it is KReturn }) {
            break
        }

        i++
    }

    // Detect which registers are used/written in the function
    val usesA = body.any { it.usesRegister("A") }
    val usesX = body.any { it.usesRegister("X") }
    val usesY = body.any { it.usesRegister("Y") }

    // by Claude - Detect output registers early (needed for register declaration logic)
    val hasAOutput = this.outputs?.contains(TrackedAsIo.A) == true
    val hasXOutput = this.outputs?.contains(TrackedAsIo.X) == true
    val hasYOutput = this.outputs?.contains(TrackedAsIo.Y) == true

    // Use the analyzed inputs from AssemblyFunction if available, otherwise fall back to read-before-write analysis
    val aIsParam = if (this.inputs != null) {
        TrackedAsIo.A in this.inputs!!
    } else {
        usesA && body.isRegisterReadBeforeWrite("A", ctx)
    }
    val xIsParam = if (this.inputs != null) {
        TrackedAsIo.X in this.inputs!!
    } else {
        usesX && body.isRegisterReadBeforeWrite("X", ctx)
    }
    val yIsParam = if (this.inputs != null) {
        TrackedAsIo.Y in this.inputs!!
    } else {
        usesY && body.isRegisterReadBeforeWrite("Y", ctx)
    }

    // Build parameter list
    val params = mutableListOf<KParam>()
    if (aIsParam) params.add(KParam("A", "Int"))
    if (xIsParam) params.add(KParam("X", "Int"))
    if (yIsParam) params.add(KParam("Y", "Int"))

    // Collect all temp variables used in the function
    val allTempVars = collectTempVars(body)

    // Check if parameters are reassigned (need local mutable copy)
    val aIsReassigned = aIsParam && body.isRegisterAssigned("A")
    val xIsReassigned = xIsParam && body.isRegisterAssigned("X")
    val yIsReassigned = yIsParam && body.isRegisterAssigned("Y")

    // Prepend register declarations for registers that are NOT parameters but are used
    // For parameters, only create local var if they're reassigned
    // by Claude - Also ensure registers are declared if they're in the function's outputs
    // This handles the case where a register value comes from a function call (captured in temp var)
    // but needs to be returned by this function
    val finalBody = mutableListOf<KotlinStmt>()
    if ((usesA || hasAOutput) && !aIsParam) {
        finalBody.add(KVarDecl("A", "Int", KLiteral("0"), mutable = true))
    } else if (aIsReassigned) {
        finalBody.add(KVarDecl("A", "Int", KVar("A"), mutable = true))
    }
    // by Claude - Also check hasXOutput to ensure X is declared when returned
    if ((usesX || hasXOutput) && !xIsParam) {
        finalBody.add(KVarDecl("X", "Int", KLiteral("0"), mutable = true))
    } else if (xIsReassigned) {
        finalBody.add(KVarDecl("X", "Int", KVar("X"), mutable = true))
    }
    if ((usesY || hasYOutput) && !yIsParam) {
        finalBody.add(KVarDecl("Y", "Int", KLiteral("0"), mutable = true))
    } else if (yIsReassigned) {
        finalBody.add(KVarDecl("Y", "Int", KVar("Y"), mutable = true))
    }

    // Declare all temp variables at function start
    for (tempVar in allTempVars.sorted()) {
        finalBody.add(KVarDecl(tempVar, "Int", KLiteral("0"), mutable = true))
    }

    // Generate delegate property declarations for memory accesses
    // Sort for consistent output
    // Skip direct accesses that are also indexed (indexed delegate handles both cases)
    for ((constant, propName) in ctx.directMemoryAccesses.toSortedMap()) {
        if (constant in ctx.indexedMemoryAccesses) continue  // Indexed delegate will handle this
        finalBody.add(KVarDecl(
            name = propName,
            type = null,  // Type inferred from delegate
            value = KCall("MemoryByte", listOf(KVar(constant))),
            mutable = true,
            delegated = true
        ))
    }
    for ((constant, propName) in ctx.indexedMemoryAccesses.toSortedMap()) {
        finalBody.add(KVarDecl(
            name = propName,
            type = null,  // Type inferred from delegate
            value = KCall("MemoryByteIndexed", listOf(KVar(constant))),
            mutable = false,  // Indexed delegates are val (array access is via get/set)
            delegated = true
        ))
    }

    // Convert temp variable declarations in body to assignments
    finalBody.addAll(convertTempDeclsToAssignments(body, allTempVars))

    // Check if this function falls through to another function (tail call pattern)
    // This happens when a function's exit block falls through to another function's entry
    // Common in 6502 code where one function increments a register and "falls through" to be called again
    run {
        // Find all blocks belonging to this function
        val functionBlocks = mutableSetOf<AssemblyBlock>()
        val toVisit = mutableListOf(this.startingBlock)
        while (toVisit.isNotEmpty()) {
            val block = toVisit.removeAt(0)
            if (!functionBlocks.add(block)) continue
            if (block.function == this) {
                block.fallThroughExit?.let { if (it.function == this) toVisit.add(it) }
                block.branchExit?.let { if (it.function == this) toVisit.add(it) }
            }
        }

        // Find exit blocks that fall through to another function
        for (block in functionBlocks) {
            val fallThrough = block.fallThroughExit
            if (fallThrough != null && fallThrough.function != this && fallThrough.function != null) {
                // This block falls through to another function - generate tail call
                val targetFunction = fallThrough.function!!
                val targetName = targetFunction.startingBlock.label?.let { assemblyLabelToKotlinName(it) }
                    ?: "func_${targetFunction.startingBlock.originalLineIndex}"

                // by Claude - Check for mutual recursion cycles before generating tail call
                // If the target function would eventually fall through back to us, skip the tail call
                val wouldCycle = wouldCreateFallThroughCycle(this, targetFunction, ctx.fallThroughGraph)

                if (wouldCycle) {
                    // Don't generate the tail call - just add a comment explaining why
                    finalBody.add(KComment("SKIPPED: Fall-through to $targetName would create mutual recursion cycle"))
                    // Note: No return added here since the caller may still want to add one
                    break
                }

                // Only add tail call if the body doesn't already end with a return
                val lastStmt = finalBody.lastOrNull()
                val bodyEndsWithReturn = lastStmt is KReturn ||
                    (lastStmt is KIf && lastStmt.thenBranch.lastOrNull() is KReturn && lastStmt.elseBranch.lastOrNull() is KReturn)

                if (!bodyEndsWithReturn) {
                    // Build arguments based on target function's inputs
                    // by Claude - Use getRegisterValueOrDefault to handle registers properly
                    val args = mutableListOf<KotlinExpr>()
                    if (targetFunction.inputs?.contains(TrackedAsIo.A) == true) {
                        args.add(getRegisterValueOrDefault("A", ctx))
                    }
                    if (targetFunction.inputs?.contains(TrackedAsIo.X) == true) {
                        args.add(getRegisterValueOrDefault("X", ctx))
                    }
                    if (targetFunction.inputs?.contains(TrackedAsIo.Y) == true) {
                        args.add(getRegisterValueOrDefault("Y", ctx))
                    }

                    finalBody.add(KComment("Fall-through tail call to $targetName"))
                    finalBody.add(KExprStmt(KCall(targetName, args)))

                    // by Claude - After tail call, add return statement with current register values
                    // This handles cases where this function outputs registers that were set before the tail call
                    val thisHasAOutput = this.outputs?.contains(TrackedAsIo.A) == true
                    val thisHasYOutput = this.outputs?.contains(TrackedAsIo.Y) == true
                    val returnValue: KotlinExpr? = when {
                        thisHasAOutput && thisHasYOutput -> {
                            KCall("Pair", listOf(
                                getRegisterValueOrDefault("A", ctx),
                                getRegisterValueOrDefault("Y", ctx)
                            ))
                        }
                        thisHasAOutput -> getRegisterValueOrDefault("A", ctx)
                        thisHasYOutput -> getRegisterValueOrDefault("Y", ctx)
                        else -> null
                    }
                    finalBody.add(KReturn(returnValue))
                }
                break  // Only handle one fall-through (there shouldn't be multiple)
            }
        }
    }

    // Generate function name
    val functionName = this.startingBlock.label?.let { label ->
        assemblyLabelToKotlinName(label)
    } ?: "func_${this.startingBlock.originalLineIndex}"

    // by Claude - Detect return type from outputs (hasAOutput/hasXOutput/hasYOutput already defined earlier)
    val hasCarryOutput = this.outputs?.contains(TrackedAsIo.CarryFlag) == true
    // by Claude - Count how many registers are outputs to determine return type
    val outputRegisterCount = listOf(hasAOutput, hasXOutput, hasYOutput).count { it }
    val returnType = when {
        outputRegisterCount >= 2 -> "Pair<Int, Int>"  // Two or more registers - return as pair
        hasAOutput -> "Int"  // A only
        hasXOutput -> "Int"  // X only (e.g., dividePDiff returns X as index)
        hasYOutput -> "Int"  // Y only (returns Y value)
        hasCarryOutput -> "Boolean"  // Carry flag only (for branch condition returns)
        else -> null  // Void
    }

    // by Claude - Handle return values based on output type
    // RTS handler now generates correct return values, so we just need to ensure paths have returns
    val bodyWithReturns = when {
        outputRegisterCount >= 2 -> {
            // Pair return: determine which two registers to return
            val firstReg = when {
                hasAOutput -> "A"
                hasXOutput -> "X"
                else -> "Y"
            }
            val secondReg = when {
                hasYOutput -> "Y"
                hasXOutput && firstReg != "X" -> "X"
                else -> "Y" // fallback
            }
            val fallbackReturn = KReturn(KCall("Pair", listOf(KVar(firstReg), KVar(secondReg))))
            finalBody.ensureReturnsPresent(fallbackReturn)
        }
        hasAOutput -> {
            // A-only return: RTS generates A value - ensure all paths have returns
            val fallbackReturn = KReturn(KVar("A"))
            finalBody.ensureReturnsPresent(fallbackReturn)
        }
        hasXOutput -> {
            // by Claude - X-only return: RTS generates X value - ensure all paths have returns
            // Common pattern: functions like dividePDiff that return an index in X
            val fallbackReturn = KReturn(KVar("X"))
            finalBody.ensureReturnsPresent(fallbackReturn)
        }
        hasYOutput -> {
            // Y-only return: RTS generates Y value - ensure all paths have returns
            val fallbackReturn = KReturn(KVar("Y"))
            finalBody.ensureReturnsPresent(fallbackReturn)
        }
        hasCarryOutput -> {
            // by Claude - Boolean return: RTS handler generates carry flag value
            // Fallback is false if no explicit return was generated
            val fallbackReturn = KReturn(KLiteral("false"))
            finalBody.ensureReturnsPresent(fallbackReturn)
        }
        else -> {
            // Void function: strip any return values from RTS handler
            finalBody.map { stmt ->
                when (stmt) {
                    is KReturn -> KReturn(null)
                    is KIf -> KIf(
                        condition = stmt.condition,
                        thenBranch = stmt.thenBranch.stripReturnValues(),
                        elseBranch = stmt.elseBranch.stripReturnValues()
                    )
                    is KWhile -> KWhile(stmt.condition, stmt.body.stripReturnValues())
                    is KDoWhile -> KDoWhile(stmt.body.stripReturnValues(), stmt.condition)
                    is KLoop -> KLoop(stmt.body.stripReturnValues())
                    else -> stmt
                }
            }
        }
    }

    return KFunction(
        name = functionName,
        params = params,
        returnType = returnType,
        body = bodyWithReturns,
        comment = "Decompiled from ${this.startingBlock.label ?: "@${this.startingBlock.originalLineIndex}"}"
    )
}

/**
 * Add return values to return statements recursively.
 * Used when a function has a return type to ensure all returns include the value.
 */
// by Claude - Transform return statements using a custom transformation function
/**
 * Transform return statements recursively using the provided transformation function.
 * The transformer receives the return value expression (may be null) and returns a new KReturn.
 */
fun List<KotlinStmt>.transformReturns(transformer: (KotlinExpr?) -> KReturn): List<KotlinStmt> {
    return this.map { stmt ->
        when (stmt) {
            is KReturn -> transformer(stmt.value)
            is KIf -> KIf(
                condition = stmt.condition,
                thenBranch = stmt.thenBranch.transformReturns(transformer),
                elseBranch = stmt.elseBranch.transformReturns(transformer)
            )
            is KWhile -> KWhile(stmt.condition, stmt.body.transformReturns(transformer))
            is KDoWhile -> KDoWhile(stmt.body.transformReturns(transformer), stmt.condition)
            is KLoop -> KLoop(stmt.body.transformReturns(transformer))
            else -> stmt
        }
    }
}

// by Claude - Ensure all return statements have values, keeping existing values
/**
 * Ensures all paths have proper returns. Keeps existing return values,
 * and adds fallback return if function doesn't end with one.
 *
 * CRITICAL FIX by Claude: Only add fallback returns to if branches when the if is the
 * LAST statement in the block. If there's code after an if statement, control should
 * flow through to that code, not have returns added to branches that make it unreachable.
 */
fun List<KotlinStmt>.ensureReturnsPresent(fallbackReturn: KReturn): List<KotlinStmt> {
    val updated = this.mapIndexed { index, stmt ->
        val isLastStatement = index == this.size - 1
        when (stmt) {
            is KReturn -> if (stmt.value == null) fallbackReturn else stmt
            is KIf -> {
                // by Claude - Only add fallback returns to if branches when it's the last statement.
                // For non-terminal ifs, one or both branches may fall through to subsequent code.
                if (isLastStatement) {
                    KIf(
                        condition = stmt.condition,
                        thenBranch = stmt.thenBranch.ensureReturnsPresent(fallbackReturn),
                        elseBranch = stmt.elseBranch.ensureReturnsPresent(fallbackReturn)
                    )
                } else {
                    // Non-terminal if: only process branches that already end with returns
                    // (to update their return values), don't add new returns
                    KIf(
                        condition = stmt.condition,
                        thenBranch = stmt.thenBranch.updateExistingReturns(fallbackReturn),
                        elseBranch = stmt.elseBranch.updateExistingReturns(fallbackReturn)
                    )
                }
            }
            is KWhile -> KWhile(stmt.condition, stmt.body.ensureReturnsPresent(fallbackReturn))
            is KDoWhile -> KDoWhile(stmt.body.ensureReturnsPresent(fallbackReturn), stmt.condition)
            is KLoop -> KLoop(stmt.body.ensureReturnsPresent(fallbackReturn))
            else -> stmt
        }
    }
    // by Claude - Check if last statement provides returns on all paths
    val lastStmt = updated.lastOrNull()
    val endsWithReturn = lastStmt is KReturn ||
        (lastStmt is KIf && lastStmt.thenBranch.lastOrNull() is KReturn && lastStmt.elseBranch.lastOrNull() is KReturn)
    return if (!endsWithReturn) {
        updated + fallbackReturn
    } else {
        updated
    }
}

// by Claude - Update return values in existing returns without adding new returns
/**
 * Updates existing return statements to have fallback values if they're empty,
 * but does NOT add new returns to branches that don't have them.
 * This preserves fall-through behavior for non-terminal if statements.
 */
private fun List<KotlinStmt>.updateExistingReturns(fallbackReturn: KReturn): List<KotlinStmt> {
    return this.map { stmt ->
        when (stmt) {
            is KReturn -> if (stmt.value == null) fallbackReturn else stmt
            is KIf -> KIf(
                condition = stmt.condition,
                thenBranch = stmt.thenBranch.updateExistingReturns(fallbackReturn),
                elseBranch = stmt.elseBranch.updateExistingReturns(fallbackReturn)
            )
            is KWhile -> KWhile(stmt.condition, stmt.body.updateExistingReturns(fallbackReturn))
            is KDoWhile -> KDoWhile(stmt.body.updateExistingReturns(fallbackReturn), stmt.condition)
            is KLoop -> KLoop(stmt.body.updateExistingReturns(fallbackReturn))
            else -> stmt
        }
    }
}

fun List<KotlinStmt>.addReturnValues(): List<KotlinStmt> {
    return this.map { stmt ->
        when (stmt) {
            is KReturn -> if (stmt.value == null) KReturn(KVar("A")) else stmt
            is KIf -> KIf(
                condition = stmt.condition,
                thenBranch = stmt.thenBranch.addReturnValues(),
                elseBranch = stmt.elseBranch.addReturnValues()
            )
            is KWhile -> KWhile(stmt.condition, stmt.body.addReturnValues())
            is KDoWhile -> KDoWhile(stmt.body.addReturnValues(), stmt.condition)
            is KLoop -> KLoop(stmt.body.addReturnValues())
            else -> stmt
        }
    }
}

// by Claude - Strip return values for void functions
/**
 * Strip return values from return statements recursively.
 * Used for void functions to remove the A register values captured by RTS handler.
 */
fun List<KotlinStmt>.stripReturnValues(): List<KotlinStmt> {
    return this.map { stmt ->
        when (stmt) {
            is KReturn -> KReturn(null)
            is KIf -> KIf(
                condition = stmt.condition,
                thenBranch = stmt.thenBranch.stripReturnValues(),
                elseBranch = stmt.elseBranch.stripReturnValues()
            )
            is KWhile -> KWhile(stmt.condition, stmt.body.stripReturnValues())
            is KDoWhile -> KDoWhile(stmt.body.stripReturnValues(), stmt.condition)
            is KLoop -> KLoop(stmt.body.stripReturnValues())
            else -> stmt
        }
    }
}

/**
 * Check if a register is read before being written in a list of statements.
 * This helps identify function parameters.
 */
fun List<KotlinStmt>.isRegisterReadBeforeWrite(registerName: String, ctx: CodeGenContext): Boolean {
    // Track if we've seen a write to this register
    var seenWrite = false

    fun checkStmt(stmt: KotlinStmt): Boolean {
        if (seenWrite) return false  // Already written, stop checking

        return when (stmt) {
            is KAssignment -> {
                // Check if target is the register (this is a write)
                if (stmt.target.isRegisterWrite(registerName)) {
                    seenWrite = true
                    false  // Write found, not a read
                } else {
                    // Check if value reads the register before the write
                    stmt.value.usesRegister(registerName)
                }
            }
            is KVarDecl -> {
                // Variable declaration with initialization is a write if it's the register
                if (stmt.name == registerName) {
                    seenWrite = true
                    false
                } else {
                    stmt.value?.usesRegister(registerName) ?: false
                }
            }
            is KExprStmt -> stmt.expr.usesRegister(registerName)
            is KIf -> {
                // Check condition first
                val conditionReads = stmt.condition.usesRegister(registerName)
                if (conditionReads) return true

                // Check branches (both need to be checked for complete analysis)
                val thenReads = stmt.thenBranch.any { checkStmt(it) }
                val elseReads = stmt.elseBranch.any { checkStmt(it) }
                thenReads || elseReads
            }
            is KWhile -> {
                val conditionReads = stmt.condition.usesRegister(registerName)
                if (conditionReads) return true
                stmt.body.any { checkStmt(it) }
            }
            is KDoWhile -> {
                val bodyReads = stmt.body.any { checkStmt(it) }
                if (bodyReads) return true
                stmt.condition.usesRegister(registerName)
            }
            is KLoop -> stmt.body.any { checkStmt(it) }
            is KReturn -> stmt.value?.usesRegister(registerName) ?: false
            // by Claude - Destructuring decl is a write to all named vars
            is KDestructuringDecl -> {
                if (registerName in stmt.names) {
                    seenWrite = true
                    false
                } else {
                    stmt.value.usesRegister(registerName)
                }
            }
            else -> false
        }
    }

    return this.any { checkStmt(it) }
}

/**
 * Check if an expression represents a write to a register.
 */
fun KotlinExpr.isRegisterWrite(registerName: String): Boolean {
    return when (this) {
        is KVar -> name == registerName
        else -> false
    }
}

/**
 * Check if a statement uses a specific register variable.
 */
fun KotlinStmt.usesRegister(registerName: String): Boolean {
    return when (this) {
        is KAssignment -> target.usesRegister(registerName) || value.usesRegister(registerName)
        is KExprStmt -> expr.usesRegister(registerName)
        is KIf -> condition.usesRegister(registerName) ||
                  thenBranch.any { it.usesRegister(registerName) } ||
                  elseBranch.any { it.usesRegister(registerName) }
        is KWhile -> condition.usesRegister(registerName) || body.any { it.usesRegister(registerName) }
        is KDoWhile -> condition.usesRegister(registerName) || body.any { it.usesRegister(registerName) }
        is KLoop -> body.any { it.usesRegister(registerName) }
        // by Claude - Handle labeled loop variants
        is KLabeledWhile -> condition.usesRegister(registerName) || body.any { it.usesRegister(registerName) }
        is KLabeledDoWhile -> condition.usesRegister(registerName) || body.any { it.usesRegister(registerName) }
        is KLabeledLoop -> body.any { it.usesRegister(registerName) }
        is KReturn -> value?.usesRegister(registerName) ?: false
        is KVarDecl -> value?.usesRegister(registerName) ?: false
        is KDestructuringDecl -> value.usesRegister(registerName)  // by Claude
        else -> false
    }
}

/**
 * Check if an expression uses a specific register variable.
 */
fun KotlinExpr.usesRegister(registerName: String): Boolean {
    return when (this) {
        is KVar -> name == registerName
        is KBinaryOp -> left.usesRegister(registerName) || right.usesRegister(registerName)
        is KUnaryOp -> expr.usesRegister(registerName)
        is KParen -> expr.usesRegister(registerName)
        is KMemberAccess -> receiver.usesRegister(registerName) || member.usesRegister(registerName)
        is KCall -> args.any { it.usesRegister(registerName) }
        is KCast -> expr.usesRegister(registerName)
        else -> false
    }
}

// by Claude - Detect terminating statements for smart if-else merge
/**
 * Check if a statement terminates control flow (return).
 * Used to determine which branch's state should be preserved after if-else.
 */
fun KotlinStmt.isTerminating(): Boolean {
    return when (this) {
        is KReturn -> true
        // by Claude - Labeled breaks terminate the current code path (exit the loop)
        is KLabeledBreak -> true
        is KBreak -> true
        // For if statements, terminating only if BOTH branches terminate
        is KIf -> thenBranch.lastOrNull()?.isTerminating() == true &&
                  elseBranch.lastOrNull()?.isTerminating() == true
        else -> false
    }
}

/**
 * Sanitize a label to make it a valid Kotlin identifier.
 * Converts addresses like $00 to zp_00, etc.
 */
fun sanitizeLabel(label: String): String {
    return when {
        label.startsWith("$") -> {
            // Zero-page or absolute address
            val hex = label.substring(1)
            "zp_$hex"
        }
        label.matches(Regex("^[0-9].*")) -> {
            // Starts with digit, prefix with underscore
            "_$label"
        }
        else -> label
    }
}

/**
 * Convert label name to camelCase property name.
 * Example: "OperMode" -> "operMode", "Player_X_Speed" -> "playerXSpeed"
 * Preserves internal capitalization: "SwimmingFlag" -> "swimmingFlag"
 */
fun labelToCamelCase(label: String): String {
    return label.split('_')
        .mapIndexed { index, part ->
            if (part.isEmpty()) return@mapIndexed ""
            if (index == 0) {
                // First word: lowercase just the first character, keep rest as-is
                part.replaceFirstChar { it.lowercase() }
            } else {
                // Subsequent words: uppercase first character, keep rest as-is
                part.replaceFirstChar { it.uppercase() }
            }
        }
        .joinToString("")
}

// Helper functions (wrapPropertyRead, wrapPropertyWrite, toKotlinExpr, etc.) moved to codegen-helpers.kt
