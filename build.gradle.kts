plugins {
    java
    id("net.kyori.blossom") version "1.3.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("xyz.jpenilla.run-velocity") version "2.0.0"
}

val configurateVersion: String by project.extra
val geantyrefVersion: String by project.extra

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")
    implementation("com.github.AlessioDP.libby:libby-velocity:43d25ade72")
    compileOnly("org.spongepowered:configurate-hocon:$configurateVersion")
}

blossom{
    replaceTokenIn("src/main/java/me/adrianed/vserverinfo/utils/Constants.java")
    replaceToken("{version}", version)
    replaceToken("{configurate}", configurateVersion)
    replaceToken("{geantyref}", geantyrefVersion)
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
        relocate("org.spongepowered", "me.adrianed.vserverinfo.libs.sponge")
        relocate("net.byteflux", "me.adrianed.vserverinfo.libs.byteflux")
        relocate("io.leangen.geantyref", "me.adrianed.vserverinfo.libs.geantyref")
    }

    runVelocity {
        velocityVersion("3.2.0-SNAPSHOT")
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))
