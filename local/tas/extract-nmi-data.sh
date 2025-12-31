#!/bin/bash
# Extract NMI-related data efficiently using dd and xxd

RAMFILE="local/tas/fceux-full-ram.bin"
if [ ! -f "$RAMFILE" ]; then
    echo "Error: $RAMFILE not found"
    exit 1
fi

FILESIZE=$(stat -f%z "$RAMFILE" 2>/dev/null || stat -c%s "$RAMFILE" 2>/dev/null)
MAXFRAMES=$((FILESIZE / 2048))

echo "=== NMI Data Extraction ==="
echo "Total frames: $MAXFRAMES"
echo ""

# Key addresses (offsets within each 2KB frame):
# $077F = IntervalTimerControl (offset 0x77F = 1919)
# $0770 = OperMode (offset 0x770 = 1904)
# $0772 = OperMode_Task (offset 0x772 = 1906)
# $074E = AreaType (offset 0x74E = 1870)
# $075F = WorldNumber (offset 0x75F = 1887)
# $0760 = LevelNumber (offset 0x760 = 1888)
# $0009 = FrameCounter (offset 0x09 = 9)

echo "Frame,IntCtrl,Mode,Task,AreaType,World,Level,FC" > /tmp/nmi_data.csv

for frame in $(seq 0 $((MAXFRAMES - 1))); do
    base=$((frame * 2048))

    intctrl=$(xxd -s $((base + 0x77f)) -l 1 -p "$RAMFILE")
    mode=$(xxd -s $((base + 0x770)) -l 1 -p "$RAMFILE")
    task=$(xxd -s $((base + 0x772)) -l 1 -p "$RAMFILE")
    areatype=$(xxd -s $((base + 0x74e)) -l 1 -p "$RAMFILE")
    world=$(xxd -s $((base + 0x75f)) -l 1 -p "$RAMFILE")
    level=$(xxd -s $((base + 0x760)) -l 1 -p "$RAMFILE")
    fc=$(xxd -s $((base + 0x09)) -l 1 -p "$RAMFILE")

    echo "$frame,$intctrl,$mode,$task,$areatype,$world,$level,$fc"
done >> /tmp/nmi_data.csv

echo "Data saved to /tmp/nmi_data.csv"
echo ""
echo "Analyzing NMI skips..."

# Analyze with awk
awk -F',' '
NR==1 { next }  # Skip header
NR==2 {
    prev_intctrl = $2
    prev_mode = $3
    prev_task = $4
    prev_areatype = $5
    prev_world = $6
    prev_level = $7
    skip_start = -1
    skip_count = 0
    next
}
{
    frame = $1
    intctrl = $2
    mode = $3
    task = $4
    areatype = $5
    world = $6
    level = $7

    if (intctrl == prev_intctrl) {
        # NMI skipped
        if (skip_start == -1) {
            skip_start = frame - 1
            skip_mode = prev_mode
            skip_task = prev_task
            skip_areatype = prev_areatype
            skip_world = prev_world
            skip_level = prev_level
        }
        skip_count++
    } else {
        if (skip_start != -1) {
            # Skip ended - report
            changes = ""
            if (skip_mode != mode) changes = changes " Mode:" skip_mode "->" mode
            if (skip_task != task) changes = changes " Task:" skip_task "->" task
            if (skip_areatype != areatype) changes = changes " AreaType:" skip_areatype "->" areatype
            if (skip_world != world) changes = changes " World:" skip_world "->" world
            if (skip_level != level) changes = changes " Level:" skip_level "->" level

            print "Frame " skip_start ": " skip_count " NMI(s) skipped," changes

            skip_start = -1
            skip_count = 0
        }
    }

    prev_intctrl = intctrl
    prev_mode = mode
    prev_task = task
    prev_areatype = areatype
    prev_world = world
    prev_level = level
}
END {
    if (skip_start != -1) {
        print "Frame " skip_start ": " skip_count " NMI(s) skipped (ongoing at end)"
    }
}
' /tmp/nmi_data.csv
