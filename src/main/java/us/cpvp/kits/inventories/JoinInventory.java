package us.cpvp.kits.inventories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.cpvp.kits.CPvPKits;
import us.cpvp.kits.entities.LoadoutConfiguration;
import us.cpvp.kits.util.StateUtil;

public class JoinInventory {
    public static class Handler implements Listener {
        private CPvPKits plugin;
        public Handler(CPvPKits plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        private void onRightClick(PlayerInteractEvent event) {
            Player player = event.getPlayer();

            ItemStack item = event.getItem();
            if (item == null) return; // ?

            if (!StateUtil.isItemOfJoinInventory(plugin, item))
                return;

            Material type = item.getType();

            if (type == Material.CLOCK) {
                event.setCancelled(true);

                plugin.dataManager().getLoadoutConfigurations(player.getUniqueId()).thenAccept(loadouts -> {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        SelectInventory select = new SelectInventory(player, loadouts) {
                            @Override
                            public void onSelect(LoadoutConfiguration loadout, boolean hasPermission) {
                                if (!hasPermission) return;

                                plugin.openModifyLoadoutInventory(player, loadout);
                            }
                        };

                        select.open();
                    });
                });
            } else if (type == Material.PAPER) {
                event.setCancelled(true);

                plugin.dataManager().getSelectedLoadoutId(player.getUniqueId()).thenAccept(id -> {
                    plugin.dataManager().getLoadoutConfiguration(player.getUniqueId(), id).thenAccept(loadout -> {
                        plugin.selectLoadout(player, loadout);
                    });
                });
            }
        }

        @EventHandler
        private void onDrop(PlayerDropItemEvent event) {
            if (StateUtil.isItemOfJoinInventory(plugin, event.getItemDrop().getItemStack()))
                event.setCancelled(true);
        }
    }

    public static void open(CPvPKits plugin, Player player) {
        player.closeInventory();

        ItemStack itemGUI = new ItemStack(Material.CLOCK);
        ItemMeta metaGUI = itemGUI.getItemMeta();
        metaGUI.setDisplayName(ChatColor.GOLD + "Modify Loadout");
        itemGUI.setItemMeta(metaGUI);
        StateUtil.setItemOfJoinInventory(plugin, itemGUI, true);

        ItemStack itemDefault = new ItemStack(Material.PAPER);
        ItemMeta metaDefault = itemDefault.getItemMeta();
        metaDefault.setDisplayName(ChatColor.WHITE + "Default Loadout");
        itemDefault.setItemMeta(metaDefault);
        StateUtil.setItemOfJoinInventory(plugin, itemDefault, true);

        Inventory inventory = player.getInventory();

        for (int i = 0; i < 9 * 4; i++) {
            inventory.setItem(i, new ItemStack(Material.AIR));
        }

        inventory.setItem(3, itemGUI);
        inventory.setItem(5, itemDefault);
    }
}