package micdoodle8.mods.galacticraft.api.tile;

import java.util.HashSet;
import micdoodle8.mods.galacticraft.api.entity.IDockable;
import net.minecraft.world.IBlockAccess;

public interface IFuelDock {
    HashSet<ILandingPadAttachable> getConnectedTiles();

    boolean isBlockAttachable(IBlockAccess world, int x, int y, int z);

    IDockable getDockedEntity();

    void dockEntity(IDockable entity);
}
