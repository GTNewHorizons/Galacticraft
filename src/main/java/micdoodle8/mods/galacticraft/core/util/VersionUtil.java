package micdoodle8.mods.galacticraft.core.util;

import java.util.HashMap;

import net.minecraft.launchwrapper.Launch;

import com.google.common.collect.Maps;

import micdoodle8.mods.galacticraft.core.obfuscation.ObfuscationEntry;

public class VersionUtil {

    private static boolean deobfuscated = true;
    private static final HashMap<String, ObfuscationEntry> nodemap = Maps.newHashMap();

    // Used in GCPlayerHandler etc

    static {
        try {
            deobfuscated = Launch.classLoader.getClassBytes("net.minecraft.world.World") != null;
        } catch (final Exception e) {
            e.printStackTrace();
        }
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
