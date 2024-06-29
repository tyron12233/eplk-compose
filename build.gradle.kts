plugins {
    kotlin("jvm") version "1.9.23"
    java
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

group = "com.tyron.compose"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {
    testImplementation(kotlin("test"))

    // compose runtime
    implementation(compose.desktop.currentOs)
    implementation("androidx.compose.runtime:runtime:1.6.8")
    implementation("org.jetbrains.compose.desktop:desktop:1.6.11")
    implementation("org.jetbrains.compose.desktop:desktop-jvm-linux-x64:1.6.11")
    implementation("org.jetbrains.compose.components:components-splitpane:1.6.11")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}