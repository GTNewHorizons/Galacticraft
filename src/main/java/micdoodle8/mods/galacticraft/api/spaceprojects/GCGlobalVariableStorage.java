package micdoodle8.mods.galacticraft.api.spaceprojects;

import java.util.HashMap;

public abstract class GCGlobalVariableStorage {
    // Maps user IDs to usernames and vice versa. Seems redundant but this makes accessing this
    // easier in certain locations (like gt commands).
    public static HashMap<String, String> GlobalSpaceProjectName = new HashMap<>(100, 0.9f);

    // Maps UUIDs to other UUIDs. This allows users to join a team.
    public static HashMap<String, String> GlobalSpaceProjectTeam = new HashMap<>(100, 0.9f);
}
