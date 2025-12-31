package com.ivieleague.decompiler6502tokotlin.interpreter

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CPU6502Test {

    @Test
    fun testCPUInitialization() {
        val cpu = CPU6502()
        assertEquals(0u, cpu.A)
        assertEquals(0u, cpu.X)
        assertEquals(0u, cpu.Y)
        assertEquals(0xFFu, cpu.SP)
        assertEquals(0u, cpu.PC)
        assertFalse(cpu.N)
        assertFalse(cpu.V)
        assertFalse(cpu.Z)
        assertFalse(cpu.C)
    }

    @Test
    fun testStatusByteConversion() {
        val cpu = CPU6502()

        // Set various flags
        cpu.N = true
        cpu.V = true
        cpu.Z = true
        cpu.C = true
        cpu.I = true
        cpu.D = true
        cpu.B = true

        val status = cpu.getStatusByte()
        // Bit pattern: NV-BDIZC = 11111111
        assertEquals(0xFFu, status)

        // Clear all flags and set from byte
        cpu.reset()
        cpu.setStatusByte(0b11000011u)

        assertTrue(cpu.N)
        assertTrue(cpu.V)
        assertFalse(cpu.B)
        assertFalse(cpu.D)
        assertFalse(cpu.I)
        assertTrue(cpu.Z)
        assertTrue(cpu.C)
    }

    @Test
    fun testUpdateZN() {
        val cpu = CPU6502()

        // Test zero flag
        cpu.updateZN(0u)
        assertTrue(cpu.Z)
        assertFalse(cpu.N)

        // Test negative flag (bit 7 set)
        cpu.updateZN(0x80u)
        assertFalse(cpu.Z)
        assertTrue(cpu.N)

        // Test positive non-zero value
        cpu.updateZN(0x42u)
        assertFalse(cpu.Z)
        assertFalse(cpu.N)

        // Test another negative value
        cpu.updateZN(0xFFu)
        assertFalse(cpu.Z)
        assertTrue(cpu.N)
    }

    @Test
    fun testReset() {
        val cpu = CPU6502()

        // Modify state
        cpu.A = 0x42u
        cpu.X = 0x11u
        cpu.Y = 0x22u
        cpu.SP = 0x50u
        cpu.PC = 0x1234u
        cpu.N = true
        cpu.Z = true
        cpu.C = true

        // Reset
        cpu.reset()

        assertEquals(0u, cpu.A)
        assertEquals(0u, cpu.X)
        assertEquals(0u, cpu.Y)
        assertEquals(0xFFu, cpu.SP)
        assertEquals(0u, cpu.PC)
        assertFalse(cpu.N)
        assertFalse(cpu.Z)
        assertFalse(cpu.C)
        assertTrue(cpu.I) // I flag should be set after reset
    }

    @Test
    fun testToString() {
        val cpu = CPU6502()
        cpu.A = 0x42u
        cpu.X = 0x11u
        cpu.Y = 0x22u
        cpu.SP = 0xFDu
        cpu.PC = 0x8000u
        cpu.N = true
        cpu.Z = true

        val str = cpu.toString()
        assertTrue(str.contains("PC=\$8000"))
        assertTrue(str.contains("A=\$42"))
        assertTrue(str.contains("X=\$11"))
        assertTrue(str.contains("Y=\$22"))
        assertTrue(str.contains("SP=\$FD"))
    }

    @Test
    fun testStatusBit5AlwaysSet() {
        val cpu = CPU6502()
        // Bit 5 should always be 1 in the status byte
        val status = cpu.getStatusByte()
        assertEquals(0b00100000, status.toInt() and 0b00100000)
    }
}
