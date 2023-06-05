package us.cpvp.kits.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import us.cpvp.kits.CPvPKits;
import us.cpvp.kits.entities.configuration.kits.IKitsConfig;
import us.cpvp.kits.inventories.JoinInventory;

import java.util.Arrays;
import java.util.Objects;

public class JoinListener implements Listener {
    private CPvPKits plugin;
    private IKitsConfig config;
    public JoinListener(CPvPKits plugin) {
        this.plugin = plugin;
        this.config = plugin.kitsConfig();
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.dataManager().getLoadoutConfigurations(player.getUniqueId()).thenAcceptAsync(loadouts -> {
            if (Arrays.stream(loadouts).allMatch(Objects::isNull)) {
                plugin.dataManager().setName(player.getUniqueId(), 1, "Default Loadout").join();
                plugin.dataManager().setPublic(player.getUniqueId(), 1, false).join();
                plugin.dataManager().setContents(player.getUniqueId(), 1, config.defaultLoadout()).join();
                plugin.dataManager().setSelectedLoadoutId(player.getUniqueId(), 1).join();
            }
        });

        JoinInventory.open(plugin, event.getPlayer());
    }
}
