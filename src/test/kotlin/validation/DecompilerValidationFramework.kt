package com.ivieleague.decompiler6502tokotlin.validation

import com.ivieleague.decompiler6502tokotlin.hand.*
import com.ivieleague.decompiler6502tokotlin.interpreter.*
import kotlin.test.assertEquals

/**
 * Framework for validating that decompiled Kotlin code produces the same results
 * as interpreting the original 6502 assembly code.
 *
 * This framework:
 * 1. Runs 6502 assembly through the interpreter
 * 2. Decompiles the same assembly to Kotlin
 * 3. Compiles and executes the generated Kotlin
 * 4. Compares memory state, register values, and flags
 */

/**
 * Represents the state we want to compare between interpreter and decompiled code.
 */
data class ExecutionState(
    val memory: Map<Int, UByte> = emptyMap(),
    val registerA: UByte = 0u,
    val registerX: UByte = 0u,
    val registerY: UByte = 0u,
    val flagN: Boolean = false,
    val flagV: Boolean = false,
    val flagZ: Boolean = false,
    val flagC: Boolean = false,
    val flagI: Boolean = false,
    val flagD: Boolean = false
) {
    companion object {
        /**
         * Capture the current state from an interpreter.
         */
        fun fromInterpreter(interp: Interpreter6502, memoryAddresses: List<Int> = emptyList()): ExecutionState {
            return ExecutionState(
                memory = memoryAddresses.associateWith { interp.memory.readByte(it) },
                registerA = interp.cpu.A,
                registerX = interp.cpu.X,
                registerY = interp.cpu.Y,
                flagN = interp.cpu.N,
                flagV = interp.cpu.V,
                flagZ = interp.cpu.Z,
                flagC = interp.cpu.C,
                flagI = interp.cpu.I,
                flagD = interp.cpu.D
            )
        }
    }

    /**
     * Compare this state with another, with options to ignore certain fields.
     */
    fun assertEquals(
        other: ExecutionState,
        message: String = "",
        ignoreFlags: Set<String> = emptySet(),
        ignoreRegisters: Set<String> = emptySet()
    ) {
        val prefix = if (message.isNotEmpty()) "$message: " else ""

        // Compare memory
        val allAddresses = (memory.keys + other.memory.keys).toSet()
        for (addr in allAddresses) {
            val expected = memory[addr] ?: 0u
            val actual = other.memory[addr] ?: 0u
            kotlin.test.assertEquals(
                expected,
                actual,
                "${prefix}Memory at address ${addr.toString(16).padStart(4, '0').uppercase()} mismatch"
            )
        }

        // Compare registers
        if ("A" !in ignoreRegisters) {
            kotlin.test.assertEquals(registerA, other.registerA, "${prefix}Register A mismatch")
        }
        if ("X" !in ignoreRegisters) {
            kotlin.test.assertEquals(registerX, other.registerX, "${prefix}Register X mismatch")
        }
        if ("Y" !in ignoreRegisters) {
            kotlin.test.assertEquals(registerY, other.registerY, "${prefix}Register Y mismatch")
        }

        // Compare flags
        if ("N" !in ignoreFlags) {
            kotlin.test.assertEquals(flagN, other.flagN, "${prefix}Flag N mismatch")
        }
        if ("V" !in ignoreFlags) {
            kotlin.test.assertEquals(flagV, other.flagV, "${prefix}Flag V mismatch")
        }
        if ("Z" !in ignoreFlags) {
            kotlin.test.assertEquals(flagZ, other.flagZ, "${prefix}Flag Z mismatch")
        }
        if ("C" !in ignoreFlags) {
            kotlin.test.assertEquals(flagC, other.flagC, "${prefix}Flag C mismatch")
        }
        if ("I" !in ignoreFlags) {
            kotlin.test.assertEquals(flagI, other.flagI, "${prefix}Flag I mismatch")
        }
        if ("D" !in ignoreFlags) {
            kotlin.test.assertEquals(flagD, other.flagD, "${prefix}Flag D mismatch")
        }
    }
}

/**
 * Test case specification for decompiler validation.
 */
data class DecompilerTestCase(
    val name: String,
    val assembly: String,
    val setupMemory: Map<Int, UByte> = emptyMap(),
    val setupRegisters: RegisterSetup = RegisterSetup(),
    val checkMemory: List<Int> = emptyList(),
    val ignoreFlags: Set<String> = emptySet(),
    val ignoreRegisters: Set<String> = emptySet(),
    val expectedOutputs: List<ExpectedOutput> = emptyList()  // NEW: for expression validation
) {
    data class RegisterSetup(
        val A: UByte? = null,
        val X: UByte? = null,
        val Y: UByte? = null,
        val C: Boolean? = null,
        val Z: Boolean? = null,
        val N: Boolean? = null,
        val V: Boolean? = null
    )

    /**
     * Describes an expected output that should appear in the generated code.
     * Used to validate that decompilation produces clean, expression-based code.
     */
    data class ExpectedOutput(
        val memoryAddress: Int,
        val expectedExpression: String  // e.g., "1u.toByte()", "(7u - 3u).toByte()"
    )
}

/**
 * Helper class to build and execute test cases.
 */
class DecompilerValidator {

    /**
     * Parse assembly source into a list of AssemblyLine objects.
     */
    fun parseAssembly(source: String): List<AssemblyLine> {
        val lines = source.trimIndent().lines()
        val result = mutableListOf<AssemblyLine>()

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith(";")) continue

            // Very basic parsing - for more complex cases, use the full parser
            // This is just for simple test cases
            val parts = trimmed.split(Regex("\\s+"), limit = 2)
            val label = if (trimmed.contains(":")) {
                parts[0].removeSuffix(":")
            } else null

            val instructionPart = if (label != null) {
                parts.getOrNull(1) ?: ""
            } else {
                parts[0]
            }

            // For now, create placeholder AssemblyLine objects
            // In real implementation, would use the full parser
            result.add(
                AssemblyLine(
                    label = label,
                    instruction = null, // Would parse properly
                    comment = null,
                    data = null,
                    constant = null
                )
            )
        }

        return result
    }

    /**
     * Execute assembly through the interpreter.
     */
    fun executeWithInterpreter(
        instructions: List<AssemblyInstruction>,
        setup: DecompilerTestCase
    ): ExecutionState {
        val interp = Interpreter6502()

        // Setup memory
        for ((addr, value) in setup.setupMemory) {
            interp.memory.writeByte(addr, value)
        }

        // Setup registers
        setup.setupRegisters.A?.let { interp.cpu.A = it }
        setup.setupRegisters.X?.let { interp.cpu.X = it }
        setup.setupRegisters.Y?.let { interp.cpu.Y = it }
        setup.setupRegisters.C?.let { interp.cpu.C = it }
        setup.setupRegisters.Z?.let { interp.cpu.Z = it }
        setup.setupRegisters.N?.let { interp.cpu.N = it }
        setup.setupRegisters.V?.let { interp.cpu.V = it }

        // Execute each instruction
        for (instruction in instructions) {
            if (interp.halted) break
            interp.executeInstruction(instruction)
        }

        return ExecutionState.fromInterpreter(interp, setup.checkMemory)
    }

    /**
     * Execute decompiled Kotlin code.
     * Generates Kotlin code, compiles it, and executes to capture state.
     */
    fun executeDecompiledCode(
        instructions: List<AssemblyInstruction>,
        setup: DecompilerTestCase
    ): ExecutionState {
        // Use ExpressionDecompiler for clean, idiomatic code
        val decompiler = ExpressionDecompiler()
        val executor = KotlinCodeExecutor()

        // Generate Kotlin code
        val kotlinCode = decompiler.generateKotlinCode(instructions)

        // Execute and capture state
        return executor.execute(kotlinCode, setup.setupMemory, setup.setupRegisters, setup.checkMemory)
    }

    /**
     * Run a validation test case.
     */
    fun validate(testCase: DecompilerTestCase, instructions: List<AssemblyInstruction>) {
        println("Running validation: ${testCase.name}")

        // Execute with interpreter
        val interpreterState = executeWithInterpreter(instructions, testCase)
        println("  Interpreter state: A=${interpreterState.registerA}, X=${interpreterState.registerX}, Y=${interpreterState.registerY}")

        // Execute with decompiled code
        val decompiledState = executeDecompiledCode(instructions, testCase)
        println("  Decompiled state: A=${decompiledState.registerA}, X=${decompiledState.registerX}, Y=${decompiledState.registerY}")

        // Compare states
        interpreterState.assertEquals(
            decompiledState,
            message = testCase.name,
            ignoreFlags = testCase.ignoreFlags,
            ignoreRegisters = testCase.ignoreRegisters
        )

        println("  ✓ Validation passed")
    }
}

/**
 * DSL for building test cases.
 */
class DecompilerTestBuilder {
    private var name: String = ""
    private var assembly: String = ""
    private val setupMemory = mutableMapOf<Int, UByte>()
    private val setupRegisters = mutableMapOf<String, Any>()
    private val checkMemory = mutableListOf<Int>()
    private val ignoreFlags = mutableSetOf<String>()
    private val ignoreRegisters = mutableSetOf<String>()

    fun name(value: String) {
        name = value
    }

    fun assembly(value: String) {
        assembly = value
    }

    fun setupMemory(addr: Int, value: UByte) {
        setupMemory[addr] = value
    }

    fun setupRegister(register: String, value: Any) {
        setupRegisters[register] = value
    }

    fun checkMemoryAt(addr: Int) {
        checkMemory.add(addr)
    }

    fun ignoreFlag(flag: String) {
        ignoreFlags.add(flag)
    }

    fun ignoreRegister(register: String) {
        ignoreRegisters.add(register)
    }

    fun build(): DecompilerTestCase {
        val regSetup = DecompilerTestCase.RegisterSetup(
            A = setupRegisters["A"] as? UByte,
            X = setupRegisters["X"] as? UByte,
            Y = setupRegisters["Y"] as? UByte,
            C = setupRegisters["C"] as? Boolean,
            Z = setupRegisters["Z"] as? Boolean,
            N = setupRegisters["N"] as? Boolean,
            V = setupRegisters["V"] as? Boolean
        )

        return DecompilerTestCase(
            name = name,
            assembly = assembly,
            setupMemory = setupMemory,
            setupRegisters = regSetup,
            checkMemory = checkMemory,
            ignoreFlags = ignoreFlags,
            ignoreRegisters = ignoreRegisters
        )
    }
}

fun decompilerTest(builder: DecompilerTestBuilder.() -> Unit): DecompilerTestCase {
    return DecompilerTestBuilder().apply(builder).build()
}
