package micdoodle8.mods.galacticraft.api.transmission.tile;

import micdoodle8.mods.galacticraft.api.transmission.grid.IGridNetwork;

public interface INetworkProvider<N, C, A> {

    IGridNetwork<N, C, A> getNetwork();

    void setNetwork(IGridNetwork<N, C, A> network);
}
