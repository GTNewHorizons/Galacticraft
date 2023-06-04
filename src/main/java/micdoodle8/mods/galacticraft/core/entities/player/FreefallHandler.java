package micdoodle8.mods.galacticraft.core.entities.player;

import micdoodle8.mods.galacticraft.api.event.ZeroGravityEvent;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase;
import micdoodle8.mods.galacticraft.api.world.IZeroGDimension;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.dimension.SpinManager;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation;
import micdoodle8.mods.galacticraft.core.entities.EntityLanderBase;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class FreefallHandler {

    private static double pPrevMotionX;
    public static double pPrevMotionY;
    private static double pPrevMotionZ;
    public static boolean sneakLast;

    private final GCPlayerStatsClient stats;

    public FreefallHandler(GCPlayerStatsClient statsClient) {
        this.stats = statsClient;
    }

    public static boolean testFreefall(EntityPlayer player) {
        final ZeroGravityEvent zeroGEvent = new ZeroGravityEvent.InFreefall(player);
        MinecraftForge.EVENT_BUS.post(zeroGEvent);
        if (zeroGEvent.isCanceled()) {
            return false;
        }

        // Test whether feet are on a block, also stops the login glitch
        final int playerFeetOnY = (int) (player.boundingBox.minY - 0.01D);
        final int xx = MathHelper.floor_double(player.posX);
        final int zz = MathHelper.floor_double(player.posZ);
        final Block b = player.worldObj.getBlock(xx, playerFeetOnY, zz);
        if (b.getMaterial() != Material.air && !(b instanceof BlockLiquid)) {
            final double blockYmax = playerFeetOnY + b.getBlockBoundsMaxY();
            if (player.boundingBox.minY - blockYmax < 0.01D && player.boundingBox.minY - blockYmax > -0.5D) {
                player.onGround = true;
                if (player.boundingBox.minY - blockYmax > 0D) {
                    player.posY -= player.boundingBox.minY - blockYmax;
                    player.boundingBox.offset(0, blockYmax - player.boundingBox.minY, 0);
                } else if (b.canCollideCheck(player.worldObj.getBlockMetadata(xx, playerFeetOnY, zz), false)) {
                    final AxisAlignedBB collisionBox = b
                            .getCollisionBoundingBoxFromPool(player.worldObj, xx, playerFeetOnY, zz);
                    if (collisionBox != null && collisionBox.intersectsWith(player.boundingBox)) {
                        player.posY -= player.boundingBox.minY - blockYmax;
                        player.boundingBox.offset(0, blockYmax - player.boundingBox.minY, 0);
                    }
                }
                return false;
            }
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    private boolean testFreefall(EntityPlayerSP p, boolean flag) {
        final World world = p.worldObj;
        final WorldProvider worldProvider = world.provider;
        if (!(worldProvider instanceof IZeroGDimension)) {
            return false;
        }
        final ZeroGravityEvent zeroGEvent = new ZeroGravityEvent.InFreefall(p);
        MinecraftForge.EVENT_BUS.post(zeroGEvent);
        if (zeroGEvent.isCanceled() || this.stats.pjumpticks > 0 || this.stats.pWasOnGround && p.movementInput.jump) {
            return false;
        }

        if (p.ridingEntity != null) {
            final Entity e = p.ridingEntity;
            if (e instanceof EntitySpaceshipBase) {
                return ((EntitySpaceshipBase) e).getLaunched();
            }
            if (e instanceof EntityLanderBase) {
                return false;
                // TODO: should check whether lander has landed (whatever that means)
                // TODO: could check other ridden entities - every entity should have its own
                // freefall check :(
            }
        }

        // This is an "on the ground" check
        if (!flag) {
            return false;
        }
        final float rY = p.rotationYaw % 360F;
        double zreach = 0D;
        double xreach = 0D;
        if (rY < 80F || rY > 280F) {
            zreach = 0.2D;
        }
        if (rY < 170F && rY > 10F) {
            xreach = 0.2D;
        }
        if (rY < 260F && rY > 100F) {
            zreach = -0.2D;
        }
        if (rY < 350F && rY > 190F) {
            xreach = -0.2D;
        }
        final AxisAlignedBB playerReach = p.boundingBox.addCoord(xreach, 0, zreach);

        boolean checkBlockWithinReach;
        if (worldProvider instanceof WorldProviderSpaceStation) {
            final SpinManager spinManager = ((WorldProviderSpaceStation) worldProvider).getSpinManager();
            checkBlockWithinReach = playerReach.maxX >= spinManager.ssBoundsMinX
                    && playerReach.minX <= spinManager.ssBoundsMaxX
                    && playerReach.maxY >= spinManager.ssBoundsMinY
                    && playerReach.minY <= spinManager.ssBoundsMaxY
                    && playerReach.maxZ >= spinManager.ssBoundsMinZ
                    && playerReach.minZ <= spinManager.ssBoundsMaxZ;
            // Player is somewhere within the space station boundaries
        } else {
            checkBlockWithinReach = true;
        }

        if (checkBlockWithinReach)
        // Player is somewhere within the space station boundaries
        {
            // Check if the player's bounding box is in the same block coordinates as any
            // non-vacuum block
            // (including torches etc)
            // If so, it's assumed the player has something close enough to grab onto, so is
            // not in freefall
            // Note: breatheable air here means the player is definitely not in freefall
            final int xm = MathHelper.floor_double(playerReach.minX);
            final int xx = MathHelper.floor_double(playerReach.maxX);
            final int ym = MathHelper.floor_double(playerReach.minY);
            final int yy = MathHelper.floor_double(playerReach.maxY);
            final int zm = MathHelper.floor_double(playerReach.minZ);
            final int zz = MathHelper.floor_double(playerReach.maxZ);
            for (int x = xm; x <= xx; x++) {
                for (int y = ym; y <= yy; y++) {
                    for (int z = zm; z <= zz; z++) {
                        // Blocks.air is hard vacuum - we want to check for that, here
                        final Block b = world.getBlock(x, y, z);
                        if (Blocks.air != b && GCBlocks.brightAir != b) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    @SideOnly(Side.CLIENT)
    public static void setupFreefallPre(EntityPlayerSP p) {
        pPrevMotionX = p.motionX;
        pPrevMotionY = p.motionY;
        pPrevMotionZ = p.motionZ;
    }

    public static void updateFreefall(EntityPlayer p) {
        pPrevMotionX = p.motionX;
        pPrevMotionY = p.motionY;
        pPrevMotionZ = p.motionZ;
    }

    @SideOnly(Side.CLIENT)
    public void preVanillaMotion(EntityPlayerSP p) {
        FreefallHandler.setupFreefallPre(p);
        this.stats.pWasOnGround = p.onGround;
    }

    @SideOnly(Side.CLIENT)
    public void postVanillaMotion(EntityPlayerSP p) {
        final World world = p.worldObj;
        final WorldProvider worldProvider = world.provider;
        if (!(worldProvider instanceof IZeroGDimension)) {
            return;
        }
        final ZeroGravityEvent zeroGEvent = new ZeroGravityEvent.Motion(p);
        MinecraftForge.EVENT_BUS.post(zeroGEvent);
        if (zeroGEvent.isCanceled()) {
            return;
        }

        boolean freefall = this.stats.inFreefall;
        if (freefall) {
            p.ySize = 0F; // Undo the sneak height adjust
        }
        freefall = this.testFreefall(p, freefall);
        this.stats.inFreefall = freefall;
        this.stats.inFreefallFirstCheck = true;

        SpinManager spinManager = null;
        if (worldProvider instanceof WorldProviderSpaceStation) {
            spinManager = ((WorldProviderSpaceStation) worldProvider).getSpinManager();
        }
        boolean doCentrifugal = spinManager != null;

        if (freefall) {
            this.stats.pjumpticks = 0;

            // Reverse effects of deceleration
            p.motionX /= 0.91F;
            p.motionZ /= 0.91F;
            p.motionY /= 0.9800000190734863D;

            if (spinManager != null) {
                doCentrifugal = spinManager.updatePlayerForSpin(p, 1F);
            }

            p.capabilities.isFlying = true;
            // Half the normal acceleration in Creative mode
            final double dx = p.motionX - FreefallHandler.pPrevMotionX;
            final double dy = p.motionY - FreefallHandler.pPrevMotionY;
            final double dz = p.motionZ - FreefallHandler.pPrevMotionZ;
            p.motionX -= dx / 1.2;
            p.motionY -= dy / 1.2;
            p.motionZ -= dz / 1.2;

            if (p.motionX > 1.2F) {
                p.motionX = 1.2F;
            }
            if (p.motionX < -1.2F) {
                p.motionX = -1.2F;
            }
            if (p.motionY > 0.7F) {
                p.motionY = 0.7F;
            }
            if (p.motionY < -0.7F) {
                p.motionY = -0.7F;
            }
            if (p.motionZ > 1.2F) {
                p.motionZ = 1.2F;
            }
            if (p.motionZ < -1.2F) {
                p.motionZ = -1.2F;
            }
            // TODO: Think about endless drift?
            // Player may run out of oxygen - that will kill the player eventually if can't
            // get back to SS
            // Could auto-kill + respawn the player if floats too far away (config option
            // whether to lose items or not)
            // But we want players to be able to enjoy the view of the spinning space
            // station from the outside
            // Arm and leg movements could start tumbling the player?
        } else
        // Not freefall - within arm's length of something or jumping
        {
            final double dy = p.motionY - FreefallHandler.pPrevMotionY;
            // if (p.motionY < 0 && this.pPrevMotionY >= 0) p.posY -= p.motionY;
            // if (p.motionY != 0) p.motionY = this.pPrevMotionY;
            if (p.movementInput.jump) {
                if ((p.onGround || this.stats.pWasOnGround) && !p.capabilities.isCreativeMode) {
                    if (this.stats.pjumpticks < 25) {
                        this.stats.pjumpticks++;
                    }
                    p.motionY -= dy;
                    // p.onGround = false;
                    // p.posY -= 0.1D;
                    // p.boundingBox.offset(0, -0.1D, 0);
                } else {
                    p.motionY += 0.015D;
                    if (this.stats.pjumpticks == 0) {
                        p.motionY -= dy;
                    }
                }
            } else if (this.stats.pjumpticks > 0) {
                p.motionY += 0.0145D * this.stats.pjumpticks;
                this.stats.pjumpticks = 0;
            } else if (p.movementInput.sneak) {
                if (!p.onGround) {
                    p.motionY -= 0.015D;
                }
                this.stats.pjumpticks = 0;
            }
        }

        // Artificial gravity of a sort...
        if (doCentrifugal && !p.onGround) {
            spinManager.applyCentrifugalForce(p);
        }

        FreefallHandler.pPrevMotionX = p.motionX;
        FreefallHandler.pPrevMotionY = p.motionY;
        FreefallHandler.pPrevMotionZ = p.motionZ;
    }
}
