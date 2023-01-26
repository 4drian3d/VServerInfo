plugins {
    java
    alias(libs.plugins.blossom)
    alias(libs.plugins.shadow)
    alias(libs.plugins.runvelocity)
}

repositories {
    maven("https://papermc.io/repo/repository/maven-public/") {
        mavenContent {
            includeGroup(libs.velocity.get().group)
        }
    }
    maven("https://jitpack.io") {
        mavenContent {
            includeGroup(libs.libby.get().group)
        }
    }
    mavenCentral()
}

dependencies {
    compileOnly(libs.velocity)
    annotationProcessor(libs.velocity)
    implementation(libs.libby)
    compileOnly(libs.configurate)
}

blossom{
    replaceTokenIn("src/main/java/me/adrianed/vserverinfo/utils/Constants.java")
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
        relocate("org.spongepowered", "me.adrianed.vserverinfo.libs.sponge")
        relocate("net.byteflux", "me.adrianed.vserverinfo.libs.byteflux")
        relocate("io.leangen.geantyref", "me.adrianed.vserverinfo.libs.geantyref")
    }

    runVelocity {
        velocityVersion(libs.versions.velocity.get())
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))
