import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication

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
            implementation("io.ktor:ktor-server-content-negotiation")
            implementation("io.ktor:ktor-serialization-kotlinx-json")
            implementation("io.ktor:ktor-server-cors")

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
