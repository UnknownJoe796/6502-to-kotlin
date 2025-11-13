# SMB Comprehensive Differential Testing

## Overview

This testing infrastructure validates the 6502-to-Kotlin decompiler by testing **all 59 functions** from Super Mario Bros. against the interpreter with random initial states.

## What Was Built

### 1. Function Analysis

**scan_functions.py** - Analyzes the SMB disassembly to identify all testable functions:
- Total labels in SMB: 1,992
- JSR targets (called functions): 295
- Functions with RTS (proper subroutines): 60
- Testable functions (excluding JumpEngine): 59
  - **42 leaf functions** (no JSR calls - easier to test)
  - **17 non-leaf functions** (make JSR calls - need mocking)

**smb_functions.txt** - Catalog of all functions organized by type

### 2. Test Generation

**generate_tests.py** - Automatically generates comprehensive test files:
- Creates individual test method for each function
- Generates summary test that runs all functions
- Output: `SMBComprehensiveDifferentialTest.kt` (1,269 lines, 60 test methods)

### 3. Core Testing Infrastructure

**SMBDifferentialTestGenerator.kt** - The heart of differential testing:

```kotlin
fun testFunction(functionName: String, numTests: Int = 10): FunctionTestResult
```

For each function:
1. **Generate 10 random initial states**
   - Random A, X, Y, SP registers
   - Random N, V, Z, C, I, D flags
   - Randomized memory:
     - Zero Page: $0000-$00FF
     - Stack: $0100-$01FF
     - Work RAM: $0200-$02FF
     - Sprite data: $0300-$03FF

2. **Execute in interpreter**
   - Load function from SMB disassembly
   - Apply initial state
   - Execute all instructions
   - Capture final state

3. **Execute generated Kotlin code**
   - Generate Kotlin from 6502 instructions
   - Execute using KotlinExecutor
   - Capture final state

4. **Compare results**
   - Compare ALL NES RAM (0x0000-0x2FFF = 12KB)
   - Compare all registers (A, X, Y, SP)
   - Compare all flags (N, V, Z, C, I, D)
   - Report all differences

### 4. Test Files Generated

**SMBComprehensiveDifferentialTest.kt** - 59 individual function tests + 1 summary:
- Each function gets its own `@Test` method
- Tests run independently (failures don't cascade)
- Detailed failure reporting with first 5 differences
- Pass/fail statistics

**QuickDifferentialValidationTest.kt** - Infrastructure validation:
- Tests random state generation
- Tests state comparison logic
- Tests a few simple functions
- Verifies infrastructure works before running full suite

## Usage

### Run All Tests

```bash
./gradlew test --tests "SMBComprehensiveDifferentialTest.testAllFunctions_Summary"
```

This will:
- Test all 59 SMB functions
- 10 random states per function = 590 total test cases
- Print summary with pass/fail statistics
- Report which functions pass/fail

### Run Individual Function Tests

```bash
# Test a specific function
./gradlew test --tests "SMBComprehensiveDifferentialTest.test_DoNothing2"

# Test all leaf functions
./gradlew test --tests "SMBComprehensiveDifferentialTest.test_*" --tests "*Leaf*"
```

### Run Quick Validation

```bash
./gradlew test --tests "QuickDifferentialValidationTest"
```

## Test Output Format

### Per-Function Output

```
Testing function: BoundingBoxCore
BoundingBoxCore: 8/10 passed (80.0%)
  Failed tests:
    Test #3:
      - A: interpreter=42, kotlin=40
      - Memory[0x0100]: interpreter=0xFF, kotlin=0x00
      - N: interpreter=true, kotlin=false
      ... and 12 more differences
```

### Summary Output

```
================================================================================
SMB Comprehensive Differential Test - Summary
================================================================================
Testing 59 functions...

✓ PASS  BoundingBoxCore                    10/10 (100%)
✗ FAIL  CheckForCoinMTiles                 3/10 (30%)
✓ PASS  CyclePlayerPalette                 10/10 (100%)
...

================================================================================
Summary:
--------------------------------------------------------------------------------
Functions tested: 59
  Fully passing (100%): 23
  Partially passing: 18
  Fully failing (0%): 18

Total test cases: 590
  Passed: 387
  Failed: 203
  Overall pass rate: 65.6%
================================================================================
```

## Function Categories

### Leaf Functions (42) - No JSR Calls

These are the easiest to test as they don't call other functions:

- BoundingBoxCore
- CheckForCoinMTiles
- CyclePlayerPalette
- DoNothing2
- DonePlayerTask
- Dump_Sq2_Regs
- Dump_Squ1_Regs
- EnemyGfxHandler
- EraseEnemyObject
- ExecGameLoopback
- FindAreaPointer
- FirebarSpin
- GetAreaObjXPosition
- GetAreaObjYPosition
- GetAreaType
- GetBlockBufferAddr
- GetEnemyBoundBoxOfsArg
- GetLrgObjAttrib
- GetObjRelativePosition
- GetProperObjOffset
- GetScreenPosition
- GoContinue
- HandlePipeEntry
- IncAreaObjOffset
- InitBlock_XY_Pos
- InitScroll
- InitVStf
- LoadEnvelopeData
- MoveLiftPlatforms
- MovePlayerYAxis
- MusicHandler
- OperModeExecutionTree
- PlayerEnemyDiff
- PosPlatform
- ProcessLengthData
- ReadJoypads
- RemBridge
- ResetPalStar
- SoundEngine
- SpawnBrickChunks
- SubtEnemyYPos
- WritePPUReg1

### Non-Leaf Functions (17) - Make JSR Calls

These functions call other functions and may need mocking:

- BrickShatter
- CheckForClimbMTiles
- CheckForSolidMTiles
- DrawExplosion_Fireworks
- DrawPlayerLoop
- EnemyLanding
- FireballBGCollision
- GameCoreRoutine
- GetPipeHeight
- KillEnemyAboveBlock
- MoveESprColOffscreen
- MoveEnemyHorizontally
- ReplaceBlockMetatile
- SetXMoveAmt
- StopPlatforms
- TerminateGame
- VariableObjOfsRelPos

## Implementation Details

### Random State Generation

```kotlin
fun generateRandomState(seed: Long): CPUState {
    val random = Random(seed)

    // Random registers
    val a = random.nextInt(256).toUByte()
    val x = random.nextInt(256).toUByte()
    val y = random.nextInt(256).toUByte()
    ...

    // Randomize key memory ranges
    for (addr in 0x0000..0x00FF) {  // Zero page
        memory[addr] = random.nextInt(256).toUByte()
    }
    ...
}
```

### State Comparison

```kotlin
fun compareStates(
    interpreterState: CPUState,
    kotlinState: CPUState
): List<String> {
    val differences = mutableListOf<String>()

    // Compare registers
    if (interpreterState.A != kotlinState.A) {
        differences.add("A: interpreter=..., kotlin=...")
    }

    // Compare ALL NES RAM (0x0000-0x2FFF)
    for (addr in 0x0000..0x2FFF) {
        if (interpreterState.memory[addr] != kotlinState.memory[addr]) {
            differences.add("Memory[$addr]: ...")
        }
    }

    return differences
}
```

### Execution Flow

```
For each function:
  For each of 10 random states:
    1. Generate random initial state (seed-based)
    2. Execute in interpreter:
       - Apply initial state to CPU/memory
       - Execute all function instructions
       - Capture final state (all RAM + registers + flags)
    3. Execute in generated Kotlin:
       - Generate Kotlin code from 6502
       - Execute via KotlinExecutor.executeDirectly()
       - Capture final state (all RAM + registers + flags)
    4. Compare states:
       - Compare all registers
       - Compare all flags
       - Compare all NES RAM (12KB)
       - Record all differences
    5. Report results:
       - Pass if states match exactly
       - Fail with detailed difference report
```

## Expected Results

### Current Status

This is the **first comprehensive test** of the decompiler against real game code.

**Expected:**
- Many failures initially (decompiler bugs to fix)
- Simple functions (DoNothing2, WritePPUReg1) may pass
- Complex functions likely to fail until bugs are fixed

**Common Failure Modes:**
1. **Missing statements** - Instructions don't emit code
2. **Wrong flag updates** - Z, N, C, V not set correctly
3. **Memory access bugs** - Incorrect addressing mode translation
4. **Carry handling** - ADC/SBC carry propagation errors
5. **Stack operations** - Push/pull not working correctly

### Success Criteria

**Phase 1: Leaf Functions**
- Goal: 90%+ of 42 leaf functions passing all 10 tests
- These don't call other functions, should be easiest

**Phase 2: Non-Leaf Functions**
- Goal: 90%+ of 17 non-leaf functions passing
- May require function call mocking

**Phase 3: Overall**
- Goal: 100% of 59 functions passing all tests
- This validates the entire decompiler

## Next Steps

### 1. Run Tests (Current Step)

```bash
# Quick validation
./gradlew test --tests "QuickDifferentialValidationTest"

# Full test suite
./gradlew test --tests "SMBComprehensiveDifferentialTest"
```

### 2. Analyze Failures

- Identify common failure patterns
- Categorize by failure type (flags, memory, registers)
- Prioritize high-impact bugs

### 3. Fix Bugs

- Fix code generator bugs one by one
- Re-run tests to verify fixes
- Track progress toward 100% pass rate

### 4. Add Function Mocking

For non-leaf functions that call other functions:
```kotlin
// Mock JSR calls to return without executing
if (instruction.op == AssemblyOp.JSR) {
    // Return immediately instead of calling function
    return emptyList()
}
```

### 5. Validate Complete SMB Decompilation

Once all tests pass:
- Generate complete Kotlin codebase from SMB
- Verify it compiles
- Compare behavior against original game

## Files in This System

### Python Scripts
- `scan_functions.py` - Analyze SMB disassembly
- `generate_tests.py` - Generate test files

### Kotlin Test Files
- `SMBDifferentialTestGenerator.kt` - Core testing engine
- `SMBComprehensiveDifferentialTest.kt` - Generated comprehensive tests (59 functions)
- `QuickDifferentialValidationTest.kt` - Infrastructure validation
- `SMBFunctionScanner.kt` - Kotlin-based function analyzer

### Data Files
- `smb_functions.txt` - Catalog of all 59 functions

### Existing Infrastructure (Used)
- `IntegrationTest.kt` - CPUState management
- `TranslationValidator.kt` - Test runner
- `KotlinExecutor.kt` - Dynamic Kotlin execution
- `stages/SMBTestFixtures.kt` - SMB code loading

## Key Metrics

| Metric | Value |
|--------|-------|
| Total SMB functions | 59 |
| Leaf functions | 42 |
| Non-leaf functions | 17 |
| Tests per function | 10 |
| Total test cases | 590 |
| RAM validated per test | 12KB (0x0000-0x2FFF) |
| Lines of test code | ~2,500 |
| Test methods generated | 60 |

## Architecture

```
SMB Disassembly (smbdism.asm)
           ↓
  scan_functions.py
           ↓
  smb_functions.txt (59 functions)
           ↓
  generate_tests.py
           ↓
  SMBComprehensiveDifferentialTest.kt (60 test methods)
           ↓
  SMBDifferentialTestGenerator.testFunction()
           ↓
    ┌──────────────────────┬──────────────────────┐
    ↓                      ↓                      ↓
Interpreter          Kotlin Generator      State Comparison
(Execute 6502)       (Generate & Execute)  (Compare all RAM)
    ↓                      ↓                      ↓
Final State A        Final State B         Differences List
    └──────────────────────┴──────────────────────┘
                           ↓
                    Pass/Fail Report
```

## Conclusion

This comprehensive testing framework validates the entire 6502-to-Kotlin decompiler against real game code from Super Mario Bros. It tests **all 59 functions** with **590 total test cases**, comparing **every byte of NES RAM** (12KB) plus all registers and flags.

The framework is **ready to run** and will identify all decompiler bugs that need to be fixed before the tool is production-ready.

---

**Status:** ✅ Infrastructure complete, ready for test execution

**Next:** Run tests and analyze results to identify decompiler bugs
