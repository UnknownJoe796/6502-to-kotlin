// SMB module: Super Mario Bros. decompiled code and TAS validation

dependencies {
    implementation(project(":core"))
    testImplementation(project(":core"))
    testImplementation(project(":core", "testArchives"))
}

tasks.withType<Test> {
    // Set working directory to project root so tests can find smbdism.asm and outputs/
    workingDir = rootProject.projectDir

    // by Claude - Default 1 minute timeout to catch infinite loops
    // TAS is ~5 min long, but tests should complete much faster
    systemProperty("junit.jupiter.execution.timeout.default", "1m")

    // Show test output in console
    testLogging {
        showStandardStreams = true
        events("passed", "skipped", "failed")
    }
}
