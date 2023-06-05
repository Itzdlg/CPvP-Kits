package us.cpvp.kits.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import us.cpvp.kits.entities.LoadoutConfiguration;

public class ModifyLoadoutEvent extends PlayerEvent {
    private final LoadoutConfiguration loadout;

    public ModifyLoadoutEvent(Player who, LoadoutConfiguration loadout) {
        super(who);

        this.loadout = loadout;
    }

    public LoadoutConfiguration loadout() {
        return loadout;
    }

    private static final HandlerList HANDLERS = new HandlerList();
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }


}
