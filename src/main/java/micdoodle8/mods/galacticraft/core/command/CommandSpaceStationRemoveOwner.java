package micdoodle8.mods.galacticraft.core.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import micdoodle8.mods.galacticraft.core.dimension.SpaceStationWorldData;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.PlayerUtil;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import com.google.common.collect.Sets;

public class CommandSpaceStationRemoveOwner extends CommandBase {

    @Override
    public String getCommandUsage(ICommandSender var1) {
        return "/" + this.getCommandName() + " <player>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
        return true;
    }

    @Override
    public String getCommandName() {
        return "ssuninvite";
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        String var3 = null;
        EntityPlayerMP playerBase = null;

        if (astring.length <= 0) {
            throw new WrongUsageException(
                    GCCoreUtil
                            .translateWithFormat("commands.ssinvite.wrongUsage", this.getCommandUsage(icommandsender)));
        }
        var3 = astring[0];

        try {
            playerBase = PlayerUtil
                    .getPlayerBaseServerFromPlayerUsername(icommandsender.getCommandSenderName(), false);

            if (playerBase != null) {
                final GCPlayerStats stats = GCPlayerStats.get(playerBase);

                if (stats.spaceStationDimensionData.isEmpty()) {
                    throw new WrongUsageException(GCCoreUtil.translate("commands.ssinvite.notFound"));
                } else {
                    for (final Map.Entry<Integer, Integer> e : stats.spaceStationDimensionData.entrySet()) {
                        final SpaceStationWorldData data = SpaceStationWorldData
                                .getStationData(playerBase.worldObj, e.getValue(), playerBase);

                        String str = null;
                        for (final String name : data.getAllowedPlayers()) {
                            if (name.equalsIgnoreCase(var3)) {
                                str = name;
                                break;
                            }
                        }

                        if (str != null) {
                            data.getAllowedPlayers().remove(str);
                            data.markDirty();
                        } else {
                            throw new CommandException(
                                    GCCoreUtil.translateWithFormat(
                                            "commands.ssuninvite.noPlayer",
                                            "\"" + var3 + "\""));
                        }
                    }
                }
            }
        } catch (final Exception var6) {
            throw new CommandException(var6.getMessage());
        }

        if (playerBase != null) {
            playerBase.addChatMessage(
                    new ChatComponentText(GCCoreUtil.translateWithFormat("gui.spacestation.removesuccess", var3)));
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
        return par2ArrayOfStr.length == 1
                ? getListOfStringsMatchingLastWord(par2ArrayOfStr, this.getPlayers(par1ICommandSender))
                : null;
    }

    protected String[] getPlayers(ICommandSender icommandsender) {
        final EntityPlayerMP playerBase = PlayerUtil
                .getPlayerBaseServerFromPlayerUsername(icommandsender.getCommandSenderName(), false);

        if (playerBase != null) {
            final GCPlayerStats stats = GCPlayerStats.get(playerBase);
            if (!stats.spaceStationDimensionData.isEmpty()) {
                final String[] allNames = MinecraftServer.getServer().getAllUsernames();
                // data.getAllowedPlayers may include some in lowercase
                // Convert to correct case at least for those players who are online
                final HashSet<String> allowedNames = Sets.newHashSet();

                for (final Map.Entry<Integer, Integer> e : stats.spaceStationDimensionData.entrySet()) {
                    final SpaceStationWorldData data = SpaceStationWorldData
                            .getStationData(playerBase.worldObj, e.getValue(), playerBase);
                    allowedNames.addAll(data.getAllowedPlayers());
                }

                final Iterator<String> itName = allowedNames.iterator();
                final ArrayList<String> replaceNames = new ArrayList<>();
                while (itName.hasNext()) {
                    final String name = itName.next();
                    for (final String allEntry : allNames) {
                        if (name.equalsIgnoreCase(allEntry)) {
                            itName.remove();
                            replaceNames.add(allEntry);
                        }
                    }
                }
                // This does the conversion to correct case
                allowedNames.addAll(replaceNames);
                final String[] rvsize = new String[allowedNames.size()];
                return allowedNames.toArray(rvsize);
            }
        }

        final String[] returnvalue = { "" };
        return returnvalue;
    }

    @Override
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
        return par2 == 0;
    }
}
