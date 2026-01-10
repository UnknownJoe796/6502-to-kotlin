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

    /** Set of function names that exist in the decompiled code (any signature) */
    private val validFunctions: Set<String> = emptySet(),

    /**
     * Function signatures: function name -> list of parameter names (A, X, Y).
     * Empty list means no parameters.
     * Example: "putBlockMetatile" -> listOf("A", "X", "Y")
     */
    private val functionSignatures: Map<String, List<String>> = emptyMap(),

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

    /**
     * Functions to skip due to unreliable capture data.
     * These are typically functions that are fall-through targets (no RTS before label),
     * so the captured data includes operations from the preceding function.
     */
    // by Claude - Added more fall-through targets with unreliable capture data
    private val skipFunctions: Set<String> = setOf(
        // DrawFireball falls through into DrawFirebar, so captured data for
        // "drawFirebar" includes operations from DrawFireball that don't match
        // the decompiled drawFirebar function.
        "drawFirebar",
        // doNothing2 is just RTS but preceding code falls through to it
        // (lda #$ff; sta $06c9 then DoNothing2: rts)
        "doNothing2"
    ),

    /** Timeout in milliseconds for each test (0 = no timeout) */
    private val testTimeoutMs: Long = 1000,

    /** Import statements to include */
    private val imports: List<String> = listOf(
        "com.ivieleague.decompiler6502tokotlin.hand.*",
        "com.ivieleague.decompiler6502tokotlin.smb.*",
        "kotlin.test.Test",
        "kotlin.test.assertEquals",
        "org.junit.jupiter.api.Assertions.assertTimeoutPreemptively",
        "org.junit.jupiter.api.BeforeAll",
        "org.junit.jupiter.api.TestInstance",
        "java.time.Duration",
        "java.io.File"
    ),

    /** ROM file path for loading test data */
    private val romFilePath: String? = null
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
        // by Claude - Add @TestInstance annotation for @BeforeAll support
        sb.appendLine("@TestInstance(TestInstance.Lifecycle.PER_CLASS)")
        sb.appendLine("class GeneratedFunctionTests {")
        sb.appendLine()

        // by Claude - Add @BeforeAll method to load ROM data before any tests run
        if (romFilePath != null) {
            sb.appendLine("    @BeforeAll")
            sb.appendLine("    fun loadRomData() {")
            sb.appendLine("        // Load ROM into memory for functions that read from ROM tables")
            sb.appendLine("        val romFile = File(\"$romFilePath\")")
            sb.appendLine("        if (romFile.exists()) {")
            sb.appendLine("            val romData = romFile.readBytes().toUByteArray()")
            sb.appendLine("            // SMB ROM: 16-byte header + 32KB PRG ROM")
            sb.appendLine("            val prgStart = 16")
            sb.appendLine("            val prgSize = 0x8000")
            sb.appendLine("            for (i in 0 until prgSize) {")
            sb.appendLine("                memory[0x8000 + i] = romData[prgStart + i]")
            sb.appendLine("            }")
            sb.appendLine("        }")
            sb.appendLine("    }")
            sb.appendLine()
        }

        // Group captured functions by their matched decompiled function
        data class MatchedFunction(
            val funcName: String,
            val capturedAddress: Int,
            val offset: Int,
            val funcData: TestDataSerialization.FunctionTestData
        )

        val matchedFunctions = mutableListOf<MatchedFunction>()
        var skippedNotValid = 0
        var skippedNoSignature = 0
        var skippedUnreliable = 0
        var exactMatches = 0
        var fuzzyMatches = 0
        var parameterizedFunctions = 0
        var parameterlessFunctions = 0

        for ((_, funcData) in data.functions.entries.sortedBy { it.value.address }) {
            // Try to find a matching decompiled function
            val match = findMatchingFunction(funcData.address)

            if (match != null) {
                val (funcName, offset) = match

                // Check if this function is in the skip list
                if (funcName in skipFunctions) {
                    skippedUnreliable++
                    continue
                }

                // Check if this function exists in validFunctions
                if (validFunctions.isEmpty() || funcName in validFunctions) {
                    // Check if we have a signature for this function
                    if (functionSignatures.containsKey(funcName)) {
                        matchedFunctions.add(MatchedFunction(funcName, funcData.address, offset, funcData))
                        if (offset == 0) exactMatches++ else fuzzyMatches++
                        if (functionSignatures[funcName]?.isNotEmpty() == true) {
                            parameterizedFunctions++
                        } else {
                            parameterlessFunctions++
                        }
                    } else {
                        skippedNoSignature++
                    }
                } else {
                    skippedNotValid++
                }
            } else {
                // Fall back to old behavior for backwards compatibility
                val funcName = funcData.functionName
                    ?: functionNames[funcData.address]
                    ?: "func_${funcData.addressHex.removePrefix("0x")}"

                // Check if this function is in the skip list
                if (funcName in skipFunctions) {
                    skippedUnreliable++
                    continue
                }

                if (validFunctions.isEmpty() || funcName in validFunctions) {
                    if (functionSignatures.containsKey(funcName)) {
                        matchedFunctions.add(MatchedFunction(funcName, funcData.address, 0, funcData))
                        exactMatches++
                        if (functionSignatures[funcName]?.isNotEmpty() == true) {
                            parameterizedFunctions++
                        } else {
                            parameterlessFunctions++
                        }
                    } else {
                        skippedNoSignature++
                    }
                } else {
                    skippedNotValid++
                }
            }
        }

        // Group by function name and generate tests
        val groupedByFunction = matchedFunctions.groupBy { it.funcName }
        var generatedFunctions = 0
        var generatedTests = 0

        for ((funcName, matches) in groupedByFunction.entries.sortedBy { it.value.first().capturedAddress }) {
            generatedFunctions++

            // Take the best match (prefer exact, then smallest offset)
            val bestMatch = matches.minByOrNull { it.offset }!!
            val funcData = bestMatch.funcData
            val offset = bestMatch.offset
            val params = functionSignatures[funcName] ?: emptyList()

            sb.appendLine("    // =========================================")
            sb.appendLine("    // ${funcData.addressHex}: $funcName")
            if (params.isNotEmpty()) {
                sb.appendLine("    // Parameters: ${params.joinToString(", ")}")
            }
            if (offset > 0) {
                sb.appendLine("    // FUZZY MATCH: captured at +$offset bytes from entry")
            }
            sb.appendLine("    // ${funcData.totalCalls} calls, ${funcData.uniqueInputStates} unique inputs")
            sb.appendLine("    // =========================================")
            sb.appendLine()

            funcData.testCases.forEachIndexed { testIdx, testCase ->
                generateTestMethod(sb, funcName, funcData.addressHex, testIdx, testCase, offset, params)
                generatedTests++
            }
        }

        // Insert summary comment
        val summaryComment = buildString {
            if (skippedNotValid > 0) {
                appendLine("// Note: $skippedNotValid functions skipped (not in validFunctions)")
            }
            if (skippedNoSignature > 0) {
                appendLine("// Note: $skippedNoSignature functions skipped (no signature found)")
            }
            if (skippedUnreliable > 0) {
                appendLine("// Note: $skippedUnreliable functions skipped (unreliable capture data)")
            }
            appendLine("// Generated $generatedTests tests for $generatedFunctions functions")
            appendLine("// Exact matches: $exactMatches, Fuzzy matches: $fuzzyMatches")
            appendLine("// Parameterless: $parameterlessFunctions, With parameters: $parameterizedFunctions")
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
        offset: Int = 0,
        params: List<String> = emptyList()
    ) {
        val safeFuncName = funcName.replace("-", "_").replace(".", "_")
        val methodName = "${safeFuncName}_frame${testCase.frame}_test$testIdx"

        sb.appendLine("    /**")
        sb.appendLine("     * Test case $testIdx from frame ${testCase.frame}")
        sb.appendLine("     * Function: $funcName ($addressHex)")
        if (params.isNotEmpty()) {
            sb.appendLine("     * Parameters: ${params.joinToString(", ")}")
        }
        if (offset > 0) {
            sb.appendLine("     * FUZZY MATCH: entry +$offset bytes")
        }
        sb.appendLine("     * Call depth: ${testCase.callDepth}")
        sb.appendLine("     * Memory reads: ${testCase.memoryReads.size}, writes: ${testCase.memoryWrites.size}")
        sb.appendLine("     */")
        sb.appendLine("    @Test")
        sb.appendLine("    fun `$methodName`() {")

        // Indent for assertTimeoutPreemptively wrapper
        val indent = if (testTimeoutMs > 0) "            " else "        "

        if (testTimeoutMs > 0) {
            sb.appendLine("        assertTimeoutPreemptively(Duration.ofMillis($testTimeoutMs)) {")
        }

        sb.appendLine("${indent}// Setup: Reset state")
        sb.appendLine("${indent}resetCPU()")
        sb.appendLine("${indent}clearMemory()")
        sb.appendLine()

        // Set input memory
        sb.appendLine("${indent}// Setup: Set input memory (${testCase.memoryReads.size} addresses)")
        if (testCase.memoryReads.isEmpty()) {
            sb.appendLine("${indent}// No memory inputs")
        } else {
            for ((addr, value) in testCase.memoryReads.toSortedMap()) {
                val addrHex = addr.toString(16).uppercase().padStart(4, '0')
                val valHex = value.toString(16).uppercase().padStart(2, '0')
                sb.appendLine("${indent}memory[0x$addrHex] = 0x${valHex}u")
            }
        }
        sb.appendLine()

        // Build function call with parameters
        val args = params.map { param ->
            when (param) {
                "A" -> "0x${testCase.inputState.A.toString(16).uppercase().padStart(2, '0')}"
                "X" -> "0x${testCase.inputState.X.toString(16).uppercase().padStart(2, '0')}"
                "Y" -> "0x${testCase.inputState.Y.toString(16).uppercase().padStart(2, '0')}"
                else -> "0"
            }
        }
        val callExpr = if (args.isEmpty()) {
            "$funcName()"
        } else {
            "$funcName(${args.joinToString(", ")})"
        }

        sb.appendLine("${indent}// Execute decompiled function")
        sb.appendLine("${indent}$callExpr")
        sb.appendLine()

        // Filter out stack writes (0x0100-0x01FF) since decompiled code uses native Kotlin calls
        val nonStackWrites = testCase.memoryWrites.filter { (addr, _) -> addr !in 0x0100..0x01FF }
        sb.appendLine("${indent}// Verify: Check output memory (${nonStackWrites.size} addresses)")
        if (nonStackWrites.isEmpty()) {
            sb.appendLine("${indent}// No memory outputs to verify (or only stack writes)")
        } else {
            for ((addr, value) in nonStackWrites.toSortedMap()) {
                val addrHex = addr.toString(16).uppercase().padStart(4, '0')
                val valHex = value.toString(16).uppercase().padStart(2, '0')
                sb.appendLine("${indent}assertEquals(0x${valHex}u, memory[0x$addrHex], \"Memory 0x$addrHex mismatch\")")
            }
        }

        if (testTimeoutMs > 0) {
            sb.appendLine("        }")
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

    companion object {
        /**
         * Parse function signatures from a Kotlin source file.
         * Returns a map of function name -> list of parameter names.
         */
        fun parseSignaturesFromFile(file: File): Map<String, List<String>> {
            if (!file.exists()) return emptyMap()
            return parseSignaturesFromText(file.readText())
        }

        /**
         * Parse function signatures from Kotlin source text.
         * Returns a map of function name -> list of parameter names.
         */
        fun parseSignaturesFromText(text: String): Map<String, List<String>> {
            val result = mutableMapOf<String, List<String>>()

            // Match: fun functionName() or fun functionName(params)
            val funPattern = Regex("""^fun ([a-zA-Z_][a-zA-Z0-9_]*)\(([^)]*)\)""", RegexOption.MULTILINE)

            for (match in funPattern.findAll(text)) {
                val funcName = match.groupValues[1]
                val paramsStr = match.groupValues[2].trim()

                val params = if (paramsStr.isEmpty()) {
                    emptyList()
                } else {
                    // Parse "A: Int, X: Int, Y: Int" -> ["A", "X", "Y"]
                    paramsStr.split(",")
                        .map { it.trim().substringBefore(":").trim() }
                        .filter { it.isNotEmpty() }
                }

                result[funcName] = params
            }

            return result
        }
    }
}
