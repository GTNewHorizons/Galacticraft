package micdoodle8.mods.galacticraft.core.dimension;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.PlayerUtil;
import micdoodle8.mods.galacticraft.core.wrappers.FlagData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class SpaceRaceManager {
    private static final Set<SpaceRace> spaceRaces = Sets.newHashSet();

    public static SpaceRace addSpaceRace(SpaceRace spaceRace) {
        SpaceRaceManager.spaceRaces.remove(spaceRace);
        SpaceRaceManager.spaceRaces.add(spaceRace);
        return spaceRace;
    }

    public static void removeSpaceRace(SpaceRace race) {
        SpaceRaceManager.spaceRaces.remove(race);
    }

    public static void tick() {
        for (final SpaceRace race : SpaceRaceManager.spaceRaces) {
            boolean playerOnline = false;

            for (final Object o : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
                if (o instanceof EntityPlayer) {
                    final EntityPlayer player = (EntityPlayer) o;

                    if (race.getPlayerNames().contains(player.getGameProfile().getName())) {
                        final CelestialBody body =
                                GalaxyRegistry.getCelestialBodyFromDimensionID(player.worldObj.provider.dimensionId);

                        if (body != null) {
                            if (!race.getCelestialBodyStatusList().containsKey(body)) {
                                race.setCelestialBodyReached(body);
                            }
                        }

                        playerOnline = true;
                    }
                }
            }

            if (playerOnline) {
                race.tick();
            }
        }
    }

    public static void loadSpaceRaces(NBTTagCompound nbt) {
        final NBTTagList tagList = nbt.getTagList("SpaceRaceList", 10);

        for (int i = 0; i < tagList.tagCount(); i++) {
            final NBTTagCompound nbt2 = tagList.getCompoundTagAt(i);
            final SpaceRace race = new SpaceRace();
            race.loadFromNBT(nbt2);
            SpaceRaceManager.spaceRaces.add(race);
        }
    }

    public static void saveSpaceRaces(NBTTagCompound nbt) {
        final NBTTagList tagList = new NBTTagList();

        for (final SpaceRace race : SpaceRaceManager.spaceRaces) {
            final NBTTagCompound nbt2 = new NBTTagCompound();
            race.saveToNBT(nbt2);
            tagList.appendTag(nbt2);
        }

        nbt.setTag("SpaceRaceList", tagList);
    }

    public static SpaceRace getSpaceRaceFromPlayer(String username) {
        for (final SpaceRace race : SpaceRaceManager.spaceRaces) {
            if (race.getPlayerNames().contains(username)) {
                return race;
            }
        }

        return null;
    }

    public static SpaceRace getSpaceRaceFromID(int teamID) {
        for (final SpaceRace race : SpaceRaceManager.spaceRaces) {
            if (race.getSpaceRaceID() == teamID) {
                return race;
            }
        }

        return null;
    }

    public static void sendSpaceRaceData(EntityPlayerMP toPlayer, SpaceRace spaceRace) {
        if (spaceRace != null) {
            final List<Object> objList = new ArrayList<Object>();
            objList.add(spaceRace.getSpaceRaceID());
            objList.add(spaceRace.getTeamName());
            objList.add(spaceRace.getFlagData());
            objList.add(spaceRace.getTeamColor());
            objList.add(spaceRace
                    .getPlayerNames()
                    .toArray(new String[spaceRace.getPlayerNames().size()]));

            if (toPlayer != null) {
                GalacticraftCore.packetPipeline.sendTo(
                        new PacketSimple(EnumSimplePacket.C_UPDATE_SPACE_RACE_DATA, objList), toPlayer);
            } else {
                GalacticraftCore.packetPipeline.sendToAll(
                        new PacketSimple(EnumSimplePacket.C_UPDATE_SPACE_RACE_DATA, objList));
            }
        }
    }

    public static ImmutableSet<SpaceRace> getSpaceRaces() {
        return ImmutableSet.copyOf(new HashSet<SpaceRace>(SpaceRaceManager.spaceRaces));
    }

    public static void onPlayerRemoval(String player, SpaceRace race) {
        for (final String member : race.getPlayerNames()) {
            final EntityPlayerMP memberObj =
                    PlayerUtil.getPlayerForUsernameVanilla(MinecraftServer.getServer(), member);

            if (memberObj != null) {
                memberObj.addChatMessage(new ChatComponentText(EnumColor.DARK_AQUA
                                + GCCoreUtil.translateWithFormat(
                                        "gui.spaceRace.chat.removeSuccess",
                                        EnumColor.RED + player + EnumColor.DARK_AQUA))
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_AQUA)));
            }
        }

        final List<String> playerList = new ArrayList<String>();
        playerList.add(player);
        final SpaceRace newRace = SpaceRaceManager.addSpaceRace(
                new SpaceRace(playerList, SpaceRace.DEFAULT_NAME, new FlagData(48, 32), new Vector3(1, 1, 1)));
        final EntityPlayerMP playerToRemove = PlayerUtil.getPlayerBaseServerFromPlayerUsername(player, true);

        if (playerToRemove != null) {
            SpaceRaceManager.sendSpaceRaceData(playerToRemove, newRace);
            SpaceRaceManager.sendSpaceRaceData(playerToRemove, race);
        }
    }
}
