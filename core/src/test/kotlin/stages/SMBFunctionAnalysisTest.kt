package com.ivieleague.decompiler6502tokotlin.hand.stages

import com.ivieleague.decompiler6502tokotlin.hand.stages.SMBTestFixtures.loadFunction
import com.ivieleague.decompiler6502tokotlin.hand.TrackedAsIo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests that function I/O analysis correctly identifies inputs, outputs, and clobbers
 * for real SMB functions.
 *
 * - Inputs: States consumed before being set (function parameters)
 * - Outputs: States that calling functions read (return values)
 * - Clobbers: States modified within the function
 */
class SMBFunctionAnalysisTest {

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `InitializeMemory has correct I O analysis`() {
        val func = loadFunction("InitializeMemory")

        func.assertIO {
            // Y register is used as byte offset within pages (read before written)
            hasInput(TrackedAsIo.Y)

            // Function returns A = #$00 (callers may read A)
            hasOutput(TrackedAsIo.A)

            // Clobbers registers A, X, Y
            clobbers(TrackedAsIo.A, TrackedAsIo.X, TrackedAsIo.Y)

            // Clobbers flags
            clobbers(TrackedAsIo.ZeroFlag, TrackedAsIo.NegativeFlag, TrackedAsIo.CarryFlag)

            // Clobbers virtual registers $06, $07
            clobbers(TrackedAsIo.VirtualRegister("$06"), TrackedAsIo.VirtualRegister("$07"))
        }
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `MusicHandler reads and writes registers`() {
        val func = loadFunction("MusicHandler")

        // MusicHandler is complex, but we can verify basic properties
        func.assertIO {
            // Should have inputs and outputs analyzed
            assertNotNull(func.inputs, "inputs should be computed")
            assertNotNull(func.outputs, "outputs should be computed")
            assertNotNull(func.clobbers, "clobbers should be computed")
        }
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `all test functions have I O analyzed`() {
        val functions = listOf(
            "InitializeMemory",
            "FloateyNumbersRoutine",
            "MusicHandler",
            "ImpedePlayerMove",
            "MoveSpritesOffscreen",
            "GetAreaObjXPosition",
            "GetAreaObjYPosition",
            "PlayerEnemyDiff",
            "InitVStf",
            "PlayerLakituDiff",
            "CheckForSolidMTiles",
            "CheckPlayerVertical",
            "HandlePipeEntry",
            "OffscreenBoundsCheck",
            "BubbleCheck"
        )

        for (funcName in functions) {
            val func = loadFunction(funcName)

            // All functions should have I/O analysis completed
            assertNotNull(func.inputs, "$funcName should have inputs analyzed")
            assertNotNull(func.outputs, "$funcName should have outputs analyzed")
            assertNotNull(func.clobbers, "$funcName should have clobbers analyzed")
        }
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `functions that modify A should clobber A`() {
        val functions = listOf(
            "InitializeMemory",
            "MusicHandler",
            "MoveSpritesOffscreen",
            "GetAreaObjXPosition",
            "GetAreaObjYPosition"
        )

        for (funcName in functions) {
            val func = loadFunction(funcName)
            assertTrue(
                TrackedAsIo.A in (func.clobbers ?: emptySet()),
                "$funcName should clobber A register"
            )
        }
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `GetAreaObjXPosition returns calculated value in A`() {
        val func = loadFunction("GetAreaObjXPosition")

        func.assertIO {
            // Simple arithmetic function - returns result in A
            hasOutput(TrackedAsIo.A)
            clobbers(TrackedAsIo.A)
        }
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `GetAreaObjYPosition returns calculated value in A`() {
        val func = loadFunction("GetAreaObjYPosition")

        func.assertIO {
            // Arithmetic with addition - returns result in A
            hasOutput(TrackedAsIo.A)
            clobbers(TrackedAsIo.A, TrackedAsIo.CarryFlag)
        }
    }
}
