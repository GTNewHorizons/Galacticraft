package micdoodle8.mods.galacticraft.core.command;

import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.PlayerUtil;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldServer;

public class CommandPlanetTeleport extends CommandBase {

    @Override
    public String getCommandUsage(ICommandSender var1) {
        return "/" + this.getCommandName() + " [<player>]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandName() {
        return "dimensiontp";
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        EntityPlayerMP playerBase = null;

        if (astring.length >= 2) {
            throw new WrongUsageException(
                    GCCoreUtil
                            .translateWithFormat("commands.dimensiontp.tooMany", this.getCommandUsage(icommandsender)));
        }
        try {
            if (astring.length == 1) {
                playerBase = PlayerUtil.getPlayerBaseServerFromPlayerUsername(astring[0], true);
            } else {
                playerBase = PlayerUtil
                        .getPlayerBaseServerFromPlayerUsername(icommandsender.getCommandSenderName(), true);
            }

            if (playerBase == null) {
                throw new Exception("Could not find player with name: " + astring[0]);
            }
            final MinecraftServer server = MinecraftServer.getServer();
            final WorldServer worldserver = server
                    .worldServerForDimension(server.worldServers[0].provider.dimensionId);
            final ChunkCoordinates chunkcoordinates = worldserver.getSpawnPoint();
            final GCPlayerStats stats = GCPlayerStats.get(playerBase);
            stats.coordsTeleportedFromX = chunkcoordinates.posX;
            stats.coordsTeleportedFromZ = chunkcoordinates.posZ;

            try {
                WorldUtil.toCelestialSelection(
                        playerBase,
                        stats,
                        Integer.MAX_VALUE,
                        GuiCelestialSelection.MapMode.TELEPORTATION);
            } catch (final Exception e) {
                e.printStackTrace();
                throw e;
            }
        } catch (final Exception var6) {
            throw new CommandException(var6.getMessage());
        }
    }
}
