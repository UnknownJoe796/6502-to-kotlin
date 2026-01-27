package com.ivieleague.decompiler6502tokotlin.hand

/**
 * by Claude - Code generation context and state tracking.
 *
 * Contains CodeGenContext class and JumpEngineTable detection.
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

    // by Claude - Stack of enclosing loops for labeled break and continue support
    // Each entry is LoopInfo(label, header, breakTargets)
    // - header: the loop header block (for continue detection)
    // - breakTargets: blocks that exit the loop
    data class LoopInfo(val label: String, val header: AssemblyBlock?, val breakTargets: Set<AssemblyBlock>)
    private var loopLabelCounter = 0
    val loopStack = mutableListOf<LoopInfo>()

    /** Push a new loop onto the stack and return its label */
    fun pushLoop(header: AssemblyBlock?, breakTargets: Set<AssemblyBlock>): String {
        val label = "loop${loopLabelCounter++}"
        loopStack.add(LoopInfo(label, header, breakTargets))
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
            val info = loopStack[i]
            if (target in info.breakTargets) return info.label
        }
        return null
    }

    /** Check if a block is a break target for ANY enclosing loop */
    fun isBreakTarget(target: AssemblyBlock): Boolean = findBreakLabel(target) != null

    // by Claude - Continue target support for internal back-branches to loop headers
    /** Find the label for a continue target (loop header). Returns null if not a continue target. */
    fun findContinueLabel(target: AssemblyBlock): String? {
        // Search from INNERMOST to outermost - continue goes to the immediate enclosing loop
        for (i in loopStack.indices.reversed()) {
            val info = loopStack[i]
            if (info.header == target) return info.label
        }
        return null
    }

    /** Check if a block is a continue target (loop header) for ANY enclosing loop */
    fun isContinueTarget(target: AssemblyBlock): Boolean = findContinueLabel(target) != null

    // by Claude - Track break targets that need flag-based exit handling
    // Maps break target block -> flag variable name
    // Used when a loop has multiple break targets where some have code to execute
    val breakTargetFlags = mutableMapOf<AssemblyBlock, String>()

    // by Claude - Counter for break target flag variables
    private var breakFlagCounter = 0

    /** Get a unique flag variable name for a break target */
    fun nextBreakFlag(): String = "exitFlag${breakFlagCounter++}"

    /** Get the flag name for a break target, or null if not a flagged target */
    fun getBreakTargetFlag(target: AssemblyBlock): String? = breakTargetFlags[target]

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
     * by Claude - Generate a return statement appropriate for the current function's output type.
     * Uses the function's declared outputs to determine what values to return.
     */
    fun generateFunctionReturn(): KReturn {
        val fn = currentFunction ?: return KReturn()
        val outputs = fn.outputs ?: return KReturn()

        val hasAOutput = TrackedAsIo.A in outputs
        val hasXOutput = TrackedAsIo.X in outputs
        val hasYOutput = TrackedAsIo.Y in outputs
        val hasCarryOutput = TrackedAsIo.CarryFlag in outputs

        // Count register outputs
        val registerOutputCount = listOf(hasAOutput, hasXOutput, hasYOutput).count { it }

        return when {
            registerOutputCount >= 2 -> {
                // Multi-register return as Pair
                val firstReg = when {
                    hasAOutput -> "A"
                    hasXOutput -> "X"
                    else -> "Y"
                }
                val secondReg = when {
                    hasYOutput -> "Y"
                    hasXOutput && firstReg != "X" -> "X"
                    else -> "Y"
                }
                KReturn(KCall("Pair", listOf(KVar(firstReg), KVar(secondReg))))
            }
            hasAOutput -> KReturn(KVar("A"))
            hasXOutput -> KReturn(KVar("X"))
            hasYOutput -> KReturn(KVar("Y"))
            // by Claude - Handle Boolean return from carry flag
            // For functions that return Boolean based on carry, use carryFlag if available
            // Otherwise fall back to false as a safe default
            hasCarryOutput -> {
                val carryValue = carryFlag
                if (carryValue != null) {
                    KReturn(carryValue)
                } else {
                    KReturn(KLiteral("false"))
                }
            }
            else -> KReturn()
        }
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
