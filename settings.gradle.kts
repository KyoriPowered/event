pluginManagement {
  includeBuild("build-logic")
}

plugins {
  id("ca.stellardrift.polyglot-version-catalogs") version "5.0.0"
}

rootProject.name = "event-parent"

listOf("api", "bom").forEach {
  include(it)
  findProject(":$it")?.name = "event-$it"
}
