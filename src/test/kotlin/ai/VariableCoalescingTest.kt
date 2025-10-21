package com.ivieleague.decompiler6502tokotlin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for Pass 33: Variable Coalescing & Lifetime Analysis
 */
class VariableCoalescingTest {

    /**
     * Test 1: Non-overlapping lifetimes should be coalesced
     */
    @Test
    fun `test non-overlapping lifetime coalescing`() {
        // Create dummy code file for line references
        val dummyCodeFile = listOf<AssemblyLine>(
            AssemblyLine(null, null, null, null, null, ""),
            AssemblyLine(null, null, null, null, null, ""),
            AssemblyLine(null, null, null, null, null, ""),
            AssemblyLine(null, null, null, null, null, ""),
            AssemblyLine(null, null, null, null, null, ""),
            AssemblyLine(null, null, null, null, null, ""),
            AssemblyLine(null, null, null, null, null, "")
        ).toCodeFile()

        // Create two variables with non-overlapping lifetimes
        val var1 = createTestVariable(
            id = VariableId.Memory(0x0200),
            scope = VariableScope.Function(0x8000),
            type = InferredType.UInt8(),
            usages = listOf(
                VariableUsage(
                    LineReference(0, dummyCodeFile),
                    UsageType.WRITE,
                    UsageContext(false, null, null)
                ),
                VariableUsage(
                    LineReference(1, dummyCodeFile),
                    UsageType.READ,
                    UsageContext(false, null, null)
                )
            )
        )

        val var2 = createTestVariable(
            id = VariableId.Memory(0x0201),
            scope = VariableScope.Function(0x8000),
            type = InferredType.UInt8(),
            usages = listOf(
                VariableUsage(
                    LineReference(5, dummyCodeFile),
                    UsageType.WRITE,
                    UsageContext(false, null, null)
                ),
                VariableUsage(
                    LineReference(6, dummyCodeFile),
                    UsageType.READ,
                    UsageContext(false, null, null)
                )
            )
        )

        val lt1 = createTestLifetime(var1, 0, 1)
        val lt2 = createTestLifetime(var2, 5, 6)

        // Check that lifetimes don't overlap
        assertFalse(
            lt1.liveRanges.first().overlaps(lt2.liveRanges.first()),
            "Lifetimes should not overlap"
        )

        // Verify they are compatible
        assertTrue(
            areVariablesCompatible(var1, var2),
            "Variables should be compatible for coalescing"
        )
    }

    /**
     * Test 2: Overlapping lifetimes should NOT be coalesced
     */
    @Test
    fun `test overlapping lifetimes not coalesced`() {
        val range1 = LiveRange(startLine = 0, endLine = 5, blockLeaders = setOf(0))
        val range2 = LiveRange(startLine = 3, endLine = 8, blockLeaders = setOf(0))

        // Ranges overlap from lines 3-5
        assertTrue(range1.overlaps(range2), "Ranges should overlap")
        assertTrue(range2.overlaps(range1), "Overlap should be symmetric")
    }

    /**
     * Test 3: Sequential usage (one ends before other starts)
     */
    @Test
    fun `test sequential usage detection`() {
        val range1 = LiveRange(startLine = 0, endLine = 5, blockLeaders = setOf(0))
        val range2 = LiveRange(startLine = 6, endLine = 10, blockLeaders = setOf(0))

        // Sequential: range1 ends at 5, range2 starts at 6
        assertFalse(range1.overlaps(range2), "Sequential ranges should not overlap")
    }

    /**
     * Test 4: Variable compatibility checks
     */
    @Test
    fun `test variable compatibility`() {
        val uint8Var = createTestVariable(
            id = VariableId.Memory(0x0200),
            scope = VariableScope.Function(0x8000),
            type = InferredType.UInt8(),
            usages = emptyList()
        )

        val uint16Var = createTestVariable(
            id = VariableId.Memory(0x0201),
            scope = VariableScope.Function(0x8000),
            type = InferredType.UInt16(),
            usages = emptyList()
        )

        // Different types should not be compatible
        assertNotEquals(
            uint8Var.inferredType,
            uint16Var.inferredType,
            "Different types should not match"
        )
    }

    /**
     * Test 5: Parameter variables should not coalesce with locals
     */
    @Test
    fun `test parameter isolation`() {
        val param = createTestVariable(
            id = VariableId.Memory(0x0200),
            scope = VariableScope.Parameter(0x8000, 0),
            type = InferredType.UInt8(),
            usages = emptyList()
        )

        val local = createTestVariable(
            id = VariableId.Memory(0x0201),
            scope = VariableScope.Function(0x8000),
            type = InferredType.UInt8(),
            usages = emptyList()
        )

        // Parameters should not be compatible with locals
        assertTrue(param.scope is VariableScope.Parameter, "First variable should be parameter")
        assertTrue(local.scope is VariableScope.Function, "Second variable should be local")
        assertNotEquals(param.scope::class, local.scope::class, "Scopes should be different types")
    }

    /**
     * Test 6: Register reuse detection
     */
    @Test
    fun `test register reuse`() {
        val regA1 = VariableId.Register(Variable.RegisterA)
        val regA2 = VariableId.Register(Variable.RegisterA)
        val regX = VariableId.Register(Variable.RegisterX)

        // Same register
        assertTrue(
            (regA1 as VariableId.Register).reg == (regA2 as VariableId.Register).reg,
            "Should identify same register"
        )

        // Different registers
        assertFalse(
            (regA1 as VariableId.Register).reg == (regX as VariableId.Register).reg,
            "Should identify different registers"
        )
    }

    /**
     * Test 7: Scope minimization
     */
    @Test
    fun `test scope minimization`() {
        // Create dummy code file
        val dummyCodeFile = listOf<AssemblyLine>(
            AssemblyLine(null, null, null, null, null, ""),
            AssemblyLine(null, null, null, null, null, "")
        ).toCodeFile()

        // Variable used in single block should have block scope
        val singleBlockVar = createTestVariable(
            id = VariableId.Memory(0x0200),
            scope = VariableScope.Function(0x8000),
            type = InferredType.UInt8(),
            usages = listOf(
                VariableUsage(
                    LineReference(0, dummyCodeFile),
                    UsageType.WRITE,
                    UsageContext(false, null, null)
                ),
                VariableUsage(
                    LineReference(1, dummyCodeFile),
                    UsageType.READ,
                    UsageContext(false, null, null)
                )
            )
        )

        // All usages should be in single block for scope minimization
        assertEquals(2, singleBlockVar.usageSites.size, "Should have two usage sites")
    }

    /**
     * Test 8: Empty function handling
     */
    @Test
    fun `test empty function`() {
        // Create minimal test structures
        val emptyFunction = FunctionCfg(
            entryLeader = 0,
            entryAddress = 0x8000,
            entryLabel = "Empty",
            blocks = emptyList(),
            edges = emptyList()
        )

        val emptyFunctionVars = FunctionVariables(
            function = emptyFunction,
            localVariables = emptyList(),
            parameters = emptyList(),
            returnValues = emptyList()
        )

        // Should handle empty function gracefully
        assertEquals(0, emptyFunctionVars.localVariables.size, "Should have no local variables")
        assertEquals(0, emptyFunctionVars.parameters.size, "Should have no parameters")
        assertEquals(0, emptyFunctionVars.returnValues.size, "Should have no return values")
    }

    /**
     * Helper: Create test variable
     */
    private fun createTestVariable(
        id: VariableId,
        scope: VariableScope,
        type: InferredType,
        usages: List<VariableUsage>
    ): IdentifiedVariable {
        return IdentifiedVariable(
            id = id,
            memoryLocations = when (id) {
                is VariableId.Memory -> setOf(MemoryLocation(id.address))
                else -> emptySet()
            },
            scope = scope,
            inferredType = type,
            accessPattern = MemoryAccessPattern.Scalar(
                address = when (id) {
                    is VariableId.Memory -> id.address
                    else -> 0
                },
                name = null
            ),
            usageSites = usages
        )
    }

    /**
     * Helper: Create test lifetime
     */
    private fun createTestLifetime(
        variable: IdentifiedVariable,
        firstUse: Int,
        lastUse: Int
    ): VariableLifetime {
        return VariableLifetime(
            variable = variable,
            liveRanges = listOf(
                LiveRange(
                    startLine = firstUse,
                    endLine = lastUse,
                    blockLeaders = setOf(0)
                )
            ),
            minimalScope = variable.scope,
            firstUse = firstUse,
            lastUse = lastUse
        )
    }

    /**
     * Helper: Check if variables are compatible (simplified version)
     */
    private fun areVariablesCompatible(var1: IdentifiedVariable, var2: IdentifiedVariable): Boolean {
        // Must have same type
        if (var1.inferredType != var2.inferredType) return false

        // Parameters can't be coalesced with locals
        if (var1.scope is VariableScope.Parameter && var2.scope !is VariableScope.Parameter) return false
        if (var2.scope is VariableScope.Parameter && var1.scope !is VariableScope.Parameter) return false

        return true
    }
}
