package io.github._4drian3d.vserverinfo.commands;

import com.google.inject.Inject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.spotify.futures.CompletableFutures;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.PingOptions;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import io.github._4drian3d.vserverinfo.ServerInfo;
import io.github._4drian3d.vserverinfo.configuration.Configuration;
import io.github._4drian3d.vserverinfo.utils.Placeholders;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

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
                    final CommandSource source = cmd.getSource();
                    if (server.equals("ALL")) {
                        return sendAllInfo(source);
                    }
                    proxyServer.getServer(server).ifPresentOrElse(sv ->
                            sv.ping(OPTIONS).handleAsync((ping, exception) -> ping == null
                                ? placeholders.offlineComponent(configuration.getSingle().getOffline(), sv)
                                : placeholders.onlineComponent(configuration.getSingle().getOnline(), sv, ping))
                            .thenAcceptAsync(source::sendMessage),
                            () -> source.sendMessage(miniMessage().deserialize(configuration.getServerNotFound())));
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
        final Collection<RegisteredServer> registeredServers = proxyServer.getAllServers();

        CompletableFutures.allAsList(registeredServers.parallelStream()
                .map(server -> server.ping(OPTIONS).handle((ping, ex) -> new Pair(server, ping)))
                .toList())
                .thenApply(pairs -> {
                    final TextComponent.Builder onlineServers = Component.text();
                    final TextComponent.Builder offlineServers = Component.text();
                    final AtomicBoolean hasOffline = new AtomicBoolean();
                    final AtomicBoolean hasOnline = new AtomicBoolean();
                    final Configuration.All allSection = configuration.getAll();

                    pairs.forEach((pair) -> {
                        if (pair.ping == null) {
                            hasOffline.set(true);
                            offlineServers.append(
                                    placeholders.offlineComponent(allSection.getOffline(), pair.server)
                            );
                        } else {
                            hasOnline.set(true);
                            onlineServers.append(
                                    placeholders.onlineComponent(allSection.getOnline(), pair.server, pair.ping)
                            );
                        }
                    });
                    if (!hasOnline.get()) {
                        onlineServers.append(miniMessage().deserialize(allSection.getOnline().getNoneFound()));
                    }
                    if (!hasOffline.get()) {
                        offlineServers.append(miniMessage().deserialize(allSection.getOffline().getNoneFound()));
                    }

                    return placeholders.infoComponent(onlineServers.build(), offlineServers.build());
                }).thenAccept(source::sendMessage);

            return Command.SINGLE_SUCCESS;
    }

    record Pair(RegisteredServer server, ServerPing ping) {}
}
