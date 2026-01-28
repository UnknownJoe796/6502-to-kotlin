package com.ivieleague.decompiler6502tokotlin.hand

/**
 * by Claude - Block-level code generation.
 *
 * Contains AssemblyBlock.toKotlin, Condition.toKotlinExpr, and related functions.
 */

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
        AssemblyOp.BMI, AssemblyOp.BPL -> {
            // by Claude - FIX: If negativeFlag is a literal boolean (e.g., from LSR which clears N),
            // it may be stale and should be reconstructed from the A register or instruction stream.
            // This fixes the "if (!false)" bug when a branch depends on N flag set by an earlier instruction
            // that was skipped (already converted block).
            val flagValue = ctx.negativeFlag
            val isLiteralBoolean = flagValue is KLiteral && (flagValue.value == "true" || flagValue.value == "false")

            if (flagValue != null && !isLiteralBoolean) {
                flagValue
            } else {
                // Fallback: build negative test from likely register
                // Most often N flag comes from A, Y, or X after LDA/DEY/DEX/etc.
                val reg = ctx.registerA ?: ctx.registerY ?: ctx.registerX
                    ?: ctx.getFunctionLevelVar("A") ?: ctx.getFunctionLevelVar("Y") ?: ctx.getFunctionLevelVar("X")
                    ?: KVar("A")
                KBinaryOp(KParen(KBinaryOp(reg, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            }
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
 * by Claude - Check if a block is a "simple RTS" block that only contains an RTS instruction.
 * Such blocks just return with no additional code to execute.
 */
fun AssemblyBlock.isSimpleRts(): Boolean {
    val instructions = this.lines.filter { it.instruction != null }.map { it.instruction!! }
    // A simple RTS block has exactly one instruction: RTS
    return instructions.size == 1 && instructions[0].op == AssemblyOp.RTS
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

        // by Claude - Check if branch target is a continue target (loop header) for an enclosing loop
        // If so, we need to generate a labeled continue. This handles the pattern:
        //   dec $00; bne SideCheckLoop (branch back to loop start if counter != 0)
        val targetIsContinueTarget = branchTarget != null && ctx.isContinueTarget(branchTarget)

        // Only generate orphaned branch handling if:
        // 1. Target is outside the function (exit), or
        // 2. Target is a break target for an enclosing loop, or
        // 3. Target is a continue target (loop header) for an enclosing loop, or
        // 4. Internal forward branch (to prevent incorrect fall-through, e.g., bpl/bmi patterns)
        // by Claude - Added isInternalForwardBranch to fix consecutive branch patterns like bpl/bmi
        if ((targetIsExit || targetIsBreakTarget || targetIsContinueTarget || isInternalForwardBranch) && branchTarget != null) {
            // Generate an if-statement for this orphaned branch
            // The condition is based on the branch type
            val condition = buildOrphanedBranchCondition(lastInstr, ctx)
            if (condition != null) {
                val targetLabel = branchTarget.label ?: "exit"

                // by Claude - Bug #2 fix: Check if branchTarget IS the entry point of another function
                // If so, generate a call to that function directly
                val targetFunc = branchTarget.function
                val targetIsFunctionEntry = targetIsExit && targetFunc != null &&
                    branchTarget == targetFunc.startingBlock

                // by Claude - Bug #1 fix: Check if branchTarget contains JMP to another function
                // If so, generate a tail call to that function instead of just returning
                val jmpInstr = branchTarget.lines.find { it.instruction?.op == AssemblyOp.JMP }?.instruction
                val jmpTarget = (jmpInstr?.address as? AssemblyAddressing.Direct)?.label
                val jmpTargetFunction = jmpTarget?.let { label ->
                    ctx.functionRegistry[assemblyLabelToKotlinName(label)]
                }

                val thenBody = if (targetIsContinueTarget) {
                    // by Claude - Branch to a continue target (loop header) - generate labeled continue
                    // This handles patterns like: dec $00; bne SideCheckLoop
                    val continueLabel = ctx.findContinueLabel(branchTarget)!!
                    listOf(
                        KComment("continue loop (branch back to $targetLabel)", commentTypeIndicator = " "),
                        KLabeledContinue(continueLabel)
                    )
                } else if (targetIsBreakTarget) {
                    // by Claude - Branch to a break target - generate labeled break
                    val breakLabel = ctx.findBreakLabel(branchTarget)!!
                    // by Claude - If this target has a flag, set it before breaking
                    val flagName = ctx.getBreakTargetFlag(branchTarget)
                    val stmtList = mutableListOf<KotlinStmt>()
                    stmtList.add(KComment("goto $targetLabel", commentTypeIndicator = " "))
                    if (flagName != null) {
                        stmtList.add(KAssignment(KVar(flagName), KLiteral("true")))
                    }
                    stmtList.add(KLabeledBreak(breakLabel))
                    stmtList.toList()
                } else if (isInternalForwardBranch) {
                    // by Claude - Internal forward branch: inline the target block's code if small enough
                    // This handles patterns like bpl/bmi where the branch goes to code
                    // elsewhere in the same function.
                    // To avoid JVM method size limits, we only inline small blocks.

                    // by Claude - Helper function to check if a block is a join point
                    // A join point is entered from multiple paths - count ALL entries regardless of function assignment
                    // This catches shared utility blocks that may have incorrect function ownership
                    fun isJoinPoint(block: AssemblyBlock): Boolean {
                        return block.enteredFrom.size > 1
                    }

                    // by Claude - Join points (blocks with multiple entry paths) need special handling.
                    // KNOWN ISSUE: The current approach (return early for Unit functions) is WRONG
                    // because the join point code still needs to execute on all paths.
                    // However, fixing this properly requires implementing labeled blocks with break/continue
                    // or restructuring the code generator to use proper control flow.
                    // TODO: Implement proper forward branch handling using labeled blocks.
                    val functionReturnsUnit = ctx.currentFunction?.outputs.isNullOrEmpty()
                    if (isJoinPoint(branchTarget) && functionReturnsUnit) {
                        listOf(
                            KComment("goto $targetLabel (shared join point - not inlined)", commentTypeIndicator = " "),
                            ctx.generateFunctionReturn()
                        )
                    } else {
                    // by Claude - Use more conservative inline limit for functions with many branches
                    // This helps prevent exponential code growth in complex functions
                    val blockCount = this.function?.blocks?.size ?: 10
                    val adjustedInlineLimit = if (blockCount > 50) 3 else 10

                    // Count instructions in the target path to decide whether to inline
                    // Stop at: RTS, JMP, external function, or JOIN POINT (multiple entries)
                    var totalInstructions = 0
                    var checkBlock: AssemblyBlock? = branchTarget
                    var checkLimit = adjustedInlineLimit  // by Claude - use same limit as inlining
                    while (checkBlock != null && checkLimit-- > 0) {
                        totalInstructions += checkBlock.lines.count { it.instruction != null }
                        val lastInstrCheck = checkBlock.lines.lastOrNull { it.instruction != null }?.instruction
                        if (lastInstrCheck?.op == AssemblyOp.RTS || lastInstrCheck?.op == AssemblyOp.JMP) break
                        val nextBlock = checkBlock.fallThroughExit
                        if (nextBlock?.function != this.function) break
                        // Stop at join points - blocks with multiple entry points
                        if (nextBlock != null && isJoinPoint(nextBlock)) break
                        checkBlock = nextBlock
                    }

                    // Only inline if target path is small (max 30 instructions to avoid bloat)
                    if (totalInstructions <= 30) {
                        val inlinedStmts = mutableListOf<KotlinStmt>()
                        inlinedStmts.add(KComment("goto $targetLabel (internal forward branch)", commentTypeIndicator = " "))

                        // Save FULL context state for inlining
                        val savedState = ctx.saveState()
                        val savedConvertedBlocks = ctx.convertedBlocks.toSet()

                        // Reset flag tracking for the inlined path
                        ctx.zeroFlag = null
                        ctx.carryFlag = null
                        ctx.negativeFlag = null
                        ctx.overflowFlag = null

                        ctx.convertedBlocks.removeAll(setOf(branchTarget))

                        var currentBlock: AssemblyBlock? = branchTarget
                        var inlineLimit = adjustedInlineLimit
                        while (currentBlock != null && inlineLimit-- > 0) {
                            if (currentBlock in ctx.convertedBlocks && currentBlock != branchTarget) break
                            inlinedStmts.addAll(currentBlock.toKotlin(ctx))
                            val lastInstrLoop = currentBlock.lines.lastOrNull { it.instruction != null }?.instruction
                            if (lastInstrLoop?.op == AssemblyOp.RTS || lastInstrLoop?.op == AssemblyOp.JMP) break
                            val nextBlock = currentBlock.fallThroughExit
                            if (nextBlock?.function != this.function) break
                            // Stop at join points - blocks with multiple entry points
                            if (nextBlock != null && isJoinPoint(nextBlock)) break
                            currentBlock = nextBlock
                        }

                        // by Claude - CRITICAL FIX: Capture ALL blocks converted during inlining
                        // including those converted in recursive toKotlin() calls.
                        // This prevents exponential code duplication where follow-through blocks
                        // get re-inlined from other branches (causing 52,682 -> 487,000+ lines!)
                        val allConvertedDuringInlining = ctx.convertedBlocks.toSet()

                        // Restore flag state but preserve the converted blocks
                        ctx.restoreState(savedState)
                        ctx.convertedBlocks.clear()
                        ctx.convertedBlocks.addAll(savedConvertedBlocks)
                        // Add ALL blocks that were converted during the entire inlining process
                        ctx.convertedBlocks.addAll(allConvertedDuringInlining)

                        val lastStmt = inlinedStmts.lastOrNull()
                        if (lastStmt !is KReturn) {
                            inlinedStmts.add(ctx.generateFunctionReturn())
                        }
                        inlinedStmts
                    } else {
                        // Target path too large - just generate comment and return
                        // This at least prevents incorrect fall-through, even if not fully correct
                        listOf(
                            KComment("goto $targetLabel (internal forward branch - not inlined due to size)", commentTypeIndicator = " "),
                            ctx.generateFunctionReturn()
                        )
                    }
                    }  // end of join point else block
                } else if (targetIsFunctionEntry) {
                    // by Claude - Branch to another function's entry point - generate call
                    val funcName = assemblyLabelToKotlinName(branchTarget.label!!)
                    val args = mutableListOf<KotlinExpr>()
                    if (targetFunc!!.inputs?.contains(TrackedAsIo.A) == true) {
                        args.add(getRegisterValueOrDefault("A", ctx))
                    }
                    if (targetFunc.inputs?.contains(TrackedAsIo.X) == true) {
                        args.add(getRegisterValueOrDefault("X", ctx))
                    }
                    if (targetFunc.inputs?.contains(TrackedAsIo.Y) == true) {
                        args.add(getRegisterValueOrDefault("Y", ctx))
                    }
                    // by Claude - Use generateFunctionReturn to properly handle functions that return values
                    listOf(
                        KComment("goto $targetLabel -> $funcName", commentTypeIndicator = " "),
                        KExprStmt(KCall(funcName, args)),
                        ctx.generateFunctionReturn()
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
                    // by Claude - Use generateFunctionReturn to properly handle functions that return values
                    listOf(
                        KComment("goto $targetLabel -> $targetName", commentTypeIndicator = " "),
                        KExprStmt(KCall(targetName, args)),
                        ctx.generateFunctionReturn()
                    )
                } else {
                    // by Claude - Use generateFunctionReturn to properly handle functions that return values
                    // No JMP target found, just return
                    listOf(
                        KComment("goto $targetLabel", commentTypeIndicator = " "),
                        ctx.generateFunctionReturn()
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
    // NOTE: Only do this if ctx.currentFunction is set - otherwise we can't determine
    // if the fall-through is to a different function.
    val fallThroughBlock = this.fallThroughExit
    if (fallThroughBlock != null && ctx.currentFunction != null &&
        fallThroughBlock.function != ctx.currentFunction && fallThroughBlock.function != null) {
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
            // by Claude - Use generateFunctionReturn to properly handle functions that return values
            stmts.add(ctx.generateFunctionReturn())
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

            // by Claude - For tail calls, if the target function returns a value and this function
            // should return the same type, use `return targetFunction()` to propagate the return value
            val thisHasAOutput = currentFunc?.outputs?.contains(TrackedAsIo.A) == true
            val thisHasXOutput = currentFunc?.outputs?.contains(TrackedAsIo.X) == true
            val thisHasYOutput = currentFunc?.outputs?.contains(TrackedAsIo.Y) == true
            val targetHasAOutput = targetFunction.outputs?.contains(TrackedAsIo.A) == true
            val targetHasXOutput = targetFunction.outputs?.contains(TrackedAsIo.X) == true
            val targetHasYOutput = targetFunction.outputs?.contains(TrackedAsIo.Y) == true

            // Count register outputs for both functions
            val thisOutputCount = listOf(thisHasAOutput, thisHasXOutput, thisHasYOutput).count { it }
            val targetOutputCount = listOf(targetHasAOutput, targetHasXOutput, targetHasYOutput).count { it }

            // Only use tail call return if BOTH functions have the SAME return type
            // (same number of outputs and matching registers)
            val canTailCallReturn = thisOutputCount == targetOutputCount && thisOutputCount > 0 &&
                (thisHasAOutput == targetHasAOutput) &&
                (thisHasXOutput == targetHasXOutput) &&
                (thisHasYOutput == targetHasYOutput)

            if (canTailCallReturn) {
                // True tail call - return the result of the function call directly
                stmts.add(KReturn(KCall(targetName, args)))
            } else {
                // Not a tail call for return value - call and return separately
                stmts.add(KExprStmt(KCall(targetName, args)))
                stmts.add(ctx.generateFunctionReturn())
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
