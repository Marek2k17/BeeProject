package versionhandlers;

import net.minecraft.server.v1_13_R2.BlockPosition;
import net.minecraft.server.v1_13_R2.ChatMessage;
import net.minecraft.server.v1_13_R2.Container;
import net.minecraft.server.v1_13_R2.ContainerAnvil;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.PacketPlayOutOpenWindow;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class VersionHandler_v1_13_2 implements VersionHandler{

    @Override
    public void openAnvilMenu(ItemStack isFirstSlot, Player p, String sTitle) {
      EntityPlayer entityPlayer = ((CraftPlayer)p).getHandle();
      ContainerAnvil anvil = new ContainerAnvil(entityPlayer.inventory, entityPlayer.world, new BlockPosition(0,0,0), null);
      anvil.checkReachable = false;
      Inventory inv = ((Container) anvil).getBukkitView().getTopInventory();
      inv.setItem(0, isFirstSlot);
      
      int containerId = entityPlayer.nextContainerCounter();
     
      ((CraftPlayer)p).getHandle().playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerId, "minecraft:anvil", new ChatMessage("Enter data:", new Object[0]), 0));
   
      entityPlayer.activeContainer = anvil;
      entityPlayer.activeContainer.windowId = containerId;
      entityPlayer.activeContainer.addSlotListener(entityPlayer);
    }
    
}
