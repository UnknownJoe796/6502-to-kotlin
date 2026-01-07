@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.testgen

import java.io.File
import kotlin.test.Test

class AnalyzeCapturedFunctionsTest {
    
    @Test
    fun analyzeCapturedVsDecompiled() {
        val asmFile = File("smbdism.asm")
        val mapper = AddressLabelMapper.fromAssemblyFile(asmFile)
        val allLabeledAddrs = mapper.getAllAddresses().sorted()
        
        // Get decompiled parameterless functions
        val decompiledFile = File("smb/src/main/kotlin/com/ivieleague/decompiler6502tokotlin/smb/SMBDecompiled.kt")
        val funcRegex = Regex("""^fun ([a-zA-Z_][a-zA-Z0-9_]*)\(\)""", RegexOption.MULTILINE)
        val decompiledFuncs = funcRegex.findAll(decompiledFile.readText()).map { it.groupValues[1] }.toSet()
        
        // Build set of decompiled function addresses
        val decompiledAddrs = mutableSetOf<Int>()
        for (label in mapper.getFunctionLabels()) {
            val funcName = AddressLabelMapper.labelToKotlinFunctionName(label)
            if (funcName in decompiledFuncs) {
                mapper.getAddress(label)?.let { decompiledAddrs.add(it) }
            }
        }
        
        // Load captured data
        val capturedFile = File("local/testgen/captured-tests-happylee-warps.json")
        val json = capturedFile.readText()
        val funcPattern = Regex(""""(0x[0-9A-Fa-f]+)":\s*\{[^}]*"totalCalls":\s*(\d+)""")
        val capturedData = funcPattern.findAll(json).map {
            val addr = it.groupValues[1].removePrefix("0x").toInt(16)
            val calls = it.groupValues[2].toInt()
            addr to calls
        }.toList()
        
        println("=== Analysis of Captured vs Decompiled Functions ===")
        println("Decompiled parameterless functions: ${decompiledFuncs.size}")
        println("Decompiled function addresses: ${decompiledAddrs.size}")
        println("Captured unique addresses: ${capturedData.size}")
        println()
        
        // Find exact matches
        val exactMatches = capturedData.filter { it.first in decompiledAddrs }
        println("EXACT MATCHES (${exactMatches.size}):")
        for ((addr, calls) in exactMatches.sortedByDescending { it.second }) {
            val label = mapper.getLabel(addr)
            val funcName = mapper.getFunctionName(addr)
            println("  0x${addr.toString(16).uppercase()}: $funcName ($calls calls)")
        }
        
        println()
        
        // Find near matches (within 50 bytes of a decompiled function)
        val nearMatches = mutableListOf<Triple<Int, Int, Int>>()  // captured, decompiled, calls
        for ((captured, calls) in capturedData) {
            if (captured in decompiledAddrs) continue
            
            val nearestDecompiled = decompiledAddrs.filter { 
                it <= captured && captured - it <= 50 
            }.maxOrNull()
            
            if (nearestDecompiled != null) {
                nearMatches.add(Triple(captured, nearestDecompiled, calls))
            }
        }
        
        println("NEAR MATCHES within 50 bytes (${nearMatches.size}):")
        for ((captured, decompiled, calls) in nearMatches.sortedByDescending { it.third }.take(20)) {
            val funcName = mapper.getFunctionName(decompiled)
            val offset = captured - decompiled
            println("  0x${captured.toString(16).uppercase()} -> $funcName +$offset ($calls calls)")
        }
        
        println()
        
        // Find decompiled functions NOT captured at all
        val capturedAddrSet = capturedData.map { it.first }.toSet()
        val notCaptured = decompiledAddrs.filter { addr ->
            addr !in capturedAddrSet && 
            !capturedData.any { it.first > addr && it.first - addr <= 50 }
        }
        println("DECOMPILED BUT NOT CAPTURED (${notCaptured.size}):")
        for (addr in notCaptured.take(20)) {
            val funcName = mapper.getFunctionName(addr)
            println("  0x${addr.toString(16).uppercase()}: $funcName")
        }
    }
}
