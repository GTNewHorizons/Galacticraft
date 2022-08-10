package micdoodle8.mods.galacticraft.core.mixins.minecraft;

import com.mojang.authlib.GameProfile;
import micdoodle8.mods.galacticraft.core.entities.player.GCEntityOtherPlayerMP;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NetHandlerPlayClient.class)
public class NetHandlerPlayClientMixin {

    @Redirect(
            method = "handleSpawnPlayer",
            at = @At(value = "NEW", target = "net/minecraft/client/entity/EntityOtherPlayerMP"),
            require = 1)
    private EntityOtherPlayerMP onNewEntityOtherPlayerMP(World world, GameProfile gameProfile) {
        return new GCEntityOtherPlayerMP(world, gameProfile);
    }
}
