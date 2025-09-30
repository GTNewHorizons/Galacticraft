package micdoodle8.mods.galacticraft.core.client.capes;

import java.util.Map;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import micdoodle8.mods.galacticraft.core.mixins.interfaces.AbstractClientPlayerExt;

public class GCCapesEventHandler {

    private final Map<String, ResourceLocation> nameToCape;

    public GCCapesEventHandler(Map<String, ResourceLocation> nameToCape) {
        this.nameToCape = nameToCape;
    }

    @SubscribeEvent
    public void onPlayerSpawn(EntityJoinWorldEvent event) {
        if (event.world.isRemote && event.entity instanceof AbstractClientPlayer player && !player.func_152122_n()) {
            if (event.entity instanceof AbstractClientPlayerExt accessor) {
                ResourceLocation cape = nameToCape.get(player.getCommandSenderName());
                if (cape != null) {
                    accessor.gc$setCape(cape);
                }
            } else {
                // the mixin didn't inject
                MinecraftForge.EVENT_BUS.unregister(this);
            }
        }
    }
}
