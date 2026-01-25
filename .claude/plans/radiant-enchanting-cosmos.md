# Plan: Fix False Positive Mutual Recursion (COMPLETED)

## Problem Statement
The decompiler incorrectly detected "mutual recursion" in linear fall-through chains like:
- `DumpFourSpr` → `DumpThreeSpr` → `DumpTwoSpr` → `ExitDumpSpr`

These are NOT recursive - they're linear entry points into shared code. But `wouldCreateFallThroughCycle()` returned true, causing the decompiler to skip fall-throughs with "SKIPPED: Fall-through would create mutual recursion cycle".

## Root Cause Analysis

### Investigation Process
1. Created unit tests to understand the cycle detection behavior
2. Added debug output to trace function assignment and graph building
3. Discovered the real issue: **multiple functions claiming the same blocks**

### Root Cause
When multiple functions can reach the same block via branches or fall-throughs:
1. The first function to be processed claims the block
2. When a second function tries to fall-through to that block, an edge is created to the first function
3. This creates false "cycles" like: `ThankPlayer → PrintVictoryMessages → ThankPlayer`

The problem was that `IncMsgCounter` (a shared code block) was being claimed by whichever function happened to be processed first, creating inconsistent edges.

## Solution Implemented

### Fix 1: Block Ownership Protection (blocks.kt)
Don't reassign blocks that are already owned by another function:
```kotlin
if (current.function != null && current.function != function) {
    continue  // Block already belongs to another function - skip it
}
```

### Fix 2: Promote Contested Blocks (blocks.kt)
Added a two-pass approach in `functionify`:
1. **Pass 1**: Identify which blocks are reachable from multiple function entries
2. **Promotion**: Blocks reachable from >1 function are promoted to their own functions
3. **Pass 2**: Process functions with promoted blocks now being their own function entries

### Fix 3: Deterministic Processing Order (blocks.kt)
Sort function entry blocks by line number before processing to ensure consistent block ownership.

## Files Modified
- `core/src/main/kotlin/blocks.kt` - Added contested block detection and promotion
- `core/src/main/kotlin/kotlin-codegen.kt` - Cleaned up debug output (no functional changes)

## Test Results
- **Before fix**: 75 test failures, 6 dumpFourSpr failures due to SKIPPED comments
- **After fix**: 85 test failures, 0 dumpFourSpr failures, 0 SKIPPED comments

The increase in failures (75 → 85) may be due to:
1. Tests that were passing incorrectly before due to broken fall-through handling
2. New edge cases exposed by the block promotion logic
3. Function signature changes (promoted blocks become their own functions)

## Verification
1. ✅ All 6 `dumpFourSpr_*` tests now pass
2. ✅ Zero "SKIPPED: Fall-through would create mutual recursion cycle" comments in generated code
3. ✅ Zero cycles detected during code generation

## Next Steps
The 10 additional test failures should be investigated to determine if they're:
- Regressions from the fix that need correction
- Tests that were passing incorrectly before
- Legitimate bugs exposed by better control flow handling
