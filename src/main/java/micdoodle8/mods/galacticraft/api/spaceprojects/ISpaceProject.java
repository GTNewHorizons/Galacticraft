package micdoodle8.mods.galacticraft.api.spaceprojects;

import static micdoodle8.mods.galacticraft.api.spaceprojects.GCGlobalVariableStorage.*;

import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;

public interface ISpaceProject {

    default void joinUserNetwork(String user_uuid_0, String user_uuid_1) {
        GlobalSpaceProjectTeam.put(user_uuid_0, user_uuid_1);
    }

    default void strongCheckOrAddUser(EntityPlayer user) {
        strongCheckOrAddUser(user.getUniqueID().toString(), user.getDisplayName());
    }

    default void strongCheckOrAddUser(UUID user_uuid, String user_name) {
        strongCheckOrAddUser(user_uuid.toString(), user_name);
    }

    default void strongCheckOrAddUser(String user_uuid, String user_name) {

        // Check if the user has a team. Add them if not.
        GlobalSpaceProjectTeam.putIfAbsent(user_uuid, user_uuid);

        // If the username linked to the users fixed uuid is not equal to their current name then remove it.
        // This indicates that their username has changed.
        if (!(GlobalSpaceProjectName.getOrDefault(user_uuid, "").equals(user_name))) {
            String old_name = GlobalSpaceProjectName.get(user_uuid);
            GlobalSpaceProjectName.remove(old_name);
        }

        // Add UUID -> Name, Name -> UUID.
        GlobalSpaceProjectName.put(user_name, user_uuid);
        GlobalSpaceProjectName.put(user_uuid, user_name);
    }

    default String GetUsernameFromUUID(String uuid) {
        return GlobalSpaceProjectName.getOrDefault(GlobalSpaceProjectTeam.getOrDefault(uuid, ""), "");
    }

    default String getUUIDFromUsername(String username) {
        return GlobalSpaceProjectTeam.getOrDefault(GlobalSpaceProjectName.getOrDefault(username, ""), "");
    }

    static void clearGlobalSpaceElevatorInformationMap() {
        GlobalSpaceProjectName.clear();
        GlobalSpaceProjectTeam.clear();
    }
}
