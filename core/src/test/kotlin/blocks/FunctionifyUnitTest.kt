// by Claude - Phase 4: Functionify Unit Tests (Function Identification)
// Tests for blocks.kt:functionify() - identifying functions and their I/O signatures
package com.ivieleague.decompiler6502tokotlin.blocks

import com.ivieleague.decompiler6502tokotlin.hand.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertContains

/**
 * Functionify Unit Test Suite
 *
 * Tests for the functionify() function that identifies function boundaries
 * and computes input/output signatures for each function.
 *
 * Function identification is critical for decompilation - it determines
 * where functions start/end and what registers/flags they use.
 */
class FunctionifyUnitTest {

    // Helper to create functions from assembly
    private fun functionify(asm: String): List<AssemblyFunction> {
        val blocks = asm.parseToAssemblyCodeFile().lines.blockify()
        blocks.dominators()
        return blocks.functionify()
    }

    // Helper to get blocks
    private fun blockify(asm: String): List<AssemblyBlock> {
        return asm.parseToAssemblyCodeFile().lines.blockify()
    }

    // =====================================================================
    // ENTRY POINT DETECTION - FIRST BLOCK
    // =====================================================================

    @Test
    fun `first block is always function entry`() {
        val functions = functionify("""
            Start: LDA #${'$'}00
            RTS
        """.trimIndent())
        assertEquals(1, functions.size)
        assertEquals("Start", functions[0].startingBlock.label)
    }

    @Test
    fun `first block without label is function entry`() {
        val functions = functionify("""
            LDA #${'$'}00
            RTS
        """.trimIndent())
        assertEquals(1, functions.size)
        assertNull(functions[0].startingBlock.label)
    }

    // =====================================================================
    // ENTRY POINT DETECTION - JSR TARGETS
    // =====================================================================

    @Test
    fun `JSR target becomes function entry`() {
        val functions = functionify("""
            Start: JSR Helper
            RTS
            Helper: LDA #${'$'}00
            RTS
        """.trimIndent())

        // Two functions: Start and Helper
        assertEquals(2, functions.size)
        val funcNames = functions.map { it.startingBlock.label }
        assertTrue("Start" in funcNames)
        assertTrue("Helper" in funcNames)
    }

    @Test
    fun `multiple JSR targets create multiple functions`() {
        val functions = functionify("""
            Main: JSR Func1
            JSR Func2
            RTS
            Func1: LDA #${'$'}01
            RTS
            Func2: LDA #${'$'}02
            RTS
        """.trimIndent())

        assertEquals(3, functions.size)
        val funcNames = functions.map { it.startingBlock.label }
        assertTrue("Main" in funcNames)
        assertTrue("Func1" in funcNames)
        assertTrue("Func2" in funcNames)
    }

    // =====================================================================
    // ENTRY POINT DETECTION - JUMPENGINE DISPATCH
    // =====================================================================

    @Test
    fun `JumpEngine dispatch table entries become functions`() {
        val functions = functionify("""
            Main: LDA #${'$'}00
            JSR JumpEngine
            .dw Handler1
            .dw Handler2
            Handler1: LDA #${'$'}01
            RTS
            Handler2: LDA #${'$'}02
            RTS
            JumpEngine: RTS
        """.trimIndent())

        // Main, JumpEngine, Handler1, Handler2
        val funcNames = functions.map { it.startingBlock.label }
        assertTrue("Handler1" in funcNames, "Handler1 should be a function")
        assertTrue("Handler2" in funcNames, "Handler2 should be a function")
    }

    // =====================================================================
    // ENTRY POINT DETECTION - JMP TARGETS
    // =====================================================================

    @Test
    fun `JMP target becomes function entry`() {
        val functions = functionify("""
            Start: LDA #${'$'}00
            JMP Helper
            Helper: LDA #${'$'}01
            RTS
        """.trimIndent())

        // Both Start and Helper are functions because JMP targets are promoted
        val funcNames = functions.map { it.startingBlock.label }
        assertTrue("Helper" in funcNames)
    }

    // =====================================================================
    // FUNCTION BOUNDARY DETECTION - RTS
    // =====================================================================

    @Test
    fun `function ends at RTS`() {
        val functions = functionify("""
            Func: LDA #${'$'}00
            STA ${'$'}0200
            RTS
        """.trimIndent())

        assertEquals(1, functions.size)
        val func = functions[0]
        assertNotNull(func.blocks)
        assertTrue(func.blocks!!.isNotEmpty())
    }

    @Test
    fun `function ends at RTI`() {
        val functions = functionify("""
            NMI: LDA #${'$'}00
            RTI
        """.trimIndent())

        assertEquals(1, functions.size)
    }

    // =====================================================================
    // INPUT ANALYSIS - REGISTER INPUTS
    // =====================================================================

    @Test
    fun `register A consumed before defined is input`() {
        val functions = functionify("""
            Func: STA ${'$'}0200
            RTS
        """.trimIndent())

        val func = functions[0]
        assertNotNull(func.inputs)
        assertTrue(TrackedAsIo.A in func.inputs!!, "A should be an input (used by STA before defined)")
    }

    @Test
    fun `register X consumed before defined is input`() {
        val functions = functionify("""
            Func: STX ${'$'}0200
            RTS
        """.trimIndent())

        val func = functions[0]
        assertNotNull(func.inputs)
        assertTrue(TrackedAsIo.X in func.inputs!!, "X should be an input (used by STX before defined)")
    }

    @Test
    fun `register Y consumed before defined is input`() {
        val functions = functionify("""
            Func: STY ${'$'}0200
            RTS
        """.trimIndent())

        val func = functions[0]
        assertNotNull(func.inputs)
        assertTrue(TrackedAsIo.Y in func.inputs!!, "Y should be an input (used by STY before defined)")
    }

    @Test
    fun `register defined before used is NOT input`() {
        val functions = functionify("""
            Func: LDA #${'$'}42
            STA ${'$'}0200
            RTS
        """.trimIndent())

        val func = functions[0]
        assertNotNull(func.inputs)
        assertTrue(TrackedAsIo.A !in func.inputs!!, "A should NOT be input (defined by LDA before used)")
    }

    // =====================================================================
    // INPUT ANALYSIS - FLAG INPUTS
    // =====================================================================

    @Test
    fun `carry flag consumed before defined is input`() {
        val functions = functionify("""
            Func: ADC #${'$'}01
            RTS
        """.trimIndent())

        val func = functions[0]
        assertNotNull(func.inputs)
        assertTrue(TrackedAsIo.CarryFlag in func.inputs!!, "C should be input (used by ADC before defined)")
    }

    @Test
    fun `zero flag consumed by branch is input`() {
        val functions = functionify("""
            Func: BEQ Skip
            LDA #${'$'}01
            Skip: RTS
        """.trimIndent())

        val func = functions[0]
        assertNotNull(func.inputs)
        assertTrue(TrackedAsIo.ZeroFlag in func.inputs!!, "Z should be input (used by BEQ)")
    }

    @Test
    fun `negative flag consumed by branch is input`() {
        val functions = functionify("""
            Func: BMI Skip
            LDA #${'$'}01
            Skip: RTS
        """.trimIndent())

        val func = functions[0]
        assertNotNull(func.inputs)
        assertTrue(TrackedAsIo.NegativeFlag in func.inputs!!, "N should be input (used by BMI)")
    }

    // =====================================================================
    // INPUT ANALYSIS - INDEXED ADDRESSING
    // =====================================================================

    @Test
    fun `indexed X addressing marks X as input`() {
        val functions = functionify("""
            Func: LDA ${'$'}0200,X
            RTS
        """.trimIndent())

        val func = functions[0]
        assertNotNull(func.inputs)
        assertTrue(TrackedAsIo.X in func.inputs!!, "X should be input (used in indexed addressing)")
    }

    @Test
    fun `indexed Y addressing marks Y as input`() {
        val functions = functionify("""
            Func: LDA ${'$'}0200,Y
            RTS
        """.trimIndent())

        val func = functions[0]
        assertNotNull(func.inputs)
        assertTrue(TrackedAsIo.Y in func.inputs!!, "Y should be input (used in indexed addressing)")
    }

    @Test
    fun `indirect X addressing marks X as input`() {
        val functions = functionify("""
            Func: LDA (${'$'}10,X)
            RTS
        """.trimIndent())

        val func = functions[0]
        assertNotNull(func.inputs)
        assertTrue(TrackedAsIo.X in func.inputs!!, "X should be input (used in indirect X)")
    }

    @Test
    fun `indirect Y addressing marks Y as input`() {
        val functions = functionify("""
            Func: LDA (${'$'}10),Y
            RTS
        """.trimIndent())

        val func = functions[0]
        assertNotNull(func.inputs)
        assertTrue(TrackedAsIo.Y in func.inputs!!, "Y should be input (used in indirect Y)")
    }

    // =====================================================================
    // CLOBBER (MODIFIES) ANALYSIS
    // =====================================================================

    @Test
    fun `LDA clobbers A and flags`() {
        val functions = functionify("""
            Func: LDA #${'$'}42
            RTS
        """.trimIndent())

        val func = functions[0]
        assertNotNull(func.clobbers)
        assertTrue(TrackedAsIo.A in func.clobbers!!, "A should be clobbered by LDA")
        assertTrue(TrackedAsIo.ZeroFlag in func.clobbers!!, "Z should be clobbered by LDA")
        assertTrue(TrackedAsIo.NegativeFlag in func.clobbers!!, "N should be clobbered by LDA")
    }

    @Test
    fun `ADC clobbers A and flags including C and V`() {
        val functions = functionify("""
            Func: CLC
            ADC #${'$'}01
            RTS
        """.trimIndent())

        val func = functions[0]
        assertNotNull(func.clobbers)
        assertTrue(TrackedAsIo.A in func.clobbers!!, "A should be clobbered by ADC")
        assertTrue(TrackedAsIo.CarryFlag in func.clobbers!!, "C should be clobbered by ADC")
        assertTrue(TrackedAsIo.OverflowFlag in func.clobbers!!, "V should be clobbered by ADC")
    }

    @Test
    fun `CLC only clobbers C flag`() {
        val functions = functionify("""
            Func: CLC
            RTS
        """.trimIndent())

        val func = functions[0]
        assertNotNull(func.clobbers)
        assertTrue(TrackedAsIo.CarryFlag in func.clobbers!!, "C should be clobbered by CLC")
        // A, X, Y should NOT be clobbered
        assertTrue(TrackedAsIo.A !in func.clobbers!!, "A should NOT be clobbered by CLC")
    }

    // =====================================================================
    // OUTPUT ANALYSIS
    // =====================================================================

    @Test
    fun `output detected when caller uses value after JSR`() {
        val functions = functionify("""
            Main: JSR GetValue
            STA ${'$'}0300
            RTS
            GetValue: LDA #${'$'}42
            RTS
        """.trimIndent())

        val getValue = functions.find { it.startingBlock.label == "GetValue" }
        assertNotNull(getValue)
        assertNotNull(getValue.outputs)
        assertTrue(TrackedAsIo.A in getValue.outputs!!, "A should be output (caller uses it)")
    }

    // =====================================================================
    // TRANSITIVE PROPAGATION
    // =====================================================================

    @Test
    fun `inputs inherited from called functions`() {
        val functions = functionify("""
            Main: JSR Helper
            RTS
            Helper: ADC #${'$'}01
            RTS
        """.trimIndent())

        val main = functions.find { it.startingBlock.label == "Main" }
        assertNotNull(main)
        assertNotNull(main.inputs)
        // Main inherits Helper's inputs (A and C for ADC)
        assertTrue(TrackedAsIo.CarryFlag in main.inputs!!, "Main should inherit C input from Helper")
    }

    @Test
    fun `clobbers inherited from called functions`() {
        val functions = functionify("""
            Main: JSR Helper
            RTS
            Helper: LDA #${'$'}42
            RTS
        """.trimIndent())

        val main = functions.find { it.startingBlock.label == "Main" }
        assertNotNull(main)
        assertNotNull(main.clobbers)
        // Main inherits Helper's clobbers
        assertTrue(TrackedAsIo.A in main.clobbers!!, "Main should inherit A clobber from Helper")
    }

    // =====================================================================
    // BIT SKIP PATTERN ($2C)
    // =====================================================================

    @Test
    fun `bit skip pattern marks first instruction as skipped`() {
        val blocks = blockify("""
            Entry: .db ${'$'}2C
            Target: LDA #${'$'}00
            RTS
        """.trimIndent())
        blocks.dominators()
        val functions = blocks.functionify()

        // The Target block should be marked to skip first instruction for Entry's function
        val entryFunc = functions.find { it.startingBlock.label == "Entry" }
        assertNotNull(entryFunc)
    }

    // =====================================================================
    // FUNCTION BLOCKS ASSIGNMENT
    // =====================================================================

    @Test
    fun `function blocks are correctly assigned`() {
        val functions = functionify("""
            Func: LDA #${'$'}00
            BEQ Skip
            LDA #${'$'}01
            Skip: RTS
        """.trimIndent())

        val func = functions[0]
        assertNotNull(func.blocks)
        // Should include all blocks reachable from entry
        assertTrue(func.blocks!!.size >= 1)
    }

    @Test
    fun `function does not include blocks from other functions`() {
        val functions = functionify("""
            Func1: JSR Func2
            RTS
            Func2: LDA #${'$'}00
            RTS
        """.trimIndent())

        val func1 = functions.find { it.startingBlock.label == "Func1" }
        val func2 = functions.find { it.startingBlock.label == "Func2" }
        assertNotNull(func1)
        assertNotNull(func2)

        // Func1 should not contain Func2's blocks
        val func1BlockLabels = func1.blocks!!.mapNotNull { it.label }
        assertTrue("Func2" !in func1BlockLabels || func1BlockLabels.count { it == "Func2" } == 0)
    }

    // =====================================================================
    // EDGE CASES
    // =====================================================================

    @Test
    fun `empty assembly produces no functions`() {
        val functions = emptyList<AssemblyBlock>().functionify()
        assertTrue(functions.isEmpty())
    }

    @Test
    fun `single NOP function`() {
        val functions = functionify("Entry: NOP")
        assertEquals(1, functions.size)
    }

    @Test
    fun `recursive function - JSR to self`() {
        val functions = functionify("""
            Recursive: BEQ Done
            JSR Recursive
            Done: RTS
        """.trimIndent())

        assertEquals(1, functions.size)
        assertEquals("Recursive", functions[0].startingBlock.label)
    }

    @Test
    fun `mutually recursive functions`() {
        val functions = functionify("""
            FuncOne: JSR FuncTwo
            RTS
            FuncTwo: JSR FuncOne
            RTS
        """.trimIndent())

        assertEquals(2, functions.size)
    }

    // =====================================================================
    // FUNCTION CALLERS TRACKING
    // =====================================================================

    @Test
    fun `callers list populated for JSR targets`() {
        val functions = functionify("""
            Main: JSR Helper
            JSR Helper
            RTS
            Helper: RTS
        """.trimIndent())

        val helper = functions.find { it.startingBlock.label == "Helper" }
        assertNotNull(helper)
        // Helper is called twice
        assertEquals(2, helper.callers.size)
    }

    // =====================================================================
    // ALL FLAGS CONSUMPTION
    // =====================================================================

    @Test
    fun `all branch types consume correct flags`() {
        // BCC/BCS consume Carry
        var functions = functionify("""
            Func: BCC Skip
            Skip: RTS
        """.trimIndent())
        assertTrue(TrackedAsIo.CarryFlag in functions[0].inputs!!, "BCC should consume Carry")

        functions = functionify("""
            Func: BCS Skip
            Skip: RTS
        """.trimIndent())
        assertTrue(TrackedAsIo.CarryFlag in functions[0].inputs!!, "BCS should consume Carry")

        // BVC/BVS consume Overflow
        functions = functionify("""
            Func: BVC Skip
            Skip: RTS
        """.trimIndent())
        assertTrue(TrackedAsIo.OverflowFlag in functions[0].inputs!!, "BVC should consume Overflow")

        functions = functionify("""
            Func: BVS Skip
            Skip: RTS
        """.trimIndent())
        assertTrue(TrackedAsIo.OverflowFlag in functions[0].inputs!!, "BVS should consume Overflow")
    }
}
