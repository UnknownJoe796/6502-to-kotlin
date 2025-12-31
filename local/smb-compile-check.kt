// Minimal SMB snippet to test what would be needed for compilation
package com.ivieleague.smb

import com.ivieleague.decompiler6502tokotlin.hand.*

// From smb-constants.kt
const val PPU_CTRL_REG1 = 0x2000
const val PPU_STATUS = 0x2002

// Test if a minimal SMB-style function compiles
fun testFunc() {
    memory[PPU_CTRL_REG1] = 0x10u
    while ((memory[PPU_STATUS].toInt() and 0x80) != 0) {
        // wait for VBlank
    }
}

fun main() {
    resetCPU()
    testFunc()
    println("Success!")
}
