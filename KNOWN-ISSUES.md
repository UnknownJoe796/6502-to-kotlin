# Known Issues in 6502-to-Kotlin Decompiler

This document tracks known decompilation issues identified through test failures in the SMB (Super Mario Bros.) test suite.

## Test Failure Summary

As of the last analysis, there are **87 failing tests** in `GeneratedFunctionTests`. These failures cluster around specific functions and share common root causes.

| Function | Failures | Primary Issue |
|----------|----------|---------------|
| moveNormalEnemy | 10 | Missing branch to ReviveStunned |
| playerBGCollision | 9 | Control flow issues with memory 0xEB |
| enemyToBGCollisionDet | 8 | Same 0xEB memory pattern |
| drawEnemyObjRow | 8 | Sprite RAM writes (64 instead of 252) |
| playerPhysicsSub | 7 | Related control flow issues |
| playerGfxHandler | 5 | Sprite handling |
| jCoinGfxHandler | 5 | Off-by-8 errors in Y positions |
| checkpointEnemyID | 5 | Control flow |
| imposeFriction | 4 | Missing branch to LeftFrict |
| enemiesAndLoopsCore | 4 | Control flow |
| drawOneSpriteRow | 4 | Sprite RAM issues |
| procEnemyCollisions | 3 | Control flow |
| floateyNumbersRoutine | 3 | Sprite Y positions |
| bumpBlock | 3 | Control flow |

---

## Root Cause #1: Consecutive Conditional Branch Pattern

**Severity**: High
**Affected Functions**: `imposeFriction`, `moveNormalEnemy`, and likely others

### Description

When assembly code has consecutive conditional branches that are mutually exclusive (e.g., `bpl` followed by `bmi`), the decompiler generates the condition check but fails to generate the actual branch/jump code.

### Example: ImposeFriction (smbdism.asm:6237-6239)

**Original Assembly:**
```asm
       lda Player_X_Speed
       beq SetAbsSpd      ; branch if zero
       bpl RghtFrict      ; branch if positive
       bmi LeftFrict      ; branch if negative (ALWAYS taken at this point!)
JoypFrict:
       lsr                ; this should NEVER be reached from above
```

After `beq` and `bpl` fail to branch, the value must be negative, so `bmi LeftFrict` is effectively **unconditional**. However, the decompiled Kotlin generates:

**Decompiled Kotlin (SMBDecompiled.kt:10763-10767):**
```kotlin
//> bpl RghtFrict
if ((A and 0x80) != 0) {
    //> bmi LeftFrict  -- COMMENT ONLY, NO BRANCH CODE!
}
// Falls through to JoypFrict incorrectly
```

### Symptom

Test expects `Player_X_Speed` (0x0057) = 13, but gets 12 (off by 1). The code takes the wrong path (RghtFrict instead of LeftFrict), applying friction in the wrong direction.

### Fix Required

The decompiler needs to recognize consecutive conditional branches on the same register/flag and understand their mutual exclusion. When a `bmi` follows a `bpl` (with no intervening code that changes flags), the `bmi` is unconditional and should generate a direct jump/call to LeftFrict.

---

## Root Cause #2: Missing Branch Targets

**Severity**: High
**Affected Functions**: `moveNormalEnemy`

### Description

Some branch targets are referenced in comments but no actual code is generated to branch to them.

### Example: MoveNormalEnemy (smbdism.asm:9314-9316)

**Original Assembly:**
```asm
       cmp #$05
       beq FallE           ; if state == 5, go to FallE
       cmp #$03
       bcs ReviveStunned   ; if state >= 3 (states 3 or 4), go to ReviveStunned
FallE: jsr MoveD_EnemyVertically
```

**Decompiled Kotlin (SMBDecompiled.kt:18244-18252):**
```kotlin
if (A != 0x05) {
    //> cmp #$03
    //> bcs ReviveStunned  -- NO CODE GENERATED FOR THIS BRANCH!
}
//> FallE: jsr MoveD_EnemyVertically
movedEnemyvertically(X)  // Always called, even for states 3-4
```

### Symptom

Test expects memory 0x005D = 16 (0x10), but gets 248 (0xF8). Enemy states 3-4 should go to `ReviveStunned` to reset the enemy, but instead fall through to `movedEnemyvertically()`.

### Fix Required

The control flow analysis needs to properly identify and generate branches for `bcs`/`bcc` comparisons that aren't simple equality checks.

---

## Root Cause #3: Internal Forward Branches Not Handled

**Severity**: Medium
**Affected Functions**: Multiple

### Description

The orphaned branch handler in `kotlin-codegen.kt` (lines 993-997) only handles:
- `targetIsExit` - branches to code outside the function
- `targetIsBreakTarget` - branches that exit a loop

It explicitly **skips** internal forward branches, assuming the control flow analysis (`controls.kt`) captured them as `IfNode` structures. When the CFG analysis misses a pattern, these branches silently fall through instead of generating proper conditional jumps.

**Code from kotlin-codegen.kt:993-997:**
```kotlin
// Only generate orphaned branch handling if:
// 1. Target is outside the function (exit), or
// 2. Target is a break target for an enclosing loop
// Skip handling for internal forward branches that just skip code within a loop
if ((targetIsExit || targetIsBreakTarget) && branchTarget != null) {
```

### Fix Required

Either:
1. Improve control flow analysis to capture more branch patterns as structured `IfNode`s, or
2. Add fallback handling for internal forward branches that weren't captured

---

## Root Cause #4: Sprite Y-Position Calculation Errors

**Severity**: Medium
**Affected Functions**: `jCoinGfxHandler`, `drawEnemyObjRow`, `drawOneSpriteRow`, `floateyNumbersRoutine`

### Description

Sprite drawing routines show consistent off-by-8 errors in Y positions, and some write incorrect attribute values (64 instead of 252).

### Examples

**jCoinGfxHandler:**
- Expected sprite Y = 122, got 121 (off by 1)
- Expected sprite Y = 130, got 122 (off by 8)

**drawEnemyObjRow / drawOneSpriteRow:**
- Expected sprite data = 252 (0xFC), got 64 (0x40)
- 0x40 is the horizontal flip attribute, suggesting wrong data is being stored

### Possible Causes

1. Y coordinate increment (`adc #$08`) not being applied correctly
2. Wrong register being used for sprite data offset
3. Attribute vs. Y-position confusion in store operations

### Fix Required

Detailed analysis of `DrawSpriteObject` (smbdism.asm:15005) and related sprite routines needed.

---

## Diagnostic Commands

### Run all SMB tests
```bash
./gradlew :smb:test --tests "GeneratedFunctionTests"
```

### Run tests for a specific function
```bash
./gradlew :smb:test --tests "GeneratedFunctionTests.moveNormalEnemy_*"
./gradlew :smb:test --tests "GeneratedFunctionTests.imposeFriction_*"
```

### Get failure details
```bash
./gradlew :smb:test --tests "GeneratedFunctionTests.imposeFriction_*" --info 2>&1 | grep -E "expected:|AssertionFailed"
```

### Count failures by function
```bash
./gradlew :smb:test --tests "GeneratedFunctionTests" 2>&1 | grep "FAILED" | sed 's/_frame[0-9]*_test[0-9]*().*//' | sed 's/GeneratedFunctionTests > //' | sort | uniq -c | sort -rn
```

---

## Priority for Fixes

1. **High**: Fix consecutive conditional branch handling (`bpl`/`bmi`, `bcs`/`bcc` pairs)
2. **High**: Ensure all branch targets generate actual branch code
3. **Medium**: Add fallback for internal forward branches missed by CFG analysis
4. **Medium**: Investigate sprite drawing Y-offset and attribute issues

---

*Document created by Claude - 2026-01-22*
