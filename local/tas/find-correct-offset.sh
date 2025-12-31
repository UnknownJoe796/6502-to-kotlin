#!/bin/bash

# In nmi-filtered-inputs.txt, each line is: NMI_INDEX FCEUX_FRAME BUTTONS
# We need to find: at interpreter frame F, what NMI index matches FCEUX's game state?

# FCEUX gameplay starts at frame 41, interpreter at frame 33
# At game frame G:
#   FCEUX wall frame = 41 + G
#   Interpreter wall frame = 33 + G

# What NMI index corresponds to a FCEUX wall frame?
# Looking at the file pattern: NMI index ≈ FCEUX frame - 8 (roughly)

echo "=== Mapping FCEUX frames to NMI indices ==="
head -200 local/tas/nmi-filtered-inputs.txt | tail -30 | while read nmi frame btn; do
    if [ ! -z "$nmi" ] && [ ! "$nmi" = "#" ]; then
        diff=$((frame - nmi))
        echo "NMI $nmi at FCEUX frame $frame (diff=$diff)"
    fi
done

echo ""
echo "=== Finding correct offset ==="
# At game frame 154:
#   - FCEUX wall frame = 195, uses NMI index ~187
#   - Interpreter wall frame = 187
# For interpreter frame 187 to use NMI 187, offset should be 0

# But wait, we need to check what buttons FCEUX has at game frame 154
echo "FCEUX at game frame 154 (wall frame 195):"
grep "^187 195" local/tas/nmi-filtered-inputs.txt || echo "(no exact match)"

echo ""
echo "What interpreter should use at game frame 154 (wall frame 187):"
echo "To get NMI 187, offset = 187 - 187 = 0"

# Let me verify: with offset 0
echo ""
echo "=== With offset 0 ==="
for if_ in 185 186 187 188 189 190; do
    idx=$((if_ - 0))  # offset 0
    line=$(grep "^$idx " local/tas/nmi-filtered-inputs.txt)
    echo "Interp frame $if_ -> NMI idx $idx -> $line"
done
