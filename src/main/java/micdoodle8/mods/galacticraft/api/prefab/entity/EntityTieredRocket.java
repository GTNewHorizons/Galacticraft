package micdoodle8.mods.galacticraft.api.prefab.entity;

import cpw.mods.fml.common.FMLCommonHandler;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Iterator;
import micdoodle8.mods.galacticraft.api.entity.ICameraZoomEntity;
import micdoodle8.mods.galacticraft.api.entity.IDockable;
import micdoodle8.mods.galacticraft.api.entity.IRocketType;
import micdoodle8.mods.galacticraft.api.entity.IWorldTransferCallback;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.IExitHeight;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;

/**
 * Do not include this prefab class in your released mod download.
 */
public abstract class EntityTieredRocket extends EntityAutoRocket
        implements IRocketType, IDockable, IInventory, IWorldTransferCallback, ICameraZoomEntity {
    public EnumRocketType rocketType;
    public float rumble;
    public int launchCooldown;
    private final ArrayList<BlockVec3> preGenList = new ArrayList();
    private Iterator<BlockVec3> preGenIterator = null;
    static boolean preGenInProgress = false;

    public EntityTieredRocket(World par1World) {
        super(par1World);
        this.setSize(0.98F, 4F);
        this.yOffset = this.height / 2.0F;
    }

    public EntityTieredRocket(World world, double posX, double posY, double posZ) {
        super(world, posX, posY, posZ);
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        // TODO reimplement once Resonant Engine comes out of alpha, bug DarkGuardsman
        // for info
        // if (Loader.isModLoaded("ICBM|Explosion"))
        // {
        // try
        // {
        // Class.forName("icbm.api.RadarRegistry").getMethod("register",
        // Entity.class).invoke(null, this);
        // }
        // catch (Exception e)
        // {
        // e.printStackTrace();
        // }
        // }
    }

    @Override
    public void setDead() {
        if (!this.isDead) {
            super.setDead();
        }

        // TODO reimplement once Resonant Engine comes out of alpha, bug Dark for info
        // if (Loader.isModLoaded("ICBM|Explosion"))
        // {
        // try
        // {
        // Class.forName("icbm.api.RadarRegistry").getMethod("unregister",
        // Entity.class).invoke(null, this);
        // }
        // catch (Exception e)
        // {
        // e.printStackTrace();
        // }
        // }
    }

    public void igniteCheckingCooldown() {
        if (!this.worldObj.isRemote && this.launchCooldown <= 0) {
            if (this.launchPhase != EnumLaunchPhase.IGNITED.ordinal()) {
                this.setFrequency();
                this.initiatePlanetsPreGen(this.chunkCoordX, this.chunkCoordZ);
                this.ignite();
            }
        }
    }

    @Override
    public boolean hasValidFuel() {
        if (this.destinationFrequency != -1) {
            if (this.fuelTank.getFluidAmount() < this.fuelToDrain()) {
                return false;
            }
            return true;
        }
        return this.fuelTank.getFluidAmount() > 0;
    }

    private void initiatePlanetsPreGen(int cx, int cz) {
        this.preGenList.clear();

        // Pre-generate terrain on all possible destination planets if the destination
        // is not being controlled by a
        // Launch Controller
        // (note: this does NOT include the Moon!)

        // This generates with a chunk radius of 12: so for 2 planets that's 1250 chunks
        // to pregen
        // It starts at the centre and generates in circles radiating out in case it
        // doesn't have time to finish
        // These will be done: 2 chunks per tick during IGNITE phase (so 800 chunks
        // during the 20 second launch
        // countdown)
        // then the ones that are left 1 chunk per tick during flight (normally flight
        // will last more than 450 ticks)
        // If the server is at less than 20tps then maybe some of the outermost chunks
        // won't be pre-generated but that's
        // probably OK
        if (this.destinationFrequency == -1 && !EntityTieredRocket.preGenInProgress) {
            final ArrayList<Integer> toPreGen = new ArrayList();
            for (final Planet planet : GalaxyRegistry.getRegisteredPlanets().values()) {
                if (planet.getDimensionID() == this.dimension) {
                    continue;
                }
                if (planet.getReachable() && planet.getTierRequirement() <= this.getRocketTier()) {
                    toPreGen.add(planet.getDimensionID());
                }
            }

            if (toPreGen.size() > 0) {
                for (final Integer dimID : toPreGen) {
                    this.preGenList.add(new BlockVec3(cx, dimID, cz));
                    if (ConfigManagerCore.enableDebug) {
                        GCLog.info("Starting terrain pregen for dimension " + dimID + " at " + (cx * 16 + 8) + ", "
                                + (cz * 16 + 8));
                    }
                }
                for (int r = 1; r < 12; r++) {
                    final int xmin = cx - r;
                    final int xmax = cx + r;
                    final int zmin = cz - r;
                    final int zmax = cz + r;
                    for (int i = -r; i < r; i++) {
                        for (final Integer dimID : toPreGen) {
                            this.preGenList.add(new BlockVec3(xmin, dimID, cz + i));
                            this.preGenList.add(new BlockVec3(xmax, dimID, cz - i));
                            this.preGenList.add(new BlockVec3(cx - i, dimID, zmin));
                            this.preGenList.add(new BlockVec3(cx + i, dimID, zmax));
                        }
                    }
                }
                this.preGenIterator = this.preGenList.iterator();
                EntityTieredRocket.preGenInProgress = true;
            }
        } else {
            this.preGenIterator = null;
        }
    }

    @Override
    public void onUpdate() {
        if (this.getWaitForPlayer()) {
            if (this.riddenByEntity != null) {
                if (this.ticks >= 40) {
                    if (!this.worldObj.isRemote) {
                        final Entity e = this.riddenByEntity;
                        e.mountEntity(null);
                        e.mountEntity(this);
                        if (ConfigManagerCore.enableDebug) {
                            GCLog.info("Remounting player in rocket.");
                        }
                    }

                    this.setWaitForPlayer(false);
                    this.motionY = -0.5D;
                } else {
                    this.motionX = this.motionY = this.motionZ = 0.0D;
                    this.riddenByEntity.motionX = this.riddenByEntity.motionY = this.riddenByEntity.motionZ = 0;
                }
            } else {
                this.motionX = this.motionY = this.motionZ = 0.0D;
            }
        }

        super.onUpdate();

        if (!this.worldObj.isRemote) {
            if (this.launchCooldown > 0) {
                this.launchCooldown--;
            }

            if (this.preGenIterator != null) {
                if (this.preGenIterator.hasNext()) {
                    final MinecraftServer mcserver = FMLCommonHandler.instance().getMinecraftServerInstance();
                    // mcserver can be null if client switches to a LAN server
                    if (mcserver != null) {
                        BlockVec3 coords = this.preGenIterator.next();
                        World w = mcserver.worldServerForDimension(coords.y);
                        if (w != null) {
                            w.getChunkFromChunkCoords(coords.x, coords.z);
                            // Pregen a second chunk if still on launchpad (low strain on server)
                            if (this.launchPhase != EnumLaunchPhase.LAUNCHED.ordinal()
                                    && this.preGenIterator.hasNext()) {
                                coords = this.preGenIterator.next();
                                w = mcserver.worldServerForDimension(coords.y);
                                w.getChunkFromChunkCoords(coords.x, coords.z);
                            }
                        }
                    }
                } else {
                    this.preGenIterator = null;
                    EntityTieredRocket.preGenInProgress = false;
                }
            }
        }

        if (this.rumble > 0) {
            this.rumble--;
        } else if (this.rumble < 0) {
            this.rumble++;
        }

        if (this.riddenByEntity != null) {
            final double rumbleAmount = this.rumble / (double) (37 - 5 * Math.max(this.getRocketTier(), 5));
            this.riddenByEntity.posX += rumbleAmount;
            this.riddenByEntity.posZ += rumbleAmount;
        }
        final boolean isIgnited = this.launchPhase == EnumLaunchPhase.IGNITED.ordinal();
        final boolean isLaunched = this.launchPhase == EnumLaunchPhase.LAUNCHED.ordinal();

        if (isIgnited || isLaunched) {
            this.performHurtAnimation();
            this.rumble = (float) this.rand.nextInt(3) - 3;

            if (this.destinationFrequency != -1 && this.landing == false && isLaunched) {
                this.onReachAtmosphere();
            }
        }

        if (!this.worldObj.isRemote) {
            this.lastLastMotionY = this.lastMotionY;
            this.lastMotionY = this.motionY;
        }
    }

    @Override
    public void decodePacketdata(ByteBuf buffer) {
        this.rocketType = EnumRocketType.values()[buffer.readInt()];
        super.decodePacketdata(buffer);

        if (buffer.readBoolean()) {
            this.posX = buffer.readDouble() / 8000.0D;
            this.posY = buffer.readDouble() / 8000.0D;
            this.posZ = buffer.readDouble() / 8000.0D;
        }
    }

    @Override
    public void getNetworkedData(ArrayList<Object> list) {
        if (this.worldObj.isRemote) {
            return;
        }
        list.add(this.rocketType != null ? this.rocketType.getIndex() : 0);
        super.getNetworkedData(list);

        final boolean sendPosUpdates =
                this.ticks < 25 || this.launchPhase != EnumLaunchPhase.LAUNCHED.ordinal() || this.landing;
        list.add(sendPosUpdates);

        if (sendPosUpdates) {
            list.add(this.posX * 8000.0D);
            list.add(this.posY * 8000.0D);
            list.add(this.posZ * 8000.0D);
        }
    }

    @Override
    public void onReachAtmosphere() {
        // Launch controlled
        if (this.destinationFrequency != -1) {
            if (this.worldObj.isRemote) {
                // stop the sounds on the client - but do not reset, the rocket may start again
                this.stopRocketSound();
                return;
            }

            this.setTarget(true, this.destinationFrequency);

            if (this.targetVec != null) {
                if (this.targetDimension != this.worldObj.provider.dimensionId) {
                    final WorldProvider targetDim = WorldUtil.getProviderForDimensionServer(this.targetDimension);
                    if (targetDim != null && targetDim.worldObj instanceof WorldServer) {
                        boolean dimensionAllowed = this.targetDimension == ConfigManagerCore.idDimensionOverworld;

                        if (targetDim instanceof IGalacticraftWorldProvider) {
                            if (((IGalacticraftWorldProvider) targetDim).canSpaceshipTierPass(this.getRocketTier())) {
                                dimensionAllowed = true;
                            } else {
                                dimensionAllowed = false;
                            }
                        } else
                        // No rocket flight to non-Galacticraft dimensions other than the Overworld
                        // allowed unless
                        // config
                        if (this.targetDimension > 1 || this.targetDimension < -1) {
                            try {
                                final Class<?> marsConfig =
                                        Class.forName("micdoodle8.mods.galacticraft.planets.mars.ConfigManagerMars");
                                if (marsConfig
                                        .getField("launchControllerAllDims")
                                        .getBoolean(null)) {
                                    dimensionAllowed = true;
                                }
                            } catch (final Exception e) {
                                e.printStackTrace();
                            }
                        }

                        if (dimensionAllowed) {
                            if (this.riddenByEntity != null) {
                                WorldUtil.transferEntityToDimension(
                                        this.riddenByEntity,
                                        this.targetDimension,
                                        (WorldServer) targetDim.worldObj,
                                        false,
                                        this);
                            } else {
                                final Entity e = WorldUtil.transferEntityToDimension(
                                        this, this.targetDimension, (WorldServer) targetDim.worldObj, false, null);
                                if (e instanceof EntityAutoRocket) {
                                    int fromSky = 800;
                                    if (this.destinationFrequency != 1) {
                                        fromSky = 0;
                                    }
                                    e.setPosition(
                                            this.targetVec.x + 0.5F,
                                            this.targetVec.y + fromSky,
                                            this.targetVec.z + 0.5f);
                                    ((EntityAutoRocket) e).landing = true;
                                    ((EntityAutoRocket) e).setWaitForPlayer(false);
                                } else {
                                    GCLog.info(
                                            "Error: failed to recreate the unmanned rocket in landing mode on target planet.");
                                    e.setDead();
                                    this.setDead();
                                }
                            }
                            return;
                        }
                    }
                    // No destination world found - in this situation continue into regular take-off
                    // (as if Not launch
                    // controlled)
                } else {
                    // Same dimension controlled rocket flight
                    int fromSky = 800;
                    if (this.destinationFrequency != 1) {
                        fromSky = 0;
                    }
                    this.setPosition(this.targetVec.x + 0.5F, this.targetVec.y + fromSky, this.targetVec.z + 0.5F);
                    // Stop any lateral motion, otherwise it will update to an incorrect x,z
                    // position first tick after
                    // spawning above target
                    this.motionX = this.motionZ = 0.0D;
                    // Small upward motion initially, to keep clear of own flame trail from launch
                    this.motionY = 0.1D;
                    if (this.riddenByEntity != null) {
                        WorldUtil.forceMoveEntityToPos(
                                this.riddenByEntity,
                                (WorldServer) this.worldObj,
                                new Vector3(this.targetVec.x + 0.5F, this.targetVec.y + 800, this.targetVec.z + 0.5F),
                                false);
                        this.setWaitForPlayer(true);
                        if (ConfigManagerCore.enableDebug) {
                            GCLog.info("Rocket repositioned, waiting for player");
                        }
                    }
                    this.landing = true;
                    // Do not destroy the rocket, we still need it!
                    return;
                }
            } else {
                // Launch controlled launch but no valid target frequency = rocket loss
                // [INVESTIGATE]
                GCLog.info(
                        "Error: the launch controlled rocket failed to find a valid landing spot when it reached space.");
                this.fuelTank.drain(Integer.MAX_VALUE, true);
                this.posY = Math.max(
                        255,
                        (this.worldObj.provider instanceof IExitHeight
                                        ? ((IExitHeight) this.worldObj.provider).getYCoordinateToTeleport()
                                        : 1200)
                                - 200);
                return;
            }
        }

        // Not launch controlled
        if (!this.worldObj.isRemote) {
            if (this.riddenByEntity instanceof EntityPlayerMP) {
                final EntityPlayerMP player = (EntityPlayerMP) this.riddenByEntity;

                this.onTeleport(player);
                final GCPlayerStats stats = GCPlayerStats.get(player);
                WorldUtil.toCelestialSelection(player, stats, this.getRocketTier());
            }

            // Destroy any rocket which reached the top of the atmosphere and is not
            // controlled by a Launch Controller
            this.setDead();
        }
        // Client side, non-launch controlled, do nothing - no reason why it can't
        // continue flying until the
        // GUICelestialSelection activates
    }

    @Override
    protected boolean shouldCancelExplosion() {

        return this.hasValidFuel() && Math.abs(this.lastLastMotionY) < 4;
    }

    public void onTeleport(EntityPlayerMP player) {}

    @Override
    protected void onRocketLand(int x, int y, int z) {
        super.onRocketLand(x, y, z);
        this.launchCooldown = 40;
    }

    @Override
    public void onLaunch() {
        super.onLaunch();
    }

    @Override
    protected boolean shouldMoveClientSide() {
        return true;
    }

    @Override
    public boolean interactFirst(EntityPlayer par1EntityPlayer) {
        if (this.launchPhase == EnumLaunchPhase.LAUNCHED.ordinal()) {
            return false;
        }

        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayerMP) {
            if (!this.worldObj.isRemote && this.riddenByEntity == par1EntityPlayer) {
                GalacticraftCore.packetPipeline.sendTo(
                        new PacketSimple(EnumSimplePacket.C_RESET_THIRD_PERSON, new Object[] {}),
                        (EntityPlayerMP) par1EntityPlayer);
                final GCPlayerStats stats = GCPlayerStats.get((EntityPlayerMP) par1EntityPlayer);
                stats.chatCooldown = 0;
                par1EntityPlayer.mountEntity(null);
            }

            return true;
        } else if (par1EntityPlayer instanceof EntityPlayerMP) {
            if (!this.worldObj.isRemote) {
                GalacticraftCore.packetPipeline.sendTo(
                        new PacketSimple(EnumSimplePacket.C_DISPLAY_ROCKET_CONTROLS, new Object[] {}),
                        (EntityPlayerMP) par1EntityPlayer);
                final GCPlayerStats stats = GCPlayerStats.get((EntityPlayerMP) par1EntityPlayer);
                stats.chatCooldown = 0;
                par1EntityPlayer.mountEntity(this);
            }

            return true;
        }

        return false;
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setInteger("Type", this.rocketType.getIndex());
        super.writeEntityToNBT(nbt);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        this.rocketType = EnumRocketType.values()[nbt.getInteger("Type")];
        super.readEntityFromNBT(nbt);
    }

    @Override
    public EnumRocketType getType() {
        return this.rocketType;
    }

    @Override
    public int getSizeInventory() {
        if (this.rocketType == null) {
            return 2;
        }
        return this.rocketType.getInventorySpace();
    }

    @Override
    public void onWorldTransferred(World world) {
        if (this.targetVec != null) {
            int fromSky = 800;
            if (this.destinationFrequency != -1) {
                fromSky = 0;
            }
            this.setPosition(this.targetVec.x + 0.5F, this.targetVec.y + fromSky, this.targetVec.z + 0.5F);
            this.landing = true;
            this.setWaitForPlayer(true);
            this.motionX = this.motionY = this.motionZ = 0.0D;
        } else {
            this.setDead();
        }
    }

    @Override
    public void updateRiderPosition() {
        if (this.riddenByEntity != null) {
            this.riddenByEntity.setPosition(
                    this.posX, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ);
        }
    }

    @Override
    public float getRotateOffset() {
        return -1.5F;
    }

    @Override
    public boolean isPlayerRocket() {
        return true;
    }
}
