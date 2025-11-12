# SMB Function Analysis for Test Suite

## Selected Test Functions

### Category: Simple Linear / Simple Loops

#### 1. **InitializeMemory**
- Pattern: Simple counted loop
- Complexity: Low
- Why: Basic loop structure, good baseline

#### 2. **ChkPauseTimer**
- Pattern: Simple if-then-else with early return
- Complexity: Low
- Why: Basic conditional, tests early return

#### 3. **DecTimers**
- Pattern: Simple loop over array
- Complexity: Low
- Why: Array iteration pattern

### Category: Intermediate Conditionals

#### 4. **GetEnemyOffscreenBits**
- Pattern: Multiple sequential conditionals
- Complexity: Medium
- Why: Multiple branches, bit manipulation

#### 5. **GetAreaObjXPosition**
- Pattern: Function with return value
- Complexity: Medium
- Why: Return value analysis, uses X register as input

#### 6. **PlayerEnemyDiff**
- Pattern: Arithmetic with conditionals
- Complexity: Medium
- Why: Tests comparison and flag tracking

### Category: Loops with Exit Conditions

#### 7. **JumpEngine**
- Pattern: Jump table dispatch
- Complexity: Medium
- Why: Indirect jump pattern

#### 8. **RelativeEnemyPosition**
- Pattern: Calculation loop
- Complexity: Medium
- Why: Loop with calculation

### Category: Complex Control Flow

#### 9. **FloateyNumbersRoutine** ⭐
- Pattern: Early return to shared epilogue
- Complexity: High
- Why: Known problematic function, backward branch to earlier label

#### 10. **MusicHandler**
- Pattern: Nested if statements
- Complexity: High
- Why: Deep nesting, multiple decision points

#### 11. **PlayerCtrlRoutine**
- Pattern: Large state machine
- Complexity: High
- Why: Many branches, complex flow

#### 12. **ImpedePlayerMove**
- Pattern: Complex branching with multiple paths
- Complexity: High
- Why: Has been problematic, needs correct structuring

### Category: Edge Cases

#### 13. **EndExitOne**
- Pattern: Shared epilogue
- Complexity: Special
- Why: Reached from multiple functions, tests function boundary detection

#### 14. **NoiseSfxHandler**
- Pattern: Sound effect dispatcher
- Complexity: Medium
- Why: Jump table or switch-like structure

#### 15. **GameTimerFireworks**
- Pattern: Do-while countdown
- Complexity: Medium
- Why: Post-test loop pattern

## Next Steps
1. Extract each function from smbdism.asm
2. Manually trace through assembly to document:
   - Expected blocks
   - Expected dominator relationships
   - Expected control flow structure
   - Expected I/O (inputs/outputs)
