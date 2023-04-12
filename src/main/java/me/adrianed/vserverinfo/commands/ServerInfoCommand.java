package me.adrianed.vserverinfo.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.inject.Inject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.proxy.server.PingOptions;

import me.adrianed.vserverinfo.ServerInfo;
import me.adrianed.vserverinfo.configuration.Configuration;
import me.adrianed.vserverinfo.utils.Placeholders;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class ServerInfoCommand {
    private static final PingOptions OPTIONS = PingOptions.builder()
            .version(ProtocolVersion.MINECRAFT_1_16_4)
            .build();

    @Inject
    private ServerInfo plugin;
    @Inject
    private ProxyServer proxyServer;
    @Inject
    private CommandManager commandManager;
    @Inject
    private Placeholders placeholders;
    @Inject
    private Configuration configuration;

    public void register() {
        final LiteralCommandNode<CommandSource> infoCommand = LiteralArgumentBuilder
            .<CommandSource>literal("serverinfo")
            .requires(src -> src.hasPermission("vserverinfo.command"))
            .executes(context -> sendAllInfo(context.getSource()))
            .then(RequiredArgumentBuilder.<CommandSource, String>argument("server", StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    proxyServer.getAllServers().forEach(sv -> builder.suggest(sv.getServerInfo().getName()));
                    builder.suggest("ALL");
                    return builder.buildFuture();
                })
                .executes(cmd -> {
                    final String server = StringArgumentType.getString(cmd, "server");
                    final var source = cmd.getSource();
                    if (server.equals("ALL")) {
                        return sendAllInfo(source);
                    }
                    proxyServer.getServer(server).ifPresentOrElse(sv ->
                            sv.ping(OPTIONS).handleAsync((ping, exception) -> exception != null
                                ? placeholders.getOfflineServerComponent(configuration.getSingle().getOffline(), sv)
                                : placeholders.getServerComponent(configuration.getSingle().getOnline(), sv, ping))
                            .thenAcceptAsync(source::sendMessage),
                            () -> source.sendMessage(MiniMessage.miniMessage().deserialize(configuration.getServerNotFound())));
                    return Command.SINGLE_SUCCESS;
                })
            ).build();

            final BrigadierCommand command = new BrigadierCommand(infoCommand);
            final CommandMeta meta = commandManager.metaBuilder(command)
                    .aliases("vserverinfo", "vinfo")
                    .plugin(plugin)
                    .build();
            commandManager.register(meta, command);
    }

    @SuppressWarnings("SameReturnValue")
    private int sendAllInfo(final CommandSource source) {
        final var registeredServers = proxyServer.getAllServers();
        final Map<RegisteredServer, ServerPing> servers = new HashMap<>(registeredServers.size());
        CompletableFuture.allOf(registeredServers.parallelStream()
            .map(server -> server.ping(OPTIONS).handleAsync((ping, ex) -> servers.put(server, ping)))
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
                                placeholders.getOfflineServerComponent(
                                        configuration.getAll().getOffline(),
                                        server)
                        );
                    } else {
                        hasOnline.set(true);
                        onlineServers.append(
                                placeholders.getServerComponent(
                                        configuration.getAll().getOnline(),
                                        server,
                                        ping)
                        );
                    }
                });
                if (!hasOnline.get()) {
                    onlineServers.append(
                            MiniMessage.miniMessage().deserialize(
                                    configuration.getAll().getOnline().getNoneFound()));
                }
                if (!hasOffline.get()) {
                    offlineServers.append(
                            MiniMessage.miniMessage().deserialize(
                                    configuration.getAll().getOffline().getNoneFound()));
                }

                return placeholders.getInfoComponent(onlineServers.build(), offlineServers.build());
            }).thenAcceptAsync(source::sendMessage);
            return Command.SINGLE_SUCCESS;
    }
}
