package micdoodle8.mods.galacticraft.core.mixins.early.minecraft;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import micdoodle8.mods.galacticraft.core.mixins.interfaces.AbstractClientPlayerExt;

@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin implements AbstractClientPlayerExt {

    @Unique
    private ResourceLocation gc$locationCape;

    @ModifyReturnValue(method = "func_152122_n", at = @At(value = "RETURN"))
    private boolean gc$hasCape(boolean original) {
        return gc$locationCape != null || original;
    }

    @ModifyReturnValue(method = "getLocationCape", at = @At(value = "RETURN"))
    private ResourceLocation gc$getCape(ResourceLocation original) {
        return gc$locationCape != null ? gc$locationCape : original;
    }

    @Override
    public void gc$setCape(ResourceLocation gcCape) {
        gc$locationCape = gcCape;
    }
}
