package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 18: Constant Propagation
 * - Track constant values through execution
 * - Fold constant expressions
 * - Identify unreachable branches
 * - Enable optimizations
 */

/**
 * Represents a constant value in the 6502 system
 */
sealed class ConstantValue {
    /** A known byte value (0-255) */
    data class Byte(val value: Int) : ConstantValue() {
        init {
            require(value in 0..255) { "Byte value must be 0-255, got $value" }
        }
        override fun toString() = "#\$${value.toString(16).uppercase().padStart(2, '0')}"
    }

    /** A known 16-bit word value */
    data class Word(val high: Int, val low: Int) : ConstantValue() {
        init {
            require(high in 0..255) { "High byte must be 0-255, got $high" }
            require(low in 0..255) { "Low byte must be 0-255, got $low" }
        }
        val value: Int get() = (high shl 8) or low
        override fun toString() = "#\$${value.toString(16).uppercase().padStart(4, '0')}"
    }

    /** Value is not yet known (top of lattice) */
    object Unknown : ConstantValue() {
        override fun toString() = "?"
    }

    /** Value has conflicting definitions (bottom of lattice) */
    object Bottom : ConstantValue() {
        override fun toString() = "⊥"
    }

    /**
     * Join two constant values in the lattice
     * Unknown ⊔ x = x
     * x ⊔ Bottom = Bottom
     * x ⊔ x = x
     * x ⊔ y = Bottom (if x ≠ y)
     */
    fun join(other: ConstantValue): ConstantValue {
        return when {
            this == Unknown -> other
            other == Unknown -> this
            this == Bottom || other == Bottom -> Bottom
            this == other -> this
            else -> Bottom
        }
    }
}

/** Processor status flags */
enum class ProcessorFlag {
    N,  // Negative
    V,  // Overflow
    Z,  // Zero
    C   // Carry
}

/**
 * Abstract state representing constant values at a program point
 */
data class ConstantState(
    val registerA: ConstantValue = ConstantValue.Unknown,
    val registerX: ConstantValue = ConstantValue.Unknown,
    val registerY: ConstantValue = ConstantValue.Unknown,
    val memory: Map<Int, ConstantValue> = emptyMap(),
    val flags: Map<ProcessorFlag, Boolean?> = emptyMap()  // null = unknown
) {
    /**
     * Join two states (for merge points in CFG)
     */
    fun join(other: ConstantState): ConstantState {
        val newMemory = mutableMapOf<Int, ConstantValue>()

        // Join memory values
        val allAddresses = (memory.keys + other.memory.keys)
        allAddresses.forEach { addr ->
            val thisVal = memory[addr] ?: ConstantValue.Unknown
            val otherVal = other.memory[addr] ?: ConstantValue.Unknown
            newMemory[addr] = thisVal.join(otherVal)
        }

        // Join flags
        val newFlags = mutableMapOf<ProcessorFlag, Boolean?>()
        ProcessorFlag.entries.forEach { flag ->
            val thisFlag = flags[flag]
            val otherFlag = other.flags[flag]
            newFlags[flag] = when {
                thisFlag == null || otherFlag == null -> null
                thisFlag == otherFlag -> thisFlag
                else -> null  // Conflicting values
            }
        }

        return ConstantState(
            registerA = registerA.join(other.registerA),
            registerX = registerX.join(other.registerX),
            registerY = registerY.join(other.registerY),
            memory = newMemory,
            flags = newFlags
        )
    }

    /**
     * Create a copy with updated register A
     */
    fun withA(value: ConstantValue) = copy(registerA = value)

    /**
     * Create a copy with updated register X
     */
    fun withX(value: ConstantValue) = copy(registerX = value)

    /**
     * Create a copy with updated register Y
     */
    fun withY(value: ConstantValue) = copy(registerY = value)

    /**
     * Create a copy with updated memory
     */
    fun withMemory(address: Int, value: ConstantValue): ConstantState {
        return copy(memory = memory + (address to value))
    }

    /**
     * Create a copy with updated flag
     */
    fun withFlag(flag: ProcessorFlag, value: Boolean?): ConstantState {
        return copy(flags = flags + (flag to value))
    }
}

/**
 * Constant facts for a single basic block
 */
data class BlockConstantFacts(
    val entryState: ConstantState,
    val exitState: ConstantState,
    val foldableInstructions: Set<Int>  // line indices where constants can be folded
)

/**
 * A constant expression that can potentially be simplified
 */
data class ConstantExpression(
    val lineIndex: Int,
    val originalInstruction: AssemblyInstruction,
    val constantResult: ConstantValue,
    val canEliminate: Boolean  // true if result is never used
)

/**
 * Constant analysis for a single function
 */
data class FunctionConstantAnalysis(
    val function: FunctionCfg,
    val blockFacts: Map<Int, BlockConstantFacts>,  // leader -> facts
    val constantExpressions: List<ConstantExpression>
)

/**
 * Complete constant propagation analysis
 */
data class ConstantPropagationAnalysis(
    val functions: List<FunctionConstantAnalysis>
)

/**
 * Perform constant propagation analysis on all functions
 */
fun AssemblyCodeFile.analyzeConstants(
    cfg: CfgConstruction
): ConstantPropagationAnalysis {
    val functionAnalyses = cfg.functions.map { function ->
        analyzeConstantsForFunction(this, function)
    }

    return ConstantPropagationAnalysis(functions = functionAnalyses)
}

/**
 * Analyze constants for a single function
 */
private fun analyzeConstantsForFunction(
    codeFile: AssemblyCodeFile,
    function: FunctionCfg
): FunctionConstantAnalysis {
    // Initialize block facts
    val blockFacts = mutableMapOf<Int, BlockConstantFacts>()

    function.blocks.forEach { block ->
        blockFacts[block.leaderIndex] = BlockConstantFacts(
            entryState = ConstantState(),
            exitState = ConstantState(),
            foldableInstructions = emptySet()
        )
    }

    // Iterative dataflow analysis
    var changed = true
    var iterations = 0

    while (changed && iterations < 100) {
        changed = false
        iterations++

        function.blocks.forEach { block ->
            val oldFacts = blockFacts[block.leaderIndex]!!

            // Compute entry state by joining predecessors' exit states
            val entryState = computeEntryState(function, block, blockFacts)

            // Transfer through block
            val (exitState, foldable) = transferBlock(codeFile, block, entryState)

            // Update if changed
            if (entryState != oldFacts.entryState || exitState != oldFacts.exitState) {
                blockFacts[block.leaderIndex] = BlockConstantFacts(
                    entryState = entryState,
                    exitState = exitState,
                    foldableInstructions = foldable
                )
                changed = true
            }
        }
    }

    // Collect constant expressions
    val constantExpressions = collectConstantExpressions(codeFile, function, blockFacts)

    return FunctionConstantAnalysis(
        function = function,
        blockFacts = blockFacts,
        constantExpressions = constantExpressions
    )
}

/**
 * Compute entry state for a block by joining predecessor exit states
 */
private fun computeEntryState(
    function: FunctionCfg,
    block: BasicBlock,
    blockFacts: Map<Int, BlockConstantFacts>
): ConstantState {
    // Find predecessor blocks
    val predecessors = function.edges
        .filter { it.toLeader == block.leaderIndex }
        .mapNotNull { edge -> blockFacts[edge.fromLeader] }

    if (predecessors.isEmpty()) {
        // Entry block - start with unknown state
        return ConstantState()
    }

    // Join all predecessor exit states
    return predecessors
        .map { it.exitState }
        .reduce { acc, state -> acc.join(state) }
}

/**
 * Transfer function: compute exit state from entry state by simulating block execution
 */
private fun transferBlock(
    codeFile: AssemblyCodeFile,
    block: BasicBlock,
    entryState: ConstantState
): Pair<ConstantState, Set<Int>> {
    var state = entryState
    val foldable = mutableSetOf<Int>()

    block.lineIndexes.forEach { lineIndex ->
        val line = codeFile.lines.getOrNull(lineIndex) ?: return@forEach
        val instruction = line.instruction ?: return@forEach

        // Apply transfer function for this instruction
        val (newState, canFold) = transferInstruction(instruction, state)
        state = newState

        if (canFold) {
            foldable.add(lineIndex)
        }
    }

    return state to foldable
}

/**
 * Transfer function for a single instruction
 */
private fun transferInstruction(
    instruction: AssemblyInstruction,
    state: ConstantState
): Pair<ConstantState, Boolean> {
    var newState = state
    var canFold = false

    when (instruction.op) {
        // Load immediate - define constant
        AssemblyOp.LDA -> {
            val value = extractImmediateValue(instruction.address)
            if (value != null) {
                newState = state.withA(ConstantValue.Byte(value))
                canFold = false  // Can't fold load itself
            } else {
                // Load from memory - check if memory is constant
                val addr = extractDirectAddress(instruction.address)
                val memValue = if (addr != null) state.memory[addr] else null
                newState = state.withA(memValue ?: ConstantValue.Unknown)
            }
        }

        AssemblyOp.LDX -> {
            val value = extractImmediateValue(instruction.address)
            if (value != null) {
                newState = state.withX(ConstantValue.Byte(value))
            } else {
                val addr = extractDirectAddress(instruction.address)
                val memValue = if (addr != null) state.memory[addr] else null
                newState = state.withX(memValue ?: ConstantValue.Unknown)
            }
        }

        AssemblyOp.LDY -> {
            val value = extractImmediateValue(instruction.address)
            if (value != null) {
                newState = state.withY(ConstantValue.Byte(value))
            } else {
                val addr = extractDirectAddress(instruction.address)
                val memValue = if (addr != null) state.memory[addr] else null
                newState = state.withY(memValue ?: ConstantValue.Unknown)
            }
        }

        // Store - propagate constant to memory
        AssemblyOp.STA -> {
            val addr = extractDirectAddress(instruction.address)
            if (addr != null) {
                newState = state.withMemory(addr, state.registerA)
            }
        }

        AssemblyOp.STX -> {
            val addr = extractDirectAddress(instruction.address)
            if (addr != null) {
                newState = state.withMemory(addr, state.registerX)
            }
        }

        AssemblyOp.STY -> {
            val addr = extractDirectAddress(instruction.address)
            if (addr != null) {
                newState = state.withMemory(addr, state.registerY)
            }
        }

        // Register transfers
        AssemblyOp.TAX -> {
            newState = state.withX(state.registerA)
        }

        AssemblyOp.TAY -> {
            newState = state.withY(state.registerA)
        }

        AssemblyOp.TXA -> {
            newState = state.withA(state.registerX)
        }

        AssemblyOp.TYA -> {
            newState = state.withA(state.registerY)
        }

        AssemblyOp.TSX -> {
            newState = state.withX(ConstantValue.Unknown)  // Stack pointer not tracked
        }

        AssemblyOp.TXS -> {
            // Don't track stack pointer
        }

        // Arithmetic with constants
        AssemblyOp.ADC -> {
            val operand = extractImmediateValue(instruction.address)
            if (operand != null && state.registerA is ConstantValue.Byte) {
                val carry = if (state.flags[ProcessorFlag.C] == true) 1 else 0
                val result = (state.registerA.value + operand + carry) and 0xFF
                newState = state.withA(ConstantValue.Byte(result))
                canFold = true
            } else {
                newState = state.withA(ConstantValue.Unknown)
            }
        }

        AssemblyOp.SBC -> {
            val operand = extractImmediateValue(instruction.address)
            if (operand != null && state.registerA is ConstantValue.Byte) {
                val carry = if (state.flags[ProcessorFlag.C] == true) 1 else 0
                val result = (state.registerA.value - operand - (1 - carry)) and 0xFF
                newState = state.withA(ConstantValue.Byte(result))
                canFold = true
            } else {
                newState = state.withA(ConstantValue.Unknown)
            }
        }

        AssemblyOp.AND -> {
            val operand = extractImmediateValue(instruction.address)
            if (operand != null && state.registerA is ConstantValue.Byte) {
                val result = state.registerA.value and operand
                newState = state.withA(ConstantValue.Byte(result))
                canFold = true
            } else {
                newState = state.withA(ConstantValue.Unknown)
            }
        }

        AssemblyOp.ORA -> {
            val operand = extractImmediateValue(instruction.address)
            if (operand != null && state.registerA is ConstantValue.Byte) {
                val result = state.registerA.value or operand
                newState = state.withA(ConstantValue.Byte(result))
                canFold = true
            } else {
                newState = state.withA(ConstantValue.Unknown)
            }
        }

        AssemblyOp.EOR -> {
            val operand = extractImmediateValue(instruction.address)
            if (operand != null && state.registerA is ConstantValue.Byte) {
                val result = state.registerA.value xor operand
                newState = state.withA(ConstantValue.Byte(result))
                canFold = true
            } else {
                newState = state.withA(ConstantValue.Unknown)
            }
        }

        // Increment/decrement
        AssemblyOp.INX -> {
            if (state.registerX is ConstantValue.Byte) {
                val result = (state.registerX.value + 1) and 0xFF
                newState = state.withX(ConstantValue.Byte(result))
            } else {
                newState = state.withX(ConstantValue.Unknown)
            }
        }

        AssemblyOp.INY -> {
            if (state.registerY is ConstantValue.Byte) {
                val result = (state.registerY.value + 1) and 0xFF
                newState = state.withY(ConstantValue.Byte(result))
            } else {
                newState = state.withY(ConstantValue.Unknown)
            }
        }

        AssemblyOp.DEX -> {
            if (state.registerX is ConstantValue.Byte) {
                val result = (state.registerX.value - 1) and 0xFF
                newState = state.withX(ConstantValue.Byte(result))
            } else {
                newState = state.withX(ConstantValue.Unknown)
            }
        }

        AssemblyOp.DEY -> {
            if (state.registerY is ConstantValue.Byte) {
                val result = (state.registerY.value - 1) and 0xFF
                newState = state.withY(ConstantValue.Byte(result))
            } else {
                newState = state.withY(ConstantValue.Unknown)
            }
        }

        // Comparisons - set flags but don't change registers
        AssemblyOp.CMP -> {
            val operand = extractImmediateValue(instruction.address)
            if (operand != null && state.registerA is ConstantValue.Byte) {
                val result = state.registerA.value - operand
                newState = state
                    .withFlag(ProcessorFlag.Z, result and 0xFF == 0)
                    .withFlag(ProcessorFlag.N, (result and 0x80) != 0)
                    .withFlag(ProcessorFlag.C, result >= 0)
            }
        }

        AssemblyOp.CPX -> {
            val operand = extractImmediateValue(instruction.address)
            if (operand != null && state.registerX is ConstantValue.Byte) {
                val result = state.registerX.value - operand
                newState = state
                    .withFlag(ProcessorFlag.Z, result and 0xFF == 0)
                    .withFlag(ProcessorFlag.N, (result and 0x80) != 0)
                    .withFlag(ProcessorFlag.C, result >= 0)
            }
        }

        AssemblyOp.CPY -> {
            val operand = extractImmediateValue(instruction.address)
            if (operand != null && state.registerY is ConstantValue.Byte) {
                val result = state.registerY.value - operand
                newState = state
                    .withFlag(ProcessorFlag.Z, result and 0xFF == 0)
                    .withFlag(ProcessorFlag.N, (result and 0x80) != 0)
                    .withFlag(ProcessorFlag.C, result >= 0)
            }
        }

        // Flag operations
        AssemblyOp.CLC -> newState = state.withFlag(ProcessorFlag.C, false)
        AssemblyOp.SEC -> newState = state.withFlag(ProcessorFlag.C, true)
        AssemblyOp.CLV -> newState = state.withFlag(ProcessorFlag.V, false)
        AssemblyOp.SEI, AssemblyOp.CLI, AssemblyOp.SED, AssemblyOp.CLD -> {
            // Don't track interrupt/decimal flags
        }

        // Operations that affect A but we don't track the result
        AssemblyOp.ASL, AssemblyOp.LSR, AssemblyOp.ROL, AssemblyOp.ROR -> {
            if (instruction.address == null || instruction.address is AssemblyAddressing.Accumulator) {
                newState = state.withA(ConstantValue.Unknown)
            }
        }

        // Stack operations - don't track values on stack
        AssemblyOp.PHA, AssemblyOp.PHP -> {
            // Don't track stack
        }

        AssemblyOp.PLA -> {
            newState = state.withA(ConstantValue.Unknown)
        }

        AssemblyOp.PLP -> {
            // Flags become unknown
            newState = state.copy(flags = emptyMap())
        }

        // Branches don't change state
        AssemblyOp.BCC, AssemblyOp.BCS, AssemblyOp.BEQ, AssemblyOp.BMI,
        AssemblyOp.BNE, AssemblyOp.BPL, AssemblyOp.BVC, AssemblyOp.BVS -> {
            // No state change
        }

        // Control flow
        AssemblyOp.JMP, AssemblyOp.JSR, AssemblyOp.RTS, AssemblyOp.RTI -> {
            // JSR/RTS might clobber everything - be conservative
            if (instruction.op == AssemblyOp.JSR) {
                // After JSR, all registers could be clobbered
                newState = ConstantState()
            }
        }

        // Other instructions
        AssemblyOp.BIT -> {
            // BIT affects flags but not registers
        }

        AssemblyOp.NOP -> {
            // No change
        }

        AssemblyOp.BRK -> {
            // End of execution
        }

        else -> {
            // Unknown instruction - invalidate everything
            newState = ConstantState()
        }
    }

    return newState to canFold
}

/**
 * Extract immediate value from addressing mode
 */
private fun extractImmediateValue(addressing: AssemblyAddressing?): Int? {
    return when (addressing) {
        is AssemblyAddressing.ValueHex -> addressing.value.toInt() and 0xFF
        is AssemblyAddressing.ValueBinary -> addressing.value.toInt() and 0xFF
        is AssemblyAddressing.ValueDecimal -> addressing.value.toInt() and 0xFF
        else -> null
    }
}

/**
 * Extract direct memory address from addressing mode
 */
private fun extractDirectAddress(addressing: AssemblyAddressing?): Int? {
    return when (addressing) {
        is AssemblyAddressing.Label -> parseAddress(addressing.label)
        else -> null
    }
}

/**
 * Parse address from label string
 */
private fun parseAddress(label: String): Int? {
    return try {
        when {
            label.startsWith("$") -> label.substring(1).toInt(16)
            label.startsWith("0x") -> label.substring(2).toInt(16)
            label.all { it.isDigit() } -> label.toInt()
            else -> null
        }
    } catch (e: NumberFormatException) {
        null
    }
}

/**
 * Collect all constant expressions in the function
 */
private fun collectConstantExpressions(
    codeFile: AssemblyCodeFile,
    function: FunctionCfg,
    blockFacts: Map<Int, BlockConstantFacts>
): List<ConstantExpression> {
    val expressions = mutableListOf<ConstantExpression>()

    function.blocks.forEach { block ->
        val facts = blockFacts[block.leaderIndex] ?: return@forEach

        facts.foldableInstructions.forEach { lineIndex ->
            val line = codeFile.lines.getOrNull(lineIndex)
            val instruction = line?.instruction

            if (instruction != null) {
                // Determine the constant result (would need state tracking within block)
                expressions.add(
                    ConstantExpression(
                        lineIndex = lineIndex,
                        originalInstruction = instruction,
                        constantResult = ConstantValue.Unknown,  // Placeholder
                        canEliminate = false
                    )
                )
            }
        }
    }

    return expressions
}
