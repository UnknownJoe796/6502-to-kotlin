package com.ivieleague.decompiler6502tokotlin

/**
 * Shared utility functions used across multiple passes
 */

/**
 * Parse hex address string like "$C000" to integer
 */
fun parseHexAddr(text: String): Int? {
    val t = text.trim()
    if (!t.startsWith("$")) return null
    val hex = t.drop(1)
    if (hex.isEmpty()) return null
    return hex.toIntOrNull(16)
}

/**
 * Split CSV string while respecting quoted strings
 */
fun splitCsvRespectingQuotes(s: String): List<String> {
    if (s.isEmpty()) return listOf("")
    
    val tokens = mutableListOf<String>()
    val current = StringBuilder()
    var inString = false
    for (ch in s) {
        when (ch) {
            '"' -> {
                inString = !inString
                current.append(ch)
            }
            ',' -> {
                if (inString) current.append(ch) else {
                    tokens += current.toString()
                    current.clear()
                }
            }
            else -> current.append(ch)
        }
    }
    tokens += current.toString()  // Always add the last token, even if empty
    return tokens
}

/**
 * Determines if a hex string represents a zero-page address (1 byte) or absolute address (2 bytes)
 * Based on address value, not just string length for better accuracy
 */
fun isZeroPageAddress(hexValue: Int): Boolean = hexValue in 0x00..0xFF

/**
 * Determines if a hex string represents a zero-page address
 */
fun isZeroPageAddress(hexString: String): Boolean {
    val addr = parseHexAddr(hexString) ?: return false
    return isZeroPageAddress(addr)
}