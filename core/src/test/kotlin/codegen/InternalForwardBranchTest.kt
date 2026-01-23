// by Claude - Tests for internal forward branch code generation
// These branches are not captured by control flow analysis but should still generate code
package com.ivieleague.decompiler6502tokotlin.codegen

import com.ivieleague.decompiler6502tokotlin.hand.*
import com.ivieleague.decompiler6502tokotlin.interpreter.Interpreter6502
import com.ivieleague.decompiler6502tokotlin.testing.EvaluatorState
import com.ivieleague.decompiler6502tokotlin.testing.KotlinAstEvaluator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

/**
 * Internal Forward Branch Tests
 *
 * Tests that internal forward branches (branches to labels within the same function)
 * generate proper conditional code, even when not captured by structured control flow analysis.
 *
 * The bug being fixed: orphaned branch handler in kotlin-codegen.kt was only generating code
 * for branches that exit the function or target break points, silently skipping internal
 * forward branches. This caused incorrect fall-through behavior.
 *
 * Example of the bug in ImposeFriction:
 * ```asm
 *    lda Player_X_Speed
 *    beq SetAbsSpd      ; branch if zero
 *    bpl RghtFrict      ; branch if positive
 *    bmi LeftFrict      ; branch if negative
 * JoypFrict: lsr
 * ```
 *
 * The bmi LeftFrict was generating only a comment, no actual branch code.
 */
class InternalForwardBranchTest {

    // =========================================================================
    // TEST HARNESS
    // =========================================================================

    // by Claude - Exceptions for control flow in evaluator
    class BreakException : Exception()
    class ReturnException : Exception()  // by Claude - Handle return statements

    class ControlFlowEvaluator(private val state: EvaluatorState) {
        private val baseEvaluator = KotlinAstEvaluator(state)
        var loopIterations = 0
        var maxIterations = 1000

        fun evaluate(stmt: KotlinStmt) {
            when (stmt) {
                // by Claude - Handle return statements to stop execution
                is KReturn -> throw ReturnException()
                is KWhile -> {
                    loopIterations = 0
                    try {
                        while (baseEvaluator.evaluateBoolExpr(stmt.condition)) {
                            if (loopIterations++ > maxIterations) {
                                throw RuntimeException("Infinite loop detected")
                            }
                            stmt.body.forEach { evaluate(it) }
                        }
                    } catch (e: BreakException) { }
                }
                is KDoWhile -> {
                    loopIterations = 0
                    try {
                        do {
                            if (loopIterations++ > maxIterations) {
                                throw RuntimeException("Infinite loop detected")
                            }
                            stmt.body.forEach { evaluate(it) }
                        } while (baseEvaluator.evaluateBoolExpr(stmt.condition))
                    } catch (e: BreakException) { }
                }
                is KLoop -> {
                    loopIterations = 0
                    try {
                        while (true) {
                            if (loopIterations++ > maxIterations) {
                                throw RuntimeException("Infinite loop detected")
                            }
                            stmt.body.forEach { evaluate(it) }
                        }
                    } catch (e: BreakException) { }
                }
                is KBreak -> throw BreakException()
                is KIf -> {
                    val cond = baseEvaluator.evaluateBoolExpr(stmt.condition)
                    if (cond) {
                        stmt.thenBranch.forEach { evaluate(it) }
                    } else {
                        stmt.elseBranch.forEach { evaluate(it) }
                    }
                }
                else -> baseEvaluator.evaluate(stmt)
            }
        }

        // by Claude - evaluateAll catches ReturnException to handle return statements
        fun evaluateAll(stmts: List<KotlinStmt>) {
            try {
                for (stmt in stmts) {
                    evaluate(stmt)
                }
            } catch (e: ReturnException) {
                // Return statement reached, stop execution normally
            }
        }
    }

    data class TestResult(
        val interpreterA: Int,
        val interpreterX: Int,
        val interpreterY: Int,
        val kotlinA: Int,
        val kotlinX: Int,
        val kotlinY: Int,
        val generatedCode: String,
        val matches: Boolean
    )

    private fun runInterpreter(
        asm: String,
        initialState: Map<String, Int> = emptyMap(),
        initialMemory: Map<Int, Int> = emptyMap(),
        maxInstructions: Int = 1000
    ): Triple<Int, Int, Int> {
        val code = asm.trimIndent().parseToAssemblyCodeFile()
        val interp = Interpreter6502()

        initialState["A"]?.let { interp.cpu.A = it.toUByte() }
        initialState["X"]?.let { interp.cpu.X = it.toUByte() }
        initialState["Y"]?.let { interp.cpu.Y = it.toUByte() }
        initialMemory.forEach { (addr, value) ->
            interp.memory.writeByte(addr, value.toUByte())
        }

        val labelToLine = mutableMapOf<String, Int>()
        code.lines.forEachIndexed { index, line ->
            line.label?.let { labelToLine[it] = index }
        }

        interp.labelResolver = { label ->
            if (label.startsWith("$")) {
                label.substring(1).toInt(16)
            } else {
                labelToLine[label] ?: label.toIntOrNull() ?: 0
            }
        }

        var pc = 0
        var instructionsExecuted = 0
        while (pc < code.lines.size && instructionsExecuted < maxInstructions) {
            val line = code.lines[pc]
            val instr = line.instruction
            if (instr != null) {
                when {
                    instr.op == AssemblyOp.RTS || instr.op == AssemblyOp.RTI -> break
                    instr.op == AssemblyOp.JMP -> {
                        val target = (instr.address as? AssemblyAddressing.Direct)?.label
                        if (target != null) {
                            val targetLine = labelToLine[target]
                            if (targetLine != null) {
                                pc = targetLine
                                instructionsExecuted++
                                continue
                            }
                        }
                    }
                    instr.op.isBranch -> {
                        val shouldBranch = when (instr.op) {
                            AssemblyOp.BEQ -> interp.cpu.Z
                            AssemblyOp.BNE -> !interp.cpu.Z
                            AssemblyOp.BCS -> interp.cpu.C
                            AssemblyOp.BCC -> !interp.cpu.C
                            AssemblyOp.BMI -> interp.cpu.N
                            AssemblyOp.BPL -> !interp.cpu.N
                            AssemblyOp.BVS -> interp.cpu.V
                            AssemblyOp.BVC -> !interp.cpu.V
                            else -> false
                        }
                        if (shouldBranch) {
                            val target = (instr.address as? AssemblyAddressing.Direct)?.label
                            if (target != null) {
                                val targetLine = labelToLine[target]
                                if (targetLine != null) {
                                    pc = targetLine
                                    instructionsExecuted++
                                    continue
                                }
                            }
                        }
                        interp.executeInstruction(instr)
                    }
                    else -> interp.executeInstruction(instr)
                }
            }
            pc++
            instructionsExecuted++
        }

        return Triple(interp.cpu.A.toInt(), interp.cpu.X.toInt(), interp.cpu.Y.toInt())
    }

    private fun decompileAndRun(
        asm: String,
        initialState: Map<String, Int> = emptyMap(),
        initialMemory: Map<Int, Int> = emptyMap(),
        debug: Boolean = false
    ): Pair<Triple<Int, Int, Int>, String> {
        val code = asm.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()

        if (blocks.isEmpty()) {
            return Pair(
                Triple(initialState["A"] ?: 0, initialState["X"] ?: 0, initialState["Y"] ?: 0),
                "// No blocks found"
            )
        }

        val startBlock = blocks.find { it.label == "Start" } ?: blocks[0]
        val func = AssemblyFunction(startBlock, emptyList())

        blocks.forEach { it.function = func }
        func.blocks = blocks.toSet()

        func.analyzeControls()

        if (debug) {
            System.err.println("DEBUG: Blocks found: ${blocks.size}")
            blocks.forEachIndexed { i, b ->
                System.err.println("  Block $i: label=${b.label}, lines=${b.lines.size}")
                b.lines.forEach { line ->
                    System.err.println("    ${line.instruction?.op} ${line.instruction?.address}")
                }
            }
        }

        // by Claude - Fix: set current function so fall-through checks work correctly
        val ctx = CodeGenContext(currentFunction = func)
        ctx.registerA = initialState["A"]?.let { KLiteral(it.toString()) }
        ctx.registerX = initialState["X"]?.let { KLiteral(it.toString()) }
        ctx.registerY = initialState["Y"]?.let { KLiteral(it.toString()) }

        // Set up memory for the evaluator
        val evalMemory = initialMemory.toMutableMap()

        val stmts = mutableListOf<KotlinStmt>()
        for (node in func.asControls ?: emptyList()) {
            stmts.addAll(node.toKotlin(ctx))
        }

        val generatedCode = stmts.joinToString("\n") { it.toKotlin() }

        if (debug) {
            System.err.println("\nDEBUG: Generated code:")
            System.err.println(generatedCode)
        }

        val evalState = EvaluatorState(
            A = initialState["A"] ?: 0,
            X = initialState["X"] ?: 0,
            Y = initialState["Y"] ?: 0,
            memory = evalMemory
        )

        val evaluator = ControlFlowEvaluator(evalState)
        try {
            evaluator.evaluateAll(stmts)
        } catch (e: Exception) {
            System.err.println("Evaluation error: ${e.message}")
        }

        return Pair(
            Triple(evalState.A, evalState.X, evalState.Y),
            generatedCode
        )
    }

    private fun verifyPattern(
        asm: String,
        initialState: Map<String, Int> = emptyMap(),
        initialMemory: Map<Int, Int> = emptyMap(),
        debug: Boolean = false
    ): TestResult {
        val (interpA, interpX, interpY) = runInterpreter(asm, initialState, initialMemory)
        val (kotlinResult, code) = decompileAndRun(asm, initialState, initialMemory, debug)
        val (kotlinA, kotlinX, kotlinY) = kotlinResult

        return TestResult(
            interpreterA = interpA,
            interpreterX = interpX,
            interpreterY = interpY,
            kotlinA = kotlinA,
            kotlinX = kotlinX,
            kotlinY = kotlinY,
            generatedCode = code,
            matches = interpA == kotlinA && interpX == kotlinX && interpY == kotlinY
        )
    }

    private fun TestResult.assertMatches(message: String = "") {
        if (!matches) {
            fail("""
                |$message
                |Control flow mismatch:
                |  Interpreter: A=$interpreterA, X=$interpreterX, Y=$interpreterY
                |  Kotlin:      A=$kotlinA, X=$kotlinX, Y=$kotlinY
                |
                |Generated code:
                |$generatedCode
            """.trimMargin())
        }
    }

    // =========================================================================
    // TEST CASE 1: Consecutive bpl/bmi branches (mutually exclusive)
    // =========================================================================

    @Test
    fun `bpl and bmi consecutive branches - positive value takes bpl path`() {
        // When value is positive (0x01-0x7F), bpl is taken
        val asm = """
            Start:
                LDA ${'$'}10           ; load value from memory
                BPL Positive      ; branch if positive
                BMI Negative      ; branch if negative (always taken after bpl fails)
                LDA #${'$'}FF         ; DEAD CODE - should never reach
                RTS
            Positive:
                LDA #${'$'}01         ; positive path
                RTS
            Negative:
                LDA #${'$'}02         ; negative path
                RTS
        """

        val result = verifyPattern(
            asm,
            initialMemory = mapOf(0x10 to 0x50)  // Positive value
        )
        assertEquals(0x01, result.interpreterA, "Interpreter: A should be 1 for positive input")
        result.assertMatches("Positive value should take bpl path")
    }

    @Test
    fun `bpl and bmi consecutive branches - negative value takes bmi path`() {
        // When value is negative (0x80-0xFF), bpl fails, bmi is taken
        val asm = """
            Start:
                LDA ${'$'}10           ; load value from memory
                BPL Positive      ; branch if positive (fails)
                BMI Negative      ; branch if negative (taken)
                LDA #${'$'}FF         ; DEAD CODE - should never reach
                RTS
            Positive:
                LDA #${'$'}01         ; positive path
                RTS
            Negative:
                LDA #${'$'}02         ; negative path
                RTS
        """

        val result = verifyPattern(
            asm,
            initialMemory = mapOf(0x10 to 0x80),  // Negative value
            debug = true
        )
        assertEquals(0x02, result.interpreterA, "Interpreter: A should be 2 for negative input")
        result.assertMatches("Negative value should take bmi path")
    }

    @Test
    fun `bpl and bmi consecutive branches - zero value takes bpl path`() {
        // Zero is considered non-negative (N flag clear), so bpl is taken
        val asm = """
            Start:
                LDA ${'$'}10           ; load value from memory
                BPL Positive      ; branch if positive (taken - zero is non-negative)
                BMI Negative      ; branch if negative
                LDA #${'$'}FF         ; DEAD CODE
                RTS
            Positive:
                LDA #${'$'}01         ; positive path (also zero path)
                RTS
            Negative:
                LDA #${'$'}02         ; negative path
                RTS
        """

        val result = verifyPattern(
            asm,
            initialMemory = mapOf(0x10 to 0x00)  // Zero value
        )
        assertEquals(0x01, result.interpreterA, "Interpreter: A should be 1 for zero input")
        result.assertMatches("Zero value should take bpl path (non-negative)")
    }

    // =========================================================================
    // TEST CASE 2: Consecutive bcs/bcc branches (mutually exclusive)
    // =========================================================================

    @Test
    fun `bcs and bcc consecutive branches - greater or equal takes bcs path`() {
        val asm = """
            Start:
                LDA ${'$'}10           ; load value
                CMP #${'$'}80          ; compare with 0x80
                BCS GreaterOrEqual
                BCC LessThan      ; always taken after bcs fails
                LDA #${'$'}FF         ; DEAD CODE
                RTS
            GreaterOrEqual:
                LDA #${'$'}01
                RTS
            LessThan:
                LDA #${'$'}02
                RTS
        """

        val result = verifyPattern(
            asm,
            initialMemory = mapOf(0x10 to 0x90)  // Greater than 0x80
        )
        assertEquals(0x01, result.interpreterA, "Interpreter: A should be 1 for value >= 0x80")
        result.assertMatches("Value >= 0x80 should take bcs path")
    }

    @Test
    fun `bcs and bcc consecutive branches - less than takes bcc path`() {
        val asm = """
            Start:
                LDA ${'$'}10           ; load value
                CMP #${'$'}80          ; compare with 0x80
                BCS GreaterOrEqual
                BCC LessThan      ; always taken after bcs fails
                LDA #${'$'}FF         ; DEAD CODE
                RTS
            GreaterOrEqual:
                LDA #${'$'}01
                RTS
            LessThan:
                LDA #${'$'}02
                RTS
        """

        val result = verifyPattern(
            asm,
            initialMemory = mapOf(0x10 to 0x50),  // Less than 0x80
            debug = true
        )
        assertEquals(0x02, result.interpreterA, "Interpreter: A should be 2 for value < 0x80")
        result.assertMatches("Value < 0x80 should take bcc path")
    }

    // =========================================================================
    // TEST CASE 3: Non-consecutive internal forward branch (beq)
    // =========================================================================

    @Test
    fun `beq internal forward branch - zero value takes branch`() {
        val asm = """
            Start:
                LDA ${'$'}10
                BEQ IsZero        ; internal forward branch
                LDA #${'$'}01          ; non-zero path
                RTS
            IsZero:
                LDA #${'$'}02          ; zero path
                RTS
        """

        val result = verifyPattern(
            asm,
            initialMemory = mapOf(0x10 to 0x00)  // Zero value
        )
        assertEquals(0x02, result.interpreterA, "Interpreter: A should be 2 for zero input")
        result.assertMatches("Zero value should branch to IsZero")
    }

    @Test
    fun `beq internal forward branch - non-zero value falls through`() {
        val asm = """
            Start:
                LDA ${'$'}10
                BEQ IsZero        ; internal forward branch (not taken)
                LDA #${'$'}01          ; non-zero path
                RTS
            IsZero:
                LDA #${'$'}02          ; zero path
                RTS
        """

        val result = verifyPattern(
            asm,
            initialMemory = mapOf(0x10 to 0x42)  // Non-zero value
        )
        assertEquals(0x01, result.interpreterA, "Interpreter: A should be 1 for non-zero input")
        result.assertMatches("Non-zero value should fall through")
    }

    // =========================================================================
    // TEST CASE 4: Triple-branch pattern (like ImposeFriction)
    // beq/bpl/bmi - all mutually exclusive
    // =========================================================================

    @Test
    fun `triple branch pattern - zero takes first branch`() {
        // Pattern from ImposeFriction
        val asm = """
            Start:
                LDA ${'$'}10
                BEQ ZeroPath      ; branch if zero
                BPL PositivePath  ; branch if positive
                BMI NegativePath  ; branch if negative
            FallThrough:
                LDA #${'$'}FF         ; DEAD CODE
                RTS
            ZeroPath:
                LDA #${'$'}01
                RTS
            PositivePath:
                LDA #${'$'}02
                RTS
            NegativePath:
                LDA #${'$'}03
                RTS
        """

        val result = verifyPattern(
            asm,
            initialMemory = mapOf(0x10 to 0x00)  // Zero
        )
        assertEquals(0x01, result.interpreterA, "Interpreter: A should be 1 for zero")
        result.assertMatches("Zero should take ZeroPath")
    }

    @Test
    fun `triple branch pattern - positive takes second branch`() {
        val asm = """
            Start:
                LDA ${'$'}10
                BEQ ZeroPath      ; branch if zero (not taken)
                BPL PositivePath  ; branch if positive (taken)
                BMI NegativePath  ; branch if negative
            FallThrough:
                LDA #${'$'}FF         ; DEAD CODE
                RTS
            ZeroPath:
                LDA #${'$'}01
                RTS
            PositivePath:
                LDA #${'$'}02
                RTS
            NegativePath:
                LDA #${'$'}03
                RTS
        """

        val result = verifyPattern(
            asm,
            initialMemory = mapOf(0x10 to 0x50)  // Positive (non-zero)
        )
        assertEquals(0x02, result.interpreterA, "Interpreter: A should be 2 for positive")
        result.assertMatches("Positive should take PositivePath")
    }

    @Test
    fun `triple branch pattern - negative takes third branch`() {
        val asm = """
            Start:
                LDA ${'$'}10
                BEQ ZeroPath      ; branch if zero (not taken)
                BPL PositivePath  ; branch if positive (not taken)
                BMI NegativePath  ; branch if negative (taken)
            FallThrough:
                LDA #${'$'}FF         ; DEAD CODE
                RTS
            ZeroPath:
                LDA #${'$'}01
                RTS
            PositivePath:
                LDA #${'$'}02
                RTS
            NegativePath:
                LDA #${'$'}03
                RTS
        """

        val result = verifyPattern(
            asm,
            initialMemory = mapOf(0x10 to 0x80),  // Negative
            debug = true
        )
        assertEquals(0x03, result.interpreterA, "Interpreter: A should be 3 for negative")
        result.assertMatches("Negative should take NegativePath")
    }

    // =========================================================================
    // TEST CASE 5: Internal forward branch within a block that has other code after
    // =========================================================================

    @Test
    fun `internal forward branch with code after branch point`() {
        val asm = """
            Start:
                LDA ${'$'}10
                BEQ SkipInc       ; skip the increment if zero
                INC ${'$'}20          ; only execute if non-zero
            SkipInc:
                LDA ${'$'}20          ; load the (possibly incremented) value
                RTS
        """

        // When input is zero, skip the INC
        val result1 = verifyPattern(
            asm,
            initialMemory = mapOf(0x10 to 0x00, 0x20 to 0x05)
        )
        assertEquals(0x05, result1.interpreterA, "A should be 5 (no increment)")
        result1.assertMatches("Zero input should skip INC")

        // When input is non-zero, execute the INC
        val result2 = verifyPattern(
            asm,
            initialMemory = mapOf(0x10 to 0x42, 0x20 to 0x05)
        )
        assertEquals(0x06, result2.interpreterA, "A should be 6 (incremented)")
        result2.assertMatches("Non-zero input should execute INC")
    }
}
