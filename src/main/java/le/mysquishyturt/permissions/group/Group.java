package le.mysquishyturt.permissions.group;

import le.mysquishyturt.permissions.permission.Permission;
import org.bukkit.permissions.PermissionAttachment;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group {

    String name;
    int priority;
    Map<String, Permission> permissions;
    Map<String, Group> inheritances;

    public Group(String name) {
        this.name = name;
        permissions = new HashMap<>();
        inheritances = new HashMap<>();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
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

    public void addInheritance(Group group) {
        inheritances.put(group.getName(), group);
    }

    public void removeInheritance(String name) {
        inheritances.remove(name);
    }

    public boolean isInherited(String name) {
        return inheritances.containsKey(name);
    }

    public Collection<Group> getGroups() {
        return inheritances.values();
    }

    public void applyPermission(PermissionAttachment attachment) {
        for (Group group : inheritances.values()) {
            group.applyPermission(attachment);
        }
        for (Permission permission : permissions.values()) {
            attachment.setPermission(permission.getName(), permission.getValue());
        }
    }
}
