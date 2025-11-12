# Integration Testing Framework

This document describes the integration testing framework for validating that the 6502-to-Kotlin decompiler produces correct translations.

## Overview

The integration testing framework verifies decompiler correctness by:

1. **Executing 6502 code through the interpreter** with various initial states
2. **Generating Kotlin code** from the same 6502 instructions
3. **Comparing the behavior** of both executions to ensure they match

This approach validates that the translation is semantically correct.

## Architecture

### Core Components

#### 1. `IntegrationTest.kt`
Basic integration tests for individual instruction sequences.

**Key Features:**
- `CPUState` data class: Captures complete processor state (registers, flags, memory)
- `TestContext`: Holds test configuration and execution logic
- Random state generation for property-based testing
- Multiple test cases covering different instruction types

**Example Test:**
```kotlin
@Test
fun testSimpleLoad() {
    val test = testSequence(
        "Simple Load",
        AssemblyInstruction(
            AssemblyOp.LDA,
            AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)
        )
    )

    val finalState = test.executeInInterpreter()
    assertEquals(0x42u, finalState.A)
}
```

#### 2. `FunctionIntegrationTest.kt`
Integration tests for complete functions from the SMB disassembly.

**Key Features:**
- Tests real functions from `smbdism.asm`
- Executes complete function bodies through the interpreter
- Validates behavior across multiple initial states
- Generates Kotlin code for entire functions

**Example Test:**
```kotlin
@Test
fun testDecTimersWithRandomStates() {
    val random = Random(54321)

    repeat(5) { i ->
        val initialState = CPUState.random(random)
        val execution = executeFunction("DecTimers", initialState)

        // Verify function executed and produced deterministic results
        assert(execution.instructionsExecuted > 0)
    }
}
```

#### 3. `TranslationValidator.kt`
Utility framework for comparing interpreter execution against translated code.

**Key Features:**
- `TestRunner`: Executes instructions and generates Kotlin code
- `ComparisonResult`: Reports differences between executions
- Random state validation with configurable test counts
- Quick test helpers for ad-hoc validation

**Example Usage:**
```kotlin
// Quick test of a single instruction
TranslationValidator.quickTest(
    AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.ByteValue(0x42u))
)

// Test with multiple random states
val runner = TranslationValidator.testInstructions(
    AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.ByteValue(0x42u)),
    AssemblyInstruction(AssemblyOp.TAX)
)
val results = runner.validateWithRandomStates(count = 10)
```

## Running the Tests

### Run All Integration Tests
```bash
./gradlew test --tests "IntegrationTest"
./gradlew test --tests "FunctionIntegrationTest"
```

### Run Specific Test
```bash
./gradlew test --tests "IntegrationTest.testSimpleLoad"
./gradlew test --tests "FunctionIntegrationTest.testDecTimersWithRandomStates"
```

### Run with Verbose Output
```bash
./gradlew test --tests "IntegrationTest" --info
```

## Test Categories

### 1. Instruction-Level Tests
Test individual 6502 instructions or small sequences.

**Coverage:**
- Load/Store operations (LDA, LDX, LDY, STA, STX, STY)
- Arithmetic (ADC, SBC, INC, DEC, INX, INY, DEX, DEY)
- Logical operations (AND, ORA, EOR, BIT)
- Register transfers (TAX, TAY, TXA, TYA, TSX, TXS)
- Stack operations (PHA, PLA, PHP, PLP)
- Shifts and rotations (ASL, LSR, ROL, ROR)
- Comparisons (CMP, CPX, CPY)
- Branches (BEQ, BNE, BCS, BCC, etc.)

**Example:**
```kotlin
@Test
fun testArithmeticAddition() {
    val test = testSequence(
        "Addition",
        AssemblyInstruction(AssemblyOp.LDA, AssemblyAddressing.ByteValue(0x10u)),
        AssemblyInstruction(AssemblyOp.ADC, AssemblyAddressing.ByteValue(0x20u)),
        initialState = CPUState(C = false)
    )

    val finalState = test.executeInInterpreter()
    assertEquals(0x30u, finalState.A)
}
```

### 2. Function-Level Tests
Test complete functions from real code (SMB disassembly).

**Functions Tested:**
- `DecTimers`: Timer decrement loop
- `RotPRandomBit`: Pseudo-random bit generator
- `InitBuffer`: VRAM buffer initialization
- And more...

**Example:**
```kotlin
@Test
fun testSmallFunctionEndToEnd() {
    val func = SMBTestFixtures.loadFunction("RotPRandomBit")
    val execution = executeFunction("RotPRandomBit", initialState)

    // Verify execution
    assert(execution.instructionsExecuted > 0)

    // Generate and verify Kotlin code
    val kotlinCode = generateKotlinForFunction("RotPRandomBit")
    assert(kotlinCode.contains("fun rotPRandomBit"))
}
```

### 3. Random State Testing
Property-based testing with randomized initial states.

**Purpose:**
- Verify behavior is consistent regardless of initial state
- Catch edge cases and corner conditions
- Test instruction sequences with many input combinations

**Example:**
```kotlin
@Test
fun testRandomizedStates() {
    val random = Random(12345)

    repeat(10) { i ->
        val initialState = CPUState.random(random)
        val execution = executeFunction("MyFunc", initialState)

        // Verify expected invariants hold for any initial state
        verifyInvariants(execution)
    }
}
```

## CPUState API

The `CPUState` data class captures complete processor state:

```kotlin
data class CPUState(
    val A: UByte = 0u,              // Accumulator
    val X: UByte = 0u,              // X register
    val Y: UByte = 0u,              // Y register
    val SP: UByte = 0xFFu,          // Stack pointer
    val PC: UShort = 0u,            // Program counter
    val N: Boolean = false,          // Negative flag
    val V: Boolean = false,          // Overflow flag
    val Z: Boolean = false,          // Zero flag
    val C: Boolean = false,          // Carry flag
    val I: Boolean = false,          // Interrupt disable
    val D: Boolean = false,          // Decimal mode
    val memory: Map<Int, UByte> = emptyMap()  // Memory snapshot
)
```

### Factory Methods

```kotlin
// Create random state
val state = CPUState.random()

// Create random state with fixed seed
val state = CPUState.random(Random(12345))

// Capture current state from interpreter
val state = CPUState.capture(cpu, memory, addressesToTrack)

// Apply state to interpreter
state.applyTo(cpu, memory)
```

## Writing New Tests

### Template for Instruction Test

```kotlin
@Test
fun testMyInstruction() {
    val test = testSequence(
        "Test Description",
        AssemblyInstruction(AssemblyOp.XXX, ...),
        initialState = CPUState(...),
        memoryAddresses = listOf(0x1000),
        labelResolver = { label -> ... }
    )

    val finalState = test.executeInInterpreter()

    // Assertions
    assertEquals(expectedValue, finalState.A)

    // Print generated code
    println(test.generateKotlinCode())
}
```

### Template for Function Test

```kotlin
@Test
fun testMyFunction() {
    val initialState = CPUState(
        A = 0x10u,
        memory = mapOf(0x100 to 0x42u)
    )

    val execution = executeFunction("MyFunc", initialState)

    // Verify execution
    assert(execution.instructionsExecuted > 0)

    // Verify results
    assertEquals(expectedValue, execution.finalState.A)

    // Generate Kotlin code
    val kotlinCode = generateKotlinForFunction("MyFunc")
    println(kotlinCode)
}
```

### Template for Random State Test

```kotlin
@Test
fun testWithRandomStates() {
    val random = Random(12345)

    repeat(10) { i ->
        val initialState = CPUState.random(random)

        val execution = executeFunction("MyFunc", initialState)

        // Verify determinism
        val execution2 = executeFunction("MyFunc", initialState)
        assertEquals(execution.finalState, execution2.finalState)
    }
}
```

## Validation Strategy

### Current Status
✅ **Phase 1 (Complete):** Interpreter execution and state capture
- Execute 6502 instructions through interpreter
- Capture and compare CPU states
- Generate Kotlin code from instructions

🚧 **Phase 2 (In Progress):** Kotlin code generation
- Convert instructions to Kotlin AST
- Generate complete function bodies
- Handle control flow structures

⏳ **Phase 3 (Planned):** Kotlin code execution
- Dynamically execute generated Kotlin code
- Compare interpreter results vs. Kotlin execution results
- Automated validation of translation correctness

### Future Enhancements

#### 1. Dynamic Kotlin Execution
```kotlin
// Use kotlin-scripting-jvm to execute generated code
val engine = KotlinJsr223JvmLocalScriptEngineFactory().scriptEngine
val result = engine.eval(generatedKotlinCode)
```

#### 2. Automated Differential Testing
```kotlin
// Automatically test all SMB functions with random states
fun testAllFunctions() {
    val allFunctions = SMBTestFixtures.allFunctions

    for (func in allFunctions) {
        val results = validateFunction(func.name, testCount = 100)
        assert(results.all { it.matches })
    }
}
```

#### 3. Coverage Metrics
```kotlin
// Track instruction coverage across tests
fun calculateCoverage(): CoverageReport {
    val allInstructions = AssemblyOp.values()
    val testedInstructions = getTestedInstructions()

    return CoverageReport(
        total = allInstructions.size,
        covered = testedInstructions.size,
        percentage = (testedInstructions.size * 100.0) / allInstructions.size
    )
}
```

#### 4. Regression Test Suite
```kotlin
// Save test cases that found bugs for regression testing
@Test
fun testRegressions() {
    val regressionCases = loadRegressionCases()

    for (case in regressionCases) {
        val result = validateCase(case)
        assert(result.matches) { "Regression: ${case.description}" }
    }
}
```

## Debugging Failed Tests

### 1. Print State Differences
```kotlin
val result = runner.compare(initialState)
result.printReport()  // Shows detailed differences
```

### 2. Step Through Interpreter
```kotlin
val interp = Interpreter6502()
initialState.applyTo(interp.cpu, interp.memory)

for (instruction in instructions) {
    println("Before: A=${interp.cpu.A}, X=${interp.cpu.X}")
    interp.executeInstruction(instruction)
    println("After: A=${interp.cpu.A}, X=${interp.cpu.X}")
}
```

### 3. Compare Generated Kotlin
```kotlin
val kotlinCode = runner.generateKotlinCode()
println("Generated Kotlin:")
println(kotlinCode)

// Manually verify the generated code looks correct
```

### 4. Test with Specific States
```kotlin
// Instead of random states, test specific edge cases
val edgeCases = listOf(
    CPUState(A = 0x00u),      // Zero
    CPUState(A = 0xFFu),      // Maximum
    CPUState(A = 0x7Fu),      // Positive max (signed)
    CPUState(A = 0x80u),      // Negative (signed)
    CPUState(C = true),       // Carry set
    CPUState(Z = true)        // Zero flag set
)

for (state in edgeCases) {
    validateState(state)
}
```

## Contributing

When adding new tests:

1. **Start small:** Test individual instructions before complex sequences
2. **Use random states:** Add random state testing to catch edge cases
3. **Test real code:** Include tests for actual SMB functions
4. **Document expectations:** Clearly comment what behavior you're testing
5. **Print generated code:** Always output the generated Kotlin for manual review

## References

- Main interpreter: `src/main/kotlin/interpreter/Interpreter6502.kt`
- Code generation: `src/main/kotlin/kotlin-codegen.kt`
- Test fixtures: `src/test/kotlin/stages/SMBTestFixtures.kt`
- SMB disassembly: `smbdism.asm`
