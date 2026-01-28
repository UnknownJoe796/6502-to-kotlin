// by Claude - Phase 3: Control Flow Pattern Verification
// Tests that verify decompiled control flow produces the same results as the interpreter
package com.ivieleague.decompiler6502tokotlin.decompiler

import com.ivieleague.decompiler6502tokotlin.hand.*
import com.ivieleague.decompiler6502tokotlin.interpreter.Interpreter6502
import com.ivieleague.decompiler6502tokotlin.testing.EvaluatorState
import com.ivieleague.decompiler6502tokotlin.testing.KotlinAstEvaluator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Control Flow Pattern Verification Test Suite
 *
 * Tests that structured control flow (if/else/while/do-while) decompiles correctly
 * by comparing the interpreter execution against the generated Kotlin code.
 *
 * Each test:
 * 1. Runs assembly through the interpreter to get ground truth
 * 2. Decompiles the assembly to Kotlin
 * 3. Evaluates the generated Kotlin
 * 4. Compares final state (registers, memory, iteration counts)
 */
class ControlFlowPatternsTest {

    // =====================================================================
    // TEST HARNESS: Extended evaluator with control flow support
    // =====================================================================

    /**
     * Extended Kotlin AST evaluator that handles control flow statements.
     */
    // by Claude - Exceptions for control flow in loops and functions
    class BreakException(val label: String? = null) : Exception()
    class ContinueException(val label: String? = null) : Exception()
    class ReturnException : Exception()  // by Claude - For early returns

    class ControlFlowEvaluator(private val state: EvaluatorState) {
        private val baseEvaluator = KotlinAstEvaluator(state)
        var loopIterations = 0
        var maxIterations = 1000 // Safety limit

        fun evaluate(stmt: KotlinStmt) {
            when (stmt) {
                is KWhile -> {
                    loopIterations = 0
                    try {
                        while (baseEvaluator.evaluateBoolExpr(stmt.condition)) {
                            if (loopIterations++ > maxIterations) {
                                throw RuntimeException("Infinite loop detected in KWhile")
                            }
                            try {
                                stmt.body.forEach { evaluate(it) }
                            } catch (e: ContinueException) {
                                // Continue to next iteration
                            }
                        }
                    } catch (e: BreakException) {
                        // Break exits the loop
                    }
                }
                // by Claude - Handle KLabeledWhile (same as KWhile but with label support)
                is KLabeledWhile -> {
                    loopIterations = 0
                    try {
                        while (baseEvaluator.evaluateBoolExpr(stmt.condition)) {
                            if (loopIterations++ > maxIterations) {
                                throw RuntimeException("Infinite loop detected in KLabeledWhile")
                            }
                            try {
                                stmt.body.forEach { evaluate(it) }
                            } catch (e: ContinueException) {
                                // Continue to next iteration
                            }
                        }
                    } catch (e: BreakException) {
                        // Break exits the loop
                    }
                }
                is KDoWhile -> {
                    loopIterations = 0
                    try {
                        do {
                            if (loopIterations++ > maxIterations) {
                                throw RuntimeException("Infinite loop detected in KDoWhile")
                            }
                            try {
                                stmt.body.forEach { evaluate(it) }
                            } catch (e: ContinueException) {
                                // Continue to next iteration (check condition)
                            }
                        } while (baseEvaluator.evaluateBoolExpr(stmt.condition))
                    } catch (e: BreakException) {
                        // Break exits the loop
                    }
                }
                // by Claude - Handle KLabeledDoWhile (same as KDoWhile but with label support)
                is KLabeledDoWhile -> {
                    loopIterations = 0
                    try {
                        do {
                            if (loopIterations++ > maxIterations) {
                                throw RuntimeException("Infinite loop detected in KLabeledDoWhile")
                            }
                            try {
                                stmt.body.forEach { evaluate(it) }
                            } catch (e: ContinueException) {
                                // Continue to next iteration (check condition)
                            }
                        } while (baseEvaluator.evaluateBoolExpr(stmt.condition))
                    } catch (e: BreakException) {
                        // Break exits the loop
                    }
                }
                is KLoop -> {
                    // by Claude - Bug 2 fix: Actually execute KLoop with break support
                    loopIterations = 0
                    try {
                        while (true) {
                            if (loopIterations++ > maxIterations) {
                                throw RuntimeException("Infinite loop detected in KLoop")
                            }
                            try {
                                stmt.body.forEach { evaluate(it) }
                            } catch (e: ContinueException) {
                                // Continue to next iteration
                            }
                        }
                    } catch (e: BreakException) {
                        // Break exits the loop
                    }
                }
                is KBreak -> {
                    throw BreakException()
                }
                // by Claude - Handle labeled break/continue
                is KLabeledBreak -> {
                    throw BreakException(stmt.label)
                }
                is KLabeledContinue -> {
                    throw ContinueException(stmt.label)
                }
                is KIf -> {
                    val cond = baseEvaluator.evaluateBoolExpr(stmt.condition)
                    if (cond) {
                        stmt.thenBranch.forEach { evaluate(it) }
                    } else {
                        stmt.elseBranch.forEach { evaluate(it) }
                    }
                }
                // by Claude - Handle KReturn by throwing ReturnException to stop execution
                is KReturn -> {
                    throw ReturnException()
                }
                else -> baseEvaluator.evaluate(stmt)
            }
        }

        fun evaluateAll(stmts: List<KotlinStmt>) {
            // by Claude - Catch ReturnException to handle early returns
            try {
                for (stmt in stmts) {
                    evaluate(stmt)
                }
            } catch (e: ReturnException) {
                // Function returned early - this is normal for early exit patterns
            }
        }
    }

    /**
     * Test result comparing interpreter and Kotlin execution.
     */
    data class ControlFlowTestResult(
        val interpreterState: InterpreterState,
        val kotlinState: KotlinState,
        val generatedCode: String,
        val matches: Boolean,
        val differences: String
    )

    data class InterpreterState(
        val A: Int,
        val X: Int,
        val Y: Int,
        val memory: Map<Int, Int>,
        val instructionsExecuted: Int
    )

    data class KotlinState(
        val A: Int,
        val X: Int,
        val Y: Int,
        val memory: Map<Int, Int>,
        val loopIterations: Int
    )

    /**
     * Run assembly through the interpreter.
     */
    private fun runInterpreter(
        asm: String,
        initialState: Map<String, Int> = emptyMap(),
        initialMemory: Map<Int, Int> = emptyMap(),
        maxInstructions: Int = 1000
    ): InterpreterState {
        val code = asm.trimIndent().parseToAssemblyCodeFile()
        val interp = Interpreter6502()

        // Set initial state
        initialState["A"]?.let { interp.cpu.A = it.toUByte() }
        initialState["X"]?.let { interp.cpu.X = it.toUByte() }
        initialState["Y"]?.let { interp.cpu.Y = it.toUByte() }
        initialMemory.forEach { (addr, value) ->
            interp.memory.writeByte(addr, value.toUByte())
        }

        // Build label table for the interpreter
        val labelToLine = mutableMapOf<String, Int>()
        code.lines.forEachIndexed { index, line ->
            line.label?.let { labelToLine[it] = index }
        }

        interp.labelResolver = { label ->
            if (label.startsWith("$")) {
                label.substring(1).toInt(16)
            } else {
                // Return line index for labels
                labelToLine[label] ?: label.toIntOrNull() ?: 0
            }
        }

        // Execute instructions
        var pc = 0
        var instructionsExecuted = 0
        while (pc < code.lines.size && instructionsExecuted < maxInstructions) {
            val line = code.lines[pc]
            val instr = line.instruction
            if (instr != null) {
                // Handle branches and jumps
                when {
                    instr.op == AssemblyOp.RTS || instr.op == AssemblyOp.RTI -> {
                        // Return - stop execution
                        break
                    }
                    instr.op == AssemblyOp.JMP -> {
                        // Unconditional jump
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
                        // Conditional branch
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
                        // Execute instruction for flag effects
                        interp.executeInstruction(instr)
                    }
                    else -> {
                        // Normal instruction
                        interp.executeInstruction(instr)
                    }
                }
            }
            pc++
            instructionsExecuted++
        }

        // Collect final memory state (only non-zero values)
        val finalMemory = mutableMapOf<Int, Int>()
        for (addr in 0..0x7FF) { // Zero page and some RAM
            val value = interp.memory.readByte(addr).toInt()
            if (value != 0) {
                finalMemory[addr] = value
            }
        }
        // Also include any addresses from initial memory
        initialMemory.keys.forEach { addr ->
            finalMemory[addr] = interp.memory.readByte(addr).toInt()
        }

        return InterpreterState(
            A = interp.cpu.A.toInt(),
            X = interp.cpu.X.toInt(),
            Y = interp.cpu.Y.toInt(),
            memory = finalMemory,
            instructionsExecuted = instructionsExecuted
        )
    }

    /**
     * Decompile assembly to Kotlin and evaluate it.
     */
    private fun decompileAndRun(
        asm: String,
        initialState: Map<String, Int> = emptyMap(),
        initialMemory: Map<Int, Int> = emptyMap(),
        debug: Boolean = false
    ): Pair<KotlinState, String> {
        val code = asm.trimIndent().parseToAssemblyCodeFile()

        // Build the decompilation pipeline
        val blocks = code.lines.blockify()
        blocks.dominators()

        if (blocks.isEmpty()) {
            return Pair(
                KotlinState(
                    A = initialState["A"] ?: 0,
                    X = initialState["X"] ?: 0,
                    Y = initialState["Y"] ?: 0,
                    memory = initialMemory,
                    loopIterations = 0
                ),
                "// No blocks found"
            )
        }

        // For test patterns, treat all blocks as a single function starting at the first block
        // This avoids issues with functionify splitting our test patterns
        val startBlock = blocks.find { it.label == "Start" } ?: blocks[0]
        val func = AssemblyFunction(startBlock, emptyList())

        // Assign all blocks to this function
        blocks.forEach { it.function = func }
        func.blocks = blocks.toSet()

        func.analyzeControls()

        if (debug) {
            System.err.println("DEBUG: Blocks found: ${blocks.size}")
            blocks.forEachIndexed { i, b ->
                System.err.println("  Block $i: label=${b.label}, lines=${b.lines.size}, " +
                    "ft=${b.fallThroughExit?.label ?: b.fallThroughExit?.originalLineIndex}, " +
                    "br=${b.branchExit?.label ?: b.branchExit?.originalLineIndex}")
            }
            System.err.println("DEBUG: Selected function: ${func.startingBlock.label}")
            System.err.println("DEBUG: Control nodes: ${func.asControls?.size}")
            func.asControls?.forEachIndexed { i, node ->
                System.err.println("  Node $i: ${node::class.simpleName} - entry=${node.entry.label ?: node.entry.originalLineIndex}")
            }
        }

        val ctx = CodeGenContext()
        ctx.registerA = initialState["A"]?.let { KLiteral(it.toString()) }
        ctx.registerX = initialState["X"]?.let { KLiteral(it.toString()) }
        ctx.registerY = initialState["Y"]?.let { KLiteral(it.toString()) }

        val stmts = mutableListOf<KotlinStmt>()
        for (node in func.asControls ?: emptyList()) {
            stmts.addAll(node.toKotlin(ctx))
        }

        val generatedCode = stmts.joinToString("\n") { it.toKotlin() }

        // Evaluate the Kotlin
        val evalState = EvaluatorState(
            A = initialState["A"] ?: 0,
            X = initialState["X"] ?: 0,
            Y = initialState["Y"] ?: 0,
            memory = initialMemory.toMutableMap()
        )

        val evaluator = ControlFlowEvaluator(evalState)
        try {
            evaluator.evaluateAll(stmts)
        } catch (e: Exception) {
            // Log the error but continue with partial state
            System.err.println("Evaluation error: ${e.message}")
        }

        return Pair(
            KotlinState(
                A = evalState.A,
                X = evalState.X,
                Y = evalState.Y,
                memory = evalState.memory.toMap(),
                loopIterations = evaluator.loopIterations
            ),
            generatedCode
        )
    }

    /**
     * Compare interpreter and Kotlin results.
     */
    private fun verifyPattern(
        asm: String,
        initialState: Map<String, Int> = emptyMap(),
        initialMemory: Map<Int, Int> = emptyMap(),
        checkRegisters: Set<String> = setOf("A", "X", "Y"),
        checkMemory: Set<Int> = emptySet()
    ): ControlFlowTestResult {
        val interpResult = runInterpreter(asm, initialState, initialMemory)
        val (kotlinResult, code) = decompileAndRun(asm, initialState, initialMemory)

        val diffs = mutableListOf<String>()

        if ("A" in checkRegisters && interpResult.A != kotlinResult.A) {
            diffs.add("A: interp=${interpResult.A}, kotlin=${kotlinResult.A}")
        }
        if ("X" in checkRegisters && interpResult.X != kotlinResult.X) {
            diffs.add("X: interp=${interpResult.X}, kotlin=${kotlinResult.X}")
        }
        if ("Y" in checkRegisters && interpResult.Y != kotlinResult.Y) {
            diffs.add("Y: interp=${interpResult.Y}, kotlin=${kotlinResult.Y}")
        }

        for (addr in checkMemory) {
            val interpVal = interpResult.memory[addr] ?: 0
            val kotlinVal = kotlinResult.memory[addr] ?: 0
            if (interpVal != kotlinVal) {
                diffs.add("mem[$addr]: interp=$interpVal, kotlin=$kotlinVal")
            }
        }

        return ControlFlowTestResult(
            interpreterState = interpResult,
            kotlinState = kotlinResult,
            generatedCode = code,
            matches = diffs.isEmpty(),
            differences = diffs.joinToString(", ")
        )
    }

    private fun ControlFlowTestResult.assertMatches() {
        if (!matches) {
            fail("""
                |Control flow pattern mismatch:
                |Differences: $differences
                |
                |Generated code:
                |$generatedCode
                |
                |Interpreter: A=${interpreterState.A}, X=${interpreterState.X}, Y=${interpreterState.Y}
                |Kotlin:      A=${kotlinState.A}, X=${kotlinState.X}, Y=${kotlinState.Y}
            """.trimMargin())
        }
    }

    // =====================================================================
    // PATTERN 1: IF-THEN (Forward Branch)
    // =====================================================================

    @Test
    fun `if-then - BEQ skip when zero`() {
        val asm = """
            Start:
                LDA #${'$'}00
                BEQ Skip
                LDA #${'$'}FF
            Skip:
                RTS
        """

        val result = verifyPattern(asm)
        assertEquals(0, result.interpreterState.A, "A should remain 0 (branch taken)")
        result.assertMatches()
    }

    @Test
    fun `if-then - BEQ not taken when non-zero`() {
        val asm = """
            Start:
                LDA #${'$'}05
                BEQ Skip
                LDA #${'$'}FF
            Skip:
                RTS
        """

        val result = verifyPattern(asm)
        assertEquals(0xFF, result.interpreterState.A, "A should be 0xFF (branch not taken)")
        result.assertMatches()
    }

    @Test
    fun `if-then - BNE skip when non-zero`() {
        val asm = """
            Start:
                LDA #${'$'}05
                BNE Skip
                LDA #${'$'}FF
            Skip:
                RTS
        """

        val result = verifyPattern(asm)
        assertEquals(5, result.interpreterState.A, "A should remain 5 (branch taken)")
        result.assertMatches()
    }

    @Test
    fun `if-then - BCS skip when carry set`() {
        val asm = """
            Start:
                SEC
                BCS Skip
                LDA #${'$'}FF
            Skip:
                LDA #${'$'}42
                RTS
        """

        val result = verifyPattern(asm)
        assertEquals(0x42, result.interpreterState.A, "A should be 0x42 (branch taken)")
        result.assertMatches()
    }

    @Test
    fun `if-then - BCC skip when carry clear`() {
        val asm = """
            Start:
                CLC
                BCC Skip
                LDA #${'$'}FF
            Skip:
                LDA #${'$'}42
                RTS
        """

        val result = verifyPattern(asm)
        assertEquals(0x42, result.interpreterState.A, "A should be 0x42 (branch taken)")
        result.assertMatches()
    }

    @Test
    fun `if-then - BMI skip when negative`() {
        val asm = """
            Start:
                LDA #${'$'}80
                BMI Skip
                LDA #${'$'}01
            Skip:
                RTS
        """

        val result = verifyPattern(asm)
        assertEquals(0x80, result.interpreterState.A, "A should remain 0x80 (branch taken, negative)")
        result.assertMatches()
    }

    @Test
    fun `if-then - BPL skip when positive`() {
        val asm = """
            Start:
                LDA #${'$'}7F
                BPL Skip
                LDA #${'$'}FF
            Skip:
                RTS
        """

        val result = verifyPattern(asm)
        assertEquals(0x7F, result.interpreterState.A, "A should remain 0x7F (branch taken, positive)")
        result.assertMatches()
    }

    // =====================================================================
    // PATTERN 2: IF-THEN-ELSE (Branch + JMP)
    // =====================================================================

    @Test
    fun `if-then-else - BEQ selects else path`() {
        val asm = """
            Start:
                LDA #${'$'}00
                BEQ Else
                LDA #${'$'}01
                JMP Done
            Else:
                LDA #${'$'}02
            Done:
                RTS
        """

        val result = verifyPattern(asm)
        assertEquals(2, result.interpreterState.A, "A should be 2 (else path taken)")
        result.assertMatches()
    }

    @Test
    fun `if-then-else - BEQ selects then path`() {
        val asm = """
            Start:
                LDA #${'$'}05
                BEQ Else
                LDA #${'$'}01
                JMP Done
            Else:
                LDA #${'$'}02
            Done:
                RTS
        """

        val result = verifyPattern(asm)
        assertEquals(1, result.interpreterState.A, "A should be 1 (then path taken)")
        result.assertMatches()
    }

    @Test
    fun `if-then-else - nested conditionals`() {
        val asm = """
            Start:
                LDA #${'$'}02
                BEQ Path0
                CMP #${'$'}01
                BEQ Path1
                LDA #${'$'}FF
                JMP Done
            Path0:
                LDA #${'$'}00
                JMP Done
            Path1:
                LDA #${'$'}11
            Done:
                RTS
        """

        // A=2, not zero, CMP #1 sets Z=0 (2 != 1), so goes to LDA #$FF
        val result = verifyPattern(asm)
        assertEquals(0xFF, result.interpreterState.A, "A should be 0xFF (default path)")
        result.assertMatches()
    }

    // =====================================================================
    // PATTERN 3: WHILE LOOP (Pre-test)
    // =====================================================================

    @Test
    fun `while loop - simple countdown`() {
        val asm = """
            Start:
                LDX #${'$'}05
            Loop:
                DEX
                BNE Loop
                RTS
        """

        val result = verifyPattern(asm, checkRegisters = setOf("X"))
        assertEquals(0, result.interpreterState.X, "X should be 0 after countdown")
        result.assertMatches()
    }

    @Test
    fun `while loop - countdown with memory write`() {
        val asm = """
            Start:
                LDX #${'$'}03
            Loop:
                STX ${'$'}00
                DEX
                BNE Loop
                RTS
        """

        val result = verifyPattern(
            asm,
            checkRegisters = setOf("X"),
            checkMemory = setOf(0x00)
        )
        assertEquals(0, result.interpreterState.X, "X should be 0")
        // Last value written should be 1 (since we write before decrement, and stop when X=0)
        assertEquals(1, result.interpreterState.memory[0x00], "Memory[0] should be 1")
        result.assertMatches()
    }

    @Test
    fun `while loop - with BPL (positive test)`() {
        val asm = """
            Start:
                LDX #${'$'}02
            Loop:
                DEX
                BPL Loop
                RTS
        """

        // Loop: X=2 -> X=1 (positive, loop) -> X=0 (positive, loop) -> X=FF (negative, exit)
        val result = verifyPattern(asm, checkRegisters = setOf("X"))
        assertEquals(0xFF, result.interpreterState.X, "X should wrap to 0xFF")
        result.assertMatches()
    }

    // =====================================================================
    // PATTERN 4: DO-WHILE LOOP (Post-test)
    // =====================================================================

    @Test
    fun `do-while loop - executes at least once`() {
        val asm = """
            Start:
                LDX #${'$'}00
            Loop:
                INX
                CPX #${'$'}01
                BNE Loop
                RTS
        """

        // X starts at 0, INX makes it 1, CPX #1 sets Z=1, BNE not taken, exit
        val result = verifyPattern(asm, checkRegisters = setOf("X"))
        assertEquals(1, result.interpreterState.X, "X should be 1 (one iteration)")
        result.assertMatches()
    }

    @Test
    fun `do-while loop - multiple iterations`() {
        val asm = """
            Start:
                LDX #${'$'}00
            Loop:
                INX
                CPX #${'$'}05
                BNE Loop
                RTS
        """

        val result = verifyPattern(asm, checkRegisters = setOf("X"))
        assertEquals(5, result.interpreterState.X, "X should be 5 (5 iterations)")
        result.assertMatches()
    }

    // =====================================================================
    // PATTERN 5: LOOP WITH EARLY EXIT (Break)
    // =====================================================================

    @Test
    fun `loop with early exit - BEQ breaks loop`() {
        // by Claude - Bug 2 FIXED: Early exit via BEQ in middle of loop now generates break
        // Pattern: Loop: LDA; BEQ exit; DEX; BNE Loop
        // Fix in kotlin-codegen.kt: Detect when header has "work" instructions (LDA)
        // and generate while(true) { work; if(exitCond) break; body } instead of while(cond).
        val asm = """
            Start:
                LDX #${'$'}05
            Loop:
                LDA ${'$'}00,X
                BEQ Done
                DEX
                BNE Loop
            Done:
                RTS
        """

        // With memory[3]=0, should exit when X=3
        val result = verifyPattern(
            asm,
            initialMemory = mapOf(0x05 to 1, 0x04 to 1, 0x03 to 0, 0x02 to 1, 0x01 to 1),
            checkRegisters = setOf("X", "A")
        )
        assertEquals(3, result.interpreterState.X, "X should be 3 (early exit)")
        assertEquals(0, result.interpreterState.A, "A should be 0 (found zero)")
        // by Claude - Bug 2 FIXED: Generates while(true) with if(exitCond) break for early exits
        result.assertMatches()
    }

    // =====================================================================
    // PATTERN 6: COMPARISON-BASED LOOPS
    // =====================================================================

    @Test
    fun `comparison loop - CPX with BEQ exit`() {
        // by Claude - Bug 1: JMP Loop pattern
        // Pattern: Loop: INX; CPX #$03; BEQ exit; JMP Loop
        // Control flow analysis now correctly creates single PreTest loop (was nested loops).
        // Remaining issue: toKotlinExpr generates "X != 0" instead of "X != 3"
        // because it doesn't track that CPX set the flags, not LDA.
        // This is a CODE GENERATION layer issue.
        val asm = """
            Start:
                LDX #${'$'}00
            Loop:
                INX
                CPX #${'$'}03
                BEQ Done
                JMP Loop
            Done:
                RTS
        """

        val result = verifyPattern(asm, checkRegisters = setOf("X"))
        assertEquals(3, result.interpreterState.X, "X should be 3")
        // by Claude - Bug 1 FIXED: Both control structure and code generation now work.
        // Fix in kotlin-codegen.kt: generate condition AFTER processing header instructions
        // so CPX sets ctx.zeroFlag = "X == 0x03" before condition is generated.
        result.assertMatches()
    }

    @Test
    fun `DEBUG - analyze JMP Loop control structure`() {
        // This pattern has:
        // - Loop header with conditional exit (BEQ Done)
        // - Loop body with unconditional back-edge (JMP Loop)
        // Expected: Single PreTest loop with condition "X != 3"
        // Actual: Nested loops (PreTest containing Infinite) - BUG!
        val asm = """
            Start:
                LDX #${'$'}00
            Loop:
                INX
                CPX #${'$'}03
                BEQ Done
                JMP Loop
            Done:
                RTS
        """.trimIndent()

        val code = asm.parseToAssemblyCodeFile()
        val blocks = code.lines.blockify()
        blocks.dominators()

        println("=== BLOCKS ===")
        blocks.forEachIndexed { i, b ->
            println("Block $i: label=${b.label}, origLineIdx=${b.originalLineIndex}")
            println("  lines: ${b.lines.mapNotNull { it.instruction?.op }}")
            println("  fallThrough: ${b.fallThroughExit?.label ?: b.fallThroughExit?.originalLineIndex}")
            println("  branch: ${b.branchExit?.label ?: b.branchExit?.originalLineIndex}")
            println("  enteredFrom: ${b.enteredFrom.map { it.label ?: it.originalLineIndex }}")
            println("  idom: ${b.immediateDominator?.label ?: b.immediateDominator?.originalLineIndex}")
        }

        // Also check natural loops
        val naturalLoops = blocks.detectNaturalLoops()
        println("\n=== NATURAL LOOPS ===")
        naturalLoops.forEach { loop ->
            println("Loop header=${loop.header.label}")
            println("  backEdges: ${loop.backEdges.map { (from, to) -> "${from.label ?: from.originalLineIndex} -> ${to.label}" }}")
            println("  body: ${loop.body.map { it.label ?: it.originalLineIndex }}")
            println("  exits: ${loop.exits.map { it.label ?: it.originalLineIndex }}")
        }

        val startBlock = blocks.find { it.label == "Start" } ?: blocks[0]
        val func = AssemblyFunction(startBlock, emptyList())
        blocks.forEach { it.function = func }
        func.blocks = blocks.toSet()

        val controls = func.analyzeControls()

        println("\n=== CONTROL NODES ===")
        fun printNode(node: ControlNode, indent: String = "") {
            when (node) {
                is BlockNode -> println("${indent}BlockNode: ${node.block.label ?: node.block.originalLineIndex}")
                is IfNode -> {
                    println("${indent}IfNode: condition=${node.condition}, join=${node.join?.label}")
                    println("${indent}  then (${node.thenBranch.size} nodes):")
                    node.thenBranch.forEach { printNode(it, "$indent    ") }
                    println("${indent}  else (${node.elseBranch.size} nodes):")
                    node.elseBranch.forEach { printNode(it, "$indent    ") }
                }
                is LoopNode -> {
                    println("${indent}LoopNode: kind=${node.kind}, header=${node.header.label}")
                    println("${indent}  condition=${node.condition}")
                    println("${indent}  body (${node.body.size} nodes):")
                    node.body.forEach { printNode(it, "$indent    ") }
                }
                is GotoNode -> println("${indent}GotoNode: to=${node.to.label ?: node.to.originalLineIndex}")
                is BreakNode -> println("${indent}BreakNode")
                is ContinueNode -> println("${indent}ContinueNode")
                else -> println("${indent}${node::class.simpleName}")
            }
        }

        controls.forEach { printNode(it) }

        // This test is for debugging, always pass
        assertTrue(true)
    }

    @Test
    fun `comparison loop - CPY with BNE continue`() {
        val asm = """
            Start:
                LDY #${'$'}05
            Loop:
                DEY
                CPY #${'$'}02
                BNE Loop
                RTS
        """

        val result = verifyPattern(asm, checkRegisters = setOf("Y"))
        assertEquals(2, result.interpreterState.Y, "Y should be 2")
        result.assertMatches()
    }

    // =====================================================================
    // PATTERN 7: MEMORY COPY LOOP
    // =====================================================================

    @Test
    fun `memory copy loop`() {
        val asm = """
            Start:
                LDX #${'$'}02
            CopyLoop:
                LDA ${'$'}10,X
                STA ${'$'}20,X
                DEX
                BPL CopyLoop
                RTS
        """

        val result = verifyPattern(
            asm,
            initialMemory = mapOf(0x10 to 0xAA, 0x11 to 0xBB, 0x12 to 0xCC),
            checkRegisters = setOf("X"),
            checkMemory = setOf(0x20, 0x21, 0x22)
        )
        assertEquals(0xFF, result.interpreterState.X, "X should wrap to 0xFF")
        assertEquals(0xAA, result.interpreterState.memory[0x20], "mem[0x20] should be 0xAA")
        assertEquals(0xBB, result.interpreterState.memory[0x21], "mem[0x21] should be 0xBB")
        assertEquals(0xCC, result.interpreterState.memory[0x22], "mem[0x22] should be 0xCC")
        result.assertMatches()
    }

    // =====================================================================
    // PATTERN 8: MULTIPLE BRANCHES TO SAME LABEL (Bug #6 pattern)
    // =====================================================================

    @Test
    fun `multiple branches to same label - two BEQ to Done`() {
        val asm = """
            Start:
                LDA ${'$'}00
                BEQ Done
                LDA ${'$'}01
                BEQ Done
                LDA #${'$'}FF
            Done:
                RTS
        """

        // Both memory locations are 0, so first BEQ should be taken
        val result = verifyPattern(
            asm,
            initialMemory = mapOf(0x00 to 0, 0x01 to 1),
            checkRegisters = setOf("A")
        )
        assertEquals(0, result.interpreterState.A, "A should be 0 (first branch taken)")
        result.assertMatches()
    }

    @Test
    fun `multiple branches to same label - second BEQ taken`() {
        val asm = """
            Start:
                LDA ${'$'}00
                BEQ Done
                LDA ${'$'}01
                BEQ Done
                LDA #${'$'}FF
            Done:
                RTS
        """

        // First non-zero, second zero
        val result = verifyPattern(
            asm,
            initialMemory = mapOf(0x00 to 1, 0x01 to 0),
            checkRegisters = setOf("A")
        )
        assertEquals(0, result.interpreterState.A, "A should be 0 (second branch taken)")
        result.assertMatches()
    }

    @Test
    fun `multiple branches to same label - no branch taken`() {
        val asm = """
            Start:
                LDA ${'$'}00
                BEQ Done
                LDA ${'$'}01
                BEQ Done
                LDA #${'$'}FF
            Done:
                RTS
        """

        // Both non-zero
        val result = verifyPattern(
            asm,
            initialMemory = mapOf(0x00 to 1, 0x01 to 2),
            checkRegisters = setOf("A")
        )
        assertEquals(0xFF, result.interpreterState.A, "A should be 0xFF (no branch taken)")
        result.assertMatches()
    }

    // =====================================================================
    // PATTERN 9: FLAG-BASED CONDITIONALS
    // =====================================================================

    @Test
    fun `flag-based conditional - carry after CMP`() {
        val asm = """
            Start:
                LDA #${'$'}05
                CMP #${'$'}03
                BCS Greater
                LDA #${'$'}00
                JMP Done
            Greater:
                LDA #${'$'}FF
            Done:
                RTS
        """

        // 5 >= 3, so carry is set, BCS taken
        val result = verifyPattern(asm, checkRegisters = setOf("A"))
        assertEquals(0xFF, result.interpreterState.A, "A should be 0xFF (5 >= 3)")
        result.assertMatches()
    }

    @Test
    fun `flag-based conditional - no carry after CMP`() {
        val asm = """
            Start:
                LDA #${'$'}02
                CMP #${'$'}05
                BCS Greater
                LDA #${'$'}00
                JMP Done
            Greater:
                LDA #${'$'}FF
            Done:
                RTS
        """

        // 2 < 5, so carry is clear, BCS not taken
        val result = verifyPattern(asm, checkRegisters = setOf("A"))
        assertEquals(0, result.interpreterState.A, "A should be 0 (2 < 5)")
        result.assertMatches()
    }

    // =====================================================================
    // PATTERN 10: SHIFT-BASED LOOPS
    // =====================================================================

    @Test
    fun `shift loop - ASL until carry`() {
        val asm = """
            Start:
                LDA #${'$'}40
            ShiftLoop:
                ASL A
                BCC ShiftLoop
                RTS
        """

        // 0x40 << 1 = 0x80 (C=0), 0x80 << 1 = 0x00 (C=1), exit
        val result = verifyPattern(asm, checkRegisters = setOf("A"))
        assertEquals(0, result.interpreterState.A, "A should be 0 after shifting out")
        result.assertMatches()
    }

    // =====================================================================
    // PATTERN 11: ARITHMETIC LOOPS
    // =====================================================================

    @Test
    fun `addition loop - accumulate sum`() {
        val asm = """
            Start:
                LDA #${'$'}00
                LDX #${'$'}03
            AddLoop:
                CLC
                ADC #${'$'}05
                DEX
                BNE AddLoop
                RTS
        """

        // Add 5 three times: 0 + 5 + 5 + 5 = 15
        val result = verifyPattern(asm, checkRegisters = setOf("A", "X"))
        assertEquals(15, result.interpreterState.A, "A should be 15 (5+5+5)")
        assertEquals(0, result.interpreterState.X, "X should be 0")
        result.assertMatches()
    }

    // =====================================================================
    // PATTERN 12: NESTED LOOPS (Bug #19 pattern)
    // =====================================================================

    @Test
    fun `nested loops - outer Y inner X`() {
        val asm = """
            Start:
                LDY #${'$'}02
            OuterLoop:
                LDX #${'$'}03
            InnerLoop:
                DEX
                BNE InnerLoop
                DEY
                BNE OuterLoop
                RTS
        """

        val result = verifyPattern(asm, checkRegisters = setOf("X", "Y"))
        assertEquals(0, result.interpreterState.X, "X should be 0")
        assertEquals(0, result.interpreterState.Y, "Y should be 0")
        result.assertMatches()
    }

    @Test
    fun `nested loops - with memory access`() {
        // Pattern: for each row (Y), process each column (X)
        val asm = """
            Start:
                LDY #${'$'}01
            RowLoop:
                LDX #${'$'}02
            ColLoop:
                TYA
                STA ${'$'}00
                DEX
                BNE ColLoop
                DEY
                BPL RowLoop
                RTS
        """

        val result = verifyPattern(
            asm,
            checkRegisters = setOf("X", "Y", "A"),
            checkMemory = setOf(0x00)
        )
        // After: Y wraps to 0xFF (negative), X=0
        assertEquals(0xFF, result.interpreterState.Y, "Y should wrap to 0xFF")
        assertEquals(0, result.interpreterState.X, "X should be 0")
        result.assertMatches()
    }

    // =====================================================================
    // STRUCTURAL TESTS (verify control node structure)
    // =====================================================================

    @Test
    fun `if-then produces IfNode with empty else`() {
        val asm = """
            Start:
                LDA #${'$'}00
                BEQ Skip
                INC ${'$'}00
            Skip:
                RTS
        """.trimIndent()

        val code = asm.parseToAssemblyCodeFile()
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        val controls = functions[0].analyzeControls()

        // First node should be IfNode
        val ifNode = controls.filterIsInstance<IfNode>().firstOrNull()
        assertTrue(ifNode != null, "Should produce an IfNode")
        assertTrue(ifNode.elseBranch.isEmpty(), "else branch should be empty for if-then")
    }

    @Test
    fun `if-then-else produces IfNode with both branches`() {
        // by Claude - Bug 3: FIXED - else branch now populated correctly
        val asm = """
            Start:
                LDA #${'$'}00
                BEQ Else
                LDA #${'$'}01
                JMP Done
            Else:
                LDA #${'$'}02
            Done:
                RTS
        """.trimIndent()

        val code = asm.parseToAssemblyCodeFile()
        val blocks = code.lines.blockify()
        blocks.dominators()

        // Use same approach as decompileAndRun - treat as single function
        val startBlock = blocks.find { it.label == "Start" } ?: blocks[0]
        val func = AssemblyFunction(startBlock, emptyList())
        blocks.forEach { it.function = func }
        func.blocks = blocks.toSet()

        val controls = func.analyzeControls()

        val ifNode = controls.filterIsInstance<IfNode>().firstOrNull()
        assertTrue(ifNode != null, "Should produce an IfNode")
        assertTrue(ifNode.thenBranch.isNotEmpty(), "then branch should not be empty")
        // by Claude - Bug 3 is fixed, else branch is now detected correctly
        assertTrue(ifNode.elseBranch.isNotEmpty(), "else branch should not be empty")
    }

    @Test
    fun `simple loop produces LoopNode`() {
        val asm = """
            Start:
                LDX #${'$'}05
            Loop:
                DEX
                BNE Loop
                RTS
        """.trimIndent()

        val code = asm.parseToAssemblyCodeFile()
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        val controls = functions[0].analyzeControls()

        val loopNode = controls.filterIsInstance<LoopNode>().firstOrNull()
        assertTrue(loopNode != null, "Should produce a LoopNode")
        assertTrue(loopNode.body.isNotEmpty(), "Loop body should not be empty")
    }

    // by Claude - DEBUG tests for Bug 2 and Bug 3
    @Test
    fun `DEBUG - analyze early exit loop control structure`() {
        // Bug 2: Early exit via BEQ doesn't generate break
        // Pattern: Loop: LDA; BEQ Done; DEX; BNE Loop
        val asm = """
            Start:
                LDX #${'$'}05
            Loop:
                LDA ${'$'}00,X
                BEQ Done
                DEX
                BNE Loop
            Done:
                RTS
        """.trimIndent()

        val code = asm.parseToAssemblyCodeFile()
        val blocks = code.lines.blockify()
        blocks.dominators()

        println("=== BLOCKS ===")
        blocks.forEachIndexed { i, b ->
            println("Block $i: label=${b.label}, origLineIdx=${b.originalLineIndex}")
            println("  lines: ${b.lines.mapNotNull { it.instruction?.op }}")
            println("  fallThrough: ${b.fallThroughExit?.label ?: b.fallThroughExit?.originalLineIndex}")
            println("  branch: ${b.branchExit?.label ?: b.branchExit?.originalLineIndex}")
            println("  enteredFrom: ${b.enteredFrom.map { it.label ?: it.originalLineIndex }}")
        }

        val naturalLoops = blocks.detectNaturalLoops()
        println("\n=== NATURAL LOOPS ===")
        naturalLoops.forEach { loop ->
            println("Loop header=${loop.header.label}")
            println("  backEdges: ${loop.backEdges.map { (from, to) -> "${from.label ?: from.originalLineIndex} -> ${to.label}" }}")
            println("  body: ${loop.body.map { it.label ?: it.originalLineIndex }}")
            println("  exits: ${loop.exits.map { it.label ?: it.originalLineIndex }}")
        }

        val startBlock = blocks.find { it.label == "Start" } ?: blocks[0]
        val func = AssemblyFunction(startBlock, emptyList())
        blocks.forEach { it.function = func }
        func.blocks = blocks.toSet()

        val controls = func.analyzeControls()

        println("\n=== CONTROL NODES ===")
        fun printNode(node: ControlNode, indent: String = "") {
            when (node) {
                is BlockNode -> println("${indent}BlockNode: ${node.block.label ?: node.block.originalLineIndex}")
                is IfNode -> {
                    println("${indent}IfNode: condition=${node.condition}, join=${node.join?.label ?: node.join?.originalLineIndex}")
                    println("${indent}  then (${node.thenBranch.size} nodes):")
                    node.thenBranch.forEach { printNode(it, "$indent    ") }
                    println("${indent}  else (${node.elseBranch.size} nodes):")
                    node.elseBranch.forEach { printNode(it, "$indent    ") }
                }
                is LoopNode -> {
                    println("${indent}LoopNode: kind=${node.kind}, header=${node.header.label ?: node.header.originalLineIndex}")
                    println("${indent}  condition=${node.condition}")
                    println("${indent}  body (${node.body.size} nodes):")
                    node.body.forEach { printNode(it, "$indent    ") }
                }
                is GotoNode -> println("${indent}GotoNode: to=${node.to.label ?: node.to.originalLineIndex}")
                is BreakNode -> println("${indent}BreakNode")
                is ContinueNode -> println("${indent}ContinueNode")
                else -> println("${indent}${node::class.simpleName}")
            }
        }
        controls.forEach { printNode(it) }

        assertTrue(true)
    }

    @Test
    fun `DEBUG - analyze if-then-else control structure`() {
        // Bug 3: If-then-else else branch detection
        val asm = """
            Start:
                LDA #${'$'}00
                BEQ Else
                LDA #${'$'}01
                JMP Done
            Else:
                LDA #${'$'}02
            Done:
                RTS
        """.trimIndent()

        val code = asm.parseToAssemblyCodeFile()
        val blocks = code.lines.blockify()
        blocks.dominators()

        println("=== BLOCKS ===")
        blocks.forEachIndexed { i, b ->
            println("Block $i: label=${b.label}, origLineIdx=${b.originalLineIndex}")
            println("  lines: ${b.lines.mapNotNull { it.instruction?.op }}")
            println("  fallThrough: ${b.fallThroughExit?.label ?: b.fallThroughExit?.originalLineIndex}")
            println("  branch: ${b.branchExit?.label ?: b.branchExit?.originalLineIndex}")
            println("  enteredFrom: ${b.enteredFrom.map { it.label ?: it.originalLineIndex }}")
        }

        val startBlock = blocks.find { it.label == "Start" } ?: blocks[0]
        val func = AssemblyFunction(startBlock, emptyList())
        blocks.forEach { it.function = func }
        func.blocks = blocks.toSet()

        val controls = func.analyzeControls()

        println("\n=== CONTROL NODES ===")
        fun printNode(node: ControlNode, indent: String = "") {
            when (node) {
                is BlockNode -> println("${indent}BlockNode: ${node.block.label ?: node.block.originalLineIndex}")
                is IfNode -> {
                    println("${indent}IfNode: condition=${node.condition}, join=${node.join?.label ?: node.join?.originalLineIndex}")
                    println("${indent}  then (${node.thenBranch.size} nodes):")
                    node.thenBranch.forEach { printNode(it, "$indent    ") }
                    println("${indent}  else (${node.elseBranch.size} nodes):")
                    node.elseBranch.forEach { printNode(it, "$indent    ") }
                }
                is LoopNode -> {
                    println("${indent}LoopNode: kind=${node.kind}, header=${node.header.label ?: node.header.originalLineIndex}")
                    println("${indent}  condition=${node.condition}")
                    println("${indent}  body (${node.body.size} nodes):")
                    node.body.forEach { printNode(it, "$indent    ") }
                }
                is GotoNode -> println("${indent}GotoNode: to=${node.to.label ?: node.to.originalLineIndex}")
                is BreakNode -> println("${indent}BreakNode")
                is ContinueNode -> println("${indent}ContinueNode")
                else -> println("${indent}${node::class.simpleName}")
            }
        }
        controls.forEach { printNode(it) }

        assertTrue(true)
    }
}
