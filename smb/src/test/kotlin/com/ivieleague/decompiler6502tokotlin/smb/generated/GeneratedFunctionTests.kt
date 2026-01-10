@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.smb.generated

import com.ivieleague.decompiler6502tokotlin.hand.*
import com.ivieleague.decompiler6502tokotlin.smb.*
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.jupiter.api.Assertions.assertTimeoutPreemptively
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import java.time.Duration
import java.io.File

/**
 * Auto-generated tests from TAS capture: pc-based-smb-tas
 * Captured at: 2026-01-10T22:04:17.731322Z
 * Total frames: 17868
 * Total captures: 404553
 * Functions with tests: 123
 *
 * These tests verify that decompiled functions produce the same
 * outputs as the original 6502 binary interpreter.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
// Note: 1 functions skipped (unreliable capture data)
// Generated 663 tests for 122 functions
// Exact matches: 122, Fuzzy matches: 0
// Parameterless: 73, With parameters: 49

class GeneratedFunctionTests {

    @BeforeAll
    fun loadRomData() {
        // Load ROM into memory for functions that read from ROM tables
        val romFile = File("/Users/jivie/Projects/decompiler-6502-kotlin/local/roms/smb.nes")
        if (romFile.exists()) {
            val romData = romFile.readBytes().toUByteArray()
            // SMB ROM: 16-byte header + 32KB PRG ROM
            val prgStart = 16
            val prgSize = 0x8000
            for (i in 0 until prgSize) {
                memory[0x8000 + i] = romData[prgStart + i]
            }
        }
    }

    // =========================================
    // 0x8182: pauseRoutine
    // 9105 calls, 18 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 3
     * Function: pauseRoutine (0x8182)
     * Call depth: 1
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `pauseRoutine_frame3_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0770] = 0x00u

            // Execute decompiled function
            pauseRoutine()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 35
     * Function: pauseRoutine (0x8182)
     * Call depth: 1
     * Memory reads: 2, writes: 0
     */
    @Test
    fun `pauseRoutine_frame35_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0770] = 0x01u
            memory[0x0772] = 0x00u

            // Execute decompiled function
            pauseRoutine()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 2 from frame 194
     * Function: pauseRoutine (0x8182)
     * Call depth: 1
     * Memory reads: 2, writes: 0
     */
    @Test
    fun `pauseRoutine_frame194_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0770] = 0x01u
            memory[0x0772] = 0x02u

            // Execute decompiled function
            pauseRoutine()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 3 from frame 197
     * Function: pauseRoutine (0x8182)
     * Call depth: 1
     * Memory reads: 5, writes: 1
     */
    @Test
    fun `pauseRoutine_frame197_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x06FC] = 0x81u
            memory[0x0770] = 0x01u
            memory[0x0772] = 0x03u
            memory[0x0776] = 0x00u
            memory[0x0777] = 0x00u

            // Execute decompiled function
            pauseRoutine()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0776], "Memory 0x0776 mismatch")
        }
    }

    /**
     * Test case 4 from frame 219
     * Function: pauseRoutine (0x8182)
     * Call depth: 1
     * Memory reads: 5, writes: 1
     */
    @Test
    fun `pauseRoutine_frame219_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x06FC] = 0x02u
            memory[0x0770] = 0x01u
            memory[0x0772] = 0x03u
            memory[0x0776] = 0x00u
            memory[0x0777] = 0x00u

            // Execute decompiled function
            pauseRoutine()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0776], "Memory 0x0776 mismatch")
        }
    }

    /**
     * Test case 5 from frame 403
     * Function: pauseRoutine (0x8182)
     * Call depth: 1
     * Memory reads: 5, writes: 1
     */
    @Test
    fun `pauseRoutine_frame403_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x06FC] = 0x41u
            memory[0x0770] = 0x01u
            memory[0x0772] = 0x03u
            memory[0x0776] = 0x00u
            memory[0x0777] = 0x00u

            // Execute decompiled function
            pauseRoutine()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0776], "Memory 0x0776 mismatch")
        }
    }

    /**
     * Test case 6 from frame 661
     * Function: pauseRoutine (0x8182)
     * Call depth: 1
     * Memory reads: 5, writes: 1
     */
    @Test
    fun `pauseRoutine_frame661_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x06FC] = 0x03u
            memory[0x0770] = 0x01u
            memory[0x0772] = 0x03u
            memory[0x0776] = 0x00u
            memory[0x0777] = 0x00u

            // Execute decompiled function
            pauseRoutine()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0776], "Memory 0x0776 mismatch")
        }
    }

    /**
     * Test case 7 from frame 2629
     * Function: pauseRoutine (0x8182)
     * Call depth: 1
     * Memory reads: 5, writes: 1
     */
    @Test
    fun `pauseRoutine_frame2629_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x06FC] = 0x42u
            memory[0x0770] = 0x01u
            memory[0x0772] = 0x03u
            memory[0x0776] = 0x00u
            memory[0x0777] = 0x00u

            // Execute decompiled function
            pauseRoutine()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0776], "Memory 0x0776 mismatch")
        }
    }

    /**
     * Test case 8 from frame 3599
     * Function: pauseRoutine (0x8182)
     * Call depth: 1
     * Memory reads: 5, writes: 1
     */
    @Test
    fun `pauseRoutine_frame3599_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x06FC] = 0x43u
            memory[0x0770] = 0x01u
            memory[0x0772] = 0x03u
            memory[0x0776] = 0x00u
            memory[0x0777] = 0x00u

            // Execute decompiled function
            pauseRoutine()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0776], "Memory 0x0776 mismatch")
        }
    }

    /**
     * Test case 9 from frame 3771
     * Function: pauseRoutine (0x8182)
     * Call depth: 1
     * Memory reads: 5, writes: 1
     */
    @Test
    fun `pauseRoutine_frame3771_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x06FC] = 0x04u
            memory[0x0770] = 0x01u
            memory[0x0772] = 0x03u
            memory[0x0776] = 0x00u
            memory[0x0777] = 0x00u

            // Execute decompiled function
            pauseRoutine()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0776], "Memory 0x0776 mismatch")
        }
    }

    // =========================================
    // 0x81C6: spriteShuffler
    // 8756 calls, 3 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 32
     * Function: spriteShuffler (0x81C6)
     * Call depth: 1
     * Memory reads: 19, writes: 24
     */
    @Test
    fun `spriteShuffler_frame32_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (19 addresses)
            memory[0x0000] = 0x28u
            memory[0x06E0] = 0x00u
            memory[0x06E1] = 0x58u
            memory[0x06E4] = 0x04u
            memory[0x06E5] = 0x30u
            memory[0x06E6] = 0x48u
            memory[0x06E7] = 0x60u
            memory[0x06E8] = 0x78u
            memory[0x06E9] = 0x90u
            memory[0x06EA] = 0xA8u
            memory[0x06EB] = 0xC0u
            memory[0x06EC] = 0xD8u
            memory[0x06ED] = 0xE8u
            memory[0x06EE] = 0x24u
            memory[0x06EF] = 0xF8u
            memory[0x06F0] = 0xFCu
            memory[0x06F1] = 0x28u
            memory[0x06F2] = 0x2Cu
            memory[0x074E] = 0x01u

            // Execute decompiled function
            spriteShuffler()

            // Verify: Check output memory (24 addresses)
            assertEquals(0x28u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x01u, memory[0x06E0], "Memory 0x06E0 mismatch")
            assertEquals(0x88u, memory[0x06E5], "Memory 0x06E5 mismatch")
            assertEquals(0xA0u, memory[0x06E6], "Memory 0x06E6 mismatch")
            assertEquals(0xB8u, memory[0x06E7], "Memory 0x06E7 mismatch")
            assertEquals(0xD0u, memory[0x06E8], "Memory 0x06E8 mismatch")
            assertEquals(0xE8u, memory[0x06E9], "Memory 0x06E9 mismatch")
            assertEquals(0x28u, memory[0x06EA], "Memory 0x06EA mismatch")
            assertEquals(0x40u, memory[0x06EB], "Memory 0x06EB mismatch")
            assertEquals(0x58u, memory[0x06EC], "Memory 0x06EC mismatch")
            assertEquals(0x68u, memory[0x06ED], "Memory 0x06ED mismatch")
            assertEquals(0x78u, memory[0x06EF], "Memory 0x06EF mismatch")
            assertEquals(0x7Cu, memory[0x06F0], "Memory 0x06F0 mismatch")
            assertEquals(0x80u, memory[0x06F1], "Memory 0x06F1 mismatch")
            assertEquals(0x84u, memory[0x06F2], "Memory 0x06F2 mismatch")
            assertEquals(0xE8u, memory[0x06F3], "Memory 0x06F3 mismatch")
            assertEquals(0xF0u, memory[0x06F4], "Memory 0x06F4 mismatch")
            assertEquals(0xF8u, memory[0x06F5], "Memory 0x06F5 mismatch")
            assertEquals(0x28u, memory[0x06F6], "Memory 0x06F6 mismatch")
            assertEquals(0x30u, memory[0x06F7], "Memory 0x06F7 mismatch")
            assertEquals(0x38u, memory[0x06F8], "Memory 0x06F8 mismatch")
            assertEquals(0x40u, memory[0x06F9], "Memory 0x06F9 mismatch")
            assertEquals(0x48u, memory[0x06FA], "Memory 0x06FA mismatch")
            assertEquals(0x50u, memory[0x06FB], "Memory 0x06FB mismatch")
        }
    }

    /**
     * Test case 1 from frame 34
     * Function: spriteShuffler (0x81C6)
     * Call depth: 1
     * Memory reads: 19, writes: 24
     */
    @Test
    fun `spriteShuffler_frame34_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (19 addresses)
            memory[0x0000] = 0x28u
            memory[0x06E0] = 0x01u
            memory[0x06E2] = 0x48u
            memory[0x06E4] = 0x04u
            memory[0x06E5] = 0x88u
            memory[0x06E6] = 0xA0u
            memory[0x06E7] = 0xB8u
            memory[0x06E8] = 0xD0u
            memory[0x06E9] = 0xE8u
            memory[0x06EA] = 0x28u
            memory[0x06EB] = 0x40u
            memory[0x06EC] = 0x58u
            memory[0x06ED] = 0x68u
            memory[0x06EE] = 0x24u
            memory[0x06EF] = 0x78u
            memory[0x06F0] = 0x7Cu
            memory[0x06F1] = 0x80u
            memory[0x06F2] = 0x84u
            memory[0x074E] = 0x01u

            // Execute decompiled function
            spriteShuffler()

            // Verify: Check output memory (24 addresses)
            assertEquals(0x28u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x02u, memory[0x06E0], "Memory 0x06E0 mismatch")
            assertEquals(0xD0u, memory[0x06E5], "Memory 0x06E5 mismatch")
            assertEquals(0xE8u, memory[0x06E6], "Memory 0x06E6 mismatch")
            assertEquals(0x28u, memory[0x06E7], "Memory 0x06E7 mismatch")
            assertEquals(0x40u, memory[0x06E8], "Memory 0x06E8 mismatch")
            assertEquals(0x58u, memory[0x06E9], "Memory 0x06E9 mismatch")
            assertEquals(0x70u, memory[0x06EA], "Memory 0x06EA mismatch")
            assertEquals(0x88u, memory[0x06EB], "Memory 0x06EB mismatch")
            assertEquals(0xA0u, memory[0x06EC], "Memory 0x06EC mismatch")
            assertEquals(0xB0u, memory[0x06ED], "Memory 0x06ED mismatch")
            assertEquals(0xC0u, memory[0x06EF], "Memory 0x06EF mismatch")
            assertEquals(0xC4u, memory[0x06F0], "Memory 0x06F0 mismatch")
            assertEquals(0xC8u, memory[0x06F1], "Memory 0x06F1 mismatch")
            assertEquals(0xCCu, memory[0x06F2], "Memory 0x06F2 mismatch")
            assertEquals(0x58u, memory[0x06F3], "Memory 0x06F3 mismatch")
            assertEquals(0x60u, memory[0x06F4], "Memory 0x06F4 mismatch")
            assertEquals(0x68u, memory[0x06F5], "Memory 0x06F5 mismatch")
            assertEquals(0x70u, memory[0x06F6], "Memory 0x06F6 mismatch")
            assertEquals(0x78u, memory[0x06F7], "Memory 0x06F7 mismatch")
            assertEquals(0x80u, memory[0x06F8], "Memory 0x06F8 mismatch")
            assertEquals(0x88u, memory[0x06F9], "Memory 0x06F9 mismatch")
            assertEquals(0x90u, memory[0x06FA], "Memory 0x06FA mismatch")
            assertEquals(0x98u, memory[0x06FB], "Memory 0x06FB mismatch")
        }
    }

    /**
     * Test case 2 from frame 36
     * Function: spriteShuffler (0x81C6)
     * Call depth: 1
     * Memory reads: 19, writes: 24
     */
    @Test
    fun `spriteShuffler_frame36_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (19 addresses)
            memory[0x0000] = 0x28u
            memory[0x06E0] = 0x02u
            memory[0x06E3] = 0x38u
            memory[0x06E4] = 0x04u
            memory[0x06E5] = 0xD0u
            memory[0x06E6] = 0xE8u
            memory[0x06E7] = 0x28u
            memory[0x06E8] = 0x40u
            memory[0x06E9] = 0x58u
            memory[0x06EA] = 0x70u
            memory[0x06EB] = 0x88u
            memory[0x06EC] = 0xA0u
            memory[0x06ED] = 0xB0u
            memory[0x06EE] = 0x24u
            memory[0x06EF] = 0xC0u
            memory[0x06F0] = 0xC4u
            memory[0x06F1] = 0xC8u
            memory[0x06F2] = 0xCCu
            memory[0x074E] = 0x01u

            // Execute decompiled function
            spriteShuffler()

            // Verify: Check output memory (24 addresses)
            assertEquals(0x28u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x06E0], "Memory 0x06E0 mismatch")
            assertEquals(0x30u, memory[0x06E5], "Memory 0x06E5 mismatch")
            assertEquals(0x48u, memory[0x06E6], "Memory 0x06E6 mismatch")
            assertEquals(0x60u, memory[0x06E7], "Memory 0x06E7 mismatch")
            assertEquals(0x78u, memory[0x06E8], "Memory 0x06E8 mismatch")
            assertEquals(0x90u, memory[0x06E9], "Memory 0x06E9 mismatch")
            assertEquals(0xA8u, memory[0x06EA], "Memory 0x06EA mismatch")
            assertEquals(0xC0u, memory[0x06EB], "Memory 0x06EB mismatch")
            assertEquals(0xD8u, memory[0x06EC], "Memory 0x06EC mismatch")
            assertEquals(0xE8u, memory[0x06ED], "Memory 0x06ED mismatch")
            assertEquals(0xF8u, memory[0x06EF], "Memory 0x06EF mismatch")
            assertEquals(0xFCu, memory[0x06F0], "Memory 0x06F0 mismatch")
            assertEquals(0x28u, memory[0x06F1], "Memory 0x06F1 mismatch")
            assertEquals(0x2Cu, memory[0x06F2], "Memory 0x06F2 mismatch")
            assertEquals(0x90u, memory[0x06F3], "Memory 0x06F3 mismatch")
            assertEquals(0x98u, memory[0x06F4], "Memory 0x06F4 mismatch")
            assertEquals(0xA0u, memory[0x06F5], "Memory 0x06F5 mismatch")
            assertEquals(0xA8u, memory[0x06F6], "Memory 0x06F6 mismatch")
            assertEquals(0xB0u, memory[0x06F7], "Memory 0x06F7 mismatch")
            assertEquals(0xB8u, memory[0x06F8], "Memory 0x06F8 mismatch")
            assertEquals(0xC0u, memory[0x06F9], "Memory 0x06F9 mismatch")
            assertEquals(0xC8u, memory[0x06FA], "Memory 0x06FA mismatch")
            assertEquals(0xD0u, memory[0x06FB], "Memory 0x06FB mismatch")
        }
    }

    // =========================================
    // 0x8212: operModeExecutionTree
    // 9104 calls, 4 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 3
     * Function: operModeExecutionTree (0x8212)
     * Call depth: 1
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `operModeExecutionTree_frame3_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0770] = 0x00u

            // Execute decompiled function
            operModeExecutionTree()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 32
     * Function: operModeExecutionTree (0x8212)
     * Call depth: 1
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `operModeExecutionTree_frame32_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0770] = 0x00u

            // Execute decompiled function
            operModeExecutionTree()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 2 from frame 36
     * Function: operModeExecutionTree (0x8212)
     * Call depth: 1
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `operModeExecutionTree_frame36_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0770] = 0x01u

            // Execute decompiled function
            operModeExecutionTree()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 3 from frame 38
     * Function: operModeExecutionTree (0x8212)
     * Call depth: 1
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `operModeExecutionTree_frame38_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0770] = 0x01u

            // Execute decompiled function
            operModeExecutionTree()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0x8220: moveAllSpritesOffscreen
    // 6 calls, 3 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 3
     * Function: moveAllSpritesOffscreen (0x8220)
     * Call depth: 1
     * Memory reads: 1, writes: 64
     */
    @Test
    fun `moveAllSpritesOffscreen_frame3_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x04A0] = 0x00u

            // Execute decompiled function
            moveAllSpritesOffscreen()

            // Verify: Check output memory (64 addresses)
            assertEquals(0xF8u, memory[0x0200], "Memory 0x0200 mismatch")
            assertEquals(0xF8u, memory[0x0204], "Memory 0x0204 mismatch")
            assertEquals(0xF8u, memory[0x0208], "Memory 0x0208 mismatch")
            assertEquals(0xF8u, memory[0x020C], "Memory 0x020C mismatch")
            assertEquals(0xF8u, memory[0x0210], "Memory 0x0210 mismatch")
            assertEquals(0xF8u, memory[0x0214], "Memory 0x0214 mismatch")
            assertEquals(0xF8u, memory[0x0218], "Memory 0x0218 mismatch")
            assertEquals(0xF8u, memory[0x021C], "Memory 0x021C mismatch")
            assertEquals(0xF8u, memory[0x0220], "Memory 0x0220 mismatch")
            assertEquals(0xF8u, memory[0x0224], "Memory 0x0224 mismatch")
            assertEquals(0xF8u, memory[0x0228], "Memory 0x0228 mismatch")
            assertEquals(0xF8u, memory[0x022C], "Memory 0x022C mismatch")
            assertEquals(0xF8u, memory[0x0230], "Memory 0x0230 mismatch")
            assertEquals(0xF8u, memory[0x0234], "Memory 0x0234 mismatch")
            assertEquals(0xF8u, memory[0x0238], "Memory 0x0238 mismatch")
            assertEquals(0xF8u, memory[0x023C], "Memory 0x023C mismatch")
            assertEquals(0xF8u, memory[0x0240], "Memory 0x0240 mismatch")
            assertEquals(0xF8u, memory[0x0244], "Memory 0x0244 mismatch")
            assertEquals(0xF8u, memory[0x0248], "Memory 0x0248 mismatch")
            assertEquals(0xF8u, memory[0x024C], "Memory 0x024C mismatch")
            assertEquals(0xF8u, memory[0x0250], "Memory 0x0250 mismatch")
            assertEquals(0xF8u, memory[0x0254], "Memory 0x0254 mismatch")
            assertEquals(0xF8u, memory[0x0258], "Memory 0x0258 mismatch")
            assertEquals(0xF8u, memory[0x025C], "Memory 0x025C mismatch")
            assertEquals(0xF8u, memory[0x0260], "Memory 0x0260 mismatch")
            assertEquals(0xF8u, memory[0x0264], "Memory 0x0264 mismatch")
            assertEquals(0xF8u, memory[0x0268], "Memory 0x0268 mismatch")
            assertEquals(0xF8u, memory[0x026C], "Memory 0x026C mismatch")
            assertEquals(0xF8u, memory[0x0270], "Memory 0x0270 mismatch")
            assertEquals(0xF8u, memory[0x0274], "Memory 0x0274 mismatch")
            assertEquals(0xF8u, memory[0x0278], "Memory 0x0278 mismatch")
            assertEquals(0xF8u, memory[0x027C], "Memory 0x027C mismatch")
            assertEquals(0xF8u, memory[0x0280], "Memory 0x0280 mismatch")
            assertEquals(0xF8u, memory[0x0284], "Memory 0x0284 mismatch")
            assertEquals(0xF8u, memory[0x0288], "Memory 0x0288 mismatch")
            assertEquals(0xF8u, memory[0x028C], "Memory 0x028C mismatch")
            assertEquals(0xF8u, memory[0x0290], "Memory 0x0290 mismatch")
            assertEquals(0xF8u, memory[0x0294], "Memory 0x0294 mismatch")
            assertEquals(0xF8u, memory[0x0298], "Memory 0x0298 mismatch")
            assertEquals(0xF8u, memory[0x029C], "Memory 0x029C mismatch")
            assertEquals(0xF8u, memory[0x02A0], "Memory 0x02A0 mismatch")
            assertEquals(0xF8u, memory[0x02A4], "Memory 0x02A4 mismatch")
            assertEquals(0xF8u, memory[0x02A8], "Memory 0x02A8 mismatch")
            assertEquals(0xF8u, memory[0x02AC], "Memory 0x02AC mismatch")
            assertEquals(0xF8u, memory[0x02B0], "Memory 0x02B0 mismatch")
            assertEquals(0xF8u, memory[0x02B4], "Memory 0x02B4 mismatch")
            assertEquals(0xF8u, memory[0x02B8], "Memory 0x02B8 mismatch")
            assertEquals(0xF8u, memory[0x02BC], "Memory 0x02BC mismatch")
            assertEquals(0xF8u, memory[0x02C0], "Memory 0x02C0 mismatch")
            assertEquals(0xF8u, memory[0x02C4], "Memory 0x02C4 mismatch")
            assertEquals(0xF8u, memory[0x02C8], "Memory 0x02C8 mismatch")
            assertEquals(0xF8u, memory[0x02CC], "Memory 0x02CC mismatch")
            assertEquals(0xF8u, memory[0x02D0], "Memory 0x02D0 mismatch")
            assertEquals(0xF8u, memory[0x02D4], "Memory 0x02D4 mismatch")
            assertEquals(0xF8u, memory[0x02D8], "Memory 0x02D8 mismatch")
            assertEquals(0xF8u, memory[0x02DC], "Memory 0x02DC mismatch")
            assertEquals(0xF8u, memory[0x02E0], "Memory 0x02E0 mismatch")
            assertEquals(0xF8u, memory[0x02E4], "Memory 0x02E4 mismatch")
            assertEquals(0xF8u, memory[0x02E8], "Memory 0x02E8 mismatch")
            assertEquals(0xF8u, memory[0x02EC], "Memory 0x02EC mismatch")
            assertEquals(0xF8u, memory[0x02F0], "Memory 0x02F0 mismatch")
            assertEquals(0xF8u, memory[0x02F4], "Memory 0x02F4 mismatch")
            assertEquals(0xF8u, memory[0x02F8], "Memory 0x02F8 mismatch")
            assertEquals(0xF8u, memory[0x02FC], "Memory 0x02FC mismatch")
        }
    }

    /**
     * Test case 1 from frame 6
     * Function: moveAllSpritesOffscreen (0x8220)
     * Call depth: 5
     * Memory reads: 1, writes: 64
     */
    @Test
    fun `moveAllSpritesOffscreen_frame6_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x04A0] = 0x00u

            // Execute decompiled function
            moveAllSpritesOffscreen()

            // Verify: Check output memory (64 addresses)
            assertEquals(0xF8u, memory[0x0200], "Memory 0x0200 mismatch")
            assertEquals(0xF8u, memory[0x0204], "Memory 0x0204 mismatch")
            assertEquals(0xF8u, memory[0x0208], "Memory 0x0208 mismatch")
            assertEquals(0xF8u, memory[0x020C], "Memory 0x020C mismatch")
            assertEquals(0xF8u, memory[0x0210], "Memory 0x0210 mismatch")
            assertEquals(0xF8u, memory[0x0214], "Memory 0x0214 mismatch")
            assertEquals(0xF8u, memory[0x0218], "Memory 0x0218 mismatch")
            assertEquals(0xF8u, memory[0x021C], "Memory 0x021C mismatch")
            assertEquals(0xF8u, memory[0x0220], "Memory 0x0220 mismatch")
            assertEquals(0xF8u, memory[0x0224], "Memory 0x0224 mismatch")
            assertEquals(0xF8u, memory[0x0228], "Memory 0x0228 mismatch")
            assertEquals(0xF8u, memory[0x022C], "Memory 0x022C mismatch")
            assertEquals(0xF8u, memory[0x0230], "Memory 0x0230 mismatch")
            assertEquals(0xF8u, memory[0x0234], "Memory 0x0234 mismatch")
            assertEquals(0xF8u, memory[0x0238], "Memory 0x0238 mismatch")
            assertEquals(0xF8u, memory[0x023C], "Memory 0x023C mismatch")
            assertEquals(0xF8u, memory[0x0240], "Memory 0x0240 mismatch")
            assertEquals(0xF8u, memory[0x0244], "Memory 0x0244 mismatch")
            assertEquals(0xF8u, memory[0x0248], "Memory 0x0248 mismatch")
            assertEquals(0xF8u, memory[0x024C], "Memory 0x024C mismatch")
            assertEquals(0xF8u, memory[0x0250], "Memory 0x0250 mismatch")
            assertEquals(0xF8u, memory[0x0254], "Memory 0x0254 mismatch")
            assertEquals(0xF8u, memory[0x0258], "Memory 0x0258 mismatch")
            assertEquals(0xF8u, memory[0x025C], "Memory 0x025C mismatch")
            assertEquals(0xF8u, memory[0x0260], "Memory 0x0260 mismatch")
            assertEquals(0xF8u, memory[0x0264], "Memory 0x0264 mismatch")
            assertEquals(0xF8u, memory[0x0268], "Memory 0x0268 mismatch")
            assertEquals(0xF8u, memory[0x026C], "Memory 0x026C mismatch")
            assertEquals(0xF8u, memory[0x0270], "Memory 0x0270 mismatch")
            assertEquals(0xF8u, memory[0x0274], "Memory 0x0274 mismatch")
            assertEquals(0xF8u, memory[0x0278], "Memory 0x0278 mismatch")
            assertEquals(0xF8u, memory[0x027C], "Memory 0x027C mismatch")
            assertEquals(0xF8u, memory[0x0280], "Memory 0x0280 mismatch")
            assertEquals(0xF8u, memory[0x0284], "Memory 0x0284 mismatch")
            assertEquals(0xF8u, memory[0x0288], "Memory 0x0288 mismatch")
            assertEquals(0xF8u, memory[0x028C], "Memory 0x028C mismatch")
            assertEquals(0xF8u, memory[0x0290], "Memory 0x0290 mismatch")
            assertEquals(0xF8u, memory[0x0294], "Memory 0x0294 mismatch")
            assertEquals(0xF8u, memory[0x0298], "Memory 0x0298 mismatch")
            assertEquals(0xF8u, memory[0x029C], "Memory 0x029C mismatch")
            assertEquals(0xF8u, memory[0x02A0], "Memory 0x02A0 mismatch")
            assertEquals(0xF8u, memory[0x02A4], "Memory 0x02A4 mismatch")
            assertEquals(0xF8u, memory[0x02A8], "Memory 0x02A8 mismatch")
            assertEquals(0xF8u, memory[0x02AC], "Memory 0x02AC mismatch")
            assertEquals(0xF8u, memory[0x02B0], "Memory 0x02B0 mismatch")
            assertEquals(0xF8u, memory[0x02B4], "Memory 0x02B4 mismatch")
            assertEquals(0xF8u, memory[0x02B8], "Memory 0x02B8 mismatch")
            assertEquals(0xF8u, memory[0x02BC], "Memory 0x02BC mismatch")
            assertEquals(0xF8u, memory[0x02C0], "Memory 0x02C0 mismatch")
            assertEquals(0xF8u, memory[0x02C4], "Memory 0x02C4 mismatch")
            assertEquals(0xF8u, memory[0x02C8], "Memory 0x02C8 mismatch")
            assertEquals(0xF8u, memory[0x02CC], "Memory 0x02CC mismatch")
            assertEquals(0xF8u, memory[0x02D0], "Memory 0x02D0 mismatch")
            assertEquals(0xF8u, memory[0x02D4], "Memory 0x02D4 mismatch")
            assertEquals(0xF8u, memory[0x02D8], "Memory 0x02D8 mismatch")
            assertEquals(0xF8u, memory[0x02DC], "Memory 0x02DC mismatch")
            assertEquals(0xF8u, memory[0x02E0], "Memory 0x02E0 mismatch")
            assertEquals(0xF8u, memory[0x02E4], "Memory 0x02E4 mismatch")
            assertEquals(0xF8u, memory[0x02E8], "Memory 0x02E8 mismatch")
            assertEquals(0xF8u, memory[0x02EC], "Memory 0x02EC mismatch")
            assertEquals(0xF8u, memory[0x02F0], "Memory 0x02F0 mismatch")
            assertEquals(0xF8u, memory[0x02F4], "Memory 0x02F4 mismatch")
            assertEquals(0xF8u, memory[0x02F8], "Memory 0x02F8 mismatch")
            assertEquals(0xF8u, memory[0x02FC], "Memory 0x02FC mismatch")
        }
    }

    /**
     * Test case 2 from frame 177
     * Function: moveAllSpritesOffscreen (0x8220)
     * Call depth: 5
     * Memory reads: 1, writes: 64
     */
    @Test
    fun `moveAllSpritesOffscreen_frame177_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x04A0] = 0x00u

            // Execute decompiled function
            moveAllSpritesOffscreen()

            // Verify: Check output memory (64 addresses)
            assertEquals(0xF8u, memory[0x0200], "Memory 0x0200 mismatch")
            assertEquals(0xF8u, memory[0x0204], "Memory 0x0204 mismatch")
            assertEquals(0xF8u, memory[0x0208], "Memory 0x0208 mismatch")
            assertEquals(0xF8u, memory[0x020C], "Memory 0x020C mismatch")
            assertEquals(0xF8u, memory[0x0210], "Memory 0x0210 mismatch")
            assertEquals(0xF8u, memory[0x0214], "Memory 0x0214 mismatch")
            assertEquals(0xF8u, memory[0x0218], "Memory 0x0218 mismatch")
            assertEquals(0xF8u, memory[0x021C], "Memory 0x021C mismatch")
            assertEquals(0xF8u, memory[0x0220], "Memory 0x0220 mismatch")
            assertEquals(0xF8u, memory[0x0224], "Memory 0x0224 mismatch")
            assertEquals(0xF8u, memory[0x0228], "Memory 0x0228 mismatch")
            assertEquals(0xF8u, memory[0x022C], "Memory 0x022C mismatch")
            assertEquals(0xF8u, memory[0x0230], "Memory 0x0230 mismatch")
            assertEquals(0xF8u, memory[0x0234], "Memory 0x0234 mismatch")
            assertEquals(0xF8u, memory[0x0238], "Memory 0x0238 mismatch")
            assertEquals(0xF8u, memory[0x023C], "Memory 0x023C mismatch")
            assertEquals(0xF8u, memory[0x0240], "Memory 0x0240 mismatch")
            assertEquals(0xF8u, memory[0x0244], "Memory 0x0244 mismatch")
            assertEquals(0xF8u, memory[0x0248], "Memory 0x0248 mismatch")
            assertEquals(0xF8u, memory[0x024C], "Memory 0x024C mismatch")
            assertEquals(0xF8u, memory[0x0250], "Memory 0x0250 mismatch")
            assertEquals(0xF8u, memory[0x0254], "Memory 0x0254 mismatch")
            assertEquals(0xF8u, memory[0x0258], "Memory 0x0258 mismatch")
            assertEquals(0xF8u, memory[0x025C], "Memory 0x025C mismatch")
            assertEquals(0xF8u, memory[0x0260], "Memory 0x0260 mismatch")
            assertEquals(0xF8u, memory[0x0264], "Memory 0x0264 mismatch")
            assertEquals(0xF8u, memory[0x0268], "Memory 0x0268 mismatch")
            assertEquals(0xF8u, memory[0x026C], "Memory 0x026C mismatch")
            assertEquals(0xF8u, memory[0x0270], "Memory 0x0270 mismatch")
            assertEquals(0xF8u, memory[0x0274], "Memory 0x0274 mismatch")
            assertEquals(0xF8u, memory[0x0278], "Memory 0x0278 mismatch")
            assertEquals(0xF8u, memory[0x027C], "Memory 0x027C mismatch")
            assertEquals(0xF8u, memory[0x0280], "Memory 0x0280 mismatch")
            assertEquals(0xF8u, memory[0x0284], "Memory 0x0284 mismatch")
            assertEquals(0xF8u, memory[0x0288], "Memory 0x0288 mismatch")
            assertEquals(0xF8u, memory[0x028C], "Memory 0x028C mismatch")
            assertEquals(0xF8u, memory[0x0290], "Memory 0x0290 mismatch")
            assertEquals(0xF8u, memory[0x0294], "Memory 0x0294 mismatch")
            assertEquals(0xF8u, memory[0x0298], "Memory 0x0298 mismatch")
            assertEquals(0xF8u, memory[0x029C], "Memory 0x029C mismatch")
            assertEquals(0xF8u, memory[0x02A0], "Memory 0x02A0 mismatch")
            assertEquals(0xF8u, memory[0x02A4], "Memory 0x02A4 mismatch")
            assertEquals(0xF8u, memory[0x02A8], "Memory 0x02A8 mismatch")
            assertEquals(0xF8u, memory[0x02AC], "Memory 0x02AC mismatch")
            assertEquals(0xF8u, memory[0x02B0], "Memory 0x02B0 mismatch")
            assertEquals(0xF8u, memory[0x02B4], "Memory 0x02B4 mismatch")
            assertEquals(0xF8u, memory[0x02B8], "Memory 0x02B8 mismatch")
            assertEquals(0xF8u, memory[0x02BC], "Memory 0x02BC mismatch")
            assertEquals(0xF8u, memory[0x02C0], "Memory 0x02C0 mismatch")
            assertEquals(0xF8u, memory[0x02C4], "Memory 0x02C4 mismatch")
            assertEquals(0xF8u, memory[0x02C8], "Memory 0x02C8 mismatch")
            assertEquals(0xF8u, memory[0x02CC], "Memory 0x02CC mismatch")
            assertEquals(0xF8u, memory[0x02D0], "Memory 0x02D0 mismatch")
            assertEquals(0xF8u, memory[0x02D4], "Memory 0x02D4 mismatch")
            assertEquals(0xF8u, memory[0x02D8], "Memory 0x02D8 mismatch")
            assertEquals(0xF8u, memory[0x02DC], "Memory 0x02DC mismatch")
            assertEquals(0xF8u, memory[0x02E0], "Memory 0x02E0 mismatch")
            assertEquals(0xF8u, memory[0x02E4], "Memory 0x02E4 mismatch")
            assertEquals(0xF8u, memory[0x02E8], "Memory 0x02E8 mismatch")
            assertEquals(0xF8u, memory[0x02EC], "Memory 0x02EC mismatch")
            assertEquals(0xF8u, memory[0x02F0], "Memory 0x02F0 mismatch")
            assertEquals(0xF8u, memory[0x02F4], "Memory 0x02F4 mismatch")
            assertEquals(0xF8u, memory[0x02F8], "Memory 0x02F8 mismatch")
            assertEquals(0xF8u, memory[0x02FC], "Memory 0x02FC mismatch")
        }
    }

    // =========================================
    // 0x8223: moveSpritesOffscreen
    // 8756 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 32
     * Function: moveSpritesOffscreen (0x8223)
     * Call depth: 1
     * Memory reads: 0, writes: 63
     */
    @Test
    fun `moveSpritesOffscreen_frame32_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            moveSpritesOffscreen()

            // Verify: Check output memory (63 addresses)
            assertEquals(0xF8u, memory[0x0204], "Memory 0x0204 mismatch")
            assertEquals(0xF8u, memory[0x0208], "Memory 0x0208 mismatch")
            assertEquals(0xF8u, memory[0x020C], "Memory 0x020C mismatch")
            assertEquals(0xF8u, memory[0x0210], "Memory 0x0210 mismatch")
            assertEquals(0xF8u, memory[0x0214], "Memory 0x0214 mismatch")
            assertEquals(0xF8u, memory[0x0218], "Memory 0x0218 mismatch")
            assertEquals(0xF8u, memory[0x021C], "Memory 0x021C mismatch")
            assertEquals(0xF8u, memory[0x0220], "Memory 0x0220 mismatch")
            assertEquals(0xF8u, memory[0x0224], "Memory 0x0224 mismatch")
            assertEquals(0xF8u, memory[0x0228], "Memory 0x0228 mismatch")
            assertEquals(0xF8u, memory[0x022C], "Memory 0x022C mismatch")
            assertEquals(0xF8u, memory[0x0230], "Memory 0x0230 mismatch")
            assertEquals(0xF8u, memory[0x0234], "Memory 0x0234 mismatch")
            assertEquals(0xF8u, memory[0x0238], "Memory 0x0238 mismatch")
            assertEquals(0xF8u, memory[0x023C], "Memory 0x023C mismatch")
            assertEquals(0xF8u, memory[0x0240], "Memory 0x0240 mismatch")
            assertEquals(0xF8u, memory[0x0244], "Memory 0x0244 mismatch")
            assertEquals(0xF8u, memory[0x0248], "Memory 0x0248 mismatch")
            assertEquals(0xF8u, memory[0x024C], "Memory 0x024C mismatch")
            assertEquals(0xF8u, memory[0x0250], "Memory 0x0250 mismatch")
            assertEquals(0xF8u, memory[0x0254], "Memory 0x0254 mismatch")
            assertEquals(0xF8u, memory[0x0258], "Memory 0x0258 mismatch")
            assertEquals(0xF8u, memory[0x025C], "Memory 0x025C mismatch")
            assertEquals(0xF8u, memory[0x0260], "Memory 0x0260 mismatch")
            assertEquals(0xF8u, memory[0x0264], "Memory 0x0264 mismatch")
            assertEquals(0xF8u, memory[0x0268], "Memory 0x0268 mismatch")
            assertEquals(0xF8u, memory[0x026C], "Memory 0x026C mismatch")
            assertEquals(0xF8u, memory[0x0270], "Memory 0x0270 mismatch")
            assertEquals(0xF8u, memory[0x0274], "Memory 0x0274 mismatch")
            assertEquals(0xF8u, memory[0x0278], "Memory 0x0278 mismatch")
            assertEquals(0xF8u, memory[0x027C], "Memory 0x027C mismatch")
            assertEquals(0xF8u, memory[0x0280], "Memory 0x0280 mismatch")
            assertEquals(0xF8u, memory[0x0284], "Memory 0x0284 mismatch")
            assertEquals(0xF8u, memory[0x0288], "Memory 0x0288 mismatch")
            assertEquals(0xF8u, memory[0x028C], "Memory 0x028C mismatch")
            assertEquals(0xF8u, memory[0x0290], "Memory 0x0290 mismatch")
            assertEquals(0xF8u, memory[0x0294], "Memory 0x0294 mismatch")
            assertEquals(0xF8u, memory[0x0298], "Memory 0x0298 mismatch")
            assertEquals(0xF8u, memory[0x029C], "Memory 0x029C mismatch")
            assertEquals(0xF8u, memory[0x02A0], "Memory 0x02A0 mismatch")
            assertEquals(0xF8u, memory[0x02A4], "Memory 0x02A4 mismatch")
            assertEquals(0xF8u, memory[0x02A8], "Memory 0x02A8 mismatch")
            assertEquals(0xF8u, memory[0x02AC], "Memory 0x02AC mismatch")
            assertEquals(0xF8u, memory[0x02B0], "Memory 0x02B0 mismatch")
            assertEquals(0xF8u, memory[0x02B4], "Memory 0x02B4 mismatch")
            assertEquals(0xF8u, memory[0x02B8], "Memory 0x02B8 mismatch")
            assertEquals(0xF8u, memory[0x02BC], "Memory 0x02BC mismatch")
            assertEquals(0xF8u, memory[0x02C0], "Memory 0x02C0 mismatch")
            assertEquals(0xF8u, memory[0x02C4], "Memory 0x02C4 mismatch")
            assertEquals(0xF8u, memory[0x02C8], "Memory 0x02C8 mismatch")
            assertEquals(0xF8u, memory[0x02CC], "Memory 0x02CC mismatch")
            assertEquals(0xF8u, memory[0x02D0], "Memory 0x02D0 mismatch")
            assertEquals(0xF8u, memory[0x02D4], "Memory 0x02D4 mismatch")
            assertEquals(0xF8u, memory[0x02D8], "Memory 0x02D8 mismatch")
            assertEquals(0xF8u, memory[0x02DC], "Memory 0x02DC mismatch")
            assertEquals(0xF8u, memory[0x02E0], "Memory 0x02E0 mismatch")
            assertEquals(0xF8u, memory[0x02E4], "Memory 0x02E4 mismatch")
            assertEquals(0xF8u, memory[0x02E8], "Memory 0x02E8 mismatch")
            assertEquals(0xF8u, memory[0x02EC], "Memory 0x02EC mismatch")
            assertEquals(0xF8u, memory[0x02F0], "Memory 0x02F0 mismatch")
            assertEquals(0xF8u, memory[0x02F4], "Memory 0x02F4 mismatch")
            assertEquals(0xF8u, memory[0x02F8], "Memory 0x02F8 mismatch")
            assertEquals(0xF8u, memory[0x02FC], "Memory 0x02FC mismatch")
        }
    }

    // =========================================
    // 0x8231: titleScreenMode
    // 28 calls, 4 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 3
     * Function: titleScreenMode (0x8231)
     * Call depth: 2
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `titleScreenMode_frame3_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0772] = 0x00u

            // Execute decompiled function
            titleScreenMode()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 6
     * Function: titleScreenMode (0x8231)
     * Call depth: 2
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `titleScreenMode_frame6_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0772] = 0x01u

            // Execute decompiled function
            titleScreenMode()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 2 from frame 30
     * Function: titleScreenMode (0x8231)
     * Call depth: 2
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `titleScreenMode_frame30_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0772] = 0x02u

            // Execute decompiled function
            titleScreenMode()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 3 from frame 32
     * Function: titleScreenMode (0x8231)
     * Call depth: 2
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `titleScreenMode_frame32_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0772] = 0x03u

            // Execute decompiled function
            titleScreenMode()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0x8245: gameMenuRoutine
    // 2 calls, 2 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 32
     * Function: gameMenuRoutine (0x8245)
     * Call depth: 3
     * Memory reads: 4, writes: 0
     */
    @Test
    fun `gameMenuRoutine_frame32_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x06FC] = 0x00u
            memory[0x06FD] = 0x00u
            memory[0x07A2] = 0x17u
            memory[0x07FC] = 0x00u

            // Execute decompiled function
            gameMenuRoutine()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 34
     * Function: gameMenuRoutine (0x8245)
     * Call depth: 3
     * Memory reads: 2, writes: 0
     */
    @Test
    fun `gameMenuRoutine_frame34_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x06FC] = 0x10u
            memory[0x06FD] = 0x00u

            // Execute decompiled function
            gameMenuRoutine()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0x82BB: nullJoypad
    // 1 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 32
     * Function: nullJoypad (0x82BB)
     * Call depth: 3
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `nullJoypad_frame32_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            nullJoypad()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x06FC], "Memory 0x06FC mismatch")
        }
    }

    // =========================================
    // 0x82C0: runDemo
    // 1 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 32
     * Function: runDemo (0x82C0)
     * Call depth: 3
     * Memory reads: 3, writes: 2
     */
    @Test
    fun `runDemo_frame32_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01FA] = 0x77u
            memory[0x01FB] = 0x81u
            memory[0x01FC] = 0x10u

            // Execute decompiled function
            runDemo()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0x82D8: chkContinue
    // Parameters: A
    // 1 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 34
     * Function: chkContinue (0x82D8)
     * Parameters: A
     * Call depth: 3
     * Memory reads: 8, writes: 33
     */
    @Test
    fun `chkContinue_frame34_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (8 addresses)
            memory[0x01F8] = 0xE8u
            memory[0x01F9] = 0x82u
            memory[0x0757] = 0x00u
            memory[0x075D] = 0x00u
            memory[0x0764] = 0x00u
            memory[0x0770] = 0x00u
            memory[0x07A2] = 0x17u
            memory[0x07FC] = 0x00u

            // Execute decompiled function
            chkContinue(0x10)

            // Verify: Check output memory (31 addresses)
            assertEquals(0x01u, memory[0x0757], "Memory 0x0757 mismatch")
            assertEquals(0x01u, memory[0x075D], "Memory 0x075D mismatch")
            assertEquals(0x01u, memory[0x0764], "Memory 0x0764 mismatch")
            assertEquals(0x00u, memory[0x076A], "Memory 0x076A mismatch")
            assertEquals(0x01u, memory[0x0770], "Memory 0x0770 mismatch")
            assertEquals(0x00u, memory[0x0772], "Memory 0x0772 mismatch")
            assertEquals(0x00u, memory[0x07A2], "Memory 0x07A2 mismatch")
            assertEquals(0x00u, memory[0x07DD], "Memory 0x07DD mismatch")
            assertEquals(0x00u, memory[0x07DE], "Memory 0x07DE mismatch")
            assertEquals(0x00u, memory[0x07DF], "Memory 0x07DF mismatch")
            assertEquals(0x00u, memory[0x07E0], "Memory 0x07E0 mismatch")
            assertEquals(0x00u, memory[0x07E1], "Memory 0x07E1 mismatch")
            assertEquals(0x00u, memory[0x07E2], "Memory 0x07E2 mismatch")
            assertEquals(0x00u, memory[0x07E3], "Memory 0x07E3 mismatch")
            assertEquals(0x00u, memory[0x07E4], "Memory 0x07E4 mismatch")
            assertEquals(0x00u, memory[0x07E5], "Memory 0x07E5 mismatch")
            assertEquals(0x00u, memory[0x07E6], "Memory 0x07E6 mismatch")
            assertEquals(0x00u, memory[0x07E7], "Memory 0x07E7 mismatch")
            assertEquals(0x00u, memory[0x07E8], "Memory 0x07E8 mismatch")
            assertEquals(0x00u, memory[0x07E9], "Memory 0x07E9 mismatch")
            assertEquals(0x00u, memory[0x07EA], "Memory 0x07EA mismatch")
            assertEquals(0x00u, memory[0x07EB], "Memory 0x07EB mismatch")
            assertEquals(0x00u, memory[0x07EC], "Memory 0x07EC mismatch")
            assertEquals(0x00u, memory[0x07ED], "Memory 0x07ED mismatch")
            assertEquals(0x00u, memory[0x07EE], "Memory 0x07EE mismatch")
            assertEquals(0x00u, memory[0x07EF], "Memory 0x07EF mismatch")
            assertEquals(0x00u, memory[0x07F0], "Memory 0x07F0 mismatch")
            assertEquals(0x00u, memory[0x07F1], "Memory 0x07F1 mismatch")
            assertEquals(0x00u, memory[0x07F2], "Memory 0x07F2 mismatch")
            assertEquals(0x00u, memory[0x07F3], "Memory 0x07F3 mismatch")
            assertEquals(0x00u, memory[0x07F4], "Memory 0x07F4 mismatch")
        }
    }

    // =========================================
    // 0x8325: drawMushroomIcon
    // 1 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 29
     * Function: drawMushroomIcon (0x8325)
     * Call depth: 5
     * Memory reads: 1, writes: 8
     */
    @Test
    fun `drawMushroomIcon_frame29_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x077A] = 0x00u

            // Execute decompiled function
            drawMushroomIcon()

            // Verify: Check output memory (8 addresses)
            assertEquals(0x07u, memory[0x0300], "Memory 0x0300 mismatch")
            assertEquals(0x22u, memory[0x0301], "Memory 0x0301 mismatch")
            assertEquals(0x49u, memory[0x0302], "Memory 0x0302 mismatch")
            assertEquals(0x83u, memory[0x0303], "Memory 0x0303 mismatch")
            assertEquals(0xCEu, memory[0x0304], "Memory 0x0304 mismatch")
            assertEquals(0x24u, memory[0x0305], "Memory 0x0305 mismatch")
            assertEquals(0x24u, memory[0x0306], "Memory 0x0306 mismatch")
            assertEquals(0x00u, memory[0x0307], "Memory 0x0307 mismatch")
        }
    }

    // =========================================
    // 0x84C3: floateyNumbersRoutine
    // Parameters: X
    // 52518 calls, 84 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 32
     * Function: floateyNumbersRoutine (0x84C3)
     * Parameters: X
     * Call depth: 6
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `floateyNumbersRoutine_frame32_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0110] = 0x00u

            // Execute decompiled function
            floateyNumbersRoutine(0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 196
     * Function: floateyNumbersRoutine (0x84C3)
     * Parameters: X
     * Call depth: 5
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `floateyNumbersRoutine_frame196_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0112] = 0x00u

            // Execute decompiled function
            floateyNumbersRoutine(0x02)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 2 from frame 1125
     * Function: floateyNumbersRoutine (0x84C3)
     * Parameters: X
     * Call depth: 6
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `floateyNumbersRoutine_frame1125_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0113] = 0x00u

            // Execute decompiled function
            floateyNumbersRoutine(0x03)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 3 from frame 1159
     * Function: floateyNumbersRoutine (0x84C3)
     * Parameters: X
     * Call depth: 8
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `floateyNumbersRoutine_frame1159_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0110] = 0x00u

            // Execute decompiled function
            floateyNumbersRoutine(0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 4 from frame 4289
     * Function: floateyNumbersRoutine (0x84C3)
     * Parameters: X
     * Call depth: 8
     * Memory reads: 10, writes: 10
     */
    @Test
    fun `floateyNumbersRoutine_frame4289_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (10 addresses)
            memory[0x0008] = 0x00u
            memory[0x0016] = 0x06u
            memory[0x001E] = 0x04u
            memory[0x0110] = 0x01u
            memory[0x0117] = 0x6Cu
            memory[0x011E] = 0xB6u
            memory[0x012C] = 0x2Eu
            memory[0x01F6] = 0x3Eu
            memory[0x01F7] = 0x85u
            memory[0x06E5] = 0x30u

            // Execute decompiled function
            floateyNumbersRoutine(0x00)

            // Verify: Check output memory (6 addresses)
            assertEquals(0xF6u, memory[0x0231], "Memory 0x0231 mismatch")
            assertEquals(0x02u, memory[0x0232], "Memory 0x0232 mismatch")
            assertEquals(0x6Cu, memory[0x0233], "Memory 0x0233 mismatch")
            assertEquals(0xFBu, memory[0x0235], "Memory 0x0235 mismatch")
            assertEquals(0x02u, memory[0x0236], "Memory 0x0236 mismatch")
            assertEquals(0x74u, memory[0x0237], "Memory 0x0237 mismatch")
        }
    }

    /**
     * Test case 5 from frame 4307
     * Function: floateyNumbersRoutine (0x84C3)
     * Parameters: X
     * Call depth: 8
     * Memory reads: 10, writes: 10
     */
    @Test
    fun `floateyNumbersRoutine_frame4307_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (10 addresses)
            memory[0x0008] = 0x00u
            memory[0x0016] = 0x06u
            memory[0x001E] = 0x04u
            memory[0x0110] = 0x01u
            memory[0x0117] = 0x6Cu
            memory[0x011E] = 0xADu
            memory[0x012C] = 0x25u
            memory[0x01F6] = 0x3Eu
            memory[0x01F7] = 0x85u
            memory[0x06E5] = 0x30u

            // Execute decompiled function
            floateyNumbersRoutine(0x00)

            // Verify: Check output memory (6 addresses)
            assertEquals(0xF6u, memory[0x0231], "Memory 0x0231 mismatch")
            assertEquals(0x02u, memory[0x0232], "Memory 0x0232 mismatch")
            assertEquals(0x6Cu, memory[0x0233], "Memory 0x0233 mismatch")
            assertEquals(0xFBu, memory[0x0235], "Memory 0x0235 mismatch")
            assertEquals(0x02u, memory[0x0236], "Memory 0x0236 mismatch")
            assertEquals(0x74u, memory[0x0237], "Memory 0x0237 mismatch")
        }
    }

    /**
     * Test case 6 from frame 4323
     * Function: floateyNumbersRoutine (0x84C3)
     * Parameters: X
     * Call depth: 8
     * Memory reads: 10, writes: 10
     */
    @Test
    fun `floateyNumbersRoutine_frame4323_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (10 addresses)
            memory[0x0008] = 0x00u
            memory[0x0016] = 0x06u
            memory[0x001E] = 0x04u
            memory[0x0110] = 0x01u
            memory[0x0117] = 0x6Cu
            memory[0x011E] = 0xA5u
            memory[0x012C] = 0x1Du
            memory[0x01F6] = 0x3Eu
            memory[0x01F7] = 0x85u
            memory[0x06E5] = 0xD0u

            // Execute decompiled function
            floateyNumbersRoutine(0x00)

            // Verify: Check output memory (6 addresses)
            assertEquals(0xF6u, memory[0x02D1], "Memory 0x02D1 mismatch")
            assertEquals(0x02u, memory[0x02D2], "Memory 0x02D2 mismatch")
            assertEquals(0x6Cu, memory[0x02D3], "Memory 0x02D3 mismatch")
            assertEquals(0xFBu, memory[0x02D5], "Memory 0x02D5 mismatch")
            assertEquals(0x02u, memory[0x02D6], "Memory 0x02D6 mismatch")
            assertEquals(0x74u, memory[0x02D7], "Memory 0x02D7 mismatch")
        }
    }

    /**
     * Test case 7 from frame 4339
     * Function: floateyNumbersRoutine (0x84C3)
     * Parameters: X
     * Call depth: 8
     * Memory reads: 10, writes: 10
     */
    @Test
    fun `floateyNumbersRoutine_frame4339_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (10 addresses)
            memory[0x0008] = 0x00u
            memory[0x0016] = 0x06u
            memory[0x001E] = 0x04u
            memory[0x0110] = 0x01u
            memory[0x0117] = 0x6Cu
            memory[0x011E] = 0x9Du
            memory[0x012C] = 0x15u
            memory[0x01F6] = 0x3Eu
            memory[0x01F7] = 0x85u
            memory[0x06E5] = 0x88u

            // Execute decompiled function
            floateyNumbersRoutine(0x00)

            // Verify: Check output memory (6 addresses)
            assertEquals(0xF6u, memory[0x0289], "Memory 0x0289 mismatch")
            assertEquals(0x02u, memory[0x028A], "Memory 0x028A mismatch")
            assertEquals(0x6Cu, memory[0x028B], "Memory 0x028B mismatch")
            assertEquals(0xFBu, memory[0x028D], "Memory 0x028D mismatch")
            assertEquals(0x02u, memory[0x028E], "Memory 0x028E mismatch")
            assertEquals(0x74u, memory[0x028F], "Memory 0x028F mismatch")
        }
    }

    /**
     * Test case 8 from frame 4355
     * Function: floateyNumbersRoutine (0x84C3)
     * Parameters: X
     * Call depth: 8
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `floateyNumbersRoutine_frame4355_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0111] = 0x00u

            // Execute decompiled function
            floateyNumbersRoutine(0x01)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 9 from frame 4363
     * Function: floateyNumbersRoutine (0x84C3)
     * Parameters: X
     * Call depth: 8
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `floateyNumbersRoutine_frame4363_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0110] = 0x00u

            // Execute decompiled function
            floateyNumbersRoutine(0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0x8567: screenRoutines
    // 343 calls, 15 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 6
     * Function: screenRoutines (0x8567)
     * Call depth: 3
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `screenRoutines_frame6_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x073C] = 0x00u

            // Execute decompiled function
            screenRoutines()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 7
     * Function: screenRoutines (0x8567)
     * Call depth: 3
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `screenRoutines_frame7_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x073C] = 0x01u

            // Execute decompiled function
            screenRoutines()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 2 from frame 10
     * Function: screenRoutines (0x8567)
     * Call depth: 3
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `screenRoutines_frame10_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x073C] = 0x03u

            // Execute decompiled function
            screenRoutines()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 3 from frame 10
     * Function: screenRoutines (0x8567)
     * Call depth: 3
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `screenRoutines_frame10_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x073C] = 0x04u

            // Execute decompiled function
            screenRoutines()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 4 from frame 12
     * Function: screenRoutines (0x8567)
     * Call depth: 3
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `screenRoutines_frame12_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x073C] = 0x08u

            // Execute decompiled function
            screenRoutines()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 5 from frame 25
     * Function: screenRoutines (0x8567)
     * Call depth: 3
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `screenRoutines_frame25_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x073C] = 0x09u

            // Execute decompiled function
            screenRoutines()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 6 from frame 26
     * Function: screenRoutines (0x8567)
     * Call depth: 3
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `screenRoutines_frame26_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x073C] = 0x0Bu

            // Execute decompiled function
            screenRoutines()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 7 from frame 27
     * Function: screenRoutines (0x8567)
     * Call depth: 3
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `screenRoutines_frame27_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x073C] = 0x0Cu

            // Execute decompiled function
            screenRoutines()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 8 from frame 29
     * Function: screenRoutines (0x8567)
     * Call depth: 3
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `screenRoutines_frame29_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x073C] = 0x0Eu

            // Execute decompiled function
            screenRoutines()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 9 from frame 45
     * Function: screenRoutines (0x8567)
     * Call depth: 3
     * Memory reads: 4, writes: 2
     */
    @Test
    fun `screenRoutines_frame45_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x01FA] = 0x77u
            memory[0x01FB] = 0x81u
            memory[0x01FC] = 0x10u
            memory[0x073C] = 0x07u

            // Execute decompiled function
            screenRoutines()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0x858B: initScreen
    // 3 calls, 2 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 6
     * Function: initScreen (0x858B)
     * Call depth: 4
     * Memory reads: 4, writes: 3
     */
    @Test
    fun `initScreen_frame6_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x01F8] = 0x8Du
            memory[0x01F9] = 0x85u
            memory[0x073C] = 0x00u
            memory[0x0770] = 0x00u

            // Execute decompiled function
            initScreen()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x01u, memory[0x073C], "Memory 0x073C mismatch")
        }
    }

    /**
     * Test case 1 from frame 38
     * Function: initScreen (0x858B)
     * Call depth: 4
     * Memory reads: 3, writes: 2
     */
    @Test
    fun `initScreen_frame38_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01F8] = 0x8Du
            memory[0x01F9] = 0x85u
            memory[0x0770] = 0x01u

            // Execute decompiled function
            initScreen()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0x859B: setupIntermediate
    // 3 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 7
     * Function: setupIntermediate (0x859B)
     * Call depth: 4
     * Memory reads: 2, writes: 6
     */
    @Test
    fun `setupIntermediate_frame7_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0744] = 0x00u
            memory[0x0756] = 0x00u

            // Execute decompiled function
            setupIntermediate()

            // Verify: Check output memory (2 addresses)
            assertEquals(0x02u, memory[0x0744], "Memory 0x0744 mismatch")
            assertEquals(0x00u, memory[0x0756], "Memory 0x0756 mismatch")
        }
    }

    // =========================================
    // 0x85BF: getAreaPalette
    // 3 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 25
     * Function: getAreaPalette (0x85BF)
     * Call depth: 4
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `getAreaPalette_frame25_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x074E] = 0x01u

            // Execute decompiled function
            getAreaPalette()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0x85C5: setvramaddrA
    // Parameters: X
    // 5 calls, 2 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 25
     * Function: setvramaddrA (0x85C5)
     * Parameters: X
     * Call depth: 4
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `setvramaddrA_frame25_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x073C] = 0x09u

            // Execute decompiled function
            setvramaddrA(0x02)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x0Au, memory[0x073C], "Memory 0x073C mismatch")
            assertEquals(0x02u, memory[0x0773], "Memory 0x0773 mismatch")
        }
    }

    /**
     * Test case 1 from frame 39
     * Function: setvramaddrA (0x85C5)
     * Parameters: X
     * Call depth: 4
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `setvramaddrA_frame39_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x073C] = 0x00u

            // Execute decompiled function
            setvramaddrA(0x03)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x01u, memory[0x073C], "Memory 0x073C mismatch")
            assertEquals(0x03u, memory[0x0773], "Memory 0x0773 mismatch")
        }
    }

    // =========================================
    // 0x85E3: getBackgroundColor
    // 3 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 26
     * Function: getBackgroundColor (0x85E3)
     * Call depth: 4
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `getBackgroundColor_frame26_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x073C] = 0x0Au
            memory[0x0744] = 0x00u

            // Execute decompiled function
            getBackgroundColor()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x0Bu, memory[0x073C], "Memory 0x073C mismatch")
        }
    }

    // =========================================
    // 0x85F1: getPlayerColors
    // 9 calls, 4 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 7
     * Function: getPlayerColors (0x85F1)
     * Call depth: 5
     * Memory reads: 5, writes: 9
     */
    @Test
    fun `getPlayerColors_frame7_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0x03u
            memory[0x0300] = 0x00u
            memory[0x0744] = 0x02u
            memory[0x0753] = 0x00u
            memory[0x0756] = 0x00u

            // Execute decompiled function
            getPlayerColors()

            // Verify: Check output memory (9 addresses)
            assertEquals(0xFFu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x3Fu, memory[0x0301], "Memory 0x0301 mismatch")
            assertEquals(0x10u, memory[0x0302], "Memory 0x0302 mismatch")
            assertEquals(0x04u, memory[0x0303], "Memory 0x0303 mismatch")
            assertEquals(0x0Fu, memory[0x0304], "Memory 0x0304 mismatch")
            assertEquals(0x16u, memory[0x0305], "Memory 0x0305 mismatch")
            assertEquals(0x27u, memory[0x0306], "Memory 0x0306 mismatch")
            assertEquals(0x18u, memory[0x0307], "Memory 0x0307 mismatch")
            assertEquals(0x00u, memory[0x0308], "Memory 0x0308 mismatch")
        }
    }

    /**
     * Test case 1 from frame 26
     * Function: getPlayerColors (0x85F1)
     * Call depth: 4
     * Memory reads: 6, writes: 9
     */
    @Test
    fun `getPlayerColors_frame26_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (6 addresses)
            memory[0x0000] = 0x03u
            memory[0x0300] = 0x00u
            memory[0x0744] = 0x00u
            memory[0x074E] = 0x01u
            memory[0x0753] = 0x00u
            memory[0x0756] = 0x00u

            // Execute decompiled function
            getPlayerColors()

            // Verify: Check output memory (9 addresses)
            assertEquals(0xFFu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x3Fu, memory[0x0301], "Memory 0x0301 mismatch")
            assertEquals(0x10u, memory[0x0302], "Memory 0x0302 mismatch")
            assertEquals(0x04u, memory[0x0303], "Memory 0x0303 mismatch")
            assertEquals(0x22u, memory[0x0304], "Memory 0x0304 mismatch")
            assertEquals(0x16u, memory[0x0305], "Memory 0x0305 mismatch")
            assertEquals(0x27u, memory[0x0306], "Memory 0x0306 mismatch")
            assertEquals(0x18u, memory[0x0307], "Memory 0x0307 mismatch")
            assertEquals(0x00u, memory[0x0308], "Memory 0x0308 mismatch")
        }
    }

    /**
     * Test case 2 from frame 32
     * Function: getPlayerColors (0x85F1)
     * Call depth: 7
     * Memory reads: 6, writes: 9
     */
    @Test
    fun `getPlayerColors_frame32_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (6 addresses)
            memory[0x0000] = 0x03u
            memory[0x0300] = 0x00u
            memory[0x0744] = 0x00u
            memory[0x074E] = 0x01u
            memory[0x0753] = 0x00u
            memory[0x0756] = 0x00u

            // Execute decompiled function
            getPlayerColors()

            // Verify: Check output memory (9 addresses)
            assertEquals(0xFFu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x3Fu, memory[0x0301], "Memory 0x0301 mismatch")
            assertEquals(0x10u, memory[0x0302], "Memory 0x0302 mismatch")
            assertEquals(0x04u, memory[0x0303], "Memory 0x0303 mismatch")
            assertEquals(0x22u, memory[0x0304], "Memory 0x0304 mismatch")
            assertEquals(0x16u, memory[0x0305], "Memory 0x0305 mismatch")
            assertEquals(0x27u, memory[0x0306], "Memory 0x0306 mismatch")
            assertEquals(0x18u, memory[0x0307], "Memory 0x0307 mismatch")
            assertEquals(0x00u, memory[0x0308], "Memory 0x0308 mismatch")
        }
    }

    /**
     * Test case 3 from frame 196
     * Function: getPlayerColors (0x85F1)
     * Call depth: 6
     * Memory reads: 6, writes: 9
     */
    @Test
    fun `getPlayerColors_frame196_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (6 addresses)
            memory[0x0000] = 0x03u
            memory[0x0300] = 0x00u
            memory[0x0744] = 0x00u
            memory[0x074E] = 0x01u
            memory[0x0753] = 0x00u
            memory[0x0756] = 0x00u

            // Execute decompiled function
            getPlayerColors()

            // Verify: Check output memory (9 addresses)
            assertEquals(0xFFu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x3Fu, memory[0x0301], "Memory 0x0301 mismatch")
            assertEquals(0x10u, memory[0x0302], "Memory 0x0302 mismatch")
            assertEquals(0x04u, memory[0x0303], "Memory 0x0303 mismatch")
            assertEquals(0x22u, memory[0x0304], "Memory 0x0304 mismatch")
            assertEquals(0x16u, memory[0x0305], "Memory 0x0305 mismatch")
            assertEquals(0x27u, memory[0x0306], "Memory 0x0306 mismatch")
            assertEquals(0x18u, memory[0x0307], "Memory 0x0307 mismatch")
            assertEquals(0x00u, memory[0x0308], "Memory 0x0308 mismatch")
        }
    }

    // =========================================
    // 0x863F: setVRAMOffset
    // Parameters: A
    // 15 calls, 8 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 7
     * Function: setVRAMOffset (0x863F)
     * Parameters: A
     * Call depth: 5
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `setVRAMOffset_frame7_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            setVRAMOffset(0x07)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x07u, memory[0x0300], "Memory 0x0300 mismatch")
        }
    }

    /**
     * Test case 1 from frame 26
     * Function: setVRAMOffset (0x863F)
     * Parameters: A
     * Call depth: 4
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `setVRAMOffset_frame26_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            setVRAMOffset(0x07)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x07u, memory[0x0300], "Memory 0x0300 mismatch")
        }
    }

    /**
     * Test case 2 from frame 32
     * Function: setVRAMOffset (0x863F)
     * Parameters: A
     * Call depth: 7
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `setVRAMOffset_frame32_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            setVRAMOffset(0x07)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x07u, memory[0x0300], "Memory 0x0300 mismatch")
        }
    }

    /**
     * Test case 3 from frame 196
     * Function: setVRAMOffset (0x863F)
     * Parameters: A
     * Call depth: 6
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `setVRAMOffset_frame196_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            setVRAMOffset(0x07)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x07u, memory[0x0300], "Memory 0x0300 mismatch")
        }
    }

    /**
     * Test case 4 from frame 4374
     * Function: setVRAMOffset (0x863F)
     * Parameters: A
     * Call depth: 9
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `setVRAMOffset_frame4374_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            setVRAMOffset(0x0A)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x0Au, memory[0x0300], "Memory 0x0300 mismatch")
        }
    }

    /**
     * Test case 5 from frame 4403
     * Function: setVRAMOffset (0x863F)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `setVRAMOffset_frame4403_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            setVRAMOffset(0x0A)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x0Au, memory[0x0300], "Memory 0x0300 mismatch")
        }
    }

    /**
     * Test case 6 from frame 4550
     * Function: setVRAMOffset (0x863F)
     * Parameters: A
     * Call depth: 9
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `setVRAMOffset_frame4550_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            setVRAMOffset(0x0A)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x0Au, memory[0x0300], "Memory 0x0300 mismatch")
        }
    }

    /**
     * Test case 7 from frame 4579
     * Function: setVRAMOffset (0x863F)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `setVRAMOffset_frame4579_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            setVRAMOffset(0x0A)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x0Au, memory[0x0300], "Memory 0x0300 mismatch")
        }
    }

    // =========================================
    // 0x8643: getAlternatePalette1
    // 3 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 26
     * Function: getAlternatePalette1 (0x8643)
     * Call depth: 4
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `getAlternatePalette1_frame26_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0733] = 0x00u
            memory[0x073C] = 0x0Bu

            // Execute decompiled function
            getAlternatePalette1()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x0Cu, memory[0x073C], "Memory 0x073C mismatch")
        }
    }

    // =========================================
    // 0x864C: setvramaddrB
    // Parameters: A
    // 1 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 28
     * Function: setvramaddrB (0x864C)
     * Parameters: A
     * Call depth: 4
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `setvramaddrB_frame28_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x073C] = 0x0Cu

            // Execute decompiled function
            setvramaddrB(0x05)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x0Du, memory[0x073C], "Memory 0x073C mismatch")
            assertEquals(0x05u, memory[0x0773], "Memory 0x0773 mismatch")
        }
    }

    // =========================================
    // 0x8652: writeTopStatusLine
    // 3 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 8
     * Function: writeTopStatusLine (0x8652)
     * Call depth: 4
     * Memory reads: 3, writes: 3
     */
    @Test
    fun `writeTopStatusLine_frame8_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01F8] = 0x56u
            memory[0x01F9] = 0x86u
            memory[0x073C] = 0x02u

            // Execute decompiled function
            writeTopStatusLine()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x03u, memory[0x073C], "Memory 0x073C mismatch")
        }
    }

    // =========================================
    // 0x865A: writeBottomStatusLine
    // 3 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 10
     * Function: writeBottomStatusLine (0x865A)
     * Call depth: 4
     * Memory reads: 6, writes: 11
     */
    @Test
    fun `writeBottomStatusLine_frame10_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (6 addresses)
            memory[0x01F8] = 0x5Cu
            memory[0x01F9] = 0x86u
            memory[0x0300] = 0x0Eu
            memory[0x073C] = 0x03u
            memory[0x075C] = 0x00u
            memory[0x075F] = 0x00u

            // Execute decompiled function
            writeBottomStatusLine()

            // Verify: Check output memory (9 addresses)
            assertEquals(0x14u, memory[0x0300], "Memory 0x0300 mismatch")
            assertEquals(0x20u, memory[0x030F], "Memory 0x030F mismatch")
            assertEquals(0x73u, memory[0x0310], "Memory 0x0310 mismatch")
            assertEquals(0x03u, memory[0x0311], "Memory 0x0311 mismatch")
            assertEquals(0x01u, memory[0x0312], "Memory 0x0312 mismatch")
            assertEquals(0x28u, memory[0x0313], "Memory 0x0313 mismatch")
            assertEquals(0x01u, memory[0x0314], "Memory 0x0314 mismatch")
            assertEquals(0x00u, memory[0x0315], "Memory 0x0315 mismatch")
            assertEquals(0x04u, memory[0x073C], "Memory 0x073C mismatch")
        }
    }

    // =========================================
    // 0x8693: displayTimeUp
    // 3 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 10
     * Function: displayTimeUp (0x8693)
     * Call depth: 4
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `displayTimeUp_frame10_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x073C] = 0x04u
            memory[0x0759] = 0x00u

            // Execute decompiled function
            displayTimeUp()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x06u, memory[0x073C], "Memory 0x073C mismatch")
        }
    }

    // =========================================
    // 0x86A8: displayIntermediate
    // 3 calls, 2 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 11
     * Function: displayIntermediate (0x86A8)
     * Call depth: 4
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `displayIntermediate_frame11_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0770] = 0x00u

            // Execute decompiled function
            displayIntermediate()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x08u, memory[0x073C], "Memory 0x073C mismatch")
        }
    }

    /**
     * Test case 1 from frame 43
     * Function: displayIntermediate (0x86A8)
     * Call depth: 4
     * Memory reads: 6, writes: 2
     */
    @Test
    fun `displayIntermediate_frame43_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (6 addresses)
            memory[0x01F8] = 0xC4u
            memory[0x01F9] = 0x86u
            memory[0x074E] = 0x01u
            memory[0x0752] = 0x00u
            memory[0x0769] = 0x00u
            memory[0x0770] = 0x01u

            // Execute decompiled function
            displayIntermediate()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0x86C7: outputInter
    // 2 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 44
     * Function: outputInter (0x86C7)
     * Call depth: 4
     * Memory reads: 2, writes: 3
     */
    @Test
    fun `outputInter_frame44_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x01F8] = 0xC9u
            memory[0x01F9] = 0x86u

            // Execute decompiled function
            outputInter()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0774], "Memory 0x0774 mismatch")
        }
    }

    // =========================================
    // 0x86E6: areaParserTaskControl
    // 36 calls, 13 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 12
     * Function: areaParserTaskControl (0x86E6)
     * Call depth: 4
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `areaParserTaskControl_frame12_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0774] = 0x01u

            // Execute decompiled function
            areaParserTaskControl()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x02u, memory[0x0774], "Memory 0x0774 mismatch")
        }
    }

    /**
     * Test case 1 from frame 14
     * Function: areaParserTaskControl (0x86E6)
     * Call depth: 4
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `areaParserTaskControl_frame14_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0774] = 0x02u

            // Execute decompiled function
            areaParserTaskControl()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x03u, memory[0x0774], "Memory 0x0774 mismatch")
        }
    }

    /**
     * Test case 2 from frame 15
     * Function: areaParserTaskControl (0x86E6)
     * Call depth: 4
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `areaParserTaskControl_frame15_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0774] = 0x03u

            // Execute decompiled function
            areaParserTaskControl()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x04u, memory[0x0774], "Memory 0x0774 mismatch")
        }
    }

    /**
     * Test case 3 from frame 16
     * Function: areaParserTaskControl (0x86E6)
     * Call depth: 4
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `areaParserTaskControl_frame16_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0774] = 0x04u

            // Execute decompiled function
            areaParserTaskControl()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x05u, memory[0x0774], "Memory 0x0774 mismatch")
        }
    }

    /**
     * Test case 4 from frame 18
     * Function: areaParserTaskControl (0x86E6)
     * Call depth: 4
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `areaParserTaskControl_frame18_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0774] = 0x06u

            // Execute decompiled function
            areaParserTaskControl()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x07u, memory[0x0774], "Memory 0x0774 mismatch")
        }
    }

    /**
     * Test case 5 from frame 19
     * Function: areaParserTaskControl (0x86E6)
     * Call depth: 4
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `areaParserTaskControl_frame19_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0774] = 0x07u

            // Execute decompiled function
            areaParserTaskControl()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x08u, memory[0x0774], "Memory 0x0774 mismatch")
        }
    }

    /**
     * Test case 6 from frame 20
     * Function: areaParserTaskControl (0x86E6)
     * Call depth: 4
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `areaParserTaskControl_frame20_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0774] = 0x08u

            // Execute decompiled function
            areaParserTaskControl()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x09u, memory[0x0774], "Memory 0x0774 mismatch")
        }
    }

    /**
     * Test case 7 from frame 22
     * Function: areaParserTaskControl (0x86E6)
     * Call depth: 4
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `areaParserTaskControl_frame22_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0774] = 0x0Au

            // Execute decompiled function
            areaParserTaskControl()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x0Bu, memory[0x0774], "Memory 0x0774 mismatch")
        }
    }

    /**
     * Test case 8 from frame 23
     * Function: areaParserTaskControl (0x86E6)
     * Call depth: 4
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `areaParserTaskControl_frame23_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0774] = 0x0Bu

            // Execute decompiled function
            areaParserTaskControl()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x0Cu, memory[0x0774], "Memory 0x0774 mismatch")
        }
    }

    /**
     * Test case 9 from frame 24
     * Function: areaParserTaskControl (0x86E6)
     * Call depth: 4
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `areaParserTaskControl_frame24_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0774] = 0x0Cu

            // Execute decompiled function
            areaParserTaskControl()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x0Du, memory[0x0774], "Memory 0x0774 mismatch")
        }
    }

    // =========================================
    // 0x86FF: drawTitleScreen
    // 3 calls, 2 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 27
     * Function: drawTitleScreen (0x86FF)
     * Call depth: 4
     * Memory reads: 3, writes: 316
     */
    @Test
    fun `drawTitleScreen_frame27_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x0000] = 0x00u
            memory[0x0001] = 0x03u
            memory[0x0770] = 0x00u

            // Execute decompiled function
            drawTitleScreen()

            // Verify: Check output memory (316 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x04u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x00u, memory[0x0300], "Memory 0x0300 mismatch")
            assertEquals(0x00u, memory[0x0301], "Memory 0x0301 mismatch")
            assertEquals(0x00u, memory[0x0302], "Memory 0x0302 mismatch")
            assertEquals(0x00u, memory[0x0303], "Memory 0x0303 mismatch")
            assertEquals(0x00u, memory[0x0304], "Memory 0x0304 mismatch")
            assertEquals(0x00u, memory[0x0305], "Memory 0x0305 mismatch")
            assertEquals(0x00u, memory[0x0306], "Memory 0x0306 mismatch")
            assertEquals(0x00u, memory[0x0307], "Memory 0x0307 mismatch")
            assertEquals(0x00u, memory[0x0308], "Memory 0x0308 mismatch")
            assertEquals(0x00u, memory[0x0309], "Memory 0x0309 mismatch")
            assertEquals(0x00u, memory[0x030A], "Memory 0x030A mismatch")
            assertEquals(0x00u, memory[0x030B], "Memory 0x030B mismatch")
            assertEquals(0x00u, memory[0x030C], "Memory 0x030C mismatch")
            assertEquals(0x00u, memory[0x030D], "Memory 0x030D mismatch")
            assertEquals(0x00u, memory[0x030E], "Memory 0x030E mismatch")
            assertEquals(0x00u, memory[0x030F], "Memory 0x030F mismatch")
            assertEquals(0x00u, memory[0x0310], "Memory 0x0310 mismatch")
            assertEquals(0x00u, memory[0x0311], "Memory 0x0311 mismatch")
            assertEquals(0x00u, memory[0x0312], "Memory 0x0312 mismatch")
            assertEquals(0x00u, memory[0x0313], "Memory 0x0313 mismatch")
            assertEquals(0x00u, memory[0x0314], "Memory 0x0314 mismatch")
            assertEquals(0x00u, memory[0x0315], "Memory 0x0315 mismatch")
            assertEquals(0x00u, memory[0x0316], "Memory 0x0316 mismatch")
            assertEquals(0x00u, memory[0x0317], "Memory 0x0317 mismatch")
            assertEquals(0x00u, memory[0x0318], "Memory 0x0318 mismatch")
            assertEquals(0x00u, memory[0x0319], "Memory 0x0319 mismatch")
            assertEquals(0x00u, memory[0x031A], "Memory 0x031A mismatch")
            assertEquals(0x00u, memory[0x031B], "Memory 0x031B mismatch")
            assertEquals(0x00u, memory[0x031C], "Memory 0x031C mismatch")
            assertEquals(0x00u, memory[0x031D], "Memory 0x031D mismatch")
            assertEquals(0x00u, memory[0x031E], "Memory 0x031E mismatch")
            assertEquals(0x00u, memory[0x031F], "Memory 0x031F mismatch")
            assertEquals(0x00u, memory[0x0320], "Memory 0x0320 mismatch")
            assertEquals(0x00u, memory[0x0321], "Memory 0x0321 mismatch")
            assertEquals(0x00u, memory[0x0322], "Memory 0x0322 mismatch")
            assertEquals(0x00u, memory[0x0323], "Memory 0x0323 mismatch")
            assertEquals(0x00u, memory[0x0324], "Memory 0x0324 mismatch")
            assertEquals(0x00u, memory[0x0325], "Memory 0x0325 mismatch")
            assertEquals(0x00u, memory[0x0326], "Memory 0x0326 mismatch")
            assertEquals(0x00u, memory[0x0327], "Memory 0x0327 mismatch")
            assertEquals(0x00u, memory[0x0328], "Memory 0x0328 mismatch")
            assertEquals(0x00u, memory[0x0329], "Memory 0x0329 mismatch")
            assertEquals(0x00u, memory[0x032A], "Memory 0x032A mismatch")
            assertEquals(0x00u, memory[0x032B], "Memory 0x032B mismatch")
            assertEquals(0x00u, memory[0x032C], "Memory 0x032C mismatch")
            assertEquals(0x00u, memory[0x032D], "Memory 0x032D mismatch")
            assertEquals(0x00u, memory[0x032E], "Memory 0x032E mismatch")
            assertEquals(0x00u, memory[0x032F], "Memory 0x032F mismatch")
            assertEquals(0x00u, memory[0x0330], "Memory 0x0330 mismatch")
            assertEquals(0x00u, memory[0x0331], "Memory 0x0331 mismatch")
            assertEquals(0x00u, memory[0x0332], "Memory 0x0332 mismatch")
            assertEquals(0x00u, memory[0x0333], "Memory 0x0333 mismatch")
            assertEquals(0x00u, memory[0x0334], "Memory 0x0334 mismatch")
            assertEquals(0x00u, memory[0x0335], "Memory 0x0335 mismatch")
            assertEquals(0x00u, memory[0x0336], "Memory 0x0336 mismatch")
            assertEquals(0x00u, memory[0x0337], "Memory 0x0337 mismatch")
            assertEquals(0x00u, memory[0x0338], "Memory 0x0338 mismatch")
            assertEquals(0x00u, memory[0x0339], "Memory 0x0339 mismatch")
            assertEquals(0x00u, memory[0x033A], "Memory 0x033A mismatch")
            assertEquals(0x00u, memory[0x033B], "Memory 0x033B mismatch")
            assertEquals(0x00u, memory[0x033C], "Memory 0x033C mismatch")
            assertEquals(0x00u, memory[0x033D], "Memory 0x033D mismatch")
            assertEquals(0x00u, memory[0x033E], "Memory 0x033E mismatch")
            assertEquals(0x00u, memory[0x033F], "Memory 0x033F mismatch")
            assertEquals(0x00u, memory[0x0340], "Memory 0x0340 mismatch")
            assertEquals(0x00u, memory[0x0341], "Memory 0x0341 mismatch")
            assertEquals(0x00u, memory[0x0342], "Memory 0x0342 mismatch")
            assertEquals(0x00u, memory[0x0343], "Memory 0x0343 mismatch")
            assertEquals(0x00u, memory[0x0344], "Memory 0x0344 mismatch")
            assertEquals(0x00u, memory[0x0345], "Memory 0x0345 mismatch")
            assertEquals(0x00u, memory[0x0346], "Memory 0x0346 mismatch")
            assertEquals(0x00u, memory[0x0347], "Memory 0x0347 mismatch")
            assertEquals(0x00u, memory[0x0348], "Memory 0x0348 mismatch")
            assertEquals(0x00u, memory[0x0349], "Memory 0x0349 mismatch")
            assertEquals(0x00u, memory[0x034A], "Memory 0x034A mismatch")
            assertEquals(0x00u, memory[0x034B], "Memory 0x034B mismatch")
            assertEquals(0x00u, memory[0x034C], "Memory 0x034C mismatch")
            assertEquals(0x00u, memory[0x034D], "Memory 0x034D mismatch")
            assertEquals(0x00u, memory[0x034E], "Memory 0x034E mismatch")
            assertEquals(0x00u, memory[0x034F], "Memory 0x034F mismatch")
            assertEquals(0x00u, memory[0x0350], "Memory 0x0350 mismatch")
            assertEquals(0x00u, memory[0x0351], "Memory 0x0351 mismatch")
            assertEquals(0x00u, memory[0x0352], "Memory 0x0352 mismatch")
            assertEquals(0x00u, memory[0x0353], "Memory 0x0353 mismatch")
            assertEquals(0x00u, memory[0x0354], "Memory 0x0354 mismatch")
            assertEquals(0x00u, memory[0x0355], "Memory 0x0355 mismatch")
            assertEquals(0x00u, memory[0x0356], "Memory 0x0356 mismatch")
            assertEquals(0x00u, memory[0x0357], "Memory 0x0357 mismatch")
            assertEquals(0x00u, memory[0x0358], "Memory 0x0358 mismatch")
            assertEquals(0x00u, memory[0x0359], "Memory 0x0359 mismatch")
            assertEquals(0x00u, memory[0x035A], "Memory 0x035A mismatch")
            assertEquals(0x00u, memory[0x035B], "Memory 0x035B mismatch")
            assertEquals(0x00u, memory[0x035C], "Memory 0x035C mismatch")
            assertEquals(0x00u, memory[0x035D], "Memory 0x035D mismatch")
            assertEquals(0x00u, memory[0x035E], "Memory 0x035E mismatch")
            assertEquals(0x00u, memory[0x035F], "Memory 0x035F mismatch")
            assertEquals(0x00u, memory[0x0360], "Memory 0x0360 mismatch")
            assertEquals(0x00u, memory[0x0361], "Memory 0x0361 mismatch")
            assertEquals(0x00u, memory[0x0362], "Memory 0x0362 mismatch")
            assertEquals(0x00u, memory[0x0363], "Memory 0x0363 mismatch")
            assertEquals(0x00u, memory[0x0364], "Memory 0x0364 mismatch")
            assertEquals(0x00u, memory[0x0365], "Memory 0x0365 mismatch")
            assertEquals(0x00u, memory[0x0366], "Memory 0x0366 mismatch")
            assertEquals(0x00u, memory[0x0367], "Memory 0x0367 mismatch")
            assertEquals(0x00u, memory[0x0368], "Memory 0x0368 mismatch")
            assertEquals(0x00u, memory[0x0369], "Memory 0x0369 mismatch")
            assertEquals(0x00u, memory[0x036A], "Memory 0x036A mismatch")
            assertEquals(0x00u, memory[0x036B], "Memory 0x036B mismatch")
            assertEquals(0x00u, memory[0x036C], "Memory 0x036C mismatch")
            assertEquals(0x00u, memory[0x036D], "Memory 0x036D mismatch")
            assertEquals(0x00u, memory[0x036E], "Memory 0x036E mismatch")
            assertEquals(0x00u, memory[0x036F], "Memory 0x036F mismatch")
            assertEquals(0x00u, memory[0x0370], "Memory 0x0370 mismatch")
            assertEquals(0x00u, memory[0x0371], "Memory 0x0371 mismatch")
            assertEquals(0x00u, memory[0x0372], "Memory 0x0372 mismatch")
            assertEquals(0x00u, memory[0x0373], "Memory 0x0373 mismatch")
            assertEquals(0x00u, memory[0x0374], "Memory 0x0374 mismatch")
            assertEquals(0x00u, memory[0x0375], "Memory 0x0375 mismatch")
            assertEquals(0x00u, memory[0x0376], "Memory 0x0376 mismatch")
            assertEquals(0x00u, memory[0x0377], "Memory 0x0377 mismatch")
            assertEquals(0x00u, memory[0x0378], "Memory 0x0378 mismatch")
            assertEquals(0x00u, memory[0x0379], "Memory 0x0379 mismatch")
            assertEquals(0x00u, memory[0x037A], "Memory 0x037A mismatch")
            assertEquals(0x00u, memory[0x037B], "Memory 0x037B mismatch")
            assertEquals(0x00u, memory[0x037C], "Memory 0x037C mismatch")
            assertEquals(0x00u, memory[0x037D], "Memory 0x037D mismatch")
            assertEquals(0x00u, memory[0x037E], "Memory 0x037E mismatch")
            assertEquals(0x00u, memory[0x037F], "Memory 0x037F mismatch")
            assertEquals(0x00u, memory[0x0380], "Memory 0x0380 mismatch")
            assertEquals(0x00u, memory[0x0381], "Memory 0x0381 mismatch")
            assertEquals(0x00u, memory[0x0382], "Memory 0x0382 mismatch")
            assertEquals(0x00u, memory[0x0383], "Memory 0x0383 mismatch")
            assertEquals(0x00u, memory[0x0384], "Memory 0x0384 mismatch")
            assertEquals(0x00u, memory[0x0385], "Memory 0x0385 mismatch")
            assertEquals(0x00u, memory[0x0386], "Memory 0x0386 mismatch")
            assertEquals(0x00u, memory[0x0387], "Memory 0x0387 mismatch")
            assertEquals(0x00u, memory[0x0388], "Memory 0x0388 mismatch")
            assertEquals(0x00u, memory[0x0389], "Memory 0x0389 mismatch")
            assertEquals(0x00u, memory[0x038A], "Memory 0x038A mismatch")
            assertEquals(0x00u, memory[0x038B], "Memory 0x038B mismatch")
            assertEquals(0x00u, memory[0x038C], "Memory 0x038C mismatch")
            assertEquals(0x00u, memory[0x038D], "Memory 0x038D mismatch")
            assertEquals(0x00u, memory[0x038E], "Memory 0x038E mismatch")
            assertEquals(0x00u, memory[0x038F], "Memory 0x038F mismatch")
            assertEquals(0x00u, memory[0x0390], "Memory 0x0390 mismatch")
            assertEquals(0x00u, memory[0x0391], "Memory 0x0391 mismatch")
            assertEquals(0x00u, memory[0x0392], "Memory 0x0392 mismatch")
            assertEquals(0x00u, memory[0x0393], "Memory 0x0393 mismatch")
            assertEquals(0x00u, memory[0x0394], "Memory 0x0394 mismatch")
            assertEquals(0x00u, memory[0x0395], "Memory 0x0395 mismatch")
            assertEquals(0x00u, memory[0x0396], "Memory 0x0396 mismatch")
            assertEquals(0x00u, memory[0x0397], "Memory 0x0397 mismatch")
            assertEquals(0x00u, memory[0x0398], "Memory 0x0398 mismatch")
            assertEquals(0x00u, memory[0x0399], "Memory 0x0399 mismatch")
            assertEquals(0x00u, memory[0x039A], "Memory 0x039A mismatch")
            assertEquals(0x00u, memory[0x039B], "Memory 0x039B mismatch")
            assertEquals(0x00u, memory[0x039C], "Memory 0x039C mismatch")
            assertEquals(0x00u, memory[0x039D], "Memory 0x039D mismatch")
            assertEquals(0x00u, memory[0x039E], "Memory 0x039E mismatch")
            assertEquals(0x00u, memory[0x039F], "Memory 0x039F mismatch")
            assertEquals(0x00u, memory[0x03A0], "Memory 0x03A0 mismatch")
            assertEquals(0x00u, memory[0x03A1], "Memory 0x03A1 mismatch")
            assertEquals(0x00u, memory[0x03A2], "Memory 0x03A2 mismatch")
            assertEquals(0x00u, memory[0x03A3], "Memory 0x03A3 mismatch")
            assertEquals(0x00u, memory[0x03A4], "Memory 0x03A4 mismatch")
            assertEquals(0x00u, memory[0x03A5], "Memory 0x03A5 mismatch")
            assertEquals(0x00u, memory[0x03A6], "Memory 0x03A6 mismatch")
            assertEquals(0x00u, memory[0x03A7], "Memory 0x03A7 mismatch")
            assertEquals(0x00u, memory[0x03A8], "Memory 0x03A8 mismatch")
            assertEquals(0x00u, memory[0x03A9], "Memory 0x03A9 mismatch")
            assertEquals(0x00u, memory[0x03AA], "Memory 0x03AA mismatch")
            assertEquals(0x00u, memory[0x03AB], "Memory 0x03AB mismatch")
            assertEquals(0x00u, memory[0x03AC], "Memory 0x03AC mismatch")
            assertEquals(0x00u, memory[0x03AD], "Memory 0x03AD mismatch")
            assertEquals(0x00u, memory[0x03AE], "Memory 0x03AE mismatch")
            assertEquals(0x00u, memory[0x03AF], "Memory 0x03AF mismatch")
            assertEquals(0x00u, memory[0x03B0], "Memory 0x03B0 mismatch")
            assertEquals(0x00u, memory[0x03B1], "Memory 0x03B1 mismatch")
            assertEquals(0x00u, memory[0x03B2], "Memory 0x03B2 mismatch")
            assertEquals(0x00u, memory[0x03B3], "Memory 0x03B3 mismatch")
            assertEquals(0x00u, memory[0x03B4], "Memory 0x03B4 mismatch")
            assertEquals(0x00u, memory[0x03B5], "Memory 0x03B5 mismatch")
            assertEquals(0x00u, memory[0x03B6], "Memory 0x03B6 mismatch")
            assertEquals(0x00u, memory[0x03B7], "Memory 0x03B7 mismatch")
            assertEquals(0x00u, memory[0x03B8], "Memory 0x03B8 mismatch")
            assertEquals(0x00u, memory[0x03B9], "Memory 0x03B9 mismatch")
            assertEquals(0x00u, memory[0x03BA], "Memory 0x03BA mismatch")
            assertEquals(0x00u, memory[0x03BB], "Memory 0x03BB mismatch")
            assertEquals(0x00u, memory[0x03BC], "Memory 0x03BC mismatch")
            assertEquals(0x00u, memory[0x03BD], "Memory 0x03BD mismatch")
            assertEquals(0x00u, memory[0x03BE], "Memory 0x03BE mismatch")
            assertEquals(0x00u, memory[0x03BF], "Memory 0x03BF mismatch")
            assertEquals(0x00u, memory[0x03C0], "Memory 0x03C0 mismatch")
            assertEquals(0x00u, memory[0x03C1], "Memory 0x03C1 mismatch")
            assertEquals(0x00u, memory[0x03C2], "Memory 0x03C2 mismatch")
            assertEquals(0x00u, memory[0x03C3], "Memory 0x03C3 mismatch")
            assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
            assertEquals(0x00u, memory[0x03C5], "Memory 0x03C5 mismatch")
            assertEquals(0x00u, memory[0x03C6], "Memory 0x03C6 mismatch")
            assertEquals(0x00u, memory[0x03C7], "Memory 0x03C7 mismatch")
            assertEquals(0x00u, memory[0x03C8], "Memory 0x03C8 mismatch")
            assertEquals(0x00u, memory[0x03C9], "Memory 0x03C9 mismatch")
            assertEquals(0x00u, memory[0x03CA], "Memory 0x03CA mismatch")
            assertEquals(0x00u, memory[0x03CB], "Memory 0x03CB mismatch")
            assertEquals(0x00u, memory[0x03CC], "Memory 0x03CC mismatch")
            assertEquals(0x00u, memory[0x03CD], "Memory 0x03CD mismatch")
            assertEquals(0x00u, memory[0x03CE], "Memory 0x03CE mismatch")
            assertEquals(0x00u, memory[0x03CF], "Memory 0x03CF mismatch")
            assertEquals(0x00u, memory[0x03D0], "Memory 0x03D0 mismatch")
            assertEquals(0x00u, memory[0x03D1], "Memory 0x03D1 mismatch")
            assertEquals(0x00u, memory[0x03D2], "Memory 0x03D2 mismatch")
            assertEquals(0x00u, memory[0x03D3], "Memory 0x03D3 mismatch")
            assertEquals(0x00u, memory[0x03D4], "Memory 0x03D4 mismatch")
            assertEquals(0x00u, memory[0x03D5], "Memory 0x03D5 mismatch")
            assertEquals(0x00u, memory[0x03D6], "Memory 0x03D6 mismatch")
            assertEquals(0x00u, memory[0x03D7], "Memory 0x03D7 mismatch")
            assertEquals(0x00u, memory[0x03D8], "Memory 0x03D8 mismatch")
            assertEquals(0x00u, memory[0x03D9], "Memory 0x03D9 mismatch")
            assertEquals(0x00u, memory[0x03DA], "Memory 0x03DA mismatch")
            assertEquals(0x00u, memory[0x03DB], "Memory 0x03DB mismatch")
            assertEquals(0x00u, memory[0x03DC], "Memory 0x03DC mismatch")
            assertEquals(0x00u, memory[0x03DD], "Memory 0x03DD mismatch")
            assertEquals(0x00u, memory[0x03DE], "Memory 0x03DE mismatch")
            assertEquals(0x00u, memory[0x03DF], "Memory 0x03DF mismatch")
            assertEquals(0x00u, memory[0x03E0], "Memory 0x03E0 mismatch")
            assertEquals(0x00u, memory[0x03E1], "Memory 0x03E1 mismatch")
            assertEquals(0x00u, memory[0x03E2], "Memory 0x03E2 mismatch")
            assertEquals(0x00u, memory[0x03E3], "Memory 0x03E3 mismatch")
            assertEquals(0x00u, memory[0x03E4], "Memory 0x03E4 mismatch")
            assertEquals(0x00u, memory[0x03E5], "Memory 0x03E5 mismatch")
            assertEquals(0x00u, memory[0x03E6], "Memory 0x03E6 mismatch")
            assertEquals(0x00u, memory[0x03E7], "Memory 0x03E7 mismatch")
            assertEquals(0x00u, memory[0x03E8], "Memory 0x03E8 mismatch")
            assertEquals(0x00u, memory[0x03E9], "Memory 0x03E9 mismatch")
            assertEquals(0x00u, memory[0x03EA], "Memory 0x03EA mismatch")
            assertEquals(0x00u, memory[0x03EB], "Memory 0x03EB mismatch")
            assertEquals(0x00u, memory[0x03EC], "Memory 0x03EC mismatch")
            assertEquals(0x00u, memory[0x03ED], "Memory 0x03ED mismatch")
            assertEquals(0x00u, memory[0x03EE], "Memory 0x03EE mismatch")
            assertEquals(0x00u, memory[0x03EF], "Memory 0x03EF mismatch")
            assertEquals(0x00u, memory[0x03F0], "Memory 0x03F0 mismatch")
            assertEquals(0x00u, memory[0x03F1], "Memory 0x03F1 mismatch")
            assertEquals(0x00u, memory[0x03F2], "Memory 0x03F2 mismatch")
            assertEquals(0x00u, memory[0x03F3], "Memory 0x03F3 mismatch")
            assertEquals(0x00u, memory[0x03F4], "Memory 0x03F4 mismatch")
            assertEquals(0x00u, memory[0x03F5], "Memory 0x03F5 mismatch")
            assertEquals(0x00u, memory[0x03F6], "Memory 0x03F6 mismatch")
            assertEquals(0x00u, memory[0x03F7], "Memory 0x03F7 mismatch")
            assertEquals(0x00u, memory[0x03F8], "Memory 0x03F8 mismatch")
            assertEquals(0x00u, memory[0x03F9], "Memory 0x03F9 mismatch")
            assertEquals(0x00u, memory[0x03FA], "Memory 0x03FA mismatch")
            assertEquals(0x00u, memory[0x03FB], "Memory 0x03FB mismatch")
            assertEquals(0x00u, memory[0x03FC], "Memory 0x03FC mismatch")
            assertEquals(0x00u, memory[0x03FD], "Memory 0x03FD mismatch")
            assertEquals(0x00u, memory[0x03FE], "Memory 0x03FE mismatch")
            assertEquals(0x00u, memory[0x03FF], "Memory 0x03FF mismatch")
            assertEquals(0x00u, memory[0x0400], "Memory 0x0400 mismatch")
            assertEquals(0x00u, memory[0x0401], "Memory 0x0401 mismatch")
            assertEquals(0x00u, memory[0x0402], "Memory 0x0402 mismatch")
            assertEquals(0x00u, memory[0x0403], "Memory 0x0403 mismatch")
            assertEquals(0x00u, memory[0x0404], "Memory 0x0404 mismatch")
            assertEquals(0x00u, memory[0x0405], "Memory 0x0405 mismatch")
            assertEquals(0x00u, memory[0x0406], "Memory 0x0406 mismatch")
            assertEquals(0x00u, memory[0x0407], "Memory 0x0407 mismatch")
            assertEquals(0x00u, memory[0x0408], "Memory 0x0408 mismatch")
            assertEquals(0x00u, memory[0x0409], "Memory 0x0409 mismatch")
            assertEquals(0x00u, memory[0x040A], "Memory 0x040A mismatch")
            assertEquals(0x00u, memory[0x040B], "Memory 0x040B mismatch")
            assertEquals(0x00u, memory[0x040C], "Memory 0x040C mismatch")
            assertEquals(0x00u, memory[0x040D], "Memory 0x040D mismatch")
            assertEquals(0x00u, memory[0x040E], "Memory 0x040E mismatch")
            assertEquals(0x00u, memory[0x040F], "Memory 0x040F mismatch")
            assertEquals(0x00u, memory[0x0410], "Memory 0x0410 mismatch")
            assertEquals(0x00u, memory[0x0411], "Memory 0x0411 mismatch")
            assertEquals(0x00u, memory[0x0412], "Memory 0x0412 mismatch")
            assertEquals(0x00u, memory[0x0413], "Memory 0x0413 mismatch")
            assertEquals(0x00u, memory[0x0414], "Memory 0x0414 mismatch")
            assertEquals(0x00u, memory[0x0415], "Memory 0x0415 mismatch")
            assertEquals(0x00u, memory[0x0416], "Memory 0x0416 mismatch")
            assertEquals(0x00u, memory[0x0417], "Memory 0x0417 mismatch")
            assertEquals(0x00u, memory[0x0418], "Memory 0x0418 mismatch")
            assertEquals(0x00u, memory[0x0419], "Memory 0x0419 mismatch")
            assertEquals(0x00u, memory[0x041A], "Memory 0x041A mismatch")
            assertEquals(0x00u, memory[0x041B], "Memory 0x041B mismatch")
            assertEquals(0x00u, memory[0x041C], "Memory 0x041C mismatch")
            assertEquals(0x00u, memory[0x041D], "Memory 0x041D mismatch")
            assertEquals(0x00u, memory[0x041E], "Memory 0x041E mismatch")
            assertEquals(0x00u, memory[0x041F], "Memory 0x041F mismatch")
            assertEquals(0x00u, memory[0x0420], "Memory 0x0420 mismatch")
            assertEquals(0x00u, memory[0x0421], "Memory 0x0421 mismatch")
            assertEquals(0x00u, memory[0x0422], "Memory 0x0422 mismatch")
            assertEquals(0x00u, memory[0x0423], "Memory 0x0423 mismatch")
            assertEquals(0x00u, memory[0x0424], "Memory 0x0424 mismatch")
            assertEquals(0x00u, memory[0x0425], "Memory 0x0425 mismatch")
            assertEquals(0x00u, memory[0x0426], "Memory 0x0426 mismatch")
            assertEquals(0x00u, memory[0x0427], "Memory 0x0427 mismatch")
            assertEquals(0x00u, memory[0x0428], "Memory 0x0428 mismatch")
            assertEquals(0x00u, memory[0x0429], "Memory 0x0429 mismatch")
            assertEquals(0x00u, memory[0x042A], "Memory 0x042A mismatch")
            assertEquals(0x00u, memory[0x042B], "Memory 0x042B mismatch")
            assertEquals(0x00u, memory[0x042C], "Memory 0x042C mismatch")
            assertEquals(0x00u, memory[0x042D], "Memory 0x042D mismatch")
            assertEquals(0x00u, memory[0x042E], "Memory 0x042E mismatch")
            assertEquals(0x00u, memory[0x042F], "Memory 0x042F mismatch")
            assertEquals(0x00u, memory[0x0430], "Memory 0x0430 mismatch")
            assertEquals(0x00u, memory[0x0431], "Memory 0x0431 mismatch")
            assertEquals(0x00u, memory[0x0432], "Memory 0x0432 mismatch")
            assertEquals(0x00u, memory[0x0433], "Memory 0x0433 mismatch")
            assertEquals(0x00u, memory[0x0434], "Memory 0x0434 mismatch")
            assertEquals(0x00u, memory[0x0435], "Memory 0x0435 mismatch")
            assertEquals(0x00u, memory[0x0436], "Memory 0x0436 mismatch")
            assertEquals(0x00u, memory[0x0437], "Memory 0x0437 mismatch")
            assertEquals(0x00u, memory[0x0438], "Memory 0x0438 mismatch")
            assertEquals(0x00u, memory[0x0439], "Memory 0x0439 mismatch")
        }
    }

    /**
     * Test case 1 from frame 193
     * Function: drawTitleScreen (0x86FF)
     * Call depth: 4
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `drawTitleScreen_frame193_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0770] = 0x01u
            memory[0x0772] = 0x01u

            // Execute decompiled function
            drawTitleScreen()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x02u, memory[0x0772], "Memory 0x0772 mismatch")
        }
    }

    // =========================================
    // 0x8742: incSubtask
    // 1 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 29
     * Function: incSubtask (0x8742)
     * Call depth: 4
     * Memory reads: 3, writes: 3
     */
    @Test
    fun `incSubtask_frame29_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01F8] = 0x44u
            memory[0x01F9] = 0x87u
            memory[0x073C] = 0x0Du

            // Execute decompiled function
            incSubtask()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x0Eu, memory[0x073C], "Memory 0x073C mismatch")
        }
    }

    // =========================================
    // 0x874B: incmodetaskB
    // 1 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 29
     * Function: incmodetaskB (0x874B)
     * Call depth: 4
     * Memory reads: 3, writes: 3
     */
    @Test
    fun `incmodetaskB_frame29_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01F8] = 0x4Du
            memory[0x01F9] = 0x87u
            memory[0x0772] = 0x01u

            // Execute decompiled function
            incmodetaskB()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x02u, memory[0x0772], "Memory 0x0772 mismatch")
        }
    }

    // =========================================
    // 0x88A2: resetScreenTimer
    // 2 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 177
     * Function: resetScreenTimer (0x88A2)
     * Call depth: 4
     * Memory reads: 3, writes: 4
     */
    @Test
    fun `resetScreenTimer_frame177_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01F8] = 0xA4u
            memory[0x01F9] = 0x88u
            memory[0x073C] = 0x07u

            // Execute decompiled function
            resetScreenTimer()

            // Verify: Check output memory (2 addresses)
            assertEquals(0x08u, memory[0x073C], "Memory 0x073C mismatch")
            assertEquals(0x07u, memory[0x07A0], "Memory 0x07A0 mismatch")
        }
    }

    // =========================================
    // 0x8967: renderAttributeTables
    // 224 calls, 57 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 13
     * Function: renderAttributeTables (0x8967)
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `renderAttributeTables_frame13_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            renderAttributeTables()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x06u, memory[0x0773], "Memory 0x0773 mismatch")
        }
    }

    /**
     * Test case 1 from frame 14
     * Function: renderAttributeTables (0x8967)
     * Call depth: 9
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `renderAttributeTables_frame14_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            renderAttributeTables()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x06u, memory[0x0773], "Memory 0x0773 mismatch")
        }
    }

    /**
     * Test case 2 from frame 15
     * Function: renderAttributeTables (0x8967)
     * Call depth: 13
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `renderAttributeTables_frame15_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            renderAttributeTables()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x06u, memory[0x0773], "Memory 0x0773 mismatch")
        }
    }

    /**
     * Test case 3 from frame 17
     * Function: renderAttributeTables (0x8967)
     * Call depth: 9
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `renderAttributeTables_frame17_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            renderAttributeTables()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x06u, memory[0x0773], "Memory 0x0773 mismatch")
        }
    }

    /**
     * Test case 4 from frame 18
     * Function: renderAttributeTables (0x8967)
     * Call depth: 12
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `renderAttributeTables_frame18_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            renderAttributeTables()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x06u, memory[0x0773], "Memory 0x0773 mismatch")
        }
    }

    /**
     * Test case 5 from frame 20
     * Function: renderAttributeTables (0x8967)
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `renderAttributeTables_frame20_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            renderAttributeTables()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x06u, memory[0x0773], "Memory 0x0773 mismatch")
        }
    }

    /**
     * Test case 6 from frame 1135
     * Function: renderAttributeTables (0x8967)
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `renderAttributeTables_frame1135_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            renderAttributeTables()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x06u, memory[0x0773], "Memory 0x0773 mismatch")
        }
    }

    /**
     * Test case 7 from frame 1261
     * Function: renderAttributeTables (0x8967)
     * Call depth: 10
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `renderAttributeTables_frame1261_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            renderAttributeTables()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x06u, memory[0x0773], "Memory 0x0773 mismatch")
        }
    }

    /**
     * Test case 8 from frame 4397
     * Function: renderAttributeTables (0x8967)
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `renderAttributeTables_frame4397_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            renderAttributeTables()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x06u, memory[0x0773], "Memory 0x0773 mismatch")
        }
    }

    /**
     * Test case 9 from frame 4547
     * Function: renderAttributeTables (0x8967)
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `renderAttributeTables_frame4547_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            renderAttributeTables()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x06u, memory[0x0773], "Memory 0x0773 mismatch")
        }
    }

    // =========================================
    // 0x89BA: setVRAMCtrl
    // 56 calls, 2 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 13
     * Function: setVRAMCtrl (0x89BA)
     * Call depth: 14
     * Memory reads: 0, writes: 2
     */
    @Test
    fun `setVRAMCtrl_frame13_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            setVRAMCtrl()

            // Verify: Check output memory (2 addresses)
            assertEquals(0x90u, memory[0x0340], "Memory 0x0340 mismatch")
            assertEquals(0x06u, memory[0x0773], "Memory 0x0773 mismatch")
        }
    }

    /**
     * Test case 1 from frame 1137
     * Function: setVRAMCtrl (0x89BA)
     * Call depth: 8
     * Memory reads: 0, writes: 2
     */
    @Test
    fun `setVRAMCtrl_frame1137_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            setVRAMCtrl()

            // Verify: Check output memory (2 addresses)
            assertEquals(0x1Cu, memory[0x0340], "Memory 0x0340 mismatch")
            assertEquals(0x06u, memory[0x0773], "Memory 0x0773 mismatch")
        }
    }

    // =========================================
    // 0x8A6A: writeBlockMetatile
    // Parameters: A
    // 3 calls, 2 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 4403
     * Function: writeBlockMetatile (0x8A6A)
     * Parameters: A
     * Call depth: 7
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `writeBlockMetatile_frame4403_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            writeBlockMetatile(0x0A)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 4579
     * Function: writeBlockMetatile (0x8A6A)
     * Parameters: A
     * Call depth: 7
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `writeBlockMetatile_frame4579_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            writeBlockMetatile(0x0A)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0x8A8C: moveVOffset
    // Parameters: Y
    // 6 calls, 6 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 4374
     * Function: moveVOffset (0x8A8C)
     * Parameters: Y
     * Call depth: 9
     * Memory reads: 5, writes: 6
     */
    @Test
    fun `moveVOffset_frame4374_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x006D] = 0x01u
            memory[0x0086] = 0x05u
            memory[0x00B5] = 0x01u
            memory[0x01EF] = 0x8Eu
            memory[0x01F0] = 0x8Au

            // Execute decompiled function
            moveVOffset(0x01)

            // Verify: Check output memory (4 addresses)
            assertEquals(0x01u, memory[0x0076], "Memory 0x0076 mismatch")
            assertEquals(0x00u, memory[0x008F], "Memory 0x008F mismatch")
            assertEquals(0x01u, memory[0x00BE], "Memory 0x00BE mismatch")
            assertEquals(0x01u, memory[0x03EA], "Memory 0x03EA mismatch")
        }
    }

    /**
     * Test case 1 from frame 4403
     * Function: moveVOffset (0x8A8C)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 2, writes: 2
     */
    @Test
    fun `moveVOffset_frame4403_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x01F2] = 0x8Eu
            memory[0x01F3] = 0x8Au

            // Execute decompiled function
            moveVOffset(0x01)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 2 from frame 4550
     * Function: moveVOffset (0x8A8C)
     * Parameters: Y
     * Call depth: 9
     * Memory reads: 5, writes: 6
     */
    @Test
    fun `moveVOffset_frame4550_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x006D] = 0x01u
            memory[0x0086] = 0x59u
            memory[0x00B5] = 0x01u
            memory[0x01EF] = 0x8Eu
            memory[0x01F0] = 0x8Au

            // Execute decompiled function
            moveVOffset(0x01)

            // Verify: Check output memory (4 addresses)
            assertEquals(0x01u, memory[0x0077], "Memory 0x0077 mismatch")
            assertEquals(0x60u, memory[0x0090], "Memory 0x0090 mismatch")
            assertEquals(0x01u, memory[0x00BF], "Memory 0x00BF mismatch")
            assertEquals(0x01u, memory[0x03EB], "Memory 0x03EB mismatch")
        }
    }

    /**
     * Test case 3 from frame 4579
     * Function: moveVOffset (0x8A8C)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 2, writes: 2
     */
    @Test
    fun `moveVOffset_frame4579_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x01F2] = 0x8Eu
            memory[0x01F3] = 0x8Au

            // Execute decompiled function
            moveVOffset(0x01)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 4 from frame 4620
     * Function: moveVOffset (0x8A8C)
     * Parameters: Y
     * Call depth: 9
     * Memory reads: 5, writes: 6
     */
    @Test
    fun `moveVOffset_frame4620_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x006D] = 0x01u
            memory[0x0086] = 0x7Fu
            memory[0x00B5] = 0x01u
            memory[0x01EF] = 0x8Eu
            memory[0x01F0] = 0x8Au

            // Execute decompiled function
            moveVOffset(0x01)

            // Verify: Check output memory (4 addresses)
            assertEquals(0x01u, memory[0x0076], "Memory 0x0076 mismatch")
            assertEquals(0x80u, memory[0x008F], "Memory 0x008F mismatch")
            assertEquals(0x01u, memory[0x00BE], "Memory 0x00BE mismatch")
            assertEquals(0x01u, memory[0x03EA], "Memory 0x03EA mismatch")
        }
    }

    /**
     * Test case 5 from frame 4649
     * Function: moveVOffset (0x8A8C)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 2, writes: 2
     */
    @Test
    fun `moveVOffset_frame4649_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x01F2] = 0x8Eu
            memory[0x01F3] = 0x8Au

            // Execute decompiled function
            moveVOffset(0x01)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0x8A94: putBlockMetatile
    // Parameters: A, X, Y
    // 6 calls, 4 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 4374
     * Function: putBlockMetatile (0x8A94)
     * Parameters: A, X, Y
     * Call depth: 9
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `putBlockMetatile_frame4374_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            putBlockMetatile(0x0A, 0x00, 0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 4403
     * Function: putBlockMetatile (0x8A94)
     * Parameters: A, X, Y
     * Call depth: 8
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `putBlockMetatile_frame4403_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            putBlockMetatile(0x0A, 0x00, 0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 2 from frame 4550
     * Function: putBlockMetatile (0x8A94)
     * Parameters: A, X, Y
     * Call depth: 9
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `putBlockMetatile_frame4550_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            putBlockMetatile(0x0A, 0x01, 0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 3 from frame 4579
     * Function: putBlockMetatile (0x8A94)
     * Parameters: A, X, Y
     * Call depth: 8
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `putBlockMetatile_frame4579_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            putBlockMetatile(0x0A, 0x01, 0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0x8E16: initializeNameTables
    // 37967 calls, 1139 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 3
     * Function: initializeNameTables (0x8E16)
     * Call depth: 2
     * Memory reads: 2, writes: 0
     */
    @Test
    fun `initializeNameTables_frame3_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0006] = 0x31u
            memory[0x0007] = 0x82u

            // Execute decompiled function
            initializeNameTables()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 32
     * Function: initializeNameTables (0x8E16)
     * Call depth: 6
     * Memory reads: 7, writes: 13
     */
    @Test
    fun `initializeNameTables_frame32_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (7 addresses)
            memory[0x0006] = 0x31u
            memory[0x0007] = 0x91u
            memory[0x0490] = 0x00u
            memory[0x0710] = 0x02u
            memory[0x071A] = 0x00u
            memory[0x074E] = 0x01u
            memory[0x0752] = 0x00u

            // Execute decompiled function
            initializeNameTables()

            // Verify: Check output memory (11 addresses)
            assertEquals(0x00u, memory[0x001D], "Memory 0x001D mismatch")
            assertEquals(0x01u, memory[0x0033], "Memory 0x0033 mismatch")
            assertEquals(0x00u, memory[0x006D], "Memory 0x006D mismatch")
            assertEquals(0x28u, memory[0x0086], "Memory 0x0086 mismatch")
            assertEquals(0x01u, memory[0x00B5], "Memory 0x00B5 mismatch")
            assertEquals(0xB0u, memory[0x00CE], "Memory 0x00CE mismatch")
            assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
            assertEquals(0xFFu, memory[0x0490], "Memory 0x0490 mismatch")
            assertEquals(0x00u, memory[0x0704], "Memory 0x0704 mismatch")
            assertEquals(0x28u, memory[0x070A], "Memory 0x070A mismatch")
            assertEquals(0x00u, memory[0x075B], "Memory 0x075B mismatch")
        }
    }

    /**
     * Test case 2 from frame 1126
     * Function: initializeNameTables (0x8E16)
     * Call depth: 7
     * Memory reads: 6, writes: 3
     */
    @Test
    fun `initializeNameTables_frame1126_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (6 addresses)
            memory[0x0006] = 0x76u
            memory[0x0007] = 0xB3u
            memory[0x000C] = 0x00u
            memory[0x009F] = 0x00u
            memory[0x0704] = 0x00u
            memory[0x070A] = 0x60u

            // Execute decompiled function
            initializeNameTables()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x60u, memory[0x0709], "Memory 0x0709 mismatch")
        }
    }

    /**
     * Test case 3 from frame 1590
     * Function: initializeNameTables (0x8E16)
     * Call depth: 7
     * Memory reads: 11, writes: 3
     */
    @Test
    fun `initializeNameTables_frame1590_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (11 addresses)
            memory[0x0006] = 0x76u
            memory[0x0007] = 0xB3u
            memory[0x000A] = 0x00u
            memory[0x000C] = 0x00u
            memory[0x000D] = 0x00u
            memory[0x009F] = 0xFDu
            memory[0x00CE] = 0x97u
            memory[0x0704] = 0x00u
            memory[0x0706] = 0x01u
            memory[0x0708] = 0xB0u
            memory[0x070A] = 0x60u

            // Execute decompiled function
            initializeNameTables()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x60u, memory[0x0709], "Memory 0x0709 mismatch")
        }
    }

    /**
     * Test case 4 from frame 1688
     * Function: initializeNameTables (0x8E16)
     * Call depth: 5
     * Memory reads: 3, writes: 0
     */
    @Test
    fun `initializeNameTables_frame1688_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x0006] = 0x69u
            memory[0x0007] = 0xB2u
            memory[0x0747] = 0xB7u

            // Execute decompiled function
            initializeNameTables()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 5 from frame 1802
     * Function: initializeNameTables (0x8E16)
     * Call depth: 5
     * Memory reads: 3, writes: 0
     */
    @Test
    fun `initializeNameTables_frame1802_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x0006] = 0x69u
            memory[0x0007] = 0xB2u
            memory[0x0747] = 0x7Eu

            // Execute decompiled function
            initializeNameTables()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 6 from frame 1916
     * Function: initializeNameTables (0x8E16)
     * Call depth: 5
     * Memory reads: 3, writes: 0
     */
    @Test
    fun `initializeNameTables_frame1916_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x0006] = 0x69u
            memory[0x0007] = 0xB2u
            memory[0x0747] = 0x45u

            // Execute decompiled function
            initializeNameTables()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 7 from frame 3694
     * Function: initializeNameTables (0x8E16)
     * Call depth: 7
     * Memory reads: 11, writes: 3
     */
    @Test
    fun `initializeNameTables_frame3694_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (11 addresses)
            memory[0x0006] = 0x76u
            memory[0x0007] = 0xB3u
            memory[0x000A] = 0x00u
            memory[0x000C] = 0x00u
            memory[0x000D] = 0x00u
            memory[0x009F] = 0xFCu
            memory[0x00CE] = 0x9Eu
            memory[0x0704] = 0x00u
            memory[0x0706] = 0x01u
            memory[0x0708] = 0xB0u
            memory[0x070A] = 0x90u

            // Execute decompiled function
            initializeNameTables()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x90u, memory[0x0709], "Memory 0x0709 mismatch")
        }
    }

    /**
     * Test case 8 from frame 4930
     * Function: initializeNameTables (0x8E16)
     * Call depth: 7
     * Memory reads: 11, writes: 3
     */
    @Test
    fun `initializeNameTables_frame4930_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (11 addresses)
            memory[0x0006] = 0x76u
            memory[0x0007] = 0xB3u
            memory[0x000A] = 0x00u
            memory[0x000C] = 0x00u
            memory[0x000D] = 0x80u
            memory[0x009F] = 0xFCu
            memory[0x00CE] = 0xA8u
            memory[0x0704] = 0x00u
            memory[0x0706] = 0x01u
            memory[0x0708] = 0xB0u
            memory[0x070A] = 0x60u

            // Execute decompiled function
            initializeNameTables()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x60u, memory[0x0709], "Memory 0x0709 mismatch")
        }
    }

    /**
     * Test case 9 from frame 6929
     * Function: initializeNameTables (0x8E16)
     * Call depth: 8
     * Memory reads: 28, writes: 31
     */
    @Test
    fun `initializeNameTables_frame6929_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (28 addresses)
            memory[0x0000] = 0x00u
            memory[0x0001] = 0x03u
            memory[0x0006] = 0xFCu
            memory[0x0007] = 0x93u
            memory[0x01F4] = 0xD5u
            memory[0x01F5] = 0x94u
            memory[0x06A0] = 0x15u
            memory[0x06A1] = 0x00u
            memory[0x06A2] = 0x00u
            memory[0x06A3] = 0x00u
            memory[0x06A4] = 0x00u
            memory[0x06A5] = 0x00u
            memory[0x06A6] = 0x00u
            memory[0x06A7] = 0x00u
            memory[0x06A8] = 0x00u
            memory[0x06A9] = 0x00u
            memory[0x06AA] = 0x00u
            memory[0x06AB] = 0x00u
            memory[0x06AC] = 0x54u
            memory[0x06AD] = 0x54u
            memory[0x0725] = 0x03u
            memory[0x0726] = 0x05u
            memory[0x0727] = 0x01u
            memory[0x0728] = 0x00u
            memory[0x0741] = 0x00u
            memory[0x0742] = 0x02u
            memory[0x0743] = 0x00u
            memory[0x074E] = 0x01u

            // Execute decompiled function
            initializeNameTables()

            // Verify: Check output memory (29 addresses)
            assertEquals(0xC0u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x04u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x54u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x00u, memory[0x05D5], "Memory 0x05D5 mismatch")
            assertEquals(0x00u, memory[0x05E5], "Memory 0x05E5 mismatch")
            assertEquals(0x00u, memory[0x05F5], "Memory 0x05F5 mismatch")
            assertEquals(0x00u, memory[0x0605], "Memory 0x0605 mismatch")
            assertEquals(0x00u, memory[0x0615], "Memory 0x0615 mismatch")
            assertEquals(0x00u, memory[0x0625], "Memory 0x0625 mismatch")
            assertEquals(0x00u, memory[0x0635], "Memory 0x0635 mismatch")
            assertEquals(0x00u, memory[0x0645], "Memory 0x0645 mismatch")
            assertEquals(0x00u, memory[0x0655], "Memory 0x0655 mismatch")
            assertEquals(0x00u, memory[0x0665], "Memory 0x0665 mismatch")
            assertEquals(0x00u, memory[0x0675], "Memory 0x0675 mismatch")
            assertEquals(0x54u, memory[0x0685], "Memory 0x0685 mismatch")
            assertEquals(0x54u, memory[0x0695], "Memory 0x0695 mismatch")
            assertEquals(0x00u, memory[0x06A1], "Memory 0x06A1 mismatch")
            assertEquals(0x00u, memory[0x06A2], "Memory 0x06A2 mismatch")
            assertEquals(0x00u, memory[0x06A3], "Memory 0x06A3 mismatch")
            assertEquals(0x00u, memory[0x06A4], "Memory 0x06A4 mismatch")
            assertEquals(0x00u, memory[0x06A5], "Memory 0x06A5 mismatch")
            assertEquals(0x00u, memory[0x06A6], "Memory 0x06A6 mismatch")
            assertEquals(0x00u, memory[0x06A7], "Memory 0x06A7 mismatch")
            assertEquals(0x00u, memory[0x06A8], "Memory 0x06A8 mismatch")
            assertEquals(0x00u, memory[0x06A9], "Memory 0x06A9 mismatch")
            assertEquals(0x00u, memory[0x06AA], "Memory 0x06AA mismatch")
            assertEquals(0x00u, memory[0x06AB], "Memory 0x06AB mismatch")
            assertEquals(0x54u, memory[0x06AC], "Memory 0x06AC mismatch")
            assertEquals(0x54u, memory[0x06AD], "Memory 0x06AD mismatch")
        }
    }

    // =========================================
    // 0x8E59: readJoypads
    // 8 calls, 4 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 3
     * Function: readJoypads (0x8E59)
     * Call depth: 2
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `readJoypads_frame3_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            readJoypads()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 3
     * Function: readJoypads (0x8E59)
     * Call depth: 1
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `readJoypads_frame3_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            readJoypads()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 2 from frame 7
     * Function: readJoypads (0x8E59)
     * Call depth: 6
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `readJoypads_frame7_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            readJoypads()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 3 from frame 7
     * Function: readJoypads (0x8E59)
     * Call depth: 5
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `readJoypads_frame7_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            readJoypads()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0x8EDA: updateScreen
    // 1613 calls, 48 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 8
     * Function: updateScreen (0x8EDA)
     * Call depth: 1
     * Memory reads: 3, writes: 0
     */
    @Test
    fun `updateScreen_frame8_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x0000] = 0x08u
            memory[0x0001] = 0x03u
            memory[0x0308] = 0x00u

            // Execute decompiled function
            updateScreen()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 14
     * Function: updateScreen (0x8EDA)
     * Call depth: 1
     * Memory reads: 122, writes: 5
     */
    @Test
    fun `updateScreen_frame14_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (122 addresses)
            memory[0x0000] = 0x5Eu
            memory[0x0001] = 0x03u
            memory[0x01F8] = 0xABu
            memory[0x01F9] = 0x8Eu
            memory[0x01FA] = 0x34u
            memory[0x035E] = 0x20u
            memory[0x035F] = 0x85u
            memory[0x0360] = 0x9Au
            memory[0x0361] = 0x24u
            memory[0x0362] = 0x24u
            memory[0x0363] = 0x24u
            memory[0x0364] = 0x24u
            memory[0x0365] = 0x24u
            memory[0x0366] = 0x24u
            memory[0x0367] = 0x24u
            memory[0x0368] = 0x24u
            memory[0x0369] = 0x24u
            memory[0x036A] = 0x24u
            memory[0x036B] = 0x24u
            memory[0x036C] = 0x24u
            memory[0x036D] = 0x24u
            memory[0x036E] = 0x24u
            memory[0x036F] = 0x24u
            memory[0x0370] = 0x24u
            memory[0x0371] = 0x24u
            memory[0x0372] = 0x32u
            memory[0x0373] = 0x34u
            memory[0x0374] = 0x26u
            memory[0x0375] = 0x26u
            memory[0x0376] = 0x26u
            memory[0x0377] = 0xB5u
            memory[0x0378] = 0xB7u
            memory[0x0379] = 0xB5u
            memory[0x037A] = 0xB7u
            memory[0x037B] = 0x20u
            memory[0x037C] = 0x86u
            memory[0x037D] = 0x9Au
            memory[0x037E] = 0x24u
            memory[0x037F] = 0x24u
            memory[0x0380] = 0x24u
            memory[0x0381] = 0x24u
            memory[0x0382] = 0x24u
            memory[0x0383] = 0x24u
            memory[0x0384] = 0x24u
            memory[0x0385] = 0x24u
            memory[0x0386] = 0x24u
            memory[0x0387] = 0x24u
            memory[0x0388] = 0x24u
            memory[0x0389] = 0x24u
            memory[0x038A] = 0x24u
            memory[0x038B] = 0x24u
            memory[0x038C] = 0x24u
            memory[0x038D] = 0x24u
            memory[0x038E] = 0x24u
            memory[0x038F] = 0x24u
            memory[0x0390] = 0x33u
            memory[0x0391] = 0x26u
            memory[0x0392] = 0x34u
            memory[0x0393] = 0x26u
            memory[0x0394] = 0xB4u
            memory[0x0395] = 0xB6u
            memory[0x0396] = 0xB4u
            memory[0x0397] = 0xB6u
            memory[0x0398] = 0x20u
            memory[0x0399] = 0x87u
            memory[0x039A] = 0x9Au
            memory[0x039B] = 0x24u
            memory[0x039C] = 0x24u
            memory[0x039D] = 0x24u
            memory[0x039E] = 0x24u
            memory[0x039F] = 0x24u
            memory[0x03A0] = 0x24u
            memory[0x03A1] = 0x24u
            memory[0x03A2] = 0x24u
            memory[0x03A3] = 0x24u
            memory[0x03A4] = 0x24u
            memory[0x03A5] = 0x24u
            memory[0x03A6] = 0x24u
            memory[0x03A7] = 0x24u
            memory[0x03A8] = 0x24u
            memory[0x03A9] = 0x24u
            memory[0x03AA] = 0x24u
            memory[0x03AB] = 0x24u
            memory[0x03AC] = 0x24u
            memory[0x03AD] = 0x24u
            memory[0x03AE] = 0x33u
            memory[0x03AF] = 0x26u
            memory[0x03B0] = 0x26u
            memory[0x03B1] = 0xB5u
            memory[0x03B2] = 0xB7u
            memory[0x03B3] = 0xB5u
            memory[0x03B4] = 0xB7u
            memory[0x03B5] = 0x23u
            memory[0x03B6] = 0xC9u
            memory[0x03B7] = 0x01u
            memory[0x03B8] = 0x00u
            memory[0x03B9] = 0x23u
            memory[0x03BA] = 0xD1u
            memory[0x03BB] = 0x01u
            memory[0x03BC] = 0x00u
            memory[0x03BD] = 0x23u
            memory[0x03BE] = 0xD9u
            memory[0x03BF] = 0x01u
            memory[0x03C0] = 0x00u
            memory[0x03C1] = 0x23u
            memory[0x03C2] = 0xE1u
            memory[0x03C3] = 0x01u
            memory[0x03C4] = 0x00u
            memory[0x03C5] = 0x23u
            memory[0x03C6] = 0xE9u
            memory[0x03C7] = 0x01u
            memory[0x03C8] = 0x00u
            memory[0x03C9] = 0x23u
            memory[0x03CA] = 0xF1u
            memory[0x03CB] = 0x01u
            memory[0x03CC] = 0x50u
            memory[0x03CD] = 0x23u
            memory[0x03CE] = 0xF9u
            memory[0x03CF] = 0x01u
            memory[0x03D0] = 0x05u
            memory[0x03D1] = 0x00u
            memory[0x0778] = 0x14u

            // Execute decompiled function
            updateScreen()

            // Verify: Check output memory (2 addresses)
            assertEquals(0xD1u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x03u, memory[0x0001], "Memory 0x0001 mismatch")
        }
    }

    /**
     * Test case 2 from frame 19
     * Function: updateScreen (0x8EDA)
     * Call depth: 1
     * Memory reads: 122, writes: 5
     */
    @Test
    fun `updateScreen_frame19_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (122 addresses)
            memory[0x0000] = 0x5Eu
            memory[0x0001] = 0x03u
            memory[0x01F8] = 0xABu
            memory[0x01F9] = 0x8Eu
            memory[0x01FA] = 0x34u
            memory[0x035E] = 0x20u
            memory[0x035F] = 0x99u
            memory[0x0360] = 0x9Au
            memory[0x0361] = 0x24u
            memory[0x0362] = 0x24u
            memory[0x0363] = 0x24u
            memory[0x0364] = 0x24u
            memory[0x0365] = 0x24u
            memory[0x0366] = 0x24u
            memory[0x0367] = 0x24u
            memory[0x0368] = 0x24u
            memory[0x0369] = 0x24u
            memory[0x036A] = 0x24u
            memory[0x036B] = 0x24u
            memory[0x036C] = 0x24u
            memory[0x036D] = 0x24u
            memory[0x036E] = 0x24u
            memory[0x036F] = 0x24u
            memory[0x0370] = 0x24u
            memory[0x0371] = 0x24u
            memory[0x0372] = 0x24u
            memory[0x0373] = 0x24u
            memory[0x0374] = 0x24u
            memory[0x0375] = 0x37u
            memory[0x0376] = 0x25u
            memory[0x0377] = 0xB5u
            memory[0x0378] = 0xB7u
            memory[0x0379] = 0xB5u
            memory[0x037A] = 0xB7u
            memory[0x037B] = 0x20u
            memory[0x037C] = 0x9Au
            memory[0x037D] = 0x9Au
            memory[0x037E] = 0x24u
            memory[0x037F] = 0x24u
            memory[0x0380] = 0x24u
            memory[0x0381] = 0x24u
            memory[0x0382] = 0x24u
            memory[0x0383] = 0x24u
            memory[0x0384] = 0x24u
            memory[0x0385] = 0x24u
            memory[0x0386] = 0x24u
            memory[0x0387] = 0x24u
            memory[0x0388] = 0x24u
            memory[0x0389] = 0x24u
            memory[0x038A] = 0x24u
            memory[0x038B] = 0x24u
            memory[0x038C] = 0x24u
            memory[0x038D] = 0x24u
            memory[0x038E] = 0x24u
            memory[0x038F] = 0x24u
            memory[0x0390] = 0x24u
            memory[0x0391] = 0x24u
            memory[0x0392] = 0x36u
            memory[0x0393] = 0x25u
            memory[0x0394] = 0xB4u
            memory[0x0395] = 0xB6u
            memory[0x0396] = 0xB4u
            memory[0x0397] = 0xB6u
            memory[0x0398] = 0x20u
            memory[0x0399] = 0x9Bu
            memory[0x039A] = 0x9Au
            memory[0x039B] = 0x24u
            memory[0x039C] = 0x24u
            memory[0x039D] = 0x24u
            memory[0x039E] = 0x24u
            memory[0x039F] = 0x24u
            memory[0x03A0] = 0x24u
            memory[0x03A1] = 0x24u
            memory[0x03A2] = 0x24u
            memory[0x03A3] = 0x24u
            memory[0x03A4] = 0x24u
            memory[0x03A5] = 0x24u
            memory[0x03A6] = 0x24u
            memory[0x03A7] = 0x24u
            memory[0x03A8] = 0x24u
            memory[0x03A9] = 0x24u
            memory[0x03AA] = 0x24u
            memory[0x03AB] = 0x24u
            memory[0x03AC] = 0x24u
            memory[0x03AD] = 0x24u
            memory[0x03AE] = 0x24u
            memory[0x03AF] = 0x37u
            memory[0x03B0] = 0x25u
            memory[0x03B1] = 0xB5u
            memory[0x03B2] = 0xB7u
            memory[0x03B3] = 0xB5u
            memory[0x03B4] = 0xB7u
            memory[0x03B5] = 0x23u
            memory[0x03B6] = 0xCEu
            memory[0x03B7] = 0x01u
            memory[0x03B8] = 0x00u
            memory[0x03B9] = 0x23u
            memory[0x03BA] = 0xD6u
            memory[0x03BB] = 0x01u
            memory[0x03BC] = 0x00u
            memory[0x03BD] = 0x23u
            memory[0x03BE] = 0xDEu
            memory[0x03BF] = 0x01u
            memory[0x03C0] = 0x00u
            memory[0x03C1] = 0x23u
            memory[0x03C2] = 0xE6u
            memory[0x03C3] = 0x01u
            memory[0x03C4] = 0x00u
            memory[0x03C5] = 0x23u
            memory[0x03C6] = 0xEEu
            memory[0x03C7] = 0x01u
            memory[0x03C8] = 0x00u
            memory[0x03C9] = 0x23u
            memory[0x03CA] = 0xF6u
            memory[0x03CB] = 0x01u
            memory[0x03CC] = 0x50u
            memory[0x03CD] = 0x23u
            memory[0x03CE] = 0xFEu
            memory[0x03CF] = 0x01u
            memory[0x03D0] = 0x05u
            memory[0x03D1] = 0x00u
            memory[0x0778] = 0x14u

            // Execute decompiled function
            updateScreen()

            // Verify: Check output memory (2 addresses)
            assertEquals(0xD1u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x03u, memory[0x0001], "Memory 0x0001 mismatch")
        }
    }

    /**
     * Test case 3 from frame 24
     * Function: updateScreen (0x8EDA)
     * Call depth: 1
     * Memory reads: 122, writes: 5
     */
    @Test
    fun `updateScreen_frame24_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (122 addresses)
            memory[0x0000] = 0x5Eu
            memory[0x0001] = 0x03u
            memory[0x01F8] = 0xABu
            memory[0x01F9] = 0x8Eu
            memory[0x01FA] = 0x34u
            memory[0x035E] = 0x24u
            memory[0x035F] = 0x8Du
            memory[0x0360] = 0x9Au
            memory[0x0361] = 0x24u
            memory[0x0362] = 0x24u
            memory[0x0363] = 0x24u
            memory[0x0364] = 0x24u
            memory[0x0365] = 0x24u
            memory[0x0366] = 0x24u
            memory[0x0367] = 0x54u
            memory[0x0368] = 0x56u
            memory[0x0369] = 0x24u
            memory[0x036A] = 0x24u
            memory[0x036B] = 0x24u
            memory[0x036C] = 0x24u
            memory[0x036D] = 0x24u
            memory[0x036E] = 0x24u
            memory[0x036F] = 0x45u
            memory[0x0370] = 0x47u
            memory[0x0371] = 0x24u
            memory[0x0372] = 0x24u
            memory[0x0373] = 0x24u
            memory[0x0374] = 0x24u
            memory[0x0375] = 0x24u
            memory[0x0376] = 0x24u
            memory[0x0377] = 0xB5u
            memory[0x0378] = 0xB7u
            memory[0x0379] = 0xB5u
            memory[0x037A] = 0xB7u
            memory[0x037B] = 0x24u
            memory[0x037C] = 0x8Eu
            memory[0x037D] = 0x9Au
            memory[0x037E] = 0x24u
            memory[0x037F] = 0x24u
            memory[0x0380] = 0x24u
            memory[0x0381] = 0x24u
            memory[0x0382] = 0x24u
            memory[0x0383] = 0x24u
            memory[0x0384] = 0x24u
            memory[0x0385] = 0x24u
            memory[0x0386] = 0x24u
            memory[0x0387] = 0x24u
            memory[0x0388] = 0x24u
            memory[0x0389] = 0x24u
            memory[0x038A] = 0x24u
            memory[0x038B] = 0x24u
            memory[0x038C] = 0x53u
            memory[0x038D] = 0x55u
            memory[0x038E] = 0x24u
            memory[0x038F] = 0x24u
            memory[0x0390] = 0x24u
            memory[0x0391] = 0x24u
            memory[0x0392] = 0x24u
            memory[0x0393] = 0x24u
            memory[0x0394] = 0xB4u
            memory[0x0395] = 0xB6u
            memory[0x0396] = 0xB4u
            memory[0x0397] = 0xB6u
            memory[0x0398] = 0x24u
            memory[0x0399] = 0x8Fu
            memory[0x039A] = 0x9Au
            memory[0x039B] = 0x24u
            memory[0x039C] = 0x24u
            memory[0x039D] = 0x24u
            memory[0x039E] = 0x24u
            memory[0x039F] = 0x24u
            memory[0x03A0] = 0x24u
            memory[0x03A1] = 0x24u
            memory[0x03A2] = 0x24u
            memory[0x03A3] = 0x24u
            memory[0x03A4] = 0x24u
            memory[0x03A5] = 0x24u
            memory[0x03A6] = 0x24u
            memory[0x03A7] = 0x24u
            memory[0x03A8] = 0x24u
            memory[0x03A9] = 0x54u
            memory[0x03AA] = 0x56u
            memory[0x03AB] = 0x24u
            memory[0x03AC] = 0x24u
            memory[0x03AD] = 0x24u
            memory[0x03AE] = 0x24u
            memory[0x03AF] = 0x24u
            memory[0x03B0] = 0x35u
            memory[0x03B1] = 0xB5u
            memory[0x03B2] = 0xB7u
            memory[0x03B3] = 0xB5u
            memory[0x03B4] = 0xB7u
            memory[0x03B5] = 0x27u
            memory[0x03B6] = 0xCBu
            memory[0x03B7] = 0x01u
            memory[0x03B8] = 0x00u
            memory[0x03B9] = 0x27u
            memory[0x03BA] = 0xD3u
            memory[0x03BB] = 0x01u
            memory[0x03BC] = 0x30u
            memory[0x03BD] = 0x27u
            memory[0x03BE] = 0xDBu
            memory[0x03BF] = 0x01u
            memory[0x03C0] = 0x00u
            memory[0x03C1] = 0x27u
            memory[0x03C2] = 0xE3u
            memory[0x03C3] = 0x01u
            memory[0x03C4] = 0xD0u
            memory[0x03C5] = 0x27u
            memory[0x03C6] = 0xEBu
            memory[0x03C7] = 0x01u
            memory[0x03C8] = 0x00u
            memory[0x03C9] = 0x27u
            memory[0x03CA] = 0xF3u
            memory[0x03CB] = 0x01u
            memory[0x03CC] = 0x50u
            memory[0x03CD] = 0x27u
            memory[0x03CE] = 0xFBu
            memory[0x03CF] = 0x01u
            memory[0x03D0] = 0x05u
            memory[0x03D1] = 0x00u
            memory[0x0778] = 0x14u

            // Execute decompiled function
            updateScreen()

            // Verify: Check output memory (2 addresses)
            assertEquals(0xD1u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x03u, memory[0x0001], "Memory 0x0001 mismatch")
        }
    }

    /**
     * Test case 4 from frame 44
     * Function: updateScreen (0x8EDA)
     * Call depth: 1
     * Memory reads: 27, writes: 5
     */
    @Test
    fun `updateScreen_frame44_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (27 addresses)
            memory[0x0000] = 0x0Bu
            memory[0x0001] = 0x03u
            memory[0x01F8] = 0xABu
            memory[0x01F9] = 0x8Eu
            memory[0x01FA] = 0x12u
            memory[0x030B] = 0x21u
            memory[0x030C] = 0x4Bu
            memory[0x030D] = 0x09u
            memory[0x030E] = 0x20u
            memory[0x030F] = 0x18u
            memory[0x0310] = 0x1Bu
            memory[0x0311] = 0x15u
            memory[0x0312] = 0x0Du
            memory[0x0313] = 0x24u
            memory[0x0314] = 0x01u
            memory[0x0315] = 0x28u
            memory[0x0316] = 0x01u
            memory[0x0317] = 0x22u
            memory[0x0318] = 0x0Cu
            memory[0x0319] = 0x47u
            memory[0x031A] = 0x24u
            memory[0x031B] = 0x23u
            memory[0x031C] = 0xDCu
            memory[0x031D] = 0x01u
            memory[0x031E] = 0xBAu
            memory[0x031F] = 0x00u
            memory[0x0778] = 0x10u

            // Execute decompiled function
            updateScreen()

            // Verify: Check output memory (2 addresses)
            assertEquals(0x1Fu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x03u, memory[0x0001], "Memory 0x0001 mismatch")
        }
    }

    /**
     * Test case 5 from frame 1265
     * Function: updateScreen (0x8EDA)
     * Call depth: 1
     * Memory reads: 31, writes: 5
     */
    @Test
    fun `updateScreen_frame1265_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (31 addresses)
            memory[0x0000] = 0x45u
            memory[0x0001] = 0x03u
            memory[0x01F8] = 0xABu
            memory[0x01F9] = 0x8Eu
            memory[0x01FA] = 0x02u
            memory[0x0345] = 0x27u
            memory[0x0346] = 0xD6u
            memory[0x0347] = 0x01u
            memory[0x0348] = 0x0Au
            memory[0x0349] = 0x27u
            memory[0x034A] = 0xDEu
            memory[0x034B] = 0x01u
            memory[0x034C] = 0x00u
            memory[0x034D] = 0x27u
            memory[0x034E] = 0xE6u
            memory[0x034F] = 0x01u
            memory[0x0350] = 0x00u
            memory[0x0351] = 0x27u
            memory[0x0352] = 0xEEu
            memory[0x0353] = 0x01u
            memory[0x0354] = 0x00u
            memory[0x0355] = 0x27u
            memory[0x0356] = 0xF6u
            memory[0x0357] = 0x01u
            memory[0x0358] = 0x50u
            memory[0x0359] = 0x27u
            memory[0x035A] = 0xFEu
            memory[0x035B] = 0x01u
            memory[0x035C] = 0x05u
            memory[0x035D] = 0x00u
            memory[0x0778] = 0x10u

            // Execute decompiled function
            updateScreen()

            // Verify: Check output memory (2 addresses)
            assertEquals(0x5Du, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x03u, memory[0x0001], "Memory 0x0001 mismatch")
        }
    }

    /**
     * Test case 6 from frame 4393
     * Function: updateScreen (0x8EDA)
     * Call depth: 1
     * Memory reads: 14, writes: 5
     */
    @Test
    fun `updateScreen_frame4393_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (14 addresses)
            memory[0x0000] = 0x07u
            memory[0x0001] = 0x03u
            memory[0x01F8] = 0xABu
            memory[0x01F9] = 0x8Eu
            memory[0x01FA] = 0x08u
            memory[0x0307] = 0x3Fu
            memory[0x0308] = 0x0Cu
            memory[0x0309] = 0x04u
            memory[0x030A] = 0x0Fu
            memory[0x030B] = 0x17u
            memory[0x030C] = 0x17u
            memory[0x030D] = 0x0Fu
            memory[0x030E] = 0x00u
            memory[0x0778] = 0x10u

            // Execute decompiled function
            updateScreen()

            // Verify: Check output memory (2 addresses)
            assertEquals(0x0Eu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x03u, memory[0x0001], "Memory 0x0001 mismatch")
        }
    }

    /**
     * Test case 7 from frame 4553
     * Function: updateScreen (0x8EDA)
     * Call depth: 1
     * Memory reads: 19, writes: 5
     */
    @Test
    fun `updateScreen_frame4553_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (19 addresses)
            memory[0x0000] = 0x06u
            memory[0x0001] = 0x03u
            memory[0x01F8] = 0xABu
            memory[0x01F9] = 0x8Eu
            memory[0x01FA] = 0x04u
            memory[0x0306] = 0x26u
            memory[0x0307] = 0x6Cu
            memory[0x0308] = 0x02u
            memory[0x0309] = 0x24u
            memory[0x030A] = 0x24u
            memory[0x030B] = 0x3Fu
            memory[0x030C] = 0x0Cu
            memory[0x030D] = 0x04u
            memory[0x030E] = 0x0Fu
            memory[0x030F] = 0x27u
            memory[0x0310] = 0x17u
            memory[0x0311] = 0x0Fu
            memory[0x0312] = 0x00u
            memory[0x0778] = 0x10u

            // Execute decompiled function
            updateScreen()

            // Verify: Check output memory (2 addresses)
            assertEquals(0x12u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x03u, memory[0x0001], "Memory 0x0001 mismatch")
        }
    }

    /**
     * Test case 8 from frame 4679
     * Function: updateScreen (0x8EDA)
     * Call depth: 1
     * Memory reads: 31, writes: 5
     */
    @Test
    fun `updateScreen_frame4679_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (31 addresses)
            memory[0x0000] = 0x45u
            memory[0x0001] = 0x03u
            memory[0x01F8] = 0xABu
            memory[0x01F9] = 0x8Eu
            memory[0x01FA] = 0x02u
            memory[0x0345] = 0x23u
            memory[0x0346] = 0xD4u
            memory[0x0347] = 0x01u
            memory[0x0348] = 0x00u
            memory[0x0349] = 0x23u
            memory[0x034A] = 0xDCu
            memory[0x034B] = 0x01u
            memory[0x034C] = 0x00u
            memory[0x034D] = 0x23u
            memory[0x034E] = 0xE4u
            memory[0x034F] = 0x01u
            memory[0x0350] = 0x00u
            memory[0x0351] = 0x23u
            memory[0x0352] = 0xECu
            memory[0x0353] = 0x01u
            memory[0x0354] = 0x00u
            memory[0x0355] = 0x23u
            memory[0x0356] = 0xF4u
            memory[0x0357] = 0x01u
            memory[0x0358] = 0x50u
            memory[0x0359] = 0x23u
            memory[0x035A] = 0xFCu
            memory[0x035B] = 0x01u
            memory[0x035C] = 0x05u
            memory[0x035D] = 0x00u
            memory[0x0778] = 0x11u

            // Execute decompiled function
            updateScreen()

            // Verify: Check output memory (2 addresses)
            assertEquals(0x5Du, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x03u, memory[0x0001], "Memory 0x0001 mismatch")
        }
    }

    /**
     * Test case 9 from frame 6823
     * Function: updateScreen (0x8EDA)
     * Call depth: 1
     * Memory reads: 31, writes: 5
     */
    @Test
    fun `updateScreen_frame6823_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (31 addresses)
            memory[0x0000] = 0x45u
            memory[0x0001] = 0x03u
            memory[0x01F8] = 0xABu
            memory[0x01F9] = 0x8Eu
            memory[0x01FA] = 0x02u
            memory[0x0345] = 0x27u
            memory[0x0346] = 0xD0u
            memory[0x0347] = 0x01u
            memory[0x0348] = 0x00u
            memory[0x0349] = 0x27u
            memory[0x034A] = 0xD8u
            memory[0x034B] = 0x01u
            memory[0x034C] = 0x00u
            memory[0x034D] = 0x27u
            memory[0x034E] = 0xE0u
            memory[0x034F] = 0x01u
            memory[0x0350] = 0x00u
            memory[0x0351] = 0x27u
            memory[0x0352] = 0xE8u
            memory[0x0353] = 0x01u
            memory[0x0354] = 0x00u
            memory[0x0355] = 0x27u
            memory[0x0356] = 0xF0u
            memory[0x0357] = 0x01u
            memory[0x0358] = 0x50u
            memory[0x0359] = 0x27u
            memory[0x035A] = 0xF8u
            memory[0x035B] = 0x01u
            memory[0x035C] = 0x05u
            memory[0x035D] = 0x00u
            memory[0x0778] = 0x11u

            // Execute decompiled function
            updateScreen()

            // Verify: Check output memory (2 addresses)
            assertEquals(0x5Du, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x03u, memory[0x0001], "Memory 0x0001 mismatch")
        }
    }

    // =========================================
    // 0x8F0E: outputNumbers
    // Parameters: A
    // 363 calls, 5 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 10
     * Function: outputNumbers (0x8F0E)
     * Parameters: A
     * Call depth: 6
     * Memory reads: 10, writes: 14
     */
    @Test
    fun `outputNumbers_frame10_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (10 addresses)
            memory[0x0002] = 0x05u
            memory[0x0003] = 0x06u
            memory[0x01F5] = 0x01u
            memory[0x0300] = 0x05u
            memory[0x07DD] = 0x00u
            memory[0x07DE] = 0x00u
            memory[0x07DF] = 0x00u
            memory[0x07E0] = 0x00u
            memory[0x07E1] = 0x00u
            memory[0x07E2] = 0x00u

            // Execute decompiled function
            outputNumbers(0x01)

            // Verify: Check output memory (13 addresses)
            assertEquals(0x05u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x00u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x0Eu, memory[0x0300], "Memory 0x0300 mismatch")
            assertEquals(0x20u, memory[0x0306], "Memory 0x0306 mismatch")
            assertEquals(0x62u, memory[0x0307], "Memory 0x0307 mismatch")
            assertEquals(0x06u, memory[0x0308], "Memory 0x0308 mismatch")
            assertEquals(0x00u, memory[0x0309], "Memory 0x0309 mismatch")
            assertEquals(0x00u, memory[0x030A], "Memory 0x030A mismatch")
            assertEquals(0x00u, memory[0x030B], "Memory 0x030B mismatch")
            assertEquals(0x00u, memory[0x030C], "Memory 0x030C mismatch")
            assertEquals(0x00u, memory[0x030D], "Memory 0x030D mismatch")
            assertEquals(0x00u, memory[0x030E], "Memory 0x030E mismatch")
            assertEquals(0x00u, memory[0x030F], "Memory 0x030F mismatch")
        }
    }

    /**
     * Test case 1 from frame 29
     * Function: outputNumbers (0x8F0E)
     * Parameters: A
     * Call depth: 6
     * Memory reads: 10, writes: 14
     */
    @Test
    fun `outputNumbers_frame29_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (10 addresses)
            memory[0x0002] = 0x00u
            memory[0x0003] = 0x06u
            memory[0x01F5] = 0x00u
            memory[0x0300] = 0x00u
            memory[0x07D7] = 0x00u
            memory[0x07D8] = 0x00u
            memory[0x07D9] = 0x00u
            memory[0x07DA] = 0x00u
            memory[0x07DB] = 0x00u
            memory[0x07DC] = 0x00u

            // Execute decompiled function
            outputNumbers(0x7D)

            // Verify: Check output memory (13 addresses)
            assertEquals(0x00u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x00u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x09u, memory[0x0300], "Memory 0x0300 mismatch")
            assertEquals(0x22u, memory[0x0301], "Memory 0x0301 mismatch")
            assertEquals(0xF0u, memory[0x0302], "Memory 0x0302 mismatch")
            assertEquals(0x06u, memory[0x0303], "Memory 0x0303 mismatch")
            assertEquals(0x00u, memory[0x0304], "Memory 0x0304 mismatch")
            assertEquals(0x00u, memory[0x0305], "Memory 0x0305 mismatch")
            assertEquals(0x00u, memory[0x0306], "Memory 0x0306 mismatch")
            assertEquals(0x00u, memory[0x0307], "Memory 0x0307 mismatch")
            assertEquals(0x00u, memory[0x0308], "Memory 0x0308 mismatch")
            assertEquals(0x00u, memory[0x0309], "Memory 0x0309 mismatch")
            assertEquals(0x00u, memory[0x030A], "Memory 0x030A mismatch")
        }
    }

    /**
     * Test case 2 from frame 199
     * Function: outputNumbers (0x8F0E)
     * Parameters: A
     * Call depth: 5
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `outputNumbers_frame199_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            outputNumbers(0x52)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 3 from frame 4295
     * Function: outputNumbers (0x8F0E)
     * Parameters: A
     * Call depth: 10
     * Memory reads: 10, writes: 14
     */
    @Test
    fun `outputNumbers_frame4295_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (10 addresses)
            memory[0x0002] = 0x05u
            memory[0x0003] = 0x06u
            memory[0x01F3] = 0x01u
            memory[0x0300] = 0x05u
            memory[0x07DD] = 0x00u
            memory[0x07DE] = 0x00u
            memory[0x07DF] = 0x00u
            memory[0x07E0] = 0x00u
            memory[0x07E1] = 0x01u
            memory[0x07E2] = 0x00u

            // Execute decompiled function
            outputNumbers(0x01)

            // Verify: Check output memory (13 addresses)
            assertEquals(0x05u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x00u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x0Eu, memory[0x0300], "Memory 0x0300 mismatch")
            assertEquals(0x20u, memory[0x0306], "Memory 0x0306 mismatch")
            assertEquals(0x62u, memory[0x0307], "Memory 0x0307 mismatch")
            assertEquals(0x06u, memory[0x0308], "Memory 0x0308 mismatch")
            assertEquals(0x00u, memory[0x0309], "Memory 0x0309 mismatch")
            assertEquals(0x00u, memory[0x030A], "Memory 0x030A mismatch")
            assertEquals(0x00u, memory[0x030B], "Memory 0x030B mismatch")
            assertEquals(0x00u, memory[0x030C], "Memory 0x030C mismatch")
            assertEquals(0x01u, memory[0x030D], "Memory 0x030D mismatch")
            assertEquals(0x00u, memory[0x030E], "Memory 0x030E mismatch")
            assertEquals(0x00u, memory[0x030F], "Memory 0x030F mismatch")
        }
    }

    /**
     * Test case 4 from frame 4375
     * Function: outputNumbers (0x8F0E)
     * Parameters: A
     * Call depth: 12
     * Memory reads: 10, writes: 14
     */
    @Test
    fun `outputNumbers_frame4375_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (10 addresses)
            memory[0x0002] = 0x0Fu
            memory[0x0003] = 0x06u
            memory[0x01ED] = 0x01u
            memory[0x0300] = 0x0Fu
            memory[0x07DD] = 0x00u
            memory[0x07DE] = 0x00u
            memory[0x07DF] = 0x00u
            memory[0x07E0] = 0x00u
            memory[0x07E1] = 0x03u
            memory[0x07E2] = 0x00u

            // Execute decompiled function
            outputNumbers(0x01)

            // Verify: Check output memory (13 addresses)
            assertEquals(0x0Fu, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x00u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x18u, memory[0x0300], "Memory 0x0300 mismatch")
            assertEquals(0x20u, memory[0x0310], "Memory 0x0310 mismatch")
            assertEquals(0x62u, memory[0x0311], "Memory 0x0311 mismatch")
            assertEquals(0x06u, memory[0x0312], "Memory 0x0312 mismatch")
            assertEquals(0x00u, memory[0x0313], "Memory 0x0313 mismatch")
            assertEquals(0x00u, memory[0x0314], "Memory 0x0314 mismatch")
            assertEquals(0x00u, memory[0x0315], "Memory 0x0315 mismatch")
            assertEquals(0x00u, memory[0x0316], "Memory 0x0316 mismatch")
            assertEquals(0x03u, memory[0x0317], "Memory 0x0317 mismatch")
            assertEquals(0x00u, memory[0x0318], "Memory 0x0318 mismatch")
            assertEquals(0x00u, memory[0x0319], "Memory 0x0319 mismatch")
        }
    }

    // =========================================
    // 0x8FCF: initializeGame
    // Parameters: A
    // 1 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 3
     * Function: initializeGame (0x8FCF)
     * Parameters: A
     * Call depth: 3
     * Memory reads: 2, writes: 35
     */
    @Test
    fun `initializeGame_frame3_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x01F8] = 0xD3u
            memory[0x01F9] = 0x8Fu

            // Execute decompiled function
            initializeGame(0x8F)

            // Verify: Check output memory (33 addresses)
            assertEquals(0x18u, memory[0x07A2], "Memory 0x07A2 mismatch")
            assertEquals(0x00u, memory[0x07B0], "Memory 0x07B0 mismatch")
            assertEquals(0x00u, memory[0x07B1], "Memory 0x07B1 mismatch")
            assertEquals(0x00u, memory[0x07B2], "Memory 0x07B2 mismatch")
            assertEquals(0x00u, memory[0x07B3], "Memory 0x07B3 mismatch")
            assertEquals(0x00u, memory[0x07B4], "Memory 0x07B4 mismatch")
            assertEquals(0x00u, memory[0x07B5], "Memory 0x07B5 mismatch")
            assertEquals(0x00u, memory[0x07B6], "Memory 0x07B6 mismatch")
            assertEquals(0x00u, memory[0x07B7], "Memory 0x07B7 mismatch")
            assertEquals(0x00u, memory[0x07B8], "Memory 0x07B8 mismatch")
            assertEquals(0x00u, memory[0x07B9], "Memory 0x07B9 mismatch")
            assertEquals(0x00u, memory[0x07BA], "Memory 0x07BA mismatch")
            assertEquals(0x00u, memory[0x07BB], "Memory 0x07BB mismatch")
            assertEquals(0x00u, memory[0x07BC], "Memory 0x07BC mismatch")
            assertEquals(0x00u, memory[0x07BD], "Memory 0x07BD mismatch")
            assertEquals(0x00u, memory[0x07BE], "Memory 0x07BE mismatch")
            assertEquals(0x00u, memory[0x07BF], "Memory 0x07BF mismatch")
            assertEquals(0x00u, memory[0x07C0], "Memory 0x07C0 mismatch")
            assertEquals(0x00u, memory[0x07C1], "Memory 0x07C1 mismatch")
            assertEquals(0x00u, memory[0x07C2], "Memory 0x07C2 mismatch")
            assertEquals(0x00u, memory[0x07C3], "Memory 0x07C3 mismatch")
            assertEquals(0x00u, memory[0x07C4], "Memory 0x07C4 mismatch")
            assertEquals(0x00u, memory[0x07C5], "Memory 0x07C5 mismatch")
            assertEquals(0x00u, memory[0x07C6], "Memory 0x07C6 mismatch")
            assertEquals(0x00u, memory[0x07C7], "Memory 0x07C7 mismatch")
            assertEquals(0x00u, memory[0x07C8], "Memory 0x07C8 mismatch")
            assertEquals(0x00u, memory[0x07C9], "Memory 0x07C9 mismatch")
            assertEquals(0x00u, memory[0x07CA], "Memory 0x07CA mismatch")
            assertEquals(0x00u, memory[0x07CB], "Memory 0x07CB mismatch")
            assertEquals(0x00u, memory[0x07CC], "Memory 0x07CC mismatch")
            assertEquals(0x00u, memory[0x07CD], "Memory 0x07CD mismatch")
            assertEquals(0x00u, memory[0x07CE], "Memory 0x07CE mismatch")
            assertEquals(0x00u, memory[0x07CF], "Memory 0x07CF mismatch")
        }
    }

    // =========================================
    // 0x8FE1: initializeArea
    // 1 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 5
     * Function: initializeArea (0x8FE1)
     * Call depth: 3
     * Memory reads: 10, writes: 49
     */
    @Test
    fun `initializeArea_frame5_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (10 addresses)
            memory[0x01F8] = 0xE3u
            memory[0x01F9] = 0x8Fu
            memory[0x0730] = 0x00u
            memory[0x0731] = 0x00u
            memory[0x0732] = 0x00u
            memory[0x0752] = 0x00u
            memory[0x075B] = 0x00u
            memory[0x075F] = 0x00u
            memory[0x076A] = 0x00u
            memory[0x0772] = 0x00u

            // Execute decompiled function
            initializeArea()

            // Verify: Check output memory (47 addresses)
            assertEquals(0x80u, memory[0x00FB], "Memory 0x00FB mismatch")
            assertEquals(0x00u, memory[0x06A0], "Memory 0x06A0 mismatch")
            assertEquals(0x00u, memory[0x071A], "Memory 0x071A mismatch")
            assertEquals(0x0Bu, memory[0x071E], "Memory 0x071E mismatch")
            assertEquals(0x20u, memory[0x0720], "Memory 0x0720 mismatch")
            assertEquals(0x80u, memory[0x0721], "Memory 0x0721 mismatch")
            assertEquals(0x00u, memory[0x0725], "Memory 0x0725 mismatch")
            assertEquals(0x00u, memory[0x0728], "Memory 0x0728 mismatch")
            assertEquals(0xFFu, memory[0x0730], "Memory 0x0730 mismatch")
            assertEquals(0xFFu, memory[0x0731], "Memory 0x0731 mismatch")
            assertEquals(0xFFu, memory[0x0732], "Memory 0x0732 mismatch")
            assertEquals(0x01u, memory[0x0772], "Memory 0x0772 mismatch")
            assertEquals(0x01u, memory[0x0774], "Memory 0x0774 mismatch")
            assertEquals(0x00u, memory[0x0780], "Memory 0x0780 mismatch")
            assertEquals(0x00u, memory[0x0781], "Memory 0x0781 mismatch")
            assertEquals(0x00u, memory[0x0782], "Memory 0x0782 mismatch")
            assertEquals(0x00u, memory[0x0783], "Memory 0x0783 mismatch")
            assertEquals(0x00u, memory[0x0784], "Memory 0x0784 mismatch")
            assertEquals(0x00u, memory[0x0785], "Memory 0x0785 mismatch")
            assertEquals(0x00u, memory[0x0786], "Memory 0x0786 mismatch")
            assertEquals(0x00u, memory[0x0787], "Memory 0x0787 mismatch")
            assertEquals(0x00u, memory[0x0788], "Memory 0x0788 mismatch")
            assertEquals(0x00u, memory[0x0789], "Memory 0x0789 mismatch")
            assertEquals(0x00u, memory[0x078A], "Memory 0x078A mismatch")
            assertEquals(0x00u, memory[0x078B], "Memory 0x078B mismatch")
            assertEquals(0x00u, memory[0x078C], "Memory 0x078C mismatch")
            assertEquals(0x00u, memory[0x078D], "Memory 0x078D mismatch")
            assertEquals(0x00u, memory[0x078E], "Memory 0x078E mismatch")
            assertEquals(0x00u, memory[0x078F], "Memory 0x078F mismatch")
            assertEquals(0x00u, memory[0x0790], "Memory 0x0790 mismatch")
            assertEquals(0x00u, memory[0x0791], "Memory 0x0791 mismatch")
            assertEquals(0x00u, memory[0x0792], "Memory 0x0792 mismatch")
            assertEquals(0x00u, memory[0x0793], "Memory 0x0793 mismatch")
            assertEquals(0x00u, memory[0x0794], "Memory 0x0794 mismatch")
            assertEquals(0x00u, memory[0x0795], "Memory 0x0795 mismatch")
            assertEquals(0x00u, memory[0x0796], "Memory 0x0796 mismatch")
            assertEquals(0x00u, memory[0x0797], "Memory 0x0797 mismatch")
            assertEquals(0x00u, memory[0x0798], "Memory 0x0798 mismatch")
            assertEquals(0x00u, memory[0x0799], "Memory 0x0799 mismatch")
            assertEquals(0x00u, memory[0x079A], "Memory 0x079A mismatch")
            assertEquals(0x00u, memory[0x079B], "Memory 0x079B mismatch")
            assertEquals(0x00u, memory[0x079C], "Memory 0x079C mismatch")
            assertEquals(0x00u, memory[0x079D], "Memory 0x079D mismatch")
            assertEquals(0x00u, memory[0x079E], "Memory 0x079E mismatch")
            assertEquals(0x00u, memory[0x079F], "Memory 0x079F mismatch")
            assertEquals(0x00u, memory[0x07A0], "Memory 0x07A0 mismatch")
            assertEquals(0x00u, memory[0x07A1], "Memory 0x07A1 mismatch")
        }
    }

    // =========================================
    // 0x906E: secondaryGameSetup
    // 1 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 30
     * Function: secondaryGameSetup (0x906E)
     * Call depth: 3
     * Memory reads: 6, writes: 288
     */
    @Test
    fun `secondaryGameSetup_frame30_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (6 addresses)
            memory[0x01F8] = 0x99u
            memory[0x01F9] = 0x90u
            memory[0x071A] = 0x00u
            memory[0x0722] = 0x00u
            memory[0x0772] = 0x02u
            memory[0x0778] = 0x10u

            // Execute decompiled function
            secondaryGameSetup()

            // Verify: Check output memory (286 addresses)
            assertEquals(0x18u, memory[0x0200], "Memory 0x0200 mismatch")
            assertEquals(0xFFu, memory[0x0201], "Memory 0x0201 mismatch")
            assertEquals(0x23u, memory[0x0202], "Memory 0x0202 mismatch")
            assertEquals(0x58u, memory[0x0203], "Memory 0x0203 mismatch")
            assertEquals(0x00u, memory[0x0300], "Memory 0x0300 mismatch")
            assertEquals(0x00u, memory[0x0301], "Memory 0x0301 mismatch")
            assertEquals(0x00u, memory[0x0302], "Memory 0x0302 mismatch")
            assertEquals(0x00u, memory[0x0303], "Memory 0x0303 mismatch")
            assertEquals(0x00u, memory[0x0304], "Memory 0x0304 mismatch")
            assertEquals(0x00u, memory[0x0305], "Memory 0x0305 mismatch")
            assertEquals(0x00u, memory[0x0306], "Memory 0x0306 mismatch")
            assertEquals(0x00u, memory[0x0307], "Memory 0x0307 mismatch")
            assertEquals(0x00u, memory[0x0308], "Memory 0x0308 mismatch")
            assertEquals(0x00u, memory[0x0309], "Memory 0x0309 mismatch")
            assertEquals(0x00u, memory[0x030A], "Memory 0x030A mismatch")
            assertEquals(0x00u, memory[0x030B], "Memory 0x030B mismatch")
            assertEquals(0x00u, memory[0x030C], "Memory 0x030C mismatch")
            assertEquals(0x00u, memory[0x030D], "Memory 0x030D mismatch")
            assertEquals(0x00u, memory[0x030E], "Memory 0x030E mismatch")
            assertEquals(0x00u, memory[0x030F], "Memory 0x030F mismatch")
            assertEquals(0x00u, memory[0x0310], "Memory 0x0310 mismatch")
            assertEquals(0x00u, memory[0x0311], "Memory 0x0311 mismatch")
            assertEquals(0x00u, memory[0x0312], "Memory 0x0312 mismatch")
            assertEquals(0x00u, memory[0x0313], "Memory 0x0313 mismatch")
            assertEquals(0x00u, memory[0x0314], "Memory 0x0314 mismatch")
            assertEquals(0x00u, memory[0x0315], "Memory 0x0315 mismatch")
            assertEquals(0x00u, memory[0x0316], "Memory 0x0316 mismatch")
            assertEquals(0x00u, memory[0x0317], "Memory 0x0317 mismatch")
            assertEquals(0x00u, memory[0x0318], "Memory 0x0318 mismatch")
            assertEquals(0x00u, memory[0x0319], "Memory 0x0319 mismatch")
            assertEquals(0x00u, memory[0x031A], "Memory 0x031A mismatch")
            assertEquals(0x00u, memory[0x031B], "Memory 0x031B mismatch")
            assertEquals(0x00u, memory[0x031C], "Memory 0x031C mismatch")
            assertEquals(0x00u, memory[0x031D], "Memory 0x031D mismatch")
            assertEquals(0x00u, memory[0x031E], "Memory 0x031E mismatch")
            assertEquals(0x00u, memory[0x031F], "Memory 0x031F mismatch")
            assertEquals(0x00u, memory[0x0320], "Memory 0x0320 mismatch")
            assertEquals(0x00u, memory[0x0321], "Memory 0x0321 mismatch")
            assertEquals(0x00u, memory[0x0322], "Memory 0x0322 mismatch")
            assertEquals(0x00u, memory[0x0323], "Memory 0x0323 mismatch")
            assertEquals(0x00u, memory[0x0324], "Memory 0x0324 mismatch")
            assertEquals(0x00u, memory[0x0325], "Memory 0x0325 mismatch")
            assertEquals(0x00u, memory[0x0326], "Memory 0x0326 mismatch")
            assertEquals(0x00u, memory[0x0327], "Memory 0x0327 mismatch")
            assertEquals(0x00u, memory[0x0328], "Memory 0x0328 mismatch")
            assertEquals(0x00u, memory[0x0329], "Memory 0x0329 mismatch")
            assertEquals(0x00u, memory[0x032A], "Memory 0x032A mismatch")
            assertEquals(0x00u, memory[0x032B], "Memory 0x032B mismatch")
            assertEquals(0x00u, memory[0x032C], "Memory 0x032C mismatch")
            assertEquals(0x00u, memory[0x032D], "Memory 0x032D mismatch")
            assertEquals(0x00u, memory[0x032E], "Memory 0x032E mismatch")
            assertEquals(0x00u, memory[0x032F], "Memory 0x032F mismatch")
            assertEquals(0x00u, memory[0x0330], "Memory 0x0330 mismatch")
            assertEquals(0x00u, memory[0x0331], "Memory 0x0331 mismatch")
            assertEquals(0x00u, memory[0x0332], "Memory 0x0332 mismatch")
            assertEquals(0x00u, memory[0x0333], "Memory 0x0333 mismatch")
            assertEquals(0x00u, memory[0x0334], "Memory 0x0334 mismatch")
            assertEquals(0x00u, memory[0x0335], "Memory 0x0335 mismatch")
            assertEquals(0x00u, memory[0x0336], "Memory 0x0336 mismatch")
            assertEquals(0x00u, memory[0x0337], "Memory 0x0337 mismatch")
            assertEquals(0x00u, memory[0x0338], "Memory 0x0338 mismatch")
            assertEquals(0x00u, memory[0x0339], "Memory 0x0339 mismatch")
            assertEquals(0x00u, memory[0x033A], "Memory 0x033A mismatch")
            assertEquals(0x00u, memory[0x033B], "Memory 0x033B mismatch")
            assertEquals(0x00u, memory[0x033C], "Memory 0x033C mismatch")
            assertEquals(0x00u, memory[0x033D], "Memory 0x033D mismatch")
            assertEquals(0x00u, memory[0x033E], "Memory 0x033E mismatch")
            assertEquals(0x00u, memory[0x033F], "Memory 0x033F mismatch")
            assertEquals(0x00u, memory[0x0340], "Memory 0x0340 mismatch")
            assertEquals(0x00u, memory[0x0341], "Memory 0x0341 mismatch")
            assertEquals(0x00u, memory[0x0342], "Memory 0x0342 mismatch")
            assertEquals(0x00u, memory[0x0343], "Memory 0x0343 mismatch")
            assertEquals(0x00u, memory[0x0344], "Memory 0x0344 mismatch")
            assertEquals(0x00u, memory[0x0345], "Memory 0x0345 mismatch")
            assertEquals(0x00u, memory[0x0346], "Memory 0x0346 mismatch")
            assertEquals(0x00u, memory[0x0347], "Memory 0x0347 mismatch")
            assertEquals(0x00u, memory[0x0348], "Memory 0x0348 mismatch")
            assertEquals(0x00u, memory[0x0349], "Memory 0x0349 mismatch")
            assertEquals(0x00u, memory[0x034A], "Memory 0x034A mismatch")
            assertEquals(0x00u, memory[0x034B], "Memory 0x034B mismatch")
            assertEquals(0x00u, memory[0x034C], "Memory 0x034C mismatch")
            assertEquals(0x00u, memory[0x034D], "Memory 0x034D mismatch")
            assertEquals(0x00u, memory[0x034E], "Memory 0x034E mismatch")
            assertEquals(0x00u, memory[0x034F], "Memory 0x034F mismatch")
            assertEquals(0x00u, memory[0x0350], "Memory 0x0350 mismatch")
            assertEquals(0x00u, memory[0x0351], "Memory 0x0351 mismatch")
            assertEquals(0x00u, memory[0x0352], "Memory 0x0352 mismatch")
            assertEquals(0x00u, memory[0x0353], "Memory 0x0353 mismatch")
            assertEquals(0x00u, memory[0x0354], "Memory 0x0354 mismatch")
            assertEquals(0x00u, memory[0x0355], "Memory 0x0355 mismatch")
            assertEquals(0x00u, memory[0x0356], "Memory 0x0356 mismatch")
            assertEquals(0x00u, memory[0x0357], "Memory 0x0357 mismatch")
            assertEquals(0x00u, memory[0x0358], "Memory 0x0358 mismatch")
            assertEquals(0x00u, memory[0x0359], "Memory 0x0359 mismatch")
            assertEquals(0x00u, memory[0x035A], "Memory 0x035A mismatch")
            assertEquals(0x00u, memory[0x035B], "Memory 0x035B mismatch")
            assertEquals(0x00u, memory[0x035C], "Memory 0x035C mismatch")
            assertEquals(0x00u, memory[0x035D], "Memory 0x035D mismatch")
            assertEquals(0x00u, memory[0x035E], "Memory 0x035E mismatch")
            assertEquals(0x00u, memory[0x035F], "Memory 0x035F mismatch")
            assertEquals(0x00u, memory[0x0360], "Memory 0x0360 mismatch")
            assertEquals(0x00u, memory[0x0361], "Memory 0x0361 mismatch")
            assertEquals(0x00u, memory[0x0362], "Memory 0x0362 mismatch")
            assertEquals(0x00u, memory[0x0363], "Memory 0x0363 mismatch")
            assertEquals(0x00u, memory[0x0364], "Memory 0x0364 mismatch")
            assertEquals(0x00u, memory[0x0365], "Memory 0x0365 mismatch")
            assertEquals(0x00u, memory[0x0366], "Memory 0x0366 mismatch")
            assertEquals(0x00u, memory[0x0367], "Memory 0x0367 mismatch")
            assertEquals(0x00u, memory[0x0368], "Memory 0x0368 mismatch")
            assertEquals(0x00u, memory[0x0369], "Memory 0x0369 mismatch")
            assertEquals(0x00u, memory[0x036A], "Memory 0x036A mismatch")
            assertEquals(0x00u, memory[0x036B], "Memory 0x036B mismatch")
            assertEquals(0x00u, memory[0x036C], "Memory 0x036C mismatch")
            assertEquals(0x00u, memory[0x036D], "Memory 0x036D mismatch")
            assertEquals(0x00u, memory[0x036E], "Memory 0x036E mismatch")
            assertEquals(0x00u, memory[0x036F], "Memory 0x036F mismatch")
            assertEquals(0x00u, memory[0x0370], "Memory 0x0370 mismatch")
            assertEquals(0x00u, memory[0x0371], "Memory 0x0371 mismatch")
            assertEquals(0x00u, memory[0x0372], "Memory 0x0372 mismatch")
            assertEquals(0x00u, memory[0x0373], "Memory 0x0373 mismatch")
            assertEquals(0x00u, memory[0x0374], "Memory 0x0374 mismatch")
            assertEquals(0x00u, memory[0x0375], "Memory 0x0375 mismatch")
            assertEquals(0x00u, memory[0x0376], "Memory 0x0376 mismatch")
            assertEquals(0x00u, memory[0x0377], "Memory 0x0377 mismatch")
            assertEquals(0x00u, memory[0x0378], "Memory 0x0378 mismatch")
            assertEquals(0x00u, memory[0x0379], "Memory 0x0379 mismatch")
            assertEquals(0x00u, memory[0x037A], "Memory 0x037A mismatch")
            assertEquals(0x00u, memory[0x037B], "Memory 0x037B mismatch")
            assertEquals(0x00u, memory[0x037C], "Memory 0x037C mismatch")
            assertEquals(0x00u, memory[0x037D], "Memory 0x037D mismatch")
            assertEquals(0x00u, memory[0x037E], "Memory 0x037E mismatch")
            assertEquals(0x00u, memory[0x037F], "Memory 0x037F mismatch")
            assertEquals(0x00u, memory[0x0380], "Memory 0x0380 mismatch")
            assertEquals(0x00u, memory[0x0381], "Memory 0x0381 mismatch")
            assertEquals(0x00u, memory[0x0382], "Memory 0x0382 mismatch")
            assertEquals(0x00u, memory[0x0383], "Memory 0x0383 mismatch")
            assertEquals(0x00u, memory[0x0384], "Memory 0x0384 mismatch")
            assertEquals(0x00u, memory[0x0385], "Memory 0x0385 mismatch")
            assertEquals(0x00u, memory[0x0386], "Memory 0x0386 mismatch")
            assertEquals(0x00u, memory[0x0387], "Memory 0x0387 mismatch")
            assertEquals(0x00u, memory[0x0388], "Memory 0x0388 mismatch")
            assertEquals(0x00u, memory[0x0389], "Memory 0x0389 mismatch")
            assertEquals(0x00u, memory[0x038A], "Memory 0x038A mismatch")
            assertEquals(0x00u, memory[0x038B], "Memory 0x038B mismatch")
            assertEquals(0x00u, memory[0x038C], "Memory 0x038C mismatch")
            assertEquals(0x00u, memory[0x038D], "Memory 0x038D mismatch")
            assertEquals(0x00u, memory[0x038E], "Memory 0x038E mismatch")
            assertEquals(0x00u, memory[0x038F], "Memory 0x038F mismatch")
            assertEquals(0x00u, memory[0x0390], "Memory 0x0390 mismatch")
            assertEquals(0x00u, memory[0x0391], "Memory 0x0391 mismatch")
            assertEquals(0x00u, memory[0x0392], "Memory 0x0392 mismatch")
            assertEquals(0x00u, memory[0x0393], "Memory 0x0393 mismatch")
            assertEquals(0x00u, memory[0x0394], "Memory 0x0394 mismatch")
            assertEquals(0x00u, memory[0x0395], "Memory 0x0395 mismatch")
            assertEquals(0x00u, memory[0x0396], "Memory 0x0396 mismatch")
            assertEquals(0x00u, memory[0x0397], "Memory 0x0397 mismatch")
            assertEquals(0x00u, memory[0x0398], "Memory 0x0398 mismatch")
            assertEquals(0x00u, memory[0x0399], "Memory 0x0399 mismatch")
            assertEquals(0x00u, memory[0x039A], "Memory 0x039A mismatch")
            assertEquals(0x00u, memory[0x039B], "Memory 0x039B mismatch")
            assertEquals(0x00u, memory[0x039C], "Memory 0x039C mismatch")
            assertEquals(0x00u, memory[0x039D], "Memory 0x039D mismatch")
            assertEquals(0x00u, memory[0x039E], "Memory 0x039E mismatch")
            assertEquals(0x00u, memory[0x039F], "Memory 0x039F mismatch")
            assertEquals(0xFFu, memory[0x03A0], "Memory 0x03A0 mismatch")
            assertEquals(0x00u, memory[0x03A1], "Memory 0x03A1 mismatch")
            assertEquals(0x00u, memory[0x03A2], "Memory 0x03A2 mismatch")
            assertEquals(0x00u, memory[0x03A3], "Memory 0x03A3 mismatch")
            assertEquals(0x00u, memory[0x03A4], "Memory 0x03A4 mismatch")
            assertEquals(0x00u, memory[0x03A5], "Memory 0x03A5 mismatch")
            assertEquals(0x00u, memory[0x03A6], "Memory 0x03A6 mismatch")
            assertEquals(0x00u, memory[0x03A7], "Memory 0x03A7 mismatch")
            assertEquals(0x00u, memory[0x03A8], "Memory 0x03A8 mismatch")
            assertEquals(0x00u, memory[0x03A9], "Memory 0x03A9 mismatch")
            assertEquals(0x00u, memory[0x03AA], "Memory 0x03AA mismatch")
            assertEquals(0x00u, memory[0x03AB], "Memory 0x03AB mismatch")
            assertEquals(0x00u, memory[0x03AC], "Memory 0x03AC mismatch")
            assertEquals(0x00u, memory[0x03AD], "Memory 0x03AD mismatch")
            assertEquals(0x00u, memory[0x03AE], "Memory 0x03AE mismatch")
            assertEquals(0x00u, memory[0x03AF], "Memory 0x03AF mismatch")
            assertEquals(0x00u, memory[0x03B0], "Memory 0x03B0 mismatch")
            assertEquals(0x00u, memory[0x03B1], "Memory 0x03B1 mismatch")
            assertEquals(0x00u, memory[0x03B2], "Memory 0x03B2 mismatch")
            assertEquals(0x00u, memory[0x03B3], "Memory 0x03B3 mismatch")
            assertEquals(0x00u, memory[0x03B4], "Memory 0x03B4 mismatch")
            assertEquals(0x00u, memory[0x03B5], "Memory 0x03B5 mismatch")
            assertEquals(0x00u, memory[0x03B6], "Memory 0x03B6 mismatch")
            assertEquals(0x00u, memory[0x03B7], "Memory 0x03B7 mismatch")
            assertEquals(0x00u, memory[0x03B8], "Memory 0x03B8 mismatch")
            assertEquals(0x00u, memory[0x03B9], "Memory 0x03B9 mismatch")
            assertEquals(0x00u, memory[0x03BA], "Memory 0x03BA mismatch")
            assertEquals(0x00u, memory[0x03BB], "Memory 0x03BB mismatch")
            assertEquals(0x00u, memory[0x03BC], "Memory 0x03BC mismatch")
            assertEquals(0x00u, memory[0x03BD], "Memory 0x03BD mismatch")
            assertEquals(0x00u, memory[0x03BE], "Memory 0x03BE mismatch")
            assertEquals(0x00u, memory[0x03BF], "Memory 0x03BF mismatch")
            assertEquals(0x00u, memory[0x03C0], "Memory 0x03C0 mismatch")
            assertEquals(0x00u, memory[0x03C1], "Memory 0x03C1 mismatch")
            assertEquals(0x00u, memory[0x03C2], "Memory 0x03C2 mismatch")
            assertEquals(0x00u, memory[0x03C3], "Memory 0x03C3 mismatch")
            assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
            assertEquals(0x00u, memory[0x03C5], "Memory 0x03C5 mismatch")
            assertEquals(0x00u, memory[0x03C6], "Memory 0x03C6 mismatch")
            assertEquals(0x00u, memory[0x03C7], "Memory 0x03C7 mismatch")
            assertEquals(0x00u, memory[0x03C8], "Memory 0x03C8 mismatch")
            assertEquals(0x00u, memory[0x03C9], "Memory 0x03C9 mismatch")
            assertEquals(0x00u, memory[0x03CA], "Memory 0x03CA mismatch")
            assertEquals(0x00u, memory[0x03CB], "Memory 0x03CB mismatch")
            assertEquals(0x00u, memory[0x03CC], "Memory 0x03CC mismatch")
            assertEquals(0x00u, memory[0x03CD], "Memory 0x03CD mismatch")
            assertEquals(0x00u, memory[0x03CE], "Memory 0x03CE mismatch")
            assertEquals(0x00u, memory[0x03CF], "Memory 0x03CF mismatch")
            assertEquals(0x00u, memory[0x03D0], "Memory 0x03D0 mismatch")
            assertEquals(0x00u, memory[0x03D1], "Memory 0x03D1 mismatch")
            assertEquals(0x00u, memory[0x03D2], "Memory 0x03D2 mismatch")
            assertEquals(0x00u, memory[0x03D3], "Memory 0x03D3 mismatch")
            assertEquals(0x00u, memory[0x03D4], "Memory 0x03D4 mismatch")
            assertEquals(0x00u, memory[0x03D5], "Memory 0x03D5 mismatch")
            assertEquals(0x00u, memory[0x03D6], "Memory 0x03D6 mismatch")
            assertEquals(0x00u, memory[0x03D7], "Memory 0x03D7 mismatch")
            assertEquals(0x00u, memory[0x03D8], "Memory 0x03D8 mismatch")
            assertEquals(0x00u, memory[0x03D9], "Memory 0x03D9 mismatch")
            assertEquals(0x00u, memory[0x03DA], "Memory 0x03DA mismatch")
            assertEquals(0x00u, memory[0x03DB], "Memory 0x03DB mismatch")
            assertEquals(0x00u, memory[0x03DC], "Memory 0x03DC mismatch")
            assertEquals(0x00u, memory[0x03DD], "Memory 0x03DD mismatch")
            assertEquals(0x00u, memory[0x03DE], "Memory 0x03DE mismatch")
            assertEquals(0x00u, memory[0x03DF], "Memory 0x03DF mismatch")
            assertEquals(0x00u, memory[0x03E0], "Memory 0x03E0 mismatch")
            assertEquals(0x00u, memory[0x03E1], "Memory 0x03E1 mismatch")
            assertEquals(0x00u, memory[0x03E2], "Memory 0x03E2 mismatch")
            assertEquals(0x00u, memory[0x03E3], "Memory 0x03E3 mismatch")
            assertEquals(0x00u, memory[0x03E4], "Memory 0x03E4 mismatch")
            assertEquals(0x00u, memory[0x03E5], "Memory 0x03E5 mismatch")
            assertEquals(0x00u, memory[0x03E6], "Memory 0x03E6 mismatch")
            assertEquals(0x00u, memory[0x03E7], "Memory 0x03E7 mismatch")
            assertEquals(0x00u, memory[0x03E8], "Memory 0x03E8 mismatch")
            assertEquals(0x00u, memory[0x03E9], "Memory 0x03E9 mismatch")
            assertEquals(0x00u, memory[0x03EA], "Memory 0x03EA mismatch")
            assertEquals(0x00u, memory[0x03EB], "Memory 0x03EB mismatch")
            assertEquals(0x00u, memory[0x03EC], "Memory 0x03EC mismatch")
            assertEquals(0x00u, memory[0x03ED], "Memory 0x03ED mismatch")
            assertEquals(0x00u, memory[0x03EE], "Memory 0x03EE mismatch")
            assertEquals(0x00u, memory[0x03EF], "Memory 0x03EF mismatch")
            assertEquals(0x00u, memory[0x03F0], "Memory 0x03F0 mismatch")
            assertEquals(0x00u, memory[0x03F1], "Memory 0x03F1 mismatch")
            assertEquals(0x00u, memory[0x03F2], "Memory 0x03F2 mismatch")
            assertEquals(0x00u, memory[0x03F3], "Memory 0x03F3 mismatch")
            assertEquals(0x00u, memory[0x03F4], "Memory 0x03F4 mismatch")
            assertEquals(0x00u, memory[0x03F5], "Memory 0x03F5 mismatch")
            assertEquals(0x00u, memory[0x03F6], "Memory 0x03F6 mismatch")
            assertEquals(0x00u, memory[0x03F7], "Memory 0x03F7 mismatch")
            assertEquals(0x00u, memory[0x03F8], "Memory 0x03F8 mismatch")
            assertEquals(0x00u, memory[0x03F9], "Memory 0x03F9 mismatch")
            assertEquals(0x00u, memory[0x03FA], "Memory 0x03FA mismatch")
            assertEquals(0x00u, memory[0x03FB], "Memory 0x03FB mismatch")
            assertEquals(0x00u, memory[0x03FC], "Memory 0x03FC mismatch")
            assertEquals(0x00u, memory[0x03FD], "Memory 0x03FD mismatch")
            assertEquals(0x00u, memory[0x03FE], "Memory 0x03FE mismatch")
            assertEquals(0x00u, memory[0x03FF], "Memory 0x03FF mismatch")
            assertEquals(0x58u, memory[0x06E1], "Memory 0x06E1 mismatch")
            assertEquals(0x48u, memory[0x06E2], "Memory 0x06E2 mismatch")
            assertEquals(0x38u, memory[0x06E3], "Memory 0x06E3 mismatch")
            assertEquals(0x04u, memory[0x06E4], "Memory 0x06E4 mismatch")
            assertEquals(0x30u, memory[0x06E5], "Memory 0x06E5 mismatch")
            assertEquals(0x48u, memory[0x06E6], "Memory 0x06E6 mismatch")
            assertEquals(0x60u, memory[0x06E7], "Memory 0x06E7 mismatch")
            assertEquals(0x78u, memory[0x06E8], "Memory 0x06E8 mismatch")
            assertEquals(0x90u, memory[0x06E9], "Memory 0x06E9 mismatch")
            assertEquals(0xA8u, memory[0x06EA], "Memory 0x06EA mismatch")
            assertEquals(0xC0u, memory[0x06EB], "Memory 0x06EB mismatch")
            assertEquals(0xD8u, memory[0x06EC], "Memory 0x06EC mismatch")
            assertEquals(0xE8u, memory[0x06ED], "Memory 0x06ED mismatch")
            assertEquals(0x24u, memory[0x06EE], "Memory 0x06EE mismatch")
            assertEquals(0xF8u, memory[0x06EF], "Memory 0x06EF mismatch")
            assertEquals(0xFCu, memory[0x06F0], "Memory 0x06F0 mismatch")
            assertEquals(0x28u, memory[0x06F1], "Memory 0x06F1 mismatch")
            assertEquals(0x2Cu, memory[0x06F2], "Memory 0x06F2 mismatch")
            assertEquals(0x01u, memory[0x0722], "Memory 0x0722 mismatch")
            assertEquals(0x00u, memory[0x0728], "Memory 0x0728 mismatch")
            assertEquals(0x00u, memory[0x0759], "Memory 0x0759 mismatch")
            assertEquals(0x02u, memory[0x0761], "Memory 0x0761 mismatch")
            assertEquals(0x00u, memory[0x0769], "Memory 0x0769 mismatch")
            assertEquals(0x03u, memory[0x0772], "Memory 0x0772 mismatch")
            assertEquals(0x00u, memory[0x0774], "Memory 0x0774 mismatch")
            assertEquals(0x10u, memory[0x0778], "Memory 0x0778 mismatch")
        }
    }

    // =========================================
    // 0x9215: gameOverMode
    // 1 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 1962
     * Function: gameOverMode (0x9215)
     * Call depth: 5
     * Memory reads: 3, writes: 9
     */
    @Test
    fun `gameOverMode_frame1962_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01F6] = 0x66u
            memory[0x01F7] = 0x92u
            memory[0x0757] = 0x00u

            // Execute decompiled function
            gameOverMode()

            // Verify: Check output memory (7 addresses)
            assertEquals(0x00u, memory[0x000E], "Memory 0x000E mismatch")
            assertEquals(0x00u, memory[0x0747], "Memory 0x0747 mismatch")
            assertEquals(0x01u, memory[0x0754], "Memory 0x0754 mismatch")
            assertEquals(0x00u, memory[0x0756], "Memory 0x0756 mismatch")
            assertEquals(0x01u, memory[0x0757], "Memory 0x0757 mismatch")
            assertEquals(0x01u, memory[0x0770], "Memory 0x0770 mismatch")
            assertEquals(0x00u, memory[0x0772], "Memory 0x0772 mismatch")
        }
    }

    // =========================================
    // 0x9586: incAreaObjOffset
    // 112 calls, 11 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 13
     * Function: incAreaObjOffset (0x9586)
     * Call depth: 8
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `incAreaObjOffset_frame13_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            incAreaObjOffset()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 21
     * Function: incAreaObjOffset (0x9586)
     * Call depth: 9
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `incAreaObjOffset_frame21_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            incAreaObjOffset()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 2 from frame 23
     * Function: incAreaObjOffset (0x9586)
     * Call depth: 9
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `incAreaObjOffset_frame23_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            incAreaObjOffset()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 3 from frame 23
     * Function: incAreaObjOffset (0x9586)
     * Call depth: 15
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `incAreaObjOffset_frame23_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            incAreaObjOffset()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 4 from frame 24
     * Function: incAreaObjOffset (0x9586)
     * Call depth: 10
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `incAreaObjOffset_frame24_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            incAreaObjOffset()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 5 from frame 24
     * Function: incAreaObjOffset (0x9586)
     * Call depth: 16
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `incAreaObjOffset_frame24_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            incAreaObjOffset()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 6 from frame 1251
     * Function: incAreaObjOffset (0x9586)
     * Call depth: 12
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `incAreaObjOffset_frame1251_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            incAreaObjOffset()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 7 from frame 4387
     * Function: incAreaObjOffset (0x9586)
     * Call depth: 9
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `incAreaObjOffset_frame4387_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            incAreaObjOffset()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 8 from frame 4589
     * Function: incAreaObjOffset (0x9586)
     * Call depth: 10
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `incAreaObjOffset_frame4589_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            incAreaObjOffset()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 9 from frame 6667
     * Function: incAreaObjOffset (0x9586)
     * Call depth: 10
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `incAreaObjOffset_frame6667_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            incAreaObjOffset()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0x9613: normObj
    // Parameters: A, X
    // 168 calls, 103 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 21
     * Function: normObj (0x9613)
     * Parameters: A, X
     * Call depth: 10
     * Memory reads: 8, writes: 1
     */
    @Test
    fun `normObj_frame21_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (8 addresses)
            memory[0x00E7] = 0x90u
            memory[0x00E8] = 0xA6u
            memory[0x0725] = 0x01u
            memory[0x0726] = 0x00u
            memory[0x0728] = 0x00u
            memory[0x072A] = 0x01u
            memory[0x072C] = 0x02u
            memory[0x0731] = 0xFFu

            // Execute decompiled function
            normObj(0x10, 0x01)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x02u, memory[0x0000], "Memory 0x0000 mismatch")
        }
    }

    /**
     * Test case 1 from frame 22
     * Function: normObj (0x9613)
     * Parameters: A, X
     * Call depth: 13
     * Memory reads: 8, writes: 1
     */
    @Test
    fun `normObj_frame22_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (8 addresses)
            memory[0x00E7] = 0x90u
            memory[0x00E8] = 0xA6u
            memory[0x0725] = 0x01u
            memory[0x0726] = 0x03u
            memory[0x0728] = 0x00u
            memory[0x072A] = 0x01u
            memory[0x072C] = 0x02u
            memory[0x0730] = 0xFFu

            // Execute decompiled function
            normObj(0x10, 0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x02u, memory[0x0000], "Memory 0x0000 mismatch")
        }
    }

    /**
     * Test case 2 from frame 1131
     * Function: normObj (0x9613)
     * Parameters: A, X
     * Call depth: 10
     * Memory reads: 8, writes: 1
     */
    @Test
    fun `normObj_frame1131_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (8 addresses)
            memory[0x00E7] = 0x90u
            memory[0x00E8] = 0xA6u
            memory[0x0725] = 0x01u
            memory[0x0726] = 0x09u
            memory[0x0728] = 0x00u
            memory[0x072A] = 0x01u
            memory[0x072C] = 0x0Au
            memory[0x0731] = 0xFFu

            // Execute decompiled function
            normObj(0x38, 0x01)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x07u, memory[0x0000], "Memory 0x0000 mismatch")
        }
    }

    /**
     * Test case 3 from frame 1251
     * Function: normObj (0x9613)
     * Parameters: A, X
     * Call depth: 13
     * Memory reads: 6, writes: 1
     */
    @Test
    fun `normObj_frame1251_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (6 addresses)
            memory[0x00E7] = 0x90u
            memory[0x00E8] = 0xA6u
            memory[0x0725] = 0x01u
            memory[0x072A] = 0x02u
            memory[0x072C] = 0x0Cu
            memory[0x0730] = 0xFFu

            // Execute decompiled function
            normObj(0x38, 0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x07u, memory[0x0000], "Memory 0x0000 mismatch")
        }
    }

    /**
     * Test case 4 from frame 4421
     * Function: normObj (0x9613)
     * Parameters: A, X
     * Call depth: 10
     * Memory reads: 8, writes: 1
     */
    @Test
    fun `normObj_frame4421_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (8 addresses)
            memory[0x00E7] = 0x90u
            memory[0x00E8] = 0xA6u
            memory[0x0725] = 0x02u
            memory[0x0726] = 0x02u
            memory[0x0728] = 0x00u
            memory[0x072A] = 0x02u
            memory[0x072C] = 0x0Cu
            memory[0x0730] = 0xFFu

            // Execute decompiled function
            normObj(0x38, 0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x07u, memory[0x0000], "Memory 0x0000 mismatch")
        }
    }

    /**
     * Test case 5 from frame 4589
     * Function: normObj (0x9613)
     * Parameters: A, X
     * Call depth: 10
     * Memory reads: 12, writes: 4
     */
    @Test
    fun `normObj_frame4589_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (12 addresses)
            memory[0x0000] = 0x07u
            memory[0x0007] = 0x00u
            memory[0x00E7] = 0x90u
            memory[0x00E8] = 0xA6u
            memory[0x01F0] = 0x5Eu
            memory[0x01F1] = 0x96u
            memory[0x0725] = 0x02u
            memory[0x0726] = 0x06u
            memory[0x0728] = 0x00u
            memory[0x072A] = 0x02u
            memory[0x072C] = 0x0Cu
            memory[0x0732] = 0xFFu

            // Execute decompiled function
            normObj(0x38, 0x02)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x07u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x0Cu, memory[0x072F], "Memory 0x072F mismatch")
        }
    }

    /**
     * Test case 6 from frame 4673
     * Function: normObj (0x9613)
     * Parameters: A, X
     * Call depth: 10
     * Memory reads: 8, writes: 1
     */
    @Test
    fun `normObj_frame4673_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (8 addresses)
            memory[0x00E7] = 0x90u
            memory[0x00E8] = 0xA6u
            memory[0x0725] = 0x02u
            memory[0x0726] = 0x09u
            memory[0x0728] = 0x00u
            memory[0x072A] = 0x02u
            memory[0x072C] = 0x0Eu
            memory[0x0731] = 0xFFu

            // Execute decompiled function
            normObj(0x38, 0x01)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x07u, memory[0x0000], "Memory 0x0000 mismatch")
        }
    }

    /**
     * Test case 7 from frame 5045
     * Function: normObj (0x9613)
     * Parameters: A, X
     * Call depth: 12
     * Memory reads: 8, writes: 1
     */
    @Test
    fun `normObj_frame5045_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (8 addresses)
            memory[0x00E7] = 0x90u
            memory[0x00E8] = 0xA6u
            memory[0x0725] = 0x02u
            memory[0x0726] = 0x0Du
            memory[0x0728] = 0x00u
            memory[0x072A] = 0x02u
            memory[0x072C] = 0x0Eu
            memory[0x0732] = 0xFFu

            // Execute decompiled function
            normObj(0x38, 0x02)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x07u, memory[0x0000], "Memory 0x0000 mismatch")
        }
    }

    /**
     * Test case 8 from frame 6817
     * Function: normObj (0x9613)
     * Parameters: A, X
     * Call depth: 10
     * Memory reads: 8, writes: 1
     */
    @Test
    fun `normObj_frame6817_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (8 addresses)
            memory[0x00E7] = 0x90u
            memory[0x00E8] = 0xA6u
            memory[0x0725] = 0x03u
            memory[0x0726] = 0x01u
            memory[0x0728] = 0x00u
            memory[0x072A] = 0x03u
            memory[0x072C] = 0x10u
            memory[0x0732] = 0xFFu

            // Execute decompiled function
            normObj(0x00, 0x02)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
        }
    }

    /**
     * Test case 9 from frame 6921
     * Function: normObj (0x9613)
     * Parameters: A, X
     * Call depth: 10
     * Memory reads: 8, writes: 1
     */
    @Test
    fun `normObj_frame6921_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (8 addresses)
            memory[0x00E7] = 0x90u
            memory[0x00E8] = 0xA6u
            memory[0x0725] = 0x03u
            memory[0x0726] = 0x04u
            memory[0x0728] = 0x00u
            memory[0x072A] = 0x03u
            memory[0x072C] = 0x10u
            memory[0x0731] = 0xFFu

            // Execute decompiled function
            normObj(0x00, 0x01)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
        }
    }

    // =========================================
    // 0x9935: getPipeHeight
    // Parameters: X
    // 8 calls, 6 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 1251
     * Function: getPipeHeight (0x9935)
     * Parameters: X
     * Call depth: 13
     * Memory reads: 2, writes: 2
     */
    @Test
    fun `getPipeHeight_frame1251_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x06AB] = 0x00u
            memory[0x0735] = 0x00u

            // Execute decompiled function
            getPipeHeight(0x0A)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x14u, memory[0x06AB], "Memory 0x06AB mismatch")
            assertEquals(0x00u, memory[0x0735], "Memory 0x0735 mismatch")
        }
    }

    /**
     * Test case 1 from frame 1259
     * Function: getPipeHeight (0x9935)
     * Parameters: X
     * Call depth: 13
     * Memory reads: 2, writes: 2
     */
    @Test
    fun `getPipeHeight_frame1259_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x06AB] = 0x00u
            memory[0x0735] = 0x00u

            // Execute decompiled function
            getPipeHeight(0x0A)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x15u, memory[0x06AB], "Memory 0x06AB mismatch")
            assertEquals(0x00u, memory[0x0735], "Memory 0x0735 mismatch")
        }
    }

    /**
     * Test case 2 from frame 4589
     * Function: getPipeHeight (0x9935)
     * Parameters: X
     * Call depth: 11
     * Memory reads: 2, writes: 2
     */
    @Test
    fun `getPipeHeight_frame4589_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x06AA] = 0x00u
            memory[0x0735] = 0x01u

            // Execute decompiled function
            getPipeHeight(0x09)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x14u, memory[0x06AA], "Memory 0x06AA mismatch")
            assertEquals(0x01u, memory[0x0735], "Memory 0x0735 mismatch")
        }
    }

    /**
     * Test case 3 from frame 4597
     * Function: getPipeHeight (0x9935)
     * Parameters: X
     * Call depth: 11
     * Memory reads: 2, writes: 2
     */
    @Test
    fun `getPipeHeight_frame4597_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x06AA] = 0x00u
            memory[0x0735] = 0x01u

            // Execute decompiled function
            getPipeHeight(0x09)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x15u, memory[0x06AA], "Memory 0x06AA mismatch")
            assertEquals(0x01u, memory[0x0735], "Memory 0x0735 mismatch")
        }
    }

    /**
     * Test case 4 from frame 6667
     * Function: getPipeHeight (0x9935)
     * Parameters: X
     * Call depth: 11
     * Memory reads: 2, writes: 2
     */
    @Test
    fun `getPipeHeight_frame6667_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x06A9] = 0x00u
            memory[0x0735] = 0x02u

            // Execute decompiled function
            getPipeHeight(0x08)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x14u, memory[0x06A9], "Memory 0x06A9 mismatch")
            assertEquals(0x02u, memory[0x0735], "Memory 0x0735 mismatch")
        }
    }

    /**
     * Test case 5 from frame 6675
     * Function: getPipeHeight (0x9935)
     * Parameters: X
     * Call depth: 11
     * Memory reads: 2, writes: 2
     */
    @Test
    fun `getPipeHeight_frame6675_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x06A9] = 0x00u
            memory[0x0735] = 0x02u

            // Execute decompiled function
            getPipeHeight(0x08)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x15u, memory[0x06A9], "Memory 0x06A9 mismatch")
            assertEquals(0x02u, memory[0x0735], "Memory 0x0735 mismatch")
        }
    }

    // =========================================
    // 0x9946: findEmptyEnemySlot
    // 8 calls, 6 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 1251
     * Function: findEmptyEnemySlot (0x9946)
     * Call depth: 14
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `findEmptyEnemySlot_frame1251_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0732] = 0x01u

            // Execute decompiled function
            findEmptyEnemySlot()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 1259
     * Function: findEmptyEnemySlot (0x9946)
     * Call depth: 14
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `findEmptyEnemySlot_frame1259_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0732] = 0x00u

            // Execute decompiled function
            findEmptyEnemySlot()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 2 from frame 4589
     * Function: findEmptyEnemySlot (0x9946)
     * Call depth: 12
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `findEmptyEnemySlot_frame4589_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0732] = 0x01u

            // Execute decompiled function
            findEmptyEnemySlot()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 3 from frame 4597
     * Function: findEmptyEnemySlot (0x9946)
     * Call depth: 12
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `findEmptyEnemySlot_frame4597_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0732] = 0x00u

            // Execute decompiled function
            findEmptyEnemySlot()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 4 from frame 6667
     * Function: findEmptyEnemySlot (0x9946)
     * Call depth: 12
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `findEmptyEnemySlot_frame6667_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0732] = 0x01u

            // Execute decompiled function
            findEmptyEnemySlot()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 5 from frame 6675
     * Function: findEmptyEnemySlot (0x9946)
     * Call depth: 12
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `findEmptyEnemySlot_frame6675_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0732] = 0x00u

            // Execute decompiled function
            findEmptyEnemySlot()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0x9A44: drawRow
    // 14 calls, 2 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 23
     * Function: drawRow (0x9A44)
     * Call depth: 10
     * Memory reads: 6, writes: 6
     */
    @Test
    fun `drawRow_frame23_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (6 addresses)
            memory[0x0007] = 0x07u
            memory[0x01EF] = 0x47u
            memory[0x01F0] = 0x9Au
            memory[0x01F6] = 0xBEu
            memory[0x01F7] = 0x92u
            memory[0x071F] = 0x05u

            // Execute decompiled function
            drawRow()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x04u, memory[0x071F], "Memory 0x071F mismatch")
        }
    }

    /**
     * Test case 1 from frame 23
     * Function: drawRow (0x9A44)
     * Call depth: 15
     * Memory reads: 3, writes: 3
     */
    @Test
    fun `drawRow_frame23_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x0007] = 0x07u
            memory[0x01EF] = 0x47u
            memory[0x01F0] = 0x9Au

            // Execute decompiled function
            drawRow()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0x9A4C: columnOfBricks
    // 26 calls, 9 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 21
     * Function: columnOfBricks (0x9A4C)
     * Call depth: 10
     * Memory reads: 6, writes: 6
     */
    @Test
    fun `columnOfBricks_frame21_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (6 addresses)
            memory[0x01F1] = 0xC0u
            memory[0x01F8] = 0xEBu
            memory[0x01F9] = 0x86u
            memory[0x06A8] = 0x00u
            memory[0x071F] = 0x04u
            memory[0x0735] = 0x00u

            // Execute decompiled function
            columnOfBricks()

            // Verify: Check output memory (2 addresses)
            assertEquals(0xC0u, memory[0x06A8], "Memory 0x06A8 mismatch")
            assertEquals(0x00u, memory[0x0735], "Memory 0x0735 mismatch")
        }
    }

    /**
     * Test case 1 from frame 23
     * Function: columnOfBricks (0x9A4C)
     * Call depth: 10
     * Memory reads: 12, writes: 4
     */
    @Test
    fun `columnOfBricks_frame23_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (12 addresses)
            memory[0x0008] = 0x00u
            memory[0x00E7] = 0x90u
            memory[0x00E8] = 0xA6u
            memory[0x01F1] = 0x51u
            memory[0x06A8] = 0x00u
            memory[0x0725] = 0x01u
            memory[0x0726] = 0x04u
            memory[0x0728] = 0x00u
            memory[0x072A] = 0x01u
            memory[0x072C] = 0x04u
            memory[0x0730] = 0xFFu
            memory[0x0735] = 0x00u

            // Execute decompiled function
            columnOfBricks()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x16u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x51u, memory[0x06A8], "Memory 0x06A8 mismatch")
            assertEquals(0x00u, memory[0x0735], "Memory 0x0735 mismatch")
        }
    }

    /**
     * Test case 2 from frame 23
     * Function: columnOfBricks (0x9A4C)
     * Call depth: 15
     * Memory reads: 3, writes: 2
     */
    @Test
    fun `columnOfBricks_frame23_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01F1] = 0x51u
            memory[0x06A8] = 0x00u
            memory[0x0735] = 0x00u

            // Execute decompiled function
            columnOfBricks()

            // Verify: Check output memory (2 addresses)
            assertEquals(0x51u, memory[0x06A8], "Memory 0x06A8 mismatch")
            assertEquals(0x00u, memory[0x0735], "Memory 0x0735 mismatch")
        }
    }

    /**
     * Test case 3 from frame 23
     * Function: columnOfBricks (0x9A4C)
     * Call depth: 16
     * Memory reads: 39, writes: 54
     */
    @Test
    fun `columnOfBricks_frame23_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (39 addresses)
            memory[0x0000] = 0x57u
            memory[0x0001] = 0x00u
            memory[0x0002] = 0x08u
            memory[0x0003] = 0x80u
            memory[0x0004] = 0xCAu
            memory[0x0005] = 0x92u
            memory[0x0006] = 0xAEu
            memory[0x0007] = 0x88u
            memory[0x01F1] = 0xC1u
            memory[0x01F4] = 0xCAu
            memory[0x01F5] = 0x92u
            memory[0x01F6] = 0xBEu
            memory[0x01F7] = 0x92u
            memory[0x0340] = 0x57u
            memory[0x03F9] = 0xAAu
            memory[0x03FA] = 0x00u
            memory[0x03FB] = 0x00u
            memory[0x03FC] = 0xD0u
            memory[0x03FD] = 0x00u
            memory[0x03FE] = 0x50u
            memory[0x03FF] = 0x05u
            memory[0x06A1] = 0x82u
            memory[0x06A2] = 0x85u
            memory[0x06A3] = 0x00u
            memory[0x06A4] = 0x00u
            memory[0x06A5] = 0x00u
            memory[0x06A6] = 0x00u
            memory[0x06A7] = 0x00u
            memory[0x06A8] = 0x51u
            memory[0x06A9] = 0x00u
            memory[0x06AA] = 0x00u
            memory[0x06AB] = 0x00u
            memory[0x06AC] = 0x54u
            memory[0x06AD] = 0x54u
            memory[0x071F] = 0x02u
            memory[0x0720] = 0x24u
            memory[0x0721] = 0x8Bu
            memory[0x0726] = 0x05u
            memory[0x0735] = 0x00u

            // Execute decompiled function
            columnOfBricks()

            // Verify: Check output memory (50 addresses)
            assertEquals(0x71u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x0Cu, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x50u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x04u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x06u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x01u, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0xACu, memory[0x0006], "Memory 0x0006 mismatch")
            assertEquals(0x8Bu, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x74u, memory[0x0340], "Memory 0x0340 mismatch")
            assertEquals(0x24u, memory[0x0398], "Memory 0x0398 mismatch")
            assertEquals(0x8Bu, memory[0x0399], "Memory 0x0399 mismatch")
            assertEquals(0x9Au, memory[0x039A], "Memory 0x039A mismatch")
            assertEquals(0x24u, memory[0x039B], "Memory 0x039B mismatch")
            assertEquals(0x24u, memory[0x039C], "Memory 0x039C mismatch")
            assertEquals(0x24u, memory[0x039D], "Memory 0x039D mismatch")
            assertEquals(0x24u, memory[0x039E], "Memory 0x039E mismatch")
            assertEquals(0x24u, memory[0x039F], "Memory 0x039F mismatch")
            assertEquals(0x24u, memory[0x03A0], "Memory 0x03A0 mismatch")
            assertEquals(0x24u, memory[0x03A1], "Memory 0x03A1 mismatch")
            assertEquals(0x24u, memory[0x03A2], "Memory 0x03A2 mismatch")
            assertEquals(0x24u, memory[0x03A3], "Memory 0x03A3 mismatch")
            assertEquals(0x24u, memory[0x03A4], "Memory 0x03A4 mismatch")
            assertEquals(0x24u, memory[0x03A5], "Memory 0x03A5 mismatch")
            assertEquals(0x24u, memory[0x03A6], "Memory 0x03A6 mismatch")
            assertEquals(0x24u, memory[0x03A7], "Memory 0x03A7 mismatch")
            assertEquals(0x24u, memory[0x03A8], "Memory 0x03A8 mismatch")
            assertEquals(0x54u, memory[0x03A9], "Memory 0x03A9 mismatch")
            assertEquals(0x56u, memory[0x03AA], "Memory 0x03AA mismatch")
            assertEquals(0x24u, memory[0x03AB], "Memory 0x03AB mismatch")
            assertEquals(0x24u, memory[0x03AC], "Memory 0x03AC mismatch")
            assertEquals(0x24u, memory[0x03AD], "Memory 0x03AD mismatch")
            assertEquals(0x24u, memory[0x03AE], "Memory 0x03AE mismatch")
            assertEquals(0x24u, memory[0x03AF], "Memory 0x03AF mismatch")
            assertEquals(0x24u, memory[0x03B0], "Memory 0x03B0 mismatch")
            assertEquals(0xB5u, memory[0x03B1], "Memory 0x03B1 mismatch")
            assertEquals(0xB7u, memory[0x03B2], "Memory 0x03B2 mismatch")
            assertEquals(0xB5u, memory[0x03B3], "Memory 0x03B3 mismatch")
            assertEquals(0xB7u, memory[0x03B4], "Memory 0x03B4 mismatch")
            assertEquals(0x00u, memory[0x03B5], "Memory 0x03B5 mismatch")
            assertEquals(0xAAu, memory[0x03F9], "Memory 0x03F9 mismatch")
            assertEquals(0x00u, memory[0x03FA], "Memory 0x03FA mismatch")
            assertEquals(0x00u, memory[0x03FB], "Memory 0x03FB mismatch")
            assertEquals(0xD0u, memory[0x03FC], "Memory 0x03FC mismatch")
            assertEquals(0x00u, memory[0x03FD], "Memory 0x03FD mismatch")
            assertEquals(0x50u, memory[0x03FE], "Memory 0x03FE mismatch")
            assertEquals(0x05u, memory[0x03FF], "Memory 0x03FF mismatch")
            assertEquals(0xC1u, memory[0x06A8], "Memory 0x06A8 mismatch")
            assertEquals(0x00u, memory[0x071F], "Memory 0x071F mismatch")
            assertEquals(0x8Cu, memory[0x0721], "Memory 0x0721 mismatch")
            assertEquals(0x00u, memory[0x0735], "Memory 0x0735 mismatch")
        }
    }

    /**
     * Test case 4 from frame 24
     * Function: columnOfBricks (0x9A4C)
     * Call depth: 10
     * Memory reads: 3, writes: 2
     */
    @Test
    fun `columnOfBricks_frame24_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01F1] = 0x51u
            memory[0x06A8] = 0x00u
            memory[0x0735] = 0x00u

            // Execute decompiled function
            columnOfBricks()

            // Verify: Check output memory (2 addresses)
            assertEquals(0x51u, memory[0x06A8], "Memory 0x06A8 mismatch")
            assertEquals(0x00u, memory[0x0735], "Memory 0x0735 mismatch")
        }
    }

    /**
     * Test case 5 from frame 24
     * Function: columnOfBricks (0x9A4C)
     * Call depth: 11
     * Memory reads: 39, writes: 52
     */
    @Test
    fun `columnOfBricks_frame24_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (39 addresses)
            memory[0x0000] = 0x1Du
            memory[0x0001] = 0x00u
            memory[0x0002] = 0x00u
            memory[0x0003] = 0x00u
            memory[0x0004] = 0xCAu
            memory[0x0005] = 0x92u
            memory[0x0006] = 0xAEu
            memory[0x0007] = 0x88u
            memory[0x01F1] = 0xC0u
            memory[0x01F4] = 0xCAu
            memory[0x01F5] = 0x92u
            memory[0x01F6] = 0xBEu
            memory[0x01F7] = 0x92u
            memory[0x0340] = 0x1Du
            memory[0x03F9] = 0x00u
            memory[0x03FA] = 0x30u
            memory[0x03FB] = 0x00u
            memory[0x03FC] = 0x10u
            memory[0x03FD] = 0x00u
            memory[0x03FE] = 0x10u
            memory[0x03FF] = 0x01u
            memory[0x06A1] = 0x00u
            memory[0x06A2] = 0x00u
            memory[0x06A3] = 0x00u
            memory[0x06A4] = 0x00u
            memory[0x06A5] = 0x00u
            memory[0x06A6] = 0x00u
            memory[0x06A7] = 0x00u
            memory[0x06A8] = 0x51u
            memory[0x06A9] = 0x00u
            memory[0x06AA] = 0x00u
            memory[0x06AB] = 0x00u
            memory[0x06AC] = 0x54u
            memory[0x06AD] = 0x54u
            memory[0x071F] = 0x06u
            memory[0x0720] = 0x24u
            memory[0x0721] = 0x8Du
            memory[0x0726] = 0x06u
            memory[0x0735] = 0x00u

            // Execute decompiled function
            columnOfBricks()

            // Verify: Check output memory (50 addresses)
            assertEquals(0x37u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x0Cu, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x50u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x01u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x06u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x00u, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0xACu, memory[0x0006], "Memory 0x0006 mismatch")
            assertEquals(0x8Bu, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x3Au, memory[0x0340], "Memory 0x0340 mismatch")
            assertEquals(0x24u, memory[0x035E], "Memory 0x035E mismatch")
            assertEquals(0x8Du, memory[0x035F], "Memory 0x035F mismatch")
            assertEquals(0x9Au, memory[0x0360], "Memory 0x0360 mismatch")
            assertEquals(0x24u, memory[0x0361], "Memory 0x0361 mismatch")
            assertEquals(0x24u, memory[0x0362], "Memory 0x0362 mismatch")
            assertEquals(0x24u, memory[0x0363], "Memory 0x0363 mismatch")
            assertEquals(0x24u, memory[0x0364], "Memory 0x0364 mismatch")
            assertEquals(0x24u, memory[0x0365], "Memory 0x0365 mismatch")
            assertEquals(0x24u, memory[0x0366], "Memory 0x0366 mismatch")
            assertEquals(0x54u, memory[0x0367], "Memory 0x0367 mismatch")
            assertEquals(0x56u, memory[0x0368], "Memory 0x0368 mismatch")
            assertEquals(0x24u, memory[0x0369], "Memory 0x0369 mismatch")
            assertEquals(0x24u, memory[0x036A], "Memory 0x036A mismatch")
            assertEquals(0x24u, memory[0x036B], "Memory 0x036B mismatch")
            assertEquals(0x24u, memory[0x036C], "Memory 0x036C mismatch")
            assertEquals(0x24u, memory[0x036D], "Memory 0x036D mismatch")
            assertEquals(0x24u, memory[0x036E], "Memory 0x036E mismatch")
            assertEquals(0x45u, memory[0x036F], "Memory 0x036F mismatch")
            assertEquals(0x47u, memory[0x0370], "Memory 0x0370 mismatch")
            assertEquals(0x24u, memory[0x0371], "Memory 0x0371 mismatch")
            assertEquals(0x24u, memory[0x0372], "Memory 0x0372 mismatch")
            assertEquals(0x24u, memory[0x0373], "Memory 0x0373 mismatch")
            assertEquals(0x24u, memory[0x0374], "Memory 0x0374 mismatch")
            assertEquals(0x24u, memory[0x0375], "Memory 0x0375 mismatch")
            assertEquals(0x24u, memory[0x0376], "Memory 0x0376 mismatch")
            assertEquals(0xB5u, memory[0x0377], "Memory 0x0377 mismatch")
            assertEquals(0xB7u, memory[0x0378], "Memory 0x0378 mismatch")
            assertEquals(0xB5u, memory[0x0379], "Memory 0x0379 mismatch")
            assertEquals(0xB7u, memory[0x037A], "Memory 0x037A mismatch")
            assertEquals(0x00u, memory[0x037B], "Memory 0x037B mismatch")
            assertEquals(0x00u, memory[0x03F9], "Memory 0x03F9 mismatch")
            assertEquals(0x30u, memory[0x03FA], "Memory 0x03FA mismatch")
            assertEquals(0x00u, memory[0x03FB], "Memory 0x03FB mismatch")
            assertEquals(0x10u, memory[0x03FC], "Memory 0x03FC mismatch")
            assertEquals(0x00u, memory[0x03FD], "Memory 0x03FD mismatch")
            assertEquals(0x10u, memory[0x03FE], "Memory 0x03FE mismatch")
            assertEquals(0x01u, memory[0x03FF], "Memory 0x03FF mismatch")
            assertEquals(0xC0u, memory[0x06A4], "Memory 0x06A4 mismatch")
            assertEquals(0x04u, memory[0x071F], "Memory 0x071F mismatch")
            assertEquals(0x8Eu, memory[0x0721], "Memory 0x0721 mismatch")
            assertEquals(0x00u, memory[0x0735], "Memory 0x0735 mismatch")
        }
    }

    /**
     * Test case 6 from frame 24
     * Function: columnOfBricks (0x9A4C)
     * Call depth: 16
     * Memory reads: 3, writes: 2
     */
    @Test
    fun `columnOfBricks_frame24_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01F1] = 0x51u
            memory[0x06A8] = 0x00u
            memory[0x0735] = 0x00u

            // Execute decompiled function
            columnOfBricks()

            // Verify: Check output memory (2 addresses)
            assertEquals(0x51u, memory[0x06A8], "Memory 0x06A8 mismatch")
            assertEquals(0x00u, memory[0x0735], "Memory 0x0735 mismatch")
        }
    }

    /**
     * Test case 7 from frame 24
     * Function: columnOfBricks (0x9A4C)
     * Call depth: 17
     * Memory reads: 3, writes: 2
     */
    @Test
    fun `columnOfBricks_frame24_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01F1] = 0xC0u
            memory[0x06A8] = 0x51u
            memory[0x0735] = 0x00u

            // Execute decompiled function
            columnOfBricks()

            // Verify: Check output memory (2 addresses)
            assertEquals(0xC0u, memory[0x06A8], "Memory 0x06A8 mismatch")
            assertEquals(0x00u, memory[0x0735], "Memory 0x0735 mismatch")
        }
    }

    /**
     * Test case 8 from frame 1123
     * Function: columnOfBricks (0x9A4C)
     * Call depth: 11
     * Memory reads: 3, writes: 2
     */
    @Test
    fun `columnOfBricks_frame1123_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01F1] = 0x51u
            memory[0x06A8] = 0x00u
            memory[0x0735] = 0x00u

            // Execute decompiled function
            columnOfBricks()

            // Verify: Check output memory (2 addresses)
            assertEquals(0x51u, memory[0x06A8], "Memory 0x06A8 mismatch")
            assertEquals(0x00u, memory[0x0735], "Memory 0x0735 mismatch")
        }
    }

    // =========================================
    // 0x9BA8: chkLrgObjLength
    // 34 calls, 15 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 21
     * Function: chkLrgObjLength (0x9BA8)
     * Call depth: 10
     * Memory reads: 9, writes: 5
     */
    @Test
    fun `chkLrgObjLength_frame21_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0000] = 0x20u
            memory[0x0008] = 0x01u
            memory[0x00E7] = 0x90u
            memory[0x00E8] = 0xA6u
            memory[0x01F6] = 0xBEu
            memory[0x01F7] = 0x92u
            memory[0x071F] = 0x05u
            memory[0x0730] = 0xFFu
            memory[0x0731] = 0xFFu

            // Execute decompiled function
            chkLrgObjLength()

            // Verify: Check output memory (3 addresses)
            assertEquals(0x20u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x04u, memory[0x071F], "Memory 0x071F mismatch")
        }
    }

    /**
     * Test case 1 from frame 23
     * Function: chkLrgObjLength (0x9BA8)
     * Call depth: 10
     * Memory reads: 9, writes: 2
     */
    @Test
    fun `chkLrgObjLength_frame23_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0008] = 0x01u
            memory[0x00E7] = 0x90u
            memory[0x00E8] = 0xA6u
            memory[0x0725] = 0x01u
            memory[0x0726] = 0x04u
            memory[0x0728] = 0x00u
            memory[0x072A] = 0x01u
            memory[0x072C] = 0x04u
            memory[0x0731] = 0xFFu

            // Execute decompiled function
            chkLrgObjLength()

            // Verify: Check output memory (2 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x16u, memory[0x0007], "Memory 0x0007 mismatch")
        }
    }

    /**
     * Test case 2 from frame 23
     * Function: chkLrgObjLength (0x9BA8)
     * Call depth: 16
     * Memory reads: 9, writes: 2
     */
    @Test
    fun `chkLrgObjLength_frame23_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0008] = 0x00u
            memory[0x00E7] = 0x90u
            memory[0x00E8] = 0xA6u
            memory[0x0725] = 0x01u
            memory[0x0726] = 0x05u
            memory[0x0728] = 0x00u
            memory[0x072A] = 0x01u
            memory[0x072C] = 0x06u
            memory[0x0730] = 0xFFu

            // Execute decompiled function
            chkLrgObjLength()

            // Verify: Check output memory (2 addresses)
            assertEquals(0x01u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x16u, memory[0x0007], "Memory 0x0007 mismatch")
        }
    }

    /**
     * Test case 3 from frame 24
     * Function: chkLrgObjLength (0x9BA8)
     * Call depth: 10
     * Memory reads: 13, writes: 5
     */
    @Test
    fun `chkLrgObjLength_frame24_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (13 addresses)
            memory[0x0000] = 0x01u
            memory[0x0007] = 0x16u
            memory[0x0008] = 0x01u
            memory[0x00E7] = 0x90u
            memory[0x00E8] = 0xA6u
            memory[0x01F0] = 0x5Eu
            memory[0x01F1] = 0x96u
            memory[0x0725] = 0x01u
            memory[0x0726] = 0x06u
            memory[0x0728] = 0x00u
            memory[0x072A] = 0x01u
            memory[0x072C] = 0x06u
            memory[0x0731] = 0xFFu

            // Execute decompiled function
            chkLrgObjLength()

            // Verify: Check output memory (3 addresses)
            assertEquals(0x01u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x16u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x06u, memory[0x072E], "Memory 0x072E mismatch")
        }
    }

    /**
     * Test case 4 from frame 24
     * Function: chkLrgObjLength (0x9BA8)
     * Call depth: 16
     * Memory reads: 13, writes: 5
     */
    @Test
    fun `chkLrgObjLength_frame24_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (13 addresses)
            memory[0x0000] = 0x01u
            memory[0x0007] = 0x16u
            memory[0x0008] = 0x01u
            memory[0x00E7] = 0x90u
            memory[0x00E8] = 0xA6u
            memory[0x01F0] = 0x5Eu
            memory[0x01F1] = 0x96u
            memory[0x0725] = 0x01u
            memory[0x0726] = 0x07u
            memory[0x0728] = 0x00u
            memory[0x072A] = 0x01u
            memory[0x072C] = 0x08u
            memory[0x0731] = 0xFFu

            // Execute decompiled function
            chkLrgObjLength()

            // Verify: Check output memory (3 addresses)
            assertEquals(0x01u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x16u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x08u, memory[0x072E], "Memory 0x072E mismatch")
        }
    }

    /**
     * Test case 5 from frame 24
     * Function: chkLrgObjLength (0x9BA8)
     * Call depth: 17
     * Memory reads: 41, writes: 52
     */
    @Test
    fun `chkLrgObjLength_frame24_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (41 addresses)
            memory[0x0000] = 0x70u
            memory[0x0001] = 0x00u
            memory[0x0002] = 0x00u
            memory[0x0003] = 0x00u
            memory[0x0004] = 0xCAu
            memory[0x0005] = 0x92u
            memory[0x0006] = 0xAEu
            memory[0x0007] = 0x88u
            memory[0x0008] = 0x00u
            memory[0x00E7] = 0x90u
            memory[0x00E8] = 0xA6u
            memory[0x01F4] = 0xCAu
            memory[0x01F5] = 0x92u
            memory[0x01F6] = 0xBEu
            memory[0x01F7] = 0x92u
            memory[0x0340] = 0x57u
            memory[0x03F9] = 0x00u
            memory[0x03FA] = 0x30u
            memory[0x03FB] = 0x00u
            memory[0x03FC] = 0xD0u
            memory[0x03FD] = 0x00u
            memory[0x03FE] = 0x50u
            memory[0x03FF] = 0x05u
            memory[0x06A1] = 0x00u
            memory[0x06A2] = 0x00u
            memory[0x06A3] = 0x00u
            memory[0x06A4] = 0x00u
            memory[0x06A5] = 0x00u
            memory[0x06A6] = 0x00u
            memory[0x06A7] = 0x00u
            memory[0x06A8] = 0xC0u
            memory[0x06A9] = 0x00u
            memory[0x06AA] = 0x00u
            memory[0x06AB] = 0x02u
            memory[0x06AC] = 0x54u
            memory[0x06AD] = 0x54u
            memory[0x071F] = 0x02u
            memory[0x0720] = 0x24u
            memory[0x0721] = 0x8Fu
            memory[0x0726] = 0x07u
            memory[0x0730] = 0xFFu

            // Execute decompiled function
            chkLrgObjLength()

            // Verify: Check output memory (48 addresses)
            assertEquals(0x71u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x0Cu, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x50u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x04u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x06u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x01u, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0xACu, memory[0x0006], "Memory 0x0006 mismatch")
            assertEquals(0x8Bu, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x74u, memory[0x0340], "Memory 0x0340 mismatch")
            assertEquals(0x24u, memory[0x0398], "Memory 0x0398 mismatch")
            assertEquals(0x8Fu, memory[0x0399], "Memory 0x0399 mismatch")
            assertEquals(0x9Au, memory[0x039A], "Memory 0x039A mismatch")
            assertEquals(0x24u, memory[0x039B], "Memory 0x039B mismatch")
            assertEquals(0x24u, memory[0x039C], "Memory 0x039C mismatch")
            assertEquals(0x24u, memory[0x039D], "Memory 0x039D mismatch")
            assertEquals(0x24u, memory[0x039E], "Memory 0x039E mismatch")
            assertEquals(0x24u, memory[0x039F], "Memory 0x039F mismatch")
            assertEquals(0x24u, memory[0x03A0], "Memory 0x03A0 mismatch")
            assertEquals(0x24u, memory[0x03A1], "Memory 0x03A1 mismatch")
            assertEquals(0x24u, memory[0x03A2], "Memory 0x03A2 mismatch")
            assertEquals(0x24u, memory[0x03A3], "Memory 0x03A3 mismatch")
            assertEquals(0x24u, memory[0x03A4], "Memory 0x03A4 mismatch")
            assertEquals(0x24u, memory[0x03A5], "Memory 0x03A5 mismatch")
            assertEquals(0x24u, memory[0x03A6], "Memory 0x03A6 mismatch")
            assertEquals(0x24u, memory[0x03A7], "Memory 0x03A7 mismatch")
            assertEquals(0x24u, memory[0x03A8], "Memory 0x03A8 mismatch")
            assertEquals(0x54u, memory[0x03A9], "Memory 0x03A9 mismatch")
            assertEquals(0x56u, memory[0x03AA], "Memory 0x03AA mismatch")
            assertEquals(0x24u, memory[0x03AB], "Memory 0x03AB mismatch")
            assertEquals(0x24u, memory[0x03AC], "Memory 0x03AC mismatch")
            assertEquals(0x24u, memory[0x03AD], "Memory 0x03AD mismatch")
            assertEquals(0x24u, memory[0x03AE], "Memory 0x03AE mismatch")
            assertEquals(0x24u, memory[0x03AF], "Memory 0x03AF mismatch")
            assertEquals(0x35u, memory[0x03B0], "Memory 0x03B0 mismatch")
            assertEquals(0xB5u, memory[0x03B1], "Memory 0x03B1 mismatch")
            assertEquals(0xB7u, memory[0x03B2], "Memory 0x03B2 mismatch")
            assertEquals(0xB5u, memory[0x03B3], "Memory 0x03B3 mismatch")
            assertEquals(0xB7u, memory[0x03B4], "Memory 0x03B4 mismatch")
            assertEquals(0x00u, memory[0x03B5], "Memory 0x03B5 mismatch")
            assertEquals(0x00u, memory[0x03F9], "Memory 0x03F9 mismatch")
            assertEquals(0x30u, memory[0x03FA], "Memory 0x03FA mismatch")
            assertEquals(0x00u, memory[0x03FB], "Memory 0x03FB mismatch")
            assertEquals(0xD0u, memory[0x03FC], "Memory 0x03FC mismatch")
            assertEquals(0x00u, memory[0x03FD], "Memory 0x03FD mismatch")
            assertEquals(0x50u, memory[0x03FE], "Memory 0x03FE mismatch")
            assertEquals(0x05u, memory[0x03FF], "Memory 0x03FF mismatch")
            assertEquals(0x00u, memory[0x071F], "Memory 0x071F mismatch")
            assertEquals(0x90u, memory[0x0721], "Memory 0x0721 mismatch")
        }
    }

    /**
     * Test case 6 from frame 1251
     * Function: chkLrgObjLength (0x9BA8)
     * Call depth: 13
     * Memory reads: 6, writes: 2
     */
    @Test
    fun `chkLrgObjLength_frame1251_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (6 addresses)
            memory[0x0000] = 0x70u
            memory[0x0008] = 0x01u
            memory[0x00E7] = 0x90u
            memory[0x00E8] = 0xA6u
            memory[0x0730] = 0xFFu
            memory[0x0731] = 0xFFu

            // Execute decompiled function
            chkLrgObjLength()

            // Verify: Check output memory (2 addresses)
            assertEquals(0x70u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
        }
    }

    /**
     * Test case 7 from frame 1259
     * Function: chkLrgObjLength (0x9BA8)
     * Call depth: 13
     * Memory reads: 6, writes: 2
     */
    @Test
    fun `chkLrgObjLength_frame1259_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (6 addresses)
            memory[0x0000] = 0x70u
            memory[0x0008] = 0x01u
            memory[0x00E7] = 0x90u
            memory[0x00E8] = 0xA6u
            memory[0x0730] = 0xFFu
            memory[0x0731] = 0xFFu

            // Execute decompiled function
            chkLrgObjLength()

            // Verify: Check output memory (2 addresses)
            assertEquals(0x70u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
        }
    }

    /**
     * Test case 8 from frame 4597
     * Function: chkLrgObjLength (0x9BA8)
     * Call depth: 11
     * Memory reads: 8, writes: 4
     */
    @Test
    fun `chkLrgObjLength_frame4597_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (8 addresses)
            memory[0x0000] = 0x70u
            memory[0x0008] = 0x01u
            memory[0x00E7] = 0x90u
            memory[0x00E8] = 0xA6u
            memory[0x06AB] = 0x00u
            memory[0x0730] = 0xFFu
            memory[0x0731] = 0xFFu
            memory[0x0735] = 0x00u

            // Execute decompiled function
            chkLrgObjLength()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x70u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x15u, memory[0x06AB], "Memory 0x06AB mismatch")
            assertEquals(0x00u, memory[0x0735], "Memory 0x0735 mismatch")
        }
    }

    /**
     * Test case 9 from frame 6667
     * Function: chkLrgObjLength (0x9BA8)
     * Call depth: 11
     * Memory reads: 9, writes: 5
     */
    @Test
    fun `chkLrgObjLength_frame6667_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0000] = 0x00u
            memory[0x0008] = 0x01u
            memory[0x00E7] = 0x90u
            memory[0x00E8] = 0xA6u
            memory[0x06AA] = 0x00u
            memory[0x06AB] = 0x00u
            memory[0x0730] = 0xFFu
            memory[0x0731] = 0xFFu
            memory[0x0735] = 0x01u

            // Execute decompiled function
            chkLrgObjLength()

            // Verify: Check output memory (5 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x14u, memory[0x06AA], "Memory 0x06AA mismatch")
            assertEquals(0x14u, memory[0x06AB], "Memory 0x06AB mismatch")
            assertEquals(0x00u, memory[0x0735], "Memory 0x0735 mismatch")
        }
    }

    // =========================================
    // 0x9BAB: chkLrgObjFixedLength
    // Parameters: X, Y
    // 34 calls, 6 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 21
     * Function: chkLrgObjFixedLength (0x9BAB)
     * Parameters: X, Y
     * Call depth: 10
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `chkLrgObjFixedLength_frame21_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            chkLrgObjFixedLength(0x08, 0xFF)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 23
     * Function: chkLrgObjFixedLength (0x9BAB)
     * Parameters: X, Y
     * Call depth: 10
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `chkLrgObjFixedLength_frame23_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            chkLrgObjFixedLength(0x08, 0xFF)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 2 from frame 23
     * Function: chkLrgObjFixedLength (0x9BAB)
     * Parameters: X, Y
     * Call depth: 16
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `chkLrgObjFixedLength_frame23_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            chkLrgObjFixedLength(0x08, 0xFF)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 3 from frame 24
     * Function: chkLrgObjFixedLength (0x9BAB)
     * Parameters: X, Y
     * Call depth: 11
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `chkLrgObjFixedLength_frame24_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            chkLrgObjFixedLength(0x04, 0xFF)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 4 from frame 1251
     * Function: chkLrgObjFixedLength (0x9BAB)
     * Parameters: X, Y
     * Call depth: 13
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `chkLrgObjFixedLength_frame1251_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            chkLrgObjFixedLength(0x0B, 0xFF)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 5 from frame 1259
     * Function: chkLrgObjFixedLength (0x9BAB)
     * Parameters: X, Y
     * Call depth: 13
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `chkLrgObjFixedLength_frame1259_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            chkLrgObjFixedLength(0x0B, 0xFF)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0x9BC7: getAreaObjXPosition
    // 34 calls, 11 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 21
     * Function: getAreaObjXPosition (0x9BC7)
     * Call depth: 11
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `getAreaObjXPosition_frame21_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getAreaObjXPosition()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 23
     * Function: getAreaObjXPosition (0x9BC7)
     * Call depth: 12
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `getAreaObjXPosition_frame23_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getAreaObjXPosition()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 2 from frame 23
     * Function: getAreaObjXPosition (0x9BC7)
     * Call depth: 17
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `getAreaObjXPosition_frame23_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getAreaObjXPosition()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 3 from frame 24
     * Function: getAreaObjXPosition (0x9BC7)
     * Call depth: 12
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `getAreaObjXPosition_frame24_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getAreaObjXPosition()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 4 from frame 24
     * Function: getAreaObjXPosition (0x9BC7)
     * Call depth: 18
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `getAreaObjXPosition_frame24_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getAreaObjXPosition()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 5 from frame 1251
     * Function: getAreaObjXPosition (0x9BC7)
     * Call depth: 15
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `getAreaObjXPosition_frame1251_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getAreaObjXPosition()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 6 from frame 1259
     * Function: getAreaObjXPosition (0x9BC7)
     * Call depth: 15
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `getAreaObjXPosition_frame1259_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getAreaObjXPosition()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 7 from frame 4589
     * Function: getAreaObjXPosition (0x9BC7)
     * Call depth: 13
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `getAreaObjXPosition_frame4589_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getAreaObjXPosition()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 8 from frame 4597
     * Function: getAreaObjXPosition (0x9BC7)
     * Call depth: 13
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `getAreaObjXPosition_frame4597_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getAreaObjXPosition()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 9 from frame 6667
     * Function: getAreaObjXPosition (0x9BC7)
     * Call depth: 13
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `getAreaObjXPosition_frame6667_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getAreaObjXPosition()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0x9C0F: findAreaPointer
    // 6 calls, 3 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 5
     * Function: findAreaPointer (0x9C0F)
     * Call depth: 4
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `findAreaPointer_frame5_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            findAreaPointer()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x01u, memory[0x074E], "Memory 0x074E mismatch")
        }
    }

    /**
     * Test case 1 from frame 6
     * Function: findAreaPointer (0x9C0F)
     * Call depth: 5
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `findAreaPointer_frame6_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            findAreaPointer()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x01u, memory[0x074E], "Memory 0x074E mismatch")
        }
    }

    /**
     * Test case 2 from frame 1962
     * Function: findAreaPointer (0x9C0F)
     * Call depth: 6
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `findAreaPointer_frame1962_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            findAreaPointer()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x01u, memory[0x074E], "Memory 0x074E mismatch")
        }
    }

    // =========================================
    // 0x9C1E: getAreaDataAddrs
    // 3 calls, 2 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 5
     * Function: getAreaDataAddrs (0x9C1E)
     * Call depth: 5
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `getAreaDataAddrs_frame5_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getAreaDataAddrs()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 1962
     * Function: getAreaDataAddrs (0x9C1E)
     * Call depth: 7
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `getAreaDataAddrs_frame1962_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getAreaDataAddrs()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0xAF6B: updScrollVar
    // 8753 calls, 689 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 33
     * Function: updScrollVar (0xAF6B)
     * Call depth: 5
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `updScrollVar_frame33_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x071F] = 0x00u
            memory[0x073D] = 0x00u
            memory[0x0773] = 0x00u

            // Execute decompiled function
            updScrollVar()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x000C], "Memory 0x000C mismatch")
        }
    }

    /**
     * Test case 1 from frame 1097
     * Function: updScrollVar (0xAF6B)
     * Call depth: 5
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `updScrollVar_frame1097_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x071F] = 0x00u
            memory[0x073D] = 0x13u
            memory[0x0773] = 0x00u

            // Execute decompiled function
            updScrollVar()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x000C], "Memory 0x000C mismatch")
        }
    }

    /**
     * Test case 2 from frame 1269
     * Function: updScrollVar (0xAF6B)
     * Call depth: 7
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `updScrollVar_frame1269_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x071F] = 0x00u
            memory[0x073D] = 0x0Bu
            memory[0x0773] = 0x00u

            // Execute decompiled function
            updScrollVar()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x000C], "Memory 0x000C mismatch")
        }
    }

    /**
     * Test case 3 from frame 3593
     * Function: updScrollVar (0xAF6B)
     * Call depth: 5
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `updScrollVar_frame3593_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x071F] = 0x00u
            memory[0x073D] = 0x09u
            memory[0x0773] = 0x00u

            // Execute decompiled function
            updScrollVar()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x000C], "Memory 0x000C mismatch")
        }
    }

    /**
     * Test case 4 from frame 4067
     * Function: updScrollVar (0xAF6B)
     * Call depth: 7
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `updScrollVar_frame4067_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x071F] = 0x00u
            memory[0x073D] = 0x1Bu
            memory[0x0773] = 0x00u

            // Execute decompiled function
            updScrollVar()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x000C], "Memory 0x000C mismatch")
        }
    }

    /**
     * Test case 5 from frame 4407
     * Function: updScrollVar (0xAF6B)
     * Call depth: 5
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `updScrollVar_frame4407_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x071F] = 0x00u
            memory[0x073D] = 0x14u
            memory[0x0773] = 0x00u

            // Execute decompiled function
            updScrollVar()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x000C], "Memory 0x000C mismatch")
        }
    }

    /**
     * Test case 6 from frame 4719
     * Function: updScrollVar (0xAF6B)
     * Call depth: 5
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `updScrollVar_frame4719_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x071F] = 0x00u
            memory[0x073D] = 0x05u
            memory[0x0773] = 0x00u

            // Execute decompiled function
            updScrollVar()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x000C], "Memory 0x000C mismatch")
        }
    }

    /**
     * Test case 7 from frame 5225
     * Function: updScrollVar (0xAF6B)
     * Call depth: 5
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `updScrollVar_frame5225_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x071F] = 0x00u
            memory[0x073D] = 0x0Cu
            memory[0x0773] = 0x00u

            // Execute decompiled function
            updScrollVar()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x000C], "Memory 0x000C mismatch")
        }
    }

    /**
     * Test case 8 from frame 6931
     * Function: updScrollVar (0xAF6B)
     * Call depth: 5
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `updScrollVar_frame6931_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x071F] = 0x03u
            memory[0x0773] = 0x00u

            // Execute decompiled function
            updScrollVar()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x000C], "Memory 0x000C mismatch")
        }
    }

    /**
     * Test case 9 from frame 8405
     * Function: updScrollVar (0xAF6B)
     * Call depth: 5
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `updScrollVar_frame8405_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x071F] = 0x00u
            memory[0x073D] = 0x1Eu
            memory[0x0773] = 0x00u

            // Execute decompiled function
            updScrollVar()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x000C], "Memory 0x000C mismatch")
        }
    }

    // =========================================
    // 0xAF8F: scrollHandler
    // 160 calls, 65 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 1123
     * Function: scrollHandler (0xAF8F)
     * Call depth: 5
     * Memory reads: 0, writes: 2
     */
    @Test
    fun `scrollHandler_frame1123_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            scrollHandler()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 1135
     * Function: scrollHandler (0xAF8F)
     * Call depth: 5
     * Memory reads: 0, writes: 2
     */
    @Test
    fun `scrollHandler_frame1135_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            scrollHandler()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 2 from frame 1255
     * Function: scrollHandler (0xAF8F)
     * Call depth: 7
     * Memory reads: 0, writes: 2
     */
    @Test
    fun `scrollHandler_frame1255_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            scrollHandler()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 3 from frame 1321
     * Function: scrollHandler (0xAF8F)
     * Call depth: 7
     * Memory reads: 3, writes: 2
     */
    @Test
    fun `scrollHandler_frame1321_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01FA] = 0x77u
            memory[0x01FB] = 0x81u
            memory[0x01FC] = 0x14u

            // Execute decompiled function
            scrollHandler()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 4 from frame 3737
     * Function: scrollHandler (0xAF8F)
     * Call depth: 7
     * Memory reads: 0, writes: 2
     */
    @Test
    fun `scrollHandler_frame3737_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            scrollHandler()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 5 from frame 4361
     * Function: scrollHandler (0xAF8F)
     * Call depth: 7
     * Memory reads: 3, writes: 2
     */
    @Test
    fun `scrollHandler_frame4361_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01FA] = 0x77u
            memory[0x01FB] = 0x81u
            memory[0x01FC] = 0x14u

            // Execute decompiled function
            scrollHandler()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 6 from frame 4431
     * Function: scrollHandler (0xAF8F)
     * Call depth: 5
     * Memory reads: 0, writes: 2
     */
    @Test
    fun `scrollHandler_frame4431_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            scrollHandler()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 7 from frame 4665
     * Function: scrollHandler (0xAF8F)
     * Call depth: 5
     * Memory reads: 3, writes: 2
     */
    @Test
    fun `scrollHandler_frame4665_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01FA] = 0x77u
            memory[0x01FB] = 0x81u
            memory[0x01FC] = 0x11u

            // Execute decompiled function
            scrollHandler()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 8 from frame 4951
     * Function: scrollHandler (0xAF8F)
     * Call depth: 5
     * Memory reads: 3, writes: 2
     */
    @Test
    fun `scrollHandler_frame4951_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01FA] = 0x77u
            memory[0x01FB] = 0x81u
            memory[0x01FC] = 0x15u

            // Execute decompiled function
            scrollHandler()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 9 from frame 6667
     * Function: scrollHandler (0xAF8F)
     * Call depth: 5
     * Memory reads: 0, writes: 2
     */
    @Test
    fun `scrollHandler_frame6667_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            scrollHandler()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0xB046: gameRoutines
    // 577 calls, 7 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 6
     * Function: gameRoutines (0xB046)
     * Call depth: 4
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `gameRoutines_frame6_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            gameRoutines()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x071B], "Memory 0x071B mismatch")
        }
    }

    /**
     * Test case 1 from frame 760
     * Function: gameRoutines (0xB046)
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `gameRoutines_frame760_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            gameRoutines()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x01u, memory[0x071B], "Memory 0x071B mismatch")
        }
    }

    /**
     * Test case 2 from frame 1260
     * Function: gameRoutines (0xB046)
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `gameRoutines_frame1260_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            gameRoutines()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x01u, memory[0x071B], "Memory 0x071B mismatch")
        }
    }

    /**
     * Test case 3 from frame 4590
     * Function: gameRoutines (0xB046)
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `gameRoutines_frame4590_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            gameRoutines()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x02u, memory[0x071B], "Memory 0x071B mismatch")
        }
    }

    /**
     * Test case 4 from frame 4926
     * Function: gameRoutines (0xB046)
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `gameRoutines_frame4926_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            gameRoutines()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x02u, memory[0x071B], "Memory 0x071B mismatch")
        }
    }

    /**
     * Test case 5 from frame 6904
     * Function: gameRoutines (0xB046)
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `gameRoutines_frame6904_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            gameRoutines()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x02u, memory[0x071B], "Memory 0x071B mismatch")
        }
    }

    /**
     * Test case 6 from frame 8662
     * Function: gameRoutines (0xB046)
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `gameRoutines_frame8662_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            gameRoutines()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x03u, memory[0x071B], "Memory 0x071B mismatch")
        }
    }

    // =========================================
    // 0xB0DF: autoControlPlayer
    // Parameters: A
    // 2 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 198
     * Function: autoControlPlayer (0xB0DF)
     * Parameters: A
     * Call depth: 5
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `autoControlPlayer_frame198_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x074E] = 0x01u
            memory[0x0756] = 0x00u

            // Execute decompiled function
            autoControlPlayer(0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0716], "Memory 0x0716 mismatch")
        }
    }

    // =========================================
    // 0xB0E2: playerCtrlRoutine
    // 2 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 198
     * Function: playerCtrlRoutine (0xB0E2)
     * Call depth: 5
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `playerCtrlRoutine_frame198_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            playerCtrlRoutine()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0758], "Memory 0x0758 mismatch")
        }
    }

    // =========================================
    // 0xB26C: donePlayerTask
    // 208 calls, 208 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 1546
     * Function: donePlayerTask (0xB26C)
     * Call depth: 5
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `donePlayerTask_frame1546_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            donePlayerTask()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 1586
     * Function: donePlayerTask (0xB26C)
     * Call depth: 5
     * Memory reads: 7, writes: 5
     */
    @Test
    fun `donePlayerTask_frame1586_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (7 addresses)
            memory[0x000E] = 0x0Bu
            memory[0x01F8] = 0xF5u
            memory[0x01F9] = 0xAEu
            memory[0x01FA] = 0x77u
            memory[0x01FB] = 0x81u
            memory[0x01FC] = 0x10u
            memory[0x0772] = 0x03u

            // Execute decompiled function
            donePlayerTask()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
        }
    }

    /**
     * Test case 2 from frame 1628
     * Function: donePlayerTask (0xB26C)
     * Call depth: 5
     * Memory reads: 7, writes: 5
     */
    @Test
    fun `donePlayerTask_frame1628_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (7 addresses)
            memory[0x000E] = 0x0Bu
            memory[0x01F8] = 0xF5u
            memory[0x01F9] = 0xAEu
            memory[0x01FA] = 0x77u
            memory[0x01FB] = 0x81u
            memory[0x01FC] = 0x10u
            memory[0x0772] = 0x03u

            // Execute decompiled function
            donePlayerTask()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
        }
    }

    /**
     * Test case 3 from frame 1670
     * Function: donePlayerTask (0xB26C)
     * Call depth: 5
     * Memory reads: 7, writes: 5
     */
    @Test
    fun `donePlayerTask_frame1670_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (7 addresses)
            memory[0x000E] = 0x0Bu
            memory[0x01F8] = 0xF5u
            memory[0x01F9] = 0xAEu
            memory[0x01FA] = 0x77u
            memory[0x01FB] = 0x81u
            memory[0x01FC] = 0x10u
            memory[0x0772] = 0x03u

            // Execute decompiled function
            donePlayerTask()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
        }
    }

    /**
     * Test case 4 from frame 1712
     * Function: donePlayerTask (0xB26C)
     * Call depth: 5
     * Memory reads: 7, writes: 5
     */
    @Test
    fun `donePlayerTask_frame1712_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (7 addresses)
            memory[0x000E] = 0x0Bu
            memory[0x01F8] = 0xF5u
            memory[0x01F9] = 0xAEu
            memory[0x01FA] = 0x77u
            memory[0x01FB] = 0x81u
            memory[0x01FC] = 0x10u
            memory[0x0772] = 0x03u

            // Execute decompiled function
            donePlayerTask()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
        }
    }

    /**
     * Test case 5 from frame 1754
     * Function: donePlayerTask (0xB26C)
     * Call depth: 5
     * Memory reads: 7, writes: 5
     */
    @Test
    fun `donePlayerTask_frame1754_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (7 addresses)
            memory[0x000E] = 0x0Bu
            memory[0x01F8] = 0xF5u
            memory[0x01F9] = 0xAEu
            memory[0x01FA] = 0x77u
            memory[0x01FB] = 0x81u
            memory[0x01FC] = 0x10u
            memory[0x0772] = 0x03u

            // Execute decompiled function
            donePlayerTask()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
        }
    }

    /**
     * Test case 6 from frame 1794
     * Function: donePlayerTask (0xB26C)
     * Call depth: 5
     * Memory reads: 7, writes: 5
     */
    @Test
    fun `donePlayerTask_frame1794_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (7 addresses)
            memory[0x000E] = 0x0Bu
            memory[0x01F8] = 0xF5u
            memory[0x01F9] = 0xAEu
            memory[0x01FA] = 0x77u
            memory[0x01FB] = 0x81u
            memory[0x01FC] = 0x10u
            memory[0x0772] = 0x03u

            // Execute decompiled function
            donePlayerTask()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
        }
    }

    /**
     * Test case 7 from frame 1836
     * Function: donePlayerTask (0xB26C)
     * Call depth: 5
     * Memory reads: 7, writes: 5
     */
    @Test
    fun `donePlayerTask_frame1836_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (7 addresses)
            memory[0x000E] = 0x0Bu
            memory[0x01F8] = 0xF5u
            memory[0x01F9] = 0xAEu
            memory[0x01FA] = 0x77u
            memory[0x01FB] = 0x81u
            memory[0x01FC] = 0x10u
            memory[0x0772] = 0x03u

            // Execute decompiled function
            donePlayerTask()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
        }
    }

    /**
     * Test case 8 from frame 1878
     * Function: donePlayerTask (0xB26C)
     * Call depth: 5
     * Memory reads: 7, writes: 5
     */
    @Test
    fun `donePlayerTask_frame1878_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (7 addresses)
            memory[0x000E] = 0x0Bu
            memory[0x01F8] = 0xF5u
            memory[0x01F9] = 0xAEu
            memory[0x01FA] = 0x77u
            memory[0x01FB] = 0x81u
            memory[0x01FC] = 0x10u
            memory[0x0772] = 0x03u

            // Execute decompiled function
            donePlayerTask()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
        }
    }

    /**
     * Test case 9 from frame 1920
     * Function: donePlayerTask (0xB26C)
     * Call depth: 5
     * Memory reads: 7, writes: 5
     */
    @Test
    fun `donePlayerTask_frame1920_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (7 addresses)
            memory[0x000E] = 0x0Bu
            memory[0x01F8] = 0xF5u
            memory[0x01F9] = 0xAEu
            memory[0x01FA] = 0x77u
            memory[0x01FB] = 0x81u
            memory[0x01FC] = 0x10u
            memory[0x0772] = 0x03u

            // Execute decompiled function
            donePlayerTask()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
        }
    }

    // =========================================
    // 0xB293: resetPalStar
    // 122 calls, 12 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 1719
     * Function: resetPalStar (0xB293)
     * Call depth: 7
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `resetPalStar_frame1719_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            resetPalStar()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x02u, memory[0x03C4], "Memory 0x03C4 mismatch")
        }
    }

    /**
     * Test case 1 from frame 1721
     * Function: resetPalStar (0xB293)
     * Call depth: 7
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `resetPalStar_frame1721_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            resetPalStar()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x03u, memory[0x03C4], "Memory 0x03C4 mismatch")
        }
    }

    /**
     * Test case 2 from frame 1723
     * Function: resetPalStar (0xB293)
     * Call depth: 7
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `resetPalStar_frame1723_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            resetPalStar()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x03u, memory[0x03C4], "Memory 0x03C4 mismatch")
        }
    }

    /**
     * Test case 3 from frame 1729
     * Function: resetPalStar (0xB293)
     * Call depth: 7
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `resetPalStar_frame1729_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            resetPalStar()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x03u, memory[0x03C4], "Memory 0x03C4 mismatch")
        }
    }

    /**
     * Test case 4 from frame 1737
     * Function: resetPalStar (0xB293)
     * Call depth: 7
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `resetPalStar_frame1737_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            resetPalStar()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
        }
    }

    /**
     * Test case 5 from frame 1745
     * Function: resetPalStar (0xB293)
     * Call depth: 7
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `resetPalStar_frame1745_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            resetPalStar()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
        }
    }

    /**
     * Test case 6 from frame 1753
     * Function: resetPalStar (0xB293)
     * Call depth: 7
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `resetPalStar_frame1753_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            resetPalStar()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x01u, memory[0x03C4], "Memory 0x03C4 mismatch")
        }
    }

    /**
     * Test case 7 from frame 1755
     * Function: resetPalStar (0xB293)
     * Call depth: 7
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `resetPalStar_frame1755_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            resetPalStar()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x01u, memory[0x03C4], "Memory 0x03C4 mismatch")
        }
    }

    /**
     * Test case 8 from frame 1761
     * Function: resetPalStar (0xB293)
     * Call depth: 7
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `resetPalStar_frame1761_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            resetPalStar()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x01u, memory[0x03C4], "Memory 0x03C4 mismatch")
        }
    }

    /**
     * Test case 9 from frame 1769
     * Function: resetPalStar (0xB293)
     * Call depth: 7
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `resetPalStar_frame1769_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            resetPalStar()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x02u, memory[0x03C4], "Memory 0x03C4 mismatch")
        }
    }

    // =========================================
    // 0xB29D: flagpoleSlide
    // 8631 calls, 18 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 33
     * Function: flagpoleSlide (0xB29D)
     * Call depth: 6
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `flagpoleSlide_frame33_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            flagpoleSlide()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
        }
    }

    /**
     * Test case 1 from frame 197
     * Function: flagpoleSlide (0xB29D)
     * Call depth: 5
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `flagpoleSlide_frame197_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            flagpoleSlide()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
        }
    }

    /**
     * Test case 2 from frame 201
     * Function: flagpoleSlide (0xB29D)
     * Call depth: 6
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `flagpoleSlide_frame201_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            flagpoleSlide()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
        }
    }

    /**
     * Test case 3 from frame 215
     * Function: flagpoleSlide (0xB29D)
     * Call depth: 6
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `flagpoleSlide_frame215_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            flagpoleSlide()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
        }
    }

    /**
     * Test case 4 from frame 225
     * Function: flagpoleSlide (0xB29D)
     * Call depth: 6
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `flagpoleSlide_frame225_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            flagpoleSlide()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
        }
    }

    /**
     * Test case 5 from frame 233
     * Function: flagpoleSlide (0xB29D)
     * Call depth: 6
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `flagpoleSlide_frame233_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            flagpoleSlide()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
        }
    }

    /**
     * Test case 6 from frame 249
     * Function: flagpoleSlide (0xB29D)
     * Call depth: 6
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `flagpoleSlide_frame249_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            flagpoleSlide()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
        }
    }

    /**
     * Test case 7 from frame 265
     * Function: flagpoleSlide (0xB29D)
     * Call depth: 6
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `flagpoleSlide_frame265_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            flagpoleSlide()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
        }
    }

    /**
     * Test case 8 from frame 1257
     * Function: flagpoleSlide (0xB29D)
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `flagpoleSlide_frame1257_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            flagpoleSlide()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
        }
    }

    /**
     * Test case 9 from frame 4393
     * Function: flagpoleSlide (0xB29D)
     * Call depth: 6
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `flagpoleSlide_frame4393_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            flagpoleSlide()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
        }
    }

    // =========================================
    // 0xB366: fallingSub
    // 4771 calls, 71 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 200
     * Function: fallingSub (0xB366)
     * Call depth: 7
     * Memory reads: 0, writes: 2
     */
    @Test
    fun `fallingSub_frame200_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            fallingSub()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 658
     * Function: fallingSub (0xB366)
     * Call depth: 7
     * Memory reads: 0, writes: 2
     */
    @Test
    fun `fallingSub_frame658_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            fallingSub()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 2 from frame 758
     * Function: fallingSub (0xB366)
     * Call depth: 7
     * Memory reads: 0, writes: 2
     */
    @Test
    fun `fallingSub_frame758_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            fallingSub()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 3 from frame 1084
     * Function: fallingSub (0xB366)
     * Call depth: 7
     * Memory reads: 0, writes: 2
     */
    @Test
    fun `fallingSub_frame1084_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            fallingSub()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 4 from frame 1266
     * Function: fallingSub (0xB366)
     * Call depth: 7
     * Memory reads: 0, writes: 2
     */
    @Test
    fun `fallingSub_frame1266_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            fallingSub()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 5 from frame 3676
     * Function: fallingSub (0xB366)
     * Call depth: 7
     * Memory reads: 0, writes: 2
     */
    @Test
    fun `fallingSub_frame3676_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            fallingSub()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 6 from frame 6904
     * Function: fallingSub (0xB366)
     * Call depth: 7
     * Memory reads: 0, writes: 2
     */
    @Test
    fun `fallingSub_frame6904_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            fallingSub()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 7 from frame 7658
     * Function: fallingSub (0xB366)
     * Call depth: 7
     * Memory reads: 0, writes: 2
     */
    @Test
    fun `fallingSub_frame7658_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            fallingSub()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 8 from frame 16522
     * Function: fallingSub (0xB366)
     * Call depth: 7
     * Memory reads: 0, writes: 2
     */
    @Test
    fun `fallingSub_frame16522_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            fallingSub()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 9 from frame 16592
     * Function: fallingSub (0xB366)
     * Call depth: 7
     * Memory reads: 0, writes: 2
     */
    @Test
    fun `fallingSub_frame16592_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            fallingSub()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0xB588: getPlayerAnimSpeed
    // 1413 calls, 7 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 200
     * Function: getPlayerAnimSpeed (0xB588)
     * Call depth: 7
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `getPlayerAnimSpeed_frame200_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0701] = 0x00u

            // Execute decompiled function
            getPlayerAnimSpeed()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x01u, memory[0x0701], "Memory 0x0701 mismatch")
        }
    }

    /**
     * Test case 1 from frame 228
     * Function: getPlayerAnimSpeed (0xB588)
     * Call depth: 7
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `getPlayerAnimSpeed_frame228_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0701] = 0x00u

            // Execute decompiled function
            getPlayerAnimSpeed()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x01u, memory[0x0701], "Memory 0x0701 mismatch")
        }
    }

    /**
     * Test case 2 from frame 664
     * Function: getPlayerAnimSpeed (0xB588)
     * Call depth: 7
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `getPlayerAnimSpeed_frame664_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0701] = 0x00u

            // Execute decompiled function
            getPlayerAnimSpeed()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x01u, memory[0x0701], "Memory 0x0701 mismatch")
        }
    }

    /**
     * Test case 3 from frame 3612
     * Function: getPlayerAnimSpeed (0xB588)
     * Call depth: 7
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `getPlayerAnimSpeed_frame3612_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0701] = 0x00u

            // Execute decompiled function
            getPlayerAnimSpeed()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x01u, memory[0x0701], "Memory 0x0701 mismatch")
        }
    }

    /**
     * Test case 4 from frame 3730
     * Function: getPlayerAnimSpeed (0xB588)
     * Call depth: 7
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `getPlayerAnimSpeed_frame3730_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0701] = 0x00u

            // Execute decompiled function
            getPlayerAnimSpeed()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x01u, memory[0x0701], "Memory 0x0701 mismatch")
        }
    }

    /**
     * Test case 5 from frame 3984
     * Function: getPlayerAnimSpeed (0xB588)
     * Call depth: 7
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `getPlayerAnimSpeed_frame3984_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0701] = 0x00u

            // Execute decompiled function
            getPlayerAnimSpeed()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x01u, memory[0x0701], "Memory 0x0701 mismatch")
        }
    }

    /**
     * Test case 6 from frame 4524
     * Function: getPlayerAnimSpeed (0xB588)
     * Call depth: 7
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `getPlayerAnimSpeed_frame4524_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0701] = 0x00u

            // Execute decompiled function
            getPlayerAnimSpeed()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x01u, memory[0x0701], "Memory 0x0701 mismatch")
        }
    }

    // =========================================
    // 0xB5BE: setAnimSpd
    // Parameters: Y
    // 160 calls, 57 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 200
     * Function: setAnimSpd (0xB5BE)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 9, writes: 4
     */
    @Test
    fun `setAnimSpd_frame200_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0057] = 0x00u
            memory[0x0086] = 0x28u
            memory[0x00CE] = 0xB0u
            memory[0x0456] = 0x18u
            memory[0x0490] = 0xFFu
            memory[0x0701] = 0x01u
            memory[0x0702] = 0x30u
            memory[0x0705] = 0x00u
            memory[0x071C] = 0x00u

            // Execute decompiled function
            setAnimSpd(0x02)

            // Verify: Check output memory (4 addresses)
            assertEquals(0x01u, memory[0x0057], "Memory 0x0057 mismatch")
            assertEquals(0x28u, memory[0x03AD], "Memory 0x03AD mismatch")
            assertEquals(0xB0u, memory[0x03B8], "Memory 0x03B8 mismatch")
            assertEquals(0x30u, memory[0x0705], "Memory 0x0705 mismatch")
        }
    }

    /**
     * Test case 1 from frame 682
     * Function: setAnimSpd (0xB5BE)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 9, writes: 4
     */
    @Test
    fun `setAnimSpd_frame682_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0057] = 0x00u
            memory[0x0086] = 0x34u
            memory[0x00CE] = 0xB0u
            memory[0x0456] = 0x18u
            memory[0x0490] = 0xFFu
            memory[0x0701] = 0x01u
            memory[0x0702] = 0x30u
            memory[0x0705] = 0x00u
            memory[0x071C] = 0x00u

            // Execute decompiled function
            setAnimSpd(0x02)

            // Verify: Check output memory (4 addresses)
            assertEquals(0x01u, memory[0x0057], "Memory 0x0057 mismatch")
            assertEquals(0x34u, memory[0x03AD], "Memory 0x03AD mismatch")
            assertEquals(0xB0u, memory[0x03B8], "Memory 0x03B8 mismatch")
            assertEquals(0x30u, memory[0x0705], "Memory 0x0705 mismatch")
        }
    }

    /**
     * Test case 2 from frame 2530
     * Function: setAnimSpd (0xB5BE)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 9, writes: 4
     */
    @Test
    fun `setAnimSpd_frame2530_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0057] = 0x00u
            memory[0x0086] = 0x29u
            memory[0x00CE] = 0xB0u
            memory[0x0456] = 0x18u
            memory[0x0490] = 0xFFu
            memory[0x0701] = 0x01u
            memory[0x0702] = 0x30u
            memory[0x0705] = 0x00u
            memory[0x071C] = 0x00u

            // Execute decompiled function
            setAnimSpd(0x02)

            // Verify: Check output memory (4 addresses)
            assertEquals(0x01u, memory[0x0057], "Memory 0x0057 mismatch")
            assertEquals(0x29u, memory[0x03AD], "Memory 0x03AD mismatch")
            assertEquals(0xB0u, memory[0x03B8], "Memory 0x03B8 mismatch")
            assertEquals(0x30u, memory[0x0705], "Memory 0x0705 mismatch")
        }
    }

    /**
     * Test case 3 from frame 3982
     * Function: setAnimSpd (0xB5BE)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 9, writes: 4
     */
    @Test
    fun `setAnimSpd_frame3982_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0057] = 0x00u
            memory[0x0086] = 0xB2u
            memory[0x00CE] = 0xB0u
            memory[0x0456] = 0x18u
            memory[0x0490] = 0xFFu
            memory[0x0701] = 0x00u
            memory[0x0702] = 0xD0u
            memory[0x0705] = 0x00u
            memory[0x071C] = 0x46u

            // Execute decompiled function
            setAnimSpd(0x02)

            // Verify: Check output memory (4 addresses)
            assertEquals(0x00u, memory[0x0057], "Memory 0x0057 mismatch")
            assertEquals(0x6Cu, memory[0x03AD], "Memory 0x03AD mismatch")
            assertEquals(0xB0u, memory[0x03B8], "Memory 0x03B8 mismatch")
            assertEquals(0xD0u, memory[0x0705], "Memory 0x0705 mismatch")
        }
    }

    /**
     * Test case 4 from frame 6818
     * Function: setAnimSpd (0xB5BE)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 9, writes: 4
     */
    @Test
    fun `setAnimSpd_frame6818_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0057] = 0x00u
            memory[0x0086] = 0x11u
            memory[0x00CE] = 0xB0u
            memory[0x0456] = 0x18u
            memory[0x0490] = 0xFFu
            memory[0x0701] = 0x01u
            memory[0x0702] = 0x30u
            memory[0x0705] = 0x00u
            memory[0x071C] = 0xA1u

            // Execute decompiled function
            setAnimSpd(0x02)

            // Verify: Check output memory (4 addresses)
            assertEquals(0x01u, memory[0x0057], "Memory 0x0057 mismatch")
            assertEquals(0x70u, memory[0x03AD], "Memory 0x03AD mismatch")
            assertEquals(0xB0u, memory[0x03B8], "Memory 0x03B8 mismatch")
            assertEquals(0x30u, memory[0x0705], "Memory 0x0705 mismatch")
        }
    }

    /**
     * Test case 5 from frame 8232
     * Function: setAnimSpd (0xB5BE)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 9, writes: 4
     */
    @Test
    fun `setAnimSpd_frame8232_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0057] = 0x00u
            memory[0x0086] = 0x51u
            memory[0x00CE] = 0xB0u
            memory[0x0456] = 0x18u
            memory[0x0490] = 0xFFu
            memory[0x0701] = 0x00u
            memory[0x0702] = 0x98u
            memory[0x0705] = 0x00u
            memory[0x071C] = 0xFCu

            // Execute decompiled function
            setAnimSpd(0x02)

            // Verify: Check output memory (4 addresses)
            assertEquals(0x00u, memory[0x0057], "Memory 0x0057 mismatch")
            assertEquals(0x55u, memory[0x03AD], "Memory 0x03AD mismatch")
            assertEquals(0xB0u, memory[0x03B8], "Memory 0x03B8 mismatch")
            assertEquals(0x98u, memory[0x0705], "Memory 0x0705 mismatch")
        }
    }

    /**
     * Test case 6 from frame 9214
     * Function: setAnimSpd (0xB5BE)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 9, writes: 4
     */
    @Test
    fun `setAnimSpd_frame9214_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0057] = 0x00u
            memory[0x0086] = 0x50u
            memory[0x00CE] = 0xB0u
            memory[0x0456] = 0x18u
            memory[0x0490] = 0xFFu
            memory[0x0701] = 0x01u
            memory[0x0702] = 0x30u
            memory[0x0705] = 0x00u
            memory[0x071C] = 0x03u

            // Execute decompiled function
            setAnimSpd(0x02)

            // Verify: Check output memory (4 addresses)
            assertEquals(0x01u, memory[0x0057], "Memory 0x0057 mismatch")
            assertEquals(0x4Du, memory[0x03AD], "Memory 0x03AD mismatch")
            assertEquals(0xB0u, memory[0x03B8], "Memory 0x03B8 mismatch")
            assertEquals(0x30u, memory[0x0705], "Memory 0x0705 mismatch")
        }
    }

    /**
     * Test case 7 from frame 11298
     * Function: setAnimSpd (0xB5BE)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 9, writes: 4
     */
    @Test
    fun `setAnimSpd_frame11298_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0057] = 0x00u
            memory[0x0086] = 0x4Fu
            memory[0x00CE] = 0xB0u
            memory[0x0456] = 0x18u
            memory[0x0490] = 0xFFu
            memory[0x0701] = 0x00u
            memory[0x0702] = 0x98u
            memory[0x0705] = 0x00u
            memory[0x071C] = 0x03u

            // Execute decompiled function
            setAnimSpd(0x02)

            // Verify: Check output memory (4 addresses)
            assertEquals(0x00u, memory[0x0057], "Memory 0x0057 mismatch")
            assertEquals(0x4Cu, memory[0x03AD], "Memory 0x03AD mismatch")
            assertEquals(0xB0u, memory[0x03B8], "Memory 0x03B8 mismatch")
            assertEquals(0x98u, memory[0x0705], "Memory 0x0705 mismatch")
        }
    }

    /**
     * Test case 8 from frame 12316
     * Function: setAnimSpd (0xB5BE)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 9, writes: 4
     */
    @Test
    fun `setAnimSpd_frame12316_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0057] = 0x00u
            memory[0x0086] = 0x40u
            memory[0x00CE] = 0xB0u
            memory[0x0456] = 0x18u
            memory[0x0490] = 0xFFu
            memory[0x0701] = 0x01u
            memory[0x0702] = 0x30u
            memory[0x0705] = 0x00u
            memory[0x071C] = 0x03u

            // Execute decompiled function
            setAnimSpd(0x02)

            // Verify: Check output memory (4 addresses)
            assertEquals(0x01u, memory[0x0057], "Memory 0x0057 mismatch")
            assertEquals(0x3Du, memory[0x03AD], "Memory 0x03AD mismatch")
            assertEquals(0xB0u, memory[0x03B8], "Memory 0x03B8 mismatch")
            assertEquals(0x30u, memory[0x0705], "Memory 0x0705 mismatch")
        }
    }

    /**
     * Test case 9 from frame 16740
     * Function: setAnimSpd (0xB5BE)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 9, writes: 4
     */
    @Test
    fun `setAnimSpd_frame16740_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0057] = 0x00u
            memory[0x0086] = 0x0Du
            memory[0x00CE] = 0xB0u
            memory[0x0456] = 0x18u
            memory[0x0490] = 0xFFu
            memory[0x0701] = 0x01u
            memory[0x0702] = 0x30u
            memory[0x0705] = 0x00u
            memory[0x071C] = 0x03u

            // Execute decompiled function
            setAnimSpd(0x02)

            // Verify: Check output memory (4 addresses)
            assertEquals(0x01u, memory[0x0057], "Memory 0x0057 mismatch")
            assertEquals(0x0Au, memory[0x03AD], "Memory 0x03AD mismatch")
            assertEquals(0xB0u, memory[0x03B8], "Memory 0x03B8 mismatch")
            assertEquals(0x30u, memory[0x0705], "Memory 0x0705 mismatch")
        }
    }

    // =========================================
    // 0xB5C5: imposeFriction
    // Parameters: A
    // 4771 calls, 27 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 200
     * Function: imposeFriction (0xB5C5)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `imposeFriction_frame200_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            imposeFriction(0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x07u, memory[0x070C], "Memory 0x070C mismatch")
        }
    }

    /**
     * Test case 1 from frame 746
     * Function: imposeFriction (0xB5C5)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `imposeFriction_frame746_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            imposeFriction(0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x04u, memory[0x070C], "Memory 0x070C mismatch")
        }
    }

    /**
     * Test case 2 from frame 760
     * Function: imposeFriction (0xB5C5)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `imposeFriction_frame760_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            imposeFriction(0x0B)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x07u, memory[0x070C], "Memory 0x070C mismatch")
        }
    }

    /**
     * Test case 3 from frame 3670
     * Function: imposeFriction (0xB5C5)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `imposeFriction_frame3670_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            imposeFriction(0x1C)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x02u, memory[0x070C], "Memory 0x070C mismatch")
        }
    }

    /**
     * Test case 4 from frame 3674
     * Function: imposeFriction (0xB5C5)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `imposeFriction_frame3674_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            imposeFriction(0x1E)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x02u, memory[0x070C], "Memory 0x070C mismatch")
        }
    }

    /**
     * Test case 5 from frame 3728
     * Function: imposeFriction (0xB5C5)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `imposeFriction_frame3728_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            imposeFriction(0x17)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x04u, memory[0x070C], "Memory 0x070C mismatch")
        }
    }

    /**
     * Test case 6 from frame 3734
     * Function: imposeFriction (0xB5C5)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `imposeFriction_frame3734_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            imposeFriction(0x13)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x04u, memory[0x070C], "Memory 0x070C mismatch")
        }
    }

    /**
     * Test case 7 from frame 3742
     * Function: imposeFriction (0xB5C5)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `imposeFriction_frame3742_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            imposeFriction(0x0C)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x07u, memory[0x070C], "Memory 0x070C mismatch")
        }
    }

    /**
     * Test case 8 from frame 6906
     * Function: imposeFriction (0xB5C5)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `imposeFriction_frame6906_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            imposeFriction(0x23)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x02u, memory[0x070C], "Memory 0x070C mismatch")
        }
    }

    /**
     * Test case 9 from frame 6914
     * Function: imposeFriction (0xB5C5)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `imposeFriction_frame6914_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            imposeFriction(0x26)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x02u, memory[0x070C], "Memory 0x070C mismatch")
        }
    }

    // =========================================
    // 0xB619: setAbsSpd
    // Parameters: A
    // 1551 calls, 323 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 200
     * Function: setAbsSpd (0xB619)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `setAbsSpd_frame200_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            setAbsSpd(0x01)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x01u, memory[0x0700], "Memory 0x0700 mismatch")
        }
    }

    /**
     * Test case 1 from frame 726
     * Function: setAbsSpd (0xB619)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `setAbsSpd_frame726_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            setAbsSpd(0x11)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x11u, memory[0x0700], "Memory 0x0700 mismatch")
        }
    }

    /**
     * Test case 2 from frame 2948
     * Function: setAbsSpd (0xB619)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `setAbsSpd_frame2948_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            setAbsSpd(0x05)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x05u, memory[0x0700], "Memory 0x0700 mismatch")
        }
    }

    /**
     * Test case 3 from frame 3756
     * Function: setAbsSpd (0xB619)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 6, writes: 5
     */
    @Test
    fun `setAbsSpd_frame3756_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (6 addresses)
            memory[0x0057] = 0xFFu
            memory[0x0086] = 0xB4u
            memory[0x00CE] = 0x98u
            memory[0x0400] = 0xF0u
            memory[0x070E] = 0x00u
            memory[0x071C] = 0x46u

            // Execute decompiled function
            setAbsSpd(0xFF)

            // Verify: Check output memory (5 addresses)
            assertEquals(0xFFu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xF0u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0xFFu, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x6Eu, memory[0x03AD], "Memory 0x03AD mismatch")
            assertEquals(0x98u, memory[0x03B8], "Memory 0x03B8 mismatch")
        }
    }

    /**
     * Test case 4 from frame 6624
     * Function: setAbsSpd (0xB619)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 3, writes: 2
     */
    @Test
    fun `setAbsSpd_frame6624_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x0086] = 0xE4u
            memory[0x00CE] = 0xB0u
            memory[0x071C] = 0x74u

            // Execute decompiled function
            setAbsSpd(0xFE)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x70u, memory[0x03AD], "Memory 0x03AD mismatch")
            assertEquals(0xB0u, memory[0x03B8], "Memory 0x03B8 mismatch")
        }
    }

    /**
     * Test case 5 from frame 7680
     * Function: setAbsSpd (0xB619)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 3, writes: 2
     */
    @Test
    fun `setAbsSpd_frame7680_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x0086] = 0x4Eu
            memory[0x00CE] = 0xB0u
            memory[0x071C] = 0xE9u

            // Execute decompiled function
            setAbsSpd(0xFA)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x65u, memory[0x03AD], "Memory 0x03AD mismatch")
            assertEquals(0xB0u, memory[0x03B8], "Memory 0x03B8 mismatch")
        }
    }

    /**
     * Test case 6 from frame 9906
     * Function: setAbsSpd (0xB619)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 6, writes: 5
     */
    @Test
    fun `setAbsSpd_frame9906_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (6 addresses)
            memory[0x0057] = 0xFCu
            memory[0x0086] = 0x49u
            memory[0x00CE] = 0xAAu
            memory[0x0400] = 0xD0u
            memory[0x070E] = 0x00u
            memory[0x071C] = 0x03u

            // Execute decompiled function
            setAbsSpd(0xFC)

            // Verify: Check output memory (5 addresses)
            assertEquals(0xFFu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xC0u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0xFFu, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x46u, memory[0x03AD], "Memory 0x03AD mismatch")
            assertEquals(0xAAu, memory[0x03B8], "Memory 0x03B8 mismatch")
        }
    }

    /**
     * Test case 7 from frame 12282
     * Function: setAbsSpd (0xB619)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 6, writes: 5
     */
    @Test
    fun `setAbsSpd_frame12282_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (6 addresses)
            memory[0x0057] = 0xFDu
            memory[0x0086] = 0x48u
            memory[0x00CE] = 0x7Bu
            memory[0x0400] = 0x00u
            memory[0x070E] = 0x00u
            memory[0x071C] = 0x03u

            // Execute decompiled function
            setAbsSpd(0xFD)

            // Verify: Check output memory (5 addresses)
            assertEquals(0xFFu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xD0u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0xFFu, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x45u, memory[0x03AD], "Memory 0x03AD mismatch")
            assertEquals(0x7Bu, memory[0x03B8], "Memory 0x03B8 mismatch")
        }
    }

    /**
     * Test case 8 from frame 15624
     * Function: setAbsSpd (0xB619)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 6, writes: 5
     */
    @Test
    fun `setAbsSpd_frame15624_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (6 addresses)
            memory[0x0057] = 0xFFu
            memory[0x0086] = 0x52u
            memory[0x00CE] = 0x96u
            memory[0x0400] = 0x50u
            memory[0x070E] = 0x00u
            memory[0x071C] = 0x03u

            // Execute decompiled function
            setAbsSpd(0xFF)

            // Verify: Check output memory (5 addresses)
            assertEquals(0xFFu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xF0u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0xFFu, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x4Fu, memory[0x03AD], "Memory 0x03AD mismatch")
            assertEquals(0x96u, memory[0x03B8], "Memory 0x03B8 mismatch")
        }
    }

    /**
     * Test case 9 from frame 16592
     * Function: setAbsSpd (0xB619)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 3, writes: 2
     */
    @Test
    fun `setAbsSpd_frame16592_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x0086] = 0x1Fu
            memory[0x00CE] = 0xB0u
            memory[0x071C] = 0x03u

            // Execute decompiled function
            setAbsSpd(0xED)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x1Cu, memory[0x03AD], "Memory 0x03AD mismatch")
            assertEquals(0xB0u, memory[0x03B8], "Memory 0x03B8 mismatch")
        }
    }

    // =========================================
    // 0xB61D: procfireballBubble
    // 192 calls, 68 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 220
     * Function: procfireballBubble (0xB61D)
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `procfireballBubble_frame220_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            procfireballBubble()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x01u, memory[0x0700], "Memory 0x0700 mismatch")
        }
    }

    /**
     * Test case 1 from frame 7658
     * Function: procfireballBubble (0xB61D)
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `procfireballBubble_frame7658_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            procfireballBubble()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x03u, memory[0x0700], "Memory 0x0700 mismatch")
        }
    }

    /**
     * Test case 2 from frame 7688
     * Function: procfireballBubble (0xB61D)
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `procfireballBubble_frame7688_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            procfireballBubble()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x09u, memory[0x0700], "Memory 0x0700 mismatch")
        }
    }

    /**
     * Test case 3 from frame 9942
     * Function: procfireballBubble (0xB61D)
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `procfireballBubble_frame9942_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            procfireballBubble()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x02u, memory[0x0700], "Memory 0x0700 mismatch")
        }
    }

    /**
     * Test case 4 from frame 11978
     * Function: procfireballBubble (0xB61D)
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `procfireballBubble_frame11978_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            procfireballBubble()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x02u, memory[0x0700], "Memory 0x0700 mismatch")
        }
    }

    /**
     * Test case 5 from frame 12296
     * Function: procfireballBubble (0xB61D)
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `procfireballBubble_frame12296_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            procfireballBubble()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x0Cu, memory[0x0700], "Memory 0x0700 mismatch")
        }
    }

    /**
     * Test case 6 from frame 12312
     * Function: procfireballBubble (0xB61D)
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `procfireballBubble_frame12312_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            procfireballBubble()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x07u, memory[0x0700], "Memory 0x0700 mismatch")
        }
    }

    /**
     * Test case 7 from frame 16508
     * Function: procfireballBubble (0xB61D)
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `procfireballBubble_frame16508_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            procfireballBubble()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x07u, memory[0x0700], "Memory 0x0700 mismatch")
        }
    }

    /**
     * Test case 8 from frame 16532
     * Function: procfireballBubble (0xB61D)
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `procfireballBubble_frame16532_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            procfireballBubble()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x12u, memory[0x0700], "Memory 0x0700 mismatch")
        }
    }

    /**
     * Test case 9 from frame 16590
     * Function: procfireballBubble (0xB61D)
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `procfireballBubble_frame16590_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            procfireballBubble()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x14u, memory[0x0700], "Memory 0x0700 mismatch")
        }
    }

    // =========================================
    // 0xBB3D: setupJumpCoin
    // Parameters: X, Y
    // 1 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 4375
     * Function: setupJumpCoin (0xBB3D)
     * Parameters: X, Y
     * Call depth: 10
     * Memory reads: 2, writes: 7
     */
    @Test
    fun `setupJumpCoin_frame4375_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x008F] = 0x00u
            memory[0x00D7] = 0x90u

            // Execute decompiled function
            setupJumpCoin(0x00, 0x08)

            // Verify: Check output memory (7 addresses)
            assertEquals(0x01u, memory[0x0032], "Memory 0x0032 mismatch")
            assertEquals(0x01u, memory[0x0082], "Memory 0x0082 mismatch")
            assertEquals(0x05u, memory[0x009B], "Memory 0x009B mismatch")
            assertEquals(0xFBu, memory[0x00B4], "Memory 0x00B4 mismatch")
            assertEquals(0x01u, memory[0x00CA], "Memory 0x00CA mismatch")
            assertEquals(0x7Fu, memory[0x00E3], "Memory 0x00E3 mismatch")
            assertEquals(0x01u, memory[0x00FE], "Memory 0x00FE mismatch")
        }
    }

    // =========================================
    // 0xBB7B: miscObjectsCore
    // 1 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 4375
     * Function: miscObjectsCore (0xBB7B)
     * Call depth: 10
     * Memory reads: 3, writes: 4
     */
    @Test
    fun `miscObjectsCore_frame4375_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01F0] = 0x7Fu
            memory[0x01F1] = 0xBBu
            memory[0x0748] = 0x00u

            // Execute decompiled function
            miscObjectsCore()

            // Verify: Check output memory (2 addresses)
            assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
            assertEquals(0x01u, memory[0x0748], "Memory 0x0748 mismatch")
        }
    }

    // =========================================
    // 0xBBD9: miscLoopBack
    // Parameters: X
    // 32 calls, 11 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 4375
     * Function: miscLoopBack (0xBBD9)
     * Parameters: X
     * Call depth: 7
     * Memory reads: 12, writes: 3
     */
    @Test
    fun `miscLoopBack_frame4375_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (12 addresses)
            memory[0x0008] = 0x08u
            memory[0x002A] = 0x00u
            memory[0x002B] = 0x00u
            memory[0x002C] = 0x00u
            memory[0x002D] = 0x00u
            memory[0x002E] = 0x00u
            memory[0x002F] = 0x00u
            memory[0x0030] = 0x00u
            memory[0x0031] = 0x00u
            memory[0x00B4] = 0xFBu
            memory[0x01F6] = 0xDDu
            memory[0x01F7] = 0xBBu

            // Execute decompiled function
            miscLoopBack(0x15)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
        }
    }

    /**
     * Test case 1 from frame 4381
     * Function: miscLoopBack (0xBBD9)
     * Parameters: X
     * Call depth: 6
     * Memory reads: 12, writes: 3
     */
    @Test
    fun `miscLoopBack_frame4381_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (12 addresses)
            memory[0x0008] = 0x08u
            memory[0x002A] = 0x00u
            memory[0x002B] = 0x00u
            memory[0x002C] = 0x00u
            memory[0x002D] = 0x00u
            memory[0x002E] = 0x00u
            memory[0x002F] = 0x00u
            memory[0x0030] = 0x00u
            memory[0x0031] = 0x00u
            memory[0x00B4] = 0xFCu
            memory[0x01F6] = 0xDDu
            memory[0x01F7] = 0xBBu

            // Execute decompiled function
            miscLoopBack(0x15)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
        }
    }

    /**
     * Test case 2 from frame 4387
     * Function: miscLoopBack (0xBBD9)
     * Parameters: X
     * Call depth: 6
     * Memory reads: 12, writes: 3
     */
    @Test
    fun `miscLoopBack_frame4387_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (12 addresses)
            memory[0x0008] = 0x08u
            memory[0x002A] = 0x00u
            memory[0x002B] = 0x00u
            memory[0x002C] = 0x00u
            memory[0x002D] = 0x00u
            memory[0x002E] = 0x00u
            memory[0x002F] = 0x00u
            memory[0x0030] = 0x00u
            memory[0x0031] = 0x00u
            memory[0x00B4] = 0xFDu
            memory[0x01F6] = 0xDDu
            memory[0x01F7] = 0xBBu

            // Execute decompiled function
            miscLoopBack(0x15)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
        }
    }

    /**
     * Test case 3 from frame 4393
     * Function: miscLoopBack (0xBBD9)
     * Parameters: X
     * Call depth: 6
     * Memory reads: 12, writes: 3
     */
    @Test
    fun `miscLoopBack_frame4393_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (12 addresses)
            memory[0x0008] = 0x08u
            memory[0x002A] = 0x00u
            memory[0x002B] = 0x00u
            memory[0x002C] = 0x00u
            memory[0x002D] = 0x00u
            memory[0x002E] = 0x00u
            memory[0x002F] = 0x00u
            memory[0x0030] = 0x00u
            memory[0x0031] = 0x00u
            memory[0x00B4] = 0xFEu
            memory[0x01F6] = 0xDDu
            memory[0x01F7] = 0xBBu

            // Execute decompiled function
            miscLoopBack(0x15)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
        }
    }

    /**
     * Test case 4 from frame 4399
     * Function: miscLoopBack (0xBBD9)
     * Parameters: X
     * Call depth: 6
     * Memory reads: 12, writes: 3
     */
    @Test
    fun `miscLoopBack_frame4399_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (12 addresses)
            memory[0x0008] = 0x08u
            memory[0x002A] = 0x00u
            memory[0x002B] = 0x00u
            memory[0x002C] = 0x00u
            memory[0x002D] = 0x00u
            memory[0x002E] = 0x00u
            memory[0x002F] = 0x00u
            memory[0x0030] = 0x00u
            memory[0x0031] = 0x00u
            memory[0x00B4] = 0xFFu
            memory[0x01F6] = 0xDDu
            memory[0x01F7] = 0xBBu

            // Execute decompiled function
            miscLoopBack(0x15)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
        }
    }

    /**
     * Test case 5 from frame 4405
     * Function: miscLoopBack (0xBBD9)
     * Parameters: X
     * Call depth: 6
     * Memory reads: 12, writes: 3
     */
    @Test
    fun `miscLoopBack_frame4405_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (12 addresses)
            memory[0x0008] = 0x08u
            memory[0x002A] = 0x00u
            memory[0x002B] = 0x00u
            memory[0x002C] = 0x00u
            memory[0x002D] = 0x00u
            memory[0x002E] = 0x00u
            memory[0x002F] = 0x00u
            memory[0x0030] = 0x00u
            memory[0x0031] = 0x00u
            memory[0x00B4] = 0x00u
            memory[0x01F6] = 0xDDu
            memory[0x01F7] = 0xBBu

            // Execute decompiled function
            miscLoopBack(0x15)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
        }
    }

    /**
     * Test case 6 from frame 4413
     * Function: miscLoopBack (0xBBD9)
     * Parameters: X
     * Call depth: 6
     * Memory reads: 12, writes: 3
     */
    @Test
    fun `miscLoopBack_frame4413_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (12 addresses)
            memory[0x0008] = 0x08u
            memory[0x002A] = 0x00u
            memory[0x002B] = 0x00u
            memory[0x002C] = 0x00u
            memory[0x002D] = 0x00u
            memory[0x002E] = 0x00u
            memory[0x002F] = 0x00u
            memory[0x0030] = 0x00u
            memory[0x0031] = 0x00u
            memory[0x00B4] = 0x01u
            memory[0x01F6] = 0xDDu
            memory[0x01F7] = 0xBBu

            // Execute decompiled function
            miscLoopBack(0x15)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
        }
    }

    /**
     * Test case 7 from frame 4419
     * Function: miscLoopBack (0xBBD9)
     * Parameters: X
     * Call depth: 6
     * Memory reads: 12, writes: 3
     */
    @Test
    fun `miscLoopBack_frame4419_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (12 addresses)
            memory[0x0008] = 0x08u
            memory[0x002A] = 0x00u
            memory[0x002B] = 0x00u
            memory[0x002C] = 0x00u
            memory[0x002D] = 0x00u
            memory[0x002E] = 0x00u
            memory[0x002F] = 0x00u
            memory[0x0030] = 0x00u
            memory[0x0031] = 0x00u
            memory[0x00B4] = 0x02u
            memory[0x01F6] = 0xDDu
            memory[0x01F7] = 0xBBu

            // Execute decompiled function
            miscLoopBack(0x15)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
        }
    }

    /**
     * Test case 8 from frame 4425
     * Function: miscLoopBack (0xBBD9)
     * Parameters: X
     * Call depth: 6
     * Memory reads: 12, writes: 3
     */
    @Test
    fun `miscLoopBack_frame4425_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (12 addresses)
            memory[0x0008] = 0x08u
            memory[0x002A] = 0x00u
            memory[0x002B] = 0x00u
            memory[0x002C] = 0x00u
            memory[0x002D] = 0x00u
            memory[0x002E] = 0x00u
            memory[0x002F] = 0x00u
            memory[0x0030] = 0x00u
            memory[0x0031] = 0x00u
            memory[0x00B4] = 0x03u
            memory[0x01F6] = 0xDDu
            memory[0x01F7] = 0xBBu

            // Execute decompiled function
            miscLoopBack(0x15)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
        }
    }

    /**
     * Test case 9 from frame 4431
     * Function: miscLoopBack (0xBBD9)
     * Parameters: X
     * Call depth: 6
     * Memory reads: 12, writes: 3
     */
    @Test
    fun `miscLoopBack_frame4431_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (12 addresses)
            memory[0x0008] = 0x08u
            memory[0x002A] = 0x00u
            memory[0x002B] = 0x00u
            memory[0x002C] = 0x00u
            memory[0x002D] = 0x00u
            memory[0x002E] = 0x00u
            memory[0x002F] = 0x00u
            memory[0x0030] = 0x00u
            memory[0x0031] = 0x00u
            memory[0x00B4] = 0x04u
            memory[0x01F6] = 0xDDu
            memory[0x01F7] = 0xBBu

            // Execute decompiled function
            miscLoopBack(0x15)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
        }
    }

    // =========================================
    // 0xBC0C: addToScore
    // 1 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 4375
     * Function: addToScore (0xBC0C)
     * Call depth: 11
     * Memory reads: 7, writes: 5
     */
    @Test
    fun `addToScore_frame4375_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (7 addresses)
            memory[0x0008] = 0x00u
            memory[0x01EE] = 0x2Fu
            memory[0x01EF] = 0xBCu
            memory[0x0300] = 0x18u
            memory[0x0313] = 0x00u
            memory[0x0753] = 0x00u
            memory[0x075E] = 0x00u

            // Execute decompiled function
            addToScore()

            // Verify: Check output memory (2 addresses)
            assertEquals(0x24u, memory[0x0313], "Memory 0x0313 mismatch")
            assertEquals(0x01u, memory[0x075E], "Memory 0x075E mismatch")
        }
    }

    // =========================================
    // 0xBD80: bumpBlock
    // Parameters: X, Y
    // 3 calls, 3 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 4375
     * Function: bumpBlock (0xBD80)
     * Parameters: X, Y
     * Call depth: 9
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `bumpBlock_frame4375_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            bumpBlock(0x00, 0x18)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x01u, memory[0x03EE], "Memory 0x03EE mismatch")
        }
    }

    /**
     * Test case 1 from frame 4551
     * Function: bumpBlock (0xBD80)
     * Parameters: X, Y
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `bumpBlock_frame4551_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            bumpBlock(0x01, 0xFF)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x03EE], "Memory 0x03EE mismatch")
        }
    }

    /**
     * Test case 2 from frame 4621
     * Function: bumpBlock (0xBD80)
     * Parameters: X, Y
     * Call depth: 8
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `bumpBlock_frame4621_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            bumpBlock(0x00, 0xFF)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x01u, memory[0x03EE], "Memory 0x03EE mismatch")
        }
    }

    // =========================================
    // 0xBDB7: mushFlowerBlock
    // 1 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 4375
     * Function: mushFlowerBlock (0xBDB7)
     * Call depth: 9
     * Memory reads: 12, writes: 7
     */
    @Test
    fun `mushFlowerBlock_frame4375_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (12 addresses)
            memory[0x0003] = 0x00u
            memory[0x0004] = 0x11u
            memory[0x0005] = 0x07u
            memory[0x0006] = 0xD0u
            memory[0x0007] = 0x05u
            memory[0x006D] = 0x01u
            memory[0x0086] = 0x05u
            memory[0x00CE] = 0x8Cu
            memory[0x01F1] = 0x0Au
            memory[0x01F2] = 0xE4u
            memory[0x01F3] = 0x01u
            memory[0x0650] = 0x00u

            // Execute decompiled function
            mushFlowerBlock()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x80u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x00u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x05u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x07u, memory[0x0005], "Memory 0x0005 mismatch")
        }
    }

    // =========================================
    // 0xBDBD: extraLifeMushBlock
    // 1 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 4375
     * Function: extraLifeMushBlock (0xBDBD)
     * Call depth: 9
     * Memory reads: 0, writes: 2
     */
    @Test
    fun `extraLifeMushBlock_frame4375_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            extraLifeMushBlock()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0xBDE7: brickShatter
    // Parameters: X
    // 2 calls, 2 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 4551
     * Function: brickShatter (0xBDE7)
     * Parameters: X
     * Call depth: 9
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `brickShatter_frame4551_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            brickShatter(0x01)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 4621
     * Function: brickShatter (0xBDE7)
     * Parameters: X
     * Call depth: 9
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `brickShatter_frame4621_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            brickShatter(0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0xBE26: spawnBrickChunks
    // Parameters: X
    // 3 calls, 3 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 4375
     * Function: spawnBrickChunks (0xBE26)
     * Parameters: X
     * Call depth: 10
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `spawnBrickChunks_frame4375_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x0006] = 0xD0u
            memory[0x0007] = 0x05u
            memory[0x0630] = 0x00u

            // Execute decompiled function
            spawnBrickChunks(0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x60u, memory[0x0002], "Memory 0x0002 mismatch")
        }
    }

    /**
     * Test case 1 from frame 4551
     * Function: spawnBrickChunks (0xBE26)
     * Parameters: X
     * Call depth: 10
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `spawnBrickChunks_frame4551_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x0006] = 0xD6u
            memory[0x0007] = 0x05u
            memory[0x0636] = 0x00u

            // Execute decompiled function
            spawnBrickChunks(0x01)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x60u, memory[0x0002], "Memory 0x0002 mismatch")
        }
    }

    /**
     * Test case 2 from frame 4621
     * Function: spawnBrickChunks (0xBE26)
     * Parameters: X
     * Call depth: 10
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `spawnBrickChunks_frame4621_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x0006] = 0xD8u
            memory[0x0007] = 0x05u
            memory[0x0638] = 0x00u

            // Execute decompiled function
            spawnBrickChunks(0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x60u, memory[0x0002], "Memory 0x0002 mismatch")
        }
    }

    // =========================================
    // 0xBEE7: moveEnemyHorizontally
    // Parameters: X
    // 3 calls, 3 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 4403
     * Function: moveEnemyHorizontally (0xBEE7)
     * Parameters: X
     * Call depth: 6
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `moveEnemyHorizontally_frame4403_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x03E4] = 0x70u

            // Execute decompiled function
            moveEnemyHorizontally(0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x05u, memory[0x0007], "Memory 0x0007 mismatch")
        }
    }

    /**
     * Test case 1 from frame 4579
     * Function: moveEnemyHorizontally (0xBEE7)
     * Parameters: X
     * Call depth: 6
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `moveEnemyHorizontally_frame4579_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x03E5] = 0x70u

            // Execute decompiled function
            moveEnemyHorizontally(0x01)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x05u, memory[0x0007], "Memory 0x0007 mismatch")
        }
    }

    /**
     * Test case 2 from frame 4649
     * Function: moveEnemyHorizontally (0xBEE7)
     * Parameters: X
     * Call depth: 6
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `moveEnemyHorizontally_frame4649_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x03E4] = 0x70u

            // Execute decompiled function
            moveEnemyHorizontally(0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x05u, memory[0x0007], "Memory 0x0007 mismatch")
        }
    }

    // =========================================
    // 0xBEEE: movePlayerHorizontally
    // 3 calls, 3 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 4403
     * Function: movePlayerHorizontally (0xBEEE)
     * Call depth: 6
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `movePlayerHorizontally_frame4403_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x03E8] = 0xC4u

            // Execute decompiled function
            movePlayerHorizontally()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x70u, memory[0x0002], "Memory 0x0002 mismatch")
        }
    }

    /**
     * Test case 1 from frame 4579
     * Function: movePlayerHorizontally (0xBEEE)
     * Call depth: 6
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `movePlayerHorizontally_frame4579_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x03E9] = 0x51u

            // Execute decompiled function
            movePlayerHorizontally()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x70u, memory[0x0002], "Memory 0x0002 mismatch")
        }
    }

    /**
     * Test case 2 from frame 4649
     * Function: movePlayerHorizontally (0xBEEE)
     * Call depth: 6
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `movePlayerHorizontally_frame4649_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x03E8] = 0x51u

            // Execute decompiled function
            movePlayerHorizontally()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x70u, memory[0x0002], "Memory 0x0002 mismatch")
        }
    }

    // =========================================
    // 0xBEF4: moveObjectHorizontally
    // Parameters: X
    // 3 calls, 3 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 4403
     * Function: moveObjectHorizontally (0xBEF4)
     * Parameters: X
     * Call depth: 6
     * Memory reads: 2, writes: 3
     */
    @Test
    fun `moveObjectHorizontally_frame4403_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0006] = 0xD0u
            memory[0x0007] = 0x05u

            // Execute decompiled function
            moveObjectHorizontally(0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0xC4u, memory[0x0640], "Memory 0x0640 mismatch")
        }
    }

    /**
     * Test case 1 from frame 4579
     * Function: moveObjectHorizontally (0xBEF4)
     * Parameters: X
     * Call depth: 6
     * Memory reads: 2, writes: 3
     */
    @Test
    fun `moveObjectHorizontally_frame4579_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0006] = 0xD6u
            memory[0x0007] = 0x05u

            // Execute decompiled function
            moveObjectHorizontally(0x01)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x51u, memory[0x0646], "Memory 0x0646 mismatch")
        }
    }

    /**
     * Test case 2 from frame 4649
     * Function: moveObjectHorizontally (0xBEF4)
     * Parameters: X
     * Call depth: 6
     * Memory reads: 2, writes: 3
     */
    @Test
    fun `moveObjectHorizontally_frame4649_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0006] = 0xD8u
            memory[0x0007] = 0x05u

            // Execute decompiled function
            moveObjectHorizontally(0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x51u, memory[0x0648], "Memory 0x0648 mismatch")
        }
    }

    // =========================================
    // 0xBF32: movePlayerVertically
    // 9322 calls, 2648 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 200
     * Function: movePlayerVertically (0xBF32)
     * Call depth: 8
     * Memory reads: 5, writes: 4
     */
    @Test
    fun `movePlayerVertically_frame200_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0x00u
            memory[0x0001] = 0x10u
            memory[0x0002] = 0x00u
            memory[0x006D] = 0x00u
            memory[0x0086] = 0x28u

            // Execute decompiled function
            movePlayerVertically()

            // Verify: Check output memory (3 addresses)
            assertEquals(0x00u, memory[0x006D], "Memory 0x006D mismatch")
            assertEquals(0x28u, memory[0x0086], "Memory 0x0086 mismatch")
            assertEquals(0x10u, memory[0x0400], "Memory 0x0400 mismatch")
        }
    }

    /**
     * Test case 1 from frame 1221
     * Function: movePlayerVertically (0xBF32)
     * Call depth: 11
     * Memory reads: 5, writes: 4
     */
    @Test
    fun `movePlayerVertically_frame1221_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0xFFu
            memory[0x0001] = 0x80u
            memory[0x0002] = 0xFFu
            memory[0x006E] = 0x01u
            memory[0x0087] = 0x50u

            // Execute decompiled function
            movePlayerVertically()

            // Verify: Check output memory (3 addresses)
            assertEquals(0x01u, memory[0x006E], "Memory 0x006E mismatch")
            assertEquals(0x50u, memory[0x0087], "Memory 0x0087 mismatch")
            assertEquals(0x00u, memory[0x0401], "Memory 0x0401 mismatch")
        }
    }

    /**
     * Test case 2 from frame 2572
     * Function: movePlayerVertically (0xBF32)
     * Call depth: 8
     * Memory reads: 5, writes: 4
     */
    @Test
    fun `movePlayerVertically_frame2572_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0x00u
            memory[0x0001] = 0x30u
            memory[0x0002] = 0x00u
            memory[0x006D] = 0x00u
            memory[0x0086] = 0x2Cu

            // Execute decompiled function
            movePlayerVertically()

            // Verify: Check output memory (3 addresses)
            assertEquals(0x00u, memory[0x006D], "Memory 0x006D mismatch")
            assertEquals(0x2Du, memory[0x0086], "Memory 0x0086 mismatch")
            assertEquals(0x00u, memory[0x0400], "Memory 0x0400 mismatch")
        }
    }

    /**
     * Test case 3 from frame 3698
     * Function: movePlayerVertically (0xBF32)
     * Call depth: 8
     * Memory reads: 7, writes: 5
     */
    @Test
    fun `movePlayerVertically_frame3698_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (7 addresses)
            memory[0x0000] = 0x01u
            memory[0x0001] = 0xE0u
            memory[0x0002] = 0x00u
            memory[0x006D] = 0x00u
            memory[0x0086] = 0x90u
            memory[0x071A] = 0x00u
            memory[0x071C] = 0x2Fu

            // Execute decompiled function
            movePlayerVertically()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x00u, memory[0x006D], "Memory 0x006D mismatch")
            assertEquals(0x92u, memory[0x0086], "Memory 0x0086 mismatch")
            assertEquals(0x70u, memory[0x0400], "Memory 0x0400 mismatch")
            assertEquals(0x2Eu, memory[0x071D], "Memory 0x071D mismatch")
        }
    }

    /**
     * Test case 4 from frame 4312
     * Function: movePlayerVertically (0xBF32)
     * Call depth: 8
     * Memory reads: 7, writes: 5
     */
    @Test
    fun `movePlayerVertically_frame4312_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (7 addresses)
            memory[0x0000] = 0x00u
            memory[0x0001] = 0xC0u
            memory[0x0002] = 0x00u
            memory[0x006D] = 0x00u
            memory[0x0086] = 0xD6u
            memory[0x071A] = 0x00u
            memory[0x071C] = 0x6Bu

            // Execute decompiled function
            movePlayerVertically()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x00u, memory[0x006D], "Memory 0x006D mismatch")
            assertEquals(0xD7u, memory[0x0086], "Memory 0x0086 mismatch")
            assertEquals(0x30u, memory[0x0400], "Memory 0x0400 mismatch")
            assertEquals(0x6Au, memory[0x071D], "Memory 0x071D mismatch")
        }
    }

    /**
     * Test case 5 from frame 4978
     * Function: movePlayerVertically (0xBF32)
     * Call depth: 8
     * Memory reads: 5, writes: 4
     */
    @Test
    fun `movePlayerVertically_frame4978_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0x00u
            memory[0x0001] = 0xA0u
            memory[0x0002] = 0x00u
            memory[0x006D] = 0x01u
            memory[0x0086] = 0xBFu

            // Execute decompiled function
            movePlayerVertically()

            // Verify: Check output memory (3 addresses)
            assertEquals(0x01u, memory[0x006D], "Memory 0x006D mismatch")
            assertEquals(0xBFu, memory[0x0086], "Memory 0x0086 mismatch")
            assertEquals(0xF0u, memory[0x0400], "Memory 0x0400 mismatch")
        }
    }

    /**
     * Test case 6 from frame 6714
     * Function: movePlayerVertically (0xBF32)
     * Call depth: 8
     * Memory reads: 5, writes: 4
     */
    @Test
    fun `movePlayerVertically_frame6714_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0x00u
            memory[0x0001] = 0x40u
            memory[0x0002] = 0x00u
            memory[0x006D] = 0x02u
            memory[0x0086] = 0x02u

            // Execute decompiled function
            movePlayerVertically()

            // Verify: Check output memory (3 addresses)
            assertEquals(0x02u, memory[0x006D], "Memory 0x006D mismatch")
            assertEquals(0x02u, memory[0x0086], "Memory 0x0086 mismatch")
            assertEquals(0xF0u, memory[0x0400], "Memory 0x0400 mismatch")
        }
    }

    /**
     * Test case 7 from frame 8164
     * Function: movePlayerVertically (0xBF32)
     * Call depth: 8
     * Memory reads: 5, writes: 4
     */
    @Test
    fun `movePlayerVertically_frame8164_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0xFFu
            memory[0x0001] = 0xF0u
            memory[0x0002] = 0xFFu
            memory[0x006D] = 0x02u
            memory[0x0086] = 0x52u

            // Execute decompiled function
            movePlayerVertically()

            // Verify: Check output memory (3 addresses)
            assertEquals(0x02u, memory[0x006D], "Memory 0x006D mismatch")
            assertEquals(0x52u, memory[0x0086], "Memory 0x0086 mismatch")
            assertEquals(0x00u, memory[0x0400], "Memory 0x0400 mismatch")
        }
    }

    /**
     * Test case 8 from frame 11378
     * Function: movePlayerVertically (0xBF32)
     * Call depth: 8
     * Memory reads: 5, writes: 4
     */
    @Test
    fun `movePlayerVertically_frame11378_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0xFFu
            memory[0x0001] = 0xE0u
            memory[0x0002] = 0xFFu
            memory[0x006D] = 0x02u
            memory[0x0086] = 0x4Eu

            // Execute decompiled function
            movePlayerVertically()

            // Verify: Check output memory (3 addresses)
            assertEquals(0x02u, memory[0x006D], "Memory 0x006D mismatch")
            assertEquals(0x4Du, memory[0x0086], "Memory 0x0086 mismatch")
            assertEquals(0xE0u, memory[0x0400], "Memory 0x0400 mismatch")
        }
    }

    /**
     * Test case 9 from frame 16564
     * Function: movePlayerVertically (0xBF32)
     * Call depth: 8
     * Memory reads: 5, writes: 4
     */
    @Test
    fun `movePlayerVertically_frame16564_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0xFEu
            memory[0x0001] = 0xB0u
            memory[0x0002] = 0xFFu
            memory[0x006D] = 0x02u
            memory[0x0086] = 0x32u

            // Execute decompiled function
            movePlayerVertically()

            // Verify: Check output memory (3 addresses)
            assertEquals(0x02u, memory[0x006D], "Memory 0x006D mismatch")
            assertEquals(0x31u, memory[0x0086], "Memory 0x0086 mismatch")
            assertEquals(0xA0u, memory[0x0400], "Memory 0x0400 mismatch")
        }
    }

    // =========================================
    // 0xBF48: movedEnemyvertically
    // Parameters: X
    // 9322 calls, 31 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 200
     * Function: movedEnemyvertically (0xBF48)
     * Parameters: X
     * Call depth: 8
     * Memory reads: 2, writes: 0
     */
    @Test
    fun `movedEnemyvertically_frame200_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0000] = 0x00u
            memory[0x01F3] = 0x00u

            // Execute decompiled function
            movedEnemyvertically(0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 658
     * Function: movedEnemyvertically (0xBF48)
     * Parameters: X
     * Call depth: 8
     * Memory reads: 2, writes: 0
     */
    @Test
    fun `movedEnemyvertically_frame658_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0000] = 0xFFu
            memory[0x01F3] = 0x01u

            // Execute decompiled function
            movedEnemyvertically(0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 2 from frame 1159
     * Function: movedEnemyvertically (0xBF48)
     * Parameters: X
     * Call depth: 11
     * Memory reads: 2, writes: 0
     */
    @Test
    fun `movedEnemyvertically_frame1159_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0000] = 0xFFu
            memory[0x01F0] = 0x00u

            // Execute decompiled function
            movedEnemyvertically(0x01)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 3 from frame 3676
     * Function: movedEnemyvertically (0xBF48)
     * Parameters: X
     * Call depth: 8
     * Memory reads: 2, writes: 0
     */
    @Test
    fun `movedEnemyvertically_frame3676_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0000] = 0x02u
            memory[0x01F3] = 0x00u

            // Execute decompiled function
            movedEnemyvertically(0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 4 from frame 4390
     * Function: movedEnemyvertically (0xBF48)
     * Parameters: X
     * Call depth: 8
     * Memory reads: 2, writes: 0
     */
    @Test
    fun `movedEnemyvertically_frame4390_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0000] = 0x02u
            memory[0x01F3] = 0x00u

            // Execute decompiled function
            movedEnemyvertically(0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 5 from frame 4446
     * Function: movedEnemyvertically (0xBF48)
     * Parameters: X
     * Call depth: 8
     * Memory reads: 2, writes: 0
     */
    @Test
    fun `movedEnemyvertically_frame4446_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0000] = 0x00u
            memory[0x01F3] = 0x00u

            // Execute decompiled function
            movedEnemyvertically(0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 6 from frame 4987
     * Function: movedEnemyvertically (0xBF48)
     * Parameters: X
     * Call depth: 11
     * Memory reads: 2, writes: 0
     */
    @Test
    fun `movedEnemyvertically_frame4987_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0000] = 0x00u
            memory[0x01F0] = 0x01u

            // Execute decompiled function
            movedEnemyvertically(0x01)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 7 from frame 6698
     * Function: movedEnemyvertically (0xBF48)
     * Parameters: X
     * Call depth: 8
     * Memory reads: 2, writes: 0
     */
    @Test
    fun `movedEnemyvertically_frame6698_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0000] = 0x00u
            memory[0x01F3] = 0x01u

            // Execute decompiled function
            movedEnemyvertically(0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 8 from frame 6872
     * Function: movedEnemyvertically (0xBF48)
     * Parameters: X
     * Call depth: 8
     * Memory reads: 2, writes: 0
     */
    @Test
    fun `movedEnemyvertically_frame6872_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0000] = 0x01u
            memory[0x01F3] = 0x01u

            // Execute decompiled function
            movedEnemyvertically(0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 9 from frame 6938
     * Function: movedEnemyvertically (0xBF48)
     * Parameters: X
     * Call depth: 8
     * Memory reads: 2, writes: 0
     */
    @Test
    fun `movedEnemyvertically_frame6938_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0000] = 0xFFu
            memory[0x01F3] = 0x01u

            // Execute decompiled function
            movedEnemyvertically(0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0xBF5C: moveRedPTroopa
    // Parameters: X, Y
    // 3962 calls, 1058 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 222
     * Function: moveRedPTroopa (0xBF5C)
     * Parameters: X, Y
     * Call depth: 7
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `moveRedPTroopa_frame222_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0000] = 0x20u
            memory[0x0002] = 0x04u
            memory[0x0007] = 0xFFu
            memory[0x009F] = 0xFCu
            memory[0x00B5] = 0x01u
            memory[0x00CE] = 0xB0u
            memory[0x01F5] = 0x00u
            memory[0x0416] = 0x00u
            memory[0x0433] = 0x00u

            // Execute decompiled function
            moveRedPTroopa(0x00, 0x00)

            // Verify: Check output memory (8 addresses)
            assertEquals(0x20u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x04u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0xFFu, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0xFCu, memory[0x009F], "Memory 0x009F mismatch")
            assertEquals(0x01u, memory[0x00B5], "Memory 0x00B5 mismatch")
            assertEquals(0xACu, memory[0x00CE], "Memory 0x00CE mismatch")
            assertEquals(0x00u, memory[0x0416], "Memory 0x0416 mismatch")
            assertEquals(0x20u, memory[0x0433], "Memory 0x0433 mismatch")
        }
    }

    /**
     * Test case 1 from frame 702
     * Function: moveRedPTroopa (0xBF5C)
     * Parameters: X, Y
     * Call depth: 7
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `moveRedPTroopa_frame702_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0000] = 0x70u
            memory[0x0002] = 0x04u
            memory[0x0007] = 0xFFu
            memory[0x009F] = 0xFCu
            memory[0x00B5] = 0x01u
            memory[0x00CE] = 0x9Du
            memory[0x01F5] = 0x00u
            memory[0x0416] = 0x40u
            memory[0x0433] = 0xA0u

            // Execute decompiled function
            moveRedPTroopa(0x00, 0x00)

            // Verify: Check output memory (8 addresses)
            assertEquals(0x70u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x04u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0xFFu, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0xFDu, memory[0x009F], "Memory 0x009F mismatch")
            assertEquals(0x01u, memory[0x00B5], "Memory 0x00B5 mismatch")
            assertEquals(0x99u, memory[0x00CE], "Memory 0x00CE mismatch")
            assertEquals(0xE0u, memory[0x0416], "Memory 0x0416 mismatch")
            assertEquals(0x10u, memory[0x0433], "Memory 0x0433 mismatch")
        }
    }

    /**
     * Test case 2 from frame 1644
     * Function: moveRedPTroopa (0xBF5C)
     * Parameters: X, Y
     * Call depth: 7
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `moveRedPTroopa_frame1644_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0000] = 0x28u
            memory[0x0002] = 0x04u
            memory[0x0007] = 0x00u
            memory[0x009F] = 0x01u
            memory[0x00B5] = 0x01u
            memory[0x00CE] = 0x7Fu
            memory[0x01F5] = 0x00u
            memory[0x0416] = 0xBEu
            memory[0x0433] = 0x50u

            // Execute decompiled function
            moveRedPTroopa(0x00, 0x00)

            // Verify: Check output memory (8 addresses)
            assertEquals(0x28u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x04u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x01u, memory[0x009F], "Memory 0x009F mismatch")
            assertEquals(0x01u, memory[0x00B5], "Memory 0x00B5 mismatch")
            assertEquals(0x81u, memory[0x00CE], "Memory 0x00CE mismatch")
            assertEquals(0x0Eu, memory[0x0416], "Memory 0x0416 mismatch")
            assertEquals(0x78u, memory[0x0433], "Memory 0x0433 mismatch")
        }
    }

    /**
     * Test case 3 from frame 1856
     * Function: moveRedPTroopa (0xBF5C)
     * Parameters: X, Y
     * Call depth: 7
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `moveRedPTroopa_frame1856_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0000] = 0x28u
            memory[0x0002] = 0x04u
            memory[0x0007] = 0x00u
            memory[0x009F] = 0x04u
            memory[0x00B5] = 0x03u
            memory[0x00CE] = 0x23u
            memory[0x01F5] = 0x00u
            memory[0x0416] = 0xCEu
            memory[0x0433] = 0x28u

            // Execute decompiled function
            moveRedPTroopa(0x00, 0x00)

            // Verify: Check output memory (8 addresses)
            assertEquals(0x28u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x04u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x04u, memory[0x009F], "Memory 0x009F mismatch")
            assertEquals(0x03u, memory[0x00B5], "Memory 0x00B5 mismatch")
            assertEquals(0x27u, memory[0x00CE], "Memory 0x00CE mismatch")
            assertEquals(0xF6u, memory[0x0416], "Memory 0x0416 mismatch")
            assertEquals(0x50u, memory[0x0433], "Memory 0x0433 mismatch")
        }
    }

    /**
     * Test case 4 from frame 2944
     * Function: moveRedPTroopa (0xBF5C)
     * Parameters: X, Y
     * Call depth: 7
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `moveRedPTroopa_frame2944_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0000] = 0x70u
            memory[0x0002] = 0x04u
            memory[0x0007] = 0x00u
            memory[0x009F] = 0x00u
            memory[0x00B5] = 0x01u
            memory[0x00CE] = 0x96u
            memory[0x01F5] = 0x00u
            memory[0x0416] = 0x20u
            memory[0x0433] = 0x30u

            // Execute decompiled function
            moveRedPTroopa(0x00, 0x00)

            // Verify: Check output memory (8 addresses)
            assertEquals(0x70u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x04u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x00u, memory[0x009F], "Memory 0x009F mismatch")
            assertEquals(0x01u, memory[0x00B5], "Memory 0x00B5 mismatch")
            assertEquals(0x96u, memory[0x00CE], "Memory 0x00CE mismatch")
            assertEquals(0x50u, memory[0x0416], "Memory 0x0416 mismatch")
            assertEquals(0xA0u, memory[0x0433], "Memory 0x0433 mismatch")
        }
    }

    /**
     * Test case 5 from frame 4122
     * Function: moveRedPTroopa (0xBF5C)
     * Parameters: X, Y
     * Call depth: 7
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `moveRedPTroopa_frame4122_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0000] = 0x70u
            memory[0x0002] = 0x04u
            memory[0x0007] = 0x00u
            memory[0x009F] = 0x01u
            memory[0x00B5] = 0x01u
            memory[0x00CE] = 0x8Eu
            memory[0x01F5] = 0x00u
            memory[0x0416] = 0x30u
            memory[0x0433] = 0x90u

            // Execute decompiled function
            moveRedPTroopa(0x00, 0x00)

            // Verify: Check output memory (8 addresses)
            assertEquals(0x70u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x04u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x02u, memory[0x009F], "Memory 0x009F mismatch")
            assertEquals(0x01u, memory[0x00B5], "Memory 0x00B5 mismatch")
            assertEquals(0x8Fu, memory[0x00CE], "Memory 0x00CE mismatch")
            assertEquals(0xC0u, memory[0x0416], "Memory 0x0416 mismatch")
            assertEquals(0x00u, memory[0x0433], "Memory 0x0433 mismatch")
        }
    }

    /**
     * Test case 6 from frame 4838
     * Function: moveRedPTroopa (0xBF5C)
     * Parameters: X, Y
     * Call depth: 7
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `moveRedPTroopa_frame4838_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0000] = 0x70u
            memory[0x0002] = 0x04u
            memory[0x0007] = 0xFFu
            memory[0x009F] = 0xFFu
            memory[0x00B5] = 0x01u
            memory[0x00CE] = 0x7Eu
            memory[0x01F5] = 0x00u
            memory[0x0416] = 0xE0u
            memory[0x0433] = 0x60u

            // Execute decompiled function
            moveRedPTroopa(0x00, 0x00)

            // Verify: Check output memory (8 addresses)
            assertEquals(0x70u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x04u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0xFFu, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0xFFu, memory[0x009F], "Memory 0x009F mismatch")
            assertEquals(0x01u, memory[0x00B5], "Memory 0x00B5 mismatch")
            assertEquals(0x7Eu, memory[0x00CE], "Memory 0x00CE mismatch")
            assertEquals(0x40u, memory[0x0416], "Memory 0x0416 mismatch")
            assertEquals(0xD0u, memory[0x0433], "Memory 0x0433 mismatch")
        }
    }

    /**
     * Test case 7 from frame 5266
     * Function: moveRedPTroopa (0xBF5C)
     * Parameters: X, Y
     * Call depth: 7
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `moveRedPTroopa_frame5266_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0000] = 0x70u
            memory[0x0002] = 0x04u
            memory[0x0007] = 0x00u
            memory[0x009F] = 0x03u
            memory[0x00B5] = 0x01u
            memory[0x00CE] = 0x71u
            memory[0x01F5] = 0x00u
            memory[0x0416] = 0x50u
            memory[0x0433] = 0x20u

            // Execute decompiled function
            moveRedPTroopa(0x00, 0x00)

            // Verify: Check output memory (8 addresses)
            assertEquals(0x70u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x04u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x03u, memory[0x009F], "Memory 0x009F mismatch")
            assertEquals(0x01u, memory[0x00B5], "Memory 0x00B5 mismatch")
            assertEquals(0x74u, memory[0x00CE], "Memory 0x00CE mismatch")
            assertEquals(0x70u, memory[0x0416], "Memory 0x0416 mismatch")
            assertEquals(0x90u, memory[0x0433], "Memory 0x0433 mismatch")
        }
    }

    /**
     * Test case 8 from frame 8414
     * Function: moveRedPTroopa (0xBF5C)
     * Parameters: X, Y
     * Call depth: 7
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `moveRedPTroopa_frame8414_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0000] = 0x20u
            memory[0x0002] = 0x04u
            memory[0x0007] = 0xFFu
            memory[0x009F] = 0xFCu
            memory[0x00B5] = 0x01u
            memory[0x00CE] = 0x96u
            memory[0x01F5] = 0x00u
            memory[0x0416] = 0xA0u
            memory[0x0433] = 0xE0u

            // Execute decompiled function
            moveRedPTroopa(0x00, 0xFF)

            // Verify: Check output memory (8 addresses)
            assertEquals(0x20u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x04u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0xFFu, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0xFDu, memory[0x009F], "Memory 0x009F mismatch")
            assertEquals(0x01u, memory[0x00B5], "Memory 0x00B5 mismatch")
            assertEquals(0x93u, memory[0x00CE], "Memory 0x00CE mismatch")
            assertEquals(0x80u, memory[0x0416], "Memory 0x0416 mismatch")
            assertEquals(0x00u, memory[0x0433], "Memory 0x0433 mismatch")
        }
    }

    /**
     * Test case 9 from frame 12296
     * Function: moveRedPTroopa (0xBF5C)
     * Parameters: X, Y
     * Call depth: 7
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `moveRedPTroopa_frame12296_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0000] = 0x70u
            memory[0x0002] = 0x04u
            memory[0x0007] = 0x00u
            memory[0x009F] = 0x03u
            memory[0x00B5] = 0x01u
            memory[0x00CE] = 0x87u
            memory[0x01F5] = 0x00u
            memory[0x0416] = 0xA0u
            memory[0x0433] = 0x90u

            // Execute decompiled function
            moveRedPTroopa(0x00, 0xFF)

            // Verify: Check output memory (8 addresses)
            assertEquals(0x70u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x04u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x04u, memory[0x009F], "Memory 0x009F mismatch")
            assertEquals(0x01u, memory[0x00B5], "Memory 0x00B5 mismatch")
            assertEquals(0x8Bu, memory[0x00CE], "Memory 0x00CE mismatch")
            assertEquals(0x30u, memory[0x0416], "Memory 0x0416 mismatch")
            assertEquals(0x00u, memory[0x0433], "Memory 0x0433 mismatch")
        }
    }

    // =========================================
    // 0xC20A: initEnemyObject
    // Parameters: X
    // 3 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 1157
     * Function: initEnemyObject (0xC20A)
     * Parameters: X
     * Call depth: 6
     * Memory reads: 5, writes: 7
     */
    @Test
    fun `initEnemyObject_frame1157_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x000A] = 0x00u
            memory[0x00B5] = 0x01u
            memory[0x01F8] = 0x07u
            memory[0x01F9] = 0xAFu
            memory[0x079F] = 0x00u

            // Execute decompiled function
            initEnemyObject(0x00)

            // Verify: Check output memory (3 addresses)
            assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
            assertEquals(0x00u, memory[0x000D], "Memory 0x000D mismatch")
            assertEquals(0x01u, memory[0x000F], "Memory 0x000F mismatch")
        }
    }

    // =========================================
    // 0xC23F: inc3B
    // 3 calls, 2 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 32
     * Function: inc3B (0xC23F)
     * Call depth: 6
     * Memory reads: 17, writes: 5
     */
    @Test
    fun `inc3B_frame32_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (17 addresses)
            memory[0x0006] = 0x01u
            memory[0x0007] = 0x20u
            memory[0x0010] = 0x00u
            memory[0x006F] = 0x01u
            memory[0x0088] = 0x60u
            memory[0x00E9] = 0x01u
            memory[0x00EA] = 0x9Fu
            memory[0x01F5] = 0x00u
            memory[0x0398] = 0x00u
            memory[0x06CB] = 0x00u
            memory[0x06CD] = 0x00u
            memory[0x071B] = 0x00u
            memory[0x071D] = 0xFFu
            memory[0x071F] = 0x00u
            memory[0x0739] = 0x03u
            memory[0x073A] = 0x01u
            memory[0x0745] = 0x00u

            // Execute decompiled function
            inc3B()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x01u, memory[0x0006], "Memory 0x0006 mismatch")
            assertEquals(0x20u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x01u, memory[0x006F], "Memory 0x006F mismatch")
            assertEquals(0x60u, memory[0x0088], "Memory 0x0088 mismatch")
        }
    }

    /**
     * Test case 1 from frame 196
     * Function: inc3B (0xC23F)
     * Call depth: 5
     * Memory reads: 17, writes: 5
     */
    @Test
    fun `inc3B_frame196_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (17 addresses)
            memory[0x0006] = 0x01u
            memory[0x0007] = 0x20u
            memory[0x0010] = 0x00u
            memory[0x006F] = 0x01u
            memory[0x0088] = 0x60u
            memory[0x00E9] = 0x01u
            memory[0x00EA] = 0x9Fu
            memory[0x01F7] = 0x00u
            memory[0x0398] = 0x00u
            memory[0x06CB] = 0x00u
            memory[0x06CD] = 0x00u
            memory[0x071B] = 0x00u
            memory[0x071D] = 0xFFu
            memory[0x071F] = 0x00u
            memory[0x0739] = 0x03u
            memory[0x073A] = 0x01u
            memory[0x0745] = 0x00u

            // Execute decompiled function
            inc3B()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x01u, memory[0x0006], "Memory 0x0006 mismatch")
            assertEquals(0x20u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x01u, memory[0x006F], "Memory 0x006F mismatch")
            assertEquals(0x60u, memory[0x0088], "Memory 0x0088 mismatch")
        }
    }

    // =========================================
    // 0xC242: inc2B
    // 3 calls, 2 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 32
     * Function: inc2B (0xC242)
     * Call depth: 6
     * Memory reads: 4, writes: 4
     */
    @Test
    fun `inc2B_frame32_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x0008] = 0x00u
            memory[0x00E9] = 0x01u
            memory[0x00EA] = 0x9Fu
            memory[0x0739] = 0x00u

            // Execute decompiled function
            inc2B()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x03u, memory[0x0739], "Memory 0x0739 mismatch")
            assertEquals(0x00u, memory[0x073B], "Memory 0x073B mismatch")
            assertEquals(0xC2u, memory[0x0750], "Memory 0x0750 mismatch")
            assertEquals(0x00u, memory[0x0751], "Memory 0x0751 mismatch")
        }
    }

    /**
     * Test case 1 from frame 196
     * Function: inc2B (0xC242)
     * Call depth: 5
     * Memory reads: 4, writes: 4
     */
    @Test
    fun `inc2B_frame196_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x0008] = 0x00u
            memory[0x00E9] = 0x01u
            memory[0x00EA] = 0x9Fu
            memory[0x0739] = 0x00u

            // Execute decompiled function
            inc2B()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x03u, memory[0x0739], "Memory 0x0739 mismatch")
            assertEquals(0x00u, memory[0x073B], "Memory 0x073B mismatch")
            assertEquals(0xC2u, memory[0x0750], "Memory 0x0750 mismatch")
            assertEquals(0x00u, memory[0x0751], "Memory 0x0751 mismatch")
        }
    }

    // =========================================
    // 0xC902: runBowserFlame
    // 838 calls, 772 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 1159
     * Function: runBowserFlame (0xC902)
     * Call depth: 8
     * Memory reads: 11, writes: 4
     */
    @Test
    fun `runBowserFlame_frame1159_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (11 addresses)
            memory[0x0000] = 0xFFu
            memory[0x0001] = 0xE9u
            memory[0x0002] = 0x01u
            memory[0x0003] = 0x79u
            memory[0x0016] = 0x06u
            memory[0x006E] = 0x01u
            memory[0x0087] = 0x5Fu
            memory[0x071A] = 0x00u
            memory[0x071B] = 0x01u
            memory[0x071C] = 0x32u
            memory[0x071D] = 0x31u

            // Execute decompiled function
            runBowserFlame()

            // Verify: Check output memory (4 addresses)
            assertEquals(0xFFu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xE9u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x01u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x79u, memory[0x0003], "Memory 0x0003 mismatch")
        }
    }

    /**
     * Test case 1 from frame 1313
     * Function: runBowserFlame (0xC902)
     * Call depth: 8
     * Memory reads: 11, writes: 4
     */
    @Test
    fun `runBowserFlame_frame1313_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (11 addresses)
            memory[0x0000] = 0x00u
            memory[0x0001] = 0x37u
            memory[0x0002] = 0x01u
            memory[0x0003] = 0xC8u
            memory[0x0016] = 0x06u
            memory[0x006E] = 0x01u
            memory[0x0087] = 0x39u
            memory[0x071A] = 0x00u
            memory[0x071B] = 0x01u
            memory[0x071C] = 0x80u
            memory[0x071D] = 0x7Fu

            // Execute decompiled function
            runBowserFlame()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x37u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x01u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0xC8u, memory[0x0003], "Memory 0x0003 mismatch")
        }
    }

    /**
     * Test case 2 from frame 1467
     * Function: runBowserFlame (0xC902)
     * Call depth: 8
     * Memory reads: 11, writes: 4
     */
    @Test
    fun `runBowserFlame_frame1467_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (11 addresses)
            memory[0x0000] = 0x00u
            memory[0x0001] = 0x3Cu
            memory[0x0002] = 0x01u
            memory[0x0003] = 0xCDu
            memory[0x0016] = 0x06u
            memory[0x006E] = 0x01u
            memory[0x0087] = 0x12u
            memory[0x071A] = 0x00u
            memory[0x071B] = 0x01u
            memory[0x071C] = 0x85u
            memory[0x071D] = 0x84u

            // Execute decompiled function
            runBowserFlame()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x3Cu, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x01u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0xCDu, memory[0x0003], "Memory 0x0003 mismatch")
        }
    }

    /**
     * Test case 3 from frame 1621
     * Function: runBowserFlame (0xC902)
     * Call depth: 7
     * Memory reads: 11, writes: 4
     */
    @Test
    fun `runBowserFlame_frame1621_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (11 addresses)
            memory[0x0000] = 0x00u
            memory[0x0001] = 0x3Cu
            memory[0x0002] = 0x01u
            memory[0x0003] = 0xCDu
            memory[0x0016] = 0x06u
            memory[0x006E] = 0x00u
            memory[0x0087] = 0xFFu
            memory[0x071A] = 0x00u
            memory[0x071B] = 0x01u
            memory[0x071C] = 0x85u
            memory[0x071D] = 0x84u

            // Execute decompiled function
            runBowserFlame()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x3Cu, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x01u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0xCDu, memory[0x0003], "Memory 0x0003 mismatch")
        }
    }

    /**
     * Test case 4 from frame 1775
     * Function: runBowserFlame (0xC902)
     * Call depth: 7
     * Memory reads: 11, writes: 4
     */
    @Test
    fun `runBowserFlame_frame1775_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (11 addresses)
            memory[0x0000] = 0x00u
            memory[0x0001] = 0x3Cu
            memory[0x0002] = 0x01u
            memory[0x0003] = 0xCDu
            memory[0x0016] = 0x06u
            memory[0x006E] = 0x00u
            memory[0x0087] = 0xFFu
            memory[0x071A] = 0x00u
            memory[0x071B] = 0x01u
            memory[0x071C] = 0x85u
            memory[0x071D] = 0x84u

            // Execute decompiled function
            runBowserFlame()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x3Cu, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x01u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0xCDu, memory[0x0003], "Memory 0x0003 mismatch")
        }
    }

    /**
     * Test case 5 from frame 1931
     * Function: runBowserFlame (0xC902)
     * Call depth: 7
     * Memory reads: 11, writes: 4
     */
    @Test
    fun `runBowserFlame_frame1931_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (11 addresses)
            memory[0x0000] = 0x00u
            memory[0x0001] = 0x3Cu
            memory[0x0002] = 0x01u
            memory[0x0003] = 0xCDu
            memory[0x0016] = 0x06u
            memory[0x006E] = 0x00u
            memory[0x0087] = 0xFFu
            memory[0x071A] = 0x00u
            memory[0x071B] = 0x01u
            memory[0x071C] = 0x85u
            memory[0x071D] = 0x84u

            // Execute decompiled function
            runBowserFlame()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x3Cu, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x01u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0xCDu, memory[0x0003], "Memory 0x0003 mismatch")
        }
    }

    /**
     * Test case 6 from frame 3867
     * Function: runBowserFlame (0xC902)
     * Call depth: 8
     * Memory reads: 11, writes: 4
     */
    @Test
    fun `runBowserFlame_frame3867_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (11 addresses)
            memory[0x0000] = 0xFFu
            memory[0x0001] = 0xFDu
            memory[0x0002] = 0x01u
            memory[0x0003] = 0x8Du
            memory[0x0016] = 0x06u
            memory[0x006E] = 0x01u
            memory[0x0087] = 0x37u
            memory[0x071A] = 0x00u
            memory[0x071B] = 0x01u
            memory[0x071C] = 0x46u
            memory[0x071D] = 0x45u

            // Execute decompiled function
            runBowserFlame()

            // Verify: Check output memory (4 addresses)
            assertEquals(0xFFu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xFDu, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x01u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x8Du, memory[0x0003], "Memory 0x0003 mismatch")
        }
    }

    /**
     * Test case 7 from frame 4021
     * Function: runBowserFlame (0xC902)
     * Call depth: 8
     * Memory reads: 11, writes: 4
     */
    @Test
    fun `runBowserFlame_frame4021_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (11 addresses)
            memory[0x0000] = 0x00u
            memory[0x0001] = 0x05u
            memory[0x0002] = 0x01u
            memory[0x0003] = 0x96u
            memory[0x0016] = 0x06u
            memory[0x006E] = 0x01u
            memory[0x0087] = 0x10u
            memory[0x071A] = 0x00u
            memory[0x071B] = 0x01u
            memory[0x071C] = 0x4Eu
            memory[0x071D] = 0x4Du

            // Execute decompiled function
            runBowserFlame()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x05u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x01u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x96u, memory[0x0003], "Memory 0x0003 mismatch")
        }
    }

    /**
     * Test case 8 from frame 4175
     * Function: runBowserFlame (0xC902)
     * Call depth: 8
     * Memory reads: 11, writes: 4
     */
    @Test
    fun `runBowserFlame_frame4175_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (11 addresses)
            memory[0x0000] = 0x00u
            memory[0x0001] = 0x19u
            memory[0x0002] = 0x01u
            memory[0x0003] = 0xAAu
            memory[0x0016] = 0x06u
            memory[0x006E] = 0x00u
            memory[0x0087] = 0xEAu
            memory[0x071A] = 0x00u
            memory[0x071B] = 0x01u
            memory[0x071C] = 0x62u
            memory[0x071D] = 0x61u

            // Execute decompiled function
            runBowserFlame()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x19u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x01u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0xAAu, memory[0x0003], "Memory 0x0003 mismatch")
        }
    }

    /**
     * Test case 9 from frame 4343
     * Function: runBowserFlame (0xC902)
     * Call depth: 8
     * Memory reads: 11, writes: 4
     */
    @Test
    fun `runBowserFlame_frame4343_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (11 addresses)
            memory[0x0000] = 0x00u
            memory[0x0001] = 0x30u
            memory[0x0002] = 0x01u
            memory[0x0003] = 0xC1u
            memory[0x0016] = 0x06u
            memory[0x006E] = 0x00u
            memory[0x0087] = 0xCFu
            memory[0x071A] = 0x00u
            memory[0x071B] = 0x01u
            memory[0x071C] = 0x79u
            memory[0x071D] = 0x78u

            // Execute decompiled function
            runBowserFlame()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x30u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x01u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0xC1u, memory[0x0003], "Memory 0x0003 mismatch")
        }
    }

    // =========================================
    // 0xD903: setPRout
    // Parameters: A, Y
    // 1 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 1545
     * Function: setPRout (0xD903)
     * Parameters: A, Y
     * Call depth: 8
     * Memory reads: 7, writes: 7
     */
    @Test
    fun `setPRout_frame1545_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (7 addresses)
            memory[0x0008] = 0x00u
            memory[0x0046] = 0x02u
            memory[0x03AD] = 0x70u
            memory[0x03AE] = 0x7Au
            memory[0x0756] = 0x00u
            memory[0x0791] = 0x00u
            memory[0x079E] = 0x00u

            // Execute decompiled function
            setPRout(0x06, 0x06)

            // Verify: Check output memory (7 addresses)
            assertEquals(0x0Bu, memory[0x000E], "Memory 0x000E mismatch")
            assertEquals(0x01u, memory[0x001D], "Memory 0x001D mismatch")
            assertEquals(0x00u, memory[0x0057], "Memory 0x0057 mismatch")
            assertEquals(0xFCu, memory[0x009F], "Memory 0x009F mismatch")
            assertEquals(0x01u, memory[0x00FC], "Memory 0x00FC mismatch")
            assertEquals(0xFFu, memory[0x0747], "Memory 0x0747 mismatch")
            assertEquals(0x00u, memory[0x0775], "Memory 0x0775 mismatch")
        }
    }

    // =========================================
    // 0xD9EE: enemiesCollision
    // Parameters: X, Y
    // 1 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 4285
     * Function: enemiesCollision (0xD9EE)
     * Parameters: X, Y
     * Call depth: 8
     * Memory reads: 0, writes: 2
     */
    @Test
    fun `enemiesCollision_frame4285_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            enemiesCollision(0x00, 0x00)

            // Verify: Check output memory (2 addresses)
            assertEquals(0xFCu, memory[0x009F], "Memory 0x009F mismatch")
            assertEquals(0x10u, memory[0x0796], "Memory 0x0796 mismatch")
        }
    }

    // =========================================
    // 0xDDBB: handleCoinMetatile
    // 67 calls, 3 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 6925
     * Function: handleCoinMetatile (0xDDBB)
     * Call depth: 7
     * Memory reads: 2, writes: 0
     */
    @Test
    fun `handleCoinMetatile_frame6925_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x001D] = 0x00u
            memory[0x0033] = 0x01u

            // Execute decompiled function
            handleCoinMetatile()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 6971
     * Function: handleCoinMetatile (0xDDBB)
     * Call depth: 7
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `handleCoinMetatile_frame6971_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x001D] = 0x01u

            // Execute decompiled function
            handleCoinMetatile()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 2 from frame 7269
     * Function: handleCoinMetatile (0xDDBB)
     * Call depth: 7
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `handleCoinMetatile_frame7269_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x001D] = 0x01u

            // Execute decompiled function
            handleCoinMetatile()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0xDDC4: handleAxeMetatile
    // 24 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 6925
     * Function: handleAxeMetatile (0xDDC4)
     * Call depth: 7
     * Memory reads: 0, writes: 2
     */
    @Test
    fun `handleAxeMetatile_frame6925_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            handleAxeMetatile()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0xDF57: checkForCoinMTiles
    // Parameters: A
    // 67 calls, 14 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 6925
     * Function: checkForCoinMTiles (0xDF57)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `checkForCoinMTiles_frame6925_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            checkForCoinMTiles(0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 6927
     * Function: checkForCoinMTiles (0xDF57)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `checkForCoinMTiles_frame6927_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            checkForCoinMTiles(0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 2 from frame 6971
     * Function: checkForCoinMTiles (0xDF57)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `checkForCoinMTiles_frame6971_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0003] = 0x01u

            // Execute decompiled function
            checkForCoinMTiles(0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x43u, memory[0x0001], "Memory 0x0001 mismatch")
        }
    }

    /**
     * Test case 3 from frame 7287
     * Function: checkForCoinMTiles (0xDF57)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `checkForCoinMTiles_frame7287_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0003] = 0x01u

            // Execute decompiled function
            checkForCoinMTiles(0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x43u, memory[0x0001], "Memory 0x0001 mismatch")
        }
    }

    /**
     * Test case 4 from frame 7367
     * Function: checkForCoinMTiles (0xDF57)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `checkForCoinMTiles_frame7367_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0003] = 0x01u

            // Execute decompiled function
            checkForCoinMTiles(0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x43u, memory[0x0001], "Memory 0x0001 mismatch")
        }
    }

    /**
     * Test case 5 from frame 8845
     * Function: checkForCoinMTiles (0xDF57)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `checkForCoinMTiles_frame8845_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0003] = 0x02u

            // Execute decompiled function
            checkForCoinMTiles(0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x43u, memory[0x0001], "Memory 0x0001 mismatch")
        }
    }

    /**
     * Test case 6 from frame 9779
     * Function: checkForCoinMTiles (0xDF57)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `checkForCoinMTiles_frame9779_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0003] = 0x01u

            // Execute decompiled function
            checkForCoinMTiles(0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x43u, memory[0x0001], "Memory 0x0001 mismatch")
        }
    }

    /**
     * Test case 7 from frame 10227
     * Function: checkForCoinMTiles (0xDF57)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `checkForCoinMTiles_frame10227_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0003] = 0x01u

            // Execute decompiled function
            checkForCoinMTiles(0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x43u, memory[0x0001], "Memory 0x0001 mismatch")
        }
    }

    /**
     * Test case 8 from frame 12389
     * Function: checkForCoinMTiles (0xDF57)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `checkForCoinMTiles_frame12389_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0003] = 0x01u

            // Execute decompiled function
            checkForCoinMTiles(0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x43u, memory[0x0001], "Memory 0x0001 mismatch")
        }
    }

    /**
     * Test case 9 from frame 15543
     * Function: checkForCoinMTiles (0xDF57)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `checkForCoinMTiles_frame15543_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            checkForCoinMTiles(0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0xDF66: getMTileAttrib
    // Parameters: A
    // 67 calls, 14 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 6925
     * Function: getMTileAttrib (0xDF66)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 0, writes: 3
     */
    @Test
    fun `getMTileAttrib_frame6925_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getMTileAttrib(0xFF)

            // Verify: Check output memory (3 addresses)
            assertEquals(0xFFu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x0057], "Memory 0x0057 mismatch")
            assertEquals(0x10u, memory[0x0785], "Memory 0x0785 mismatch")
        }
    }

    /**
     * Test case 1 from frame 6927
     * Function: getMTileAttrib (0xDF66)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 0, writes: 3
     */
    @Test
    fun `getMTileAttrib_frame6927_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getMTileAttrib(0xFF)

            // Verify: Check output memory (3 addresses)
            assertEquals(0xFFu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x0057], "Memory 0x0057 mismatch")
            assertEquals(0x10u, memory[0x0785], "Memory 0x0785 mismatch")
        }
    }

    /**
     * Test case 2 from frame 6971
     * Function: getMTileAttrib (0xDF66)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `getMTileAttrib_frame6971_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0754] = 0x01u

            // Execute decompiled function
            getMTileAttrib(0xFF)

            // Verify: Check output memory (3 addresses)
            assertEquals(0xFFu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x0057], "Memory 0x0057 mismatch")
            assertEquals(0x10u, memory[0x0785], "Memory 0x0785 mismatch")
        }
    }

    /**
     * Test case 3 from frame 7287
     * Function: getMTileAttrib (0xDF66)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `getMTileAttrib_frame7287_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0754] = 0x01u

            // Execute decompiled function
            getMTileAttrib(0xFF)

            // Verify: Check output memory (3 addresses)
            assertEquals(0xFFu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x0057], "Memory 0x0057 mismatch")
            assertEquals(0x10u, memory[0x0785], "Memory 0x0785 mismatch")
        }
    }

    /**
     * Test case 4 from frame 7367
     * Function: getMTileAttrib (0xDF66)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `getMTileAttrib_frame7367_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0754] = 0x01u

            // Execute decompiled function
            getMTileAttrib(0xFF)

            // Verify: Check output memory (3 addresses)
            assertEquals(0xFFu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x0057], "Memory 0x0057 mismatch")
            assertEquals(0x10u, memory[0x0785], "Memory 0x0785 mismatch")
        }
    }

    /**
     * Test case 5 from frame 8845
     * Function: getMTileAttrib (0xDF66)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `getMTileAttrib_frame8845_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0754] = 0x01u

            // Execute decompiled function
            getMTileAttrib(0xFF)

            // Verify: Check output memory (3 addresses)
            assertEquals(0xFFu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x0057], "Memory 0x0057 mismatch")
            assertEquals(0x10u, memory[0x0785], "Memory 0x0785 mismatch")
        }
    }

    /**
     * Test case 6 from frame 9433
     * Function: getMTileAttrib (0xDF66)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 0, writes: 3
     */
    @Test
    fun `getMTileAttrib_frame9433_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getMTileAttrib(0xFF)

            // Verify: Check output memory (3 addresses)
            assertEquals(0xFFu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x0057], "Memory 0x0057 mismatch")
            assertEquals(0x10u, memory[0x0785], "Memory 0x0785 mismatch")
        }
    }

    /**
     * Test case 7 from frame 9779
     * Function: getMTileAttrib (0xDF66)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `getMTileAttrib_frame9779_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0754] = 0x01u

            // Execute decompiled function
            getMTileAttrib(0xFF)

            // Verify: Check output memory (3 addresses)
            assertEquals(0xFFu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x0057], "Memory 0x0057 mismatch")
            assertEquals(0x10u, memory[0x0785], "Memory 0x0785 mismatch")
        }
    }

    /**
     * Test case 8 from frame 12389
     * Function: getMTileAttrib (0xDF66)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `getMTileAttrib_frame12389_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x0754] = 0x01u

            // Execute decompiled function
            getMTileAttrib(0xFF)

            // Verify: Check output memory (3 addresses)
            assertEquals(0xFFu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x0057], "Memory 0x0057 mismatch")
            assertEquals(0x10u, memory[0x0785], "Memory 0x0785 mismatch")
        }
    }

    /**
     * Test case 9 from frame 15543
     * Function: getMTileAttrib (0xDF66)
     * Parameters: A
     * Call depth: 8
     * Memory reads: 0, writes: 3
     */
    @Test
    fun `getMTileAttrib_frame15543_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getMTileAttrib(0xFF)

            // Verify: Check output memory (3 addresses)
            assertEquals(0xFFu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x0057], "Memory 0x0057 mismatch")
            assertEquals(0x10u, memory[0x0785], "Memory 0x0785 mismatch")
        }
    }

    // =========================================
    // 0xDF77: enemyToBGCollisionDet
    // Parameters: X
    // 67 calls, 3 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 6925
     * Function: enemyToBGCollisionDet (0xDF77)
     * Parameters: X
     * Call depth: 8
     * Memory reads: 4, writes: 3
     */
    @Test
    fun `enemyToBGCollisionDet_frame6925_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x0000] = 0xFFu
            memory[0x006D] = 0x02u
            memory[0x0086] = 0x55u
            memory[0x0490] = 0xFFu

            // Execute decompiled function
            enemyToBGCollisionDet(0x01)

            // Verify: Check output memory (3 addresses)
            assertEquals(0x02u, memory[0x006D], "Memory 0x006D mismatch")
            assertEquals(0x54u, memory[0x0086], "Memory 0x0086 mismatch")
            assertEquals(0xFEu, memory[0x0490], "Memory 0x0490 mismatch")
        }
    }

    /**
     * Test case 1 from frame 6927
     * Function: enemyToBGCollisionDet (0xDF77)
     * Parameters: X
     * Call depth: 8
     * Memory reads: 4, writes: 3
     */
    @Test
    fun `enemyToBGCollisionDet_frame6927_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x0000] = 0xFFu
            memory[0x006D] = 0x02u
            memory[0x0086] = 0x54u
            memory[0x0490] = 0xFFu

            // Execute decompiled function
            enemyToBGCollisionDet(0x01)

            // Verify: Check output memory (3 addresses)
            assertEquals(0x02u, memory[0x006D], "Memory 0x006D mismatch")
            assertEquals(0x53u, memory[0x0086], "Memory 0x0086 mismatch")
            assertEquals(0xFEu, memory[0x0490], "Memory 0x0490 mismatch")
        }
    }

    /**
     * Test case 2 from frame 6929
     * Function: enemyToBGCollisionDet (0xDF77)
     * Parameters: X
     * Call depth: 8
     * Memory reads: 4, writes: 3
     */
    @Test
    fun `enemyToBGCollisionDet_frame6929_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x0000] = 0xFFu
            memory[0x006D] = 0x02u
            memory[0x0086] = 0x53u
            memory[0x0490] = 0xFFu

            // Execute decompiled function
            enemyToBGCollisionDet(0x01)

            // Verify: Check output memory (3 addresses)
            assertEquals(0x02u, memory[0x006D], "Memory 0x006D mismatch")
            assertEquals(0x52u, memory[0x0086], "Memory 0x0086 mismatch")
            assertEquals(0xFEu, memory[0x0490], "Memory 0x0490 mismatch")
        }
    }

    // =========================================
    // 0xE252: boundingBoxCore
    // Parameters: X, Y
    // 838 calls, 388 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 1159
     * Function: boundingBoxCore (0xE252)
     * Parameters: X, Y
     * Call depth: 8
     * Memory reads: 7, writes: 6
     */
    @Test
    fun `boundingBoxCore_frame1159_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (7 addresses)
            memory[0x0000] = 0x48u
            memory[0x0001] = 0x2Eu
            memory[0x006E] = 0x01u
            memory[0x0087] = 0x60u
            memory[0x03D1] = 0x0Fu
            memory[0x071A] = 0x00u
            memory[0x071C] = 0x32u

            // Execute decompiled function
            boundingBoxCore(0x00, 0x44)

            // Verify: Check output memory (6 addresses)
            assertEquals(0x2Eu, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x08u, memory[0x03D8], "Memory 0x03D8 mismatch")
            assertEquals(0xFFu, memory[0x04B0], "Memory 0x04B0 mismatch")
            assertEquals(0xFFu, memory[0x04B1], "Memory 0x04B1 mismatch")
            assertEquals(0xFFu, memory[0x04B2], "Memory 0x04B2 mismatch")
            assertEquals(0xFFu, memory[0x04B3], "Memory 0x04B3 mismatch")
        }
    }

    /**
     * Test case 1 from frame 1235
     * Function: boundingBoxCore (0xE252)
     * Parameters: X, Y
     * Call depth: 8
     * Memory reads: 7, writes: 4
     */
    @Test
    fun `boundingBoxCore_frame1235_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (7 addresses)
            memory[0x0000] = 0x48u
            memory[0x0001] = 0xF5u
            memory[0x006E] = 0x01u
            memory[0x0087] = 0x4Du
            memory[0x03D1] = 0x03u
            memory[0x071A] = 0x00u
            memory[0x071C] = 0x58u

            // Execute decompiled function
            boundingBoxCore(0x00, 0x44)

            // Verify: Check output memory (2 addresses)
            assertEquals(0xF5u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x00u, memory[0x03D8], "Memory 0x03D8 mismatch")
        }
    }

    /**
     * Test case 2 from frame 1315
     * Function: boundingBoxCore (0xE252)
     * Parameters: X, Y
     * Call depth: 8
     * Memory reads: 7, writes: 4
     */
    @Test
    fun `boundingBoxCore_frame1315_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (7 addresses)
            memory[0x0000] = 0x48u
            memory[0x0001] = 0xB8u
            memory[0x006E] = 0x01u
            memory[0x0087] = 0x39u
            memory[0x03D1] = 0x00u
            memory[0x071A] = 0x00u
            memory[0x071C] = 0x81u

            // Execute decompiled function
            boundingBoxCore(0x00, 0x44)

            // Verify: Check output memory (2 addresses)
            assertEquals(0xB8u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x00u, memory[0x03D8], "Memory 0x03D8 mismatch")
        }
    }

    /**
     * Test case 3 from frame 1453
     * Function: boundingBoxCore (0xE252)
     * Parameters: X, Y
     * Call depth: 8
     * Memory reads: 7, writes: 4
     */
    @Test
    fun `boundingBoxCore_frame1453_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (7 addresses)
            memory[0x0000] = 0x48u
            memory[0x0001] = 0x91u
            memory[0x006E] = 0x01u
            memory[0x0087] = 0x16u
            memory[0x03D1] = 0x00u
            memory[0x071A] = 0x00u
            memory[0x071C] = 0x85u

            // Execute decompiled function
            boundingBoxCore(0x00, 0x44)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x91u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x00u, memory[0x03D8], "Memory 0x03D8 mismatch")
        }
    }

    /**
     * Test case 4 from frame 3807
     * Function: boundingBoxCore (0xE252)
     * Parameters: X, Y
     * Call depth: 8
     * Memory reads: 7, writes: 6
     */
    @Test
    fun `boundingBoxCore_frame3807_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (7 addresses)
            memory[0x0000] = 0x48u
            memory[0x0001] = 0x00u
            memory[0x006E] = 0x01u
            memory[0x0087] = 0x46u
            memory[0x03D1] = 0x0Fu
            memory[0x071A] = 0x00u
            memory[0x071C] = 0x46u

            // Execute decompiled function
            boundingBoxCore(0x00, 0x44)

            // Verify: Check output memory (6 addresses)
            assertEquals(0x00u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x08u, memory[0x03D8], "Memory 0x03D8 mismatch")
            assertEquals(0xFFu, memory[0x04B0], "Memory 0x04B0 mismatch")
            assertEquals(0xFFu, memory[0x04B1], "Memory 0x04B1 mismatch")
            assertEquals(0xFFu, memory[0x04B2], "Memory 0x04B2 mismatch")
            assertEquals(0xFFu, memory[0x04B3], "Memory 0x04B3 mismatch")
        }
    }

    /**
     * Test case 5 from frame 3963
     * Function: boundingBoxCore (0xE252)
     * Parameters: X, Y
     * Call depth: 8
     * Memory reads: 7, writes: 4
     */
    @Test
    fun `boundingBoxCore_frame3963_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (7 addresses)
            memory[0x0000] = 0x48u
            memory[0x0001] = 0xD9u
            memory[0x006E] = 0x01u
            memory[0x0087] = 0x1Fu
            memory[0x03D1] = 0x00u
            memory[0x071A] = 0x00u
            memory[0x071C] = 0x46u

            // Execute decompiled function
            boundingBoxCore(0x00, 0x44)

            // Verify: Check output memory (2 addresses)
            assertEquals(0xD9u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x00u, memory[0x03D8], "Memory 0x03D8 mismatch")
        }
    }

    /**
     * Test case 6 from frame 4067
     * Function: boundingBoxCore (0xE252)
     * Parameters: X, Y
     * Call depth: 8
     * Memory reads: 7, writes: 4
     */
    @Test
    fun `boundingBoxCore_frame4067_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (7 addresses)
            memory[0x0000] = 0x48u
            memory[0x0001] = 0xAAu
            memory[0x006E] = 0x01u
            memory[0x0087] = 0x05u
            memory[0x03D1] = 0x00u
            memory[0x071A] = 0x00u
            memory[0x071C] = 0x5Bu

            // Execute decompiled function
            boundingBoxCore(0x00, 0x44)

            // Verify: Check output memory (2 addresses)
            assertEquals(0xAAu, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x00u, memory[0x03D8], "Memory 0x03D8 mismatch")
        }
    }

    /**
     * Test case 7 from frame 4219
     * Function: boundingBoxCore (0xE252)
     * Parameters: X, Y
     * Call depth: 8
     * Memory reads: 7, writes: 4
     */
    @Test
    fun `boundingBoxCore_frame4219_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (7 addresses)
            memory[0x0000] = 0x48u
            memory[0x0001] = 0x7Du
            memory[0x006E] = 0x00u
            memory[0x0087] = 0xDFu
            memory[0x03D1] = 0x00u
            memory[0x071A] = 0x00u
            memory[0x071C] = 0x62u

            // Execute decompiled function
            boundingBoxCore(0x00, 0x44)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x7Du, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x00u, memory[0x03D8], "Memory 0x03D8 mismatch")
        }
    }

    /**
     * Test case 8 from frame 4343
     * Function: boundingBoxCore (0xE252)
     * Parameters: X, Y
     * Call depth: 8
     * Memory reads: 7, writes: 4
     */
    @Test
    fun `boundingBoxCore_frame4343_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (7 addresses)
            memory[0x0000] = 0x48u
            memory[0x0001] = 0x56u
            memory[0x006E] = 0x00u
            memory[0x0087] = 0xCFu
            memory[0x03D1] = 0x00u
            memory[0x071A] = 0x00u
            memory[0x071C] = 0x79u

            // Execute decompiled function
            boundingBoxCore(0x00, 0x44)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x56u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x00u, memory[0x03D8], "Memory 0x03D8 mismatch")
        }
    }

    /**
     * Test case 9 from frame 5053
     * Function: boundingBoxCore (0xE252)
     * Parameters: X, Y
     * Call depth: 8
     * Memory reads: 7, writes: 6
     */
    @Test
    fun `boundingBoxCore_frame5053_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (7 addresses)
            memory[0x0000] = 0x48u
            memory[0x0001] = 0x2Cu
            memory[0x006E] = 0x02u
            memory[0x0087] = 0x90u
            memory[0x03D1] = 0x0Fu
            memory[0x071A] = 0x01u
            memory[0x071C] = 0x64u

            // Execute decompiled function
            boundingBoxCore(0x00, 0x44)

            // Verify: Check output memory (6 addresses)
            assertEquals(0x2Cu, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x08u, memory[0x03D8], "Memory 0x03D8 mismatch")
            assertEquals(0xFFu, memory[0x04B0], "Memory 0x04B0 mismatch")
            assertEquals(0xFFu, memory[0x04B1], "Memory 0x04B1 mismatch")
            assertEquals(0xFFu, memory[0x04B2], "Memory 0x04B2 mismatch")
            assertEquals(0xFFu, memory[0x04B3], "Memory 0x04B3 mismatch")
        }
    }

    // =========================================
    // 0xE2DB: playerCollisionCore
    // 9458 calls, 104 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 200
     * Function: playerCollisionCore (0xE2DB)
     * Call depth: 7
     * Memory reads: 16, writes: 12
     */
    @Test
    fun `playerCollisionCore_frame200_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (16 addresses)
            memory[0x0000] = 0x00u
            memory[0x0004] = 0x00u
            memory[0x000E] = 0x08u
            memory[0x001D] = 0x00u
            memory[0x009F] = 0x00u
            memory[0x00B5] = 0x01u
            memory[0x00CE] = 0xB0u
            memory[0x00EB] = 0x0Eu
            memory[0x01F3] = 0x09u
            memory[0x01F4] = 0xC3u
            memory[0x01F5] = 0xDCu
            memory[0x0704] = 0x00u
            memory[0x070E] = 0x00u
            memory[0x0714] = 0x00u
            memory[0x0716] = 0x00u
            memory[0x0754] = 0x01u

            // Execute decompiled function
            playerCollisionCore()

            // Verify: Check output memory (9 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x54u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x00u, memory[0x001D], "Memory 0x001D mismatch")
            assertEquals(0x00u, memory[0x009F], "Memory 0x009F mismatch")
            assertEquals(0xB0u, memory[0x00CE], "Memory 0x00CE mismatch")
            assertEquals(0x13u, memory[0x00EB], "Memory 0x00EB mismatch")
            assertEquals(0x00u, memory[0x0433], "Memory 0x0433 mismatch")
            assertEquals(0x00u, memory[0x0484], "Memory 0x0484 mismatch")
            assertEquals(0xFFu, memory[0x0490], "Memory 0x0490 mismatch")
        }
    }

    /**
     * Test case 1 from frame 240
     * Function: playerCollisionCore (0xE2DB)
     * Call depth: 7
     * Memory reads: 13, writes: 7
     */
    @Test
    fun `playerCollisionCore_frame240_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (13 addresses)
            memory[0x0000] = 0x00u
            memory[0x000E] = 0x08u
            memory[0x001D] = 0x01u
            memory[0x00B5] = 0x01u
            memory[0x00CE] = 0x98u
            memory[0x00EB] = 0x0Eu
            memory[0x01F3] = 0x09u
            memory[0x01F4] = 0xC3u
            memory[0x01F5] = 0xDCu
            memory[0x0704] = 0x00u
            memory[0x0714] = 0x00u
            memory[0x0716] = 0x00u
            memory[0x0754] = 0x01u

            // Execute decompiled function
            playerCollisionCore()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x13u, memory[0x00EB], "Memory 0x00EB mismatch")
            assertEquals(0xFFu, memory[0x0490], "Memory 0x0490 mismatch")
        }
    }

    /**
     * Test case 2 from frame 296
     * Function: playerCollisionCore (0xE2DB)
     * Call depth: 7
     * Memory reads: 13, writes: 7
     */
    @Test
    fun `playerCollisionCore_frame296_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (13 addresses)
            memory[0x0000] = 0x00u
            memory[0x000E] = 0x08u
            memory[0x001D] = 0x01u
            memory[0x00B5] = 0x01u
            memory[0x00CE] = 0x8Du
            memory[0x00EB] = 0x0Eu
            memory[0x01F3] = 0x09u
            memory[0x01F4] = 0xC3u
            memory[0x01F5] = 0xDCu
            memory[0x0704] = 0x00u
            memory[0x0714] = 0x00u
            memory[0x0716] = 0x00u
            memory[0x0754] = 0x01u

            // Execute decompiled function
            playerCollisionCore()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x13u, memory[0x00EB], "Memory 0x00EB mismatch")
            assertEquals(0xFFu, memory[0x0490], "Memory 0x0490 mismatch")
        }
    }

    /**
     * Test case 3 from frame 340
     * Function: playerCollisionCore (0xE2DB)
     * Call depth: 7
     * Memory reads: 13, writes: 7
     */
    @Test
    fun `playerCollisionCore_frame340_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (13 addresses)
            memory[0x0000] = 0x00u
            memory[0x000E] = 0x08u
            memory[0x001D] = 0x01u
            memory[0x00B5] = 0x01u
            memory[0x00CE] = 0xAAu
            memory[0x00EB] = 0x0Eu
            memory[0x01F3] = 0x09u
            memory[0x01F4] = 0xC3u
            memory[0x01F5] = 0xDCu
            memory[0x0704] = 0x00u
            memory[0x0714] = 0x00u
            memory[0x0716] = 0x00u
            memory[0x0754] = 0x01u

            // Execute decompiled function
            playerCollisionCore()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x13u, memory[0x00EB], "Memory 0x00EB mismatch")
            assertEquals(0xFFu, memory[0x0490], "Memory 0x0490 mismatch")
        }
    }

    /**
     * Test case 4 from frame 460
     * Function: playerCollisionCore (0xE2DB)
     * Call depth: 7
     * Memory reads: 13, writes: 7
     */
    @Test
    fun `playerCollisionCore_frame460_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (13 addresses)
            memory[0x0000] = 0x00u
            memory[0x000E] = 0x08u
            memory[0x001D] = 0x01u
            memory[0x00B5] = 0x01u
            memory[0x00CE] = 0x80u
            memory[0x00EB] = 0x0Eu
            memory[0x01F3] = 0x09u
            memory[0x01F4] = 0xC3u
            memory[0x01F5] = 0xDCu
            memory[0x0704] = 0x00u
            memory[0x0714] = 0x00u
            memory[0x0716] = 0x00u
            memory[0x0754] = 0x01u

            // Execute decompiled function
            playerCollisionCore()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x13u, memory[0x00EB], "Memory 0x00EB mismatch")
            assertEquals(0xFFu, memory[0x0490], "Memory 0x0490 mismatch")
        }
    }

    /**
     * Test case 5 from frame 712
     * Function: playerCollisionCore (0xE2DB)
     * Call depth: 7
     * Memory reads: 13, writes: 7
     */
    @Test
    fun `playerCollisionCore_frame712_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (13 addresses)
            memory[0x0000] = 0x00u
            memory[0x000E] = 0x08u
            memory[0x001D] = 0x01u
            memory[0x00B5] = 0x01u
            memory[0x00CE] = 0x8Fu
            memory[0x00EB] = 0x0Eu
            memory[0x01F3] = 0x09u
            memory[0x01F4] = 0xC3u
            memory[0x01F5] = 0xDCu
            memory[0x0704] = 0x00u
            memory[0x0714] = 0x00u
            memory[0x0716] = 0x00u
            memory[0x0754] = 0x01u

            // Execute decompiled function
            playerCollisionCore()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x13u, memory[0x00EB], "Memory 0x00EB mismatch")
            assertEquals(0xFFu, memory[0x0490], "Memory 0x0490 mismatch")
        }
    }

    /**
     * Test case 6 from frame 1702
     * Function: playerCollisionCore (0xE2DB)
     * Call depth: 7
     * Memory reads: 3, writes: 0
     */
    @Test
    fun `playerCollisionCore_frame1702_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x0000] = 0x00u
            memory[0x000E] = 0x0Bu
            memory[0x0716] = 0x00u

            // Execute decompiled function
            playerCollisionCore()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 7 from frame 4734
     * Function: playerCollisionCore (0xE2DB)
     * Call depth: 7
     * Memory reads: 13, writes: 7
     */
    @Test
    fun `playerCollisionCore_frame4734_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (13 addresses)
            memory[0x0000] = 0x00u
            memory[0x000E] = 0x08u
            memory[0x001D] = 0x01u
            memory[0x00B5] = 0x01u
            memory[0x00CE] = 0x77u
            memory[0x00EB] = 0x0Eu
            memory[0x01F3] = 0x09u
            memory[0x01F4] = 0xC3u
            memory[0x01F5] = 0xDCu
            memory[0x0704] = 0x00u
            memory[0x0714] = 0x00u
            memory[0x0716] = 0x00u
            memory[0x0754] = 0x01u

            // Execute decompiled function
            playerCollisionCore()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x13u, memory[0x00EB], "Memory 0x00EB mismatch")
            assertEquals(0xFFu, memory[0x0490], "Memory 0x0490 mismatch")
        }
    }

    /**
     * Test case 8 from frame 5016
     * Function: playerCollisionCore (0xE2DB)
     * Call depth: 7
     * Memory reads: 13, writes: 7
     */
    @Test
    fun `playerCollisionCore_frame5016_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (13 addresses)
            memory[0x0000] = 0x00u
            memory[0x000E] = 0x08u
            memory[0x001D] = 0x01u
            memory[0x00B5] = 0x01u
            memory[0x00CE] = 0x61u
            memory[0x00EB] = 0x0Eu
            memory[0x01F3] = 0x09u
            memory[0x01F4] = 0xC3u
            memory[0x01F5] = 0xDCu
            memory[0x0704] = 0x00u
            memory[0x0714] = 0x00u
            memory[0x0716] = 0x00u
            memory[0x0754] = 0x01u

            // Execute decompiled function
            playerCollisionCore()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x13u, memory[0x00EB], "Memory 0x00EB mismatch")
            assertEquals(0xFFu, memory[0x0490], "Memory 0x0490 mismatch")
        }
    }

    /**
     * Test case 9 from frame 5052
     * Function: playerCollisionCore (0xE2DB)
     * Call depth: 7
     * Memory reads: 13, writes: 7
     */
    @Test
    fun `playerCollisionCore_frame5052_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (13 addresses)
            memory[0x0000] = 0x00u
            memory[0x000E] = 0x08u
            memory[0x001D] = 0x01u
            memory[0x00B5] = 0x01u
            memory[0x00CE] = 0x72u
            memory[0x00EB] = 0x0Eu
            memory[0x01F3] = 0x09u
            memory[0x01F4] = 0xC3u
            memory[0x01F5] = 0xDCu
            memory[0x0704] = 0x00u
            memory[0x0714] = 0x00u
            memory[0x0716] = 0x00u
            memory[0x0754] = 0x01u

            // Execute decompiled function
            playerCollisionCore()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x0001], "Memory 0x0001 mismatch")
            assertEquals(0x13u, memory[0x00EB], "Memory 0x00EB mismatch")
            assertEquals(0xFFu, memory[0x0490], "Memory 0x0490 mismatch")
        }
    }

    // =========================================
    // 0xE2DD: sprObjectCollisionCore
    // Parameters: X, Y
    // 9458 calls, 6 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 200
     * Function: sprObjectCollisionCore (0xE2DD)
     * Parameters: X, Y
     * Call depth: 7
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `sprObjectCollisionCore_frame200_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            sprObjectCollisionCore(0x00, 0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 436
     * Function: sprObjectCollisionCore (0xE2DD)
     * Parameters: X, Y
     * Call depth: 7
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `sprObjectCollisionCore_frame436_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            sprObjectCollisionCore(0x00, 0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 2 from frame 1221
     * Function: sprObjectCollisionCore (0xE2DD)
     * Parameters: X, Y
     * Call depth: 9
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `sprObjectCollisionCore_frame1221_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            sprObjectCollisionCore(0x01, 0x04)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 3 from frame 1702
     * Function: sprObjectCollisionCore (0xE2DD)
     * Parameters: X, Y
     * Call depth: 7
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `sprObjectCollisionCore_frame1702_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            sprObjectCollisionCore(0x00, 0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 4 from frame 4375
     * Function: sprObjectCollisionCore (0xE2DD)
     * Parameters: X, Y
     * Call depth: 9
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `sprObjectCollisionCore_frame4375_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            sprObjectCollisionCore(0x11, 0x44)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 5 from frame 4389
     * Function: sprObjectCollisionCore (0xE2DD)
     * Parameters: X, Y
     * Call depth: 8
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `sprObjectCollisionCore_frame4389_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            sprObjectCollisionCore(0x11, 0x44)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0xE352: blockbufferchkFball
    // Parameters: X
    // 19 calls, 19 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 4289
     * Function: blockbufferchkFball (0xE352)
     * Parameters: X
     * Call depth: 9
     * Memory reads: 8, writes: 1
     */
    @Test
    fun `blockbufferchkFball_frame4289_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (8 addresses)
            memory[0x0006] = 0x04u
            memory[0x0007] = 0x01u
            memory[0x04AC] = 0x6Fu
            memory[0x04AD] = 0xB4u
            memory[0x04AF] = 0xC0u
            memory[0x04B1] = 0xC6u
            memory[0x04B2] = 0x78u
            memory[0x04B3] = 0xCCu

            // Execute decompiled function
            blockbufferchkFball(0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
        }
    }

    /**
     * Test case 1 from frame 4293
     * Function: blockbufferchkFball (0xE352)
     * Parameters: X
     * Call depth: 9
     * Memory reads: 8, writes: 1
     */
    @Test
    fun `blockbufferchkFball_frame4293_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (8 addresses)
            memory[0x0006] = 0x04u
            memory[0x0007] = 0x01u
            memory[0x04AC] = 0x6Fu
            memory[0x04AD] = 0xAFu
            memory[0x04AF] = 0xBBu
            memory[0x04B1] = 0xC6u
            memory[0x04B2] = 0x77u
            memory[0x04B3] = 0xCCu

            // Execute decompiled function
            blockbufferchkFball(0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
        }
    }

    /**
     * Test case 2 from frame 4301
     * Function: blockbufferchkFball (0xE352)
     * Parameters: X
     * Call depth: 9
     * Memory reads: 8, writes: 1
     */
    @Test
    fun `blockbufferchkFball_frame4301_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (8 addresses)
            memory[0x0006] = 0x04u
            memory[0x0007] = 0x01u
            memory[0x04AC] = 0x6Fu
            memory[0x04AD] = 0xABu
            memory[0x04AF] = 0xB7u
            memory[0x04B1] = 0xC6u
            memory[0x04B2] = 0x75u
            memory[0x04B3] = 0xCCu

            // Execute decompiled function
            blockbufferchkFball(0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
        }
    }

    /**
     * Test case 3 from frame 4309
     * Function: blockbufferchkFball (0xE352)
     * Parameters: X
     * Call depth: 9
     * Memory reads: 8, writes: 1
     */
    @Test
    fun `blockbufferchkFball_frame4309_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (8 addresses)
            memory[0x0006] = 0x04u
            memory[0x0007] = 0x01u
            memory[0x04AC] = 0x6Fu
            memory[0x04AD] = 0xADu
            memory[0x04AF] = 0xB9u
            memory[0x04B1] = 0xC6u
            memory[0x04B2] = 0x73u
            memory[0x04B3] = 0xCCu

            // Execute decompiled function
            blockbufferchkFball(0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
        }
    }

    /**
     * Test case 4 from frame 4317
     * Function: blockbufferchkFball (0xE352)
     * Parameters: X
     * Call depth: 9
     * Memory reads: 8, writes: 1
     */
    @Test
    fun `blockbufferchkFball_frame4317_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (8 addresses)
            memory[0x0006] = 0x04u
            memory[0x0007] = 0x01u
            memory[0x04AC] = 0x6Fu
            memory[0x04AD] = 0xB6u
            memory[0x04AF] = 0xC2u
            memory[0x04B1] = 0xC6u
            memory[0x04B2] = 0x70u
            memory[0x04B3] = 0xCCu

            // Execute decompiled function
            blockbufferchkFball(0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
        }
    }

    /**
     * Test case 5 from frame 4325
     * Function: blockbufferchkFball (0xE352)
     * Parameters: X
     * Call depth: 9
     * Memory reads: 3, writes: 0
     */
    @Test
    fun `blockbufferchkFball_frame4325_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x0006] = 0x04u
            memory[0x04AC] = 0x6Fu
            memory[0x04B2] = 0x6Cu

            // Execute decompiled function
            blockbufferchkFball(0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 6 from frame 4333
     * Function: blockbufferchkFball (0xE352)
     * Parameters: X
     * Call depth: 9
     * Memory reads: 3, writes: 0
     */
    @Test
    fun `blockbufferchkFball_frame4333_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x0006] = 0x04u
            memory[0x04AC] = 0x70u
            memory[0x04B2] = 0x68u

            // Execute decompiled function
            blockbufferchkFball(0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 7 from frame 4341
     * Function: blockbufferchkFball (0xE352)
     * Parameters: X
     * Call depth: 9
     * Memory reads: 3, writes: 0
     */
    @Test
    fun `blockbufferchkFball_frame4341_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x0006] = 0x04u
            memory[0x04AC] = 0x71u
            memory[0x04B2] = 0x64u

            // Execute decompiled function
            blockbufferchkFball(0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 8 from frame 4349
     * Function: blockbufferchkFball (0xE352)
     * Parameters: X
     * Call depth: 9
     * Memory reads: 3, writes: 0
     */
    @Test
    fun `blockbufferchkFball_frame4349_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x0006] = 0x04u
            memory[0x04AC] = 0x73u
            memory[0x04B2] = 0x60u

            // Execute decompiled function
            blockbufferchkFball(0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 9 from frame 4357
     * Function: blockbufferchkFball (0xE352)
     * Parameters: X
     * Call depth: 9
     * Memory reads: 3, writes: 0
     */
    @Test
    fun `blockbufferchkFball_frame4357_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x0006] = 0x04u
            memory[0x04AC] = 0x73u
            memory[0x04B2] = 0x58u

            // Execute decompiled function
            blockbufferchkFball(0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0xE3EB: drawVine
    // Parameters: Y
    // 25620 calls, 5198 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 200
     * Function: drawVine (0xE3EB)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 13, writes: 7
     */
    @Test
    fun `drawVine_frame200_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (13 addresses)
            memory[0x0003] = 0x00u
            memory[0x0004] = 0x0Eu
            memory[0x0005] = 0x30u
            memory[0x0006] = 0x03u
            memory[0x0007] = 0x05u
            memory[0x006D] = 0x00u
            memory[0x0086] = 0x28u
            memory[0x00CE] = 0xB0u
            memory[0x01A9] = 0x00u
            memory[0x01F1] = 0x0Au
            memory[0x01F2] = 0xE4u
            memory[0x01F3] = 0x00u
            memory[0x05A3] = 0x00u

            // Execute decompiled function
            drawVine(0x0E)

            // Verify: Check output memory (4 addresses)
            assertEquals(0xA0u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x00u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x30u, memory[0x0005], "Memory 0x0005 mismatch")
        }
    }

    /**
     * Test case 1 from frame 1120
     * Function: drawVine (0xE3EB)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 13, writes: 7
     */
    @Test
    fun `drawVine_frame1120_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (13 addresses)
            memory[0x0003] = 0x00u
            memory[0x0004] = 0x0Eu
            memory[0x0005] = 0x7Au
            memory[0x0006] = 0x07u
            memory[0x0007] = 0x05u
            memory[0x006D] = 0x00u
            memory[0x0086] = 0x72u
            memory[0x00CE] = 0x96u
            memory[0x01A9] = 0x00u
            memory[0x01F1] = 0x0Au
            memory[0x01F2] = 0xE4u
            memory[0x01F3] = 0x00u
            memory[0x0587] = 0x00u

            // Execute decompiled function
            drawVine(0x0E)

            // Verify: Check output memory (4 addresses)
            assertEquals(0x80u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x00u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x06u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x7Au, memory[0x0005], "Memory 0x0005 mismatch")
        }
    }

    /**
     * Test case 2 from frame 3034
     * Function: drawVine (0xE3EB)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 13, writes: 7
     */
    @Test
    fun `drawVine_frame3034_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (13 addresses)
            memory[0x0003] = 0x00u
            memory[0x0004] = 0x0Fu
            memory[0x0005] = 0x41u
            memory[0x0006] = 0x04u
            memory[0x0007] = 0x05u
            memory[0x006D] = 0x00u
            memory[0x0086] = 0x3Eu
            memory[0x00CE] = 0x89u
            memory[0x01A9] = 0x00u
            memory[0x01F1] = 0x0Au
            memory[0x01F2] = 0xE4u
            memory[0x01F3] = 0x00u
            memory[0x0584] = 0x00u

            // Execute decompiled function
            drawVine(0x0F)

            // Verify: Check output memory (4 addresses)
            assertEquals(0x80u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x00u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x09u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x41u, memory[0x0005], "Memory 0x0005 mismatch")
        }
    }

    /**
     * Test case 3 from frame 3762
     * Function: drawVine (0xE3EB)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 13, writes: 7
     */
    @Test
    fun `drawVine_frame3762_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (13 addresses)
            memory[0x0003] = 0x00u
            memory[0x0004] = 0x10u
            memory[0x0005] = 0xC0u
            memory[0x0006] = 0x0Cu
            memory[0x0007] = 0x05u
            memory[0x006D] = 0x00u
            memory[0x0086] = 0xB4u
            memory[0x00CE] = 0x94u
            memory[0x01A9] = 0x00u
            memory[0x01F0] = 0x0Au
            memory[0x01F1] = 0xE4u
            memory[0x01F2] = 0x00u
            memory[0x059C] = 0x00u

            // Execute decompiled function
            drawVine(0x10)

            // Verify: Check output memory (4 addresses)
            assertEquals(0x90u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x00u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x04u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0xC0u, memory[0x0005], "Memory 0x0005 mismatch")
        }
    }

    /**
     * Test case 4 from frame 4490
     * Function: drawVine (0xE3EB)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 13, writes: 7
     */
    @Test
    fun `drawVine_frame4490_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (13 addresses)
            memory[0x0003] = 0x00u
            memory[0x0004] = 0x0Eu
            memory[0x0005] = 0x4Au
            memory[0x0006] = 0xD4u
            memory[0x0007] = 0x05u
            memory[0x006D] = 0x01u
            memory[0x0086] = 0x42u
            memory[0x00CE] = 0x93u
            memory[0x01A9] = 0x00u
            memory[0x01F1] = 0x0Au
            memory[0x01F2] = 0xE4u
            memory[0x01F3] = 0x00u
            memory[0x0654] = 0x00u

            // Execute decompiled function
            drawVine(0x0E)

            // Verify: Check output memory (4 addresses)
            assertEquals(0x80u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x00u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x03u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x4Au, memory[0x0005], "Memory 0x0005 mismatch")
        }
    }

    /**
     * Test case 5 from frame 4948
     * Function: drawVine (0xE3EB)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 13, writes: 7
     */
    @Test
    fun `drawVine_frame4948_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (13 addresses)
            memory[0x0003] = 0x00u
            memory[0x0004] = 0x10u
            memory[0x0005] = 0xBEu
            memory[0x0006] = 0xDBu
            memory[0x0007] = 0x05u
            memory[0x006D] = 0x01u
            memory[0x0086] = 0xB2u
            memory[0x00CE] = 0x93u
            memory[0x01A9] = 0x00u
            memory[0x01F0] = 0x0Au
            memory[0x01F1] = 0xE4u
            memory[0x01F2] = 0x00u
            memory[0x066B] = 0x00u

            // Execute decompiled function
            drawVine(0x10)

            // Verify: Check output memory (4 addresses)
            assertEquals(0x90u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x00u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x03u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0xBEu, memory[0x0005], "Memory 0x0005 mismatch")
        }
    }

    /**
     * Test case 6 from frame 6734
     * Function: drawVine (0xE3EB)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 13, writes: 7
     */
    @Test
    fun `drawVine_frame6734_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (13 addresses)
            memory[0x0003] = 0x00u
            memory[0x0004] = 0x10u
            memory[0x0005] = 0x11u
            memory[0x0006] = 0x01u
            memory[0x0007] = 0x05u
            memory[0x006D] = 0x02u
            memory[0x0086] = 0x05u
            memory[0x00CE] = 0x94u
            memory[0x01A9] = 0x00u
            memory[0x01F0] = 0x0Au
            memory[0x01F1] = 0xE4u
            memory[0x01F2] = 0x00u
            memory[0x0591] = 0x00u

            // Execute decompiled function
            drawVine(0x10)

            // Verify: Check output memory (4 addresses)
            assertEquals(0x90u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x00u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x04u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x11u, memory[0x0005], "Memory 0x0005 mismatch")
        }
    }

    /**
     * Test case 7 from frame 7974
     * Function: drawVine (0xE3EB)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 13, writes: 7
     */
    @Test
    fun `drawVine_frame7974_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (13 addresses)
            memory[0x0003] = 0x00u
            memory[0x0004] = 0x0Eu
            memory[0x0005] = 0x4Eu
            memory[0x0006] = 0x04u
            memory[0x0007] = 0x05u
            memory[0x006D] = 0x02u
            memory[0x0086] = 0x46u
            memory[0x00CE] = 0x99u
            memory[0x01A9] = 0x00u
            memory[0x01F1] = 0x0Au
            memory[0x01F2] = 0xE4u
            memory[0x01F3] = 0x00u
            memory[0x0584] = 0x00u

            // Execute decompiled function
            drawVine(0x0E)

            // Verify: Check output memory (4 addresses)
            assertEquals(0x80u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x00u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x09u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x4Eu, memory[0x0005], "Memory 0x0005 mismatch")
        }
    }

    /**
     * Test case 8 from frame 10208
     * Function: drawVine (0xE3EB)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 13, writes: 7
     */
    @Test
    fun `drawVine_frame10208_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (13 addresses)
            memory[0x0003] = 0x00u
            memory[0x0004] = 0x0Fu
            memory[0x0005] = 0x51u
            memory[0x0006] = 0x05u
            memory[0x0007] = 0x05u
            memory[0x006D] = 0x02u
            memory[0x0086] = 0x4Eu
            memory[0x00CE] = 0x87u
            memory[0x01A9] = 0x00u
            memory[0x01F1] = 0x0Au
            memory[0x01F2] = 0xE4u
            memory[0x01F3] = 0x00u
            memory[0x0585] = 0x00u

            // Execute decompiled function
            drawVine(0x0F)

            // Verify: Check output memory (4 addresses)
            assertEquals(0x80u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x00u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x07u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x51u, memory[0x0005], "Memory 0x0005 mismatch")
        }
    }

    /**
     * Test case 9 from frame 16390
     * Function: drawVine (0xE3EB)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 13, writes: 7
     */
    @Test
    fun `drawVine_frame16390_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (13 addresses)
            memory[0x0003] = 0x00u
            memory[0x0004] = 0x10u
            memory[0x0005] = 0x5Fu
            memory[0x0006] = 0x05u
            memory[0x0007] = 0x05u
            memory[0x006D] = 0x02u
            memory[0x0086] = 0x53u
            memory[0x00CE] = 0xA5u
            memory[0x01A9] = 0x00u
            memory[0x01F0] = 0x0Au
            memory[0x01F1] = 0xE4u
            memory[0x01F2] = 0x00u
            memory[0x05A5] = 0x00u

            // Execute decompiled function
            drawVine(0x10)

            // Verify: Check output memory (4 addresses)
            assertEquals(0xA0u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x00u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x05u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x5Fu, memory[0x0005], "Memory 0x0005 mismatch")
        }
    }

    // =========================================
    // 0xEB6D: moveESprRowOffscreen
    // Parameters: A, X
    // 838 calls, 16 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 1159
     * Function: moveESprRowOffscreen (0xEB6D)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 9, writes: 5
     */
    @Test
    fun `moveESprRowOffscreen_frame1159_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0004] = 0x08u
            memory[0x0016] = 0x06u
            memory[0x001E] = 0x00u
            memory[0x0046] = 0x02u
            memory[0x00CF] = 0xB8u
            memory[0x00EB] = 0x02u
            memory[0x01F3] = 0x73u
            memory[0x01F4] = 0xEBu
            memory[0x01F5] = 0x01u

            // Execute decompiled function
            moveESprRowOffscreen(0x01, 0x00)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x48u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x00EB], "Memory 0x00EB mismatch")
        }
    }

    /**
     * Test case 1 from frame 1161
     * Function: moveESprRowOffscreen (0xEB6D)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 9, writes: 5
     */
    @Test
    fun `moveESprRowOffscreen_frame1161_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0004] = 0x08u
            memory[0x0016] = 0x06u
            memory[0x001E] = 0x00u
            memory[0x0046] = 0x02u
            memory[0x00CF] = 0xB8u
            memory[0x00EB] = 0x02u
            memory[0x01F3] = 0x73u
            memory[0x01F4] = 0xEBu
            memory[0x01F5] = 0x01u

            // Execute decompiled function
            moveESprRowOffscreen(0x01, 0x00)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x48u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x00EB], "Memory 0x00EB mismatch")
        }
    }

    /**
     * Test case 2 from frame 1221
     * Function: moveESprRowOffscreen (0xEB6D)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 9, writes: 5
     */
    @Test
    fun `moveESprRowOffscreen_frame1221_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0004] = 0x08u
            memory[0x0016] = 0x06u
            memory[0x001E] = 0x00u
            memory[0x0046] = 0x02u
            memory[0x00CF] = 0xB8u
            memory[0x00EB] = 0x02u
            memory[0x01F3] = 0x73u
            memory[0x01F4] = 0xEBu
            memory[0x01F5] = 0x00u

            // Execute decompiled function
            moveESprRowOffscreen(0x00, 0x00)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x48u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x00EB], "Memory 0x00EB mismatch")
        }
    }

    /**
     * Test case 3 from frame 1223
     * Function: moveESprRowOffscreen (0xEB6D)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 9, writes: 5
     */
    @Test
    fun `moveESprRowOffscreen_frame1223_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0004] = 0x08u
            memory[0x0016] = 0x06u
            memory[0x001E] = 0x00u
            memory[0x0046] = 0x02u
            memory[0x00CF] = 0xB8u
            memory[0x00EB] = 0x02u
            memory[0x01F3] = 0x73u
            memory[0x01F4] = 0xEBu
            memory[0x01F5] = 0x00u

            // Execute decompiled function
            moveESprRowOffscreen(0x00, 0x00)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x48u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x00EB], "Memory 0x00EB mismatch")
        }
    }

    /**
     * Test case 4 from frame 1233
     * Function: moveESprRowOffscreen (0xEB6D)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 8, writes: 4
     */
    @Test
    fun `moveESprRowOffscreen_frame1233_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (8 addresses)
            memory[0x0004] = 0x08u
            memory[0x0016] = 0x06u
            memory[0x001E] = 0x00u
            memory[0x0046] = 0x02u
            memory[0x00CF] = 0xB8u
            memory[0x00EB] = 0x02u
            memory[0x01F4] = 0xC9u
            memory[0x01F5] = 0x00u

            // Execute decompiled function
            moveESprRowOffscreen(0x00, 0x00)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x48u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x00EB], "Memory 0x00EB mismatch")
        }
    }

    /**
     * Test case 5 from frame 1237
     * Function: moveESprRowOffscreen (0xEB6D)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 8, writes: 4
     */
    @Test
    fun `moveESprRowOffscreen_frame1237_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (8 addresses)
            memory[0x0004] = 0x08u
            memory[0x0016] = 0x06u
            memory[0x001E] = 0x00u
            memory[0x0046] = 0x02u
            memory[0x00CF] = 0xB8u
            memory[0x00EB] = 0x02u
            memory[0x01F4] = 0xC9u
            memory[0x01F5] = 0x00u

            // Execute decompiled function
            moveESprRowOffscreen(0x00, 0x00)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x48u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x00EB], "Memory 0x00EB mismatch")
        }
    }

    /**
     * Test case 6 from frame 4287
     * Function: moveESprRowOffscreen (0xEB6D)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 5, writes: 3
     */
    @Test
    fun `moveESprRowOffscreen_frame4287_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0004] = 0x08u
            memory[0x0016] = 0x06u
            memory[0x001E] = 0x04u
            memory[0x01F4] = 0xC9u
            memory[0x01F5] = 0x00u

            // Execute decompiled function
            moveESprRowOffscreen(0x00, 0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x48u, memory[0x0000], "Memory 0x0000 mismatch")
        }
    }

    /**
     * Test case 7 from frame 4291
     * Function: moveESprRowOffscreen (0xEB6D)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 5, writes: 3
     */
    @Test
    fun `moveESprRowOffscreen_frame4291_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0004] = 0x08u
            memory[0x0016] = 0x06u
            memory[0x001E] = 0x04u
            memory[0x01F4] = 0xC9u
            memory[0x01F5] = 0x00u

            // Execute decompiled function
            moveESprRowOffscreen(0x00, 0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x48u, memory[0x0000], "Memory 0x0000 mismatch")
        }
    }

    /**
     * Test case 8 from frame 4987
     * Function: moveESprRowOffscreen (0xEB6D)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 10, writes: 7
     */
    @Test
    fun `moveESprRowOffscreen_frame4987_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (10 addresses)
            memory[0x0004] = 0x08u
            memory[0x0016] = 0x06u
            memory[0x001E] = 0x00u
            memory[0x0046] = 0x02u
            memory[0x0058] = 0xF8u
            memory[0x00CF] = 0xB8u
            memory[0x00EB] = 0x02u
            memory[0x01F3] = 0x73u
            memory[0x01F4] = 0xEBu
            memory[0x01F5] = 0x01u

            // Execute decompiled function
            moveESprRowOffscreen(0x01, 0x00)

            // Verify: Check output memory (4 addresses)
            assertEquals(0x48u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x01u, memory[0x0046], "Memory 0x0046 mismatch")
            assertEquals(0x08u, memory[0x0058], "Memory 0x0058 mismatch")
            assertEquals(0x02u, memory[0x00EB], "Memory 0x00EB mismatch")
        }
    }

    /**
     * Test case 9 from frame 4991
     * Function: moveESprRowOffscreen (0xEB6D)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 9, writes: 5
     */
    @Test
    fun `moveESprRowOffscreen_frame4991_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0004] = 0x08u
            memory[0x0016] = 0x06u
            memory[0x001E] = 0x00u
            memory[0x0046] = 0x01u
            memory[0x00CF] = 0xB8u
            memory[0x00EB] = 0x02u
            memory[0x01F3] = 0x73u
            memory[0x01F4] = 0xEBu
            memory[0x01F5] = 0x01u

            // Execute decompiled function
            moveESprRowOffscreen(0x01, 0x00)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x48u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x00EB], "Memory 0x00EB mismatch")
        }
    }

    // =========================================
    // 0xEB77: moveESprColOffscreen
    // Parameters: A, X
    // 838 calls, 9 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 1159
     * Function: moveESprColOffscreen (0xEB77)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 3, writes: 3
     */
    @Test
    fun `moveESprColOffscreen_frame1159_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01F3] = 0x7Du
            memory[0x01F4] = 0xEBu
            memory[0x01F5] = 0x00u

            // Execute decompiled function
            moveESprColOffscreen(0x00, 0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 1161
     * Function: moveESprColOffscreen (0xEB77)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 3, writes: 3
     */
    @Test
    fun `moveESprColOffscreen_frame1161_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01F3] = 0x7Du
            memory[0x01F4] = 0xEBu
            memory[0x01F5] = 0x00u

            // Execute decompiled function
            moveESprColOffscreen(0x00, 0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 2 from frame 1163
     * Function: moveESprColOffscreen (0xEB77)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 3, writes: 3
     */
    @Test
    fun `moveESprColOffscreen_frame1163_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01F3] = 0x7Du
            memory[0x01F4] = 0xEBu
            memory[0x01F5] = 0x00u

            // Execute decompiled function
            moveESprColOffscreen(0x00, 0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 3 from frame 1221
     * Function: moveESprColOffscreen (0xEB77)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `moveESprColOffscreen_frame1221_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x01F5] = 0x00u

            // Execute decompiled function
            moveESprColOffscreen(0x00, 0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 4 from frame 1223
     * Function: moveESprColOffscreen (0xEB77)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `moveESprColOffscreen_frame1223_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x01F5] = 0x00u

            // Execute decompiled function
            moveESprColOffscreen(0x00, 0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 5 from frame 1225
     * Function: moveESprColOffscreen (0xEB77)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `moveESprColOffscreen_frame1225_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x01F5] = 0x00u

            // Execute decompiled function
            moveESprColOffscreen(0x00, 0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 6 from frame 1233
     * Function: moveESprColOffscreen (0xEB77)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `moveESprColOffscreen_frame1233_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x01F5] = 0x00u

            // Execute decompiled function
            moveESprColOffscreen(0x00, 0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 7 from frame 1235
     * Function: moveESprColOffscreen (0xEB77)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `moveESprColOffscreen_frame1235_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x01F5] = 0x00u

            // Execute decompiled function
            moveESprColOffscreen(0x00, 0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 8 from frame 1237
     * Function: moveESprColOffscreen (0xEB77)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `moveESprColOffscreen_frame1237_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (1 addresses)
            memory[0x01F5] = 0x00u

            // Execute decompiled function
            moveESprColOffscreen(0x00, 0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0xEC00: moveColOffscreen
    // Parameters: Y
    // 42 calls, 9 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 4375
     * Function: moveColOffscreen (0xEC00)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 6, writes: 6
     */
    @Test
    fun `moveColOffscreen_frame4375_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (6 addresses)
            memory[0x0008] = 0x00u
            memory[0x01F3] = 0x16u
            memory[0x01F4] = 0xECu
            memory[0x03D4] = 0x00u
            memory[0x03E8] = 0xC4u
            memory[0x074E] = 0x01u

            // Execute decompiled function
            moveColOffscreen(0x58)

            // Verify: Check output memory (4 addresses)
            assertEquals(0x03u, memory[0x025A], "Memory 0x025A mismatch")
            assertEquals(0x43u, memory[0x025E], "Memory 0x025E mismatch")
            assertEquals(0x83u, memory[0x0262], "Memory 0x0262 mismatch")
            assertEquals(0xC3u, memory[0x0266], "Memory 0x0266 mismatch")
        }
    }

    /**
     * Test case 1 from frame 4377
     * Function: moveColOffscreen (0xEC00)
     * Parameters: Y
     * Call depth: 7
     * Memory reads: 6, writes: 6
     */
    @Test
    fun `moveColOffscreen_frame4377_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (6 addresses)
            memory[0x0008] = 0x00u
            memory[0x01F3] = 0x16u
            memory[0x01F4] = 0xECu
            memory[0x03D4] = 0x00u
            memory[0x03E8] = 0xC4u
            memory[0x074E] = 0x01u

            // Execute decompiled function
            moveColOffscreen(0xA0)

            // Verify: Check output memory (4 addresses)
            assertEquals(0x03u, memory[0x02A2], "Memory 0x02A2 mismatch")
            assertEquals(0x43u, memory[0x02A6], "Memory 0x02A6 mismatch")
            assertEquals(0x83u, memory[0x02AA], "Memory 0x02AA mismatch")
            assertEquals(0xC3u, memory[0x02AE], "Memory 0x02AE mismatch")
        }
    }

    /**
     * Test case 2 from frame 4379
     * Function: moveColOffscreen (0xEC00)
     * Parameters: Y
     * Call depth: 7
     * Memory reads: 6, writes: 6
     */
    @Test
    fun `moveColOffscreen_frame4379_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (6 addresses)
            memory[0x0008] = 0x00u
            memory[0x01F3] = 0x16u
            memory[0x01F4] = 0xECu
            memory[0x03D4] = 0x00u
            memory[0x03E8] = 0xC4u
            memory[0x074E] = 0x01u

            // Execute decompiled function
            moveColOffscreen(0xD8)

            // Verify: Check output memory (4 addresses)
            assertEquals(0x03u, memory[0x02DA], "Memory 0x02DA mismatch")
            assertEquals(0x43u, memory[0x02DE], "Memory 0x02DE mismatch")
            assertEquals(0x83u, memory[0x02E2], "Memory 0x02E2 mismatch")
            assertEquals(0xC3u, memory[0x02E6], "Memory 0x02E6 mismatch")
        }
    }

    /**
     * Test case 3 from frame 4551
     * Function: moveColOffscreen (0xEC00)
     * Parameters: Y
     * Call depth: 7
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `moveColOffscreen_frame4551_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01F4] = 0x00u
            memory[0x03D4] = 0x00u
            memory[0x03E9] = 0x51u

            // Execute decompiled function
            moveColOffscreen(0xB0)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 4 from frame 4553
     * Function: moveColOffscreen (0xEC00)
     * Parameters: Y
     * Call depth: 7
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `moveColOffscreen_frame4553_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01F4] = 0x00u
            memory[0x03D4] = 0x00u
            memory[0x03E9] = 0x51u

            // Execute decompiled function
            moveColOffscreen(0xE8)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 5 from frame 4555
     * Function: moveColOffscreen (0xEC00)
     * Parameters: Y
     * Call depth: 7
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `moveColOffscreen_frame4555_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01F4] = 0x00u
            memory[0x03D4] = 0x00u
            memory[0x03E9] = 0x51u

            // Execute decompiled function
            moveColOffscreen(0x68)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 6 from frame 4621
     * Function: moveColOffscreen (0xEC00)
     * Parameters: Y
     * Call depth: 7
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `moveColOffscreen_frame4621_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01F4] = 0x00u
            memory[0x03D4] = 0x00u
            memory[0x03E8] = 0x51u

            // Execute decompiled function
            moveColOffscreen(0x58)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 7 from frame 4623
     * Function: moveColOffscreen (0xEC00)
     * Parameters: Y
     * Call depth: 7
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `moveColOffscreen_frame4623_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01F4] = 0x00u
            memory[0x03D4] = 0x00u
            memory[0x03E8] = 0x51u

            // Execute decompiled function
            moveColOffscreen(0xA0)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 8 from frame 4625
     * Function: moveColOffscreen (0xEC00)
     * Parameters: Y
     * Call depth: 7
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `moveColOffscreen_frame4625_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01F4] = 0x00u
            memory[0x03D4] = 0x00u
            memory[0x03E8] = 0x51u

            // Execute decompiled function
            moveColOffscreen(0xD8)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0xEF92: drawPlayerLoop
    // Parameters: X
    // 130 calls, 4 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 1703
     * Function: drawPlayerLoop (0xEF92)
     * Parameters: X
     * Call depth: 7
     * Memory reads: 3, writes: 3
     */
    @Test
    fun `drawPlayerLoop_frame1703_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x0000] = 0x00u
            memory[0x01F6] = 0x94u
            memory[0x01F7] = 0xEFu

            // Execute decompiled function
            drawPlayerLoop(0x03)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
        }
    }

    /**
     * Test case 1 from frame 1707
     * Function: drawPlayerLoop (0xEF92)
     * Parameters: X
     * Call depth: 7
     * Memory reads: 3, writes: 3
     */
    @Test
    fun `drawPlayerLoop_frame1707_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x0000] = 0x01u
            memory[0x01F6] = 0x94u
            memory[0x01F7] = 0xEFu

            // Execute decompiled function
            drawPlayerLoop(0x03)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
        }
    }

    /**
     * Test case 2 from frame 1711
     * Function: drawPlayerLoop (0xEF92)
     * Parameters: X
     * Call depth: 7
     * Memory reads: 3, writes: 3
     */
    @Test
    fun `drawPlayerLoop_frame1711_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x0000] = 0x03u
            memory[0x01F6] = 0x94u
            memory[0x01F7] = 0xEFu

            // Execute decompiled function
            drawPlayerLoop(0x03)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
        }
    }

    /**
     * Test case 3 from frame 1715
     * Function: drawPlayerLoop (0xEF92)
     * Parameters: X
     * Call depth: 7
     * Memory reads: 3, writes: 3
     */
    @Test
    fun `drawPlayerLoop_frame1715_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x0000] = 0x07u
            memory[0x01F6] = 0x94u
            memory[0x01F7] = 0xEFu

            // Execute decompiled function
            drawPlayerLoop(0x03)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
        }
    }

    // =========================================
    // 0xF018: getCurrentAnimOffset
    // 4920 calls, 520 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 33
     * Function: getCurrentAnimOffset (0xF018)
     * Call depth: 7
     * Memory reads: 2, writes: 3
     */
    @Test
    fun `getCurrentAnimOffset_frame33_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x01F2] = 0x2Au
            memory[0x01F3] = 0xF0u

            // Execute decompiled function
            getCurrentAnimOffset()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x070D], "Memory 0x070D mismatch")
        }
    }

    /**
     * Test case 1 from frame 791
     * Function: getCurrentAnimOffset (0xF018)
     * Call depth: 7
     * Memory reads: 4, writes: 3
     */
    @Test
    fun `getCurrentAnimOffset_frame791_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x01F4] = 0x40u
            memory[0x01F5] = 0xF0u
            memory[0x0700] = 0x02u
            memory[0x0781] = 0x01u

            // Execute decompiled function
            getCurrentAnimOffset()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x03u, memory[0x0000], "Memory 0x0000 mismatch")
        }
    }

    /**
     * Test case 2 from frame 1331
     * Function: getCurrentAnimOffset (0xF018)
     * Call depth: 9
     * Memory reads: 4, writes: 3
     */
    @Test
    fun `getCurrentAnimOffset_frame1331_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x01F4] = 0x40u
            memory[0x01F5] = 0xF0u
            memory[0x0700] = 0x05u
            memory[0x0781] = 0x03u

            // Execute decompiled function
            getCurrentAnimOffset()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x03u, memory[0x0000], "Memory 0x0000 mismatch")
        }
    }

    /**
     * Test case 3 from frame 3627
     * Function: getCurrentAnimOffset (0xF018)
     * Call depth: 7
     * Memory reads: 5, writes: 0
     */
    @Test
    fun `getCurrentAnimOffset_frame3627_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x000E] = 0x08u
            memory[0x0045] = 0x01u
            memory[0x06D5] = 0x60u
            memory[0x06E4] = 0x04u
            memory[0x0700] = 0x09u

            // Execute decompiled function
            getCurrentAnimOffset()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 4 from frame 4053
     * Function: getCurrentAnimOffset (0xF018)
     * Call depth: 9
     * Memory reads: 5, writes: 0
     */
    @Test
    fun `getCurrentAnimOffset_frame4053_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x000E] = 0x08u
            memory[0x0045] = 0x01u
            memory[0x06D5] = 0x68u
            memory[0x06E4] = 0x04u
            memory[0x0700] = 0x09u

            // Execute decompiled function
            getCurrentAnimOffset()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 5 from frame 4467
     * Function: getCurrentAnimOffset (0xF018)
     * Call depth: 7
     * Memory reads: 4, writes: 3
     */
    @Test
    fun `getCurrentAnimOffset_frame4467_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x01F4] = 0x40u
            memory[0x01F5] = 0xF0u
            memory[0x0700] = 0x01u
            memory[0x0781] = 0x05u

            // Execute decompiled function
            getCurrentAnimOffset()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x03u, memory[0x0000], "Memory 0x0000 mismatch")
        }
    }

    /**
     * Test case 6 from frame 5077
     * Function: getCurrentAnimOffset (0xF018)
     * Call depth: 9
     * Memory reads: 4, writes: 3
     */
    @Test
    fun `getCurrentAnimOffset_frame5077_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x01F4] = 0x40u
            memory[0x01F5] = 0xF0u
            memory[0x0700] = 0x06u
            memory[0x0781] = 0x02u

            // Execute decompiled function
            getCurrentAnimOffset()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x03u, memory[0x0000], "Memory 0x0000 mismatch")
        }
    }

    /**
     * Test case 7 from frame 7655
     * Function: getCurrentAnimOffset (0xF018)
     * Call depth: 7
     * Memory reads: 4, writes: 3
     */
    @Test
    fun `getCurrentAnimOffset_frame7655_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x01F4] = 0x40u
            memory[0x01F5] = 0xF0u
            memory[0x0700] = 0x02u
            memory[0x0781] = 0x01u

            // Execute decompiled function
            getCurrentAnimOffset()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x03u, memory[0x0000], "Memory 0x0000 mismatch")
        }
    }

    /**
     * Test case 8 from frame 9205
     * Function: getCurrentAnimOffset (0xF018)
     * Call depth: 7
     * Memory reads: 13, writes: 10
     */
    @Test
    fun `getCurrentAnimOffset_frame9205_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (13 addresses)
            memory[0x0007] = 0x04u
            memory[0x0033] = 0x02u
            memory[0x01F4] = 0x40u
            memory[0x01F5] = 0xF0u
            memory[0x03AD] = 0x4Du
            memory[0x03B8] = 0xB0u
            memory[0x03C4] = 0x00u
            memory[0x06D5] = 0x60u
            memory[0x06E4] = 0x04u
            memory[0x0700] = 0x01u
            memory[0x070C] = 0x07u
            memory[0x070D] = 0x00u
            memory[0x0781] = 0x00u

            // Execute decompiled function
            getCurrentAnimOffset()

            // Verify: Check output memory (8 addresses)
            assertEquals(0x34u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xB0u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x02u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x4Du, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x4Du, memory[0x0755], "Memory 0x0755 mismatch")
            assertEquals(0x07u, memory[0x0781], "Memory 0x0781 mismatch")
        }
    }

    /**
     * Test case 9 from frame 16521
     * Function: getCurrentAnimOffset (0xF018)
     * Call depth: 7
     * Memory reads: 2, writes: 0
     */
    @Test
    fun `getCurrentAnimOffset_frame16521_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0045] = 0x02u
            memory[0x0700] = 0x0Eu

            // Execute decompiled function
            getCurrentAnimOffset()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0xF023: threeFrameExtent
    // 308 calls, 83 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 745
     * Function: threeFrameExtent (0xF023)
     * Call depth: 7
     * Memory reads: 4, writes: 0
     */
    @Test
    fun `threeFrameExtent_frame745_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x000E] = 0x08u
            memory[0x0033] = 0x03u
            memory[0x06D5] = 0x60u
            memory[0x06E4] = 0x04u

            // Execute decompiled function
            threeFrameExtent()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 1077
     * Function: threeFrameExtent (0xF023)
     * Call depth: 7
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `threeFrameExtent_frame1077_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0007] = 0x04u
            memory[0x0033] = 0x01u
            memory[0x01F4] = 0xE6u
            memory[0x01F5] = 0xEFu
            memory[0x03AD] = 0x50u
            memory[0x03B8] = 0xB0u
            memory[0x03C4] = 0x00u
            memory[0x06D5] = 0x70u
            memory[0x06E4] = 0x04u

            // Execute decompiled function
            threeFrameExtent()

            // Verify: Check output memory (7 addresses)
            assertEquals(0x3Bu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xB0u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x01u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x50u, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x50u, memory[0x0755], "Memory 0x0755 mismatch")
        }
    }

    /**
     * Test case 2 from frame 1205
     * Function: threeFrameExtent (0xF023)
     * Call depth: 9
     * Memory reads: 4, writes: 0
     */
    @Test
    fun `threeFrameExtent_frame1205_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x000E] = 0x08u
            memory[0x0033] = 0x01u
            memory[0x06D5] = 0x68u
            memory[0x06E4] = 0x04u

            // Execute decompiled function
            threeFrameExtent()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 3 from frame 3335
     * Function: threeFrameExtent (0xF023)
     * Call depth: 7
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `threeFrameExtent_frame3335_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0007] = 0x04u
            memory[0x0033] = 0x01u
            memory[0x01F4] = 0xE6u
            memory[0x01F5] = 0xEFu
            memory[0x03AD] = 0x43u
            memory[0x03B8] = 0xB0u
            memory[0x03C4] = 0x00u
            memory[0x06D5] = 0x70u
            memory[0x06E4] = 0x04u

            // Execute decompiled function
            threeFrameExtent()

            // Verify: Check output memory (7 addresses)
            assertEquals(0x3Bu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xB0u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x01u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x43u, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x43u, memory[0x0755], "Memory 0x0755 mismatch")
        }
    }

    /**
     * Test case 4 from frame 3673
     * Function: threeFrameExtent (0xF023)
     * Call depth: 7
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `threeFrameExtent_frame3673_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0007] = 0x04u
            memory[0x0033] = 0x01u
            memory[0x01F4] = 0xE6u
            memory[0x01F5] = 0xEFu
            memory[0x03AD] = 0x57u
            memory[0x03B8] = 0xB0u
            memory[0x03C4] = 0x00u
            memory[0x06D5] = 0x70u
            memory[0x06E4] = 0x04u

            // Execute decompiled function
            threeFrameExtent()

            // Verify: Check output memory (7 addresses)
            assertEquals(0x3Bu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xB0u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x01u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x57u, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x57u, memory[0x0755], "Memory 0x0755 mismatch")
        }
    }

    /**
     * Test case 5 from frame 4343
     * Function: threeFrameExtent (0xF023)
     * Call depth: 9
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `threeFrameExtent_frame4343_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0007] = 0x04u
            memory[0x0033] = 0x01u
            memory[0x01F4] = 0xE6u
            memory[0x01F5] = 0xEFu
            memory[0x03AD] = 0x6Fu
            memory[0x03B8] = 0xB0u
            memory[0x03C4] = 0x00u
            memory[0x06D5] = 0x70u
            memory[0x06E4] = 0x04u

            // Execute decompiled function
            threeFrameExtent()

            // Verify: Check output memory (7 addresses)
            assertEquals(0x3Bu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xB0u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x01u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x6Fu, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x6Fu, memory[0x0755], "Memory 0x0755 mismatch")
        }
    }

    /**
     * Test case 6 from frame 4971
     * Function: threeFrameExtent (0xF023)
     * Call depth: 7
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `threeFrameExtent_frame4971_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0007] = 0x04u
            memory[0x0033] = 0x01u
            memory[0x01F4] = 0xE6u
            memory[0x01F5] = 0xEFu
            memory[0x03AD] = 0x70u
            memory[0x03B8] = 0x90u
            memory[0x03C4] = 0x00u
            memory[0x06D5] = 0x60u
            memory[0x06E4] = 0x04u

            // Execute decompiled function
            threeFrameExtent()

            // Verify: Check output memory (7 addresses)
            assertEquals(0x34u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x90u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x01u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x70u, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x70u, memory[0x0755], "Memory 0x0755 mismatch")
        }
    }

    /**
     * Test case 7 from frame 16533
     * Function: threeFrameExtent (0xF023)
     * Call depth: 7
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `threeFrameExtent_frame16533_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0007] = 0x04u
            memory[0x0033] = 0x02u
            memory[0x01F4] = 0xE6u
            memory[0x01F5] = 0xEFu
            memory[0x03AD] = 0x43u
            memory[0x03B8] = 0xB0u
            memory[0x03C4] = 0x00u
            memory[0x06D5] = 0x68u
            memory[0x06E4] = 0x04u

            // Execute decompiled function
            threeFrameExtent()

            // Verify: Check output memory (7 addresses)
            assertEquals(0x38u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xB0u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x02u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x43u, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x43u, memory[0x0755], "Memory 0x0755 mismatch")
        }
    }

    /**
     * Test case 8 from frame 16603
     * Function: threeFrameExtent (0xF023)
     * Call depth: 7
     * Memory reads: 4, writes: 0
     */
    @Test
    fun `threeFrameExtent_frame16603_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x000E] = 0x08u
            memory[0x0033] = 0x02u
            memory[0x06D5] = 0x70u
            memory[0x06E4] = 0x04u

            // Execute decompiled function
            threeFrameExtent()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 9 from frame 16627
     * Function: threeFrameExtent (0xF023)
     * Call depth: 7
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `threeFrameExtent_frame16627_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0007] = 0x04u
            memory[0x0033] = 0x02u
            memory[0x01F4] = 0xE6u
            memory[0x01F5] = 0xEFu
            memory[0x03AD] = 0x0Du
            memory[0x03B8] = 0xB0u
            memory[0x03C4] = 0x00u
            memory[0x06D5] = 0x70u
            memory[0x06E4] = 0x04u

            // Execute decompiled function
            threeFrameExtent()

            // Verify: Check output memory (7 addresses)
            assertEquals(0x3Bu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xB0u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x02u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x0Du, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x0Du, memory[0x0755], "Memory 0x0755 mismatch")
        }
    }

    // =========================================
    // 0xF025: animationControl
    // Parameters: A
    // 308 calls, 68 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 745
     * Function: animationControl (0xF025)
     * Parameters: A
     * Call depth: 7
     * Memory reads: 12, writes: 10
     */
    @Test
    fun `animationControl_frame745_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (12 addresses)
            memory[0x0007] = 0x04u
            memory[0x0033] = 0x03u
            memory[0x01F4] = 0x40u
            memory[0x01F5] = 0xF0u
            memory[0x03AD] = 0x49u
            memory[0x03B8] = 0xB0u
            memory[0x03C4] = 0x00u
            memory[0x06D5] = 0x60u
            memory[0x06E4] = 0x04u
            memory[0x070C] = 0x07u
            memory[0x070D] = 0x00u
            memory[0x0781] = 0x00u

            // Execute decompiled function
            animationControl(0x01)

            // Verify: Check output memory (8 addresses)
            assertEquals(0x34u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xB0u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x03u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x49u, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x49u, memory[0x0755], "Memory 0x0755 mismatch")
            assertEquals(0x07u, memory[0x0781], "Memory 0x0781 mismatch")
        }
    }

    /**
     * Test case 1 from frame 757
     * Function: animationControl (0xF025)
     * Parameters: A
     * Call depth: 7
     * Memory reads: 2, writes: 3
     */
    @Test
    fun `animationControl_frame757_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x01F4] = 0x2Au
            memory[0x01F5] = 0xF0u

            // Execute decompiled function
            animationControl(0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x070D], "Memory 0x070D mismatch")
        }
    }

    /**
     * Test case 2 from frame 1199
     * Function: animationControl (0xF025)
     * Parameters: A
     * Call depth: 9
     * Memory reads: 3, writes: 3
     */
    @Test
    fun `animationControl_frame1199_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01F4] = 0x40u
            memory[0x01F5] = 0xF0u
            memory[0x0781] = 0x03u

            // Execute decompiled function
            animationControl(0x01)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x03u, memory[0x0000], "Memory 0x0000 mismatch")
        }
    }

    /**
     * Test case 3 from frame 1285
     * Function: animationControl (0xF025)
     * Parameters: A
     * Call depth: 9
     * Memory reads: 12, writes: 10
     */
    @Test
    fun `animationControl_frame1285_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (12 addresses)
            memory[0x0007] = 0x04u
            memory[0x0033] = 0x03u
            memory[0x01F4] = 0x40u
            memory[0x01F5] = 0xF0u
            memory[0x03AD] = 0x70u
            memory[0x03B8] = 0xB0u
            memory[0x03C4] = 0x00u
            memory[0x06D5] = 0x60u
            memory[0x06E4] = 0x04u
            memory[0x070C] = 0x04u
            memory[0x070D] = 0x00u
            memory[0x0781] = 0x00u

            // Execute decompiled function
            animationControl(0x01)

            // Verify: Check output memory (8 addresses)
            assertEquals(0x34u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xB0u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x03u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x70u, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x70u, memory[0x0755], "Memory 0x0755 mismatch")
            assertEquals(0x04u, memory[0x0781], "Memory 0x0781 mismatch")
        }
    }

    /**
     * Test case 4 from frame 3675
     * Function: animationControl (0xF025)
     * Parameters: A
     * Call depth: 7
     * Memory reads: 12, writes: 10
     */
    @Test
    fun `animationControl_frame3675_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (12 addresses)
            memory[0x0007] = 0x04u
            memory[0x0033] = 0x01u
            memory[0x01F4] = 0x40u
            memory[0x01F5] = 0xF0u
            memory[0x03AD] = 0x58u
            memory[0x03B8] = 0xB0u
            memory[0x03C4] = 0x00u
            memory[0x06D5] = 0x70u
            memory[0x06E4] = 0x04u
            memory[0x070C] = 0x02u
            memory[0x070D] = 0x02u
            memory[0x0781] = 0x00u

            // Execute decompiled function
            animationControl(0x01)

            // Verify: Check output memory (8 addresses)
            assertEquals(0x3Bu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xB0u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x01u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x58u, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x58u, memory[0x0755], "Memory 0x0755 mismatch")
            assertEquals(0x02u, memory[0x0781], "Memory 0x0781 mismatch")
        }
    }

    /**
     * Test case 5 from frame 4331
     * Function: animationControl (0xF025)
     * Parameters: A
     * Call depth: 9
     * Memory reads: 3, writes: 3
     */
    @Test
    fun `animationControl_frame4331_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01F4] = 0x40u
            memory[0x01F5] = 0xF0u
            memory[0x0781] = 0x04u

            // Execute decompiled function
            animationControl(0x01)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x03u, memory[0x0000], "Memory 0x0000 mismatch")
        }
    }

    /**
     * Test case 6 from frame 4425
     * Function: animationControl (0xF025)
     * Parameters: A
     * Call depth: 7
     * Memory reads: 12, writes: 10
     */
    @Test
    fun `animationControl_frame4425_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (12 addresses)
            memory[0x0007] = 0x04u
            memory[0x0033] = 0x01u
            memory[0x01F4] = 0x40u
            memory[0x01F5] = 0xF0u
            memory[0x03AD] = 0x70u
            memory[0x03B8] = 0xB0u
            memory[0x03C4] = 0x00u
            memory[0x06D5] = 0x70u
            memory[0x06E4] = 0x04u
            memory[0x070C] = 0x04u
            memory[0x070D] = 0x02u
            memory[0x0781] = 0x00u

            // Execute decompiled function
            animationControl(0x01)

            // Verify: Check output memory (8 addresses)
            assertEquals(0x3Bu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xB0u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x01u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x70u, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x70u, memory[0x0755], "Memory 0x0755 mismatch")
            assertEquals(0x04u, memory[0x0781], "Memory 0x0781 mismatch")
        }
    }

    /**
     * Test case 7 from frame 4961
     * Function: animationControl (0xF025)
     * Parameters: A
     * Call depth: 7
     * Memory reads: 12, writes: 10
     */
    @Test
    fun `animationControl_frame4961_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (12 addresses)
            memory[0x0007] = 0x04u
            memory[0x0033] = 0x01u
            memory[0x01F4] = 0x40u
            memory[0x01F5] = 0xF0u
            memory[0x03AD] = 0x70u
            memory[0x03B8] = 0x90u
            memory[0x03C4] = 0x00u
            memory[0x06D5] = 0x68u
            memory[0x06E4] = 0x04u
            memory[0x070C] = 0x04u
            memory[0x070D] = 0x01u
            memory[0x0781] = 0x00u

            // Execute decompiled function
            animationControl(0x01)

            // Verify: Check output memory (8 addresses)
            assertEquals(0x38u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x90u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x01u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x70u, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x70u, memory[0x0755], "Memory 0x0755 mismatch")
            assertEquals(0x04u, memory[0x0781], "Memory 0x0781 mismatch")
        }
    }

    /**
     * Test case 8 from frame 16521
     * Function: animationControl (0xF025)
     * Parameters: A
     * Call depth: 7
     * Memory reads: 12, writes: 10
     */
    @Test
    fun `animationControl_frame16521_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (12 addresses)
            memory[0x0007] = 0x04u
            memory[0x0033] = 0x02u
            memory[0x01F4] = 0x40u
            memory[0x01F5] = 0xF0u
            memory[0x03AD] = 0x49u
            memory[0x03B8] = 0xB0u
            memory[0x03C4] = 0x00u
            memory[0x06D5] = 0x60u
            memory[0x06E4] = 0x04u
            memory[0x070C] = 0x07u
            memory[0x070D] = 0x00u
            memory[0x0781] = 0x00u

            // Execute decompiled function
            animationControl(0x02)

            // Verify: Check output memory (8 addresses)
            assertEquals(0x34u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xB0u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x02u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x49u, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x49u, memory[0x0755], "Memory 0x0755 mismatch")
            assertEquals(0x07u, memory[0x0781], "Memory 0x0781 mismatch")
        }
    }

    /**
     * Test case 9 from frame 16587
     * Function: animationControl (0xF025)
     * Parameters: A
     * Call depth: 7
     * Memory reads: 12, writes: 10
     */
    @Test
    fun `animationControl_frame16587_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (12 addresses)
            memory[0x0007] = 0x04u
            memory[0x0033] = 0x02u
            memory[0x01F4] = 0x40u
            memory[0x01F5] = 0xF0u
            memory[0x03AD] = 0x20u
            memory[0x03B8] = 0xB0u
            memory[0x03C4] = 0x00u
            memory[0x06D5] = 0x60u
            memory[0x06E4] = 0x04u
            memory[0x070C] = 0x04u
            memory[0x070D] = 0x00u
            memory[0x0781] = 0x00u

            // Execute decompiled function
            animationControl(0x02)

            // Verify: Check output memory (8 addresses)
            assertEquals(0x34u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xB0u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x02u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x20u, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
            assertEquals(0x20u, memory[0x0755], "Memory 0x0755 mismatch")
            assertEquals(0x04u, memory[0x0781], "Memory 0x0781 mismatch")
        }
    }

    // =========================================
    // 0xF086: getOffsetFromAnimCtrl
    // Parameters: A, Y
    // 262 calls, 3 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 201
     * Function: getOffsetFromAnimCtrl (0xF086)
     * Parameters: A, Y
     * Call depth: 7
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `getOffsetFromAnimCtrl_frame201_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0000] = 0x03u
            memory[0x01F5] = 0x60u

            // Execute decompiled function
            getOffsetFromAnimCtrl(0x01, 0x0C)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x01u, memory[0x070D], "Memory 0x070D mismatch")
        }
    }

    /**
     * Test case 1 from frame 215
     * Function: getOffsetFromAnimCtrl (0xF086)
     * Parameters: A, Y
     * Call depth: 7
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `getOffsetFromAnimCtrl_frame215_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0000] = 0x03u
            memory[0x01F5] = 0x68u

            // Execute decompiled function
            getOffsetFromAnimCtrl(0x02, 0x0C)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x02u, memory[0x070D], "Memory 0x070D mismatch")
        }
    }

    /**
     * Test case 2 from frame 687
     * Function: getOffsetFromAnimCtrl (0xF086)
     * Parameters: A, Y
     * Call depth: 7
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `getOffsetFromAnimCtrl_frame687_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0000] = 0x03u
            memory[0x01F5] = 0x70u

            // Execute decompiled function
            getOffsetFromAnimCtrl(0x03, 0x0C)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x00u, memory[0x070D], "Memory 0x070D mismatch")
        }
    }

    // =========================================
    // 0xF108: relativeEnemyPosition
    // 208 calls, 4 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 1545
     * Function: relativeEnemyPosition (0xF108)
     * Call depth: 8
     * Memory reads: 3, writes: 4
     */
    @Test
    fun `relativeEnemyPosition_frame1545_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x021A] = 0x00u
            memory[0x021E] = 0x00u
            memory[0x0222] = 0x00u

            // Execute decompiled function
            relativeEnemyPosition()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x00u, memory[0x0216], "Memory 0x0216 mismatch")
            assertEquals(0x40u, memory[0x021A], "Memory 0x021A mismatch")
            assertEquals(0x00u, memory[0x021E], "Memory 0x021E mismatch")
            assertEquals(0x40u, memory[0x0222], "Memory 0x0222 mismatch")
        }
    }

    /**
     * Test case 1 from frame 1721
     * Function: relativeEnemyPosition (0xF108)
     * Call depth: 8
     * Memory reads: 3, writes: 4
     */
    @Test
    fun `relativeEnemyPosition_frame1721_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x021A] = 0x02u
            memory[0x021E] = 0x02u
            memory[0x0222] = 0x02u

            // Execute decompiled function
            relativeEnemyPosition()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x02u, memory[0x0216], "Memory 0x0216 mismatch")
            assertEquals(0x42u, memory[0x021A], "Memory 0x021A mismatch")
            assertEquals(0x02u, memory[0x021E], "Memory 0x021E mismatch")
            assertEquals(0x42u, memory[0x0222], "Memory 0x0222 mismatch")
        }
    }

    /**
     * Test case 2 from frame 1723
     * Function: relativeEnemyPosition (0xF108)
     * Call depth: 8
     * Memory reads: 3, writes: 4
     */
    @Test
    fun `relativeEnemyPosition_frame1723_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x021A] = 0x03u
            memory[0x021E] = 0x03u
            memory[0x0222] = 0x03u

            // Execute decompiled function
            relativeEnemyPosition()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x03u, memory[0x0216], "Memory 0x0216 mismatch")
            assertEquals(0x43u, memory[0x021A], "Memory 0x021A mismatch")
            assertEquals(0x03u, memory[0x021E], "Memory 0x021E mismatch")
            assertEquals(0x43u, memory[0x0222], "Memory 0x0222 mismatch")
        }
    }

    /**
     * Test case 3 from frame 1755
     * Function: relativeEnemyPosition (0xF108)
     * Call depth: 8
     * Memory reads: 3, writes: 4
     */
    @Test
    fun `relativeEnemyPosition_frame1755_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x021A] = 0x01u
            memory[0x021E] = 0x01u
            memory[0x0222] = 0x01u

            // Execute decompiled function
            relativeEnemyPosition()

            // Verify: Check output memory (4 addresses)
            assertEquals(0x01u, memory[0x0216], "Memory 0x0216 mismatch")
            assertEquals(0x41u, memory[0x021A], "Memory 0x021A mismatch")
            assertEquals(0x01u, memory[0x021E], "Memory 0x021E mismatch")
            assertEquals(0x41u, memory[0x0222], "Memory 0x0222 mismatch")
        }
    }

    // =========================================
    // 0xF147: getBubbleOffscreenBits
    // 17563 calls, 327 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 33
     * Function: getBubbleOffscreenBits (0xF147)
     * Call depth: 6
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `getBubbleOffscreenBits_frame33_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getBubbleOffscreenBits()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 710
     * Function: getBubbleOffscreenBits (0xF147)
     * Call depth: 7
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `getBubbleOffscreenBits_frame710_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getBubbleOffscreenBits()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 2 from frame 747
     * Function: getBubbleOffscreenBits (0xF147)
     * Call depth: 6
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `getBubbleOffscreenBits_frame747_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getBubbleOffscreenBits()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 3 from frame 1158
     * Function: getBubbleOffscreenBits (0xF147)
     * Call depth: 7
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `getBubbleOffscreenBits_frame1158_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getBubbleOffscreenBits()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 4 from frame 1224
     * Function: getBubbleOffscreenBits (0xF147)
     * Call depth: 7
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `getBubbleOffscreenBits_frame1224_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getBubbleOffscreenBits()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 5 from frame 3677
     * Function: getBubbleOffscreenBits (0xF147)
     * Call depth: 6
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `getBubbleOffscreenBits_frame3677_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getBubbleOffscreenBits()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 6 from frame 4425
     * Function: getBubbleOffscreenBits (0xF147)
     * Call depth: 7
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `getBubbleOffscreenBits_frame4425_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getBubbleOffscreenBits()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 7 from frame 7688
     * Function: getBubbleOffscreenBits (0xF147)
     * Call depth: 7
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `getBubbleOffscreenBits_frame7688_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getBubbleOffscreenBits()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 8 from frame 8407
     * Function: getBubbleOffscreenBits (0xF147)
     * Call depth: 6
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `getBubbleOffscreenBits_frame8407_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getBubbleOffscreenBits()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 9 from frame 16605
     * Function: getBubbleOffscreenBits (0xF147)
     * Call depth: 6
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `getBubbleOffscreenBits_frame16605_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            getBubbleOffscreenBits()

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0xF165: getEnemyOffscreenBits
    // 922 calls, 884 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 1159
     * Function: getEnemyOffscreenBits (0xF165)
     * Call depth: 8
     * Memory reads: 24, writes: 12
     */
    @Test
    fun `getEnemyOffscreenBits_frame1159_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (24 addresses)
            memory[0x0000] = 0x00u
            memory[0x0004] = 0x00u
            memory[0x0008] = 0x00u
            memory[0x0009] = 0x7Fu
            memory[0x0016] = 0x06u
            memory[0x001E] = 0x00u
            memory[0x0046] = 0x02u
            memory[0x00CF] = 0xB8u
            memory[0x00EB] = 0xD0u
            memory[0x00EC] = 0x00u
            memory[0x00ED] = 0x00u
            memory[0x00EF] = 0x06u
            memory[0x0109] = 0x00u
            memory[0x01F4] = 0x6Du
            memory[0x01F5] = 0xF1u
            memory[0x036A] = 0x00u
            memory[0x03AE] = 0x2Eu
            memory[0x03C5] = 0x00u
            memory[0x03D1] = 0x0Fu
            memory[0x03D8] = 0x08u
            memory[0x06E5] = 0xD0u
            memory[0x0747] = 0x00u
            memory[0x074E] = 0x01u
            memory[0x0796] = 0x00u

            // Execute decompiled function
            getEnemyOffscreenBits()

            // Verify: Check output memory (9 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xB8u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x02u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x03u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x2Eu, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0xD0u, memory[0x00EB], "Memory 0x00EB mismatch")
            assertEquals(0x00u, memory[0x00EC], "Memory 0x00EC mismatch")
            assertEquals(0x00u, memory[0x00ED], "Memory 0x00ED mismatch")
            assertEquals(0x06u, memory[0x00EF], "Memory 0x00EF mismatch")
        }
    }

    /**
     * Test case 1 from frame 1335
     * Function: getEnemyOffscreenBits (0xF165)
     * Call depth: 8
     * Memory reads: 25, writes: 12
     */
    @Test
    fun `getEnemyOffscreenBits_frame1335_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (25 addresses)
            memory[0x0000] = 0x00u
            memory[0x0003] = 0x02u
            memory[0x0004] = 0x00u
            memory[0x0008] = 0x00u
            memory[0x0009] = 0xD7u
            memory[0x0016] = 0x06u
            memory[0x001E] = 0x00u
            memory[0x0046] = 0x02u
            memory[0x00CF] = 0xB8u
            memory[0x00EB] = 0x30u
            memory[0x00EC] = 0x00u
            memory[0x00ED] = 0x00u
            memory[0x00EF] = 0x06u
            memory[0x0109] = 0x00u
            memory[0x01F4] = 0x6Du
            memory[0x01F5] = 0xF1u
            memory[0x036A] = 0x00u
            memory[0x03AE] = 0xAFu
            memory[0x03C5] = 0x00u
            memory[0x03D1] = 0x00u
            memory[0x03D8] = 0x00u
            memory[0x06E5] = 0x30u
            memory[0x0747] = 0x00u
            memory[0x074E] = 0x01u
            memory[0x0796] = 0x00u

            // Execute decompiled function
            getEnemyOffscreenBits()

            // Verify: Check output memory (9 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xB8u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x01u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x03u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0xAFu, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0x30u, memory[0x00EB], "Memory 0x00EB mismatch")
            assertEquals(0x00u, memory[0x00EC], "Memory 0x00EC mismatch")
            assertEquals(0x00u, memory[0x00ED], "Memory 0x00ED mismatch")
            assertEquals(0x06u, memory[0x00EF], "Memory 0x00EF mismatch")
        }
    }

    /**
     * Test case 2 from frame 1511
     * Function: getEnemyOffscreenBits (0xF165)
     * Call depth: 8
     * Memory reads: 24, writes: 12
     */
    @Test
    fun `getEnemyOffscreenBits_frame1511_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (24 addresses)
            memory[0x0000] = 0x00u
            memory[0x0004] = 0x00u
            memory[0x0008] = 0x00u
            memory[0x0009] = 0x2Fu
            memory[0x0016] = 0x06u
            memory[0x001E] = 0x00u
            memory[0x0046] = 0x02u
            memory[0x00CF] = 0xB8u
            memory[0x00EB] = 0x88u
            memory[0x00EC] = 0x00u
            memory[0x00ED] = 0x00u
            memory[0x00EF] = 0x06u
            memory[0x0109] = 0x00u
            memory[0x01F4] = 0x6Du
            memory[0x01F5] = 0xF1u
            memory[0x036A] = 0x00u
            memory[0x03AE] = 0x83u
            memory[0x03C5] = 0x00u
            memory[0x03D1] = 0x00u
            memory[0x03D8] = 0x00u
            memory[0x06E5] = 0x88u
            memory[0x0747] = 0x00u
            memory[0x074E] = 0x01u
            memory[0x0796] = 0x00u

            // Execute decompiled function
            getEnemyOffscreenBits()

            // Verify: Check output memory (9 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xB8u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x02u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x03u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x83u, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0x88u, memory[0x00EB], "Memory 0x00EB mismatch")
            assertEquals(0x00u, memory[0x00EC], "Memory 0x00EC mismatch")
            assertEquals(0x00u, memory[0x00ED], "Memory 0x00ED mismatch")
            assertEquals(0x06u, memory[0x00EF], "Memory 0x00EF mismatch")
        }
    }

    /**
     * Test case 3 from frame 1688
     * Function: getEnemyOffscreenBits (0xF165)
     * Call depth: 8
     * Memory reads: 22, writes: 12
     */
    @Test
    fun `getEnemyOffscreenBits_frame1688_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (22 addresses)
            memory[0x0000] = 0x00u
            memory[0x0004] = 0x00u
            memory[0x0008] = 0x00u
            memory[0x0009] = 0x88u
            memory[0x0016] = 0x06u
            memory[0x001E] = 0x00u
            memory[0x0046] = 0x02u
            memory[0x00CF] = 0xB8u
            memory[0x00EB] = 0x30u
            memory[0x00EC] = 0x00u
            memory[0x00ED] = 0x00u
            memory[0x00EF] = 0x06u
            memory[0x0109] = 0x00u
            memory[0x01F4] = 0x6Du
            memory[0x01F5] = 0xF1u
            memory[0x036A] = 0x00u
            memory[0x03AE] = 0x7Au
            memory[0x03C5] = 0x00u
            memory[0x03D1] = 0x00u
            memory[0x06E5] = 0x30u
            memory[0x0747] = 0xB7u
            memory[0x0796] = 0x00u

            // Execute decompiled function
            getEnemyOffscreenBits()

            // Verify: Check output memory (9 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xB8u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x02u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x03u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x7Au, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0x30u, memory[0x00EB], "Memory 0x00EB mismatch")
            assertEquals(0x00u, memory[0x00EC], "Memory 0x00EC mismatch")
            assertEquals(0x00u, memory[0x00ED], "Memory 0x00ED mismatch")
            assertEquals(0x06u, memory[0x00EF], "Memory 0x00EF mismatch")
        }
    }

    /**
     * Test case 4 from frame 1864
     * Function: getEnemyOffscreenBits (0xF165)
     * Call depth: 8
     * Memory reads: 22, writes: 12
     */
    @Test
    fun `getEnemyOffscreenBits_frame1864_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (22 addresses)
            memory[0x0000] = 0x00u
            memory[0x0004] = 0x00u
            memory[0x0008] = 0x00u
            memory[0x0009] = 0xE0u
            memory[0x0016] = 0x06u
            memory[0x001E] = 0x00u
            memory[0x0046] = 0x02u
            memory[0x00CF] = 0xB8u
            memory[0x00EB] = 0x88u
            memory[0x00EC] = 0x00u
            memory[0x00ED] = 0x00u
            memory[0x00EF] = 0x06u
            memory[0x0109] = 0x00u
            memory[0x01F4] = 0x6Du
            memory[0x01F5] = 0xF1u
            memory[0x036A] = 0x00u
            memory[0x03AE] = 0x7Au
            memory[0x03C5] = 0x00u
            memory[0x03D1] = 0x00u
            memory[0x06E5] = 0x88u
            memory[0x0747] = 0x5Fu
            memory[0x0796] = 0x00u

            // Execute decompiled function
            getEnemyOffscreenBits()

            // Verify: Check output memory (9 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xB8u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x02u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x03u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x7Au, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0x88u, memory[0x00EB], "Memory 0x00EB mismatch")
            assertEquals(0x00u, memory[0x00EC], "Memory 0x00EC mismatch")
            assertEquals(0x00u, memory[0x00ED], "Memory 0x00ED mismatch")
            assertEquals(0x06u, memory[0x00EF], "Memory 0x00EF mismatch")
        }
    }

    /**
     * Test case 5 from frame 3785
     * Function: getEnemyOffscreenBits (0xF165)
     * Call depth: 8
     * Memory reads: 23, writes: 12
     */
    @Test
    fun `getEnemyOffscreenBits_frame3785_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (23 addresses)
            memory[0x0000] = 0x00u
            memory[0x0003] = 0x02u
            memory[0x0004] = 0x00u
            memory[0x0008] = 0x00u
            memory[0x0009] = 0xE0u
            memory[0x0016] = 0x06u
            memory[0x001E] = 0x00u
            memory[0x0046] = 0x02u
            memory[0x00CF] = 0xB8u
            memory[0x00EB] = 0x30u
            memory[0x00EC] = 0x00u
            memory[0x00ED] = 0x00u
            memory[0x00EF] = 0x06u
            memory[0x0109] = 0x00u
            memory[0x01F4] = 0x6Du
            memory[0x01F5] = 0xF1u
            memory[0x036A] = 0x00u
            memory[0x03AE] = 0x06u
            memory[0x03C5] = 0x00u
            memory[0x03D1] = 0x0Fu
            memory[0x06E5] = 0x30u
            memory[0x0747] = 0x00u
            memory[0x0796] = 0x00u

            // Execute decompiled function
            getEnemyOffscreenBits()

            // Verify: Check output memory (9 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xB8u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x01u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x03u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x06u, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0x30u, memory[0x00EB], "Memory 0x00EB mismatch")
            assertEquals(0x00u, memory[0x00EC], "Memory 0x00EC mismatch")
            assertEquals(0x00u, memory[0x00ED], "Memory 0x00ED mismatch")
            assertEquals(0x06u, memory[0x00EF], "Memory 0x00EF mismatch")
        }
    }

    /**
     * Test case 6 from frame 3961
     * Function: getEnemyOffscreenBits (0xF165)
     * Call depth: 8
     * Memory reads: 22, writes: 12
     */
    @Test
    fun `getEnemyOffscreenBits_frame3961_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (22 addresses)
            memory[0x0000] = 0x00u
            memory[0x0004] = 0x00u
            memory[0x0008] = 0x00u
            memory[0x0009] = 0x38u
            memory[0x0016] = 0x06u
            memory[0x001E] = 0x00u
            memory[0x0046] = 0x02u
            memory[0x00CF] = 0xB8u
            memory[0x00EB] = 0x88u
            memory[0x00EC] = 0x00u
            memory[0x00ED] = 0x00u
            memory[0x00EF] = 0x06u
            memory[0x0109] = 0x00u
            memory[0x01F4] = 0x6Du
            memory[0x01F5] = 0xF1u
            memory[0x036A] = 0x00u
            memory[0x03AE] = 0xDAu
            memory[0x03C5] = 0x00u
            memory[0x03D1] = 0x00u
            memory[0x06E5] = 0x88u
            memory[0x0747] = 0x00u
            memory[0x0796] = 0x00u

            // Execute decompiled function
            getEnemyOffscreenBits()

            // Verify: Check output memory (9 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xB8u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x02u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x03u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0xDAu, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0x88u, memory[0x00EB], "Memory 0x00EB mismatch")
            assertEquals(0x00u, memory[0x00EC], "Memory 0x00EC mismatch")
            assertEquals(0x00u, memory[0x00ED], "Memory 0x00ED mismatch")
            assertEquals(0x06u, memory[0x00EF], "Memory 0x00EF mismatch")
        }
    }

    /**
     * Test case 7 from frame 4137
     * Function: getEnemyOffscreenBits (0xF165)
     * Call depth: 8
     * Memory reads: 23, writes: 12
     */
    @Test
    fun `getEnemyOffscreenBits_frame4137_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (23 addresses)
            memory[0x0000] = 0x00u
            memory[0x0003] = 0x02u
            memory[0x0004] = 0x00u
            memory[0x0008] = 0x00u
            memory[0x0009] = 0x90u
            memory[0x0016] = 0x06u
            memory[0x001E] = 0x00u
            memory[0x0046] = 0x02u
            memory[0x00CF] = 0xB8u
            memory[0x00EB] = 0xD0u
            memory[0x00EC] = 0x00u
            memory[0x00ED] = 0x00u
            memory[0x00EF] = 0x06u
            memory[0x0109] = 0x00u
            memory[0x01F4] = 0x6Du
            memory[0x01F5] = 0xF1u
            memory[0x036A] = 0x00u
            memory[0x03AE] = 0x94u
            memory[0x03C5] = 0x00u
            memory[0x03D1] = 0x00u
            memory[0x06E5] = 0xD0u
            memory[0x0747] = 0x00u
            memory[0x0796] = 0x00u

            // Execute decompiled function
            getEnemyOffscreenBits()

            // Verify: Check output memory (9 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xB8u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x01u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x03u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x94u, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0xD0u, memory[0x00EB], "Memory 0x00EB mismatch")
            assertEquals(0x00u, memory[0x00EC], "Memory 0x00EC mismatch")
            assertEquals(0x00u, memory[0x00ED], "Memory 0x00ED mismatch")
            assertEquals(0x06u, memory[0x00EF], "Memory 0x00EF mismatch")
        }
    }

    /**
     * Test case 8 from frame 4315
     * Function: getEnemyOffscreenBits (0xF165)
     * Call depth: 8
     * Memory reads: 27, writes: 18
     */
    @Test
    fun `getEnemyOffscreenBits_frame4315_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (27 addresses)
            memory[0x0000] = 0x00u
            memory[0x0002] = 0xB8u
            memory[0x0004] = 0x00u
            memory[0x0008] = 0x00u
            memory[0x0009] = 0xE9u
            memory[0x0016] = 0x06u
            memory[0x001E] = 0x04u
            memory[0x0046] = 0x02u
            memory[0x00CF] = 0xB8u
            memory[0x00EB] = 0x88u
            memory[0x00EC] = 0x04u
            memory[0x00ED] = 0x04u
            memory[0x00EF] = 0x06u
            memory[0x0109] = 0x00u
            memory[0x01F4] = 0x6Du
            memory[0x01F5] = 0xF1u
            memory[0x028A] = 0x43u
            memory[0x0292] = 0x03u
            memory[0x036A] = 0x00u
            memory[0x03AE] = 0x63u
            memory[0x03C5] = 0x00u
            memory[0x03D1] = 0x00u
            memory[0x03D8] = 0x00u
            memory[0x06E5] = 0x88u
            memory[0x0747] = 0x00u
            memory[0x074E] = 0x01u
            memory[0x0796] = 0x10u

            // Execute decompiled function
            getEnemyOffscreenBits()

            // Verify: Check output memory (15 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xB9u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x02u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x03u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x63u, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0x88u, memory[0x00EB], "Memory 0x00EB mismatch")
            assertEquals(0x04u, memory[0x00EC], "Memory 0x00EC mismatch")
            assertEquals(0x04u, memory[0x00ED], "Memory 0x00ED mismatch")
            assertEquals(0x06u, memory[0x00EF], "Memory 0x00EF mismatch")
            assertEquals(0x03u, memory[0x028A], "Memory 0x028A mismatch")
            assertEquals(0x43u, memory[0x028E], "Memory 0x028E mismatch")
            assertEquals(0x83u, memory[0x0292], "Memory 0x0292 mismatch")
            assertEquals(0xC3u, memory[0x0296], "Memory 0x0296 mismatch")
            assertEquals(0x83u, memory[0x029A], "Memory 0x029A mismatch")
            assertEquals(0xC3u, memory[0x029E], "Memory 0x029E mismatch")
        }
    }

    /**
     * Test case 9 from frame 5019
     * Function: getEnemyOffscreenBits (0xF165)
     * Call depth: 8
     * Memory reads: 24, writes: 12
     */
    @Test
    fun `getEnemyOffscreenBits_frame5019_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (24 addresses)
            memory[0x0000] = 0x00u
            memory[0x0004] = 0x00u
            memory[0x0008] = 0x00u
            memory[0x0009] = 0x49u
            memory[0x0016] = 0x06u
            memory[0x001E] = 0x00u
            memory[0x0046] = 0x01u
            memory[0x00CF] = 0xB8u
            memory[0x00EB] = 0xD0u
            memory[0x00EC] = 0x00u
            memory[0x00ED] = 0x00u
            memory[0x00EF] = 0x06u
            memory[0x0109] = 0x00u
            memory[0x01F4] = 0x6Du
            memory[0x01F5] = 0xF1u
            memory[0x036A] = 0x00u
            memory[0x03AE] = 0x2Cu
            memory[0x03C5] = 0x00u
            memory[0x03D1] = 0x0Fu
            memory[0x03D8] = 0x08u
            memory[0x06E5] = 0xD0u
            memory[0x0747] = 0x00u
            memory[0x074E] = 0x01u
            memory[0x0796] = 0x00u

            // Execute decompiled function
            getEnemyOffscreenBits()

            // Verify: Check output memory (9 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0xB8u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x01u, memory[0x0003], "Memory 0x0003 mismatch")
            assertEquals(0x03u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x2Cu, memory[0x0005], "Memory 0x0005 mismatch")
            assertEquals(0xD0u, memory[0x00EB], "Memory 0x00EB mismatch")
            assertEquals(0x00u, memory[0x00EC], "Memory 0x00EC mismatch")
            assertEquals(0x00u, memory[0x00ED], "Memory 0x00ED mismatch")
            assertEquals(0x06u, memory[0x00EF], "Memory 0x00EF mismatch")
        }
    }

    // =========================================
    // 0xF170: setOffscrBitsOffset
    // Parameters: A, X
    // 922 calls, 380 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 1159
     * Function: setOffscrBitsOffset (0xF170)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `setOffscrBitsOffset_frame1159_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            setOffscrBitsOffset(0x2E, 0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 1 from frame 1235
     * Function: setOffscrBitsOffset (0xF170)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `setOffscrBitsOffset_frame1235_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            setOffscrBitsOffset(0xF5, 0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 2 from frame 1313
     * Function: setOffscrBitsOffset (0xF170)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `setOffscrBitsOffset_frame1313_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            setOffscrBitsOffset(0xB9, 0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 3 from frame 1445
     * Function: setOffscrBitsOffset (0xF170)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `setOffscrBitsOffset_frame1445_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            setOffscrBitsOffset(0x93, 0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 4 from frame 3891
     * Function: setOffscrBitsOffset (0xF170)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `setOffscrBitsOffset_frame3891_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            setOffscrBitsOffset(0xEB, 0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 5 from frame 4099
     * Function: setOffscrBitsOffset (0xF170)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `setOffscrBitsOffset_frame4099_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            setOffscrBitsOffset(0xA0, 0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 6 from frame 4251
     * Function: setOffscrBitsOffset (0xF170)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `setOffscrBitsOffset_frame4251_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            setOffscrBitsOffset(0x75, 0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 7 from frame 4357
     * Function: setOffscrBitsOffset (0xF170)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `setOffscrBitsOffset_frame4357_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            setOffscrBitsOffset(0x4B, 0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 8 from frame 4557
     * Function: setOffscrBitsOffset (0xF170)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `setOffscrBitsOffset_frame4557_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            setOffscrBitsOffset(0x73, 0x01)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    /**
     * Test case 9 from frame 4641
     * Function: setOffscrBitsOffset (0xF170)
     * Parameters: A, X
     * Call depth: 8
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `setOffscrBitsOffset_frame4641_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (0 addresses)
            // No memory inputs

            // Execute decompiled function
            setOffscrBitsOffset(0x69, 0x00)

            // Verify: Check output memory (0 addresses)
            // No memory outputs to verify (or only stack writes)
        }
    }

    // =========================================
    // 0xF1C0: getOffScreenBitsSet
    // Parameters: Y
    // 18443 calls, 12 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 32
     * Function: getOffScreenBitsSet (0xF1C0)
     * Parameters: Y
     * Call depth: 6
     * Memory reads: 5, writes: 5
     */
    @Test
    fun `getOffScreenBitsSet_frame32_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0x00u
            memory[0x0008] = 0x05u
            memory[0x01F3] = 0xC4u
            memory[0x01F4] = 0xF1u
            memory[0x01F5] = 0x00u

            // Execute decompiled function
            getOffScreenBitsSet(0x00)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x03D0], "Memory 0x03D0 mismatch")
        }
    }

    /**
     * Test case 1 from frame 196
     * Function: getOffScreenBitsSet (0xF1C0)
     * Parameters: Y
     * Call depth: 5
     * Memory reads: 5, writes: 5
     */
    @Test
    fun `getOffScreenBitsSet_frame196_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0x00u
            memory[0x0008] = 0x05u
            memory[0x01F5] = 0xC4u
            memory[0x01F6] = 0xF1u
            memory[0x01F7] = 0x00u

            // Execute decompiled function
            getOffScreenBitsSet(0x00)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x03D0], "Memory 0x03D0 mismatch")
        }
    }

    /**
     * Test case 2 from frame 200
     * Function: getOffScreenBitsSet (0xF1C0)
     * Parameters: Y
     * Call depth: 7
     * Memory reads: 5, writes: 5
     */
    @Test
    fun `getOffScreenBitsSet_frame200_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0x00u
            memory[0x0008] = 0x05u
            memory[0x01F3] = 0xC4u
            memory[0x01F4] = 0xF1u
            memory[0x01F5] = 0x00u

            // Execute decompiled function
            getOffScreenBitsSet(0x00)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x03D0], "Memory 0x03D0 mismatch")
        }
    }

    /**
     * Test case 3 from frame 1124
     * Function: getOffScreenBitsSet (0xF1C0)
     * Parameters: Y
     * Call depth: 7
     * Memory reads: 5, writes: 5
     */
    @Test
    fun `getOffScreenBitsSet_frame1124_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0x00u
            memory[0x0008] = 0x00u
            memory[0x01F3] = 0xC4u
            memory[0x01F4] = 0xF1u
            memory[0x01F5] = 0x00u

            // Execute decompiled function
            getOffScreenBitsSet(0x00)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x03D0], "Memory 0x03D0 mismatch")
        }
    }

    /**
     * Test case 4 from frame 1159
     * Function: getOffScreenBitsSet (0xF1C0)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 5, writes: 5
     */
    @Test
    fun `getOffScreenBitsSet_frame1159_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0x0Fu
            memory[0x0008] = 0x00u
            memory[0x01F3] = 0xC4u
            memory[0x01F4] = 0xF1u
            memory[0x01F5] = 0x01u

            // Execute decompiled function
            getOffScreenBitsSet(0x01)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x0Fu, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x0Fu, memory[0x03D1], "Memory 0x03D1 mismatch")
        }
    }

    /**
     * Test case 5 from frame 1233
     * Function: getOffScreenBitsSet (0xF1C0)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 5, writes: 5
     */
    @Test
    fun `getOffScreenBitsSet_frame1233_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0x03u
            memory[0x0008] = 0x00u
            memory[0x01F3] = 0xC4u
            memory[0x01F4] = 0xF1u
            memory[0x01F5] = 0x01u

            // Execute decompiled function
            getOffScreenBitsSet(0x01)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x03u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x03u, memory[0x03D1], "Memory 0x03D1 mismatch")
        }
    }

    /**
     * Test case 6 from frame 1243
     * Function: getOffScreenBitsSet (0xF1C0)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 5, writes: 5
     */
    @Test
    fun `getOffScreenBitsSet_frame1243_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0x01u
            memory[0x0008] = 0x00u
            memory[0x01F3] = 0xC4u
            memory[0x01F4] = 0xF1u
            memory[0x01F5] = 0x01u

            // Execute decompiled function
            getOffScreenBitsSet(0x01)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x01u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x01u, memory[0x03D1], "Memory 0x03D1 mismatch")
        }
    }

    /**
     * Test case 7 from frame 1253
     * Function: getOffScreenBitsSet (0xF1C0)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 5, writes: 5
     */
    @Test
    fun `getOffScreenBitsSet_frame1253_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0x00u
            memory[0x0008] = 0x00u
            memory[0x01F3] = 0xC4u
            memory[0x01F4] = 0xF1u
            memory[0x01F5] = 0x01u

            // Execute decompiled function
            getOffScreenBitsSet(0x01)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x03D1], "Memory 0x03D1 mismatch")
        }
    }

    /**
     * Test case 8 from frame 4375
     * Function: getOffScreenBitsSet (0xF1C0)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 5, writes: 5
     */
    @Test
    fun `getOffScreenBitsSet_frame4375_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0x00u
            memory[0x0008] = 0x00u
            memory[0x01F2] = 0xC4u
            memory[0x01F3] = 0xF1u
            memory[0x01F4] = 0x04u

            // Execute decompiled function
            getOffScreenBitsSet(0x04)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x03D4], "Memory 0x03D4 mismatch")
        }
    }

    /**
     * Test case 9 from frame 4375
     * Function: getOffScreenBitsSet (0xF1C0)
     * Parameters: Y
     * Call depth: 8
     * Memory reads: 5, writes: 5
     */
    @Test
    fun `getOffScreenBitsSet_frame4375_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0x00u
            memory[0x0008] = 0x08u
            memory[0x01F3] = 0xC4u
            memory[0x01F4] = 0xF1u
            memory[0x01F5] = 0x06u

            // Execute decompiled function
            getOffScreenBitsSet(0x06)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x03D6], "Memory 0x03D6 mismatch")
        }
    }

    // =========================================
    // 0xF1F6: getYOffscreenBits
    // Parameters: X
    // 27176 calls, 3077 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 32
     * Function: getYOffscreenBits (0xF1F6)
     * Parameters: X
     * Call depth: 8
     * Memory reads: 9, writes: 5
     */
    @Test
    fun `getYOffscreenBits_frame32_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0004] = 0x00u
            memory[0x006D] = 0x00u
            memory[0x0086] = 0x28u
            memory[0x01EF] = 0x1Du
            memory[0x01F0] = 0xF2u
            memory[0x071A] = 0x00u
            memory[0x071B] = 0x00u
            memory[0x071C] = 0x00u
            memory[0x071D] = 0xFFu

            // Execute decompiled function
            getYOffscreenBits(0x00)

            // Verify: Check output memory (3 addresses)
            assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x38u, memory[0x0006], "Memory 0x0006 mismatch")
            assertEquals(0xD8u, memory[0x0007], "Memory 0x0007 mismatch")
        }
    }

    /**
     * Test case 1 from frame 1161
     * Function: getYOffscreenBits (0xF1F6)
     * Parameters: X
     * Call depth: 10
     * Memory reads: 5, writes: 2
     */
    @Test
    fun `getYOffscreenBits_frame1161_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0004] = 0x01u
            memory[0x006E] = 0x01u
            memory[0x0087] = 0x5Fu
            memory[0x071B] = 0x01u
            memory[0x071D] = 0x32u

            // Execute decompiled function
            getYOffscreenBits(0x01)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x01u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0xD3u, memory[0x0007], "Memory 0x0007 mismatch")
        }
    }

    /**
     * Test case 2 from frame 1318
     * Function: getYOffscreenBits (0xF1F6)
     * Parameters: X
     * Call depth: 9
     * Memory reads: 9, writes: 5
     */
    @Test
    fun `getYOffscreenBits_frame1318_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0004] = 0x00u
            memory[0x006D] = 0x00u
            memory[0x0086] = 0xF2u
            memory[0x01EF] = 0x1Du
            memory[0x01F0] = 0xF2u
            memory[0x071A] = 0x00u
            memory[0x071B] = 0x01u
            memory[0x071C] = 0x82u
            memory[0x071D] = 0x81u

            // Execute decompiled function
            getYOffscreenBits(0x00)

            // Verify: Check output memory (3 addresses)
            assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x38u, memory[0x0006], "Memory 0x0006 mismatch")
            assertEquals(0x90u, memory[0x0007], "Memory 0x0007 mismatch")
        }
    }

    /**
     * Test case 3 from frame 3807
     * Function: getYOffscreenBits (0xF1F6)
     * Parameters: X
     * Call depth: 10
     * Memory reads: 5, writes: 2
     */
    @Test
    fun `getYOffscreenBits_frame3807_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0004] = 0x01u
            memory[0x006E] = 0x01u
            memory[0x0087] = 0x46u
            memory[0x071B] = 0x01u
            memory[0x071D] = 0x45u

            // Execute decompiled function
            getYOffscreenBits(0x01)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x01u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0xFFu, memory[0x0007], "Memory 0x0007 mismatch")
        }
    }

    /**
     * Test case 4 from frame 4331
     * Function: getYOffscreenBits (0xF1F6)
     * Parameters: X
     * Call depth: 10
     * Memory reads: 9, writes: 5
     */
    @Test
    fun `getYOffscreenBits_frame4331_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0004] = 0x00u
            memory[0x006D] = 0x00u
            memory[0x0086] = 0xE0u
            memory[0x01F1] = 0x1Du
            memory[0x01F2] = 0xF2u
            memory[0x071A] = 0x00u
            memory[0x071B] = 0x01u
            memory[0x071C] = 0x73u
            memory[0x071D] = 0x72u

            // Execute decompiled function
            getYOffscreenBits(0x00)

            // Verify: Check output memory (3 addresses)
            assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x38u, memory[0x0006], "Memory 0x0006 mismatch")
            assertEquals(0x93u, memory[0x0007], "Memory 0x0007 mismatch")
        }
    }

    /**
     * Test case 5 from frame 4543
     * Function: getYOffscreenBits (0xF1F6)
     * Parameters: X
     * Call depth: 8
     * Memory reads: 9, writes: 5
     */
    @Test
    fun `getYOffscreenBits_frame4543_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0004] = 0x00u
            memory[0x006D] = 0x01u
            memory[0x0086] = 0x54u
            memory[0x01F1] = 0x1Du
            memory[0x01F2] = 0xF2u
            memory[0x071A] = 0x00u
            memory[0x071B] = 0x01u
            memory[0x071C] = 0xE4u
            memory[0x071D] = 0xE3u

            // Execute decompiled function
            getYOffscreenBits(0x00)

            // Verify: Check output memory (3 addresses)
            assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x38u, memory[0x0006], "Memory 0x0006 mismatch")
            assertEquals(0x90u, memory[0x0007], "Memory 0x0007 mismatch")
        }
    }

    /**
     * Test case 6 from frame 4950
     * Function: getYOffscreenBits (0xF1F6)
     * Parameters: X
     * Call depth: 8
     * Memory reads: 9, writes: 5
     */
    @Test
    fun `getYOffscreenBits_frame4950_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0004] = 0x00u
            memory[0x006D] = 0x01u
            memory[0x0086] = 0xB3u
            memory[0x01F2] = 0x1Du
            memory[0x01F3] = 0xF2u
            memory[0x071A] = 0x01u
            memory[0x071B] = 0x02u
            memory[0x071C] = 0x43u
            memory[0x071D] = 0x42u

            // Execute decompiled function
            getYOffscreenBits(0x00)

            // Verify: Check output memory (3 addresses)
            assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x38u, memory[0x0006], "Memory 0x0006 mismatch")
            assertEquals(0x90u, memory[0x0007], "Memory 0x0007 mismatch")
        }
    }

    /**
     * Test case 7 from frame 6682
     * Function: getYOffscreenBits (0xF1F6)
     * Parameters: X
     * Call depth: 8
     * Memory reads: 9, writes: 5
     */
    @Test
    fun `getYOffscreenBits_frame6682_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0004] = 0x00u
            memory[0x006D] = 0x01u
            memory[0x0086] = 0xF9u
            memory[0x01F2] = 0x1Du
            memory[0x01F3] = 0xF2u
            memory[0x071A] = 0x01u
            memory[0x071B] = 0x02u
            memory[0x071C] = 0x89u
            memory[0x071D] = 0x88u

            // Execute decompiled function
            getYOffscreenBits(0x00)

            // Verify: Check output memory (3 addresses)
            assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x38u, memory[0x0006], "Memory 0x0006 mismatch")
            assertEquals(0x90u, memory[0x0007], "Memory 0x0007 mismatch")
        }
    }

    /**
     * Test case 8 from frame 7703
     * Function: getYOffscreenBits (0xF1F6)
     * Parameters: X
     * Call depth: 8
     * Memory reads: 9, writes: 5
     */
    @Test
    fun `getYOffscreenBits_frame7703_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0004] = 0x00u
            memory[0x006D] = 0x02u
            memory[0x0086] = 0x48u
            memory[0x01F1] = 0x1Du
            memory[0x01F2] = 0xF2u
            memory[0x071A] = 0x01u
            memory[0x071B] = 0x02u
            memory[0x071C] = 0xE9u
            memory[0x071D] = 0xE8u

            // Execute decompiled function
            getYOffscreenBits(0x00)

            // Verify: Check output memory (3 addresses)
            assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x38u, memory[0x0006], "Memory 0x0006 mismatch")
            assertEquals(0xA1u, memory[0x0007], "Memory 0x0007 mismatch")
        }
    }

    /**
     * Test case 9 from frame 12308
     * Function: getYOffscreenBits (0xF1F6)
     * Parameters: X
     * Call depth: 8
     * Memory reads: 9, writes: 5
     */
    @Test
    fun `getYOffscreenBits_frame12308_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (9 addresses)
            memory[0x0004] = 0x00u
            memory[0x006D] = 0x02u
            memory[0x0086] = 0x41u
            memory[0x01F2] = 0x1Du
            memory[0x01F3] = 0xF2u
            memory[0x071A] = 0x02u
            memory[0x071B] = 0x03u
            memory[0x071C] = 0x03u
            memory[0x071D] = 0x02u

            // Execute decompiled function
            getYOffscreenBits(0x00)

            // Verify: Check output memory (3 addresses)
            assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
            assertEquals(0x38u, memory[0x0006], "Memory 0x0006 mismatch")
            assertEquals(0xC2u, memory[0x0007], "Memory 0x0007 mismatch")
        }
    }

    // =========================================
    // 0xF26D: dividePDiff
    // Parameters: A, Y
    // 45185 calls, 707 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 32
     * Function: dividePDiff (0xF26D)
     * Parameters: A, Y
     * Call depth: 9
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `dividePDiff_frame32_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0006] = 0x38u
            memory[0x0007] = 0xD7u

            // Execute decompiled function
            dividePDiff(0x08, 0x01)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x08u, memory[0x0005], "Memory 0x0005 mismatch")
        }
    }

    /**
     * Test case 1 from frame 303
     * Function: dividePDiff (0xF26D)
     * Parameters: A, Y
     * Call depth: 8
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `dividePDiff_frame303_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0006] = 0x20u
            memory[0x0007] = 0x79u

            // Execute decompiled function
            dividePDiff(0x04, 0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x04u, memory[0x0005], "Memory 0x0005 mismatch")
        }
    }

    /**
     * Test case 2 from frame 703
     * Function: dividePDiff (0xF26D)
     * Parameters: A, Y
     * Call depth: 9
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `dividePDiff_frame703_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0006] = 0x38u
            memory[0x0007] = 0xC9u

            // Execute decompiled function
            dividePDiff(0x08, 0x01)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x08u, memory[0x0005], "Memory 0x0005 mismatch")
        }
    }

    /**
     * Test case 3 from frame 751
     * Function: dividePDiff (0xF26D)
     * Parameters: A, Y
     * Call depth: 9
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `dividePDiff_frame751_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0006] = 0x38u
            memory[0x0007] = 0xB2u

            // Execute decompiled function
            dividePDiff(0x08, 0x01)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x08u, memory[0x0005], "Memory 0x0005 mismatch")
        }
    }

    /**
     * Test case 4 from frame 1194
     * Function: dividePDiff (0xF26D)
     * Parameters: A, Y
     * Call depth: 10
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `dividePDiff_frame1194_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0006] = 0x38u
            memory[0x0007] = 0x9Cu

            // Execute decompiled function
            dividePDiff(0x08, 0x01)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x08u, memory[0x0005], "Memory 0x0005 mismatch")
        }
    }

    /**
     * Test case 5 from frame 1281
     * Function: dividePDiff (0xF26D)
     * Parameters: A, Y
     * Call depth: 11
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `dividePDiff_frame1281_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0006] = 0x38u
            memory[0x0007] = 0x30u

            // Execute decompiled function
            dividePDiff(0x08, 0x01)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x08u, memory[0x0005], "Memory 0x0005 mismatch")
        }
    }

    /**
     * Test case 6 from frame 1517
     * Function: dividePDiff (0xF26D)
     * Parameters: A, Y
     * Call depth: 11
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `dividePDiff_frame1517_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0006] = 0x38u
            memory[0x0007] = 0x7Eu

            // Execute decompiled function
            dividePDiff(0x08, 0x01)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x08u, memory[0x0005], "Memory 0x0005 mismatch")
        }
    }

    /**
     * Test case 7 from frame 3931
     * Function: dividePDiff (0xF26D)
     * Parameters: A, Y
     * Call depth: 11
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `dividePDiff_frame3931_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0006] = 0x38u
            memory[0x0007] = 0x1Eu

            // Execute decompiled function
            dividePDiff(0x08, 0x01)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x08u, memory[0x0005], "Memory 0x0005 mismatch")
        }
    }

    /**
     * Test case 8 from frame 4515
     * Function: dividePDiff (0xF26D)
     * Parameters: A, Y
     * Call depth: 9
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `dividePDiff_frame4515_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0006] = 0x20u
            memory[0x0007] = 0x99u

            // Execute decompiled function
            dividePDiff(0x04, 0x00)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x04u, memory[0x0005], "Memory 0x0005 mismatch")
        }
    }

    /**
     * Test case 9 from frame 16590
     * Function: dividePDiff (0xF26D)
     * Parameters: A, Y
     * Call depth: 10
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `dividePDiff_frame16590_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x0006] = 0x38u
            memory[0x0007] = 0xE2u

            // Execute decompiled function
            dividePDiff(0x08, 0x01)

            // Verify: Check output memory (1 addresses)
            assertEquals(0x08u, memory[0x0005], "Memory 0x0005 mismatch")
        }
    }

    // =========================================
    // 0xF286: soundEngine
    // 37618 calls, 9333 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 33
     * Function: soundEngine (0xF286)
     * Call depth: 8
     * Memory reads: 5, writes: 9
     */
    @Test
    fun `soundEngine_frame33_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0xFCu
            memory[0x0001] = 0xFCu
            memory[0x0002] = 0xB0u
            memory[0x0004] = 0x00u
            memory[0x0005] = 0x28u

            // Execute decompiled function
            soundEngine()

            // Verify: Check output memory (9 addresses)
            assertEquals(0xB8u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0xB0u, memory[0x0204], "Memory 0x0204 mismatch")
            assertEquals(0xFCu, memory[0x0205], "Memory 0x0205 mismatch")
            assertEquals(0x00u, memory[0x0206], "Memory 0x0206 mismatch")
            assertEquals(0x28u, memory[0x0207], "Memory 0x0207 mismatch")
            assertEquals(0xB0u, memory[0x0208], "Memory 0x0208 mismatch")
            assertEquals(0xFCu, memory[0x0209], "Memory 0x0209 mismatch")
            assertEquals(0x00u, memory[0x020A], "Memory 0x020A mismatch")
            assertEquals(0x30u, memory[0x020B], "Memory 0x020B mismatch")
        }
    }

    /**
     * Test case 1 from frame 1219
     * Function: soundEngine (0xF286)
     * Call depth: 10
     * Memory reads: 5, writes: 9
     */
    @Test
    fun `soundEngine_frame1219_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0xFCu
            memory[0x0001] = 0xFCu
            memory[0x0002] = 0x9Au
            memory[0x0004] = 0x00u
            memory[0x0005] = 0x68u

            // Execute decompiled function
            soundEngine()

            // Verify: Check output memory (9 addresses)
            assertEquals(0xA2u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x9Au, memory[0x0204], "Memory 0x0204 mismatch")
            assertEquals(0xFCu, memory[0x0205], "Memory 0x0205 mismatch")
            assertEquals(0x00u, memory[0x0206], "Memory 0x0206 mismatch")
            assertEquals(0x68u, memory[0x0207], "Memory 0x0207 mismatch")
            assertEquals(0x9Au, memory[0x0208], "Memory 0x0208 mismatch")
            assertEquals(0xFCu, memory[0x0209], "Memory 0x0209 mismatch")
            assertEquals(0x00u, memory[0x020A], "Memory 0x020A mismatch")
            assertEquals(0x70u, memory[0x020B], "Memory 0x020B mismatch")
        }
    }

    /**
     * Test case 2 from frame 1773
     * Function: soundEngine (0xF286)
     * Call depth: 9
     * Memory reads: 5, writes: 9
     */
    @Test
    fun `soundEngine_frame1773_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0x9Fu
            memory[0x0001] = 0x9Fu
            memory[0x0002] = 0x8Eu
            memory[0x0004] = 0x02u
            memory[0x0005] = 0x70u

            // Execute decompiled function
            soundEngine()

            // Verify: Check output memory (9 addresses)
            assertEquals(0x96u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x8Eu, memory[0x021C], "Memory 0x021C mismatch")
            assertEquals(0x9Fu, memory[0x021D], "Memory 0x021D mismatch")
            assertEquals(0x02u, memory[0x021E], "Memory 0x021E mismatch")
            assertEquals(0x70u, memory[0x021F], "Memory 0x021F mismatch")
            assertEquals(0x8Eu, memory[0x0220], "Memory 0x0220 mismatch")
            assertEquals(0x9Fu, memory[0x0221], "Memory 0x0221 mismatch")
            assertEquals(0x02u, memory[0x0222], "Memory 0x0222 mismatch")
            assertEquals(0x78u, memory[0x0223], "Memory 0x0223 mismatch")
        }
    }

    /**
     * Test case 3 from frame 3145
     * Function: soundEngine (0xF286)
     * Call depth: 8
     * Memory reads: 5, writes: 9
     */
    @Test
    fun `soundEngine_frame3145_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0xFCu
            memory[0x0001] = 0xFCu
            memory[0x0002] = 0x7Fu
            memory[0x0004] = 0x00u
            memory[0x0005] = 0x3Eu

            // Execute decompiled function
            soundEngine()

            // Verify: Check output memory (9 addresses)
            assertEquals(0x87u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x7Fu, memory[0x0204], "Memory 0x0204 mismatch")
            assertEquals(0xFCu, memory[0x0205], "Memory 0x0205 mismatch")
            assertEquals(0x00u, memory[0x0206], "Memory 0x0206 mismatch")
            assertEquals(0x3Eu, memory[0x0207], "Memory 0x0207 mismatch")
            assertEquals(0x7Fu, memory[0x0208], "Memory 0x0208 mismatch")
            assertEquals(0xFCu, memory[0x0209], "Memory 0x0209 mismatch")
            assertEquals(0x00u, memory[0x020A], "Memory 0x020A mismatch")
            assertEquals(0x46u, memory[0x020B], "Memory 0x020B mismatch")
        }
    }

    /**
     * Test case 4 from frame 3939
     * Function: soundEngine (0xF286)
     * Call depth: 9
     * Memory reads: 5, writes: 9
     */
    @Test
    fun `soundEngine_frame3939_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0x72u
            memory[0x0001] = 0x73u
            memory[0x0002] = 0xC8u
            memory[0x0004] = 0x03u
            memory[0x0005] = 0xDFu

            // Execute decompiled function
            soundEngine()

            // Verify: Check output memory (9 addresses)
            assertEquals(0xD0u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0xC8u, memory[0x02E0], "Memory 0x02E0 mismatch")
            assertEquals(0x73u, memory[0x02E1], "Memory 0x02E1 mismatch")
            assertEquals(0x43u, memory[0x02E2], "Memory 0x02E2 mismatch")
            assertEquals(0xDFu, memory[0x02E3], "Memory 0x02E3 mismatch")
            assertEquals(0xC8u, memory[0x02E4], "Memory 0x02E4 mismatch")
            assertEquals(0x72u, memory[0x02E5], "Memory 0x02E5 mismatch")
            assertEquals(0x43u, memory[0x02E6], "Memory 0x02E6 mismatch")
            assertEquals(0xE7u, memory[0x02E7], "Memory 0x02E7 mismatch")
        }
    }

    /**
     * Test case 5 from frame 4573
     * Function: soundEngine (0xF286)
     * Call depth: 8
     * Memory reads: 5, writes: 9
     */
    @Test
    fun `soundEngine_frame4573_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0x86u
            memory[0x0001] = 0x86u
            memory[0x0002] = 0x94u
            memory[0x0004] = 0x03u
            memory[0x0005] = 0x69u

            // Execute decompiled function
            soundEngine()

            // Verify: Check output memory (9 addresses)
            assertEquals(0x9Cu, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x94u, memory[0x0270], "Memory 0x0270 mismatch")
            assertEquals(0x86u, memory[0x0271], "Memory 0x0271 mismatch")
            assertEquals(0x03u, memory[0x0272], "Memory 0x0272 mismatch")
            assertEquals(0x69u, memory[0x0273], "Memory 0x0273 mismatch")
            assertEquals(0x94u, memory[0x0274], "Memory 0x0274 mismatch")
            assertEquals(0x86u, memory[0x0275], "Memory 0x0275 mismatch")
            assertEquals(0x03u, memory[0x0276], "Memory 0x0276 mismatch")
            assertEquals(0x71u, memory[0x0277], "Memory 0x0277 mismatch")
        }
    }

    /**
     * Test case 6 from frame 7485
     * Function: soundEngine (0xF286)
     * Call depth: 8
     * Memory reads: 5, writes: 9
     */
    @Test
    fun `soundEngine_frame7485_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0x3Au
            memory[0x0001] = 0x37u
            memory[0x0002] = 0xC0u
            memory[0x0004] = 0x00u
            memory[0x0005] = 0x69u

            // Execute decompiled function
            soundEngine()

            // Verify: Check output memory (9 addresses)
            assertEquals(0xC8u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0xC0u, memory[0x0214], "Memory 0x0214 mismatch")
            assertEquals(0x3Au, memory[0x0215], "Memory 0x0215 mismatch")
            assertEquals(0x00u, memory[0x0216], "Memory 0x0216 mismatch")
            assertEquals(0x69u, memory[0x0217], "Memory 0x0217 mismatch")
            assertEquals(0xC0u, memory[0x0218], "Memory 0x0218 mismatch")
            assertEquals(0x37u, memory[0x0219], "Memory 0x0219 mismatch")
            assertEquals(0x00u, memory[0x021A], "Memory 0x021A mismatch")
            assertEquals(0x71u, memory[0x021B], "Memory 0x021B mismatch")
        }
    }

    /**
     * Test case 7 from frame 8875
     * Function: soundEngine (0xF286)
     * Call depth: 8
     * Memory reads: 5, writes: 9
     */
    @Test
    fun `soundEngine_frame8875_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0xFCu
            memory[0x0001] = 0xFCu
            memory[0x0002] = 0x96u
            memory[0x0004] = 0x00u
            memory[0x0005] = 0x4Fu

            // Execute decompiled function
            soundEngine()

            // Verify: Check output memory (9 addresses)
            assertEquals(0x9Eu, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0x96u, memory[0x0204], "Memory 0x0204 mismatch")
            assertEquals(0xFCu, memory[0x0205], "Memory 0x0205 mismatch")
            assertEquals(0x00u, memory[0x0206], "Memory 0x0206 mismatch")
            assertEquals(0x4Fu, memory[0x0207], "Memory 0x0207 mismatch")
            assertEquals(0x96u, memory[0x0208], "Memory 0x0208 mismatch")
            assertEquals(0xFCu, memory[0x0209], "Memory 0x0209 mismatch")
            assertEquals(0x00u, memory[0x020A], "Memory 0x020A mismatch")
            assertEquals(0x57u, memory[0x020B], "Memory 0x020B mismatch")
        }
    }

    /**
     * Test case 8 from frame 11279
     * Function: soundEngine (0xF286)
     * Call depth: 8
     * Memory reads: 5, writes: 9
     */
    @Test
    fun `soundEngine_frame11279_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0xFCu
            memory[0x0001] = 0xFCu
            memory[0x0002] = 0xA0u
            memory[0x0004] = 0x00u
            memory[0x0005] = 0x4Du

            // Execute decompiled function
            soundEngine()

            // Verify: Check output memory (9 addresses)
            assertEquals(0xA8u, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0xA0u, memory[0x020C], "Memory 0x020C mismatch")
            assertEquals(0xFCu, memory[0x020D], "Memory 0x020D mismatch")
            assertEquals(0x40u, memory[0x020E], "Memory 0x020E mismatch")
            assertEquals(0x4Du, memory[0x020F], "Memory 0x020F mismatch")
            assertEquals(0xA0u, memory[0x0210], "Memory 0x0210 mismatch")
            assertEquals(0xFCu, memory[0x0211], "Memory 0x0211 mismatch")
            assertEquals(0x40u, memory[0x0212], "Memory 0x0212 mismatch")
            assertEquals(0x55u, memory[0x0213], "Memory 0x0213 mismatch")
        }
    }

    /**
     * Test case 9 from frame 16503
     * Function: soundEngine (0xF286)
     * Call depth: 8
     * Memory reads: 5, writes: 9
     */
    @Test
    fun `soundEngine_frame16503_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x0000] = 0x32u
            memory[0x0001] = 0x41u
            memory[0x0002] = 0xA2u
            memory[0x0004] = 0x00u
            memory[0x0005] = 0x4Eu

            // Execute decompiled function
            soundEngine()

            // Verify: Check output memory (9 addresses)
            assertEquals(0xAAu, memory[0x0002], "Memory 0x0002 mismatch")
            assertEquals(0xA2u, memory[0x0214], "Memory 0x0214 mismatch")
            assertEquals(0x32u, memory[0x0215], "Memory 0x0215 mismatch")
            assertEquals(0x00u, memory[0x0216], "Memory 0x0216 mismatch")
            assertEquals(0x4Eu, memory[0x0217], "Memory 0x0217 mismatch")
            assertEquals(0xA2u, memory[0x0218], "Memory 0x0218 mismatch")
            assertEquals(0x41u, memory[0x0219], "Memory 0x0219 mismatch")
            assertEquals(0x00u, memory[0x021A], "Memory 0x021A mismatch")
            assertEquals(0x56u, memory[0x021B], "Memory 0x021B mismatch")
        }
    }

    // =========================================
    // 0xF35F: setfreqSqu2
    // Parameters: A
    // 9077 calls, 179 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 35
     * Function: setfreqSqu2 (0xF35F)
     * Parameters: A
     * Call depth: 1
     * Memory reads: 4, writes: 7
     */
    @Test
    fun `setfreqSqu2_frame35_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x0000] = 0x00u
            memory[0x01F9] = 0x68u
            memory[0x01FA] = 0x8Eu
            memory[0x074B] = 0x00u

            // Execute decompiled function
            setfreqSqu2(0x00)

            // Verify: Check output memory (5 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x00FE], "Memory 0x00FE mismatch")
            assertEquals(0x00u, memory[0x00FF], "Memory 0x00FF mismatch")
            assertEquals(0x00u, memory[0x06FD], "Memory 0x06FD mismatch")
            assertEquals(0x00u, memory[0x074B], "Memory 0x074B mismatch")
        }
    }

    /**
     * Test case 1 from frame 287
     * Function: setfreqSqu2 (0xF35F)
     * Parameters: A
     * Call depth: 1
     * Memory reads: 4, writes: 7
     */
    @Test
    fun `setfreqSqu2_frame287_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x0000] = 0x00u
            memory[0x01F9] = 0x68u
            memory[0x01FA] = 0x8Eu
            memory[0x074B] = 0x00u

            // Execute decompiled function
            setfreqSqu2(0x00)

            // Verify: Check output memory (5 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x00FE], "Memory 0x00FE mismatch")
            assertEquals(0x00u, memory[0x00FF], "Memory 0x00FF mismatch")
            assertEquals(0x00u, memory[0x06FD], "Memory 0x06FD mismatch")
            assertEquals(0x00u, memory[0x074B], "Memory 0x074B mismatch")
        }
    }

    /**
     * Test case 2 from frame 681
     * Function: setfreqSqu2 (0xF35F)
     * Parameters: A
     * Call depth: 1
     * Memory reads: 4, writes: 7
     */
    @Test
    fun `setfreqSqu2_frame681_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x0000] = 0x00u
            memory[0x01F9] = 0x68u
            memory[0x01FA] = 0x8Eu
            memory[0x074B] = 0x00u

            // Execute decompiled function
            setfreqSqu2(0x00)

            // Verify: Check output memory (5 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x00FE], "Memory 0x00FE mismatch")
            assertEquals(0x00u, memory[0x00FF], "Memory 0x00FF mismatch")
            assertEquals(0x00u, memory[0x06FD], "Memory 0x06FD mismatch")
            assertEquals(0x00u, memory[0x074B], "Memory 0x074B mismatch")
        }
    }

    /**
     * Test case 3 from frame 1553
     * Function: setfreqSqu2 (0xF35F)
     * Parameters: A
     * Call depth: 1
     * Memory reads: 4, writes: 7
     */
    @Test
    fun `setfreqSqu2_frame1553_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x0000] = 0x00u
            memory[0x01F9] = 0x68u
            memory[0x01FA] = 0x8Eu
            memory[0x074B] = 0x00u

            // Execute decompiled function
            setfreqSqu2(0x00)

            // Verify: Check output memory (5 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x00FE], "Memory 0x00FE mismatch")
            assertEquals(0x00u, memory[0x00FF], "Memory 0x00FF mismatch")
            assertEquals(0x00u, memory[0x06FD], "Memory 0x06FD mismatch")
            assertEquals(0x00u, memory[0x074B], "Memory 0x074B mismatch")
        }
    }

    /**
     * Test case 4 from frame 1817
     * Function: setfreqSqu2 (0xF35F)
     * Parameters: A
     * Call depth: 1
     * Memory reads: 4, writes: 7
     */
    @Test
    fun `setfreqSqu2_frame1817_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x0000] = 0x00u
            memory[0x01F9] = 0x68u
            memory[0x01FA] = 0x8Eu
            memory[0x074B] = 0x00u

            // Execute decompiled function
            setfreqSqu2(0x00)

            // Verify: Check output memory (5 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x00FE], "Memory 0x00FE mismatch")
            assertEquals(0x00u, memory[0x00FF], "Memory 0x00FF mismatch")
            assertEquals(0x00u, memory[0x06FD], "Memory 0x06FD mismatch")
            assertEquals(0x00u, memory[0x074B], "Memory 0x074B mismatch")
        }
    }

    /**
     * Test case 5 from frame 2941
     * Function: setfreqSqu2 (0xF35F)
     * Parameters: A
     * Call depth: 1
     * Memory reads: 4, writes: 7
     */
    @Test
    fun `setfreqSqu2_frame2941_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x0000] = 0x00u
            memory[0x01F9] = 0x68u
            memory[0x01FA] = 0x8Eu
            memory[0x074B] = 0x00u

            // Execute decompiled function
            setfreqSqu2(0x00)

            // Verify: Check output memory (5 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x00FE], "Memory 0x00FE mismatch")
            assertEquals(0x00u, memory[0x00FF], "Memory 0x00FF mismatch")
            assertEquals(0x00u, memory[0x06FD], "Memory 0x06FD mismatch")
            assertEquals(0x00u, memory[0x074B], "Memory 0x074B mismatch")
        }
    }

    /**
     * Test case 6 from frame 3763
     * Function: setfreqSqu2 (0xF35F)
     * Parameters: A
     * Call depth: 1
     * Memory reads: 4, writes: 7
     */
    @Test
    fun `setfreqSqu2_frame3763_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x0000] = 0x00u
            memory[0x01F9] = 0x68u
            memory[0x01FA] = 0x8Eu
            memory[0x074B] = 0x00u

            // Execute decompiled function
            setfreqSqu2(0x00)

            // Verify: Check output memory (5 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x00FE], "Memory 0x00FE mismatch")
            assertEquals(0x00u, memory[0x00FF], "Memory 0x00FF mismatch")
            assertEquals(0x00u, memory[0x06FD], "Memory 0x06FD mismatch")
            assertEquals(0x00u, memory[0x074B], "Memory 0x074B mismatch")
        }
    }

    /**
     * Test case 7 from frame 4401
     * Function: setfreqSqu2 (0xF35F)
     * Parameters: A
     * Call depth: 1
     * Memory reads: 4, writes: 7
     */
    @Test
    fun `setfreqSqu2_frame4401_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x0000] = 0x00u
            memory[0x01F9] = 0x68u
            memory[0x01FA] = 0x8Eu
            memory[0x074B] = 0x00u

            // Execute decompiled function
            setfreqSqu2(0x00)

            // Verify: Check output memory (5 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x00FE], "Memory 0x00FE mismatch")
            assertEquals(0x00u, memory[0x00FF], "Memory 0x00FF mismatch")
            assertEquals(0x00u, memory[0x06FD], "Memory 0x06FD mismatch")
            assertEquals(0x00u, memory[0x074B], "Memory 0x074B mismatch")
        }
    }

    /**
     * Test case 8 from frame 4927
     * Function: setfreqSqu2 (0xF35F)
     * Parameters: A
     * Call depth: 1
     * Memory reads: 4, writes: 7
     */
    @Test
    fun `setfreqSqu2_frame4927_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x0000] = 0x00u
            memory[0x01F9] = 0x68u
            memory[0x01FA] = 0x8Eu
            memory[0x074B] = 0x00u

            // Execute decompiled function
            setfreqSqu2(0x00)

            // Verify: Check output memory (5 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x00FE], "Memory 0x00FE mismatch")
            assertEquals(0x00u, memory[0x00FF], "Memory 0x00FF mismatch")
            assertEquals(0x00u, memory[0x06FD], "Memory 0x06FD mismatch")
            assertEquals(0x00u, memory[0x074B], "Memory 0x074B mismatch")
        }
    }

    /**
     * Test case 9 from frame 16581
     * Function: setfreqSqu2 (0xF35F)
     * Parameters: A
     * Call depth: 1
     * Memory reads: 4, writes: 7
     */
    @Test
    fun `setfreqSqu2_frame16581_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (4 addresses)
            memory[0x0000] = 0x00u
            memory[0x01F9] = 0x68u
            memory[0x01FA] = 0x8Eu
            memory[0x074B] = 0x00u

            // Execute decompiled function
            setfreqSqu2(0x00)

            // Verify: Check output memory (5 addresses)
            assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
            assertEquals(0x00u, memory[0x00FE], "Memory 0x00FE mismatch")
            assertEquals(0x00u, memory[0x00FF], "Memory 0x00FF mismatch")
            assertEquals(0x00u, memory[0x06FD], "Memory 0x06FD mismatch")
            assertEquals(0x00u, memory[0x074B], "Memory 0x074B mismatch")
        }
    }

    // =========================================
    // 0xF363: setfreqTri
    // Parameters: A
    // 9077 calls, 414 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 35
     * Function: setfreqTri (0xF363)
     * Parameters: A
     * Call depth: 1
     * Memory reads: 2, writes: 2
     */
    @Test
    fun `setfreqTri_frame35_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x00F4] = 0x00u
            memory[0x07C0] = 0x00u

            // Execute decompiled function
            setfreqTri(0x00)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x00u, memory[0x00FA], "Memory 0x00FA mismatch")
            assertEquals(0x00u, memory[0x00FD], "Memory 0x00FD mismatch")
        }
    }

    /**
     * Test case 1 from frame 273
     * Function: setfreqTri (0xF363)
     * Parameters: A
     * Call depth: 1
     * Memory reads: 2, writes: 3
     */
    @Test
    fun `setfreqTri_frame273_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x00F4] = 0x01u
            memory[0x07C0] = 0x27u

            // Execute decompiled function
            setfreqTri(0x00)

            // Verify: Check output memory (3 addresses)
            assertEquals(0x00u, memory[0x00FA], "Memory 0x00FA mismatch")
            assertEquals(0x00u, memory[0x00FD], "Memory 0x00FD mismatch")
            assertEquals(0x28u, memory[0x07C0], "Memory 0x07C0 mismatch")
        }
    }

    /**
     * Test case 2 from frame 719
     * Function: setfreqTri (0xF363)
     * Parameters: A
     * Call depth: 1
     * Memory reads: 2, writes: 3
     */
    @Test
    fun `setfreqTri_frame719_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x00F4] = 0x01u
            memory[0x07C0] = 0x30u

            // Execute decompiled function
            setfreqTri(0x00)

            // Verify: Check output memory (3 addresses)
            assertEquals(0x00u, memory[0x00FA], "Memory 0x00FA mismatch")
            assertEquals(0x00u, memory[0x00FD], "Memory 0x00FD mismatch")
            assertEquals(0x30u, memory[0x07C0], "Memory 0x07C0 mismatch")
        }
    }

    /**
     * Test case 3 from frame 1607
     * Function: setfreqTri (0xF363)
     * Parameters: A
     * Call depth: 1
     * Memory reads: 2, writes: 3
     */
    @Test
    fun `setfreqTri_frame1607_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x00F4] = 0x00u
            memory[0x07C0] = 0x11u

            // Execute decompiled function
            setfreqTri(0x00)

            // Verify: Check output memory (3 addresses)
            assertEquals(0x00u, memory[0x00FA], "Memory 0x00FA mismatch")
            assertEquals(0x00u, memory[0x00FD], "Memory 0x00FD mismatch")
            assertEquals(0x10u, memory[0x07C0], "Memory 0x07C0 mismatch")
        }
    }

    /**
     * Test case 4 from frame 2167
     * Function: setfreqTri (0xF363)
     * Parameters: A
     * Call depth: 1
     * Memory reads: 2, writes: 3
     */
    @Test
    fun `setfreqTri_frame2167_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x00F4] = 0x01u
            memory[0x07C0] = 0x13u

            // Execute decompiled function
            setfreqTri(0x00)

            // Verify: Check output memory (3 addresses)
            assertEquals(0x00u, memory[0x00FA], "Memory 0x00FA mismatch")
            assertEquals(0x00u, memory[0x00FD], "Memory 0x00FD mismatch")
            assertEquals(0x14u, memory[0x07C0], "Memory 0x07C0 mismatch")
        }
    }

    /**
     * Test case 5 from frame 2995
     * Function: setfreqTri (0xF363)
     * Parameters: A
     * Call depth: 1
     * Memory reads: 2, writes: 3
     */
    @Test
    fun `setfreqTri_frame2995_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x00F4] = 0x01u
            memory[0x07C0] = 0x30u

            // Execute decompiled function
            setfreqTri(0x00)

            // Verify: Check output memory (3 addresses)
            assertEquals(0x00u, memory[0x00FA], "Memory 0x00FA mismatch")
            assertEquals(0x00u, memory[0x00FD], "Memory 0x00FD mismatch")
            assertEquals(0x30u, memory[0x07C0], "Memory 0x07C0 mismatch")
        }
    }

    /**
     * Test case 6 from frame 4411
     * Function: setfreqTri (0xF363)
     * Parameters: A
     * Call depth: 1
     * Memory reads: 2, writes: 3
     */
    @Test
    fun `setfreqTri_frame4411_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x00F4] = 0x01u
            memory[0x07C0] = 0x30u

            // Execute decompiled function
            setfreqTri(0x00)

            // Verify: Check output memory (3 addresses)
            assertEquals(0x00u, memory[0x00FA], "Memory 0x00FA mismatch")
            assertEquals(0x00u, memory[0x00FD], "Memory 0x00FD mismatch")
            assertEquals(0x30u, memory[0x07C0], "Memory 0x07C0 mismatch")
        }
    }

    /**
     * Test case 7 from frame 15975
     * Function: setfreqTri (0xF363)
     * Parameters: A
     * Call depth: 1
     * Memory reads: 2, writes: 3
     */
    @Test
    fun `setfreqTri_frame15975_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x00F4] = 0x01u
            memory[0x07C0] = 0x30u

            // Execute decompiled function
            setfreqTri(0x00)

            // Verify: Check output memory (3 addresses)
            assertEquals(0x00u, memory[0x00FA], "Memory 0x00FA mismatch")
            assertEquals(0x00u, memory[0x00FD], "Memory 0x00FD mismatch")
            assertEquals(0x30u, memory[0x07C0], "Memory 0x07C0 mismatch")
        }
    }

    /**
     * Test case 8 from frame 16663
     * Function: setfreqTri (0xF363)
     * Parameters: A
     * Call depth: 1
     * Memory reads: 2, writes: 3
     */
    @Test
    fun `setfreqTri_frame16663_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x00F4] = 0x00u
            memory[0x07C0] = 0x07u

            // Execute decompiled function
            setfreqTri(0x00)

            // Verify: Check output memory (3 addresses)
            assertEquals(0x00u, memory[0x00FA], "Memory 0x00FA mismatch")
            assertEquals(0x00u, memory[0x00FD], "Memory 0x00FD mismatch")
            assertEquals(0x06u, memory[0x07C0], "Memory 0x07C0 mismatch")
        }
    }

    /**
     * Test case 9 from frame 16949
     * Function: setfreqTri (0xF363)
     * Parameters: A
     * Call depth: 1
     * Memory reads: 2, writes: 3
     */
    @Test
    fun `setfreqTri_frame16949_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (2 addresses)
            memory[0x00F4] = 0x01u
            memory[0x07C0] = 0x10u

            // Execute decompiled function
            setfreqTri(0x00)

            // Verify: Check output memory (3 addresses)
            assertEquals(0x00u, memory[0x00FA], "Memory 0x00FA mismatch")
            assertEquals(0x00u, memory[0x00FD], "Memory 0x00FD mismatch")
            assertEquals(0x11u, memory[0x07C0], "Memory 0x07C0 mismatch")
        }
    }

    // =========================================
    // 0xF45D: stopSquare1Sfx
    // 1 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 4285
     * Function: stopSquare1Sfx (0xF45D)
     * Call depth: 2
     * Memory reads: 3, writes: 3
     */
    @Test
    fun `stopSquare1Sfx_frame4285_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01F7] = 0x68u
            memory[0x01F8] = 0xF4u
            memory[0x07BB] = 0x0Eu

            // Execute decompiled function
            stopSquare1Sfx()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x0Du, memory[0x07BB], "Memory 0x07BB mismatch")
        }
    }

    // =========================================
    // 0xF527: stopSquare2Sfx
    // 1 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 4375
     * Function: stopSquare2Sfx (0xF527)
     * Call depth: 2
     * Memory reads: 3, writes: 3
     */
    @Test
    fun `stopSquare2Sfx_frame4375_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (3 addresses)
            memory[0x01F7] = 0x2Bu
            memory[0x01F8] = 0xF5u
            memory[0x07BD] = 0x35u

            // Execute decompiled function
            stopSquare2Sfx()

            // Verify: Check output memory (1 addresses)
            assertEquals(0x34u, memory[0x07BD], "Memory 0x07BD mismatch")
        }
    }

    // =========================================
    // 0xF87B: alternateLengthHandler
    // Parameters: A
    // 782 calls, 168 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 195
     * Function: alternateLengthHandler (0xF87B)
     * Parameters: A
     * Call depth: 2
     * Memory reads: 5, writes: 4
     */
    @Test
    fun `alternateLengthHandler_frame195_test0`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x00F5] = 0xF9u
            memory[0x00F6] = 0xFAu
            memory[0x01F7] = 0x8Cu
            memory[0x01F8] = 0xF8u
            memory[0x07B0] = 0x21u

            // Execute decompiled function
            alternateLengthHandler(0x01)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x22u, memory[0x07B0], "Memory 0x07B0 mismatch")
            assertEquals(0x12u, memory[0x07BA], "Memory 0x07BA mismatch")
        }
    }

    /**
     * Test case 1 from frame 603
     * Function: alternateLengthHandler (0xF87B)
     * Parameters: A
     * Call depth: 2
     * Memory reads: 5, writes: 4
     */
    @Test
    fun `alternateLengthHandler_frame603_test1`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x00F5] = 0x01u
            memory[0x00F6] = 0xFAu
            memory[0x01F7] = 0x8Cu
            memory[0x01F8] = 0xF8u
            memory[0x07B0] = 0xBEu

            // Execute decompiled function
            alternateLengthHandler(0x01)

            // Verify: Check output memory (2 addresses)
            assertEquals(0xBFu, memory[0x07B0], "Memory 0x07B0 mismatch")
            assertEquals(0x06u, memory[0x07BA], "Memory 0x07BA mismatch")
        }
    }

    /**
     * Test case 2 from frame 3461
     * Function: alternateLengthHandler (0xF87B)
     * Parameters: A
     * Call depth: 2
     * Memory reads: 5, writes: 4
     */
    @Test
    fun `alternateLengthHandler_frame3461_test2`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x00F5] = 0x01u
            memory[0x00F6] = 0xFAu
            memory[0x01F7] = 0x8Cu
            memory[0x01F8] = 0xF8u
            memory[0x07B0] = 0xB9u

            // Execute decompiled function
            alternateLengthHandler(0x01)

            // Verify: Check output memory (2 addresses)
            assertEquals(0xBAu, memory[0x07B0], "Memory 0x07B0 mismatch")
            assertEquals(0x06u, memory[0x07BA], "Memory 0x07BA mismatch")
        }
    }

    /**
     * Test case 3 from frame 3905
     * Function: alternateLengthHandler (0xF87B)
     * Parameters: A
     * Call depth: 2
     * Memory reads: 5, writes: 4
     */
    @Test
    fun `alternateLengthHandler_frame3905_test3`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x00F5] = 0x75u
            memory[0x00F6] = 0xFAu
            memory[0x01F7] = 0x8Cu
            memory[0x01F8] = 0xF8u
            memory[0x07B0] = 0x46u

            // Execute decompiled function
            alternateLengthHandler(0x01)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x47u, memory[0x07B0], "Memory 0x07B0 mismatch")
            assertEquals(0x06u, memory[0x07BA], "Memory 0x07BA mismatch")
        }
    }

    /**
     * Test case 4 from frame 4493
     * Function: alternateLengthHandler (0xF87B)
     * Parameters: A
     * Call depth: 2
     * Memory reads: 5, writes: 4
     */
    @Test
    fun `alternateLengthHandler_frame4493_test4`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x00F5] = 0x9Du
            memory[0x00F6] = 0xFAu
            memory[0x01F7] = 0x8Cu
            memory[0x01F8] = 0xF8u
            memory[0x07B0] = 0x1Fu

            // Execute decompiled function
            alternateLengthHandler(0x01)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x20u, memory[0x07B0], "Memory 0x07B0 mismatch")
            assertEquals(0x06u, memory[0x07BA], "Memory 0x07BA mismatch")
        }
    }

    /**
     * Test case 5 from frame 5765
     * Function: alternateLengthHandler (0xF87B)
     * Parameters: A
     * Call depth: 2
     * Memory reads: 5, writes: 4
     */
    @Test
    fun `alternateLengthHandler_frame5765_test5`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x00F5] = 0x9Du
            memory[0x00F6] = 0xFAu
            memory[0x01F7] = 0x8Cu
            memory[0x01F8] = 0xF8u
            memory[0x07B0] = 0x1Du

            // Execute decompiled function
            alternateLengthHandler(0x01)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x1Eu, memory[0x07B0], "Memory 0x07B0 mismatch")
            assertEquals(0x06u, memory[0x07BA], "Memory 0x07BA mismatch")
        }
    }

    /**
     * Test case 6 from frame 6305
     * Function: alternateLengthHandler (0xF87B)
     * Parameters: A
     * Call depth: 2
     * Memory reads: 5, writes: 4
     */
    @Test
    fun `alternateLengthHandler_frame6305_test6`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x00F5] = 0xDBu
            memory[0x00F6] = 0xFAu
            memory[0x01F7] = 0x8Cu
            memory[0x01F8] = 0xF8u
            memory[0x07B0] = 0x44u

            // Execute decompiled function
            alternateLengthHandler(0x01)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x45u, memory[0x07B0], "Memory 0x07B0 mismatch")
            assertEquals(0x1Bu, memory[0x07BA], "Memory 0x07BA mismatch")
        }
    }

    /**
     * Test case 7 from frame 8465
     * Function: alternateLengthHandler (0xF87B)
     * Parameters: A
     * Call depth: 2
     * Memory reads: 5, writes: 4
     */
    @Test
    fun `alternateLengthHandler_frame8465_test7`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x00F5] = 0x4Bu
            memory[0x00F6] = 0xFBu
            memory[0x01F7] = 0x8Cu
            memory[0x01F8] = 0xF8u
            memory[0x07B0] = 0x54u

            // Execute decompiled function
            alternateLengthHandler(0x01)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x55u, memory[0x07B0], "Memory 0x07B0 mismatch")
            assertEquals(0x1Bu, memory[0x07BA], "Memory 0x07BA mismatch")
        }
    }

    /**
     * Test case 8 from frame 9869
     * Function: alternateLengthHandler (0xF87B)
     * Parameters: A
     * Call depth: 2
     * Memory reads: 5, writes: 4
     */
    @Test
    fun `alternateLengthHandler_frame9869_test8`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x00F5] = 0x4Bu
            memory[0x00F6] = 0xFBu
            memory[0x01F7] = 0x8Cu
            memory[0x01F8] = 0xF8u
            memory[0x07B0] = 0x57u

            // Execute decompiled function
            alternateLengthHandler(0x01)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x58u, memory[0x07B0], "Memory 0x07B0 mismatch")
            assertEquals(0x12u, memory[0x07BA], "Memory 0x07BA mismatch")
        }
    }

    /**
     * Test case 9 from frame 17049
     * Function: alternateLengthHandler (0xF87B)
     * Parameters: A
     * Call depth: 2
     * Memory reads: 5, writes: 4
     */
    @Test
    fun `alternateLengthHandler_frame17049_test9`() {
        assertTimeoutPreemptively(Duration.ofMillis(1000)) {
            // Setup: Reset state
            resetCPU()
            clearMemory()

            // Setup: Set input memory (5 addresses)
            memory[0x00F5] = 0xC2u
            memory[0x00F6] = 0xFAu
            memory[0x01F7] = 0x8Cu
            memory[0x01F8] = 0xF8u
            memory[0x07B0] = 0x5Eu

            // Execute decompiled function
            alternateLengthHandler(0x01)

            // Verify: Check output memory (2 addresses)
            assertEquals(0x5Fu, memory[0x07B0], "Memory 0x07B0 mismatch")
            assertEquals(0x0Cu, memory[0x07BA], "Memory 0x07BA mismatch")
        }
    }

}
