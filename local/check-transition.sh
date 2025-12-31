#!/bin/bash
echo "=== Detailed transition timing ==="
echo "Frame  I_OM  F_OM  I_FC  F_FC  Notes"
for frame in 35 36 37 38 39 40 41 42 43 44 45 46; do
  i_om=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x770)) "local/tas/interpreter-full-ram.bin" 2>/dev/null | awk '{print $1}')
  f_om=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x770)) "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')
  i_fc=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x09)) "local/tas/interpreter-full-ram.bin" 2>/dev/null | awk '{print $1}')
  f_fc=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x09)) "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')

  notes=""
  [ "$i_om" != "$f_om" ] && notes="OperMode DIFF"
  [ "$i_fc" != "$f_fc" ] && notes="$notes FC_DIFF"

  printf "%5d  %4s  %4s  %4s  %4s  %s\n" $frame "$i_om" "$f_om" "$i_fc" "$f_fc" "$notes"
done
