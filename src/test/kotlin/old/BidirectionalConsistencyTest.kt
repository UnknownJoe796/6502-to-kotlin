package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue

/**
 * Tests that bidirectional property delegates maintain graph consistency.
 */
class BidirectionalConsistencyTest {

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `blockify maintains bidirectional consistency`() {
        val code = """
            Start:
                LDA #${'$'}00
                BEQ Target
                LDA #${'$'}01
            Target:
                RTS
        """.trimIndent()

        val blocks = code.parseToAssemblyCodeFile().lines.blockify()

        // Validate all blocks have consistent references
        blocks.validateAllConsistency()
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `dominator analysis maintains bidirectional consistency`() {
        val code = """
            Entry:
                LDA #${'$'}00
                BEQ Branch1
            MainPath:
                LDX #${'$'}01
                JMP Merge
            Branch1:
                LDX #${'$'}02
            Merge:
                RTS
        """.trimIndent()

        val blocks = code.parseToAssemblyCodeFile().lines.blockify()
        blocks.dominators()

        // Validate dominator tree is consistent
        blocks.validateAllConsistency()
    }

    @Test
    @Timeout(30, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `SMB disassembly maintains bidirectional consistency throughout analysis`() {
        val asmFile = File("smbdism.asm")
        if (!asmFile.exists()) {
            println("Skipping SMB test - smbdism.asm not found")
            return
        }

        val code = asmFile.readText().parseToAssemblyCodeFile()

        // After blockify
        val blocks = code.lines.blockify()
        blocks.validateAllConsistency()

        // After dominator analysis
        blocks.dominators()
        blocks.validateAllConsistency()

        // After functionify
        val functions = blocks.functionify()
        blocks.validateAllConsistency()

        assertTrue(functions.isNotEmpty(), "Should have found functions")
    }

    @Test
    fun `setting fallThroughExit automatically updates enteredFrom`() {
        val code = """
            A:
                NOP
            B:
                NOP
            C:
                RTS
        """.trimIndent()

        val blocks = code.parseToAssemblyCodeFile().lines.blockify()

        val blockA = blocks.find { it.label == "A" }!!
        val blockB = blocks.find { it.label == "B" }!!

        // Verify bidirectional link was created
        assertTrue(blockA.fallThroughExit == blockB)
        assertTrue(blockB.enteredFrom.contains(blockA))
    }

    @Test
    fun `setting branchExit automatically updates enteredFrom`() {
        val code = """
            Start:
                LDA #${'$'}00
                BEQ Target
            NotTaken:
                NOP
            Target:
                RTS
        """.trimIndent()

        val blocks = code.parseToAssemblyCodeFile().lines.blockify()

        val start = blocks.find { it.label == "Start" }!!
        val target = blocks.find { it.label == "Target" }!!

        // Verify bidirectional link was created
        assertTrue(start.branchExit == target)
        assertTrue(target.enteredFrom.contains(start))
    }

    @Test
    fun `setting immediateDominator automatically updates dominates`() {
        val code = """
            Entry:
                LDA #${'$'}00
            Child:
                RTS
        """.trimIndent()

        val blocks = code.parseToAssemblyCodeFile().lines.blockify()
        blocks.dominators()

        val entry = blocks.find { it.label == "Entry" }!!
        val child = blocks.find { it.label == "Child" }!!

        // Verify bidirectional dominator link
        assertTrue(child.immediateDominator == entry)
        assertTrue(entry.dominates.contains(child))
    }
}
