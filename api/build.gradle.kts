plugins {
  id("event.common")
}

dependencies {
  compileOnlyApi("org.checkerframework:checker-qual:3.13.0")
}

tasks.jar {
  manifest.attributes(
    "Automatic-Module-Name" to "net.kyori.event"
  )
}
