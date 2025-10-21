package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 29: Parameter Recovery
 * - Identify function parameters from calling conventions
 * - Detect return values
 * - Infer calling conventions
 * - Identify side effects
 */

/**
 * Function signature with parameters and return values
 */
data class FunctionSignature(
    val function: FunctionCfg,
    val name: String,
    val parameters: List<Parameter>,
    val returnValue: ReturnValue?,
    val callingConvention: CallingConvention,
    val sideEffects: List<SideEffect>
)

/**
 * A function parameter
 */
data class Parameter(
    val name: String,
    val type: InferredType,
    val location: ParameterLocation,
    val index: Int,
    val isOptional: Boolean = false,
    val defaultValue: Int? = null
)

/**
 * Where a parameter is passed
 */
sealed class ParameterLocation {
    data class Register(val register: Variable) : ParameterLocation() {
        override fun toString() = "Reg($register)"
    }
    data class Memory(val address: Int) : ParameterLocation() {
        override fun toString() = "Mem(\$${address.toString(16).uppercase()})"
    }
    object Stack : ParameterLocation() {
        override fun toString() = "Stack"
    }
    data class RegisterPair(val low: Variable, val high: Variable) : ParameterLocation() {
        override fun toString() = "RegPair($low,$high)"
    }
}

/**
 * Function return value
 */
data class ReturnValue(
    val name: String,
    val type: InferredType,
    val location: ParameterLocation
)

/**
 * Calling convention used by a function
 */
sealed class CallingConvention {
    /** Register-based (most common in 6502) */
    data class RegisterBased(
        val parameterOrder: List<Variable>,
        val returnRegister: Variable?
    ) : CallingConvention()

    /** Memory-based */
    data class MemoryBased(
        val parameterAddresses: List<Int>
    ) : CallingConvention()

    /** Stack-based (rare on 6502) */
    object StackBased : CallingConvention()

    /** Mixed */
    data class Mixed(
        val registers: List<Variable>,
        val memoryLocations: List<Int>
    ) : CallingConvention()
}

/**
 * Side effect of a function
 */
sealed class SideEffect {
    data class ModifiesMemory(val address: Int, val name: String?) : SideEffect()
    data class ModifiesGlobal(val variable: IdentifiedVariable) : SideEffect()
    data class CallsFunction(val targetFunction: FunctionCfg) : SideEffect()
    object ModifiesHardware : SideEffect()
}

/**
 * Parameter recovery result
 */
data class ParameterRecovery(
    val functionSignatures: List<FunctionSignature>,
    val inferredConventions: Map<CallingConvention, Int>
)

/**
 * Recover function parameters and signatures
 */
fun VariableIdentification.recoverParameters(
    cfg: CfgConstruction,
    callGraph: CallGraphConstruction,
    naming: VariableNaming,
    resolution: AddressResolution
): ParameterRecovery {
    val signatures = functions.map { funcVars ->
        val function = funcVars.function
        val callGraphFunc = callGraph.functionCallInfo[function.entryLeader]

        recoverFunctionSignature(
            function,
            funcVars,
            callGraphFunc,
            cfg,
            naming,
            resolution
        )
    }

    // Count calling conventions
    val conventionCounts = signatures.groupingBy { it.callingConvention::class }.eachCount()

    return ParameterRecovery(
        functionSignatures = signatures,
        inferredConventions = emptyMap()  // TODO: populate properly
    )
}

/**
 * Recover signature for a single function
 */
private fun recoverFunctionSignature(
    function: FunctionCfg,
    funcVars: FunctionVariables,
    callGraphInfo: FunctionCallInfo?,
    cfg: CfgConstruction,
    naming: VariableNaming,
    resolution: AddressResolution
): FunctionSignature {
    // Get function name
    val functionName = function.entryLabel ?: "func_${function.entryAddress.toString(16).uppercase()}"

    // Analyze call sites to determine parameters
    val parameters = identifyParameters(function, funcVars, callGraphInfo, naming)

    // Identify return value
    val returnValue = identifyReturnValue(function, funcVars, naming)

    // Infer calling convention
    val callingConvention = inferCallingConvention(parameters, returnValue)

    // Identify side effects
    val sideEffects = identifySideEffects(function, funcVars, cfg)

    return FunctionSignature(
        function = function,
        name = functionName,
        parameters = parameters,
        returnValue = returnValue,
        callingConvention = callingConvention,
        sideEffects = sideEffects
    )
}

/**
 * Identify function parameters
 */
private fun identifyParameters(
    function: FunctionCfg,
    funcVars: FunctionVariables,
    callGraphInfo: FunctionCallInfo?,
    naming: VariableNaming
): List<Parameter> {
    val parameters = mutableListOf<Parameter>()

    // Use already-identified parameters from variable identification
    funcVars.parameters.forEachIndexed { index, variable ->
        val namedVar = naming.namedVariables[variable.id]
        val paramName = namedVar?.name ?: "param$index"

        val location = when (variable.id) {
            is VariableId.Register -> {
                ParameterLocation.Register((variable.id as VariableId.Register).reg)
            }
            is VariableId.Memory -> {
                ParameterLocation.Memory((variable.id as VariableId.Memory).address)
            }
            is VariableId.MultiByteMemory -> {
                // For 16-bit values, use register pair if in A+X
                ParameterLocation.Memory((variable.id as VariableId.MultiByteMemory).baseAddress)
            }
            else -> ParameterLocation.Memory(0)
        }

        parameters.add(
            Parameter(
                name = paramName,
                type = variable.inferredType,
                location = location,
                index = index
            )
        )
    }

    // If no parameters identified, try to infer from register usage
    if (parameters.isEmpty()) {
        // Check if function reads registers at entry
        val firstBlock = function.blocks.firstOrNull()
        if (firstBlock != null) {
            // This is a simple heuristic - could be enhanced
            // For now, we assume common 6502 convention: A, X, Y
        }
    }

    return parameters
}

/**
 * Identify function return value
 */
private fun identifyReturnValue(
    function: FunctionCfg,
    funcVars: FunctionVariables,
    naming: VariableNaming
): ReturnValue? {
    // Use already-identified return values from variable identification
    val returnVar = funcVars.returnValues.firstOrNull() ?: return null

    val namedVar = naming.namedVariables[returnVar.id]
    val returnName = namedVar?.name ?: "result"

    val location = when (returnVar.id) {
        is VariableId.Register -> {
            ParameterLocation.Register((returnVar.id as VariableId.Register).reg)
        }
        is VariableId.Memory -> {
            ParameterLocation.Memory((returnVar.id as VariableId.Memory).address)
        }
        else -> ParameterLocation.Register(Variable.RegisterA)  // Default
    }

    return ReturnValue(
        name = returnName,
        type = returnVar.inferredType,
        location = location
    )
}

/**
 * Infer calling convention from parameters and return value
 */
private fun inferCallingConvention(
    parameters: List<Parameter>,
    returnValue: ReturnValue?
): CallingConvention {
    // Check if all parameters are in registers
    val registerParams = parameters.mapNotNull {
        (it.location as? ParameterLocation.Register)?.register
    }

    val memoryParams = parameters.mapNotNull {
        (it.location as? ParameterLocation.Memory)?.address
    }

    return when {
        // All register-based
        registerParams.size == parameters.size -> {
            val returnReg = (returnValue?.location as? ParameterLocation.Register)?.register
            CallingConvention.RegisterBased(registerParams, returnReg)
        }

        // All memory-based
        memoryParams.size == parameters.size -> {
            CallingConvention.MemoryBased(memoryParams)
        }

        // Mixed
        registerParams.isNotEmpty() && memoryParams.isNotEmpty() -> {
            CallingConvention.Mixed(registerParams, memoryParams)
        }

        // Default to register-based
        else -> {
            CallingConvention.RegisterBased(emptyList(), Variable.RegisterA)
        }
    }
}

/**
 * Identify function side effects
 */
private fun identifySideEffects(
    function: FunctionCfg,
    funcVars: FunctionVariables,
    cfg: CfgConstruction
): List<SideEffect> {
    val sideEffects = mutableListOf<SideEffect>()

    // Check for memory modifications
    funcVars.localVariables.forEach { variable ->
        val hasWrites = variable.usageSites.any { it.usageType == UsageType.WRITE || it.usageType == UsageType.READ_MODIFY_WRITE }
        if (hasWrites && variable.scope is VariableScope.Global) {
            sideEffects.add(SideEffect.ModifiesGlobal(variable))
        }
    }

    // Check for hardware register writes (PPU, APU, etc.)
    funcVars.localVariables.forEach { variable ->
        val address = when (val id = variable.id) {
            is VariableId.Memory -> id.address
            else -> null
        }

        if (address != null && isHardwareRegister(address)) {
            sideEffects.add(SideEffect.ModifiesHardware)
        }
    }

    return sideEffects
}

/**
 * Check if an address is a hardware register (NES-specific)
 */
private fun isHardwareRegister(address: Int): Boolean {
    return address in 0x2000..0x2007 ||  // PPU registers
           address in 0x4000..0x4017      // APU/IO registers
}
