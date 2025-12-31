@file:OptIn(ExperimentalUnsignedTypes::class)

package validation

import com.ivieleague.decompiler6502tokotlin.hand.MemoryByte
import com.ivieleague.decompiler6502tokotlin.hand.memory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import kotlin.test.assertEquals

/**
 * Tests that verify the complete pattern of property delegates with type conversions
 * matches what the decompiler generates.
 *
 * MemoryByte delegates use Int internally for easier arithmetic,
 * while the underlying memory array uses UByte.
 */
class FullPatternCompilationTest {

    // Property delegates with constants (like generated constants file)
    var operMode by MemoryByte(0x0770)
    val OPERMODE = 0x0770
    var worldNumber by MemoryByte(0x075F)
    val WORLDNUMBER = 0x075F
    var continueWorld by MemoryByte(0x076E)
    val CONTINUEWORLD = 0x076E
    var playerSize by MemoryByte(0x0754)
    val PLAYERSIZE = 0x0754

    // CPU registers
    var A: Int = 0
    var X: Int = 0
    var Y: Int = 0

    @BeforeEach
    fun setup() {
        for (i in 0 until 0x10000) {
            memory[i] = 0u
        }
        A = 0
        X = 0
        Y = 0
    }

    @Test
    fun `property read works directly`() {
        operMode = 5
        // Pattern: LDA OperMode - no toInt() needed, MemoryByte returns Int
        A = operMode
        assertEquals(5, A)
    }

    @Test
    fun `property comparison works directly`() {
        operMode = 1
        // Pattern: BEQ (branch if equal to zero)
        val isZero = operMode == 0
        assertEquals(false, isZero)
    }

    @Test
    fun `property write with literal works`() {
        // Pattern: LDA #$01 / STA PlayerSize
        A = 0x01
        playerSize = A  // MemoryByte accepts Int
        assertEquals(0x01, playerSize)
    }

    @Test
    fun `property write with expression works`() {
        // Pattern: LDA #$00 / ASL / STA OperMode
        A = 0x00
        operMode = (A shl 1) and 0xFF  // MemoryByte accepts Int
        assertEquals(0x00, operMode)
    }

    @Test
    fun `property to property works directly`() {
        worldNumber = 0x42
        // Pattern: LDA WorldNumber / STA ContinueWorld
        continueWorld = worldNumber
        assertEquals(0x42, continueWorld)
    }

    @Test
    fun `indexed access with uppercase constant works`() {
        memory[OPERMODE + 0x10] = 0x99u
        val value = memory[OPERMODE + 0x10].toInt()  // memory[] returns UByte
        assertEquals(0x99, value)
    }

    @Test
    fun `full terminateGame pattern compiles`() {
        // Simulate the terminateGame function pattern
        worldNumber = 5

        // if (carryFlag)
        if (true) {
            // continueworld = worldnumber
            continueWorld = worldNumber

            // opermode = 0x01
            operMode = 0x01
        }

        assertEquals(5, continueWorld)
        assertEquals(1, operMode)
    }
}
