#!/bin/bash
# Cleanup old project files

echo "Moving old markdown files to local/archive..."
mkdir -p local/archive

# Move old markdown planning/progress files
mv analyze-functions.md local/archive/ 2>/dev/null
mv ASSEMBLY-CONSTANTS-PLAN.md local/archive/ 2>/dev/null
mv EXPRESSION_DECOMPILER_RESULTS.md local/archive/ 2>/dev/null
mv FIBONACCI_VALIDATION.md local/archive/ 2>/dev/null
mv function-analysis-notes.md local/archive/ 2>/dev/null
mv IMPLEMENTATION-SUMMARY.md local/archive/ 2>/dev/null
mv PHASE5-PLAN.md local/archive/ 2>/dev/null
mv PHASE8-IMPLEMENTATION-STATUS.md local/archive/ 2>/dev/null
mv PHASE9-PLAN.md local/archive/ 2>/dev/null
mv PROGRESS_SUMMARY.md local/archive/ 2>/dev/null
mv SCALING_ROADMAP.md local/archive/ 2>/dev/null
mv TEST-SUITE-PROGRESS.md local/archive/ 2>/dev/null
mv VALIDATION_DEMO.md local/archive/ 2>/dev/null
mv VALIDATION_FRAMEWORK.md local/archive/ 2>/dev/null

# Remove old directories
echo "Removing old directories..."
rm -rf com/ 2>/dev/null
rm -rf decompiled/ 2>/dev/null
rm -rf docs/ 2>/dev/null
rm -rf META-INF/ 2>/dev/null

echo "Cleanup complete!"
echo "Archived files are in local/archive/"
