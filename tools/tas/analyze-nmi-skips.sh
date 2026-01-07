#!/bin/bash
# Analyze FCEUX RAM dump to find NMI-skip events
# An NMI skip occurs when IntCtrl ($077F) doesn't decrement between frames

RAMFILE="local/tas/fceux-full-ram.bin"
if [ ! -f "$RAMFILE" ]; then
    echo "Error: $RAMFILE not found"
    exit 1
fi

FILESIZE=$(stat -f%z "$RAMFILE" 2>/dev/null || stat -c%s "$RAMFILE" 2>/dev/null)
MAXFRAMES=$((FILESIZE / 2048))

echo "=== NMI Skip Analysis ==="
echo "Analyzing $MAXFRAMES frames from FCEUX RAM dump"
echo ""
echo "Looking for frames where IntCtrl doesn't decrement (NMI was skipped)"
echo ""

# Track state
prev_intctrl=""
prev_fc=""
prev_mode=""
prev_task=""
prev_world=""
prev_level=""
skip_start=-1
skip_count=0

echo "Frame | IntCtrl | FC | Mode | Task | World | Level | Event"
echo "------|---------|----|----- |------|-------|-------|------"

for frame in $(seq 0 $((MAXFRAMES - 1))); do
    base=$((frame * 2048))

    # Read key addresses
    intctrl=$(xxd -s $((base + 0x77f)) -l 1 "$RAMFILE" 2>/dev/null | awk '{print $2}')
    fc=$(xxd -s $((base + 0x09)) -l 1 "$RAMFILE" 2>/dev/null | awk '{print $2}')
    mode=$(xxd -s $((base + 0x770)) -l 1 "$RAMFILE" 2>/dev/null | awk '{print $2}')
    task=$(xxd -s $((base + 0x772)) -l 1 "$RAMFILE" 2>/dev/null | awk '{print $2}')
    world=$(xxd -s $((base + 0x75f)) -l 1 "$RAMFILE" 2>/dev/null | awk '{print $2}')
    level=$(xxd -s $((base + 0x760)) -l 1 "$RAMFILE" 2>/dev/null | awk '{print $2}')

    # Convert hex to decimal for comparison
    intctrl_dec=$((16#$intctrl))
    prev_intctrl_dec=$((16#${prev_intctrl:-ff}))

    event=""

    # Detect NMI skip: IntCtrl stays the same or increases (reset)
    if [ -n "$prev_intctrl" ]; then
        if [ "$intctrl" = "$prev_intctrl" ]; then
            event="NMI_SKIP"
            if [ $skip_start -eq -1 ]; then
                skip_start=$((frame - 1))
            fi
            ((skip_count++))
        elif [ $intctrl_dec -gt $prev_intctrl_dec ]; then
            # IntCtrl increased - likely a reset or initialization
            if [ $skip_start -ne -1 ]; then
                event="NMI_RESUME (skipped $skip_count frames from $skip_start)"
                skip_start=-1
                skip_count=0
            else
                event="INTCTRL_RESET"
            fi
        else
            # Normal decrement
            if [ $skip_start -ne -1 ]; then
                event="NMI_RESUME (skipped $skip_count frames from $skip_start)"
                skip_start=-1
                skip_count=0
            fi
        fi
    fi

    # Detect state changes
    if [ "$mode" != "$prev_mode" ] && [ -n "$prev_mode" ]; then
        event="${event:+$event, }MODE_CHANGE($prev_mode->$mode)"
    fi
    if [ "$task" != "$prev_task" ] && [ -n "$prev_task" ]; then
        event="${event:+$event, }TASK_CHANGE($prev_task->$task)"
    fi
    if [ "$world" != "$prev_world" ] && [ -n "$prev_world" ]; then
        event="${event:+$event, }WORLD_CHANGE($prev_world->$world)"
    fi
    if [ "$level" != "$prev_level" ] && [ -n "$prev_level" ]; then
        event="${event:+$event, }LEVEL_CHANGE($prev_level->$level)"
    fi

    # Print if there's an event
    if [ -n "$event" ]; then
        printf "%5d |   %s    | %s |  %s  |  %s   |   %s   |   %s   | %s\n" \
            $frame "$intctrl" "$fc" "$mode" "$task" "$world" "$level" "$event"
    fi

    prev_intctrl=$intctrl
    prev_fc=$fc
    prev_mode=$mode
    prev_task=$task
    prev_world=$world
    prev_level=$level
done

echo ""
echo "=== Summary ==="
echo "Total frames analyzed: $MAXFRAMES"
