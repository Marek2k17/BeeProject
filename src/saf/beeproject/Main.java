package saf.beeproject;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import versionhandlers.VersionHandler;
import versionhandlers.VersionHandler_v1_13_2;

public class Main extends JavaPlugin {
    private final BeeController beeController = new BeeController(this);
    private VersionHandler versionHandler;
    
    private Path pProbs = Paths.get("./Bee/config.properties");
    FileConfiguration config;
    
    @Override
    public void onEnable(){
        config = getConfig();
        config.addDefault("recipe", null);
        config.options().copyDefaults(true);
        saveConfig();
        
        versionHandler = new VersionHandler_v1_13_2();
        
        getServer().getPluginManager().registerEvents(beeController, this);
        getCommand("bee").setExecutor(new BeeCommand(this));
        loadRecipeFromConfig();
        
        System.out.println("Startet Bees.");
    }
 
    @Override
    public void onDisable(){
        beeController.packAllBees();
        
        System.out.println("Ended Bees.");
    }
    
    public void saveRecipeToConfig( Map<Character,String> mapRecipe ) {
        config.set("recipe", mapRecipe);
        saveConfig();
    }
    
    private void loadRecipeFromConfig() {
        if( config.get("recipe") != null ) {
            NamespacedKey key = new NamespacedKey(this, "bee_head");
            ShapedRecipe recipe = new ShapedRecipe(key, getBeeController().getBeeHead());

            recipe.shape("ABC", "DEF", "GHI");
            Object o = config.get("recipe");
            if( o instanceof MemorySection ) {
                ((MemorySection)o).getValues(false).entrySet().forEach((entry) -> {
                    recipe.setIngredient( entry.getKey().charAt(0), getMaterialFromString((String)entry.getValue()) );
                });
            }
            
            Bukkit.addRecipe(recipe);
        }
    }
    
    //I am using this becouse Material.getMaterial() wont give me the right material.
    private Material getMaterialFromString( String sName ) {
        for( Material m : Material.values() ) {
            if( m.name().equals(sName) ) {
                return m;
            }
        }
        return null;
    }
    
    public void giveHeadToPlayer( Player p ) {
        p.getInventory().addItem(this.beeController.getBeeHead());
    }
    
    public BeeController getBeeController() {
        return this.beeController;
    }
    
    public VersionHandler getVersionHandler() {
        return this.versionHandler;
    }
    
    public String getServerVersion() {
        String sReturn = Bukkit.getServer().getClass().getPackage().getName();
        return sReturn.substring(sReturn.lastIndexOf(".")+1);
    }
}