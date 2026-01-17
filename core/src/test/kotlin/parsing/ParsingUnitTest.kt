// by Claude - Phase 1: Parsing Unit Tests (Foundation Layer)
// Tests for models.kt and parsing.kt - the foundation of the decompiler pipeline
package com.ivieleague.decompiler6502tokotlin.parsing

import com.ivieleague.decompiler6502tokotlin.hand.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertIs

/**
 * Parsing Unit Test Suite
 *
 * This is the foundation for decompiler correctness verification.
 * Tests verify that:
 * 1. AssemblyAddressing.parse() correctly parses all addressing modes
 * 2. parseDbItems() correctly parses .db directives
 * 3. parseWordItems() correctly parses .dw directives
 * 4. splitCsvRespectingQuotes() correctly handles CSV parsing
 * 5. Full line parsing produces correct AssemblyLine objects
 */
class ParsingUnitTest {

    // =====================================================================
    // IMMEDIATE VALUE PARSING (AssemblyAddressing.parse())
    // =====================================================================

    @Test
    fun `immediate hex values - standard hex`() {
        val addr = AssemblyAddressing.parse("#\$42")
        assertIs<AssemblyAddressing.ByteValue>(addr)
        assertEquals(0x42.toUByte(), addr.value)
        assertEquals(AssemblyAddressing.Radix.Hex, addr.radix)
    }

    @Test
    fun `immediate hex values - max byte value`() {
        val addr = AssemblyAddressing.parse("#\$FF")
        assertIs<AssemblyAddressing.ByteValue>(addr)
        assertEquals(0xFF.toUByte(), addr.value)
    }

    @Test
    fun `immediate hex values - zero`() {
        val addr = AssemblyAddressing.parse("#\$00")
        assertIs<AssemblyAddressing.ByteValue>(addr)
        assertEquals(0x00.toUByte(), addr.value)
    }

    @Test
    fun `immediate hex values - short value becomes ShortValue`() {
        val addr = AssemblyAddressing.parse("#\$1234")
        assertIs<AssemblyAddressing.ShortValue>(addr)
        assertEquals(0x1234.toUShort(), addr.value)
    }

    @Test
    fun `immediate binary values - simple pattern`() {
        val addr = AssemblyAddressing.parse("#%10101010")
        assertIs<AssemblyAddressing.ByteValue>(addr)
        assertEquals(0b10101010.toUByte(), addr.value)
        assertEquals(AssemblyAddressing.Radix.Binary, addr.radix)
    }

    @Test
    fun `immediate binary values - all zeros`() {
        val addr = AssemblyAddressing.parse("#%00000000")
        assertIs<AssemblyAddressing.ByteValue>(addr)
        assertEquals(0.toUByte(), addr.value)
    }

    @Test
    fun `immediate binary values - all ones`() {
        val addr = AssemblyAddressing.parse("#%11111111")
        assertIs<AssemblyAddressing.ByteValue>(addr)
        assertEquals(0xFF.toUByte(), addr.value)
    }

    @Test
    fun `immediate decimal values - standard decimal`() {
        val addr = AssemblyAddressing.parse("#42")
        assertIs<AssemblyAddressing.ByteValue>(addr)
        assertEquals(42.toUByte(), addr.value)
        assertEquals(AssemblyAddressing.Radix.Decimal, addr.radix)
    }

    @Test
    fun `immediate decimal values - max byte value`() {
        val addr = AssemblyAddressing.parse("#255")
        assertIs<AssemblyAddressing.ByteValue>(addr)
        assertEquals(255.toUByte(), addr.value)
    }

    @Test
    fun `immediate decimal values - becomes ShortValue when over 255`() {
        val addr = AssemblyAddressing.parse("#256")
        assertIs<AssemblyAddressing.ShortValue>(addr)
        assertEquals(256.toUShort(), addr.value)
    }

    // =====================================================================
    // CONSTANT REFERENCE PARSING
    // =====================================================================

    @Test
    fun `constant reference - simple name`() {
        val addr = AssemblyAddressing.parse("#MyConstant")
        assertIs<AssemblyAddressing.ConstantReference>(addr)
        assertEquals("MyConstant", addr.name)
    }

    @Test
    fun `constant reference - underscore in name`() {
        val addr = AssemblyAddressing.parse("#My_Constant")
        assertIs<AssemblyAddressing.ConstantReference>(addr)
        assertEquals("My_Constant", addr.name)
    }

    // =====================================================================
    // HI/LO BYTE SELECTION PARSING
    // =====================================================================

    @Test
    fun `hi-lo byte selection - lower byte of hex value`() {
        val addr = AssemblyAddressing.parse("#<\$1234")
        assertIs<AssemblyAddressing.ValueLowerSelection>(addr)
        assertIs<AssemblyAddressing.ShortValue>(addr.value)
        assertEquals(0x1234.toUShort(), (addr.value as AssemblyAddressing.ShortValue).value)
    }

    @Test
    fun `hi-lo byte selection - upper byte of hex value`() {
        val addr = AssemblyAddressing.parse("#>\$1234")
        assertIs<AssemblyAddressing.ValueUpperSelection>(addr)
        assertIs<AssemblyAddressing.ShortValue>(addr.value)
        assertEquals(0x1234.toUShort(), (addr.value as AssemblyAddressing.ShortValue).value)
    }

    @Test
    fun `hi-lo byte selection - lower byte of constant`() {
        val addr = AssemblyAddressing.parse("#<MyConst")
        assertIs<AssemblyAddressing.ConstantReferenceLower>(addr)
        assertEquals("MyConst", addr.name)
    }

    @Test
    fun `hi-lo byte selection - upper byte of constant`() {
        val addr = AssemblyAddressing.parse("#>MyConst")
        assertIs<AssemblyAddressing.ConstantReferenceUpper>(addr)
        assertEquals("MyConst", addr.name)
    }

    // =====================================================================
    // DIRECT ADDRESSING PARSING
    // =====================================================================

    @Test
    fun `direct addressing - hex address`() {
        val addr = AssemblyAddressing.parse("\$1000")
        assertIs<AssemblyAddressing.Direct>(addr)
        assertEquals("\$1000", addr.label)
        assertEquals(0, addr.offset)
    }

    @Test
    fun `direct addressing - label`() {
        val addr = AssemblyAddressing.parse("MyLabel")
        assertIs<AssemblyAddressing.Direct>(addr)
        assertEquals("MyLabel", addr.label)
        assertEquals(0, addr.offset)
    }

    @Test
    fun `direct addressing - label with positive offset`() {
        val addr = AssemblyAddressing.parse("Label+5")
        assertIs<AssemblyAddressing.Direct>(addr)
        assertEquals("Label", addr.label)
        assertEquals(5, addr.offset)
    }

    @Test
    fun `direct addressing - label with negative offset`() {
        val addr = AssemblyAddressing.parse("Label-1")
        assertIs<AssemblyAddressing.Direct>(addr)
        assertEquals("Label", addr.label)
        assertEquals(-1, addr.offset)
    }

    @Test
    fun `direct addressing - label with hex offset`() {
        val addr = AssemblyAddressing.parse("Label+\$10")
        assertIs<AssemblyAddressing.Direct>(addr)
        assertEquals("Label", addr.label)
        assertEquals(0x10, addr.offset)
    }

    @Test
    fun `direct addressing - complex offset expression`() {
        val addr = AssemblyAddressing.parse("VRAM_Buffer1-1+\$100")
        assertIs<AssemblyAddressing.Direct>(addr)
        assertEquals("VRAM_Buffer1", addr.label)
        assertEquals(-1 + 0x100, addr.offset)
    }

    // =====================================================================
    // INDEXED ADDRESSING PARSING
    // =====================================================================

    @Test
    fun `indexed addressing - direct X`() {
        val addr = AssemblyAddressing.parse("\$1000,X")
        assertIs<AssemblyAddressing.DirectX>(addr)
        assertEquals("\$1000", addr.label)
        assertEquals(0, addr.offset)
    }

    @Test
    fun `indexed addressing - direct Y`() {
        val addr = AssemblyAddressing.parse("\$1000,Y")
        assertIs<AssemblyAddressing.DirectY>(addr)
        assertEquals("\$1000", addr.label)
        assertEquals(0, addr.offset)
    }

    @Test
    fun `indexed addressing - label with X index`() {
        val addr = AssemblyAddressing.parse("Label,X")
        assertIs<AssemblyAddressing.DirectX>(addr)
        assertEquals("Label", addr.label)
    }

    @Test
    fun `indexed addressing - label with offset and X index`() {
        val addr = AssemblyAddressing.parse("Label+2,X")
        assertIs<AssemblyAddressing.DirectX>(addr)
        assertEquals("Label", addr.label)
        assertEquals(2, addr.offset)
    }

    @Test
    fun `indexed addressing - label with offset and Y index`() {
        val addr = AssemblyAddressing.parse("Label+2,Y")
        assertIs<AssemblyAddressing.DirectY>(addr)
        assertEquals("Label", addr.label)
        assertEquals(2, addr.offset)
    }

    @Test
    fun `indexed addressing - case insensitive X`() {
        val addr = AssemblyAddressing.parse("\$1000,x")
        assertIs<AssemblyAddressing.DirectX>(addr)
    }

    @Test
    fun `indexed addressing - case insensitive Y`() {
        val addr = AssemblyAddressing.parse("\$1000,y")
        assertIs<AssemblyAddressing.DirectY>(addr)
    }

    // =====================================================================
    // INDIRECT ADDRESSING PARSING
    // =====================================================================

    @Test
    fun `indirect addressing - absolute indirect`() {
        val addr = AssemblyAddressing.parse("(\$1000)")
        assertIs<AssemblyAddressing.IndirectAbsolute>(addr)
        assertEquals("\$1000", addr.label)
    }

    @Test
    fun `indirect addressing - label indirect`() {
        val addr = AssemblyAddressing.parse("(Label)")
        assertIs<AssemblyAddressing.IndirectAbsolute>(addr)
        assertEquals("Label", addr.label)
    }

    @Test
    fun `indirect addressing - indexed indirect X`() {
        val addr = AssemblyAddressing.parse("(\$10,X)")
        assertIs<AssemblyAddressing.IndirectX>(addr)
        assertEquals("\$10", addr.label)
    }

    @Test
    fun `indirect addressing - indirect indexed Y`() {
        val addr = AssemblyAddressing.parse("(\$10),Y")
        assertIs<AssemblyAddressing.IndirectY>(addr)
        assertEquals("\$10", addr.label)
    }

    // =====================================================================
    // ACCUMULATOR MODE PARSING
    // =====================================================================

    @Test
    fun `accumulator mode - returns null for A`() {
        val addr = AssemblyAddressing.parse("A")
        assertNull(addr)
    }

    @Test
    fun `accumulator mode - case insensitive`() {
        val addr = AssemblyAddressing.parse("a")
        assertNull(addr)
    }

    // =====================================================================
    // FULL LINE PARSING
    // =====================================================================

    @Test
    fun `full line parsing - label plus instruction`() {
        val file = "MyLabel: LDA #\$42".parseToAssemblyCodeFile()
        assertEquals(1, file.lines.size)
        val line = file.lines[0]
        assertEquals("MyLabel", line.label)
        assertNotNull(line.instruction)
        assertEquals(AssemblyOp.LDA, line.instruction!!.op)
    }

    @Test
    fun `full line parsing - instruction only`() {
        val file = "LDA #\$42".parseToAssemblyCodeFile()
        assertEquals(1, file.lines.size)
        val line = file.lines[0]
        assertNull(line.label)
        assertNotNull(line.instruction)
        assertEquals(AssemblyOp.LDA, line.instruction!!.op)
    }

    @Test
    fun `full line parsing - constant definition`() {
        val file = "MyVar = \$0722".parseToAssemblyCodeFile()
        assertEquals(1, file.lines.size)
        val line = file.lines[0]
        assertNotNull(line.constant)
        assertEquals("MyVar", line.constant!!.name)
    }

    @Test
    fun `full line parsing - data directive db`() {
        val file = ".db \$42, \$FF".parseToAssemblyCodeFile()
        assertEquals(1, file.lines.size)
        val line = file.lines[0]
        assertNotNull(line.data)
        assertIs<AssemblyData.Db>(line.data)
    }

    @Test
    fun `full line parsing - data directive with label`() {
        val file = "DataLabel: .db \$42".parseToAssemblyCodeFile()
        assertEquals(1, file.lines.size)
        val line = file.lines[0]
        assertEquals("DataLabel", line.label)
        assertNotNull(line.data)
    }

    @Test
    fun `full line parsing - comment preservation`() {
        val file = "LDA #\$42 ; Load the value".parseToAssemblyCodeFile()
        assertEquals(1, file.lines.size)
        val line = file.lines[0]
        assertEquals("Load the value", line.comment)
    }

    @Test
    fun `full line parsing - comment only line`() {
        val file = "; This is a comment".parseToAssemblyCodeFile()
        assertEquals(1, file.lines.size)
        val line = file.lines[0]
        assertNull(line.instruction)
        assertEquals("This is a comment", line.comment)
    }

    @Test
    fun `full line parsing - blank line`() {
        val file = "".parseToAssemblyCodeFile()
        assertEquals(1, file.lines.size)
        val line = file.lines[0]
        assertNull(line.instruction)
        assertNull(line.label)
    }

    @Test
    fun `full line parsing - multiple lines`() {
        val asm = """
            Start: LDA #${'$'}00
            STA ${'$'}0200
            RTS
        """.trimIndent()
        val file = asm.parseToAssemblyCodeFile()
        assertEquals(3, file.lines.size)
        assertEquals("Start", file.lines[0].label)
        assertEquals(AssemblyOp.LDA, file.lines[0].instruction?.op)
        assertEquals(AssemblyOp.STA, file.lines[1].instruction?.op)
        assertEquals(AssemblyOp.RTS, file.lines[2].instruction?.op)
    }

    // =====================================================================
    // DATA DIRECTIVE PARSING
    // =====================================================================

    @Test
    fun `db parsing - hex byte values`() {
        val file = ".db \$42, \$FF".parseToAssemblyCodeFile()
        val data = file.lines[0].data as AssemblyData.Db
        assertEquals(2, data.items.size)
        assertIs<AssemblyData.DbItem.ByteValue>(data.items[0])
        assertEquals(0x42, (data.items[0] as AssemblyData.DbItem.ByteValue).value)
        assertEquals(0xFF, (data.items[1] as AssemblyData.DbItem.ByteValue).value)
    }

    @Test
    fun `db parsing - string literal`() {
        val file = ".db \"hello\"".parseToAssemblyCodeFile()
        val data = file.lines[0].data as AssemblyData.Db
        assertEquals(1, data.items.size)
        assertIs<AssemblyData.DbItem.StringLiteral>(data.items[0])
        assertEquals("hello", (data.items[0] as AssemblyData.DbItem.StringLiteral).text)
    }

    @Test
    fun `db parsing - mixed items`() {
        val file = ".db \$42, \"text\", \$00".parseToAssemblyCodeFile()
        val data = file.lines[0].data as AssemblyData.Db
        assertEquals(3, data.items.size)
        assertIs<AssemblyData.DbItem.ByteValue>(data.items[0])
        assertIs<AssemblyData.DbItem.StringLiteral>(data.items[1])
        assertIs<AssemblyData.DbItem.ByteValue>(data.items[2])
    }

    @Test
    fun `db parsing - label expression`() {
        val file = ".db MyLabel".parseToAssemblyCodeFile()
        val data = file.lines[0].data as AssemblyData.Db
        assertEquals(1, data.items.size)
        assertIs<AssemblyData.DbItem.Expr>(data.items[0])
        assertEquals("MyLabel", (data.items[0] as AssemblyData.DbItem.Expr).expr)
    }

    @Test
    fun `db parsing - hi-lo expressions`() {
        val file = ".db <Label, >Label".parseToAssemblyCodeFile()
        val data = file.lines[0].data as AssemblyData.Db
        assertEquals(2, data.items.size)
        assertIs<AssemblyData.DbItem.Expr>(data.items[0])
        assertEquals("<Label", (data.items[0] as AssemblyData.DbItem.Expr).expr)
        assertEquals(">Label", (data.items[1] as AssemblyData.DbItem.Expr).expr)
    }

    @Test
    fun `db parsing - decimal values`() {
        val file = ".db 42, 255".parseToAssemblyCodeFile()
        val data = file.lines[0].data as AssemblyData.Db
        assertEquals(2, data.items.size)
        assertEquals(42, (data.items[0] as AssemblyData.DbItem.ByteValue).value)
        assertEquals(255, (data.items[1] as AssemblyData.DbItem.ByteValue).value)
    }

    @Test
    fun `db parsing - binary values`() {
        val file = ".db %10101010".parseToAssemblyCodeFile()
        val data = file.lines[0].data as AssemblyData.Db
        assertEquals(1, data.items.size)
        assertEquals(0b10101010, (data.items[0] as AssemblyData.DbItem.ByteValue).value)
    }

    @Test
    fun `db byteCount - simple bytes`() {
        val file = ".db \$42, \$FF, \$00".parseToAssemblyCodeFile()
        val data = file.lines[0].data as AssemblyData.Db
        assertEquals(3, data.byteCount())
    }

    @Test
    fun `db byteCount - string literal`() {
        val file = ".db \"hello\"".parseToAssemblyCodeFile()
        val data = file.lines[0].data as AssemblyData.Db
        assertEquals(5, data.byteCount())  // "hello" is 5 characters
    }

    // =====================================================================
    // WORD DIRECTIVE PARSING
    // =====================================================================

    @Test
    fun `dw parsing - word values little endian`() {
        val file = ".dw \$1234".parseToAssemblyCodeFile()
        val data = file.lines[0].data as AssemblyData.Db
        assertEquals(2, data.items.size)
        // Little-endian: low byte first
        assertEquals(0x34, (data.items[0] as AssemblyData.DbItem.ByteValue).value)
        assertEquals(0x12, (data.items[1] as AssemblyData.DbItem.ByteValue).value)
    }

    @Test
    fun `dw parsing - multiple words`() {
        val file = ".dw \$1234, \$ABCD".parseToAssemblyCodeFile()
        val data = file.lines[0].data as AssemblyData.Db
        assertEquals(4, data.items.size)
        assertEquals(0x34, (data.items[0] as AssemblyData.DbItem.ByteValue).value)
        assertEquals(0x12, (data.items[1] as AssemblyData.DbItem.ByteValue).value)
        assertEquals(0xCD, (data.items[2] as AssemblyData.DbItem.ByteValue).value)
        assertEquals(0xAB, (data.items[3] as AssemblyData.DbItem.ByteValue).value)
    }

    @Test
    fun `dw parsing - label reference becomes expr pair`() {
        val file = ".dw MyLabel".parseToAssemblyCodeFile()
        val data = file.lines[0].data as AssemblyData.Db
        assertEquals(2, data.items.size)  // 2 bytes for a word
        assertIs<AssemblyData.DbItem.Expr>(data.items[0])
    }

    // =====================================================================
    // INSTRUCTION OPCODE PARSING
    // =====================================================================

    @Test
    fun `opcode parsing - all branch instructions`() {
        val branches = listOf("BEQ", "BNE", "BCC", "BCS", "BMI", "BPL", "BVC", "BVS")
        for (branch in branches) {
            val file = "$branch Label".parseToAssemblyCodeFile()
            val instr = file.lines[0].instruction
            assertNotNull(instr, "Failed to parse $branch")
            assertTrue(instr.op.isBranch, "$branch should be a branch instruction")
        }
    }

    @Test
    fun `opcode parsing - all transfer instructions`() {
        val transfers = listOf("TAX", "TAY", "TXA", "TYA", "TSX", "TXS")
        for (transfer in transfers) {
            val file = transfer.parseToAssemblyCodeFile()
            val instr = file.lines[0].instruction
            assertNotNull(instr, "Failed to parse $transfer")
            assertEquals(AssemblyOp.valueOf(transfer), instr.op)
        }
    }

    @Test
    fun `opcode parsing - case insensitive`() {
        val file = "lda #\$42".parseToAssemblyCodeFile()
        val instr = file.lines[0].instruction
        assertNotNull(instr)
        assertEquals(AssemblyOp.LDA, instr.op)
    }

    // =====================================================================
    // EDGE CASES AND ERROR HANDLING
    // =====================================================================

    @Test
    fun `edge case - whitespace handling`() {
        val file = "  LDA   #\$42  ".parseToAssemblyCodeFile()
        val instr = file.lines[0].instruction
        assertNotNull(instr)
        assertEquals(AssemblyOp.LDA, instr.op)
    }

    @Test
    fun `edge case - label with colon in middle of line`() {
        val file = "Loop: INX".parseToAssemblyCodeFile()
        assertEquals("Loop", file.lines[0].label)
        assertEquals(AssemblyOp.INX, file.lines[0].instruction?.op)
    }

    @Test
    fun `edge case - complex real world line`() {
        val file = "VRAM_Buffer1: .db \$00, \$00, \$00".parseToAssemblyCodeFile()
        assertEquals("VRAM_Buffer1", file.lines[0].label)
        assertNotNull(file.lines[0].data)
    }

    // =====================================================================
    // ASSEMBLY OP METADATA
    // =====================================================================

    @Test
    fun `assembly op - branch flags`() {
        // Verify branch instruction metadata
        assertTrue(AssemblyOp.BEQ.isBranch)
        assertTrue(AssemblyOp.BEQ.flagPositive)  // Branch when flag is SET
        assertTrue(AssemblyOp.BNE.isBranch)
        assertTrue(!AssemblyOp.BNE.flagPositive)  // Branch when flag is CLEAR
    }

    @Test
    fun `assembly op - modifies flags`() {
        // LDA modifies A, N, Z
        val ldaModifies = AssemblyOp.LDA.modifies(AssemblyAddressing.Value::class)
        assertTrue(AssemblyAffectable.A in ldaModifies)
        assertTrue(AssemblyAffectable.Negative in ldaModifies)
        assertTrue(AssemblyAffectable.Zero in ldaModifies)
    }

    @Test
    fun `assembly op - reads flags`() {
        // BEQ reads Zero flag
        val beqReads = AssemblyOp.BEQ.reads(null)
        assertTrue(AssemblyAffectable.Zero in beqReads)

        // ADC reads Carry flag
        val adcReads = AssemblyOp.ADC.reads(AssemblyAddressing.Value::class)
        assertTrue(AssemblyAffectable.Carry in adcReads)
    }

    // =====================================================================
    // ADDRESSING MODE toString() ROUND-TRIP
    // =====================================================================

    @Test
    fun `toString round-trip - ByteValue`() {
        val addr = AssemblyAddressing.ByteValue(0x42.toUByte(), AssemblyAddressing.Radix.Hex)
        assertEquals("#\$42", addr.toString())
    }

    @Test
    fun `toString round-trip - Direct with offset`() {
        val addr = AssemblyAddressing.Direct("Label", 5)
        assertEquals("Label+5", addr.toString())
    }

    @Test
    fun `toString round-trip - Direct with negative offset`() {
        val addr = AssemblyAddressing.Direct("Label", -3)
        assertEquals("Label-3", addr.toString())
    }

    @Test
    fun `toString round-trip - DirectX`() {
        val addr = AssemblyAddressing.DirectX("Label", 0)
        assertEquals("Label,X", addr.toString())
    }

    @Test
    fun `toString round-trip - IndirectY`() {
        val addr = AssemblyAddressing.IndirectY("Label", 0)
        assertEquals("(Label),Y", addr.toString())
    }
}
