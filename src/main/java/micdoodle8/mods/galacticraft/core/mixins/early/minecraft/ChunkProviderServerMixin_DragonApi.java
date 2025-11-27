package micdoodle8.mods.galacticraft.core.mixins.early.minecraft;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import micdoodle8.mods.galacticraft.core.util.WorldUtil;

@Mixin(ChunkProviderServer.class)
public class ChunkProviderServerMixin_DragonApi {

    @Dynamic
    @SuppressWarnings("MixinAnnotationTarget")
    @WrapWithCondition(
            at = @At(
                    remap = false,
                    target = "LReika/DragonAPI/Auxiliary/WorldGenInterceptionRegistry;interceptChunkPopulation(IILnet/minecraft/world/World;Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/chunk/IChunkProvider;)V",
                    value = "INVOKE"),
            method = "populate(Lnet/minecraft/world/chunk/IChunkProvider;II)V",
            require = 1)
    private boolean galacticraft$checkOtherModPreventGenerate(int chunkX, int chunkZ, World world,
            IChunkProvider chunkProvider, IChunkProvider chunkGenerator) {
        return !WorldUtil.otherModPreventGenerate(chunkX, chunkZ, world, chunkProvider, chunkGenerator);
    }
}
