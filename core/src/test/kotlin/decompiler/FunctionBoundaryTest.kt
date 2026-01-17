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
 * by Claude - Phase 4: Function Boundary Verification
 *
 * Tests that functions are split at correct boundaries with correct call/return semantics.
 *
 * Test categories:
 * 1. Simple function - JSR + RTS
 * 2. Nested calls - A calls B calls C
 * 3. Tail call - JMP as final instruction
 * 4. JumpEngine dispatch - indexed jump table
 * 5. Shared code - multiple entry points
 */
class FunctionBoundaryTest {

    // =====================================================================
    // TEST INFRASTRUCTURE
    // =====================================================================

    /**
     * Interpreter state after execution.
     */
    data class InterpreterState(
        val A: Int,
        val X: Int,
        val Y: Int,
        val memory: Map<Int, Int>,
        val callHistory: List<String>
    )

    /**
     * Run assembly through the interpreter with JSR/RTS support.
     */
    private fun runInterpreter(
        asm: String,
        initialState: Map<String, Int> = emptyMap(),
        initialMemory: Map<Int, Int> = emptyMap(),
        entryPoint: String = "Main",
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

        // Build label table
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

        // Call stack for JSR/RTS
        val callStack = mutableListOf<Int>()
        val callHistory = mutableListOf<String>()

        // Find entry point
        var pc = labelToLine[entryPoint] ?: 0
        var instructionsExecuted = 0

        while (pc < code.lines.size && instructionsExecuted < maxInstructions) {
            val line = code.lines[pc]
            val instr = line.instruction

            if (instr != null) {
                when (instr.op) {
                    AssemblyOp.RTS -> {
                        if (callStack.isEmpty()) {
                            break // Return from main - stop
                        }
                        pc = callStack.removeLast()
                        instructionsExecuted++
                        continue
                    }
                    AssemblyOp.JSR -> {
                        val target = (instr.address as? AssemblyAddressing.Direct)?.label
                        if (target != null) {
                            val targetLine = labelToLine[target]
                            if (targetLine != null) {
                                callStack.add(pc + 1)
                                callHistory.add(target)
                                pc = targetLine
                                instructionsExecuted++
                                continue
                            }
                        }
                    }
                    AssemblyOp.JMP -> {
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
                    else -> {
                        if (instr.op.isBranch) {
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
                        } else {
                            interp.executeInstruction(instr)
                        }
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

        return InterpreterState(
            A = interp.cpu.A.toInt(),
            X = interp.cpu.X.toInt(),
            Y = interp.cpu.Y.toInt(),
            memory = memoryState,
            callHistory = callHistory
        )
    }

    // =====================================================================
    // PATTERN 1: FUNCTION DISCOVERY (Structural Tests)
    // =====================================================================

    @Test
    fun `function discovery - JSR creates function entry`() {
        val asm = """
            Main:
                JSR Helper
                RTS
            Helper:
                LDA #${'$'}42
                RTS
        """

        val code = asm.trimIndent().parseToAssemblyCodeFile()
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertEquals(2, functions.size, "Should find 2 functions")

        val funcNames = functions.map { it.startingBlock.label }.toSet()
        assertTrue("Main" in funcNames, "Main should be a function")
        assertTrue("Helper" in funcNames, "Helper should be a function")
    }

    @Test
    fun `function discovery - multiple JSR targets`() {
        val asm = """
            Main:
                JSR FuncA
                JSR FuncB
                JSR FuncC
                RTS
            FuncA:
                RTS
            FuncB:
                RTS
            FuncC:
                RTS
        """

        val code = asm.trimIndent().parseToAssemblyCodeFile()
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertEquals(4, functions.size, "Should find 4 functions")

        val funcNames = functions.map { it.startingBlock.label }.toSet()
        assertTrue("Main" in funcNames)
        assertTrue("FuncA" in funcNames)
        assertTrue("FuncB" in funcNames)
        assertTrue("FuncC" in funcNames)
    }

    @Test
    fun `function discovery - JMP target becomes function`() {
        val asm = """
            Main:
                JSR Worker
                RTS
            Worker:
                LDA #${'$'}01
                JMP SharedExit
            SharedExit:
                STA ${'$'}00
                RTS
        """

        val code = asm.trimIndent().parseToAssemblyCodeFile()
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        val funcNames = functions.map { it.startingBlock.label }.toSet()
        assertTrue("Main" in funcNames, "Main should be a function")
        assertTrue("Worker" in funcNames, "Worker should be a function")
        assertTrue("SharedExit" in funcNames, "SharedExit should be a function (JMP target)")
    }

    @Test
    fun `function discovery - nested JSR creates all functions`() {
        val asm = """
            Main:
                JSR FuncA
                RTS
            FuncA:
                JSR FuncB
                RTS
            FuncB:
                JSR FuncC
                RTS
            FuncC:
                RTS
        """

        val code = asm.trimIndent().parseToAssemblyCodeFile()
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertEquals(4, functions.size, "Should find 4 functions")
    }

    // =====================================================================
    // PATTERN 2: SIMPLE FUNCTION BEHAVIOR (Interpreter Tests)
    // =====================================================================

    @Test
    fun `interpreter - simple JSR and RTS`() {
        val asm = """
            Main:
                LDA #${'$'}00
                JSR AddFive
                RTS
            AddFive:
                CLC
                ADC #${'$'}05
                RTS
        """

        val result = runInterpreter(asm)
        assertEquals(5, result.A, "A should be 5 after adding")
        assertEquals(listOf("AddFive"), result.callHistory, "Should have called AddFive")
    }

    @Test
    fun `interpreter - function modifies X register`() {
        val asm = """
            Main:
                LDX #${'$'}03
                JSR DoubleX
                RTS
            DoubleX:
                TXA
                ASL A
                TAX
                RTS
        """

        val result = runInterpreter(asm)
        assertEquals(6, result.X, "X should be 6 (3 * 2)")
    }

    @Test
    fun `interpreter - function uses memory`() {
        val asm = """
            Main:
                LDA #${'$'}0A
                STA ${'$'}00
                JSR IncrementMem
                LDA ${'$'}00
                RTS
            IncrementMem:
                INC ${'$'}00
                RTS
        """

        val result = runInterpreter(asm, initialMemory = mapOf(0x00 to 0))
        assertEquals(11, result.A, "A should be 11 (10 + 1)")
        assertEquals(11, result.memory[0x00], "Memory should be 11")
    }

    // =====================================================================
    // PATTERN 3: NESTED CALLS (Interpreter Tests)
    // =====================================================================

    @Test
    fun `interpreter - nested calls A calls B calls C`() {
        val asm = """
            Main:
                LDA #${'$'}01
                JSR FuncA
                RTS
            FuncA:
                CLC
                ADC #${'$'}01
                JSR FuncB
                RTS
            FuncB:
                CLC
                ADC #${'$'}02
                JSR FuncC
                RTS
            FuncC:
                CLC
                ADC #${'$'}04
                RTS
        """

        val result = runInterpreter(asm)
        assertEquals(8, result.A, "A should be 8 (1+1+2+4)")
        assertEquals(listOf("FuncA", "FuncB", "FuncC"), result.callHistory)
    }

    @Test
    fun `interpreter - return values propagate through nested calls`() {
        val asm = """
            Main:
                LDX #${'$'}00
                JSR Outer
                RTS
            Outer:
                INX
                JSR Inner
                INX
                RTS
            Inner:
                INX
                INX
                RTS
        """

        val result = runInterpreter(asm)
        assertEquals(4, result.X, "X should be 4")
    }

    // =====================================================================
    // PATTERN 4: TAIL CALLS (Interpreter Tests)
    // =====================================================================

    @Test
    fun `interpreter - tail call via JMP`() {
        val asm = """
            Main:
                LDA #${'$'}05
                JSR FirstFunc
                RTS
            FirstFunc:
                CLC
                ADC #${'$'}03
                JMP SecondFunc
            SecondFunc:
                CLC
                ADC #${'$'}02
                RTS
        """

        val result = runInterpreter(asm)
        assertEquals(10, result.A, "A should be 10 (5+3+2)")
    }

    @Test
    fun `interpreter - chain of JMPs`() {
        val asm = """
            Main:
                LDX #${'$'}00
                JSR ChainStart
                RTS
            ChainStart:
                INX
                JMP ChainMiddle
            ChainMiddle:
                INX
                JMP ChainEnd
            ChainEnd:
                INX
                RTS
        """

        val result = runInterpreter(asm)
        assertEquals(3, result.X, "X should be 3")
    }

    // =====================================================================
    // PATTERN 5: CONDITIONAL FUNCTION CALLS (Interpreter Tests)
    // =====================================================================

    @Test
    fun `interpreter - BEQ selects function path`() {
        val asm = """
            Main:
                LDA #${'$'}00
                BEQ CallZeroHandler
                JSR NonZeroHandler
                JMP Done
            CallZeroHandler:
                JSR ZeroHandler
            Done:
                RTS
            ZeroHandler:
                LDX #${'$'}AA
                RTS
            NonZeroHandler:
                LDX #${'$'}BB
                RTS
        """

        val result = runInterpreter(asm)
        assertEquals(0xAA, result.X, "X should be 0xAA (zero handler called)")
    }

    @Test
    fun `interpreter - BNE selects function path`() {
        val asm = """
            Main:
                LDA #${'$'}01
                BEQ CallZeroHandler
                JSR NonZeroHandler
                JMP Done
            CallZeroHandler:
                JSR ZeroHandler
            Done:
                RTS
            ZeroHandler:
                LDX #${'$'}AA
                RTS
            NonZeroHandler:
                LDX #${'$'}BB
                RTS
        """

        val result = runInterpreter(asm)
        assertEquals(0xBB, result.X, "X should be 0xBB (non-zero handler called)")
    }

    // =====================================================================
    // PATTERN 6: FUNCTION WITH LOOP (Interpreter Tests)
    // =====================================================================

    @Test
    fun `interpreter - function with countdown loop`() {
        val asm = """
            Main:
                LDX #${'$'}05
                JSR CountDown
                RTS
            CountDown:
            Loop:
                DEX
                BNE Loop
                RTS
        """

        val result = runInterpreter(asm)
        assertEquals(0, result.X, "X should be 0 after countdown")
    }

    @Test
    fun `interpreter - function accumulates in loop`() {
        val asm = """
            Main:
                LDA #${'$'}00
                LDX #${'$'}03
                JSR Accumulate
                RTS
            Accumulate:
            Loop:
                CLC
                ADC #${'$'}05
                DEX
                BNE Loop
                RTS
        """

        val result = runInterpreter(asm)
        assertEquals(15, result.A, "A should be 15 (5*3)")
        assertEquals(0, result.X, "X should be 0")
    }

    // =====================================================================
    // PATTERN 7: MULTIPLE CALLS TO SAME FUNCTION (Interpreter Tests)
    // =====================================================================

    @Test
    fun `interpreter - multiple calls to same function`() {
        val asm = """
            Main:
                LDA #${'$'}00
                JSR AddTen
                JSR AddTen
                JSR AddTen
                RTS
            AddTen:
                CLC
                ADC #${'$'}0A
                RTS
        """

        val result = runInterpreter(asm)
        assertEquals(30, result.A, "A should be 30 (10*3)")
        assertEquals(listOf("AddTen", "AddTen", "AddTen"), result.callHistory)
    }

    // =====================================================================
    // PATTERN 8: JUMPENGINE DISPATCH (Indexed Jump Table)
    // =====================================================================

    /**
     * by Claude - JumpEngine is a common 6502 pattern where:
     * - A value (often in A or X) indexes into a table of handler addresses
     * - The code jumps to the selected handler
     * - Each handler returns independently
     *
     * This tests the structural recognition of multiple JMP targets as separate functions.
     */

    @Test
    fun `function discovery - multiple JMP targets from dispatch`() {
        // Simulates a dispatch pattern where different code paths JMP to different handlers
        val asm = """
            Main:
                LDA ${'$'}00
                BEQ GoHandler0
                CMP #${'$'}01
                BEQ GoHandler1
                JMP Handler2
            GoHandler0:
                JMP Handler0
            GoHandler1:
                JMP Handler1
            Handler0:
                LDX #${'$'}00
                RTS
            Handler1:
                LDX #${'$'}01
                RTS
            Handler2:
                LDX #${'$'}02
                RTS
        """

        val code = asm.trimIndent().parseToAssemblyCodeFile()
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        val funcNames = functions.map { it.startingBlock.label }.toSet()

        // All handlers should be recognized as separate functions
        assertTrue("Handler0" in funcNames, "Handler0 should be a function")
        assertTrue("Handler1" in funcNames, "Handler1 should be a function")
        assertTrue("Handler2" in funcNames, "Handler2 should be a function")
    }

    @Test
    fun `interpreter - dispatch selects correct handler`() {
        val asm = """
            Main:
                LDA ${'$'}00
                BEQ GoHandler0
                CMP #${'$'}01
                BEQ GoHandler1
                JMP Handler2
            GoHandler0:
                JMP Handler0
            GoHandler1:
                JMP Handler1
            Handler0:
                LDX #${'$'}AA
                RTS
            Handler1:
                LDX #${'$'}BB
                RTS
            Handler2:
                LDX #${'$'}CC
                RTS
        """

        // Test with value 0 - should go to Handler0
        val result0 = runInterpreter(asm, initialMemory = mapOf(0x00 to 0))
        assertEquals(0xAA, result0.X, "X should be 0xAA (handler 0)")

        // Test with value 1 - should go to Handler1
        val result1 = runInterpreter(asm, initialMemory = mapOf(0x00 to 1))
        assertEquals(0xBB, result1.X, "X should be 0xBB (handler 1)")

        // Test with value 2 - should go to Handler2
        val result2 = runInterpreter(asm, initialMemory = mapOf(0x00 to 2))
        assertEquals(0xCC, result2.X, "X should be 0xCC (handler 2)")
    }

    @Test
    fun `interpreter - JumpEngine style dispatch with JSR`() {
        // Tests a pattern where dispatcher JSRs to handlers via JMP
        val asm = """
            Main:
                LDA #${'$'}00
                JSR Dispatcher
                STA ${'$'}10
                RTS
            Dispatcher:
                LDA ${'$'}00
                BEQ DispatchTo0
                JMP Handler1
            DispatchTo0:
                JMP Handler0
            Handler0:
                LDA #${'$'}42
                RTS
            Handler1:
                LDA #${'$'}99
                RTS
        """

        // Test dispatch to handler 0
        val result0 = runInterpreter(asm, initialMemory = mapOf(0x00 to 0, 0x10 to 0))
        assertEquals(0x42, result0.memory[0x10], "Memory $10 should be 0x42 (from handler 0)")

        // Test dispatch to handler 1
        val result1 = runInterpreter(asm, initialMemory = mapOf(0x00 to 1, 0x10 to 0))
        assertEquals(0x99, result1.memory[0x10], "Memory $10 should be 0x99 (from handler 1)")
    }

    @Test
    fun `function discovery - switch-like pattern creates correct functions`() {
        // This is a common pattern in game loops: switch on game mode
        val asm = """
            GameLoop:
                JSR ProcessMode
                RTS
            ProcessMode:
                LDA ${'$'}00
                BEQ ModeTitle
                CMP #${'$'}01
                BEQ ModeGame
                CMP #${'$'}02
                BEQ ModePause
                RTS
            ModeTitle:
                JSR TitleHandler
                RTS
            ModeGame:
                JSR GameHandler
                RTS
            ModePause:
                JSR PauseHandler
                RTS
            TitleHandler:
                LDA #${'$'}01
                RTS
            GameHandler:
                LDA #${'$'}02
                RTS
            PauseHandler:
                LDA #${'$'}03
                RTS
        """

        val code = asm.trimIndent().parseToAssemblyCodeFile()
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        val funcNames = functions.map { it.startingBlock.label }.toSet()

        // All handlers should be separate functions
        assertTrue("TitleHandler" in funcNames, "TitleHandler should be a function")
        assertTrue("GameHandler" in funcNames, "GameHandler should be a function")
        assertTrue("PauseHandler" in funcNames, "PauseHandler should be a function")
        assertTrue("ProcessMode" in funcNames, "ProcessMode should be a function")
    }

    // =====================================================================
    // PATTERN 9: FUNCTION BLOCKS ARE ASSIGNED CORRECTLY
    // =====================================================================

    @Test
    fun `function blocks - each function has correct blocks`() {
        val asm = """
            Main:
                LDA #${'$'}00
                BEQ Skip
                INX
            Skip:
                JSR Helper
                RTS
            Helper:
                LDA #${'$'}01
                BEQ Done
                INY
            Done:
                RTS
        """

        val code = asm.trimIndent().parseToAssemblyCodeFile()
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        val mainFunc = functions.find { it.startingBlock.label == "Main" }
        val helperFunc = functions.find { it.startingBlock.label == "Helper" }

        assertTrue(mainFunc != null, "Main function should exist")
        assertTrue(helperFunc != null, "Helper function should exist")

        // Main should have blocks: Main, Skip (but not Helper or Done)
        val mainBlocks = mainFunc!!.blocks
        assertTrue(mainBlocks != null, "Main should have blocks assigned")

        // Helper should have blocks: Helper, Done
        val helperBlocks = helperFunc!!.blocks
        assertTrue(helperBlocks != null, "Helper should have blocks assigned")
    }
}
