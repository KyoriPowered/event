plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(libs.indraCommon)
    implementation(libs.indraPublishingSonatype)

    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}