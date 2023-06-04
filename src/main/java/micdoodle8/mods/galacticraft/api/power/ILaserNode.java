package micdoodle8.mods.galacticraft.api.power;

import net.minecraft.tileentity.TileEntity;

import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.api.vector.Vector3;

public interface ILaserNode extends IEnergyHandlerGC {

    Vector3 getInputPoint();

    Vector3 getOutputPoint(boolean offset);

    ILaserNode getTarget();

    TileEntity getTile();

    boolean canConnectTo(ILaserNode node);

    Vector3 getColor();

    void addNode(ILaserNode node);

    void removeNode(ILaserNode node);

    int compareTo(ILaserNode otherNode, BlockVec3 origin);
}
