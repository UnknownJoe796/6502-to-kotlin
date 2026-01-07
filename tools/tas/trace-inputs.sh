#!/bin/bash

# Check what inputs are in nmi-filtered-inputs.txt around key frames
echo "=== NMI-filtered inputs around gameplay start ==="
echo "(FCEUX NMI index -> buttons)"
head -50 local/tas/nmi-filtered-inputs.txt | tail -20

echo ""
echo "=== Key buttons ==="
echo "0x10 = Start"
echo "0x80 = Right"  
echo "0x01 = A (Jump)"
echo "0x40 = B (Run)"
echo ""

# Check what the TAS is doing around game frame 150-200
# Interpreter enters gameplay at frame 38, so game frame 150 = wall frame 188
# With offset 11, that reads input 177
echo "=== Inputs around game frame 150-200 (wall frame 188-238, input idx 177-227) ==="
sed -n '175,230p' local/tas/nmi-filtered-inputs.txt | head -30

echo ""
echo "=== What does FCEUX do at game frames 150-200? ==="
echo "(FCEUX enters gameplay at frame 41, so game frame 150 = wall frame 191)"
echo "Checking FCEUX buttons at frames 190-230..."
sed -n '190,230p' local/tas/nmi-filtered-inputs.txt | head -20
