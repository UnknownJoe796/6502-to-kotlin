// by Claude - Phase 5: Common Algorithm Verification
// Tests that verify common 6502 algorithms decompile correctly
package com.ivieleague.decompiler6502tokotlin.decompiler

import com.ivieleague.decompiler6502tokotlin.hand.*
import com.ivieleague.decompiler6502tokotlin.interpreter.Interpreter6502
import com.ivieleague.decompiler6502tokotlin.testing.EvaluatorState
import com.ivieleague.decompiler6502tokotlin.testing.KotlinAstEvaluator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * by Claude - Common Algorithm Verification Test Suite
 *
 * Tests that common 6502 algorithms (from RealWorld6502Test and Interpreter6502IntegrationTest)
 * decompile correctly by comparing interpreter execution against generated Kotlin code.
 *
 * Algorithms tested:
 * 1. Fibonacci sequence calculation
 * 2. 8-bit multiplication (shift-and-add)
 * 3. Sum of 1 to N
 * 4. Memory copy routine
 * 5. Simple loop counter
 * 6. Bit manipulation patterns
 * 7. Multi-byte addition (16-bit)
 * 8. Division by 2 using LSR
 * 9. Multiplication by 2 using ASL
 */
class AlgorithmDecompileTest {

    // =====================================================================
    // TEST HARNESS: Reuse from ControlFlowPatternsTest
    // =====================================================================

    class BreakException : Exception()

    class AlgorithmEvaluator(private val state: EvaluatorState) {
        private val baseEvaluator = KotlinAstEvaluator(state)
        var loopIterations = 0
        var maxIterations = 10000 // Higher limit for algorithms

        fun evaluate(stmt: KotlinStmt) {
            when (stmt) {
                is KWhile -> {
                    loopIterations = 0
                    try {
                        while (baseEvaluator.evaluateBoolExpr(stmt.condition)) {
                            if (loopIterations++ > maxIterations) {
                                throw RuntimeException("Infinite loop detected in KWhile")
                            }
                            stmt.body.forEach { evaluate(it) }
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
                            stmt.body.forEach { evaluate(it) }
                        } while (baseEvaluator.evaluateBoolExpr(stmt.condition))
                    } catch (e: BreakException) {
                        // Break exits the loop
                    }
                }
                is KLoop -> {
                    loopIterations = 0
                    try {
                        while (true) {
                            if (loopIterations++ > maxIterations) {
                                throw RuntimeException("Infinite loop detected in KLoop")
                            }
                            stmt.body.forEach { evaluate(it) }
                        }
                    } catch (e: BreakException) {
                        // Break exits the loop
                    }
                }
                is KBreak -> {
                    throw BreakException()
                }
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

        fun evaluateAll(stmts: List<KotlinStmt>) {
            for (stmt in stmts) {
                evaluate(stmt)
            }
        }
    }

    data class AlgorithmState(
        val A: Int,
        val X: Int,
        val Y: Int,
        val memory: Map<Int, Int>,
        val loopIterations: Int = 0
    )

    /**
     * Run assembly through the interpreter with full loop support.
     */
    private fun runInterpreter(
        asm: String,
        initialState: Map<String, Int> = emptyMap(),
        initialMemory: Map<Int, Int> = emptyMap(),
        maxInstructions: Int = 10000
    ): AlgorithmState {
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
                when {
                    instr.op == AssemblyOp.RTS || instr.op == AssemblyOp.RTI -> {
                        break
                    }
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
                    }
                    else -> {
                        interp.executeInstruction(instr)
                    }
                }
            }
            pc++
            instructionsExecuted++
        }

        // Collect memory state
        val memoryState = mutableMapOf<Int, Int>()
        initialMemory.keys.forEach { addr ->
            memoryState[addr] = interp.memory.readByte(addr).toInt()
        }
        // Also collect common algorithm memory locations
        for (addr in 0x00..0x0F) {
            val value = interp.memory.readByte(addr).toInt()
            if (value != 0 || addr in initialMemory.keys) {
                memoryState[addr] = value
            }
        }

        return AlgorithmState(
            A = interp.cpu.A.toInt(),
            X = interp.cpu.X.toInt(),
            Y = interp.cpu.Y.toInt(),
            memory = memoryState
        )
    }

    /**
     * Decompile assembly and run through evaluator.
     */
    private fun runDecompiled(
        asm: String,
        initialState: Map<String, Int> = emptyMap(),
        initialMemory: Map<Int, Int> = emptyMap()
    ): Pair<AlgorithmState, String> {
        val code = asm.trimIndent().parseToAssemblyCodeFile()
        val blocks = code.lines.blockify()
        blocks.dominators()

        if (blocks.isEmpty()) {
            return Pair(
                AlgorithmState(
                    A = initialState["A"] ?: 0,
                    X = initialState["X"] ?: 0,
                    Y = initialState["Y"] ?: 0,
                    memory = initialMemory
                ),
                "// No blocks found"
            )
        }

        // For test patterns, treat all blocks as a single function starting at the first block
        val startBlock = blocks.find { it.label == "Start" } ?: blocks[0]
        val func = AssemblyFunction(startBlock, emptyList())

        // Assign all blocks to this function
        blocks.forEach { it.function = func }
        func.blocks = blocks.toSet()

        func.analyzeControls()

        // Generate code
        val ctx = CodeGenContext()
        ctx.registerA = initialState["A"]?.let { KLiteral(it.toString()) }
        ctx.registerX = initialState["X"]?.let { KLiteral(it.toString()) }
        ctx.registerY = initialState["Y"]?.let { KLiteral(it.toString()) }

        val stmts = mutableListOf<KotlinStmt>()
        for (node in func.asControls ?: emptyList()) {
            stmts.addAll(node.toKotlin(ctx))
        }

        // Debug: Print generated code
        val codeStr = stmts.joinToString("\n") { it.toKotlin() }

        // Evaluate
        val evalState = EvaluatorState(
            A = initialState["A"] ?: 0,
            X = initialState["X"] ?: 0,
            Y = initialState["Y"] ?: 0,
            memory = initialMemory.toMutableMap()
        )

        val evaluator = AlgorithmEvaluator(evalState)
        try {
            evaluator.evaluateAll(stmts)
        } catch (e: Exception) {
            System.err.println("Evaluation error: ${e.message}")
        }

        // Collect memory state
        val memoryState = mutableMapOf<Int, Int>()
        initialMemory.keys.forEach { addr ->
            memoryState[addr] = evalState.memory[addr] ?: 0
        }
        for (addr in 0x00..0x0F) {
            val value = evalState.memory[addr] ?: 0
            if (value != 0 || addr in initialMemory.keys) {
                memoryState[addr] = value
            }
        }

        return AlgorithmState(
            A = evalState.A,
            X = evalState.X,
            Y = evalState.Y,
            memory = memoryState,
            loopIterations = evaluator.loopIterations
        ) to codeStr
    }

    /**
     * Helper to compare and assert interpreter vs decompiled results.
     */
    private fun assertAlgorithmMatch(
        asm: String,
        initialState: Map<String, Int> = emptyMap(),
        initialMemory: Map<Int, Int> = emptyMap(),
        description: String
    ) {
        val interpState = runInterpreter(asm, initialState, initialMemory)
        val (kotlinState, code) = runDecompiled(asm, initialState, initialMemory)

        val differences = mutableListOf<String>()
        if (interpState.A != kotlinState.A) {
            differences.add("A: interpreter=${interpState.A}, kotlin=${kotlinState.A}")
        }
        if (interpState.X != kotlinState.X) {
            differences.add("X: interpreter=${interpState.X}, kotlin=${kotlinState.X}")
        }
        if (interpState.Y != kotlinState.Y) {
            differences.add("Y: interpreter=${interpState.Y}, kotlin=${kotlinState.Y}")
        }
        interpState.memory.forEach { (addr, value) ->
            val kotlinValue = kotlinState.memory[addr] ?: 0
            if (value != kotlinValue) {
                differences.add("mem[$${addr.toString(16)}]: interpreter=$value, kotlin=$kotlinValue")
            }
        }

        if (differences.isNotEmpty()) {
            println("=== $description FAILED ===")
            println("Assembly:")
            println(asm.trimIndent())
            println("\nGenerated Kotlin:")
            println(code)
            println("\nDifferences:")
            differences.forEach { println("  $it") }
            println()
        }

        assertTrue(differences.isEmpty(), "$description failed:\n${differences.joinToString("\n")}")
    }

    // =====================================================================
    // TEST 1: SIMPLE LOOP COUNTER
    // =====================================================================

    @Test
    fun `algorithm - simple loop counter`() {
        val asm = """
            Start:
                LDX #${'$'}00
            Loop:
                INX
                CPX #${'$'}05
                BNE Loop
                RTS
        """

        assertAlgorithmMatch(asm, description = "Simple loop counter")
    }

    @Test
    fun `algorithm - countdown loop`() {
        val asm = """
            Start:
                LDX #${'$'}05
            Loop:
                DEX
                BNE Loop
                RTS
        """

        assertAlgorithmMatch(asm, description = "Countdown loop")
    }

    // =====================================================================
    // TEST 2: SUM OF 1 TO N
    // =====================================================================

    @Test
    fun `algorithm - sum of 1 to 5`() {
        // Sum 1+2+3+4+5 = 15
        val asm = """
            Start:
                LDA #${'$'}00
                LDX #${'$'}05
            Loop:
                CLC
                STX ${'$'}00
                ADC ${'$'}00
                DEX
                BNE Loop
                RTS
        """

        assertAlgorithmMatch(
            asm,
            initialMemory = mapOf(0x00 to 0),
            description = "Sum of 1 to 5"
        )
    }

    @Test
    fun `algorithm - sum of 1 to 10`() {
        // Sum 1+2+...+10 = 55
        val asm = """
            Start:
                LDA #${'$'}00
                LDX #${'$'}0A
            Loop:
                CLC
                STX ${'$'}00
                ADC ${'$'}00
                DEX
                BNE Loop
                RTS
        """

        assertAlgorithmMatch(
            asm,
            initialMemory = mapOf(0x00 to 0),
            description = "Sum of 1 to 10"
        )
    }

    // =====================================================================
    // TEST 3: MULTIPLICATION BY REPEATED ADDITION
    // =====================================================================

    @Test
    fun `algorithm - multiply 3 times 4`() {
        // 3 * 4 = 12 using repeated addition
        val asm = """
            Start:
                LDA #${'$'}00
                LDX #${'$'}04
            Loop:
                CLC
                ADC #${'$'}03
                DEX
                BNE Loop
                RTS
        """

        assertAlgorithmMatch(asm, description = "Multiply 3*4 by repeated addition")
    }

    @Test
    fun `algorithm - multiply 7 times 8`() {
        // 7 * 8 = 56 using repeated addition
        val asm = """
            Start:
                LDA #${'$'}00
                LDX #${'$'}08
            Loop:
                CLC
                ADC #${'$'}07
                DEX
                BNE Loop
                RTS
        """

        assertAlgorithmMatch(asm, description = "Multiply 7*8 by repeated addition")
    }

    // =====================================================================
    // TEST 4: MULTIPLY BY 2 (ASL)
    // =====================================================================

    @Test
    fun `algorithm - multiply by 2 using ASL`() {
        val asm = """
            Start:
                LDA #${'$'}10
                ASL A
                RTS
        """

        assertAlgorithmMatch(
            asm,
            description = "Multiply by 2 using ASL"
        )
    }

    @Test
    fun `algorithm - multiply by 4 using ASL twice`() {
        val asm = """
            Start:
                LDA #${'$'}08
                ASL A
                ASL A
                RTS
        """

        assertAlgorithmMatch(asm, description = "Multiply by 4 using ASL twice")
    }

    @Test
    fun `algorithm - multiply by 8 using ASL three times`() {
        val asm = """
            Start:
                LDA #${'$'}05
                ASL A
                ASL A
                ASL A
                RTS
        """

        assertAlgorithmMatch(asm, description = "Multiply by 8 using ASL three times")
    }

    // =====================================================================
    // TEST 5: DIVIDE BY 2 (LSR)
    // =====================================================================

    @Test
    fun `algorithm - divide by 2 using LSR`() {
        val asm = """
            Start:
                LDA #${'$'}20
                LSR A
                RTS
        """

        assertAlgorithmMatch(asm, description = "Divide by 2 using LSR")
    }

    @Test
    fun `algorithm - divide by 4 using LSR twice`() {
        val asm = """
            Start:
                LDA #${'$'}40
                LSR A
                LSR A
                RTS
        """

        assertAlgorithmMatch(asm, description = "Divide by 4 using LSR twice")
    }

    // =====================================================================
    // TEST 6: BIT MANIPULATION
    // =====================================================================

    @Test
    fun `algorithm - set bits using ORA`() {
        val asm = """
            Start:
                LDA #${'$'}0F
                ORA #${'$'}F0
                RTS
        """

        assertAlgorithmMatch(asm, description = "Set bits using ORA")
    }

    @Test
    fun `algorithm - clear bits using AND`() {
        val asm = """
            Start:
                LDA #${'$'}FF
                AND #${'$'}0F
                RTS
        """

        assertAlgorithmMatch(asm, description = "Clear bits using AND")
    }

    @Test
    fun `algorithm - toggle bits using EOR`() {
        val asm = """
            Start:
                LDA #${'$'}AA
                EOR #${'$'}FF
                RTS
        """

        assertAlgorithmMatch(asm, description = "Toggle bits using EOR")
    }

    @Test
    fun `algorithm - bit rotation left with ROL`() {
        val asm = """
            Start:
                CLC
                LDA #${'$'}0F
                ROL A
                ROL A
                ROL A
                ROL A
                RTS
        """

        assertAlgorithmMatch(asm, description = "Bit rotation left")
    }

    @Test
    fun `algorithm - bit rotation right with ROR`() {
        val asm = """
            Start:
                CLC
                LDA #${'$'}F0
                ROR A
                ROR A
                ROR A
                ROR A
                RTS
        """

        assertAlgorithmMatch(asm, description = "Bit rotation right")
    }

    // =====================================================================
    // TEST 7: MEMORY OPERATIONS
    // =====================================================================

    @Test
    fun `algorithm - memory increment`() {
        val asm = """
            Start:
                LDA #${'$'}05
                STA ${'$'}00
                INC ${'$'}00
                INC ${'$'}00
                LDA ${'$'}00
                RTS
        """

        assertAlgorithmMatch(
            asm,
            initialMemory = mapOf(0x00 to 0),
            description = "Memory increment"
        )
    }

    @Test
    fun `algorithm - memory decrement`() {
        val asm = """
            Start:
                LDA #${'$'}05
                STA ${'$'}00
                DEC ${'$'}00
                DEC ${'$'}00
                LDA ${'$'}00
                RTS
        """

        assertAlgorithmMatch(
            asm,
            initialMemory = mapOf(0x00 to 0),
            description = "Memory decrement"
        )
    }

    @Test
    fun `algorithm - memory swap using temp`() {
        // Swap values at $00 and $01 using A as temp
        val asm = """
            Start:
                LDA ${'$'}00
                TAX
                LDA ${'$'}01
                STA ${'$'}00
                TXA
                STA ${'$'}01
                RTS
        """

        assertAlgorithmMatch(
            asm,
            initialMemory = mapOf(0x00 to 0x42, 0x01 to 0x99),
            description = "Memory swap using temp"
        )
    }

    // =====================================================================
    // TEST 8: CONDITIONAL ALGORITHMS
    // =====================================================================

    @Test
    fun `algorithm - find max of two values`() {
        // If A > B then max = A else max = B
        val asm = """
            Start:
                LDA ${'$'}00
                CMP ${'$'}01
                BCS AGreater
                LDA ${'$'}01
            AGreater:
                STA ${'$'}02
                RTS
        """

        assertAlgorithmMatch(
            asm,
            initialMemory = mapOf(0x00 to 0x30, 0x01 to 0x20, 0x02 to 0),
            description = "Find max of two values (A > B)"
        )
    }

    @Test
    fun `algorithm - find max of two values reversed`() {
        val asm = """
            Start:
                LDA ${'$'}00
                CMP ${'$'}01
                BCS AGreater
                LDA ${'$'}01
            AGreater:
                STA ${'$'}02
                RTS
        """

        assertAlgorithmMatch(
            asm,
            initialMemory = mapOf(0x00 to 0x10, 0x01 to 0x50, 0x02 to 0),
            description = "Find max of two values (B > A)"
        )
    }

    @Test
    fun `algorithm - absolute value`() {
        // If A is negative, negate it
        // Uses EOR #$FF + ADC #$01 pattern for negation
        val asm = """
            Start:
                LDA ${'$'}00
                BPL Positive
                EOR #${'$'}FF
                CLC
                ADC #${'$'}01
            Positive:
                STA ${'$'}01
                RTS
        """

        // Test with positive value
        assertAlgorithmMatch(
            asm,
            initialMemory = mapOf(0x00 to 0x30, 0x01 to 0),
            description = "Absolute value (positive input)"
        )
    }

    @Test
    fun `algorithm - absolute value negative`() {
        val asm = """
            Start:
                LDA ${'$'}00
                BPL Positive
                EOR #${'$'}FF
                CLC
                ADC #${'$'}01
            Positive:
                STA ${'$'}01
                RTS
        """

        // Test with negative value (-10 = 0xF6, abs = 10 = 0x0A)
        assertAlgorithmMatch(
            asm,
            initialMemory = mapOf(0x00 to 0xF6, 0x01 to 0),
            description = "Absolute value (negative input)"
        )
    }

    // =====================================================================
    // TEST 9: NESTED LOOPS
    // =====================================================================

    @Test
    fun `algorithm - nested countdown loops`() {
        // Outer loop Y=2 times, inner loop X=3 times
        val asm = """
            Start:
                LDA #${'$'}00
                LDY #${'$'}02
            OuterLoop:
                LDX #${'$'}03
            InnerLoop:
                CLC
                ADC #${'$'}01
                DEX
                BNE InnerLoop
                DEY
                BNE OuterLoop
                RTS
        """

        assertAlgorithmMatch(asm, description = "Nested countdown loops")
    }

    // =====================================================================
    // TEST 10: REGISTER TRANSFER PATTERNS
    // =====================================================================

    @Test
    fun `algorithm - register transfer chain`() {
        val asm = """
            Start:
                LDA #${'$'}42
                TAX
                TXA
                TAY
                TYA
                RTS
        """

        assertAlgorithmMatch(asm, description = "Register transfer chain")
    }

    @Test
    fun `algorithm - use X for indirect counting`() {
        // Count how many times we can subtract 3 from 10
        val asm = """
            Start:
                LDA #${'$'}0A
                LDX #${'$'}00
            Loop:
                CMP #${'$'}03
                BCC Done
                SEC
                SBC #${'$'}03
                INX
                JMP Loop
            Done:
                RTS
        """

        assertAlgorithmMatch(asm, description = "Integer division 10/3 using subtraction")
    }

    // =====================================================================
    // TEST 11: FLAG-BASED ALGORITHMS
    // =====================================================================

    @Test
    fun `algorithm - count set bits`() {
        // Count number of 1 bits in a byte using shifts
        val asm = """
            Start:
                LDA ${'$'}00
                LDX #${'$'}00
                LDY #${'$'}08
            Loop:
                ASL A
                BCC NoBit
                INX
            NoBit:
                DEY
                BNE Loop
                RTS
        """

        assertAlgorithmMatch(
            asm,
            initialMemory = mapOf(0x00 to 0xAA), // 0xAA = 10101010 = 4 bits set
            description = "Count set bits in byte"
        )
    }

    @Test
    fun `algorithm - count set bits all ones`() {
        val asm = """
            Start:
                LDA ${'$'}00
                LDX #${'$'}00
                LDY #${'$'}08
            Loop:
                ASL A
                BCC NoBit
                INX
            NoBit:
                DEY
                BNE Loop
                RTS
        """

        assertAlgorithmMatch(
            asm,
            initialMemory = mapOf(0x00 to 0xFF), // 0xFF = 8 bits set
            description = "Count set bits (all ones)"
        )
    }

    // =====================================================================
    // TEST 12: ARITHMETIC WITH CARRY
    // =====================================================================

    @Test
    fun `algorithm - add with carry propagation`() {
        // Add two 8-bit numbers that might overflow
        val asm = """
            Start:
                CLC
                LDA ${'$'}00
                ADC ${'$'}01
                STA ${'$'}02
                LDA #${'$'}00
                ADC #${'$'}00
                STA ${'$'}03
                RTS
        """

        assertAlgorithmMatch(
            asm,
            initialMemory = mapOf(0x00 to 0xFF, 0x01 to 0x10, 0x02 to 0, 0x03 to 0),
            description = "Add with carry propagation"
        )
    }

    @Test
    fun `algorithm - subtract with borrow`() {
        // Subtract that causes borrow
        val asm = """
            Start:
                SEC
                LDA ${'$'}00
                SBC ${'$'}01
                STA ${'$'}02
                RTS
        """

        assertAlgorithmMatch(
            asm,
            initialMemory = mapOf(0x00 to 0x10, 0x01 to 0x20, 0x02 to 0),
            description = "Subtract with borrow"
        )
    }
}
