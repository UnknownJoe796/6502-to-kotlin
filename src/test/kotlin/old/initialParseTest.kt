package com.ivieleague.decompiler6502tokotlin.hand

import java.io.File
import kotlin.test.Test


class InitialParseTest {
    @Test fun addressing() {
        println(AssemblyAddressing.parse("#\$2000")!!::class)
        println(AssemblyAddressing.parse("#\$07d6")!!::class)
    }
    @Test fun smbTest() {
        val parsed = File("smbdism.asm").readText().parseToAssemblyCodeFile()
        File("outputs").resolve("smbdism-parse.txt").writer().use { out ->
            parsed.lines.forEach {
                it.label?.let { label ->
                    out.append("$label:  ")
                }
                it.instruction?.let { instruction ->
                    out.append(instruction.toString())
                }
                it.data?.let { data ->
                    when(data) {
                        is AssemblyData.Db -> out.append("DATA: " + data.items.joinToString(", ") { it.toString() })
                    }
                }
                it.constant?.let { addressing ->
                    out.append(addressing.name)
                    out.append(" = ")
                    out.append(addressing.value.toString())
                }
                it.comment?.let { comment ->
                    out.append("         ; $comment")
                }
                out.appendLine()
            }
        }
    }
}