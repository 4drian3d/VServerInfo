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
            "<gradient:#bdc3c7:#2c3e50>Server Info:</gradient>",
            "<aqua>Online Servers:</aqua> ",
            "<onlineservers>",
            "<red>Offline Servers:</red> ",
            "<offlineservers>"
        );

        @Comment("Set the hover with the information to be displayed for each online server")
        private List<String> hover_available_format = List.of(
            "<gradient:#be93c5:#7bc6cc>Players Count:</gradient> <white><count>",
            "<gradient:#be93c5:#7bc6cc>Max Players:</gradient> <white><maxcount>",
            "<gradient:#be93c5:#7bc6cc>Motd:</gradient>",
            "<motd>"
        );

        @Comment("Sets the text formatting of each online server")
        private String server_available_format = "<gradient:#4ecdc4:#55670><server></gradient>";

        @Comment("Sets the text formatting of each offline server")
        private String server_not_available_format = "<gradient:#ee0979:#ff6a00><server></gradient>";

        @Comment("Message to send if no server was found in a category")
        private String servers_not_found = "<gradient:#DBE6F6:#C5796D>No server found in this category</gradient>";

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

        public String getNotFoundFormat(){
            return this.servers_not_found;
        }
    }

    private Config(){}
}
