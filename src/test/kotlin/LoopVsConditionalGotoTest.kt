package com.ivieleague.decompiler6502tokotlin.hand

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit

/**
 * Tests to distinguish between actual loops and conditional tail recursion / goto patterns.
 * Based on issues found in ProcessAreaData from SMB disassembly.
 */
class LoopVsConditionalGotoTest {

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `PreTest loop with backward conditional branch should be detected as PreTest not PostTest`() {
        // Pattern: while (x >= 0) { body; x--; }
        // This is a PreTest loop even though the condition is at the bottom
        val code = """
            loop_header:
              LDA counter
              BEQ exit
            loop_body:
              STA ${'$'}0200
              DEC counter
              BPL loop_header
            exit:
              RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertEquals(1, functions.size)

        val controls = functions[0].analyzeControls()
        
        val loopNodes = controls.filterIsInstance<LoopNode>()
        assertEquals(1, loopNodes.size, "Should detect exactly one loop")
        
        val loop = loopNodes[0]
        // This should be PreTest because entry is at loop_header with a forward exit condition
        assertEquals(LoopKind.PreTest, loop.kind, 
            "Loop with header condition should be PreTest, not PostTest")
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `conditional tail recursion should not be detected as PostTest loop`() {
        // Pattern similar to ProcessAreaData: function with conditional restart at end
        val code = """
            process_data:
              LDA #${'$'}05
              STA counter
            main_loop:
              DEC counter
              BPL main_loop
              ; After main loop, conditional restart
              LDA flag1
              BNE process_data
              LDA flag2
              BNE process_data
            done:
              RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertEquals(1, functions.size)

        val controls = functions[0].analyzeControls()
        
        val loopNodes = controls.filterIsInstance<LoopNode>()
        
        // Should have ONE loop (main_loop), NOT three loops
        assertTrue(loopNodes.size <= 2, 
            "Should have at most 2 loops (main loop + maybe outer retry), got ${loopNodes.size}")
        
        // The main_loop should be detected
        val mainLoop = loopNodes.find { it.header.label == "main_loop" }
        assertNotNull(mainLoop, "Should detect main_loop")
        
        // The conditional jumps back to process_data should NOT create separate PostTest loops
        val processDataLoops = loopNodes.filter { 
            it.kind == LoopKind.PostTest && it.header.label == "process_data" 
        }
        assertTrue(processDataLoops.size <= 1, 
            "Should not create multiple PostTest loops for conditional tail recursion")
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `actual ProcessAreaData pattern from SMB`() {
        // Simplified version of the ProcessAreaData pattern
        val code = """
            ProcessAreaData:
              LDX #${'$'}02
            ProcADLoop:
              STX offset
              LDA data,X
              CMP #${'$'}FD
              BEQ RdyDecode
              LDA flag,X
              BMI ChkLength
            RdyDecode:
              STA temp
            ChkLength:
              LDX offset
              LDA length,X
              BMI ProcLoopb
              DEC length,X
            ProcLoopb:
              DEX
              BPL ProcADLoop
              ; Post-loop: conditional restart
              LDA behind_flag
              BNE ProcessAreaData
              LDA backload_flag
              BNE ProcessAreaData
            EndAParse:
              RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertEquals(1, functions.size)

        val controls = functions[0].analyzeControls()
        
        val loopNodes = controls.filterIsInstance<LoopNode>()
        
        // Should detect the main ProcADLoop but not create spurious loops
        assertTrue(loopNodes.isNotEmpty(), "Should detect at least the main loop")
        
        // The main loop should be from ProcADLoop with BPL condition
        val mainLoop = loopNodes.find { 
            it.header.label == "ProcADLoop" || it.header.label == "ProcessAreaData"
        }
        assertNotNull(mainLoop, "Should detect the main ProcADLoop")
        
        // Should NOT have 3+ separate PostTest loops
        assertTrue(loopNodes.size <= 2, 
            "Should not detect 3+ loops (got ${loopNodes.size}). " +
            "Loops detected: ${loopNodes.map { "${it.kind} at ${it.header.label}" }}")
        
        // Verify we don't have massive block duplication in loop bodies
        loopNodes.forEach { loop ->
            val bodyBlockCount = loop.body.size
            assertTrue(bodyBlockCount < 20, 
                "Loop body should not have ${bodyBlockCount} blocks - indicates incorrect analysis")
        }
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `loop with self-branch at bottom is PostTest (do-while semantics)`() {
        // Pattern: loop_start: body; test; branch_back_if_true loop_start
        // This has do-while semantics (body executes at least once)
        val code = """
            func:
              LDX #${'$'}05
            loop_start:
              STX temp
              LDA data,X
              STA result
              DEX
              BPL loop_start
            after_loop:
              RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertEquals(1, functions.size)

        val controls = functions[0].analyzeControls()

        val loopNodes = controls.filterIsInstance<LoopNode>()
        assertEquals(1, loopNodes.size, "Should detect exactly one loop")

        val loop = loopNodes[0]
        // Self-branch with bottom test is PostTest (do-while) not PreTest
        // Body executes at least once before test
        assertEquals(LoopKind.PostTest, loop.kind,
            "Self-looping block with bottom test has do-while (PostTest) semantics")
        assertEquals("loop_start", loop.header.label,
            "Loop header should be loop_start")
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `true PostTest loop should still be detected correctly`() {
        // Make sure we don't break actual PostTest loop detection
        val code = """
            loop_start:
              DEC counter
              LDA counter
              BNE loop_start
            done:
              RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()
        assertEquals(1, functions.size)

        val controls = functions[0].analyzeControls()
        
        val loopNodes = controls.filterIsInstance<LoopNode>()
        assertEquals(1, loopNodes.size, "Should detect exactly one loop")
        
        val loop = loopNodes[0]
        assertEquals(LoopKind.PostTest, loop.kind, 
            "Simple do-while should still be detected as PostTest")
    }
}
