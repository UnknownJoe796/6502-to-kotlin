#!/bin/bash
# Compare IntervalTimerControl timing between FCEUX and interpreter around the warp

RAM_DUMP="local/tas/fceux-full-ram.bin"

echo "=== FCEUX IntervalTimerControl around warp (frame 7600-7800) ==="
echo "Frame | IntCtrl | World | Level | WarpZone | PlayerX | PlayerY | Mode | Task"

for frame in $(seq 7600 7800); do
    offset=$((frame * 2048))
    if [ $offset -lt 36864000 ]; then
        intctrl=$(xxd -s $((offset + 0x077F)) -l 1 -p "$RAM_DUMP")
        world=$(xxd -s $((offset + 0x075F)) -l 1 -p "$RAM_DUMP")
        level=$(xxd -s $((offset + 0x0760)) -l 1 -p "$RAM_DUMP")
        warpzone=$(xxd -s $((offset + 0x06D6)) -l 1 -p "$RAM_DUMP")
        playerx=$(xxd -s $((offset + 0x0086)) -l 1 -p "$RAM_DUMP")
        playery=$(xxd -s $((offset + 0x00CE)) -l 1 -p "$RAM_DUMP")
        mode=$(xxd -s $((offset + 0x0770)) -l 1 -p "$RAM_DUMP")
        task=$(xxd -s $((offset + 0x0772)) -l 1 -p "$RAM_DUMP")
        fc=$(xxd -s $((offset + 0x0009)) -l 1 -p "$RAM_DUMP")

        # Convert hex to decimal for easier reading
        intctrl_dec=$(printf "%d" 0x$intctrl)
        world_dec=$(printf "%d" 0x$world)
        level_dec=$(printf "%d" 0x$level)
        mode_dec=$(printf "%d" 0x$mode)
        task_dec=$(printf "%d" 0x$task)
        fc_dec=$(printf "%d" 0x$fc)

        # Only show every 5th frame unless something interesting
        if [ $((frame % 5)) -eq 0 ] || [ "$world_dec" -ne 3 ] || [ $intctrl_dec -eq 20 ] || [ $intctrl_dec -eq 1 ]; then
            echo "$frame | IntCtrl=$intctrl_dec | W$(($world_dec+1))-$(($level_dec+1)) | WarpZ=$warpzone | X=$playerx Y=$playery | Mode=$mode_dec Task=$task_dec FC=$fc_dec"
        fi
    fi
done | head -80
