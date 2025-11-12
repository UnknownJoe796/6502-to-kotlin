package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

class SMBControlsDisplayTest {

    @Test
    fun emitIndentedControlStructures() {
        // Parse the SMB disassembly
        val asmFile = File("smbdism.asm")
        assertTrue(asmFile.exists(), "smbdism.asm must exist at project root")
        val code = asmFile.readText().parseToAssemblyCodeFile()

        // Basic block and function analysis
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertTrue(functions.isNotEmpty(), "Expected at least one function in SMB disassembly")

        // Ensure outputs directory exists
        val outDir = File("outputs")
        if (!outDir.exists()) outDir.mkdirs()
        val outFile = outDir.resolve("smb-controls-structure.txt")

        outFile.bufferedWriter().use { out ->
            for (f in functions) {
                val fname = f.startingBlock.label ?: ("func@" + f.startingBlock.originalLineIndex)
                out.appendLine("def $fname():")

                // Build/ensure controls with improvements
                val nodes = f.improveControlFlow()
                nodes.forEach { it.print(out, 1) }
                out.appendLine()
            }
        }
    }
}

/**
 * Print a control node in a format that mimics final code generation.
 *
 * Key strategy to avoid duplication:
 * - For IfNode/LoopNode: Split the condition block into:
 *   1. Instructions BEFORE the branch (setup code)
 *   2. The branch itself (becomes the condition)
 * - Only print setup code once, branch becomes part of control structure
 * - Track consumed blocks and partially-consumed blocks (where branch was extracted)
 */
fun ControlNode.print(
    out: Appendable,
    indent: Int = 0,
    consumed: MutableSet<AssemblyBlock> = mutableSetOf(),
    partiallyConsumed: MutableMap<AssemblyBlock, AssemblyLine> = mutableMapOf()
) {
    when(this) {
        is BlockNode -> {
            // Check if this block has been fully or partially consumed
            when {
                this.block in consumed -> {
                    // Fully consumed, skip
                }
                this.block in partiallyConsumed -> {
                    // Partially consumed - only print instructions AFTER the branch
                    val branchLine = partiallyConsumed[this.block]!!
                    var foundBranch = false
                    this.block.lines.filter { it.instruction != null }.forEach { line ->
                        if (line == branchLine) {
                            foundBranch = true
                        } else if (foundBranch) {
                            // Print instructions after the branch
                            out.appendLine("  ".repeat(indent) + line.originalLine?.trim())
                        }
                    }
                    consumed.add(this.block)
                }
                else -> {
                    // Not consumed - print all instructions
                    // Show block label if it exists (helps identify reused blocks)
                    val blockLabel = this.block.label ?: this.block.lines.firstOrNull()?.label
                    if (blockLabel != null) {
                        out.appendLine("  ".repeat(indent) + "// Block: $blockLabel")
                    }

                    this.block.lines.filter { it.instruction != null }.forEach {
                        out.appendLine("  ".repeat(indent) + it.originalLine?.trim())
                    }
                    consumed.add(this.block)
                }
            }
        }
        is BreakNode -> {
            out.appendLine("  ".repeat(indent) + "break@loop${this.loop.id}")
        }
        is ContinueNode -> {
            out.appendLine("  ".repeat(indent) + "continue@loop${this.loop.id}")
        }
        is GotoNode -> {
            out.appendLine("  ".repeat(indent) + "goto ${this.to.label ?: "@${this.to.originalLineIndex}"}")
        }
        is IfNode -> {
            // Print instructions BEFORE the branch in the condition block
            val condBlock = this.condition.branchBlock
            condBlock.lines.filter { it.instruction != null }.forEach { line ->
                if (line == this.condition.branchLine) {
                    // Stop before the branch
                    return@forEach
                }
                if (condBlock !in consumed && condBlock !in partiallyConsumed) {
                    out.appendLine("  ".repeat(indent) + line.originalLine?.trim())
                }
            }

            // Mark the condition block as partially consumed (branch extracted)
            partiallyConsumed[condBlock] = this.condition.branchLine

            // Print symbolic if statement (will be replaced with actual expression later)
            val branch = this.condition.branchLine.instruction
            val sense = if (this.condition.sense) "" else "!"
            out.appendLine("  ".repeat(indent) + "if ($sense$branch) {  // from ${condBlock.label ?: "@${condBlock.originalLineIndex}"}")

            this.thenBranch.forEach { it.print(out, indent + 1, consumed, partiallyConsumed) }

            if(this.elseBranch.isNotEmpty()) {
                out.appendLine("  ".repeat(indent) + "} else {")
                this.elseBranch.forEach { it.print(out, indent + 1, consumed, partiallyConsumed) }
            }
            out.appendLine("  ".repeat(indent) + "}")
        }
        is LoopNode -> {
            when (this.kind) {
                LoopKind.PreTest -> {
                    // while (condition) { body }
                    // Print instructions BEFORE the condition branch
                    if (this.condition != null) {
                        val condBlock = this.condition.branchBlock
                        condBlock.lines.filter { it.instruction != null }.forEach { line ->
                            if (line == this.condition.branchLine) {
                                return@forEach
                            }
                            if (condBlock !in consumed && condBlock !in partiallyConsumed) {
                                out.appendLine("  ".repeat(indent) + line.originalLine?.trim())
                            }
                        }
                        partiallyConsumed[condBlock] = this.condition.branchLine
                    }

                    val branch = this.condition?.branchLine?.instruction?.toString() ?: "true"
                    val sense = if (this.condition?.sense == true) "" else "!"
                    out.appendLine("  ".repeat(indent) + "while ($sense$branch) {  // loop${this.id}")
                    this.body.forEach { it.print(out, indent + 1, consumed, partiallyConsumed) }
                    out.appendLine("  ".repeat(indent) + "}")
                }
                LoopKind.PostTest -> {
                    // do { body } while (condition)
                    out.appendLine("  ".repeat(indent) + "do {  // loop${this.id}")
                    this.body.forEach { it.print(out, indent + 1, consumed, partiallyConsumed) }

                    // Extract the condition branch from the body
                    if (this.condition != null) {
                        partiallyConsumed[this.condition.branchBlock] = this.condition.branchLine
                    }

                    val branch = this.condition?.branchLine?.instruction?.toString() ?: "true"
                    val sense = if (this.condition?.sense == true) "" else "!"
                    out.appendLine("  ".repeat(indent) + "} while ($sense$branch)")
                }
                LoopKind.Infinite -> {
                    // loop { body }
                    out.appendLine("  ".repeat(indent) + "loop {  // loop${this.id} (infinite)")
                    this.body.forEach { it.print(out, indent + 1, consumed, partiallyConsumed) }
                    out.appendLine("  ".repeat(indent) + "}")
                }
            }

            // Mark header as consumed after processing
            consumed.add(this.header)
        }
        is SwitchNode -> {
            out.appendLine("  ".repeat(indent) + "switch (${this.selector}) {")
            this.cases.forEach { c ->
                out.appendLine("  ".repeat(indent + 1) + "case ${c.matchValues.joinToString(", ")} ->")
                c.nodes.forEach { it.print(out, indent + 2, consumed, partiallyConsumed) }
            }
            if (this.defaultBranch.isNotEmpty()) {
                out.appendLine("  ".repeat(indent + 1) + "else ->")
                this.defaultBranch.forEach { it.print(out, indent + 2, consumed, partiallyConsumed) }
            }
            out.appendLine("  ".repeat(indent) + "}")
        }
    }
}
