#!/bin/bash
echo "=== Game state comparison (gameplay frames 50-100) ==="
echo "Frame  I_FC  F_FC  I_X  F_X  I_Page F_Page  Diff"
for frame in 50 60 70 80 90 100 120 150 200 250 300; do
  # Frame Counter
  i_fc=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x09)) "local/tas/interpreter-full-ram.bin" 2>/dev/null | awk '{print $1}')
  f_fc=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x09)) "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')

  # Player X position
  i_x=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x86)) "local/tas/interpreter-full-ram.bin" 2>/dev/null | awk '{print $1}')
  f_x=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x86)) "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')

  # Screen page
  i_page=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x71a)) "local/tas/interpreter-full-ram.bin" 2>/dev/null | awk '{print $1}')
  f_page=$(od -An -t u1 -N 1 -j $((frame * 2048 + 0x71a)) "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')

  diff=""
  [ "$i_fc" != "$f_fc" ] && diff="${diff}FC "
  [ "$i_x" != "$f_x" ] && diff="${diff}X "
  [ "$i_page" != "$f_page" ] && diff="${diff}Page "

  printf "%5d  %4s  %4s  %3s  %3s  %6s %6s  %s\n" \
    $frame "$i_fc" "$f_fc" "$i_x" "$f_x" "$i_page" "$f_page" "$diff"
done
