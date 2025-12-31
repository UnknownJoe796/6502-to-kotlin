#!/bin/bash
echo "FCEUX vs Interpreter FC comparison"
echo "Frame  FCEUX_FC  FCEUX_Mode  Interp_FC  Interp_Mode"
for frame in 35 36 37 38 39 40 41 42 43 44 45; do
  f_offset=$((frame * 2048))
  i_offset=$((frame * 2048))
  
  f_fc=$(od -An -t u1 -N 1 -j $((f_offset + 0x09)) "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')
  f_mode=$(od -An -t u1 -N 1 -j $((f_offset + 0x770)) "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')
  
  i_fc=$(od -An -t u1 -N 1 -j $((i_offset + 0x09)) "local/tas/interpreter-full-ram.bin" 2>/dev/null | awk '{print $1}')
  i_mode=$(od -An -t u1 -N 1 -j $((i_offset + 0x770)) "local/tas/interpreter-full-ram.bin" 2>/dev/null | awk '{print $1}')
  
  if [ -n "$f_fc" ] && [ -n "$i_fc" ]; then
    printf "%5d  %8s  %11s  %9s  %11s\n" $frame "$f_fc" "$f_mode" "$i_fc" "$i_mode"
  fi
done
