package micdoodle8.mods.galacticraft.core.util;

import java.util.HashMap;

import net.minecraft.launchwrapper.Launch;

import com.google.common.collect.Maps;

import micdoodle8.mods.galacticraft.core.obfuscation.FieldObfuscationEntry;
import micdoodle8.mods.galacticraft.core.obfuscation.MethodObfuscationEntry;
import micdoodle8.mods.galacticraft.core.obfuscation.ObfuscationEntry;

public class VersionUtil {

    private static boolean deobfuscated = true;
    private static final HashMap<String, ObfuscationEntry> nodemap = Maps.newHashMap();

    // Used in GCPlayerHandler etc
    public static final String KEY_FIELD_FLOATINGTICKCOUNT = "floatingTickCount";
    public static final String KEY_FIELD_MUSICTICKER = "mcMusicTicker";

    public static final String KEY_FIELD_CAMERA_ZOOM = "cameraZoom";
    public static final String KEY_FIELD_CAMERA_YAW = "cameraYaw";
    public static final String KEY_FIELD_CAMERA_PITCH = "cameraPitch";

    public static final String KEY_METHOD_ORIENT_CAMERA = "orientCamera";

    static {
        try {
            deobfuscated = Launch.classLoader.getClassBytes("net.minecraft.world.World") != null;
        } catch (final Exception e) {
            e.printStackTrace();
        }

        // Same for both versions
        nodemap.put(KEY_FIELD_FLOATINGTICKCOUNT, new ObfuscationEntry("floatingTickCount", "field_147365_f"));
        nodemap.put(KEY_FIELD_MUSICTICKER, new ObfuscationEntry("mcMusicTicker", "field_147126_aw"));

        nodemap.put(KEY_FIELD_CAMERA_ZOOM, new FieldObfuscationEntry("cameraZoom", "field_78503_V"));
        nodemap.put(KEY_FIELD_CAMERA_YAW, new FieldObfuscationEntry("cameraYaw", "field_78502_W"));
        nodemap.put(KEY_FIELD_CAMERA_PITCH, new FieldObfuscationEntry("cameraPitch", "field_78509_X"));

        nodemap.put(KEY_METHOD_ORIENT_CAMERA, new MethodObfuscationEntry("orientCamera", "func_78467_g", ""));
    }

    private static String getName(String keyName) {
        return nodemap.get(keyName).name;
    }

    private static String getObfName(String keyName) {
        return nodemap.get(keyName).obfuscatedName;
    }

    public static String getNameDynamic(String keyName) {
        try {
            if (deobfuscated) {
                return getName(keyName);
            }
            return getObfName(keyName);
        } catch (final NullPointerException e) {
            System.err.println("Could not find key: " + keyName);
            throw e;
        }
    }
}
