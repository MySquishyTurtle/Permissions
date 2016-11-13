## **Permissions** V1.0.0


An example of a permissions file can be seen below. It is important that the format be followed, otherwise the permissions will not load properly.
You must include whether the permission is given or taken from the player by adding "true" or "false" at the end of the permission node. Note that the permissions used here may or may not exist, they were just examples.

~~~
groups:
 default:  
   permissions:   
   - bukkit.command.me:false 
   inheritance: []
   priority: 0
 moderator:
   permissions:
   - bukkit.command.kick:true
   inheritance:
   - default
   priority: 1
 admin:
   permissions:
   - bukkit.command.mute:true
   inheritance:
   - moderator
   priority: 2
 developer:
   permissions:
   - bukkit.command.ban:true
   inheritance:
   - admin
   priority: 3  
~~~
   
   Below is a player section generated after a player logs into the server. This is where the player's rank is stored. You can also give the player specific permissions for just them here.


~~~
players:
 ff0b4c92-edc5-4474-a145-04a433c9e485:
   name: MySquishyTurtle
   groups:
   - developer
   permissions: []
~~~

  There is a command that can be used for adding and removing a player from groups. It is ```/group <add, remove> <player> <group>```.
