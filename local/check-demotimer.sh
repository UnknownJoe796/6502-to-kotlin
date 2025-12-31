#!/bin/bash
echo "DemoTimer ($07a2) comparison"
echo "Frame  Interp  FCEUX"
for frame in 35 36 37 38 39 40 41 42 43 44 45; do
  i_offset=$((frame * 2048 + 0x7a2))
  f_offset=$((frame * 2048 + 0x7a2))
  
  i_val=$(od -An -t u1 -N 1 -j $i_offset "local/tas/interpreter-full-ram.bin" 2>/dev/null | awk '{print $1}')
  f_val=$(od -An -t u1 -N 1 -j $f_offset "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')
  
  if [ -n "$i_val" ] && [ -n "$f_val" ]; then
    printf "%5d  %6d  %5d" $frame "$i_val" "$f_val"
    if [ "$i_val" != "$f_val" ]; then
      printf "  ← DIFF"
    fi
    printf "\n"
  fi
done
