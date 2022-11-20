package micdoodle8.mods.galacticraft.planets;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class PlanetsProxy implements IGuiHandler {
    public void preInit(FMLPreInitializationEvent event) {
        for (final IPlanetsModule module : GalacticraftPlanets.commonModules.values()) {
            module.preInit(event);
        }
    }

    public void init(FMLInitializationEvent event) {
        for (final IPlanetsModule module : GalacticraftPlanets.commonModules.values()) {
            module.init(event);
        }
    }

    public void postInit(FMLPostInitializationEvent event) {
        for (final IPlanetsModule module : GalacticraftPlanets.commonModules.values()) {
            module.postInit(event);
        }
    }

    public void serverStarting(FMLServerStartingEvent event) {
        for (final IPlanetsModule module : GalacticraftPlanets.commonModules.values()) {
            module.serverStarting(event);
        }
    }

    public void serverInit(FMLServerStartedEvent event) {
        for (final IPlanetsModule module : GalacticraftPlanets.commonModules.values()) {
            module.serverInit(event);
        }
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        for (final IPlanetsModule module : GalacticraftPlanets.commonModules.values()) {
            final List<Integer> guiIDs = new ArrayList<Integer>();
            module.getGuiIDs(guiIDs);
            if (guiIDs.contains(ID)) {
                return module.getGuiElement(Side.SERVER, ID, player, world, x, y, z);
            }
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        for (final IPlanetsModuleClient module : GalacticraftPlanets.clientModules.values()) {
            final List<Integer> guiIDs = new ArrayList<Integer>();
            module.getGuiIDs(guiIDs);
            if (guiIDs.contains(ID)) {
                return module.getGuiElement(Side.CLIENT, ID, player, world, x, y, z);
            }
        }

        return null;
    }
}
