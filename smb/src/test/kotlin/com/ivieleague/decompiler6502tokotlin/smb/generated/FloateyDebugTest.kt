@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.smb.generated

import com.ivieleague.decompiler6502tokotlin.hand.*
import com.ivieleague.decompiler6502tokotlin.smb.*
import kotlin.test.Test
import kotlin.test.assertEquals
import java.io.File

/**
 * Debug test for floateyNumbersRoutine
 */
class FloateyDebugTest {

    @Test
    fun `debug floateyNumbersRoutine sprite writes`() {
        val log = StringBuilder()
        fun log(s: String) { log.appendLine(s); println(s) }

        // Reset state
        resetCPU()
        clearMemory()

        // Setup input memory (from test4)
        memory[0x0008] = 0x00u  // ObjectOffset
        memory[0x0016] = 0x06u  // Enemy_ID[0]
        memory[0x001E] = 0x04u  // Enemy_State[0]
        memory[0x0110] = 0x01u  // FloateyNum_Control[0]
        memory[0x0117] = 0x6Cu  // FloateyNum_X_Pos[0]
        memory[0x011E] = 0xB6u  // FloateyNum_Y_Pos[0]
        memory[0x012C] = 0x2Eu  // FloateyNum_Timer[0]
        memory[0x01F6] = 0x3Eu
        memory[0x01F7] = 0x85u
        memory[0x06E5] = 0x30u  // Enemy_SprDataOffset[0]

        // Print key inputs
        log("=== Input State ===")
        log("ObjectOffset (0x0008): ${memory[0x0008]}")
        log("Enemy_ID[0] (0x0016): ${memory[0x0016]}")
        log("Enemy_State[0] (0x001E): ${memory[0x001E]}")
        log("FloateyNum_Control[0] (0x0110): ${memory[0x0110]}")
        log("FloateyNum_X_Pos[0] (0x0117): ${memory[0x0117]}")
        log("FloateyNum_Y_Pos[0] (0x011E): ${memory[0x011E]}")
        log("FloateyNum_Timer[0] (0x012C): ${memory[0x012C]}")
        log("Enemy_SprDataOffset[0] (0x06E5): ${memory[0x06E5]}")

        // Execute
        log("\n=== Executing floateyNumbersRoutine(0x00) ===")
        floateyNumbersRoutine(0x00)

        // Check sprite data area
        log("\n=== Sprite Data Outputs ===")
        for (offset in 0x30..0x38) {
            val addr = 0x0200 + offset
            val value = memory[addr]
            log("Sprite_Data+0x${offset.toString(16)} (0x${addr.toString(16)}): 0x${value.toString(16).padStart(2, '0')}")
        }

        // Check specific expected addresses
        log("\n=== Expected Outputs ===")
        log("0x0230 (Y pos 1): 0x${memory[0x0230].toString(16).padStart(2, '0')}")
        log("0x0231 (Tile 1): 0x${memory[0x0231].toString(16).padStart(2, '0')} (expected: 0xF6)")
        log("0x0232 (Attr 1): 0x${memory[0x0232].toString(16).padStart(2, '0')} (expected: 0x02)")
        log("0x0233 (X pos 1): 0x${memory[0x0233].toString(16).padStart(2, '0')} (expected: 0x6C)")
        log("0x0234 (Y pos 2): 0x${memory[0x0234].toString(16).padStart(2, '0')}")
        log("0x0235 (Tile 2): 0x${memory[0x0235].toString(16).padStart(2, '0')} (expected: 0xFB)")
        log("0x0236 (Attr 2): 0x${memory[0x0236].toString(16).padStart(2, '0')} (expected: 0x02)")
        log("0x0237 (X pos 2): 0x${memory[0x0237].toString(16).padStart(2, '0')} (expected: 0x74)")

        // Also check FloateyNum values after execution
        log("\n=== FloateyNum After Execution ===")
        log("FloateyNum_Timer[0] (0x012C): ${memory[0x012C]}")
        log("FloateyNum_Y_Pos[0] (0x011E): ${memory[0x011E]}")
        log("FloateyNum_Control[0] (0x0110): ${memory[0x0110]}")

        // Write to file
        File("local/floatey-debug.log").writeText(log.toString())

        // Assertions
        assertEquals(0xF6u.toUByte(), memory[0x0231], "Tile 1")
    }
}
