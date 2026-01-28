package com.ivieleague.decompiler6502tokotlin.hand

// by Claude - Structural analysis algorithm for principled control flow analysis
// This implements bottom-up region formation with guaranteed edge coverage

/**
 * Result of structural analysis for a function.
 */
data class StructureResult(
    val region: Region,
    val edgeTracker: EdgeTracker,
    val validation: EdgeValidationResult?
)

/**
 * Configuration for structural analysis.
 */
data class StructureConfig(
    /** Whether to throw on validation failure (missing edges) */
    val throwOnMissingEdges: Boolean = false,
    /** Whether to use 6502-specific pattern recognition */
    val use6502Patterns: Boolean = true
)

/**
 * Main entry point for structural analysis of a function.
 *
 * This replaces the old analyzeControls() function with a principled algorithm that:
 * 1. Tracks every CFG edge
 * 2. Builds structured regions bottom-up
 * 3. Validates that every edge is accounted for
 * 4. Reports unstructured edges with diagnostics
 */
fun AssemblyFunction.analyzeStructure(
    config: StructureConfig = StructureConfig()
): StructureResult {
    val blocks = this.blocks ?: setOf(this.startingBlock)

    // Handle empty functions (e.g., those absorbed via BIT skip pattern)
    if (blocks.isEmpty()) {
        val tracker = EdgeTracker(this, blocks)
        return StructureResult(
            region = UnstructuredRegion(
                id = nextRegionId(),
                entry = this.startingBlock,
                blocks = emptySet(),
                unstructuredEdges = emptyList()
            ),
            edgeTracker = tracker,
            validation = tracker.validateSoft() // Return valid (empty) validation
        )
    }

    // Create edge tracker for this function
    val tracker = EdgeTracker(this, blocks)

    // Recompute dominators for THIS function's reachable blocks
    blocks.toList().dominators()

    // Detect natural loops
    val naturalLoops = blocks.toList().detectNaturalLoops()

    // Build the region hierarchy
    val analyzer = StructuralAnalyzer(this, blocks, tracker, naturalLoops, config)
    val region = analyzer.analyze()

    // Validate edge coverage
    val validation = if (config.throwOnMissingEdges) {
        tracker.validate()
    } else {
        tracker.validateSoft()
    }

    return StructureResult(region, tracker, validation)
}

/**
 * Internal class that performs the structural analysis.
 */
private class StructuralAnalyzer(
    private val function: AssemblyFunction,
    private val allBlocks: Set<AssemblyBlock>,
    private val tracker: EdgeTracker,
    private val naturalLoops: List<NaturalLoop>,
    private val config: StructureConfig
) {
    // Layout order (sorted by original line index, with entry first)
    private val layout: List<AssemblyBlock>
    private val indexOf: Map<AssemblyBlock, Int>
    private val loopByHeader: Map<AssemblyBlock, NaturalLoop>

    // Track which blocks have been consumed into regions
    private val consumedBlocks = mutableSetOf<AssemblyBlock>()

    // Track loop headers currently being processed (for recursion prevention)
    private val processingLoopHeaders = mutableSetOf<AssemblyBlock>()

    init {
        val sortedByAddress = allBlocks.sortedBy { it.originalLineIndex }
        layout = if (sortedByAddress.firstOrNull() == function.startingBlock) {
            sortedByAddress
        } else {
            listOf(function.startingBlock) + sortedByAddress.filter { it != function.startingBlock }
        }
        indexOf = layout.withIndex().associate { it.value to it.index }
        loopByHeader = naturalLoops.associateBy { it.header }
    }

    /**
     * Main analysis entry point.
     */
    fun analyze(): Region {
        return buildRegionForRange(0, layout.size)
    }

    /**
     * Build a region for a range of blocks in the layout.
     */
    private fun buildRegionForRange(startIdx: Int, endIdx: Int): Region {
        if (startIdx >= endIdx) {
            // Empty range - return an empty unstructured region
            return UnstructuredRegion(
                id = nextRegionId(),
                entry = layout.getOrElse(startIdx) { function.startingBlock },
                blocks = emptySet(),
                unstructuredEdges = emptyList()
            )
        }

        val regions = mutableListOf<Region>()
        var i = startIdx

        while (i < endIdx) {
            val block = layout[i]

            // Skip blocks already consumed by a previous region
            if (block in consumedBlocks) {
                i++
                continue
            }

            // Try to build a region starting at this block
            val result = tryBuildRegion(block, i, endIdx)

            if (result != null) {
                regions.add(result.region)
                consumedBlocks.addAll(result.region.blocks)
                i = result.nextIndex
            } else {
                // Fall back to a single block region
                val blockRegion = BlockRegion(nextRegionId(), block)
                regions.add(blockRegion)
                consumedBlocks.add(block)
                consumeBlockEdgesAsStructured(block)
                i++
            }
        }

        return when {
            regions.isEmpty() -> UnstructuredRegion(
                id = nextRegionId(),
                entry = layout.getOrElse(startIdx) { function.startingBlock },
                blocks = emptySet(),
                unstructuredEdges = emptyList()
            )
            regions.size == 1 -> regions.first()
            else -> SequenceRegion(nextRegionId(), regions)
        }
    }

    /**
     * Result of trying to build a region.
     */
    private data class RegionBuildResult(
        val region: Region,
        val nextIndex: Int
    )

    /**
     * Try to build a structured region starting at the given block.
     */
    private fun tryBuildRegion(block: AssemblyBlock, blockIdx: Int, endIdx: Int): RegionBuildResult? {
        // 1. Check for natural loop at this header
        val loop = loopByHeader[block]?.takeIf { block !in processingLoopHeaders }
        if (loop != null) {
            val result = tryBuildLoopRegion(block, loop, blockIdx, endIdx)
            if (result != null) return result
        }

        // 2. Check for if-then-else pattern
        if (isConditionalBlock(block)) {
            val result = tryBuildIfRegion(block, blockIdx, endIdx)
            if (result != null) return result
        }

        // 3. Check for infinite loop (JMP backward)
        if (isUnconditionalJmp(block)) {
            val target = block.branchExit
            val targetIdx = target?.let { indexOf[it] } ?: -1
            if (target != null && targetIdx in 0..blockIdx && target !in processingLoopHeaders) {
                val result = tryBuildInfiniteLoop(block, target, targetIdx, blockIdx, endIdx)
                if (result != null) return result
            }
        }

        // 4. Fall back to single block
        return null
    }

    /**
     * Try to build a loop region from a natural loop.
     */
    private fun tryBuildLoopRegion(
        header: AssemblyBlock,
        loop: NaturalLoop,
        headerIdx: Int,
        endIdx: Int
    ): RegionBuildResult? {
        // Filter loop body to only blocks in current range
        val loopBodyBlocks = loop.body.filter { indexOf[it] != null && indexOf[it]!! < endIdx }
        if (loopBodyBlocks.isEmpty()) return null

        val loopEnd = loopBodyBlocks.maxOf { indexOf[it]!! } + 1

        // Determine loop kind
        val headerIsConditional = isConditionalBlock(header)
        val backEdgeSource = loop.backEdges.firstOrNull()?.first
        val backEdgeIsConditional = backEdgeSource?.let { isConditionalBlock(it) } ?: false

        // Check if header's branch is internal (stays in loop) vs external (exits loop)
        val headerBranchInLoop = header.branchExit?.let { it in loop.body } ?: false

        // by Claude - Check if header and back-edge branches check the SAME flag.
        // This distinguishes between "early exit" patterns (same flag, inverted) vs
        // "loop condition at header" patterns (different flags).
        // Example: Loop: LDA $00,X; BEQ Done; DEX; BNE Loop - header (BEQ) and back-edge (BNE) both check Z
        // Example: loop_header: LDA counter; BEQ exit; STA; DEC; BPL loop_header - header (BEQ=Z) vs back-edge (BPL=N)
        fun getFlagCheckedByBranch(op: AssemblyOp?): Char? = when (op) {
            AssemblyOp.BEQ, AssemblyOp.BNE -> 'Z'
            AssemblyOp.BCS, AssemblyOp.BCC -> 'C'
            AssemblyOp.BMI, AssemblyOp.BPL -> 'N'
            AssemblyOp.BVS, AssemblyOp.BVC -> 'V'
            else -> null
        }
        val headerBranchOp = header.lastInstructionLine()?.instruction?.op
        val backEdgeBranchOp = backEdgeSource?.lastInstructionLine()?.instruction?.op
        val headerFlag = getFlagCheckedByBranch(headerBranchOp)
        val backEdgeFlag = getFlagCheckedByBranch(backEdgeBranchOp)
        val sameFlag = headerFlag != null && backEdgeFlag != null && headerFlag == backEdgeFlag

        // by Claude - Check if header has body instructions that should execute before condition.
        // Key insight for patterns:
        // - Pattern: INX; CPX #$03; BEQ -> INX is body, CPX/BEQ is condition = PostTest
        // - Pattern: LDA #$00; BEQ -> LDA immediate is condition setup = PreTest
        // - Pattern: LDA $addr; BEQ -> Just loading to test = PreTest (no side effects)
        // - Pattern: LDA $addr; STA $other; BEQ -> STA is body code = PostTest
        //
        // CRITICAL FIX: Loads (LDA/LDX/LDY) by themselves are NOT body code - they're condition setup.
        // Only state-MODIFYING instructions (stores, increments, decrements, shifts, arithmetic that
        // stores results) are body code. Loading a value to test it is part of the condition.
        val stateModifyingOps = setOf(
            AssemblyOp.INX, AssemblyOp.INY, AssemblyOp.DEX, AssemblyOp.DEY,
            AssemblyOp.INC, AssemblyOp.DEC,
            AssemblyOp.ASL, AssemblyOp.LSR, AssemblyOp.ROL, AssemblyOp.ROR,
            AssemblyOp.STA, AssemblyOp.STX, AssemblyOp.STY,
            // Note: ADC/SBC/AND/ORA/EOR modify A register but that's typically used in conditions
            // Only count them as body if they occur BEFORE a compare/branch
            AssemblyOp.TAX, AssemblyOp.TAY, AssemblyOp.TXA, AssemblyOp.TYA, // Transfers that preserve value
            AssemblyOp.PHA, AssemblyOp.PHP, AssemblyOp.PLA, AssemblyOp.PLP  // Stack operations
        )

        val hasCompareInstruction = header.lines.any { line ->
            line.instruction?.op in setOf(AssemblyOp.CMP, AssemblyOp.CPX, AssemblyOp.CPY, AssemblyOp.BIT)
        }

        val headerHasBodyInstructions = if (!hasCompareInstruction) {
            // No compare instruction - check for state-modifying instructions before the branch
            // Loads (LDA/LDX/LDY) are condition setup, not body code
            header.lines.any { line ->
                val op = line.instruction?.op ?: return@any false
                if (op.isBranch) return@any false // Don't count the branch itself
                op in stateModifyingOps
            }
        } else {
            // Has compare - check for state-modifying instructions BEFORE the compare
            var foundCompare = false
            header.lines.any { line ->
                val op = line.instruction?.op ?: return@any false
                if (op in setOf(AssemblyOp.CMP, AssemblyOp.CPX, AssemblyOp.CPY, AssemblyOp.BIT)) {
                    foundCompare = true
                    return@any false // Don't count compare as body
                }
                if (foundCompare) return@any false // After compare is condition, not body
                // Before compare: only state-modifying instructions count as body
                op in stateModifyingOps
            }
        }

        // by Claude - Debug
        val debugLoop = header.label == "Loop"
        val calculatedIsSingleBlock = loop.body.size == 1 && backEdgeSource == header
        if (debugLoop) {
            println("DEBUG Loop detection for ${header.label}:")
            println("  headerIsConditional=$headerIsConditional")
            println("  backEdgeIsConditional=$backEdgeIsConditional")
            println("  headerBranchInLoop=$headerBranchInLoop")
            println("  hasExits=${loop.exits.isNotEmpty()}, exits=${loop.exits.map { it.label }}")
            println("  loop.body=${loop.body.map { it.label ?: "@${it.originalLineIndex}" }}")
            println("  loop.backEdges=${loop.backEdges.map { (from, to) -> "${from.label ?: "@${from.originalLineIndex}"} -> ${to.label}" }}")
            println("  backEdgeSource=${backEdgeSource?.label ?: backEdgeSource?.originalLineIndex}")
            println("  isSingleBlock=$calculatedIsSingleBlock")
            println("  headerHasBodyInstructions=$headerHasBodyInstructions")
            println("  header.branchExit=${header.branchExit?.label}")
            println("  header.fallThroughExit=${header.fallThroughExit?.label}")
            println("  headerFlag=$headerFlag, backEdgeFlag=$backEdgeFlag, sameFlag=$sameFlag")
        }

        val loopKind = determineLoopKind(
            headerIsConditional = headerIsConditional,
            backEdgeIsConditional = backEdgeIsConditional,
            headerBranchInLoop = headerBranchInLoop,
            hasExits = loop.exits.isNotEmpty(),
            isSingleBlock = calculatedIsSingleBlock,
            headerHasBodyInstructions = headerHasBodyInstructions,
            headerAndBackEdgeSameFlag = sameFlag
        )

        if (debugLoop) {
            println("  -> loopKind=$loopKind")
        }

        // Mark as processing to prevent recursion
        processingLoopHeaders.add(header)

        try {
            // Build the body region
            // by Claude - Include header in body for PostTest loops when:
            // 1. Header has internal branch (original condition)
            // 2. Header has body instructions before condition
            // 3. Header is not conditional (all its instructions are body code)
            // 4. Back-edge is from different block (header's branch is early exit, not loop condition)
            val backEdgeFromDifferent = backEdgeSource != null && backEdgeSource != header
            val includeHeaderInBody = loopKind == LoopKind.PostTest &&
                (headerBranchInLoop || headerHasBodyInstructions || !headerIsConditional || backEdgeFromDifferent)
            val bodyStartIdx = if (includeHeaderInBody) {
                headerIdx // Include header in body for PostTest
            } else {
                headerIdx + 1
            }

            val bodyRegion = if (bodyStartIdx < loopEnd) {
                buildRegionForRange(bodyStartIdx, loopEnd)
            } else {
                BlockRegion(nextRegionId(), header)
            }

            // Consume loop edges
            consumeLoopEdges(loop, header)

            // Create the appropriate loop region
            val region = when (loopKind) {
                LoopKind.PreTest -> {
                    val condLine = header.lastInstructionLine()!!
                    val exitBlock = loop.exits.firstOrNull()
                    WhileLoopRegion(
                        id = nextRegionId(),
                        header = header,
                        conditionLine = condLine,
                        continueOnBranch = header.branchExit == header, // branch continues if it's a self-loop
                        bodyRegion = bodyRegion,
                        exitBlock = exitBlock
                    )
                }
                LoopKind.PostTest -> {
                    // by Claude - Determine condition block for PostTest loops:
                    // 1. If header has body instructions AND header's branch is INTERNAL (stays in loop),
                    //    then header's branch is an if-then inside the loop, not the loop condition.
                    //    Use back-edge source for condition.
                    // 2. If header has body instructions AND header's branch is EXTERNAL (exits loop),
                    //    then header's branch IS the loop condition.
                    // 3. Otherwise use back-edge source (traditional do-while).
                    val condBlock = if (headerHasBodyInstructions && headerIsConditional && !headerBranchInLoop) {
                        // Header has body + external exit condition (e.g., INX; CPX; BEQ Done)
                        header
                    } else {
                        // Either header's branch is internal (if-then inside loop) or no header condition
                        backEdgeSource ?: header
                    }
                    val condLine = condBlock.lastInstructionLine()!!
                    val exitBlock = loop.exits.firstOrNull()
                    DoWhileLoopRegion(
                        id = nextRegionId(),
                        header = header,
                        conditionBlock = condBlock,
                        conditionLine = condLine,
                        continueOnBranch = condBlock.branchExit == header,
                        bodyRegion = bodyRegion,
                        exitBlock = exitBlock
                    )
                }
                LoopKind.Infinite -> {
                    InfiniteLoopRegion(
                        id = nextRegionId(),
                        header = header,
                        bodyRegion = bodyRegion
                    )
                }
            }

            return RegionBuildResult(region, loopEnd)
        } finally {
            processingLoopHeaders.remove(header)
        }
    }

    /**
     * Try to build an if-then or if-then-else region.
     */
    private fun tryBuildIfRegion(block: AssemblyBlock, blockIdx: Int, endIdx: Int): RegionBuildResult? {
        val ft = block.fallThroughExit
        val br = block.branchExit

        if (ft == null || br == null) return null

        val ftIdx = indexOf[ft] ?: return null
        val brIdx = indexOf[br] ?: -1

        // by Claude - CRITICAL FIX for Bug #4: Check for backward branches FIRST.
        // When a conditional branches backward to an earlier block in MEMORY ORDER,
        // the standard if-then detection fails. Handle backward branches to return blocks
        // as early-return if-then patterns.
        // NOTE: Check MEMORY order (originalLineIndex), not layout order (brIdx).
        // Layout order has entry first, but memory order may have branch target first.
        val isBackwardInMemory = br.originalLineIndex < block.originalLineIndex
        if (isBackwardInMemory) {
            // This is a backward branch (in memory order)
            if (isReturnBlock(br)) {
                // Build an if-then with the backward branch target as the then-body
                return buildBackwardBranchIfThen(
                    condBlock = block,
                    branchTarget = br,
                    fallThroughIdx = ftIdx,
                    endIdx = endIdx
                )
            } else {
                // Not a return block - mark as unstructured (loop continue or goto)
                tracker.markUnstructured(block, br, "backward branch (loop continue or goto)")
                // Continue to try standard if-then pattern for the forward fall-through
            }
        }

        // Fall-through should be next block (standard if-then pattern)
        // Note: For backward branch cases that aren't early-return, we skip this check
        // and let the fall-through be handled as a sequence
        if (ftIdx != blockIdx + 1) return null

        // Branch should be forward within range
        val effectiveBrIdx = if (brIdx in (blockIdx + 1) until endIdx) brIdx else -1

        if (effectiveBrIdx <= 0) {
            // Branch is backward or out of range - already handled above for backward branches
            // This case is for branches that are forward but out of the current range
            return null
        }

        // by Claude - Check for if-then-else pattern: then ends with JMP to join
        // The then path may be one or more blocks, ending with a JMP to the join point.
        // The else path is from the branch target to the join point.
        val lastThenIdx = effectiveBrIdx - 1
        if (lastThenIdx >= blockIdx + 1) {  // Changed from > to >= to handle single-block then
            val lastThenBlock = layout[lastThenIdx]
            if (isUnconditionalJmp(lastThenBlock)) {
                val join = lastThenBlock.branchExit
                val joinIdx = join?.let { indexOf[it] }
                if (joinIdx != null && joinIdx > effectiveBrIdx && joinIdx <= endIdx) {
                    // IF-THEN-ELSE pattern
                    // Include the JMP block in the then region (thenEnd = lastThenIdx + 1)
                    // The code generator will handle the JMP as implicit since the
                    // if-then-else structure already provides the control flow.
                    return buildIfThenElseRegion(
                        condBlock = block,
                        thenStart = blockIdx + 1,
                        thenEnd = lastThenIdx + 1, // Include the JMP block
                        elseStart = effectiveBrIdx,
                        elseEnd = joinIdx,
                        joinIdx = joinIdx,
                        endIdx = endIdx
                    )
                }
            }
        }

        // IF-THEN pattern
        return buildIfThenRegion(
            condBlock = block,
            thenStart = blockIdx + 1,
            thenEnd = effectiveBrIdx,
            joinIdx = effectiveBrIdx,
            endIdx = endIdx
        )
    }

    /**
     * Build an if-then region.
     */
    private fun buildIfThenRegion(
        condBlock: AssemblyBlock,
        thenStart: Int,
        thenEnd: Int,
        joinIdx: Int,
        endIdx: Int
    ): RegionBuildResult? {
        if (thenStart >= thenEnd) {
            // Empty then branch - this shouldn't happen in well-formed code
            return null
        }

        val thenRegion = buildRegionForRange(thenStart, thenEnd)
        val condLine = condBlock.lastInstructionLine() ?: return null
        val joinBlock = layout.getOrNull(joinIdx)

        // Consume the conditional branch edge (to join)
        condBlock.branchExit?.let { tracker.consumeEdge(condBlock, it) }
        // Consume the fall-through edge (to then)
        condBlock.fallThroughExit?.let { tracker.consumeEdge(condBlock, it) }

        val region = IfThenRegion(
            id = nextRegionId(),
            conditionBlock = condBlock,
            conditionLine = condLine,
            sense = false, // fall-through is the then-path
            thenRegion = thenRegion,
            joinBlock = joinBlock
        )

        return RegionBuildResult(region, joinIdx)
    }

    /**
     * Build an if-then-else region.
     */
    private fun buildIfThenElseRegion(
        condBlock: AssemblyBlock,
        thenStart: Int,
        thenEnd: Int,
        elseStart: Int,
        elseEnd: Int,
        joinIdx: Int,
        endIdx: Int
    ): RegionBuildResult? {
        val thenRegion = buildRegionForRange(thenStart, thenEnd)
        val elseRegion = buildRegionForRange(elseStart, elseEnd)
        val condLine = condBlock.lastInstructionLine() ?: return null
        val joinBlock = layout.getOrNull(joinIdx)

        // Consume edges
        condBlock.branchExit?.let { tracker.consumeEdge(condBlock, it) }
        condBlock.fallThroughExit?.let { tracker.consumeEdge(condBlock, it) }

        // Consume the JMP-to-join edge from end of then block
        val lastThenBlock = layout.getOrNull(thenEnd)
        lastThenBlock?.branchExit?.let { tracker.consumeEdge(lastThenBlock, it) }

        val region = IfThenElseRegion(
            id = nextRegionId(),
            conditionBlock = condBlock,
            conditionLine = condLine,
            sense = false, // fall-through is the then-path
            thenRegion = thenRegion,
            elseRegion = elseRegion,
            joinBlock = joinBlock
        )

        return RegionBuildResult(region, joinIdx)
    }

    /**
     * by Claude - Build an if-then region for a backward branch to an early-return block.
     * This handles the pattern where a conditional branches backward to code that ends with RTS.
     *
     * Pattern:
     *   EntryPoint:
     *     ldy counter
     *     beq EarlyReturn    ; backward branch
     *     ... main code ...
     *   EarlyReturn:         ; earlier in memory
     *     lda #$00
     *     rts
     *
     * Generates:
     *   if (Y == 0) {
     *       A = 0x00
     *       return
     *   }
     *   // main code
     */
    private fun buildBackwardBranchIfThen(
        condBlock: AssemblyBlock,
        branchTarget: AssemblyBlock,
        fallThroughIdx: Int,
        endIdx: Int
    ): RegionBuildResult? {
        val condLine = condBlock.lastInstructionLine() ?: return null

        // Mark the backward target block as consumed
        consumedBlocks.add(branchTarget)

        // Build the then-body from the backward target block
        // This is just the single block (which ends with return)
        val thenRegion = BlockRegion(nextRegionId(), branchTarget)

        // Consume edges
        tracker.consumeEdge(condBlock, branchTarget) // branch edge
        condBlock.fallThroughExit?.let { tracker.consumeEdge(condBlock, it) } // fall-through edge

        // The join point is the fall-through (main code continues after the if)
        val joinBlock = condBlock.fallThroughExit

        val region = IfThenRegion(
            id = nextRegionId(),
            conditionBlock = condBlock,
            conditionLine = condLine,
            sense = true, // branch IS taken for then-path (inverted from normal if-then)
            thenRegion = thenRegion,
            joinBlock = joinBlock
        )

        // Continue processing from fall-through
        return RegionBuildResult(region, fallThroughIdx)
    }

    /**
     * Try to build an infinite loop region.
     */
    private fun tryBuildInfiniteLoop(
        jmpBlock: AssemblyBlock,
        header: AssemblyBlock,
        headerIdx: Int,
        jmpIdx: Int,
        endIdx: Int
    ): RegionBuildResult? {
        // Mark as processing
        processingLoopHeaders.add(header)

        try {
            // Build the body from header to jmp block (inclusive)
            val bodyRegion = buildRegionForRange(headerIdx, jmpIdx + 1)

            // Consume the back-edge
            tracker.consumeEdge(jmpBlock, header)

            val region = InfiniteLoopRegion(
                id = nextRegionId(),
                header = header,
                bodyRegion = bodyRegion
            )

            return RegionBuildResult(region, jmpIdx + 1)
        } finally {
            processingLoopHeaders.remove(header)
        }
    }

    // ========================================================================
    // Helper methods
    // ========================================================================

    private fun AssemblyBlock.lastInstructionLine(): AssemblyLine? =
        this.lines.lastOrNull { it.instruction != null }

    private fun isConditionalBlock(block: AssemblyBlock): Boolean =
        block.lastInstructionLine()?.instruction?.op?.isBranch == true

    private fun isUnconditionalJmp(block: AssemblyBlock): Boolean =
        block.lastInstructionLine()?.instruction?.op == AssemblyOp.JMP

    private fun isReturnBlock(block: AssemblyBlock): Boolean =
        block.lastInstructionLine()?.instruction?.op.let {
            it == AssemblyOp.RTS || it == AssemblyOp.RTI
        } == true

    private fun determineLoopKind(
        headerIsConditional: Boolean,
        backEdgeIsConditional: Boolean,
        headerBranchInLoop: Boolean,
        hasExits: Boolean,
        isSingleBlock: Boolean,
        headerHasBodyInstructions: Boolean = false,  // by Claude
        headerAndBackEdgeSameFlag: Boolean = false   // by Claude - true if header and back-edge check same CPU flag
    ): LoopKind {
        return when {
            // Infinite loop: no exits and no conditional
            !hasExits && !headerIsConditional && !backEdgeIsConditional -> LoopKind.Infinite
            // Single-block self-loop with conditional: PostTest (do-while)
            // This handles patterns like: Loop: LDA $00,X; BEQ exit; DEX; BNE Loop
            // The body (LDA, DEX) always executes at least once before checking continue condition
            isSingleBlock && headerIsConditional -> LoopKind.PostTest
            // Header has internal branch only, back-edge is conditional: PostTest
            headerIsConditional && headerBranchInLoop && backEdgeIsConditional -> LoopKind.PostTest
            // Header not conditional, back-edge is: PostTest
            !headerIsConditional && backEdgeIsConditional -> LoopKind.PostTest
            // by Claude - Header has body instructions before the condition: PostTest (do-while)
            // This handles patterns like: Loop: INX; CPX #$03; BEQ Done; JMP Loop
            // The INX is loop body, CPX/BEQ is the condition, so body executes before condition
            headerIsConditional && headerHasBodyInstructions && hasExits -> LoopKind.PostTest
            // by Claude - Multi-block loop with conditional back-edge and header exit, SAME FLAG: PostTest
            // Pattern: Loop: LDA $00,X; BEQ Done; DEX; BNE Loop (blocks: [Loop, @5])
            // Header (BEQ=Z) and back-edge (BNE=Z) check the SAME flag - this is "early exit" pattern.
            // Header branch goes to exit (early exit), back-edge has the actual loop continuation condition.
            // Body always executes before back-edge condition is checked -> PostTest
            !isSingleBlock && backEdgeIsConditional && headerIsConditional && !headerBranchInLoop && hasExits && headerAndBackEdgeSameFlag -> LoopKind.PostTest
            // by Claude - Multi-block loop with conditional back-edge and header exit, DIFFERENT FLAGS: PreTest
            // Pattern: loop_header: LDA counter; BEQ exit; STA; DEC; BPL loop_header
            // Header (BEQ=Z) and back-edge (BPL=N) check DIFFERENT flags - header has the primary condition.
            // If header condition fails initially, body never executes -> PreTest
            !isSingleBlock && backEdgeIsConditional && headerIsConditional && !headerBranchInLoop && hasExits && !headerAndBackEdgeSameFlag -> LoopKind.PreTest
            // Header is conditional with external exit and UNCONDITIONAL back-edge: PreTest (while)
            // This handles patterns like: loop_header: LDA counter; BEQ exit; loop_body: ...; JMP loop_header
            // The condition is checked before the body executes
            headerIsConditional && !headerBranchInLoop && hasExits && !headerHasBodyInstructions && !backEdgeIsConditional -> LoopKind.PreTest
            // Default to PreTest
            else -> LoopKind.PreTest
        }
    }

    /**
     * Consume edges for a natural loop.
     */
    private fun consumeLoopEdges(loop: NaturalLoop, header: AssemblyBlock) {
        // Consume back-edges
        for ((source, _) in loop.backEdges) {
            tracker.consumeEdge(source, header)
        }

        // Consume internal edges within the loop body
        for (block in loop.body) {
            block.fallThroughExit?.let { target ->
                if (target in loop.body) {
                    tracker.consumeEdge(block, target)
                }
            }
            block.branchExit?.let { target ->
                if (target in loop.body) {
                    tracker.consumeEdge(block, target)
                } else if (target in loop.exits) {
                    // Exit edge - consume as structured (break)
                    tracker.consumeEdge(block, target)
                }
            }
        }
    }

    /**
     * Consume a block's edges as structured (fall-through and branch).
     */
    private fun consumeBlockEdgesAsStructured(block: AssemblyBlock) {
        block.fallThroughExit?.let { target ->
            if (target in allBlocks) {
                tracker.consumeEdge(block, target)
            }
        }
        block.branchExit?.let { target ->
            if (target in allBlocks) {
                tracker.consumeEdge(block, target)
            }
        }
    }
}

// ============================================================================
// Conversion from Region to ControlNode (for compatibility with existing codegen)
// ============================================================================

/**
 * Convert a Region hierarchy to the existing ControlNode hierarchy.
 * This allows gradual migration - the new structural analysis produces Regions,
 * which are then converted to ControlNodes for code generation.
 */
fun Region.toControlNodes(): List<ControlNode> {
    var nextId = 0

    fun Region.convert(): List<ControlNode> {
        return when (this) {
            is BlockRegion -> listOf(BlockNode(id = nextId++, block = block))

            is SequenceRegion -> children.flatMap { it.convert() }

            is IfThenRegion -> {
                val cond = Condition(
                    branchBlock = conditionBlock,
                    branchLine = conditionLine,
                    sense = sense
                )
                listOf(IfNode(
                    id = nextId++,
                    condition = cond,
                    thenBranch = thenRegion.convert().toMutableList(),
                    elseBranch = mutableListOf(),
                    join = joinBlock
                ))
            }

            is IfThenElseRegion -> {
                val cond = Condition(
                    branchBlock = conditionBlock,
                    branchLine = conditionLine,
                    sense = sense
                )
                listOf(IfNode(
                    id = nextId++,
                    condition = cond,
                    thenBranch = thenRegion.convert().toMutableList(),
                    elseBranch = elseRegion.convert().toMutableList(),
                    join = joinBlock
                ))
            }

            is WhileLoopRegion -> {
                val cond = Condition(
                    branchBlock = header,
                    branchLine = conditionLine,
                    sense = continueOnBranch
                )
                listOf(LoopNode(
                    id = nextId++,
                    kind = LoopKind.PreTest,
                    header = header,
                    condition = cond,
                    body = bodyRegion.convert().toMutableList(),
                    continueTargets = setOf(header),
                    breakTargets = exitBlock?.let { setOf(it) } ?: emptySet()
                ))
            }

            is DoWhileLoopRegion -> {
                val cond = Condition(
                    branchBlock = conditionBlock,
                    branchLine = conditionLine,
                    sense = continueOnBranch
                )
                listOf(LoopNode(
                    id = nextId++,
                    kind = LoopKind.PostTest,
                    header = header,
                    condition = cond,
                    body = bodyRegion.convert().toMutableList(),
                    continueTargets = setOf(header),
                    breakTargets = exitBlock?.let { setOf(it) } ?: emptySet()
                ))
            }

            is InfiniteLoopRegion -> {
                listOf(LoopNode(
                    id = nextId++,
                    kind = LoopKind.Infinite,
                    header = header,
                    condition = null,
                    body = bodyRegion.convert().toMutableList(),
                    continueTargets = setOf(header),
                    breakTargets = emptySet()
                ))
            }

            is SwitchRegion -> {
                // Convert switch cases
                val switchCases = cases.map { case ->
                    SwitchNode.Case(
                        matchValues = case.indices,
                        nodes = case.region?.convert()?.toMutableList() ?: mutableListOf()
                    )
                }
                listOf(SwitchNode(
                    id = nextId++,
                    selector = selectorExpr ?: LiteralValue(0),
                    cases = switchCases,
                    defaultBranch = defaultRegion?.convert()?.toMutableList() ?: mutableListOf(),
                    join = joinBlock
                ))
            }

            is UnstructuredRegion -> {
                // For unstructured regions, emit blocks with gotos
                val nodes = mutableListOf<ControlNode>()

                // Add child regions if any
                for (child in childRegions) {
                    nodes.addAll(child.convert())
                }

                // Add remaining blocks as BlockNodes
                val childBlocks = childRegions.flatMapTo(mutableSetOf()) { it.blocks }
                for (block in blocks) {
                    if (block !in childBlocks) {
                        nodes.add(BlockNode(id = nextId++, block = block))
                    }
                }

                // Add GotoNodes for unstructured edges
                for (edge in unstructuredEdges) {
                    nodes.add(GotoNode(id = nextId++, from = edge.source, to = edge.target))
                }

                nodes
            }
        }
    }

    return this.convert()
}

/**
 * Analyze structure and convert to control nodes in one step.
 * This is the main entry point for the new analysis that's compatible with existing code.
 */
fun AssemblyFunction.analyzeStructureToControls(
    config: StructureConfig = StructureConfig()
): List<ControlNode> {
    val result = analyzeStructure(config)
    return result.region.toControlNodes()
}
