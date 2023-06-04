package micdoodle8.mods.galacticraft.api.transmission.tile;

import micdoodle8.mods.galacticraft.api.transmission.NetworkType;

// TODO Fix raw type extension of INetworkProvider
public interface ITransmitter extends INetworkProvider, INetworkConnection {

    NetworkType getNetworkType();
}
