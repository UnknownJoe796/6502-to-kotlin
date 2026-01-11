package com.ivieleague.decompiler6502tokotlin.hand.stages

import com.ivieleague.decompiler6502tokotlin.hand.*
import java.io.File

/**
 * Test fixtures and utilities for SMB stage testing.
 */
object SMBTestFixtures {

    private val smbFile by lazy {
        File("smbdism.asm").also {
            require(it.exists()) { "smbdism.asm not found in project root" }
        }
    }

    private val allCode by lazy {
        smbFile.readText().parseToAssemblyCodeFile()
    }

    private val allBlocks by lazy {
        allCode.lines.blockify()
    }

    internal val allFunctions by lazy {
        allBlocks.dominators()
        val functions = allBlocks.functionify()
        // Analyze control flow for all functions
        functions.analyzeControls()
        functions
    }

    /**
     * Get all blocks that belong to a specific function.
     * This properly handles shared epilogue code and function boundaries.
     */
    fun getFunctionBlocks(funcName: String): List<AssemblyBlock> {
        val func = allFunctions.firstOrNull { it.startingBlock.label == funcName }
            ?: error("Function $funcName not found. Available functions: ${allFunctions.take(10).map { it.startingBlock.label }}")

        return allBlocks.filter { it.function == func }
    }

    /**
     * Load and return the function object for analysis.
     */
    fun loadFunction(name: String): AssemblyFunction {
        return allFunctions.firstOrNull { it.startingBlock.label == name }
            ?: error("Function $name not found after analysis")
    }

    /**
     * Get just the blocks for blockification testing (before function analysis).
     * This returns blocks that would be part of this function based on reachability.
     */
    fun getBlocksForFunction(funcName: String): List<AssemblyBlock> {
        // Find the entry block
        val entryBlock = allBlocks.firstOrNull { it.label == funcName }
            ?: error("Block with label $funcName not found")

        // Collect reachable blocks until we hit another function entry or RTS
        val reachable = mutableSetOf<AssemblyBlock>()
        val toVisit = mutableListOf(entryBlock)
        val functionEntries = allBlocks.filter { block ->
            allCode.lines.any { line ->
                line.instruction?.op == AssemblyOp.JSR &&
                (line.instruction.address as? AssemblyAddressing.Direct)?.label == block.label
            }
        }.toSet()

        while (toVisit.isNotEmpty()) {
            val current = toVisit.removeFirst()
            if (current in reachable) continue
            if (current != entryBlock && current in functionEntries) continue // Hit another function

            reachable.add(current)

            current.fallThroughExit?.let { if (it !in reachable) toVisit.add(it) }
            current.branchExit?.let { if (it !in reachable) toVisit.add(it) }
        }

        return reachable.sortedBy { it.originalLineIndex }
    }
}

/**
 * Helper to check if a block is dominated by another.
 */
fun isDominatedBy(block: AssemblyBlock, dominator: AssemblyBlock): Boolean {
    var current: AssemblyBlock? = block
    while (current != null) {
        if (current == dominator) return true
        current = current.immediateDominator
    }
    return false
}

/**
 * Pretty-print control flow for debugging.
 */
fun List<ControlNode>.prettyPrint(indent: Int = 0): String = buildString {
    for (node in this) {
        append("  ".repeat(indent))
        when (node) {
            is BlockNode -> {
                appendLine("Block: ${node.block.label ?: "@${node.block.originalLineIndex}"}")
            }
            is IfNode -> {
                val condBlock = node.condition.branchBlock
                appendLine("If (${condBlock.label ?: "@${condBlock.originalLineIndex}"} ${if (node.condition.sense) "taken" else "not-taken"}):")
                append("  ".repeat(indent + 1))
                appendLine("Then:")
                append(node.thenBranch.prettyPrint(indent + 2))
                if (node.elseBranch.isNotEmpty()) {
                    append("  ".repeat(indent + 1))
                    appendLine("Else:")
                    append(node.elseBranch.prettyPrint(indent + 2))
                }
            }
            is LoopNode -> {
                appendLine("Loop (${node.kind}):")
                append(node.body.prettyPrint(indent + 1))
            }
            is GotoNode -> {
                appendLine("Goto: ${node.to.label ?: "@${node.to.originalLineIndex}"}")
            }
            is BreakNode -> {
                appendLine("Break")
            }
            is ContinueNode -> {
                appendLine("Continue")
            }
            is SwitchNode -> {
                appendLine("Switch:")
                for ((value, branch) in node.cases) {
                    append("  ".repeat(indent + 1))
                    appendLine("Case $value:")
                    append(branch.prettyPrint(indent + 2))
                }
            }
        }
    }
}
