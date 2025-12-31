package com.ivieleague.decompiler6502tokotlin.smb

import com.ivieleague.decompiler6502tokotlin.hand.*
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests the controller reading mechanism in isolation
 */
class ControllerReadingTest {

    @Test
    fun `test memory intercept is being used`() {
        var interceptCallCount = 0
        var lastAddress = -1

        // Set up a test intercept
        memoryReadIntercept = { addr ->
            interceptCallCount++
            lastAddress = addr
            if (addr == 0x4016) {
                0x01u  // Return a test value
            } else {
                null
            }
        }

        // Read via MemoryByte
        var testVar by MemoryByte(0x4016)
        val value = testVar

        println("Intercept was called $interceptCallCount times")
        println("Last address: $lastAddress")
        println("Value read: $value")

        assertEquals(1, interceptCallCount, "Intercept should have been called once")
        assertEquals(0x4016, lastAddress, "Should have read from 0x4016")
        assertEquals(1, value, "Should have read 0x01 from intercept")

        // Clean up
        memoryReadIntercept = null
    }

    @Test
    fun `test MemoryByteIndexed uses intercept`() {
        var interceptCallCount = 0

        memoryReadIntercept = { addr ->
            interceptCallCount++
            when (addr) {
                0x4016 -> 0x55u
                0x4017 -> 0xAAu
                else -> null
            }
        }

        val joypadPort by MemoryByteIndexed(0x4016)
        val value0 = joypadPort[0]
        val value1 = joypadPort[1]

        println("Intercept called $interceptCallCount times")
        println("joypadPort[0] = $value0")
        println("joypadPort[1] = $value1")

        assertEquals(2, interceptCallCount, "Intercept should have been called twice")
        assertEquals(0x55, value0, "Should read 0x55 from port 0")
        assertEquals(0xAA, value1, "Should read 0xAA from port 1")

        memoryReadIntercept = null
    }

    @Test
    fun `test controller reading produces correct button state`() {
        // Set up controller
        val controller = ControllerInput()
        controller.setButtons(0, ControllerInput.START)  // 0x10

        // Set up intercept to use this controller
        memoryReadIntercept = { addr ->
            when (addr) {
                0x4016, 0x4017 -> controller.readController(addr)
                else -> null
            }
        }
        memoryWriteIntercept = { addr, value ->
            when (addr) {
                0x4016 -> {
                    controller.writeStrobe(value)
                    true
                }
                else -> false
            }
        }

        // Strobe the controller (like readJoypads does)
        var joypadPortWrite by MemoryByte(0x4016)
        joypadPortWrite = 1  // Strobe on
        joypadPortWrite = 0  // Strobe off - latches button state

        // Now read 8 bits (like readPortBits does)
        var accumulated = 0
        val joypadPort by MemoryByteIndexed(0x4016)

        repeat(8) {
            val portValue = joypadPort[0]
            val bit = ((portValue shr 1) or portValue) and 0x01
            accumulated = ((accumulated shl 1) and 0xFE) or bit
            println("Read $it: portValue=$portValue, bit=$bit, accumulated=${accumulated.toString(16)}")
        }

        println("Final accumulated: ${accumulated.toString(16)}")
        assertEquals(0x10, accumulated, "Should read 0x10 (Start button)")

        memoryReadIntercept = null
        memoryWriteIntercept = null
    }
}
