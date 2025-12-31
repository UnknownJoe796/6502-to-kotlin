plugins {
    kotlin("jvm") version "2.2.0" apply false
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
