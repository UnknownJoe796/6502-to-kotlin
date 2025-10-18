package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 17: Goto Elimination
 * - Transform low-level jumps into structured control flow
 * - Replace gotos with break, continue, return where possible
 * - Handle irreducible control flow
 * - Produce structured code suitable for code generation
 */

/**
 * Types of control flow statements
 */
sealed class ControlFlowStatement {
    /** Structured if-then-else */
    data class If(
        val condition: String, // Placeholder for condition expression
        val thenBody: List<ControlFlowStatement>,
        val elseBody: List<ControlFlowStatement>?
    ) : ControlFlowStatement()

    /** Structured while loop */
    data class While(
        val condition: String,
        val body: List<ControlFlowStatement>
    ) : ControlFlowStatement()

    /** Structured do-while loop */
    data class DoWhile(
        val body: List<ControlFlowStatement>,
        val condition: String
    ) : ControlFlowStatement()

    /** Break statement (exit current loop) */
    data class Break(val label: String? = null) : ControlFlowStatement()

    /** Continue statement (next loop iteration) */
    data class Continue(val label: String? = null) : ControlFlowStatement()

    /** Return statement */
    data class Return(val value: String? = null) : ControlFlowStatement()

    /** Basic block code */
    data class BlockCode(
        val blockLeader: Int,
        val instructions: List<String> // Placeholder for actual instructions
    ) : ControlFlowStatement()

    /** Sequence of statements */
    data class Sequence(
        val statements: List<ControlFlowStatement>
    ) : ControlFlowStatement()

    /** Labeled block (for break/continue targets) */
    data class Labeled(
        val label: String,
        val statement: ControlFlowStatement
    ) : ControlFlowStatement()

    /** Goto statement (fallback for irreducible control flow) */
    data class Goto(
        val target: Int,
        val reason: String // Why goto couldn't be eliminated
    ) : ControlFlowStatement()
}

/**
 * Result of goto elimination for a function
 */
data class FunctionStructuredCode(
    /** The function being analyzed */
    val function: FunctionCfg,
    /** Structured control flow statements */
    val body: List<ControlFlowStatement>,
    /** Gotos that couldn't be eliminated */
    val remainingGotos: List<ControlFlowStatement.Goto>,
    /** Whether all gotos were successfully eliminated */
    val isFullyStructured: Boolean
)

/**
 * Complete goto elimination result
 */
data class GotoElimination(
    /** Structured code for each function */
    val functions: List<FunctionStructuredCode>,
    /** Functions with remaining gotos */
    val functionsWithGotos: List<FunctionStructuredCode>
)

/**
 * Eliminate gotos using region information
 */
fun AssemblyCodeFile.eliminateGotos(
    resolution: AddressResolution = this.resolveAddresses(),
    entries: EntryPointDiscovery = this.discoverEntryPoints(resolution),
    reachability: ReachabilityReport = this.analyzeReachability(resolution, entries),
    blocks: BasicBlockConstruction = this.constructBasicBlocks(resolution, reachability, entries),
    cfg: CfgConstruction = this.constructCfg(resolution, reachability, blocks, entries),
    dominators: DominatorConstruction = this.constructDominatorTrees(cfg),
    loops: LoopDetection = this.detectLoops(resolution, entries, reachability, blocks, cfg, dominators),
    conditionals: ConditionalDetection = this.detectConditionals(resolution, entries, reachability, blocks, cfg, dominators, loops),
    regions: RegionFormation = this.formRegions(resolution, entries, reachability, blocks, cfg, dominators, loops, conditionals)
): GotoElimination {
    val functionStructuredCode = cfg.functions.mapIndexed { index, function ->
        val regionInfo = regions.functions[index]
        val loopInfo = loops.functions[index]
        val conditionalInfo = conditionals.functions[index]
        eliminateFunctionGotos(this, function, regionInfo, loopInfo, conditionalInfo)
    }

    val functionsWithGotos = functionStructuredCode.filter { !it.isFullyStructured }

    return GotoElimination(
        functions = functionStructuredCode,
        functionsWithGotos = functionsWithGotos
    )
}

/**
 * Eliminate gotos in a single function
 */
private fun eliminateFunctionGotos(
    codeFile: AssemblyCodeFile,
    function: FunctionCfg,
    regionInfo: FunctionRegionInfo,
    loopInfo: FunctionLoopInfo,
    conditionalInfo: FunctionConditionalInfo
): FunctionStructuredCode {
    // Transform regions into structured control flow
    val body = transformRegion(codeFile, regionInfo.rootRegion, loopInfo, conditionalInfo)

    // Collect remaining gotos
    val remainingGotos = mutableListOf<ControlFlowStatement.Goto>()
    fun collectGotos(stmt: ControlFlowStatement) {
        when (stmt) {
            is ControlFlowStatement.Goto -> remainingGotos.add(stmt)
            is ControlFlowStatement.If -> {
                stmt.thenBody.forEach { collectGotos(it) }
                stmt.elseBody?.forEach { collectGotos(it) }
            }
            is ControlFlowStatement.While -> stmt.body.forEach { collectGotos(it) }
            is ControlFlowStatement.DoWhile -> stmt.body.forEach { collectGotos(it) }
            is ControlFlowStatement.Sequence -> stmt.statements.forEach { collectGotos(it) }
            is ControlFlowStatement.Labeled -> collectGotos(stmt.statement)
            else -> {}
        }
    }
    body.forEach { collectGotos(it) }

    return FunctionStructuredCode(
        function = function,
        body = body,
        remainingGotos = remainingGotos,
        isFullyStructured = remainingGotos.isEmpty()
    )
}

/**
 * Transform a region into structured control flow
 */
private fun transformRegion(
    codeFile: AssemblyCodeFile,
    region: Region,
    loopInfo: FunctionLoopInfo,
    conditionalInfo: FunctionConditionalInfo
): List<ControlFlowStatement> {
    return when (region) {
        is Region.Block -> {
            // Simple block - just return block code
            listOf(ControlFlowStatement.BlockCode(
                blockLeader = region.entry,
                instructions = listOf("// Block ${region.entry} instructions")
            ))
        }

        is Region.Loop -> {
            // Transform loop based on type
            val bodyStatements = transformRegion(codeFile, region.body, loopInfo, conditionalInfo)

            when (region.loop.loopType) {
                LoopType.WHILE -> {
                    listOf(ControlFlowStatement.While(
                        condition = "loop_${region.entry}_condition",
                        body = bodyStatements
                    ))
                }
                LoopType.DO_WHILE -> {
                    listOf(ControlFlowStatement.DoWhile(
                        body = bodyStatements,
                        condition = "loop_${region.entry}_condition"
                    ))
                }
                LoopType.INFINITE -> {
                    listOf(ControlFlowStatement.While(
                        condition = "true",
                        body = bodyStatements
                    ))
                }
                else -> {
                    // Default to while for other types
                    listOf(ControlFlowStatement.While(
                        condition = "loop_${region.entry}_condition",
                        body = bodyStatements
                    ))
                }
            }
        }

        is Region.IfThenElse -> {
            // Transform conditional
            val thenStatements = transformRegion(codeFile, region.thenRegion, loopInfo, conditionalInfo)
            val elseStatements = region.elseRegion?.let {
                transformRegion(codeFile, it, loopInfo, conditionalInfo)
            }

            listOf(ControlFlowStatement.If(
                condition = "if_${region.entry}_condition",
                thenBody = thenStatements,
                elseBody = elseStatements
            ))
        }

        is Region.Sequence -> {
            // Transform each child in sequence
            val statements = region.children.flatMap {
                transformRegion(codeFile, it, loopInfo, conditionalInfo)
            }
            listOf(ControlFlowStatement.Sequence(statements))
        }

        is Region.Function -> {
            // Transform function body
            transformRegion(codeFile, region.body, loopInfo, conditionalInfo)
        }
    }
}

/**
 * Helper to check if a jump can be replaced with break
 */
private fun canUseBreak(
    target: Int,
    currentLoop: NaturalLoop?,
    loopInfo: FunctionLoopInfo
): Boolean {
    if (currentLoop == null) return false

    // Check if target is an exit target of the current loop
    return target in currentLoop.exitTargets
}

/**
 * Helper to check if a jump can be replaced with continue
 */
private fun canUseContinue(
    target: Int,
    currentLoop: NaturalLoop?,
    loopInfo: FunctionLoopInfo
): Boolean {
    if (currentLoop == null) return false

    // Check if target is the loop header
    return target == currentLoop.header
}

/**
 * Helper to check if a jump can be replaced with return
 */
private fun canUseReturn(
    edge: CfgEdge,
    function: FunctionCfg
): Boolean {
    // Check if this is a return edge
    return edge.kind == CfgEdgeKind.RETURN
}

/**
 * Helper to get structured code statistics
 */
fun FunctionStructuredCode.getStatistics(): Map<String, Int> {
    val stats = mutableMapOf(
        "ifs" to 0,
        "loops" to 0,
        "breaks" to 0,
        "continues" to 0,
        "returns" to 0,
        "gotos" to 0,
        "blocks" to 0
    )

    fun countStatements(stmt: ControlFlowStatement) {
        when (stmt) {
            is ControlFlowStatement.If -> {
                stats["ifs"] = (stats["ifs"] ?: 0) + 1
                stmt.thenBody.forEach { countStatements(it) }
                stmt.elseBody?.forEach { countStatements(it) }
            }
            is ControlFlowStatement.While, is ControlFlowStatement.DoWhile -> {
                stats["loops"] = (stats["loops"] ?: 0) + 1
                when (stmt) {
                    is ControlFlowStatement.While -> stmt.body.forEach { countStatements(it) }
                    is ControlFlowStatement.DoWhile -> stmt.body.forEach { countStatements(it) }
                    else -> {}
                }
            }
            is ControlFlowStatement.Break -> stats["breaks"] = (stats["breaks"] ?: 0) + 1
            is ControlFlowStatement.Continue -> stats["continues"] = (stats["continues"] ?: 0) + 1
            is ControlFlowStatement.Return -> stats["returns"] = (stats["returns"] ?: 0) + 1
            is ControlFlowStatement.Goto -> stats["gotos"] = (stats["gotos"] ?: 0) + 1
            is ControlFlowStatement.BlockCode -> stats["blocks"] = (stats["blocks"] ?: 0) + 1
            is ControlFlowStatement.Sequence -> stmt.statements.forEach { countStatements(it) }
            is ControlFlowStatement.Labeled -> countStatements(stmt.statement)
        }
    }

    body.forEach { countStatements(it) }
    return stats
}
