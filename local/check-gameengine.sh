#!/bin/bash
echo "GameEngineSubroutine ($075F) comparison"
echo "Frame  Interp  FCEUX"
for frame in 36 37 38 39 40 41 42; do
  i_val=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x75f)) "local/tas/interpreter-full-ram.bin" 2>/dev/null | awk '{print $1}')
  f_val=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x75f)) "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')
  
  printf "%5d  %6d  %5d" $frame "$i_val" "$f_val"
  if [ "$i_val" != "$f_val" ]; then
    printf "  ← DIFF"
  fi
  printf "\n"
done
