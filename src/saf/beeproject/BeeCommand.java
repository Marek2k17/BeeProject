package saf.beeproject;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BeeCommand implements CommandExecutor {
    private final Main m;
    
    BeeCommand( Main m ) {
        this.m = m;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if( label.equals("bee") ) {
            if( args.length > 0 ) {
                switch( args[0] ) {
                    case "help":
                        sender.sendMessage("/bee rename <Name> - Renames your Bee.");
                        sender.sendMessage("/bee pack - Packs the be to your Inventory.");
                        sender.sendMessage("/bee tphere - Teleports the Bee to you.");
                        sender.sendMessage("/bee setrecipe - Opens Menu to enter new recipe.");
                        break;
                        
                    case "rename":
                        if( args.length == 1) {
                            sender.sendMessage("Bee: I need a name, don't leave it blank. (Try /bee help)");
                        }
                        else {
                            String sName = "";
                            for( int i = 1; i < args.length; i++ ) {
                                sName += args[i] + " ";
                            }
                            
                            //Replacing last blankspace
                            sName = sName.substring(0, sName.length() - 1);
                            
                            if( !sName.isEmpty() ) {
                                this.m.getBeeController().renameBee( (Player)sender, sName );
                            }
                        }
                    break;
                        
                    case "pack":
                        this.m.getBeeController().packBeeToInventory((Player)sender);
                    break;
                        
                    case "tphere":
                        this.m.getBeeController().teleportBeeToPlayer((Player)sender);
                    break;
                    
                    case "setrecipe":
                        this.m.getBeeController().openRecipeEditor((Player)sender);
                    break;
                }
            }
            else {
                m.giveHeadToPlayer((Player)sender);
            }
            
        }
        
        return true;
    }
    
}
