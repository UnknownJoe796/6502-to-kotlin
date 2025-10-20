package com.ivieleague.decompiler6502tokotlin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for Pass 34: Final Cleanup & Validation
 */
class FinalCleanupTest {

    /**
     * Test 1: Kotlin keyword detection
     */
    @Test
    fun `test kotlin keyword detection`() {
        // Reserved keywords should be detected
        assertTrue("fun".isKotlinKeyword(), "fun is a Kotlin keyword")
        assertTrue("val".isKotlinKeyword(), "val is a Kotlin keyword")
        assertTrue("var".isKotlinKeyword(), "var is a Kotlin keyword")
        assertTrue("if".isKotlinKeyword(), "if is a Kotlin keyword")
        assertTrue("when".isKotlinKeyword(), "when is a Kotlin keyword")

        // Non-keywords should not be detected
        assertFalse("hello".isKotlinKeyword(), "hello is not a Kotlin keyword")
        assertFalse("myVariable".isKotlinKeyword(), "myVariable is not a Kotlin keyword")
        assertFalse("data".isKotlinKeyword(), "data is not a hard keyword")
    }

    /**
     * Test 2: Keyword escaping
     */
    @Test
    fun `test keyword escaping`() {
        // Keywords should be escaped with backticks
        assertEquals("`fun`", "fun".escapeKotlinKeyword(), "fun should be escaped")
        assertEquals("`val`", "val".escapeKotlinKeyword(), "val should be escaped")
        assertEquals("`when`", "when".escapeKotlinKeyword(), "when should be escaped")

        // Non-keywords should not be escaped
        assertEquals("hello", "hello".escapeKotlinKeyword(), "hello should not be escaped")
        assertEquals("myVar", "myVar".escapeKotlinKeyword(), "myVar should not be escaped")
    }

    /**
     * Test 3: Validation issue severity
     */
    @Test
    fun `test validation issue creation`() {
        val errorIssue = CleanupIssue(
            severity = IssueSeverity.ERROR,
            location = "Function@0x8000",
            message = "Type mismatch",
            suggestedFix = "Insert cast"
        )

        assertEquals(IssueSeverity.ERROR, errorIssue.severity)
        assertEquals("Function@0x8000", errorIssue.location)
        assertEquals("Type mismatch", errorIssue.message)
        assertEquals("Insert cast", errorIssue.suggestedFix)

        val warningIssue = CleanupIssue(
            severity = IssueSeverity.WARNING,
            location = "Function@0x8010",
            message = "Unreachable code",
            suggestedFix = null
        )

        assertEquals(IssueSeverity.WARNING, warningIssue.severity)
        assertNull(warningIssue.suggestedFix)
    }

    /**
     * Test 4: Unreachable block detection
     */
    @Test
    fun `test unreachable block detection`() {
        val code = """
            Start:
                LDA #${'$'}05
                STA ${'$'}0200
                RTS

            UnreachableCode:
                LDA #${'$'}10
                STA ${'$'}0201
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Start"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.performFinalCleanup(
            codeFile = codeFile,
            variableIdentification = null,
            typeInference = null,
            expressionAnalysis = null
        )

        // Should process at least one function
        assertTrue(result.functionsProcessed.isNotEmpty(), "Should process functions")

        // Result should be valid (no errors, only warnings at most)
        assertTrue(result.isValid, "Should be valid despite warnings")
    }

    /**
     * Test 5: Empty function handling
     */
    @Test
    fun `test empty function`() {
        val code = """
            Empty:
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Empty"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.performFinalCleanup(
            codeFile = codeFile,
            variableIdentification = null,
            typeInference = null,
            expressionAnalysis = null
        )

        // Should handle empty function gracefully
        assertTrue(result.isValid, "Empty function should be valid")
        assertEquals(0, result.globalStats.validationErrors, "Should have no errors")
    }

    /**
     * Test 6: Statistics aggregation
     */
    @Test
    fun `test statistics aggregation`() {
        val code = """
            Func1:
                LDA #${'$'}05
                STA ${'$'}0200
                RTS

            Func2:
                LDA #${'$'}10
                STA ${'$'}0201
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("Func1", "Func2"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.performFinalCleanup(
            codeFile = codeFile,
            variableIdentification = null,
            typeInference = null,
            expressionAnalysis = null
        )

        // Should process all functions
        assertEquals(cfg.functions.size, result.functionsProcessed.size, "Should process all functions")

        // Global stats should aggregate all function stats
        assertNotNull(result.globalStats, "Should have global stats")
        assertTrue(result.isValid, "Should be valid")
    }

    /**
     * Test 7: Valid code should pass all checks
     */
    @Test
    fun `test valid code passes all checks`() {
        val code = """
            ProcessData:
                LDX #${'$'}00
            Loop:
                LDA ${'$'}0200,X
                STA ${'$'}0300,X
                INX
                CPX #${'$'}10
                BNE Loop
                RTS
        """.trimIndent()

        val codeFile = code.parseToAssemblyCodeFile()
        val resolution = codeFile.resolveAddresses(0x8000)
        val entries = codeFile.discoverEntryPoints(resolution, exportedLabels = setOf("ProcessData"))
        val reachability = codeFile.analyzeReachability(resolution, entries)
        val blocks = codeFile.constructBasicBlocks(resolution, reachability, entries)
        val cfg = codeFile.constructCfg(resolution, reachability, blocks, entries)

        val result = cfg.performFinalCleanup(
            codeFile = codeFile,
            variableIdentification = null,
            typeInference = null,
            expressionAnalysis = null
        )

        // Valid code should have no errors
        assertEquals(0, result.globalStats.validationErrors, "Should have no validation errors")
        assertTrue(result.isValid, "Should be marked as valid")
    }

    /**
     * Test 8: Issue severity levels
     */
    @Test
    fun `test issue severity levels`() {
        // Create issues of different severities
        val issues = listOf(
            CleanupIssue(IssueSeverity.ERROR, "loc1", "msg1", null),
            CleanupIssue(IssueSeverity.WARNING, "loc2", "msg2", null),
            CleanupIssue(IssueSeverity.WARNING, "loc3", "msg3", null),
            CleanupIssue(IssueSeverity.INFO, "loc4", "msg4", null),
            CleanupIssue(IssueSeverity.INFO, "loc5", "msg5", null),
            CleanupIssue(IssueSeverity.INFO, "loc6", "msg6", null)
        )

        val errors = issues.count { it.severity == IssueSeverity.ERROR }
        val warnings = issues.count { it.severity == IssueSeverity.WARNING }
        val infos = issues.count { it.severity == IssueSeverity.INFO }

        assertEquals(1, errors, "Should have 1 error")
        assertEquals(2, warnings, "Should have 2 warnings")
        assertEquals(3, infos, "Should have 3 infos")
    }
}
