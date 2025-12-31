#!/bin/bash
# Compare state when entering W4-3

RAM_DUMP="local/tas/fceux-full-ram.bin"

echo "=== FCEUX state around W4-3 entry (frame 6530-6600) ==="
echo "Frame | World-Level | PlayerX | PlayerY | Lives | RNG0 | RNG1 | IntCtrl | FC"

for frame in $(seq 6530 6600); do
    offset=$((frame * 2048))
    if [ $offset -lt 36864000 ]; then
        world=$(xxd -s $((offset + 0x075F)) -l 1 -p "$RAM_DUMP")
        level=$(xxd -s $((offset + 0x0760)) -l 1 -p "$RAM_DUMP")
        playerx=$(xxd -s $((offset + 0x0086)) -l 1 -p "$RAM_DUMP")
        playery=$(xxd -s $((offset + 0x00CE)) -l 1 -p "$RAM_DUMP")
        lives=$(xxd -s $((offset + 0x075A)) -l 1 -p "$RAM_DUMP")
        rng0=$(xxd -s $((offset + 0x07A7)) -l 1 -p "$RAM_DUMP")
        rng1=$(xxd -s $((offset + 0x07A8)) -l 1 -p "$RAM_DUMP")
        intctrl=$(xxd -s $((offset + 0x077F)) -l 1 -p "$RAM_DUMP")
        fc=$(xxd -s $((offset + 0x0009)) -l 1 -p "$RAM_DUMP")

        world_dec=$((0x$world + 1))
        level_dec=$((0x$level + 1))
        intctrl_dec=$(printf "%d" 0x$intctrl)
        fc_dec=$(printf "%d" 0x$fc)
        lives_dec=$(printf "%d" 0x$lives)

        # Show all frames where world/level changes
        if [ "$world" != "03" ] || [ "$level" != "01" ]; then
            echo "$frame | W$world_dec-$level_dec | X=$playerx Y=$playery | Lives=$lives_dec | RNG=$rng0 $rng1 | IntCtrl=$intctrl_dec | FC=$fc_dec"
        fi
    fi
done

echo ""
echo "=== Also check frames before W4-3 entry ==="
for frame in 6535 6538 6540 6541 6542; do
    offset=$((frame * 2048))
    world=$(xxd -s $((offset + 0x075F)) -l 1 -p "$RAM_DUMP")
    level=$(xxd -s $((offset + 0x0760)) -l 1 -p "$RAM_DUMP")
    playerx=$(xxd -s $((offset + 0x0086)) -l 1 -p "$RAM_DUMP")
    playery=$(xxd -s $((offset + 0x00CE)) -l 1 -p "$RAM_DUMP")
    lives=$(xxd -s $((offset + 0x075A)) -l 1 -p "$RAM_DUMP")
    rng0=$(xxd -s $((offset + 0x07A7)) -l 1 -p "$RAM_DUMP")
    rng1=$(xxd -s $((offset + 0x07A8)) -l 1 -p "$RAM_DUMP")
    intctrl=$(xxd -s $((offset + 0x077F)) -l 1 -p "$RAM_DUMP")
    fc=$(xxd -s $((offset + 0x0009)) -l 1 -p "$RAM_DUMP")

    world_dec=$((0x$world + 1))
    level_dec=$((0x$level + 1))
    intctrl_dec=$(printf "%d" 0x$intctrl)
    fc_dec=$(printf "%d" 0x$fc)
    lives_dec=$(printf "%d" 0x$lives)

    echo "Frame $frame: W$world_dec-$level_dec | X=$playerx Y=$playery | Lives=$lives_dec | RNG=$rng0 $rng1 | IntCtrl=$intctrl_dec | FC=$fc_dec"
done
