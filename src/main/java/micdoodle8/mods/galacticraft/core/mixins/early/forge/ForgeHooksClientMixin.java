package micdoodle8.mods.galacticraft.core.mixins.early.forge;

import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import micdoodle8.mods.galacticraft.core.event.EventHandlerGC;

@Mixin(value = ForgeHooksClient.class, remap = false)
public class ForgeHooksClientMixin {

    @Inject(method = "orientBedCamera", at = @At("HEAD"), require = 1)
    private static void galacticraft$onOrientBedCamera(CallbackInfo callbackInfo) {
        MinecraftForge.EVENT_BUS.post(new EventHandlerGC.OrientCameraEvent());
    }
}
