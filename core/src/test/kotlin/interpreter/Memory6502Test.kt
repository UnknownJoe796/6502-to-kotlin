package com.ivieleague.decompiler6502tokotlin.interpreter

import kotlin.test.Test
import kotlin.test.assertEquals

class Memory6502Test {

    @Test
    fun testMemoryReadWrite() {
        val memory = Memory6502()

        // Test byte read/write
        memory.writeByte(0x1000, 0x42u)
        assertEquals(0x42u, memory.readByte(0x1000))

        // Test different addresses
        memory.writeByte(0x0000, 0xFFu)
        memory.writeByte(0xFFFF, 0xAAu)
        assertEquals(0xFFu, memory.readByte(0x0000))
        assertEquals(0xAAu, memory.readByte(0xFFFF))
    }

    @Test
    fun testMemoryWordOperations() {
        val memory = Memory6502()

        // Test word write (little-endian)
        memory.writeWord(0x2000, 0x1234u)
        assertEquals(0x34u, memory.readByte(0x2000)) // Low byte
        assertEquals(0x12u, memory.readByte(0x2001)) // High byte
        assertEquals(0x1234u, memory.readWord(0x2000))
    }

    @Test
    fun testLoadProgram() {
        val memory = Memory6502()
        val program = listOf<UByte>(0xA9u, 0x42u, 0x85u, 0x10u) // LDA #$42; STA $10

        memory.loadProgram(0x8000, program)
        assertEquals(0xA9u, memory.readByte(0x8000))
        assertEquals(0x42u, memory.readByte(0x8001))
        assertEquals(0x85u, memory.readByte(0x8002))
        assertEquals(0x10u, memory.readByte(0x8003))
    }

    @Test
    fun testMemoryReset() {
        val memory = Memory6502()
        memory.writeByte(0x1000, 0x42u)
        memory.writeByte(0x2000, 0xFFu)

        memory.reset()

        assertEquals(0x00u, memory.readByte(0x1000))
        assertEquals(0x00u, memory.readByte(0x2000))
    }

    @Test
    fun testMemoryDump() {
        val memory = Memory6502()
        memory.writeByte(0x1000, 0x11u)
        memory.writeByte(0x1001, 0x22u)
        memory.writeByte(0x1002, 0x33u)

        val dump = memory.dump(0x1000, 3)
        assertEquals(3, dump.size)
        assertEquals(0x11.toByte(), dump[0])
        assertEquals(0x22.toByte(), dump[1])
        assertEquals(0x33.toByte(), dump[2])
    }

    @Test
    fun testAddressWrapping() {
        val memory = Memory6502()

        // Test that addresses wrap around in 64K space
        memory.writeByte(0x10000, 0x42u) // Should wrap to 0x0000
        assertEquals(0x42u, memory.readByte(0x0000))

        memory.writeByte(0x1FFFF, 0xAAu) // Should wrap to 0xFFFF
        assertEquals(0xAAu, memory.readByte(0xFFFF))
    }
}
