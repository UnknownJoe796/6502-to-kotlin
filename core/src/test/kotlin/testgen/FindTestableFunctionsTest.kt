@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.testgen

import java.io.File
import kotlin.test.Test

class FindTestableFunctionsTest {
    
    @Test
    fun fuzzyMatchCapturedToDecompiled() {
        val asmFile = File("smbdism.asm")
        val mapper = AddressLabelMapper.fromAssemblyFile(asmFile)
        val allLabeledAddrs = mapper.getAllAddresses().sorted()
        
        // Get decompiled parameterless functions
        val decompiledFile = File("smb/src/main/kotlin/com/ivieleague/decompiler6502tokotlin/smb/SMBDecompiled.kt")
        val funcRegex = Regex("""^fun ([a-zA-Z_][a-zA-Z0-9_]*)\(\)""", RegexOption.MULTILINE)
        val decompiledFuncs = funcRegex.findAll(decompiledFile.readText()).map { it.groupValues[1] }.toSet()
        
        // Load captured addresses with call counts
        val capturedFile = File("local/testgen/captured-tests-happylee-warps.json")
        val json = capturedFile.readText()
        
        // Parse function data
        val funcPattern = Regex(""""(0x[0-9A-Fa-f]+)":\s*\{[^}]*"totalCalls":\s*(\d+)""")
        val capturedData = funcPattern.findAll(json).map {
            val addr = it.groupValues[1].removePrefix("0x").toInt(16)
            val calls = it.groupValues[2].toInt()
            addr to calls
        }.sortedByDescending { it.second }.toList()
        
        println("=== Fuzzy Matching: Captured -> Nearest Preceding Label -> Decompiled ===\n")
        
        var totalTestable = 0
        val testableEntries = mutableListOf<Triple<Int, String, Int>>()
        
        for ((captured, calls) in capturedData) {
            // Find nearest preceding label
            val nearestBefore = allLabeledAddrs.filter { it <= captured }.maxOrNull() ?: continue
            val distance = captured - nearestBefore
            
            // Skip if too far (more than 50 bytes)
            if (distance > 50) continue
            
            val label = mapper.getLabel(nearestBefore) ?: continue
            val funcName = AddressLabelMapper.labelToKotlinFunctionName(label)
            
            if (funcName in decompiledFuncs) {
                totalTestable++
                if (testableEntries.size < 30) {
                    testableEntries.add(Triple(captured, funcName, calls))
                }
            }
        }
        
        testableEntries.forEach { (addr, funcName, calls) ->
            val nearestBefore = allLabeledAddrs.filter { it <= addr }.maxOrNull()!!
            val distance = addr - nearestBefore
            val status = if (distance == 0) "EXACT" else "+$distance"
            println("0x${addr.toString(16).uppercase()}: $funcName ($calls calls) [$status]")
        }
        
        println("\n--- Summary ---")
        println("Potential testable (with fuzzy matching): $totalTestable")
        println("This would allow testing ${testableEntries.distinctBy { it.second }.size} unique functions")
    }
}
