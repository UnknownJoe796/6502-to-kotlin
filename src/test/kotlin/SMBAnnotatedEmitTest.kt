package com.ivieleague.decompiler6502tokotlin.hand

import java.io.File
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.assertEquals

class SMBAnnotatedEmitTest {
    @Test
    fun emitAnnotatedSmb() {
        // Parse the SMB disassembly
        val asmFile = File("smbdism.asm")
        assertTrue(asmFile.exists(), "smbdism.asm must exist at project root")
        val code = asmFile.readText().parseToAssemblyCodeFile()

        // Basic block and function analysis (non-ai)
        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        // Ensure outputs directory exists
        val outDir = File("outputs")
        if (!outDir.exists()) outDir.mkdirs()
        val outFile = outDir.resolve("smbdism-annotated.asm")

        outFile.bufferedWriter().use { out ->
            // Emit each line with extra comments from analysis
            for (line in code.lines) {
                // Reconstruct base textual form, similar to InitialParseTest
                val sb = StringBuilder()
                line.label?.let { label ->
                    sb.append(label)
                    sb.append(":  ")
                }
                line.instruction?.let { instr ->
                    sb.append(instr.toString())
                }
                line.data?.let { data ->
                    when (data) {
                        is AssemblyData.Db -> sb.append("DATA: ")
                            .append(data.items.joinToString(", ") { it.toString() })
                    }
                }
                line.constant?.let { c ->
                    sb.append(c.name).append(" = ").append(c.value.toString())
                }
                line.comment?.let { comment ->
                    while (sb.length < 30) sb.append(' ')
                    sb.append("; ")
                    sb.append(comment)
                }

                while (sb.length < 100) sb.append(' ')
                sb.append("; ")
                line.block?.let { b ->
                    if(b.lines.firstOrNull() == line) {
                        b.function?.let { f ->
                            if (b == f.startingBlock) {
                                sb.append("fun(")
                                sb.append(f.inputs?.joinToString(""))
                                sb.append("): ")
                                sb.append(f.outputs?.joinToString(""))
                            }
                        }
                        sb.append(" B")
                    }
                }

                out.appendLine(sb.toString())
            }
        }
    }
}
