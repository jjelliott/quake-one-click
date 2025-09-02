plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.2.1"
    id("groovy")
    id("com.diffplug.spotless") version "7.0.3"
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

    // Use the new GUI library
    implementation(project(":quake-installer-gui"))

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

spotless {
    java {
//        eclipse()
//            // Optional: Enable the Sort Members feature globally. (default: false)
//            .sortMembersEnabled(true)
//            // Optional: Specify the sort order of the member categories. (default: T,SF,SI,SM,F,I,C,M)
//            //   SF,SI,SM,F,I,C,M,T = Static Fields, Static Initializers, Static Methods, Fields, Initializers, Constructors, Methods, (Nested) Types
//            .sortMembersOrder("SF,SI,SM,F,I,C,M,T")
//            // Optional: Enable the reordering of fields, enum constants, and initializers. (default: true)
//            .sortMembersDoNotSortFields(false)
//            // Optional: Enable reordering of members of the same category by the visibility within the category. (default: false)
//            .sortMembersVisibilityOrderEnabled(true)
//            // Optional: Specify the ordering of members of the same category by the visibility within the category. (default: B,V,R,D)
//            //   B,R,D,V = Public, Protected, Package, Private
//            .sortMembersVisibilityOrder("B,R,D,V")
        palantirJavaFormat().style("GOOGLE")
    }
}



