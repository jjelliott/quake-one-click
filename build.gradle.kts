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
    toolchainDetection.set(true)
}

tasks{
    compileGroovy{
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
            events ("passed", "skipped", "failed")
        }
    }
}



