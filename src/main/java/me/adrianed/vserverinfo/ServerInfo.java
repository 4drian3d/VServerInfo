package me.adrianed.vserverinfo;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import me.adrianed.vserverinfo.commands.ServerInfoCommand;
import me.adrianed.vserverinfo.configuration.Configuration;
import me.adrianed.vserverinfo.configuration.Loader;
import me.adrianed.vserverinfo.utils.Constants;
import me.adrianed.vserverinfo.utils.Libraries;
import org.slf4j.Logger;

@Plugin(
    id = "vserverinfo",
    name = "vServerInfo",
    version = Constants.VERSION,
    description = "Get Information about your servers",
    authors = ("4drian3d"),
    dependencies = { @Dependency(id = "miniplaceholders", optional = true)}
)
public final class ServerInfo {
    @Inject
    private Logger logger;
    @Inject
    private Injector injector;

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        injector.getInstance(Libraries.class).load();

        final Configuration configuration = injector.getInstance(Loader.class)
                .loadConfig();
        if (configuration == null) {
            return;
        }

        injector = injector.createChildInjector(
                binder -> binder.bind(Configuration.class).toInstance(configuration)
        );

        injector.getInstance(ServerInfoCommand.class).register();
        logger.info("ServerInfo correctly started");
    }
}
