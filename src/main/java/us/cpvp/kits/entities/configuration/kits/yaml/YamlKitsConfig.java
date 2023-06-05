package us.cpvp.kits.entities.configuration.kits.yaml;

import me.epic.betteritemconfig.ItemFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import us.cpvp.kits.entities.configuration.exceptions.MissingFileException;
import us.cpvp.kits.entities.configuration.kits.IKitsConfig;
import us.cpvp.kits.entities.configuration.kits.ILoadoutItems;
import us.cpvp.kits.entities.configuration.exceptions.MissingConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Uses a backing ConfigurationSection to supply
 * configuration values.
 *
 * @implNote This implementation uses a backing
 * ConfigurationSection, which is not harmful
 * to performance because YamlConfiguration#load
 * will load the file properties into memory first.
 * This implementation DOES NOT use IO resources.
 *
 * @see IKitsConfig
 * @see ConfigurationSection
 */
public class YamlKitsConfig implements IKitsConfig {
    private final ConfigurationSection config;

    private final ItemStack[] defaultLoadout;
    private final ILoadoutItems loadoutItems;

    public YamlKitsConfig(File file) throws IOException, InvalidConfigurationException {
        if (!file.exists())
            throw new MissingFileException(file);

        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.load(file);

        this.config = yamlConfiguration;

        this.defaultLoadout = loadDefaultLoadout();
        this.loadoutItems = loadLoadoutItems();
    }

    public YamlKitsConfig(ConfigurationSection section) {
        this.config = section;

        this.defaultLoadout = loadDefaultLoadout();
        this.loadoutItems = loadLoadoutItems();
    }

    private ItemStack[] loadDefaultLoadout() {
        if (!config.contains("default-loadout"))
            throw new MissingConfigurationException(config, "default-loadout");

        ItemStack[] contents = new ItemStack[9 * 4];
        ConfigurationSection section = config.getConfigurationSection("default-loadout");
        for (String key : section.getKeys(false)) {
            int slot = Integer.valueOf(key);
            contents[slot] = loadoutItems().getItemStack(key);
        }

        return contents;
    }

    private ILoadoutItems loadLoadoutItems() {
        if (!config.contains("kit-items"))
            throw new MissingConfigurationException(config, "kit-items");

        Map<String, ItemStack> values = new HashMap<>();
        Map<String, Integer> slots = new HashMap<>();
        Map<Integer, ItemStack> items = new HashMap<>();

        ConfigurationSection section = config.getConfigurationSection("kit-items");
        for (String id : section.getKeys(false)) {
            ConfigurationSection itemSection = section.getConfigurationSection(id);
            if (!itemSection.contains("display_slot") || !itemSection.contains("item"))
                throw new MissingConfigurationException(itemSection, "display_slot");

            int displaySlot = itemSection.getInt("display_slot");
            ItemStack item = ItemFactory.DEFAULT.read(itemSection.getConfigurationSection("item"));

            id = id.toLowerCase();

            values.put(id, item);
            slots.put(id, displaySlot);
            items.put(displaySlot, item);
        }

        return new ILoadoutItems() {
            @Override
            public Set<String> getNames() {
                return values.keySet();
            }

            public Set<Integer> getSlots() {
                return items.keySet();
            }

            @Override
            public ItemStack getItemStack(String id) {
                return values.get(id.toLowerCase());
            }

            @Override
            public ItemStack getItemStack(int slot) {
                return items.get(slot);
            }

            @Override
            public int getSlot(String id) {
                return slots.get(id.toLowerCase());
            }
        };
    }

    @Override
    public ItemStack[] defaultLoadout() {
        return defaultLoadout;
    }

    @Override
    public ILoadoutItems loadoutItems() {
        return loadoutItems;
    }
}
