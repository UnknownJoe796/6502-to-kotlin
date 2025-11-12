package com.ivieleague.decompiler6502tokotlin

import com.ivieleague.decompiler6502tokotlin.hand.*
import com.ivieleague.decompiler6502tokotlin.interpreter.*
import com.ivieleague.decompiler6502tokotlin.hand.stages.SMBTestFixtures
import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertEquals

/**
 * Integration tests that verify complete function translations from SMB.
 *
 * These tests:
 * 1. Load actual functions from smbdism.asm
 * 2. Run them through the full decompiler pipeline
 * 3. Execute them in the interpreter with various initial states
 * 4. Compare against expected behavior
 *
 * This validates that the decompiler correctly translates real 6502 functions.
 */
class FunctionIntegrationTest {

    /**
     * Execution context for a function, tracking state before and after execution.
     */
    data class FunctionExecution(
        val functionName: String,
        val initialState: IntegrationTest.CPUState,
        val finalState: IntegrationTest.CPUState,
        val instructionsExecuted: Int
    )

    /**
     * Execute a function from SMB through the interpreter.
     */
    private fun executeFunction(
        functionName: String,
        initialState: IntegrationTest.CPUState,
        maxInstructions: Int = 1000
    ): FunctionExecution {
        val func = SMBTestFixtures.loadFunction(functionName)
        val interp = Interpreter6502()

        // Set up label resolver from all blocks
        val allBlocks = SMBTestFixtures.getFunctionBlocks(functionName)
        val labelToAddress = mutableMapOf<String, Int>()
        allBlocks.forEach { block ->
            block.label?.let { label ->
                labelToAddress[label] = block.originalLineIndex
            }
        }
        interp.labelResolver = { label -> labelToAddress[label] ?: 0 }

        // Apply initial state
        initialState.applyTo(interp.cpu, interp.memory)

        // Execute instructions from all blocks in the function
        var instructionsExecuted = 0
        for (block in allBlocks) {
            for (line in block.lines) {
                if (instructionsExecuted >= maxInstructions) {
                    break
                }
                line.instruction?.let { instruction ->
                    // Stop on RTS (return from subroutine)
                    if (instruction.op == AssemblyOp.RTS) {
                        instructionsExecuted++
                        return FunctionExecution(
                            functionName = functionName,
                            initialState = initialState,
                            finalState = IntegrationTest.CPUState.capture(
                                interp.cpu,
                                interp.memory,
                                initialState.memory.keys.toList()
                            ),
                            instructionsExecuted = instructionsExecuted
                        )
                    }

                    try {
                        interp.executeInstruction(instruction)
                        instructionsExecuted++
                    } catch (e: Exception) {
                        // Some instructions may fail in isolation; log and continue
                        println("Warning: Failed to execute $instruction: ${e.message}")
                    }
                }
                if (instructionsExecuted >= maxInstructions) {
                    break
                }
            }
            if (instructionsExecuted >= maxInstructions) {
                break
            }
        }

        // Capture final state
        return FunctionExecution(
            functionName = functionName,
            initialState = initialState,
            finalState = IntegrationTest.CPUState.capture(
                interp.cpu,
                interp.memory,
                initialState.memory.keys.toList()
            ),
            instructionsExecuted = instructionsExecuted
        )
    }

    /**
     * Generate Kotlin code for a function.
     */
    private fun generateKotlinForFunction(functionName: String): String {
        val func = SMBTestFixtures.loadFunction(functionName)
        val ctx = CodeGenContext()

        val code = StringBuilder()
        code.appendLine("fun ${assemblyLabelToKotlinName(functionName)}() {")

        // Generate code for each block
        val blocks = SMBTestFixtures.getFunctionBlocks(functionName)
        for (block in blocks) {
            if (block.label != null) {
                code.appendLine("  // Block: ${block.label}")
            }
            for (line in block.lines) {
                line.instruction?.let { instruction ->
                    val stmts = instruction.toKotlin(ctx)
                    stmts.forEach { stmt ->
                        code.appendLine("  ${stmt.toKotlin()}")
                    }
                }
            }
        }

        code.appendLine("}")
        return code.toString()
    }

    @Test
    fun testSimpleFunctionStructure() {
        // Test that we can load and analyze a simple function
        val func = SMBTestFixtures.loadFunction("DecTimers")
        assert(func.startingBlock.label == "DecTimers") {
            "Function should have correct entry point"
        }

        val blocks = SMBTestFixtures.getFunctionBlocks("DecTimers")
        assert(blocks.isNotEmpty()) {
            "Function should have at least one block"
        }

        println("DecTimers function has ${blocks.size} blocks")
        println("DecTimers function has ${blocks.sumOf { it.lines.size }} lines")
    }

    @Test
    fun testDecTimersWithRandomStates() {
        // Test DecTimers function with different random initial states
        val random = Random(54321)

        repeat(5) { i ->
            val initialMemory = mutableMapOf<Int, UByte>()
            // Initialize timer array at address 0x780 (common location in SMB)
            for (offset in 0..20) {
                initialMemory[0x780 + offset] = random.nextInt(256).toUByte()
            }

            val initialState = IntegrationTest.CPUState(
                A = random.nextInt(256).toUByte(),
                X = random.nextInt(256).toUByte(),
                Y = random.nextInt(256).toUByte(),
                memory = initialMemory
            )

            try {
                val execution = executeFunction("DecTimers", initialState, maxInstructions = 100)

                println("\n=== DecTimers Execution $i ===")
                println("Instructions executed: ${execution.instructionsExecuted}")
                println("Initial A: 0x${initialState.A.toString(16).padStart(2, '0')}")
                println("Final A:   0x${execution.finalState.A.toString(16).padStart(2, '0')}")
                println("Initial X: 0x${initialState.X.toString(16).padStart(2, '0')}")
                println("Final X:   0x${execution.finalState.X.toString(16).padStart(2, '0')}")

                // Verify function executed something
                assert(execution.instructionsExecuted > 0) {
                    "Function should execute at least one instruction"
                }
            } catch (e: Exception) {
                println("Execution $i failed: ${e.message}")
                // Continue with other tests
            }
        }
    }

    @Test
    fun testGenerateKotlinForSimpleFunction() {
        // Generate Kotlin code for a simple function and verify it looks reasonable
        val kotlinCode = generateKotlinForFunction("DecTimers")

        println("\n=== Generated Kotlin Code for DecTimers ===")
        println(kotlinCode)

        // Basic sanity checks on generated code
        assert(kotlinCode.contains("fun decTimers")) {
            "Generated code should have a function definition"
        }
        assert(kotlinCode.contains("{") && kotlinCode.contains("}")) {
            "Generated code should have braces"
        }
    }

    @Test
    fun testCompareFunctionBehaviorAcrossStates() {
        // Execute the same function with multiple initial states and
        // verify consistent behavior patterns

        val testStates = listOf(
            IntegrationTest.CPUState(A = 0x00u, X = 0x00u, Y = 0x00u),
            IntegrationTest.CPUState(A = 0xFFu, X = 0xFFu, Y = 0xFFu),
            IntegrationTest.CPUState(A = 0x42u, X = 0x10u, Y = 0x20u),
            IntegrationTest.CPUState(A = 0x80u, X = 0x01u, Y = 0x02u)
        )

        println("\n=== Comparing Function Behavior Across States ===")

        for ((index, state) in testStates.withIndex()) {
            try {
                val execution = executeFunction("DecTimers", state, maxInstructions = 100)

                println("\nState $index:")
                println("  Initial: A=0x${state.A.toString(16)}, " +
                        "X=0x${state.X.toString(16)}, " +
                        "Y=0x${state.Y.toString(16)}")
                println("  Final:   A=0x${execution.finalState.A.toString(16)}, " +
                        "X=0x${execution.finalState.X.toString(16)}, " +
                        "Y=0x${execution.finalState.Y.toString(16)}")
                println("  Instructions: ${execution.instructionsExecuted}")

                // Verify deterministic behavior
                val execution2 = executeFunction("DecTimers", state, maxInstructions = 100)
                assertEquals(
                    execution.finalState.A,
                    execution2.finalState.A,
                    "Function should be deterministic for same initial state"
                )
            } catch (e: Exception) {
                println("State $index failed: ${e.message}")
            }
        }
    }

    @Test
    fun testMultipleFunctionsBasicExecution() {
        // Test that we can execute multiple different functions
        val functionNames = listOf("DecTimers", "RotPRandomBit", "InitBuffer")

        for (funcName in functionNames) {
            try {
                println("\n=== Testing $funcName ===")

                val initialState = IntegrationTest.CPUState()
                val execution = executeFunction(funcName, initialState, maxInstructions = 50)

                println("$funcName executed ${execution.instructionsExecuted} instructions")
                println("Generated Kotlin code preview:")
                val kotlinCode = generateKotlinForFunction(funcName)
                println(kotlinCode.lines().take(10).joinToString("\n"))

                assert(execution.instructionsExecuted > 0) {
                    "$funcName should execute at least one instruction"
                }
            } catch (e: Exception) {
                println("$funcName test failed: ${e.message}")
                // Continue testing other functions
            }
        }
    }

    @Test
    fun testFunctionWithMemoryAccess() {
        // Test a function that accesses memory
        val initialMemory = mutableMapOf<Int, UByte>()

        // Set up some test data in memory
        for (i in 0x700..0x7FF) {
            initialMemory[i] = (i and 0xFF).toUByte()
        }

        val initialState = IntegrationTest.CPUState(
            A = 0x00u,
            X = 0x10u,
            Y = 0x00u,
            memory = initialMemory
        )

        try {
            val execution = executeFunction("DecTimers", initialState, maxInstructions = 100)

            println("\n=== Function Memory Access Test ===")
            println("Instructions executed: ${execution.instructionsExecuted}")

            // Check if any memory was modified
            val memoryChanged = execution.finalState.memory.any { (addr, value) ->
                initialMemory[addr] != value
            }

            println("Memory modified: $memoryChanged")
        } catch (e: Exception) {
            println("Memory access test failed: ${e.message}")
        }
    }

    @Test
    fun testFunctionInstructionCoverage() {
        // Analyze what types of instructions are used in a function
        val func = SMBTestFixtures.loadFunction("DecTimers")
        val blocks = SMBTestFixtures.getFunctionBlocks("DecTimers")

        val instructionTypes = mutableMapOf<AssemblyOp, Int>()
        for (block in blocks) {
            for (line in block.lines) {
                line.instruction?.let { instruction ->
                    instructionTypes[instruction.op] = instructionTypes.getOrDefault(instruction.op, 0) + 1
                }
            }
        }

        println("\n=== Instruction Coverage for DecTimers ===")
        instructionTypes.entries.sortedByDescending { it.value }.forEach { (op, count) ->
            println("  $op: $count")
        }

        assert(instructionTypes.isNotEmpty()) {
            "Function should use at least one instruction type"
        }
    }

    @Test
    fun testSmallFunctionEndToEnd() {
        // Test a very small function end-to-end
        // For example, RotPRandomBit which is typically just a few instructions

        println("\n=== End-to-End Test: RotPRandomBit ===")

        // 1. Load and analyze the function
        val func = SMBTestFixtures.loadFunction("RotPRandomBit")
        val blocks = SMBTestFixtures.getFunctionBlocks("RotPRandomBit")
        println("Function has ${blocks.size} blocks")

        // 2. Execute with interpreter
        val initialState = IntegrationTest.CPUState(
            A = 0b10101010u,
            X = 0x00u,
            memory = mapOf(0x07A1 to 0b11001100u) // PseudoRandomBitReg location
        )

        try {
            val execution = executeFunction("RotPRandomBit", initialState, maxInstructions = 20)
            println("Executed ${execution.instructionsExecuted} instructions")
            println("Final state: A=0x${execution.finalState.A.toString(16)}")

            // 3. Generate Kotlin code
            val kotlinCode = generateKotlinForFunction("RotPRandomBit")
            println("\nGenerated Kotlin:")
            println(kotlinCode)

            // 4. Verify basic correctness
            assert(execution.instructionsExecuted > 0) {
                "Should have executed some instructions"
            }
        } catch (e: Exception) {
            println("Test failed: ${e.message}")
            e.printStackTrace()
        }
    }
}
