package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Pass2AddressResolutionFixTest {
    
    @Test
    fun zeroPageAddressingDetection() {
        val assembly = """
            start:
                LDA ${'$'}10     ; Zero-page addressing - should be 2 bytes
                LDA ${'$'}1000   ; Absolute addressing - should be 3 bytes  
                LDA ${'$'}FF     ; Zero-page addressing - should be 2 bytes
                STA ${'$'}200    ; Absolute addressing - should be 3 bytes
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses(0x8000)
        
        // Check instruction sizes
        val instructions = resolution.resolved.filter { it.line.instruction != null }
        assertEquals(4, instructions.size)
        
        // LDA $10 - zero page
        assertEquals(2, instructions[0].sizeBytes, "LDA ${'$'}10 should be 2 bytes (zero-page)")
        
        // LDA $1000 - absolute  
        assertEquals(3, instructions[1].sizeBytes, "LDA ${'$'}1000 should be 3 bytes (absolute)")
        
        // LDA $FF - zero page
        assertEquals(2, instructions[2].sizeBytes, "LDA ${'$'}FF should be 2 bytes (zero-page)")
        
        // STA $200 - absolute
        assertEquals(3, instructions[3].sizeBytes, "STA ${'$'}200 should be 3 bytes (absolute)")
    }
    
    @Test
    fun indexedAddressingDetection() {
        val assembly = """
            start:
                LDA ${'$'}10,X   ; Zero-page indexed - should be 2 bytes
                LDA ${'$'}1000,X ; Absolute indexed - should be 3 bytes
                STA ${'$'}20,Y   ; Zero-page indexed - should be 2 bytes
                STA ${'$'}2000,Y ; Absolute indexed - should be 3 bytes
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses(0x8000)
        
        val instructions = resolution.resolved.filter { it.line.instruction != null }
        assertEquals(4, instructions.size)
        
        assertEquals(2, instructions[0].sizeBytes, "LDA ${'$'}10,X should be 2 bytes")
        assertEquals(3, instructions[1].sizeBytes, "LDA ${'$'}1000,X should be 3 bytes")
        assertEquals(2, instructions[2].sizeBytes, "STA ${'$'}20,Y should be 2 bytes")
        assertEquals(3, instructions[3].sizeBytes, "STA ${'$'}2000,Y should be 3 bytes")
    }
    
    @Test
    fun dataDirectiveByteCountAccurate() {
        val assembly = """
            data_section:
                .db ${'$'}10, ${'$'}20, ${'$'}30       ; 3 bytes
                .db "hello"             ; 5 bytes  
                .db ${'$'}40, "world", ${'$'}50   ; 1 + 5 + 1 = 7 bytes
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses(0x2000)
        
        val dataLines = resolution.resolved.filter { it.isData }
        assertEquals(3, dataLines.size)
        
        assertEquals(3, dataLines[0].sizeBytes, "First .db should be 3 bytes")
        assertEquals(5, dataLines[1].sizeBytes, "Second .db should be 5 bytes")  
        assertEquals(7, dataLines[2].sizeBytes, "Third .db should be 7 bytes")
    }
    
    @Test
    fun labelToAddressMapping() {
        val assembly = """
            start:
                LDA #${'$'}10
            loop:
                DEX
                BNE loop
            end:
                RTS
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses(0x8000)
        
        assertEquals(0x8000, resolution.labelToAddress["start"])
        assertEquals(0x8002, resolution.labelToAddress["loop"])
        assertEquals(0x8005, resolution.labelToAddress["end"])
    }
}