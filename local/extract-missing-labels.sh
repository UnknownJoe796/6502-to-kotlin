#!/bin/bash
# Extract code labels (data table addresses) from assembly and print as constants

echo "// Additional code label constants (data table addresses)"
echo ""

# Find all code labels that are referenced but not in constants
grep "^[A-Za-z_][A-Za-z0-9_]*:" smbdism.asm | \
  sed 's/:.*//' | \
  while read label; do
    # Get the address of this label
    addr=$(grep -m1 "^$label:" smbdism.asm | awk '{print $1}' | tr -d ':' | grep "^[0-9a-fA-F]")
    if [ -n "$addr" ]; then
      upper=$(echo "$label" | tr '[:lower:]' '[:upper:]')
      echo "const val $upper = 0x$addr"
    fi
  done | sort | uniq
