package com.ivieleague.decompiler6502tokotlin.interpreter

/**
 * Represents 64KB of memory for the 6502 processor.
 * Address space: $0000 - $FFFF
 */
class Memory6502 {
    private val data = ByteArray(0x10000) // 64KB

    /**
     * Read a byte from memory at the specified address
     */
    fun readByte(address: Int): UByte {
        return data[address and 0xFFFF].toUByte()
    }

    /**
     * Write a byte to memory at the specified address
     */
    fun writeByte(address: Int, value: UByte) {
        data[address and 0xFFFF] = value.toByte()
    }

    /**
     * Read a 16-bit word (little-endian) from memory
     */
    fun readWord(address: Int): UShort {
        val lo = readByte(address).toInt()
        val hi = readByte(address + 1).toInt()
        return ((hi shl 8) or lo).toUShort()
    }

    /**
     * Write a 16-bit word (little-endian) to memory
     */
    fun writeWord(address: Int, value: UShort) {
        writeByte(address, (value.toInt() and 0xFF).toUByte())
        writeByte(address + 1, ((value.toInt() shr 8) and 0xFF).toUByte())
    }

    /**
     * Load a program into memory at the specified address
     */
    fun loadProgram(startAddress: Int, program: ByteArray) {
        program.forEachIndexed { index, byte ->
            data[(startAddress + index) and 0xFFFF] = byte
        }
    }

    /**
     * Load a program into memory at the specified address
     */
    fun loadProgram(startAddress: Int, program: List<UByte>) {
        program.forEachIndexed { index, byte ->
            data[(startAddress + index) and 0xFFFF] = byte.toByte()
        }
    }

    /**
     * Reset all memory to zero
     */
    fun reset() {
        data.fill(0)
    }

    /**
     * Get a copy of memory for debugging/inspection
     */
    fun dump(startAddress: Int, length: Int): ByteArray {
        return data.copyOfRange(startAddress and 0xFFFF, (startAddress + length) and 0xFFFF)
    }
}
