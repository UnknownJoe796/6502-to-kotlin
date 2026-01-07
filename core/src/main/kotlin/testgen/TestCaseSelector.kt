package com.ivieleague.decompiler6502tokotlin.testgen

/**
 * Selects representative test cases from captured function calls.
 * Deduplicates by input state hash and samples evenly across time.
 */
class TestCaseSelector(
    /** Maximum test cases per function */
    private val maxTestsPerFunction: Int = 10,

    /** Minimum different states required to generate any tests */
    private val minUniqueStates: Int = 1,

    /** Maximum memory operations (reads + writes) for a valid test case */
    private val maxMemoryOperations: Int = 500
) {
    /**
     * Select test cases from captures.
     * Returns map of function address -> selected captures.
     */
    fun selectTestCases(
        captures: List<FunctionCallCapture>
    ): Map<Int, List<FunctionCallCapture>> {
        // Group by function address
        val byFunction = captures.groupBy { it.functionAddress }

        return byFunction.mapValues { (_, functionCaptures) ->
            selectForFunction(functionCaptures)
        }.filter { it.value.isNotEmpty() }
    }

    /**
     * Select test cases for a single function.
     */
    private fun selectForFunction(
        captures: List<FunctionCallCapture>
    ): List<FunctionCallCapture> {
        // Filter out captures with excessive memory operations
        // These are likely corrupted by NMI interference or merge behavior
        val filtered = captures.filter { capture ->
            val totalOps = capture.memoryReads.size + capture.memoryWrites.size
            totalOps <= maxMemoryOperations
        }

        // Deduplicate by input state hash
        val uniqueByHash = filtered
            .groupBy { it.inputStateHash }
            .mapValues { (_, group) ->
                // Keep the first occurrence of each unique input
                group.first()
            }
            .values.toList()

        // Not enough unique states for meaningful tests
        if (uniqueByHash.size < minUniqueStates) {
            return emptyList()
        }

        // If we have fewer than max, return all
        if (uniqueByHash.size <= maxTestsPerFunction) {
            return uniqueByHash.sortedBy { it.timestamp }
        }

        // Sample evenly across timestamps
        return sampleEvenly(uniqueByHash, maxTestsPerFunction)
    }

    /**
     * Sample N items evenly distributed across the timestamp range.
     */
    private fun sampleEvenly(
        captures: List<FunctionCallCapture>,
        n: Int
    ): List<FunctionCallCapture> {
        val sorted = captures.sortedBy { it.timestamp }
        val step = sorted.size.toDouble() / n

        return (0 until n).map { i ->
            val idx = (i * step).toInt().coerceIn(0, sorted.size - 1)
            sorted[idx]
        }
    }

    /**
     * Get statistics about the captures.
     */
    fun getStatistics(captures: List<FunctionCallCapture>): CaptureStatistics {
        val byFunction = captures.groupBy { it.functionAddress }
        
        val functionStats = byFunction.map { (addr, functionCaptures) ->
            val uniqueInputs = functionCaptures.map { it.inputStateHash }.toSet().size
            FunctionStatistics(
                address = addr,
                totalCalls = functionCaptures.size,
                uniqueInputStates = uniqueInputs,
                selectedTests = minOf(uniqueInputs, maxTestsPerFunction)
            )
        }.sortedByDescending { it.totalCalls }

        return CaptureStatistics(
            totalCaptures = captures.size,
            uniqueFunctions = byFunction.size,
            functions = functionStats
        )
    }
}

/**
 * Statistics about captured function calls.
 */
data class CaptureStatistics(
    val totalCaptures: Int,
    val uniqueFunctions: Int,
    val functions: List<FunctionStatistics>
)

/**
 * Statistics for a single function.
 */
data class FunctionStatistics(
    val address: Int,
    val totalCalls: Int,
    val uniqueInputStates: Int,
    val selectedTests: Int
) {
    override fun toString(): String {
        val addrHex = address.toString(16).uppercase().padStart(4, '0')
        return "0x$addrHex: $totalCalls calls, $uniqueInputStates unique inputs -> $selectedTests tests"
    }
}
