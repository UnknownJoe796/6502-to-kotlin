package com.ivieleague.decompiler6502tokotlin.testgen

import java.io.File

/**
 * Metadata about a decompiled function, extracted from the generated Kotlin source.
 */
data class DecompiledFunctionMetadata(
    /** Kotlin function name (camelCase) */
    val functionName: String,

    /** Original assembly label name (PascalCase), from the "Decompiled from X" comment */
    val sourceLabel: String?,

    /** Parameter names in order (e.g., ["A", "X", "Y"]) */
    val parameters: List<String>,

    /** Binary address of the function entry point (from AddressLabelMapper) */
    val address: Int?
)

/**
 * Extracts metadata from decompiled Kotlin source files.
 *
 * This parses the generated SMBDecompiled.kt to find:
 * - Function names and signatures
 * - Source assembly labels (from comments)
 * - Binary addresses (via AddressLabelMapper)
 */
class DecompiledFunctionExtractor(
    private val addressMapper: AddressLabelMapper? = null,
    private val romData: ByteArray? = null
) {
    /**
     * Extract all function metadata from a decompiled Kotlin file.
     */
    fun extractFromFile(file: File): List<DecompiledFunctionMetadata> {
        if (!file.exists()) return emptyList()
        return extractFromText(file.readText())
    }

    /**
     * Extract all function metadata from decompiled Kotlin source text.
     */
    fun extractFromText(text: String): List<DecompiledFunctionMetadata> {
        val result = mutableListOf<DecompiledFunctionMetadata>()
        val lines = text.lines()

        var currentSourceLabel: String? = null

        for (i in lines.indices) {
            val line = lines[i].trim()

            // Look for "// Decompiled from X" comments
            if (line.startsWith("// Decompiled from ")) {
                currentSourceLabel = line.removePrefix("// Decompiled from ").trim()
                // Handle "@N" format for unlabeled functions
                if (currentSourceLabel.startsWith("@")) {
                    currentSourceLabel = null
                }
                continue
            }

            // Look for function declarations
            if (line.startsWith("fun ")) {
                val metadata = parseFunctionDeclaration(line, currentSourceLabel)
                if (metadata != null) {
                    result.add(metadata)
                }
                currentSourceLabel = null
            }
        }

        return result
    }

    /**
     * Parse a function declaration line to extract name and parameters.
     */
    private fun parseFunctionDeclaration(line: String, sourceLabel: String?): DecompiledFunctionMetadata? {
        // Match: fun functionName() or fun functionName(params)
        val funPattern = Regex("""^fun ([a-zA-Z_][a-zA-Z0-9_]*)\(([^)]*)\)""")
        val match = funPattern.find(line) ?: return null

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

        // Get address from mapper if available, with optional ROM verification
        val address = if (sourceLabel != null && addressMapper != null) {
            val mapperAddr = addressMapper.getAddress(sourceLabel)
            // If we have ROM data, verify/correct the address
            if (mapperAddr != null && romData != null) {
                AddressLabelMapper.verifyAddressWithRom(sourceLabel, mapperAddr, romData)
            } else {
                mapperAddr
            }
        } else {
            null
        }

        return DecompiledFunctionMetadata(
            functionName = funcName,
            sourceLabel = sourceLabel,
            parameters = params,
            address = address
        )
    }

    /**
     * Get a map of address -> function metadata for all functions with known addresses.
     */
    fun getAddressToFunctionMap(file: File): Map<Int, DecompiledFunctionMetadata> {
        return extractFromFile(file)
            .filter { it.address != null }
            .associateBy { it.address!! }
    }

    /**
     * Get a map of function name -> metadata.
     */
    fun getFunctionNameMap(file: File): Map<String, DecompiledFunctionMetadata> {
        return extractFromFile(file).associateBy { it.functionName }
    }

    companion object {
        /**
         * Create an extractor with address mapping from an assembly file.
         */
        fun withAddressMapper(asmFile: File): DecompiledFunctionExtractor {
            val mapper = if (asmFile.exists()) {
                AddressLabelMapper.fromAssemblyFile(asmFile)
            } else {
                null
            }
            return DecompiledFunctionExtractor(mapper)
        }

        /**
         * Create an extractor with ROM-verified address mapping.
         * This uses ROM signature verification to correct mapper inaccuracies.
         *
         * @param asmFile The assembly file for initial address calculation
         * @param romData The ROM data (including iNES header) for verification
         */
        fun withRomVerifiedMapper(asmFile: File, romData: ByteArray): DecompiledFunctionExtractor {
            val mapper = if (asmFile.exists()) {
                AddressLabelMapper.fromAssemblyFile(asmFile)
            } else {
                null
            }
            return DecompiledFunctionExtractor(mapper, romData)
        }
    }
}
