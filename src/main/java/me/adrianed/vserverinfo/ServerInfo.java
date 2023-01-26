package me.adrianed.vserverinfo;

import java.nio.file.Path;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import me.adrianed.vserverinfo.configuration.Configuration;
import me.adrianed.vserverinfo.configuration.Loader;
import me.adrianed.vserverinfo.utils.Libraries;
import org.slf4j.Logger;

import me.adrianed.vserverinfo.commands.ServerInfoCommand;
import me.adrianed.vserverinfo.utils.Constants;

@Plugin(
    id = "vserverinfo",
    name = "vServerInfo",
    version = Constants.VERSION,
    description = "Get Information about your servers",
    authors = ("4drian3d")
)
public final class ServerInfo {
    @Inject
    private ProxyServer proxy;
    @Inject
    private Logger logger;
    @Inject
    @DataDirectory
    private Path path;
    private Configuration configuration;

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        Libraries.load(this, logger, path, proxy.getPluginManager());
        this.configuration = Loader.loadConfig(path, logger);
        if (this.configuration == null) {
            return;
        }
        ServerInfoCommand.command(this);
        logger.info("ServerInfo correctly started");
    }

    public Configuration config() {
        return this.configuration;
    }

    public ProxyServer proxy() {
        return this.proxy;
    }
}
