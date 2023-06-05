package us.cpvp.kits.entities.configuration.database;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import us.cpvp.kits.data.IDataManager;
import us.cpvp.kits.entities.configuration.database.loaders.IDatabaseConfigurationLoader;
import us.cpvp.kits.entities.configuration.database.loaders.MongoDatabaseConfigurationLoader;
import us.cpvp.kits.entities.configuration.database.loaders.NoDatabaseConfigurationLoaderException;
import us.cpvp.kits.entities.configuration.exceptions.MissingConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseConfig<TUpdate> {
    public static final Map<String, IDatabaseConfigurationLoader<?>> STORAGE_TYPES = new HashMap<>();

    static {
        STORAGE_TYPES.put("mongo", new MongoDatabaseConfigurationLoader());
    }

    public static IDataManager<?> load(File file) throws IOException, InvalidConfigurationException, NoDatabaseConfigurationLoaderException {
        YamlConfiguration config = new YamlConfiguration();
        config.load(file);

        if (!config.contains("type"))
            throw new MissingConfigurationException(file, "type");

        String providedType = config.getString("type");

        for (String type : STORAGE_TYPES.keySet()) {
            if (!providedType.equalsIgnoreCase(type))
                continue;

            IDatabaseConfigurationLoader<?> loader = STORAGE_TYPES.get(type);
            return loader.load(config);
        }

        throw new NoDatabaseConfigurationLoaderException(providedType);
    }
}
