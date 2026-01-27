package com.ivieleague.decompiler6502tokotlin.hand

// by Claude - 6502-specific pattern handlers for control flow analysis
// These recognize idioms that are unique to 6502 assembly

/**
 * 6502-specific patterns that need special recognition during control flow analysis.
 *
 * 1. CONSECUTIVE BRANCH PAIRS (bpl/bmi, bcs/bcc):
 *    When bmi follows bpl with no flag-changing code, the bmi is effectively unconditional.
 *    Pattern: bpl Target / bmi Other
 *    - If N is set: bpl falls through, bmi branches to Other
 *    - If N is clear: bpl branches to Target, bmi is never reached
 *    So the bmi acts as "else goto Other"
 *
 * 2. BIT SKIP PATTERN (.db $2c):
 *    A literal $2C byte encodes as "BIT abs" which "eats" the next 2 bytes.
 *    Pattern: .db $2c / lda #$01 / shared_code:
 *    This makes the LDA unreachable from the fall-through path but reachable from a JMP.
 *    Already tracked in skipFirstInstructionForFunctions.
 *
 * 3. JUMPENGINE DISPATCH:
 *    JSR JumpEngine followed by .dw entries for case targets.
 *    Pattern: jsr JumpEngine / .dw Case0 / .dw Case1 / ...
 *    This is a computed jump (switch/case).
 */

/**
 * Information about a consecutive branch pair pattern.
 */
data class ConsecutiveBranchPair(
    val firstBlock: AssemblyBlock,
    val firstLine: AssemblyLine,
    val firstOp: AssemblyOp,
    val secondBlock: AssemblyBlock,
    val secondLine: AssemblyLine,
    val secondOp: AssemblyOp,
    /** The flag being tested by both branches */
    val testedFlag: AssemblyAffectable
) {
    /**
     * Determines if the second branch is effectively unconditional.
     * This happens when:
     * - Both branches test the same flag
     * - No flag-changing instructions between them
     * - The branches are mutually exclusive (one tests flag set, other tests flag clear)
     */
    val secondBranchIsUnconditional: Boolean get() {
        // Check if they're testing the same flag in opposite ways
        return when {
            firstOp == AssemblyOp.BPL && secondOp == AssemblyOp.BMI -> true
            firstOp == AssemblyOp.BMI && secondOp == AssemblyOp.BPL -> true
            firstOp == AssemblyOp.BCS && secondOp == AssemblyOp.BCC -> true
            firstOp == AssemblyOp.BCC && secondOp == AssemblyOp.BCS -> true
            firstOp == AssemblyOp.BEQ && secondOp == AssemblyOp.BNE -> true
            firstOp == AssemblyOp.BNE && secondOp == AssemblyOp.BEQ -> true
            firstOp == AssemblyOp.BVS && secondOp == AssemblyOp.BVC -> true
            firstOp == AssemblyOp.BVC && secondOp == AssemblyOp.BVS -> true
            else -> false
        }
    }
}

/**
 * Detect consecutive branch pairs in a function's blocks.
 * These are patterns where two conditional branches test the same flag
 * in opposite ways, making the second branch effectively unconditional.
 */
fun List<AssemblyBlock>.detectConsecutiveBranchPairs(): List<ConsecutiveBranchPair> {
    val pairs = mutableListOf<ConsecutiveBranchPair>()

    for (i in 0 until size - 1) {
        val firstBlock = this[i]
        val secondBlock = this[i + 1]

        // First block must end with a conditional branch
        val firstLine = firstBlock.lines.lastOrNull { it.instruction != null } ?: continue
        val firstInstr = firstLine.instruction ?: continue
        val firstOp = firstInstr.op
        if (!firstOp.isBranch) continue

        // Second block must immediately follow (fall-through)
        if (firstBlock.fallThroughExit != secondBlock) continue

        // Second block must also end with a conditional branch
        val secondLine = secondBlock.lines.lastOrNull { it.instruction != null } ?: continue
        val secondInstr = secondLine.instruction ?: continue
        val secondOp = secondInstr.op
        if (!secondOp.isBranch) continue

        // Check that no flag-changing instructions exist between the branches
        // (in the second block before its branch)
        val flagChangingOps = mutableSetOf<AssemblyAffectable>()
        val flagTypes = setOf(
            AssemblyAffectable.Negative,
            AssemblyAffectable.Zero,
            AssemblyAffectable.Carry,
            AssemblyAffectable.Overflow
        )
        for (line in secondBlock.lines) {
            if (line == secondLine) break
            val instr = line.instruction ?: continue
            // modifies() returns all affected things - filter to just flags
            val modified = instr.op.modifies(instr.address?.let { it::class })
            flagChangingOps.addAll(modified.filter { it in flagTypes })
        }

        // Determine which flag the first branch tests
        val testedFlag = when (firstOp) {
            AssemblyOp.BPL, AssemblyOp.BMI -> AssemblyAffectable.Negative
            AssemblyOp.BCS, AssemblyOp.BCC -> AssemblyAffectable.Carry
            AssemblyOp.BEQ, AssemblyOp.BNE -> AssemblyAffectable.Zero
            AssemblyOp.BVS, AssemblyOp.BVC -> AssemblyAffectable.Overflow
            else -> continue
        }

        // If the tested flag was modified between branches, this isn't a pair pattern
        if (testedFlag in flagChangingOps) continue

        // Check if the second branch tests the same flag
        val secondTestedFlag = when (secondOp) {
            AssemblyOp.BPL, AssemblyOp.BMI -> AssemblyAffectable.Negative
            AssemblyOp.BCS, AssemblyOp.BCC -> AssemblyAffectable.Carry
            AssemblyOp.BEQ, AssemblyOp.BNE -> AssemblyAffectable.Zero
            AssemblyOp.BVS, AssemblyOp.BVC -> AssemblyAffectable.Overflow
            else -> continue
        }

        if (testedFlag != secondTestedFlag) continue

        pairs.add(ConsecutiveBranchPair(
            firstBlock = firstBlock,
            firstLine = firstLine,
            firstOp = firstOp,
            secondBlock = secondBlock,
            secondLine = secondLine,
            secondOp = secondOp,
            testedFlag = testedFlag
        ))
    }

    return pairs
}

/**
 * Information about a BIT skip pattern ($2C).
 */
data class BitSkipPattern(
    val skipperBlock: AssemblyBlock,
    val skipperLine: AssemblyLine,
    val targetBlock: AssemblyBlock,
    /** The instruction that gets skipped when falling through */
    val skippedInstruction: AssemblyLine?
)

/**
 * Detect BIT skip patterns in a list of blocks.
 * These are .db $2c bytes that encode as BIT abs, skipping the next 2 bytes.
 */
fun List<AssemblyBlock>.detectBitSkipPatterns(): List<BitSkipPattern> {
    val patterns = mutableListOf<BitSkipPattern>()

    for (block in this) {
        for (line in block.lines) {
            val data = line.data
            if (data is AssemblyData.Db) {
                // Check for $2C byte (BIT absolute opcode)
                val has2c = data.items.any { item ->
                    item is AssemblyData.DbItem.ByteValue && item.value == 0x2C
                }
                if (has2c) {
                    val target = block.fallThroughExit
                    if (target != null) {
                        val skipped = target.lines.firstOrNull { it.instruction != null }
                        patterns.add(BitSkipPattern(
                            skipperBlock = block,
                            skipperLine = line,
                            targetBlock = target,
                            skippedInstruction = skipped
                        ))
                    }
                }
            }
        }
    }

    return patterns
}

/**
 * Information about a JumpEngine dispatch pattern.
 */
data class JumpEnginePattern(
    val dispatchBlock: AssemblyBlock,
    val dispatchLine: AssemblyLine,
    /** List of target function labels in order (index 0, 1, 2, ...) */
    val targets: List<String>,
    /** The data lines containing the dispatch table */
    val tableLines: List<AssemblyLine>
)

/**
 * Detect JumpEngine dispatch patterns in a list of blocks.
 */
fun List<AssemblyBlock>.detectJumpEnginePatterns(): List<JumpEnginePattern> {
    val patterns = mutableListOf<JumpEnginePattern>()

    for (block in this) {
        for ((lineIdx, line) in block.lines.withIndex()) {
            val instr = line.instruction ?: continue
            if (instr.op != AssemblyOp.JSR) continue

            val targetLabel = (instr.address as? AssemblyAddressing.Direct)?.label
            if (targetLabel != "JumpEngine") continue

            // Found a JSR JumpEngine - collect the dispatch table
            val targets = mutableListOf<String>()
            val tableLines = mutableListOf<AssemblyLine>()

            // Scan following lines for .dw or .db entries
            var scanBlock = block
            var scanIdx = lineIdx + 1

            outer@ while (true) {
                while (scanIdx >= scanBlock.lines.size) {
                    scanBlock = scanBlock.fallThroughExit ?: break@outer
                    scanIdx = 0
                }

                val dataLine = scanBlock.lines[scanIdx]

                when (val data = dataLine.data) {
                    is AssemblyData.Db -> {
                        // Extract function names from Expr items
                        for (item in data.items) {
                            if (item is AssemblyData.DbItem.Expr) {
                                val funcName = item.expr
                                    .removePrefix("<")
                                    .removePrefix(">")
                                    .trim()
                                if (funcName.isNotEmpty() && funcName.first().isLetter()) {
                                    targets.add(funcName)
                                }
                            }
                        }
                        tableLines.add(dataLine)
                        scanIdx++
                    }
                    null -> {
                        // Check if it's code or a label - stop collecting
                        if (dataLine.instruction != null || dataLine.label != null) {
                            break@outer
                        }
                        scanIdx++
                    }
                    else -> {
                        scanIdx++
                    }
                }
            }

            if (targets.isNotEmpty()) {
                patterns.add(JumpEnginePattern(
                    dispatchBlock = block,
                    dispatchLine = line,
                    targets = targets,
                    tableLines = tableLines
                ))
            }
        }
    }

    return patterns
}

// ============================================================================
// Pattern Application to Edge Tracking
// ============================================================================

/**
 * Apply 6502-specific pattern knowledge to an EdgeTracker.
 * This marks certain edges as structured based on known idioms.
 */
fun EdgeTracker.apply6502Patterns(
    blocks: List<AssemblyBlock>,
    branchPairs: List<ConsecutiveBranchPair>,
    bitSkipPatterns: List<BitSkipPattern>,
    jumpEnginePatterns: List<JumpEnginePattern>
) {
    // Handle consecutive branch pairs
    for (pair in branchPairs) {
        if (pair.secondBranchIsUnconditional) {
            // The second branch acts as an unconditional goto
            // Mark it as structured (it's part of an if-else pattern)
            val target = pair.secondBlock.branchExit
            if (target != null) {
                // This edge is part of the control structure
                consumeEdge(pair.secondBlock, target)
            }
        }
    }

    // Handle BIT skip patterns
    for (pattern in bitSkipPatterns) {
        // The fall-through edge "skips" the first instruction
        // This is already handled by skipFirstInstructionForFunctions
        // Just ensure the edge is marked as consumed
        consumeEdge(pattern.skipperBlock, pattern.targetBlock)
    }

    // Handle JumpEngine patterns
    for (pattern in jumpEnginePatterns) {
        // JumpEngine doesn't return - it dispatches to one of the targets
        // The fall-through edge (to the dispatch table) is consumed
        val tableBlock = pattern.dispatchBlock.fallThroughExit
        if (tableBlock != null) {
            consumeEdge(pattern.dispatchBlock, tableBlock)
        }
    }
}

// ============================================================================
// Pattern Summary for Diagnostics
// ============================================================================

/**
 * Generate a diagnostic summary of detected 6502 patterns.
 */
fun generate6502PatternSummary(
    branchPairs: List<ConsecutiveBranchPair>,
    bitSkipPatterns: List<BitSkipPattern>,
    jumpEnginePatterns: List<JumpEnginePattern>
): String = buildString {
    appendLine("6502 Pattern Detection Summary")
    appendLine("==============================")
    appendLine()

    if (branchPairs.isNotEmpty()) {
        appendLine("Consecutive Branch Pairs: ${branchPairs.size}")
        for (pair in branchPairs.take(10)) {
            val label = pair.firstBlock.label ?: "line ${pair.firstBlock.originalLineIndex}"
            val status = if (pair.secondBranchIsUnconditional) "unconditional" else "conditional"
            appendLine("  - $label: ${pair.firstOp}/${pair.secondOp} ($status)")
        }
        if (branchPairs.size > 10) {
            appendLine("  ... and ${branchPairs.size - 10} more")
        }
        appendLine()
    }

    if (bitSkipPatterns.isNotEmpty()) {
        appendLine("BIT Skip Patterns: ${bitSkipPatterns.size}")
        for (pattern in bitSkipPatterns.take(10)) {
            val label = pattern.skipperBlock.label ?: "line ${pattern.skipperBlock.originalLineIndex}"
            val target = pattern.targetBlock.label ?: "line ${pattern.targetBlock.originalLineIndex}"
            appendLine("  - $label -> $target")
        }
        if (bitSkipPatterns.size > 10) {
            appendLine("  ... and ${bitSkipPatterns.size - 10} more")
        }
        appendLine()
    }

    if (jumpEnginePatterns.isNotEmpty()) {
        appendLine("JumpEngine Dispatches: ${jumpEnginePatterns.size}")
        for (pattern in jumpEnginePatterns.take(10)) {
            val label = pattern.dispatchBlock.label ?: "line ${pattern.dispatchBlock.originalLineIndex}"
            appendLine("  - $label: ${pattern.targets.size} targets")
            appendLine("    [${pattern.targets.take(5).joinToString(", ")}${if (pattern.targets.size > 5) ", ..." else ""}]")
        }
        if (jumpEnginePatterns.size > 10) {
            appendLine("  ... and ${jumpEnginePatterns.size - 10} more")
        }
    }
}

// ============================================================================
// Condition Expression Building for 6502 Patterns
// ============================================================================

/**
 * Build a condition expression for a consecutive branch pair.
 * When the second branch is unconditional, we need to express the combined condition.
 */
fun ConsecutiveBranchPair.buildCombinedCondition(): ConditionExpr {
    // The combined condition represents the first branch's test
    return FlagTest(
        flag = testedFlag,
        positive = when (firstOp) {
            AssemblyOp.BPL -> false  // BPL branches when N=0
            AssemblyOp.BMI -> true   // BMI branches when N=1
            AssemblyOp.BCS -> true   // BCS branches when C=1
            AssemblyOp.BCC -> false  // BCC branches when C=0
            AssemblyOp.BEQ -> true   // BEQ branches when Z=1
            AssemblyOp.BNE -> false  // BNE branches when Z=0
            AssemblyOp.BVS -> true   // BVS branches when V=1
            AssemblyOp.BVC -> false  // BVC branches when V=0
            else -> true
        }
    )
}
