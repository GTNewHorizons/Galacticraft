package micdoodle8.mods.galacticraft.core.network;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.server.FMLServerHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Satellite;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket;
import micdoodle8.mods.galacticraft.api.recipe.ISchematicPage;
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry;
import micdoodle8.mods.galacticraft.api.tile.IDisableableMachine;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.fx.EntityFXSparks;
import micdoodle8.mods.galacticraft.core.client.gui.GuiIdsCore;
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiBuggy;
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiParaChest;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import micdoodle8.mods.galacticraft.core.command.CommandGCEnergyUnits;
import micdoodle8.mods.galacticraft.core.dimension.SpaceRace;
import micdoodle8.mods.galacticraft.core.dimension.SpaceRaceManager;
import micdoodle8.mods.galacticraft.core.dimension.SpaceStationWorldData;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseConductor;
import micdoodle8.mods.galacticraft.core.entities.EntityBuggy;
import micdoodle8.mods.galacticraft.core.entities.EntityCelestialFake;
import micdoodle8.mods.galacticraft.core.entities.IBubbleProvider;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler.EnumModelPacket;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient;
import micdoodle8.mods.galacticraft.core.inventory.ContainerSchematic;
import micdoodle8.mods.galacticraft.core.inventory.IInventorySettable;
import micdoodle8.mods.galacticraft.core.items.ItemParaChute;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.tick.KeyHandlerClient;
import micdoodle8.mods.galacticraft.core.tick.TickHandlerClient;
import micdoodle8.mods.galacticraft.core.tile.TileEntityAirLockController;
import micdoodle8.mods.galacticraft.core.tile.TileEntityArclamp;
import micdoodle8.mods.galacticraft.core.tile.TileEntityScreen;
import micdoodle8.mods.galacticraft.core.tile.TileEntityTelemetry;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import micdoodle8.mods.galacticraft.core.util.MapUtil;
import micdoodle8.mods.galacticraft.core.util.PlayerUtil;
import micdoodle8.mods.galacticraft.core.util.VersionUtil;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import micdoodle8.mods.galacticraft.core.wrappers.FlagData;
import micdoodle8.mods.galacticraft.core.wrappers.Footprint;
import micdoodle8.mods.galacticraft.core.wrappers.PlayerGearData;

public class PacketSimple extends Packet implements IPacket {

    public enum EnumSimplePacket {

        // SERVER
        S_RESPAWN_PLAYER(Side.SERVER, String.class),
        S_TELEPORT_ENTITY(Side.SERVER, String.class, Boolean.class),
        S_IGNITE_ROCKET(Side.SERVER),
        S_OPEN_SCHEMATIC_PAGE(Side.SERVER, Integer.class),
        S_OPEN_FUEL_GUI(Side.SERVER, String.class),
        S_UPDATE_SHIP_YAW(Side.SERVER, Float.class),
        S_UPDATE_SHIP_PITCH(Side.SERVER, Float.class),
        S_BIND_SPACE_STATION_ID(Side.SERVER, Integer.class),
        S_UNLOCK_NEW_SCHEMATIC(Side.SERVER),
        S_UPDATE_DISABLEABLE_BUTTON(Side.SERVER, Integer.class, Integer.class, Integer.class, Integer.class),
        S_ON_FAILED_CHEST_UNLOCK(Side.SERVER, Integer.class),
        S_RENAME_SPACE_STATION(Side.SERVER, String.class, Integer.class),
        S_OPEN_EXTENDED_INVENTORY(Side.SERVER),
        S_ON_ADVANCED_GUI_CLICKED_INT(Side.SERVER, Integer.class, Integer.class, Integer.class, Integer.class,
                Integer.class),
        S_ON_ADVANCED_GUI_CLICKED_STRING(Side.SERVER, Integer.class, Integer.class, Integer.class, Integer.class,
                String.class),
        S_UPDATE_SHIP_MOTION_Y(Side.SERVER, Integer.class, Boolean.class),
        S_START_NEW_SPACE_RACE(Side.SERVER, Integer.class, String.class, FlagData.class, Vector3.class, String[].class),
        S_REQUEST_FLAG_DATA(Side.SERVER, String.class),
        S_INVITE_RACE_PLAYER(Side.SERVER, String.class, Integer.class),
        S_REMOVE_RACE_PLAYER(Side.SERVER, String.class, Integer.class),
        S_ADD_RACE_PLAYER(Side.SERVER, String.class, Integer.class),
        S_COMPLETE_CBODY_HANDSHAKE(Side.SERVER, String.class),
        S_REQUEST_GEAR_DATA(Side.SERVER, String.class),
        S_REQUEST_ARCLAMP_FACING(Side.SERVER, Integer.class, Integer.class, Integer.class),
        S_REQUEST_OVERWORLD_IMAGE(Side.SERVER),
        S_REQUEST_MAP_IMAGE(Side.SERVER, Integer.class, Integer.class, Integer.class),
        S_REQUEST_PLAYERSKIN(Side.SERVER, String.class),
        S_UPDATE_VIEWSCREEN_REQUEST(Side.SERVER, Integer.class, Integer.class, Integer.class, Integer.class),
        S_BUILDFLAGS_UPDATE(Side.SERVER, Integer.class),
        // CLIENT
        C_AIR_REMAINING(Side.CLIENT, Integer.class, Integer.class, String.class),
        C_UPDATE_DIMENSION_LIST(Side.CLIENT, String.class, String.class, Integer.class),
        C_SPAWN_SPARK_PARTICLES(Side.CLIENT, Integer.class, Integer.class, Integer.class),
        C_UPDATE_GEAR_SLOT(Side.CLIENT, String.class, Integer.class, Integer.class),
        C_CLOSE_GUI(Side.CLIENT),
        C_RESET_THIRD_PERSON(Side.CLIENT),
        C_UPDATE_SPACESTATION_LIST(Side.CLIENT, Integer[].class),
        C_UPDATE_SPACESTATION_DATA(Side.CLIENT, Integer.class, NBTTagCompound.class),
        C_UPDATE_SPACESTATION_CLIENT_ID(Side.CLIENT, String.class),
        C_UPDATE_PLANETS_LIST(Side.CLIENT, Integer[].class),
        C_UPDATE_CONFIGS(Side.CLIENT, Integer.class, Double.class, Integer.class, Integer.class, Integer.class,
                String.class, Float.class, Float.class, Float.class, Float.class, Integer.class, String[].class),
        C_UPDATE_STATS(Side.CLIENT, Integer.class),
        C_ADD_NEW_SCHEMATIC(Side.CLIENT, Integer.class),
        C_UPDATE_SCHEMATIC_LIST(Side.CLIENT, Integer[].class),
        C_PLAY_SOUND_BOSS_DEATH(Side.CLIENT),
        C_PLAY_SOUND_EXPLODE(Side.CLIENT),
        C_PLAY_SOUND_BOSS_LAUGH(Side.CLIENT),
        C_PLAY_SOUND_BOW(Side.CLIENT),
        C_UPDATE_OXYGEN_VALIDITY(Side.CLIENT, Boolean.class),
        C_OPEN_PARACHEST_GUI(Side.CLIENT, Integer.class, Integer.class, Integer.class),
        C_UPDATE_WIRE_BOUNDS(Side.CLIENT, Integer.class, Integer.class, Integer.class),
        C_OPEN_SPACE_RACE_GUI(Side.CLIENT),
        C_UPDATE_SPACE_RACE_DATA(Side.CLIENT, Integer.class, String.class, FlagData.class, Vector3.class,
                String[].class),
        C_OPEN_JOIN_RACE_GUI(Side.CLIENT, Integer.class),
        C_UPDATE_FOOTPRINT_LIST(Side.CLIENT, Long.class, Footprint[].class),
        C_FOOTPRINTS_REMOVED(Side.CLIENT, Long.class, BlockVec3.class),
        C_UPDATE_STATION_SPIN(Side.CLIENT, Float.class, Boolean.class),
        C_UPDATE_STATION_DATA(Side.CLIENT, Double.class, Double.class),
        C_UPDATE_STATION_BOX(Side.CLIENT, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class,
                Integer.class),
        C_UPDATE_THERMAL_LEVEL(Side.CLIENT, Integer.class, Boolean.class),
        C_DISPLAY_ROCKET_CONTROLS(Side.CLIENT),
        C_GET_CELESTIAL_BODY_LIST(Side.CLIENT),
        C_UPDATE_ENERGYUNITS(Side.CLIENT, Integer.class),
        C_RESPAWN_PLAYER(Side.CLIENT, String.class, Integer.class, String.class, Integer.class),
        C_UPDATE_ARCLAMP_FACING(Side.CLIENT, Integer.class, Integer.class, Integer.class, Integer.class),
        C_UPDATE_VIEWSCREEN(Side.CLIENT, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class),
        C_UPDATE_TELEMETRY(Side.CLIENT, Integer.class, Integer.class, Integer.class, String.class, Integer.class,
                Integer.class, Integer.class, Integer.class, Integer.class, String.class),
        C_SEND_PLAYERSKIN(Side.CLIENT, String.class, String.class, String.class, String.class),
        C_SEND_OVERWORLD_IMAGE(Side.CLIENT, Integer.class, Integer.class, byte[].class),
        S_CANCEL_TELEPORTATION(Side.SERVER);

        private final Side targetSide;
        private final Class<?>[] decodeAs;

        EnumSimplePacket(Side targetSide, Class<?>... decodeAs) {
            this.targetSide = targetSide;
            this.decodeAs = decodeAs;
        }

        public Side getTargetSide() {
            return this.targetSide;
        }

        public Class<?>[] getDecodeClasses() {
            return this.decodeAs;
        }
    }

    private EnumSimplePacket type;
    private List<Object> data;
    private static String spamCheckString;

    public PacketSimple() {}

    public PacketSimple(EnumSimplePacket packetType, Object[] data) {
        this(packetType, Arrays.asList(data));
    }

    public PacketSimple(EnumSimplePacket packetType, List<Object> data) {
        if (packetType.getDecodeClasses().length != data.size()) {
            GCLog.info("Simple Packet Core found data length different than packet type");
            new RuntimeException().printStackTrace();
        }

        this.type = packetType;
        this.data = data;
    }

    @Override
    public void encodeInto(ChannelHandlerContext context, ByteBuf buffer) {
        buffer.writeInt(this.type.ordinal());

        try {
            NetworkUtil.encodeData(buffer, this.data);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void decodeInto(ChannelHandlerContext context, ByteBuf buffer) {
        this.type = EnumSimplePacket.values()[buffer.readInt()];

        try {
            if (this.type.getDecodeClasses().length > 0) {
                this.data = NetworkUtil.decodeData(this.type.getDecodeClasses(), buffer);
            }
            if (buffer.readableBytes() > 0) {
                GCLog.severe("Galacticraft packet length problem for packet type " + this.type.toString());
            }
        } catch (final Exception e) {
            System.err.println(
                    "[Galacticraft] Error handling simple packet type: " + this.type.toString()
                            + " "
                            + buffer.toString());
            e.printStackTrace();
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void handleClientSide(EntityPlayer player) {
        EntityClientPlayerMP playerBaseClient = null;
        GCPlayerStatsClient stats = null;

        if (player instanceof EntityClientPlayerMP) {
            playerBaseClient = (EntityClientPlayerMP) player;
            stats = GCPlayerStatsClient.get(playerBaseClient);
        } else if (this.type != EnumSimplePacket.C_UPDATE_SPACESTATION_LIST
                && this.type != EnumSimplePacket.C_UPDATE_PLANETS_LIST
                && this.type != EnumSimplePacket.C_UPDATE_CONFIGS) {
                    return;
                }

        switch (this.type) {
            case C_AIR_REMAINING:
                if (String.valueOf(this.data.get(2)).equals(
                        String.valueOf(FMLClientHandler.instance().getClient().thePlayer.getGameProfile().getName()))) {
                    TickHandlerClient.airRemaining = (Integer) this.data.get(0);
                    TickHandlerClient.airRemaining2 = (Integer) this.data.get(1);
                }
                break;
            case C_UPDATE_DIMENSION_LIST:
                if (String.valueOf(this.data.get(0))
                        .equals(FMLClientHandler.instance().getClient().thePlayer.getGameProfile().getName())) {
                    final String dimensionList = (String) this.data.get(1);
                    if (ConfigManagerCore.enableDebug && !dimensionList.equals(PacketSimple.spamCheckString)) {
                        GCLog.info("DEBUG info: " + dimensionList);
                        PacketSimple.spamCheckString = dimensionList;
                    }
                    final String[] destinations = dimensionList.split("\\?");
                    final List<CelestialBody> possibleCelestialBodies = Lists.newArrayList();
                    final Map<Integer, Map<String, GuiCelestialSelection.StationDataGUI>> spaceStationData = Maps
                            .newHashMap();
                    // Map<String, String> spaceStationNames = Maps.newHashMap();
                    // Map<String, Integer> spaceStationIDs = Maps.newHashMap();
                    // Map<String, Integer> spaceStationHomes = Maps.newHashMap();

                    for (final String str : destinations) {
                        CelestialBody celestialBody = WorldUtil.getReachableCelestialBodiesForName(str);

                        if (celestialBody == null && str.contains("$")) {
                            final String[] values = str.split("\\$");

                            final int homePlanetID = Integer.parseInt(values[4]);

                            for (final Satellite satellite : GalaxyRegistry.getRegisteredSatellites().values()) {
                                if (satellite.getParentPlanet().getDimensionID() == homePlanetID) {
                                    celestialBody = satellite;
                                    break;
                                }
                            }

                            if (!spaceStationData.containsKey(homePlanetID)) {
                                spaceStationData
                                        .put(homePlanetID, new HashMap<String, GuiCelestialSelection.StationDataGUI>());
                            }

                            spaceStationData.get(homePlanetID).put(
                                    values[1],
                                    new GuiCelestialSelection.StationDataGUI(values[2], Integer.parseInt(values[3])));

                            // spaceStationNames.put(values[1], values[2]);
                            // spaceStationIDs.put(values[1], Integer.parseInt(values[3]));
                            // spaceStationHomes.put(values[1], Integer.parseInt(values[4]));
                        }

                        if (celestialBody != null) {
                            possibleCelestialBodies.add(celestialBody);
                        }
                    }

                    if (FMLClientHandler.instance().getClient().theWorld != null) {
                        if (!(FMLClientHandler.instance().getClient().currentScreen instanceof GuiCelestialSelection)) {
                            final GuiCelestialSelection gui = new GuiCelestialSelection(
                                    GuiCelestialSelection.MapMode.fromInteger((Integer) this.data.get(2)),
                                    possibleCelestialBodies);
                            gui.spaceStationMap = spaceStationData;
                            // gui.spaceStationNames = spaceStationNames;
                            // gui.spaceStationIDs = spaceStationIDs;
                            FMLClientHandler.instance().getClient().displayGuiScreen(gui);
                        } else {
                            ((GuiCelestialSelection) FMLClientHandler.instance()
                                    .getClient().currentScreen).possibleBodies = possibleCelestialBodies;
                            ((GuiCelestialSelection) FMLClientHandler.instance()
                                    .getClient().currentScreen).spaceStationMap = spaceStationData;
                            // ((GuiCelestialSelection)
                            // FMLClientHandler.instance().getClient().currentScreen).spaceStationNames =
                            // spaceStationNames;
                            // ((GuiCelestialSelection)
                            // FMLClientHandler.instance().getClient().currentScreen).spaceStationIDs =
                            // spaceStationIDs;
                        }
                    }
                }
                break;
            case C_SPAWN_SPARK_PARTICLES:
                int x, y, z;
                x = (Integer) this.data.get(0);
                y = (Integer) this.data.get(1);
                z = (Integer) this.data.get(2);
                final Minecraft mc = Minecraft.getMinecraft();

                for (int i = 0; i < 4; i++) {
                    if (mc != null && mc.renderViewEntity != null && mc.effectRenderer != null && mc.theWorld != null) {
                        final EntityFX fx = new EntityFXSparks(
                                mc.theWorld,
                                x - 0.15 + 0.5,
                                y + 1.2,
                                z + 0.15 + 0.5,
                                mc.theWorld.rand.nextDouble() / 20 - mc.theWorld.rand.nextDouble() / 20,
                                mc.theWorld.rand.nextDouble() / 20 - mc.theWorld.rand.nextDouble() / 20);

                        if (fx != null) {
                            mc.effectRenderer.addEffect(fx);
                        }
                    }
                }
                break;
            case C_UPDATE_GEAR_SLOT:
                final int subtype = (Integer) this.data.get(2);
                EntityPlayer gearDataPlayer = null;
                final MinecraftServer server = MinecraftServer.getServer();
                final String gearName = (String) this.data.get(0);

                if (server != null) {
                    gearDataPlayer = PlayerUtil.getPlayerForUsernameVanilla(server, gearName);
                } else {
                    gearDataPlayer = player.worldObj.getPlayerEntityByName(gearName);
                }

                if (gearDataPlayer != null) {
                    PlayerGearData gearData = ClientProxyCore.playerItemData
                            .get(gearDataPlayer.getGameProfile().getName());

                    if (gearData == null) {
                        gearData = new PlayerGearData(player);
                        if (!ClientProxyCore.gearDataRequests.contains(gearName)) {
                            GalacticraftCore.packetPipeline.sendToServer(
                                    new PacketSimple(
                                            PacketSimple.EnumSimplePacket.S_REQUEST_GEAR_DATA,
                                            new Object[] { gearName }));
                            ClientProxyCore.gearDataRequests.add(gearName);
                        }
                    } else {
                        ClientProxyCore.gearDataRequests.remove(gearName);
                    }

                    final EnumModelPacket type = EnumModelPacket.values()[(Integer) this.data.get(1)];

                    switch (type) {
                        case ADDMASK:
                            gearData.setMask(0);
                            break;
                        case REMOVEMASK:
                            gearData.setMask(-1);
                            break;
                        case ADDGEAR:
                            gearData.setGear(0);
                            break;
                        case REMOVEGEAR:
                            gearData.setGear(-1);
                            break;
                        case ADDLEFTGREENTANK:
                            gearData.setLeftTank(0);
                            break;
                        case ADDLEFTORANGETANK:
                            gearData.setLeftTank(1);
                            break;
                        case ADDLEFTREDTANK:
                            gearData.setLeftTank(2);
                            break;
                        case ADDLEFTBLUETANK:
                            gearData.setLeftTank(3);
                            break;
                        case ADDLEFTVIOLETTANK:
                            gearData.setLeftTank(4);
                            break;
                        case ADDLEFTGRAYTANK:
                            gearData.setLeftTank(Integer.MAX_VALUE);
                            break;
                        case ADDRIGHTGREENTANK:
                            gearData.setRightTank(0);
                            break;
                        case ADDRIGHTORANGETANK:
                            gearData.setRightTank(1);
                            break;
                        case ADDRIGHTREDTANK:
                            gearData.setRightTank(2);
                            break;
                        case ADDRIGHTBLUETANK:
                            gearData.setRightTank(3);
                            break;
                        case ADDRIGHTVIOLETTANK:
                            gearData.setRightTank(4);
                            break;
                        case ADDRIGHTGRAYTANK:
                            gearData.setRightTank(Integer.MAX_VALUE);
                            break;
                        case REMOVE_LEFT_TANK:
                            gearData.setLeftTank(-1);
                            break;
                        case REMOVE_RIGHT_TANK:
                            gearData.setRightTank(-1);
                            break;
                        case ADD_PARACHUTE:
                            String name = "";

                            if (subtype != -1) {
                                name = ItemParaChute.names[subtype];
                                gearData.setParachute(
                                        new ResourceLocation(
                                                GalacticraftCore.ASSET_PREFIX,
                                                "textures/model/parachute/" + name + ".png"));
                            }
                            break;
                        case REMOVE_PARACHUTE:
                            gearData.setParachute(null);
                            break;
                        case ADD_FREQUENCY_MODULE:
                            gearData.setFrequencyModule(0);
                            break;
                        case REMOVE_FREQUENCY_MODULE:
                            gearData.setFrequencyModule(-1);
                            break;
                        case ADD_THERMAL_HELMET:
                            gearData.setThermalPadding(0, 0);
                            break;
                        case ADD_THERMAL_CHESTPLATE:
                            gearData.setThermalPadding(1, 0);
                            break;
                        case ADD_THERMAL_LEGGINGS:
                            gearData.setThermalPadding(2, 0);
                            break;
                        case ADD_THERMAL_BOOTS:
                            gearData.setThermalPadding(3, 0);
                            break;
                        case REMOVE_THERMAL_HELMET:
                            gearData.setThermalPadding(0, -1);
                            break;
                        case REMOVE_THERMAL_CHESTPLATE:
                            gearData.setThermalPadding(1, -1);
                            break;
                        case REMOVE_THERMAL_LEGGINGS:
                            gearData.setThermalPadding(2, -1);
                            break;
                        case REMOVE_THERMAL_BOOTS:
                            gearData.setThermalPadding(3, -1);
                            break;
                        case SHOWMASK:
                            gearData.setRenderMask(true);
                            break;
                        case HIDEMASK:
                            gearData.setRenderMask(false);
                            break;
                        case SHOWGEAR:
                            gearData.setRenderGear(true);
                            break;
                        case HIDEGEAR:
                            gearData.setRenderGear(false);
                            break;
                        case SHOWLEFTTANK:
                            gearData.setRenderLeftTank(true);
                            break;
                        case HIDELEFTTANK:
                            gearData.setRenderLeftTank(false);
                            break;
                        case SHOWRIGHTTANK:
                            gearData.setRenderRightTank(true);
                            break;
                        case HIDERIGHTTANK:
                            gearData.setRenderRightTank(false);
                            break;
                        case SHOWFREQUENCYMODULE:
                            gearData.setRenderFrequencyModule(true);
                            break;
                        case HIDEFREQUENCYMODULE:
                            gearData.setRenderFrequencyModule(false);
                            break;
                        case SHOWTHERMALHELMET:
                            gearData.setRenderThermalPadding(0, true);
                            break;
                        case SHOWTHERMALCHESTPLATE:
                            gearData.setRenderThermalPadding(1, true);
                            break;
                        case SHOWTHERMALLEGGINGS:
                            gearData.setRenderThermalPadding(2, true);
                            break;
                        case SHOWTHERMALBOOTS:
                            gearData.setRenderThermalPadding(3, true);
                            break;
                        case HIDETHERMALHELMET:
                            gearData.setRenderThermalPadding(0, false);
                            break;
                        case HIDETHERMALCHESTPLATE:
                            gearData.setRenderThermalPadding(1, false);
                            break;
                        case HIDETHERMALLEGGINGS:
                            gearData.setRenderThermalPadding(2, false);
                            break;
                        case HIDETHERMALBOOTS:
                            gearData.setRenderThermalPadding(3, false);
                            break;
                        default:
                            break;
                    }

                    ClientProxyCore.playerItemData.put(gearName, gearData);
                }

                break;
            case C_CLOSE_GUI:
                FMLClientHandler.instance().getClient().displayGuiScreen(null);
                break;
            case C_RESET_THIRD_PERSON:
                FMLClientHandler.instance().getClient().gameSettings.thirdPersonView = stats.thirdPersonView;
                break;
            case C_UPDATE_SPACESTATION_LIST:
                WorldUtil.decodeSpaceStationListClient(this.data);
                break;
            case C_UPDATE_SPACESTATION_DATA:
                final SpaceStationWorldData var4 = SpaceStationWorldData
                        .getMPSpaceStationData(player.worldObj, (Integer) this.data.get(0), player);
                var4.readFromNBT((NBTTagCompound) this.data.get(1));
                break;
            case C_UPDATE_SPACESTATION_CLIENT_ID:
                ClientProxyCore.clientSpaceStationID = WorldUtil.stringToSpaceStationData((String) this.data.get(0));
                break;
            case C_UPDATE_PLANETS_LIST:
                WorldUtil.decodePlanetsListClient(this.data);
                break;
            case C_UPDATE_CONFIGS:
                ConfigManagerCore.saveClientConfigOverrideable();
                ConfigManagerCore.setConfigOverride(this.data);
                break;
            case C_ADD_NEW_SCHEMATIC:
                final ISchematicPage page = SchematicRegistry.getMatchingRecipeForID((Integer) this.data.get(0));
                if (!stats.unlockedSchematics.contains(page)) {
                    stats.unlockedSchematics.add(page);
                }
                break;
            case C_UPDATE_SCHEMATIC_LIST:
                for (final Object o : this.data) {
                    final Integer schematicID = (Integer) o;

                    if (schematicID != -2) {
                        Collections.sort(stats.unlockedSchematics);

                        if (!stats.unlockedSchematics.contains(SchematicRegistry.getMatchingRecipeForID(schematicID))) {
                            stats.unlockedSchematics.add(SchematicRegistry.getMatchingRecipeForID(schematicID));
                        }
                    }
                }
                break;
            case C_PLAY_SOUND_BOSS_DEATH:
                player.playSound(GalacticraftCore.TEXTURE_PREFIX + "entity.bossdeath", 10.0F, 0.8F);
                break;
            case C_PLAY_SOUND_EXPLODE:
                player.playSound("random.explode", 10.0F, 0.7F);
                break;
            case C_PLAY_SOUND_BOSS_LAUGH:
                player.playSound(GalacticraftCore.TEXTURE_PREFIX + "entity.bosslaugh", 10.0F, 0.2F);
                break;
            case C_PLAY_SOUND_BOW:
                player.playSound("random.bow", 10.0F, 0.2F);
                break;
            case C_UPDATE_OXYGEN_VALIDITY:
                stats.oxygenSetupValid = (Boolean) this.data.get(0);
                break;
            case C_OPEN_PARACHEST_GUI:
                switch ((Integer) this.data.get(1)) {
                    case 0:
                        if (player.ridingEntity instanceof EntityBuggy) {
                            FMLClientHandler.instance().getClient().displayGuiScreen(
                                    new GuiBuggy(
                                            player.inventory,
                                            (EntityBuggy) player.ridingEntity,
                                            ((EntityBuggy) player.ridingEntity).getType()));
                            player.openContainer.windowId = (Integer) this.data.get(0);
                        }
                        break;
                    case 1:
                        final int entityID = (Integer) this.data.get(2);
                        final Entity entity = player.worldObj.getEntityByID(entityID);

                        if (entity instanceof IInventorySettable) {
                            FMLClientHandler.instance().getClient()
                                    .displayGuiScreen(new GuiParaChest(player.inventory, (IInventorySettable) entity));
                        }

                        player.openContainer.windowId = (Integer) this.data.get(0);
                        break;
                }
                break;
            case C_UPDATE_WIRE_BOUNDS:
                TileEntity tile = player.worldObj.getTileEntity(
                        (Integer) this.data.get(0),
                        (Integer) this.data.get(1),
                        (Integer) this.data.get(2));

                if (tile instanceof TileBaseConductor) {
                    ((TileBaseConductor) tile).adjacentConnections = null;
                    player.worldObj.getBlock(tile.xCoord, tile.yCoord, tile.zCoord)
                            .setBlockBoundsBasedOnState(player.worldObj, tile.xCoord, tile.yCoord, tile.zCoord);
                }
                break;
            case C_OPEN_SPACE_RACE_GUI:
                if (Minecraft.getMinecraft().currentScreen == null) {
                    TickHandlerClient.spaceRaceGuiScheduled = false;
                    player.openGui(
                            GalacticraftCore.instance,
                            GuiIdsCore.SPACE_RACE_START,
                            player.worldObj,
                            (int) player.posX,
                            (int) player.posY,
                            (int) player.posZ);
                } else {
                    TickHandlerClient.spaceRaceGuiScheduled = true;
                }
                break;
            case C_UPDATE_SPACE_RACE_DATA:
                final Integer teamID = (Integer) this.data.get(0);
                final String teamName = (String) this.data.get(1);
                final FlagData flagData = (FlagData) this.data.get(2);
                final Vector3 teamColor = (Vector3) this.data.get(3);
                final List<String> playerList = new ArrayList<>();

                for (int i = 4; i < this.data.size(); i++) {
                    final String playerName = (String) this.data.get(i);
                    ClientProxyCore.flagRequestsSent.remove(playerName);
                    playerList.add(playerName);
                }

                final SpaceRace race = new SpaceRace(playerList, teamName, flagData, teamColor);
                race.setSpaceRaceID(teamID);
                SpaceRaceManager.addSpaceRace(race);
                break;
            case C_OPEN_JOIN_RACE_GUI:
                stats.spaceRaceInviteTeamID = (Integer) this.data.get(0);
                player.openGui(
                        GalacticraftCore.instance,
                        GuiIdsCore.SPACE_RACE_JOIN,
                        player.worldObj,
                        (int) player.posX,
                        (int) player.posY,
                        (int) player.posZ);
                break;
            case C_UPDATE_FOOTPRINT_LIST:
                final List<Footprint> printList = new ArrayList<>();
                final long chunkKey = (Long) this.data.get(0);
                for (int i = 1; i < this.data.size(); i++) {
                    final Footprint print = (Footprint) this.data.get(i);
                    if (!print.owner.equals(player.getCommandSenderName())) {
                        printList.add(print);
                    }
                }
                ClientProxyCore.footprintRenderer.setFootprints(chunkKey, printList);
                break;
            case C_FOOTPRINTS_REMOVED:
                final long chunkKey0 = (Long) this.data.get(0);
                final BlockVec3 position = (BlockVec3) this.data.get(1);
                final List<Footprint> footprintList = ClientProxyCore.footprintRenderer.footprints.get(chunkKey0);
                final List<Footprint> toRemove = new ArrayList<>();

                if (footprintList != null) {
                    for (final Footprint footprint : footprintList) {
                        if (footprint.position.x > position.x && footprint.position.x < position.x + 1
                                && footprint.position.z > position.z
                                && footprint.position.z < position.z + 1) {
                            toRemove.add(footprint);
                        }
                    }
                }

                if (!toRemove.isEmpty()) {
                    footprintList.removeAll(toRemove);
                    ClientProxyCore.footprintRenderer.footprints.put(chunkKey0, footprintList);
                }
                break;
            case C_UPDATE_STATION_SPIN:
                if (playerBaseClient.worldObj.provider instanceof WorldProviderSpaceStation) {
                    ((WorldProviderSpaceStation) playerBaseClient.worldObj.provider).getSpinManager()
                            .setSpinRate((Float) this.data.get(0), (Boolean) this.data.get(1));
                }
                break;
            case C_UPDATE_STATION_DATA:
                if (playerBaseClient.worldObj.provider instanceof WorldProviderSpaceStation) {
                    ((WorldProviderSpaceStation) playerBaseClient.worldObj.provider).getSpinManager()
                            .setSpinCentre((Double) this.data.get(0), (Double) this.data.get(1));
                }
                break;
            case C_UPDATE_STATION_BOX:
                if (playerBaseClient.worldObj.provider instanceof WorldProviderSpaceStation) {
                    ((WorldProviderSpaceStation) playerBaseClient.worldObj.provider).getSpinManager().setSpinBox(
                            (Integer) this.data.get(0),
                            (Integer) this.data.get(1),
                            (Integer) this.data.get(2),
                            (Integer) this.data.get(3),
                            (Integer) this.data.get(4),
                            (Integer) this.data.get(5));
                }
                break;
            case C_UPDATE_THERMAL_LEVEL:
                stats.thermalLevel = (Integer) this.data.get(0);
                stats.thermalLevelNormalising = (Boolean) this.data.get(1);
                break;
            case C_DISPLAY_ROCKET_CONTROLS:
                player.addChatMessage(
                        new ChatComponentText(
                                GameSettings.getKeyDisplayString(KeyHandlerClient.spaceKey.getKeyCode()) + "  - "
                                        + GCCoreUtil.translate("gui.rocket.launch.name")));
                player.addChatMessage(
                        new ChatComponentText(
                                GameSettings.getKeyDisplayString(KeyHandlerClient.leftKey.getKeyCode()) + " / "
                                        + GameSettings.getKeyDisplayString(KeyHandlerClient.rightKey.getKeyCode())
                                        + "  - "
                                        + GCCoreUtil.translate("gui.rocket.turn.name")));
                player.addChatMessage(
                        new ChatComponentText(
                                GameSettings.getKeyDisplayString(KeyHandlerClient.accelerateKey.getKeyCode()) + " / "
                                        + GameSettings.getKeyDisplayString(KeyHandlerClient.decelerateKey.getKeyCode())
                                        + "  - "
                                        + GCCoreUtil.translate("gui.rocket.updown.name")));
                player.addChatMessage(
                        new ChatComponentText(
                                GameSettings.getKeyDisplayString(KeyHandlerClient.openFuelGui.getKeyCode())
                                        + "       - "
                                        + GCCoreUtil.translate("gui.rocket.inv.name")));
                break;
            case C_GET_CELESTIAL_BODY_LIST:
                String str = "";

                for (final CelestialBody cBody : GalaxyRegistry.getRegisteredPlanets().values()) {
                    str = str.concat(cBody.getUnlocalizedName() + ";");
                }

                for (final CelestialBody cBody : GalaxyRegistry.getRegisteredMoons().values()) {
                    str = str.concat(cBody.getUnlocalizedName() + ";");
                }

                for (final CelestialBody cBody : GalaxyRegistry.getRegisteredSatellites().values()) {
                    str = str.concat(cBody.getUnlocalizedName() + ";");
                }

                for (final SolarSystem solarSystem : GalaxyRegistry.getRegisteredSolarSystems().values()) {
                    str = str.concat(solarSystem.getUnlocalizedName() + ";");
                }

                GalacticraftCore.packetPipeline.sendToServer(
                        new PacketSimple(EnumSimplePacket.S_COMPLETE_CBODY_HANDSHAKE, new Object[] { str }));
                break;
            case C_UPDATE_ENERGYUNITS:
                CommandGCEnergyUnits.handleParamClientside((Integer) this.data.get(0));
                break;
            case C_RESPAWN_PLAYER:
                final WorldProvider provider = WorldUtil.getProviderForNameClient((String) this.data.get(0));
                final int dimID = provider.dimensionId;
                if (ConfigManagerCore.enableDebug) {
                    GCLog.info("DEBUG: Client receiving respawn packet for dim " + dimID);
                }
                final int par2 = (Integer) this.data.get(1);
                final String par3 = (String) this.data.get(2);
                final int par4 = (Integer) this.data.get(3);
                WorldUtil.forceRespawnClient(dimID, par2, par3, par4);
                break;
            case C_UPDATE_ARCLAMP_FACING:
                tile = player.worldObj.getTileEntity(
                        (Integer) this.data.get(0),
                        (Integer) this.data.get(1),
                        (Integer) this.data.get(2));
                final int facingNew = (Integer) this.data.get(3);
                if (tile instanceof TileEntityArclamp) {
                    ((TileEntityArclamp) tile).facing = facingNew;
                }
                break;
            case C_UPDATE_STATS:
                stats.buildFlags = (Integer) this.data.get(0);
                break;
            case C_UPDATE_VIEWSCREEN:
                tile = player.worldObj.getTileEntity(
                        (Integer) this.data.get(0),
                        (Integer) this.data.get(1),
                        (Integer) this.data.get(2));
                if (tile instanceof TileEntityScreen screenTile) {
                    final int screenType = (Integer) this.data.get(3);
                    final int flags = (Integer) this.data.get(4);
                    screenTile.imageType = screenType;
                    screenTile.connectedUp = (flags & 8) != 0;
                    screenTile.connectedDown = (flags & 4) != 0;
                    screenTile.connectedLeft = (flags & 2) != 0;
                    screenTile.connectedRight = (flags & 1) != 0;
                    screenTile.refreshNextTick(true);
                }
                break;
            case C_UPDATE_TELEMETRY:
                tile = player.worldObj.getTileEntity(
                        (Integer) this.data.get(0),
                        (Integer) this.data.get(1),
                        (Integer) this.data.get(2));
                if (tile instanceof TileEntityTelemetry) {
                    final String name = (String) this.data.get(3);
                    if (name.startsWith("$")) {
                        // It's a player name
                        ((TileEntityTelemetry) tile).clientClass = EntityPlayerMP.class;
                        final String strName = name.substring(1);
                        ((TileEntityTelemetry) tile).clientName = strName;
                        GameProfile profile = FMLClientHandler.instance().getClientPlayerEntity().getGameProfile();
                        if (!strName.equals(profile.getName())) {
                            profile = PlayerUtil.getOtherPlayerProfile(strName);
                            if (profile == null) {
                                final String strUUID = (String) this.data.get(9);
                                profile = PlayerUtil.makeOtherPlayerProfile(strName, strUUID);
                            }
                            if (VersionUtil.mcVersion1_7_10 && !profile.getProperties().containsKey("textures")) {
                                GalacticraftCore.packetPipeline.sendToServer(
                                        new PacketSimple(
                                                EnumSimplePacket.S_REQUEST_PLAYERSKIN,
                                                new Object[] { strName }));
                            }
                        }
                        ((TileEntityTelemetry) tile).clientGameProfile = profile;
                    } else {
                        ((TileEntityTelemetry) tile).clientClass = EntityList.stringToClassMapping.get(name);
                    }
                    ((TileEntityTelemetry) tile).clientData = new int[5];
                    for (int i = 4; i < 9; i++) {
                        ((TileEntityTelemetry) tile).clientData[i - 4] = (Integer) this.data.get(i);
                    }
                }
                break;
            case C_SEND_PLAYERSKIN:
                final String strName = (String) this.data.get(0);
                final String s1 = (String) this.data.get(1);
                final String s2 = (String) this.data.get(2);
                final String strUUID = (String) this.data.get(3);
                GameProfile gp = PlayerUtil.getOtherPlayerProfile(strName);
                if (gp == null) {
                    gp = PlayerUtil.makeOtherPlayerProfile(strName, strUUID);
                }
                gp.getProperties().put("textures", new Property("textures", s1, s2));
                break;
            case C_SEND_OVERWORLD_IMAGE:
                try {
                    final int cx = (Integer) this.data.get(0);
                    final int cz = (Integer) this.data.get(1);
                    final byte[] bytes = (byte[]) this.data.get(2);

                    try {
                        final File folder = new File(FMLClientHandler.instance().getClient().mcDataDir, "assets/temp");
                        if (folder.exists() || folder.mkdir()) {
                            MapUtil.getOverworldImageFromRaw(folder, cx, cz, bytes);
                        } else {
                            System.err.println("Cannot create directory %minecraftDir%/assets/temp!");
                        }
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                } catch (final Exception e) {

                }
                break;
            default:
                break;
        }
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
        final EntityPlayerMP playerBase = PlayerUtil.getPlayerBaseServerFromPlayer(player, false);

        if (playerBase == null) {
            return;
        }

        final GCPlayerStats stats = GCPlayerStats.get(playerBase);

        switch (this.type) {
            case S_RESPAWN_PLAYER:
                playerBase.playerNetServerHandler.sendPacket(
                        new S07PacketRespawn(
                                player.dimension,
                                player.worldObj.difficultySetting,
                                player.worldObj.getWorldInfo().getTerrainType(),
                                playerBase.theItemInWorldManager.getGameType()));
                break;
            case S_TELEPORT_ENTITY:
                try {
                    final Entity fakeEntity = new EntityCelestialFake(
                            player.worldObj,
                            player.posX,
                            player.posY,
                            player.posZ,
                            0.0F);
                    player.worldObj.spawnEntityInWorld(fakeEntity);
                    player.mountEntity(fakeEntity);
                    final WorldProvider provider = WorldUtil.getProviderForNameServer((String) this.data.get(0));
                    final Integer dim = provider.dimensionId;
                    GCLog.info("Found matching world (" + dim.toString() + ") for name: " + this.data.get(0));

                    if (playerBase.worldObj instanceof WorldServer world) {
                        WorldUtil.transferEntityToDimension(playerBase, dim, world, (Boolean) this.data.get(1), null);
                    }

                    stats.teleportCooldown = 10;
                    GalacticraftCore.packetPipeline
                            .sendTo(new PacketSimple(EnumSimplePacket.C_CLOSE_GUI, new Object[] {}), playerBase);
                } catch (final Exception e) {
                    GCLog.severe("Error occurred when attempting to transfer entity to dimension: " + this.data.get(0));
                    e.printStackTrace();
                }
                break;
            case S_IGNITE_ROCKET:
                if (!player.worldObj.isRemote && !player.isDead
                        && player.ridingEntity != null
                        && !player.ridingEntity.isDead
                        && player.ridingEntity instanceof EntityTieredRocket ship) {
                    if (!ship.landing) {

                        if (ship.hasValidFuel()) {
                            ship.igniteCheckingCooldown();
                            stats.launchAttempts = 0;
                        } else if (stats.chatCooldown == 0) {
                            player.addChatMessage(
                                    new ChatComponentText(GCCoreUtil.translate("gui.rocket.warning.nofuel")));
                            stats.chatCooldown = 250;
                        }
                    }
                }
                break;
            case S_OPEN_SCHEMATIC_PAGE:
                if (player != null) {
                    final ISchematicPage page = SchematicRegistry.getMatchingRecipeForID((Integer) this.data.get(0));

                    player.openGui(
                            GalacticraftCore.instance,
                            page.getGuiID(),
                            player.worldObj,
                            (int) player.posX,
                            (int) player.posY,
                            (int) player.posZ);
                }
                break;
            case S_OPEN_FUEL_GUI:
                if (player.ridingEntity instanceof EntityBuggy) {
                    GCCoreUtil.openBuggyInv(
                            playerBase,
                            (EntityBuggy) player.ridingEntity,
                            ((EntityBuggy) player.ridingEntity).getType());
                } else if (player.ridingEntity instanceof EntitySpaceshipBase) {
                    player.openGui(
                            GalacticraftCore.instance,
                            GuiIdsCore.ROCKET_INVENTORY,
                            player.worldObj,
                            (int) player.posX,
                            (int) player.posY,
                            (int) player.posZ);
                }
                break;
            case S_UPDATE_SHIP_YAW:
                if (player.ridingEntity instanceof EntitySpaceshipBase ship && ship != null) {
                    ship.rotationYaw = (Float) this.data.get(0);
                }
                break;
            case S_UPDATE_SHIP_PITCH:
                if (player.ridingEntity instanceof EntitySpaceshipBase ship && ship != null) {
                    ship.rotationPitch = (Float) this.data.get(0);
                }
                break;
            case S_BIND_SPACE_STATION_ID:
                final int homeID = (Integer) this.data.get(0);
                if ((!stats.spaceStationDimensionData.containsKey(homeID)
                        || stats.spaceStationDimensionData.get(homeID) == -1
                        || stats.spaceStationDimensionData.get(homeID) == 0)
                        && !ConfigManagerCore.disableSpaceStationCreation) {
                    if (playerBase.capabilities.isCreativeMode
                            || WorldUtil.getSpaceStationRecipe(homeID).matches(playerBase, true)) {
                        WorldUtil.bindSpaceStationToNewDimension(playerBase.worldObj, playerBase, homeID);
                    }
                }
                break;
            case S_UNLOCK_NEW_SCHEMATIC:
                final Container container = player.openContainer;

                if (container instanceof ContainerSchematic schematicContainer) {
                    ItemStack stack = schematicContainer.craftMatrix.getStackInSlot(0);

                    if (stack != null) {
                        final ISchematicPage page = SchematicRegistry.getMatchingRecipeForItemStack(stack);

                        if (page != null) {
                            SchematicRegistry.unlockNewPage(playerBase, stack);

                            if (--stack.stackSize <= 0) {
                                stack = null;
                            }

                            schematicContainer.craftMatrix.setInventorySlotContents(0, stack);
                            schematicContainer.craftMatrix.markDirty();

                            GalacticraftCore.packetPipeline.sendTo(
                                    new PacketSimple(
                                            EnumSimplePacket.C_ADD_NEW_SCHEMATIC,
                                            new Object[] { page.getPageID() }),
                                    playerBase);
                        }
                    }
                }
                break;
            case S_UPDATE_DISABLEABLE_BUTTON:
                final TileEntity tileAt = player.worldObj.getTileEntity(
                        (Integer) this.data.get(0),
                        (Integer) this.data.get(1),
                        (Integer) this.data.get(2));

                if (tileAt instanceof IDisableableMachine machine) {
                    machine.setDisabled((Integer) this.data.get(3), !machine.getDisabled((Integer) this.data.get(3)));
                }
                break;
            case S_ON_FAILED_CHEST_UNLOCK:
                if (stats.chatCooldown == 0) {
                    player.addChatMessage(
                            new ChatComponentText(
                                    GCCoreUtil.translateWithFormat("gui.chest.warning.wrongkey", this.data.get(0))));
                    stats.chatCooldown = 100;
                }
                break;
            case S_RENAME_SPACE_STATION:
                final SpaceStationWorldData ssdata = SpaceStationWorldData
                        .getStationData(playerBase.worldObj, (Integer) this.data.get(1), playerBase);

                if (ssdata != null && ssdata.getOwner().equalsIgnoreCase(player.getGameProfile().getName())) {
                    ssdata.setSpaceStationName((String) this.data.get(0));
                    ssdata.setDirty(true);
                }
                break;
            case S_OPEN_EXTENDED_INVENTORY:
                player.openGui(GalacticraftCore.instance, GuiIdsCore.EXTENDED_INVENTORY, player.worldObj, 0, 0, 0);
                break;
            case S_ON_ADVANCED_GUI_CLICKED_INT:
                final TileEntity tile1 = player.worldObj.getTileEntity(
                        (Integer) this.data.get(1),
                        (Integer) this.data.get(2),
                        (Integer) this.data.get(3));

                switch ((Integer) this.data.get(0)) {
                    case 0:
                        if (tile1 instanceof TileEntityAirLockController airlockController) {
                            airlockController.redstoneActivation = (Integer) this.data.get(4) == 1;
                        }
                        break;
                    case 1:
                        if (tile1 instanceof TileEntityAirLockController airlockController) {
                            airlockController.playerDistanceActivation = (Integer) this.data.get(4) == 1;
                        }
                        break;
                    case 2:
                        if (tile1 instanceof TileEntityAirLockController airlockController) {
                            airlockController.playerDistanceSelection = (Integer) this.data.get(4);
                        }
                        break;
                    case 3:
                        if (tile1 instanceof TileEntityAirLockController airlockController) {
                            airlockController.playerNameMatches = (Integer) this.data.get(4) == 1;
                        }
                        break;
                    case 4:
                        if (tile1 instanceof TileEntityAirLockController airlockController) {
                            airlockController.invertSelection = (Integer) this.data.get(4) == 1;
                        }
                        break;
                    case 5:
                        if (tile1 instanceof TileEntityAirLockController airlockController) {
                            airlockController.lastHorizontalModeEnabled = airlockController.horizontalModeEnabled;
                            airlockController.horizontalModeEnabled = (Integer) this.data.get(4) == 1;
                        }
                        break;
                    case 6:
                        if (tile1 instanceof IBubbleProvider distributor) {
                            distributor.setBubbleVisible((Integer) this.data.get(4) == 1);
                        }
                        break;
                    default:
                        break;
                }
                break;
            case S_ON_ADVANCED_GUI_CLICKED_STRING:
                final TileEntity tile2 = player.worldObj.getTileEntity(
                        (Integer) this.data.get(1),
                        (Integer) this.data.get(2),
                        (Integer) this.data.get(3));

                switch ((Integer) this.data.get(0)) {
                    case 0:
                        if (tile2 instanceof TileEntityAirLockController airlockController) {
                            airlockController.playerToOpenFor = (String) this.data.get(4);
                        }
                        break;
                    default:
                        break;
                }
                break;
            case S_UPDATE_SHIP_MOTION_Y:
                final int entityID = (Integer) this.data.get(0);
                final boolean up = (Boolean) this.data.get(1);

                final Entity entity2 = player.worldObj.getEntityByID(entityID);

                if (entity2 instanceof EntityAutoRocket autoRocket) {
                    autoRocket.motionY += up ? 0.02F : -0.02F;
                }

                break;
            case S_START_NEW_SPACE_RACE:
                final Integer teamID = (Integer) this.data.get(0);
                final String teamName = (String) this.data.get(1);
                final FlagData flagData = (FlagData) this.data.get(2);
                final Vector3 teamColor = (Vector3) this.data.get(3);
                final List<String> playerList = new ArrayList<>();

                for (int i = 4; i < this.data.size(); i++) {
                    playerList.add((String) this.data.get(i));
                }

                final boolean previousData = SpaceRaceManager.getSpaceRaceFromID(teamID) != null;

                final SpaceRace newRace = new SpaceRace(playerList, teamName, flagData, teamColor);

                if (teamID > 0) {
                    newRace.setSpaceRaceID(teamID);
                }

                SpaceRaceManager.addSpaceRace(newRace);

                if (previousData) {
                    SpaceRaceManager.sendSpaceRaceData(
                            null,
                            SpaceRaceManager.getSpaceRaceFromPlayer(playerBase.getGameProfile().getName()));
                }
                break;
            case S_REQUEST_FLAG_DATA:
                SpaceRaceManager.sendSpaceRaceData(
                        playerBase,
                        SpaceRaceManager.getSpaceRaceFromPlayer((String) this.data.get(0)));
                break;
            case S_INVITE_RACE_PLAYER:
                final EntityPlayerMP playerInvited = PlayerUtil
                        .getPlayerBaseServerFromPlayerUsername((String) this.data.get(0), true);
                if (playerInvited != null) {
                    final Integer teamInvitedTo = (Integer) this.data.get(1);
                    final SpaceRace race = SpaceRaceManager.getSpaceRaceFromID(teamInvitedTo);

                    if (race != null) {
                        GCPlayerStats.get(playerInvited).spaceRaceInviteTeamID = teamInvitedTo;
                        final String dA = EnumColor.DARK_AQUA.getCode();
                        final String bG = EnumColor.BRIGHT_GREEN.getCode();
                        final String dB = EnumColor.PURPLE.getCode();
                        String teamNameTotal = "";
                        final String[] teamNameSplit = race.getTeamName().split(" ");
                        for (final String teamNamePart : teamNameSplit) {
                            teamNameTotal = teamNameTotal.concat(dB + teamNamePart + " ");
                        }
                        playerInvited.addChatMessage(
                                new ChatComponentText(
                                        dA + GCCoreUtil.translateWithFormat(
                                                "gui.spaceRace.chat.inviteReceived",
                                                bG + player.getGameProfile().getName() + dA)
                                                + "  "
                                                + GCCoreUtil.translateWithFormat(
                                                        "gui.spaceRace.chat.toJoin",
                                                        teamNameTotal,
                                                        EnumColor.AQUA + "/joinrace" + dA)).setChatStyle(
                                                                new ChatStyle()
                                                                        .setColor(EnumChatFormatting.DARK_AQUA)));
                    }
                }
                break;
            case S_REMOVE_RACE_PLAYER:
                final Integer teamInvitedTo = (Integer) this.data.get(1);
                final SpaceRace race = SpaceRaceManager.getSpaceRaceFromID(teamInvitedTo);

                if (race != null) {
                    final String playerToRemove = (String) this.data.get(0);

                    if (!race.getPlayerNames().remove(playerToRemove)) {
                        player.addChatMessage(
                                new ChatComponentText(
                                        GCCoreUtil.translateWithFormat("gui.spaceRace.chat.notFound", playerToRemove)));
                    } else {
                        SpaceRaceManager.onPlayerRemoval(playerToRemove, race);
                    }
                }
                break;
            case S_ADD_RACE_PLAYER:
                final Integer teamToAddPlayer = (Integer) this.data.get(1);
                final SpaceRace spaceRaceToAddPlayer = SpaceRaceManager.getSpaceRaceFromID(teamToAddPlayer);

                if (spaceRaceToAddPlayer != null) {
                    final String playerToAdd = (String) this.data.get(0);

                    if (!spaceRaceToAddPlayer.getPlayerNames().contains(playerToAdd)) {
                        SpaceRace oldRace = null;
                        while ((oldRace = SpaceRaceManager.getSpaceRaceFromPlayer(playerToAdd)) != null) {
                            SpaceRaceManager.removeSpaceRace(oldRace);
                        }

                        spaceRaceToAddPlayer.getPlayerNames().add(playerToAdd);
                        SpaceRaceManager.sendSpaceRaceData(null, spaceRaceToAddPlayer);

                        for (final String member : spaceRaceToAddPlayer.getPlayerNames()) {
                            final EntityPlayerMP memberObj = PlayerUtil
                                    .getPlayerForUsernameVanilla(MinecraftServer.getServer(), member);

                            if (memberObj != null) {
                                memberObj.addChatMessage(
                                        new ChatComponentText(
                                                EnumColor.DARK_AQUA + GCCoreUtil.translateWithFormat(
                                                        "gui.spaceRace.chat.addSuccess",
                                                        EnumColor.BRIGHT_GREEN + playerToAdd + EnumColor.DARK_AQUA))
                                                                .setChatStyle(
                                                                        new ChatStyle().setColor(
                                                                                EnumChatFormatting.DARK_AQUA)));
                            }
                        }
                    } else {
                        player.addChatMessage(
                                new ChatComponentText(GCCoreUtil.translate("gui.spaceRace.chat.alreadyPart"))
                                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_RED)));
                    }
                }
                break;
            case S_COMPLETE_CBODY_HANDSHAKE:
                final String completeList = (String) this.data.get(0);
                final List<String> clientObjects = Arrays.asList(completeList.split(";"));
                final List<String> serverObjects = Lists.newArrayList();
                String missingObjects = "";

                for (final CelestialBody cBody : GalaxyRegistry.getRegisteredPlanets().values()) {
                    serverObjects.add(cBody.getUnlocalizedName());
                }

                for (final CelestialBody cBody : GalaxyRegistry.getRegisteredMoons().values()) {
                    serverObjects.add(cBody.getUnlocalizedName());
                }

                for (final CelestialBody cBody : GalaxyRegistry.getRegisteredSatellites().values()) {
                    serverObjects.add(cBody.getUnlocalizedName());
                }

                for (final SolarSystem solarSystem : GalaxyRegistry.getRegisteredSolarSystems().values()) {
                    serverObjects.add(solarSystem.getUnlocalizedName());
                }

                for (final String str : serverObjects) {
                    if (!clientObjects.contains(str)) {
                        missingObjects = missingObjects.concat(str + "\n");
                    }
                }

                if (missingObjects.length() > 0) {
                    playerBase.playerNetServerHandler
                            .kickPlayerFromServer("Missing Galacticraft Celestial Objects:\n\n " + missingObjects);
                }

                break;
            case S_REQUEST_GEAR_DATA:
                final String name = (String) this.data.get(0);
                final EntityPlayerMP e = PlayerUtil.getPlayerBaseServerFromPlayerUsername(name, true);
                if (e != null) {
                    GCPlayerHandler.checkGear(e, GCPlayerStats.get(e), true);
                }
                break;
            case S_REQUEST_ARCLAMP_FACING:
                final TileEntity tileAL = player.worldObj.getTileEntity(
                        (Integer) this.data.get(0),
                        (Integer) this.data.get(1),
                        (Integer) this.data.get(2));
                if (tileAL instanceof TileEntityArclamp) {
                    ((TileEntityArclamp) tileAL).updateClientFlag = true;
                }
                break;
            case S_BUILDFLAGS_UPDATE:
                stats.buildFlags = (Integer) this.data.get(0);
                break;
            case S_UPDATE_VIEWSCREEN_REQUEST:
                final TileEntity tile = player.worldObj.getTileEntity(
                        (Integer) this.data.get(1),
                        (Integer) this.data.get(2),
                        (Integer) this.data.get(3));
                if (tile instanceof TileEntityScreen) {
                    ((TileEntityScreen) tile).updateClients();
                }
                break;
            case S_REQUEST_OVERWORLD_IMAGE:
                MapUtil.sendOverworldToClient(playerBase);
                // if (GalacticraftCore.enableJPEG)
                // {
                // ChunkCoordIntPair chunkCoordIntPair = new
                // ChunkCoordIntPair((int)Math.floor(stats.coordsTeleportedFromX) >> 4,
                // (int)Math.floor(stats.coordsTeleportedFromZ) >> 4);
                // File baseFolder = new
                // File(MinecraftServer.getServer().worldServerForDimension(0).getChunkSaveLocation(),
                // "galacticraft/overworldMap");
                // if (!baseFolder.exists())
                // {
                // if (!baseFolder.mkdirs())
                // {
                // GCLog.severe("Base folder(s) could not be created: " +
                // baseFolder.getAbsolutePath());
                // }
                // }
                // File outputFile = new File(baseFolder, "" + chunkCoordIntPair.chunkXPos + "_"
                // +
                // chunkCoordIntPair.chunkZPos + ".bin");
                // boolean success = true;
                //
                // if (!outputFile.exists() || !outputFile.isFile())
                // {
                // success = false;
                // //BufferedImage image = new BufferedImage(400, 400,
                // BufferedImage.TYPE_INT_RGB);
                // //MapUtil.getLocalMap(MinecraftServer.getServer().worldServerForDimension(0),
                // chunkCoordIntPair.chunkXPos, chunkCoordIntPair.chunkZPos, image);
                // int scale = 4;
                // //ConfigManagerCore.mapsize
                // MapUtil.getBiomeMapForCoords(MinecraftServer.getServer().worldServerForDimension(0),
                // chunkCoordIntPair.chunkXPos, chunkCoordIntPair.chunkZPos, scale, 64, 64,
                // outputFile, playerBase);
                // }
                //
                // if (success)
                // {
                // try
                // {
                // byte[] bytes = FileUtils.readFileToByteArray(outputFile);
                // //Class c =
                // Launch.classLoader.loadClass("org.apache.commons.codec.binary.Base64");
                // //byte[] bytes64 = (byte[])c.getMethod("encodeBase64",
                // byte[].class).invoke(null,
                // bytes);
                // GalacticraftCore.packetPipeline.sendTo(new
                // PacketSimple(EnumSimplePacket.C_SEND_OVERWORLD_IMAGE, new Object[] { bytes }
                // ), playerBase);
                // }
                // catch (Exception ex)
                // {
                // System.err.println("Error sending overworld image to player.");
                // ex.printStackTrace();
                // }
                // }
                // }
                break;
            case S_REQUEST_MAP_IMAGE:
                final int dim = (Integer) this.data.get(0);
                final int cx = (Integer) this.data.get(1);
                final int cz = (Integer) this.data.get(2);
                MapUtil.sendOrCreateMap(WorldUtil.getProviderForDimensionServer(dim).worldObj, cx, cz, playerBase);
                break;
            case S_REQUEST_PLAYERSKIN:
                final String strName = (String) this.data.get(0);
                final EntityPlayerMP playerRequested = FMLServerHandler.instance().getServer().getConfigurationManager()
                        .func_152612_a(strName);

                // Player not online
                if (playerRequested == null) {
                    return;
                }

                final GameProfile gp = playerRequested.getGameProfile();
                if (gp == null) {
                    return;
                }

                final Property property = (Property) Iterables
                        .getFirst(gp.getProperties().get("textures"), (Object) null);
                if (property == null) {
                    return;
                }
                GalacticraftCore.packetPipeline.sendTo(
                        new PacketSimple(
                                EnumSimplePacket.C_SEND_PLAYERSKIN,
                                new Object[] { strName, property.getValue(), property.getSignature(),
                                        playerRequested.getUniqueID().toString() }),
                        playerBase);
                break;
            case S_CANCEL_TELEPORTATION:
                WorldUtil.cancelTeleportation(playerBase);
                break;
            default:
                break;
        }
    }

    /*
     * BEGIN "net.minecraft.network.Packet" IMPLEMENTATION This is for handling server->client packets before the player
     * has joined the world
     */

    @Override
    public void readPacketData(PacketBuffer var1) {
        this.decodeInto(null, var1);
    }

    @Override
    public void writePacketData(PacketBuffer var1) {
        this.encodeInto(null, var1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void processPacket(INetHandler var1) {
        if (this.type != EnumSimplePacket.C_UPDATE_SPACESTATION_LIST
                && this.type != EnumSimplePacket.C_UPDATE_PLANETS_LIST
                && this.type != EnumSimplePacket.C_UPDATE_CONFIGS) {
            return;
        }

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            this.handleClientSide(FMLClientHandler.instance().getClientPlayerEntity());
        }
    }

    /*
     * END "net.minecraft.network.Packet" IMPLEMENTATION This is for handling server->client packets before the player
     * has joined the world
     */
}
