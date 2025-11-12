package com.ivieleague.decompiler6502tokotlin.hand.stages

import com.ivieleague.decompiler6502tokotlin.hand.stages.SMBTestFixtures.loadFunction
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Golden output tests capture the "correct" decompiled output for SMB functions
 * and verify that future changes don't regress the quality of decompilation.
 *
 * These tests compare the current decompiled output against saved golden files.
 * If the output changes, the test will fail, allowing manual verification of
 * whether the change is an improvement or a regression.
 */
class SMBGoldenOutputTest {

    private val goldenDir = File("src/test/resources/golden-output")

    init {
        if (!goldenDir.exists()) {
            goldenDir.mkdirs()
        }
    }

    /**
     * Get the decompiled output for a function.
     * This is a simplified version - real implementation would use the full decompiler pipeline.
     */
    private fun getFunctionOutput(funcName: String): String {
        val func = loadFunction(funcName)

        return buildString {
            appendLine("Function: ${func.startingBlock.label}")
            appendLine("Inputs: ${func.inputs?.joinToString()}")
            appendLine("Outputs: ${func.outputs?.joinToString()}")
            appendLine("Clobbers: ${func.clobbers?.joinToString()}")
            appendLine("Control flow nodes: ${func.asControls?.size}")
            // TODO: Add actual decompiled Kotlin code here
        }
    }

    private fun verifyOrUpdateGolden(funcName: String, updateMode: Boolean = false) {
        val output = getFunctionOutput(funcName)
        val goldenFile = File(goldenDir, "$funcName.txt")

        if (updateMode || !goldenFile.exists()) {
            goldenFile.writeText(output)
            println("Updated golden output for $funcName")
        } else {
            val expected = goldenFile.readText()
            assertEquals(expected, output,
                "Golden output mismatch for $funcName. " +
                "Run with UPDATE_GOLDEN=true to update golden files.")
        }
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `InitializeMemory golden output matches`() {
        val updateMode = System.getenv("UPDATE_GOLDEN")?.toBoolean() ?: false
        verifyOrUpdateGolden("InitializeMemory", updateMode)
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `FloateyNumbersRoutine golden output matches`() {
        val updateMode = System.getenv("UPDATE_GOLDEN")?.toBoolean() ?: false
        verifyOrUpdateGolden("FloateyNumbersRoutine", updateMode)
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `MusicHandler golden output matches`() {
        val updateMode = System.getenv("UPDATE_GOLDEN")?.toBoolean() ?: false
        verifyOrUpdateGolden("MusicHandler", updateMode)
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `MoveSpritesOffscreen golden output matches`() {
        val updateMode = System.getenv("UPDATE_GOLDEN")?.toBoolean() ?: false
        verifyOrUpdateGolden("MoveSpritesOffscreen", updateMode)
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `GetAreaObjXPosition golden output matches`() {
        val updateMode = System.getenv("UPDATE_GOLDEN")?.toBoolean() ?: false
        verifyOrUpdateGolden("GetAreaObjXPosition", updateMode)
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `GetAreaObjYPosition golden output matches`() {
        val updateMode = System.getenv("UPDATE_GOLDEN")?.toBoolean() ?: false
        verifyOrUpdateGolden("GetAreaObjYPosition", updateMode)
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `PlayerEnemyDiff golden output matches`() {
        val updateMode = System.getenv("UPDATE_GOLDEN")?.toBoolean() ?: false
        verifyOrUpdateGolden("PlayerEnemyDiff", updateMode)
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `InitVStf golden output matches`() {
        val updateMode = System.getenv("UPDATE_GOLDEN")?.toBoolean() ?: false
        verifyOrUpdateGolden("InitVStf", updateMode)
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `PlayerLakituDiff golden output matches`() {
        val updateMode = System.getenv("UPDATE_GOLDEN")?.toBoolean() ?: false
        verifyOrUpdateGolden("PlayerLakituDiff", updateMode)
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `CheckForSolidMTiles golden output matches`() {
        val updateMode = System.getenv("UPDATE_GOLDEN")?.toBoolean() ?: false
        verifyOrUpdateGolden("CheckForSolidMTiles", updateMode)
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `CheckPlayerVertical golden output matches`() {
        val updateMode = System.getenv("UPDATE_GOLDEN")?.toBoolean() ?: false
        verifyOrUpdateGolden("CheckPlayerVertical", updateMode)
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `HandlePipeEntry golden output matches`() {
        val updateMode = System.getenv("UPDATE_GOLDEN")?.toBoolean() ?: false
        verifyOrUpdateGolden("HandlePipeEntry", updateMode)
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `OffscreenBoundsCheck golden output matches`() {
        val updateMode = System.getenv("UPDATE_GOLDEN")?.toBoolean() ?: false
        verifyOrUpdateGolden("OffscreenBoundsCheck", updateMode)
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `BubbleCheck golden output matches`() {
        val updateMode = System.getenv("UPDATE_GOLDEN")?.toBoolean() ?: false
        verifyOrUpdateGolden("BubbleCheck", updateMode)
    }
}
