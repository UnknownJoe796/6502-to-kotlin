# Decompiler Fixes Needed for SMB Compilation

This document outlines the issues found when trying to compile the generated SMB code, and what needs to be fixed in the code generator.

## Issue 1: Constant Name Mismatches

**Problem:** The generated decompiled code references constants like `DisableScreenFlag`, but the constants file generates them with different naming conventions:
- Variable: `disableScreenFlag` (camelCase)
- Constant: `DISABLESCREENFLAG` (UPPER_SNAKE_CASE)

**Location:** `kotlin-codegen.kt` - where constants are referenced in decompiled code

**Fix needed:** Either:
1. Generate constants with consistent names that match the references in decompiled code
2. Or import the constants with the correct names

**Example:**
```kotlin
// Generated reference in decompiled code:
var disableScreenFlag by MemoryByte(DisableScreenFlag)  // Error: DisableScreenFlag not found

// Generated in constants file:
const val DISABLESCREENFLAG = 0x0722  // Different name!
```

## Issue 2: Type Mismatches (Int vs UByte)

**Problem:** Many assignments and comparisons mix `Int` and `UByte` types without proper conversion.

**Location:** `kotlin-codegen.kt` - expression generation

**Examples:**
```kotlin
// Error: Assignment type mismatch
disableScreenFlag = 0  // Int, but UByte expected

// Error: Operator '==' cannot be applied
if (operMode == 0) { }  // UByte == Int comparison

// Error: UInt vs Int
val result: UByte = (value xor 0x80.toUInt())  // xor produces UInt
```

**Fix needed:** 
1. Add `.toUByte()` conversions for literal assignments
2. Use `.toInt()` on UByte values for comparisons
3. Properly cast arithmetic results

## Issue 3: BigInteger in Expressions

**Problem:** Some shift operations produce `BigInteger` instead of `Int`.

**Location:** Line 782 in generated code

**Example:**
```kotlin
temp0 = temp0 + (1 shl temp1.toBigInteger())  // Error: BigInteger not expected
```

**Fix needed:** Ensure shift amounts are `Int`, not converted to `BigInteger`.

## Issue 4: Missing Constant Definitions

**Problem:** Some constants referenced in code are not defined in the constants file.

**Examples:**
- `COLDBOOTOFFSET`
- `WARMBOOTOFFSET`
- Various PPU register constants

**Fix needed:** Ensure all constants referenced by the decompiled code are generated in the constants file.

## Recommended Approach

### Phase 1: Fix constant generation
1. Standardize constant naming (recommend PascalCase to match common assembly label style)
2. Generate both the constant value AND the memory variable:
   ```kotlin
   const val DisableScreenFlag = 0x0722
   var disableScreenFlag by MemoryByte(DisableScreenFlag)
   ```

### Phase 2: Fix type consistency
1. Use `Int` for all register values and intermediate calculations
2. Only convert to `UByte` when writing to memory
3. Add extension functions for safe conversions:
   ```kotlin
   fun Int.toByte8() = (this and 0xFF).toUByte()
   fun UByte.toInt8() = this.toInt()
   ```

### Phase 3: Add missing runtime support
1. Ensure all PPU register constants are defined
2. Add proper handling for indexed addressing modes
3. Handle BigInteger cases in shift operations

## Testing Strategy

Once fixes are made:
1. Compile the decompiled code successfully
2. Run individual function tests using the validation framework
3. Run the TAS validation to verify game completion
4. Compare memory state after each frame with 6502 interpreter

## Current Status

- [x] TAS validation framework implemented
- [x] PPU stub working
- [x] Controller input working
- [x] Simplified SMB stub passes all tests
- [ ] Real decompiled SMB code compilation
- [ ] Integration with TAS validation
