package micdoodle8.mods.galacticraft.core.mixins.minecraft;

import micdoodle8.mods.galacticraft.core.entities.player.GCEntityClientPlayerMP;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerControllerMP.class)
public abstract class PlayerControllerMPMixin {

    @Shadow
    @Final
    private Minecraft mc;

    @Shadow
    @Final
    private NetHandlerPlayClient netClientHandler;

    public EntityClientPlayerMP func_147493_a(World world, StatFileWriter stats) {
        return new GCEntityClientPlayerMP(this.mc, world, this.mc.getSession(), this.netClientHandler, stats);
    }
}
