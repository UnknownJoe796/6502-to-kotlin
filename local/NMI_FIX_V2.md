# NMI Timing Fix - Version 2

## Current Status After First Fix
After implementing edge-triggered NMI, the interpreter shows:
- Frame 43: Mode=1, FC=40 (game started, FC hasn't reset)
- Frame 44: Mode=1, FC=0 (FC resets here)

But FCEUX shows:
- Frame 43: Mode=1, FC=0 (FC resets immediately when game starts)

## Analysis
The problem is that NMI is triggering at the right time, but the game logic is running one frame behind.

Looking at the current code flow:
1. Frame 43 starts
2. Run instructions until cycle 27395
3. VBlank is set, NMI is triggered
4. NMI handler runs (OperMode becomes 1)
5. Continue running instructions until frame end
6. Dump state: shows Mode=1, FC=40

The issue: **We dump state at the END of frame 43, but the game hasn't run its main loop yet!**

SMB's structure:
- NMI handler updates some state
- Main game loop (called from NMI) increments FC and runs game logic
- RTI returns, game waits for next NMI

So when we dump state at end of frame 43:
- NMI just fired and started OperMode=1 transition
- But the main game loop hasn't run yet to reset FC

On frame 44:
- Game runs its first full frame of gameplay
- FC resets to 0

## Root Cause
The issue is **NOT the NMI timing** - it's the **frame boundary timing**!

FCEUX captures state AFTER the NMI handler completes and the main loop runs.
Our interpreter captures state BEFORE the next frame starts.

## Solution Options

### Option A: Run NMI handler to completion before dumping state
Currently we run the NMI handler in a separate loop. But we dump state **before** that loop. We should dump state **after** the NMI cycles complete.

### Option B: Align frame numbering differently
FCEUX might be numbering frames differently - their "frame 43" might be our "frame 44".

### Option C: Check when FC actually resets in the code
Look at the SMB disassembly to see exactly when FC is reset and ensure we're capturing that moment.

## Investigation Needed
1. Check if FCEUX frame numbers match wall-clock frames or NMI counts
2. Verify when SMB actually increments FC
3. Compare exact timing of state capture between FCEUX and interpreter

Actually, looking at the code again - we DO run the NMI handler to completion before dumping state (lines 542-570). So the issue must be something else.

Let me check the $2000 writes more carefully to see if there's a pattern around frame 43...
