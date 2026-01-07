# Decompiler Completion Plan

## Project Status (as of January 2026)

### What's Working
- **6502 Interpreter**: 58/59 tests passing, runs full TAS playthrough
- **SMB Module Compiles**: 17,559 lines of decompiled Kotlin code compiles
- **TAS Function Capture**: Framework captures function input/output states
- **Core Decompiler**: 97% error reduction (7,489 → 224 errors)
- **Control Flow Analysis**: Recovers if/else, loops from 6502 branches
- **Expression Reconstruction**: Converts 6502 idioms to Kotlin expressions

### What Needs Work
- **224 Compilation Errors**: All missing ROM label constants (data table addresses)
- **Behavioral Verification**: Decompiled functions not validated against interpreter
- **Generated Tests**: Need to run against decompiled code
- **Edge Cases**: Some type conversions, specific instruction patterns

---

## Phase 1: Fix Remaining Compilation Errors

**Goal**: Zero compilation errors in decompiled SMB code

**Problem**: 224 errors are all missing ROM label constants - assembly labels like `MushroomIconData:` that reference ROM addresses.

**Approach**: Extract ROM addresses from `smbdism.asm`, generate constants file

**Steps**:
1. Scan `smbdism.asm` for all label definitions with addresses
2. Generate `SMBRomAddresses.kt` with all ROM labels
3. Update imports in decompiled code
4. Verify zero compilation errors

**Estimated effort**: 1-2 hours

---

## Phase 2: Run Generated Function Tests

**Goal**: Establish baseline of which functions work correctly

**Steps**:
1. Review `local/testgen/GeneratedFunctionTests.kt`
2. Create test runner that calls decompiled functions with captured inputs
3. Compare outputs (registers, flags, memory writes)
4. Generate report of passing/failing functions

**Metrics to track**:
- Total unique functions: ~74
- Tests per function: 1-10
- Pass rate, failure patterns

---

## Phase 3: Fix Failing Decompiled Functions

**Goal**: High pass rate on behavioral tests

**Workflow per function**:
1. Identify function in `smbdism.asm`
2. Compare original assembly vs decompiled Kotlin
3. Diagnose discrepancy (flags, addressing, control flow)
4. Fix code generator (`kotlin-codegen.kt`) or decompiled output
5. Verify test passes

**Common issues** (from `DECOMPILER_FIXES_NEEDED.md`):
- Flag updates after arithmetic/logic ops
- Indexed addressing calculations
- Branch condition polarity
- Stack push/pull balance
- Type conversions (Int vs UByte)

**Priority order** (by call frequency):
1. `0x9BE1`: 3,398 calls
2. `0xC047`: 2,904 calls
3. Continue down the list

---

## Phase 4: Full TAS Validation

**Goal**: Decompiled code produces identical game state to interpreter

**Approach**:
1. Run same TAS inputs through both interpreter and decompiled code
2. Compare memory state at key points (every 100 frames)
3. Debug divergences using `NMI-TIMING-INSIGHTS.md`

**Key challenges**:
- NMI timing (multi-frame handlers)
- Frame skip detection
- RNG synchronization

---

## Phase 5: Polish and Document

**Goal**: Clean, readable, maintainable decompiled codebase

**Tasks**:
- Remove debug artifacts
- Improve variable names
- Add comments for complex patterns
- Document known limitations
- Create usage guide

---

## Key Files Reference

| File | Purpose |
|------|---------|
| `smbdism.asm` | Original SMB disassembly |
| `outputs/smb-decompiled.kt` | Generated decompiled code |
| `smb/src/main/kotlin/.../SMBDecompiled.kt` | Copy in SMB module |
| `core/src/main/kotlin/kotlin-codegen.kt` | Code generation logic |
| `local/testgen/GeneratedFunctionTests.kt` | Generated tests from TAS |
| `local/testgen/captured-tests-happylee-warps.json` | Raw captured data |
| `docs/NMI-TIMING-INSIGHTS.md` | Timing knowledge for debugging |
| `docs/DECOMPILER-TESTING-GUIDE.md` | Testing workflow guide |

---

## Current Test Results (January 2026)

### Phase 2 Findings

**Tests completed**: 9 out of 21 function groups (initializeNameTables hung)

| Status | Function | Description |
|--------|----------|-------------|
| ✓ PASS | pauseRoutine | 2 tests pass |
| ✓ PASS | soundEngine | 10 tests pass |
| ✓ PASS | processWhirlpools | 10 tests pass |
| ✗ FAIL | updateScreen | Memory write mismatches |
| ✗ FAIL | renderAttributeTables | Memory write mismatches |
| ✗ FAIL | relativePlayerPosition | Memory write mismatches |
| ✗ FAIL | processAreaData | Memory write mismatches |
| ✗ FAIL | playSqu2Sfx | Memory write mismatches |
| ✗ FAIL | jumpSwimSub | Memory write mismatches |
| ⏳ HUNG | initializeNameTables | Infinite loop |

### Phase 3 Progress (In Progress)

**Bugs Found and Fixed in Code Generator:**

1. **Duplicate do-while loops** (controls.kt)
   - Code generator was creating two nested loops where only one exists
   - Fixed by adding `processingLoopHeaders` check to prevent nested loop detection inside natural loops
   - Affected: `spriteShuffler`, `getPlayerColors`

2. **Wrong DEC/INC flag condition** (kotlin-codegen.kt)
   - After `dec $00`, condition used `(memory[0x0] - 1)` instead of fresh read of `memory[0x0]`
   - Fixed by using fresh memory read for flag computation after assignment
   - Affected: `getPlayerColors`, any function with DEC/INC + branch pattern

3. **ADC carry flag expression evaluation** (kotlin-codegen.kt)
   - Two issues fixed:
     a. Carry flag expression referenced A register which would have different value after masking
     b. ADC inside if-blocks didn't update the function-level register variable
   - Fixed by:
     - Capturing full sum in temp variable before masking
     - Updating function-level variable when inside a branch context
   - Affected: `spriteShuffler`, any function with ADC + BCC pattern

4. **Missing ROM initialization** - Tests didn't load ROM data before running
   - Fixed by adding `@BeforeTest` with `initializeRomData()`

**Updated Test Results (January 3, 2026):**

| Status | Function | Notes |
|--------|----------|-------|
| ✓ PASS | pauseRoutine | 2 tests |
| ✓ PASS | spriteShuffler | 3 tests - **FIXED by ADC carry fix** |
| ✓ PASS | operModeExecutionTree | 2 tests |
| ✗ FAIL | demoEngine | 8/10 failures - needs investigation |
| ✗ FAIL | moveSpritesOffscreen | Failures - needs investigation |
| ✗ FAIL | drawMushroomIcon | Failures - needs investigation |

**Pass Rate**: 3+ passing function groups, more to test

### Remaining Issues

1. **Other decompiler bugs** - Need to investigate remaining test failures
2. **Incomplete ROM data** - Some ROM addresses may still be missing
3. **Full test suite needs running** - Many functions still untested

---

## Success Criteria

- [ ] Zero compilation errors ✓ (DONE)
- [ ] >80% function tests passing
- [ ] Full TAS playthrough works with decompiled code
- [ ] Clean, documented output
