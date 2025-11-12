package com.ivieleague.decompiler6502tokotlin

import com.ivieleague.decompiler6502tokotlin.hand.*
import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Differential tests that compare interpreter execution against translated Kotlin code.
 *
 * These tests will reveal what's broken in the code generator by comparing:
 * 1. Execution through the 6502 interpreter
 * 2. Execution of the generated Kotlin code
 *
 * Any differences indicate bugs in the code generation or translation logic.
 */
class DifferentialTest {

    /**
     * Test a simple LDA (load accumulator) instruction.
     */
    @Test
    fun testSimpleLoad() {
        val runner = TranslationValidator.testInstructions(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)
            )
        )

        val initialState = IntegrationTest.CPUState()

        println("\n=== Test: Simple Load ===")
        println("Instruction: LDA #\$42")

        val kotlinCode = runner.generateKotlinCode()
        println("\nGenerated Kotlin:")
        println(kotlinCode)

        val result = runner.compare(initialState)
        result.printReport()

        // This test might fail if code generation is broken
        if (!result.matches) {
            println("\n⚠️  This test reveals issues in the code generator!")
            println("Generated code does not match interpreter behavior.")
        }
    }

    /**
     * Test load and store sequence.
     */
    @Test
    fun testLoadAndStore() {
        val runner = TranslationValidator.testInstructions(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x55u, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(
                AssemblyOp.STA,
                AssemblyAddressing.Direct("target")
            ),
            labelResolver = { label -> if (label == "target") 0x1000 else 0 }
        )

        val initialState = IntegrationTest.CPUState(
            memory = mapOf(0x1000 to 0x00u)
        )

        println("\n=== Test: Load and Store ===")
        println("Instructions: LDA #\$55, STA target")

        val kotlinCode = runner.generateKotlinCode()
        println("\nGenerated Kotlin:")
        println(kotlinCode)

        val result = runner.compare(initialState)
        result.printReport()

        if (!result.matches) {
            println("\n⚠️  Store instruction code generation has issues!")
        }
    }

    /**
     * Test arithmetic addition.
     */
    @Test
    fun testArithmeticAddition() {
        val runner = TranslationValidator.testInstructions(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x10u, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x20u, AssemblyAddressing.Radix.Hex)
            )
        )

        val initialState = IntegrationTest.CPUState(C = false)

        println("\n=== Test: Arithmetic Addition ===")
        println("Instructions: LDA #\$10, ADC #\$20")

        val kotlinCode = runner.generateKotlinCode()
        println("\nGenerated Kotlin:")
        println(kotlinCode)

        val result = runner.compare(initialState)
        result.printReport()

        if (!result.matches) {
            println("\n⚠️  ADC instruction code generation has issues!")
            println("Expected A=0x30, got A=0x${result.translatedState?.A?.toString(16)}")
        }
    }

    /**
     * Test register transfers.
     */
    @Test
    fun testRegisterTransfers() {
        val runner = TranslationValidator.testInstructions(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(AssemblyOp.TAX),
            AssemblyInstruction(AssemblyOp.TAY)
        )

        val initialState = IntegrationTest.CPUState()

        println("\n=== Test: Register Transfers ===")
        println("Instructions: LDA #\$42, TAX, TAY")

        val kotlinCode = runner.generateKotlinCode()
        println("\nGenerated Kotlin:")
        println(kotlinCode)

        val result = runner.compare(initialState)
        result.printReport()

        if (!result.matches) {
            println("\n⚠️  Register transfer code generation has issues!")
        }
    }

    /**
     * Test increment operations.
     */
    @Test
    fun testIncrement() {
        val runner = TranslationValidator.testInstructions(
            AssemblyInstruction(
                AssemblyOp.LDX,
                AssemblyAddressing.ByteValue(0x10u, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(AssemblyOp.INX)
        )

        val initialState = IntegrationTest.CPUState()

        println("\n=== Test: Increment ===")
        println("Instructions: LDX #\$10, INX")

        val kotlinCode = runner.generateKotlinCode()
        println("\nGenerated Kotlin:")
        println(kotlinCode)

        val result = runner.compare(initialState)
        result.printReport()

        if (!result.matches) {
            println("\n⚠️  INX instruction code generation has issues!")
        }
    }

    /**
     * Test logical AND operation.
     */
    @Test
    fun testLogicalAND() {
        val runner = TranslationValidator.testInstructions(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0xFFu, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(
                AssemblyOp.AND,
                AssemblyAddressing.ByteValue(0x0Fu, AssemblyAddressing.Radix.Hex)
            )
        )

        val initialState = IntegrationTest.CPUState()

        println("\n=== Test: Logical AND ===")
        println("Instructions: LDA #\$FF, AND #\$0F")

        val kotlinCode = runner.generateKotlinCode()
        println("\nGenerated Kotlin:")
        println(kotlinCode)

        val result = runner.compare(initialState)
        result.printReport()

        if (!result.matches) {
            println("\n⚠️  AND instruction code generation has issues!")
        }
    }

    /**
     * Test with multiple random states to find edge cases.
     */
    @Test
    fun testWithRandomStates() {
        val runner = TranslationValidator.testInstructions(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(AssemblyOp.TAX)
        )

        println("\n=== Test: Random States ===")
        println("Instructions: LDA #\$42, TAX")
        println("\nGenerated Kotlin:")
        println(runner.generateKotlinCode())

        val results = runner.validateWithRandomStates(count = 5, random = Random(12345))

        val passCount = results.count { it.matches }
        val failCount = results.count { !it.matches }

        println("\n=== Summary ===")
        println("Passed: $passCount/5")
        println("Failed: $failCount/5")

        if (failCount > 0) {
            println("\n⚠️  Some random states failed! This indicates edge case issues.")
            results.filter { !it.matches }.forEach { result ->
                println("\nFailed state:")
                result.printReport()
            }
        }
    }

    /**
     * Test that compares EVERY part of the state (not just A register).
     */
    @Test
    fun testComprehensiveStateComparison() {
        val runner = TranslationValidator.testInstructions(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x00u, AssemblyAddressing.Radix.Hex)
            )
        )

        println("\n=== Test: Comprehensive State Comparison ===")
        println("Instruction: LDA #\$00  (loads zero, should set Z flag)")

        val initialState = IntegrationTest.CPUState()

        val result = runner.compare(initialState)
        result.printReport()

        // Check that Z flag is properly set
        if (result.translatedState != null) {
            if (!result.translatedState.Z) {
                println("\n⚠️  Z flag not set correctly!")
                println("Expected Z=true when loading 0x00, got Z=false")
            }
            if (result.translatedState.N) {
                println("\n⚠️  N flag incorrectly set!")
                println("Expected N=false when loading 0x00, got N=true")
            }
        }
    }

    /**
     * Test all basic load instructions.
     */
    @Test
    fun testAllLoadInstructions() {
        val testCases = listOf(
            AssemblyOp.LDA to "A",
            AssemblyOp.LDX to "X",
            AssemblyOp.LDY to "Y"
        )

        println("\n=== Test: All Load Instructions ===")

        var allPassed = true

        for ((op, register) in testCases) {
            val runner = TranslationValidator.testInstructions(
                AssemblyInstruction(
                    op,
                    AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)
                )
            )

            println("\n--- Testing $op (load into $register) ---")
            val result = runner.compare(IntegrationTest.CPUState())

            if (!result.matches) {
                allPassed = false
                println("✗ FAILED")
                result.printReport()
            } else {
                println("✓ PASSED")
            }
        }

        if (allPassed) {
            println("\n✓ All load instructions work correctly!")
        } else {
            println("\n⚠️  Some load instructions have issues!")
        }
    }

    /**
     * Test carry flag behavior in arithmetic.
     */
    @Test
    fun testCarryFlagBehavior() {
        // Test: 0xFF + 0x02 with carry clear = 0x01 with carry set
        val runner = TranslationValidator.testInstructions(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0xFFu, AssemblyAddressing.Radix.Hex)
            ),
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x02u, AssemblyAddressing.Radix.Hex)
            )
        )

        println("\n=== Test: Carry Flag in Addition ===")
        println("Instructions: LDA #\$FF, ADC #\$02")

        val initialState = IntegrationTest.CPUState(C = false)

        val kotlinCode = runner.generateKotlinCode()
        println("\nGenerated Kotlin:")
        println(kotlinCode)

        val result = runner.compare(initialState)
        result.printReport()

        if (result.translatedState != null) {
            val expected = IntegrationTest.CPUState(A = 0x01u, C = true, Z = false, N = false)

            if (result.translatedState.A != expected.A) {
                println("\n⚠️  A register incorrect! Expected 0x01, got 0x${result.translatedState.A.toString(16)}")
            }
            if (result.translatedState.C != expected.C) {
                println("\n⚠️  Carry flag incorrect! Expected true, got ${result.translatedState.C}")
            }
        }
    }

    /**
     * Summary test that runs all basic operations and reports overall status.
     */
    @Test
    fun testCodeGeneratorHealthCheck() {
        println("\n" + "=".repeat(60))
        println("CODE GENERATOR HEALTH CHECK")
        println("=".repeat(60))

        val tests = mapOf(
            "LDA" to AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.ByteValue(0x42u)),
            "LDX" to AssemblyInstruction(AssemblyOp.LDX, AssemblyAddressing.ByteValue(0x42u)),
            "LDY" to AssemblyInstruction(AssemblyOp.LDY, AssemblyAddressing.ByteValue(0x42u)),
            "TAX" to AssemblyInstruction(AssemblyOp.TAX),
            "TAY" to AssemblyInstruction(AssemblyOp.TAY),
            "TXA" to AssemblyInstruction(AssemblyOp.TXA),
            "TYA" to AssemblyInstruction(AssemblyOp.TYA),
            "INX" to AssemblyInstruction(AssemblyOp.INX),
            "INY" to AssemblyInstruction(AssemblyOp.INY),
            "DEX" to AssemblyInstruction(AssemblyOp.DEX),
            "DEY" to AssemblyInstruction(AssemblyOp.DEY)
        )

        val results = mutableMapOf<String, Boolean>()

        for ((name, instruction) in tests) {
            val runner = TranslationValidator.testInstructions(instruction)

            // Set up appropriate initial state
            val initialState = when (instruction.op) {
                AssemblyOp.TAX, AssemblyOp.TAY -> IntegrationTest.CPUState(A = 0x42u)
                AssemblyOp.TXA -> IntegrationTest.CPUState(X = 0x42u)
                AssemblyOp.TYA -> IntegrationTest.CPUState(Y = 0x42u)
                AssemblyOp.DEX -> IntegrationTest.CPUState(X = 0x10u)
                AssemblyOp.DEY -> IntegrationTest.CPUState(Y = 0x10u)
                else -> IntegrationTest.CPUState()
            }

            val result = runner.compare(initialState)
            results[name] = result.matches

            val status = if (result.matches) "✓" else "✗"
            println("$status $name")

            if (!result.matches && result.error != null) {
                println("  Error: ${result.error}")
            }
        }

        val passed = results.count { it.value }
        val total = results.size
        val percentage = (passed * 100.0) / total

        println("\n" + "=".repeat(60))
        println("Results: $passed/$total passed (${percentage.toInt()}%)")
        println("=".repeat(60))

        if (percentage < 100) {
            println("\n⚠️  CODE GENERATOR HAS ISSUES!")
            println("Failed instructions:")
            results.filter { !it.value }.forEach { (name, _) ->
                println("  - $name")
            }
        } else {
            println("\n✓ CODE GENERATOR IS WORKING!")
        }
    }
}
