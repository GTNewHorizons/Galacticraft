package micdoodle8.mods.galacticraft.api.spaceprojects;

import java.util.HashMap;

import com.google.common.collect.HashBiMap;

public abstract class GCGlobalVariableStorage {
    // Maps user IDs to usernames and vice versa. Seems redundant but this makes accessing this
    // easier in certain locations (like gt commands).
    public static HashBiMap<String, String> GlobalSpaceProjectName = HashBiMap.create(100);

    // Maps UUIDs to other UUIDs. This allows users to join a team.
    public static HashMap<String, String> GlobalSpaceProjectTeam = new HashMap<>(100, 0.9f);
}
