@file:OptIn(ExperimentalUnsignedTypes::class)

package com.ivieleague.decompiler6502tokotlin.testgen

import com.ivieleague.decompiler6502tokotlin.hand.AssemblyAddressing
import com.ivieleague.decompiler6502tokotlin.hand.parseToAssemblyCodeFile
import java.io.File
import kotlin.test.Test

/**
 * by Claude - Debug test to find where address calculation errors accumulate.
 */
class AddressCalculationDebugTest {

    @Test
    fun findWhereAddressDriftStarts() {
        val asmFile = File("smbdism.asm")
        val romFile = File("local/roms/smb.nes")
        if (!romFile.exists()) {
            println("ROM file not found, skipping")
            return
        }

        val mapper = AddressLabelMapper.fromAssemblyFile(asmFile)
        val romData = romFile.readBytes()
        val prgOffset = 16 // iNES header

        println("=== Finding Where Address Drift Starts ===\n")

        // Get all addresses in order
        val addresses = mapper.getAllAddresses().sorted()

        var lastCorrectAddr = 0
        var lastCorrectLabel = "START"
        var driftDetected = false

        for (addr in addresses) {
            if (addr < 0x8000 || addr >= 0x10000) continue // Skip non-PRG addresses

            val label = mapper.getLabel(addr) ?: continue
            val romOffset = addr - 0x8000 + prgOffset

            if (romOffset < 0 || romOffset >= romData.size) continue

            val actualByte = romData[romOffset].toInt() and 0xFF

            // Check if this looks like a valid instruction start
            // Common first opcodes: LDA=0xA9/0xA5/0xAD, LDX=0xA2/0xA6/0xAE, LDY=0xA0/0xA4/0xAC
            // STA=0x85/0x8D, STX=0x86/0x8E, STY=0x84/0x8C, JSR=0x20, JMP=0x4C, RTS=0x60
            // SEI=0x78, CLD=0xD8, CLC=0x18, SEC=0x38, PHP=0x08, PHA=0x48, PLA=0x68, PLP=0x28
            // BIT=0x24/0x2C, TAX=0xAA, TXA=0x8A, TAY=0xA8, TYA=0x98, INX=0xE8, DEX=0xCA
            // INY=0xC8, DEY=0x88, AND=0x29/0x25/0x2D, ORA=0x09/0x05/0x0D, EOR=0x49/0x45/0x4D
            // CMP=0xC9/0xC5/0xCD, CPX=0xE0/0xE4/0xEC, CPY=0xC0/0xC4/0xCC
            // Branch opcodes: BCC=0x90, BCS=0xB0, BEQ=0xF0, BNE=0xD0, BMI=0x30, BPL=0x10

            val validOpcodes = setOf(
                0x00, 0x01, 0x05, 0x06, 0x08, 0x09, 0x0A, 0x0D, 0x0E, 0x10, 0x11, 0x15, 0x16, 0x18, 0x19, 0x1D, 0x1E,
                0x20, 0x21, 0x24, 0x25, 0x26, 0x28, 0x29, 0x2A, 0x2C, 0x2D, 0x2E, 0x30, 0x31, 0x35, 0x36, 0x38, 0x39, 0x3D, 0x3E,
                0x40, 0x41, 0x45, 0x46, 0x48, 0x49, 0x4A, 0x4C, 0x4D, 0x4E, 0x50, 0x51, 0x55, 0x56, 0x58, 0x59, 0x5D, 0x5E,
                0x60, 0x61, 0x65, 0x66, 0x68, 0x69, 0x6A, 0x6C, 0x6D, 0x6E, 0x70, 0x71, 0x75, 0x76, 0x78, 0x79, 0x7D, 0x7E,
                0x81, 0x84, 0x85, 0x86, 0x88, 0x8A, 0x8C, 0x8D, 0x8E, 0x90, 0x91, 0x94, 0x95, 0x96, 0x98, 0x99, 0x9A, 0x9D,
                0xA0, 0xA1, 0xA2, 0xA4, 0xA5, 0xA6, 0xA8, 0xA9, 0xAA, 0xAC, 0xAD, 0xAE, 0xB0, 0xB1, 0xB4, 0xB5, 0xB6, 0xB8, 0xB9, 0xBA, 0xBC, 0xBD, 0xBE,
                0xC0, 0xC1, 0xC4, 0xC5, 0xC6, 0xC8, 0xC9, 0xCA, 0xCC, 0xCD, 0xCE, 0xD0, 0xD1, 0xD5, 0xD6, 0xD8, 0xD9, 0xDD, 0xDE,
                0xE0, 0xE1, 0xE4, 0xE5, 0xE6, 0xE8, 0xE9, 0xEA, 0xEC, 0xED, 0xEE, 0xF0, 0xF1, 0xF5, 0xF6, 0xF8, 0xF9, 0xFD, 0xFE
            )

            val isValid = actualByte in validOpcodes

            if (!isValid && !driftDetected) {
                // First invalid - print context
                println("*** DRIFT DETECTED ***")
                println("Last correct: $lastCorrectLabel @ 0x${lastCorrectAddr.toString(16).uppercase()}")
                println("First error:  $label @ 0x${addr.toString(16).uppercase()}")
                println("  Found byte 0x${actualByte.toString(16).uppercase()}, not a valid opcode")

                // Search for where this label's expected opcode actually is
                println("\n  Searching nearby for valid opcodes...")
                for (delta in listOf(-100, -50, -20, -10, -5, -4, -3, -2, -1, 1, 2, 3, 4, 5, 10, 20, 50, 100)) {
                    val searchOffset = romOffset + delta
                    if (searchOffset >= 0 && searchOffset < romData.size) {
                        val byte = romData[searchOffset].toInt() and 0xFF
                        if (byte in validOpcodes) {
                            println("    delta=$delta: 0x${byte.toString(16).uppercase()} (valid opcode)")
                        }
                    }
                }

                driftDetected = true
                println("\n  Continuing to find drift pattern...\n")
            }

            if (isValid) {
                lastCorrectAddr = addr
                lastCorrectLabel = label
            } else if (driftDetected && (addr and 0x0FFF) == 0) {
                // Print status every 0x1000 bytes once drift is detected
                println("At 0x${addr.toString(16).uppercase()}: offset ~${addr - lastCorrectAddr} bytes from last correct")
            }
        }

        if (!driftDetected) {
            println("No drift detected! All labels appear to have valid opcodes.")
        }
    }

    @Test
    fun debugConstantParsing() {
        val asmFile = File("smbdism.asm")
        val text = asmFile.readText()

        println("=== Checking Constant Parsing ===\n")

        // Parse the file using the real parser
        val codeFile = text.parseToAssemblyCodeFile()

        // Count constants that were parsed
        var parsedConstantsCount = 0
        var zeroPageCount = 0
        var nonZeroPageCount = 0
        var nullValueCount = 0
        val sampleConstants = mutableListOf<String>()
        val nullConstants = mutableListOf<String>()

        for (line in codeFile.lines) {
            val lineConstant = line.constant
            if (lineConstant != null) {
                parsedConstantsCount++
                val value = when (val v = lineConstant.value) {
                    is AssemblyAddressing.ByteValue -> v.value.toInt()
                    is AssemblyAddressing.ShortValue -> v.value.toInt()
                    is AssemblyAddressing.ValueLowerSelection -> (v.value.value.toInt() and 0xFF)
                    is AssemblyAddressing.ValueUpperSelection -> ((v.value.value.toInt() shr 8) and 0xFF)
                    else -> null
                }
                if (value != null) {
                    if (value < 256) {
                        zeroPageCount++
                    } else {
                        nonZeroPageCount++
                    }
                    if (sampleConstants.size < 10) {
                        sampleConstants.add("${lineConstant.name} = 0x${value.toString(16).uppercase()} (${if (value < 256) "ZP" else "ABS"})")
                    }
                } else {
                    nullValueCount++
                    if (nullConstants.size < 10) {
                        nullConstants.add("${lineConstant.name} = ${lineConstant.value}")
                    }
                }
            }
        }

        println("Parsed $parsedConstantsCount constants:")
        println("  Zero-page (<256): $zeroPageCount")
        println("  Non-zero-page (>=256): $nonZeroPageCount")
        println("  Null/unparseable: $nullValueCount")
        println("\nSample constants:")
        sampleConstants.forEach { println("  $it") }
        if (nullConstants.isNotEmpty()) {
            println("\nUnparseable constants (value returned null):")
            nullConstants.forEach { println("  $it") }
        }

        // Now test that mapper collects them internally
        val mapper = AddressLabelMapper.fromAssemblyFile(asmFile)

        // The mapper doesn't expose the constants map, but we can check labels
        println("\nMapper has ${mapper.getAllAddresses().size} address labels")
        println("Mapper has ${mapper.getJsrTargetLabels().size} JSR target labels")

        // Note: Constants like FrameCounter=$09 are NOT in the address map
        // because they are RAM addresses, not code addresses.
        // The mapper only tracks code labels.
    }

    @Test
    fun traceAddressCalculationBetweenLabels() {
        val asmFile = File("smbdism.asm")
        val text = asmFile.readText()

        println("=== Tracing Address Calculation from OutputCol to OutputTScr ===\n")

        val codeFile = text.parseToAssemblyCodeFile()

        // Build constants map same way as AddressLabelMapper
        val constants = mutableMapOf<String, Int>()
        for (line in codeFile.lines) {
            val lineConstant = line.constant
            if (lineConstant != null) {
                val value = when (val v = lineConstant.value) {
                    is AssemblyAddressing.ByteValue -> v.value.toInt()
                    is AssemblyAddressing.ShortValue -> v.value.toInt()
                    else -> null
                }
                if (value != null) {
                    constants[lineConstant.name] = value
                }
            }
        }
        println("Loaded ${constants.size} constants")

        // Find lines between OutputCol and OutputTScr
        var foundOutputCol = false
        var foundOutputTScr = false
        var currentAddress = 0
        var inCode = false

        for (line in codeFile.lines) {
            val original = line.originalLine ?: ""
            val trimmed = original.trim()

            if (trimmed.startsWith(".org", ignoreCase = true)) {
                val addrStr = trimmed.substringAfter(".org", "")
                    .trim()
                    .removePrefix("$")
                    .trim()
                currentAddress = addrStr.toIntOrNull(16) ?: continue
                inCode = true
                continue
            }

            if (!inCode) continue

            val label = line.label

            if (label == "OutputCol") {
                println("Found OutputCol at address 0x${currentAddress.toString(16).uppercase()}")
                foundOutputCol = true
            }

            if (foundOutputCol && !foundOutputTScr) {
                // Calculate instruction size and print
                val instr = line.instruction
                val data = line.data

                val size = when {
                    instr != null -> getInstructionSizeWithDebug(instr, constants)
                    data != null -> {
                        if (data is com.ivieleague.decompiler6502tokotlin.hand.AssemblyData.Db) data.byteCount() else 0
                    }
                    else -> 0
                }

                if (size > 0) {
                    val instrStr = instr?.toString() ?: data?.toString() ?: ""
                    println("  0x${currentAddress.toString(16).uppercase()}: $instrStr = $size bytes")
                }

                currentAddress += size

                if (label == "OutputTScr") {
                    println("\nOutputTScr reached at 0x${(currentAddress - size).toString(16).uppercase()}")
                    foundOutputTScr = true
                    break
                }
            }
        }
    }

    // by Claude - Debug version of getInstructionSize
    private fun getInstructionSizeWithDebug(
        instr: com.ivieleague.decompiler6502tokotlin.hand.AssemblyInstruction,
        constants: Map<String, Int>
    ): Int {
        if (instr.address == null) return 1

        return when (val addr = instr.address) {
            is AssemblyAddressing.ByteValue,
            is AssemblyAddressing.ShortValue,
            is AssemblyAddressing.ValueLowerSelection,
            is AssemblyAddressing.ValueUpperSelection,
            is AssemblyAddressing.ConstantReference,
            // by Claude - Added new hi/lo constant reference types
            is AssemblyAddressing.ConstantReferenceLower,
            is AssemblyAddressing.ConstantReferenceUpper -> 2

            is AssemblyAddressing.Direct -> {
                if (instr.op.isBranch) return 2
                if (instr.op == com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.JMP ||
                    instr.op == com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.JSR) return 3
                val isZp = isZeroPageLabelDebug(addr.label, constants)
                if (isZp) 2 else 3
            }

            is AssemblyAddressing.DirectX -> {
                val isZp = isZeroPageLabelDebug(addr.label, constants)
                if (isZp) 2 else 3
            }
            is AssemblyAddressing.DirectY -> {
                // by Claude - Only LDX and STX support zero-page,Y. All others are absolute,Y (3 bytes)
                val supportsZpY = instr.op == com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.LDX ||
                                  instr.op == com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.STX
                val isZp = isZeroPageLabelDebug(addr.label, constants)
                if (supportsZpY && isZp) 2 else 3
            }

            is AssemblyAddressing.IndirectX,
            is AssemblyAddressing.IndirectY -> 2

            is AssemblyAddressing.IndirectAbsolute -> 3

            else -> 1 // Unknown addressing mode
        }
    }

    private fun isZeroPageLabelDebug(label: String, constants: Map<String, Int>): Boolean {
        if (label.startsWith("$")) {
            val hex = label.removePrefix("$").substringBefore('+').substringBefore('-')
            val addr = hex.toIntOrNull(16) ?: return false
            return addr < 256
        }
        val baseLabel = label.substringBefore('+').substringBefore('-')
        val constValue = constants[baseLabel]
        if (constValue != null) {
            val isZp = constValue < 256
            return isZp
        }
        // Not found - assume NOT zero page
        return false
    }

    // by Claude - More focused test: trace from OutputCol to TScrClear
    @Test
    fun traceFromOutputColToError() {
        val asmFile = File("smbdism.asm")
        val romFile = File("local/roms/smb.nes")
        if (!romFile.exists()) {
            println("ROM file not found, skipping")
            return
        }

        val text = asmFile.readText()
        val codeFile = text.parseToAssemblyCodeFile()
        val romData = romFile.readBytes()
        val prgOffset = 16 // iNES header

        println("=== Tracing from OutputCol to find 3-byte drift ===\n")

        // Build constants map
        val constants = mutableMapOf<String, Int>()
        for (line in codeFile.lines) {
            val lineConstant = line.constant
            if (lineConstant != null) {
                val value = when (val v = lineConstant.value) {
                    is AssemblyAddressing.ByteValue -> v.value.toInt()
                    is AssemblyAddressing.ShortValue -> v.value.toInt()
                    is AssemblyAddressing.ValueLowerSelection -> (v.value.value.toInt() and 0xFF)
                    is AssemblyAddressing.ValueUpperSelection -> ((v.value.value.toInt() shr 8) and 0xFF)
                    else -> null
                }
                if (value != null) {
                    constants[lineConstant.name] = value
                }
            }
        }

        var calculatedAddress = 0
        var inCode = false
        var foundOutputCol = false
        var traceCount = 0

        for (line in codeFile.lines) {
            val original = line.originalLine ?: ""
            val trimmed = original.trim()

            if (trimmed.startsWith(".org", ignoreCase = true)) {
                val addrStr = trimmed.substringAfter(".org", "").trim().removePrefix("$").trim()
                calculatedAddress = addrStr.toIntOrNull(16) ?: continue
                inCode = true
                continue
            }

            if (!inCode) continue

            val label = line.label
            val instr = line.instruction
            val data = line.data

            // Calculate size
            val size = when {
                instr != null -> getInstructionSizeWithDebug(instr, constants)
                data != null -> if (data is com.ivieleague.decompiler6502tokotlin.hand.AssemblyData.Db) data.byteCount() else 0
                else -> 0
            }

            // Start tracing at a given label (change to debug different areas)
            // by Claude - Set to Start to trace from beginning, or change to any label name to debug specific areas
            val startLabel = "Start"  // Default: trace from start of ROM
            if (label == startLabel) {
                foundOutputCol = true
                println("Found $startLabel at 0x${calculatedAddress.toString(16).uppercase()}")
                println("\nTracing instructions:\n")
            }

            if (foundOutputCol && size > 0) {
                val romOffset = calculatedAddress - 0x8000 + prgOffset
                val romByte = if (romOffset in romData.indices)
                    romData[romOffset].toInt() and 0xFF else -1
                val romByte2 = if (romOffset + 1 in romData.indices)
                    romData[romOffset + 1].toInt() and 0xFF else -1
                val romByte3 = if (romOffset + 2 in romData.indices)
                    romData[romOffset + 2].toInt() and 0xFF else -1

                val addrMode = when (instr?.address) {
                    is AssemblyAddressing.Direct -> {
                        val lbl = (instr.address as AssemblyAddressing.Direct).label
                        val isZp = isZeroPageLabelDebug(lbl, constants)
                        if (isZp) "ZP" else "ABS"
                    }
                    is AssemblyAddressing.DirectX -> {
                        val lbl = (instr.address as AssemblyAddressing.DirectX).label
                        val isZp = isZeroPageLabelDebug(lbl, constants)
                        if (isZp) "ZP,X" else "ABS,X"
                    }
                    is AssemblyAddressing.DirectY -> {
                        val lbl = (instr.address as AssemblyAddressing.DirectY).label
                        // by Claude - Only LDX and STX support zero-page,Y
                        val supportsZpY = instr.op == com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.LDX ||
                                          instr.op == com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.STX
                        val isZp = isZeroPageLabelDebug(lbl, constants)
                        if (supportsZpY && isZp) "ZP,Y" else "ABS,Y"
                    }
                    is AssemblyAddressing.ByteValue -> "IMM"
                    is AssemblyAddressing.ShortValue -> "IMM"
                    is AssemblyAddressing.ConstantReference -> "IMM(ref)"
                    is AssemblyAddressing.ConstantReferenceLower -> "IMM(<ref)"
                    is AssemblyAddressing.ConstantReferenceUpper -> "IMM(>ref)"
                    is AssemblyAddressing.IndirectY -> "IND,Y"
                    is AssemblyAddressing.IndirectX -> "IND,X"
                    null -> "IMP"
                    else -> "?"
                }

                val instrStr = if (instr != null) "${instr.op} ${instr.address ?: ""}" else data?.toString() ?: ""
                val labelStr = if (label != null) "$label: " else "        "

                println("  0x${calculatedAddress.toString(16).uppercase().padStart(4, '0')}: $labelStr$instrStr")
                println("         Size=$size ($addrMode)  ROM=[${romByte.toString(16).padStart(2, '0')}, ${romByte2.toString(16).padStart(2, '0')}, ${romByte3.toString(16).padStart(2, '0')}]")

                traceCount++
                if (traceCount > 100) {
                    println("\n... stopping after 100 instructions")
                    break
                }
            }

            calculatedAddress += size
        }
    }

    @Test
    fun findExactDriftLocation() {
        val asmFile = File("smbdism.asm")
        val romFile = File("local/roms/smb.nes")
        if (!romFile.exists()) {
            println("ROM file not found, skipping")
            return
        }

        val text = asmFile.readText()
        val codeFile = text.parseToAssemblyCodeFile()
        val romData = romFile.readBytes()
        val prgOffset = 16 // iNES header

        println("=== Finding Exact Drift Location ===\n")

        // Build constants map
        val constants = mutableMapOf<String, Int>()
        for (line in codeFile.lines) {
            val lineConstant = line.constant
            if (lineConstant != null) {
                val value = when (val v = lineConstant.value) {
                    is AssemblyAddressing.ByteValue -> v.value.toInt()
                    is AssemblyAddressing.ShortValue -> v.value.toInt()
                    is AssemblyAddressing.ValueLowerSelection -> (v.value.value.toInt() and 0xFF)
                    is AssemblyAddressing.ValueUpperSelection -> ((v.value.value.toInt() shr 8) and 0xFF)
                    else -> null
                }
                if (value != null) {
                    constants[lineConstant.name] = value
                }
            }
        }

        var calculatedAddress = 0
        var inCode = false
        var driftDetected = false
        var totalDrift = 0
        var lastGoodLabel = ""
        var lastGoodAddress = 0
        var instructionCount = 0

        for (line in codeFile.lines) {
            val original = line.originalLine ?: ""
            val trimmed = original.trim()

            if (trimmed.startsWith(".org", ignoreCase = true)) {
                val addrStr = trimmed.substringAfter(".org", "")
                    .trim()
                    .removePrefix("$")
                    .trim()
                calculatedAddress = addrStr.toIntOrNull(16) ?: continue
                inCode = true
                println("Starting at .org 0x${calculatedAddress.toString(16).uppercase()}")
                continue
            }

            if (!inCode) continue

            val label = line.label
            val instr = line.instruction
            val data = line.data

            // Calculate size
            val size = when {
                instr != null -> getInstructionSizeWithDebug(instr, constants)
                data != null -> {
                    if (data is com.ivieleague.decompiler6502tokotlin.hand.AssemblyData.Db) data.byteCount() else 0
                }
                else -> 0
            }

            // For instructions, verify against ROM
            if (instr != null && calculatedAddress >= 0x8000 && calculatedAddress < 0x10000) {
                val romOffset = calculatedAddress - 0x8000 + prgOffset
                if (romOffset >= 0 && romOffset < romData.size) {
                    val romByte = romData[romOffset].toInt() and 0xFF
                    val expectedOpcode = getExpectedOpcodeForInstruction(instr, constants)

                    if (expectedOpcode != null && romByte != expectedOpcode) {
                        if (!driftDetected) {
                            println("\n*** FIRST DRIFT DETECTED ***")
                            println("  Last good: $lastGoodLabel @ 0x${lastGoodAddress.toString(16).uppercase()}")
                            println("  Instruction count since start: $instructionCount")
                        }
                        driftDetected = true

                        // Find where this opcode actually is
                        var actualOffset: Int? = null
                        for (delta in -50..50) {
                            val searchOffset = romOffset + delta
                            if (searchOffset >= 0 && searchOffset < romData.size) {
                                if ((romData[searchOffset].toInt() and 0xFF) == expectedOpcode) {
                                    actualOffset = delta
                                    break
                                }
                            }
                        }

                        if (actualOffset != null && actualOffset != totalDrift) {
                            val newDrift = actualOffset
                            println("\nDrift changed at instruction $instructionCount:")
                            println("  ${instr.op} ${instr.address ?: ""}")
                            println("  Calculated: 0x${calculatedAddress.toString(16).uppercase()}")
                            println("  Expected opcode: 0x${expectedOpcode.toString(16).uppercase()}")
                            println("  Found opcode at delta: $actualOffset (was $totalDrift)")
                            println("  Size used: $size bytes")

                            // Check if this instruction's size might be wrong
                            if (newDrift != totalDrift) {
                                val sizeDiff = newDrift - totalDrift
                                println("  *** SIZE ERROR: instruction sized as $size but should be ${size - sizeDiff}")
                            }
                            totalDrift = newDrift
                        }
                    } else if (expectedOpcode != null) {
                        lastGoodLabel = label ?: lastGoodLabel
                        lastGoodAddress = calculatedAddress
                    }
                }
                instructionCount++
            }

            calculatedAddress += size

            // Stop after significant drift to avoid too much output
            if (totalDrift > 10 && instructionCount > 1000) {
                println("\n... stopping after $instructionCount instructions with drift of $totalDrift bytes")
                break
            }
        }

        println("\nFinal drift: $totalDrift bytes after $instructionCount instructions")
    }

    // by Claude - Get expected opcode for an instruction
    private fun getExpectedOpcodeForInstruction(
        instr: com.ivieleague.decompiler6502tokotlin.hand.AssemblyInstruction,
        constants: Map<String, Int>
    ): Int? {
        val op = instr.op
        val addr = instr.address

        // Implied addressing (no operand)
        if (addr == null) {
            return when (op) {
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.RTS -> 0x60
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.RTI -> 0x40
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.SEI -> 0x78
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.CLI -> 0x58
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.CLD -> 0xD8
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.SED -> 0xF8
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.CLC -> 0x18
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.SEC -> 0x38
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.CLV -> 0xB8
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.PHA -> 0x48
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.PLA -> 0x68
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.PHP -> 0x08
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.PLP -> 0x28
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.TAX -> 0xAA
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.TXA -> 0x8A
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.TAY -> 0xA8
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.TYA -> 0x98
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.TXS -> 0x9A
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.TSX -> 0xBA
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.INX -> 0xE8
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.DEX -> 0xCA
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.INY -> 0xC8
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.DEY -> 0x88
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.NOP -> 0xEA
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.BRK -> 0x00
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.ASL -> 0x0A // Accumulator
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.LSR -> 0x4A
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.ROL -> 0x2A
                com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.ROR -> 0x6A
                else -> null
            }
        }

        // For other addressing modes, just return the base opcode for immediate
        // This is simplified - a full implementation would handle all modes
        return when {
            addr is AssemblyAddressing.ByteValue ||
            addr is AssemblyAddressing.ShortValue ||
            addr is AssemblyAddressing.ConstantReference ||
            addr is AssemblyAddressing.ConstantReferenceLower ||
            addr is AssemblyAddressing.ConstantReferenceUpper ||
            addr is AssemblyAddressing.ValueLowerSelection ||
            addr is AssemblyAddressing.ValueUpperSelection -> {
                // Immediate addressing
                when (op) {
                    com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.LDA -> 0xA9
                    com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.LDX -> 0xA2
                    com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.LDY -> 0xA0
                    com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.CMP -> 0xC9
                    com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.CPX -> 0xE0
                    com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.CPY -> 0xC0
                    com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.ADC -> 0x69
                    com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.SBC -> 0xE9
                    com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.AND -> 0x29
                    com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.ORA -> 0x09
                    com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.EOR -> 0x49
                    else -> null
                }
            }
            op.isBranch -> {
                when (op) {
                    com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.BCC -> 0x90
                    com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.BCS -> 0xB0
                    com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.BEQ -> 0xF0
                    com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.BNE -> 0xD0
                    com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.BMI -> 0x30
                    com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.BPL -> 0x10
                    com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.BVC -> 0x50
                    com.ivieleague.decompiler6502tokotlin.hand.AssemblyOp.BVS -> 0x70
                    else -> null
                }
            }
            else -> null // Skip complex addressing modes for now
        }
    }

    @Test
    fun verifyMapperAddressesAgainstRom() {
        val asmFile = File("smbdism.asm")
        val romFile = File("local/roms/smb.nes")
        if (!romFile.exists()) {
            println("ROM file not found, skipping")
            return
        }

        val mapper = AddressLabelMapper.fromAssemblyFile(asmFile)
        val romData = romFile.readBytes()
        val prgOffset = 16 // iNES header

        println("=== Verifying AddressLabelMapper Addresses Against ROM ===\n")

        // Read the assembly to get labels that are for instructions (not data)
        val text = asmFile.readText()
        val lines = text.lines()

        val instructionLabels = mutableSetOf<String>()
        var inCode = false
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.startsWith(".org", ignoreCase = true)) {
                inCode = true
                continue
            }
            if (!inCode) continue

            val labelPart = line.substringBefore(";").substringBefore(":").trim()
            val instrPart = line.substringBefore(";").substringAfter(":").trim()

            if (labelPart.isNotEmpty() && labelPart.first().isLetter()) {
                // Check if this line has an instruction (not data)
                val firstToken = instrPart.substringBefore(" ").lowercase()
                if (firstToken.isNotEmpty() && !firstToken.startsWith(".") && !instrPart.contains("=")) {
                    instructionLabels.add(labelPart)
                }
            }
        }

        println("Found ${instructionLabels.size} instruction labels to verify\n")

        var correctCount = 0
        var errorCount = 0
        var lastCorrectLabel = ""
        var lastCorrectAddr = 0
        var firstErrorLabel: String? = null
        var firstErrorAddr = 0
        var firstErrorOffset = 0

        // Check each instruction label
        val sortedAddrs = mapper.getAllAddresses().filter { addr ->
            val label = mapper.getLabel(addr) ?: return@filter false
            label in instructionLabels && addr >= 0x8000
        }.sorted()

        for (addr in sortedAddrs) {
            val label = mapper.getLabel(addr) ?: continue
            val romOffset = addr - 0x8000 + prgOffset

            if (romOffset < 0 || romOffset >= romData.size) continue

            val romByte = romData[romOffset].toInt() and 0xFF

            // Check if this looks like a valid instruction start (common first opcodes)
            // We can't know the exact expected opcode without parsing, but we can check if it's a valid opcode
            val validOpcodes = setOf(
                0x00, 0x01, 0x05, 0x06, 0x08, 0x09, 0x0A, 0x0D, 0x0E, 0x10, 0x11, 0x15, 0x16, 0x18, 0x19, 0x1D, 0x1E,
                0x20, 0x21, 0x24, 0x25, 0x26, 0x28, 0x29, 0x2A, 0x2C, 0x2D, 0x2E, 0x30, 0x31, 0x35, 0x36, 0x38, 0x39, 0x3D, 0x3E,
                0x40, 0x41, 0x45, 0x46, 0x48, 0x49, 0x4A, 0x4C, 0x4D, 0x4E, 0x50, 0x51, 0x55, 0x56, 0x58, 0x59, 0x5D, 0x5E,
                0x60, 0x61, 0x65, 0x66, 0x68, 0x69, 0x6A, 0x6C, 0x6D, 0x6E, 0x70, 0x71, 0x75, 0x76, 0x78, 0x79, 0x7D, 0x7E,
                0x81, 0x84, 0x85, 0x86, 0x88, 0x8A, 0x8C, 0x8D, 0x8E, 0x90, 0x91, 0x94, 0x95, 0x96, 0x98, 0x99, 0x9A, 0x9D,
                0xA0, 0xA1, 0xA2, 0xA4, 0xA5, 0xA6, 0xA8, 0xA9, 0xAA, 0xAC, 0xAD, 0xAE, 0xB0, 0xB1, 0xB4, 0xB5, 0xB6, 0xB8, 0xB9, 0xBA, 0xBC, 0xBD, 0xBE,
                0xC0, 0xC1, 0xC4, 0xC5, 0xC6, 0xC8, 0xC9, 0xCA, 0xCC, 0xCD, 0xCE, 0xD0, 0xD1, 0xD5, 0xD6, 0xD8, 0xD9, 0xDD, 0xDE,
                0xE0, 0xE1, 0xE4, 0xE5, 0xE6, 0xE8, 0xE9, 0xEA, 0xEC, 0xED, 0xEE, 0xF0, 0xF1, 0xF5, 0xF6, 0xF8, 0xF9, 0xFD, 0xFE
            )

            if (romByte in validOpcodes) {
                correctCount++
                lastCorrectLabel = label
                lastCorrectAddr = addr
            } else {
                errorCount++
                if (firstErrorLabel == null) {
                    firstErrorLabel = label
                    firstErrorAddr = addr
                    firstErrorOffset = addr - lastCorrectAddr
                    println("*** FIRST ERROR ***")
                    println("  Last correct: $lastCorrectLabel @ 0x${lastCorrectAddr.toString(16).uppercase()}")
                    println("  First error:  $label @ 0x${addr.toString(16).uppercase()}")
                    println("  Found byte:   0x${romByte.toString(16).uppercase()} (not valid opcode)")
                    println("  Offset from last correct: $firstErrorOffset bytes")
                    println()
                }
            }
        }

        println("Summary:")
        println("  Correct: $correctCount")
        println("  Errors:  $errorCount")
        println("  Success rate: ${String.format("%.2f", correctCount * 100.0 / (correctCount + errorCount))}%")

        if (firstErrorLabel != null) {
            println("\nFirst error occurred at $firstErrorLabel (0x${firstErrorAddr.toString(16).uppercase()})")
            println("This is ${firstErrorOffset} bytes after $lastCorrectLabel")
        }
    }

    private fun getExpectedOpcode(op: String, fullInstr: String): Int? {
        val operand = fullInstr.substringAfter(" ").trim()
        return when (op) {
            "lda" -> when {
                operand.startsWith("#") -> 0xA9
                operand.contains(",x") -> if (isZeroPage(operand)) 0xB5 else 0xBD
                operand.contains(",y") -> if (isZeroPage(operand)) 0xB9 else 0xB9
                operand.startsWith("(") && operand.endsWith(",x)") -> 0xA1
                operand.startsWith("(") && operand.endsWith("),y") -> 0xB1
                else -> if (isZeroPage(operand)) 0xA5 else 0xAD
            }
            "ldx" -> when {
                operand.startsWith("#") -> 0xA2
                operand.contains(",y") -> if (isZeroPage(operand)) 0xB6 else 0xBE
                else -> if (isZeroPage(operand)) 0xA6 else 0xAE
            }
            "ldy" -> when {
                operand.startsWith("#") -> 0xA0
                operand.contains(",x") -> if (isZeroPage(operand)) 0xB4 else 0xBC
                else -> if (isZeroPage(operand)) 0xA4 else 0xAC
            }
            "sta" -> when {
                operand.contains(",x") -> if (isZeroPage(operand)) 0x95 else 0x9D
                operand.contains(",y") -> 0x99
                operand.startsWith("(") && operand.endsWith(",x)") -> 0x81
                operand.startsWith("(") && operand.endsWith("),y") -> 0x91
                else -> if (isZeroPage(operand)) 0x85 else 0x8D
            }
            "stx" -> when {
                operand.contains(",y") -> 0x96
                else -> if (isZeroPage(operand)) 0x86 else 0x8E
            }
            "sty" -> when {
                operand.contains(",x") -> 0x94
                else -> if (isZeroPage(operand)) 0x84 else 0x8C
            }
            "jsr" -> 0x20
            "jmp" -> if (operand.startsWith("(")) 0x6C else 0x4C
            "rts" -> 0x60
            "rti" -> 0x40
            "sei" -> 0x78
            "cli" -> 0x58
            "cld" -> 0xD8
            "sed" -> 0xF8
            "clc" -> 0x18
            "sec" -> 0x38
            "clv" -> 0xB8
            "pha" -> 0x48
            "pla" -> 0x68
            "php" -> 0x08
            "plp" -> 0x28
            "tax" -> 0xAA
            "txa" -> 0x8A
            "tay" -> 0xA8
            "tya" -> 0x98
            "txs" -> 0x9A
            "tsx" -> 0xBA
            "inx" -> 0xE8
            "dex" -> 0xCA
            "iny" -> 0xC8
            "dey" -> 0x88
            "nop" -> 0xEA
            "brk" -> 0x00
            "bcc" -> 0x90
            "bcs" -> 0xB0
            "beq" -> 0xF0
            "bne" -> 0xD0
            "bmi" -> 0x30
            "bpl" -> 0x10
            "bvc" -> 0x50
            "bvs" -> 0x70
            "bit" -> if (isZeroPage(operand)) 0x24 else 0x2C
            "cmp" -> when {
                operand.startsWith("#") -> 0xC9
                operand.contains(",x") -> if (isZeroPage(operand)) 0xD5 else 0xDD
                operand.contains(",y") -> 0xD9
                operand.startsWith("(") && operand.endsWith(",x)") -> 0xC1
                operand.startsWith("(") && operand.endsWith("),y") -> 0xD1
                else -> if (isZeroPage(operand)) 0xC5 else 0xCD
            }
            "cpx" -> when {
                operand.startsWith("#") -> 0xE0
                else -> if (isZeroPage(operand)) 0xE4 else 0xEC
            }
            "cpy" -> when {
                operand.startsWith("#") -> 0xC0
                else -> if (isZeroPage(operand)) 0xC4 else 0xCC
            }
            "adc" -> when {
                operand.startsWith("#") -> 0x69
                operand.contains(",x") -> if (isZeroPage(operand)) 0x75 else 0x7D
                operand.contains(",y") -> 0x79
                operand.startsWith("(") && operand.endsWith(",x)") -> 0x61
                operand.startsWith("(") && operand.endsWith("),y") -> 0x71
                else -> if (isZeroPage(operand)) 0x65 else 0x6D
            }
            "sbc" -> when {
                operand.startsWith("#") -> 0xE9
                operand.contains(",x") -> if (isZeroPage(operand)) 0xF5 else 0xFD
                operand.contains(",y") -> 0xF9
                operand.startsWith("(") && operand.endsWith(",x)") -> 0xE1
                operand.startsWith("(") && operand.endsWith("),y") -> 0xF1
                else -> if (isZeroPage(operand)) 0xE5 else 0xED
            }
            "and" -> when {
                operand.startsWith("#") -> 0x29
                operand.contains(",x") -> if (isZeroPage(operand)) 0x35 else 0x3D
                operand.contains(",y") -> 0x39
                operand.startsWith("(") && operand.endsWith(",x)") -> 0x21
                operand.startsWith("(") && operand.endsWith("),y") -> 0x31
                else -> if (isZeroPage(operand)) 0x25 else 0x2D
            }
            "ora" -> when {
                operand.startsWith("#") -> 0x09
                operand.contains(",x") -> if (isZeroPage(operand)) 0x15 else 0x1D
                operand.contains(",y") -> 0x19
                operand.startsWith("(") && operand.endsWith(",x)") -> 0x01
                operand.startsWith("(") && operand.endsWith("),y") -> 0x11
                else -> if (isZeroPage(operand)) 0x05 else 0x0D
            }
            "eor" -> when {
                operand.startsWith("#") -> 0x49
                operand.contains(",x") -> if (isZeroPage(operand)) 0x55 else 0x5D
                operand.contains(",y") -> 0x59
                operand.startsWith("(") && operand.endsWith(",x)") -> 0x41
                operand.startsWith("(") && operand.endsWith("),y") -> 0x51
                else -> if (isZeroPage(operand)) 0x45 else 0x4D
            }
            "inc" -> when {
                operand.contains(",x") -> if (isZeroPage(operand)) 0xF6 else 0xFE
                else -> if (isZeroPage(operand)) 0xE6 else 0xEE
            }
            "dec" -> when {
                operand.contains(",x") -> if (isZeroPage(operand)) 0xD6 else 0xDE
                else -> if (isZeroPage(operand)) 0xC6 else 0xCE
            }
            "asl" -> when {
                operand.isEmpty() || operand.lowercase() == "a" -> 0x0A
                operand.contains(",x") -> if (isZeroPage(operand)) 0x16 else 0x1E
                else -> if (isZeroPage(operand)) 0x06 else 0x0E
            }
            "lsr" -> when {
                operand.isEmpty() || operand.lowercase() == "a" -> 0x4A
                operand.contains(",x") -> if (isZeroPage(operand)) 0x56 else 0x5E
                else -> if (isZeroPage(operand)) 0x46 else 0x4E
            }
            "rol" -> when {
                operand.isEmpty() || operand.lowercase() == "a" -> 0x2A
                operand.contains(",x") -> if (isZeroPage(operand)) 0x36 else 0x3E
                else -> if (isZeroPage(operand)) 0x26 else 0x2E
            }
            "ror" -> when {
                operand.isEmpty() || operand.lowercase() == "a" -> 0x6A
                operand.contains(",x") -> if (isZeroPage(operand)) 0x76 else 0x7E
                else -> if (isZeroPage(operand)) 0x66 else 0x6E
            }
            else -> null
        }
    }

    private fun isZeroPage(operand: String): Boolean {
        // Extract the address part (remove ,x ,y etc)
        val addrPart = operand.substringBefore(",").substringAfter("(").substringBefore(")").trim()

        // If it's a hex value like $1d, check if < 256
        if (addrPart.startsWith("$")) {
            val value = addrPart.drop(1).toIntOrNull(16) ?: return false
            return value < 256
        }

        // For labels, we can't determine here - would need the constants map
        // This is a limitation of this test - we'll get some false mismatches
        return false
    }
}
