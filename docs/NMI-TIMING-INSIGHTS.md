# NMI Timing Insights for SMB Decompilation

This document captures critical knowledge gained from debugging the 6502 interpreter TAS execution. These insights apply directly to the decompiled Kotlin version.

## The Core Problem

Super Mario Bros relies on precise NMI (Non-Maskable Interrupt) timing. The NMI fires once per frame (~29,780 CPU cycles on NTSC) during VBlank. However, **NMI handlers can take longer than one frame to execute**, causing subsequent VBlank NMIs to be missed.

This is not a bug - it's intentional behavior the game relies on for timing.

## Key Insight: Variable Execution Time

Different code paths in the NMI handler have vastly different execution times:

| Frame | Steps | Cycles (est.) | Frames Consumed | Context |
|-------|-------|---------------|-----------------|---------|
| 5 | 24,375 | ~68,250 | 3 | Initialization/setup |
| 42 | 13,166 | ~36,864 | 2 | Title screen transition |
| 612, 926, etc. | ~12,400 | ~34,700 | 2 | Level transitions |
| Normal gameplay | 3,000-7,000 | ~10,000-20,000 | 1 | Standard frame |

The **cycle-per-step ratio varies from ~2.4 to ~2.8** depending on instruction mix:
- Zero-page operations: 3 cycles
- Immediate operations: 2 cycles
- Absolute addressing: 4 cycles
- Indirect/indexed: 4-6 cycles

A fixed estimate cannot work because the acceptable window is impossibly tight (~0.002 cycles/step).

## IntervalTimerControl ($077F) - The Timing Oracle

`IntervalTimerControl` is SMB's internal frame counter that:
- Decrements by 1 each NMI
- Wraps from 0 to 20
- Is used for timing game events (animations, enemy spawns, etc.)

**Critical insight**: By observing when IntCtrl decrements, you can determine exactly how many frames an NMI consumed. If IntCtrl stays the same for N frames then decrements, that NMI took N frames.

## Implications for Decompiled Kotlin Version

### 1. Frame Timing Must Be Explicit

The decompiled code needs explicit frame timing, not implicit cycle counting:

```kotlin
// BAD: Assumes every call is one frame
fun gameLoop() {
    while (true) {
        nmiHandler()  // What if this takes 2 frames?
    }
}

// GOOD: Explicit frame consumption
fun gameLoop() {
    var frame = 0
    while (true) {
        val framesConsumed = runNmiHandler()
        frame += framesConsumed
        // Skip input processing for consumed frames
    }
}
```

### 2. Long NMI Detection

The decompiled version should detect when NMI handlers consume multiple frames:

```kotlin
// Key scenarios that trigger multi-frame NMIs:
// - Level initialization (OperMode_Task transitions)
// - Screen scrolling setup
// - Enemy/object spawning bursts
// - Score/status bar updates during transitions

fun nmiHandler(): Int {
    val startTime = measureCycles()

    // ... NMI logic ...

    val cyclesUsed = measureCycles() - startTime
    return (cyclesUsed + CYCLES_PER_FRAME - 1) / CYCLES_PER_FRAME
}
```

### 3. TAS Replay Requirements

For TAS replay to work correctly:

1. **Input timing must account for frame skips**: If NMI takes 3 frames, inputs for frames N+1 and N+2 are never read by the game
2. **State comparison must use correct frame offsets**: Compare after the long NMI completes, not at each nominal frame
3. **RNG will diverge if timing is wrong**: PseudoRandomBitReg updates each NMI, so frame skips affect RNG state

### 4. OperMode_Task State Machine

The `OperMode_Task` variable ($0772) indicates what the game is doing:
- Task 1: Initialization/transition (often multi-frame)
- Task 3: Normal gameplay (usually single-frame)

Multi-frame NMIs typically occur during Task 1 transitions.

## Debugging Checklist

When debugging timing issues in the decompiled version:

1. **Check IntervalTimerControl**: Is it decrementing at the expected rate?
2. **Check FrameCounter**: Does it match expected progression?
3. **Check OperMode_Task**: Are we in a transition state?
4. **Compare RNG values**: Divergence indicates frame timing mismatch
5. **Look for cumulative drift**: Small timing errors accumulate over thousands of frames

## Frame Timing Formula

For reference, the correct formula for frames consumed:

```kotlin
// Using floor division (not ceiling!)
// An NMI finishing 14% into the next frame doesn't skip that frame
val framesConsumed = maxOf(1, cyclesUsed / CYCLES_PER_FRAME)

// VBlank NMI is edge-triggered: only missed if handler is still
// running when VBlank occurs (~cycle 27,394 of each frame)
```

## Test Validation

The interpreter now passes these validation tests:
- **8,000 frames** of cycle-accurate comparison with FCEUX
- **Complete TAS playthrough** from start to victory
- **All warps work correctly**: W1-2 pipe, W4-2 warp zone → W8-1

## Files Reference

- `core/src/test/kotlin/interpreter/FrameCounterBasedTASTest.kt`: Contains timing tests
- `local/tas/fceux-full-ram.bin`: FCEUX RAM dumps for comparison
- `happylee-warps.fm2`: TAS input file for testing

## Key SMB Memory Addresses

Quick reference for debugging:

| Address | Name | Purpose |
|---------|------|---------|
| $0009 | FrameCounter | Increments each NMI (0-255, wraps) |
| $077F | IntervalTimerControl | Decrements each NMI (20→0, wraps to 20) |
| $0770 | OperMode | Game mode (0=demo, 1=title, 2=gameplay) |
| $0772 | OperMode_Task | Current task (1=init/transition, 3=gameplay) |
| $075F | WorldNumber | Current world (0-7) |
| $0760 | LevelNumber | Current level (0-3) |
| $075A | NumberofLives | Player lives |
| $0086 | Player_X_Position | Mario's X position |
| $00CE | Player_Y_Position | Mario's Y position |
| $006D | Player_PageLoc | Screen page (horizontal position) |
| $07A7 | PseudoRandomBitReg[0] | RNG byte 0 |
| $07A8 | PseudoRandomBitReg[1] | RNG byte 1 |
| $06D6 | WarpZoneControl | Warp zone state |
| $001D | Player_State | Player state (0=ground, 1=climb, etc.) |

## Summary

The key wisdom: **NMI timing is not uniform**. The decompiled Kotlin version must either:
1. Track actual execution time and handle multi-frame NMIs explicitly
2. Use a frame timing oracle (like IntCtrl comparison) for validation
3. Structure the code to make timing-critical paths identifiable

Do not assume "one function call = one frame" - that assumption breaks TAS replay and causes cumulative drift leading to warp failures and other timing-sensitive bugs.
