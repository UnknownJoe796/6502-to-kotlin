package com.ivieleague.decompiler6502tokotlin.validation

import com.ivieleague.decompiler6502tokotlin.hand.*

/**
 * Interprets Kotlin AST nodes directly for validation testing.
 *
 * This allows us to execute decompiled Kotlin code WITHOUT needing to compile it,
 * using the same memory model as the assembly interpreter for direct comparison.
 */
class KotlinAstInterpreter(
    val memory: UByteArray,
    var A: Int = 0,
    var X: Int = 0,
    var Y: Int = 0,
    var flagN: Boolean = false,
    var flagZ: Boolean = false,
    var flagC: Boolean = false,
    var flagV: Boolean = false,
    var SP: Int = 0xFF
) {
    // Local variables within the function
    private val locals = mutableMapOf<String, Int>()

    // Delegate property values (base addresses for MemoryByte/MemoryByteIndexed)
    private val delegateAddresses = mutableMapOf<String, Int>()

    // Track if we've returned
    private var hasReturned = false

    // Function registry for JSR calls
    var functionRegistry: Map<String, KFunction> = emptyMap()

    // Constants from the assembly
    var constants: Map<String, Int> = emptyMap()

    /**
     * Execute a KFunction and return final state
     */
    fun execute(function: KFunction): ExecutionResult {
        hasReturned = false
        locals.clear()
        delegateAddresses.clear()

        // Execute each statement
        for (stmt in function.body) {
            if (hasReturned) break
            executeStmt(stmt)
        }

        return ExecutionResult(
            A = A,
            X = X,
            Y = Y,
            flagN = flagN,
            flagZ = flagZ,
            flagC = flagC,
            flagV = flagV,
            memory = memory.copyOf()
        )
    }

    private fun executeStmt(stmt: KotlinStmt) {
        if (hasReturned) return

        when (stmt) {
            is KVarDecl -> executeVarDecl(stmt)
            is KAssignment -> executeAssignment(stmt)
            is KExprStmt -> evalExpr(stmt.expr)
            is KIf -> executeIf(stmt)
            is KWhile -> executeWhile(stmt)
            is KDoWhile -> executeDoWhile(stmt)
            is KLoop -> executeLoop(stmt)
            is KReturn -> {
                hasReturned = true
            }
            is KWhen -> executeWhen(stmt)
            is KComment, is KBlockComment -> { /* Skip comments */ }
            is KBreak -> { /* TODO: Handle break in loops */ }
            is KContinue -> { /* TODO: Handle continue in loops */ }
        }
    }

    private fun executeVarDecl(decl: KVarDecl) {
        if (decl.delegated && decl.value != null) {
            // This is a delegate declaration like: var x by MemoryByte(ADDR)
            val call = decl.value as? KCall ?: return
            if (call.name == "MemoryByte" || call.name == "MemoryByteIndexed") {
                val addrExpr = call.args.firstOrNull() ?: return
                val addr = evalExpr(addrExpr)
                delegateAddresses[decl.name] = addr
            }
        } else if (decl.value != null) {
            // Regular variable declaration
            locals[decl.name] = evalExpr(decl.value)
        } else {
            // Declaration without initializer
            locals[decl.name] = 0
        }
    }

    private fun executeAssignment(assign: KAssignment) {
        val value = evalExpr(assign.value)

        when (val target = assign.target) {
            is KVar -> {
                val name = target.name
                when (name) {
                    "A" -> A = value and 0xFF
                    "X" -> X = value and 0xFF
                    "Y" -> Y = value and 0xFF
                    "flagN" -> flagN = value != 0
                    "flagZ" -> flagZ = value != 0
                    "flagC" -> flagC = value != 0
                    "flagV" -> flagV = value != 0
                    else -> {
                        // Check if it's a delegate property
                        val delegateAddr = delegateAddresses[name]
                        if (delegateAddr != null) {
                            memory[delegateAddr and 0xFFFF] = (value and 0xFF).toUByte()
                        } else {
                            locals[name] = value
                        }
                    }
                }
            }
            is KMemberAccess -> {
                if (target.isIndexed) {
                    // Array access like memory[addr] or delegateProp[index]
                    when (val receiver = target.receiver) {
                        is KVar -> {
                            val receiverName = receiver.name
                            if (receiverName == "memory") {
                                val addr = evalExpr(target.member)
                                memory[addr and 0xFFFF] = (value and 0xFF).toUByte()
                            } else {
                                // Delegate indexed access
                                val baseAddr = delegateAddresses[receiverName] ?: 0
                                val index = evalExpr(target.member)
                                memory[(baseAddr + index) and 0xFFFF] = (value and 0xFF).toUByte()
                            }
                        }
                        else -> {
                            // Nested access - evaluate receiver
                            val addr = evalExpr(target.member)
                            memory[addr and 0xFFFF] = (value and 0xFF).toUByte()
                        }
                    }
                }
            }
            else -> {
                // Other target types
            }
        }
    }

    private fun executeIf(ifStmt: KIf) {
        val condition = evalExpr(ifStmt.condition) != 0
        if (condition) {
            for (stmt in ifStmt.thenBranch) {
                if (hasReturned) break
                executeStmt(stmt)
            }
        } else {
            for (stmt in ifStmt.elseBranch) {
                if (hasReturned) break
                executeStmt(stmt)
            }
        }
    }

    private fun executeWhile(whileStmt: KWhile) {
        var iterations = 0
        val maxIterations = 10000 // Prevent infinite loops

        while (evalExpr(whileStmt.condition) != 0 && iterations < maxIterations) {
            for (stmt in whileStmt.body) {
                if (hasReturned) return
                executeStmt(stmt)
            }
            iterations++
        }
    }

    private fun executeDoWhile(doWhile: KDoWhile) {
        var iterations = 0
        val maxIterations = 10000

        do {
            for (stmt in doWhile.body) {
                if (hasReturned) return
                executeStmt(stmt)
            }
            iterations++
        } while (evalExpr(doWhile.condition) != 0 && iterations < maxIterations)
    }

    private fun executeLoop(loop: KLoop) {
        var iterations = 0
        val maxIterations = 10000

        while (iterations < maxIterations) {
            for (stmt in loop.body) {
                if (hasReturned) return
                executeStmt(stmt)
            }
            iterations++
        }
    }

    private fun executeWhen(whenStmt: KWhen) {
        val subject = evalExpr(whenStmt.subject)

        for (branch in whenStmt.branches) {
            val matches = branch.values.any { evalExpr(it) == subject }
            if (matches) {
                for (stmt in branch.body) {
                    if (hasReturned) return
                    executeStmt(stmt)
                }
                return
            }
        }

        // Execute else branch if no match
        for (stmt in whenStmt.elseBranch) {
            if (hasReturned) return
            executeStmt(stmt)
        }
    }

    private fun evalExpr(expr: KotlinExpr): Int {
        return when (expr) {
            is KLiteral -> parseLiteral(expr.value)
            is KVar -> evalVar(expr.name)
            is KBinaryOp -> evalBinaryOp(expr)
            is KUnaryOp -> evalUnaryOp(expr)
            is KCall -> evalCall(expr)
            is KMemberAccess -> evalMemberAccess(expr)
            is KParen -> evalExpr(expr.expr)
            is KCast -> evalExpr(expr.expr) // Ignore cast for now
            is KIfExpr -> {
                if (evalExpr(expr.condition) != 0) {
                    evalExpr(expr.thenExpr)
                } else {
                    evalExpr(expr.elseExpr)
                }
            }
        }
    }

    private fun parseLiteral(value: String): Int {
        return when {
            value.startsWith("0x") || value.startsWith("0X") ->
                value.substring(2).toIntOrNull(16) ?: 0
            value.startsWith("0b") || value.startsWith("0B") ->
                value.substring(2).toIntOrNull(2) ?: 0
            value == "true" -> 1
            value == "false" -> 0
            else -> value.toIntOrNull() ?: 0
        }
    }

    private fun evalVar(name: String): Int {
        return when (name) {
            "A" -> A
            "X" -> X
            "Y" -> Y
            "flagN" -> if (flagN) 1 else 0
            "flagZ" -> if (flagZ) 1 else 0
            "flagC" -> if (flagC) 1 else 0
            "flagV" -> if (flagV) 1 else 0
            else -> {
                // Check delegates first
                val delegateAddr = delegateAddresses[name]
                if (delegateAddr != null) {
                    memory[delegateAddr and 0xFFFF].toInt()
                } else {
                    // Check locals, then constants
                    locals[name] ?: constants[name] ?: constants[name.uppercase()] ?: 0
                }
            }
        }
    }

    private fun evalBinaryOp(op: KBinaryOp): Int {
        val left = evalExpr(op.left)
        val right = evalExpr(op.right)

        return when (op.op) {
            "+" -> left + right
            "-" -> left - right
            "*" -> left * right
            "/" -> if (right != 0) left / right else 0
            "%" -> if (right != 0) left % right else 0
            "and" -> left and right
            "or" -> left or right
            "xor" -> left xor right
            "shl" -> left shl right
            "shr" -> left shr right
            "ushr" -> left ushr right
            "==" -> if (left == right) 1 else 0
            "!=" -> if (left != right) 1 else 0
            "<" -> if (left < right) 1 else 0
            ">" -> if (left > right) 1 else 0
            "<=" -> if (left <= right) 1 else 0
            ">=" -> if (left >= right) 1 else 0
            "&&" -> if (left != 0 && right != 0) 1 else 0
            "||" -> if (left != 0 || right != 0) 1 else 0
            else -> 0
        }
    }

    private fun evalUnaryOp(op: KUnaryOp): Int {
        val value = evalExpr(op.expr)
        return when (op.op) {
            "!" -> if (value == 0) 1 else 0
            "-" -> -value
            "~", ".inv()" -> value.inv()
            else -> value
        }
    }

    private fun evalCall(call: KCall): Int {
        // Handle built-in functions
        when (call.name) {
            "MemoryByte", "MemoryByteIndexed" -> {
                // These are delegate constructors, return the address
                return call.args.firstOrNull()?.let { evalExpr(it) } ?: 0
            }
            "readWord" -> {
                val addr = call.args.firstOrNull()?.let { evalExpr(it) } ?: 0
                val low = memory[addr and 0xFFFF].toInt()
                val high = memory[(addr + 1) and 0xFFFF].toInt()
                return low or (high shl 8)
            }
            "updateZN" -> {
                val value = call.args.firstOrNull()?.let { evalExpr(it) } ?: 0
                flagZ = (value and 0xFF) == 0
                flagN = (value and 0x80) != 0
                return 0
            }
            else -> {
                // Try to call a function from the registry
                val func = functionRegistry[call.name]
                if (func != null) {
                    // Save state, execute, restore locals
                    val savedLocals = locals.toMap()
                    val savedDelegates = delegateAddresses.toMap()

                    // Set up arguments (simplified - assumes args map to registers)
                    val args = call.args.map { evalExpr(it) }
                    if (args.isNotEmpty()) A = args[0] and 0xFF
                    if (args.size > 1) X = args[1] and 0xFF
                    if (args.size > 2) Y = args[2] and 0xFF

                    locals.clear()
                    delegateAddresses.clear()

                    val savedReturned = hasReturned
                    hasReturned = false

                    for (stmt in func.body) {
                        if (hasReturned) break
                        executeStmt(stmt)
                    }

                    hasReturned = savedReturned
                    locals.clear()
                    locals.putAll(savedLocals)
                    delegateAddresses.clear()
                    delegateAddresses.putAll(savedDelegates)

                    return A // Return value in A
                }
                return 0
            }
        }
    }

    private fun evalMemberAccess(access: KMemberAccess): Int {
        if (access.isIndexed) {
            when (val receiver = access.receiver) {
                is KVar -> {
                    val name = receiver.name
                    if (name == "memory") {
                        val addr = evalExpr(access.member)
                        return memory[addr and 0xFFFF].toInt()
                    } else {
                        // Delegate indexed access
                        val baseAddr = delegateAddresses[name] ?: constants[name] ?: 0
                        val index = evalExpr(access.member)
                        return memory[(baseAddr + index) and 0xFFFF].toInt()
                    }
                }
                else -> {
                    val addr = evalExpr(access.member)
                    return memory[addr and 0xFFFF].toInt()
                }
            }
        } else {
            // Property access like x.toInt() - just evaluate the receiver
            return evalExpr(access.receiver)
        }
    }

    data class ExecutionResult(
        val A: Int,
        val X: Int,
        val Y: Int,
        val flagN: Boolean,
        val flagZ: Boolean,
        val flagC: Boolean,
        val flagV: Boolean,
        val memory: UByteArray
    )
}
