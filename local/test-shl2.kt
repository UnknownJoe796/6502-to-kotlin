fun main() {
    val memory = ByteArray(256)
    memory[128] = 16
    val result1 = memory[128].toUByte() shl 2
    val result2 = (memory[128].toUByte() shl 2)
    val result3 = ((memory[128].toUByte()) shl 2)
    println("result1: $result1, result2: $result2, result3: $result3")
}
