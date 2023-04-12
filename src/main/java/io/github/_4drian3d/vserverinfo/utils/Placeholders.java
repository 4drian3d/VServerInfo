package io.github._4drian3d.vserverinfo.utils;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import io.github._4drian3d.vserverinfo.configuration.Configuration;
import io.github.miniplaceholders.api.MiniPlaceholders;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collector;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public final class Placeholders {
    private static final Collector<Component, ?, Component> COLLECTOR = Component.toComponent(Component.newline());

    @Inject
    private Configuration configuration;
    @Inject
    private PluginManager pluginManager;

    public Component getServerComponent(Configuration.OnlineFormat config, final RegisteredServer server, final ServerPing ping){
        final TagResolver.Builder builder = TagResolver.builder()
                .resolvers(
                        Placeholder.unparsed("server", server.getServerInfo().getName()),
                        Placeholder.component("motd", ping.getDescriptionComponent()),
                        Placeholder.unparsed("count", String.valueOf(server.getPlayersConnected().size())),
                        Placeholder.unparsed("maxcount", String.valueOf(ping.asBuilder().getMaximumPlayers())),
                        Placeholder.unparsed("ip", String.valueOf(server.getServerInfo().getAddress().getAddress().getHostAddress()))
                );

        if (pluginManager.isLoaded("miniplaceholders")) {
            builder.resolver(MiniPlaceholders.getGlobalPlaceholders());
        }

        final TagResolver resolver = builder.build();

        return miniMessage()
            .deserialize(config.getFormat(), resolver)
            .hoverEvent(
                HoverEvent.showText(config.getHover()
                    .stream()
                    .map(st -> miniMessage().deserialize(st, resolver))
                    .collect(COLLECTOR)
                ))
            .append(Component.space());
    }

    public Component getOfflineServerComponent(Configuration.OfflineFormat config, final RegisteredServer server) {
        final TagResolver.Builder builder = TagResolver.builder()
                .resolvers(
                        Placeholder.unparsed("server", server.getServerInfo().getName()),
                        Placeholder.unparsed("ip", String.valueOf(server.getServerInfo().getAddress().getAddress().getHostAddress()))
                );
        if (pluginManager.isLoaded("miniplaceholders")) {
            builder.resolver(MiniPlaceholders.getGlobalPlaceholders());
        }
        final TagResolver resolver = builder.build();

        return miniMessage()
            .deserialize(config.getFormat(), resolver)
                .append(Component.space())
                .hoverEvent(HoverEvent.showText(
                        config.getHover()
                                .stream()
                                .map(st -> miniMessage().deserialize(st, resolver))
                                .collect(COLLECTOR)
                ));
    }

    public Component getInfoComponent(@NotNull Component online, @NotNull Component offline){
        final TagResolver.Builder builder = TagResolver.builder().resolvers(
            Placeholder.component("onlineservers", online),
            Placeholder.component("offlineservers", offline)
        );

        if (pluginManager.isLoaded("miniplaceholders")) {
            builder.resolver(MiniPlaceholders.getGlobalPlaceholders());
        }
        final TagResolver resolver = builder.build();

        return configuration.getAll().getFormat()
            .stream()
            .map(st -> miniMessage().deserialize(st, resolver))
            .collect(COLLECTOR);
    }
}
