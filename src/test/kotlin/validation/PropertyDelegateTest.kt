@file:OptIn(ExperimentalUnsignedTypes::class)

package validation

import com.ivieleague.decompiler6502tokotlin.hand.MemoryByte
import com.ivieleague.decompiler6502tokotlin.hand.memory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import kotlin.test.assertEquals

/**
 * Tests for property delegate pattern used in generated code.
 * Verifies that memory-backed properties work correctly.
 */
class PropertyDelegateTest {

    // Test property delegates like the generated constants
    var operMode by MemoryByte(0x0770)
    val OPERMODE = 0x0770
    var altEntranceControl by MemoryByte(0x0752)
    val ALTENTRANCECONTROL = 0x0752
    var musicSelectData by MemoryByte(0x07FC)
    val MUSICSELECTDATA = 0x07FC
    var areaMusicQueue by MemoryByte(0xFB)
    val AREAMUSICQUEUE = 0xFB

    @BeforeEach
    fun setup() {
        // Clear memory before each test
        for (i in 0 until 0x10000) {
            memory[i] = 0u
        }
    }

    @Test
    fun `property delegates read from memory`() {
        memory[0x0770] = 0x42u
        assertEquals(0x42u, operMode)
    }

    @Test
    fun `property delegates write to memory`() {
        operMode = 0x99u
        assertEquals(0x99u, memory[0x0770])
    }

    @Test
    fun `property comparison works like generated code`() {
        operMode = 1u
        val condition = !(operMode.toInt() == 0)
        assertEquals(true, condition)
    }

    @Test
    fun `indexed access with uppercase constant works`() {
        memory[MUSICSELECTDATA + 0x04] = 0x55u
        val value = memory[MUSICSELECTDATA + 0x04]
        assertEquals(0x55u, value)
    }

    @Test
    fun `property to property assignment works`() {
        operMode = 0x33u
        areaMusicQueue = operMode
        assertEquals(0x33u, areaMusicQueue)
        assertEquals(0x33u, memory[AREAMUSICQUEUE])
    }

    @Test
    fun `simulates getAreaMusic pattern`() {
        // Setup similar to the generated function
        operMode = 1u  // Not in title screen
        altEntranceControl = 3u  // Not 0x02
        memory[MUSICSELECTDATA + 0x04] = 0x42u

        // Pattern from generated code (with .toInt() for type safety)
        if (!(operMode.toInt() == 0)) {
            if (!(altEntranceControl.toInt() - 0x02 == 0)) {
                areaMusicQueue = memory[MUSICSELECTDATA + 0x04]
            }
        }

        assertEquals(0x42u, areaMusicQueue)
    }
}
