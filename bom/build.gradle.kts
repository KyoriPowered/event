plugins {
  id("event.base")
  id("java-platform")
}

indra {
  configurePublications {
    from(components["javaPlatform"])
  }
}

dependencies {
  constraints {
    listOf("api").forEach {
      api(project(":event-$it"))
    }
  }
}
