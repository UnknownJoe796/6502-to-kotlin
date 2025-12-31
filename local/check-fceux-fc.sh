#!/bin/bash
# Check FrameCounter values in FCEUX RAM dump
file="local/tas/fceux-full-ram.bin"
echo "Frame  FC(0x09) OperMode(0x770)"
for frame in 40 41 42 43 44 45 46 47 48 49 50; do
  offset=$((frame * 2048))
  fc=$(od -An -t u1 -N 1 -j $((offset + 0x09)) "$file" | awk '{print $1}')
  mode=$(od -An -t u1 -N 1 -j $((offset + 0x770)) "$file" | awk '{print $1}')
  printf "%5d  %8d  %13d\n" $frame $fc $mode
done
