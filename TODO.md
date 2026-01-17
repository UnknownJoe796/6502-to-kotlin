# SMB Decompilation Issues TODO

## Test Results Summary
- **Total Tests:** 1,956
- **Passed:** 1,197 (61.2%)
- **Failed:** 759 (38.8%)

## Failure Categories

### Category 1: StackOverflowError (Infinite Recursion)
**Example:** `getRow` function at SMBDecompiled.kt:8594

**Root Cause:** The decompiler generates BOTH a do-while loop AND recursive tail calls for the same code, when the assembly only has tail calls within the loop.

**Specific Issues:**
- [ ] **Loop condition misidentification**: The condition `(A and 0x80) == 0` is from wrong source. The 6502 code uses `bpl RenderUnderPart` which branches on bit 7 of Y (sign flag from `dey`), not on A
- [ ] **Missing loop variable tracking**: Decompiler doesn't recognize Y is being decremented (`dey`) and tested (bit 7), making Y the actual loop counter
- [ ] **Tail call handling**: JMP instructions should become direct jumps or tail calls, not regular function calls with separate returns

**Affected Functions:**
- `getRow()`
- `drawRow()`
- `renderUnderPart()`

---

### Category 2: Timeout (Infinite Loops)
**Examples:** `blockObjectsCore`, `saveAB`, `getPlayerOffscreenBits`, `getBlockOffscreenBits`

**Root Cause:** Wrong loop exit condition variable - decompiler uses A register instead of the actual loop counter (usually Y).

**Specific Issues:**
- [ ] **Tail call JMP not threaded**: `jmp GetYOffscreenBits` is a tail call but decompiled as function call + separate return, losing the return value
- [ ] **Loop exit detection uses wrong variable**: Assembly `bne ExXOfsBS` branches when A != 0, but decompiler generates `while ((A and 0x80) == 0)` checking sign bit
- [ ] **Unreachable code generation**: Nested double negations like `if (!(A >= 0xF0)) { if (!(A >= 0xF0)) { } }` that can never execute

**Affected Functions:**
- `getXOffscreenBits()`
- `getYOffscreenBits()`
- `runOffscrBitsSubs()`
- `blockObjectsCore()`

---

### Category 3: Assertion Failures (Wrong Output)

#### 3a. Control Flow Misparsing - Loop Termination
**Example:** `setAttrib_frame22_test3` (expected 0x71, got 0x59)

- [ ] **Loop-back branches as early returns**: Assembly `bcc DrawMTLoop` (loop back if X < 0x0D) becomes `if (!(X >= 0x0D)) { return }` instead of continuing the loop
- [ ] Causes functions to exit prematurely, skipping work

#### 3b. Carry Flag / Borrow Handling
**Example:** `getXOffscreenBits_frame1715_test3` (expected 0xC5, got 0xC4 - off by 1)

- [ ] **SBC borrow semantics inverted**: 6502 carry flag is inverted in subtraction (carry CLEAR means borrow occurred)
- [ ] Current code: `temp1 = A - val - if (temp0 >= 0) 0 else 1` checks wrong condition
- [ ] Should check carry flag state, not result sign

#### 3c. Multiple Premature Returns
**Example:** `getXOffscreenBits` has 4+ return statements in loop body

- [ ] **Return statements break loop iteration**: Functions that should iterate (checking both screen edges) return after first check
- [ ] Loop structures dismantled into early exits

#### 3d. Shift vs Rotate Semantics
**Example:** `drawOneSpriteRow_frame1525_test3` (expected 0x79, got 0x77 - off by 2)

- [ ] **ROL/ROR carry chain broken**: Original uses rotate-left preserving carry as input/output for chained operations
- [ ] Decompiler uses simple shift, losing carry propagation between instructions

#### 3e. JumpEngine Dispatch
**Example:** `areaParserTasks_frame1501_test7` (expected 0x00, got 0x01)

- [ ] **Tail calls don't preserve semantics**: JumpEngine dispatch calls don't correctly chain memory modifications
- [ ] Functions called but results not properly threaded back

---

## Priority Fixes

### High Priority (Blocks many functions)
1. [ ] Fix loop exit condition detection - use correct variable from branch instruction
2. [ ] Fix tail call handling for JMP instructions
3. [ ] Fix carry/borrow flag semantics in SBC

### Medium Priority (Affects specific patterns)
4. [ ] Fix loop-back branch detection (bcc/bcs to earlier labels)
5. [ ] Fix ROL/ROR carry chain preservation
6. [ ] Eliminate unreachable code from nested negations

### Lower Priority (Edge cases)
7. [ ] Improve JumpEngine dispatch table handling
8. [ ] Fix multiple return statements in loop bodies

---

## Investigation Notes

### Loop Exit Condition Bug
The decompiler is generating `while ((A and 0x80) == 0)` for many loops when:
- The actual loop counter is Y (decremented by `dey`)
- The exit check is `bpl` (branch if positive) on Y
- A is just data being loaded, not the loop control

**Fix approach:** Track which register the branch instruction actually tests (via the flags it consumes) and use that for the loop condition.

### Tail Call Bug
Assembly pattern:
```asm
jsr SomeFunc
jmp OtherFunc   ; tail call - doesn't return here
```

Currently generates:
```kotlin
someFunc()
otherFunc()
return A  // WRONG - shouldn't return here, OtherFunc's return IS our return
```

Should generate:
```kotlin
someFunc()
return otherFunc()  // Tail call - their return is our return
```

### Carry Flag in SBC
6502 SBC semantics:
- Carry SET before SBC = no borrow
- Carry CLEAR before SBC = borrow (subtract extra 1)
- After SBC: Carry SET if no borrow occurred, CLEAR if borrow occurred

This is **inverted** from intuitive "carry" meaning. The decompiler needs to track this correctly.

---

## Specific Code Locations to Fix

### Bug 1: Loop Exit Condition (HIGH PRIORITY)
**File:** `core/src/main/kotlin/kotlin-codegen.kt`

**Primary Location - Lines 740-747** in `Condition.toKotlinExpr()`:
```kotlin
AssemblyOp.BMI, AssemblyOp.BPL -> ctx.negativeFlag ?: run {
    // Fallback: build negative test from likely register
    val reg = ctx.registerA ?: ctx.registerY ?: ctx.registerX
        ?: ctx.getFunctionLevelVar("A") ?: ctx.getFunctionLevelVar("Y") ?: ctx.getFunctionLevelVar("X")
        ?: KVar("A")
    KBinaryOp(KParen(KBinaryOp(reg, "and", KLiteral("0x80"))), "!=", KLiteral("0"))
}
```
**Problem:** Uses `(reg and 0x80) != 0` instead of `reg != 0` for loop counters like Y after DEY.

**Secondary Location - Lines 999-1002** in `buildOrphanedBranchCondition()`: Same fallback issue.

**Fix:** When register is Y or X (likely loop counter), use direct `!= 0` test instead of bit-7 test.

---

### Bug 2: JMP Tail Call Returns (HIGH PRIORITY)
**File:** `core/src/main/kotlin/instruction-handlers.kt`

**Location - Lines 693-694** in JMP handler:
```kotlin
stmts.add(KExprStmt(KCall(targetName, args)))  // Line 693
stmts.add(KReturn())  // Line 694 - WRONG: bare return
```

**Problem:** Generates `someLabel(); return` instead of `return someLabel()`.

**Fix:** Check target function's outputs (like JSR handler does at lines 779-814) and generate `return targetName(args)` for functions with return values.

---

### Bug 3: SBC Carry/Borrow - VERIFIED CORRECT
**File:** `core/src/main/kotlin/instruction-handlers.kt`
**Lines:** 214-264

After investigation, the SBC implementation is CORRECT. It properly:
- Computes borrow as `NOT carry` (lines 222-225)
- Subtracts `A - operand - borrow` (line 229)
- Sets carry as `result >= 0` (line 251)

Off-by-1 errors are likely from other sources (flag propagation, multi-byte chains).

---

### Bug 4: Loop-back Branch Early Returns (HIGH PRIORITY)
**File:** `core/src/main/kotlin/controls.kt`

**Location - Lines 1050-1085** in `buildRange()`:
```kotlin
val isBackwardBranch = br != null && br.originalLineIndex < b.originalLineIndex
val backwardTargetExits = isBackwardBranch && br?.isExitLike() == true

if (backwardTargetExits && br != null && ft != null) {
    val cond = Condition(
        branchBlock = b,
        branchLine = last!!,
        sense = true, // <-- PROBLEM: hardcoded sense
    )
```

**Problem:** When `BCC DrawMTLoop` should loop back, the code generates `if (!cond) { return }` instead of continuing the loop.

**Root Cause:** The sense value calculation at line 1070 is hardcoded to `true`. Combined with condition negation logic in `kotlin-codegen.kt` lines 761-766, this produces inverted conditions.

**Additional Issue in `kotlin-codegen.kt` lines 761-766:**
```kotlin
val finalExpr = if (this.sense == positiveTest) {
    flagExpr
} else {
    KUnaryOp("!", flagExpr)  // Double negation occurs here
}
```

---

### Bug 5: ROL/ROR Carry Chain - VERIFIED CORRECT
**File:** `core/src/main/kotlin/instruction-handlers.kt`
**Lines:** 529-621

After investigation, the ROL/ROR implementation is CORRECT. It properly:
- Captures old carry BEFORE operation
- Computes new carry from original value's appropriate bit
- Injects old carry into correct position
- Updates ctx.carryFlag for next instruction

Off-by-2 errors in functions like `drawOneSpriteRow` are NOT from ROL/ROR but from other sources.

---

### Bug 6: Unreachable Nested Conditions (MEDIUM PRIORITY)
**File:** `core/src/main/kotlin/controls.kt`

**Location - Lines 1050-1085:** When consecutive backward branches target the same exit block (e.g., `BCC UpdSte` followed by `BCS KillBlock`), both create IfNode structures.

**Problem Pattern:**
```asm
BCC UpdSte    ; carry clear → backward to exit
BCS KillBlock ; carry set → backward to exit
```

Generates unreachable code:
```kotlin
if (!(A >= 0xF0)) {        // From BCC
    if (!(A >= 0xF0)) {    // From BCS - UNREACHABLE!
        // ...
    }
}
```

**Fix locations:**
1. `controls.kt` lines 1050-1085: Detect consecutive branches to same exit
2. `kotlin-codegen.kt` lines 641-709: Add simplify() rule for nested identical conditions

---

## Files to Modify

- `core/src/main/kotlin/kotlin-codegen.kt` - Loop exit conditions (lines 740-747, 999-1002)
- `core/src/main/kotlin/instruction-handlers.kt` - JMP tail calls (lines 693-694)
- `core/src/main/kotlin/controls.kt` - Backward branch handling (lines 1050-1085)

---

## VERIFIED BUG: Self-Recursive Tail Calls (NEW - by Claude)

**Symptoms:** StackOverflowError in functions like `renderUnderPart`

**Root Cause Verified:** When a conditional branch (like `bpl RenderUnderPart`) targets the START of the CURRENT function, the decompiler treats it as a tail call to another function instead of a loop continuation.

**Code Location:** `kotlin-codegen.kt` line 864 in orphaned branch handling:
```kotlin
val thenBody = when {
    // Bug: This matches when branchTargetFunction == ctx.currentFunction (self!)
    branchTargetFunction != null -> {
        // Generates recursive tail call + return
    }
```

**Assembly Pattern:**
```asm
RenderUnderPart:        ; ← Function entry point
    sty AreaObjectHeight
    ; ... loop body ...
    dey
    bpl RenderUnderPart   ; ← Backward branch to same function's start
```

**Generated (Wrong):**
```kotlin
do {
    // loop body
    Y = (Y - 1) and 0xFF
    if (!((Y and 0x80) != 0)) {
        renderUnderPart(A, X, Y)  // RECURSIVE CALL - causes StackOverflow!
        return
    }
} while ((A and 0x80) == 0)  // Wrong condition too
```

**Should Generate:**
```kotlin
do {
    // loop body
    Y = (Y - 1) and 0xFF
} while ((Y and 0x80) == 0)  // Loop continues while Y is positive
```

**Fix:** At line 864, add condition: `branchTargetFunction != ctx.currentFunction`

---

## VERIFIED BUG: Premature Returns in Forward Branches (NEW - by Claude)

**Symptoms:** Timeouts in functions like `getXOffscreenBits`, wrong values in many tests

**Root Cause Verified:** Forward branches to internal labels (like `bmi XLdBData`) are generating return statements when they should just be control flow. The join point code becomes unreachable.

**Assembly Pattern:**
```asm
XOfsLoop:
    cmp #$00
    bmi XLdBData          ; Forward branch if negative
    ldx ...
    cmp #$01
    bpl XLdBData          ; Forward branch if positive
    lda #$38              ; Code only reached if 0 < A < 1 (impossible for bytes!)
    jsr DividePDiff       ; Falls through to XLdBData
XLdBData:                 ; JOIN POINT - should be reached by ALL paths
    lda XOffscreenBitsData,x
    ; ... continue ...
```

**Generated (Wrong):**
```kotlin
do {
    if ((A and 0x80) == 0) {       // bmi check
        X = defaultXOnscreenOfs[1 + Y]
        if (((A - 0x01) and 0x80) != 0) {  // bpl check
            A = 0x38
            dividePDiff(A, Y)
            return A   // PREMATURE RETURN - skips XLdBData code
        } else {
            return A   // PREMATURE RETURN
        }
    } else {
        return A       // PREMATURE RETURN
    }
    // XLdBData code is here but UNREACHABLE due to all the returns
    A = xOffscreenBitsData[X]
    // ...
} while (...)
```

**Should Generate:** Forward branches to internal join points should NOT generate returns. The join point code should be after the if-else structure, reachable by all paths.

**Key Insight (by Claude):** The bug is specifically in `ensureReturnsPresent()` function (kotlin-codegen.kt lines 1581-1602). This function recursively adds fallback returns to ALL if-else branches. But when the if-else structure is followed by join point code, those returns make the join point unreachable.

The issue is that `ensureReturnsPresent` doesn't know about the code structure - it just sees an if-else without returns and adds them, not knowing that the code after the if-else is supposed to be reached.

**Simpler test cases pass** because the control flow analysis correctly builds the structure without intermediate returns. The bug only manifests when:
1. The assembly has multiple forward branches to the same join point
2. The branches get nested into if-else structures
3. `ensureReturnsPresent` adds returns to all branches
4. The join point code becomes unreachable

---

## Testing Strategy

After each fix:
1. Run `./gradlew :core:test` to ensure no regressions
2. Run `./gradlew :core:test --tests "KotlinCodeGenTest.generateKotlinForSMB"` to regenerate
3. Run `./gradlew :smb:test --tests "GeneratedFunctionTests"` to measure improvement
4. Target: Get from 61% passing to 80%+ passing
