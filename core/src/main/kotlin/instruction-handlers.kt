// by Claude - Extracted from kotlin-codegen.kt for maintainability
package com.ivieleague.decompiler6502tokotlin.hand

/**
 * Instruction-to-Kotlin code generation.
 *
 * This file contains the core instruction handler that converts 6502 opcodes
 * to their Kotlin equivalents. Each opcode maps to Kotlin code that performs
 * the equivalent operation.
 */

/**
 * Convert a 6502 instruction to Kotlin statements.
 *
 * This is the core instruction-to-code translator. Each 6502 opcode maps to
 * Kotlin code that performs the equivalent operation.
 *
 * @param lineIndex The original line index of this instruction (for JumpEngine lookup)
 */
fun AssemblyInstruction.toKotlin(ctx: CodeGenContext, lineIndex: Int = -1): List<KotlinStmt> {
    val stmts = mutableListOf<KotlinStmt>()

    when (this.op) {
        // ===========================
        // Load instructions
        // All load instructions set Z (zero) and N (negative) flags
        // ===========================
        // by Claude - LDA: always generate a statement to ensure the value is captured
        // Previous bug: when no function-level var existed, only ctx.registerA was updated
        // without generating a statement, losing the modification in if-bodies
        AssemblyOp.LDA -> {
            val rawValue = this.address.toKotlinExpr(ctx)
            val value = wrapPropertyRead(rawValue)
            // Always get or create a function-level variable to ensure statement is generated
            val (varName, isNew) = ctx.getOrCreateFunctionLevelVar("A")
            val varRef = KVar(varName)
            if (isNew) {
                stmts.add(KVarDecl(varName, "Int", value, mutable = true))
            } else {
                stmts.add(KAssignment(varRef, value))
            }
            ctx.registerA = varRef
            // LDA sets Z flag if value == 0, N flag if bit 7 is set
            ctx.zeroFlag = KBinaryOp(varRef, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(varRef, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        // by Claude - LDX: always generate a statement to ensure the value is captured
        // Previous bug: when no function-level var existed, only ctx.registerX was updated
        // without generating a statement, losing the modification in if-bodies
        AssemblyOp.LDX -> {
            val rawValue = this.address.toKotlinExpr(ctx)
            val value = wrapPropertyRead(rawValue)
            // Always get or create a function-level variable to ensure statement is generated
            val (varName, isNew) = ctx.getOrCreateFunctionLevelVar("X")
            val varRef = KVar(varName)
            if (isNew) {
                stmts.add(KVarDecl(varName, "Int", value, mutable = true))
            } else {
                stmts.add(KAssignment(varRef, value))
            }
            ctx.registerX = varRef
            // LDX sets Z flag if value == 0, N flag if bit 7 is set
            ctx.zeroFlag = KBinaryOp(varRef, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(varRef, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        // by Claude - LDY: always generate a statement to ensure the value is captured
        // Previous bug: when no function-level var existed, only ctx.registerY was updated
        // without generating a statement, losing the modification in if-bodies
        AssemblyOp.LDY -> {
            val rawValue = this.address.toKotlinExpr(ctx)
            val value = wrapPropertyRead(rawValue)
            // Always get or create a function-level variable to ensure statement is generated
            val (varName, isNew) = ctx.getOrCreateFunctionLevelVar("Y")
            val varRef = KVar(varName)
            if (isNew) {
                stmts.add(KVarDecl(varName, "Int", value, mutable = true))
            } else {
                stmts.add(KAssignment(varRef, value))
            }
            ctx.registerY = varRef
            // LDY sets Z flag if value == 0, N flag if bit 7 is set
            ctx.zeroFlag = KBinaryOp(varRef, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(varRef, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        // ===========================
        // Store instructions
        // ===========================
        AssemblyOp.STA -> {
            val target = this.address.toKotlinExpr(ctx)
            val rawValue = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            val value = wrapPropertyWrite(target, rawValue)
            stmts.add(KAssignment(target, value))
        }

        AssemblyOp.STX -> {
            val target = this.address.toKotlinExpr(ctx)
            val rawValue = ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X")
            val value = wrapPropertyWrite(target, rawValue)
            stmts.add(KAssignment(target, value))
        }

        AssemblyOp.STY -> {
            val target = this.address.toKotlinExpr(ctx)
            val rawValue = ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y")
            val value = wrapPropertyWrite(target, rawValue)
            stmts.add(KAssignment(target, value))
        }

        // ===========================
        // Transfer instructions
        // All transfer instructions set Z and N flags based on value transferred
        // ===========================
        // by Claude - Bug #13/#14/#18 fix: Always emit assignment for transfer instructions
        // The previous implementation just aliased registers (ctx.registerY = ctx.registerA),
        // but this breaks when the source register is later modified before the target is used.
        // Example: TAY followed by PLA - Y should keep the pre-PLA value of A.
        AssemblyOp.TAX -> {
            val value = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            // by Claude - Always emit X = A to materialize the transfer
            stmts.add(KAssignment(KVar("X"), value))
            ctx.registerX = KVar("X")
            ctx.zeroFlag = KBinaryOp(KVar("X"), "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(KVar("X"), "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.TAY -> {
            val value = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            // by Claude - Always emit Y = A to materialize the transfer
            stmts.add(KAssignment(KVar("Y"), value))
            ctx.registerY = KVar("Y")
            ctx.zeroFlag = KBinaryOp(KVar("Y"), "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(KVar("Y"), "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.TXA -> {
            val value = ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X")
            // by Claude - Always emit A = X to materialize the transfer
            stmts.add(KAssignment(KVar("A"), value))
            ctx.registerA = KVar("A")
            ctx.zeroFlag = KBinaryOp(KVar("A"), "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(KVar("A"), "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.TYA -> {
            val value = ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y")
            // by Claude - Always emit A = Y to materialize the transfer
            stmts.add(KAssignment(KVar("A"), value))
            ctx.registerA = KVar("A")
            ctx.zeroFlag = KBinaryOp(KVar("A"), "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(KVar("A"), "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        // ===========================
        // Arithmetic instructions
        // ===========================
        AssemblyOp.ADC -> {
            val operand = this.address.toKotlinExpr(ctx)
            val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            val carryIn = ctx.carryFlag ?: KLiteral("false")

            // by Claude - Fixed to use proper KIfExpr instead of embedding raw syntax in KLiteral
            // Simplify carry to int: if carry is literal true/false, just use 1 or 0
            // Otherwise generate: if (carry) 1 else 0
            val carryInt: KotlinExpr = when {
                carryIn is KLiteral && carryIn.value in listOf("0", "false") -> KLiteral("0")
                carryIn is KLiteral && carryIn.value in listOf("1", "true") -> KLiteral("1")
                else -> KIfExpr(carryIn, KLiteral("1"), KLiteral("0"))
            }

            // Full sum (may exceed 255)
            val fullSum = KBinaryOp(KBinaryOp(a, "+", wrapPropertyRead(operand)), "+", carryInt)

            // CRITICAL FIX: Store the full sum in a temp variable so the carry check
            // is based on the value at the time of the ADC, not after A is reassigned.
            // Without this, the carryFlag expression would reference A (e.g., temp2) which
            // will have a different value after the masked result is assigned.
            val sumVarName = ctx.nextTempVar()
            stmts.add(KVarDecl(sumVarName, "Int", fullSum, mutable = false))
            val sumVar = KVar(sumVarName)

            // Result masked to 8 bits (now using the captured sum variable)
            val result = KBinaryOp(sumVar, "and", KLiteral("0xFF"))

            // CRITICAL FIX: If there's a function-level variable for A, we need to update it
            // so that stores after merged branches use the correct value.
            // Without this, code inside an if-block would compute a new value but the
            // store after the if would use the old function-level var value.
            val funcLevelVar = ctx.getFunctionLevelVar("A")
            if (funcLevelVar != null) {
                stmts.add(KAssignment(funcLevelVar, result))
                ctx.registerA = funcLevelVar
            } else {
                ctx.registerA = result
            }

            // Carry set if sum > 255 (using the captured sum variable)
            ctx.carryFlag = KBinaryOp(sumVar, ">", KLiteral("0xFF"))
            ctx.zeroFlag = KBinaryOp(result, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(result, "and", KLiteral("0x80"))), "!=", KLiteral("0"))

            // by Claude - Overflow flag for ADC: set when adding two positives gives negative,
            // or adding two negatives gives positive. Formula: V = (A ^ result) & (operand ^ result) & 0x80
            // This detects when the sign of the result differs from what you'd expect from the operand signs.
            val operandWrapped = wrapPropertyRead(operand)
            val aXorResult = KBinaryOp(a, "xor", result)
            val opXorResult = KBinaryOp(operandWrapped, "xor", result)
            val overflowExpr = KBinaryOp(KParen(KBinaryOp(aXorResult, "and", opXorResult)), "and", KLiteral("0x80"))
            ctx.overflowFlag = KBinaryOp(overflowExpr, "!=", KLiteral("0"))
        }

        AssemblyOp.SBC -> {
            val operand = this.address.toKotlinExpr(ctx)
            val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            val carryIn = ctx.carryFlag ?: KLiteral("true")

            // by Claude - Fixed to use proper KIfExpr instead of embedding raw syntax in KLiteral
            // Simplify borrow: if carry is literal true/false, just use 0 or 1
            // borrow = NOT carry, so: carry=true -> borrow=0, carry=false -> borrow=1
            val borrowInt: KotlinExpr = when {
                carryIn is KLiteral && carryIn.value in listOf("1", "true") -> KLiteral("0")
                carryIn is KLiteral && carryIn.value in listOf("0", "false") -> KLiteral("1")
                else -> KIfExpr(carryIn, KLiteral("0"), KLiteral("1"))
            }

            // Full difference (may go negative)
            val fullDiff = KBinaryOp(KBinaryOp(a, "-", wrapPropertyRead(operand)), "-", borrowInt)

            // CRITICAL FIX: Store the difference in a temp variable so the carry check
            // is based on the value at the time of the SBC, not after A is reassigned.
            val diffVarName = ctx.nextTempVar()
            stmts.add(KVarDecl(diffVarName, "Int", fullDiff, mutable = false))
            val diffVar = KVar(diffVarName)

            // Result masked to 8 bits (now using the captured diff variable)
            val result = KBinaryOp(diffVar, "and", KLiteral("0xFF"))

            // CRITICAL FIX: If there's a function-level variable for A, we need to update it
            // so that stores after merged branches use the correct value.
            val funcLevelVar = ctx.getFunctionLevelVar("A")
            if (funcLevelVar != null) {
                stmts.add(KAssignment(funcLevelVar, result))
                ctx.registerA = funcLevelVar
            } else {
                ctx.registerA = result
            }

            // Carry set if no borrow (diff >= 0 before masking)
            ctx.carryFlag = KBinaryOp(diffVar, ">=", KLiteral("0"))
            ctx.zeroFlag = KBinaryOp(result, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(result, "and", KLiteral("0x80"))), "!=", KLiteral("0"))

            // by Claude - Overflow flag for SBC: set when subtracting creates unexpected sign change.
            // For SBC, V = (A ^ result) & (A ^ operand) & 0x80
            // This detects cases like: positive - negative = negative (should be positive)
            // or: negative - positive = positive (should be negative)
            val operandWrapped = wrapPropertyRead(operand)
            val aXorResult = KBinaryOp(a, "xor", result)
            val aXorOperand = KBinaryOp(a, "xor", operandWrapped)
            val overflowExpr = KBinaryOp(KParen(KBinaryOp(aXorResult, "and", aXorOperand)), "and", KLiteral("0x80"))
            ctx.overflowFlag = KBinaryOp(overflowExpr, "!=", KLiteral("0"))
        }

        AssemblyOp.INC -> {
            val target = this.address.toKotlinExpr(ctx)
            val readValue = wrapPropertyRead(target)
            val result = KBinaryOp(KParen(KBinaryOp(readValue, "+", KLiteral("1"))), "and", KLiteral("0xFF"))
            val wrappedResult = wrapPropertyWrite(target, result)
            stmts.add(KAssignment(target, wrappedResult))
            // CRITICAL FIX: Set flags based on fresh read of target (after assignment), not the result expression
            // The result expression contains `+1` which would be applied again when evaluating a loop condition
            val newValue = wrapPropertyRead(target)
            ctx.zeroFlag = KBinaryOp(newValue, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(newValue, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.DEC -> {
            val target = this.address.toKotlinExpr(ctx)
            val readValue = wrapPropertyRead(target)
            val result = KBinaryOp(KParen(KBinaryOp(readValue, "-", KLiteral("1"))), "and", KLiteral("0xFF"))
            val wrappedResult = wrapPropertyWrite(target, result)
            stmts.add(KAssignment(target, wrappedResult))
            // CRITICAL FIX: Set flags based on fresh read of target (after assignment), not the result expression
            // The result expression contains `-1` which would be applied again when evaluating a loop condition
            val newValue = wrapPropertyRead(target)
            ctx.zeroFlag = KBinaryOp(newValue, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(newValue, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.INX -> {
            var x = ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X")
            // If X is not a variable, materialize it first so we can update it
            if (x !is KVar) {
                stmts.addAll(ctx.materializeRegister("X", mutable = true))
                x = ctx.registerX as KVar
            }
            val result = KBinaryOp(KParen(KBinaryOp(x, "+", KLiteral("1"))), "and", KLiteral("0xFF"))
            stmts.add(KAssignment(x, result))
            ctx.zeroFlag = KBinaryOp(x, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(x, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.INY -> {
            var y = ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y")
            // If Y is not a variable, materialize it first so we can update it
            if (y !is KVar) {
                stmts.addAll(ctx.materializeRegister("Y", mutable = true))
                y = ctx.registerY as KVar
            }
            val result = KBinaryOp(KParen(KBinaryOp(y, "+", KLiteral("1"))), "and", KLiteral("0xFF"))
            stmts.add(KAssignment(y, result))
            ctx.zeroFlag = KBinaryOp(y, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(y, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.DEX -> {
            var x = ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X")
            // If X is not a variable, materialize it first so we can update it
            if (x !is KVar) {
                stmts.addAll(ctx.materializeRegister("X", mutable = true))
                x = ctx.registerX as KVar
            }
            val result = KBinaryOp(KParen(KBinaryOp(x, "-", KLiteral("1"))), "and", KLiteral("0xFF"))
            stmts.add(KAssignment(x, result))
            // Flags are set based on the NEW value (after decrement)
            ctx.zeroFlag = KBinaryOp(x, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(x, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.DEY -> {
            var y = ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y")
            // If Y is not a variable, materialize it first so we can update it
            if (y !is KVar) {
                stmts.addAll(ctx.materializeRegister("Y", mutable = true))
                y = ctx.registerY as KVar
            }
            val result = KBinaryOp(KParen(KBinaryOp(y, "-", KLiteral("1"))), "and", KLiteral("0xFF"))
            stmts.add(KAssignment(y, result))
            // Flags are set based on the NEW value (after decrement)
            ctx.zeroFlag = KBinaryOp(y, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(y, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        // ===========================
        // Logical instructions
        // All logical instructions set Z and N flags based on result
        // Emit explicit assignments to make data flow visible
        // ===========================
        // by Claude - Fixed AND/ORA/EOR to emit assignment statement like ADC/SBC do
        AssemblyOp.AND -> {
            val operand = wrapPropertyRead(this.address.toKotlinExpr(ctx))
            val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            val resultExpr = KBinaryOp(a, "and", operand)

            // CRITICAL FIX: If there's a function-level variable for A, we need to update it
            // so that stores after merged branches use the correct value.
            val funcLevelVar = ctx.getFunctionLevelVar("A")
            if (funcLevelVar != null) {
                stmts.add(KAssignment(funcLevelVar, resultExpr))
                ctx.registerA = funcLevelVar
                ctx.zeroFlag = KBinaryOp(funcLevelVar, "==", KLiteral("0"))
                ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(funcLevelVar, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            } else if (a is KVar) {
                // A is already a mutable variable, just assign to it
                stmts.add(KAssignment(a, resultExpr))
                ctx.registerA = a
                ctx.zeroFlag = KBinaryOp(a, "==", KLiteral("0"))
                ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(a, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            } else {
                // Use temp variable (for expressions)
                val varName = ctx.nextTempVar()
                stmts.add(KVarDecl(varName, "Int", resultExpr, mutable = false))
                val varRef = KVar(varName)
                ctx.registerA = varRef
                ctx.zeroFlag = KBinaryOp(varRef, "==", KLiteral("0"))
                ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(varRef, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            }
        }

        // by Claude - Fixed ORA to emit assignment statement
        AssemblyOp.ORA -> {
            val operand = wrapPropertyRead(this.address.toKotlinExpr(ctx))
            val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            val resultExpr = KBinaryOp(a, "or", operand)

            val funcLevelVar = ctx.getFunctionLevelVar("A")
            if (funcLevelVar != null) {
                stmts.add(KAssignment(funcLevelVar, resultExpr))
                ctx.registerA = funcLevelVar
                ctx.zeroFlag = KBinaryOp(funcLevelVar, "==", KLiteral("0"))
                ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(funcLevelVar, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            } else if (a is KVar) {
                stmts.add(KAssignment(a, resultExpr))
                ctx.registerA = a
                ctx.zeroFlag = KBinaryOp(a, "==", KLiteral("0"))
                ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(a, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            } else {
                val varName = ctx.nextTempVar()
                stmts.add(KVarDecl(varName, "Int", resultExpr, mutable = false))
                val varRef = KVar(varName)
                ctx.registerA = varRef
                ctx.zeroFlag = KBinaryOp(varRef, "==", KLiteral("0"))
                ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(varRef, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            }
        }

        // by Claude - Fixed EOR to emit assignment statement
        AssemblyOp.EOR -> {
            val operand = wrapPropertyRead(this.address.toKotlinExpr(ctx))
            val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            val resultExpr = KBinaryOp(a, "xor", operand)

            val funcLevelVar = ctx.getFunctionLevelVar("A")
            if (funcLevelVar != null) {
                stmts.add(KAssignment(funcLevelVar, resultExpr))
                ctx.registerA = funcLevelVar
                ctx.zeroFlag = KBinaryOp(funcLevelVar, "==", KLiteral("0"))
                ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(funcLevelVar, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            } else if (a is KVar) {
                stmts.add(KAssignment(a, resultExpr))
                ctx.registerA = a
                ctx.zeroFlag = KBinaryOp(a, "==", KLiteral("0"))
                ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(a, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            } else {
                val varName = ctx.nextTempVar()
                stmts.add(KVarDecl(varName, "Int", resultExpr, mutable = false))
                val varRef = KVar(varName)
                ctx.registerA = varRef
                ctx.zeroFlag = KBinaryOp(varRef, "==", KLiteral("0"))
                ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(varRef, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            }
        }

        // ===========================
        // Shift instructions
        // ===========================
        AssemblyOp.ASL -> {
            // ASL shifts left, bit 7 goes into carry, Z/N updated from result
            if (this.address == null) {
                // Accumulator mode
                val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")

                // by Claude - CRITICAL FIX: Capture original value BEFORE the shift
                // The carry flag must test bit 7 of the ORIGINAL value, not the shifted result.
                // If `a` is a KVar (mutable), we must capture it to avoid the stale reference bug.
                val originalValue: KotlinExpr
                if (a is KVar) {
                    // Capture the original value into a temp variable
                    val origVarName = "orig${ctx.nextFlagVar()}"
                    stmts.add(KVarDecl(origVarName, "Int", a, mutable = false))
                    originalValue = KVar(origVarName)
                } else {
                    // Expression is immutable, safe to reference directly
                    originalValue = a
                }

                // Carry flag = bit 7 of ORIGINAL value (shifted out)
                ctx.carryFlag = KBinaryOp(KParen(KBinaryOp(originalValue, "and", KLiteral("0x80"))), "!=", KLiteral("0"))

                // Compute the shift result using the original value
                val result = KBinaryOp(KParen(KBinaryOp(originalValue, "shl", KLiteral("1"))), "and", KLiteral("0xFF"))

                // If A is a KVar (mutable variable), emit assignment; otherwise track expression
                if (a is KVar) {
                    stmts.add(KAssignment(a, result))
                } else {
                    ctx.registerA = result
                }

                // Flags are based on the result, not the original
                ctx.zeroFlag = KBinaryOp(result, "==", KLiteral("0"))
                ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(result, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            } else {
                // by Claude - Memory mode ASL
                val target = this.address.toKotlinExpr(ctx)
                val readValue = wrapPropertyRead(target)

                // by Claude - CRITICAL FIX: Capture original value BEFORE the shift
                // The carry flag must test bit 7 of the ORIGINAL value, not the shifted result.
                //
                // We use TWO temp variables:
                // 1. origN: captures the original Int value (for use in shift calculation)
                // 2. carryFromAslN: captures the carry decision as a boolean
                //
                // The carry flag expression uses the carryFromAslN variable, which ensures:
                // - For ASL/ROL sequences: The carry boolean is pre-computed BEFORE the memory
                //   is modified, so ROL gets the correct carry value.
                // - For control flow: The carry boolean variable name can be referenced in loop
                //   conditions. If control flow creates a loop where this variable is out of
                //   scope, it's a control flow analysis issue, not an instruction handler issue.
                val originalValue: KotlinExpr
                if (readValue is KVar) {
                    // Capture the original value into a temp variable
                    val origVarName = "orig${ctx.nextFlagVar()}"
                    stmts.add(KVarDecl(origVarName, "Int", readValue, mutable = false))
                    originalValue = KVar(origVarName)

                    // Capture the carry decision as a boolean variable
                    val carryVarName = "carryFromAsl${ctx.nextFlagVar()}"
                    val carryExpr = KBinaryOp(KParen(KBinaryOp(originalValue, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
                    stmts.add(KVarDecl(carryVarName, "Boolean", carryExpr, mutable = false))
                    ctx.carryFlag = KVar(carryVarName)
                } else {
                    // Expression is immutable, safe to reference directly
                    originalValue = readValue
                    ctx.carryFlag = KBinaryOp(KParen(KBinaryOp(originalValue, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
                }

                val shifted = KBinaryOp(KParen(KBinaryOp(originalValue, "shl", KLiteral("1"))), "and", KLiteral("0xFF"))
                val wrappedShifted = wrapPropertyWrite(target, shifted)
                stmts.add(KAssignment(target, wrappedShifted))
                ctx.zeroFlag = KBinaryOp(shifted, "==", KLiteral("0"))
                ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(shifted, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            }
        }

        AssemblyOp.LSR -> {
            // LSR shifts right, bit 0 goes into carry, Z/N updated from result
            if (this.address == null) {
                // Accumulator mode
                val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")

                // by Claude - CRITICAL FIX: Capture original value BEFORE the shift
                val originalValue: KotlinExpr
                if (a is KVar) {
                    val origVarName = "orig${ctx.nextFlagVar()}"
                    stmts.add(KVarDecl(origVarName, "Int", a, mutable = false))
                    originalValue = KVar(origVarName)
                } else {
                    originalValue = a
                }

                // Carry flag = bit 0 of ORIGINAL value (shifted out)
                ctx.carryFlag = KBinaryOp(KParen(KBinaryOp(originalValue, "and", KLiteral("0x01"))), "!=", KLiteral("0"))
                val result = KBinaryOp(originalValue, "shr", KLiteral("1"))

                // If A is a KVar (mutable variable), emit assignment; otherwise track expression
                if (a is KVar) {
                    stmts.add(KAssignment(a, result))
                } else {
                    ctx.registerA = result
                }
                ctx.zeroFlag = KBinaryOp(result, "==", KLiteral("0"))
                ctx.negativeFlag = KLiteral("false")  // LSR always clears N (bit 7 becomes 0)
            } else {
                // by Claude - Memory mode LSR
                val target = this.address.toKotlinExpr(ctx)
                val readValue = wrapPropertyRead(target)

                // by Claude - CRITICAL FIX: Capture original value BEFORE the shift
                // The carry flag must test bit 0 of the ORIGINAL value, not the shifted result.
                // See ASL memory mode handler for detailed explanation.
                val originalValue: KotlinExpr
                if (readValue is KVar) {
                    // Capture the original value into a temp variable
                    val origVarName = "orig${ctx.nextFlagVar()}"
                    stmts.add(KVarDecl(origVarName, "Int", readValue, mutable = false))
                    originalValue = KVar(origVarName)

                    // Capture the carry decision as a boolean variable
                    val carryVarName = "carryFromLsr${ctx.nextFlagVar()}"
                    val carryExpr = KBinaryOp(KParen(KBinaryOp(originalValue, "and", KLiteral("0x01"))), "!=", KLiteral("0"))
                    stmts.add(KVarDecl(carryVarName, "Boolean", carryExpr, mutable = false))
                    ctx.carryFlag = KVar(carryVarName)
                } else {
                    // Expression is immutable, safe to reference directly
                    originalValue = readValue
                    ctx.carryFlag = KBinaryOp(KParen(KBinaryOp(originalValue, "and", KLiteral("0x01"))), "!=", KLiteral("0"))
                }

                val shifted = KBinaryOp(originalValue, "shr", KLiteral("1"))
                val wrappedShifted = wrapPropertyWrite(target, shifted)
                stmts.add(KAssignment(target, wrappedShifted))
                ctx.zeroFlag = KBinaryOp(shifted, "==", KLiteral("0"))
                ctx.negativeFlag = KLiteral("false")  // LSR always clears N
            }
        }

        AssemblyOp.ROL -> {
            // ROL rotates left through carry: old bit 7 -> carry, old carry -> bit 0
            if (this.address == null) {
                // Accumulator mode
                val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
                val oldCarry = ctx.carryFlag ?: KVar("flagC")

                // by Claude - CRITICAL FIX: Capture original value BEFORE the rotate
                val originalValue: KotlinExpr
                if (a is KVar) {
                    val origVarName = "orig${ctx.nextFlagVar()}"
                    stmts.add(KVarDecl(origVarName, "Int", a, mutable = false))
                    originalValue = KVar(origVarName)
                } else {
                    originalValue = a
                }

                // New carry = old bit 7 of ORIGINAL value
                ctx.carryFlag = KBinaryOp(KParen(KBinaryOp(originalValue, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
                // Result = (a << 1) | old_carry, masked to 8 bits
                val shiftedPart = KBinaryOp(KParen(KBinaryOp(originalValue, "shl", KLiteral("1"))), "and", KLiteral("0xFE"))
                val carryBit = KIfExpr(oldCarry, KLiteral("1"), KLiteral("0"))
                val result = KBinaryOp(shiftedPart, "or", carryBit)

                // If A is a KVar (mutable variable), emit assignment; otherwise track expression
                if (a is KVar) {
                    stmts.add(KAssignment(a, result))
                } else {
                    ctx.registerA = result
                }
                ctx.zeroFlag = KBinaryOp(result, "==", KLiteral("0"))
                ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(result, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            } else {
                val target = this.address.toKotlinExpr(ctx)
                val readValue = wrapPropertyRead(target)
                val oldCarry = ctx.carryFlag ?: KVar("flagC")
                ctx.carryFlag = KBinaryOp(KParen(KBinaryOp(readValue, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
                val shiftedPart = KBinaryOp(KParen(KBinaryOp(readValue, "shl", KLiteral("1"))), "and", KLiteral("0xFE"))
                val carryBit = KIfExpr(oldCarry, KLiteral("1"), KLiteral("0"))
                val result = KBinaryOp(shiftedPart, "or", carryBit)
                val wrappedResult = wrapPropertyWrite(target, result)
                stmts.add(KAssignment(target, wrappedResult))
                ctx.zeroFlag = KBinaryOp(result, "==", KLiteral("0"))
                ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(result, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            }
        }

        AssemblyOp.ROR -> {
            // ROR rotates right through carry: old bit 0 -> carry, old carry -> bit 7
            if (this.address == null) {
                // Accumulator mode
                val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
                val oldCarry = ctx.carryFlag ?: KVar("flagC")

                // by Claude - CRITICAL FIX: Capture original value BEFORE the rotate
                val originalValue: KotlinExpr
                if (a is KVar) {
                    val origVarName = "orig${ctx.nextFlagVar()}"
                    stmts.add(KVarDecl(origVarName, "Int", a, mutable = false))
                    originalValue = KVar(origVarName)
                } else {
                    originalValue = a
                }

                // New carry = old bit 0 of ORIGINAL value
                ctx.carryFlag = KBinaryOp(KParen(KBinaryOp(originalValue, "and", KLiteral("0x01"))), "!=", KLiteral("0"))
                // Result = (a >> 1) | (old_carry << 7)
                val shiftedPart = KBinaryOp(originalValue, "shr", KLiteral("1"))
                val carryBit = KIfExpr(oldCarry, KLiteral("0x80"), KLiteral("0"))
                val result = KBinaryOp(shiftedPart, "or", carryBit)

                // If A is a KVar (mutable variable), emit assignment; otherwise track expression
                if (a is KVar) {
                    stmts.add(KAssignment(a, result))
                } else {
                    ctx.registerA = result
                }
                ctx.zeroFlag = KBinaryOp(result, "==", KLiteral("0"))
                ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(result, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            } else {
                val target = this.address.toKotlinExpr(ctx)
                val readValue = wrapPropertyRead(target)
                val oldCarry = ctx.carryFlag ?: KVar("flagC")
                ctx.carryFlag = KBinaryOp(KParen(KBinaryOp(readValue, "and", KLiteral("0x01"))), "!=", KLiteral("0"))
                val shiftedPart = KBinaryOp(readValue, "shr", KLiteral("1"))
                val carryBit = KIfExpr(oldCarry, KLiteral("0x80"), KLiteral("0"))
                val result = KBinaryOp(shiftedPart, "or", carryBit)
                val wrappedResult = wrapPropertyWrite(target, result)
                stmts.add(KAssignment(target, wrappedResult))
                ctx.zeroFlag = KBinaryOp(result, "==", KLiteral("0"))
                ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(result, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            }
        }

        // ===========================
        // Compare instructions
        // ===========================
        // by Claude - Compare instructions set Z, C, N flags based on (reg - operand)
        // Z = 1 if result == 0 (reg == operand)
        // C = 1 if reg >= operand (no borrow)
        // N = 1 if bit 7 of (reg - operand) & 0xFF is set
        AssemblyOp.CMP -> {
            val operand = wrapPropertyRead(this.address.toKotlinExpr(ctx))
            val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            val diff = KBinaryOp(KParen(KBinaryOp(a, "-", operand)), "and", KLiteral("0xFF"))
            ctx.zeroFlag = KBinaryOp(a, "==", operand)
            ctx.carryFlag = KBinaryOp(a, ">=", operand)
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(diff, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.CPX -> {
            val operand = wrapPropertyRead(this.address.toKotlinExpr(ctx))
            val x = ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X")
            val diff = KBinaryOp(KParen(KBinaryOp(x, "-", operand)), "and", KLiteral("0xFF"))
            ctx.zeroFlag = KBinaryOp(x, "==", operand)
            ctx.carryFlag = KBinaryOp(x, ">=", operand)
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(diff, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.CPY -> {
            val operand = wrapPropertyRead(this.address.toKotlinExpr(ctx))
            val y = ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y")
            val diff = KBinaryOp(KParen(KBinaryOp(y, "-", operand)), "and", KLiteral("0xFF"))
            ctx.zeroFlag = KBinaryOp(y, "==", operand)
            ctx.carryFlag = KBinaryOp(y, ">=", operand)
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(diff, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        // ===========================
        // Branch instructions (handled by control flow)
        // ===========================
        AssemblyOp.BEQ, AssemblyOp.BNE, AssemblyOp.BCC, AssemblyOp.BCS,
        AssemblyOp.BMI, AssemblyOp.BPL, AssemblyOp.BVC, AssemblyOp.BVS -> {
            // These are handled by control flow conversion
        }

        // ===========================
        // Jump/subroutine instructions
        // ===========================
        AssemblyOp.JMP -> {
            // by Claude - Bug #1 fix: Generate tail call to target function when JMP points to a known function
            // This handles cases like `jmp ChkContinue` where ChkContinue is a separate function
            val addr = this.address
            if (addr is AssemblyAddressing.Direct) {
                val targetLabel = addr.label
                val targetName = assemblyLabelToKotlinName(targetLabel)
                val targetFunction = ctx.functionRegistry[targetName]
                val currentFunctionName = ctx.currentFunction?.let { assemblyLabelToKotlinName(it.startingBlock.label ?: "") }

                // by Claude - Bug #7 fix: Don't generate tail call to self (would cause infinite recursion)
                // This happens when SetAttrib is both an internal label AND a registered function
                if (targetFunction != null && targetName != currentFunctionName) {
                    // Generate tail call to the target function
                    // by Claude - Use getRegisterValueOrDefault to avoid unresolved reference errors
                    val args = mutableListOf<KotlinExpr>()
                    if (targetFunction.inputs?.contains(TrackedAsIo.A) == true) {
                        args.add(getRegisterValueOrDefault("A", ctx))
                    }
                    if (targetFunction.inputs?.contains(TrackedAsIo.X) == true) {
                        args.add(getRegisterValueOrDefault("X", ctx))
                    }
                    if (targetFunction.inputs?.contains(TrackedAsIo.Y) == true) {
                        args.add(getRegisterValueOrDefault("Y", ctx))
                    }

                    // by Claude - CRITICAL FIX: Propagate return value from JMP target
                    // If the current function outputs A and the target function also outputs A,
                    // we should use `return targetFunction(args)` instead of calling and returning separately.
                    // This ensures the tail call properly returns the target's result.
                    val currentFunc = ctx.currentFunction
                    val currentOutputsA = currentFunc?.outputs?.contains(TrackedAsIo.A) == true
                    val targetOutputsA = targetFunction.outputs?.contains(TrackedAsIo.A) == true

                    if (currentOutputsA && targetOutputsA) {
                        // Both functions output A - use return with call
                        stmts.add(KReturn(value = KCall(targetName, args)))
                    } else {
                        // No return value to propagate
                        stmts.add(KExprStmt(KCall(targetName, args)))
                        stmts.add(KReturn())
                    }
                } else if (targetFunction == null) {
                    // Target is not a known function - add comment for debugging
                    stmts.add(KComment("jmp $targetLabel (not a known function)", commentTypeIndicator = ">"))
                }
                // else: targetName == currentFunctionName, this is an internal goto - control flow handles it
            } else if (addr is AssemblyAddressing.IndirectAbsolute) {
                // Indirect JMP - can't resolve at compile time
                stmts.add(KComment("jmp (${addr.label}) - indirect jump", commentTypeIndicator = ">"))
            }
            // Note: For JMPs within the same function (loops, gotos), control flow analysis handles them
        }

        AssemblyOp.JSR -> {
            // Get the label name directly without converting to memory access
            val assemblyLabel = when (val addr = this.address) {
                is AssemblyAddressing.Direct -> addr.label
                is AssemblyAddressing.IndirectAbsolute -> addr.label
                else -> this.address.toKotlinExpr(ctx).toKotlin()
            }

            // Check if this is a JumpEngine call with a known dispatch table
            val jumpTable = ctx.jumpEngineTables[lineIndex]
            if (assemblyLabel == "JumpEngine" && jumpTable != null) {
                // Generate a when() statement that dispatches based on A register
                val indexExpr = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")

                // by Claude - Use getRegisterValueOrDefault to avoid unresolved reference errors
                val branches = jumpTable.targets.mapIndexed { index, targetLabel ->
                    val targetFunctionName = assemblyLabelToKotlinName(targetLabel)
                    val targetFunction = ctx.functionRegistry[targetFunctionName]

                    // Build argument list for target function
                    // For JumpEngine dispatch, A contains the index value
                    val args = mutableListOf<KotlinExpr>()
                    if (targetFunction?.inputs != null) {
                        if (TrackedAsIo.A in targetFunction.inputs!!) {
                            // Pass the index value as A (since A holds the dispatch index)
                            args.add(KLiteral("$index"))
                        }
                        if (TrackedAsIo.X in targetFunction.inputs!!) {
                            args.add(getRegisterValueOrDefault("X", ctx))
                        }
                        if (TrackedAsIo.Y in targetFunction.inputs!!) {
                            args.add(getRegisterValueOrDefault("Y", ctx))
                        }
                    }

                    KWhenBranch(
                        values = listOf(KLiteral("$index")),
                        body = listOf(KExprStmt(KCall(targetFunctionName, args)))
                    )
                }

                stmts.add(KWhen(
                    subject = indexExpr,
                    branches = branches,
                    elseBranch = listOf(KComment("Unknown JumpEngine index"))
                ))
                stmts.add(KReturn())
            } else {
                val functionName = assemblyLabelToKotlinName(assemblyLabel)

                // Look up the target function to determine what parameters to pass
                val targetFunction = ctx.functionRegistry[functionName]

                // Build argument list based on target function's inputs
                // by Claude - Use getRegisterValueOrDefault to avoid unresolved reference errors
                val args = mutableListOf<KotlinExpr>()
                if (targetFunction?.inputs != null) {
                    if (TrackedAsIo.A in targetFunction.inputs!!) {
                        args.add(getRegisterValueOrDefault("A", ctx))
                    }
                    if (TrackedAsIo.X in targetFunction.inputs!!) {
                        args.add(getRegisterValueOrDefault("X", ctx))
                    }
                    if (TrackedAsIo.Y in targetFunction.inputs!!) {
                        args.add(getRegisterValueOrDefault("Y", ctx))
                    }
                }

                // by Claude - Capture register outputs from functions that return values
                val hasAOutput = targetFunction?.outputs?.contains(TrackedAsIo.A) == true
                val hasXOutput = targetFunction?.outputs?.contains(TrackedAsIo.X) == true
                val hasYOutput = targetFunction?.outputs?.contains(TrackedAsIo.Y) == true
                // by Claude - Bug #2 fix: detect carry flag as boolean return
                val hasCarryOutput = targetFunction?.outputs?.contains(TrackedAsIo.CarryFlag) == true
                // by Claude - Count register outputs to determine how many values are returned
                val outputRegisterCount = listOf(hasAOutput, hasXOutput, hasYOutput).count { it }

                when {
                    outputRegisterCount >= 2 -> {
                        // by Claude - Function returns Pair<Int, Int> - capture both
                        // Determine which two registers are being returned
                        val pairVar = ctx.nextPairVar()
                        stmts.add(KVarDecl(pairVar, mutable = false, value = KCall(functionName, args)))

                        // First element of pair
                        val firstVar = ctx.nextTempVar()
                        stmts.add(KVarDecl(firstVar, mutable = true, value = KMemberAccess(KVar(pairVar), KVar("first"))))
                        when {
                            hasAOutput -> ctx.registerA = KVar(firstVar)
                            hasXOutput -> ctx.registerX = KVar(firstVar)
                            else -> ctx.registerY = KVar(firstVar)
                        }

                        // Second element of pair
                        val secondVar = ctx.nextTempVar()
                        stmts.add(KVarDecl(secondVar, mutable = true, value = KMemberAccess(KVar(pairVar), KVar("second"))))
                        when {
                            hasYOutput -> ctx.registerY = KVar(secondVar)
                            hasXOutput && !hasAOutput -> ctx.registerX = KVar(secondVar)
                            else -> ctx.registerY = KVar(secondVar)
                        }

                        // by Claude - CRITICAL: Also update register variables directly
                        // This is needed because ctx tracking doesn't persist across control flow boundaries
                        if (hasXOutput) {
                            val xTempVar = if (hasAOutput) secondVar else firstVar
                            val (xVarName, _) = ctx.getOrCreateFunctionLevelVar("X")
                            stmts.add(KAssignment(KVar(xVarName), KVar(xTempVar)))
                        }
                    }
                    hasAOutput -> {
                        // by Claude - Function returns Int (A value) - capture it
                        val resultVar = ctx.nextTempVar()
                        stmts.add(KVarDecl(resultVar, mutable = true, value = KCall(functionName, args)))
                        ctx.registerA = KVar(resultVar)
                    }
                    hasXOutput -> {
                        // by Claude - Function returns Int (X value) - capture it
                        // Common pattern: functions like dividePDiff that return an index in X
                        val resultVar = ctx.nextTempVar()
                        stmts.add(KVarDecl(resultVar, mutable = true, value = KCall(functionName, args)))
                        ctx.registerX = KVar(resultVar)
                        // by Claude - CRITICAL: Also update the X variable directly
                        // This is needed because ctx.registerX tracking doesn't persist across
                        // control flow boundaries (if-else merge). When the X value is used later
                        // outside the if block, it needs to find the updated value in X.
                        val (xVarName, _) = ctx.getOrCreateFunctionLevelVar("X")
                        stmts.add(KAssignment(KVar(xVarName), KVar(resultVar)))
                    }
                    hasYOutput -> {
                        // by Claude - Function returns Int (Y value) - capture it
                        val resultVar = ctx.nextTempVar()
                        stmts.add(KVarDecl(resultVar, mutable = true, value = KCall(functionName, args)))
                        ctx.registerY = KVar(resultVar)
                    }
                    // by Claude - Bug #2 fix: capture carry flag boolean return
                    // by Claude - Bug fix: Capture the result in a variable to avoid calling the function multiple times
                    hasCarryOutput -> {
                        // Function returns Boolean (carry flag) - capture it in a variable
                        // The carry flag variable is then used by subsequent BCS/BCC conditions
                        val flagVar = "flag${ctx.nextFlagVar()}"
                        stmts.add(KVarDecl(flagVar, "Boolean", KCall(functionName, args), mutable = false))
                        ctx.carryFlag = KVar(flagVar)
                    }
                    else -> {
                        // Void function - just call it
                        stmts.add(KExprStmt(KCall(functionName, args)))
                    }
                }

                // Terminal subroutines like JumpEngine don't return - add a return statement
                if (isTerminalSubroutine(this)) {
                    stmts.add(KReturn())
                }
            }
        }

        // by Claude - RTS returns based on function output type
        AssemblyOp.RTS, AssemblyOp.RTI -> {
            val funcOutputs = ctx.currentFunction?.outputs
            val hasAOutput = funcOutputs?.contains(TrackedAsIo.A) == true
            val hasXOutput = funcOutputs?.contains(TrackedAsIo.X) == true
            val hasYOutput = funcOutputs?.contains(TrackedAsIo.Y) == true
            // by Claude - Bug #2 fix: detect carry flag as boolean return
            val hasCarryOutput = funcOutputs?.contains(TrackedAsIo.CarryFlag) == true
            // by Claude - Count register outputs
            val outputRegisterCount = listOf(hasAOutput, hasXOutput, hasYOutput).count { it }

            val returnValue = when {
                outputRegisterCount >= 2 -> {
                    // Return Pair for functions that output multiple registers
                    // Determine which registers to return based on priority: A > X > Y
                    // by Claude - Fixed: secondValue now correctly picks X when firstValue is A and X is an output
                    val firstReg = when {
                        hasAOutput -> "A"
                        hasXOutput -> "X"
                        else -> "Y"
                    }
                    val secondReg = when {
                        hasYOutput -> "Y"
                        hasXOutput && firstReg != "X" -> "X"  // X is output and wasn't used as first
                        else -> "Y" // fallback (shouldn't happen with correct outputs)
                    }
                    val firstValue = when (firstReg) {
                        "A" -> ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
                        "X" -> ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X")
                        else -> ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y")
                    }
                    val secondValue = when (secondReg) {
                        "A" -> ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
                        "X" -> ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X")
                        else -> ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y")
                    }
                    KCall("Pair", listOf(firstValue, secondValue))
                }
                hasAOutput -> {
                    // Return A value
                    ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
                }
                hasXOutput -> {
                    // by Claude - Return X value (e.g., dividePDiff returns index in X)
                    ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X")
                }
                hasYOutput -> {
                    // Return Y value
                    ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y")
                }
                // by Claude - Bug #2 fix: return carry flag as boolean
                hasCarryOutput -> {
                    // Return carry flag expression as boolean
                    // This is set by CMP, CPX, CPY, ADC, SBC, etc.
                    ctx.carryFlag ?: KLiteral("false")
                }
                else -> {
                    // Void function - return with no value (will be stripped by function generator)
                    null
                }
            }
            stmts.add(KReturn(returnValue))
        }

        // ===========================
        // Stack instructions
        // ===========================
        AssemblyOp.PHA -> {
            // Push A to stack
            val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            stmts.add(KExprStmt(KCall("push", listOf(a))))
        }

        AssemblyOp.PLA -> {
            // Pull A from stack and update Z/N flags
            val pullExpr = KCall("pull", emptyList())
            val existingVar = ctx.getFunctionLevelVar("A")
            if (existingVar != null) {
                stmts.add(KAssignment(existingVar, pullExpr))
                ctx.registerA = existingVar
            } else {
                val tempVar = ctx.nextTempVar()
                stmts.add(KVarDecl(tempVar, "Int", pullExpr, mutable = true))
                ctx.registerA = KVar(tempVar)
            }
            // PLA sets Z and N flags based on pulled value
            val a = ctx.registerA!!
            ctx.zeroFlag = KBinaryOp(a, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(a, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.PHP -> {
            // Push processor status - simplified, just call push with flags packed
            stmts.add(KComment("TODO: PHP - push processor status"))
        }

        AssemblyOp.PLP -> {
            // Pull processor status - simplified, just call pull and unpack flags
            stmts.add(KComment("TODO: PLP - pull processor status"))
        }

        // ===========================
        // Flag instructions
        // ===========================
        AssemblyOp.CLC -> {
            ctx.carryFlag = KLiteral("false")
        }

        AssemblyOp.SEC -> {
            ctx.carryFlag = KLiteral("true")
        }

        AssemblyOp.CLV -> {
            ctx.overflowFlag = KLiteral("0")
        }

        // ===========================
        // Test bits
        // ===========================
        AssemblyOp.BIT -> {
            val operand = wrapPropertyRead(this.address.toKotlinExpr(ctx))
            val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            // Z = (A AND memory) == 0
            ctx.zeroFlag = KBinaryOp(KParen(KBinaryOp(a, "and", operand)), "==", KLiteral("0"))
            // N = bit 7 of memory value
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(operand, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            // V = bit 6 of memory value
            ctx.overflowFlag = KBinaryOp(KParen(KBinaryOp(operand, "and", KLiteral("0x40"))), "!=", KLiteral("0"))
        }

        // ===========================
        // Other
        // ===========================
        AssemblyOp.NOP -> {
            // No operation
        }

        else -> {
            // Unhandled instruction - will still have assembly comment
        }
    }

    return stmts
}
