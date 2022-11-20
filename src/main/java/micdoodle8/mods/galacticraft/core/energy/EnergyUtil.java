package micdoodle8.mods.galacticraft.core.energy;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyConductor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import java.lang.reflect.Method;
import java.util.List;
import micdoodle8.mods.galacticraft.api.transmission.NetworkType;
import micdoodle8.mods.galacticraft.api.transmission.tile.IConductor;
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector;
import micdoodle8.mods.galacticraft.api.transmission.tile.IElectrical;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.core.energy.tile.EnergyStorageTile;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseConductor;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class EnergyUtil {
    private static boolean isMekLoaded = EnergyConfigHandler.isMekanismLoaded();
    private static boolean isRFLoaded = EnergyConfigHandler.isRFAPILoaded();
    private static boolean isRF1Loaded = EnergyConfigHandler.isRFAPIv1Loaded();
    private static boolean isRF2Loaded = EnergyConfigHandler.isRFAPIv2Loaded();
    private static boolean isIC2Loaded = EnergyConfigHandler.isIndustrialCraft2Loaded();
    private static boolean isBCLoaded = EnergyConfigHandler.isBuildcraftLoaded();
    private static boolean isBC6Loaded = isBCLoaded && EnergyConfigHandler.getBuildcraftVersion() == 6;
    private static boolean isBCReallyLoaded = EnergyConfigHandler.isBuildcraftReallyLoaded();

    public static boolean voltageParameterIC2 = false;
    public static Method demandedEnergyIC2 = null;
    public static Method injectEnergyIC2 = null;
    public static Method offeredEnergyIC2 = null;
    public static Method drawEnergyIC2 = null;
    private static Class<?> clazzMekCable = null;
    public static Class<?> clazzEnderIOCable = null;
    public static Class<?> clazzMFRRednetEnergyCable = null;
    public static Class<?> clazzRailcraftEngine = null;
    private static Class<?> clazzPipeTile = null;
    private static Class<?> clazzPipeWood = null;
    public static boolean initialisedIC2Methods = EnergyUtil.initialiseIC2Methods();

    /**
     * Tests whether an IConductor tile (a GC Aluminium Wire) can connect on each
     * its 6 sides Returns a 6 member array, containing for each of the 6 standard
     * directions: the connectable TileEntity if a connection was found, or else
     * null. (This saves on the calling code having to use World.getTileEntity(x, y,
     * z) a second time.)
     *
     * @param tile
     * @return
     */
    public static TileEntity[] getAdjacentPowerConnections(TileEntity tile) {
        final TileEntity[] adjacentConnections = new TileEntity[6];

        final BlockVec3 thisVec = new BlockVec3(tile);
        final World world = tile.getWorldObj();
        for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            final TileEntity tileEntity = thisVec.getTileEntityOnSide(world, direction);

            if (tileEntity == null) {
                continue;
            }

            if (tileEntity instanceof IConnector) {
                if (((IConnector) tileEntity).canConnect(direction.getOpposite(), NetworkType.POWER)) {
                    adjacentConnections[direction.ordinal()] = tileEntity;
                }
                continue;
            }

            if (isBCReallyLoaded) {
                // Do not connect GC wires directly to BC pipes of any type
                if (clazzPipeTile.isInstance(tileEntity)) {
                    continue;
                }
            }

            if (isIC2Loaded && tileEntity instanceof IEnergyTile) {
                if (tileEntity instanceof IEnergyConductor) {
                    continue;
                }

                boolean doneIC2 = false;
                if (tileEntity instanceof IEnergyAcceptor) {
                    doneIC2 = true;
                    if (((IEnergyAcceptor) tileEntity).acceptsEnergyFrom(tile, direction.getOpposite())) {
                        adjacentConnections[direction.ordinal()] = tileEntity;
                    }
                }
                if (tileEntity instanceof IEnergyEmitter) {
                    doneIC2 = true;
                    if (((IEnergyEmitter) tileEntity).emitsEnergyTo(tile, direction.getOpposite())) {
                        adjacentConnections[direction.ordinal()] = tileEntity;
                    }
                }

                if (doneIC2) {
                    continue;
                }
            }

            if (isRFLoaded && tileEntity instanceof IEnergyConnection) {
                if (isRF2Loaded && (tileEntity instanceof IEnergyProvider || tileEntity instanceof IEnergyReceiver)
                        || isRF1Loaded && tileEntity instanceof IEnergyHandler
                        || clazzRailcraftEngine != null && clazzRailcraftEngine.isInstance(tileEntity)) {
                    // Do not connect GC wires directly to power conduits
                    if (clazzEnderIOCable != null && clazzEnderIOCable.isInstance(tileEntity)) {
                        continue;
                    }
                    if (clazzMFRRednetEnergyCable != null && clazzMFRRednetEnergyCable.isInstance(tileEntity)) {
                        continue;
                    }

                    if (((IEnergyConnection) tileEntity).canConnectEnergy(direction.getOpposite())) {
                        adjacentConnections[direction.ordinal()] = tileEntity;
                    }
                }
                continue;
            }
        }

        return adjacentConnections;
    }

    /**
     * Similar to getAdjacentPowerConnections but specific to energy receivers only
     * Adds the adjacent power connections found to the passed acceptors, directions
     * parameter Lists (Note: an acceptor can therefore sometimes be entered in the
     * Lists more than once, with a different direction each time: this would
     * represent GC wires connected to the acceptor on more than one side.)
     *
     * @param conductor
     * @param acceptors
     * @param directions
     * @throws Exception
     */
    public static void setAdjacentPowerConnections(
            TileEntity conductor, List<TileEntity> acceptors, List<ForgeDirection> directions) throws Exception {
        final BlockVec3 thisVec = new BlockVec3(conductor);
        final World world = conductor.getWorldObj();
        for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            final TileEntity tileEntity = thisVec.getTileEntityOnSide(world, direction);

            if (tileEntity == null || tileEntity instanceof IConductor) // world.getTileEntity will not have returned an
            // invalid tile, invalid tiles are
            // null
            {
                continue;
            }

            final ForgeDirection sideFrom = direction.getOpposite();

            if (tileEntity instanceof IElectrical) {
                if (((IElectrical) tileEntity).canConnect(sideFrom, NetworkType.POWER)) {
                    acceptors.add(tileEntity);
                    directions.add(sideFrom);
                }
                continue;
            }

            if (isBCReallyLoaded && clazzPipeTile.isInstance(tileEntity)) {
                continue;
            }

            if (isIC2Loaded && tileEntity instanceof IEnergyAcceptor) {
                if (tileEntity instanceof IEnergyConductor) {
                    continue;
                }
                if (((IEnergyAcceptor) tileEntity).acceptsEnergyFrom((TileEntity) conductor, sideFrom)) {
                    acceptors.add(tileEntity);
                    directions.add(sideFrom);
                }
                continue;
            }

            if (isRF2Loaded && tileEntity instanceof IEnergyReceiver
                    || isRF1Loaded && tileEntity instanceof IEnergyHandler) {
                if (clazzEnderIOCable != null && clazzEnderIOCable.isInstance(tileEntity)) {
                    continue;
                }
                if (clazzMFRRednetEnergyCable != null && clazzMFRRednetEnergyCable.isInstance(tileEntity)) {
                    continue;
                }

                if (((IEnergyConnection) tileEntity).canConnectEnergy(sideFrom)) {
                    acceptors.add(tileEntity);
                    directions.add(sideFrom);
                }
                continue;
            }
        }
        return;
    }

    public static float otherModsEnergyTransfer(
            TileEntity tileAdj, ForgeDirection inputAdj, float toSend, boolean simulate) {
        if (isIC2Loaded && !EnergyConfigHandler.disableIC2Output && tileAdj instanceof IEnergySink) {
            double demanded = 0;
            try {
                demanded = (Double) EnergyUtil.demandedEnergyIC2.invoke(tileAdj);
            } catch (final Exception ex) {
                if (ConfigManagerCore.enableDebug) {
                    ex.printStackTrace();
                }
            }

            if (simulate) {
                return Math.min(toSend, (float) demanded / EnergyConfigHandler.TO_IC2_RATIO);
            }

            final double energySendingIC2 = Math.min(toSend * EnergyConfigHandler.TO_IC2_RATIO, demanded);
            if (energySendingIC2 >= 1D) {
                double result = 0;
                try {
                    if (EnergyUtil.voltageParameterIC2) {
                        result = energySendingIC2
                                - (Double) EnergyUtil.injectEnergyIC2.invoke(tileAdj, inputAdj, energySendingIC2, 120D);
                    } else {
                        result = energySendingIC2
                                - (Double) EnergyUtil.injectEnergyIC2.invoke(tileAdj, inputAdj, energySendingIC2);
                    }
                } catch (final Exception ex) {
                    if (ConfigManagerCore.enableDebug) {
                        ex.printStackTrace();
                    }
                }
                if (result < 0D) {
                    return 0F;
                }
                return (float) result / EnergyConfigHandler.TO_IC2_RATIO;
            }
        } else if (isRF1Loaded && !EnergyConfigHandler.disableRFOutput && tileAdj instanceof IEnergyHandler) {
            final float sent = ((IEnergyHandler) tileAdj)
                            .receiveEnergy(
                                    inputAdj,
                                    MathHelper.floor_float(toSend * EnergyConfigHandler.TO_RF_RATIO),
                                    simulate)
                    / EnergyConfigHandler.TO_RF_RATIO;
            // GCLog.debug("Beam/storage offering RF1 up to " + toSend + " into pipe, it
            // accepted " + sent);
            return sent;
        } else if (isRF2Loaded && !EnergyConfigHandler.disableRFOutput && tileAdj instanceof IEnergyReceiver) {
            final float sent = ((IEnergyReceiver) tileAdj)
                            .receiveEnergy(
                                    inputAdj,
                                    MathHelper.floor_float(toSend * EnergyConfigHandler.TO_RF_RATIO),
                                    simulate)
                    / EnergyConfigHandler.TO_RF_RATIO;
            // GCLog.debug("Beam/storage offering RF2 up to " + toSend + " into pipe, it
            // accepted " + sent);
            return sent;
        }
        return 0F;
    }

    public static float otherModsEnergyExtract(
            TileEntity tileAdj, ForgeDirection inputAdj, float toPull, boolean simulate) {
        if (isIC2Loaded && !EnergyConfigHandler.disableIC2Input && tileAdj instanceof IEnergySource) {
            double offered = 0;
            try {
                offered = (Double) EnergyUtil.offeredEnergyIC2.invoke(tileAdj);
            } catch (final Exception ex) {
                if (ConfigManagerCore.enableDebug) {
                    ex.printStackTrace();
                }
            }

            if (simulate) {
                return Math.min(toPull, (float) offered / EnergyConfigHandler.TO_IC2_RATIO);
            }

            final double energySendingIC2 = Math.min(toPull * EnergyConfigHandler.TO_IC2_RATIO, offered);
            if (energySendingIC2 >= 1D) {
                double resultIC2 = 0;
                try {
                    resultIC2 = energySendingIC2 - (Double) EnergyUtil.drawEnergyIC2.invoke(tileAdj, energySendingIC2);
                } catch (final Exception ex) {
                    if (ConfigManagerCore.enableDebug) {
                        ex.printStackTrace();
                    }
                }
                if (resultIC2 < 0D) {
                    resultIC2 = 0D;
                }
                return (float) resultIC2 / EnergyConfigHandler.TO_IC2_RATIO;
            }
        } else if (isRF2Loaded && !EnergyConfigHandler.disableRFInput && tileAdj instanceof IEnergyProvider) {
            final float sent = ((IEnergyProvider) tileAdj)
                            .extractEnergy(
                                    inputAdj,
                                    MathHelper.floor_float(toPull * EnergyConfigHandler.TO_RF_RATIO),
                                    simulate)
                    / EnergyConfigHandler.TO_RF_RATIO;
            return sent;
        } else if (isRF1Loaded && !EnergyConfigHandler.disableRFInput && tileAdj instanceof IEnergyHandler) {
            final float sent = ((IEnergyHandler) tileAdj)
                            .extractEnergy(
                                    inputAdj,
                                    MathHelper.floor_float(toPull * EnergyConfigHandler.TO_RF_RATIO),
                                    simulate)
                    / EnergyConfigHandler.TO_RF_RATIO;
            return sent;
        }

        return 0F;
    }

    /**
     * Test whether an energy connection can be made to a tile using other mods'
     * energy methods.
     *
     * Parameters:
     *
     * @param tileAdj  - the tile under test, it might be an energy tile from
     *                 another mod
     * @param inputAdj - the energy input side for that tile which is under test
     */
    public static boolean otherModCanReceive(TileEntity tileAdj, ForgeDirection inputAdj) {
        if (tileAdj instanceof TileBaseConductor || tileAdj instanceof EnergyStorageTile) {
            return false; // Do not try using other mods' methods to connect to GC's own tiles
        }

        if (isIC2Loaded && tileAdj instanceof IEnergyAcceptor) {
            return ((IEnergyAcceptor) tileAdj).acceptsEnergyFrom(null, inputAdj);
        } else if (isRF1Loaded && tileAdj instanceof IEnergyHandler
                || isRF2Loaded && tileAdj instanceof IEnergyReceiver) {
            return ((IEnergyConnection) tileAdj).canConnectEnergy(inputAdj);
        }

        return false;
    }

    /**
     * Test whether a tile can output energy using other mods' energy methods.
     * Currently restricted to IC2 and RF mods - Mekanism tiles do not provide an
     * interface to "output" energy
     *
     * Parameters:
     *
     * @param tileAdj - the tile under test, it might be an energy tile from another
     *                mod
     * @param side    - the energy output side for that tile which is under test
     */
    public static boolean otherModCanProduce(TileEntity tileAdj, ForgeDirection side) {
        if (tileAdj instanceof TileBaseConductor || tileAdj instanceof EnergyStorageTile) {
            return false; // Do not try using other mods' methods to connect to GC's own tiles
        }

        if (isIC2Loaded && tileAdj instanceof IEnergyEmitter) {
            return ((IEnergyEmitter) tileAdj).emitsEnergyTo(null, side);
        } else if (isRF1Loaded && tileAdj instanceof IEnergyHandler
                || isRF2Loaded && tileAdj instanceof IEnergyProvider) {
            return ((IEnergyConnection) tileAdj).canConnectEnergy(side);
        }

        return false;
    }

    public static boolean initialiseIC2Methods() {
        // Initialise a couple of non-IC2 classes
        try {
            clazzMekCable = Class.forName("codechicken.multipart.TileMultipart");
        } catch (final Exception e) {
        }
        try {
            clazzEnderIOCable = Class.forName("crazypants.enderio.conduit.TileConduitBundle");
        } catch (final Exception e) {
        }
        try {
            clazzMFRRednetEnergyCable =
                    Class.forName("powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetEnergy");
        } catch (final Exception e) {
        }
        try {
            clazzRailcraftEngine = Class.forName("mods.railcraft.common.blocks.machine.beta.TileEngine");
        } catch (final Exception e) {
        }
        try {
            clazzPipeTile = Class.forName("buildcraft.transport.TileGenericPipe");
        } catch (final Exception e) {
        }
        try {
            clazzPipeWood = Class.forName("buildcraft.transport.pipes.PipePowerWood");
        } catch (final Exception e) {
        }

        if (isIC2Loaded) {
            GCLog.debug("Initialising IC2 methods OK");

            try {
                final Class<?> clazz = Class.forName("ic2.api.energy.tile.IEnergySink");

                GCLog.debug("Found IC2 IEnergySink class OK");

                try {
                    // 1.7.2 version
                    EnergyUtil.demandedEnergyIC2 = clazz.getMethod("demandedEnergyUnits");
                } catch (final Exception e) {
                    // if that fails, try 1.7.10 version
                    try {
                        EnergyUtil.demandedEnergyIC2 = clazz.getMethod("getDemandedEnergy");
                    } catch (final Exception ee) {
                        ee.printStackTrace();
                    }
                }

                GCLog.debug("Set IC2 demandedEnergy method OK");

                try {
                    // 1.7.2 version
                    EnergyUtil.injectEnergyIC2 =
                            clazz.getMethod("injectEnergyUnits", ForgeDirection.class, double.class);
                    GCLog.debug("IC2 inject 1.7.2 succeeded");
                } catch (final Exception e) {
                    // if that fails, try 1.7.10 version
                    try {
                        EnergyUtil.injectEnergyIC2 =
                                clazz.getMethod("injectEnergy", ForgeDirection.class, double.class, double.class);
                        EnergyUtil.voltageParameterIC2 = true;
                        GCLog.debug("IC2 inject 1.7.10 succeeded");
                    } catch (final Exception ee) {
                        ee.printStackTrace();
                    }
                }

                final Class<?> clazzSource = Class.forName("ic2.api.energy.tile.IEnergySource");
                EnergyUtil.offeredEnergyIC2 = clazzSource.getMethod("getOfferedEnergy");
                EnergyUtil.drawEnergyIC2 = clazzSource.getMethod("drawEnergy", double.class);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        if (clazzPipeTile == null) {
            isBCReallyLoaded = false;
        }

        return true;
    }
}
