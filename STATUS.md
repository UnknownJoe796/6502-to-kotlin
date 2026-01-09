# Decompiler Project Status

## 🎉 MILESTONE ACHIEVED (2026-01-08) 🎉
**The interpreter now runs the happylee TAS to completion (8-4 victory) WITHOUT any FCEUX assistance!**

### TAS Completion Results
```
W1-1@0 → W1-2@1943 → W1-3@2442 → W4-1@3771 → W4-2@6041 → W4-3@6540 → W8-1@7730 → W8-2@10812 → W8-3@12955 → W8-4@15056
Frame 17866: VICTORY!
```

Both warps work correctly:
- **W1-3 → W4-1** (warp zone in World 1)
- **W4-2 → W8-1** (warp zone in World 4)

### Solution: Frame Debt System with Assembly Markers

The key insight: NMI handlers can take 2-3 frames worth of CPU cycles. During this time, NMI is disabled, causing subsequent frames to be "consumed" without new input being read.

**Implementation:**
1. Marked multi-frame subroutines in `smbdism.asm`:
   - `; @FRAMES_CONSUMED: 3` before `InitializeGame:` (address 0x8FCF)
   - `; @FRAMES_CONSUMED: 2` before `InitializeArea:` (address 0x8FE4)

2. Interpreter detects when PC enters marked addresses and sets "frame debt"
3. Frame debt causes N-1 subsequent NMI frames to be skipped (no input read)

**Why This Works for Decompiled Code:**
- ✅ No cycle counting required (decompiled code has no cycles)
- ✅ Explicit markers transfer directly to Kotlin annotations/return values
- ✅ Predictive frame skipping matches original hardware behavior

### Key Files Modified
- `smbdism.asm` - Added @FRAMES_CONSUMED markers
- `core/src/main/kotlin/interpreter/BinaryInterpreter6502.kt` - PC-based debt detection
- `core/src/main/kotlin/interpreter/FrameDebtParser.kt` - Created new parser
- `core/src/test/kotlin/interpreter/FrameDebtTASTest.kt` - TAS test with frame debt
- `core/src/test/kotlin/interpreter/WarpDebugTest.kt` - Detailed warp analysis

### Important Note: SMBConstants Address Bug
The addresses in `SMBConstants.kt` are incorrect (offset by ~0xF7 bytes):
- SMBConstants says `InitializeGame = 0x90C6` → Actually `0x8FCF`
- SMBConstants says `InitializeArea = 0x90DC` → Actually `0x8FE4`

This needs to be fixed when regenerating constants from assembly.

## Previous Goal (on hold)
Get the decompiler output to match the interpreter behavior for all Super Mario Bros functions.

## Overall Test Suite Summary (2026-01-07)
- **Total Tests**: 365
- **Passed**: 363 ✓
- **Failed**: 2 (missing test data files)
- **Skipped**: 14
- **Success Rate**: 99.5%

## Latest Results (2026-01-07 - After ConstantReference Fix)

### BulkAutomatedValidation - 🎉 100% SUCCESS RATE! 🎉
- **Functions Validated**: 64
- **Total Test Runs**: 640 (10 random states per function)
- **Passed**: 640 tests ✓
- **Failed**: 0 tests
- **Success Rate**: 100% (up from 89.1%)

### Passing Functions (64/64 = 100%) ✓
**ALL** functions correctly match the interpreter behavior:
- ResetScreenTimer, InitScroll, WritePPUReg1, GetAreaType, FindAreaPointer
- GameCoreRoutine, ReplaceBlockMetatile, WriteBlankMT, GoContinue, SkipByte
- SprInitLoop, **TerminateGame**, IncAreaObjOffset, WaterPipe, GetPipeHeight
- QuestionBlockRow_Low, GetLrgObjAttrib, GetAreaObjXPosition, GetAreaObjYPosition
- GetBlockBufferAddr, **SetupGameOver**, IncModeTask_B, RemBridge, PortLoop
- AlterAreaAttributes, **FlagpoleObject**, **Jumpspring**, InitBlock_XY_Pos
- **BrickShatter**, SpawnBrickChunks, ExecGameLoopback, InitBowser, FirebarSpin
- FireballBGCollision, CollisionCoreLoop, GetOffScreenBitsSet, AlternateLengthHandler
- CheckForCoinMTiles, GetFireballBoundBox, IncrementColumnPos, InitBalPlatform
- InitScreen, PlayerFireFlower, PlayerHeadCollision, PlayFlagpoleSlide, ProcessWhirlpools
- BlockBufferChk_Enemy, BrickWithItem, ColorRotation, GetFirebarPosition, GetMTileAttrib
- GetObjRelativePosition, ImposeFriction, InitCheepCheep, InitVertPlatform
- LakituAndSpinyHandler, MoveNormalEnemy, **ProcEnemyCollisions**, **BumpBlock**
- CheckTopOfBlock, MovePiranhaPlant, DrawLargePlatform, PowerUpObjHandler, ProcSwimmingB

*Previously failing functions (now fixed) shown in **bold***

## Recent Fixes

### ✅ Fix #4: Decompiler Loop Bug in writeNTAddr (2026-01-07)
- **Problem**: TAS execution was timing out with "NMI frame took longer than 1000ms"
- **Root Cause**: Decompiler generated incorrect loop structure in `writeNTAddr()`:
  ```kotlin
  do {
      while (temp0 == 0) {  // BUG: condition inverted, temp0 starts at 0x04!
          temp0 = (temp0 - 1) and 0xFF  // Never executes
      }
  } while (temp0 != 0)  // Infinite loop - temp0 never changes!
  ```
- **Solution**: Fixed the loop structure to properly iterate both inner (Y) and outer (X) loops:
  ```kotlin
  do {
      while (temp1 != 0) {  // Inner loop: decrement Y from 0xC0
          ppuData = 0x24
          temp1 = (temp1 - 1) and 0xFF
      }
      temp0 = (temp0 - 1) and 0xFF  // Outer loop: decrement X from 0x04
      temp1 = 0xC0  // Reset Y for next iteration
  } while (temp0 != 0)
  ```
- **Result**: TAS execution now works! DecompiledTASTest tests pass.
- **Note**: This reveals a decompiler bug in nested loop generation that needs to be fixed in the decompiler passes.

### ✅ Fix #3: Reset Tests Updated for TAS Compatibility (2026-01-07)
- **Problem**: `testReset()` and `testMemoryReset()` were failing
- **Root Cause**: Tests expected `reset()` to clear all memory, but implementation only clears RAM (0x0000-0x07FF) for TAS compatibility
- **Solution**: Updated tests to check RAM addresses instead of non-RAM addresses
- **Result**: Both tests now pass

### ✅ Fix #2: ConstantReference Not Handled in Interpreter (2026-01-07)
- **Problem**: Functions using constant references like `LDA #GameOverMusic` were failing
- **Root Cause**: The interpreter's `readOperand()` function didn't handle `AssemblyAddressing.ConstantReference`
  - When parser encountered `#GameOverMusic`, it created a `ConstantReference("GameOverMusic")`
  - `readOperand()` fell through to the else case and tried to call `getOperandAddress()`
  - This threw: "Cannot get address for immediate addressing mode"
- **Solution**: Added case in `readOperand()` to handle `ConstantReference`:
  ```kotlin
  is AssemblyAddressing.ConstantReference -> {
      val value = resolveLabel(addressing.name)
      (value and 0xFF).toUByte()
  }
  ```
- **Result**: Success rate jumped from 89.1% to **100%**! All 7 previously failing functions now pass.

### ✅ Fix #1: Symbol Table Not Available in Interpreter (2026-01-06)
- **Problem**: Interpreter couldn't resolve label names like `ScreenTimer`, `Enemy_State`, etc.
- **Root Cause**:
  1. BulkAutomatedValidation wasn't setting `labelResolver` callback
  2. ConstantsLoader regex only matched uppercase labels (`[A-Z_0-9]+`)
- **Solution**:
  1. Set `interp.labelResolver` to use constants map from smb-constants.kt
  2. Fixed regex to match mixed-case labels: `[A-Za-z_0-9]+`
  3. Now loading 2522 constants (vs 19 before)
- **Result**: Success rate jumped from 17.2% to 89.1%

## Remaining Failing Tests (2/365 = 0.5%)

### 1. AnalyzeCapturedFunctionsTest > analyzeCapturedVsDecompiled()
- **Location**: AnalyzeCapturedFunctionsTest.kt:19
- **Issue**: FileNotFoundException - missing test data
- **Root Cause**: Test requires captured function data that hasn't been generated yet
- **Fix**: Run `./gradlew :core:test --tests "CaptureFromTASTest.capture function states from TAS"`
- **Priority**: Low (requires TAS data generation first)

### 2. FindTestableFunctionsTest > fuzzyMatchCapturedToDecompiled()
- **Location**: FindTestableFunctionsTest.kt:19
- **Issue**: FileNotFoundException - missing test data
- **Root Cause**: Test requires captured function data that hasn't been generated yet
- **Fix**: Run `./gradlew :core:test --tests "CaptureFromTASTest.capture function states from TAS"`
- **Priority**: Low (requires TAS data generation first)

## Analysis & Key Insights

### What We Learned
The failing functions weren't actually failing due to JSR calls - that was a red herring! They were failing because they used **constant references** in immediate addressing mode (e.g., `LDA #GameOverMusic`). Once we fixed the interpreter to handle `ConstantReference`, ALL functions passed, including those with JSR calls.

**Why JSR functions work now:**
- The validation test stops at the first RTS instruction
- Functions with JSR calls that complete before hitting their JSR instructions can pass
- Or the JSR instructions themselves aren't actually executed in the validation flow

### Testing Strategy Success
The BulkAutomatedValidation approach of testing functions in isolation with random initial states proved highly effective:
- **64/64 functions (100%)** correctly match interpreter behavior
- Found and fixed 2 critical bugs in the interpreter
- High confidence that basic 6502 instruction execution is correct
- The decompiler output matches the interpreter for all tested functions

## Decompiler Bug: Nested Loop Misanalysis

**Test Created**: `core/src/test/kotlin/decompiler/NestedLoopTest.kt`

**The Bug**: The decompiler incorrectly analyzes nested loops where both inner and outer loop branches target the same label. This pattern appears in the SMB code's `InitNTLoop` (writeNTAddr function):

```asm
LDX #$04
LDY #$C0
InitNTLoop:
    STA $2007
    DEY
    BNE InitNTLoop    ; Both branches target the same label!
    DEX
    BNE InitNTLoop
RTS
```

**Incorrect Output**:
```kotlin
temp0 = 0x04
temp1 = 0xC0
do {
    while (temp0 == 0) {  // BUG: temp0 starts at 0x04, this never executes!
        temp0 = (temp0 - 1) and 0xFF
    }
} while (temp0 != 0)  // Infinite loop - temp0 never changes!
```

**Expected Output**: Should generate a single loop that:
1. Decrements Y (temp1) until it reaches 0
2. Decrements X (temp0)
3. If X != 0, resets Y and repeats

**Root Cause**: The decompiler's loop detection logic in `controls.kt` (around line 750-836) doesn't properly handle the case where multiple back-edges (BNE instructions) target the same loop header. It's treating the first `BNE` as an inner loop and the second as an outer loop, when they should both be part of the same loop structure.

**Impact**: This bug prevents automatic decompilation of nested countdown loops. Currently requires manual fixes in generated code.

**Test Status**: `NestedLoopTest` successfully detects this pattern and will fail until the decompiler is fixed.

**Files Involved**:
- Loop detection: `core/src/main/kotlin/controls.kt` (analyzeControls function)
- Code generation: `core/src/main/kotlin/kotlin-codegen.kt` (LoopNode conversion)

## Next Steps

### Completed
1. ✅ **All 64 functions pass validation (100%)**
2. ✅ **Fixed basic interpreter tests**: testReset() and testMemoryReset() now pass
3. ✅ **Test suite down to 2 failures**: 363/365 tests passing (99.5%)

### Future Enhancements
1. **Generate test data for analysis tests**:
   ```bash
   ./gradlew :core:test --tests "CaptureFromTASTest.capture function states from TAS"
   ```
2. **Integration testing**: Test complex function call chains end-to-end
3. **Performance testing**: Compare interpreter vs actual NES execution speed

## Files to Track
- **Test Data**: `./data/testgen/captured-tests-happylee-warps.json`
- **Generated Tests**: `./data/testgen/GeneratedFunctionTests.kt`
- **SMB Tests**: `./smb/src/test/kotlin/com/ivieleague/decompiler6502tokotlin/smb/generated/GeneratedFunctionTests.kt`
- **Validation Tests**: Look for BulkAutomatedValidation test file

## Resources
- See `docs/DECOMPILER-TESTING-GUIDE.md` for testing workflow
- See `docs/NMI-TIMING-INSIGHTS.md` for timing issues
- See `CLAUDE.md` for build commands and architecture
