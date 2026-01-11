// by Claude - SSA (Static Single Assignment) representation for 6502 decompilation
package com.ivieleague.decompiler6502tokotlin.hand.ssa

import com.ivieleague.decompiler6502tokotlin.hand.AssemblyBlock
import com.ivieleague.decompiler6502tokotlin.hand.KotlinExpr
import com.ivieleague.decompiler6502tokotlin.hand.KVar

/**
 * SSA (Static Single Assignment) form for 6502 decompilation.
 *
 * In SSA form, each register/flag assignment creates a new version.
 * This eliminates the "stale reference" bug where flag expressions
 * reference mutable variables that have been modified:
 *
 * BEFORE (buggy):
 *   A = (A shl 1) and 0xFF  // A is modified
 *   flagC = (A and 0x80) != 0  // Uses MODIFIED A!
 *
 * AFTER (SSA):
 *   val A_1 = (A_0 shl 1) and 0xFF
 *   val flagC_1 = (A_0 and 0x80) != 0  // Uses immutable A_0
 */

/**
 * Identifies what kind of value is being tracked in SSA form.
 * These represent the 6502's registers and flags.
 */
sealed class SSABase {
    /** 6502 Accumulator register */
    object RegisterA : SSABase() {
        override fun toString() = "A"
    }

    /** 6502 X index register */
    object RegisterX : SSABase() {
        override fun toString() = "X"
    }

    /** 6502 Y index register */
    object RegisterY : SSABase() {
        override fun toString() = "Y"
    }

    /** Zero flag (Z) - set when result is zero */
    object FlagZero : SSABase() {
        override fun toString() = "Z"
    }

    /** Negative flag (N) - set when bit 7 of result is set */
    object FlagNegative : SSABase() {
        override fun toString() = "N"
    }

    /** Carry flag (C) - set by arithmetic and shift operations */
    object FlagCarry : SSABase() {
        override fun toString() = "C"
    }

    /** Overflow flag (V) - set by ADC/SBC when signed overflow occurs */
    object FlagOverflow : SSABase() {
        override fun toString() = "V"
    }

    /**
     * Get the parameter name for this base when it's a function input.
     * Registers use their letter, flags use descriptive names.
     */
    fun paramName(): String = when (this) {
        RegisterA -> "A"
        RegisterX -> "X"
        RegisterY -> "Y"
        FlagZero -> "flagZ"
        FlagNegative -> "flagN"
        FlagCarry -> "flagC"
        FlagOverflow -> "flagV"
    }

    /**
     * Get the versioned variable name for a specific version.
     * Version 0 represents the initial/parameter value.
     */
    fun versionedName(version: Int): String = when {
        version == 0 -> paramName()  // Use clean name for parameters
        else -> "${paramName()}_$version"
    }

    companion object {
        /** All register bases */
        val REGISTERS = listOf(RegisterA, RegisterX, RegisterY)

        /** All flag bases */
        val FLAGS = listOf(FlagZero, FlagNegative, FlagCarry, FlagOverflow)

        /** All bases */
        val ALL = REGISTERS + FLAGS
    }
}

/**
 * A versioned SSA value.
 *
 * Each assignment in SSA form creates a new SSAValue with an incremented version.
 * The definingExpr captures the expression that computes this value.
 *
 * @property base What register/flag this value represents
 * @property version The version number (0 = parameter/initial, 1+ = subsequent definitions)
 * @property definingExpr The expression that computes this value (references other SSA values)
 * @property definingBlock The block where this value was defined (null for parameters)
 */
data class SSAValue(
    val base: SSABase,
    val version: Int,
    val definingExpr: KotlinExpr,
    val definingBlock: AssemblyBlock? = null
) {
    /**
     * Convert this SSA value to a Kotlin expression.
     *
     * For simple cases (version 0), returns the parameter name.
     * For later versions, returns the versioned variable name.
     */
    fun toExpr(): KotlinExpr = KVar(base.versionedName(version))

    /**
     * Get the variable name for this SSA value.
     */
    val name: String get() = base.versionedName(version)

    override fun toString() = "${base.versionedName(version)} = ${definingExpr}"
}

/**
 * A phi node that merges multiple SSA values at control flow join points.
 *
 * When different predecessors define different versions of a value,
 * a phi node creates a new version that represents "whichever value came from the active path."
 *
 * Example:
 *   if (condition) {
 *       A_1 = 10
 *   } else {
 *       A_2 = 20
 *   }
 *   // At join: A_3 = phi(A_1 from then, A_2 from else)
 *
 * @property target The base (register/flag) being merged
 * @property resultVersion The version number of the phi result
 * @property sources Map from predecessor block to the SSA value from that path
 */
data class PhiNode(
    val target: SSABase,
    val resultVersion: Int,
    val sources: Map<AssemblyBlock, SSAValue>
) {
    /** Get the SSA value this phi produces */
    val result: SSAValue
        get() = SSAValue(
            base = target,
            version = resultVersion,
            definingExpr = KVar("phi(${sources.values.joinToString(", ") { it.name }})")
        )

    /** Get the variable name for this phi result */
    val name: String get() = target.versionedName(resultVersion)

    override fun toString() = "$name = phi(${sources.entries.joinToString(", ") { (block, value) ->
        "${value.name} from ${block.label ?: "block_${block.originalLineIndex}"}"
    }})"
}

/**
 * Immutable SSA state at a program point.
 *
 * Maps each SSABase to its current SSAValue (the most recent version).
 *
 * @property values The current value for each register/flag
 */
data class SSAState(
    val values: Map<SSABase, SSAValue>
) {
    /** Get the current value for a base, or null if undefined */
    operator fun get(base: SSABase): SSAValue? = values[base]

    /** Create a new state with an updated value */
    fun with(base: SSABase, value: SSAValue): SSAState =
        SSAState(values + (base to value))

    /** Create a new state with multiple updated values */
    fun withAll(updates: Map<SSABase, SSAValue>): SSAState =
        SSAState(values + updates)

    companion object {
        /** Empty initial state */
        val EMPTY = SSAState(emptyMap())

        /** Create initial state with parameters for given bases */
        fun withParameters(bases: Set<SSABase>): SSAState {
            val values = bases.associateWith { base ->
                SSAValue(
                    base = base,
                    version = 0,
                    definingExpr = KVar(base.paramName())
                )
            }
            return SSAState(values)
        }
    }
}

/**
 * Result of SSA construction for a function.
 *
 * Contains the phi nodes at each block and the SSA state at block exits.
 *
 * @property phiNodes Phi nodes at the entry of each block
 * @property blockEntryState SSA state at the entry of each block (after phis)
 * @property blockExitState SSA state at the exit of each block
 * @property allValues All SSA values defined in the function
 */
data class SSAResult(
    val phiNodes: Map<AssemblyBlock, List<PhiNode>>,
    val blockEntryState: Map<AssemblyBlock, SSAState>,
    val blockExitState: Map<AssemblyBlock, SSAState>,
    val allValues: List<SSAValue>
) {
    /** Get phi nodes for a block (empty list if none) */
    fun getPhis(block: AssemblyBlock): List<PhiNode> = phiNodes[block] ?: emptyList()

    /** Get the entry state for a block */
    fun getEntryState(block: AssemblyBlock): SSAState = blockEntryState[block] ?: SSAState.EMPTY

    /** Get the exit state for a block */
    fun getExitState(block: AssemblyBlock): SSAState = blockExitState[block] ?: SSAState.EMPTY
}

/**
 * Context for building SSA within a single block.
 *
 * Tracks the current state and version counters during instruction processing.
 */
class SSABlockContext(
    initialState: SSAState,
    private val versionCounters: MutableMap<SSABase, Int>,
    val currentBlock: AssemblyBlock
) {
    /** Current SSA state (updated as values are defined) */
    var currentState: SSAState = initialState
        private set

    /** All values defined in this block */
    val definedValues: MutableList<SSAValue> = mutableListOf()

    /**
     * Get the current value for a base.
     *
     * Returns the most recent version, or a parameter reference if undefined.
     */
    fun getValue(base: SSABase): SSAValue {
        return currentState[base] ?: SSAValue(
            base = base,
            version = 0,
            definingExpr = KVar(base.paramName())
        )
    }

    /**
     * Define a new SSA value.
     *
     * Creates a new version and updates the current state.
     *
     * @param base The register/flag being defined
     * @param expr The expression computing this value (should reference other SSA values)
     * @return The newly created SSA value
     */
    fun defineValue(base: SSABase, expr: KotlinExpr): SSAValue {
        val version = versionCounters.merge(base, 1, Int::plus)!!
        val value = SSAValue(
            base = base,
            version = version,
            definingExpr = expr,
            definingBlock = currentBlock
        )
        currentState = currentState.with(base, value)
        definedValues.add(value)
        return value
    }

    /** Get the current version counter for a base */
    fun getVersion(base: SSABase): Int = versionCounters[base] ?: 0
}
