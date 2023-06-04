package micdoodle8.mods.galacticraft.planets.mars.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.transmission.NetworkType;
import micdoodle8.mods.galacticraft.api.transmission.grid.IGridNetwork;
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector;
import micdoodle8.mods.galacticraft.api.transmission.tile.INetworkProvider;
import micdoodle8.mods.galacticraft.api.transmission.tile.ITransmitter;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.core.tick.TickHandlerServer;

public class TileEntityHydrogenPipe extends TileEntity implements ITransmitter {

    private IGridNetwork network;

    public TileEntity[] adjacentConnections = null;

    @Override
    public boolean canUpdate() {
        return this.worldObj == null || !this.worldObj.isRemote;
    }

    @Override
    public void validate() {
        super.validate();
        if (!this.worldObj.isRemote) {
            TickHandlerServer.hydrogenTransmitterUpdates.add(this);
        }

        if (this.worldObj != null && this.worldObj.isRemote) {
            this.worldObj.func_147479_m(this.xCoord, this.yCoord, this.zCoord);
        }
    }

    @Override
    public void invalidate() {
        if (!this.worldObj.isRemote) {
            this.getNetwork().split(this);
        }

        super.invalidate();
    }

    @Override
    public IGridNetwork getNetwork() {
        if (this.network == null) {
            this.resetNetwork();
        }

        return this.network;
    }

    @Override
    public void onNetworkChanged() {}

    protected void resetNetwork() {
        final HydrogenNetwork network = new HydrogenNetwork();
        network.getTransmitters().add(this);
        this.setNetwork(network);
    }

    @Override
    public void setNetwork(IGridNetwork network) {
        this.network = network;
    }

    @Override
    public void refresh() {
        if (!this.worldObj.isRemote) {
            this.adjacentConnections = null;

            for (final ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
                final TileEntity tileEntity = new BlockVec3(this).getTileEntityOnSide(this.worldObj, side);

                if (tileEntity != null && tileEntity.getClass() == this.getClass()
                        && tileEntity instanceof INetworkProvider
                        && !this.getNetwork().equals(((INetworkProvider) tileEntity).getNetwork())) {
                    this.setNetwork(
                            (IGridNetwork) this.getNetwork().merge(((INetworkProvider) tileEntity).getNetwork()));
                }
            }

            this.getNetwork().refresh();
        }
    }

    @Override
    public TileEntity[] getAdjacentConnections() {
        /**
         * Cache the adjacentConnections.
         */
        if (this.adjacentConnections == null) {
            this.adjacentConnections = TileEntityHydrogenPipe.getAdjacentHydrogenConnections(this);
        }

        return this.adjacentConnections;
    }

    public static TileEntity[] getAdjacentHydrogenConnections(TileEntity tile) {
        final TileEntity[] adjacentConnections = new TileEntity[ForgeDirection.VALID_DIRECTIONS.length];
        final BlockVec3 thisVec = new BlockVec3(tile);
        for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            final TileEntity tileEntity = thisVec.getTileEntityOnSide(tile.getWorldObj(), direction);

            if (tileEntity instanceof IConnector
                    && ((IConnector) tileEntity).canConnect(direction.getOpposite(), NetworkType.HYDROGEN)) {
                adjacentConnections[direction.ordinal()] = tileEntity;
            }
        }

        return adjacentConnections;
    }

    @Override
    public boolean canConnect(ForgeDirection direction, NetworkType type) {
        return type == NetworkType.HYDROGEN;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(
                this.xCoord,
                this.yCoord,
                this.zCoord,
                this.xCoord + 1,
                this.yCoord + 1,
                this.zCoord + 1);
    }

    @Override
    public NetworkType getNetworkType() {
        return NetworkType.HYDROGEN;
    }
}
