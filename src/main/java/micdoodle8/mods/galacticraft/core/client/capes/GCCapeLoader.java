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
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLLog;

public class GCCapeLoader implements Runnable {

    @Override
    public void run() {
        final Map<String, String> nameToCapeMap = loadNameToCapeMap();
        if (!nameToCapeMap.isEmpty()) {
            Minecraft.getMinecraft().func_152343_a(() -> {
                MinecraftForge.EVENT_BUS.register(new GCCapesEventHandler(nameToCapeMap));
                return null;
            });
        }
    }

    private static Map<String, String> loadNameToCapeMap() {
        Map<String, String> nameCapesMap = new HashMap<>();
        final int timeout = 10000;
        URL capeListUrl;
        try {
            capeListUrl = new URL("https://raw.github.com/micdoodle8/Galacticraft/master/capes.txt");
        } catch (final MalformedURLException e) {
            FMLLog.severe("Error getting capes list URL");
            e.printStackTrace();
            return nameCapesMap;
        }

        URLConnection connection;

        try {
            connection = capeListUrl.openConnection();
        } catch (final IOException e) {
            e.printStackTrace();
            return nameCapesMap;
        }

        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        InputStream stream;

        try {
            stream = connection.getInputStream();
        } catch (final IOException e) {
            e.printStackTrace();
            return nameCapesMap;
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
                    nameCapesMap.put(username, capeName);
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
        return nameCapesMap;
    }
}
