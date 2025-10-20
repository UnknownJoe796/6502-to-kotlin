package com.ivieleague.decompiler6502tokotlin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for Pass 36: Expression Emission
 *
 * This pass converts Pass 22 expression trees into Kotlin AST expressions,
 * emitting idiomatic Kotlin code from 6502 operations.
 */
class Pass36ExpressionEmissionTest {

    @Test
    fun `test literal expression emission`() {
        val expr = Expression.Literal(0x42)
        val kotlinExpr = expr.toKotlinExpression()

        assertTrue(kotlinExpr is KotlinAst.Expression.Literal)
        val literal = kotlinExpr as KotlinAst.Expression.Literal
        assertEquals(0x42, literal.value)
        assertEquals(KotlinAst.KotlinType.Int, literal.type)
    }

    @Test
    fun `test variable reference emission`() {
        val exprA = Expression.VariableRef(Variable.RegisterA)
        val kotlinExprA = exprA.toKotlinExpression()

        assertTrue(kotlinExprA is KotlinAst.Expression.Variable)
        assertEquals("A", (kotlinExprA as KotlinAst.Expression.Variable).name)

        val exprX = Expression.VariableRef(Variable.RegisterX)
        val kotlinExprX = exprX.toKotlinExpression()
        assertEquals("X", (kotlinExprX as KotlinAst.Expression.Variable).name)
    }

    @Test
    fun `test memory access emission`() {
        val address = Expression.Literal(0x0200)
        val memAccess = Expression.MemoryAccess(address)
        val kotlinExpr = memAccess.toKotlinExpression()

        assertTrue(kotlinExpr is KotlinAst.Expression.ArrayAccess)
        val arrayAccess = kotlinExpr as KotlinAst.Expression.ArrayAccess

        assertTrue(arrayAccess.array is KotlinAst.Expression.Variable)
        assertEquals("memory", (arrayAccess.array as KotlinAst.Expression.Variable).name)

        assertTrue(arrayAccess.index is KotlinAst.Expression.Literal)
        assertEquals(0x0200, (arrayAccess.index as KotlinAst.Expression.Literal).value)
    }

    @Test
    fun `test arithmetic binary operation emission`() {
        val left = Expression.Literal(5)
        val right = Expression.Literal(3)
        val addExpr = Expression.BinaryOp(BinaryOperator.ADD, left, right)

        val kotlinExpr = addExpr.toKotlinExpression()

        // Should be wrapped in masking: (5 + 3) and 0xFF
        assertTrue(kotlinExpr is KotlinAst.Expression.Binary)
        val outerBinary = kotlinExpr as KotlinAst.Expression.Binary
        assertEquals(KotlinAst.BinaryOp.AND, outerBinary.operator)

        assertTrue(outerBinary.left is KotlinAst.Expression.Binary)
        val innerBinary = outerBinary.left as KotlinAst.Expression.Binary
        assertEquals(KotlinAst.BinaryOp.PLUS, innerBinary.operator)

        assertTrue(outerBinary.right is KotlinAst.Expression.Literal)
        assertEquals(0xFF, (outerBinary.right as KotlinAst.Expression.Literal).value)
    }

    @Test
    fun `test bitwise operation emission`() {
        val left = Expression.VariableRef(Variable.RegisterA)
        val right = Expression.Literal(0x0F)
        val andExpr = Expression.BinaryOp(BinaryOperator.AND, left, right)

        val kotlinExpr = andExpr.toKotlinExpression()

        assertTrue(kotlinExpr is KotlinAst.Expression.Binary)
        val binary = kotlinExpr as KotlinAst.Expression.Binary
        assertEquals(KotlinAst.BinaryOp.AND, binary.operator)

        // Should NOT be wrapped in extra masking (bitwise ops don't need it)
        assertTrue(binary.left is KotlinAst.Expression.Variable)
        assertTrue(binary.right is KotlinAst.Expression.Literal)
    }

    @Test
    fun `test comparison operation emission`() {
        val left = Expression.VariableRef(Variable.RegisterX)
        val right = Expression.Literal(0x10)
        val cmpExpr = Expression.Comparison(ComparisonOp.EQ, left, right)

        val kotlinExpr = cmpExpr.toKotlinExpression()

        assertTrue(kotlinExpr is KotlinAst.Expression.Binary)
        val binary = kotlinExpr as KotlinAst.Expression.Binary
        assertEquals(KotlinAst.BinaryOp.EQ, binary.operator)
    }

    @Test
    fun `test unary operation emission`() {
        val operand = Expression.VariableRef(Variable.RegisterA)
        val notExpr = Expression.UnaryOp(UnaryOperator.NOT, operand)

        val kotlinExpr = notExpr.toKotlinExpression()

        assertTrue(kotlinExpr is KotlinAst.Expression.Unary)
        val unary = kotlinExpr as KotlinAst.Expression.Unary
        assertEquals(KotlinAst.UnaryOp.NOT, unary.operator)

        assertTrue(unary.operand is KotlinAst.Expression.Variable)
        assertEquals("A", (unary.operand as KotlinAst.Expression.Variable).name)
    }

    @Test
    fun `test expression formatting`() {
        // Test literal formatting
        val literal = KotlinAst.Expression.Literal(0x42, KotlinAst.KotlinType.Int)
        assertEquals("0x42", literal.formatAsKotlin())

        // Test variable formatting
        val variable = KotlinAst.Expression.Variable("A")
        assertEquals("A", variable.formatAsKotlin())

        // Test binary operation formatting
        val binary = KotlinAst.Expression.Binary(
            left = KotlinAst.Expression.Variable("A"),
            operator = KotlinAst.BinaryOp.PLUS,
            right = KotlinAst.Expression.Literal(1, KotlinAst.KotlinType.Int)
        )
        assertEquals("(A + 0x1)", binary.formatAsKotlin())

        // Test array access formatting
        val arrayAccess = KotlinAst.Expression.ArrayAccess(
            array = KotlinAst.Expression.Variable("memory"),
            index = KotlinAst.Expression.Literal(0x0200, KotlinAst.KotlinType.Int)
        )
        assertEquals("memory[0x200]", arrayAccess.formatAsKotlin())
    }

    @Test
    fun `test function call emission`() {
        val arg1 = Expression.Literal(0x42)
        val arg2 = Expression.VariableRef(Variable.RegisterA)
        val callExpr = Expression.FunctionCall("ProcessData", listOf(arg1, arg2))

        val kotlinExpr = callExpr.toKotlinExpression()

        assertTrue(kotlinExpr is KotlinAst.Expression.Call)
        val call = kotlinExpr as KotlinAst.Expression.Call
        assertEquals("ProcessData", call.name)
        assertEquals(2, call.arguments.size)
    }

    @Test
    fun `test assignment emission`() {
        val target = Expression.VariableRef(Variable.RegisterA)
        val value = Expression.Literal(0x42)
        val assignment = Expression.Assignment(target, value)

        val kotlinExpr = assignment.toKotlinExpression()

        assertTrue(kotlinExpr is KotlinAst.Expression.Binary)
        val binary = kotlinExpr as KotlinAst.Expression.Binary
        assertEquals(KotlinAst.BinaryOp.ASSIGN, binary.operator)
    }
}
