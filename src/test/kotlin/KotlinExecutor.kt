package com.ivieleague.decompiler6502tokotlin

import javax.script.ScriptEngineManager
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

/**
 * Executes generated Kotlin code in a 6502-like environment and captures the resulting state.
 *
 * This is the bridge between generated Kotlin code and our test framework.
 * It provides a simulated 6502 environment (registers, flags, memory) and executes
 * the generated code within that environment.
 */
object KotlinExecutor {

    /**
     * Execution environment that mimics 6502 processor state.
     * Generated Kotlin code will read from and write to these variables.
     */
    class ExecutionEnvironment {
        // Registers
        var A: UByte = 0u
        var X: UByte = 0u
        var Y: UByte = 0u
        var SP: UByte = 0xFFu
        var PC: UShort = 0u

        // Flags
        var N: Boolean = false
        var V: Boolean = false
        var Z: Boolean = false
        var C: Boolean = false
        var I: Boolean = false
        var D: Boolean = false

        // Memory (simplified - using map for sparse storage)
        private val memoryMap = mutableMapOf<Int, UByte>()

        fun readByte(address: Int): UByte {
            return memoryMap.getOrDefault(address, 0u)
        }

        fun writeByte(address: Int, value: UByte) {
            memoryMap[address] = value
        }

        fun readWord(address: Int): UShort {
            val low = readByte(address).toInt()
            val high = readByte(address + 1).toInt()
            return ((high shl 8) or low).toUShort()
        }

        fun writeWord(address: Int, value: UShort) {
            writeByte(address, (value.toInt() and 0xFF).toUByte())
            writeByte(address + 1, (value.toInt() shr 8).toUByte())
        }

        /**
         * Initialize from a CPUState.
         */
        fun loadState(state: IntegrationTest.CPUState) {
            A = state.A
            X = state.X
            Y = state.Y
            SP = state.SP
            PC = state.PC
            N = state.N
            V = state.V
            Z = state.Z
            C = state.C
            I = state.I
            D = state.D
            state.memory.forEach { (addr, value) ->
                writeByte(addr, value)
            }
        }

        /**
         * Capture current state as CPUState.
         */
        fun captureState(addressesToTrack: List<Int> = emptyList()): IntegrationTest.CPUState {
            return IntegrationTest.CPUState(
                A = A,
                X = X,
                Y = Y,
                SP = SP,
                PC = PC,
                N = N,
                V = V,
                Z = Z,
                C = C,
                I = I,
                D = D,
                memory = addressesToTrack.associateWith { readByte(it) }
            )
        }

        /**
         * Helper function to update Zero and Negative flags based on a value.
         */
        fun updateZN(value: UByte) {
            Z = (value == 0.toUByte())
            N = (value.toInt() and 0x80) != 0
        }
    }

    /**
     * Execute Kotlin code using JSR-223 scripting API.
     *
     * This is simpler than the full Kotlin scripting API and sufficient for our needs.
     */
    fun executeWithJSR223(
        kotlinCode: String,
        initialState: IntegrationTest.CPUState,
        addressesToTrack: List<Int> = emptyList()
    ): Result<IntegrationTest.CPUState> {
        return try {
            // Create execution environment
            val env = ExecutionEnvironment()
            env.loadState(initialState)

            // Get Kotlin script engine
            val engine = ScriptEngineManager().getEngineByExtension("kts")
                ?: return Result.failure(Exception("Kotlin scripting engine not found"))

            // Put environment in context
            engine.put("env", env)

            // Wrap the code to use the environment
            val wrappedCode = """
                val env = bindings["env"] as com.ivieleague.decompiler6502tokotlin.KotlinExecutor.ExecutionEnvironment

                // Make environment variables accessible
                var A: UByte
                    get() = env.A
                    set(value) { env.A = value }

                var X: UByte
                    get() = env.X
                    set(value) { env.X = value }

                var Y: UByte
                    get() = env.Y
                    set(value) { env.Y = value }

                var N: Boolean
                    get() = env.N
                    set(value) { env.N = value }

                var V: Boolean
                    get() = env.V
                    set(value) { env.V = value }

                var Z: Boolean
                    get() = env.Z
                    set(value) { env.Z = value }

                var C: Boolean
                    get() = env.C
                    set(value) { env.C = value }

                fun readByte(addr: Int) = env.readByte(addr)
                fun writeByte(addr: Int, value: UByte) = env.writeByte(addr, value)
                fun updateZN(value: UByte) = env.updateZN(value)

                // Execute the generated code
                $kotlinCode
            """.trimIndent()

            // Execute the script
            engine.eval(wrappedCode)

            // Capture final state
            Result.success(env.captureState(addressesToTrack))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Execute Kotlin code in a simpler way - direct interpretation.
     *
     * This attempts to parse and execute the generated code directly by
     * mapping 6502 operations to Kotlin operations.
     */
    fun executeDirectly(
        kotlinCode: String,
        initialState: IntegrationTest.CPUState,
        addressesToTrack: List<Int> = emptyList()
    ): Result<IntegrationTest.CPUState> {
        return try {
            // Create execution environment
            val env = ExecutionEnvironment()
            env.loadState(initialState)

            // Parse and execute each line of the generated code
            // This is a simplified interpreter for the generated Kotlin code
            val lines = kotlinCode.lines().map { it.trim() }.filter { it.isNotEmpty() }

            for (line in lines) {
                try {
                    executeLine(line, env)
                } catch (e: Exception) {
                    // Continue on errors - some generated code may be invalid
                    println("Warning: Failed to execute line '$line': ${e.message}")
                }
            }

            Result.success(env.captureState(addressesToTrack))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Execute a single line of generated Kotlin code.
     *
     * This is a simplified interpreter that handles common patterns from the code generator.
     */
    private fun executeLine(line: String, env: ExecutionEnvironment) {
        // Skip empty lines and comments
        if (line.isEmpty() || line.startsWith("//")) return

        // Handle simple assignments: A = value
        val assignmentPattern = """^([AXYN])\s*=\s*(.+)$""".toRegex()
        val assignmentMatch = assignmentPattern.find(line)
        if (assignmentMatch != null) {
            val (target, expr) = assignmentMatch.destructured
            val value = evaluateExpression(expr, env)

            when (target) {
                "A" -> {
                    env.A = value
                    env.updateZN(value)
                }
                "X" -> {
                    env.X = value
                    env.updateZN(value)
                }
                "Y" -> {
                    env.Y = value
                    env.updateZN(value)
                }
            }
            return
        }

        // Handle flag assignments: Z = expression
        val flagPattern = """^([NVZC])\s*=\s*(.+)$""".toRegex()
        val flagMatch = flagPattern.find(line)
        if (flagMatch != null) {
            val (flag, expr) = flagMatch.destructured
            val value = evaluateBooleanExpression(expr, env)

            when (flag) {
                "N" -> env.N = value
                "V" -> env.V = value
                "Z" -> env.Z = value
                "C" -> env.C = value
            }
            return
        }

        // Handle memory writes: memory[addr] = value
        val memWritePattern = """memory\[(.+?)\]\s*=\s*(.+)""".toRegex()
        val memWriteMatch = memWritePattern.find(line)
        if (memWriteMatch != null) {
            val (addrExpr, valueExpr) = memWriteMatch.destructured
            val addr = evaluateExpression(addrExpr, env).toInt()
            val value = evaluateExpression(valueExpr, env)
            env.writeByte(addr, value)
            return
        }
    }

    /**
     * Evaluate a Kotlin expression to a UByte value.
     */
    private fun evaluateExpression(expr: String, env: ExecutionEnvironment): UByte {
        val trimmed = expr.trim()

        // Literal values
        if (trimmed.startsWith("0x")) {
            return trimmed.substring(2).toInt(16).toUByte()
        }
        if (trimmed.all { it.isDigit() }) {
            return trimmed.toInt().toUByte()
        }

        // Variables
        when (trimmed) {
            "A" -> return env.A
            "X" -> return env.X
            "Y" -> return env.Y
        }

        // Memory read: memory[addr]
        val memReadPattern = """memory\[(.+?)\]""".toRegex()
        val memReadMatch = memReadPattern.find(trimmed)
        if (memReadMatch != null) {
            val addrExpr = memReadMatch.groupValues[1]
            val addr = evaluateExpression(addrExpr, env).toInt()
            return env.readByte(addr)
        }

        // Binary operations
        // Handle parentheses first
        if (trimmed.startsWith("(") && trimmed.endsWith(")")) {
            return evaluateExpression(trimmed.substring(1, trimmed.length - 1), env)
        }

        // Try to parse binary operations
        for (op in listOf("+", "-", "and", "or", "xor", "shl", "shr")) {
            val parts = splitByOperator(trimmed, op)
            if (parts.size == 2) {
                val left = evaluateExpression(parts[0], env)
                val right = evaluateExpression(parts[1], env)

                return when (op) {
                    "+" -> ((left.toInt() + right.toInt()) and 0xFF).toUByte()
                    "-" -> ((left.toInt() - right.toInt()) and 0xFF).toUByte()
                    "and" -> (left.toInt() and right.toInt()).toUByte()
                    "or" -> (left.toInt() or right.toInt()).toUByte()
                    "xor" -> (left.toInt() xor right.toInt()).toUByte()
                    "shl" -> ((left.toInt() shl right.toInt()) and 0xFF).toUByte()
                    "shr" -> (left.toInt() shr right.toInt()).toUByte()
                    else -> 0u
                }
            }
        }

        // Unknown expression
        return 0u
    }

    /**
     * Evaluate a boolean expression.
     */
    private fun evaluateBooleanExpression(expr: String, env: ExecutionEnvironment): Boolean {
        val trimmed = expr.trim()

        when (trimmed) {
            "true" -> return true
            "false" -> return false
            "N" -> return env.N
            "V" -> return env.V
            "Z" -> return env.Z
            "C" -> return env.C
        }

        // Comparisons
        for (op in listOf("==", "!=", ">", "<", ">=", "<=")) {
            val parts = splitByOperator(trimmed, op)
            if (parts.size == 2) {
                val left = evaluateExpression(parts[0], env).toInt()
                val right = evaluateExpression(parts[1], env).toInt()

                return when (op) {
                    "==" -> left == right
                    "!=" -> left != right
                    ">" -> left > right
                    "<" -> left < right
                    ">=" -> left >= right
                    "<=" -> left <= right
                    else -> false
                }
            }
        }

        return false
    }

    /**
     * Split an expression by an operator, respecting parentheses.
     */
    private fun splitByOperator(expr: String, operator: String): List<String> {
        var depth = 0
        var lastSplit = 0
        val parts = mutableListOf<String>()

        var i = 0
        while (i < expr.length) {
            when {
                expr[i] == '(' -> depth++
                expr[i] == ')' -> depth--
                depth == 0 && expr.substring(i).startsWith(operator) -> {
                    parts.add(expr.substring(lastSplit, i).trim())
                    lastSplit = i + operator.length
                    i += operator.length - 1
                }
            }
            i++
        }

        if (parts.isEmpty()) return listOf(expr)

        parts.add(expr.substring(lastSplit).trim())
        return parts
    }
}
