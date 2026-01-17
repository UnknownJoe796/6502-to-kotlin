@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.validation

import com.ivieleague.decompiler6502tokotlin.hand.*
import com.ivieleague.decompiler6502tokotlin.interpreter.*
import java.io.File
import kotlin.test.assertEquals

/**
 * Framework for comparing binary 6502 interpreter execution against decompiled Kotlin code.
 *
 * This framework enables function-by-function validation by:
 * 1. Running a function in the binary interpreter (actual NES ROM execution)
 * 2. Running the equivalent decompiled Kotlin function
 * 3. Comparing memory state, registers, and flags
 *
 * This is the key validation step - proving the decompiled code behaves identically
 * to the original ROM.
 */
class BinaryComparisonFramework {

    /**
     * Represents the state we want to compare.
     */
    data class State(
        val a: Int,
        val x: Int,
        val y: Int,
        val sp: Int,
        val n: Boolean,
        val v: Boolean,
        val z: Boolean,
        val c: Boolean,
        val memory: Map<Int, Int>  // Address -> Value
    ) {
        companion object {
            /**
             * Capture state from the binary interpreter.
             */
            fun fromBinaryInterpreter(interp: BinaryInterpreter6502, addresses: List<Int>): State {
                return State(
                    a = interp.cpu.A.toInt(),
                    x = interp.cpu.X.toInt(),
                    y = interp.cpu.Y.toInt(),
                    sp = interp.cpu.SP.toInt(),
                    n = interp.cpu.N,
                    v = interp.cpu.V,
                    z = interp.cpu.Z,
                    c = interp.cpu.C,
                    memory = addresses.associateWith { interp.memory.readByte(it).toInt() }
                )
            }

            /**
             * Capture state from the decompiled code's runtime.
             * by Claude - Changed from using globals to explicit parameters since
             * decompiled code uses local variables, not globals.
             */
            fun fromDecompiledRuntime(
                a: Int,
                x: Int,
                y: Int,
                addresses: List<Int>
            ): State {
                return State(
                    a = a,
                    x = x,
                    y = y,
                    sp = SP,
                    n = flagN,
                    v = flagV,
                    z = flagZ,
                    c = flagC,
                    memory = addresses.associateWith { memory[it].toInt() }
                )
            }
        }

        /**
         * Assert this state equals another.
         */
        fun assertEqualsTo(
            other: State,
            message: String = "",
            ignoreFlags: Set<String> = emptySet(),
            ignoreRegisters: Set<String> = emptySet()
        ) {
            val prefix = if (message.isNotEmpty()) "$message: " else ""

            if ("A" !in ignoreRegisters) {
                assertEquals(a, other.a, "${prefix}Register A mismatch: expected $a, got ${other.a}")
            }
            if ("X" !in ignoreRegisters) {
                assertEquals(x, other.x, "${prefix}Register X mismatch: expected $x, got ${other.x}")
            }
            if ("Y" !in ignoreRegisters) {
                assertEquals(y, other.y, "${prefix}Register Y mismatch: expected $y, got ${other.y}")
            }
            if ("SP" !in ignoreRegisters) {
                assertEquals(sp, other.sp, "${prefix}Stack pointer mismatch: expected $sp, got ${other.sp}")
            }

            if ("N" !in ignoreFlags) {
                assertEquals(n, other.n, "${prefix}Flag N mismatch: expected $n, got ${other.n}")
            }
            if ("V" !in ignoreFlags) {
                assertEquals(v, other.v, "${prefix}Flag V mismatch: expected $v, got ${other.v}")
            }
            if ("Z" !in ignoreFlags) {
                assertEquals(z, other.z, "${prefix}Flag Z mismatch: expected $z, got ${other.z}")
            }
            if ("C" !in ignoreFlags) {
                assertEquals(c, other.c, "${prefix}Flag C mismatch: expected $c, got ${other.c}")
            }

            // Compare memory
            for ((addr, expectedValue) in memory) {
                val actualValue = other.memory[addr] ?: 0
                assertEquals(
                    expectedValue, actualValue,
                    "${prefix}Memory[$${addr.toString(16).uppercase()}] mismatch: expected $expectedValue, got $actualValue"
                )
            }
        }

        override fun toString(): String = buildString {
            appendLine("State:")
            appendLine("  A=$${a.toString(16).padStart(2, '0')} X=$${x.toString(16).padStart(2, '0')} Y=$${y.toString(16).padStart(2, '0')} SP=$${sp.toString(16).padStart(2, '0')}")
            appendLine("  N=$n V=$v Z=$z C=$c")
            if (memory.isNotEmpty()) {
                appendLine("  Memory: ${memory.entries.joinToString { "$${it.key.toString(16)}=${it.value}" }}")
            }
        }
    }

    /**
     * Sync state from binary interpreter to decompiled runtime.
     * by Claude - Returns register values since decompiled code uses local variables.
     * Memory and flags are still synced via globals.
     */
    data class RegisterState(val a: Int, val x: Int, val y: Int)

    fun syncInterpreterToDecompiled(interp: BinaryInterpreter6502, memoryRanges: List<IntRange> = emptyList()): RegisterState {
        // Sync stack pointer (still global)
        SP = interp.cpu.SP.toInt()

        // Sync flags (still global)
        flagN = interp.cpu.N
        flagV = interp.cpu.V
        flagZ = interp.cpu.Z
        flagC = interp.cpu.C
        flagI = interp.cpu.I
        flagD = interp.cpu.D

        // Sync memory
        for (range in memoryRanges) {
            for (addr in range) {
                memory[addr] = interp.memory.readByte(addr)
            }
        }

        // Return register values for caller to pass to decompiled function
        return RegisterState(
            a = interp.cpu.A.toInt(),
            x = interp.cpu.X.toInt(),
            y = interp.cpu.Y.toInt()
        )
    }

    /**
     * Sync state from decompiled runtime to binary interpreter.
     * by Claude - Takes register values as parameters since decompiled code uses local variables.
     */
    fun syncDecompiledToInterpreter(
        interp: BinaryInterpreter6502,
        a: Int,
        x: Int,
        y: Int,
        memoryRanges: List<IntRange> = emptyList()
    ) {
        // Sync registers (from parameters)
        interp.cpu.A = a.toUByte()
        interp.cpu.X = x.toUByte()
        interp.cpu.Y = y.toUByte()
        interp.cpu.SP = SP.toUByte()

        // Sync flags (still global)
        interp.cpu.N = flagN
        interp.cpu.V = flagV
        interp.cpu.Z = flagZ
        interp.cpu.C = flagC
        interp.cpu.I = flagI
        interp.cpu.D = flagD

        // Sync memory
        for (range in memoryRanges) {
            for (addr in range) {
                interp.memory.writeByte(addr, memory[addr])
            }
        }
    }

    /**
     * Load SMB ROM into binary interpreter.
     */
    fun loadSMBRom(interp: BinaryInterpreter6502): Boolean {
        val romPaths = listOf(
            "local/roms/smb.nes",
            "smb.nes",
            "../smb.nes",
            "../../smb.nes"
        )
        val romFile = romPaths.map { File(it) }.firstOrNull { it.exists() } ?: return false
        val rom = NESLoader.load(romFile)
        NESLoader.loadIntoMemory(rom, interp.memory)
        return true
    }

    /**
     * Run the binary interpreter until it returns from a subroutine (RTS at original SP level).
     */
    fun runUntilRTS(interp: BinaryInterpreter6502, maxCycles: Int = 100000): Int {
        val initialSP = interp.cpu.SP.toInt()
        var cycles = 0

        while (cycles < maxCycles && !interp.halted) {
            val pc = interp.cpu.PC.toInt()
            val opcode = interp.memory.readByte(pc).toInt()

            cycles += interp.step()

            // Check if we hit RTS and returned to original stack level
            if (opcode == 0x60 && interp.cpu.SP.toInt() == initialSP) {
                break
            }
        }
        return cycles
    }

    /**
     * Jump to a function and run until it returns.
     */
    fun callFunction(interp: BinaryInterpreter6502, address: Int, maxCycles: Int = 100000): Int {
        // Push fake return address (will stop when RTS tries to return past this)
        val returnAddr = 0xFFFF - 1  // -1 because RTS adds 1
        interp.memory.writeByte(0x100 + interp.cpu.SP.toInt(), ((returnAddr shr 8) and 0xFF).toUByte())
        interp.cpu.SP = ((interp.cpu.SP.toInt() - 1) and 0xFF).toUByte()
        interp.memory.writeByte(0x100 + interp.cpu.SP.toInt(), (returnAddr and 0xFF).toUByte())
        interp.cpu.SP = ((interp.cpu.SP.toInt() - 1) and 0xFF).toUByte()

        // Set PC to function
        interp.cpu.PC = address.toUShort()

        // Run until RTS
        return runUntilRTS(interp, maxCycles)
    }

    /**
     * Compare a decompiled function against the binary interpreter.
     *
     * @param functionAddress The address of the function in ROM
     * @param decompiledFunction The decompiled Kotlin function to test
     * @param setupState Optional state setup before running
     * @param memoryToCheck List of memory addresses to compare
     * @param ignoreFlags Flags to ignore in comparison
     * @param ignoreRegisters Registers to ignore in comparison
     */
    fun compareFunction(
        functionAddress: Int,
        decompiledFunction: () -> Unit,
        setupState: ((BinaryInterpreter6502) -> Unit)? = null,
        memoryToCheck: List<Int> = emptyList(),
        ignoreFlags: Set<String> = emptySet(),
        ignoreRegisters: Set<String> = emptySet()
    ): ComparisonResult {
        // Set up binary interpreter
        val interp = BinaryInterpreter6502()
        if (!loadSMBRom(interp)) {
            return ComparisonResult.Skipped("ROM not available")
        }

        // Apply setup
        setupState?.invoke(interp)

        // Capture initial state
        val initialState = State.fromBinaryInterpreter(interp, memoryToCheck)

        // Run in binary interpreter
        val interpCycles = callFunction(interp, functionAddress)
        val interpState = State.fromBinaryInterpreter(interp, memoryToCheck)

        // Reset for decompiled test
        resetCPU()
        clearMemory()

        // Load ROM into decompiled memory
        val romFile = listOf("local/roms/smb.nes", "smb.nes").map { File(it) }.first { it.exists() }
        val rom = NESLoader.load(romFile)
        loadROM(rom.prgRom, 0x8000)

        // by Claude - TODO: This framework needs redesign for local-variable register approach
        // The decompiled functions now take registers as parameters and return values,
        // not via globals. For now, sync global state (flags, memory) and note that
        // register comparison won't work correctly until this framework is updated.
        SP = initialState.sp
        flagN = initialState.n
        flagV = initialState.v
        flagZ = initialState.z
        flagC = initialState.c
        for ((addr, value) in initialState.memory) {
            memory[addr] = value.toUByte()
        }

        // Run decompiled function
        // TODO: Need to pass initialState.a, initialState.x, initialState.y as parameters
        // and capture return values for proper comparison
        try {
            decompiledFunction()
        } catch (e: Exception) {
            return ComparisonResult.Error("Decompiled function threw exception: ${e.message}")
        }

        // Capture decompiled state
        // TODO: Need to capture return values from decompiledFunction, not globals
        val decompiledState = State.fromDecompiledRuntime(
            a = initialState.a,  // Placeholder - should be return value
            x = initialState.x,  // Placeholder - should be return value
            y = initialState.y,  // Placeholder - should be return value
            addresses = memoryToCheck
        )

        // Compare states
        return try {
            interpState.assertEqualsTo(
                decompiledState,
                "Function at $${functionAddress.toString(16)}",
                ignoreFlags,
                ignoreRegisters
            )
            ComparisonResult.Match(interpCycles)
        } catch (e: AssertionError) {
            ComparisonResult.Mismatch(
                message = e.message ?: "Unknown mismatch",
                interpreterState = interpState,
                decompiledState = decompiledState
            )
        }
    }

    sealed class ComparisonResult {
        data class Match(val cycles: Int) : ComparisonResult()
        data class Mismatch(
            val message: String,
            val interpreterState: State,
            val decompiledState: State
        ) : ComparisonResult()
        data class Error(val message: String) : ComparisonResult()
        data class Skipped(val reason: String) : ComparisonResult()
    }
}

/**
 * SMB-specific function addresses from the disassembly.
 */
object SMBFunctions {
    // Core game functions
    const val Start = 0x8000
    const val NonMaskableInterrupt = 0x8004

    // Common utility functions (add more as needed)
    // These addresses should be verified against smbdism.asm
}
