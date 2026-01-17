# Test Generation Infrastructure - Improvement Plan

*by Claude*

## Current State Analysis

### Statistics
- **1956 tests generated** from 287 functions
- **802 tests failing (41%)** - indicates decompiler bugs (expected)
- **1154 tests passing (59%)** - confirms infrastructure is working
- Coverage: ~287 of ~400+ unique function addresses captured

### Architecture Summary
The current system has two capture approaches:

1. **JSR-based (FunctionCallTracer)**: Captures ALL function calls by hooking JSR/RTS
2. **PC-based (PCBasedFunctionTracer)**: Captures ONLY target functions by PC matching

Both approaches:
- Track memory reads (inputs) and writes (outputs)
- Handle NMI context isolation
- Deduplicate by input state hash
- Generate Kotlin test code with assertions

---

## Identified Issues

### 1. Input State Hash May Miss Important Variations
**Current**: Hash includes `functionAddress + A + X + Y + SP + flags + memoryReads`

**Problem**: Two calls with identical hash but different outputs indicate:
- The hash misses some input that affects behavior
- Could be: caller context, previous operations, timing-dependent state

**Impact**: Tests may not cover all meaningful scenarios

### 2. Only Memory Outputs Are Verified
**Current**: Tests only check `assertEquals(expected, memory[addr])`

**Missing**:
- CPU register outputs (A, X, Y) - often the "return value"
- Flag outputs (N, Z, C, V) - critical for branch decisions
- Stack pointer changes

**Impact**: Functions could produce correct memory but wrong return values

### 3. Fall-Through Functions Are Unreliable
**Current**: Skip list has only 2 functions (`drawFirebar`, `doNothing2`)

**Problem**: Many more functions are fall-through targets. Their captured data includes operations from preceding code.

**Impact**: Tests verify wrong behavior

### 4. No Separation of "Pure" vs "Impure" Functions
**Issue**: Some functions are pure (same inputs = same outputs), others depend on:
- Interrupt state
- Frame timing
- RNG state
- Previous function side effects

**Impact**: Hard to isolate and test impure functions

### 5. Limited Failure Categorization
**Current**: Tests fail with generic assertion errors

**Missing**:
- Timeout (infinite loop) vs wrong output
- Single address mismatch vs many mismatches
- "Close" failures (off by 1) vs completely wrong

**Impact**: Hard to prioritize which decompiler bugs to fix first

---

## Improvement Proposals

### Phase 1: Better Test Quality (Quick Wins)

#### 1.1 Add Register Output Verification
Modify `KotlinTestGenerator.generateTestMethod()` to also verify:
```kotlin
// Current: only memory
assertEquals(0xABu, memory[0x1234])

// Improved: also registers
assertEquals(0xABu, A, "Register A mismatch")
assertEquals(0xCDu, X, "Register X mismatch")
assertEquals(0xEFu, Y, "Register Y mismatch")
```

**Effort**: Low (just add more assertions)
**Value**: High (catches "wrong return value" bugs)

#### 1.2 Add Flag Output Verification
Add assertions for flags that matter:
```kotlin
assertEquals(true, flagZ, "Zero flag mismatch")
assertEquals(false, flagC, "Carry flag mismatch")
```

**Effort**: Low
**Value**: Medium (helps debug branch condition issues)

#### 1.3 Categorize Test Failures
Create a test report that groups failures:
- `TIMEOUT`: Function didn't return (infinite loop)
- `REGISTER_MISMATCH`: Output registers wrong
- `MEMORY_MISMATCH_SINGLE`: One address wrong
- `MEMORY_MISMATCH_MANY`: Many addresses wrong
- `STACK_OVERFLOW`: Kotlin recursion too deep

**Effort**: Medium (needs custom test listener)
**Value**: High (prioritizes decompiler fixes)

### Phase 2: Better Coverage

#### 2.1 Identify All Fall-Through Targets
Analyze `smbdism.asm` to find labels that:
- Have no JSR references (internal labels)
- Are preceded by code that doesn't end in RTS/JMP

Add these to skip list or generate special tests.

**Effort**: Medium
**Value**: Medium (removes unreliable tests)

#### 2.2 Input State Hash Improvements
Include more state in the hash:
- Previous instruction's address (caller context)
- Zero-page memory (commonly shared state)
- Specific global variables (e.g., `OperMode`, `GameEngineSubroutine`)

**Effort**: Medium
**Value**: Medium (better deduplication)

#### 2.3 Multi-Call Sequence Testing
Some bugs only appear in sequences. Generate tests like:
```kotlin
@Test
fun `functionA then functionB sequence`() {
    setupInputA()
    functionA()
    // Don't clear memory
    functionB()
    verifyOutputB()
}
```

**Effort**: High
**Value**: Medium (catches interaction bugs)

### Phase 3: Structural Improvements

#### 3.1 Differential Testing Mode
Instead of capturing "expected" values, run both:
- Original interpreter
- Decompiled Kotlin

Compare outputs in real-time to find divergence.

```kotlin
class DifferentialTest {
    fun runComparison(functionName: String, inputs: TestInputs) {
        val interpResult = runInterpreter(inputs)
        val kotlinResult = runDecompiled(inputs)
        assertStatesEqual(interpResult, kotlinResult)
    }
}
```

**Effort**: High
**Value**: Very High (precise divergence detection)

#### 3.2 Minimal Failing Input Reduction
When a test fails, try to find the minimal inputs that cause failure:
- Remove memory reads one at a time
- See which are actually necessary for the bug

**Effort**: High
**Value**: High (easier debugging)

#### 3.3 Function Purity Analysis
Classify functions as:
- **Pure**: Output depends only on explicit inputs
- **State-dependent**: Reads global state
- **NMI-affected**: Behavior changes if NMI interrupts
- **Random**: Uses RNG

Generate appropriate test strategies for each category.

**Effort**: High
**Value**: High (correct testing strategy per function)

---

## Recommended Implementation Order

1. **Quick wins (1.1, 1.2, 1.3)** - Immediate value, low effort
2. **Fall-through detection (2.1)** - Removes noise from test results
3. **Input hash improvements (2.2)** - Better test diversity
4. **Differential testing (3.1)** - Best for finding decompiler bugs
5. **Minimal input reduction (3.2)** - Helps fix specific bugs
6. **Sequence testing (2.3)** - Catches interaction bugs
7. **Purity analysis (3.3)** - Improves overall architecture

---

## Implementation Details

### 1.1 Register Output Verification

Location: `KotlinTestGenerator.kt:generateTestMethod()`

Change:
```kotlin
// After executing function, verify outputs
sb.appendLine("${indent}// Verify: Check output registers")
sb.appendLine("${indent}assertEquals(0x${testCase.outputState.A.toString(16).uppercase().padStart(2, '0')}u.toUByte(), A, \"Register A mismatch\")")
sb.appendLine("${indent}assertEquals(0x${testCase.outputState.X.toString(16).uppercase().padStart(2, '0')}u.toUByte(), X, \"Register X mismatch\")")
sb.appendLine("${indent}assertEquals(0x${testCase.outputState.Y.toString(16).uppercase().padStart(2, '0')}u.toUByte(), Y, \"Register Y mismatch\")")
```

### 2.1 Fall-Through Detection

Create new analysis pass:
```kotlin
fun findFallThroughTargets(asmLines: List<AssemblyLine>): Set<String> {
    val fallThroughs = mutableSetOf<String>()

    for (i in 1 until asmLines.size) {
        val current = asmLines[i]
        val previous = asmLines[i - 1]

        // If current line has a label
        if (current.label != null) {
            // And previous line doesn't end control flow
            val prevInstr = previous.instruction
            if (prevInstr != null && !prevInstr.op.endsControlFlow) {
                fallThroughs.add(current.label)
            }
        }
    }

    return fallThroughs
}
```

### 3.1 Differential Testing

New file: `DifferentialTester.kt`
```kotlin
class DifferentialTester(
    private val interpreter: BinaryInterpreter6502,
    private val decompiledFunctions: Map<String, (Int, Int, Int) -> Unit>
) {
    data class Divergence(
        val functionName: String,
        val frame: Int,
        val inputState: CpuState,
        val memoryInputs: Map<Int, Int>,
        val interpreterOutput: CpuState,
        val kotlinOutput: CpuState,
        val interpreterMemory: Map<Int, Int>,
        val kotlinMemory: Map<Int, Int>
    )

    fun findDivergences(maxFrames: Int): List<Divergence> {
        // Run TAS frame by frame
        // Before each function call:
        // 1. Snapshot interpreter state
        // 2. Run interpreter version
        // 3. Snapshot interpreter result
        // 4. Reset to snapshot
        // 5. Run Kotlin version
        // 6. Compare results
    }
}
```

---

## Metrics to Track

After implementing improvements, track:

1. **Test count by category**
   - Passing
   - Failing (timeout)
   - Failing (register mismatch)
   - Failing (memory mismatch)
   - Skipped (fall-through)

2. **Coverage metrics**
   - Functions with tests / Total functions
   - Unique input states tested / Total unique states captured
   - Lines of decompiled code covered (requires instrumentation)

3. **Decompiler quality trend**
   - Pass rate over time
   - Functions fixed per week

---

## Next Steps

1. Implement Phase 1 quick wins
2. Run tests and analyze failure distribution
3. Prioritize decompiler fixes based on failure categories
4. Implement Phase 2 as coverage gaps become apparent
5. Consider Phase 3 for systematic bug hunting

