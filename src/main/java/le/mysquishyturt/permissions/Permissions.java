package le.mysquishyturt.permissions;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import com.sk89q.minecraft.util.commands.CommandsManager;
import com.sk89q.minecraft.util.commands.MissingNestedCommandException;
import com.sk89q.minecraft.util.commands.WrappedCommandException;
import le.mysquishyturt.permissions.commands.GroupCommands;
import le.mysquishyturt.permissions.commands.PermissionsReloadCommand;
import le.mysquishyturt.permissions.configHandler.ConfigHandler;
import le.mysquishyturt.permissions.playerListener.PlayerListener;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Permissions extends JavaPlugin {

    private static Permissions instance;
    ConfigHandler configHandler;
    private CommandsManager<CommandSender> commands;

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
        setupCommands();
        getLogger().info("Permissions loaded.");
    }

    @Override
    public void onDisable() {
        configHandler.saveGroups();
        configHandler.saveAllPlayers();
        saveConfig();
        getLogger().info("Permissions saved and unloaded.");
    }

    private void setupCommands() {
        this.commands = new CommandsManager<CommandSender>() {
            @Override
            public boolean hasPermission(CommandSender sender, String perm) {
                return sender instanceof ConsoleCommandSender || sender.hasPermission(perm);
            }
        };

        CommandsManagerRegistration cmdRegister = new CommandsManagerRegistration(this, this.commands);

        cmdRegister.register(GroupCommands.GroupNestedCommand.class);
        cmdRegister.register(PermissionsReloadCommand.class);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        try {
            this.commands.execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                sender.sendMessage(ChatColor.RED + "Number expected, string received instead.");
            } else {
                sender.sendMessage(ChatColor.RED + e.getCause().getMessage());
            }
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }
        return true;
    }
}
