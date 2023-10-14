package micdoodle8.mods.galacticraft.core.mixins.minecraft;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import micdoodle8.mods.galacticraft.core.util.WorldUtil;

@Mixin(ChunkProviderServer.class)
public class ChunkProviderServerMixin {

    @WrapWithCondition(
            at = @At(
                    remap = false,
                    target = "Lcpw/mods/fml/common/registry/GameRegistry;generateWorld(IILnet/minecraft/world/World;Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/chunk/IChunkProvider;)V",
                    value = "INVOKE"),
            method = "populate(Lnet/minecraft/world/chunk/IChunkProvider;II)V",
            require = 1)
    private boolean galacticraft$checkOtherModPreventGenerate(int chunkX, int chunkZ, World world,
            IChunkProvider chunkProvider, IChunkProvider chunkGenerator) {
        return !WorldUtil.otherModPreventGenerate(chunkX, chunkZ, world, chunkProvider, chunkGenerator);
    }
}
