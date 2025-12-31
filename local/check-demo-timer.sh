#!/bin/bash
echo "=== DemoTimer (\$07a2) around transition ==="
echo "Frame  I_DT  F_DT  I_OM  F_OM  I_Joy  F_Joy"
for frame in 32 33 34 35 36 37 38 39 40 41 42 43 44; do
  i_dt=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x7a2)) "local/tas/interpreter-full-ram.bin" 2>/dev/null | awk '{print $1}')
  f_dt=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x7a2)) "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')
  i_om=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x770)) "local/tas/interpreter-full-ram.bin" 2>/dev/null | awk '{print $1}')
  f_om=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x770)) "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')
  i_joy=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x06fc)) "local/tas/interpreter-full-ram.bin" 2>/dev/null | awk '{print $1}')
  f_joy=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x06fc)) "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')

  printf "%5d  %4s  %4s  %4s  %4s  %5s  %5s\n" $frame "$i_dt" "$f_dt" "$i_om" "$f_om" "$i_joy" "$f_joy"
done
