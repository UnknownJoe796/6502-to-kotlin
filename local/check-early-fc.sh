#!/bin/bash
echo "=== FrameCounter at early frames ==="
echo "Frame  I_FC  F_FC  Diff"
for frame in 3 4 5 6 7 8 9 10 11 12 13 14 15 20 25 30 35 40 45 50; do
  i_fc=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x09)) "local/tas/interpreter-full-ram.bin" 2>/dev/null | awk '{print $1}')
  f_fc=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x09)) "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')

  if [ -n "$i_fc" ] && [ -n "$f_fc" ]; then
    diff=$((i_fc - f_fc))
    printf "%5d  %4s  %4s  %+d\n" $frame "$i_fc" "$f_fc" $diff
  fi
done
