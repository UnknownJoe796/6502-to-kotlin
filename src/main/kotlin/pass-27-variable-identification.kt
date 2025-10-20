package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 27: Variable Identification
 * - Group memory accesses into logical variables
 * - Determine variable scope (global, local, parameter)
 * - Detect multi-byte variables (16-bit values)
 * - Identify loop roles (induction variables, bounds, accumulators)
 */

/**
 * A logical variable in the decompiled code
 */
data class IdentifiedVariable(
    val id: VariableId,
    val memoryLocations: Set<MemoryLocation>,
    val scope: VariableScope,
    val inferredType: InferredType,
    val accessPattern: MemoryAccessPattern,
    val usageSites: List<VariableUsage>
)

/**
 * Unique identifier for a variable
 */
sealed class VariableId {
    data class Register(val reg: Variable) : VariableId()
    data class Memory(val address: Int) : VariableId()
    data class MultiByteMemory(val baseAddress: Int, val size: Int) : VariableId()
    data class ArrayElement(val arrayId: VariableId, val index: Int?) : VariableId()

    override fun toString(): String = when (this) {
        is Register -> "Reg($reg)"
        is Memory -> "Mem(\$${address.toString(16).uppercase().padStart(4, '0')})"
        is MultiByteMemory -> "MultiMem(\$${baseAddress.toString(16).uppercase().padStart(4, '0')}, $size bytes)"
        is ArrayElement -> "Array($arrayId[$index])"
    }
}

/**
 * Memory location reference
 */
data class MemoryLocation(
    val address: Int,
    val byteOffset: Int = 0  // For multi-byte variables: 0=LSB, 1=MSB
)

/**
 * Variable scope
 */
sealed class VariableScope {
    object Global : VariableScope()
    data class Function(val functionAddress: Int) : VariableScope()
    data class Block(val blockLeader: Int) : VariableScope()
    data class Parameter(val functionAddress: Int, val paramIndex: Int) : VariableScope()
    data class ReturnValue(val functionAddress: Int) : VariableScope()

    override fun toString(): String = when (this) {
        is Global -> "Global"
        is Function -> "Function(\$${functionAddress.toString(16).uppercase()})"
        is Block -> "Block(\$${blockLeader.toString(16).uppercase()})"
        is Parameter -> "Param($paramIndex)"
        is ReturnValue -> "Return"
    }
}

/**
 * Variable usage at a specific location
 */
data class VariableUsage(
    val lineRef: AssemblyLineReference,
    val usageType: UsageType,
    val context: UsageContext
)

enum class UsageType {
    READ, WRITE, READ_MODIFY_WRITE
}

data class UsageContext(
    val inLoop: Boolean,
    val loopRole: LoopRole?,
    val expressionRole: ExpressionRole?
)

enum class LoopRole {
    INDUCTION_VARIABLE,  // Loop counter (incremented/decremented)
    LOOP_BOUND,          // Limit value (compared against)
    LOOP_ACCUMULATOR     // Sum/product being accumulated
}

enum class ExpressionRole {
    OPERAND,
    RESULT,
    INTERMEDIATE
}

/**
 * Variable identification result
 */
data class VariableIdentification(
    val functions: List<FunctionVariables>,
    val globals: List<IdentifiedVariable>
)

/**
 * Variables for a single function
 */
data class FunctionVariables(
    val function: FunctionCfg,
    val localVariables: List<IdentifiedVariable>,
    val parameters: List<IdentifiedVariable>,
    val returnValues: List<IdentifiedVariable>
)

/**
 * Perform variable identification on the entire program
 */
fun AssemblyCodeFile.identifyVariables(
    cfg: CfgConstruction,
    dataFlow: DataFlowAnalysis,
    typeInference: TypeInferenceAnalysis,
    memoryPatterns: MemoryAccessAnalysis,
    loops: LoopDetection
): VariableIdentification {
    // First, identify all variables per function
    val functionVariables = cfg.functions.mapIndexed { index, function ->
        val funcDataFlow = dataFlow.functions.getOrNull(index)
        val funcTypes = typeInference.functions.getOrNull(index)
        val funcMemory = memoryPatterns.functions.getOrNull(index)
        val funcLoops = loops.functions.getOrNull(index)

        identifyVariablesForFunction(
            this,
            function,
            funcDataFlow,
            funcTypes,
            funcMemory,
            funcLoops
        )
    }

    // Then, identify global variables (accessed across multiple functions)
    val globals = identifyGlobalVariables(functionVariables, typeInference.globalTypes, memoryPatterns.globalMemoryMap)

    return VariableIdentification(
        functions = functionVariables,
        globals = globals
    )
}

/**
 * Identify variables for a single function
 */
private fun identifyVariablesForFunction(
    codeFile: AssemblyCodeFile,
    function: FunctionCfg,
    dataFlow: FunctionDataFlow?,
    typeInfo: FunctionTypeInfo?,
    memoryAnalysis: FunctionMemoryAnalysis?,
    loopAnalysis: FunctionLoopInfo?
): FunctionVariables {
    val identifiedVars = mutableMapOf<VariableId, IdentifiedVariable>()

    // 1. Identify CPU registers used by this function
    val registerVars = identifyRegisterVariables(codeFile, function, dataFlow, typeInfo)
    registerVars.forEach { identifiedVars[it.id] = it }

    // 2. Identify multi-byte variables
    val multiByteVars = detectMultiByteVariables(codeFile, function, memoryAnalysis)
    multiByteVars.forEach { identifiedVars[it.id] = it }

    // 3. Identify single-byte memory variables
    memoryAnalysis?.memoryAccesses?.forEach { (address, memInfo) ->
        // Skip if already part of a multi-byte variable
        if (multiByteVars.any { it.memoryLocations.any { loc -> loc.address == address } }) {
            return@forEach
        }

        val usages = collectUsages(codeFile, address, function, loopAnalysis)
        val typedVar = typeInfo?.variableTypes?.get(Variable.Memory(address))

        val variableId = VariableId.Memory(address)
        identifiedVars[variableId] = IdentifiedVariable(
            id = variableId,
            memoryLocations = setOf(MemoryLocation(address)),
            scope = determineScope(address, function, memoryAnalysis, typeInfo),
            inferredType = typedVar?.inferredType ?: InferredType.UInt8(),
            accessPattern = memInfo.accessPattern,
            usageSites = usages
        )
    }

    // 4. Separate into parameters, return values, and locals
    val parameters = mutableListOf<IdentifiedVariable>()
    val returnValues = mutableListOf<IdentifiedVariable>()
    val localVariables = mutableListOf<IdentifiedVariable>()

    identifiedVars.values.forEach { variable ->
        when (variable.scope) {
            is VariableScope.Parameter -> parameters.add(variable)
            is VariableScope.ReturnValue -> returnValues.add(variable)
            else -> localVariables.add(variable)
        }
    }

    return FunctionVariables(
        function = function,
        localVariables = localVariables,
        parameters = parameters,
        returnValues = returnValues
    )
}

/**
 * Identify CPU registers (A, X, Y, SP) used by this function
 * Determines if each register is:
 * - Parameter (live-in at function entry)
 * - Return value (live-out at function exit)
 * - Local variable (used only within function)
 */
private fun identifyRegisterVariables(
    codeFile: AssemblyCodeFile,
    function: FunctionCfg,
    dataFlow: FunctionDataFlow?,
    typeInfo: FunctionTypeInfo?
): List<IdentifiedVariable> {
    val registerVars = mutableListOf<IdentifiedVariable>()

    // Check each CPU register
    val registersToCheck = listOf(
        Variable.RegisterA,
        Variable.RegisterX,
        Variable.RegisterY,
        Variable.StackPointer
    )

    for (register in registersToCheck) {
        // Check if register is used by this function
        val isUsed = isRegisterUsedInFunction(register, function, codeFile)
        if (!isUsed) continue

        // Determine if it's a parameter (read before written)
        val entryFacts = dataFlow?.blockFacts?.get(function.entryLeader)
        val isParameter = entryFacts?.liveIn?.contains(register) ?: false

        // Determine if it's a return value (live at exit)
        val exitBlocks = function.blocks.filter { block ->
            // Block ends with RTS or is a leaf in CFG
            val lastLine = block.lineIndexes.lastOrNull()
            lastLine != null && codeFile.get(lastLine).content.instruction?.op == AssemblyOp.RTS
        }
        val isReturn = exitBlocks.any { exitBlock ->
            dataFlow?.blockFacts?.get(exitBlock.leaderIndex)?.liveOut?.contains(register) == true
        }

        // Determine scope
        val scope = when {
            isParameter -> {
                val paramIndex = typeInfo?.parameters?.indexOfFirst { it.variable == register } ?: 0
                VariableScope.Parameter(function.entryAddress, paramIndex)
            }
            isReturn -> VariableScope.ReturnValue(function.entryAddress)
            else -> VariableScope.Function(function.entryAddress)
        }

        // Collect usage sites
        val usages = collectRegisterUsages(register, function, codeFile)

        val variableId = VariableId.Register(register)
        registerVars.add(
            IdentifiedVariable(
                id = variableId,
                memoryLocations = emptySet(),
                scope = scope,
                inferredType = InferredType.UInt8(),
                accessPattern = MemoryAccessPattern.Scalar(0, null),
                usageSites = usages
            )
        )
    }

    return registerVars
}

/**
 * Check if a register is used in a function
 */
private fun isRegisterUsedInFunction(
    register: Variable,
    function: FunctionCfg,
    codeFile: AssemblyCodeFile
): Boolean {
    return function.blocks.any { block ->
        block.lineIndexes.any { lineIndex ->
            val instruction = codeFile.get(lineIndex).content.instruction
            instruction != null && isInstructionUsingRegister(instruction, register)
        }
    }
}

/**
 * Check if an instruction uses a specific register
 */
private fun isInstructionUsingRegister(instruction: AssemblyInstruction, register: Variable): Boolean {
    return when (register) {
        Variable.RegisterA -> {
            instruction.op in setOf(
                AssemblyOp.LDA, AssemblyOp.STA, AssemblyOp.ADC, AssemblyOp.SBC,
                AssemblyOp.AND, AssemblyOp.ORA, AssemblyOp.EOR, AssemblyOp.CMP,
                AssemblyOp.TAX, AssemblyOp.TAY, AssemblyOp.TXA, AssemblyOp.TYA
            ) || (instruction.op in setOf(AssemblyOp.ASL, AssemblyOp.LSR, AssemblyOp.ROL, AssemblyOp.ROR) &&
                  (instruction.address == null || instruction.address is AssemblyAddressing.Accumulator))
        }
        Variable.RegisterX -> instruction.op in setOf(
            AssemblyOp.LDX, AssemblyOp.STX, AssemblyOp.INX, AssemblyOp.DEX,
            AssemblyOp.CPX, AssemblyOp.TAX, AssemblyOp.TXA, AssemblyOp.TXS, AssemblyOp.TSX
        ) || instruction.address is AssemblyAddressing.DirectX || instruction.address is AssemblyAddressing.IndirectX
        Variable.RegisterY -> instruction.op in setOf(
            AssemblyOp.LDY, AssemblyOp.STY, AssemblyOp.INY, AssemblyOp.DEY,
            AssemblyOp.CPY, AssemblyOp.TAY, AssemblyOp.TYA
        ) || instruction.address is AssemblyAddressing.DirectY || instruction.address is AssemblyAddressing.IndirectY
        Variable.StackPointer -> instruction.op in setOf(
            AssemblyOp.TXS, AssemblyOp.TSX, AssemblyOp.PHA, AssemblyOp.PLA,
            AssemblyOp.PHP, AssemblyOp.PLP, AssemblyOp.JSR, AssemblyOp.RTS
        )
        else -> false
    }
}

/**
 * Collect all usages of a register in a function
 */
private fun collectRegisterUsages(
    register: Variable,
    function: FunctionCfg,
    codeFile: AssemblyCodeFile
): List<VariableUsage> {
    val usages = mutableListOf<VariableUsage>()

    function.blocks.forEach { block ->
        block.lineIndexes.forEach { lineIndex ->
            val lineRef = codeFile.get(lineIndex)
            val instruction = lineRef.content.instruction

            if (instruction != null && isInstructionUsingRegister(instruction, register)) {
                val usageType = when {
                    isRegisterWrite(instruction, register) -> UsageType.WRITE
                    isRegisterRead(instruction, register) -> UsageType.READ
                    else -> UsageType.READ_MODIFY_WRITE
                }

                usages.add(
                    VariableUsage(
                        lineRef = lineRef,
                        usageType = usageType,
                        context = UsageContext(
                            inLoop = false,  // TODO: check loop info
                            loopRole = null,
                            expressionRole = null
                        )
                    )
                )
            }
        }
    }

    return usages
}

/**
 * Check if instruction writes to register
 */
private fun isRegisterWrite(instruction: AssemblyInstruction, register: Variable): Boolean {
    return when (register) {
        Variable.RegisterA -> {
            instruction.op in setOf(
                AssemblyOp.LDA, AssemblyOp.ADC, AssemblyOp.SBC, AssemblyOp.AND,
                AssemblyOp.ORA, AssemblyOp.EOR, AssemblyOp.TXA, AssemblyOp.TYA, AssemblyOp.PLA
            ) || (instruction.op in setOf(AssemblyOp.ASL, AssemblyOp.LSR, AssemblyOp.ROL, AssemblyOp.ROR) &&
                  (instruction.address == null || instruction.address is AssemblyAddressing.Accumulator))
        }
        Variable.RegisterX -> instruction.op in setOf(
            AssemblyOp.LDX, AssemblyOp.INX, AssemblyOp.DEX, AssemblyOp.TAX, AssemblyOp.TSX
        )
        Variable.RegisterY -> instruction.op in setOf(
            AssemblyOp.LDY, AssemblyOp.INY, AssemblyOp.DEY, AssemblyOp.TAY
        )
        Variable.StackPointer -> instruction.op in setOf(AssemblyOp.TXS)
        else -> false
    }
}

/**
 * Check if instruction reads from register
 */
private fun isRegisterRead(instruction: AssemblyInstruction, register: Variable): Boolean {
    return when (register) {
        Variable.RegisterA -> instruction.op in setOf(
            AssemblyOp.STA, AssemblyOp.CMP, AssemblyOp.TAX, AssemblyOp.TAY, AssemblyOp.PHA
        )
        Variable.RegisterX -> instruction.op in setOf(
            AssemblyOp.STX, AssemblyOp.CPX, AssemblyOp.TXA, AssemblyOp.TXS
        ) || instruction.address is AssemblyAddressing.DirectX || instruction.address is AssemblyAddressing.IndirectX
        Variable.RegisterY -> instruction.op in setOf(
            AssemblyOp.STY, AssemblyOp.CPY, AssemblyOp.TYA
        ) || instruction.address is AssemblyAddressing.DirectY || instruction.address is AssemblyAddressing.IndirectY
        Variable.StackPointer -> instruction.op in setOf(
            AssemblyOp.TSX, AssemblyOp.PHA, AssemblyOp.PLA, AssemblyOp.PHP, AssemblyOp.PLP
        )
        else -> false
    }
}

/**
 * Detect multi-byte variables (16-bit values)
 */
private fun detectMultiByteVariables(
    codeFile: AssemblyCodeFile,
    function: FunctionCfg,
    memoryAnalysis: FunctionMemoryAnalysis?
): List<IdentifiedVariable> {
    val multiByteVars = mutableListOf<IdentifiedVariable>()
    val processed = mutableSetOf<Int>()

    // Look for consecutive byte accesses that suggest multi-byte values
    function.blocks.forEach { block ->
        var i = 0
        val lines = block.lineIndexes.map { codeFile.get(it) }

        while (i < lines.size - 1) {
            val line1 = lines[i]
            val line2 = lines[i + 1]
            val instr1 = line1.content.instruction
            val instr2 = line2.content.instruction

            if (instr1 != null && instr2 != null) {
                // Pattern: LDA addr / LDX addr+1 suggests 16-bit load
                val addr1 = getMemoryAddress(instr1)
                val addr2 = getMemoryAddress(instr2)

                if (addr1 != null && addr2 != null && addr2 == addr1 + 1 && addr1 !in processed) {
                    // Found potential 16-bit variable
                    val variableId = VariableId.MultiByteMemory(addr1, 2)
                    multiByteVars.add(
                        IdentifiedVariable(
                            id = variableId,
                            memoryLocations = setOf(
                                MemoryLocation(addr1, 0),
                                MemoryLocation(addr2, 1)
                            ),
                            scope = VariableScope.Function(function.entryAddress),
                            inferredType = InferredType.UInt16(),
                            accessPattern = MemoryAccessPattern.Scalar(addr1, null),
                            usageSites = emptyList()  // TODO: collect usages
                        )
                    )
                    processed.add(addr1)
                    processed.add(addr2)
                }
            }
            i++
        }
    }

    return multiByteVars
}

/**
 * Get memory address from instruction, if any
 */
private fun getMemoryAddress(instruction: AssemblyInstruction): Int? {
    return when (val addr = instruction.address) {
        is AssemblyAddressing.Label -> {
            // Try to parse as hex address
            val label = addr.label
            when {
                label.startsWith("$") -> label.substring(1).toIntOrNull(16)
                label.startsWith("0x") -> label.substring(2).toIntOrNull(16)
                else -> null
            }
        }
        else -> null
    }
}

/**
 * Collect all usages of a memory location
 */
private fun collectUsages(
    codeFile: AssemblyCodeFile,
    address: Int,
    function: FunctionCfg,
    loopAnalysis: FunctionLoopInfo?
): List<VariableUsage> {
    val usages = mutableListOf<VariableUsage>()

    function.blocks.forEach { block ->
        val inLoop = loopAnalysis?.loops?.any { loop ->
            block.leaderIndex in loop.body
        } ?: false

        block.lineIndexes.forEach { lineIndex ->
            val lineRef = codeFile.get(lineIndex)
            val instruction = lineRef.content.instruction

            if (instruction != null) {
                val accessedAddr = getMemoryAddress(instruction)
                if (accessedAddr == address) {
                    val usageType = when (instruction.op) {
                        AssemblyOp.STA, AssemblyOp.STX, AssemblyOp.STY -> UsageType.WRITE
                        AssemblyOp.INC, AssemblyOp.DEC -> UsageType.READ_MODIFY_WRITE
                        else -> UsageType.READ
                    }

                    // Determine loop role if in a loop
                    val loopRole = if (inLoop) {
                        when (instruction.op) {
                            AssemblyOp.INC, AssemblyOp.DEC, AssemblyOp.INX, AssemblyOp.INY,
                            AssemblyOp.DEX, AssemblyOp.DEY -> LoopRole.INDUCTION_VARIABLE
                            AssemblyOp.CMP, AssemblyOp.CPX, AssemblyOp.CPY -> LoopRole.LOOP_BOUND
                            AssemblyOp.ADC, AssemblyOp.SBC -> LoopRole.LOOP_ACCUMULATOR
                            else -> null
                        }
                    } else null

                    usages.add(
                        VariableUsage(
                            lineRef = lineRef,
                            usageType = usageType,
                            context = UsageContext(
                                inLoop = inLoop,
                                loopRole = loopRole,
                                expressionRole = null  // TODO: determine from expression analysis
                            )
                        )
                    )
                }
            }
        }
    }

    return usages
}

/**
 * Determine the scope of a variable
 */
private fun determineScope(
    address: Int,
    function: FunctionCfg,
    memoryAnalysis: FunctionMemoryAnalysis?,
    typeInfo: FunctionTypeInfo?
): VariableScope {
    // Check if it's a parameter (live-in at entry)
    val isParameter = typeInfo?.parameters?.any {
        (it.variable as? Variable.Memory)?.address == address
    } ?: false

    if (isParameter) {
        val paramIndex = typeInfo?.parameters?.indexOfFirst {
            (it.variable as? Variable.Memory)?.address == address
        } ?: 0
        return VariableScope.Parameter(function.entryAddress, paramIndex)
    }

    // Check if it's a return value (live-out at exit)
    val isReturn = typeInfo?.returns?.any {
        (it.variable as? Variable.Memory)?.address == address
    } ?: false

    if (isReturn) {
        return VariableScope.ReturnValue(function.entryAddress)
    }

    // Otherwise, it's a local variable
    return VariableScope.Function(function.entryAddress)
}

/**
 * Identify global variables (accessed by multiple functions)
 */
private fun identifyGlobalVariables(
    functionVariables: List<FunctionVariables>,
    globalTypes: Map<Variable, TypedVariable>,
    globalMemoryMap: Map<Int, MemoryLocationInfo>
): List<IdentifiedVariable> {
    // Track which addresses are accessed by which functions
    val addressToFunctions = mutableMapOf<Int, MutableSet<Int>>()

    functionVariables.forEach { funcVars ->
        val functionAddr = funcVars.function.entryAddress

        (funcVars.localVariables + funcVars.parameters + funcVars.returnValues).forEach { variable ->
            variable.memoryLocations.forEach { memLoc ->
                addressToFunctions.getOrPut(memLoc.address) { mutableSetOf() }.add(functionAddr)
            }
        }
    }

    // Global variables are accessed by multiple functions
    val globalAddresses = addressToFunctions.filter { it.value.size > 1 }.keys

    return globalAddresses.map { address ->
        val memInfo = globalMemoryMap[address]
        val typedVar = globalTypes[Variable.Memory(address)]

        IdentifiedVariable(
            id = VariableId.Memory(address),
            memoryLocations = setOf(MemoryLocation(address)),
            scope = VariableScope.Global,
            inferredType = typedVar?.inferredType ?: InferredType.UInt8(),
            accessPattern = memInfo?.accessPattern ?: MemoryAccessPattern.Scalar(address, null),
            usageSites = emptyList()  // Could collect from all functions
        )
    }
}
