fun main() {
    val memory = ByteArray(256)
    memory[128] = 16
    val result = (memory[128].toUByte()) shl 2
    println(result)
}
