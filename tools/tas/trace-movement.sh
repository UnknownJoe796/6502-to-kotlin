#!/bin/bash

read_byte() {
    od -An -j$2 -N1 -tu1 "$1" | tr -d ' '
}

FCEUX="local/tas/fceux-full-ram.bin"
INTERP="local/tas/interpreter-full-ram.bin"

PLAYER_X=134
X_SPEED=87

# Get gameplay start frames
FSTART=41
ISTART=35  # From last analysis

echo "=== Movement comparison (aligned by game frame) ==="
echo "GameFr | FCEUX(wf) X/Spd | INTERP(wf) X/Spd | Input(off=8)"

for gf in $(seq 150 165); do
    ff=$((FSTART + gf))
    if_=$((ISTART + gf))
    
    fo=$((ff * 2048))
    io=$((if_ * 2048))
    
    fx=$(read_byte $FCEUX $((fo + PLAYER_X)))
    fs=$(read_byte $FCEUX $((fo + X_SPEED)))
    
    ix=$(read_byte $INTERP $((io + PLAYER_X)))
    is=$(read_byte $INTERP $((io + X_SPEED)))
    
    # What input does interpreter use? (wall frame - offset)
    iidx=$((if_ - 8))
    
    # Get the input from nmi-filtered-inputs
    input_line=$(sed -n "$((iidx + 8))p" local/tas/nmi-filtered-inputs.txt 2>/dev/null)
    input_btn=$(echo "$input_line" | awk '{print $3}')
    
    # Check for movement start
    if [ "$fx" != "0" ] || [ "$ix" != "0" ]; then
        marker="<-- "
    else
        marker=""
    fi
    
    printf "G%3d | F%3d X=%3d spd=%3d | I%3d X=%3d spd=%3d | idx=%3d %s %s\n" \
        $gf $ff "$fx" "$fs" $if_ "$ix" "$is" $iidx "$input_btn" "$marker"
done
