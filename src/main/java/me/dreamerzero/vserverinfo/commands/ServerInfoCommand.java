package me.dreamerzero.vserverinfo.commands;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;

import me.dreamerzero.vserverinfo.utils.TemplateUtils;
import net.kyori.adventure.text.Component;

public class ServerInfoCommand {
    public static void brigadierServerInfo(final ProxyServer proxy){
        LiteralCommandNode<CommandSource> serverinfoCommand = LiteralArgumentBuilder
            .<CommandSource>literal("serverinfo")
            .executes(context -> {
                Set<RegisteredServer> offlineservers = new HashSet<>();
                Map<RegisteredServer, ServerPing> onlineservers = new HashMap<>();
                CompletableFuture.allOf(proxy.getAllServers().stream()
                    .map(server -> server.ping().handleAsync((ping, exception) -> {
                        if(exception != null) {
                            offlineservers.add(server);
                            return null;
                        } else {
                            onlineservers.put(server, ping);
                            return ping;
                        }
                    })).filter(ping -> ping.join() != null)
                    .toArray(CompletableFuture[]::new)).thenAccept(a->{

                        var onlineserverscomponent = Component.text();
                        if(onlineservers.isEmpty()){
                            onlineserverscomponent.append(TemplateUtils.getNotFoundComponent());
                        } else {
                            onlineservers.entrySet().forEach(entry  ->
                                onlineserverscomponent.append(
                                    TemplateUtils.getServerComponent(
                                        entry.getKey(),
                                        entry.getValue())
                            ));
                        }

                        var offlineserverscomponent = Component.text();
                        if(offlineservers.isEmpty()){
                            offlineserverscomponent.append(TemplateUtils.getNotFoundComponent());
                        } else {
                            offlineservers.forEach(offlineserver  ->
                                offlineserverscomponent.append(
                                    TemplateUtils.getOfflineServerComponent(offlineserver))
                            );
                        }

                        context.getSource().sendMessage(
                            TemplateUtils.getInfoComponent(
                                onlineserverscomponent.build(),
                                offlineserverscomponent.build()));
                    });
                return 1;
            })
            .build();

            CommandManager manager = proxy.getCommandManager();
            BrigadierCommand infocommand = new BrigadierCommand(serverinfoCommand);
            CommandMeta servermeta = manager.metaBuilder(infocommand)
                .aliases("vserverinfo")
                .build();
            proxy.getCommandManager().register(servermeta, infocommand);
    }

    private ServerInfoCommand(){}
}
