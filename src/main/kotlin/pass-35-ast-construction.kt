package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 35: Kotlin AST Construction
 * - Build a Kotlin-specific AST from all Phase 1-8 analysis results
 * - Organize code structure (file, functions, variables)
 * - Prepare for idiomatic Kotlin emission in Passes 36-39
 *
 * This AST is separate from Pass 22's expression trees - it represents
 * the target Kotlin code structure, not the source assembly expressions.
 */

/**
 * Root of Kotlin AST - represents a complete .kt file
 */
sealed class KotlinAst {

    /**
     * A complete Kotlin source file
     */
    data class File(
        val packageName: String,
        val imports: List<Import>,
        val declarations: List<Declaration>,
        val header: FileHeader
    ) : KotlinAst()

    /**
     * File-level header information
     */
    data class FileHeader(
        val comment: String,
        val originalSource: String,
        val suppressions: List<String> = listOf(
            "UNUSED_VARIABLE",
            "UNUSED_PARAMETER",
            "MagicNumber",
            "VariableNaming",
            "FunctionNaming"
        )
    )

    /**
     * Import statement
     */
    data class Import(
        val packagePath: String,
        val name: String? = null  // null = import entire package
    )

    /**
     * Top-level declarations (functions, properties, classes)
     */
    sealed class Declaration : KotlinAst() {

        /**
         * Function declaration
         */
        data class Function(
            val name: String,
            val parameters: List<Parameter>,
            val returnType: KotlinType?,
            val body: Block,
            val kdoc: String? = null,
            val annotations: List<Annotation> = emptyList(),
            val visibility: Visibility = Visibility.PUBLIC
        ) : Declaration()

        /**
         * Property (variable) declaration
         */
        data class Property(
            val name: String,
            val type: KotlinType,
            val initializer: Expression?,
            val isVar: Boolean,  // true = var, false = val
            val kdoc: String? = null,
            val visibility: Visibility = Visibility.PUBLIC
        ) : Declaration()

        /**
         * Data class declaration (for multi-byte structures)
         */
        data class DataClass(
            val name: String,
            val properties: List<Property>,
            val kdoc: String? = null
        ) : Declaration()

        /**
         * Enum class declaration (for state machines, etc.)
         */
        data class EnumClass(
            val name: String,
            val entries: List<EnumEntry>,
            val kdoc: String? = null
        ) : Declaration()
    }

    /**
     * Statements (inside functions)
     */
    sealed class Statement : KotlinAst() {

        /**
         * Assignment statement
         */
        data class Assignment(
            val target: Expression,
            val value: Expression
        ) : Statement()

        /**
         * If-then-else statement
         */
        data class If(
            val condition: Expression,
            val thenBranch: Block,
            val elseBranch: Block? = null
        ) : Statement()

        /**
         * When expression/statement
         */
        data class When(
            val subject: Expression?,
            val branches: List<WhenBranch>
        ) : Statement()

        /**
         * While loop
         */
        data class While(
            val condition: Expression,
            val body: Block,
            val label: String? = null
        ) : Statement()

        /**
         * For loop
         */
        data class For(
            val variable: String,
            val range: Expression,
            val body: Block
        ) : Statement()

        /**
         * Return statement
         */
        data class Return(
            val value: Expression? = null
        ) : Statement()

        /**
         * Expression statement (expression evaluated for side effects)
         */
        data class ExpressionStatement(
            val expression: Expression
        ) : Statement()

        /**
         * Comment (including original assembly)
         */
        data class Comment(
            val text: String,
            val originalAssembly: List<AssemblyLine> = emptyList()
        ) : Statement()

        /**
         * Variable declaration
         */
        data class LocalVariable(
            val name: String,
            val type: KotlinType?,
            val initializer: Expression?,
            val isVar: Boolean
        ) : Statement()
    }

    /**
     * Expressions
     */
    sealed class Expression : KotlinAst() {

        /**
         * Literal value
         */
        data class Literal(
            val value: Any,  // Int, String, Boolean, etc.
            val type: KotlinType
        ) : Expression()

        /**
         * Variable reference
         */
        data class Variable(
            val name: String
        ) : Expression()

        /**
         * Binary operation (a + b, a && b, etc.)
         */
        data class Binary(
            val left: Expression,
            val operator: BinaryOp,
            val right: Expression
        ) : Expression()

        /**
         * Unary operation (!, -, etc.)
         */
        data class Unary(
            val operator: UnaryOp,
            val operand: Expression
        ) : Expression()

        /**
         * Function call
         */
        data class Call(
            val name: String,
            val arguments: List<Expression>
        ) : Expression()

        /**
         * Property access (obj.property)
         */
        data class PropertyAccess(
            val receiver: Expression,
            val propertyName: String
        ) : Expression()

        /**
         * Array access (array[index])
         */
        data class ArrayAccess(
            val array: Expression,
            val index: Expression
        ) : Expression()

        /**
         * Type cast (value as Type or value.toType())
         */
        data class Cast(
            val expression: Expression,
            val targetType: KotlinType,
            val isSafe: Boolean = false  // true = as?, false = as
        ) : Expression()

        /**
         * If expression (val x = if (cond) a else b)
         */
        data class IfExpression(
            val condition: Expression,
            val thenValue: Expression,
            val elseValue: Expression
        ) : Expression()

        /**
         * When expression (val x = when(y) { ... })
         */
        data class WhenExpression(
            val subject: Expression?,
            val branches: List<WhenBranch>
        ) : Expression()
    }

    /**
     * Kotlin types
     */
    sealed class KotlinType {
        object Unit : KotlinType() {
            override fun toString() = "Unit"
        }
        object Int : KotlinType() {
            override fun toString() = "Int"
        }
        object UByte : KotlinType() {
            override fun toString() = "UByte"
        }
        object UShort : KotlinType() {
            override fun toString() = "UShort"
        }
        object Boolean : KotlinType() {
            override fun toString() = "Boolean"
        }
        object String : KotlinType() {
            override fun toString() = "String"
        }
        object ByteArray : KotlinType() {
            override fun toString() = "ByteArray"
        }
        data class Array(val elementType: KotlinType) : KotlinType()
        data class Custom(val typeName: String) : KotlinType()
        data class Nullable(val type: KotlinType) : KotlinType()
    }

    /**
     * Binary operators
     */
    enum class BinaryOp(val symbol: String) {
        // Arithmetic
        PLUS("+"),
        MINUS("-"),
        TIMES("*"),
        DIV("/"),
        MOD("%"),

        // Bitwise
        AND("and"),
        OR("or"),
        XOR("xor"),
        SHL("shl"),
        SHR("shr"),

        // Comparison
        EQ("=="),
        NE("!="),
        LT("<"),
        LE("<="),
        GT(">"),
        GE(">="),

        // Logical
        LOGICAL_AND("&&"),
        LOGICAL_OR("||"),

        // Assignment
        ASSIGN("="),
        PLUS_ASSIGN("+="),
        MINUS_ASSIGN("-="),
        TIMES_ASSIGN("*="),
        DIV_ASSIGN("/="),
        AND_ASSIGN("&="),
        OR_ASSIGN("|="),
        XOR_ASSIGN("^=")
    }

    /**
     * Unary operators
     */
    enum class UnaryOp(val symbol: String) {
        NOT("!"),
        MINUS("-"),
        PLUS("+"),
        INV("inv()")
    }

    /**
     * Visibility modifiers
     */
    enum class Visibility {
        PUBLIC, PRIVATE, INTERNAL, PROTECTED
    }

    /**
     * Block of statements
     */
    data class Block(
        val statements: List<Statement>
    )

    /**
     * Function parameter
     */
    data class Parameter(
        val name: String,
        val type: KotlinType,
        val defaultValue: Expression? = null
    )

    /**
     * Annotation
     */
    data class Annotation(
        val name: String,
        val arguments: Map<String, String> = emptyMap()
    )

    /**
     * When branch
     */
    data class WhenBranch(
        val condition: WhenCondition,
        val body: Block
    )

    /**
     * When condition
     */
    sealed class WhenCondition {
        data class Value(val expression: Expression) : WhenCondition()
        object Else : WhenCondition()
    }

    /**
     * Enum entry
     */
    data class EnumEntry(
        val name: String,
        val value: Int? = null
    )
}

/**
 * Builder for constructing Kotlin AST from analysis results
 */
class KotlinAstBuilder(
    val packageName: String = "decompiled",
    val originalSource: String = "assembly"
) {
    private val imports = mutableListOf<KotlinAst.Import>()
    private val declarations = mutableListOf<KotlinAst.Declaration>()

    /**
     * Add an import
     */
    fun addImport(packagePath: String, name: String? = null) {
        imports.add(KotlinAst.Import(packagePath, name))
    }

    /**
     * Add a function declaration
     */
    fun addFunction(
        name: String,
        parameters: List<KotlinAst.Parameter> = emptyList(),
        returnType: KotlinAst.KotlinType? = null,
        body: KotlinAst.Block,
        kdoc: String? = null
    ) {
        declarations.add(
            KotlinAst.Declaration.Function(
                name = name,
                parameters = parameters,
                returnType = returnType,
                body = body,
                kdoc = kdoc
            )
        )
    }

    /**
     * Add a property declaration
     */
    fun addProperty(
        name: String,
        type: KotlinAst.KotlinType,
        initializer: KotlinAst.Expression?,
        isVar: Boolean = true,
        kdoc: String? = null
    ) {
        declarations.add(
            KotlinAst.Declaration.Property(
                name = name,
                type = type,
                initializer = initializer,
                isVar = isVar,
                kdoc = kdoc
            )
        )
    }

    /**
     * Build the complete file AST
     */
    fun build(): KotlinAst.File {
        return KotlinAst.File(
            packageName = packageName,
            imports = imports,
            declarations = declarations,
            header = KotlinAst.FileHeader(
                comment = "Decompiled from $originalSource",
                originalSource = originalSource
            )
        )
    }
}

/**
 * Construct Kotlin AST from analysis results
 */
fun CfgConstruction.constructKotlinAst(
    codeFile: AssemblyCodeFile,
    variableAnalysis: VariableIdentification? = null,
    typeAnalysis: TypeInferenceAnalysis? = null,
    expressionAnalysis: ExpressionTreeAnalysis? = null,
    parameterRecovery: ParameterRecovery? = null
): KotlinAst.File {

    val builder = KotlinAstBuilder(
        packageName = "decompiled",
        originalSource = codeFile.file?.name ?: "assembly"
    )

    // Add necessary imports
    builder.addImport("kotlin.experimental", "and")
    builder.addImport("kotlin.experimental", "or")
    builder.addImport("kotlin.experimental", "xor")

    // Add memory array
    builder.addProperty(
        name = "memory",
        type = KotlinAst.KotlinType.ByteArray,
        initializer = KotlinAst.Expression.Call(
            name = "ByteArray",
            arguments = listOf(
                KotlinAst.Expression.Literal(65536, KotlinAst.KotlinType.Int)
            )
        ),
        isVar = false,
        kdoc = "64KB address space"
    )

    // Always add CPU registers as globals for complete decompilation
    // These are shared state across all 6502 functions
    addCpuRegisters(builder)

    // Add CPU flags
    addCpuFlags(builder)

    // Add global variables with memory abstraction if available
    if (variableAnalysis != null) {
        // Create memory access strategy
        val memoryStrategy = variableAnalysis.createMemoryAccessStrategy(null)

        // Add typed memory accessors for global variables
        // These will have custom getters/setters backed by memory array
        memoryStrategy.globalVariables.forEach { typedVar ->
            addTypedMemoryVariable(builder, typedVar)
        }
    } else {
        // Legacy: add global variables directly
        variableAnalysis?.globals?.forEach { variable ->
            addGlobalVariable(builder, variable, typeAnalysis)
        }
    }

    // Add functions
    functions.forEach { function ->
        val funcVars = variableAnalysis?.functions?.find { it.function.entryAddress == function.entryAddress }
        val funcSig = parameterRecovery?.functionSignatures?.find { it.function.entryAddress == function.entryAddress }
        addFunction(builder, function, codeFile, expressionAnalysis, funcVars, funcSig)
    }

    return builder.build()
}

/**
 * Add CPU register declarations
 */
/**
 * Add CPU registers as globals (legacy mode when no variable analysis)
 */
private fun addCpuRegisters(builder: KotlinAstBuilder) {
    builder.addProperty(
        name = "A",
        type = KotlinAst.KotlinType.Int,
        initializer = KotlinAst.Expression.Literal(0, KotlinAst.KotlinType.Int),
        isVar = true,
        kdoc = "Accumulator"
    )

    builder.addProperty(
        name = "X",
        type = KotlinAst.KotlinType.Int,
        initializer = KotlinAst.Expression.Literal(0, KotlinAst.KotlinType.Int),
        isVar = true,
        kdoc = "Index X"
    )

    builder.addProperty(
        name = "Y",
        type = KotlinAst.KotlinType.Int,
        initializer = KotlinAst.Expression.Literal(0, KotlinAst.KotlinType.Int),
        isVar = true,
        kdoc = "Index Y"
    )

    builder.addProperty(
        name = "SP",
        type = KotlinAst.KotlinType.Int,
        initializer = KotlinAst.Expression.Literal(0xFF, KotlinAst.KotlinType.Int),
        isVar = true,
        kdoc = "Stack pointer"
    )
}

/**
 * Add only CPU registers that are truly global (used by multiple functions)
 */
private fun addGlobalCpuRegisters(builder: KotlinAstBuilder, variableAnalysis: VariableIdentification) {
    val globalRegisters = variableAnalysis.globals.filter { it.id is VariableId.Register }

    for (registerVar in globalRegisters) {
        val register = (registerVar.id as VariableId.Register).reg
        val (name, initialValue, doc) = when (register) {
            Variable.RegisterA -> Triple("A", 0, "Accumulator (global)")
            Variable.RegisterX -> Triple("X", 0, "Index X (global)")
            Variable.RegisterY -> Triple("Y", 0, "Index Y (global)")
            Variable.StackPointer -> Triple("SP", 0xFF, "Stack pointer (global)")
            else -> continue
        }

        builder.addProperty(
            name = name,
            type = KotlinAst.KotlinType.Int,
            initializer = KotlinAst.Expression.Literal(initialValue, KotlinAst.KotlinType.Int),
            isVar = true,
            kdoc = doc
        )
    }
}

/**
 * Add CPU flag declarations
 */
private fun addCpuFlags(builder: KotlinAstBuilder) {
    listOf(
        "flagN" to "Negative",
        "flagV" to "Overflow",
        "flagZ" to "Zero",
        "flagC" to "Carry"
    ).forEach { (name, desc) ->
        builder.addProperty(
            name = name,
            type = KotlinAst.KotlinType.Boolean,
            initializer = KotlinAst.Expression.Literal(false, KotlinAst.KotlinType.Boolean),
            isVar = true,
            kdoc = desc
        )
    }
}

/**
 * Add a global variable from analysis
 */
/**
 * Add a typed memory variable with memory-backed accessor
 */
private fun addTypedMemoryVariable(
    builder: KotlinAstBuilder,
    typedVar: TypedMemoryVariable
) {
    // For now, add as a regular property
    // Future: emit custom getters/setters backed by memory array
    builder.addProperty(
        name = typedVar.name,
        type = typedVar.type,
        initializer = getDefaultInitializer(typedVar.type),
        isVar = typedVar.isMutable,
        kdoc = typedVar.kdoc
    )
}

private fun addGlobalVariable(
    builder: KotlinAstBuilder,
    variable: IdentifiedVariable,
    typeAnalysis: TypeInferenceAnalysis?
) {
    val kotlinType = mapInferredTypeToKotlinType(variable.inferredType)
    val name = generateVariableName(variable)

    builder.addProperty(
        name = name,
        type = kotlinType,
        initializer = getDefaultInitializer(kotlinType),
        isVar = true,
        kdoc = "Variable at ${variable.id}"
    )
}

/**
 * Add a function to the AST
 */
private fun addFunction(
    builder: KotlinAstBuilder,
    function: FunctionCfg,
    codeFile: AssemblyCodeFile,
    expressionAnalysis: ExpressionTreeAnalysis?,
    funcVars: FunctionVariables?,
    funcSig: FunctionSignature?
) {
    val functionName = funcSig?.name?.sanitizeKotlinIdentifier()
        ?: function.entryLabel?.sanitizeKotlinIdentifier()
        ?: "function_${function.entryAddress.toString(16).uppercase()}"

    // Build function body statements
    val bodyStatements = mutableListOf<KotlinAst.Statement>()

    // Add function-local CPU registers based on variable analysis
    if (funcVars != null) {
        // Find which registers are local to this function (not parameters)
        val parameterRegisters = funcSig?.parameters?.mapNotNull { param ->
            (param.location as? ParameterLocation.Register)?.register
        }?.toSet() ?: emptySet()

        val localRegisters = funcVars.localVariables.filter {
            it.id is VariableId.Register &&
            (it.id as VariableId.Register).reg !in parameterRegisters
        }

        for (registerVar in localRegisters) {
            val register = (registerVar.id as VariableId.Register).reg
            val (name, initialValue, doc) = when (register) {
                Variable.RegisterA -> Triple("A", 0, "Accumulator")
                Variable.RegisterX -> Triple("X", 0, "Index X")
                Variable.RegisterY -> Triple("Y", 0, "Index Y")
                Variable.StackPointer -> Triple("SP", 0xFF, "Stack pointer")
                else -> continue
            }

            bodyStatements.add(
                KotlinAst.Statement.LocalVariable(
                    name = name,
                    type = KotlinAst.KotlinType.Int,
                    initializer = KotlinAst.Expression.Literal(initialValue, KotlinAst.KotlinType.Int),
                    isVar = true
                )
            )
        }
    }

    // Add placeholder comment
    bodyStatements.add(KotlinAst.Statement.Comment("Function body - to be filled by Pass 37"))
    bodyStatements.add(KotlinAst.Statement.Return())

    val body = KotlinAst.Block(statements = bodyStatements)

    // Convert Pass 29 parameters to Kotlin AST parameters
    val parameters = funcSig?.parameters?.map { param ->
        KotlinAst.Parameter(
            name = param.name,
            type = mapInferredTypeToKotlinType(param.type)
        )
    } ?: emptyList()

    // Get return type from Pass 29
    val returnType = funcSig?.returnValue?.let {
        mapInferredTypeToKotlinType(it.type)
    }

    builder.addFunction(
        name = functionName,
        parameters = parameters,
        returnType = returnType,
        body = body,
        kdoc = funcSig?.let { buildFunctionKdoc(it) }
            ?: "Function at 0x${function.entryAddress.toString(16).uppercase()}"
    )
}

/**
 * Build KDoc from function signature
 */
private fun buildFunctionKdoc(signature: FunctionSignature): String {
    val builder = StringBuilder()
    builder.append("Function at 0x${signature.function.entryAddress.toString(16).uppercase()}")

    if (signature.parameters.isNotEmpty()) {
        builder.append("\n\nParameters:")
        signature.parameters.forEach { param ->
            builder.append("\n  - ${param.name}: ${param.type} (${param.location})")
        }
    }

    if (signature.returnValue != null) {
        builder.append("\n\nReturns: ${signature.returnValue.type} (${signature.returnValue.location})")
    }

    if (signature.sideEffects.isNotEmpty()) {
        builder.append("\n\nSide effects:")
        signature.sideEffects.forEach { effect ->
            builder.append("\n  - $effect")
        }
    }

    return builder.toString()
}

/**
 * Map InferredType to KotlinType
 */
internal fun mapInferredTypeToKotlinType(type: InferredType): KotlinAst.KotlinType {
    return when (type) {
        is InferredType.UInt8 -> KotlinAst.KotlinType.UByte
        is InferredType.UInt16 -> KotlinAst.KotlinType.UShort
        is InferredType.Int8 -> KotlinAst.KotlinType.Int
        is InferredType.Boolean -> KotlinAst.KotlinType.Boolean
        is InferredType.Pointer -> KotlinAst.KotlinType.UShort
        else -> KotlinAst.KotlinType.Int  // Default fallback
    }
}

/**
 * Generate variable name from IdentifiedVariable
 */
private fun generateVariableName(variable: IdentifiedVariable): String {
    return when (val id = variable.id) {
        is VariableId.Memory -> "var_${id.address.toString(16).uppercase()}"
        is VariableId.MultiByteMemory -> "var_${id.baseAddress.toString(16).uppercase()}_${id.size}byte"
        is VariableId.Register -> id.reg.toString().lowercase()
        is VariableId.ArrayElement -> "array_element"
    }
}

/**
 * Get default initializer for a type
 */
private fun getDefaultInitializer(type: KotlinAst.KotlinType): KotlinAst.Expression {
    return when (type) {
        is KotlinAst.KotlinType.Int -> KotlinAst.Expression.Literal(0, type)
        is KotlinAst.KotlinType.UByte -> KotlinAst.Expression.Literal(0u, type)
        is KotlinAst.KotlinType.UShort -> KotlinAst.Expression.Literal(0u, type)
        is KotlinAst.KotlinType.Boolean -> KotlinAst.Expression.Literal(false, type)
        is KotlinAst.KotlinType.String -> KotlinAst.Expression.Literal("", type)
        else -> KotlinAst.Expression.Literal(0, KotlinAst.KotlinType.Int)
    }
}

/**
 * Sanitize identifier to be valid Kotlin
 */
internal fun String.sanitizeKotlinIdentifier(): String {
    val sanitized = this.replace(Regex("[^a-zA-Z0-9_]"), "_")

    val withPrefix = if (sanitized.firstOrNull()?.isDigit() == true) {
        "_$sanitized"
    } else {
        sanitized
    }

    // Escape Kotlin keywords by wrapping in backticks
    return if (withPrefix.isKotlinKeyword()) {
        "`$withPrefix`"
    } else {
        withPrefix
    }
}
