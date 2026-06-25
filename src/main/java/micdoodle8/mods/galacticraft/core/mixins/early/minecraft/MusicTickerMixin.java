package micdoodle8.mods.galacticraft.core.mixins.early.minecraft;

import net.minecraft.client.audio.MusicTicker;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import cpw.mods.fml.client.FMLClientHandler;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;

@Mixin(MusicTicker.class)
public class MusicTickerMixin {

    /**
     * @reason Play Galacticraft music if the player is in one of our dimensions. Otherwise, play the normal music. This
     *         mixin replaces the old MusicTickerGC class, which used to entirely replace Minecraft's own MusicTicker.
     */
    @ModifyVariable(
            method = "update",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/client/Minecraft;func_147109_W()Lnet/minecraft/client/audio/MusicTicker$MusicType;"))
    private MusicTicker.MusicType galacticraft$replaceMusicType(MusicTicker.MusicType orig) {
        if (FMLClientHandler.instance().getWorldClient() != null
                && FMLClientHandler.instance().getWorldClient().provider instanceof IGalacticraftWorldProvider) {
            return ClientProxyCore.MUSIC_TYPE_MARS;
        }
        return orig;
    }
}
