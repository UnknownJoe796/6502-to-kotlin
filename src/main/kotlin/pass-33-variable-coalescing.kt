package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 33: Variable Coalescing & Lifetime Analysis
 * - Compute precise variable lifetimes
 * - Merge variables with non-overlapping lifetimes
 * - Minimize variable scope (function → block → loop)
 * - Reduce number of variables for cleaner decompiled code
 *
 * This pass is critical for reducing the variable count from Phase 7's
 * analysis (which can identify 1000+ variables) to a manageable set.
 */

/**
 * Live range of a variable (continuous region where it's live)
 */
data class LiveRange(
    val startLine: Int,  // First line where variable becomes live
    val endLine: Int,    // Last line where variable is live
    val blockLeaders: Set<Int>  // Blocks where this range is active
) {
    fun overlaps(other: LiveRange): Boolean {
        // Check for any overlap in line ranges
        return !(endLine < other.startLine || other.endLine < startLine)
    }
}

/**
 * Complete lifetime information for a variable
 */
data class VariableLifetime(
    val variable: IdentifiedVariable,
    val liveRanges: List<LiveRange>,
    val minimalScope: VariableScope,
    val firstUse: Int,  // Line index of first use
    val lastUse: Int    // Line index of last use
)

/**
 * Reason for coalescing two variables
 */
enum class CoalescingReason {
    NON_OVERLAPPING_LIFETIMES,  // Lifetimes don't overlap
    SAME_TYPE_SAME_SCOPE,       // Same type and scope, sequential usage
    COPY_RELATIONSHIP,           // One is a copy of the other
    REGISTER_REUSE              // Different uses of same register
}

/**
 * Opportunity to coalesce two variables
 */
data class CoalescingOpportunity(
    val variable1: VariableId,
    val variable2: VariableId,
    val reason: CoalescingReason,
    val benefit: Int,  // Higher = more beneficial to coalesce
    val mergedLifetime: VariableLifetime
)

/**
 * Result of coalescing variables
 */
data class CoalescedVariable(
    val mergedId: VariableId,
    val originalVariables: List<VariableId>,
    val lifetime: VariableLifetime,
    val reason: CoalescingReason
)

/**
 * Variable coalescing analysis for a function
 */
data class FunctionVariableCoalescing(
    val function: FunctionCfg,
    val variableLifetimes: List<VariableLifetime>,
    val coalescingOpportunities: List<CoalescingOpportunity>,
    val coalescedVariables: List<CoalescedVariable>,
    val originalVariableCount: Int,
    val finalVariableCount: Int
)

/**
 * Statistics for variable coalescing
 */
data class VariableCoalescingStats(
    val totalOriginalVariables: Int,
    val totalFinalVariables: Int,
    val variablesCoalesced: Int,
    val scopesMinimized: Int,
    val coalescingsByReason: Map<CoalescingReason, Int>
)

/**
 * Complete variable coalescing result
 */
data class VariableCoalescingResult(
    val functionsCoalesced: List<FunctionVariableCoalescing>,
    val globalStats: VariableCoalescingStats
)

/**
 * Perform variable coalescing on all functions
 */
fun VariableIdentification.coalesceVariables(
    cfg: CfgConstruction,
    dataFlow: DataFlowAnalysis
): VariableCoalescingResult {
    val functionsCoalesced = cfg.functions.mapIndexed { index, function ->
        val functionVars = this.functions.getOrNull(index)
        val funcDataFlow = dataFlow.functions.getOrNull(index)

        if (functionVars != null && funcDataFlow != null) {
            coalesceVariablesForFunction(function, functionVars, funcDataFlow)
        } else {
            // No variables for this function
            FunctionVariableCoalescing(
                function = function,
                variableLifetimes = emptyList(),
                coalescingOpportunities = emptyList(),
                coalescedVariables = emptyList(),
                originalVariableCount = 0,
                finalVariableCount = 0
            )
        }
    }

    val globalStats = VariableCoalescingStats(
        totalOriginalVariables = functionsCoalesced.sumOf { it.originalVariableCount },
        totalFinalVariables = functionsCoalesced.sumOf { it.finalVariableCount },
        variablesCoalesced = functionsCoalesced.sumOf { it.originalVariableCount - it.finalVariableCount },
        scopesMinimized = functionsCoalesced.sumOf { it.variableLifetimes.count { lt -> lt.minimalScope != VariableScope.Global } },
        coalescingsByReason = functionsCoalesced
            .flatMap { it.coalescedVariables }
            .groupBy { it.reason }
            .mapValues { it.value.size }
    )

    return VariableCoalescingResult(
        functionsCoalesced = functionsCoalesced,
        globalStats = globalStats
    )
}

/**
 * Coalesce variables for a single function
 */
private fun coalesceVariablesForFunction(
    function: FunctionCfg,
    functionVars: FunctionVariables,
    dataFlow: FunctionDataFlow
): FunctionVariableCoalescing {

    val allVariables = functionVars.localVariables + functionVars.parameters + functionVars.returnValues

    // Step 1: Compute lifetimes for each variable
    val lifetimes = allVariables.map { variable ->
        computeVariableLifetime(variable, function, dataFlow)
    }

    // Step 2: Find coalescing opportunities
    val opportunities = findCoalescingOpportunities(lifetimes, functionVars)

    // Step 3: Apply coalescing (greedy approach: take best opportunities first)
    val coalesced = applyCoalescing(opportunities, lifetimes)

    val finalVariableCount = allVariables.size - coalesced.sumOf { it.originalVariables.size - 1 }

    return FunctionVariableCoalescing(
        function = function,
        variableLifetimes = lifetimes,
        coalescingOpportunities = opportunities,
        coalescedVariables = coalesced,
        originalVariableCount = allVariables.size,
        finalVariableCount = finalVariableCount
    )
}

/**
 * Compute the lifetime of a variable
 */
private fun computeVariableLifetime(
    variable: IdentifiedVariable,
    function: FunctionCfg,
    dataFlow: FunctionDataFlow
): VariableLifetime {

    val usages = variable.usageSites
    if (usages.isEmpty()) {
        // Variable never used - minimal lifetime
        return VariableLifetime(
            variable = variable,
            liveRanges = emptyList(),
            minimalScope = variable.scope,
            firstUse = -1,
            lastUse = -1
        )
    }

    // Extract line indexes from usage sites
    val lineIndexes = usages.map { it.lineRef.line }
    val firstUse = lineIndexes.minOrNull() ?: -1
    val lastUse = lineIndexes.maxOrNull() ?: -1

    // Build live ranges from liveness data
    val liveRanges = buildLiveRanges(variable, lineIndexes, function, dataFlow)

    // Determine minimal scope
    val minimalScope = determineMinimalScope(variable, liveRanges, function)

    return VariableLifetime(
        variable = variable,
        liveRanges = liveRanges,
        minimalScope = minimalScope,
        firstUse = firstUse,
        lastUse = lastUse
    )
}

/**
 * Build live ranges from usage sites
 */
private fun buildLiveRanges(
    variable: IdentifiedVariable,
    lineIndexes: List<Int>,
    function: FunctionCfg,
    dataFlow: FunctionDataFlow
): List<LiveRange> {
    if (lineIndexes.isEmpty()) return emptyList()

    // Simple approach: create one live range from first to last use
    // A more sophisticated approach would use liveness analysis to find gaps
    val firstLine = lineIndexes.minOrNull() ?: return emptyList()
    val lastLine = lineIndexes.maxOrNull() ?: return emptyList()

    // Find all blocks that contain these lines
    val blockLeaders = lineIndexes.mapNotNull { lineIdx ->
        function.blocks.find { block ->
            lineIdx in block.lineIndexes
        }?.leaderIndex
    }.toSet()

    return listOf(
        LiveRange(
            startLine = firstLine,
            endLine = lastLine,
            blockLeaders = blockLeaders
        )
    )
}

/**
 * Determine the minimal scope for a variable
 */
private fun determineMinimalScope(
    variable: IdentifiedVariable,
    liveRanges: List<LiveRange>,
    function: FunctionCfg
): VariableScope {
    if (liveRanges.isEmpty()) return variable.scope

    // If all uses are in a single block, minimize to that block
    val blockLeaders = liveRanges.flatMap { it.blockLeaders }.toSet()
    if (blockLeaders.size == 1) {
        return VariableScope.Block(blockLeaders.first())
    }

    // Otherwise, keep function scope
    return VariableScope.Function(function.entryAddress)
}

/**
 * Find opportunities to coalesce variables
 */
private fun findCoalescingOpportunities(
    lifetimes: List<VariableLifetime>,
    functionVars: FunctionVariables
): List<CoalescingOpportunity> {

    val opportunities = mutableListOf<CoalescingOpportunity>()

    // Compare all pairs of variables
    for (i in lifetimes.indices) {
        for (j in i + 1 until lifetimes.size) {
            val lt1 = lifetimes[i]
            val lt2 = lifetimes[j]

            // Check if lifetimes overlap
            val overlaps = lt1.liveRanges.any { r1 ->
                lt2.liveRanges.any { r2 -> r1.overlaps(r2) }
            }

            if (!overlaps) {
                // Variables don't overlap - can coalesce if compatible
                if (areCompatible(lt1.variable, lt2.variable)) {
                    val benefit = calculateCoalescingBenefit(lt1, lt2)
                    val reason = determineCoalescingReason(lt1, lt2)

                    opportunities.add(
                        CoalescingOpportunity(
                            variable1 = lt1.variable.id,
                            variable2 = lt2.variable.id,
                            reason = reason,
                            benefit = benefit,
                            mergedLifetime = mergeLifetimes(lt1, lt2)
                        )
                    )
                }
            }
        }
    }

    // Sort by benefit (higher is better)
    return opportunities.sortedByDescending { it.benefit }
}

/**
 * Check if two variables are compatible for coalescing
 */
private fun areCompatible(var1: IdentifiedVariable, var2: IdentifiedVariable): Boolean {
    // Must have same type
    if (var1.inferredType != var2.inferredType) return false

    // Parameters can't be coalesced with locals
    if (var1.scope is VariableScope.Parameter && var2.scope !is VariableScope.Parameter) return false
    if (var2.scope is VariableScope.Parameter && var1.scope !is VariableScope.Parameter) return false

    // Return values can't be coalesced with locals
    if (var1.scope is VariableScope.ReturnValue && var2.scope !is VariableScope.ReturnValue) return false
    if (var2.scope is VariableScope.ReturnValue && var1.scope !is VariableScope.ReturnValue) return false

    return true
}

/**
 * Calculate benefit of coalescing (higher = better)
 */
private fun calculateCoalescingBenefit(lt1: VariableLifetime, lt2: VariableLifetime): Int {
    var benefit = 0

    // Benefit for same memory location
    val sharedMemory = lt1.variable.memoryLocations.intersect(lt2.variable.memoryLocations)
    benefit += sharedMemory.size * 10

    // Benefit for sequential usage (one ends, other starts)
    if (lt1.lastUse < lt2.firstUse || lt2.lastUse < lt1.firstUse) {
        benefit += 5
    }

    // Benefit for same scope
    if (lt1.minimalScope == lt2.minimalScope) {
        benefit += 3
    }

    return benefit
}

/**
 * Determine reason for coalescing
 */
private fun determineCoalescingReason(lt1: VariableLifetime, lt2: VariableLifetime): CoalescingReason {
    // Check for register reuse
    if (lt1.variable.id is VariableId.Register && lt2.variable.id is VariableId.Register) {
        if ((lt1.variable.id as VariableId.Register).reg == (lt2.variable.id as VariableId.Register).reg) {
            return CoalescingReason.REGISTER_REUSE
        }
    }

    // Check for sequential usage
    if (lt1.lastUse < lt2.firstUse || lt2.lastUse < lt1.firstUse) {
        if (lt1.minimalScope == lt2.minimalScope && lt1.variable.inferredType == lt2.variable.inferredType) {
            return CoalescingReason.SAME_TYPE_SAME_SCOPE
        }
    }

    return CoalescingReason.NON_OVERLAPPING_LIFETIMES
}

/**
 * Merge two lifetimes
 */
private fun mergeLifetimes(lt1: VariableLifetime, lt2: VariableLifetime): VariableLifetime {
    val mergedRanges = lt1.liveRanges + lt2.liveRanges

    return VariableLifetime(
        variable = lt1.variable,  // Use first variable as base
        liveRanges = mergedRanges,
        minimalScope = if (lt1.minimalScope == lt2.minimalScope) lt1.minimalScope else VariableScope.Function(0),
        firstUse = minOf(lt1.firstUse, lt2.firstUse),
        lastUse = maxOf(lt1.lastUse, lt2.lastUse)
    )
}

/**
 * Apply coalescing greedily
 */
private fun applyCoalescing(
    opportunities: List<CoalescingOpportunity>,
    lifetimes: List<VariableLifetime>
): List<CoalescedVariable> {

    val coalesced = mutableListOf<CoalescedVariable>()
    val alreadyCoalesced = mutableSetOf<VariableId>()

    for (opportunity in opportunities) {
        // Skip if either variable already coalesced
        if (opportunity.variable1 in alreadyCoalesced || opportunity.variable2 in alreadyCoalesced) {
            continue
        }

        // Create coalesced variable
        coalesced.add(
            CoalescedVariable(
                mergedId = opportunity.variable1,  // Use first as representative
                originalVariables = listOf(opportunity.variable1, opportunity.variable2),
                lifetime = opportunity.mergedLifetime,
                reason = opportunity.reason
            )
        )

        alreadyCoalesced.add(opportunity.variable1)
        alreadyCoalesced.add(opportunity.variable2)
    }

    return coalesced
}
