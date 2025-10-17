package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 11: SSA Form Construction
 * - Convert to Static Single Assignment form using dominator frontiers
 * - Insert φ (phi) functions at join points
 * - Rename variables to ensure single assignment property
 * - Handle 6502-specific concerns (registers, memory locations, flags)
 */

/**
 * Represents a variable in SSA form with a unique version number
 */
data class SsaVariable(
    /** The base variable (register, memory location, etc.) */
    val baseVariable: Variable,
    /** Version number for SSA form (0 for original, 1+ for renamed) */
    val version: Int
) {
    override fun toString(): String = "${baseVariable}_$version"
}

/**
 * A φ (phi) function that merges multiple definitions at join points
 */
data class PhiFunction(
    /** The SSA variable being defined by this φ function */
    val result: SsaVariable,
    /** List of (predecessor block leader, SSA variable) pairs */
    val operands: List<Pair<Int, SsaVariable>>,
    /** The basic block leader where this φ function is placed */
    val blockLeader: Int
)

/**
 * An SSA instruction with renamed variables
 */
data class SsaInstruction(
    /** The original line reference */
    val lineRef: AssemblyLineReference,
    /** The original instruction */
    val originalInstruction: AssemblyInstruction,
    /** Variables defined by this instruction (in SSA form) */
    val defines: List<SsaVariable>,
    /** Variables used by this instruction (in SSA form) */
    val uses: List<SsaVariable>,
    /** The basic block leader containing this instruction */
    val blockLeader: Int
)

/**
 * SSA form representation of a basic block
 */
data class SsaBasicBlock(
    /** Original basic block */
    val originalBlock: BasicBlock,
    /** φ functions at the beginning of this block */
    val phiFunctions: List<PhiFunction>,
    /** SSA instructions in this block */
    val instructions: List<SsaInstruction>
)

/**
 * SSA form representation of a function
 */
data class SsaFunction(
    /** Original function CFG */
    val originalFunction: FunctionCfg,
    /** SSA basic blocks */
    val blocks: List<SsaBasicBlock>,
    /** All SSA variables in this function */
    val ssaVariables: Set<SsaVariable>,
    /** Entry block leader */
    val entryLeader: Int
)

/**
 * Complete SSA form construction result
 */
data class SsaConstruction(
    /** SSA functions */
    val functions: List<SsaFunction>
)

/**
 * Convert functions to SSA form using dominator frontiers
 */
fun AssemblyCodeFile.constructSsaForm(
    cfg: CfgConstruction,
    dominators: DominatorConstruction,
    dataflow: DataFlowAnalysis
): SsaConstruction {
    val ssaFunctions = cfg.functions.mapIndexed { index, function ->
        val domAnalysis = dominators.functions[index]
        val dataflowAnalysis = dataflow.functions[index]
        convertFunctionToSsa(this, function, domAnalysis, dataflowAnalysis)
    }
    
    return SsaConstruction(functions = ssaFunctions)
}

/**
 * Legacy extension for backward compatibility
 */
@Deprecated("Use AssemblyCodeFile.constructSsaForm instead")
fun List<AssemblyLine>.constructSsaForm(
    cfg: CfgConstruction,
    dominators: DominatorConstruction,
    dataflow: DataFlowAnalysis
): SsaConstruction = this.toCodeFile().constructSsaForm(cfg, dominators, dataflow)

/**
 * Convert a single function to SSA form
 */
private fun convertFunctionToSsa(
    codeFile: AssemblyCodeFile,
    function: FunctionCfg,
    dominatorAnalysis: DominatorAnalysis,
    dataflowAnalysis: FunctionDataFlow
): SsaFunction {
    // Step 1: Place φ functions using dominance frontiers
    val phiFunctions = placePhiFunctions(function, dominatorAnalysis, dataflowAnalysis)
    
    // Step 2: Rename variables to ensure SSA property
    val (ssaBlocks, ssaVariables) = renameVariables(
        codeFile, function, dominatorAnalysis, dataflowAnalysis, phiFunctions
    )
    
    return SsaFunction(
        originalFunction = function,
        blocks = ssaBlocks,
        ssaVariables = ssaVariables,
        entryLeader = function.entryLeader
    )
}

/**
 * Place φ functions at appropriate join points using dominance frontiers
 */
private fun placePhiFunctions(
    function: FunctionCfg,
    dominatorAnalysis: DominatorAnalysis,
    dataflowAnalysis: FunctionDataFlow
): Map<Int, List<PhiFunction>> {
    val phiFunctions = mutableMapOf<Int, MutableList<PhiFunction>>()
    
    // Group definitions by variable
    val definitionsByVariable = dataflowAnalysis.definitions.groupBy { it.variable }
    
    definitionsByVariable.forEach { (variable, definitions) ->
        // Find blocks that define this variable
        val definingBlocks = definitions.map { it.blockLeader }.toSet()
        
        // Compute where φ functions are needed using dominance frontiers
        val phiNeeded = mutableSetOf<Int>()
        val worklist = ArrayDeque(definingBlocks)
        
        while (worklist.isNotEmpty()) {
            val block = worklist.removeFirst()
            val domNode = dominatorAnalysis.leaderToDomNode[block] ?: continue
            
            // For each block in this block's dominance frontier
            domNode.dominanceFrontier.forEach { frontierBlock ->
                if (frontierBlock !in phiNeeded) {
                    phiNeeded.add(frontierBlock)
                    
                    // If frontier block doesn't already define this variable, add to worklist
                    if (frontierBlock !in definingBlocks) {
                        worklist.add(frontierBlock)
                    }
                }
            }
        }
        
        // Create φ functions for blocks that need them
        phiNeeded.forEach { blockLeader ->
            // Find predecessors of this block
            val predecessors = function.edges
                .filter { it.toLeader == blockLeader }
                .map { it.fromLeader }
            
            if (predecessors.size > 1) {
                // Create φ function with placeholder operands (will be filled during renaming)
                val phiFunction = PhiFunction(
                    result = SsaVariable(variable, 0), // Placeholder version
                    operands = predecessors.map { pred -> pred to SsaVariable(variable, 0) },
                    blockLeader = blockLeader
                )
                
                phiFunctions.getOrPut(blockLeader) { mutableListOf() }.add(phiFunction)
            }
        }
    }
    
    return phiFunctions
}

/**
 * Rename variables to maintain SSA property using recursive algorithm
 */
private fun renameVariables(
    codeFile: AssemblyCodeFile,
    function: FunctionCfg,
    dominatorAnalysis: DominatorAnalysis,
    dataflowAnalysis: FunctionDataFlow,
    phiFunctions: Map<Int, List<PhiFunction>>
): Pair<List<SsaBasicBlock>, Set<SsaVariable>> {
    val ssaBlocks = mutableListOf<SsaBasicBlock>()
    val allSsaVariables = mutableSetOf<SsaVariable>()
    
    // Variable versioning state
    val currentVersions = mutableMapOf<Variable, Int>()
    val versionStacks = mutableMapOf<Variable, ArrayDeque<Int>>()
    
    // Initialize version tracking for all variables
    dataflowAnalysis.definitions.map { it.variable }.distinct().forEach { variable ->
        currentVersions[variable] = 0
        versionStacks[variable] = ArrayDeque<Int>().apply { addLast(0) }
    }
    
    // Build children map from dominator tree
    val children = mutableMapOf<Int, MutableList<Int>>()
    dominatorAnalysis.leaderToDomNode.values.forEach { node ->
        node.children.forEach { child ->
            children.getOrPut(node.leaderIndex) { mutableListOf() }.add(child.leaderIndex)
        }
    }
    
    // Recursive renaming starting from entry block
    fun renameBlock(blockLeader: Int) {
        val block = function.blocks.find { it.leaderIndex == blockLeader } ?: return
        val savedVersions = mutableMapOf<Variable, Int>()
        
        // Save current versions for restoration
        currentVersions.forEach { (variable, version) ->
            savedVersions[variable] = version
        }
        
        // Process φ functions first (they define variables)
        val renamedPhiFunctions = phiFunctions[blockLeader]?.map { phi ->
            val newVersion = incrementVersion(phi.result.baseVariable, currentVersions, versionStacks)
            val renamedResult = SsaVariable(phi.result.baseVariable, newVersion)
            allSsaVariables.add(renamedResult)
            
            phi.copy(result = renamedResult)
        } ?: emptyList()
        
        // Process instructions in the block
        val renamedInstructions = mutableListOf<SsaInstruction>()
        
        block.lineIndexes.forEach { lineIndex ->
            val lineRef = codeFile.get(lineIndex)
            val line = lineRef.content
            line.instruction?.let { instr ->
                // Find uses and definitions for this instruction
                val instructionUses = dataflowAnalysis.uses.filter { 
                    it.lineRef.line == lineIndex 
                }
                val instructionDefs = dataflowAnalysis.definitions.filter { 
                    it.lineRef.line == lineIndex 
                }
                
                // Rename uses (use current versions)
                val ssaUses = instructionUses.map { use ->
                    val currentVersion = versionStacks[use.variable]?.lastOrNull() ?: 0
                    SsaVariable(use.variable, currentVersion).also { allSsaVariables.add(it) }
                }
                
                // Rename definitions (create new versions)
                val ssaDefs = instructionDefs.map { def ->
                    val newVersion = incrementVersion(def.variable, currentVersions, versionStacks)
                    SsaVariable(def.variable, newVersion).also { allSsaVariables.add(it) }
                }
                
                val ssaInstruction = SsaInstruction(
                    lineRef = lineRef,
                    originalInstruction = instr,
                    defines = ssaDefs,
                    uses = ssaUses,
                    blockLeader = blockLeader
                )
                
                renamedInstructions.add(ssaInstruction)
            }
        }
        
        // Create SSA block
        val ssaBlock = SsaBasicBlock(
            originalBlock = block,
            phiFunctions = renamedPhiFunctions,
            instructions = renamedInstructions
        )
        ssaBlocks.add(ssaBlock)
        
        // Update φ function operands in successor blocks
        function.edges.filter { it.fromLeader == blockLeader && it.toLeader != null }.forEach { edge ->
            val successorLeader = edge.toLeader!!
            val successorPhis = phiFunctions[successorLeader] ?: emptyList()
            
            successorPhis.forEach { phi ->
                val currentVersion = versionStacks[phi.result.baseVariable]?.lastOrNull() ?: 0
                val ssaVar = SsaVariable(phi.result.baseVariable, currentVersion)
                allSsaVariables.add(ssaVar)
                
                // Update the operand for this predecessor
                val updatedOperands = phi.operands.map { (predLeader, _) ->
                    if (predLeader == blockLeader) {
                        predLeader to ssaVar
                    } else {
                        predLeader to SsaVariable(phi.result.baseVariable, 0) // Placeholder
                    }
                }
                
                // Find and update the phi function in our working set
                val blockPhis = phiFunctions[successorLeader]?.toMutableList() ?: mutableListOf()
                val phiIndex = blockPhis.indexOfFirst { 
                    it.result.baseVariable == phi.result.baseVariable 
                }
                if (phiIndex >= 0) {
                    blockPhis[phiIndex] = phi.copy(operands = updatedOperands)
                }
            }
        }
        
        // Recursively process dominated children
        children[blockLeader]?.forEach { child ->
            renameBlock(child)
        }
        
        // Restore versions for variables defined in this block
        currentVersions.putAll(savedVersions)
        
        // Restore version stacks
        renamedPhiFunctions.forEach { phi ->
            versionStacks[phi.result.baseVariable]?.removeLastOrNull()
        }
        renamedInstructions.flatMap { it.defines }.forEach { ssaDef ->
            versionStacks[ssaDef.baseVariable]?.removeLastOrNull()
        }
    }
    
    renameBlock(function.entryLeader)
    
    return ssaBlocks to allSsaVariables
}

/**
 * Increment version counter and update stacks
 */
private fun incrementVersion(
    variable: Variable,
    currentVersions: MutableMap<Variable, Int>,
    versionStacks: MutableMap<Variable, ArrayDeque<Int>>
): Int {
    val newVersion = (currentVersions[variable] ?: 0) + 1
    currentVersions[variable] = newVersion
    versionStacks.getOrPut(variable) { ArrayDeque() }.addLast(newVersion)
    return newVersion
}