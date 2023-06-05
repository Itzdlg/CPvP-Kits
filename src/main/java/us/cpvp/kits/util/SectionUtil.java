package us.cpvp.kits.util;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class SectionUtil {
    private SectionUtil() {}

    public static boolean containsAll(ConfigurationSection section, String... keys) {
        List<String> configKeys = new ArrayList<>(section.getKeys(true));
        // configKeys.replaceAll(String::toLowerCase); # Case Insensitive. YAML is inappropriate for this method

        for (String key : keys)
            if (!configKeys.contains(key.toLowerCase()))
                return false;

        return true;
    }
}
