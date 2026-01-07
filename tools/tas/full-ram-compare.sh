#!/bin/bash

FCEUX="local/tas/fceux-full-ram.bin"
INTERP="local/tas/interpreter-full-ram.bin"

FCEUX_SIZE=$(stat -f%z "$FCEUX" 2>/dev/null || stat -c%s "$FCEUX")
INTERP_SIZE=$(stat -f%z "$INTERP" 2>/dev/null || stat -c%s "$INTERP")

FCEUX_FRAMES=$((FCEUX_SIZE / 2048))
INTERP_FRAMES=$((INTERP_SIZE / 2048))

echo "FCEUX: $FCEUX_FRAMES frames, Interpreter: $INTERP_FRAMES frames"

# Correct gameplay start frames
FCEUX_START=41
INTERP_START=37

echo ""
echo "=== Full RAM comparison (ignoring \$0009 FrameCounter) ==="
echo "Aligned by game frame (FCEUX-$FCEUX_START, INTERP-$INTERP_START)"
echo ""

for gf in 0 5 10 20 50 100 150 152 153 154 155 156 157 158 159 160 170 180 190 200; do
    ff=$((FCEUX_START + gf))
    if_=$((INTERP_START + gf))
    
    if [ $ff -ge $FCEUX_FRAMES ] || [ $if_ -ge $INTERP_FRAMES ]; then
        continue
    fi
    
    dd if="$FCEUX" bs=2048 skip=$ff count=1 2>/dev/null > /tmp/fceux_frame.bin
    dd if="$INTERP" bs=2048 skip=$if_ count=1 2>/dev/null > /tmp/interp_frame.bin
    
    # Zero out FrameCounter ($0009)
    printf '\x00' | dd of=/tmp/fceux_frame.bin bs=1 seek=9 count=1 conv=notrunc 2>/dev/null
    printf '\x00' | dd of=/tmp/interp_frame.bin bs=1 seek=9 count=1 conv=notrunc 2>/dev/null
    
    diff_count=$(cmp -l /tmp/fceux_frame.bin /tmp/interp_frame.bin 2>/dev/null | wc -l)
    
    if [ "$diff_count" -eq 0 ]; then
        echo "Game frame $gf (F$ff/I$if_): PERFECT MATCH"
    else
        echo "Game frame $gf (F$ff/I$if_): $diff_count bytes differ"
        cmp -l /tmp/fceux_frame.bin /tmp/interp_frame.bin 2>/dev/null | head -5 | while read pos f_val i_val; do
            addr=$((pos - 1))
            printf "  \$%04X: FCEUX=%d INTERP=%d\n" $addr $f_val $i_val
        done
    fi
done

rm -f /tmp/fceux_frame.bin /tmp/interp_frame.bin
