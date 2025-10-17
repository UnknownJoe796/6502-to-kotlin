package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Pass3ValidationEnhancedTest {
    
    @Test
    fun illegalAddressingModeDetection() {
        val assembly = """
            start:
                LDA A       ; Valid - accumulator mode for LDA doesn't exist, should be caught
                BEQ #${'$'}10    ; Invalid - branch with immediate 
                STA #${'$'}20    ; Invalid - store with immediate
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val report = lines.validateDisassembly()
        
        assertTrue(report.issues.isNotEmpty(), "Should detect addressing mode issues")
        
        val addressingIssues = report.addressingModeIssues
        assertTrue(addressingIssues.any { it.message.contains("Branch BEQ must target a label") })
        assertTrue(addressingIssues.any { it.message.contains("Illegal addressing mode for STA") })
    }
    
    @Test
    fun validInstructionsPassValidation() {
        val assembly = """
            start:
                LDA #${'$'}10
                STA ${'$'}2000
                LDX ${'$'}20
                BEQ end
                INX
                JMP start
            end:
                RTS
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val report = lines.validateDisassembly()
        
        assertEquals(0, report.issues.size, "Valid assembly should pass validation")
    }
    
    @Test
    fun dataInCodeSectionDetection() {
        val assembly = """
            start:
                LDA #${'$'}10
                .db ${'$'}20, ${'$'}30    ; Data in reachable code
                STA ${'$'}2000
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses()
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("start"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val report = lines.validateDisassembly(resolution, reachability)
        
        val dataInCodeIssues = report.dataInCodeIssues
        assertTrue(dataInCodeIssues.isNotEmpty(), "Should detect data in code section")
        assertTrue(dataInCodeIssues.any { it.message.contains("Data directive found in reachable code section") })
    }
    
    @Test
    fun branchTargetValidation() {
        val assembly = """
            start:
                CMP #${'$'}10
                BEQ target
                BNE #${'$'}1000   ; Invalid - branch with immediate addressing
            target:
                RTS
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val report = lines.validateDisassembly()
        
        val branchIssues = report.addressingModeIssues.filter { it.message.contains("Branch") }
        assertEquals(1, branchIssues.size, "Should detect one invalid branch")
        assertTrue(branchIssues[0].message.contains("Branch BNE must target a label"))
    }
    
    @Test
    fun validationIssueTypes() {
        val assembly = """
            start:
                LDA #${'$'}10
                STA #${'$'}20    ; Illegal addressing mode
                BEQ #${'$'}1000   ; Illegal addressing mode for branch
                .db ${'$'}30     ; Will be data in code if reachable
        """.trimIndent()
        
        val lines = assembly.parseAssemblyLines()
        val resolution = lines.resolveAddresses()
        val entries = lines.discoverEntryPoints(resolution, exportedLabels = setOf("start"))
        val reachability = lines.analyzeReachability(resolution, entries)
        val report = lines.validateDisassembly(resolution, reachability)
        
        // Should have issues of different types
        assertTrue(report.addressingModeIssues.isNotEmpty())
        assertTrue(report.dataInCodeIssues.isNotEmpty())
        
        // Check that categorization works
        assertEquals(
            report.addressingModeIssues.size + report.dataInCodeIssues.size + report.opcodeIssues.size,
            report.issues.size
        )
    }
}