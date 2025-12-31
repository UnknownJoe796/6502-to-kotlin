package com.ivieleague.decompiler6502tokotlin.smb

/**
 * NES Controller Input handling for TAS validation.
 *
 * SMB uses reversed bit order due to how ReadPortBits uses ROL:
 * - Bit 7: A (first read)
 * - Bit 6: B
 * - Bit 5: Select
 * - Bit 4: Start
 * - Bit 3: Up
 * - Bit 2: Down
 * - Bit 1: Left
 * - Bit 0: Right (last read)
 *
 * Controller is read via $4016 (player 1) and $4017 (player 2).
 * Games write 1 then 0 to $4016 to strobe, then read 8 times.
 */
class ControllerInput {
    // Button bit masks (SMB bit order - reversed from standard NES)
    companion object {
        const val A = 0x80      // Bit 7
        const val B = 0x40      // Bit 6
        const val SELECT = 0x20 // Bit 5
        const val START = 0x10  // Bit 4
        const val UP = 0x08     // Bit 3
        const val DOWN = 0x04   // Bit 2
        const val LEFT = 0x02   // Bit 1
        const val RIGHT = 0x01  // Bit 0
    }
    
    // Current button state for each controller (0-1)
    private val buttonState = intArrayOf(0, 0)
    
    // Shift register state for serial reading
    private val shiftRegister = intArrayOf(0, 0)
    private var strobeState = false
    
    /**
     * Set button state for a controller (0 = player 1, 1 = player 2)
     */
    fun setButtons(controller: Int, buttons: Int) {
        if (controller in 0..1) {
            buttonState[controller] = buttons and 0xFF
        }
    }
    
    /**
     * Write to $4016 (controller strobe)
     */
    fun writeStrobe(value: UByte) {
        val newStrobe = (value.toInt() and 0x01) != 0
        if (strobeState && !newStrobe) {
            // Falling edge: latch button state into shift registers
            shiftRegister[0] = buttonState[0]
            shiftRegister[1] = buttonState[1]
        }
        strobeState = newStrobe
    }
    
    /**
     * Read from $4016 (player 1) or $4017 (player 2)
     *
     * Returns bits in serial order: A first (from bit 7), Right last (from bit 0).
     * This matches how NES hardware presents controller data, and SMB's
     * ReadPortBits uses ROL to reassemble back to: A=bit7, Right=bit0.
     */
    fun readController(address: Int): UByte {
        val controller = if (address == 0x4016) 0 else 1

        if (strobeState) {
            // During strobe, always return current A button state (bit 7)
            return ((buttonState[controller] shr 7) and 0x01).toUByte()
        }

        // Return next bit from shift register (MSB first: A, B, Select, Start, Up, Down, Left, Right)
        val bit = (shiftRegister[controller] shr 7) and 0x01
        shiftRegister[controller] = (shiftRegister[controller] shl 1) and 0xFF

        return bit.toUByte()
    }
    
    /**
     * Get the current button state as a human-readable string
     */
    fun buttonsToString(buttons: Int): String {
        val parts = mutableListOf<String>()
        if (buttons and RIGHT != 0) parts.add("R")
        if (buttons and LEFT != 0) parts.add("L")
        if (buttons and DOWN != 0) parts.add("D")
        if (buttons and UP != 0) parts.add("U")
        if (buttons and START != 0) parts.add("St")
        if (buttons and SELECT != 0) parts.add("Se")
        if (buttons and B != 0) parts.add("B")
        if (buttons and A != 0) parts.add("A")
        return if (parts.isEmpty()) "." else parts.joinToString("+")
    }
    
    /**
     * Parse button string into button mask
     * Format: "R", "L", "D", "U", "St", "Se", "B", "A" separated by "+"
     * Or single-character format: "RLDUTSBA" where each char position is a button
     */
    fun parseButtons(input: String): Int {
        if (input == "." || input.isBlank()) return 0
        
        // Check for FCEUX/BizHawk format: |..|RLDUTSBA|
        val trimmed = input.trim().removePrefix("|").removeSuffix("|")
        if (trimmed.contains("|")) {
            // Parse second field (after the pipe)
            val parts = trimmed.split("|")
            if (parts.size >= 2) {
                return parseFceuxFormat(parts[1])
            }
        }
        
        // Check if single-char format (8 chars representing RLDUTSBA)
        if (input.length == 8 && !input.contains("+")) {
            return parseFceuxFormat(input)
        }
        
        // Parse "R+L+A" format
        var buttons = 0
        for (part in input.split("+")) {
            buttons = buttons or when (part.trim().uppercase()) {
                "R", "RIGHT" -> RIGHT
                "L", "LEFT" -> LEFT
                "D", "DOWN" -> DOWN
                "U", "UP" -> UP
                "ST", "START" -> START
                "SE", "SELECT" -> SELECT
                "B" -> B
                "A" -> A
                else -> 0
            }
        }
        return buttons
    }
    
    /**
     * Parse FCEUX/BizHawk format: "RLDUTSBA" where each position is either
     * the button letter (pressed) or '.' (not pressed)
     *
     * FM2 format order: R L D U T S B A (positions 0-7)
     * Maps to SMB bit order: Right=bit0, Left=bit1, Down=bit2, Up=bit3, Start=bit4, Select=bit5, B=bit6, A=bit7
     */
    fun parseFceuxFormat(input: String): Int {
        var buttons = 0
        if (input.length >= 8) {
            if (input[0] != '.') buttons = buttons or RIGHT   // 0x01
            if (input[1] != '.') buttons = buttons or LEFT    // 0x02
            if (input[2] != '.') buttons = buttons or DOWN    // 0x04
            if (input[3] != '.') buttons = buttons or UP      // 0x08
            if (input[4] != '.') buttons = buttons or START   // 0x10
            if (input[5] != '.') buttons = buttons or SELECT  // 0x20
            if (input[6] != '.') buttons = buttons or B       // 0x40
            if (input[7] != '.') buttons = buttons or A       // 0x80
        }
        return buttons
    }
    
    /**
     * Reset controller state
     */
    fun reset() {
        buttonState[0] = 0
        buttonState[1] = 0
        shiftRegister[0] = 0
        shiftRegister[1] = 0
        strobeState = false
    }
}

/**
 * TAS movie format for input playback
 */
class TASMovie(
    val inputs: List<FrameInput>
) {
    data class FrameInput(
        val player1: Int = 0,
        val player2: Int = 0
    )
    
    companion object {
        /**
         * Parse FM2 (FCEUX movie) format
         * Format: |command|player1|player2| per line
         * Where player buttons are in RLDUTSBA format
         */
        fun parseFM2(content: String): TASMovie {
            val inputs = mutableListOf<FrameInput>()
            val controller = ControllerInput()
            
            for (line in content.lines()) {
                // Skip comments and metadata
                if (line.startsWith("comment") || 
                    line.startsWith("subtitle") ||
                    line.isBlank() ||
                    !line.startsWith("|")) {
                    continue
                }
                
                // Parse |cmd|p1|p2| format
                val parts = line.removePrefix("|").removeSuffix("|").split("|")
                if (parts.size >= 2) {
                    val p1 = if (parts.size >= 2) controller.parseFceuxFormat(parts[1].padEnd(8, '.')) else 0
                    val p2 = if (parts.size >= 3) controller.parseFceuxFormat(parts[2].padEnd(8, '.')) else 0
                    inputs.add(FrameInput(p1, p2))
                }
            }
            
            return TASMovie(inputs)
        }
        
        /**
         * Parse BK2 (BizHawk movie) input log format
         * Similar to FM2 but may have different header format
         */
        fun parseBK2InputLog(content: String): TASMovie {
            // BK2 input log format is similar to FM2
            return parseFM2(content)
        }
        
        /**
         * Create a simple movie from button string per frame
         * Format: one line per frame, each line is buttons for that frame
         */
        fun fromSimpleFormat(content: String): TASMovie {
            val inputs = mutableListOf<FrameInput>()
            val controller = ControllerInput()
            
            for (line in content.lines()) {
                if (line.isBlank() || line.startsWith("#")) continue
                val buttons = controller.parseButtons(line.trim())
                inputs.add(FrameInput(buttons, 0))
            }
            
            return TASMovie(inputs)
        }
    }
    
    /**
     * Get input for a specific frame
     */
    fun getFrame(frameNumber: Int): FrameInput {
        return if (frameNumber < inputs.size) {
            inputs[frameNumber]
        } else {
            FrameInput(0, 0)  // No input past end of movie
        }
    }
    
    val frameCount: Int get() = inputs.size
}
