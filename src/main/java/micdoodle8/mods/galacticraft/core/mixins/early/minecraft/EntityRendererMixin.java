package micdoodle8.mods.galacticraft.core.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @Shadow
    private Minecraft mc;

    @Inject(method = "orientCamera", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, require = 1)
    private void galacticraft$onOrientCamera(float partialTicks, CallbackInfo callbackInfo) {
        ClientProxyCore.orientCamera(partialTicks);
    }

    @Redirect(
            method = "updateLightmap",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/WorldClient;getSunBrightness(F)F",
                    ordinal = 0),
            require = 1)
    private float galacticraft$onUpdateLightmap(WorldClient world, float constOne) {
        return WorldUtil.getWorldBrightness(world);
    }

    @Redirect(
            method = "updateFogColor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/WorldClient;getSkyColor(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/util/Vec3;"),
            require = 1)
    private Vec3 galacticraft$onUpdateSkyColor(WorldClient world, Entity entity, float v) {
        return WorldUtil.getSkyColorHook(world);
    }

    @ModifyVariable(
            method = "updateLightmap",
            at = @At(value = "CONSTANT", args = "intValue=255", shift = At.Shift.BEFORE),
            ordinal = 8,
            require = 1)
    private float galacticraft$onUpdateLightmapRed(float value) {
        return WorldUtil.getColorRed(this.mc.theWorld) * value;
    }

    @ModifyVariable(
            method = "updateLightmap",
            at = @At(value = "CONSTANT", args = "intValue=255", shift = At.Shift.BEFORE),
            ordinal = 9,
            require = 1)
    private float galacticraft$onUpdateLightmapGreen(float value) {
        return WorldUtil.getColorGreen(this.mc.theWorld) * value;
    }

    @ModifyVariable(
            method = "updateLightmap",
            at = @At(value = "CONSTANT", args = "intValue=255", shift = At.Shift.BEFORE),
            ordinal = 10,
            require = 1)
    private float galacticraft$onUpdateLightmapBlue(float value) {
        return WorldUtil.getColorBlue(this.mc.theWorld) * value;
    }
}
