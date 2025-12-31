package com.ivieleague.decompiler6502tokotlin.hand.stages

import com.ivieleague.decompiler6502tokotlin.hand.stages.SMBTestFixtures.getBlocksForFunction
import com.ivieleague.decompiler6502tokotlin.hand.validateAllConsistency
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit

/**
 * Tests that blockify() correctly creates control flow graph structures
 * for real SMB functions.
 */
class SMBBlockificationTest {

    @Test
    @Timeout(5, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `ChkPauseTimer creates correct block structure`() {
        val blocks = getBlocksForFunction("ChkPauseTimer")

        blocks.assertStructure {
            // Should have entry block
            assertBlock("ChkPauseTimer") {
                // LDA GamePauseTimer, BEQ ChkStart
                hasInstructionCount(2)
                hasBranch("ChkStart")
                // Fall through to next block (DEC, RTS)
            }

            assertBlock("ChkStart") {
                // LDA SavedJoypad1Bits, AND #Start_Button, BEQ ClrPauseTimer
                hasBranch("ClrPauseTimer")
                hasEnteredFrom("ChkPauseTimer")
            }

            assertBlock("ClrPauseTimer") {
                // LDA GamePauseStatus, AND #%01111111
                hasEnteredFrom("ChkStart")
                hasFallThrough("SetPause")
            }

            assertBlock("SetPause") {
                // STA GamePauseStatus
                hasFallThrough("ExitPause")
            }

            assertBlock("ExitPause") {
                // RTS
                isReturn()
            }
        }
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `blockify maintains bidirectional consistency for all test functions`() {
        val testFunctions = listOf(
            "ChkPauseTimer",
            "InitializeMemory",
            "FloateyNumbersRoutine",
            "GameTimerFireworks",
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

        for (funcName in testFunctions) {
            val blocks = getBlocksForFunction(funcName)
            // All blocks should have consistent forward/backward references
            blocks.validateAllConsistency()
        }
    }

    @Test
    @Timeout(5, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `InitializeMemory creates nested loop structure`() {
        val blocks = getBlocksForFunction("InitializeMemory")

        blocks.assertStructure {
            // Entry block sets up initial values
            assertBlock("InitializeMemory") {
                hasInstructionCount(3) // LDX, LDA, STA
                hasFallThrough("InitPageLoop")
            }

            // Outer loop header
            assertBlock("InitPageLoop") {
                hasInstructionCount(1) // STX
                hasFallThrough("InitByteLoop")
                // Should be entered from entry and from outer loop back edge
            }

            // Inner loop header
            assertBlock("InitByteLoop") {
                hasInstructionCount(2) // CPX, BNE
                hasBranch("InitByte")
                // Should be entered from InitPageLoop and from inner loop back edge
            }

            // InitByte writes memory
            assertBlock("InitByte") {
                hasInstructionCount(1) // STA
                hasFallThrough("SkipByte")
            }

            // SkipByte has inner loop back edge
            assertBlock("SkipByte") {
                hasInstructionCount(3) // DEY, CPY, BNE
                hasBranch("InitByteLoop") // Back edge to inner loop!
            }
        }

        // Verify bidirectional consistency
        blocks.validateAllConsistency()
    }

    @Test
    @Timeout(5, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `GameTimerFireworks creates if-else-if chain with merge point`() {
        val blocks = getBlocksForFunction("GameTimerFireworks")

        blocks.assertStructure {
            // Entry block checks first condition
            assertBlock("GameTimerFireworks") {
                hasInstructionCount(4) // LDY, LDA, CMP, BEQ
                hasBranch("SetFWC") // Branch on first match
                // Falls through to next check
            }

            // SetFWC is the merge point where all branches converge
            assertBlock("SetFWC") {
                hasInstructionCount(2) // STA, STY
                hasFallThrough("IncrementSFTask1")
                // Should have multiple predecessors (from all the if branches)
            }

            assertBlock("IncrementSFTask1") {
                hasInstructionCount(1) // INC
                hasFallThrough("StarFlagExit")
            }

            assertBlock("StarFlagExit") {
                hasInstructionCount(1) // RTS
                isReturn()
            }
        }

        // Verify bidirectional consistency
        blocks.validateAllConsistency()
    }

    @Test
    @Timeout(5, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `FloateyNumbersRoutine has branch to earlier block EndExitOne`() {
        val blocks = getBlocksForFunction("FloateyNumbersRoutine")

        blocks.assertStructure {
            assertBlock("FloateyNumbersRoutine") {
                // Should branch to EndExitOne (which appears earlier in source)
                hasBranch("EndExitOne")
            }

            // EndExitOne should be reachable from multiple sources
            assertBlock("EndExitOne") {
                // Will have multiple predecessors
                isReturn()
            }
        }
    }

    @Test
    @Timeout(5, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `MoveSpritesOffscreen creates simple loop structure`() {
        val blocks = getBlocksForFunction("MoveSpritesOffscreen")

        blocks.assertStructure {
            // Entry block initializes loop
            assertBlock("MoveSpritesOffscreen") {
                hasInstructionCount(2) // LDY #$04, LDA #$f8
                hasFallThrough("SprInitLoop")
            }

            // Loop body - stores A, increments Y by 4, and branches back
            assertBlock("SprInitLoop") {
                hasInstructionCount(6) // STA, INY (4x), BNE
                hasBranch("SprInitLoop") // Back edge - loops until Y wraps to 0
            }
        }

        blocks.validateAllConsistency()
    }

    @Test
    @Timeout(5, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `GetAreaObjXPosition creates straight line code`() {
        val blocks = getBlocksForFunction("GetAreaObjXPosition")

        blocks.assertStructure {
            // Single block with simple arithmetic
            assertBlock("GetAreaObjXPosition") {
                hasInstructionCount(6) // LDA, ASL (4x), RTS
                isReturn()
            }
        }

        blocks.validateAllConsistency()
    }

    @Test
    @Timeout(5, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `GetAreaObjYPosition creates straight line code with addition`() {
        val blocks = getBlocksForFunction("GetAreaObjYPosition")

        blocks.assertStructure {
            // Single block with arithmetic and addition
            assertBlock("GetAreaObjYPosition") {
                hasInstructionCount(8) // LDA, ASL (4x), CLC, ADC, RTS
                isReturn()
            }
        }

        blocks.validateAllConsistency()
    }

    @Test
    @Timeout(5, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `PlayerEnemyDiff calculates position difference`() {
        val blocks = getBlocksForFunction("PlayerEnemyDiff")

        blocks.assertStructure {
            // Single block - subtraction with borrow
            assertBlock("PlayerEnemyDiff") {
                hasInstructionCount(7) // LDA, SEC, SBC, STA, LDA, SBC, RTS
                isReturn()
            }
        }

        blocks.validateAllConsistency()
    }

    @Test
    @Timeout(5, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `InitVStf initializes vertical speed to zero`() {
        val blocks = getBlocksForFunction("InitVStf")

        blocks.assertStructure {
            // Single block - initialize two memory locations to zero
            assertBlock("InitVStf") {
                hasInstructionCount(4) // LDA, STA, STA, RTS
                isReturn()
            }
        }

        blocks.validateAllConsistency()
    }

    @Test
    @Timeout(5, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `PlayerLakituDiff has conditional branches`() {
        val blocks = getBlocksForFunction("PlayerLakituDiff")

        blocks.assertStructure {
            // Entry block calls PlayerEnemyDiff and branches
            assertBlock("PlayerLakituDiff") {
                hasInstructionCount(3) // LDY, JSR, BPL
                hasBranch("ChkLakDif")
            }

            // ChkLakDif checks distance and branches
            assertBlock("ChkLakDif") {
                // Should have branch instructions
            }
        }

        blocks.validateAllConsistency()
    }

    @Test
    @Timeout(5, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `CheckForSolidMTiles has simple structure with JSR`() {
        val blocks = getBlocksForFunction("CheckForSolidMTiles")

        blocks.assertStructure {
            // Single block - JSR, compare, and return
            assertBlock("CheckForSolidMTiles") {
                hasInstructionCount(3) // JSR, CMP, RTS
                isReturn()
            }
        }

        blocks.validateAllConsistency()
    }

    @Test
    @Timeout(5, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `CheckPlayerVertical has sequential conditional checks`() {
        val blocks = getBlocksForFunction("CheckPlayerVertical")

        blocks.assertStructure {
            // Entry block checks player offscreen bits
            assertBlock("CheckPlayerVertical") {
                // LDA, CMP, BCS ExCPV
                hasBranch("ExCPV")
            }

            // ExCPV is the return point
            assertBlock("ExCPV") {
                isReturn()
            }
        }

        blocks.validateAllConsistency()
    }

    @Test
    @Timeout(5, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `HandlePipeEntry has multiple conditional branches`() {
        val blocks = getBlocksForFunction("HandlePipeEntry")

        blocks.assertStructure {
            // Entry block checks down button
            assertBlock("HandlePipeEntry") {
                // LDA, AND, BEQ ExPipeE
                hasBranch("ExPipeE")
            }

            // ExPipeE is the exit/return point
            assertBlock("ExPipeE") {
                isReturn()
            }
        }

        blocks.validateAllConsistency()
    }

    @Test
    @Timeout(5, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `OffscreenBoundsCheck has complex calculations`() {
        val blocks = getBlocksForFunction("OffscreenBoundsCheck")

        blocks.assertStructure {
            // Entry block checks enemy ID
            assertBlock("OffscreenBoundsCheck") {
                // LDA, CMP, BEQ ExScrnBd
                hasBranch("ExScrnBd")
            }

            // Should have block for exit
            assertBlock("ExScrnBd") {
                isReturn()
            }
        }

        blocks.validateAllConsistency()
    }

    @Test
    @Timeout(5, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `BubbleCheck has state machine structure`() {
        val blocks = getBlocksForFunction("BubbleCheck")

        blocks.assertStructure {
            // Entry block checks bubble position
            assertBlock("BubbleCheck") {
                // LDA, AND, STA, LDA, CMP, BNE MoveBubl
                hasBranch("MoveBubl")
            }

            // Should have blocks for moving bubble and exiting
            assertBlock("ExitBubl") {
                isReturn()
            }
        }

        blocks.validateAllConsistency()
    }
}
