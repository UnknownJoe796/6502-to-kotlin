@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.smb.demo

import com.ivieleague.decompiler6502tokotlin.hand.*

/**
 * Demo: What decompiled code looks like WITH delegates
 *
 * Compare this to the current output which uses memory[X] everywhere.
 */

// Current approach (verbose, repetitive):
fun resetScreenTimer_OLD() {
    memory[SCREENTIMER] = 0x07u
    memory[SCREENROUTINETASK] = ((memory[SCREENROUTINETASK].toInt() + 1) and 0xFF).toUByte()
}

// With delegates (clean, readable):
fun resetScreenTimer_NEW() {
    var screenTimer by MemoryByte(SCREENTIMER)
    var screenRoutineTask by MemoryByte(SCREENROUTINETASK)

    screenTimer = 0x07u
    screenRoutineTask = ((screenRoutineTask.toInt() + 1) and 0xFF).toUByte()
}

// Current approach with indexed access (very verbose):
fun collisionCoreLoop_OLD() {
    var x = memory[OBJECTOFFSET]
    var y = 0x1cu

    do {
        val enemyFlag = memory[ENEMY_FLAG.toInt() + x.toInt()]
        if (enemyFlag.toInt() and 0x80 == 0) {
            val xDiff = memory[ENEMYOFFSCRBITSET.toInt() + x.toInt()]
            memory[ENEMY_FLAG.toInt() + x.toInt()] = 0xFFu
        }
        x = ((x.toInt() + 1) and 0xFF).toUByte()
        y = ((y.toInt() - 1) and 0xFF).toUByte()
    } while (y.toInt() != 0)
}

// With indexed delegates (much cleaner!):
fun collisionCoreLoop_NEW() {
    var objectOffset by MemoryByte(OBJECTOFFSET)
    val enemyFlag by MemoryByteIndexed(ENEMY_FLAG)
    val enemyOffscr by MemoryByteIndexed(ENEMYOFFSCRBITSET)

    var x = objectOffset
    var y = 0x1cu

    do {
        if (enemyFlag[x.toInt()].toInt() and 0x80 == 0) {
            val xDiff = enemyOffscr[x.toInt()]
            enemyFlag[x.toInt()] = 0xFFu
        }
        x = ((x.toInt() + 1) and 0xFF).toUByte()
        y = ((y.toInt() - 1) and 0xFF).toUByte()
    } while (y.toInt() != 0)
}

/**
 * Benefits of delegate approach:
 *
 * 1. **Readability**: Properties with names instead of memory[CONSTANT]
 * 2. **Less repetition**: CONSTANT appears once in declaration, not everywhere
 * 3. **Array syntax**: enemyFlag[x] instead of memory[ENEMY_FLAG.toInt() + x.toInt()]
 * 4. **Type safety**: Delegates ensure UByte types automatically
 * 5. **Maintainability**: Easy to see which memory locations a function uses
 */

// Mock constants for demo
const val SCREENTIMER = 0x07A0
const val SCREENROUTINETASK = 0x073C
const val OBJECTOFFSET = 0x0008
const val ENEMY_FLAG = 0x000F
const val ENEMYOFFSCRBITSET = 0x0017
