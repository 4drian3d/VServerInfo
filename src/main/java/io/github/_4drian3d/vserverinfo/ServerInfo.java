package io.github._4drian3d.vserverinfo;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import io.github._4drian3d.vserverinfo.commands.ServerInfoCommand;
import io.github._4drian3d.vserverinfo.configuration.Configuration;
import io.github._4drian3d.vserverinfo.configuration.Loader;
import io.github._4drian3d.vserverinfo.utils.Constants;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

@Plugin(
    id = "vserverinfo",
    name = "vServerInfo",
    version = Constants.VERSION,
    description = "Get Information about your servers",
    authors = ("4drian3d"),
    dependencies = { @Dependency(id = "miniplaceholders", optional = true) }
)
public final class ServerInfo {
    @Inject
    private ComponentLogger logger;
    @Inject
    private Injector injector;

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        final Configuration configuration = injector.getInstance(Loader.class)
                .loadConfig();
        if (configuration == null) {
            return;
        }

        injector = injector.createChildInjector(
                binder -> binder.bind(Configuration.class).toInstance(configuration)
        );

        injector.getInstance(ServerInfoCommand.class).register();
        logger.info(miniMessage().deserialize(
                "<gradient:#E8C547:#F59916>VServerInfo</gradient> by <aqua>4drian3d <white>has correctly started"));
    }
}
