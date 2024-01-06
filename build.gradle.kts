plugins {
    java
    alias(libs.plugins.idea.ext)
    alias(libs.plugins.blossom)
    alias(libs.plugins.shadow)
    alias(libs.plugins.runvelocity)
}

repositories {
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
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
    build {
        dependsOn(shadowJar)
    }

    compileJava {
        options.apply {
            release.set(17)
            encoding = Charsets.UTF_8.name()
        }
    }

    shadowJar {
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set("")
    }

    runVelocity {
        velocityVersion(libs.versions.velocity.get())
        downloadPlugins {
            url("https://cdn.modrinth.com/data/HQyibRsN/versions/pxgKwgNJ/MiniPlaceholders-Velocity-2.2.3.jar")
        }
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))
