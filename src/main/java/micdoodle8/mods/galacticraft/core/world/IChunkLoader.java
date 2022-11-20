package micdoodle8.mods.galacticraft.core.world;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

public interface IChunkLoader {
    void onTicketLoaded(Ticket ticket, boolean placed);

    Ticket getTicket();

    World getWorldObj();

    ChunkCoordinates getCoords();

    String getOwnerName();

    void setOwnerName(String ownerName);
}
