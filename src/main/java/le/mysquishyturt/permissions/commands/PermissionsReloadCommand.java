package le.mysquishyturt.permissions.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import le.mysquishyturt.permissions.configHandler.ConfigHandler;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public class PermissionsReloadCommand {

    @Command(aliases = {"permissions", "permission", "pms"}, desc = "Permissions base command.", min = 1, max = 1)
    @CommandPermissions("permissions.perms.reload")
    public static void permissions(CommandContext cmd, CommandSender sender) {
        if (cmd.getString(0).equalsIgnoreCase("reload")) {
            ConfigHandler handler = ConfigHandler.getInstance();
            handler.reload();
            sender.sendMessage(ChatColor.DARK_AQUA + "Permissions reloaded.");
        }
    }
}
