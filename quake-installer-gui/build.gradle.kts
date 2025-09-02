plugins {
    id("java-library")
}

version = "0.1"
group = "io.github.jjelliott.imgui"

repositories {
    mavenCentral()
}

dependencies {
    api("io.github.spair:imgui-java-app:1.89.0")

    implementation("org.lwjgl:lwjgl-stb")
    runtimeOnly("org.lwjgl:lwjgl-stb:3.3.4:natives-windows")
    runtimeOnly("org.lwjgl:lwjgl-stb:3.3.4:natives-macos")
    runtimeOnly("org.lwjgl:lwjgl-stb:3.3.4:natives-macos-arm64")
    runtimeOnly("org.lwjgl:lwjgl-stb:3.3.4:natives-linux")

    implementation("org.lwjgl:lwjgl-tinyfd")
    runtimeOnly("org.lwjgl:lwjgl-tinyfd:3.3.4:natives-windows")
    runtimeOnly("org.lwjgl:lwjgl-tinyfd:3.3.4:natives-macos")
    runtimeOnly("org.lwjgl:lwjgl-tinyfd:3.3.4:natives-macos-arm64")
    runtimeOnly("org.lwjgl:lwjgl-tinyfd:3.3.4:natives-linux")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
