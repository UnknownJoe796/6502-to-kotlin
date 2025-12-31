#!/bin/bash
# Fix the 8-frame offset in nmi-filtered-inputs.txt
# The input values are shifted 8 positions too late
# This script shifts them 8 positions earlier

INPUT_FILE="local/tas/nmi-filtered-inputs.txt"
OUTPUT_FILE="local/tas/nmi-filtered-inputs-fixed.txt"

# Use awk to shift inputs 8 positions earlier
awk '
BEGIN {
    shift = 8
    count = 0
}
/^#/ {
    # Print header lines as-is
    print
    next
}
{
    # Store NMI index, frame, and input
    nmi[count] = $1
    frame[count] = $2
    input[count] = $3
    count++
}
END {
    # Output with shifted inputs
    for (i = 0; i < count; i++) {
        shifted = i + shift
        if (shifted < count) {
            new_input = input[shifted]
        } else {
            new_input = "0x00"
        }
        print nmi[i], frame[i], new_input
    }
}
' "$INPUT_FILE" > "$OUTPUT_FILE"

echo "Created $OUTPUT_FILE"
echo ""
echo "Verification - Original around frame 34:"
grep -E "^2[567] |^2[89] |^3[0-5] " "$INPUT_FILE"
echo ""
echo "Verification - Fixed around frame 34:"
grep -E "^2[567] |^2[89] |^3[0-5] " "$OUTPUT_FILE"
