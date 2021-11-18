package me.dreamerzero.vserverinfo.configuration;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

public class Config {
    private static Configuration configuration;
    public static Configuration getConfig(){
        return configuration;
    }

    public static void loadConfig(@NotNull Path path, @NotNull Logger logger){
        File configFile = new File(path.toFile(), "config.conf");
        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
            .defaultOptions(opts -> opts
                .shouldCopyDefaults(true)
                .header("""
                VServerInfo | by 4drian3d

                Review the MiniMessage guide at:
                Official Guide: https://docs.adventure.kyori.net/minimessage#format
                Spanish Guide: https://gist.github.com/4drian3d/9ccce0ca1774285e38becb09b73728f3

                Check the placeholders available for each message at: 
                """)
            )
            .file(configFile)
            .build();

        try {
            final CommentedConfigurationNode node = loader.load();
            configuration = node.get(Configuration.class);
            node.set(Configuration.class, configuration);
            loader.save(node);
        } catch (ConfigurateException exception){
            logger.error("Could not load configuration: {}", exception.getMessage());
        }
    }

    @ConfigSerializable
    public static class Configuration {

        @Comment("Here you can set the format of the main plugin command")
        private List<String> info_format = List.of(
            "<green>Server Info:</green>",
            "<aqua>Online Servers:</aqua> ",
            "<onlineservers>",
            "<red>Offline Servers:</red> ",
            "<offlineservers>"
        );

        @Comment("Set the hover with the information to be displayed for each online server")
        private List<String> hover_available_format = List.of(
            "<aqua>Players Count: <white><count>",
            "<aqua>Max Players: <white><maxcount>",
            "<aqua>Motd:",
            "<motd>"
        );

        @Comment("Sets the text formatting of each online server")
        private String server_available_format = "<green><server></green>";

        @Comment("Sets the text formatting of each offline server")
        private String server_not_available_format = "<red><server></red>";

        public String getAvailableFormat(){
            return this.server_available_format;
        }

        public List<String> getAvailableHover(){
            return this.hover_available_format;
        }

        public String getNotAvailableFormat(){
            return this.server_not_available_format;
        }

        public List<String> getinfoFormat(){
            return this.info_format;
        }
    }

    private Config(){}
}
