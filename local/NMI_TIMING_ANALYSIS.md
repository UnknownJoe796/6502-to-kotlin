# NMI Timing Issue Analysis

## Problem Summary
The interpreter's Frame Counter (FC) resets 1 wall-clock frame later than FCEUX:
- **FCEUX**: FC resets at wall-clock frame 43
- **Interpreter**: FC resets at wall-clock frame 44

This causes TAS input timing issues because inputs are applied based on NMI count, but NMIs are firing at different wall-clock frames.

## Root Cause: NMI Triggering Logic

### Current (Incorrect) Implementation
The test code in `FullTASValidationTest.kt` triggers NMI like this:

```kotlin
// At cycle 27395 (VBlank start):
if (!vblankSet && frameCycles >= vblankStartCycle) {
    ppu.beginVBlank()  // Set VBlank flag
    vblankSet = true
    // NMI fires ONLY if NMI bit is set AT THIS EXACT MOMENT
    if (ppu.shouldTriggerNmi()) {
        interp.triggerNmi()
        // ...
    }
}
```

**Problem**: This only checks if NMI is enabled at cycle 27395. If SMB toggles NMI on/off during the frame, we might miss the NMI!

### What SMB Actually Does
Looking at `/local/tas/ppu-2000-writes.txt`, SMB writes to $2000 multiple times per frame:

```
Frame 3: Write $2000 = 0x10 (NMI=disabled)
Frame 3: Write $2000 = 0x90 (NMI=ENABLED)   // Last write enables NMI
Frame 3: Write $2000 = 0x10 (NMI=disabled)  // Disables again!
Frame 3: Write $2000 = 0x10 (NMI=disabled)
Frame 3: Write $2000 = 0x90 (NMI=ENABLED)   // Final state: ENABLED
```

Each frame typically:
1. Disables NMI (0x10) multiple times during rendering
2. Re-enables NMI (0x90) at the end

This is a common NES pattern - games disable NMI while updating PPU registers to avoid mid-frame interrupts.

### NES Hardware Behavior
On real NES hardware:
- VBlank flag ($2002 bit 7) is set at the start of scanline 241
- If NMI enable bit ($2000 bit 7) is **already set**, NMI fires immediately (within ~2 PPU clocks)
- If NMI enable bit transitions 0→1 **while VBlank is set**, NMI fires immediately
- If NMI enable bit transitions 0→1 **while VBlank is clear**, NMI does NOT fire until next VBlank

### FCEUX vs Interpreter Timing

**FCEUX** (from `/local/tas/fceux-frame-index.txt`):
```
Frame 5: NMI enabled (1)
Frame 6-7: NMI disabled (0)  ← FCEUX sees frames where NMI is disabled!
Frame 8+: NMI enabled (1)
Frame 43: NMI disabled (0)   ← Gameplay starts, NMI temporarily disabled
Frame 44+: NMI enabled (1)
```

**Interpreter** (from `/local/tas/interpreter-frame-index.txt`):
```
Frame 0-2: NMI disabled (0)
Frame 3+: NMI enabled (1)    ← Always enabled, never sees disabled frames
```

**Why the difference?**
- FCEUX's Lua script reads $2000 at frame boundaries and captures the momentary state
- The interpreter checks NMI at VBlank time (cycle 27395)
- If SMB writes 0x10 (disable) after VBlank but before the next frame, FCEUX sees it but the interpreter doesn't

## Solution: Fix NMI Triggering Logic

### Option 1: Edge-Triggered NMI (Correct Hardware Behavior)
Trigger NMI when bit 7 of $2000 transitions from 0→1 **while VBlank is set**:

```kotlin
class PPUStub {
    private var vblankActive = false

    fun write(addr: Int, value: UByte, totalCycles: Long) {
        when (addr and 0x07) {
            0x00 -> {
                val wasEnabled = (ppuCtrl.toInt() and 0x80) != 0
                ppuCtrl = value
                val nowEnabled = (ppuCtrl.toInt() and 0x80) != 0

                // Edge-triggered: 0→1 transition while VBlank is active triggers NMI
                if (!wasEnabled && nowEnabled && vblankActive) {
                    // Request NMI immediately
                    nmiPending = true
                }
            }
        }
    }

    fun beginVBlank() {
        ppuStatus = (ppuStatus.toInt() or 0xC0).toUByte()
        vblankActive = true

        // Level-triggered: If NMI already enabled, trigger immediately
        if ((ppuCtrl.toInt() and 0x80) != 0) {
            nmiPending = true
        }
    }

    fun endVBlank() {
        ppuStatus = (ppuStatus.toInt() and 0x7F).toUByte()
        vblankActive = false
    }
}
```

### Option 2: Sample NMI State at End of Frame (Simpler)
Instead of checking at VBlank start (cycle 27395), check at the very end of the frame to capture the final state:

```kotlin
// At end of frame (cycle 29780):
if (ppu.shouldTriggerNmi()) {
    interp.triggerNmi()
    nmiCount++
}
```

But this is less accurate - it would trigger NMI late.

## Recommended Fix

Implement **Option 1** (edge-triggered NMI) because it matches hardware behavior:

1. Add `vblankActive` flag to `PPUStub`
2. In `write($2000)`: trigger NMI on 0→1 transition if VBlank is active
3. In `beginVBlank()`: trigger NMI if NMI bit is already set
4. In `endVBlank()`: clear `vblankActive` flag

This will ensure NMIs fire at the correct time regardless of when SMB toggles the NMI enable bit.

## Impact on Frame Alignment

Once NMI timing is fixed, the interpreter's frames should align with FCEUX:
- Both will see NMI fire at the same wall-clock frames
- TAS inputs will be processed at the correct game frames
- FC will reset at frame 43 (not 44)
- OperMode will transition at frame 42 (not 43)

This should fix the pause button timing issue seen at game frame 153.
