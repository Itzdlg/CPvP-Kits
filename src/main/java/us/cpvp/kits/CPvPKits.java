package us.cpvp.kits;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import us.cpvp.kits.data.IDataManager;
import us.cpvp.kits.entities.LoadoutConfiguration;
import us.cpvp.kits.entities.configuration.database.DatabaseConfig;
import us.cpvp.kits.entities.configuration.database.loaders.NoDatabaseConfigurationLoaderException;
import us.cpvp.kits.entities.configuration.exceptions.MissingConfigurationException;
import us.cpvp.kits.entities.configuration.kits.IKitsConfig;
import us.cpvp.kits.entities.configuration.kits.yaml.YamlKitsConfig;
import us.cpvp.kits.inventories.JoinInventory;
import us.cpvp.kits.inventories.ModifyInventory;
import us.cpvp.kits.listeners.JoinListener;

import java.io.File;
import java.io.IOException;

public class CPvPKits extends JavaPlugin {
    private IKitsConfig kitsConfig;
    private IDataManager<?> dataManager = null;

    @Override
    public void onEnable() {
        try {
            loadKitsConfig();

            if (dataManager == null) // Can be set by another plugin after onLoad()
                loadDatabaseConfig();
        } catch (IOException | InvalidConfigurationException ex) {
            getLogger().severe("Unable to load kits.yml or database.yml");
            ex.printStackTrace();
        } catch (NoDatabaseConfigurationLoaderException | MissingConfigurationException ex) {
            getLogger().severe(ex.getMessage());

            Bukkit.getPluginManager().disablePlugin(this);
        }

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new JoinListener(this), this);
        pluginManager.registerEvents(new JoinInventory.Handler(this), this);
    }

    private void loadKitsConfig() throws IOException, InvalidConfigurationException {
        File file = new File(getDataFolder(), "kits.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            saveResource("kits.yml", true);
        }

        this.kitsConfig = new YamlKitsConfig(file);
    }

    private void loadDatabaseConfig() throws IOException, InvalidConfigurationException, NoDatabaseConfigurationLoaderException {
        File file = new File(getDataFolder(), "database.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            saveResource("database.yml", true);
        }

        this.dataManager = DatabaseConfig.load(file);
    }

    public void openModifyLoadoutInventory(Player player, LoadoutConfiguration loadout) {
        player.closeInventory();

        ModifyInventory inventory = new ModifyInventory(dataManager, kitsConfig.loadoutItems(), player, loadout, true, true);
        inventory.open();
    }

    public void selectLoadout(Player player, LoadoutConfiguration loadout) {
        player.closeInventory();

        player.sendMessage("selected loadout " + loadout.loadoutId());
    }

    public IKitsConfig kitsConfig() {
        return kitsConfig;
    }

    public IDataManager<?> dataManager() {
        return dataManager;
    }

    public void dataManager(IDataManager<?> dataManager) {
        if (this.dataManager != null)
            throw new IllegalStateException("The data manager may only be set by another plugin during the load phase.");

        this.dataManager = dataManager;
    }
}
