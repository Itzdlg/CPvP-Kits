package us.cpvp.kits.entities.configuration.exceptions;

import org.bukkit.configuration.ConfigurationSection;

import java.io.File;

public class MissingConfigurationException extends RuntimeException {
    String config, key;

    public MissingConfigurationException(File config, String key) {
        super("Missing configuration for %s in %s".formatted(key, config.getName()));

        this.config = config.getName();
        this.key = key;
    }

    public MissingConfigurationException(ConfigurationSection config, String key) {
        super("Missing configuration for %s in %s".formatted(key, config.getName()));

        this.config = config.getName();
        this.key = key;
    }

    public String config() {
        return config;
    }

    public String key() {
        return key;
    }
}
