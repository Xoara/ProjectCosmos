package com.xoarasol.projectcosmos.configuration;

import com.xoarasol.projectcosmos.ProjectCosmos;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {

    private final Path path;
    private final FileConfiguration config;

    public Config(String name) {
        path = Paths.get(ProjectCosmos.getPlugin().getDataFolder().toString(), name);
        config = YamlConfiguration.loadConfiguration(path.toFile());
        reloadConfig();
    }

    private void createConfig() {
        try {
            Files.createFile(path);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private void reloadConfig() {
        if (Files.notExists(path)) {
            createConfig();
        }

        try {
            config.load(path.toFile());
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
