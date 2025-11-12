package com.ivieleague.decompiler6502tokotlin.hand.stages

import com.ivieleague.decompiler6502tokotlin.hand.stages.SMBTestFixtures.loadFunction
import com.ivieleague.decompiler6502tokotlin.hand.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit

/**
 * Debug test that outputs control flow structures as plain text
 * to help analyze and write better assertions.
 */
class DebugControlFlowStructures {

    private fun printControlFlow(funcName: String) {
        println("\n========== $funcName ==========")
        val func = loadFunction(funcName)

        println("\n--- Function Info ---")
        println("Entry: ${func.startingBlock.label}")
        println("Inputs: ${func.inputs?.joinToString()}")
        println("Outputs: ${func.outputs?.joinToString()}")
        println("Clobbers: ${func.clobbers?.joinToString()}")

        println("\n--- Control Flow Nodes (${func.asControls?.size ?: 0} nodes) ---")
        func.asControls?.forEachIndexed { index, node ->
            println("\n[$index] ${node.javaClass.simpleName}")
            when (node) {
                is BlockNode -> {
                    println("  Block: ${node.block.label ?: "@${node.block.originalLineIndex}"}")
                    println("  Lines: ${node.block.lines.size}")
                    node.block.lines.take(3).forEach { line ->
                        val instr = line.instruction
                        if (instr != null) {
                            println("    ${instr.op} ${instr.address ?: ""}")
                        }
                    }
                    if (node.block.lines.size > 3) {
                        println("    ... (${node.block.lines.size - 3} more)")
                    }
                }
                is IfNode -> {
                    println("  Condition block: ${node.condition.branchBlock.label ?: "@${node.condition.branchBlock.originalLineIndex}"}")
                    println("  Sense: ${node.condition.sense}")
                    println("  Then branch: ${node.thenBranch.size} nodes")
                    println("  Else branch: ${node.elseBranch.size} nodes")
                }
                is LoopNode -> {
                    println("  Type: ${node.javaClass.simpleName}")
                    println("  Body nodes: ${node.body.size}")
                    println("  Entry: ${node.entry.label ?: "@${node.entry.originalLineIndex}"}")
                }
                is GotoNode -> {
                    println("  Jump from: ${node.entry.label ?: "@${node.entry.originalLineIndex}"}")
                }
                is BreakNode -> {
                    println("  Break from loop")
                }
                is ContinueNode -> {
                    println("  Continue to loop start")
                }
                is SwitchNode -> {
                    println("  Cases: ${node.cases.size}")
                }
            }
        }

        println("\n--- Blocks Graph ---")
        val blocks = mutableListOf<AssemblyBlock>()
        fun collect(b: AssemblyBlock?) {
            if (b == null || b in blocks) return
            blocks.add(b)
            collect(b.fallThroughExit)
            collect(b.branchExit)
        }
        collect(func.startingBlock)

        blocks.forEach { block ->
            println("\n${block.label ?: "@${block.originalLineIndex}"}")
            println("  Lines: ${block.lines.size}")
            println("  Fall through: ${block.fallThroughExit?.label ?: block.fallThroughExit?.originalLineIndex}")
            println("  Branch: ${block.branchExit?.label ?: block.branchExit?.originalLineIndex}")
            println("  Entered from: ${block.enteredFrom.map { it.label ?: "@${it.originalLineIndex}" }}")
            println("  Dominator: ${block.immediateDominator?.label ?: block.immediateDominator?.originalLineIndex}")
        }

        println("\n")
    }

    @Test
    @Timeout(30, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `debug all control flow structures`() {
        val functions = listOf(
            "InitializeMemory",
            "FloateyNumbersRoutine",
            "MusicHandler",
            "ImpedePlayerMove",
            "MoveSpritesOffscreen",
            "GetAreaObjXPosition",
            "GetAreaObjYPosition",
            "PlayerEnemyDiff",
            "InitVStf",
            "PlayerLakituDiff",
            "CheckForSolidMTiles",
            "CheckPlayerVertical",
            "HandlePipeEntry",
            "OffscreenBoundsCheck",
            "BubbleCheck"
        )

        functions.forEach { funcName ->
            printControlFlow(funcName)
        }
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `debug simple functions`() {
        val output = buildString {
            listOf(
                "GetAreaObjXPosition",
                "GetAreaObjYPosition",
                "PlayerEnemyDiff",
                "InitVStf"
            ).forEach { funcName ->
                append(captureControlFlow(funcName))
            }
        }
        java.io.File("outputs/debug-simple-functions.txt").writeText(output)
        println("Wrote debug output to outputs/debug-simple-functions.txt")
    }

    private fun captureControlFlow(funcName: String): String = buildString {
        appendLine("\n========== $funcName ==========")
        val func = loadFunction(funcName)

        appendLine("\n--- Function Info ---")
        appendLine("Entry: ${func.startingBlock.label}")
        appendLine("Inputs: ${func.inputs?.joinToString()}")
        appendLine("Outputs: ${func.outputs?.joinToString()}")
        appendLine("Clobbers: ${func.clobbers?.joinToString()}")

        appendLine("\n--- Control Flow Nodes (${func.asControls?.size ?: 0} nodes) ---")
        func.asControls?.forEachIndexed { index, node ->
            appendLine("\n[$index] ${node.javaClass.simpleName}")
            when (node) {
                is BlockNode -> {
                    appendLine("  Block: ${node.block.label ?: "@${node.block.originalLineIndex}"}")
                    appendLine("  Lines: ${node.block.lines.size}")
                    node.block.lines.take(3).forEach { line ->
                        val instr = line.instruction
                        if (instr != null) {
                            appendLine("    ${instr.op} ${instr.address ?: ""}")
                        }
                    }
                    if (node.block.lines.size > 3) {
                        appendLine("    ... (${node.block.lines.size - 3} more)")
                    }
                }
                is IfNode -> {
                    appendLine("  Condition block: ${node.condition.branchBlock.label ?: "@${node.condition.branchBlock.originalLineIndex}"}")
                    appendLine("  Sense: ${node.condition.sense}")
                    appendLine("  Then branch: ${node.thenBranch.size} nodes")
                    appendLine("  Else branch: ${node.elseBranch.size} nodes")
                }
                is LoopNode -> {
                    appendLine("  Type: ${node.javaClass.simpleName}")
                    appendLine("  Body nodes: ${node.body.size}")
                    appendLine("  Entry: ${node.entry.label ?: "@${node.entry.originalLineIndex}"}")
                }
                is GotoNode -> {
                    appendLine("  Jump from: ${node.entry.label ?: "@${node.entry.originalLineIndex}"}")
                }
                is BreakNode -> {
                    appendLine("  Break from loop")
                }
                is ContinueNode -> {
                    appendLine("  Continue to loop start")
                }
                is SwitchNode -> {
                    appendLine("  Cases: ${node.cases.size}")
                }
            }
        }

        appendLine("\n--- Blocks Graph ---")
        val blocks = mutableListOf<AssemblyBlock>()
        fun collect(b: AssemblyBlock?) {
            if (b == null || b in blocks) return
            blocks.add(b)
            collect(b.fallThroughExit)
            collect(b.branchExit)
        }
        collect(func.startingBlock)

        blocks.forEach { block ->
            appendLine("\n${block.label ?: "@${block.originalLineIndex}"}")
            appendLine("  Lines: ${block.lines.size}")
            appendLine("  Fall through: ${block.fallThroughExit?.label ?: block.fallThroughExit?.originalLineIndex}")
            appendLine("  Branch: ${block.branchExit?.label ?: block.branchExit?.originalLineIndex}")
            appendLine("  Entered from: ${block.enteredFrom.map { it.label ?: "@${it.originalLineIndex}" }}")
            appendLine("  Dominator: ${block.immediateDominator?.label ?: block.immediateDominator?.originalLineIndex}")
        }

        appendLine("\n")
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `debug loop functions`() {
        val output = buildString {
            listOf(
                "InitializeMemory",
                "MoveSpritesOffscreen"
            ).forEach { append(captureControlFlow(it)) }
        }
        java.io.File("outputs/debug-loop-functions.txt").writeText(output)
        println("Wrote debug output to outputs/debug-loop-functions.txt")
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `debug conditional functions`() {
        val output = buildString {
            listOf(
                "CheckPlayerVertical",
                "HandlePipeEntry",
                "OffscreenBoundsCheck",
                "BubbleCheck",
                "PlayerLakituDiff"
            ).forEach { append(captureControlFlow(it)) }
        }
        java.io.File("outputs/debug-conditional-functions.txt").writeText(output)
        println("Wrote debug output to outputs/debug-conditional-functions.txt")
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `debug state machine functions`() {
        val output = buildString {
            listOf(
                "FloateyNumbersRoutine",
                "MusicHandler",
                "ImpedePlayerMove"
            ).forEach { append(captureControlFlow(it)) }
        }
        java.io.File("outputs/debug-complex-functions.txt").writeText(output)
        println("Wrote debug output to outputs/debug-complex-functions.txt")
    }
}
