package micdoodle8.mods.galacticraft.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.VersionParser;
import cpw.mods.fml.relauncher.FMLInjectionData;
import micdoodle8.mods.galacticraft.core.obfuscation.FieldObfuscationEntry;
import micdoodle8.mods.galacticraft.core.obfuscation.MethodObfuscationEntry;
import micdoodle8.mods.galacticraft.core.obfuscation.ObfuscationEntry;

public class VersionUtil {

    private static DefaultArtifactVersion mcVersion = null;
    public static boolean mcVersion1_7_2 = false;
    public static boolean mcVersion1_7_10 = false;
    private static boolean deobfuscated = true;
    private static final HashMap<String, ObfuscationEntry> nodemap = Maps.newHashMap();
    private static final HashMap<Integer, Object> reflectionCache = Maps.newHashMap();
    // Note: in reflectionCache, currently positions 3, 5, 7, 11, 13, 15, 17 are
    // unused and 21 onwards are also free.

    // Used in GCPlayerHandler etc
    public static final String KEY_FIELD_FLOATINGTICKCOUNT = "floatingTickCount";
    public static final String KEY_FIELD_BIOMEINDEXLAYER = "biomeIndexLayer";
    public static final String KEY_FIELD_MUSICTICKER = "mcMusicTicker";

    public static final String KEY_FIELD_CAMERA_ZOOM = "cameraZoom";
    public static final String KEY_FIELD_CAMERA_YAW = "cameraYaw";
    public static final String KEY_FIELD_CAMERA_PITCH = "cameraPitch";
    public static final String KEY_FIELD_CHUNKCACHE_WORLDOBJ = "chunkCacheWorldObj";

    public static final String KEY_METHOD_ORIENT_CAMERA = "orientCamera";
    public static Block sand;

    static {
        mcVersion = new DefaultArtifactVersion((String) FMLInjectionData.data()[4]);
        mcVersion1_7_2 = VersionUtil.mcVersionMatches("1.7.2");
        mcVersion1_7_10 = VersionUtil.mcVersionMatches("1.7.10");

        try {
            deobfuscated = Launch.classLoader.getClassBytes("net.minecraft.world.World") != null;
        } catch (final Exception e) {
            e.printStackTrace();
        }

        if (mcVersion1_7_10) {
            // nodemap.put(KEY_CLASS_COMPRESSED_STREAM_TOOLS, new
            // ObfuscationEntry("net/minecraft/nbt/CompressedStreamTools", "du"));
            // nodemap.put(KEY_CLASS_NBT_SIZE_TRACKER, new
            // ObfuscationEntry("net/minecraft/nbt/NBTSizeTracker", "ds"));
            // nodemap.put(KEY_CLASS_YGG_CONVERTER, new
            // ObfuscationEntry("net/minecraft/server/management/PreYggdrasilConverter",
            // "nz"));
            // nodemap.put(KEY_CLASS_TEXTURE_UTIL, new
            // ObfuscationEntry("net/minecraft/client/renderer/texture/TextureUtil",
            // "bqi"));
            // nodemap.put(KEY_CLASS_COMMAND_BASE, new
            // ObfuscationEntry("net/minecraft/command/CommandBase",
            // "y"));
            // nodemap.put(KEY_CLASS_SCALED_RES, new
            // ObfuscationEntry("net/minecraft/client/gui/ScaledResolution", "bca"));

            sand = Blocks.sand;
        } else if (mcVersion1_7_2) {
            // nodemap.put(KEY_CLASS_COMPRESSED_STREAM_TOOLS, new
            // ObfuscationEntry("net/minecraft/nbt/CompressedStreamTools", "dr"));
            // nodemap.put(KEY_CLASS_NBT_SIZE_TRACKER, new ObfuscationEntry("", "")); // Not
            // part of 1.7.2
            // nodemap.put(KEY_CLASS_YGG_CONVERTER, new ObfuscationEntry("", "")); // Not
            // part of 1.7.2
            // nodemap.put(KEY_CLASS_TEXTURE_UTIL, new
            // ObfuscationEntry("net/minecraft/client/renderer/texture/TextureUtil",
            // "bqa"));
            // nodemap.put(KEY_CLASS_COMMAND_BASE, new
            // ObfuscationEntry("net/minecraft/command/CommandBase",
            // "y"));
            // nodemap.put(KEY_CLASS_SCALED_RES, new
            // ObfuscationEntry("net/minecraft/client/gui/ScaledResolution", "bam"));

            try {
                final Field sandField = Blocks.class.getField(deobfuscated ? "sand" : "field_150354_m");
                sand = (Block) sandField.get(null);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        // Same for both versions
        nodemap.put(KEY_FIELD_FLOATINGTICKCOUNT, new ObfuscationEntry("floatingTickCount", "field_147365_f"));
        nodemap.put(KEY_FIELD_BIOMEINDEXLAYER, new ObfuscationEntry("biomeIndexLayer", "field_76945_e"));
        nodemap.put(KEY_FIELD_MUSICTICKER, new ObfuscationEntry("mcMusicTicker", "field_147126_aw"));

        nodemap.put(KEY_FIELD_CAMERA_ZOOM, new FieldObfuscationEntry("cameraZoom", "field_78503_V"));
        nodemap.put(KEY_FIELD_CAMERA_YAW, new FieldObfuscationEntry("cameraYaw", "field_78502_W"));
        nodemap.put(KEY_FIELD_CAMERA_PITCH, new FieldObfuscationEntry("cameraPitch", "field_78509_X"));
        nodemap.put(KEY_FIELD_CHUNKCACHE_WORLDOBJ, new FieldObfuscationEntry("worldObj", "field_72815_e"));

        nodemap.put(KEY_METHOD_ORIENT_CAMERA, new MethodObfuscationEntry("orientCamera", "func_78467_g", ""));
    }

    public static boolean mcVersionMatches(String version) {
        return VersionParser.parseRange("[" + version + "]").containsVersion(mcVersion);
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

    public static World getWorld(IBlockAccess world) {
        if (world instanceof World) {
            return (World) world;
        }

        if (world instanceof ChunkCache) {
            try {
                Field f = (Field) reflectionCache.get(20);
                if (f == null) {
                    final Class<?> c = Class.forName("net.minecraft.world.ChunkCache");
                    f = c.getDeclaredField(getNameDynamic(KEY_FIELD_CHUNKCACHE_WORLDOBJ));
                    f.setAccessible(true);
                    reflectionCache.put(20, f);
                }

                return (World) f.get(world);
            } catch (final Throwable t) {
                t.printStackTrace();
            }
        }

        return null;
    }

    public static ItemStack createStack(Block block, int meta) {
        try {
            Method m = (Method) reflectionCache.get(3);
            if (m == null) {
                final Class<?> c = Class.forName("net.minecraft.block.Block");
                final Method[] mm = c.getDeclaredMethods();
                for (final Method testMethod : mm) {
                    if ("func_149644_j".equals(testMethod.getName())) {
                        m = testMethod;
                        break;
                    }
                }
                m.setAccessible(true);
                reflectionCache.put(3, m);
            }

            return (ItemStack) m.invoke(block, meta);
        } catch (final Throwable t) {
            t.printStackTrace();
        }

        return null;
    }
}
