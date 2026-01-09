package com.ivieleague.decompiler6502tokotlin.interpreter

import java.io.File

/**
 * Parses @FRAMES_CONSUMED markers from assembly files.
 *
 * Format: `; @FRAMES_CONSUMED: N` on a line before a label.
 * When the subroutine at that label is called via JSR, it will cause
 * N-1 subsequent NMI frames to be skipped (the subroutine consumes N frames total).
 */
object FrameDebtParser {

    private val MARKER_PATTERN = Regex("""; @FRAMES_CONSUMED:\s*(\d+)""")
    private val LABEL_PATTERN = Regex("""^(\w+):""")

    /**
     * Parse assembly file and extract frame debt map.
     * @param asmFile Path to assembly file
     * @param labelToAddress Map of label names to their addresses
     * @return Map of subroutine address to frames consumed
     */
    fun parse(asmFile: File, labelToAddress: Map<String, Int>): Map<Int, Int> {
        val result = mutableMapOf<Int, Int>()
        var pendingFramesConsumed: Int? = null

        asmFile.forEachLine { line ->
            // Check for @FRAMES_CONSUMED marker
            val markerMatch = MARKER_PATTERN.find(line)
            if (markerMatch != null) {
                pendingFramesConsumed = markerMatch.groupValues[1].toInt()
                return@forEachLine
            }

            // Check for label
            val labelMatch = LABEL_PATTERN.find(line.trim())
            if (labelMatch != null && pendingFramesConsumed != null) {
                val labelName = labelMatch.groupValues[1]
                val address = labelToAddress[labelName]
                if (address != null) {
                    result[address] = pendingFramesConsumed!!
                    println("FrameDebt: $labelName (0x${address.toString(16)}) consumes $pendingFramesConsumed frames")
                } else {
                    println("WARNING: @FRAMES_CONSUMED marker for unknown label: $labelName")
                }
                pendingFramesConsumed = null
            } else if (line.isNotBlank() && !line.trimStart().startsWith(";")) {
                // Non-comment, non-blank line without a label clears pending marker
                pendingFramesConsumed = null
            }
        }

        return result
    }

    /**
     * Parse from assembly text content directly.
     */
    fun parseText(asmText: String, labelToAddress: Map<String, Int>): Map<Int, Int> {
        val result = mutableMapOf<Int, Int>()
        var pendingFramesConsumed: Int? = null

        asmText.lineSequence().forEach { line ->
            // Check for @FRAMES_CONSUMED marker
            val markerMatch = MARKER_PATTERN.find(line)
            if (markerMatch != null) {
                pendingFramesConsumed = markerMatch.groupValues[1].toInt()
                return@forEach
            }

            // Check for label
            val labelMatch = LABEL_PATTERN.find(line.trim())
            if (labelMatch != null && pendingFramesConsumed != null) {
                val labelName = labelMatch.groupValues[1]
                val address = labelToAddress[labelName]
                if (address != null) {
                    result[address] = pendingFramesConsumed!!
                } else {
                    // Label might not be in the map if it's a local label
                }
                pendingFramesConsumed = null
            } else if (line.isNotBlank() && !line.trimStart().startsWith(";")) {
                // Non-comment, non-blank line without a label clears pending marker
                pendingFramesConsumed = null
            }
        }

        return result
    }
}
