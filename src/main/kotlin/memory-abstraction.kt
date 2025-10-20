package com.ivieleague.decompiler6502tokotlin

/**
 * Memory Abstraction Layer
 *
 * Provides typed accessors for known memory locations while maintaining
 * raw byte array access for bulk operations (like InitializeMemory in SMB).
 *
 * This solves the dual-access problem:
 * - Functions like InitializeMemory need: memory.fill(0) or memory[i] = value
 * - Other functions need: playerX, enemyState[0], etc.
 */

/**
 * Memory accessor strategy for a variable
 */
sealed class MemoryAccessor {
    /** Direct byte array access: memory[address] */
    data class DirectArray(val address: Int) : MemoryAccessor()

    /** Typed property accessor: var playerX by memory(0x86) */
    data class TypedProperty(val propertyName: String, val address: Int, val type: KotlinAst.KotlinType) : MemoryAccessor()

    /** Array element accessor: enemyState[index] backed by memory[baseAddr + index] */
    data class ArrayElement(val arrayName: String, val baseAddress: Int, val elementSize: Int, val count: Int) : MemoryAccessor()

    /** Multi-byte accessor: var enemyXPos: UShort backed by memory[addr] + memory[addr+1] << 8 */
    data class MultiByteProperty(val propertyName: String, val baseAddress: Int, val size: Int, val type: KotlinAst.KotlinType) : MemoryAccessor()
}

/**
 * Memory access pattern analysis result
 */
data class MemoryAccessStrategy(
    val globalVariables: List<TypedMemoryVariable>,
    val arrayVariables: List<MemoryArray>,
    val useRawArrayAccess: Boolean = true  // Always keep raw array for bulk ops
)

/**
 * A typed memory variable with property accessor
 */
data class TypedMemoryVariable(
    val name: String,
    val address: Int,
    val type: KotlinAst.KotlinType,
    val kdoc: String? = null,
    val isMutable: Boolean = true
)

/**
 * A memory-backed array
 */
data class MemoryArray(
    val name: String,
    val baseAddress: Int,
    val elementType: KotlinAst.KotlinType,
    val count: Int,
    val kdoc: String? = null
)

/**
 * Determine memory access strategy from variable identification
 */
fun VariableIdentification.createMemoryAccessStrategy(
    naming: VariableNaming?
): MemoryAccessStrategy {
    val typedVars = mutableListOf<TypedMemoryVariable>()
    val arrays = mutableListOf<MemoryArray>()

    // Process global memory variables
    for (globalVar in globals) {
        when (val id = globalVar.id) {
            is VariableId.Memory -> {
                // Single-byte memory variable
                val varName = naming?.namedVariables?.get(id)?.name
                    ?: "var_${id.address.toString(16).uppercase()}"

                val kotlinType = mapInferredTypeToKotlinType(globalVar.inferredType)

                typedVars.add(
                    TypedMemoryVariable(
                        name = varName,
                        address = id.address,
                        type = kotlinType,
                        kdoc = "Memory location \$${id.address.toString(16).uppercase()}"
                    )
                )
            }

            is VariableId.MultiByteMemory -> {
                // Multi-byte memory variable (16-bit, etc.)
                val varName = naming?.namedVariables?.get(id)?.name
                    ?: "var_${id.baseAddress.toString(16).uppercase()}_${id.size}byte"

                val kotlinType = mapInferredTypeToKotlinType(globalVar.inferredType)

                typedVars.add(
                    TypedMemoryVariable(
                        name = varName,
                        address = id.baseAddress,
                        type = kotlinType,
                        kdoc = "${id.size}-byte value at \$${id.baseAddress.toString(16).uppercase()}"
                    )
                )
            }

            is VariableId.ArrayElement -> {
                // Array element - group into arrays
                // This is complex, skip for now
            }

            else -> {
                // Skip register variables
            }
        }
    }

    return MemoryAccessStrategy(
        globalVariables = typedVars,
        arrayVariables = arrays,
        useRawArrayAccess = true
    )
}

/**
 * Generate Kotlin code for memory access layer
 */
fun MemoryAccessStrategy.emitMemoryAccessors(): List<KotlinAst.Declaration.Property> {
    val properties = mutableListOf<KotlinAst.Declaration.Property>()

    // Add typed property accessors for global variables
    for (variable in globalVariables) {
        when (variable.type) {
            KotlinAst.KotlinType.UByte, KotlinAst.KotlinType.Int -> {
                // Single-byte accessor with custom getter/setter
                properties.add(
                    KotlinAst.Declaration.Property(
                        name = variable.name,
                        type = variable.type,
                        initializer = null,  // Will use custom getter
                        isVar = variable.isMutable,
                        kdoc = variable.kdoc
                    )
                )
            }

            KotlinAst.KotlinType.UShort -> {
                // Multi-byte accessor (16-bit)
                properties.add(
                    KotlinAst.Declaration.Property(
                        name = variable.name,
                        type = variable.type,
                        initializer = null,
                        isVar = variable.isMutable,
                        kdoc = variable.kdoc
                    )
                )
            }

            else -> {
                // Default: direct property
                properties.add(
                    KotlinAst.Declaration.Property(
                        name = variable.name,
                        type = variable.type,
                        initializer = KotlinAst.Expression.Literal(0, variable.type),
                        isVar = variable.isMutable,
                        kdoc = variable.kdoc
                    )
                )
            }
        }
    }

    return properties
}

/**
 * Emit memory accessor as string with custom getter/setter
 *
 * Example output:
 * ```kotlin
 * var playerX: Int
 *     get() = memory[0x86].toInt() and 0xFF
 *     set(value) { memory[0x86] = value.toByte() }
 * ```
 */
fun TypedMemoryVariable.emitMemoryPropertyWithAccessors(): String {
    val builder = StringBuilder()

    if (kdoc != null) {
        builder.appendLine("/** $kdoc */")
    }

    val varKeyword = if (isMutable) "var" else "val"
    builder.appendLine("$varKeyword $name: $type")

    // Getter
    builder.append("    get() = ")
    when (type) {
        KotlinAst.KotlinType.UByte -> {
            builder.appendLine("memory[0x${address.toString(16).uppercase()}].toUByte()")
        }
        KotlinAst.KotlinType.Int -> {
            builder.appendLine("memory[0x${address.toString(16).uppercase()}].toInt() and 0xFF")
        }
        KotlinAst.KotlinType.UShort -> {
            // 16-bit: low byte + high byte << 8
            val highAddr = address + 1
            builder.appendLine("(memory[0x${address.toString(16).uppercase()}].toInt() and 0xFF) or ((memory[0x${highAddr.toString(16).uppercase()}].toInt() and 0xFF) shl 8)")
        }
        else -> {
            builder.appendLine("memory[0x${address.toString(16).uppercase()}].toInt()")
        }
    }

    // Setter (if mutable)
    if (isMutable) {
        builder.append("    set(value) { ")
        when (type) {
            KotlinAst.KotlinType.UByte, KotlinAst.KotlinType.Int -> {
                builder.append("memory[0x${address.toString(16).uppercase()}] = value.toByte()")
            }
            KotlinAst.KotlinType.UShort -> {
                // 16-bit: store low and high bytes
                val highAddr = address + 1
                builder.append("memory[0x${address.toString(16).uppercase()}] = (value and 0xFF).toByte(); ")
                builder.append("memory[0x${highAddr.toString(16).uppercase()}] = ((value shr 8) and 0xFF).toByte()")
            }
            else -> {
                builder.append("memory[0x${address.toString(16).uppercase()}] = value.toByte()")
            }
        }
        builder.appendLine(" }")
    }

    return builder.toString()
}
