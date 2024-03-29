package io.github._4drian3d.vserverinfo.configuration;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Path;

public final class Loader {
    @Inject
    @DataDirectory
    private Path path;
    @Inject
    private Logger logger;

    public Configuration loadConfig()  {
        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .defaultOptions(opts -> opts
                        .shouldCopyDefaults(true)
                        .header("""
                                VServerInfo | by 4drian3d
                                Review the MiniMessage guide at:
                                https://docs.adventure.kyori.net/minimessage#format
                        
                                Check the placeholders available for each message at:
                                https://github.com/4drian3d/VServerInfo/wiki/Placeholders
                                """)
                )
                .path(path.resolve("config.conf"))
                .build();

        try {
            final CommentedConfigurationNode node = loader.load();
            final Configuration config = node.get(Configuration.class);
            node.set(Configuration.class, config);
            loader.save(node);
            return config;
        } catch (ConfigurateException exception){
            logger.error("Could not load config.conf file", exception);
            return null;
        }
    }
}
