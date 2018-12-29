package saf.beeproject;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MovementUpdater implements Runnable{
    private Player p;
    private BeeHead bee;
    private Location playerLocation;
    
    MovementUpdater( Player p, BeeHead bee ) {
        this.p = p;
        this.bee = bee;
        this.playerLocation = p.getLocation();
    }

    @Override
    public void run() {
        if( !p.getLocation().equals(playerLocation) ) {
            playerLocation = p.getLocation();
            bee.rotateToLocation(playerLocation);
            
            if( bee.getBeeLocation() != null ) {
                if( playerLocation.distance(bee.getBeeLocation()) >= 5 ) {
                    if( playerLocation.distance(bee.getBeeLocation()) > 64 ) {
                        bee.teleportToLocation( playerLocation );
                    }
                    else {
                        bee.moveToLocation( playerLocation );
                    }
                    
                }
            }
        }
    }
    
}
