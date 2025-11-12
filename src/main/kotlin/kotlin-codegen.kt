package com.ivieleague.decompiler6502tokotlin.hand

/**
 * Code generation: Convert control flow structures and 6502 instructions to Kotlin AST.
 *
 * This is the main code generation pipeline that produces actual Kotlin code from
 * the analyzed assembly structure.
 */

/**
 * Context for code generation - tracks state during conversion.
 */
class CodeGenContext(
    /** Function registry to look up function signatures for call sites */
    val functionRegistry: Map<String, AssemblyFunction> = emptyMap()
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
 */
fun AssemblyInstruction.toKotlin(ctx: CodeGenContext): List<KotlinStmt> {
    val stmts = mutableListOf<KotlinStmt>()

    when (this.op) {
        // ===========================
        // Load instructions
        // ===========================
        AssemblyOp.LDA -> {
            val value = this.address.toKotlinExpr(ctx)
            ctx.registerA = value
            // Emit assignment statement
            stmts.add(KAssignment(KVar("A"), value))
            // Update Z and N flags
            stmts.add(KExprStmt(KCall("updateZN", listOf(KVar("A")))))
        }

        AssemblyOp.LDX -> {
            val value = this.address.toKotlinExpr(ctx)
            ctx.registerX = value
            // Emit assignment statement
            stmts.add(KAssignment(KVar("X"), value))
            // Update Z and N flags
            stmts.add(KExprStmt(KCall("updateZN", listOf(KVar("X")))))
        }

        AssemblyOp.LDY -> {
            val value = this.address.toKotlinExpr(ctx)
            ctx.registerY = value
            // Emit assignment statement
            stmts.add(KAssignment(KVar("Y"), value))
            // Update Z and N flags
            stmts.add(KExprStmt(KCall("updateZN", listOf(KVar("Y")))))
        }

        // ===========================
        // Store instructions
        // ===========================
        AssemblyOp.STA -> {
            val target = this.address.toKotlinExpr(ctx)
            val value = ctx.registerA ?: KVar("A")
            stmts.add(KAssignment(target, value))
        }

        AssemblyOp.STX -> {
            val target = this.address.toKotlinExpr(ctx)
            val value = ctx.registerX ?: KVar("X")
            stmts.add(KAssignment(target, value))
        }

        AssemblyOp.STY -> {
            val target = this.address.toKotlinExpr(ctx)
            val value = ctx.registerY ?: KVar("Y")
            stmts.add(KAssignment(target, value))
        }

        // ===========================
        // Transfer instructions
        // ===========================
        AssemblyOp.TAX -> {
            val value = ctx.registerA ?: KVar("A")
            ctx.registerX = value
            stmts.add(KAssignment(KVar("X"), value))
            stmts.add(KExprStmt(KCall("updateZN", listOf(KVar("X")))))
        }

        AssemblyOp.TAY -> {
            val value = ctx.registerA ?: KVar("A")
            ctx.registerY = value
            stmts.add(KAssignment(KVar("Y"), value))
            stmts.add(KExprStmt(KCall("updateZN", listOf(KVar("Y")))))
        }

        AssemblyOp.TXA -> {
            val value = ctx.registerX ?: KVar("X")
            ctx.registerA = value
            stmts.add(KAssignment(KVar("A"), value))
            stmts.add(KExprStmt(KCall("updateZN", listOf(KVar("A")))))
        }

        AssemblyOp.TYA -> {
            val value = ctx.registerY ?: KVar("Y")
            ctx.registerA = value
            stmts.add(KAssignment(KVar("A"), value))
            stmts.add(KExprStmt(KCall("updateZN", listOf(KVar("A")))))
        }

        // ===========================
        // Arithmetic instructions
        // ===========================
        AssemblyOp.ADC -> {
            val operand = this.address.toKotlinExpr(ctx)
            val a = ctx.registerA ?: KVar("A")

            // Generate: val sum = A + operand + (if (C) 1 else 0)
            val carryValue = KIfExpr(KVar("C"), KLiteral("1"), KLiteral("0"))
            val sum = KBinaryOp(KBinaryOp(a, "+", operand), "+", carryValue)

            // C = sum > 0xFF
            stmts.add(KAssignment(KVar("C"), KBinaryOp(sum, ">", KLiteral("0xFF"))))

            // A = sum and 0xFF
            val result = KBinaryOp(KParen(sum), "and", KLiteral("0xFF"))
            ctx.registerA = result
            stmts.add(KAssignment(KVar("A"), result))

            // Update Z and N flags
            stmts.add(KExprStmt(KCall("updateZN", listOf(KVar("A")))))

            // TODO: V flag (overflow) - complex calculation
        }

        AssemblyOp.SBC -> {
            val operand = this.address.toKotlinExpr(ctx)
            val a = ctx.registerA ?: KVar("A")

            // Generate: val diff = A - operand - (if (C) 0 else 1)
            val borrow = KIfExpr(KVar("C"), KLiteral("0"), KLiteral("1"))
            val diff = KBinaryOp(KBinaryOp(a, "-", operand), "-", borrow)

            // C = diff >= 0 (no borrow)
            stmts.add(KAssignment(KVar("C"), KBinaryOp(diff, ">=", KLiteral("0"))))

            // A = diff and 0xFF
            val result = KBinaryOp(KParen(diff), "and", KLiteral("0xFF"))
            ctx.registerA = result
            stmts.add(KAssignment(KVar("A"), result))

            // Update Z and N flags
            stmts.add(KExprStmt(KCall("updateZN", listOf(KVar("A")))))

            // TODO: V flag (overflow) - complex calculation
        }

        AssemblyOp.INC -> {
            val target = this.address.toKotlinExpr(ctx)
            val incremented = KBinaryOp(KParen(KBinaryOp(target, "+", KLiteral("1"))), "and", KLiteral("0xFF"))
            stmts.add(KAssignment(target, incremented))
        }

        AssemblyOp.DEC -> {
            val target = this.address.toKotlinExpr(ctx)
            val decremented = KBinaryOp(KParen(KBinaryOp(target, "-", KLiteral("1"))), "and", KLiteral("0xFF"))
            stmts.add(KAssignment(target, decremented))
        }

        AssemblyOp.INX -> {
            val x = ctx.registerX ?: KVar("X")
            val incremented = KBinaryOp(KParen(KBinaryOp(x, "+", KLiteral("1"))), "and", KLiteral("0xFF"))
            ctx.registerX = incremented
            stmts.add(KAssignment(KVar("X"), incremented))
            stmts.add(KExprStmt(KCall("updateZN", listOf(KVar("X")))))
        }

        AssemblyOp.INY -> {
            val y = ctx.registerY ?: KVar("Y")
            val incremented = KBinaryOp(KParen(KBinaryOp(y, "+", KLiteral("1"))), "and", KLiteral("0xFF"))
            ctx.registerY = incremented
            stmts.add(KAssignment(KVar("Y"), incremented))
            stmts.add(KExprStmt(KCall("updateZN", listOf(KVar("Y")))))
        }

        AssemblyOp.DEX -> {
            val x = ctx.registerX ?: KVar("X")
            val decremented = KBinaryOp(KParen(KBinaryOp(x, "-", KLiteral("1"))), "and", KLiteral("0xFF"))
            ctx.registerX = decremented
            stmts.add(KAssignment(KVar("X"), decremented))
            stmts.add(KExprStmt(KCall("updateZN", listOf(KVar("X")))))
        }

        AssemblyOp.DEY -> {
            val y = ctx.registerY ?: KVar("Y")
            val decremented = KBinaryOp(KParen(KBinaryOp(y, "-", KLiteral("1"))), "and", KLiteral("0xFF"))
            ctx.registerY = decremented
            stmts.add(KAssignment(KVar("Y"), decremented))
            stmts.add(KExprStmt(KCall("updateZN", listOf(KVar("Y")))))
        }

        // ===========================
        // Logical instructions
        // ===========================
        AssemblyOp.AND -> {
            val operand = this.address.toKotlinExpr(ctx)
            val a = ctx.registerA ?: KVar("A")
            val result = KBinaryOp(a, "and", operand)
            ctx.registerA = result
            stmts.add(KAssignment(KVar("A"), result))
            stmts.add(KExprStmt(KCall("updateZN", listOf(KVar("A")))))
        }

        AssemblyOp.ORA -> {
            val operand = this.address.toKotlinExpr(ctx)
            val a = ctx.registerA ?: KVar("A")
            val result = KBinaryOp(a, "or", operand)
            ctx.registerA = result
            stmts.add(KAssignment(KVar("A"), result))
            stmts.add(KExprStmt(KCall("updateZN", listOf(KVar("A")))))
        }

        AssemblyOp.EOR -> {
            val operand = this.address.toKotlinExpr(ctx)
            val a = ctx.registerA ?: KVar("A")
            val result = KBinaryOp(a, "xor", operand)
            ctx.registerA = result
            stmts.add(KAssignment(KVar("A"), result))
            stmts.add(KExprStmt(KCall("updateZN", listOf(KVar("A")))))
        }

        // ===========================
        // Shift instructions
        // ===========================
        AssemblyOp.ASL -> {
            if (this.address == null) {
                // Accumulator mode
                val a = ctx.registerA ?: KVar("A")
                ctx.registerA = KBinaryOp(KParen(KBinaryOp(a, "shl", KLiteral("1"))), "and", KLiteral("0xFF"))
            } else {
                val target = this.address.toKotlinExpr(ctx)
                val shifted = KBinaryOp(KParen(KBinaryOp(target, "shl", KLiteral("1"))), "and", KLiteral("0xFF"))
                stmts.add(KAssignment(target, shifted))
            }
        }

        AssemblyOp.LSR -> {
            if (this.address == null) {
                // Accumulator mode
                val a = ctx.registerA ?: KVar("A")
                ctx.registerA = KBinaryOp(a, "shr", KLiteral("1"))
            } else {
                val target = this.address.toKotlinExpr(ctx)
                val shifted = KBinaryOp(target, "shr", KLiteral("1"))
                stmts.add(KAssignment(target, shifted))
            }
        }

        // ===========================
        // Compare instructions
        // ===========================
        AssemblyOp.CMP -> {
            val operand = this.address.toKotlinExpr(ctx)
            val a = ctx.registerA ?: KVar("A")

            // Set flags based on comparison
            val diff = KBinaryOp(a, "-", operand)
            ctx.zeroFlag = KBinaryOp(diff, "==", KLiteral("0"))
            ctx.carryFlag = KBinaryOp(a, ">=", operand)
            ctx.negativeFlag = KBinaryOp(diff, "<", KLiteral("0"))
        }

        AssemblyOp.CPX -> {
            val operand = this.address.toKotlinExpr(ctx)
            val x = ctx.registerX ?: KVar("X")
            ctx.zeroFlag = KBinaryOp(x, "==", operand)
            ctx.carryFlag = KBinaryOp(x, ">=", operand)
        }

        AssemblyOp.CPY -> {
            val operand = this.address.toKotlinExpr(ctx)
            val y = ctx.registerY ?: KVar("Y")
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
            val target = this.address.toKotlinExpr(ctx)

            // Get the assembly label and convert it to Kotlin function name format
            val assemblyLabel = target.toKotlin()
            val functionName = assemblyLabelToKotlinName(assemblyLabel)

            // Look up the target function to determine what parameters to pass
            val targetFunction = ctx.functionRegistry[functionName]

            // Build argument list based on target function's inputs
            val args = mutableListOf<KotlinExpr>()
            if (targetFunction?.inputs != null) {
                if (TrackedAsIo.A in targetFunction.inputs!!) {
                    args.add(ctx.registerA ?: KVar("A"))
                }
                if (TrackedAsIo.X in targetFunction.inputs!!) {
                    args.add(ctx.registerX ?: KVar("X"))
                }
                if (TrackedAsIo.Y in targetFunction.inputs!!) {
                    args.add(ctx.registerY ?: KVar("Y"))
                }
            }

            stmts.add(KExprStmt(KCall(functionName, args)))
        }

        AssemblyOp.RTS, AssemblyOp.RTI -> {
            stmts.add(KReturn())
        }

        // ===========================
        // Stack instructions
        // ===========================
        AssemblyOp.PHA -> {
            // Push A to stack - no Kotlin equivalent, handled by register tracking
        }

        AssemblyOp.PLA -> {
            // Pull A from stack - no Kotlin equivalent, handled by register tracking
        }

        // ===========================
        // Flag instructions
        // ===========================
        AssemblyOp.CLC -> {
            ctx.carryFlag = KLiteral("0")
        }

        AssemblyOp.SEC -> {
            ctx.carryFlag = KLiteral("1")
        }

        AssemblyOp.CLV -> {
            ctx.overflowFlag = KLiteral("0")
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

            // Then add the if statement
            val condition = this.condition.toKotlinExpr(ctx)
            val thenStmts = this.thenBranch.flatMap { it.toKotlin(ctx) }
            val elseStmts = this.elseBranch.flatMap { it.toKotlin(ctx) }

            result.add(KIf(condition, thenStmts, elseStmts))

            result
        }

        is LoopNode -> {
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

            listOf(loop)
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
            // TODO: Implement switch conversion
            listOf(KComment("TODO: switch"))
        }
    }
}

/**
 * Convert a Condition to a Kotlin expression.
 */
fun Condition.toKotlinExpr(ctx: CodeGenContext): KotlinExpr {
    // Get the branch instruction
    val branchInstr = this.branchLine.instruction ?: return KLiteral("/* unknown condition */")

    // Find the instruction that set the flags for this branch
    val flagSetter = this.branchBlock.findFlagSetterForBranch()

    // Build the flag expression from the flag setter instruction
    val flagExpr = if (flagSetter?.instruction != null) {
        // Create a temporary context to convert the flag setter and its dependencies
        val tempCtx = CodeGenContext()

        // Find the block containing the flag setter
        val setterBlock = this.branchBlock
        val setterIndex = setterBlock.lines.indexOf(flagSetter)

        // Process instructions in reverse to build register state
        // Look back up to 10 instructions for register assignments
        val startIndex = maxOf(0, setterIndex - 10)
        for (i in startIndex until setterIndex) {
            val line = setterBlock.lines[i]
            line.instruction?.toKotlin(tempCtx)
        }

        // Now process the flag setter itself
        flagSetter.instruction.toKotlin(tempCtx)

        // Extract the flag expression based on which flag the branch tests
        when (branchInstr.op) {
            AssemblyOp.BEQ, AssemblyOp.BNE -> tempCtx.zeroFlag ?: KLiteral("zeroFlag")
            AssemblyOp.BCC, AssemblyOp.BCS -> tempCtx.carryFlag ?: KLiteral("carryFlag")
            AssemblyOp.BMI, AssemblyOp.BPL -> tempCtx.negativeFlag ?: KLiteral("negativeFlag")
            AssemblyOp.BVC, AssemblyOp.BVS -> tempCtx.overflowFlag ?: KLiteral("overflowFlag")
            else -> KLiteral("/* unknown branch */")
        }
    } else {
        // Fall back to context if no flag setter found
        when (branchInstr.op) {
            AssemblyOp.BEQ, AssemblyOp.BNE -> ctx.zeroFlag ?: KLiteral("zeroFlag")
            AssemblyOp.BCC, AssemblyOp.BCS -> ctx.carryFlag ?: KLiteral("carryFlag")
            AssemblyOp.BMI, AssemblyOp.BPL -> ctx.negativeFlag ?: KLiteral("negativeFlag")
            AssemblyOp.BVC, AssemblyOp.BVS -> ctx.overflowFlag ?: KLiteral("overflowFlag")
            else -> KLiteral("/* unknown branch */")
        }
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

    return finalExpr
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
            stmts.addAll(instr.toKotlin(ctx))
        }
    }

    return stmts
}

/**
 * Convert an AssemblyFunction to a KFunction.
 * @param functionRegistry Map of function names to AssemblyFunction for looking up call signatures
 */
fun AssemblyFunction.toKotlinFunction(functionRegistry: Map<String, AssemblyFunction> = emptyMap()): KFunction {
    val ctx = CodeGenContext(functionRegistry)

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

    // Prepend register declarations for registers that are NOT parameters but are used
    val finalBody = mutableListOf<KotlinStmt>()
    if (usesA && !aIsParam) finalBody.add(KVarDecl("A", "Int", KLiteral("0"), mutable = true))
    if (usesX && !xIsParam) finalBody.add(KVarDecl("X", "Int", KLiteral("0"), mutable = true))
    if (usesY && !yIsParam) finalBody.add(KVarDecl("Y", "Int", KLiteral("0"), mutable = true))
    finalBody.addAll(body)

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
            KVar(sanitizeLabel(this.name))
        }

        is AssemblyAddressing.Direct -> {
            KVar(sanitizeLabel(this.label))
        }

        is AssemblyAddressing.DirectX -> {
            val index = ctx.registerX ?: KVar("X")
            KMemberAccess(KVar(sanitizeLabel(this.label)), index, isIndexed = true)
        }

        is AssemblyAddressing.DirectY -> {
            val index = ctx.registerY ?: KVar("Y")
            KMemberAccess(KVar(sanitizeLabel(this.label)), index, isIndexed = true)
        }

        is AssemblyAddressing.IndirectX -> {
            // (label,X) - indirect indexed by X
            KComment("/* indirect X: ${this} */").let { KVar("TODO") }
        }

        is AssemblyAddressing.IndirectY -> {
            // (label),Y - indirect indexed by Y
            KComment("/* indirect Y: ${this} */").let { KVar("TODO") }
        }

        is AssemblyAddressing.IndirectAbsolute -> {
            // (label) - absolute indirect
            KComment("/* indirect: ${this} */").let { KVar("TODO") }
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
