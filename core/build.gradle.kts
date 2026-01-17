// Core module: 6502 decompiler, runtime support, and interpreter

plugins {
    kotlin("plugin.serialization")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}

tasks.withType<Test> {
    // Set working directory to project root so tests can find smbdism.asm and outputs/
    workingDir = rootProject.projectDir

    // More memory for large TAS captures
    maxHeapSize = "4g"

    // by Claude - Reduced default timeout from 10m to 1m to catch infinite loops
    // TAS is ~5 min long, but tests should complete much faster. Individual tests
    // can extend with @Timeout if needed.
    systemProperty("junit.jupiter.execution.timeout.default", "1m")

    // Show test output in console
    testLogging {
        showStandardStreams = true
        events("passed", "skipped", "failed")
    }
}
