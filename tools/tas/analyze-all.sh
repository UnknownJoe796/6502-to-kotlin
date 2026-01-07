#!/bin/bash
# Comprehensive TAS analysis script
# Run with: ./local/tas/analyze-all.sh

set -e

FCEUX="local/tas/fceux-full-ram.bin"
INTERP="local/tas/interpreter-full-ram.bin"

read_byte() {
    od -An -j$2 -N1 -tu1 "$1" 2>/dev/null | tr -d ' '
}

echo "=========================================="
echo "TAS COMPREHENSIVE ANALYSIS REPORT"
echo "=========================================="
echo ""

# 1. Find gameplay start frames
echo "=== 1. GAMEPLAY START FRAMES ==="
for f in $(seq 30 50); do
    offset=$((f * 2048 + 0x770))
    mode=$(read_byte $FCEUX $offset)
    if [ "$mode" = "1" ]; then
        echo "FCEUX enters gameplay at frame $f"
        FCEUX_START=$f
        break
    fi
done

for f in $(seq 30 50); do
    offset=$((f * 2048 + 0x770))
    mode=$(read_byte $INTERP $offset)
    if [ "$mode" = "1" ]; then
        echo "Interpreter enters gameplay at frame $f"
        INTERP_START=$f
        break
    fi
done
echo "Frame offset: $((INTERP_START - FCEUX_START))"
echo ""

# 2. Button flow comparison at movement start
echo "=== 2. BUTTON FLOW AT MOVEMENT START ==="
echo "G.Fr | FC: \$0A \$0C \$6FC | INT: \$0A \$0C \$6FC | Match?"
echo "-----|-----------------|-----------------|------"

for gf in 150 151 152 153 154 155 156 157 158 159 160; do
    ff=$((FCEUX_START + gf))
    if_=$((INTERP_START + gf))

    fo=$((ff * 2048))
    io=$((if_ * 2048))

    f_0A=$(read_byte $FCEUX $((fo + 0x0A)))
    f_0C=$(read_byte $FCEUX $((fo + 0x0C)))
    f_6FC=$(read_byte $FCEUX $((fo + 0x6FC)))

    i_0A=$(read_byte $INTERP $((io + 0x0A)))
    i_0C=$(read_byte $INTERP $((io + 0x0C)))
    i_6FC=$(read_byte $INTERP $((io + 0x6FC)))

    match="OK"
    [ "$f_0A" != "$i_0A" ] || [ "$f_0C" != "$i_0C" ] || [ "$f_6FC" != "$i_6FC" ] && match="DIFF"

    printf "G%3d | %3d %3d %3d | %3d %3d %3d | %s\n" \
        $gf "$f_0A" "$f_0C" "$f_6FC" "$i_0A" "$i_0C" "$i_6FC" "$match"
done
echo ""

# 3. Full RAM comparison (ignoring FrameCounter)
echo "=== 3. FULL RAM COMPARISON (ignoring \$0009) ==="

for gf in 0 50 100 150 153 154 155 156 160 180 200 300; do
    ff=$((FCEUX_START + gf))
    if_=$((INTERP_START + gf))

    dd if="$FCEUX" bs=2048 skip=$ff count=1 2>/dev/null > /tmp/fceux_frame.bin
    dd if="$INTERP" bs=2048 skip=$if_ count=1 2>/dev/null > /tmp/interp_frame.bin

    # Zero out FrameCounter
    printf '\x00' | dd of=/tmp/fceux_frame.bin bs=1 seek=9 count=1 conv=notrunc 2>/dev/null
    printf '\x00' | dd of=/tmp/interp_frame.bin bs=1 seek=9 count=1 conv=notrunc 2>/dev/null

    diff_count=$(cmp -l /tmp/fceux_frame.bin /tmp/interp_frame.bin 2>/dev/null | wc -l | tr -d ' ')

    echo "Game frame $gf (F$ff/I$if_): $diff_count bytes differ"
    if [ "$diff_count" -gt 0 ] && [ "$diff_count" -lt 200 ]; then
        cmp -l /tmp/fceux_frame.bin /tmp/interp_frame.bin 2>/dev/null | head -3 | while read pos f_val i_val; do
            addr=$((pos - 1))
            printf "  \$%04X: FC=%d INT=%d\n" $addr $f_val $i_val
        done
    fi
done
rm -f /tmp/fceux_frame.bin /tmp/interp_frame.bin
echo ""

# 4. Speed comparison at key frames
echo "=== 4. PLAYER SPEED COMPARISON ==="
echo "G.Fr | FCEUX X/Spd | INTERP X/Spd"
for gf in 154 155 156 157 158 159 160 170 180 190 200; do
    ff=$((FCEUX_START + gf))
    if_=$((INTERP_START + gf))

    fo=$((ff * 2048))
    io=$((if_ * 2048))

    fx=$(read_byte $FCEUX $((fo + 0x86)))
    fs=$(read_byte $FCEUX $((fo + 0x57)))
    ix=$(read_byte $INTERP $((io + 0x86)))
    is=$(read_byte $INTERP $((io + 0x57)))

    printf "G%3d | X=%3d Spd=%3d | X=%3d Spd=%3d\n" $gf "$fx" "$fs" "$ix" "$is"
done
echo ""

# 5. State debug summary
echo "=== 5. STATE DEBUG SUMMARY ==="
head -20 local/tas/state-debug.txt
echo "..."
echo "(Showing first 20 lines)"
echo ""

echo "=========================================="
echo "ANALYSIS COMPLETE"
echo "=========================================="
