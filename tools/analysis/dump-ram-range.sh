#!/bin/bash
# Dump a range of RAM addresses for comparison
interp_frame=$1
fceux_frame=$2
start_addr=$3
end_addr=$4

echo "RAM dump comparison:"
echo "Interpreter frame $interp_frame vs FCEUX frame $fceux_frame"
echo "Address range: 0x$(printf %04x $start_addr) - 0x$(printf %04x $end_addr)"
echo ""

for addr in $(seq $start_addr $end_addr); do
  i_offset=$((interp_frame * 2048 + addr))
  f_offset=$((fceux_frame * 2048 + addr))
  
  i_val=$(od -An -t u1 -N 1 -j $i_offset "local/tas/interpreter-full-ram.bin" 2>/dev/null | awk '{print $1}')
  f_val=$(od -An -t u1 -N 1 -j $f_offset "local/tas/fceux-full-ram.bin" 2>/dev/null | awk '{print $1}')
  
  if [ -n "$i_val" ] && [ -n "$f_val" ] && [ "$i_val" != "$f_val" ]; then
    printf "0x%04x: interp=%3d (0x%02x)  fceux=%3d (0x%02x)\n" $addr $i_val $i_val $f_val $f_val
  fi
done
