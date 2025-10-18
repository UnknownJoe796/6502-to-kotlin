package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 19: Memory Access Pattern Analysis
 * - Classify memory locations by access pattern
 * - Detect arrays (indexed addressing)
 * - Detect structures (indirect indexed)
 * - Detect pointers (indirect addressing)
 * - Distinguish scalars from composites
 */

/**
 * Memory access pattern classification
 */
sealed class MemoryAccessPattern {
    /** Single scalar variable at fixed address */
    data class Scalar(
        val address: Int,
        val name: String?
    ) : MemoryAccessPattern()

    /** Array accessed with index register */
    data class Array(
        val baseAddress: Int,
        val indexRegister: Variable,  // X or Y
        val observedIndices: Set<Int>,  // Observed index values
        val estimatedLength: Int?,
        val name: String?
    ) : MemoryAccessPattern()

    /** Structure field access via indirect indexed */
    data class StructField(
        val basePointer: Int,  // Zero page pointer
        val offset: Int,
        val fieldName: String?
    ) : MemoryAccessPattern()

    /** Pointer indirection */
    data class Pointer(
        val pointerAddress: Int,
        val pointsTo: MemoryRegion?,
        val name: String?
    ) : MemoryAccessPattern()

    /** Indexed indirect or indirect indexed array */
    data class IndirectArray(
        val basePointer: Int,
        val indexRegister: Variable,
        val name: String?
    ) : MemoryAccessPattern()
}

/**
 * Memory region classification
 */
data class MemoryRegion(
    val startAddress: Int,
    val endAddress: Int,
    val regionType: RegionType,
    val name: String?
)

enum class RegionType {
    CODE,
    DATA,
    ZERO_PAGE,      // $0000-$00FF
    STACK,          // $0100-$01FF
    IO_REGISTERS,   // NES: $2000-$2007 (PPU), $4000-$4017 (APU/IO)
    ROM,            // Read-only memory
    RAM             // Read-write memory
}

/**
 * Information about a specific memory location
 */
data class MemoryLocationInfo(
    val address: Int,
    val accessPattern: MemoryAccessPattern,
    val reads: List<AssemblyLineReference>,
    val writes: List<AssemblyLineReference>,
    val isConstant: Boolean,      // Never written (or only written in init)
    val isIndexed: Boolean,       // Accessed with ,X or ,Y
    val indirectionLevel: Int     // 0=direct, 1=indirect, 2=double indirect
) {
    val isReadOnly: Boolean get() = writes.isEmpty()
    val isWriteOnly: Boolean get() = reads.isEmpty()
    val accessCount: Int get() = reads.size + writes.size
}

/**
 * Memory analysis for a single function
 */
data class FunctionMemoryAnalysis(
    val function: FunctionCfg,
    val memoryAccesses: Map<Int, MemoryLocationInfo>,  // address -> info
    val identifiedArrays: List<MemoryAccessPattern.Array>,
    val identifiedStructures: List<MemoryAccessPattern.StructField>,
    val identifiedPointers: List<MemoryAccessPattern.Pointer>
)

/**
 * Complete memory access analysis
 */
data class MemoryAccessAnalysis(
    val functions: List<FunctionMemoryAnalysis>,
    val globalMemoryMap: Map<Int, MemoryLocationInfo>
)

/**
 * Perform memory access pattern analysis
 */
fun AssemblyCodeFile.analyzeMemoryPatterns(
    cfg: CfgConstruction,
    constants: ConstantPropagationAnalysis,
    resolution: AddressResolution
): MemoryAccessAnalysis {
    // Analyze each function
    val functionAnalyses = cfg.functions.mapIndexed { index, function ->
        val constantAnalysis = constants.functions.getOrNull(index)
        analyzeMemoryForFunction(this, function, constantAnalysis, resolution)
    }

    // Build global memory map by merging function analyses
    val globalMap = buildGlobalMemoryMap(functionAnalyses)

    return MemoryAccessAnalysis(
        functions = functionAnalyses,
        globalMemoryMap = globalMap
    )
}

/**
 * Analyze memory access patterns for a single function
 */
private fun analyzeMemoryForFunction(
    codeFile: AssemblyCodeFile,
    function: FunctionCfg,
    constantAnalysis: FunctionConstantAnalysis?,
    resolution: AddressResolution
): FunctionMemoryAnalysis {
    // Collect all memory accesses
    val accesses = mutableMapOf<Int, MutableList<MemoryAccess>>()

    function.blocks.forEach { block ->
        block.lineIndexes.forEach { lineIndex ->
            val lineRef = codeFile.get(lineIndex)
            val line = lineRef.content
            val instruction = line.instruction

            if (instruction != null) {
                extractMemoryAccesses(lineRef, instruction, accesses, resolution)
            }
        }
    }

    // Classify each memory location
    val memoryLocations = mutableMapOf<Int, MemoryLocationInfo>()

    accesses.forEach { (address, accessList) ->
        val pattern = classifyAccessPattern(address, accessList, constantAnalysis)
        val reads = accessList.filter { it.isRead }.map { it.lineRef }
        val writes = accessList.filter { !it.isRead }.map { it.lineRef }
        val isIndexed = accessList.any { it.indexRegister != null }
        val indirectionLevel = accessList.maxOfOrNull { it.indirectionLevel } ?: 0

        memoryLocations[address] = MemoryLocationInfo(
            address = address,
            accessPattern = pattern,
            reads = reads,
            writes = writes,
            isConstant = writes.isEmpty(),
            isIndexed = isIndexed,
            indirectionLevel = indirectionLevel
        )
    }

    // Extract specific patterns
    val arrays = memoryLocations.values.mapNotNull { info ->
        (info.accessPattern as? MemoryAccessPattern.Array)
    }

    val structs = memoryLocations.values.mapNotNull { info ->
        (info.accessPattern as? MemoryAccessPattern.StructField)
    }

    val pointers = memoryLocations.values.mapNotNull { info ->
        (info.accessPattern as? MemoryAccessPattern.Pointer)
    }

    return FunctionMemoryAnalysis(
        function = function,
        memoryAccesses = memoryLocations,
        identifiedArrays = arrays,
        identifiedStructures = structs,
        identifiedPointers = pointers
    )
}

/**
 * Internal representation of a memory access
 */
private data class MemoryAccess(
    val lineRef: AssemblyLineReference,
    val address: Int,
    val isRead: Boolean,
    val indexRegister: Variable?,  // X or Y if indexed
    val indirectionLevel: Int,      // 0=direct, 1=indirect
    val observedIndex: Int?        // Index value if known from constants
)

/**
 * Extract memory accesses from an instruction
 */
private fun extractMemoryAccesses(
    lineRef: AssemblyLineReference,
    instruction: AssemblyInstruction,
    accesses: MutableMap<Int, MutableList<MemoryAccess>>,
    resolution: AddressResolution
) {
    val addressing = instruction.address ?: return
    val op = instruction.op

    // Determine if this is a read or write
    val isRead = op !in setOf(
        AssemblyOp.STA, AssemblyOp.STX, AssemblyOp.STY,
        AssemblyOp.INC, AssemblyOp.DEC,
        AssemblyOp.ASL, AssemblyOp.LSR, AssemblyOp.ROL, AssemblyOp.ROR
    )

    when (addressing) {
        // Direct addressing
        is AssemblyAddressing.Label -> {
            val address = resolveAddress(addressing.label, resolution)
            if (address != null && address < 0x10000) {
                accesses.getOrPut(address) { mutableListOf() }.add(
                    MemoryAccess(
                        lineRef = lineRef,
                        address = address,
                        isRead = isRead,
                        indexRegister = null,
                        indirectionLevel = 0,
                        observedIndex = null
                    )
                )
            }
        }

        // Indexed addressing
        is AssemblyAddressing.DirectX -> {
            val baseAddr = resolveAddress(addressing.label, resolution)
            if (baseAddr != null && baseAddr < 0x10000) {
                accesses.getOrPut(baseAddr) { mutableListOf() }.add(
                    MemoryAccess(
                        lineRef = lineRef,
                        address = baseAddr,
                        isRead = isRead,
                        indexRegister = Variable.RegisterX,
                        indirectionLevel = 0,
                        observedIndex = null  // Would need constant propagation
                    )
                )
            }
        }

        is AssemblyAddressing.DirectY -> {
            val baseAddr = resolveAddress(addressing.label, resolution)
            if (baseAddr != null && baseAddr < 0x10000) {
                accesses.getOrPut(baseAddr) { mutableListOf() }.add(
                    MemoryAccess(
                        lineRef = lineRef,
                        address = baseAddr,
                        isRead = isRead,
                        indexRegister = Variable.RegisterY,
                        indirectionLevel = 0,
                        observedIndex = null
                    )
                )
            }
        }

        // Indirect addressing
        is AssemblyAddressing.IndirectX -> {
            val ptrAddr = resolveAddress(addressing.label, resolution)
            if (ptrAddr != null && ptrAddr < 0x100) {  // ZP only
                accesses.getOrPut(ptrAddr) { mutableListOf() }.add(
                    MemoryAccess(
                        lineRef = lineRef,
                        address = ptrAddr,
                        isRead = isRead,
                        indexRegister = Variable.RegisterX,
                        indirectionLevel = 1,
                        observedIndex = null
                    )
                )
            }
        }

        is AssemblyAddressing.IndirectY -> {
            val ptrAddr = resolveAddress(addressing.label, resolution)
            if (ptrAddr != null && ptrAddr < 0x100) {  // ZP only
                accesses.getOrPut(ptrAddr) { mutableListOf() }.add(
                    MemoryAccess(
                        lineRef = lineRef,
                        address = ptrAddr,
                        isRead = isRead,
                        indexRegister = Variable.RegisterY,
                        indirectionLevel = 1,
                        observedIndex = null
                    )
                )
            }
        }

        is AssemblyAddressing.IndirectAbsolute -> {
            val ptrAddr = resolveAddress(addressing.label, resolution)
            if (ptrAddr != null && ptrAddr < 0x10000) {
                accesses.getOrPut(ptrAddr) { mutableListOf() }.add(
                    MemoryAccess(
                        lineRef = lineRef,
                        address = ptrAddr,
                        isRead = isRead,
                        indexRegister = null,
                        indirectionLevel = 1,
                        observedIndex = null
                    )
                )
            }
        }

        else -> {
            // Immediate, accumulator, etc. - no memory access
        }
    }
}

/**
 * Classify the access pattern for a memory location
 */
private fun classifyAccessPattern(
    address: Int,
    accessList: List<MemoryAccess>,
    constantAnalysis: FunctionConstantAnalysis?
): MemoryAccessPattern {
    // Check for indexed access (array pattern)
    val indexedAccesses = accessList.filter { it.indexRegister != null }
    if (indexedAccesses.isNotEmpty()) {
        val indexRegister = indexedAccesses.first().indexRegister!!
        val indirectionLevel = indexedAccesses.first().indirectionLevel

        // Only classify as array if majority of accesses are indexed
        // This filters out incidental indexed access from memory-clearing loops
        val indexedRatio = indexedAccesses.size.toDouble() / accessList.size
        val isLikelyArray = indexedRatio >= 0.5  // At least 50% of accesses must be indexed

        if (isLikelyArray) {
            return if (indirectionLevel > 0) {
                // Indirect indexed - likely structure or dynamic array
                MemoryAccessPattern.IndirectArray(
                    basePointer = address,
                    indexRegister = indexRegister,
                    name = null
                )
            } else {
                // Direct indexed - likely array
                val observedIndices = indexedAccesses.mapNotNull { it.observedIndex }.toSet()
                MemoryAccessPattern.Array(
                    baseAddress = address,
                    indexRegister = indexRegister,
                    observedIndices = observedIndices,
                    estimatedLength = if (observedIndices.isNotEmpty()) observedIndices.maxOrNull()?.plus(1) else null,
                    name = null
                )
            }
        }
        // If less than 50% indexed, fall through to scalar classification
    }

    // Check for pointer pattern (indirect addressing without index)
    val indirectAccesses = accessList.filter { it.indirectionLevel > 0 && it.indexRegister == null }
    if (indirectAccesses.isNotEmpty() && address < 0x100) {
        return MemoryAccessPattern.Pointer(
            pointerAddress = address,
            pointsTo = null,  // Would need deeper analysis
            name = null
        )
    }

    // Default: scalar variable
    return MemoryAccessPattern.Scalar(
        address = address,
        name = null
    )
}

/**
 * Resolve address from label string using symbol table
 */
private fun resolveAddress(label: String, resolution: AddressResolution): Int? {
    // First try parsing as a numeric literal
    val numericAddress = parseAddress(label)
    if (numericAddress != null) return numericAddress

    // Otherwise lookup in symbol table
    return resolution.labelToAddress[label]
}

/**
 * Parse address from numeric label string
 */
private fun parseAddress(label: String): Int? {
    return try {
        when {
            label.startsWith("$") -> label.substring(1).toInt(16)
            label.startsWith("0x") -> label.substring(2).toInt(16)
            label.all { it.isDigit() } -> label.toInt()
            else -> null
        }
    } catch (e: NumberFormatException) {
        null
    }
}

/**
 * Build global memory map by merging function analyses
 */
private fun buildGlobalMemoryMap(
    functionAnalyses: List<FunctionMemoryAnalysis>
): Map<Int, MemoryLocationInfo> {
    // First, collect ALL accesses per address across all functions
    val accessesByAddress = mutableMapOf<Int, MutableList<MemoryAccess>>()

    functionAnalyses.forEach { funcAnalysis ->
        funcAnalysis.memoryAccesses.forEach { (address, info) ->
            // Reconstruct MemoryAccess objects from the info
            // Note: We don't have direct access to the original MemoryAccess list here,
            // but we can approximate by counting indexed vs non-indexed from the pattern
            // For now, let's use the existing merge logic but be smarter about it
            accessesByAddress.getOrPut(address) { mutableListOf() }
        }
    }

    val globalMap = mutableMapOf<Int, MemoryLocationInfo>()

    functionAnalyses.forEach { funcAnalysis ->
        funcAnalysis.memoryAccesses.forEach { (address, info) ->
            val existing = globalMap[address]

            if (existing == null) {
                globalMap[address] = info
            } else {
                // Merge: combine reads/writes
                val allReads = (existing.reads + info.reads).distinct()
                val allWrites = (existing.writes + info.writes).distinct()
                val allAccesses = allReads.size + allWrites.size

                // Determine if this should be classified as array based on global access pattern
                // Count how many accesses are indexed
                val existingIndexed = if (existing.accessPattern is MemoryAccessPattern.Array) existing.reads.size + existing.writes.size else 0
                val infoIndexed = if (info.accessPattern is MemoryAccessPattern.Array) info.reads.size + info.writes.size else 0
                val totalIndexedAccesses = existingIndexed + infoIndexed
                val indexedRatio = totalIndexedAccesses.toDouble() / allAccesses

                // Reclassify based on global ratio
                val globalPattern = if (indexedRatio >= 0.5) {
                    // Prefer array if at least 50% of ALL accesses are indexed
                    mergePatterns(existing.accessPattern, info.accessPattern)
                } else {
                    // Otherwise, treat as scalar
                    MemoryAccessPattern.Scalar(address = address, name = null)
                }

                globalMap[address] = MemoryLocationInfo(
                    address = address,
                    accessPattern = globalPattern,
                    reads = allReads,
                    writes = allWrites,
                    isConstant = existing.isConstant && info.isConstant,
                    isIndexed = existing.isIndexed || info.isIndexed,
                    indirectionLevel = maxOf(existing.indirectionLevel, info.indirectionLevel)
                )
            }
        }
    }

    return globalMap
}

/**
 * Merge two access patterns, preferring more specific information
 */
private fun mergePatterns(
    p1: MemoryAccessPattern,
    p2: MemoryAccessPattern
): MemoryAccessPattern {
    // If one is an array/struct/pointer and the other is scalar, prefer the composite
    return when {
        p1 is MemoryAccessPattern.Array || p2 is MemoryAccessPattern.Array -> {
            (p1 as? MemoryAccessPattern.Array) ?: (p2 as? MemoryAccessPattern.Array) ?: p1
        }
        p1 is MemoryAccessPattern.Pointer || p2 is MemoryAccessPattern.Pointer -> {
            (p1 as? MemoryAccessPattern.Pointer) ?: (p2 as? MemoryAccessPattern.Pointer) ?: p1
        }
        p1 is MemoryAccessPattern.StructField || p2 is MemoryAccessPattern.StructField -> {
            (p1 as? MemoryAccessPattern.StructField) ?: (p2 as? MemoryAccessPattern.StructField) ?: p1
        }
        else -> p1
    }
}

/**
 * Classify a memory address into a region type
 */
fun classifyMemoryRegion(address: Int): RegionType {
    return when {
        address < 0x0100 -> RegionType.ZERO_PAGE
        address in 0x0100..0x01FF -> RegionType.STACK
        address in 0x2000..0x2007 -> RegionType.IO_REGISTERS  // NES PPU
        address in 0x4000..0x4017 -> RegionType.IO_REGISTERS  // NES APU/IO
        address >= 0x8000 -> RegionType.ROM                    // NES ROM typically starts here
        else -> RegionType.RAM
    }
}
