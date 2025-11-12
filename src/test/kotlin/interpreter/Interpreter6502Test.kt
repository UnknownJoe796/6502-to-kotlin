package com.ivieleague.decompiler6502tokotlin.interpreter

import com.ivieleague.decompiler6502tokotlin.hand.*
import org.junit.Test
import kotlin.test.*

class Interpreter6502Test {

    @Test
    fun testLDAImmediate() {
        val interp = Interpreter6502()
        val instruction = AssemblyInstruction(
            AssemblyOp.LDA,
            AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)
        )

        interp.executeInstruction(instruction)

        assertEquals(0x42u, interp.cpu.A)
        assertFalse(interp.cpu.Z)
        assertFalse(interp.cpu.N)
    }

    @Test
    fun testLDAZeroFlag() {
        val interp = Interpreter6502()
        val instruction = AssemblyInstruction(
            AssemblyOp.LDA,
            AssemblyAddressing.ByteValue(0x00u, AssemblyAddressing.Radix.Hex)
        )

        interp.executeInstruction(instruction)

        assertEquals(0x00u, interp.cpu.A)
        assertTrue(interp.cpu.Z)
        assertFalse(interp.cpu.N)
    }

    @Test
    fun testLDANegativeFlag() {
        val interp = Interpreter6502()
        val instruction = AssemblyInstruction(
            AssemblyOp.LDA,
            AssemblyAddressing.ByteValue(0x80u, AssemblyAddressing.Radix.Hex)
        )

        interp.executeInstruction(instruction)

        assertEquals(0x80u, interp.cpu.A)
        assertFalse(interp.cpu.Z)
        assertTrue(interp.cpu.N)
    }

    @Test
    fun testLDAFromMemory() {
        val interp = Interpreter6502()
        interp.memory.writeByte(0x1000, 0x55u)
        interp.labelResolver = { label -> if (label == "1000") 0x1000 else 0 }

        val instruction = AssemblyInstruction(
            AssemblyOp.LDA,
            AssemblyAddressing.Direct("1000")
        )

        interp.executeInstruction(instruction)

        assertEquals(0x55u, interp.cpu.A)
    }

    @Test
    fun testLDXAndLDY() {
        val interp = Interpreter6502()

        val ldx = AssemblyInstruction(
            AssemblyOp.LDX,
            AssemblyAddressing.ByteValue(0x12u, AssemblyAddressing.Radix.Hex)
        )
        interp.executeInstruction(ldx)
        assertEquals(0x12u, interp.cpu.X)

        val ldy = AssemblyInstruction(
            AssemblyOp.LDY,
            AssemblyAddressing.ByteValue(0x34u, AssemblyAddressing.Radix.Hex)
        )
        interp.executeInstruction(ldy)
        assertEquals(0x34u, interp.cpu.Y)
    }

    @Test
    fun testSTA() {
        val interp = Interpreter6502()
        interp.cpu.A = 0x42u
        interp.labelResolver = { label -> if (label == "1000") 0x1000 else 0 }

        val instruction = AssemblyInstruction(
            AssemblyOp.STA,
            AssemblyAddressing.Direct("1000")
        )

        interp.executeInstruction(instruction)

        assertEquals(0x42u, interp.memory.readByte(0x1000))
    }

    @Test
    fun testSTXAndSTY() {
        val interp = Interpreter6502()
        interp.cpu.X = 0x11u
        interp.cpu.Y = 0x22u
        interp.labelResolver = { label ->
            when (label) {
                "1000" -> 0x1000
                "2000" -> 0x2000
                else -> 0
            }
        }

        val stx = AssemblyInstruction(
            AssemblyOp.STX,
            AssemblyAddressing.Direct("1000")
        )
        interp.executeInstruction(stx)
        assertEquals(0x11u, interp.memory.readByte(0x1000))

        val sty = AssemblyInstruction(
            AssemblyOp.STY,
            AssemblyAddressing.Direct("2000")
        )
        interp.executeInstruction(sty)
        assertEquals(0x22u, interp.memory.readByte(0x2000))
    }

    @Test
    fun testTransferInstructions() {
        val interp = Interpreter6502()

        // TAX
        interp.cpu.A = 0x42u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.TAX))
        assertEquals(0x42u, interp.cpu.X)

        // TAY
        interp.cpu.A = 0x55u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.TAY))
        assertEquals(0x55u, interp.cpu.Y)

        // TXA
        interp.cpu.X = 0x11u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.TXA))
        assertEquals(0x11u, interp.cpu.A)

        // TYA
        interp.cpu.Y = 0x22u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.TYA))
        assertEquals(0x22u, interp.cpu.A)

        // TSX
        interp.cpu.SP = 0xFDu
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.TSX))
        assertEquals(0xFDu, interp.cpu.X)

        // TXS
        interp.cpu.X = 0x50u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.TXS))
        assertEquals(0x50u, interp.cpu.SP)
    }

    @Test
    fun testStackOperations() {
        val interp = Interpreter6502()

        // PHA - Push accumulator
        interp.cpu.A = 0x42u
        interp.cpu.SP = 0xFFu
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PHA))
        assertEquals(0xFEu, interp.cpu.SP)
        assertEquals(0x42u, interp.memory.readByte(0x01FF))

        // PLA - Pull accumulator
        interp.cpu.A = 0x00u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PLA))
        assertEquals(0x42u, interp.cpu.A)
        assertEquals(0xFFu, interp.cpu.SP)
    }

    @Test
    fun testPHPAndPLP() {
        val interp = Interpreter6502()

        // Set some flags
        interp.cpu.N = true
        interp.cpu.Z = true
        interp.cpu.C = true
        interp.cpu.SP = 0xFFu

        // PHP - Push status
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PHP))

        // Clear flags
        interp.cpu.N = false
        interp.cpu.Z = false
        interp.cpu.C = false

        // PLP - Pull status
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.PLP))

        assertTrue(interp.cpu.N)
        assertTrue(interp.cpu.Z)
        assertTrue(interp.cpu.C)
    }

    @Test
    fun testAND() {
        val interp = Interpreter6502()
        interp.cpu.A = 0b11110000u

        val instruction = AssemblyInstruction(
            AssemblyOp.AND,
            AssemblyAddressing.ByteValue(0b10101010u, AssemblyAddressing.Radix.Binary)
        )

        interp.executeInstruction(instruction)

        assertEquals(0b10100000u, interp.cpu.A)
        assertFalse(interp.cpu.Z)
        assertTrue(interp.cpu.N)
    }

    @Test
    fun testEOR() {
        val interp = Interpreter6502()
        interp.cpu.A = 0b11110000u

        val instruction = AssemblyInstruction(
            AssemblyOp.EOR,
            AssemblyAddressing.ByteValue(0b10101010u, AssemblyAddressing.Radix.Binary)
        )

        interp.executeInstruction(instruction)

        assertEquals(0b01011010u, interp.cpu.A)
        assertFalse(interp.cpu.Z)
        assertFalse(interp.cpu.N)
    }

    @Test
    fun testORA() {
        val interp = Interpreter6502()
        interp.cpu.A = 0b11110000u

        val instruction = AssemblyInstruction(
            AssemblyOp.ORA,
            AssemblyAddressing.ByteValue(0b10101010u, AssemblyAddressing.Radix.Binary)
        )

        interp.executeInstruction(instruction)

        assertEquals(0b11111010u, interp.cpu.A)
        assertFalse(interp.cpu.Z)
        assertTrue(interp.cpu.N)
    }

    @Test
    fun testBIT() {
        val interp = Interpreter6502()
        interp.cpu.A = 0b00001111u
        interp.memory.writeByte(0x1000, 0b11000000u)
        interp.labelResolver = { label -> if (label == "1000") 0x1000 else 0 }

        val instruction = AssemblyInstruction(
            AssemblyOp.BIT,
            AssemblyAddressing.Direct("1000")
        )

        interp.executeInstruction(instruction)

        // N and V flags are set from bits 7 and 6 of the memory value
        assertTrue(interp.cpu.N)
        assertTrue(interp.cpu.V)
        // Z flag is set if (A & memory) == 0
        assertTrue(interp.cpu.Z)
    }

    @Test
    fun testADC() {
        val interp = Interpreter6502()

        // Simple addition
        interp.cpu.A = 0x10u
        interp.cpu.C = false
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x20u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x30u, interp.cpu.A)
        assertFalse(interp.cpu.C)
        assertFalse(interp.cpu.V)

        // Addition with carry
        interp.cpu.A = 0x50u
        interp.cpu.C = true
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x50u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0xA1u, interp.cpu.A)
        assertFalse(interp.cpu.C)

        // Addition causing carry
        interp.cpu.A = 0xFFu
        interp.cpu.C = false
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x02u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x01u, interp.cpu.A)
        assertTrue(interp.cpu.C)
    }

    @Test
    fun testSBC() {
        val interp = Interpreter6502()

        // Simple subtraction (carry flag must be set for no borrow)
        interp.cpu.A = 0x50u
        interp.cpu.C = true
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.SBC,
                AssemblyAddressing.ByteValue(0x30u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x20u, interp.cpu.A)
        assertTrue(interp.cpu.C)

        // Subtraction causing borrow
        interp.cpu.A = 0x10u
        interp.cpu.C = true
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.SBC,
                AssemblyAddressing.ByteValue(0x20u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0xF0u, interp.cpu.A)
        assertFalse(interp.cpu.C)
    }

    @Test
    fun testCMP() {
        val interp = Interpreter6502()

        // A == operand
        interp.cpu.A = 0x42u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.CMP,
                AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertTrue(interp.cpu.Z)
        assertTrue(interp.cpu.C)

        // A > operand
        interp.cpu.A = 0x50u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.CMP,
                AssemblyAddressing.ByteValue(0x30u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertFalse(interp.cpu.Z)
        assertTrue(interp.cpu.C)

        // A < operand
        interp.cpu.A = 0x10u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.CMP,
                AssemblyAddressing.ByteValue(0x20u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertFalse(interp.cpu.Z)
        assertFalse(interp.cpu.C)
    }

    @Test
    fun testCPXAndCPY() {
        val interp = Interpreter6502()

        // CPX
        interp.cpu.X = 0x42u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.CPX,
                AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertTrue(interp.cpu.Z)
        assertTrue(interp.cpu.C)

        // CPY
        interp.cpu.Y = 0x50u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.CPY,
                AssemblyAddressing.ByteValue(0x30u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertFalse(interp.cpu.Z)
        assertTrue(interp.cpu.C)
    }

    @Test
    fun testINCAndDEC() {
        val interp = Interpreter6502()
        interp.memory.writeByte(0x1000, 0x42u)
        interp.labelResolver = { label -> if (label == "1000") 0x1000 else 0 }

        // INC
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.INC,
                AssemblyAddressing.Direct("1000")
            )
        )
        assertEquals(0x43u, interp.memory.readByte(0x1000))

        // DEC
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.DEC,
                AssemblyAddressing.Direct("1000")
            )
        )
        assertEquals(0x42u, interp.memory.readByte(0x1000))
    }

    @Test
    fun testINXINYDEXDEY() {
        val interp = Interpreter6502()

        // INX
        interp.cpu.X = 0x10u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.INX))
        assertEquals(0x11u, interp.cpu.X)

        // INY
        interp.cpu.Y = 0x20u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.INY))
        assertEquals(0x21u, interp.cpu.Y)

        // DEX
        interp.cpu.X = 0x15u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.DEX))
        assertEquals(0x14u, interp.cpu.X)

        // DEY
        interp.cpu.Y = 0x25u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.DEY))
        assertEquals(0x24u, interp.cpu.Y)
    }

    @Test
    fun testASL() {
        val interp = Interpreter6502()

        // ASL Accumulator
        interp.cpu.A = 0b01010101u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ASL, null))
        assertEquals(0b10101010u, interp.cpu.A)
        assertFalse(interp.cpu.C)
        assertTrue(interp.cpu.N)

        // ASL with carry
        interp.cpu.A = 0b10000000u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ASL, null))
        assertEquals(0x00u, interp.cpu.A)
        assertTrue(interp.cpu.C)
        assertTrue(interp.cpu.Z)
    }

    @Test
    fun testLSR() {
        val interp = Interpreter6502()

        // LSR Accumulator
        interp.cpu.A = 0b10101010u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.LSR, null))
        assertEquals(0b01010101u, interp.cpu.A)
        assertFalse(interp.cpu.C)

        // LSR with carry
        interp.cpu.A = 0b00000001u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.LSR, null))
        assertEquals(0x00u, interp.cpu.A)
        assertTrue(interp.cpu.C)
        assertTrue(interp.cpu.Z)
    }

    @Test
    fun testROL() {
        val interp = Interpreter6502()

        // ROL without carry
        interp.cpu.A = 0b01010101u
        interp.cpu.C = false
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ROL, null))
        assertEquals(0b10101010u, interp.cpu.A)
        assertFalse(interp.cpu.C)

        // ROL with carry in
        interp.cpu.A = 0b01010101u
        interp.cpu.C = true
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ROL, null))
        assertEquals(0b10101011u, interp.cpu.A)
        assertFalse(interp.cpu.C)

        // ROL with carry out
        interp.cpu.A = 0b10000000u
        interp.cpu.C = false
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ROL, null))
        assertEquals(0x00u, interp.cpu.A)
        assertTrue(interp.cpu.C)
    }

    @Test
    fun testROR() {
        val interp = Interpreter6502()

        // ROR without carry
        interp.cpu.A = 0b10101010u
        interp.cpu.C = false
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ROR, null))
        assertEquals(0b01010101u, interp.cpu.A)
        assertFalse(interp.cpu.C)

        // ROR with carry in
        interp.cpu.A = 0b10101010u
        interp.cpu.C = true
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ROR, null))
        assertEquals(0b11010101u, interp.cpu.A)
        assertFalse(interp.cpu.C)

        // ROR with carry out
        interp.cpu.A = 0b00000001u
        interp.cpu.C = false
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ROR, null))
        assertEquals(0x00u, interp.cpu.A)
        assertTrue(interp.cpu.C)
    }

    @Test
    fun testBranchInstructions() {
        val interp = Interpreter6502()

        // BCC - Branch if carry clear
        interp.cpu.C = false
        assertTrue(interp.shouldBranch(AssemblyInstruction(AssemblyOp.BCC)))
        interp.cpu.C = true
        assertFalse(interp.shouldBranch(AssemblyInstruction(AssemblyOp.BCC)))

        // BCS - Branch if carry set
        interp.cpu.C = true
        assertTrue(interp.shouldBranch(AssemblyInstruction(AssemblyOp.BCS)))
        interp.cpu.C = false
        assertFalse(interp.shouldBranch(AssemblyInstruction(AssemblyOp.BCS)))

        // BEQ - Branch if zero set
        interp.cpu.Z = true
        assertTrue(interp.shouldBranch(AssemblyInstruction(AssemblyOp.BEQ)))
        interp.cpu.Z = false
        assertFalse(interp.shouldBranch(AssemblyInstruction(AssemblyOp.BEQ)))

        // BNE - Branch if zero clear
        interp.cpu.Z = false
        assertTrue(interp.shouldBranch(AssemblyInstruction(AssemblyOp.BNE)))
        interp.cpu.Z = true
        assertFalse(interp.shouldBranch(AssemblyInstruction(AssemblyOp.BNE)))

        // BMI - Branch if negative set
        interp.cpu.N = true
        assertTrue(interp.shouldBranch(AssemblyInstruction(AssemblyOp.BMI)))
        interp.cpu.N = false
        assertFalse(interp.shouldBranch(AssemblyInstruction(AssemblyOp.BMI)))

        // BPL - Branch if negative clear
        interp.cpu.N = false
        assertTrue(interp.shouldBranch(AssemblyInstruction(AssemblyOp.BPL)))
        interp.cpu.N = true
        assertFalse(interp.shouldBranch(AssemblyInstruction(AssemblyOp.BPL)))

        // BVC - Branch if overflow clear
        interp.cpu.V = false
        assertTrue(interp.shouldBranch(AssemblyInstruction(AssemblyOp.BVC)))
        interp.cpu.V = true
        assertFalse(interp.shouldBranch(AssemblyInstruction(AssemblyOp.BVC)))

        // BVS - Branch if overflow set
        interp.cpu.V = true
        assertTrue(interp.shouldBranch(AssemblyInstruction(AssemblyOp.BVS)))
        interp.cpu.V = false
        assertFalse(interp.shouldBranch(AssemblyInstruction(AssemblyOp.BVS)))
    }

    @Test
    fun testFlagInstructions() {
        val interp = Interpreter6502()

        // Set flags
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.SEC))
        assertTrue(interp.cpu.C)

        interp.executeInstruction(AssemblyInstruction(AssemblyOp.SEI))
        assertTrue(interp.cpu.I)

        interp.executeInstruction(AssemblyInstruction(AssemblyOp.SED))
        assertTrue(interp.cpu.D)

        // Clear flags
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.CLC))
        assertFalse(interp.cpu.C)

        interp.executeInstruction(AssemblyInstruction(AssemblyOp.CLI))
        assertFalse(interp.cpu.I)

        interp.executeInstruction(AssemblyInstruction(AssemblyOp.CLD))
        assertFalse(interp.cpu.D)

        interp.cpu.V = true
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.CLV))
        assertFalse(interp.cpu.V)
    }

    @Test
    fun testNOP() {
        val interp = Interpreter6502()
        val cpuBefore = interp.cpu.toString()

        interp.executeInstruction(AssemblyInstruction(AssemblyOp.NOP))

        // CPU state should be unchanged
        assertEquals(cpuBefore, interp.cpu.toString())
    }

    @Test
    fun testBRK() {
        val interp = Interpreter6502()
        assertFalse(interp.halted)

        interp.executeInstruction(AssemblyInstruction(AssemblyOp.BRK))

        assertTrue(interp.halted)
    }

    @Test
    fun testIndexedAddressing() {
        val interp = Interpreter6502()
        interp.memory.writeByte(0x1005, 0x42u)
        interp.cpu.X = 0x05u
        interp.labelResolver = { label -> if (label == "1000") 0x1000 else 0 }

        val instruction = AssemblyInstruction(
            AssemblyOp.LDA,
            AssemblyAddressing.DirectX("1000")
        )

        interp.executeInstruction(instruction)

        assertEquals(0x42u, interp.cpu.A)
    }

    @Test
    fun testIndirectYAddressing() {
        val interp = Interpreter6502()
        // Set up zero page pointer
        interp.memory.writeWord(0x0010, 0x2000u)
        // Write value at effective address (0x2000 + Y)
        interp.cpu.Y = 0x05u
        interp.memory.writeByte(0x2005, 0x77u)
        interp.labelResolver = { label -> if (label == "10") 0x10 else 0 }

        val instruction = AssemblyInstruction(
            AssemblyOp.LDA,
            AssemblyAddressing.IndirectY("10")
        )

        interp.executeInstruction(instruction)

        assertEquals(0x77u, interp.cpu.A)
    }

    @Test
    fun testReset() {
        val interp = Interpreter6502()
        interp.cpu.A = 0x42u
        interp.memory.writeByte(0x1000, 0x55u)
        interp.halted = true

        interp.reset()

        assertEquals(0u, interp.cpu.A)
        assertEquals(0u, interp.memory.readByte(0x1000))
        assertFalse(interp.halted)
    }

    @Test
    fun testHexLabelResolution() {
        val interp = Interpreter6502()
        interp.memory.writeByte(0x1234, 0x99u)

        val instruction = AssemblyInstruction(
            AssemblyOp.LDA,
            AssemblyAddressing.Direct("$1234")
        )

        interp.executeInstruction(instruction)

        assertEquals(0x99u, interp.cpu.A)
    }

    @Test
    fun testDecimalLabelResolution() {
        val interp = Interpreter6502()
        interp.memory.writeByte(100, 0x88u)

        val instruction = AssemblyInstruction(
            AssemblyOp.LDA,
            AssemblyAddressing.Direct("100")
        )

        interp.executeInstruction(instruction)

        assertEquals(0x88u, interp.cpu.A)
    }
}
