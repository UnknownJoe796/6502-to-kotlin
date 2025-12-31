@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.smb

import com.ivieleague.decompiler6502tokotlin.hand.*

/**
 * Simplified SMB stub for testing the TAS validation framework.
 * 
 * This mimics the basic game loop structure without the full decompiled code.
 * It demonstrates:
 * 1. NMI-driven frame updates
 * 2. Controller input processing
 * 3. Game state transitions
 * 4. Victory condition detection
 * 
 * Memory layout (matching real SMB):
 * - $0770: OperMode (0=Title, 1=Game, 2=Victory)
 * - $0772: OperMode_Task
 * - $075F: WorldNumber (0-7)
 * - $0760: LevelNumber (0-3)
 * - $075A: NumberofLives
 * - $0776: GamePauseStatus
 * - $06FC: SavedJoypad1Bits
 */
object SimpleSMBStub {
    // Memory addresses (matching real SMB)
    const val OPER_MODE = 0x0770
    const val OPER_MODE_TASK = 0x0772
    const val WORLD_NUMBER = 0x075F
    const val LEVEL_NUMBER = 0x0760
    const val NUMBER_OF_LIVES = 0x075A
    const val GAME_PAUSE_STATUS = 0x0776
    const val SAVED_JOYPAD1_BITS = 0x06FC
    const val PLAYER_X_POS = 0x0086
    const val PLAYER_Y_POS = 0x00CE
    const val SCREEN_LEFT_EDGE = 0x071C
    
    // Game modes
    const val MODE_TITLE = 0
    const val MODE_GAME = 1
    const val MODE_VICTORY = 2
    
    // Frame counter
    private var frameCounter = 0
    private var levelCompleteFrames = 0
    
    /**
     * Initialize game state (like RESET vector)
     */
    fun initialize() {
        clearMemory()
        resetCPU()
        
        // Set up initial game state
        memory[OPER_MODE] = MODE_TITLE.toUByte()
        memory[OPER_MODE_TASK] = 0u
        memory[WORLD_NUMBER] = 0u  // World 1
        memory[LEVEL_NUMBER] = 0u  // Level 1
        memory[NUMBER_OF_LIVES] = 3u
        memory[PLAYER_X_POS] = 40u  // Starting X position
        memory[PLAYER_Y_POS] = 160u // Ground level
        
        frameCounter = 0
        levelCompleteFrames = 0
    }
    
    /**
     * Process one frame (like NMI handler)
     */
    fun runFrame(controller1: Int, controller2: Int = 0) {
        frameCounter++
        
        // Save controller input
        memory[SAVED_JOYPAD1_BITS] = controller1.toUByte()
        
        // Run game based on current mode
        when (memory[OPER_MODE].toInt()) {
            MODE_TITLE -> processTitleScreen(controller1)
            MODE_GAME -> processGameplay(controller1)
            MODE_VICTORY -> processVictory()
        }
    }
    
    private fun processTitleScreen(controller: Int) {
        // Press START to begin game
        if ((controller and ControllerInput.START) != 0) {
            memory[OPER_MODE] = MODE_GAME.toUByte()
            memory[OPER_MODE_TASK] = 0u
        }
    }
    
    private fun processGameplay(controller: Int) {
        val worldNum = memory[WORLD_NUMBER].toInt()
        val levelNum = memory[LEVEL_NUMBER].toInt()
        var playerX = memory[PLAYER_X_POS].toInt()
        var screenLeft = memory[SCREEN_LEFT_EDGE].toInt()
        
        // Simple movement simulation
        if ((controller and ControllerInput.RIGHT) != 0) {
            playerX += 2
            if (playerX > 200) {
                // Scroll screen
                screenLeft += 2
                playerX = 200
            }
        }
        if ((controller and ControllerInput.LEFT) != 0) {
            if (playerX > screenLeft + 8) {
                playerX -= 2
            }
        }
        
        // Jump (simplified - just affects completion speed)
        if ((controller and ControllerInput.A) != 0) {
            // Jumping makes you go slightly faster
            if ((controller and ControllerInput.RIGHT) != 0) {
                playerX += 1
                if (playerX > 200) {
                    screenLeft += 1
                    playerX = 200
                }
            }
        }
        
        memory[PLAYER_X_POS] = playerX.toUByte()
        memory[SCREEN_LEFT_EDGE] = screenLeft.toUByte()
        
        // Check level completion (simplified: after ~500 frames of moving right)
        if (screenLeft >= 200) {
            levelCompleteFrames++
            if (levelCompleteFrames >= 30) {
                advanceLevel()
                levelCompleteFrames = 0
            }
        }
    }
    
    private fun advanceLevel() {
        var worldNum = memory[WORLD_NUMBER].toInt()
        var levelNum = memory[LEVEL_NUMBER].toInt()
        
        levelNum++
        if (levelNum >= 4) {
            levelNum = 0
            worldNum++
        }
        
        // Check for victory (completed 8-4)
        if (worldNum >= 8) {
            memory[OPER_MODE] = MODE_VICTORY.toUByte()
            return
        }
        
        memory[WORLD_NUMBER] = worldNum.toUByte()
        memory[LEVEL_NUMBER] = levelNum.toUByte()
        memory[SCREEN_LEFT_EDGE] = 0u
        memory[PLAYER_X_POS] = 40u
    }
    
    private fun processVictory() {
        // Victory screen - game is won
    }
    
    /**
     * Check if game is in victory state
     */
    fun isVictory(): Boolean {
        return memory[OPER_MODE].toInt() == MODE_VICTORY
    }
    
    /**
     * Get current world (1-8)
     */
    fun getCurrentWorld(): Int = memory[WORLD_NUMBER].toInt() + 1
    
    /**
     * Get current level (1-4)
     */
    fun getCurrentLevel(): Int = memory[LEVEL_NUMBER].toInt() + 1
    
    /**
     * Get frame count
     */
    fun getFrameCount(): Int = frameCounter
    
    /**
     * Get game state summary
     */
    fun getStateSummary(): String {
        return buildString {
            appendLine("Frame: $frameCounter")
            appendLine("Mode: ${memory[OPER_MODE]}")
            appendLine("World: ${getCurrentWorld()}-${getCurrentLevel()}")
            appendLine("Player X: ${memory[PLAYER_X_POS]}")
            appendLine("Screen: ${memory[SCREEN_LEFT_EDGE]}")
            appendLine("Victory: ${isVictory()}")
        }
    }
}
