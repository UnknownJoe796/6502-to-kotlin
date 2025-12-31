@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.smb

import com.ivieleague.decompiler6502tokotlin.hand.*
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Safe tests for individual decompiled functions.
 * Each test is isolated to identify infinite loop issues.
 */
class SafeFunctionTest {

    private fun initState() {
        resetCPU()
        clearMemory()
    }

    @Test
    fun `test doNothing1`() {
        initState()
        doNothing1()
        assertTrue(memory[0x6C9].toInt() == 0xFF, "Should write 0xFF to $6C9")
    }

    @Test
    fun `test doNothing2`() {
        initState()
        doNothing2()
        assertTrue(true, "Should return without error")
    }

    @Test
    fun `test getAreaMusic basic`() {
        initState()
        memory[OperMode] = 0u  // Title screen mode - function should exit early
        getAreaMusic()
        assertTrue(true, "Should return without error")
    }

    @Test
    fun `test colorRotation`() {
        initState()
        memory[ColorRotateOffset] = 0u
        memory[FrameCounter] = 0u
        colorRotation()
        assertTrue(true, "Should complete")
    }

    @Test
    fun `test readJoypads`() {
        initState()
        // Simulate joypad port behavior
        memory[JOYPAD_PORT] = 0u
        readJoypads()
        assertTrue(true, "Should complete")
    }
}
