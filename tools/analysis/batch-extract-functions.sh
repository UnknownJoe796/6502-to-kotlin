#!/bin/bash
# Extract multiple simple functions

FUNCTIONS="IncSubtask IncModeTask_B SetFore ExitAFrenzy InitPlatScrl ExitVWalk SetVRAMCtrl SetPESub ResetTitle ChkPauseTimer"

for func in $FUNCTIONS; do
    echo "=== $func ==="
    grep -A 10 "^${func}:" outputs/smbdism-annotated.asm | sed '/^$/q' | grep -v "^$" | head -8
    echo ""
done
