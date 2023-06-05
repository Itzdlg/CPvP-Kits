package us.cpvp.kits.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import us.cpvp.kits.entities.LoadoutConfiguration;

public class ModifyLoadoutContentsEvent extends ModifyLoadoutEvent implements Cancellable {
    private final ItemStack[] newContents;
    private boolean cancelled;

    public ModifyLoadoutContentsEvent(Player who, LoadoutConfiguration loadout, ItemStack[] newContents) {
        super(who, loadout);

        this.newContents = newContents;
    }

    public ItemStack[] newContents() {
        return newContents;
    }

    private static final HandlerList HANDLERS = new HandlerList();
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }


    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
