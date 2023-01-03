package me.adrianed.vserverinfo.utils;

import java.util.stream.Collector;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;

import me.adrianed.vserverinfo.configuration.Configuration;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public final class Placeholders {
    private static final Collector<Component, ?, Component> COLLECTOR = Component.toComponent(Component.newline());

    public static Component getServerComponent(Configuration.OnlineFormat config, final RegisteredServer server, final ServerPing ping){
        final TagResolver resolver = TagResolver.resolver(
            Placeholder.unparsed("server", server.getServerInfo().getName()),
            Placeholder.component("motd", ping.getDescriptionComponent()),
            Placeholder.unparsed("count", String.valueOf(server.getPlayersConnected().size())),
            Placeholder.unparsed("maxcount", String.valueOf(ping.asBuilder().getMaximumPlayers())),
            Placeholder.unparsed("ip", String.valueOf(server.getServerInfo().getAddress().getAddress().getHostAddress()))
        );

        return MiniMessage.miniMessage()
            .deserialize(config.getFormat(), resolver)
            .hoverEvent(
                HoverEvent.showText(config.getHover()
                    .stream()
                    .map(st -> MiniMessage.miniMessage().deserialize(st, resolver))
                    .collect(COLLECTOR)
                ))
            .append(Component.space());
    }

    public static Component getOfflineServerComponent(Configuration.OfflineFormat config, final RegisteredServer server) {
        return MiniMessage.miniMessage()
            .deserialize(config.getFormat(),
                Placeholder.unparsed("server", server.getServerInfo().getName()),
                Placeholder.unparsed("count", String.valueOf(server.getPlayersConnected().size())),
                Placeholder.unparsed("ip", String.valueOf(server.getServerInfo().getAddress().getAddress().getHostAddress()))
            ).append(Component.space())
                .hoverEvent(HoverEvent.showText(
                        config.getHover()
                                .stream()
                                .map(MiniMessage.miniMessage()::deserialize)
                                .collect(COLLECTOR)
                ));
    }

    public static Component getInfoComponent(final Configuration config, @NotNull Component online, @NotNull Component offline){
        final TagResolver resolver = TagResolver.resolver(
            Placeholder.component("onlineservers", online),
            Placeholder.component("offlineservers", offline)
        );

        return config.getAll().getFormat()
            .stream()
            .map(st -> MiniMessage.miniMessage().deserialize(st, resolver))
            .collect(COLLECTOR);
    }

    private Placeholders() {}
}
