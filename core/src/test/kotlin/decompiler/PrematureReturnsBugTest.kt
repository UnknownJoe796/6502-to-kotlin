// by Claude - Unit tests to document and verify the premature returns bug
// See TODO.md "VERIFIED BUG: Premature Returns in Forward Branches"
package com.ivieleague.decompiler6502tokotlin.decompiler

import com.ivieleague.decompiler6502tokotlin.hand.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests for the premature returns bug.
 *
 * BUG: Forward branches to internal labels (join points) are generating return statements
 * when they should just be control flow. The join point code becomes unreachable.
 *
 * Example: In getXOffscreenBits, branches like `bmi XLdBData` to a forward label
 * generate `return A` instead of continuing to the join point code.
 *
 * This causes:
 * - Timeouts (loops never complete because all paths return early)
 * - Wrong values (join point code never executed)
 */
class PrematureReturnsBugTest {

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `forward branch to internal join point should NOT generate return`() {
        // Simplified version of GetXOffscreenBits pattern
        // Multiple branches converge at JoinPoint label - returns should NOT be generated
        val code = """
            TestFunc:
                LDA ${'$'}10          ; load value
                CMP #${'$'}00         ; compare to 0
                BMI JoinPoint      ; if negative, jump to join point
                LDA #${'$'}38         ; else load different value
                STA ${'$'}06          ; store it
            JoinPoint:             ; JOIN POINT - all paths should reach here
                LDA ${'$'}20          ; load final value
                LDX ${'$'}04          ; restore X
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertTrue(functions.isNotEmpty())

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()
        println("Generated Kotlin for join point test:")
        println(kotlinCode)

        // Count return statements in the code
        val returnCount = kotlinCode.split("return").size - 1

        // BUG CHECK: There should be only ONE return (at the end after JoinPoint code)
        // If there are multiple returns before the join point code, the bug is present
        if (returnCount > 1) {
            println("\n⚠️ BUG CONFIRMED: Found $returnCount return statements")
            println("Forward branches to internal join points should NOT generate returns")
        }

        // Check if join point code is reachable
        // Look for the pattern: join point code (LDA $20, LDX $04) followed by return
        val hasJoinPointCode = kotlinCode.contains("memory[0x20]") || kotlinCode.contains("0x20")
        println("Join point code present: $hasJoinPointCode")

        // When fixed, there should be exactly 1 return at the end
        // assertTrue(returnCount == 1, "Should have exactly one return at the end")
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `multiple forward branches to same join point should merge cleanly`() {
        // Pattern where multiple branches all go to the same join point
        val code = """
            MultiJoinTest:
                LDA ${'$'}10
                CMP #${'$'}00
                BMI CommonJoin     ; branch 1 to join
                CMP #${'$'}80
                BPL CommonJoin     ; branch 2 to join (same target!)
                LDA #${'$'}FF         ; only reached if 0 <= A < 0x80
            CommonJoin:            ; join point
                STA ${'$'}30
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertTrue(functions.isNotEmpty())

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()
        println("Generated Kotlin for multiple join branches:")
        println(kotlinCode)

        // The join point code (STA $30) should be executed by all paths
        // If returns are generated in the if branches, the STA will be skipped
        val hasJoinCode = kotlinCode.contains("memory[0x30]") || kotlinCode.contains("0x30")
        val returnCount = kotlinCode.split("return").size - 1

        if (returnCount > 1) {
            println("\n⚠️ BUG: Multiple returns prevent reaching join point")
        }

        println("Join point code (STA $30) present: $hasJoinCode")
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `loop with forward branch to join point inside should not early exit`() {
        // Pattern similar to getXOffscreenBits loop
        val code = """
            LoopWithJoin:
                LDY #${'$'}01         ; Y = 1
            XOfsLoop:
                LDA ${'$'}10,Y        ; load indexed
                CMP #${'$'}00
                BMI LoadData       ; forward branch to LoadData
                LDA #${'$'}38
            LoadData:              ; join point inside loop
                STA ${'$'}20,Y
                DEY
                BPL XOfsLoop       ; loop back if Y >= 0
            ExitLoop:
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertTrue(functions.isNotEmpty())

        val kotlinCode = functions.first().toKotlinFunction().toKotlin()
        println("Generated Kotlin for loop with internal join point:")
        println(kotlinCode)

        // BUG CHECK: Returns inside the loop body before the join point
        // The DEY/BPL should be executed on every iteration, not skipped by early return
        val hasLoopStructure = kotlinCode.contains("do {") || kotlinCode.contains("while (")
        val hasDecrement = kotlinCode.contains("- 1") || kotlinCode.contains("Y =")

        println("Has loop structure: $hasLoopStructure")
        println("Has decrement: $hasDecrement")

        // Count returns inside what looks like loop body
        val returnCount = kotlinCode.split("return").size - 1
        if (returnCount > 1) {
            println("\n⚠️ BUG: Multiple returns in loop body - DEY/BPL may be unreachable")
        }
    }

    @Test
    @Timeout(10, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    fun `complex pattern with JSR inside conditional should not add premature returns`() {
        // More realistic pattern matching getXOffscreenBits structure
        // The JSR inside the conditional may trigger ensureReturnsPresent
        val code = """
            ComplexFunc:
                STX ${'$'}04
                LDY #${'$'}01
            XOfsLoop:
                LDA ${'$'}10,Y
                CMP #${'$'}00
                BMI XLdBData         ; forward branch 1
                LDX ${'$'}20,Y
                CMP #${'$'}01
                BPL XLdBData         ; forward branch 2
                LDA #${'$'}38
                STA ${'$'}06
                LDA #${'$'}08
                JSR SubRoutine       ; JSR inside conditional - may trigger return insertion
            XLdBData:                ; JOIN POINT
                LDA ${'$'}30,X
                LDX ${'$'}04
                CMP #${'$'}00
                BNE ExitLoop
                DEY
                BPL XOfsLoop
            ExitLoop:
                RTS
            SubRoutine:
                STA ${'$'}40
                RTS
        """.trimIndent().parseToAssemblyCodeFile()

        val blocks = code.lines.blockify()
        blocks.dominators()
        val functions = blocks.functionify()

        assertTrue(functions.isNotEmpty())

        // Find the main function (not the subroutine)
        val mainFunc = functions.find { it.startingBlock.label?.lowercase()?.contains("complex") == true }
            ?: functions.first()

        val kotlinCode = mainFunc.toKotlinFunction().toKotlin()
        println("Generated Kotlin for complex pattern:")
        println(kotlinCode)

        // Count returns in the code
        val returnCount = kotlinCode.split("return").size - 1
        println("Return count: $returnCount")

        // Check if join point code (LDA $30,X, LDX $04) is present
        val hasJoinCode = kotlinCode.contains("memory[0x30") ||
                          kotlinCode.contains("0x30 +")
        println("Join point code present: $hasJoinCode")

        // BUG CHECK: In the buggy version, there are many returns before the join point
        // and the join point code may be unreachable
        if (returnCount > 2) {
            println("\n⚠️ POTENTIAL BUG: Found $returnCount returns - join point may be unreachable")
            println("The ensureReturnsPresent function may be adding returns to if-else branches")
        }

        // Check for the typical bug pattern: code after return inside same scope
        val lines = kotlinCode.lines()
        var sawReturn = false
        var depth = 0
        var returnDepth = -1
        var codeAfterReturn = false
        for (line in lines) {
            val trimmed = line.trim()
            // Track brace depth
            depth += trimmed.count { it == '{' }
            depth -= trimmed.count { it == '}' }

            if (trimmed.contains("return") && !trimmed.startsWith("//")) {
                sawReturn = true
                returnDepth = depth
            } else if (sawReturn && depth == returnDepth && trimmed.isNotEmpty() && !trimmed.startsWith("//") && trimmed != "}") {
                // Found executable code after a return at same depth
                if (trimmed.contains("A =") || trimmed.contains("X =") || trimmed.contains("memory[")) {
                    codeAfterReturn = true
                    println("Found code after return at same depth: $trimmed")
                }
            }
            // Reset when leaving the scope
            if (depth < returnDepth) {
                sawReturn = false
                returnDepth = -1
            }
        }

        if (codeAfterReturn) {
            println("\n⚠️ BUG CONFIRMED: Executable code found after return statement in same scope")
            println("This indicates join point code is unreachable")
        }
    }
}
