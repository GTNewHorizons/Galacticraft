package micdoodle8.mods.galacticraft.core.client.capes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLLog;

public class GCCapeLoader implements Runnable {

    // spotless:off
    private static final ResourceLocation CAPE_BLUE       = new ResourceLocation("galacticraftcore:textures/capes/capeBlue.png");
    private static final ResourceLocation CAPE_BROWN      = new ResourceLocation("galacticraftcore:textures/capes/capeBrown.png");
    private static final ResourceLocation CAPE_CYAN       = new ResourceLocation("galacticraftcore:textures/capes/capeCyan.png");
    private static final ResourceLocation CAPE_DARK_GRAY  = new ResourceLocation("galacticraftcore:textures/capes/capeDarkGray.png");
    private static final ResourceLocation CAPE_DARK_GREEN = new ResourceLocation("galacticraftcore:textures/capes/capeDarkGreen.png");
    private static final ResourceLocation CAPE_LIGHT_BLUE = new ResourceLocation("galacticraftcore:textures/capes/capeLightBlue.png");
    private static final ResourceLocation CAPE_LIGHT_GRAY = new ResourceLocation("galacticraftcore:textures/capes/capeLightGray.png");
    private static final ResourceLocation CAPE_LIME       = new ResourceLocation("galacticraftcore:textures/capes/capeLime.png");
    private static final ResourceLocation CAPE_MAGENTA    = new ResourceLocation("galacticraftcore:textures/capes/capeMagenta.png");
    private static final ResourceLocation CAPE_ORANGE     = new ResourceLocation("galacticraftcore:textures/capes/capeOrange.png");
    private static final ResourceLocation CAPE_PINK       = new ResourceLocation("galacticraftcore:textures/capes/capePink.png");
    private static final ResourceLocation CAPE_PURPLE     = new ResourceLocation("galacticraftcore:textures/capes/capePurple.png");
    private static final ResourceLocation CAPE_RAINBOW    = new ResourceLocation("galacticraftcore:textures/capes/capeRainbow.png");
    private static final ResourceLocation CAPE_RED        = new ResourceLocation("galacticraftcore:textures/capes/capeRed.png");
    private static final ResourceLocation CAPE_YELLOW     = new ResourceLocation("galacticraftcore:textures/capes/capeYellow.png");
    // spotless:on

    @Override
    public void run() {
        final Map<String, ResourceLocation> nameToCape = loadNameToCapeMap();
        if (!nameToCape.isEmpty()) {
            Minecraft.getMinecraft().func_152343_a(() -> {
                MinecraftForge.EVENT_BUS.register(new GCCapesEventHandler(nameToCape));
                return null;
            });
        }
    }

    private static Map<String, ResourceLocation> loadNameToCapeMap() {
        Map<String, ResourceLocation> nameToCape = new HashMap<>();
        final int timeout = 10000;
        URL capeListUrl;
        try {
            capeListUrl = new URL("https://raw.github.com/micdoodle8/Galacticraft/master/capes.txt");
        } catch (final MalformedURLException e) {
            FMLLog.severe("Error getting capes list URL");
            e.printStackTrace();
            return nameToCape;
        }

        URLConnection connection;

        try {
            connection = capeListUrl.openConnection();
        } catch (final IOException e) {
            e.printStackTrace();
            return nameToCape;
        }

        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        InputStream stream;

        try {
            stream = connection.getInputStream();
        } catch (final IOException e) {
            e.printStackTrace();
            return nameToCape;
        }

        final InputStreamReader streamReader = new InputStreamReader(stream);
        final BufferedReader reader = new BufferedReader(streamReader);

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (line.contains(":")) {
                    final int splitLocation = line.indexOf(":");
                    final String username = line.substring(0, splitLocation);
                    final String capeName = line.substring(splitLocation + 1);
                    nameToCape.put(username, capeFromString(capeName));
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }

        try {
            reader.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        try {
            streamReader.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        try {
            stream.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return nameToCape;
    }

    private static ResourceLocation capeFromString(String capeName) {
        return switch (capeName) {
            case "capeBrown" -> CAPE_BROWN;
            case "capeCyan" -> CAPE_CYAN;
            case "capeDarkGray" -> CAPE_DARK_GRAY;
            case "capeDarkGreen" -> CAPE_DARK_GREEN;
            case "capeLightBlue" -> CAPE_LIGHT_BLUE;
            case "capeLightGray" -> CAPE_LIGHT_GRAY;
            case "capeLime" -> CAPE_LIME;
            case "capeMagenta" -> CAPE_MAGENTA;
            case "capeOrange" -> CAPE_ORANGE;
            case "capePink" -> CAPE_PINK;
            case "capePurple" -> CAPE_PURPLE;
            case "capeRainbow" -> CAPE_RAINBOW;
            case "capeRed" -> CAPE_RED;
            case "capeYellow" -> CAPE_YELLOW;
            default -> CAPE_BLUE;
        };
    }
}
