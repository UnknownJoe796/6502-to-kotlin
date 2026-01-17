// by Claude - Extracted from kotlin-codegen.kt for maintainability
package com.ivieleague.decompiler6502tokotlin.hand

/**
 * Helper functions for code generation.
 * These are used by both instruction handlers and higher-level code generation.
 */

/**
 * Convert assembly label to Kotlin function/variable name.
 * Handles snake_case, kebab-case, and PascalCase conversion to camelCase.
 */
fun assemblyLabelToKotlinName(label: String): String {
    return if (label.contains('_') || label.contains('-')) {
        // Handle snake_case or kebab-case: Split and convert to camelCase
        label.split('_', '-')
            .joinToString("") { it.lowercase().replaceFirstChar { c -> c.uppercase() } }
            .replaceFirstChar { it.lowercase() }
    } else {
        // Already in PascalCase or camelCase, just lowercase first char
        label.replaceFirstChar { it.lowercase() }
    }
}

/**
 * Wraps a memory read expression to convert UByte to Int.
 * memory[X] returns UByte, needs .toInt() for expressions.
 */
fun wrapPropertyRead(expr: KotlinExpr): KotlinExpr {
    return when (expr) {
        is KMemberAccess -> {
            if (expr.receiver is KVar && (expr.receiver as KVar).name == "memory" && expr.isIndexed) {
                KMemberAccess(expr, KCall("toInt"), isIndexed = false)
            } else {
                expr
            }
        }
        else -> expr
    }
}

/**
 * Check if an expression is known to be in the 0-255 range (byte-sized).
 * This helps avoid unnecessary `and 0xFF` masking.
 */
fun KotlinExpr.isKnownByteSized(): Boolean {
    return when (this) {
        is KLiteral -> {
            val v = when {
                value.startsWith("0x") || value.startsWith("0X") -> value.drop(2).toIntOrNull(16)
                value.startsWith("0b") || value.startsWith("0B") -> value.drop(2).toIntOrNull(2)
                else -> value.toIntOrNull()
            }
            v != null && v in 0..255
        }
        is KVar -> true
        is KMemberAccess -> {
            if (isIndexed && receiver is KVar && (receiver as KVar).name == "memory") true
            else if (!isIndexed && member is KCall && (member as KCall).name == "toInt") true
            else false
        }
        is KParen -> expr.isKnownByteSized()
        is KBinaryOp -> {
            if (op == "and") {
                val leftVal = (left as? KLiteral)?.value?.let {
                    if (it.startsWith("0x")) it.drop(2).toIntOrNull(16) else it.toIntOrNull()
                }
                val rightVal = (right as? KLiteral)?.value?.let {
                    if (it.startsWith("0x")) it.drop(2).toIntOrNull(16) else it.toIntOrNull()
                }
                (rightVal != null && rightVal <= 0xFF) || (leftVal != null && leftVal <= 0xFF)
            } else false
        }
        else -> false
    }
}

/**
 * Wraps a register value (Int) with .toUByte() for writing to memory.
 * memory[X] expects UByte, but registers and temp variables are Int.
 * Optimized to avoid unnecessary `and 0xFF` when the value is known to be byte-sized.
 */
fun wrapPropertyWrite(target: KotlinExpr, value: KotlinExpr): KotlinExpr {
    return when {
        target is KMemberAccess && target.receiver is KVar &&
            (target.receiver as KVar).name == "memory" && target.isIndexed -> {
            if (value.isKnownByteSized()) {
                val safeValue = when (value) {
                    is KBinaryOp -> KParen(value)
                    else -> value
                }
                KMemberAccess(safeValue, KCall("toUByte"), isIndexed = false)
            } else {
                val masked = when (value) {
                    is KBinaryOp, is KLiteral -> KBinaryOp(KParen(value), "and", KLiteral("0xFF"))
                    else -> KBinaryOp(value, "and", KLiteral("0xFF"))
                }
                KMemberAccess(KParen(masked), KCall("toUByte"), isIndexed = false)
            }
        }
        else -> value
    }
}

// Hardware register prefixes that commonly have multiple registers at offsets
// (e.g., SND_SQUARE1_REG at $4000 has registers at +0, +1, +2, +3)
internal val HARDWARE_REGISTER_PREFIXES = setOf(
    "SND_SQUARE1_REG", "SND_SQUARE2_REG", "SND_TRIANGLE_REG", "SND_NOISE_REG",
    "PPU_CTRL_REG", "PPU_STATUS", "PPU_SPR_ADDR", "PPU_SPR_DATA",
    "SND_DELTA_REG"
)

/**
 * Convert addressing mode to Kotlin expression.
 */
fun AssemblyAddressing?.toKotlinExpr(ctx: CodeGenContext): KotlinExpr {
    if (this == null) return KLiteral("0")

    return when (this) {
        is AssemblyAddressing.ByteValue -> {
            KLiteral("0x${this.value.toString(16).uppercase().padStart(2, '0')}")
        }

        is AssemblyAddressing.ShortValue -> {
            KLiteral("0x${this.value.toString(16).uppercase().padStart(4, '0')}")
        }

        is AssemblyAddressing.ConstantReference -> {
            // by Claude - Bug fix: Wrap compound expressions in parentheses
            // When the constant name contains operators (like A_Button+Start_Button),
            // we need parentheses to preserve correct evaluation order in expressions like
            // `A - A_Button+Start_Button` which should be `A - (A_Button+Start_Button)`
            val expr = KVar(this.name)
            if (this.name.contains('+') || this.name.contains('-')) {
                KParen(expr)
            } else {
                expr
            }
        }

        is AssemblyAddressing.Direct -> {
            if (this.label.startsWith("$")) {
                val addr = this.label.substring(1).toIntOrNull(16) ?: 0
                val finalAddr = if (this.offset != 0) {
                    KBinaryOp(KLiteral("0x${addr.toString(16).uppercase()}"), "+", KLiteral(this.offset.toString()))
                } else {
                    KLiteral("0x${addr.toString(16).uppercase()}")
                }
                KMemberAccess(KVar("memory"), finalAddr, isIndexed = true)
            } else {
                if (this.offset != 0) {
                    val propName = ctx.registerIndexedAccess(this.label)
                    KMemberAccess(KVar(propName), KLiteral(this.offset.toString()), isIndexed = true)
                } else if (this.label in ctx.indexedMemoryAccesses) {
                    val propName = ctx.indexedMemoryAccesses[this.label]!!
                    KMemberAccess(KVar(propName), KLiteral("0"), isIndexed = true)
                } else if (this.label in HARDWARE_REGISTER_PREFIXES) {
                    val propName = ctx.registerIndexedAccess(this.label)
                    KMemberAccess(KVar(propName), KLiteral("0"), isIndexed = true)
                } else {
                    val propName = ctx.registerDirectAccess(this.label)
                    KVar(propName)
                }
            }
        }

        is AssemblyAddressing.DirectX -> {
            val index = ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X")
            if (this.label.startsWith("$")) {
                val addr = this.label.substring(1).toIntOrNull(16) ?: 0
                val baseAddr = if (this.offset != 0) {
                    KBinaryOp(KLiteral("0x${addr.toString(16).uppercase()}"), "+", KLiteral(this.offset.toString()))
                } else {
                    KLiteral("0x${addr.toString(16).uppercase()}")
                }
                KMemberAccess(KVar("memory"), KBinaryOp(baseAddr, "+", index), isIndexed = true)
            } else {
                val propName = ctx.registerIndexedAccess(this.label)
                if (this.offset != 0) {
                    val indexExpr = KBinaryOp(KLiteral(this.offset.toString()), "+", index)
                    KMemberAccess(KVar(propName), indexExpr, isIndexed = true)
                } else {
                    KMemberAccess(KVar(propName), index, isIndexed = true)
                }
            }
        }

        is AssemblyAddressing.DirectY -> {
            val index = ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y")
            if (this.label.startsWith("$")) {
                val addr = this.label.substring(1).toIntOrNull(16) ?: 0
                val baseAddr = if (this.offset != 0) {
                    KBinaryOp(KLiteral("0x${addr.toString(16).uppercase()}"), "+", KLiteral(this.offset.toString()))
                } else {
                    KLiteral("0x${addr.toString(16).uppercase()}")
                }
                KMemberAccess(KVar("memory"), KBinaryOp(baseAddr, "+", index), isIndexed = true)
            } else {
                val propName = ctx.registerIndexedAccess(this.label)
                if (this.offset != 0) {
                    val indexExpr = KBinaryOp(KLiteral(this.offset.toString()), "+", index)
                    KMemberAccess(KVar(propName), indexExpr, isIndexed = true)
                } else {
                    KMemberAccess(KVar(propName), index, isIndexed = true)
                }
            }
        }

        is AssemblyAddressing.IndirectX -> {
            val x = ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X")
            val baseExpr: KotlinExpr = if (this.label.startsWith("$")) {
                val addr = this.label.substring(1).toIntOrNull(16) ?: 0
                if (this.offset != 0) {
                    KBinaryOp(KLiteral("0x${addr.toString(16).uppercase()}"), "+", KLiteral(this.offset.toString()))
                } else {
                    KLiteral("0x${addr.toString(16).uppercase()}")
                }
            } else {
                val baseLabel = this.label
                if (this.offset != 0) {
                    KBinaryOp(KVar(baseLabel), "+", KLiteral(this.offset.toString()))
                } else {
                    KVar(baseLabel)
                }
            }
            val zpAddr = KBinaryOp(KParen(KBinaryOp(baseExpr, "+", x)), "and", KLiteral("0xFF"))
            KMemberAccess(KVar("memory"), KCall("readWord", listOf(zpAddr)), isIndexed = true)
        }

        is AssemblyAddressing.IndirectY -> {
            val y = ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y")
            val baseExpr: KotlinExpr = if (this.label.startsWith("$")) {
                val addr = this.label.substring(1).toIntOrNull(16) ?: 0
                if (this.offset != 0) {
                    KBinaryOp(KLiteral("0x${addr.toString(16).uppercase()}"), "+", KLiteral(this.offset.toString()))
                } else {
                    KLiteral("0x${addr.toString(16).uppercase()}")
                }
            } else {
                val baseLabel = this.label
                if (this.offset != 0) {
                    KBinaryOp(KVar(baseLabel), "+", KLiteral(this.offset.toString()))
                } else {
                    KVar(baseLabel)
                }
            }
            val ptrAddr = KCall("readWord", listOf(baseExpr))
            val finalAddr = KBinaryOp(ptrAddr, "+", y)
            KMemberAccess(KVar("memory"), finalAddr, isIndexed = true)
        }

        is AssemblyAddressing.IndirectAbsolute -> {
            val baseExpr: KotlinExpr = if (this.label.startsWith("$")) {
                val addr = this.label.substring(1).toIntOrNull(16) ?: 0
                if (this.offset != 0) {
                    KBinaryOp(KLiteral("0x${addr.toString(16).uppercase()}"), "+", KLiteral(this.offset.toString()))
                } else {
                    KLiteral("0x${addr.toString(16).uppercase()}")
                }
            } else {
                val baseLabel = this.label
                if (this.offset != 0) {
                    KBinaryOp(KVar(baseLabel), "+", KLiteral(this.offset.toString()))
                } else {
                    KVar(baseLabel)
                }
            }
            KCall("readWord", listOf(baseExpr))
        }

        is AssemblyAddressing.ValueLowerSelection -> {
            KBinaryOp(this.value.value.toKotlinExpr(ctx), "and", KLiteral("0xFF"))
        }

        is AssemblyAddressing.ValueUpperSelection -> {
            KBinaryOp(this.value.value.toKotlinExpr(ctx), "shr", KLiteral("8"))
        }

        // by Claude - Added handlers for constant reference hi/lo selection
        is AssemblyAddressing.ConstantReferenceLower -> {
            KBinaryOp(KVar(this.name), "and", KLiteral("0xFF"))
        }

        is AssemblyAddressing.ConstantReferenceUpper -> {
            KBinaryOp(KVar(this.name), "shr", KLiteral("8"))
        }

        else -> {
            KLiteral("/* TODO: ${this} */")
        }
    }
}

/**
 * Helper to convert UShort to Kotlin expression.
 */
fun UShort.toKotlinExpr(ctx: CodeGenContext): KotlinExpr {
    return KLiteral("0x${this.toString(16).uppercase().padStart(4, '0')}")
}
