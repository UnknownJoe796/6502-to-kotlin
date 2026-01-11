// by Claude - SSA construction using dominance frontiers
package com.ivieleague.decompiler6502tokotlin.hand.ssa

import com.ivieleague.decompiler6502tokotlin.hand.*

/**
 * Builds SSA form for a function using the classic algorithm:
 * 1. Compute dominance frontiers from the existing dominator tree
 * 2. Insert phi nodes at dominance frontiers
 * 3. Rename variables in reverse postorder traversal
 *
 * References:
 * - Cytron et al., "Efficiently Computing Static Single Assignment Form and the Control Dependence Graph"
 * - Cooper, Harvey, Kennedy, "A Simple, Fast Dominance Algorithm"
 */
class SSABuilder(
    private val function: AssemblyFunction,
    private val functionRegistry: Map<String, AssemblyFunction>
) {
    // The blocks in this function
    private val blocks: Set<AssemblyBlock> = function.blocks ?: emptySet()
    private val startBlock: AssemblyBlock = function.startingBlock

    // Version counters for each base
    private val versionCounters = mutableMapOf<SSABase, Int>()

    // Phi nodes to insert at each block
    private val phiNodes = mutableMapOf<AssemblyBlock, MutableList<PhiNode>>()

    // SSA state at block entry and exit
    private val blockEntryState = mutableMapOf<AssemblyBlock, SSAState>()
    private val blockExitState = mutableMapOf<AssemblyBlock, SSAState>()

    // All defined SSA values
    private val allValues = mutableListOf<SSAValue>()

    /**
     * Build SSA form for the function.
     *
     * @return SSAResult containing phi nodes and block states
     */
    fun build(): SSAResult {
        if (blocks.isEmpty()) {
            return SSAResult(
                phiNodes = emptyMap(),
                blockEntryState = emptyMap(),
                blockExitState = emptyMap(),
                allValues = emptyList()
            )
        }

        // Step 1: Compute dominance frontiers
        val dominanceFrontiers = computeDominanceFrontiers()

        // Step 2: Identify which bases are defined in which blocks
        val defSites = identifyDefinitionSites()

        // Step 3: Insert phi nodes at dominance frontiers
        insertPhiNodes(dominanceFrontiers, defSites)

        // Step 4: Rename variables in dominator tree order
        renameVariables()

        return SSAResult(
            phiNodes = phiNodes.mapValues { it.value.toList() },
            blockEntryState = blockEntryState.toMap(),
            blockExitState = blockExitState.toMap(),
            allValues = allValues.toList()
        )
    }

    /**
     * Compute dominance frontiers for all blocks.
     *
     * The dominance frontier of a block B is the set of blocks where B's dominance ends.
     * Formally: DF(B) = { Y | B dominates a predecessor of Y, but B does not strictly dominate Y }
     *
     * We use the algorithm from "A Simple, Fast Dominance Algorithm" by Cooper et al.
     */
    private fun computeDominanceFrontiers(): Map<AssemblyBlock, Set<AssemblyBlock>> {
        val df = mutableMapOf<AssemblyBlock, MutableSet<AssemblyBlock>>()
        for (block in blocks) {
            df[block] = mutableSetOf()
        }

        for (block in blocks) {
            val predecessors = block.enteredFrom.filter { it in blocks }
            if (predecessors.size >= 2) {
                // This is a join point - check each predecessor's dominance
                for (pred in predecessors) {
                    var runner = pred
                    while (runner != block.immediateDominator && runner in blocks) {
                        df.getOrPut(runner) { mutableSetOf() }.add(block)
                        runner = runner.immediateDominator ?: break
                    }
                }
            }
        }

        return df
    }

    /**
     * Identify which SSA bases are defined (assigned) in which blocks.
     *
     * This is a simple approximation - we mark a block as defining a base if:
     * - It contains an instruction that modifies that base's register/flag
     */
    private fun identifyDefinitionSites(): Map<SSABase, Set<AssemblyBlock>> {
        val defSites = SSABase.ALL.associateWith { mutableSetOf<AssemblyBlock>() }

        for (block in blocks) {
            for (line in block.lines) {
                val instr = line.instruction ?: continue
                val op = instr.op

                // Determine which bases this instruction defines
                val addrClass = instr.address?.let { it::class }
                val modifies = op.modifies(addrClass)
                for (affectable in modifies) {
                    val base = affectableToSSABase(affectable)
                    if (base != null) {
                        defSites[base]!!.add(block)
                    }
                }
            }
        }

        // Entry block defines parameters (registers that are inputs)
        val inputs = function.inputs ?: emptySet()
        for (input in inputs) {
            val base = trackedAsIoToSSABase(input)
            if (base != null) {
                defSites[base]!!.add(startBlock)
            }
        }

        return defSites.mapValues { it.value.toSet() }
    }

    /**
     * Insert phi nodes at dominance frontiers.
     *
     * For each base that is defined in multiple blocks, we insert a phi node
     * at each block in the dominance frontier of the definition sites.
     */
    private fun insertPhiNodes(
        dominanceFrontiers: Map<AssemblyBlock, Set<AssemblyBlock>>,
        defSites: Map<SSABase, Set<AssemblyBlock>>
    ) {
        for (base in SSABase.ALL) {
            val defs = defSites[base] ?: continue
            if (defs.isEmpty()) continue

            // Worklist algorithm
            val worklist = defs.toMutableSet()
            val hasPhiFor = mutableSetOf<AssemblyBlock>()

            while (worklist.isNotEmpty()) {
                val block = worklist.first()
                worklist.remove(block)

                val df = dominanceFrontiers[block] ?: continue
                for (frontierBlock in df) {
                    if (frontierBlock !in hasPhiFor && frontierBlock in blocks) {
                        // Insert phi node
                        val phiList = phiNodes.getOrPut(frontierBlock) { mutableListOf() }

                        // Allocate a new version for the phi result
                        val resultVersion = versionCounters.merge(base, 1, Int::plus)!!

                        // Sources will be filled in during renaming
                        phiList.add(PhiNode(
                            target = base,
                            resultVersion = resultVersion,
                            sources = emptyMap() // Filled in later
                        ))

                        hasPhiFor.add(frontierBlock)

                        // Phi node also counts as a definition
                        if (frontierBlock !in defs) {
                            worklist.add(frontierBlock)
                        }
                    }
                }
            }
        }
    }

    /**
     * Rename variables by traversing blocks in dominator tree order.
     *
     * This fills in:
     * - The actual SSA values for each instruction
     * - The sources for each phi node
     * - The entry and exit states for each block
     */
    private fun renameVariables() {
        // Initialize with parameters
        val initialState = createInitialState()

        // Traverse in dominator tree order (preorder DFS)
        val visited = mutableSetOf<AssemblyBlock>()
        val stack = mutableListOf<Pair<AssemblyBlock, SSAState>>()
        stack.add(startBlock to initialState)

        while (stack.isNotEmpty()) {
            val (block, incomingState) = stack.removeLast()
            if (block in visited) continue
            visited.add(block)

            // Process this block
            val exitState = processBlock(block, incomingState)
            blockExitState[block] = exitState

            // Add dominated children to stack
            val children = block.dominates.filter { it in blocks }
            for (child in children.reversed()) { // Reversed so first child is processed first
                stack.add(child to exitState)
            }
        }

        // Second pass: fill in phi node sources
        fillPhiSources()
    }

    /**
     * Create initial SSA state for function entry.
     */
    private fun createInitialState(): SSAState {
        val inputs = function.inputs ?: emptySet()
        val values = mutableMapOf<SSABase, SSAValue>()

        for (input in inputs) {
            val base = trackedAsIoToSSABase(input) ?: continue
            val value = SSAValue(
                base = base,
                version = 0,
                definingExpr = KVar(base.paramName())
            )
            values[base] = value
            allValues.add(value)
        }

        return SSAState(values)
    }

    /**
     * Process a single block, generating SSA values and updating state.
     */
    private fun processBlock(block: AssemblyBlock, incomingState: SSAState): SSAState {
        var state = incomingState

        // First, process phi nodes at this block
        val phis = phiNodes[block]
        if (phis != null) {
            for (phi in phis) {
                // Create SSA value for phi result
                val value = SSAValue(
                    base = phi.target,
                    version = phi.resultVersion,
                    definingExpr = KVar(phi.name), // Will be resolved during code generation
                    definingBlock = block
                )
                state = state.with(phi.target, value)
                allValues.add(value)
            }
        }

        // Save entry state (after phis)
        blockEntryState[block] = state

        // Process each instruction
        val ctx = SSABlockContext(state, versionCounters, block)

        for (line in block.lines) {
            val instr = line.instruction ?: continue
            processInstruction(instr, ctx)
        }

        // Record all defined values
        allValues.addAll(ctx.definedValues)

        return ctx.currentState
    }

    /**
     * Process a single instruction, updating SSA context.
     */
    private fun processInstruction(instr: AssemblyInstruction, ctx: SSABlockContext) {
        val op = instr.op

        // Get current values for source registers
        val a = ctx.getValue(SSABase.RegisterA)
        val x = ctx.getValue(SSABase.RegisterX)
        val y = ctx.getValue(SSABase.RegisterY)
        val flagC = ctx.getValue(SSABase.FlagCarry)

        // Generate new values based on instruction
        when (op) {
            // Load instructions - set register and flags
            AssemblyOp.LDA -> {
                val value = resolveOperandExpr(instr.address, ctx)
                ctx.defineValue(SSABase.RegisterA, value)
                ctx.defineValue(SSABase.FlagZero, KBinaryOp(value, "==", KLiteral("0")))
                ctx.defineValue(SSABase.FlagNegative,
                    KBinaryOp(KParen(KBinaryOp(value, "and", KLiteral("0x80"))), "!=", KLiteral("0")))
            }

            AssemblyOp.LDX -> {
                val value = resolveOperandExpr(instr.address, ctx)
                ctx.defineValue(SSABase.RegisterX, value)
                ctx.defineValue(SSABase.FlagZero, KBinaryOp(value, "==", KLiteral("0")))
                ctx.defineValue(SSABase.FlagNegative,
                    KBinaryOp(KParen(KBinaryOp(value, "and", KLiteral("0x80"))), "!=", KLiteral("0")))
            }

            AssemblyOp.LDY -> {
                val value = resolveOperandExpr(instr.address, ctx)
                ctx.defineValue(SSABase.RegisterY, value)
                ctx.defineValue(SSABase.FlagZero, KBinaryOp(value, "==", KLiteral("0")))
                ctx.defineValue(SSABase.FlagNegative,
                    KBinaryOp(KParen(KBinaryOp(value, "and", KLiteral("0x80"))), "!=", KLiteral("0")))
            }

            // Transfer instructions
            AssemblyOp.TAX -> {
                val value = a.definingExpr
                ctx.defineValue(SSABase.RegisterX, value)
                ctx.defineValue(SSABase.FlagZero, KBinaryOp(value, "==", KLiteral("0")))
                ctx.defineValue(SSABase.FlagNegative,
                    KBinaryOp(KParen(KBinaryOp(value, "and", KLiteral("0x80"))), "!=", KLiteral("0")))
            }

            AssemblyOp.TAY -> {
                val value = a.definingExpr
                ctx.defineValue(SSABase.RegisterY, value)
                ctx.defineValue(SSABase.FlagZero, KBinaryOp(value, "==", KLiteral("0")))
                ctx.defineValue(SSABase.FlagNegative,
                    KBinaryOp(KParen(KBinaryOp(value, "and", KLiteral("0x80"))), "!=", KLiteral("0")))
            }

            AssemblyOp.TXA -> {
                val value = x.definingExpr
                ctx.defineValue(SSABase.RegisterA, value)
                ctx.defineValue(SSABase.FlagZero, KBinaryOp(value, "==", KLiteral("0")))
                ctx.defineValue(SSABase.FlagNegative,
                    KBinaryOp(KParen(KBinaryOp(value, "and", KLiteral("0x80"))), "!=", KLiteral("0")))
            }

            AssemblyOp.TYA -> {
                val value = y.definingExpr
                ctx.defineValue(SSABase.RegisterA, value)
                ctx.defineValue(SSABase.FlagZero, KBinaryOp(value, "==", KLiteral("0")))
                ctx.defineValue(SSABase.FlagNegative,
                    KBinaryOp(KParen(KBinaryOp(value, "and", KLiteral("0x80"))), "!=", KLiteral("0")))
            }

            // Increment/Decrement
            AssemblyOp.INX -> {
                val result = KBinaryOp(KParen(KBinaryOp(x.definingExpr, "+", KLiteral("1"))), "and", KLiteral("0xFF"))
                ctx.defineValue(SSABase.RegisterX, result)
                ctx.defineValue(SSABase.FlagZero, KBinaryOp(result, "==", KLiteral("0")))
                ctx.defineValue(SSABase.FlagNegative,
                    KBinaryOp(KParen(KBinaryOp(result, "and", KLiteral("0x80"))), "!=", KLiteral("0")))
            }

            AssemblyOp.INY -> {
                val result = KBinaryOp(KParen(KBinaryOp(y.definingExpr, "+", KLiteral("1"))), "and", KLiteral("0xFF"))
                ctx.defineValue(SSABase.RegisterY, result)
                ctx.defineValue(SSABase.FlagZero, KBinaryOp(result, "==", KLiteral("0")))
                ctx.defineValue(SSABase.FlagNegative,
                    KBinaryOp(KParen(KBinaryOp(result, "and", KLiteral("0x80"))), "!=", KLiteral("0")))
            }

            AssemblyOp.DEX -> {
                val result = KBinaryOp(KParen(KBinaryOp(x.definingExpr, "-", KLiteral("1"))), "and", KLiteral("0xFF"))
                ctx.defineValue(SSABase.RegisterX, result)
                ctx.defineValue(SSABase.FlagZero, KBinaryOp(result, "==", KLiteral("0")))
                ctx.defineValue(SSABase.FlagNegative,
                    KBinaryOp(KParen(KBinaryOp(result, "and", KLiteral("0x80"))), "!=", KLiteral("0")))
            }

            AssemblyOp.DEY -> {
                val result = KBinaryOp(KParen(KBinaryOp(y.definingExpr, "-", KLiteral("1"))), "and", KLiteral("0xFF"))
                ctx.defineValue(SSABase.RegisterY, result)
                ctx.defineValue(SSABase.FlagZero, KBinaryOp(result, "==", KLiteral("0")))
                ctx.defineValue(SSABase.FlagNegative,
                    KBinaryOp(KParen(KBinaryOp(result, "and", KLiteral("0x80"))), "!=", KLiteral("0")))
            }

            // Arithmetic - ADC (Add with Carry)
            AssemblyOp.ADC -> {
                val operand = resolveOperandExpr(instr.address, ctx)
                val carryInt = KIfExpr(flagC.definingExpr, KLiteral("1"), KLiteral("0"))
                val fullSum = KBinaryOp(
                    KBinaryOp(a.definingExpr, "+", operand),
                    "+", carryInt
                )
                val result = KBinaryOp(fullSum, "and", KLiteral("0xFF"))
                ctx.defineValue(SSABase.RegisterA, result)
                ctx.defineValue(SSABase.FlagCarry, KBinaryOp(fullSum, ">", KLiteral("0xFF")))
                ctx.defineValue(SSABase.FlagZero, KBinaryOp(result, "==", KLiteral("0")))
                ctx.defineValue(SSABase.FlagNegative,
                    KBinaryOp(KParen(KBinaryOp(result, "and", KLiteral("0x80"))), "!=", KLiteral("0")))
                // Overflow flag for ADC is complex - simplified here
                ctx.defineValue(SSABase.FlagOverflow, KLiteral("false"))
            }

            // Arithmetic - SBC (Subtract with Borrow)
            AssemblyOp.SBC -> {
                val operand = resolveOperandExpr(instr.address, ctx)
                val borrowInt = KIfExpr(flagC.definingExpr, KLiteral("0"), KLiteral("1"))
                val fullDiff = KBinaryOp(
                    KBinaryOp(a.definingExpr, "-", operand),
                    "-", borrowInt
                )
                val result = KBinaryOp(fullDiff, "and", KLiteral("0xFF"))
                ctx.defineValue(SSABase.RegisterA, result)
                ctx.defineValue(SSABase.FlagCarry, KBinaryOp(fullDiff, ">=", KLiteral("0")))
                ctx.defineValue(SSABase.FlagZero, KBinaryOp(result, "==", KLiteral("0")))
                ctx.defineValue(SSABase.FlagNegative,
                    KBinaryOp(KParen(KBinaryOp(result, "and", KLiteral("0x80"))), "!=", KLiteral("0")))
                ctx.defineValue(SSABase.FlagOverflow, KLiteral("false"))
            }

            // Logical operations
            AssemblyOp.AND -> {
                val operand = resolveOperandExpr(instr.address, ctx)
                val result = KBinaryOp(a.definingExpr, "and", operand)
                ctx.defineValue(SSABase.RegisterA, result)
                ctx.defineValue(SSABase.FlagZero, KBinaryOp(result, "==", KLiteral("0")))
                ctx.defineValue(SSABase.FlagNegative,
                    KBinaryOp(KParen(KBinaryOp(result, "and", KLiteral("0x80"))), "!=", KLiteral("0")))
            }

            AssemblyOp.ORA -> {
                val operand = resolveOperandExpr(instr.address, ctx)
                val result = KBinaryOp(a.definingExpr, "or", operand)
                ctx.defineValue(SSABase.RegisterA, result)
                ctx.defineValue(SSABase.FlagZero, KBinaryOp(result, "==", KLiteral("0")))
                ctx.defineValue(SSABase.FlagNegative,
                    KBinaryOp(KParen(KBinaryOp(result, "and", KLiteral("0x80"))), "!=", KLiteral("0")))
            }

            AssemblyOp.EOR -> {
                val operand = resolveOperandExpr(instr.address, ctx)
                val result = KBinaryOp(a.definingExpr, "xor", operand)
                ctx.defineValue(SSABase.RegisterA, result)
                ctx.defineValue(SSABase.FlagZero, KBinaryOp(result, "==", KLiteral("0")))
                ctx.defineValue(SSABase.FlagNegative,
                    KBinaryOp(KParen(KBinaryOp(result, "and", KLiteral("0x80"))), "!=", KLiteral("0")))
            }

            // Shift operations - THIS IS THE KEY FIX
            // Carry is captured BEFORE the shift, using the original value
            AssemblyOp.ASL -> {
                if (instr.address == null) {
                    // Accumulator mode
                    val original = a.definingExpr
                    // CRITICAL: Capture carry from original value BEFORE shifting
                    ctx.defineValue(SSABase.FlagCarry,
                        KBinaryOp(KParen(KBinaryOp(original, "and", KLiteral("0x80"))), "!=", KLiteral("0")))
                    val result = KBinaryOp(KParen(KBinaryOp(original, "shl", KLiteral("1"))), "and", KLiteral("0xFF"))
                    ctx.defineValue(SSABase.RegisterA, result)
                    ctx.defineValue(SSABase.FlagZero, KBinaryOp(result, "==", KLiteral("0")))
                    ctx.defineValue(SSABase.FlagNegative,
                        KBinaryOp(KParen(KBinaryOp(result, "and", KLiteral("0x80"))), "!=", KLiteral("0")))
                }
                // Memory mode would need to be handled separately (reads memory, modifies, writes back)
            }

            AssemblyOp.LSR -> {
                if (instr.address == null) {
                    val original = a.definingExpr
                    // CRITICAL: Capture carry from original value BEFORE shifting
                    ctx.defineValue(SSABase.FlagCarry,
                        KBinaryOp(KParen(KBinaryOp(original, "and", KLiteral("0x01"))), "!=", KLiteral("0")))
                    val result = KBinaryOp(original, "shr", KLiteral("1"))
                    ctx.defineValue(SSABase.RegisterA, result)
                    ctx.defineValue(SSABase.FlagZero, KBinaryOp(result, "==", KLiteral("0")))
                    ctx.defineValue(SSABase.FlagNegative, KLiteral("false")) // LSR always clears N
                }
            }

            AssemblyOp.ROL -> {
                if (instr.address == null) {
                    val original = a.definingExpr
                    val oldCarry = flagC.definingExpr
                    // CRITICAL: Capture carry from original value BEFORE rotating
                    ctx.defineValue(SSABase.FlagCarry,
                        KBinaryOp(KParen(KBinaryOp(original, "and", KLiteral("0x80"))), "!=", KLiteral("0")))
                    val carryIn = KIfExpr(oldCarry, KLiteral("1"), KLiteral("0"))
                    val result = KBinaryOp(
                        KParen(KBinaryOp(KParen(KBinaryOp(original, "shl", KLiteral("1"))), "or", carryIn)),
                        "and", KLiteral("0xFF")
                    )
                    ctx.defineValue(SSABase.RegisterA, result)
                    ctx.defineValue(SSABase.FlagZero, KBinaryOp(result, "==", KLiteral("0")))
                    ctx.defineValue(SSABase.FlagNegative,
                        KBinaryOp(KParen(KBinaryOp(result, "and", KLiteral("0x80"))), "!=", KLiteral("0")))
                }
            }

            AssemblyOp.ROR -> {
                if (instr.address == null) {
                    val original = a.definingExpr
                    val oldCarry = flagC.definingExpr
                    // CRITICAL: Capture carry from original value BEFORE rotating
                    ctx.defineValue(SSABase.FlagCarry,
                        KBinaryOp(KParen(KBinaryOp(original, "and", KLiteral("0x01"))), "!=", KLiteral("0")))
                    val carryIn = KIfExpr(oldCarry, KLiteral("0x80"), KLiteral("0"))
                    val result = KBinaryOp(
                        KParen(KBinaryOp(original, "shr", KLiteral("1"))),
                        "or", carryIn
                    )
                    ctx.defineValue(SSABase.RegisterA, result)
                    ctx.defineValue(SSABase.FlagZero, KBinaryOp(result, "==", KLiteral("0")))
                    ctx.defineValue(SSABase.FlagNegative,
                        KBinaryOp(KParen(KBinaryOp(result, "and", KLiteral("0x80"))), "!=", KLiteral("0")))
                }
            }

            // Compare operations - only set flags, don't modify registers
            AssemblyOp.CMP -> {
                val operand = resolveOperandExpr(instr.address, ctx)
                val diff = KBinaryOp(a.definingExpr, "-", operand)
                ctx.defineValue(SSABase.FlagCarry, KBinaryOp(a.definingExpr, ">=", operand))
                ctx.defineValue(SSABase.FlagZero, KBinaryOp(a.definingExpr, "==", operand))
                ctx.defineValue(SSABase.FlagNegative,
                    KBinaryOp(KParen(KBinaryOp(KParen(diff), "and", KLiteral("0x80"))), "!=", KLiteral("0")))
            }

            AssemblyOp.CPX -> {
                val operand = resolveOperandExpr(instr.address, ctx)
                val diff = KBinaryOp(x.definingExpr, "-", operand)
                ctx.defineValue(SSABase.FlagCarry, KBinaryOp(x.definingExpr, ">=", operand))
                ctx.defineValue(SSABase.FlagZero, KBinaryOp(x.definingExpr, "==", operand))
                ctx.defineValue(SSABase.FlagNegative,
                    KBinaryOp(KParen(KBinaryOp(KParen(diff), "and", KLiteral("0x80"))), "!=", KLiteral("0")))
            }

            AssemblyOp.CPY -> {
                val operand = resolveOperandExpr(instr.address, ctx)
                val diff = KBinaryOp(y.definingExpr, "-", operand)
                ctx.defineValue(SSABase.FlagCarry, KBinaryOp(y.definingExpr, ">=", operand))
                ctx.defineValue(SSABase.FlagZero, KBinaryOp(y.definingExpr, "==", operand))
                ctx.defineValue(SSABase.FlagNegative,
                    KBinaryOp(KParen(KBinaryOp(KParen(diff), "and", KLiteral("0x80"))), "!=", KLiteral("0")))
            }

            // Flag operations
            AssemblyOp.SEC -> ctx.defineValue(SSABase.FlagCarry, KLiteral("true"))
            AssemblyOp.CLC -> ctx.defineValue(SSABase.FlagCarry, KLiteral("false"))
            AssemblyOp.CLV -> ctx.defineValue(SSABase.FlagOverflow, KLiteral("false"))

            // BIT - test bits in memory with accumulator
            AssemblyOp.BIT -> {
                val operand = resolveOperandExpr(instr.address, ctx)
                val andResult = KBinaryOp(a.definingExpr, "and", operand)
                ctx.defineValue(SSABase.FlagZero, KBinaryOp(andResult, "==", KLiteral("0")))
                ctx.defineValue(SSABase.FlagNegative,
                    KBinaryOp(KParen(KBinaryOp(operand, "and", KLiteral("0x80"))), "!=", KLiteral("0")))
                ctx.defineValue(SSABase.FlagOverflow,
                    KBinaryOp(KParen(KBinaryOp(operand, "and", KLiteral("0x40"))), "!=", KLiteral("0")))
            }

            // Store operations don't modify registers or flags
            AssemblyOp.STA, AssemblyOp.STX, AssemblyOp.STY -> {
                // No register/flag changes
            }

            // Increment/Decrement memory
            AssemblyOp.INC -> {
                // Memory is modified - we don't track memory in SSA (yet)
                // But we should set the flags based on the result
                // For now, mark flags as unknown
            }

            AssemblyOp.DEC -> {
                // Memory is modified - similar to INC
            }

            // Stack operations
            AssemblyOp.PHA, AssemblyOp.PHP -> {
                // Push doesn't modify registers/flags we track
            }

            AssemblyOp.PLA -> {
                // Pop value into A, set N and Z flags
                val value = KVar("stack_pop") // Placeholder
                ctx.defineValue(SSABase.RegisterA, value)
                ctx.defineValue(SSABase.FlagZero, KBinaryOp(value, "==", KLiteral("0")))
                ctx.defineValue(SSABase.FlagNegative,
                    KBinaryOp(KParen(KBinaryOp(value, "and", KLiteral("0x80"))), "!=", KLiteral("0")))
            }

            AssemblyOp.PLP -> {
                // Pop flags - all flags become unknown
                ctx.defineValue(SSABase.FlagZero, KVar("popped_Z"))
                ctx.defineValue(SSABase.FlagNegative, KVar("popped_N"))
                ctx.defineValue(SSABase.FlagCarry, KVar("popped_C"))
                ctx.defineValue(SSABase.FlagOverflow, KVar("popped_V"))
            }

            // Branch and jump instructions don't modify registers/flags
            AssemblyOp.BEQ, AssemblyOp.BNE, AssemblyOp.BCS, AssemblyOp.BCC,
            AssemblyOp.BMI, AssemblyOp.BPL, AssemblyOp.BVS, AssemblyOp.BVC,
            AssemblyOp.JMP, AssemblyOp.JSR, AssemblyOp.RTS, AssemblyOp.RTI -> {
                // No register/flag changes
            }

            // Other instructions we don't handle yet
            else -> {
                // No changes to tracked state
            }
        }
    }

    /**
     * Fill in phi node sources based on predecessor block exit states.
     */
    private fun fillPhiSources() {
        for ((block, phis) in phiNodes) {
            val predecessors = block.enteredFrom.filter { it in blocks }
            val updatedPhis = phis.map { phi ->
                val sources = predecessors.associate { pred ->
                    val predExitState = blockExitState[pred] ?: SSAState.EMPTY
                    val sourceValue = predExitState[phi.target] ?: SSAValue(
                        base = phi.target,
                        version = 0,
                        definingExpr = KVar(phi.target.paramName())
                    )
                    pred to sourceValue
                }
                phi.copy(sources = sources)
            }
            phiNodes[block] = updatedPhis.toMutableList()
        }
    }

    /**
     * Resolve an operand addressing mode to a Kotlin expression.
     */
    private fun resolveOperandExpr(address: AssemblyAddressing?, ctx: SSABlockContext): KotlinExpr {
        return when (address) {
            null -> KVar("A") // Accumulator mode
            is AssemblyAddressing.ByteValue -> KLiteral("0x${address.value.toString(16).uppercase()}")
            is AssemblyAddressing.ShortValue -> KLiteral("0x${address.value.toString(16).uppercase()}")
            is AssemblyAddressing.Direct -> KVar(assemblyLabelToKotlinName(address.label))
            is AssemblyAddressing.DirectX -> KMemberAccess(
                KVar(assemblyLabelToKotlinName(address.label)),
                ctx.getValue(SSABase.RegisterX).definingExpr,
                isIndexed = true
            )
            is AssemblyAddressing.DirectY -> KMemberAccess(
                KVar(assemblyLabelToKotlinName(address.label)),
                ctx.getValue(SSABase.RegisterY).definingExpr,
                isIndexed = true
            )
            is AssemblyAddressing.IndirectX -> KVar("indirect_x_${address.label}")
            is AssemblyAddressing.IndirectY -> KVar("indirect_y_${address.label}")
            is AssemblyAddressing.IndirectAbsolute -> KVar("indirect_${address.label}")
            else -> KVar("unknown")
        }
    }

    companion object {
        /**
         * Convert AssemblyAffectable to SSABase.
         */
        fun affectableToSSABase(affectable: AssemblyAffectable): SSABase? = when (affectable) {
            AssemblyAffectable.A -> SSABase.RegisterA
            AssemblyAffectable.X -> SSABase.RegisterX
            AssemblyAffectable.Y -> SSABase.RegisterY
            AssemblyAffectable.Zero -> SSABase.FlagZero
            AssemblyAffectable.Negative -> SSABase.FlagNegative
            AssemblyAffectable.Carry -> SSABase.FlagCarry
            AssemblyAffectable.Overflow -> SSABase.FlagOverflow
            else -> null
        }

        /**
         * Convert TrackedAsIo to SSABase.
         */
        fun trackedAsIoToSSABase(tracked: TrackedAsIo): SSABase? = when (tracked) {
            TrackedAsIo.A -> SSABase.RegisterA
            TrackedAsIo.X -> SSABase.RegisterX
            TrackedAsIo.Y -> SSABase.RegisterY
            TrackedAsIo.ZeroFlag -> SSABase.FlagZero
            TrackedAsIo.NegativeFlag -> SSABase.FlagNegative
            TrackedAsIo.CarryFlag -> SSABase.FlagCarry
            TrackedAsIo.OverflowFlag -> SSABase.FlagOverflow
            else -> null
        }
    }
}

/**
 * Extension to build SSA for a function.
 */
fun AssemblyFunction.buildSSA(functionRegistry: Map<String, AssemblyFunction> = emptyMap()): SSAResult {
    return SSABuilder(this, functionRegistry).build()
}
