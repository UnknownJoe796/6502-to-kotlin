#!/bin/bash
# Extract a function from assembly
FUNC=$1
grep -A 30 "^${FUNC}:" outputs/smbdism-annotated.asm | sed '/^$/q' | head -20
