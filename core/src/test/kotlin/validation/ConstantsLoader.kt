package com.ivieleague.decompiler6502tokotlin.validation

import java.io.File

/**
 * Loads constants from the generated constants file
 */
object ConstantsLoader {

    /**
     * Parse constants file and return map of name -> address
     */
    fun loadConstants(file: File): Map<String, Int> {
        val constants = mutableMapOf<String, Int>()

        file.readLines().forEach { line ->
            // Match: const val NAME = 0xADDR (name can include digits)
            val match = Regex("""const val ([A-Z_0-9]+) = (0x[0-9A-Fa-f]+)""").find(line)
            if (match != null) {
                val name = match.groupValues[1]
                val addr = match.groupValues[2].substring(2).toInt(16)
                constants[name] = addr
            }
        }

        return constants
    }

    /**
     * Replace constant names in assembly with their addresses
     */
    fun substituteConstants(assemblyCode: String, constants: Map<String, Int>): String {
        var result = assemblyCode

        // Sort constants by name length (longest first) to avoid partial replacements
        val sortedConstants = constants.entries.sortedByDescending { it.key.length }

        // Replace each constant with its address (case-insensitive)
        sortedConstants.forEach { (name, addr) ->
            // Match constant name as a whole word (case-insensitive)
            val pattern = Regex("""\b${Regex.escape(name)}\b""", RegexOption.IGNORE_CASE)
            val replacement = "${'$'}${addr.toString(16).uppercase().padStart(4, '0')}"

            // Use replaceAll with literal replacement
            result = pattern.replace(result) { replacement }
        }

        return result
    }

    /**
     * Load constants and substitute them in assembly code
     */
    fun preprocessAssembly(assemblyCode: String, constantsFile: File): String {
        val constants = loadConstants(constantsFile)
        return substituteConstants(assemblyCode, constants)
    }
}
