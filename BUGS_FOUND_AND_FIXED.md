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

## Bugs Still To Find

Based on systematic review, potential remaining issues:

### Memory Operations
- ✅ Direct addressing fixed
- ✅ Indexed addressing fixed
- ✅ Indirect addressing fixed
- ❓ Stack operations (PHA/PLA/PHP/PLP) - need to verify
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

### Bugs Found: 3 (all critical/high severity)
1. Direct addressing mode bug (CRITICAL)
2. INC/DEC flag updates (HIGH)
3. Missing constant resolution (HIGH)

### Commits: 3
- 8c02c82: Fix addressing modes
- 314fc4f: Fix INC/DEC flags
- f5b567c: Add constants support

### Lines Changed: ~150 lines
- kotlin-codegen.kt: ~75 lines
- KotlinExecutor.kt: ~40 lines
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
