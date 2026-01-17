@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.hand

/**
 * Runtime support for decompiled 6502 code.
 *
 * This module provides the memory model, CPU registers, and helper functions
 * that decompiled code needs to execute correctly. The design is faithful to
 * the NES/6502 architecture:
 *
 * - 64KB address space as a global byte array
 * - Registers (A, X, Y) as global mutable state
 * - Status flags as individual booleans
 * - Stack in page $01 (addresses $0100-$01FF)
 * - Little-endian word operations
 */

// =============================================================================
// MEMORY MODEL
// =============================================================================

/**
 * 64KB memory space, faithful to the NES memory map.
 *
 * Key regions:
 * - $0000-$00FF: Zero Page (fast access, used for pointers)
 * - $0100-$01FF: Stack
 * - $0200-$07FF: RAM
 * - $2000-$2007: PPU Registers
 * - $4000-$401F: APU and I/O Registers
 * - $8000-$FFFF: PRG ROM (game code)
 */
val memory = UByteArray(0x10000)

/**
 * Memory intercept callback for hardware register emulation.
 *
 * Set this to intercept reads from specific addresses (e.g., PPU registers,
 * controller ports). Return null to use the actual memory value.
 *
 * Example for controller input:
 * ```
 * memoryReadIntercept = { addr ->
 *     when (addr) {
 *         0x4016 -> controller.readBit()
 *         else -> null
 *     }
 * }
 * ```
 */
var memoryReadIntercept: ((Int) -> UByte?)? = null

/**
 * Memory write intercept callback for hardware register emulation.
 *
 * Set this to intercept writes to specific addresses. Return true if the
 * write was handled (don't write to memory), false otherwise.
 */
var memoryWriteIntercept: ((Int, UByte) -> Boolean)? = null

/**
 * Loop check callback for infinite loop detection.
 *
 * This callback should be invoked inside loops in decompiled code.
 * The callback can check for timeout and throw an exception if
 * the loop has been running too long.
 *
 * Used by SMBGameRunner to detect infinite loops in the decompiled code.
 */
var loopCheckCallback: (() -> Unit)? = null

/**
 * Call this inside loops to allow timeout detection.
 * Does nothing if no callback is set.
 */
inline fun checkLoop() {
    loopCheckCallback?.invoke()
}

/**
 * Property delegate for memory byte access.
 * Allows declaring variables that automatically read/write to specific memory addresses.
 * Uses Int internally for easier arithmetic compatibility.
 *
 * Supports memory intercepts for hardware register emulation.
 *
 * Example:
 * ```
 * var operMode by MemoryByte(0x0770)
 * operMode = 1  // Writes to memory[0x0770]
 * val x = operMode  // Reads from memory[0x0770] as Int
 * ```
 */
class MemoryByte(private val address: Int) {
    operator fun getValue(thisRef: Any?, property: kotlin.reflect.KProperty<*>): Int {
        val addr = address and 0xFFFF
        return memoryReadIntercept?.invoke(addr)?.toInt() ?: memory[addr].toInt()
    }

    operator fun setValue(thisRef: Any?, property: kotlin.reflect.KProperty<*>, value: Int) {
        val addr = address and 0xFFFF
        val byte = (value and 0xFF).toUByte()
        if (memoryWriteIntercept?.invoke(addr, byte) != true) {
            memory[addr] = byte
        }
    }
}

/**
 * Property delegate for indexed memory byte access.
 * Allows declaring array-like access to contiguous memory regions.
 *
 * Example:
 * ```
 * val enemyFlag by MemoryByteIndexed(0x000F)
 * val flag = enemyFlag[x]  // Reads from memory[0x000F + x]
 * enemyFlag[x] = 0u       // Writes to memory[0x000F + x]
 * ```
 */
class MemoryByteIndexed(private val baseAddress: Int) {
    operator fun getValue(thisRef: Any?, property: kotlin.reflect.KProperty<*>): MemoryByteArray {
        return MemoryByteArray(baseAddress)
    }

    // No setValue needed - the MemoryByteArray handles indexing
}

/**
 * Array-like accessor for indexed memory access.
 * Returned by MemoryByteIndexed delegate to enable array syntax.
 * Uses Int internally for easier arithmetic compatibility.
 *
 * Supports memory intercepts for hardware register emulation.
 */
class MemoryByteArray(private val baseAddress: Int) {
    operator fun get(index: Int): Int {
        val addr = (baseAddress + index) and 0xFFFF
        return memoryReadIntercept?.invoke(addr)?.toInt() ?: memory[addr].toInt()
    }

    operator fun set(index: Int, value: Int) {
        val addr = (baseAddress + index) and 0xFFFF
        val byte = (value and 0xFF).toUByte()
        if (memoryWriteIntercept?.invoke(addr, byte) != true) {
            memory[addr] = byte
        }
    }
}

// =============================================================================
// CPU STATE (Stack only - registers are local variables in decompiled functions)
// =============================================================================

// by Claude - Removed global A, X, Y registers. The decompiled code uses local variables
// and parameter passing for registers, which is the correct functional approach.
// Global registers would make this an interpreter, not a decompilation.

/**
 * Stack Pointer - points to current top of stack in page $01.
 * Stack grows downward: push decrements, pull increments.
 * Starts at $FF (address $01FF).
 */
var SP: Int = 0xFF

// =============================================================================
// STATUS FLAGS
// =============================================================================

/**
 * Negative Flag (N) - Set if bit 7 of result is 1.
 * Used to test sign of value in two's complement arithmetic.
 */
var flagN: Boolean = false

/**
 * Overflow Flag (V) - Set if signed overflow occurred.
 * Set when the sign of the result doesn't match the expected sign.
 */
var flagV: Boolean = false

/**
 * Zero Flag (Z) - Set if result is zero.
 * Most common flag for conditionals and loops.
 */
var flagZ: Boolean = false

/**
 * Carry Flag (C) - Set if unsigned overflow/underflow occurred.
 * Also used as the "borrow" flag for subtraction.
 */
var flagC: Boolean = false

/**
 * Interrupt Disable Flag (I) - When set, IRQ interrupts are disabled.
 */
var flagI: Boolean = false

/**
 * Decimal Flag (D) - When set, arithmetic uses BCD mode.
 * Note: NES doesn't use decimal mode, so this is usually ignored.
 */
var flagD: Boolean = false

// =============================================================================
// MEMORY ACCESS HELPERS
// =============================================================================

/**
 * Read a 16-bit word from memory in little-endian format.
 *
 * This is the primary helper for indirect addressing modes:
 * - (zp,X): `memory[readWord((zp + X) and 0xFF)]`
 * - (zp),Y: `memory[readWord(zp) + Y]`
 *
 * @param addr The address to read from (low byte location)
 * @return The 16-bit value as an Int (low byte | high byte << 8)
 */
fun readWord(addr: Int): Int {
    val low = memory[addr and 0xFFFF].toInt()
    val high = memory[(addr + 1) and 0xFFFF].toInt()
    return low or (high shl 8)
}

/**
 * Write a 16-bit word to memory in little-endian format.
 *
 * @param addr The address to write to (low byte location)
 * @param value The 16-bit value to write
 */
fun writeWord(addr: Int, value: Int) {
    memory[addr and 0xFFFF] = (value and 0xFF).toUByte()
    memory[(addr + 1) and 0xFFFF] = ((value shr 8) and 0xFF).toUByte()
}

// =============================================================================
// STACK OPERATIONS
// =============================================================================

/**
 * Push a byte onto the stack.
 * Stack is in page $01 ($0100-$01FF), grows downward.
 */
fun push(value: Int) {
    memory[0x100 + SP] = (value and 0xFF).toUByte()
    SP = (SP - 1) and 0xFF
}

/**
 * Pull (pop) a byte from the stack.
 * Stack is in page $01 ($0100-$01FF), grows downward.
 */
fun pull(): Int {
    SP = (SP + 1) and 0xFF
    return memory[0x100 + SP].toInt()
}

/**
 * Push a 16-bit word onto the stack (high byte first, then low byte).
 * Used by JSR to push return address.
 */
fun pushWord(value: Int) {
    push((value shr 8) and 0xFF)  // High byte first
    push(value and 0xFF)           // Low byte second
}

/**
 * Pull a 16-bit word from the stack (low byte first, then high byte).
 * Used by RTS to restore return address.
 */
fun pullWord(): Int {
    val low = pull()
    val high = pull()
    return low or (high shl 8)
}

// =============================================================================
// FLAG UPDATE HELPERS
// =============================================================================

/**
 * Update Zero and Negative flags based on a value.
 * Called after most arithmetic/logical operations.
 *
 * @param value The 8-bit result value
 */
fun updateZN(value: Int) {
    flagZ = (value and 0xFF) == 0
    flagN = (value and 0x80) != 0
}

/**
 * Update all flags for ADC (add with carry).
 *
 * @param a Original accumulator value
 * @param operand Value being added
 * @param result Full result (may be > 255)
 */
fun updateFlagsADC(a: Int, operand: Int, result: Int) {
    flagC = result > 0xFF
    flagZ = (result and 0xFF) == 0
    flagN = (result and 0x80) != 0
    // Overflow: sign of result differs from expected sign
    flagV = ((a xor result) and (operand xor result) and 0x80) != 0
}

/**
 * Update all flags for SBC (subtract with borrow).
 *
 * @param a Original accumulator value
 * @param operand Value being subtracted
 * @param result Full result (may be negative)
 */
fun updateFlagsSBC(a: Int, operand: Int, result: Int) {
    flagC = result >= 0  // Carry is set if no borrow needed
    flagZ = (result and 0xFF) == 0
    flagN = (result and 0x80) != 0
    // Overflow for subtraction
    flagV = ((a xor operand) and (a xor result) and 0x80) != 0
}

/**
 * Update flags for comparison (CMP, CPX, CPY).
 *
 * @param reg Register value
 * @param operand Value being compared
 */
fun updateFlagsCompare(reg: Int, operand: Int) {
    val result = reg - operand
    flagC = reg >= operand  // Carry set if reg >= operand (unsigned)
    flagZ = (result and 0xFF) == 0
    flagN = (result and 0x80) != 0
}

// =============================================================================
// INITIALIZATION
// =============================================================================

/**
 * Reset the CPU state to power-on defaults.
 * Note: A, X, Y registers are local variables in decompiled functions,
 * so only stack pointer and flags need resetting here.
 */
fun resetCPU() {
    // by Claude - Removed A, X, Y resets (now local variables in functions)
    SP = 0xFF
    flagN = false
    flagV = false
    flagZ = false
    flagC = false
    flagI = true  // Interrupts disabled on reset
    flagD = false
}

/**
 * Clear all memory to zero.
 */
fun clearMemory() {
    // Only clear RAM (0x0000-0x7FFF), preserve ROM area (0x8000+)
    // This allows ROM data to persist across test runs
    for (i in 0 until 0x8000) {
        memory[i] = 0u
    }
}

/**
 * Clear all memory including ROM area.
 * Use this sparingly - most tests should use clearMemory() instead.
 */
fun clearAllMemory() {
    for (i in memory.indices) {
        memory[i] = 0u
    }
}

/**
 * Load a ROM into memory starting at a given address.
 *
 * @param data The ROM data to load
 * @param startAddr The starting address (typically $8000 for PRG ROM)
 */
fun loadROM(data: ByteArray, startAddr: Int = 0x8000) {
    for (i in data.indices) {
        memory[(startAddr + i) and 0xFFFF] = data[i].toUByte()
    }
}
