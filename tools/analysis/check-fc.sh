#!/bin/bash
echo "FCEUX FrameCounter and IntervalTimerControl progression:"
for frame in 0 1 2 3 4 5 6 7 8 9 10 15 20 50 100; do
  fc_offset=$((frame * 2048 + 9))
  int_offset=$((frame * 2048 + 1919))
  fc=$(xxd -s $fc_offset -l 1 local/tas/fceux-full-ram.bin 2>/dev/null | awk '{print $2}')
  intctrl=$(xxd -s $int_offset -l 1 local/tas/fceux-full-ram.bin 2>/dev/null | awk '{print $2}')
  printf "Frame %3d: FC=0x%s (%d), IntCtrl=0x%s (%d)\n" $frame "$fc" "0x$fc" "$intctrl" "0x$intctrl"
done
