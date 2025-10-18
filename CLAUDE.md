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