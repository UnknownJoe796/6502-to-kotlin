package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 2: Address Resolution
 * - Assign absolute addresses to all instructions and data bytes
 * - Resolve label references to addresses (label -> absolute address)
 * - Identify data sections vs code sections (heuristic: .db lines are data)
 */

data class ResolvedLine(
    val line: AssemblyLine,
    val address: Int?,
    val sizeBytes: Int,
    val isData: Boolean
)

data class AddressResolution(
    val resolved: List<ResolvedLine>,
    val labelToAddress: Map<String, Int>
)

fun List<AssemblyLine>.resolveAddresses(baseAddress: Int = 0): AddressResolution {
    val resolved = mutableListOf<ResolvedLine>()
    val labelToAddress = mutableMapOf<String, Int>()
    var pc = baseAddress

    fun parseDbByteCount(originalLine: String): Int {
        // Remove comment
        val noComment = originalLine.substringBefore(';')
        val afterDb = noComment.substringAfter(".db", missingDelimiterValue = "")
        if (afterDb.isEmpty()) return 0
        
        // Use shared CSV parsing utility
        val tokens = splitCsvRespectingQuotes(afterDb)
        
        // Count bytes: string literals count as their length; others count as 1 value each
        return tokens.mapNotNull { tokRaw ->
            val tok = tokRaw.trim()
            if (tok.isEmpty()) null
            else if (tok.startsWith('"') && tok.endsWith('"') && tok.length >= 2) {
                // naive: 1 byte per character
                tok.substring(1, tok.length - 1).length
            } else {
                1
            }
        }.sum()
    }

    fun estimateSizeBytes(instr: AssemblyInstruction): Int {
        val op = instr.op
        val addr = instr.address
        // Implied/accumulator
        val impliedOps = setOf(
            AssemblyOp.BRK, AssemblyOp.NOP, AssemblyOp.RTS, AssemblyOp.RTI,
            AssemblyOp.TAX, AssemblyOp.TAY, AssemblyOp.TXA, AssemblyOp.TYA, AssemblyOp.TSX, AssemblyOp.TXS,
            AssemblyOp.PHA, AssemblyOp.PHP, AssemblyOp.PLA, AssemblyOp.PLP,
            AssemblyOp.CLC, AssemblyOp.CLD, AssemblyOp.CLI, AssemblyOp.CLV, AssemblyOp.SEC, AssemblyOp.SED, AssemblyOp.SEI,
            AssemblyOp.INX, AssemblyOp.INY, AssemblyOp.DEX, AssemblyOp.DEY
        )
        if (op in impliedOps && addr == null) return 1

        // Branches always 2 bytes (opcode + 8-bit relative offset)
        if (op.isBranch) return 2

        // JSR/JMP are always absolute (or indirect) => 3 bytes
        if (op == AssemblyOp.JSR || op == AssemblyOp.JMP) return 3

        // Immediate values => 2 bytes
        if (addr is AssemblyAddressing.ValueHex || addr is AssemblyAddressing.ValueBinary || addr is AssemblyAddressing.ValueDecimal || addr is AssemblyAddressing.ValueReference) {
            return 2
        }

        // Indirect via zero-page pointer addressing is 2 bytes
        if (addr is AssemblyAddressing.IndirectX || addr is AssemblyAddressing.IndirectY) return 2

        // If addressing omitted or explicit accumulator and op supports accumulator (ASL/LSR/ROL/ROR) => 1 byte
        if ((addr == null || addr is AssemblyAddressing.Accumulator) &&
            (op == AssemblyOp.ASL || op == AssemblyOp.LSR || op == AssemblyOp.ROL || op == AssemblyOp.ROR)
        ) return 1

        // For direct memory operands: use improved addressing detection
        fun getAddressingSize(label: String): Int {
            val hexAddr = parseHexAddr(label)
            return if (hexAddr != null && isZeroPageAddress(hexAddr)) 2 else 3
        }
        
        when (addr) {
            is AssemblyAddressing.DirectX -> return getAddressingSize(addr.label)
            is AssemblyAddressing.DirectY -> return getAddressingSize(addr.label)
            is AssemblyAddressing.Label -> return getAddressingSize(addr.label)
            else -> {}
        }
        // Fallback conservative assumption
        return 3
    }

    this.forEach { line ->
        // If there is a label on this line, map it to the current pc before consuming bytes
        line.label?.let { lbl -> labelToAddress.putIfAbsent(lbl, pc) }

        if (line.data is AssemblyData.Db) {
            val byteCount = (line.data as AssemblyData.Db).byteCount()
            resolved += ResolvedLine(line = line, address = pc, sizeBytes = byteCount, isData = true)
            pc += byteCount
        } else if (line.instruction != null) {
            val size = estimateSizeBytes(line.instruction)
            resolved += ResolvedLine(line = line, address = pc, sizeBytes = size, isData = false)
            pc += size
        } else {
            // Non-instruction, non-.db line: carry through with no address impact
            resolved += ResolvedLine(line = line, address = null, sizeBytes = 0, isData = false)
        }
    }

    return AddressResolution(resolved = resolved, labelToAddress = labelToAddress)
}
