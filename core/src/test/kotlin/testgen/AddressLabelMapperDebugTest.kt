@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.testgen

import java.io.File
import kotlin.test.Test

class AddressLabelMapperDebugTest {
    
    @Test
    fun verifyFloateyNumbersRoutineAddress() {
        val asmFile = File("smbdism.asm")
        val mapper = AddressLabelMapper.fromAssemblyFile(asmFile)
        
        val addr = mapper.getAddress("FloateyNumbersRoutine")
        println("FloateyNumbersRoutine: 0x${addr?.toString(16)?.uppercase()}")
        
        // Check what's at 0x84C3
        val label = mapper.getLabel(0x84C3)
        println("Label at 0x84C3: $label")
        
        // Check nearby labels
        println("\nNearby labels:")
        for (delta in -20..20) {
            val nearAddr = 0x84C3 + delta
            val nearLabel = mapper.getLabel(nearAddr)
            if (nearLabel != null) {
                println("0x${nearAddr.toString(16).uppercase()}: $nearLabel")
            }
        }
    }
}
