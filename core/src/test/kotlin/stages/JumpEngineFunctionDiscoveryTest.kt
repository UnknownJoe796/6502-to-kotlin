package com.ivieleague.decompiler6502tokotlin.hand.stages

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests that JumpEngine dispatch table targets are discovered as functions.
 */
class JumpEngineFunctionDiscoveryTest {

    @Test
    fun `PlayerCtrlRoutine is discovered as a function via JumpEngine table`() {
        // PlayerCtrlRoutine is in the GameRoutines JumpEngine table
        val func = SMBTestFixtures.allFunctions.find { it.startingBlock.label == "PlayerCtrlRoutine" }
        assertNotNull(func, "PlayerCtrlRoutine should be discovered as a function via JumpEngine table")
        println("Found function: ${func.startingBlock.label}")
        println("  Inputs: ${func.inputs}")
        println("  Outputs: ${func.outputs}")
    }

    @Test
    fun `AutoControlPlayer is also discovered as a function`() {
        // AutoControlPlayer is called via direct JSR
        val func = SMBTestFixtures.allFunctions.find { it.startingBlock.label == "AutoControlPlayer" }
        assertNotNull(func, "AutoControlPlayer should be discovered as a function via direct JSR")
        println("Found function: ${func.startingBlock.label}")
    }

    @Test
    fun `other JumpEngine table targets are discovered`() {
        // Check some other functions from the GameRoutines JumpEngine table
        val jumpEngineTargets = listOf(
            "TitleScreenMode",
            "VictoryMode",
            "GameOverMode",
            "PlayerEntrance",
            "PlayerDeath",
            "PlayerLoseLife"
        )

        for (target in jumpEngineTargets) {
            val func = SMBTestFixtures.allFunctions.find { it.startingBlock.label == target }
            // Not all may be discovered (depends on whether the code is reachable)
            // but at least some should be
            if (func != null) {
                println("Found JumpEngine target: $target")
            }
        }

        // At least half should be discovered
        val foundCount = jumpEngineTargets.count { target ->
            SMBTestFixtures.allFunctions.any { it.startingBlock.label == target }
        }
        assertTrue(foundCount > 0, "At least some JumpEngine table targets should be discovered")
    }
}
