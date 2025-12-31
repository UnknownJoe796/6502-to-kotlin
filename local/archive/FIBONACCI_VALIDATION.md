# Fibonacci Initialization Validation - Complete!

## Overview

Successfully validated the decompilation of a real-world 6502 subroutine: the initialization portion of a Fibonacci calculator from https://gist.github.com/pedrofranceschi/1285964

## The 6502 Subroutine

This subroutine sets up the Fibonacci calculation for computing the 7th Fibonacci number:

```assembly
LDX #$01      ; x = 1 (first Fibonacci number)
STX $00       ; Store x in memory location $00
SEC           ; Set carry for subtraction
LDY #$07      ; Y = 7 (which Fibonacci number to compute)
TYA           ; Transfer Y to A
SBC #$03      ; A = 7 - 3 = 4 (iteration count)
TAY           ; Transfer A back to Y
CLC           ; Clear carry for addition
LDA #$02      ; A = 2 (second Fibonacci number)
STA $01       ; Store A in memory location $01
```

### Memory Layout After Execution

- `$00` = 1 (previous Fibonacci value)
- `$01` = 2 (current Fibonacci value)
- A = 2
- X = 1
- Y = 4 (iterations remaining)
- C = 0 (clear for addition)

## Generated Kotlin Code

The decompiler successfully generates this Kotlin code:

```kotlin
// Generated from 6502 assembly
class Generated6502 {
    // CPU Registers
    var A: UByte = 0u
    var X: UByte = 0u
    var Y: UByte = 0u
    var SP: UByte = 0xFFu

    // Flags
    var N: Boolean = false
    var V: Boolean = false
    var Z: Boolean = false
    var C: Boolean = false
    var I: Boolean = false
    var D: Boolean = false

    // Memory (64K)
    val memory = ByteArray(65536)

    fun updateZN(value: UByte) {
        Z = value == 0.toUByte()
        N = (value.toInt() and 0x80) != 0
    }

    fun execute() {
        // LDX
        X = 1u
        updateZN(X)
        // STX
        memory[0] = X.toByte()
        // SEC
        C = true
        // LDY
        Y = 7u
        updateZN(Y)
        // TYA
        A = Y
        updateZN(A)
        // SBC
        val result = A.toInt() - (3u).toInt() - (if (C) 0 else 1)
        C = result >= 0
        val resultByte = (result and 0xFF).toUByte()
        V = ((A.toInt() xor resultByte.toInt()) and 0x80) != 0 && ((A.toInt() xor (3u).toInt()) and 0x80) != 0
        A = resultByte
        updateZN(A)
        // TAY
        Y = A
        updateZN(Y)
        // CLC
        C = false
        // LDA
        A = 2u
        updateZN(A)
        // STA
        memory[1] = A.toByte()
    }
}
```

## Validation Result

✅ **PASSED** - End-to-end validation successful!

The validation framework:
1. Executed the 6502 assembly through the interpreter
2. Generated the Kotlin code shown above
3. Compiled it with kotlinc
4. Executed the compiled code
5. Compared the final states

### State Comparison

| Component | Interpreter | Decompiled | Match |
|-----------|------------|------------|-------|
| Register A | 0x02 | 0x02 | ✅ |
| Register X | 0x01 | 0x01 | ✅ |
| Register Y | 0x04 | 0x04 | ✅ |
| Flag N | false | false | ✅ |
| Flag V | false | false | ✅ |
| Flag Z | false | false | ✅ |
| Flag C | false | false | ✅ |
| Flag I | false | false | ✅ |
| Flag D | false | false | ✅ |
| Memory[0x00] | 0x01 | 0x01 | ✅ |
| Memory[0x01] | 0x02 | 0x02 | ✅ |

**Result**: Perfect match! The decompiled Kotlin code produces semantically equivalent results to the original 6502 assembly.

## What This Demonstrates

### Correctly Handled Instructions

1. **Load/Store**: LDX, LDY, LDA, STX, STA
2. **Arithmetic**: SBC (subtract with borrow)
3. **Transfer**: TYA (Y→A), TAY (A→Y)
4. **Flag Control**: SEC (set carry), CLC (clear carry)
5. **Flag Updates**: Proper N and Z flag handling

### Correctly Handled Features

1. **Immediate Addressing**: `LDX #$01`, `LDY #$07`, `SBC #$03`, `LDA #$02`
2. **Direct Addressing**: `STX $00`, `STA $01`
3. **Overflow Flag Logic**: Complex V flag calculation for SBC
4. **Carry Flag Logic**: Proper carry handling in subtraction
5. **Zero-Page Memory**: Operations on addresses $00 and $01

## Running the Test

```bash
# Run the Fibonacci initialization validation
./gradlew test --tests "DecompilerValidationTest.testFibonacciInitialization"

# View the generated Kotlin code
./gradlew test --tests "DecompilerValidationTest.showFibonacciGeneratedCode"
./local/show-fibonacci-code.sh
```

## Code Quality Analysis

### Strengths

- ✅ Functionally correct - produces identical results
- ✅ All register operations preserved
- ✅ All flag operations preserved
- ✅ Memory operations correct
- ✅ Compiles and runs successfully

### Areas for Improvement (Future Work)

- Verbose comments (every instruction labeled)
- No variable names (uses raw memory addresses)
- No recognition of patterns (e.g., "this is Fibonacci setup")
- No type inference (everything is UByte/Boolean)
- No optimization (temporary variables not eliminated)

These improvements will come from the full 41-pass decompilation pipeline. The current `SimpleDecompiler` is designed for validation, not production-quality output.

## Significance

This is the **first successful end-to-end validation** of a real-world 6502 subroutine through the complete pipeline:

```
6502 Assembly → Interpreter → Kotlin Code → Compilation → Execution → Validation
```

This proves the validation framework is ready for:
1. Testing control flow (loops, branches)
2. Testing complete functions
3. Testing the full 41-pass decompiler
4. Regression testing as the decompiler evolves

## Next Steps

### Immediate: Add Loop Support

The original Fibonacci calculator has a loop body that we need to support:

```assembly
loop:
    LDX $01    ; x = a
    ADC $00    ; a += x
    STA $01    ; stores a
    STX $00    ; stores x
    DEY        ; y -= 1
    BNE loop   ; branch if not zero
```

To validate this, we need to:
1. Add BNE (Branch if Not Equal) instruction support
2. Generate `while` loops in Kotlin
3. Handle backward branches
4. Validate loop semantics

### Short-term: More Real-World Subroutines

From the RealWorld6502Test suite:
- 8-bit multiplication (repeated addition)
- Sum 1 to N (simple loop)
- Memory copy routine
- Bit manipulation patterns

### Long-term: Complete SMB Functions

Once control flow is working, validate complete Super Mario Bros. functions from the disassembly.

## Test Statistics

- **New tests added**: 2 (testFibonacciInitialization, showFibonacciGeneratedCode)
- **Total validation tests**: 12
- **Total project tests**: 237
- **All tests passing**: ✅

## Conclusion

The validation framework successfully handles a real-world 6502 subroutine with complex operations including arithmetic, transfers, and flag manipulation. This is a major milestone toward validating the full decompiler pipeline!
