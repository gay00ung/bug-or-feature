plugins {
    kotlin("jvm")
    id("io.ktor.plugin") version "3.0.3"
    kotlin("plugin.serialization")
}

dependencies {
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-cors")

    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-content-negotiation")

    implementation("org.jetbrains.exposed:exposed-core:0.57.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.57.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.57.0")
    implementation("org.xerial:sqlite-jdbc:3.47.1.0")

    implementation(project(":shared"))
    implementation(libs.kobweb.api)
}
