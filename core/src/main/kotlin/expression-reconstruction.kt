package com.ivieleague.decompiler6502tokotlin.hand

/**
 * Expression Reconstruction for 6502 Decompiler
 *
 * This file contains logic to reconstruct high-level condition expressions from
 * low-level 6502 flag operations and branch instructions.
 *
 * ## Strategy
 *
 * 1. **Analyze instructions before branch**: Look backward from a branch instruction
 *    to find the most recent instruction that sets the flags being tested.
 *
 * 2. **Match patterns**: Recognize common 6502 patterns:
 *    - CMP #value; BEQ  → A == value
 *    - LDA $addr; BEQ   → memory[$addr] == 0
 *    - AND #mask; BNE   → (A & mask) != 0
 *    - CLC; ADC #value  → A = A + value (no flags test, just operation)
 *
 * 3. **Handle flag-setting operations**:
 *    - CMP, CPX, CPY: Set N, Z, C for comparisons
 *    - LDA, LDX, LDY: Set N, Z
 *    - AND, ORA, EOR: Set N, Z
 *    - ADC, SBC: Set N, V, Z, C
 *    - INC, DEC, INX, DEX, INY, DEY: Set N, Z
 *    - ASL, LSR, ROL, ROR: Set N, Z, C
 *    - BIT: Set N, V, Z
 *
 * 4. **Branch instruction mapping**:
 *    - BEQ/BNE: Test Z flag
 *    - BCC/BCS: Test C flag
 *    - BMI/BPL: Test N flag
 *    - BVC/BVS: Test V flag
 */

/**
 * Reconstruct condition expressions for all IfNodes and LoopNodes in a function.
 */
fun AssemblyFunction.reconstructExpressions() {
    val controls = this.asControls ?: return

    fun processNode(node: ControlNode) {
        when (node) {
            is IfNode -> {
                reconstructCondition(node.condition)
                node.thenBranch.forEach { processNode(it) }
                node.elseBranch.forEach { processNode(it) }
            }
            is LoopNode -> {
                node.condition?.let { reconstructCondition(it) }
                node.body.forEach { processNode(it) }
            }
            else -> {}
        }
    }

    controls.forEach { processNode(it) }
}

/**
 * Reconstruct the condition expression for a single Condition object.
 *
 * Analyzes the block containing the branch to find flag-setting instructions
 * and builds an appropriate ConditionExpr.
 */
fun reconstructCondition(condition: Condition) {
    val branchInst = condition.branchLine.instruction ?: return
    val branchOp = branchInst.op

    // Determine which flag the branch tests
    val testedFlag = when (branchOp) {
        AssemblyOp.BEQ, AssemblyOp.BNE -> AssemblyAffectable.Zero
        AssemblyOp.BCC, AssemblyOp.BCS -> AssemblyAffectable.Carry
        AssemblyOp.BMI, AssemblyOp.BPL -> AssemblyAffectable.Negative
        AssemblyOp.BVC, AssemblyOp.BVS -> AssemblyAffectable.Overflow
        else -> {
            // Not a conditional branch
            condition.expr = UnknownCond
            return
        }
    }

    // Determine if the branch is taken when flag is set (positive) or clear (negative)
    val branchIfSet = branchOp.flagPositive

    // Look backward through the block to find the instruction that set this flag
    val block = condition.branchBlock
    val lines = block.lines.filter { it.instruction != null }
    val branchIndex = lines.indexOfFirst { it === condition.branchLine }

    if (branchIndex < 0) {
        condition.expr = FlagTest(testedFlag, branchIfSet)
        return
    }

    // Search backward for flag-setting instruction
    for (i in (branchIndex - 1) downTo 0) {
        val line = lines[i]
        val inst = line.instruction ?: continue
        val op = inst.op

        // Check if this instruction modifies the flag we're testing
        val modifiedFlags = op.modifies(inst.address?.let { it::class })
        if (testedFlag !in modifiedFlags) continue

        // Found the flag-setting instruction - reconstruct expression
        condition.expr = reconstructFromInstruction(inst, testedFlag, branchIfSet)
        return
    }

    // No flag-setting instruction found in this block - use direct flag test
    condition.expr = FlagTest(testedFlag, branchIfSet)
}

/**
 * Reconstruct a condition expression from a flag-setting instruction.
 */
private fun reconstructFromInstruction(
    inst: AssemblyInstruction,
    testedFlag: AssemblyAffectable,
    branchIfSet: Boolean
): ConditionExpr {
    return when (inst.op) {
        // Comparison instructions
        AssemblyOp.CMP -> reconstructComparison(RegisterValue(AssemblyAffectable.A), inst, testedFlag, branchIfSet)
        AssemblyOp.CPX -> reconstructComparison(RegisterValue(AssemblyAffectable.X), inst, testedFlag, branchIfSet)
        AssemblyOp.CPY -> reconstructComparison(RegisterValue(AssemblyAffectable.Y), inst, testedFlag, branchIfSet)

        // Load instructions - test if value is zero/negative
        AssemblyOp.LDA -> reconstructLoad(RegisterValue(AssemblyAffectable.A), inst, testedFlag, branchIfSet)
        AssemblyOp.LDX -> reconstructLoad(RegisterValue(AssemblyAffectable.X), inst, testedFlag, branchIfSet)
        AssemblyOp.LDY -> reconstructLoad(RegisterValue(AssemblyAffectable.Y), inst, testedFlag, branchIfSet)

        // Bitwise operations
        AssemblyOp.AND -> reconstructBitwise(inst, testedFlag, branchIfSet)
        AssemblyOp.ORA -> reconstructBitwise(inst, testedFlag, branchIfSet)
        AssemblyOp.EOR -> reconstructBitwise(inst, testedFlag, branchIfSet)

        // BIT instruction - special case
        AssemblyOp.BIT -> reconstructBit(inst, testedFlag, branchIfSet)

        // Arithmetic - for now just flag test
        AssemblyOp.ADC, AssemblyOp.SBC,
        AssemblyOp.INC, AssemblyOp.DEC,
        AssemblyOp.INX, AssemblyOp.DEX,
        AssemblyOp.INY, AssemblyOp.DEY,
        AssemblyOp.ASL, AssemblyOp.LSR,
        AssemblyOp.ROL, AssemblyOp.ROR -> FlagTest(testedFlag, branchIfSet)

        else -> FlagTest(testedFlag, branchIfSet)
    }
}

/**
 * Reconstruct comparison expression (CMP, CPX, CPY).
 *
 * CMP/CPX/CPY performs register - memory, setting:
 * - Z = 1 if equal
 * - C = 1 if register >= memory (unsigned)
 * - N = 1 if result is negative
 */
private fun reconstructComparison(
    register: RegisterValue,
    inst: AssemblyInstruction,
    testedFlag: AssemblyAffectable,
    branchIfSet: Boolean
): ConditionExpr {
    val compareValue = valueFromAddressing(inst.address)

    return when (testedFlag) {
        AssemblyAffectable.Zero -> {
            // Z flag: BEQ = branch if equal, BNE = branch if not equal
            ComparisonExpr(
                register,
                if (branchIfSet) CompareOp.EQ else CompareOp.NE,
                compareValue
            )
        }
        AssemblyAffectable.Carry -> {
            // C flag: BCS = branch if >=, BCC = branch if <
            ComparisonExpr(
                register,
                if (branchIfSet) CompareOp.GE_UNSIGNED else CompareOp.LT_UNSIGNED,
                compareValue
            )
        }
        AssemblyAffectable.Negative -> {
            // N flag: BMI = branch if negative (result < 0)
            // For signed comparison, this is complex - use flag test for now
            FlagTest(testedFlag, branchIfSet)
        }
        else -> FlagTest(testedFlag, branchIfSet)
    }
}

/**
 * Reconstruct load expression (LDA, LDX, LDY).
 *
 * Load instructions set Z if value is zero, N if bit 7 is set.
 */
private fun reconstructLoad(
    register: RegisterValue,
    inst: AssemblyInstruction,
    testedFlag: AssemblyAffectable,
    branchIfSet: Boolean
): ConditionExpr {
    return when (testedFlag) {
        AssemblyAffectable.Zero -> {
            // Testing if loaded value is zero
            val loadedValue = valueFromAddressing(inst.address)
            ComparisonExpr(
                loadedValue,
                if (branchIfSet) CompareOp.EQ else CompareOp.NE,
                LiteralValue(0)
            )
        }
        AssemblyAffectable.Negative -> {
            // Testing if bit 7 is set (negative in signed interpretation)
            FlagTest(testedFlag, branchIfSet)
        }
        else -> FlagTest(testedFlag, branchIfSet)
    }
}

/**
 * Reconstruct bitwise operation expression (AND, ORA, EOR).
 *
 * These operate on accumulator and set Z/N flags on result.
 */
private fun reconstructBitwise(
    inst: AssemblyInstruction,
    testedFlag: AssemblyAffectable,
    branchIfSet: Boolean
): ConditionExpr {
    return when (testedFlag) {
        AssemblyAffectable.Zero -> {
            // Testing if result is zero
            // For AND, this is common: AND #mask; BNE = test if any bits set
            if (inst.op == AssemblyOp.AND) {
                val maskValue = valueFromAddressing(inst.address)
                if (maskValue is LiteralValue) {
                    return BitwiseTest(
                        RegisterValue(AssemblyAffectable.A),
                        maskValue.value,
                        nonZero = !branchIfSet // BNE = branch if not zero
                    )
                }
            }
            FlagTest(testedFlag, branchIfSet)
        }
        else -> FlagTest(testedFlag, branchIfSet)
    }
}

/**
 * Reconstruct BIT instruction expression.
 *
 * BIT tests bits in memory against accumulator:
 * - Z = 1 if (A & memory) == 0
 * - N = bit 7 of memory
 * - V = bit 6 of memory
 */
private fun reconstructBit(
    inst: AssemblyInstruction,
    testedFlag: AssemblyAffectable,
    branchIfSet: Boolean
): ConditionExpr {
    val memValue = valueFromAddressing(inst.address)

    return when (testedFlag) {
        AssemblyAffectable.Zero -> {
            // Z flag: testing if (A & memory) == 0
            BitwiseTest(
                RegisterValue(AssemblyAffectable.A),
                // We don't know the mask value at compile time, use flag test
                0xFF,
                nonZero = !branchIfSet
            )
        }
        else -> FlagTest(testedFlag, branchIfSet)
    }
}

/**
 * Extract a ValueExpr from an addressing mode.
 */
private fun valueFromAddressing(address: AssemblyAddressing?): ValueExpr {
    return when (address) {
        is AssemblyAddressing.ByteValue -> LiteralValue(address.value.toInt())
        is AssemblyAddressing.ShortValue -> LiteralValue(address.value.toInt())
        is AssemblyAddressing.ConstantReference -> MemoryValue(address.name)
        is AssemblyAddressing.Direct -> MemoryValue(address.label)
        is AssemblyAddressing.DirectX -> MemoryValue("${address.label},X")
        is AssemblyAddressing.DirectY -> MemoryValue("${address.label},Y")
        else -> LiteralValue(0) // Placeholder for complex addressing
    }
}

/**
 * Apply expression reconstruction to all functions.
 */
fun List<AssemblyFunction>.reconstructAllExpressions() {
    forEach { it.reconstructExpressions() }
}
