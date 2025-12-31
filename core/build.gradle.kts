// Core module: 6502 decompiler, runtime support, and interpreter

dependencies {
    // No additional dependencies for core
}

tasks.withType<Test> {
    // Set working directory to project root so tests can find smbdism.asm and outputs/
    workingDir = rootProject.projectDir

    // Show test output in console
    testLogging {
        showStandardStreams = true
        events("passed", "skipped", "failed")
    }
}
