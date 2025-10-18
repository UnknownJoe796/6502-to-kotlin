# Phase 5: Type & Value Analysis - Implementation Plan

## Overview

Phase 5 focuses on extracting semantic information from assembly code by analyzing constant values, memory access patterns, types, and stack usage. This phase bridges the gap between low-level structural analysis (Phase 4) and high-level expression reconstruction (Phase 6).

## Goals

1. **Track constant values** through execution to enable optimizations
2. **Identify memory access patterns** (arrays, structs, pointers vs. scalars)
3. **Infer semantic types** from usage (boolean, counter, index, enum, etc.)
4. **Analyze stack frames** to find saved registers and parameters

## Pass Dependencies

```
Phase 3 (Passes 9-13): Data Flow Analysis
         ↓
Phase 4 (Passes 14-17): Structural Analysis
         ↓
Phase 5 (Passes 18-21): Type & Value Analysis
         ├── Pass 18: Constant Propagation (foundation)
         ├── Pass 21: Stack Frame Analysis (independent)
         ├── Pass 19: Memory Access Patterns (uses Pass 18)
         └── Pass 20: Type Inference (uses all above)
```

## Pass 18: Constant Propagation

### Purpose
Track which values are constant at each program point to enable optimizations and simplifications.

### Key Insights
- Many 6502 programs use immediate loads: `LDA #$05`
- Constants can be propagated through register transfers
- Enables branch elimination and dead code detection
- Foundation for type inference and optimization

### Data Structures

```kotlin
// Constant value representation
sealed class ConstantValue {
    data class Byte(val value: Int) : ConstantValue()  // 0-255
    data class Word(val high: Int, val low: Int) : ConstantValue()  // 16-bit value
    object Unknown : ConstantValue()
    object Bottom : ConstantValue()  // Conflicting values (not constant)
}

// Abstract state at each program point
data class ConstantState(
    val registerA: ConstantValue,
    val registerX: ConstantValue,
    val registerY: ConstantValue,
    val memory: Map<Int, ConstantValue>,  // address -> value
    val flags: Map<ProcessorFlag, Boolean?>  // null = unknown
)

// Per-block constant analysis
data class BlockConstantFacts(
    val entryState: ConstantState,
    val exitState: ConstantState,
    val foldableInstructions: Set<Int>  // line indices where we can fold constants
)

// Function-level results
data class FunctionConstantAnalysis(
    val function: FunctionCfg,
    val blockFacts: Map<Int, BlockConstantFacts>,  // leader -> facts
    val constantExpressions: List<ConstantExpression>
)

data class ConstantExpression(
    val lineIndex: Int,
    val originalInstruction: AssemblyInstruction,
    val constantResult: ConstantValue,
    val canEliminate: Boolean  // true if this is dead code
)

data class ConstantPropagationAnalysis(
    val functions: List<FunctionConstantAnalysis>
)
```

### Algorithm

1. **Lattice-based dataflow analysis**: Use standard constant propagation lattice (Top → Constants → Bottom)
2. **Forward analysis**: Propagate constants from function entry through CFG
3. **Join operation**: At merge points, if values differ → Bottom (not constant)
4. **Transfer function**: For each instruction, compute output state from input state
5. **Iterate to fixed point**: Handle loops properly

### Key Features

- Track immediate loads: `LDA #$05` → A = 5
- Propagate through arithmetic: if A=5 and `ADC #$03`, then A=8
- Handle register transfers: `TAX` with A=5 → X=5
- Track memory stores and loads for constants
- Detect unreachable branches: `LDA #$00; BNE target` → never taken
- Mark foldable expressions for later optimization

### Dependencies
- Pass 10 (Data Flow Analysis)
- Pass 7 (CFG Construction)

---

## Pass 19: Memory Access Pattern Analysis

### Purpose
Identify how memory is accessed to distinguish arrays, structures, and scalars.

### Key Patterns (6502-specific)

- **Scalar**: `LDA $80` - fixed address
- **Array**: `LDA $200,X` - base + index register
- **Struct field**: `LDA ($80),Y` - indirect indexed (common in NES)
- **Pointer**: `LDA ($80)` - indirect addressing through zero page

### Data Structures

```kotlin
// Memory access pattern
sealed class MemoryAccessPattern {
    // Single scalar variable at fixed address
    data class Scalar(val address: Int, val name: String?) : MemoryAccessPattern()

    // Array accessed with index register
    data class Array(
        val baseAddress: Int,
        val indexRegister: Variable,  // X or Y
        val elementSize: Int,  // typically 1
        val estimatedLength: Int?,
        val name: String?
    ) : MemoryAccessPattern()

    // Structure field access
    data class StructField(
        val basePointer: Int,  // zero page pointer
        val offset: Int,
        val fieldName: String?
    ) : MemoryAccessPattern()

    // Pointer indirection
    data class Pointer(
        val pointerAddress: Int,  // zero page typically
        val pointsTo: MemoryRegion?,
        val name: String?
    ) : MemoryAccessPattern()

    // Indexed indirect or indirect indexed
    data class IndirectArray(
        val basePointer: Int,
        val indexRegister: Variable,
        val name: String?
    ) : MemoryAccessPattern()
}

data class MemoryRegion(
    val startAddress: Int,
    val endAddress: Int,
    val regionType: RegionType,
    val name: String?
)

enum class RegionType {
    CODE,
    DATA,
    ZERO_PAGE,
    STACK,
    IO_REGISTERS,
    ROM,
    RAM
}

// Analysis per memory location
data class MemoryLocationInfo(
    val address: Int,
    val accessPattern: MemoryAccessPattern,
    val reads: List<AssemblyLineReference>,
    val writes: List<AssemblyLineReference>,
    val isConstant: Boolean,  // never written after init
    val isIndexed: Boolean,   // accessed with ,X or ,Y
    val indirectionLevel: Int // 0=direct, 1=indirect, 2=double indirect
)

data class FunctionMemoryAnalysis(
    val function: FunctionCfg,
    val memoryAccesses: Map<Int, MemoryLocationInfo>,  // address -> info
    val identifiedArrays: List<MemoryAccessPattern.Array>,
    val identifiedStructures: List<MemoryAccessPattern.StructField>,
    val identifiedPointers: List<MemoryAccessPattern.Pointer>
)

data class MemoryAccessAnalysis(
    val functions: List<FunctionMemoryAnalysis>,
    val globalMemoryMap: Map<Int, MemoryLocationInfo>
)
```

### Algorithm

1. **Scan all memory accesses**: Collect reads/writes per address
2. **Detect arrays**: Look for patterns like `LDA array,X` with varying X values
3. **Detect structures**: Look for indirect addressing with multiple offsets
4. **Detect pointers**: Track zero-page addresses used in indirect mode
5. **Infer array bounds**: Use max observed index + constant propagation
6. **Classify memory regions**: Use address ranges (ZP, IO, RAM, ROM)

### Heuristics

- If address < $100 → Zero Page (likely pointers or frequently-used vars)
- If accessed with `,X` or `,Y` and X/Y varies → Array
- If indirect addressing `(ptr),Y` → Structure or dynamic array
- If only written once (in init code) → Constant/ROM data
- If address in $2000-$2007 range → PPU registers (NES-specific)

### Dependencies
- Pass 10 (Data Flow Analysis)
- Pass 18 (Constant Propagation helps bound analysis)

---

## Pass 20: Type Inference

### Purpose
Infer semantic types from usage patterns to generate more readable code.

### Type System

```kotlin
// Inferred type system
sealed class InferredType {
    // Primitive types
    data class UInt8(val value: IntRange? = null) : InferredType()  // 0-255
    data class Int8(val value: IntRange? = null) : InferredType()   // -128 to 127
    data class UInt16(val value: IntRange? = null) : InferredType() // 16-bit
    data class Boolean : InferredType()  // 0 or 1
    data class Enum(val possibleValues: Set<Int>, val name: String?) : InferredType()

    // Composite types
    data class Array(val elementType: InferredType, val length: Int?) : InferredType()
    data class Pointer(val pointsTo: InferredType) : InferredType()
    data class Struct(val fields: Map<Int, FieldInfo>) : InferredType()  // offset -> field

    // Special types
    object Counter : InferredType()  // Incremented/decremented, compared
    object Index : InferredType()    // Used as array index
    object BitFlags : InferredType() // Used with AND/OR/EOR bit operations
    object Unknown : InferredType()
}

data class FieldInfo(
    val offset: Int,
    val type: InferredType,
    val name: String?
)

// Type constraint from usage
sealed class TypeConstraint {
    data class MustBe(val type: InferredType) : TypeConstraint()
    data class UsedInComparison(val signed: Boolean) : TypeConstraint()
    data class UsedInBranch : TypeConstraint()  // Suggests boolean
    data class UsedAsIndex : TypeConstraint()   // Suggests index/counter
    data class IncrementedDecremented : TypeConstraint()  // Counter
    data class BitManipulated : TypeConstraint()  // BitFlags
    data class LimitedValues(val values: Set<Int>) : TypeConstraint()  // Enum
}

// Variable with inferred type
data class TypedVariable(
    val variable: Variable,
    val inferredType: InferredType,
    val confidence: Double,  // 0.0 - 1.0
    val constraints: List<TypeConstraint>,
    val usageCount: Int
)

data class FunctionTypeInfo(
    val function: FunctionCfg,
    val variableTypes: Map<Variable, TypedVariable>,
    val parameters: List<TypedVariable>,  // Identified input parameters
    val returns: List<TypedVariable>,     // Identified return values
    val localVariables: Set<TypedVariable>
)

data class TypeInferenceAnalysis(
    val functions: List<FunctionTypeInfo>,
    val globalTypes: Map<Variable, TypedVariable>
)
```

### Algorithm

1. **Collect constraints**: Scan all uses of each variable
2. **Boolean inference**: Variable only used in branches → Boolean
3. **Counter detection**: Inc/dec + comparison → Counter
4. **Index detection**: Used in `,X` or `,Y` addressing → Index
5. **Signed detection**: Use of BMI/BPL after CMP → signed comparison
6. **Enum detection**: Variable has limited set of values (< 10 distinct) → Enum
7. **Pointer detection**: Used in indirect addressing → Pointer
8. **Array type inference**: Index into array → Array with element type
9. **Unification**: Combine constraints to find most specific type
10. **Confidence scoring**: Based on how many constraints agree

### Type Inference Rules

- Only used in `BEQ`/`BNE` after load → Boolean
- Incremented, then compared in loop → Counter/Index
- Values always 0,1,2,3 → Enum or small counter
- Loaded before `STA (ptr),Y` → Pointer deref
- Used with BMI/BPL → likely signed
- Used with `AND #$01` → Boolean or bit flag

### Dependencies
- Pass 10 (Data Flow Analysis)
- Pass 18 (Constant Propagation)
- Pass 19 (Memory Access Patterns)

---

## Pass 21: Stack Frame Analysis

### Purpose
Analyze stack operations to find saved registers and parameter passing.

### Stack Patterns (6502)

```asm
; Register save/restore
PHA            ; Save A
TXA
PHA            ; Save X
...
PLA            ; Restore X
TAX
PLA            ; Restore A

; Parameter passing (rare in 6502)
LDA param
PHA
JSR function
```

### Data Structures

```kotlin
// Stack operation
sealed class StackOp {
    data class Push(val lineRef: AssemblyLineReference, val value: Variable) : StackOp()
    data class Pull(val lineRef: AssemblyLineReference, val target: Variable) : StackOp()
}

// Matched push/pull pair
data class StackFrame(
    val pushOp: StackOp.Push,
    val pullOp: StackOp.Pull,
    val isParameterPassing: Boolean,
    val isRegisterSave: Boolean
)

// Stack slot at a specific depth
data class StackSlot(
    val depth: Int,  // Relative to entry SP
    val pushedValue: Variable?,
    val pushedAt: AssemblyLineReference?,
    val pulledAt: AssemblyLineReference?
)

// Stack state at a program point
data class StackState(
    val depth: Int,  // Current depth relative to function entry
    val slots: List<StackSlot>
)

// Function stack analysis
data class FunctionStackAnalysis(
    val function: FunctionCfg,
    val entryStackDepth: Int,  // SP at entry
    val exitStackDepth: Int,   // SP at exit (should match entry for well-formed)
    val maxStackDepth: Int,    // Maximum stack usage
    val savedRegisters: List<StackFrame>,  // Registers saved at entry, restored at exit
    val parameters: List<StackParameter>,  // Parameters passed via stack
    val localVariables: List<StackVariable>,  // Local variables on stack
    val isStackBalanced: Boolean  // Entry depth == exit depth
)

data class StackParameter(
    val slot: StackSlot,
    val type: InferredType,
    val name: String?
)

data class StackVariable(
    val slot: StackSlot,
    val type: InferredType,
    val name: String?,
    val scope: IntRange  // Line range where variable is live
)

data class StackFrameAnalysis(
    val functions: List<FunctionStackAnalysis>
)
```

### Algorithm

1. **Track stack depth**: Simulate SP through execution
   - Entry: SP = initial value
   - `PHA`/`PHP`: SP -= 1
   - `PLA`/`PLP`: SP += 1
   - `JSR`: SP -= 2 (return address)
   - `RTS`: SP += 2
2. **Match push/pull pairs**:
   - Forward scan for `PHA` → backward scan for matching `PLA`
   - Use dataflow to track stack depth at each point
3. **Identify register saves**: Common pattern:
   ```asm
   PHA        ; Save A
   TXA
   PHA        ; Save X
   ...
   PLA        ; Restore X
   TAX
   PLA        ; Restore A
   ```
4. **Detect parameter passing**:
   - Push before `JSR` → parameters
   - Pull after `RTS` → return values
5. **Calculate stack frame layout**:
   - Entry: saved registers
   - Middle: local variables
   - Exit: restore registers
6. **Verify balance**: Entry SP == Exit SP for all paths

### 6502 Stack Details

- Stack is page 1: $0100-$01FF (256 bytes)
- SP is 8-bit offset from $0100
- Stack grows downward
- Most code doesn't use stack heavily (register-based)
- Stack mainly for JSR/RTS and temporary saves

### Dependencies
- Pass 10 (Data Flow Analysis)
- Pass 7 (CFG Construction)

---

## Implementation Timeline

### Order of Implementation

1. **Pass 18** (Constant Propagation) - Foundation for other analyses
2. **Pass 21** (Stack Frame) - Independent, simpler to implement
3. **Pass 19** (Memory Patterns) - Uses constants from Pass 18
4. **Pass 20** (Type Inference) - Uses all previous passes

### Testing Approach

1. **Create `Phase5AnalysisDemo.kt`**: Similar to Phase4AnalysisDemo
   - Test constant propagation on simple functions
   - Show memory access patterns found
   - Display inferred types for variables
   - Show stack frame analysis

2. **Unit tests**:
   - `ConstantPropagationTest.kt`: Test various constant scenarios
   - `MemoryAccessTest.kt`: Test array/struct/pointer detection
   - `TypeInferenceTest.kt`: Test type inference rules
   - `StackFrameTest.kt`: Test PHA/PLA matching

3. **Integration points**:
   - Phase 5 builds on Phase 3 (data flow) and Phase 4 (structure)
   - Outputs feed into Phase 6 (expression reconstruction)
   - Type info crucial for Phase 7 (variable naming)

4. **Validation**:
   - Run on Super Mario Bros code
   - Verify detected arrays (sprite tables, etc.)
   - Check inferred types make sense
   - Ensure stack analysis is balanced

---

## Expected Outcomes

After Phase 5, we should be able to:

1. **Know constant values** at each program point
2. **Distinguish** between scalars, arrays, structures, and pointers
3. **Assign semantic types** to variables (boolean, counter, etc.)
4. **Understand stack usage** and parameter passing
5. **Enable Phase 6** (Expression Reconstruction) with rich type information
6. **Support Phase 7** (Variable Naming) with semantic hints

## Success Metrics

- Constant propagation finds 80%+ of immediate loads
- Memory pattern detection identifies known arrays (e.g., sprite tables)
- Type inference assigns non-"Unknown" types to 70%+ of variables
- Stack analysis correctly matches 95%+ of PHA/PLA pairs
- All analysis completes in reasonable time (< 5 seconds for SMB)

## Integration with Later Phases

### Phase 6: Expression Reconstruction
- Use constant values to simplify expressions
- Use type info to generate properly-typed expressions
- Use memory patterns to generate array/struct accesses

### Phase 7: Variable & Naming
- Use inferred types for better variable names
- Use memory patterns to distinguish var types
- Use stack analysis to name parameters

### Phase 8: Optimization
- Use constant propagation for dead code elimination
- Use type info for algebraic simplification
- Use stack analysis to optimize register allocation
