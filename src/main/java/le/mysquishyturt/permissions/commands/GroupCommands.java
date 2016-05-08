package le.mysquishyturt.permissions.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import le.mysquishyturt.permissions.Permissions;
import le.mysquishyturt.permissions.configHandler.ConfigHandler;
import le.mysquishyturt.permissions.group.Group;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public class GroupCommands {

    @Command(aliases = "add", desc = "Add a player to a group", usage = "<player> <group>", min = 2, max = 2)
    public static void add(CommandContext args, CommandSender sender) throws CommandException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(args.getString(0));
        ConfigHandler handler = ConfigHandler.getInstance();

        if(player == null)
            throw new CommandException("Could not find player!");
        if(!handler.isGroup(args.getString(1)))
            throw new CommandException("That is not a valid group!");

        UUID uuid = player.getUniqueId();
        Group group = handler.getGroup(args.getString(1));

        handler.addPlayerToConfig(uuid);

        if(player.isOnline()) {
            handler.getPlayerPermission(uuid).addGroup(group);
            handler.reloadPlayer(uuid);
        } else {
            List<String> groups = Permissions.getInstance().getConfig().getStringList("players." + uuid + ".groups");
            groups.add(group.getName());
            Permissions.getInstance().getConfig().set("players." + uuid + ".groups", groups);
        }

        sender.sendMessage(ChatColor.DARK_AQUA + args.getString(0) + ChatColor.GRAY + " was added to " + ChatColor.DARK_AQUA + args.getString(1));
    }

    @Command(aliases = "remove", desc = "Remove a player from a group", usage = "<player> <group>", min = 2, max = 2)
    public static void remove(CommandContext args, CommandSender sender) throws CommandException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(args.getString(0));
        ConfigHandler handler = ConfigHandler.getInstance();

        if (player == null)
            throw new CommandException("Could not find player!");

        UUID uuid = player.getUniqueId();

        if (player.isOnline()) {
            if (handler.isGroup(args.getString(1))) {
                if (!handler.getPlayerPermission(uuid).hasGroup(args.getString(1))) {
                    throw new CommandException("Player is not in that group");
                }
                handler.getPlayerPermission(uuid).removeGroup(args.getString(1));
                handler.reloadPlayer(uuid);
            } else {
                throw new CommandException("Invalid group");
            }

        } else {
            List<String> groups = Permissions.getInstance().getConfig().getStringList("players." + uuid + ".groups");

            if (!groups.contains(args.getString(1))) {
                throw new CommandException("Player is not in that group!");
            }

            groups.remove(args.getString(1));
            Permissions.getInstance().getConfig().set("players." + uuid + ".groups", groups);
            sender.sendMessage(ChatColor.DARK_AQUA + args.getString(0) + ChatColor.GRAY + " removed from " + ChatColor.DARK_AQUA + args.getString(1));
        }
    }


    @Command(aliases = "list", desc = "Lists a player's groups", usage = "<player>", min = 1, max = 1)
        public static void list(CommandContext args, CommandSender sender) throws CommandException {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args.getString(0));
            ConfigHandler handler = ConfigHandler.getInstance();

            if(player == null)
                throw new CommandException("Could not find player!");

            UUID uuid = player.getUniqueId();
            List<String> groups = Permissions.getInstance().getConfig().getStringList("players." + uuid + ".groups");

            sender.sendMessage(ChatColor.DARK_AQUA + args.getString(0) + ChatColor.GRAY + "'s groups");

            for(String group : groups) {
                sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.DARK_AQUA + group);
            }
        }

    public static class GroupNestedCommand {
        @Command(aliases = {"group", "groups"}, desc = "Manage a player's permission groups.", usage = "<add, remove, list>")
        @CommandPermissions("permissions.groups.manage")
        @NestedCommand(GroupCommands.class)
        public static void group(final CommandContext cmd, final CommandSender sender) throws CommandException {
        }
    }
}
