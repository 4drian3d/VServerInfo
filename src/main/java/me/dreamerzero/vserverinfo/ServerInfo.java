package me.dreamerzero.vserverinfo;

import java.nio.file.Path;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import org.slf4j.Logger;

import me.dreamerzero.vserverinfo.commands.ServerInfoCommand;
import me.dreamerzero.vserverinfo.configuration.Config;
import me.dreamerzero.vserverinfo.utils.Constants;

@Plugin(
    id = "vserverinfo",
    name = "vServerInfo",
    version = Constants.VERSION,
    description = "Get Info about your servers",
    authors = ("4drian3d")
)
public class ServerInfo {
    private final ProxyServer proxy;
    private final Logger logger;
    private final Path path;
    private Config.Configuration configuration;

    @Inject
    public ServerInfo(ProxyServer proxy, Logger logger, @DataDirectory Path path) {
        this.proxy = proxy;
        this.logger = logger;
        this.path = path;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        this.configuration = Config.loadConfig(path, logger);
        if (configuration == null) {
            return;
        }
        ServerInfoCommand.command(this);
        logger.info("ServerInfo correctly started");
    }

    public Config.Configuration config() {
        return this.configuration;
    }

    public ProxyServer proxy() {
        return this.proxy;
    }

}
