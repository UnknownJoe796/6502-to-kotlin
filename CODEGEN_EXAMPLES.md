# Code Generation Examples

This document shows what Kotlin code is generated for various 6502 instructions after the fixes.

## Load Instructions

### LDA #$42
```kotlin
A = 0x42
updateZN(A)
```

### LDX #$10
```kotlin
X = 0x10
updateZN(X)
```

### LDY #$20
```kotlin
Y = 0x20
updateZN(Y)
```

## Register Transfers

### TAX (Transfer A to X)
```kotlin
X = A
updateZN(X)
```

### TAY (Transfer A to Y)
```kotlin
Y = A
updateZN(Y)
```

### TXA (Transfer X to A)
```kotlin
A = X
updateZN(A)
```

### TYA (Transfer Y to A)
```kotlin
A = Y
updateZN(A)
```

## Arithmetic Operations

### ADC #$20 (Add with Carry)
```kotlin
C = A + 0x20 + (if (C) 1 else 0) > 0xFF
A = (A + 0x20 + (if (C) 1 else 0)) and 0xFF
updateZN(A)
```

### SBC #$10 (Subtract with Carry/Borrow)
```kotlin
C = A - 0x10 - (if (C) 0 else 1) >= 0
A = (A - 0x10 - (if (C) 0 else 1)) and 0xFF
updateZN(A)
```

## Increment/Decrement

### INX (Increment X)
```kotlin
X = (X + 1) and 0xFF
updateZN(X)
```

### INY (Increment Y)
```kotlin
Y = (Y + 1) and 0xFF
updateZN(Y)
```

### DEX (Decrement X)
```kotlin
X = (X - 1) and 0xFF
updateZN(X)
```

### DEY (Decrement Y)
```kotlin
Y = (Y - 1) and 0xFF
updateZN(Y)
```

## Logical Operations

### AND #$0F (Bitwise AND)
```kotlin
A = A and 0x0F
updateZN(A)
```

### ORA #$80 (Bitwise OR)
```kotlin
A = A or 0x80
updateZN(A)
```

### EOR #$FF (Bitwise XOR)
```kotlin
A = A xor 0xFF
updateZN(A)
```

## Complete Sequence Example

### LDA #$10, ADC #$20, TAX
```kotlin
A = 0x10
updateZN(A)
C = A + 0x20 + (if (C) 1 else 0) > 0xFF
A = (A + 0x20 + (if (C) 1 else 0)) and 0xFF
updateZN(A)
X = A
updateZN(X)
```

Expected result (assuming C was false initially):
- A = 0x30
- X = 0x30
- Z = false (result is not zero)
- N = false (bit 7 is not set)
- C = false (no overflow from addition)

## Key Features

1. **All instructions now emit statements** - No more context-only updates
2. **Flag updates included** - Every operation that affects flags calls `updateZN()`
3. **Carry flag handling** - ADC and SBC properly read and update the carry flag
4. **Valid Kotlin syntax** - Generated code uses proper if expressions and operators
5. **Type safety** - All values are masked with `and 0xFF` to stay in byte range

## Execution Model

The generated code assumes an execution environment with:

- **Registers**: `A`, `X`, `Y` (UByte values)
- **Flags**: `N`, `V`, `Z`, `C`, `I`, `D` (Boolean values)
- **Helper function**: `updateZN(value: UByte)` that sets:
  - `Z = (value == 0)`
  - `N = (value and 0x80) != 0`

This matches the `ExecutionEnvironment` class in `KotlinExecutor.kt`.
