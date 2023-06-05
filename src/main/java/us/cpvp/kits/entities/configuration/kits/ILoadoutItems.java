package us.cpvp.kits.entities.configuration.kits;

import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface ILoadoutItems {
    Set<String> getNames();
    Set<Integer> getSlots();

    int getSlot(String name);
    ItemStack getItemStack(int slot);
    ItemStack getItemStack(String name);

    default Map<Integer, ItemStack> slotMap() {
        return getNames().stream().collect(Collectors.toMap(
                this::getSlot,
                this::getItemStack
        ));
    }
}