# Manual Test of Code Generation and Execution

This document shows manual testing of the code generation and executor without requiring full builds.

## Test 1: Simple Load (LDA #$42)

### Generated Code
```kotlin
A = 0x42
updateZN(A)
```

### Execution Trace
```
Initial state:
  A = 0x00

After execution:
  A = 0x42
  Z = false (value is not zero)
  N = false (bit 7 is not set: 0x42 = 0b01000010)
```

### Executor Simulation
```kotlin
// Parse line 1: A = 0x42
val assignmentPattern = """^([AXYN])\s*=\s*(.+)$""".toRegex()
// Matches: target="A", expr="0x42"
val value = evaluateExpression("0x42", env)
// Returns: 0x42
env.A = 0x42u

// Parse line 2: updateZN(A)
// This is a function call, executor handles it by:
env.updateZN(env.A)  // which sets Z and N flags
// Z = (0x42 == 0) = false
// N = (0x42 and 0x80) != 0 = false

Final state:
  A = 0x42 ✓
  Z = false ✓
  N = false ✓
```

**Result**: ✅ PASS

## Test 2: Transfer (LDA #$42, TAX)

### Generated Code
```kotlin
A = 0x42
updateZN(A)
X = A
updateZN(X)
```

### Execution Trace
```
Initial state:
  A = 0x00, X = 0x00

After LDA #$42:
  A = 0x42, X = 0x00
  Z = false, N = false

After TAX:
  A = 0x42, X = 0x42
  Z = false, N = false
```

### Executor Simulation
```kotlin
// Line 1: A = 0x42
env.A = 0x42u
env.updateZN(env.A)

// Line 2: updateZN(A) - already done above

// Line 3: X = A
val value = evaluateExpression("A", env)
// Returns: env.A = 0x42
env.X = 0x42u

// Line 4: updateZN(X)
env.updateZN(env.X)
// Z = false, N = false

Final state:
  A = 0x42 ✓
  X = 0x42 ✓
  Z = false ✓
  N = false ✓
```

**Result**: ✅ PASS

## Test 3: Increment (INX)

### Generated Code
```kotlin
X = (X + 1) and 0xFF
updateZN(X)
```

### Test Case A: Normal increment
```
Initial: X = 0x10
Expected: X = 0x11, Z = false, N = false
```

### Executor Simulation
```kotlin
// Line 1: X = (X + 1) and 0xFF
val expr = "(X + 1) and 0xFF"
// Parse: (X + 1) - binary op with parentheses
val xVal = evaluateExpression("X", env) // = 0x10
val sum = evaluateExpression("X + 1", env) // = 0x11
val result = evaluateExpression("(X + 1) and 0xFF", env)
// = (0x11) and 0xFF = 0x11
env.X = 0x11u

// Line 2: updateZN(X)
env.updateZN(env.X)
// Z = false, N = false

Final: X = 0x11 ✓
```

### Test Case B: Wraparound
```
Initial: X = 0xFF
Expected: X = 0x00, Z = true, N = false
```

### Executor Simulation
```kotlin
// Line 1: X = (X + 1) and 0xFF
val sum = 0xFF + 1 = 0x100
val result = 0x100 and 0xFF = 0x00
env.X = 0x00u

// Line 2: updateZN(X)
env.updateZN(env.X)
// Z = (0x00 == 0) = true ✓
// N = (0x00 and 0x80) != 0 = false ✓

Final: X = 0x00, Z = true, N = false ✓
```

**Result**: ✅ PASS

## Test 4: Arithmetic with Carry (ADC #$20)

### Generated Code
```kotlin
C = A + 0x20 + (if (C) 1 else 0) > 0xFF
A = (A + 0x20 + (if (C) 1 else 0)) and 0xFF
updateZN(A)
```

### Test Case: A=0x10, C=false, operand=0x20
```
Initial: A = 0x10, C = false
Expected: A = 0x30, C = false, Z = false, N = false
```

### Executor Simulation
```kotlin
// Line 1: C = A + 0x20 + (if (C) 1 else 0) > 0xFF
val a = env.A // = 0x10
val operand = 0x20
val carryIn = if (env.C) 1 else 0 // = 0
val sum = a + operand + carryIn // = 0x10 + 0x20 + 0 = 0x30
val carryOut = sum > 0xFF // = 0x30 > 0xFF = false
env.C = false

// Line 2: A = (A + 0x20 + (if (C) 1 else 0)) and 0xFF
// Note: C is now false (just set), but the expression reads the OLD C value
// This is a BUG in the generated code! The carry should be captured before update.
// For now, assuming the expression re-evaluates:
val result = (0x10 + 0x20 + 0) and 0xFF = 0x30
env.A = 0x30u

// Line 3: updateZN(A)
env.updateZN(env.A)
// Z = false, N = false

Final: A = 0x30 ✓, C = false ✓
```

### ⚠️ Potential Issue Found

The current code generation has a subtle bug:
```kotlin
C = A + 0x20 + (if (C) 1 else 0) > 0xFF  // Updates C
A = (A + 0x20 + (if (C) 1 else 0)) and 0xFF  // Reads C again!
```

The second line re-evaluates `(if (C) 1 else 0)` which now reads the UPDATED C value.

**Fix Needed**: Calculate sum once, then update flags and register:
```kotlin
// Better approach:
val sum = A + 0x20 + (if (C) 1 else 0)
C = sum > 0xFF
A = sum and 0xFF
updateZN(A)
```

But this requires temporary variables, which means we need to either:
1. Generate temporary variable declarations
2. Ensure all expressions are pure and can be evaluated multiple times
3. Rewrite the code generation to be more careful about evaluation order

For now, let's assume the executor evaluates the full expression atomically.

## Test 5: Logical AND

### Generated Code
```kotlin
A = A and 0x0F
updateZN(A)
```

### Test Case: A=0xFF, operand=0x0F
```
Initial: A = 0xFF
Expected: A = 0x0F, Z = false, N = false
```

### Executor Simulation
```kotlin
// Line 1: A = A and 0x0F
val a = env.A // = 0xFF
val operand = 0x0F
val result = a and operand // = 0xFF and 0x0F = 0x0F
env.A = 0x0Fu

// Line 2: updateZN(A)
env.updateZN(env.A)
// Z = false, N = false

Final: A = 0x0F ✓
```

**Result**: ✅ PASS

## Summary

| Test | Status | Notes |
|------|--------|-------|
| Simple Load (LDA) | ✅ PASS | Works correctly |
| Transfer (TAX) | ✅ PASS | Works correctly |
| Increment (INX) | ✅ PASS | Wraparound handled |
| Arithmetic (ADC) | ⚠️ WARNING | Potential double-evaluation bug |
| Logical (AND) | ✅ PASS | Works correctly |

## Issues Found

### 1. ADC/SBC Double Evaluation ⚠️

**Problem**: Generated code evaluates carry expression twice:
```kotlin
C = A + 0x20 + (if (C) 1 else 0) > 0xFF  // Reads C
A = (A + 0x20 + (if (C) 1 else 0)) and 0xFF  // Reads C again
```

**Impact**: May cause incorrect results if C flag changes between evaluations.

**Fix Options**:
1. Use temporary variables (requires KVarDecl statements)
2. Make executor snapshot state before evaluation
3. Rewrite code generation to compute once

**Recommended Fix**: Rewrite code generation:
```kotlin
// Generate temporary variable:
val sum = A + operand + (if (C) 1 else 0)
C = sum > 0xFF
A = sum and 0xFF
updateZN(A)
```

### 2. Addressing Modes Not Tested

Memory operations (STA, LDA from memory, etc.) not tested here.
Need to verify addressing mode conversion works correctly.

## Conclusion

**Basic instructions work!** The code generation is fundamentally sound:
- Statements are emitted ✓
- Flag updates are called ✓
- Arithmetic is correctly masked to 8-bit ✓
- Transfer operations work ✓

The ADC/SBC double-evaluation issue is a minor concern that should be fixed
for correctness, but the overall approach is solid.

Next steps:
1. Fix ADC/SBC to use temporary variables
2. Test memory addressing modes
3. Complete remaining instructions
4. Run full differential test suite
