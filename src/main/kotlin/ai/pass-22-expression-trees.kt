package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 22: Expression Tree Building
 * - Reconstruct complex expressions from instruction sequences
 * - Build expression ASTs from accumulator-based operations
 * - Handle 6502-specific computation patterns
 * - Track expression flow through registers and memory
 */

/**
 * Expression AST node representing high-level computations
 */
sealed class Expression {
    // Leaf nodes
    data class Literal(val value: Int, val type: InferredType = InferredType.UInt8()) : Expression() {
        override fun toString() = when {
            value < 10 -> value.toString()
            value < 256 -> "0x${value.toString(16).uppercase()}"
            else -> "0x${value.toString(16).uppercase().padStart(4, '0')}"
        }
    }

    data class VariableRef(
        val variable: Variable,
        val type: InferredType = InferredType.Unknown
    ) : Expression() {
        override fun toString() = variable.toString()
    }

    data class MemoryAccess(
        val address: Expression,
        val type: InferredType = InferredType.UInt8()
    ) : Expression() {
        override fun toString() = "mem[$address]"
    }

    // Binary operations
    data class BinaryOp(
        val op: BinaryOperator,
        val left: Expression,
        val right: Expression
    ) : Expression() {
        override fun toString() = "($left ${op.symbol} $right)"
    }

    data class Comparison(
        val op: ComparisonOp,
        val left: Expression,
        val right: Expression
    ) : Expression() {
        override fun toString() = "($left ${op.symbol} $right)"
    }

    // Unary operations
    data class UnaryOp(
        val op: UnaryOperator,
        val operand: Expression
    ) : Expression() {
        override fun toString() = "${op.symbol}($operand)"
    }

    // Array/struct access
    data class ArrayAccess(
        val base: Expression,
        val index: Expression,
        val type: InferredType = InferredType.UInt8()
    ) : Expression() {
        override fun toString() = "$base[$index]"
    }

    data class FieldAccess(
        val base: Expression,
        val fieldName: String,
        val offset: Int
    ) : Expression() {
        override fun toString() = "$base.$fieldName"
    }

    // Function calls
    data class FunctionCall(
        val target: String,
        val arguments: List<Expression>
    ) : Expression() {
        override fun toString() = "$target(${arguments.joinToString(", ")})"
    }

    // Assignment
    data class Assignment(
        val target: Expression,
        val value: Expression
    ) : Expression() {
        override fun toString() = "$target = $value"
    }

    // Phi function (from SSA)
    data class Phi(
        val variable: SsaVariable,
        val operands: List<Pair<Int, Expression>>  // (block leader, expression)
    ) : Expression() {
        override fun toString() = "φ(${operands.joinToString { it.second.toString() }})"
    }
}

/**
 * Binary operators
 */
enum class BinaryOperator(val symbol: String) {
    ADD("+"),
    SUB("-"),
    MUL("*"),      // Recognized from idioms
    DIV("/"),      // Recognized from idioms
    MOD("%"),      // Recognized from idioms
    AND("&"),
    OR("|"),
    XOR("^"),
    SHL("<<"),
    SHR(">>"),
    ROL("rol"),    // Rotate left (6502 specific)
    ROR("ror")     // Rotate right (6502 specific)
}

/**
 * Comparison operators
 */
enum class ComparisonOp(val symbol: String) {
    EQ("=="),
    NE("!="),
    LT("<"),
    LE("<="),
    GT(">"),
    GE(">=")
}

/**
 * Unary operators
 */
enum class UnaryOperator(val symbol: String) {
    NEG("-"),
    NOT("!"),
    COMPLEMENT("~")
}

/**
 * An expression tree representing a computation
 */
data class ExpressionTree(
    val root: Expression,
    val lineRange: IntRange,           // Source lines contributing to this expression
    val ssaVariables: Set<SsaVariable> = emptySet()  // If using SSA
)

/**
 * Expression state while building trees
 */
data class ExpressionState(
    val registerA: Expression? = null,
    val registerX: Expression? = null,
    val registerY: Expression? = null,
    val memory: Map<Int, Expression> = emptyMap(),
    val flags: FlagState = FlagState()
) {
    fun withA(expr: Expression?) = copy(registerA = expr)
    fun withX(expr: Expression?) = copy(registerX = expr)
    fun withY(expr: Expression?) = copy(registerY = expr)
    fun withMemory(address: Int, expr: Expression?) = copy(
        memory = if (expr != null) memory + (address to expr) else memory - address
    )
}

/**
 * Processor flag state for expression building
 */
data class FlagState(
    val carry: Boolean? = null,
    val zero: Boolean? = null,
    val negative: Boolean? = null,
    val overflow: Boolean? = null,
    val lastComparison: Expression.Comparison? = null  // Track comparisons for branches
)

/**
 * Expressions extracted from a basic block
 */
data class BlockExpressions(
    val block: BasicBlock,
    val expressions: List<ExpressionTree>,
    val assignments: Map<Variable, Expression>,
    val entryState: ExpressionState,
    val exitState: ExpressionState
)

/**
 * Expression analysis for a function
 */
data class FunctionExpressions(
    val function: FunctionCfg,
    val blockExpressions: Map<Int, BlockExpressions>  // leader -> expressions
)

/**
 * Complete expression tree analysis
 */
data class ExpressionTreeAnalysis(
    val functions: List<FunctionExpressions>
)

/**
 * Build expression trees for all functions
 */
fun AssemblyCodeFile.buildExpressionTrees(
    cfg: CfgConstruction,
    constants: ConstantPropagationAnalysis,
    types: TypeInferenceAnalysis,
    memoryPatterns: MemoryAccessAnalysis,
    ssa: SsaConstruction? = null
): ExpressionTreeAnalysis {
    val functionExpressions = cfg.functions.mapIndexed { index, function ->
        val constAnalysis = constants.functions.getOrNull(index)
        val typeInfo = types.functions.getOrNull(index)
        val memoryAnalysis = memoryPatterns.functions.getOrNull(index)
        val ssaFunction = ssa?.functions?.getOrNull(index)

        buildFunctionExpressions(
            this,
            function,
            constAnalysis,
            typeInfo,
            memoryAnalysis,
            ssaFunction
        )
    }

    return ExpressionTreeAnalysis(functions = functionExpressions)
}

/**
 * Build expression trees for a single function
 */
private fun buildFunctionExpressions(
    codeFile: AssemblyCodeFile,
    function: FunctionCfg,
    constants: FunctionConstantAnalysis?,
    types: FunctionTypeInfo?,
    memory: FunctionMemoryAnalysis?,
    ssa: SsaFunction?
): FunctionExpressions {
    val blockExpressions = mutableMapOf<Int, BlockExpressions>()

    function.blocks.forEach { block ->
        val expressions = buildBlockExpressions(
            codeFile,
            block,
            function,
            constants,
            types,
            memory,
            ssa
        )
        blockExpressions[block.leaderIndex] = expressions
    }

    return FunctionExpressions(
        function = function,
        blockExpressions = blockExpressions
    )
}

/**
 * Build expression trees for a single basic block
 */
private fun buildBlockExpressions(
    codeFile: AssemblyCodeFile,
    block: BasicBlock,
    function: FunctionCfg,
    constants: FunctionConstantAnalysis?,
    types: FunctionTypeInfo?,
    memory: FunctionMemoryAnalysis?,
    ssa: SsaFunction?
): BlockExpressions {
    var state = ExpressionState()
    val expressions = mutableListOf<ExpressionTree>()
    val assignments = mutableMapOf<Variable, Expression>()

    val entryState = state

    block.lineIndexes.forEach { lineIndex ->
        val lineRef = codeFile.get(lineIndex)
        val line = lineRef.content
        val instruction = line.instruction ?: return@forEach

        // Get type information if available
        val getType = { variable: Variable ->
            types?.variableTypes?.get(variable)?.inferredType ?: InferredType.Unknown
        }

        // Process instruction and update expression state
        state = processInstruction(
            instruction,
            state,
            lineIndex,
            getType,
            constants,
            memory,
            expressions,
            assignments
        )
    }

    return BlockExpressions(
        block = block,
        expressions = expressions,
        assignments = assignments,
        entryState = entryState,
        exitState = state
    )
}

/**
 * Process a single instruction and update expression state
 */
private fun processInstruction(
    instruction: AssemblyInstruction,
    state: ExpressionState,
    lineIndex: Int,
    getType: (Variable) -> InferredType,
    constants: FunctionConstantAnalysis?,
    memory: FunctionMemoryAnalysis?,
    expressions: MutableList<ExpressionTree>,
    assignments: MutableMap<Variable, Expression>
): ExpressionState {
    return when (instruction.op) {
        // Load operations - create leaf expressions
        AssemblyOp.LDA -> {
            val expr = createLoadExpression(instruction.address, state, getType)
            state.withA(expr)
        }

        AssemblyOp.LDX -> {
            val expr = createLoadExpression(instruction.address, state, getType)
            state.withX(expr)
        }

        AssemblyOp.LDY -> {
            val expr = createLoadExpression(instruction.address, state, getType)
            state.withY(expr)
        }

        // Store operations - create assignments
        AssemblyOp.STA -> {
            val target = createStoreTarget(instruction.address, state, getType)
            if (state.registerA != null && target != null) {
                expressions.add(
                    ExpressionTree(
                        root = Expression.Assignment(target, state.registerA),
                        lineRange = lineIndex..lineIndex
                    )
                )
                if (target is Expression.VariableRef) {
                    assignments[target.variable] = state.registerA
                }
            }
            state
        }

        AssemblyOp.STX -> {
            val target = createStoreTarget(instruction.address, state, getType)
            if (state.registerX != null && target != null) {
                expressions.add(
                    ExpressionTree(
                        root = Expression.Assignment(target, state.registerX),
                        lineRange = lineIndex..lineIndex
                    )
                )
                if (target is Expression.VariableRef) {
                    assignments[target.variable] = state.registerX
                }
            }
            state
        }

        AssemblyOp.STY -> {
            val target = createStoreTarget(instruction.address, state, getType)
            if (state.registerY != null && target != null) {
                expressions.add(
                    ExpressionTree(
                        root = Expression.Assignment(target, state.registerY),
                        lineRange = lineIndex..lineIndex
                    )
                )
                if (target is Expression.VariableRef) {
                    assignments[target.variable] = state.registerY
                }
            }
            state
        }

        // Register transfers
        AssemblyOp.TAX -> state.withX(state.registerA)
        AssemblyOp.TAY -> state.withY(state.registerA)
        AssemblyOp.TXA -> state.withA(state.registerX)
        AssemblyOp.TYA -> state.withA(state.registerY)
        AssemblyOp.TSX -> state.withX(null)  // Stack pointer not tracked as expression
        AssemblyOp.TXS -> state  // Stack pointer update

        // Arithmetic operations
        AssemblyOp.ADC -> {
            val operand = createLoadExpression(instruction.address, state, getType)
            if (state.registerA != null && operand != null) {
                val expr = Expression.BinaryOp(BinaryOperator.ADD, state.registerA, operand)
                state.withA(expr)
            } else {
                state.withA(null)  // Unknown result
            }
        }

        AssemblyOp.SBC -> {
            val operand = createLoadExpression(instruction.address, state, getType)
            if (state.registerA != null && operand != null) {
                val expr = Expression.BinaryOp(BinaryOperator.SUB, state.registerA, operand)
                state.withA(expr)
            } else {
                state.withA(null)
            }
        }

        // Logical operations
        AssemblyOp.AND -> {
            val operand = createLoadExpression(instruction.address, state, getType)
            if (state.registerA != null && operand != null) {
                val expr = Expression.BinaryOp(BinaryOperator.AND, state.registerA, operand)
                state.withA(expr)
            } else {
                state.withA(null)
            }
        }

        AssemblyOp.ORA -> {
            val operand = createLoadExpression(instruction.address, state, getType)
            if (state.registerA != null && operand != null) {
                val expr = Expression.BinaryOp(BinaryOperator.OR, state.registerA, operand)
                state.withA(expr)
            } else {
                state.withA(null)
            }
        }

        AssemblyOp.EOR -> {
            val operand = createLoadExpression(instruction.address, state, getType)
            if (state.registerA != null && operand != null) {
                val expr = Expression.BinaryOp(BinaryOperator.XOR, state.registerA, operand)
                state.withA(expr)
            } else {
                state.withA(null)
            }
        }

        // Shift and rotate
        AssemblyOp.ASL -> {
            if (instruction.address == null || instruction.address is AssemblyAddressing.Accumulator) {
                if (state.registerA != null) {
                    val expr = Expression.BinaryOp(
                        BinaryOperator.SHL,
                        state.registerA,
                        Expression.Literal(1)
                    )
                    state.withA(expr)
                } else {
                    state.withA(null)
                }
            } else {
                state  // Memory shift not tracked
            }
        }

        AssemblyOp.LSR -> {
            if (instruction.address == null || instruction.address is AssemblyAddressing.Accumulator) {
                if (state.registerA != null) {
                    val expr = Expression.BinaryOp(
                        BinaryOperator.SHR,
                        state.registerA,
                        Expression.Literal(1)
                    )
                    state.withA(expr)
                } else {
                    state.withA(null)
                }
            } else {
                state
            }
        }

        AssemblyOp.ROL -> {
            if (instruction.address == null || instruction.address is AssemblyAddressing.Accumulator) {
                if (state.registerA != null) {
                    val expr = Expression.BinaryOp(
                        BinaryOperator.ROL,
                        state.registerA,
                        Expression.Literal(1)
                    )
                    state.withA(expr)
                } else {
                    state.withA(null)
                }
            } else {
                state
            }
        }

        AssemblyOp.ROR -> {
            if (instruction.address == null || instruction.address is AssemblyAddressing.Accumulator) {
                if (state.registerA != null) {
                    val expr = Expression.BinaryOp(
                        BinaryOperator.ROR,
                        state.registerA,
                        Expression.Literal(1)
                    )
                    state.withA(expr)
                } else {
                    state.withA(null)
                }
            } else {
                state
            }
        }

        // Increment/decrement
        AssemblyOp.INX -> {
            if (state.registerX != null) {
                val expr = Expression.BinaryOp(
                    BinaryOperator.ADD,
                    state.registerX,
                    Expression.Literal(1)
                )
                state.withX(expr)
            } else {
                state.withX(null)
            }
        }

        AssemblyOp.INY -> {
            if (state.registerY != null) {
                val expr = Expression.BinaryOp(
                    BinaryOperator.ADD,
                    state.registerY,
                    Expression.Literal(1)
                )
                state.withY(expr)
            } else {
                state.withY(null)
            }
        }

        AssemblyOp.DEX -> {
            if (state.registerX != null) {
                val expr = Expression.BinaryOp(
                    BinaryOperator.SUB,
                    state.registerX,
                    Expression.Literal(1)
                )
                state.withX(expr)
            } else {
                state.withX(null)
            }
        }

        AssemblyOp.DEY -> {
            if (state.registerY != null) {
                val expr = Expression.BinaryOp(
                    BinaryOperator.SUB,
                    state.registerY,
                    Expression.Literal(1)
                )
                state.withY(expr)
            } else {
                state.withY(null)
            }
        }

        // Comparisons - don't change registers, but track for branches
        AssemblyOp.CMP -> {
            val operand = createLoadExpression(instruction.address, state, getType)
            state  // Comparisons tracked separately for branches
        }

        AssemblyOp.CPX -> state
        AssemblyOp.CPY -> state

        // Flag operations
        AssemblyOp.CLC, AssemblyOp.SEC, AssemblyOp.CLV,
        AssemblyOp.SEI, AssemblyOp.CLI, AssemblyOp.SED, AssemblyOp.CLD -> state

        // Stack operations - lose tracking
        AssemblyOp.PHA, AssemblyOp.PHP -> state
        AssemblyOp.PLA -> state.withA(null)
        AssemblyOp.PLP -> state

        // Control flow
        AssemblyOp.JMP, AssemblyOp.JSR, AssemblyOp.RTS, AssemblyOp.RTI -> {
            if (instruction.op == AssemblyOp.JSR) {
                // Function call - may clobber everything
                ExpressionState()
            } else {
                state
            }
        }

        // Branches - no state change
        AssemblyOp.BCC, AssemblyOp.BCS, AssemblyOp.BEQ, AssemblyOp.BMI,
        AssemblyOp.BNE, AssemblyOp.BPL, AssemblyOp.BVC, AssemblyOp.BVS -> state

        // Other instructions
        AssemblyOp.BIT -> state
        AssemblyOp.NOP -> state
        AssemblyOp.BRK -> state

        else -> state
    }
}

/**
 * Create an expression for a load operation
 */
private fun createLoadExpression(
    addressing: AssemblyAddressing?,
    state: ExpressionState,
    getType: (Variable) -> InferredType
): Expression? {
    return when (addressing) {
        is AssemblyAddressing.ValueHex -> {
            Expression.Literal(addressing.value.toInt() and 0xFF)
        }

        is AssemblyAddressing.ValueBinary -> {
            Expression.Literal(addressing.value.toInt() and 0xFF)
        }

        is AssemblyAddressing.ValueDecimal -> {
            Expression.Literal(addressing.value.toInt() and 0xFF)
        }

        is AssemblyAddressing.Label -> {
            // Try to parse as address
            val address = parseAddress(addressing.label)
            if (address != null) {
                Expression.MemoryAccess(
                    Expression.Literal(address),
                    InferredType.UInt8()
                )
            } else {
                // Named variable
                Expression.VariableRef(
                    Variable.Memory(addressing.label.hashCode()),
                    getType(Variable.Memory(addressing.label.hashCode()))
                )
            }
        }

        is AssemblyAddressing.DirectX -> {
            // Array access: base[X]
            val baseAddr = parseAddress(addressing.label) ?: 0
            Expression.ArrayAccess(
                Expression.Literal(baseAddr),
                state.registerX ?: Expression.VariableRef(Variable.RegisterX)
            )
        }

        is AssemblyAddressing.DirectY -> {
            // Array access: base[Y]
            val baseAddr = parseAddress(addressing.label) ?: 0
            Expression.ArrayAccess(
                Expression.Literal(baseAddr),
                state.registerY ?: Expression.VariableRef(Variable.RegisterY)
            )
        }

        is AssemblyAddressing.IndirectX -> {
            // Indexed indirect: (ptr,X)
            Expression.MemoryAccess(
                Expression.VariableRef(Variable.Indirect(parseAddress(addressing.label) ?: 0))
            )
        }

        is AssemblyAddressing.IndirectY -> {
            // Indirect indexed: (ptr),Y
            val ptrAddr = parseAddress(addressing.label) ?: 0
            Expression.ArrayAccess(
                Expression.MemoryAccess(Expression.Literal(ptrAddr)),
                state.registerY ?: Expression.VariableRef(Variable.RegisterY)
            )
        }

        is AssemblyAddressing.Accumulator -> {
            state.registerA
        }

        null -> null

        else -> null
    }
}

/**
 * Create a target expression for a store operation
 */
private fun createStoreTarget(
    addressing: AssemblyAddressing?,
    state: ExpressionState,
    getType: (Variable) -> InferredType
): Expression? {
    return when (addressing) {
        is AssemblyAddressing.Label -> {
            val address = parseAddress(addressing.label)
            if (address != null) {
                Expression.MemoryAccess(
                    Expression.Literal(address),
                    InferredType.UInt8()
                )
            } else {
                Expression.VariableRef(
                    Variable.Memory(addressing.label.hashCode()),
                    getType(Variable.Memory(addressing.label.hashCode()))
                )
            }
        }

        is AssemblyAddressing.DirectX -> {
            val baseAddr = parseAddress(addressing.label) ?: 0
            Expression.ArrayAccess(
                Expression.Literal(baseAddr),
                state.registerX ?: Expression.VariableRef(Variable.RegisterX)
            )
        }

        is AssemblyAddressing.DirectY -> {
            val baseAddr = parseAddress(addressing.label) ?: 0
            Expression.ArrayAccess(
                Expression.Literal(baseAddr),
                state.registerY ?: Expression.VariableRef(Variable.RegisterY)
            )
        }

        is AssemblyAddressing.IndirectY -> {
            val ptrAddr = parseAddress(addressing.label) ?: 0
            Expression.ArrayAccess(
                Expression.MemoryAccess(Expression.Literal(ptrAddr)),
                state.registerY ?: Expression.VariableRef(Variable.RegisterY)
            )
        }

        else -> null
    }
}

/**
 * Parse address from label string
 */
private fun parseAddress(label: String): Int? {
    return try {
        when {
            label.startsWith("$") -> label.substring(1).toInt(16)
            label.startsWith("0x") -> label.substring(2).toInt(16)
            label.all { it.isDigit() } -> label.toInt()
            else -> null
        }
    } catch (e: NumberFormatException) {
        null
    }
}
