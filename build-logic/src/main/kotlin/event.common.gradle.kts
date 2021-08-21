import net.kyori.indra.repository.sonatypeSnapshots
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("net.kyori.indra")
    id("net.kyori.indra.checkstyle")
    id("net.kyori.indra.license-header")
}

repositories {
    mavenCentral()
    sonatypeSnapshots()
}


val libs = the<LibrariesForLibs>()

dependencies {
    checkstyle(libs.stylecheck)
    testImplementation("com.google.guava:guava-testlib:30.1.1-jre")
    testImplementation("com.google.truth:truth:1.1.2")
    testImplementation("com.google.truth.extensions:truth-java8-extension:1.1.2")
    testImplementation(platform("org.junit:junit-bom:5.7.2"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
}