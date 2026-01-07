# Decompiler Testing Guide

This guide explains how to use the function state capture system to validate and improve the decompiler.

## Overview

The testing system captures function input/output states from the **interpreter** running a TAS (tool-assisted speedrun), then generates unit tests that verify the **decompiled Kotlin functions** produce identical behavior.

```
┌─────────────────┐     ┌──────────────────┐     ┌─────────────────┐
│  6502 ROM +     │ ──► │  Interpreter     │ ──► │  Captured       │
│  TAS Inputs     │     │  with Tracer     │     │  Function States│
└─────────────────┘     └──────────────────┘     └─────────────────┘
                                                          │
                                                          ▼
┌─────────────────┐     ┌──────────────────┐     ┌─────────────────┐
│  Test Results   │ ◄── │  Run Generated   │ ◄── │  Generated      │
│  Pass/Fail      │     │  Kotlin Tests    │     │  Test File      │
└─────────────────┘     └──────────────────┘     └─────────────────┘
```

## Quick Start

### 1. Capture Function States

Run the capture test to generate test data from a TAS:

```bash
./gradlew :core:test --tests "CaptureFromTASTest.capture function states from TAS"
```

This produces:
- `local/testgen/captured-tests-happylee-warps.json` - Raw capture data
- `local/testgen/GeneratedFunctionTests.kt` - Generated Kotlin tests

### 2. Review Capture Statistics

The test output shows which functions were captured:
```
Top 20 most-called functions:
  0x9BE1: 3398 calls, 10 unique inputs -> 10 tests
  0xC047: 2904 calls, 9 unique inputs -> 9 tests
  ...
```

### 3. Map Function Addresses to Names

Use the SMB disassembly (`smbdism.asm`) to find function names:
```bash
grep -n "^[A-Za-z].*:" smbdism.asm | grep -i "8182\|9BE1\|C047"
```

## File Structure

### Capture Infrastructure

| File | Purpose |
|------|---------|
| `core/src/main/kotlin/testgen/FunctionCallCapture.kt` | Data classes: `CpuState`, `FunctionCallCapture` |
| `core/src/main/kotlin/testgen/FunctionCallTracer.kt` | Hooks into interpreter to capture JSR/RTS |
| `core/src/main/kotlin/testgen/TestCaseSelector.kt` | Deduplication and sampling logic |
| `core/src/main/kotlin/testgen/TestDataSerialization.kt` | JSON I/O for captures |
| `core/src/main/kotlin/testgen/KotlinTestGenerator.kt` | Generates Kotlin test code |
| `core/src/test/kotlin/testgen/CaptureFromTASTest.kt` | Main capture test |

### Decompiler Components

| File | Purpose |
|------|---------|
| `core/src/main/kotlin/pass-*.kt` | Decompiler passes (parsing, analysis, codegen) |
| `core/src/main/kotlin/kotlin-ast.kt` | Kotlin AST representation |
| `core/src/main/kotlin/kotlin-codegen.kt` | Kotlin code generation |
| `core/src/main/kotlin/runtime-support.kt` | Runtime support (memory, registers, flags) |

### Outputs

| File | Purpose |
|------|---------|
| `outputs/smb-decompiled.kt` | Generated decompiled SMB code |
| `local/testgen/*.json` | Captured test data |
| `local/testgen/*.kt` | Generated test files |

## Workflow: Fixing a Decompiled Function

### Step 1: Identify a Failing Function

From capture statistics, pick a function to fix. Start with frequently-called functions for maximum impact.

Example: Function at `0x8182` was captured 511 times with 2 unique input states.

### Step 2: Find the Function in Disassembly

```bash
# Find the label at address 8182
grep -n "^.*8182" smbdism.asm
```

### Step 3: Find the Decompiled Version

```bash
# Search for the function in decompiled output
grep -n "func_8182\|8182" outputs/smb-decompiled.kt
```

### Step 4: Create a Focused Test

Extract test cases for this specific function from the JSON:

```kotlin
// In a new test file
@Test
fun `test func_8182 case 1`() {
    // From captured data:
    // Input: A=0, X=1, Y=0, flagZ=true, memory[0x0770]=0x00
    // Expected output: flagN=true, flagZ=false
    
    resetCPU()
    clearMemory()
    A = 0; X = 1; Y = 0
    flagZ = true
    memory[0x0770] = 0x00u
    
    func_8182()
    
    assertEquals(true, flagN)
    assertEquals(false, flagZ)
}
```

### Step 5: Analyze the Original Assembly

Read the assembly code at that address to understand what it should do:

```asm
; Example: Function checks OperMode and sets flags
func_8182:
    LDA $0770      ; Load OperMode
    BEQ .done      ; If zero, skip
    ; ... more code
.done:
    RTS
```

### Step 6: Fix the Decompiler

Common issues to look for in the decompiler passes:

1. **Flag handling** (`pass-*-expression-*.kt`): N/Z/C/V flags not updated correctly
2. **Addressing modes** (`pass-1-parsing.kt`): Incorrect memory address calculation
3. **Control flow** (`pass-*-control-flow-*.kt`): Branches not translated correctly
4. **Stack operations**: Push/pull not paired correctly

### Step 7: Re-run and Verify

After fixing the decompiler:

```bash
# Regenerate decompiled code
./gradlew :core:test --tests "SMBGoldenOutputTest"

# Re-run the specific test
./gradlew test --tests "test func_8182*"
```

## Understanding Captured Data

### CpuState

```kotlin
data class CpuState(
    val A: Int,      // Accumulator (0-255)
    val X: Int,      // X index register (0-255)  
    val Y: Int,      // Y index register (0-255)
    val SP: Int,     // Stack pointer (0-255, stack at $0100+SP)
    val PC: Int,     // Program counter
    val flagN: Boolean,  // Negative (bit 7 of result)
    val flagV: Boolean,  // Overflow
    val flagZ: Boolean,  // Zero
    val flagC: Boolean,  // Carry
    val flagI: Boolean,  // Interrupt disable
    val flagD: Boolean   // Decimal mode (not used on NES)
)
```

### FunctionCallCapture

```kotlin
data class FunctionCallCapture(
    val functionAddress: Int,     // Entry point (e.g., 0x8182)
    val frame: Int,               // TAS frame number
    val timestamp: Long,          // Instruction count
    val callerAddress: Int,       // Address of JSR instruction
    val callDepth: Int,           // Nesting level (0 = top-level)
    val inputState: CpuState,     // CPU state at function entry
    val memoryReads: Map<Int, Int>,   // Addresses read -> values
    val outputState: CpuState,    // CPU state at RTS
    val memoryWrites: Map<Int, Int>,  // Addresses written -> values
    val nestedCalls: List<Int>,   // Addresses of nested JSR calls
    val inputStateHash: Long      // Hash for deduplication
)
```

### Key Insights from Captures

- **memoryReads**: These are the function's inputs. Set these up before calling.
- **memoryWrites**: These are the function's outputs. Verify these after calling.
- **nestedCalls**: Shows which other functions this one calls (helps understand dependencies).
- **callDepth**: 0 means called from NMI handler; higher means nested subroutine.

## Customizing the Capture

### Capture Specific Functions Only

Edit `CaptureFromTASTest.kt`:

```kotlin
val targetFunctions = setOf(
    0x8182,  // OperMode check
    0x9BE1,  // Frequently called
    0xC047,  // Another target
)
val tracer = FunctionCallTracer(interp, targetFunctions = targetFunctions)
```

### Capture More/Fewer Frames

```kotlin
val maxFrames = 3000  // Increase for more coverage
```

### Adjust Test Case Selection

```kotlin
val selector = TestCaseSelector(
    maxTestsPerFunction = 20,  // More tests per function
    minUniqueStates = 1        // Include functions with only 1 unique input
)
```

## Common Decompiler Issues

### 1. Missing Flag Updates

**Symptom**: Tests fail on flag assertions (flagN, flagZ, flagC, flagV)

**Cause**: The decompiled code doesn't update flags after operations

**Fix**: Check `kotlin-codegen.kt` for flag update generation after arithmetic/logic operations

### 2. Incorrect Memory Addressing

**Symptom**: Tests fail because wrong memory locations are read/written

**Cause**: Indexed addressing modes (e.g., `LDA $0200,X`) not translated correctly

**Fix**: Check addressing mode handling in expression generation

### 3. Branch Condition Inversion

**Symptom**: Control flow goes wrong direction

**Cause**: Branch conditions (BEQ, BNE, etc.) translated with wrong polarity

**Fix**: Check branch translation in control flow analysis passes

### 4. Stack Imbalance

**Symptom**: SP (stack pointer) differs between expected and actual

**Cause**: Push/pull operations not properly paired in decompiled code

**Fix**: Check stack operation translation

## Runtime Support Requirements

The generated tests expect these functions/variables in the runtime:

```kotlin
// From runtime-support.kt
var A: Int        // Accumulator
var X: Int        // X register
var Y: Int        // Y register  
var SP: Int       // Stack pointer
var flagN: Boolean
var flagV: Boolean
var flagZ: Boolean
var flagC: Boolean
var flagI: Boolean
var flagD: Boolean
val memory: UByteArray  // 64KB memory

fun resetCPU()     // Reset all registers and flags
fun clearMemory()  // Zero all memory
```

## Tips for Efficient Debugging

1. **Start with simple functions**: Functions with few memory reads/writes are easier to debug
2. **Check the assembly first**: Understand what the original code does before fixing
3. **Use the interpreter as oracle**: Run the same inputs through `BinaryInterpreter6502` to verify expected behavior
4. **Focus on high-frequency functions**: Fixing a function called 3000 times has more impact than one called once
5. **Look for patterns**: If multiple functions fail the same way, the bug is likely in a shared decompiler pass

## Example: Full Debug Session

```bash
# 1. Run capture
./gradlew :core:test --tests "CaptureFromTASTest.capture function states from TAS"

# 2. Find most-called function with failures
cat local/testgen/captured-tests-happylee-warps.json | jq '.functions | to_entries | sort_by(-.value.totalCalls) | .[0:5]'

# 3. Look up function in disassembly
grep -B5 -A20 "^.*9BE1" smbdism.asm

# 4. Create minimal test
# (copy test case from generated file, add to new test class)

# 5. Run and analyze failure
./gradlew test --tests "MyDebugTest" --info

# 6. Fix decompiler, regenerate, re-test
./gradlew :core:test --tests "SMBGoldenOutputTest"
./gradlew test --tests "MyDebugTest"
```
