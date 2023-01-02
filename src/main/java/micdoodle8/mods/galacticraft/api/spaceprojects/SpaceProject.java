package micdoodle8.mods.galacticraft.api.spaceprojects;

import java.util.HashMap;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class SpaceProject {
    // Maps UUIDs to other UUIDs. This allows users to join a team.
    public static HashMap<UUID, UUID> GlobalSpaceProjectTeam = new HashMap<>(100, 0.9f);

    public static void joinUserNetwork(UUID user_uuid_0, UUID user_uuid_1) {
        GlobalSpaceProjectTeam.put(user_uuid_0, user_uuid_1);
    }

    public static void strongCheckOrAddUser(EntityPlayer user) {
        strongCheckOrAddUser(user.getUniqueID());
    }

    public static void strongCheckOrAddUser(UUID user_uuid) {
        // Check if the user has a team. Add them if not.
        GlobalSpaceProjectTeam.putIfAbsent(user_uuid, user_uuid);
    }

    public static boolean checkUserTeam(UUID user_uuid) {
        return GlobalSpaceProjectTeam.containsKey(user_uuid);
    }

    public static String getUsernameFromUUID(UUID uuid) {
        return MinecraftServer.getServer().func_152358_ax().func_152652_a(uuid).getName();
    }

    public static UUID getUUIDFromUsername(String username) {
        return MinecraftServer.getServer()
                .func_152358_ax()
                .func_152655_a(username)
                .getId();
    }

    /**
     * Only use this if you know what you are doing. This will clear all current Spce Project Teams
     */
    public static void clearGlobalSpaceProjectInformationMap() {
        GlobalSpaceProjectTeam.clear();
    }
}
