// by Claude - Phase 1: Instruction Semantics Verification
// Tests that verify generated Kotlin code matches interpreter behavior for each 6502 instruction
package com.ivieleague.decompiler6502tokotlin.decompiler

import com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp
import com.ivieleague.decompiler6502tokotlin.testing.*
import kotlin.test.Test

/**
 * Instruction Semantics Test Suite
 *
 * This is the foundation for decompiler correctness verification.
 * Each test verifies that a specific 6502 instruction produces the same
 * result when:
 * 1. Executed through the interpreter
 * 2. Converted to Kotlin AST and evaluated
 *
 * Success criteria: 100% of instruction tests pass for all addressing modes.
 */
class InstructionSemanticsTest {

    private val harness = InstructionTestHarness()

    // =====================================================================
    // LOAD INSTRUCTIONS (LDA, LDX, LDY)
    // All set Z flag if value == 0, N flag if bit 7 is set
    // =====================================================================

    @Test
    fun `LDA immediate - normal value`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.LDA, imm(0x42)),
            initial = InstructionTestHarness.InitialState(A = 0xFF)
        ).assertMatches()
    }

    @Test
    fun `LDA immediate - zero value sets Z flag`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.LDA, imm(0x00)),
            initial = InstructionTestHarness.InitialState(A = 0xFF, Z = false, N = true)
        ).assertMatches()
    }

    @Test
    fun `LDA immediate - negative value sets N flag`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.LDA, imm(0x80)),
            initial = InstructionTestHarness.InitialState(A = 0x00, Z = true, N = false)
        ).assertMatches()
    }

    @Test
    fun `LDA immediate - high negative value`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.LDA, imm(0xFF)),
            initial = InstructionTestHarness.InitialState(A = 0x00)
        ).assertMatches()
    }

    @Test
    fun `LDA direct - reads from memory`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.LDA, direct(0x1000)),
            initial = InstructionTestHarness.InitialState(
                A = 0x00,
                memory = mapOf(0x1000 to 0x55)
            )
        ).assertMatches()
    }

    @Test
    fun `LDX immediate - normal value`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.LDX, imm(0x12)),
            initial = InstructionTestHarness.InitialState(X = 0xFF)
        ).assertMatches()
    }

    @Test
    fun `LDX immediate - zero value sets Z flag`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.LDX, imm(0x00)),
            initial = InstructionTestHarness.InitialState(X = 0x55, Z = false)
        ).assertMatches()
    }

    @Test
    fun `LDX immediate - negative value sets N flag`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.LDX, imm(0x80)),
            initial = InstructionTestHarness.InitialState(X = 0x00, N = false)
        ).assertMatches()
    }

    @Test
    fun `LDY immediate - normal value`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.LDY, imm(0x34)),
            initial = InstructionTestHarness.InitialState(Y = 0x00)
        ).assertMatches()
    }

    @Test
    fun `LDY immediate - zero value sets Z flag`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.LDY, imm(0x00)),
            initial = InstructionTestHarness.InitialState(Y = 0xAB, Z = false)
        ).assertMatches()
    }

    @Test
    fun `LDY immediate - negative value sets N flag`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.LDY, imm(0xFE)),
            initial = InstructionTestHarness.InitialState(Y = 0x00, N = false)
        ).assertMatches()
    }

    // =====================================================================
    // STORE INSTRUCTIONS (STA, STX, STY)
    // These don't affect any flags
    // =====================================================================

    @Test
    fun `STA direct - stores A to memory`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.STA, direct(0x1000)),
            initial = InstructionTestHarness.InitialState(A = 0x42)
        ).assertMatches()
    }

    @Test
    fun `STX direct - stores X to memory`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.STX, direct(0x1000)),
            initial = InstructionTestHarness.InitialState(X = 0x11)
        ).assertMatches()
    }

    @Test
    fun `STY direct - stores Y to memory`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.STY, direct(0x1000)),
            initial = InstructionTestHarness.InitialState(Y = 0x22)
        ).assertMatches()
    }

    // =====================================================================
    // TRANSFER INSTRUCTIONS (TAX, TAY, TXA, TYA, TSX, TXS)
    // TAX, TAY, TXA, TYA set Z and N flags; TSX sets Z and N; TXS doesn't
    // =====================================================================

    @Test
    fun `TAX - transfers A to X`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.TAX),
            initial = InstructionTestHarness.InitialState(A = 0x42, X = 0x00)
        ).assertMatches()
    }

    @Test
    fun `TAX - zero value sets Z flag`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.TAX),
            initial = InstructionTestHarness.InitialState(A = 0x00, X = 0xFF, Z = false)
        ).assertMatches()
    }

    @Test
    fun `TAX - negative value sets N flag`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.TAX),
            initial = InstructionTestHarness.InitialState(A = 0x80, X = 0x00, N = false)
        ).assertMatches()
    }

    @Test
    fun `TAY - transfers A to Y`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.TAY),
            initial = InstructionTestHarness.InitialState(A = 0x55, Y = 0x00)
        ).assertMatches()
    }

    @Test
    fun `TXA - transfers X to A`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.TXA),
            initial = InstructionTestHarness.InitialState(X = 0x11, A = 0x00)
        ).assertMatches()
    }

    @Test
    fun `TYA - transfers Y to A`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.TYA),
            initial = InstructionTestHarness.InitialState(Y = 0x22, A = 0x00)
        ).assertMatches()
    }

    // =====================================================================
    // INCREMENT/DECREMENT INSTRUCTIONS (INX, INY, DEX, DEY, INC, DEC)
    // All set Z and N flags based on result
    // =====================================================================

    @Test
    fun `INX - increments X`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.INX),
            initial = InstructionTestHarness.InitialState(X = 0x10)
        ).assertMatches()
    }

    @Test
    fun `INX - wraps from FF to 00 and sets Z flag`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.INX),
            initial = InstructionTestHarness.InitialState(X = 0xFF, Z = false)
        ).assertMatches()
    }

    @Test
    fun `INX - 7F to 80 sets N flag`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.INX),
            initial = InstructionTestHarness.InitialState(X = 0x7F, N = false)
        ).assertMatches()
    }

    @Test
    fun `INY - increments Y`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.INY),
            initial = InstructionTestHarness.InitialState(Y = 0x20)
        ).assertMatches()
    }

    @Test
    fun `INY - wraps from FF to 00`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.INY),
            initial = InstructionTestHarness.InitialState(Y = 0xFF)
        ).assertMatches()
    }

    @Test
    fun `DEX - decrements X`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.DEX),
            initial = InstructionTestHarness.InitialState(X = 0x15)
        ).assertMatches()
    }

    @Test
    fun `DEX - 01 to 00 sets Z flag`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.DEX),
            initial = InstructionTestHarness.InitialState(X = 0x01, Z = false)
        ).assertMatches()
    }

    @Test
    fun `DEX - 00 to FF sets N flag`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.DEX),
            initial = InstructionTestHarness.InitialState(X = 0x00, N = false)
        ).assertMatches()
    }

    @Test
    fun `DEY - decrements Y`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.DEY),
            initial = InstructionTestHarness.InitialState(Y = 0x25)
        ).assertMatches()
    }

    @Test
    fun `DEY - wraps from 00 to FF`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.DEY),
            initial = InstructionTestHarness.InitialState(Y = 0x00)
        ).assertMatches()
    }

    // =====================================================================
    // LOGICAL INSTRUCTIONS (AND, ORA, EOR)
    // All set Z and N flags based on result
    // =====================================================================

    @Test
    fun `AND immediate - basic and operation`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.AND, imm(0x0F)),
            initial = InstructionTestHarness.InitialState(A = 0xFF)
        ).assertMatches()
    }

    @Test
    fun `AND immediate - result zero sets Z flag`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.AND, imm(0x0F)),
            initial = InstructionTestHarness.InitialState(A = 0xF0, Z = false)
        ).assertMatches()
    }

    @Test
    fun `AND immediate - high bit set sets N flag`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.AND, imm(0xFF)),
            initial = InstructionTestHarness.InitialState(A = 0x80, N = false)
        ).assertMatches()
    }

    @Test
    fun `ORA immediate - basic or operation`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.ORA, imm(0x0F)),
            initial = InstructionTestHarness.InitialState(A = 0xF0)
        ).assertMatches()
    }

    @Test
    fun `ORA immediate - zero or zero is zero`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.ORA, imm(0x00)),
            initial = InstructionTestHarness.InitialState(A = 0x00)
        ).assertMatches()
    }

    @Test
    fun `EOR immediate - basic xor operation`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.EOR, imm(0xFF)),
            initial = InstructionTestHarness.InitialState(A = 0xAA)
        ).assertMatches()
    }

    @Test
    fun `EOR immediate - xor with same value gives zero`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.EOR, imm(0x42)),
            initial = InstructionTestHarness.InitialState(A = 0x42, Z = false)
        ).assertMatches()
    }

    // =====================================================================
    // ARITHMETIC INSTRUCTIONS (ADC, SBC)
    // Set C (carry/borrow), V (overflow), Z, N flags
    // =====================================================================

    @Test
    fun `ADC immediate - simple addition no carry`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.ADC, imm(0x20)),
            initial = InstructionTestHarness.InitialState(A = 0x10, C = false)
        ).assertMatches()
    }

    @Test
    fun `ADC immediate - addition with carry in`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.ADC, imm(0x50)),
            initial = InstructionTestHarness.InitialState(A = 0x50, C = true)
        ).assertMatches()
    }

    @Test
    fun `ADC immediate - overflow sets carry`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.ADC, imm(0x02)),
            initial = InstructionTestHarness.InitialState(A = 0xFF, C = false)
        ).assertMatches()
    }

    @Test
    fun `ADC immediate - signed overflow sets V flag`() {
        // 0x7F + 0x01 = 0x80 (127 + 1 = -128 in signed, overflow!)
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.ADC, imm(0x01)),
            initial = InstructionTestHarness.InitialState(A = 0x7F, C = false, V = false)
        ).assertMatches()
    }

    @Test
    fun `ADC immediate - negative plus negative overflow`() {
        // 0x80 + 0x80 = 0x100 -> 0x00 (overflow and carry)
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.ADC, imm(0x80)),
            initial = InstructionTestHarness.InitialState(A = 0x80, C = false)
        ).assertMatches()
    }

    @Test
    fun `ADC immediate - result zero sets Z flag`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.ADC, imm(0x01)),
            initial = InstructionTestHarness.InitialState(A = 0xFF, C = false, Z = false)
        ).assertMatches()
    }

    @Test
    fun `SBC immediate - simple subtraction with carry set`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.SBC, imm(0x30)),
            initial = InstructionTestHarness.InitialState(A = 0x50, C = true)
        ).assertMatches()
    }

    @Test
    fun `SBC immediate - subtraction with borrow (carry clear)`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.SBC, imm(0x30)),
            initial = InstructionTestHarness.InitialState(A = 0x50, C = false)
        ).assertMatches()
    }

    @Test
    fun `SBC immediate - underflow clears carry`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.SBC, imm(0x20)),
            initial = InstructionTestHarness.InitialState(A = 0x10, C = true)
        ).assertMatches()
    }

    @Test
    fun `SBC immediate - signed overflow sets V flag`() {
        // 0x80 - 0x01 = 0x7F (-128 - 1 = 127 in signed, overflow!)
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.SBC, imm(0x01)),
            initial = InstructionTestHarness.InitialState(A = 0x80, C = true, V = false)
        ).assertMatches()
    }

    // =====================================================================
    // COMPARE INSTRUCTIONS (CMP, CPX, CPY)
    // Set C, Z, N flags based on subtraction (A/X/Y - operand)
    // =====================================================================

    @Test
    fun `CMP immediate - equal values set Z and C`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.CMP, imm(0x42)),
            initial = InstructionTestHarness.InitialState(A = 0x42)
        ).assertMatches()
    }

    @Test
    fun `CMP immediate - A greater sets C, clears Z`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.CMP, imm(0x30)),
            initial = InstructionTestHarness.InitialState(A = 0x50)
        ).assertMatches()
    }

    @Test
    fun `CMP immediate - A less clears C`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.CMP, imm(0x20)),
            initial = InstructionTestHarness.InitialState(A = 0x10)
        ).assertMatches()
    }

    @Test
    fun `CMP immediate - negative result sets N`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.CMP, imm(0x01)),
            initial = InstructionTestHarness.InitialState(A = 0x00)
        ).assertMatches()
    }

    @Test
    fun `CPX immediate - equal values`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.CPX, imm(0x42)),
            initial = InstructionTestHarness.InitialState(X = 0x42)
        ).assertMatches()
    }

    @Test
    fun `CPX immediate - X greater`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.CPX, imm(0x30)),
            initial = InstructionTestHarness.InitialState(X = 0x50)
        ).assertMatches()
    }

    @Test
    fun `CPY immediate - equal values`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.CPY, imm(0x42)),
            initial = InstructionTestHarness.InitialState(Y = 0x42)
        ).assertMatches()
    }

    @Test
    fun `CPY immediate - Y less`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.CPY, imm(0x50)),
            initial = InstructionTestHarness.InitialState(Y = 0x30)
        ).assertMatches()
    }

    // =====================================================================
    // SHIFT INSTRUCTIONS (ASL, LSR, ROL, ROR)
    // All affect C, Z, N flags
    // =====================================================================

    @Test
    fun `ASL accumulator - shift left`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.ASL, null),  // Accumulator mode
            initial = InstructionTestHarness.InitialState(A = 0b01010101)
        ).assertMatches()
    }

    @Test
    fun `ASL accumulator - high bit goes to carry`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.ASL, null),
            initial = InstructionTestHarness.InitialState(A = 0b10000000, C = false)
        ).assertMatches()
    }

    @Test
    fun `ASL accumulator - shifts in zero`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.ASL, null),
            initial = InstructionTestHarness.InitialState(A = 0b10000001)
        ).assertMatches()
    }

    @Test
    fun `LSR accumulator - shift right`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.LSR, null),
            initial = InstructionTestHarness.InitialState(A = 0b10101010)
        ).assertMatches()
    }

    @Test
    fun `LSR accumulator - low bit goes to carry`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.LSR, null),
            initial = InstructionTestHarness.InitialState(A = 0b00000001, C = false)
        ).assertMatches()
    }

    @Test
    fun `LSR accumulator - always clears N flag`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.LSR, null),
            initial = InstructionTestHarness.InitialState(A = 0xFF, N = true)
        ).assertMatches()
    }

    @Test
    fun `ROL accumulator - rotate left without carry`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.ROL, null),
            initial = InstructionTestHarness.InitialState(A = 0b01010101, C = false)
        ).assertMatches()
    }

    @Test
    fun `ROL accumulator - rotate left with carry`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.ROL, null),
            initial = InstructionTestHarness.InitialState(A = 0b01010101, C = true)
        ).assertMatches()
    }

    @Test
    fun `ROL accumulator - high bit goes to carry`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.ROL, null),
            initial = InstructionTestHarness.InitialState(A = 0b10000000, C = false)
        ).assertMatches()
    }

    @Test
    fun `ROR accumulator - rotate right without carry`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.ROR, null),
            initial = InstructionTestHarness.InitialState(A = 0b10101010, C = false)
        ).assertMatches()
    }

    @Test
    fun `ROR accumulator - rotate right with carry`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.ROR, null),
            initial = InstructionTestHarness.InitialState(A = 0b10101010, C = true)
        ).assertMatches()
    }

    @Test
    fun `ROR accumulator - low bit goes to carry`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.ROR, null),
            initial = InstructionTestHarness.InitialState(A = 0b00000001, C = false)
        ).assertMatches()
    }

    // =====================================================================
    // FLAG INSTRUCTIONS (CLC, SEC, CLV, CLD, SED, CLI, SEI)
    // =====================================================================

    @Test
    fun `CLC - clears carry flag`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.CLC),
            initial = InstructionTestHarness.InitialState(C = true)
        ).assertMatches()
    }

    @Test
    fun `SEC - sets carry flag`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.SEC),
            initial = InstructionTestHarness.InitialState(C = false)
        ).assertMatches()
    }

    @Test
    fun `CLV - clears overflow flag`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.CLV),
            initial = InstructionTestHarness.InitialState(V = true)
        ).assertMatches()
    }

    // =====================================================================
    // NOP - No operation
    // =====================================================================

    @Test
    fun `NOP - no state change`() {
        harness.verifyInstruction(
            instruction = instr(AssemblyOp.NOP),
            initial = InstructionTestHarness.InitialState(
                A = 0x42, X = 0x11, Y = 0x22, C = true, Z = false, N = true
            )
        ).assertMatches()
    }
}
