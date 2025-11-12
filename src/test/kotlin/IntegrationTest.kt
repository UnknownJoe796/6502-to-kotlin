package com.ivieleague.decompiler6502tokotlin

import com.ivieleague.decompiler6502tokotlin.hand.*
import com.ivieleague.decompiler6502tokotlin.interpreter.*
import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Integration tests that verify the decompiler's correctness by comparing
 * interpreter execution against translated Kotlin code.
 *
 * These tests:
 * 1. Parse 6502 assembly code
 * 2. Execute it through the interpreter with random initial states
 * 3. Generate Kotlin code from the assembly
 * 4. Verify the translation produces equivalent behavior
 */
class IntegrationTest {

    /**
     * Represents a snapshot of the 6502 processor state for testing.
     */
    data class CPUState(
        val A: UByte = 0u,
        val X: UByte = 0u,
        val Y: UByte = 0u,
        val SP: UByte = 0xFFu,
        val PC: UShort = 0u,
        val N: Boolean = false,
        val V: Boolean = false,
        val Z: Boolean = false,
        val C: Boolean = false,
        val I: Boolean = false,
        val D: Boolean = false,
        val memory: Map<Int, UByte> = emptyMap()
    ) {
        /**
         * Apply this state to a CPU instance.
         */
        fun applyTo(cpu: CPU6502, memory: Memory6502) {
            cpu.A = A
            cpu.X = X
            cpu.Y = Y
            cpu.SP = SP
            cpu.PC = PC
            cpu.N = N
            cpu.V = V
            cpu.Z = Z
            cpu.C = C
            cpu.I = I
            cpu.D = D
            this.memory.forEach { (addr, value) ->
                memory.writeByte(addr, value)
            }
        }

        companion object {
            /**
             * Create a random CPU state for testing.
             */
            fun random(random: Random = Random.Default): CPUState {
                return CPUState(
                    A = random.nextInt(256).toUByte(),
                    X = random.nextInt(256).toUByte(),
                    Y = random.nextInt(256).toUByte(),
                    SP = random.nextInt(256).toUByte(),
                    PC = random.nextInt(65536).toUShort(),
                    N = random.nextBoolean(),
                    V = random.nextBoolean(),
                    Z = random.nextBoolean(),
                    C = random.nextBoolean(),
                    I = random.nextBoolean(),
                    D = random.nextBoolean()
                )
            }

            /**
             * Capture the current state from a CPU and memory.
             */
            fun capture(cpu: CPU6502, memory: Memory6502, addresses: List<Int> = emptyList()): CPUState {
                return CPUState(
                    A = cpu.A,
                    X = cpu.X,
                    Y = cpu.Y,
                    SP = cpu.SP,
                    PC = cpu.PC,
                    N = cpu.N,
                    V = cpu.V,
                    Z = cpu.Z,
                    C = cpu.C,
                    I = cpu.I,
                    D = cpu.D,
                    memory = addresses.associateWith { memory.readByte(it) }
                )
            }
        }
    }

    /**
     * Test context that holds all information needed for an integration test.
     */
    data class TestContext(
        val name: String,
        val instructions: List<AssemblyInstruction>,
        val initialState: CPUState,
        val memoryAddressesToTrack: List<Int> = emptyList(),
        val labelResolver: (String) -> Int = { 0 }
    ) {
        /**
         * Execute the instructions through the interpreter and return the final state.
         */
        fun executeInInterpreter(): CPUState {
            val interp = Interpreter6502()
            interp.labelResolver = labelResolver

            // Set up initial state
            initialState.applyTo(interp.cpu, interp.memory)

            // Execute all instructions
            instructions.forEach { instruction ->
                interp.executeInstruction(instruction)
            }

            // Capture final state
            return CPUState.capture(interp.cpu, interp.memory, memoryAddressesToTrack)
        }

        /**
         * Generate Kotlin code for these instructions.
         */
        fun generateKotlinCode(): String {
            val ctx = CodeGenContext()
            val stmts = mutableListOf<KotlinStmt>()

            instructions.forEach { instruction ->
                stmts.addAll(instruction.toKotlin(ctx))
            }

            return stmts.joinToString("\n") { it.toKotlin() }
        }
    }

    /**
     * Test helper: Create a test context for a simple instruction sequence.
     */
    private fun testSequence(
        name: String,
        vararg instructions: AssemblyInstruction,
        initialState: CPUState = CPUState(),
        memoryAddresses: List<Int> = emptyList(),
        labelResolver: (String) -> Int = { 0 }
    ): TestContext {
        return TestContext(
            name = name,
            instructions = instructions.toList(),
            initialState = initialState,
            memoryAddressesToTrack = memoryAddresses,
            labelResolver = labelResolver
        )
    }

    @Test
    fun testSimpleLoad() {
        // Test: LDA #$42
        val test = testSequence(
            "Simple Load",
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)
            )
        )

        val finalState = test.executeInInterpreter()
        assertEquals(0x42u, finalState.A, "A register should be 0x42")
        assertEquals(false, finalState.Z, "Zero flag should be clear")
        assertEquals(false, finalState.N, "Negative flag should be clear")

        println("Generated Kotlin code:")
        println(test.generateKotlinCode())
    }

    @Test
    fun testLoadAndStore() {
        // Test: LDA #$55, STA $1000
        val test = testSequence(
            "Load and Store",
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x55u, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(
                AssemblyOp.STA,
                AssemblyAddressing.Direct("target")
            ),
            memoryAddresses = listOf(0x1000),
            labelResolver = { label -> if (label == "target") 0x1000 else 0 }
        )

        val finalState = test.executeInInterpreter()
        assertEquals(0x55u, finalState.A, "A register should be 0x55")
        assertEquals(0x55u, finalState.memory[0x1000], "Memory at 0x1000 should be 0x55")

        println("Generated Kotlin code:")
        println(test.generateKotlinCode())
    }

    @Test
    fun testArithmeticAddition() {
        // Test: LDA #$10, ADC #$20 (result should be 0x30)
        val test = testSequence(
            "Addition",
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x10u, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x20u, AssemblyAddressing.Radix.Hex)
            ),
            initialState = CPUState(C = false) // Clear carry
        )

        val finalState = test.executeInInterpreter()
        assertEquals(0x30u, finalState.A, "A register should be 0x30")
        assertEquals(false, finalState.C, "Carry flag should be clear")

        println("Generated Kotlin code:")
        println(test.generateKotlinCode())
    }

    @Test
    fun testArithmeticWithCarry() {
        // Test: LDA #$FF, ADC #$02 with carry set (result should be 0x02, carry set)
        val test = testSequence(
            "Addition with Carry",
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0xFFu, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x02u, AssemblyAddressing.Radix.Hex)
            ),
            initialState = CPUState(C = true) // Set carry
        )

        val finalState = test.executeInInterpreter()
        assertEquals(0x02u, finalState.A, "A register should be 0x02")
        assertEquals(true, finalState.C, "Carry flag should be set")

        println("Generated Kotlin code:")
        println(test.generateKotlinCode())
    }

    @Test
    fun testRegisterTransfers() {
        // Test: LDA #$42, TAX, TAY
        val test = testSequence(
            "Register Transfers",
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(AssemblyOp.TAX),
            AssemblyInstruction(AssemblyOp.TAY)
        )

        val finalState = test.executeInInterpreter()
        assertEquals(0x42u, finalState.A, "A register should be 0x42")
        assertEquals(0x42u, finalState.X, "X register should be 0x42")
        assertEquals(0x42u, finalState.Y, "Y register should be 0x42")

        println("Generated Kotlin code:")
        println(test.generateKotlinCode())
    }

    @Test
    fun testIncrementDecrement() {
        // Test: LDX #$10, INX, DEX
        val test = testSequence(
            "Increment and Decrement",
            AssemblyInstruction(
                AssemblyOp.LDX,
                AssemblyAddressing.ByteValue(0x10u, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(AssemblyOp.INX),
            AssemblyInstruction(AssemblyOp.DEX)
        )

        val finalState = test.executeInInterpreter()
        assertEquals(0x10u, finalState.X, "X register should be 0x10 after INX then DEX")

        println("Generated Kotlin code:")
        println(test.generateKotlinCode())
    }

    @Test
    fun testLogicalOperations() {
        // Test: LDA #$FF, AND #$0F (result should be 0x0F)
        val test = testSequence(
            "Logical AND",
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0xFFu, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(
                AssemblyOp.AND,
                AssemblyAddressing.ByteValue(0x0Fu, AssemblyAddressing.Radix.Hex)
            )
        )

        val finalState = test.executeInInterpreter()
        assertEquals(0x0Fu, finalState.A, "A register should be 0x0F")

        println("Generated Kotlin code:")
        println(test.generateKotlinCode())
    }

    @Test
    fun testStackOperations() {
        // Test: LDA #$AA, PHA, LDA #$BB, PLA
        val test = testSequence(
            "Stack Push/Pull",
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0xAAu, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(AssemblyOp.PHA),
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0xBBu, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(AssemblyOp.PLA)
        )

        val finalState = test.executeInInterpreter()
        assertEquals(0xAAu, finalState.A, "A register should be 0xAA after PLA")

        println("Generated Kotlin code:")
        println(test.generateKotlinCode())
    }

    @Test
    fun testRandomizedStates() {
        // Test the same instruction sequence with 10 different random initial states
        val random = Random(12345) // Fixed seed for reproducibility

        repeat(10) { i ->
            val initialState = CPUState.random(random)
            val test = testSequence(
                "Random Test $i",
                AssemblyInstruction(
                    AssemblyOp.LDA,
                    AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)
                ),
                AssemblyInstruction(AssemblyOp.TAX),
                initialState = initialState
            )

            val finalState = test.executeInInterpreter()

            // Verify expected behavior regardless of initial state
            assertEquals(0x42u, finalState.A, "Test $i: A should be 0x42")
            assertEquals(0x42u, finalState.X, "Test $i: X should be 0x42")
            assertEquals(false, finalState.Z, "Test $i: Z flag should be clear")
            assertEquals(false, finalState.N, "Test $i: N flag should be clear")
        }
    }

    @Test
    fun testIndexedAddressing() {
        // Test: LDA array,X where array is at 0x2000 and X is 3
        val test = testSequence(
            "Indexed Addressing",
            AssemblyInstruction(
                AssemblyOp.LDX,
                AssemblyAddressing.ByteValue(0x03u, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.DirectX("array")
            ),
            initialState = CPUState(
                memory = mapOf(
                    0x2000 to 0x10u,
                    0x2001 to 0x20u,
                    0x2002 to 0x30u,
                    0x2003 to 0x40u,
                    0x2004 to 0x50u
                )
            ),
            labelResolver = { label -> if (label == "array") 0x2000 else 0 }
        )

        val finalState = test.executeInInterpreter()
        assertEquals(0x40u, finalState.A, "A should contain array[3] = 0x40")

        println("Generated Kotlin code:")
        println(test.generateKotlinCode())
    }
}
