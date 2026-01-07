package dumb

import java.io.File
import kotlin.test.Test

class TempTest {
    @Test
    fun test() {
        val f = File("/Users/jivie/Library/Application Support/JetBrains/IntelliJIdea2025.3/scratches/scratch_344.txt")

        data class Edit(val lineNumber: Int, var text: String, val add: Boolean)

        val lines = f.readLines().let {
            val out = ArrayList<Edit>()
            var last: Edit? = null
            it.forEach { l ->
                val trimmed = l.trim()
                val n = trimmed.substringBefore(' ', "").toIntOrNull()
                if (n == null) {
                    last?.text += trimmed.drop(1)
                } else {
                    val c = trimmed.substringAfter(' ', "").trim()
                    val add = c.startsWith('+')
                    last = Edit(n, c.drop(1), add)
                    out.add(last)
                }
            }
            out
        }
        var lastLine = -1
        lines
            .filter { !it.add }
            .groupBy { it.lineNumber }
            .mapValues { it.value.first() }
            .entries
            .sortedBy { it.key }
            .map { it.value }
            .forEach {
                if (lastLine != it.lineNumber - 1)  println("// Line ${it.lineNumber}")
                println(it.text)
                lastLine = it.lineNumber
            }
    }
}