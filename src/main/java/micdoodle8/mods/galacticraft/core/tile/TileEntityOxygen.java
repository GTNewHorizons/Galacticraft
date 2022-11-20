package micdoodle8.mods.galacticraft.core.tile;

import cpw.mods.fml.relauncher.Side;
import java.util.EnumSet;
import micdoodle8.mods.galacticraft.api.transmission.NetworkType;
import micdoodle8.mods.galacticraft.api.transmission.grid.IOxygenNetwork;
import micdoodle8.mods.galacticraft.api.transmission.tile.IOxygenReceiver;
import micdoodle8.mods.galacticraft.api.transmission.tile.IOxygenStorage;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlock;
import micdoodle8.mods.galacticraft.core.oxygen.NetworkHelper;
import micdoodle8.mods.galacticraft.core.util.Annotations.NetworkedField;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityOxygen extends TileBaseElectricBlock implements IOxygenReceiver, IOxygenStorage {
    public float maxOxygen;
    public float oxygenPerTick;

    @NetworkedField(targetSide = Side.CLIENT)
    public float storedOxygen;

    public float lastStoredOxygen;
    public static int timeSinceOxygenRequest;

    public TileEntityOxygen(float maxOxygen, float oxygenPerTick) {
        this.maxOxygen = maxOxygen;
        this.oxygenPerTick = oxygenPerTick;
    }

    public int getScaledOxygenLevel(int scale) {
        return (int) Math.floor(this.getOxygenStored() * scale / (this.getMaxOxygenStored() - this.oxygenPerTick));
    }

    public abstract boolean shouldUseOxygen();

    public int getCappedScaledOxygenLevel(int scale) {
        return (int)
                Math.max(Math.min(Math.floor((double) this.storedOxygen / (double) this.maxOxygen * scale), scale), 0);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!this.worldObj.isRemote) {
            if (TileEntityOxygen.timeSinceOxygenRequest > 0) {
                TileEntityOxygen.timeSinceOxygenRequest--;
            }

            if (this.shouldUseOxygen()) {
                this.storedOxygen = Math.max(this.storedOxygen - this.oxygenPerTick, 0);
            }
        }

        this.lastStoredOxygen = this.storedOxygen;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        if (nbt.hasKey("storedOxygen")) {
            this.storedOxygen = nbt.getInteger("storedOxygen");
        } else {
            this.storedOxygen = nbt.getFloat("storedOxygenF");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setFloat("storedOxygenF", this.storedOxygen);
    }

    @Override
    public void setOxygenStored(float oxygen) {
        this.storedOxygen = Math.max(Math.min(oxygen, this.getMaxOxygenStored()), 0);
    }

    @Override
    public float getOxygenStored() {
        return this.storedOxygen;
    }

    @Override
    public float getMaxOxygenStored() {
        return this.maxOxygen;
    }

    public EnumSet<ForgeDirection> getOxygenInputDirections() {
        return EnumSet.allOf(ForgeDirection.class);
    }

    public EnumSet<ForgeDirection> getOxygenOutputDirections() {
        return EnumSet.noneOf(ForgeDirection.class);
    }

    @Override
    public boolean canConnect(ForgeDirection direction, NetworkType type) {
        if (direction == null || direction.equals(ForgeDirection.UNKNOWN)) {
            return false;
        }

        if (type == NetworkType.OXYGEN) {
            return this.getOxygenInputDirections().contains(direction)
                    || this.getOxygenOutputDirections().contains(direction);
        }
        if (type == NetworkType.POWER)
        // return this.nodeAvailable(new EnergySourceAdjacent(direction));
        {
            return super.canConnect(direction, type);
        }

        return false;
    }

    @Override
    public float receiveOxygen(ForgeDirection from, float receive, boolean doReceive) {
        if (this.getOxygenInputDirections().contains(from)) {
            if (!doReceive) {
                return this.getOxygenRequest(from);
            }

            return this.receiveOxygen(receive, doReceive);
        }

        return 0;
    }

    public float receiveOxygen(float receive, boolean doReceive) {
        if (receive > 0) {
            final float prevOxygenStored = this.getOxygenStored();
            final float newStoredOxygen = Math.min(prevOxygenStored + receive, this.getMaxOxygenStored());

            if (doReceive) {
                TileEntityOxygen.timeSinceOxygenRequest = 20;
                this.setOxygenStored(newStoredOxygen);
            }

            return Math.max(newStoredOxygen - prevOxygenStored, 0);
        }

        return 0;
    }

    @Override
    public float provideOxygen(ForgeDirection from, float request, boolean doProvide) {
        if (this.getOxygenOutputDirections().contains(from)) {
            return this.provideOxygen(request, doProvide);
        }

        return 0;
    }

    public float provideOxygen(float request, boolean doProvide) {
        if (request > 0) {
            final float requestedOxygen = Math.min(request, this.storedOxygen);

            if (doProvide) {
                this.setOxygenStored(this.storedOxygen - requestedOxygen);
            }

            return requestedOxygen;
        }

        return 0;
    }

    public void produceOxygen() {
        if (!this.worldObj.isRemote) {
            for (final ForgeDirection direction : this.getOxygenOutputDirections()) {
                if (direction != ForgeDirection.UNKNOWN) {
                    this.produceOxygen(direction);
                }
            }
        }
    }

    public boolean produceOxygen(ForgeDirection outputDirection) {
        final float provide = this.getOxygenProvide(outputDirection);

        if (provide > 0F) {
            final TileEntity outputTile = new BlockVec3(this).getTileEntityOnSide(this.worldObj, outputDirection);
            final IOxygenNetwork outputNetwork =
                    NetworkHelper.getOxygenNetworkFromTileEntity(outputTile, outputDirection);

            if (outputNetwork != null) {
                final float powerRequest = outputNetwork.getRequest(this);

                if (powerRequest > 0) {
                    final float rejectedPower = outputNetwork.produce(provide, this);

                    this.provideOxygen(Math.max(provide - rejectedPower, 0), true);
                    return true;
                }
            } else if (outputTile instanceof IOxygenReceiver) {
                final float requestedOxygen =
                        ((IOxygenReceiver) outputTile).getOxygenRequest(outputDirection.getOpposite());

                if (requestedOxygen > 0) {
                    final float acceptedOxygen =
                            ((IOxygenReceiver) outputTile).receiveOxygen(outputDirection.getOpposite(), provide, true);
                    this.provideOxygen(acceptedOxygen, true);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public float getOxygenRequest(ForgeDirection direction) {
        if (this.shouldPullOxygen()) {
            return this.oxygenPerTick * 2;
        } else {
            return 0;
        }
    }

    @Override
    public boolean shouldPullOxygen() {
        return this.storedOxygen < this.maxOxygen;
    }

    /**
     * Make sure this does not exceed the oxygen stored. This should return 0 if no
     * oxygen is stored. Implementing tiles must respect this or you will generate
     * infinite oxygen.
     */
    @Override
    public float getOxygenProvide(ForgeDirection direction) {
        return 0;
    }
}
