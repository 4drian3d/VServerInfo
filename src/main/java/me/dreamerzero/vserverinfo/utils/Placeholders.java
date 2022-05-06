package me.dreamerzero.vserverinfo.utils;

import java.util.stream.Collector;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;

import org.jetbrains.annotations.NotNull;

import me.dreamerzero.vserverinfo.configuration.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class Placeholders {
    private static final Collector<Component, ?, Component> COLLECTOR = Component.toComponent(Component.newline());

    public static Component getServerComponent(Config.Configuration config, final RegisteredServer server, final ServerPing ping){
        final TagResolver resolver = TagResolver.resolver(
            Placeholder.unparsed("server", server.getServerInfo().getName()),
            Placeholder.component("motd", ping.getDescriptionComponent()),
            Placeholder.unparsed("count", String.valueOf(server.getPlayersConnected().size())),
            Placeholder.unparsed("maxcount", String.valueOf(ping.asBuilder().getMaximumPlayers())),
            Placeholder.unparsed("ip", String.valueOf(server.getServerInfo().getAddress().getAddress().getHostAddress()))
        );

        return MiniMessage.miniMessage()
            .deserialize(config.getAvailableFormat(), resolver)
            .hoverEvent(
                HoverEvent.showText(config.getAvailableHover()
                    .stream()
                    .map(st -> MiniMessage.miniMessage().deserialize(st, resolver))
                    .collect(COLLECTOR)
                ))
            .append(Component.space());
    }

    public static Component getOfflineServerComponent(Config.Configuration config, final RegisteredServer server){
        return MiniMessage.miniMessage()
            .deserialize(config.getNotAvailableFormat(),
                Placeholder.unparsed("server", server.getServerInfo().getName()),
                Placeholder.unparsed("count", String.valueOf(server.getPlayersConnected().size())),
                Placeholder.unparsed("ip", String.valueOf(server.getServerInfo().getAddress().getAddress().getHostAddress()))
            ).append(Component.space());
    }

    public static Component getInfoComponent(Config.Configuration config, @NotNull Component online, @NotNull Component offline){
        final TagResolver resolver = TagResolver.resolver(
            Placeholder.component("onlineservers", online),
            Placeholder.component("offlineservers", offline)
        );

        return config.getinfoFormat()
            .stream()
            .map(st -> MiniMessage.miniMessage().deserialize(st, resolver))
            .collect(COLLECTOR);
    }

    private Placeholders(){}
}
