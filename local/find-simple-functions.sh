#!/bin/bash
# Find simple SMB functions suitable for validation testing
# Criteria: short (< 15 instructions), ends with RTS, no complex branches

echo "Finding simple SMB functions..."
echo ""

awk '
/^[A-Z][A-Za-z0-9_]*:/ {
    if (func_name != "" && instr_count > 0 && instr_count <= 15 && has_rts) {
        print func_name ":" instr_count
    }
    func_name = $1
    gsub(/:/, "", func_name)
    instr_count = 0
    has_rts = 0
    next
}
/^[A-Z]{3}/ {
    if (func_name != "") {
        instr_count++
        if ($1 == "RTS") has_rts = 1
    }
}
END {
    if (func_name != "" && instr_count > 0 && instr_count <= 15 && has_rts) {
        print func_name ":" instr_count
    }
}
' outputs/smbdism-annotated.asm | head -50
