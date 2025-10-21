package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 24: Expression Simplification
 * - Apply algebraic identities
 * - Perform constant folding
 * - Simplify boolean expressions
 * - Eliminate redundant operations
 * - Normalize expression forms
 */

/**
 * A simplification rule that can be applied to expressions
 */
data class SimplificationRule(
    val name: String,
    val matcher: (Expression) -> Boolean,
    val transformer: (Expression) -> Expression
) {
    fun apply(expr: Expression): Expression? {
        return if (matcher(expr)) transformer(expr) else null
    }
}

/**
 * Result of simplifying an expression
 */
data class SimplificationResult(
    val original: Expression,
    val simplified: Expression,
    val rulesApplied: List<String>
)

/**
 * Simplifications for a basic block
 */
data class BlockSimplifications(
    val block: BasicBlock,
    val simplifications: Map<ExpressionTree, SimplificationResult>
)

/**
 * Simplifications for a function
 */
data class FunctionSimplifications(
    val function: FunctionCfg,
    val blockSimplifications: Map<Int, BlockSimplifications>
)

/**
 * Complete expression simplification analysis
 */
data class ExpressionSimplificationAnalysis(
    val functions: List<FunctionSimplifications>
)

/**
 * Standard simplification rules
 */
private val simplificationRules = listOf(
    // ===== Arithmetic Identity Rules =====
    SimplificationRule(
        name = "add-zero-right",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.ADD && it.right is Expression.Literal && (it.right as Expression.Literal).value == 0 },
        transformer = { (it as Expression.BinaryOp).left }
    ),
    SimplificationRule(
        name = "add-zero-left",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.ADD && it.left is Expression.Literal && (it.left as Expression.Literal).value == 0 },
        transformer = { (it as Expression.BinaryOp).right }
    ),
    SimplificationRule(
        name = "sub-zero",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.SUB && it.right is Expression.Literal && (it.right as Expression.Literal).value == 0 },
        transformer = { (it as Expression.BinaryOp).left }
    ),
    SimplificationRule(
        name = "sub-self",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.SUB && it.left == it.right },
        transformer = { Expression.Literal(0) }
    ),
    SimplificationRule(
        name = "mul-zero",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.MUL && (it.left is Expression.Literal && (it.left as Expression.Literal).value == 0 || it.right is Expression.Literal && (it.right as Expression.Literal).value == 0) },
        transformer = { Expression.Literal(0) }
    ),
    SimplificationRule(
        name = "mul-one-right",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.MUL && it.right is Expression.Literal && (it.right as Expression.Literal).value == 1 },
        transformer = { (it as Expression.BinaryOp).left }
    ),
    SimplificationRule(
        name = "mul-one-left",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.MUL && it.left is Expression.Literal && (it.left as Expression.Literal).value == 1 },
        transformer = { (it as Expression.BinaryOp).right }
    ),
    SimplificationRule(
        name = "div-one",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.DIV && it.right is Expression.Literal && (it.right as Expression.Literal).value == 1 },
        transformer = { (it as Expression.BinaryOp).left }
    ),
    SimplificationRule(
        name = "div-self",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.DIV && it.left == it.right },
        transformer = { Expression.Literal(1) }
    ),

    // ===== Bitwise Identity Rules =====
    SimplificationRule(
        name = "and-all-ones",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.AND && it.right is Expression.Literal && (it.right as Expression.Literal).value == 0xFF },
        transformer = { (it as Expression.BinaryOp).left }
    ),
    SimplificationRule(
        name = "and-zero",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.AND && (it.left is Expression.Literal && (it.left as Expression.Literal).value == 0 || it.right is Expression.Literal && (it.right as Expression.Literal).value == 0) },
        transformer = { Expression.Literal(0) }
    ),
    SimplificationRule(
        name = "and-self",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.AND && it.left == it.right },
        transformer = { (it as Expression.BinaryOp).left }
    ),
    SimplificationRule(
        name = "or-zero-right",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.OR && it.right is Expression.Literal && (it.right as Expression.Literal).value == 0 },
        transformer = { (it as Expression.BinaryOp).left }
    ),
    SimplificationRule(
        name = "or-zero-left",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.OR && it.left is Expression.Literal && (it.left as Expression.Literal).value == 0 },
        transformer = { (it as Expression.BinaryOp).right }
    ),
    SimplificationRule(
        name = "or-all-ones",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.OR && (it.left is Expression.Literal && (it.left as Expression.Literal).value == 0xFF || it.right is Expression.Literal && (it.right as Expression.Literal).value == 0xFF) },
        transformer = { Expression.Literal(0xFF) }
    ),
    SimplificationRule(
        name = "or-self",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.OR && it.left == it.right },
        transformer = { (it as Expression.BinaryOp).left }
    ),
    SimplificationRule(
        name = "xor-zero-right",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.XOR && it.right is Expression.Literal && (it.right as Expression.Literal).value == 0 },
        transformer = { (it as Expression.BinaryOp).left }
    ),
    SimplificationRule(
        name = "xor-zero-left",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.XOR && it.left is Expression.Literal && (it.left as Expression.Literal).value == 0 },
        transformer = { (it as Expression.BinaryOp).right }
    ),
    SimplificationRule(
        name = "xor-self",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.XOR && it.left == it.right },
        transformer = { Expression.Literal(0) }
    ),

    // ===== Shift Rules =====
    SimplificationRule(
        name = "shift-zero",
        matcher = { it is Expression.BinaryOp && (it.op == BinaryOperator.SHL || it.op == BinaryOperator.SHR) && it.right is Expression.Literal && (it.right as Expression.Literal).value == 0 },
        transformer = { (it as Expression.BinaryOp).left }
    ),
    SimplificationRule(
        name = "shift-zero-value",
        matcher = { it is Expression.BinaryOp && (it.op == BinaryOperator.SHL || it.op == BinaryOperator.SHR) && it.left is Expression.Literal && (it.left as Expression.Literal).value == 0 },
        transformer = { Expression.Literal(0) }
    ),

    // ===== Constant Folding Rules =====
    SimplificationRule(
        name = "const-add",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.ADD && it.left is Expression.Literal && it.right is Expression.Literal },
        transformer = {
            val b = it as Expression.BinaryOp
            Expression.Literal(((b.left as Expression.Literal).value + (b.right as Expression.Literal).value) and 0xFF)
        }
    ),
    SimplificationRule(
        name = "const-sub",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.SUB && it.left is Expression.Literal && it.right is Expression.Literal },
        transformer = {
            val b = it as Expression.BinaryOp
            Expression.Literal(((b.left as Expression.Literal).value - (b.right as Expression.Literal).value) and 0xFF)
        }
    ),
    SimplificationRule(
        name = "const-mul",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.MUL && it.left is Expression.Literal && it.right is Expression.Literal },
        transformer = {
            val b = it as Expression.BinaryOp
            Expression.Literal(((b.left as Expression.Literal).value * (b.right as Expression.Literal).value) and 0xFF)
        }
    ),
    SimplificationRule(
        name = "const-and",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.AND && it.left is Expression.Literal && it.right is Expression.Literal },
        transformer = {
            val b = it as Expression.BinaryOp
            Expression.Literal((b.left as Expression.Literal).value and (b.right as Expression.Literal).value)
        }
    ),
    SimplificationRule(
        name = "const-or",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.OR && it.left is Expression.Literal && it.right is Expression.Literal },
        transformer = {
            val b = it as Expression.BinaryOp
            Expression.Literal((b.left as Expression.Literal).value or (b.right as Expression.Literal).value)
        }
    ),
    SimplificationRule(
        name = "const-xor",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.XOR && it.left is Expression.Literal && it.right is Expression.Literal },
        transformer = {
            val b = it as Expression.BinaryOp
            Expression.Literal((b.left as Expression.Literal).value xor (b.right as Expression.Literal).value)
        }
    ),
    SimplificationRule(
        name = "const-shl",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.SHL && it.left is Expression.Literal && it.right is Expression.Literal },
        transformer = {
            val b = it as Expression.BinaryOp
            Expression.Literal(((b.left as Expression.Literal).value shl (b.right as Expression.Literal).value) and 0xFF)
        }
    ),
    SimplificationRule(
        name = "const-shr",
        matcher = { it is Expression.BinaryOp && it.op == BinaryOperator.SHR && it.left is Expression.Literal && it.right is Expression.Literal },
        transformer = {
            val b = it as Expression.BinaryOp
            Expression.Literal((b.left as Expression.Literal).value ushr (b.right as Expression.Literal).value)
        }
    ),

    // ===== Comparison Simplification =====
    SimplificationRule(
        name = "const-eq",
        matcher = { it is Expression.Comparison && it.op == ComparisonOp.EQ && it.left is Expression.Literal && it.right is Expression.Literal },
        transformer = {
            val c = it as Expression.Comparison
            Expression.Literal(if ((c.left as Expression.Literal).value == (c.right as Expression.Literal).value) 1 else 0)
        }
    ),
    SimplificationRule(
        name = "const-ne",
        matcher = { it is Expression.Comparison && it.op == ComparisonOp.NE && it.left is Expression.Literal && it.right is Expression.Literal },
        transformer = {
            val c = it as Expression.Comparison
            Expression.Literal(if ((c.left as Expression.Literal).value != (c.right as Expression.Literal).value) 1 else 0)
        }
    ),
    SimplificationRule(
        name = "const-lt",
        matcher = { it is Expression.Comparison && it.op == ComparisonOp.LT && it.left is Expression.Literal && it.right is Expression.Literal },
        transformer = {
            val c = it as Expression.Comparison
            Expression.Literal(if ((c.left as Expression.Literal).value < (c.right as Expression.Literal).value) 1 else 0)
        }
    ),

    // ===== Double Negation =====
    SimplificationRule(
        name = "double-not",
        matcher = { it is Expression.UnaryOp && it.op == UnaryOperator.NOT && it.operand is Expression.UnaryOp && (it.operand as Expression.UnaryOp).op == UnaryOperator.NOT },
        transformer = { ((it as Expression.UnaryOp).operand as Expression.UnaryOp).operand }
    ),
    SimplificationRule(
        name = "double-neg",
        matcher = { it is Expression.UnaryOp && it.op == UnaryOperator.NEG && it.operand is Expression.UnaryOp && (it.operand as Expression.UnaryOp).op == UnaryOperator.NEG },
        transformer = { ((it as Expression.UnaryOp).operand as Expression.UnaryOp).operand }
    ),

    // ===== Associativity and Commutativity =====
    SimplificationRule(
        name = "merge-consecutive-shifts",
        matcher = {
            it is Expression.BinaryOp && it.op == BinaryOperator.SHL &&
            it.left is Expression.BinaryOp && (it.left as Expression.BinaryOp).op == BinaryOperator.SHL &&
            it.right is Expression.Literal && (it.left as Expression.BinaryOp).right is Expression.Literal
        },
        transformer = {
            val outer = it as Expression.BinaryOp
            val inner = outer.left as Expression.BinaryOp
            val totalShift = (outer.right as Expression.Literal).value + (inner.right as Expression.Literal).value
            Expression.BinaryOp(BinaryOperator.SHL, inner.left, Expression.Literal(totalShift))
        }
    )
)

/**
 * Simplify expressions in all functions
 */
fun ExpressionTreeAnalysis.simplifyExpressions(): ExpressionSimplificationAnalysis {
    val functionSimplifications = functions.map { funcExpr ->
        simplifyFunctionExpressions(funcExpr)
    }

    return ExpressionSimplificationAnalysis(functions = functionSimplifications)
}

/**
 * Simplify expressions in a single function
 */
private fun simplifyFunctionExpressions(
    functionExpressions: FunctionExpressions
): FunctionSimplifications {
    val blockSimplifications = mutableMapOf<Int, BlockSimplifications>()

    functionExpressions.blockExpressions.forEach { (leader, blockExpr) ->
        val simplifications = simplifyBlockExpressions(blockExpr)
        blockSimplifications[leader] = simplifications
    }

    return FunctionSimplifications(
        function = functionExpressions.function,
        blockSimplifications = blockSimplifications
    )
}

/**
 * Simplify expressions in a basic block
 */
private fun simplifyBlockExpressions(
    blockExpressions: BlockExpressions
): BlockSimplifications {
    val simplifications = mutableMapOf<ExpressionTree, SimplificationResult>()

    blockExpressions.expressions.forEach { exprTree ->
        val result = simplifyExpressionTree(exprTree.root)
        if (result.simplified != result.original) {
            simplifications[exprTree] = result
        }
    }

    return BlockSimplifications(
        block = blockExpressions.block,
        simplifications = simplifications
    )
}

/**
 * Simplify a single expression tree
 */
fun simplifyExpressionTree(expr: Expression): SimplificationResult {
    val rulesApplied = mutableListOf<String>()
    var current = expr
    var changed = true
    var iterations = 0
    val maxIterations = 100  // Prevent infinite loops

    // Fixed-point iteration: keep applying rules until no changes
    while (changed && iterations < maxIterations) {
        changed = false
        iterations++

        // Apply simplification bottom-up (recursively simplify children first)
        val simplified = simplifyBottomUp(current, rulesApplied)
        if (simplified != current) {
            current = simplified
            changed = true
        }
    }

    return SimplificationResult(
        original = expr,
        simplified = current,
        rulesApplied = rulesApplied
    )
}

/**
 * Simplify expression bottom-up (children first, then parent)
 */
private fun simplifyBottomUp(
    expr: Expression,
    rulesApplied: MutableList<String>
): Expression {
    // First, recursively simplify children
    val withSimplifiedChildren = when (expr) {
        is Expression.BinaryOp -> {
            val leftSimplified = simplifyBottomUp(expr.left, rulesApplied)
            val rightSimplified = simplifyBottomUp(expr.right, rulesApplied)
            if (leftSimplified != expr.left || rightSimplified != expr.right) {
                expr.copy(left = leftSimplified, right = rightSimplified)
            } else {
                expr
            }
        }

        is Expression.UnaryOp -> {
            val operandSimplified = simplifyBottomUp(expr.operand, rulesApplied)
            if (operandSimplified != expr.operand) {
                expr.copy(operand = operandSimplified)
            } else {
                expr
            }
        }

        is Expression.Comparison -> {
            val leftSimplified = simplifyBottomUp(expr.left, rulesApplied)
            val rightSimplified = simplifyBottomUp(expr.right, rulesApplied)
            if (leftSimplified != expr.left || rightSimplified != expr.right) {
                expr.copy(left = leftSimplified, right = rightSimplified)
            } else {
                expr
            }
        }

        is Expression.ArrayAccess -> {
            val baseSimplified = simplifyBottomUp(expr.base, rulesApplied)
            val indexSimplified = simplifyBottomUp(expr.index, rulesApplied)
            if (baseSimplified != expr.base || indexSimplified != expr.index) {
                expr.copy(base = baseSimplified, index = indexSimplified)
            } else {
                expr
            }
        }

        is Expression.MemoryAccess -> {
            val addressSimplified = simplifyBottomUp(expr.address, rulesApplied)
            if (addressSimplified != expr.address) {
                expr.copy(address = addressSimplified)
            } else {
                expr
            }
        }

        is Expression.Assignment -> {
            val targetSimplified = simplifyBottomUp(expr.target, rulesApplied)
            val valueSimplified = simplifyBottomUp(expr.value, rulesApplied)
            if (targetSimplified != expr.target || valueSimplified != expr.value) {
                expr.copy(target = targetSimplified, value = valueSimplified)
            } else {
                expr
            }
        }

        is Expression.FunctionCall -> {
            val argsSimplified = expr.arguments.map { simplifyBottomUp(it, rulesApplied) }
            if (argsSimplified != expr.arguments) {
                expr.copy(arguments = argsSimplified)
            } else {
                expr
            }
        }

        // Leaf nodes - no children to simplify
        is Expression.Literal,
        is Expression.VariableRef,
        is Expression.FieldAccess,
        is Expression.Phi -> expr
    }

    // Then apply simplification rules to the node itself
    for (rule in simplificationRules) {
        val simplified = rule.apply(withSimplifiedChildren)
        if (simplified != null && simplified != withSimplifiedChildren) {
            rulesApplied.add(rule.name)
            return simplified
        }
    }

    return withSimplifiedChildren
}
