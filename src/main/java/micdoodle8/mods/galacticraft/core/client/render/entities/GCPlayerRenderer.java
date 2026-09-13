package micdoodle8.mods.galacticraft.core.client.render.entities;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Loader;
import micdoodle8.mods.galacticraft.api.item.IHoldableItem;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.model.ModelPlayerGC;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.wrappers.PlayerGearData;
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule;

/**
 * Draws the Galacticraft equipment (oxygen mask, tanks, tubes, parachute, frequency module) and the thermal armor on
 * top of whichever player model is being rendered.
 * <p>
 * Both are called from {@code RendererLivingEntityMixin} once the player model itself has been drawn, so Galacticraft
 * does not need to own the player renderer or the player model - that leaves other mods (e.g. Et Futurum Requiem's 1.8
 * skin support) free to install their own.
 * <p>
 * If RenderPlayerAPI is installed, none of this is used: {@link RenderPlayerBaseGC} and
 * {@link micdoodle8.mods.galacticraft.core.client.model.ModelPlayerBaseGC} handle both jobs instead, and the mixins are
 * disabled.
 */
public class GCPlayerRenderer {

    /**
     * Set while rendering a player somewhere other than in the world (currently the Telemetry Display Screen), to
     * suppress the equipment and thermal armor overlays.
     */
    public static boolean flagRenderOverride = false;

    public static final ModelPlayerGC modelGear = new ModelPlayerGC(0.0F);
    public static final ModelBiped modelThermalPadding = new ModelPlayerGC(0.25F);
    public static final ModelBiped modelThermalPaddingHelmet = new ModelPlayerGC(0.9F);

    private static final ResourceLocation thermalPaddingTexture0 = new ResourceLocation(
            AsteroidsModule.ASSET_PREFIX,
            "textures/misc/thermalPadding_0.png");
    private static final ResourceLocation thermalPaddingTexture1 = new ResourceLocation(
            AsteroidsModule.ASSET_PREFIX,
            "textures/misc/thermalPadding_1.png");

    private static Boolean isSmartRenderLoaded = null;

    /**
     * Cached result of the search for a launching rocket to wave at, per player, valid for one tick.
     */
    private static final Map<EntityPlayer, int[]> rocketWaveCache = new WeakHashMap<>();

    /**
     * The gear data of the player currently being rendered, or null if it has not been received from the server yet.
     * Also sends the request for it, at most once per player.
     */
    private static PlayerGearData getGearData(EntityPlayer player) {
        final PlayerGearData gearData = ClientProxyCore.playerItemData.get(player.getCommandSenderName());

        if (gearData == null) {
            final String id = player.getGameProfile().getName();

            if (!ClientProxyCore.gearDataRequests.contains(id)) {
                GalacticraftCore.packetPipeline.sendToServer(
                        new PacketSimple(PacketSimple.EnumSimplePacket.S_REQUEST_GEAR_DATA, new Object[] { id }));
                ClientProxyCore.gearDataRequests.add(id);
            }
        }

        return gearData;
    }

    /**
     * True while a player is being rendered somewhere Galacticraft should keep its hands off, i.e. the Telemetry
     * Display Screen. Also honours the deprecated {@link RenderPlayerGC#flagThermalOverride} for external callers.
     */
    @SuppressWarnings("deprecation")
    public static boolean isRenderOverridden() {
        return flagRenderOverride || RenderPlayerGC.flagThermalOverride;
    }

    /**
     * True if the overlays should be skipped entirely for this render.
     */
    private static boolean skipRender(RendererLivingEntity inst, EntityLivingBase entity) {
        return !(inst instanceof RenderPlayer) || !(entity instanceof AbstractClientPlayer)
                || entity.isInvisible()
                || isRenderOverridden();
    }

    /**
     * Copies the animation state of the model being rendered onto one of the Galacticraft overlay models, so that the
     * overlay follows the player's pose.
     */
    private static void copyModelState(ModelBiped target, ModelBase mainModel, EntityLivingBase entity, float limbSwing,
            float limbSwingAmount) {
        target.onGround = mainModel.onGround;
        target.isRiding = mainModel.isRiding;
        target.isChild = mainModel.isChild;

        if (mainModel instanceof ModelBiped biped) {
            target.heldItemLeft = biped.heldItemLeft;
            target.heldItemRight = biped.heldItemRight;
            target.isSneak = biped.isSneak;
            target.aimedBow = biped.aimedBow;
        }

        target.setLivingAnimations(entity, limbSwing, limbSwingAmount, 0.0F);
    }

    /**
     * Applies the Galacticraft player pose to a biped model: the freefall animation in zero gravity, both arms overhead
     * under a parachute or while carrying a rocket, and waving at a nearby launching rocket.
     * <p>
     * Called for the player's own model, for the armor models drawn over it and for the Galacticraft overlay models, so
     * that all of them stay in the same pose.
     */
    public static void applyPose(ModelBiped model, EntityPlayer player, float limbSwing, float limbSwingAmount,
            float ticksExisted) {
        if (isRenderOverridden()) {
            return;
        }

        final ItemStack currentItemStack = player.inventory.getCurrentItem();

        if (!player.onGround && player.worldObj.provider instanceof IGalacticraftWorldProvider
                && player.ridingEntity == null
                && (currentItemStack == null || !(currentItemStack.getItem() instanceof IHoldableItem))) {
            final float speedModifier = 0.1162F * 2;

            final float angularSwingArm = MathHelper.cos(limbSwing * (speedModifier / 2));
            final float rightMod = model.heldItemRight != 0 ? 1 : 2;
            model.bipedRightArm.rotateAngleX -= MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * rightMod
                    * limbSwingAmount
                    * 0.5F;
            model.bipedLeftArm.rotateAngleX -= MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
            model.bipedRightArm.rotateAngleX += -angularSwingArm * 4.0F * limbSwingAmount * 0.5F;
            model.bipedLeftArm.rotateAngleX += angularSwingArm * 4.0F * limbSwingAmount * 0.5F;
            model.bipedLeftLeg.rotateAngleX -= MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F
                    * limbSwingAmount;
            model.bipedLeftLeg.rotateAngleX += MathHelper.cos(limbSwing * 0.1162F * 2 + (float) Math.PI) * 1.4F
                    * limbSwingAmount;
            model.bipedRightLeg.rotateAngleX -= MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            model.bipedRightLeg.rotateAngleX += MathHelper.cos(limbSwing * 0.1162F * 2) * 1.4F * limbSwingAmount;
        }

        final PlayerGearData gearData = ClientProxyCore.playerItemData.get(player.getCommandSenderName());

        if (gearData != null && gearData.getParachute() != null) {
            model.bipedLeftArm.rotateAngleX += (float) Math.PI;
            model.bipedLeftArm.rotateAngleZ += (float) Math.PI / 10;
            model.bipedRightArm.rotateAngleX += (float) Math.PI;
            model.bipedRightArm.rotateAngleZ -= (float) Math.PI / 10;
        }

        if (currentItemStack != null && currentItemStack.getItem() instanceof IHoldableItem holdableItem) {
            if (holdableItem.shouldHoldLeftHandUp(player)) {
                model.bipedLeftArm.rotateAngleX = 0;
                model.bipedLeftArm.rotateAngleZ = 0;

                model.bipedLeftArm.rotateAngleX += (float) Math.PI + 0.3;
                model.bipedLeftArm.rotateAngleZ += (float) Math.PI / 10;
            }

            if (holdableItem.shouldHoldRightHandUp(player)) {
                model.bipedRightArm.rotateAngleX = 0;
                model.bipedRightArm.rotateAngleZ = 0;

                model.bipedRightArm.rotateAngleX += (float) Math.PI + 0.3;
                model.bipedRightArm.rotateAngleZ -= (float) Math.PI / 10;
            }

            if (player.onGround && holdableItem.shouldCrouch(player)) {
                model.bipedBody.rotateAngleX = 0.5F;
                model.bipedRightLeg.rotationPointZ = 4.0F;
                model.bipedLeftLeg.rotationPointZ = 4.0F;
                model.bipedRightLeg.rotationPointY = 9.0F;
                model.bipedLeftLeg.rotationPointY = 9.0F;
                model.bipedHead.rotationPointY = 1.0F;
                model.bipedHeadwear.rotationPointY = 1.0F;
            }
        }

        if (shouldWaveAtRocket(player)) {
            model.bipedRightArm.rotateAngleZ -= (float) (Math.PI / 8) + MathHelper.sin(ticksExisted * 0.9F) * 0.2F;
            model.bipedRightArm.rotateAngleX = (float) Math.PI;
        }
    }

    /**
     * True if there is a rocket launching nearby that the player should be waving at.
     * <p>
     * The search is cached per player per tick: the pose is applied to several models per player per frame, and the
     * search is far too expensive to repeat that often.
     */
    private static boolean shouldWaveAtRocket(EntityPlayer player) {
        final int[] cached = rocketWaveCache.get(player);

        if (cached != null && cached[0] == player.ticksExisted) {
            return cached[1] != 0;
        }

        final List<Entity> entitiesInAABB = player.worldObj.getEntitiesWithinAABBExcludingEntity(
                player,
                AxisAlignedBB.getBoundingBox(
                        player.posX - 20,
                        0,
                        player.posZ - 20,
                        player.posX + 20,
                        200,
                        player.posZ + 20));

        boolean wave = false;

        for (Entity entity : entitiesInAABB) {
            if (entity instanceof EntityTieredRocket ship) {
                if (ship.riddenByEntity != null && !ship.riddenByEntity.equals(player)
                        && (ship.getLaunched() || ship.timeUntilLaunch < 390)) {
                    wave = true;
                    break;
                }
            }
        }

        rocketWaveCache.put(player, new int[] { player.ticksExisted, wave ? 1 : 0 });
        return wave;
    }

    /**
     * Draws the equipment and thermal armor overlays for a player that has just been rendered, and puts the player's
     * own texture back afterwards if anything was drawn.
     */
    public static void renderOverlays(RendererLivingEntity inst, EntityLivingBase entity, float limbSwing,
            float limbSwingAmount, float ticksExisted, float headYaw, float headPitch, float scale) {
        if (skipRender(inst, entity)) {
            return;
        }

        boolean drawn = renderGear(inst, entity, limbSwing, limbSwingAmount, ticksExisted, headYaw, headPitch, scale);
        drawn |= renderThermalPadding(
                inst,
                entity,
                limbSwing,
                limbSwingAmount,
                ticksExisted,
                headYaw,
                headPitch,
                scale);

        if (drawn) {
            // The overlays bind their own textures; put the player's skin back for whatever renders next.
            inst.bindEntityTexture(entity);
        }
    }

    /**
     * Renders the Galacticraft equipment the player is wearing.
     */
    public static boolean renderGear(RendererLivingEntity inst, EntityLivingBase entity, float limbSwing,
            float limbSwingAmount, float ticksExisted, float headYaw, float headPitch, float scale) {
        if (skipRender(inst, entity)) {
            return false;
        }

        final PlayerGearData gearData = getGearData((EntityPlayer) entity);

        if (gearData == null) {
            return false;
        }

        modelGear.setGearData(gearData);
        copyModelState(modelGear, inst.mainModel, entity, limbSwing, limbSwingAmount);
        modelGear.setRotationAngles(limbSwing, limbSwingAmount, ticksExisted, headYaw, headPitch, scale, entity);
        modelGear.renderGear(entity, scale);
        modelGear.setGearData(null);
        return true;
    }

    /**
     * Renders the thermal armor, if the player is wearing it. The thermal armor render is done after the corresponding
     * body part of the player is drawn.
     */
    public static boolean renderThermalPadding(RendererLivingEntity inst, EntityLivingBase entity, float limbSwing,
            float limbSwingAmount, float ticksExisted, float headYaw, float headPitch, float scale) {
        if (isSmartRenderLoaded == null) {
            isSmartRenderLoaded = Loader.isModLoaded("SmartRender");
        }

        if (isSmartRenderLoaded || skipRender(inst, entity)) {
            return false;
        }

        final PlayerGearData gearData = ClientProxyCore.playerItemData.get(entity.getCommandSenderName());

        if (gearData == null) {
            return false;
        }

        boolean drawn = false;

        for (int i = 0; i < 4; ++i) {
            final ModelBiped modelBiped = i == 0 ? modelThermalPaddingHelmet : modelThermalPadding;

            // Padding sub-type 0 is standard Thermal Armor. See PacketSimple handling of
            // C_UPDATE_GEAR_SLOT for how the sub-type gets set
            if (gearData.getThermalPadding(i) != 0) {
                continue;
            }

            GL11.glColor4f(1, 1, 1, 1);
            Minecraft.getMinecraft().renderEngine.bindTexture(thermalPaddingTexture1);
            modelBiped.bipedHead.showModel = i == 0 && gearData.getRenderThermalPadding(i);
            modelBiped.bipedHeadwear.showModel = i == 0 && gearData.getRenderThermalPadding(i);
            modelBiped.bipedBody.showModel = (i == 1 || i == 2) && gearData.getRenderThermalPadding(i);
            modelBiped.bipedRightArm.showModel = i == 1 && gearData.getRenderThermalPadding(i);
            modelBiped.bipedLeftArm.showModel = i == 1 && gearData.getRenderThermalPadding(i);
            modelBiped.bipedRightLeg.showModel = (i == 2 || i == 3) && gearData.getRenderThermalPadding(i);
            modelBiped.bipedLeftLeg.showModel = (i == 2 || i == 3) && gearData.getRenderThermalPadding(i);

            copyModelState(modelBiped, inst.mainModel, entity, limbSwing, limbSwingAmount);
            modelBiped.render(entity, limbSwing, limbSwingAmount, ticksExisted, headYaw, headPitch, scale);

            // Start alpha render
            GL11.glDisable(GL11.GL_LIGHTING);
            Minecraft.getMinecraft().renderEngine.bindTexture(thermalPaddingTexture0);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            final float time = entity.ticksExisted / 10.0F;
            final float sTime = (float) Math.sin(time) * 0.5F + 0.5F;

            float r = 0.2F * sTime;
            float g = 1.0F * sTime;
            float b = 0.2F * sTime;

            if (entity.worldObj.provider instanceof IGalacticraftWorldProvider provider) {
                final float modifier = provider.getThermalLevelModifier();

                if (modifier > 0) {
                    b = g;
                    g = r;
                } else if (modifier < 0) {
                    r = g;
                    g = b;
                }
            }

            GL11.glColor4f(r, g, b, 0.4F * sTime);
            modelBiped.render(entity, limbSwing, limbSwingAmount, ticksExisted, headYaw, headPitch, scale);
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_LIGHTING);
            drawn = true;
        }

        return drawn;
    }
}
