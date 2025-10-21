package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 12: Live Variable Analysis
 * - Compute live-in and live-out sets for each basic block
 * - Determine which variables are live at each program point
 * - Support register allocation and dead code elimination
 * - Use backward dataflow analysis on SSA form
 */

/**
 * Live variable information for a single basic block
 */
data class LivenessInfo(
    /** Variables live at the entry to this block */
    val liveIn: Set<SsaVariable>,
    /** Variables live at the exit from this block */
    val liveOut: Set<SsaVariable>,
    /** Variables defined in this block (kill set) */
    val def: Set<SsaVariable>,
    /** Variables used in this block before being defined (gen set) */
    val use: Set<SsaVariable>,
    /** The basic block leader this info applies to */
    val blockLeader: Int
)

/**
 * Live variable information for a single SSA instruction
 */
data class InstructionLiveness(
    /** The SSA instruction */
    val instruction: SsaInstruction,
    /** Variables live before this instruction executes */
    val liveBefore: Set<SsaVariable>,
    /** Variables live after this instruction executes */
    val liveAfter: Set<SsaVariable>
)

/**
 * Liveness analysis result for a function
 */
data class FunctionLiveness(
    /** The SSA function */
    val function: SsaFunction,
    /** Liveness info per basic block */
    val blockLiveness: Map<Int, LivenessInfo>,
    /** Detailed liveness info per instruction */
    val instructionLiveness: List<InstructionLiveness>,
    /** Variables that are never used (dead on arrival) */
    val deadVariables: Set<SsaVariable>
)

/**
 * Complete liveness analysis result
 */
data class LivenessAnalysis(
    /** Liveness analysis for each function */
    val functions: List<FunctionLiveness>
)

/**
 * Perform live variable analysis on SSA form
 */
fun AssemblyCodeFile.analyzeLiveness(
    ssa: SsaConstruction
): LivenessAnalysis {
    val functionLiveness = ssa.functions.map { function ->
        analyzeFunctionLiveness(function)
    }

    return LivenessAnalysis(functions = functionLiveness)
}

/**
 * Analyze liveness for a single function
 */
private fun analyzeFunctionLiveness(function: SsaFunction): FunctionLiveness {
    // Step 1: Compute gen and kill sets for each block
    val blockGenKill = function.blocks.associate { block ->
        val gen = mutableSetOf<SsaVariable>()
        val kill = mutableSetOf<SsaVariable>()

        // Process phi functions first
        block.phiFunctions.forEach { phi ->
            kill.add(phi.result)
            phi.operands.forEach { (_, ssaVar) ->
                if (ssaVar !in kill) {
                    gen.add(ssaVar)
                }
            }
        }

        // Process instructions in order
        block.instructions.forEach { instr ->
            // Uses before definitions go into gen set
            instr.uses.forEach { use ->
                if (use !in kill) {
                    gen.add(use)
                }
            }

            // Definitions go into kill set
            instr.defines.forEach { def ->
                kill.add(def)
            }
        }

        block.originalBlock.leaderIndex to Pair(gen, kill)
    }

    // Step 2: Build successor and predecessor maps
    val successors = mutableMapOf<Int, MutableSet<Int>>()
    val predecessors = mutableMapOf<Int, MutableSet<Int>>()

    function.blocks.forEach { block ->
        successors[block.originalBlock.leaderIndex] = mutableSetOf()
        predecessors[block.originalBlock.leaderIndex] = mutableSetOf()
    }

    function.originalFunction.edges.forEach { edge ->
        val from = edge.fromLeader
        val to = edge.toLeader
        if (to != null) {
            successors[from]?.add(to)
            predecessors[to]?.add(from)
        }
    }

    // Step 3: Compute live-in and live-out using backward dataflow analysis
    // live-out[B] = Union of live-in[S] for all successors S of B
    // live-in[B] = use[B] ∪ (live-out[B] - def[B])

    val liveIn = mutableMapOf<Int, MutableSet<SsaVariable>>()
    val liveOut = mutableMapOf<Int, MutableSet<SsaVariable>>()

    // Initialize
    function.blocks.forEach { block ->
        val leader = block.originalBlock.leaderIndex
        liveIn[leader] = mutableSetOf()
        liveOut[leader] = mutableSetOf()
    }

    // Iterate until fixed point
    var changed = true
    var iterations = 0
    while (changed && iterations < 100) {
        changed = false
        iterations++

        // Process blocks in reverse postorder for better convergence
        function.blocks.reversed().forEach { block ->
            val leader = block.originalBlock.leaderIndex
            val (gen, kill) = blockGenKill[leader] ?: (emptySet<SsaVariable>() to emptySet<SsaVariable>())

            // Compute new live-out: union of successors' live-in
            val newLiveOut = mutableSetOf<SsaVariable>()
            successors[leader]?.forEach { succ ->
                newLiveOut.addAll(liveIn[succ] ?: emptySet())
            }

            // Compute new live-in: gen ∪ (live-out - kill)
            val newLiveIn = mutableSetOf<SsaVariable>()
            newLiveIn.addAll(gen)
            newLiveIn.addAll(newLiveOut - kill)

            // Check for changes
            if (newLiveIn != liveIn[leader] || newLiveOut != liveOut[leader]) {
                liveIn[leader] = newLiveIn
                liveOut[leader] = newLiveOut
                changed = true
            }
        }
    }

    // Step 4: Create block liveness info
    val blockLiveness = function.blocks.associate { block ->
        val leader = block.originalBlock.leaderIndex
        val (gen, kill) = blockGenKill[leader] ?: (emptySet<SsaVariable>() to emptySet<SsaVariable>())

        leader to LivenessInfo(
            liveIn = liveIn[leader]?.toSet() ?: emptySet(),
            liveOut = liveOut[leader]?.toSet() ?: emptySet(),
            def = kill,
            use = gen,
            blockLeader = leader
        )
    }

    // Step 5: Compute instruction-level liveness
    val instructionLiveness = mutableListOf<InstructionLiveness>()

    function.blocks.forEach { block ->
        val leader = block.originalBlock.leaderIndex
        var currentLive = liveOut[leader]?.toMutableSet() ?: mutableSetOf()

        // Process instructions in reverse order
        val allInstructions = block.instructions.reversed()
        allInstructions.forEach { instr ->
            val liveAfter = currentLive.toSet()

            // Remove definitions from live set
            currentLive.removeAll(instr.defines)

            // Add uses to live set
            currentLive.addAll(instr.uses)

            val liveBefore = currentLive.toSet()

            instructionLiveness.add(InstructionLiveness(
                instruction = instr,
                liveBefore = liveBefore,
                liveAfter = liveAfter
            ))
        }

        // Process phi functions
        block.phiFunctions.reversed().forEach { phi ->
            currentLive.remove(phi.result)
            phi.operands.forEach { (_, ssaVar) ->
                currentLive.add(ssaVar)
            }
        }
    }

    // Step 6: Identify dead variables (never live at any point)
    val allVariables = function.ssaVariables
    val everLive = mutableSetOf<SsaVariable>()

    // Check block-level liveness
    blockLiveness.values.forEach { info ->
        everLive.addAll(info.liveIn)
        everLive.addAll(info.liveOut)
    }

    // Also check instruction-level liveness (variables can be live within a block)
    instructionLiveness.forEach { instrLiveness ->
        everLive.addAll(instrLiveness.liveBefore)
        everLive.addAll(instrLiveness.liveAfter)
    }

    val deadVariables = allVariables - everLive

    return FunctionLiveness(
        function = function,
        blockLiveness = blockLiveness,
        instructionLiveness = instructionLiveness.reversed(), // Restore original order
        deadVariables = deadVariables
    )
}

/**
 * Helper to get variables live at a specific program point
 */
fun FunctionLiveness.getLiveAtInstruction(instructionIndex: Int): Set<SsaVariable> {
    return instructionLiveness.getOrNull(instructionIndex)?.liveBefore ?: emptySet()
}

/**
 * Helper to check if a variable is live at any point in the function
 */
fun FunctionLiveness.isVariableLive(variable: SsaVariable): Boolean {
    return variable !in deadVariables
}

/**
 * Helper to find all instructions where a variable is last used
 */
fun FunctionLiveness.findLastUses(variable: SsaVariable): List<SsaInstruction> {
    val lastUses = mutableListOf<SsaInstruction>()

    instructionLiveness.forEach { instrLiveness ->
        val instr = instrLiveness.instruction
        // Variable is last used if it's in liveBefore but not in liveAfter and is used by this instruction
        if (variable in instrLiveness.liveBefore &&
            variable !in instrLiveness.liveAfter &&
            variable in instr.uses) {
            lastUses.add(instr)
        }
    }

    return lastUses
}
