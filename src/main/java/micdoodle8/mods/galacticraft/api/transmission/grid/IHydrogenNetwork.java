package micdoodle8.mods.galacticraft.api.transmission.grid;

import net.minecraft.tileentity.TileEntity;

import micdoodle8.mods.galacticraft.api.transmission.tile.ITransmitter;

/**
 * The hydrogen Network in interface form.
 *
 * @author Calclavia
 */
public interface IHydrogenNetwork extends IGridNetwork<IHydrogenNetwork, ITransmitter, TileEntity> {

    /**
     * Produces hydrogen in this hydrogen network.
     *
     * @return Rejected energy in Joules.
     */
    float produce(float sendAmount, TileEntity... ignoreTiles);

    /**
     * Gets the total amount of hydrogen requested/needed in the network.
     *
     * @param ignoreTiles The TileEntities to ignore during this calculation (optional).
     */
    float getRequest(TileEntity... ignoreTiles);
}
