package me.dreamerzero.vserverinfo.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;

import me.dreamerzero.vserverinfo.ServerInfo;
import me.dreamerzero.vserverinfo.utils.Placeholders;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ServerInfoCommand {
    public static void command(final ServerInfo plugin){
        final LiteralCommandNode<CommandSource> infoCommand = LiteralArgumentBuilder
            .<CommandSource>literal("serverinfo")
            .executes(context -> sendAllInfo(plugin, context.getSource()))
            .then(RequiredArgumentBuilder.<CommandSource, String>argument("server", StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    plugin.proxy().getAllServers().forEach(sv -> builder.suggest(sv.getServerInfo().getName()));
                    builder.suggest("ALL");
                    return builder.buildFuture();
                })
                .executes(cmd -> {
                    final String server = cmd.getArgument("server", String.class);
                    if (server.equals("ALL")) {
                        return sendAllInfo(plugin, cmd.getSource());
                    }
                    plugin.proxy().getServer(server).ifPresentOrElse(sv -> {
                        sv.ping().whenCompleteAsync((ping, exception) -> {
                            if(exception != null) {
                                cmd.getSource().sendMessage(
                                    Placeholders.getOfflineServerComponent(plugin.config(), sv)
                                );
                            } else {
                                cmd.getSource().sendMessage(
                                    Placeholders.getServerComponent(
                                        plugin.config(),
                                        sv,
                                        ping
                                    )
                                );
                            }
                        });
                    }, () -> {
                        cmd.getSource().sendMessage(MiniMessage.miniMessage().deserialize(plugin.config().getNotExistsFormat()));
                    });
                    return Command.SINGLE_SUCCESS;
                })
            ).build();

            final CommandManager manager = plugin.proxy().getCommandManager();
            manager.register(manager.metaBuilder("serverinfo")
                .aliases("vserverinfo")
                .build(), new BrigadierCommand(infoCommand));
    }

    private static int sendAllInfo(final ServerInfo plugin, CommandSource source) {
        final var registeredServers = plugin.proxy().getAllServers();
        final Map<RegisteredServer, ServerPing> servers = new HashMap<>(registeredServers.size());
        CompletableFuture.allOf(registeredServers.parallelStream()
            .map(server -> server.ping().handleAsync((ping, ex) -> servers.put(server, ping)))
            .toArray(CompletableFuture[]::new))
            .thenRunAsync(() -> {
                final TextComponent.Builder onlineServers = Component.text();
                final TextComponent.Builder offlineServers = Component.text();
                servers.forEach((server, ping) -> {
                    if (ping == null) {
                        offlineServers.append(
                            Placeholders.getOfflineServerComponent(plugin.config(), server)
                        );
                    } else {
                        onlineServers.append(
                            Placeholders.getServerComponent(plugin.config(), server, ping)
                        );
                    }
                });
                source.sendMessage(
                    Placeholders.getInfoComponent(
                        plugin.config(),
                        onlineServers.build(),
                        offlineServers.build()
                    ));
            });
            return Command.SINGLE_SUCCESS;
    }

    private ServerInfoCommand(){}
}
