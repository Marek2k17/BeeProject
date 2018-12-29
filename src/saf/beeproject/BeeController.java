package saf.beeproject;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class BeeController implements Listener{
    private final String sBeeMenuTitle = ChatColor.GOLD + "Bee Menu";
    private final static String sRenameBee = "§2Rename Bee";
    private final static String sPackBeeToInventory = "§2Pack to Inventory";
    private final String sDuplicateBee = "You already have a Bee following you!";
    private final String sNoBeeAlive = "You don't have any Bees alive! :(";
    private final String sRenameError = "You don't have any Bees alive or in your Hand to rename.";
    private final String sRecipeEditor = ChatColor.GOLD + "Edit Bee recipe";
    private final List<String> lore = Lists.newArrayList(
                    ChatColor.GRAY + "This is a friendly Bee.", 
                    ChatColor.GRAY + "It will follow you arround.", 
                    ChatColor.GRAY + "Just rightclick the ground!"
            );
    
    private Map<Player,BeeHead> mapFollowingBees = new HashMap();
    private Map<BeeHead,Integer> mapMovementControllerShedulerIds = new HashMap();
    
    private ItemStack isHead = null;
    private SkullMeta meta = null;
    private String sBeeHead = "http://textures.minecraft.net/texture/947322f831e3c168cfbd3e28fe925144b261e79eb39c771349fac55a8126473";
    
    Main m;
    
    BeeController( Main m ) {
        this.m = m;
    }
    
    public ItemStack getBeeHead( String sName ) {
        this.isHead = new ItemStack( Material.PLAYER_HEAD );
        meta = (SkullMeta)this.isHead.getItemMeta();

        meta.setDisplayName(sName);

        meta.setLore( lore );

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", this.sBeeHead).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }

        meta.addItemFlags( ItemFlag.HIDE_ENCHANTS );
        this.isHead.setItemMeta(meta);
        this.isHead.addUnsafeEnchantment(Enchantment.LUCK, 1);
        
        return isHead;
    }
    
    public ItemStack getBeeHead() {
        return getBeeHead("Mr Bee");
    }
    
    public void openBeeMenu( Player p ) {
        Inventory inv = Bukkit.createInventory(null, InventoryType.DROPPER, sBeeMenuTitle);
        
        ItemStack isBtn;
        ItemMeta imBtn;
        
        isBtn = new ItemStack( Material.CHEST );
        imBtn = isBtn.getItemMeta();
        imBtn.setDisplayName(sPackBeeToInventory);
        imBtn.setLore(Lists.newArrayList(
            ChatColor.GREEN + "Packs the Bee back to inventory."
        ));
        isBtn.setItemMeta(imBtn);
        inv.setItem(3, isBtn);
        
        isBtn = new ItemStack( Material.NAME_TAG );
        imBtn = isBtn.getItemMeta();
        imBtn.setDisplayName(sRenameBee);
        imBtn.setLore(Lists.newArrayList(
            ChatColor.GREEN + "Opens menu to rename your Bee."
        ));
        isBtn.setItemMeta(imBtn);
        inv.setItem(5, isBtn);
        
        p.closeInventory();
        p.openInventory(inv);
    }
    
    public void packBeeToInventory( Player p ) {
        BeeHead bee = this.mapFollowingBees.get(p);
        int taskId = mapMovementControllerShedulerIds.get(bee);
        Bukkit.getScheduler().cancelTask(taskId);
        bee.getArmorStand().remove();
        p.getInventory().addItem(getBeeHead(bee.getName()));
        mapMovementControllerShedulerIds.remove(bee);
        mapFollowingBees.remove(p);
        p.closeInventory();
    }
    
    public void openRenameBeeMenu( Player p ) {
        if( mapFollowingBees.containsKey(p) ) {
            m.getVersionHandler().openAnvilMenu(getBeeHead(mapFollowingBees.get(p).getName()), p, sRenameBee);
        }
    }
    
    public void renameBee( Player p, String sName ) {
        if( this.mapFollowingBees.containsKey(p) ) {
            this.mapFollowingBees.get(p).rename(sName);
        }
        else {
            ItemStack isHand = p.getInventory().getItemInMainHand();
            if( isHand != null && isHand.hasItemMeta() ) {
                if(isHand.getItemMeta().getLore().equals(this.lore) ) {
                    ItemMeta meta = isHand.getItemMeta();
                    meta.setDisplayName(sName);
                    isHand.setItemMeta(meta);
                }
                else {
                    p.sendMessage(sRenameError);
                }
            }
            else {
                p.sendMessage(sRenameError);
            }
        }
    }
    
    public void teleportBeeToPlayer( Player p ) {
        if( this.mapFollowingBees.containsKey(p) ) {
            this.mapFollowingBees.get(p).teleportToLocation(p.getLocation());
        }
        else {
            p.sendMessage(sNoBeeAlive);
        }
    }
    
    public void openRecipeEditor( Player p ) {
        Inventory inv = Bukkit.createInventory(null, InventoryType.DROPPER, sRecipeEditor);
        p.closeInventory();
        p.openInventory(inv);
    }
    
    public void packAllBees() {
        mapFollowingBees.keySet().forEach((p) -> {
            packBeeToInventory(p);
        });
    }
    
    @EventHandler
    public void onPlayerQuit( PlayerQuitEvent e ) {
        if( mapFollowingBees.containsKey(e.getPlayer()) ) {
            packBeeToInventory(e.getPlayer());
        }
    }
    
    @EventHandler
    public void onRecipeSave( InventoryCloseEvent e ) {
        if( e.getInventory().getType().equals(InventoryType.DROPPER) ) {
            if( e.getInventory().getTitle() != null ) {
                if( e.getInventory().getTitle().equals(this.sRecipeEditor) ) {
                    
                    boolean bValidRecipe = false;
                    
                    NamespacedKey key = new NamespacedKey(m, "bee_head");
                    ShapedRecipe recipe = new ShapedRecipe(key, getBeeHead());

                    recipe.shape("ABC", "DEF", "GHI");
                    char[] c = { 'A','B','C','D','E','F','G','H','I' };
                    Map<Character,String> mapRecipe = new HashMap();
                    
                    for( int i = 0; i < 9; i++ ) {
                        Material mat = (e.getInventory().getItem(i) != null) ? e.getInventory().getItem(i).getType() : Material.AIR;
                        recipe.setIngredient(c[i], mat);
                        mapRecipe.put(c[i], mat.name());
                        
                        if( e.getInventory().getItem(i) != null )
                            bValidRecipe = true;
                    }
                    
                    if( bValidRecipe ) {
                        Iterator<Recipe> serverRecipes = Bukkit.recipeIterator();
                        while( serverRecipes.hasNext() ) {
                            Recipe recipeLoop = serverRecipes.next();
                            if( recipeLoop.getResult().equals(getBeeHead()) ) {
                                serverRecipes.remove();
                            }
                        }
                        
                        bValidRecipe = Bukkit.addRecipe(recipe);
                    }
                    
                    if( !bValidRecipe ) {
                        e.getPlayer().sendMessage("Your recipe is not valid or could not be addeded.");
                    }
                    else {
                        m.saveRecipeToConfig(mapRecipe);
                        e.getPlayer().sendMessage("Your Recipe has been updated!");
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onBeeSpawn( PlayerInteractEvent e ) {
        if( e.getAction().equals(Action.RIGHT_CLICK_BLOCK) ) {
            if( e.getItem() != null && e.getItem().hasItemMeta() ) {
                if( e.getItem().getItemMeta().getLore().equals(this.lore) ) {

                    if( !mapFollowingBees.containsKey(e.getPlayer()) ) {
                        BeeHead head = new BeeHead(e.getPlayer(), getBeeHead(e.getItem().getItemMeta().getDisplayName()), e.getClickedBlock().getLocation().add(0,1,0));
                        mapFollowingBees.put(e.getPlayer(), head);
                        mapMovementControllerShedulerIds.put(head, Bukkit.getScheduler().runTaskTimer(m, head.getMovementController(), 0, 20).getTaskId());
                        e.getItem().setAmount(e.getItem().getAmount()-1);
                    }
                    else {
                        e.getPlayer().sendMessage(this.sDuplicateBee);
                    }
                    e.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onBeeRightClick( PlayerArmorStandManipulateEvent e ) {
        if( e.getHand().equals(EquipmentSlot.HAND) ) {
                ArmorStand stand = e.getRightClicked();
                boolean bFound = false;
                
                for( BeeHead bee : mapFollowingBees.values() ) {
                    if( bee.getArmorStand().equals(stand) ) {
                        if( bee.isOwnedByPlayer(e.getPlayer()) ) {
                            openBeeMenu(e.getPlayer());
                        }
                        else {
                            e.getPlayer().damage(2);
                        }
                        
                        bFound = true;
                        break;
                    }
                }
                
                e.setCancelled(bFound);
        }
    }
    
    @EventHandler
    public void onBeeRename( InventoryCloseEvent e ) {
        if( e.getInventory() != null ) {
            if( e.getInventory().getType().equals(InventoryType.ANVIL) ) {
                if( e.getInventory().getItem(0) != null ) {
                    if( e.getInventory().getItem(0).hasItemMeta() ) {
                        if( e.getInventory().getItem(0).getItemMeta().getLore().equals(this.lore) ) {
                            AnvilInventory anvil = (AnvilInventory)e.getInventory();
                            if( !anvil.getRenameText().isEmpty() ) {
                                String sName = anvil.getRenameText();
                                renameBee( (Player)e.getPlayer(), sName );
                                e.getInventory().setItem(0, null);
                            }
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onBeeAnvilClick( InventoryClickEvent  e) {
        if( e.getInventory() != null ) {
            if( e.getInventory().getType().equals(InventoryType.ANVIL) ) {
                if( e.getInventory().getItem(0) != null ) {
                    if( e.getInventory().getItem(0).hasItemMeta() ) {
                        if( e.getInventory().getItem(0).getItemMeta().getLore().equals(this.lore) ) {
                            e.setCancelled( true );
                            if( e.getRawSlot() == 2 ) {
                                e.getWhoClicked().closeInventory();
                            }
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onBeeMenuClick( InventoryClickEvent e ) {
        if( e.getInventory() != null && e.getInventory().getTitle() != null ) {
            if( e.getInventory().getTitle().equals(sBeeMenuTitle) ) {
                if( e.getRawSlot() < 9 ) {
                    e.setCancelled(true);
                    if( e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() ) {
                        Player p = (Player)e.getWhoClicked();
                        
                        switch( e.getCurrentItem().getItemMeta().getDisplayName() ) {
                            case sPackBeeToInventory:
                                packBeeToInventory((Player)e.getWhoClicked());
                            break;
                            
                            case sRenameBee:
                                openRenameBeeMenu(p);
                            break;
                        }
                    }
                }
            }
        }
    }
}
