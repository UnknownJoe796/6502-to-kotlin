package com.ivieleague.decompiler6502tokotlin.validation

import com.ivieleague.decompiler6502tokotlin.hand.*
import com.ivieleague.decompiler6502tokotlin.interpreter.*

/**
 * Integration layer between the validation framework and the decompiler.
 *
 * This module:
 * 1. Takes assembly instructions
 * 2. Runs them through the decompilation pipeline
 * 3. Generates Kotlin code
 * 4. Provides hooks for execution
 */

/**
 * Simple decompiler for straight-line code (no branches/jumps).
 * This is a simplified version for initial validation testing.
 */
class SimpleDecompiler {

    /**
     * Generate Kotlin code from a list of instructions.
     * For now, this generates simple imperative code without full decompilation.
     */
    fun generateKotlinCode(instructions: List<AssemblyInstruction>): String {
        val code = StringBuilder()

        code.appendLine("// Generated from 6502 assembly")
        code.appendLine("class Generated6502 {")
        code.appendLine("    // CPU Registers")
        code.appendLine("    var A: UByte = 0u")
        code.appendLine("    var X: UByte = 0u")
        code.appendLine("    var Y: UByte = 0u")
        code.appendLine("    var SP: UByte = 0xFFu")
        code.appendLine()
        code.appendLine("    // Flags")
        code.appendLine("    var N: Boolean = false")
        code.appendLine("    var V: Boolean = false")
        code.appendLine("    var Z: Boolean = false")
        code.appendLine("    var C: Boolean = false")
        code.appendLine("    var I: Boolean = false")
        code.appendLine("    var D: Boolean = false")
        code.appendLine()
        code.appendLine("    // Memory (64K)")
        code.appendLine("    val memory = ByteArray(65536)")
        code.appendLine()
        code.appendLine("    fun updateZN(value: UByte) {")
        code.appendLine("        Z = value == 0.toUByte()")
        code.appendLine("        N = (value.toInt() and 0x80) != 0")
        code.appendLine("    }")
        code.appendLine()
        code.appendLine("    fun execute() {")

        // Generate code for each instruction
        for (inst in instructions) {
            val kotlinCode = instructionToKotlin(inst)
            code.appendLine("        // ${inst.op}")
            kotlinCode.forEach { line ->
                code.appendLine("        $line")
            }
        }

        code.appendLine("    }")
        code.appendLine("}")

        return code.toString()
    }

    /**
     * Convert a single instruction to Kotlin code lines.
     */
    private fun instructionToKotlin(inst: AssemblyInstruction): List<String> {
        return when (inst.op) {
            // Load instructions
            AssemblyOp.LDA -> {
                val value = addressingToKotlin(inst.address, "A")
                listOf(
                    "A = $value",
                    "updateZN(A)"
                )
            }
            AssemblyOp.LDX -> {
                val value = addressingToKotlin(inst.address, "X")
                listOf(
                    "X = $value",
                    "updateZN(X)"
                )
            }
            AssemblyOp.LDY -> {
                val value = addressingToKotlin(inst.address, "Y")
                listOf(
                    "Y = $value",
                    "updateZN(Y)"
                )
            }

            // Store instructions
            AssemblyOp.STA -> {
                val addr = addressingToAddress(inst.address!!)
                listOf("memory[$addr] = A.toByte()")
            }
            AssemblyOp.STX -> {
                val addr = addressingToAddress(inst.address!!)
                listOf("memory[$addr] = X.toByte()")
            }
            AssemblyOp.STY -> {
                val addr = addressingToAddress(inst.address!!)
                listOf("memory[$addr] = Y.toByte()")
            }

            // Arithmetic
            AssemblyOp.ADC -> {
                val value = addressingToKotlin(inst.address, "A")
                listOf(
                    "val result = A.toInt() + ($value).toInt() + (if (C) 1 else 0)",
                    "C = result > 0xFF",
                    "val resultByte = (result and 0xFF).toUByte()",
                    "V = ((A.toInt() xor resultByte.toInt()) and 0x80) != 0 && ((A.toInt() xor ($value).toInt()) and 0x80) == 0",
                    "A = resultByte",
                    "updateZN(A)"
                )
            }
            AssemblyOp.SBC -> {
                val value = addressingToKotlin(inst.address, "A")
                listOf(
                    "val result = A.toInt() - ($value).toInt() - (if (C) 0 else 1)",
                    "C = result >= 0",
                    "val resultByte = (result and 0xFF).toUByte()",
                    "V = ((A.toInt() xor resultByte.toInt()) and 0x80) != 0 && ((A.toInt() xor ($value).toInt()) and 0x80) != 0",
                    "A = resultByte",
                    "updateZN(A)"
                )
            }

            // Logical
            AssemblyOp.AND -> {
                val value = addressingToKotlin(inst.address, "A")
                listOf(
                    "A = (A.toInt() and ($value).toInt()).toUByte()",
                    "updateZN(A)"
                )
            }
            AssemblyOp.ORA -> {
                val value = addressingToKotlin(inst.address, "A")
                listOf(
                    "A = (A.toInt() or ($value).toInt()).toUByte()",
                    "updateZN(A)"
                )
            }
            AssemblyOp.EOR -> {
                val value = addressingToKotlin(inst.address, "A")
                listOf(
                    "A = (A.toInt() xor ($value).toInt()).toUByte()",
                    "updateZN(A)"
                )
            }

            // Comparison
            AssemblyOp.CMP -> {
                val value = addressingToKotlin(inst.address, "A")
                listOf(
                    "val cmpResult = A.toInt() - ($value).toInt()",
                    "C = cmpResult >= 0",
                    "Z = cmpResult == 0",
                    "N = (cmpResult and 0x80) != 0"
                )
            }
            AssemblyOp.CPX -> {
                val value = addressingToKotlin(inst.address, "X")
                listOf(
                    "val cmpResult = X.toInt() - ($value).toInt()",
                    "C = cmpResult >= 0",
                    "Z = cmpResult == 0",
                    "N = (cmpResult and 0x80) != 0"
                )
            }
            AssemblyOp.CPY -> {
                val value = addressingToKotlin(inst.address, "Y")
                listOf(
                    "val cmpResult = Y.toInt() - ($value).toInt()",
                    "C = cmpResult >= 0",
                    "Z = cmpResult == 0",
                    "N = (cmpResult and 0x80) != 0"
                )
            }

            // Transfer
            AssemblyOp.TAX -> listOf("X = A", "updateZN(X)")
            AssemblyOp.TAY -> listOf("Y = A", "updateZN(Y)")
            AssemblyOp.TXA -> listOf("A = X", "updateZN(A)")
            AssemblyOp.TYA -> listOf("A = Y", "updateZN(A)")
            AssemblyOp.TSX -> listOf("X = SP", "updateZN(X)")
            AssemblyOp.TXS -> listOf("SP = X")

            // Increment/Decrement
            AssemblyOp.INX -> listOf("X = (X.toInt() + 1 and 0xFF).toUByte()", "updateZN(X)")
            AssemblyOp.INY -> listOf("Y = (Y.toInt() + 1 and 0xFF).toUByte()", "updateZN(Y)")
            AssemblyOp.DEX -> listOf("X = (X.toInt() - 1 and 0xFF).toUByte()", "updateZN(X)")
            AssemblyOp.DEY -> listOf("Y = (Y.toInt() - 1 and 0xFF).toUByte()", "updateZN(Y)")
            AssemblyOp.INC -> {
                val addr = addressingToAddress(inst.address!!)
                listOf(
                    "val incValue = (memory[$addr].toUByte().toInt() + 1 and 0xFF).toUByte()",
                    "memory[$addr] = incValue.toByte()",
                    "updateZN(incValue)"
                )
            }
            AssemblyOp.DEC -> {
                val addr = addressingToAddress(inst.address!!)
                listOf(
                    "val decValue = (memory[$addr].toUByte().toInt() - 1 and 0xFF).toUByte()",
                    "memory[$addr] = decValue.toByte()",
                    "updateZN(decValue)"
                )
            }

            // Shifts
            AssemblyOp.ASL -> {
                if (inst.address == null) {
                    listOf(
                        "C = (A.toInt() and 0x80) != 0",
                        "A = ((A.toInt() shl 1) and 0xFF).toUByte()",
                        "updateZN(A)"
                    )
                } else {
                    val addr = addressingToAddress(inst.address)
                    listOf(
                        "val aslValue = memory[$addr].toUByte()",
                        "C = (aslValue.toInt() and 0x80) != 0",
                        "val aslResult = ((aslValue.toInt() shl 1) and 0xFF).toUByte()",
                        "memory[$addr] = aslResult.toByte()",
                        "updateZN(aslResult)"
                    )
                }
            }
            AssemblyOp.LSR -> {
                if (inst.address == null) {
                    listOf(
                        "C = (A.toInt() and 0x01) != 0",
                        "A = (A.toInt() ushr 1).toUByte()",
                        "updateZN(A)"
                    )
                } else {
                    val addr = addressingToAddress(inst.address)
                    listOf(
                        "val lsrValue = memory[$addr].toUByte()",
                        "C = (lsrValue.toInt() and 0x01) != 0",
                        "val lsrResult = (lsrValue.toInt() ushr 1).toUByte()",
                        "memory[$addr] = lsrResult.toByte()",
                        "updateZN(lsrResult)"
                    )
                }
            }

            // Flags
            AssemblyOp.CLC -> listOf("C = false")
            AssemblyOp.SEC -> listOf("C = true")
            AssemblyOp.CLI -> listOf("I = false")
            AssemblyOp.SEI -> listOf("I = true")
            AssemblyOp.CLD -> listOf("D = false")
            AssemblyOp.SED -> listOf("D = true")
            AssemblyOp.CLV -> listOf("V = false")

            // Stack
            AssemblyOp.PHA -> listOf(
                "memory[0x0100 + SP.toInt()] = A.toByte()",
                "SP = (SP.toInt() - 1 and 0xFF).toUByte()"
            )
            AssemblyOp.PLA -> listOf(
                "SP = (SP.toInt() + 1 and 0xFF).toUByte()",
                "A = memory[0x0100 + SP.toInt()].toUByte()",
                "updateZN(A)"
            )

            AssemblyOp.NOP -> listOf("// NOP")

            else -> listOf("// TODO: ${inst.op}")
        }
    }

    /**
     * Convert addressing mode to Kotlin expression for reading a value.
     */
    private fun addressingToKotlin(addressing: AssemblyAddressing?, register: String): String {
        return when (addressing) {
            is AssemblyAddressing.ByteValue -> "${addressing.value}u"
            is AssemblyAddressing.Direct -> {
                val addr = parseAddress(addressing.label)
                "memory[$addr].toUByte()"
            }
            is AssemblyAddressing.DirectX -> {
                val addr = parseAddress(addressing.label)
                "memory[$addr + X.toInt()].toUByte()"
            }
            is AssemblyAddressing.DirectY -> {
                val addr = parseAddress(addressing.label)
                "memory[$addr + Y.toInt()].toUByte()"
            }
            null -> register // Accumulator mode
            else -> "0u // TODO: $addressing"
        }
    }

    /**
     * Convert addressing mode to memory address expression.
     */
    private fun addressingToAddress(addressing: AssemblyAddressing): String {
        return when (addressing) {
            is AssemblyAddressing.Direct -> parseAddress(addressing.label).toString()
            is AssemblyAddressing.DirectX -> {
                val addr = parseAddress(addressing.label)
                "($addr + X.toInt())"
            }
            is AssemblyAddressing.DirectY -> {
                val addr = parseAddress(addressing.label)
                "($addr + Y.toInt())"
            }
            else -> "0 // TODO: $addressing"
        }
    }

    /**
     * Parse address label (hex or decimal).
     */
    private fun parseAddress(label: String): Int {
        return when {
            label.startsWith("\$") -> label.substring(1).toInt(16)
            label.all { it.isDigit() } -> label.toInt()
            else -> 0 // TODO: label resolution
        }
    }
}

/**
 * Executor for generated Kotlin code.
 * Compiles Kotlin code using kotlinc and executes it to capture state.
 */
class KotlinCodeExecutor {

    /**
     * Execute generated Kotlin code and capture the resulting state.
     *
     * Process:
     * 1. Write generated code to temporary .kt file
     * 2. Compile with kotlinc to .jar
     * 3. Execute and capture state using a wrapper
     * 4. Return ExecutionState for comparison
     */
    fun execute(
        kotlinCode: String,
        initialMemory: Map<Int, UByte>,
        initialRegisters: DecompilerTestCase.RegisterSetup,
        memoryAddressesToCheck: List<Int> = emptyList()
    ): ExecutionState {
        // Create temporary directory for compilation
        val tempDir = java.io.File("./local/kotlin-compile-temp-${System.currentTimeMillis()}")
        tempDir.mkdirs()

        try {
            // Write generated code to file
            val sourceFile = java.io.File(tempDir, "Generated6502.kt")
            sourceFile.writeText(kotlinCode)

            // Write wrapper code that sets up initial state and captures final state
            val wrapperCode = generateWrapperCode(initialMemory, initialRegisters, memoryAddressesToCheck)
            val wrapperFile = java.io.File(tempDir, "Wrapper.kt")
            wrapperFile.writeText(wrapperCode)

            // Compile with kotlinc
            val jarFile = java.io.File(tempDir, "generated.jar")
            val compileProcess = ProcessBuilder(
                "kotlinc",
                sourceFile.absolutePath,
                wrapperFile.absolutePath,
                "-include-runtime",
                "-d", jarFile.absolutePath
            ).redirectErrorStream(true).start()

            val compileOutput = compileProcess.inputStream.bufferedReader().readText()
            val compileResult = compileProcess.waitFor()

            if (compileResult != 0) {
                println("Compilation failed:")
                println(compileOutput)
                throw RuntimeException("Failed to compile generated Kotlin code: exit code $compileResult")
            }

            // Execute compiled code
            val executeProcess = ProcessBuilder(
                "java",
                "-jar",
                jarFile.absolutePath
            ).redirectErrorStream(true).start()

            val executeOutput = executeProcess.inputStream.bufferedReader().readText()
            val executeResult = executeProcess.waitFor()

            if (executeResult != 0) {
                println("Execution failed:")
                println(executeOutput)
                throw RuntimeException("Failed to execute compiled code: exit code $executeResult")
            }

            // Parse output to extract state
            val result = parseExecutionOutput(executeOutput)

            // Save generated code for inspection if DEBUG flag is set
            if (System.getProperty("SAVE_GENERATED_CODE") != null) {
                println("Generated files saved in: ${tempDir.absolutePath}")
            } else {
                // Clean up temporary files
                tempDir.deleteRecursively()
            }

            return result
        } catch (e: Exception) {
            // On error, don't clean up so we can inspect the files
            println("Error occurred. Generated files saved in: ${tempDir.absolutePath}")
            throw e
        }
    }

    /**
     * Generate wrapper code that:
     * 1. Creates instance of Generated6502
     * 2. Sets up initial memory and registers
     * 3. Calls execute()
     * 4. Prints final state in parseable format
     */
    private fun generateWrapperCode(
        initialMemory: Map<Int, UByte>,
        initialRegisters: DecompilerTestCase.RegisterSetup,
        memoryAddressesToCheck: List<Int>
    ): String {
        val code = StringBuilder()

        code.appendLine("fun main() {")
        code.appendLine("    val cpu = Generated6502()")
        code.appendLine()

        // Set up initial memory
        if (initialMemory.isNotEmpty()) {
            code.appendLine("    // Initial memory setup")
            for ((addr, value) in initialMemory) {
                code.appendLine("    cpu.memory[$addr] = ${value.toByte()}  // 0x${value.toString(16).uppercase()}")
            }
            code.appendLine()
        }

        // Set up initial registers
        code.appendLine("    // Initial register setup")
        initialRegisters.A?.let { code.appendLine("    cpu.A = ${it}u") }
        initialRegisters.X?.let { code.appendLine("    cpu.X = ${it}u") }
        initialRegisters.Y?.let { code.appendLine("    cpu.Y = ${it}u") }
        initialRegisters.C?.let { code.appendLine("    cpu.C = $it") }
        initialRegisters.Z?.let { code.appendLine("    cpu.Z = $it") }
        initialRegisters.N?.let { code.appendLine("    cpu.N = $it") }
        initialRegisters.V?.let { code.appendLine("    cpu.V = $it") }
        code.appendLine()

        // Execute
        code.appendLine("    // Execute generated code")
        code.appendLine("    cpu.execute()")
        code.appendLine()

        // Print state in parseable format
        code.appendLine("    // Print final state")
        code.appendLine("    println(\"STATE_BEGIN\")")
        code.appendLine("    println(\"A=${'$'}{cpu.A}\")")
        code.appendLine("    println(\"X=${'$'}{cpu.X}\")")
        code.appendLine("    println(\"Y=${'$'}{cpu.Y}\")")
        code.appendLine("    println(\"N=${'$'}{cpu.N}\")")
        code.appendLine("    println(\"V=${'$'}{cpu.V}\")")
        code.appendLine("    println(\"Z=${'$'}{cpu.Z}\")")
        code.appendLine("    println(\"C=${'$'}{cpu.C}\")")
        code.appendLine("    println(\"I=${'$'}{cpu.I}\")")
        code.appendLine("    println(\"D=${'$'}{cpu.D}\")")

        // Print memory values
        for (addr in memoryAddressesToCheck) {
            code.appendLine("    println(\"MEM[$addr]=${'$'}{cpu.memory[$addr].toUByte()}\")")
        }

        code.appendLine("    println(\"STATE_END\")")
        code.appendLine("}")

        return code.toString()
    }

    /**
     * Parse execution output to extract final state.
     */
    private fun parseExecutionOutput(output: String): ExecutionState {
        val lines = output.lines()

        // Find STATE_BEGIN and STATE_END markers
        val beginIndex = lines.indexOf("STATE_BEGIN")
        val endIndex = lines.indexOf("STATE_END")

        if (beginIndex == -1 || endIndex == -1) {
            throw RuntimeException("Could not find state markers in output:\n$output")
        }

        val stateLines = lines.subList(beginIndex + 1, endIndex)
        val state = mutableMapOf<String, String>()
        val memory = mutableMapOf<Int, UByte>()

        for (line in stateLines) {
            if (line.startsWith("MEM[")) {
                // Parse memory lines: MEM[4096]=66
                val memPattern = Regex("""MEM\[(\d+)]=(\d+)""")
                val match = memPattern.find(line)
                if (match != null) {
                    val addr = match.groupValues[1].toInt()
                    val value = match.groupValues[2].toUByte()
                    memory[addr] = value
                }
            } else {
                // Parse register/flag lines: A=42
                val parts = line.split("=", limit = 2)
                if (parts.size == 2) {
                    state[parts[0]] = parts[1]
                }
            }
        }

        return ExecutionState(
            memory = memory,
            registerA = state["A"]?.toUByte() ?: 0u,
            registerX = state["X"]?.toUByte() ?: 0u,
            registerY = state["Y"]?.toUByte() ?: 0u,
            flagN = state["N"]?.toBoolean() ?: false,
            flagV = state["V"]?.toBoolean() ?: false,
            flagZ = state["Z"]?.toBoolean() ?: false,
            flagC = state["C"]?.toBoolean() ?: false,
            flagI = state["I"]?.toBoolean() ?: false,
            flagD = state["D"]?.toBoolean() ?: false
        )
    }
}

/**
 * Expression-building decompiler that produces idiomatic Kotlin code.
 *
 * Unlike SimpleDecompiler which generates one statement per instruction,
 * this decompiler:
 * - Traces values through registers
 * - Builds compound expressions
 * - Eliminates intermediate register variables
 * - Produces minimal, clean output
 */
class ExpressionDecompiler {

    /**
     * Represents a value that flows through the code.
     */
    sealed class Value {
        data class Constant(val value: UByte) : Value() {
            override fun toKotlin() = "${value}u"
        }
        data class MemoryRead(val address: Int) : Value() {
            override fun toKotlin() = "memory[$address].toUByte()"
        }
        data class BinaryOp(val left: Value, val op: String, val right: Value) : Value() {
            override fun toKotlin() = "(${left.toKotlin()} $op ${right.toKotlin()})"
        }
        data class UnaryOp(val op: String, val operand: Value) : Value() {
            override fun toKotlin() = "($op ${operand.toKotlin()})"
        }
        data class ShiftLeft(val operand: Value, val count: Int = 1) : Value() {
            override fun toKotlin(): String {
                val operandStr = operand.toKotlin()
                // UByte doesn't have shl, so we need to convert to UInt first
                val expr = "($operandStr.toUInt() shl $count)"
                return expr
            }
        }
        data class ShiftRight(val operand: Value, val count: Int = 1) : Value() {
            override fun toKotlin(): String {
                val operandStr = operand.toKotlin()
                // UByte doesn't have ushr, so we need to convert to UInt first
                val expr = "($operandStr.toUInt() ushr $count)"
                return expr
            }
        }
        data class Masked(val operand: Value, val mask: Int = 0xFF) : Value() {
            override fun toKotlin() = "(${operand.toKotlin()} and ${mask}u)"
        }

        abstract fun toKotlin(): String
    }

    /**
     * Represents a pending carry from a previous operation.
     * Used to detect multi-byte arithmetic patterns.
     */
    private data class PendingCarry(
        val sourceExpression: Value,  // The expression that generated the carry
        val lowByteAddress: Int?       // Address where low byte was stored (if known)
    )

    /**
     * Tracks the current value in each register.
     */
    private data class RegisterState(
        var A: Value? = null,
        var X: Value? = null,
        var Y: Value? = null,
        var carrySet: Boolean = false,
        var pendingCarry: PendingCarry? = null  // Carry output from previous ADC/SBC
    )

    /**
     * Generate expression-based Kotlin code.
     */
    fun generateKotlinCode(instructions: List<AssemblyInstruction>): String {
        val state = RegisterState()
        val outputs = mutableListOf<Pair<Int, Value>>()  // memory address -> value

        // Trace values through instructions
        for (inst in instructions) {
            processInstruction(inst, state, outputs)
        }

        // Generate code
        return buildKotlinCode(outputs)
    }

    private fun processInstruction(
        inst: AssemblyInstruction,
        state: RegisterState,
        outputs: MutableList<Pair<Int, Value>>
    ) {
        when (inst.op) {
            // Load instructions - capture the value
            AssemblyOp.LDA -> {
                state.A = parseValue(inst.address, state)
            }
            AssemblyOp.LDX -> {
                state.X = parseValue(inst.address, state)
            }
            AssemblyOp.LDY -> {
                state.Y = parseValue(inst.address, state)
            }

            // Store instructions - record the output
            AssemblyOp.STA -> {
                val addr = getAddress(inst.address!!)
                state.A?.let { value ->
                    outputs.add(addr to Value.Masked(value))
                    // Track potential carry for multi-byte operations
                    if (value is Value.BinaryOp && value.op == "+") {
                        state.pendingCarry = PendingCarry(value, addr)
                    }
                }
            }
            AssemblyOp.STX -> {
                val addr = getAddress(inst.address!!)
                state.X?.let { value ->
                    outputs.add(addr to Value.Masked(value))
                }
            }
            AssemblyOp.STY -> {
                val addr = getAddress(inst.address!!)
                state.Y?.let { value ->
                    outputs.add(addr to Value.Masked(value))
                }
            }

            // Arithmetic
            AssemblyOp.ADC -> {
                val right = parseValue(inst.address, state)

                // Check if this is the high byte of a multi-byte addition
                // Pattern: ADC #$00 after a previous addition with pending carry
                val isHighByteAddition = right is Value.Constant && right.value == 0u.toUByte() &&
                                        state.pendingCarry != null && !state.carrySet

                state.A = if (isHighByteAddition && state.A != null) {
                    // This is adding carry from previous operation
                    // Create a CarryValue to represent "carry from previous ADC"
                    val carryValue = Value.Constant(1u) // Simplified: assume carry will be generated
                    Value.BinaryOp(state.A!!, "+", carryValue)
                } else if (state.A != null) {
                    if (state.carrySet) {
                        Value.BinaryOp(Value.BinaryOp(state.A!!, "+", right), "+", Value.Constant(1u))
                    } else {
                        Value.BinaryOp(state.A!!, "+", right)
                    }
                } else {
                    right
                }

                // Clear pending carry after we've potentially used it
                if (isHighByteAddition) {
                    state.pendingCarry = null
                }
            }
            AssemblyOp.SBC -> {
                val right = parseValue(inst.address, state)
                state.A = if (state.A != null) {
                    if (state.carrySet) {
                        Value.BinaryOp(state.A!!, "-", right)
                    } else {
                        Value.BinaryOp(Value.BinaryOp(state.A!!, "-", right), "-", Value.Constant(1u))
                    }
                } else {
                    Value.UnaryOp("-", right)
                }
            }

            // Logical
            AssemblyOp.AND -> {
                val right = parseValue(inst.address, state)
                state.A = if (state.A != null) {
                    Value.BinaryOp(state.A!!, "and", right)
                } else {
                    right
                }
            }
            AssemblyOp.ORA -> {
                val right = parseValue(inst.address, state)
                state.A = if (state.A != null) {
                    Value.BinaryOp(state.A!!, "or", right)
                } else {
                    right
                }
            }
            AssemblyOp.EOR -> {
                val right = parseValue(inst.address, state)
                state.A = if (state.A != null) {
                    Value.BinaryOp(state.A!!, "xor", right)
                } else {
                    right
                }
            }

            // Transfers - just copy the value
            AssemblyOp.TAX -> {
                state.X = state.A
            }
            AssemblyOp.TAY -> {
                state.Y = state.A
            }
            AssemblyOp.TXA -> {
                state.A = state.X
            }
            AssemblyOp.TYA -> {
                state.A = state.Y
            }

            // Shifts
            AssemblyOp.ASL -> {
                if (inst.address == null) {
                    // Accumulator mode
                    state.A = state.A?.let {
                        if (it is Value.ShiftLeft) {
                            Value.ShiftLeft(it.operand, it.count + 1)
                        } else {
                            Value.ShiftLeft(it)
                        }
                    }
                }
            }
            AssemblyOp.LSR -> {
                if (inst.address == null) {
                    // Accumulator mode
                    state.A = state.A?.let {
                        if (it is Value.ShiftRight) {
                            Value.ShiftRight(it.operand, it.count + 1)
                        } else {
                            Value.ShiftRight(it)
                        }
                    }
                }
            }

            // Increments/Decrements
            AssemblyOp.INX -> {
                state.X = state.X?.let { Value.BinaryOp(it, "+", Value.Constant(1u)) } ?: Value.Constant(1u)
            }
            AssemblyOp.INY -> {
                state.Y = state.Y?.let { Value.BinaryOp(it, "+", Value.Constant(1u)) } ?: Value.Constant(1u)
            }
            AssemblyOp.DEX -> {
                state.X = state.X?.let { Value.BinaryOp(it, "-", Value.Constant(1u)) }
            }
            AssemblyOp.DEY -> {
                state.Y = state.Y?.let { Value.BinaryOp(it, "-", Value.Constant(1u)) }
            }

            // Flags
            AssemblyOp.CLC -> {
                state.carrySet = false
            }
            AssemblyOp.SEC -> {
                state.carrySet = true
            }

            // Ignore other flag operations for expression building
            AssemblyOp.CLI, AssemblyOp.SEI, AssemblyOp.CLD, AssemblyOp.SED, AssemblyOp.CLV -> {
                // These don't affect value flow
            }

            else -> {
                // Unknown instruction - reset state to be safe
                state.A = null
                state.X = null
                state.Y = null
            }
        }
    }

    private fun parseValue(addressing: AssemblyAddressing?, state: RegisterState): Value {
        return when (addressing) {
            is AssemblyAddressing.ByteValue -> Value.Constant(addressing.value)
            is AssemblyAddressing.Direct -> {
                val addr = parseAddress(addressing.label)
                Value.MemoryRead(addr)
            }
            null -> Value.Constant(0u)
            else -> Value.Constant(0u)  // TODO: handle other addressing modes
        }
    }

    private fun getAddress(addressing: AssemblyAddressing): Int {
        return when (addressing) {
            is AssemblyAddressing.Direct -> parseAddress(addressing.label)
            else -> 0
        }
    }

    private fun parseAddress(label: String): Int {
        return when {
            label.startsWith("\$") -> label.substring(1).toInt(16)
            label.all { it.isDigit() } -> label.toInt()
            else -> 0
        }
    }

    private fun buildKotlinCode(outputs: List<Pair<Int, Value>>): String {
        val code = StringBuilder()

        code.appendLine("// Generated from 6502 assembly")
        code.appendLine("class Generated6502 {")
        code.appendLine("    // Memory")
        code.appendLine("    val memory = ByteArray(65536)")
        code.appendLine()
        code.appendLine("    // Stub registers/flags (not used in expression-based code)")
        code.appendLine("    var A: UByte = 0u")
        code.appendLine("    var X: UByte = 0u")
        code.appendLine("    var Y: UByte = 0u")
        code.appendLine("    var N: Boolean = false")
        code.appendLine("    var V: Boolean = false")
        code.appendLine("    var Z: Boolean = false")
        code.appendLine("    var C: Boolean = false")
        code.appendLine("    var I: Boolean = false")
        code.appendLine("    var D: Boolean = false")
        code.appendLine()
        code.appendLine("    fun execute() {")

        for ((addr, value) in outputs) {
            val simplified = simplifyValue(value)
            val expr = simplified.toKotlin()
            code.appendLine("        memory[$addr] = ($expr and 0xFFu).toByte()")
        }

        code.appendLine("    }")
        code.appendLine("}")

        return code.toString()
    }

    /**
     * Simplify value expressions.
     */
    private fun simplifyValue(value: Value): Value {
        return when (value) {
            is Value.Masked -> {
                // If inner value is a constant, just return it (masking is redundant for constants < 256)
                when (val inner = simplifyValue(value.operand)) {
                    is Value.Constant -> inner  // Constants are already in range
                    is Value.Masked -> inner  // Already masked, don't double-mask
                    is Value.ShiftLeft -> inner  // Shifts produce values that need masking anyway
                    is Value.ShiftRight -> inner  // Shifts produce values that need masking anyway
                    else -> value  // Keep original for complex expressions
                }
            }
            is Value.BinaryOp -> Value.BinaryOp(simplifyValue(value.left), value.op, simplifyValue(value.right))
            is Value.UnaryOp -> Value.UnaryOp(value.op, simplifyValue(value.operand))
            is Value.ShiftLeft -> Value.ShiftLeft(simplifyValue(value.operand), value.count)
            is Value.ShiftRight -> Value.ShiftRight(simplifyValue(value.operand), value.count)
            else -> value
        }
    }
}
