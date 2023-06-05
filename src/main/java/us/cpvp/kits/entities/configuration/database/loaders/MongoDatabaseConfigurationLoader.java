package us.cpvp.kits.entities.configuration.database.loaders;

import com.mongodb.client.result.UpdateResult;
import org.bukkit.configuration.ConfigurationSection;
import us.cpvp.kits.data.IDataManager;
import us.cpvp.kits.data.MongoDataManager;
import us.cpvp.kits.util.SectionUtil;

public class MongoDatabaseConfigurationLoader implements IDatabaseConfigurationLoader<UpdateResult> {
    @Override
    public IDataManager<UpdateResult> load(ConfigurationSection config) {
        if (!config.contains("type"))
            throw new IllegalArgumentException("Missing type property in provided configuration.");

        if (!config.getString("type").equalsIgnoreCase("mongo"))
            throw new IllegalArgumentException("The configuration section provided is not for a Mongo database.");

        if (!SectionUtil.containsAll(config, "url", "database", "connection"))
            throw new IllegalArgumentException("Missing required properties in provided configuration.");

        String url = config.getString("url");
        String database = config.getString("database");
        String collection = config.getString("collection");

        return new MongoDataManager(url, database, collection);
    }
}
