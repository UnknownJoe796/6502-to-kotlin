#!/bin/bash
# General RAM dump analysis script
# Usage: ./local/analyze-ram-dumps.sh

fceux_file="local/tas/fceux-full-ram.bin"
interp_file="local/tas/interpreter-full-ram.bin"

# Function to read a byte from a RAM dump
read_byte() {
  local file=$1
  local frame=$2
  local addr=$3
  local offset=$((frame * 2048 + addr))
  od -An -t u1 -N 1 -j $offset "$file" 2>/dev/null | awk '{print $1}'
}

case "${1:-help}" in
  "find-fc")
    # Find frame where FC equals a specific value
    fc_target=$2
    system=${3:-interpreter}
    file="${interp_file}"
    if [ "$system" = "fceux" ]; then
      file="${fceux_file}"
    fi

    echo "Finding $system frame where FC=$fc_target"
    for frame in $(seq 0 100); do
      fc=$(read_byte "$file" $frame 0x09)
      mode=$(read_byte "$file" $frame 0x770)
      if [ "$fc" = "$fc_target" ]; then
        echo "Frame $frame: FC=$fc, OperMode=$mode"
      fi
    done
    ;;

  "compare-frames")
    # Compare two frames
    i_frame=$2
    f_frame=$3

    echo "Comparing Interpreter frame $i_frame vs FCEUX frame $f_frame"
    echo ""
    echo "Address    Name                    Interp    FCEUX"

    # Key addresses
    declare -A addrs=(
      [0x0009]="FrameCounter"
      [0x0770]="OperMode"
      [0x00FB]="JoypadBitMask"
      [0x0016]="SavedJoypad1Bits"
      [0x0086]="Player_X"
      [0x00CE]="Player_Y"
      [0x075C]="WorldNumber"
      [0x075D]="LevelNumber"
      [0x0700]="GamePauseStatus"
      [0x077F]="IntervalTimerControl"
    )

    for addr in $(echo "${!addrs[@]}" | tr ' ' '\n' | sort); do
      name="${addrs[$addr]}"
      addr_dec=$((addr))

      i_val=$(read_byte "$interp_file" $i_frame $addr_dec)
      f_val=$(read_byte "$fceux_file" $f_frame $addr_dec)

      if [ -n "$i_val" ] && [ -n "$f_val" ]; then
        printf "%-10s %-23s %9d %9d" "$addr" "$name" "$i_val" "$f_val"
        if [ "$i_val" != "$f_val" ]; then
          printf "  ← DIFF"
        fi
        printf "\n"
      fi
    done
    ;;

  "show-frames")
    # Show range of frames
    start=$2
    end=$3
    system=${4:-interpreter}
    file="${interp_file}"
    if [ "$system" = "fceux" ]; then
      file="${fceux_file}"
    fi

    echo "$system frames $start-$end"
    echo "Frame  FC(0x09)  OperMode(0x770)  JoypadMask(0xFB)"
    for frame in $(seq $start $end); do
      fc=$(read_byte "$file" $frame 0x09)
      mode=$(read_byte "$file" $frame 0x770)
      jmask=$(read_byte "$file" $frame 0xFB)

      if [ -n "$fc" ]; then
        printf "%5d  %8s  %15s  %16s\n" $frame "$fc" "$mode" "$jmask"
      fi
    done
    ;;

  *)
    echo "RAM dump analysis script"
    echo ""
    echo "Usage:"
    echo "  ./local/analyze-ram-dumps.sh find-fc FC_VALUE [system]"
    echo "    Find frame where FrameCounter equals FC_VALUE"
    echo "    system: interpreter (default) or fceux"
    echo ""
    echo "  ./local/analyze-ram-dumps.sh compare-frames INTERP_FRAME FCEUX_FRAME"
    echo "    Compare RAM state between two frames"
    echo ""
    echo "  ./local/analyze-ram-dumps.sh show-frames START END [system]"
    echo "    Show frame details for a range"
    echo ""
    echo "Examples:"
    echo "  ./local/analyze-ram-dumps.sh find-fc 34 interpreter"
    echo "  ./local/analyze-ram-dumps.sh compare-frames 37 41"
    echo "  ./local/analyze-ram-dumps.sh show-frames 35 45 fceux"
    ;;
esac
