package micdoodle8.mods.galacticraft.api.tile;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Used for colored pipes to set/get colors
 */
public interface IColorable {
    /**
     * Sets the color of the tile
     *
     * @param col the color, equivalent to the dye colors in vanilla minecraft
     */
    void setColor(byte col);

    /**
     * Gets the color of this tile
     *
     * @return the color of the tile, equivalent to the dye colors in vanilla
     * minecraft
     */
    byte getColor();

    /**
     * Called when a tile adjacent to this one has it's color changed
     *
     * @param direction the direction (relative to this tile) that was updated.
     */
    void onAdjacentColorChanged(ForgeDirection direction);
}
