package micdoodle8.mods.galacticraft.core.mixins.minecraft;

import cpw.mods.fml.common.registry.GameRegistry;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkProviderServer.class)
public class ChunkProviderServerMixin {

    @Redirect(
            method = "populate(Lnet/minecraft/world/chunk/IChunkProvider;II)V",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lcpw/mods/fml/common/registry/GameRegistry;generateWorld(IILnet/minecraft/world/World;Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/chunk/IChunkProvider;)V",
                            remap = false),
            require = 1)
    private void onRegistry(
            int chunkX, int chunkZ, World world, IChunkProvider currentChunkProvider, IChunkProvider chunkGenerator) {
        if (!WorldUtil.otherModPreventGenerate(chunkX, chunkZ, world, currentChunkProvider, chunkGenerator)) {
            GameRegistry.generateWorld(chunkX, chunkZ, world, currentChunkProvider, chunkGenerator);
        }
    }
}
