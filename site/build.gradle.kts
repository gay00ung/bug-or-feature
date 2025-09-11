import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import org.gradle.jvm.tasks.Jar

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kobweb.application)
    alias(libs.plugins.kobwebx.markdown)
}

group = "net.lateinit.bug_or_feature.site"
version = "1.0-SNAPSHOT"

kobweb {
    app {
        index {
            description.set("Powered by Kobweb")
        }
    }
}

kotlin {
    configAsKobwebApplication("bug_or_feature", includeServer = true)

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared"))
            implementation(libs.serialization.json)
        }
        jvmMain.dependencies {
            implementation(libs.kobweb.api)
            implementation(project.dependencies.platform("io.ktor:ktor-bom:2.3.7"))

            implementation("io.ktor:ktor-server-core")
            implementation("io.ktor:ktor-server-netty")
            implementation("io.ktor:ktor-server-content-negotiation")
            implementation("io.ktor:ktor-serialization-kotlinx-json")
            implementation("io.ktor:ktor-server-cors")

            // Logging backend to avoid SLF4J warnings
            implementation("org.slf4j:slf4j-simple:2.0.13")

            // Force a coroutines version compatible with Ktor 2.3.7 to avoid NoSuchMethodError at runtime
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.3")

            implementation("org.jetbrains.exposed:exposed-core:0.57.0")
            implementation("org.jetbrains.exposed:exposed-dao:0.57.0")
            implementation("org.jetbrains.exposed:exposed-jdbc:0.57.0")
            implementation("org.xerial:sqlite-jdbc:3.47.1.0")
        }
        jsMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.html.core)
            implementation(libs.kobweb.core)
            implementation(libs.kobweb.silk)
            implementation(libs.silk.icons.fa)
            implementation(libs.kobwebx.markdown)
            implementation(libs.serialization.json)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.js)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }
    }
}

// Ensure the JVM runtime uses the intended coroutines version
configurations.matching { it.name.contains("jvm", ignoreCase = true) }.configureEach {
    resolutionStrategy {
        force(
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3",
            "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.3",
            "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.3"
        )
    }
}

// Create a self-contained (fat) JAR for the Ktor server so we can run it without the 'application' plugin
// This avoids the incompatibility between 'application' and 'kotlin-multiplatform' plugins.
tasks.register<Jar>("serverFatJar") {
    group = "build"
    archiveBaseName.set("site-server")
    archiveClassifier.set("")
    archiveFileName.set("site-server.jar")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // Main class for Ktor server
    manifest {
        attributes["Main-Class"] = "net.lateinit.bug_or_feature.site.server.ServerKt"
    }

    // Include compiled classes from the JVM target
    val jvmTarget = kotlin.targets.getByName("jvm")
    val jvmCompilation = jvmTarget.compilations.getByName("main")
    from(jvmCompilation.output)

    // Include runtime dependencies
    val runtimeClasspath = configurations.getByName("jvmRuntimeClasspath")
    dependsOn(jvmCompilation.compileAllTaskName)
    from(runtimeClasspath.map { if (it.isDirectory) it else zipTree(it) })
}
