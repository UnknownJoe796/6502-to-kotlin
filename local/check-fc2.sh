#!/bin/bash
# Check FrameCounter values in interpreter RAM dump
file="local/tas/interpreter-full-ram.bin"
echo "Frame  FC(0x09) OperMode(0x770)"
for frame in 0 1 2 3 4 5 10 20 30 40 50 100 200 300; do
  offset=$((frame * 2048))
  fc=$(od -An -t u1 -N 1 -j $((offset + 0x09)) "$file" 2>/dev/null | awk '{print $1}')
  mode=$(od -An -t u1 -N 1 -j $((offset + 0x770)) "$file" 2>/dev/null | awk '{print $1}')
  if [ -n "$fc" ]; then
    printf "%5d  %8d  %13d\n" $frame $fc $mode
  fi
done
