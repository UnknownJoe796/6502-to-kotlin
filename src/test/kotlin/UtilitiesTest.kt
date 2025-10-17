package com.ivieleague.decompiler6502tokotlin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class UtilitiesTest {
    
    @Test
    fun parseHexAddrValid() {
        assertEquals(49152, parseHexAddr("${'$'}C000"))  // 0xC000
        assertEquals(16, parseHexAddr("${'$'}10"))       // 0x10
        assertEquals(255, parseHexAddr("${'$'}FF"))      // 0xFF
        assertEquals(0, parseHexAddr("${'$'}0"))         // 0x0
        assertEquals(4660, parseHexAddr("${'$'}1234"))   // 0x1234
    }
    
    @Test
    fun parseHexAddrInvalid() {
        assertNull(parseHexAddr("C000"))  // No $ prefix
        assertNull(parseHexAddr("${'$'}"))     // Empty hex
        assertNull(parseHexAddr("${'$'}GGGG")) // Invalid hex
        assertNull(parseHexAddr(""))      // Empty string
        assertNull(parseHexAddr("123"))   // No $ prefix
    }
    
    @Test
    fun parseHexAddrWhitespace() {
        assertEquals(49152, parseHexAddr("  ${'$'}C000  "))  // 0xC000
        assertEquals(16, parseHexAddr("\t${'$'}10\n"))       // 0x10
    }
    
    @Test
    fun splitCsvRespectingQuotesBasic() {
        val result = splitCsvRespectingQuotes("a,b,c")
        assertEquals(listOf("a", "b", "c"), result)
    }
    
    @Test
    fun splitCsvRespectingQuotesWithQuotes() {
        val result = splitCsvRespectingQuotes("\"hello, world\",b,\"test\"")
        assertEquals(listOf("\"hello, world\"", "b", "\"test\""), result)
    }
    
    @Test
    fun splitCsvRespectingQuotesEmpty() {
        assertEquals(listOf(""), splitCsvRespectingQuotes(""))
        assertEquals(listOf("", "", ""), splitCsvRespectingQuotes(",,"))
    }
    
    @Test
    fun splitCsvRespectingQuotesComplex() {
        val result = splitCsvRespectingQuotes("${'$'}10, \"test,string\", ${'$'}20, \"another\"")
        assertEquals(listOf("${'$'}10", " \"test,string\"", " ${'$'}20", " \"another\""), result)
    }
    
    @Test
    fun isZeroPageAddressInt() {
        assertTrue(isZeroPageAddress(0))      // 0x00
        assertTrue(isZeroPageAddress(16))     // 0x10
        assertTrue(isZeroPageAddress(255))    // 0xFF
        assertFalse(isZeroPageAddress(256))   // 0x100
        assertFalse(isZeroPageAddress(49152)) // 0xC000
        assertFalse(isZeroPageAddress(-1))
    }
    
    @Test
    fun isZeroPageAddressString() {
        assertTrue(isZeroPageAddress("${'$'}00"))
        assertTrue(isZeroPageAddress("${'$'}10"))
        assertTrue(isZeroPageAddress("${'$'}FF"))
        assertFalse(isZeroPageAddress("${'$'}100"))
        assertFalse(isZeroPageAddress("${'$'}C000"))
        assertFalse(isZeroPageAddress("${'$'}1000"))
        assertFalse(isZeroPageAddress("invalid"))
    }
}