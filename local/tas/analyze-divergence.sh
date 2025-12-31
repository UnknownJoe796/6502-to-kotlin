#!/bin/bash

# Read bytes from binary file
# Usage: read_byte file offset
read_byte() {
    od -An -j$2 -N1 -tu1 "$1" | tr -d ' '
}

FCEUX="local/tas/fceux-full-ram.bin"
INTERP="local/tas/interpreter-full-ram.bin"

# SMB addresses
OPERMODE=1904    # 0x0770
PLAYER_X=134     # 0x0086
SCREEN_PAGE=1818 # 0x071A
X_SPEED=87       # 0x0057
FC=9             # 0x09

echo "=== Finding gameplay start frames ==="

# Find FCEUX gameplay start
for f in $(seq 30 60); do
    offset=$((f * 2048 + OPERMODE))
    mode=$(read_byte $FCEUX $offset)
    if [ "$mode" = "1" ]; then
        echo "FCEUX enters gameplay at frame $f"
        FCEUX_START=$f
        break
    fi
done

# Find Interpreter gameplay start  
for f in $(seq 30 60); do
    offset=$((f * 2048 + OPERMODE))
    mode=$(read_byte $INTERP $offset)
    if [ "$mode" = "1" ]; then
        echo "Interpreter enters gameplay at frame $f"
        INTERP_START=$f
        break
    fi
done

echo ""
echo "Frame offset = $((INTERP_START - FCEUX_START))"
echo ""

echo "=== Comparing aligned frames ==="
echo "GameFr | FCEUX: X Pg Spd | INTERP: X Pg Spd | Match"
echo "-------|----------------|-----------------|------"

for gf in 0 5 10 15 20 25 30 40 50 75 100 150 200 250 300 400 500; do
    ff=$((FCEUX_START + gf))
    if_=$((INTERP_START + gf))
    
    fo=$((ff * 2048))
    io=$((if_ * 2048))
    
    fx=$(read_byte $FCEUX $((fo + PLAYER_X)))
    fp=$(read_byte $FCEUX $((fo + SCREEN_PAGE)))
    fs=$(read_byte $FCEUX $((fo + X_SPEED)))
    
    ix=$(read_byte $INTERP $((io + PLAYER_X)))
    ip=$(read_byte $INTERP $((io + SCREEN_PAGE)))
    is=$(read_byte $INTERP $((io + X_SPEED)))
    
    match="✓"
    [ "$fx" != "$ix" ] || [ "$fp" != "$ip" ] && match="✗"
    
    printf "%6d | X=%3d pg=%d spd=%3d | X=%3d pg=%d spd=%3d | %s\n" $gf "$fx" "$fp" "$fs" "$ix" "$ip" "$is" "$match"
done
