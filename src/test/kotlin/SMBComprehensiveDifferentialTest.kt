package com.ivieleague.decompiler6502tokotlin.hand

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Comprehensive differential tests for ALL Super Mario Bros. functions.
 *
 * Tests each function with 10 random initial states:
 * - Executes in interpreter
 * - Executes generated Kotlin code
 * - Compares all RAM (0x0000-0x2FFF) plus registers/flags
 *
 * Generated automatically from SMB disassembly analysis.
 * Total functions tested: 59 (42 leaf + NON_42 non-leaf)
 */
class SMBComprehensiveDifferentialTest {

    // ========================================================================
    // LEAF FUNCTIONS (42)
    // These functions don't call other functions - easiest to test
    // ========================================================================
    @Test
    fun test_BoundingBoxCore() {
        val result = SMBDifferentialTestGenerator.testFunction("BoundingBoxCore", numTests = 10)
        println("BoundingBoxCore: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_CheckForCoinMTiles() {
        val result = SMBDifferentialTestGenerator.testFunction("CheckForCoinMTiles", numTests = 10)
        println("CheckForCoinMTiles: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_CyclePlayerPalette() {
        val result = SMBDifferentialTestGenerator.testFunction("CyclePlayerPalette", numTests = 10)
        println("CyclePlayerPalette: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_DoNothing2() {
        val result = SMBDifferentialTestGenerator.testFunction("DoNothing2", numTests = 10)
        println("DoNothing2: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_DonePlayerTask() {
        val result = SMBDifferentialTestGenerator.testFunction("DonePlayerTask", numTests = 10)
        println("DonePlayerTask: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_Dump_Sq2_Regs() {
        val result = SMBDifferentialTestGenerator.testFunction("Dump_Sq2_Regs", numTests = 10)
        println("Dump_Sq2_Regs: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_Dump_Squ1_Regs() {
        val result = SMBDifferentialTestGenerator.testFunction("Dump_Squ1_Regs", numTests = 10)
        println("Dump_Squ1_Regs: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_EnemyGfxHandler() {
        val result = SMBDifferentialTestGenerator.testFunction("EnemyGfxHandler", numTests = 10)
        println("EnemyGfxHandler: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_EraseEnemyObject() {
        val result = SMBDifferentialTestGenerator.testFunction("EraseEnemyObject", numTests = 10)
        println("EraseEnemyObject: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_ExecGameLoopback() {
        val result = SMBDifferentialTestGenerator.testFunction("ExecGameLoopback", numTests = 10)
        println("ExecGameLoopback: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_FindAreaPointer() {
        val result = SMBDifferentialTestGenerator.testFunction("FindAreaPointer", numTests = 10)
        println("FindAreaPointer: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_FirebarSpin() {
        val result = SMBDifferentialTestGenerator.testFunction("FirebarSpin", numTests = 10)
        println("FirebarSpin: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_GetAreaObjXPosition() {
        val result = SMBDifferentialTestGenerator.testFunction("GetAreaObjXPosition", numTests = 10)
        println("GetAreaObjXPosition: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_GetAreaObjYPosition() {
        val result = SMBDifferentialTestGenerator.testFunction("GetAreaObjYPosition", numTests = 10)
        println("GetAreaObjYPosition: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_GetAreaType() {
        val result = SMBDifferentialTestGenerator.testFunction("GetAreaType", numTests = 10)
        println("GetAreaType: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_GetBlockBufferAddr() {
        val result = SMBDifferentialTestGenerator.testFunction("GetBlockBufferAddr", numTests = 10)
        println("GetBlockBufferAddr: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_GetEnemyBoundBoxOfsArg() {
        val result = SMBDifferentialTestGenerator.testFunction("GetEnemyBoundBoxOfsArg", numTests = 10)
        println("GetEnemyBoundBoxOfsArg: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_GetLrgObjAttrib() {
        val result = SMBDifferentialTestGenerator.testFunction("GetLrgObjAttrib", numTests = 10)
        println("GetLrgObjAttrib: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_GetObjRelativePosition() {
        val result = SMBDifferentialTestGenerator.testFunction("GetObjRelativePosition", numTests = 10)
        println("GetObjRelativePosition: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_GetProperObjOffset() {
        val result = SMBDifferentialTestGenerator.testFunction("GetProperObjOffset", numTests = 10)
        println("GetProperObjOffset: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_GetScreenPosition() {
        val result = SMBDifferentialTestGenerator.testFunction("GetScreenPosition", numTests = 10)
        println("GetScreenPosition: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_GoContinue() {
        val result = SMBDifferentialTestGenerator.testFunction("GoContinue", numTests = 10)
        println("GoContinue: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_HandlePipeEntry() {
        val result = SMBDifferentialTestGenerator.testFunction("HandlePipeEntry", numTests = 10)
        println("HandlePipeEntry: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_IncAreaObjOffset() {
        val result = SMBDifferentialTestGenerator.testFunction("IncAreaObjOffset", numTests = 10)
        println("IncAreaObjOffset: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_InitBlock_XY_Pos() {
        val result = SMBDifferentialTestGenerator.testFunction("InitBlock_XY_Pos", numTests = 10)
        println("InitBlock_XY_Pos: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_InitScroll() {
        val result = SMBDifferentialTestGenerator.testFunction("InitScroll", numTests = 10)
        println("InitScroll: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_InitVStf() {
        val result = SMBDifferentialTestGenerator.testFunction("InitVStf", numTests = 10)
        println("InitVStf: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_LoadEnvelopeData() {
        val result = SMBDifferentialTestGenerator.testFunction("LoadEnvelopeData", numTests = 10)
        println("LoadEnvelopeData: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_MoveLiftPlatforms() {
        val result = SMBDifferentialTestGenerator.testFunction("MoveLiftPlatforms", numTests = 10)
        println("MoveLiftPlatforms: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_MovePlayerYAxis() {
        val result = SMBDifferentialTestGenerator.testFunction("MovePlayerYAxis", numTests = 10)
        println("MovePlayerYAxis: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_MusicHandler() {
        val result = SMBDifferentialTestGenerator.testFunction("MusicHandler", numTests = 10)
        println("MusicHandler: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_OperModeExecutionTree() {
        val result = SMBDifferentialTestGenerator.testFunction("OperModeExecutionTree", numTests = 10)
        println("OperModeExecutionTree: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_PlayerEnemyDiff() {
        val result = SMBDifferentialTestGenerator.testFunction("PlayerEnemyDiff", numTests = 10)
        println("PlayerEnemyDiff: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_PosPlatform() {
        val result = SMBDifferentialTestGenerator.testFunction("PosPlatform", numTests = 10)
        println("PosPlatform: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_ProcessLengthData() {
        val result = SMBDifferentialTestGenerator.testFunction("ProcessLengthData", numTests = 10)
        println("ProcessLengthData: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_ReadJoypads() {
        val result = SMBDifferentialTestGenerator.testFunction("ReadJoypads", numTests = 10)
        println("ReadJoypads: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_RemBridge() {
        val result = SMBDifferentialTestGenerator.testFunction("RemBridge", numTests = 10)
        println("RemBridge: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_ResetPalStar() {
        val result = SMBDifferentialTestGenerator.testFunction("ResetPalStar", numTests = 10)
        println("ResetPalStar: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_SoundEngine() {
        val result = SMBDifferentialTestGenerator.testFunction("SoundEngine", numTests = 10)
        println("SoundEngine: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_SpawnBrickChunks() {
        val result = SMBDifferentialTestGenerator.testFunction("SpawnBrickChunks", numTests = 10)
        println("SpawnBrickChunks: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_SubtEnemyYPos() {
        val result = SMBDifferentialTestGenerator.testFunction("SubtEnemyYPos", numTests = 10)
        println("SubtEnemyYPos: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_WritePPUReg1() {
        val result = SMBDifferentialTestGenerator.testFunction("WritePPUReg1", numTests = 10)
        println("WritePPUReg1: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    // ========================================================================
    // NON-LEAF FUNCTIONS (17)
    // These functions call other functions - may need mocking
    // ========================================================================
    @Test
    fun test_BrickShatter() {
        val result = SMBDifferentialTestGenerator.testFunction("BrickShatter", numTests = 10)
        println("BrickShatter: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_CheckForClimbMTiles() {
        val result = SMBDifferentialTestGenerator.testFunction("CheckForClimbMTiles", numTests = 10)
        println("CheckForClimbMTiles: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_CheckForSolidMTiles() {
        val result = SMBDifferentialTestGenerator.testFunction("CheckForSolidMTiles", numTests = 10)
        println("CheckForSolidMTiles: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_DrawExplosion_Fireworks() {
        val result = SMBDifferentialTestGenerator.testFunction("DrawExplosion_Fireworks", numTests = 10)
        println("DrawExplosion_Fireworks: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_DrawPlayerLoop() {
        val result = SMBDifferentialTestGenerator.testFunction("DrawPlayerLoop", numTests = 10)
        println("DrawPlayerLoop: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_EnemyLanding() {
        val result = SMBDifferentialTestGenerator.testFunction("EnemyLanding", numTests = 10)
        println("EnemyLanding: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_FireballBGCollision() {
        val result = SMBDifferentialTestGenerator.testFunction("FireballBGCollision", numTests = 10)
        println("FireballBGCollision: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_GameCoreRoutine() {
        val result = SMBDifferentialTestGenerator.testFunction("GameCoreRoutine", numTests = 10)
        println("GameCoreRoutine: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_GetPipeHeight() {
        val result = SMBDifferentialTestGenerator.testFunction("GetPipeHeight", numTests = 10)
        println("GetPipeHeight: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_KillEnemyAboveBlock() {
        val result = SMBDifferentialTestGenerator.testFunction("KillEnemyAboveBlock", numTests = 10)
        println("KillEnemyAboveBlock: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_MoveESprColOffscreen() {
        val result = SMBDifferentialTestGenerator.testFunction("MoveESprColOffscreen", numTests = 10)
        println("MoveESprColOffscreen: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_MoveEnemyHorizontally() {
        val result = SMBDifferentialTestGenerator.testFunction("MoveEnemyHorizontally", numTests = 10)
        println("MoveEnemyHorizontally: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_ReplaceBlockMetatile() {
        val result = SMBDifferentialTestGenerator.testFunction("ReplaceBlockMetatile", numTests = 10)
        println("ReplaceBlockMetatile: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_SetXMoveAmt() {
        val result = SMBDifferentialTestGenerator.testFunction("SetXMoveAmt", numTests = 10)
        println("SetXMoveAmt: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_StopPlatforms() {
        val result = SMBDifferentialTestGenerator.testFunction("StopPlatforms", numTests = 10)
        println("StopPlatforms: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_TerminateGame() {
        val result = SMBDifferentialTestGenerator.testFunction("TerminateGame", numTests = 10)
        println("TerminateGame: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    @Test
    fun test_VariableObjOfsRelPos() {
        val result = SMBDifferentialTestGenerator.testFunction("VariableObjOfsRelPos", numTests = 10)
        println("VariableObjOfsRelPos: ${result.passedTests}/${result.totalTests} passed (${"%.1f".format(result.passRate * 100)}%)")

        if (result.failedTests > 0) {
            println("  Failed tests:")
            result.testCases.filter { !it.passed }.forEach { testCase ->
                println("    Test #${testCase.testNumber}:")
                testCase.differences.take(5).forEach { diff ->
                    println("      - $diff")
                }
                if (testCase.differences.size > 5) {
                    println("      ... and ${testCase.differences.size - 5} more differences")
                }
            }
        }
    }

    // ========================================================================
    // Summary Test - Run All Functions
    // ========================================================================

    @Test
    fun testAllFunctions_Summary() {
        val allFunctions = listOf(
            "BoundingBoxCore",
            "CheckForCoinMTiles",
            "CyclePlayerPalette",
            "DoNothing2",
            "DonePlayerTask",
            "Dump_Sq2_Regs",
            "Dump_Squ1_Regs",
            "EnemyGfxHandler",
            "EraseEnemyObject",
            "ExecGameLoopback",
            "FindAreaPointer",
            "FirebarSpin",
            "GetAreaObjXPosition",
            "GetAreaObjYPosition",
            "GetAreaType",
            "GetBlockBufferAddr",
            "GetEnemyBoundBoxOfsArg",
            "GetLrgObjAttrib",
            "GetObjRelativePosition",
            "GetProperObjOffset",
            "GetScreenPosition",
            "GoContinue",
            "HandlePipeEntry",
            "IncAreaObjOffset",
            "InitBlock_XY_Pos",
            "InitScroll",
            "InitVStf",
            "LoadEnvelopeData",
            "MoveLiftPlatforms",
            "MovePlayerYAxis",
            "MusicHandler",
            "OperModeExecutionTree",
            "PlayerEnemyDiff",
            "PosPlatform",
            "ProcessLengthData",
            "ReadJoypads",
            "RemBridge",
            "ResetPalStar",
            "SoundEngine",
            "SpawnBrickChunks",
            "SubtEnemyYPos",
            "WritePPUReg1",
            "BrickShatter",
            "CheckForClimbMTiles",
            "CheckForSolidMTiles",
            "DrawExplosion_Fireworks",
            "DrawPlayerLoop",
            "EnemyLanding",
            "FireballBGCollision",
            "GameCoreRoutine",
            "GetPipeHeight",
            "KillEnemyAboveBlock",
            "MoveESprColOffscreen",
            "MoveEnemyHorizontally",
            "ReplaceBlockMetatile",
            "SetXMoveAmt",
            "StopPlatforms",
            "TerminateGame",
            "VariableObjOfsRelPos"
        )

        println("=" . repeat(80))
        println("SMB Comprehensive Differential Test - Summary")
        println("=" . repeat(80))
        println("Testing ${allFunctions.size} functions...")
        println()

        val results = mutableListOf<SMBDifferentialTestGenerator.FunctionTestResult>()

        allFunctions.forEach { functionName ->
            try {
                val result = SMBDifferentialTestGenerator.testFunction(functionName, numTests = 10)
                results.add(result)

                val status = if (result.passRate == 1.0) "✓ PASS" else "✗ FAIL"
                println("$status  ${functionName.padEnd(35)} ${result.passedTests}/10 (${"%.0f".format(result.passRate * 100)}%)")

            } catch (e: Exception) {
                println("✗ ERROR ${functionName.padEnd(35)} ${e.message}")
            }
        }

        println()
        println("=" . repeat(80))
        println("Summary:")
        println("-" . repeat(80))

        val totalFunctions = results.size
        val fullyPassing = results.count { it.passRate == 1.0 }
        val partiallyPassing = results.count { it.passRate > 0.0 && it.passRate < 1.0 }
        val fullyFailing = results.count { it.passRate == 0.0 }

        val totalTests = results.sumOf { it.totalTests }
        val totalPassed = results.sumOf { it.passedTests }
        val overallPassRate = if (totalTests > 0) totalPassed.toDouble() / totalTests else 0.0

        println("Functions tested: $totalFunctions")
        println("  Fully passing (100%): $fullyPassing")
        println("  Partially passing: $partiallyPassing")
        println("  Fully failing (0%): $fullyFailing")
        println()
        println("Total test cases: $totalTests")
        println("  Passed: $totalPassed")
        println("  Failed: ${totalTests - totalPassed}")
        println("  Overall pass rate: ${"%.1f".format(overallPassRate * 100)}%")
        println("=" . repeat(80))

        // Save detailed results
        saveDetailedResults(results)
    }

    private fun saveDetailedResults(results: List<SMBDifferentialTestGenerator.FunctionTestResult>) {
        // TODO: Save to file for analysis
    }
}
