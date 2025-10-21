package com.ivieleague.decompiler6502tokotlin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for Pass 24: Expression Simplification
 */
class ExpressionSimplificationTest {

    @Test
    fun `test add zero simplification`() {
        val expr = Expression.BinaryOp(
            BinaryOperator.ADD,
            Expression.Literal(42),
            Expression.Literal(0)
        )

        val result = simplifyExpressionTree(expr)

        // Should simplify to just 42
        assertTrue(result.simplified is Expression.Literal)
        assertEquals(42, (result.simplified as Expression.Literal).value)
        assertTrue(result.rulesApplied.contains("add-zero-right"))
    }

    @Test
    fun `test multiply by one simplification`() {
        val expr = Expression.BinaryOp(
            BinaryOperator.MUL,
            Expression.VariableRef(Variable.RegisterA),
            Expression.Literal(1)
        )

        val result = simplifyExpressionTree(expr)

        // Should simplify to just the variable
        assertTrue(result.simplified is Expression.VariableRef)
        assertTrue(result.rulesApplied.contains("mul-one-right"))
    }

    @Test
    fun `test multiply by zero simplification`() {
        val expr = Expression.BinaryOp(
            BinaryOperator.MUL,
            Expression.VariableRef(Variable.RegisterA),
            Expression.Literal(0)
        )

        val result = simplifyExpressionTree(expr)

        // Should simplify to 0
        assertTrue(result.simplified is Expression.Literal)
        assertEquals(0, (result.simplified as Expression.Literal).value)
        assertTrue(result.rulesApplied.contains("mul-zero"))
    }

    @Test
    fun `test subtract self simplification`() {
        val varRef = Expression.VariableRef(Variable.RegisterX)
        val expr = Expression.BinaryOp(
            BinaryOperator.SUB,
            varRef,
            varRef
        )

        val result = simplifyExpressionTree(expr)

        // Should simplify to 0
        assertTrue(result.simplified is Expression.Literal)
        assertEquals(0, (result.simplified as Expression.Literal).value)
        assertTrue(result.rulesApplied.contains("sub-self"))
    }

    @Test
    fun `test AND with all ones simplification`() {
        val expr = Expression.BinaryOp(
            BinaryOperator.AND,
            Expression.VariableRef(Variable.RegisterA),
            Expression.Literal(0xFF)
        )

        val result = simplifyExpressionTree(expr)

        // Should simplify to just the variable
        assertTrue(result.simplified is Expression.VariableRef)
        assertTrue(result.rulesApplied.contains("and-all-ones"))
    }

    @Test
    fun `test AND with zero simplification`() {
        val expr = Expression.BinaryOp(
            BinaryOperator.AND,
            Expression.VariableRef(Variable.RegisterA),
            Expression.Literal(0)
        )

        val result = simplifyExpressionTree(expr)

        // Should simplify to 0
        assertTrue(result.simplified is Expression.Literal)
        assertEquals(0, (result.simplified as Expression.Literal).value)
        assertTrue(result.rulesApplied.contains("and-zero"))
    }

    @Test
    fun `test OR with zero simplification`() {
        val expr = Expression.BinaryOp(
            BinaryOperator.OR,
            Expression.Literal(42),
            Expression.Literal(0)
        )

        val result = simplifyExpressionTree(expr)

        // Should simplify to 42
        assertTrue(result.simplified is Expression.Literal)
        assertEquals(42, (result.simplified as Expression.Literal).value)
        assertTrue(result.rulesApplied.contains("or-zero-right"))
    }

    @Test
    fun `test XOR self simplification`() {
        val varRef = Expression.VariableRef(Variable.RegisterA)
        val expr = Expression.BinaryOp(
            BinaryOperator.XOR,
            varRef,
            varRef
        )

        val result = simplifyExpressionTree(expr)

        // Should simplify to 0
        assertTrue(result.simplified is Expression.Literal)
        assertEquals(0, (result.simplified as Expression.Literal).value)
        assertTrue(result.rulesApplied.contains("xor-self"))
    }

    @Test
    fun `test constant folding addition`() {
        val expr = Expression.BinaryOp(
            BinaryOperator.ADD,
            Expression.Literal(10),
            Expression.Literal(5)
        )

        val result = simplifyExpressionTree(expr)

        // Should fold to 15
        assertTrue(result.simplified is Expression.Literal)
        assertEquals(15, (result.simplified as Expression.Literal).value)
        assertTrue(result.rulesApplied.contains("const-add"))
    }

    @Test
    fun `test constant folding subtraction`() {
        val expr = Expression.BinaryOp(
            BinaryOperator.SUB,
            Expression.Literal(20),
            Expression.Literal(8)
        )

        val result = simplifyExpressionTree(expr)

        // Should fold to 12
        assertTrue(result.simplified is Expression.Literal)
        assertEquals(12, (result.simplified as Expression.Literal).value)
        assertTrue(result.rulesApplied.contains("const-sub"))
    }

    @Test
    fun `test constant folding bitwise AND`() {
        val expr = Expression.BinaryOp(
            BinaryOperator.AND,
            Expression.Literal(0xF0),
            Expression.Literal(0x0F)
        )

        val result = simplifyExpressionTree(expr)

        // Should fold to 0
        assertTrue(result.simplified is Expression.Literal)
        assertEquals(0, (result.simplified as Expression.Literal).value)
        assertTrue(result.rulesApplied.contains("const-and"))
    }

    @Test
    fun `test constant folding bitwise OR`() {
        val expr = Expression.BinaryOp(
            BinaryOperator.OR,
            Expression.Literal(0xF0),
            Expression.Literal(0x0F)
        )

        val result = simplifyExpressionTree(expr)

        // Should fold to 0xFF
        assertTrue(result.simplified is Expression.Literal)
        assertEquals(0xFF, (result.simplified as Expression.Literal).value)
        assertTrue(result.rulesApplied.contains("const-or"))
    }

    @Test
    fun `test constant folding shift left`() {
        val expr = Expression.BinaryOp(
            BinaryOperator.SHL,
            Expression.Literal(1),
            Expression.Literal(3)
        )

        val result = simplifyExpressionTree(expr)

        // Should fold to 8
        assertTrue(result.simplified is Expression.Literal)
        assertEquals(8, (result.simplified as Expression.Literal).value)
        assertTrue(result.rulesApplied.contains("const-shl"))
    }

    @Test
    fun `test double negation elimination`() {
        val expr = Expression.UnaryOp(
            UnaryOperator.NOT,
            Expression.UnaryOp(
                UnaryOperator.NOT,
                Expression.VariableRef(Variable.RegisterA)
            )
        )

        val result = simplifyExpressionTree(expr)

        // Should simplify to just the variable
        assertTrue(result.simplified is Expression.VariableRef)
        assertTrue(result.rulesApplied.contains("double-not"))
    }

    @Test
    fun `test nested expression simplification`() {
        // (x + 0) * 1
        val expr = Expression.BinaryOp(
            BinaryOperator.MUL,
            Expression.BinaryOp(
                BinaryOperator.ADD,
                Expression.VariableRef(Variable.RegisterA),
                Expression.Literal(0)
            ),
            Expression.Literal(1)
        )

        val result = simplifyExpressionTree(expr)

        // Should simplify all the way to just x
        assertTrue(result.simplified is Expression.VariableRef)
        assertTrue(result.rulesApplied.contains("add-zero-right"))
        assertTrue(result.rulesApplied.contains("mul-one-right"))
    }

    @Test
    fun `test complex constant folding`() {
        // (3 + 5) * 2
        val expr = Expression.BinaryOp(
            BinaryOperator.MUL,
            Expression.BinaryOp(
                BinaryOperator.ADD,
                Expression.Literal(3),
                Expression.Literal(5)
            ),
            Expression.Literal(2)
        )

        val result = simplifyExpressionTree(expr)

        // Should fold to 16
        assertTrue(result.simplified is Expression.Literal)
        assertEquals(16, (result.simplified as Expression.Literal).value)
    }

    @Test
    fun `test shift by zero simplification`() {
        val expr = Expression.BinaryOp(
            BinaryOperator.SHL,
            Expression.VariableRef(Variable.RegisterA),
            Expression.Literal(0)
        )

        val result = simplifyExpressionTree(expr)

        // Should simplify to just the variable
        assertTrue(result.simplified is Expression.VariableRef)
        assertTrue(result.rulesApplied.contains("shift-zero"))
    }

    @Test
    fun `test merge consecutive shifts`() {
        // (x << 2) << 3 should become x << 5
        val expr = Expression.BinaryOp(
            BinaryOperator.SHL,
            Expression.BinaryOp(
                BinaryOperator.SHL,
                Expression.VariableRef(Variable.RegisterA),
                Expression.Literal(2)
            ),
            Expression.Literal(3)
        )

        val result = simplifyExpressionTree(expr)

        // Should merge to single shift by 5
        assertTrue(result.simplified is Expression.BinaryOp)
        val binOp = result.simplified as Expression.BinaryOp
        assertEquals(BinaryOperator.SHL, binOp.op)
        assertTrue(binOp.right is Expression.Literal)
        assertEquals(5, (binOp.right as Expression.Literal).value)
        assertTrue(result.rulesApplied.contains("merge-consecutive-shifts"))
    }
}
