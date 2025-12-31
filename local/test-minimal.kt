@file:OptIn(ExperimentalUnsignedTypes::class)

// Runtime support
val memory = UByteArray(0x10000)
var A: Int = 0
var X: Int = 0
var Y: Int = 0
var flagC: Boolean = false

fun readWord(addr: Int): Int {
    val low = memory[addr and 0xFFFF].toInt()
    val high = memory[(addr + 1) and 0xFFFF].toInt()
    return low or (high shl 8)
}

// Test a simple generated function pattern
fun testFunc() {
    var localX: Int = 0
    // LDA immediate
    A = 0x42
    // STA zero page
    memory[0x06] = (A and 0xFF).toUByte()
    // LDA zero page
    A = memory[0x06].toInt()
    // Check zero flag pattern
    val zeroCheck = A == 0
    if (!zeroCheck) {
        println("Not zero!")
    }
    // Indirect Y addressing
    memory[readWord(0x06) + Y] = A.toUByte()
}

fun main() {
    testFunc()
    println("Success!")
}
