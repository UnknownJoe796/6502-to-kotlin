package com.ivieleague.decompiler6502tokotlin.smb

import com.ivieleague.decompiler6502tokotlin.hand.*

/**
 * SMB-specific runtime that integrates PPU and controller input
 * with the decompiled game code.
 *
 * This provides:
 * - PPU register emulation for game timing
 * - Controller input injection for TAS playback
 * - Memory-mapped I/O interception
 * - NMI handling for frame-based game loop
 * - Path-based NMI skip detection for TAS accuracy
 *
 * ## NMI Skipping
 *
 * The NES PPU triggers NMI every ~29,780 CPU cycles. However, if the NMI
 * handler takes longer than one frame (e.g., during InitializeGame or
 * InitializeArea), subsequent NMIs are missed because NMI is disabled
 * during the handler.
 *
 * We detect these multi-frame operations by monitoring Task 00→01 transitions:
 * - Mode 0 (Title): InitializeGame takes 2 extra frames
 * - Mode 1 (Game):  InitializeArea takes 1 extra frame
 *
 * Reference: local/tas/NMI-SKIP-PATTERNS.md
 */
class SMBRuntime {
    val ppu = PPUStub()
    val controller = ControllerInput()

    // Frame counter
    var frameCount: Int = 0
        private set

    // NMI pending flag
    private var nmiPending: Boolean = false

    // Victory detection
    var gameBeaten: Boolean = false
        private set

    // Current game state (for debugging/validation)
    var currentWorld: Int = 0
        private set
    var currentLevel: Int = 0
        private set

    // === Path-based NMI Skip Detection ===

    /** Number of NMIs to skip (for multi-frame operations) */
    private var nmisToSkip: Int = 0

    /** Previous frame's Task value for detecting 00→01 transitions */
    private var prevTask: Int = -1

    /** Previous frame's Mode value for determining skip count */
    private var prevMode: Int = -1

    /** Whether this is the first frame after reset */
    private var isFirstFrame: Boolean = true

    /** Debug: track skipped NMI count */
    var skippedNmiCount: Int = 0
        private set
    
    /**
     * Initialize the runtime and reset all state
     */
    fun initialize() {
        resetCPU()
        clearMemory()
        ppu.reset()
        controller.reset()
        frameCount = 0
        nmiPending = false
        gameBeaten = false
        currentWorld = 0
        currentLevel = 0

        // Reset NMI skip state
        nmisToSkip = 3  // Reset sequence skips first 3 NMIs (frames 0-2)
        prevTask = -1
        prevMode = -1
        isFirstFrame = true
        skippedNmiCount = 0
    }

    /**
     * Disable NMI skipping.
     * Use this when loading state from interpreter to avoid skip logic mismatch.
     */
    fun disableNmiSkip() {
        nmisToSkip = 0
        isFirstFrame = false
        prevTask = memory[OperMode_Task].toInt()
        prevMode = memory[OperMode].toInt()
    }

    /**
     * Hook for memory reads - intercepts PPU and controller reads
     */
    fun interceptRead(address: Int): UByte? {
        return when (address) {
            in 0x2000..0x2007 -> ppu.readRegister(address)
            0x4016 -> controller.readController(0x4016)
            0x4017 -> controller.readController(0x4017)
            else -> null  // Use normal memory
        }
    }
    
    /**
     * Hook for memory writes - intercepts PPU and controller writes
     */
    fun interceptWrite(address: Int, value: UByte): Boolean {
        return when (address) {
            in 0x2000..0x2007 -> {
                ppu.writeRegister(address, value)
                true
            }
            0x4014 -> {
                // OAM DMA - copy 256 bytes from CPU memory to OAM
                val sourceAddr = value.toInt() shl 8
                val data = UByteArray(256) { memory[(sourceAddr + it) and 0xFFFF] }
                ppu.oamDma(data)
                true
            }
            0x4016 -> {
                controller.writeStrobe(value)
                true
            }
            else -> false  // Use normal memory
        }
    }
    
    /**
     * Run one frame of the game
     *
     * @param player1Input Controller input for player 1
     * @param player2Input Controller input for player 2
     */
    fun runFrame(player1Input: Int = 0, player2Input: Int = 0) {
        // Set controller input
        controller.setButtons(0, player1Input)
        controller.setButtons(1, player2Input)

        // End VBlank from previous frame
        ppu.endVBlank()

        // Run game logic for visible scanlines (would happen during rendering)
        // The decompiled code handles this timing internally

        // Begin VBlank
        ppu.beginVBlank()

        // Detect path-based NMI skips BEFORE checking if NMI should fire
        detectNmiSkip()

        // Check if NMI should fire (considering skips)
        if (ppu.shouldTriggerNmi()) {
            if (nmisToSkip > 0) {
                // Skip this NMI (multi-frame operation in progress)
                nmisToSkip--
                skippedNmiCount++
            } else {
                nmiPending = true
            }
        }

        frameCount++

        // Update game state tracking
        updateGameState()
    }

    /**
     * Detect path-based NMI skips by monitoring Task transitions.
     *
     * All NMI skips in SMB are associated with Task 00→01 transitions:
     * - Mode 0 (Title): InitializeGame = 2 NMIs skipped
     * - Mode 1 (Game):  InitializeArea = 1 NMI skipped
     *
     * This is called BEFORE the NMI check so skip counts are set in time.
     */
    private fun detectNmiSkip() {
        if (isFirstFrame) {
            isFirstFrame = false
            // First frame detection - reset skips already set in initialize()
            return
        }

        // Read current state from memory
        val currentTask = memory[OperMode_Task].toInt()
        val currentMode = memory[OperMode].toInt()

        // Detect Task 00→01 transition
        if (prevTask == 0 && currentTask == 1) {
            when (currentMode) {
                0 -> {
                    // Title screen mode: InitializeGame takes 2 extra frames
                    nmisToSkip = 2
                }
                1 -> {
                    // Game mode: InitializeArea takes 1 extra frame
                    nmisToSkip = 1
                }
                // Other modes don't have significant multi-frame operations
            }
        }

        // Update previous state
        prevTask = currentTask
        prevMode = currentMode
    }
    
    /**
     * Check if NMI is pending and clear the flag
     */
    fun checkAndClearNmi(): Boolean {
        val pending = nmiPending
        nmiPending = false
        return pending
    }

    /**
     * Explicitly skip the next N NMIs.
     *
     * Use this in decompiled code for edge cases that aren't detected
     * by the Task transition detection (e.g., respawn after death within
     * the same level, internal area transitions).
     *
     * Example usage in decompiled code:
     * ```kotlin
     * fun someExpensiveOperation() {
     *     // ... do expensive work ...
     *     runtime.skipNmi(1)  // This took an extra frame
     * }
     * ```
     */
    fun skipNmi(count: Int) {
        nmisToSkip += count
    }

    /**
     * Get the current NMI skip count (for debugging)
     */
    fun getNmisToSkip(): Int = nmisToSkip
    
    /**
     * Update game state from memory for validation
     */
    private fun updateGameState() {
        // SMB memory addresses for game state
        // These are from the SMB disassembly
        val worldNumber = memory[0x075F].toInt()  // WorldNumber
        val levelNumber = memory[0x0760].toInt()  // LevelNumber
        val operMode = memory[0x0770].toInt()     // OperMode
        
        currentWorld = worldNumber + 1
        currentLevel = levelNumber + 1
        
        // Check for victory condition
        // World 8-4 beaten when Princess is rescued (specific game state)
        // OperMode = 2 (Victory mode) or checking specific victory flag
        if (worldNumber == 7 && levelNumber == 3) {
            // In 8-4, check if the axe has been hit or princess shown
            val screenRoutineTask = memory[0x073C].toInt()
            // Victory sequence typically sets specific flags
            if (operMode == 2 || screenRoutineTask >= 0x80) {
                gameBeaten = true
            }
        }
    }
    
    /**
     * Run a TAS movie and verify completion
     * 
     * @param movie The TAS movie to play
     * @param maxFrames Maximum frames to run (safety limit)
     * @param onFrame Optional callback per frame for debugging
     * @return true if game was beaten, false otherwise
     */
    fun runTAS(
        movie: TASMovie,
        maxFrames: Int = 500000,
        onFrame: ((frame: Int, world: Int, level: Int) -> Unit)? = null
    ): Boolean {
        initialize()
        
        // Initialize game (call RESET vector equivalent)
        // This would call the main initialization routine
        
        var frame = 0
        while (frame < maxFrames && frame < movie.frameCount && !gameBeaten) {
            val input = movie.getFrame(frame)
            runFrame(input.player1, input.player2)
            
            onFrame?.invoke(frame, currentWorld, currentLevel)
            
            frame++
        }
        
        return gameBeaten
    }
    
    /**
     * Get current game state summary
     */
    fun getStateSummary(): String {
        return buildString {
            appendLine("Frame: $frameCount")
            appendLine("World: $currentWorld-$currentLevel")
            appendLine("Game Beaten: $gameBeaten")
            appendLine("PPU Scroll: ${ppu.getScroll()}")
            appendLine("Skipped NMIs: $skippedNmiCount")
            appendLine("Pending NMI skips: $nmisToSkip")

            // Add some key memory values for debugging
            val lives = memory[0x075A].toInt()  // NumberofLives
            val coins = memory[0x075E].toInt()  // CoinTally
            val score = buildString {
                for (i in 0 until 6) {
                    append(memory[0x07DD + i].toInt())
                }
            }
            appendLine("Lives: $lives, Coins: $coins, Score: $score")
        }
    }
}

/**
 * Memory wrapper that intercepts PPU/APU reads and writes
 * Use this in place of direct memory access in decompiled code
 */
class InterceptedMemory(private val runtime: SMBRuntime) {
    operator fun get(address: Int): UByte {
        return runtime.interceptRead(address) ?: memory[address and 0xFFFF]
    }
    
    operator fun set(address: Int, value: UByte) {
        if (!runtime.interceptWrite(address, value)) {
            memory[address and 0xFFFF] = value
        }
    }
}
