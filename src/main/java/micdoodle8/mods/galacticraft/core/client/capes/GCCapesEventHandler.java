package micdoodle8.mods.galacticraft.core.client.capes;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import micdoodle8.mods.galacticraft.core.client.render.ThreadDownloadImageDataGC;
import micdoodle8.mods.galacticraft.core.mixins.interfaces.AbstractClientPlayerExt;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;

public class GCCapesEventHandler {

    private final Map<String, String> nameToCapeMap;
    private final Map<String, ResourceLocation> capeToResource;
    private final Map<String, ResourceLocation> nameToResource;

    public GCCapesEventHandler(Map<String, String> nameToCapeMap) {
        this.nameToCapeMap = nameToCapeMap;
        this.capeToResource = new HashMap<>();
        this.nameToResource = new HashMap<>();
    }

    @SubscribeEvent
    public void onPlayerSpawn(EntityJoinWorldEvent event) {
        if (event.world.isRemote && event.entity instanceof AbstractClientPlayer player && !player.func_152122_n()) {
            if (event.entity instanceof AbstractClientPlayerExt accessor) {
                ResourceLocation cape = nameToResource.get(player.getCommandSenderName());
                if (cape != null) {
                    accessor.gc$setCape(cape);
                } else {
                    String capeName = nameToCapeMap.get(player.getCommandSenderName());
                    if (capeName != null) {
                        this.assignCapeToPlayer(player, accessor, capeName);
                    }
                }
            } else {
                // the mixin didn't inject
                MinecraftForge.EVENT_BUS.unregister(this);
            }
        }
    }

    private void assignCapeToPlayer(AbstractClientPlayer player, AbstractClientPlayerExt accessor, String capeName) {
        ResourceLocation location = capeToResource.get(capeName);
        if (location == null) {
            location = downloadCapeTexture(capeName);
            if (location == null) {
                nameToCapeMap.remove(player.getCommandSenderName());
                return;
            } else {
                capeToResource.put(capeName, location);
            }
        }
        nameToResource.put(player.getCommandSenderName(), location);
        accessor.gc$setCape(location);
    }

    private static ResourceLocation downloadCapeTexture(String capeName) {
        try {
            final String capeUrl = "https://raw.github.com/micdoodle8/Galacticraft/master/capes/" + capeName + ".png";
            final String dirName = Minecraft.getMinecraft().mcDataDir.getAbsolutePath();
            File directory = new File(dirName, "assets");
            boolean success = true;
            if (!directory.exists()) {
                success = directory.mkdir();
            }
            if (success) {
                directory = new File(directory, "gcCapes");
                if (!directory.exists()) {
                    success = directory.mkdir();
                }

                if (success) {
                    final File file = new File(directory, capeName);
                    final ResourceLocation resourcelocation = new ResourceLocation("gcCapes/" + capeName);
                    final ThreadDownloadImageDataGC threaddownloadimagedata = new ThreadDownloadImageDataGC(
                            file,
                            capeUrl,
                            null,
                            new IImageBuffer() {

                                @Override
                                public BufferedImage parseUserSkin(BufferedImage p_78432_1_) {
                                    if (p_78432_1_ == null) {
                                        return null;
                                    }
                                    final BufferedImage bufferedimage1 = new BufferedImage(512, 256, 2);
                                    final Graphics graphics = bufferedimage1.getGraphics();
                                    graphics.drawImage(p_78432_1_, 0, 0, null);
                                    graphics.dispose();
                                    return bufferedimage1;
                                }

                                @Override
                                public void func_152634_a() {}
                            });

                    if (ClientProxyCore.mc.getTextureManager().loadTexture(resourcelocation, threaddownloadimagedata)) {
                        return resourcelocation;
                    }
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
