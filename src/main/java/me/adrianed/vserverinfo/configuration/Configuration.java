package me.adrianed.vserverinfo.configuration;

import java.util.List;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@SuppressWarnings("ALL")
@ConfigSerializable
public class Configuration {
    @Comment("Message to send if the selected server does not exists in Velocity Configuration")
    private String serverNotFound = "<gradient:#DBE6F6:#C5796D>This server does not exists</gradient>";
    @Comment("Formats for '/serverinfo' or '/serverinfo ALL' command")
    private All all = new All();
    @Comment("Formats for '/serverinfo <server>' command")
    private Single single = new Single();

    public String getServerNotFound() {
        return serverNotFound;
    }

    public All getAll() {
        return all;
    }

    public Single getSingle() {
        return single;
    }

    @ConfigSerializable
    public static class All {
        @Comment("Command Format")
        private List<String> format = List.of(
                "<gradient:#bdc3c7:#2c3e50>Server Info:</gradient>",
                "<aqua>Online Servers:</aqua> ",
                "<onlineservers>",
                "<red>Offline Servers:</red> ",
                "<offlineservers>"
        );
        private Online online = new Online();
        private Offline offline = new Offline();

        public List<String> getFormat() {
            return format;
        }

        public Online getOnline() {
            return online;
        }

        public Offline getOffline() {
            return offline;
        }

        @ConfigSerializable
        public static class Online implements OnlineFormat {
            @Comment("Sets the text formatting of each online server")
            private String format = "<gradient:#4ecdc4:#55670><server></gradient>";
            @Comment("Set the hover with the information to be displayed for each online server")
            private List<String> hover = List.of(
                    "<gradient:#be93c5:#7bc6cc>Players Count:</gradient> <white><count>",
                    "<gradient:#be93c5:#7bc6cc>Max Players:</gradient> <white><maxcount>",
                    "<gradient:#be93c5:#7bc6cc>Motd:</gradient>",
                    "<motd>"
            );
            @Comment("Message to display if there is no server Online")
            private String noneFound = "<gray>NONE";

            @Override
            public String getFormat() {
                return format;
            }

            @Override
            public List<String> getHover() {
                return hover;
            }

            public String getNoneFound() {
                return noneFound;
            }
        }

        @ConfigSerializable
        public static class Offline implements OfflineFormat {
            @Comment("Sets the text formatting of each offline server")
            private String format = "<gradient:#ee0979:#ff6a00><server></gradient>";
            @Comment("Set the hover with the information to be displayed for each offline server")
            private List<String> hover = List.of();
            @Comment("Message to display if there is no server offline")
            private String noneFound = "<gray>NONE";

            @Override
            public String getFormat() {
                return format;
            }

            @Override
            public List<String> getHover() {
                return hover;
            }

            public String getNoneFound() {
                return noneFound;
            }
        }

    }

    @ConfigSerializable
    public static class Single {
        private Online online = new Online();
        private Offline offline = new Offline();

        public Online getOnline() {
            return online;
        }

        public Offline getOffline() {
            return offline;
        }

        @ConfigSerializable
        public static class Online implements OnlineFormat {
            @Comment("Format to use in case the server is Online")
            private String format = "<gray>Online Server: <gradient:#4ecdc4:#55670><server></gradient>";
            @Comment("Hover to apply to show server information online")
            private List<String> hover = List.of(
                    "<gradient:#be93c5:#7bc6cc>Players Count:</gradient> <white><count>",
                    "<gradient:#be93c5:#7bc6cc>Max Players:</gradient> <white><maxcount>",
                    "<gradient:#be93c5:#7bc6cc>Motd:</gradient>",
                    "<motd>"
            );

            @Override
            public String getFormat() {
                return format;
            }

            @Override
            public List<String> getHover() {
                return hover;
            }
        }

        @ConfigSerializable
        public static class Offline implements OfflineFormat {
            @Comment("Format to use in case the server is Offline")
            private String format = "<dark_gray>Offline Server: <gradient:#ee0979:#ff6a00><server></gradient>";
            private List<String> hover = List.of("");

            @Override
            public String getFormat() {
                return format;
            }

            @Override
            public List<String> getHover() {
                return hover;
            }
        }
    }

    public static sealed interface FormatConfig permits OnlineFormat, OfflineFormat {
        String getFormat();

        List<String> getHover();
    }

    public static non-sealed interface OnlineFormat extends FormatConfig {}
    public static non-sealed interface OfflineFormat extends FormatConfig {}
}
