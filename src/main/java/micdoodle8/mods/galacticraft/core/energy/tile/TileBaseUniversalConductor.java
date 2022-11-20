package micdoodle8.mods.galacticraft.core.energy.tile;

import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import cpw.mods.fml.common.eventhandler.Event;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import java.lang.reflect.Constructor;
import mekanism.api.energy.IStrictEnergyAcceptor;
import micdoodle8.mods.galacticraft.api.transmission.tile.IConductor;
import micdoodle8.mods.galacticraft.api.transmission.tile.IElectrical;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler;
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

@InterfaceList({
    @Interface(modid = "IC2API", iface = "ic2.api.energy.tile.IEnergySink"),
    @Interface(modid = "IC2API", iface = "ic2.api.energy.tile.IEnergyEmitter"),
    @Interface(modid = "CoFHAPI|energy", iface = "cofh.api.energy.IEnergyHandler"),
    @Interface(modid = "MekanismAPI|energy", iface = "mekanism.api.energy.IStrictEnergyAcceptor"),
})
public abstract class TileBaseUniversalConductor extends TileBaseConductor
        implements IEnergySink, IEnergyEmitter, IEnergyHandler, IStrictEnergyAcceptor {
    protected boolean isAddedToEnergyNet;
    protected Object powerHandlerBC;

    // public float buildcraftBuffer = EnergyConfigHandler.BC3_RATIO * 50;
    private float IC2surplusJoules = 0F;

    public TileBaseUniversalConductor() {
        this.initBC();
    }

    @Override
    public void onNetworkChanged() {}

    private void initBC() {}

    @Override
    public TileEntity[] getAdjacentConnections() {
        return EnergyUtil.getAdjacentPowerConnections(this);
    }

    @Override
    public boolean canUpdate() {
        return EnergyConfigHandler.isIndustrialCraft2Loaded();
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!this.isAddedToEnergyNet) {
            if (!this.worldObj.isRemote) {
                this.initIC();
            }

            this.isAddedToEnergyNet = true;
        }
    }

    @Override
    public void invalidate() {
        this.IC2surplusJoules = 0F;
        this.unloadTileIC2();
        super.invalidate();
    }

    @Override
    public void onChunkUnload() {
        this.unloadTileIC2();
        super.onChunkUnload();
    }

    protected void initIC() {
        if (EnergyConfigHandler.isIndustrialCraft2Loaded() && !this.worldObj.isRemote) {
            try {
                final Class<?> tileLoadEvent = Class.forName("ic2.api.energy.event.EnergyTileLoadEvent");
                final Class<?> energyTile = Class.forName("ic2.api.energy.tile.IEnergyTile");
                final Constructor<?> constr = tileLoadEvent.getConstructor(energyTile);
                final Object o = constr.newInstance(this);

                if (o != null && o instanceof Event) {
                    MinecraftForge.EVENT_BUS.post((Event) o);
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void unloadTileIC2() {
        if (this.isAddedToEnergyNet && this.worldObj != null) {
            if (!this.worldObj.isRemote && EnergyConfigHandler.isIndustrialCraft2Loaded()) {
                try {
                    final Class<?> tileLoadEvent = Class.forName("ic2.api.energy.event.EnergyTileUnloadEvent");
                    final Class<?> energyTile = Class.forName("ic2.api.energy.tile.IEnergyTile");
                    final Constructor<?> constr = tileLoadEvent.getConstructor(energyTile);
                    final Object o = constr.newInstance(this);

                    if (o != null && o instanceof Event) {
                        MinecraftForge.EVENT_BUS.post((Event) o);
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }

            this.isAddedToEnergyNet = false;
        }
    }

    @Override
    public double getDemandedEnergy() {
        if (this.getNetwork() == null) {
            return 0.0;
        }

        if (this.IC2surplusJoules < 0.001F) {
            this.IC2surplusJoules = 0F;
            return this.getNetwork().getRequest(this) / EnergyConfigHandler.IC2_RATIO;
        }

        this.IC2surplusJoules = this.getNetwork().produce(this.IC2surplusJoules, true, 1, this);
        if (this.IC2surplusJoules < 0.001F) {
            this.IC2surplusJoules = 0F;
            return this.getNetwork().getRequest(this) / EnergyConfigHandler.IC2_RATIO;
        }
        return 0D;
    }

    @Override
    public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
        final TileEntity tile = new BlockVec3(this).getTileEntityOnSide(this.worldObj, directionFrom);
        int tier = (int) voltage > 120 ? 2 : 1;
        if (tile instanceof IEnergySource && ((IEnergySource) tile).getOfferedEnergy() >= 128) {
            tier = 2;
        }
        final float convertedEnergy = (float) amount * EnergyConfigHandler.IC2_RATIO;
        final float surplus = this.getNetwork().produce(convertedEnergy, true, tier, this, tile);

        if (surplus >= 0.001F) {
            this.IC2surplusJoules = surplus;
        } else {
            this.IC2surplusJoules = 0F;
        }

        return 0D;
    }

    @Override
    public int getSinkTier() {
        return 3;
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
        // Don't add connection to IC2 grid if it's a Galacticraft tile
        if (emitter instanceof IElectrical || emitter instanceof IConductor) {
            return false;
        }

        // Don't make connection with IC2 wires [don't want risk of multiple connections
        // + there is a graphical glitch
        // in IC2]
        try {
            final Class<?> conductorIC2 = Class.forName("ic2.api.energy.tile.IEnergyConductor");
            if (conductorIC2.isInstance(emitter)) {
                return false;
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
        // Don't add connection to IC2 grid if it's a Galacticraft tile
        if (receiver instanceof IElectrical || receiver instanceof IConductor) {
            return false;
        }

        // Don't make connection with IC2 wires [don't want risk of multiple connections
        // + there is a graphical glitch
        // in IC2]
        try {
            final Class<?> conductorIC2 = Class.forName("ic2.api.energy.tile.IEnergyConductor");
            if (conductorIC2.isInstance(receiver)) {
                return false;
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        if (this.getNetwork() == null) {
            return 0;
        }
        final float receiveGC = maxReceive * EnergyConfigHandler.RF_RATIO;
        final float sentGC = receiveGC - this.getNetwork().produce(receiveGC, !simulate, 1);
        return MathHelper.floor_float(sentGC / EnergyConfigHandler.RF_RATIO);
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        // Do not form wire-to-wire connections with EnderIO conduits
        final TileEntity tile = new BlockVec3(this).getTileEntityOnSide(this.worldObj, from);
        try {
            if (EnergyUtil.clazzEnderIOCable != null && EnergyUtil.clazzEnderIOCable.isInstance(tile)) {
                return false;
            }
        } catch (final Exception e) {
        }
        return true;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return 0;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        if (this.getNetwork() == null) {
            return 0;
        }

        return MathHelper.floor_float(this.getNetwork().getRequest(this) / EnergyConfigHandler.RF_RATIO);
    }

    @Override
    public double transferEnergyToAcceptor(ForgeDirection side, double amount) {
        if (!this.canReceiveEnergy(side)) {
            return 0;
        }

        return amount
                - this.getNetwork().produce((float) amount * EnergyConfigHandler.MEKANISM_RATIO, true, 1, this)
                        / EnergyConfigHandler.MEKANISM_RATIO;
    }

    @Override
    public boolean canReceiveEnergy(ForgeDirection side) {
        if (this.getNetwork() == null) {
            return false;
        }

        final TileEntity te = new BlockVec3(this).getTileEntityOnSide(this.worldObj, side);
        try {
            if (Class.forName("codechicken.multipart.TileMultipart").isInstance(te)) {
                return false;
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public double getEnergy() {
        return 0;
    }

    @Override
    public void setEnergy(double energy) {
        ;
    }

    @Override
    public double getMaxEnergy() {
        if (this.getNetwork() == null) {
            return 0;
        }
        return this.getNetwork().getRequest(this) / EnergyConfigHandler.MEKANISM_RATIO;
    }
}
