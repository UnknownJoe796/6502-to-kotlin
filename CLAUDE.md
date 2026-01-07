# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a 6502 (NES) to Kotlin decompiler implementing a multi-pass compilation architecture. The project decompiles 6502 assembly code into readable Kotlin code through 41 distinct analysis passes organized into 9 phases:

1. **Parsing & Initial Analysis** (Passes 1-3): Parse assembly, resolve addresses, validate instructions
2. **Function & Block Discovery** (Passes 4-8): Find entry points, analyze reachability, construct CFG
3. **Data Flow Analysis** (Passes 9-13): Build dominator trees, analyze liveness, construct call graphs
4. **Structural Analysis** (Passes 14-17): Detect loops/conditionals, form regions, eliminate gotos
5. **Type & Value Analysis** (Passes 18-21): Propagate constants, infer types, analyze stack frames
6. **Expression Reconstruction** (Passes 22-26): Build expression trees, recognize idioms
7. **Variable & Naming** (Passes 27-29): Identify variables, generate names, recover parameters
8. **Optimization & Cleanup** (Passes 30-34): Dead code elimination, simplification
9. **Code Generation** (Passes 35-38): Generate Kotlin, add comments, format output

## Build and Test Commands

### Building the project
```bash
./gradlew build
```

### Running tests
```bash
./gradlew test
```

### Running a single test class
```bash
./gradlew test --tests "ParsingTest"
```

### Running a specific test method
```bash
./gradlew test --tests "ParsingTest.parseSmbDisasmAndBuildSymbols"
```

### Running tests for specific fixes
```bash
# Test utilities and shared functions
./gradlew test --tests "UtilitiesTest"

# Test address resolution improvements  
./gradlew test --tests "Pass2AddressResolutionFixTest"

# Test enhanced validation
./gradlew test --tests "Pass3ValidationEnhancedTest"

# Test entry point discovery improvements
./gradlew test --tests "Pass4EntryPointsJumpTableTest"

# Test basic block construction fixes
./gradlew test --tests "Pass6BasicBlocksFixTest"
```

## Architecture

### Core Data Model
- **AssemblyLine**: Represents a single line of assembly (label, instruction/data/constant, comment)
- **AssemblyConstant**: Assembly-time constant definitions using `=` directive (e.g., `Player_X = $86`)
- **AssemblyInstruction**: Operation + addressing mode combination
- **AssemblyOp**: Enum of all 6502 opcodes with metadata (affected flags, addressing modes)
- **AssemblyAddressing**: Sealed class hierarchy for different addressing modes
- **AssemblyData**: Data directives (.db, etc.)

### Pass Implementation Structure
Each pass is implemented in its own file following the pattern `pass-N-name.kt` where:
- Pass 1 (pass-1-parsing.kt): Contains the core parsing logic and data structures
- Passes 2-8: Individual analysis passes (files exist but may not be implemented yet)
- Each pass operates on the output of previous passes

### Test Structure
Tests follow the pattern `NameTest.kt` and include:
- **ParsingTest**: Tests assembly parsing and symbol table construction using smbdism.asm
- Additional test files for each pass

### Sample Data
- **smbdism.asm**: Super Mario Bros. disassembly used as test input and reference

## Development Notes

### Flag Analysis
The project models 6502 processor flags (N, V, Z, C) and tracks:
- Which instructions affect which flags (`affectedFlags`)
- Which instructions consume flag state (`consumedFlag`)
- Branch conditions (`isBranch`, `flagPositive`)

### Addressing Mode Support
Comprehensive support for all 6502 addressing modes:
- Immediate values (hex, binary, decimal, references)
- Direct memory access and indexed variants
- Indirect addressing modes
- Accumulator operations

### Symbol Resolution
The parser builds symbol tables tracking:
- Label-to-line mappings
- Duplicate label detection
- Support for forward references
- **Assembly-time constants**: Constants defined with `=` directive (e.g., `MyVar = $0722`) are:
  - Parsed in Pass 1 and stored in `AssemblyLine.constant`
  - Resolved to addresses in Pass 2 using multi-pass resolution (supports forward references)
  - Added to the symbol table (`labelToAddress`) alongside labels
  - Support hex values, decimal values, indexed addressing (e.g., `Array = $0200,X`), and constant references

## Critical: NMI Timing for Decompiled Code

**See `docs/NMI-TIMING-INSIGHTS.md` for full details.**

Key points for decompiled Kotlin version:
- NMI handlers can take **1-3 frames** to execute, not always 1
- `IntervalTimerControl` ($077F) is the reliable timing indicator
- Different code paths have different cycle costs (2.4 to 2.8 cycles/step)
- A fixed cycle estimate **cannot work** - the window is too tight
- TAS replay requires accurate frame skip detection
- Frame 5 (init) takes 3 frames; level transitions take 2 frames; normal gameplay takes 1 frame

When debugging timing issues: check IntCtrl progression, FrameCounter, OperMode_Task, and RNG values.

## Function State Capture & Testing

**See `docs/DECOMPILER-TESTING-GUIDE.md` for full details.**

The project includes infrastructure to validate decompiled functions against the interpreter:

### Quick Start
```bash
# Capture function states from TAS replay (creates test data)
./gradlew :core:test --tests "CaptureFromTASTest.capture function states from TAS"

# Output files:
# - local/testgen/captured-tests-happylee-warps.json (raw data)
# - local/testgen/GeneratedFunctionTests.kt (runnable tests)
```

### Key Files
| File | Purpose |
|------|---------|
| `core/src/main/kotlin/testgen/FunctionCallTracer.kt` | Captures function input/output states |
| `core/src/main/kotlin/testgen/TestCaseSelector.kt` | Deduplicates and samples test cases |
| `core/src/main/kotlin/testgen/KotlinTestGenerator.kt` | Generates Kotlin test code |
| `core/src/test/kotlin/testgen/CaptureFromTASTest.kt` | Main capture test |

### Workflow for Fixing Decompiled Functions
1. Run capture to get test data
2. Pick a frequently-called function from the statistics
3. Find function in `smbdism.asm` to understand expected behavior
4. Create a focused test from captured data
5. Fix the decompiler pass that handles that pattern
6. Regenerate decompiled code and verify tests pass

### Captured Data Structure
Each `FunctionCallCapture` contains:
- `inputState`: CPU registers and flags at function entry
- `memoryReads`: Memory addresses read (inputs to set up)
- `outputState`: CPU registers and flags at RTS
- `memoryWrites`: Memory addresses written (outputs to verify)