package me.dreamerzero.vserverinfo.configuration;

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

    public static Configuration loadConfig(@NotNull Path path, @NotNull Logger logger){
        final Path configFile = path.resolve("config.conf");
        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
            .defaultOptions(opts -> opts
                .shouldCopyDefaults(true)
                .header("""
                VServerInfo | by 4drian3d

                Review the MiniMessage guide at:
                Official Guide: https://docs.adventure.kyori.net/minimessage#format
                Spanish Guide: https://gist.github.com/4drian3d/9ccce0ca1774285e38becb09b73728f3

                Check the placeholders available for each message at:
                https://github.com/4drian3d/VServerInfo/wiki/Placeholders
                """)
            )
            .path(configFile)
            .build();

        Configuration configuration;

        try {
            final CommentedConfigurationNode node = loader.load();
            configuration = node.get(Configuration.class);
            node.set(Configuration.class, configuration);
            loader.save(node);
            return configuration;
        } catch (ConfigurateException exception){
            logger.error("Could not load configuration: ", exception);
            return null;
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
        private String serverNotExists = "<gradient:#DBE6F6:#C5796D>This server does not exists</gradient>";

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

        public String getNotExistsFormat(){
            return this.serverNotExists;
        }
    }

    private Config(){}
}
