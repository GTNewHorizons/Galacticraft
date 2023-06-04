package micdoodle8.mods.galacticraft.core.mixins.minecraft;

import net.minecraft.client.renderer.ItemRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Inject(
            method = "renderOverlays",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V",
                    shift = At.Shift.BEFORE,
                    remap = false),
            require = 1)
    private void onRenderOverlays(float partialTicks, CallbackInfo callbackInfo) {
        ClientProxyCore.renderLiquidOverlays(partialTicks);
    }
}
