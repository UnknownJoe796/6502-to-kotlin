// by Claude - Test harness for verifying 6502 instruction semantics
package com.ivieleague.decompiler6502tokotlin.testing

import com.ivieleague.decompiler6502tokotlin.hand.*
import com.ivieleague.decompiler6502tokotlin.interpreter.CPU6502
import com.ivieleague.decompiler6502tokotlin.interpreter.Interpreter6502
import com.ivieleague.decompiler6502tokotlin.interpreter.Memory6502

/**
 * Test harness for verifying that generated Kotlin code produces the same
 * results as the 6502 interpreter.
 *
 * This is the foundation for Phase 1 of the decompiler correctness plan:
 * ensuring every 6502 instruction produces equivalent Kotlin.
 */
class InstructionTestHarness {

    /**
     * CPU state snapshot for comparison.
     */
    data class CpuSnapshot(
        val A: Int,
        val X: Int,
        val Y: Int,
        val Z: Boolean,  // Zero flag
        val N: Boolean,  // Negative flag
        val C: Boolean,  // Carry flag
        val V: Boolean   // Overflow flag
    ) {
        companion object {
            fun from(cpu: CPU6502) = CpuSnapshot(
                A = cpu.A.toInt(),
                X = cpu.X.toInt(),
                Y = cpu.Y.toInt(),
                Z = cpu.Z,
                N = cpu.N,
                C = cpu.C,
                V = cpu.V
            )

            fun from(state: EvaluatorState) = CpuSnapshot(
                A = state.A,
                X = state.X,
                Y = state.Y,
                Z = state.zeroFlag,
                N = state.negativeFlag,
                C = state.carryFlag,
                V = state.overflowFlag
            )
        }

        /**
         * Returns a descriptive diff between this and another snapshot.
         */
        fun diff(other: CpuSnapshot): String {
            val diffs = mutableListOf<String>()
            if (A != other.A) diffs.add("A: $A vs ${other.A}")
            if (X != other.X) diffs.add("X: $X vs ${other.X}")
            if (Y != other.Y) diffs.add("Y: $Y vs ${other.Y}")
            if (Z != other.Z) diffs.add("Z: $Z vs ${other.Z}")
            if (N != other.N) diffs.add("N: $N vs ${other.N}")
            if (C != other.C) diffs.add("C: $C vs ${other.C}")
            if (V != other.V) diffs.add("V: $V vs ${other.V}")
            return if (diffs.isEmpty()) "identical" else diffs.joinToString(", ")
        }
    }

    /**
     * Initial state for a test case.
     */
    data class InitialState(
        val A: Int = 0,
        val X: Int = 0,
        val Y: Int = 0,
        val C: Boolean = false,
        val Z: Boolean = false,
        val N: Boolean = false,
        val V: Boolean = false,
        val memory: Map<Int, Int> = emptyMap()
    )

    /**
     * Run a single instruction through the interpreter and return the final state.
     */
    fun runInterpreter(instruction: AssemblyInstruction, initial: InitialState): CpuSnapshot {
        val interp = Interpreter6502()

        // Set initial state
        interp.cpu.A = initial.A.toUByte()
        interp.cpu.X = initial.X.toUByte()
        interp.cpu.Y = initial.Y.toUByte()
        interp.cpu.C = initial.C
        interp.cpu.Z = initial.Z
        interp.cpu.N = initial.N
        interp.cpu.V = initial.V

        // Set up memory
        for ((addr, value) in initial.memory) {
            interp.memory.writeByte(addr, value.toUByte())
        }

        // Set up label resolver for hex addresses
        interp.labelResolver = { label ->
            if (label.startsWith("$")) {
                label.substring(1).toInt(16)
            } else {
                initial.memory.keys.find { addr ->
                    addr.toString(16).uppercase() == label.uppercase()
                } ?: label.toIntOrNull() ?: 0
            }
        }

        // Execute the instruction
        interp.executeInstruction(instruction)

        return CpuSnapshot.from(interp.cpu)
    }

    /**
     * Generate Kotlin code for an instruction and evaluate it.
     *
     * The code generation system works by:
     * 1. Generating explicit statements (KVarDecl, KAssignment) for some operations
     * 2. Tracking register/flag values as expressions in the context for others
     *
     * We need to handle both patterns:
     * - Explicit statements are evaluated directly
     * - Context expressions are evaluated after statement execution
     */
    fun runKotlin(instruction: AssemblyInstruction, initial: InitialState): Pair<CpuSnapshot, List<String>> {
        // Create code generation context
        val ctx = CodeGenContext()

        // Initialize context with initial state as expressions
        ctx.registerA = KLiteral(initial.A.toString())
        ctx.registerX = KLiteral(initial.X.toString())
        ctx.registerY = KLiteral(initial.Y.toString())
        ctx.carryFlag = KLiteral(if (initial.C) "true" else "false")
        ctx.zeroFlag = KLiteral(if (initial.Z) "true" else "false")
        ctx.negativeFlag = KLiteral(if (initial.N) "true" else "false")
        ctx.overflowFlag = KLiteral(if (initial.V) "true" else "false")

        // Generate Kotlin statements
        val stmts = instruction.toKotlin(ctx)

        // Create evaluator state with initial values
        val evalState = EvaluatorState(
            A = initial.A,
            X = initial.X,
            Y = initial.Y,
            carryFlag = initial.C,
            zeroFlag = initial.Z,
            negativeFlag = initial.N,
            overflowFlag = initial.V,
            memory = initial.memory.toMutableMap()
        )

        // Evaluate the explicit statements first
        val evaluator = KotlinAstEvaluator(evalState)
        val log = mutableListOf<String>()
        for (stmt in stmts) {
            log.add(stmt.toKotlin())
            evaluator.evaluate(stmt)
        }

        // After evaluating statements, evaluate the final register/flag expressions
        // from the context. This handles cases where codegen tracks values as expressions
        // rather than explicit assignments (e.g., ADC stores result in ctx.registerA).
        ctx.registerA?.let {
            val value = evaluator.evaluateIntExpr(it)
            evalState.A = value and 0xFF  // Mask to 8 bits
            evalState.variables["A"] = evalState.A
            log.add("// ctx.registerA = ${it.toKotlin()} -> $value")
        }
        ctx.registerX?.let {
            val value = evaluator.evaluateIntExpr(it)
            evalState.X = value and 0xFF
            evalState.variables["X"] = evalState.X
            log.add("// ctx.registerX = ${it.toKotlin()} -> $value")
        }
        ctx.registerY?.let {
            val value = evaluator.evaluateIntExpr(it)
            evalState.Y = value and 0xFF
            evalState.variables["Y"] = evalState.Y
            log.add("// ctx.registerY = ${it.toKotlin()} -> $value")
        }

        // Evaluate final flag expressions from context
        ctx.zeroFlag?.let { evalState.zeroFlag = evaluator.evaluateBoolExpr(it) }
        ctx.carryFlag?.let { evalState.carryFlag = evaluator.evaluateBoolExpr(it) }
        ctx.negativeFlag?.let { evalState.negativeFlag = evaluator.evaluateBoolExpr(it) }
        ctx.overflowFlag?.let { evalState.overflowFlag = evaluator.evaluateBoolExpr(it) }

        return Pair(CpuSnapshot.from(evalState), log)
    }

    /**
     * Verify that interpreter and generated Kotlin produce the same result.
     */
    fun verifyInstruction(
        instruction: AssemblyInstruction,
        initial: InitialState = InitialState()
    ): VerificationResult {
        val interpResult = runInterpreter(instruction, initial)
        val (kotlinResult, kotlinCode) = runKotlin(instruction, initial)

        val matches = interpResult == kotlinResult

        return VerificationResult(
            instruction = instruction,
            initial = initial,
            interpreterResult = interpResult,
            kotlinResult = kotlinResult,
            kotlinCode = kotlinCode,
            matches = matches
        )
    }

    data class VerificationResult(
        val instruction: AssemblyInstruction,
        val initial: InitialState,
        val interpreterResult: CpuSnapshot,
        val kotlinResult: CpuSnapshot,
        val kotlinCode: List<String>,
        val matches: Boolean
    ) {
        fun assertMatches() {
            if (!matches) {
                val diff = interpreterResult.diff(kotlinResult)
                val codeStr = kotlinCode.joinToString("\n  ")
                throw AssertionError("""
                    |Instruction: ${instruction.op} ${instruction.address}
                    |Initial: A=${initial.A}, X=${initial.X}, Y=${initial.Y}, C=${initial.C}, Z=${initial.Z}, N=${initial.N}
                    |Generated code:
                    |  $codeStr
                    |Expected (interpreter): $interpreterResult
                    |Actual (kotlin): $kotlinResult
                    |Differences: $diff
                """.trimMargin())
            }
        }
    }
}

/**
 * Mutable state used by the AST evaluator.
 */
class EvaluatorState(
    var A: Int = 0,
    var X: Int = 0,
    var Y: Int = 0,
    var carryFlag: Boolean = false,
    var zeroFlag: Boolean = false,
    var negativeFlag: Boolean = false,
    var overflowFlag: Boolean = false,
    val memory: MutableMap<Int, Int> = mutableMapOf(),
    val variables: MutableMap<String, Int> = mutableMapOf()
)

/**
 * Evaluator for Kotlin AST nodes.
 * This interprets the generated Kotlin AST against an EvaluatorState.
 */
class KotlinAstEvaluator(private val state: EvaluatorState) {

    fun evaluate(stmt: KotlinStmt) {
        when (stmt) {
            is KVarDecl -> {
                val value = stmt.value?.let { evaluateIntExpr(it) } ?: 0
                state.variables[stmt.name] = value
                // Also update registers if this is a register variable
                when (stmt.name) {
                    "A" -> state.A = value
                    "X" -> state.X = value
                    "Y" -> state.Y = value
                }
            }
            is KAssignment -> {
                val value = evaluateIntExpr(stmt.value)
                when (val target = stmt.target) {
                    is KVar -> {
                        state.variables[target.name] = value
                        when (target.name) {
                            "A" -> state.A = value
                            "X" -> state.X = value
                            "Y" -> state.Y = value
                        }
                    }
                    is KMemberAccess -> {
                        if (target.isIndexed) {
                            val addr = evaluateIntExpr(target.member)
                            state.memory[addr] = value and 0xFF
                        }
                    }
                    else -> { /* Other targets not yet supported */ }
                }
            }
            is KExprStmt -> {
                // Just evaluate for side effects (like push/pull)
                evaluateIntExpr(stmt.expr)
            }
            is KComment -> { /* Ignore comments */ }
            is KIf -> {
                val cond = evaluateBoolExpr(stmt.condition)
                if (cond) {
                    stmt.thenBranch.forEach { evaluate(it) }
                } else {
                    stmt.elseBranch.forEach { evaluate(it) }
                }
            }
            else -> { /* Other statements not yet supported */ }
        }
    }

    fun evaluateIntExpr(expr: KotlinExpr): Int {
        return when (expr) {
            is KLiteral -> parseLiteral(expr.value)
            is KVar -> when (expr.name) {
                "A" -> state.A
                "X" -> state.X
                "Y" -> state.Y
                else -> state.variables[expr.name] ?: 0
            }
            is KBinaryOp -> {
                val left = evaluateIntExpr(expr.left)
                val right = evaluateIntExpr(expr.right)
                when (expr.op) {
                    "+" -> left + right
                    "-" -> left - right
                    "*" -> left * right
                    "/" -> if (right != 0) left / right else 0
                    "and" -> left and right
                    "or" -> left or right
                    "xor" -> left xor right
                    "shl" -> left shl right
                    "shr" -> left shr right
                    "ushr" -> left ushr right
                    else -> 0
                }
            }
            is KUnaryOp -> {
                val operand = evaluateIntExpr(expr.expr)
                when (expr.op) {
                    "-" -> -operand
                    "inv" -> operand.inv()
                    else -> operand
                }
            }
            is KParen -> evaluateIntExpr(expr.expr)
            is KMemberAccess -> {
                when {
                    expr.isIndexed -> {
                        // Indexed access: memory[addr] or array[index]
                        val addr = evaluateIntExpr(expr.member)
                        state.memory[addr] ?: 0
                    }
                    expr.member is KCall && (expr.member as KCall).name == "toInt" -> {
                        // .toInt() call on some expression - evaluate the receiver
                        evaluateIntExpr(expr.receiver)
                    }
                    expr.member is KCall && (expr.member as KCall).name == "toUByte" -> {
                        // .toUByte() call - evaluate receiver and mask to byte
                        evaluateIntExpr(expr.receiver) and 0xFF
                    }
                    else -> 0
                }
            }
            is KIfExpr -> {
                val cond = evaluateBoolExpr(expr.condition)
                if (cond) evaluateIntExpr(expr.thenExpr) else evaluateIntExpr(expr.elseExpr)
            }
            is KCall -> {
                // Handle special functions
                when (expr.name) {
                    "push" -> {
                        // Push to stack - simplified, just record in memory
                        val value = evaluateIntExpr(expr.args[0])
                        // Stack push not tracked in this simple evaluator
                        value
                    }
                    "pull" -> {
                        // Pull from stack - simplified
                        0
                    }
                    else -> 0
                }
            }
            else -> 0
        }
    }

    fun evaluateBoolExpr(expr: KotlinExpr): Boolean {
        return when (expr) {
            is KLiteral -> expr.value == "true" || expr.value == "1"
            is KVar -> when (expr.name) {
                "flagC", "carryFlag" -> state.carryFlag
                "flagZ", "zeroFlag" -> state.zeroFlag
                "flagN", "negativeFlag" -> state.negativeFlag
                "flagV", "overflowFlag" -> state.overflowFlag
                else -> state.variables[expr.name]?.let { it != 0 } ?: false
            }
            is KBinaryOp -> {
                when (expr.op) {
                    "==" -> evaluateIntExpr(expr.left) == evaluateIntExpr(expr.right)
                    "!=" -> evaluateIntExpr(expr.left) != evaluateIntExpr(expr.right)
                    "<" -> evaluateIntExpr(expr.left) < evaluateIntExpr(expr.right)
                    ">" -> evaluateIntExpr(expr.left) > evaluateIntExpr(expr.right)
                    "<=" -> evaluateIntExpr(expr.left) <= evaluateIntExpr(expr.right)
                    ">=" -> evaluateIntExpr(expr.left) >= evaluateIntExpr(expr.right)
                    "&&", "and" -> evaluateBoolExpr(expr.left) && evaluateBoolExpr(expr.right)
                    "||", "or" -> evaluateBoolExpr(expr.left) || evaluateBoolExpr(expr.right)
                    else -> false
                }
            }
            is KUnaryOp -> {
                if (expr.op == "!") {
                    !evaluateBoolExpr(expr.expr)
                } else {
                    false
                }
            }
            is KParen -> evaluateBoolExpr(expr.expr)
            is KCall -> {
                // Boolean-returning calls
                false
            }
            else -> false
        }
    }

    private fun parseLiteral(value: String): Int {
        return when {
            value == "true" -> 1
            value == "false" -> 0
            value.startsWith("0x") -> value.substring(2).toInt(16)
            value.startsWith("0b") -> value.substring(2).toInt(2)
            value.startsWith("-") -> -(parseLiteral(value.substring(1)))
            else -> value.toIntOrNull() ?: 0
        }
    }
}

// ===== Convenience DSL for creating test cases =====

/**
 * Create an immediate byte value addressing mode.
 */
fun imm(value: Int) = AssemblyAddressing.ByteValue(value.toUByte(), AssemblyAddressing.Radix.Hex)

/**
 * Create a direct memory addressing mode.
 */
fun direct(addr: Int) = AssemblyAddressing.Direct("\$${addr.toString(16).padStart(4, '0').uppercase()}")

/**
 * Create a direct X-indexed memory addressing mode.
 */
fun directX(addr: Int) = AssemblyAddressing.DirectX("\$${addr.toString(16).padStart(4, '0').uppercase()}")

/**
 * Create a direct Y-indexed memory addressing mode.
 */
fun directY(addr: Int) = AssemblyAddressing.DirectY("\$${addr.toString(16).padStart(4, '0').uppercase()}")

/**
 * Create an instruction.
 */
fun instr(op: AssemblyOp, addr: AssemblyAddressing? = null) = AssemblyInstruction(op, addr)
