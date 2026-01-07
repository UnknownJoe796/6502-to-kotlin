#!/bin/bash
echo "=== OperMode ($0770) comparison ==="
echo "Frame  Interp  FCEUX"
for frame in 38 39 40 41 42 43 44 45 46 47 48 49 50; do
  offset=$((frame * 2048 + 0x770))
  i_val=$(od -An -t u1 -N 1 -j $offset "local/tas/interpreter-full-ram.bin" 2>/dev/null | awk '{print $1}')
  f_val=$(od -An -t u1 -N 1 -j $offset "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')

  printf "%5d  %6s  %5s" $frame "$i_val" "$f_val"
  if [ "$i_val" != "$f_val" ]; then
    printf "  ← DIFF"
  fi
  printf "\n"
done
