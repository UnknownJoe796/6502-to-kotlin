#!/bin/bash
# Analyze FCEUX RAM state around the warp transition (frame 7700-7800)

RAM_DUMP="local/tas/fceux-full-ram.bin"

# Key addresses for warp detection
# WorldNumber = $075F
# LevelNumber = $075C (area type: 0=water, 1=ground, 2=underground, 3=castle)
# AreaNumber = $0750
# AreaPointer = $0750
# Player_State = $001D (0=on ground, 1=climbing, 2=in air, etc)
# WarpZoneControl = $06D6
# EnterSidePipeCollision = ?
# OperMode = $0770 (0=demo, 1=title, 2=gameplay)
# OperMode_Task = $0772

for frame in $(seq 7700 7750); do
    offset=$((frame * 2048))

    if [ $offset -lt $(stat -f%z "$RAM_DUMP") ]; then
        # Extract values at key addresses
        world=$(xxd -s $((offset + 0x075F)) -l 1 -p "$RAM_DUMP")
        level=$(xxd -s $((offset + 0x075C)) -l 1 -p "$RAM_DUMP")
        area=$(xxd -s $((offset + 0x0750)) -l 1 -p "$RAM_DUMP")
        playerX=$(xxd -s $((offset + 0x0086)) -l 1 -p "$RAM_DUMP")
        playerY=$(xxd -s $((offset + 0x00CE)) -l 1 -p "$RAM_DUMP")
        playerState=$(xxd -s $((offset + 0x001D)) -l 1 -p "$RAM_DUMP")
        warpZone=$(xxd -s $((offset + 0x06D6)) -l 1 -p "$RAM_DUMP")
        operMode=$(xxd -s $((offset + 0x0770)) -l 1 -p "$RAM_DUMP")
        modeTask=$(xxd -s $((offset + 0x0772)) -l 1 -p "$RAM_DUMP")
        scrollLock=$(xxd -s $((offset + 0x0723)) -l 1 -p "$RAM_DUMP")
        pageScroll=$(xxd -s $((offset + 0x071A)) -l 1 -p "$RAM_DUMP")
        fc=$(xxd -s $((offset + 0x0009)) -l 1 -p "$RAM_DUMP")

        # Player page (horizontal screen position indicator)
        playerPage=$(xxd -s $((offset + 0x006D)) -l 1 -p "$RAM_DUMP")

        echo "Frame $frame: W$(printf "%d" 0x$world)-$(printf "%d" 0x$level) Area=$area X=$playerX($playerPage) Y=$playerY State=$playerState WarpZ=$warpZone Mode=$operMode Task=$modeTask FC=$fc"
    fi
done
