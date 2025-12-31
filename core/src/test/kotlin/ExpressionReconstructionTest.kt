package com.ivieleague.decompiler6502tokotlin.hand

import com.ivieleague.decompiler6502tokotlin.hand.stages.SMBTestFixtures
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests for expression reconstruction from 6502 flag operations.
 */
class ExpressionReconstructionTest {

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `CheckPlayerVertical reconstructs comparison expressions`() {
        val func = SMBTestFixtures.loadFunction("CheckPlayerVertical")

        // Apply expression reconstruction
        func.reconstructExpressions()

        val controls = func.asControls
        assertNotNull(controls, "Control flow should be analyzed")

        // Find IfNodes and check their conditions
        fun findIfNodes(nodes: List<ControlNode>): List<IfNode> {
            val result = mutableListOf<IfNode>()
            fun collect(node: ControlNode) {
                when (node) {
                    is IfNode -> {
                        result.add(node)
                        node.thenBranch.forEach { collect(it) }
                        node.elseBranch.forEach { collect(it) }
                    }
                    is LoopNode -> node.body.forEach { collect(it) }
                    else -> {}
                }
            }
            nodes.forEach { collect(it) }
            return result
        }

        val ifNodes = findIfNodes(controls)
        assertTrue(ifNodes.isNotEmpty(), "Should have at least one IfNode")

        // Check that at least one condition was reconstructed (not UnknownCond)
        val reconstructedCount = ifNodes.count { it.condition.expr !is UnknownCond }
        println("CheckPlayerVertical: Reconstructed $reconstructedCount/${ifNodes.size} conditions")

        // Print condition expressions for inspection
        ifNodes.forEachIndexed { index, ifNode ->
            val exprType = ifNode.condition.expr::class.simpleName
            val kotlinExpr = try {
                ifNode.condition.toKotlinCondition().toKotlin()
            } catch (e: Exception) {
                "Error: ${e.message}"
            }
            println("  [$index] $exprType: $kotlinExpr")
        }

        // At least some conditions should be reconstructed
        assertTrue(reconstructedCount > 0, "At least one condition should be reconstructed from UnknownCond")
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `HandlePipeEntry reconstructs guard clause conditions`() {
        val func = SMBTestFixtures.loadFunction("HandlePipeEntry")

        // Apply expression reconstruction
        func.reconstructExpressions()

        val controls = func.asControls
        assertNotNull(controls, "Control flow should be analyzed")

        // Collect all IfNodes
        fun findIfNodes(nodes: List<ControlNode>): List<IfNode> {
            val result = mutableListOf<IfNode>()
            fun collect(node: ControlNode) {
                when (node) {
                    is IfNode -> {
                        result.add(node)
                        node.thenBranch.forEach { collect(it) }
                        node.elseBranch.forEach { collect(it) }
                    }
                    is LoopNode -> node.body.forEach { collect(it) }
                    else -> {}
                }
            }
            nodes.forEach { collect(it) }
            return result
        }

        val ifNodes = findIfNodes(controls)
        assertTrue(ifNodes.isNotEmpty(), "Should have guard clauses as IfNodes")

        val reconstructedCount = ifNodes.count { it.condition.expr !is UnknownCond }
        println("HandlePipeEntry: Reconstructed $reconstructedCount/${ifNodes.size} conditions")

        // Print reconstructed expressions
        ifNodes.forEachIndexed { index, ifNode ->
            val exprType = ifNode.condition.expr::class.simpleName
            val kotlinExpr = try {
                ifNode.condition.toKotlinCondition().toKotlin()
            } catch (e: Exception) {
                "Error: ${e.message}"
            }
            println("  [$index] $exprType: $kotlinExpr")
        }

        assertTrue(reconstructedCount > 0, "Should reconstruct at least some guard clause conditions")
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `MoveSpritesOffscreen reconstructs loop condition`() {
        val func = SMBTestFixtures.loadFunction("MoveSpritesOffscreen")

        // Apply expression reconstruction
        func.reconstructExpressions()

        val controls = func.asControls
        assertNotNull(controls, "Control flow should be analyzed")

        // Find LoopNodes
        fun findLoopNodes(nodes: List<ControlNode>): List<LoopNode> {
            val result = mutableListOf<LoopNode>()
            fun collect(node: ControlNode) {
                when (node) {
                    is LoopNode -> {
                        result.add(node)
                        node.body.forEach { collect(it) }
                    }
                    is IfNode -> {
                        node.thenBranch.forEach { collect(it) }
                        node.elseBranch.forEach { collect(it) }
                    }
                    else -> {}
                }
            }
            nodes.forEach { collect(it) }
            return result
        }

        val loopNodes = findLoopNodes(controls)
        assertTrue(loopNodes.isNotEmpty(), "Should have at least one loop")

        // Check loop conditions
        loopNodes.forEachIndexed { index, loopNode ->
            loopNode.condition?.let { cond ->
                val exprType = cond.expr::class.simpleName
                val kotlinExpr = try {
                    cond.toKotlinCondition().toKotlin()
                } catch (e: Exception) {
                    "Error: ${e.message}"
                }
                println("Loop [$index] condition: $exprType: $kotlinExpr")
            } ?: println("Loop [$index] has no condition (infinite loop)")
        }
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `OffscreenBoundsCheck reconstructs complex conditionals`() {
        val func = SMBTestFixtures.loadFunction("OffscreenBoundsCheck")

        // Apply expression reconstruction
        func.reconstructExpressions()

        val controls = func.asControls
        assertNotNull(controls, "Control flow should be analyzed")

        // Count reconstructed conditions
        fun countReconstructed(nodes: List<ControlNode>): Pair<Int, Int> {
            var total = 0
            var reconstructed = 0

            fun count(node: ControlNode) {
                when (node) {
                    is IfNode -> {
                        total++
                        if (node.condition.expr !is UnknownCond) reconstructed++
                        node.thenBranch.forEach { count(it) }
                        node.elseBranch.forEach { count(it) }
                    }
                    is LoopNode -> {
                        node.condition?.let {
                            total++
                            if (it.expr !is UnknownCond) reconstructed++
                        }
                        node.body.forEach { count(it) }
                    }
                    else -> {}
                }
            }
            nodes.forEach { count(it) }
            return reconstructed to total
        }

        val (reconstructed, total) = countReconstructed(controls)
        println("OffscreenBoundsCheck: Reconstructed $reconstructed/$total conditions")

        assertTrue(total > 0, "Should have conditional nodes")
        assertTrue(reconstructed > 0, "Should reconstruct at least some conditions")
    }
}
