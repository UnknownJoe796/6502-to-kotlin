package com.ivieleague.decompiler6502tokotlin.hand

/**
 * by Claude - Code generation utilities.
 *
 * Contains expression simplification, statement utilities, return handling,
 * register analysis, and label utilities.
 */

/**
 * Simplify a Kotlin expression for readability.
 *
 * Patterns simplified:
 * - `!(a - b == 0)` → `a != b`
 * - `!(a == 0)` → `a != 0`
 * - `a - b == 0` → `a == b`
 * - `!(a != b)` → `a == b`
 * - `!(a == b)` → `a != b`
 * - `a - 0x00` → `a`
 */
fun KotlinExpr.simplify(): KotlinExpr {
    return when (this) {
        is KUnaryOp -> {
            val simplifiedInner = expr.simplify()
            when {
                // !(a == b) → a != b
                op == "!" && simplifiedInner is KBinaryOp && simplifiedInner.op == "==" -> {
                    KBinaryOp(simplifiedInner.left.simplify(), "!=", simplifiedInner.right.simplify())
                }
                // !(a != b) → a == b
                op == "!" && simplifiedInner is KBinaryOp && simplifiedInner.op == "!=" -> {
                    KBinaryOp(simplifiedInner.left.simplify(), "==", simplifiedInner.right.simplify())
                }
                // Propagate simplification
                else -> KUnaryOp(op, simplifiedInner, postfix)
            }
        }
        is KBinaryOp -> {
            val simplifiedLeft = left.simplify()
            val simplifiedRight = right.simplify()
            when {
                // (a - b) == 0 → a == b
                op == "==" && simplifiedRight.isZero() && simplifiedLeft is KBinaryOp && simplifiedLeft.op == "-" -> {
                    KBinaryOp(simplifiedLeft.left.simplify(), "==", simplifiedLeft.right.simplify())
                }
                // 0 == (a - b) → a == b
                op == "==" && simplifiedLeft.isZero() && simplifiedRight is KBinaryOp && simplifiedRight.op == "-" -> {
                    KBinaryOp(simplifiedRight.left.simplify(), "==", simplifiedRight.right.simplify())
                }
                // (a - b) != 0 → a != b
                op == "!=" && simplifiedRight.isZero() && simplifiedLeft is KBinaryOp && simplifiedLeft.op == "-" -> {
                    KBinaryOp(simplifiedLeft.left.simplify(), "!=", simplifiedLeft.right.simplify())
                }
                // a - 0 → a (remove useless subtraction)
                op == "-" && simplifiedRight.isZero() -> {
                    simplifiedLeft
                }
                // a + 0 → a (remove useless addition)
                op == "+" && simplifiedRight.isZero() -> {
                    simplifiedLeft
                }
                // 0 + a → a
                op == "+" && simplifiedLeft.isZero() -> {
                    simplifiedRight
                }
                else -> KBinaryOp(simplifiedLeft, op, simplifiedRight)
            }
        }
        is KParen -> {
            val simplified = expr.simplify()
            // Only keep parens if still needed
            if (simplified is KBinaryOp || simplified is KUnaryOp) KParen(simplified) else simplified
        }
        is KMemberAccess -> {
            KMemberAccess(receiver.simplify(), member.simplify(), isIndexed)
        }
        is KCall -> {
            KCall(name, args.map { it.simplify() })
        }
        is KCast -> {
            KCast(expr.simplify(), type)
        }
        is KIfExpr -> {
            KIfExpr(condition.simplify(), thenExpr.simplify(), elseExpr.simplify())
        }
        // Literals and variables don't need simplification
        else -> this
    }
}

/**
 * Check if an expression represents zero.
 */
fun KotlinExpr.isZero(): Boolean {
    return when (this) {
        is KLiteral -> value == "0" || value == "0x00" || value == "0x0" || value.lowercase() == "0x00"
        else -> false
    }
}

/**
 * Check if a register is assigned to (as opposed to just read from).
 */
fun List<KotlinStmt>.isRegisterAssigned(register: String): Boolean {
    fun checkStmt(stmt: KotlinStmt): Boolean {
        return when (stmt) {
            is KAssignment -> stmt.target is KVar && (stmt.target as KVar).name == register
            is KVarDecl -> stmt.name == register
            is KDestructuringDecl -> register in stmt.names  // by Claude
            is KIf -> stmt.thenBranch.any { checkStmt(it) } || stmt.elseBranch.any { checkStmt(it) }
            is KWhile -> stmt.body.any { checkStmt(it) }
            is KDoWhile -> stmt.body.any { checkStmt(it) }
            is KLoop -> stmt.body.any { checkStmt(it) }
            // by Claude - Handle labeled loop variants
            is KLabeledWhile -> stmt.body.any { checkStmt(it) }
            is KLabeledDoWhile -> stmt.body.any { checkStmt(it) }
            is KLabeledLoop -> stmt.body.any { checkStmt(it) }
            is KWhen -> stmt.branches.any { it.body.any { s -> checkStmt(s) } } || stmt.elseBranch.any { checkStmt(it) }
            else -> false
        }
    }
    return this.any { checkStmt(it) }
}

/**
 * Collect all temp variable names (tempN) used in a list of statements.
 */
fun collectTempVars(stmts: List<KotlinStmt>): Set<String> {
    val tempVars = mutableSetOf<String>()

    fun collectFromExpr(expr: KotlinExpr) {
        when (expr) {
            is KVar -> if (expr.name.startsWith("temp") && expr.name.substring(4).all { it.isDigit() }) {
                tempVars.add(expr.name)
            }
            is KBinaryOp -> {
                collectFromExpr(expr.left)
                collectFromExpr(expr.right)
            }
            is KUnaryOp -> collectFromExpr(expr.expr)
            is KParen -> collectFromExpr(expr.expr)
            is KMemberAccess -> {
                collectFromExpr(expr.receiver)
                collectFromExpr(expr.member)
            }
            is KCall -> expr.args.forEach { collectFromExpr(it) }
            is KCast -> collectFromExpr(expr.expr)
            is KIfExpr -> {
                collectFromExpr(expr.condition)
                collectFromExpr(expr.thenExpr)
                collectFromExpr(expr.elseExpr)
            }
            else -> {}
        }
    }

    fun collectFromStmt(stmt: KotlinStmt) {
        when (stmt) {
            is KVarDecl -> {
                if (stmt.name.startsWith("temp") && stmt.name.substring(4).all { it.isDigit() }) {
                    tempVars.add(stmt.name)
                }
                stmt.value?.let { collectFromExpr(it) }
            }
            is KAssignment -> {
                collectFromExpr(stmt.target)
                collectFromExpr(stmt.value)
            }
            is KExprStmt -> collectFromExpr(stmt.expr)
            is KIf -> {
                collectFromExpr(stmt.condition)
                stmt.thenBranch.forEach { collectFromStmt(it) }
                stmt.elseBranch.forEach { collectFromStmt(it) }
            }
            is KWhile -> {
                collectFromExpr(stmt.condition)
                stmt.body.forEach { collectFromStmt(it) }
            }
            is KDoWhile -> {
                stmt.body.forEach { collectFromStmt(it) }
                collectFromExpr(stmt.condition)
            }
            is KLoop -> stmt.body.forEach { collectFromStmt(it) }
            // by Claude - Handle labeled loop variants
            is KLabeledWhile -> {
                collectFromExpr(stmt.condition)
                stmt.body.forEach { collectFromStmt(it) }
            }
            is KLabeledDoWhile -> {
                stmt.body.forEach { collectFromStmt(it) }
                collectFromExpr(stmt.condition)
            }
            is KLabeledLoop -> stmt.body.forEach { collectFromStmt(it) }
            is KReturn -> stmt.value?.let { collectFromExpr(it) }
            is KWhen -> {
                collectFromExpr(stmt.subject)
                stmt.branches.forEach { branch ->
                    branch.body.forEach { collectFromStmt(it) }
                }
                stmt.elseBranch.forEach { collectFromStmt(it) }
            }
            // by Claude - Destructuring decls use pairA/pairY names, not temp, so just collect from value
            is KDestructuringDecl -> collectFromExpr(stmt.value)
            else -> {}
        }
    }

    stmts.forEach { collectFromStmt(it) }
    return tempVars
}

/**
 * Convert temp variable declarations to assignments (since they're declared at function start).
 */
fun convertTempDeclsToAssignments(stmts: List<KotlinStmt>, tempVars: Set<String>): List<KotlinStmt> {
    fun convertStmt(stmt: KotlinStmt): KotlinStmt {
        return when (stmt) {
            is KVarDecl -> {
                // If this is a temp variable declaration, convert to assignment
                if (stmt.name in tempVars) {
                    KAssignment(KVar(stmt.name), stmt.value ?: KLiteral("0"))
                } else {
                    stmt
                }
            }
            is KIf -> KIf(
                condition = stmt.condition,
                thenBranch = stmt.thenBranch.map { convertStmt(it) },
                elseBranch = stmt.elseBranch.map { convertStmt(it) }
            )
            is KWhile -> KWhile(stmt.condition, stmt.body.map { convertStmt(it) })
            is KDoWhile -> KDoWhile(stmt.body.map { convertStmt(it) }, stmt.condition)
            is KLoop -> KLoop(stmt.body.map { convertStmt(it) })
            // by Claude - Handle labeled loop variants
            is KLabeledWhile -> KLabeledWhile(stmt.label, stmt.condition, stmt.body.map { convertStmt(it) })
            is KLabeledDoWhile -> KLabeledDoWhile(stmt.label, stmt.body.map { convertStmt(it) }, stmt.condition)
            is KLabeledLoop -> KLabeledLoop(stmt.label, stmt.body.map { convertStmt(it) })
            is KWhen -> KWhen(
                subject = stmt.subject,
                branches = stmt.branches.map { branch ->
                    KWhenBranch(branch.values, branch.body.map { convertStmt(it) })
                },
                elseBranch = stmt.elseBranch.map { convertStmt(it) }
            )
            else -> stmt
        }
    }

    return stmts.map { convertStmt(it) }
}

// ===== RETURN STATEMENT UTILITIES =====

/**
 * Transform return statements recursively using the provided transformation function.
 * The transformer receives the return value expression (may be null) and returns a new KReturn.
 */
// by Claude - Transform return statements using a custom transformation function
fun List<KotlinStmt>.transformReturns(transformer: (KotlinExpr?) -> KReturn): List<KotlinStmt> {
    return this.map { stmt ->
        when (stmt) {
            is KReturn -> transformer(stmt.value)
            is KIf -> KIf(
                condition = stmt.condition,
                thenBranch = stmt.thenBranch.transformReturns(transformer),
                elseBranch = stmt.elseBranch.transformReturns(transformer)
            )
            is KWhile -> KWhile(stmt.condition, stmt.body.transformReturns(transformer))
            is KDoWhile -> KDoWhile(stmt.body.transformReturns(transformer), stmt.condition)
            is KLoop -> KLoop(stmt.body.transformReturns(transformer))
            else -> stmt
        }
    }
}

/**
 * Ensures all paths have proper returns. Keeps existing return values,
 * and adds fallback return if function doesn't end with one.
 *
 * CRITICAL FIX by Claude: Only add fallback returns to if branches when the if is the
 * LAST statement in the block. If there's code after an if statement, control should
 * flow through to that code, not have returns added to branches that make it unreachable.
 */
// by Claude - Ensure all return statements have values, keeping existing values
fun List<KotlinStmt>.ensureReturnsPresent(fallbackReturn: KReturn): List<KotlinStmt> {
    val updated = this.mapIndexed { index, stmt ->
        val isLastStatement = index == this.size - 1
        when (stmt) {
            is KReturn -> if (stmt.value == null) fallbackReturn else stmt
            is KIf -> {
                // by Claude - Only add fallback returns to if branches when it's the last statement.
                // For non-terminal ifs, one or both branches may fall through to subsequent code.
                if (isLastStatement) {
                    KIf(
                        condition = stmt.condition,
                        thenBranch = stmt.thenBranch.ensureReturnsPresent(fallbackReturn),
                        elseBranch = stmt.elseBranch.ensureReturnsPresent(fallbackReturn)
                    )
                } else {
                    // Non-terminal if: only process branches that already end with returns
                    // (to update their return values), don't add new returns
                    KIf(
                        condition = stmt.condition,
                        thenBranch = stmt.thenBranch.updateExistingReturns(fallbackReturn),
                        elseBranch = stmt.elseBranch.updateExistingReturns(fallbackReturn)
                    )
                }
            }
            is KWhile -> KWhile(stmt.condition, stmt.body.ensureReturnsPresent(fallbackReturn))
            is KDoWhile -> KDoWhile(stmt.body.ensureReturnsPresent(fallbackReturn), stmt.condition)
            is KLoop -> KLoop(stmt.body.ensureReturnsPresent(fallbackReturn))
            else -> stmt
        }
    }
    // by Claude - Check if last statement provides returns on all paths
    val lastStmt = updated.lastOrNull()
    val endsWithReturn = lastStmt is KReturn ||
        (lastStmt is KIf && lastStmt.thenBranch.lastOrNull() is KReturn && lastStmt.elseBranch.lastOrNull() is KReturn)
    return if (!endsWithReturn) {
        updated + fallbackReturn
    } else {
        updated
    }
}

// by Claude - Update return values in existing returns without adding new returns
/**
 * Updates existing return statements to have fallback values if they're empty,
 * but does NOT add new returns to branches that don't have them.
 * This preserves fall-through behavior for non-terminal if statements.
 */
private fun List<KotlinStmt>.updateExistingReturns(fallbackReturn: KReturn): List<KotlinStmt> {
    return this.map { stmt ->
        when (stmt) {
            is KReturn -> if (stmt.value == null) fallbackReturn else stmt
            is KIf -> KIf(
                condition = stmt.condition,
                thenBranch = stmt.thenBranch.updateExistingReturns(fallbackReturn),
                elseBranch = stmt.elseBranch.updateExistingReturns(fallbackReturn)
            )
            is KWhile -> KWhile(stmt.condition, stmt.body.updateExistingReturns(fallbackReturn))
            is KDoWhile -> KDoWhile(stmt.body.updateExistingReturns(fallbackReturn), stmt.condition)
            is KLoop -> KLoop(stmt.body.updateExistingReturns(fallbackReturn))
            else -> stmt
        }
    }
}

fun List<KotlinStmt>.addReturnValues(): List<KotlinStmt> {
    return this.map { stmt ->
        when (stmt) {
            is KReturn -> if (stmt.value == null) KReturn(KVar("A")) else stmt
            is KIf -> KIf(
                condition = stmt.condition,
                thenBranch = stmt.thenBranch.addReturnValues(),
                elseBranch = stmt.elseBranch.addReturnValues()
            )
            is KWhile -> KWhile(stmt.condition, stmt.body.addReturnValues())
            is KDoWhile -> KDoWhile(stmt.body.addReturnValues(), stmt.condition)
            is KLoop -> KLoop(stmt.body.addReturnValues())
            else -> stmt
        }
    }
}

// by Claude - Strip return values for void functions
/**
 * Strip return values from return statements recursively.
 * Used for void functions to remove the A register values captured by RTS handler.
 */
fun List<KotlinStmt>.stripReturnValues(): List<KotlinStmt> {
    return this.map { stmt ->
        when (stmt) {
            is KReturn -> KReturn(null)
            is KIf -> KIf(
                condition = stmt.condition,
                thenBranch = stmt.thenBranch.stripReturnValues(),
                elseBranch = stmt.elseBranch.stripReturnValues()
            )
            is KWhile -> KWhile(stmt.condition, stmt.body.stripReturnValues())
            is KDoWhile -> KDoWhile(stmt.body.stripReturnValues(), stmt.condition)
            is KLoop -> KLoop(stmt.body.stripReturnValues())
            else -> stmt
        }
    }
}

// ===== REGISTER ANALYSIS UTILITIES =====

/**
 * Check if a register is read before being written in a list of statements.
 * This helps identify function parameters.
 */
fun List<KotlinStmt>.isRegisterReadBeforeWrite(registerName: String, ctx: CodeGenContext): Boolean {
    // Track if we've seen a write to this register
    var seenWrite = false

    fun checkStmt(stmt: KotlinStmt): Boolean {
        if (seenWrite) return false  // Already written, stop checking

        return when (stmt) {
            is KAssignment -> {
                // Check if target is the register (this is a write)
                if (stmt.target.isRegisterWrite(registerName)) {
                    seenWrite = true
                    false  // Write found, not a read
                } else {
                    // Check if value reads the register before the write
                    stmt.value.usesRegister(registerName)
                }
            }
            is KVarDecl -> {
                // Variable declaration with initialization is a write if it's the register
                if (stmt.name == registerName) {
                    seenWrite = true
                    false
                } else {
                    stmt.value?.usesRegister(registerName) ?: false
                }
            }
            is KExprStmt -> stmt.expr.usesRegister(registerName)
            is KIf -> {
                // Check condition first
                val conditionReads = stmt.condition.usesRegister(registerName)
                if (conditionReads) return true

                // Check branches (both need to be checked for complete analysis)
                val thenReads = stmt.thenBranch.any { checkStmt(it) }
                val elseReads = stmt.elseBranch.any { checkStmt(it) }
                thenReads || elseReads
            }
            is KWhile -> {
                val conditionReads = stmt.condition.usesRegister(registerName)
                if (conditionReads) return true
                stmt.body.any { checkStmt(it) }
            }
            is KDoWhile -> {
                val bodyReads = stmt.body.any { checkStmt(it) }
                if (bodyReads) return true
                stmt.condition.usesRegister(registerName)
            }
            is KLoop -> stmt.body.any { checkStmt(it) }
            is KReturn -> stmt.value?.usesRegister(registerName) ?: false
            // by Claude - Destructuring decl is a write to all named vars
            is KDestructuringDecl -> {
                if (registerName in stmt.names) {
                    seenWrite = true
                    false
                } else {
                    stmt.value.usesRegister(registerName)
                }
            }
            else -> false
        }
    }

    return this.any { checkStmt(it) }
}

/**
 * Check if an expression represents a write to a register.
 */
fun KotlinExpr.isRegisterWrite(registerName: String): Boolean {
    return when (this) {
        is KVar -> name == registerName
        else -> false
    }
}

/**
 * Check if a statement uses a specific register variable.
 */
fun KotlinStmt.usesRegister(registerName: String): Boolean {
    return when (this) {
        is KAssignment -> target.usesRegister(registerName) || value.usesRegister(registerName)
        is KExprStmt -> expr.usesRegister(registerName)
        is KIf -> condition.usesRegister(registerName) ||
                  thenBranch.any { it.usesRegister(registerName) } ||
                  elseBranch.any { it.usesRegister(registerName) }
        is KWhile -> condition.usesRegister(registerName) || body.any { it.usesRegister(registerName) }
        is KDoWhile -> condition.usesRegister(registerName) || body.any { it.usesRegister(registerName) }
        is KLoop -> body.any { it.usesRegister(registerName) }
        // by Claude - Handle labeled loop variants
        is KLabeledWhile -> condition.usesRegister(registerName) || body.any { it.usesRegister(registerName) }
        is KLabeledDoWhile -> condition.usesRegister(registerName) || body.any { it.usesRegister(registerName) }
        is KLabeledLoop -> body.any { it.usesRegister(registerName) }
        is KReturn -> value?.usesRegister(registerName) ?: false
        is KVarDecl -> value?.usesRegister(registerName) ?: false
        is KDestructuringDecl -> value.usesRegister(registerName)  // by Claude
        else -> false
    }
}

/**
 * Check if an expression uses a specific register variable.
 */
fun KotlinExpr.usesRegister(registerName: String): Boolean {
    return when (this) {
        is KVar -> name == registerName
        is KBinaryOp -> left.usesRegister(registerName) || right.usesRegister(registerName)
        is KUnaryOp -> expr.usesRegister(registerName)
        is KParen -> expr.usesRegister(registerName)
        is KMemberAccess -> receiver.usesRegister(registerName) || member.usesRegister(registerName)
        is KCall -> args.any { it.usesRegister(registerName) }
        is KCast -> expr.usesRegister(registerName)
        else -> false
    }
}

// by Claude - Detect terminating statements for smart if-else merge
/**
 * Check if a statement terminates control flow (return).
 * Used to determine which branch's state should be preserved after if-else.
 */
fun KotlinStmt.isTerminating(): Boolean {
    return when (this) {
        is KReturn -> true
        // by Claude - Labeled breaks terminate the current code path (exit the loop)
        is KLabeledBreak -> true
        is KBreak -> true
        // For if statements, terminating only if BOTH branches terminate
        is KIf -> {
            val thenTerminates = thenBranch.lastOrNull()?.isTerminating() == true
            val elseTerminates = elseBranch.lastOrNull()?.isTerminating() == true

            if (thenTerminates && elseTerminates) {
                true
            } else if (elseBranch.isEmpty()) {
                // by Claude - Special case: nested if with same condition (bpl/bmi pattern)
                // Look for ANY nested if with the same condition whose then branch terminates.
                // This handles: if (cond) { if (cond) { return } } where the fall-through is dead code.
                // The nested if can be anywhere in the then branch, not just at the end.
                val nestedSameConditionIf = thenBranch.filterIsInstance<KIf>()
                    .find { conditionsAreEquivalent(this.condition, it.condition) }
                if (nestedSameConditionIf != null &&
                    nestedSameConditionIf.thenBranch.lastOrNull()?.isTerminating() == true) {
                    true
                } else {
                    thenTerminates
                }
            } else {
                false
            }
        }
        else -> false
    }
}

/**
 * by Claude - Check if two conditions are structurally equivalent.
 * Used to detect nested ifs with the same condition (bpl/bmi pattern).
 */
private fun conditionsAreEquivalent(cond1: KotlinExpr, cond2: KotlinExpr): Boolean {
    // Compare rendered strings for simplicity
    // This handles cases like (A and 0x80) != 0 being the same
    return cond1.toKotlin() == cond2.toKotlin()
}

// ===== LABEL UTILITIES =====

/**
 * Sanitize a label to make it a valid Kotlin identifier.
 * Converts addresses like $00 to zp_00, etc.
 */
fun sanitizeLabel(label: String): String {
    return when {
        label.startsWith("$") -> {
            // Zero-page or absolute address
            val hex = label.substring(1)
            "zp_$hex"
        }
        label.matches(Regex("^[0-9].*")) -> {
            // Starts with digit, prefix with underscore
            "_$label"
        }
        else -> label
    }
}

/**
 * Convert label name to camelCase property name.
 * Example: "OperMode" -> "operMode", "Player_X_Speed" -> "playerXSpeed"
 * Preserves internal capitalization: "SwimmingFlag" -> "swimmingFlag"
 */
fun labelToCamelCase(label: String): String {
    return label.split('_')
        .mapIndexed { index, part ->
            if (part.isEmpty()) return@mapIndexed ""
            if (index == 0) {
                // First word: lowercase just the first character, keep rest as-is
                part.replaceFirstChar { it.lowercase() }
            } else {
                // Subsequent words: uppercase first character, keep rest as-is
                part.replaceFirstChar { it.uppercase() }
            }
        }
        .joinToString("")
}
