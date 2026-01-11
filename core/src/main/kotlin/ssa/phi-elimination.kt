// by Claude - Phi elimination for out-of-SSA translation
package com.ivieleague.decompiler6502tokotlin.hand.ssa

import com.ivieleague.decompiler6502tokotlin.hand.*

/**
 * Translates SSA form back to executable Kotlin code.
 *
 * Strategy:
 * 1. For phi nodes at join points (if-else merge): Insert copies at end of each predecessor
 * 2. For phi nodes at loop headers: Use mutable variables with initialization before loop
 *
 * This produces clean Kotlin code without explicit phi functions.
 */
class PhiEliminator(
    private val ssaResult: SSAResult,
    private val function: AssemblyFunction
) {
    /**
     * Variables that need to be declared as mutable at function start due to phi nodes.
     * Maps from variable name to its initial expression.
     */
    val mutableVariables = mutableMapOf<String, KotlinExpr>()

    /**
     * Copies to insert at the end of each block (before branch/jump).
     * Maps block to list of (varName, expression) assignments.
     */
    val blockCopies = mutableMapOf<AssemblyBlock, MutableList<Pair<String, KotlinExpr>>>()

    /**
     * Analyze phi nodes and determine what copies/declarations are needed.
     */
    fun analyze() {
        for ((block, phis) in ssaResult.phiNodes) {
            for (phi in phis) {
                analyzePhiNode(block, phi)
            }
        }
    }

    private fun analyzePhiNode(block: AssemblyBlock, phi: PhiNode) {
        val varName = phi.name  // e.g., "Y_3"

        // Check if this is a loop header phi (back-edge present)
        val isLoopHeader = block.enteredFrom.any { pred ->
            // A back-edge is when a predecessor is dominated by this block
            isDominatedBy(pred, block)
        }

        if (isLoopHeader) {
            // Loop phi: declare mutable variable with initial value from entry edge
            val entryPred = block.enteredFrom.find { !isDominatedBy(it, block) }
            val initialValue = if (entryPred != null) {
                phi.sources[entryPred]?.definingExpr ?: KVar(phi.target.paramName())
            } else {
                KVar(phi.target.paramName())
            }
            mutableVariables[varName] = initialValue

            // Insert copy from back-edge source at end of back-edge block
            for ((pred, value) in phi.sources) {
                if (isDominatedBy(pred, block)) {
                    // This is a back-edge - insert copy before the branch
                    val copies = blockCopies.getOrPut(pred) { mutableListOf() }
                    copies.add(varName to value.definingExpr)
                }
            }
        } else {
            // Join point phi: insert copies at each predecessor
            for ((pred, value) in phi.sources) {
                val copies = blockCopies.getOrPut(pred) { mutableListOf() }
                copies.add(varName to value.definingExpr)
            }
            // The variable still needs to be declared (but initialized from first path)
            val firstValue = phi.sources.values.firstOrNull()?.definingExpr ?: KVar(phi.target.paramName())
            mutableVariables[varName] = firstValue
        }
    }

    private fun isDominatedBy(block: AssemblyBlock, dominator: AssemblyBlock): Boolean {
        var current: AssemblyBlock? = block
        while (current != null) {
            if (current == dominator) return true
            current = current.immediateDominator
        }
        return false
    }

    /**
     * Generate variable declarations for phi targets.
     */
    fun generateDeclarations(): List<KotlinStmt> {
        return mutableVariables.map { (name, initialExpr) ->
            KVarDecl(name, "Int", initialExpr, mutable = true)
        }
    }

    /**
     * Get copies that should be inserted at the end of a block.
     */
    fun getCopiesForBlock(block: AssemblyBlock): List<KotlinStmt> {
        val copies = blockCopies[block] ?: return emptyList()
        return copies.map { (varName, expr) ->
            KAssignment(KVar(varName), expr)
        }
    }
}

/**
 * SSA-based code generation context.
 *
 * Unlike the old CodeGenContext, this uses immutable SSA values computed upfront.
 * The phi eliminator handles variable declarations and copies.
 */
class SSACodeGenContext(
    val functionRegistry: Map<String, AssemblyFunction>,
    val jumpEngineTables: Map<Int, JumpEngineTable>,
    val currentFunction: AssemblyFunction,
    val ssaResult: SSAResult,
    val phiEliminator: PhiEliminator
) {
    /** Track which blocks have been converted to avoid duplicates */
    val convertedBlocks = mutableSetOf<AssemblyBlock>()

    /** Memory model: tracks which addresses are variables vs raw memory */
    val memory = mutableMapOf<Int, String>()

    /** Generated variable counter */
    private var tempVarCounter = 0
    fun nextTempVar(): String = "temp${tempVarCounter++}"

    /** Track direct memory accesses: constant -> property name */
    val directMemoryAccesses = mutableMapOf<String, String>()

    /** Track indexed memory accesses: constant -> property name */
    val indexedMemoryAccesses = mutableMapOf<String, String>()

    private fun toCamelCase(name: String): String {
        if ('_' in name) {
            val parts = name.lowercase().split('_')
            return parts.mapIndexed { index, part ->
                if (index == 0) part else part.replaceFirstChar { it.uppercase() }
            }.joinToString("")
        }
        return name.replaceFirstChar { it.lowercase() }
    }

    fun registerDirectAccess(constant: String): String {
        return directMemoryAccesses.getOrPut(constant) { toCamelCase(constant) }
    }

    fun registerIndexedAccess(constant: String): String {
        directMemoryAccesses.remove(constant)
        return indexedMemoryAccesses.getOrPut(constant) { toCamelCase(constant) }
    }

    // --- SSA Value Access ---

    /**
     * Get the SSA value for a base at a specific block's exit.
     */
    fun getValueAtBlockExit(block: AssemblyBlock, base: SSABase): SSAValue? {
        return ssaResult.blockExitState[block]?.get(base)
    }

    /**
     * Get the SSA value for a base at a specific block's entry.
     */
    fun getValueAtBlockEntry(block: AssemblyBlock, base: SSABase): SSAValue? {
        return ssaResult.blockEntryState[block]?.get(base)
    }

    /**
     * Get the flag expression for a branch condition based on the block's exit state.
     */
    fun getFlagExprForBranch(block: AssemblyBlock, op: AssemblyOp): KotlinExpr? {
        val exitState = ssaResult.blockExitState[block] ?: return null

        return when (op) {
            AssemblyOp.BEQ, AssemblyOp.BNE -> exitState[SSABase.FlagZero]?.definingExpr
            AssemblyOp.BCS, AssemblyOp.BCC -> exitState[SSABase.FlagCarry]?.definingExpr
            AssemblyOp.BMI, AssemblyOp.BPL -> exitState[SSABase.FlagNegative]?.definingExpr
            AssemblyOp.BVS, AssemblyOp.BVC -> exitState[SSABase.FlagOverflow]?.definingExpr
            else -> null
        }
    }
}

/**
 * Extension to convert a function using SSA-based code generation.
 */
fun AssemblyFunction.toKotlinFunctionSSA(
    functionRegistry: Map<String, AssemblyFunction> = emptyMap(),
    jumpEngineTables: Map<Int, JumpEngineTable> = emptyMap()
): KFunction {
    // Build SSA form
    val ssaResult = this.buildSSA(functionRegistry)

    // Create phi eliminator and analyze
    val phiEliminator = PhiEliminator(ssaResult, this)
    phiEliminator.analyze()

    // Create SSA context
    val ctx = SSACodeGenContext(
        functionRegistry = functionRegistry,
        jumpEngineTables = jumpEngineTables,
        currentFunction = this,
        ssaResult = ssaResult,
        phiEliminator = phiEliminator
    )

    // Pre-scan memory accesses (same as original)
    preScanMemoryAccessesSSA(ctx)

    // Get control flow structure
    val controlNodes = this.asControls ?: this.analyzeControls()

    // Convert control nodes to Kotlin statements
    val body = mutableListOf<KotlinStmt>()
    for (node in controlNodes) {
        val stmts = node.toKotlinSSA(ctx)
        body.addAll(stmts)
        // If this node contained a return at the top level, stop processing
        if (stmts.any { it is KReturn }) break
    }

    // Detect which registers are used/written in the function
    val usesA = body.any { it.usesRegister("A") }
    val usesX = body.any { it.usesRegister("X") }
    val usesY = body.any { it.usesRegister("Y") }

    // Use the analyzed inputs from AssemblyFunction
    val aIsParam = this.inputs?.contains(TrackedAsIo.A) == true
    val xIsParam = this.inputs?.contains(TrackedAsIo.X) == true
    val yIsParam = this.inputs?.contains(TrackedAsIo.Y) == true

    // Build parameter list
    val params = mutableListOf<KParam>()
    if (aIsParam) params.add(KParam("A", "Int"))
    if (xIsParam) params.add(KParam("X", "Int"))
    if (yIsParam) params.add(KParam("Y", "Int"))

    // Build final body with declarations
    val finalBody = mutableListOf<KotlinStmt>()

    // Register declarations for non-parameter registers
    if (usesA && !aIsParam) {
        finalBody.add(KVarDecl("A", "Int", KLiteral("0"), mutable = true))
    }
    if (usesX && !xIsParam) {
        finalBody.add(KVarDecl("X", "Int", KLiteral("0"), mutable = true))
    }
    if (usesY && !yIsParam) {
        finalBody.add(KVarDecl("Y", "Int", KLiteral("0"), mutable = true))
    }

    // Phi variable declarations
    finalBody.addAll(phiEliminator.generateDeclarations())

    // Memory delegate declarations
    for ((constant, propName) in ctx.directMemoryAccesses.toSortedMap()) {
        if (constant in ctx.indexedMemoryAccesses) continue
        finalBody.add(KVarDecl(
            name = propName,
            type = null,
            value = KCall("MemoryByte", listOf(KVar(constant))),
            mutable = true,
            delegated = true
        ))
    }
    for ((constant, propName) in ctx.indexedMemoryAccesses.toSortedMap()) {
        finalBody.add(KVarDecl(
            name = propName,
            type = null,
            value = KCall("MemoryByteIndexed", listOf(KVar(constant))),
            mutable = false,
            delegated = true
        ))
    }

    finalBody.addAll(body)

    // Generate function name
    val functionName = this.startingBlock.label?.let { assemblyLabelToKotlinName(it) }
        ?: "func_${this.startingBlock.originalLineIndex}"

    // Detect return type from outputs
    val hasAOutput = this.outputs?.contains(TrackedAsIo.A) == true
    val hasYOutput = this.outputs?.contains(TrackedAsIo.Y) == true

    val returnType = when {
        hasAOutput && hasYOutput -> "Pair<Int, Int>"
        hasAOutput -> "Int"
        hasYOutput -> "Int"
        else -> null
    }

    return KFunction(
        name = functionName,
        params = params,
        returnType = returnType,
        body = finalBody,
        comment = "SSA-decompiled from ${this.startingBlock.label ?: "@${this.startingBlock.originalLineIndex}"}"
    )
}

/**
 * Pre-scan memory accesses for SSA context.
 */
private fun AssemblyFunction.preScanMemoryAccessesSSA(ctx: SSACodeGenContext) {
    val blocksToVisit = mutableListOf(startingBlock)
    val visitedBlocks = mutableSetOf<AssemblyBlock>()

    while (blocksToVisit.isNotEmpty()) {
        val block = blocksToVisit.removeAt(0)
        if (!visitedBlocks.add(block)) continue
        if (block.function != this) continue

        for (line in block.lines) {
            val address = line.instruction?.address
            when (address) {
                is AssemblyAddressing.Direct -> {
                    if (!address.label.startsWith("$")) {
                        if (address.offset != 0) {
                            ctx.registerIndexedAccess(address.label)
                        } else if (address.label in HARDWARE_REGISTER_PREFIXES) {
                            ctx.registerIndexedAccess(address.label)
                        }
                    }
                }
                is AssemblyAddressing.DirectX -> {
                    if (!address.label.startsWith("$")) {
                        ctx.registerIndexedAccess(address.label)
                    }
                }
                is AssemblyAddressing.DirectY -> {
                    if (!address.label.startsWith("$")) {
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
 * Convert a ControlNode to Kotlin statements using SSA context.
 */
fun ControlNode.toKotlinSSA(ctx: SSACodeGenContext): List<KotlinStmt> {
    return when (this) {
        is BlockNode -> this.block.toKotlinSSA(ctx)

        is IfNode -> {
            val result = mutableListOf<KotlinStmt>()

            // Include statements from the branch block
            val branchBlock = this.condition.branchBlock
            result.addAll(branchBlock.toKotlinSSA(ctx))

            // Get condition from SSA state
            val condition = this.condition.toKotlinExprSSA(ctx)

            // Process branches
            val thenStmts = this.thenBranch.flatMap { it.toKotlinSSA(ctx) }
            val elseStmts = this.elseBranch.flatMap { it.toKotlinSSA(ctx) }

            result.add(KIf(condition, thenStmts, elseStmts))
            result
        }

        is LoopNode -> {
            val result = mutableListOf<KotlinStmt>()

            // Phi copies for loop variables should be inserted before the loop body
            // (handled by phi eliminator during declaration)

            val bodyStmts = this.body.flatMap { it.toKotlinSSA(ctx) }

            val loop = when (this.kind) {
                LoopKind.PreTest -> {
                    val condition = this.condition?.toKotlinExprSSA(ctx) ?: KLiteral("true")
                    KWhile(condition, bodyStmts)
                }
                LoopKind.PostTest -> {
                    val condition = this.condition?.toKotlinExprSSA(ctx) ?: KLiteral("true")
                    KDoWhile(bodyStmts, condition)
                }
                LoopKind.Infinite -> KLoop(bodyStmts)
            }

            result.add(loop)
            result
        }

        is GotoNode -> listOf(KComment("goto ${this.to.label ?: "@${this.to.originalLineIndex}"}"))
        is BreakNode -> listOf(KBreak)
        is ContinueNode -> listOf(KContinue)

        is SwitchNode -> {
            val selectorExpr = this.selector.toKotlinExpr()
            val branches = this.cases.map { case ->
                val values = case.matchValues.map { KLiteral("0x${it.toString(16).uppercase()}") }
                val body = case.nodes.flatMap { it.toKotlinSSA(ctx) }
                KWhenBranch(values, body)
            }
            val defaultBody = this.defaultBranch.flatMap { it.toKotlinSSA(ctx) }
            listOf(KWhen(selectorExpr, branches, defaultBody))
        }
    }
}

/**
 * Convert an AssemblyBlock to Kotlin statements using SSA context.
 */
fun AssemblyBlock.toKotlinSSA(ctx: SSACodeGenContext): List<KotlinStmt> {
    if (this in ctx.convertedBlocks) return emptyList()
    ctx.convertedBlocks.add(this)

    val stmts = mutableListOf<KotlinStmt>()

    // Handle skip-byte pattern
    val shouldSkipForThisFunction = ctx.currentFunction in this.skipFirstInstructionForFunctions
    var skipNextInstruction = shouldSkipForThisFunction

    // Convert each line
    for (line in this.lines) {
        if (line.originalLine != null) {
            val trimmedLine = line.originalLine.trim()
            if (trimmedLine.isNotEmpty()) {
                stmts.add(KComment(trimmedLine, commentTypeIndicator = ">"))
            }
        }

        val instr = line.instruction
        if (instr != null && !instr.op.isBranch && instr.op != AssemblyOp.JMP) {
            if (skipNextInstruction) {
                stmts.add(KComment("(skipped by BIT \$2C)", commentTypeIndicator = " "))
                skipNextInstruction = false
            } else {
                // Use SSA-based instruction generation
                stmts.addAll(instr.toKotlinSSA(ctx, line.originalLineIndex, this))
            }
        }
    }

    // Add phi copies at block end
    stmts.addAll(ctx.phiEliminator.getCopiesForBlock(this))

    return stmts
}

/**
 * Convert a Condition to Kotlin expression using SSA context.
 */
fun Condition.toKotlinExprSSA(ctx: SSACodeGenContext): KotlinExpr {
    val branchInstr = this.branchLine.instruction ?: return KLiteral("/* unknown condition */")

    // Get flag from SSA exit state of the branch block
    val flagExpr = ctx.getFlagExprForBranch(this.branchBlock, branchInstr.op) ?: run {
        // Fallback if SSA state is missing
        when (branchInstr.op) {
            AssemblyOp.BEQ, AssemblyOp.BNE -> KBinaryOp(KVar("A"), "==", KLiteral("0"))
            AssemblyOp.BCS, AssemblyOp.BCC -> KVar("flagC")
            AssemblyOp.BMI, AssemblyOp.BPL -> KBinaryOp(KParen(KBinaryOp(KVar("A"), "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            AssemblyOp.BVS, AssemblyOp.BVC -> KVar("flagV")
            else -> KLiteral("true")
        }
    }

    // Determine polarity
    val positiveTest = when (branchInstr.op) {
        AssemblyOp.BEQ, AssemblyOp.BCS, AssemblyOp.BMI, AssemblyOp.BVS -> true
        AssemblyOp.BNE, AssemblyOp.BCC, AssemblyOp.BPL, AssemblyOp.BVC -> false
        else -> true
    }

    val finalExpr = if (this.sense == positiveTest) {
        flagExpr
    } else {
        KUnaryOp("!", flagExpr)
    }

    return finalExpr.simplify()
}

/**
 * Convert an addressing mode to Kotlin expression for SSA context.
 * This version doesn't rely on mutable register state from CodeGenContext.
 */
fun AssemblyAddressing?.toKotlinExprSSA(ctx: SSACodeGenContext, block: AssemblyBlock): KotlinExpr {
    if (this == null) return KLiteral("0")

    // Get current register values from SSA exit state
    val exitState = ctx.ssaResult.blockExitState[block] ?: SSAState.EMPTY
    val xExpr = exitState[SSABase.RegisterX]?.definingExpr ?: KVar("X")
    val yExpr = exitState[SSABase.RegisterY]?.definingExpr ?: KVar("Y")

    return when (this) {
        is AssemblyAddressing.ByteValue -> {
            KLiteral("0x${this.value.toString(16).uppercase().padStart(2, '0')}")
        }

        is AssemblyAddressing.ShortValue -> {
            KLiteral("0x${this.value.toString(16).uppercase().padStart(4, '0')}")
        }

        is AssemblyAddressing.ConstantReference -> {
            KVar(this.name)
        }

        is AssemblyAddressing.Direct -> {
            if (this.label.startsWith("$")) {
                val addr = this.label.substring(1).toIntOrNull(16) ?: 0
                val finalAddr = if (this.offset != 0) {
                    KBinaryOp(KLiteral("0x${addr.toString(16).uppercase()}"), "+", KLiteral(this.offset.toString()))
                } else {
                    KLiteral("0x${addr.toString(16).uppercase()}")
                }
                KMemberAccess(KVar("memory"), finalAddr, isIndexed = true)
            } else {
                if (this.offset != 0) {
                    val propName = ctx.registerIndexedAccess(this.label)
                    KMemberAccess(KVar(propName), KLiteral(this.offset.toString()), isIndexed = true)
                } else if (this.label in ctx.indexedMemoryAccesses) {
                    val propName = ctx.indexedMemoryAccesses[this.label]!!
                    KMemberAccess(KVar(propName), KLiteral("0"), isIndexed = true)
                } else if (this.label in HARDWARE_REGISTER_PREFIXES) {
                    val propName = ctx.registerIndexedAccess(this.label)
                    KMemberAccess(KVar(propName), KLiteral("0"), isIndexed = true)
                } else {
                    val propName = ctx.registerDirectAccess(this.label)
                    KVar(propName)
                }
            }
        }

        is AssemblyAddressing.DirectX -> {
            if (this.label.startsWith("$")) {
                val addr = this.label.substring(1).toIntOrNull(16) ?: 0
                val baseAddr = if (this.offset != 0) {
                    KBinaryOp(KLiteral("0x${addr.toString(16).uppercase()}"), "+", KLiteral(this.offset.toString()))
                } else {
                    KLiteral("0x${addr.toString(16).uppercase()}")
                }
                KMemberAccess(KVar("memory"), KBinaryOp(baseAddr, "+", xExpr), isIndexed = true)
            } else {
                val propName = ctx.registerIndexedAccess(this.label)
                if (this.offset != 0) {
                    val indexExpr = KBinaryOp(KLiteral(this.offset.toString()), "+", xExpr)
                    KMemberAccess(KVar(propName), indexExpr, isIndexed = true)
                } else {
                    KMemberAccess(KVar(propName), xExpr, isIndexed = true)
                }
            }
        }

        is AssemblyAddressing.DirectY -> {
            if (this.label.startsWith("$")) {
                val addr = this.label.substring(1).toIntOrNull(16) ?: 0
                val baseAddr = if (this.offset != 0) {
                    KBinaryOp(KLiteral("0x${addr.toString(16).uppercase()}"), "+", KLiteral(this.offset.toString()))
                } else {
                    KLiteral("0x${addr.toString(16).uppercase()}")
                }
                KMemberAccess(KVar("memory"), KBinaryOp(baseAddr, "+", yExpr), isIndexed = true)
            } else {
                val propName = ctx.registerIndexedAccess(this.label)
                if (this.offset != 0) {
                    val indexExpr = KBinaryOp(KLiteral(this.offset.toString()), "+", yExpr)
                    KMemberAccess(KVar(propName), indexExpr, isIndexed = true)
                } else {
                    KMemberAccess(KVar(propName), yExpr, isIndexed = true)
                }
            }
        }

        is AssemblyAddressing.IndirectX -> {
            val baseExpr: KotlinExpr = if (this.label.startsWith("$")) {
                val addr = this.label.substring(1).toIntOrNull(16) ?: 0
                if (this.offset != 0) {
                    KBinaryOp(KLiteral("0x${addr.toString(16).uppercase()}"), "+", KLiteral(this.offset.toString()))
                } else {
                    KLiteral("0x${addr.toString(16).uppercase()}")
                }
            } else {
                if (this.offset != 0) {
                    KBinaryOp(KVar(this.label), "+", KLiteral(this.offset.toString()))
                } else {
                    KVar(this.label)
                }
            }
            val zpAddr = KBinaryOp(KParen(KBinaryOp(baseExpr, "+", xExpr)), "and", KLiteral("0xFF"))
            KMemberAccess(KVar("memory"), KCall("readWord", listOf(zpAddr)), isIndexed = true)
        }

        is AssemblyAddressing.IndirectY -> {
            val baseExpr: KotlinExpr = if (this.label.startsWith("$")) {
                val addr = this.label.substring(1).toIntOrNull(16) ?: 0
                if (this.offset != 0) {
                    KBinaryOp(KLiteral("0x${addr.toString(16).uppercase()}"), "+", KLiteral(this.offset.toString()))
                } else {
                    KLiteral("0x${addr.toString(16).uppercase()}")
                }
            } else {
                if (this.offset != 0) {
                    KBinaryOp(KVar(this.label), "+", KLiteral(this.offset.toString()))
                } else {
                    KVar(this.label)
                }
            }
            val ptrAddr = KCall("readWord", listOf(baseExpr))
            val finalAddr = KBinaryOp(ptrAddr, "+", yExpr)
            KMemberAccess(KVar("memory"), finalAddr, isIndexed = true)
        }

        is AssemblyAddressing.IndirectAbsolute -> {
            val baseExpr: KotlinExpr = if (this.label.startsWith("$")) {
                val addr = this.label.substring(1).toIntOrNull(16) ?: 0
                if (this.offset != 0) {
                    KBinaryOp(KLiteral("0x${addr.toString(16).uppercase()}"), "+", KLiteral(this.offset.toString()))
                } else {
                    KLiteral("0x${addr.toString(16).uppercase()}")
                }
            } else {
                if (this.offset != 0) {
                    KBinaryOp(KVar(this.label), "+", KLiteral(this.offset.toString()))
                } else {
                    KVar(this.label)
                }
            }
            KCall("readWord", listOf(baseExpr))
        }

        else -> {
            KLiteral("/* TODO: ${this} */")
        }
    }
}

/**
 * Convert an instruction to Kotlin using SSA context.
 *
 * For most instructions, we use the SSA-computed values.
 * Side effects (memory stores, function calls) still generate statements.
 */
fun AssemblyInstruction.toKotlinSSA(
    ctx: SSACodeGenContext,
    lineIndex: Int,
    block: AssemblyBlock
): List<KotlinStmt> {
    // Get SSA values for this block
    val exitState = ctx.ssaResult.blockExitState[block]

    return when (this.op) {
        // Store operations - these are side effects that need to be generated
        AssemblyOp.STA -> {
            val aValue = exitState?.get(SSABase.RegisterA)?.definingExpr ?: KVar("A")
            val target = this.address.toKotlinExprSSA(ctx, block)
            listOf(KAssignment(target, aValue))
        }

        AssemblyOp.STX -> {
            val xValue = exitState?.get(SSABase.RegisterX)?.definingExpr ?: KVar("X")
            val target = this.address.toKotlinExprSSA(ctx, block)
            listOf(KAssignment(target, xValue))
        }

        AssemblyOp.STY -> {
            val yValue = exitState?.get(SSABase.RegisterY)?.definingExpr ?: KVar("Y")
            val target = this.address.toKotlinExprSSA(ctx, block)
            listOf(KAssignment(target, yValue))
        }

        // JSR - function calls
        AssemblyOp.JSR -> {
            val label = (this.address as? AssemblyAddressing.Direct)?.label ?: return emptyList()
            val funcName = assemblyLabelToKotlinName(label)
            // TODO: Build arguments based on target function's inputs
            listOf(KExprStmt(KCall(funcName, emptyList())))
        }

        // RTS - return
        AssemblyOp.RTS -> {
            val aValue = exitState?.get(SSABase.RegisterA)?.definingExpr ?: KVar("A")
            listOf(KReturn(aValue))
        }

        // INC/DEC memory - side effects
        AssemblyOp.INC -> {
            val target = this.address.toKotlinExprSSA(ctx, block)
            listOf(KAssignment(target, KBinaryOp(KParen(KBinaryOp(target, "+", KLiteral("1"))), "and", KLiteral("0xFF"))))
        }

        AssemblyOp.DEC -> {
            val target = this.address.toKotlinExprSSA(ctx, block)
            listOf(KAssignment(target, KBinaryOp(KParen(KBinaryOp(target, "-", KLiteral("1"))), "and", KLiteral("0xFF"))))
        }

        // Register operations that modify A/X/Y - emit assignments
        AssemblyOp.LDA, AssemblyOp.LDX, AssemblyOp.LDY,
        AssemblyOp.TAX, AssemblyOp.TAY, AssemblyOp.TXA, AssemblyOp.TYA,
        AssemblyOp.INX, AssemblyOp.INY, AssemblyOp.DEX, AssemblyOp.DEY,
        AssemblyOp.ADC, AssemblyOp.SBC, AssemblyOp.AND, AssemblyOp.ORA, AssemblyOp.EOR,
        AssemblyOp.ASL, AssemblyOp.LSR, AssemblyOp.ROL, AssemblyOp.ROR,
        AssemblyOp.PLA -> {
            // For register-modifying ops, we need to emit the assignment
            // The SSA value has the computed expression
            val stmts = mutableListOf<KotlinStmt>()

            when (this.op) {
                AssemblyOp.LDA, AssemblyOp.TXA, AssemblyOp.TYA, AssemblyOp.ADC, AssemblyOp.SBC,
                AssemblyOp.AND, AssemblyOp.ORA, AssemblyOp.EOR, AssemblyOp.PLA -> {
                    val aValue = exitState?.get(SSABase.RegisterA)?.definingExpr
                    if (aValue != null && aValue !is KVar) {
                        stmts.add(KAssignment(KVar("A"), aValue))
                    }
                }
                AssemblyOp.LDX, AssemblyOp.TAX, AssemblyOp.INX, AssemblyOp.DEX -> {
                    val xValue = exitState?.get(SSABase.RegisterX)?.definingExpr
                    if (xValue != null && xValue !is KVar) {
                        stmts.add(KAssignment(KVar("X"), xValue))
                    }
                }
                AssemblyOp.LDY, AssemblyOp.TAY, AssemblyOp.INY, AssemblyOp.DEY -> {
                    val yValue = exitState?.get(SSABase.RegisterY)?.definingExpr
                    if (yValue != null && yValue !is KVar) {
                        stmts.add(KAssignment(KVar("Y"), yValue))
                    }
                }
                AssemblyOp.ASL, AssemblyOp.LSR, AssemblyOp.ROL, AssemblyOp.ROR -> {
                    if (this.address == null) {
                        // Accumulator mode
                        val aValue = exitState?.get(SSABase.RegisterA)?.definingExpr
                        if (aValue != null) {
                            stmts.add(KAssignment(KVar("A"), aValue))
                        }
                    }
                }
                else -> {}
            }
            stmts
        }

        // Compare, BIT, flag operations - no statements needed (flags are tracked in SSA)
        AssemblyOp.CMP, AssemblyOp.CPX, AssemblyOp.CPY, AssemblyOp.BIT,
        AssemblyOp.SEC, AssemblyOp.CLC, AssemblyOp.CLV,
        AssemblyOp.PHA, AssemblyOp.PHP, AssemblyOp.PLP -> emptyList()

        // NOP and other no-ops
        AssemblyOp.NOP -> emptyList()

        else -> emptyList()
    }
}
