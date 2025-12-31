#!/bin/bash
# Analyze the exact timing of Start button and OperMode transition

echo "=== INTERPRETER TIMELINE ==="
echo "Wall Frame | NMI Fired | FC | OperMode | DemoTimer | Notes"
echo "-----------|-----------|----|-----------|-----------|---------"

for frame in 36 37 38 39 40 41 42 43; do
  offset=$((frame * 2048))
  fc=$(od -An -t u1 -N 1 -j $((offset + 0x09)) "local/tas/interpreter-full-ram.bin" 2>/dev/null | awk '{print $1}')
  mode=$(od -An -t u1 -N 1 -j $((offset + 0x770)) "local/tas/interpreter-full-ram.bin" 2>/dev/null | awk '{print $1}')
  demo=$(od -An -t u1 -N 1 -j $((offset + 0x7a2)) "local/tas/interpreter-full-ram.bin" 2>/dev/null | awk '{print $1}')

  # Check NMI fired from frame index
  nmi=$(sed -n "$((frame+1))p" "local/tas/interpreter-frame-index.txt" | awk '{print $3}')

  # Calculate cumulative NMI count up to this frame
  nmi_count=$(head -$((frame+1)) "local/tas/interpreter-frame-index.txt" | grep " 1$" | wc -l | awk '{print $1}')

  notes=""
  if [ "$frame" = "38" ]; then
    notes="Start (0x10) applied at frame start"
  fi
  if [ "$mode" = "1" ] && [ "$frame" -gt 38 ]; then
    notes="OperMode TRANSITIONED"
  fi

  printf "%10d | %9s | %2s | %9s | %9s | %s\n" $frame "$nmi" "$fc" "$mode" "$demo" "$notes"
done

echo ""
echo "=== FCEUX TIMELINE ==="
echo "Wall Frame | NMI Fired | FC | OperMode | DemoTimer | NMI Index | Input"
echo "-----------|-----------|----|-----------|-----------|-----------|---------"

for frame in 36 37 38 39 40 41 42 43; do
  # FCEUX frame index starts at 1, not 0
  line=$((frame + 1))
  offset=$((frame * 2048))
  fc=$(od -An -t u1 -N 1 -j $((offset + 0x09)) "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')
  mode=$(od -An -t u1 -N 1 -j $((offset + 0x770)) "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')
  demo=$(od -An -t u1 -N 1 -j $((offset + 0x7a2)) "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')

  # Check NMI fired
  nmi=$(sed -n "${line}p" "local/tas/fceux-frame-index.txt" | awk '{print $3}')

  # Calculate cumulative NMI count
  nmi_count=$(head -$line "local/tas/fceux-frame-index.txt" | grep " 1$" | wc -l | awk '{print $1}')

  # Get input from nmi-filtered-inputs (NMI index = nmi_count - 1)
  input_line=$((nmi_count))
  input=$(sed -n "${input_line}p" "local/tas/nmi-filtered-inputs.txt" | awk '{print $3}')
  [ -z "$input" ] && input="N/A"

  printf "%10d | %9s | %2s | %9s | %9s | %9d | %s\n" $frame "$nmi" "$fc" "$mode" "$demo" $((nmi_count - 1)) "$input"
done

echo ""
echo "Key observations:"
echo "- Interpreter applies Start (0x10) at frame 38 (when nmiInputIndex=35)"
echo "- FCEUX OperMode transitions at frame 41"
echo "- Need to find when Start is actually processed in FCEUX"
