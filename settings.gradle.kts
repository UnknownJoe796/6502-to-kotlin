plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "decompiler-6502-kotlin"

include(":core")
include(":smb")