#!/bin/bash
# Quick NMI skip analysis - first 500 frames

RAMFILE="local/tas/fceux-full-ram.bin"
MAXFRAMES=500

echo "=== Quick NMI Skip Analysis (first $MAXFRAMES frames) ==="
echo ""
echo "Frame,IntCtrl,Mode,Task,AreaType,World,Level"

for frame in $(seq 0 $((MAXFRAMES - 1))); do
    base=$((frame * 2048))
    intctrl=$(xxd -s $((base + 0x77f)) -l 1 -p "$RAMFILE")
    mode=$(xxd -s $((base + 0x770)) -l 1 -p "$RAMFILE")
    task=$(xxd -s $((base + 0x772)) -l 1 -p "$RAMFILE")
    areatype=$(xxd -s $((base + 0x74e)) -l 1 -p "$RAMFILE")
    world=$(xxd -s $((base + 0x75f)) -l 1 -p "$RAMFILE")
    level=$(xxd -s $((base + 0x760)) -l 1 -p "$RAMFILE")
    echo "$frame,$intctrl,$mode,$task,$areatype,$world,$level"
done | tee /tmp/nmi_quick.csv | awk -F',' '
NR<=2 { prev_intctrl = $2; prev_mode = $3; prev_task = $4; prev_areatype = $5; next }
{
    frame = $1
    intctrl = $2
    mode = $3
    task = $4
    areatype = $5

    if (intctrl == prev_intctrl && prev_intctrl != "") {
        print "NMI SKIP at frame " frame-1 ": IntCtrl=" intctrl " Mode=" mode " Task=" task " AreaType=" areatype
    }

    # Detect state changes
    if (mode != prev_mode && prev_mode != "") {
        print "MODE CHANGE at frame " frame ": " prev_mode " -> " mode
    }
    if (task != prev_task && prev_task != "") {
        print "TASK CHANGE at frame " frame ": " prev_task " -> " task " (Mode=" mode ")"
    }

    prev_intctrl = intctrl
    prev_mode = mode
    prev_task = task
    prev_areatype = areatype
}
'
