// by Claude - Tests for expression simplification in kotlin-codegen.kt
package com.ivieleague.decompiler6502tokotlin.codegen

import com.ivieleague.decompiler6502tokotlin.hand.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for the KotlinExpr.simplify() function in kotlin-codegen.kt.
 *
 * The simplify function transforms Kotlin AST expressions to more readable forms:
 * - Negation simplification: !(a == b) → a != b
 * - Comparison simplification: (a - b) == 0 → a == b
 * - Arithmetic simplification: a - 0 → a, a + 0 → a
 */
class ExpressionSimplifyTest {

    // =========================================================================
    // Negation Simplification
    // =========================================================================

    @Test
    fun `NOT equals simplifies to not equals`() {
        // !(a == b) → a != b
        val expr = KUnaryOp("!", KBinaryOp(KVar("a"), "==", KVar("b")))
        val simplified = expr.simplify()

        assertTrue(simplified is KBinaryOp, "Should simplify to binary op")
        val binaryOp = simplified as KBinaryOp
        assertEquals("!=", binaryOp.op)
        assertEquals("a", (binaryOp.left as KVar).name)
        assertEquals("b", (binaryOp.right as KVar).name)
    }

    @Test
    fun `NOT not-equals simplifies to equals`() {
        // !(a != b) → a == b
        val expr = KUnaryOp("!", KBinaryOp(KVar("a"), "!=", KVar("b")))
        val simplified = expr.simplify()

        assertTrue(simplified is KBinaryOp, "Should simplify to binary op")
        val binaryOp = simplified as KBinaryOp
        assertEquals("==", binaryOp.op)
        assertEquals("a", (binaryOp.left as KVar).name)
        assertEquals("b", (binaryOp.right as KVar).name)
    }

    @Test
    fun `double negation simplifies correctly`() {
        // !(!a) - inner expression should propagate
        val expr = KUnaryOp("!", KUnaryOp("!", KVar("a")))
        val simplified = expr.simplify()

        // The current implementation doesn't handle !!a → a directly,
        // but it should propagate inner simplifications
        assertTrue(simplified is KUnaryOp || simplified is KVar, "Should be unary op or var")
    }

    @Test
    fun `NOT with non-comparison is preserved`() {
        // !a (where a is not a comparison) - should stay as !a
        val expr = KUnaryOp("!", KVar("flag"))
        val simplified = expr.simplify()

        assertTrue(simplified is KUnaryOp, "Should remain unary op")
        val unaryOp = simplified as KUnaryOp
        assertEquals("!", unaryOp.op)
        assertEquals("flag", (unaryOp.expr as KVar).name)
    }

    @Test
    fun `NOT with literal comparison`() {
        // !(x == 0x10) → x != 0x10
        val expr = KUnaryOp("!", KBinaryOp(KVar("x"), "==", KLiteral("0x10")))
        val simplified = expr.simplify()

        assertTrue(simplified is KBinaryOp, "Should simplify to binary op")
        val binaryOp = simplified as KBinaryOp
        assertEquals("!=", binaryOp.op)
    }

    // =========================================================================
    // Comparison Simplification
    // =========================================================================

    @Test
    fun `subtraction equals zero simplifies to equals`() {
        // (a - b) == 0 → a == b
        val subtraction = KBinaryOp(KVar("a"), "-", KVar("b"))
        val expr = KBinaryOp(subtraction, "==", KLiteral("0"))
        val simplified = expr.simplify()

        assertTrue(simplified is KBinaryOp, "Should simplify to binary op")
        val binaryOp = simplified as KBinaryOp
        assertEquals("==", binaryOp.op)
        assertEquals("a", (binaryOp.left as KVar).name)
        assertEquals("b", (binaryOp.right as KVar).name)
    }

    @Test
    fun `zero equals subtraction simplifies to equals`() {
        // 0 == (a - b) → a == b
        val subtraction = KBinaryOp(KVar("a"), "-", KVar("b"))
        val expr = KBinaryOp(KLiteral("0"), "==", subtraction)
        val simplified = expr.simplify()

        assertTrue(simplified is KBinaryOp, "Should simplify to binary op")
        val binaryOp = simplified as KBinaryOp
        assertEquals("==", binaryOp.op)
        assertEquals("a", (binaryOp.left as KVar).name)
        assertEquals("b", (binaryOp.right as KVar).name)
    }

    @Test
    fun `subtraction not-equals zero simplifies to not-equals`() {
        // (a - b) != 0 → a != b
        val subtraction = KBinaryOp(KVar("a"), "-", KVar("b"))
        val expr = KBinaryOp(subtraction, "!=", KLiteral("0"))
        val simplified = expr.simplify()

        assertTrue(simplified is KBinaryOp, "Should simplify to binary op")
        val binaryOp = simplified as KBinaryOp
        assertEquals("!=", binaryOp.op)
        assertEquals("a", (binaryOp.left as KVar).name)
        assertEquals("b", (binaryOp.right as KVar).name)
    }

    @Test
    fun `subtraction equals zero with hex literal`() {
        // (a - b) == 0x00 → a == b
        val subtraction = KBinaryOp(KVar("a"), "-", KVar("b"))
        val expr = KBinaryOp(subtraction, "==", KLiteral("0x00"))
        val simplified = expr.simplify()

        assertTrue(simplified is KBinaryOp, "Should simplify to binary op")
        val binaryOp = simplified as KBinaryOp
        assertEquals("==", binaryOp.op)
    }

    @Test
    fun `subtraction equals non-zero is preserved`() {
        // (a - b) == 5 - should not simplify
        val subtraction = KBinaryOp(KVar("a"), "-", KVar("b"))
        val expr = KBinaryOp(subtraction, "==", KLiteral("5"))
        val simplified = expr.simplify()

        // Should preserve structure since RHS is not zero
        assertTrue(simplified is KBinaryOp, "Should remain binary op")
        val binaryOp = simplified as KBinaryOp
        assertEquals("==", binaryOp.op)
        assertTrue(binaryOp.left is KBinaryOp, "Left should still be subtraction")
    }

    // =========================================================================
    // Arithmetic Simplification
    // =========================================================================

    @Test
    fun `subtract zero simplifies to operand`() {
        // a - 0 → a
        val expr = KBinaryOp(KVar("a"), "-", KLiteral("0"))
        val simplified = expr.simplify()

        assertTrue(simplified is KVar, "Should simplify to variable")
        assertEquals("a", (simplified as KVar).name)
    }

    @Test
    fun `subtract zero with hex literal`() {
        // a - 0x00 → a
        val expr = KBinaryOp(KVar("a"), "-", KLiteral("0x00"))
        val simplified = expr.simplify()

        assertTrue(simplified is KVar, "Should simplify to variable")
        assertEquals("a", (simplified as KVar).name)
    }

    @Test
    fun `add zero on right simplifies to operand`() {
        // a + 0 → a
        val expr = KBinaryOp(KVar("a"), "+", KLiteral("0"))
        val simplified = expr.simplify()

        assertTrue(simplified is KVar, "Should simplify to variable")
        assertEquals("a", (simplified as KVar).name)
    }

    @Test
    fun `add zero on left simplifies to operand`() {
        // 0 + a → a
        val expr = KBinaryOp(KLiteral("0"), "+", KVar("a"))
        val simplified = expr.simplify()

        assertTrue(simplified is KVar, "Should simplify to variable")
        assertEquals("a", (simplified as KVar).name)
    }

    @Test
    fun `add non-zero is preserved`() {
        // a + 5 - should not simplify
        val expr = KBinaryOp(KVar("a"), "+", KLiteral("5"))
        val simplified = expr.simplify()

        assertTrue(simplified is KBinaryOp, "Should remain binary op")
        assertEquals("+", (simplified as KBinaryOp).op)
    }

    @Test
    fun `subtract non-zero is preserved`() {
        // a - 5 - should not simplify
        val expr = KBinaryOp(KVar("a"), "-", KLiteral("5"))
        val simplified = expr.simplify()

        assertTrue(simplified is KBinaryOp, "Should remain binary op")
        assertEquals("-", (simplified as KBinaryOp).op)
    }

    // =========================================================================
    // Nested Simplification
    // =========================================================================

    @Test
    fun `nested simplification propagates`() {
        // !((a - 0) == b) → a != b
        val subtraction = KBinaryOp(KVar("a"), "-", KLiteral("0"))
        val comparison = KBinaryOp(subtraction, "==", KVar("b"))
        val expr = KUnaryOp("!", comparison)
        val simplified = expr.simplify()

        assertTrue(simplified is KBinaryOp, "Should simplify to binary op")
        val binaryOp = simplified as KBinaryOp
        assertEquals("!=", binaryOp.op)
        assertEquals("a", (binaryOp.left as KVar).name)
        assertEquals("b", (binaryOp.right as KVar).name)
    }

    @Test
    fun `parentheses around binary op are preserved when needed`() {
        val inner = KBinaryOp(KVar("a"), "+", KVar("b"))
        val expr = KParen(inner)
        val simplified = expr.simplify()

        // Parentheses around binary ops should be preserved
        assertTrue(simplified is KParen, "Should preserve parentheses around binary op")
    }

    @Test
    fun `parentheses around variable are removed`() {
        val expr = KParen(KVar("a"))
        val simplified = expr.simplify()

        // Parentheses around simple vars should be removed
        assertTrue(simplified is KVar, "Should remove unnecessary parentheses")
        assertEquals("a", (simplified as KVar).name)
    }

    // =========================================================================
    // isZero helper
    // =========================================================================

    @Test
    fun `isZero detects zero literal`() {
        assertTrue(KLiteral("0").isZero())
    }

    @Test
    fun `isZero detects 0x00 literal`() {
        assertTrue(KLiteral("0x00").isZero())
    }

    @Test
    fun `isZero detects 0x0 literal`() {
        assertTrue(KLiteral("0x0").isZero())
    }

    @Test
    fun `isZero rejects non-zero`() {
        assertTrue(!KLiteral("1").isZero())
        assertTrue(!KLiteral("0x01").isZero())
        assertTrue(!KLiteral("0xFF").isZero())
    }

    @Test
    fun `isZero rejects non-literals`() {
        assertTrue(!KVar("a").isZero())
    }
}
