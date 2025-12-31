fun main() {
    val cpu = Generated6502()

    // Initial memory setup
    cpu.memory[128] = 16  // 0x10

    // Initial register setup

    // Execute generated code
    cpu.execute()

    // Print final state
    println("STATE_BEGIN")
    println("A=${cpu.A}")
    println("X=${cpu.X}")
    println("Y=${cpu.Y}")
    println("N=${cpu.N}")
    println("V=${cpu.V}")
    println("Z=${cpu.Z}")
    println("C=${cpu.C}")
    println("I=${cpu.I}")
    println("D=${cpu.D}")
    println("MEM[2]=${cpu.memory[2].toUByte()}")
    println("STATE_END")
}
