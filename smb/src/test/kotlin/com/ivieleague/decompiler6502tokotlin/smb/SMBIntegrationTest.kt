package com.ivieleague.decompiler6502tokotlin.smb

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * Integration tests for the TAS validation framework using the simplified SMB stub.
 * 
 * These tests validate that:
 * 1. The game loop processes correctly
 * 2. Controller input affects game state
 * 3. Level progression works
 * 4. Victory detection works
 * 5. TAS movie playback works end-to-end
 */
class SMBIntegrationTest {
    
    @Test
    fun `test game initialization`() {
        SimpleSMBStub.initialize()
        
        assertEquals(1, SimpleSMBStub.getCurrentWorld())
        assertEquals(1, SimpleSMBStub.getCurrentLevel())
        assertFalse(SimpleSMBStub.isVictory())
    }
    
    @Test
    fun `test title screen start`() {
        SimpleSMBStub.initialize()
        
        // Run a few frames with no input - should stay on title
        repeat(10) {
            SimpleSMBStub.runFrame(0)
        }
        assertEquals(1, SimpleSMBStub.getCurrentWorld())
        
        // Press START to begin game
        SimpleSMBStub.runFrame(ControllerInput.START)
        
        // Game should now be running
        assertEquals(1, SimpleSMBStub.getCurrentWorld())
        assertEquals(1, SimpleSMBStub.getCurrentLevel())
    }
    
    @Test
    fun `test movement and level completion`() {
        SimpleSMBStub.initialize()
        
        // Start game
        SimpleSMBStub.runFrame(ControllerInput.START)
        
        // Hold right to advance through level
        // The stub completes a level when screen position reaches 200
        var framesRun = 0
        val maxFrames = 5000
        
        while (SimpleSMBStub.getCurrentLevel() == 1 && framesRun < maxFrames) {
            SimpleSMBStub.runFrame(ControllerInput.RIGHT or ControllerInput.A)
            framesRun++
        }
        
        println("Level completed in $framesRun frames")
        assertTrue(framesRun < maxFrames, "Should complete level 1-1")
        
        // Should now be on level 1-2
        assertEquals(1, SimpleSMBStub.getCurrentWorld())
        assertEquals(2, SimpleSMBStub.getCurrentLevel())
    }
    
    @Test
    fun `test full game completion with TAS`() {
        SimpleSMBStub.initialize()
        
        // Create a simple TAS that holds right+A to speedrun through all levels
        val movie = mutableListOf<TASMovie.FrameInput>()
        
        // First few frames to start the game
        movie.add(TASMovie.FrameInput(0))  // Wait
        movie.add(TASMovie.FrameInput(ControllerInput.START))  // Press start
        
        // Then hold right+A for the rest of the run
        // 32 levels (8 worlds * 4 levels), each taking ~130 frames = ~4200 frames
        repeat(50000) {
            movie.add(TASMovie.FrameInput(ControllerInput.RIGHT or ControllerInput.A))
        }
        
        val tas = TASMovie(movie)
        
        // Play back the TAS
        var frame = 0
        var lastWorld = 0
        var lastLevel = 0
        
        while (frame < tas.frameCount && !SimpleSMBStub.isVictory()) {
            val input = tas.getFrame(frame)
            SimpleSMBStub.runFrame(input.player1, input.player2)
            
            // Log progress when level changes
            val currentWorld = SimpleSMBStub.getCurrentWorld()
            val currentLevel = SimpleSMBStub.getCurrentLevel()
            if (currentWorld != lastWorld || currentLevel != lastLevel) {
                println("Frame $frame: Now at World $currentWorld-$currentLevel")
                lastWorld = currentWorld
                lastLevel = currentLevel
            }
            
            frame++
        }
        
        println("\nFinal state after $frame frames:")
        println(SimpleSMBStub.getStateSummary())
        
        assertTrue(SimpleSMBStub.isVictory(), "TAS should complete the game")
        println("Game completed in $frame frames!")
    }
    
    @Test
    fun `test runtime integration with stub`() {
        val runtime = SMBRuntime()
        runtime.initialize()
        
        // Initialize the stub
        SimpleSMBStub.initialize()
        
        // Run a few frames through the runtime
        repeat(5) {
            runtime.runFrame(0)
            SimpleSMBStub.runFrame(0)
        }
        
        // Start the game
        runtime.runFrame(ControllerInput.START)
        SimpleSMBStub.runFrame(ControllerInput.START)
        
        // Both should be in sync
        assertEquals(runtime.frameCount, SimpleSMBStub.getFrameCount())
    }
    
    @Test
    fun `test TAS movie from string format`() {
        SimpleSMBStub.initialize()
        
        // Create a simple movie in human-readable format
        val movieContent = """
            # Frame 0: Wait on title
            .
            # Frame 1: Press start
            St
            # Frames 2-100: Run right
            R+A
            R+A
            R+A
            R+A
            R+A
        """.trimIndent()
        
        val movie = TASMovie.fromSimpleFormat(movieContent)
        
        assertEquals(7, movie.frameCount)
        assertEquals(0, movie.getFrame(0).player1)  // No input
        assertEquals(ControllerInput.START, movie.getFrame(1).player1)  // Start
        assertEquals(ControllerInput.RIGHT or ControllerInput.A, movie.getFrame(2).player1)  // R+A
    }
    
    @Test
    fun `verify level progression sequence`() {
        SimpleSMBStub.initialize()
        SimpleSMBStub.runFrame(ControllerInput.START)
        
        val expectedSequence = listOf(
            1 to 1, 1 to 2, 1 to 3, 1 to 4,
            2 to 1, 2 to 2, 2 to 3, 2 to 4,
            3 to 1, 3 to 2, 3 to 3, 3 to 4,
            4 to 1, 4 to 2, 4 to 3, 4 to 4,
            5 to 1, 5 to 2, 5 to 3, 5 to 4,
            6 to 1, 6 to 2, 6 to 3, 6 to 4,
            7 to 1, 7 to 2, 7 to 3, 7 to 4,
            8 to 1, 8 to 2, 8 to 3, 8 to 4
        )
        
        var sequenceIndex = 0
        var framesRun = 0
        val maxFrames = 200000
        
        while (!SimpleSMBStub.isVictory() && framesRun < maxFrames && sequenceIndex < expectedSequence.size) {
            val (expectedWorld, expectedLevel) = expectedSequence[sequenceIndex]
            assertEquals(expectedWorld, SimpleSMBStub.getCurrentWorld(), "World mismatch at sequence $sequenceIndex")
            assertEquals(expectedLevel, SimpleSMBStub.getCurrentLevel(), "Level mismatch at sequence $sequenceIndex")
            
            // Advance to next level
            while (SimpleSMBStub.getCurrentWorld() == expectedWorld && 
                   SimpleSMBStub.getCurrentLevel() == expectedLevel &&
                   framesRun < maxFrames) {
                SimpleSMBStub.runFrame(ControllerInput.RIGHT or ControllerInput.A)
                framesRun++
            }
            
            sequenceIndex++
        }
        
        assertTrue(SimpleSMBStub.isVictory(), "Should complete all 32 levels")
        println("Completed all 32 levels in $framesRun frames")
    }
}
