package me.anutley.dislink;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Path;

public class Config {

    public final static YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
            .path(Path.of("config.yml"))
            .build();
    private static CommentedConfigurationNode root = null;


    static {

        try {
            root = loader.load();
        } catch (IOException e) {
            System.err.println("An error occurred while loading this configuration: " + e.getMessage());
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            }
        }
    }

    public static CommentedConfigurationNode get() {
        return root;
    }

}
