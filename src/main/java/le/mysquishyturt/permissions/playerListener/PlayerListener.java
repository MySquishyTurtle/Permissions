package le.mysquishyturt.permissions.playerListener;

import le.mysquishyturt.permissions.configHandler.ConfigHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ConfigHandler.getInstance().addPlayerToConfig(event.getPlayer().getUniqueId());
        ConfigHandler.getInstance().loadPlayerPermissions(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        ConfigHandler.getInstance().savePlayerPermissions(event.getPlayer().getUniqueId());
    }
}
