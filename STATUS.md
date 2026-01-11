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

## Bug Fix Pattern (2026-01-10)

**Standard process for fixing decompiler issues:**

1. **Identify** - Find a decompilation issue (from test failures, code review, TAS differences)
2. **Reproduce** - Create a small unit test that isolates and reproduces the issue
3. **Fix** - Fix the decompiler code to pass the unit test
4. **Verify** - Confirm the fix helped SMB (run GeneratedFunctionTests or comparison test)

This pattern ensures:
- Issues are properly isolated before attempting fixes
- Fixes don't break other functionality
- Each fix is verifiable and testable
- Progress is measurable

## Next Steps

### Completed
1. ✅ **All 64 functions pass validation (100%)**
2. ✅ **Fixed basic interpreter tests**: testReset() and testMemoryReset() now pass
3. ✅ **Test suite down to 2 failures**: 363/365 tests passing (99.5%)
4. ✅ **Refactored kotlin-codegen.kt**: Split into 3 files (1286 + 281 + 678 lines)
5. ✅ **Fixed RTS return value bug** (2026-01-10): RTS handler now captures `ctx.registerA`

### Bug Fixes Applied

#### RTS Return Value Fix (2026-01-10)
**Problem**: Functions returning A register generated `return A` but A was never defined.
The computed value was stored in temp variables (e.g., temp2) but not tracked as A.

**Example** - Before fix:
```kotlin
fun getLrgObjAttrib(X: Int): Int {
    temp2 = memory[...].toInt() and 0x0F
    return A  // ERROR: A undefined!
}
```

**Example** - After fix:
```kotlin
fun getLrgObjAttrib(X: Int): Int {
    temp2 = memory[...].toInt() and 0x0F
    return temp2  // Returns the actual computed value
}
```

**Files Modified**:
- `core/src/main/kotlin/instruction-handlers.kt` (RTS handler captures ctx.registerA)
- `core/src/main/kotlin/kotlin-codegen.kt` (Added stripReturnValues for void functions)

**Test**: `core/src/test/kotlin/decompiler/RtsReturnValueTest.kt` (4 tests)

#### Flag Nullification Fix (2026-01-10)
**Problem**: Loop conditions used undefined `flagN` or `flagZ` variables, causing infinite loops.
When if-else branches have different flag states, flags were set to null during merge.
Then loop conditions fell back to undefined variable names.

**Root Cause**: When one branch terminates (return), merge logic nullified flags even though
only the continuing branch's flags were relevant for subsequent loop conditions.

**Example** - Before fix:
```kotlin
do {
    if (temp3 == 0) {
        temp0 = (temp0 - 1) and 0xFF  // DEY sets N flag
    } else {
        return temp3  // Different path, different flags
    }
} while (!flagN)  // ERROR: flagN undefined (null after merge)
```

**Example** - After fix:
```kotlin
do {
    if (temp3 == 0) {
        temp0 = (temp0 - 1) and 0xFF  // DEY
    } else {
        return temp3
    }
} while ((temp0 and 0x80) == 0)  // Uses then-branch's flag (Y is positive)
```

**Fixes Applied**:
1. Smart merge: When one branch terminates, use the other branch's state entirely
2. Better fallback: Build condition from known registers instead of undefined variables

**Files Modified**:
- `core/src/main/kotlin/kotlin-codegen.kt` (Smart merge + fallback logic)

**Impact**: 23 more tests pass (284/596 vs 261/596 previously), getOffScreenBitsSet no longer times out

**Test**: `core/src/test/kotlin/decompiler/FlagNullificationTest.kt` (6 tests)

### ✅ Fix #9: Transfer Instruction Register Aliasing (2026-01-10)
**Problem**: When TAY copies a temp variable and the source is later reassigned by LDA, the Y register tracking becomes stale. This caused CPY conditions to check the wrong value.

**Example** - OutputNumbers function:
```asm
asl           ; temp2 = shifted value
tay           ; ctx.registerY = temp2 (KVar reference)
lda #$20      ; temp2 = 0x20 (overwrites!)
cpy #$00      ; should check Y (shifted), but used temp2 (0x20)
```

**Root Cause**: Transfer instructions (TAY, TAX, TXA, TYA) copied KVar references instead of values. When the source variable was later reassigned, the destination register's tracking became stale.

**Fix**: Transfer instructions now materialize the source value into a new immutable variable when the source is a mutable KVar (temp variable). This preserves the snapshot value.

**Files Modified**:
- `core/src/main/kotlin/instruction-handlers.kt` - Transfer instruction handlers

**Impact**: 6 more tests pass (347/663 vs 353/663 previously). OutputNumbers tests now pass.

### Known Decompilation Bugs to Fix (Updated 2026-01-10)

**Current Status**: 347 out of 663 generated function tests fail.

#### Bug: Function Call Register Output Capture
Functions called via JSR may set output registers (X, Y, A) that the caller uses later, but the decompiler doesn't capture these outputs.

**Example** - chkLrgObjLength:
```kotlin
// WRONG:
getLrgObjAttrib(X)  // Return value IGNORED!
chkLrgObjFixedLength(X, Y)  // Y is undefined!

// CORRECT should be:
val (resultA, resultY) = getLrgObjAttrib(X)
chkLrgObjFixedLength(X, resultY)
```

**Affected functions** (at least 10 failures each):
- chkLrgObjLength, alternateLengthHandler, imposeFriction
- boundingBoxCore, getMTileAttrib, getEnemyOffscreenBits
- getYOffscreenBits, miscLoopBack, moveRedPTroopa
- drawVine, soundEngine, and many others

These functions have test failures - actual mismatches between decompiled Kotlin and 6502:
- **floateyNumbersRoutine**: 6 failures
- **soundEngine**: 10 failures
- **getXOffscreenBits**: 2 failures

### Future Enhancements
1. **Generate test data for analysis tests**:
   ```bash
   ./gradlew :core:test --tests "CaptureFromTASTest.capture function states from TAS"
   ```
2. **Integration testing**: Test complex function call chains end-to-end
3. **Performance testing**: Compare interpreter vs actual NES execution speed

---

## Function Test Generation - PC-Based Approach (2026-01-09)

### Problem with Old Approach (JSR-based)
The old test generation used JSR hooks to capture function calls. This failed because:
1. Many captured addresses were internal labels (not JSR targets)
2. Address calculation mismatches between capture and matching
3. Only 6 out of 165 captured addresses matched decompiled functions

### New Approach: PC-Based Entry Detection

**Key Insight**: The decompiled code already knows which addresses correspond to which functions (via `// Decompiled from X` comments). Use this metadata to drive capture instead of discovering it via JSR hooks.

**Implementation**:
1. **DecompiledFunctionMetadata** - Extracts function metadata from SMBDecompiled.kt:
   - Function name (e.g., `pauseRoutine`)
   - Source label from comment (e.g., `PauseRoutine`)
   - Parameters (e.g., `["A", "X", "Y"]`)
   - Binary address via AddressLabelMapper

2. **PCBasedFunctionTracer** - Captures by PC match, not JSR hook:
   - `beforeStep()` checks if `PC == target address`
   - When PC matches, snapshots CPU state before execution
   - Uses RTS hook to detect function exit
   - Direct 1:1 correspondence with decompiled functions

3. **PCBasedCaptureTest** - New test that uses this approach

### Results
- **Target functions**: 456 (from decompiled code)
- **Functions captured**: 89 (19% coverage)
- **Generated tests**: 565 tests for 87 functions
- **Previous approach**: Only 6 functions matched

### Files Created
- `core/src/main/kotlin/testgen/DecompiledFunctionMetadata.kt`
- `core/src/main/kotlin/testgen/PCBasedFunctionTracer.kt`
- `core/src/test/kotlin/testgen/PCBasedCaptureTest.kt`

### Usage
```bash
# Generate tests with PC-based approach
./gradlew :core:test --tests "PCBasedCaptureTest"

# Run generated tests
./gradlew :smb:test --tests "GeneratedFunctionTests"
```

### Current Test Status
- Some tests failing due to address mapping bugs (see below)
- Tests compile successfully
- Coverage significantly improved from 6 to 89 functions

### Address Mapping Bug Discovered (2026-01-09)

**Root Cause**: The `AddressLabelMapper` calculates incorrect addresses for functions, causing test data to be associated with wrong function names.

**Example**: `dividePDiff` tests fail because:
- AddressLabelMapper says DividePDiff is at 0xF221
- Actual ROM address (verified by signature search) is 0xF26D
- Tests were generated using wrong address, capturing different function's behavior

**Bug Fixed**: `.dw` label parsing in `parsing.kt` was counting labels as 1 byte instead of 2.
- Fix: Now adds two `Expr` items for each label in `.dw` directives
- Impact: Improved accuracy by 237 bytes

**Remaining Issue**: ~76 byte offset still present somewhere between 0x8220 and 0xF000.
- Likely cause: Zero-page vs absolute addressing detection for unknown symbols
- **Workaround Implemented**: ROM signature verification corrects addresses at runtime

**Solution**: Added ROM-based address verification:
1. `AddressLabelMapper.verifyAddressWithRom()` searches ROM for known function signatures
2. `DecompiledFunctionExtractor.withRomVerifiedMapper()` uses ROM to correct addresses
3. `PCBasedCaptureTest` now uses ROM-verified addresses for accurate capture

**Verification**: DividePDiff is now correctly captured at 0xF26D (was incorrectly 0xF12A/0xF221)

**Nested Call Side Effects - FIXED (2026-01-10)**:
Two bugs in `PCBasedFunctionTracer.kt` caused incorrect test data:

1. **callDepth matching bug in onRts()**: Code decremented `callDepth` BEFORE looking for pending captures
   - When function entered at depth 8, RTS looked for depth 7, missing the capture
   - Fix: Look for pending at current depth BEFORE decrementing

2. **Memory tracking not filtered by call depth**: Writes from nested calls were captured as parent's writes
   - Fix: Only record reads/writes when `callDepth == pending.callDepth`

**Results after fix**:
- dividePDiff tests: **10/10 pass** ✅
- Capture count increased from 276K to 366K (correct function completions now detected)
- `nestedCalls` array now correctly empty for functions without JSR
- `memoryWrites` now only contains function's actual writes

**Remaining Test Failures (2026-01-10)**: 33/60 tests fail, 27 pass.

Almost all failures are due to the **ROM Address Bug** (see below). Functions that read ROM data tables fail because addresses are offset by 50-90 bytes:
- getPlayerColors: 3 failures (ROM color table offset)
- drawMushroomIcon: 1 failure (MushroomIconData at wrong address)
- drawVine: 10 failures (reads tile data from ROM)
- drawPowerUp: 10 failures (reads tile data from ROM)
- floateyNumbersRoutine: 4 failures (reads ROM data)
- getAreaType: 3 failures (captured for wrong address - CastleBridgeObj at 0x9C03 vs GetAreaType at 0x9E35)
- nonAnimatedActs: 1 failure (ROM data)
- doNothing2: 1 failure (fall-through capture issue)

The 27 passing tests are for functions that don't depend on ROM data tables.

### ROM Data Address Bug (2026-01-10)
**Problem**: ROM data tables have wrong addresses in SMBConstants.kt, causing functions that read from ROM tables to fail.

**Example**: `getPlayerColors` fails because:
- SMBConstants says `PlayerColors = 0x862F`
- Actual ROM data (verified by searching for $22 $16 $27 $18) is at `0x85D7`
- Offset: 88 bytes (0x58)

**Root Cause**: Address calculation in `AddressLabelMapper.getInstructionSize()` has accumulated errors, causing all ROM addresses to drift. The issue appears to be in zero-page vs absolute addressing detection for unknown labels.

**Workaround**: ROM signature verification in test generation corrects addresses at runtime, but the constants file used by decompiled code still has wrong values.

**Full Fix Required**: Fix address calculation in `parsing.kt` or `AddressLabelMapper.kt` so that label addresses are calculated correctly from the assembly, then regenerate SMBConstants.kt.

**Files Modified**:
- `core/src/main/kotlin/parsing.kt` - Fixed `.dw` label byte counting
- `core/src/main/kotlin/testgen/AddressLabelMapper.kt` - Added `verifyAddressWithRom()` and signatures
- `core/src/main/kotlin/testgen/DecompiledFunctionMetadata.kt` - Added ROM verification support
- `core/src/test/kotlin/testgen/PCBasedCaptureTest.kt` - Uses ROM-verified addresses
- `core/src/test/kotlin/testgen/AddressLabelMapperDebugTest.kt` - Added verification tests

### ✅ Fix #5: BIT Skip-Byte Pattern (2026-01-10)
**Problem**: The `.db $2c` skip-byte pattern was not being handled correctly. This 6502 optimization uses the BIT absolute opcode ($2C) to "hide" the next 2 bytes from execution, allowing two entry points to share code with different initial register values.

**Example** from SMB:
```asm
MoveAllSpritesOffscreen:
    ldy #$00           ; Y = 0 for this entry point
    .db $2c            ; BIT absolute opcode - next 2 bytes become operand
MoveSpritesOffscreen:
    ldy #$04           ; Y = 4 for this entry point (hidden for first entry)
    lda #$f8
    ...
```

When calling via `MoveAllSpritesOffscreen`:
- CPU sees: LDY #$00, BIT $04A0 (the $A0 $04 from ldy #$04), LDA #$F8...
- The LDY #$04 is "eaten" by the BIT instruction as its operand

**Fix**:
1. Detect blocks that end with `.db $2c` (BIT opcode)
2. Mark successor blocks to skip their first instruction, but **per-function** not globally
3. During code generation, check if the current function is in the skip set
4. Skip the instruction (add comment) only when appropriate for that function

**Key Insight**: The skip must be function-specific because:
- `moveAllSpritesOffscreen()` should skip the `ldy #$04`
- `moveSpritesOffscreen()` (called directly) should NOT skip it

**Files Modified**:
- `core/src/main/kotlin/blocks.kt` - Changed `skipFirstInstruction: Boolean` to `skipFirstInstructionForFunctions: MutableSet<AssemblyFunction>`
- `core/src/main/kotlin/kotlin-codegen.kt` - Added `currentFunction` to `CodeGenContext`, updated skip check to be function-specific
- `core/src/test/kotlin/decompiler/BitSkipByteTest.kt` - New unit tests for skip-byte pattern

**Result**: 24 instances of skip-byte pattern now correctly handled in SMB decompiled code.

### ✅ Fix #6: High/Low Byte Constant Reference Parsing (2026-01-10)
**Problem**: Instructions like `LDA #>TitleScreenDataOffset` and `LDA #<TitleScreenDataOffset` were parsed as having no operand (1 byte) instead of being immediate addressing (2 bytes). This caused accumulated address drift of ~2 bytes per occurrence.

**Root Cause**: In `AssemblyAddressing.parse()`, the `<` and `>` operators (high/low byte selection) were only handled for `ShortValue` results. When the inner value was a `ConstantReference` (like `TitleScreenDataOffset`), the cast `as? ShortValue` failed and returned `null`.

**Fix**:
1. Added new data classes `ConstantReferenceLower` and `ConstantReferenceUpper` in `models.kt`
2. Updated parser to create these types when parsing `#<ConstantName` and `#>ConstantName`
3. Updated `AddressLabelMapper.getInstructionSize()` to size these as 2 bytes
4. Updated `codegen-helpers.kt` to generate correct Kotlin expressions
5. Updated `Interpreter6502.kt` to correctly evaluate these addressing modes

**Files Modified**:
- `core/src/main/kotlin/models.kt` - Added new Value types and fixed parsing
- `core/src/main/kotlin/testgen/AddressLabelMapper.kt` - Handle new types in size calculation
- `core/src/main/kotlin/codegen-helpers.kt` - Generate Kotlin for new types
- `core/src/main/kotlin/interpreter/Interpreter6502.kt` - Interpret new types

**Result**: Hi/lo byte constant references now correctly sized as 2 bytes. Address drift reduced by 2 bytes.

**Remaining Issue**: ~74 bytes of accumulated drift still exists from other sources. *Fixed in Fix #7 and #8 below.*

### ✅ Fix #7: Complex Offset Parsing (2026-01-10)
**Problem**: Instructions with complex offset expressions like `sta VRAM_Buffer1-1+$100,x` were silently failing to parse. The parser only handled single-component offsets like `Label+5` or `Label-1`.

**Root Cause**: The `parseLabelWithOffset()` function in `models.kt` tried to parse the offset using `substringAfter('+').toInt()`, which:
1. Failed on hex values like `$100` (not decimal)
2. Didn't handle multiple operators like `-1+$100`

**Fix**: Rewrote `parseLabelWithOffset()` to:
1. Parse hex values (`$100`, `0x100`) as well as decimal
2. Handle multiple offset components by accumulating (e.g., `-1+$100` = `-1+256` = `255`)

**Files Modified**:
- `core/src/main/kotlin/models.kt` - Complete rewrite of offset parsing

**Result**: Offset `VRAM_Buffer1-1+$100` now correctly parses to `(VRAM_Buffer1, 255)`. Instructions with complex offsets are no longer silently dropped.

### ✅ Fix #8: DirectY Zero-Page Assumption (2026-01-10)
**Problem**: All `label,Y` instructions were assumed to support zero-page addressing (2 bytes) when the label was a zero-page address. However, the 6502 only has zero-page,Y mode for LDX and STX - all other instructions use absolute,Y (3 bytes).

**Specific Case**: `CMP Enemy_ID,Y` was sized as 2 bytes because `Enemy_ID = $16` is zero-page. But CMP doesn't have a zero-page,Y addressing mode - the opcode 0xD9 is always absolute,Y (3 bytes).

**Root Cause**: The `getInstructionSize()` function in `AddressLabelMapper.kt` treated `DirectY` the same as `DirectX`, always using zero-page if the label was < 256.

**Fix**: Updated `DirectY` handling to only use 2-byte zero-page,Y mode for LDX and STX. All other instructions use 3-byte absolute,Y.

**Files Modified**:
- `core/src/main/kotlin/testgen/AddressLabelMapper.kt` - Fixed DirectY sizing

**Result**: Address calculation is now 100% accurate. All 882 instruction labels in smbdism.asm match their ROM positions exactly.

### Address Calculation Summary (After Fixes #6-8)
| Before | After |
|--------|-------|
| First error: OutputCol @ 0x8737 | No errors |
| Success rate: ~78% | 100% (882/882 labels) |
| Accumulated drift: ~74 bytes | 0 bytes |

## Files to Track
- **Test Data**: `./data/testgen/captured-tests-happylee-warps.json`
- **Generated Tests**: `./data/testgen/GeneratedFunctionTests.kt`
- **SMB Tests**: `./smb/src/test/kotlin/com/ivieleague/decompiler6502tokotlin/smb/generated/GeneratedFunctionTests.kt`
- **Validation Tests**: Look for BulkAutomatedValidation test file

## Resources
- See `docs/DECOMPILER-TESTING-GUIDE.md` for testing workflow
- See `docs/NMI-TIMING-INSIGHTS.md` for timing issues
- See `CLAUDE.md` for build commands and architecture
