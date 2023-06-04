package micdoodle8.mods.galacticraft.api.transmission.tile;

import net.minecraftforge.common.util.ForgeDirection;

import micdoodle8.mods.galacticraft.api.transmission.NetworkType;

/**
 * Applied to TileEntities that can connect to an electrical OR oxygen network.
 *
 * @author Calclavia, micdoodle8
 */
public interface IConnector {

    /**
     * @return If the connection is possible.
     */
    boolean canConnect(ForgeDirection direction, NetworkType type);
}
