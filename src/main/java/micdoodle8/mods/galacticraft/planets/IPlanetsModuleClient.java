package micdoodle8.mods.galacticraft.planets;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import java.util.List;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface IPlanetsModuleClient {
    void preInit(FMLPreInitializationEvent event);

    void init(FMLInitializationEvent event);

    void postInit(FMLPostInitializationEvent event);

    void getGuiIDs(List<Integer> idList);

    Object getGuiElement(Side side, int ID, EntityPlayer player, World world, int x, int y, int z);

    int getBlockRenderID(Block block);

    void spawnParticle(String particleID, Vector3 position, Vector3 motion, Object... extraData);
}
