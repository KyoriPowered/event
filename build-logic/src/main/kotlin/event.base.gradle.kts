plugins {
    id("net.kyori.indra.publishing")
}

indra {
    github("KyoriPowered", "event") {
        ci(true)
    }
    mitLicense()

    configurePublications {
        pom {
            developers {
                developer {
                    id.set("kashike")
                    timezone.set("America/Vancouver")
                }
            }
        }
    }
}