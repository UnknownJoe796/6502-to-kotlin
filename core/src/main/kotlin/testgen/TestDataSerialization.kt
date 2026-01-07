package com.ivieleague.decompiler6502tokotlin.testgen

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * JSON serialization for captured test data.
 */
object TestDataSerialization {
    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    /**
     * Container for all captured data from a TAS run.
     */
    @Serializable
    data class CapturedTestData(
        val tasName: String,
        val captureTimestamp: String,
        val totalFrames: Int,
        val totalCaptures: Int,
        val functions: Map<String, FunctionTestData>  // hex address string -> data
    )

    /**
     * Test data for a single function.
     */
    @Serializable
    data class FunctionTestData(
        val address: Int,
        val addressHex: String,
        val functionName: String?,  // From symbol table if available
        val totalCalls: Int,
        val uniqueInputStates: Int,
        val testCases: List<FunctionCallCapture>
    )

    /**
     * Save captured test data to a JSON file.
     */
    fun save(data: CapturedTestData, file: File) {
        file.parentFile?.mkdirs()
        file.writeText(json.encodeToString(data))
    }

    /**
     * Load captured test data from a JSON file.
     */
    fun load(file: File): CapturedTestData {
        return json.decodeFromString(file.readText())
    }

    /**
     * Create CapturedTestData from captures and selected test cases.
     */
    fun createTestData(
        tasName: String,
        totalFrames: Int,
        allCaptures: List<FunctionCallCapture>,
        selectedByFunction: Map<Int, List<FunctionCallCapture>>,
        functionNames: Map<Int, String> = emptyMap()
    ): CapturedTestData {
        val capturesByFunction = allCaptures.groupBy { it.functionAddress }

        val functions = selectedByFunction.mapKeys { (addr, _) ->
            "0x${addr.toString(16).uppercase().padStart(4, '0')}"
        }.mapValues { (hexAddr, testCases) ->
            val addr = testCases.first().functionAddress
            val allCalls = capturesByFunction[addr] ?: emptyList()
            val uniqueInputs = allCalls.map { it.inputStateHash }.toSet().size

            FunctionTestData(
                address = addr,
                addressHex = hexAddr,
                functionName = functionNames[addr],
                totalCalls = allCalls.size,
                uniqueInputStates = uniqueInputs,
                testCases = testCases
            )
        }

        return CapturedTestData(
            tasName = tasName,
            captureTimestamp = java.time.Instant.now().toString(),
            totalFrames = totalFrames,
            totalCaptures = allCaptures.size,
            functions = functions
        )
    }
}
