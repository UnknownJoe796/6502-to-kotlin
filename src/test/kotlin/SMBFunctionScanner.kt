package com.ivieleague.decompiler6502tokotlin.hand

import com.ivieleague.decompiler6502tokotlin.hand.stages.parseAssembly
import java.io.File

/**
 * Scans the SMB disassembly to identify all functions and analyze their properties.
 */
object SMBFunctionScanner {

    data class FunctionInfo(
        val name: String,
        val startLine: Int,
        val endLine: Int,
        val hasReturn: Boolean,
        val callsMade: List<String>,
        val isJumpEngine: Boolean,
        val isLeaf: Boolean,
        val linesOfCode: Int
    ) {
        override fun toString(): String {
            val calls = if (callsMade.isEmpty()) "none" else callsMade.joinToString(", ")
            return "$name (lines $startLine-$endLine, ${linesOfCode} LOC): " +
                   "calls=[$calls], leaf=$isLeaf, return=$hasReturn"
        }
    }

    /**
     * Scans the SMB disassembly file and extracts all function information.
     */
    fun scanAllFunctions(): Map<String, FunctionInfo> {
        val asmFile = File("smbdism.asm")
        require(asmFile.exists()) { "smbdism.asm not found" }

        val lines = asmFile.readLines()
        val functions = mutableMapOf<String, FunctionInfo>()
        val labelToLine = mutableMapOf<String, Int>()
        val jsrTargets = mutableSetOf<String>()

        // First pass: Find all labels and JSR targets
        lines.forEachIndexed { index, line ->
            val trimmed = line.trim()

            // Find labels (lines starting with identifier followed by :)
            if (trimmed.matches(Regex("^[A-Za-z_][A-Za-z0-9_]*:"))) {
                val label = trimmed.substring(0, trimmed.length - 1)
                labelToLine[label] = index
            }

            // Find JSR targets
            if (trimmed.contains("jsr ")) {
                val parts = trimmed.split(Regex("\\s+"))
                val jsrIndex = parts.indexOfFirst { it == "jsr" }
                if (jsrIndex >= 0 && jsrIndex + 1 < parts.size) {
                    val target = parts[jsrIndex + 1].trim()
                    jsrTargets.add(target)
                }
            }
        }

        // Second pass: Analyze each label to determine if it's a function
        labelToLine.forEach { (label, startLine) ->
            val info = analyzeFunctionAt(label, startLine, lines, labelToLine)
            if (info != null) {
                functions[label] = info
            }
        }

        return functions
    }

    /**
     * Analyzes a potential function starting at the given label.
     */
    private fun analyzeFunctionAt(
        label: String,
        startLine: Int,
        lines: List<String>,
        labelToLine: Map<String, Int>
    ): FunctionInfo? {
        var currentLine = startLine + 1
        val callsMade = mutableListOf<String>()
        var hasReturn = false
        var endLine = startLine
        var linesOfCode = 0

        // Scan forward until we hit RTS, RTI, JMP (endless loop), or another label
        while (currentLine < lines.size) {
            val line = lines[currentLine].trim()

            // Skip empty lines and comments
            if (line.isEmpty() || line.startsWith(";")) {
                currentLine++
                continue
            }

            // Check if we hit another label (function boundary)
            if (line.matches(Regex("^[A-Za-z_][A-Za-z0-9_]*:"))) {
                break
            }

            // Count actual code lines
            if (line.matches(Regex("^\\s*[a-z]{3}\\s+.*")) ||
                line.matches(Regex("^\\s*[a-z]{3}\\s*$"))) {
                linesOfCode++
            }

            endLine = currentLine

            // Check for RTS (return from subroutine)
            if (line.contains("rts")) {
                hasReturn = true
                break
            }

            // Check for RTI (return from interrupt)
            if (line.contains("rti")) {
                hasReturn = true
                break
            }

            // Check for JSR (function call)
            if (line.contains("jsr ")) {
                val parts = line.split(Regex("\\s+"))
                val jsrIndex = parts.indexOfFirst { it == "jsr" }
                if (jsrIndex >= 0 && jsrIndex + 1 < parts.size) {
                    val target = parts[jsrIndex + 1].trim()
                    callsMade.add(target)
                }
            }

            // Check for unconditional JMP to self (endless loop - treat as end)
            if (line.contains("jmp $label")) {
                break
            }

            currentLine++
        }

        // Only consider it a function if:
        // 1. It has code (not just a label for data)
        // 2. It has a return OR it's short enough to be inline code
        if (linesOfCode == 0) {
            return null  // Probably a data label
        }

        val isJumpEngine = label == "JumpEngine"
        val isLeaf = callsMade.isEmpty()

        return FunctionInfo(
            name = label,
            startLine = startLine,
            endLine = endLine,
            hasReturn = hasReturn,
            callsMade = callsMade,
            isJumpEngine = isJumpEngine,
            isLeaf = isLeaf,
            linesOfCode = linesOfCode
        )
    }

    /**
     * Filters functions to only those that are testable.
     */
    fun getTestableFunctions(allFunctions: Map<String, FunctionInfo>): Map<String, FunctionInfo> {
        return allFunctions.filter { (_, info) ->
            // Skip JumpEngine (uses computed jumps)
            !info.isJumpEngine &&
            // Must have a return statement
            info.hasReturn &&
            // Prefer leaf functions (but we can handle non-leaf with mocking)
            info.linesOfCode > 0
        }
    }

    /**
     * Gets only leaf functions (functions that don't call other functions).
     */
    fun getLeafFunctions(allFunctions: Map<String, FunctionInfo>): Map<String, FunctionInfo> {
        return allFunctions.filter { (_, info) ->
            !info.isJumpEngine &&
            info.hasReturn &&
            info.isLeaf &&
            info.linesOfCode > 0
        }
    }

    /**
     * Prints a comprehensive report of all functions.
     */
    fun printReport() {
        val allFunctions = scanAllFunctions()
        val testable = getTestableFunctions(allFunctions)
        val leafFunctions = getLeafFunctions(allFunctions)

        println("=" .repeat(80))
        println("SMB Function Analysis Report")
        println("=" .repeat(80))

        println("\nTotal labels found: ${allFunctions.size}")
        println("Testable functions: ${testable.size}")
        println("Leaf functions: ${leafFunctions.size}")
        println("Non-leaf functions: ${testable.size - leafFunctions.size}")

        println("\n" + "-".repeat(80))
        println("Leaf Functions (No JSR calls):")
        println("-".repeat(80))
        leafFunctions.values.sortedBy { it.startLine }.forEach { info ->
            println(info)
        }

        println("\n" + "-".repeat(80))
        println("Non-Leaf Functions (Make JSR calls):")
        println("-".repeat(80))
        testable.values.filter { !it.isLeaf }.sortedBy { it.startLine }.forEach { info ->
            println(info)
        }

        println("\n" + "-".repeat(80))
        println("Skipped Functions:")
        println("-".repeat(80))
        allFunctions.values.filter { it.isJumpEngine || !it.hasReturn }
            .sortedBy { it.startLine }.forEach { info ->
            val reason = when {
                info.isJumpEngine -> "JumpEngine"
                !info.hasReturn -> "No RTS/RTI"
                else -> "Unknown"
            }
            println("${info.name}: $reason")
        }

        println("\n" + "=".repeat(80))
    }
}

fun main() {
    SMBFunctionScanner.printReport()
}
