plugins {
    kotlin("jvm") version "2.0.0"
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "org.vinYtFetch"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.0")
}

application {
    mainClass.set("org.vinYtFetch.MainKt")
}

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "org.vinYtFetch.MainKt"
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}