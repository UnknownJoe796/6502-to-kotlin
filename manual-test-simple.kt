#!/usr/bin/env kotlin

/**
 * Manual test to validate code generation for simple SMB functions.
 * This bypasses Gradle and runs directly to see what's happening.
 */

@file:DependsOn(".")

// Simple test to see what we generate for DoNothing2
fun main() {
    println("=".repeat(80))
    println("Manual Code Generation Test - DoNothing2")
    println("=".repeat(80))

    // DoNothing2 is just: rts
    // This should generate basically nothing (just return)

    println("\nFunction: DoNothing2")
    println("Assembly: rts")
    println("\nExpected Kotlin: (empty or just return)")
    println("\nThis is the simplest possible test case.")
    println("\nNext: Try WritePPUReg1 which does:")
    println("  sta PPU_CTRL_REG1")
    println("  sta Mirror_PPU_CTRL_REG1")
    println("  rts")
    println("\nExpected Kotlin:")
    println("  memory[PPU_CTRL_REG1] = A")
    println("  memory[Mirror_PPU_CTRL_REG1] = A")
}

main()
