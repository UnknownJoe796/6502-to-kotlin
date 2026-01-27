package com.ivieleague.decompiler6502tokotlin.hand

/**
 * by Claude - Control flow code generation.
 *
 * Contains ControlNode.toKotlin conversion and fall-through graph utilities.
 */

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
                val funcName = func.startingBlock.label
                val targetName = fallThrough.function!!.startingBlock.label
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
// by Claude - Check if adding a fall-through tail call from sourceFunc to targetFunc would create a cycle.
// A cycle exists if we can follow fall-through relationships from targetFunc back to sourceFunc.
fun wouldCreateFallThroughCycle(
    sourceFunc: AssemblyFunction,
    targetFunc: AssemblyFunction,
    fallThroughGraph: Map<AssemblyFunction, Set<AssemblyFunction>>
): Boolean {
    if (sourceFunc === targetFunc) return true

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
                        // by Claude - If this target has a flag, set it before breaking
                        val flagName = ctx.getBreakTargetFlag(branchTarget)
                        if (flagName != null) {
                            elseStmts.add(KAssignment(KVar(flagName), KLiteral("true")))
                        }
                        // Add labeled break to else branch
                        elseStmts.add(KLabeledBreak(breakLabel))
                    }
                }
            }

            // by Claude - Fix for internal forward branches with empty thenBranch (e.g., bpl/bmi consecutive branches)
            // When thenBranch has no actionable code (empty or only comments) and this is an internal forward branch
            // (like bmi LeftFrict after bpl RghtFrict), we need to generate code to jump to the branch target
            // instead of falling through incorrectly. This handles the pattern where mutually exclusive branches
            // (bpl/bmi) have their targets outside the current control flow range.
            val thenHasOnlyComments = thenStmts.all { it is KComment }
            if (thenHasOnlyComments && this.condition.sense) {
                // sense=true means branch-taken executes the then-branch
                // But then-branch is empty, so the branch target code isn't being generated here
                val branchTarget = branchBlock.branchExit
                val isInternalForwardBranch = branchTarget != null &&
                    branchTarget.function == branchBlock.function &&
                    branchTarget.originalLineIndex > branchBlock.originalLineIndex

                if (isInternalForwardBranch && branchTarget != null) {
                    // Check if branch target is a known function entry point or has a JMP to another function
                    val targetLabel = branchTarget.label ?: "internal_target"
                    val jmpInstr = branchTarget.lines.find { it.instruction?.op == AssemblyOp.JMP }?.instruction
                    val jmpTarget = (jmpInstr?.address as? AssemblyAddressing.Direct)?.label
                    val jmpTargetFunction = jmpTarget?.let { label ->
                        ctx.functionRegistry[assemblyLabelToKotlinName(label)]
                    }

                    // Generate a comment and return to prevent incorrect fall-through
                    // The actual branch target code should be generated elsewhere in the function
                    if (jmpTargetFunction != null && jmpTarget != null) {
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
                        thenStmts.toMutableList().also { stmts ->
                            stmts.add(KComment("goto $targetLabel -> $targetName (internal forward branch)", commentTypeIndicator = " "))
                            stmts.add(KExprStmt(KCall(targetName, args)))
                            stmts.add(ctx.generateFunctionReturn())
                        }.also { result.add(KIf(condition, it, elseStmts)); return result }
                    } else {
                        // No JMP target - just add a comment indicating the branch and return
                        // This at least prevents incorrect fall-through
                        // by Claude - Use generateFunctionReturn to properly handle functions that return values
                        thenStmts.toMutableList().also { stmts ->
                            stmts.add(KComment("goto $targetLabel (internal forward branch - code generated later)", commentTypeIndicator = " "))
                            stmts.add(ctx.generateFunctionReturn())
                        }.also { result.add(KIf(condition, it, elseStmts)); return result }
                    }
                }
            }

            // by Claude - Smart merge: if one branch terminates (return), use the other branch's state
            // This preserves flags from the continuing branch for loop conditions
            val thenTerminates = thenStmts.lastOrNull()?.isTerminating() == true
            val elseTerminates = elseStmts.lastOrNull()?.isTerminating() == true

            // by Claude - FIX: Only merge convertedBlocks from branches that reach the continuation.
            // If a branch terminates (returns), its convertedBlocks should NOT be included
            // because the continuation code is NOT reachable from that path.
            // This fixes the bug where LeftFrict was converted in a terminating path but then
            // was skipped when reached via a different path (JoypFrict -> BCC fall-through).
            ctx.convertedBlocks.clear()
            ctx.convertedBlocks.addAll(convertedBlocksBeforeBranch)
            if (!thenTerminates) ctx.convertedBlocks.addAll(thenConvertedBlocks)
            if (!elseTerminates) ctx.convertedBlocks.addAll(elseConvertedBlocks)

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

            // by Claude - Detect break targets with code (not simple RTS)
            // When a loop has multiple break targets where some have code and some are simple RTS,
            // we need flag variables to track which exit path was taken.
            // Example: CheckSideMTiles has code, ExSCH is just RTS
            // NOTE: We only register the flag here - the declaration is added at function level
            // in toKotlinFunction() to avoid scope issues with nested control structures.
            val breakTargetsWithCode = this.breakTargets.filter { !it.isSimpleRts() }
            val hasSimpleRtsTarget = this.breakTargets.any { it.isSimpleRts() }

            // Only generate flags if we have BOTH: targets with code AND a simple RTS target
            // This pattern indicates: break to target with code vs normal loop exit to RTS
            if (breakTargetsWithCode.isNotEmpty() && hasSimpleRtsTarget) {
                for (target in breakTargetsWithCode) {
                    // Check if we already have a flag for this target (avoid duplicates in nested loops)
                    if (ctx.breakTargetFlags[target] == null) {
                        val flagName = ctx.nextBreakFlag()
                        ctx.breakTargetFlags[target] = flagName
                    }
                    // NOTE: Flag declaration is added at function level, not here
                }
            }

            // by Claude - Push loop onto stack for labeled break and continue support
            // - Header is needed for continue detection (branches back to loop start)
            // - Break targets are needed for break detection (branches to loop exit)
            val loopLabel = ctx.pushLoop(this.header, this.breakTargets)
            val needsLabel = this.breakTargets.isNotEmpty() || this.continueTargets.isNotEmpty()

            // by Claude - For PreTest (while) loops, include header block instructions in the body.
            // In 6502 code, the loop header often contains both body instructions (like INX, CPX)
            // and the condition check (like BEQ). The structural analysis separates the header from
            // the body, but for correct code generation, we need to emit the header's non-branch
            // instructions at the start of each loop iteration.
            val headerStmts = if (this.kind == LoopKind.PreTest) {
                val stmts = mutableListOf<KotlinStmt>()
                for (line in this.header.lines) {
                    // Add original assembly line as comment
                    if (line.originalLine != null) {
                        val trimmedLine = line.originalLine.trim()
                        if (trimmedLine.isNotEmpty()) {
                            stmts.add(KComment(trimmedLine, commentTypeIndicator = ">"))
                        }
                    }
                    // Generate Kotlin code for the instruction (excluding branch instructions)
                    val instr = line.instruction
                    if (instr != null && !instr.op.isBranch) {
                        stmts.addAll(instr.toKotlin(ctx, line.originalLineIndex))
                    }
                }
                stmts
            } else {
                emptyList<KotlinStmt>()
            }

            // by Claude - CRITICAL FIX: For PreTest loops, save state AFTER processing header
            // instructions but BEFORE processing body. The header (like CMP) sets flags that
            // determine the loop condition, while the body (like SBC) may overwrite those flags.
            // We need to use the header's flag state for the condition, not the body's state.
            val postHeaderState = if (this.kind == LoopKind.PreTest) ctx.saveState() else null

            val bodyStmts = headerStmts + this.body.flatMap { it.toKotlin(ctx) }

            // by Claude - Pop loop from stack after body is processed
            ctx.popLoop()

            val loop = when (this.kind) {
                LoopKind.PreTest -> {
                    // by Claude - For PreTest loops, restore the flag state from after processing
                    // the header but before processing the body. This ensures the condition uses
                    // the flags set by the header's comparison (like CMP), not flags overwritten
                    // by the body (like SBC).
                    postHeaderState?.let { ctx.restoreState(it) }
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
            // by Claude - Enhanced GotoNode handling with diagnostics
            val targetLabel = this.to.label ?: "@${this.to.originalLineIndex}"
            val sourceLabel = this.from.label ?: "@${this.from.originalLineIndex}"

            // Check if this is actually a break target
            val breakLabel = ctx.findBreakLabel(this.to)
            if (breakLabel != null) {
                return listOf(KLabeledBreak(breakLabel))
            }

            // Check if this is actually a continue target
            val continueLabel = ctx.findContinueLabel(this.to)
            if (continueLabel != null) {
                return listOf(KLabeledContinue(continueLabel))
            }

            // Otherwise emit as a comment with diagnostic info
            // In a fully structured program, we shouldn't reach here
            listOf(
                KComment("UNSTRUCTURED: goto $targetLabel (from $sourceLabel)"),
                KComment("  -> This edge could not be represented by structured control flow")
            )
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
