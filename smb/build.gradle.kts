// SMB module: Super Mario Bros. decompiled code and TAS validation

dependencies {
    implementation(project(":core"))
    testImplementation(project(":core"))
    testImplementation(project(":core", "testArchives"))
}

tasks.withType<Test> {
    // Set working directory to project root so tests can find smbdism.asm and outputs/
    workingDir = rootProject.projectDir
}
