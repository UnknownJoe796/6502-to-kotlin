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
        memory[0] = (1u and 0xFF).toByte()
        memory[1] = (2u and 0xFF).toByte()
    }
}

fun main() {
    val cpu = Generated6502()
    cpu.execute()
    println("Memory[0] = ${cpu.memory[0].toUByte()}")
    println("Memory[1] = ${cpu.memory[1].toUByte()}")
}
