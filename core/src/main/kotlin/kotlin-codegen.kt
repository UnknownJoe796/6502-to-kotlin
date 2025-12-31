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
    val jumpEngineTables: Map<Int, JumpEngineTable> = emptyMap()
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

    /** Track which blocks have been converted to avoid duplicates */
    val convertedBlocks = mutableSetOf<AssemblyBlock>()

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
     */
    fun registerIndexedAccess(constant: String): String {
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
    fun getOrCreateFunctionLevelVar(register: String): Pair<String, Boolean> {
        return when (register) {
            "A" -> {
                if (functionLevelA == null) {
                    functionLevelA = nextTempVar()
                    Pair(functionLevelA!!, true)
                } else {
                    Pair(functionLevelA!!, false)
                }
            }
            "X" -> {
                if (functionLevelX == null) {
                    functionLevelX = nextTempVar()
                    Pair(functionLevelX!!, true)
                } else {
                    Pair(functionLevelX!!, false)
                }
            }
            "Y" -> {
                if (functionLevelY == null) {
                    functionLevelY = nextTempVar()
                    Pair(functionLevelY!!, true)
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

/**
 * Convert an assembly label to a Kotlin function name.
 * Examples: "ImposeGravity" -> "imposeGravity", "Move_Platform_Up" -> "movePlatformUp"
 */
fun assemblyLabelToKotlinName(label: String): String {
    return if (label.contains('_') || label.contains('-')) {
        // Handle snake_case or kebab-case: Split and convert to camelCase
        label.split('_', '-')
            .joinToString("") { it.lowercase().replaceFirstChar { c -> c.uppercase() } }
            .replaceFirstChar { it.lowercase() }
    } else {
        // Already in PascalCase or camelCase, just lowercase first char
        label.replaceFirstChar { it.lowercase() }
    }
}

/**
 * Convert a 6502 instruction to Kotlin statements.
 *
 * This is the core instruction-to-code translator. Each 6502 opcode maps to
 * Kotlin code that performs the equivalent operation.
 *
 * @param lineIndex The original line index of this instruction (for JumpEngine lookup)
 */
fun AssemblyInstruction.toKotlin(ctx: CodeGenContext, lineIndex: Int = -1): List<KotlinStmt> {
    val stmts = mutableListOf<KotlinStmt>()

    when (this.op) {
        // ===========================
        // Load instructions
        // All load instructions set Z (zero) and N (negative) flags
        // ===========================
        AssemblyOp.LDA -> {
            val rawValue = this.address.toKotlinExpr(ctx)
            val value = wrapPropertyRead(rawValue)
            // If a function-level A var exists, assign to it; otherwise track expression
            val existingVar = ctx.getFunctionLevelVar("A")
            if (existingVar != null) {
                stmts.add(KAssignment(existingVar, value))
                ctx.registerA = existingVar
            } else {
                ctx.registerA = value
            }
            // LDA sets Z flag if value == 0, N flag if bit 7 is set
            val flagRef = ctx.registerA!!
            ctx.zeroFlag = KBinaryOp(flagRef, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(flagRef, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.LDX -> {
            val rawValue = this.address.toKotlinExpr(ctx)
            val value = wrapPropertyRead(rawValue)
            // If a function-level X var exists, assign to it; otherwise track expression
            val existingVar = ctx.getFunctionLevelVar("X")
            if (existingVar != null) {
                stmts.add(KAssignment(existingVar, value))
                ctx.registerX = existingVar
            } else {
                ctx.registerX = value
            }
            // LDX sets Z flag if value == 0, N flag if bit 7 is set
            val flagRef = ctx.registerX!!
            ctx.zeroFlag = KBinaryOp(flagRef, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(flagRef, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.LDY -> {
            val rawValue = this.address.toKotlinExpr(ctx)
            val value = wrapPropertyRead(rawValue)
            // If a function-level Y var exists, assign to it; otherwise track expression
            val existingVar = ctx.getFunctionLevelVar("Y")
            if (existingVar != null) {
                stmts.add(KAssignment(existingVar, value))
                ctx.registerY = existingVar
            } else {
                ctx.registerY = value
            }
            // LDY sets Z flag if value == 0, N flag if bit 7 is set
            val flagRef = ctx.registerY!!
            ctx.zeroFlag = KBinaryOp(flagRef, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(flagRef, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        // ===========================
        // Store instructions
        // ===========================
        AssemblyOp.STA -> {
            val target = this.address.toKotlinExpr(ctx)
            val rawValue = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            val value = wrapPropertyWrite(target, rawValue)
            stmts.add(KAssignment(target, value))
        }

        AssemblyOp.STX -> {
            val target = this.address.toKotlinExpr(ctx)
            val rawValue = ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X")
            val value = wrapPropertyWrite(target, rawValue)
            stmts.add(KAssignment(target, value))
        }

        AssemblyOp.STY -> {
            val target = this.address.toKotlinExpr(ctx)
            val rawValue = ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y")
            val value = wrapPropertyWrite(target, rawValue)
            stmts.add(KAssignment(target, value))
        }

        // ===========================
        // Transfer instructions
        // All transfer instructions set Z and N flags based on value transferred
        // ===========================
        AssemblyOp.TAX -> {
            val value = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            ctx.registerX = value
            ctx.zeroFlag = KBinaryOp(value, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(value, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.TAY -> {
            val value = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            ctx.registerY = value
            ctx.zeroFlag = KBinaryOp(value, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(value, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.TXA -> {
            val value = ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X")
            ctx.registerA = value
            ctx.zeroFlag = KBinaryOp(value, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(value, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.TYA -> {
            val value = ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y")
            ctx.registerA = value
            ctx.zeroFlag = KBinaryOp(value, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(value, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        // ===========================
        // Arithmetic instructions
        // ===========================
        AssemblyOp.ADC -> {
            val operand = this.address.toKotlinExpr(ctx)
            val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            val carryIn = ctx.carryFlag ?: KLiteral("false")

            // Simplify carry to int: if carry is literal true/false, just use 1 or 0
            // Otherwise generate: if (carry) 1 else 0
            val carryInt: KotlinExpr = when {
                carryIn is KLiteral && carryIn.value in listOf("0", "false") -> KLiteral("0")
                carryIn is KLiteral && carryIn.value in listOf("1", "true") -> KLiteral("1")
                else -> KParen(KLiteral("if (${carryIn.toKotlin()}) 1 else 0"))
            }

            // Full sum (may exceed 255)
            val fullSum = KBinaryOp(KBinaryOp(a, "+", wrapPropertyRead(operand)), "+", carryInt)
            // Result masked to 8 bits
            val result = KBinaryOp(KParen(fullSum), "and", KLiteral("0xFF"))
            ctx.registerA = result
            // Carry set if result > 255
            ctx.carryFlag = KBinaryOp(fullSum, ">", KLiteral("0xFF"))
            ctx.zeroFlag = KBinaryOp(result, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(result, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.SBC -> {
            val operand = this.address.toKotlinExpr(ctx)
            val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            val carryIn = ctx.carryFlag ?: KLiteral("true")

            // Simplify borrow: if carry is literal true/false, just use 0 or 1
            // borrow = NOT carry, so: carry=true -> borrow=0, carry=false -> borrow=1
            val borrowInt: KotlinExpr = when {
                carryIn is KLiteral && carryIn.value in listOf("1", "true") -> KLiteral("0")
                carryIn is KLiteral && carryIn.value in listOf("0", "false") -> KLiteral("1")
                else -> KParen(KLiteral("if (${carryIn.toKotlin()}) 0 else 1"))
            }

            // Full difference (may go negative)
            val fullDiff = KBinaryOp(KBinaryOp(a, "-", wrapPropertyRead(operand)), "-", borrowInt)
            // Result masked to 8 bits
            val result = KBinaryOp(KParen(fullDiff), "and", KLiteral("0xFF"))
            ctx.registerA = result
            // Carry set if no borrow (result >= 0 before masking)
            ctx.carryFlag = KBinaryOp(fullDiff, ">=", KLiteral("0"))
            ctx.zeroFlag = KBinaryOp(result, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(result, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.INC -> {
            val target = this.address.toKotlinExpr(ctx)
            val readValue = wrapPropertyRead(target)
            val result = KBinaryOp(KParen(KBinaryOp(readValue, "+", KLiteral("1"))), "and", KLiteral("0xFF"))
            val wrappedResult = wrapPropertyWrite(target, result)
            stmts.add(KAssignment(target, wrappedResult))
            ctx.zeroFlag = KBinaryOp(result, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(result, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.DEC -> {
            val target = this.address.toKotlinExpr(ctx)
            val readValue = wrapPropertyRead(target)
            val result = KBinaryOp(KParen(KBinaryOp(readValue, "-", KLiteral("1"))), "and", KLiteral("0xFF"))
            val wrappedResult = wrapPropertyWrite(target, result)
            stmts.add(KAssignment(target, wrappedResult))
            ctx.zeroFlag = KBinaryOp(result, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(result, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.INX -> {
            var x = ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X")
            // If X is not a variable, materialize it first so we can update it
            if (x !is KVar) {
                stmts.addAll(ctx.materializeRegister("X", mutable = true))
                x = ctx.registerX as KVar
            }
            val result = KBinaryOp(KParen(KBinaryOp(x, "+", KLiteral("1"))), "and", KLiteral("0xFF"))
            stmts.add(KAssignment(x, result))
            ctx.zeroFlag = KBinaryOp(x, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(x, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.INY -> {
            var y = ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y")
            // If Y is not a variable, materialize it first so we can update it
            if (y !is KVar) {
                stmts.addAll(ctx.materializeRegister("Y", mutable = true))
                y = ctx.registerY as KVar
            }
            val result = KBinaryOp(KParen(KBinaryOp(y, "+", KLiteral("1"))), "and", KLiteral("0xFF"))
            stmts.add(KAssignment(y, result))
            ctx.zeroFlag = KBinaryOp(y, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(y, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.DEX -> {
            var x = ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X")
            // If X is not a variable, materialize it first so we can update it
            if (x !is KVar) {
                stmts.addAll(ctx.materializeRegister("X", mutable = true))
                x = ctx.registerX as KVar
            }
            val result = KBinaryOp(KParen(KBinaryOp(x, "-", KLiteral("1"))), "and", KLiteral("0xFF"))
            stmts.add(KAssignment(x, result))
            // Flags are set based on the NEW value (after decrement)
            ctx.zeroFlag = KBinaryOp(x, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(x, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.DEY -> {
            var y = ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y")
            // If Y is not a variable, materialize it first so we can update it
            if (y !is KVar) {
                stmts.addAll(ctx.materializeRegister("Y", mutable = true))
                y = ctx.registerY as KVar
            }
            val result = KBinaryOp(KParen(KBinaryOp(y, "-", KLiteral("1"))), "and", KLiteral("0xFF"))
            stmts.add(KAssignment(y, result))
            // Flags are set based on the NEW value (after decrement)
            ctx.zeroFlag = KBinaryOp(y, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(y, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        // ===========================
        // Logical instructions
        // All logical instructions set Z and N flags based on result
        // Emit explicit assignments to make data flow visible
        // ===========================
        AssemblyOp.AND -> {
            val operand = wrapPropertyRead(this.address.toKotlinExpr(ctx))
            val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            val resultExpr = KBinaryOp(a, "and", operand)
            // Emit assignment to make data flow visible
            val varName = ctx.nextTempVar()
            stmts.add(KVarDecl(varName, "Int", resultExpr, mutable = false))
            val varRef = KVar(varName)
            ctx.registerA = varRef
            ctx.zeroFlag = KBinaryOp(varRef, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(varRef, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.ORA -> {
            val operand = wrapPropertyRead(this.address.toKotlinExpr(ctx))
            val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            val resultExpr = KBinaryOp(a, "or", operand)
            val varName = ctx.nextTempVar()
            stmts.add(KVarDecl(varName, "Int", resultExpr, mutable = false))
            val varRef = KVar(varName)
            ctx.registerA = varRef
            ctx.zeroFlag = KBinaryOp(varRef, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(varRef, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.EOR -> {
            val operand = wrapPropertyRead(this.address.toKotlinExpr(ctx))
            val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            val resultExpr = KBinaryOp(a, "xor", operand)
            val varName = ctx.nextTempVar()
            stmts.add(KVarDecl(varName, "Int", resultExpr, mutable = false))
            val varRef = KVar(varName)
            ctx.registerA = varRef
            ctx.zeroFlag = KBinaryOp(varRef, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(varRef, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        // ===========================
        // Shift instructions
        // ===========================
        AssemblyOp.ASL -> {
            // ASL shifts left, bit 7 goes into carry, Z/N updated from result
            if (this.address == null) {
                // Accumulator mode
                val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
                // Carry flag = bit 7 of original value (shifted out)
                ctx.carryFlag = KBinaryOp(KParen(KBinaryOp(a, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
                val result = KBinaryOp(KParen(KBinaryOp(a, "shl", KLiteral("1"))), "and", KLiteral("0xFF"))
                // If A is a KVar (mutable variable), emit assignment; otherwise track expression
                if (a is KVar) {
                    stmts.add(KAssignment(a, result))
                } else {
                    ctx.registerA = result
                }
                ctx.zeroFlag = KBinaryOp(a, "==", KLiteral("0"))
                ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(a, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            } else {
                val target = this.address.toKotlinExpr(ctx)
                val readValue = wrapPropertyRead(target)
                // Carry flag = bit 7 of original value (shifted out)
                ctx.carryFlag = KBinaryOp(KParen(KBinaryOp(readValue, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
                val shifted = KBinaryOp(KParen(KBinaryOp(readValue, "shl", KLiteral("1"))), "and", KLiteral("0xFF"))
                val wrappedShifted = wrapPropertyWrite(target, shifted)
                stmts.add(KAssignment(target, wrappedShifted))
                ctx.zeroFlag = KBinaryOp(shifted, "==", KLiteral("0"))
                ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(shifted, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            }
        }

        AssemblyOp.LSR -> {
            // LSR shifts right, bit 0 goes into carry, Z/N updated from result
            if (this.address == null) {
                // Accumulator mode
                val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
                // Carry flag = bit 0 of original value (shifted out)
                ctx.carryFlag = KBinaryOp(KParen(KBinaryOp(a, "and", KLiteral("0x01"))), "!=", KLiteral("0"))
                val result = KBinaryOp(a, "shr", KLiteral("1"))
                // If A is a KVar (mutable variable), emit assignment; otherwise track expression
                if (a is KVar) {
                    stmts.add(KAssignment(a, result))
                } else {
                    ctx.registerA = result
                }
                ctx.zeroFlag = KBinaryOp(a, "==", KLiteral("0"))
                ctx.negativeFlag = KLiteral("false")  // LSR always clears N (bit 7 becomes 0)
            } else {
                val target = this.address.toKotlinExpr(ctx)
                val readValue = wrapPropertyRead(target)
                // Carry flag = bit 0 of original value (shifted out)
                ctx.carryFlag = KBinaryOp(KParen(KBinaryOp(readValue, "and", KLiteral("0x01"))), "!=", KLiteral("0"))
                val shifted = KBinaryOp(readValue, "shr", KLiteral("1"))
                val wrappedShifted = wrapPropertyWrite(target, shifted)
                stmts.add(KAssignment(target, wrappedShifted))
                ctx.zeroFlag = KBinaryOp(shifted, "==", KLiteral("0"))
                ctx.negativeFlag = KLiteral("false")  // LSR always clears N
            }
        }

        AssemblyOp.ROL -> {
            // ROL rotates left through carry: old bit 7 -> carry, old carry -> bit 0
            if (this.address == null) {
                // Accumulator mode
                val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
                val oldCarry = ctx.carryFlag ?: KVar("flagC")
                // New carry = old bit 7
                ctx.carryFlag = KBinaryOp(KParen(KBinaryOp(a, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
                // Result = (a << 1) | old_carry, masked to 8 bits
                val shiftedPart = KBinaryOp(KParen(KBinaryOp(a, "shl", KLiteral("1"))), "and", KLiteral("0xFE"))
                val carryBit = KIfExpr(oldCarry, KLiteral("1"), KLiteral("0"))
                val result = KBinaryOp(shiftedPart, "or", carryBit)
                // If A is a KVar (mutable variable), emit assignment; otherwise track expression
                if (a is KVar) {
                    stmts.add(KAssignment(a, result))
                } else {
                    ctx.registerA = result
                }
                ctx.zeroFlag = KBinaryOp(a, "==", KLiteral("0"))
                ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(a, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            } else {
                val target = this.address.toKotlinExpr(ctx)
                val readValue = wrapPropertyRead(target)
                val oldCarry = ctx.carryFlag ?: KVar("flagC")
                ctx.carryFlag = KBinaryOp(KParen(KBinaryOp(readValue, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
                val shiftedPart = KBinaryOp(KParen(KBinaryOp(readValue, "shl", KLiteral("1"))), "and", KLiteral("0xFE"))
                val carryBit = KIfExpr(oldCarry, KLiteral("1"), KLiteral("0"))
                val result = KBinaryOp(shiftedPart, "or", carryBit)
                val wrappedResult = wrapPropertyWrite(target, result)
                stmts.add(KAssignment(target, wrappedResult))
                ctx.zeroFlag = KBinaryOp(result, "==", KLiteral("0"))
                ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(result, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            }
        }

        AssemblyOp.ROR -> {
            // ROR rotates right through carry: old bit 0 -> carry, old carry -> bit 7
            if (this.address == null) {
                // Accumulator mode
                val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
                val oldCarry = ctx.carryFlag ?: KVar("flagC")
                // New carry = old bit 0
                ctx.carryFlag = KBinaryOp(KParen(KBinaryOp(a, "and", KLiteral("0x01"))), "!=", KLiteral("0"))
                // Result = (a >> 1) | (old_carry << 7)
                val shiftedPart = KBinaryOp(a, "shr", KLiteral("1"))
                val carryBit = KIfExpr(oldCarry, KLiteral("0x80"), KLiteral("0"))
                val result = KBinaryOp(shiftedPart, "or", carryBit)
                // If A is a KVar (mutable variable), emit assignment; otherwise track expression
                if (a is KVar) {
                    stmts.add(KAssignment(a, result))
                } else {
                    ctx.registerA = result
                }
                ctx.zeroFlag = KBinaryOp(a, "==", KLiteral("0"))
                ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(a, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            } else {
                val target = this.address.toKotlinExpr(ctx)
                val readValue = wrapPropertyRead(target)
                val oldCarry = ctx.carryFlag ?: KVar("flagC")
                ctx.carryFlag = KBinaryOp(KParen(KBinaryOp(readValue, "and", KLiteral("0x01"))), "!=", KLiteral("0"))
                val shiftedPart = KBinaryOp(readValue, "shr", KLiteral("1"))
                val carryBit = KIfExpr(oldCarry, KLiteral("0x80"), KLiteral("0"))
                val result = KBinaryOp(shiftedPart, "or", carryBit)
                val wrappedResult = wrapPropertyWrite(target, result)
                stmts.add(KAssignment(target, wrappedResult))
                ctx.zeroFlag = KBinaryOp(result, "==", KLiteral("0"))
                ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(result, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            }
        }

        // ===========================
        // Compare instructions
        // ===========================
        AssemblyOp.CMP -> {
            val operand = wrapPropertyRead(this.address.toKotlinExpr(ctx))
            val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")

            // Set flags based on comparison
            val diff = KBinaryOp(a, "-", operand)
            ctx.zeroFlag = KBinaryOp(diff, "==", KLiteral("0"))
            ctx.carryFlag = KBinaryOp(a, ">=", operand)
            ctx.negativeFlag = KBinaryOp(diff, "<", KLiteral("0"))
        }

        AssemblyOp.CPX -> {
            val operand = wrapPropertyRead(this.address.toKotlinExpr(ctx))
            val x = ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X")
            ctx.zeroFlag = KBinaryOp(x, "==", operand)
            ctx.carryFlag = KBinaryOp(x, ">=", operand)
        }

        AssemblyOp.CPY -> {
            val operand = wrapPropertyRead(this.address.toKotlinExpr(ctx))
            val y = ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y")
            ctx.zeroFlag = KBinaryOp(y, "==", operand)
            ctx.carryFlag = KBinaryOp(y, ">=", operand)
        }

        // ===========================
        // Branch instructions (handled by control flow)
        // ===========================
        AssemblyOp.BEQ, AssemblyOp.BNE, AssemblyOp.BCC, AssemblyOp.BCS,
        AssemblyOp.BMI, AssemblyOp.BPL, AssemblyOp.BVC, AssemblyOp.BVS -> {
            // These are handled by control flow conversion
        }

        // ===========================
        // Jump/subroutine instructions
        // ===========================
        AssemblyOp.JMP -> {
            // Handled by control flow
        }

        AssemblyOp.JSR -> {
            // Get the label name directly without converting to memory access
            val assemblyLabel = when (val addr = this.address) {
                is AssemblyAddressing.Direct -> addr.label
                is AssemblyAddressing.IndirectAbsolute -> addr.label
                else -> this.address.toKotlinExpr(ctx).toKotlin()
            }

            // Check if this is a JumpEngine call with a known dispatch table
            val jumpTable = ctx.jumpEngineTables[lineIndex]
            if (assemblyLabel == "JumpEngine" && jumpTable != null) {
                // Generate a when() statement that dispatches based on A register
                val indexExpr = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")

                val branches = jumpTable.targets.mapIndexed { index, targetLabel ->
                    val targetFunctionName = assemblyLabelToKotlinName(targetLabel)
                    val targetFunction = ctx.functionRegistry[targetFunctionName]

                    // Build argument list for target function
                    val args = mutableListOf<KotlinExpr>()
                    if (targetFunction?.inputs != null) {
                        if (TrackedAsIo.X in targetFunction.inputs!!) {
                            args.add(ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X"))
                        }
                        if (TrackedAsIo.Y in targetFunction.inputs!!) {
                            args.add(ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y"))
                        }
                    }

                    KWhenBranch(
                        values = listOf(KLiteral("$index")),
                        body = listOf(KExprStmt(KCall(targetFunctionName, args)))
                    )
                }

                stmts.add(KWhen(
                    subject = indexExpr,
                    branches = branches,
                    elseBranch = listOf(KComment("Unknown JumpEngine index"))
                ))
                stmts.add(KReturn())
            } else {
                val functionName = assemblyLabelToKotlinName(assemblyLabel)

                // Look up the target function to determine what parameters to pass
                val targetFunction = ctx.functionRegistry[functionName]

                // Build argument list based on target function's inputs
                val args = mutableListOf<KotlinExpr>()
                if (targetFunction?.inputs != null) {
                    if (TrackedAsIo.A in targetFunction.inputs!!) {
                        args.add(ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A"))
                    }
                    if (TrackedAsIo.X in targetFunction.inputs!!) {
                        args.add(ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X"))
                    }
                    if (TrackedAsIo.Y in targetFunction.inputs!!) {
                        args.add(ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y"))
                    }
                }

                stmts.add(KExprStmt(KCall(functionName, args)))

                // Terminal subroutines like JumpEngine don't return - add a return statement
                if (isTerminalSubroutine(this)) {
                    stmts.add(KReturn())
                }
            }
        }

        AssemblyOp.RTS, AssemblyOp.RTI -> {
            stmts.add(KReturn())
        }

        // ===========================
        // Stack instructions
        // ===========================
        AssemblyOp.PHA -> {
            // Push A to stack
            val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            stmts.add(KExprStmt(KCall("push", listOf(a))))
        }

        AssemblyOp.PLA -> {
            // Pull A from stack and update Z/N flags
            val pullExpr = KCall("pull", emptyList())
            val existingVar = ctx.getFunctionLevelVar("A")
            if (existingVar != null) {
                stmts.add(KAssignment(existingVar, pullExpr))
                ctx.registerA = existingVar
            } else {
                val tempVar = ctx.nextTempVar()
                stmts.add(KVarDecl(tempVar, "Int", pullExpr, mutable = true))
                ctx.registerA = KVar(tempVar)
            }
            // PLA sets Z and N flags based on pulled value
            val a = ctx.registerA!!
            ctx.zeroFlag = KBinaryOp(a, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(a, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.PHP -> {
            // Push processor status - simplified, just call push with flags packed
            stmts.add(KComment("TODO: PHP - push processor status"))
        }

        AssemblyOp.PLP -> {
            // Pull processor status - simplified, just call pull and unpack flags
            stmts.add(KComment("TODO: PLP - pull processor status"))
        }

        // ===========================
        // Flag instructions
        // ===========================
        AssemblyOp.CLC -> {
            ctx.carryFlag = KLiteral("false")
        }

        AssemblyOp.SEC -> {
            ctx.carryFlag = KLiteral("true")
        }

        AssemblyOp.CLV -> {
            ctx.overflowFlag = KLiteral("0")
        }

        // ===========================
        // Test bits
        // ===========================
        AssemblyOp.BIT -> {
            val operand = wrapPropertyRead(this.address.toKotlinExpr(ctx))
            val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            // Z = (A AND memory) == 0
            ctx.zeroFlag = KBinaryOp(KParen(KBinaryOp(a, "and", operand)), "==", KLiteral("0"))
            // N = bit 7 of memory value
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(operand, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            // V = bit 6 of memory value
            ctx.overflowFlag = KBinaryOp(KParen(KBinaryOp(operand, "and", KLiteral("0x40"))), "!=", KLiteral("0"))
        }

        // ===========================
        // Other
        // ===========================
        AssemblyOp.NOP -> {
            // No operation
        }

        else -> {
            // Unhandled instruction - will still have assembly comment
        }
    }

    return stmts
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
            val branchBlock = this.condition.branchBlock
            result.addAll(branchBlock.toKotlin(ctx))

            // Materialize any complex register expressions before branching
            // This ensures we capture values into named variables before they can be lost
            result.addAll(ctx.materializeAllRegisters())

            // Save state before branches - each branch starts from the same state
            val stateBeforeBranch = ctx.saveState()

            // Then add the if statement - process branches independently
            val condition = this.condition.toKotlinExpr(ctx)

            // Process then branch
            ctx.restoreState(stateBeforeBranch)
            val thenStmts = this.thenBranch.flatMap { it.toKotlin(ctx) }
            val thenState = ctx.saveState()

            // Process else branch (starting fresh from pre-branch state)
            ctx.restoreState(stateBeforeBranch)
            val elseStmts = this.elseBranch.flatMap { it.toKotlin(ctx) }
            val elseState = ctx.saveState()

            // Merge states - if branches set different values, result is unknown (null)
            ctx.mergeStates(thenState, elseState)

            result.add(KIf(condition, thenStmts, elseStmts))

            result
        }

        is LoopNode -> {
            val result = mutableListOf<KotlinStmt>()

            // Materialize any literal register values before the loop body
            // This ensures loop counters are proper variables that can be updated
            result.addAll(ctx.materializeRegister("X", mutable = true))
            result.addAll(ctx.materializeRegister("Y", mutable = true))

            val bodyStmts = this.body.flatMap { it.toKotlin(ctx) }

            val loop = when (this.kind) {
                LoopKind.PreTest -> {
                    val condition = this.condition?.toKotlinExpr(ctx) ?: KLiteral("true")
                    KWhile(condition, bodyStmts)
                }
                LoopKind.PostTest -> {
                    val condition = this.condition?.toKotlinExpr(ctx) ?: KLiteral("true")
                    KDoWhile(bodyStmts, condition)
                }
                LoopKind.Infinite -> {
                    KLoop(bodyStmts)
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
fun Condition.toKotlinExpr(ctx: CodeGenContext): KotlinExpr {
    // Get the branch instruction
    val branchInstr = this.branchLine.instruction ?: return KLiteral("/* unknown condition */")

    // Use the current context's flags - they should have been set when the block was processed
    // This avoids re-processing instructions which would create duplicate temp variables
    val flagExpr = when (branchInstr.op) {
        AssemblyOp.BEQ, AssemblyOp.BNE -> ctx.zeroFlag ?: KVar("flagZ")
        AssemblyOp.BCC, AssemblyOp.BCS -> ctx.carryFlag ?: KVar("flagC")
        AssemblyOp.BMI, AssemblyOp.BPL -> ctx.negativeFlag ?: KVar("flagN")
        AssemblyOp.BVC, AssemblyOp.BVS -> ctx.overflowFlag ?: KVar("flagV")
        else -> KLiteral("/* unknown branch */")
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

    // Convert each line - show ALL assembly lines as comments
    for (line in this.lines) {
        // Add original assembly line as comment
        if (line.originalLine != null) {
            val trimmedLine = line.originalLine.trim()
            if (trimmedLine.isNotEmpty()) {
                stmts.add(KComment(trimmedLine, commentTypeIndicator = ">"))
            }
        }

        // Generate Kotlin code for the instruction (if it's not a branch/jump)
        val instr = line.instruction
        if (instr != null && !instr.op.isBranch && instr.op != AssemblyOp.JMP) {
            stmts.addAll(instr.toKotlin(ctx, line.originalLineIndex))
        }
    }

    // Handle orphaned conditional branches - branches that weren't picked up by control flow analysis
    // This happens when the branch target is outside the function or leads to an immediate exit (like JMP to another function)
    val lastInstr = this.lines.lastOrNull { it.instruction != null }?.instruction
    if (lastInstr?.op?.isBranch == true) {
        val branchTarget = this.branchExit
        val fallthrough = this.fallThroughExit

        // Check if branch target is outside this function or is an exit block (contains JMP)
        val targetIsExit = branchTarget?.function != this.function ||
                           branchTarget?.lines?.any { it.instruction?.op == AssemblyOp.JMP } == true

        if (targetIsExit && branchTarget != null) {
            // Generate an if-statement for this orphaned branch
            // The condition is based on the branch type
            val condition = buildOrphanedBranchCondition(lastInstr, ctx)
            if (condition != null) {
                // Branch taken = exit, so generate: if (condition) { return }
                val targetLabel = branchTarget.label ?: "exit"
                stmts.add(KIf(
                    condition = condition,
                    thenBranch = listOf(
                        KComment("goto $targetLabel", commentTypeIndicator = " "),
                        KReturn()
                    ),
                    elseBranch = emptyList()
                ))
            }
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
            is KIf -> stmt.thenBranch.any { checkStmt(it) } || stmt.elseBranch.any { checkStmt(it) }
            is KWhile -> stmt.body.any { checkStmt(it) }
            is KDoWhile -> stmt.body.any { checkStmt(it) }
            is KLoop -> stmt.body.any { checkStmt(it) }
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
            is KReturn -> stmt.value?.let { collectFromExpr(it) }
            is KWhen -> {
                collectFromExpr(stmt.subject)
                stmt.branches.forEach { branch ->
                    branch.body.forEach { collectFromStmt(it) }
                }
                stmt.elseBranch.forEach { collectFromStmt(it) }
            }
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

/**
 * Convert an AssemblyFunction to a KFunction.
 * @param functionRegistry Map of function names to AssemblyFunction for looking up call signatures
 * @param jumpEngineTables Map of line index to JumpEngine dispatch tables
 */
fun AssemblyFunction.toKotlinFunction(
    functionRegistry: Map<String, AssemblyFunction> = emptyMap(),
    jumpEngineTables: Map<Int, JumpEngineTable> = emptyMap()
): KFunction {
    val ctx = CodeGenContext(functionRegistry, jumpEngineTables)

    // Get improved control flow
    val controlNodes = this.asControls ?: this.improveControlFlow()

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
    val finalBody = mutableListOf<KotlinStmt>()
    if (usesA && !aIsParam) {
        finalBody.add(KVarDecl("A", "Int", KLiteral("0"), mutable = true))
    } else if (aIsReassigned) {
        finalBody.add(KVarDecl("A", "Int", KVar("A"), mutable = true))
    }
    if (usesX && !xIsParam) {
        finalBody.add(KVarDecl("X", "Int", KLiteral("0"), mutable = true))
    } else if (xIsReassigned) {
        finalBody.add(KVarDecl("X", "Int", KVar("X"), mutable = true))
    }
    if (usesY && !yIsParam) {
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

                // Only add tail call if the body doesn't already end with a return
                val lastStmt = finalBody.lastOrNull()
                val bodyEndsWithReturn = lastStmt is KReturn ||
                    (lastStmt is KIf && lastStmt.thenBranch.lastOrNull() is KReturn && lastStmt.elseBranch.lastOrNull() is KReturn)

                if (!bodyEndsWithReturn) {
                    // Build arguments based on target function's inputs
                    val args = mutableListOf<KotlinExpr>()
                    if (targetFunction.inputs?.contains(TrackedAsIo.A) == true) {
                        args.add(ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A"))
                    }
                    if (targetFunction.inputs?.contains(TrackedAsIo.X) == true) {
                        args.add(ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X"))
                    }
                    if (targetFunction.inputs?.contains(TrackedAsIo.Y) == true) {
                        args.add(ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y"))
                    }

                    finalBody.add(KComment("Fall-through tail call to $targetName"))
                    finalBody.add(KExprStmt(KCall(targetName, args)))
                }
                break  // Only handle one fall-through (there shouldn't be multiple)
            }
        }
    }

    // Generate function name
    val functionName = this.startingBlock.label?.let { label ->
        assemblyLabelToKotlinName(label)
    } ?: "func_${this.startingBlock.originalLineIndex}"

    // Detect return type from outputs
    val returnType = when {
        this.outputs == null || this.outputs!!.isEmpty() -> null
        TrackedAsIo.A in this.outputs!! -> "Int"  // A register returns a value
        else -> null  // Other outputs (flags, etc.) don't translate to return values
    }

    // If function has a return type, update return statements to return the appropriate value
    val bodyWithReturns = if (returnType != null) {
        finalBody.map { stmt ->
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
    } else {
        finalBody
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
        is KReturn -> value?.usesRegister(registerName) ?: false
        is KVarDecl -> value?.usesRegister(registerName) ?: false
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

/**
 * Wraps a memory read (UByte) with .toInt() for use in expressions.
 * memory[X] returns UByte, but we need Int for comparisons and arithmetic.
 */
fun wrapPropertyRead(expr: KotlinExpr): KotlinExpr {
    return when (expr) {
        is KMemberAccess -> {
            // memory[X] returns UByte, needs .toInt() for expressions
            if (expr.receiver is KVar && (expr.receiver as KVar).name == "memory" && expr.isIndexed) {
                KMemberAccess(expr, KCall("toInt"), isIndexed = false)
            } else {
                expr
            }
        }
        else -> expr
    }
}

/**
 * Check if an expression is known to be in the 0-255 range (byte-sized).
 * This helps avoid unnecessary `and 0xFF` masking.
 */
fun KotlinExpr.isKnownByteSized(): Boolean {
    return when (this) {
        // Literals that fit in a byte
        is KLiteral -> {
            val v = when {
                value.startsWith("0x") || value.startsWith("0X") -> value.drop(2).toIntOrNull(16)
                value.startsWith("0b") || value.startsWith("0B") -> value.drop(2).toIntOrNull(2)
                else -> value.toIntOrNull()
            }
            v != null && v in 0..255
        }
        // Simple variable - in 6502 decompilation, temp vars are typically byte-sized
        is KVar -> true
        // Memory reads are byte-sized
        is KMemberAccess -> {
            // memory[X].toInt() is byte-sized
            if (isIndexed && receiver is KVar && (receiver as KVar).name == "memory") true
            // property.toInt() where property is a memory delegate
            else if (!isIndexed && member is KCall && (member as KCall).name == "toInt") true
            else false
        }
        // Parenthesized expression - check inner
        is KParen -> expr.isKnownByteSized()
        // Bitwise AND with 0xFF is obviously byte-sized
        is KBinaryOp -> {
            if (op == "and") {
                val leftVal = (left as? KLiteral)?.value?.let {
                    if (it.startsWith("0x")) it.drop(2).toIntOrNull(16) else it.toIntOrNull()
                }
                val rightVal = (right as? KLiteral)?.value?.let {
                    if (it.startsWith("0x")) it.drop(2).toIntOrNull(16) else it.toIntOrNull()
                }
                // If masking with 0xFF or less, result is byte-sized
                (rightVal != null && rightVal <= 0xFF) || (leftVal != null && leftVal <= 0xFF)
            } else false
        }
        else -> false
    }
}

/**
 * Wraps a register value (Int) with .toUByte() for writing to memory.
 * memory[X] expects UByte, but registers and temp variables are Int.
 *
 * Optimized to avoid unnecessary `and 0xFF` when the value is known to be byte-sized.
 */
fun wrapPropertyWrite(target: KotlinExpr, value: KotlinExpr): KotlinExpr {
    return when {
        // If target is memory[X], we need to convert Int to UByte
        target is KMemberAccess && target.receiver is KVar &&
            (target.receiver as KVar).name == "memory" && target.isIndexed -> {
            if (value.isKnownByteSized()) {
                // Value is already byte-sized, just convert to UByte
                // Wrap in parens if it's a binary op to ensure correct precedence
                val safeValue = when (value) {
                    is KBinaryOp -> KParen(value)
                    else -> value
                }
                KMemberAccess(safeValue, KCall("toUByte"), isIndexed = false)
            } else {
                // Value might exceed byte range, mask it first
                val masked = when (value) {
                    is KBinaryOp, is KLiteral -> KBinaryOp(KParen(value), "and", KLiteral("0xFF"))
                    else -> KBinaryOp(value, "and", KLiteral("0xFF"))
                }
                KMemberAccess(KParen(masked), KCall("toUByte"), isIndexed = false)
            }
        }
        else -> value
    }
}

/**
 * Convert addressing mode to Kotlin expression.
 */
fun AssemblyAddressing?.toKotlinExpr(ctx: CodeGenContext): KotlinExpr {
    if (this == null) return KLiteral("0")

    return when (this) {
        is AssemblyAddressing.ByteValue -> {
            KLiteral("0x${this.value.toString(16).uppercase().padStart(2, '0')}")
        }

        is AssemblyAddressing.ShortValue -> {
            KLiteral("0x${this.value.toString(16).uppercase().padStart(4, '0')}")
        }

        is AssemblyAddressing.ConstantReference -> {
            // Constants use original case to match constants file
            KVar(this.name)
        }

        is AssemblyAddressing.Direct -> {
            if (this.label.startsWith("$")) {
                // Numeric address - direct memory access (no delegate, use memory[])
                val addr = this.label.substring(1).toIntOrNull(16) ?: 0
                val finalAddr = if (this.offset != 0) {
                    KBinaryOp(KLiteral("0x${addr.toString(16).uppercase()}"), "+", KLiteral(this.offset.toString()))
                } else {
                    KLiteral("0x${addr.toString(16).uppercase()}")
                }
                KMemberAccess(KVar("memory"), finalAddr, isIndexed = true)
            } else {
                // Named label - use delegate property
                if (this.offset != 0) {
                    // Offset access - use indexed delegate
                    val propName = ctx.registerIndexedAccess(this.label)
                    KMemberAccess(KVar(propName), KLiteral(this.offset.toString()), isIndexed = true)
                } else if (this.label in ctx.indexedMemoryAccesses) {
                    // Direct access but we already have an indexed delegate for this
                    // Use indexed access with index 0 to avoid duplicate declarations
                    val propName = ctx.indexedMemoryAccesses[this.label]!!
                    KMemberAccess(KVar(propName), KLiteral("0"), isIndexed = true)
                } else {
                    // Direct access - use simple delegate
                    val propName = ctx.registerDirectAccess(this.label)
                    KVar(propName)
                }
            }
        }

        is AssemblyAddressing.DirectX -> {
            val index = ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X")
            if (this.label.startsWith("$")) {
                // Numeric address - direct memory access with X offset (no delegate)
                val addr = this.label.substring(1).toIntOrNull(16) ?: 0
                val baseAddr = if (this.offset != 0) {
                    KBinaryOp(KLiteral("0x${addr.toString(16).uppercase()}"), "+", KLiteral(this.offset.toString()))
                } else {
                    KLiteral("0x${addr.toString(16).uppercase()}")
                }
                KMemberAccess(KVar("memory"), KBinaryOp(baseAddr, "+", index), isIndexed = true)
            } else {
                // Named label with index - use indexed delegate
                val propName = ctx.registerIndexedAccess(this.label)
                if (this.offset != 0) {
                    // property[offset + X]
                    val indexExpr = KBinaryOp(KLiteral(this.offset.toString()), "+", index)
                    KMemberAccess(KVar(propName), indexExpr, isIndexed = true)
                } else {
                    // property[X]
                    KMemberAccess(KVar(propName), index, isIndexed = true)
                }
            }
        }

        is AssemblyAddressing.DirectY -> {
            val index = ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y")
            if (this.label.startsWith("$")) {
                // Numeric address - direct memory access with Y offset (no delegate)
                val addr = this.label.substring(1).toIntOrNull(16) ?: 0
                val baseAddr = if (this.offset != 0) {
                    KBinaryOp(KLiteral("0x${addr.toString(16).uppercase()}"), "+", KLiteral(this.offset.toString()))
                } else {
                    KLiteral("0x${addr.toString(16).uppercase()}")
                }
                KMemberAccess(KVar("memory"), KBinaryOp(baseAddr, "+", index), isIndexed = true)
            } else {
                // Named label with index - use indexed delegate
                val propName = ctx.registerIndexedAccess(this.label)
                if (this.offset != 0) {
                    // property[offset + Y]
                    val indexExpr = KBinaryOp(KLiteral(this.offset.toString()), "+", index)
                    KMemberAccess(KVar(propName), indexExpr, isIndexed = true)
                } else {
                    // property[Y]
                    KMemberAccess(KVar(propName), index, isIndexed = true)
                }
            }
        }

        is AssemblyAddressing.IndirectX -> {
            // (zp,X) - Indexed indirect: Read pointer from (zp + X), then access that address
            // 6502 semantics: ptr = mem[zp+X] | (mem[zp+X+1] << 8), result = mem[ptr]
            // Note: zp+X wraps within zero page (and 0xFF)
            val x = ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X")
            // Build base address expression
            val baseExpr: KotlinExpr = if (this.label.startsWith("$")) {
                val addr = this.label.substring(1).toIntOrNull(16) ?: 0
                if (this.offset != 0) {
                    KBinaryOp(KLiteral("0x${addr.toString(16).uppercase()}"), "+", KLiteral(this.offset.toString()))
                } else {
                    KLiteral("0x${addr.toString(16).uppercase()}")
                }
            } else {
                // Named labels use original case to match constants file
                val baseLabel = this.label
                if (this.offset != 0) {
                    KBinaryOp(KVar(baseLabel), "+", KLiteral(this.offset.toString()))
                } else {
                    KVar(baseLabel)
                }
            }
            // memory[readWord((base + X) and 0xFF)]
            val zpAddr = KBinaryOp(
                KParen(KBinaryOp(baseExpr, "+", x)),
                "and",
                KLiteral("0xFF")
            )
            KMemberAccess(
                KVar("memory"),
                KCall("readWord", listOf(zpAddr)),
                isIndexed = true
            )
        }

        is AssemblyAddressing.IndirectY -> {
            // (zp),Y - Indirect indexed: Read pointer from zp, add Y to get final address
            // 6502 semantics: ptr = mem[zp] | (mem[zp+1] << 8), result = mem[ptr + Y]
            val y = ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y")
            // Build base address expression
            val baseExpr: KotlinExpr = if (this.label.startsWith("$")) {
                val addr = this.label.substring(1).toIntOrNull(16) ?: 0
                if (this.offset != 0) {
                    KBinaryOp(KLiteral("0x${addr.toString(16).uppercase()}"), "+", KLiteral(this.offset.toString()))
                } else {
                    KLiteral("0x${addr.toString(16).uppercase()}")
                }
            } else {
                // Named labels use original case to match constants file
                val baseLabel = this.label
                if (this.offset != 0) {
                    KBinaryOp(KVar(baseLabel), "+", KLiteral(this.offset.toString()))
                } else {
                    KVar(baseLabel)
                }
            }
            // memory[readWord(base) + Y]
            val ptrAddr = KCall("readWord", listOf(baseExpr))
            val finalAddr = KBinaryOp(ptrAddr, "+", y)
            KMemberAccess(
                KVar("memory"),
                finalAddr,
                isIndexed = true
            )
        }

        is AssemblyAddressing.IndirectAbsolute -> {
            // (addr) - Absolute indirect: Read 16-bit pointer from addr, use as target
            // Only used by JMP instruction
            // 6502 semantics: target = mem[addr] | (mem[addr+1] << 8)
            val baseExpr: KotlinExpr = if (this.label.startsWith("$")) {
                val addr = this.label.substring(1).toIntOrNull(16) ?: 0
                if (this.offset != 0) {
                    KBinaryOp(KLiteral("0x${addr.toString(16).uppercase()}"), "+", KLiteral(this.offset.toString()))
                } else {
                    KLiteral("0x${addr.toString(16).uppercase()}")
                }
            } else {
                // Named labels use original case to match constants file
                val baseLabel = this.label
                if (this.offset != 0) {
                    KBinaryOp(KVar(baseLabel), "+", KLiteral(this.offset.toString()))
                } else {
                    KVar(baseLabel)
                }
            }
            // readWord(base) - returns the address to jump to
            KCall("readWord", listOf(baseExpr))
        }

        is AssemblyAddressing.ValueLowerSelection -> {
            // <value - low byte
            KBinaryOp(this.value.value.toKotlinExpr(ctx), "and", KLiteral("0xFF"))
        }

        is AssemblyAddressing.ValueUpperSelection -> {
            // >value - high byte
            KBinaryOp(this.value.value.toKotlinExpr(ctx), "shr", KLiteral("8"))
        }

        else -> {
            KLiteral("/* TODO: ${this} */")
        }
    }
}

/**
 * Helper to convert UShort to Kotlin expression.
 */
fun UShort.toKotlinExpr(ctx: CodeGenContext): KotlinExpr {
    return KLiteral("0x${this.toString(16).uppercase().padStart(4, '0')}")
}
