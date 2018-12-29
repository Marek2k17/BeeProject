package versionhandlers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface VersionHandler {
    public void openAnvilMenu( ItemStack isFirstSlot, Player p, String sTitle );
}
