#!/bin/bash
echo "=== SavedJoypad1Bits at early frames ==="
echo "Frame  I_Joy  F_Joy"
for frame in 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42; do
  i_joy=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x06fc)) "local/tas/interpreter-full-ram.bin" 2>/dev/null | awk '{print $1}')
  f_joy=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x06fc)) "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')

  printf "%5d  %5s  %5s\n" $frame "$i_joy" "$f_joy"
done
