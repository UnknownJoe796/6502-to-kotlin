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
        AssemblyOp.LDA -> {
            val rawValue = this.address.toKotlinExpr(ctx)
            val value = wrapPropertyRead(rawValue)
            // If a function-level A var exists, assign to it; otherwise track expression
            val existingVar = ctx.getFunctionLevelVar("A")
            if (existingVar != null) {
                stmts.add(KAssignment(existingVar, value))
                ctx.registerA = existingVar
            } else {
                ctx.registerA = value
            }
            // LDA sets Z flag if value == 0, N flag if bit 7 is set
            val flagRef = ctx.registerA!!
            ctx.zeroFlag = KBinaryOp(flagRef, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(flagRef, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.LDX -> {
            val rawValue = this.address.toKotlinExpr(ctx)
            val value = wrapPropertyRead(rawValue)
            // If a function-level X var exists, assign to it; otherwise track expression
            val existingVar = ctx.getFunctionLevelVar("X")
            if (existingVar != null) {
                stmts.add(KAssignment(existingVar, value))
                ctx.registerX = existingVar
            } else {
                ctx.registerX = value
            }
            // LDX sets Z flag if value == 0, N flag if bit 7 is set
            val flagRef = ctx.registerX!!
            ctx.zeroFlag = KBinaryOp(flagRef, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(flagRef, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.LDY -> {
            val rawValue = this.address.toKotlinExpr(ctx)
            val value = wrapPropertyRead(rawValue)
            // If a function-level Y var exists, assign to it; otherwise track expression
            val existingVar = ctx.getFunctionLevelVar("Y")
            if (existingVar != null) {
                stmts.add(KAssignment(existingVar, value))
                ctx.registerY = existingVar
            } else {
                ctx.registerY = value
            }
            // LDY sets Z flag if value == 0, N flag if bit 7 is set
            val flagRef = ctx.registerY!!
            ctx.zeroFlag = KBinaryOp(flagRef, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(flagRef, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
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
        AssemblyOp.TAX -> {
            val value = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            ctx.registerX = value
            ctx.zeroFlag = KBinaryOp(value, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(value, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.TAY -> {
            val value = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            ctx.registerY = value
            ctx.zeroFlag = KBinaryOp(value, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(value, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.TXA -> {
            val value = ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X")
            ctx.registerA = value
            ctx.zeroFlag = KBinaryOp(value, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(value, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.TYA -> {
            val value = ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y")
            ctx.registerA = value
            ctx.zeroFlag = KBinaryOp(value, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(value, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        // ===========================
        // Arithmetic instructions
        // ===========================
        AssemblyOp.ADC -> {
            val operand = this.address.toKotlinExpr(ctx)
            val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            val carryIn = ctx.carryFlag ?: KLiteral("false")

            // Simplify carry to int: if carry is literal true/false, just use 1 or 0
            // Otherwise generate: if (carry) 1 else 0
            val carryInt: KotlinExpr = when {
                carryIn is KLiteral && carryIn.value in listOf("0", "false") -> KLiteral("0")
                carryIn is KLiteral && carryIn.value in listOf("1", "true") -> KLiteral("1")
                else -> KParen(KLiteral("if (${carryIn.toKotlin()}) 1 else 0"))
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
        }

        AssemblyOp.SBC -> {
            val operand = this.address.toKotlinExpr(ctx)
            val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            val carryIn = ctx.carryFlag ?: KLiteral("true")

            // Simplify borrow: if carry is literal true/false, just use 0 or 1
            // borrow = NOT carry, so: carry=true -> borrow=0, carry=false -> borrow=1
            val borrowInt: KotlinExpr = when {
                carryIn is KLiteral && carryIn.value in listOf("1", "true") -> KLiteral("0")
                carryIn is KLiteral && carryIn.value in listOf("0", "false") -> KLiteral("1")
                else -> KParen(KLiteral("if (${carryIn.toKotlin()}) 0 else 1"))
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
        AssemblyOp.AND -> {
            val operand = wrapPropertyRead(this.address.toKotlinExpr(ctx))
            val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            val resultExpr = KBinaryOp(a, "and", operand)
            // Emit assignment to make data flow visible
            val varName = ctx.nextTempVar()
            stmts.add(KVarDecl(varName, "Int", resultExpr, mutable = false))
            val varRef = KVar(varName)
            ctx.registerA = varRef
            ctx.zeroFlag = KBinaryOp(varRef, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(varRef, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.ORA -> {
            val operand = wrapPropertyRead(this.address.toKotlinExpr(ctx))
            val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            val resultExpr = KBinaryOp(a, "or", operand)
            val varName = ctx.nextTempVar()
            stmts.add(KVarDecl(varName, "Int", resultExpr, mutable = false))
            val varRef = KVar(varName)
            ctx.registerA = varRef
            ctx.zeroFlag = KBinaryOp(varRef, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(varRef, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        AssemblyOp.EOR -> {
            val operand = wrapPropertyRead(this.address.toKotlinExpr(ctx))
            val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
            val resultExpr = KBinaryOp(a, "xor", operand)
            val varName = ctx.nextTempVar()
            stmts.add(KVarDecl(varName, "Int", resultExpr, mutable = false))
            val varRef = KVar(varName)
            ctx.registerA = varRef
            ctx.zeroFlag = KBinaryOp(varRef, "==", KLiteral("0"))
            ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(varRef, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
        }

        // ===========================
        // Shift instructions
        // ===========================
        AssemblyOp.ASL -> {
            // ASL shifts left, bit 7 goes into carry, Z/N updated from result
            if (this.address == null) {
                // Accumulator mode
                val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")
                // Carry flag = bit 7 of original value (shifted out)
                ctx.carryFlag = KBinaryOp(KParen(KBinaryOp(a, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
                val result = KBinaryOp(KParen(KBinaryOp(a, "shl", KLiteral("1"))), "and", KLiteral("0xFF"))
                // If A is a KVar (mutable variable), emit assignment; otherwise track expression
                if (a is KVar) {
                    stmts.add(KAssignment(a, result))
                } else {
                    ctx.registerA = result
                }
                ctx.zeroFlag = KBinaryOp(a, "==", KLiteral("0"))
                ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(a, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
            } else {
                val target = this.address.toKotlinExpr(ctx)
                val readValue = wrapPropertyRead(target)
                // Carry flag = bit 7 of original value (shifted out)
                ctx.carryFlag = KBinaryOp(KParen(KBinaryOp(readValue, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
                val shifted = KBinaryOp(KParen(KBinaryOp(readValue, "shl", KLiteral("1"))), "and", KLiteral("0xFF"))
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
                // Carry flag = bit 0 of original value (shifted out)
                ctx.carryFlag = KBinaryOp(KParen(KBinaryOp(a, "and", KLiteral("0x01"))), "!=", KLiteral("0"))
                val result = KBinaryOp(a, "shr", KLiteral("1"))
                // If A is a KVar (mutable variable), emit assignment; otherwise track expression
                if (a is KVar) {
                    stmts.add(KAssignment(a, result))
                } else {
                    ctx.registerA = result
                }
                ctx.zeroFlag = KBinaryOp(a, "==", KLiteral("0"))
                ctx.negativeFlag = KLiteral("false")  // LSR always clears N (bit 7 becomes 0)
            } else {
                val target = this.address.toKotlinExpr(ctx)
                val readValue = wrapPropertyRead(target)
                // Carry flag = bit 0 of original value (shifted out)
                ctx.carryFlag = KBinaryOp(KParen(KBinaryOp(readValue, "and", KLiteral("0x01"))), "!=", KLiteral("0"))
                val shifted = KBinaryOp(readValue, "shr", KLiteral("1"))
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
                // New carry = old bit 7
                ctx.carryFlag = KBinaryOp(KParen(KBinaryOp(a, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
                // Result = (a << 1) | old_carry, masked to 8 bits
                val shiftedPart = KBinaryOp(KParen(KBinaryOp(a, "shl", KLiteral("1"))), "and", KLiteral("0xFE"))
                val carryBit = KIfExpr(oldCarry, KLiteral("1"), KLiteral("0"))
                val result = KBinaryOp(shiftedPart, "or", carryBit)
                // If A is a KVar (mutable variable), emit assignment; otherwise track expression
                if (a is KVar) {
                    stmts.add(KAssignment(a, result))
                } else {
                    ctx.registerA = result
                }
                ctx.zeroFlag = KBinaryOp(a, "==", KLiteral("0"))
                ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(a, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
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
                // New carry = old bit 0
                ctx.carryFlag = KBinaryOp(KParen(KBinaryOp(a, "and", KLiteral("0x01"))), "!=", KLiteral("0"))
                // Result = (a >> 1) | (old_carry << 7)
                val shiftedPart = KBinaryOp(a, "shr", KLiteral("1"))
                val carryBit = KIfExpr(oldCarry, KLiteral("0x80"), KLiteral("0"))
                val result = KBinaryOp(shiftedPart, "or", carryBit)
                // If A is a KVar (mutable variable), emit assignment; otherwise track expression
                if (a is KVar) {
                    stmts.add(KAssignment(a, result))
                } else {
                    ctx.registerA = result
                }
                ctx.zeroFlag = KBinaryOp(a, "==", KLiteral("0"))
                ctx.negativeFlag = KBinaryOp(KParen(KBinaryOp(a, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
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
        AssemblyOp.CMP -> {
            val operand = wrapPropertyRead(this.address.toKotlinExpr(ctx))
            val a = ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A")

            // Set flags based on comparison
            val diff = KBinaryOp(a, "-", operand)
            ctx.zeroFlag = KBinaryOp(diff, "==", KLiteral("0"))
            ctx.carryFlag = KBinaryOp(a, ">=", operand)
            ctx.negativeFlag = KBinaryOp(diff, "<", KLiteral("0"))
        }

        AssemblyOp.CPX -> {
            val operand = wrapPropertyRead(this.address.toKotlinExpr(ctx))
            val x = ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X")
            ctx.zeroFlag = KBinaryOp(x, "==", operand)
            ctx.carryFlag = KBinaryOp(x, ">=", operand)
        }

        AssemblyOp.CPY -> {
            val operand = wrapPropertyRead(this.address.toKotlinExpr(ctx))
            val y = ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y")
            ctx.zeroFlag = KBinaryOp(y, "==", operand)
            ctx.carryFlag = KBinaryOp(y, ">=", operand)
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
            // Handled by control flow
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
                            args.add(ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X"))
                        }
                        if (TrackedAsIo.Y in targetFunction.inputs!!) {
                            args.add(ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y"))
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
                val args = mutableListOf<KotlinExpr>()
                if (targetFunction?.inputs != null) {
                    if (TrackedAsIo.A in targetFunction.inputs!!) {
                        args.add(ctx.registerA ?: ctx.getFunctionLevelVar("A") ?: KVar("A"))
                    }
                    if (TrackedAsIo.X in targetFunction.inputs!!) {
                        args.add(ctx.registerX ?: ctx.getFunctionLevelVar("X") ?: KVar("X"))
                    }
                    if (TrackedAsIo.Y in targetFunction.inputs!!) {
                        args.add(ctx.registerY ?: ctx.getFunctionLevelVar("Y") ?: KVar("Y"))
                    }
                }

                stmts.add(KExprStmt(KCall(functionName, args)))

                // Terminal subroutines like JumpEngine don't return - add a return statement
                if (isTerminalSubroutine(this)) {
                    stmts.add(KReturn())
                }
            }
        }

        // by Claude - Fixed RTS to capture current A register value for return
        AssemblyOp.RTS, AssemblyOp.RTI -> {
            // Capture the current A register value as potential return value.
            // The function generator will use this value for A-returning functions,
            // or strip it for void functions.
            val returnValue = ctx.registerA ?: ctx.getFunctionLevelVar("A")
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
