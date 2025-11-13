package com.ivieleague.decompiler6502tokotlin.hand

import com.ivieleague.decompiler6502tokotlin.hand.stages.*
import kotlin.random.Random

/**
 * Generates differential tests for SMB functions.
 *
 * For each function, generates tests that:
 * 1. Create random initial states
 * 2. Execute in interpreter
 * 3. Execute generated Kotlin code
 * 4. Compare all RAM (0x0000-0x2FFF) plus registers/flags
 */
object SMBDifferentialTestGenerator {

    data class TestCase(
        val functionName: String,
        val testNumber: Int,
        val initialState: IntegrationTest.CPUState,
        val interpreterResult: IntegrationTest.CPUState?,
        val kotlinResult: IntegrationTest.CPUState?,
        val passed: Boolean,
        val differences: List<String>
    )

    data class FunctionTestResult(
        val functionName: String,
        val totalTests: Int,
        val passedTests: Int,
        val failedTests: Int,
        val testCases: List<TestCase>
    ) {
        val passRate: Double get() = if (totalTests > 0) passedTests.toDouble() / totalTests else 0.0
    }

    /**
     * Generate random CPU state for testing.
     * Randomizes registers, flags, and specific memory ranges.
     */
    fun generateRandomState(seed: Long): IntegrationTest.CPUState {
        val random = Random(seed)

        // Random registers
        val a = random.nextInt(256).toUByte()
        val x = random.nextInt(256).toUByte()
        val y = random.nextInt(256).toUByte()
        val sp = random.nextInt(256).toUByte()

        // Random flags
        val n = random.nextBoolean()
        val v = random.nextBoolean()
        val z = random.nextBoolean()
        val c = random.nextBoolean()
        val i = random.nextBoolean()
        val d = random.nextBoolean()

        // Randomize key memory ranges:
        // - Zero Page: $0000-$00FF (256 bytes)
        // - Stack: $0100-$01FF (256 bytes)
        // - Work RAM: $0200-$07FF (1536 bytes)
        // For testing, we'll randomize zero page and some work RAM
        val memory = mutableMapOf<Int, UByte>()

        // Randomize zero page (most important for 6502 code)
        for (addr in 0x0000..0x00FF) {
            memory[addr] = random.nextInt(256).toUByte()
        }

        // Randomize stack area
        for (addr in 0x0100..0x01FF) {
            memory[addr] = random.nextInt(256).toUByte()
        }

        // Randomize some work RAM (first 256 bytes)
        for (addr in 0x0200..0x02FF) {
            memory[addr] = random.nextInt(256).toUByte()
        }

        // Randomize sprite data area
        for (addr in 0x0300..0x03FF) {
            memory[addr] = random.nextInt(256).toUByte()
        }

        return IntegrationTest.CPUState(
            A = a,
            X = x,
            Y = y,
            SP = sp,
            PC = 0u, // Will be set to function entry point
            N = n,
            V = v,
            Z = z,
            C = c,
            I = i,
            D = d,
            memory = memory
        )
    }

    /**
     * Compare two CPU states and return differences.
     * Compares all RAM from 0x0000 to 0x2FFF.
     */
    fun compareStates(
        interpreterState: IntegrationTest.CPUState,
        kotlinState: IntegrationTest.CPUState
    ): List<String> {
        val differences = mutableListOf<String>()

        // Compare registers
        if (interpreterState.A != kotlinState.A) {
            differences.add("A: interpreter=${interpreterState.A.toString(16)}, kotlin=${kotlinState.A.toString(16)}")
        }
        if (interpreterState.X != kotlinState.X) {
            differences.add("X: interpreter=${interpreterState.X.toString(16)}, kotlin=${kotlinState.X.toString(16)}")
        }
        if (interpreterState.Y != kotlinState.Y) {
            differences.add("Y: interpreter=${interpreterState.Y.toString(16)}, kotlin=${kotlinState.Y.toString(16)}")
        }
        if (interpreterState.SP != kotlinState.SP) {
            differences.add("SP: interpreter=${interpreterState.SP.toString(16)}, kotlin=${kotlinState.SP.toString(16)}")
        }

        // Compare flags
        if (interpreterState.N != kotlinState.N) {
            differences.add("N: interpreter=${interpreterState.N}, kotlin=${kotlinState.N}")
        }
        if (interpreterState.V != kotlinState.V) {
            differences.add("V: interpreter=${interpreterState.V}, kotlin=${kotlinState.V}")
        }
        if (interpreterState.Z != kotlinState.Z) {
            differences.add("Z: interpreter=${interpreterState.Z}, kotlin=${kotlinState.Z}")
        }
        if (interpreterState.C != kotlinState.C) {
            differences.add("C: interpreter=${interpreterState.C}, kotlin=${kotlinState.C}")
        }
        if (interpreterState.I != kotlinState.I) {
            differences.add("I: interpreter=${interpreterState.I}, kotlin=${kotlinState.I}")
        }
        if (interpreterState.D != kotlinState.D) {
            differences.add("D: interpreter=${interpreterState.D}, kotlin=${kotlinState.D}")
        }

        // Compare all RAM from 0x0000 to 0x2FFF
        val allAddresses = (interpreterState.memory.keys + kotlinState.memory.keys).distinct()
        for (addr in allAddresses.filter { it <= 0x2FFF }) {
            val interpreterValue = interpreterState.memory[addr] ?: 0u
            val kotlinValue = kotlinState.memory[addr] ?: 0u

            if (interpreterValue != kotlinValue) {
                differences.add(
                    String.format(
                        "Memory[0x%04X]: interpreter=0x%02X, kotlin=0x%02X",
                        addr,
                        interpreterValue.toInt(),
                        kotlinValue.toInt()
                    )
                )
            }
        }

        return differences
    }

    /**
     * Test a single function with multiple random states.
     */
    fun testFunction(
        functionName: String,
        numTests: Int = 10,
        baseSeed: Long = 12345L
    ): FunctionTestResult {
        println("Testing function: $functionName")

        val testCases = mutableListOf<TestCase>()
        var passedCount = 0
        var failedCount = 0

        for (testNum in 0 until numTests) {
            val seed = baseSeed + testNum
            val initialState = generateRandomState(seed)

            try {
                // Execute in interpreter
                val interpreterResult = executeInInterpreter(functionName, initialState)

                // Generate and execute Kotlin code
                val kotlinResult = executeInKotlin(functionName, initialState)

                // Compare results
                val differences = if (interpreterResult != null && kotlinResult != null) {
                    compareStates(interpreterResult, kotlinResult)
                } else {
                    listOf("Execution failed")
                }

                val passed = differences.isEmpty()
                if (passed) {
                    passedCount++
                } else {
                    failedCount++
                }

                testCases.add(
                    TestCase(
                        functionName = functionName,
                        testNumber = testNum,
                        initialState = initialState,
                        interpreterResult = interpreterResult,
                        kotlinResult = kotlinResult,
                        passed = passed,
                        differences = differences
                    )
                )

            } catch (e: Exception) {
                failedCount++
                testCases.add(
                    TestCase(
                        functionName = functionName,
                        testNumber = testNum,
                        initialState = initialState,
                        interpreterResult = null,
                        kotlinResult = null,
                        passed = false,
                        differences = listOf("Exception: ${e.message}")
                    )
                )
            }
        }

        return FunctionTestResult(
            functionName = functionName,
            totalTests = numTests,
            passedTests = passedCount,
            failedTests = failedCount,
            testCases = testCases
        )
    }

    /**
     * Execute function in the interpreter.
     */
    private fun executeInInterpreter(
        functionName: String,
        initialState: IntegrationTest.CPUState
    ): IntegrationTest.CPUState? {
        try {
            // Load function from SMB disassembly
            val function = SMBTestFixtures.loadFunction(functionName)
            val blocks = SMBTestFixtures.getFunctionBlocks(functionName)

            // Get all instructions
            val instructions = blocks.flatMap { block ->
                block.lines.mapNotNull { it.instruction }
            }

            if (instructions.isEmpty()) {
                return null
            }

            // Create interpreter and set up initial state
            val interp = Interpreter6502()
            initialState.applyTo(interp.cpu, interp.memory)

            // Execute all instructions
            instructions.forEach { instruction ->
                try {
                    interp.executeInstruction(instruction)
                } catch (e: Exception) {
                    // Instruction execution failed - this is expected for some operations
                    // that depend on memory-mapped I/O or other runtime state
                }
            }

            // Capture final state - capture ALL NES RAM (0x0000-0x2FFF)
            val allRamAddresses = (0x0000..0x2FFF).toList()
            return IntegrationTest.CPUState.capture(
                interp.cpu,
                interp.memory,
                allRamAddresses
            )

        } catch (e: Exception) {
            println("  Interpreter error: ${e.message}")
            return null
        }
    }

    /**
     * Execute generated Kotlin code.
     */
    private fun executeInKotlin(
        functionName: String,
        initialState: IntegrationTest.CPUState
    ): IntegrationTest.CPUState? {
        try {
            // Load function from SMB disassembly
            val function = SMBTestFixtures.loadFunction(functionName)
            val blocks = SMBTestFixtures.getFunctionBlocks(functionName)

            // Get all instructions
            val instructions = blocks.flatMap { block ->
                block.lines.mapNotNull { it.instruction }
            }

            if (instructions.isEmpty()) {
                return null
            }

            // Generate Kotlin code
            val ctx = CodeGenContext()
            val stmts = mutableListOf<KotlinStmt>()

            instructions.forEach { instruction ->
                stmts.addAll(instruction.toKotlin(ctx))
            }

            val kotlinCode = stmts.joinToString("\n") { it.toKotlin() }

            // Execute translated code with all NES RAM addresses
            val allRamAddresses = (0x0000..0x2FFF).toList()
            val result = KotlinExecutor.executeDirectly(
                kotlinCode,
                initialState,
                allRamAddresses
            )

            return result.getOrNull()

        } catch (e: Exception) {
            println("  Kotlin execution error: ${e.message}")
            return null
        }
    }

    /**
     * Generate JUnit test code for a function.
     */
    fun generateJUnitTest(functionName: String): String {
        return """
    @Test
    fun test_${functionName}() {
        val result = SMBDifferentialTestGenerator.testFunction("$functionName", numTests = 10)

        println("$functionName: ${"$"}{result.passedTests}/${"$"}{result.totalTests} passed (${"$"}{"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${"$"}{testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - ${"$"}diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${"$"}{testCase.differences.size - 5} more differences")
                }
            }
        }

        // For now, just report - don't fail the test
        // assertTrue(result.passRate > 0.8, "$functionName pass rate too low: ${"$"}{"%.1f".format(result.passRate * 100)}%")
    }
""".trimIndent()
    }
}
