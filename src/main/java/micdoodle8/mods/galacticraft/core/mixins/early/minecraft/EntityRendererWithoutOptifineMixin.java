package micdoodle8.mods.galacticraft.core.mixins.early.minecraft;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.util.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import micdoodle8.mods.galacticraft.core.util.WorldUtil;

@Mixin(EntityRenderer.class)
public class EntityRendererWithoutOptifineMixin {

    @Redirect(
            method = "updateFogColor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/WorldClient;getFogColor(F)Lnet/minecraft/util/Vec3;"),
            require = 1)
    private Vec3 galacticraft$onUpdateFogColor(WorldClient worldClient, float v) {
        return WorldUtil.getFogColorHook(worldClient);
    }
}
