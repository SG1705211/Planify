val releaseVersion: String by project
val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

val exposed_version: String by project
val h2_version: String by project

kotlin {
    jvmToolchain(17)
}

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.4.21"
    id("io.ktor.plugin") version "2.3.6"
}

group = "org.example"
version = releaseVersion

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("com.h2database:h2:$h2_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.testng:testng:7.1.0")
    implementation("com.mohamedrejeb.richeditor:richeditor-compose:1.0.0-beta03")
    implementation("org.jetbrains.compose.material3:material3-desktop:1.5.3")
    implementation("org.xerial:sqlite-jdbc:3.43.2.1")
    implementation("org.jetbrains.exposed", "exposed-core", "0.44.0")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.44.0")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.44.0")
    implementation("org.xerial:sqlite-jdbc:3.43.2.1")
    implementation ("org.jetbrains.exposed:exposed-jodatime:0.34.1") // Adjust version as necessary
    implementation ("joda-time:joda-time:2.10.10") // Adjust version as necessary
    implementation ("io.ktor:ktor-client-core:2.3.6")
    implementation ("io.ktor:ktor-client-cio:2.3.6")
}

tasks.test {
    useJUnitPlatform()
}