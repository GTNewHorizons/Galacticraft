package micdoodle8.mods.galacticraft.core.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Block.class)
public interface BlockAccessor {

    @Invoker
    ItemStack invokeCreateStackedBlock(int meta);
}
