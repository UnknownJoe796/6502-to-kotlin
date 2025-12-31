#!/bin/bash
# Compare interpreter and FCEUX state at specific frames

INTERP_FRAME=$1
OFFSET=$2
FCEUX_FRAME=$((INTERP_FRAME + OFFSET))

echo "=== Frame Comparison ==="
echo "Interpreter frame: $INTERP_FRAME"
echo "FCEUX equivalent frame: $FCEUX_FRAME"
echo ""

# Extract from interpreter dump
echo "Interpreter state:"
grep "F${INTERP_FRAME} " local/tas/interpreter-state-dump.txt

# Calculate byte offset in FCEUX RAM
BYTE_OFFSET=$((FCEUX_FRAME * 2048))
echo ""
echo "FCEUX state (from RAM dump at offset $BYTE_OFFSET):"

# Read key bytes from FCEUX RAM
if [ -f "local/tas/fceux-full-ram.bin" ]; then
    # Read bytes at specific addresses
    PLAYER_X=$(od -An -tu1 -j $((BYTE_OFFSET + 0x86)) -N 1 local/tas/fceux-full-ram.bin | tr -d ' ')
    PLAYER_Y=$(od -An -tu1 -j $((BYTE_OFFSET + 0xCE)) -N 1 local/tas/fceux-full-ram.bin | tr -d ' ')
    PAGE=$(od -An -tu1 -j $((BYTE_OFFSET + 0x71A)) -N 1 local/tas/fceux-full-ram.bin | tr -d ' ')
    FC=$(od -An -tu1 -j $((BYTE_OFFSET + 0x09)) -N 1 local/tas/fceux-full-ram.bin | tr -d ' ')
    MODE=$(od -An -tu1 -j $((BYTE_OFFSET + 0x770)) -N 1 local/tas/fceux-full-ram.bin | tr -d ' ')
    WORLD=$(od -An -tu1 -j $((BYTE_OFFSET + 0x75F)) -N 1 local/tas/fceux-full-ram.bin | tr -d ' ')
    LEVEL=$(od -An -tu1 -j $((BYTE_OFFSET + 0x760)) -N 1 local/tas/fceux-full-ram.bin | tr -d ' ')
    LIVES=$(od -An -tu1 -j $((BYTE_OFFSET + 0x75A)) -N 1 local/tas/fceux-full-ram.bin | tr -d ' ')

    WORLD_DISP=$((WORLD + 1))
    LEVEL_DISP=$((LEVEL + 1))

    echo "  X=$PLAYER_X Y=$PLAYER_Y Page=$PAGE FC=$FC Mode=$MODE W=${WORLD_DISP}-${LEVEL_DISP} Lives=$LIVES"
else
    echo "  FCEUX RAM dump not found"
fi
