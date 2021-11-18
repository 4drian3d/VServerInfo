package me.dreamerzero.vserverinfo;

import java.nio.file.Path;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import org.slf4j.Logger;

import me.dreamerzero.vserverinfo.commands.ServerInfoCommand;
import me.dreamerzero.vserverinfo.configuration.Config;

public class ServerInfo {
    private final ProxyServer proxy;
    private final Logger logger;
    private final Path path;

    @Inject
    public ServerInfo(ProxyServer proxy, Logger logger, @DataDirectory Path path) {
        this.proxy = proxy;
        this.logger = logger;
        this.path = path;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        Config.loadConfig(path, logger);
        ServerInfoCommand.brigadierServerInfo(proxy);
        logger.info("ServerInfo correctly started");
    }

}
