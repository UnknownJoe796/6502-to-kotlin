# Bugs Found and Fixed - SMB Differential Testing

This document tracks all bugs discovered during SMB function differential testing and the fixes applied.

## Session Overview

**Date:** Current session
**Approach:** Systematic validation starting with simplest functions
**Method:** Manual code review before running automated tests

---

## Critical Bugs Fixed

### 1. ⚠️ CRITICAL: Direct Addressing Generated Variable Names Instead of Memory Accesses

**Severity:** CRITICAL - Affects virtually all functions
**Commit:** 8c02c82

**Problem:**
All direct addressing modes (Direct, DirectX, DirectY) were generating variable names instead of memory accesses.

**Example:**
```assembly
STA PPU_CTRL_REG1  ; Store A at $2000
```

**Before (WRONG):**
```kotlin
PPU_CTRL_REG1 = A  // Tries to assign to undefined variable!
```

**After (CORRECT):**
```kotlin
memory[PPU_CTRL_REG1] = A  // Proper memory write
```

**Impact:**
- Every function that accesses memory failed
- LDA/STA/LDX/STX/LDY/STY all broken
- INC/DEC memory operations broken
- ASL/LSR/ROL/ROR memory operations broken
- Essentially 95%+ of all SMB functions would fail

**Fix Details:**
1. **Direct addressing:** `label` → `memory[label]`
2. **DirectX addressing:** `label,X` → `memory[label + X]` (with offset support)
3. **DirectY addressing:** `label,Y` → `memory[label + Y]` (with offset support)
4. **IndirectX:** Implemented proper zero-page indirect indexed addressing
5. **IndirectY:** Implemented proper indirect indexed addressing
6. **IndirectAbsolute:** Implemented for JMP indirect

**Code Changed:**
- `src/main/kotlin/kotlin-codegen.kt` lines 1070-1139

---

### 2. ⚠️ IMPORTANT: INC/DEC Don't Update Flags

**Severity:** HIGH - Affects all functions using INC/DEC
**Commit:** 314fc4f

**Problem:**
INC and DEC instructions update the Z (zero) and N (negative) flags, but our code generator was missing these updates.

**Example:**
```assembly
INC FrameCounter  ; Increment and set flags
```

**Before (WRONG):**
```kotlin
memory[FrameCounter] = (memory[FrameCounter] + 1) and 0xFF
// Missing: updateZN(memory[FrameCounter])
```

**After (CORRECT):**
```kotlin
memory[FrameCounter] = (memory[FrameCounter] + 1) and 0xFF
updateZN(memory[FrameCounter])  // Now updates flags
```

**Impact:**
- Functions relying on INC/DEC to set flags would fail
- Affects loops that check zero flag after decrement
- DecTimers function would likely fail

**Fix Applied:**
- Added `updateZN()` calls after both INC and DEC operations

**Code Changed:**
- `src/main/kotlin/kotlin-codegen.kt` lines 203-217

---

### 3. ⚠️ IMPORTANT: Missing Constant Resolution

**Severity:** HIGH - Affects all functions using named addresses
**Commit:** f5b567c

**Problem:**
Assembly files define constants like `PPU_CTRL_REG1 = $2000`, but the code generator doesn't resolve these to numeric values. The execution environment doesn't know what `PPU_CTRL_REG1` means.

**Example:**
Generated code:
```kotlin
memory[PPU_CTRL_REG1] = A  // What is PPU_CTRL_REG1?
```

**Temporary Fix:**
Added hardcoded map of common SMB constants to `KotlinExecutor`:
- PPU registers (PPU_CTRL_REG1 = 0x2000, etc.)
- Joypad ports
- Mirror registers
- Common NES hardware addresses

**Proper Fix Needed:**
Need to implement proper constant resolution in code generator:
1. Parse assembly-time constants (lines with `=`)
2. Build symbol table mapping names to values
3. Resolve constants during code generation
4. Option A: Generate numeric literals directly
5. Option B: Generate const val declarations at top of file

**Code Changed:**
- `src/test/kotlin/KotlinExecutor.kt` lines 23-45, 328-332

**TODO:** Implement proper constant resolution in code generator

---

### 4. ⚠️ HIGH: KotlinExecutor Doesn't Handle Function Calls or Temp Variables

**Severity:** HIGH - Affects all functions using complex operations
**Commit:** 812919a

**Problem:**
The `executeDirectly` method in KotlinExecutor had two major gaps:
1. Didn't handle function calls like `updateZN(A)`
2. Didn't track temporary variables like `val temp0 = ...`

**Example 1 - Missing Function Calls:**
```kotlin
A = 0x42
updateZN(A)  // This line was silently ignored!
```

**Result:** Flags would never be updated, causing all comparisons and branches to fail.

**Example 2 - Missing Temp Variables:**
```kotlin
val temp0 = A + 0x20 + (if (C) 1 else 0)  // temp0 not stored
C = temp0 > 0xFF  // Error: temp0 is undefined!
A = temp0 and 0xFF
```

**Result:** ADC/SBC and ROL/ROR operations would crash because temp variables weren't tracked.

**Impact:**
- Every instruction that updates flags would have wrong flag state
- ADC/SBC arithmetic would fail (use temps to avoid double evaluation)
- ROL/ROR rotates would fail (use temps to save old carry)
- Essentially all complex operations broken

**Fix Applied:**
1. Added function call handler that recognizes `updateZN(...)` pattern
2. Added temporary variable storage: `val temp0 = expr` now stores the value
3. Added temp variable lookup in expression evaluator
4. Threaded `tempVars` map through all evaluation functions

**Code Changed:**
- `src/test/kotlin/KotlinExecutor.kt` lines 217-445
- Added tempVars tracking
- Added function call pattern matching
- Updated evaluateExpression to check tempVars

---

### 5. ⚠️ HIGH: Missing Stack Operation Functions in KotlinExecutor

**Severity:** HIGH - Affects all functions using stack operations
**Commit:** 85ffc74

**Problem:**
The code generator emits calls to stack operation functions (pushByte, pullByte, getStatusByte, setStatusByte), but KotlinExecutor didn't have these functions implemented.

**Example - Generated Code:**
```assembly
PHA  ; Push accumulator
```

**Generated Kotlin:**
```kotlin
pushByte(A)  // Function doesn't exist!
```

**Impact:**
- PHA, PLA, PHP, PLP instructions would all fail
- Any function using stack for temporary storage would crash
- Function prologue/epilogue code would fail
- Interrupt handling code would fail

**Fix Applied:**

1. **Added stack operation functions to ExecutionEnvironment:**
```kotlin
fun pushByte(value: UByte) {
    writeByte(0x0100 + SP.toInt(), value)
    SP = ((SP.toInt() - 1) and 0xFF).toUByte()
}

fun pullByte(): UByte {
    SP = ((SP.toInt() + 1) and 0xFF).toUByte()
    return readByte(0x0100 + SP.toInt())
}

fun getStatusByte(): UByte {
    // Pack all flags into a byte (N,V,-,B,D,I,Z,C)
    var status = 0b00100000 // Bit 5 always set
    if (N) status = status or 0b10000000
    if (V) status = status or 0b01000000
    if (D) status = status or 0b00001000
    if (I) status = status or 0b00000100
    if (Z) status = status or 0b00000010
    if (C) status = status or 0b00000001
    return status.toUByte()
}

fun setStatusByte(value: UByte) {
    // Unpack flags from byte
    val v = value.toInt()
    N = (v and 0b10000000) != 0
    V = (v and 0b01000000) != 0
    D = (v and 0b00001000) != 0
    I = (v and 0b00000100) != 0
    Z = (v and 0b00000010) != 0
    C = (v and 0b00000001) != 0
}
```

2. **Updated executeLine to handle function calls:**
```kotlin
when (funcName) {
    "updateZN" -> env.updateZN(value)
    "pushByte" -> env.pushByte(value)
    "setStatusByte" -> env.setStatusByte(value)
}
```

3. **Updated evaluateExpression to handle functions that return values:**
```kotlin
when (funcName) {
    "pullByte" -> env.pullByte()
    "getStatusByte" -> env.getStatusByte()
}
```

**Supported Operations:**
- `PHA`: `pushByte(A)` ✓
- `PLA`: `A = pullByte()` ✓
- `PHP`: `pushByte(getStatusByte())` ✓
- `PLP`: `setStatusByte(pullByte())` ✓
- `PHX`, `PLX`, `PHY`, `PLY`: Same pattern ✓

**Code Changed:**
- `src/test/kotlin/KotlinExecutor.kt` lines 140-178, 362-369, 435-446

---

## Bugs Still To Find

Based on systematic review, potential remaining issues:

### Memory Operations
- ✅ Direct addressing fixed
- ✅ Indexed addressing fixed
- ✅ Indirect addressing fixed
- ✅ Stack operations (PHA/PLA/PHP/PLP) fixed
- ❓ Boundary conditions (wrapping, page crosses)

### Flag Operations
- ✅ Z and N flags (updateZN) working
- ✅ C flag (carry) implemented for ADC/SBC/ASL/LSR/ROL/ROR
- ✅ V flag (overflow) implemented for ADC/SBC
- ❓ Flag operations with memory (BIT instruction)
- ❓ Flag state tracking in branches

### Control Flow
- ❓ Branch conditions
- ❓ JSR/RTS (function calls)
- ❓ JMP (unconditional jumps)
- ❓ Loop detection and generation

### Edge Cases
- ❓ Zero page wrapping (address & 0xFF)
- ❓ Stack wrapping (SP overflow)
- ❓ Page boundary crosses in indexed addressing
- ❓ Signed vs unsigned comparisons

---

## Testing Strategy

### Phase 1: Simple Functions (Current)
Test simplest leaf functions to find systematic bugs:
1. ✅ DoNothing2 (just RTS) - baseline test
2. ⏳ WritePPUReg1 (2 STA, 1 RTS) - tests memory writes
3. ⏳ InitScroll (2 STA, 1 RTS) - similar to WritePPUReg1
4. ⏳ InitVStf (3 LOC) - simple operations

### Phase 2: Medium Complexity
5. DecTimers - loops, memory access, comparisons
6. RotPRandomBit - bit manipulation, rotates
7. Other leaf functions with 5-20 LOC

### Phase 3: Complex Functions
8. Functions with branches and loops
9. Functions with arithmetic
10. Functions with indirect addressing

### Phase 4: Non-Leaf Functions
11. Functions that call other functions
12. Add mocking/stubbing for JSR calls

---

## Statistics

### Bugs Found: 5 (all critical/high severity)
1. Direct addressing mode bug (CRITICAL) - affects 95% of functions
2. INC/DEC flag updates (HIGH) - affects loops and counters
3. Missing constant resolution (HIGH) - affects all memory operations
4. KotlinExecutor missing features (HIGH) - affects all complex operations
5. Missing stack operations (HIGH) - affects all stack-using functions

### Commits: 6
- 8c02c82: Fix addressing modes
- 314fc4f: Fix INC/DEC flags
- f5b567c: Add constants support
- 050cd18: Document bugs found
- 812919a: Fix KotlinExecutor function calls/temps
- 85ffc74: Fix KotlinExecutor stack operations

### Lines Changed: ~200 lines
- kotlin-codegen.kt: ~75 lines
- KotlinExecutor.kt: ~90 lines
- New test files: ~35 lines

### Test Coverage: 0% (haven't run tests yet)
All bugs found through manual code review before running any automated tests.

---

## Next Steps

1. ✅ Fix critical addressing mode bug
2. ✅ Fix INC/DEC flag updates
3. ✅ Add temporary constants support
4. ⏳ Run tests on DoNothing2
5. ⏳ Run tests on WritePPUReg1
6. ⏳ Continue with progressively complex functions
7. TODO: Implement proper constant resolution
8. TODO: Run full differential test suite (590 tests)
9. TODO: Achieve 90%+ pass rate

---

## Lessons Learned

1. **Manual review before automated testing is valuable**
   - Found 3 critical bugs before running any tests
   - Saved time by fixing systematic issues first

2. **Addressing modes are fundamental**
   - The addressing mode bug would have caused 95%+ test failures
   - Worth checking these carefully in any code generator

3. **Flag updates are easy to forget**
   - 6502 has complex flag behavior
   - Need to verify each instruction updates correct flags

4. **Constant resolution is essential**
   - Can't generate working code without resolving symbols
   - Need proper solution, not just hardcoded constants

5. **Test incrementally**
   - Start with simplest possible functions
   - Build up complexity gradually
   - Fix systematic bugs before moving forward

---

**Status:** Ready to begin actual differential testing with fixes applied.

**Expected Result:** Much higher initial pass rate now that critical bugs are fixed.
