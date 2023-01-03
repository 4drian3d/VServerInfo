package me.adrianed.vserverinfo.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

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

import me.adrianed.vserverinfo.ServerInfo;
import me.adrianed.vserverinfo.utils.Placeholders;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class ServerInfoCommand {
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
                    final String server = StringArgumentType.getString(cmd, "server");
                    final var source = cmd.getSource();
                    if (server.equals("ALL")) {
                        return sendAllInfo(plugin, source);
                    }
                    plugin.proxy().getServer(server).ifPresentOrElse(sv ->
                            sv.ping().handleAsync((ping, exception) -> exception != null
                                ? Placeholders.getOfflineServerComponent(plugin.config().getSingle().getOffline(), sv)
                                : Placeholders.getServerComponent(plugin.config().getSingle().getOnline(), sv, ping))
                            .thenAcceptAsync(source::sendMessage),
                            () -> source.sendMessage(MiniMessage.miniMessage().deserialize(plugin.config().getServerNotFound())));
                    return Command.SINGLE_SUCCESS;
                })
            ).build();

            final CommandManager manager = plugin.proxy().getCommandManager();
            final var command = new BrigadierCommand(infoCommand);
            final var meta = manager.metaBuilder(command)
                    .aliases("vserverinfo")
                    .plugin(plugin)
                    .build();
            manager.register(meta, command);
    }

    @SuppressWarnings("SameReturnValue")
    private static int sendAllInfo(final ServerInfo plugin, CommandSource source) {
        final var registeredServers = plugin.proxy().getAllServers();
        final Map<RegisteredServer, ServerPing> servers = new HashMap<>(registeredServers.size());
        CompletableFuture.allOf(registeredServers.parallelStream()
            .map(server -> server.ping().handleAsync((ping, ex) -> servers.put(server, ping)))
            .toArray(CompletableFuture[]::new))
            .thenApplyAsync((ignored) -> {
                final TextComponent.Builder onlineServers = Component.text();
                final TextComponent.Builder offlineServers = Component.text();
                final AtomicBoolean hasOffline = new AtomicBoolean(false);
                final AtomicBoolean hasOnline = new AtomicBoolean(false);
                servers.forEach((server, ping) -> {
                    if (ping == null) {
                        hasOffline.set(true);
                        offlineServers.append(
                            Placeholders.getOfflineServerComponent(plugin.config().getAll().getOffline(), server)
                        );
                    } else {
                        hasOnline.set(true);
                        onlineServers.append(
                            Placeholders.getServerComponent(plugin.config().getAll().getOnline(), server, ping)
                        );
                    }
                });
                if (!hasOnline.get()) {
                    onlineServers.append(
                            MiniMessage.miniMessage().deserialize(
                                    plugin.config().getAll().getOnline().getNoneFound()));
                }
                if (!hasOffline.get()) {
                    offlineServers.append(
                            MiniMessage.miniMessage().deserialize(
                                    plugin.config().getAll().getOffline().getNoneFound()));
                }

                return Placeholders.getInfoComponent(plugin.config(), onlineServers.build(), offlineServers.build());
            }).thenAcceptAsync(source::sendMessage);
            return Command.SINGLE_SUCCESS;
    }

    private ServerInfoCommand() {
    }
}
