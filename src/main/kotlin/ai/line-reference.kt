package com.ivieleague.decompiler6502tokotlin

import java.io.File

data class LineReference<T>(val line: Int, val inside: CodeFile<T>) {
    val content: T get() {
        require(line in inside.lines.indices) { "Line $line out of bounds in ${inside.file?.name ?: "unknown file"}" }
        return inside.lines[line]
    }
    
    val isValid: Boolean get() = line in inside.lines.indices
    
    override fun toString(): String = "${inside.file?.name ?: "code"}:${line + 1}"
}

class CodeFile<T>(val lines: List<T>, val file: File? = null) {
    val size get() = lines.size
    operator fun get(line: Int): LineReference<T> = LineReference(line, this)
    
    fun getOrNull(line: Int): LineReference<T>? =
        if (line in lines.indices) LineReference(line, this) else null
        
    fun filterRefs(predicate: (T) -> Boolean): List<LineReference<T>> =
        lines.mapIndexedNotNull { index, line ->
            if (predicate(line)) LineReference(index, this) else null
        }
        
    fun mapRefs(predicate: (T) -> Boolean): List<LineReference<T>> = filterRefs(predicate)
}

// Type aliases for assembly code
typealias AssemblyLineReference = LineReference<AssemblyLine>
typealias AssemblyCodeFile = CodeFile<AssemblyLine>

// Extension to create AssemblyCodeFile from List<AssemblyLine>
fun List<AssemblyLine>.toCodeFile(file: File? = null): AssemblyCodeFile = CodeFile(this, file)