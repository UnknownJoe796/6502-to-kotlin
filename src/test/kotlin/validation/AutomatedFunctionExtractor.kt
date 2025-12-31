package com.ivieleague.decompiler6502tokotlin.validation

import java.io.File

/**
 * Automatically extracts functions from assembly and decompiled Kotlin for validation.
 */
object AutomatedFunctionExtractor {

    data class FunctionPair(
        val name: String,
        val assemblyCode: String,
        val kotlinCode: String,
        val signature: String
    )

    /**
     * Extract a function from assembly file
     */
    fun extractAssemblyFunction(file: File, functionLabel: String): String? {
        val lines = file.readLines()
        val startPattern = Regex("^${Regex.escape(functionLabel)}:.*")

        var startIdx = lines.indexOfFirst { startPattern.containsMatchIn(it) }
        if (startIdx == -1) return null

        val code = mutableListOf<String>()
        code.add("${functionLabel}:")

        // Extract until RTS or next function label
        for (i in (startIdx + 1) until lines.size) {
            val line = lines[i]

            // Extract instruction part (before comment)
            val instruction = if (line.contains(";")) {
                line.substring(0, line.indexOf(";")).trim()
            } else {
                line.trim()
            }

            // Skip empty lines
            if (instruction.isEmpty()) continue

            // Skip DATA: lines and separator lines
            if (instruction.startsWith("DATA:")) continue
            if (instruction.startsWith("-----")) continue

            // Stop at next function label
            if (Regex("^[A-Z][A-Za-z0-9_]*:").containsMatchIn(instruction)) break

            // Add instruction
            code.add(instruction)

            // Stop after RTS
            if (instruction.startsWith("RTS") || instruction.endsWith("RTS")) {
                break
            }
        }

        return code.joinToString("\n")
    }

    /**
     * Find decompiled Kotlin function by looking for assembly label in comments
     */
    fun findKotlinFunction(file: File, assemblyLabel: String): Pair<String, String>? {
        val content = file.readText()
        val lines = content.lines()

        // Look for comment with assembly label
        val commentPattern = Regex("^\\s*//> ${Regex.escape(assemblyLabel)}:")

        var commentIdx = lines.indexOfFirst { commentPattern.containsMatchIn(it) }
        if (commentIdx == -1) return null

        // Search backwards for function signature
        var funcIdx = commentIdx
        while (funcIdx >= 0) {
            val line = lines[funcIdx]
            if (line.trim().startsWith("fun ")) {
                // Found function signature
                val signature = line.trim()

                // Extract function body
                val body = extractFunctionBody(lines, funcIdx)
                return Pair(signature, body)
            }
            funcIdx--

            // Don't search too far back
            if (commentIdx - funcIdx > 20) break
        }

        return null
    }

    /**
     * Extract function body from function signature line
     */
    private fun extractFunctionBody(lines: List<String>, signatureIdx: Int): String {
        val body = mutableListOf<String>()
        var braceCount = 0
        var started = false

        for (i in signatureIdx until lines.size) {
            val line = lines[i]

            // Track braces
            braceCount += line.count { it == '{' }
            braceCount -= line.count { it == '}' }

            if (braceCount > 0) started = true

            body.add(line)

            // End of function
            if (started && braceCount == 0) break
        }

        return body.joinToString("\n")
    }

    /**
     * Extract multiple function pairs automatically
     */
    fun extractFunctionPairs(
        assemblyFile: File,
        kotlinFile: File,
        functionLabels: List<String>
    ): List<FunctionPair> {
        val pairs = mutableListOf<FunctionPair>()

        for (label in functionLabels) {
            val assembly = extractAssemblyFunction(assemblyFile, label)
            val kotlin = findKotlinFunction(kotlinFile, label)

            if (assembly != null && kotlin != null) {
                pairs.add(FunctionPair(
                    name = label,
                    assemblyCode = assembly,
                    kotlinCode = kotlin.second,
                    signature = kotlin.first
                ))
                println("✓ Extracted $label")
            } else {
                println("✗ Failed to extract $label (asm=${assembly != null}, kt=${kotlin != null})")
            }
        }

        return pairs
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val assemblyFile = File("outputs/smbdism-annotated.asm")
        val kotlinFile = File("outputs/smb-decompiled.kt")

        // Test with known functions
        val testFunctions = listOf(
            "ResetScreenTimer",
            "InitPlatScrl",
            "SetVRAMCtrl",
            "ResetTitle",
            "ChkPauseTimer"
        )

        println("Extracting functions...")
        val pairs = extractFunctionPairs(assemblyFile, kotlinFile, testFunctions)

        println("\nExtracted ${pairs.size} function pairs:")
        pairs.forEach { pair ->
            println("\n=== ${pair.name} ===")
            println("Signature: ${pair.signature}")
            println("\nAssembly (${pair.assemblyCode.lines().size} lines):")
            println(pair.assemblyCode.lines().take(5).joinToString("\n"))
            println("\nKotlin (${pair.kotlinCode.lines().size} lines):")
            println(pair.kotlinCode.lines().take(5).joinToString("\n"))
        }
    }
}
