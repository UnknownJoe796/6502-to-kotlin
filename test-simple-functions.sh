#!/bin/bash
# Quick test script to validate simple functions without full Gradle build

echo "Testing simple SMB functions with fixed code generator..."
echo ""

# Try to run the quick validation test
echo "Attempting to run QuickDifferentialValidationTest..."
echo "This will test DoNothing2, ReadJoypads, and WritePPUReg1"
echo ""

# Check if gradlew exists
if [ ! -f "./gradlew" ]; then
    echo "Error: gradlew not found"
    exit 1
fi

# Try to run the test
./gradlew test --tests "QuickDifferentialValidationTest" --console=plain 2>&1 | tee test-output.txt

# Check result
if grep -q "BUILD SUCCESSFUL" test-output.txt; then
    echo ""
    echo "✓ Tests PASSED!"
    echo ""
    echo "Summary:"
    grep -A 10 "QuickDifferentialValidationTest" test-output.txt | grep "✓\|✗\|PASS\|FAIL" || echo "Check test-output.txt for details"
else
    echo ""
    echo "Tests failed or couldn't run. Checking for errors..."
    echo ""

    if grep -q "UnknownHostException\|Connection refused" test-output.txt; then
        echo "⚠ Network error - Gradle trying to download dependencies"
        echo "This is expected in offline environment"
    fi

    if grep -q "compilation error\|cannot find symbol" test-output.txt; then
        echo "⚠ Compilation error found"
        grep -A 3 "error:" test-output.txt | head -20
    fi

    echo ""
    echo "Full output saved to test-output.txt"
fi

echo ""
echo "Done."
