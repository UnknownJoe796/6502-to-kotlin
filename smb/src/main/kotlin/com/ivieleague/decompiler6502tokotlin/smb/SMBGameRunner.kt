package com.ivieleague.decompiler6502tokotlin.smb

import com.ivieleague.decompiler6502tokotlin.hand.*

// Debug flag for controller reading
private const val DEBUG_CONTROLLER = false

/**
 * Runs the decompiled SMB game code with proper frame timing.
 *
 * This class integrates:
 * - SMBRuntime: PPU/controller emulation and NMI skip detection
 * - SMBDecompiled: The decompiled game logic
 * - TAS playback: Frame-by-frame input injection
 *
 * The game loop follows the NES hardware model:
 * 1. VBlank begins → NMI fires (if enabled)
 * 2. NMI handler runs game logic
 * 3. VBlank ends → rendering begins
 * 4. Repeat
 */
class SMBGameRunner {
    val runtime = SMBRuntime()

    // Game state tracking
    var frameCount: Int = 0
        private set

    /**
     * Initialize the game (cold boot).
     *
     * This calls the Start routine from the decompiled code,
     * setting up initial memory state.
     */
    fun initialize() {
        runtime.initialize()
        frameCount = 0

        // Clear memory (equivalent to hardware reset)
        clearMemory()

        // Load ROM data tables (graphics, music, level layouts, etc.)
        initializeRomData()

        // Patch missing ROM data tables (symbolic references not extracted)
        patchMissingRomData()

        // Set up memory intercepts for hardware registers
        setupMemoryIntercepts()

        // Run the Start routine (reset vector)
        coldBoot()
    }

    /**
     * Patch missing ROM data tables.
     *
     * The ROM data extraction missed the area data address lookup tables
     * because they use symbolic references (Expr) instead of literal bytes.
     * We need to populate these manually.
     */
    private fun patchMissingRomData() {
        // AreaDataHOffsets at 0x9F07 - offsets for each area type
        // [water, ground, underground, castle]
        memory[0x9F07] = 0x00u  // Water starts at index 0
        memory[0x9F08] = 0x03u  // Ground starts at index 3
        memory[0x9F09] = 0x19u  // Underground starts at index 25 (0x19)
        memory[0x9F0A] = 0x1Cu  // Castle starts at index 28 (0x1C)

        // Area data addresses in order:
        // Water: L_WaterArea1, L_WaterArea2, L_WaterArea3
        // Ground: L_GroundArea1-22
        // Underground: L_UndergroundArea1-3
        // Castle: L_CastleArea1-6
        val areaAddresses = listOf(
            // Water areas (0-2)
            L_WaterArea1, L_WaterArea2, L_WaterArea3,
            // Ground areas (3-24)
            L_GroundArea1, L_GroundArea2, L_GroundArea3, L_GroundArea4, L_GroundArea5,
            L_GroundArea6, L_GroundArea7, L_GroundArea8, L_GroundArea9, L_GroundArea10,
            L_GroundArea11, L_GroundArea12, L_GroundArea13, L_GroundArea14, L_GroundArea15,
            L_GroundArea16, L_GroundArea17, L_GroundArea18, L_GroundArea19, L_GroundArea20,
            L_GroundArea21, L_GroundArea22,
            // Underground areas (25-27)
            L_UndergroundArea1, L_UndergroundArea2, L_UndergroundArea3,
            // Castle areas (28-33)
            L_CastleArea1, L_CastleArea2, L_CastleArea3, L_CastleArea4, L_CastleArea5, L_CastleArea6
        )

        // AreaDataAddrLow at 0x9F0B - low bytes of level data addresses
        for ((i, addr) in areaAddresses.withIndex()) {
            memory[0x9F0B + i] = (addr and 0xFF).toUByte()
        }

        // AreaDataAddrHigh at 0x9F2D - high bytes of level data addresses
        for ((i, addr) in areaAddresses.withIndex()) {
            memory[0x9F2D + i] = ((addr shr 8) and 0xFF).toUByte()
        }

        // Also patch enemy data address tables (similar structure)
        // EnemyAddrHOffsets at 0x9EBF
        memory[EnemyAddrHOffsets] = 0x00u      // Water starts at index 0
        memory[EnemyAddrHOffsets + 1] = 0x03u  // Ground starts at index 3
        memory[EnemyAddrHOffsets + 2] = 0x19u  // Underground starts at index 25
        memory[EnemyAddrHOffsets + 3] = 0x1Cu  // Castle starts at index 28

        // Enemy data addresses (same order as level data)
        val enemyAddresses = listOf(
            // Water areas (0-2)
            E_WaterArea1, E_WaterArea2, E_WaterArea3,
            // Ground areas (3-24)
            E_GroundArea1, E_GroundArea2, E_GroundArea3, E_GroundArea4, E_GroundArea5,
            E_GroundArea6, E_GroundArea7, E_GroundArea8, E_GroundArea9, E_GroundArea10,
            E_GroundArea11, E_GroundArea12, E_GroundArea13, E_GroundArea14, E_GroundArea15,
            E_GroundArea16, E_GroundArea17, E_GroundArea18, E_GroundArea19, E_GroundArea20,
            E_GroundArea21, E_GroundArea22,
            // Underground areas (25-27)
            E_UndergroundArea1, E_UndergroundArea2, E_UndergroundArea3,
            // Castle areas (28-33)
            E_CastleArea1, E_CastleArea2, E_CastleArea3, E_CastleArea4, E_CastleArea5, E_CastleArea6
        )

        // EnemyDataAddrLow at 0x9EC3
        for ((i, addr) in enemyAddresses.withIndex()) {
            memory[EnemyDataAddrLow + i] = (addr and 0xFF).toUByte()
        }

        // EnemyDataAddrHigh at 0x9EE5
        for ((i, addr) in enemyAddresses.withIndex()) {
            memory[EnemyDataAddrHigh + i] = ((addr shr 8) and 0xFF).toUByte()
        }

        // WorldAddrOffsets at 0x9E93 - offsets from AreaAddrOffsets base
        // Each world's area list starts at different offset
        val worldAreaListOffsets = listOf(0, 5, 10, 14, 19, 23, 27, 32)  // Calculated from World*Areas labels
        for ((i, offset) in worldAreaListOffsets.withIndex()) {
            memory[WorldAddrOffsets + i] = offset.toUByte()
        }

        // AreaAddrOffsets at 0x9E9B - area pointers for each level in each world
        // World1Areas: 37, 41, 192, 38, 96
        // World2Areas: 40, 41, 1, 39, 98
        // etc.
        val areaPointers = listOf(
            // World 1 (0-4)
            37, 41, 192, 38, 96,
            // World 2 (5-9)
            40, 41, 1, 39, 98,
            // World 3 (10-13)
            36, 53, 32, 99,
            // World 4 (14-18)
            34, 41, 65, 44, 97,
            // World 5 (19-22)
            42, 49, 38, 98,
            // World 6 (23-26)
            46, 35, 45, 96,
            // World 7 (27-31)
            51, 41, 1, 39, 100,
            // World 8 (32-35)
            48, 50, 33, 101
        )
        for ((i, ptr) in areaPointers.withIndex()) {
            memory[AreaAddrOffsets + i] = ptr.toUByte()
        }

        // Load actual level data
        loadLevelData()
    }

    /**
     * Load level data bytes into ROM addresses.
     * This data was not extracted by the decompiler because it's raw bytes.
     */
    private fun loadLevelData() {
        // L_GroundArea6 at 0xA86D - World 1-1 level data
        // Header: $50 $21 - bits 3-5 of $50 = PlayerEntranceCtrl = 2
        val groundArea6 = ubyteArrayOf(
            0x50u, 0x21u,
            0x07u, 0x81u, 0x47u, 0x24u, 0x57u, 0x00u, 0x63u, 0x01u, 0x77u, 0x01u,
            0xC9u, 0x71u, 0x68u, 0xF2u, 0xE7u, 0x73u, 0x97u, 0xFBu, 0x06u, 0x83u,
            0x5Cu, 0x01u, 0xD7u, 0x22u, 0xE7u, 0x00u, 0x03u, 0xA7u, 0x6Cu, 0x02u,
            0xB3u, 0x22u, 0xE3u, 0x01u, 0xE7u, 0x07u, 0x47u, 0xA0u, 0x57u, 0x06u,
            0xA7u, 0x01u, 0xD3u, 0x00u, 0xD7u, 0x01u, 0x07u, 0x81u, 0x67u, 0x20u,
            0x93u, 0x22u, 0x03u, 0xA3u, 0x1Cu, 0x61u, 0x17u, 0x21u, 0x6Fu, 0x33u,
            0xC7u, 0x63u, 0xD8u, 0x62u, 0xE9u, 0x61u, 0xFAu, 0x60u, 0x4Fu, 0xB3u,
            0x87u, 0x63u, 0x9Cu, 0x01u, 0xB7u, 0x63u, 0xC8u, 0x62u, 0xD9u, 0x61u,
            0xEAu, 0x60u, 0x39u, 0xF1u, 0x87u, 0x21u, 0xA7u, 0x01u, 0xB7u, 0x20u,
            0x39u, 0xF1u, 0x5Fu, 0x38u, 0x6Du, 0xC1u, 0xAFu, 0x26u,
            0xFDu  // End marker
        )
        groundArea6.copyInto(memory, L_GroundArea6)
    }

    /**
     * Set up memory read/write intercepts for hardware registers.
     *
     * This allows the decompiled code to read controller input
     * and PPU status through normal memory access.
     */
    private fun setupMemoryIntercepts() {
        memoryReadIntercept = { addr ->
            when (addr) {
                0x4016, 0x4017 -> {
                    // Controller port read - return next bit from controller
                    val bit = runtime.controller.readController(addr)
                    if (DEBUG_CONTROLLER) System.err.println("memoryReadIntercept: addr=${addr.toString(16)}, bit=$bit")
                    bit
                }
                0x2002 -> {
                    // PPU status - return VBlank flag and clear it
                    runtime.ppu.readRegister(0x2002)
                }
                else -> null  // Use normal memory
            }
        }

        memoryWriteIntercept = { addr, value ->
            when (addr) {
                0x4016 -> {
                    // Controller strobe
                    runtime.controller.writeStrobe(value)
                    true
                }
                in 0x2000..0x2007 -> {
                    // PPU registers
                    runtime.ppu.writeRegister(addr, value)
                    true
                }
                else -> false  // Use normal memory
            }
        }
    }

    /**
     * Cold boot initialization.
     * Mirrors the Start routine in smbdism.asm lines 593-669
     */
    private fun coldBoot() {
        // PPU initialization - write to actual PPU register via intercept
        runtime.ppu.writeRegister(0x2000, 0x10u)  // Init PPU control (NMI disabled)

        // Wait for VBlank (simulated - just proceed)
        // In real hardware, this waits for PPU warm-up

        // Check for warm boot (we always do cold boot for TAS)
        // Clear memory from $0000-$076F
        for (addr in 0x0000..0x076F) {
            memory[addr] = 0x00u
        }

        // Initialize the demo timer and interval timer control
        memory[DemoTimer] = 0x18u
        memory[IntervalTimerControl] = 0x14u

        // Set initial PPU state in mirror registers
        memory[Mirror_PPU_CTRL_REG1] = 0x10u
        memory[Mirror_PPU_CTRL_REG2] = 0x06u

        // Enable NMI in mirror register AND actual PPU (line 660 in smbdism.asm)
        val ppuCtrl = 0x10 or 0x80  // Base value with NMI enabled
        memory[Mirror_PPU_CTRL_REG1] = ppuCtrl.toUByte()
        runtime.ppu.writeRegister(0x2000, ppuCtrl.toUByte())  // Enable NMI in actual PPU
    }

    /**
     * Run one frame of the game.
     *
     * @param player1Input Controller input for player 1 (bit flags)
     * @param player2Input Controller input for player 2 (bit flags)
     */
    fun runFrame(player1Input: Int = 0, player2Input: Int = 0) {
        // Set controller input before the frame
        runtime.controller.setButtons(0, player1Input)
        runtime.controller.setButtons(1, player2Input)

        // Run the frame through the runtime (handles NMI skip detection)
        runtime.runFrame(player1Input, player2Input)

        // Check if NMI should fire this frame
        if (runtime.checkAndClearNmi()) {
            // Execute NMI handler (the main game logic)
            nmiHandler()
        }

        frameCount++
    }

    // Timeout for NMI handler in milliseconds
    // If a single frame takes longer than this, the decompiled code has a bug (infinite loop)
    var nmiTimeoutMs: Long = 1000

    // Exception thrown when timeout occurs - stored here to rethrow in main thread
    @Volatile
    private var timeoutException: RuntimeException? = null

    /**
     * NMI handler - called once per frame (unless skipped).
     *
     * This mirrors NonMaskableInterrupt in smbdism.asm lines 739-847.
     * The NMI handler:
     * 1. Disables NMI
     * 2. Updates PPU (OAM DMA, VRAM)
     * 3. Runs sound engine
     * 4. Reads controllers
     * 5. Handles pause
     * 6. Updates timers
     * 7. Advances RNG
     * 8. Calls OperModeExecutionTree (main game logic)
     * 9. Re-enables NMI
     *
     * If execution takes longer than [nmiTimeoutMs], throws an exception
     * indicating the decompiled code has an infinite loop.
     */
    private fun nmiHandler() {
        timeoutException = null

        // Run nmiHandlerImpl in a separate thread so we can timeout
        val thread = Thread {
            try {
                nmiHandlerImpl()
            } catch (e: Exception) {
                timeoutException = RuntimeException("Exception in NMI handler: ${e.message}", e)
            }
        }
        thread.start()

        // Wait for completion with timeout
        thread.join(nmiTimeoutMs)

        // Check if thread is still running (timeout occurred)
        if (thread.isAlive) {
            // Capture state before interrupting
            val state = "Frame=$frameCount, OperMode=${memory[OperMode]}, Task=${memory[OperMode_Task]}, " +
                "GameEngineSubroutine=${memory[GameEngineSubroutine]}"

            // Try to interrupt and stop the thread
            thread.interrupt()
            @Suppress("DEPRECATION")
            thread.stop()  // Force stop - we don't care about cleanup here

            throw RuntimeException(
                "NMI frame took longer than ${nmiTimeoutMs}ms - likely infinite loop in decompiled code. $state"
            )
        }

        // Check if there was an exception in the thread
        timeoutException?.let { throw it }
    }

    private fun nmiHandlerImpl() {
        // Disable NMI in mirror register
        val mirrorPpuCtrl = memory[Mirror_PPU_CTRL_REG1].toInt()
        memory[Mirror_PPU_CTRL_REG1] = (mirrorPpuCtrl and 0x7F).toUByte()

        // PPU updates (simulated - we don't have real PPU)
        // In real hardware: OAM DMA, VRAM buffer updates

        // Sound engine (stub for now)
        // soundEngine()

        // Read joypads
        readJoypads()

        // Pause handling
        pauseRoutine()

        // Update top score display
        updateTopScore()

        // Timer handling (if not paused)
        val gamePauseStatus = memory[GamePauseStatus].toInt()
        if ((gamePauseStatus and 0x01) == 0) {
            handleTimers()
        }

        // Increment frame counter
        memory[FrameCounter] = ((memory[FrameCounter].toInt() + 1) and 0xFF).toUByte()

        // Advance RNG (always, even when paused)
        advanceRng()

        // Main game logic (if not paused)
        if ((gamePauseStatus and 0x01) == 0) {
            operModeExecutionTree()
        }

        // Re-enable NMI in mirror register
        memory[Mirror_PPU_CTRL_REG1] = (memory[Mirror_PPU_CTRL_REG1].toInt() or 0x80).toUByte()
    }

    /**
     * Handle frame and interval timers.
     * Mirrors lines 785-800 of NonMaskableInterrupt.
     */
    private fun handleTimers() {
        val timerControl = memory[TimerControl].toInt()

        if (timerControl == 0) {
            // Decrement interval timer control
            var intervalCtrl = memory[IntervalTimerControl].toInt()
            intervalCtrl--

            val endOffset: Int
            if (intervalCtrl < 0) {
                // Interval timer expired, reset and decrement interval timers too
                intervalCtrl = 0x14
                endOffset = 0x23
            } else {
                // Only decrement frame timers
                endOffset = 0x14
            }
            memory[IntervalTimerControl] = intervalCtrl.toUByte()

            // Decrement timers from endOffset down to 0
            for (x in endOffset downTo 0) {
                val timer = memory[Timers + x].toInt()
                if (timer > 0) {
                    memory[Timers + x] = (timer - 1).toUByte()
                }
            }
        } else {
            // Timer control active, decrement it
            memory[TimerControl] = (timerControl - 1).toUByte()
        }
    }

    /**
     * Advance the pseudo-random number generator.
     * Mirrors lines 803-815 of NonMaskableInterrupt.
     *
     * The LFSR uses 7 bytes at PseudoRandomBitReg ($07A7-$07AD).
     */
    private fun advanceRng() {
        // Get d1 bits from first two bytes and XOR them
        val byte0 = memory[PseudoRandomBitReg].toInt()
        val byte1 = memory[PseudoRandomBitReg + 1].toInt()
        val d1_0 = (byte0 and 0x02) shr 1
        val d1_1 = (byte1 and 0x02) shr 1
        val newBit = d1_0 xor d1_1

        // Rotate through all 7 bytes
        var carry = newBit
        for (i in 0 until 7) {
            val current = memory[PseudoRandomBitReg + i].toInt()
            val newCarry = current and 0x01
            val rotated = (current shr 1) or (carry shl 7)
            memory[PseudoRandomBitReg + i] = (rotated and 0xFF).toUByte()
            carry = newCarry
        }
    }

    /**
     * Run a TAS movie.
     *
     * @param movie The TAS movie to play
     * @param maxFrames Maximum frames to run (safety limit)
     * @param onFrame Optional callback per frame
     * @return true if game was beaten
     */
    fun runTAS(
        movie: TASMovie,
        maxFrames: Int = 500000,
        onFrame: ((frame: Int, world: Int, level: Int) -> Unit)? = null
    ): Boolean {
        initialize()

        var beaten = false
        var frame = 0

        while (frame < maxFrames && frame < movie.frameCount) {
            val input = movie.getFrame(frame)
            runFrame(input.player1, input.player2)

            // Check for victory
            val operMode = memory[OperMode].toInt()
            if (operMode == 2) {  // Victory mode
                beaten = true
            }

            // Callback for progress tracking
            val world = memory[WorldNumber].toInt() + 1
            val level = memory[LevelNumber].toInt() + 1
            onFrame?.invoke(frame, world, level)

            // Check for game over
            if (operMode == 3) {  // Game over mode
                break
            }

            frame++
        }

        return beaten
    }

    /**
     * Get current game state for debugging.
     */
    fun getState(): GameState {
        return GameState(
            frame = frameCount,
            world = memory[WorldNumber].toInt() + 1,
            level = memory[LevelNumber].toInt() + 1,
            operMode = memory[OperMode].toInt(),
            operModeTask = memory[OperMode_Task].toInt(),
            frameCounter = memory[FrameCounter].toInt(),
            intervalTimerControl = memory[IntervalTimerControl].toInt(),
            lives = memory[NumberofLives].toInt(),
            playerX = memory[Player_X_Position].toInt(),
            playerY = memory[Player_Y_Position].toInt()
        )
    }

    data class GameState(
        val frame: Int,
        val world: Int,
        val level: Int,
        val operMode: Int,
        val operModeTask: Int,
        val frameCounter: Int,
        val intervalTimerControl: Int,
        val lives: Int,
        val playerX: Int,
        val playerY: Int
    )
}
