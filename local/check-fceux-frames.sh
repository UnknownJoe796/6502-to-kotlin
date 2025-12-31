#!/bin/bash
echo "FCEUX frames 38-44"
echo "Frame  FC(0x09)  OperMode(0x770)  JoypadMask(0xFB)  SavedJoy(0x16)"
for frame in 38 39 40 41 42 43 44; do
  offset=$((frame * 2048))
  fc=$(od -An -t u1 -N 1 -j $((offset + 0x09)) "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')
  mode=$(od -An -t u1 -N 1 -j $((offset + 0x770)) "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')
  jmask=$(od -An -t u1 -N 1 -j $((offset + 0xFB)) "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')
  sjoy=$(od -An -t u1 -N 1 -j $((offset + 0x16)) "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')
  
  if [ -n "$fc" ]; then
    printf "%5d  %8s  %15s  %16s  %16s\n" $frame "$fc" "$mode" "$jmask" "$sjoy"
  fi
done
