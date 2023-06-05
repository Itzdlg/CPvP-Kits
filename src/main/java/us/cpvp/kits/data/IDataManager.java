package us.cpvp.kits.data;

import org.bukkit.inventory.ItemStack;
import us.cpvp.kits.entities.LoadoutConfiguration;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IDataManager<TUpdate> {
    CompletableFuture<LoadoutConfiguration[]> getLoadoutConfigurations(UUID playerId);
    CompletableFuture<LoadoutConfiguration> getLoadoutConfiguration(UUID playerId, int loadoutId);

    CompletableFuture<Integer> getSelectedLoadoutId(UUID playerId);
    CompletableFuture<TUpdate> setSelectedLoadoutId(UUID playerId, int loadoutId);

    CompletableFuture<String> getName(UUID playerId, int loadoutId);
    CompletableFuture<TUpdate> setName(UUID playerId, int loadoutId, String name);

    CompletableFuture<Boolean> isPublic(UUID playerId, int loadoutId);
    CompletableFuture<TUpdate> setPublic(UUID playerId, int loadoutId, boolean isPublic);

    CompletableFuture<ItemStack[]> getContents(UUID playerId, int loadoutId);
    CompletableFuture<TUpdate> setContents(UUID playerId, int loadoutId, ItemStack[] contents);

    CompletableFuture<TUpdate> deleteLoadout(UUID playerId, int loadoutId);
}
