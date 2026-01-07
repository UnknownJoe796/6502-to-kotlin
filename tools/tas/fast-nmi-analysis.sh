#!/bin/bash
# Fast NMI skip analysis - extracts key bytes efficiently

RAMFILE="local/tas/fceux-full-ram.bin"
if [ ! -f "$RAMFILE" ]; then
    echo "Error: $RAMFILE not found"
    exit 1
fi

FILESIZE=$(stat -f%z "$RAMFILE" 2>/dev/null || stat -c%s "$RAMFILE" 2>/dev/null)
MAXFRAMES=$((FILESIZE / 2048))

echo "=== Fast NMI Skip Analysis ==="
echo "Analyzing $MAXFRAMES frames"
echo ""

# Extract all IntCtrl values at once
echo "Extracting IntCtrl ($077F) for all frames..."
for frame in $(seq 0 $((MAXFRAMES - 1))); do
    base=$((frame * 2048))
    printf "%d " $(xxd -s $((base + 0x77f)) -l 1 -p "$RAMFILE")
done > /tmp/intctrl_values.txt

# Extract all OperMode values
echo "Extracting OperMode ($0770) for all frames..."
for frame in $(seq 0 $((MAXFRAMES - 1))); do
    base=$((frame * 2048))
    printf "%d " $(xxd -s $((base + 0x770)) -l 1 -p "$RAMFILE")
done > /tmp/mode_values.txt

# Extract all Task values
echo "Extracting Task ($0772) for all frames..."
for frame in $(seq 0 $((MAXFRAMES - 1))); do
    base=$((frame * 2048))
    printf "%d " $(xxd -s $((base + 0x772)) -l 1 -p "$RAMFILE")
done > /tmp/task_values.txt

# Extract AreaType values
echo "Extracting AreaType ($074E) for all frames..."
for frame in $(seq 0 $((MAXFRAMES - 1))); do
    base=$((frame * 2048))
    printf "%d " $(xxd -s $((base + 0x74e)) -l 1 -p "$RAMFILE")
done > /tmp/areatype_values.txt

echo ""
echo "Data extracted. Now analyzing NMI skips..."
echo ""

# Use awk to find consecutive same values in IntCtrl (meaning NMI was skipped)
cat /tmp/intctrl_values.txt | tr ' ' '\n' | awk '
NR==1 { prev=$1; next }
{
    if ($1 == prev && $1 != "") {
        print NR-1 ": IntCtrl stayed at " prev " (NMI skipped)"
    }
    prev = $1
}
'
