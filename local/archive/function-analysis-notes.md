# Function Analysis Notes

## InitializeMemory

### Assembly Code
```asm
InitializeMemory:
              ldx #$07          ; set initial high byte to $0700-$07ff
              lda #$00          ; set initial low byte to start of page (at $00 of page)
              sta $06
InitPageLoop: stx $07
InitByteLoop: cpx #$01          ; check to see if we're on the stack ($0100-$01ff)
              bne InitByte      ; if not, go ahead anyway
              cpy #$60          ; otherwise, check to see if we're at $0160-$01ff
              bcs SkipByte      ; if so, skip write
InitByte:     sta ($06),y       ; otherwise, initialize byte with current low byte in Y
SkipByte:     dey
              cpy #$ff          ; do this until all bytes in page have been erased
              bne InitByteLoop
              dex               ; go onto the next page
              bpl InitPageLoop  ; do this until all pages of memory have been erased
              rts
```

### Expected Blocks

1. **InitializeMemory** (entry)
   - Instructions: LDX, LDA, STA (3 instructions)
   - FallThrough: InitPageLoop
   - Branch: null

2. **InitPageLoop**
   - Instructions: STX (1 instruction)
   - FallThrough: InitByteLoop
   - Branch: null

3. **InitByteLoop**
   - Instructions: CPX, BNE (2 instructions)
   - FallThrough: (next block - stack check)
   - Branch: InitByte

4. **Stack check block** (unlabeled)
   - Instructions: CPY, BCS (2 instructions)
   - FallThrough: InitByte
   - Branch: SkipByte

5. **InitByte**
   - Instructions: STA (1 instruction)
   - FallThrough: SkipByte
   - Branch: null

6. **SkipByte**
   - Instructions: DEY, CPY, BNE (3 instructions)
   - FallThrough: (next block - DEX)
   - Branch: InitByteLoop (back edge!)

7. **Decrement page block** (unlabeled)
   - Instructions: DEX, BPL (2 instructions)
   - FallThrough: (RTS block)
   - Branch: InitPageLoop (back edge!)

8. **Return block** (unlabeled)
   - Instructions: RTS (1 instruction)
   - FallThrough: null
   - Branch: null

### Control Flow Structure

**Nested loops:**
```
setup (InitializeMemory)
outer loop (InitPageLoop):
    setup page
    inner loop (InitByteLoop):
        if on stack page:
            if in protected range:
                skip
            else:
                write
        else:
            write
        decrement Y
        if Y != 0xFF: continue inner loop
    decrement X
    if X >= 0: continue outer loop
return
```

### Expected Dominators

- InitializeMemory dominates: all blocks
- InitPageLoop dominates: InitByteLoop and all blocks in inner loop
- InitByteLoop dominates: stack check, InitByte, SkipByte

### Expected I/O

- **Inputs**:
  - Y register (used as byte offset within pages)
- **Outputs**:
  - None (function has no return value that callers use)
- **Clobbers**:
  - Registers: A, X, Y
  - Flags: Zero, Negative, Carry
  - Virtual registers: $06, $07
  - Memory: Pages $0000-$07FF (except $0160-$01FF on stack)

---

## GameTimerFireworks

### Assembly Code
```asm
GameTimerFireworks:
        ldy #$05               ; set default state for star flag object
        lda GameTimerDisplay+2 ; get game timer's last digit
        cmp #$01
        beq SetFWC             ; if last digit of game timer set to 1, skip ahead
        ldy #$03               ; otherwise load new value for state
        cmp #$03
        beq SetFWC             ; if last digit of game timer set to 3, skip ahead
        ldy #$00               ; otherwise load one more potential value for state
        cmp #$06
        beq SetFWC             ; if last digit of game timer set to 6, skip ahead
        lda #$ff               ; otherwise set value for no fireworks
SetFWC: sta FireworksCounter   ; set fireworks counter here
        sty Enemy_State,x      ; set whatever state we have in star flag object

IncrementSFTask1:
      inc StarFlagTaskControl  ; increment star flag object task number

StarFlagExit:
      rts                      ; leave
```

### Expected Blocks

1. **GameTimerFireworks** (entry)
   - Instructions: LDY, LDA, CMP, BEQ (4 instructions)
   - FallThrough: (next block)
   - Branch: SetFWC

2. **Check digit 3** (unlabeled)
   - Instructions: LDY, CMP, BEQ (3 instructions)
   - FallThrough: (next block)
   - Branch: SetFWC

3. **Check digit 6** (unlabeled)
   - Instructions: LDY, CMP, BEQ (3 instructions)
   - FallThrough: (next block - load FF)
   - Branch: SetFWC

4. **Load FF** (unlabeled)
   - Instructions: LDA (1 instruction)
   - FallThrough: SetFWC
   - Branch: null

5. **SetFWC**
   - Instructions: STA, STY (2 instructions)
   - FallThrough: IncrementSFTask1
   - Branch: null

6. **IncrementSFTask1**
   - Instructions: INC (1 instruction)
   - FallThrough: StarFlagExit
   - Branch: null

7. **StarFlagExit**
   - Instructions: RTS (1 instruction)
   - FallThrough: null
   - Branch: null

### Control Flow Structure

**Sequential if-else-if chain:**
```
if (timer last digit == 1) {
    Y = 5
    goto SetFWC
} else if (timer last digit == 3) {
    Y = 3
    goto SetFWC
} else if (timer last digit == 6) {
    Y = 0
    goto SetFWC
} else {
    A = 0xFF
}
SetFWC:
FireworksCounter = A
Enemy_State[X] = Y
increment task
return
```

### Expected Dominators

- GameTimerFireworks dominates: all blocks
- SetFWC dominates: IncrementSFTask1, StarFlagExit (merge point from all branches)

### Expected I/O

- **Inputs**:
  - X register (used as index into Enemy_State)
  - GameTimerDisplay+2 (memory, last digit of game timer)
- **Outputs**:
  - FireworksCounter (memory)
  - Enemy_State[X] (memory)
  - StarFlagTaskControl (memory)
- **Clobbers**:
  - Registers: A, Y
  - Flags: Zero, Negative, Carry

---

## ChkPauseTimer

### Assembly Code
```asm
ChkPauseTimer: lda GamePauseTimer     ; check if pause timer is still counting down
               beq ChkStart
               dec GamePauseTimer     ; if so, decrement and leave
               rts
ChkStart:      lda SavedJoypad1Bits   ; check to see if start is pressed
               and #Start_Button      ; on controller 1
               beq ClrPauseTimer
               lda GamePauseStatus    ; check to see if timer flag is set
               and #%10000000         ; and if so, do not reset timer
               bne ExitPause
               lda #$2b               ; set pause timer
               sta GamePauseTimer
               lda GamePauseStatus
               tay
               iny                    ; set pause sfx queue for next pause mode
               sty PauseSoundQueue
               eor #%00000001         ; invert d0 and set d7
               ora #%10000000
               bne SetPause           ; unconditional branch
ClrPauseTimer: lda GamePauseStatus    ; clear timer flag if timer is at zero and start button
               and #%01111111         ; is not pressed
SetPause:      sta GamePauseStatus
ExitPause:     rts
```

### Expected Blocks

1. **ChkPauseTimer** (entry)
   - Instructions: LDA, BEQ
   - FallThrough: DecAndReturn
   - Branch: ChkStart

2. **DecAndReturn** (unlabeled in source)
   - Instructions: DEC, RTS
   - FallThrough: null
   - Branch: null

3. **ChkStart**
   - Instructions: LDA, AND, BEQ
   - FallThrough: CheckStatus
   - Branch: ClrPauseTimer

4. **CheckStatus** (unlabeled in source)
   - Instructions: LDA, AND, BNE
   - FallThrough: SetTimer
   - Branch: ExitPause

5. **SetTimer** (unlabeled in source)
   - Instructions: LDA, STA, LDA, TAY, INY, STY, EOR, ORA, BNE
   - FallThrough: (unreachable due to BNE)
   - Branch: SetPause

6. **ClrPauseTimer**
   - Instructions: LDA, AND
   - FallThrough: SetPause
   - Branch: null

7. **SetPause**
   - Instructions: STA
   - FallThrough: ExitPause
   - Branch: null

8. **ExitPause**
   - Instructions: RTS
   - FallThrough: null
   - Branch: null

### Expected Dominators

- ChkPauseTimer dominates: all blocks
- DecAndReturn dominated by: ChkPauseTimer
- ChkStart dominated by: ChkPauseTimer
- CheckStatus dominated by: ChkStart
- SetTimer dominated by: CheckStatus
- ClrPauseTimer dominated by: ChkStart
- SetPause dominated by: ChkStart (merge point from CheckStatus path and ClrPauseTimer)
- ExitPause dominated by: ChkPauseTimer (reached from multiple paths)

### Expected Control Flow

```
if (GamePauseTimer == 0) {
    goto ChkStart
} else {
    GamePauseTimer--
    return
}

ChkStart:
if (Start_Button pressed) {
    if (GamePauseStatus & 0x80 != 0) {
        return
    } else {
        // Set timer logic
        goto SetPause
    }
} else {
    // Clear flag
    goto SetPause
}

SetPause:
GamePauseStatus = ...
return
```

### Expected I/O

- **Inputs**:
  - GamePauseTimer (memory)
  - SavedJoypad1Bits (memory)
  - GamePauseStatus (memory)
- **Outputs**:
  - GamePauseTimer (memory, may be decremented or reset)
  - GamePauseStatus (memory, pause flag modified)
  - PauseSoundQueue (memory, set when pause toggled)
- **Clobbers**:
  - Registers: A, Y
  - Flags: Zero, Negative, Carry

