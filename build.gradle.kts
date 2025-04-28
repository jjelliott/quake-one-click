plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.2.1"
    id("groovy")
}

version = "0.1"
group = "io.github.jjelliott.q1installer"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("info.picocli:picocli-codegen")
    annotationProcessor("io.micronaut:micronaut-inject-java")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    implementation("io.github.spair:imgui-java-app:1.89.0")

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
    implementation("info.picocli:picocli")
    implementation("io.micronaut.picocli:micronaut-picocli")
    implementation("org.apache.commons:commons-compress:1.26.1")
    runtimeOnly("ch.qos.logback:logback-classic")
    testImplementation(platform("org.apache.groovy:groovy-bom:4.0.20"))
    testImplementation("org.apache.groovy:groovy")
    testImplementation(platform("org.spockframework:spock-bom:2.3-groovy-4.0"))
    testImplementation("org.spockframework:spock-core")
}


application {
    mainClass.set("io.github.jjelliott.q1installer.Q1InstallerCommand")
}

java {
    sourceCompatibility = JavaVersion.toVersion("17")
    targetCompatibility = JavaVersion.toVersion("17")
}

micronaut {
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("io.github.jjelliott.q1installer.*")
    }
}

graalvmNative {
    binaries {
        all {
            resources.autodetect()
        }
        named("main") { // Use named("main") to configure the 'main' binary
            javaLauncher.set(javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(21))
                vendor.set(JvmVendorSpec.GRAAL_VM)
            })
        }
    }
    agent {
        enabled.set(true)
    }
}

tasks {
    compileGroovy {
        enabled = false
    }
    runnerJar {
        enabled = false
    }
    shadowJar {
        archiveClassifier.set("")
        archiveVersion.set("")
    }
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}



