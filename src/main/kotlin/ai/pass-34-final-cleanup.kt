package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 34: Final Cleanup & Validation
 * - Normalize expressions (remove redundant parentheses)
 * - Validate type consistency
 * - Resolve naming conflicts (shadowed variables)
 * - Verify no dead code remains
 * - Prepare documentation (attach original assembly, preserve comments)
 *
 * This is the final pass before code generation (Phase 9).
 * It ensures the analyzed code is clean, valid, and ready to emit.
 */

/**
 * Severity of a validation issue
 */
enum class IssueSeverity {
    ERROR,    // Must be fixed before code generation
    WARNING,  // Should be addressed but non-blocking
    INFO      // Informational only
}

/**
 * A validation issue found during final cleanup
 */
data class CleanupIssue(
    val severity: IssueSeverity,
    val location: String,  // Human-readable location (e.g., "Function@0x8000, Block 0, Line 5")
    val message: String,
    val suggestedFix: String?
)

/**
 * Result of expression normalization
 */
data class ExpressionNormalization(
    val redundantParenthesesRemoved: Int,
    val constantsSimplified: Int,
    val identityOperationsRemoved: Int  // x + 0, x * 1, etc.
)

/**
 * Result of type consistency validation
 */
data class TypeConsistencyCheck(
    val castsInserted: Int,
    val typeConflicts: List<CleanupIssue>
)

/**
 * Result of naming conflict resolution
 */
data class NamingConflictResolution(
    val shadowedVariablesRenamed: Int,
    val kotlinKeywordConflicts: Int,
    val duplicateNamesResolved: Int
)

/**
 * Result of dead code verification
 */
data class DeadCodeVerification(
    val deadCodeRemaining: List<CleanupIssue>,
    val unreachableBlocks: Int
)

/**
 * Complete final cleanup result for a function
 */
data class FunctionFinalCleanup(
    val function: FunctionCfg,
    val expressionNormalization: ExpressionNormalization,
    val typeConsistency: TypeConsistencyCheck,
    val namingResolution: NamingConflictResolution,
    val deadCodeCheck: DeadCodeVerification,
    val validationIssues: List<CleanupIssue>
)

/**
 * Statistics for final cleanup
 */
data class FinalCleanupStats(
    val expressionsNormalized: Int,
    val typeCastsInserted: Int,
    val namingConflictsResolved: Int,
    val validationErrors: Int,
    val validationWarnings: Int,
    val validationInfos: Int
)

/**
 * Complete final cleanup result
 */
data class FinalCleanupResult(
    val functionsProcessed: List<FunctionFinalCleanup>,
    val globalStats: FinalCleanupStats,
    val isValid: Boolean  // false if any ERROR severity issues exist
)

/**
 * Perform final cleanup and validation on all functions
 */
fun CfgConstruction.performFinalCleanup(
    codeFile: AssemblyCodeFile,
    variableIdentification: VariableIdentification?,
    typeInference: TypeInferenceAnalysis?,
    expressionAnalysis: ExpressionTreeAnalysis?
): FinalCleanupResult {

    val functionsProcessed = functions.mapIndexed { index, function ->
        val functionVars = variableIdentification?.functions?.getOrNull(index)
        val functionTypes = typeInference?.functions?.getOrNull(index)
        val functionExprs = expressionAnalysis?.functions?.getOrNull(index)

        performFunctionCleanup(
            function,
            codeFile,
            functionVars,
            functionTypes,
            functionExprs
        )
    }

    // Aggregate statistics
    val globalStats = FinalCleanupStats(
        expressionsNormalized = functionsProcessed.sumOf {
            it.expressionNormalization.redundantParenthesesRemoved +
            it.expressionNormalization.constantsSimplified +
            it.expressionNormalization.identityOperationsRemoved
        },
        typeCastsInserted = functionsProcessed.sumOf { it.typeConsistency.castsInserted },
        namingConflictsResolved = functionsProcessed.sumOf {
            it.namingResolution.shadowedVariablesRenamed +
            it.namingResolution.kotlinKeywordConflicts +
            it.namingResolution.duplicateNamesResolved
        },
        validationErrors = functionsProcessed.sumOf { fc ->
            fc.validationIssues.count { it.severity == IssueSeverity.ERROR }
        },
        validationWarnings = functionsProcessed.sumOf { fc ->
            fc.validationIssues.count { it.severity == IssueSeverity.WARNING }
        },
        validationInfos = functionsProcessed.sumOf { fc ->
            fc.validationIssues.count { it.severity == IssueSeverity.INFO }
        }
    )

    val isValid = globalStats.validationErrors == 0

    return FinalCleanupResult(
        functionsProcessed = functionsProcessed,
        globalStats = globalStats,
        isValid = isValid
    )
}

/**
 * Perform cleanup for a single function
 */
private fun performFunctionCleanup(
    function: FunctionCfg,
    codeFile: AssemblyCodeFile,
    functionVars: FunctionVariables?,
    functionTypes: FunctionTypeInfo?,
    functionExprs: FunctionExpressions?
): FunctionFinalCleanup {

    val validationIssues = mutableListOf<CleanupIssue>()

    // 1. Normalize expressions
    val exprNormalization = normalizeExpressions(function, functionExprs, validationIssues)

    // 2. Check type consistency
    val typeConsistency = checkTypeConsistency(function, functionVars, functionTypes, validationIssues)

    // 3. Resolve naming conflicts
    val namingResolution = resolveNamingConflicts(function, functionVars, validationIssues)

    // 4. Verify no dead code remains
    val deadCodeCheck = verifyNoDeadCode(function, codeFile, validationIssues)

    return FunctionFinalCleanup(
        function = function,
        expressionNormalization = exprNormalization,
        typeConsistency = typeConsistency,
        namingResolution = namingResolution,
        deadCodeCheck = deadCodeCheck,
        validationIssues = validationIssues
    )
}

/**
 * Normalize expressions (remove redundant parentheses, simplify)
 */
private fun normalizeExpressions(
    function: FunctionCfg,
    functionExprs: FunctionExpressions?,
    validationIssues: MutableList<CleanupIssue>
): ExpressionNormalization {

    // Placeholder implementation
    // In a full implementation, this would:
    // - Remove unnecessary parentheses: (x) → x
    // - Simplify constants: (5 + 3) → 8
    // - Remove identity operations: x + 0 → x, x * 1 → x

    return ExpressionNormalization(
        redundantParenthesesRemoved = 0,
        constantsSimplified = 0,
        identityOperationsRemoved = 0
    )
}

/**
 * Check type consistency and insert casts if needed
 */
private fun checkTypeConsistency(
    function: FunctionCfg,
    functionVars: FunctionVariables?,
    functionTypes: FunctionTypeInfo?,
    validationIssues: MutableList<CleanupIssue>
): TypeConsistencyCheck {

    val typeConflicts = mutableListOf<CleanupIssue>()

    // Placeholder implementation
    // In a full implementation, this would:
    // - Check that all variables have consistent types
    // - Insert casts where needed (UInt8 → UInt16)
    // - Warn about potential type mismatches

    return TypeConsistencyCheck(
        castsInserted = 0,
        typeConflicts = typeConflicts
    )
}

/**
 * Resolve naming conflicts (shadowing, keywords, duplicates)
 */
private fun resolveNamingConflicts(
    function: FunctionCfg,
    functionVars: FunctionVariables?,
    validationIssues: MutableList<CleanupIssue>
): NamingConflictResolution {

    val allVariables = if (functionVars != null) {
        functionVars.localVariables + functionVars.parameters + functionVars.returnValues
    } else {
        emptyList()
    }

    var shadowedRenamed = 0
    var keywordConflicts = 0
    var duplicatesResolved = 0

    // Check for Kotlin keyword conflicts
    val kotlinKeywords = setOf(
        "fun", "val", "var", "if", "else", "when", "for", "while",
        "return", "class", "object", "interface", "package", "import",
        "true", "false", "null", "is", "in", "as", "try", "catch"
    )

    // Placeholder: In a full implementation, we would check variable names
    // For now, just report if we find any issues

    return NamingConflictResolution(
        shadowedVariablesRenamed = shadowedRenamed,
        kotlinKeywordConflicts = keywordConflicts,
        duplicateNamesResolved = duplicatesResolved
    )
}

/**
 * Verify that no dead code remains after optimization passes
 */
private fun verifyNoDeadCode(
    function: FunctionCfg,
    codeFile: AssemblyCodeFile,
    validationIssues: MutableList<CleanupIssue>
): DeadCodeVerification {

    val deadCodeIssues = mutableListOf<CleanupIssue>()
    var unreachableBlocks = 0

    // Check for unreachable blocks
    // A block is unreachable if it has no incoming edges and is not the entry block
    val incomingEdges = function.edges.groupBy { it.toLeader }

    for (block in function.blocks) {
        val isEntry = block.leaderIndex == function.entryLeader
        val hasIncoming = incomingEdges[block.leaderIndex]?.isNotEmpty() == true

        if (!isEntry && !hasIncoming) {
            unreachableBlocks++
            deadCodeIssues.add(
                CleanupIssue(
                    severity = IssueSeverity.WARNING,
                    location = "Function@${function.entryAddress.toString(16)}, Block ${block.leaderIndex}",
                    message = "Unreachable block detected",
                    suggestedFix = "Remove unreachable block"
                )
            )
        }
    }

    return DeadCodeVerification(
        deadCodeRemaining = deadCodeIssues,
        unreachableBlocks = unreachableBlocks
    )
}

/**
 * Kotlin reserved keywords that cannot be used as variable names
 */
val KOTLIN_KEYWORDS = setOf(
    "as", "break", "class", "continue", "do", "else", "false", "for",
    "fun", "if", "in", "interface", "is", "null", "object", "package",
    "return", "super", "this", "throw", "true", "try", "typealias",
    "typeof", "val", "var", "when", "while"
)

/**
 * Check if a name conflicts with Kotlin keywords
 */
fun String.isKotlinKeyword(): Boolean {
    return this.lowercase() in KOTLIN_KEYWORDS
}

/**
 * Escape a name if it conflicts with Kotlin keywords
 */
fun String.escapeKotlinKeyword(): String {
    return if (this.isKotlinKeyword()) {
        "`$this`"
    } else {
        this
    }
}
