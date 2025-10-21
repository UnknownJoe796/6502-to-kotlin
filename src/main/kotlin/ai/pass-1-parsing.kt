package com.ivieleague.decompiler6502tokotlin

enum class AssemblyFlag {
    N, V, Z, C
}

enum class AddressCategory {
    IMPLIED,
    ACCUMULATOR,
    IMMEDIATE,
    MEM,
    MEM_X,
    MEM_Y,
    INDIRECT_X,
    INDIRECT_Y,
    JMP_INDIRECT
}

enum class AssemblyOp(
    val description: String,
    val affectedFlags: Set<AssemblyFlag> = setOf(),
    val consumedFlag: AssemblyFlag? = null,
    val isBranch: Boolean = false,
    val flagPositive: Boolean = false,
    val allowedCategories: Set<AddressCategory> = emptySet()
) {
    LDA(
        description = "Load Accumulator",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.Z),
        allowedCategories = setOf(
            AddressCategory.IMMEDIATE, AddressCategory.MEM, AddressCategory.MEM_X, AddressCategory.MEM_Y,
            AddressCategory.INDIRECT_X, AddressCategory.INDIRECT_Y
        )
    ),
    LDX(
        description = "Load X Register",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.Z),
        allowedCategories = setOf(AddressCategory.IMMEDIATE, AddressCategory.MEM, AddressCategory.MEM_Y)
    ),
    LDY(
        description = "Load Y Register",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.Z),
        allowedCategories = setOf(AddressCategory.IMMEDIATE, AddressCategory.MEM, AddressCategory.MEM_X)
    ),
    STA(
        description = "Store Accumulator",
        allowedCategories = setOf(
            AddressCategory.MEM, AddressCategory.MEM_X, AddressCategory.MEM_Y,
            AddressCategory.INDIRECT_X, AddressCategory.INDIRECT_Y
        )
    ),
    STX(
        description = "Store X Register",
        allowedCategories = setOf(AddressCategory.MEM, AddressCategory.MEM_Y)
    ),
    STY(
        description = "Store Y Register",
        allowedCategories = setOf(AddressCategory.MEM, AddressCategory.MEM_X)
    ),
    TAX(
        description = "Transfer accumulator to X",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.Z),
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    TAY(
        description = "Transfer accumulator to Y",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.Z),
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    TXA(
        description = "Transfer X to accumulator",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.Z),
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    TYA(
        description = "Transfer Y to accumulator",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.Z),
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    TSX(
        description = "Transfer stack pointer to X",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.Z),
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    TXS(
        description = "Transfer X to stack pointer",
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    PHA(
        description = "Push accumulator on stack",
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    PHP(
        description = "Push processor status on stack",
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    PLA(
        description = "Pull accumulator from stack",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.Z),
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    PLP(
        description = "Pull processor status from stack",
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    AND(
        description = "Logical AND",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.Z),
        allowedCategories = setOf(
            AddressCategory.IMMEDIATE, AddressCategory.MEM, AddressCategory.MEM_X, AddressCategory.MEM_Y,
            AddressCategory.INDIRECT_X, AddressCategory.INDIRECT_Y
        )
    ),
    EOR(
        description = "Exclusive OR",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.Z),
        allowedCategories = setOf(
            AddressCategory.IMMEDIATE, AddressCategory.MEM, AddressCategory.MEM_X, AddressCategory.MEM_Y,
            AddressCategory.INDIRECT_X, AddressCategory.INDIRECT_Y
        )
    ),
    ORA(
        description = "Logical Inclusive OR",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.Z),
        allowedCategories = setOf(
            AddressCategory.IMMEDIATE, AddressCategory.MEM, AddressCategory.MEM_X, AddressCategory.MEM_Y,
            AddressCategory.INDIRECT_X, AddressCategory.INDIRECT_Y
        )
    ),
    BIT(
        description = "Bit Test",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.V, AssemblyFlag.Z),
        allowedCategories = setOf(AddressCategory.MEM)
    ),
    ADC(
        description = "Add with Carry",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.V, AssemblyFlag.Z, AssemblyFlag.C),
        consumedFlag = AssemblyFlag.C,
        allowedCategories = setOf(
            AddressCategory.IMMEDIATE, AddressCategory.MEM, AddressCategory.MEM_X, AddressCategory.MEM_Y,
            AddressCategory.INDIRECT_X, AddressCategory.INDIRECT_Y
        )
    ),
    SBC(
        description = "Subtract with Carry",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.V, AssemblyFlag.Z, AssemblyFlag.C),
        consumedFlag = AssemblyFlag.C,
        allowedCategories = setOf(
            AddressCategory.IMMEDIATE, AddressCategory.MEM, AddressCategory.MEM_X, AddressCategory.MEM_Y,
            AddressCategory.INDIRECT_X, AddressCategory.INDIRECT_Y
        )
    ),
    CMP(
        description = "Compare accumulator",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.Z, AssemblyFlag.C),
        allowedCategories = setOf(
            AddressCategory.IMMEDIATE, AddressCategory.MEM, AddressCategory.MEM_X, AddressCategory.MEM_Y,
            AddressCategory.INDIRECT_X, AddressCategory.INDIRECT_Y
        )
    ),
    CPX(
        description = "Compare X register",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.Z, AssemblyFlag.C),
        allowedCategories = setOf(AddressCategory.IMMEDIATE, AddressCategory.MEM)
    ),
    CPY(
        description = "Compare Y register",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.Z, AssemblyFlag.C),
        allowedCategories = setOf(AddressCategory.IMMEDIATE, AddressCategory.MEM)
    ),
    INC(
        description = "Increment a memory location",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.Z),
        allowedCategories = setOf(AddressCategory.MEM, AddressCategory.MEM_X)
    ),
    INX(
        description = "Increment the X register",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.Z),
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    INY(
        description = "Increment the Y register",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.Z),
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    DEC(
        description = "Decrement a memory location",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.Z),
        allowedCategories = setOf(AddressCategory.MEM, AddressCategory.MEM_X)
    ),
    DEX(
        description = "Decrement the X register",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.Z),
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    DEY(
        description = "Decrement the Y register",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.Z),
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    ASL(
        description = "Arithmetic Shift Left",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.Z, AssemblyFlag.C),
        allowedCategories = setOf(AddressCategory.ACCUMULATOR, AddressCategory.MEM, AddressCategory.MEM_X)
    ),
    LSR(
        description = "Logical Shift Right",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.Z, AssemblyFlag.C),
        allowedCategories = setOf(AddressCategory.ACCUMULATOR, AddressCategory.MEM, AddressCategory.MEM_X)
    ),
    ROL(
        description = "Rotate Left",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.Z, AssemblyFlag.C),
        allowedCategories = setOf(AddressCategory.ACCUMULATOR, AddressCategory.MEM, AddressCategory.MEM_X)
    ),
    ROR(
        description = "Rotate Right",
        affectedFlags = setOf(AssemblyFlag.N, AssemblyFlag.Z, AssemblyFlag.C),
        allowedCategories = setOf(AddressCategory.ACCUMULATOR, AddressCategory.MEM, AddressCategory.MEM_X)
    ),
    JMP(
        description = "Jump to another location",
        allowedCategories = setOf(AddressCategory.MEM, AddressCategory.JMP_INDIRECT)
    ),
    JSR(
        description = "Jump to a subroutine",
        allowedCategories = setOf(AddressCategory.MEM)
    ),
    RTS(
        description = "Return from subroutine",
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    BCC(
        description = "Branch if carry flag clear",
        consumedFlag = AssemblyFlag.C,
        isBranch = true,
        flagPositive = false
    ),
    BCS(
        description = "Branch if carry flag set",
        consumedFlag = AssemblyFlag.C,
        isBranch = true,
        flagPositive = true
    ),
    BEQ(
        description = "Branch if zero flag set",
        consumedFlag = AssemblyFlag.Z,
        isBranch = true,
        flagPositive = true
    ),
    BMI(
        description = "Branch if negative flag set",
        consumedFlag = AssemblyFlag.N,
        isBranch = true,
        flagPositive = true
    ),
    BNE(
        description = "Branch if zero flag clear",
        consumedFlag = AssemblyFlag.Z,
        isBranch = true,
        flagPositive = false
    ),
    BPL(
        description = "Branch if negative flag clear",
        consumedFlag = AssemblyFlag.N,
        isBranch = true,
        flagPositive = false
    ),
    BVC(
        description = "Branch if overflow flag clear",
        consumedFlag = AssemblyFlag.V,
        isBranch = true,
        flagPositive = false
    ),
    BVS(
        description = "Branch if overflow flag set",
        consumedFlag = AssemblyFlag.V,
        isBranch = true,
        flagPositive = true
    ),
    CLC(
        description = "Clear carry flag",
        affectedFlags = setOf(AssemblyFlag.C),
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    CLD(
        description = "Clear decimal mode flag",
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    CLI(
        description = "Clear interrupt disable flag",
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    CLV(
        description = "Clear overflow flag",
        affectedFlags = setOf(AssemblyFlag.V),
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    SEC(
        description = "Set carry flag",
        affectedFlags = setOf(AssemblyFlag.C),
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    SED(
        description = "Set decimal mode flag",
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    SEI(
        description = "Set interrupt disable flag",
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    BRK(
        description = "Force an interrupt",
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    NOP(
        description = "No Operation",
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    RTI(
        description = "Return from Interrupt",
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    ;

    companion object {
        fun parse(text: String): AssemblyOp = AssemblyOp.valueOf(text.uppercase())
    }
}

/**
 * Assembly-time constant definition (using = directive)
 */
data class AssemblyConstant(
    val name: String,
    val value: AssemblyAddressing  // Can be hex, decimal, or expression
)

data class AssemblyLine(
    val label: String? = null,
    val instruction: AssemblyInstruction? = null,
    val data: AssemblyData? = null,
    val constant: AssemblyConstant? = null,
    val comment: String? = null,
    val originalLine: String? = null,
) {
    override fun toString(): String = buildString {
        if (label != null) {
            append(label)
            append(": ")
        }
        if (instruction != null) {
            append(instruction)
        } else if (data != null) {
            append(data)
        } else if (constant != null) {
            append(constant.name)
            append(" = ")
            append(constant.value)
        }
        if (comment != null) {
            while (length < 32) append(' ')
            append("; ")
            append(comment)
        }
    }
}

data class AssemblyInstruction(
    val op: AssemblyOp,
    val address: AssemblyAddressing? = null,
) {
    val addressAsLabel get() = address as AssemblyAddressing.Label
    override fun toString(): String = "$op ${address ?: ""}"
}

sealed class AssemblyAddressing {
    data class ValueHex(val value: Byte) : AssemblyAddressing() {
        override fun toString(): String = "#$" + Integer.toString(value.toInt() and 0xFF, 16).padStart(2, '0')
    }

    data class ValueBinary(val value: Byte) : AssemblyAddressing() {
        override fun toString(): String = "#%" + Integer.toString(value.toInt() and 0xFF, 2).padStart(8, '0')
    }

    data class ValueDecimal(val value: Byte) : AssemblyAddressing() {
        override fun toString(): String = "#" + (value.toInt() and 0xFF)
    }

    data class ValueReference(val name: String) : AssemblyAddressing() {
        override fun toString(): String = "#$name"
    }

    object Accumulator : AssemblyAddressing() {
        override fun toString(): String = "A"
    }

    data class Label(val label: String) : AssemblyAddressing() {
        override fun toString(): String = label
    }

    data class DirectX(val label: String) : AssemblyAddressing() {
        override fun toString(): String = "$label,X"
    }

    data class DirectY(val label: String) : AssemblyAddressing() {
        override fun toString(): String = "$label,Y"
    }

    data class IndirectX(val label: String) : AssemblyAddressing() {
        override fun toString(): String = "($label,X)"
    }

    data class IndirectY(val label: String) : AssemblyAddressing() {
        override fun toString(): String = "($label),Y"
    }

    data class IndirectAbsolute(val label: String) : AssemblyAddressing() {
        override fun toString(): String = "($label)"
    }

    companion object {
        fun parse(text: String): AssemblyAddressing {
            val trimmed = text.trim()
            if (trimmed.equals("A", ignoreCase = true)) {
                return Accumulator
            }
            if (trimmed.startsWith("#$")) {
                return ValueHex(trimmed.substring(2).toInt(16).toByte())
            }
            if (trimmed.startsWith("#%")) {
                return ValueBinary(trimmed.substring(2).toInt(2).toByte())
            }
            if (trimmed.startsWith("#")) {
                val payload = trimmed.drop(1)
                if (payload.firstOrNull()?.isLetter() == true) {
                    return ValueReference(payload)
                }
                return ValueDecimal(payload.toInt().toByte())
            }
            val noWs = trimmed.filter { !it.isWhitespace() }
            if (noWs.endsWith(",X)", ignoreCase = true)) {
                return IndirectX(trimmed.substringAfter('(').substringBefore(','))
            }
            if (noWs.endsWith("),Y", ignoreCase = true)) {
                return IndirectY(trimmed.substringAfter('(').substringBefore(')'))
            }
            if (noWs.startsWith("(") && noWs.endsWith(")") && !noWs.contains(',')) {
                return IndirectAbsolute(trimmed.substringAfter('(').substringBefore(')'))
            }
            if (noWs.endsWith(",X", ignoreCase = true)) {
                return DirectX(trimmed.substringBefore(',').trim())
            }
            if (noWs.endsWith(",Y", ignoreCase = true)) {
                return DirectY(trimmed.substringBefore(',').trim())
            }
            return Label(trimmed)
        }
    }
}

fun String.parseToAssemblyCodeFile(): AssemblyCodeFile {
    // Regex to detect constant definitions: Name = Value
    val constantPattern = Regex("""^\s*([A-Za-z_][A-Za-z0-9_]*)\s*=\s*(.+?)(?:\s*;.*)?$""")

    return this.split('\n')
        .map { line ->
            // Check for constant definition first (before label parsing)
            // But skip directives that start with '.'
            val trimmedLine = line.substringBefore(';').trim()
            if (!trimmedLine.startsWith('.')) {
                val constantMatch = constantPattern.matchEntire(trimmedLine)
                if (constantMatch != null) {
                    val (name, valueStr) = constantMatch.destructured
                    val value = AssemblyAddressing.parse(valueStr.trim())
                    return@map AssemblyLine(
                        label = null,
                        instruction = null,
                        data = null,
                        constant = AssemblyConstant(name, value),
                        comment = line.substringAfter(';', "").trim().takeIf { it.isNotBlank() },
                        originalLine = line
                    )
                }
            }

            // Normal parsing (labels, instructions, data)
            val label = line.substringBefore(':', "").trim().takeIf { it.isNotBlank() }
            val instrOrDirText = line.substringAfter(":").substringBefore(";").trim()
            var instruction: AssemblyInstruction? = null
            var data: AssemblyData? = null
            if (instrOrDirText.isNotBlank()) {
                val firstToken = instrOrDirText.substringBefore(' ').trim()
                val rest = instrOrDirText.substringAfter(firstToken, missingDelimiterValue = "").trim()
                val firstLower = firstToken.lowercase()
                if (firstLower == ".db" || firstLower == ".byte" || firstLower == ".dw" || firstLower == ".word") {
                    data = AssemblyData.Db(parseDbItems(rest))
                } else {
                    instruction = runCatching {
                        AssemblyInstruction(
                            op = AssemblyOp.parse(firstToken),
                            address = rest.takeUnless { it.isBlank() }?.let { AssemblyAddressing.parse(it) }
                        )
                    }.getOrNull()
                }
            }
            AssemblyLine(
                label = label,
                instruction = instruction,
                data = data,
                constant = null,
                comment = line.substringAfter(';', "").trim().takeIf { it.isNotBlank() },
                originalLine = line
            )
        }
        .let { AssemblyCodeFile(it) }
}

data class SymbolTable(
    val labelToLineIndex: Map<String, Int>,
    val duplicates: Map<String, List<Int>>
)

fun AssemblyCodeFile.buildSymbolTable(): SymbolTable {
    val labelToLineIndex = mutableMapOf<String, Int>()
    val duplicates = mutableMapOf<String, MutableList<Int>>()
    lines.forEachIndexed { index, line ->
        val lbl = line.label?.trim()
        if (!lbl.isNullOrEmpty()) {
            val existing = labelToLineIndex.putIfAbsent(lbl, index)
            if (existing != null) {
                val list = duplicates.getOrPut(lbl) { mutableListOf(existing) }
                list.add(index)
            }
        }
    }
    return SymbolTable(labelToLineIndex, duplicates.mapValues { it.value.toList() })
}

// Data directives for assembly (.db and similar)
sealed class AssemblyData {
    data class Db(val items: List<DbItem>) : AssemblyData() {
        fun byteCount(): Int = items.sumOf {
            when (it) {
                is DbItem.ByteValue -> 1
                is DbItem.StringLiteral -> it.text.length
                is DbItem.Expr -> 1 // unknown expression: assume 1 byte
            }
        }

        override fun toString(): String {
            val rendered = items.joinToString(", ") { item ->
                when (item) {
                    is DbItem.ByteValue -> "${'$'}" + item.value.toString(16).padStart(2, '0')
                    is DbItem.StringLiteral -> "\"" + item.text + "\""
                    is DbItem.Expr -> item.expr
                }
            }
            return ".db " + rendered
        }
    }

    sealed class DbItem {
        data class ByteValue(val value: Int) : DbItem()
        data class StringLiteral(val text: String) : DbItem()
        data class Expr(val expr: String) : DbItem()
    }
}


private fun parseDbItems(payload: String): List<AssemblyData.DbItem> {
    val items = mutableListOf<AssemblyData.DbItem>()
    val tokens = splitCsvRespectingQuotes(payload)
    for (raw in tokens) {
        val tok = raw.trim()
        if (tok.isEmpty()) continue
        if (tok.length >= 2 && tok.first() == '"' && tok.last() == '"') {
            items += AssemblyData.DbItem.StringLiteral(tok.substring(1, tok.length - 1))
            continue
        }
        try {
            when {
                tok.startsWith("$") -> {
                    val v = tok.drop(1).trim().ifEmpty { "0" }.toInt(16) and 0xFF
                    items += AssemblyData.DbItem.ByteValue(v)
                }
                tok.startsWith("%") -> {
                    val v = tok.drop(1).trim().ifEmpty { "0" }.toInt(2) and 0xFF
                    items += AssemblyData.DbItem.ByteValue(v)
                }
                tok.all { it.isDigit() } -> {
                    val v = tok.toInt() and 0xFF
                    items += AssemblyData.DbItem.ByteValue(v)
                }
                else -> {
                    // Unknown expression/label: keep as expression and assume 1 byte for sizing
                    items += AssemblyData.DbItem.Expr(tok)
                }
            }
        } catch (_: Throwable) {
            items += AssemblyData.DbItem.Expr(tok)
        }
    }
    return items
}
