package le.mysquishyturt.permissions.permission;

import le.mysquishyturt.permissions.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.PermissionAttachment;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerPermission {

    UUID uuid;
    PermissionAttachment attachment;
    Map<String, Group> groups;
    Map<String, Permission> permissions;

    public PlayerPermission(UUID uuid, PermissionAttachment attachment) {
        this.uuid = uuid;
        this.attachment = attachment;
        groups = new HashMap<>();
        permissions = new HashMap<>();
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public PermissionAttachment getAttachment() {
        return this.attachment;
    }

    public void addGroup(Group group) {
        groups.put(group.getName(), group);
    }

    public void removeGroup(String name) {
        groups.remove(name);
    }

    public boolean hasGroup(String name) {
        return groups.containsKey(name);
    }

    public Collection<Group> getGroups() {
        return groups.values();
    }

    public void addPermission(Permission permission) {
        permissions.put(permission.getName(), permission);
    }

    public void removePermission(String name) {
        permissions.remove(name);
    }

    public boolean hasPermission(String name) {
        return permissions.containsKey(name);
    }

    public Collection<Permission> getPermissions() {
        return permissions.values();
    }

    public void buildPermissions() {
        List<Group> permGroups = groups.values().stream().sorted((a, b) -> Integer.compare(a.getPriority(), b.getPriority())).collect(Collectors.toList());
        for (Group group : permGroups) {
            group.applyPermission(attachment);
        }
        for (Permission permission : permissions.values()) {
            attachment.setPermission(permission.getName(), permission.getValue());
        }
    }

    public void removePermissions() {
        for (Map.Entry permission : attachment.getPermissions().entrySet()) {
            attachment.unsetPermission(permission.getKey().toString());
        }
    }

    public void saveToConfig(FileConfiguration fileConfig) {
        String path = "players." + uuid + ".";
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        fileConfig.set(path + "name", player.getName());
        fileConfig.set(path + "groups", groups.values().stream().map(Group::getName).collect(Collectors.toList()));
        fileConfig.set(path + "permissions", permissions.values().stream().map(permission -> permission.getName() + ":" + permission.getValue()).collect(Collectors.toList()));
    }
}
