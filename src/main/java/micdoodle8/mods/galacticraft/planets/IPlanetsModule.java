package micdoodle8.mods.galacticraft.planets;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

public interface IPlanetsModule {
    void preInit(FMLPreInitializationEvent event);

    void init(FMLInitializationEvent event);

    void postInit(FMLPostInitializationEvent event);

    void serverInit(FMLServerStartedEvent event);

    void serverStarting(FMLServerStartingEvent event);

    void getGuiIDs(List<Integer> idList);

    Object getGuiElement(Side side, int ID, EntityPlayer player, World world, int x, int y, int z);

    Configuration getConfiguration();

    void syncConfig();
}
