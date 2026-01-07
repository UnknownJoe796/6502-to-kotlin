@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.smb.generated

import com.ivieleague.decompiler6502tokotlin.hand.*
import com.ivieleague.decompiler6502tokotlin.smb.*
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Auto-generated tests from TAS capture: happylee-warps
 * Captured at: 2026-01-04T05:03:27.630599Z
 * Total frames: 17867
 * Total captures: 1000413
 * Functions with tests: 157
 * Fuzzy matching enabled: threshold = 50 bytes
 *
 * These tests verify that decompiled functions produce the same
 * outputs as the original 6502 binary interpreter.
 */
// Note: 102 functions skipped (not in validFunctions)
// Generated tests for 36 functions
// Exact matches: 4, Fuzzy matches: 51

class GeneratedFunctionTests {

    // =========================================
    // 0x8182: pauseRoutine
    // 9004 calls, 2 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 3
     * Function: pauseRoutine (0x8182)
     * Call depth: 0
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `pauseRoutine_frame3_test0`() {
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

    /**
     * Test case 1 from frame 195
     * Function: pauseRoutine (0x8182)
     * Call depth: 0
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `pauseRoutine_frame195_test1`() {
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

    // =========================================
    // 0x81C6: spriteShuffler
    // 8848 calls, 3 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 32
     * Function: spriteShuffler (0x81C6)
     * Call depth: 0
     * Memory reads: 19, writes: 24
     */
    @Test
    fun `spriteShuffler_frame32_test0`() {
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

    /**
     * Test case 1 from frame 34
     * Function: spriteShuffler (0x81C6)
     * Call depth: 0
     * Memory reads: 19, writes: 24
     */
    @Test
    fun `spriteShuffler_frame34_test1`() {
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

    /**
     * Test case 2 from frame 36
     * Function: spriteShuffler (0x81C6)
     * Call depth: 0
     * Memory reads: 19, writes: 24
     */
    @Test
    fun `spriteShuffler_frame36_test2`() {
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

    // =========================================
    // 0x8212: operModeExecutionTree
    // 9003 calls, 2 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 3
     * Function: operModeExecutionTree (0x8212)
     * Call depth: 0
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `operModeExecutionTree_frame3_test0`() {
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

    /**
     * Test case 1 from frame 32
     * Function: operModeExecutionTree (0x8212)
     * Call depth: 0
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `operModeExecutionTree_frame32_test1`() {
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

    // =========================================
    // 0x8220: moveSpritesOffscreen
    // FUZZY MATCH: captured at +1 bytes from entry
    // 7 calls, 2 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 3
     * Function: moveSpritesOffscreen (0x8220)
     * FUZZY MATCH: entry +1 bytes
     * Call depth: 0
     * Memory reads: 1, writes: 64
     */
    @Test
    fun `moveSpritesOffscreen_frame3_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x04A0] = 0x00u

        // Execute decompiled function
        moveSpritesOffscreen()

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

    /**
     * Test case 1 from frame 6
     * Function: moveSpritesOffscreen (0x8220)
     * FUZZY MATCH: entry +1 bytes
     * Call depth: 4
     * Memory reads: 1, writes: 64
     */
    @Test
    fun `moveSpritesOffscreen_frame6_test1`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x04A0] = 0x00u

        // Execute decompiled function
        moveSpritesOffscreen()

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

    // =========================================
    // 0x8325: drawMushroomIcon
    // FUZZY MATCH: captured at +8 bytes from entry
    // 6 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 29
     * Function: drawMushroomIcon (0x8325)
     * FUZZY MATCH: entry +8 bytes
     * Call depth: 4
     * Memory reads: 1, writes: 8
     */
    @Test
    fun `drawMushroomIcon_frame29_test0`() {
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

    // =========================================
    // 0x836B: demoEngine
    // FUZZY MATCH: captured at +8 bytes from entry
    // 6041 calls, 2565 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 988
     * Function: demoEngine (0x836B)
     * FUZZY MATCH: entry +8 bytes
     * Call depth: 3
     * Memory reads: 2, writes: 3
     */
    @Test
    fun `demoEngine_frame988_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (2 addresses)
        memory[0x0717] = 0x00u
        memory[0x0718] = 0x00u

        // Execute decompiled function
        demoEngine()

        // Verify: Check output memory (3 addresses)
        assertEquals(0x01u, memory[0x06FC], "Memory 0x06FC mismatch")
        assertEquals(0x01u, memory[0x0717], "Memory 0x0717 mismatch")
        assertEquals(0x9Au, memory[0x0718], "Memory 0x0718 mismatch")
    }

    /**
     * Test case 1 from frame 1500
     * Function: demoEngine (0x836B)
     * FUZZY MATCH: entry +8 bytes
     * Call depth: 3
     * Memory reads: 2, writes: 2
     */
    @Test
    fun `demoEngine_frame1500_test1`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (2 addresses)
        memory[0x0717] = 0x06u
        memory[0x0718] = 0x14u

        // Execute decompiled function
        demoEngine()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x80u, memory[0x06FC], "Memory 0x06FC mismatch")
        assertEquals(0x13u, memory[0x0718], "Memory 0x0718 mismatch")
    }

    /**
     * Test case 2 from frame 2014
     * Function: demoEngine (0x836B)
     * FUZZY MATCH: entry +8 bytes
     * Call depth: 3
     * Memory reads: 2, writes: 2
     */
    @Test
    fun `demoEngine_frame2014_test2`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (2 addresses)
        memory[0x0717] = 0x0Du
        memory[0x0718] = 0x2Eu

        // Execute decompiled function
        demoEngine()

        // Verify: Check output memory (2 addresses)
        assertEquals(0xC1u, memory[0x06FC], "Memory 0x06FC mismatch")
        assertEquals(0x2Du, memory[0x0718], "Memory 0x0718 mismatch")
    }

    /**
     * Test case 3 from frame 2526
     * Function: demoEngine (0x836B)
     * FUZZY MATCH: entry +8 bytes
     * Call depth: 3
     * Memory reads: 2, writes: 2
     */
    @Test
    fun `demoEngine_frame2526_test3`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (2 addresses)
        memory[0x0717] = 0x12u
        memory[0x0718] = 0x2Eu

        // Execute decompiled function
        demoEngine()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x01u, memory[0x06FC], "Memory 0x06FC mismatch")
        assertEquals(0x2Du, memory[0x0718], "Memory 0x0718 mismatch")
    }

    /**
     * Test case 4 from frame 3040
     * Function: demoEngine (0x836B)
     * FUZZY MATCH: entry +8 bytes
     * Call depth: 3
     * Memory reads: 2, writes: 2
     */
    @Test
    fun `demoEngine_frame3040_test4`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (2 addresses)
        memory[0x0717] = 0x15u
        memory[0x0718] = 0x5Du

        // Execute decompiled function
        demoEngine()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x06FC], "Memory 0x06FC mismatch")
        assertEquals(0x5Cu, memory[0x0718], "Memory 0x0718 mismatch")
    }

    /**
     * Test case 5 from frame 4652
     * Function: demoEngine (0x836B)
     * FUZZY MATCH: entry +8 bytes
     * Call depth: 3
     * Memory reads: 2, writes: 2
     */
    @Test
    fun `demoEngine_frame4652_test5`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (2 addresses)
        memory[0x0717] = 0x05u
        memory[0x0718] = 0x0Du

        // Execute decompiled function
        demoEngine()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x41u, memory[0x06FC], "Memory 0x06FC mismatch")
        assertEquals(0x0Cu, memory[0x0718], "Memory 0x0718 mismatch")
    }

    /**
     * Test case 6 from frame 6116
     * Function: demoEngine (0x836B)
     * FUZZY MATCH: entry +8 bytes
     * Call depth: 3
     * Memory reads: 2, writes: 2
     */
    @Test
    fun `demoEngine_frame6116_test6`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (2 addresses)
        memory[0x0717] = 0x15u
        memory[0x0718] = 0x9Cu

        // Execute decompiled function
        demoEngine()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x06FC], "Memory 0x06FC mismatch")
        assertEquals(0x9Bu, memory[0x0718], "Memory 0x0718 mismatch")
    }

    /**
     * Test case 7 from frame 8592
     * Function: demoEngine (0x836B)
     * FUZZY MATCH: entry +8 bytes
     * Call depth: 3
     * Memory reads: 2, writes: 2
     */
    @Test
    fun `demoEngine_frame8592_test7`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (2 addresses)
        memory[0x0717] = 0x10u
        memory[0x0718] = 0x53u

        // Execute decompiled function
        demoEngine()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x01u, memory[0x06FC], "Memory 0x06FC mismatch")
        assertEquals(0x52u, memory[0x0718], "Memory 0x0718 mismatch")
    }

    /**
     * Test case 8 from frame 11160
     * Function: demoEngine (0x836B)
     * FUZZY MATCH: entry +8 bytes
     * Call depth: 3
     * Memory reads: 2, writes: 2
     */
    @Test
    fun `demoEngine_frame11160_test8`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (2 addresses)
        memory[0x0717] = 0x09u
        memory[0x0718] = 0x58u

        // Execute decompiled function
        demoEngine()

        // Verify: Check output memory (2 addresses)
        assertEquals(0xC2u, memory[0x06FC], "Memory 0x06FC mismatch")
        assertEquals(0x57u, memory[0x0718], "Memory 0x0718 mismatch")
    }

    /**
     * Test case 9 from frame 13902
     * Function: demoEngine (0x836B)
     * FUZZY MATCH: entry +8 bytes
     * Call depth: 3
     * Memory reads: 2, writes: 2
     */
    @Test
    fun `demoEngine_frame13902_test9`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (2 addresses)
        memory[0x0717] = 0x02u
        memory[0x0718] = 0x0Eu

        // Execute decompiled function
        demoEngine()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x80u, memory[0x06FC], "Memory 0x06FC mismatch")
        assertEquals(0x0Du, memory[0x0718], "Memory 0x0718 mismatch")
    }

    // =========================================
    // 0x85F1: getPlayerColors
    // FUZZY MATCH: captured at +28 bytes from entry
    // 14 calls, 3 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 7
     * Function: getPlayerColors (0x85F1)
     * FUZZY MATCH: entry +28 bytes
     * Call depth: 4
     * Memory reads: 5, writes: 10
     */
    @Test
    fun `getPlayerColors_frame7_test0`() {
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

        // Verify: Check output memory (10 addresses)
        assertEquals(0xFFu, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x07u, memory[0x0300], "Memory 0x0300 mismatch")
        assertEquals(0x3Fu, memory[0x0301], "Memory 0x0301 mismatch")
        assertEquals(0x10u, memory[0x0302], "Memory 0x0302 mismatch")
        assertEquals(0x04u, memory[0x0303], "Memory 0x0303 mismatch")
        assertEquals(0x0Fu, memory[0x0304], "Memory 0x0304 mismatch")
        assertEquals(0x16u, memory[0x0305], "Memory 0x0305 mismatch")
        assertEquals(0x27u, memory[0x0306], "Memory 0x0306 mismatch")
        assertEquals(0x18u, memory[0x0307], "Memory 0x0307 mismatch")
        assertEquals(0x00u, memory[0x0308], "Memory 0x0308 mismatch")
    }

    /**
     * Test case 1 from frame 32
     * Function: getPlayerColors (0x85F1)
     * FUZZY MATCH: entry +28 bytes
     * Call depth: 6
     * Memory reads: 6, writes: 10
     */
    @Test
    fun `getPlayerColors_frame32_test1`() {
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

        // Verify: Check output memory (10 addresses)
        assertEquals(0xFFu, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x07u, memory[0x0300], "Memory 0x0300 mismatch")
        assertEquals(0x3Fu, memory[0x0301], "Memory 0x0301 mismatch")
        assertEquals(0x10u, memory[0x0302], "Memory 0x0302 mismatch")
        assertEquals(0x04u, memory[0x0303], "Memory 0x0303 mismatch")
        assertEquals(0x22u, memory[0x0304], "Memory 0x0304 mismatch")
        assertEquals(0x16u, memory[0x0305], "Memory 0x0305 mismatch")
        assertEquals(0x27u, memory[0x0306], "Memory 0x0306 mismatch")
        assertEquals(0x18u, memory[0x0307], "Memory 0x0307 mismatch")
        assertEquals(0x00u, memory[0x0308], "Memory 0x0308 mismatch")
    }

    /**
     * Test case 2 from frame 5465
     * Function: getPlayerColors (0x85F1)
     * FUZZY MATCH: entry +28 bytes
     * Call depth: 9
     * Memory reads: 6, writes: 10
     */
    @Test
    fun `getPlayerColors_frame5465_test2`() {
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

        // Verify: Check output memory (10 addresses)
        assertEquals(0xFFu, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x07u, memory[0x0300], "Memory 0x0300 mismatch")
        assertEquals(0x3Fu, memory[0x0301], "Memory 0x0301 mismatch")
        assertEquals(0x10u, memory[0x0302], "Memory 0x0302 mismatch")
        assertEquals(0x04u, memory[0x0303], "Memory 0x0303 mismatch")
        assertEquals(0x22u, memory[0x0304], "Memory 0x0304 mismatch")
        assertEquals(0x16u, memory[0x0305], "Memory 0x0305 mismatch")
        assertEquals(0x27u, memory[0x0306], "Memory 0x0306 mismatch")
        assertEquals(0x18u, memory[0x0307], "Memory 0x0307 mismatch")
        assertEquals(0x00u, memory[0x0308], "Memory 0x0308 mismatch")
    }

    // =========================================
    // 0x896A: renderAttributeTables
    // FUZZY MATCH: captured at +33 bytes from entry
    // 175 calls, 35 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 13
     * Function: renderAttributeTables (0x896A)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 13
     * Memory reads: 12, writes: 40
     */
    @Test
    fun `renderAttributeTables_frame13_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (12 addresses)
        memory[0x0000] = 0x23u
        memory[0x0001] = 0x00u
        memory[0x0340] = 0x74u
        memory[0x03F9] = 0x00u
        memory[0x03FA] = 0x00u
        memory[0x03FB] = 0x00u
        memory[0x03FC] = 0x00u
        memory[0x03FD] = 0x00u
        memory[0x03FE] = 0x50u
        memory[0x03FF] = 0x05u
        memory[0x0720] = 0x20u
        memory[0x0721] = 0x84u

        // Execute decompiled function
        renderAttributeTables()

        // Verify: Check output memory (40 addresses)
        assertEquals(0x23u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0xF8u, memory[0x0001], "Memory 0x0001 mismatch")
        assertEquals(0x90u, memory[0x0340], "Memory 0x0340 mismatch")
        assertEquals(0x23u, memory[0x03B5], "Memory 0x03B5 mismatch")
        assertEquals(0xC8u, memory[0x03B6], "Memory 0x03B6 mismatch")
        assertEquals(0x01u, memory[0x03B7], "Memory 0x03B7 mismatch")
        assertEquals(0x00u, memory[0x03B8], "Memory 0x03B8 mismatch")
        assertEquals(0x23u, memory[0x03B9], "Memory 0x03B9 mismatch")
        assertEquals(0xD0u, memory[0x03BA], "Memory 0x03BA mismatch")
        assertEquals(0x01u, memory[0x03BB], "Memory 0x03BB mismatch")
        assertEquals(0x00u, memory[0x03BC], "Memory 0x03BC mismatch")
        assertEquals(0x23u, memory[0x03BD], "Memory 0x03BD mismatch")
        assertEquals(0xD8u, memory[0x03BE], "Memory 0x03BE mismatch")
        assertEquals(0x01u, memory[0x03BF], "Memory 0x03BF mismatch")
        assertEquals(0x00u, memory[0x03C0], "Memory 0x03C0 mismatch")
        assertEquals(0x23u, memory[0x03C1], "Memory 0x03C1 mismatch")
        assertEquals(0xE0u, memory[0x03C2], "Memory 0x03C2 mismatch")
        assertEquals(0x01u, memory[0x03C3], "Memory 0x03C3 mismatch")
        assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
        assertEquals(0x23u, memory[0x03C5], "Memory 0x03C5 mismatch")
        assertEquals(0xE8u, memory[0x03C6], "Memory 0x03C6 mismatch")
        assertEquals(0x01u, memory[0x03C7], "Memory 0x03C7 mismatch")
        assertEquals(0x00u, memory[0x03C8], "Memory 0x03C8 mismatch")
        assertEquals(0x23u, memory[0x03C9], "Memory 0x03C9 mismatch")
        assertEquals(0xF0u, memory[0x03CA], "Memory 0x03CA mismatch")
        assertEquals(0x01u, memory[0x03CB], "Memory 0x03CB mismatch")
        assertEquals(0x50u, memory[0x03CC], "Memory 0x03CC mismatch")
        assertEquals(0x23u, memory[0x03CD], "Memory 0x03CD mismatch")
        assertEquals(0xF8u, memory[0x03CE], "Memory 0x03CE mismatch")
        assertEquals(0x01u, memory[0x03CF], "Memory 0x03CF mismatch")
        assertEquals(0x05u, memory[0x03D0], "Memory 0x03D0 mismatch")
        assertEquals(0x00u, memory[0x03D1], "Memory 0x03D1 mismatch")
        assertEquals(0x00u, memory[0x03F9], "Memory 0x03F9 mismatch")
        assertEquals(0x00u, memory[0x03FA], "Memory 0x03FA mismatch")
        assertEquals(0x00u, memory[0x03FB], "Memory 0x03FB mismatch")
        assertEquals(0x00u, memory[0x03FC], "Memory 0x03FC mismatch")
        assertEquals(0x00u, memory[0x03FD], "Memory 0x03FD mismatch")
        assertEquals(0x00u, memory[0x03FE], "Memory 0x03FE mismatch")
        assertEquals(0x00u, memory[0x03FF], "Memory 0x03FF mismatch")
        assertEquals(0x06u, memory[0x0773], "Memory 0x0773 mismatch")
    }

    /**
     * Test case 1 from frame 16
     * Function: renderAttributeTables (0x896A)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 13
     * Memory reads: 12, writes: 40
     */
    @Test
    fun `renderAttributeTables_frame16_test1`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (12 addresses)
        memory[0x0000] = 0x23u
        memory[0x0001] = 0x0Cu
        memory[0x0340] = 0x74u
        memory[0x03F9] = 0x00u
        memory[0x03FA] = 0x00u
        memory[0x03FB] = 0x00u
        memory[0x03FC] = 0x00u
        memory[0x03FD] = 0x00u
        memory[0x03FE] = 0x50u
        memory[0x03FF] = 0x05u
        memory[0x0720] = 0x20u
        memory[0x0721] = 0x90u

        // Execute decompiled function
        renderAttributeTables()

        // Verify: Check output memory (40 addresses)
        assertEquals(0x23u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0xFBu, memory[0x0001], "Memory 0x0001 mismatch")
        assertEquals(0x90u, memory[0x0340], "Memory 0x0340 mismatch")
        assertEquals(0x23u, memory[0x03B5], "Memory 0x03B5 mismatch")
        assertEquals(0xCBu, memory[0x03B6], "Memory 0x03B6 mismatch")
        assertEquals(0x01u, memory[0x03B7], "Memory 0x03B7 mismatch")
        assertEquals(0x00u, memory[0x03B8], "Memory 0x03B8 mismatch")
        assertEquals(0x23u, memory[0x03B9], "Memory 0x03B9 mismatch")
        assertEquals(0xD3u, memory[0x03BA], "Memory 0x03BA mismatch")
        assertEquals(0x01u, memory[0x03BB], "Memory 0x03BB mismatch")
        assertEquals(0x00u, memory[0x03BC], "Memory 0x03BC mismatch")
        assertEquals(0x23u, memory[0x03BD], "Memory 0x03BD mismatch")
        assertEquals(0xDBu, memory[0x03BE], "Memory 0x03BE mismatch")
        assertEquals(0x01u, memory[0x03BF], "Memory 0x03BF mismatch")
        assertEquals(0x00u, memory[0x03C0], "Memory 0x03C0 mismatch")
        assertEquals(0x23u, memory[0x03C1], "Memory 0x03C1 mismatch")
        assertEquals(0xE3u, memory[0x03C2], "Memory 0x03C2 mismatch")
        assertEquals(0x01u, memory[0x03C3], "Memory 0x03C3 mismatch")
        assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
        assertEquals(0x23u, memory[0x03C5], "Memory 0x03C5 mismatch")
        assertEquals(0xEBu, memory[0x03C6], "Memory 0x03C6 mismatch")
        assertEquals(0x01u, memory[0x03C7], "Memory 0x03C7 mismatch")
        assertEquals(0x00u, memory[0x03C8], "Memory 0x03C8 mismatch")
        assertEquals(0x23u, memory[0x03C9], "Memory 0x03C9 mismatch")
        assertEquals(0xF3u, memory[0x03CA], "Memory 0x03CA mismatch")
        assertEquals(0x01u, memory[0x03CB], "Memory 0x03CB mismatch")
        assertEquals(0x50u, memory[0x03CC], "Memory 0x03CC mismatch")
        assertEquals(0x23u, memory[0x03CD], "Memory 0x03CD mismatch")
        assertEquals(0xFBu, memory[0x03CE], "Memory 0x03CE mismatch")
        assertEquals(0x01u, memory[0x03CF], "Memory 0x03CF mismatch")
        assertEquals(0x05u, memory[0x03D0], "Memory 0x03D0 mismatch")
        assertEquals(0x00u, memory[0x03D1], "Memory 0x03D1 mismatch")
        assertEquals(0x00u, memory[0x03F9], "Memory 0x03F9 mismatch")
        assertEquals(0x00u, memory[0x03FA], "Memory 0x03FA mismatch")
        assertEquals(0x00u, memory[0x03FB], "Memory 0x03FB mismatch")
        assertEquals(0x00u, memory[0x03FC], "Memory 0x03FC mismatch")
        assertEquals(0x00u, memory[0x03FD], "Memory 0x03FD mismatch")
        assertEquals(0x00u, memory[0x03FE], "Memory 0x03FE mismatch")
        assertEquals(0x00u, memory[0x03FF], "Memory 0x03FF mismatch")
        assertEquals(0x06u, memory[0x0773], "Memory 0x0773 mismatch")
    }

    /**
     * Test case 2 from frame 20
     * Function: renderAttributeTables (0x896A)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 13
     * Memory reads: 12, writes: 40
     */
    @Test
    fun `renderAttributeTables_frame20_test2`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (12 addresses)
        memory[0x0000] = 0x23u
        memory[0x0001] = 0x1Cu
        memory[0x0340] = 0x74u
        memory[0x03F9] = 0x00u
        memory[0x03FA] = 0x00u
        memory[0x03FB] = 0x00u
        memory[0x03FC] = 0x00u
        memory[0x03FD] = 0x00u
        memory[0x03FE] = 0x50u
        memory[0x03FF] = 0x05u
        memory[0x0720] = 0x24u
        memory[0x0721] = 0x80u

        // Execute decompiled function
        renderAttributeTables()

        // Verify: Check output memory (40 addresses)
        assertEquals(0x23u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0xFFu, memory[0x0001], "Memory 0x0001 mismatch")
        assertEquals(0x90u, memory[0x0340], "Memory 0x0340 mismatch")
        assertEquals(0x23u, memory[0x03B5], "Memory 0x03B5 mismatch")
        assertEquals(0xCFu, memory[0x03B6], "Memory 0x03B6 mismatch")
        assertEquals(0x01u, memory[0x03B7], "Memory 0x03B7 mismatch")
        assertEquals(0x00u, memory[0x03B8], "Memory 0x03B8 mismatch")
        assertEquals(0x23u, memory[0x03B9], "Memory 0x03B9 mismatch")
        assertEquals(0xD7u, memory[0x03BA], "Memory 0x03BA mismatch")
        assertEquals(0x01u, memory[0x03BB], "Memory 0x03BB mismatch")
        assertEquals(0x00u, memory[0x03BC], "Memory 0x03BC mismatch")
        assertEquals(0x23u, memory[0x03BD], "Memory 0x03BD mismatch")
        assertEquals(0xDFu, memory[0x03BE], "Memory 0x03BE mismatch")
        assertEquals(0x01u, memory[0x03BF], "Memory 0x03BF mismatch")
        assertEquals(0x00u, memory[0x03C0], "Memory 0x03C0 mismatch")
        assertEquals(0x23u, memory[0x03C1], "Memory 0x03C1 mismatch")
        assertEquals(0xE7u, memory[0x03C2], "Memory 0x03C2 mismatch")
        assertEquals(0x01u, memory[0x03C3], "Memory 0x03C3 mismatch")
        assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
        assertEquals(0x23u, memory[0x03C5], "Memory 0x03C5 mismatch")
        assertEquals(0xEFu, memory[0x03C6], "Memory 0x03C6 mismatch")
        assertEquals(0x01u, memory[0x03C7], "Memory 0x03C7 mismatch")
        assertEquals(0x00u, memory[0x03C8], "Memory 0x03C8 mismatch")
        assertEquals(0x23u, memory[0x03C9], "Memory 0x03C9 mismatch")
        assertEquals(0xF7u, memory[0x03CA], "Memory 0x03CA mismatch")
        assertEquals(0x01u, memory[0x03CB], "Memory 0x03CB mismatch")
        assertEquals(0x50u, memory[0x03CC], "Memory 0x03CC mismatch")
        assertEquals(0x23u, memory[0x03CD], "Memory 0x03CD mismatch")
        assertEquals(0xFFu, memory[0x03CE], "Memory 0x03CE mismatch")
        assertEquals(0x01u, memory[0x03CF], "Memory 0x03CF mismatch")
        assertEquals(0x05u, memory[0x03D0], "Memory 0x03D0 mismatch")
        assertEquals(0x00u, memory[0x03D1], "Memory 0x03D1 mismatch")
        assertEquals(0x00u, memory[0x03F9], "Memory 0x03F9 mismatch")
        assertEquals(0x00u, memory[0x03FA], "Memory 0x03FA mismatch")
        assertEquals(0x00u, memory[0x03FB], "Memory 0x03FB mismatch")
        assertEquals(0x00u, memory[0x03FC], "Memory 0x03FC mismatch")
        assertEquals(0x00u, memory[0x03FD], "Memory 0x03FD mismatch")
        assertEquals(0x00u, memory[0x03FE], "Memory 0x03FE mismatch")
        assertEquals(0x00u, memory[0x03FF], "Memory 0x03FF mismatch")
        assertEquals(0x06u, memory[0x0773], "Memory 0x0773 mismatch")
    }

    /**
     * Test case 3 from frame 23
     * Function: renderAttributeTables (0x896A)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 16
     * Memory reads: 12, writes: 40
     */
    @Test
    fun `renderAttributeTables_frame23_test3`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (12 addresses)
        memory[0x0000] = 0x27u
        memory[0x0001] = 0x08u
        memory[0x0340] = 0x74u
        memory[0x03F9] = 0xAAu
        memory[0x03FA] = 0x00u
        memory[0x03FB] = 0x00u
        memory[0x03FC] = 0xD0u
        memory[0x03FD] = 0x00u
        memory[0x03FE] = 0x50u
        memory[0x03FF] = 0x05u
        memory[0x0720] = 0x24u
        memory[0x0721] = 0x8Cu

        // Execute decompiled function
        renderAttributeTables()

        // Verify: Check output memory (40 addresses)
        assertEquals(0x27u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0xFAu, memory[0x0001], "Memory 0x0001 mismatch")
        assertEquals(0x90u, memory[0x0340], "Memory 0x0340 mismatch")
        assertEquals(0x27u, memory[0x03B5], "Memory 0x03B5 mismatch")
        assertEquals(0xCAu, memory[0x03B6], "Memory 0x03B6 mismatch")
        assertEquals(0x01u, memory[0x03B7], "Memory 0x03B7 mismatch")
        assertEquals(0xAAu, memory[0x03B8], "Memory 0x03B8 mismatch")
        assertEquals(0x27u, memory[0x03B9], "Memory 0x03B9 mismatch")
        assertEquals(0xD2u, memory[0x03BA], "Memory 0x03BA mismatch")
        assertEquals(0x01u, memory[0x03BB], "Memory 0x03BB mismatch")
        assertEquals(0x00u, memory[0x03BC], "Memory 0x03BC mismatch")
        assertEquals(0x27u, memory[0x03BD], "Memory 0x03BD mismatch")
        assertEquals(0xDAu, memory[0x03BE], "Memory 0x03BE mismatch")
        assertEquals(0x01u, memory[0x03BF], "Memory 0x03BF mismatch")
        assertEquals(0x00u, memory[0x03C0], "Memory 0x03C0 mismatch")
        assertEquals(0x27u, memory[0x03C1], "Memory 0x03C1 mismatch")
        assertEquals(0xE2u, memory[0x03C2], "Memory 0x03C2 mismatch")
        assertEquals(0x01u, memory[0x03C3], "Memory 0x03C3 mismatch")
        assertEquals(0xD0u, memory[0x03C4], "Memory 0x03C4 mismatch")
        assertEquals(0x27u, memory[0x03C5], "Memory 0x03C5 mismatch")
        assertEquals(0xEAu, memory[0x03C6], "Memory 0x03C6 mismatch")
        assertEquals(0x01u, memory[0x03C7], "Memory 0x03C7 mismatch")
        assertEquals(0x00u, memory[0x03C8], "Memory 0x03C8 mismatch")
        assertEquals(0x27u, memory[0x03C9], "Memory 0x03C9 mismatch")
        assertEquals(0xF2u, memory[0x03CA], "Memory 0x03CA mismatch")
        assertEquals(0x01u, memory[0x03CB], "Memory 0x03CB mismatch")
        assertEquals(0x50u, memory[0x03CC], "Memory 0x03CC mismatch")
        assertEquals(0x27u, memory[0x03CD], "Memory 0x03CD mismatch")
        assertEquals(0xFAu, memory[0x03CE], "Memory 0x03CE mismatch")
        assertEquals(0x01u, memory[0x03CF], "Memory 0x03CF mismatch")
        assertEquals(0x05u, memory[0x03D0], "Memory 0x03D0 mismatch")
        assertEquals(0x00u, memory[0x03D1], "Memory 0x03D1 mismatch")
        assertEquals(0x00u, memory[0x03F9], "Memory 0x03F9 mismatch")
        assertEquals(0x00u, memory[0x03FA], "Memory 0x03FA mismatch")
        assertEquals(0x00u, memory[0x03FB], "Memory 0x03FB mismatch")
        assertEquals(0x00u, memory[0x03FC], "Memory 0x03FC mismatch")
        assertEquals(0x00u, memory[0x03FD], "Memory 0x03FD mismatch")
        assertEquals(0x00u, memory[0x03FE], "Memory 0x03FE mismatch")
        assertEquals(0x00u, memory[0x03FF], "Memory 0x03FF mismatch")
        assertEquals(0x06u, memory[0x0773], "Memory 0x0773 mismatch")
    }

    /**
     * Test case 4 from frame 1265
     * Function: renderAttributeTables (0x896A)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 10
     * Memory reads: 12, writes: 40
     */
    @Test
    fun `renderAttributeTables_frame1265_test4`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (12 addresses)
        memory[0x0000] = 0x27u
        memory[0x0001] = 0x18u
        memory[0x0340] = 0x00u
        memory[0x03F9] = 0xA0u
        memory[0x03FA] = 0x0Au
        memory[0x03FB] = 0x00u
        memory[0x03FC] = 0x00u
        memory[0x03FD] = 0x00u
        memory[0x03FE] = 0x50u
        memory[0x03FF] = 0x05u
        memory[0x0720] = 0x24u
        memory[0x0721] = 0x9Cu

        // Execute decompiled function
        renderAttributeTables()

        // Verify: Check output memory (40 addresses)
        assertEquals(0x27u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0xFEu, memory[0x0001], "Memory 0x0001 mismatch")
        assertEquals(0x1Cu, memory[0x0340], "Memory 0x0340 mismatch")
        assertEquals(0x27u, memory[0x0341], "Memory 0x0341 mismatch")
        assertEquals(0xCEu, memory[0x0342], "Memory 0x0342 mismatch")
        assertEquals(0x01u, memory[0x0343], "Memory 0x0343 mismatch")
        assertEquals(0xA0u, memory[0x0344], "Memory 0x0344 mismatch")
        assertEquals(0x27u, memory[0x0345], "Memory 0x0345 mismatch")
        assertEquals(0xD6u, memory[0x0346], "Memory 0x0346 mismatch")
        assertEquals(0x01u, memory[0x0347], "Memory 0x0347 mismatch")
        assertEquals(0x0Au, memory[0x0348], "Memory 0x0348 mismatch")
        assertEquals(0x27u, memory[0x0349], "Memory 0x0349 mismatch")
        assertEquals(0xDEu, memory[0x034A], "Memory 0x034A mismatch")
        assertEquals(0x01u, memory[0x034B], "Memory 0x034B mismatch")
        assertEquals(0x00u, memory[0x034C], "Memory 0x034C mismatch")
        assertEquals(0x27u, memory[0x034D], "Memory 0x034D mismatch")
        assertEquals(0xE6u, memory[0x034E], "Memory 0x034E mismatch")
        assertEquals(0x01u, memory[0x034F], "Memory 0x034F mismatch")
        assertEquals(0x00u, memory[0x0350], "Memory 0x0350 mismatch")
        assertEquals(0x27u, memory[0x0351], "Memory 0x0351 mismatch")
        assertEquals(0xEEu, memory[0x0352], "Memory 0x0352 mismatch")
        assertEquals(0x01u, memory[0x0353], "Memory 0x0353 mismatch")
        assertEquals(0x00u, memory[0x0354], "Memory 0x0354 mismatch")
        assertEquals(0x27u, memory[0x0355], "Memory 0x0355 mismatch")
        assertEquals(0xF6u, memory[0x0356], "Memory 0x0356 mismatch")
        assertEquals(0x01u, memory[0x0357], "Memory 0x0357 mismatch")
        assertEquals(0x50u, memory[0x0358], "Memory 0x0358 mismatch")
        assertEquals(0x27u, memory[0x0359], "Memory 0x0359 mismatch")
        assertEquals(0xFEu, memory[0x035A], "Memory 0x035A mismatch")
        assertEquals(0x01u, memory[0x035B], "Memory 0x035B mismatch")
        assertEquals(0x05u, memory[0x035C], "Memory 0x035C mismatch")
        assertEquals(0x00u, memory[0x035D], "Memory 0x035D mismatch")
        assertEquals(0x00u, memory[0x03F9], "Memory 0x03F9 mismatch")
        assertEquals(0x00u, memory[0x03FA], "Memory 0x03FA mismatch")
        assertEquals(0x00u, memory[0x03FB], "Memory 0x03FB mismatch")
        assertEquals(0x00u, memory[0x03FC], "Memory 0x03FC mismatch")
        assertEquals(0x00u, memory[0x03FD], "Memory 0x03FD mismatch")
        assertEquals(0x00u, memory[0x03FE], "Memory 0x03FE mismatch")
        assertEquals(0x00u, memory[0x03FF], "Memory 0x03FF mismatch")
        assertEquals(0x06u, memory[0x0773], "Memory 0x0773 mismatch")
    }

    /**
     * Test case 5 from frame 1461
     * Function: renderAttributeTables (0x896A)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 10
     * Memory reads: 12, writes: 40
     */
    @Test
    fun `renderAttributeTables_frame1461_test5`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (12 addresses)
        memory[0x0000] = 0x23u
        memory[0x0001] = 0x04u
        memory[0x0340] = 0x00u
        memory[0x03F9] = 0x00u
        memory[0x03FA] = 0x00u
        memory[0x03FB] = 0x00u
        memory[0x03FC] = 0x00u
        memory[0x03FD] = 0x00u
        memory[0x03FE] = 0x50u
        memory[0x03FF] = 0x05u
        memory[0x0720] = 0x20u
        memory[0x0721] = 0x88u

        // Execute decompiled function
        renderAttributeTables()

        // Verify: Check output memory (40 addresses)
        assertEquals(0x23u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0xF9u, memory[0x0001], "Memory 0x0001 mismatch")
        assertEquals(0x1Cu, memory[0x0340], "Memory 0x0340 mismatch")
        assertEquals(0x23u, memory[0x0341], "Memory 0x0341 mismatch")
        assertEquals(0xC9u, memory[0x0342], "Memory 0x0342 mismatch")
        assertEquals(0x01u, memory[0x0343], "Memory 0x0343 mismatch")
        assertEquals(0x00u, memory[0x0344], "Memory 0x0344 mismatch")
        assertEquals(0x23u, memory[0x0345], "Memory 0x0345 mismatch")
        assertEquals(0xD1u, memory[0x0346], "Memory 0x0346 mismatch")
        assertEquals(0x01u, memory[0x0347], "Memory 0x0347 mismatch")
        assertEquals(0x00u, memory[0x0348], "Memory 0x0348 mismatch")
        assertEquals(0x23u, memory[0x0349], "Memory 0x0349 mismatch")
        assertEquals(0xD9u, memory[0x034A], "Memory 0x034A mismatch")
        assertEquals(0x01u, memory[0x034B], "Memory 0x034B mismatch")
        assertEquals(0x00u, memory[0x034C], "Memory 0x034C mismatch")
        assertEquals(0x23u, memory[0x034D], "Memory 0x034D mismatch")
        assertEquals(0xE1u, memory[0x034E], "Memory 0x034E mismatch")
        assertEquals(0x01u, memory[0x034F], "Memory 0x034F mismatch")
        assertEquals(0x00u, memory[0x0350], "Memory 0x0350 mismatch")
        assertEquals(0x23u, memory[0x0351], "Memory 0x0351 mismatch")
        assertEquals(0xE9u, memory[0x0352], "Memory 0x0352 mismatch")
        assertEquals(0x01u, memory[0x0353], "Memory 0x0353 mismatch")
        assertEquals(0x00u, memory[0x0354], "Memory 0x0354 mismatch")
        assertEquals(0x23u, memory[0x0355], "Memory 0x0355 mismatch")
        assertEquals(0xF1u, memory[0x0356], "Memory 0x0356 mismatch")
        assertEquals(0x01u, memory[0x0357], "Memory 0x0357 mismatch")
        assertEquals(0x50u, memory[0x0358], "Memory 0x0358 mismatch")
        assertEquals(0x23u, memory[0x0359], "Memory 0x0359 mismatch")
        assertEquals(0xF9u, memory[0x035A], "Memory 0x035A mismatch")
        assertEquals(0x01u, memory[0x035B], "Memory 0x035B mismatch")
        assertEquals(0x05u, memory[0x035C], "Memory 0x035C mismatch")
        assertEquals(0x00u, memory[0x035D], "Memory 0x035D mismatch")
        assertEquals(0x00u, memory[0x03F9], "Memory 0x03F9 mismatch")
        assertEquals(0x00u, memory[0x03FA], "Memory 0x03FA mismatch")
        assertEquals(0x00u, memory[0x03FB], "Memory 0x03FB mismatch")
        assertEquals(0x00u, memory[0x03FC], "Memory 0x03FC mismatch")
        assertEquals(0x00u, memory[0x03FD], "Memory 0x03FD mismatch")
        assertEquals(0x00u, memory[0x03FE], "Memory 0x03FE mismatch")
        assertEquals(0x00u, memory[0x03FF], "Memory 0x03FF mismatch")
        assertEquals(0x06u, memory[0x0773], "Memory 0x0773 mismatch")
    }

    /**
     * Test case 6 from frame 1623
     * Function: renderAttributeTables (0x896A)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 9
     * Memory reads: 12, writes: 40
     */
    @Test
    fun `renderAttributeTables_frame1623_test6`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (12 addresses)
        memory[0x0000] = 0x23u
        memory[0x0001] = 0x14u
        memory[0x0340] = 0x00u
        memory[0x03F9] = 0x00u
        memory[0x03FA] = 0x00u
        memory[0x03FB] = 0x00u
        memory[0x03FC] = 0x00u
        memory[0x03FD] = 0x00u
        memory[0x03FE] = 0x50u
        memory[0x03FF] = 0x05u
        memory[0x0720] = 0x20u
        memory[0x0721] = 0x98u

        // Execute decompiled function
        renderAttributeTables()

        // Verify: Check output memory (40 addresses)
        assertEquals(0x23u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0xFDu, memory[0x0001], "Memory 0x0001 mismatch")
        assertEquals(0x1Cu, memory[0x0340], "Memory 0x0340 mismatch")
        assertEquals(0x23u, memory[0x0341], "Memory 0x0341 mismatch")
        assertEquals(0xCDu, memory[0x0342], "Memory 0x0342 mismatch")
        assertEquals(0x01u, memory[0x0343], "Memory 0x0343 mismatch")
        assertEquals(0x00u, memory[0x0344], "Memory 0x0344 mismatch")
        assertEquals(0x23u, memory[0x0345], "Memory 0x0345 mismatch")
        assertEquals(0xD5u, memory[0x0346], "Memory 0x0346 mismatch")
        assertEquals(0x01u, memory[0x0347], "Memory 0x0347 mismatch")
        assertEquals(0x00u, memory[0x0348], "Memory 0x0348 mismatch")
        assertEquals(0x23u, memory[0x0349], "Memory 0x0349 mismatch")
        assertEquals(0xDDu, memory[0x034A], "Memory 0x034A mismatch")
        assertEquals(0x01u, memory[0x034B], "Memory 0x034B mismatch")
        assertEquals(0x00u, memory[0x034C], "Memory 0x034C mismatch")
        assertEquals(0x23u, memory[0x034D], "Memory 0x034D mismatch")
        assertEquals(0xE5u, memory[0x034E], "Memory 0x034E mismatch")
        assertEquals(0x01u, memory[0x034F], "Memory 0x034F mismatch")
        assertEquals(0x00u, memory[0x0350], "Memory 0x0350 mismatch")
        assertEquals(0x23u, memory[0x0351], "Memory 0x0351 mismatch")
        assertEquals(0xEDu, memory[0x0352], "Memory 0x0352 mismatch")
        assertEquals(0x01u, memory[0x0353], "Memory 0x0353 mismatch")
        assertEquals(0x00u, memory[0x0354], "Memory 0x0354 mismatch")
        assertEquals(0x23u, memory[0x0355], "Memory 0x0355 mismatch")
        assertEquals(0xF5u, memory[0x0356], "Memory 0x0356 mismatch")
        assertEquals(0x01u, memory[0x0357], "Memory 0x0357 mismatch")
        assertEquals(0x50u, memory[0x0358], "Memory 0x0358 mismatch")
        assertEquals(0x23u, memory[0x0359], "Memory 0x0359 mismatch")
        assertEquals(0xFDu, memory[0x035A], "Memory 0x035A mismatch")
        assertEquals(0x01u, memory[0x035B], "Memory 0x035B mismatch")
        assertEquals(0x05u, memory[0x035C], "Memory 0x035C mismatch")
        assertEquals(0x00u, memory[0x035D], "Memory 0x035D mismatch")
        assertEquals(0x00u, memory[0x03F9], "Memory 0x03F9 mismatch")
        assertEquals(0x00u, memory[0x03FA], "Memory 0x03FA mismatch")
        assertEquals(0x00u, memory[0x03FB], "Memory 0x03FB mismatch")
        assertEquals(0x00u, memory[0x03FC], "Memory 0x03FC mismatch")
        assertEquals(0x00u, memory[0x03FD], "Memory 0x03FD mismatch")
        assertEquals(0x00u, memory[0x03FE], "Memory 0x03FE mismatch")
        assertEquals(0x00u, memory[0x03FF], "Memory 0x03FF mismatch")
        assertEquals(0x06u, memory[0x0773], "Memory 0x0773 mismatch")
    }

    /**
     * Test case 7 from frame 2351
     * Function: renderAttributeTables (0x896A)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 8
     * Memory reads: 12, writes: 40
     */
    @Test
    fun `renderAttributeTables_frame2351_test7`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (12 addresses)
        memory[0x0000] = 0x27u
        memory[0x0001] = 0x00u
        memory[0x0340] = 0x00u
        memory[0x03F9] = 0x00u
        memory[0x03FA] = 0x00u
        memory[0x03FB] = 0x00u
        memory[0x03FC] = 0x00u
        memory[0x03FD] = 0x00u
        memory[0x03FE] = 0x50u
        memory[0x03FF] = 0x05u
        memory[0x0720] = 0x24u
        memory[0x0721] = 0x84u

        // Execute decompiled function
        renderAttributeTables()

        // Verify: Check output memory (40 addresses)
        assertEquals(0x27u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0xF8u, memory[0x0001], "Memory 0x0001 mismatch")
        assertEquals(0x1Cu, memory[0x0340], "Memory 0x0340 mismatch")
        assertEquals(0x27u, memory[0x0341], "Memory 0x0341 mismatch")
        assertEquals(0xC8u, memory[0x0342], "Memory 0x0342 mismatch")
        assertEquals(0x01u, memory[0x0343], "Memory 0x0343 mismatch")
        assertEquals(0x00u, memory[0x0344], "Memory 0x0344 mismatch")
        assertEquals(0x27u, memory[0x0345], "Memory 0x0345 mismatch")
        assertEquals(0xD0u, memory[0x0346], "Memory 0x0346 mismatch")
        assertEquals(0x01u, memory[0x0347], "Memory 0x0347 mismatch")
        assertEquals(0x00u, memory[0x0348], "Memory 0x0348 mismatch")
        assertEquals(0x27u, memory[0x0349], "Memory 0x0349 mismatch")
        assertEquals(0xD8u, memory[0x034A], "Memory 0x034A mismatch")
        assertEquals(0x01u, memory[0x034B], "Memory 0x034B mismatch")
        assertEquals(0x00u, memory[0x034C], "Memory 0x034C mismatch")
        assertEquals(0x27u, memory[0x034D], "Memory 0x034D mismatch")
        assertEquals(0xE0u, memory[0x034E], "Memory 0x034E mismatch")
        assertEquals(0x01u, memory[0x034F], "Memory 0x034F mismatch")
        assertEquals(0x00u, memory[0x0350], "Memory 0x0350 mismatch")
        assertEquals(0x27u, memory[0x0351], "Memory 0x0351 mismatch")
        assertEquals(0xE8u, memory[0x0352], "Memory 0x0352 mismatch")
        assertEquals(0x01u, memory[0x0353], "Memory 0x0353 mismatch")
        assertEquals(0x00u, memory[0x0354], "Memory 0x0354 mismatch")
        assertEquals(0x27u, memory[0x0355], "Memory 0x0355 mismatch")
        assertEquals(0xF0u, memory[0x0356], "Memory 0x0356 mismatch")
        assertEquals(0x01u, memory[0x0357], "Memory 0x0357 mismatch")
        assertEquals(0x50u, memory[0x0358], "Memory 0x0358 mismatch")
        assertEquals(0x27u, memory[0x0359], "Memory 0x0359 mismatch")
        assertEquals(0xF8u, memory[0x035A], "Memory 0x035A mismatch")
        assertEquals(0x01u, memory[0x035B], "Memory 0x035B mismatch")
        assertEquals(0x05u, memory[0x035C], "Memory 0x035C mismatch")
        assertEquals(0x00u, memory[0x035D], "Memory 0x035D mismatch")
        assertEquals(0x00u, memory[0x03F9], "Memory 0x03F9 mismatch")
        assertEquals(0x00u, memory[0x03FA], "Memory 0x03FA mismatch")
        assertEquals(0x00u, memory[0x03FB], "Memory 0x03FB mismatch")
        assertEquals(0x00u, memory[0x03FC], "Memory 0x03FC mismatch")
        assertEquals(0x00u, memory[0x03FD], "Memory 0x03FD mismatch")
        assertEquals(0x00u, memory[0x03FE], "Memory 0x03FE mismatch")
        assertEquals(0x00u, memory[0x03FF], "Memory 0x03FF mismatch")
        assertEquals(0x06u, memory[0x0773], "Memory 0x0773 mismatch")
    }

    /**
     * Test case 8 from frame 2613
     * Function: renderAttributeTables (0x896A)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 8
     * Memory reads: 12, writes: 40
     */
    @Test
    fun `renderAttributeTables_frame2613_test8`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (12 addresses)
        memory[0x0000] = 0x27u
        memory[0x0001] = 0x10u
        memory[0x0340] = 0x00u
        memory[0x03F9] = 0xA0u
        memory[0x03FA] = 0x0Au
        memory[0x03FB] = 0x00u
        memory[0x03FC] = 0x00u
        memory[0x03FD] = 0x00u
        memory[0x03FE] = 0x50u
        memory[0x03FF] = 0x05u
        memory[0x0720] = 0x24u
        memory[0x0721] = 0x94u

        // Execute decompiled function
        renderAttributeTables()

        // Verify: Check output memory (40 addresses)
        assertEquals(0x27u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0xFCu, memory[0x0001], "Memory 0x0001 mismatch")
        assertEquals(0x1Cu, memory[0x0340], "Memory 0x0340 mismatch")
        assertEquals(0x27u, memory[0x0341], "Memory 0x0341 mismatch")
        assertEquals(0xCCu, memory[0x0342], "Memory 0x0342 mismatch")
        assertEquals(0x01u, memory[0x0343], "Memory 0x0343 mismatch")
        assertEquals(0xA0u, memory[0x0344], "Memory 0x0344 mismatch")
        assertEquals(0x27u, memory[0x0345], "Memory 0x0345 mismatch")
        assertEquals(0xD4u, memory[0x0346], "Memory 0x0346 mismatch")
        assertEquals(0x01u, memory[0x0347], "Memory 0x0347 mismatch")
        assertEquals(0x0Au, memory[0x0348], "Memory 0x0348 mismatch")
        assertEquals(0x27u, memory[0x0349], "Memory 0x0349 mismatch")
        assertEquals(0xDCu, memory[0x034A], "Memory 0x034A mismatch")
        assertEquals(0x01u, memory[0x034B], "Memory 0x034B mismatch")
        assertEquals(0x00u, memory[0x034C], "Memory 0x034C mismatch")
        assertEquals(0x27u, memory[0x034D], "Memory 0x034D mismatch")
        assertEquals(0xE4u, memory[0x034E], "Memory 0x034E mismatch")
        assertEquals(0x01u, memory[0x034F], "Memory 0x034F mismatch")
        assertEquals(0x00u, memory[0x0350], "Memory 0x0350 mismatch")
        assertEquals(0x27u, memory[0x0351], "Memory 0x0351 mismatch")
        assertEquals(0xECu, memory[0x0352], "Memory 0x0352 mismatch")
        assertEquals(0x01u, memory[0x0353], "Memory 0x0353 mismatch")
        assertEquals(0x00u, memory[0x0354], "Memory 0x0354 mismatch")
        assertEquals(0x27u, memory[0x0355], "Memory 0x0355 mismatch")
        assertEquals(0xF4u, memory[0x0356], "Memory 0x0356 mismatch")
        assertEquals(0x01u, memory[0x0357], "Memory 0x0357 mismatch")
        assertEquals(0x50u, memory[0x0358], "Memory 0x0358 mismatch")
        assertEquals(0x27u, memory[0x0359], "Memory 0x0359 mismatch")
        assertEquals(0xFCu, memory[0x035A], "Memory 0x035A mismatch")
        assertEquals(0x01u, memory[0x035B], "Memory 0x035B mismatch")
        assertEquals(0x05u, memory[0x035C], "Memory 0x035C mismatch")
        assertEquals(0x00u, memory[0x035D], "Memory 0x035D mismatch")
        assertEquals(0x00u, memory[0x03F9], "Memory 0x03F9 mismatch")
        assertEquals(0x00u, memory[0x03FA], "Memory 0x03FA mismatch")
        assertEquals(0x00u, memory[0x03FB], "Memory 0x03FB mismatch")
        assertEquals(0x00u, memory[0x03FC], "Memory 0x03FC mismatch")
        assertEquals(0x00u, memory[0x03FD], "Memory 0x03FD mismatch")
        assertEquals(0x00u, memory[0x03FE], "Memory 0x03FE mismatch")
        assertEquals(0x00u, memory[0x03FF], "Memory 0x03FF mismatch")
        assertEquals(0x06u, memory[0x0773], "Memory 0x0773 mismatch")
    }

    /**
     * Test case 9 from frame 5381
     * Function: renderAttributeTables (0x896A)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 10
     * Memory reads: 12, writes: 40
     */
    @Test
    fun `renderAttributeTables_frame5381_test9`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (12 addresses)
        memory[0x0000] = 0x27u
        memory[0x0001] = 0x04u
        memory[0x0340] = 0x00u
        memory[0x03F9] = 0x00u
        memory[0x03FA] = 0x00u
        memory[0x03FB] = 0x00u
        memory[0x03FC] = 0x00u
        memory[0x03FD] = 0x00u
        memory[0x03FE] = 0x50u
        memory[0x03FF] = 0x05u
        memory[0x0720] = 0x24u
        memory[0x0721] = 0x88u

        // Execute decompiled function
        renderAttributeTables()

        // Verify: Check output memory (40 addresses)
        assertEquals(0x27u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0xF9u, memory[0x0001], "Memory 0x0001 mismatch")
        assertEquals(0x1Cu, memory[0x0340], "Memory 0x0340 mismatch")
        assertEquals(0x27u, memory[0x0341], "Memory 0x0341 mismatch")
        assertEquals(0xC9u, memory[0x0342], "Memory 0x0342 mismatch")
        assertEquals(0x01u, memory[0x0343], "Memory 0x0343 mismatch")
        assertEquals(0x00u, memory[0x0344], "Memory 0x0344 mismatch")
        assertEquals(0x27u, memory[0x0345], "Memory 0x0345 mismatch")
        assertEquals(0xD1u, memory[0x0346], "Memory 0x0346 mismatch")
        assertEquals(0x01u, memory[0x0347], "Memory 0x0347 mismatch")
        assertEquals(0x00u, memory[0x0348], "Memory 0x0348 mismatch")
        assertEquals(0x27u, memory[0x0349], "Memory 0x0349 mismatch")
        assertEquals(0xD9u, memory[0x034A], "Memory 0x034A mismatch")
        assertEquals(0x01u, memory[0x034B], "Memory 0x034B mismatch")
        assertEquals(0x00u, memory[0x034C], "Memory 0x034C mismatch")
        assertEquals(0x27u, memory[0x034D], "Memory 0x034D mismatch")
        assertEquals(0xE1u, memory[0x034E], "Memory 0x034E mismatch")
        assertEquals(0x01u, memory[0x034F], "Memory 0x034F mismatch")
        assertEquals(0x00u, memory[0x0350], "Memory 0x0350 mismatch")
        assertEquals(0x27u, memory[0x0351], "Memory 0x0351 mismatch")
        assertEquals(0xE9u, memory[0x0352], "Memory 0x0352 mismatch")
        assertEquals(0x01u, memory[0x0353], "Memory 0x0353 mismatch")
        assertEquals(0x00u, memory[0x0354], "Memory 0x0354 mismatch")
        assertEquals(0x27u, memory[0x0355], "Memory 0x0355 mismatch")
        assertEquals(0xF1u, memory[0x0356], "Memory 0x0356 mismatch")
        assertEquals(0x01u, memory[0x0357], "Memory 0x0357 mismatch")
        assertEquals(0x50u, memory[0x0358], "Memory 0x0358 mismatch")
        assertEquals(0x27u, memory[0x0359], "Memory 0x0359 mismatch")
        assertEquals(0xF9u, memory[0x035A], "Memory 0x035A mismatch")
        assertEquals(0x01u, memory[0x035B], "Memory 0x035B mismatch")
        assertEquals(0x05u, memory[0x035C], "Memory 0x035C mismatch")
        assertEquals(0x00u, memory[0x035D], "Memory 0x035D mismatch")
        assertEquals(0x00u, memory[0x03F9], "Memory 0x03F9 mismatch")
        assertEquals(0x00u, memory[0x03FA], "Memory 0x03FA mismatch")
        assertEquals(0x00u, memory[0x03FB], "Memory 0x03FB mismatch")
        assertEquals(0x00u, memory[0x03FC], "Memory 0x03FC mismatch")
        assertEquals(0x00u, memory[0x03FD], "Memory 0x03FD mismatch")
        assertEquals(0x00u, memory[0x03FE], "Memory 0x03FE mismatch")
        assertEquals(0x00u, memory[0x03FF], "Memory 0x03FF mismatch")
        assertEquals(0x06u, memory[0x0773], "Memory 0x0773 mismatch")
    }

    // =========================================
    // 0x89E1: colorRotation
    // FUZZY MATCH: captured at +33 bytes from entry
    // 8844 calls, 1731 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 33
     * Function: colorRotation (0x89E1)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 5
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `colorRotation_frame33_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x0009] = 0x1Au

        // Execute decompiled function
        colorRotation()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 1 from frame 379
     * Function: colorRotation (0x89E1)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 6
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `colorRotation_frame379_test1`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x0009] = 0xC7u

        // Execute decompiled function
        colorRotation()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 2 from frame 857
     * Function: colorRotation (0x89E1)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 6
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `colorRotation_frame857_test2`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x0009] = 0xB6u

        // Execute decompiled function
        colorRotation()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 3 from frame 1583
     * Function: colorRotation (0x89E1)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 7
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `colorRotation_frame1583_test3`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x0009] = 0x21u

        // Execute decompiled function
        colorRotation()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 4 from frame 2613
     * Function: colorRotation (0x89E1)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 6
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `colorRotation_frame2613_test4`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x0009] = 0x24u

        // Execute decompiled function
        colorRotation()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 5 from frame 4267
     * Function: colorRotation (0x89E1)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 6
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `colorRotation_frame4267_test5`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x0009] = 0x13u

        // Execute decompiled function
        colorRotation()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 6 from frame 5545
     * Function: colorRotation (0x89E1)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 8
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `colorRotation_frame5545_test6`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x0009] = 0x92u

        // Execute decompiled function
        colorRotation()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 7 from frame 6231
     * Function: colorRotation (0x89E1)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 8
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `colorRotation_frame6231_test7`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x0009] = 0xE9u

        // Execute decompiled function
        colorRotation()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 8 from frame 8965
     * Function: colorRotation (0x89E1)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 10
     * Memory reads: 5, writes: 11
     */
    @Test
    fun `colorRotation_frame8965_test8`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (5 addresses)
        memory[0x0000] = 0x03u
        memory[0x0009] = 0x30u
        memory[0x0300] = 0x00u
        memory[0x06D4] = 0x00u
        memory[0x074E] = 0x01u

        // Execute decompiled function
        colorRotation()

        // Verify: Check output memory (11 addresses)
        assertEquals(0xFFu, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x07u, memory[0x0300], "Memory 0x0300 mismatch")
        assertEquals(0x3Fu, memory[0x0301], "Memory 0x0301 mismatch")
        assertEquals(0x0Cu, memory[0x0302], "Memory 0x0302 mismatch")
        assertEquals(0x04u, memory[0x0303], "Memory 0x0303 mismatch")
        assertEquals(0x0Fu, memory[0x0304], "Memory 0x0304 mismatch")
        assertEquals(0x27u, memory[0x0305], "Memory 0x0305 mismatch")
        assertEquals(0x17u, memory[0x0306], "Memory 0x0306 mismatch")
        assertEquals(0x0Fu, memory[0x0307], "Memory 0x0307 mismatch")
        assertEquals(0x00u, memory[0x0308], "Memory 0x0308 mismatch")
        assertEquals(0x01u, memory[0x06D4], "Memory 0x06D4 mismatch")
    }

    /**
     * Test case 9 from frame 12957
     * Function: colorRotation (0x89E1)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 6
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `colorRotation_frame12957_test9`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x0009] = 0xAEu

        // Execute decompiled function
        colorRotation()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    // =========================================
    // 0x8A61: destroyBlockMetatile
    // FUZZY MATCH: captured at +23 bytes from entry
    // 15 calls, 3 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 1345
     * Function: destroyBlockMetatile (0x8A61)
     * FUZZY MATCH: entry +23 bytes
     * Call depth: 9
     * Memory reads: 4, writes: 4
     */
    @Test
    fun `destroyBlockMetatile_frame1345_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (4 addresses)
        memory[0x01F2] = 0x63u
        memory[0x01F3] = 0x8Au
        memory[0x03EC] = 0x01u
        memory[0x03F0] = 0x00u

        // Execute decompiled function
        destroyBlockMetatile()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x03EC], "Memory 0x03EC mismatch")
        assertEquals(0x01u, memory[0x03F0], "Memory 0x03F0 mismatch")
    }

    /**
     * Test case 1 from frame 1519
     * Function: destroyBlockMetatile (0x8A61)
     * FUZZY MATCH: entry +23 bytes
     * Call depth: 8
     * Memory reads: 4, writes: 4
     */
    @Test
    fun `destroyBlockMetatile_frame1519_test1`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (4 addresses)
        memory[0x01F2] = 0x63u
        memory[0x01F3] = 0x8Au
        memory[0x03ED] = 0x01u
        memory[0x03F0] = 0x01u

        // Execute decompiled function
        destroyBlockMetatile()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x03ED], "Memory 0x03ED mismatch")
        assertEquals(0x02u, memory[0x03F0], "Memory 0x03F0 mismatch")
    }

    /**
     * Test case 2 from frame 5107
     * Function: destroyBlockMetatile (0x8A61)
     * FUZZY MATCH: entry +23 bytes
     * Call depth: 7
     * Memory reads: 4, writes: 4
     */
    @Test
    fun `destroyBlockMetatile_frame5107_test2`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (4 addresses)
        memory[0x01F2] = 0x63u
        memory[0x01F3] = 0x8Au
        memory[0x03EC] = 0x01u
        memory[0x03F0] = 0x02u

        // Execute decompiled function
        destroyBlockMetatile()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x03EC], "Memory 0x03EC mismatch")
        assertEquals(0x03u, memory[0x03F0], "Memory 0x03F0 mismatch")
    }

    // =========================================
    // 0x8E04: initializeNameTables
    // FUZZY MATCH: captured at +12 bytes from entry
    // 47012 calls, 2595 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 3
     * Function: initializeNameTables (0x8E04)
     * FUZZY MATCH: entry +12 bytes
     * Call depth: 1
     * Memory reads: 10, writes: 6
     */
    @Test
    fun `initializeNameTables_frame3_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (10 addresses)
        memory[0x0004] = 0x17u
        memory[0x0005] = 0x82u
        memory[0x0006] = 0x31u
        memory[0x0007] = 0x82u
        memory[0x01F8] = 0x17u
        memory[0x01F9] = 0x82u
        memory[0x01FA] = 0x77u
        memory[0x01FB] = 0x81u
        memory[0x01FC] = 0x10u
        memory[0x0772] = 0x00u

        // Execute decompiled function
        initializeNameTables()

        // Verify: Check output memory (4 addresses)
        assertEquals(0x17u, memory[0x0004], "Memory 0x0004 mismatch")
        assertEquals(0x82u, memory[0x0005], "Memory 0x0005 mismatch")
        assertEquals(0x31u, memory[0x0006], "Memory 0x0006 mismatch")
        assertEquals(0x82u, memory[0x0007], "Memory 0x0007 mismatch")
    }

    /**
     * Test case 1 from frame 1187
     * Function: initializeNameTables (0x8E04)
     * FUZZY MATCH: entry +12 bytes
     * Call depth: 7
     * Memory reads: 15, writes: 12
     */
    @Test
    fun `initializeNameTables_frame1187_test1`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (15 addresses)
        memory[0x0004] = 0x91u
        memory[0x0005] = 0xC8u
        memory[0x0006] = 0xE0u
        memory[0x0007] = 0xC8u
        memory[0x000A] = 0x00u
        memory[0x00B5] = 0x01u
        memory[0x01F4] = 0x91u
        memory[0x01F5] = 0xC8u
        memory[0x01F6] = 0x07u
        memory[0x01F7] = 0xAFu
        memory[0x071F] = 0x00u
        memory[0x073D] = 0x15u
        memory[0x0747] = 0x00u
        memory[0x0773] = 0x00u
        memory[0x079F] = 0x00u

        // Execute decompiled function
        initializeNameTables()

        // Verify: Check output memory (8 addresses)
        assertEquals(0x91u, memory[0x0004], "Memory 0x0004 mismatch")
        assertEquals(0xC8u, memory[0x0005], "Memory 0x0005 mismatch")
        assertEquals(0xE0u, memory[0x0006], "Memory 0x0006 mismatch")
        assertEquals(0xC8u, memory[0x0007], "Memory 0x0007 mismatch")
        assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
        assertEquals(0x00u, memory[0x000C], "Memory 0x000C mismatch")
        assertEquals(0x00u, memory[0x000D], "Memory 0x000D mismatch")
        assertEquals(0x00u, memory[0x03C5], "Memory 0x03C5 mismatch")
    }

    /**
     * Test case 2 from frame 1525
     * Function: initializeNameTables (0x8E04)
     * FUZZY MATCH: entry +12 bytes
     * Call depth: 9
     * Memory reads: 35, writes: 48
     */
    @Test
    fun `initializeNameTables_frame1525_test2`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (35 addresses)
        memory[0x0000] = 0x00u
        memory[0x0001] = 0x00u
        memory[0x0002] = 0x04u
        memory[0x0003] = 0x80u
        memory[0x0004] = 0xCAu
        memory[0x0005] = 0x92u
        memory[0x0006] = 0xAEu
        memory[0x0007] = 0x88u
        memory[0x01F2] = 0xCAu
        memory[0x01F3] = 0x92u
        memory[0x0340] = 0x00u
        memory[0x03F9] = 0x00u
        memory[0x03FA] = 0x00u
        memory[0x03FB] = 0x00u
        memory[0x03FC] = 0x00u
        memory[0x03FD] = 0x00u
        memory[0x03FE] = 0x00u
        memory[0x03FF] = 0x00u
        memory[0x06A1] = 0x81u
        memory[0x06A2] = 0x84u
        memory[0x06A3] = 0x00u
        memory[0x06A4] = 0x00u
        memory[0x06A5] = 0x00u
        memory[0x06A6] = 0x00u
        memory[0x06A7] = 0x00u
        memory[0x06A8] = 0x00u
        memory[0x06A9] = 0x12u
        memory[0x06AA] = 0x14u
        memory[0x06AB] = 0x14u
        memory[0x06AC] = 0x54u
        memory[0x06AD] = 0x54u
        memory[0x071F] = 0x07u
        memory[0x0720] = 0x20u
        memory[0x0721] = 0x8Cu
        memory[0x0726] = 0x06u

        // Execute decompiled function
        initializeNameTables()

        // Verify: Check output memory (48 addresses)
        assertEquals(0x1Au, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x0Cu, memory[0x0001], "Memory 0x0001 mismatch")
        assertEquals(0x50u, memory[0x0002], "Memory 0x0002 mismatch")
        assertEquals(0x01u, memory[0x0003], "Memory 0x0003 mismatch")
        assertEquals(0x06u, memory[0x0004], "Memory 0x0004 mismatch")
        assertEquals(0x00u, memory[0x0005], "Memory 0x0005 mismatch")
        assertEquals(0xACu, memory[0x0006], "Memory 0x0006 mismatch")
        assertEquals(0x8Bu, memory[0x0007], "Memory 0x0007 mismatch")
        assertEquals(0x1Du, memory[0x0340], "Memory 0x0340 mismatch")
        assertEquals(0x20u, memory[0x0341], "Memory 0x0341 mismatch")
        assertEquals(0x8Cu, memory[0x0342], "Memory 0x0342 mismatch")
        assertEquals(0x9Au, memory[0x0343], "Memory 0x0343 mismatch")
        assertEquals(0x36u, memory[0x0344], "Memory 0x0344 mismatch")
        assertEquals(0x25u, memory[0x0345], "Memory 0x0345 mismatch")
        assertEquals(0x3Au, memory[0x0346], "Memory 0x0346 mismatch")
        assertEquals(0x24u, memory[0x0347], "Memory 0x0347 mismatch")
        assertEquals(0x24u, memory[0x0348], "Memory 0x0348 mismatch")
        assertEquals(0x24u, memory[0x0349], "Memory 0x0349 mismatch")
        assertEquals(0x24u, memory[0x034A], "Memory 0x034A mismatch")
        assertEquals(0x24u, memory[0x034B], "Memory 0x034B mismatch")
        assertEquals(0x24u, memory[0x034C], "Memory 0x034C mismatch")
        assertEquals(0x24u, memory[0x034D], "Memory 0x034D mismatch")
        assertEquals(0x24u, memory[0x034E], "Memory 0x034E mismatch")
        assertEquals(0x24u, memory[0x034F], "Memory 0x034F mismatch")
        assertEquals(0x24u, memory[0x0350], "Memory 0x0350 mismatch")
        assertEquals(0x24u, memory[0x0351], "Memory 0x0351 mismatch")
        assertEquals(0x24u, memory[0x0352], "Memory 0x0352 mismatch")
        assertEquals(0x24u, memory[0x0353], "Memory 0x0353 mismatch")
        assertEquals(0x60u, memory[0x0354], "Memory 0x0354 mismatch")
        assertEquals(0x64u, memory[0x0355], "Memory 0x0355 mismatch")
        assertEquals(0x68u, memory[0x0356], "Memory 0x0356 mismatch")
        assertEquals(0x68u, memory[0x0357], "Memory 0x0357 mismatch")
        assertEquals(0x68u, memory[0x0358], "Memory 0x0358 mismatch")
        assertEquals(0x68u, memory[0x0359], "Memory 0x0359 mismatch")
        assertEquals(0xB4u, memory[0x035A], "Memory 0x035A mismatch")
        assertEquals(0xB6u, memory[0x035B], "Memory 0x035B mismatch")
        assertEquals(0xB4u, memory[0x035C], "Memory 0x035C mismatch")
        assertEquals(0xB6u, memory[0x035D], "Memory 0x035D mismatch")
        assertEquals(0x00u, memory[0x035E], "Memory 0x035E mismatch")
        assertEquals(0x22u, memory[0x03F9], "Memory 0x03F9 mismatch")
        assertEquals(0x00u, memory[0x03FA], "Memory 0x03FA mismatch")
        assertEquals(0x00u, memory[0x03FB], "Memory 0x03FB mismatch")
        assertEquals(0x00u, memory[0x03FC], "Memory 0x03FC mismatch")
        assertEquals(0x00u, memory[0x03FD], "Memory 0x03FD mismatch")
        assertEquals(0x10u, memory[0x03FE], "Memory 0x03FE mismatch")
        assertEquals(0x01u, memory[0x03FF], "Memory 0x03FF mismatch")
        assertEquals(0x8Du, memory[0x0721], "Memory 0x0721 mismatch")
        assertEquals(0x06u, memory[0x0773], "Memory 0x0773 mismatch")
    }

    /**
     * Test case 3 from frame 2014
     * Function: initializeNameTables (0x8E04)
     * FUZZY MATCH: entry +12 bytes
     * Call depth: 7
     * Memory reads: 19, writes: 15
     */
    @Test
    fun `initializeNameTables_frame2014_test3`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (19 addresses)
        memory[0x0000] = 0x60u
        memory[0x0002] = 0x04u
        memory[0x0004] = 0x50u
        memory[0x0005] = 0xB3u
        memory[0x0006] = 0x6Du
        memory[0x0007] = 0xB3u
        memory[0x000C] = 0x01u
        memory[0x000E] = 0x08u
        memory[0x009F] = 0x04u
        memory[0x00B5] = 0x01u
        memory[0x00CE] = 0x88u
        memory[0x01F2] = 0x50u
        memory[0x01F3] = 0xB3u
        memory[0x0416] = 0xC0u
        memory[0x0433] = 0x00u
        memory[0x0709] = 0x60u
        memory[0x070A] = 0x60u
        memory[0x070E] = 0x00u
        memory[0x0747] = 0x00u

        // Execute decompiled function
        initializeNameTables()

        // Verify: Check output memory (13 addresses)
        assertEquals(0x60u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x04u, memory[0x0002], "Memory 0x0002 mismatch")
        assertEquals(0x50u, memory[0x0004], "Memory 0x0004 mismatch")
        assertEquals(0xB3u, memory[0x0005], "Memory 0x0005 mismatch")
        assertEquals(0x6Du, memory[0x0006], "Memory 0x0006 mismatch")
        assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
        assertEquals(0x04u, memory[0x009F], "Memory 0x009F mismatch")
        assertEquals(0x01u, memory[0x00B5], "Memory 0x00B5 mismatch")
        assertEquals(0x8Cu, memory[0x00CE], "Memory 0x00CE mismatch")
        assertEquals(0xC0u, memory[0x0416], "Memory 0x0416 mismatch")
        assertEquals(0x60u, memory[0x0433], "Memory 0x0433 mismatch")
        assertEquals(0x02u, memory[0x06FF], "Memory 0x06FF mismatch")
        assertEquals(0x60u, memory[0x0709], "Memory 0x0709 mismatch")
    }

    /**
     * Test case 4 from frame 4116
     * Function: initializeNameTables (0x8E04)
     * FUZZY MATCH: entry +12 bytes
     * Call depth: 2
     * Memory reads: 10, writes: 7
     */
    @Test
    fun `initializeNameTables_frame4116_test4`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (10 addresses)
        memory[0x0004] = 0x36u
        memory[0x0005] = 0x82u
        memory[0x0006] = 0x45u
        memory[0x0007] = 0x82u
        memory[0x01F8] = 0x36u
        memory[0x01F9] = 0x82u
        memory[0x06FC] = 0x41u
        memory[0x06FD] = 0x00u
        memory[0x07A2] = 0x02u
        memory[0x07FC] = 0x00u

        // Execute decompiled function
        initializeNameTables()

        // Verify: Check output memory (5 addresses)
        assertEquals(0x36u, memory[0x0004], "Memory 0x0004 mismatch")
        assertEquals(0x82u, memory[0x0005], "Memory 0x0005 mismatch")
        assertEquals(0x45u, memory[0x0006], "Memory 0x0006 mismatch")
        assertEquals(0x82u, memory[0x0007], "Memory 0x0007 mismatch")
        assertEquals(0x00u, memory[0x06FC], "Memory 0x06FC mismatch")
    }

    /**
     * Test case 5 from frame 5385
     * Function: initializeNameTables (0x8E04)
     * FUZZY MATCH: entry +12 bytes
     * Call depth: 7
     * Memory reads: 15, writes: 12
     */
    @Test
    fun `initializeNameTables_frame5385_test5`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (15 addresses)
        memory[0x0004] = 0x91u
        memory[0x0005] = 0xC8u
        memory[0x0006] = 0xE0u
        memory[0x0007] = 0xC8u
        memory[0x000A] = 0xC0u
        memory[0x00B5] = 0x01u
        memory[0x01F4] = 0x91u
        memory[0x01F5] = 0xC8u
        memory[0x01F6] = 0x07u
        memory[0x01F7] = 0xAFu
        memory[0x071F] = 0x00u
        memory[0x073D] = 0x17u
        memory[0x0747] = 0x00u
        memory[0x0773] = 0x00u
        memory[0x079F] = 0x00u

        // Execute decompiled function
        initializeNameTables()

        // Verify: Check output memory (8 addresses)
        assertEquals(0x91u, memory[0x0004], "Memory 0x0004 mismatch")
        assertEquals(0xC8u, memory[0x0005], "Memory 0x0005 mismatch")
        assertEquals(0xE0u, memory[0x0006], "Memory 0x0006 mismatch")
        assertEquals(0xC8u, memory[0x0007], "Memory 0x0007 mismatch")
        assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
        assertEquals(0x00u, memory[0x000C], "Memory 0x000C mismatch")
        assertEquals(0xC0u, memory[0x000D], "Memory 0x000D mismatch")
        assertEquals(0x00u, memory[0x03C5], "Memory 0x03C5 mismatch")
    }

    /**
     * Test case 6 from frame 5555
     * Function: initializeNameTables (0x8E04)
     * FUZZY MATCH: entry +12 bytes
     * Call depth: 6
     * Memory reads: 18, writes: 11
     */
    @Test
    fun `initializeNameTables_frame5555_test6`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (18 addresses)
        memory[0x0000] = 0x02u
        memory[0x0001] = 0x0Cu
        memory[0x0002] = 0x03u
        memory[0x0003] = 0x9Du
        memory[0x0004] = 0x91u
        memory[0x0005] = 0xC8u
        memory[0x0006] = 0xE0u
        memory[0x0007] = 0xC8u
        memory[0x0016] = 0x06u
        memory[0x006E] = 0x02u
        memory[0x0087] = 0xB1u
        memory[0x01F4] = 0x91u
        memory[0x01F5] = 0xC8u
        memory[0x071A] = 0x02u
        memory[0x071B] = 0x03u
        memory[0x071C] = 0x55u
        memory[0x071D] = 0x54u
        memory[0x0747] = 0xD2u

        // Execute decompiled function
        initializeNameTables()

        // Verify: Check output memory (9 addresses)
        assertEquals(0x02u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x0Cu, memory[0x0001], "Memory 0x0001 mismatch")
        assertEquals(0x03u, memory[0x0002], "Memory 0x0002 mismatch")
        assertEquals(0x9Du, memory[0x0003], "Memory 0x0003 mismatch")
        assertEquals(0x91u, memory[0x0004], "Memory 0x0004 mismatch")
        assertEquals(0xC8u, memory[0x0005], "Memory 0x0005 mismatch")
        assertEquals(0xE0u, memory[0x0006], "Memory 0x0006 mismatch")
        assertEquals(0xC8u, memory[0x0007], "Memory 0x0007 mismatch")
        assertEquals(0x00u, memory[0x03C5], "Memory 0x03C5 mismatch")
    }

    /**
     * Test case 7 from frame 5982
     * Function: initializeNameTables (0x8E04)
     * FUZZY MATCH: entry +12 bytes
     * Call depth: 7
     * Memory reads: 20, writes: 15
     */
    @Test
    fun `initializeNameTables_frame5982_test7`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (20 addresses)
        memory[0x0000] = 0x28u
        memory[0x0002] = 0x04u
        memory[0x0004] = 0x50u
        memory[0x0005] = 0xB3u
        memory[0x0006] = 0x76u
        memory[0x0007] = 0xB3u
        memory[0x000A] = 0x80u
        memory[0x000C] = 0x00u
        memory[0x000D] = 0x80u
        memory[0x000E] = 0x0Bu
        memory[0x009F] = 0xFEu
        memory[0x00B5] = 0x01u
        memory[0x00CE] = 0x7Fu
        memory[0x01F2] = 0x50u
        memory[0x01F3] = 0xB3u
        memory[0x0416] = 0x60u
        memory[0x0433] = 0xC8u
        memory[0x0704] = 0x00u
        memory[0x0709] = 0x28u
        memory[0x0747] = 0xDEu

        // Execute decompiled function
        initializeNameTables()

        // Verify: Check output memory (13 addresses)
        assertEquals(0x28u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x04u, memory[0x0002], "Memory 0x0002 mismatch")
        assertEquals(0x50u, memory[0x0004], "Memory 0x0004 mismatch")
        assertEquals(0xB3u, memory[0x0005], "Memory 0x0005 mismatch")
        assertEquals(0x76u, memory[0x0006], "Memory 0x0006 mismatch")
        assertEquals(0xFFu, memory[0x0007], "Memory 0x0007 mismatch")
        assertEquals(0xFEu, memory[0x009F], "Memory 0x009F mismatch")
        assertEquals(0x01u, memory[0x00B5], "Memory 0x00B5 mismatch")
        assertEquals(0x7Eu, memory[0x00CE], "Memory 0x00CE mismatch")
        assertEquals(0x28u, memory[0x0416], "Memory 0x0416 mismatch")
        assertEquals(0xF0u, memory[0x0433], "Memory 0x0433 mismatch")
        assertEquals(0x00u, memory[0x06FF], "Memory 0x06FF mismatch")
        assertEquals(0x28u, memory[0x0709], "Memory 0x0709 mismatch")
    }

    /**
     * Test case 8 from frame 6112
     * Function: initializeNameTables (0x8E04)
     * FUZZY MATCH: entry +12 bytes
     * Call depth: 7
     * Memory reads: 19, writes: 15
     */
    @Test
    fun `initializeNameTables_frame6112_test8`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (19 addresses)
        memory[0x0000] = 0x28u
        memory[0x0002] = 0x04u
        memory[0x0004] = 0x50u
        memory[0x0005] = 0xB3u
        memory[0x0006] = 0x76u
        memory[0x0007] = 0xB3u
        memory[0x000C] = 0x00u
        memory[0x000E] = 0x0Bu
        memory[0x009F] = 0x04u
        memory[0x00B5] = 0x02u
        memory[0x00CE] = 0x30u
        memory[0x01F2] = 0x50u
        memory[0x01F3] = 0xB3u
        memory[0x0416] = 0xE8u
        memory[0x0433] = 0x00u
        memory[0x0704] = 0x00u
        memory[0x0709] = 0x28u
        memory[0x070A] = 0x70u
        memory[0x0747] = 0x9Du

        // Execute decompiled function
        initializeNameTables()

        // Verify: Check output memory (13 addresses)
        assertEquals(0x28u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x04u, memory[0x0002], "Memory 0x0002 mismatch")
        assertEquals(0x50u, memory[0x0004], "Memory 0x0004 mismatch")
        assertEquals(0xB3u, memory[0x0005], "Memory 0x0005 mismatch")
        assertEquals(0x76u, memory[0x0006], "Memory 0x0006 mismatch")
        assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
        assertEquals(0x04u, memory[0x009F], "Memory 0x009F mismatch")
        assertEquals(0x02u, memory[0x00B5], "Memory 0x00B5 mismatch")
        assertEquals(0x34u, memory[0x00CE], "Memory 0x00CE mismatch")
        assertEquals(0xE8u, memory[0x0416], "Memory 0x0416 mismatch")
        assertEquals(0x28u, memory[0x0433], "Memory 0x0433 mismatch")
        assertEquals(0x00u, memory[0x06FF], "Memory 0x06FF mismatch")
        assertEquals(0x28u, memory[0x0709], "Memory 0x0709 mismatch")
    }

    /**
     * Test case 9 from frame 6242
     * Function: initializeNameTables (0x8E04)
     * FUZZY MATCH: entry +12 bytes
     * Call depth: 5
     * Memory reads: 14, writes: 9
     */
    @Test
    fun `initializeNameTables_frame6242_test9`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (14 addresses)
        memory[0x0004] = 0x4Eu
        memory[0x0005] = 0xB0u
        memory[0x0006] = 0x69u
        memory[0x0007] = 0xB2u
        memory[0x000E] = 0x0Bu
        memory[0x01F4] = 0x4Eu
        memory[0x01F5] = 0xB0u
        memory[0x01F6] = 0xF5u
        memory[0x01F7] = 0xAEu
        memory[0x01FA] = 0x77u
        memory[0x01FB] = 0x81u
        memory[0x01FC] = 0x10u
        memory[0x0747] = 0x5Cu
        memory[0x0772] = 0x03u

        // Execute decompiled function
        initializeNameTables()

        // Verify: Check output memory (5 addresses)
        assertEquals(0x4Eu, memory[0x0004], "Memory 0x0004 mismatch")
        assertEquals(0xB0u, memory[0x0005], "Memory 0x0005 mismatch")
        assertEquals(0x69u, memory[0x0006], "Memory 0x0006 mismatch")
        assertEquals(0xB2u, memory[0x0007], "Memory 0x0007 mismatch")
        assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
    }

    // =========================================
    // 0x8E5C: readJoypads
    // FUZZY MATCH: captured at +33 bytes from entry
    // 9004 calls, 2 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 3
     * Function: readJoypads (0x8E5C)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 0
     * Memory reads: 4, writes: 5
     */
    @Test
    fun `readJoypads_frame3_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (4 addresses)
        memory[0x0000] = 0x00u
        memory[0x01F9] = 0x68u
        memory[0x01FA] = 0x8Eu
        memory[0x074B] = 0x00u

        // Execute decompiled function
        readJoypads()

        // Verify: Check output memory (3 addresses)
        assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x00u, memory[0x06FD], "Memory 0x06FD mismatch")
        assertEquals(0x00u, memory[0x074B], "Memory 0x074B mismatch")
    }

    /**
     * Test case 1 from frame 14
     * Function: readJoypads (0x8E5C)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 0
     * Memory reads: 4, writes: 5
     */
    @Test
    fun `readJoypads_frame14_test1`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (4 addresses)
        memory[0x0000] = 0x00u
        memory[0x01F9] = 0x68u
        memory[0x01FA] = 0x8Eu
        memory[0x074B] = 0x00u

        // Execute decompiled function
        readJoypads()

        // Verify: Check output memory (3 addresses)
        assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x00u, memory[0x06FD], "Memory 0x06FD mismatch")
        assertEquals(0x00u, memory[0x074B], "Memory 0x074B mismatch")
    }

    // =========================================
    // 0x8EDD: updateScreen
    // FUZZY MATCH: captured at +33 bytes from entry
    // 9004 calls, 160 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 3
     * Function: updateScreen (0x8EDD)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 0
     * Memory reads: 3, writes: 0
     */
    @Test
    fun `updateScreen_frame3_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (3 addresses)
        memory[0x0000] = 0x01u
        memory[0x0001] = 0x03u
        memory[0x0301] = 0x00u

        // Execute decompiled function
        updateScreen()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 1 from frame 25
     * Function: updateScreen (0x8EDD)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 0
     * Memory reads: 6, writes: 5
     */
    @Test
    fun `updateScreen_frame25_test1`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (6 addresses)
        memory[0x0000] = 0xC8u
        memory[0x0001] = 0x8Cu
        memory[0x01F8] = 0xABu
        memory[0x01F9] = 0x8Eu
        memory[0x01FA] = 0x40u
        memory[0x0778] = 0x10u

        // Execute decompiled function
        updateScreen()

        // Verify: Check output memory (2 addresses)
        assertEquals(0xEBu, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x8Cu, memory[0x0001], "Memory 0x0001 mismatch")
    }

    /**
     * Test case 2 from frame 1159
     * Function: updateScreen (0x8EDD)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 0
     * Memory reads: 35, writes: 5
     */
    @Test
    fun `updateScreen_frame1159_test2`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (35 addresses)
        memory[0x0000] = 0x41u
        memory[0x0001] = 0x03u
        memory[0x01F8] = 0xABu
        memory[0x01F9] = 0x8Eu
        memory[0x01FA] = 0x02u
        memory[0x0341] = 0x27u
        memory[0x0342] = 0xCCu
        memory[0x0343] = 0x01u
        memory[0x0344] = 0x00u
        memory[0x0345] = 0x27u
        memory[0x0346] = 0xD4u
        memory[0x0347] = 0x01u
        memory[0x0348] = 0x00u
        memory[0x0349] = 0x27u
        memory[0x034A] = 0xDCu
        memory[0x034B] = 0x01u
        memory[0x034C] = 0x00u
        memory[0x034D] = 0x27u
        memory[0x034E] = 0xE4u
        memory[0x034F] = 0x01u
        memory[0x0350] = 0x10u
        memory[0x0351] = 0x27u
        memory[0x0352] = 0xECu
        memory[0x0353] = 0x01u
        memory[0x0354] = 0x00u
        memory[0x0355] = 0x27u
        memory[0x0356] = 0xF4u
        memory[0x0357] = 0x01u
        memory[0x0358] = 0x50u
        memory[0x0359] = 0x27u
        memory[0x035A] = 0xFCu
        memory[0x035B] = 0x01u
        memory[0x035C] = 0x05u
        memory[0x035D] = 0x00u
        memory[0x0778] = 0x14u

        // Execute decompiled function
        updateScreen()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x5Du, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x03u, memory[0x0001], "Memory 0x0001 mismatch")
    }

    /**
     * Test case 3 from frame 1307
     * Function: updateScreen (0x8EDD)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 0
     * Memory reads: 35, writes: 5
     */
    @Test
    fun `updateScreen_frame1307_test3`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (35 addresses)
        memory[0x0000] = 0x41u
        memory[0x0001] = 0x03u
        memory[0x01F8] = 0xABu
        memory[0x01F9] = 0x8Eu
        memory[0x01FA] = 0x02u
        memory[0x0341] = 0x27u
        memory[0x0342] = 0xCFu
        memory[0x0343] = 0x01u
        memory[0x0344] = 0xA0u
        memory[0x0345] = 0x27u
        memory[0x0346] = 0xD7u
        memory[0x0347] = 0x01u
        memory[0x0348] = 0x0Au
        memory[0x0349] = 0x27u
        memory[0x034A] = 0xDFu
        memory[0x034B] = 0x01u
        memory[0x034C] = 0x00u
        memory[0x034D] = 0x27u
        memory[0x034E] = 0xE7u
        memory[0x034F] = 0x01u
        memory[0x0350] = 0x00u
        memory[0x0351] = 0x27u
        memory[0x0352] = 0xEFu
        memory[0x0353] = 0x01u
        memory[0x0354] = 0x00u
        memory[0x0355] = 0x27u
        memory[0x0356] = 0xF7u
        memory[0x0357] = 0x01u
        memory[0x0358] = 0x50u
        memory[0x0359] = 0x27u
        memory[0x035A] = 0xFFu
        memory[0x035B] = 0x01u
        memory[0x035C] = 0x05u
        memory[0x035D] = 0x00u
        memory[0x0778] = 0x14u

        // Execute decompiled function
        updateScreen()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x5Du, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x03u, memory[0x0001], "Memory 0x0001 mismatch")
    }

    /**
     * Test case 4 from frame 1491
     * Function: updateScreen (0x8EDD)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 0
     * Memory reads: 36, writes: 5
     */
    @Test
    fun `updateScreen_frame1491_test4`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (36 addresses)
        memory[0x0000] = 0x41u
        memory[0x0001] = 0x03u
        memory[0x01F8] = 0xABu
        memory[0x01F9] = 0x8Eu
        memory[0x01FA] = 0x34u
        memory[0x0341] = 0x20u
        memory[0x0342] = 0x89u
        memory[0x0343] = 0x9Au
        memory[0x0344] = 0x24u
        memory[0x0345] = 0x35u
        memory[0x0346] = 0x39u
        memory[0x0347] = 0x24u
        memory[0x0348] = 0x24u
        memory[0x0349] = 0x24u
        memory[0x034A] = 0x24u
        memory[0x034B] = 0x24u
        memory[0x034C] = 0x24u
        memory[0x034D] = 0x24u
        memory[0x034E] = 0x24u
        memory[0x034F] = 0x24u
        memory[0x0350] = 0x24u
        memory[0x0351] = 0x24u
        memory[0x0352] = 0x24u
        memory[0x0353] = 0x24u
        memory[0x0354] = 0x24u
        memory[0x0355] = 0x24u
        memory[0x0356] = 0x24u
        memory[0x0357] = 0x24u
        memory[0x0358] = 0x24u
        memory[0x0359] = 0x24u
        memory[0x035A] = 0xB5u
        memory[0x035B] = 0xB7u
        memory[0x035C] = 0xB5u
        memory[0x035D] = 0xB7u
        memory[0x035E] = 0x00u
        memory[0x0778] = 0x14u

        // Execute decompiled function
        updateScreen()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x5Eu, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x03u, memory[0x0001], "Memory 0x0001 mismatch")
    }

    /**
     * Test case 5 from frame 1577
     * Function: updateScreen (0x8EDD)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 0
     * Memory reads: 36, writes: 5
     */
    @Test
    fun `updateScreen_frame1577_test5`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (36 addresses)
        memory[0x0000] = 0x41u
        memory[0x0001] = 0x03u
        memory[0x01F8] = 0xABu
        memory[0x01F9] = 0x8Eu
        memory[0x01FA] = 0x34u
        memory[0x0341] = 0x20u
        memory[0x0342] = 0x93u
        memory[0x0343] = 0x9Au
        memory[0x0344] = 0x24u
        memory[0x0345] = 0x24u
        memory[0x0346] = 0x24u
        memory[0x0347] = 0x24u
        memory[0x0348] = 0x24u
        memory[0x0349] = 0x24u
        memory[0x034A] = 0x24u
        memory[0x034B] = 0x24u
        memory[0x034C] = 0x24u
        memory[0x034D] = 0x24u
        memory[0x034E] = 0x24u
        memory[0x034F] = 0x24u
        memory[0x0350] = 0x24u
        memory[0x0351] = 0x24u
        memory[0x0352] = 0x24u
        memory[0x0353] = 0x24u
        memory[0x0354] = 0x24u
        memory[0x0355] = 0x24u
        memory[0x0356] = 0x24u
        memory[0x0357] = 0x24u
        memory[0x0358] = 0x24u
        memory[0x0359] = 0x35u
        memory[0x035A] = 0xB5u
        memory[0x035B] = 0xB7u
        memory[0x035C] = 0xB5u
        memory[0x035D] = 0xB7u
        memory[0x035E] = 0x00u
        memory[0x0778] = 0x15u

        // Execute decompiled function
        updateScreen()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x5Eu, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x03u, memory[0x0001], "Memory 0x0001 mismatch")
    }

    /**
     * Test case 6 from frame 2293
     * Function: updateScreen (0x8EDD)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 0
     * Memory reads: 36, writes: 5
     */
    @Test
    fun `updateScreen_frame2293_test6`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (36 addresses)
        memory[0x0000] = 0x41u
        memory[0x0001] = 0x03u
        memory[0x01F8] = 0xABu
        memory[0x01F9] = 0x8Eu
        memory[0x01FA] = 0x34u
        memory[0x0341] = 0x20u
        memory[0x0342] = 0x9Du
        memory[0x0343] = 0x9Au
        memory[0x0344] = 0x24u
        memory[0x0345] = 0x24u
        memory[0x0346] = 0x24u
        memory[0x0347] = 0x24u
        memory[0x0348] = 0x24u
        memory[0x0349] = 0x24u
        memory[0x034A] = 0x24u
        memory[0x034B] = 0x24u
        memory[0x034C] = 0x24u
        memory[0x034D] = 0x24u
        memory[0x034E] = 0x24u
        memory[0x034F] = 0x24u
        memory[0x0350] = 0x24u
        memory[0x0351] = 0x24u
        memory[0x0352] = 0x61u
        memory[0x0353] = 0x65u
        memory[0x0354] = 0x69u
        memory[0x0355] = 0x69u
        memory[0x0356] = 0x69u
        memory[0x0357] = 0x69u
        memory[0x0358] = 0x69u
        memory[0x0359] = 0x69u
        memory[0x035A] = 0xB5u
        memory[0x035B] = 0xB7u
        memory[0x035C] = 0xB5u
        memory[0x035D] = 0xB7u
        memory[0x035E] = 0x00u
        memory[0x0778] = 0x15u

        // Execute decompiled function
        updateScreen()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x5Eu, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x03u, memory[0x0001], "Memory 0x0001 mismatch")
    }

    /**
     * Test case 7 from frame 2429
     * Function: updateScreen (0x8EDD)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 0
     * Memory reads: 14, writes: 5
     */
    @Test
    fun `updateScreen_frame2429_test7`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (14 addresses)
        memory[0x0000] = 0x01u
        memory[0x0001] = 0x03u
        memory[0x01F8] = 0xABu
        memory[0x01F9] = 0x8Eu
        memory[0x01FA] = 0x08u
        memory[0x0301] = 0x3Fu
        memory[0x0302] = 0x0Cu
        memory[0x0303] = 0x04u
        memory[0x0304] = 0x0Fu
        memory[0x0305] = 0x17u
        memory[0x0306] = 0x17u
        memory[0x0307] = 0x0Fu
        memory[0x0308] = 0x00u
        memory[0x0778] = 0x15u

        // Execute decompiled function
        updateScreen()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x08u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x03u, memory[0x0001], "Memory 0x0001 mismatch")
    }

    /**
     * Test case 8 from frame 4505
     * Function: updateScreen (0x8EDD)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 0
     * Memory reads: 36, writes: 5
     */
    @Test
    fun `updateScreen_frame4505_test8`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (36 addresses)
        memory[0x0000] = 0x41u
        memory[0x0001] = 0x03u
        memory[0x01F8] = 0xABu
        memory[0x01F9] = 0x8Eu
        memory[0x01FA] = 0x34u
        memory[0x0341] = 0x24u
        memory[0x0342] = 0x9Eu
        memory[0x0343] = 0x9Au
        memory[0x0344] = 0x24u
        memory[0x0345] = 0x24u
        memory[0x0346] = 0x24u
        memory[0x0347] = 0x38u
        memory[0x0348] = 0x3Cu
        memory[0x0349] = 0x24u
        memory[0x034A] = 0x24u
        memory[0x034B] = 0x24u
        memory[0x034C] = 0x24u
        memory[0x034D] = 0x24u
        memory[0x034E] = 0x24u
        memory[0x034F] = 0x24u
        memory[0x0350] = 0x24u
        memory[0x0351] = 0x24u
        memory[0x0352] = 0x24u
        memory[0x0353] = 0x24u
        memory[0x0354] = 0x24u
        memory[0x0355] = 0x24u
        memory[0x0356] = 0x24u
        memory[0x0357] = 0x24u
        memory[0x0358] = 0x24u
        memory[0x0359] = 0x24u
        memory[0x035A] = 0xB4u
        memory[0x035B] = 0xB6u
        memory[0x035C] = 0xB4u
        memory[0x035D] = 0xB6u
        memory[0x035E] = 0x00u
        memory[0x0778] = 0x10u

        // Execute decompiled function
        updateScreen()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x5Eu, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x03u, memory[0x0001], "Memory 0x0001 mismatch")
    }

    /**
     * Test case 9 from frame 5601
     * Function: updateScreen (0x8EDD)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 0
     * Memory reads: 36, writes: 5
     */
    @Test
    fun `updateScreen_frame5601_test9`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (36 addresses)
        memory[0x0000] = 0x41u
        memory[0x0001] = 0x03u
        memory[0x01F8] = 0xABu
        memory[0x01F9] = 0x8Eu
        memory[0x01FA] = 0x34u
        memory[0x0341] = 0x24u
        memory[0x0342] = 0x99u
        memory[0x0343] = 0x9Au
        memory[0x0344] = 0x24u
        memory[0x0345] = 0x24u
        memory[0x0346] = 0x24u
        memory[0x0347] = 0x24u
        memory[0x0348] = 0x24u
        memory[0x0349] = 0x24u
        memory[0x034A] = 0x24u
        memory[0x034B] = 0x24u
        memory[0x034C] = 0x24u
        memory[0x034D] = 0x24u
        memory[0x034E] = 0x24u
        memory[0x034F] = 0x24u
        memory[0x0350] = 0x24u
        memory[0x0351] = 0x24u
        memory[0x0352] = 0x24u
        memory[0x0353] = 0x24u
        memory[0x0354] = 0x24u
        memory[0x0355] = 0x24u
        memory[0x0356] = 0x24u
        memory[0x0357] = 0x24u
        memory[0x0358] = 0x37u
        memory[0x0359] = 0x25u
        memory[0x035A] = 0xB5u
        memory[0x035B] = 0xB7u
        memory[0x035C] = 0xB5u
        memory[0x035D] = 0xB7u
        memory[0x035E] = 0x00u
        memory[0x0778] = 0x14u

        // Execute decompiled function
        updateScreen()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x5Eu, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x03u, memory[0x0001], "Memory 0x0001 mismatch")
    }

    // =========================================
    // 0x8F97: updateTopScore
    // FUZZY MATCH: captured at +33 bytes from entry
    // 9004 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 3
     * Function: updateTopScore (0x8F97)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 0
     * Memory reads: 14, writes: 8
     */
    @Test
    fun `updateTopScore_frame3_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (14 addresses)
        memory[0x01F9] = 0x9Bu
        memory[0x01FA] = 0x8Fu
        memory[0x07D7] = 0x00u
        memory[0x07D8] = 0x00u
        memory[0x07D9] = 0x00u
        memory[0x07DA] = 0x00u
        memory[0x07DB] = 0x00u
        memory[0x07DC] = 0x00u
        memory[0x07E3] = 0x00u
        memory[0x07E4] = 0x00u
        memory[0x07E5] = 0x00u
        memory[0x07E6] = 0x00u
        memory[0x07E7] = 0x00u
        memory[0x07E8] = 0x00u

        // Execute decompiled function
        updateTopScore()

        // Verify: Check output memory (6 addresses)
        assertEquals(0x00u, memory[0x07D7], "Memory 0x07D7 mismatch")
        assertEquals(0x00u, memory[0x07D8], "Memory 0x07D8 mismatch")
        assertEquals(0x00u, memory[0x07D9], "Memory 0x07D9 mismatch")
        assertEquals(0x00u, memory[0x07DA], "Memory 0x07DA mismatch")
        assertEquals(0x00u, memory[0x07DB], "Memory 0x07DB mismatch")
        assertEquals(0x00u, memory[0x07DC], "Memory 0x07DC mismatch")
    }

    // =========================================
    // 0x90ED: getAreaMusic
    // FUZZY MATCH: captured at +33 bytes from entry
    // 6 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 31
     * Function: getAreaMusic (0x90ED)
     * FUZZY MATCH: entry +33 bytes
     * Call depth: 3
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `getAreaMusic_frame31_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x0770] = 0x00u

        // Execute decompiled function
        getAreaMusic()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    // =========================================
    // 0x92AA: areaParserTasks
    // FUZZY MATCH: captured at +6 bytes from entry
    // 6 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 31
     * Function: areaParserTasks (0x92AA)
     * FUZZY MATCH: entry +6 bytes
     * Call depth: 3
     * Memory reads: 0, writes: 1
     */
    @Test
    fun `areaParserTasks_frame31_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (0 addresses)
        // No memory inputs

        // Execute decompiled function
        areaParserTasks()

        // Verify: Check output memory (1 addresses)
        assertEquals(0xFFu, memory[0x06C9], "Memory 0x06C9 mismatch")
    }

    // =========================================
    // 0x92AF: incrementColumnPos
    // 6 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 31
     * Function: incrementColumnPos (0x92AF)
     * Call depth: 3
     * Memory reads: 0, writes: 0
     */
    @Test
    fun `incrementColumnPos_frame31_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (0 addresses)
        // No memory inputs

        // Execute decompiled function
        incrementColumnPos()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    // =========================================
    // 0x9508: processAreaData
    // FUZZY MATCH: captured at +44 bytes from entry
    // 350 calls, 25 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 13
     * Function: processAreaData (0x9508)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 7
     * Memory reads: 14, writes: 6
     */
    @Test
    fun `processAreaData_frame13_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (14 addresses)
        memory[0x0008] = 0x02u
        memory[0x00E7] = 0x90u
        memory[0x00E8] = 0xA6u
        memory[0x01F2] = 0x67u
        memory[0x01F3] = 0x95u
        memory[0x0725] = 0x00u
        memory[0x0728] = 0x00u
        memory[0x0729] = 0x00u
        memory[0x072A] = 0x00u
        memory[0x072B] = 0x00u
        memory[0x072C] = 0x00u
        memory[0x0730] = 0xFFu
        memory[0x0731] = 0xFFu
        memory[0x0732] = 0xFFu

        // Execute decompiled function
        processAreaData()

        // Verify: Check output memory (4 addresses)
        assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
        assertEquals(0x00u, memory[0x0729], "Memory 0x0729 mismatch")
        assertEquals(0x01u, memory[0x072A], "Memory 0x072A mismatch")
        assertEquals(0x01u, memory[0x072B], "Memory 0x072B mismatch")
    }

    /**
     * Test case 1 from frame 21
     * Function: processAreaData (0x9508)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 7
     * Memory reads: 26, writes: 20
     */
    @Test
    fun `processAreaData_frame21_test1`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (26 addresses)
        memory[0x0000] = 0x00u
        memory[0x0006] = 0xD0u
        memory[0x0007] = 0x05u
        memory[0x00E7] = 0x90u
        memory[0x00E8] = 0xA6u
        memory[0x01F4] = 0xD5u
        memory[0x01F5] = 0x94u
        memory[0x06A0] = 0x10u
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
        memory[0x06AB] = 0x05u
        memory[0x06AC] = 0x54u
        memory[0x06AD] = 0x54u
        memory[0x0725] = 0x01u
        memory[0x072A] = 0x01u
        memory[0x072B] = 0x01u
        memory[0x072C] = 0x00u
        memory[0x0732] = 0xFFu

        // Execute decompiled function
        processAreaData()

        // Verify: Check output memory (16 addresses)
        assertEquals(0xC0u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x02u, memory[0x0008], "Memory 0x0008 mismatch")
        assertEquals(0x00u, memory[0x05D0], "Memory 0x05D0 mismatch")
        assertEquals(0x00u, memory[0x05E0], "Memory 0x05E0 mismatch")
        assertEquals(0x00u, memory[0x05F0], "Memory 0x05F0 mismatch")
        assertEquals(0x00u, memory[0x0600], "Memory 0x0600 mismatch")
        assertEquals(0x00u, memory[0x0610], "Memory 0x0610 mismatch")
        assertEquals(0x00u, memory[0x0620], "Memory 0x0620 mismatch")
        assertEquals(0x00u, memory[0x0630], "Memory 0x0630 mismatch")
        assertEquals(0xC0u, memory[0x0640], "Memory 0x0640 mismatch")
        assertEquals(0x00u, memory[0x0650], "Memory 0x0650 mismatch")
        assertEquals(0x00u, memory[0x0660], "Memory 0x0660 mismatch")
        assertEquals(0x00u, memory[0x0670], "Memory 0x0670 mismatch")
        assertEquals(0x54u, memory[0x0680], "Memory 0x0680 mismatch")
        assertEquals(0x54u, memory[0x0690], "Memory 0x0690 mismatch")
        assertEquals(0x00u, memory[0x0729], "Memory 0x0729 mismatch")
    }

    /**
     * Test case 2 from frame 23
     * Function: processAreaData (0x9508)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 12
     * Memory reads: 7, writes: 5
     */
    @Test
    fun `processAreaData_frame23_test2`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (7 addresses)
        memory[0x00E7] = 0x90u
        memory[0x00E8] = 0xA6u
        memory[0x01F6] = 0xBEu
        memory[0x01F7] = 0x92u
        memory[0x071F] = 0x04u
        memory[0x072C] = 0x04u
        memory[0x0732] = 0x03u

        // Execute decompiled function
        processAreaData()

        // Verify: Check output memory (3 addresses)
        assertEquals(0x02u, memory[0x0008], "Memory 0x0008 mismatch")
        assertEquals(0x03u, memory[0x071F], "Memory 0x071F mismatch")
        assertEquals(0x00u, memory[0x0729], "Memory 0x0729 mismatch")
    }

    /**
     * Test case 3 from frame 24
     * Function: processAreaData (0x9508)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 13
     * Memory reads: 7, writes: 5
     */
    @Test
    fun `processAreaData_frame24_test3`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (7 addresses)
        memory[0x00E7] = 0x90u
        memory[0x00E8] = 0xA6u
        memory[0x01F6] = 0xBEu
        memory[0x01F7] = 0x92u
        memory[0x071F] = 0x04u
        memory[0x072C] = 0x08u
        memory[0x0732] = 0x01u

        // Execute decompiled function
        processAreaData()

        // Verify: Check output memory (3 addresses)
        assertEquals(0x02u, memory[0x0008], "Memory 0x0008 mismatch")
        assertEquals(0x03u, memory[0x071F], "Memory 0x071F mismatch")
        assertEquals(0x00u, memory[0x0729], "Memory 0x0729 mismatch")
    }

    /**
     * Test case 4 from frame 1251
     * Function: processAreaData (0x9508)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 11
     * Memory reads: 25, writes: 20
     */
    @Test
    fun `processAreaData_frame1251_test4`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (25 addresses)
        memory[0x0000] = 0x00u
        memory[0x0006] = 0xDCu
        memory[0x0007] = 0x05u
        memory[0x00E7] = 0x90u
        memory[0x00E8] = 0xA6u
        memory[0x01F2] = 0xD5u
        memory[0x01F3] = 0x94u
        memory[0x06A0] = 0x1Cu
        memory[0x06A1] = 0x00u
        memory[0x06A2] = 0x81u
        memory[0x06A3] = 0x84u
        memory[0x06A4] = 0x00u
        memory[0x06A5] = 0x00u
        memory[0x06A6] = 0x00u
        memory[0x06A7] = 0x00u
        memory[0x06A8] = 0x00u
        memory[0x06A9] = 0x00u
        memory[0x06AA] = 0x12u
        memory[0x06AB] = 0x14u
        memory[0x06AC] = 0x54u
        memory[0x06AD] = 0x54u
        memory[0x0725] = 0x01u
        memory[0x072A] = 0x01u
        memory[0x072C] = 0x0Au
        memory[0x0732] = 0xFFu

        // Execute decompiled function
        processAreaData()

        // Verify: Check output memory (16 addresses)
        assertEquals(0xC0u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x02u, memory[0x0008], "Memory 0x0008 mismatch")
        assertEquals(0x00u, memory[0x05DC], "Memory 0x05DC mismatch")
        assertEquals(0x00u, memory[0x05EC], "Memory 0x05EC mismatch")
        assertEquals(0x00u, memory[0x05FC], "Memory 0x05FC mismatch")
        assertEquals(0x00u, memory[0x060C], "Memory 0x060C mismatch")
        assertEquals(0x00u, memory[0x061C], "Memory 0x061C mismatch")
        assertEquals(0x00u, memory[0x062C], "Memory 0x062C mismatch")
        assertEquals(0x00u, memory[0x063C], "Memory 0x063C mismatch")
        assertEquals(0x00u, memory[0x064C], "Memory 0x064C mismatch")
        assertEquals(0x00u, memory[0x065C], "Memory 0x065C mismatch")
        assertEquals(0x12u, memory[0x066C], "Memory 0x066C mismatch")
        assertEquals(0x14u, memory[0x067C], "Memory 0x067C mismatch")
        assertEquals(0x54u, memory[0x068C], "Memory 0x068C mismatch")
        assertEquals(0x54u, memory[0x069C], "Memory 0x069C mismatch")
        assertEquals(0x00u, memory[0x0729], "Memory 0x0729 mismatch")
    }

    /**
     * Test case 5 from frame 1293
     * Function: processAreaData (0x9508)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 11
     * Memory reads: 14, writes: 4
     */
    @Test
    fun `processAreaData_frame1293_test5`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (14 addresses)
        memory[0x0008] = 0x02u
        memory[0x00E7] = 0x90u
        memory[0x00E8] = 0xA6u
        memory[0x01F0] = 0x67u
        memory[0x01F1] = 0x95u
        memory[0x0725] = 0x01u
        memory[0x0728] = 0x00u
        memory[0x0729] = 0x00u
        memory[0x072A] = 0x02u
        memory[0x072B] = 0x01u
        memory[0x072C] = 0x0Cu
        memory[0x0730] = 0xFFu
        memory[0x0731] = 0xFFu
        memory[0x0732] = 0xFFu

        // Execute decompiled function
        processAreaData()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
        assertEquals(0x00u, memory[0x0729], "Memory 0x0729 mismatch")
    }

    /**
     * Test case 6 from frame 1531
     * Function: processAreaData (0x9508)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 10
     * Memory reads: 23, writes: 20
     */
    @Test
    fun `processAreaData_frame1531_test6`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (23 addresses)
        memory[0x0000] = 0x00u
        memory[0x0006] = 0x07u
        memory[0x0007] = 0x05u
        memory[0x00E7] = 0x90u
        memory[0x00E8] = 0xA6u
        memory[0x01F2] = 0xD5u
        memory[0x01F3] = 0x94u
        memory[0x06A0] = 0x07u
        memory[0x06A1] = 0x82u
        memory[0x06A2] = 0x85u
        memory[0x06A3] = 0x00u
        memory[0x06A4] = 0x00u
        memory[0x06A5] = 0x00u
        memory[0x06A6] = 0x00u
        memory[0x06A7] = 0x00u
        memory[0x06A8] = 0x00u
        memory[0x06A9] = 0x13u
        memory[0x06AA] = 0x15u
        memory[0x06AB] = 0x15u
        memory[0x06AC] = 0x54u
        memory[0x06AD] = 0x54u
        memory[0x072C] = 0x0Eu
        memory[0x0732] = 0x00u

        // Execute decompiled function
        processAreaData()

        // Verify: Check output memory (16 addresses)
        assertEquals(0xC0u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x02u, memory[0x0008], "Memory 0x0008 mismatch")
        assertEquals(0x00u, memory[0x0507], "Memory 0x0507 mismatch")
        assertEquals(0x00u, memory[0x0517], "Memory 0x0517 mismatch")
        assertEquals(0x00u, memory[0x0527], "Memory 0x0527 mismatch")
        assertEquals(0x00u, memory[0x0537], "Memory 0x0537 mismatch")
        assertEquals(0x00u, memory[0x0547], "Memory 0x0547 mismatch")
        assertEquals(0x00u, memory[0x0557], "Memory 0x0557 mismatch")
        assertEquals(0x00u, memory[0x0567], "Memory 0x0567 mismatch")
        assertEquals(0x00u, memory[0x0577], "Memory 0x0577 mismatch")
        assertEquals(0x13u, memory[0x0587], "Memory 0x0587 mismatch")
        assertEquals(0x15u, memory[0x0597], "Memory 0x0597 mismatch")
        assertEquals(0x15u, memory[0x05A7], "Memory 0x05A7 mismatch")
        assertEquals(0x54u, memory[0x05B7], "Memory 0x05B7 mismatch")
        assertEquals(0x54u, memory[0x05C7], "Memory 0x05C7 mismatch")
        assertEquals(0x00u, memory[0x0729], "Memory 0x0729 mismatch")
    }

    /**
     * Test case 7 from frame 2289
     * Function: processAreaData (0x9508)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 9
     * Memory reads: 25, writes: 20
     */
    @Test
    fun `processAreaData_frame2289_test7`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (25 addresses)
        memory[0x0000] = 0x00u
        memory[0x0006] = 0x0Eu
        memory[0x0007] = 0x05u
        memory[0x00E7] = 0x90u
        memory[0x00E8] = 0xA6u
        memory[0x01F2] = 0xD5u
        memory[0x01F3] = 0x94u
        memory[0x06A0] = 0x0Eu
        memory[0x06A1] = 0x00u
        memory[0x06A2] = 0x00u
        memory[0x06A3] = 0x00u
        memory[0x06A4] = 0x00u
        memory[0x06A5] = 0x00u
        memory[0x06A6] = 0x00u
        memory[0x06A7] = 0x00u
        memory[0x06A8] = 0x12u
        memory[0x06A9] = 0x14u
        memory[0x06AA] = 0x14u
        memory[0x06AB] = 0x14u
        memory[0x06AC] = 0x54u
        memory[0x06AD] = 0x54u
        memory[0x0725] = 0x02u
        memory[0x072A] = 0x02u
        memory[0x072C] = 0x0Eu
        memory[0x0732] = 0xFFu

        // Execute decompiled function
        processAreaData()

        // Verify: Check output memory (16 addresses)
        assertEquals(0xC0u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x02u, memory[0x0008], "Memory 0x0008 mismatch")
        assertEquals(0x00u, memory[0x050E], "Memory 0x050E mismatch")
        assertEquals(0x00u, memory[0x051E], "Memory 0x051E mismatch")
        assertEquals(0x00u, memory[0x052E], "Memory 0x052E mismatch")
        assertEquals(0x00u, memory[0x053E], "Memory 0x053E mismatch")
        assertEquals(0x00u, memory[0x054E], "Memory 0x054E mismatch")
        assertEquals(0x00u, memory[0x055E], "Memory 0x055E mismatch")
        assertEquals(0x00u, memory[0x056E], "Memory 0x056E mismatch")
        assertEquals(0x12u, memory[0x057E], "Memory 0x057E mismatch")
        assertEquals(0x14u, memory[0x058E], "Memory 0x058E mismatch")
        assertEquals(0x14u, memory[0x059E], "Memory 0x059E mismatch")
        assertEquals(0x14u, memory[0x05AE], "Memory 0x05AE mismatch")
        assertEquals(0x54u, memory[0x05BE], "Memory 0x05BE mismatch")
        assertEquals(0x54u, memory[0x05CE], "Memory 0x05CE mismatch")
        assertEquals(0x00u, memory[0x0729], "Memory 0x0729 mismatch")
    }

    /**
     * Test case 8 from frame 2607
     * Function: processAreaData (0x9508)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 9
     * Memory reads: 26, writes: 20
     */
    @Test
    fun `processAreaData_frame2607_test8`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (26 addresses)
        memory[0x0000] = 0x00u
        memory[0x0006] = 0xD9u
        memory[0x0007] = 0x05u
        memory[0x00E7] = 0x90u
        memory[0x00E8] = 0xA6u
        memory[0x01F2] = 0xD5u
        memory[0x01F3] = 0x94u
        memory[0x06A0] = 0x19u
        memory[0x06A1] = 0x00u
        memory[0x06A2] = 0x81u
        memory[0x06A3] = 0x84u
        memory[0x06A4] = 0x00u
        memory[0x06A5] = 0x00u
        memory[0x06A6] = 0x00u
        memory[0x06A7] = 0x00u
        memory[0x06A8] = 0x10u
        memory[0x06A9] = 0x14u
        memory[0x06AA] = 0x14u
        memory[0x06AB] = 0x14u
        memory[0x06AC] = 0x54u
        memory[0x06AD] = 0x54u
        memory[0x0725] = 0x03u
        memory[0x072A] = 0x03u
        memory[0x072B] = 0x01u
        memory[0x072C] = 0x10u
        memory[0x0732] = 0xFFu

        // Execute decompiled function
        processAreaData()

        // Verify: Check output memory (16 addresses)
        assertEquals(0xC0u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x02u, memory[0x0008], "Memory 0x0008 mismatch")
        assertEquals(0x00u, memory[0x05D9], "Memory 0x05D9 mismatch")
        assertEquals(0x00u, memory[0x05E9], "Memory 0x05E9 mismatch")
        assertEquals(0x00u, memory[0x05F9], "Memory 0x05F9 mismatch")
        assertEquals(0x00u, memory[0x0609], "Memory 0x0609 mismatch")
        assertEquals(0x00u, memory[0x0619], "Memory 0x0619 mismatch")
        assertEquals(0x00u, memory[0x0629], "Memory 0x0629 mismatch")
        assertEquals(0x00u, memory[0x0639], "Memory 0x0639 mismatch")
        assertEquals(0x10u, memory[0x0649], "Memory 0x0649 mismatch")
        assertEquals(0x14u, memory[0x0659], "Memory 0x0659 mismatch")
        assertEquals(0x14u, memory[0x0669], "Memory 0x0669 mismatch")
        assertEquals(0x14u, memory[0x0679], "Memory 0x0679 mismatch")
        assertEquals(0x54u, memory[0x0689], "Memory 0x0689 mismatch")
        assertEquals(0x54u, memory[0x0699], "Memory 0x0699 mismatch")
        assertEquals(0x00u, memory[0x0729], "Memory 0x0729 mismatch")
    }

    /**
     * Test case 9 from frame 5521
     * Function: processAreaData (0x9508)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 11
     * Memory reads: 14, writes: 4
     */
    @Test
    fun `processAreaData_frame5521_test9`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (14 addresses)
        memory[0x0008] = 0x02u
        memory[0x00E7] = 0x90u
        memory[0x00E8] = 0xA6u
        memory[0x01F0] = 0x67u
        memory[0x01F1] = 0x95u
        memory[0x0725] = 0x03u
        memory[0x0728] = 0x00u
        memory[0x0729] = 0x00u
        memory[0x072A] = 0x04u
        memory[0x072B] = 0x01u
        memory[0x072C] = 0x12u
        memory[0x0730] = 0xFFu
        memory[0x0731] = 0xFFu
        memory[0x0732] = 0xFFu

        // Execute decompiled function
        processAreaData()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x0008], "Memory 0x0008 mismatch")
        assertEquals(0x00u, memory[0x0729], "Memory 0x0729 mismatch")
    }

    // =========================================
    // 0x9589: incAreaObjOffset
    // FUZZY MATCH: captured at +44 bytes from entry
    // 54 calls, 10 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 21
     * Function: incAreaObjOffset (0x9589)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 9
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `incAreaObjOffset_frame21_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x072C] = 0x00u

        // Execute decompiled function
        incAreaObjOffset()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x072B], "Memory 0x072B mismatch")
        assertEquals(0x02u, memory[0x072C], "Memory 0x072C mismatch")
    }

    /**
     * Test case 1 from frame 23
     * Function: incAreaObjOffset (0x9589)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 9
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `incAreaObjOffset_frame23_test1`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x072C] = 0x02u

        // Execute decompiled function
        incAreaObjOffset()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x072B], "Memory 0x072B mismatch")
        assertEquals(0x04u, memory[0x072C], "Memory 0x072C mismatch")
    }

    /**
     * Test case 2 from frame 23
     * Function: incAreaObjOffset (0x9589)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 15
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `incAreaObjOffset_frame23_test2`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x072C] = 0x04u

        // Execute decompiled function
        incAreaObjOffset()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x072B], "Memory 0x072B mismatch")
        assertEquals(0x06u, memory[0x072C], "Memory 0x072C mismatch")
    }

    /**
     * Test case 3 from frame 24
     * Function: incAreaObjOffset (0x9589)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 10
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `incAreaObjOffset_frame24_test3`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x072C] = 0x06u

        // Execute decompiled function
        incAreaObjOffset()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x072B], "Memory 0x072B mismatch")
        assertEquals(0x08u, memory[0x072C], "Memory 0x072C mismatch")
    }

    /**
     * Test case 4 from frame 24
     * Function: incAreaObjOffset (0x9589)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 16
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `incAreaObjOffset_frame24_test4`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x072C] = 0x08u

        // Execute decompiled function
        incAreaObjOffset()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x072B], "Memory 0x072B mismatch")
        assertEquals(0x0Au, memory[0x072C], "Memory 0x072C mismatch")
    }

    /**
     * Test case 5 from frame 1251
     * Function: incAreaObjOffset (0x9589)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 13
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `incAreaObjOffset_frame1251_test5`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x072C] = 0x0Au

        // Execute decompiled function
        incAreaObjOffset()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x072B], "Memory 0x072B mismatch")
        assertEquals(0x0Cu, memory[0x072C], "Memory 0x072C mismatch")
    }

    /**
     * Test case 6 from frame 1523
     * Function: incAreaObjOffset (0x9589)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 12
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `incAreaObjOffset_frame1523_test6`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x072C] = 0x0Cu

        // Execute decompiled function
        incAreaObjOffset()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x072B], "Memory 0x072B mismatch")
        assertEquals(0x0Eu, memory[0x072C], "Memory 0x072C mismatch")
    }

    /**
     * Test case 7 from frame 2289
     * Function: incAreaObjOffset (0x9589)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 11
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `incAreaObjOffset_frame2289_test7`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x072C] = 0x0Eu

        // Execute decompiled function
        incAreaObjOffset()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x072B], "Memory 0x072B mismatch")
        assertEquals(0x10u, memory[0x072C], "Memory 0x072C mismatch")
    }

    /**
     * Test case 8 from frame 2607
     * Function: incAreaObjOffset (0x9589)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 11
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `incAreaObjOffset_frame2607_test8`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x072C] = 0x10u

        // Execute decompiled function
        incAreaObjOffset()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x072B], "Memory 0x072B mismatch")
        assertEquals(0x12u, memory[0x072C], "Memory 0x072C mismatch")
    }

    /**
     * Test case 9 from frame 5821
     * Function: incAreaObjOffset (0x9589)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 17
     * Memory reads: 1, writes: 2
     */
    @Test
    fun `incAreaObjOffset_frame5821_test9`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x072C] = 0x12u

        // Execute decompiled function
        incAreaObjOffset()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x072B], "Memory 0x072B mismatch")
        assertEquals(0x14u, memory[0x072C], "Memory 0x072C mismatch")
    }

    // =========================================
    // 0x9BBB: findAreaPointer
    // FUZZY MATCH: captured at +7 bytes from entry
    // 95 calls, 14 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 21
     * Function: findAreaPointer (0x9BBB)
     * FUZZY MATCH: entry +7 bytes
     * Call depth: 10
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `findAreaPointer_frame21_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (3 addresses)
        memory[0x00E7] = 0x90u
        memory[0x00E8] = 0xA6u
        memory[0x072F] = 0x00u

        // Execute decompiled function
        findAreaPointer()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x07u, memory[0x0007], "Memory 0x0007 mismatch")
    }

    /**
     * Test case 1 from frame 23
     * Function: findAreaPointer (0x9BBB)
     * FUZZY MATCH: entry +7 bytes
     * Call depth: 11
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `findAreaPointer_frame23_test1`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (3 addresses)
        memory[0x00E7] = 0x90u
        memory[0x00E8] = 0xA6u
        memory[0x072F] = 0x02u

        // Execute decompiled function
        findAreaPointer()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x07u, memory[0x0007], "Memory 0x0007 mismatch")
    }

    /**
     * Test case 2 from frame 23
     * Function: findAreaPointer (0x9BBB)
     * FUZZY MATCH: entry +7 bytes
     * Call depth: 16
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `findAreaPointer_frame23_test2`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (3 addresses)
        memory[0x00E7] = 0x90u
        memory[0x00E8] = 0xA6u
        memory[0x072E] = 0x04u

        // Execute decompiled function
        findAreaPointer()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x07u, memory[0x0007], "Memory 0x0007 mismatch")
    }

    /**
     * Test case 3 from frame 24
     * Function: findAreaPointer (0x9BBB)
     * FUZZY MATCH: entry +7 bytes
     * Call depth: 17
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `findAreaPointer_frame24_test3`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (3 addresses)
        memory[0x00E7] = 0x90u
        memory[0x00E8] = 0xA6u
        memory[0x072E] = 0x08u

        // Execute decompiled function
        findAreaPointer()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x07u, memory[0x0007], "Memory 0x0007 mismatch")
    }

    /**
     * Test case 4 from frame 1145
     * Function: findAreaPointer (0x9BBB)
     * FUZZY MATCH: entry +7 bytes
     * Call depth: 13
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `findAreaPointer_frame1145_test4`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (3 addresses)
        memory[0x00E7] = 0x90u
        memory[0x00E8] = 0xA6u
        memory[0x072F] = 0x02u

        // Execute decompiled function
        findAreaPointer()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x07u, memory[0x0007], "Memory 0x0007 mismatch")
    }

    /**
     * Test case 5 from frame 1259
     * Function: findAreaPointer (0x9BBB)
     * FUZZY MATCH: entry +7 bytes
     * Call depth: 15
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `findAreaPointer_frame1259_test5`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (3 addresses)
        memory[0x00E7] = 0x90u
        memory[0x00E8] = 0xA6u
        memory[0x072F] = 0x0Au

        // Execute decompiled function
        findAreaPointer()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x09u, memory[0x0007], "Memory 0x0007 mismatch")
    }

    /**
     * Test case 6 from frame 1523
     * Function: findAreaPointer (0x9BBB)
     * FUZZY MATCH: entry +7 bytes
     * Call depth: 14
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `findAreaPointer_frame1523_test6`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (3 addresses)
        memory[0x00E7] = 0x90u
        memory[0x00E8] = 0xA6u
        memory[0x072F] = 0x0Cu

        // Execute decompiled function
        findAreaPointer()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x08u, memory[0x0007], "Memory 0x0007 mismatch")
    }

    /**
     * Test case 7 from frame 1531
     * Function: findAreaPointer (0x9BBB)
     * FUZZY MATCH: entry +7 bytes
     * Call depth: 14
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `findAreaPointer_frame1531_test7`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (3 addresses)
        memory[0x00E7] = 0x90u
        memory[0x00E8] = 0xA6u
        memory[0x072F] = 0x0Cu

        // Execute decompiled function
        findAreaPointer()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x08u, memory[0x0007], "Memory 0x0007 mismatch")
    }

    /**
     * Test case 8 from frame 2297
     * Function: findAreaPointer (0x9BBB)
     * FUZZY MATCH: entry +7 bytes
     * Call depth: 13
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `findAreaPointer_frame2297_test8`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (3 addresses)
        memory[0x00E7] = 0x90u
        memory[0x00E8] = 0xA6u
        memory[0x072F] = 0x0Eu

        // Execute decompiled function
        findAreaPointer()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x07u, memory[0x0007], "Memory 0x0007 mismatch")
    }

    /**
     * Test case 9 from frame 2607
     * Function: findAreaPointer (0x9BBB)
     * FUZZY MATCH: entry +7 bytes
     * Call depth: 13
     * Memory reads: 3, writes: 1
     */
    @Test
    fun `findAreaPointer_frame2607_test9`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (3 addresses)
        memory[0x00E7] = 0x90u
        memory[0x00E8] = 0xA6u
        memory[0x072F] = 0x10u

        // Execute decompiled function
        findAreaPointer()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x07u, memory[0x0007], "Memory 0x0007 mismatch")
    }

    // =========================================
    // 0x9BE1: getAreaDataAddrs
    // FUZZY MATCH: captured at +30 bytes from entry
    // 67340 calls, 877 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 13
     * Function: getAreaDataAddrs (0x9BE1)
     * FUZZY MATCH: entry +30 bytes
     * Call depth: 7
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `getAreaDataAddrs_frame13_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x01F3] = 0x00u

        // Execute decompiled function
        getAreaDataAddrs()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x0006], "Memory 0x0006 mismatch")
        assertEquals(0x05u, memory[0x0007], "Memory 0x0007 mismatch")
    }

    /**
     * Test case 1 from frame 1116
     * Function: getAreaDataAddrs (0x9BE1)
     * FUZZY MATCH: entry +30 bytes
     * Call depth: 9
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `getAreaDataAddrs_frame1116_test1`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x01ED] = 0x07u

        // Execute decompiled function
        getAreaDataAddrs()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x07u, memory[0x0006], "Memory 0x0006 mismatch")
        assertEquals(0x05u, memory[0x0007], "Memory 0x0007 mismatch")
    }

    /**
     * Test case 2 from frame 1233
     * Function: getAreaDataAddrs (0x9BE1)
     * FUZZY MATCH: entry +30 bytes
     * Call depth: 9
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `getAreaDataAddrs_frame1233_test2`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x01EE] = 0x0Du

        // Execute decompiled function
        getAreaDataAddrs()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x0Du, memory[0x0006], "Memory 0x0006 mismatch")
        assertEquals(0x05u, memory[0x0007], "Memory 0x0007 mismatch")
    }

    /**
     * Test case 3 from frame 1347
     * Function: getAreaDataAddrs (0x9BE1)
     * FUZZY MATCH: entry +30 bytes
     * Call depth: 9
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `getAreaDataAddrs_frame1347_test3`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x01EE] = 0x11u

        // Execute decompiled function
        getAreaDataAddrs()

        // Verify: Check output memory (2 addresses)
        assertEquals(0xD1u, memory[0x0006], "Memory 0x0006 mismatch")
        assertEquals(0x05u, memory[0x0007], "Memory 0x0007 mismatch")
    }

    /**
     * Test case 4 from frame 1523
     * Function: getAreaDataAddrs (0x9BE1)
     * FUZZY MATCH: entry +30 bytes
     * Call depth: 11
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `getAreaDataAddrs_frame1523_test4`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x01F1] = 0x06u

        // Execute decompiled function
        getAreaDataAddrs()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x06u, memory[0x0006], "Memory 0x0006 mismatch")
        assertEquals(0x05u, memory[0x0007], "Memory 0x0007 mismatch")
    }

    /**
     * Test case 5 from frame 1854
     * Function: getAreaDataAddrs (0x9BE1)
     * FUZZY MATCH: entry +30 bytes
     * Call depth: 9
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `getAreaDataAddrs_frame1854_test5`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x01EE] = 0x18u

        // Execute decompiled function
        getAreaDataAddrs()

        // Verify: Check output memory (2 addresses)
        assertEquals(0xD8u, memory[0x0006], "Memory 0x0006 mismatch")
        assertEquals(0x05u, memory[0x0007], "Memory 0x0007 mismatch")
    }

    /**
     * Test case 6 from frame 2237
     * Function: getAreaDataAddrs (0x9BE1)
     * FUZZY MATCH: entry +30 bytes
     * Call depth: 9
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `getAreaDataAddrs_frame2237_test6`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x01EE] = 0x1Cu

        // Execute decompiled function
        getAreaDataAddrs()

        // Verify: Check output memory (2 addresses)
        assertEquals(0xDCu, memory[0x0006], "Memory 0x0006 mismatch")
        assertEquals(0x05u, memory[0x0007], "Memory 0x0007 mismatch")
    }

    /**
     * Test case 7 from frame 2367
     * Function: getAreaDataAddrs (0x9BE1)
     * FUZZY MATCH: entry +30 bytes
     * Call depth: 9
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `getAreaDataAddrs_frame2367_test7`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x01EE] = 0x02u

        // Execute decompiled function
        getAreaDataAddrs()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x02u, memory[0x0006], "Memory 0x0006 mismatch")
        assertEquals(0x05u, memory[0x0007], "Memory 0x0007 mismatch")
    }

    /**
     * Test case 8 from frame 2580
     * Function: getAreaDataAddrs (0x9BE1)
     * FUZZY MATCH: entry +30 bytes
     * Call depth: 9
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `getAreaDataAddrs_frame2580_test8`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x01EE] = 0x08u

        // Execute decompiled function
        getAreaDataAddrs()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x08u, memory[0x0006], "Memory 0x0006 mismatch")
        assertEquals(0x05u, memory[0x0007], "Memory 0x0007 mismatch")
    }

    /**
     * Test case 9 from frame 3193
     * Function: getAreaDataAddrs (0x9BE1)
     * FUZZY MATCH: entry +30 bytes
     * Call depth: 13
     * Memory reads: 1, writes: 3
     */
    @Test
    fun `getAreaDataAddrs_frame3193_test9`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x01EC] = 0x17u

        // Execute decompiled function
        getAreaDataAddrs()

        // Verify: Check output memory (2 addresses)
        assertEquals(0xD7u, memory[0x0006], "Memory 0x0006 mismatch")
        assertEquals(0x05u, memory[0x0007], "Memory 0x0007 mismatch")
    }

    // =========================================
    // 0xB273: playerEndLevel
    // FUZZY MATCH: captured at +28 bytes from entry
    // 6 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 1834
     * Function: playerEndLevel (0xB273)
     * FUZZY MATCH: entry +28 bytes
     * Call depth: 6
     * Memory reads: 0, writes: 2
     */
    @Test
    fun `playerEndLevel_frame1834_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (0 addresses)
        // No memory inputs

        // Execute decompiled function
        playerEndLevel()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x08u, memory[0x000E], "Memory 0x000E mismatch")
        assertEquals(0x00u, memory[0x0747], "Memory 0x0747 mismatch")
    }

    // =========================================
    // 0xB329: jumpSwimSub
    // FUZZY MATCH: captured at +42 bytes from entry
    // 8416 calls, 884 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 36
     * Function: jumpSwimSub (0xB329)
     * FUZZY MATCH: entry +42 bytes
     * Call depth: 6
     * Memory reads: 12, writes: 8
     */
    @Test
    fun `jumpSwimSub_frame36_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (12 addresses)
        memory[0x000E] = 0x08u
        memory[0x001D] = 0x00u
        memory[0x0057] = 0x00u
        memory[0x00B5] = 0x01u
        memory[0x00CE] = 0xB0u
        memory[0x01F2] = 0x3Du
        memory[0x01F3] = 0xB3u
        memory[0x01F4] = 0x2Du
        memory[0x01F5] = 0xB1u
        memory[0x03C4] = 0x00u
        memory[0x070B] = 0x00u
        memory[0x0754] = 0x01u

        // Execute decompiled function
        jumpSwimSub()

        // Verify: Check output memory (4 addresses)
        assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
        assertEquals(0x01u, memory[0x0499], "Memory 0x0499 mismatch")
        assertEquals(0x00u, memory[0x0714], "Memory 0x0714 mismatch")
        assertEquals(0x18u, memory[0x0789], "Memory 0x0789 mismatch")
    }

    /**
     * Test case 1 from frame 1464
     * Function: jumpSwimSub (0xB329)
     * FUZZY MATCH: entry +42 bytes
     * Call depth: 6
     * Memory reads: 12, writes: 9
     */
    @Test
    fun `jumpSwimSub_frame1464_test1`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (12 addresses)
        memory[0x000E] = 0x08u
        memory[0x001D] = 0x01u
        memory[0x0057] = 0x18u
        memory[0x00B5] = 0x01u
        memory[0x00CE] = 0xB0u
        memory[0x01F2] = 0x3Du
        memory[0x01F3] = 0xB3u
        memory[0x01F4] = 0x2Du
        memory[0x01F5] = 0xB1u
        memory[0x03C4] = 0x00u
        memory[0x070B] = 0x00u
        memory[0x0754] = 0x01u

        // Execute decompiled function
        jumpSwimSub()

        // Verify: Check output memory (5 addresses)
        assertEquals(0x01u, memory[0x0045], "Memory 0x0045 mismatch")
        assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
        assertEquals(0x01u, memory[0x0499], "Memory 0x0499 mismatch")
        assertEquals(0x00u, memory[0x0714], "Memory 0x0714 mismatch")
        assertEquals(0x18u, memory[0x0789], "Memory 0x0789 mismatch")
    }

    /**
     * Test case 2 from frame 1880
     * Function: jumpSwimSub (0xB329)
     * FUZZY MATCH: entry +42 bytes
     * Call depth: 6
     * Memory reads: 13, writes: 8
     */
    @Test
    fun `jumpSwimSub_frame1880_test2`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (13 addresses)
        memory[0x000E] = 0x08u
        memory[0x001D] = 0x01u
        memory[0x0057] = 0xF0u
        memory[0x00B5] = 0x01u
        memory[0x00CE] = 0x53u
        memory[0x01F2] = 0x3Du
        memory[0x01F3] = 0xB3u
        memory[0x01F4] = 0x2Du
        memory[0x01F5] = 0xB1u
        memory[0x03C4] = 0x00u
        memory[0x070B] = 0x00u
        memory[0x0714] = 0x00u
        memory[0x0754] = 0x00u

        // Execute decompiled function
        jumpSwimSub()

        // Verify: Check output memory (4 addresses)
        assertEquals(0x02u, memory[0x0045], "Memory 0x0045 mismatch")
        assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
        assertEquals(0x00u, memory[0x0499], "Memory 0x0499 mismatch")
        assertEquals(0x18u, memory[0x0789], "Memory 0x0789 mismatch")
    }

    /**
     * Test case 3 from frame 2188
     * Function: jumpSwimSub (0xB329)
     * FUZZY MATCH: entry +42 bytes
     * Call depth: 6
     * Memory reads: 13, writes: 8
     */
    @Test
    fun `jumpSwimSub_frame2188_test3`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (13 addresses)
        memory[0x000E] = 0x08u
        memory[0x001D] = 0x01u
        memory[0x0057] = 0x05u
        memory[0x00B5] = 0x01u
        memory[0x00CE] = 0x8Du
        memory[0x01F2] = 0x3Du
        memory[0x01F3] = 0xB3u
        memory[0x01F4] = 0x2Du
        memory[0x01F5] = 0xB1u
        memory[0x03C4] = 0x00u
        memory[0x070B] = 0x00u
        memory[0x0714] = 0x00u
        memory[0x0754] = 0x00u

        // Execute decompiled function
        jumpSwimSub()

        // Verify: Check output memory (4 addresses)
        assertEquals(0x01u, memory[0x0045], "Memory 0x0045 mismatch")
        assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
        assertEquals(0x00u, memory[0x0499], "Memory 0x0499 mismatch")
        assertEquals(0x18u, memory[0x0789], "Memory 0x0789 mismatch")
    }

    /**
     * Test case 4 from frame 2580
     * Function: jumpSwimSub (0xB329)
     * FUZZY MATCH: entry +42 bytes
     * Call depth: 6
     * Memory reads: 13, writes: 8
     */
    @Test
    fun `jumpSwimSub_frame2580_test4`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (13 addresses)
        memory[0x000E] = 0x08u
        memory[0x001D] = 0x02u
        memory[0x0057] = 0x18u
        memory[0x00B5] = 0x01u
        memory[0x00CE] = 0x85u
        memory[0x01F2] = 0x3Du
        memory[0x01F3] = 0xB3u
        memory[0x01F4] = 0x2Du
        memory[0x01F5] = 0xB1u
        memory[0x03C4] = 0x00u
        memory[0x070B] = 0x00u
        memory[0x0714] = 0x00u
        memory[0x0754] = 0x00u

        // Execute decompiled function
        jumpSwimSub()

        // Verify: Check output memory (4 addresses)
        assertEquals(0x01u, memory[0x0045], "Memory 0x0045 mismatch")
        assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
        assertEquals(0x00u, memory[0x0499], "Memory 0x0499 mismatch")
        assertEquals(0x18u, memory[0x0789], "Memory 0x0789 mismatch")
    }

    /**
     * Test case 5 from frame 4718
     * Function: jumpSwimSub (0xB329)
     * FUZZY MATCH: entry +42 bytes
     * Call depth: 6
     * Memory reads: 12, writes: 9
     */
    @Test
    fun `jumpSwimSub_frame4718_test5`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (12 addresses)
        memory[0x000E] = 0x08u
        memory[0x001D] = 0x01u
        memory[0x0057] = 0x1Cu
        memory[0x00B5] = 0x01u
        memory[0x00CE] = 0xB0u
        memory[0x01F2] = 0x3Du
        memory[0x01F3] = 0xB3u
        memory[0x01F4] = 0x2Du
        memory[0x01F5] = 0xB1u
        memory[0x03C4] = 0x00u
        memory[0x070B] = 0x00u
        memory[0x0754] = 0x01u

        // Execute decompiled function
        jumpSwimSub()

        // Verify: Check output memory (5 addresses)
        assertEquals(0x01u, memory[0x0045], "Memory 0x0045 mismatch")
        assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
        assertEquals(0x01u, memory[0x0499], "Memory 0x0499 mismatch")
        assertEquals(0x00u, memory[0x0714], "Memory 0x0714 mismatch")
        assertEquals(0x18u, memory[0x0789], "Memory 0x0789 mismatch")
    }

    /**
     * Test case 6 from frame 5232
     * Function: jumpSwimSub (0xB329)
     * FUZZY MATCH: entry +42 bytes
     * Call depth: 6
     * Memory reads: 13, writes: 8
     */
    @Test
    fun `jumpSwimSub_frame5232_test6`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (13 addresses)
        memory[0x000E] = 0x08u
        memory[0x001D] = 0x01u
        memory[0x0057] = 0x28u
        memory[0x00B5] = 0x01u
        memory[0x00CE] = 0x41u
        memory[0x01F2] = 0x3Du
        memory[0x01F3] = 0xB3u
        memory[0x01F4] = 0x2Du
        memory[0x01F5] = 0xB1u
        memory[0x03C4] = 0x00u
        memory[0x070B] = 0x00u
        memory[0x0714] = 0x00u
        memory[0x0754] = 0x00u

        // Execute decompiled function
        jumpSwimSub()

        // Verify: Check output memory (4 addresses)
        assertEquals(0x01u, memory[0x0045], "Memory 0x0045 mismatch")
        assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
        assertEquals(0x00u, memory[0x0499], "Memory 0x0499 mismatch")
        assertEquals(0x18u, memory[0x0789], "Memory 0x0789 mismatch")
    }

    /**
     * Test case 7 from frame 5674
     * Function: jumpSwimSub (0xB329)
     * FUZZY MATCH: entry +42 bytes
     * Call depth: 6
     * Memory reads: 12, writes: 9
     */
    @Test
    fun `jumpSwimSub_frame5674_test7`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (12 addresses)
        memory[0x000E] = 0x08u
        memory[0x001D] = 0x01u
        memory[0x0057] = 0x03u
        memory[0x00B5] = 0x01u
        memory[0x00CE] = 0x93u
        memory[0x01F2] = 0x3Du
        memory[0x01F3] = 0xB3u
        memory[0x01F4] = 0x2Du
        memory[0x01F5] = 0xB1u
        memory[0x03C4] = 0x00u
        memory[0x070B] = 0x00u
        memory[0x0754] = 0x01u

        // Execute decompiled function
        jumpSwimSub()

        // Verify: Check output memory (5 addresses)
        assertEquals(0x01u, memory[0x0045], "Memory 0x0045 mismatch")
        assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
        assertEquals(0x01u, memory[0x0499], "Memory 0x0499 mismatch")
        assertEquals(0x00u, memory[0x0714], "Memory 0x0714 mismatch")
        assertEquals(0x18u, memory[0x0789], "Memory 0x0789 mismatch")
    }

    /**
     * Test case 8 from frame 5968
     * Function: jumpSwimSub (0xB329)
     * FUZZY MATCH: entry +42 bytes
     * Call depth: 6
     * Memory reads: 12, writes: 8
     */
    @Test
    fun `jumpSwimSub_frame5968_test8`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (12 addresses)
        memory[0x000E] = 0x0Bu
        memory[0x001D] = 0x01u
        memory[0x0057] = 0x00u
        memory[0x00B5] = 0x01u
        memory[0x00CE] = 0x89u
        memory[0x01F2] = 0x3Du
        memory[0x01F3] = 0xB3u
        memory[0x01F4] = 0x2Du
        memory[0x01F5] = 0xB1u
        memory[0x03C4] = 0x00u
        memory[0x070B] = 0x00u
        memory[0x0754] = 0x01u

        // Execute decompiled function
        jumpSwimSub()

        // Verify: Check output memory (4 addresses)
        assertEquals(0x00u, memory[0x03C4], "Memory 0x03C4 mismatch")
        assertEquals(0x01u, memory[0x0499], "Memory 0x0499 mismatch")
        assertEquals(0x00u, memory[0x0714], "Memory 0x0714 mismatch")
        assertEquals(0x18u, memory[0x0789], "Memory 0x0789 mismatch")
    }

    /**
     * Test case 9 from frame 6170
     * Function: jumpSwimSub (0xB329)
     * FUZZY MATCH: entry +42 bytes
     * Call depth: 6
     * Memory reads: 15, writes: 10
     */
    @Test
    fun `jumpSwimSub_frame6170_test9`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (15 addresses)
        memory[0x0007] = 0x04u
        memory[0x000E] = 0x0Bu
        memory[0x001D] = 0x01u
        memory[0x0057] = 0x00u
        memory[0x00B5] = 0x02u
        memory[0x00CE] = 0xAFu
        memory[0x01F2] = 0x3Du
        memory[0x01F3] = 0xB3u
        memory[0x01F4] = 0x2Du
        memory[0x01F5] = 0xB1u
        memory[0x03C4] = 0x01u
        memory[0x070B] = 0x00u
        memory[0x0743] = 0x00u
        memory[0x0754] = 0x01u
        memory[0x0759] = 0x00u

        // Execute decompiled function
        jumpSwimSub()

        // Verify: Check output memory (6 addresses)
        assertEquals(0x04u, memory[0x0007], "Memory 0x0007 mismatch")
        assertEquals(0x01u, memory[0x03C4], "Memory 0x03C4 mismatch")
        assertEquals(0x01u, memory[0x0499], "Memory 0x0499 mismatch")
        assertEquals(0x00u, memory[0x0714], "Memory 0x0714 mismatch")
        assertEquals(0x01u, memory[0x0723], "Memory 0x0723 mismatch")
        assertEquals(0x18u, memory[0x0789], "Memory 0x0789 mismatch")
    }

    // =========================================
    // 0xB74F: processWhirlpools
    // FUZZY MATCH: captured at +14 bytes from entry
    // 8844 calls, 15 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 33
     * Function: processWhirlpools (0xB74F)
     * FUZZY MATCH: entry +14 bytes
     * Call depth: 5
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `processWhirlpools_frame33_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x0770] = 0x00u

        // Execute decompiled function
        processWhirlpools()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 1 from frame 37
     * Function: processWhirlpools (0xB74F)
     * FUZZY MATCH: entry +14 bytes
     * Call depth: 6
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `processWhirlpools_frame37_test1`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x0770] = 0x00u

        // Execute decompiled function
        processWhirlpools()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 2 from frame 43
     * Function: processWhirlpools (0xB74F)
     * FUZZY MATCH: entry +14 bytes
     * Call depth: 6
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `processWhirlpools_frame43_test2`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x0770] = 0x00u

        // Execute decompiled function
        processWhirlpools()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 3 from frame 61
     * Function: processWhirlpools (0xB74F)
     * FUZZY MATCH: entry +14 bytes
     * Call depth: 6
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `processWhirlpools_frame61_test3`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x0770] = 0x00u

        // Execute decompiled function
        processWhirlpools()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 4 from frame 1317
     * Function: processWhirlpools (0xB74F)
     * FUZZY MATCH: entry +14 bytes
     * Call depth: 9
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `processWhirlpools_frame1317_test4`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x0770] = 0x00u

        // Execute decompiled function
        processWhirlpools()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 5 from frame 1491
     * Function: processWhirlpools (0xB74F)
     * FUZZY MATCH: entry +14 bytes
     * Call depth: 10
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `processWhirlpools_frame1491_test5`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x0770] = 0x00u

        // Execute decompiled function
        processWhirlpools()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 6 from frame 1511
     * Function: processWhirlpools (0xB74F)
     * FUZZY MATCH: entry +14 bytes
     * Call depth: 7
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `processWhirlpools_frame1511_test6`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x0770] = 0x00u

        // Execute decompiled function
        processWhirlpools()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 7 from frame 1517
     * Function: processWhirlpools (0xB74F)
     * FUZZY MATCH: entry +14 bytes
     * Call depth: 7
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `processWhirlpools_frame1517_test7`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x0770] = 0x00u

        // Execute decompiled function
        processWhirlpools()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 8 from frame 1555
     * Function: processWhirlpools (0xB74F)
     * FUZZY MATCH: entry +14 bytes
     * Call depth: 7
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `processWhirlpools_frame1555_test8`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x0770] = 0x00u

        // Execute decompiled function
        processWhirlpools()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 9 from frame 5459
     * Function: processWhirlpools (0xB74F)
     * FUZZY MATCH: entry +14 bytes
     * Call depth: 12
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `processWhirlpools_frame5459_test9`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x0770] = 0x00u

        // Execute decompiled function
        processWhirlpools()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    // =========================================
    // 0xBB84: giveOneCoin
    // FUZZY MATCH: captured at +17 bytes from entry
    // 9 calls, 1 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 1317
     * Function: giveOneCoin (0xBB84)
     * FUZZY MATCH: entry +17 bytes
     * Call depth: 11
     * Memory reads: 1, writes: 1
     */
    @Test
    fun `giveOneCoin_frame1317_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x0032] = 0x00u

        // Execute decompiled function
        giveOneCoin()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x08u, memory[0x06B7], "Memory 0x06B7 mismatch")
    }

    // =========================================
    // 0xBBFE: powerUpObjHandler
    // FUZZY MATCH: captured at +4 bytes from entry
    // 9 calls, 2 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 1317
     * Function: powerUpObjHandler (0xBBFE)
     * FUZZY MATCH: entry +4 bytes
     * Call depth: 11
     * Memory reads: 7, writes: 6
     */
    @Test
    fun `powerUpObjHandler_frame1317_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (7 addresses)
        memory[0x0008] = 0x00u
        memory[0x01EC] = 0x0Bu
        memory[0x01ED] = 0xBCu
        memory[0x0300] = 0x18u
        memory[0x0313] = 0x00u
        memory[0x0753] = 0x00u
        memory[0x075E] = 0x00u

        // Execute decompiled function
        powerUpObjHandler()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x24u, memory[0x0313], "Memory 0x0313 mismatch")
        assertEquals(0x01u, memory[0x075E], "Memory 0x075E mismatch")
    }

    /**
     * Test case 1 from frame 5079
     * Function: powerUpObjHandler (0xBBFE)
     * FUZZY MATCH: entry +4 bytes
     * Call depth: 11
     * Memory reads: 7, writes: 6
     */
    @Test
    fun `powerUpObjHandler_frame5079_test1`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (7 addresses)
        memory[0x0008] = 0x00u
        memory[0x01EC] = 0x0Bu
        memory[0x01ED] = 0xBCu
        memory[0x0300] = 0x18u
        memory[0x0313] = 0x00u
        memory[0x0753] = 0x00u
        memory[0x075E] = 0x01u

        // Execute decompiled function
        powerUpObjHandler()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x24u, memory[0x0313], "Memory 0x0313 mismatch")
        assertEquals(0x02u, memory[0x075E], "Memory 0x075E mismatch")
    }

    // =========================================
    // 0xBD9B: checkTopOfBlock
    // FUZZY MATCH: captured at +16 bytes from entry
    // 15 calls, 3 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 1317
     * Function: checkTopOfBlock (0xBD9B)
     * FUZZY MATCH: entry +16 bytes
     * Call depth: 9
     * Memory reads: 6, writes: 8
     */
    @Test
    fun `checkTopOfBlock_frame1317_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (6 addresses)
        memory[0x0005] = 0xC0u
        memory[0x01EE] = 0x9Du
        memory[0x01EF] = 0xBDu
        memory[0x01F0] = 0x7Au
        memory[0x01F1] = 0xBDu
        memory[0x03EE] = 0x00u

        // Execute decompiled function
        checkTopOfBlock()

        // Verify: Check output memory (6 addresses)
        assertEquals(0x00u, memory[0x0060], "Memory 0x0060 mismatch")
        assertEquals(0x00u, memory[0x009F], "Memory 0x009F mismatch")
        assertEquals(0xFEu, memory[0x00A8], "Memory 0x00A8 mismatch")
        assertEquals(0x02u, memory[0x00FF], "Memory 0x00FF mismatch")
        assertEquals(0x01u, memory[0x03EE], "Memory 0x03EE mismatch")
        assertEquals(0x00u, memory[0x043C], "Memory 0x043C mismatch")
    }

    /**
     * Test case 1 from frame 1491
     * Function: checkTopOfBlock (0xBD9B)
     * FUZZY MATCH: entry +16 bytes
     * Call depth: 9
     * Memory reads: 6, writes: 8
     */
    @Test
    fun `checkTopOfBlock_frame1491_test1`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (6 addresses)
        memory[0x0005] = 0xC1u
        memory[0x01EE] = 0x9Du
        memory[0x01EF] = 0xBDu
        memory[0x01F0] = 0x7Au
        memory[0x01F1] = 0xBDu
        memory[0x03EE] = 0x01u

        // Execute decompiled function
        checkTopOfBlock()

        // Verify: Check output memory (6 addresses)
        assertEquals(0x00u, memory[0x0061], "Memory 0x0061 mismatch")
        assertEquals(0x00u, memory[0x009F], "Memory 0x009F mismatch")
        assertEquals(0xFEu, memory[0x00A9], "Memory 0x00A9 mismatch")
        assertEquals(0x02u, memory[0x00FF], "Memory 0x00FF mismatch")
        assertEquals(0x00u, memory[0x03EE], "Memory 0x03EE mismatch")
        assertEquals(0x00u, memory[0x043D], "Memory 0x043D mismatch")
    }

    /**
     * Test case 2 from frame 5079
     * Function: checkTopOfBlock (0xBD9B)
     * FUZZY MATCH: entry +16 bytes
     * Call depth: 9
     * Memory reads: 6, writes: 8
     */
    @Test
    fun `checkTopOfBlock_frame5079_test2`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (6 addresses)
        memory[0x0005] = 0xC0u
        memory[0x01EE] = 0x9Du
        memory[0x01EF] = 0xBDu
        memory[0x01F0] = 0x7Au
        memory[0x01F1] = 0xBDu
        memory[0x03EE] = 0x00u

        // Execute decompiled function
        checkTopOfBlock()

        // Verify: Check output memory (6 addresses)
        assertEquals(0x00u, memory[0x0060], "Memory 0x0060 mismatch")
        assertEquals(0x00u, memory[0x009F], "Memory 0x009F mismatch")
        assertEquals(0xFEu, memory[0x00A8], "Memory 0x00A8 mismatch")
        assertEquals(0x02u, memory[0x00FF], "Memory 0x00FF mismatch")
        assertEquals(0x01u, memory[0x03EE], "Memory 0x03EE mismatch")
        assertEquals(0x00u, memory[0x043C], "Memory 0x043C mismatch")
    }

    // =========================================
    // 0xBF02: moveEnemySlowVert
    // FUZZY MATCH: captured at +10 bytes from entry
    // 4164 calls, 7 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 1181
     * Function: moveEnemySlowVert (0xBF02)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 10
     * Memory reads: 3, writes: 2
     */
    @Test
    fun `moveEnemySlowVert_frame1181_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (3 addresses)
        memory[0x0008] = 0x00u
        memory[0x01EF] = 0x05u
        memory[0x01F0] = 0xBFu

        // Execute decompiled function
        moveEnemySlowVert()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 1 from frame 1623
     * Function: moveEnemySlowVert (0xBF02)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 9
     * Memory reads: 3, writes: 2
     */
    @Test
    fun `moveEnemySlowVert_frame1623_test1`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (3 addresses)
        memory[0x0008] = 0x05u
        memory[0x01EF] = 0x05u
        memory[0x01F0] = 0xBFu

        // Execute decompiled function
        moveEnemySlowVert()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 2 from frame 2021
     * Function: moveEnemySlowVert (0xBF02)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 10
     * Memory reads: 3, writes: 2
     */
    @Test
    fun `moveEnemySlowVert_frame2021_test2`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (3 addresses)
        memory[0x0008] = 0x00u
        memory[0x01EF] = 0x05u
        memory[0x01F0] = 0xBFu

        // Execute decompiled function
        moveEnemySlowVert()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 3 from frame 2621
     * Function: moveEnemySlowVert (0xBF02)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 12
     * Memory reads: 3, writes: 2
     */
    @Test
    fun `moveEnemySlowVert_frame2621_test3`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (3 addresses)
        memory[0x0008] = 0x01u
        memory[0x01EF] = 0x05u
        memory[0x01F0] = 0xBFu

        // Execute decompiled function
        moveEnemySlowVert()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 4 from frame 2843
     * Function: moveEnemySlowVert (0xBF02)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 12
     * Memory reads: 3, writes: 2
     */
    @Test
    fun `moveEnemySlowVert_frame2843_test4`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (3 addresses)
        memory[0x0008] = 0x01u
        memory[0x01EF] = 0x05u
        memory[0x01F0] = 0xBFu

        // Execute decompiled function
        moveEnemySlowVert()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 5 from frame 5459
     * Function: moveEnemySlowVert (0xBF02)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 14
     * Memory reads: 3, writes: 2
     */
    @Test
    fun `moveEnemySlowVert_frame5459_test5`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (3 addresses)
        memory[0x0008] = 0x02u
        memory[0x01EF] = 0x05u
        memory[0x01F0] = 0xBFu

        // Execute decompiled function
        moveEnemySlowVert()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 6 from frame 5791
     * Function: moveEnemySlowVert (0xBF02)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 14
     * Memory reads: 3, writes: 2
     */
    @Test
    fun `moveEnemySlowVert_frame5791_test6`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (3 addresses)
        memory[0x0008] = 0x02u
        memory[0x01EF] = 0x05u
        memory[0x01F0] = 0xBFu

        // Execute decompiled function
        moveEnemySlowVert()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    // =========================================
    // 0xC226: initGoomba
    // FUZZY MATCH: captured at +1 bytes from entry
    // 12 calls, 2 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 1179
     * Function: initGoomba (0xC226)
     * FUZZY MATCH: entry +1 bytes
     * Call depth: 7
     * Memory reads: 5, writes: 5
     */
    @Test
    fun `initGoomba_frame1179_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (5 addresses)
        memory[0x0008] = 0x00u
        memory[0x000F] = 0x01u
        memory[0x01F4] = 0x10u
        memory[0x01F5] = 0xC2u
        memory[0x0739] = 0x03u

        // Execute decompiled function
        initGoomba()

        // Verify: Check output memory (3 addresses)
        assertEquals(0x00u, memory[0x001E], "Memory 0x001E mismatch")
        assertEquals(0x05u, memory[0x0739], "Memory 0x0739 mismatch")
        assertEquals(0x00u, memory[0x073B], "Memory 0x073B mismatch")
    }

    /**
     * Test case 1 from frame 2017
     * Function: initGoomba (0xC226)
     * FUZZY MATCH: entry +1 bytes
     * Call depth: 7
     * Memory reads: 5, writes: 5
     */
    @Test
    fun `initGoomba_frame2017_test1`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (5 addresses)
        memory[0x0008] = 0x00u
        memory[0x000F] = 0x01u
        memory[0x01F4] = 0x10u
        memory[0x01F5] = 0xC2u
        memory[0x0739] = 0x05u

        // Execute decompiled function
        initGoomba()

        // Verify: Check output memory (3 addresses)
        assertEquals(0x00u, memory[0x001E], "Memory 0x001E mismatch")
        assertEquals(0x07u, memory[0x0739], "Memory 0x0739 mismatch")
        assertEquals(0x00u, memory[0x073B], "Memory 0x073B mismatch")
    }

    // =========================================
    // 0xDB1C: getEnemyBoundBoxOfs
    // FUZZY MATCH: captured at +13 bytes from entry
    // 7 calls, 3 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 2843
     * Function: getEnemyBoundBoxOfs (0xDB1C)
     * FUZZY MATCH: entry +13 bytes
     * Call depth: 12
     * Memory reads: 3, writes: 2
     */
    @Test
    fun `getEnemyBoundBoxOfs_frame2843_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (3 addresses)
        memory[0x0016] = 0x06u
        memory[0x0046] = 0x01u
        memory[0x0058] = 0x08u

        // Execute decompiled function
        getEnemyBoundBoxOfs()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x02u, memory[0x0046], "Memory 0x0046 mismatch")
        assertEquals(0xF8u, memory[0x0058], "Memory 0x0058 mismatch")
    }

    /**
     * Test case 1 from frame 5465
     * Function: getEnemyBoundBoxOfs (0xDB1C)
     * FUZZY MATCH: entry +13 bytes
     * Call depth: 9
     * Memory reads: 3, writes: 2
     */
    @Test
    fun `getEnemyBoundBoxOfs_frame5465_test1`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (3 addresses)
        memory[0x0016] = 0x06u
        memory[0x0046] = 0x01u
        memory[0x0058] = 0x08u

        // Execute decompiled function
        getEnemyBoundBoxOfs()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x02u, memory[0x0046], "Memory 0x0046 mismatch")
        assertEquals(0xF8u, memory[0x0058], "Memory 0x0058 mismatch")
    }

    /**
     * Test case 2 from frame 5791
     * Function: getEnemyBoundBoxOfs (0xDB1C)
     * FUZZY MATCH: entry +13 bytes
     * Call depth: 14
     * Memory reads: 3, writes: 2
     */
    @Test
    fun `getEnemyBoundBoxOfs_frame5791_test2`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (3 addresses)
        memory[0x0017] = 0x06u
        memory[0x0047] = 0x01u
        memory[0x0059] = 0x08u

        // Execute decompiled function
        getEnemyBoundBoxOfs()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x02u, memory[0x0047], "Memory 0x0047 mismatch")
        assertEquals(0xF8u, memory[0x0059], "Memory 0x0059 mismatch")
    }

    // =========================================
    // 0xE5BB: drawPowerUp
    // FUZZY MATCH: captured at +44 bytes from entry
    // 210 calls, 6 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 1317
     * Function: drawPowerUp (0xE5BB)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 11
     * Memory reads: 0, writes: 4
     */
    @Test
    fun `drawPowerUp_frame1317_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (0 addresses)
        // No memory inputs

        // Execute decompiled function
        drawPowerUp()

        // Verify: Check output memory (4 addresses)
        assertEquals(0x87u, memory[0x0259], "Memory 0x0259 mismatch")
        assertEquals(0x87u, memory[0x025D], "Memory 0x025D mismatch")
        assertEquals(0x87u, memory[0x0261], "Memory 0x0261 mismatch")
        assertEquals(0x87u, memory[0x0265], "Memory 0x0265 mismatch")
    }

    /**
     * Test case 1 from frame 1319
     * Function: drawPowerUp (0xE5BB)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 10
     * Memory reads: 0, writes: 4
     */
    @Test
    fun `drawPowerUp_frame1319_test1`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (0 addresses)
        // No memory inputs

        // Execute decompiled function
        drawPowerUp()

        // Verify: Check output memory (4 addresses)
        assertEquals(0x87u, memory[0x02A1], "Memory 0x02A1 mismatch")
        assertEquals(0x87u, memory[0x02A5], "Memory 0x02A5 mismatch")
        assertEquals(0x87u, memory[0x02A9], "Memory 0x02A9 mismatch")
        assertEquals(0x87u, memory[0x02AD], "Memory 0x02AD mismatch")
    }

    /**
     * Test case 2 from frame 1321
     * Function: drawPowerUp (0xE5BB)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 10
     * Memory reads: 0, writes: 4
     */
    @Test
    fun `drawPowerUp_frame1321_test2`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (0 addresses)
        // No memory inputs

        // Execute decompiled function
        drawPowerUp()

        // Verify: Check output memory (4 addresses)
        assertEquals(0x87u, memory[0x02D9], "Memory 0x02D9 mismatch")
        assertEquals(0x87u, memory[0x02DD], "Memory 0x02DD mismatch")
        assertEquals(0x87u, memory[0x02E1], "Memory 0x02E1 mismatch")
        assertEquals(0x87u, memory[0x02E5], "Memory 0x02E5 mismatch")
    }

    /**
     * Test case 3 from frame 1491
     * Function: drawPowerUp (0xE5BB)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 12
     * Memory reads: 0, writes: 4
     */
    @Test
    fun `drawPowerUp_frame1491_test3`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (0 addresses)
        // No memory inputs

        // Execute decompiled function
        drawPowerUp()

        // Verify: Check output memory (4 addresses)
        assertEquals(0x87u, memory[0x0269], "Memory 0x0269 mismatch")
        assertEquals(0x87u, memory[0x026D], "Memory 0x026D mismatch")
        assertEquals(0x87u, memory[0x0271], "Memory 0x0271 mismatch")
        assertEquals(0x87u, memory[0x0275], "Memory 0x0275 mismatch")
    }

    /**
     * Test case 4 from frame 1493
     * Function: drawPowerUp (0xE5BB)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 11
     * Memory reads: 0, writes: 4
     */
    @Test
    fun `drawPowerUp_frame1493_test4`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (0 addresses)
        // No memory inputs

        // Execute decompiled function
        drawPowerUp()

        // Verify: Check output memory (4 addresses)
        assertEquals(0x87u, memory[0x02B1], "Memory 0x02B1 mismatch")
        assertEquals(0x87u, memory[0x02B5], "Memory 0x02B5 mismatch")
        assertEquals(0x87u, memory[0x02B9], "Memory 0x02B9 mismatch")
        assertEquals(0x87u, memory[0x02BD], "Memory 0x02BD mismatch")
    }

    /**
     * Test case 5 from frame 1495
     * Function: drawPowerUp (0xE5BB)
     * FUZZY MATCH: entry +44 bytes
     * Call depth: 9
     * Memory reads: 0, writes: 4
     */
    @Test
    fun `drawPowerUp_frame1495_test5`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (0 addresses)
        // No memory inputs

        // Execute decompiled function
        drawPowerUp()

        // Verify: Check output memory (4 addresses)
        assertEquals(0x87u, memory[0x02E9], "Memory 0x02E9 mismatch")
        assertEquals(0x87u, memory[0x02ED], "Memory 0x02ED mismatch")
        assertEquals(0x87u, memory[0x02F1], "Memory 0x02F1 mismatch")
        assertEquals(0x87u, memory[0x02F5], "Memory 0x02F5 mismatch")
    }

    // =========================================
    // 0xEFBE: chkForPlayerAttrib
    // FUZZY MATCH: captured at +24 bytes from entry
    // 8632 calls, 946 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 33
     * Function: chkForPlayerAttrib (0xEFBE)
     * FUZZY MATCH: entry +24 bytes
     * Call depth: 6
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `chkForPlayerAttrib_frame33_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (9 addresses)
        memory[0x0007] = 0x04u
        memory[0x0033] = 0x01u
        memory[0x01F2] = 0xE6u
        memory[0x01F3] = 0xEFu
        memory[0x03AD] = 0x28u
        memory[0x03B8] = 0xB0u
        memory[0x03C4] = 0x00u
        memory[0x06D5] = 0xB8u
        memory[0x06E4] = 0x04u

        // Execute decompiled function
        chkForPlayerAttrib()

        // Verify: Check output memory (7 addresses)
        assertEquals(0x4Fu, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0xB0u, memory[0x0002], "Memory 0x0002 mismatch")
        assertEquals(0x01u, memory[0x0003], "Memory 0x0003 mismatch")
        assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
        assertEquals(0x28u, memory[0x0005], "Memory 0x0005 mismatch")
        assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
        assertEquals(0x28u, memory[0x0755], "Memory 0x0755 mismatch")
    }

    /**
     * Test case 1 from frame 1313
     * Function: chkForPlayerAttrib (0xEFBE)
     * FUZZY MATCH: entry +24 bytes
     * Call depth: 9
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `chkForPlayerAttrib_frame1313_test1`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (9 addresses)
        memory[0x0007] = 0x04u
        memory[0x0033] = 0x01u
        memory[0x01F2] = 0xE6u
        memory[0x01F3] = 0xEFu
        memory[0x03AD] = 0x70u
        memory[0x03B8] = 0x93u
        memory[0x03C4] = 0x00u
        memory[0x06D5] = 0x80u
        memory[0x06E4] = 0x04u

        // Execute decompiled function
        chkForPlayerAttrib()

        // Verify: Check output memory (7 addresses)
        assertEquals(0x42u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x93u, memory[0x0002], "Memory 0x0002 mismatch")
        assertEquals(0x01u, memory[0x0003], "Memory 0x0003 mismatch")
        assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
        assertEquals(0x70u, memory[0x0005], "Memory 0x0005 mismatch")
        assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
        assertEquals(0x70u, memory[0x0755], "Memory 0x0755 mismatch")
    }

    /**
     * Test case 2 from frame 1701
     * Function: chkForPlayerAttrib (0xEFBE)
     * FUZZY MATCH: entry +24 bytes
     * Call depth: 8
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `chkForPlayerAttrib_frame1701_test2`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (9 addresses)
        memory[0x0007] = 0x04u
        memory[0x0033] = 0x02u
        memory[0x01F2] = 0xE6u
        memory[0x01F3] = 0xEFu
        memory[0x03AD] = 0x49u
        memory[0x03B8] = 0x70u
        memory[0x03C4] = 0x00u
        memory[0x06D5] = 0x80u
        memory[0x06E4] = 0x04u

        // Execute decompiled function
        chkForPlayerAttrib()

        // Verify: Check output memory (7 addresses)
        assertEquals(0x42u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x70u, memory[0x0002], "Memory 0x0002 mismatch")
        assertEquals(0x02u, memory[0x0003], "Memory 0x0003 mismatch")
        assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
        assertEquals(0x49u, memory[0x0005], "Memory 0x0005 mismatch")
        assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
        assertEquals(0x49u, memory[0x0755], "Memory 0x0755 mismatch")
    }

    /**
     * Test case 3 from frame 2011
     * Function: chkForPlayerAttrib (0xEFBE)
     * FUZZY MATCH: entry +24 bytes
     * Call depth: 7
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `chkForPlayerAttrib_frame2011_test3`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (9 addresses)
        memory[0x0007] = 0x04u
        memory[0x0033] = 0x01u
        memory[0x01F2] = 0xE6u
        memory[0x01F3] = 0xEFu
        memory[0x03AD] = 0x58u
        memory[0x03B8] = 0x84u
        memory[0x03C4] = 0x00u
        memory[0x06D5] = 0x10u
        memory[0x06E4] = 0x04u

        // Execute decompiled function
        chkForPlayerAttrib()

        // Verify: Check output memory (7 addresses)
        assertEquals(0x16u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x84u, memory[0x0002], "Memory 0x0002 mismatch")
        assertEquals(0x01u, memory[0x0003], "Memory 0x0003 mismatch")
        assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
        assertEquals(0x58u, memory[0x0005], "Memory 0x0005 mismatch")
        assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
        assertEquals(0x58u, memory[0x0755], "Memory 0x0755 mismatch")
    }

    /**
     * Test case 4 from frame 2507
     * Function: chkForPlayerAttrib (0xEFBE)
     * FUZZY MATCH: entry +24 bytes
     * Call depth: 7
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `chkForPlayerAttrib_frame2507_test4`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (9 addresses)
        memory[0x0007] = 0x04u
        memory[0x0033] = 0x01u
        memory[0x01F2] = 0xE6u
        memory[0x01F3] = 0xEFu
        memory[0x03AD] = 0x72u
        memory[0x03B8] = 0x71u
        memory[0x03C4] = 0x00u
        memory[0x06D5] = 0x20u
        memory[0x06E4] = 0x04u

        // Execute decompiled function
        chkForPlayerAttrib()

        // Verify: Check output memory (7 addresses)
        assertEquals(0x26u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x71u, memory[0x0002], "Memory 0x0002 mismatch")
        assertEquals(0x01u, memory[0x0003], "Memory 0x0003 mismatch")
        assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
        assertEquals(0x72u, memory[0x0005], "Memory 0x0005 mismatch")
        assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
        assertEquals(0x72u, memory[0x0755], "Memory 0x0755 mismatch")
    }

    /**
     * Test case 5 from frame 4831
     * Function: chkForPlayerAttrib (0xEFBE)
     * FUZZY MATCH: entry +24 bytes
     * Call depth: 8
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `chkForPlayerAttrib_frame4831_test5`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (9 addresses)
        memory[0x0007] = 0x04u
        memory[0x0033] = 0x02u
        memory[0x01F2] = 0xE6u
        memory[0x01F3] = 0xEFu
        memory[0x03AD] = 0x6Eu
        memory[0x03B8] = 0xB0u
        memory[0x03C4] = 0x00u
        memory[0x06D5] = 0x70u
        memory[0x06E4] = 0x04u

        // Execute decompiled function
        chkForPlayerAttrib()

        // Verify: Check output memory (7 addresses)
        assertEquals(0x3Bu, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0xB0u, memory[0x0002], "Memory 0x0002 mismatch")
        assertEquals(0x02u, memory[0x0003], "Memory 0x0003 mismatch")
        assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
        assertEquals(0x6Eu, memory[0x0005], "Memory 0x0005 mismatch")
        assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
        assertEquals(0x6Eu, memory[0x0755], "Memory 0x0755 mismatch")
    }

    /**
     * Test case 6 from frame 5183
     * Function: chkForPlayerAttrib (0xEFBE)
     * FUZZY MATCH: entry +24 bytes
     * Call depth: 7
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `chkForPlayerAttrib_frame5183_test6`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (9 addresses)
        memory[0x0007] = 0x04u
        memory[0x0033] = 0x01u
        memory[0x01F2] = 0xE6u
        memory[0x01F3] = 0xEFu
        memory[0x03AD] = 0x12u
        memory[0x03B8] = 0x70u
        memory[0x03C4] = 0x00u
        memory[0x06D5] = 0x08u
        memory[0x06E4] = 0x04u

        // Execute decompiled function
        chkForPlayerAttrib()

        // Verify: Check output memory (7 addresses)
        assertEquals(0x0Eu, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x70u, memory[0x0002], "Memory 0x0002 mismatch")
        assertEquals(0x01u, memory[0x0003], "Memory 0x0003 mismatch")
        assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
        assertEquals(0x12u, memory[0x0005], "Memory 0x0005 mismatch")
        assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
        assertEquals(0x12u, memory[0x0755], "Memory 0x0755 mismatch")
    }

    /**
     * Test case 7 from frame 5399
     * Function: chkForPlayerAttrib (0xEFBE)
     * FUZZY MATCH: entry +24 bytes
     * Call depth: 9
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `chkForPlayerAttrib_frame5399_test7`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (9 addresses)
        memory[0x0007] = 0x04u
        memory[0x0033] = 0x01u
        memory[0x01F2] = 0xE6u
        memory[0x01F3] = 0xEFu
        memory[0x03AD] = 0x70u
        memory[0x03B8] = 0x78u
        memory[0x03C4] = 0x00u
        memory[0x06D5] = 0x20u
        memory[0x06E4] = 0x04u

        // Execute decompiled function
        chkForPlayerAttrib()

        // Verify: Check output memory (7 addresses)
        assertEquals(0x26u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x78u, memory[0x0002], "Memory 0x0002 mismatch")
        assertEquals(0x01u, memory[0x0003], "Memory 0x0003 mismatch")
        assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
        assertEquals(0x70u, memory[0x0005], "Memory 0x0005 mismatch")
        assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
        assertEquals(0x70u, memory[0x0755], "Memory 0x0755 mismatch")
    }

    /**
     * Test case 8 from frame 5891
     * Function: chkForPlayerAttrib (0xEFBE)
     * FUZZY MATCH: entry +24 bytes
     * Call depth: 11
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `chkForPlayerAttrib_frame5891_test8`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (9 addresses)
        memory[0x0007] = 0x04u
        memory[0x0033] = 0x02u
        memory[0x01F2] = 0xE6u
        memory[0x01F3] = 0xEFu
        memory[0x03AD] = 0x6Fu
        memory[0x03B8] = 0xB0u
        memory[0x03C4] = 0x00u
        memory[0x06D5] = 0x70u
        memory[0x06E4] = 0x04u

        // Execute decompiled function
        chkForPlayerAttrib()

        // Verify: Check output memory (7 addresses)
        assertEquals(0x3Bu, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0xB0u, memory[0x0002], "Memory 0x0002 mismatch")
        assertEquals(0x02u, memory[0x0003], "Memory 0x0003 mismatch")
        assertEquals(0x00u, memory[0x0004], "Memory 0x0004 mismatch")
        assertEquals(0x6Fu, memory[0x0005], "Memory 0x0005 mismatch")
        assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
        assertEquals(0x6Fu, memory[0x0755], "Memory 0x0755 mismatch")
    }

    /**
     * Test case 9 from frame 6145
     * Function: chkForPlayerAttrib (0xEFBE)
     * FUZZY MATCH: entry +24 bytes
     * Call depth: 9
     * Memory reads: 9, writes: 9
     */
    @Test
    fun `chkForPlayerAttrib_frame6145_test9`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (9 addresses)
        memory[0x0007] = 0x04u
        memory[0x0033] = 0x02u
        memory[0x01F2] = 0xE6u
        memory[0x01F3] = 0xEFu
        memory[0x03AD] = 0x67u
        memory[0x03B8] = 0x78u
        memory[0x03C4] = 0x03u
        memory[0x06D5] = 0xB0u
        memory[0x06E4] = 0x04u

        // Execute decompiled function
        chkForPlayerAttrib()

        // Verify: Check output memory (7 addresses)
        assertEquals(0x9Fu, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x78u, memory[0x0002], "Memory 0x0002 mismatch")
        assertEquals(0x02u, memory[0x0003], "Memory 0x0003 mismatch")
        assertEquals(0x03u, memory[0x0004], "Memory 0x0004 mismatch")
        assertEquals(0x67u, memory[0x0005], "Memory 0x0005 mismatch")
        assertEquals(0x00u, memory[0x0007], "Memory 0x0007 mismatch")
        assertEquals(0x67u, memory[0x0755], "Memory 0x0755 mismatch")
    }

    // =========================================
    // 0xEFEC: relativePlayerPosition
    // FUZZY MATCH: captured at +5 bytes from entry
    // 7944 calls, 335 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 33
     * Function: relativePlayerPosition (0xEFEC)
     * FUZZY MATCH: entry +5 bytes
     * Call depth: 6
     * Memory reads: 6, writes: 3
     */
    @Test
    fun `relativePlayerPosition_frame33_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (6 addresses)
        memory[0x000C] = 0x00u
        memory[0x001D] = 0x00u
        memory[0x0057] = 0x00u
        memory[0x01F2] = 0x2Au
        memory[0x01F3] = 0xF0u
        memory[0x0714] = 0x00u

        // Execute decompiled function
        relativePlayerPosition()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x00u, memory[0x070D], "Memory 0x070D mismatch")
    }

    /**
     * Test case 1 from frame 1053
     * Function: relativePlayerPosition (0xEFEC)
     * FUZZY MATCH: entry +5 bytes
     * Call depth: 7
     * Memory reads: 13, writes: 5
     */
    @Test
    fun `relativePlayerPosition_frame1053_test1`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (13 addresses)
        memory[0x0000] = 0x03u
        memory[0x000C] = 0x01u
        memory[0x001D] = 0x00u
        memory[0x0033] = 0x01u
        memory[0x0045] = 0x01u
        memory[0x0057] = 0x14u
        memory[0x01F2] = 0x40u
        memory[0x01F3] = 0xF0u
        memory[0x0700] = 0x14u
        memory[0x070C] = 0x04u
        memory[0x070D] = 0x02u
        memory[0x0714] = 0x00u
        memory[0x0781] = 0x00u

        // Execute decompiled function
        relativePlayerPosition()

        // Verify: Check output memory (3 addresses)
        assertEquals(0x03u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x00u, memory[0x070D], "Memory 0x070D mismatch")
        assertEquals(0x04u, memory[0x0781], "Memory 0x0781 mismatch")
    }

    /**
     * Test case 2 from frame 1373
     * Function: relativePlayerPosition (0xEFEC)
     * FUZZY MATCH: entry +5 bytes
     * Call depth: 9
     * Memory reads: 8, writes: 3
     */
    @Test
    fun `relativePlayerPosition_frame1373_test2`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (8 addresses)
        memory[0x000C] = 0x02u
        memory[0x001D] = 0x00u
        memory[0x0057] = 0xFBu
        memory[0x01F2] = 0x40u
        memory[0x01F3] = 0xF0u
        memory[0x0700] = 0x05u
        memory[0x0714] = 0x00u
        memory[0x0781] = 0x02u

        // Execute decompiled function
        relativePlayerPosition()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x03u, memory[0x0000], "Memory 0x0000 mismatch")
    }

    /**
     * Test case 3 from frame 1627
     * Function: relativePlayerPosition (0xEFEC)
     * FUZZY MATCH: entry +5 bytes
     * Call depth: 8
     * Memory reads: 8, writes: 3
     */
    @Test
    fun `relativePlayerPosition_frame1627_test3`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (8 addresses)
        memory[0x000C] = 0x02u
        memory[0x001D] = 0x00u
        memory[0x0057] = 0xFAu
        memory[0x01F2] = 0x40u
        memory[0x01F3] = 0xF0u
        memory[0x0700] = 0x06u
        memory[0x0714] = 0x00u
        memory[0x0781] = 0x05u

        // Execute decompiled function
        relativePlayerPosition()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x03u, memory[0x0000], "Memory 0x0000 mismatch")
    }

    /**
     * Test case 4 from frame 2041
     * Function: relativePlayerPosition (0xEFEC)
     * FUZZY MATCH: entry +5 bytes
     * Call depth: 9
     * Memory reads: 8, writes: 3
     */
    @Test
    fun `relativePlayerPosition_frame2041_test4`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (8 addresses)
        memory[0x000C] = 0x01u
        memory[0x001D] = 0x00u
        memory[0x0057] = 0x01u
        memory[0x01F2] = 0x40u
        memory[0x01F3] = 0xF0u
        memory[0x0700] = 0x01u
        memory[0x0714] = 0x00u
        memory[0x0781] = 0x05u

        // Execute decompiled function
        relativePlayerPosition()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x03u, memory[0x0000], "Memory 0x0000 mismatch")
    }

    /**
     * Test case 5 from frame 2235
     * Function: relativePlayerPosition (0xEFEC)
     * FUZZY MATCH: entry +5 bytes
     * Call depth: 7
     * Memory reads: 10, writes: 3
     */
    @Test
    fun `relativePlayerPosition_frame2235_test5`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (10 addresses)
        memory[0x000C] = 0x01u
        memory[0x001D] = 0x00u
        memory[0x0033] = 0x01u
        memory[0x0045] = 0x01u
        memory[0x0057] = 0x13u
        memory[0x01F2] = 0x40u
        memory[0x01F3] = 0xF0u
        memory[0x0700] = 0x13u
        memory[0x0714] = 0x00u
        memory[0x0781] = 0x06u

        // Execute decompiled function
        relativePlayerPosition()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x03u, memory[0x0000], "Memory 0x0000 mismatch")
    }

    /**
     * Test case 6 from frame 2637
     * Function: relativePlayerPosition (0xEFEC)
     * FUZZY MATCH: entry +5 bytes
     * Call depth: 11
     * Memory reads: 9, writes: 3
     */
    @Test
    fun `relativePlayerPosition_frame2637_test6`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (9 addresses)
        memory[0x000C] = 0x02u
        memory[0x001D] = 0x00u
        memory[0x0033] = 0x02u
        memory[0x0045] = 0x01u
        memory[0x0057] = 0x0Cu
        memory[0x01F2] = 0x2Au
        memory[0x01F3] = 0xF0u
        memory[0x0700] = 0x0Cu
        memory[0x0714] = 0x00u

        // Execute decompiled function
        relativePlayerPosition()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x00u, memory[0x070D], "Memory 0x070D mismatch")
    }

    /**
     * Test case 7 from frame 2711
     * Function: relativePlayerPosition (0xEFEC)
     * FUZZY MATCH: entry +5 bytes
     * Call depth: 11
     * Memory reads: 10, writes: 3
     */
    @Test
    fun `relativePlayerPosition_frame2711_test7`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (10 addresses)
        memory[0x000C] = 0x02u
        memory[0x001D] = 0x00u
        memory[0x0033] = 0x02u
        memory[0x0045] = 0x02u
        memory[0x0057] = 0xEAu
        memory[0x01F2] = 0x40u
        memory[0x01F3] = 0xF0u
        memory[0x0700] = 0x16u
        memory[0x0714] = 0x00u
        memory[0x0781] = 0x03u

        // Execute decompiled function
        relativePlayerPosition()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x03u, memory[0x0000], "Memory 0x0000 mismatch")
    }

    /**
     * Test case 8 from frame 5101
     * Function: relativePlayerPosition (0xEFEC)
     * FUZZY MATCH: entry +5 bytes
     * Call depth: 7
     * Memory reads: 10, writes: 3
     */
    @Test
    fun `relativePlayerPosition_frame5101_test8`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (10 addresses)
        memory[0x000C] = 0x00u
        memory[0x001D] = 0x00u
        memory[0x0033] = 0x02u
        memory[0x0045] = 0x02u
        memory[0x0057] = 0xE9u
        memory[0x01F2] = 0x40u
        memory[0x01F3] = 0xF0u
        memory[0x0700] = 0x17u
        memory[0x0714] = 0x00u
        memory[0x0781] = 0x03u

        // Execute decompiled function
        relativePlayerPosition()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x03u, memory[0x0000], "Memory 0x0000 mismatch")
    }

    /**
     * Test case 9 from frame 5649
     * Function: relativePlayerPosition (0xEFEC)
     * FUZZY MATCH: entry +5 bytes
     * Call depth: 13
     * Memory reads: 8, writes: 3
     */
    @Test
    fun `relativePlayerPosition_frame5649_test9`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (8 addresses)
        memory[0x000C] = 0x01u
        memory[0x001D] = 0x00u
        memory[0x0057] = 0x00u
        memory[0x01F2] = 0x40u
        memory[0x01F3] = 0xF0u
        memory[0x0700] = 0x00u
        memory[0x0714] = 0x00u
        memory[0x0781] = 0x03u

        // Execute decompiled function
        relativePlayerPosition()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x03u, memory[0x0000], "Memory 0x0000 mismatch")
    }

    // =========================================
    // 0xF062: getMiscOffscreenBits
    // FUZZY MATCH: captured at +10 bytes from entry
    // 2560 calls, 15 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 989
     * Function: getMiscOffscreenBits (0xF062)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 8
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `getMiscOffscreenBits_frame989_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x070D] = 0x00u

        // Execute decompiled function
        getMiscOffscreenBits()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 1 from frame 991
     * Function: getMiscOffscreenBits (0xF062)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 8
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `getMiscOffscreenBits_frame991_test1`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x070D] = 0x01u

        // Execute decompiled function
        getMiscOffscreenBits()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 2 from frame 1806
     * Function: getMiscOffscreenBits (0xF062)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 7
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `getMiscOffscreenBits_frame1806_test2`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x070D] = 0x00u

        // Execute decompiled function
        getMiscOffscreenBits()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 3 from frame 1851
     * Function: getMiscOffscreenBits (0xF062)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 8
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `getMiscOffscreenBits_frame1851_test3`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x070D] = 0x00u

        // Execute decompiled function
        getMiscOffscreenBits()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 4 from frame 1865
     * Function: getMiscOffscreenBits (0xF062)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 8
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `getMiscOffscreenBits_frame1865_test4`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x070D] = 0x01u

        // Execute decompiled function
        getMiscOffscreenBits()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 5 from frame 2033
     * Function: getMiscOffscreenBits (0xF062)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 10
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `getMiscOffscreenBits_frame2033_test5`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x070D] = 0x02u

        // Execute decompiled function
        getMiscOffscreenBits()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 6 from frame 2249
     * Function: getMiscOffscreenBits (0xF062)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 8
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `getMiscOffscreenBits_frame2249_test6`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x070D] = 0x02u

        // Execute decompiled function
        getMiscOffscreenBits()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 7 from frame 2297
     * Function: getMiscOffscreenBits (0xF062)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 8
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `getMiscOffscreenBits_frame2297_test7`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x070D] = 0x02u

        // Execute decompiled function
        getMiscOffscreenBits()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 8 from frame 2429
     * Function: getMiscOffscreenBits (0xF062)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 8
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `getMiscOffscreenBits_frame2429_test8`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x070D] = 0x00u

        // Execute decompiled function
        getMiscOffscreenBits()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    /**
     * Test case 9 from frame 2433
     * Function: getMiscOffscreenBits (0xF062)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 8
     * Memory reads: 1, writes: 0
     */
    @Test
    fun `getMiscOffscreenBits_frame2433_test9`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (1 addresses)
        memory[0x070D] = 0x01u

        // Execute decompiled function
        getMiscOffscreenBits()

        // Verify: Check output memory (0 addresses)
        // No memory outputs to verify (or only stack writes)
    }

    // =========================================
    // 0xF19B: soundEngine
    // FUZZY MATCH: captured at +14 bytes from entry
    // 693 calls, 23 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 1317
     * Function: soundEngine (0xF19B)
     * FUZZY MATCH: entry +14 bytes
     * Call depth: 10
     * Memory reads: 5, writes: 5
     */
    @Test
    fun `soundEngine_frame1317_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (5 addresses)
        memory[0x0000] = 0x00u
        memory[0x0008] = 0x08u
        memory[0x01F1] = 0xC4u
        memory[0x01F2] = 0x9Fu
        memory[0x01F3] = 0xF1u

        // Execute decompiled function
        soundEngine()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x00u, memory[0x03D6], "Memory 0x03D6 mismatch")
    }

    /**
     * Test case 1 from frame 1321
     * Function: soundEngine (0xF19B)
     * FUZZY MATCH: entry +14 bytes
     * Call depth: 9
     * Memory reads: 5, writes: 5
     */
    @Test
    fun `soundEngine_frame1321_test1`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (5 addresses)
        memory[0x0000] = 0x00u
        memory[0x0008] = 0x08u
        memory[0x01F1] = 0xC4u
        memory[0x01F2] = 0x9Fu
        memory[0x01F3] = 0xF1u

        // Execute decompiled function
        soundEngine()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x00u, memory[0x03D6], "Memory 0x03D6 mismatch")
    }

    /**
     * Test case 2 from frame 1325
     * Function: soundEngine (0xF19B)
     * FUZZY MATCH: entry +14 bytes
     * Call depth: 9
     * Memory reads: 5, writes: 5
     */
    @Test
    fun `soundEngine_frame1325_test2`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (5 addresses)
        memory[0x0000] = 0x00u
        memory[0x0008] = 0x08u
        memory[0x01F1] = 0xC4u
        memory[0x01F2] = 0x9Fu
        memory[0x01F3] = 0xF1u

        // Execute decompiled function
        soundEngine()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x00u, memory[0x03D6], "Memory 0x03D6 mismatch")
    }

    /**
     * Test case 3 from frame 1329
     * Function: soundEngine (0xF19B)
     * FUZZY MATCH: entry +14 bytes
     * Call depth: 9
     * Memory reads: 5, writes: 5
     */
    @Test
    fun `soundEngine_frame1329_test3`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (5 addresses)
        memory[0x0000] = 0x00u
        memory[0x0008] = 0x08u
        memory[0x01F1] = 0xC4u
        memory[0x01F2] = 0x9Fu
        memory[0x01F3] = 0xF1u

        // Execute decompiled function
        soundEngine()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x00u, memory[0x03D6], "Memory 0x03D6 mismatch")
    }

    /**
     * Test case 4 from frame 1335
     * Function: soundEngine (0xF19B)
     * FUZZY MATCH: entry +14 bytes
     * Call depth: 9
     * Memory reads: 5, writes: 5
     */
    @Test
    fun `soundEngine_frame1335_test4`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (5 addresses)
        memory[0x0000] = 0x00u
        memory[0x0008] = 0x08u
        memory[0x01F1] = 0xC4u
        memory[0x01F2] = 0x9Fu
        memory[0x01F3] = 0xF1u

        // Execute decompiled function
        soundEngine()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x00u, memory[0x03D6], "Memory 0x03D6 mismatch")
    }

    /**
     * Test case 5 from frame 1339
     * Function: soundEngine (0xF19B)
     * FUZZY MATCH: entry +14 bytes
     * Call depth: 9
     * Memory reads: 5, writes: 5
     */
    @Test
    fun `soundEngine_frame1339_test5`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (5 addresses)
        memory[0x0000] = 0x00u
        memory[0x0008] = 0x08u
        memory[0x01F1] = 0xC4u
        memory[0x01F2] = 0x9Fu
        memory[0x01F3] = 0xF1u

        // Execute decompiled function
        soundEngine()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x00u, memory[0x03D6], "Memory 0x03D6 mismatch")
    }

    /**
     * Test case 6 from frame 1343
     * Function: soundEngine (0xF19B)
     * FUZZY MATCH: entry +14 bytes
     * Call depth: 9
     * Memory reads: 5, writes: 5
     */
    @Test
    fun `soundEngine_frame1343_test6`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (5 addresses)
        memory[0x0000] = 0x00u
        memory[0x0008] = 0x08u
        memory[0x01F1] = 0xC4u
        memory[0x01F2] = 0x9Fu
        memory[0x01F3] = 0xF1u

        // Execute decompiled function
        soundEngine()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x00u, memory[0x03D6], "Memory 0x03D6 mismatch")
    }

    /**
     * Test case 7 from frame 1349
     * Function: soundEngine (0xF19B)
     * FUZZY MATCH: entry +14 bytes
     * Call depth: 9
     * Memory reads: 5, writes: 5
     */
    @Test
    fun `soundEngine_frame1349_test7`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (5 addresses)
        memory[0x0000] = 0x00u
        memory[0x0008] = 0x08u
        memory[0x01F1] = 0xC4u
        memory[0x01F2] = 0x9Fu
        memory[0x01F3] = 0xF1u

        // Execute decompiled function
        soundEngine()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x00u, memory[0x03D6], "Memory 0x03D6 mismatch")
    }

    /**
     * Test case 8 from frame 1353
     * Function: soundEngine (0xF19B)
     * FUZZY MATCH: entry +14 bytes
     * Call depth: 9
     * Memory reads: 5, writes: 5
     */
    @Test
    fun `soundEngine_frame1353_test8`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (5 addresses)
        memory[0x0000] = 0x00u
        memory[0x0008] = 0x08u
        memory[0x01F1] = 0xC4u
        memory[0x01F2] = 0x9Fu
        memory[0x01F3] = 0xF1u

        // Execute decompiled function
        soundEngine()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x00u, memory[0x03D6], "Memory 0x03D6 mismatch")
    }

    /**
     * Test case 9 from frame 1359
     * Function: soundEngine (0xF19B)
     * FUZZY MATCH: entry +14 bytes
     * Call depth: 9
     * Memory reads: 5, writes: 5
     */
    @Test
    fun `soundEngine_frame1359_test9`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (5 addresses)
        memory[0x0000] = 0x00u
        memory[0x0008] = 0x08u
        memory[0x01F1] = 0xC4u
        memory[0x01F2] = 0x9Fu
        memory[0x01F3] = 0xF1u

        // Execute decompiled function
        soundEngine()

        // Verify: Check output memory (2 addresses)
        assertEquals(0x00u, memory[0x0000], "Memory 0x0000 mismatch")
        assertEquals(0x00u, memory[0x03D6], "Memory 0x03D6 mismatch")
    }

    // =========================================
    // 0xF26D: playSqu2Sfx
    // FUZZY MATCH: captured at +10 bytes from entry
    // 55091 calls, 873 unique inputs
    // =========================================

    /**
     * Test case 0 from frame 32
     * Function: playSqu2Sfx (0xF26D)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 8
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `playSqu2Sfx_frame32_test0`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (2 addresses)
        memory[0x0006] = 0x38u
        memory[0x0007] = 0xD7u

        // Execute decompiled function
        playSqu2Sfx()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x08u, memory[0x0005], "Memory 0x0005 mismatch")
    }

    /**
     * Test case 1 from frame 1068
     * Function: playSqu2Sfx (0xF26D)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 10
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `playSqu2Sfx_frame1068_test1`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (2 addresses)
        memory[0x0006] = 0x38u
        memory[0x0007] = 0xB7u

        // Execute decompiled function
        playSqu2Sfx()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x08u, memory[0x0005], "Memory 0x0005 mismatch")
    }

    /**
     * Test case 2 from frame 1172
     * Function: playSqu2Sfx (0xF26D)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 10
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `playSqu2Sfx_frame1172_test2`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (2 addresses)
        memory[0x0006] = 0x38u
        memory[0x0007] = 0x97u

        // Execute decompiled function
        playSqu2Sfx()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x08u, memory[0x0005], "Memory 0x0005 mismatch")
    }

    /**
     * Test case 3 from frame 1317
     * Function: playSqu2Sfx (0xF26D)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 12
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `playSqu2Sfx_frame1317_test3`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (2 addresses)
        memory[0x0006] = 0x38u
        memory[0x0007] = 0x53u

        // Execute decompiled function
        playSqu2Sfx()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x08u, memory[0x0005], "Memory 0x0005 mismatch")
    }

    /**
     * Test case 4 from frame 1398
     * Function: playSqu2Sfx (0xF26D)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 9
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `playSqu2Sfx_frame1398_test4`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (2 addresses)
        memory[0x0006] = 0x20u
        memory[0x0007] = 0x70u

        // Execute decompiled function
        playSqu2Sfx()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x04u, memory[0x0005], "Memory 0x0005 mismatch")
    }

    /**
     * Test case 5 from frame 1595
     * Function: playSqu2Sfx (0xF26D)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 11
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `playSqu2Sfx_frame1595_test5`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (2 addresses)
        memory[0x0006] = 0x38u
        memory[0x0007] = 0xE5u

        // Execute decompiled function
        playSqu2Sfx()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x08u, memory[0x0005], "Memory 0x0005 mismatch")
    }

    /**
     * Test case 6 from frame 1902
     * Function: playSqu2Sfx (0xF26D)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 9
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `playSqu2Sfx_frame1902_test6`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (2 addresses)
        memory[0x0006] = 0x20u
        memory[0x0007] = 0xC7u

        // Execute decompiled function
        playSqu2Sfx()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x04u, memory[0x0005], "Memory 0x0005 mismatch")
    }

    /**
     * Test case 7 from frame 2713
     * Function: playSqu2Sfx (0xF26D)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 11
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `playSqu2Sfx_frame2713_test7`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (2 addresses)
        memory[0x0006] = 0x38u
        memory[0x0007] = 0x20u

        // Execute decompiled function
        playSqu2Sfx()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x08u, memory[0x0005], "Memory 0x0005 mismatch")
    }

    /**
     * Test case 8 from frame 5098
     * Function: playSqu2Sfx (0xF26D)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 9
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `playSqu2Sfx_frame5098_test8`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (2 addresses)
        memory[0x0006] = 0x38u
        memory[0x0007] = 0xF9u

        // Execute decompiled function
        playSqu2Sfx()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x08u, memory[0x0005], "Memory 0x0005 mismatch")
    }

    /**
     * Test case 9 from frame 5619
     * Function: playSqu2Sfx (0xF26D)
     * FUZZY MATCH: entry +10 bytes
     * Call depth: 13
     * Memory reads: 2, writes: 1
     */
    @Test
    fun `playSqu2Sfx_frame5619_test9`() {
        // Setup: Reset state
        resetCPU()
        clearMemory()

        // Setup: Set input memory (2 addresses)
        memory[0x0006] = 0x38u
        memory[0x0007] = 0x46u

        // Execute decompiled function
        playSqu2Sfx()

        // Verify: Check output memory (1 addresses)
        assertEquals(0x08u, memory[0x0005], "Memory 0x0005 mismatch")
    }

}
