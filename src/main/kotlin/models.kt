package com.ivieleague.decompiler6502tokotlin.hand

import kotlin.reflect.KClass

// All right, let's show you how it's done.

enum class AssemblyFlag {
    N, V, Z, C
}

object AddressCategory {
    val IMPLIED = null
    val ACCUMULATOR = IMPLIED
    val IMMEDIATE = AssemblyAddressing.Value::class
    val MEM = AssemblyAddressing.Direct::class
    val MEM_X = AssemblyAddressing.DirectX::class
    val MEM_Y = AssemblyAddressing.DirectY::class
    val INDIRECT_X = AssemblyAddressing.IndirectX::class
    val INDIRECT_Y = AssemblyAddressing.IndirectY::class
    val JMP_INDIRECT = AssemblyAddressing.IndirectAbsolute::class
}

enum class AssemblyOp(
    val description: String,
    val affectedFlags: Set<AssemblyFlag> = setOf(),
    val consumedFlag: AssemblyFlag? = null,
    val isBranch: Boolean = false,
    val flagPositive: Boolean = false,
    val allowedCategories: Set<KClass<out AssemblyAddressing>?> = emptySet()
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

data class AssemblyConstant(
    val name: String,
    val value: AssemblyAddressing.Value  // Can be hex, decimal, or expression
)

data class AssemblyLine(
    val label: String? = null,
    val instruction: AssemblyInstruction? = null,
    val data: AssemblyData? = null,
    val constant: AssemblyConstant? = null,
    val comment: String? = null,
    val originalLine: String? = null,
    val originalLineIndex: Int = -1,
) {
    var block: AssemblyBlock? = null
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
    val addressAsLabel get() = address as AssemblyAddressing.Direct
    override fun toString(): String = "$op ${address ?: ""}"
}

sealed interface AssemblyAddressing {
    sealed interface Value : AssemblyAddressing
    enum class Radix(val base: Int, val prefix: String, val perByte: Int?) {
        Binary(2, "%", 8),
        Decimal(10, "", null),
        Hex(16, "$", 2)
    }
    data class ByteValue(val byte: UByte, val radix: Radix) : Value {
        override fun toString(): String = "#${radix.prefix}${byte.toString(radix.base).let {
            if (radix.perByte != null) it.padStart(radix.perByte, '0') else it 
        }}"
    }
    data class ShortValue(val byte: UShort, val radix: Radix) : Value {
        override fun toString(): String = "#${radix.prefix}${byte.toString(radix.base).let {
            if (radix.perByte != null) it.padStart(radix.perByte, '0') else it 
        }}"
    }
    data class ValueLowerSelection(val value: ShortValue) : Value {
        override fun toString(): String = "#<${value.toString().removePrefix("#")}"
    }
    data class ValueUpperSelection(val value: ShortValue) : Value {
        override fun toString(): String = "#>${value.toString().removePrefix("#")}"
    }
    data class ConstantReference(val name: String) : Value {
        override fun toString(): String = "#$name"
    }

    data class Direct(val label: String, val offset: Int = 0) : AssemblyAddressing {
        override fun toString(): String = "$label${offset.renderOffset()}"
    }

    data class DirectX(val label: String, val offset: Int = 0) : AssemblyAddressing {
        override fun toString(): String = "$label${offset.renderOffset()},X"
    }

    data class DirectY(val label: String, val offset: Int = 0) : AssemblyAddressing {
        override fun toString(): String = "$label${offset.renderOffset()},Y"
    }

    data class IndirectX(val label: String, val offset: Int = 0) : AssemblyAddressing {
        override fun toString(): String = "($label${offset.renderOffset()},X)"
    }

    data class IndirectY(val label: String, val offset: Int = 0) : AssemblyAddressing {
        override fun toString(): String = "($label${offset.renderOffset()}),Y"
    }

    data class IndirectAbsolute(val label: String, val offset: Int = 0) : AssemblyAddressing {
        override fun toString(): String = "($label${offset.renderOffset()})"
    }

    companion object {
        private fun Int.renderOffset() = when {
            this > 0 -> "+$this"
            this < 0 -> "$this"
            else -> ""
        }
        fun parse(text: String): AssemblyAddressing? {
            val trimmed = text.trim()
            if (trimmed.equals("A", ignoreCase = true)) {
                return null
            }
            if (trimmed.startsWith("#")) {
                val nextLetter = trimmed.get(1)
                when {
                    nextLetter.isDigit() -> {
                        val directValue = trimmed.substring(1).toUInt(10)
                        if(directValue > 0xFFu) return ShortValue(directValue.toUShort(), Radix.Decimal)
                        return ByteValue(directValue.toUByte(), Radix.Decimal)
                    }
                    nextLetter.isLetter() -> {
                        return ConstantReference(trimmed.substring(1))
                    }
                    nextLetter == '<' -> parse("#" + trimmed.substring(2))?.let { return ValueLowerSelection(it as? ShortValue ?: return null) }
                    nextLetter == '>' -> parse("#" + trimmed.substring(2))?.let { return ValueUpperSelection(it as? ShortValue ?: return null) }
                    else -> {
                        val radix = Radix.entries.find { it.prefix == nextLetter.toString() } ?: throw IllegalStateException("What am I supposed to do with '$trimmed'?")
                        val digits = trimmed.substring(2)
                        val directValue = digits.toUInt(radix.base)
                        return if (digits <= radix.prefix) ByteValue(directValue.toUByte(), radix)
                        else ShortValue(directValue.toUShort(), radix)
                    }
                }
            }
            fun String.parseLabelWithOffset(): Pair<String, Int> {
                val opIndex = indexOfAny(charArrayOf('+', '-'))
                if (opIndex < 0) return this to 0
                return substring(0, opIndex) to substring(opIndex).substringAfter('+').toInt()
            }
            if (trimmed.endsWith(",X)", ignoreCase = true)) {
                val parsed = trimmed.substringAfter('(').substringBefore(',').parseLabelWithOffset()
                return IndirectX(parsed.first, parsed.second)
            }
            if (trimmed.endsWith("),Y", ignoreCase = true)) {
                val parsed = trimmed.substringAfter('(').substringBefore(')').parseLabelWithOffset()
                return IndirectY(parsed.first, parsed.second)
            }
            if (trimmed.startsWith("(") && trimmed.endsWith(")") && !trimmed.contains(',')) {
                val parsed = trimmed.substringAfter('(').substringBefore(')').parseLabelWithOffset()
                return IndirectAbsolute(parsed.first, parsed.second)
            }
            if (trimmed.endsWith(",X", ignoreCase = true)) {
                val parsed = trimmed.substringBefore(',').trim().parseLabelWithOffset()
                return DirectX(parsed.first, parsed.second)
            }
            if (trimmed.endsWith(",Y", ignoreCase = true)) {
                val parsed = trimmed.substringBefore(',').trim().parseLabelWithOffset()
                return DirectY(parsed.first, parsed.second)
            }
            if (trimmed.startsWith("(") && trimmed.equals(")")) {
                val parsed = trimmed.substringAfter('(').substringBeforeLast(')').trim().parseLabelWithOffset()
                return IndirectAbsolute(parsed.first, parsed.second)
            }
            val parsed = trimmed.parseLabelWithOffset()
            return Direct(parsed.first, parsed.second)
        }
    }
}

data class AssemblyCodeFile(
    val lines: List<AssemblyLine>
) {}

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
