#!/bin/bash
# Summarize NMI-skip events by pattern

RAMFILE="local/tas/fceux-full-ram.bin"
if [ ! -f "$RAMFILE" ]; then
    echo "Error: $RAMFILE not found"
    exit 1
fi

FILESIZE=$(stat -f%z "$RAMFILE" 2>/dev/null || stat -c%s "$RAMFILE" 2>/dev/null)
MAXFRAMES=$((FILESIZE / 2048))

echo "=== NMI Skip Pattern Summary ==="
echo ""

# Track patterns
declare -A patterns

prev_intctrl=""
prev_mode=""
prev_task=""
prev_world=""
prev_level=""
prev_areatype=""
skip_started=0

for frame in $(seq 0 $((MAXFRAMES - 1))); do
    base=$((frame * 2048))

    intctrl=$(xxd -s $((base + 0x77f)) -l 1 "$RAMFILE" 2>/dev/null | awk '{print $2}')
    mode=$(xxd -s $((base + 0x770)) -l 1 "$RAMFILE" 2>/dev/null | awk '{print $2}')
    task=$(xxd -s $((base + 0x772)) -l 1 "$RAMFILE" 2>/dev/null | awk '{print $2}')
    world=$(xxd -s $((base + 0x75f)) -l 1 "$RAMFILE" 2>/dev/null | awk '{print $2}')
    level=$(xxd -s $((base + 0x760)) -l 1 "$RAMFILE" 2>/dev/null | awk '{print $2}')
    areatype=$(xxd -s $((base + 0x74e)) -l 1 "$RAMFILE" 2>/dev/null | awk '{print $2}')

    if [ -n "$prev_intctrl" ]; then
        # Detect NMI skip (IntCtrl stays same)
        if [ "$intctrl" = "$prev_intctrl" ]; then
            if [ $skip_started -eq 0 ]; then
                skip_started=1
                skip_frame=$((frame - 1))
                skip_mode_before=$prev_mode
                skip_task_before=$prev_task
                skip_world_before=$prev_world
                skip_level_before=$prev_level
                skip_areatype_before=$prev_areatype
            fi
            skip_mode_after=$mode
            skip_task_after=$task
            skip_world_after=$world
            skip_level_after=$level
            skip_areatype_after=$areatype
            skip_count=$((skip_count + 1))
        else
            # Skip ended - record pattern
            if [ $skip_started -eq 1 ]; then
                pattern="Mode:${skip_mode_before}→${skip_mode_after} Task:${skip_task_before}→${skip_task_after}"
                if [ "$skip_world_before" != "$skip_world_after" ]; then
                    pattern="$pattern World:${skip_world_before}→${skip_world_after}"
                fi
                if [ "$skip_level_before" != "$skip_level_after" ]; then
                    pattern="$pattern Level:${skip_level_before}→${skip_level_after}"
                fi
                if [ "$skip_areatype_before" != "$skip_areatype_after" ]; then
                    pattern="$pattern AreaType:${skip_areatype_before}→${skip_areatype_after}"
                fi
                pattern="$pattern (${skip_count} frames)"

                # Record pattern
                echo "Frame $skip_frame: $pattern"

                skip_started=0
                skip_count=0
            fi
        fi
    fi

    prev_intctrl=$intctrl
    prev_mode=$mode
    prev_task=$task
    prev_world=$world
    prev_level=$level
    prev_areatype=$areatype
done

echo ""
echo "=== Analysis Complete ==="
