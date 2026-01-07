#!/bin/bash
# Analyze NMI skip patterns across entire TAS

RAMFILE="local/tas/fceux-full-ram.bin"
FILESIZE=$(stat -f%z "$RAMFILE" 2>/dev/null || stat -c%s "$RAMFILE" 2>/dev/null)
MAXFRAMES=$((FILESIZE / 2048))

echo "=== Full NMI Skip Pattern Analysis ($MAXFRAMES frames) ==="
echo ""

prev_intctrl=""
prev_mode=""
prev_task=""
prev_areatype=""
prev_world=""
prev_level=""
skip_frame=-1
skip_count=0

for frame in $(seq 0 $((MAXFRAMES - 1))); do
    base=$((frame * 2048))
    intctrl=$(xxd -s $((base + 0x77f)) -l 1 -p "$RAMFILE")
    mode=$(xxd -s $((base + 0x770)) -l 1 -p "$RAMFILE")
    task=$(xxd -s $((base + 0x772)) -l 1 -p "$RAMFILE")
    areatype=$(xxd -s $((base + 0x74e)) -l 1 -p "$RAMFILE")
    world=$(xxd -s $((base + 0x75f)) -l 1 -p "$RAMFILE")
    level=$(xxd -s $((base + 0x760)) -l 1 -p "$RAMFILE")

    if [ -n "$prev_intctrl" ]; then
        if [ "$intctrl" = "$prev_intctrl" ]; then
            # NMI skip detected
            if [ $skip_frame -eq -1 ]; then
                skip_frame=$((frame - 1))
                skip_mode=$prev_mode
                skip_task=$prev_task
                skip_areatype=$prev_areatype
                skip_world=$prev_world
                skip_level=$prev_level
            fi
            ((skip_count++))
        else
            # NMI resumed - report skip if any
            if [ $skip_frame -ne -1 ]; then
                changes=""
                [ "$skip_mode" != "$mode" ] && changes="${changes}Mode:${skip_mode}→${mode} "
                [ "$skip_task" != "$task" ] && changes="${changes}Task:${skip_task}→${task} "
                [ "$skip_areatype" != "$areatype" ] && changes="${changes}Area:${skip_areatype}→${areatype} "
                [ "$skip_world" != "$world" ] && changes="${changes}W:${skip_world}→${world} "
                [ "$skip_level" != "$level" ] && changes="${changes}L:${skip_level}→${level} "

                echo "Frame $skip_frame: $skip_count skip(s) | $changes"
                skip_frame=-1
                skip_count=0
            fi
        fi
    fi

    prev_intctrl=$intctrl
    prev_mode=$mode
    prev_task=$task
    prev_areatype=$areatype
    prev_world=$world
    prev_level=$level
done 2>/dev/null
