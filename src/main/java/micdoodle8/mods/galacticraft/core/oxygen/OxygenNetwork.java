package micdoodle8.mods.galacticraft.core.oxygen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.FMLLog;
import micdoodle8.mods.galacticraft.api.transmission.NetworkType;
import micdoodle8.mods.galacticraft.api.transmission.grid.IOxygenNetwork;
import micdoodle8.mods.galacticraft.api.transmission.grid.Pathfinder;
import micdoodle8.mods.galacticraft.api.transmission.grid.PathfinderChecker;
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector;
import micdoodle8.mods.galacticraft.api.transmission.tile.INetworkConnection;
import micdoodle8.mods.galacticraft.api.transmission.tile.INetworkProvider;
import micdoodle8.mods.galacticraft.api.transmission.tile.IOxygenReceiver;
import micdoodle8.mods.galacticraft.api.transmission.tile.ITransmitter;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;

/**
 * An Oxygen Network comprised of ITransmitter which can transmit oxygen
 */
public class OxygenNetwork implements IOxygenNetwork {

    public Map<TileEntity, ForgeDirection> oxygenTiles;

    private final Set<ITransmitter> pipes = new HashSet<>();

    @Override
    public float produce(float totalOxygen, TileEntity... ignoreTiles) {
        float remainingUsableOxygen = totalOxygen;

        if (this.oxygenTiles == null || this.oxygenTiles.isEmpty()) {
            this.refreshOxygenTiles();
        }

        if (!this.oxygenTiles.isEmpty()) {
            final float totalOxygenRequest = this.getRequest(ignoreTiles);

            if (totalOxygenRequest > 0) {
                final List<TileEntity> ignoreTilesList = Arrays.asList(ignoreTiles);
                for (final TileEntity tileEntity : new HashSet<>(this.oxygenTiles.keySet())) {
                    if ((!ignoreTilesList.contains(tileEntity) && tileEntity instanceof IOxygenReceiver oxygenTile)
                            && oxygenTile.shouldPullOxygen()) {
                        for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                            if (oxygenTile.canConnect(direction, NetworkType.OXYGEN)) {
                                final TileEntity tile = new BlockVec3(tileEntity)
                                        .getTileEntityOnSide(tileEntity.getWorldObj(), direction);

                                if (tile instanceof ITransmitter transmitter && this.pipes.contains(transmitter)) {
                                    final float oxygenToSend = Math.min(
                                            remainingUsableOxygen,
                                            totalOxygen
                                                    * (oxygenTile.getOxygenRequest(direction) / totalOxygenRequest));

                                    if (oxygenToSend > 0) {
                                        remainingUsableOxygen -= oxygenTile
                                                .receiveOxygen(direction, oxygenToSend, true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return remainingUsableOxygen;
    }

    /**
     * @return How much oxygen this network needs.
     */
    @Override
    public float getRequest(TileEntity... ignoreTiles) {
        final List<Float> requests = new ArrayList<>();

        if (this.oxygenTiles == null || this.oxygenTiles.isEmpty()) {
            this.refreshOxygenTiles();
        }

        final List<TileEntity> ignoreTilesList = Arrays.asList(ignoreTiles);
        for (final TileEntity tileEntity : new HashSet<>(this.oxygenTiles.keySet())) {
            if (ignoreTilesList.contains(tileEntity)) {
                continue;
            }

            if ((tileEntity instanceof IOxygenReceiver oxygenTile && !tileEntity.isInvalid()
                    && oxygenTile.shouldPullOxygen())
                    && (tileEntity.getWorldObj().getTileEntity(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord)
                            == tileEntity)) {
                for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                    if (oxygenTile.canConnect(direction, NetworkType.OXYGEN)) {
                        final TileEntity tile = new BlockVec3(tileEntity)
                                .getTileEntityOnSide(tileEntity.getWorldObj(), direction);

                        if (tile instanceof ITransmitter transmitter && this.pipes.contains(transmitter)) {
                            requests.add(((IOxygenReceiver) tileEntity).getOxygenRequest(direction));
                        }
                    }
                }
            }
        }

        float total = 0.0F;

        for (final Float f : requests) {
            total += f;
        }

        return total;
    }

    /**
     * This function is called to refresh all conductors in this network
     */
    @Override
    public void refresh() {
        if (this.oxygenTiles != null) {
            this.oxygenTiles.clear();
        }

        try {
            final Iterator<ITransmitter> it = this.pipes.iterator();

            while (it.hasNext()) {
                final ITransmitter transmitter = it.next();

                if (transmitter == null) {
                    it.remove();
                    continue;
                }

                transmitter.onNetworkChanged();

                if (((TileEntity) transmitter).isInvalid() || ((TileEntity) transmitter).getWorldObj() == null) {
                    it.remove();
                    continue;
                }
                transmitter.setNetwork(this);
            }
        } catch (final Exception e) {
            FMLLog.severe("Failed to refresh oxygen pipe network.");
            e.printStackTrace();
        }
    }

    public void refreshOxygenTiles() {
        if (this.oxygenTiles == null) {
            this.oxygenTiles = new HashMap<>();
        } else {
            this.oxygenTiles.clear();
        }

        try {
            final Iterator<ITransmitter> it = this.pipes.iterator();

            while (it.hasNext()) {
                final ITransmitter transmitter = it.next();

                if (transmitter == null || ((TileEntity) transmitter).isInvalid()
                        || ((TileEntity) transmitter).getWorldObj() == null) {
                    it.remove();
                    continue;
                }

                // This causes problems with Sealed Oxygen Pipes (and maybe also unwanted chunk
                // loading)
                /*
                 * if (!(((TileEntity) transmitter).getWorldObj().getBlock(((TileEntity) transmitter).xCoord,
                 * ((TileEntity) transmitter).yCoord, ((TileEntity) transmitter).zCoord) instanceof BlockTransmitter)) {
                 * it.remove(); continue; }
                 */
                int i = 0;
                for (final TileEntity acceptor : transmitter.getAdjacentConnections()) {
                    if (!(acceptor instanceof ITransmitter) && acceptor instanceof IConnector) {
                        this.oxygenTiles.put(acceptor, ForgeDirection.getOrientation(i));
                    }
                    i++;
                }
            }
        } catch (final Exception e) {
            FMLLog.severe("Failed to refresh oxygen pipe network.");
            e.printStackTrace();
        }
    }

    @Override
    public Set<ITransmitter> getTransmitters() {
        return this.pipes;
    }

    @Override
    public IOxygenNetwork merge(IOxygenNetwork network) {
        if (network != null && network != this) {
            final OxygenNetwork newNetwork = new OxygenNetwork();
            newNetwork.pipes.addAll(this.pipes);
            newNetwork.pipes.addAll(network.getTransmitters());
            newNetwork.refresh();
            return newNetwork;
        }

        return this;
    }

    @Override
    public void split(ITransmitter splitPoint) {
        if (splitPoint instanceof TileEntity) {
            this.pipes.remove(splitPoint);

            /**
             * Loop through the connected blocks and attempt to see if there are connections between the two points
             * elsewhere.
             */
            final TileEntity[] connectedBlocks = splitPoint.getAdjacentConnections();

            for (final TileEntity connectedBlockA : connectedBlocks) {
                if (connectedBlockA instanceof INetworkConnection) {
                    for (final TileEntity connectedBlockB : connectedBlocks) {
                        if (connectedBlockA != connectedBlockB && connectedBlockB instanceof INetworkConnection) {
                            final Pathfinder finder = new PathfinderChecker(
                                    ((TileEntity) splitPoint).getWorldObj(),
                                    (INetworkConnection) connectedBlockB,
                                    NetworkType.OXYGEN,
                                    splitPoint);
                            finder.init(new BlockVec3(connectedBlockA));

                            if (finder.results.size() > 0) {
                                /**
                                 * The connections A and B are still intact elsewhere. Set all references of wire
                                 * connection into one network.
                                 */
                                for (final BlockVec3 node : finder.closedSet) {
                                    final TileEntity nodeTile = node
                                            .getTileEntity(((TileEntity) splitPoint).getWorldObj());

                                    if (nodeTile instanceof INetworkProvider && nodeTile != splitPoint) {
                                        ((INetworkProvider) nodeTile).setNetwork(this);
                                    }
                                }
                            } else {
                                /**
                                 * The connections A and B are not connected anymore. Give both of them a new network.
                                 */
                                final IOxygenNetwork newNetwork = new OxygenNetwork();

                                for (final BlockVec3 node : finder.closedSet) {
                                    final TileEntity nodeTile = node
                                            .getTileEntity(((TileEntity) splitPoint).getWorldObj());

                                    if (nodeTile instanceof INetworkProvider && nodeTile != splitPoint) {
                                        newNetwork.getTransmitters().add((ITransmitter) nodeTile);
                                    }
                                }

                                newNetwork.refresh();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "OxygenNetwork[" + this.hashCode()
                + "|Pipes:"
                + this.pipes.size()
                + "|Acceptors:"
                + (this.oxygenTiles == null ? 0 : this.oxygenTiles.size())
                + "]";
    }
}
