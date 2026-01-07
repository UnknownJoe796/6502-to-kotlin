#!/bin/bash
echo "=== SavedJoypad1Bits (\$0016) and controller state ==="
echo "Frame  I_Joy  F_Joy  I_Start  F_Start"
for frame in 30 31 32 33 34 35 36 37 38 39 40 41 42 43; do
  i_joy=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x16)) "local/tas/interpreter-full-ram.bin" 2>/dev/null | awk '{print $1}')
  f_joy=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x16)) "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')

  # Check if bit 4 (Start = 0x10) is set
  i_start="N"
  f_start="N"
  [ "$((i_joy & 16))" -ne 0 ] && i_start="Y"
  [ "$((f_joy & 16))" -ne 0 ] && f_start="Y"

  printf "%5d  %5s  %5s  %7s  %7s" $frame "$i_joy" "$f_joy" "$i_start" "$f_start"
  if [ "$i_joy" != "$f_joy" ]; then
    printf "  ← DIFF"
  fi
  printf "\n"
done
