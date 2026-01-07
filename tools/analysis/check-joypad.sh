#!/bin/bash
echo "SavedJoypad1Bits ($0016) and DemoTimer ($07a2)"
echo "Frame  I_Joy  F_Joy  I_Demo  F_Demo"
for frame in 36 37 38 39 40 41 42; do
  i_joy=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x16)) "local/tas/interpreter-full-ram.bin" 2>/dev/null | awk '{print $1}')
  f_joy=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x16)) "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')
  i_demo=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x7a2)) "local/tas/interpreter-full-ram.bin" 2>/dev/null | awk '{print $1}')
  f_demo=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x7a2)) "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')
  
  printf "%5d  %5d  %5d  %6d  %6d\n" $frame "$i_joy" "$f_joy" "$i_demo" "$f_demo"
done
