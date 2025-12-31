# Scaling Roadmap: Path to Full Frame Decompilation

## Current Status (as of Test 255)

### ✅ What Works

**Expression-Based Decompilation:**
- Successfully decompiles straight-line 6502 code to clean Kotlin expressions
- Handles up to 10-instruction sequences with multiple outputs
- Recognizes patterns: consecutive shifts (`shl 4`), bitwise ops, arithmetic
- Code reduction: 60-83% vs statement-based approach
- **255 tests passing** with 100% validation success

**Real-World Validation:**
- Tested on actual Super Mario Bros code sequences
- 5 SMB test scenarios covering:
  - Metatile calculations (multiply by 4)
  - Status bar offsets (add 32, multiply by 2)
  - Sprite positioning (horizontal spacing)
  - Coin position (10-instruction, 2-output sequence)
  - Whirlpool sizing (multiply by 16)

### ❌ Current Limitations

**1. No Control Flow Support**
- Cannot handle branches (BNE, BEQ, BCC, etc.)
- Cannot generate `if`/`else` statements
- Cannot generate `while`/`for` loops
- **Impact**: ~95% of SMB subroutines contain branches

**2. No Carry Flag Propagation Between Calculations**
- Tracks carry within a single expression (`A + B + carry`)
- **Cannot** propagate carry output from one store to next load
- **Example failure**: `GetScreenPosition` subroutine
  ```assembly
  adc #$ff     ; rightX = leftX + 255, generates carry
  sta $0725    ; store rightX (carry lost here!)
  lda $071B    ; load leftPage
  adc #$00     ; should add carry from previous, but carry is lost
  ```

**3. Straight-Line Code Only**
- Works perfectly for sequential operations
- Cannot handle backward jumps (loops)
- Cannot handle forward jumps (conditionals)
- No JSR/RTS support (function calls)

## Path to Complete Subroutines

### Phase 1: Carry Flag Propagation (CRITICAL)

**Problem**: Multi-byte arithmetic requires carry propagation between stores.

**Solution Options**:

A. **Emit Carry Variables** (Simple but verbose)
```kotlin
val carry1 = (leftX + 255u > 255u)
memory[0x0725] = ((leftX + 255u) and 0xFFu).toByte()
memory[0x0726] = ((leftPage + (if (carry1) 1u else 0u)) and 0xFFu).toByte()
```

B. **Defer Store Until Expression Complete** (Cleaner)
```kotlin
val result16 = leftPage.toUInt() * 256u + leftX.toUInt() + 255u
memory[0x0725] = (result16 and 0xFFu).toByte()
memory[0x0726] = ((result16 shr 8) and 0xFFu).toByte()
```

C. **Multi-Byte Value Recognition** (Best but complex)
```kotlin
val rightPos = leftPos + 255u  // Recognizes 16-bit value split across two bytes
memory[0x0725] = (rightPos and 0xFFu).toByte()
memory[0x0726] = ((rightPos shr 8) and 0xFFu).toByte()
```

**Implementation Steps**:
1. Track carry output from arithmetic operations
2. Create CarryValue type in Value hierarchy
3. Detect multi-byte arithmetic patterns
4. Generate appropriate Kotlin based on pattern

**Estimated Complexity**: Medium (1-2 weeks)

### Phase 2: Basic Control Flow (ESSENTIAL)

**Problem**: 95% of SMB code has branches.

**Solution**: Implement CFG-based decompilation.

**2A: Simple If/Else** (First milestone)
```assembly
ldx #$05
cpx #$10
bcs Skip      ; if X >= 16, skip
lda #$FF
sta $20
Skip: rts
```

Should generate:
```kotlin
if (X < 16u) {
    memory[0x20] = 0xFFu.toByte()
}
```

**Requirements**:
- Build Control Flow Graph (CFG)
- Detect branch conditions
- Generate if/else from forward branches
- Track flag state at branch points

**2B: Simple Loops** (Second milestone)
```assembly
ldx #$00
Loop: lda Data,x
sta $20,x
inx
cpx #$10
bne Loop
```

Should generate:
```kotlin
for (i in 0u until 16u) {
    memory[0x20 + i] = memory[Data + i]
}
```

**Requirements**:
- Detect backward branches (loops)
- Recognize loop patterns (counter-based, zero-test)
- Generate while/for as appropriate
- Track loop induction variables

**Estimated Complexity**: High (3-4 weeks)

### Phase 3: Function Calls and Returns

**Problem**: SMB uses JSR/RTS extensively for code reuse.

**Solution**: Function extraction and calling convention.

```assembly
GetScreenPosition:
    lda ScreenLeft_X_Pos
    clc
    adc #$ff
    sta ScreenRight_X_Pos
    lda ScreenLeft_PageLoc
    adc #$00
    sta ScreenRight_PageLoc
    rts

Caller:
    jsr GetScreenPosition
```

Should generate:
```kotlin
fun getScreenPosition() {
    val leftX = memory[0x071A].toUByte()
    val leftPage = memory[0x071B].toUByte()
    val rightPos = (leftPage.toUInt() * 256u + leftX.toUInt() + 255u)
    memory[0x0725] = (rightPos and 0xFFu).toByte()
    memory[0x0726] = ((rightPos shr 8) and 0xFFu).toByte()
}

// In caller:
getScreenPosition()
```

**Requirements**:
- Detect subroutine boundaries (label to RTS)
- Extract parameters (memory reads before writes)
- Extract return values (memory writes)
- Generate function calls for JSR

**Estimated Complexity**: Medium-High (2-3 weeks)

## Path to Full Frame Simulation

### Phase 4: Multi-Subroutine Integration

**Goal**: Decompile and link multiple related subroutines.

**Example**: Player movement subroutines
- ReadJoypad
- UpdatePlayerPosition
- CheckCollision
- UpdateSprite

**Requirements**:
- Shared memory layout understanding
- Call graph construction
- Parameter passing analysis

**Estimated Complexity**: Medium (2 weeks)

### Phase 5: Game State Management

**Goal**: Recognize and group related memory locations into structs.

**Example**:
```assembly
Player_X_Pos      = $0086
Player_Y_Pos      = $00CE
Player_State      = $001D
Player_Anim_Frame = $0100
```

Should generate:
```kotlin
data class Player(
    var xPos: UByte,
    var yPos: UByte,
    var state: UByte,
    var animFrame: UByte
)
```

**Requirements**:
- Memory access pattern analysis
- Struct/array detection
- Variable naming heuristics

**Estimated Complexity**: High (3-4 weeks)

### Phase 6: Frame Logic Simulation

**Goal**: Run entire game frame and validate output.

**Test Strategy**:
1. Start with NMI (VBlank) handler - runs every frame
2. Decompile all called subroutines
3. Initialize game state
4. Run frame, compare:
   - PPU writes (graphics updates)
   - Memory state changes
   - Sound register writes

**Success Criteria**:
- Decompiled frame execution matches interpreter exactly
- All sprite positions correct
- All background tiles correct
- Player input processed identically

**Estimated Complexity**: Very High (6-8 weeks)

## Immediate Next Steps

### Week 1-2: Carry Propagation
1. Implement carry output tracking
2. Add CarryValue type
3. Test on GetScreenPosition
4. Test on multi-byte addition patterns

### Week 3-5: Basic Control Flow
1. Implement CFG construction
2. Add simple if/else generation
3. Test on conditional sequences
4. Add simple loop detection
5. Test on counter loops

### Week 6-7: First Complete Subroutine
1. Choose target subroutine with:
   - Simple control flow (1-2 branches)
   - No function calls
   - Limited carry usage
2. Implement missing features as needed
3. Validate end-to-end

### Month 2-3: Scale to Multiple Subroutines
1. Function call support
2. Parameter extraction
3. Multi-subroutine tests
4. Integration testing

### Month 3-4: Frame Simulation
1. Struct generation
2. Variable naming
3. Full NMI handler
4. Frame-by-frame validation

## Metrics and Milestones

### Current Metrics
- ✅ 255 tests passing
- ✅ 10-instruction sequences
- ✅ 2-output calculations
- ✅ 60-83% code reduction
- ❌ 0 complete subroutines
- ❌ 0 branches handled
- ❌ 0 frames simulated

### Target Metrics (3 Months)
- 🎯 500+ tests passing
- 🎯 Complete subroutines with branches
- 🎯 Multi-byte arithmetic correct
- 🎯 10+ subroutines linked
- 🎯 1 complete frame validated

### Ultimate Goal (6 Months)
- 🎯 Full NMI handler decompiled
- 🎯 Player movement logic working
- 🎯 Collision detection validated
- 🎯 Graphics updates matching
- 🎯 Complete frame simulation passing

## Key Insights from Scaling

### What We Learned

1. **Expression-based approach scales well**
   - 10-instruction sequences work great
   - Code stays readable
   - Validation is reliable

2. **Carry propagation is CRITICAL**
   - Multi-byte math everywhere in 6502
   - Current limitation blocks most subroutines
   - Must solve before scaling further

3. **Control flow is unavoidable**
   - 95% of code has branches
   - Can't test complete subroutines without it
   - Need CFG-based approach

4. **Real-world testing drives progress**
   - SMB code revealed carry issue
   - Actual patterns guide design
   - Validation against interpreter is invaluable

### Design Decisions

1. **Keep expression-based core**
   - Works well for straight-line code
   - Extend rather than replace

2. **Add control flow layer**
   - CFG construction
   - Block-based expression generation
   - Control flow synthesis on top

3. **Defer variable naming**
   - Get correctness first
   - Names can be added later
   - Focus on executable output

## Conclusion

The ExpressionDecompiler successfully proved the concept for straight-line code. To reach our goal of full frame simulation, we must now tackle:

1. **Immediate**: Carry propagation (weeks)
2. **Short-term**: Control flow (months)
3. **Long-term**: Full frame simulation (6 months)

Each phase builds on the previous, maintaining our 100% validation success rate while expanding capabilities.

**Next commit should implement**: Carry flag propagation system.
