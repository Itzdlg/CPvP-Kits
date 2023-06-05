package us.cpvp.kits.entities.configuration.database.loaders;

import org.bukkit.configuration.ConfigurationSection;
import us.cpvp.kits.data.IDataManager;

public interface IDatabaseConfigurationLoader<TUpdate> {
    IDataManager<TUpdate> load(ConfigurationSection section);
}
