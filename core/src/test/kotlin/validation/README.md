# Decompiler Validation Framework

This framework validates that the decompiler produces Kotlin code that is semantically equivalent to the original 6502 assembly by comparing execution results.

## Architecture

### Core Concept

```
6502 Assembly
    ├─> Interpreter Execution ──> Execution State A
    └─> Decompilation to Kotlin ──> Execution State B
                                         │
                                         ├─> Compare States
                                         └─> Assert Equality
```

### Components

1. **ExecutionState** (`DecompilerValidationFramework.kt`)
   - Captures a snapshot of execution state
   - Includes: registers (A, X, Y), flags (N, V, Z, C, I, D), memory
   - Can compare two states with configurable ignore lists
   - Extracted from interpreter or decompiled code execution

2. **DecompilerTestCase** (`DecompilerValidationFramework.kt`)
   - Specification for a test case
   - Includes: assembly source, initial memory/register setup, addresses to check
   - Supports ignoring specific flags or registers in comparison

3. **DecompilerValidator** (`DecompilerValidationFramework.kt`)
   - Orchestrates test execution
   - Runs assembly through interpreter
   - Runs decompiled Kotlin code (TODO: integration needed)
   - Compares resulting states

4. **DSL Builder** (`decompilerTest` function)
   - Fluent API for creating test cases
   - Makes tests readable and maintainable

## Current Status

✅ **Completed:**
- Framework structure and interfaces
- Interpreter execution path
- State capture and comparison
- 10 validation test cases demonstrating various 6502 operations

⏳ **TODO:**
- Integrate actual decompilation pipeline
- Add Kotlin code compilation/execution
- Expand test coverage to all 6502 instructions
- Add tests for complex control flow (loops, conditionals)

## Test Cases

The framework currently includes tests for:

1. **Simple Load/Store** - Basic LDA/STA operations
2. **Arithmetic with Carry** - ADC with overflow
3. **Comparison** - CMP instruction and flag behavior
4. **Indexed Addressing** - X/Y register indexing
5. **Loop with Decrement** - Iterative summing
6. **Bit Manipulation** - AND, ORA, EOR operations
7. **Transfer Operations** - Register-to-register transfers
8. **Stack Operations** - PHA/PLA
9. **Shift Operations** - ASL/LSR
10. **Multi-byte Addition** - 16-bit arithmetic with carry

## Usage Example

### Basic Test

```kotlin
@Test
fun testMyOperation() {
    val instructions = listOf(
        AssemblyInstruction(AssemblyOp.LDA,
            AssemblyAddressing.ByteValue(0x42u, AssemblyAddressing.Radix.Hex)),
        AssemblyInstruction(AssemblyOp.STA,
            AssemblyAddressing.Direct("\$1000"))
    )

    val interp = Interpreter6502()
    for (inst in instructions) {
        interp.executeInstruction(inst)
    }

    val state = ExecutionState.fromInterpreter(interp, listOf(0x1000))
    assertEquals(0x42u, state.registerA)
    assertEquals(0x42u, state.memory[0x1000])
}
```

### Using the DSL (Future)

```kotlin
val testCase = decompilerTest {
    name("Simple Addition")
    assembly("""
        LDA #$10
        CLC
        ADC #$20
        STA $1000
    """)
    setupMemory(0x2000, 0x05u)
    setupRegister("X", 0x10u)
    checkMemoryAt(0x1000)
    checkMemoryAt(0x1001)
    ignoreFlag("I")  // Don't care about interrupt flag
}

DecompilerValidator().validate(testCase, instructions)
```

## Integration Roadmap

### Phase 1: Interpreter Validation (CURRENT)
- ✅ Execute assembly through interpreter
- ✅ Capture and compare states
- ✅ Demonstrate framework with test cases

### Phase 2: Decompiler Integration (NEXT)
- Parse assembly to IR
- Run decompilation passes
- Generate Kotlin AST
- Convert AST to Kotlin source code

### Phase 3: Kotlin Execution
- Compile generated Kotlin (kotlinc or script engine)
- Execute with same initial state as interpreter
- Capture execution results
- Full end-to-end validation

### Phase 4: Advanced Testing
- Test complete functions/subroutines
- Test control flow (if/while/for)
- Test cross-function calls
- Test edge cases and corner cases

## State Comparison Details

### What Gets Compared

**Always:**
- All 8-bit registers (A, X, Y)
- All memory addresses specified in `checkMemory`

**Configurable:**
- Processor flags (N, V, Z, C, I, D)
- Individual registers (can be ignored)

### Why Ignore Flags/Registers?

Some operations may set flags differently even if the core logic is equivalent. For example:
- Decompiled code might not set the I (interrupt) flag the same way
- Different code paths might leave unused registers in different states
- Temporary calculations might affect flags differently

## Future Enhancements

1. **Assembly Parser Integration**
   - Full assembly syntax support
   - Label resolution
   - Constant definitions

2. **Memory State Diffing**
   - Show exactly what changed
   - Highlight unexpected differences
   - Memory dump visualization

3. **Performance Testing**
   - Compare instruction counts
   - Measure decompiled code performance
   - Optimize generated code

4. **Coverage Analysis**
   - Track which 6502 instructions are tested
   - Identify missing test cases
   - Generate coverage reports

5. **Fuzzing**
   - Generate random valid 6502 programs
   - Automatically validate decompilation
   - Find edge cases

## Running Tests

```bash
# Run all validation tests
./gradlew test --tests "DecompilerValidationTest"

# Run specific test
./gradlew test --tests "DecompilerValidationTest.testSimpleLoadStore"

# Run all interpreter tests (including validation)
./gradlew test --tests "*interpreter.*"
```

## Contributing

When adding new validation tests:

1. Start with the interpreter-only version to verify logic
2. Add corresponding decompilation test once integrated
3. Use descriptive test names
4. Document expected behavior
5. Include edge cases (overflow, underflow, wrapping)
6. Test both success and failure paths
