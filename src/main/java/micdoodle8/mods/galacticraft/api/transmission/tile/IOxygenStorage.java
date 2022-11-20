package micdoodle8.mods.galacticraft.api.transmission.tile;

/**
 * This interface is to be applied to all TileEntities which have Oxygen stores
 */
public interface IOxygenStorage {
    /**
     * Sets the amount of oxygen this unit has stored.
     */
    void setOxygenStored(float oxygen);

    /**
     * * @return Get the amount of oxygen currently stored in the block.
     */
    float getOxygenStored();

    /**
     * @return Get the max amount of oxygen that can be stored in the block.
     */
    float getMaxOxygenStored();
}
