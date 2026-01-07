#!/bin/bash

read_byte() {
    od -An -j$2 -N1 -tu1 "$1" | tr -d ' '
}

FCEUX="local/tas/fceux-full-ram.bin"
INTERP="local/tas/interpreter-full-ram.bin"

# SMB addresses  
PLAYER_X=134     # 0x0086
X_SPEED=87       # 0x0057
FC=9             # 0x09

# FCEUX gameplay starts at 41, interpreter at 33
# Check game frames 140-160 (first movement)
echo "=== Fine trace around first movement ==="
echo "GameFr | Wall(F/I) | FCEUX X/Spd/FC | INTERP X/Spd/FC"

for gf in $(seq 140 165); do
    ff=$((41 + gf))  # FCEUX wall frame
    if_=$((33 + gf))  # Interpreter wall frame
    
    fo=$((ff * 2048))
    io=$((if_ * 2048))
    
    fx=$(read_byte $FCEUX $((fo + PLAYER_X)))
    fs=$(read_byte $FCEUX $((fo + X_SPEED)))
    ffc=$(read_byte $FCEUX $((fo + FC)))
    
    ix=$(read_byte $INTERP $((io + PLAYER_X)))
    is=$(read_byte $INTERP $((io + X_SPEED)))
    ifc=$(read_byte $INTERP $((io + FC)))
    
    # Check for first X change
    if [ "$fx" != "0" ] || [ "$ix" != "0" ]; then
        marker="<-- MOVEMENT"
    else
        marker=""
    fi
    
    printf "%6d | %3d/%3d  | X=%3d spd=%3d FC=%3d | X=%3d spd=%3d FC=%3d %s\n" \
        $gf $ff $if_ "$fx" "$fs" "$ffc" "$ix" "$is" "$ifc" "$marker"
done

# Also check what inputs are being used
echo ""
echo "=== Input indices at these frames ==="
echo "With offset 6: interpreter frame F uses input F-6"
for if_ in $(seq 173 198); do
    idx=$((if_ - 6))
    # Read the input from nmi-filtered-inputs.txt
    input=$(sed -n "$((idx + 8))p" local/tas/nmi-filtered-inputs.txt | awk '{print $3}')
    echo "Interp frame $if_ -> input idx $idx -> $input"
done
