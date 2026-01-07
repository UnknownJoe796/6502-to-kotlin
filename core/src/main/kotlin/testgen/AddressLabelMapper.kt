package com.ivieleague.decompiler6502tokotlin.testgen

import com.ivieleague.decompiler6502tokotlin.hand.*
import java.io.File

/**
 * Maps between binary addresses and assembly label names.
 *
 * This is needed because:
 * - The binary interpreter captures function calls by runtime address (e.g., 0x8182)
 * - The decompiler outputs functions by their label names (e.g., PauseRoutine)
 *
 * This class parses the assembly file and computes the address for each label,
 * allowing us to map captured addresses back to function names.
 */
class AddressLabelMapper private constructor(
    private val labelToAddress: Map<String, Int>,
    private val addressToLabel: Map<Int, String>
) {
    /**
     * Get the label name for a given address.
     * Returns null if no label exists at that address.
     */
    fun getLabel(address: Int): String? = addressToLabel[address]

    /**
     * Get the address for a given label.
     * Returns null if the label is not found.
     */
    fun getAddress(label: String): Int? = labelToAddress[label]

    /**
     * Get a function name suitable for Kotlin code.
     * Converts PascalCase to camelCase and handles special characters.
     */
    fun getFunctionName(address: Int): String? {
        val label = getLabel(address) ?: return null
        return labelToKotlinFunctionName(label)
    }

    /**
     * Get all labels that are function entry points (called by JSR).
     */
    fun getFunctionLabels(): Set<String> = labelToAddress.keys

    /**
     * Get all addresses that have labels.
     */
    fun getAllAddresses(): Set<Int> = addressToLabel.keys

    companion object {
        /**
         * Parse an assembly file and build the address mapping.
         * Uses the existing parser infrastructure for accurate byte counting.
         *
         * @param asmFile Path to the assembly file (e.g., smbdism.asm)
         * @param baseAddress The starting address for code (default $8000 for NES PRG-ROM)
         */
        fun fromAssemblyFile(asmFile: File, baseAddress: Int = 0x8000): AddressLabelMapper {
            val text = asmFile.readText()
            return fromAssemblyText(text, baseAddress)
        }

        /**
         * Parse assembly text and build the address mapping.
         * Uses the existing AssemblyCodeFile parser for correct byte sizing.
         */
        fun fromAssemblyText(text: String, baseAddress: Int = 0x8000): AddressLabelMapper {
            val labelToAddress = mutableMapOf<String, Int>()
            val addressToLabel = mutableMapOf<Int, String>()
            val constants = mutableMapOf<String, Int>()  // For zero-page detection

            // Use the existing parser
            val codeFile = text.parseToAssemblyCodeFile()

            // First pass: collect all constants
            for (line in codeFile.lines) {
                val constant = line.constant
                if (constant != null) {
                    val value = parseConstantValue(constant.value)
                    if (value != null) {
                        constants[constant.name] = value
                    }
                }
            }

            var currentAddress = 0
            var inCode = false

            for (line in codeFile.lines) {
                // Handle .org directive in original line
                val original = line.originalLine ?: ""
                val trimmed = original.trim()

                if (trimmed.startsWith(".org", ignoreCase = true)) {
                    val addrStr = trimmed.substringAfter(".org", "")
                        .trim()
                        .removePrefix("$")
                        .trim()
                    currentAddress = addrStr.toIntOrNull(16) ?: continue
                    inCode = true
                    continue
                }

                // Skip if not in code section
                if (!inCode) continue

                // Record label if present (use parsed label which is more reliable)
                val label = line.label
                if (label != null && label.first().isLetter()) {
                    labelToAddress[label] = currentAddress
                    addressToLabel[currentAddress] = label
                }

                // Calculate size from instruction or data
                val size = when {
                    line.instruction != null -> getInstructionSize(line.instruction!!, constants)
                    line.data != null -> {
                        val data = line.data!!
                        if (data is AssemblyData.Db) data.byteCount() else 0
                    }
                    else -> 0
                }
                currentAddress += size
            }

            return AddressLabelMapper(labelToAddress, addressToLabel)
        }

        /**
         * Parse a constant value to an integer.
         */
        private fun parseConstantValue(value: AssemblyAddressing.Value): Int? {
            return when (value) {
                is AssemblyAddressing.ByteValue -> value.value.toInt()
                is AssemblyAddressing.ShortValue -> value.value.toInt()
                is AssemblyAddressing.ValueLowerSelection -> parseConstantValue(value.value)?.and(0xFF)
                is AssemblyAddressing.ValueUpperSelection -> parseConstantValue(value.value)?.shr(8)?.and(0xFF)
                is AssemblyAddressing.ConstantReference -> null  // Can't resolve references
            }
        }

        /**
         * Get the byte size of an instruction based on its opcode and addressing mode.
         */
        private fun getInstructionSize(instr: AssemblyInstruction, constants: Map<String, Int> = emptyMap()): Int {
            // Implied/Accumulator = 1 byte
            if (instr.address == null) return 1

            return when (instr.address) {
                // Immediate = 2 bytes
                is AssemblyAddressing.ByteValue,
                is AssemblyAddressing.ShortValue,
                is AssemblyAddressing.ValueLowerSelection,
                is AssemblyAddressing.ValueUpperSelection,
                is AssemblyAddressing.ConstantReference -> 2

                // Direct addressing - depends on whether zero page or not
                is AssemblyAddressing.Direct -> {
                    val addr = instr.address
                    // Branch instructions are always 2 bytes
                    if (instr.op.isBranch) return 2
                    // JMP and JSR are always 3 bytes
                    if (instr.op == AssemblyOp.JMP || instr.op == AssemblyOp.JSR) return 3
                    // Try to determine if zero page
                    if (isZeroPageLabel(addr.label, constants)) 2 else 3
                }

                // Indexed modes - same logic
                is AssemblyAddressing.DirectX -> {
                    if (isZeroPageLabel(instr.address.label, constants)) 2 else 3
                }
                is AssemblyAddressing.DirectY -> {
                    if (isZeroPageLabel(instr.address.label, constants)) 2 else 3
                }

                // Indirect modes - always use zero-page base
                is AssemblyAddressing.IndirectX,
                is AssemblyAddressing.IndirectY -> 2

                // Indirect absolute (JMP only) = 3 bytes
                is AssemblyAddressing.IndirectAbsolute -> 3
            }
        }

        /**
         * Determine if a label refers to a zero-page address.
         */
        private fun isZeroPageLabel(label: String, constants: Map<String, Int> = emptyMap()): Boolean {
            // If it's a hex address
            if (label.startsWith("$")) {
                val hex = label.removePrefix("$").substringBefore('+').substringBefore('-')
                val addr = hex.toIntOrNull(16) ?: return false
                return addr < 256
            }
            // Check if it's a known constant
            val baseLabel = label.substringBefore('+').substringBefore('-')
            val constValue = constants[baseLabel]
            if (constValue != null) {
                return constValue < 256
            }
            // Unknown label - assume not zero page (safer for ROM addresses)
            return false
        }

        /**
         * Convert assembly label to Kotlin function name.
         * PascalCase -> camelCase, remove special chars
         */
        fun labelToKotlinFunctionName(label: String): String {
            if (label.isEmpty()) return "func"
            // Convert first letter to lowercase (PascalCase -> camelCase)
            val result = label.first().lowercaseChar() + label.drop(1)
            // Replace invalid chars with underscore
            return result.map { if (it.isLetterOrDigit() || it == '_') it else '_' }.joinToString("")
        }
    }
}
