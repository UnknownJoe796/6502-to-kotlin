package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Test
import java.io.File

class IfElseDebugTest {

    @Test
    fun debugIfElse() {
        val code = """
            CheckValue:
                LDA #${'$'}00
                BEQ IsZero
            NotZero:
                LDX #${'$'}01
                RTS
            IsZero:
                LDX #${'$'}02
                RTS
        """.trimIndent()

        val parsed = code.parseToAssemblyCodeFile()
        val blocks = parsed.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        val func = functions.first()

        val outFile = File("outputs/ifelse-debug.txt")
        outFile.parentFile?.mkdirs()
        val out = outFile.bufferedWriter()

        out.appendLine("Control flow nodes:")
        val controls = func.improveControlFlow()

        fun printNode(node: ControlNode, indent: Int = 0) {
            val prefix = "  ".repeat(indent)
            when (node) {
                is BlockNode -> {
                    out.appendLine("$prefix- Block: ${node.block.label ?: "@${node.block.originalLineIndex}"}")
                }
                is IfNode -> {
                    out.appendLine("$prefix- If:")
                    out.appendLine("$prefix    Then (${node.thenBranch.size} nodes):")
                    node.thenBranch.forEach { printNode(it, indent + 2) }
                    out.appendLine("$prefix    Else (${node.elseBranch.size} nodes):")
                    node.elseBranch.forEach { printNode(it, indent + 2) }
                }
                else -> out.appendLine("$prefix- ${node::class.simpleName}")
            }
        }

        controls.forEach { printNode(it) }

        // Generate Kotlin
        val kFunc = func.toKotlinFunction()
        out.appendLine("\nGenerated Kotlin:")
        out.appendLine(kFunc.toKotlin())

        out.close()
        println("Output written to: ${outFile.absolutePath}")
    }
}
