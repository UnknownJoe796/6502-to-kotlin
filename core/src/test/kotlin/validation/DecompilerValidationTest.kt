package com.ivieleague.decompiler6502tokotlin.validation

import com.ivieleague.decompiler6502tokotlin.hand.*
import com.ivieleague.decompiler6502tokotlin.interpreter.*
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests that validate the decompiler produces equivalent code to the interpreter.
 *
 * These tests run both the interpreter and decompiled Kotlin code, comparing their final states
 * to ensure semantic equivalence.
 */
class DecompilerValidationTest {

    /**
     * Test simple load and store operations.
     */
    @Test
    fun testSimpleLoadStore() {
        val instructions = listOf(
            // LDA #$42
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)
            ),
            // STA $1000
            AssemblyInstruction(
                AssemblyOp.STA,
                AssemblyAddressing.Direct("\$1000")
            )
        )

        val interp = Interpreter6502()
        interp.executeInstruction(instructions[0])
        interp.executeInstruction(instructions[1])

        assertEquals(0x42u, interp.cpu.A, "A should be 0x42")
        assertEquals(0x42u, interp.memory.readByte(0x1000), "Memory at 0x1000 should be 0x42")

        // This demonstrates the framework - next step is to compare with decompiled code
        val state = ExecutionState.fromInterpreter(interp, listOf(0x1000))
        assertEquals(0x42u, state.registerA)
        assertEquals(0x42u, state.memory[0x1000])
    }

    /**
     * Test arithmetic with carry flag.
     */
    @Test
    fun testArithmeticWithCarry() {
        val instructions = listOf(
            // LDA #$FF
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0xFFu, AssemblyAddressing.Radix.Hex)
            ),
            // CLC
            AssemblyInstruction(AssemblyOp.CLC),
            // ADC #$01
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x01u, AssemblyAddressing.Radix.Hex)
            )
        )

        val interp = Interpreter6502()
        for (inst in instructions) {
            interp.executeInstruction(inst)
        }

        val state = ExecutionState.fromInterpreter(interp)
        assertEquals(0x00u, state.registerA, "A should wrap to 0")
        assertEquals(true, state.flagC, "Carry flag should be set")
        assertEquals(true, state.flagZ, "Zero flag should be set")
    }

    /**
     * Test comparison operations.
     */
    @Test
    fun testComparison() {
        val instructions = listOf(
            // LDA #$42
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)
            ),
            // CMP #$42
            AssemblyInstruction(
                AssemblyOp.CMP,
                AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)
            )
        )

        val interp = Interpreter6502()
        for (inst in instructions) {
            interp.executeInstruction(inst)
        }

        val state = ExecutionState.fromInterpreter(interp)
        assertEquals(true, state.flagZ, "Zero flag should be set when equal")
        assertEquals(true, state.flagC, "Carry flag should be set when A >= operand")
    }

    /**
     * Test indexed addressing mode.
     */
    @Test
    fun testIndexedAddressing() {
        val instructions = listOf(
            // LDX #$05
            AssemblyInstruction(
                AssemblyOp.LDX,
                AssemblyAddressing.ByteValue(0x05u, AssemblyAddressing.Radix.Hex)
            ),
            // LDA $1000,X
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.DirectX("\$1000")
            )
        )

        val interp = Interpreter6502()
        // Setup memory at $1005
        interp.memory.writeByte(0x1005, 0x99u)

        for (inst in instructions) {
            interp.executeInstruction(inst)
        }

        val state = ExecutionState.fromInterpreter(interp)
        assertEquals(0x99u, state.registerA, "A should contain value from $1005")
        assertEquals(0x05u, state.registerX, "X should be 5")
    }

    /**
     * Test loop with decrement.
     */
    @Test
    fun testLoopWithDecrement() {
        // Sum numbers from 5 down to 1
        val interp = Interpreter6502()

        // Setup counter at $00
        interp.memory.writeByte(0x00, 0x05u)
        // Setup sum at $01
        interp.memory.writeByte(0x01, 0x00u)

        val instructions = listOf(
            // LDA $01
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$01")),
            // CLC
            AssemblyInstruction(AssemblyOp.CLC),
            // ADC $00
            AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.Direct("\$00")),
            // STA $01
            AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$01")),
            // DEC $00
            AssemblyInstruction(AssemblyOp.DEC, AssemblyAddressing.Direct("\$00")),
            // LDA $00
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$00"))
        )

        // Execute loop 5 times
        for (i in 0 until 5) {
            for (inst in instructions) {
                interp.executeInstruction(inst)
            }
            if (interp.cpu.Z) break
        }

        val state = ExecutionState.fromInterpreter(interp, listOf(0x00, 0x01))
        assertEquals(15u, state.memory[0x01], "Sum should be 15 (5+4+3+2+1)")
        assertEquals(0u, state.memory[0x00], "Counter should be 0")
    }

    /**
     * Test bit manipulation.
     */
    @Test
    fun testBitManipulation() {
        val instructions = listOf(
            // LDA #$0F
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x0Fu, AssemblyAddressing.Radix.Hex)
            ),
            // ORA #$F0
            AssemblyInstruction(
                AssemblyOp.ORA,
                AssemblyAddressing.ByteValue(0xF0u, AssemblyAddressing.Radix.Hex)
            ),
            // STA $2000
            AssemblyInstruction(
                AssemblyOp.STA,
                AssemblyAddressing.Direct("\$2000")
            ),
            // AND #$AA
            AssemblyInstruction(
                AssemblyOp.AND,
                AssemblyAddressing.ByteValue(0xAAu, AssemblyAddressing.Radix.Hex)
            )
        )

        val interp = Interpreter6502()
        for (inst in instructions) {
            interp.executeInstruction(inst)
        }

        val state = ExecutionState.fromInterpreter(interp, listOf(0x2000))
        assertEquals(0xFFu, state.memory[0x2000], "Memory should have \$FF from ORA")
        assertEquals(0xAAu, state.registerA, "A should have \$AA from AND")
    }

    /**
     * Test transfer operations.
     */
    @Test
    fun testTransferOperations() {
        val instructions = listOf(
            // LDA #$42
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)
            ),
            // TAX
            AssemblyInstruction(AssemblyOp.TAX),
            // TAY
            AssemblyInstruction(AssemblyOp.TAY),
            // LDA #$99
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x99u, AssemblyAddressing.Radix.Hex)
            ),
            // TXA
            AssemblyInstruction(AssemblyOp.TXA)
        )

        val interp = Interpreter6502()
        for (inst in instructions) {
            interp.executeInstruction(inst)
        }

        val state = ExecutionState.fromInterpreter(interp)
        assertEquals(0x42u, state.registerA, "A should be restored from X")
        assertEquals(0x42u, state.registerX, "X should have original A value")
        assertEquals(0x42u, state.registerY, "Y should have original A value")
    }

    /**
     * Test stack operations.
     */
    @Test
    fun testStackOperations() {
        val instructions = listOf(
            // LDA #$55
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x55u, AssemblyAddressing.Radix.Hex)
            ),
            // PHA
            AssemblyInstruction(AssemblyOp.PHA),
            // LDA #$AA
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0xAAu, AssemblyAddressing.Radix.Hex)
            ),
            // PLA
            AssemblyInstruction(AssemblyOp.PLA)
        )

        val interp = Interpreter6502()
        val initialSP = interp.cpu.SP

        for (inst in instructions) {
            interp.executeInstruction(inst)
        }

        val state = ExecutionState.fromInterpreter(interp)
        assertEquals(0x55u, state.registerA, "A should be restored from stack")
        assertEquals(initialSP, interp.cpu.SP, "Stack pointer should be restored")
    }

    /**
     * Test shift operations.
     */
    @Test
    fun testShiftOperations() {
        val instructions = listOf(
            // LDA #$AA (10101010)
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0xAAu, AssemblyAddressing.Radix.Hex)
            ),
            // ASL A (shift left)
            AssemblyInstruction(AssemblyOp.ASL, null),
            // STA $3000
            AssemblyInstruction(
                AssemblyOp.STA,
                AssemblyAddressing.Direct("\$3000")
            ),
            // LDA #$55 (01010101)
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x55u, AssemblyAddressing.Radix.Hex)
            ),
            // LSR A (shift right)
            AssemblyInstruction(AssemblyOp.LSR, null)
        )

        val interp = Interpreter6502()
        for (inst in instructions) {
            interp.executeInstruction(inst)
        }

        val state = ExecutionState.fromInterpreter(interp, listOf(0x3000))
        assertEquals(0x54u, state.memory[0x3000], "ASL should produce 01010100")
        assertEquals(0x2Au, state.registerA, "LSR should produce 00101010")
    }

    /**
     * Test 16-bit addition (multi-byte).
     */
    @Test
    fun testMultiByteAddition() {
        val interp = Interpreter6502()

        // Store first 16-bit number: $12FF (little-endian)
        interp.memory.writeByte(0x00, 0xFFu)
        interp.memory.writeByte(0x01, 0x12u)

        // Store second 16-bit number: $5601
        interp.memory.writeByte(0x02, 0x01u)
        interp.memory.writeByte(0x03, 0x56u)

        val instructions = listOf(
            // Add low bytes
            AssemblyInstruction(AssemblyOp.CLC),
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$00")),
            AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.Direct("\$02")),
            AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$04")),
            // Add high bytes with carry
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$01")),
            AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.Direct("\$03")),
            AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$05"))
        )

        for (inst in instructions) {
            interp.executeInstruction(inst)
        }

        val state = ExecutionState.fromInterpreter(interp, listOf(0x04, 0x05))
        assertEquals(0x00u, state.memory[0x04], "Low byte should be $00 (with carry)")
        assertEquals(0x69u, state.memory[0x05], "High byte should be $69")
    }

    /**
     * Demonstrate the validation framework structure.
     * This shows how we would use the framework once decompilation is integrated.
     */
    @Test
    fun demonstrateValidationFramework() {
        val testCase = decompilerTest {
            name("Simple Addition")
            assembly("""
                LDA #$10
                CLC
                ADC #$20
                STA $1000
            """)
            checkMemoryAt(0x1000)
        }

        // When decompilation is integrated, this would:
        // 1. Parse the assembly
        // 2. Run it through interpreter
        // 3. Decompile to Kotlin
        // 4. Execute Kotlin
        // 5. Compare results

        assertEquals("Simple Addition", testCase.name)
        assertEquals(listOf(0x1000), testCase.checkMemory)
    }

    /**
     * Show generated Kotlin code for Fibonacci initialization.
     */
    @Test
    fun showFibonacciGeneratedCode() {
        val instructions = listOf(
            AssemblyInstruction(AssemblyOp.LDX, AssemblyAddressing.ByteValue(0x01u, AssemblyAddressing.Radix.Hex)),
            AssemblyInstruction(AssemblyOp.STX, AssemblyAddressing.Direct("\$00")),
            AssemblyInstruction(AssemblyOp.SEC),
            AssemblyInstruction(AssemblyOp.LDY, AssemblyAddressing.ByteValue(0x07u, AssemblyAddressing.Radix.Hex)),
            AssemblyInstruction(AssemblyOp.TYA),
            AssemblyInstruction(AssemblyOp.SBC, AssemblyAddressing.ByteValue(0x03u, AssemblyAddressing.Radix.Hex)),
            AssemblyInstruction(AssemblyOp.TAY),
            AssemblyInstruction(AssemblyOp.CLC),
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.ByteValue(0x02u, AssemblyAddressing.Radix.Hex)),
            AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$01"))
        )

        val simpleDecompiler = SimpleDecompiler()
        val simpleCode = simpleDecompiler.generateKotlinCode(instructions)

        val exprDecompiler = ExpressionDecompiler()
        val exprCode = exprDecompiler.generateKotlinCode(instructions)

        println("\n" + "=".repeat(70))
        println("SIMPLE DECOMPILER OUTPUT (Statement-based)")
        println("=".repeat(70))
        println(simpleCode)
        println("\n" + "=".repeat(70))
        println("EXPRESSION DECOMPILER OUTPUT (Expression-based)")
        println("=".repeat(70))
        println(exprCode)
        println("=".repeat(70) + "\n")
    }

    /**
     * Test double shift operation.
     */
    @Test
    fun showDoubleShiftCode() {
        val instructions = listOf(
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.ByteValue(0x10u, AssemblyAddressing.Radix.Hex)),
            AssemblyInstruction(AssemblyOp.ASL, null),  // A = A << 1
            AssemblyInstruction(AssemblyOp.ASL, null),  // A = A << 1 (again)
            AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$2000"))
        )

        val exprDecompiler = ExpressionDecompiler()
        val exprCode = exprDecompiler.generateKotlinCode(instructions)

        println("\n" + "=".repeat(70))
        println("DOUBLE SHIFT TEST (should be: value shl 2)")
        println("=".repeat(70))
        println(exprCode)
        println("=".repeat(70) + "\n")
    }

    /**
     * Test arithmetic expression building.
     */
    @Test
    fun testArithmeticExpression() {
        val instructions = listOf(
            // Calculate (5 + 10) * 4 using shifts
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.ByteValue(0x05u, AssemblyAddressing.Radix.Hex)),
            AssemblyInstruction(AssemblyOp.CLC),
            AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.ByteValue(0x0Au, AssemblyAddressing.Radix.Hex)),
            AssemblyInstruction(AssemblyOp.ASL, null),  // * 2
            AssemblyInstruction(AssemblyOp.ASL, null),  // * 2 again = * 4 total
            AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$3000"))
        )

        val testCase = DecompilerTestCase(
            name = "Arithmetic Expression Test",
            assembly = "(5 + 10) << 2",
            setupMemory = emptyMap(),
            setupRegisters = DecompilerTestCase.RegisterSetup(),
            checkMemory = listOf(0x3000),
            ignoreFlags = setOf("N", "V", "Z", "C", "I", "D"),
            ignoreRegisters = setOf("A", "X", "Y")
        )

        DecompilerValidator().validate(testCase, instructions)

        // Verify the result manually
        val interp = Interpreter6502()
        for (inst in instructions) {
            interp.executeInstruction(inst)
        }
        assertEquals(60u, interp.memory.readByte(0x3000), "Should be (5 + 10) * 4 = 60")
    }

    /**
     * Test bitwise operations.
     */
    @Test
    fun testBitwiseOperations() {
        val instructions = listOf(
            // (0xFF and 0x0F) or 0xF0 = 0xFF
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.ByteValue(0xFFu, AssemblyAddressing.Radix.Hex)),
            AssemblyInstruction(AssemblyOp.AND, AssemblyAddressing.ByteValue(0x0Fu, AssemblyAddressing.Radix.Hex)),
            AssemblyInstruction(AssemblyOp.ORA, AssemblyAddressing.ByteValue(0xF0u, AssemblyAddressing.Radix.Hex)),
            AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$4000"))
        )

        val testCase = DecompilerTestCase(
            name = "Bitwise Operations Test",
            assembly = "(0xFF and 0x0F) or 0xF0",
            setupMemory = emptyMap(),
            setupRegisters = DecompilerTestCase.RegisterSetup(),
            checkMemory = listOf(0x4000),
            ignoreFlags = setOf("N", "V", "Z", "C", "I", "D"),
            ignoreRegisters = setOf("A", "X", "Y")
        )

        DecompilerValidator().validate(testCase, instructions)

        val interp = Interpreter6502()
        for (inst in instructions) {
            interp.executeInstruction(inst)
        }
        assertEquals(0xFFu, interp.memory.readByte(0x4000))
    }

    /**
     * Test register value propagation.
     */
    @Test
    fun testRegisterPropagation() {
        val instructions = listOf(
            // Load 42, transfer to X and Y, store both
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.ByteValue(0x2Au, AssemblyAddressing.Radix.Hex)),
            AssemblyInstruction(AssemblyOp.TAX),
            AssemblyInstruction(AssemblyOp.TAY),
            AssemblyInstruction(AssemblyOp.STX, AssemblyAddressing.Direct("\$5000")),
            AssemblyInstruction(AssemblyOp.STY, AssemblyAddressing.Direct("\$5001"))
        )

        val testCase = DecompilerTestCase(
            name = "Register Propagation Test",
            assembly = "Transfer value through registers",
            setupMemory = emptyMap(),
            setupRegisters = DecompilerTestCase.RegisterSetup(),
            checkMemory = listOf(0x5000, 0x5001),
            ignoreFlags = setOf("N", "V", "Z", "C", "I", "D"),
            ignoreRegisters = setOf("A", "X", "Y")
        )

        DecompilerValidator().validate(testCase, instructions)

        val interp = Interpreter6502()
        for (inst in instructions) {
            interp.executeInstruction(inst)
        }
        assertEquals(0x2Au, interp.memory.readByte(0x5000), "X should hold 42")
        assertEquals(0x2Au, interp.memory.readByte(0x5001), "Y should hold 42")
    }

    /**
     * Test memory-to-memory operations.
     */
    @Test
    fun testMemoryOperations() {
        val instructions = listOf(
            // Read from $10, add 5, store to $20
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$10")),
            AssemblyInstruction(AssemblyOp.CLC),
            AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.ByteValue(0x05u, AssemblyAddressing.Radix.Hex)),
            AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$20"))
        )

        val testCase = DecompilerTestCase(
            name = "Memory Operations Test",
            assembly = "memory[$20] = memory[$10] + 5",
            setupMemory = mapOf(0x10 to 0x0Au),  // Initial value at $10 is 10
            setupRegisters = DecompilerTestCase.RegisterSetup(),
            checkMemory = listOf(0x20),
            ignoreFlags = setOf("N", "V", "Z", "C", "I", "D"),
            ignoreRegisters = setOf("A", "X", "Y")
        )

        DecompilerValidator().validate(testCase, instructions)

        val interp = Interpreter6502()
        interp.memory.writeByte(0x10, 0x0Au)
        for (inst in instructions) {
            interp.executeInstruction(inst)
        }
        assertEquals(0x0Fu, interp.memory.readByte(0x20), "Should be 10 + 5 = 15")
    }

    /**
     * Test subtraction with carry.
     */
    @Test
    fun testSubtractionWithCarry() {
        val instructions = listOf(
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.ByteValue(0x50u, AssemblyAddressing.Radix.Hex)),
            AssemblyInstruction(AssemblyOp.SEC),  // Set carry for subtraction
            AssemblyInstruction(AssemblyOp.SBC, AssemblyAddressing.ByteValue(0x30u, AssemblyAddressing.Radix.Hex)),
            AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$6000"))
        )

        val testCase = DecompilerTestCase(
            name = "Subtraction Test",
            assembly = "80 - 48 = 32",
            setupMemory = emptyMap(),
            setupRegisters = DecompilerTestCase.RegisterSetup(),
            checkMemory = listOf(0x6000),
            ignoreFlags = setOf("N", "V", "Z", "C", "I", "D"),
            ignoreRegisters = setOf("A", "X", "Y")
        )

        DecompilerValidator().validate(testCase, instructions)

        val interp = Interpreter6502()
        for (inst in instructions) {
            interp.executeInstruction(inst)
        }
        assertEquals(0x20u, interp.memory.readByte(0x6000), "Should be 80 - 48 = 32")
    }

    /**
     * Show all the generated code for various test cases.
     */
    @Test
    fun showAllGeneratedCode() {
        val testCases = mapOf(
            "Arithmetic: (5 + 10) << 2" to listOf(
                AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.ByteValue(0x05u, AssemblyAddressing.Radix.Hex)),
                AssemblyInstruction(AssemblyOp.CLC),
                AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.ByteValue(0x0Au, AssemblyAddressing.Radix.Hex)),
                AssemblyInstruction(AssemblyOp.ASL, null),
                AssemblyInstruction(AssemblyOp.ASL, null),
                AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$3000"))
            ),
            "Bitwise: (0xFF and 0x0F) or 0xF0" to listOf(
                AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.ByteValue(0xFFu, AssemblyAddressing.Radix.Hex)),
                AssemblyInstruction(AssemblyOp.AND, AssemblyAddressing.ByteValue(0x0Fu, AssemblyAddressing.Radix.Hex)),
                AssemblyInstruction(AssemblyOp.ORA, AssemblyAddressing.ByteValue(0xF0u, AssemblyAddressing.Radix.Hex)),
                AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$4000"))
            ),
            "Register Propagation" to listOf(
                AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.ByteValue(0x2Au, AssemblyAddressing.Radix.Hex)),
                AssemblyInstruction(AssemblyOp.TAX),
                AssemblyInstruction(AssemblyOp.TAY),
                AssemblyInstruction(AssemblyOp.STX, AssemblyAddressing.Direct("\$5000")),
                AssemblyInstruction(AssemblyOp.STY, AssemblyAddressing.Direct("\$5001"))
            ),
            "Memory Read + Add" to listOf(
                AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$10")),
                AssemblyInstruction(AssemblyOp.CLC),
                AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.ByteValue(0x05u, AssemblyAddressing.Radix.Hex)),
                AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$20"))
            ),
            "Subtraction: 80 - 48" to listOf(
                AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.ByteValue(0x50u, AssemblyAddressing.Radix.Hex)),
                AssemblyInstruction(AssemblyOp.SEC),
                AssemblyInstruction(AssemblyOp.SBC, AssemblyAddressing.ByteValue(0x30u, AssemblyAddressing.Radix.Hex)),
                AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$6000"))
            )
        )

        val decompiler = ExpressionDecompiler()

        println("\n" + "=".repeat(70))
        println("EXPRESSION DECOMPILER - ALL TEST CASES")
        println("=".repeat(70))

        for ((name, instructions) in testCases) {
            val code = decompiler.generateKotlinCode(instructions)
            println("\n### $name ###")
            // Extract just the execute() function body
            val executeBody = code.lines()
                .dropWhile { !it.contains("fun execute()") }
                .drop(1)  // Skip the "fun execute()" line
                .takeWhile { !it.contains("}") || it.contains("memory[") }
                .filter { it.trim().startsWith("memory[") }
                .joinToString("\n")
            println(executeBody)
        }

        println("\n" + "=".repeat(70))
    }

    /**
     * Test Fibonacci initialization subroutine (setup without loop).
     * This is a real-world example from the Fibonacci calculator in RealWorld6502Test.
     *
     * Based on: https://gist.github.com/pedrofranceschi/1285964
     */
    @Test
    fun testFibonacciInitialization() {
        // This is the setup portion of the Fibonacci calculator for N=7
        // Memory layout:
        //   $00 = previous value (x)
        //   $01 = current value (a)
        val instructions = listOf(
            // LDX #$01 - x = 1
            AssemblyInstruction(
                AssemblyOp.LDX,
                AssemblyAddressing.ByteValue(0x01u, AssemblyAddressing.Radix.Hex)
            ),
            // STX $00 - stores x
            AssemblyInstruction(
                AssemblyOp.STX,
                AssemblyAddressing.Direct("\$00")
            ),
            // SEC - clean carry for subtraction
            AssemblyInstruction(AssemblyOp.SEC),
            // LDY #$07 - calculates 7th fibonacci number
            AssemblyInstruction(
                AssemblyOp.LDY,
                AssemblyAddressing.ByteValue(0x07u, AssemblyAddressing.Radix.Hex)
            ),
            // TYA - transfer y register to accumulator
            AssemblyInstruction(AssemblyOp.TYA),
            // SBC #$03 - handles the algorithm iteration counting (7-3=4 iterations)
            AssemblyInstruction(
                AssemblyOp.SBC,
                AssemblyAddressing.ByteValue(0x03u, AssemblyAddressing.Radix.Hex)
            ),
            // TAY - transfer the accumulator back to y register
            AssemblyInstruction(AssemblyOp.TAY),
            // CLC - clean carry for addition
            AssemblyInstruction(AssemblyOp.CLC),
            // LDA #$02 - a = 2 (second Fibonacci number)
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x02u, AssemblyAddressing.Radix.Hex)
            ),
            // STA $01 - stores a
            AssemblyInstruction(
                AssemblyOp.STA,
                AssemblyAddressing.Direct("\$01")
            )
        )

        val testCase = DecompilerTestCase(
            name = "Fibonacci Initialization (N=7)",
            assembly = "Fibonacci setup for N=7",
            setupMemory = emptyMap(),
            setupRegisters = DecompilerTestCase.RegisterSetup(),
            checkMemory = listOf(0x00, 0x01),
            ignoreFlags = setOf("N", "V", "Z", "C", "I", "D"),  // Expression decompiler doesn't track flags
            ignoreRegisters = setOf("A", "X", "Y")  // Expression decompiler doesn't use registers
        )

        // Run validation
        DecompilerValidator().validate(testCase, instructions)

        // Also verify the expected values manually
        val interp = Interpreter6502()
        for (inst in instructions) {
            interp.executeInstruction(inst)
        }

        assertEquals(0x02u, interp.cpu.A, "A should be 2 (second Fibonacci)")
        assertEquals(0x01u, interp.cpu.X, "X should be 1 (first Fibonacci)")
        assertEquals(0x04u, interp.cpu.Y, "Y should be 4 (iteration count)")
        assertEquals(0x01u, interp.memory.readByte(0x00), "Memory[0x00] should be 1")
        assertEquals(0x02u, interp.memory.readByte(0x01), "Memory[0x01] should be 2")
        assertEquals(false, interp.cpu.C, "Carry should be clear (CLC)")
    }

    // ========================================================================
    // SMB DISASSEMBLY TESTS - Real code from Super Mario Bros
    // ========================================================================

    /**
     * Test from SMB: Metatile multiplication by 4 (lines 1828-1830)
     * Original code:
     *   lda MetatileBuffer,x    ; get metatile number
     *   asl                     ; multiply by 4
     *   asl
     *   sta $02                 ; store result
     *
     * This tests multiple initial values to ensure the decompiler handles all cases.
     */
    @Test
    fun testSMBMetatileMultiplyBy4() {
        // Test with metatile value = 0x10 (16 decimal, should give 64)
        val instructions = listOf(
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$80")),  // Load from $80
            AssemblyInstruction(AssemblyOp.ASL, null),  // Multiply by 2
            AssemblyInstruction(AssemblyOp.ASL, null),  // Multiply by 2 again (total = *4)
            AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$02"))   // Store to $02
        )

        // Test case 1: metatile = 0x10 (16) -> result should be 0x40 (64)
        val testCase1 = DecompilerTestCase(
            name = "SMB Metatile Multiply by 4 (value=0x10)",
            assembly = "memory[$02] = memory[$80] * 4",
            setupMemory = mapOf(0x80 to 0x10u),  // Metatile value = 16
            setupRegisters = DecompilerTestCase.RegisterSetup(),
            checkMemory = listOf(0x02),
            ignoreFlags = setOf("N", "V", "Z", "C", "I", "D"),
            ignoreRegisters = setOf("A", "X", "Y")
        )

        DecompilerValidator().validate(testCase1, instructions)

        // Verify with interpreter
        val interp1 = Interpreter6502()
        interp1.memory.writeByte(0x80, 0x10u)
        for (inst in instructions) {
            interp1.executeInstruction(inst)
        }
        assertEquals(0x40u, interp1.memory.readByte(0x02), "16 * 4 should be 64")

        // Test case 2: metatile = 0x08 (8) -> result should be 0x20 (32)
        val testCase2 = DecompilerTestCase(
            name = "SMB Metatile Multiply by 4 (value=0x08)",
            assembly = "memory[$02] = memory[$80] * 4",
            setupMemory = mapOf(0x80 to 0x08u),  // Metatile value = 8
            setupRegisters = DecompilerTestCase.RegisterSetup(),
            checkMemory = listOf(0x02),
            ignoreFlags = setOf("N", "V", "Z", "C", "I", "D"),
            ignoreRegisters = setOf("A", "X", "Y")
        )

        DecompilerValidator().validate(testCase2, instructions)

        val interp2 = Interpreter6502()
        interp2.memory.writeByte(0x80, 0x08u)
        for (inst in instructions) {
            interp2.executeInstruction(inst)
        }
        assertEquals(0x20u, interp2.memory.readByte(0x02), "8 * 4 should be 32")

        // Test case 3: metatile = 0x3F (63) -> result should be 0xFC (252)
        val testCase3 = DecompilerTestCase(
            name = "SMB Metatile Multiply by 4 (value=0x3F)",
            assembly = "memory[$02] = memory[$80] * 4",
            setupMemory = mapOf(0x80 to 0x3Fu),  // Metatile value = 63
            setupRegisters = DecompilerTestCase.RegisterSetup(),
            checkMemory = listOf(0x02),
            ignoreFlags = setOf("N", "V", "Z", "C", "I", "D"),
            ignoreRegisters = setOf("A", "X", "Y")
        )

        DecompilerValidator().validate(testCase3, instructions)

        val interp3 = Interpreter6502()
        interp3.memory.writeByte(0x80, 0x3Fu)
        for (inst in instructions) {
            interp3.executeInstruction(inst)
        }
        assertEquals(0xFCu, interp3.memory.readByte(0x02), "63 * 4 should be 252")
    }

    /**
     * Test from SMB: Add 32 pixels for status bar (lines 1974-1978)
     * Original code:
     *   lda $02               ; get vertical offset
     *   clc
     *   adc #$20              ; add 32 pixels for the status bar
     *   asl
     *   rol $05               ; shift and rotate (more complex, we'll test just the add+shift)
     *
     * Simplified to test: load, add 32, shift left once
     */
    @Test
    fun testSMBStatusBarCoordinateAdjustment() {
        val instructions = listOf(
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$02")),  // Load vertical offset
            AssemblyInstruction(AssemblyOp.CLC),
            AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.ByteValue(0x20u, AssemblyAddressing.Radix.Hex)),  // Add 32
            AssemblyInstruction(AssemblyOp.ASL, null),  // Shift left (multiply by 2)
            AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$04"))   // Store result
        )

        // Test case 1: offset = 0x10 (16) -> (16 + 32) * 2 = 96
        val testCase1 = DecompilerTestCase(
            name = "SMB Status Bar Adjust (offset=0x10)",
            assembly = "memory[$04] = (memory[$02] + 32) * 2",
            setupMemory = mapOf(0x02 to 0x10u),
            setupRegisters = DecompilerTestCase.RegisterSetup(),
            checkMemory = listOf(0x04),
            ignoreFlags = setOf("N", "V", "Z", "C", "I", "D"),
            ignoreRegisters = setOf("A", "X", "Y")
        )

        DecompilerValidator().validate(testCase1, instructions)

        val interp1 = Interpreter6502()
        interp1.memory.writeByte(0x02, 0x10u)
        for (inst in instructions) {
            interp1.executeInstruction(inst)
        }
        assertEquals(0x60u, interp1.memory.readByte(0x04), "(16 + 32) * 2 should be 96")

        // Test case 2: offset = 0x00 (0) -> (0 + 32) * 2 = 64
        val testCase2 = DecompilerTestCase(
            name = "SMB Status Bar Adjust (offset=0x00)",
            assembly = "memory[$04] = (memory[$02] + 32) * 2",
            setupMemory = mapOf(0x02 to 0x00u),
            setupRegisters = DecompilerTestCase.RegisterSetup(),
            checkMemory = listOf(0x04),
            ignoreFlags = setOf("N", "V", "Z", "C", "I", "D"),
            ignoreRegisters = setOf("A", "X", "Y")
        )

        DecompilerValidator().validate(testCase2, instructions)

        val interp2 = Interpreter6502()
        interp2.memory.writeByte(0x02, 0x00u)
        for (inst in instructions) {
            interp2.executeInstruction(inst)
        }
        assertEquals(0x40u, interp2.memory.readByte(0x04), "(0 + 32) * 2 should be 64")

        // Test case 3: offset = 0x50 (80) -> (80 + 32) * 2 = 224
        val testCase3 = DecompilerTestCase(
            name = "SMB Status Bar Adjust (offset=0x50)",
            assembly = "memory[$04] = (memory[$02] + 32) * 2",
            setupMemory = mapOf(0x02 to 0x50u),
            setupRegisters = DecompilerTestCase.RegisterSetup(),
            checkMemory = listOf(0x04),
            ignoreFlags = setOf("N", "V", "Z", "C", "I", "D"),
            ignoreRegisters = setOf("A", "X", "Y")
        )

        DecompilerValidator().validate(testCase3, instructions)

        val interp3 = Interpreter6502()
        interp3.memory.writeByte(0x02, 0x50u)
        for (inst in instructions) {
            interp3.executeInstruction(inst)
        }
        assertEquals(0xE0u, interp3.memory.readByte(0x04), "(80 + 32) * 2 should be 224")
    }

    /**
     * Test from SMB: Sprite horizontal position adjustment
     * From floatey number sprite code - adds 8 pixels to X coordinate for right sprite
     * Original pattern:
     *   lda FloateyNum_X_Pos,x       ; get horizontal coordinate
     *   sta Sprite_X_Position,y      ; store into X coordinate of left sprite
     *   clc
     *   adc #$08                     ; add eight pixels
     *   sta Sprite_X_Position+4,y    ; store into X coordinate of right sprite
     */
    @Test
    fun testSMBSpriteHorizontalSpacing() {
        val instructions = listOf(
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$30")),  // Get X position
            AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$40")),  // Store to left sprite
            AssemblyInstruction(AssemblyOp.CLC),
            AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.ByteValue(0x08u, AssemblyAddressing.Radix.Hex)),  // Add 8 pixels
            AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$44"))   // Store to right sprite
        )

        // Test case 1: X position = 0x40 (64) -> left=64, right=72
        val testCase1 = DecompilerTestCase(
            name = "SMB Sprite Spacing (X=0x40)",
            assembly = "left sprite at X=64, right sprite at X=72",
            setupMemory = mapOf(0x30 to 0x40u),
            setupRegisters = DecompilerTestCase.RegisterSetup(),
            checkMemory = listOf(0x40, 0x44),
            ignoreFlags = setOf("N", "V", "Z", "C", "I", "D"),
            ignoreRegisters = setOf("A", "X", "Y")
        )

        DecompilerValidator().validate(testCase1, instructions)

        val interp1 = Interpreter6502()
        interp1.memory.writeByte(0x30, 0x40u)
        for (inst in instructions) {
            interp1.executeInstruction(inst)
        }
        assertEquals(0x40u, interp1.memory.readByte(0x40), "Left sprite should be at 64")
        assertEquals(0x48u, interp1.memory.readByte(0x44), "Right sprite should be at 72")

        // Test case 2: X position = 0x00 (0) -> left=0, right=8
        val testCase2 = DecompilerTestCase(
            name = "SMB Sprite Spacing (X=0x00)",
            assembly = "left sprite at X=0, right sprite at X=8",
            setupMemory = mapOf(0x30 to 0x00u),
            setupRegisters = DecompilerTestCase.RegisterSetup(),
            checkMemory = listOf(0x40, 0x44),
            ignoreFlags = setOf("N", "V", "Z", "C", "I", "D"),
            ignoreRegisters = setOf("A", "X", "Y")
        )

        DecompilerValidator().validate(testCase2, instructions)

        val interp2 = Interpreter6502()
        interp2.memory.writeByte(0x30, 0x00u)
        for (inst in instructions) {
            interp2.executeInstruction(inst)
        }
        assertEquals(0x00u, interp2.memory.readByte(0x40), "Left sprite should be at 0")
        assertEquals(0x08u, interp2.memory.readByte(0x44), "Right sprite should be at 8")

        // Test case 3: X position = 0xF8 (248) -> left=248, right=0 (wraps around)
        val testCase3 = DecompilerTestCase(
            name = "SMB Sprite Spacing (X=0xF8, with wrap)",
            assembly = "left sprite at X=248, right sprite wraps to X=0",
            setupMemory = mapOf(0x30 to 0xF8u),
            setupRegisters = DecompilerTestCase.RegisterSetup(),
            checkMemory = listOf(0x40, 0x44),
            ignoreFlags = setOf("N", "V", "Z", "C", "I", "D"),
            ignoreRegisters = setOf("A", "X", "Y")
        )

        DecompilerValidator().validate(testCase3, instructions)

        val interp3 = Interpreter6502()
        interp3.memory.writeByte(0x30, 0xF8u)
        for (inst in instructions) {
            interp3.executeInstruction(inst)
        }
        assertEquals(0xF8u, interp3.memory.readByte(0x40), "Left sprite should be at 248")
        assertEquals(0x00u, interp3.memory.readByte(0x44), "Right sprite should wrap to 0")
    }

    // ========================================================================
    // SCALING UP: Larger code sequences from SMB
    // ========================================================================

    /**
     * Test from SMB: Coin position calculation (lines 6980-6988)
     * This is a longer sequence (10 instructions) with TWO outputs:
     * 1. Horizontal position: (value * 16) + 5
     * 2. Vertical position: another_value + 32
     *
     * Original code:
     *   lda $06                    ; get low byte of block buffer offset
     *   asl
     *   asl                        ; multiply by 16 to use lower nybble
     *   asl
     *   asl
     *   ora #$05                   ; add five pixels
     *   sta Misc_X_Position,y      ; save as horizontal coordinate for misc object
     *   lda $02                    ; get vertical high nybble offset from earlier
     *   adc #$20                   ; add 32 pixels for the status bar
     *   sta Misc_Y_Position,y      ; store as vertical coordinate
     */
    @Test
    fun testSMBCoinPositionCalculation() {
        val instructions = listOf(
            // Calculate horizontal position
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$06")),
            AssemblyInstruction(AssemblyOp.ASL, null),  // * 2
            AssemblyInstruction(AssemblyOp.ASL, null),  // * 4
            AssemblyInstruction(AssemblyOp.ASL, null),  // * 8
            AssemblyInstruction(AssemblyOp.ASL, null),  // * 16
            AssemblyInstruction(AssemblyOp.ORA, AssemblyAddressing.ByteValue(0x05u, AssemblyAddressing.Radix.Hex)),  // +5
            AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$50")),  // Store X position
            // Calculate vertical position
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$02")),
            AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.ByteValue(0x20u, AssemblyAddressing.Radix.Hex)),  // +32
            AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$51"))   // Store Y position
        )

        // Test case 1: block offset = 0x02 (2), vertical offset = 0x10 (16)
        // Horizontal: (2 * 16) + 5 = 37 (0x25)
        // Vertical: 16 + 32 = 48 (0x30)
        val testCase1 = DecompilerTestCase(
            name = "SMB Coin Position (offset=0x02, vertical=0x10)",
            assembly = "Calculate coin sprite position",
            setupMemory = mapOf(0x06 to 0x02u, 0x02 to 0x10u),
            setupRegisters = DecompilerTestCase.RegisterSetup(C = false), // Carry clear for ADC
            checkMemory = listOf(0x50, 0x51),
            ignoreFlags = setOf("N", "V", "Z", "C", "I", "D"),
            ignoreRegisters = setOf("A", "X", "Y")
        )

        DecompilerValidator().validate(testCase1, instructions)

        val interp1 = Interpreter6502()
        interp1.memory.writeByte(0x06, 0x02u)
        interp1.memory.writeByte(0x02, 0x10u)
        interp1.cpu.C = false
        for (inst in instructions) {
            interp1.executeInstruction(inst)
        }
        assertEquals(0x25u, interp1.memory.readByte(0x50), "Horizontal should be (2*16)+5 = 37")
        assertEquals(0x30u, interp1.memory.readByte(0x51), "Vertical should be 16+32 = 48")

        // Test case 2: block offset = 0x0F (15), vertical offset = 0x00 (0)
        // Horizontal: (15 * 16) + 5 = 245 (0xF5)
        // Vertical: 0 + 32 = 32 (0x20)
        val testCase2 = DecompilerTestCase(
            name = "SMB Coin Position (offset=0x0F, vertical=0x00)",
            assembly = "Calculate coin sprite position",
            setupMemory = mapOf(0x06 to 0x0Fu, 0x02 to 0x00u),
            setupRegisters = DecompilerTestCase.RegisterSetup(C = false),
            checkMemory = listOf(0x50, 0x51),
            ignoreFlags = setOf("N", "V", "Z", "C", "I", "D"),
            ignoreRegisters = setOf("A", "X", "Y")
        )

        DecompilerValidator().validate(testCase2, instructions)

        val interp2 = Interpreter6502()
        interp2.memory.writeByte(0x06, 0x0Fu)
        interp2.memory.writeByte(0x02, 0x00u)
        interp2.cpu.C = false
        for (inst in instructions) {
            interp2.executeInstruction(inst)
        }
        assertEquals(0xF5u, interp2.memory.readByte(0x50), "Horizontal should be (15*16)+5 = 245")
        assertEquals(0x20u, interp2.memory.readByte(0x51), "Vertical should be 0+32 = 32")

        // Test case 3: block offset = 0x07 (7), vertical offset = 0xC0 (192)
        // Horizontal: (7 * 16) + 5 = 117 (0x75)
        // Vertical: 192 + 32 = 224 (0xE0)
        val testCase3 = DecompilerTestCase(
            name = "SMB Coin Position (offset=0x07, vertical=0xC0)",
            assembly = "Calculate coin sprite position",
            setupMemory = mapOf(0x06 to 0x07u, 0x02 to 0xC0u),
            setupRegisters = DecompilerTestCase.RegisterSetup(C = false),
            checkMemory = listOf(0x50, 0x51),
            ignoreFlags = setOf("N", "V", "Z", "C", "I", "D"),
            ignoreRegisters = setOf("A", "X", "Y")
        )

        DecompilerValidator().validate(testCase3, instructions)

        val interp3 = Interpreter6502()
        interp3.memory.writeByte(0x06, 0x07u)
        interp3.memory.writeByte(0x02, 0xC0u)
        interp3.cpu.C = false
        for (inst in instructions) {
            interp3.executeInstruction(inst)
        }
        assertEquals(0x75u, interp3.memory.readByte(0x50), "Horizontal should be (7*16)+5 = 117")
        assertEquals(0xE0u, interp3.memory.readByte(0x51), "Vertical should be 192+32 = 224")
    }

    /**
     * Test from SMB: Multiply by 16 for whirlpool size (lines 4231-4235)
     * This tests 4 consecutive ASL instructions (multiply by 16)
     *
     * Original code:
     *   tya
     *   asl                          ; multiply by 16 to get size of whirlpool
     *   asl
     *   asl
     *   asl
     *   sta Whirlpool_Length,x       ; save size of whirlpool here
     */
    @Test
    fun testSMBWhirlpoolSizeCalculation() {
        val instructions = listOf(
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$10")),  // Load from memory
            AssemblyInstruction(AssemblyOp.ASL, null),  // * 2
            AssemblyInstruction(AssemblyOp.ASL, null),  // * 4
            AssemblyInstruction(AssemblyOp.ASL, null),  // * 8
            AssemblyInstruction(AssemblyOp.ASL, null),  // * 16
            AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$20"))
        )

        // Test case 1: length = 0x02 (2) → size = 32 (0x20)
        val testCase1 = DecompilerTestCase(
            name = "SMB Whirlpool Size (length=0x02)",
            assembly = "size = length * 16",
            setupMemory = mapOf(0x10 to 0x02u),
            setupRegisters = DecompilerTestCase.RegisterSetup(),
            checkMemory = listOf(0x20),
            ignoreFlags = setOf("N", "V", "Z", "C", "I", "D"),
            ignoreRegisters = setOf("A", "X", "Y")
        )

        DecompilerValidator().validate(testCase1, instructions)

        val interp1 = Interpreter6502()
        interp1.memory.writeByte(0x10, 0x02u)
        for (inst in instructions) {
            interp1.executeInstruction(inst)
        }
        assertEquals(0x20u, interp1.memory.readByte(0x20), "2 * 16 should be 32")

        // Test case 2: length = 0x0A (10) → size = 160 (0xA0)
        val testCase2 = DecompilerTestCase(
            name = "SMB Whirlpool Size (length=0x0A)",
            assembly = "size = length * 16",
            setupMemory = mapOf(0x10 to 0x0Au),
            setupRegisters = DecompilerTestCase.RegisterSetup(),
            checkMemory = listOf(0x20),
            ignoreFlags = setOf("N", "V", "Z", "C", "I", "D"),
            ignoreRegisters = setOf("A", "X", "Y")
        )

        DecompilerValidator().validate(testCase2, instructions)

        val interp2 = Interpreter6502()
        interp2.memory.writeByte(0x10, 0x0Au)
        for (inst in instructions) {
            interp2.executeInstruction(inst)
        }
        assertEquals(0xA0u, interp2.memory.readByte(0x20), "10 * 16 should be 160")

        // Test case 3: length = 0x0F (15) → size = 240 (0xF0)
        val testCase3 = DecompilerTestCase(
            name = "SMB Whirlpool Size (length=0x0F)",
            assembly = "size = length * 16",
            setupMemory = mapOf(0x10 to 0x0Fu),
            setupRegisters = DecompilerTestCase.RegisterSetup(),
            checkMemory = listOf(0x20),
            ignoreFlags = setOf("N", "V", "Z", "C", "I", "D"),
            ignoreRegisters = setOf("A", "X", "Y")
        )

        DecompilerValidator().validate(testCase3, instructions)

        val interp3 = Interpreter6502()
        interp3.memory.writeByte(0x10, 0x0Fu)
        for (inst in instructions) {
            interp3.executeInstruction(inst)
        }
        assertEquals(0xF0u, interp3.memory.readByte(0x20), "15 * 16 should be 240")
    }

    // ========================================================================
    // COMPLETE SUBROUTINES: Path toward full subroutine testing
    // ========================================================================

    /**
     * BLOCKED: Complete subroutine test - GetScreenPosition
     *
     * This test reveals a KEY LIMITATION of the current ExpressionDecompiler:
     * It cannot propagate carry flags between separate calculations.
     *
     * GetScreenPosition from SMB does:
     *   1. rightX = leftX + 255  (generates carry if overflow)
     *   2. rightPage = leftPage + 0 + carry  (uses carry from step 1)
     *
     * Current limitation: After STA in step 1, the carry information is lost.
     * The second ADC #$00 should add the carry, but we don't track carry output from expressions.
     *
     * To fix this, we need:
     * 1. Track carry OUTPUT from arithmetic expressions
     * 2. Propagate carry state between instructions
     * 3. Potentially generate flag variables in output code
     *
     * This is documented in SCALING_ROADMAP.md
     */
    @Test  // Re-enabled with carry propagation!
    fun testSMBCompleteGetScreenPosition() {
        val instructions = listOf(
            // Load left X position
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$071A")),  // ScreenLeft_X_Pos
            AssemblyInstruction(AssemblyOp.CLC),
            AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.ByteValue(0xFFu, AssemblyAddressing.Radix.Hex)),  // Add 255
            AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$0725")),  // ScreenRight_X_Pos
            // Add carry to page
            AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$071B")),  // ScreenLeft_PageLoc
            AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.ByteValue(0x00u, AssemblyAddressing.Radix.Hex)),  // Add 0 + carry
            AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$0726"))   // ScreenRight_PageLoc
        )

        // Test case 1: Left boundary at page 0, position 100
        // Right should be at page 1, position 99 (100 + 255 = 355 = page 1, pos 99)
        val testCase1 = DecompilerTestCase(
            name = "GetScreenPosition (page=0, pos=100)",
            assembly = "Calculate right screen boundary from left",
            setupMemory = mapOf(0x071A to 100u, 0x071B to 0u),
            setupRegisters = DecompilerTestCase.RegisterSetup(C = false),
            checkMemory = listOf(0x0725, 0x0726),
            ignoreFlags = setOf("N", "V", "Z", "C", "I", "D"),
            ignoreRegisters = setOf("A", "X", "Y")
        )

        DecompilerValidator().validate(testCase1, instructions)

        val interp1 = Interpreter6502()
        interp1.memory.writeByte(0x071A, 100u)
        interp1.memory.writeByte(0x071B, 0u)
        interp1.cpu.C = false
        for (inst in instructions) {
            interp1.executeInstruction(inst)
        }
        assertEquals(99u, interp1.memory.readByte(0x0725), "Right X should be 99 (100+255 mod 256)")
        assertEquals(1u, interp1.memory.readByte(0x0726), "Right page should be 1 (carry from 100+255)")

        // Test case 2: Left boundary at page 2, position 50
        // Right should be at page 3, position 49
        val testCase2 = DecompilerTestCase(
            name = "GetScreenPosition (page=2, pos=50)",
            assembly = "Calculate right screen boundary from left",
            setupMemory = mapOf(0x071A to 50u, 0x071B to 2u),
            setupRegisters = DecompilerTestCase.RegisterSetup(C = false),
            checkMemory = listOf(0x0725, 0x0726),
            ignoreFlags = setOf("N", "V", "Z", "C", "I", "D"),
            ignoreRegisters = setOf("A", "X", "Y")
        )

        DecompilerValidator().validate(testCase2, instructions)

        val interp2 = Interpreter6502()
        interp2.memory.writeByte(0x071A, 50u)
        interp2.memory.writeByte(0x071B, 2u)
        interp2.cpu.C = false
        for (inst in instructions) {
            interp2.executeInstruction(inst)
        }
        assertEquals(49u, interp2.memory.readByte(0x0725), "Right X should be 49")
        assertEquals(3u, interp2.memory.readByte(0x0726), "Right page should be 3")

        // Test case 3: Edge case - position 1 (minimal carry)
        // Right should be at page 1, position 0
        val testCase3 = DecompilerTestCase(
            name = "GetScreenPosition (page=0, pos=1, edge case)",
            assembly = "Calculate right screen boundary from left",
            setupMemory = mapOf(0x071A to 1u, 0x071B to 0u),
            setupRegisters = DecompilerTestCase.RegisterSetup(C = false),
            checkMemory = listOf(0x0725, 0x0726),
            ignoreFlags = setOf("N", "V", "Z", "C", "I", "D"),
            ignoreRegisters = setOf("A", "X", "Y")
        )

        DecompilerValidator().validate(testCase3, instructions)

        val interp3 = Interpreter6502()
        interp3.memory.writeByte(0x071A, 1u)
        interp3.memory.writeByte(0x071B, 0u)
        interp3.cpu.C = false
        for (inst in instructions) {
            interp3.executeInstruction(inst)
        }
        assertEquals(0u, interp3.memory.readByte(0x0725), "Right X should be 0 (1+255=256=0)")
        assertEquals(1u, interp3.memory.readByte(0x0726), "Right page should be 1")
    }

    /**
     * Show generated code for SMB tests
     */
    @Test
    fun showSMBGeneratedCode() {
        println("\n======================================================================")
        println("SMB DISASSEMBLY - GENERATED CODE")
        println("======================================================================\n")

        val testCases = mapOf(
            "SMB: Metatile * 4" to listOf(
                AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$80")),
                AssemblyInstruction(AssemblyOp.ASL, null),
                AssemblyInstruction(AssemblyOp.ASL, null),
                AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$02"))
            ),
            "SMB: Status Bar Offset" to listOf(
                AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$02")),
                AssemblyInstruction(AssemblyOp.CLC),
                AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.ByteValue(0x20u, AssemblyAddressing.Radix.Hex)),
                AssemblyInstruction(AssemblyOp.ASL, null),
                AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$04"))
            ),
            "SMB: Sprite Spacing" to listOf(
                AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$30")),
                AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$40")),
                AssemblyInstruction(AssemblyOp.CLC),
                AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.ByteValue(0x08u, AssemblyAddressing.Radix.Hex)),
                AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$44"))
            ),
            "SMB: Coin Position (10 instructions, 2 outputs)" to listOf(
                AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$06")),
                AssemblyInstruction(AssemblyOp.ASL, null),
                AssemblyInstruction(AssemblyOp.ASL, null),
                AssemblyInstruction(AssemblyOp.ASL, null),
                AssemblyInstruction(AssemblyOp.ASL, null),
                AssemblyInstruction(AssemblyOp.ORA, AssemblyAddressing.ByteValue(0x05u, AssemblyAddressing.Radix.Hex)),
                AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$50")),
                AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$02")),
                AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.ByteValue(0x20u, AssemblyAddressing.Radix.Hex)),
                AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$51"))
            ),
            "SMB: Whirlpool Size (* 16)" to listOf(
                AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$10")),
                AssemblyInstruction(AssemblyOp.ASL, null),
                AssemblyInstruction(AssemblyOp.ASL, null),
                AssemblyInstruction(AssemblyOp.ASL, null),
                AssemblyInstruction(AssemblyOp.ASL, null),
                AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$20"))
            ),
            "SMB: COMPLETE SUBROUTINE - GetScreenPosition" to listOf(
                AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$071A")),
                AssemblyInstruction(AssemblyOp.CLC),
                AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.ByteValue(0xFFu, AssemblyAddressing.Radix.Hex)),
                AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$0725")),
                AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.Direct("\$071B")),
                AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.ByteValue(0x00u, AssemblyAddressing.Radix.Hex)),
                AssemblyInstruction(AssemblyOp.STA, AssemblyAddressing.Direct("\$0726"))
            )
        )

        val decompiler = ExpressionDecompiler()
        for ((name, instructions) in testCases) {
            val code = decompiler.generateKotlinCode(instructions)
            println("### $name ###")
            // Extract just the execute() body
            val lines = code.lines()
            val executeStart = lines.indexOfFirst { it.contains("fun execute()") }
            if (executeStart != -1) {
                for (i in executeStart + 1 until lines.size) {
                    val line = lines[i].trim()
                    if (line == "}") break
                    if (line.startsWith("memory[")) {
                        println("        $line")
                    }
                }
            }
            println()
        }

        println("======================================================================")
    }
}
