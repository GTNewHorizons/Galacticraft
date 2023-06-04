package micdoodle8.mods.galacticraft.planets.mars.tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import micdoodle8.mods.galacticraft.api.transmission.NetworkType;
import micdoodle8.mods.galacticraft.api.transmission.grid.IHydrogenNetwork;
import micdoodle8.mods.galacticraft.api.transmission.grid.Pathfinder;
import micdoodle8.mods.galacticraft.api.transmission.grid.PathfinderChecker;
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector;
import micdoodle8.mods.galacticraft.api.transmission.tile.INetworkConnection;
import micdoodle8.mods.galacticraft.api.transmission.tile.INetworkProvider;
import micdoodle8.mods.galacticraft.api.transmission.tile.ITransmitter;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.FMLLog;

public class HydrogenNetwork implements IHydrogenNetwork {

    public Map<TileEntity, ForgeDirection> hydrogenTiles = new HashMap<>();

    private final Set<ITransmitter> pipes = new HashSet<>();

    @Override
    public float produce(float totalHydrogen, TileEntity... ignoreTiles) {
        float remainingUsableHydrogen = totalHydrogen;

        if (this.hydrogenTiles.isEmpty()) {
            this.refreshHydrogenTiles();
        }

        if (!this.hydrogenTiles.isEmpty()) {
            final float totalHydrogenRequest = this.getRequest(ignoreTiles);

            if (totalHydrogenRequest > 0) {
                final List<TileEntity> ignoreTilesList = Arrays.asList(ignoreTiles);
                for (final TileEntity tileEntity : new HashSet<>(this.hydrogenTiles.keySet())) {
                    if (!ignoreTilesList.contains(tileEntity) && (tileEntity instanceof TileEntityMethaneSynthesizer hydrogenTile)) {
                        if (hydrogenTile.shouldPullHydrogen()) {
                            for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                                final TileEntity tile = new BlockVec3(tileEntity)
                                        .modifyPositionFromSide(direction, 1)
                                        .getTileEntity(tileEntity.getWorldObj());

                                if (hydrogenTile.canConnect(direction, NetworkType.HYDROGEN)
                                        && tile instanceof ITransmitter transmitter && this.pipes.contains(transmitter)) {
                                    final float hydrogenToSend = Math.max(
                                            totalHydrogen,
                                            totalHydrogen * (hydrogenTile.getHydrogenRequest(direction)
                                                    / totalHydrogenRequest));

                                    if (hydrogenToSend > 0) {
                                        remainingUsableHydrogen -= hydrogenTile
                                                .receiveHydrogen(direction, hydrogenToSend, true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return remainingUsableHydrogen;
    }

    /**
     * @return How much hydrogen this network needs.
     */
    @Override
    public float getRequest(TileEntity... ignoreTiles) {
        final List<Float> requests = new ArrayList<>();

        if (this.hydrogenTiles.isEmpty()) {
            this.refreshHydrogenTiles();
        }

        final List<TileEntity> ignoreTilesList = Arrays.asList(ignoreTiles);
        for (final TileEntity tileEntity : new HashSet<>(this.hydrogenTiles.keySet())) {
            if (ignoreTilesList.contains(tileEntity)) {
                continue;
            }

            if ((tileEntity instanceof TileEntityMethaneSynthesizer
                    && ((TileEntityMethaneSynthesizer) tileEntity).shouldPullHydrogen()) && !tileEntity.isInvalid()) {
                if (tileEntity.getWorldObj().getTileEntity(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord)
                        == tileEntity) {
                    for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                        final BlockVec3 tileVec = new BlockVec3(tileEntity);
                        final TileEntity tile = tileVec.modifyPositionFromSide(direction, 1)
                                .getTileEntity(tileEntity.getWorldObj());

                        if (((TileEntityMethaneSynthesizer) tileEntity).canConnect(direction, NetworkType.HYDROGEN)
                                && tile instanceof ITransmitter transmitter && this.pipes.contains(transmitter)) {
                            requests.add(((TileEntityMethaneSynthesizer) tileEntity).getHydrogenRequest(direction));
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
        this.hydrogenTiles.clear();

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
            FMLLog.severe("Failed to refresh hydrogen pipe network.");
            e.printStackTrace();
        }
    }

    public void refreshHydrogenTiles() {
        try {
            final Iterator<ITransmitter> it = this.pipes.iterator();

            while (it.hasNext()) {
                final ITransmitter transmitter = it.next();

                if (transmitter == null || ((TileEntity) transmitter).isInvalid()
                        || ((TileEntity) transmitter).getWorldObj() == null) {
                    it.remove();
                    continue;
                }

                /*
                 * if (!(((TileEntity) transmitter).getWorldObj().getBlock(((TileEntity) transmitter).xCoord,
                 * ((TileEntity) transmitter).yCoord, ((TileEntity) transmitter).zCoord) instanceof BlockTransmitter)) {
                 * it.remove(); continue; }
                 */
                for (int i = 0; i < transmitter.getAdjacentConnections().length; i++) {
                    final TileEntity acceptor = transmitter.getAdjacentConnections()[i];

                    if (!(acceptor instanceof ITransmitter) && acceptor instanceof IConnector) {
                        this.hydrogenTiles.put(acceptor, ForgeDirection.getOrientation(i));
                    }
                }
            }
        } catch (final Exception e) {
            FMLLog.severe("Failed to refresh hydrogen pipe network.");
            e.printStackTrace();
        }
    }

    @Override
    public Set<ITransmitter> getTransmitters() {
        return this.pipes;
    }

    @Override
    public IHydrogenNetwork merge(IHydrogenNetwork network) {
        if (network != null && network != this) {
            final HydrogenNetwork newNetwork = new HydrogenNetwork();
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
                                    NetworkType.HYDROGEN,
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

                                    if ((nodeTile instanceof INetworkProvider) && (nodeTile != splitPoint)) {
                                        ((INetworkProvider) nodeTile).setNetwork(this);
                                    }
                                }
                            } else {
                                /**
                                 * The connections A and B are not connected anymore. Give both of them a new network.
                                 */
                                final IHydrogenNetwork newNetwork = new HydrogenNetwork();

                                for (final BlockVec3 node : finder.closedSet) {
                                    final TileEntity nodeTile = node
                                            .getTileEntity(((TileEntity) splitPoint).getWorldObj());

                                    if ((nodeTile instanceof INetworkProvider) && (nodeTile != splitPoint)) {
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
        return "HydrogenNetwork[" + this
                .hashCode() + "|Pipes:" + this.pipes.size() + "|Acceptors:" + this.hydrogenTiles.size() + "]";
    }
}
