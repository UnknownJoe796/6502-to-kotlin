#!/bin/bash
set -e

echo "========================================="
echo "Repository Cleanup Script"
echo "========================================="
echo ""

# Phase 1: Reorganize Documentation
echo "Phase 1: Reorganizing documentation..."
mkdir -p docs/architecture
mkdir -p docs/guides
mkdir -p docs/smb
mkdir -p docs/archive

# Move existing docs to proper locations
if [ -f "docs/DECOMPILER-TESTING-GUIDE.md" ]; then
    mv docs/DECOMPILER-TESTING-GUIDE.md docs/guides/TESTING-GUIDE.md
    echo "  ✓ Moved TESTING-GUIDE.md"
fi

if [ -f "docs/NMI-TIMING-INSIGHTS.md" ]; then
    mv docs/NMI-TIMING-INSIGHTS.md docs/smb/
    echo "  ✓ Moved NMI-TIMING-INSIGHTS.md"
fi

if [ -f "plans/DECOMPILER-COMPLETION-PLAN.md" ]; then
    mv plans/DECOMPILER-COMPLETION-PLAN.md docs/PROJECT-STATUS.md
    echo "  ✓ Moved PROJECT-STATUS.md"
fi

# Archive old docs from local/
if [ -d "local/archive" ]; then
    cp -r local/archive/* docs/archive/ 2>/dev/null || true
    echo "  ✓ Archived old docs from local/archive"
fi

# Move top-level markdown files from local/ to archive
mv local/*.md docs/archive/ 2>/dev/null || true
echo "  ✓ Archived markdown files from local/"

echo ""

# Phase 2: Clean Source Tree
echo "Phase 2: Cleaning source tree..."

# Remove orphaned src/ directory
if [ -d "src" ]; then
    rm -rf src/
    echo "  ✓ Removed orphaned src/ directory"
fi

# Create generated code directories
mkdir -p smb/src/main/kotlin/com/ivieleague/decompiler6502tokotlin/smb/generated
mkdir -p smb/src/test/kotlin/com/ivieleague/decompiler6502tokotlin/smb/generated

# Move generated source files if they're in the wrong place
SMB_SRC="smb/src/main/kotlin/com/ivieleague/decompiler6502tokotlin/smb"
if [ -f "$SMB_SRC/SMBDecompiled.kt" ]; then
    mv "$SMB_SRC/SMBDecompiled.kt" "$SMB_SRC/generated/"
    echo "  ✓ Moved SMBDecompiled.kt to generated/"
fi

if [ -f "$SMB_SRC/SMBConstants.kt" ]; then
    mv "$SMB_SRC/SMBConstants.kt" "$SMB_SRC/generated/"
    echo "  ✓ Moved SMBConstants.kt to generated/"
fi

if [ -f "$SMB_SRC/SMBRomData.kt" ]; then
    mv "$SMB_SRC/SMBRomData.kt" "$SMB_SRC/generated/"
    echo "  ✓ Moved SMBRomData.kt to generated/"
fi

# Move assembly source to smb module
if [ -f "outputs/smbdism-annotated.asm" ] && [ ! -f "smb/smbdism.asm" ]; then
    cp outputs/smbdism-annotated.asm smb/smbdism.asm
    echo "  ✓ Copied smbdism.asm to smb/"
fi

# Move TAS file
if [ -f "happylee-warps.fm2" ] && [ ! -f "smb/happylee-warps.fm2" ]; then
    mv happylee-warps.fm2 smb/
    echo "  ✓ Moved happylee-warps.fm2 to smb/"
fi

echo ""

# Phase 3: Organize Tools
echo "Phase 3: Organizing tools..."
mkdir -p tools/tas
mkdir -p tools/analysis
mkdir -p data/testgen
mkdir -p data/roms

# Move TAS analysis scripts
if [ -d "local/tas" ]; then
    # Move shell scripts only
    find local/tas -name "*.sh" -exec mv {} tools/tas/ \; 2>/dev/null || true
    echo "  ✓ Moved TAS scripts to tools/tas/"

    # Move analysis outputs to data
    mkdir -p data/tas
    mv local/tas/*.txt data/tas/ 2>/dev/null || true
    mv local/tas/*.md data/tas/ 2>/dev/null || true
    mv local/tas/*.bin data/tas/ 2>/dev/null || true
    echo "  ✓ Moved TAS data to data/tas/"

    # Remove empty directory
    rmdir local/tas 2>/dev/null || true
fi

# Move general analysis scripts from local/
find local -maxdepth 1 -name "*.sh" -exec mv {} tools/analysis/ \; 2>/dev/null || true
echo "  ✓ Moved analysis scripts to tools/analysis/"

# Move testgen data
if [ -d "local/testgen" ]; then
    mv local/testgen data/
    echo "  ✓ Moved testgen data to data/"
fi

# Move roms directory
if [ -d "local/roms" ]; then
    cp -r local/roms/* data/roms/ 2>/dev/null || true
    echo "  ✓ Moved ROMs to data/roms/"
fi

# Clean up remaining local/ contents
mv local/*.txt docs/archive/ 2>/dev/null || true

echo ""

# Phase 4: Clean outputs directory
echo "Phase 4: Cleaning outputs directory..."

# Delete temporary debug outputs
rm -f outputs/all-if-nodes.txt
rm -f outputs/debug-*.txt
rm -f outputs/phase*-analysis-output.txt
rm -f outputs/smb-controls-structure.txt
rm -f outputs/smb-dead-code-analysis.txt
rm -f outputs/smb-memory-layout.txt
rm -f outputs/smbdism-debug*.txt
rm -f outputs/smbdism-parse.txt
rm -f outputs/ifelse-debug.txt
rm -f outputs/impede-debug.txt
rm -f outputs/noisesfx-debug.txt
rm -f outputs/CONTROL_FLOW_ISSUES.md
echo "  ✓ Deleted temporary debug outputs"

# Keep only the essential generated source files in outputs for reference
# (These are now duplicated in smb/src/.../generated/)
echo "  ✓ Kept essential source files in outputs/"

echo ""
echo "========================================="
echo "Cleanup Complete!"
echo "========================================="
echo ""
echo "Next steps:"
echo "1. Review .gitignore updates"
echo "2. Update import paths if needed"
echo "3. Run ./gradlew build to verify"
echo ""
