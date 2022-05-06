import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation

plugins {
    java
    id("net.kyori.blossom") version "1.3.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.maven.apache.org/maven2/")
}

dependencies {
    shadow("org.spongepowered:configurate-hocon:4.1.2")
    compileOnly("com.velocitypowered:velocity-api:3.1.2-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.1.2-SNAPSHOT")
}

blossom{
    replaceTokenIn("src/main/java/me/dreamerzero/vserverinfo/utils/Constants.java")
    replaceToken("{version}", version)
}
tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        dependsOn(getByName("relocateShadowJar") as ConfigureShadowRelocation)
        minimize()
        archiveFileName.set("VServerInfo.jar")
        configurations = listOf(project.configurations.shadow.get())
    }

    create<ConfigureShadowRelocation>("relocateShadowJar") {
        target = shadowJar.get()
        prefix = "me.dreamerzero.vserverinfo.libs"
    }

    compileJava {
        options.release.set(17)
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))
