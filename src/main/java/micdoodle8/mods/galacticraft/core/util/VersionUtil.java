package micdoodle8.mods.galacticraft.core.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.VersionParser;
import cpw.mods.fml.relauncher.FMLInjectionData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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

    private static final String KEY_CLASS_SCALED_RES = "scaledResolution";
    private static final String KEY_CLASS_RENDER_PLAYER = "renderPlayer";
    private static final String KEY_CLASS_ENTITYLIST = "entityList";

    private static final String KEY_METHOD_PLAYER_FOR_NAME = "getPlayerForUsername";
    private static final String KEY_METHOD_PLAYER_IS_OPPED = "isPlayerOpped";
    private static final String KEY_METHOD_PLAYER_TEXTURE = "getEntityTexture";

    // Used in GCPlayerHandler etc
    public static final String KEY_FIELD_FLOATINGTICKCOUNT = "floatingTickCount";
    public static final String KEY_FIELD_BIOMEINDEXLAYER = "biomeIndexLayer";
    public static final String KEY_FIELD_MUSICTICKER = "mcMusicTicker";

    public static final String KEY_FIELD_CAMERA_ZOOM = "cameraZoom";
    public static final String KEY_FIELD_CAMERA_YAW = "cameraYaw";
    public static final String KEY_FIELD_CAMERA_PITCH = "cameraPitch";
    public static final String KEY_FIELD_CLASSTOIDMAPPING = "classToIDMapping";
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
            nodemap.put(KEY_CLASS_SCALED_RES, new ObfuscationEntry("net/minecraft/client/gui/ScaledResolution"));
            nodemap.put(
                    KEY_CLASS_RENDER_PLAYER,
                    new ObfuscationEntry("net/minecraft/client/renderer/entity/RenderPlayer"));
            nodemap.put(KEY_CLASS_ENTITYLIST, new ObfuscationEntry("net/minecraft/entity/EntityList"));

            // Method descriptions are empty, since they are not needed for reflection.
            nodemap.put(KEY_METHOD_PLAYER_FOR_NAME, new MethodObfuscationEntry("func_152612_a", "func_152612_a", ""));
            nodemap.put(KEY_METHOD_PLAYER_IS_OPPED, new MethodObfuscationEntry("func_152596_g", "func_152596_g", ""));
            nodemap.put(KEY_METHOD_PLAYER_TEXTURE, new MethodObfuscationEntry("getEntityTexture", "func_110775_a", ""));
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
            nodemap.put(KEY_CLASS_SCALED_RES, new ObfuscationEntry("net/minecraft/client/gui/ScaledResolution"));
            nodemap.put(
                    KEY_CLASS_RENDER_PLAYER,
                    new ObfuscationEntry("net/minecraft/client/renderer/entity/RenderPlayer"));
            nodemap.put(KEY_CLASS_ENTITYLIST, new ObfuscationEntry("net/minecraft/entity/EntityList"));

            nodemap.put(
                    KEY_METHOD_PLAYER_FOR_NAME,
                    new MethodObfuscationEntry("getPlayerForUsername", "func_72361_f", ""));
            nodemap.put(KEY_METHOD_PLAYER_IS_OPPED, new MethodObfuscationEntry("isPlayerOpped", "func_72353_e", ""));
            nodemap.put(KEY_METHOD_PLAYER_TEXTURE, new MethodObfuscationEntry("getEntityTexture", "func_110775_a", ""));

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
        nodemap.put(KEY_FIELD_CLASSTOIDMAPPING, new FieldObfuscationEntry("classToIDMapping", "field_75624_e"));
        nodemap.put(KEY_FIELD_CHUNKCACHE_WORLDOBJ, new FieldObfuscationEntry("worldObj", "field_72815_e"));

        nodemap.put(KEY_METHOD_ORIENT_CAMERA, new MethodObfuscationEntry("orientCamera", "func_78467_g", ""));
    }

    public static boolean mcVersionMatches(String version) {
        return VersionParser.parseRange("[" + version + "]").containsVersion(mcVersion);
    }

    public static EntityPlayerMP getPlayerForUsername(MinecraftServer server, String username) {
        try {
            Method m = (Method) reflectionCache.get(12);

            if (m == null) {
                final Class<?> c = server.getConfigurationManager().getClass();
                m = c.getMethod(getNameDynamic(KEY_METHOD_PLAYER_FOR_NAME), String.class);
                reflectionCache.put(12, m);
            }

            return (EntityPlayerMP) m.invoke(server.getConfigurationManager(), username);
        } catch (final Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    public static boolean isPlayerOpped(EntityPlayerMP player) {
        try {
            if (mcVersion1_7_10) {
                Method m = (Method) reflectionCache.get(14);

                if (m == null) {
                    final Class<?> c = player.mcServer.getConfigurationManager().getClass();
                    m = c.getMethod(getNameDynamic(KEY_METHOD_PLAYER_IS_OPPED), GameProfile.class);
                    reflectionCache.put(14, m);
                }

                return (Boolean) m.invoke(player.mcServer.getConfigurationManager(), player.getGameProfile());
            }
            if (mcVersion1_7_2) {
                Method m = (Method) reflectionCache.get(14);

                if (m == null) {
                    final Class<?> c = player.mcServer.getConfigurationManager().getClass();
                    m = c.getMethod(getNameDynamic(KEY_METHOD_PLAYER_IS_OPPED), String.class);
                    reflectionCache.put(14, m);
                }

                return (Boolean) m.invoke(player.mcServer.getConfigurationManager(), player.getGameProfile().getName());
            }
        } catch (final Throwable t) {
            t.printStackTrace();
        }

        return false;
    }

    @SideOnly(Side.CLIENT)
    public static ScaledResolution getScaledRes(Minecraft mc, int width, int height) {
        try {
            if (mcVersion1_7_10) {
                Constructor<?> m = (Constructor<?>) reflectionCache.get(16);

                if (m == null) {
                    final Class<?> c = Class.forName(getNameDynamic(KEY_CLASS_SCALED_RES).replace('/', '.'));
                    m = c.getConstructor(Minecraft.class, int.class, int.class);
                    reflectionCache.put(16, m);
                }

                return (ScaledResolution) m.newInstance(mc, width, height);
            }
            if (mcVersion1_7_2) {
                Constructor<?> m = (Constructor<?>) reflectionCache.get(16);

                if (m == null) {
                    final Class<?> c = Class.forName(getNameDynamic(KEY_CLASS_SCALED_RES).replace('/', '.'));
                    m = c.getConstructor(GameSettings.class, int.class, int.class);
                    reflectionCache.put(16, m);
                }

                return (ScaledResolution) m.newInstance(mc.gameSettings, width, height);
            }
        } catch (final Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    public static Method getPlayerTextureMethod() {
        try {
            Method m = (Method) reflectionCache.get(18);

            if (m == null) {
                final Class<?> c = Class.forName(getNameDynamic(KEY_CLASS_RENDER_PLAYER).replace('/', '.'));
                m = c.getMethod(getNameDynamic(KEY_METHOD_PLAYER_TEXTURE), AbstractClientPlayer.class);
                m.setAccessible(true);
                reflectionCache.put(18, m);
            }

            return m;
        } catch (final Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    public static void putClassToIDMapping(Class<?> mobClazz, int id) {
        // Achieves this, with private field:
        // EntityList.classToIDMapping.put(mobClazz, id);
        try {
            final Class<?> c = Class.forName(getNameDynamic(KEY_CLASS_ENTITYLIST).replace('/', '.'));
            final Field f = c.getDeclaredField(getNameDynamic(KEY_FIELD_CLASSTOIDMAPPING));
            f.setAccessible(true);
            @SuppressWarnings("unchecked")
            final Map<Class<?>, Integer> classToIDMapping = (Map<Class<?>, Integer>) f.get(null);
            classToIDMapping.put(mobClazz, id);
        } catch (final Throwable t) {
            t.printStackTrace();
        }
    }

    public static int getClassToIDMapping(Class<?> mobClazz) {
        // Achieves this, with private field:
        // EntityList.classToIDMapping.put(mobClazz, id);
        try {
            final Class<?> c = Class.forName(getNameDynamic(KEY_CLASS_ENTITYLIST).replace('/', '.'));
            final Field f = c.getDeclaredField(getNameDynamic(KEY_FIELD_CLASSTOIDMAPPING));
            f.setAccessible(true);
            @SuppressWarnings("unchecked")
            final Map<Class<?>, Integer> classToIDMapping = (Map<Class<?>, Integer>) f.get(null);
            final Integer i = classToIDMapping.get(mobClazz);

            return i != null ? i : 0;
        } catch (final Throwable t) {
            t.printStackTrace();
        }

        return 0;
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

    public static GameProfile constructGameProfile(UUID uuid, String strName) {
        try {
            Class<?> c = (Class<?>) reflectionCache.get(19);
            if (c == null) {
                c = Class.forName("com.mojang.authlib.GameProfile");
                reflectionCache.put(19, c);
            }

            if (mcVersion1_7_10) {
                return (GameProfile) c.getConstructor(UUID.class, String.class).newInstance(uuid, strName);
            }

            if (mcVersion1_7_2) {
                return (GameProfile) c.getConstructor(String.class, String.class)
                        .newInstance(uuid.toString().replace("-", ""), strName);
            }
        } catch (final Throwable t) {
            t.printStackTrace();
        }

        return null;
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
