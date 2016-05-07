package le.mysquishyturt.permissions;

import le.mysquishyturt.permissions.configHandler.ConfigHandler;
import le.mysquishyturt.permissions.playerListener.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Permissions extends JavaPlugin {

    private static Permissions instance;
    ConfigHandler configHandler;

    public static Permissions getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        configHandler = ConfigHandler.getInstance();
        getConfig().options().copyDefaults(true);
        saveConfig();
        configHandler.loadGroups();
        configHandler.loadAllPlayers();
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getLogger().info("Permissions loaded.");
    }

    @Override
    public void onDisable() {
        configHandler.saveGroups();
        configHandler.saveAllPlayers();
        saveConfig();
        getLogger().info("Permissions saved and unloaded.");
    }
}
