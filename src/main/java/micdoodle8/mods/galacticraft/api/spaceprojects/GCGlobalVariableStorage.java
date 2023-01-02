package micdoodle8.mods.galacticraft.api.spaceprojects;

import com.google.common.collect.HashBiMap;

public abstract class GCGlobalVariableStorage {
    // Maps user IDs to usernames and vice versa. Seems redundant but this makes accessing this
    // easier in certain locations (like gt commands).
    public static HashBiMap<String, String> GlobalSpaceProjectName = HashBiMap.create(100);

    // Maps UUIDs to other UUIDs. This allows users to join a team.
    public static HashBiMap<String, String> GlobalSpaceProjectTeam = HashBiMap.create(100);
}
