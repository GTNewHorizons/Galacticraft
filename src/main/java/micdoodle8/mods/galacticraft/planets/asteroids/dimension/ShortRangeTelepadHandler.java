package micdoodle8.mods.galacticraft.planets.asteroids.dimension;

import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.collect.Maps;

import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.planets.asteroids.tick.AsteroidsTickHandlerServer;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityShortRangeTelepad;

public class ShortRangeTelepadHandler extends WorldSavedData {

    public static final String saveDataID = "ShortRangeTelepads";
    private static final Map<Integer, TelepadEntry> tileMap = Maps.newHashMap();

    public ShortRangeTelepadHandler(String saveDataID) {
        super(saveDataID);
    }

    public static class TelepadEntry {

        public int dimensionID;
        public BlockVec3 position;

        public TelepadEntry(int dimID, BlockVec3 position) {
            this.dimensionID = dimID;
            this.position = position;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(this.dimensionID).append(this.position.hashCode()).toHashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof TelepadEntry) {
                return new EqualsBuilder().append(((TelepadEntry) other).dimensionID, this.dimensionID)
                        .append(((TelepadEntry) other).position, this.position).isEquals();
            }

            return false;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        final NBTTagList tagList = nbt.getTagList("TelepadList", 10);
        tileMap.clear();

        for (int i = 0; i < tagList.tagCount(); i++) {
            final NBTTagCompound nbt2 = tagList.getCompoundTagAt(i);
            final int address = nbt2.getInteger("Address");
            final int dimID = nbt2.getInteger("DimID");
            final int posX = nbt2.getInteger("PosX");
            final int posY = nbt2.getInteger("PosY");
            final int posZ = nbt2.getInteger("PosZ");
            tileMap.put(address, new TelepadEntry(dimID, new BlockVec3(posX, posY, posZ)));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        final NBTTagList tagList = new NBTTagList();

        for (final Map.Entry<Integer, TelepadEntry> e : tileMap.entrySet()) {
            final NBTTagCompound nbt2 = new NBTTagCompound();
            nbt2.setInteger("Address", e.getKey());
            nbt2.setInteger("DimID", e.getValue().dimensionID);
            nbt2.setInteger("PosX", e.getValue().position.x);
            nbt2.setInteger("PosY", e.getValue().position.y);
            nbt2.setInteger("PosZ", e.getValue().position.z);
            tagList.appendTag(nbt2);
        }

        nbt.setTag("TelepadList", tagList);
    }

    public static void addShortRangeTelepad(TileEntityShortRangeTelepad telepad) {
        if (!telepad.getWorldObj().isRemote && telepad.addressValid) {
            final TelepadEntry newEntry = new TelepadEntry(
                    telepad.getWorldObj().provider.dimensionId,
                    new BlockVec3(telepad));
            final TelepadEntry previous = tileMap.put(telepad.address, newEntry);

            if (previous == null || !previous.equals(newEntry)) {
                AsteroidsTickHandlerServer.spaceRaceData.setDirty(true);
            }
        }
    }

    public static void removeShortRangeTeleporter(TileEntityShortRangeTelepad telepad) {
        if (!telepad.getWorldObj().isRemote && telepad.addressValid) {
            tileMap.remove(telepad.address);
            AsteroidsTickHandlerServer.spaceRaceData.setDirty(true);
        }
    }

    public static TelepadEntry getLocationFromAddress(int address) {
        return tileMap.get(address);
    }
}
