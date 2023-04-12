plugins {
    java
    alias(libs.plugins.blossom)
    alias(libs.plugins.shadow)
    alias(libs.plugins.runvelocity)
}

repositories {
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.alessiodp.com/releases/") {
        mavenContent {
            includeGroup(libs.libby.get().group)
        }
    }
}

dependencies {
    compileOnly(libs.velocity)
    annotationProcessor(libs.velocity)
    implementation(libs.libby)
    implementation(libs.velocityhexlogger)
    compileOnly(libs.configurate)
    compileOnly(libs.miniplaceholders)
    compileOnly(libs.completables)
}

blossom{
    replaceTokenIn("src/main/java/io/github/_4drian3d/vserverinfo/utils/Constants.java")
    replaceToken("{version}", version)
    replaceToken("{configurate}", libs.versions.configurate.get())
    replaceToken("{geantyref}", libs.versions.geantyref.get())
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
        relocate("org.spongepowered", "io.github._4drian3d.vserverinfo.libs.sponge")
        relocate("net.byteflux", "io.github._4drian3d.libs.byteflux")
        relocate("io.leangen.geantyref", "io.github._4drian3d.libs.geantyref")
        relocate("io.github._4drian3d.velocityhexlogger", "io.github._4drian3d.vserverinfo.velocityhexlogger")
        relocate("net.kyori.adventure.text.logger.slf4j", "io.github._4drian3d.vserverinfo.component.logger")
    }

    runVelocity {
        velocityVersion(libs.versions.velocity.get())
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))
