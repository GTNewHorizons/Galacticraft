package micdoodle8.mods.galacticraft.core.mixins.early.minecraft;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import micdoodle8.mods.galacticraft.core.client.render.entities.GCPlayerRenderer;

/**
 * Applies the Galacticraft player pose - the freefall animation in zero gravity, both arms overhead under a parachute
 * or while carrying a rocket, and waving at a nearby launching rocket.
 * <p>
 * This used to be done by ModelPlayerGC being installed as the player's model (and as the armor models, so that armor
 * follows the pose). Hooking ModelBiped instead keeps that behaviour for the player model, the armor models and the
 * thermal armor overlays alike, while letting other mods own the player model itself.
 * <p>
 * Disabled when RenderPlayerAPI is installed - ModelPlayerBaseGC does the same job there.
 */
@Mixin(ModelBiped.class)
public abstract class ModelBipedMixin {

    @Inject(method = "setRotationAngles", at = @At("TAIL"), require = 1)
    private void galacticraft$applyGalacticraftPose(float limbSwing, float limbSwingAmount, float ticksExisted,
            float headYaw, float headPitch, float scale, Entity entity, CallbackInfo ci) {
        if (entity instanceof AbstractClientPlayer player) {
            GCPlayerRenderer.applyPose((ModelBiped) (Object) this, player, limbSwing, limbSwingAmount, ticksExisted);
        }
    }
}
