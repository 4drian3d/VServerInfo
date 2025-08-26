plugins {
    java
    alias(libs.plugins.idea.ext)
    alias(libs.plugins.blossom)
    alias(libs.plugins.runvelocity)
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(libs.velocity)
    annotationProcessor(libs.velocity)
    compileOnly(libs.miniplaceholders)
    compileOnly(libs.completables)
}

sourceSets {
    main {
        blossom {
            javaSources {
                property("version", project.version.toString())
            }
        }
    }
}

tasks {

    compileJava {
        options.apply {
            release.set(21)
            encoding = Charsets.UTF_8.name()
        }
    }

    runVelocity {
        velocityVersion(libs.versions.velocity.get())
        downloadPlugins {
            url("https://cdn.modrinth.com/data/HQyibRsN/versions/pxgKwgNJ/MiniPlaceholders-Velocity-2.2.3.jar")
        }
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))
