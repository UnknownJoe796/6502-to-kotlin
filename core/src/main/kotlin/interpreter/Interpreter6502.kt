package com.ivieleague.decompiler6502tokotlin.interpreter

import com.ivieleague.decompiler6502tokotlin.hand.*

/**
 * 6502 Interpreter that executes assembly instructions.
 */
class Interpreter6502(
    val cpu: CPU6502 = CPU6502(),
    val memory: Memory6502 = Memory6502()
) {
    var halted: Boolean = false

    /**
     * Execute a single instruction
     */
    fun executeInstruction(instruction: AssemblyInstruction) {
        if (halted) return

        when (instruction.op) {
            // Load instructions
            AssemblyOp.LDA -> {
                cpu.A = readOperand(instruction.address)
                cpu.updateZN(cpu.A)
            }
            AssemblyOp.LDX -> {
                cpu.X = readOperand(instruction.address)
                cpu.updateZN(cpu.X)
            }
            AssemblyOp.LDY -> {
                cpu.Y = readOperand(instruction.address)
                cpu.updateZN(cpu.Y)
            }

            // Store instructions
            AssemblyOp.STA -> writeOperand(instruction.address!!, cpu.A)
            AssemblyOp.STX -> writeOperand(instruction.address!!, cpu.X)
            AssemblyOp.STY -> writeOperand(instruction.address!!, cpu.Y)

            // Transfer instructions
            AssemblyOp.TAX -> {
                cpu.X = cpu.A
                cpu.updateZN(cpu.X)
            }
            AssemblyOp.TAY -> {
                cpu.Y = cpu.A
                cpu.updateZN(cpu.Y)
            }
            AssemblyOp.TXA -> {
                cpu.A = cpu.X
                cpu.updateZN(cpu.A)
            }
            AssemblyOp.TYA -> {
                cpu.A = cpu.Y
                cpu.updateZN(cpu.A)
            }
            AssemblyOp.TSX -> {
                cpu.X = cpu.SP
                cpu.updateZN(cpu.X)
            }
            AssemblyOp.TXS -> {
                cpu.SP = cpu.X
            }

            // Stack instructions
            AssemblyOp.PHA -> pushByte(cpu.A)
            AssemblyOp.PHP -> pushByte(cpu.getStatusByte())
            AssemblyOp.PLA -> {
                cpu.A = pullByte()
                cpu.updateZN(cpu.A)
            }
            AssemblyOp.PLP -> cpu.setStatusByte(pullByte())

            // Logical instructions
            AssemblyOp.AND -> {
                cpu.A = (cpu.A.toInt() and readOperand(instruction.address).toInt()).toUByte()
                cpu.updateZN(cpu.A)
            }
            AssemblyOp.EOR -> {
                cpu.A = (cpu.A.toInt() xor readOperand(instruction.address).toInt()).toUByte()
                cpu.updateZN(cpu.A)
            }
            AssemblyOp.ORA -> {
                cpu.A = (cpu.A.toInt() or readOperand(instruction.address).toInt()).toUByte()
                cpu.updateZN(cpu.A)
            }
            AssemblyOp.BIT -> {
                val value = readOperand(instruction.address)
                cpu.Z = ((cpu.A.toInt() and value.toInt()) == 0)
                cpu.N = (value.toInt() and 0x80) != 0
                cpu.V = (value.toInt() and 0x40) != 0
            }

            // Arithmetic instructions
            AssemblyOp.ADC -> {
                val operand = readOperand(instruction.address)
                val result = cpu.A.toInt() + operand.toInt() + (if (cpu.C) 1 else 0)
                cpu.C = result > 0xFF
                val resultByte = (result and 0xFF).toUByte()
                // Overflow: result sign differs from both operands
                cpu.V = ((cpu.A.toInt() xor resultByte.toInt()) and 0x80) != 0 &&
                        ((cpu.A.toInt() xor operand.toInt()) and 0x80) == 0
                cpu.A = resultByte
                cpu.updateZN(cpu.A)
            }
            AssemblyOp.SBC -> {
                val operand = readOperand(instruction.address)
                val result = cpu.A.toInt() - operand.toInt() - (if (cpu.C) 0 else 1)
                cpu.C = result >= 0
                val resultByte = (result and 0xFF).toUByte()
                // Overflow: result sign differs from both operands
                cpu.V = ((cpu.A.toInt() xor resultByte.toInt()) and 0x80) != 0 &&
                        ((cpu.A.toInt() xor operand.toInt()) and 0x80) != 0
                cpu.A = resultByte
                cpu.updateZN(cpu.A)
            }

            // Compare instructions
            AssemblyOp.CMP -> compare(cpu.A, readOperand(instruction.address))
            AssemblyOp.CPX -> compare(cpu.X, readOperand(instruction.address))
            AssemblyOp.CPY -> compare(cpu.Y, readOperand(instruction.address))

            // Increment/Decrement instructions
            AssemblyOp.INC -> {
                val addr = getOperandAddress(instruction.address!!)
                val value = (memory.readByte(addr).toInt() + 1 and 0xFF).toUByte()
                memory.writeByte(addr, value)
                cpu.updateZN(value)
            }
            AssemblyOp.INX -> {
                cpu.X = (cpu.X.toInt() + 1 and 0xFF).toUByte()
                cpu.updateZN(cpu.X)
            }
            AssemblyOp.INY -> {
                cpu.Y = (cpu.Y.toInt() + 1 and 0xFF).toUByte()
                cpu.updateZN(cpu.Y)
            }
            AssemblyOp.DEC -> {
                val addr = getOperandAddress(instruction.address!!)
                val value = (memory.readByte(addr).toInt() - 1 and 0xFF).toUByte()
                memory.writeByte(addr, value)
                cpu.updateZN(value)
            }
            AssemblyOp.DEX -> {
                cpu.X = (cpu.X.toInt() - 1 and 0xFF).toUByte()
                cpu.updateZN(cpu.X)
            }
            AssemblyOp.DEY -> {
                cpu.Y = (cpu.Y.toInt() - 1 and 0xFF).toUByte()
                cpu.updateZN(cpu.Y)
            }

            // Shift/Rotate instructions
            AssemblyOp.ASL -> {
                val (addr, value) = getOperandForShift(instruction.address)
                cpu.C = (value.toInt() and 0x80) != 0
                val result = ((value.toInt() shl 1) and 0xFF).toUByte()
                writeShiftResult(addr, result)
                cpu.updateZN(result)
            }
            AssemblyOp.LSR -> {
                val (addr, value) = getOperandForShift(instruction.address)
                cpu.C = (value.toInt() and 0x01) != 0
                val result = (value.toInt() ushr 1).toUByte()
                writeShiftResult(addr, result)
                cpu.updateZN(result)
            }
            AssemblyOp.ROL -> {
                val (addr, value) = getOperandForShift(instruction.address)
                val oldCarry = if (cpu.C) 1 else 0
                cpu.C = (value.toInt() and 0x80) != 0
                val result = (((value.toInt() shl 1) or oldCarry) and 0xFF).toUByte()
                writeShiftResult(addr, result)
                cpu.updateZN(result)
            }
            AssemblyOp.ROR -> {
                val (addr, value) = getOperandForShift(instruction.address)
                val oldCarry = if (cpu.C) 0x80 else 0
                cpu.C = (value.toInt() and 0x01) != 0
                val result = ((value.toInt() ushr 1) or oldCarry).toUByte()
                writeShiftResult(addr, result)
                cpu.updateZN(result)
            }

            // Branch instructions (handled separately, not updating PC here)
            AssemblyOp.BCC, AssemblyOp.BCS, AssemblyOp.BEQ, AssemblyOp.BMI,
            AssemblyOp.BNE, AssemblyOp.BPL, AssemblyOp.BVC, AssemblyOp.BVS -> {
                // Branch handling is done externally by checking shouldBranch()
            }

            // Jump instructions
            AssemblyOp.JMP -> {
                // JMP handling is done externally
            }
            AssemblyOp.JSR -> {
                // JSR handling is done externally
            }
            AssemblyOp.RTS -> {
                // RTS handling is done externally
            }

            // Flag instructions
            AssemblyOp.CLC -> cpu.C = false
            AssemblyOp.CLD -> cpu.D = false
            AssemblyOp.CLI -> cpu.I = false
            AssemblyOp.CLV -> cpu.V = false
            AssemblyOp.SEC -> cpu.C = true
            AssemblyOp.SED -> cpu.D = true
            AssemblyOp.SEI -> cpu.I = true

            // Other instructions
            AssemblyOp.BRK -> halted = true
            AssemblyOp.NOP -> { /* Do nothing */ }
            AssemblyOp.RTI -> {
                // RTI handling is done externally
            }
        }
    }

    /**
     * Check if a branch instruction should be taken
     */
    fun shouldBranch(instruction: AssemblyInstruction): Boolean {
        return when (instruction.op) {
            AssemblyOp.BCC -> !cpu.C
            AssemblyOp.BCS -> cpu.C
            AssemblyOp.BEQ -> cpu.Z
            AssemblyOp.BMI -> cpu.N
            AssemblyOp.BNE -> !cpu.Z
            AssemblyOp.BPL -> !cpu.N
            AssemblyOp.BVC -> !cpu.V
            AssemblyOp.BVS -> cpu.V
            else -> false
        }
    }

    /**
     * Read an operand value based on addressing mode
     */
    private fun readOperand(addressing: AssemblyAddressing?): UByte {
        return when (addressing) {
            is AssemblyAddressing.ByteValue -> addressing.value
            is AssemblyAddressing.ShortValue -> (addressing.value.toInt() and 0xFF).toUByte()
            is AssemblyAddressing.ValueLowerSelection ->
                ((addressing.value.value.toInt() and 0xFF).toUByte())
            is AssemblyAddressing.ValueUpperSelection ->
                ((addressing.value.value.toInt() shr 8) and 0xFF).toUByte()
            is AssemblyAddressing.ConstantReference -> {
                // Resolve the constant name to its value using labelResolver
                val value = resolveLabel(addressing.name)
                (value and 0xFF).toUByte()
            }
            // by Claude - Added handlers for constant reference hi/lo selection
            is AssemblyAddressing.ConstantReferenceLower -> {
                val value = resolveLabel(addressing.name)
                (value and 0xFF).toUByte()
            }
            is AssemblyAddressing.ConstantReferenceUpper -> {
                val value = resolveLabel(addressing.name)
                ((value shr 8) and 0xFF).toUByte()
            }
            else -> {
                val addr = getOperandAddress(addressing!!)
                memory.readByte(addr)
            }
        }
    }

    /**
     * Write an operand value to memory based on addressing mode
     */
    private fun writeOperand(addressing: AssemblyAddressing, value: UByte) {
        val addr = getOperandAddress(addressing)
        memory.writeByte(addr, value)
    }

    /**
     * Get the effective address for an operand
     */
    private fun getOperandAddress(addressing: AssemblyAddressing): Int {
        return when (addressing) {
            is AssemblyAddressing.Direct -> resolveLabel(addressing.label) + addressing.offset
            is AssemblyAddressing.DirectX -> resolveLabel(addressing.label) + addressing.offset + cpu.X.toInt()
            is AssemblyAddressing.DirectY -> resolveLabel(addressing.label) + addressing.offset + cpu.Y.toInt()
            is AssemblyAddressing.IndirectX -> {
                val baseAddr = (resolveLabel(addressing.label) + addressing.offset + cpu.X.toInt()) and 0xFF
                memory.readWord(baseAddr).toInt()
            }
            is AssemblyAddressing.IndirectY -> {
                val baseAddr = (resolveLabel(addressing.label) + addressing.offset) and 0xFF
                val indirectAddr = memory.readWord(baseAddr).toInt()
                (indirectAddr + cpu.Y.toInt()) and 0xFFFF
            }
            is AssemblyAddressing.IndirectAbsolute -> {
                val addr = resolveLabel(addressing.label) + addressing.offset
                memory.readWord(addr).toInt()
            }
            else -> throw IllegalArgumentException("Cannot get address for immediate addressing mode")
        }
    }

    /**
     * Get operand for shift/rotate instructions (which can operate on A or memory)
     */
    private fun getOperandForShift(addressing: AssemblyAddressing?): Pair<Int?, UByte> {
        return if (addressing == null) {
            // Accumulator mode
            null to cpu.A
        } else {
            // Memory mode
            val addr = getOperandAddress(addressing)
            addr to memory.readByte(addr)
        }
    }

    /**
     * Write result of shift/rotate instruction
     */
    private fun writeShiftResult(address: Int?, value: UByte) {
        if (address == null) {
            cpu.A = value
        } else {
            memory.writeByte(address, value)
        }
    }

    /**
     * Compare two values and set flags
     */
    private fun compare(register: UByte, operand: UByte) {
        val result = register.toInt() - operand.toInt()
        cpu.C = result >= 0
        cpu.Z = result == 0
        cpu.N = (result and 0x80) != 0
    }

    /**
     * Push a byte onto the stack
     */
    private fun pushByte(value: UByte) {
        memory.writeByte(CPU6502.STACK_BASE + cpu.SP.toInt(), value)
        cpu.SP = (cpu.SP.toInt() - 1 and 0xFF).toUByte()
    }

    /**
     * Pull a byte from the stack
     */
    private fun pullByte(): UByte {
        cpu.SP = (cpu.SP.toInt() + 1 and 0xFF).toUByte()
        return memory.readByte(CPU6502.STACK_BASE + cpu.SP.toInt())
    }

    /**
     * Push a word onto the stack
     */
    fun pushWord(value: UShort) {
        pushByte(((value.toInt() shr 8) and 0xFF).toUByte())
        pushByte((value.toInt() and 0xFF).toUByte())
    }

    /**
     * Pull a word from the stack
     */
    fun pullWord(): UShort {
        val lo = pullByte().toInt()
        val hi = pullByte().toInt()
        return ((hi shl 8) or lo).toUShort()
    }

    /**
     * Resolve a label to an address (can be overridden for symbol table support)
     */
    var labelResolver: ((String) -> Int)? = null

    private fun resolveLabel(label: String): Int {
        // Try to parse as a hex number first
        if (label.startsWith("$")) {
            return label.substring(1).toInt(16)
        }
        // Use custom resolver if available (check before parsing as decimal)
        labelResolver?.invoke(label)?.let { return it }
        // Try to parse as a decimal number
        if (label.all { it.isDigit() }) {
            return label.toInt()
        }
        // Failed to resolve
        throw IllegalArgumentException("Cannot resolve label: $label")
    }

    /**
     * Reset the interpreter state
     */
    fun reset() {
        cpu.reset()
        memory.reset()
        halted = false
    }
}
