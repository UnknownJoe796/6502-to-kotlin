plugins {
    kotlin("jvm") version "2.2.0" apply false
    kotlin("plugin.serialization") version "2.2.0" apply false
}

allprojects {
    group = "com.ivieleague.decompiler6502tokotlin"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        "testImplementation"(kotlin("test"))
        // by Claude - JUnit 5 timeout annotations for preventing infinite loops
        "testImplementation"("org.junit.jupiter:junit-jupiter-api:5.10.2")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
        jvmToolchain(17)
    }
}

// Configure core module to expose test classes
project(":core") {
    val testJar by tasks.registering(Jar::class) {
        archiveClassifier.set("tests")
        from(project.extensions.getByType<SourceSetContainer>()["test"].output)
    }

    configurations.create("testArchives") {
        extendsFrom(configurations["testImplementation"])
    }

    artifacts {
        add("testArchives", testJar)
    }
}
