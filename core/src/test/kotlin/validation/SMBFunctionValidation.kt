package com.ivieleague.decompiler6502tokotlin.validation

import com.ivieleague.decompiler6502tokotlin.hand.*
import com.ivieleague.decompiler6502tokotlin.interpreter.CPU6502
import com.ivieleague.decompiler6502tokotlin.interpreter.Interpreter6502
import com.ivieleague.decompiler6502tokotlin.interpreter.Memory6502
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Validation framework for comparing decompiled Kotlin functions against original 6502 assembly.
 *
 * This validates correctness by:
 * 1. Executing original assembly through the 6502 interpreter
 * 2. Executing decompiled Kotlin code with same initial state
 * 3. Comparing final memory, registers, and flags
 */
class SMBFunctionValidation {

    /**
     * Test case data structure
     */
    data class ValidationCase(
        val name: String,
        val assemblyCode: String,
        val initialState: MachineState,
        val expectedState: MachineState? = null,  // If null, will be captured from assembly execution
        val kotlinFunction: (MachineState) -> MachineState
    )

    /**
     * Machine state snapshot for comparison
     */
    data class MachineState(
        var A: UByte = 0u,
        var X: UByte = 0u,
        var Y: UByte = 0u,
        var SP: UByte = 0xFFu,
        var flagZ: Boolean = false,
        var flagN: Boolean = false,
        var flagC: Boolean = false,
        var flagV: Boolean = false,
        val memory: MutableMap<Int, UByte> = mutableMapOf()
    ) {
        fun copyFrom(cpu: CPU6502, mem: Memory6502, addresses: List<Int> = emptyList()) {
            A = cpu.A
            X = cpu.X
            Y = cpu.Y
            SP = cpu.SP
            flagZ = cpu.Z
            flagN = cpu.N
            flagC = cpu.C
            flagV = cpu.V
            addresses.forEach { addr ->
                memory[addr] = mem.readByte(addr)
            }
        }

        fun copyTo(cpu: CPU6502, mem: Memory6502) {
            cpu.A = A
            cpu.X = X
            cpu.Y = Y
            cpu.SP = SP
            cpu.Z = flagZ
            cpu.N = flagN
            cpu.C = flagC
            cpu.V = flagV
            memory.forEach { (addr, value) ->
                mem.writeByte(addr, value)
            }
        }

        override fun toString(): String = buildString {
            appendLine("Registers: A=$${A.toString(16).uppercase().padStart(2, '0')} " +
                    "X=$${X.toString(16).uppercase().padStart(2, '0')} " +
                    "Y=$${Y.toString(16).uppercase().padStart(2, '0')} " +
                    "SP=$${SP.toString(16).uppercase().padStart(2, '0')}")
            appendLine("Flags: Z=$flagZ N=$flagN C=$flagC V=$flagV")
            if (memory.isNotEmpty()) {
                appendLine("Memory:")
                memory.toSortedMap().forEach { (addr, value) ->
                    appendLine("  $${addr.toString(16).uppercase().padStart(4, '0')}: " +
                            "$${value.toString(16).uppercase().padStart(2, '0')}")
                }
            }
        }
    }

    /**
     * Execute assembly code through interpreter and capture final state
     */
    private fun executeAssembly(
        assemblyCode: String,
        initialState: MachineState,
        watchAddresses: List<Int> = emptyList()
    ): MachineState {
        val interp = Interpreter6502()

        // Set initial state
        initialState.copyTo(interp.cpu, interp.memory)

        // Parse and execute assembly
        val parsed = assemblyCode.parseToAssemblyCodeFile()
        for (line in parsed.lines) {
            line.instruction?.let { instruction ->
                interp.executeInstruction(instruction)
                // Stop at RTS
                if (instruction.op == AssemblyOp.RTS) break
            }
        }

        // Capture final state
        val finalState = MachineState()
        finalState.copyFrom(interp.cpu, interp.memory, watchAddresses)
        return finalState
    }

    /**
     * Run validation test
     */
    private fun validate(case: ValidationCase, watchAddresses: List<Int> = emptyList()) {
        println("\n=== Validating: ${case.name} ===")

        // Execute assembly
        val assemblyResult = executeAssembly(case.assemblyCode, case.initialState, watchAddresses)
        println("\nAssembly execution result:")
        println(assemblyResult)

        // Execute Kotlin function
        val kotlinState = case.initialState.copy(memory = case.initialState.memory.toMutableMap())
        val kotlinResult = case.kotlinFunction(kotlinState)
        println("\nKotlin execution result:")
        println(kotlinResult)

        // Compare results
        val expected = case.expectedState ?: assemblyResult

        assertEquals(expected.A, kotlinResult.A, "Register A mismatch")
        assertEquals(expected.X, kotlinResult.X, "Register X mismatch")
        assertEquals(expected.Y, kotlinResult.Y, "Register Y mismatch")
        assertEquals(expected.flagZ, kotlinResult.flagZ, "Zero flag mismatch")
        assertEquals(expected.flagN, kotlinResult.flagN, "Negative flag mismatch")
        assertEquals(expected.flagC, kotlinResult.flagC, "Carry flag mismatch")
        assertEquals(expected.flagV, kotlinResult.flagV, "Overflow flag mismatch")

        // Compare memory
        watchAddresses.forEach { addr ->
            assertEquals(
                expected.memory[addr] ?: 0u,
                kotlinResult.memory[addr] ?: 0u,
                "Memory mismatch at $${addr.toString(16).uppercase()}"
            )
        }

        println("\n✓ Validation PASSED!")
    }

    /**
     * Example: Simple function that loads A with a constant
     */
    @Test
    fun testSimpleLoad() {
        val case = ValidationCase(
            name = "Simple LDA #$42",
            assemblyCode = """
                TestFunc:
                    LDA #${'$'}42
                    RTS
            """.trimIndent(),
            initialState = MachineState(A = 0u),
            kotlinFunction = { state ->
                state.A = 0x42u
                state.flagZ = false
                state.flagN = false
                state
            }
        )

        validate(case)
    }

    /**
     * Example: Function that adds two numbers
     */
    @Test
    fun testSimpleAdd() {
        val case = ValidationCase(
            name = "Add A + X",
            assemblyCode = """
                AddFunc:
                    TXA
                    CLC
                    ADC #${'$'}05
                    RTS
            """.trimIndent(),
            initialState = MachineState(A = 0u, X = 0x10u),
            kotlinFunction = { state ->
                state.A = state.X
                state.flagC = false
                val result = state.A.toInt() + 0x05
                state.A = (result and 0xFF).toUByte()
                state.flagZ = (state.A == 0u.toUByte())
                state.flagN = (state.A.toInt() and 0x80) != 0
                state.flagC = result > 0xFF
                state
            }
        )

        validate(case)
    }

    /**
     * Example: Function that writes to memory
     */
    @Test
    fun testMemoryWrite() {
        val case = ValidationCase(
            name = "Store to zero page",
            assemblyCode = """
                StoreFunc:
                    LDA #${'$'}FF
                    STA ${'$'}00
                    RTS
            """.trimIndent(),
            initialState = MachineState(),
            kotlinFunction = { state ->
                state.A = 0xFFu
                state.flagZ = false
                state.flagN = true
                state.memory[0x00] = 0xFFu
                state
            }
        )

        validate(case, watchAddresses = listOf(0x00))
    }

    /**
     * REAL SMB FUNCTION: ResetScreenTimer
     *
     * Original assembly:
     * ```
     * ResetScreenTimer:
     *     LDA #$07
     *     STA ScreenTimer       ; $07A0
     *     INC ScreenRoutineTask ; $073C
     *     RTS
     * ```
     */
    @Test
    fun testResetScreenTimer() {
        val SCREENTIMER = 0x07A0
        val SCREENROUTINETASK = 0x073C

        val case = ValidationCase(
            name = "SMB ResetScreenTimer",
            assemblyCode = """
                ResetScreenTimer:
                    LDA #${'$'}07
                    STA ${'$'}07A0
                    INC ${'$'}073C
                    RTS
            """.trimIndent(),
            initialState = MachineState(
                memory = mutableMapOf(
                    SCREENROUTINETASK to 0x00u
                )
            ),
            kotlinFunction = { state ->
                // Decompiled Kotlin code
                state.memory[SCREENTIMER] = ((0x07) and 0xFF).toUByte()
                state.memory[SCREENROUTINETASK] = (((state.memory[SCREENROUTINETASK]?.toInt() ?: 0) + 1) and 0xFF).toUByte()
                state.A = 0x07u
                state.flagZ = false
                state.flagN = false
                state
            }
        )

        validate(case, watchAddresses = listOf(SCREENTIMER, SCREENROUTINETASK))
    }
}
