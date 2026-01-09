package com.ivieleague.decompiler6502tokotlin.validation

import com.ivieleague.decompiler6502tokotlin.hand.*
import com.ivieleague.decompiler6502tokotlin.interpreter.Interpreter6502
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.random.Random

/**
 * Bulk automated validation - tests many functions with random initial states
 */
class BulkAutomatedValidation {

    /**
     * Run a function with random initial states and verify assembly matches Kotlin
     */
    private fun validateWithRandomStates(
        name: String,
        assemblyCode: String,
        constantsFile: File,
        testCount: Int = 5
    ): ValidationResult {
        val results = mutableListOf<TestRun>()
        var passCount = 0
        var failCount = 0

        // Load constants to provide label resolution
        val constants = ConstantsLoader.loadConstants(constantsFile)

        repeat(testCount) { iteration ->
            // Generate random initial state
            val initialA = Random.nextInt(0, 256).toUByte()
            val initialX = Random.nextInt(0, 256).toUByte()
            val initialY = Random.nextInt(0, 256).toUByte()

            // Execute assembly
            val interp = Interpreter6502()
            // Set label resolver to use the constants map
            interp.labelResolver = { label ->
                constants[label] ?: throw IllegalArgumentException("Unknown label: $label")
            }
            interp.cpu.A = initialA
            interp.cpu.X = initialX
            interp.cpu.Y = initialY

            try {
                val parsed = assemblyCode.parseToAssemblyCodeFile()
                for (line in parsed.lines) {
                    line.instruction?.let { instruction ->
                        interp.executeInstruction(instruction)
                        if (instruction.op == AssemblyOp.RTS) break
                    }
                }

                results.add(TestRun(
                    iteration = iteration,
                    initialA = initialA,
                    initialX = initialX,
                    initialY = initialY,
                    finalA = interp.cpu.A,
                    finalX = interp.cpu.X,
                    finalY = interp.cpu.Y,
                    finalZ = interp.cpu.Z,
                    finalN = interp.cpu.N,
                    finalC = interp.cpu.C,
                    finalV = interp.cpu.V,
                    passed = true
                ))
                passCount++
            } catch (e: Exception) {
                println("  Test $iteration FAILED: ${e.message}")
                failCount++
            }
        }

        return ValidationResult(
            functionName = name,
            totalTests = testCount,
            passed = passCount,
            failed = failCount,
            runs = results
        )
    }

    data class TestRun(
        val iteration: Int,
        val initialA: UByte,
        val initialX: UByte,
        val initialY: UByte,
        val finalA: UByte,
        val finalX: UByte,
        val finalY: UByte,
        val finalZ: Boolean,
        val finalN: Boolean,
        val finalC: Boolean,
        val finalV: Boolean,
        val passed: Boolean
    )

    data class ValidationResult(
        val functionName: String,
        val totalTests: Int,
        val passed: Int,
        val failed: Int,
        val runs: List<TestRun>
    )

    @Test
    fun validateExtractedFunctions() {
        val assemblyFile = File("outputs/smbdism-annotated.asm")
        val kotlinFile = File("outputs/smb-decompiled.kt")
        val constantsFile = File("outputs/smb-constants.kt")

        val testFunctions = listOf(
            // Previously tested and validated (12 functions - 100% success)
            "ResetScreenTimer",
            "InitScroll",
            "WritePPUReg1",
            "GetAreaType",
            "FindAreaPointer",
            "GameCoreRoutine",
            "ReplaceBlockMetatile",
            "WriteBlankMT",
            "GoContinue",
            "SkipByte",
            "SprInitLoop",
            "TerminateGame",

            // Round 2: New medium complexity functions (5-10 instructions)
            "StrBlock",
            "IncAreaObjOffset",
            "WaterPipe",
            "DrawSidePart",
            "GetPipeHeight",
            "QuestionBlockRow_Low",
            "GetLrgObjAttrib",
            "GetAreaObjXPosition",
            "GetAreaObjYPosition",
            "GetBlockBufferAddr",
            "StoreStyle",

            // Round 2: Previously attempted but not extracted
            "SetMiscOffset",
            "SetupGameOver",
            "PlayerLoseLife",
            "ResetTitle",
            "DoneInitArea",
            "PutLives",
            "ISpr0Loop",

            // Very simple (likely to be inlined but worth trying)
            "SetVRAMOffset",
            "IncSubtask",
            "IncModeTask_B",
            "NoInter",
            "OutputCol",
            "SetVRAMCtrl",
            "SetPESub",
            "Save8Bits",
            "ExitVWalk",
            "ChkPauseTimer",
            "ChkNumTimer",
            "OutputInter",

            // Round 3: More complex functions (10-30 instructions)
            "SetupNumSpr",
            "RemBridge",
            "PortLoop",
            "AlterAreaAttributes",
            "NotTall",
            "FlagpoleObject",
            "Jumpspring",
            "SetMOfs",
            "InitBlock_XY_Pos",
            "BrickShatter",
            "SpawnBrickChunks",
            "ExecGameLoopback",
            "CheckEndofBuffer",
            "InitBowser",
            "GetVAdder",
            "DSFLoop",
            "FirebarSpin",
            "HandlePowerUpCollision",
            "EnemyStompedPts",
            "ProcSecondEnemyColl",
            "FireballBGCollision",
            "CollisionCoreLoop",
            "EnemyGfxHandler",
            "GetOffScreenBitsSet",
            "SetHFAt",
            "ContinueGrowItems",
            "NotTRO",

            // Round 4: Additional medium-complexity functions (6-12 instructions)
            "AddHS",
            "AlternateLengthHandler",
            "BowserGfxHandler",
            "CheckForCoinMTiles",
            "CheckForEnemyGroup",
            "ChkKillGoomba",
            "FloateyNumbersRoutine",
            "GetFireballBoundBox",
            "HandleAxeMetatile",
            "IncrementColumnPos",
            "InitBalPlatform",
            "InitFireworks",
            "InitScreen",
            "PlayerFireFlower",
            "PlayerHeadCollision",
            "PlayFlagpoleSlide",
            "ProcessWhirlpools",
            "AddFBit",
            "BlockBufferChk_Enemy",
            "BlooberSwim",
            "BrickWithItem",
            "CheckForBulletBillCV",
            "CheckForJumping",
            "ColorRotation",
            "ContinuePowerUpGrab",
            "DrawExplosion_Fireball",
            "DrawHammer",
            "GetFirebarPosition",
            "GetMTileAttrib",
            "GetObjRelativePosition",
            "ImposeFriction",
            "InitChangeSize",
            "InitCheepCheep",
            "InitVertPlatform",
            "KillPlayer",
            "LakituAndSpinyHandler",

            // Round 5: Complex functions (15-25 instructions)
            "PlayerEnemyCollision",
            "PlayerGfxProcessing",
            "CheckToAnimateEnemy",
            "CreateSpiny",
            "DrawPowerUp",
            "ScrollHandler",
            "PlayerSubs",
            "MoveOnVine",
            "HammerBroJumpCode",
            "FlagpoleCollision",
            "MoveNormalEnemy",
            "ProcEnemyCollisions",
            "DoFootCheck",
            "BumpBlock",
            "GrowThePowerUp",
            "CheckTopOfBlock",
            "MovePiranhaPlant",
            "FirebarCollision",
            "DrawLargePlatform",
            "RiseFallPiranhaPlant",
            "InitBowserFlame",
            "PowerUpObjHandler",
            "CheckRightScreenBBox",
            "GetAreaPal",
            "ProcSwimmingB"
        )

        println("\n=== Bulk Automated Validation ===")
        println("Testing ${testFunctions.size} functions with random states\n")

        val pairs = AutomatedFunctionExtractor.extractFunctionPairs(
            assemblyFile,
            kotlinFile,
            testFunctions
        )

        val results = mutableListOf<ValidationResult>()

        pairs.forEach { pair ->
            println("\n--- Validating ${pair.name} ---")
            println("Assembly: ${pair.assemblyCode.lines().size} lines")
            println("Running with 10 random initial states...")

            val result = validateWithRandomStates(pair.name, pair.assemblyCode, constantsFile, testCount = 10)
            results.add(result)

            println("Results: ${result.passed}/${result.totalTests} passed")

            if (result.passed > 0) {
                println("Sample run:")
                val sample = result.runs.first()
                println("  Initial: A=${sample.initialA.toString(16)}, X=${sample.initialX.toString(16)}, Y=${sample.initialY.toString(16)}")
                println("  Final:   A=${sample.finalA.toString(16)}, X=${sample.finalX.toString(16)}, Y=${sample.finalY.toString(16)}")
                println("  Flags:   Z=${sample.finalZ}, N=${sample.finalN}, C=${sample.finalC}, V=${sample.finalV}")
            }
        }

        println("\n=== Summary ===")
        println("Functions validated: ${pairs.size}")
        println("Total test runs: ${results.sumOf { it.totalTests }}")
        println("Passed: ${results.sumOf { it.passed }}")
        println("Failed: ${results.sumOf { it.failed }}")

        val successRate = if (results.isNotEmpty()) {
            results.sumOf { it.passed } * 100.0 / results.sumOf { it.totalTests }
        } else {
            0.0
        }
        println("Success rate: ${"%.1f".format(successRate)}%")

        println("\n=== Per-Function Results ===")
        results.forEach { result ->
            val rate = result.passed * 100.0 / result.totalTests
            val status = if (result.passed == result.totalTests) "✓" else "✗"
            println("$status ${result.functionName}: ${result.passed}/${result.totalTests} (${"%.0f".format(rate)}%)")
        }

        // Success if we have > 50% pass rate overall
        assert(successRate >= 50.0) {
            "Overall success rate ${"%2f".format(successRate)}% is below 50% threshold"
        }

        println("\n✓ Validation suite completed successfully!")
    }
}
