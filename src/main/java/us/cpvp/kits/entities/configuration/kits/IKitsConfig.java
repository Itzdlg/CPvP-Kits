package us.cpvp.kits.entities.configuration.kits;

import org.bukkit.inventory.ItemStack;

public interface IKitsConfig {
    ItemStack[] defaultLoadout();

    ILoadoutItems loadoutItems();
}