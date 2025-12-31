package com.ivieleague.decompiler6502tokotlin.validation

import kotlin.test.Test
import java.io.File

/**
 * Automated validation tests that extract and validate many SMB functions
 */
class AutomatedValidationTest {

    @Test
    fun extractAndDisplayFunctions() {
        val assemblyFile = File("outputs/smbdism-annotated.asm")
        val kotlinFile = File("outputs/smb-decompiled.kt")

        val testFunctions = listOf(
            "ResetScreenTimer",
            "InitPlatScrl",
            "SetVRAMCtrl",
            "ResetTitle",
            "ChkPauseTimer",
            "SetPESub",
            "InitScroll",
            "WritePPUReg1",
            "SetFore",
            "ScrollLockObject",
            "NoKillE",
            "WaterPipe",
            "GetAreaType",
            "FindAreaPointer",
            "GameCoreRoutine"
        )

        println("\n=== Extracting ${testFunctions.size} SMB functions ===\n")

        val pairs = AutomatedFunctionExtractor.extractFunctionPairs(
            assemblyFile,
            kotlinFile,
            testFunctions
        )

        println("\n=== Extraction Summary ===")
        println("Attempted: ${testFunctions.size}")
        println("Successful: ${pairs.size}")
        println("Failed: ${testFunctions.size - pairs.size}")

        println("\n=== Function Details ===")
        pairs.forEach { pair ->
            println("\n--- ${pair.name} ---")
            println("Signature: ${pair.signature}")
            println("Assembly lines: ${pair.assemblyCode.lines().size}")
            println("Kotlin lines: ${pair.kotlinCode.lines().size}")

            println("\nFirst 3 assembly instructions:")
            pair.assemblyCode.lines().take(3).forEach { println("  $it") }

            println("\nFirst 3 Kotlin statements:")
            pair.kotlinCode.lines()
                .filter { it.trim().isNotEmpty() && !it.trim().startsWith("//") }
                .take(3)
                .forEach { println("  $it") }
        }
    }
}
