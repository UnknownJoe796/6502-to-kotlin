package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 28: Variable Naming
 * - Generate meaningful, readable names for variables
 * - Use original assembly labels where available
 * - Infer names from usage patterns and types
 * - Resolve naming conflicts
 * - Apply Kotlin naming conventions
 */

/**
 * A variable with an assigned name
 */
data class NamedVariable(
    val variable: IdentifiedVariable,
    val name: String,
    val nameSource: NameSource,
    val confidence: Double  // 0.0-1.0: How confident we are in this name
)

/**
 * Source of a variable name
 */
sealed class NameSource {
    object OriginalLabel : NameSource() {
        override fun toString() = "OriginalLabel"
    }
    object InferredFromUsage : NameSource() {
        override fun toString() = "InferredFromUsage"
    }
    object InferredFromType : NameSource() {
        override fun toString() = "InferredFromType"
    }
    object Generic : NameSource() {
        override fun toString() = "Generic"
    }
}

/**
 * Naming conventions configuration
 */
data class NamingConventions(
    val registerPrefix: String = "reg",
    val tempPrefix: String = "temp",
    val counterPrefix: String = "counter",
    val indexPrefix: String = "index",
    val flagPrefix: String = "flag",
    val pointerPrefix: String = "ptr",
    val addressPrefix: String = "var",
    val paramPrefix: String = "param",
    val useCamelCase: Boolean = true,
    val avoidKeywords: Boolean = true
)

/**
 * Variable naming result
 */
data class VariableNaming(
    val namedVariables: Map<VariableId, NamedVariable>,
    val namingConflicts: List<NamingConflict>
)

/**
 * A naming conflict that was resolved
 */
data class NamingConflict(
    val variables: List<IdentifiedVariable>,
    val conflictingName: String,
    val resolution: String
)

/**
 * Perform variable naming
 */
fun VariableIdentification.assignNames(
    resolution: AddressResolution,
    conventions: NamingConventions = NamingConventions()
): VariableNaming {
    val namedVariables = mutableMapOf<VariableId, NamedVariable>()
    val conflicts = mutableListOf<NamingConflict>()
    val usedNames = mutableSetOf<String>()

    // First, name global variables
    globals.forEach { variable ->
        val namedVar = nameVariable(variable, resolution, conventions, usedNames)
        namedVariables[variable.id] = namedVar
        usedNames.add(namedVar.name)
    }

    // Then, name variables per function
    functions.forEach { funcVars ->
        val functionUsedNames = usedNames.toMutableSet()

        // Name parameters first (they have priority)
        funcVars.parameters.forEach { variable ->
            val namedVar = nameVariable(variable, resolution, conventions, functionUsedNames)
            namedVariables[variable.id] = namedVar
            functionUsedNames.add(namedVar.name)
        }

        // Then return values
        funcVars.returnValues.forEach { variable ->
            val namedVar = nameVariable(variable, resolution, conventions, functionUsedNames)
            namedVariables[variable.id] = namedVar
            functionUsedNames.add(namedVar.name)
        }

        // Finally, local variables
        funcVars.localVariables.forEach { variable ->
            val namedVar = nameVariable(variable, resolution, conventions, functionUsedNames)
            namedVariables[variable.id] = namedVar
            functionUsedNames.add(namedVar.name)
        }
    }

    return VariableNaming(
        namedVariables = namedVariables,
        namingConflicts = conflicts
    )
}

/**
 * Generate a name for a single variable
 */
private fun nameVariable(
    variable: IdentifiedVariable,
    resolution: AddressResolution,
    conventions: NamingConventions,
    usedNames: MutableSet<String>
): NamedVariable {
    // Try different naming strategies in priority order
    val strategies = listOf(
        { nameFromOriginalLabel(variable, resolution, conventions) },
        { nameFromUsageContext(variable, conventions) },
        { nameFromType(variable, conventions) },
        { nameGeneric(variable, conventions) }
    )

    for (strategy in strategies) {
        val (name, source, confidence) = strategy()
        if (name != null) {
            val finalName = makeNameUnique(name, usedNames, conventions)
            return NamedVariable(
                variable = variable,
                name = finalName,
                nameSource = source,
                confidence = confidence
            )
        }
    }

    // Fallback (should never happen)
    val fallbackName = makeNameUnique("unknown", usedNames, conventions)
    return NamedVariable(
        variable = variable,
        name = fallbackName,
        nameSource = NameSource.Generic,
        confidence = 0.0
    )
}

/**
 * Try to name from original assembly label
 */
private fun nameFromOriginalLabel(
    variable: IdentifiedVariable,
    resolution: AddressResolution,
    conventions: NamingConventions
): Triple<String?, NameSource, Double> {
    // Get address from variable ID
    val address = when (val id = variable.id) {
        is VariableId.Memory -> id.address
        is VariableId.MultiByteMemory -> id.baseAddress
        else -> return Triple(null, NameSource.OriginalLabel, 0.0)
    }

    // Look for label at this address (reverse lookup)
    val label = resolution.labelToAddress.entries.find { it.value == address }?.key
    if (label != null && !label.startsWith("L_")) {  // Skip auto-generated labels
        val cleanName = cleanLabelName(label, conventions)
        return Triple(cleanName, NameSource.OriginalLabel, 1.0)
    }

    return Triple(null, NameSource.OriginalLabel, 0.0)
}

/**
 * Try to name from usage context
 */
private fun nameFromUsageContext(
    variable: IdentifiedVariable,
    conventions: NamingConventions
): Triple<String?, NameSource, Double> {
    // Check loop roles
    val loopRoles = variable.usageSites.mapNotNull { it.context.loopRole }
    if (loopRoles.isNotEmpty()) {
        val name = when (loopRoles.first()) {
            LoopRole.INDUCTION_VARIABLE -> "${conventions.indexPrefix}${getIndexSuffix(variable)}"
            LoopRole.LOOP_BOUND -> "limit"
            LoopRole.LOOP_ACCUMULATOR -> "accumulator"
        }
        return Triple(name, NameSource.InferredFromUsage, 0.8)
    }

    return Triple(null, NameSource.InferredFromUsage, 0.0)
}

/**
 * Try to name from inferred type
 */
private fun nameFromType(
    variable: IdentifiedVariable,
    conventions: NamingConventions
): Triple<String?, NameSource, Double> {
    val name = when (variable.inferredType) {
        is InferredType.Boolean -> "${conventions.flagPrefix}${getSuffix(variable)}"
        is InferredType.Counter -> "${conventions.counterPrefix}${getSuffix(variable)}"
        is InferredType.Index -> "${conventions.indexPrefix}${getSuffix(variable)}"
        is InferredType.Pointer -> "${conventions.pointerPrefix}${getSuffix(variable)}"
        is InferredType.BitFlags -> "flags${getSuffix(variable)}"
        else -> null
    }

    if (name != null) {
        return Triple(name, NameSource.InferredFromType, 0.6)
    }

    return Triple(null, NameSource.InferredFromType, 0.0)
}

/**
 * Generate a generic name based on memory location
 */
private fun nameGeneric(
    variable: IdentifiedVariable,
    conventions: NamingConventions
): Triple<String?, NameSource, Double> {
    val name = when (val id = variable.id) {
        is VariableId.Register -> {
            val reg = when (id.reg) {
                Variable.RegisterA -> "a"
                Variable.RegisterX -> "x"
                Variable.RegisterY -> "y"
                else -> "reg"
            }
            "${conventions.registerPrefix}${reg.capitalize()}"
        }
        is VariableId.Memory -> {
            "${conventions.addressPrefix}_${id.address.toString(16).uppercase().padStart(4, '0')}"
        }
        is VariableId.MultiByteMemory -> {
            "${conventions.addressPrefix}16_${id.baseAddress.toString(16).uppercase().padStart(4, '0')}"
        }
        is VariableId.ArrayElement -> {
            "array${id.index ?: ""}"
        }
    }

    return Triple(name, NameSource.Generic, 0.3)
}

/**
 * Clean up an assembly label to follow Kotlin conventions
 */
private fun cleanLabelName(label: String, conventions: NamingConventions): String {
    // Remove special characters and convert to camelCase
    var name = label
        .replace("_", " ")
        .split(" ")
        .filter { it.isNotEmpty() }
        .mapIndexed { index, word ->
            if (conventions.useCamelCase && index > 0) {
                word.lowercase().replaceFirstChar { it.uppercase() }
            } else {
                word.lowercase()
            }
        }
        .joinToString("")

    // Avoid Kotlin keywords
    if (conventions.avoidKeywords && isKotlinKeyword(name)) {
        name = "_$name"
    }

    return name
}

/**
 * Make a name unique by adding numeric suffixes
 */
private fun makeNameUnique(
    baseName: String,
    usedNames: Set<String>,
    conventions: NamingConventions
): String {
    if (baseName !in usedNames) {
        return baseName
    }

    // Try adding numeric suffixes
    var counter = 2
    while (true) {
        val candidateName = "$baseName$counter"
        if (candidateName !in usedNames) {
            return candidateName
        }
        counter++
        if (counter > 1000) {
            // Safety check
            return "${baseName}_${System.currentTimeMillis()}"
        }
    }
}

/**
 * Get a suffix based on variable's memory location
 */
private fun getSuffix(variable: IdentifiedVariable): String {
    return when (val id = variable.id) {
        is VariableId.Memory -> {
            val addr = id.address
            when {
                addr < 0x100 -> ""  // Zero page - no suffix
                else -> "_${addr.toString(16).uppercase()}"
            }
        }
        is VariableId.MultiByteMemory -> "_${id.baseAddress.toString(16).uppercase()}"
        else -> ""
    }
}

/**
 * Get an index suffix (i, j, k, then numeric)
 */
private fun getIndexSuffix(variable: IdentifiedVariable): String {
    // For now, just use empty suffix - could be enhanced to use i/j/k
    return ""
}

/**
 * Check if a name is a Kotlin keyword
 */
private fun isKotlinKeyword(name: String): Boolean {
    val keywords = setOf(
        "as", "break", "class", "continue", "do", "else", "false", "for",
        "fun", "if", "in", "interface", "is", "null", "object", "package",
        "return", "super", "this", "throw", "true", "try", "typealias",
        "typeof", "val", "var", "when", "while"
    )
    return name in keywords
}

/**
 * Capitalize first character (for older Kotlin versions)
 */
private fun String.capitalize(): String {
    return this.replaceFirstChar { it.uppercase() }
}
