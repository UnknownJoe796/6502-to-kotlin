// Generated from 6502 assembly
class Generated6502 {
    // Memory
    val memory = ByteArray(65536)

    // Stub registers/flags (not used in expression-based code)
    var A: UByte = 0u
    var X: UByte = 0u
    var Y: UByte = 0u
    var N: Boolean = false
    var V: Boolean = false
    var Z: Boolean = false
    var C: Boolean = false
    var I: Boolean = false
    var D: Boolean = false

    fun execute() {
        memory[2] = (((memory[128].toUByte()) shl 2) and 0xFFu).toByte()
    }
}
