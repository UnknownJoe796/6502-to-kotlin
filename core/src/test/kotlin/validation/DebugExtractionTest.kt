package com.ivieleague.decompiler6502tokotlin.validation

import com.ivieleague.decompiler6502tokotlin.hand.*
import java.io.File
import kotlin.test.Test

class DebugExtractionTest {

    @Test
    fun debugHandlePowerUpCollision() {
        val assemblyFile = File("outputs/smbdism-annotated.asm")

        val asm = AutomatedFunctionExtractor.extractAssemblyFunction(assemblyFile, "HandlePowerUpCollision")

        println("=== Extracted Assembly ===")
        println(asm)
        println("=== END ===")
    }

    @Test
    fun debugInjurePlayer() {
        val assemblyFile = File("outputs/smbdism-annotated.asm")

        val asm = AutomatedFunctionExtractor.extractAssemblyFunction(assemblyFile, "InjurePlayer")

        println("=== Extracted Assembly ===")
        println(asm)
        println("=== END ===")
    }

    @Test
    fun debugOutputInter() {
        val assemblyFile = File("outputs/smbdism-annotated.asm")

        val asm = AutomatedFunctionExtractor.extractAssemblyFunction(assemblyFile, "OutputInter")

        println("=== Extracted Assembly for OutputInter ===")
        println(asm)
        println("=== END ===")
    }

    @Test
    fun debugChkPauseTimer() {
        val assemblyFile = File("outputs/smbdism-annotated.asm")

        val asm = AutomatedFunctionExtractor.extractAssemblyFunction(assemblyFile, "ChkPauseTimer")

        println("=== Extracted Assembly for ChkPauseTimer ===")
        println(asm)

        if (asm != null) {
            println("\n=== Parsing ===")
            val parsed = try {
                asm.parseToAssemblyCodeFile()
            } catch (e: Exception) {
                println("Parse error: ${e.message}")
                e.printStackTrace()
                return
            }

            println("Parsed ${parsed.lines.size} lines")

            println("\n=== Blockifying ===")
            val blocks = try {
                parsed.lines.blockify()
            } catch (e: Exception) {
                println("Blockify error: ${e.message}")
                e.printStackTrace()
                return
            }

            println("Created ${blocks.size} blocks")
            blocks.forEach { block ->
                println("  Block: ${block.label} (${block.lines.size} lines)")
            }
        }
        println("=== END ===")
    }
}
