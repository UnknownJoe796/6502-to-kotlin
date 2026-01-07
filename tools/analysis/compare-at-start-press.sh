#!/bin/bash
echo "RAM comparison at Start button press"
echo "Interpreter frame 38 vs FCEUX frame 42"
echo ""
echo "Address    Name                    Interp    FCEUX"

# Key addresses to check
addrs=(
  "0x0009:FrameCounter"
  "0x0770:OperMode"
  "0x00FB:JoypadBitMask"
  "0x0016:SavedJoypad1Bits"
  "0x0086:Player_X"
  "0x00CE:Player_Y"
  "0x075C:WorldNumber"
  "0x075D:LevelNumber"
)

for addr_name in "${addrs[@]}"; do
  addr="${addr_name%%:*}"
  name="${addr_name##*:}"
  addr_dec=$((addr))
  
  i_offset=$((38 * 2048 + addr_dec))
  f_offset=$((42 * 2048 + addr_dec))
  
  i_val=$(od -An -t u1 -N 1 -j $i_offset "local/tas/interpreter-full-ram.bin" 2>/dev/null | awk '{print $1}')
  f_val=$(od -An -t u1 -N 1 -j $f_offset "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')
  
  if [ -n "$i_val" ] && [ -n "$f_val" ]; then
    printf "%-10s %-23s %9d %9d" "$addr" "$name" "$i_val" "$f_val"
    if [ "$i_val" != "$f_val" ]; then
      printf "  ← DIFF"
    fi
    printf "\n"
  fi
done
