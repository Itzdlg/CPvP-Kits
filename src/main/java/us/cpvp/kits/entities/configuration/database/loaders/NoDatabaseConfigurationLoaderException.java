package us.cpvp.kits.entities.configuration.database.loaders;

public class NoDatabaseConfigurationLoaderException extends RuntimeException {
    public NoDatabaseConfigurationLoaderException(String type) {
        super("No database configuration loader was found for the type " + type);
    }
}
