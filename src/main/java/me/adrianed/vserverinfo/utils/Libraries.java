package me.adrianed.vserverinfo.utils;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import me.adrianed.vserverinfo.ServerInfo;
import net.byteflux.libby.Library;
import net.byteflux.libby.VelocityLibraryManager;
import net.byteflux.libby.relocation.Relocation;
import org.slf4j.Logger;

import java.nio.file.Path;

public final class Libraries {
    @Inject
    private ServerInfo plugin;
    @Inject
    private Logger logger;
    @Inject
    @DataDirectory
    private Path path;
    @Inject
    private PluginManager pluginManager;

    public void load() {
        final VelocityLibraryManager<ServerInfo> libraryManager
                = new VelocityLibraryManager<>(logger, path, pluginManager, plugin, "libs");
        final Relocation configurateRelocation
                = new Relocation("org{}spongepowered", "me.adrianed.vserverinfo.libs.sponge");
        final Relocation geantyrefRelocation =
                new Relocation("io{}leangen{}geantyref", "me.adrianed.vserverinfo.libs.geantyref");
        final Library hocon = Library.builder()
                .groupId("org{}spongepowered")
                .artifactId("configurate-hocon")
                .version(Constants.CONFIGURATE)
                .id("configurate-hocon")
                .relocate(configurateRelocation)
                .relocate(geantyrefRelocation)
                .build();
        final Library confCore = Library.builder()
                .groupId("org{}spongepowered")
                .artifactId("configurate-core")
                .version(Constants.CONFIGURATE)
                .id("configurate-core")
                .relocate(configurateRelocation)
                .relocate(geantyrefRelocation)
                .build();
        final Library geantyref = Library.builder()
                .groupId("io{}leangen{}geantyref")
                .artifactId("geantyref")
                .version(Constants.GEANTYREF)
                .id("geantyref")
                .relocate(geantyrefRelocation)
                .build();

        libraryManager.addMavenCentral();
        libraryManager.loadLibrary(geantyref);
        libraryManager.loadLibrary(confCore);
        libraryManager.loadLibrary(hocon);
    }
}
