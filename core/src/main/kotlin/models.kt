package com.ivieleague.decompiler6502tokotlin.hand

import kotlin.reflect.KClass

// All right, let's show you how it's done.

enum class AssemblyAffectable {
    A, X, Y, Stack, DisableInterrupt, StackPointer,
    Negative, Overflow, Zero, Carry,
    Memory
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
    val modifies: (KClass<out AssemblyAddressing>?) -> Set<AssemblyAffectable> = { setOf() },
    val reads: (KClass<out AssemblyAddressing>?) -> Set<AssemblyAffectable> = { setOf() },
    val isBranch: Boolean = false,
    val flagPositive: Boolean = false,
    val allowedCategories: Set<KClass<out AssemblyAddressing>?> = emptySet()
) {
    LDA(
        description = "Load Accumulator",
        modifies = { setOf(AssemblyAffectable.A, AssemblyAffectable.Negative, AssemblyAffectable.Zero) },
        reads = { setOf(AssemblyAffectable.Memory) },
        allowedCategories = setOf(
            AddressCategory.IMMEDIATE, AddressCategory.MEM, AddressCategory.MEM_X, AddressCategory.MEM_Y,
            AddressCategory.INDIRECT_X, AddressCategory.INDIRECT_Y
        )
    ),
    LDX(
        description = "Load X Register",
        modifies = { setOf(AssemblyAffectable.X, AssemblyAffectable.Negative, AssemblyAffectable.Zero) },
        reads = { setOf(AssemblyAffectable.Memory) },
        allowedCategories = setOf(AddressCategory.IMMEDIATE, AddressCategory.MEM, AddressCategory.MEM_Y)
    ),
    LDY(
        description = "Load Y Register",
        modifies = { setOf(AssemblyAffectable.Y, AssemblyAffectable.Negative, AssemblyAffectable.Zero) },
        reads = { setOf(AssemblyAffectable.Memory) },
        allowedCategories = setOf(AddressCategory.IMMEDIATE, AddressCategory.MEM, AddressCategory.MEM_X)
    ),
    STA(
        description = "Store Accumulator",
        reads = { setOf(AssemblyAffectable.A) },
        modifies = { setOf(AssemblyAffectable.Memory) },
        allowedCategories = setOf(
            AddressCategory.MEM, AddressCategory.MEM_X, AddressCategory.MEM_Y,
            AddressCategory.INDIRECT_X, AddressCategory.INDIRECT_Y
        )
    ),
    STX(
        description = "Store X Register",
        reads = { setOf(AssemblyAffectable.X) },
        modifies = { setOf(AssemblyAffectable.Memory) },
        allowedCategories = setOf(AddressCategory.MEM, AddressCategory.MEM_Y)
    ),
    STY(
        description = "Store Y Register",
        reads = { setOf(AssemblyAffectable.Y) },
        modifies = { setOf(AssemblyAffectable.Memory) },
        allowedCategories = setOf(AddressCategory.MEM, AddressCategory.MEM_X)
    ),
    TAX(
        description = "Transfer accumulator to X",
        reads = { setOf(AssemblyAffectable.A) },
        modifies = { setOf(AssemblyAffectable.X, AssemblyAffectable.Negative, AssemblyAffectable.Zero) },
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    TAY(
        description = "Transfer accumulator to Y",
        reads = { setOf(AssemblyAffectable.A) },
        modifies = { setOf(AssemblyAffectable.Y, AssemblyAffectable.Negative, AssemblyAffectable.Zero) },
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    TXA(
        description = "Transfer X to accumulator",
        reads = { setOf(AssemblyAffectable.X) },
        modifies = { setOf(AssemblyAffectable.A, AssemblyAffectable.Negative, AssemblyAffectable.Zero) },
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    TYA(
        description = "Transfer Y to accumulator",
        reads = { setOf(AssemblyAffectable.Y) },
        modifies = { setOf(AssemblyAffectable.A, AssemblyAffectable.Negative, AssemblyAffectable.Zero) },
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    TSX(
        description = "Transfer stack pointer to X",
        reads = { setOf(AssemblyAffectable.StackPointer) },
        modifies = { setOf(AssemblyAffectable.X, AssemblyAffectable.Negative, AssemblyAffectable.Zero) },
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    TXS(
        description = "Transfer X to stack pointer",
        reads = { setOf(AssemblyAffectable.X) },
        modifies = { setOf(AssemblyAffectable.StackPointer) },
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    PHA(
        description = "Push accumulator on stack",
        reads = { setOf(AssemblyAffectable.A) },
        modifies = { setOf(AssemblyAffectable.Stack) },
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    PHP(
        description = "Push processor status on stack",
        modifies = { setOf(AssemblyAffectable.Stack) },
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    PLA(
        description = "Pull accumulator from stack",
        modifies = { setOf(AssemblyAffectable.A, AssemblyAffectable.Negative, AssemblyAffectable.Zero) },
        reads = { setOf(AssemblyAffectable.Stack) },
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    PLP(
        description = "Pull processor status from stack",
        modifies = { setOf(AssemblyAffectable.Negative, AssemblyAffectable.Overflow, AssemblyAffectable.Zero, AssemblyAffectable.Carry, AssemblyAffectable.DisableInterrupt) },
        reads = { setOf(AssemblyAffectable.Stack) },
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    AND(
        description = "Logical AND",
        modifies = { setOf(AssemblyAffectable.A, AssemblyAffectable.Negative, AssemblyAffectable.Zero) },
        reads = { setOf(AssemblyAffectable.A, AssemblyAffectable.Memory) },
        allowedCategories = setOf(
            AddressCategory.IMMEDIATE, AddressCategory.MEM, AddressCategory.MEM_X, AddressCategory.MEM_Y,
            AddressCategory.INDIRECT_X, AddressCategory.INDIRECT_Y
        )
    ),
    EOR(
        description = "Exclusive OR",
        modifies = { setOf(AssemblyAffectable.A, AssemblyAffectable.Negative, AssemblyAffectable.Zero) },
        reads = { setOf(AssemblyAffectable.A, AssemblyAffectable.Memory) },
        allowedCategories = setOf(
            AddressCategory.IMMEDIATE, AddressCategory.MEM, AddressCategory.MEM_X, AddressCategory.MEM_Y,
            AddressCategory.INDIRECT_X, AddressCategory.INDIRECT_Y
        )
    ),
    ORA(
        description = "Logical Inclusive OR",
        modifies = { setOf(AssemblyAffectable.A, AssemblyAffectable.Negative, AssemblyAffectable.Zero) },
        reads = { setOf(AssemblyAffectable.A, AssemblyAffectable.Memory) },
        allowedCategories = setOf(
            AddressCategory.IMMEDIATE, AddressCategory.MEM, AddressCategory.MEM_X, AddressCategory.MEM_Y,
            AddressCategory.INDIRECT_X, AddressCategory.INDIRECT_Y
        )
    ),
    BIT(
        description = "Bit Test",
        modifies = { setOf(AssemblyAffectable.Negative, AssemblyAffectable.Overflow, AssemblyAffectable.Zero) },
        reads = { setOf(AssemblyAffectable.A, AssemblyAffectable.Memory) },
        allowedCategories = setOf(AddressCategory.MEM)
    ),
    ADC(
        description = "Add with Carry",
        modifies = { setOf(AssemblyAffectable.A, AssemblyAffectable.Negative, AssemblyAffectable.Overflow, AssemblyAffectable.Zero, AssemblyAffectable.Carry) },
        reads = { setOf(AssemblyAffectable.A, AssemblyAffectable.Carry, AssemblyAffectable.Memory) },
        allowedCategories = setOf(
            AddressCategory.IMMEDIATE, AddressCategory.MEM, AddressCategory.MEM_X, AddressCategory.MEM_Y,
            AddressCategory.INDIRECT_X, AddressCategory.INDIRECT_Y
        )
    ),
    SBC(
        description = "Subtract with Carry",
        modifies = { setOf(AssemblyAffectable.A, AssemblyAffectable.Negative, AssemblyAffectable.Overflow, AssemblyAffectable.Zero, AssemblyAffectable.Carry) },
        reads = { setOf(AssemblyAffectable.A, AssemblyAffectable.Carry, AssemblyAffectable.Memory) },
        allowedCategories = setOf(
            AddressCategory.IMMEDIATE, AddressCategory.MEM, AddressCategory.MEM_X, AddressCategory.MEM_Y,
            AddressCategory.INDIRECT_X, AddressCategory.INDIRECT_Y
        )
    ),
    CMP(
        description = "Compare accumulator",
        modifies = { setOf(AssemblyAffectable.Negative, AssemblyAffectable.Zero, AssemblyAffectable.Carry) },
        reads = { setOf(AssemblyAffectable.A, AssemblyAffectable.Memory) },
        allowedCategories = setOf(
            AddressCategory.IMMEDIATE, AddressCategory.MEM, AddressCategory.MEM_X, AddressCategory.MEM_Y,
            AddressCategory.INDIRECT_X, AddressCategory.INDIRECT_Y
        )
    ),
    CPX(
        description = "Compare X register",
        modifies = { setOf(AssemblyAffectable.Negative, AssemblyAffectable.Zero, AssemblyAffectable.Carry) },
        reads = { setOf(AssemblyAffectable.X) },
        allowedCategories = setOf(AddressCategory.IMMEDIATE, AddressCategory.MEM)
    ),
    CPY(
        description = "Compare Y register",
        modifies = { setOf(AssemblyAffectable.Negative, AssemblyAffectable.Zero, AssemblyAffectable.Carry) },
        reads = { setOf(AssemblyAffectable.Y) },
        allowedCategories = setOf(AddressCategory.IMMEDIATE, AddressCategory.MEM)
    ),
    INC(
        description = "Increment a memory location",
        modifies = { setOf(AssemblyAffectable.Memory, AssemblyAffectable.Negative, AssemblyAffectable.Zero) },
        reads = { setOf(AssemblyAffectable.Memory) },
        allowedCategories = setOf(AddressCategory.MEM, AddressCategory.MEM_X)
    ),
    INX(
        description = "Increment the X register",
        modifies = { setOf(AssemblyAffectable.X, AssemblyAffectable.Negative, AssemblyAffectable.Zero) },
        reads = { setOf(AssemblyAffectable.X) },
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    INY(
        description = "Increment the Y register",
        modifies = { setOf(AssemblyAffectable.Y, AssemblyAffectable.Negative, AssemblyAffectable.Zero) },
        reads = { setOf(AssemblyAffectable.Y) },
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    DEC(
        description = "Decrement a memory location",
        modifies = { setOf(AssemblyAffectable.Memory, AssemblyAffectable.Negative, AssemblyAffectable.Zero) },
        reads = { setOf(AssemblyAffectable.Memory) },
        allowedCategories = setOf(AddressCategory.MEM, AddressCategory.MEM_X)
    ),
    DEX(
        description = "Decrement the X register",
        modifies = { setOf(AssemblyAffectable.X, AssemblyAffectable.Negative, AssemblyAffectable.Zero) },
        reads = { setOf(AssemblyAffectable.X) },
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    DEY(
        description = "Decrement the Y register",
        modifies = { setOf(AssemblyAffectable.Y, AssemblyAffectable.Negative, AssemblyAffectable.Zero) },
        reads = { setOf(AssemblyAffectable.Y) },
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    ASL(
        description = "Arithmetic Shift Left",
        reads = { if(it == null) setOf(AssemblyAffectable.A) else setOf(AssemblyAffectable.Memory) },
        modifies = { (if(it == null) setOf(AssemblyAffectable.A) else setOf(AssemblyAffectable.Memory)) + setOf(AssemblyAffectable.Negative, AssemblyAffectable.Zero, AssemblyAffectable.Carry) },
        allowedCategories = setOf(AddressCategory.ACCUMULATOR, AddressCategory.MEM, AddressCategory.MEM_X)
    ),
    LSR(
        description = "Logical Shift Right",
        reads = { if(it == null) setOf(AssemblyAffectable.A) else setOf(AssemblyAffectable.Memory) },
        modifies = { (if(it == null) setOf(AssemblyAffectable.A) else setOf(AssemblyAffectable.Memory)) + setOf(AssemblyAffectable.Negative, AssemblyAffectable.Zero, AssemblyAffectable.Carry) },
        allowedCategories = setOf(AddressCategory.ACCUMULATOR, AddressCategory.MEM, AddressCategory.MEM_X)
    ),
    ROL(
        description = "Rotate Left",
        reads = { (if(it == null) setOf(AssemblyAffectable.A) else setOf(AssemblyAffectable.Memory)) + setOf(AssemblyAffectable.Carry) },
        modifies = { (if(it == null) setOf(AssemblyAffectable.A) else setOf(AssemblyAffectable.Memory)) + setOf(AssemblyAffectable.Negative, AssemblyAffectable.Zero, AssemblyAffectable.Carry) },
        allowedCategories = setOf(AddressCategory.ACCUMULATOR, AddressCategory.MEM, AddressCategory.MEM_X)
    ),
    ROR(
        description = "Rotate Right",
        reads = { (if(it == null) setOf(AssemblyAffectable.A) else setOf(AssemblyAffectable.Memory)) + setOf(AssemblyAffectable.Carry) },
        modifies = { (if(it == null) setOf(AssemblyAffectable.A) else setOf(AssemblyAffectable.Memory)) + setOf(AssemblyAffectable.Negative, AssemblyAffectable.Zero, AssemblyAffectable.Carry) },
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
        reads = { setOf(AssemblyAffectable.Carry) },
        isBranch = true,
        flagPositive = false
    ),
    BCS(
        description = "Branch if carry flag set",
        reads = { setOf(AssemblyAffectable.Carry) },
        isBranch = true,
        flagPositive = true
    ),
    BEQ(
        description = "Branch if zero flag set",
        reads = { setOf(AssemblyAffectable.Zero) },
        isBranch = true,
        flagPositive = true
    ),
    BMI(
        description = "Branch if negative flag set",
        reads = { setOf(AssemblyAffectable.Negative) },
        isBranch = true,
        flagPositive = true
    ),
    BNE(
        description = "Branch if zero flag clear",
        reads = { setOf(AssemblyAffectable.Zero) },
        isBranch = true,
        flagPositive = false
    ),
    BPL(
        description = "Branch if negative flag clear",
        reads = { setOf(AssemblyAffectable.Negative) },
        isBranch = true,
        flagPositive = false
    ),
    BVC(
        description = "Branch if overflow flag clear",
        reads = { setOf(AssemblyAffectable.Overflow) },
        isBranch = true,
        flagPositive = false
    ),
    BVS(
        description = "Branch if overflow flag set",
        reads = { setOf(AssemblyAffectable.Overflow) },
        isBranch = true,
        flagPositive = true
    ),
    CLC(
        description = "Clear carry flag",
        modifies = { setOf(AssemblyAffectable.Carry) },
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    CLD(
        description = "Clear decimal mode flag",
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    CLI(
        description = "Clear interrupt disable flag",
        modifies = { setOf(AssemblyAffectable.DisableInterrupt) },
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    CLV(
        description = "Clear overflow flag",
        modifies = { setOf(AssemblyAffectable.Overflow) },
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    SEC(
        description = "Set carry flag",
        modifies = { setOf(AssemblyAffectable.Carry) },
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    SED(
        description = "Set decimal mode flag",
        allowedCategories = setOf(AddressCategory.IMPLIED)
    ),
    SEI(
        description = "Set interrupt disable flag",
        modifies = { setOf(AssemblyAffectable.DisableInterrupt) },
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
    data class ByteValue(val value: UByte, val radix: Radix) : Value {
        override fun toString(): String = "#${radix.prefix}${value.toString(radix.base).let {
            if (radix.perByte != null) it.padStart(radix.perByte, '0') else it 
        }}"
    }
    data class ShortValue(val value: UShort, val radix: Radix) : Value {
        override fun toString(): String = "#${radix.prefix}${value.toString(radix.base).let {
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
    // by Claude - Added to handle #<ConstantName and #>ConstantName patterns
    data class ConstantReferenceLower(val name: String) : Value {
        override fun toString(): String = "#<$name"
    }
    data class ConstantReferenceUpper(val name: String) : Value {
        override fun toString(): String = "#>$name"
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
                    // by Claude - Fixed to handle both ShortValue and ConstantReference for hi/lo byte selection
                    nextLetter == '<' -> {
                        val inner = parse("#" + trimmed.substring(2)) ?: return null
                        return when (inner) {
                            is ShortValue -> ValueLowerSelection(inner)
                            is ConstantReference -> ConstantReferenceLower(inner.name)
                            else -> null
                        }
                    }
                    nextLetter == '>' -> {
                        val inner = parse("#" + trimmed.substring(2)) ?: return null
                        return when (inner) {
                            is ShortValue -> ValueUpperSelection(inner)
                            is ConstantReference -> ConstantReferenceUpper(inner.name)
                            else -> null
                        }
                    }
                    else -> {
                        val radix = Radix.entries.find { it.prefix == nextLetter.toString() } ?: throw IllegalStateException("What am I supposed to do with '$trimmed'?")
                        val digits = trimmed.substring(2)
                        val directValue = digits.toUInt(radix.base)
                        return if (directValue <= 0xFFu) ByteValue(directValue.toUByte(), radix)
                        else ShortValue(directValue.toUShort(), radix)
                    }
                }
            }
            // by Claude - Helper to parse hex or decimal offset values
            fun parseOffsetValue(value: String): Int {
                val trimmed = value.trim()
                return when {
                    trimmed.startsWith("$") -> trimmed.substring(1).toInt(16)
                    trimmed.startsWith("0x", ignoreCase = true) -> trimmed.substring(2).toInt(16)
                    else -> trimmed.toInt()
                }
            }

            // by Claude - Fixed to handle complex offsets like "VRAM_Buffer1-1+$100"
            fun String.parseLabelWithOffset(): Pair<String, Int> {
                val firstOpIndex = indexOfAny(charArrayOf('+', '-'))
                if (firstOpIndex < 0) return this to 0

                val label = substring(0, firstOpIndex)
                val offsetPart = substring(firstOpIndex)

                // Parse offset expression with multiple components (e.g., "-1+$100")
                var offset = 0
                var currentSign = 1
                var currentNumber = StringBuilder()

                for (char in offsetPart) {
                    when {
                        char == '+' -> {
                            if (currentNumber.isNotEmpty()) {
                                offset += currentSign * parseOffsetValue(currentNumber.toString())
                                currentNumber.clear()
                            }
                            currentSign = 1
                        }
                        char == '-' -> {
                            if (currentNumber.isNotEmpty()) {
                                offset += currentSign * parseOffsetValue(currentNumber.toString())
                                currentNumber.clear()
                            }
                            currentSign = -1
                        }
                        else -> currentNumber.append(char)
                    }
                }

                // Don't forget the last number
                if (currentNumber.isNotEmpty()) {
                    offset += currentSign * parseOffsetValue(currentNumber.toString())
                }

                return label to offset
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
            if (trimmed.startsWith("(") && trimmed.endsWith(")")) {
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
