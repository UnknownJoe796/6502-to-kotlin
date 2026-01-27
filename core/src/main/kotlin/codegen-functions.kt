package com.ivieleague.decompiler6502tokotlin.hand

/**
 * by Claude - Function-level code generation.
 *
 * Contains toKotlinFunction, preScanMemoryAccesses, and generateEmptyFunctionCode.
 */

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
    // by Claude - Handle "empty functions" specially
    // These are functions whose starting block is owned by another function (via BIT skip pattern)
    // We need to generate code that sets the appropriate register values and calls the target
    val fallsToFunc = this.emptyFunctionFallsTo
    if (fallsToFunc != null) {
        return generateEmptyFunctionCode(functionRegistry, fallThroughGraph)
    }

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

        // by Claude - FIX: Don't stop at return! Continue processing all control nodes.
        // Some blocks after an RTS are still reachable via branches (like CheckSideMTiles
        // which is reached via branches from inside SideCheckLoop, but comes after ExSCH's RTS
        // in layout order). The control flow analysis already determined what's reachable.

        i++
    }

    // by Claude - Post-process: Handle flagged break targets that come after a simple return
    // Pattern we're looking for in body:
    //   ...code...
    //   return           <- from simple RTS block (like ExSCH)
    //   ...more code...  <- from flagged break target (like CheckSideMTiles)
    //
    // We need to restructure this to:
    //   ...code...
    //   if (exitFlag0) {   <- check the flag
    //       ...more code... <- flagged target code
    //   }
    //   return
    if (ctx.breakTargetFlags.isNotEmpty()) {
        val restructured = mutableListOf<KotlinStmt>()
        var j = 0
        while (j < body.size) {
            val stmt = body[j]

            // Look for: return followed by more statements
            if (stmt is KReturn && j + 1 < body.size) {
                // Collect all statements after this return
                val afterReturn = body.drop(j + 1)
                if (afterReturn.isNotEmpty()) {
                    // Find which flag this code belongs to
                    // The flags were registered for specific blocks - find matching flag
                    val flagsToCheck = ctx.breakTargetFlags.values.toList()

                    if (flagsToCheck.isNotEmpty()) {
                        // Generate if (flag) { afterReturn code }
                        // For multiple flags, we'd need multiple if blocks, but typically there's just one
                        for (flagName in flagsToCheck) {
                            val ifBlock = KIf(
                                condition = KVar(flagName),
                                thenBranch = afterReturn,
                                elseBranch = emptyList()
                            )
                            restructured.add(ifBlock)
                        }
                        restructured.add(stmt) // Add the return after the if blocks
                        break // Done processing
                    }
                }
            }

            restructured.add(stmt)
            j++
        }

        // Replace body with restructured version
        body.clear()
        body.addAll(restructured)
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

    // by Claude - Declare break target flag variables at function level
    // These are used to track which loop exit path was taken when a loop has
    // multiple break targets (some with code, some simple RTS)
    for ((_, flagName) in ctx.breakTargetFlags) {
        finalBody.add(KVarDecl(flagName, "Boolean", KLiteral("false"), mutable = true))
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

                    // by Claude - For tail calls, if the target function returns a value and this function
                    // should return the same type, use `return targetFunction()` to propagate the return value
                    val thisHasAOutput = this.outputs?.contains(TrackedAsIo.A) == true
                    val thisHasXOutput = this.outputs?.contains(TrackedAsIo.X) == true
                    val thisHasYOutput = this.outputs?.contains(TrackedAsIo.Y) == true
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
                        finalBody.add(KReturn(KCall(targetName, args)))
                    } else {
                        // Not a tail call for return value - call and return separately
                        finalBody.add(KExprStmt(KCall(targetName, args)))
                        // by Claude - Fixed: handle all multi-register output combinations (X+Y, A+X, etc.)
                        val returnValue: KotlinExpr? = when {
                            thisOutputCount >= 2 -> {
                                // Multi-register return as Pair - determine which two registers
                                val firstReg = when {
                                    thisHasAOutput -> "A"
                                    thisHasXOutput -> "X"
                                    else -> "Y"
                                }
                                val secondReg = when {
                                    thisHasYOutput -> "Y"
                                    thisHasXOutput && firstReg != "X" -> "X"
                                    else -> "Y" // fallback
                                }
                                KCall("Pair", listOf(
                                    getRegisterValueOrDefault(firstReg, ctx),
                                    getRegisterValueOrDefault(secondReg, ctx)
                                ))
                            }
                            thisHasAOutput -> getRegisterValueOrDefault("A", ctx)
                            thisHasXOutput -> getRegisterValueOrDefault("X", ctx)
                            thisHasYOutput -> getRegisterValueOrDefault("Y", ctx)
                            else -> null
                        }
                        finalBody.add(KReturn(returnValue))
                    }
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

// by Claude - Generate code for "empty functions" whose starting block is owned by another function
// These functions exist due to the BIT skip pattern (e.g., BlockBufferColli_Side)
// We need to execute the starting block's instructions (without skipping) and call the target
private fun AssemblyFunction.generateEmptyFunctionCode(
    functionRegistry: Map<String, AssemblyFunction>,
    fallThroughGraph: Map<AssemblyFunction, Set<AssemblyFunction>>
): KFunction {
    val body = mutableListOf<KotlinStmt>()
    val params = mutableListOf<KParam>()

    // Collect register values from the starting block's instructions
    // These are the values that would be set by executing this function's code
    var aValue: KotlinExpr? = null
    var xValue: KotlinExpr? = null
    var yValue: KotlinExpr? = null

    // Helper to convert immediate value to Kotlin expression
    fun immediateToExpr(addr: AssemblyAddressing.Value): KotlinExpr {
        return when (addr) {
            is AssemblyAddressing.ByteValue -> KLiteral("0x${addr.value.toString(16).uppercase().padStart(2, '0')}")
            is AssemblyAddressing.ShortValue -> KLiteral("0x${addr.value.toString(16).uppercase().padStart(4, '0')}")
            is AssemblyAddressing.ConstantReference -> KVar(addr.name)
            is AssemblyAddressing.ConstantReferenceLower -> KCall("lowByte", listOf(KVar(addr.name)))
            is AssemblyAddressing.ConstantReferenceUpper -> KCall("highByte", listOf(KVar(addr.name)))
            is AssemblyAddressing.ValueLowerSelection -> KCall("lowByte", listOf(KLiteral("0x${addr.value.value.toString(16)}")))
            is AssemblyAddressing.ValueUpperSelection -> KCall("highByte", listOf(KLiteral("0x${addr.value.value.toString(16)}")))
        }
    }

    // Scan the starting block for LDA/LDX/LDY immediate values
    // These set register values before the fall-through to the target function
    for (line in startingBlock.lines) {
        val instr = line.instruction ?: continue
        when (instr.op) {
            AssemblyOp.LDA -> {
                val addr = instr.address
                if (addr is AssemblyAddressing.Value) {
                    aValue = immediateToExpr(addr)
                }
            }
            AssemblyOp.LDX -> {
                val addr = instr.address
                if (addr is AssemblyAddressing.Value) {
                    xValue = immediateToExpr(addr)
                }
            }
            AssemblyOp.LDY -> {
                val addr = instr.address
                if (addr is AssemblyAddressing.Value) {
                    yValue = immediateToExpr(addr)
                }
            }
            else -> { /* ignore other instructions for now */ }
        }
    }

    // Add parameters only for inputs that are NOT set by the starting block's code
    // If the block sets a register explicitly (e.g., lda #$01), it's not a parameter
    if (this.inputs?.contains(TrackedAsIo.A) == true && aValue == null) {
        params.add(KParam("A", "Int"))
        aValue = KVar("A")
    }
    if (this.inputs?.contains(TrackedAsIo.X) == true && xValue == null) {
        params.add(KParam("X", "Int"))
        xValue = KVar("X")
    }
    if (this.inputs?.contains(TrackedAsIo.Y) == true && yValue == null) {
        params.add(KParam("Y", "Int"))
        yValue = KVar("Y")
    }

    // Get the target function (the one we fall through to)
    val targetFunc = emptyFunctionFallsTo!!
    val targetName = targetFunc.startingBlock.label?.let { assemblyLabelToKotlinName(it) }
        ?: "func_${targetFunc.startingBlock.originalLineIndex}"

    // Build arguments for the target function call
    val args = mutableListOf<KotlinExpr>()
    if (targetFunc.inputs?.contains(TrackedAsIo.A) == true) {
        args.add(aValue ?: KLiteral("0"))
    }
    if (targetFunc.inputs?.contains(TrackedAsIo.X) == true) {
        args.add(xValue ?: KLiteral("0"))
    }
    if (targetFunc.inputs?.contains(TrackedAsIo.Y) == true) {
        args.add(yValue ?: KLiteral("0"))
    }

    // Determine return type based on outputs
    val hasAOutput = this.outputs?.contains(TrackedAsIo.A) == true
    val hasXOutput = this.outputs?.contains(TrackedAsIo.X) == true
    val hasYOutput = this.outputs?.contains(TrackedAsIo.Y) == true
    val outputCount = listOf(hasAOutput, hasXOutput, hasYOutput).count { it }

    val returnType = when {
        outputCount >= 2 -> "Pair<Int, Int>"
        hasAOutput || hasXOutput || hasYOutput -> "Int"
        else -> null
    }

    // Generate the tail call
    if (returnType != null) {
        body.add(KReturn(KCall(targetName, args)))
    } else {
        body.add(KExprStmt(KCall(targetName, args)))
        body.add(KReturn(null))
    }

    // Generate function name
    val functionName = this.startingBlock.label?.let { assemblyLabelToKotlinName(it) }
        ?: "func_${this.startingBlock.originalLineIndex}"

    return KFunction(
        name = functionName,
        params = params,
        returnType = returnType,
        body = body,
        comment = "Decompiled from ${this.startingBlock.label ?: "@${this.startingBlock.originalLineIndex}"}"
    )
}
