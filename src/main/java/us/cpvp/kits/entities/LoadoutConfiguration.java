package us.cpvp.kits.entities;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class LoadoutConfiguration {
    private final UUID playerId;
    private final int loadoutId;

    private final String name;
    private final boolean isPublic;
    private final ItemStack[] contents;

    public LoadoutConfiguration(UUID playerId, int loadoutId, String name, boolean isPublic, ItemStack[] contents) {
        this.playerId = playerId;
        this.loadoutId = loadoutId;
        this.name = name;
        this.isPublic = isPublic;
        this.contents = contents;
    }

    public UUID playerId() {
        return playerId;
    }

    public int loadoutId() {
        return loadoutId;
    }

    public String name() {
        return name;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public ItemStack[] contents() {
        return contents;
    }
}
