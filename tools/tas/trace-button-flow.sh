#!/bin/bash

FCEUX="local/tas/fceux-full-ram.bin"
INTERP="local/tas/interpreter-full-ram.bin"

read_byte() {
    od -An -j$2 -N1 -tu1 "$1" | tr -d ' '
}

FCEUX_START=41
INTERP_START=37  # Updated!

echo "=== Button flow trace (frames 150-165) ==="
echo "G.Fr | FC: \$0A \$0C \$6FC | INT: \$0A \$0C \$6FC | Match?"

for gf in 150 151 152 153 154 155 156 157 158 159 160 161 162 163 164 165; do
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
    
    match="✓"
    [ "$f_0A" != "$i_0A" ] || [ "$f_0C" != "$i_0C" ] || [ "$f_6FC" != "$i_6FC" ] && match="✗"
    
    printf "G%3d | %3d %3d %3d | %3d %3d %3d | %s\n" \
        $gf "$f_0A" "$f_0C" "$f_6FC" "$i_0A" "$i_0C" "$i_6FC" "$match"
done
