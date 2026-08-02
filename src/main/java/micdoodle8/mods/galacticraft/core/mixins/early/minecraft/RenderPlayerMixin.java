package micdoodle8.mods.galacticraft.core.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import micdoodle8.mods.galacticraft.api.entity.ICameraZoomEntity;
import micdoodle8.mods.galacticraft.api.world.IZeroGDimension;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderPlayerGC;
import micdoodle8.mods.galacticraft.core.client.render.entities.RotatePlayerEvent;
import micdoodle8.mods.galacticraft.core.tile.TileEntityMulti;
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockMachineMars;
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks;

/**
 * Fires the {@link RotatePlayerEvent} used by the Cryogenic Chamber to lay a sleeping player out along the machine,
 * tilts the player with the rocket they are riding, and lowers them slightly when sneaking in zero gravity.
 * <p>
 * This used to be RenderPlayerGC.rotateCorpse. Injecting into RenderPlayer means it also applies to player renderers
 * installed by other mods.
 * <p>
 * Disabled when RenderPlayerAPI is installed - RenderPlayerBaseGC does the same job there.
 */
@Mixin(RenderPlayer.class)
public abstract class RenderPlayerMixin {

    @Inject(
            method = "rotateCorpse(Lnet/minecraft/client/entity/AbstractClientPlayer;FFF)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 1)
    private void galacticraft$onRotateCorpse(AbstractClientPlayer player, float par2, float par3, float par4,
            CallbackInfo ci) {
        if (player.isEntityAlive() && player.isPlayerSleeping()) {
            // Deprecated subclass on purpose: Forge only notifies listeners of the posted class and its supertypes, so
            // this is what keeps addons listening for RenderPlayerGC.RotatePlayerEvent working. Listeners of the
            // top-level RotatePlayerEvent receive it as well.
            @SuppressWarnings("deprecation")
            final RotatePlayerEvent event = new RenderPlayerGC.RotatePlayerEvent(player);
            MinecraftForge.EVENT_BUS.post(event);

            if (!event.vanillaOverride) {
                return;
            }

            if (event.shouldRotate == null) {
                GL11.glRotatef(player.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
            } else if (event.shouldRotate) {
                GL11.glRotatef(galacticraft$getSleepingRotation(player), 0.0F, 1.0F, 0.0F);
            }

            galacticraft$applyZeroGSneakOffset(player);
            ci.cancel();
            return;
        }

        if (Minecraft.getMinecraft().gameSettings.thirdPersonView != 0
                && player.ridingEntity instanceof ICameraZoomEntity zoomEntity) {
            final Entity rocket = player.ridingEntity;
            final float rotateOffset = zoomEntity.getRotateOffset();

            if (rotateOffset > -10F) {
                GL11.glTranslatef(0, -rotateOffset, 0);
                final float anglePitch = rocket.prevRotationPitch;
                final float angleYaw = rocket.prevRotationYaw;
                GL11.glRotatef(-angleYaw, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(anglePitch, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(0, rotateOffset, 0);
            }
        }
    }

    @Inject(
            method = "rotateCorpse(Lnet/minecraft/client/entity/AbstractClientPlayer;FFF)V",
            at = @At("RETURN"),
            require = 1)
    private void galacticraft$afterRotateCorpse(AbstractClientPlayer player, float par2, float par3, float par4,
            CallbackInfo ci) {
        galacticraft$applyZeroGSneakOffset(player);
    }

    /**
     * The angle a player asleep in a Cryogenic Chamber should be laid out at.
     */
    @Unique
    private static float galacticraft$getSleepingRotation(AbstractClientPlayer player) {
        final ChunkCoordinates pos = player.playerLocation;

        if (pos == null) {
            return 0.0F;
        }

        Block bed = player.worldObj.getBlock(pos.posX, pos.posY, pos.posZ);
        int meta = player.worldObj.getBlockMetadata(pos.posX, pos.posY, pos.posZ);

        if (!bed.isBed(player.worldObj, pos.posX, pos.posY, pos.posZ, player)) {
            return 0.0F;
        }

        if (bed == GCBlocks.fakeBlock && meta == 5) {
            final TileEntity tile = player.worldObj.getTileEntity(pos.posX, pos.posY, pos.posZ);

            if (tile instanceof TileEntityMulti multi) {
                bed = multi.mainBlockPosition.getBlock(player.worldObj);
                meta = multi.mainBlockPosition.getBlockMetadata(player.worldObj);
            }
        }

        if (bed == MarsBlocks.machine && (meta & 12) == BlockMachineMars.CRYOGENIC_CHAMBER_METADATA) {
            return switch (meta & 3) {
                case 1 -> 270.0F;
                case 2 -> 180.0F;
                case 0 -> 90.0F;
                default -> 0.0F;
            };
        }

        return 0.0F;
    }

    @Unique
    private static void galacticraft$applyZeroGSneakOffset(AbstractClientPlayer player) {
        if (player.isSneaking() && player.worldObj.provider instanceof IZeroGDimension) {
            GL11.glTranslatef(0F, -0.1F, 0F);
        }
    }
}
