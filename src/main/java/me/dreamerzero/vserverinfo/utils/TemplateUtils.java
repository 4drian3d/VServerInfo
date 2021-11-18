package me.dreamerzero.vserverinfo.utils;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;

import org.jetbrains.annotations.NotNull;

import me.dreamerzero.vserverinfo.configuration.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
import net.kyori.adventure.text.minimessage.transformation.TransformationType;

public class TemplateUtils {
    private static final MiniMessage mmwithouthover = MiniMessage.builder()
        .transformations(transformations ->
            transformations
                .clear()
                .add(TransformationType.COLOR)
                .add(TransformationType.GRADIENT)
                .add(TransformationType.DECORATION)
                .add(TransformationType.CLICK_EVENT)
                .add(TransformationType.RAINBOW)
                .add(TransformationType.TRANSLATABLE)
            .build()
        ).build();
    private static final MiniMessage regularmm = MiniMessage.miniMessage();

    private static TemplateResolver getServerTemplates(final RegisteredServer server, final ServerPing ping){
        final ServerPing.Builder pingbuilder = ping.asBuilder();
        var serverinfo = server.getServerInfo();
        return TemplateResolver.templates(
            Template.template("server", serverinfo.getName()),
            Template.template("motd", ping.getDescriptionComponent()),
            Template.template("count", String.valueOf(pingbuilder.getOnlinePlayers())),
            Template.template("maxcount", String.valueOf(pingbuilder.getMaximumPlayers())),
            Template.template("name", serverinfo.getName()),
            Template.template("ip", String.valueOf(serverinfo.getAddress().getAddress().getHostAddress()))
        );
    }

    private static TemplateResolver getOfflineServerTemplates(final RegisteredServer server){
        var serverinfo = server.getServerInfo();
        return TemplateResolver.templates(
            Template.template("server", serverinfo.getName()),
            Template.template("name", serverinfo.getName()),
            Template.template("count", String.valueOf(server.getPlayersConnected().size())),
            Template.template("ip", String.valueOf(serverinfo.getAddress().getAddress().getHostAddress()))
        );
    }

    public static Component getServerComponent(final RegisteredServer server, final ServerPing ping){
        Component component = mmwithouthover.deserialize(
            Config.getConfig().getAvailableFormat(),
            getServerTemplates(server, ping));
        var hover = Component.text();

        Config.getConfig().getAvailableHover().forEach(line ->
            hover.append(mmwithouthover.deserialize(line, getServerTemplates(server, ping))
                .append(Component.newline()))
        );

        return component.hoverEvent(HoverEvent.showText(hover.build())).append(Component.space());
    }

    public static Component getOfflineServerComponent(final RegisteredServer server){
        return regularmm.deserialize(
            Config.getConfig().getNotAvailableFormat(),
            getOfflineServerTemplates(server)).append(Component.space());
    }

    public static Component getInfoComponent(@NotNull Component online, @NotNull Component offline){
        TemplateResolver templates = TemplateResolver.templates(
            Template.template("onlineservers", online),
            Template.template("offlineservers", offline)
        );

        var infocomponent = Component.text();

        Config.getConfig().getinfoFormat().forEach(line ->
            infocomponent
                .append(regularmm.deserialize(line, templates))
                .append(Component.newline())
        );

        return infocomponent.build();
    }

    public static Component getNotFoundComponent(){
        return regularmm.deserialize(Config.getConfig().getNotAvailableFormat());
    }

    private TemplateUtils(){}
}
