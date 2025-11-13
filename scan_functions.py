#!/usr/bin/env python3
"""
Quick scanner to identify SMB functions for differential testing.
"""

import re
from collections import defaultdict

def scan_smb_functions():
    """Scan smbdism.asm and identify all testable functions."""

    with open('smbdism.asm', 'r') as f:
        lines = f.readlines()

    # Find all labels
    label_pattern = re.compile(r'^([A-Za-z_][A-Za-z0-9_]*):', re.MULTILINE)
    jsr_pattern = re.compile(r'\bjsr\s+([A-Za-z_][A-Za-z0-9_]*)')

    labels = {}  # label_name -> line_number
    jsr_targets = set()  # set of function names called with JSR

    # First pass: find all labels and JSR targets
    for i, line in enumerate(lines):
        # Find labels
        match = label_pattern.match(line)
        if match:
            label_name = match.group(1)
            labels[label_name] = i

        # Find JSR targets
        for match in jsr_pattern.finditer(line):
            jsr_targets.add(match.group(1))

    print(f"Total labels: {len(labels)}")
    print(f"Unique JSR targets: {len(jsr_targets)}")
    print()

    # Second pass: analyze each label to see if it's a function
    functions = {}  # label_name -> function_info

    for label_name, start_line in labels.items():
        # Skip if not a JSR target (not a true function)
        if label_name not in jsr_targets:
            continue

        # Scan forward to find RTS and collect info
        current_line = start_line + 1
        has_rts = False
        calls_made = []
        end_line = start_line
        code_lines = 0

        while current_line < len(lines):
            line = lines[current_line].strip()

            # Skip empty lines and comments
            if not line or line.startswith(';'):
                current_line += 1
                continue

            # Hit another label? Stop
            if label_pattern.match(line):
                break

            # Count code lines (instructions)
            if re.match(r'^\s*[a-z]{3}(\s|$)', line):
                code_lines += 1

            end_line = current_line

            # Found RTS?
            if 'rts' in line.lower():
                has_rts = True
                break

            # Found JSR?
            for match in jsr_pattern.finditer(line):
                calls_made.append(match.group(1))

            current_line += 1

        # Only include if it has RTS (is a proper function)
        if has_rts and code_lines > 0:
            functions[label_name] = {
                'start_line': start_line,
                'end_line': end_line,
                'code_lines': code_lines,
                'calls_made': calls_made,
                'is_leaf': len(calls_made) == 0,
                'is_jump_engine': label_name == 'JumpEngine'
            }

    return functions

def main():
    functions = scan_smb_functions()

    # Filter for testable functions
    testable = {name: info for name, info in functions.items()
                if not info['is_jump_engine']}

    leaf_functions = {name: info for name, info in testable.items()
                      if info['is_leaf']}

    non_leaf_functions = {name: info for name, info in testable.items()
                          if not info['is_leaf']}

    print("=" * 80)
    print("SMB Function Analysis")
    print("=" * 80)
    print(f"Total functions (JSR targets with RTS): {len(functions)}")
    print(f"Testable functions: {len(testable)}")
    print(f"  Leaf functions (no JSR calls): {len(leaf_functions)}")
    print(f"  Non-leaf functions (make JSR calls): {len(non_leaf_functions)}")
    print()

    # Show leaf functions
    print("-" * 80)
    print(f"LEAF FUNCTIONS ({len(leaf_functions)}):")
    print("-" * 80)
    for name in sorted(leaf_functions.keys()):
        info = leaf_functions[name]
        print(f"  {name:30s} (line {info['start_line']:5d}, {info['code_lines']:3d} LOC)")

    print()
    print("-" * 80)
    print(f"NON-LEAF FUNCTIONS ({len(non_leaf_functions)}) - First 30:")
    print("-" * 80)
    for name in sorted(non_leaf_functions.keys())[:30]:
        info = non_leaf_functions[name]
        calls = ', '.join(info['calls_made'][:3])
        if len(info['calls_made']) > 3:
            calls += f" ... +{len(info['calls_made'])-3} more"
        print(f"  {name:30s} (line {info['start_line']:5d}, {info['code_lines']:3d} LOC) -> {calls}")

    # Save to file for later use
    with open('smb_functions.txt', 'w') as f:
        f.write("# SMB Functions\n\n")
        f.write(f"## Leaf Functions ({len(leaf_functions)})\n")
        for name in sorted(leaf_functions.keys()):
            f.write(f"{name}\n")

        f.write(f"\n## Non-Leaf Functions ({len(non_leaf_functions)})\n")
        for name in sorted(non_leaf_functions.keys()):
            info = non_leaf_functions[name]
            calls = ','.join(info['calls_made'])
            f.write(f"{name} -> {calls}\n")

    print()
    print("Results saved to smb_functions.txt")

if __name__ == '__main__':
    main()
