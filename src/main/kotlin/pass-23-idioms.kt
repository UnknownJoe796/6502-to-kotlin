package com.ivieleague.decompiler6502tokotlin

/**
 * Pass 23: Idiom Recognition
 * - Recognize common 6502 assembly patterns
 * - Replace multi-instruction sequences with high-level equivalents
 * - Detect 16-bit arithmetic, multiplication, division
 * - Identify bit manipulation patterns
 * - Recognize NES-specific patterns
 */

/**
 * Recognized assembly idioms
 */
sealed class Idiom {
    // 16-bit arithmetic
    data class Add16(
        val targetLow: Variable,
        val targetHigh: Variable,
        val operandLow: Expression,
        val operandHigh: Expression
    ) : Idiom()

    data class Sub16(
        val targetLow: Variable,
        val targetHigh: Variable,
        val operandLow: Expression,
        val operandHigh: Expression
    ) : Idiom()

    data class Load16(
        val targetLow: Variable,
        val targetHigh: Variable,
        val valueLow: Int,
        val valueHigh: Int
    ) : Idiom()

    data class Inc16(
        val addressLow: Int,
        val addressHigh: Int
    ) : Idiom()

    data class Dec16(
        val addressLow: Int,
        val addressHigh: Int
    ) : Idiom()

    // Multiplication/division
    data class MultiplyByPowerOf2(
        val operand: Expression,
        val power: Int  // operand * (2^power)
    ) : Idiom()

    data class MultiplyByConstant(
        val operand: Expression,
        val multiplier: Int,
        val method: String  // "shifts-and-adds", "table-lookup", etc.
    ) : Idiom()

    data class DivideByPowerOf2(
        val operand: Expression,
        val power: Int  // operand / (2^power)
    ) : Idiom()

    // Bit manipulation
    data class SetBit(
        val target: Expression,
        val bitIndex: Int
    ) : Idiom()

    data class ClearBit(
        val target: Expression,
        val bitIndex: Int
    ) : Idiom()

    data class TestBit(
        val target: Expression,
        val bitIndex: Int
    ) : Idiom()

    data class ToggleBit(
        val target: Expression,
        val bitIndex: Int
    ) : Idiom()

    data class MaskBits(
        val target: Expression,
        val mask: Int,
        val invert: Boolean
    ) : Idiom()

    // Array operations
    data class ArrayCopy(
        val sourceBase: Int,
        val destBase: Int,
        val length: Expression,
        val indexRegister: Variable
    ) : Idiom()

    data class ArrayFill(
        val destBase: Int,
        val value: Expression,
        val length: Expression,
        val indexRegister: Variable
    ) : Idiom()

    data class ArrayClear(
        val destBase: Int,
        val length: Expression,
        val indexRegister: Variable
    ) : Idiom()

    // Control flow patterns
    data class MinValue(
        val a: Expression,
        val b: Expression
    ) : Idiom()

    data class MaxValue(
        val a: Expression,
        val b: Expression
    ) : Idiom()

    data class Clamp(
        val value: Expression,
        val min: Int,
        val max: Int
    ) : Idiom()

    // NES-specific patterns
    data class WaitForVBlank(
        val ppuStatusRegister: Int = 0x2002
    ) : Idiom()

    data class PpuWrite(
        val ppuAddrRegister: Int = 0x2006,
        val ppuDataRegister: Int = 0x2007,
        val address: Expression,
        val data: Expression
    ) : Idiom()

    data class OamDmaTransfer(
        val oamDmaRegister: Int = 0x4014,
        val sourcePageAddress: Int
    ) : Idiom()
}

/**
 * An instance of a recognized idiom in the code
 */
data class IdiomInstance(
    val idiom: Idiom,
    val lineRange: IntRange,
    val replacementExpression: Expression,
    val confidence: Double,
    val description: String
)

/**
 * Idiom recognition result for a function
 */
data class IdiomRecognitionResult(
    val function: FunctionCfg,
    val recognizedIdioms: List<IdiomInstance>,
    val lineToIdiom: Map<Int, IdiomInstance>
)

/**
 * Complete idiom recognition analysis
 */
data class IdiomRecognitionAnalysis(
    val functions: List<IdiomRecognitionResult>
)

/**
 * Recognize idioms in all functions
 */
fun AssemblyCodeFile.recognizeIdioms(
    cfg: CfgConstruction,
    expressions: ExpressionTreeAnalysis,
    constants: ConstantPropagationAnalysis
): IdiomRecognitionAnalysis {
    val functionResults = cfg.functions.mapIndexed { index, function ->
        val funcExpressions = expressions.functions.getOrNull(index)
        val funcConstants = constants.functions.getOrNull(index)

        recognizeFunctionIdioms(
            this,
            function,
            funcExpressions,
            funcConstants
        )
    }

    return IdiomRecognitionAnalysis(functions = functionResults)
}

/**
 * Recognize idioms in a single function
 */
private fun recognizeFunctionIdioms(
    codeFile: AssemblyCodeFile,
    function: FunctionCfg,
    expressions: FunctionExpressions?,
    constants: FunctionConstantAnalysis?
): IdiomRecognitionResult {
    val recognizedIdioms = mutableListOf<IdiomInstance>()

    function.blocks.forEach { block ->
        val blockIdioms = recognizeBlockIdioms(codeFile, block, expressions, constants)
        recognizedIdioms.addAll(blockIdioms)
    }

    // Build line-to-idiom map
    val lineToIdiom = recognizedIdioms
        .flatMap { idiom -> idiom.lineRange.map { line -> line to idiom } }
        .toMap()

    return IdiomRecognitionResult(
        function = function,
        recognizedIdioms = recognizedIdioms,
        lineToIdiom = lineToIdiom
    )
}

/**
 * Recognize idioms in a basic block
 */
private fun recognizeBlockIdioms(
    codeFile: AssemblyCodeFile,
    block: BasicBlock,
    expressions: FunctionExpressions?,
    constants: FunctionConstantAnalysis?
): List<IdiomInstance> {
    val idioms = mutableListOf<IdiomInstance>()
    val lines = block.lineIndexes.map { codeFile.get(it) }

    // Try to recognize various patterns
    idioms.addAll(recognize16BitArithmetic(lines, expressions))
    idioms.addAll(recognizeMultiplicationPatterns(lines, expressions))
    idioms.addAll(recognizeBitManipulation(lines, expressions))
    idioms.addAll(recognizeArrayOperations(lines, expressions))
    idioms.addAll(recognizeNesPatterns(lines, expressions))

    // Sort by confidence and remove overlapping low-confidence matches
    return removeOverlappingIdioms(idioms)
}

/**
 * Recognize 16-bit arithmetic patterns
 */
private fun recognize16BitArithmetic(
    lines: List<AssemblyLineReference>,
    expressions: FunctionExpressions?
): List<IdiomInstance> {
    val idioms = mutableListOf<IdiomInstance>()

    // Pattern: 16-bit addition
    // CLC
    // LDA low1
    // ADC low2
    // STA resultLow
    // LDA high1
    // ADC high2
    // STA resultHigh
    for (i in 0 until lines.size - 6) {
        val instructions = lines.subList(i, i + 7).mapNotNull { it.content.instruction }
        if (instructions.size == 7 &&
            instructions[0].op == AssemblyOp.CLC &&
            instructions[1].op == AssemblyOp.LDA &&
            instructions[2].op == AssemblyOp.ADC &&
            instructions[3].op == AssemblyOp.STA &&
            instructions[4].op == AssemblyOp.LDA &&
            instructions[5].op == AssemblyOp.ADC &&
            instructions[6].op == AssemblyOp.STA
        ) {
            idioms.add(
                IdiomInstance(
                    idiom = Idiom.Add16(
                        targetLow = Variable.RegisterA,
                        targetHigh = Variable.RegisterA,
                        operandLow = Expression.Literal(0),
                        operandHigh = Expression.Literal(0)
                    ),
                    lineRange = lines[i].line..lines[i + 6].line,
                    replacementExpression = Expression.BinaryOp(
                        BinaryOperator.ADD,
                        Expression.Literal(0),  // Placeholder
                        Expression.Literal(0)
                    ),
                    confidence = 0.95,
                    description = "16-bit addition"
                )
            )
        }
    }

    // Pattern: 16-bit increment
    // INC addrLow
    // BNE skip
    // INC addrHigh
    // skip:
    for (i in 0 until lines.size - 3) {
        val instructions = lines.subList(i, minOf(i + 3, lines.size)).mapNotNull { it.content.instruction }
        if (instructions.size >= 3 &&
            instructions[0].op == AssemblyOp.INC &&
            instructions[1].op == AssemblyOp.BNE &&
            instructions[2].op == AssemblyOp.INC
        ) {
            idioms.add(
                IdiomInstance(
                    idiom = Idiom.Inc16(0, 1),  // Placeholder addresses
                    lineRange = lines[i].line..lines[i + 2].line,
                    replacementExpression = Expression.BinaryOp(
                        BinaryOperator.ADD,
                        Expression.Literal(0),
                        Expression.Literal(1)
                    ),
                    confidence = 0.9,
                    description = "16-bit increment"
                )
            )
        }
    }

    return idioms
}

/**
 * Recognize multiplication patterns
 */
private fun recognizeMultiplicationPatterns(
    lines: List<AssemblyLineReference>,
    expressions: FunctionExpressions?
): List<IdiomInstance> {
    val idioms = mutableListOf<IdiomInstance>()

    // Pattern: Multiply by power of 2 using repeated ASL
    // LDA value
    // ASL A (repeated N times)
    for (i in 0 until lines.size - 1) {
        val instructions = lines.subList(i, minOf(i + 10, lines.size)).mapNotNull { it.content.instruction }

        if (instructions.isNotEmpty() && instructions[0].op == AssemblyOp.LDA) {
            // Count consecutive ASL A instructions
            var shiftCount = 0
            for (j in 1 until instructions.size) {
                if (instructions[j].op == AssemblyOp.ASL &&
                    (instructions[j].address == null || instructions[j].address is AssemblyAddressing.Accumulator)
                ) {
                    shiftCount++
                } else {
                    break
                }
            }

            if (shiftCount > 0) {
                idioms.add(
                    IdiomInstance(
                        idiom = Idiom.MultiplyByPowerOf2(
                            Expression.VariableRef(Variable.RegisterA),
                            shiftCount
                        ),
                        lineRange = lines[i].line..lines[i + shiftCount].line,
                        replacementExpression = Expression.BinaryOp(
                            BinaryOperator.MUL,
                            Expression.VariableRef(Variable.RegisterA),
                            Expression.Literal(1 shl shiftCount)
                        ),
                        confidence = 1.0,
                        description = "Multiply by ${1 shl shiftCount} (shift left $shiftCount)"
                    )
                )
            }
        }
    }

    // Pattern: Multiply by 10 = *8 + *2
    // LDA value
    // ASL A      ; *2
    // STA temp
    // ASL A      ; *4
    // ASL A      ; *8
    // CLC
    // ADC temp   ; *8 + *2 = *10
    for (i in 0 until lines.size - 6) {
        val instructions = lines.subList(i, i + 7).mapNotNull { it.content.instruction }
        if (instructions.size == 7 &&
            instructions[0].op == AssemblyOp.LDA &&
            instructions[1].op == AssemblyOp.ASL &&
            instructions[2].op == AssemblyOp.STA &&
            instructions[3].op == AssemblyOp.ASL &&
            instructions[4].op == AssemblyOp.ASL &&
            instructions[5].op == AssemblyOp.CLC &&
            instructions[6].op == AssemblyOp.ADC
        ) {
            idioms.add(
                IdiomInstance(
                    idiom = Idiom.MultiplyByConstant(
                        Expression.VariableRef(Variable.RegisterA),
                        10,
                        "shifts-and-adds"
                    ),
                    lineRange = lines[i].line..lines[i + 6].line,
                    replacementExpression = Expression.BinaryOp(
                        BinaryOperator.MUL,
                        Expression.VariableRef(Variable.RegisterA),
                        Expression.Literal(10)
                    ),
                    confidence = 0.95,
                    description = "Multiply by 10"
                )
            )
        }
    }

    return idioms
}

/**
 * Recognize bit manipulation patterns
 */
private fun recognizeBitManipulation(
    lines: List<AssemblyLineReference>,
    expressions: FunctionExpressions?
): List<IdiomInstance> {
    val idioms = mutableListOf<IdiomInstance>()

    // Pattern: Set bit with ORA
    // LDA variable
    // ORA #mask
    // STA variable
    for (i in 0 until lines.size - 2) {
        val instructions = lines.subList(i, i + 3).mapNotNull { it.content.instruction }
        if (instructions.size == 3 &&
            instructions[0].op == AssemblyOp.LDA &&
            instructions[1].op == AssemblyOp.ORA &&
            instructions[1].address is AssemblyAddressing.ValueHex &&
            instructions[2].op == AssemblyOp.STA
        ) {
            val mask = (instructions[1].address as AssemblyAddressing.ValueHex).value.toInt()
            val bitIndex = Integer.numberOfTrailingZeros(mask)

            // Check if mask is a single bit
            if (mask > 0 && (mask and (mask - 1)) == 0) {
                idioms.add(
                    IdiomInstance(
                        idiom = Idiom.SetBit(
                            Expression.VariableRef(Variable.RegisterA),
                            bitIndex
                        ),
                        lineRange = lines[i].line..lines[i + 2].line,
                        replacementExpression = Expression.BinaryOp(
                            BinaryOperator.OR,
                            Expression.VariableRef(Variable.RegisterA),
                            Expression.Literal(mask)
                        ),
                        confidence = 0.9,
                        description = "Set bit $bitIndex"
                    )
                )
            }
        }
    }

    // Pattern: Clear bit with AND
    // LDA variable
    // AND #mask (inverted bit)
    // STA variable
    for (i in 0 until lines.size - 2) {
        val instructions = lines.subList(i, i + 3).mapNotNull { it.content.instruction }
        if (instructions.size == 3 &&
            instructions[0].op == AssemblyOp.LDA &&
            instructions[1].op == AssemblyOp.AND &&
            instructions[1].address is AssemblyAddressing.ValueHex &&
            instructions[2].op == AssemblyOp.STA
        ) {
            val mask = (instructions[1].address as AssemblyAddressing.ValueHex).value.toInt()
            val invertedMask = mask xor 0xFF
            val bitIndex = Integer.numberOfTrailingZeros(invertedMask)

            // Check if inverted mask is a single bit
            if (invertedMask > 0 && (invertedMask and (invertedMask - 1)) == 0) {
                idioms.add(
                    IdiomInstance(
                        idiom = Idiom.ClearBit(
                            Expression.VariableRef(Variable.RegisterA),
                            bitIndex
                        ),
                        lineRange = lines[i].line..lines[i + 2].line,
                        replacementExpression = Expression.BinaryOp(
                            BinaryOperator.AND,
                            Expression.VariableRef(Variable.RegisterA),
                            Expression.Literal(mask)
                        ),
                        confidence = 0.9,
                        description = "Clear bit $bitIndex"
                    )
                )
            }
        }
    }

    // Pattern: Test bit with AND (without storing)
    // LDA variable
    // AND #mask
    // BEQ/BNE label
    for (i in 0 until lines.size - 2) {
        val instructions = lines.subList(i, i + 3).mapNotNull { it.content.instruction }
        if (instructions.size == 3 &&
            instructions[0].op == AssemblyOp.LDA &&
            instructions[1].op == AssemblyOp.AND &&
            instructions[1].address is AssemblyAddressing.ValueHex &&
            (instructions[2].op == AssemblyOp.BEQ || instructions[2].op == AssemblyOp.BNE)
        ) {
            val mask = (instructions[1].address as AssemblyAddressing.ValueHex).value.toInt()
            val bitIndex = Integer.numberOfTrailingZeros(mask)

            if (mask > 0 && (mask and (mask - 1)) == 0) {
                idioms.add(
                    IdiomInstance(
                        idiom = Idiom.TestBit(
                            Expression.VariableRef(Variable.RegisterA),
                            bitIndex
                        ),
                        lineRange = lines[i].line..lines[i + 1].line,
                        replacementExpression = Expression.Comparison(
                            ComparisonOp.NE,
                            Expression.BinaryOp(
                                BinaryOperator.AND,
                                Expression.VariableRef(Variable.RegisterA),
                                Expression.Literal(mask)
                            ),
                            Expression.Literal(0)
                        ),
                        confidence = 0.95,
                        description = "Test bit $bitIndex"
                    )
                )
            }
        }
    }

    return idioms
}

/**
 * Recognize array operation patterns
 */
private fun recognizeArrayOperations(
    lines: List<AssemblyLineReference>,
    expressions: FunctionExpressions?
): List<IdiomInstance> {
    val idioms = mutableListOf<IdiomInstance>()

    // Pattern: Array fill/clear loop
    // LDX #0
    // LDA #value
    // loop:
    // STA array,X
    // INX
    // CPX #length
    // BNE loop
    for (i in 0 until lines.size - 5) {
        val instructions = lines.subList(i, minOf(i + 6, lines.size)).mapNotNull { it.content.instruction }
        if (instructions.size >= 6 &&
            instructions[0].op == AssemblyOp.LDX &&
            instructions[1].op == AssemblyOp.LDA &&
            instructions[2].op == AssemblyOp.STA &&
            instructions[2].address is AssemblyAddressing.DirectX &&
            instructions[3].op == AssemblyOp.INX &&
            instructions[4].op == AssemblyOp.CPX &&
            instructions[5].op == AssemblyOp.BNE
        ) {
            val isZero = instructions[1].address is AssemblyAddressing.ValueHex &&
                (instructions[1].address as AssemblyAddressing.ValueHex).value.toInt() == 0

            idioms.add(
                IdiomInstance(
                    idiom = if (isZero) {
                        Idiom.ArrayClear(0, Expression.Literal(0), Variable.RegisterX)
                    } else {
                        Idiom.ArrayFill(0, Expression.Literal(0), Expression.Literal(0), Variable.RegisterX)
                    },
                    lineRange = lines[i].line..lines[minOf(i + 5, lines.size - 1)].line,
                    replacementExpression = Expression.FunctionCall(
                        if (isZero) "memset" else "memfill",
                        listOf(Expression.Literal(0), Expression.Literal(0), Expression.Literal(0))
                    ),
                    confidence = 0.85,
                    description = if (isZero) "Array clear loop" else "Array fill loop"
                )
            )
        }
    }

    return idioms
}

/**
 * Recognize NES-specific patterns
 */
private fun recognizeNesPatterns(
    lines: List<AssemblyLineReference>,
    expressions: FunctionExpressions?
): List<IdiomInstance> {
    val idioms = mutableListOf<IdiomInstance>()

    // Pattern: Wait for VBlank
    // vblankwait:
    // BIT $2002
    // BPL vblankwait
    for (i in 0 until lines.size - 1) {
        val instructions = lines.subList(i, i + 2).mapNotNull { it.content.instruction }
        if (instructions.size == 2 &&
            instructions[0].op == AssemblyOp.BIT &&
            instructions[0].address is AssemblyAddressing.Label &&
            (instructions[0].address as AssemblyAddressing.Label).label == "$2002" &&
            instructions[1].op == AssemblyOp.BPL
        ) {
            idioms.add(
                IdiomInstance(
                    idiom = Idiom.WaitForVBlank(),
                    lineRange = lines[i].line..lines[i + 1].line,
                    replacementExpression = Expression.FunctionCall(
                        "waitForVBlank",
                        emptyList()
                    ),
                    confidence = 1.0,
                    description = "Wait for VBlank"
                )
            )
        }
    }

    return idioms
}

/**
 * Remove overlapping idioms, preferring higher confidence matches
 */
private fun removeOverlappingIdioms(idioms: List<IdiomInstance>): List<IdiomInstance> {
    val sorted = idioms.sortedByDescending { it.confidence }
    val result = mutableListOf<IdiomInstance>()
    val usedLines = mutableSetOf<Int>()

    for (idiom in sorted) {
        val lines = idiom.lineRange.toSet()
        if (lines.none { it in usedLines }) {
            result.add(idiom)
            usedLines.addAll(lines)
        }
    }

    return result.sortedBy { it.lineRange.first }
}
