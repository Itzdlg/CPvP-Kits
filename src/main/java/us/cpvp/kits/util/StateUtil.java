package us.cpvp.kits.util;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import us.cpvp.kits.CPvPKits;

import java.util.function.Function;

public class StateUtil {
    private StateUtil() {}

    private static final Function<CPvPKits, NamespacedKey> inventoryType
            = (plugin) -> new NamespacedKey(plugin, "inventory_type");

    private static final Function<CPvPKits, NamespacedKey> itemOfJoinInventory
            = (plugin) -> new NamespacedKey(plugin, "item_of_join_inventory");

    public static void setItemOfJoinInventory(CPvPKits plugin, ItemStack item, boolean value) {
        if (item.getItemMeta() == null) return;

        PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
        dataContainer.set(itemOfJoinInventory.apply(plugin), PersistentDataType.BYTE, (byte) (value ? 1 : 0));
    }

    public static boolean isItemOfJoinInventory(CPvPKits plugin, ItemStack item) {
        if (item.getItemMeta() == null) return true;

        PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
        if (!dataContainer.has(itemOfJoinInventory.apply(plugin), PersistentDataType.BYTE))
            return true;

        return dataContainer.get(itemOfJoinInventory.apply(plugin), PersistentDataType.BYTE) == 1;
    }

    public static void setInventoryType(CPvPKits plugin, Player player, String value) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        dataContainer.set(inventoryType.apply(plugin), PersistentDataType.STRING, value);
    }

    public static String getInventoryType(CPvPKits plugin, Player player) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        return dataContainer.get(inventoryType.apply(plugin), PersistentDataType.STRING);
    }
}
