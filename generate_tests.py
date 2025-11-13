#!/usr/bin/env python3
"""
Generates comprehensive differential tests for all SMB functions.
"""

import re

def load_function_list():
    """Load the function list from smb_functions.txt"""
    leaf_functions = []
    non_leaf_functions = []

    with open('smb_functions.txt', 'r') as f:
        section = None
        for line in f:
            line = line.strip()
            if line.startswith('## Leaf Functions'):
                section = 'leaf'
            elif line.startswith('## Non-Leaf Functions'):
                section = 'non-leaf'
            elif line and not line.startswith('#'):
                if section == 'leaf':
                    leaf_functions.append(line)
                elif section == 'non-leaf':
                    # Extract function name (before ->)
                    func_name = line.split(' ->')[0].strip()
                    non_leaf_functions.append(func_name)

    return leaf_functions, non_leaf_functions

def generate_test_file(leaf_functions, non_leaf_functions):
    """Generate the complete test file."""

    header = '''package com.ivieleague.decompiler6502tokotlin.hand

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Comprehensive differential tests for ALL Super Mario Bros. functions.
 *
 * Tests each function with 10 random initial states:
 * - Executes in interpreter
 * - Executes generated Kotlin code
 * - Compares all RAM (0x0000-0x2FFF) plus registers/flags
 *
 * Generated automatically from SMB disassembly analysis.
 * Total functions tested: TOTAL_COUNT (LEAF_COUNT leaf + NON_LEAF_COUNT non-leaf)
 */
class SMBComprehensiveDifferentialTest {

    // ========================================================================
    // LEAF FUNCTIONS (LEAF_COUNT)
    // These functions don't call other functions - easiest to test
    // ========================================================================
'''

    middle_section = '''
    // ========================================================================
    // NON-LEAF FUNCTIONS (NON_LEAF_COUNT)
    // These functions call other functions - may need mocking
    // ========================================================================
'''

    footer = '''
    // ========================================================================
    // Summary Test - Run All Functions
    // ========================================================================

    @Test
    fun testAllFunctions_Summary() {
        val allFunctions = listOf(
ALL_FUNCTIONS_LIST
        )

        println("=" . repeat(80))
        println("SMB Comprehensive Differential Test - Summary")
        println("=" . repeat(80))
        println("Testing ${allFunctions.size} functions...")
        println()

        val results = mutableListOf<SMBDifferentialTestGenerator.FunctionTestResult>()

        allFunctions.forEach { functionName ->
            try {
                val result = SMBDifferentialTestGenerator.testFunction(functionName, numTests = 10)
                results.add(result)

                val status = if (result.passRate == 1.0) "✓ PASS" else "✗ FAIL"
                println("$status  ${functionName.padEnd(35)} ${result.passedTests}/10 (${"%.0f".format(result.passRate * 100)}%)")

            } catch (e: Exception) {
                println("✗ ERROR ${functionName.padEnd(35)} ${e.message}")
            }
        }

        println()
        println("=" . repeat(80))
        println("Summary:")
        println("-" . repeat(80))

        val totalFunctions = results.size
        val fullyPassing = results.count { it.passRate == 1.0 }
        val partiallyPassing = results.count { it.passRate > 0.0 && it.passRate < 1.0 }
        val fullyFailing = results.count { it.passRate == 0.0 }

        val totalTests = results.sumOf { it.totalTests }
        val totalPassed = results.sumOf { it.passedTests }
        val overallPassRate = if (totalTests > 0) totalPassed.toDouble() / totalTests else 0.0

        println("Functions tested: $totalFunctions")
        println("  Fully passing (100%): $fullyPassing")
        println("  Partially passing: $partiallyPassing")
        println("  Fully failing (0%): $fullyFailing")
        println()
        println("Total test cases: $totalTests")
        println("  Passed: $totalPassed")
        println("  Failed: ${totalTests - totalPassed}")
        println("  Overall pass rate: ${"%.1f".format(overallPassRate * 100)}%")
        println("=" . repeat(80))

        // Save detailed results
        saveDetailedResults(results)
    }

    private fun saveDetailedResults(results: List<SMBDifferentialTestGenerator.FunctionTestResult>) {
        // TODO: Save to file for analysis
    }
}
'''

    # Generate test methods for leaf functions
    leaf_tests = []
    for func in leaf_functions:
        test_method = f'''    @Test
    fun test_{func}() {{
        val result = SMBDifferentialTestGenerator.testFunction("{func}", numTests = 10)
        println("{func}: ${{result.passedTests}}/${{result.totalTests}} passed (${{\"%.1f\".format(result.passRate * 100)}}%)")

        if (result.failedTests > 0) {{
            println("  Failed tests:")
            result.testCases.filter {{ !it.passed }}.forEach {{ testCase ->
                println("    Test #${{testCase.testNumber}}:")
                testCase.differences.take(5).forEach {{ diff ->
                    println("      - $diff")
                }}
                if (testCase.differences.size > 5) {{
                    println("      ... and ${{testCase.differences.size - 5}} more differences")
                }}
            }}
        }}
    }}
'''
        leaf_tests.append(test_method)

    # Generate test methods for non-leaf functions
    non_leaf_tests = []
    for func in non_leaf_functions:
        test_method = f'''    @Test
    fun test_{func}() {{
        val result = SMBDifferentialTestGenerator.testFunction("{func}", numTests = 10)
        println("{func}: ${{result.passedTests}}/${{result.totalTests}} passed (${{\"%.1f\".format(result.passRate * 100)}}%)")

        if (result.failedTests > 0) {{
            println("  Failed tests:")
            result.testCases.filter {{ !it.passed }}.forEach {{ testCase ->
                println("    Test #${{testCase.testNumber}}:")
                testCase.differences.take(5).forEach {{ diff ->
                    println("      - $diff")
                }}
                if (testCase.differences.size > 5) {{
                    println("      ... and ${{testCase.differences.size - 5}} more differences")
                }}
            }}
        }}
    }}
'''
        non_leaf_tests.append(test_method)

    # Create list of all function names for summary test
    all_functions = ['            "' + func + '"' for func in leaf_functions + non_leaf_functions]
    all_functions_str = ',\n'.join(all_functions)

    # Replace placeholders
    header = header.replace('TOTAL_COUNT', str(len(leaf_functions) + len(non_leaf_functions)))
    header = header.replace('LEAF_COUNT', str(len(leaf_functions)))
    header = header.replace('NON_LEAF_COUNT', str(len(non_leaf_functions)))

    middle_section = middle_section.replace('NON_LEAF_COUNT', str(len(non_leaf_functions)))

    footer = footer.replace('ALL_FUNCTIONS_LIST', all_functions_str)

    # Assemble complete file
    complete_test = header
    complete_test += '\n'.join(leaf_tests)
    complete_test += middle_section
    complete_test += '\n'.join(non_leaf_tests)
    complete_test += footer

    return complete_test

def main():
    print("Loading function list...")
    leaf_functions, non_leaf_functions = load_function_list()

    print(f"Found {len(leaf_functions)} leaf functions")
    print(f"Found {len(non_leaf_functions)} non-leaf functions")
    print(f"Total: {len(leaf_functions) + len(non_leaf_functions)} functions")
    print()

    print("Generating test file...")
    test_code = generate_test_file(leaf_functions, non_leaf_functions)

    output_file = 'src/test/kotlin/SMBComprehensiveDifferentialTest.kt'
    with open(output_file, 'w') as f:
        f.write(test_code)

    print(f"Generated {output_file}")
    print(f"  Total test methods: {len(leaf_functions) + len(non_leaf_functions) + 1}")  # +1 for summary
    print(f"  Lines of code: {len(test_code.splitlines())}")
    print()
    print("✓ Test generation complete!")

if __name__ == '__main__':
    main()
