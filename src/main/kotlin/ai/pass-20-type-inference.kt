package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 20: Type Inference
 * - Infer semantic types from usage patterns
 * - Classify as boolean, counter, index, enum, etc.
 * - Distinguish signed from unsigned
 * - Identify bit flags and masks
 * - Build type constraints and unify them
 */

/**
 * Inferred type system for 6502 variables
 */
sealed class InferredType {
    // Primitive types
    data class UInt8(val range: IntRange? = 0..255) : InferredType() {
        override fun toString() = "UInt8${range?.let { "[$it]" } ?: ""}"
    }

    data class Int8(val range: IntRange? = -128..127) : InferredType() {
        override fun toString() = "Int8${range?.let { "[$it]" } ?: ""}"
    }

    data class UInt16(val range: IntRange? = 0..65535) : InferredType() {
        override fun toString() = "UInt16${range?.let { "[$it]" } ?: ""}"
    }

    object Boolean : InferredType() {
        override fun toString() = "Boolean"
    }

    data class Enum(val possibleValues: Set<Int>, val name: String? = null) : InferredType() {
        override fun toString() = "Enum${name?.let { "($it)" } ?: ""}[${possibleValues.joinToString()}]"
    }

    // Semantic types
    object Counter : InferredType() {
        override fun toString() = "Counter"
    }

    object Index : InferredType() {
        override fun toString() = "Index"
    }

    object BitFlags : InferredType() {
        override fun toString() = "BitFlags"
    }

    object Pointer : InferredType() {
        override fun toString() = "Pointer"
    }

    object Unknown : InferredType() {
        override fun toString() = "Unknown"
    }
}

/**
 * Type constraint derived from usage
 */
sealed class TypeConstraint {
    data class MustBe(val type: InferredType) : TypeConstraint()
    data class UsedInComparison(val signed: Boolean) : TypeConstraint()
    object UsedInBranch : TypeConstraint()  // Suggests boolean or flags
    object UsedAsIndex : TypeConstraint()   // Suggests index/counter
    object IncrementedDecremented : TypeConstraint()  // Counter
    object BitManipulated : TypeConstraint()  // BitFlags
    data class LimitedValues(val values: Set<Int>) : TypeConstraint()  // Enum
    data class InRange(val min: Int, val max: Int) : TypeConstraint()
}

/**
 * Variable with inferred type
 */
data class TypedVariable(
    val variable: Variable,
    val inferredType: InferredType,
    val confidence: Double,         // 0.0 - 1.0
    val constraints: List<TypeConstraint>,
    val usageCount: Int
)

/**
 * Type information for a function
 */
data class FunctionTypeInfo(
    val function: FunctionCfg,
    val variableTypes: Map<Variable, TypedVariable>,
    val parameters: List<TypedVariable>,      // Identified input parameters
    val returns: List<TypedVariable>,         // Identified return values
    val localVariables: Set<TypedVariable>
)

/**
 * Complete type inference analysis
 */
data class TypeInferenceAnalysis(
    val functions: List<FunctionTypeInfo>,
    val globalTypes: Map<Variable, TypedVariable>
)

/**
 * Perform type inference on all functions
 */
fun AssemblyCodeFile.inferTypes(
    cfg: CfgConstruction,
    dataFlow: DataFlowAnalysis,
    constants: ConstantPropagationAnalysis,
    memoryPatterns: MemoryAccessAnalysis
): TypeInferenceAnalysis {
    val functionTypeInfos = cfg.functions.mapIndexed { index, function ->
        val funcDataFlow = dataFlow.functions.getOrNull(index)
        val funcConstants = constants.functions.getOrNull(index)
        val funcMemory = memoryPatterns.functions.getOrNull(index)

        inferTypesForFunction(
            this,
            function,
            funcDataFlow,
            funcConstants,
            funcMemory
        )
    }

    // Build global type map
    val globalTypes = buildGlobalTypeMap(functionTypeInfos)

    return TypeInferenceAnalysis(
        functions = functionTypeInfos,
        globalTypes = globalTypes
    )
}

/**
 * Infer types for a single function
 */
private fun inferTypesForFunction(
    codeFile: AssemblyCodeFile,
    function: FunctionCfg,
    dataFlow: FunctionDataFlow?,
    constants: FunctionConstantAnalysis?,
    memoryPatterns: FunctionMemoryAnalysis?
): FunctionTypeInfo {
    // Collect constraints for each variable
    val constraints = collectConstraints(codeFile, function, dataFlow, constants, memoryPatterns)

    // Infer type for each variable based on constraints
    val typedVariables = constraints.mapValues { (variable, varConstraints) ->
        val inferredType = inferTypeFromConstraints(variable, varConstraints, constants, memoryPatterns)
        val confidence = calculateConfidence(varConstraints, inferredType)
        val usageCount = varConstraints.size

        TypedVariable(
            variable = variable,
            inferredType = inferredType,
            confidence = confidence,
            constraints = varConstraints,
            usageCount = usageCount
        )
    }

    // Identify parameters (live-in at function entry)
    val parameters = identifyParameters(function, dataFlow, typedVariables)

    // Identify returns (live-out at function exit)
    val returns = identifyReturns(function, dataFlow, typedVariables)

    // Identify local variables (not parameters or returns)
    val localVariables = typedVariables.values.filter { tv ->
        tv !in parameters && tv !in returns
    }.toSet()

    return FunctionTypeInfo(
        function = function,
        variableTypes = typedVariables,
        parameters = parameters,
        returns = returns,
        localVariables = localVariables
    )
}

/**
 * Collect type constraints for all variables in a function
 */
private fun collectConstraints(
    codeFile: AssemblyCodeFile,
    function: FunctionCfg,
    dataFlow: FunctionDataFlow?,
    constants: FunctionConstantAnalysis?,
    memoryPatterns: FunctionMemoryAnalysis?
): Map<Variable, List<TypeConstraint>> {
    val constraints = mutableMapOf<Variable, MutableList<TypeConstraint>>()

    function.blocks.forEach { block ->
        block.lineIndexes.forEach { lineIndex ->
            val lineRef = codeFile.get(lineIndex)
            val line = lineRef.content
            val instruction = line.instruction

            if (instruction != null) {
                extractConstraintsFromInstruction(instruction, constraints)
            }
        }
    }

    // Add constraints from constant propagation
    constants?.let { constantAnalysis ->
        addConstraintsFromConstants(constantAnalysis, constraints)
    }

    // Add constraints from memory patterns
    memoryPatterns?.let { memAnalysis ->
        addConstraintsFromMemory(memAnalysis, constraints)
    }

    return constraints
}

/**
 * Extract type constraints from a single instruction
 */
private fun extractConstraintsFromInstruction(
    instruction: AssemblyInstruction,
    constraints: MutableMap<Variable, MutableList<TypeConstraint>>
) {
    when (instruction.op) {
        // Increment/decrement suggests counter
        AssemblyOp.INX -> {
            constraints.getOrPut(Variable.RegisterX) { mutableListOf() }.add(
                TypeConstraint.IncrementedDecremented
            )
        }
        AssemblyOp.INY -> {
            constraints.getOrPut(Variable.RegisterY) { mutableListOf() }.add(
                TypeConstraint.IncrementedDecremented
            )
        }
        AssemblyOp.DEX -> {
            constraints.getOrPut(Variable.RegisterX) { mutableListOf() }.add(
                TypeConstraint.IncrementedDecremented
            )
        }
        AssemblyOp.DEY -> {
            constraints.getOrPut(Variable.RegisterY) { mutableListOf() }.add(
                TypeConstraint.IncrementedDecremented
            )
        }

        // Indexed addressing suggests index
        AssemblyOp.LDA, AssemblyOp.STA -> {
            when (instruction.address) {
                is AssemblyAddressing.DirectX -> {
                    constraints.getOrPut(Variable.RegisterX) { mutableListOf() }.add(
                        TypeConstraint.UsedAsIndex
                    )
                }
                is AssemblyAddressing.DirectY -> {
                    constraints.getOrPut(Variable.RegisterY) { mutableListOf() }.add(
                        TypeConstraint.UsedAsIndex
                    )
                }
                else -> {}
            }
        }

        // Bitwise operations suggest bit flags
        AssemblyOp.AND, AssemblyOp.ORA, AssemblyOp.EOR -> {
            constraints.getOrPut(Variable.RegisterA) { mutableListOf() }.add(
                TypeConstraint.BitManipulated
            )
        }

        // Branches after comparison
        AssemblyOp.BEQ, AssemblyOp.BNE -> {
            // Suggests boolean comparison
            constraints.getOrPut(Variable.RegisterA) { mutableListOf() }.add(
                TypeConstraint.UsedInBranch
            )
        }

        // Signed comparisons
        AssemblyOp.BMI, AssemblyOp.BPL -> {
            constraints.getOrPut(Variable.RegisterA) { mutableListOf() }.add(
                TypeConstraint.UsedInComparison(signed = true)
            )
        }

        // Unsigned comparisons
        AssemblyOp.BCC, AssemblyOp.BCS -> {
            constraints.getOrPut(Variable.RegisterA) { mutableListOf() }.add(
                TypeConstraint.UsedInComparison(signed = false)
            )
        }

        // Comparisons
        AssemblyOp.CMP -> {
            constraints.getOrPut(Variable.RegisterA) { mutableListOf() }.add(
                TypeConstraint.UsedInComparison(signed = false)
            )
        }
        AssemblyOp.CPX -> {
            constraints.getOrPut(Variable.RegisterX) { mutableListOf() }.add(
                TypeConstraint.UsedInComparison(signed = false)
            )
        }
        AssemblyOp.CPY -> {
            constraints.getOrPut(Variable.RegisterY) { mutableListOf() }.add(
                TypeConstraint.UsedInComparison(signed = false)
            )
        }

        else -> {}
    }
}

/**
 * Add constraints from constant propagation
 */
private fun addConstraintsFromConstants(
    constants: FunctionConstantAnalysis,
    constraints: MutableMap<Variable, MutableList<TypeConstraint>>
) {
    // Track observed constant values for each register
    val observedValues = mutableMapOf<Variable, MutableSet<Int>>()

    constants.blockFacts.values.forEach { facts ->
        fun addValue(variable: Variable, value: ConstantValue) {
            if (value is ConstantValue.Byte) {
                observedValues.getOrPut(variable) { mutableSetOf() }.add(value.value)
            }
        }

        addValue(Variable.RegisterA, facts.exitState.registerA)
        addValue(Variable.RegisterX, facts.exitState.registerX)
        addValue(Variable.RegisterY, facts.exitState.registerY)
    }

    // If a variable has limited distinct values, it might be an enum
    observedValues.forEach { (variable, values) ->
        if (values.size in 2..10) {  // Heuristic: 2-10 distinct values suggests enum
            constraints.getOrPut(variable) { mutableListOf() }.add(
                TypeConstraint.LimitedValues(values)
            )
        }

        // If all values are 0 or 1, suggest boolean
        if (values.all { it == 0 || it == 1 }) {
            constraints.getOrPut(variable) { mutableListOf() }.add(
                TypeConstraint.MustBe(InferredType.Boolean)
            )
        }

        // Add range constraint
        if (values.isNotEmpty()) {
            val min = values.minOrNull() ?: 0
            val max = values.maxOrNull() ?: 255
            constraints.getOrPut(variable) { mutableListOf() }.add(
                TypeConstraint.InRange(min, max)
            )
        }
    }
}

/**
 * Add constraints from memory patterns
 */
private fun addConstraintsFromMemory(
    memoryPatterns: FunctionMemoryAnalysis,
    constraints: MutableMap<Variable, MutableList<TypeConstraint>>
) {
    // If memory is used as a pointer (indirect addressing), suggest pointer type
    memoryPatterns.identifiedPointers.forEach { pointer ->
        val variable = Variable.ZeroPage(pointer.pointerAddress)
        constraints.getOrPut(variable) { mutableListOf() }.add(
            TypeConstraint.MustBe(InferredType.Pointer)
        )
    }
}

/**
 * Infer type from collected constraints
 */
private fun inferTypeFromConstraints(
    variable: Variable,
    varConstraints: List<TypeConstraint>,
    constants: FunctionConstantAnalysis?,
    memoryPatterns: FunctionMemoryAnalysis?
): InferredType {
    // Check for explicit type constraints first
    val mustBeConstraints = varConstraints.filterIsInstance<TypeConstraint.MustBe>()
    if (mustBeConstraints.isNotEmpty()) {
        return mustBeConstraints.first().type
    }

    // Count different constraint types
    val hasIncDec = varConstraints.any { it is TypeConstraint.IncrementedDecremented }
    val hasIndexUse = varConstraints.any { it is TypeConstraint.UsedAsIndex }
    val hasBitOps = varConstraints.any { it is TypeConstraint.BitManipulated }
    val hasBranch = varConstraints.any { it is TypeConstraint.UsedInBranch }
    val hasSignedCmp = varConstraints.any { it is TypeConstraint.UsedInComparison && it.signed }
    val limitedValues = varConstraints.filterIsInstance<TypeConstraint.LimitedValues>().firstOrNull()

    // Apply inference rules
    return when {
        // Boolean: used only in branches with limited values (0, 1)
        limitedValues != null && limitedValues.values.all { it == 0 || it == 1 } && hasBranch -> {
            InferredType.Boolean
        }

        // Enum: limited set of distinct values
        limitedValues != null && limitedValues.values.size in 2..10 -> {
            InferredType.Enum(limitedValues.values)
        }

        // Index: used in indexed addressing
        hasIndexUse -> {
            InferredType.Index
        }

        // Counter: incremented/decremented
        hasIncDec -> {
            InferredType.Counter
        }

        // BitFlags: used with bitwise operations
        hasBitOps -> {
            InferredType.BitFlags
        }

        // Signed: used in signed comparisons
        hasSignedCmp -> {
            InferredType.Int8()
        }

        // Default: unsigned 8-bit
        else -> {
            InferredType.UInt8()
        }
    }
}

/**
 * Calculate confidence score for inferred type
 */
private fun calculateConfidence(
    constraints: List<TypeConstraint>,
    inferredType: InferredType
): Double {
    if (constraints.isEmpty()) return 0.5  // Medium confidence for no constraints

    // Count how many constraints support the inferred type
    var supportingConstraints = 0
    var totalConstraints = constraints.size

    constraints.forEach { constraint ->
        val supports = when {
            constraint is TypeConstraint.MustBe && constraint.type == inferredType -> true
            inferredType is InferredType.Counter && constraint is TypeConstraint.IncrementedDecremented -> true
            inferredType is InferredType.Index && constraint is TypeConstraint.UsedAsIndex -> true
            inferredType is InferredType.BitFlags && constraint is TypeConstraint.BitManipulated -> true
            inferredType is InferredType.Boolean && constraint is TypeConstraint.UsedInBranch -> true
            else -> false
        }

        if (supports) supportingConstraints++
    }

    return (supportingConstraints.toDouble() / totalConstraints).coerceIn(0.0, 1.0)
}

/**
 * Identify function parameters (variables that are live-in at entry)
 */
private fun identifyParameters(
    function: FunctionCfg,
    dataFlow: FunctionDataFlow?,
    typedVariables: Map<Variable, TypedVariable>
): List<TypedVariable> {
    if (dataFlow == null) return emptyList()

    val entryBlock = function.blocks.firstOrNull() ?: return emptyList()
    val entryFacts = dataFlow.blockFacts[entryBlock.leaderIndex] ?: return emptyList()

    // Parameters are variables that are live at function entry
    return entryFacts.liveIn.mapNotNull { variable ->
        typedVariables[variable]
    }
}

/**
 * Identify function return values (variables that are live-out at exit)
 */
private fun identifyReturns(
    function: FunctionCfg,
    dataFlow: FunctionDataFlow?,
    typedVariables: Map<Variable, TypedVariable>
): List<TypedVariable> {
    if (dataFlow == null) return emptyList()

    // Find exit blocks (blocks ending with RTS/RTI)
    val exitBlocks = function.blocks.filter { block ->
        function.edges.any { edge ->
            edge.fromLeader == block.leaderIndex && edge.kind == CfgEdgeKind.RETURN
        }
    }

    // Collect variables that are live-out at any exit
    val liveOutAtExit = exitBlocks.flatMap { block ->
        val facts = dataFlow.blockFacts[block.leaderIndex]
        facts?.liveOut ?: emptySet()
    }.toSet()

    return liveOutAtExit.mapNotNull { variable ->
        typedVariables[variable]
    }
}

/**
 * Build global type map by merging function analyses
 */
private fun buildGlobalTypeMap(
    functionTypeInfos: List<FunctionTypeInfo>
): Map<Variable, TypedVariable> {
    val globalMap = mutableMapOf<Variable, TypedVariable>()

    functionTypeInfos.forEach { funcInfo ->
        funcInfo.variableTypes.forEach { (variable, typedVar) ->
            val existing = globalMap[variable]

            if (existing == null) {
                globalMap[variable] = typedVar
            } else {
                // Merge: combine constraints, pick more specific type
                val mergedConstraints = (existing.constraints + typedVar.constraints).distinct()
                val mergedType = mergeTypes(existing.inferredType, typedVar.inferredType)
                val mergedConfidence = (existing.confidence + typedVar.confidence) / 2.0

                globalMap[variable] = TypedVariable(
                    variable = variable,
                    inferredType = mergedType,
                    confidence = mergedConfidence,
                    constraints = mergedConstraints,
                    usageCount = existing.usageCount + typedVar.usageCount
                )
            }
        }
    }

    return globalMap
}

/**
 * Merge two types, preferring more specific information
 */
private fun mergeTypes(t1: InferredType, t2: InferredType): InferredType {
    // If types match, return either
    if (t1 == t2) return t1

    // Prefer non-Unknown types
    if (t1 is InferredType.Unknown) return t2
    if (t2 is InferredType.Unknown) return t1

    // Prefer more specific semantic types over generic UInt8
    if (t1 is InferredType.UInt8 && t2 !is InferredType.UInt8) return t2
    if (t2 is InferredType.UInt8 && t1 !is InferredType.UInt8) return t1

    // If both are semantic or primitive, prefer the first one (arbitrary but consistent)
    return t1
}
