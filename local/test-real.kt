@file:OptIn(ExperimentalUnsignedTypes::class)

// Runtime support (from runtime-support.kt)
val memory = UByteArray(0x10000)
var A: Int = 0
var X: Int = 0
var Y: Int = 0

fun readWord(addr: Int): Int {
    val low = memory[addr and 0xFFFF].toInt()
    val high = memory[(addr + 1) and 0xFFFF].toInt()
    return low or (high shl 8)
}

// Named constants for memory addresses (subset)
const val OperMode = 0x0770
const val AltEntranceControl = 0x0752
const val PlayerEntranceCtrl = 0x0710

// Example decompiled function pattern
fun testGetAreaMusic() {
    // lda OperMode ; if in title screen mode, leave
    A = memory[OperMode].toInt()
    // beq ExitGetM
    if (!(A == 0)) {
        // lda AltEntranceControl
        A = memory[AltEntranceControl].toInt()
        // cmp #$02 ; beq ChkAreaType
        if (!(A - 0x02 == 0)) {
            println("Not alternate entrance!")
        }
    }
    println("Music check done")
}

fun main() {
    // Set up some test state
    memory[OperMode] = 1u  // Not title screen
    memory[AltEntranceControl] = 3u  // Not $02
    testGetAreaMusic()
    println("Test passed!")
}
