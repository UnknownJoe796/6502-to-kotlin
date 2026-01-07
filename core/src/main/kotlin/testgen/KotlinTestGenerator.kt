package com.ivieleague.decompiler6502tokotlin.testgen

import java.io.File

/**
 * Generates Kotlin test files from captured function call data.
 */
class KotlinTestGenerator(
    /** Package name for generated tests */
    private val packageName: String = "com.ivieleague.decompiler6502tokotlin.generated",

    /** Function name lookup (address -> name) */
    private val functionNames: Map<Int, String> = emptyMap(),

    /** Set of function names that are available and parameterless (can be tested) */
    private val validFunctions: Set<String> = emptySet(),

    /**
     * Maximum offset from function entry point for fuzzy matching.
     * Set to 0 to disable fuzzy matching (exact matches only).
     * Recommended: 10-20 for conservative matching, 50 for aggressive.
     */
    private val fuzzyMatchThreshold: Int = 0,

    /**
     * Map of decompiled function addresses to their names.
     * Required for fuzzy matching to work.
     */
    private val decompiledFunctionAddresses: Map<Int, String> = emptyMap(),

    /** Import statements to include */
    private val imports: List<String> = listOf(
        "com.ivieleague.decompiler6502tokotlin.hand.*",
        "com.ivieleague.decompiler6502tokotlin.smb.*",  // Import decompiled SMB functions
        "kotlin.test.Test",
        "kotlin.test.assertEquals"
    )
) {
    // Sorted list of decompiled function addresses for efficient fuzzy lookup
    private val sortedDecompiledAddresses = decompiledFunctionAddresses.keys.sorted()

    /**
     * Find the best matching decompiled function for a captured address.
     * Returns the function name and offset, or null if no match found.
     */
    private fun findMatchingFunction(capturedAddress: Int): Pair<String, Int>? {
        // First check for exact match
        decompiledFunctionAddresses[capturedAddress]?.let {
            return it to 0
        }

        // If fuzzy matching disabled, no match
        if (fuzzyMatchThreshold <= 0) return null

        // Find nearest preceding decompiled function address
        val nearestPreceding = sortedDecompiledAddresses
            .filter { it < capturedAddress }
            .maxOrNull() ?: return null

        val offset = capturedAddress - nearestPreceding
        if (offset > fuzzyMatchThreshold) return null

        val funcName = decompiledFunctionAddresses[nearestPreceding] ?: return null
        return funcName to offset
    }
    /**
     * Generate test file for all captured functions.
     */
    fun generateTestFile(
        data: TestDataSerialization.CapturedTestData,
        outputFile: File
    ) {
        val sb = StringBuilder()

        // File header
        sb.appendLine("@file:OptIn(ExperimentalUnsignedTypes::class)")
        sb.appendLine()
        sb.appendLine("package $packageName")
        sb.appendLine()
        for (import in imports) {
            sb.appendLine("import $import")
        }
        sb.appendLine()
        sb.appendLine("/**")
        sb.appendLine(" * Auto-generated tests from TAS capture: ${data.tasName}")
        sb.appendLine(" * Captured at: ${data.captureTimestamp}")
        sb.appendLine(" * Total frames: ${data.totalFrames}")
        sb.appendLine(" * Total captures: ${data.totalCaptures}")
        sb.appendLine(" * Functions with tests: ${data.functions.size}")
        if (fuzzyMatchThreshold > 0) {
            sb.appendLine(" * Fuzzy matching enabled: threshold = $fuzzyMatchThreshold bytes")
        }
        sb.appendLine(" *")
        sb.appendLine(" * These tests verify that decompiled functions produce the same")
        sb.appendLine(" * outputs as the original 6502 binary interpreter.")
        sb.appendLine(" */")
        sb.appendLine("class GeneratedFunctionTests {")
        sb.appendLine()

        // Group captured functions by their matched decompiled function
        data class MatchedFunction(
            val funcName: String,
            val capturedAddress: Int,
            val offset: Int,
            val funcData: TestDataSerialization.FunctionTestData
        )

        val matchedFunctions = mutableListOf<MatchedFunction>()
        var skippedFunctions = 0
        var exactMatches = 0
        var fuzzyMatches = 0

        for ((_, funcData) in data.functions.entries.sortedBy { it.value.address }) {
            // Try to find a matching decompiled function
            val match = findMatchingFunction(funcData.address)

            if (match != null) {
                val (funcName, offset) = match
                // Check if this function is in validFunctions
                if (validFunctions.isEmpty() || funcName in validFunctions) {
                    matchedFunctions.add(MatchedFunction(funcName, funcData.address, offset, funcData))
                    if (offset == 0) exactMatches++ else fuzzyMatches++
                } else {
                    skippedFunctions++
                }
            } else {
                // Fall back to old behavior for backwards compatibility
                val funcName = funcData.functionName
                    ?: functionNames[funcData.address]
                    ?: "func_${funcData.addressHex.removePrefix("0x")}"

                if (validFunctions.isEmpty() || funcName in validFunctions) {
                    matchedFunctions.add(MatchedFunction(funcName, funcData.address, 0, funcData))
                    exactMatches++
                } else {
                    skippedFunctions++
                }
            }
        }

        // Group by function name and generate tests
        val groupedByFunction = matchedFunctions.groupBy { it.funcName }
        var generatedFunctions = 0

        for ((funcName, matches) in groupedByFunction.entries.sortedBy { it.value.first().capturedAddress }) {
            generatedFunctions++

            // Take the best match (prefer exact, then smallest offset)
            val bestMatch = matches.minByOrNull { it.offset }!!
            val funcData = bestMatch.funcData
            val offset = bestMatch.offset

            sb.appendLine("    // =========================================")
            sb.appendLine("    // ${funcData.addressHex}: $funcName")
            if (offset > 0) {
                sb.appendLine("    // FUZZY MATCH: captured at +$offset bytes from entry")
            }
            sb.appendLine("    // ${funcData.totalCalls} calls, ${funcData.uniqueInputStates} unique inputs")
            sb.appendLine("    // =========================================")
            sb.appendLine()

            funcData.testCases.forEachIndexed { testIdx, testCase ->
                generateTestMethod(sb, funcName, funcData.addressHex, testIdx, testCase, offset)
            }
        }

        // Insert summary comment
        val summaryComment = buildString {
            if (skippedFunctions > 0) {
                appendLine("// Note: $skippedFunctions functions skipped (not in validFunctions)")
            }
            appendLine("// Generated tests for $generatedFunctions functions")
            appendLine("// Exact matches: $exactMatches, Fuzzy matches: $fuzzyMatches")
            appendLine()
        }
        sb.insert(sb.indexOf("class GeneratedFunctionTests"), summaryComment)

        sb.appendLine("}")

        outputFile.parentFile?.mkdirs()
        outputFile.writeText(sb.toString())
    }

    /**
     * Generate a single test method.
     */
    private fun generateTestMethod(
        sb: StringBuilder,
        funcName: String,
        addressHex: String,
        testIdx: Int,
        testCase: FunctionCallCapture,
        offset: Int = 0
    ) {
        val safeFuncName = funcName.replace("-", "_").replace(".", "_")
        val methodName = "${safeFuncName}_frame${testCase.frame}_test$testIdx"

        sb.appendLine("    /**")
        sb.appendLine("     * Test case $testIdx from frame ${testCase.frame}")
        sb.appendLine("     * Function: $funcName ($addressHex)")
        if (offset > 0) {
            sb.appendLine("     * FUZZY MATCH: entry +$offset bytes")
        }
        sb.appendLine("     * Call depth: ${testCase.callDepth}")
        sb.appendLine("     * Memory reads: ${testCase.memoryReads.size}, writes: ${testCase.memoryWrites.size}")
        sb.appendLine("     */")
        sb.appendLine("    @Test")
        sb.appendLine("    fun `$methodName`() {")
        sb.appendLine("        // Setup: Reset state")
        sb.appendLine("        resetCPU()")
        sb.appendLine("        clearMemory()")
        sb.appendLine()
        // Note: We don't set CPU registers/flags as input since the decompiled code
        // is high-level Kotlin that doesn't use global register state.
        // The function reads its inputs from memory.
        sb.appendLine("        // Setup: Set input memory (${testCase.memoryReads.size} addresses)")
        if (testCase.memoryReads.isEmpty()) {
            sb.appendLine("        // No memory inputs")
        } else {
            for ((addr, value) in testCase.memoryReads.toSortedMap()) {
                val addrHex = addr.toString(16).uppercase().padStart(4, '0')
                val valHex = value.toString(16).uppercase().padStart(2, '0')
                sb.appendLine("        memory[0x$addrHex] = 0x${valHex}u")
            }
        }
        sb.appendLine()
        sb.appendLine("        // Execute decompiled function")
        sb.appendLine("        $funcName()")
        sb.appendLine()
        // Note: We don't check CPU registers/flags since the decompiled code is high-level
        // Kotlin that uses local variables, not global register state.
        // We only verify memory writes (the actual side effects).
        // Filter out stack writes (0x0100-0x01FF) since decompiled code uses native Kotlin calls
        val nonStackWrites = testCase.memoryWrites.filter { (addr, _) -> addr !in 0x0100..0x01FF }
        sb.appendLine("        // Verify: Check output memory (${nonStackWrites.size} addresses)")
        if (nonStackWrites.isEmpty()) {
            sb.appendLine("        // No memory outputs to verify (or only stack writes)")
        } else {
            for ((addr, value) in nonStackWrites.toSortedMap()) {
                val addrHex = addr.toString(16).uppercase().padStart(4, '0')
                val valHex = value.toString(16).uppercase().padStart(2, '0')
                sb.appendLine("        assertEquals(0x${valHex}u, memory[0x$addrHex], \"Memory 0x$addrHex mismatch\")")
            }
        }
        sb.appendLine("    }")
        sb.appendLine()
    }

    /**
     * Generate a summary of the test data.
     */
    fun generateSummary(data: TestDataSerialization.CapturedTestData): String {
        val sb = StringBuilder()
        sb.appendLine("=== Test Generation Summary ===")
        sb.appendLine("TAS: ${data.tasName}")
        sb.appendLine("Total frames: ${data.totalFrames}")
        sb.appendLine("Total captures: ${data.totalCaptures}")
        sb.appendLine("Functions with tests: ${data.functions.size}")
        sb.appendLine()
        sb.appendLine("Functions (sorted by call count):")
        for ((_, funcData) in data.functions.entries.sortedByDescending { it.value.totalCalls }) {
            val name = funcData.functionName ?: functionNames[funcData.address] ?: "unknown"
            sb.appendLine("  ${funcData.addressHex} ($name): ${funcData.totalCalls} calls -> ${funcData.testCases.size} tests")
        }
        return sb.toString()
    }
}
