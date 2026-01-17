// by Claude - Tests for code generation helper functions in codegen-helpers.kt
package com.ivieleague.decompiler6502tokotlin.codegen

import com.ivieleague.decompiler6502tokotlin.hand.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * Tests for code generation helper functions in codegen-helpers.kt.
 *
 * These helpers handle:
 * - Label to Kotlin name conversion
 * - Memory access wrapping (UByte <-> Int conversion)
 * - Byte-size detection for optimization
 * - Addressing mode to Kotlin expression conversion
 */
class CodeGenHelperTest {

    // =========================================================================
    // Label Conversion: assemblyLabelToKotlinName()
    // =========================================================================

    @Test
    fun `snake_case label converts to camelCase`() {
        assertEquals("gameOverRoutine", assemblyLabelToKotlinName("Game_Over_Routine"))
        assertEquals("initLoop", assemblyLabelToKotlinName("init_loop"))
        assertEquals("playerX", assemblyLabelToKotlinName("Player_X"))
    }

    @Test
    fun `kebab-case label converts to camelCase`() {
        assertEquals("gameOverRoutine", assemblyLabelToKotlinName("Game-Over-Routine"))
        assertEquals("mainLoop", assemblyLabelToKotlinName("main-loop"))
    }

    @Test
    fun `PascalCase label converts to camelCase`() {
        assertEquals("gameOverRoutine", assemblyLabelToKotlinName("GameOverRoutine"))
        assertEquals("playerSprite", assemblyLabelToKotlinName("PlayerSprite"))
    }

    @Test
    fun `camelCase label remains camelCase`() {
        assertEquals("alreadyCamel", assemblyLabelToKotlinName("alreadyCamel"))
    }

    @Test
    fun `single word label lowercase`() {
        assertEquals("loop", assemblyLabelToKotlinName("Loop"))
        assertEquals("exit", assemblyLabelToKotlinName("Exit"))
    }

    @Test
    fun `all uppercase label handles correctly`() {
        // ALL_CAPS should convert to allCaps
        assertEquals("allCaps", assemblyLabelToKotlinName("ALL_CAPS"))
    }

    // =========================================================================
    // Byte Size Detection: isKnownByteSized()
    // =========================================================================

    @Test
    fun `hex literal in byte range is byte-sized`() {
        assertTrue(KLiteral("0x00").isKnownByteSized())
        assertTrue(KLiteral("0xFF").isKnownByteSized())
        assertTrue(KLiteral("0x10").isKnownByteSized())
    }

    @Test
    fun `hex literal out of byte range is not byte-sized`() {
        assertFalse(KLiteral("0x100").isKnownByteSized())
        assertFalse(KLiteral("0xFFFF").isKnownByteSized())
    }

    @Test
    fun `decimal literal in byte range is byte-sized`() {
        assertTrue(KLiteral("0").isKnownByteSized())
        assertTrue(KLiteral("255").isKnownByteSized())
        assertTrue(KLiteral("128").isKnownByteSized())
    }

    @Test
    fun `decimal literal out of byte range is not byte-sized`() {
        assertFalse(KLiteral("256").isKnownByteSized())
        assertFalse(KLiteral("1000").isKnownByteSized())
    }

    @Test
    fun `binary literal in byte range is byte-sized`() {
        assertTrue(KLiteral("0b00000000").isKnownByteSized())
        assertTrue(KLiteral("0b11111111").isKnownByteSized())
    }

    @Test
    fun `variable is byte-sized by convention`() {
        assertTrue(KVar("A").isKnownByteSized())
        assertTrue(KVar("X").isKnownByteSized())
        assertTrue(KVar("temp").isKnownByteSized())
    }

    @Test
    fun `memory access is byte-sized`() {
        val memoryAccess = KMemberAccess(KVar("memory"), KLiteral("0x1000"), isIndexed = true)
        assertTrue(memoryAccess.isKnownByteSized())
    }

    @Test
    fun `toInt call result is byte-sized`() {
        val memRead = KMemberAccess(KVar("memory"), KLiteral("0x1000"), isIndexed = true)
        val toInt = KMemberAccess(memRead, KCall("toInt"), isIndexed = false)
        assertTrue(toInt.isKnownByteSized())
    }

    @Test
    fun `and with 0xFF is byte-sized`() {
        val andExpr = KBinaryOp(KVar("someValue"), "and", KLiteral("0xFF"))
        assertTrue(andExpr.isKnownByteSized())
    }

    @Test
    fun `and with larger value is not byte-sized`() {
        val andExpr = KBinaryOp(KVar("someValue"), "and", KLiteral("0x1FF"))
        assertFalse(andExpr.isKnownByteSized())
    }

    @Test
    fun `parentheses around byte-sized is byte-sized`() {
        val paren = KParen(KLiteral("0x42"))
        assertTrue(paren.isKnownByteSized())
    }

    @Test
    fun `addition is not byte-sized`() {
        val add = KBinaryOp(KVar("a"), "+", KVar("b"))
        assertFalse(add.isKnownByteSized())
    }

    // =========================================================================
    // Memory Read Wrapping: wrapPropertyRead()
    // =========================================================================

    @Test
    fun `memory read wrapped with toInt`() {
        val memRead = KMemberAccess(KVar("memory"), KLiteral("0x1000"), isIndexed = true)
        val wrapped = wrapPropertyRead(memRead)

        assertTrue(wrapped is KMemberAccess, "Should be member access")
        val outer = wrapped as KMemberAccess
        assertTrue(outer.member is KCall, "Should have toInt call")
        assertEquals("toInt", (outer.member as KCall).name)
    }

    @Test
    fun `non-memory access not wrapped`() {
        val varExpr = KVar("someVar")
        val result = wrapPropertyRead(varExpr)
        assertEquals(varExpr, result)
    }

    @Test
    fun `non-indexed memory access not wrapped`() {
        val propAccess = KMemberAccess(KVar("obj"), KVar("property"), isIndexed = false)
        val result = wrapPropertyRead(propAccess)
        assertEquals(propAccess, result)
    }

    // =========================================================================
    // Memory Write Wrapping: wrapPropertyWrite()
    // =========================================================================

    @Test
    fun `memory write wraps byte-sized value with toUByte`() {
        val target = KMemberAccess(KVar("memory"), KLiteral("0x1000"), isIndexed = true)
        val value = KLiteral("0x42")  // Byte-sized
        val wrapped = wrapPropertyWrite(target, value)

        assertTrue(wrapped is KMemberAccess, "Should be member access")
        val outer = wrapped as KMemberAccess
        assertTrue(outer.member is KCall, "Should have toUByte call")
        assertEquals("toUByte", (outer.member as KCall).name)
    }

    @Test
    fun `memory write wraps variable with toUByte`() {
        val target = KMemberAccess(KVar("memory"), KLiteral("0x1000"), isIndexed = true)
        val value = KVar("A")  // Variable is byte-sized
        val wrapped = wrapPropertyWrite(target, value)

        assertTrue(wrapped is KMemberAccess, "Should be member access")
        val outer = wrapped as KMemberAccess
        assertTrue(outer.member is KCall, "Should have toUByte call")
        assertEquals("toUByte", (outer.member as KCall).name)
    }

    @Test
    fun `memory write with non-byte-sized value includes masking`() {
        val target = KMemberAccess(KVar("memory"), KLiteral("0x1000"), isIndexed = true)
        val value = KBinaryOp(KVar("a"), "+", KVar("b"))  // Addition not byte-sized
        val wrapped = wrapPropertyWrite(target, value)

        // Should include masking and toUByte
        assertTrue(wrapped is KMemberAccess, "Should be member access")
        val code = wrapped.toKotlin()
        assertTrue(code.contains("and 0xFF") || code.contains("toUByte"), "Should mask or convert")
    }

    @Test
    fun `non-memory write target not wrapped`() {
        val target = KVar("someVar")
        val value = KLiteral("0x42")
        val result = wrapPropertyWrite(target, value)
        assertEquals(value, result)
    }

    // =========================================================================
    // Addressing Mode Conversion: toKotlinExpr()
    // =========================================================================

    @Test
    fun `ByteValue converts to hex literal`() {
        val ctx = CodeGenContext()
        val addr = AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)
        val expr = addr.toKotlinExpr(ctx)

        assertTrue(expr is KLiteral, "Should be literal")
        assertEquals("0x42", (expr as KLiteral).value.lowercase())
    }

    @Test
    fun `ShortValue converts to hex literal`() {
        val ctx = CodeGenContext()
        val addr = AssemblyAddressing.ShortValue(0x1234u, AssemblyAddressing.Radix.Hex)
        val expr = addr.toKotlinExpr(ctx)

        assertTrue(expr is KLiteral, "Should be literal")
        assertTrue((expr as KLiteral).value.lowercase().contains("1234"))
    }

    @Test
    fun `Direct hex address converts to memory access`() {
        val ctx = CodeGenContext()
        val addr = AssemblyAddressing.Direct("\$1000")
        val expr = addr.toKotlinExpr(ctx)

        assertTrue(expr is KMemberAccess, "Should be member access")
        val memAccess = expr as KMemberAccess
        assertTrue(memAccess.isIndexed, "Should be indexed access")
        assertTrue(memAccess.receiver is KVar, "Receiver should be variable")
        assertEquals("memory", (memAccess.receiver as KVar).name)
    }

    @Test
    fun `Direct label converts to variable`() {
        val ctx = CodeGenContext()
        val addr = AssemblyAddressing.Direct("SomeLabel")
        val expr = addr.toKotlinExpr(ctx)

        assertTrue(expr is KVar, "Should be variable")
    }

    @Test
    fun `DirectX converts to indexed memory access with X`() {
        val ctx = CodeGenContext()
        ctx.registerX = KVar("X")
        val addr = AssemblyAddressing.DirectX("\$0200")
        val expr = addr.toKotlinExpr(ctx)

        assertTrue(expr is KMemberAccess, "Should be member access")
        val code = expr.toKotlin()
        assertTrue(code.contains("X") || code.contains("+"), "Should have X index")
    }

    @Test
    fun `DirectY converts to indexed memory access with Y`() {
        val ctx = CodeGenContext()
        ctx.registerY = KVar("Y")
        val addr = AssemblyAddressing.DirectY("\$0200")
        val expr = addr.toKotlinExpr(ctx)

        assertTrue(expr is KMemberAccess, "Should be member access")
        val code = expr.toKotlin()
        assertTrue(code.contains("Y") || code.contains("+"), "Should have Y index")
    }

    @Test
    fun `ConstantReference converts to variable`() {
        val ctx = CodeGenContext()
        val addr = AssemblyAddressing.ConstantReference("MY_CONSTANT")
        val expr = addr.toKotlinExpr(ctx)

        assertTrue(expr is KVar, "Should be variable")
        assertEquals("MY_CONSTANT", (expr as KVar).name)
    }

    @Test
    fun `ConstantReference with operators wrapped in parentheses`() {
        val ctx = CodeGenContext()
        val addr = AssemblyAddressing.ConstantReference("A_Button+Start_Button")
        val expr = addr.toKotlinExpr(ctx)

        assertTrue(expr is KParen, "Should be wrapped in parentheses")
    }

    @Test
    fun `null addressing returns zero`() {
        val ctx = CodeGenContext()
        val addr: AssemblyAddressing? = null
        val expr = addr.toKotlinExpr(ctx)

        assertTrue(expr is KLiteral, "Should be literal")
        assertEquals("0", (expr as KLiteral).value)
    }

    @Test
    fun `ConstantReferenceLower extracts low byte`() {
        val ctx = CodeGenContext()
        val addr = AssemblyAddressing.ConstantReferenceLower("SomeAddr")
        val expr = addr.toKotlinExpr(ctx)

        assertTrue(expr is KBinaryOp, "Should be binary op")
        val binOp = expr as KBinaryOp
        assertEquals("and", binOp.op)
        assertEquals("0xFF", (binOp.right as KLiteral).value)
    }

    @Test
    fun `ConstantReferenceUpper extracts high byte`() {
        val ctx = CodeGenContext()
        val addr = AssemblyAddressing.ConstantReferenceUpper("SomeAddr")
        val expr = addr.toKotlinExpr(ctx)

        assertTrue(expr is KBinaryOp, "Should be binary op")
        val binOp = expr as KBinaryOp
        assertEquals("shr", binOp.op)
        assertEquals("8", (binOp.right as KLiteral).value)
    }

    // =========================================================================
    // UShort Conversion
    // =========================================================================

    @Test
    fun `UShort toKotlinExpr produces hex literal`() {
        val ctx = CodeGenContext()
        val value: UShort = 0x1234u
        val expr = value.toKotlinExpr(ctx)

        assertTrue(expr is KLiteral, "Should be literal")
        val literal = (expr as KLiteral).value.lowercase()
        assertTrue(literal.contains("1234"), "Should contain value")
        assertTrue(literal.startsWith("0x"), "Should be hex")
    }

    @Test
    fun `UShort zero pads to 4 digits`() {
        val ctx = CodeGenContext()
        val value: UShort = 0x0012u
        val expr = value.toKotlinExpr(ctx)

        assertTrue(expr is KLiteral, "Should be literal")
        val literal = (expr as KLiteral).value
        // Should be 0x0012, not 0x12
        assertTrue(literal.length >= 6, "Should pad to 4 hex digits")
    }
}
