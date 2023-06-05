package us.cpvp.kits.inventories;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import us.cpvp.kits.entities.LoadoutConfiguration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public abstract class SelectInventory {
    private static final String[] WHITE_SKULL_URL = new String[] {
            "http://textures.minecraft.net/texture/ca516fbae16058f251aef9a68d3078549f48f6d5b683f19cf5a1745217d72cc",
            "http://textures.minecraft.net/texture/4698add39cf9e4ea92d42fadefdec3be8a7dafa11fb359de752e9f54aecedc9a",
            "http://textures.minecraft.net/texture/fd9e4cd5e1b9f3c8d6ca5a1bf45d86edd1d51e535dbf855fe9d2f5d4cffcd2",
            "http://textures.minecraft.net/texture/f2a3d53898141c58d5acbcfc87469a87d48c5c1fc82fb4e72f7015a3648058",
            "http://textures.minecraft.net/texture/d1fe36c4104247c87ebfd358ae6ca7809b61affd6245fa984069275d1cba763"
    };

    private static final String[] STONE_SKULL_URL = new String[] {
            "http://textures.minecraft.net/texture/31a9463fd3c433d5e1d9fec6d5d4b09a83a970b0b74dd546ce67a73348caab",
            "http://textures.minecraft.net/texture/acb419d984d8796373c9646233c7a02664bd2ce3a1d3476dd9b1c5463b14ebe",
            "http://textures.minecraft.net/texture/f8ebab57b7614bb22a117be43e848bcd14daecb50e8f5d0926e4864dff470",
            "http://textures.minecraft.net/texture/62bfcfb489da867dce96e3c3c17a3db7c79cae8ac1f9a5a8c8ac95e4ba3",
            "http://textures.minecraft.net/texture/ef4ecf110b0acee4af1da343fb136f1f2c216857dfda6961defdbee7b9528"
    };

    private final Player player;
    private final LoadoutConfiguration[] loadouts;

    public SelectInventory(Player player, LoadoutConfiguration[] loadouts) {
        this.player = player;
        this.loadouts = loadouts;
    }

    public abstract void onSelect(LoadoutConfiguration loadout, boolean hasPermission);

    public void open() {
        ChestGui gui = new ChestGui(3, ChatColor.DARK_BLUE + "Select A Loadout");
        gui.setOnGlobalClick(event -> event.setCancelled(true));

        StaticPane backgroundPane = new StaticPane(0, 0, 9, 3);

        ItemStack itemEtc = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta metaEtc = itemEtc.getItemMeta();
        metaEtc.setDisplayName(ChatColor.WHITE.toString());
        itemEtc.setItemMeta(metaEtc);

        backgroundPane.fillWith(itemEtc);

        StaticPane loadoutPane = new StaticPane(0, 0, 9, 3);

        for (int loadoutId = 1; loadoutId <= loadouts.length; loadoutId++) {
            LoadoutConfiguration loadout;
            if (loadouts[loadoutId - 1] == null)
                loadout = new LoadoutConfiguration(player.getUniqueId(), loadoutId, "Loadout " + loadoutId, false, new ItemStack[0]);
            else {
                loadout = loadouts[loadoutId - 1];
            }

            boolean hasPermission = loadoutId <= 3 || player.hasPermission("kits.loadout." + loadoutId);

            ItemStack itemLoadout = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta metaLoadout = (SkullMeta) itemLoadout.getItemMeta();
            metaLoadout.setDisplayName(
                    hasPermission ? (ChatColor.WHITE + loadout.name() + " (" + loadoutId + ")") : (ChatColor.RED + "Slot Locked")
            );

            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID(), "");
            try {
                profile.getTextures().setSkin(new URL(hasPermission ? WHITE_SKULL_URL[loadoutId - 1] : STONE_SKULL_URL[loadoutId - 1]));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            metaLoadout.setOwnerProfile(profile);

            itemLoadout.setItemMeta(metaLoadout);

            loadoutPane.addItem(new GuiItem(itemLoadout, (event) -> onSelect(loadout, hasPermission)), 1 + loadoutId, 1);
        }

        gui.addPane(loadoutPane);
        gui.addPane(backgroundPane);

        gui.show(player);
    }
}
