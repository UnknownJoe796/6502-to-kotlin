#!/bin/bash
# Analyze FCEUX progress at key frames

RAMFILE="local/tas/fceux-full-ram.bin"

# SMB addresses
OPERMODE=$((0x0770))
WORLDNUM=$((0x075F))
LEVELNUM=$((0x0760))
PAGELOG=$((0x071A))
PLAYERX=$((0x0086))
LIVES=$((0x075A))
FC=$((0x0009))

echo "=== FCEUX Progress Analysis ==="
echo ""

for frame in 1000 2000 3000 4000 5000 6000 7000 8000 9000 10000 12000 14000 16000 17000; do
    offset=$((frame * 2048))

    mode=$(xxd -s $((offset + OPERMODE)) -l 1 -p "$RAMFILE")
    world=$(xxd -s $((offset + WORLDNUM)) -l 1 -p "$RAMFILE")
    level=$(xxd -s $((offset + LEVELNUM)) -l 1 -p "$RAMFILE")
    page=$(xxd -s $((offset + PAGELOG)) -l 1 -p "$RAMFILE")
    playerx=$(xxd -s $((offset + PLAYERX)) -l 1 -p "$RAMFILE")
    lives=$(xxd -s $((offset + LIVES)) -l 1 -p "$RAMFILE")
    fc=$(xxd -s $((offset + FC)) -l 1 -p "$RAMFILE")

    world_dec=$((16#$world + 1))
    level_dec=$((16#$level + 1))
    page_dec=$((16#$page))
    playerx_dec=$((16#$playerx))
    lives_dec=$((16#$lives))
    fc_dec=$((16#$fc))

    echo "Frame $frame: World $world_dec-$level_dec, Page=$page_dec, X=$playerx_dec, Lives=$lives_dec, FC=$fc_dec"
done
