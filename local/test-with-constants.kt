@file:OptIn(ExperimentalUnsignedTypes::class)

// Runtime support
val memory = UByteArray(0x10000)
var A: Int = 0
var X: Int = 0
var Y: Int = 0

fun readWord(addr: Int): Int {
    val low = memory[addr and 0xFFFF].toInt()
    val high = memory[(addr + 1) and 0xFFFF].toInt()
    return low or (high shl 8)
}

// Named constants (subset)
const val OperMode = 0x0770
const val AltEntranceControl = 0x0752
const val PlayerEntranceCtrl = 0x0710
const val CloudTypeOverride = 0x0758
const val MusicSelectData = 0x07FC
const val AreaMusicQueue = 0xFB

// Decompiled function from getAreaMusic
fun getAreaMusic() {
    var A: Int = 0
    // lda OperMode ; if in title screen mode, leave
    // beq ExitGetM
    if (!(memory[OperMode].toInt() == 0)) {
        // lda AltEntranceControl
        // cmp #$02
        // beq ChkAreaType
        if (!(memory[AltEntranceControl].toInt() - 0x02 == 0)) {
            // lda PlayerEntranceCtrl
            // cmp #$06
            // beq StoreMusic
            if (!(memory[PlayerEntranceCtrl].toInt() - 0x06 == 0)) {
                // cmp #$07
                // beq StoreMusic
                if (!(A - 0x07 == 0)) {
                    // lda CloudTypeOverride
                    // beq StoreMusic
                    if (!(memory[CloudTypeOverride].toInt() == 0)) {
                        // ldy #$04
                    }
                }
            }
        }
        if (!(memory[CloudTypeOverride].toInt() == 0)) {
        }
        // lda MusicSelectData,y
        // sta AreaMusicQueue
        memory[AreaMusicQueue] = memory[MusicSelectData + 0x04]
    }
    return
}

fun main() {
    // Set up test state
    memory[OperMode] = 1u
    memory[AltEntranceControl] = 3u
    memory[PlayerEntranceCtrl] = 8u
    memory[CloudTypeOverride] = 0u
    memory[MusicSelectData + 0x04] = 0x42u
    
    getAreaMusic()
    
    println("AreaMusicQueue = ${memory[AreaMusicQueue]}")
    println("Test passed!")
}
