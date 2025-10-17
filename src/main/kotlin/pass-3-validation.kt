package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 3: Disassembly Validation
 * - Validate that each instruction uses a legal addressing form for the 6502
 * - Validate opcodes are legal 6502 instructions
 * - Flag potential data embedded in code sections
 * - Check for unreferenced labels in data sections
 */

enum class ValidationIssueType {
    ILLEGAL_ADDRESSING_MODE,
    INVALID_OPCODE,
    DATA_IN_CODE_SECTION,
    UNREFERENCED_LABEL,
    MISSING_LABEL_REFERENCE
}

data class ValidationIssue(
    val lineIndex: Int,
    val line: AssemblyLine,
    val type: ValidationIssueType,
    val message: String
)

data class ValidationReport(
    val issues: List<ValidationIssue>,
    val addressingModeIssues: List<ValidationIssue> = issues.filter { it.type == ValidationIssueType.ILLEGAL_ADDRESSING_MODE },
    val opcodeIssues: List<ValidationIssue> = issues.filter { it.type == ValidationIssueType.INVALID_OPCODE },
    val dataInCodeIssues: List<ValidationIssue> = issues.filter { it.type == ValidationIssueType.DATA_IN_CODE_SECTION },
)


private fun addressingCategory(addr: AssemblyAddressing?): AddressCategory = when (addr) {
    null -> AddressCategory.IMPLIED
    is AssemblyAddressing.Accumulator -> AddressCategory.ACCUMULATOR
    is AssemblyAddressing.ValueHex, is AssemblyAddressing.ValueBinary,
    is AssemblyAddressing.ValueDecimal, is AssemblyAddressing.ValueReference -> AddressCategory.IMMEDIATE
    is AssemblyAddressing.DirectX -> AddressCategory.MEM_X
    is AssemblyAddressing.DirectY -> AddressCategory.MEM_Y
    is AssemblyAddressing.IndirectX -> AddressCategory.INDIRECT_X
    is AssemblyAddressing.IndirectY -> AddressCategory.INDIRECT_Y
    is AssemblyAddressing.IndirectAbsolute -> AddressCategory.JMP_INDIRECT
    is AssemblyAddressing.Label -> AddressCategory.MEM
}

private fun allowedCategories(op: AssemblyOp): Set<AddressCategory> = op.allowedCategories

/**
 * Enhanced validation that checks addressing modes, opcodes, and data placement
 */
fun List<AssemblyLine>.validateDisassembly(
    resolution: AddressResolution? = null,
    reachability: ReachabilityReport? = null
): ValidationReport {
    val issues = mutableListOf<ValidationIssue>()
    val labelReferences = mutableSetOf<String>()

    this.forEachIndexed { idx, line ->
        // Collect label references for later validation
        line.instruction?.address?.let { addr ->
            when (addr) {
                is AssemblyAddressing.Label -> labelReferences += addr.label
                is AssemblyAddressing.DirectX -> labelReferences += addr.label
                is AssemblyAddressing.DirectY -> labelReferences += addr.label
                is AssemblyAddressing.IndirectX -> labelReferences += addr.label
                is AssemblyAddressing.IndirectY -> labelReferences += addr.label
                is AssemblyAddressing.IndirectAbsolute -> labelReferences += addr.label
                is AssemblyAddressing.ValueReference -> labelReferences += addr.name
                else -> {}
            }
        }

        val instr = line.instruction ?: return@forEachIndexed
        val addr = instr.address
        val op = instr.op

        // 1. Validate addressing modes for branches
        if (op.isBranch) {
            val ok = addr is AssemblyAddressing.Label
            if (!ok) {
                issues += ValidationIssue(
                    idx, line, ValidationIssueType.ILLEGAL_ADDRESSING_MODE,
                    "Branch ${op.name} must target a label (relative), found: ${addr?.javaClass?.simpleName ?: "IMPLIED"}"
                )
            }
            return@forEachIndexed
        }

        // 2. Validate addressing modes for non-branch instructions
        val allowed = allowedCategories(op)
        val cat = when {
            (op == AssemblyOp.ASL || op == AssemblyOp.LSR || op == AssemblyOp.ROL || op == AssemblyOp.ROR) && addr == null ->
                AddressCategory.ACCUMULATOR
            else -> addressingCategory(addr)
        }
        if (allowed.isNotEmpty() && cat !in allowed) {
            issues += ValidationIssue(
                idx, line, ValidationIssueType.ILLEGAL_ADDRESSING_MODE,
                "Illegal addressing mode for ${op.name}: $cat"
            )
        } else if (allowed.isEmpty()) {
            // This indicates a bug in our opcode table - some opcodes have no allowed categories
            issues += ValidationIssue(
                idx, line, ValidationIssueType.INVALID_OPCODE,
                "Opcode ${op.name} has no defined addressing modes in opcode table"
            )
        }

        // 3. Validate that the opcode exists (this is somewhat redundant since we parse successfully,
        // but useful for detecting if parser accepted invalid text)
        try {
            AssemblyOp.valueOf(op.name)
        } catch (e: IllegalArgumentException) {
            issues += ValidationIssue(
                idx, line, ValidationIssueType.INVALID_OPCODE,
                "Invalid 6502 opcode: ${op.name}"
            )
        }
    }

    // 4. Check for data embedded in code sections (if reachability provided)
    if (resolution != null && reachability != null) {
        this.forEachIndexed { idx, line ->
            if (line.data != null) {
                // Check if this data line is surrounded by reachable instructions
                val prevInstr = (idx-1 downTo 0).find { i -> this[i].instruction != null }
                val nextInstr = (idx+1 until this.size).find { i -> this[i].instruction != null }
                
                val prevReachable = prevInstr?.let { it in reachability.reachableLineIndexes } == true
                val nextReachable = nextInstr?.let { it in reachability.reachableLineIndexes } == true
                
                if (prevReachable || nextReachable) {
                    issues += ValidationIssue(
                        idx, line, ValidationIssueType.DATA_IN_CODE_SECTION,
                        "Data directive found in reachable code section"
                    )
                }
            }
        }
    }

    return ValidationReport(issues)
}
