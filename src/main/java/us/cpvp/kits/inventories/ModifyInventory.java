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
import us.cpvp.kits.data.IDataManager;
import us.cpvp.kits.entities.LoadoutConfiguration;
import us.cpvp.kits.entities.configuration.kits.ILoadoutItems;
import us.cpvp.kits.events.ModifyLoadoutContentsEvent;
import us.cpvp.kits.util.ItemUtil;

public class ModifyInventory {
    private final Player player;
    private final LoadoutConfiguration loadout;

    private final IDataManager<?> dataManager;
    private final ILoadoutItems kitItemSelection;

    private final boolean publishEvents, listenCancelled;

    public ModifyInventory(IDataManager<?> dataManager, ILoadoutItems kitItemSelection, Player player, LoadoutConfiguration loadout, boolean publishEvents, boolean listenCancelled) {
        this.player = player;
        // this.playerId = player.getUniqueId();
        this.loadout = loadout;
        this.dataManager = dataManager;
        this.kitItemSelection = kitItemSelection;

        this.publishEvents = publishEvents;
        this.listenCancelled = listenCancelled;
    }

    public void open() {
        ChestGui gui = new ChestGui(6, ChatColor.DARK_BLUE + "Modify \"" + loadout.name() + "\"");
        gui.setOnGlobalClick(event -> event.setCancelled(true));

        StaticPane line = new StaticPane(0, 4, 9, 1);

        ItemStack itemEtc = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        ItemMeta metaEtc = itemEtc.getItemMeta();
        metaEtc.setDisplayName(ChatColor.WHITE.toString());
        itemEtc.setItemMeta(metaEtc);

        for (int i = 0; i < 9; i++)
            line.addItem(new GuiItem(itemEtc, event -> event.setCancelled(true)), i, 0);

        StaticPane kit = new StaticPane(0, 0, 9, 4);

        for (int i = 0; i < loadout.contents().length; i++) {
            kit.addItem(new GuiItem(loadout.contents()[i], event -> {
                if (event.getRawSlot() >= 9 * 4 && event.getRawSlot() < 9 * 5) return;

                if (ItemUtil.isEmpty(event.getCursor())) { // Clicking to obtain item
                    player.setItemOnCursor(event.getCurrentItem());
                    event.setCurrentItem(new ItemStack(Material.AIR));
                    return;
                }

                // Clicking to set item
                ItemStack oldCursor = event.getCursor();
                player.setItemOnCursor(event.getCurrentItem());
                event.setCurrentItem(oldCursor);

            }), i % 9, i / 9);
        }

        StaticPane control = new StaticPane(0, 5, 9, 1);

        ItemStack itemSave = new ItemStack(Material.LIME_WOOL);
        ItemMeta metaSave = itemSave.getItemMeta();
        metaSave.setDisplayName(ChatColor.GREEN + "Save Kit");
        itemSave.setItemMeta(metaSave);

        ItemStack itemCancel = new ItemStack(Material.RED_WOOL);
        ItemMeta metaCancel = itemCancel.getItemMeta();
        metaCancel.setDisplayName(ChatColor.RED + "Revert Kit");
        itemCancel.setItemMeta(metaCancel);

        ItemStack itemRename = new ItemStack(Material.ANVIL);
        ItemMeta metaRename = itemRename.getItemMeta();
        metaRename.setDisplayName(ChatColor.GRAY + "Rename Kit");
        itemRename.setItemMeta(metaRename);

        ItemStack itemDefault = new ItemStack(Material.PAPER);
        ItemMeta metaDefault = itemDefault.getItemMeta();
        metaDefault.setDisplayName(ChatColor.YELLOW + "Make Default");
        itemDefault.setItemMeta(metaDefault);

        ItemStack itemPublic = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta metaPublic = itemPublic.getItemMeta();
        metaPublic.setDisplayName(ChatColor.RED + "Not Public");
        itemPublic.setItemMeta(metaPublic);

        ItemStack itemTrash = new ItemStack(Material.BARRIER);
        ItemMeta metaTrash = itemTrash.getItemMeta();
        metaTrash.setDisplayName(ChatColor.RED + "Trash Item");
        itemTrash.setItemMeta(metaTrash);

        control.addItem(new GuiItem(itemTrash, event -> {
            player.setItemOnCursor(new ItemStack(Material.AIR));
        }), 8, 0);

        // Control Outline
        //    xx-xxx--x

        control.addItem(new GuiItem(itemSave, event -> {
            ItemStack[] contents = new ItemStack[9 * 4];
            for (int slot = 0; slot < 9 * 4; slot++)
                contents[slot] = gui.getInventory().getItem(slot);

            if (publishEvents) {
                ModifyLoadoutContentsEvent newEvent = new ModifyLoadoutContentsEvent(player, loadout, contents);
                Bukkit.getPluginManager().callEvent(newEvent);

                if (listenCancelled && event.isCancelled())
                    return;
            }

            dataManager.setContents(player.getUniqueId(), loadout.loadoutId(), contents);
        }), 0, 0);

        control.addItem(new GuiItem(itemCancel, event -> {
            player.closeInventory();

/*            ModifyInventory newSession = new ModifyInventory(dataManager, kitItemSelection, player, loadout);
            newSession.open();*/
        }), 1, 0);

        StaticPane selection = new StaticPane(0, 6, 9, 3);

        for (int slot : kitItemSelection.getSlots()) {
            ItemStack item = kitItemSelection.getItemStack(slot);

            selection.addItem(new GuiItem(item, event -> {
                player.setItemOnCursor(item);
            }), slot % 9, (slot / 9) + 1);
        }

        gui.addPane(kit);
        gui.addPane(line);
        gui.addPane(control);
        gui.addPane(selection);

        gui.show(player);
    }
}