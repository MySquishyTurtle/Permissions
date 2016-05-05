package le.mysquishyturt.permissions.configHandler;

import le.mysquishyturt.permissions.Permissions;
import le.mysquishyturt.permissions.group.Group;
import le.mysquishyturt.permissions.permission.Permission;
import le.mysquishyturt.permissions.permission.PlayerPermission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ConfigHandler {

    private static ConfigHandler instance;
    Permissions plugin;
    Map<UUID, PlayerPermission> onlinePlayers;
    Map<String, Group> groups;

    private ConfigHandler() {
        plugin = Permissions.getInstance();
        onlinePlayers = new HashMap<>();
        groups = new HashMap<>();
    }

    public static ConfigHandler getInstance() {
        if (instance == null) {
            instance = new ConfigHandler();
        }
        return instance;
    }

    public void loadPlayerPermissions(UUID uuid) {
        Map<String, Object> playerNode = plugin.getConfig().getConfigurationSection("players." + uuid).getValues(false);
        PlayerPermission playerPermission = new PlayerPermission(uuid, Bukkit.getPlayer(uuid).addAttachment(plugin));
        if (playerNode != null) {
            for (String group : (List<String>) playerNode.get("groups")) {
                playerPermission.addGroup(groups.get(group));
            }
            for (Permission permission : getPermissionList((List<String>) playerNode.get("permissions"))) {
                playerPermission.addPermission(permission);
            }
        }
        playerPermission.buildPermissions();
        onlinePlayers.put(uuid, playerPermission);
    }

    public void savePlayerPermissions(UUID uuid) {
        PlayerPermission playerPermission = onlinePlayers.get(uuid);
        playerPermission.saveToConfig(plugin.getConfig());
        playerPermission.removePermissions();
        onlinePlayers.remove(uuid);
    }

    public void loadGroups() {
        Map<String, Object> groupz = plugin.getConfig().getConfigurationSection("groups").getValues(false);
        for (Map.Entry group : groupz.entrySet()) {
            Group newGroup =  new Group(group.getKey().toString());
            MemorySection section = (MemorySection) group.getValue();
            getPermissionList((List<String>) section.get("permissions")).stream().forEach(newGroup::addPermission);
            newGroup.setPriority((int) section.get("priority"));
            groups.put(newGroup.getName(), newGroup);
        }
        for (Map.Entry group : groupz.entrySet()) {
            Group currentGroup = groups.get(group.getKey());
            MemorySection section = (MemorySection) group.getValue();
            for (String name : (List<String>) section.get("inheritance")) {
                if (groups.containsKey(name)) {
                    currentGroup.addInheritance(groups.get(name));
                }
            }
        }

    }

    public void saveGroups() {
        for (String name : groups.keySet()) {
            Group currentGroup = groups.get(name);
            String path = "groups." + name + ".";
            FileConfiguration fileConfig = plugin.getConfig();
            fileConfig.set(path + "permissions", currentGroup.getPermissions().stream().map(permission -> permission.getName() + ":" + permission.getValue()).collect(Collectors.toList()));
            fileConfig.set(path + "inheritance", currentGroup.getGroups().stream().map(Group::getName).collect(Collectors.toList()));
            fileConfig.set(path + "priority", currentGroup.getPriority());
        }
        groups.clear();
    }

    public void reload() {
        saveAllPlayers();
        saveGroups();
        plugin.saveConfig();
        loadGroups();
        loadAllPlayers();
    }

    public void loadAllPlayers() {
        Bukkit.getOnlinePlayers().stream().forEach(player -> loadPlayerPermissions(player.getUniqueId()));
    }

    public void saveAllPlayers() {
        onlinePlayers.keySet().stream().forEach(this::savePlayerPermissions);
    }

    private List<Permission> getPermissionList(List<String> perms) {
        List<Permission> results = new ArrayList<>();
        String[] splits;
        for (String node : perms) {
            splits = node.split(":");
            results.add(new Permission(splits[0], Boolean.valueOf(splits[1])));
        }
        return results;
    }
}
