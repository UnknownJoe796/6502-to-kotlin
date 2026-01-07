#!/bin/bash
cd "$(dirname "$0")/.."

# Run test and extract system-out from XML
./gradlew test --tests "DecompilerValidationTest.showFibonacciGeneratedCode" > /dev/null 2>&1

# Extract and display the output from test results
cat build/test-results/test/TEST-com.ivieleague.decompiler6502tokotlin.validation.DecompilerValidationTest.xml | \
    sed -n '/<system-out>/,/<\/system-out>/p' | \
    sed 's/<system-out><!\[CDATA\[//' | \
    sed 's/\]\]><\/system-out>//' | \
    head -200
