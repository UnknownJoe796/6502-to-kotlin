package com.ivieleague.decompiler6502tokotlin.hand

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Quick validation test to verify the differential testing infrastructure works
 * before running all 59 SMB function tests.
 *
 * Tests a few simple functions to make sure:
 * 1. Functions can be loaded from SMB disassembly
 * 2. Kotlin code can be generated
 * 3. Interpreter execution works
 * 4. Comparison logic works
 */
class QuickDifferentialValidationTest {

    @Test
    fun testInfrastructure_DoNothing2() {
        println("=" .repeat(60))
        println("Testing infrastructure with DoNothing2 (simplest function)")
        println("=" .repeat(60))

        val result = SMBDifferentialTestGenerator.testFunction(
            functionName = "DoNothing2",
            numTests = 3  // Just 3 tests for quick validation
        )

        println("Result: ${result.passedTests}/${result.totalTests} passed")

        if (result.failedTests > 0) {
            println("\nFailed test details:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("  Test #${testCase.testNumber}:")
                testCase.differences.forEach { diff ->
                    println("    - $diff")
                }
            }
        }

        println("=" .repeat(60))

        // For now, just report - we expect some failures
        assertTrue(result.totalTests > 0, "Should have executed tests")
    }

    @Test
    fun testInfrastructure_ReadJoypads() {
        println("=" .repeat(60))
        println("Testing infrastructure with ReadJoypads")
        println("=" .repeat(60))

        val result = SMBDifferentialTestGenerator.testFunction(
            functionName = "ReadJoypads",
            numTests = 3
        )

        println("Result: ${result.passedTests}/${result.totalTests} passed")

        if (result.failedTests > 0) {
            println("\nFailed test details:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("  Test #${testCase.testNumber}:")
                testCase.differences.forEach { diff ->
                    println("    - $diff")
                }
            }
        }

        println("=" .repeat(60))

        assertTrue(result.totalTests > 0, "Should have executed tests")
    }

    @Test
    fun testInfrastructure_WritePPUReg1() {
        println("=" .repeat(60))
        println("Testing infrastructure with WritePPUReg1")
        println("=" .repeat(60))

        val result = SMBDifferentialTestGenerator.testFunction(
            functionName = "WritePPUReg1",
            numTests = 3
        )

        println("Result: ${result.passedTests}/${result.totalTests} passed")

        if (result.failedTests > 0) {
            println("\nFailed test details:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("  Test #${testCase.testNumber}:")
                testCase.differences.take(10).forEach { diff ->
                    println("    - $diff")
                }
                if (testCase.differences.size > 10) {
                    println("    ... and ${testCase.differences.size - 10} more differences")
                }
            }
        }

        println("=" .repeat(60))

        assertTrue(result.totalTests > 0, "Should have executed tests")
    }

    @Test
    fun testRandomStateGeneration() {
        println("Testing random state generation...")

        val state1 = SMBDifferentialTestGenerator.generateRandomState(12345L)
        val state2 = SMBDifferentialTestGenerator.generateRandomState(12345L)
        val state3 = SMBDifferentialTestGenerator.generateRandomState(99999L)

        // Same seed should produce same state
        assertTrue(state1.A == state2.A, "Same seed should produce same A register")
        assertTrue(state1.X == state2.X, "Same seed should produce same X register")

        // Different seed should produce different state (with high probability)
        assertTrue(state1.A != state3.A || state1.X != state3.X,
            "Different seed should produce different state")

        println("✓ Random state generation working correctly")
    }

    @Test
    fun testCompareStates() {
        println("Testing state comparison...")

        val state1 = IntegrationTest.CPUState(
            A = 0x42u,
            X = 0x10u,
            Y = 0x20u,
            memory = mapOf(0x100 to 0xFFu, 0x200 to 0xAAu)
        )

        val state2 = IntegrationTest.CPUState(
            A = 0x42u,
            X = 0x11u,  // Different X
            Y = 0x20u,
            memory = mapOf(0x100 to 0xFFu, 0x200 to 0xBBu)  // Different memory
        )

        val differences = SMBDifferentialTestGenerator.compareStates(state1, state2)

        assertTrue(differences.size >= 2, "Should detect X and memory differences")
        assertTrue(differences.any { it.contains("X:") }, "Should report X difference")
        assertTrue(differences.any { it.contains("0x0200") }, "Should report memory difference at 0x200")

        println("✓ State comparison working correctly")
        println("  Detected ${differences.size} differences:")
        differences.forEach { println("    - $it") }
    }
}
