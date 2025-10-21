package com.ivieleague.decompiler6502tokotlin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Demonstration of memory abstraction layer
 *
 * Shows how global memory variables get typed accessors
 * while still maintaining the raw memory array for bulk operations
 */
class MemoryAbstractionDemo {

    @Test
    fun `demonstrate memory abstraction for global variables`() {
        val asm = """
            .export Init, UpdatePlayer, ProcessEnemy

            ; Global variables in zero page
            PlayerX = ${'$'}0086
            PlayerY = ${'$'}0087
            EnemyState = ${'$'}0090

            Init:
                LDA #${'$'}00
                STA PlayerX
                STA PlayerY
                RTS

            UpdatePlayer:
                LDA PlayerX
                CLC
                ADC #${'$'}01
                STA PlayerX
                RTS

            ProcessEnemy:
                LDA EnemyState
                CMP #${'$'}00
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Init", "UpdatePlayer", "ProcessEnemy"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        // Run variable identification
        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val typeInference = codeFile.inferTypes(cfg, dataFlow, constants, memoryPatterns)
        val loops = codeFile.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val variableId = codeFile.identifyVariables(cfg, dataFlow, typeInference, memoryPatterns, loops)

        println("\n=== Memory Abstraction Strategy ===")
        val memoryStrategy = variableId.createMemoryAccessStrategy(null)

        println("Global memory variables: ${memoryStrategy.globalVariables.size}")
        memoryStrategy.globalVariables.forEach { variable ->
            println("  - ${variable.name} at \$${variable.address.toString(16).uppercase()}: ${variable.type}")
        }

        println("\nArray variables: ${memoryStrategy.arrayVariables.size}")
        println("Use raw array access: ${memoryStrategy.useRawArrayAccess}")

        // Emit the Kotlin code
        println("\n=== Generated Kotlin Code ===")
        val result = cfg.emitCompleteKotlin(codeFile, variableId, null, null, null, null)
        println(result.kotlinFile.emitToString())

        // Verify global variables were identified
        assertTrue(memoryStrategy.globalVariables.isNotEmpty(), "Should identify global memory variables")

        // Should identify PlayerX, PlayerY, EnemyState as global (used by multiple functions)
        val addresses = memoryStrategy.globalVariables.map { it.address }.toSet()
        assertTrue(addresses.contains(0x86) || addresses.contains(0x87) || addresses.contains(0x90),
            "Should identify at least one of the player/enemy variables as global")
    }

    @Test
    fun `demonstrate typed memory property emission`() {
        // Create a sample typed memory variable
        val playerXVar = TypedMemoryVariable(
            name = "playerX",
            address = 0x86,
            type = KotlinAst.KotlinType.Int,
            kdoc = "Player X position",
            isMutable = true
        )

        println("\n=== Typed Memory Property (with custom accessor) ===")
        println(playerXVar.emitMemoryPropertyWithAccessors())

        // 16-bit variable
        val enemyXPosVar = TypedMemoryVariable(
            name = "enemyXPos",
            address = 0x90,
            type = KotlinAst.KotlinType.UShort,
            kdoc = "Enemy X position (16-bit)",
            isMutable = true
        )

        println("\n=== 16-bit Typed Memory Property ===")
        println(enemyXPosVar.emitMemoryPropertyWithAccessors())
    }

    @Test
    fun `test memory abstraction preserves raw array`() {
        val asm = """
            Init:
                LDA #${'$'}00
                STA ${'$'}0200
                RTS
        """.trimIndent()

        val codeFile = asm.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Init"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val dominators = codeFile.constructDominatorTrees(cfg)
        val constants = codeFile.analyzeConstants(cfg)
        val dataFlow = codeFile.analyzeDataFlow(cfg, dominators)
        val memoryPatterns = codeFile.analyzeMemoryPatterns(cfg, constants, resolution)
        val typeInference = codeFile.inferTypes(cfg, dataFlow, constants, memoryPatterns)
        val loops = codeFile.detectLoops(resolution, entries, reachability, blocks, cfg, dominators)
        val variableId = codeFile.identifyVariables(cfg, dataFlow, typeInference, memoryPatterns, loops)

        val result = cfg.emitCompleteKotlin(codeFile, variableId, null, null, null, null)
        val kotlinCode = result.kotlinFile.emitToString()

        // Should still have the raw memory array for bulk operations
        assertTrue(kotlinCode.contains("val memory"), "Should preserve raw memory array")
        assertTrue(kotlinCode.contains("ByteArray"), "Memory should be ByteArray type")
    }
}
