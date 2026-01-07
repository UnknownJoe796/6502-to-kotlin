#!/bin/bash
# Find functions with 5-20 instructions that might be good validation candidates

awk '
BEGIN { 
    in_function = 0
    function_name = ""
    instruction_count = 0
}

/^[A-Z][A-Za-z0-9_]+:/ {
    # New label found
    if (in_function && instruction_count >= 5 && instruction_count <= 20) {
        print function_name ":" instruction_count
    }
    function_name = $1
    gsub(/:/, "", function_name)
    in_function = 1
    instruction_count = 0
    next
}

in_function {
    # Skip empty lines and comments
    if ($0 ~ /^[[:space:]]*$/) next
    if ($0 ~ /^[[:space:]]*;/) next
    if ($0 ~ /^DATA:/) next
    if ($0 ~ /^-----/) next
    
    # Count instruction
    instruction_count++
    
    # Check for RTS (end of function)
    if ($0 ~ /RTS/) {
        if (instruction_count >= 5 && instruction_count <= 20) {
            print function_name ":" instruction_count
        }
        in_function = 0
    }
}
' outputs/smbdism-annotated.asm | sort -t: -k2 -n
