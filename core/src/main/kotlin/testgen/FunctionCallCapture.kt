@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.testgen

import com.ivieleague.decompiler6502tokotlin.interpreter.CPU6502
import kotlinx.serialization.Serializable

/**
 * Captures complete state for a single function call.
 * Used for generating unit tests from interpreter execution traces.
 */
@Serializable
data class FunctionCallCapture(
    /** Function entry address (e.g., 0x8F4E) */
    val functionAddress: Int,

    /** Frame number when this call occurred */
    val frame: Int,

    /** Timestamp within frame (instruction count) */
    val timestamp: Long,

    /** Caller address (JSR location) */
    val callerAddress: Int,

    /** Call stack depth at entry */
    val callDepth: Int,

    /** CPU state at function entry */
    val inputState: CpuState,

    /** Memory addresses read during execution (inputs) */
    val memoryReads: Map<Int, Int>,

    /** CPU state at function exit (RTS) */
    val outputState: CpuState,

    /** Memory addresses written during execution (outputs) */
    val memoryWrites: Map<Int, Int>,

    /** Nested function calls made during execution (addresses only) */
    val nestedCalls: List<Int>,

    /** Unique hash of input state for deduplication */
    val inputStateHash: Long,

    /** Function name (if known from decompiled metadata) */
    val functionName: String? = null
)

/**
 * CPU register and flag state snapshot.
 */
@Serializable
data class CpuState(
    val A: Int,
    val X: Int,
    val Y: Int,
    val SP: Int,
    val PC: Int,
    val flagN: Boolean,
    val flagV: Boolean,
    val flagZ: Boolean,
    val flagC: Boolean,
    val flagI: Boolean,
    val flagD: Boolean
) {
    companion object {
        /**
         * Create a CpuState snapshot from a CPU6502 instance.
         */
        fun from(cpu: CPU6502): CpuState = CpuState(
            A = cpu.A.toInt(),
            X = cpu.X.toInt(),
            Y = cpu.Y.toInt(),
            SP = cpu.SP.toInt(),
            PC = cpu.PC.toInt(),
            flagN = cpu.N,
            flagV = cpu.V,
            flagZ = cpu.Z,
            flagC = cpu.C,
            flagI = cpu.I,
            flagD = cpu.D
        )
    }

    /**
     * Apply this state to a CPU6502 instance.
     */
    fun applyTo(cpu: CPU6502) {
        cpu.A = A.toUByte()
        cpu.X = X.toUByte()
        cpu.Y = Y.toUByte()
        cpu.SP = SP.toUByte()
        cpu.PC = PC.toUShort()
        cpu.N = flagN
        cpu.V = flagV
        cpu.Z = flagZ
        cpu.C = flagC
        cpu.I = flagI
        cpu.D = flagD
    }
}

/**
 * Generate a hash that uniquely identifies the "interesting" input state.
 * Used for deduplication - we only keep one test case per unique input.
 */
fun computeInputStateHash(
    functionAddress: Int,
    cpuState: CpuState,
    memoryReads: Map<Int, Int>
): Long {
    var hash = functionAddress.toLong() * 31

    // Include CPU registers
    hash = hash * 31 + cpuState.A
    hash = hash * 31 + cpuState.X
    hash = hash * 31 + cpuState.Y
    hash = hash * 31 + cpuState.SP

    // Include flags as bit pattern
    var flags = 0
    if (cpuState.flagN) flags = flags or 0x80
    if (cpuState.flagV) flags = flags or 0x40
    if (cpuState.flagZ) flags = flags or 0x02
    if (cpuState.flagC) flags = flags or 0x01
    hash = hash * 31 + flags

    // Include memory reads (sorted for determinism)
    for ((addr, value) in memoryReads.toSortedMap()) {
        hash = hash * 31 + addr
        hash = hash * 31 + value
    }

    return hash
}
