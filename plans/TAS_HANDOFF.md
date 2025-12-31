# TAS Validation Handoff - Interpreter vs FCEUX

## Current Status
**TAS does NOT complete successfully.** Mario dies in World 1-1 around frame 4268, while FCEUX is at World 4-1 by that point. Massive divergence occurs despite NMI timing fixes.

## Root Cause Identified: Extra NMIs

### The Problem
The interpreter fires **9 extra NMIs** compared to FCEUX over 6000 frames. Each NMI = one game logic update, causing timing drift.

### NMI Discrepancy Frames
These frames have NMI=0 in FCEUX but NMI=1 in our interpreter (before fix):

| FCEUX Frame | Interpreter Frame | Byte Offset | Cause |
|-------------|-------------------|-------------|-------|
| 4 | 3 | 6144 | Boot timing |
| 6 | 5 | 10240 | Boot timing |
| 7 | 6 | 12288 | Boot timing |
| 43 | 42 | 86016 | Game start transition |
| 613 | 612 | 1253376 | Level 1-2 entry |
| 927 | 926 | 1896448 | Unknown transition |
| 1945 | 1944 | 3981312 | Level transition |
| 2444 | 2443 | 5003264 | Level 1-3 entry |
| 3815 | 3814 | 7811072 | Level 4-1 entry |

### Fix Applied (Workaround)
In `FullTASValidationTest.kt`, NMI is suppressed on specific frames:
```kotlin
val suppressNmiFrames = setOf(3, 5, 6, 42, 612, 926, 1944, 2443, 3814)
```

This makes NMI=0 counts match (12 each), but **TAS still diverges**.

## Why NMI Timing Differs

### Technical Details
1. **VBlank starts at cycle 27395** (scanline 241, dot 1)
2. Game writes `$2000 = 0x90` (NMI enable) at cycle ~18346 in frame 3
3. Game writes `$2000 = 0x10` (NMI disable) at cycle ~29780 (post-VBlank)
4. Our interpreter sees NMI enabled at VBlank start → fires NMI
5. FCEUX doesn't fire NMI for same frame

### Likely FCEUX Difference
- May use "dot 17 hack" (check NMI at PPU dot 17 instead of dot 1)
- May sample NMI enable at different CPU cycle
- Frame boundaries may be VBlank-to-VBlank vs our fixed 29780 cycles

## Input System

### Current Approach
- Using `nmi-filtered-inputs.txt` which maps FCEUX NMI index → button state
- Converts FCEUX button encoding to NES shift register order
- Uses state-based offset sync at level transitions

### Input Offset Tracking
```kotlin
var syncOffset = 0  // Updated at level transitions
inputIndex = maxOf(0, frame - syncOffset)
```

## Key Files

| File | Purpose |
|------|---------|
| `core/src/test/kotlin/interpreter/FullTASValidationTest.kt` | Main TAS test |
| `local/tas/fceux-full-ram.bin` | FCEUX RAM dump (2KB per frame) |
| `local/tas/interpreter-full-ram.bin` | Interpreter RAM dump |
| `local/tas/fceux-frame-index.txt` | FCEUX frame→NMI mapping |
| `local/tas/interpreter-frame-index.txt` | Interpreter frame→NMI mapping |
| `local/tas/nmi-filtered-inputs.txt` | TAS inputs indexed by NMI count |
| `local/tas/ppu-2000-writes.txt` | Debug log of $2000 writes with cycle timing |

## Remaining Issues

### 1. Input Timing Still Wrong
Even with NMI counts matching, inputs aren't being applied at the correct game states. The state-based sync offset approach doesn't fully solve the drift.

### 2. Possible Additional Causes
- PPU sprite 0 hit timing may differ
- Controller strobe timing
- Instruction cycle counts may be slightly off
- Memory initialization differences

## Relevance to Decompiled Version

The NMI suppression "hack" will likely be needed for decompiled code too, because:
1. Decompiled version has no cycle-accurate timing
2. Functions execute "instantly" vs real NES timing
3. Same game states (level loads, boot) will need NMI suppression

**Recommended approach for decompiled version:**
```kotlin
fun shouldFireNmi(): Boolean {
    // Don't fire during level loading or boot states
    if (operMode_Task in setOf(LOADING_LEVEL, BOOT_INIT)) return false
    return nmiEnabled
}
```

This is game-state-based rather than frame-number-based, which is more robust.

## Next Steps to Complete TAS

1. **Compare RAM byte-by-byte** at early frames (100-200) to find first divergence point
2. **Check instruction cycle accuracy** - some opcodes may have wrong cycle counts
3. **Verify PPU status read timing** - $2002 reads affect game timing
4. **Consider frame-by-frame input override** - use FCEUX RAM dumps to force correct inputs
5. **Test with simpler TAS** - try a non-speedrun playthrough first

## Commands to Run Tests

```bash
# Run TAS validation test
./gradlew :core:test --tests "FullTASValidationTest.run game with TAS input*"

# Compare NMI counts
grep " 0$" local/tas/fceux-frame-index.txt | wc -l
grep " 0$" local/tas/interpreter-frame-index.txt | wc -l

# Check final state
tail -10 local/tas/interpreter-state-dump.txt
```
