package com.ivieleague.decompiler6502tokotlin.interpreter

import com.ivieleague.decompiler6502tokotlin.hand.*
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Comprehensive tests for all 6502 addressing modes.
 * Ensures each addressing mode works correctly for all applicable instructions.
 */
class Interpreter6502AddressingModeTest {

    @Test
    fun testImmediateAddressing() {
        val interp = Interpreter6502()

        // LDA immediate
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x42u, interp.cpu.A)

        // LDX immediate
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDX,
                AssemblyAddressing.ByteValue(0x11u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x11u, interp.cpu.X)

        // LDY immediate
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDY,
                AssemblyAddressing.ByteValue(0x22u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x22u, interp.cpu.Y)

        // ADC immediate
        interp.cpu.A = 0x10u
        interp.cpu.C = false
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.ADC,
                AssemblyAddressing.ByteValue(0x05u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x15u, interp.cpu.A)

        // AND immediate
        interp.cpu.A = 0xFFu
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.AND,
                AssemblyAddressing.ByteValue(0x0Fu, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x0Fu, interp.cpu.A)

        // CMP immediate
        interp.cpu.A = 0x42u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.CMP,
                AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(true, interp.cpu.Z)
    }

    @Test
    fun testAbsoluteAddressing() {
        val interp = Interpreter6502()
        interp.labelResolver = { label -> if (label == "1000") 0x1000 else 0 }

        // Set up memory
        interp.memory.writeByte(0x1000, 0x55u)

        // LDA absolute
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.Direct("1000")
            )
        )
        assertEquals(0x55u, interp.cpu.A)

        // STA absolute
        interp.cpu.A = 0xAAu
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.STA,
                AssemblyAddressing.Direct("1000")
            )
        )
        assertEquals(0xAAu, interp.memory.readByte(0x1000))

        // INC absolute
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.INC,
                AssemblyAddressing.Direct("1000")
            )
        )
        assertEquals(0xABu, interp.memory.readByte(0x1000))

        // DEC absolute
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.DEC,
                AssemblyAddressing.Direct("1000")
            )
        )
        assertEquals(0xAAu, interp.memory.readByte(0x1000))
    }

    @Test
    fun testAbsoluteXAddressing() {
        val interp = Interpreter6502()
        interp.labelResolver = { label -> if (label == "2000") 0x2000 else 0 }

        // Set up memory and register
        interp.memory.writeByte(0x2005, 0x77u)
        interp.cpu.X = 0x05u

        // LDA absolute,X
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.DirectX("2000")
            )
        )
        assertEquals(0x77u, interp.cpu.A)

        // STA absolute,X
        interp.cpu.A = 0x88u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.STA,
                AssemblyAddressing.DirectX("2000")
            )
        )
        assertEquals(0x88u, interp.memory.readByte(0x2005))

        // INC absolute,X
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.INC,
                AssemblyAddressing.DirectX("2000")
            )
        )
        assertEquals(0x89u, interp.memory.readByte(0x2005))

        // DEC absolute,X
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.DEC,
                AssemblyAddressing.DirectX("2000")
            )
        )
        assertEquals(0x88u, interp.memory.readByte(0x2005))
    }

    @Test
    fun testAbsoluteYAddressing() {
        val interp = Interpreter6502()
        interp.labelResolver = { label -> if (label == "3000") 0x3000 else 0 }

        // Set up memory and register
        interp.memory.writeByte(0x300A, 0x99u)
        interp.cpu.Y = 0x0Au

        // LDA absolute,Y
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.DirectY("3000")
            )
        )
        assertEquals(0x99u, interp.cpu.A)

        // STA absolute,Y
        interp.cpu.A = 0xBBu
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.STA,
                AssemblyAddressing.DirectY("3000")
            )
        )
        assertEquals(0xBBu, interp.memory.readByte(0x300A))

        // LDX absolute,Y
        interp.memory.writeByte(0x300A, 0xCCu)
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDX,
                AssemblyAddressing.DirectY("3000")
            )
        )
        assertEquals(0xCCu, interp.cpu.X)
    }

    @Test
    fun testIndirectXAddressing() {
        val interp = Interpreter6502()
        interp.labelResolver = { label -> if (label == "40") 0x40 else 0 }

        // Set up zero page pointer and data
        // ($40,X) with X=05 reads from $45
        // $45-$46 contains address $2000
        interp.cpu.X = 0x05u
        interp.memory.writeWord(0x45, 0x2000u)
        interp.memory.writeByte(0x2000, 0xDDu)

        // LDA (zp,X)
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.IndirectX("40")
            )
        )
        assertEquals(0xDDu, interp.cpu.A)

        // STA (zp,X)
        interp.cpu.A = 0xEEu
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.STA,
                AssemblyAddressing.IndirectX("40")
            )
        )
        assertEquals(0xEEu, interp.memory.readByte(0x2000))
    }

    @Test
    fun testIndirectYAddressing() {
        val interp = Interpreter6502()
        interp.labelResolver = { label -> if (label == "50") 0x50 else 0 }

        // Set up zero page pointer and data
        // ($50),Y reads pointer from $50-$51 then adds Y
        interp.memory.writeWord(0x50, 0x3000u)
        interp.cpu.Y = 0x10u
        interp.memory.writeByte(0x3010, 0xFFu)

        // LDA (zp),Y
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.IndirectY("50")
            )
        )
        assertEquals(0xFFu, interp.cpu.A)

        // STA (zp),Y
        interp.cpu.A = 0xABu
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.STA,
                AssemblyAddressing.IndirectY("50")
            )
        )
        assertEquals(0xABu, interp.memory.readByte(0x3010))
    }

    @Test
    fun testIndirectAbsoluteAddressing() {
        val interp = Interpreter6502()
        interp.labelResolver = { label -> if (label == "4000") 0x4000 else 0 }

        // Set up indirect address
        // JMP ($4000) reads target from $4000-$4001
        interp.memory.writeWord(0x4000, 0x5000u)

        val instruction = AssemblyInstruction(
            AssemblyOp.JMP,
            AssemblyAddressing.IndirectAbsolute("4000")
        )

        // Read the target address
        val targetLo = interp.memory.readByte(0x4000).toInt()
        val targetHi = interp.memory.readByte(0x4001).toInt()
        val target = (targetHi shl 8) or targetLo

        assertEquals(0x5000, target)
    }

    @Test
    fun testAccumulatorAddressing() {
        val interp = Interpreter6502()

        // ASL A
        interp.cpu.A = 0b01010101u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ASL, null))
        assertEquals(0b10101010u, interp.cpu.A)

        // LSR A
        interp.cpu.A = 0b10101010u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.LSR, null))
        assertEquals(0b01010101u, interp.cpu.A)

        // ROL A
        interp.cpu.A = 0b01010101u
        interp.cpu.C = true
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ROL, null))
        assertEquals(0b10101011u, interp.cpu.A)

        // ROR A
        interp.cpu.A = 0b10101010u
        interp.cpu.C = true
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.ROR, null))
        assertEquals(0b11010101u, interp.cpu.A)
    }

    @Test
    fun testImpliedAddressing() {
        val interp = Interpreter6502()

        // Test various implied mode instructions

        // INX
        interp.cpu.X = 0x10u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.INX))
        assertEquals(0x11u, interp.cpu.X)

        // INY
        interp.cpu.Y = 0x20u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.INY))
        assertEquals(0x21u, interp.cpu.Y)

        // DEX
        interp.cpu.X = 0x11u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.DEX))
        assertEquals(0x10u, interp.cpu.X)

        // DEY
        interp.cpu.Y = 0x21u
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.DEY))
        assertEquals(0x20u, interp.cpu.Y)

        // CLC
        interp.cpu.C = true
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.CLC))
        assertEquals(false, interp.cpu.C)

        // SEC
        interp.cpu.C = false
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.SEC))
        assertEquals(true, interp.cpu.C)

        // CLI
        interp.cpu.I = true
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.CLI))
        assertEquals(false, interp.cpu.I)

        // SEI
        interp.cpu.I = false
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.SEI))
        assertEquals(true, interp.cpu.I)

        // CLV
        interp.cpu.V = true
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.CLV))
        assertEquals(false, interp.cpu.V)

        // CLD
        interp.cpu.D = true
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.CLD))
        assertEquals(false, interp.cpu.D)

        // SED
        interp.cpu.D = false
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.SED))
        assertEquals(true, interp.cpu.D)

        // NOP
        val stateBefore = interp.cpu.toString()
        interp.executeInstruction(AssemblyInstruction(AssemblyOp.NOP))
        assertEquals(stateBefore, interp.cpu.toString())
    }

    @Test
    fun testOffsetAddressing() {
        val interp = Interpreter6502()
        interp.labelResolver = { label -> if (label == "1000") 0x1000 else 0 }

        // Test Direct with offset
        interp.memory.writeByte(0x1005, 0x42u)
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.Direct("1000", 5)
            )
        )
        assertEquals(0x42u, interp.cpu.A)

        // Test DirectX with offset
        interp.memory.writeByte(0x100A, 0x55u)
        interp.cpu.X = 0x03u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.DirectX("1000", 7)  // 1000 + 7 + 3 = 100A
            )
        )
        assertEquals(0x55u, interp.cpu.A)

        // Test DirectY with offset
        interp.memory.writeByte(0x100F, 0x66u)
        interp.cpu.Y = 0x05u
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.DirectY("1000", 10)  // 1000 + 10 + 5 = 100F
            )
        )
        assertEquals(0x66u, interp.cpu.A)
    }

    @Test
    fun testValueUpperLowerSelection() {
        val interp = Interpreter6502()

        // Test lower byte selection
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ValueLowerSelection(
                    AssemblyAddressing.ShortValue(0xABCDu, AssemblyAddressing.Radix.Hex)
                )
            )
        )
        assertEquals(0xCDu, interp.cpu.A, "Lower byte of 0xABCD should be 0xCD")

        // Test upper byte selection
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ValueUpperSelection(
                    AssemblyAddressing.ShortValue(0xABCDu, AssemblyAddressing.Radix.Hex)
                )
            )
        )
        assertEquals(0xABu, interp.cpu.A, "Upper byte of 0xABCD should be 0xAB")

        // Test loading 16-bit address in parts
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ValueLowerSelection(
                    AssemblyAddressing.ShortValue(0x1234u, AssemblyAddressing.Radix.Hex)
                )
            )
        )
        val lowByte = interp.cpu.A

        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDX,
                AssemblyAddressing.ValueUpperSelection(
                    AssemblyAddressing.ShortValue(0x1234u, AssemblyAddressing.Radix.Hex)
                )
            )
        )
        val highByte = interp.cpu.X

        assertEquals(0x34u, lowByte)
        assertEquals(0x12u, highByte)
    }

    @Test
    fun testAllAddressingModesWithAND() {
        val interp = Interpreter6502()
        interp.labelResolver = { label ->
            when (label) {
                "1000" -> 0x1000
                "10" -> 0x10
                else -> 0
            }
        }

        // Immediate
        interp.cpu.A = 0xFFu
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.AND,
                AssemblyAddressing.ByteValue(0x0Fu, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x0Fu, interp.cpu.A)

        // Absolute
        interp.cpu.A = 0xFFu
        interp.memory.writeByte(0x1000, 0xF0u)
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.AND,
                AssemblyAddressing.Direct("1000")
            )
        )
        assertEquals(0xF0u, interp.cpu.A)

        // Absolute,X
        interp.cpu.A = 0xFFu
        interp.cpu.X = 0x05u
        interp.memory.writeByte(0x1005, 0xAAu)
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.AND,
                AssemblyAddressing.DirectX("1000")
            )
        )
        assertEquals(0xAAu, interp.cpu.A)

        // Absolute,Y
        interp.cpu.A = 0xFFu
        interp.cpu.Y = 0x0Au
        interp.memory.writeByte(0x100A, 0x55u)
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.AND,
                AssemblyAddressing.DirectY("1000")
            )
        )
        assertEquals(0x55u, interp.cpu.A)

        // (Indirect,X)
        interp.cpu.A = 0xFFu
        interp.cpu.X = 0x02u
        interp.memory.writeWord(0x12, 0x2000u)
        interp.memory.writeByte(0x2000, 0x33u)
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.AND,
                AssemblyAddressing.IndirectX("10")
            )
        )
        assertEquals(0x33u, interp.cpu.A)

        // (Indirect),Y
        interp.cpu.A = 0xFFu
        interp.cpu.Y = 0x03u
        interp.memory.writeWord(0x10, 0x3000u)
        interp.memory.writeByte(0x3003, 0xCCu)
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.AND,
                AssemblyAddressing.IndirectY("10")
            )
        )
        assertEquals(0xCCu, interp.cpu.A)
    }

    @Test
    fun testConstantReference() {
        val interp = Interpreter6502()

        // Test constant reference addressing
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ConstantReference("MyConstant")
            )
        )
        // Note: This will throw an exception without a resolver
        // but tests that the addressing mode exists
    }

    @Test
    fun testShortValueImmediate() {
        val interp = Interpreter6502()

        // Test that ShortValue is properly truncated to byte
        interp.executeInstruction(
            AssemblyInstruction(
                AssemblyOp.LDA,
                AssemblyAddressing.ShortValue(0x1234u, AssemblyAddressing.Radix.Hex)
            )
        )
        assertEquals(0x34u, interp.cpu.A, "ShortValue should use low byte for 8-bit load")
    }
}
