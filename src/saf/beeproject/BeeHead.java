package saf.beeproject;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class BeeHead{
    private boolean bIsSpawned = false;
    private final MovementUpdater rotationUpdater;
    private ArmorStand armorStand = null;
    private String sName = "Mr Bee";
    private Player owner;
    
    BeeHead(Player p, ItemStack bee, Location locSpawn) {
        this.armorStand = (ArmorStand)p.getWorld().spawnEntity(locSpawn, EntityType.ARMOR_STAND);
        this.owner = p;
        this.sName = bee.getItemMeta().getDisplayName();
        
        armorStand.setSmall(true);
        armorStand.setBasePlate(false);
        armorStand.setCanPickupItems(false);
        armorStand.setCollidable(false);
        armorStand.setGravity(true);
        armorStand.setInvulnerable(true);
        armorStand.setVisible(false);
        armorStand.setRemoveWhenFarAway(false);
        armorStand.setCustomName(ChatColor.GOLD + sName);
        armorStand.setCustomNameVisible(true);
        
        armorStand.setHelmet(bee);
        armorStand.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, Integer.MAX_VALUE, 2, true, false), true);
        
        this.rotationUpdater = new MovementUpdater(p, this);
    }
    
    public void rotateToPlayer( Player p ) {
        rotateToLocation(p.getLocation());
    }
    
    public void rotateToLocation( Location loc ) {
        Location newLoc = armorStand.getLocation();
        
        newLoc.setDirection(loc.toVector().subtract(newLoc.toVector()));
        
        armorStand.teleport(newLoc);
    }
    
    public void moveToLocation( Location loc ) {
        Location newLoc = armorStand.getLocation();
        newLoc.setDirection(loc.toVector().subtract(newLoc.toVector()));
        armorStand.setVelocity(newLoc.getDirection().add(new Vector(0,0.33,0)));
    }
    
    public void teleportToLocation( Location loc ) {
        armorStand.teleport(loc);
    }
    
    public void rename( String sName ) {
        this.sName = sName;
        this.getArmorStand().setCustomName( ChatColor.GOLD + sName);
    }
    
    public MovementUpdater getMovementController() {
        return this.rotationUpdater;
    }
    
    public ArmorStand getArmorStand() {
        return this.armorStand;
    }
    
    public boolean isOwnedByPlayer( Player p ) {
        return this.owner.equals(p);
    }
    
    public Location getBeeLocation() {
        return (this.armorStand != null) ? this.armorStand.getLocation() : null;
    }
    
    public String getName() {
        return ChatColor.stripColor(sName);
    }
}
